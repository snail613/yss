package com.yss.main.operdeal.income.paid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.dao.IYssConvert;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdata.InvestPayRecBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.pojo.dayfinish.InvestPaid;
import com.yss.pojo.dayfinish.ScotDefrayBean;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @author shashijie ,2011-9-28 下午04:26:19 STORY 1561 送股税金
 */
public class BounsScot extends BaseIncomePaid {
	CashTransAdmin cashtransAdmin = new CashTransAdmin(); //生成资金调拨控制类
    CashPayRecAdmin cashpayrecadmin = new CashPayRecAdmin(); //生成现金应收应付控制类
	
    public BounsScot() {
    }

    public void calculateIncome(IYssConvert bean) {
    	
    }

    public ArrayList getIncomes() throws YssException {
        ArrayList alResult = new ArrayList();
        try {
            alResult.addAll(getDayIncomes(dDate));
            return alResult;
        } catch (Exception e) {
            throw new YssException("获取送股税金数据出错", e);
        }
    }

    /**获取需要送股交税的数据
     * @param dDate 业务日期
     * @return
     * @throws YssException
     * @author shashijie ,2011-9-28 , STORY 1561
     * @modified 
     */
    protected ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
        ArrayList alPaid = new ArrayList();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = getStrSql();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	ScotDefrayBean scot = getScotDefaryBean(rs);
                alPaid.add(scot);
            }
            return alPaid;
        } catch (Exception e) {
            throw new YssException("取应交税金出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取昨日库存的送服应付税金数据
     * @return
     * @author shashijie ,2011-9-29 , STORY 1561
     * @modified
     */
    private String getStrSql() {
		String sqlString = 
			" select e.FPortname,d.FSubAccTypeName,c.FAcctypeName,b.FAccType,b.FSubAccType,b.FCuryCode," +
			" b.FCashAccName, " +
			//现金应收应付库存
			" a.* From "+pub.yssGetTableName("Tb_Stock_CashPayRec")+" a left join (select b1.FCashAccCode, "+
	        " b1.FCashAccName,b1.FCuryCode,b1.FAccType,b1.FSubAccType From "+
	        //现金账户
	        pub.yssGetTableName("Tb_Para_CashAccount")+" b1 where b1.FCheckState = 1) b on a.FCashAccCode = "+
	        //账户类型设置
	        " b.FCashAccCode left join (select c1.FAcctypeCode, c1.FAcctypeName From Tb_Base_AccountType c1 "+
	        " where c1.FCheckState = 1) c on c.FacctypeCode = b.FAccType left join " +
	        //账户子类型设置
	        " (select d1.FSubAccTypeCode, d1.FSubAccTypeName From Tb_Base_SubAccountType d1 "+
	        " where d1.FCheckState = 1) d on d.FSubAccTypeCode = b.FSubAccType " +
	        //组合设置
	        " left join (select e1.FPortCode,e1.FPortName from "+pub.yssGetTableName("Tb_Para_Portfolio")+
	        " e1 where e1.FCheckState = 1 ) e on e.FPortCode = a.FPortCode "+
	        " where a.FCheckState = 1 "+
	        " and a.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))+
	        " and a.FTsfTypeCode = '07'"+
	        " and a.FSubTsfTypeCode = '07SE'";
		return sqlString;
	}

	/**设置实体BEAN
     * @param rs
     * @return
     * @author shashijie ,2011-9-28 , STORY 1561
     * @modified 
     */
    private ScotDefrayBean getScotDefaryBean(ResultSet rs) throws Exception {
    	ScotDefrayBean scot = new ScotDefrayBean();
        
    	scot.setBal(rs.getDouble("FBal"));
    	scot.setStorageDate(rs.getDate("FStorageDate"));
        scot.setPortCode(rs.getString("FPortCode"));
        scot.setPortName(rs.getString("FPortName"));
        scot.setCuryCode(rs.getString("FCuryCode"));
        
        scot.setAnalysisCode1(rs.getString("FAnalysisCode1"));
        
        scot.setAnalysisCode2(rs.getString("FAnalysisCode2"));
        
        scot.setAnalysisCode3(rs.getString("FAnalysisCode3"));
        
        scot.setCashAccCode(rs.getString("FCashAccCode"));
        scot.setCashAccName(rs.getString("FCashAccName"));
        scot.setTsfTypeCode(rs.getString("FAccType"));
        scot.setTsfTypeName(rs.getString("FAccTypeName"));
        scot.setSubTsfTypeCode(rs.getString("FSubAccType"));
        scot.setSubTsfTypeName(rs.getString("FSubAccTypeName"));
        
        //公共获取汇率类
		BaseOperDeal base = new BaseOperDeal();
		base.setYssPub(pub);
		//基础汇率
        double dBaseRate = base.getCuryRate(dDate, rs.getString("FCuryCode"),
        								rs.getString("FPortCode"),
										YssOperCons.YSS_RATE_BASE);
        //组合汇率
        double dPortRate = base.getCuryRate(dDate, rs.getString("FCuryCode"),
        								rs.getString("FPortCode"),
					                    YssOperCons.YSS_RATE_PORT);
        scot.setdBaseRate(dBaseRate);
        scot.setdPortRate(dPortRate);
        
        return scot;
	}

	/**
	 * shashijie 2011-10-08 STORY 1561 保存送股税金
	 */
	public void saveIncome(ArrayList alIncome) throws YssException {
		cashtransAdmin.setYssPub(pub);//资金调拨
        cashpayrecadmin.setYssPub(pub);//现金应收应付
		try{
			//送股税金业务
	    	doOpertionBounsScot(alIncome);
	    	//此处产生资金调拨
	        createCashTransfer();
	        //此处产生现金应收应付
	        createCashPayRecAdmin();
        } catch (Exception e) {
            throw new YssException("系统保存送股税金支付时出现异常!" + "\n", e);
        }
    }

	/**
	 * 产生资金调拨数据
	 * @author shashijie ,2011-10-8 , STORY 1561
	 * @modified 
	 */
	private void createCashTransfer() throws YssException {
		try {
    		//先删后增加资金调拨
    		cashtransAdmin.insert("", null,dDate,YssOperCons.YSS_ZJDBLX_Fee,
    				"03SE","", "","","", "","BounsShareRate", "",-1,"",portCodes,0,"","","",true,
    				"","");
        } catch (Exception ex) {
            throw new YssException("生成资金调拨出现异常！", ex);
        } finally {
            //dbl.endTransFinal(conn, bTrans);
        }
	}
	

	/**
	 * 产生现金应收应付数据
	 * @author shashijie ,2011-10-8 , STORY 1561
	 * @modified 
	 */
	private void createCashPayRecAdmin() throws YssException {
		try{
			//删除之前生成的应收数据
			cashpayrecadmin.delete("", dDate, dDate, 
					YssOperCons.YSS_ZJDBLX_Fee+","+YssOperCons.Yss_ZJDBLX_REGULATE, 
					"03SE,9803SE", "", "", this.portCodes, "",
					"", "", 1,0,"","BounsShareRate");
			//生成现金应收应付数据
			cashpayrecadmin.insert();
    	 }catch (Exception ex) {
             throw new YssException("生成现金应收应付出现异常！", ex);
         } finally {
             //dbl.endTransFinal(conn, bTrans);
         }    	
	}

	/**
	 * 处理送股税金数据
	 * @param alIncome
	 * @author shashijie ,2011-10-8 , STORY 1561
	 * @modified
	 */
    private void doOpertionBounsScot(ArrayList alIncome) throws YssException{
    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    	Date logStartTime = null;//业务子项开始时间
    	String portCode = "";//组合代码
		//add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A 设置菜单代码
    	this.setFunName("incomepaid");
    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    	try{
    		for (int i = 0; i < alIncome.size(); i++) {
    			ScotDefrayBean scot = (ScotDefrayBean)alIncome.get(i);
    			
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    			logInfo = "";
    			logStartTime = new Date();
    			portCode = scot.getPortCode();
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    			
    			//原币金额  = 应付税金
    			double bMoney = Double.valueOf(scot.getBalMoney()).doubleValue();
    			//现金应收应付
    			CashPecPayBean cashpecpay = setCashPecPay(scot,YssOperCons.YSS_ZJDBLX_Fee,"03SE",bMoney);
    			cashpayrecadmin.addList(cashpecpay);
    			//若调整金额不为空,则再产生,调整金额9803SE应收应付数据
    			if (scot.getAdjustMoney()!=null && scot.getAdjustMoney().trim().length()>0) {
    				CashPecPayBean cashpecpay2 = setCashPecPay(scot,YssOperCons.Yss_ZJDBLX_REGULATE,"9803SE",
    						Double.valueOf(scot.getAdjustMoney()).doubleValue());
    				cashpayrecadmin.addList(cashpecpay2);
    			}
    			//设置资金调拨
    			createPrincipalExtCashTransfer(scot);
    			
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,20,operType,this.pub,false,
                		portCode,dDate,dDate,dDate,logInfo,
                		logStartTime,logSumCode, new Date());//20收益支付 ，operType是处理项目
        		}
                //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    		}
    	}catch(Exception e){
    		//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    		try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,20,operType,this.pub,
                		true, portCode, dDate, dDate, dDate,
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + " \r\n 支付送股税金出错 \r\n " + e.getMessage())//处理日志信息 除去特殊符号
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
    			throw new YssException ("支付送股税金出错！",e);
    		}
    		//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
    	}
	}

    /**
     * 设置资金调拨
     * @param scot
     * @author shashijie ,2011-10-8 , STORY 1561
     * @modified
     */
    private void createPrincipalExtCashTransfer(ScotDefrayBean scot) throws YssException {
    	TransferBean transfer = null;//现金调拨（主表）
        TransferSetBean transferSet = null;//资金调拨(子表)
        ArrayList subTransfer = new ArrayList();//存放资金调拨(子表集合)
        try {
        	//设置资金调拨主表
    		transfer = setTransfer1(scot,YssOperCons.YSS_ZJDBLX_Fee,"03SE");
            //资金调拨子表
        	transferSet = setTransferSet(scot);
        	
        	//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
        	logInfo += "到账日期:" + YssFun.formatDate(transfer.getDtTransferDate(),"yyyy-MM-dd") +
        		       "\r\n现金账户代码:" + transferSet.getSCashAccCode() +
        	           "\r\n" + ((transferSet.getIInOut() == 1) ? "流入":"流出") + 
        	           "金额:" + transferSet.getDMoney() +
        	           YssCons.YSS_LINESPLITMARK;
        	//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
        	
        	subTransfer.add(transferSet);
        	 
        	transfer.setSubTrans(subTransfer);//将子数据放入资金调拨中
        	cashtransAdmin.addList(transfer);
        } catch (Exception e) {
            throw new YssException("生成资金调拨出现异常！", e);
        }
	}

    
	/**设置资金调拨子数据的基本数据
	 * @param scot
	 * @return
	 * @author shashijie ,2011-10-8 , STORY 1561
	 * @modified 
	 */
	private TransferSetBean setTransferSet(ScotDefrayBean scot) throws YssException {
		TransferSetBean transferSet = null;
        try {
            transferSet = new TransferSetBean();
            transferSet.setSPortCode(scot.getPortCode());//组合代码
            transferSet.setStrAttrClsCode(" ");//所属分类
            
            //---设置分析代码 ---------------------------------------------------------
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
    		boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
    		boolean analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
    		
            transferSet.setSAnalysisCode1(analy1 ? " " : " ");
            transferSet.setSAnalysisCode2(analy2 ? " " : " ");
            transferSet.setSAnalysisCode3(analy3 ? " " : " ");
            //-----------------------------------------------------------------------
            
            //实际支付金额
            transferSet.setDMoney(Double.valueOf(scot.getRealMoney()).doubleValue());//调拨金额
            transferSet.setDBaseRate(scot.getdBaseRate());//基础汇率
            transferSet.setDPortRate(scot.getdPortRate());//组合汇率
            transferSet.setSCashAccCode(scot.getCashAccCode());//现金帐户代码
        	transferSet.setIInOut(-1);//资金流向 1代表流入;-1代表流出
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
        return transferSet;
	}

	/**设置现金调拨（主表）
	 * @param scot
	 * @param yssZjdblxFee
	 * @param string
	 * @return
	 * @author shashijie ,2011-10-8 , STORY 1561
	 * @modified 
	 */
	private TransferBean setTransfer1(ScotDefrayBean scot, String TypeCode,
			String TsfTypeCode) throws YssException {
		TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(dDate);//业务日期
            transfer.setDtTransferDate(scot.getPaidDate());//调拨日期
            transfer.setStrTsfTypeCode(TypeCode);//调拨类型
            transfer.setStrSubTsfTypeCode(TsfTypeCode);//调拨子类型
            transfer.setFNumType("BounsShareRate");//编号类型(换股对价)
            transfer.setFRelaNum("");//关联编号
            transfer.setCprNum("");//现金应收应付编号
            transfer.setSrcCashAccCode("FCashAccCode");//来源帐户代码
            transfer.setStrSecurityCode("");//投资品种代码
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
	}

	/**设置现金应收应付数据 
	 * @param scot 支付Bean对象
	 * @param TypeCode 业务类型
	 * @param TsfTypeCode 业务子类型
	 * @param bMoney 原币金额
	 * @author shashijie ,2011-10-08 , STORY 1561
	 * @modified 
	 */
	private CashPecPayBean setCashPecPay(ScotDefrayBean scot,String TypeCode,String TsfTypeCode,double bMoney) 
					throws YssException{
    	CashPecPayBean cashpecpay = null;
    	try{
    		cashpecpay = new CashPecPayBean();
    		//cashpecpay.setTradeDate(rs.getDate("FOperDate"));//业务日期
    		cashpecpay.setTradeDate(dDate);//业务日期
    		cashpecpay.setTsfTypeCode(TypeCode);//业务类型
    		cashpecpay.setSubTsfTypeCode(TsfTypeCode);//业务子类型
    		cashpecpay.setStrAttrClsCode(" ");//所属分类
    		cashpecpay.setRelaNum("");//关联编号
    		cashpecpay.setRelaNumType("BounsShareRate");//关联编号类型
    		//原币金额
    		cashpecpay.setMoney(bMoney);//金额
    		cashpecpay.setDataSource(1);//来源标志
    		cashpecpay.checkStateId = 1;
    		cashpecpay.setPortCode(scot.getPortCode());//组合代码
    		cashpecpay.setNum("");//编号
    		cashpecpay.setCashAccCode(scot.getCashAccCode());//现金账户
    		cashpecpay.setCuryCode(scot.getCuryCode());//币种代码
    		cashpecpay.setInOutType(1);//方向
    		//cashpecpay.setInvestManagerCode();//投资经理代码
    		cashpecpay.setBaseCuryRate(scot.getdBaseRate());//基础汇率
    		cashpecpay.setPortCuryRate(scot.getdPortRate());//组合汇率
    		//基础货币金额
    		double bacecurymoney = YssD.mul(cashpecpay.getMoney(),cashpecpay.getBaseCuryRate());
    		cashpecpay.setBaseCuryMoney(bacecurymoney);
    		//组合货币金额
    		double portcurymoney = YssD.div(bacecurymoney, cashpecpay.getPortCuryRate());
    		cashpecpay.setPortCuryMoney(portcurymoney);
    	}catch (Exception e) {
            throw new YssException("设置现金应收应付数据出现异常！", e);
        }
        return cashpecpay; //返回资金调拨数据
    	
    }
	
	protected InvestPayRecBean setInvestRecPayAttr(InvestPaid paid,
        String equals) throws
        YssException {
		try {
			InvestPayRecBean investpay = new InvestPayRecBean();
			// investpay.setTradeDate(paid.getPDate()); //业务日期为支付日期。sj edit
			// 20080806 暂无 0000372
			investpay.setTradeDate(paid.getmDate());// edit by yanghaiming
													// 20100416 MS00997
													// QDV4建行2010年02月23日01_B
													// 增加mdate为业务日期
			investpay.setFIVPayCatCode(paid.getIVPayCatCode());
			investpay.setPortCode(paid.getPortCode());
			investpay.setAnalysisCode1(paid.getAnalysisCode1());
			investpay.setAnalysisCode2(paid.getAnalysisCode2());
			// -----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified
			// ------------//
			investpay.setAnalysisCode3(paid.getAnalysisCode3());
			// --------------------------------------------------------------------------------//
			investpay.setTsftTypeCode(paid.getTsfTypeCode());
			investpay.setCuryCode(paid.getCuryCode());

			// add by lidaolong 20110314 #386 增加一个功能，能够自动支付管理费和托管费
			ResultSet rs = getNewRate(paid);
			// double BaseCuryRate = 0;//
			// double PortCuryRate = 0;//
			if (rs.next()) {
				// 基础汇率 = 昨日库存应付基础货币总额 / 昨日库存应付原币总额，
				// 组合汇率 = 昨日库存应付基础货币总额 / 昨日库存应付组合货币总额；
				// BaseCuryRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYBAL"));
				// PortCuryRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYPBAL"));
			}
			// end by lidaolong 20110314

			if (equals.equalsIgnoreCase("true")) {
				// ----modify by wuweiqi 20110211 BUG #1036 两非支付后，查看运营应收应付原币发现为0
				// -------//
				// investpay.setMoney(paid.getInvestMoney());
				investpay.setMoney(paid.getMoney());
				// -----BUG #1036 end by wuweiqi
				// 20110211----------------------------------------------//
				if (paid.getInvestMoney() == 0.0) {
					investpay.setBaseCuryMoney(paid.getBaseCuryMoney());
					investpay.setPortCuryMoney(paid.getPortCuryMoney());
				} else {
					// ---add by songjie 2011.07.01 BUG 2161
					// QDV4建行2011年06月23日01_B start---//
					// 若实付运营金额 不等于应付运营金额，则已实付运营金额为准生成运营应收应付数据
					if (paid.getMoney() != paid.getInvestMoney()) {
						investpay.setBaseCuryMoney(this.getSettingOper()
								.calBaseMoney(paid.getMoney(),
										paid.getBaseCuryRate()));
						investpay.setPortCuryMoney(this.getSettingOper()
								.calPortMoney(paid.getMoney(),
										paid.getBaseCuryRate(),
										paid.getPortCuryRate(),
										// linjunyun 2008-11-25 bug:MS00011
										// 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
										paid.getCuryCode(), paid.getDDate(),
										paid.getPortCode()));
					} else {
						// ---add by songjie 2011.07.01 BUG 2161
						// QDV4建行2011年06月23日01_B end---//
						// edit by lidaolong 用昨日应收应付中的数据
						// 改成用公有的计算基础货币金额的方法来计算
						investpay.setBaseCuryMoney(this.getSettingOper()
								.calBaseMoney(paid.getInvestMoney(),
										paid.getBaseCuryRate()));
						investpay.setPortCuryMoney(this.getSettingOper()
								.calPortMoney(paid.getInvestMoney(),
										paid.getBaseCuryRate(),
										paid.getPortCuryRate(),
										// linjunyun 2008-11-25 bug:MS00011
										// 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
										paid.getCuryCode(), paid.getDDate(),
										paid.getPortCode()));
						// //end by lidaolong 20110314
					}// add by songjie 2011.07.01 BUG 2161 QDV4建行2011年06月23日01_B
						// 添加右大括号
				}

				investpay.setSubTsfTypeCode(paid.getSubTsfTypeCode());
			} else if (equals.equalsIgnoreCase("false")) {
				investpay.setMoney(paid.getBalMoney());

				investpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
						investpay.getMoney(), paid.getBaseCuryRate()));
				investpay.setPortCuryMoney(this.getSettingOper()
						.calPortMoney(investpay.getMoney(),
								paid.getBaseCuryRate(), paid.getPortCuryRate(),
								// linjunyun 2008-11-25 bug:MS00011
								// 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
								paid.getCuryCode(), paid.getDDate(),
								paid.getPortCode()));
				investpay.setTsftTypeCode("98"); // 调拨类型转换成98 sj edit 20080218
				investpay.setSubTsfTypeCode("98" + paid.getSubTsfTypeCode());

			}

			// add by lidaolong 20110316 #386 增加一个功能，能够自动支付管理费和托管费
			investpay.setBaseCuryRate(paid.getBaseCuryRate());
			investpay.setPortCuryRate(paid.getPortCuryRate());
			/*
			 * investpay.setBaseCuryRate(BaseCuryRate);
			 * investpay.setPortCuryRate(PortCuryRate);
			 */
			// end by lidaolong 20110316

			/*
			 * paid.getMoney金额均为0，所以基础货币金额和组合货币金额不能用paid.getMoney进行计算fazmm20070804
			 * investpay.setBaseCuryMoney(YssD.mul(paid.getMoney(),
			 * paid.getBaseCuryRate()));
			 * investpay.setPortCuryMoney(YssD.div(YssD.mul(paid.getMoney(),
			 * paid.getBaseCuryRate()), paid.getPortCuryRate()));
			 */
			investpay.setCheckState(1); // 生成的应收应付直接进入已审核。sj edit 20080626.
			return investpay;

		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		}
    }

    protected TransferBean setTransferAttr(InvestPaid paid) {
        TransferBean transfer = new TransferBean();
        transfer.setDtTransferDate(paid.getPDate()); //修改为调拨日期。sj edit 20081017
        transfer.setDtTransDate(paid.getmDate());//edit by yanghaiming 20100416 MS00997  QDV4建行2010年02月23日01_B 增加mdate为业务日期
        transfer.setCheckStateId(1);
        //---- MS00331 QDV4中保2009年03月20日01_B 自动数据的标示调整为正确的数据 ---------
        transfer.setDataSource(1); //1为自动，0为手工
        //--------------------------------------------------------------------------
        transfer.setStrTsfTypeCode(paid.getTsfTypeCode());
        transfer.setStrSubTsfTypeCode(paid.getSubTsfTypeCode());
        return transfer;
    }

    protected TransferSetBean setTransferSetAttr(InvestPaid paid) throws YssException, SQLException {
        TransferSetBean transferset = new TransferSetBean();
        transferset.setSPortCode(paid.getPortCode());
        transferset.setSAnalysisCode1(paid.getAnalysisCode1());
        transferset.setCheckStateId(1);
        //---------QDV4中保2008-11-4日01_B ----若为02IV(YSS_ZJDBZLX_IV_Income)的，则为收入类，03IV的则为支出类。sj modified 20081218  ---//
        transferset.setSAnalysisCode2(paid.getAnalysisCode22()); //使用现金类的分析代码来填充值。
        //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
        transferset.setSAnalysisCode3(paid.getAnalysisCode23()); //使用现金类的分析代码来填充值。
        //--------------------------------------------------------------------------------//
        transferset.setIInOut(paid.getSubTsfTypeCode().equalsIgnoreCase(
            YssOperCons.YSS_ZJDBZLX_IV_Income) ? 1 : -1); //20070806
        //-------------------------------------------------------------------------------------------------------------------------//
        transferset.setSCashAccCode(paid.getCashAccCode());
        transferset.setDMoney(paid.getMoney());
        
        //add by lidaolong 20110314 #386 增加一个功能，能够自动支付管理费和托管费
    	ResultSet rs = getNewRate(paid);
    	//基础汇率 = 昨日现金库存基础货币总额 / 昨日现金库存原币总额，
    	//组合汇率 = 昨日现金库存基础货币总额  /  昨日现金库存组合货币总额
    	//double dBaseRate = 1;//
        //double dPortRate = 1;//
    	if (rs.next()){
    		//---add by songjie 2011.05.16 BUG 1887 QDV4海富通2011年05月06日01_B---//
    		if(rs.getDouble("FYESTERDAYBAL") != 0){
	    		//dBaseRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYBAL"));
	    		//dPortRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYPBAL"));
	    		transferset.setDBaseRate(paid.getBaseCuryRate());
	    		transferset.setDPortRate(paid.getPortCuryRate());
	    		//---add by songjie 2011.05.16 BUG 1887 QDV4海富通2011年05月06日01_B---//
	            //---delete by songjie 2011.05.16 BUG 1887 QDV4海富通2011年05月06日01_B---//
	    		//    		if(rs.getDouble("FYesAccBal") !=0){
	//    		dBaseRate=YssD.div(rs.getDouble("FYesAccBBal"),rs.getDouble("FYesAccBal"));
	//    		dPortRate=YssD.div(rs.getDouble("FYesAccBBal"),rs.getDouble("FYesAccPBal"));
	//    		transferset.setDBaseRate(dBaseRate);
	//    		transferset.setDPortRate(dPortRate);	
	    		//---delete by songjie 2011.05.16 BUG 1887 QDV4海富通2011年05月06日01_B---//    		
	    	 }else{
    			transferset.setDBaseRate(paid.getBaseCuryRate());
        		transferset.setDPortRate(paid.getPortCuryRate());
    	 }
        dbl.closeResultSetFinal(rs);
    	}else{
			transferset.setDBaseRate(paid.getBaseCuryRate());
    		transferset.setDPortRate(paid.getPortCuryRate());
	 }
    	//end by lidaolong 20110314
        return transferset;
    }
    
    /**
     * 修改产生的运营应收应付数据和资金调拨
     * add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
     * @throws YssException 
     */
    private ResultSet getNewRate(InvestPaid investPay) throws YssException{
   
		ResultSet rs = null;

		String strSql = // 昨日的运营应收应付库存（用来计算基础汇率和组合汇率）
		"select SI.*,SC.*  from ( "
				+ "select y.FBal as FYesterdayBal,y.FPortCuryBal as FYesterdayPBal,y.FBaseCuryBal as FYesterdayBBal"
				+ ",y.FPortCode,y.FIVPayCatCode "
				+ " from " + pub.yssGetTableName("Tb_Stock_InvestPayRec") + " y"
				+ " where FTsfTypeCode ='07' and FSubTsfTypeCode ='07IV'and FStorageDate = "
				+ dbl.sqlDate(YssFun.addDay(dDate, -1))
				+ " and y.Fportcode="
				+ dbl.sqlString(investPay.getPortCode())
				+ " and y.Fivpaycatcode="
				+ dbl.sqlString(investPay.getIVPayCatCode())
				+ " group by y.FPortCode,y.FIVPayCatCode,y.FBal,y.FPortCuryBal,y.FBaseCuryBal) SI"
				+
				// 昨日的现金库存（用来计算资金调拨的基础汇和组合汇率）
				" left join ("
				+ "select FAccBalance as FYesAccBal,FPortCuryBal as FYesAccPBal,FBaseCuryBal as FYesAccBBal,FPortCode,FCashAccCode"
				+ " from " + pub.yssGetTableName("Tb_Stock_Cash") + " where FStorageDate ="
				+ dbl.sqlDate(YssFun.addDay(dDate, -1)) + " and FCuryCode ="
				+ dbl.sqlString(investPay.getCuryCode()) + ") SC"
				+ " on (SC.FPortCode = SI.FPortCode )";

		try {
			rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);

		} catch (SQLException e) {
			throw new YssException(e.getMessage());
		}
		return rs;
    }
}
