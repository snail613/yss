package com.yss.main.operdeal.income.paid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.dao.IYssConvert;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.bond.BondInsCfgFormula;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.BrokerBean;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.manager.CashTransAdmin;
import com.yss.manager.SecRecPayAdmin;
import com.yss.pojo.dayfinish.BondPaid;
import com.yss.pojo.dayfinish.SecBLendPaid;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
/**
  * @author admin add by zhouxiang 2010.12.17 证券借贷收益支付
 */
public class PaidSecLendIncome extends BaseIncomePaid {
	 CtlPubPara pubpara = null;
	 boolean isFourDigit = false;
	 private boolean bTPVer =false;
	 public PaidSecLendIncome(){
		 
	 }
	 public void calculateIncome(IYssConvert bean) {
	    }

	    /**
	     * getIncomes
	     *
	     * @return ArrayList
	     */
	    public ArrayList getIncomes() throws YssException {
	        ArrayList alResult = new ArrayList();
	        try {
	            alResult.addAll(getDayIncomes(dDate));
	            return alResult;
	        } catch (Exception e) {
	            throw new YssException("获取证券借贷利息数据出错", e);
	        }
	    }

	    protected ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
	        ArrayList alPaid = new ArrayList();
	        String strSql = "";
	        String strCashAcc = "";
	        ResultSet rsCashAcc = null;
	        ResultSet rs = null;
	        SecBLendPaid paid = null;
	        double BaseCuryRate;
	        double PortCuryRate;
	        String CashAccCode = " ";
	        String CashAccName = " ";
	        String getNextDate = "";
	        BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
	            getOperDealCtx().getBean("cashacclinkdeal");
	        CashAccountBean caBean = null;
	        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 --------------------------
	        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
	        rateOper.setYssPub(pub);
	        //-----------------------------------------------------------------------------------
	        try {

	            strSql = "select distinct a. *, port.FPortName,port.FPortCury, inv.FInvMgrName, bro.FBrokerName,sec.FSecurityName, v1.FVocName as FInvestTypeName,j.FAttrClsName as FAttrClsName," + //2009-08-22 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
	                "tsf.FTsfTypeName,subtsf.FSubTsfTypeName,cury.FCuryName,h.fassetgroupcode, h.fassetgroupname " + //MS00001《QDV4.1赢时胜（上海）2009年4月20日01_A》fanghaoln 20090514 跨组合群国内项目
					",sec.FCatCode " +//添加 品种类型 by leeyu add 20100510 QDV4中保2010年5月8日01_B 合并太平版本代码
	                " from (select * from " +
	                pub.yssGetTableName("Tb_Stock_SecRecPay") +
	                " where FTsfTypeCode in( " +
	                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) +","+ dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) // '06','07'
	                +") and FSubTsfTypeCode in( " +
	                dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_RLI) +","+dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_PLI) //  '06RLI' 应收借入利息 "07PLI"应付借贷利息
	                +") and " +
	                operSql.sqlStoragEve(dDate) + 
	                //-------------------------------------------------------------------------------------------------
	                " and FYearMonth <> " +
	                dbl.sqlString(YssFun.formatDate(this.dDate, "yyyy") + "00") +
	                " and FPortCode in (" +
	                operSql.sqlCodes(portCodes) + ")) a " +
	                //---------------------------------------
	                " left join (select FPortCode, FPortName,FPortCury from " +
	                pub.yssGetTableName("tb_para_portfolio") +
	                " where FCheckState = 1) port on a.FPortCode = port.FPortCode " +
	                //----------------------------------------
	                " left join (select FInvMgrCode, FInvMgrName from " +
	                pub.yssGetTableName("Tb_Para_InvestManager") +
	                " where FCheckState = 1) inv on a.FAnalysisCode1 = inv.FInvMgrCode " +
	                //----------------------------------------
	                " left join (select FBrokerCode, FBrokerName from " +
	                pub.yssGetTableName("Tb_Para_Broker") +
	                " where FCheckState = 1) bro on a.FAnalysisCode2 = bro.FBrokerCode " +
	                //----------------------------------------
	                " left join (select FSecurityCode, FSecurityName,FCatCode from " +
	                pub.yssGetTableName("tb_para_security") +
	                " where FCheckState = 1) sec on a.FSecurityCode = sec.FSecurityCode " +
	                //----------------------------------------
	                " left join (select FTsfTypeCode, FTsfTypeName from tb_base_transfertype " +
	                " where FCheckState = 1) tsf on a.FTsfTypeCode = tsf.FTsfTypeCode " +
	                //----------------------------------------
	                " left join (select FSubTsfTypeCode, FSubTsfTypeName from tb_base_subtransfertype " +
	                " where FCheckState = 1) subtsf on a.FSubTsfTypeCode = subtsf.FSubTsfTypeCode" +
	                //----------------------------------------
	                " left join (select FCuryCode, FCuryName from " +
	                pub.yssGetTableName("tb_para_currency") +
	                " where FCheckState = 1) cury on a.FCuryCode = cury.FCuryCode " +
	                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
	                pub.getPrefixTB() + "' " +
	                " left join (SELECT FVocCode, FVocName from Tb_Fun_Vocabulary WHERE FVocTypeCode = " +
	                dbl.sqlString(YssCons.YSS_InvestType) + ") v1 ON v1.FVocCode = a.FInvestType" +

	                " left join (select FAttrClsCode,FAttrClsName from " +
	                pub.yssGetTableName("Tb_Para_AttributeClass") +
	                ") j on a.FAttrClsCode = j.FAttrClsCode " +
	                getBLSecurity(dDate, this.isAll);

	            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
	            while (rs.next()) {
	                paid = new SecBLendPaid();
	                paid.setDDate(dDate);
	                paid.setAssetGroupCode(rs.getString("fassetgroupcode"));
	                paid.setAssetGroupName(rs.getString("fassetgroupname"));
	                //---------------------------------------------------------------------------------------------
	                paid.setSecurityCode(rs.getString("FSecurityCode"));
	                paid.setSecurityName(rs.getString("FSecurityName"));
	                paid.setPortCode(rs.getString("FPortCode"));
	                paid.setPortName(rs.getString("FPortName"));
	                paid.setInvmgrCode(rs.getString("FAnalysisCode1"));
	                paid.setInvmgrName(rs.getString("FInvMgrName"));
	                paid.setBrokerCode(rs.getString("FAnalysisCode2"));
	                paid.setBrokerName(rs.getString("FBrokerName"));
	                paid.setTsfTypeCode(rs.getString("FTsfTypeCode"));
	                paid.setTsfTypeName(rs.getString("FTsfTypeName"));
	                paid.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
	                paid.setSubTsfTypeName(rs.getString("FSubTsfTypeName"));
	                paid.setCuryCode(rs.getString("FCuryCode"));
	                paid.setCuryName(rs.getString("FCuryName"));
	                paid.setMoney(rs.getDouble("FBal"));//昨日库存金额
	                paid.setMMoney(rs.getDouble("FMBal"));
	                paid.setVMoney(rs.getDouble("FVBal"));
	                if (!this.isAll.equalsIgnoreCase("true")) { //当不是全部显示时，将到期日期赋值
	                    paid.setNextCpnDate(YssFun.addDay(dDate, -1)); //调整为获取支付当天的前一天，作为支付日期。
	                }
	                paid.setAttrClsCode(rs.getString("FAttrClsCode"));
	                paid.setAttrClsName(rs.getString("FAttrClsName"));
	                paid.setInvestType(rs.getString("FInvestType"));
	                paid.setInvestTypeName(rs.getString("FInvestTypeName"));
	                //--------------------------------------------------//
	                //获取现金帐户
	                cashacc.setYssPub(pub);
	                cashacc.setLinkParaAttr(rs.getString("FAnalysisCode1"),
	                                        rs.getString("FPortCode"),
	                                        rs.getString("FSecurityCode"),
	                                        rs.getString("FAnalysisCode2"),
	                                        "06", dDate);

	                caBean = cashacc.getCashAccountBean();
	                if (caBean != null) {
	                    CashAccCode = caBean.getStrCashAcctCode();
	                }
	                strCashAcc = "select FCashAccName from " +
	                    pub.yssGetTableName("Tb_Para_CashAccount") +
	                    " where FCheckState = 1 and FCashAccCode = " +
	                    dbl.sqlString(CashAccCode);
	                rsCashAcc = dbl.queryByPreparedStatement(strCashAcc); //modify by fangjiang 2011.08.14 STORY #788
	                if (rsCashAcc.next()) {
	                    CashAccName = rsCashAcc.getString("FCashAccName");
	                }
	                if (rsCashAcc != null) {
	                    dbl.closeResultSetFinal(rsCashAcc);
	                }
	                paid.setCashAccCode(CashAccCode);
	                paid.setCashAccName(CashAccName);

	                //当天的基础汇率
	                BaseCuryRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FCuryCode"), paid.getPortCode(), YssOperCons.YSS_RATE_BASE);
	                //当天的组合汇率
	                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
	                                          paid.getPortCode());
	                PortCuryRate = rateOper.getDPortRate();
	               


	                paid.setBaseCuryRate(BaseCuryRate);
	                paid.setPortCuryRate(PortCuryRate);

	                alPaid.add(paid);
	            }
	            return alPaid;
	        } catch (Exception e) {
	            throw new YssException("取应收利息出错", e);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }

	    }

	    /**
	     * saveIncome
	     *
	     * @param alIncome ArrayList
	     */
	    public void saveIncome(ArrayList alIncome) throws YssException {
	        int i = 0;
	        SecBLendPaid bondpaid = new SecBLendPaid();
	        SecPecPayBean secpecpay = null;
	        SecPecPayBean lxSecpecPay = null;
	        TransferBean transfer = null;
	        TransferSetBean transferset = null;
	      	BrokerBean broker = null;
	        CashTransAdmin cashtrans = null;
	        SecRecPayAdmin recpay = null;
	        Connection conn = dbl.loadConnection();
	        String sDesc = "";
	       
	        String delSecNum = ""; //删除所需的证券应收应付的编号。
	        String inSecNum = ""; //插入所需的证券应收应付的编号。此处之所以需要两个编号是为了避免前后出现藏数据的情况。
	        int excuteRows = -1; //初始值设为-1,是为了之后将无值和新创建的情况分开.其中-1位新创建.
	        CtlPubPara pubPara = new CtlPubPara();
	        pubPara.setYssPub(pub);
	        String sPara =pubPara.getNavType();//通过净值表类型来判断
	        if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
	      	  bTPVer=false;//国内QDII统计模式
	        }else{
	       	  bTPVer=true;//太平资产统计模式
	        }
		    pubpara = new CtlPubPara();
	        pubpara.setYssPub(pub);
	        String digit = pubpara.getKeepFourDigit(); 
	        if (digit.toLowerCase().equalsIgnoreCase("two")) { //若两位
	            isFourDigit = false;
	        } else if (digit.toLowerCase().equalsIgnoreCase("four")) { //若四位
	            isFourDigit = true;
	        }
	//----------------------------------------------------------------------
	        
	    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	    	Date logStartTime = null;//业务子项开始时间
	    	String portCode = "";//组合代码
	    	boolean bTrans = false;
			//add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A 设置菜单代码
	    	this.setFunName("incomepaid");
	    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	        try {
	            conn.setAutoCommit(false);
	            bTrans = true;
	            for (i = 0; i < alIncome.size(); i++) {
	                bondpaid = (SecBLendPaid) alIncome.get(i);
	                
	    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	    			logInfo = "";//日志信息
	    			logStartTime = new Date();//开始时间
	    			portCode = bondpaid.getPortCode();//组合代码
	    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	                
	                secpecpay = setSecRecPayAttr(bondpaid, "true"); 
	                transfer = setTransferAttr(bondpaid);
	                transferset = setTransferSetAttr(bondpaid);
	                if (this.isCheckData.equalsIgnoreCase("true")) {
	                    secpecpay.checkStateId = 1;
	                    transfer.checkStateId = 1;
	                } else { 
	                    secpecpay.checkStateId = 0;
	                    transfer.checkStateId = 0;
	                }
	                recpay = new SecRecPayAdmin();
	                recpay.setYssPub(pub);
	                recpay.addList(secpecpay);

	                cashtrans = new CashTransAdmin();
	                cashtrans.setYssPub(pub);
	                
	            	//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
	            	logInfo += "证券代码:" + secpecpay.getStrSecurityCode() + 
	            		       "\r\n到账日期:" + YssFun.formatDate(transfer.getDtTransferDate(),"yyyy-MM-dd") +
	            		       "\r\n现金账户代码:" + transferset.getSCashAccCode() +
	            	           "\r\n" + ((transferset.getIInOut() == 1) ? "流入":"流出") + 
	            	           "证券借贷利息:" + transferset.getDMoney() +
	            	           YssCons.YSS_LINESPLITMARK;
	            	//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
	                
	                cashtrans.addList(transfer, transferset);

	                if (bondpaid.getBalMoney() != 0) { //如果原额与修改金额不同则再插入一条
	                    lxSecpecPay = setSecRecPayAttr(bondpaid, "false"); //sj edit 20071124 如果有差额，则用字符窜false表示not相等
	                    //===================================================================================================
	                    //fanghaoln 20090714 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
	                    if (this.isCheckData.equalsIgnoreCase("true")) { //如果前台传来true表示选中了审核状态统计之后的数据放到已审核里面
	                        lxSecpecPay.checkStateId = 1;
	                    } else { //如果没有选中已审核的状态表示数据放到未审核里面
	                        lxSecpecPay.checkStateId = 0;
	                    }
	                    //===========================================end=======================================================

	                    recpay.addList(lxSecpecPay);
	                }
	             delSecNum = recpay.loadSecPRNums(secpecpay.getTransDate(), secpecpay.getStrTsfTypeCode(), secpecpay.getStrSubTsfTypeCode(),
	                                                 secpecpay.getStrSecurityCode(), secpecpay.getStrPortCode(),
	                                                 secpecpay.getInvMgrCode(), secpecpay.getBrokerCode());
	              broker = new BrokerBean();
		          broker.setStrCode(secpecpay.getBrokerCode());
		          broker.setYssPub(pub);
		          broker.getSetting();
		          sDesc = "[" + YssFun.formatDate(secpecpay.getTransDate()) + "]";
		          sDesc += "[" + secpecpay.getInvMgrCodeName() + "]" + "证券借贷利息";
		          sDesc += "["+ secpecpay.getStrSecurityCode() +"：" + secpecpay.getStrSecurityName() + "]";
		          sDesc += secpecpay.getBrokerCode().trim().length() == 0?"": "[" + broker.getStrShortName() + "]" ;
		          transferset.setSDesc(sDesc);
		          //------------------------------------------------------------------------------//
	                if (delSecNum.trim().length() > 0) {
	                    delSecNum = delSecNum.substring(0, delSecNum.length() - 1);
	                }
	                //secpecpay.setStrTsfTypeCode("02");
	                //--------- MS00278  QDV4中保2009年02月24日03_B -----------------------------------------------------
	                recpay.insert("", secpecpay.getTransDate(),
	                              secpecpay.getTransDate(), secpecpay.getStrTsfTypeCode()+","+"98",
	                              secpecpay.getStrSubTsfTypeCode()+",98"+secpecpay.getStrSubTsfTypeCode(), secpecpay.getStrPortCode(), secpecpay.getInvMgrCode(),
	                              secpecpay.getBrokerCode(), secpecpay.getStrSecurityCode(), secpecpay.getStrCuryCode(),
	                              -99, true, 0, isFourDigit); //通过判断通用参数来设置应收应付的小数舍入位数。//MS00275 QDV4中保2009年02月27日01_B 将数据来源设置为-99的目的是在删除时不论是手工还是自动，一并删除。
	                //-------------------------------------------------------------------------------------------------

	                //------
	                inSecNum = recpay.getIncomeNum(); //获取收入02的编号 MS00089
	                if (delSecNum.trim().length() > 0) { //以之前获取需删除的编号为筛选条件进行删除.
	                    excuteRows = cashtrans.deleteWithReturnRows("SecBLendPaid", delSecNum);
	                }
	                if (excuteRows == 0) { //当为历史数据,其关联编号无值时,使用此方式.
	                    cashtrans.insert(transfer.getDtTransferDate(),
	                                     transfer.getDtTransDate(), transfer.getStrTsfTypeCode(),
	                                     transfer.getStrSubTsfTypeCode(), transfer.getStrPortCode(),
	                                     transfer.getStrSecurityCode(), -99); 
	                    //---------------------------------------------------------------------------------------------------//
	                } else { //当新建和有关联编号的数据重复计算时,使用此方式.
	                    transfer.setFRelaNum(inSecNum);
	             		transferset.setSDesc(sDesc);
	                    transferset.setSDesc(inSecNum.trim().length() == 0 ? transferset.getSDesc() + "\r\n未能正確將關聯編號錄入資金調撥記錄中!" : transferset.getSDesc()); //
	                    cashtrans.insert();
	                }
	                //------
	                
	    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	                //生成业务日志数据
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			logOper.setDayFinishIData(this,20,operType,this.pub,
	                		false,portCode,dDate,dDate,dDate,logInfo,
	                		logStartTime,logSumCode, new Date());//20收益支付 ，operType是处理项目
	        		}
	                //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	            }
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	        } catch (Exception e) {
	    		//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	    		try{
				    //生成业务日志数据
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			logOper.setDayFinishIData(this,20,operType,this.pub,
	                		true, portCode, dDate, dDate, dDate,
	                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
	                		(logInfo + " \r\n 支付证券借贷利息出错 \r\n " + e.getMessage())//处理日志信息 除去特殊符号
	        				.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
	        				//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
	                		logStartTime,logSumCode, new Date());//20收益支付 ，operType是处理项目
	        		}
	    		}catch(Exception ex){
	    			ex.printStackTrace();
	    		}
	    		//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	    		//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
	    		finally{//添加 finally 保证可以抛出异常
	    			throw new YssException("系统保存证券借贷付利息时出现异常!" + "\n", e); 
	    		}
	    		//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
	        } finally{
			    //add by songjie 2012.10.29 关闭事务
	        	dbl.endTransFinal(conn, bTrans);
	        }
	    }

	    /**
	     * 重写方法，以便能够传入差额和原额 sj 20071124
	     * @param bondpaid BondPaid
	     * @param equals String
	     * @throws YssException
	     * @return SecPecPayBean
	     */
	    protected SecPecPayBean setSecRecPayAttr(SecBLendPaid bondpaid, String equals) throws YssException {
	        try {
	            SecPecPayBean secpecpay = new SecPecPayBean();
	            secpecpay.setTransDate(bondpaid.getmDate());
	            secpecpay.setStrPortCode(bondpaid.getPortCode());
	            secpecpay.setInvMgrCode(bondpaid.getInvmgrCode());
	            secpecpay.setInvMgrCodeName(bondpaid.getInvmgrName());
	            secpecpay.setBrokerCode(bondpaid.getBrokerCode());
	            secpecpay.setStrSecurityCode(bondpaid.getSecurityCode());
	            secpecpay.setStrSecurityName(bondpaid.getSecurityName());
	            secpecpay.setStrCuryCode(bondpaid.getCuryCode());
	            secpecpay.setBaseCuryRate(bondpaid.getBaseCuryRate());
	            secpecpay.setPortCuryRate(bondpaid.getPortCuryRate());
	            secpecpay.setAttrClsCode(bondpaid.getAttrClsCode());
	            secpecpay.setInvestType(bondpaid.getInvestType());
	            //-------------------------------------------------//
	            if (equals.equalsIgnoreCase("true")) {
	                secpecpay.setMoney(bondpaid.getLx());
	                secpecpay.setMMoney(bondpaid.getLx());
	                secpecpay.setVMoney(bondpaid.getLx());
	                //利用公有方法重新得到基础货币金额 sunny
	                //------MS00278  QDV4中保2009年02月24日03_B -----------------------------------------------------
	                secpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
	                    bondpaid.getLx(),
	                    bondpaid.getBaseCuryRate(), isFourDigit ? 4 : 2)); //通过通用参数来控制计算金额的小数位数。以下雷同
	                secpecpay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
	                    bondpaid.getLx(),
	                    bondpaid.getBaseCuryRate(), isFourDigit ? 4 : 2));
	                secpecpay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
	                    bondpaid.getLx(),
	                    bondpaid.getBaseCuryRate(), isFourDigit ? 4 : 2));
	                secpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
	                    bondpaid.getLx(),
	                    bondpaid.getBaseCuryRate(), bondpaid.getPortCuryRate(),
	                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
	                    bondpaid.getCuryCode(), bondpaid.getDDate(), bondpaid.getPortCode(), isFourDigit ? 4 : 2));
	                secpecpay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
	                    bondpaid.getLx(),
	                    bondpaid.getBaseCuryRate(), bondpaid.getPortCuryRate(),
	                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
	                    bondpaid.getCuryCode(), bondpaid.getDDate(), bondpaid.getPortCode(), isFourDigit ? 4 : 2));
	                secpecpay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
	                    bondpaid.getLx(),
	                    bondpaid.getBaseCuryRate(), bondpaid.getPortCuryRate(),
	                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
	                    bondpaid.getCuryCode(), bondpaid.getDDate(), bondpaid.getPortCode(), isFourDigit ? 4 : 2));
	                //------------------------------------------------------------------------------------------
	                if(bondpaid.getSubTsfTypeCode().equalsIgnoreCase(YssOperCons.YSS_SECLEND_SUBDBLX_RLI)){
	                	 secpecpay.setStrTsfTypeCode("02");
	 	                 secpecpay.setStrSubTsfTypeCode(YssOperCons.Yss_ZJDBZLX_SEC_Income);
	                }else if(bondpaid.getSubTsfTypeCode().equalsIgnoreCase(YssOperCons.YSS_SECLEND_SUBDBLX_PLI)){
	                	 secpecpay.setStrTsfTypeCode("03");
	 	                 secpecpay.setStrSubTsfTypeCode(YssOperCons.Yss_ZJDBZLX_SEC_Fee);
	                }
	            } else if (equals.equalsIgnoreCase("false")) {
	                secpecpay.setMoney(bondpaid.getBalMoney()); //修改后的金额与修改前的金额的差 sj 20071124
	                secpecpay.setMMoney(bondpaid.getBalMoney());
	                secpecpay.setVMoney(bondpaid.getBalMoney());
	                secpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney( //replace paid.getMoney to secpecpay.getMoney,secpecpay的Money
	                    secpecpay. //值在前面以做修改
	                    getMoney(),
	                    bondpaid.getBaseCuryRate()));
	                secpecpay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
	                    secpecpay.getMMoney(),
	                    bondpaid.getBaseCuryRate()));
	                secpecpay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
	                    secpecpay.getVMoney(),
	                    bondpaid.getBaseCuryRate()));
	                secpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
	                    secpecpay.getMoney(),
	                    bondpaid.getBaseCuryRate(), bondpaid.getPortCuryRate(),
	                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
	                    bondpaid.getCuryCode(), bondpaid.getDDate(), bondpaid.getPortCode()));
	                secpecpay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
	                    secpecpay.getMMoney(),
	                    bondpaid.getBaseCuryRate(), bondpaid.getPortCuryRate(),
	                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
	                    bondpaid.getCuryCode(), bondpaid.getDDate(), bondpaid.getPortCode()));
	                secpecpay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
	                    secpecpay.getVMoney(),
	                    bondpaid.getBaseCuryRate(), bondpaid.getPortCuryRate(),
	                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
	                    bondpaid.getCuryCode(), bondpaid.getDDate(), bondpaid.getPortCode()));
	                
	                if(bondpaid.getSubTsfTypeCode().equalsIgnoreCase(YssOperCons.YSS_SECLEND_SUBDBLX_RLI)){//调整金额和原额不同的时候应该多产生一笔余额的应收应付
	                	 secpecpay.setStrTsfTypeCode("98");
	                	 secpecpay.setStrSubTsfTypeCode("9802LE");
	                }else if(bondpaid.getSubTsfTypeCode().equalsIgnoreCase(YssOperCons.YSS_SECLEND_SUBDBLX_PLI)){
	                	 secpecpay.setStrTsfTypeCode("98");
	                	 secpecpay.setStrSubTsfTypeCode("9803LE");
	                }
	            }
	            secpecpay.setCheckState(1);
	            return secpecpay;
	        } catch (Exception e) {
	            throw new YssException("系统设置证券借贷应收应付数据时出现异常!" + "\n", e); 
	        }
	    }

	protected TransferBean setTransferAttr(SecBLendPaid bondpaid) {
		TransferBean transfer = new TransferBean();
		transfer.setDtTransferDate(bondpaid.getDDate());
		transfer.setDtTransDate(bondpaid.getmDate());
		transfer.setStrSecurityCode(bondpaid.getSecurityCode());
		if (bondpaid.getSubTsfTypeCode().equalsIgnoreCase(
				YssOperCons.YSS_SECLEND_SUBDBLX_RLI)) {
			transfer.setStrTsfTypeCode("02");
			transfer.setStrSubTsfTypeCode(YssOperCons.Yss_ZJDBZLX_SEC_Income);
		} else if (bondpaid.getSubTsfTypeCode().equalsIgnoreCase(
				YssOperCons.YSS_SECLEND_SUBDBLX_PLI)) {
			transfer.setStrTsfTypeCode("03");
			transfer.setStrSubTsfTypeCode(YssOperCons.Yss_ZJDBZLX_SEC_Fee);
		}
		transfer.setStrPortCode(bondpaid.getPortCode());
		transfer.setFNumType("SecBLendPaid");
		transfer.setDataSource(1);
		return transfer;
	}

	protected TransferSetBean setTransferSetAttr(SecBLendPaid bondpaid)
			throws SQLException, YssException {
		// -----------------------配置信息出错 sj 20080805 bug
		// 0000354----------------------------------------------------------------------------
		boolean analy2 = false;
		String analy2Type = "";
		boolean analy3 = false;
		String analy3Type = "";
		analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
		analy2Type = operSql.storageAnalysisType("FAnalysisCode2", "Cash");
		analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
		analy3Type = operSql.storageAnalysisType("FAnalysisCode3", "Cash");
		TransferSetBean transferset = new TransferSetBean();
		transferset.setSPortCode(bondpaid.getPortCode());
		transferset.setSAnalysisCode1(bondpaid.getSAnalysisCode1());
		transferset.setSCashAccCode(bondpaid.getCashAccCode());
		transferset.setDMoney(bondpaid.getMoney());
		transferset.setDBaseRate(bondpaid.getBaseCuryRate());
		transferset.setDPortRate(bondpaid.getPortCuryRate());
		if (bondpaid.getSubTsfTypeCode().equalsIgnoreCase(
				YssOperCons.YSS_SECLEND_SUBDBLX_RLI)) {
			transferset.setIInOut(1);
		} else if (bondpaid.getSubTsfTypeCode().equalsIgnoreCase(
				YssOperCons.YSS_SECLEND_SUBDBLX_PLI)) {
			transferset.setIInOut(-1);
		}

		transferset.setCheckStateId(1); // 生成的直接进入已审核。sj edit 20080626.
		return transferset;
	}

	    /**
	     * 返回条件的证券借贷利息
	     * @param dDate Date
	     * @param isAll String　是否全部显示　
	     * @return String　返回证券代码集　用,隔开
	     * @throws YssException
	     * MS00241 QDV4中保2008年12月29日01_A
	     */
	    private String getBLSecurity(java.util.Date dDate, String isAll) throws YssException {
	        String sqlStr = "";
	        String sSecurity = "";
	        ResultSet rs = null;
	        HashMap hmSecuInfo = null;
	        try {
	            if (isAll.equalsIgnoreCase("true")) { //若为全部显示，则直接返回
	                return sSecurity;
	            }
	            //昨日借入数量-今日证券借贷借入归还交易数据的数量为0才是结算日，收益支付 或者 昨日借出数量-今日借出召回数量=0才是结算日
	            sqlStr = "select a.fsecuritycode, a.fportcode, a.fbrokercode,(c.famount - a.ftradeamount) as settleval  from "
	            	+pub.yssGetTableName("tb_data_seclendtrade")
	            	+" a join (select b.fsecuritycode, b.fportcode, b.fanalysiscode2 as fbrokercode,b.famount from "
	            	+pub.yssGetTableName("tb_stock_secrecpay")
	            	+" b  where b.fstoragedate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))
	            	+" and b.fsubtsftypecode = "+dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BSC)
	            	+") c on a.fsecuritycode = c.fsecuritycode  and a.fportcode = c.fportcode and a.fbrokercode = c.fbrokercode"
	            	+" where a.fbargaindate = "+dbl.sqlDate(dDate)
	            	+" and a.ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rcb)
	            	+"union all "
	            	+"select a.fsecuritycode, a.fportcode, a.fbrokercode,(c.famount - a.ftradeamount) as settleval  from "
	            	+pub.yssGetTableName("tb_data_seclendtrade")
	            	+" a join (select b.fsecuritycode, b.fportcode, b.fanalysiscode2 as fbrokercode,b.famount from "
	            	+pub.yssGetTableName("tb_stock_secrecpay")
	            	+" b  where b.fstoragedate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))
	            	+" and b.fsubtsftypecode = "+dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BLC)
	            	+") c on a.fsecuritycode = c.fsecuritycode  and a.fportcode = c.fportcode and a.fbrokercode = c.fbrokercode"
	            	+" where a.fbargaindate = "+dbl.sqlDate(dDate)
	            	+" and a.ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Lr);
	            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
	            while (rs.next()) {
	                if (rs.getDouble("settleval") == 0) { //获取昨日库存-今日发生的交易数 只有为0才是结算日返回证券代码
	                    sSecurity += rs.getString("FSecurityCode") + ","; //拼装证券代码
	                }
	            }
	            if (sSecurity.length() > 1) {
	                sSecurity = sSecurity.substring(0, sSecurity.length() - 1);
	            }
	        } catch (Exception ex) {
	            throw new YssException("获取证券借贷支付日期出现错误！", ex);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
	        //edit by songjie 2011.07.15 查询证券借贷的收益支付数据时报错：未明确定义列
	        return " where a.FSecurityCode in (" + operSql.sqlCodes(sSecurity) + ")"; //拼装筛选语句,当无需显示的证券借贷利息,则返回'';
	    }

}
