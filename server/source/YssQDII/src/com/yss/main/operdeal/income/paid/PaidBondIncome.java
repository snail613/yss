package com.yss.main.operdeal.income.paid;

import java.sql.*;
//MS00241 QDV4中保2008年12月29日01_A leeyu 20090223
import java.util.*;
import java.util.Date;

//MS00278 QDV4中保2009年02月24日03_B
import com.yss.commeach.*;
import com.yss.main.cashmanage.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.bond.*; //MS00241 QDV4中保2008年12月29日01_A by leeyu 20090223
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.pojo.dayfinish.*;
import com.yss.main.parasetting.BrokerBean;
import com.yss.util.*;

public class PaidBondIncome
    extends BaseIncomePaid {
    //------MS00278  QDV4中保2009年02月24日03_B -------------------
    CtlPubPara pubpara = null;
    boolean isFourDigit = false;
	private boolean bTPVer =false;//区分是太平资产的库存统计还是QDII的库存统计,合并版本时调整 
    //-----------------------------------------------------------

    public PaidBondIncome() {
    }

    /**
     * calculateIncome
     *
     * @param bean BaseBean
     */
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
            throw new YssException("获取债券利息数据出错", e);
        }
    }

    protected ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
        ArrayList alPaid = new ArrayList();
        String strSql = "";
        String strCashAcc = "";
        ResultSet rsCashAcc = null;
        ResultSet rs = null;
        BondPaid paid = null;
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
                " where FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + // '06'
                " and FSubTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FI_RecInterest) + //  '06FI'
                " and " +
                //-------QDV4赢时胜(上海)2009年1月5日02_B BugID:MS00144 sj modified 20090121 ------------------------
                operSql.sqlStoragEve(dDate) + //债券支付时,应该支付的是昨天的库存金额.而不是当日的金额.故修改成昨日的筛选条件.
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
                " left join (select FSecurityCode, FSecurityName,FCatCode from " +//添加 品种类型 by leeyu add 20100510 QDV4中保2010年5月8日01_B 合并太平版本代码
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
                // BugNO  :MS00759 QDV4中保2009年10月23日01_B fanghaoln 20091029
                " join " +
                "( select FsecurityCode,FFaceValue,FInsCashDate,FIssuePrice from " + //增加连接把债券兑付的数据不显示出来
                pub.yssGetTableName("tb_para_fixinterest") +
                " where FCheckState=1 " +
                ")c on c.FsecurityCode=a.FsecurityCode and " +
                dbl.sqlDateAdd("c.FInsCashDate", "-1") + " <>a.FStorageDate" +//显示的数据不等于债券兑付的数据
                //----------------------------------------end fanghao---MS00759--------------------------------------------------------------
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
                //=====================================================================================
                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
                pub.getPrefixTB() + "' " +
                //----2009-08-22 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
                " left join (SELECT FVocCode, FVocName from Tb_Fun_Vocabulary WHERE FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_InvestType) + ") v1 ON v1.FVocCode = a.FInvestType" +

                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +
                //-------------------------MS00024-------------------------------------------------------------//
                //=============================================================================================
                //-----MS00241 QDV4中保2008年12月29日01_A 通过计算来获取当日到期的债券代码，并将其作为筛选条件----
                getFISecurity(dDate, this.isAll);

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                paid = new BondPaid();
                paid.setDDate(dDate);
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090526
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
                paid.setMoney(rs.getDouble("FBal"));
                paid.setMMoney(rs.getDouble("FMBal"));
                paid.setVMoney(rs.getDouble("FVBal"));
                if (!this.isAll.equalsIgnoreCase("true")) { //当不是全部显示时，将到期日期赋值
                    paid.setNextCpnDate(YssFun.addDay(dDate, -1)); //调整为获取支付当天的前一天，作为支付日期。
                }
                //----2009-08-22 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
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
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 --------------------------
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                          paid.getPortCode());
                PortCuryRate = rateOper.getDPortRate();
                //-----------------------------------------------------------------------------------


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
        String strSql = "";
        int i = 0;
        BondPaid bondpaid = new BondPaid();
        SecPecPayBean secpecpay = null;
        SecPecPayBean lxSecpecPay = null;
        TransferBean transfer = null;
        TransferSetBean transferset = null;
      	BrokerBean broker = null;
        CashTransAdmin cashtrans = null;
        SecRecPayAdmin recpay = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
      	String sDesc = "";//资金调拨描述信息 add by yanghaiming 20091230 MS00888 QDV4中保2009年12月25日01_A 
        //------为了使资金调拨按照债券的券商不同而分类保存 sj modified 20081222 MS00114  --//
        String delSecNum = ""; //删除所需的证券应收应付的编号。
        String inSecNum = ""; //插入所需的证券应收应付的编号。此处之所以需要两个编号是为了避免前后出现藏数据的情况。
        int excuteRows = -1; //初始值设为-1,是为了之后将无值和新创建的情况分开.其中-1位新创建.
        //---------------------------------------------------------------------------//
	    //合并太平版本代码，用参数判断条件
        CtlPubPara pubPara = new CtlPubPara();
        pubPara.setYssPub(pub);
        String sPara =pubPara.getNavType();//通过净值表类型来判断
        
        
        if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
      	  bTPVer=false;//国内QDII统计模式
        }else{
       	  bTPVer=true;//太平资产统计模式
        }
	    //合并太平版本代码
        //----------- MS00278  QDV4中保2009年02月24日03_B ------------------------
        pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        String digit = pubpara.getKeepFourDigit(); //通过通用参数来获取是否为保留四位小数
        if (digit.toLowerCase().equalsIgnoreCase("two")) { //若两位
            isFourDigit = false;
        } else if (digit.toLowerCase().equalsIgnoreCase("four")) { //若四位
            isFourDigit = true;
        }
