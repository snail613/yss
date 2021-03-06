package com.yss.commeach;

import java.sql.ResultSet;
import java.util.*;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssOperCons;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.storagemanage.CashStorageBean;
import com.yss.util.YssCons;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EachRateOper
    extends BaseCommEach {
    private String sPortCode;
    private String sVariableCode; //可变参数，可为货币或账户代码
    private Date dRateDate;
    private String sBaseRateSrc;
    private String sPortRateSrc;
    //--------MS00011 ----------//
    private double dBaseRate;
    private double dPortRate;
    private double dPortMoney;
    private double dBal;
    private double dBaseMoney;
    //-----------------------------
  	//--------MS00849 QDV4中保2009年12月07日01_A  蒋世超 添加 2009.12.10----------------
   	private double dMoney;//调拨金额
   	private String sInOut;//资金流向
   	private String sManagerCode;//投资经理
   	private String sType;//品种类型
   	private String sSecurityCode;//证券代码  by guyichuan 20110426 STORY #562
  	//--------MS00849 QDV4中保2009年12月07日01_A end ---------------------------------- 
    public EachRateOper() {
    }

    public void setDBaseMoney(double dBaseMoney) {
        this.dBaseMoney = dBaseMoney;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sVariableCode = sCuryCode;
    }

    public void setDRateDate(Date dRateDate) {
        this.dRateDate = dRateDate;
    }

    public void setSBaseRateSrc(String sBaseRateSrc) {
        this.sBaseRateSrc = sBaseRateSrc;
    }

    public void setSPortRateSrc(String sPortRateSrc) {
        this.sPortRateSrc = sPortRateSrc;
    }

    public double getDBaseMoney() {
        return dBaseMoney;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public String getSCuryCode() {
        return sVariableCode;
    }

    public Date getDRateDate() {
        return dRateDate;
    }

    public String getSBaseRateSrc() {
        return sBaseRateSrc;
    }

    public String getSPortRateSrc() {
        return sPortRateSrc;
    }

    //MS00177 添加汇率的GET方法 leeyu
    public double getDPortRate() {
        return dPortRate;
    }

    //MS00177 添加汇率的GET方法 leeyu
    public double getDBaseRate() {
        return dBaseRate;
    }

    //----------------------------------------------------------------------------
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\t", -1);
            this.sPortCode = reqAry[0];
            this.sVariableCode = reqAry[1];
            this.sSecurityCode=reqAry[2];//by guyichuan 20110426 STORY #562
            if (YssFun.isDate(reqAry[3])) {
                this.dRateDate = YssFun.parseDate(reqAry[3]);
            } else {
                this.dRateDate = YssFun.parseDate("9998-12-31");
            }
            this.sBaseRateSrc = reqAry[4];
            this.sPortRateSrc = reqAry[5];
            this.dBaseRate = YssFun.toDouble(reqAry[6]);
            this.dPortRate = YssFun.toDouble(reqAry[7]);
            this.dBal = YssFun.toDouble(reqAry[8]);
	        //--- MS00849 MS00849 QDV4中保2009年12月07日01_A  蒋世超 添加 2009.12.10 --------------
	        if(reqAry.length ==15 ){
	            this.sInOut = reqAry[10];
	        	this.dMoney = YssFun.toDouble(reqAry[11]);
	        	this.sManagerCode = reqAry[12];
	        	this.sType = reqAry[13];
	        } 
	        //--- MS00849 MS00849 QDV4中保2009年12月07日01_A  END --------------------------------
        } catch (Exception e) {
            throw new YssException("解析汇率信息出错\r\n" + e.getMessage(), e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sPortCode).append("\t");
        buf.append(this.sVariableCode).append("\t");
        buf.append(YssFun.formatDate(this.dRateDate)).append("\t");
        buf.append(Double.toString(this.dBaseRate)).append("\t");
        buf.append(Double.toString(this.dPortRate)).append("\t");
        buf.append(Double.toString(this.dPortMoney)).append("\t");
        buf.append(Double.toString(this.dBaseMoney)).append("\t");
        return buf.toString();
    }

    /**
     * 使用现金帐户计算组合货币金额
     * @param dRateDate Date
     * @param sCashAccCode String
     * @param sPortCode String
     * @throws YssException
     */
    private void calcPortMoneyWithAcc(Date dRateDate,
                                      String sCashAccCode, String sPortCode) throws
        YssException {
        CashAccountBean cashAcc = null;
        cashAcc = new CashAccountBean();
        cashAcc.setYssPub(pub);
        cashAcc.setStrCashAcctCode(this.sVariableCode);
        cashAcc.getSetting();
        this.dPortMoney = this.getSettingOper().calPortMoney(this.dBal,
            this.dBaseRate, this.dPortRate, cashAcc.getStrCurrencyCode(),
            dRateDate, sPortCode);
    }

    /**
     * 使用交易货币计算组合货币金额
     * @param dRateDate Date
     * @param sCuryCode String
     * @param sPortCode String
     * @throws YssException
     */
    private void calcPortMoneyWithCury(Date dRateDate,
                                       String sCuryCode, String sPortCode) throws
        YssException {
        //--------MS00215 QDV4中保2009年01月22日01_B 将两位小数调整为4位小数 sj modified 20090124
        int digit = 0;
        digit = getDigit(this.dBal); //获取动态设置小数位。
        this.dPortMoney = this.getSettingOper().calPortMoney(this.dBal,
            this.dBaseRate, this.dPortRate, sCuryCode,
            dRateDate, sPortCode, digit); //将获取小数位加入方法中。
        //-----------------------------------------------------------------------------------
    }

    /**
     * 用原币和基础汇率计算基础货币金额
     * 20081209   王晓光
     * @param dRateDate Date
     * @param sCuryCode String
     * @param sPortCode String
     * @throws YssException
     */
    private void calcBaseMoneyWithCury() throws
        YssException {
        //--------MS00215 QDV4中保2009年01月22日01_B 将两位小数调整为4位小数 sj modified 20090124 -----------------------
        int digit = 0;
        digit = getDigit(this.dBal); //获取动态设置小数位。
        //----- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415  --------
        if (this.dBaseRate == 0.0) { //如果没有基础汇率值，新获取
            getRate(this.dRateDate, this.sVariableCode, this.sPortCode);
        }
        //-------------------------------------------------------------------
        this.dBaseMoney = this.getSettingOper().calBaseMoney(this.dBal, this.dBaseRate, digit); ////将获取小数位加入方法中。
        //-----------------------------------------------------------------------------------------------------------
    }

    /**
     * 通过账户代码获计算基础金额
     * @throws YssException
     * MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415
     */
    private void calcBaseMoneyWithAcc() throws YssException {
        int digit = 0;
        digit = getDigit(this.dBal); //获取动态设置小数位。
        if (this.dBaseRate == 0.0) { //若基础金额为0，通过账户代码来计算基础金额
            CashAccountBean cashAcc = new CashAccountBean();
            cashAcc.setYssPub(pub);
            cashAcc.setStrCashAcctCode(this.sVariableCode);
            cashAcc.getSetting();
            getRate(this.dRateDate, cashAcc.getStrCurrencyCode(), this.sPortCode);
        }
        this.dBaseMoney = this.getSettingOper().calBaseMoney(this.dBal, this.dBaseRate, digit); ////将获取小数位加入方法中。
    }

    /**
     * 该方法同时获取基础货币金额和组合货币金额
     * 用交易货币计算
     * 20081209   王晓光
     * @param dRateDate Date
     * @param sCuryCode String
     * @param sPortCode String
     * @throws YssException
     */
    private void calcPBMoneyWithCury(Date dRateDate,
                                     String sCuryCode, String sPortCode) throws YssException {
        this.calcPortMoneyWithCury(dRateDate, sCuryCode, sPortCode);
        this.calcBaseMoneyWithCury();
    }

    /**
     * 该方法同时获取基础货币金额和组合货币金额
     * 用现金账户计算
     * @param dRateDate Date
     * @param sCuryCode String
     * @param sPortCode String
     * @throws YssException
     */
    private void calcPBMoneyWithAcc(Date dRateDate, String sCuryCode, String sPortCode) throws YssException {
        // -- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415
        this.calcBaseMoneyWithAcc(); //通过账户来计算基础金额
        //------------------------------------------------------
        this.calcPortMoneyWithAcc(dRateDate, sCuryCode, sPortCode);
    }
   //--- MS00849 QDV4中保2009年12月07日01_A 蒋世超 添加 2009.12.11 ----------------
   private void getAvgRate() throws YssException{
		CashStorageBean cashStg = null;
		double dScale =0.0;
		double accBaseMoney = 0.0;
		double accPortMoney = 0.0;
		try{
		  cashStg = operFun.getCashAccStg(this.dRateDate,this.sVariableCode, this.sPortCode,this.sManagerCode,this.sType,"");
		  if (YssFun.toDouble(cashStg.getStrAccBalance()) != 0) {
                dScale = YssD.div(this.dMoney,YssFun.toDouble(cashStg.getStrAccBalance()));
          }
		 accBaseMoney =  YssD.round(YssD.mul(YssFun.toDouble(cashStg.getStrBaseCuryBal()), dScale),4);
         accPortMoney =  YssD.round(YssD.mul(YssFun.toDouble(cashStg.getStrPortCuryBal()), dScale),4);
         this.dBaseRate  = YssD.round(YssD.div(accBaseMoney, this.dMoney), 15);
         this.dPortRate  = YssD.round(YssD.div(YssD.mul(this.dMoney, this.dBaseRate), accPortMoney), 15);
		} catch (Exception e) {
	         throw new YssException(e.getMessage());
	      }  

   }
 //--- MS00849 QDV4中保2009年12月07日01_A end  -----------------------------------
   
    private void getRate(Date dRateDate, String sCuryCode, String sPortCode) throws
        YssException {
        BaseOperDeal obj = null;
        String PortCury = "";
        try {
            obj = new BaseOperDeal();
            obj.setYssPub(pub);
            this.dBaseRate = obj.getCuryRate(dRateDate, sCuryCode,
                                             sPortCode,
                                             YssOperCons.YSS_RATE_BASE);
        } catch (Exception e) {
            throw new YssException("获取基础汇率信息出错\r\n" + e.getMessage(), e);
        }
        try {
        	//add by lidaolong 2011.02.18  #399 QDV4上海2010年12月10日02_A
        	String strSql ="select FPortCury from "+
               			pub.yssGetTableName("Tb_Para_Portfolio") +
               			" where FPortCode ="+dbl.sqlString(sPortCode)+
               			" and FCuryCode ='"+pub.getPortBaseCury(sPortCode)+"'";
        	ResultSet rs =dbl.openResultSet(strSql);
        	 if (rs.next()){
        		 sCuryCode =rs.getString("FPortCury");
        	 }
        	 dbl.closeResultSetFinal(rs);
        	 //---end lidaolong---
            String sKey = YssFun.formatDate(dRateDate,
                                            "yyyy-MM-dd") + "\f\f" +
                sCuryCode + "\f\f" + sPortCode + "\f\f" +
                YssOperCons.YSS_RATE_MARK;
            if (this.getSettingOper().isDirectWay(sCuryCode, sKey)) {
                PortCury = this.getSettingOper().getDirectPort(dRateDate, "",
                    sCuryCode, "", "", "",
                    "",
                    sPortCode,
                    YssOperCons.YSS_RATE_MARK);
                this.dPortRate = YssFun.toDouble(PortCury.split(YssCons.
                    YSS_PASSAGESPLITMARK)[0]);
            } else {
                this.dPortRate = obj.getCuryRate(dRateDate, sCuryCode,
                                                 sPortCode,
                                                 YssOperCons.YSS_RATE_PORT);
            }
        } catch (Exception e) {
            throw new YssException("获取组合汇率信息出错\r\n" + e.getMessage(), e);
        }
    }

   public String getOperValue(String sType) throws YssException {
      String reStr = "";
      String para = "";
      String sTradeCury="";
      
      CashAccountBean cashAcc = null;
      TradeSubBean tradeBean=null;
      if (sType.equalsIgnoreCase("rate")) {
         getRate(this.dRateDate, this.sVariableCode, this.sPortCode);
      }
      else if (sType.equalsIgnoreCase("accrate")) {
       //---- MS00849 QDV4中保2009年12月07日01_A  蒋世超 添加  2009.12.11 ---------------
    	 CtlPubPara pubPara = new CtlPubPara();
	     pubPara.setYssPub(pub);
    	 para = pubPara.getOutRatePara();
     	 if(para.equalsIgnoreCase("1") && "流出".equalsIgnoreCase(sInOut) ){	   
			   getAvgRate();
			  if(this.dBaseRate == 0 && this.dPortRate == 0){
				 throw new YssException("【"+this.sPortCode+"组合的"+this.sVariableCode+"账户下没有所选品种库存】");
			  }
	      }else{
            cashAcc = new CashAccountBean();
            cashAcc.setYssPub(pub);
            cashAcc.setStrCashAcctCode(this.sVariableCode);
            cashAcc.getSetting();
            /**by guyichuan 20110426 STORY #562 取证券信代码对应的币种--start */
            if(this.sSecurityCode!=null&&this.sSecurityCode.length()!=0){
            		tradeBean=new TradeSubBean();
            		tradeBean.setYssPub(pub);
            		sTradeCury=tradeBean.getCuryCode(this.sSecurityCode);    //--end-- by guyichuan 20110426 STORY #562           
            		getRate(this.dRateDate, sTradeCury, this.sPortCode);     //modified by guyichuan 20110426 STORY #562
            	}else{
           			 getRate(this.dRateDate, cashAcc.getStrCurrencyCode(), this.sPortCode);     
          }       
        }
      }
//      else if (sType.equalsIgnoreCase("calcPortMoneyWithAcc")) {
//          this.calcPortMoneyWithAcc(this.dRateDate,sVariableCode,this.sPortCode);
//      }
//      else if (sType.equalsIgnoreCase("calcPortMoneyWithCury")){
//         this.calcPortMoneyWithCury(this.dRateDate,sVariableCode,this.sPortCode);
//      }
      //一次性从后台将基础货币金额和组合货币金额传到前台    20081209    王晓光
      else if(sType.equalsIgnoreCase("calcPBMoneyWithAcc"))
      {
         this.calcPBMoneyWithAcc(this.dRateDate,this.sVariableCode,this.sPortCode);
      }
      else if(sType.equalsIgnoreCase("calcPBMoneyWithCury")) {
         this.calcPBMoneyWithCury(this.dRateDate,sVariableCode,this.sPortCode);
      }
      reStr = this.buildRowStr();
      return reStr;
   }

    /**
     * 获取原币的小数位。
     * @param bal double
     * @return int
     * @throws YssException
     * MS00215 QDV4中保2009年01月22日01_B 将两位小数调整为4位小数 sj modified 20090124
     */
    private int getDigit(double bal) throws YssException {
        int digit = 0;
        digit = this.getSettingOper().getRoundDigit(bal); //获取输入的金额的小数位
        return digit;
    }

    /**
     * 重载的获取组合利率的方法
     * @param dRateDate Date 日期
     * @param sCuryCode String 交易货币
     * @param sPortCode String 组合
     * @throws YssException
     * MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415
     */
    public void getInnerPortRate(Date dRateDate, String sCuryCode, String sPortCode) throws YssException {
        getInnerPortRate(dRateDate, sCuryCode, sPortCode, "", "", "", "");
    }

    /**
     * 在后台获取汇率的通用方法
     * @param dRateDate Date
     * @param sCuryCode String
     * @param sPortCode String
     * @param sBaseRateSrc_f String 基础来源
     * @param sBaseRateField_f String 基础字段
     * @param sPortRateSrc_f String 组合来源
     * @param sPortRateField_f String 组合字段
     * @throws YssException
     * MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415
     */
    public void getInnerPortRate(Date dRateDate, String sCuryCode, String sPortCode, String sBaseRateSrc_f,
                                 String sBaseRateField_f,
                                 String sPortRateSrc_f,
                                 String sPortRateField_f) throws YssException {
        BaseOperDeal obj = null;
        String PortCury = "";
        try {
            obj = new BaseOperDeal();
            obj.setYssPub(pub);
            String sKey = YssFun.formatDate(dRateDate,
                                            "yyyy-MM-dd") + "\f\f" +
                sCuryCode + "\f\f" + sPortCode + "\f\f" +
                YssOperCons.YSS_RATE_MARK;
            if (this.getSettingOper().isDirectWay(sCuryCode, sKey)) { //如果为直接获取
                PortCury = this.getSettingOper().getDirectPort(dRateDate, "",
                    sCuryCode, sBaseRateSrc_f, sBaseRateField_f, sPortRateSrc_f,
                    sPortRateField_f,
                    sPortCode,
                    YssOperCons.YSS_RATE_MARK);
                this.dPortRate = YssFun.toDouble(PortCury.split(YssCons.
                    YSS_PASSAGESPLITMARK)[0]);
            } else { //间接获取
            	
            	//add by lidaolong 2011.02.18  #399 QDV4上海2010年12月10日02_A
            	//取组合汇率时，本位币取组合下面的币值
            	String strSql ="select FPortCury from "+
                   			pub.yssGetTableName("Tb_Para_Portfolio") +
                   			" where FPortCode ="+dbl.sqlString(sPortCode)+
                   			" and FCuryCode ='"+pub.getPortBaseCury(sPortCode)+"'";
            	ResultSet rs =dbl.openResultSet(strSql);
            	 if (rs.next()){
            		 sCuryCode =rs.getString("FPortCury");
            	 }
            	 dbl.closeResultSetFinal(rs);
            	 //---end lidaolong---
            	
                this.dPortRate = obj.getCuryRate(dRateDate, sBaseRateSrc_f,
                                                 sBaseRateField_f, sPortRateSrc_f,
                                                 sPortRateField_f,
                                                 sCuryCode,
                                                 sPortCode,
                                                 YssOperCons.YSS_RATE_PORT);
            }
        } catch (Exception e) {
            throw new YssException("获取组合汇率信息出错\r\n", e);
        }
    }


   /**
    * 在后台获取汇率的通用方法
    * @param dRateDate Date
    * @param sCuryCode String
    * @param sPortCode String
    * @param sBaseRateSrc_f String 基础来源
    * @param sBaseRateField_f String 基础字段
    * @param sPortRateSrc_f String 组合来源
    * @param sPortRateField_f String 组合字段
    * @throws YssException
    */
   private double getFactValue(double bal,double inBal) throws YssException {
      double outBal = 0;
      int digit = 0;
      digit = getDigit(bal);
      outBal = YssFun.roundIt(inBal,digit);
      return outBal;
   }

}
