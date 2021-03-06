package com.yss.main.operdeal.cashmanage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.main.cashmanage.CommandTypeBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.util.YssException;


public class CommandCfgForMula extends BaseCommandOper{
	public SecurityBean security;
	public CommandTypeBean command;
	// edit by songjie 2013.04.02 STORY #3528
	// 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
	public String sFIstg = "autoCalc";// 判断版本 QDV4中保2010年03月03日01_A MS01009
										// 获取版本信息 by leeyu 20100315
	public HashMap hmBondParams = null;// 保存债券参数 QDV4中保2010年03月03日01_A MS01009
										// 获取版本信息 by leeyu 20100315
	public static HashMap hmKey = null;
	public static HashMap hmSec = null;
	
	private String FTranType = "";      //划款方式
	private String FSellWay = "";		//销售渠道
	private String FPortCode = "";		//投资组合代码
	private String FAlgFormula = "";	//计算公式
	private String sellData = ""; //销售日期
	
	public String dateType = "";   //日期类型
	
	public String getFTranType() {
		return FTranType;
	}

	public void setFTranType(String fTranType) {
		FTranType = fTranType;
	}

	public String getFSellWay() {
		return FSellWay;
	}

	public void setFSellWay(String fSellWay) {
		FSellWay = fSellWay;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	public String getFAlgFormula() {
		return FAlgFormula;
	}

	public void setFAlgFormula(String fAlgFormula) {
		FAlgFormula = fAlgFormula;
	}

	public String getSellData() {
		return sellData;
	}

	public void setSellData(String sellData) {
		this.sellData = sellData;
	}

	public CommandCfgForMula() {
	}

	public void init(Object obj) throws YssException {
		if (obj == null) {
			return;
		}
		command = (CommandTypeBean) obj;
		this.sign = "(,),+,-,*,/,>,<,="; // 添加了最后三个标记，为了在函数中直接判断。sj edit
											// 20080804
		
		this.FTranType = command.getFTranType();
		this.FSellWay = command.getFSellWay();
		this.FPortCode = command.getFPortCode();
		this.formula = command.getFAlgFormula();
		this.sellData = command.getSellData();
		command.setYssPub(pub);
    }
    

	public void init2(Object obj, Date statDate, Date endDate, Date IssueDate, double FaceRate, String fixInterest)
			throws YssException {
		if (obj == null) {
			return;
		}
		command = (CommandTypeBean) obj;
		this.sign = "(,),+,-,*,/,>,<,=";// 添加了最后三个标记，为了在函数中直接判断

		
	}

	public double calBondInterest() throws YssException {
		return this.calcFormulaDouble();
	}
	
	public Object getKeywordValue(String sKeyword) throws YssException {
		Object objResult = null;
		String sellType = "";   //销售类型
		
		 if(sKeyword.equals("ConfirmDate"))
		 {
			 dateType = "FConfimDate";
		 }else if(sKeyword.equals("SettlementDate"))
		 {
			 dateType = "FSettleDate";
		 }else if(sKeyword.equals("TradeDate"))
		 {
			 dateType = "FTradeDate";
		 }
	    if (sKeyword.equalsIgnoreCase("TypeRedeem")) {
			objResult = "02";
		} else if(sKeyword.equalsIgnoreCase("TypeAllot")) {
			objResult = "01";
		} else if(sKeyword.equalsIgnoreCase("TypeTransferIn")) {
			objResult = "04";
		} else if(sKeyword.equalsIgnoreCase("TypeTransferOut")) {
			objResult = "05";
		} else if(sKeyword.equalsIgnoreCase("TypeBonus")) {
			objResult = "03";
		} else if(sKeyword.equalsIgnoreCase("TypeBonusConvert")) {
			objResult = "08";
		} else if(sKeyword.equalsIgnoreCase("TypeBonusArrival")) {
			objResult = "10";
		} else if(sKeyword.equalsIgnoreCase("TradeDate")) {
			//--add by liuxiaojun  stroy 4094 这里的确认日期  销售日期  结算日期全部视为交易日期
			objResult = this.sellData;
		} else if(sKeyword.equalsIgnoreCase("ConfirmDate")) {
			objResult = this.sellData;
		} else if(sKeyword.equalsIgnoreCase("SettlementDate")) {
			objResult = this.sellData;
		} else{
			objResult = sKeyword;
		}
	    //--end by liuxiaojun  stroy 4094 这里的确认日期  销售日期  结算日期全部视为交易日期
		 return objResult;
		
    }
	
	 public Object getExpressValue(String sKeyword, ArrayList alParams) throws
     YssException {
		 Object objResult = null;
		 if(sKeyword.equalsIgnoreCase("SalesAmount")) {
			 String sKey = "FSellMoney";
			 if (alParams.size() == 2){
				 objResult = new Double(getMoneyValue(sKey,FPortCode,dateType,(String)alParams.get(0),(String)alParams.get(1)));
			 }
		} else if(sKeyword.equalsIgnoreCase("SettlementAmount")) {
			String sKey = "FSettleMoney";
			 if (alParams.size() == 2){
				 objResult = new Double(getMoneyValue(sKey,FPortCode,dateType,(String)alParams.get(0),(String)alParams.get(1)));
			 }
		} else if(sKeyword.equalsIgnoreCase("PaidUpFundAmount")) {
			String sKey = "转入";     //目前tb_xxx_ta_trade  无实收基金金额字段
			 if (alParams.size() == 2){
				 objResult = new Double(getMoneyValue(sKey,FPortCode,dateType,(String)alParams.get(0),(String)alParams.get(1)));
			 }
		} else if(sKeyword.equalsIgnoreCase("TradeExpense1")) {
			String sKey = "FTradeFee1";
			 if (alParams.size() == 2){
				 objResult = new Double(getMoneyValue(sKey,FPortCode,dateType,(String)alParams.get(0),(String)alParams.get(1)));
			 }
		} else if(sKeyword.equalsIgnoreCase("TradeExpense2")) {
			String sKey = "FTradeFee2";
			 if (alParams.size() == 2){
				 objResult = new Double(getMoneyValue(sKey,FPortCode,dateType,(String)alParams.get(0),(String)alParams.get(1)));
			 }
		} else if(sKeyword.equalsIgnoreCase("TradeExpense3")) {
			String sKey = "FTradeFee3";
			 if (alParams.size() == 2){
				 objResult = new Double(getMoneyValue(sKey,FPortCode,dateType,(String)alParams.get(0),(String)alParams.get(1)));
			 }
		} else if(sKeyword.equalsIgnoreCase("TradeExpense4")) {
			String sKey = "FTradeFee4";
			 if (alParams.size() == 2){
				 objResult = new Double(getMoneyValue(sKey,FPortCode,dateType,(String)alParams.get(0),(String) alParams.get(1)));
			 }
		} else {
			objResult = sKeyword;
		}
		 return objResult;
	 }
	
	public String getTaSellWayValue(String sKey) throws YssException {
		
		 String strSql = "";
		 String dResult = "";
		 ResultSet rs = null;
		 try{
			 strSql = "select FSellTypeCode from " +
             pub.yssGetTableName("Tb_Ta_SellType") +
             " where FSellTypeName = " +
             dbl.sqlString(sKey);
			 rs=dbl.openResultSet(strSql);
			 if(rs.next()){
				 dResult = rs.getString("FSellTypeCode");
			 }
			 dbl.closeResultSetFinal(rs);
			 return dResult;
		 }catch (Exception e) {
			// TODO: handle exception
			 throw new YssException("获取销售类型出错", e);
		}finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	
	public Double getMoneyValue(String sKey,String portCode,String dateType,String sellType,String date ) throws YssException{
		String strSql = "";
		double dResult = 0.0;
		ResultSet rs = null;
		
		 try{
			 strSql = "select "+ sKey +" from " +
             pub.yssGetTableName("Tb_Ta_Trade") +
             " where FPortCode = " +
             dbl.sqlString(portCode) +
             " and FSellType = " + 
             dbl.sqlString(sellType) +
             " and  "+dateType+" = " +
             dbl.sqlDate(date);
             System.out.println(strSql);
			 rs=dbl.openResultSet(strSql);
			 while(rs.next()){
				 dResult += rs.getDouble(sKey);
			 }
			 dbl.closeResultSetFinal(rs);
			 System.out.println(dResult);
			 return dResult;
		 }catch (Exception e) {
			// TODO: handle exception
			 throw new YssException("获取金额出错", e);
		}finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	
	
}