//----------------------------------------------------------------------
    	
        //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    	Date logStartTime = null;//业务子项开始时间
    	String portCode = "";//组合代码
		//add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A 添加菜单代码
    	this.setFunName("incomepaid");
    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            for (i = 0; i < alIncome.size(); i++) {
                bondpaid = (BondPaid) alIncome.get(i);
                
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    			logInfo = "";//日志信息
    			logStartTime = new Date();//开始时间
    			portCode = bondpaid.getPortCode();//组合代码
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
                secpecpay = setSecRecPayAttr(bondpaid, "true"); //sj edit 20071124 如果没有差额，则用字符窜true表示相等
                transfer = setTransferAttr(bondpaid);
                transferset = setTransferSetAttr(bondpaid);
                //===================================================================================================
                //fanghaoln 20090625 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
                if (this.isCheckData.equalsIgnoreCase("true")) { //如果前台传来true表示选中了审核状态统计之后的数据放到已审核里面
                    secpecpay.checkStateId = 1;
                    transfer.checkStateId = 1;
                } else { //如果没有选中已审核的状态表示数据放到未审核里面
                    secpecpay.checkStateId = 0;
                    transfer.checkStateId = 0;
                }
                //===========================================end=======================================================
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
            	           "债券利息金额:" + transferset.getDMoney() +
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
                //-----------在插入证券应收应付之前获取其编号，避免获取藏数据的情况。sj modified 20081217 MS00089-------------//
                delSecNum = recpay.loadSecPRNums(secpecpay.getTransDate(), "02", "02FI",
                                                 secpecpay.getStrSecurityCode(), secpecpay.getStrPortCode(),
                                                 secpecpay.getInvMgrCode(), secpecpay.getBrokerCode());
	          //设置债券派息在资金调拨里的描述信息 合并太平版本代码
	          //----------add by yanghaiming 20091230 MS00888 QDV4中保2009年12月25日01_A ---------------//
	          broker = new BrokerBean();
	          broker.setStrCode(secpecpay.getBrokerCode());
	          broker.setYssPub(pub);
	          broker.getSetting();
	          sDesc = "[" + YssFun.formatDate(secpecpay.getTransDate()) + "]";
	          sDesc += "[" + secpecpay.getInvMgrCodeName() + "]" + "债券派息";
	          sDesc += "["+ secpecpay.getStrSecurityCode() +"：" + secpecpay.getStrSecurityName() + "]";
	          sDesc += secpecpay.getBrokerCode().trim().length() == 0?"": "[" + broker.getStrShortName() + "]" ;
	          transferset.setSDesc(sDesc);
	          //------------------------------------------------------------------------------//
                if (delSecNum.trim().length() > 0) {
                    delSecNum = delSecNum.substring(0, delSecNum.length() - 1);
                }

                //--------- MS00278  QDV4中保2009年02月24日03_B -----------------------------------------------------
                recpay.insert("", secpecpay.getTransDate(),
                              secpecpay.getTransDate(), "02" + "," + "98",
                              "9802FI" + "," + "02FI", secpecpay.getStrPortCode(), secpecpay.getInvMgrCode(),
                              secpecpay.getBrokerCode(), secpecpay.getStrSecurityCode(), secpecpay.getStrCuryCode(),
                              -99, true, 0, isFourDigit); //通过判断通用参数来设置应收应付的小数舍入位数。//MS00275 QDV4中保2009年02月27日01_B 将数据来源设置为-99的目的是在删除时不论是手工还是自动，一并删除。
                //-------------------------------------------------------------------------------------------------

                //------
                inSecNum = recpay.getIncomeNum(); //获取收入02的编号 MS00089
                if (delSecNum.trim().length() > 0) { //以之前获取需删除的编号为筛选条件进行删除.
                    excuteRows = cashtrans.deleteWithReturnRows("BondPaid", delSecNum, 1); //編號類型為債券支付,方向為流入.
                }
                if (excuteRows == 0) { //当为历史数据,其关联编号无值时,使用此方式.
                    //-------此处将资金调拨的处理位置进行调整,在产生应收应付之后.
                    //-------添加了组合信息的筛选,使在多组合操作时可删除正确的数据。sj modified 20081217 MS00089 ----------------//
                    cashtrans.insert(transfer.getDtTransferDate(),
                                     transfer.getDtTransDate(), "02",
                                     "02FI", transfer.getStrPortCode(),
                                     transfer.getStrSecurityCode(), -99); //MS00275 QDV4中保2009年02月27日01_B 将数据来源设置为-99的目的是在删除时不论是手工还是自动，一并删除。
                    //---------------------------------------------------------------------------------------------------//
                } else { //当新建和有关联编号的数据重复计算时,使用此方式.
                    transfer.setFRelaNum(inSecNum);
             		transferset.setSDesc(sDesc);//設置資金調撥的券商信息.
                    transferset.setSDesc(inSecNum.trim().length() == 0 ? transferset.getSDesc() + "\r\n未能正確將關聯編號錄入資金調撥記錄中!" : transferset.getSDesc()); //
                    cashtrans.insert();
                }
                //------
                
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //生成业务日志数据
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,20,operType,this.pub,false,
                		portCode,dDate,dDate,dDate,logInfo,
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
                		(logInfo + " \r\n 支付债券利息出错 \r\n " + e.getMessage())//处理日志信息 除去特殊符号
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
    			throw new YssException("系统保存债券支付利息时出现异常!" + "\n", e); //by 曹丞 2009.02.01 保存债券支付利息异常信息 MS00004 QDV4.1-2009.2.1_09A
    		}
    		//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
    	}
    }

    /**
     * 重写方法，以便能够传入差额和原额 sj 20071124
     * @param bondpaid BondPaid
     * @param equals String
     * @throws YssException
     * @return SecPecPayBean
     */
    protected SecPecPayBean setSecRecPayAttr(BondPaid bondpaid, String equals) throws YssException {
        try {
            SecPecPayBean secpecpay = new SecPecPayBean();
            secpecpay.setTransDate(bondpaid.getmDate());//edit by yanghaiming 20100225	MS00997  QDV4建行2010年02月23日01_B  这里取业务日期
            secpecpay.setStrPortCode(bondpaid.getPortCode());
            secpecpay.setInvMgrCode(bondpaid.getInvmgrCode());
            secpecpay.setInvMgrCodeName(bondpaid.getInvmgrName());//add by yanghaiming 20091230 MS00888 QDV4中保2009年12月25日01_A 合并太平版本代码
            secpecpay.setBrokerCode(bondpaid.getBrokerCode());
            secpecpay.setStrSecurityCode(bondpaid.getSecurityCode());
            secpecpay.setStrSecurityName(bondpaid.getSecurityName());//add by yanghaiming 20091230 MS00888 QDV4中保2009年12月25日01_A 合并太平版本代码
            secpecpay.setStrCuryCode(bondpaid.getCuryCode());
            secpecpay.setBaseCuryRate(bondpaid.getBaseCuryRate());
            secpecpay.setPortCuryRate(bondpaid.getPortCuryRate());
            //----2009-08-22 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
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

                secpecpay.setStrTsfTypeCode("02");
                secpecpay.setStrSubTsfTypeCode("02FI");
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
                secpecpay.setStrTsfTypeCode("98"); //调拨类型转换成98 sj edit 20080218
                secpecpay.setStrSubTsfTypeCode("9802FI");
            }
            secpecpay.setCheckState(1); //生成的应收应付直接进入已审核。sj edit 20080626.
            return secpecpay;
        } catch (Exception e) {
            throw new YssException("系统设置债券应收应付数据时出现异常!" + "\n", e); // by 曹丞 2009.02.01 设置债券应收应付数据异常信息 MS00004 QDV4.1-2009.2.1_09A
        }
    }

    protected TransferBean setTransferAttr(BondPaid bondpaid) {
        TransferBean transfer = new TransferBean();
        transfer.setDtTransferDate(bondpaid.getDDate());
        transfer.setDtTransDate(bondpaid.getmDate());//edit by yanghaiming 20100225 MS00997  QDV4建行2010年02月23日01_B 
        transfer.setStrSecurityCode(bondpaid.getSecurityCode());
        transfer.setStrTsfTypeCode("02");
        transfer.setStrSubTsfTypeCode("02FI");
        transfer.setCheckStateId(1); //生成的直接进入已审核。sj edit 20080626.
        //-------添加对组合信息的获取，以便正确删除对应组合的数据。sj 20081217 MS00089 -----//
        transfer.setStrPortCode(bondpaid.getPortCode());
        //---------------------------------------------------------------------------//
        transfer.setFNumType("BondPaid"); //添加编号类型.sj 20081222 MS00114
        //MS00275 QDV4中保2009年02月27日01_B 将数据来源设置为自动。-------------------------------------//
        transfer.setDataSource(1);
        //-----------------------------------------------------------------------------------------//
        return transfer;
    }

    protected TransferSetBean setTransferSetAttr(BondPaid bondpaid) throws
        SQLException, YssException {
        //-----------------------配置信息出错 sj 20080805 bug 0000354----------------------------------------------------------------------------
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
        //----------添加资金调拨的分析代码,与应收应付的分离 sj 20081222 MS00114 --------------------------------------
        transferset.setSAnalysisCode1(bondpaid.getSAnalysisCode1());
        if (analy2 && analy2Type.equalsIgnoreCase("004")) { //若分析代码2为品种类型.则默认为FI.sj edit 20080805
           //transferset.setSAnalysisCode2("FI");
		   //合并太平版本时调整分析代码
		   if(bTPVer)
	    	    transferset.setSAnalysisCode2(bondpaid.getAttrClsCode().trim().length()>0?bondpaid.getAttrClsCode():"FI");//调整，从代码中获取的方式 by leeyu 20100510 QDV4中保2010年5月8日01_B
		   else
		   		transferset.setSAnalysisCode2("FI");
        } else if (analy3 && analy3Type.equalsIgnoreCase("004")) {
           //transferset.setSAnalysisCode3("FI");
		   //合并太平版本时调整分析代码
		   if(bTPVer)
	    	    transferset.setSAnalysisCode3(bondpaid.getAttrClsCode().trim().length()>0?bondpaid.getAttrClsCode():"FI");//调整，从代码中获取的方式 by leeyu 20100510 QDV4中保2010年5月8日01_B
		   else
		   		transferset.setSAnalysisCode2("FI");
        } else {
            transferset.setSAnalysisCode2(bondpaid.getSAnalysisCode2());
        }
        //-------------------------------------------------------------------------------------------------------
        transferset.setSCashAccCode(bondpaid.getCashAccCode());
        transferset.setDMoney(bondpaid.getMoney());
        transferset.setDBaseRate(bondpaid.getBaseCuryRate());
        transferset.setDPortRate(bondpaid.getPortCuryRate());
        transferset.setIInOut(1);
        transferset.setCheckStateId(1); //生成的直接进入已审核。sj edit 20080626.
        return transferset;
    }
    
    /**
     *add by huangqirong 2012-07-11 bug #4940 获取证券代码变更的证券代码
     * */
    private Hashtable<String, String> getSecuritys(java.util.Date dDate) throws YssException {
    	Hashtable<String, String> htSec = new Hashtable<String, String>();
		String sql = "";		
		ResultSet rs = null;		
		try {			
			sql = "select * from "
					+ pub.yssGetTableName("Tb_Para_SecCodeChange")
					+ " where FBUSINESSDATE="+dbl.sqlDate(this.dDate) 
					+ " and FCheckState=1 ";			
			rs = dbl.queryByPreparedStatement(sql);
			while (rs.next()) {
				htSec.put(rs.getString("FSecurityCodeBefore"), rs.getString("FSecurityCodeBefore"));				
			}
		} catch (Exception e) {
			throw new YssException("获取证券代码变更时出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs); // 释放资源
		}
		return htSec;
	}

    /**
     * 返回条件的债券信息代码
     * @param dDate Date
     * @param isAll String　是否全部显示　
     * @return String　返回证券代码集　用,隔开
     * @throws YssException
     * MS00241 QDV4中保2008年12月29日01_A
     */
    private String getFISecurity(java.util.Date dDate, String isAll) throws YssException {
        String sqlStr = "";
        String sSecurity = "";
        ResultSet rs = null;
        HashMap hmSecuInfo = null;
        BondInsCfgFormula mula = null;
        Iterator it = null;
        String errorMsg = "";
        try {
            if (isAll.equalsIgnoreCase("true")) { //若为全部显示，则直接返回
            	//add by huangqirong 2012-07-25 bug #4940
            	sSecurity = " where FSecurityCode not in (select fsecuritycodebefore from " 
            				+ pub.yssGetTableName("Tb_Para_SecCodeChange") + 
            				" where FBUSINESSDATE <= " + dbl.sqlDate(dDate) + " and FCheckState=1 )";
            	//---end---
                return sSecurity;
            }
            Hashtable<String ,String> htSecs = this.getSecuritys(dDate);//add by huangqirong 2012-07-25 bug #4940
            hmSecuInfo = new HashMap();
            sqlStr = "select interestTime.* from (select distinct FSecurityCode from  " + pub.yssGetTableName("TB_Stock_Security") +
                " where FCheckState = 1 and FPortCode in (" +
                operSql.sqlCodes(portCodes) + ") and " + operSql.sqlStoragEve(dDate) + ") stock" + //获取前一天的证券库存
                
                //20130411 modified by liubo.Story #3528
                //计息期间的获取，改为以债券计息期间设置的数据为准
                //================================
//                " join " +
//                " (select * from " + pub.yssGetTableName("Tb_Para_Fixinterest") +
//                " where FCheckState = 1) para on stock.FSecurityCode = para.FSecurityCode";
                " left join (select * from " + pub.yssGetTableName("tb_para_interesttime") + ") interestTime on stock.FSecurityCode = interestTime.FSecurityCode " +
                " where interestTime.Finsstartdate = " + dbl.sqlDate(dDate);
            	//================================
            
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	
//                mula = new BondInsCfgFormula();
//                mula.setYssPub(this.pub);
//                mula.getNextStartDateAndEndDate(dDate, rs, hmSecuInfo); //将债券信息和日期信息传入，以计算到期日期
//                
//                //--- add by jiangshichao BUG1835收益支付--债券利息收支，组合选择“017、044”选择业务日期为“2011-02-25”日及之后的日期，点击查询，系统报错 
//                if(hmSecuInfo.containsKey("error")){
//                	if(errorMsg.length()==0){
//                		errorMsg ="检测到以下债券计息起始日和计息截止日为同一天\r\n";
//                	}
//                	errorMsg+=rs.getString("FSecurityCode")+"\r\n";
//                	hmSecuInfo.remove("error");
//                	continue;
//                }
//                //add by yanghaiming 20110222 #601
//                if(rs.getString("FVALUEDATES") != null && rs.getString("FVALUEDATES").trim().length() > 0){//如果债券信息设置中有设置过起息日，则对起息日截止日处理
//                	mula.getRealStartDateAndEndDate(dDate,rs.getString("FSecurityCode"),rs, hmSecuInfo);
//                }
                //add by yanghaiming 20110222 #601

                //20130411 modified by liubo.Story #3528
                //计息期间的获取，改为以债券计息期间设置的数据为准
                //================================
//            	if (YssFun.dateDiff(YssFun.addDay( (java.util.Date) hmSecuInfo.get("InsStartDate"), -1), YssFun.addDay(dDate, -1)) == 0)
                if (YssFun.dateDiff(YssFun.addDay(rs.getDate("FINSSTARTDATE"), -1), YssFun.addDay(dDate, -1)) == 0) 
                { 
                    if(!htSecs.containsKey(rs.getString("FSecurityCode"))) //add by huangqirong 2012-07-25 bug #4940
                    	sSecurity += rs.getString("FSecurityCode") + ","; //拼装证券代码
                }
                hmSecuInfo.clear(); //清空容器，以便下次使用
            }
            if (sSecurity.length() > 1) {
                sSecurity = sSecurity.substring(0, sSecurity.length() - 1);
            }
            
            if(errorMsg.length()!=0){
            	throw new YssException(errorMsg);
            }
        } catch (Exception ex) {
            throw new YssException(errorMsg.length()!=0?errorMsg:"获取债券支付日期出现错误！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return " where FSecurityCode in (" + operSql.sqlCodes(sSecurity) + ")"; //拼装筛选语句,当无需显示的债券,则返回'';
    }
}
