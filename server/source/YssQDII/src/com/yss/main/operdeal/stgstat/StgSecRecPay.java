package com.yss.main.operdeal.stgstat;

import java.util.*;
import java.util.Date;
import java.sql.*;

import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.main.storagemanage.SecRecPayBalBean;
import com.yss.util.*;
import com.yss.main.storagemanage.SecurityStorageBean;
import com.yss.manager.SecRecPayStorageAdmin;
import com.yss.manager.SecurityStorageAdmin;
import com.yss.manager.SecRecPayAdmin;
import com.yss.manager.CashPayRecAdmin;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;

public class StgSecRecPay
      extends BaseStgStatDeal {
	  private String sStgNums="";//添加 库存综计时统计编号　 by leeyu 20100417 QDV4中保2010年4月14日02_B MS01092 //更改成统计的编号 合并太平版本代码
   public StgSecRecPay() {
   }
   
   /**
    * 
    * @throws YssException 
 * @方法名：getSecLendEveStg
    * @参数：
    * @返回类型：void
    * @说明：获取前一天的证券借贷证券应收应付库存，并按关键字为key放入hashtable中，
    * add by zhangfa 20101119证券借贷-证券应收应付库存统计
    */
   private void getSecLendEveStg(HashMap hmEveStg,java.util.Date dDate) throws YssException{
	   SecRecPayBalBean secrecpaybal = null;
		boolean analy1;
		boolean analy2;
		boolean analy3;
		ResultSet rs = null;
		String sKey = "";
		String strSql = "";
		  try {
		          analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
		          analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
		          analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
		          strSql = "select a.*," + dbl.sqlDate(dDate) +
	               " as FOperDate from(" +
	               " select FStorageDate, FPortCode, FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FCatType,FAttrClsCode,FSecurityCode,FInvestType" + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
	               (analy1 ? ",FAnalysisCode1" : "") +
	               (analy2 ? ",FAnalysisCode2" : "") +
	               (analy3 ? ",FAnalysisCode3" : "") + "," +
	               dbl.sqlIsNull("FBal", "0") + " as FBal, " +
	               dbl.sqlIsNull("FBaseCuryBal", "0") + " as FBaseCuryBal, " +
	               dbl.sqlIsNull("FPortCuryBal", "0") + " as FPortCuryBal, " +
	               dbl.sqlIsNull("FMBal", "0") + " as FMBal, " +
	               dbl.sqlIsNull("FMBaseCuryBal", "0") + " as FMBaseCuryBal, " +
	               dbl.sqlIsNull("FMPortCuryBal", "0") + " as FMPortCuryBal," +
	               dbl.sqlIsNull("FVBal", "0") + " as FVBal, " +
	               dbl.sqlIsNull("FVBaseCuryBal", "0") + " as FVBaseCuryBal, " +
	               dbl.sqlIsNull("FVPortCuryBal", "0") + " as FVPortCuryBal, " +
	               dbl.sqlIsNull("FBalF", "0") + " AS FBalF," +
	               dbl.sqlIsNull("FBaseCuryBalF", "0") + " AS FBaseCuryBalF," +
	               dbl.sqlIsNull("FPortCuryBalF", "0") + " AS FPortCuryBalF ," +
	               dbl.sqlIsNull("FAmount", "0") + " AS FAmount " +
	               " from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
	               " where FCheckstate=1 and FPortCode in (" + portCodes +")"+
	               /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
	                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//					(statCodes.length()>0?") and FSecurityCode in("+operSql.sqlCodes(statCodes):"")+
	               (statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":"")+
	               /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
	               " and FSubTsfTypeCode in ('10BSC','10BLC', '07MBI', '06MHR') "+//权证数据也不加入应收应付，直接入库存 取消07aw 06aw
	               " and  FAmount <>0"+
	               " and " +operSql.sqlStoragEve(dDate) + ") a";
	         rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
	         while (rs.next()) {
	             secrecpaybal = new SecRecPayBalBean();
	             sKey = rs.getString("FSecurityCode") + "\f" +
	                   rs.getString("FTsfTypeCode") + "\f" +
	                   rs.getString("FSubTsfTypeCode") + "\f" +
	                   rs.getString("FPortCode") +
	                   (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
	                   (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
	                   (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
	                   (rs.getString("FAttrClsCode") == null ||
	                    rs.getString("FAttrClsCode").trim().length() == 0 ? " " :
	                    rs.getString("FAttrClsCode")) + "\f" +  
	 				   rs.getString("FInvestType"); 
 
	             secrecpaybal.setDtStorageDate(rs.getDate("FOperDate"));
	             secrecpaybal.setSPortCode(rs.getString("FPortCode"));
	             secrecpaybal.setSSecurityCode(rs.getString("FSecurityCode"));
	             secrecpaybal.setSAnalysisCode1( (analy1 ?
	                                              rs.getString("FAnalysisCode1") :
	                                              " "));
	             secrecpaybal.setSAnalysisCode2( (analy2 ?
	                                              rs.getString("FAnalysisCode2") :
	                                              " "));
	             secrecpaybal.setSAnalysisCode3( (analy3 ?
	                                              rs.getString("FAnalysisCode3") :
	                                              " "));
	             secrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
	             secrecpaybal.setSSubTsfTypeCode(rs.getString(
	                   "FSubTsfTypeCode"));
	 			secrecpaybal.setSInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
	             secrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
	             secrecpaybal.setDBal(rs.getDouble("FBal"));
	             secrecpaybal.setDBaseBal(rs.getDouble("FBaseCuryBal"));
	             secrecpaybal.setDPortBal(rs.getDouble("FPortCuryBal"));
	             secrecpaybal.setDMBal(rs.getDouble("FMBal"));
	             secrecpaybal.setDMBaseBal(rs.getDouble("FMBaseCuryBal"));
	             secrecpaybal.setDMPortBal(rs.getDouble("FMPortCuryBal"));
	             secrecpaybal.setDVBal(rs.getDouble("FVBal"));
	             secrecpaybal.setDVBaseBal(rs.getDouble("FVBaseCuryBal"));
	             secrecpaybal.setDVPortBal(rs.getDouble("FVPortCuryBal"));
	             secrecpaybal.setAmount(rs.getDouble("FAmount"));

	             secrecpaybal.setBalF(rs.getDouble("FBalF"));
	             secrecpaybal.setBaseBalF(rs.getDouble("FBaseCuryBalF"));
	             secrecpaybal.setPortBalF(rs.getDouble("FPortCuryBalF"));
	             //------------------------------------------------//
	             secrecpaybal.setAttrClsCode((rs.getString("FAttrClsCode")==null || rs.getString("FAttrClsCode").trim().length() == 0)?" ":rs.getString("FAttrClsCode"));
	             hmEveStg.put(sKey, secrecpaybal);
	          }
		  }
	      catch (Exception ex) {
	         throw new YssException("系统进行证券借贷证券应收应付库存统计,在昨日应收应付库存时出现异常!\n", ex); 
	      }
	      finally {
	         dbl.closeResultSetFinal(rs);
	      }   
	   
   }
   /**
    * sj 获取前一天的证券应收应付库存，并按关键字为key放入hashtable中，
    * 以便与后面的今日证券应收应付款相加  edit 20071203
    * @param dDate Date
    * @throws YssException
    * @return HashMap
    */
   private HashMap getEveStg(java.util.Date dDate) throws
         YssException {
      HashMap hmEveStg = new HashMap();
      SecRecPayBalBean secrecpaybal = null;
      boolean analy1;
      boolean analy2;
      boolean analy3;
      ResultSet rs = null;
      String sKey = "";
      String strSql = "";
      try {
         analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
         analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
         analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
         strSql = "select a.*," + dbl.sqlDate(dDate) +
               " as FOperDate from(" +
               " select FStorageDate, FPortCode, FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FCatType,FAttrClsCode,FSecurityCode,FInvestType" + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
               (analy1 ? ",FAnalysisCode1" : "") +
               (analy2 ? ",FAnalysisCode2" : "") +
               (analy3 ? ",FAnalysisCode3" : "") + "," +
               dbl.sqlIsNull("FBal", "0") + " as FBal, " +
               dbl.sqlIsNull("FBaseCuryBal", "0") + " as FBaseCuryBal, " +
               dbl.sqlIsNull("FPortCuryBal", "0") + " as FPortCuryBal, " +
               dbl.sqlIsNull("FMBal", "0") + " as FMBal, " +
               dbl.sqlIsNull("FMBaseCuryBal", "0") + " as FMBaseCuryBal, " +
               dbl.sqlIsNull("FMPortCuryBal", "0") + " as FMPortCuryBal," +
               dbl.sqlIsNull("FVBal", "0") + " as FVBal, " +
               dbl.sqlIsNull("FVBaseCuryBal", "0") + " as FVBaseCuryBal, " +
               dbl.sqlIsNull("FVPortCuryBal", "0") + " as FVPortCuryBal, " +
               //2008.11.14 蒋锦 添加
               //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
               //储存保留8位小数的原币，基础货币，本位币金额,用于计算估值增值汇兑损益
               dbl.sqlIsNull("FBalF", "0") + " AS FBalF," +
               dbl.sqlIsNull("FBaseCuryBalF", "0") + " AS FBaseCuryBalF," +
               dbl.sqlIsNull("FPortCuryBalF", "0") + " AS FPortCuryBalF ," +
               //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
               dbl.sqlIsNull("FAmount", "0") + " AS FAmount " + 
               //------------end-----------------------------------------------------
               
               " from " + pub.yssGetTableName("Tb_Stock_SecRecPay") + 
               " where FCheckstate=1 and FPortCode in (" + portCodes +")"+
               /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//				(statCodes.length()>0?") and FSecurityCode in("+operSql.sqlCodes(statCodes):"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
               (statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":"")+
               /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               " and (fbal <> 0 or fbaseCuryBal <> 0 or fportCuryBal <> 0 or famount <>0 ) and " +
               operSql.sqlStoragEve(dDate) + ") a";
         rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
         while (rs.next()) {
            secrecpaybal = new SecRecPayBalBean();
            sKey = rs.getString("FSecurityCode") + "\f" +
                  rs.getString("FTsfTypeCode") + "\f" +
                  rs.getString("FSubTsfTypeCode") + "\f" +
                  rs.getString("FPortCode") +
                  (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                  (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                  (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                  (rs.getString("FAttrClsCode") == null ||
                   rs.getString("FAttrClsCode").trim().length() == 0 ? " " :
                   rs.getString("FAttrClsCode")) + "\f" +  // 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
				   rs.getString("FInvestType"); //获取投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
//           if (rs.getString("FSecurityCode").equalsIgnoreCase("3368 HK") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("09EQ")){
//              int t = 1;
//           }
            secrecpaybal.setDtStorageDate(rs.getDate("FOperDate"));
            secrecpaybal.setSPortCode(rs.getString("FPortCode"));
            secrecpaybal.setSSecurityCode(rs.getString("FSecurityCode"));
            secrecpaybal.setSAnalysisCode1( (analy1 ?
                                             rs.getString("FAnalysisCode1") :
                                             " "));
            secrecpaybal.setSAnalysisCode2( (analy2 ?
                                             rs.getString("FAnalysisCode2") :
                                             " "));
            secrecpaybal.setSAnalysisCode3( (analy3 ?
                                             rs.getString("FAnalysisCode3") :
                                             " "));
            secrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
            secrecpaybal.setSSubTsfTypeCode(rs.getString(
                  "FSubTsfTypeCode"));
			secrecpaybal.setSInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
            secrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
            secrecpaybal.setDBal(rs.getDouble("FBal"));
            secrecpaybal.setDBaseBal(rs.getDouble("FBaseCuryBal"));
            secrecpaybal.setDPortBal(rs.getDouble("FPortCuryBal"));
            secrecpaybal.setDMBal(rs.getDouble("FMBal"));
            secrecpaybal.setDMBaseBal(rs.getDouble("FMBaseCuryBal"));
            secrecpaybal.setDMPortBal(rs.getDouble("FMPortCuryBal"));
            secrecpaybal.setDVBal(rs.getDouble("FVBal"));
            secrecpaybal.setDVBaseBal(rs.getDouble("FVBaseCuryBal"));
            secrecpaybal.setDVPortBal(rs.getDouble("FVPortCuryBal"));
            secrecpaybal.setAmount(rs.getDouble("FAmount"));
            //------------2008.11.14 蒋锦 添加-----------------//
            //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
            //储存保留8位小数的原币，基础货币，本位币金额,用于计算估值增值汇兑损益
            secrecpaybal.setBalF(rs.getDouble("FBalF"));
            secrecpaybal.setBaseBalF(rs.getDouble("FBaseCuryBalF"));
            secrecpaybal.setPortBalF(rs.getDouble("FPortCuryBalF"));
            //------------------------------------------------//
            secrecpaybal.setAttrClsCode((rs.getString("FAttrClsCode")==null || rs.getString("FAttrClsCode").trim().length() == 0)?" ":rs.getString("FAttrClsCode"));
            hmEveStg.put(sKey, secrecpaybal);
         }
      }
      catch (Exception ex) {
         throw new YssException("系统进行证券应收应付库存统计,在昨日应收应付库存时出现异常!\n", ex);// by 曹丞 2009.01.22 统计昨日应收应付库存异常信息 MS00004 QDV4.1-2009.2.1_09A
      }
      finally {
    	  //edit by songjie 2011.04.27 资产估值报游标超出最大数错误
         dbl.closeResultSetFinal(rs);//edit by songjie 2011.07.08 BUG 2179 QDV4工银2011年06月28日01_B
      }

      return hmEveStg;
   }

   public ArrayList getStorageStatData(java.util.Date dDate) throws
         YssException {
      ArrayList all = new ArrayList();
      ResultSet rs = null;

      boolean analy1 = false;
      boolean analy2 = false;
      boolean analy3 = false;

      HashMap hmEveStg = null;

      Iterator iter = null;
      String sKey = "";

      SecRecPayBalBean secRecstorage = null;
		// --- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 ---------------------------
		CtlPubPara pubPara = null;
		boolean bTPCost = false;// 区分是太平资产的库存统计还是QDII的库存统计,合并版本时调整
		String sPara_tp = "";
		try {
			pubPara = new CtlPubPara();
			pubPara.setYssPub(pub);
			sPara_tp = pubPara.getNavType();// 通过净值表类型来判断
			if (sPara_tp != null && sPara_tp.trim().equalsIgnoreCase("new")) {
				bTPCost = false;// 国内QDII统计模式
			} else {
				bTPCost = true;// 太平资产统计模式
			}
			if (bYearChange) {
				if (bTPCost) {
					// 太平资产年终结账方式：根据09、14、18报表产生虚拟账户的期初数和证券的特殊处理
					yearChange_TP(dDate, portCodes);
				} else {
					// QDII年终结账方式
					yearChange(dDate, portCodes);
				}
			}
		// --- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 end------------------------
			
         hmEveStg = getEveStg(dDate);//查找昨日证券应收应付
         DeleteSecLPayRecDate(dDate);
       //add by zhangfa 20101119证券借贷-证券应收应付库存统计
         getSecLendEveStg(hmEveStg,dDate);
         //-----------------end------------------------------
         analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
         analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
         analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

         getStorageRecData(dDate, hmEveStg, analy1, analy2, analy3);//产生今日的应收应付
         getStgTradeSecLendStatData(dDate, hmEveStg, analy1, analy2, analy3,this.portCodes);//冲减前日的应收应付
         getStgTradeSubRelaData(dDate, hmEveStg, analy1, analy2, analy3);
         iter = hmEveStg.keySet().iterator();
         while (iter.hasNext()) {
            sKey = (String) iter.next();
            secRecstorage = (SecRecPayBalBean) hmEveStg.get(sKey);
            //------ add by wangzuochun 2010.12.15 BUG #639 系统处理债券计息时并未冲减买入利息，导致最终统计债券应收利息金额计算多 
			//setBuyInterestMoney(dDate,secRecstorage); //modify huangqirong 2013-01-24 bug #6965  处理含息卖出的利息不能统计到证券库存中的应收应付信息里 之前太平不影响qd
			//----------------- BUG #639 ---------------//
			
            secRecstorage.setIStorageState(0); //自动计算（未锁定）
            secRecstorage.checkStateId = 1;
            all.add(secRecstorage);
         }
         return all;
      }
      catch (Exception e) {
         throw new YssException(e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

  /**
    * 
    * @throws YssException 
 * @方法名：getStgTradeSecLendStatData
    * @参数：
    * @返回类型：void
    * @说明：冲减借入股票成本， 应付送股，应付配股权证，应收送股，应收配股权证 --证券借贷业务-库存统计2010.12.3
    */
   public void getStgTradeSecLendStatData(java.util.Date dDate, HashMap hmEveStg, boolean bAnaly1, boolean bAnaly2,  boolean bAnaly3,String portCodes) throws YssException{
	   SecRecPayBalBean secrecpaybal = null;
	   String strSql = "";
	   ResultSet rs = null;
	   String sKey = "";
	   String sKey1="";
	   String sKey2="";
	   String sKey3="";
	   //add by zhouxiang 2010.11.29
	   String sDKey = "";//用来传递原币成本类型的KEY值以便传递到估值增值的方法中去
	   String sDKey1="";
	   String sDKey2="";
	   String sDKey3="";
	   SecRecPayBalBean secRecstorage=new SecRecPayBalBean();
	   //end by zhouxiang 2010.11.29
	   String sTsfTypeCode="";
	   String sSubTsfTypeCode="";
	   double dBaseRate = 1;
	   double dPortRate = 1;
	   boolean analy1;
	   boolean analy2;
	   boolean analy3;
	   try{
		   analy1 = bAnaly1;
	       analy2 = bAnaly2;
	       analy3 = bAnaly3;
			strSql = " select a.*, b.FPortCury, c.FTradeCury  from (select a.FSecurityCode, FPortCode,a.FInvMgrCode as FAnalysisCode1,a.FBrokerCode as FAnalysisCode2,FAttrClsCode,FTradeTypeCode,"
					+ " Sum(NVL(a.FTradeAmount *(-1), 0)) as FTradeAmount, "				//add by zhouxiang 2010.11.26 证券借贷--冲减应收应付库存
					+ " sum(NVL(a.FCost *(-1), 0)) as FMoney,"
					+ " sum(NVL(a.FMCost *(-1), 0)) as FMMoney,"
					+ " sum(NVL(a.FVCost *(-1), 0)) as FVMoney,"
					+ "	sum(NVL(a.FBaseCuryCost *(-1), 0)) as FBaseCuryMoney,"
					+ " sum(NVL(a.FMBaseCuryCost *(-1), 0)) as FMBaseCuryMoney,"
					+ " sum(NVL(a.FVBaseCuryCost *(-1), 0)) as FVBaseCuryMoney,"
					+ " sum(NVL(a.FPortCuryCost *(-1), 0)) as FPortCuryMoney,"
					+ " sum(NVL(a.FMPortCuryCost *(-1), 0)) as FMPortCuryMoney,"
					+ " sum(NVL(a.FVPortCuryCost *(-1), 0)) as FVPortCuryMoney,"
					+ " sum(NVL(a.FTotalCost *(-1), 0)) as FTotalCost,"
					+ " 'Natural' as FTradeState, 'C' as FInvestType"
					+ " from (select a1.*, a2.FCashInd, a2.FAmountInd, sec.fcatcode  from "
					+ pub.yssGetTableName("tb_DATA_SecLendTRADE")
					+ "  a1"
					+ " join (select * from Tb_Base_TradeType where FCheckState = 1) a2 on a1.FTradeTypeCode =a2.FTradeTypeCode"
					+ " join (select FSecurityCode, FCatCode from "
					+ pub.yssGetTableName("tb_para_security")
					+ "  where FCheckState = 1) sec on a1.FSecurityCode =sec.FSecurityCode"
					+ " where a1.fbargaindate ="
					+ dbl.sqlDate(dDate)
					+ "  and a1.FTradeTypeCode in ("
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rcb) // -----冲减的类型包括借入成本（10BSC）：（使用借入归还 Rcb 借入归还送股 Rbsb交易数据冲减）
					+ ","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rbsb)
					/*+ ","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Awrb)// ------应付借入配股权证（07AW）：使用借入归还配股权证Awrb冲减
*/					+ ","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Mhlr)// -----借出成本（10BLC）：（借出召回 Lr	 借出召回送股 Mhlr冲减）
					+","
					+dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Lr)
				/*	+ ","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Lpwr)// -----应收配股权证（06AW）：（借出召回配股权证 Lpwr冲减）
*/					+ ")"
					+ " and a1.fcheckstate = 1 and FPortCode in (" 
					+ operSql.sqlCodes(portCodes) 
					+ " )) a "
					+ " group by a.FSecurityCode,a.FPortCode,a.FAttrClsCode,a.FInvMgrCode,a.FBrokerCode,FTradeTypeCode  ) a"
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					+ " left join (select FPortCode, FPortName, FPortCury from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1 ) b on a.FPortCode =b.FPortCode"
				
					//end by lidaolong
					+ " left join (select FSecurityCode, FTradeCury from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode"
					//add by zhouxiang 2010.11.26 证券借贷冲减借贷利息 ---------应付借贷利息(07PLI)   03LE  证券借贷费用        应收借贷利息(06RLI) 02LE证券借贷收入
					+ " union all select a.*, b.FPortCury, c.FTradeCury from (select Fsecuritycode,fportcode,FAnalysisCode1,FAnalysisCode2,"
					+ " FAttrClsCode,FSubTsfTypeCode,0 as FTradeAmount,sum(NVL(a.FMoney * (-1), 0)) as FMoney,"
					+ " sum(NVL(a.FMMoney * (-1), 0)) as FMMoney, sum(NVL(a.FVMoney * (-1), 0)) as FVMoney,sum(NVL(a.FBaseCuryMoney * (-1), 0)) as FBaseCuryMoney,"
					+ " sum(NVL(a.FMBaseCuryMoney * (-1), 0)) as FMBaseCuryMoney,sum(NVL(a.FVBaseCuryMoney * (-1), 0)) as FVBaseCuryMoney, sum(NVL(a.FPortCuryMoney * (-1), 0)) as FPortCuryMoney,"
					+ " sum(NVL(a.FMPortCuryMoney * (-1), 0)) as FMPortCuryMoney,sum(NVL(a.FVPortCuryMoney * (-1), 0)) as FVPortCuryMoney,"
					+ " 0 as FTotalCost,'Natural' as FTradeState,'C' as FInvestType from "
					+ pub.yssGetTableName("tb_data_secrecpay")
					+ " a where fTransdate = "
					+ dbl.sqlDate(dDate)
					+ " and fcheckstate = 1  and FSubTsfTypeCode in ("
					+ dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_Income)
					+ ","
					+ dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_Fee)
					+ ")"// 03LE 证券借贷收入 02LE 证券借贷费用
					+ " group by Fsecuritycode,fportcode,FAnalysisCode1,FAnalysisCode2,FAttrClsCode,FSubTsfTypeCode) a"
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					
			
					+ " left join (select FPortCode, FPortName, FPortCury  from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where  FCheckState = 1 ) b on a.FPortCode =b.FPortCode  left join (select FSecurityCode, FTradeCury from "
					
					//end by lidaolong
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode";
			rs=dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		   while (rs.next()){
			   sKey1=sKey = rs.getString("FSecurityCode") + "\f";
			   if(rs.getString("FTradeTypeCode").equals(YssOperCons.YSS_SECLEND_JYLX_Rcb)||rs.getString("FTradeTypeCode").equals(YssOperCons.YSS_SECLEND_JYLX_Rbsb)){//冲减借入成本判断
				   sKey2="10"+ "\f"+"10BSC"+"\f";
				   sTsfTypeCode="10";
				   sSubTsfTypeCode="10BSC";
			   }else if(rs.getString("FTradeTypeCode").equals("Awrb")){													//冲减应付借入权证
				   sKey2="07"+ "\f"+"07AW"+"\f";
				   sTsfTypeCode="07";
				   sSubTsfTypeCode="07AW";
			   }else if(rs.getString("FTradeTypeCode").equals(YssOperCons.YSS_SECLEND_JYLX_Mhlr)||rs.getString("FTradeTypeCode").equals(YssOperCons.YSS_SECLEND_JYLX_Lr)){	//冲减借入找回
				   sKey2="10"+ "\f"+"10BLC"+"\f";
				   sTsfTypeCode="10";
				   sSubTsfTypeCode="10BLC";
			   }else if(rs.getString("FTradeTypeCode").equals(YssOperCons.YSS_SECLEND_JYLX_Lpwr)){						//冲减应收配股权证
				   sKey2="06"+ "\f"+"06AW"+"\f";
				   sTsfTypeCode="06";
				   sSubTsfTypeCode="06AW";
			   }else if(rs.getString("FTradeTypeCode").equals(YssOperCons.Yss_ZJDBZLX_SEC_Fee)){						//应付借贷利息(07PLI)   03LE  证券借贷费用     (取自应收应付界面的数据)
				   sKey2="07"+ "\f"+"07PLI"+"\f";
				   sTsfTypeCode="07";
				   sSubTsfTypeCode="07PLI";
			   }else if(rs.getString("FTradeTypeCode").equals(YssOperCons.Yss_ZJDBZLX_SEC_Income)){						//应收借贷利息(06RLI)    02LE证券借贷收入
				   sKey2="06"+ "\f"+"06RLI"+"\f";
				   sTsfTypeCode="06";
				   sSubTsfTypeCode="06RLI";
			   }
			   sKey3= rs.getString("FPortCode") +
               (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
               (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
               (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
               (rs.getString("FAttrClsCode") == null ||
                rs.getString("FAttrClsCode").trim().length() == 0 ? " " :
                rs.getString("FAttrClsCode"))+ "\f" + 
				   rs.getString("FInvestType"); 
			 
			   sKey=sKey1+sKey2+sKey3;
			   if(hmEveStg.containsKey(sKey)){
				   secrecpaybal = (SecRecPayBalBean) hmEveStg.get(sKey);
				   secrecpaybal.setAmount(YssD.add(rs.getDouble("FTradeAmount"), secrecpaybal.getAmount()));
				   secrecpaybal.setDBal(
		                     YssD.add(rs.getDouble("FMoney"), secrecpaybal.getDBal()));
		               secrecpaybal.setDMBal(
		                     YssD.add(rs.getDouble("FMMoney"), secrecpaybal.getDMBal()));
		               secrecpaybal.setDVBal(
		                     YssD.add(rs.getDouble("FVMoney"), secrecpaybal.getDVBal()));

		               secrecpaybal.setDBaseBal(
		                     YssD.add(rs.getDouble("FBaseCuryMoney"),
		                              secrecpaybal.getDBaseBal()));
		               secrecpaybal.setDMBaseBal(
		                     YssD.add(rs.getDouble("FMBaseCuryMoney"),
		                              secrecpaybal.getDMBaseBal()));
		               secrecpaybal.setDVBaseBal(
		                     YssD.add(rs.getDouble("FVBaseCuryMoney"),
		                              secrecpaybal.getDVBaseBal()));

		               secrecpaybal.setDPortBal(
		                     YssD.add(rs.getDouble("FPortCuryMoney"),
		                              secrecpaybal.getDPortBal()));
		               secrecpaybal.setDMPortBal(
		                     YssD.add(rs.getDouble("FMPortCuryMoney"),
		                              secrecpaybal.getDMPortBal()));
		               secrecpaybal.setDVPortBal(
		                     YssD.add(rs.getDouble("FVPortCuryMoney"),
		                              secrecpaybal.getDVPortBal()));
				   secrecpaybal.setAttrClsCode((rs.getString("FAttrClsCode")==null || rs.getString("FAttrClsCode").trim().length() == 0)?" ":rs.getString("FAttrClsCode"));//这里加上属性代码,也是主键之一,by leeyu BUG:0000437
	               hmEveStg.put(sKey, secrecpaybal);
	              /* if(sSubTsfTypeCode.equals("10BLC")){							//如果是成本则需要计算估值增值
	            	   sDKey = sKey;
	            	   sDKey1=sKey1;
	            	   sDKey2=sKey2;
	            	   sDKey3=sKey3;
	            	}*/
	            }else {
	               secrecpaybal = new SecRecPayBalBean();
	               secrecpaybal.setDtStorageDate(dDate);
	               secrecpaybal.setSPortCode(rs.getString("FPortCode"));
	               secrecpaybal.setSSecurityCode(rs.getString("FSecurityCode"));
	               
	               secrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
	                     "FAnalysisCode1") : " "));
	               secrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
	                     "FAnalysisCode2") : " "));
	               secrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
	                     "FAnalysisCode3") : " "));
	                     
	               secrecpaybal.setSTsfTypeCode(sTsfTypeCode);
	               secrecpaybal.setSSubTsfTypeCode(sSubTsfTypeCode);
				   secrecpaybal.setSInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
	               secrecpaybal.setSCuryCode(rs.getString("FTradeCury"));
	               
	               secrecpaybal.setDBal(
	                     YssD.add(rs.getDouble("FMoney"), secrecpaybal.getDBal()));
	               secrecpaybal.setDMBal(
	                     YssD.add(rs.getDouble("FMMoney"), secrecpaybal.getDMBal()));
	               secrecpaybal.setDVBal(
	                     YssD.add(rs.getDouble("FVMoney"), secrecpaybal.getDVBal()));

	               secrecpaybal.setDBaseBal(
	                     YssD.add(rs.getDouble("FBaseCuryMoney"),
	                              secrecpaybal.getDBaseBal()));
	               secrecpaybal.setDMBaseBal(
	                     YssD.add(rs.getDouble("FMBaseCuryMoney"),
	                              secrecpaybal.getDMBaseBal()));
	               secrecpaybal.setDVBaseBal(
	                     YssD.add(rs.getDouble("FVBaseCuryMoney"),
	                              secrecpaybal.getDVBaseBal()));

	               secrecpaybal.setDPortBal(
	                     YssD.add(rs.getDouble("FPortCuryMoney"),
	                              secrecpaybal.getDPortBal()));
	               secrecpaybal.setDMPortBal(
	                     YssD.add(rs.getDouble("FMPortCuryMoney"),
	                              secrecpaybal.getDMPortBal()));
	               secrecpaybal.setDVPortBal(
	                     YssD.add(rs.getDouble("FVPortCuryMoney"),
	                              secrecpaybal.getDVPortBal()));
	               /**
	               //-----------2008.11.13 蒋锦 添加-------------//
	               //储存保留8位小数的原币，基础货币，本位币金额
	               //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
	               secrecpaybal.setBalF(YssD.add(rs.getDouble("FMoneyF"), secrecpaybal.getBalF()));
	               secrecpaybal.setBaseBalF(YssD.add(rs.getDouble("FBaseCuryMoneyF"), secrecpaybal.getBaseBalF()));
	               secrecpaybal.setPortBalF(YssD.add(rs.getDouble("FPortCuryMoneyF"), secrecpaybal.getPortBalF()));
	               //-------------------------------------------//
	                */
	              secrecpaybal.setAttrClsCode((rs.getString("FAttrClsCode")==null || rs.getString("FAttrClsCode").trim().length() == 0)?" ":rs.getString("FAttrClsCode"));
	               hmEveStg.put(sKey, secrecpaybal);
	             /*  if(sSubTsfTypeCode.equals("10BLC")){							//如果是成本则需要计算估值增值
	            	   sDKey = sKey;
	            	   sDKey1=sKey1;
	            	   sDKey2=sKey2;
	            	   sDKey3=sKey3;
	               }*/
	            }
			   	/*if(sDKey3.length()>0){
			   		if(hmEveStg.containsKey(sDKey1+"60"+ "\f"+"60BI01"+sDKey3)){//如果存在昨日的估值增值则产生估值增值余额，
			   		};
			   		getValueAddBorrowLend(hmEveStg,sDKey1,sDKey2,sDKey3,dDate);//借入成本估值增值
			   	}*/
			   
		   }
		   HashMap tempMap=new HashMap();
		   tempMap=(HashMap) hmEveStg.clone();
		   Iterator iter = tempMap.keySet().iterator();
		   SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
		   secPayAdmin.setYssPub(pub);
		   ArrayList SecList=new ArrayList();
	       while (iter.hasNext()) {
	       sKey = (String) iter.next();
	            secRecstorage = (SecRecPayBalBean) tempMap.get(sKey);
	            if(secRecstorage.getSSubTsfTypeCode().equals("10BSC")){//冲减以后存在成本就计算估值增值
	            	getValueAddBorrowLend(hmEveStg,secRecstorage.getSSecurityCode()+ "\f","10"+ "\f"+"10BSC"+"\f",secRecstorage.getSPortCode()+                        
	            			(analy1 ? "\f" + secRecstorage.getSAnalysisCode1() : "") +
	                        (analy2 ? "\f" + secRecstorage.getSAnalysisCode2() : "") +
	                        (analy3 ? "\f" + secRecstorage.getSAnalysisCode3() : "") + "\f" +
	                        (secRecstorage.getAttrClsCode() == null ||
	                         secRecstorage.getAttrClsCode().trim().length() == 0 ? " " :
	                        	 secRecstorage.getAttrClsCode())+ "\f" + 
	                        	 secRecstorage.getSInvestType(),dDate);//借入成本估值增值
	            	
	            	SecRecPayBalBean  secValueAdd=new SecRecPayBalBean();
	            	secValueAdd=(SecRecPayBalBean) hmEveStg.get(secRecstorage.getSSecurityCode()+ "\f"+"60"+ "\f"+"60BI01"+"\f"+secRecstorage.getSPortCode()+                        
	            			(analy1 ? "\f" + secRecstorage.getSAnalysisCode1() : "") +
	                        (analy2 ? "\f" + secRecstorage.getSAnalysisCode2() : "") +
	                        (analy3 ? "\f" + secRecstorage.getSAnalysisCode3() : "") + "\f" +
	                        (secRecstorage.getAttrClsCode() == null ||
	                         secRecstorage.getAttrClsCode().trim().length() == 0 ? " " :
	                        	 secRecstorage.getAttrClsCode())+ "\f" + 
	                        	 secRecstorage.getSInvestType());
	            //	getYesteryayAmountVal(bAnaly1,bAnaly2,bAnaly3,secRecstorage,dDate,secValueAdd,SecList);							   //计算借入成本估值增值余额
	            	//modified by zhaoxianlin 20121117 #story 3208
	            }
	       }
	       secPayAdmin.addList(SecList);
	       secPayAdmin.insert(dDate, dDate, YssOperCons.YSS_SECLEND_DBLX_BEAV,
                   "60BI01",
                   secRecstorage.getSPortCode(), -99);
		   
	   } catch (Exception e) {
	         throw new YssException("系统进行证券应收应付库存统计,冲减借入股票成本,应付送股,应付配股权证,应收送股,应收配股权证数据时出现异常!\n",e);// by 曹丞2009.01.22 统计当日证券应收应付信息异常 MS00004 QDV4.1-2009.2.1_09A
	      }
	      finally {
	         dbl.closeResultSetFinal(rs);
	      }
	   
   }
   /**
    * @param secRecstorage 证券代码
    * @param dDate 		        日期
 * @param secValueAdd add by zhouxiang 2010.12.3 产生借入估增余额的证券应收应付借入时：今日估增-昨日估增  归还时=借入估增-昨日估增+归还数量*（昨日估增金额/昨日借入成本数）
 * @throws YssException 
    */
   private void getYesteryayAmountVal(boolean bAnaly1, boolean bAnaly2,  boolean bAnaly3,SecRecPayBalBean secRecstorage, Date dDate, SecRecPayBalBean secValueAdd,ArrayList list) throws YssException {
	ResultSet rs=null;
	String strSql="";
	double dBaseRate=0;
    double dPortRate=0;
    double dCost=0;
    double dBaseCost=0;
    double dPortCost=0;
    double dPirce=0;
    try{
    	strSql="select a.fcashacccode, a.fportcode, a.ftradetypecode,b.fcurycode,a.finvmgrcode,a.ftradeamount from "+pub.yssGetTableName("tb_data_seclendtrade")
    		+" a left join (select distinct a1.fcashacccode, a1.fcurycode from "+pub.yssGetTableName("tb_para_cashaccount")
    	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    /*		+" a1 where a1.fcheckstate = 1 and a1.fstartdate <= "+dbl.sqlDate(dDate)*/
    				+" a1 where a1.fcheckstate = 1 "
    		//end by lidaolong
    		+") b on a.fcashacccode = b.fcashacccode where a.fbargaindate = "+dbl.sqlDate(dDate)
    		+" and a.fsecuritycode = "+dbl.sqlString(secRecstorage.getSSecurityCode())
    		+" and a.ftradetypecode in ('borrow', 'Rcb')";
    	rs=dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
    	dCost=YssD.sub(secValueAdd.getDBal(),getTodaySubYesterday(secRecstorage.getSSecurityCode(),dDate));
    	if(rs.next()){//必须当天有借入归还业务的时候才产生加借入归还*昨日平均借入成本
    		if(rs.getString("ftradetypecode").equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_Rcb)){
    			dPirce=getYesterdayBInCost(secRecstorage.getSSecurityCode(),dDate,0);//0为原币，1为基础货币，2为组合货币
    			dCost=YssD.add(dCost,YssD.mul(rs.getDouble("ftradeamount"),dPirce));
    		}
    	}
    	 SecPecPayBean secpecpay = new SecPecPayBean();
         secpecpay.setTransDate(dDate);
         secpecpay.setStrPortCode(secRecstorage.getSPortCode());
         secpecpay.setInvMgrCode(secRecstorage.getSAnalysisCode1());
         secpecpay.setBrokerCode(secRecstorage.getSAnalysisCode2());
         secpecpay.setStrSecurityCode(secRecstorage.getSSecurityCode());
   	  	 secpecpay.setInvestType(secRecstorage.getSInvestType()); 
   	  	 secpecpay.setStrCuryCode(secRecstorage.getSCuryCode());
   	  	 dBaseRate=this.getSettingOper().getCuryRate(dDate, secRecstorage.getSCuryCode(), secRecstorage.getSPortCode(), YssOperCons.YSS_RATE_BASE);
   	  	 dPortRate=this.getSettingOper().getCuryRate(dDate, secRecstorage.getSCuryCode(), secRecstorage.getSPortCode(), YssOperCons.YSS_RATE_PORT);
   	  	 dCost=YssD.round(dCost, 2);
   	  	 
   	  	 secpecpay.setMoney(dCost);
         secpecpay.setMMoney(dCost);
         secpecpay.setVMoney(dCost);
         dBaseCost=this.getSettingOper().calBaseMoney(dCost, dBaseRate);
         dPortCost=this.getSettingOper().calPortMoney(dCost, dBaseRate, dPortRate, secRecstorage.getSCuryCode(), dDate, secRecstorage.getSPortCode());
         
         secpecpay.setBaseCuryMoney(dBaseCost);
         secpecpay.setMBaseCuryMoney(dBaseCost);
         secpecpay.setVBaseCuryMoney(dBaseCost);
         
         secpecpay.setPortCuryMoney(dPortCost);
         secpecpay.setMPortCuryMoney(dPortCost);
         secpecpay.setVPortCuryMoney(dPortCost);
        
         secpecpay.setStrTsfTypeCode(YssOperCons.YSS_SECLEND_DBLX_BEAV); //估值增值
         secpecpay.setStrSubTsfTypeCode("60BI01");//借入股票估增发生额
        
         secpecpay.setBaseCuryRate(dBaseRate);
         secpecpay.setPortCuryRate(dPortRate);
         secpecpay.checkStateId = 1;
         list.add(secpecpay);         
    }catch(Exception e){
    	throw new YssException("借入股票估增余额计算失败");
    }finally{
    	dbl.closeResultSetFinal(rs);
    }
	
}
//昨日借入估增的值---------
private double getTodaySubYesterday(String sSecurityCode, Date dDate) throws YssException {
	ResultSet rs=null;
	String strSql="";
	double dTotal=0;
	try{
		strSql = "select a.fbal as todaybal,b.yesdaybal from "+pub.yssGetTableName("tb_stock_secrecpay")
				+" a left join (select a1.fbal as yesdaybal,a1.fsecuritycode, a1.fanalysiscode2 from "+pub.yssGetTableName("tb_stock_secrecpay")
				+" a1 where a1.fstoragedate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))
				+" and a1.fsecuritycode ="+dbl.sqlString(sSecurityCode)
				+" and a1.fsubtsftypecode = '60BI01') b on a.fsecuritycode = b.fsecuritycode and a.fanalysiscode2 = b.fanalysiscode2"
				+" where a.fstoragedate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))
				+" and a.fsecuritycode = "+dbl.sqlString(sSecurityCode)
				+" and a.fsubtsftypecode = '60BI01'";
		rs=dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		if(rs.next()){
			dTotal=rs.getDouble("yesdaybal");
		}
	}catch(Exception e){
    	throw new YssException("获取今日估增减昨日估增报错");
    }finally{
    	dbl.closeResultSetFinal(rs);
    }
	return dTotal;
}
//公用方法，获取昨日借入平均成本
public double getYesterdayBInCost(String sSecurityCode, Date dDate,
		 int yssRateBase) throws YssException {
	ResultSet rs=null;
	String strSql="";
	double dPirce=0;
	try{
		strSql = "select (case when c.famount=0 then 0 else (a.fbal / c.famount) end  ) as price,(case when c.famount=0 then 0 else(a.fbasecurybal / c.famount)end ) as basepirce,"
			+"(case when c.famount=0 then 0 else(a.fportcurybal / c.famount)end ) as portpirce"
			+ " from "
			+ pub.yssGetTableName("tb_stock_secrecpay")
			+" a left join (select b.famount,b.fstoragedate,b.fsecuritycode from "+pub.yssGetTableName("tb_stock_secrecpay")
			+" b  where b.fsecuritycode ="+dbl.sqlString(sSecurityCode)
			+" and b.fsubtsftypecode = '10BSC' and b.fstoragedate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))
			+") c on a.fsecuritycode =  c.fsecuritycode and a.fstoragedate = c.fstoragedate"
			+ "  where a.fstoragedate = "
			+ dbl.sqlDate(YssFun.addDay(dDate, -1))
			+ " and a.fsubtsftypecode = '60BI01' and a.fsecuritycode ="
			+ dbl.sqlString(sSecurityCode);
		rs=dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		if(rs.next()){
			if(yssRateBase==0){
				dPirce=rs.getDouble("price");
			}
		}
	}catch(Exception e){
    	throw new YssException("获取昨日借入平均成本出错");
    }finally{
    	dbl.closeResultSetFinal(rs);
    }
	return dPirce;
}



/**
    * *
    * @param hmEveStg
    * @param sKey1 借入估增的证券代码 sKey2,用来组建KEY查找类型为（10BLC）成本的数量和成本金额用来计算 计算公式：估值增值=借入数量*股市行情表金额
    * @param sKey3  借入估增组合代码 ，分析代码（券商代码，投资经理，所属分类，投资类型）
    * @param dDate 日期用来查询估值行情表中最近一日的行情 add by zhouxiang 2010.12.3
 * @throws YssException 
     */
   private void getValueAddBorrowLend(HashMap hmEveStg, String sKey1,String sKey2, String sKey3,java.util.Date dDate) throws YssException {
	   String sKey=sKey1+sKey2+sKey3;
	   String sVDKey=sKey1+"60"+ "\f"+"60BI01"+"\f"+sKey3;
	   SecRecPayBalBean secrecpaybal1 = new SecRecPayBalBean();	//应收应付库存余额--估值增值
	   SecRecPayBalBean secrecpaybal0=null;						//应收应付库存余额--成本
	   secrecpaybal0 = (SecRecPayBalBean)hmEveStg.get(sKey);
	   secrecpaybal1.setAmount(0);								//估增数值为0
	   String sSecurityCode=sKey1.split("\f")[0];				//证券代码
	   String sCurycode=secrecpaybal0.getSCuryCode();
	   String sPortcode=secrecpaybal0.getSPortCode();
	   secrecpaybal1.setDtStorageDate(dDate);
	   
	   double dPrice=getValMktPriceBySecAndPort(sPortcode,sSecurityCode,dDate);//获取对应证券代码的估值行情
	   double dValueAddForBL=YssD.sub(YssD.mul(dPrice, secrecpaybal0.getAmount()), secrecpaybal0.getDBal()); //证券借贷借入估值增值(原币)=借入数量*估值行情-借入成本
	   
       secrecpaybal1.setSPortCode(sPortcode);
       secrecpaybal1.setSSecurityCode(sSecurityCode);
       
       secrecpaybal1.setSAnalysisCode1(secrecpaybal0.getSAnalysisCode1());
       secrecpaybal1.setSAnalysisCode2(secrecpaybal0.getSAnalysisCode2());
       secrecpaybal1.setSAnalysisCode3(secrecpaybal0.getSAnalysisCode3());
             
       secrecpaybal1.setSTsfTypeCode("60");						//应收应付库存类型----估值增值
       secrecpaybal1.setSSubTsfTypeCode("60BI01");
	   secrecpaybal1.setSInvestType(secrecpaybal0.getSInvestType());
	   secrecpaybal1.setSCuryCode(sCurycode);
	   
	   double dBaseRate=this.getSettingOper().getCuryRate(dDate, sCurycode,sPortcode, YssOperCons.YSS_RATE_BASE);
	   double dPortRate=this.getSettingOper().getCuryRate(dDate, sCurycode, sPortcode, YssOperCons.YSS_RATE_PORT);
	   double dBaseMoney=this.getSettingOper().calBaseMoney(dValueAddForBL, dBaseRate);
	   double dPortMoney=this.getSettingOper().calPortMoney(dValueAddForBL, dBaseRate, dPortRate, sCurycode, dDate, sPortcode);
	   //原币
       secrecpaybal1.setDBal(YssD.round(dValueAddForBL,2));									
       secrecpaybal1.setDMBal(YssD.round(dValueAddForBL,2));
       secrecpaybal1.setDVBal(YssD.round(dValueAddForBL,2));
       //基础货币
       secrecpaybal1.setDBaseBal(dBaseMoney);
       secrecpaybal1.setDMBaseBal(dBaseMoney);
       secrecpaybal1.setDVBaseBal(dBaseMoney);
       //组合货币
       secrecpaybal1.setDPortBal(dPortMoney);
       secrecpaybal1.setDMPortBal(dPortMoney);
       secrecpaybal1.setDVPortBal(dPortMoney);
       /**
       //-----------2008.11.13 蒋锦 添加-------------//
       //储存保留8位小数的原币，基础货币，本位币金额
       //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
       secrecpaybal.setBalF(YssD.add(rs.getDouble("FMoneyF"), secrecpaybal.getBalF()));
       secrecpaybal.setBaseBalF(YssD.add(rs.getDouble("FBaseCuryMoneyF"), secrecpaybal.getBaseBalF()));
       secrecpaybal.setPortBalF(YssD.add(rs.getDouble("FPortCuryMoneyF"), secrecpaybal.getPortBalF()));
       //-------------------------------------------//
        */
     
       secrecpaybal1.setAttrClsCode(secrecpaybal0.getAttrClsCode());
       hmEveStg.put(sVDKey, secrecpaybal1);
      
}
/**
 * 使用组合代码，证券代码和日期获取估值行情
 * @param sPortCode
 * @param sSecurityCode
 * @param dDate
 * @return
 * @throws YssException 
 */
public double getValMktPriceBySecAndPort(String sPortCode,
		String sSecurityCode, Date dDate) throws YssException {
	  String strSql="";
	  ResultSet rs=null;
	  double dPrice=0;
	  try{
		  strSql="select a.fprice from "+pub.yssGetTableName("Tb_Data_ValMktPrice")
		  		+" a  join (select fportcode, fsecuritycode, max(fvaldate) as fvaldate from "+pub.yssGetTableName("Tb_Data_ValMktPrice")
		  		+" where fvaldate <= "+dbl.sqlDate(dDate)+" and fportcode = "+dbl.sqlString(sPortCode)
		  		+" and fsecuritycode ="+dbl.sqlString(sSecurityCode)
		  		+" group by fportcode, fsecuritycode) b on a.fportcode = b.fportcode and a.fsecuritycode =b.fsecuritycode"
		  		+"  and a.fvaldate = b.fvaldate where a.fportcode ="+dbl.sqlString(sPortCode)
		  		+" and a.fsecuritycode = "+dbl.sqlString(sSecurityCode);
		  rs=dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		  if(rs.next()){
			  dPrice=rs.getDouble("fprice");
		  }
	  }catch(Exception e){
		  throw new YssException(e);
	  }finally{
		  dbl.closeResultSetFinal(rs);
	  }
	return dPrice;
}

/**
    * sj 重写的方法，在中间放入了获取前一天的证券应收应付库存的数据的hashtable
    * edit 20071203
    * edit 20071204  独立出来，以便与新加入的方法一起使用
    * getStorageRecData
    *获得证券应收应付库存的数据 插入到证券应收应付库存表
    * 每个金额都乘以方向 by ly 080218
    * @return ArrayList
    */
   public void getStorageRecData(java.util.Date dDate, HashMap hmEveStg,
                                 boolean bAnaly1, boolean bAnaly2,
                                 boolean bAnaly3
                                 ) throws
         YssException {
      String strSql = "", strTmpSql = "";
      ResultSet rs = null;
      SecRecPayBalBean secrecpaybal = null;
//   ArrayList all = new ArrayList();
//      java.util.Date dDate = null;
      String strError = "统计证券应收应付库存出错";

		// --- QDV4中保2010年1月11日01_B add by jiangshichao 2010.01.18 合并太平版本代码--------
      String sRecPayPara = "";
      CtlPubPara pubPara = new CtlPubPara();
      pubPara.setYssPub(pub);
      sRecPayPara = pubPara.getRecPayPara();// 获取应收应付库存统计方式
      double dScale = 0;
      double dBaseMoney = 0;
      double dPortMoney = 0;
      // --- QDV4中保2010年1月11日01_B end ----------------------------------
      boolean analy1;
      boolean analy2;
      boolean analy3;
      double baseMoney = 0;
      double portMoney = 0;
      String sKey = "";
      double dBaseRate = 1;
      double dPortRate = 1;
      //HashMap hmEveStg = null;
      Iterator iter = null;
      try {    	  
    	 //add by songjie 2011.01.31 BUG:1008 QDV4上海(37上线测试)2011年1月26日03_B 
    	 String res = storageAnalysis();
         analy1 = bAnaly1;
         analy2 = bAnaly2;
         analy3 = bAnaly3;
         strSql = "select FTransDate, FPortCode, FInvestType," + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               "FAttrClsCode, FCatType, FSecurityCode,FTsfTypeCode," +
               //-------------------------------------------
               "(case when FSubTsfTypeCode = '06FI_B' then '06FI' " +
//               " when FSubTsfTypeCode = '07FI_B' then '07FI' " + //现将07FI_B的数据修改成02FI_B样式之后就不用再获取了 sj modified 20081229 MS00121
               " else FSubTsfTypeCode end) as FSubTsfTypeCode," +
               //-------------------------------------------
               "FCuryCode, " +
               (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                " as FAnalysisCode1," : " ") +
               (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                " as FAnalysisCode2," : " ") +
               (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                " as FAnalysisCode3," : " ") +
               " sum(FMoney*FInOut) as FMoney, " +
               " sum(FBaseCuryMoney*FInOut) as FBaseCuryMoney," +
               " sum(FPortCuryMoney*FInOut) as FPortCuryMoney," +
               " sum(FMMoney*FInOut) as FMMoney, " +
               " sum(FMBaseCuryMoney*FInOut) as FMBaseCuryMoney," +
               " sum(FMPortCuryMoney*FInOut) as FMPortCuryMoney," +
               " sum(FVMoney*FInOut) as FVMoney, " +
               " sum(FVBaseCuryMoney*FInOut) as FVBaseCuryMoney," +
               " sum(FVPortCuryMoney*FInOut) as FVPortCuryMoney," +
               //-----------2008.11.13 蒋锦 添加-------------//
               //储存保留8位小数的原币，基础货币，本位币金额,用于计算估值增值汇兑损益
               //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
               " sum(FMoneyF*FInOut) as FMoneyF," +
               " sum(FBaseCuryMoneyF*FInOut) as FBaseCuryMoneyF," +
               " sum(FPortCuryMoneyF*FInOut) as FPortCuryMoneyF," +
               //-------------------------------------------//
               //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
               " 0 as FAmount," +//edited by zhouxiang 2010.11.27
               //-------------------end----------------------------------------------
               " 'Stat' as FType from " +
               pub.yssGetTableName("Tb_Data_SecRecPay") +
               " where FTransDate =" + dbl.sqlDate(dDate) +
			   (sStgNums.length()>0?(" and FNum in("+operSql.sqlCodes(sStgNums)+")"):"")+//by leeyu add 20100417 QDV4中保2010年4月14日02_B MS01092
               " and FPortCode in (" + portCodes + ")" +
               /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//			   (statCodes.length()>0?(" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")"):"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
               (statCodes.length()>0?(" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500)) + ")":"")+
               /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               " and (FMoney <> 0 or FBaseCuryMoney <> 0 or FPortCuryMoney <> 0)" +
               " and FCheckState = 1 and (FTsfTypeCode in (" +
               dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + ", " + //应收款项
               dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + ", " + //应付款项
               dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) + ", " + //汇兑损益
               dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) + //估值增值
               "," + dbl.sqlString(YssOperCons.Yss_ZJDBLX_Discounts) +
               "," + dbl.sqlString(YssOperCons.Yss_ZJDBLX_Premium) +
                "," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Cost) + //2009.09.05 蒋锦 添加溢折价的应收应付统计 MS00656 QDV4赢时胜(上海)2009年8月24日01_A
			   ")  or (FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) +
			   " and FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FP01_SR) + ")) " +//xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持               
			   " group by FTransDate, FPortCode, FSecurityCode, FInvestType" + //投资类型 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               (analy1 ? ", FAnalysisCode1" : " ") +
               (analy2 ? ", FAnalysisCode2" : " ") +
               (analy3 ? ", FAnalysisCode3" : " ") +
               ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode, FCatType, FAttrClsCode" +
               //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15//edited by zhouxiang 2010.11.27 证券借贷应收应付改从交易数据取数取数
					" union all select b.fbargaindate as FTransDate,b.FPortCode,'C' as FInvestType,b.FAttrClsCode,b.FCatType,b.FSecurityCode,"
					+ "b.FTsfTypeCode,b.FSubTsfTypeCode,b.FCuryCode"
					+ (analy1 ? ", FAnalysisCode1" : " ")
					+ (analy2 ? ", FAnalysisCode2" : " ")
					+ (analy3 ? ", FAnalysisCode3" : " ")
					+ ",sum(b.FMoney) as FMoney, sum(b.FBaseCuryMoney) as FBaseCuryMoney,"
					+ "sum(b.FPortCuryMoney) as FPortCuryMoney,sum(b.FMMoney) as FMMoney,sum(b.FMBaseCuryMoney) as FMBaseCuryMoney,sum(b.FMPortCuryMoney) as FMPortCuryMoney,"
					+ "sum(b.FVMoney) as FVMoney,sum(b.FVBaseCuryMoney) as FVBaseCuryMoney,sum(b.FVPortCuryMoney) as FVPortCuryMoney,"
					+ "sum(b.FMoneyF) as FMoneyF, sum(b.FBaseCuryMoneyF) as FBaseCuryMoneyF,sum(b.FPortCuryMoneyF) as FPortCuryMoneyF,"
					+ "sum(FAmount) as FAmount,'Stat' as FType  from (select a.FbargainDate,a.Fportcode, a.fattrclscode,  a.fattrclscode as FCatType,"
					+ "a.fsecuritycode,(case when ftradetypecode = 'borrow' then  '10' when ftradetypecode = 'BInPaySec' then  '10'"
					+" when ftradetypecode = 'BInPayOP' then"
					+ "'07' when ftradetypecode = 'Loan' then '10' when ftradetypecode = 'BOutRecSec' then '10'  when ftradetypecode = 'BOutRecOP' then '06' "
					+ " else ftradetypecode end) as FTsfTypeCode,(case when ftradetypecode = 'borrow' then '10BSC' when ftradetypecode = 'BInPaySec' then"
					+ " '10BSC' when ftradetypecode = 'BInPayOP' then  '07AW' when ftradetypecode = 'Loan' then  '10BLC' when ftradetypecode = 'BOutRecSec' then"
					+ " '10BLC' when ftradetypecode = 'BOutRecOP' then  '06AW' else  ftradetypecode end) as FSubTsfTypeCode,b.ftradecury as FCuryCode,"
					//delete by songjie 2011.01.31 BUG:1008 QDV4上海(37上线测试)2011年1月26日03_B
//					+ " a.finvmgrcode as FAnalysisCode1,a.fbrokercode as FAnalysisCode2,sum(nvl(a.FCost, 0)) as FMoney,sum(NVL(a.FBaseCuryCost, 0)) as FBaseCuryMoney,"
					//add by songjie 2011.01.31 BUG:1008 QDV4上海(37上线测试)2011年1月26日03_B
					+ (res.split("\t").length >0 ? res.split("\t")[0]:"") + " sum(nvl(a.FCost, 0)) as FMoney,sum(NVL(a.FBaseCuryCost, 0)) as FBaseCuryMoney, " 
					+ " sum(nvl(FPortCuryCost, 0)) as FPortCuryMoney,sum(nvl(a.FMCost, 0)) as FMMoney,sum(nvl(FMBaseCuryCost, 0)) as FMBaseCuryMoney,"
					+ " sum(nvl(FMPortCuryCost, 0)) as FMPortCuryMoney,sum(nvl(a.FVCost, 0)) as FVMoney,sum(nvl(a.FVBaseCuryCost, 0)) as FVBaseCuryMoney,"
					+ " sum(nvl(FVPortCuryCost, 0)) as FVPortCuryMoney,sum(nvl(a.FCost, 0)) as FMoneyF,sum(nvl(FBaseCuryCost, 0)) as FBaseCuryMoneyF,"
					+ " sum(nvl(FPortCuryCost, 0)) as FPortCuryMoneyF,Ftradeamount as FAmount from "
					+ pub.yssGetTableName("tb_DATA_SecLendTRADE")
					+ " a left join (select * from "+pub.yssGetTableName("tb_para_security")+" where fcheckstate = 1) b on a.fsecuritycode =b.fsecuritycode where"
					+ " a.fcheckstate = 1 and a.fportcode in ("
					+ portCodes
					+ ")and fbargaindate = "
					+ dbl.sqlDate(dDate)
					/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
					* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//					+ (statCodes.length() > 0 ? (" and a.FSecurityCode in("+ operSql.sqlCodes(statCodes) + ")") : "")
					+ (statCodes.length() > 0 ? (" and (" + operSql.getNumsDetail(statCodes,"a.FSecurityCode",500)) + ")" : "")
					/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
					//edited by zhouxiang 2010.12.31 权证数据不显示在应收应付中， 而是直接计入库存基本分页中 此处取消 , 'BInPayOP'  , 'BOutRecOP'
					+ "and a.ftradetypecode in ('borrow', 'BInPaySec', 'Loan',"
					+ " 'BOutRecSec') group by a.FbargainDate,a.Fportcode,a.fattrclscode,a.fsecuritycode,ftradetypecode,b.ftradecury,"
					//edit by songjie 2011.01.31 BUG:1008 QDV4上海(37上线测试)2011年1月26日03_B
					+ (res.split("\t").length >1 ? res.split("\t")[1]:"") + " a.ftradeamount) b group by b.fbargaindate,b.FPortCode,b.FAttrClsCode,b.FCatType,b.FSecurityCode,"
					//add by songjie 2011.01.31 BUG:1008 QDV4上海(37上线测试)2011年1月26日03_B
					+ " b.FTsfTypeCode,b.FSubTsfTypeCode, " + 
					(analy1 ? " FAnalysisCode1," : " ") + (analy2 ? " FAnalysisCode2," : " ") + (analy3 ? " FAnalysisCode3," : " ") + "b.FCuryCode" +
					//add by songjie 2011.01.31 BUG:1008 QDV4上海(37上线测试)2011年1月26日03_B
					//delete by songjie 2011.01.31 BUG:1008 QDV4上海(37上线测试)2011年1月26日03_B
					//					+ " b.FTsfTypeCode,b.FSubTsfTypeCode, b.FCuryCode,b.FAnalysisCode1, b.FAnalysisCode2" +
               //--------------------end------------------------------------
			   //" union "
			   " union all "+//并行优化,改为union all,原因是上下代码已经采用FType区分过 by leeyu 20100604
               //------------------------------------
               "select FTransDate, FPortCode, FInvestType,FAttrClsCode, FCatType, FSecurityCode, " + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               "(case when FTsfTypeCode = '02' " +
//               " or " + //现将07FI_B的数据修改成02FI_B样式之后就不用再获取了 sj modified 20081229 MS00121
//               "(FTsfTypeCode = '07' and (FSubTsfTypeCode = '07FI' or FSubTsfTypeCode = '07FI_B')) " +
               " then '06'" +
               " when FTsfTypeCode = '03' then '07' else FTsfTypeCode end) as FTsfTypeCode," +
               "(case when FSubTsfTypeCode = '02FI_B' then '06FI' " + //现将07FI_B的数据修改成02FI_B样式,用以冲抵计提的利息。 sj modified 20081229 MS00121
               " when FTsftypecode ='02' then '06'" + dbl.sqlJN() +
               dbl.sqlSubStr("FSubTsfTypeCode", "3") +
               " when FTsftypecode ='03' then '07'" + dbl.sqlJN() +
               dbl.sqlSubStr("FSubTsfTypeCode", "3") +
               " else FSubTsfTypeCode " +
               " end) as FSubTsfTypeCode," +
               "FCuryCode, " +
               (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                " as FAnalysisCode1," : " ") +
               (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                " as FAnalysisCode2," : " ") +
               (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                " as FAnalysisCode3," : " ") +
               " sum(-FMoney*FInOut) as FMoney, " +
               " sum(-FBaseCuryMoney*FINOut) as FBaseCuryMoney," +
               " sum(-FPortCuryMoney*FINOut) as FPortCuryMoney," +
               " sum(-FMMoney*FINOut) as FMMoney, " +
               " sum(-FMBaseCuryMoney*FInOut) as FMBaseCuryMoney," +
               " sum(-FMPortCuryMoney*FINOut) as FMPortCuryMoney," +
               " sum(-FVMoney*FInOut) as FVMoney, " +
               " sum(-FVBaseCuryMoney*FInOut) as FVBaseCuryMoney," +
               " sum(-FVPortCuryMoney*FInOut) as FVPortCuryMoney," +
               " sum(-FMoneyF*FInOut) as FMoneyF," +
               " sum(-FBaseCuryMoneyF*FInOut) as FBaseCuryMoneyF," +
               " sum(-FPortCuryMoneyF*FInOut) as FPortCuryMoneyF," +
               //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
               " 0 as FAmount," +//edited by zhouxiang 2010.11.27
               //-------------------end----------------------------------------------
               " 'Rush' as FType from " +
               pub.yssGetTableName("Tb_Data_SecRecPay") +
               " where FTransDate =" + dbl.sqlDate(dDate) +
			   (sStgNums.length()>0?(" and FNum in("+operSql.sqlCodes(sStgNums)+")"):"")+//by leeyu add 20100417 QDV4中保2010年4月14日02_B MS01092
               " and FPortCode in (" + portCodes + ")" +
               /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/	
//			   (statCodes.length()>0?(" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")"):"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
               (statCodes.length()>0?(" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500)) + ")":"")+
               /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               " and (FMoney <> 0 or FBaseCuryMoney <> 0 or FPortCuryMoney <> 0)" +
//-------------MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A sj ----------------------
//               " and FCheckState = 1 and (FTsfTypeCode in ('02','03','07') " +
               " and FCheckState = 1 and (FTsfTypeCode in ('02','03') " +//去除07数据
               "  and FSubTsfTypeCode not in ('02LE','03LE')"+
//-----------------------------------------------------------------------------------
               //--------------只有流入状态用来冲减06，07，流出状态用来冲减其本身 胡坤 20090707 QDV4招商证券2009年06月04日01_A:MS00484
               " and FInout = 1) " +
               //----------------------------------end-------------------//
               " group by FTransDate, FPortCode, FAttrClsCode, FCatType, FSecurityCode, FInvestType" + //投资类型 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               (analy1 ? ", FAnalysisCode1" : " ") +
               (analy2 ? ", FAnalysisCode2" : " ") +
               (analy3 ? ", FAnalysisCode3" : " ") +
               ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode " +
			   // --------- QDV4中保2010年1月11日01_B add by jiangshichao
			   // 2010.01.18 ----------
			   (sRecPayPara.equalsIgnoreCase("1") ? " order by FType desc": ""); // 根据先加后减的规则，这里按资金的冲减状态进行排序
			   // --------- QDV4中保2010年1月11日01_B end
			  // --------------------------------------
         rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
         while (rs.next()) {
            sKey = rs.getString("FSecurityCode") + "\f" +
                  rs.getString("FTsfTypeCode") + "\f" +
                  rs.getString("FSubTsfTypeCode") + "\f" +
                  rs.getString("FPortCode") +
                  (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                  (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                  (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                  (rs.getString("FAttrClsCode") == null ||
                   rs.getString("FAttrClsCode").trim().length() == 0 ? " " :
                   rs.getString("FAttrClsCode"))+ "\f" + //这里加上属性代码,也是主键之一,by leeyu BUG:0000437  // 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
				   rs.getString("FInvestType"); //获取投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
            if (hmEveStg.containsKey(sKey)) {
               secrecpaybal = (SecRecPayBalBean) hmEveStg.get(sKey);
               secrecpaybal.setDtStorageDate(rs.getDate("FTransDate"));
               secrecpaybal.setSPortCode(rs.getString("FPortCode"));
               secrecpaybal.setSSecurityCode(rs.getString("FSecurityCode"));
               secrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                     "FAnalysisCode1") : " "));
               secrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                     "FAnalysisCode2") : " "));
               secrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                     "FAnalysisCode3") : " "));
               secrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
               secrecpaybal.setSSubTsfTypeCode(rs.getString(
                     "FSubTsfTypeCode"));
			   secrecpaybal.setSInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               secrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
               //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
               secrecpaybal.setAmount(YssD.add(rs.getDouble("FAmount"),secrecpaybal.getAmount()));
               //---------------end--------------------------------------------------
			   // --- QDV4中保2010年1月11日01_B add by jiangshichao 2010.01.14-----------------------------
			   if (sRecPayPara.equalsIgnoreCase("1")&& rs.getString("FType").equalsIgnoreCase("Rush")) {
					dScale = YssD.div(rs.getDouble("FMoney"), secrecpaybal.getDBal());
					dBaseMoney = YssD.round(YssD.mul(secrecpaybal.getDBaseBal(), dScale), 2);
					dPortMoney = YssD.round(YssD.mul(secrecpaybal.getDPortBal(), dScale), 2);
					dBaseRate = YssD.round(YssD.div(dBaseMoney, rs.getDouble("FMoney")), 15);
					dPortRate = YssD.round(YssD.div(dBaseMoney, dPortMoney), 15);
						
					secrecpaybal.setDBaseRate(dBaseRate);// 用移動加權計算出來的匯率去更新業務類別為【收入】數據的匯率。
					secrecpaybal.setDPortRate(dPortRate);
                       //核算成本
					secrecpaybal.setDBal(YssD.add(rs.getDouble("FMoney"),secrecpaybal.getDBal()));
					secrecpaybal.setDBaseBal(YssD.add(dBaseMoney,secrecpaybal.getDBaseBal()));
					secrecpaybal.setDPortBal(YssD.add(dPortMoney,secrecpaybal.getDPortBal()));
                        //管理成本
					secrecpaybal.setDMBal(YssD.add(rs.getDouble("FMMoney"),secrecpaybal.getDMBal()));
					secrecpaybal.setDMBaseBal(YssD.add(YssD.round(YssD.mul(secrecpaybal.getDMBaseBal(),dScale), 2), secrecpaybal.getDMBaseBal()));
					secrecpaybal.setDMPortBal(YssD.add(YssD.round(YssD.mul(secrecpaybal.getDMPortBal(),dScale), 2), secrecpaybal.getDMPortBal()));
                        //估值成本
					secrecpaybal.setDVBal(YssD.add(rs.getDouble("FVMoney"),secrecpaybal.getDVBal()));
					secrecpaybal.setDVBaseBal(YssD.add(YssD.round(YssD.mul(secrecpaybal.getDVBaseBal(),dScale), 2), secrecpaybal.getDVBaseBal()));
					secrecpaybal.setDVPortBal(YssD.add(YssD.round(YssD.mul(secrecpaybal.getDVPortBal(),dScale), 2), secrecpaybal.getDVPortBal()));
						
					if(secrecpaybal.getSTsfTypeCode().equalsIgnoreCase("06")){
						SecRecPayStorageAdmin  secRecPayStorageAdmin = new SecRecPayStorageAdmin();
						secRecPayStorageAdmin.setYssPub(pub);
						secRecPayStorageAdmin.updateAvgRate(secrecpaybal,dBaseMoney<0?(-dBaseMoney):dBaseMoney,dPortMoney<0?(-dPortMoney):dPortMoney);
					}
				} else {
						// --- QDV4中保2010年1月11日01_B end----------------------------------------------------------
					secrecpaybal.setDBal(
							YssD.add(rs.getDouble("FMoney"), secrecpaybal.getDBal()));
					secrecpaybal.setDBaseBal(
							YssD.add(rs.getDouble("FBaseCuryMoney"),
									secrecpaybal.getDBaseBal()));
					secrecpaybal.setDPortBal(
							YssD.add(rs.getDouble("FPortCuryMoney"),
									secrecpaybal.getDPortBal()));
					secrecpaybal.setDMBal(YssD.add(rs.getDouble("FMMoney"),
							secrecpaybal.getDMBal()));
					secrecpaybal.setDMBaseBal(
							YssD.add(rs.getDouble("FMBaseCuryMoney"),
									secrecpaybal.getDMBaseBal()));
					secrecpaybal.setDMPortBal(
							YssD.add(rs.getDouble("FMPortCuryMoney"),
									secrecpaybal.getDMPortBal()));
					secrecpaybal.setDVBal(YssD.add(rs.getDouble("FVMoney"),
							secrecpaybal.getDVBal()));
					secrecpaybal.setDVBaseBal(
							YssD.add(rs.getDouble("FVBaseCuryMoney"),
									secrecpaybal.getDVBaseBal()));
					secrecpaybal.setDVPortBal(
							YssD.add(rs.getDouble("FVPortCuryMoney"),
									secrecpaybal.getDVPortBal()));
				}
               //-----------2008.11.13 蒋锦 添加-------------//
               //储存保留8位小数的原币，基础货币，本位币金额，用于计算估值增值汇兑损益
               //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
               secrecpaybal.setBalF(YssD.add(rs.getDouble("FMoneyF"), secrecpaybal.getBalF()));
               secrecpaybal.setBaseBalF(YssD.add(rs.getDouble("FBaseCuryMoneyF"), secrecpaybal.getBaseBalF()));
               secrecpaybal.setPortBalF(YssD.add(rs.getDouble("FPortCuryMoneyF"), secrecpaybal.getPortBalF()));
               //-------------------------------------------//
               secrecpaybal.setAttrClsCode((rs.getString("FAttrClsCode")==null || rs.getString("FAttrClsCode").trim().length() == 0)?" ":rs.getString("FAttrClsCode"));//这里加上属性代码,也是主键之一,by leeyu BUG:0000437
               hmEveStg.put(sKey, secrecpaybal);
            }
            else {
               secrecpaybal = new SecRecPayBalBean();
               secrecpaybal.setDtStorageDate(dDate);
               secrecpaybal.setSPortCode(rs.getString("FPortCode"));
               secrecpaybal.setSSecurityCode(rs.getString("FSecurityCode"));
               secrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                     "FAnalysisCode1") : " "));
               secrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                     "FAnalysisCode2") : " "));
               secrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                     "FAnalysisCode3") : " "));
               secrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
               secrecpaybal.setSSubTsfTypeCode(rs.getString(
                     "FSubTsfTypeCode"));
			   secrecpaybal.setSInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               secrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
               secrecpaybal.setDBal(
                     YssD.add(rs.getDouble("FMoney"), secrecpaybal.getDBal()));
               secrecpaybal.setDMBal(
                     YssD.add(rs.getDouble("FMMoney"), secrecpaybal.getDMBal()));
               secrecpaybal.setDVBal(
                     YssD.add(rs.getDouble("FVMoney"), secrecpaybal.getDVBal()));

               secrecpaybal.setDBaseBal(
                     YssD.add(rs.getDouble("FBaseCuryMoney"),
                              secrecpaybal.getDBaseBal()));
               secrecpaybal.setDMBaseBal(
                     YssD.add(rs.getDouble("FMBaseCuryMoney"),
                              secrecpaybal.getDMBaseBal()));
               secrecpaybal.setDVBaseBal(
                     YssD.add(rs.getDouble("FVBaseCuryMoney"),
                              secrecpaybal.getDVBaseBal()));

               secrecpaybal.setDPortBal(
                     YssD.add(rs.getDouble("FPortCuryMoney"),
                              secrecpaybal.getDPortBal()));
               secrecpaybal.setDMPortBal(
                     YssD.add(rs.getDouble("FMPortCuryMoney"),
                              secrecpaybal.getDMPortBal()));
               secrecpaybal.setDVPortBal(
                     YssD.add(rs.getDouble("FVPortCuryMoney"),
                              secrecpaybal.getDVPortBal()));
               
               //add by zhangfa 证券借贷业务需求-证券应收应付界面添加数量参数  2010-11-15
               secrecpaybal.setAmount(rs.getDouble("FAmount"));
               //---------------end--------------------------------------------------
               //-----------2008.11.13 蒋锦 添加-------------//
               //储存保留8位小数的原币，基础货币，本位币金额
               //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
               secrecpaybal.setBalF(YssD.add(rs.getDouble("FMoneyF"), secrecpaybal.getBalF()));
               secrecpaybal.setBaseBalF(YssD.add(rs.getDouble("FBaseCuryMoneyF"), secrecpaybal.getBaseBalF()));
               secrecpaybal.setPortBalF(YssD.add(rs.getDouble("FPortCuryMoneyF"), secrecpaybal.getPortBalF()));
               //-------------------------------------------//
               secrecpaybal.setAttrClsCode((rs.getString("FAttrClsCode")==null || rs.getString("FAttrClsCode").trim().length() == 0)?" ":rs.getString("FAttrClsCode"));
               hmEveStg.put(sKey, secrecpaybal);
            }
         }

         /*iter = hmEveStg.keySet().iterator();
                while (iter.hasNext()) {
            sKey = (String) iter.next();
            secrecpaybal = (SecRecPayBalBean) hmEveStg.get(sKey);
            all.add(secrecpaybal);
                }
                return all;*/
      }
      catch (Exception e) {
         throw new YssException("系统进行证券应收应付库存统计,在当日应收应付库存时出现异常!\n",e);// by 曹丞2009.01.22 统计当日证券应收应付信息异常 MS00004 QDV4.1-2009.2.1_09A
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }

   }

   /**
    * add by songjie 
    * 2011.01.31
    * BUG:1008
    * QDV4上海(37上线测试)2011年1月26日03_B
    * @return String
    */
   public String storageAnalysis() throws YssException, SQLException {
       String sResult = "";
       String sResult1 = "";
       String strSql = "";
       ResultSet rs = null;
       strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
           pub.yssGetTableName("Tb_Para_StorageCfg") +
           " where FCheckState = 1 and FStorageType = " +
           dbl.sqlString(YssOperCons.YSS_KCLX_Security);
       rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
       if (rs.next()) {
           for (int i = 1; i <= 3; i++) {
               if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                   rs.getString("FAnalysisCode" + String.valueOf(i)).equalsIgnoreCase("002")) {
            	   sResult = sResult + " a.FBrokercode as FAnalysisCode" + String.valueOf(i) + ",";
            	   sResult1 = sResult1 + " a.FBrokercode, ";
               } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                          rs.getString("FAnalysisCode" + String.valueOf(i)).equalsIgnoreCase("003")) {
            	   sResult = sResult + " b.FExchangeCode as FAnalysisCode" + String.valueOf(i) + ",";
            	   sResult1 = sResult1 + " b.FExchangeCode, ";
               } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                          rs.getString("FAnalysisCode" + String.valueOf(i)).equalsIgnoreCase("001")) {
            	   sResult = sResult + " a.FInvmgrcode as FAnalysisCode" + String.valueOf(i) + ",";
            	   sResult1 = sResult1 + " a.FInvmgrcode, ";
               } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                       rs.getString("FAnalysisCode" + String.valueOf(i)).equalsIgnoreCase("004")){
            	   sResult = sResult + " b.FCatCode as FAnalysisCode" + String.valueOf(i) + ",";
            	   sResult1 = sResult1 + " b.FCatCode, ";
               } else {
                   sResult = sResult + " ' ' as FAnalysisCode" + String.valueOf(i) + ",";
               }
           }
       }

       dbl.closeResultSetFinal(rs);

       return sResult + "\t" + sResult1;

   }
   
	/** 合并太平版本代码
	 * 此方法专用于太平资产转货业务的证券库存统计
	 *  by leeyu 20100417 QDV4中保2010年4月14日02_B MS01092
	 */
	public ArrayList getPartInsideChangeGoodsStatData(java.util.Date dDate) throws YssException {
		sStgNums=getStgSecPaytNums(dDate);
		return getStorageStatData(dDate);
	}
    /**
     * 统计交易关联子项 sj 20071204
     * 2009.07.20 蒋锦 修改
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 增加了ETF基金申购赎回和债转股业务的交易关联数据处理
     * @param dDate Date
     * @param hmEveStg HashMap
     * @param bAnaly1 boolean
     * @param bAnaly2 boolean
     * @param bAnaly3 boolean
     * @throws YssException
     */
    private void getStgTradeSubRelaData(java.util.Date dDate, HashMap hmEveStg,
                                        boolean bAnaly1, boolean bAnaly2,
                                        boolean bAnaly3) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        SecRecPayBalBean secRecstorage = null;

        String sKey = "";

      double dBaseRate = 1;
      double dPortRate = 1;
      try {
         strSql =
               "select a.*,b.FTradeTypeCode,b.FTradeCury,b.FCatCode,b.FSubCatCode,b.FInvestType," + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               " b.FPortCode,b.FPortCury,b.FAttrClsCode from " +
               pub.yssGetTableName("Tb_Data_TradeRelaSub") +
               //---------------------------------------
               " a left join (select FNum,b1.FSecurityCode as FSecurityCode,FTradeTypeCode,FTradeCury,FCatCode,FInvestType," + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               " FSubCatCode,b1.FPortCode as FPortCode,FPortCury,FAttrClsCode from " +
               pub.yssGetTableName("Tb_Data_SubTrade") +
               //---------------------------------------
               " b1 left join (select * from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " where FCheckState = 1) b2 on b1.FSecurityCode = b2.FSecurityCode " +
               //---------------------------------------
               " left join (select * from " +
               pub.yssGetTableName("Tb_Para_Portfolio") +
               " where FCheckState = 1) b3 on b1.FPortCode = b3.FPortCode " +
               //---------------------------------------
               " )" +
               " b on a.FNum = b.FNum where a.FCheckState = 1"+ //FCheckState指代不明 lzp modify 20080121
               /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//			   (statCodes.length()>0?(" and a.FSecurityCode in("+operSql.sqlCodes(statCodes)+")"):"");//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
               (statCodes.length()>0?(" and (" + operSql.getNumsDetail(statCodes,"a.FSecurityCode",500)) + ")":"");
         		/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
         while (rs.next()) {
            //-------------------增加币种有效性信息检查 by 曹丞 2009.01.22 MS00004 QDV4.1-2009.2.1_09A---//
            if (rs.getString("FTradeCury") == null|| rs.getString("FTradeCury").trim().length() == 0)
            {
               throw new YssException("系统进行证券应收应付库存统计,在统计交易关联子项时检查到代码为【" +
                                      rs.getString("FSecurityCode") +
                                      "】证券对应的币种信息不存在!" + "\n" +
                                      "请核查以下信息:" + "\n" +
                                      "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                      "2.【证券品种信息】中该证券交易币种项设置是否正确!");
            }
            //-------------------------------------------------------------------------------------------//
            dBaseRate = 1;
            dPortRate = 1;

            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                  rs.getString("FTradeCury"),
                  rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
            dPortRate = this.getSettingOper().getCuryRate(dDate,
                  rs.getString("FPortCury"),
                  rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
            //-------------------增加汇率有效性信息检查 by 曹丞 2009.01.22 MS00004 QDV4.1-2009.2.1_09A----//
            if(dBaseRate==0||dPortRate==0)
            {
               throw new YssException("系统进行证券应收应付库存统计,在统计交易关联子项时检查代码为【" +
                                      rs.getString("FSecurityCode") +
                                      "】证券对应的汇率信息不存在!" + "\n" +
                                      "请核查以下信息:" + "\n" +
                                      "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                      "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");

            }
            //-----------------------------------------------------------------------------------//
            sKey = rs.getString("FSecurityCode") + "\f" +
                  rs.getString("FTsfTypeCode") + "\f" +
                  rs.getString("FSubTsfTypeCode") + "\f" +
                  rs.getString("FPortCode") +
                  (bAnaly1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                  (bAnaly2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                  (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                  (rs.getString("FAttrClsCode") == null ||
                   rs.getString("FAttrClsCode").length() == 0 ? " " :
                   rs.getString("FAttrClsCode")) + "\f" +  //这里加上属性代码,也是主键之一,by leeyu BUG:0000437  // modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
					rs.getString("FInvestType"); //获取投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;

                //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
                //2009.07.20 蒋锦 修改
                //删除掉了期货的处理，期货的处理已经不在此进行了
                //判断品种类型和业务类型来设置成本流动方向
                int iInOut = rs.getInt("FInOut");//流动方向
                if (hmEveStg.containsKey(sKey)) {
                    //---------------把证券应收应付库存中的数量和成本转出
                    secRecstorage = (SecRecPayBalBean) hmEveStg.get(sKey);
                    secRecstorage.setDtStorageDate(dDate);
                    secRecstorage.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    secRecstorage.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    secRecstorage.setSInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
                    secRecstorage.setDBal(YssD.add(secRecstorage.getDBal(),
                                                   YssD.mul(rs.getDouble("FBal"), iInOut)));
                    secRecstorage.setDMBal(YssD.add(secRecstorage.getDMBal(),
                                                    YssD.mul(rs.getDouble("FMBal"), iInOut)));
                    secRecstorage.setDVBal(YssD.add(secRecstorage.getDVBal(),
                                                    YssD.mul(rs.getDouble("FVBal"), iInOut)));
                    secRecstorage.setDBaseBal(YssD.add(secRecstorage.getDBaseBal(),
                                                       YssD.mul(rs.getDouble("FBaseCuryBal"), iInOut)));
                    secRecstorage.setDMBaseBal(YssD.add(secRecstorage.
                                                        getDMBaseBal(), YssD.mul(rs.getDouble("FMBaseCuryBal"), iInOut)));
                    secRecstorage.setDVBaseBal(YssD.add(secRecstorage.
                                                        getDVBaseBal(), YssD.mul(rs.getDouble("FVBaseCuryBal"), iInOut)));
                    secRecstorage.setDPortBal(YssD.add(secRecstorage.getDPortBal(),
                                                       YssD.mul(rs.getDouble("FPortCuryBal"), iInOut)));
                    secRecstorage.setDMPortBal(YssD.add(secRecstorage.
                                                        getDMPortBal(), YssD.mul(rs.getDouble("FMPortCuryBal"), iInOut)));
                    secRecstorage.setDVPortBal(YssD.add(secRecstorage.
                                                        getDVPortBal(), YssD.mul(rs.getDouble("FVPortCuryBal"), iInOut)));
                    secRecstorage.setBalF(YssD.add(secRecstorage.getBalF(), YssD.mul(rs.getDouble("FBal"), iInOut)));
                    secRecstorage.setPortBalF(YssD.add(secRecstorage.getPortBalF(), YssD.mul(rs.getDouble("FPortCuryBal"), iInOut)));
                    secRecstorage.setBaseBalF(YssD.add(secRecstorage.getBaseBalF(), YssD.mul(rs.getDouble("FBaseCuryBal"), iInOut)));
                } else {
                    secRecstorage = new SecRecPayBalBean();
                    secRecstorage.setDtStorageDate(dDate);
                    secRecstorage.setSPortCode(rs.getString("FPortCode"));
                    secRecstorage.setSSecurityCode(rs.getString("FSecurityCode"));
                    secRecstorage.setSAnalysisCode1( (bAnaly1 ? rs.getString(
                        "FAnalysisCode1") : " "));
                    secRecstorage.setSAnalysisCode2( (bAnaly2 ? rs.getString(
                        "FAnalysisCode2") : " "));
                    secRecstorage.setSAnalysisCode3( (bAnaly3 ? rs.getString(
                        "FAnalysisCode3") : " "));
                    secRecstorage.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    secRecstorage.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    secRecstorage.setSInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
                    secRecstorage.setSCuryCode(rs.getString("FTradeCury"));
                    secRecstorage.setDBal(
                        YssD.add(rs.getDouble("FBal"), secRecstorage.getDBal()));
                    secRecstorage.setDMBal(
                        YssD.add(rs.getDouble("FMBal"), secRecstorage.getDMBal()));
                    secRecstorage.setDVBal(
                        YssD.add(rs.getDouble("FVBal"), secRecstorage.getDVBal()));

                    secRecstorage.setDBaseBal(
                        YssD.add(rs.getDouble("FBaseCuryBal"),
                                 secRecstorage.getDBaseBal()));
                    secRecstorage.setDMBaseBal(
                        YssD.add(rs.getDouble("FMBaseCuryBal"),
                                 secRecstorage.getDMBaseBal()));
                    secRecstorage.setDVBaseBal(
                        YssD.add(rs.getDouble("FVBaseCuryBal"),
                                 secRecstorage.getDVBaseBal()));

                    secRecstorage.setDPortBal(
                        YssD.add(rs.getDouble("FPortCuryBal"),
                                 secRecstorage.getDPortBal()));
                    secRecstorage.setDMPortBal(
                        YssD.add(rs.getDouble("FMPortCuryBal"),
                                 secRecstorage.getDMPortBal()));
                    secRecstorage.setDVPortBal(
                        YssD.add(rs.getDouble("FVPortCuryBal"),
                                 secRecstorage.getDVPortBal()));
                    secRecstorage.setBalF(YssD.add(rs.getDouble("FBal"), secRecstorage.getBalF()));
                    secRecstorage.setBaseBalF(YssD.add(rs.getDouble("FBaseCuryBal"), secRecstorage.getBaseBalF()));
                    secRecstorage.setPortBalF(YssD.add(rs.getDouble("FPortCuryBal"), secRecstorage.getPortBalF()));
                    secRecstorage.setAttrClsCode( (rs.getString("FAttrClsCode") == null || rs.getString("FAttrClsCode").trim().length() == 0) ? " " : rs.getString("FAttrClsCode"));
                    hmEveStg.put(sKey, secRecstorage);
                }
            }
        } catch (Exception e) {
            throw new YssException("系统进行证券应收应付库存统计,在统计当日关联交易时出现异常!\n", e); //by 曹丞 2009.01.22 统计当日关联交易异常 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

   public void yearChange(java.util.Date dDate, String portCode) throws
         YssException {
      Connection conn = dbl.loadConnection();
      boolean bTrans = false; //代表是否开始了事务
      String strSql = "";
      String YearMonth = "";
      int Year;
      ResultSet rs = null;
      try {
         conn.setAutoCommit(false);
         bTrans = true;
         YearMonth = YssFun.getYear(dDate) + "00";
         Year = YssFun.getYear(dDate);
         strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Secrecpay") +
               " where FYearMonth = " + dbl.sqlString(YearMonth)+
               " and FPortCode in( " +operSql.sqlCodes(portCode) + ")" ;//添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的 by leeyu 20090220 QDV4华夏2009年2月13日01_B MS00246
         dbl.executeSql(strSql);
         strSql = "insert into " + pub.yssGetTableName("Tb_Stock_Secrecpay") +
               "(FSECURITYCODE,FYearMonth,FStorageDate,FPORTCODE,FInvestType," + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FATTRCLSCODE,FCATTYPE," +
               "FTSFTYPECODE,FSUBTSFTYPECODE," +
               "FCURYCODE,FBAL,FMBAL,FVBAL," +
               "FBASECURYBAL,FMBASECURYBAL," +
               "FVBASECURYBAL,FPORTCURYBAL,FMPORTCURYBAL,FVPORTCURYBAL," +
               //---------------2008.11.14 蒋锦 添加---------------------//
               //储存用于计算估值增值汇兑损益的保留8位小数的原币，基础货币，本位币金额
               //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
               "FBalF, FBASECURYBALF, FPORTCURYBALF," +
               //-------------------------------------------------------//
               "FSTORAGEIND,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" +
               "(select FSECURITYCODE," + dbl.sqlString(YearMonth) +
               " as FYearMonth, " +
               dbl.sqlDate(new Integer(Year).toString() + "-01-01") +
               " as FStorageDate,FPORTCODE,FInvestType," + //投资类型 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FATTRCLSCODE,FCATTYPE," +
               "FTSFTYPECODE,FSUBTSFTYPECODE," +
               "FCURYCODE,FBAL,FMBAL,FVBAL," +
               "FBASECURYBAL,FMBASECURYBAL," +
               "FVBASECURYBAL,FPORTCURYBAL,FMPORTCURYBAL,FVPORTCURYBAL," +
               //---------------2008.11.14 蒋锦 添加---------------------//
               //储存保留8位小数的原币，基础货币，本位币金额
               //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
               "FBalF, FBASECURYBALF, FPORTCURYBALF," +
               //-------------------------------------------------------//
               "FSTORAGEIND,FCHECKSTATE," +
               dbl.sqlString(pub.getUserCode()) +
               " as FCREATOR," +
               dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
               " as FCREATETIME," +
               dbl.sqlString(pub.getUserName()) + " as FCHECKUSER," +
               dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
               " as FCHECKTIME" +
               " from " + pub.yssGetTableName("Tb_Stock_Secrecpay") +
               " where FYearMonth = " + dbl.sqlString( (Year - 1) + "12") +
               " and FStorageDate = " +
               dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31") +
               " and FPortCode in( " + portCode + ")" +
               ")";

         dbl.executeSql(strSql);
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (Exception e) {
         throw new YssException("年度结转错误!\n", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
         //dbl.closeStatementFinal(pst);
         dbl.endTransFinal(conn, bTrans);
      }

   }

    //add by jiangshichao 2011.01.15 合并太平资产年终结账需求
	public void yearChange_TP(java.util.Date dDate, String portCode)
			throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		String YearMonth = "";
		int Year;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			YearMonth = YssFun.getYear(dDate) + "00";
			Year = YssFun.getYear(dDate);
			strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Secrecpay")
					+ " where FYearMonth = " + dbl.sqlString(YearMonth)
					+ " and FPortCode in( " + operSql.sqlCodes(portCode) + ")";// 添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的
			// by
			// leeyu
			// 20090220
			// QDV4华夏2009年2月13日01_B
			// MS00246
			dbl.executeSql(strSql);

			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Stock_Secrecpay")
					+ "(FSECURITYCODE,FYearMonth,FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FATTRCLSCODE,FCATTYPE,"
					+ "FTSFTYPECODE,FSUBTSFTYPECODE,"
					+ "FCURYCODE,FBAL,FMBAL,FVBAL,"
					+ "FBASECURYBAL,FMBASECURYBAL,"
					+ "FVBASECURYBAL,FPORTCURYBAL,FMPORTCURYBAL,FVPORTCURYBAL,"
					+
					// ---------------2008.11.14 蒋锦 添加---------------------//
					// 储存用于计算估值增值汇兑损益的保留8位小数的原币，基础货币，本位币金额
					// 编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
					"FBalF, FBASECURYBALF, FPORTCURYBALF,"
					+
					// -------------------------------------------------------//
					"FSTORAGEIND,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
					+ "(select FSECURITYCODE,"
					+ dbl.sqlString(YearMonth)
					+ " as FYearMonth, "
					+ dbl.sqlDate(new Integer(Year).toString() + "-01-01")
					+ " as FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FATTRCLSCODE,FCATTYPE,"
					+ "FTSFTYPECODE,FSUBTSFTYPECODE,FCURYCODE,"

					/*********************************************************************************
					 * 需求编号:QDV4太平2010年12月20日01_A
					 * 需求描述：证券类年终结转时，核算成本和管理成本要调整为上年末市值
					 * 
					 * @author jiangshichao 2010.12.25
					 * 
					 *         
					 *         相关库存(核算、管理)【成本汇兑损益(9905%)、估值增值(09)、估值增值汇兑损益(9909%)
					 *         】数据清0， 【应收利息、估值成本等根据原处理方式】进行处理
					 * 
					 */
					// 1. 原币
					+ " case when  ftsftypecode='09' or fsubtsftypecode like '9905%' or fsubtsftypecode like '9909%'  then 0  else FBAL  end as FBAL,"
					+ " case when  ftsftypecode='09' or fsubtsftypecode like '9905%' or fsubtsftypecode like '9909%'  then 0  else FMBAL  end as FMBAL,"
					+ " FVBAL, "
					// 2. 基础货币
					+ " case when  ftsftypecode='09' or fsubtsftypecode like '9905%' or fsubtsftypecode like '9909%'  then 0  else FBASECURYBAL  end as FBASECURYBAL,"
					+ " case when  ftsftypecode='09' or fsubtsftypecode like '9905%' or fsubtsftypecode like '9909%'  then 0  else FMBASECURYBAL  end as FMBASECURYBAL,"
					+ " FVBASECURYBAL,"
					// 3. 组合货币
					+ " case when  ftsftypecode='09' or fsubtsftypecode like '9905%' or fsubtsftypecode like '9909%'  then 0  else FPORTCURYBAL  end as FPORTCURYBAL,"
					+ " case when  ftsftypecode='09' or fsubtsftypecode like '9905%' or fsubtsftypecode like '9909%'  then 0  else FMPORTCURYBAL  end as FMPORTCURYBAL,"
					+ " FVPORTCURYBAL,"
					// ---------------------- QDV4太平2010年12月20日01_A jiangshichao
					// 2010.12.25 end ---------------//
					+
					// ---------------2008.11.14 蒋锦 添加---------------------//
					// 储存保留8位小数的原币，基础货币，本位币金额
					// 编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
					"FBalF, FBASECURYBALF, FPORTCURYBALF,"
					+
					// -------------------------------------------------------//
					"FSTORAGEIND,FCHECKSTATE,"
					+ dbl.sqlString(pub.getUserCode())
					+ " as FCREATOR,"
					+ dbl
							.sqlString(YssFun
									.formatDatetime(new java.util.Date()))
					+ " as FCREATETIME,"
					+ dbl.sqlString(pub.getUserName())
					+ " as FCHECKUSER,"
					+ dbl
							.sqlString(YssFun
									.formatDatetime(new java.util.Date()))
					+ " as FCHECKTIME" + " from "
					+ pub.yssGetTableName("Tb_Stock_Secrecpay")
					+ " where FYearMonth = " + dbl.sqlString((Year - 1) + "12")
					+ " and FStorageDate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and FPortCode in( " + portCode + ")" + ")";

			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("年度结转错误!\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			// dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}

	}
   
   
   
   
   /**
    * 若有利息的应收应付库存但没有证券库存，则添加一条为0的证券库存。
    * sj 080117
    * modify by wangzuochun 2010.12.03 
    * @param dDate Date
    * @throws YssException
    */
   protected void afterSaveStorage(java.util.Date dDate) throws YssException {
      ResultSet rs = null;
      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      String strSql = "";
      SecurityStorageBean secStorage = null;
      SecurityStorageAdmin secAdmin = new SecurityStorageAdmin();
      
      boolean analy1;
      boolean analy2;
      boolean analy3;
      try {
         analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
         analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
         analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
         strSql = "select distinct a.FSecurityCode as AFSecurityCode" +
               ",a.FYearMonth as AFYearMonth" +
               ",a.FStoragedate as AFstoragedate,a.FportCode as AFportCode,a.FInvestType as AFInvestType" + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
               ",a.FCuryCode as AFCuryCode" +
               ",a.FCatType as AFCatType" +
               ",a.FAttrClsCode as AFAttrClsCode" +
               ",d.FPortCury as AFPortCury" +
               (analy1 ? ",a.FAnalysisCode1 as AFAnalysisCode1" : "") +
               (analy2 ? ",a.FAnalysisCode2 as AFAnalysisCode2" : "") +
               (analy3 ? ",a.FAnalysisCode3 as AFAnalysisCode3" : "") +
               ",(case when b.FSecurityCode is null then ' ' else b.FSecurityCode end) as BFSecurityCode " +
               " from (select * from " +
               pub.yssGetTableName("Tb_Stock_SecRecPay") +
               " where FPortCode in(" + portCodes + ") " +
               /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/	
//			   (statCodes.length()>0?(" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")"):"") +//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
               (statCodes.length()>0?(" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500)) + ")":"") +
               /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               //" and FTsfTypeCode = '06' and FSubTsfTypeCode = '06FI' and FStorageDate = " +
			   " and FTsfTypeCode in ('06','07') and FSubTsfTypeCode in ('06FI','07PLI') and FStorageDate = " +
               //modified by zhaoxianlin 20121205 #story 3208 银华卖空  增加应付借贷利息类型
               dbl.sqlDate(dDate) + " and FCheckState = 1) a " +
               //--------------------------------------------------------------
               " left join (select * from " +
               pub.yssGetTableName("Tb_Stock_Security") +
               " where FPortCode in(" +
               portCodes +")"+
               /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//			   (statCodes.length()>0?(") and FSecurityCode in("+operSql.sqlCodes(statCodes)):"") +//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
               (statCodes.length()>0?(" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500)) + ")":"") +
               /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               " and FCheckState = 1) b on a.FYearMonth = b.FYearMonth" +
               " and a.FStoragedate = b.FStoragedate and a.FPortCode = b.FPortCode and a.FSecurityCode = b.FSecurityCode " +
               (analy1 ? " and a.FAnalysisCode1 = b.FAnalysisCode1" : "") +
               (analy2 ? " and a.FAnalysisCode2 = b.FAnalysisCode2" : "") +
               (analy3 ? " and a.FAnalysisCode3 = b.FAnalysisCode3" : "") +
               //-------------------------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               
   
               " left join (select  FPortCode, FPortCury, FStorageInitDate from " +
               pub.yssGetTableName("Tb_Para_Portfolio") + 
               " where  FCheckState = 1 and FASSETGROUPCODE = " +
               dbl.sqlString(pub.getAssetGroupCode()) +") d on a.FPortCode = d.FPortCode";
         
         //end by lidaolong 
         
         rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
         while (rs.next()) {
            if (rs.getString("BFSecurityCode").trim().length() == 0) {
               
               secAdmin = new SecurityStorageAdmin();
			   secAdmin.setYssPub(pub);
				
               secStorage = new SecurityStorageBean();
               secStorage.setStrSecurityCode(rs.getString("AFSecurityCode"));
               secStorage.setStrStorageDate(YssFun.formatDate(rs.getDate(
                     "AFStoragedate"), "yyyy-MM-dd"));
               secStorage.setStrPortCode(rs.getString("AFPortCode"));
				secStorage.setInvestType(rs.getString("AFInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
               secStorage.setCatType(rs.getString("AFCatType"));
               secStorage.setAttrCode(rs.getString("AFAttrClsCode"));
               secStorage.setStrCuryCode(rs.getString("AFCuryCode"));
               if (analy1) {
                  secStorage.setStrFAnalysisCode1(rs.getString(
                        "AFAnalysisCode1"));
               }
               else {
                  secStorage.setStrFAnalysisCode1(" ");
               }
               if (analy2) {
                  secStorage.setStrFAnalysisCode2(rs.getString(
                        "AFAnalysisCode2"));
               }
               else {
                  secStorage.setStrFAnalysisCode2(" ");
               }
               if (analy3) {
                  secStorage.setStrFAnalysisCode3(rs.getString(
                        "AFAnalysisCode3"));
               }
               else {
                  secStorage.setStrFAnalysisCode3(" ");
               }
               if (rs.getString("AFYearMonth").trim().length() > 0) {
                  secStorage.setStrYearMonth(rs.getString("AFYearMonth"));
               }
               secStorage.setStrStorageCost("0");
               secStorage.setStrMStorageCost("0");
               secStorage.setStrVStorageCost("0");
               secStorage.setStrBaseCuryCost("0");
               secStorage.setStrMBaseCuryCost("0");
               secStorage.setStrVBaseCuryCost("0");
               secStorage.setStrPortCuryCost("0");
               secStorage.setStrMPortCuryCost("0");
               secStorage.setStrVPortCuryCost("0");
               secStorage.setStrStorageAmount("0");
               secStorage.setStrFreezeAmount("0");
               secStorage.setStrBaseCuryRate(YssFun.formatNumber(this.
                     getSettingOper().getCuryRate(dDate,
                                                  rs.getString("AFCuryCode"),
                                                  rs.getString("AFPortCode"),
                                                  YssOperCons.YSS_RATE_BASE),
                     "#,##0.##"));
               secStorage.setStrPortCuryRate(YssFun.formatNumber(this.
                     getSettingOper().getCuryRate(dDate,
                                                  rs.getString("AFPortCury"),
                                                  rs.getString("AFPortCode"),
                                                  YssOperCons.YSS_RATE_PORT),
                     "#,##0.##"));
               secStorage.checkStateId = 1;
               secAdmin.addList(secStorage);
               
               secAdmin.insert(dDate, dDate, portCodes, 
            		   			secStorage.getStrFAnalysisCode1(),
            		   			secStorage.getStrFAnalysisCode2(),
            		   			secStorage.getStrSecurityCode(), secStorage.getStrCuryCode());
            }
         }
         
         //-------2008.06.18 蒋锦 添加 证券应收应付库存统计完成后 将原币金额为 0 的数据的基础货币余额和组合货币余额都设为0------//
         String strUpdate = "";
         strUpdate = "UPDATE " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
               " SET FPortCuryBal = 0," +
               " FMPortCuryBal = 0," +
               " FVPortCuryBal = 0," +
               " FBaseCuryBal = 0," +
               " FMBaseCuryBal = 0," +
               " FVBaseCuryBal = 0," +
               //---------------2008.11.14 蒋锦 添加---------------------//
               //储存保留8位小数的原币，基础货币，本位币金额, 可用于计算估值增值汇兑损益
               //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
               " FBalF = 0," +
               " FBASECURYBALF = 0," +
               " FPORTCURYBALF = 0" +
               //-------------------------------------------------------//
               " WHERE " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'" +
               " AND FPortCode IN (" + this.portCodes + ")" +
               " AND FStorageDate = " + dbl.sqlDate(dDate) +
               " AND FBal = 0 " +
               /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//			   (statCodes.length()>0?(" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")"):"") +//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
               (statCodes.length()>0?(" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500)) + ")":"") +
               /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               " AND (FTsfTypeCode = '06' OR FTsfTypeCode = '07')";
         conn.setAutoCommit(false);
         bTrans = false;
         dbl.executeSql(strUpdate);
         conn.commit();
         bTrans = true;
         conn.setAutoCommit(true);
         //---------------------------------------------------------------------------------------------------------//
      }
      catch (Exception e) {
         throw new YssException("系统进行证券应收应付库存统计,在统计证券应收应付库存工作完成的后续处理时出现异常!\n",e);//by 曹丞 2009.01.22 统计证券应收应付库存工作完成的后续处理异常信息 MS00004 QDV4.1-2009.2.1_09A
      }
      finally {
         dbl.closeResultSetFinal(rs);
         dbl.endTransFinal(conn, bTrans);
      }

   }

   /**
    * 在统计之前向证券应收应付表中插入一些数据 sj add 20080217
    * @param dDate Date
    * @throws YssException
    */
   protected void beforeStatStorage(java.util.Date dDate) throws YssException {
        try {
//------------MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A 将生成收益的处理，移植回购业务处理里 ---
//            insPurchase(dDate); //设置回购应收应付的相关数据 sj
//-------------------------------------------------------------------------------------------
        } catch (Exception e) {
         throw new YssException("系统进行证券应收应付库存统计,在统计证券应收应付库存的前期处理工作时出现异常!\n", e);// by 曹丞2009.01.22 统计证券应收应付库存的前期处理工作信息异常 MS00004 QDV4.1-2009.2.1_09A
        }
   }

   private void insPurchase(java.util.Date dDate) throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double dPhIncome = 0;
      SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
      boolean analy1 = false;
      boolean analy2 = false;
      boolean analy3 = false;
      try {
         analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");//sj 判断分析代码 20080226
         analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
         analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
         strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade") +
               " a left join (select FFeeCode,FAccountingWay as FAW1 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") b on a.FFeeCode1 = b.FFeeCode" +
               //--------------------------------------------------------
               " left join (select FFeeCode,FAccountingWay as FAW2 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") c on a.FFeeCode2 = c.FFeeCode" +
               //--------------------------------------------------------
               " left join (select FFeeCode,FAccountingWay as FAW3 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") d on a.FFeeCode3 = d.FFeeCode" +
               //--------------------------------------------------------
               " left join (select FFeeCode,FAccountingWay as FAW4 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") e on a.FFeeCode4 = e.FFeeCode" +
               //--------------------------------------------------------
               " left join (select FFeeCode,FAccountingWay as FAW5 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") f on a.FFeeCode5 = f.FFeeCode" +
               //--------------------------------------------------------
               " left join (select FFeeCode,FAccountingWay as FAW6 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") g on a.FFeeCode6 = g.FFeeCode" +
               //--------------------------------------------------------
               " left join (select FFeeCode,FAccountingWay as FAW7 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") h on a.FFeeCode7 = h.FFeeCode" +
               //--------------------------------------------------------
               " left join (select FFeeCode,FAccountingWay as FAW8 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") i on a.FFeeCode8 = i.FFeeCode" +
               //--------------------------------------------------------
               " left join (select FSecurityCode as FSecurityCode_j,FCatCode,FSubCatCode from " +
               pub.yssGetTableName("Tb_Para_Security") +
               ") j on a.FSecurityCode = j.FSecurityCode_j" +
               //--------------------------------------------------------
               " where (FMatureDate " +
               " between " + dbl.sqlDate(dDate) + " and " +
               dbl.sqlDate(dDate) +
               " ) and FCheckState = 1 and FPortCode in (" + portCodes +")" +
               //2008.08.21 蒋锦 添加只取回购的数据 BUG: 0000510
               " AND FTradeTypeCode IN ('24', '25')" +
               " order by FNum";

         rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
         while (rs.next()) {
            dPhIncome = rs.getDouble("FAccruedinterest");
            if (rs.getInt("FAW1") == 0) { //计入成本
               dPhIncome = YssD.sub(dPhIncome,
                                    rs.getDouble("FTradeFee1"));
            }
            if (rs.getInt("FAW2") == 0) { //计入成本
               dPhIncome = YssD.sub(dPhIncome,
                                    rs.getDouble("FTradeFee2"));
            }
            if (rs.getInt("FAW3") == 0) { //计入成本
               dPhIncome = YssD.sub(dPhIncome,
                                    rs.getDouble("FTradeFee3"));
            }
            if (rs.getInt("FAW4") == 0) { //计入成本
               dPhIncome = YssD.sub(dPhIncome,
                                    rs.getDouble("FTradeFee4"));
            }
            if (rs.getInt("FAW5") == 0) { //计入成本
               dPhIncome = YssD.sub(dPhIncome,
                                    rs.getDouble("FTradeFee5"));
            }
            if (rs.getInt("FAW6") == 0) { //计入成本
               dPhIncome = YssD.sub(dPhIncome,
                                    rs.getDouble("FTradeFee6"));
            }
            if (rs.getInt("FAW7") == 0) { //计入成本
               dPhIncome = YssD.sub(dPhIncome,
                                    rs.getDouble("FTradeFee7"));
            }
            if (rs.getInt("FAW8") == 0) { //计入成本
               dPhIncome = YssD.sub(dPhIncome,
                                    rs.getDouble("FTradeFee8"));
            }
            insertSecPecPay(rs.getDate("FMatureDate"), //往证券应收应付款插入数据，回购收益
                            rs.getString("FPortCode"),
                            rs.getString("FSecurityCode"),
                            analy1?rs.getString("FInvMgrCode"):" ",//sj 判断分析代码 20080226
                            analy2?rs.getString("FBrokerCode"):" ", "",//sj 判断分析代码 20080226
                            rs.getString("FTradeTypeCode"),
                            dPhIncome,
                            rs.getDouble("FBaseCuryRate"),
                            rs.getDouble("FPortCuryRate"), secPayAdmin, rs.getString("FInvestType")); //增加投资类型 modify by wangzuochun 2009.08.15 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
         }
         secPayAdmin.setYssPub(pub);
         secPayAdmin.insert(dDate, dDate, YssOperCons.YSS_ZJDBLX_Fee + "," + YssOperCons.YSS_ZJDBLX_Income,
                            YssOperCons.YSS_ZJDBZLX_RE_Fee + "," + YssOperCons.YSS_ZJDBZLX_RE_Income,
                            portCodes, -99);//MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
      }
      catch (Exception e) {
         throw new YssException("设置回购应收应付数据出错!\n", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   private void insertSecPecPay(java.util.Date dTransDate,
                                String sPortCode, String sSecurityCode,
                                String sAnalysisCode1,
                                String sAnalysisCode2, String sAnalysisCode3,
                                String sTradeType, double dInterest,
                                double dBaseRate, double dPortRate,
                                SecRecPayAdmin secPayAdmin, String sInvestType) throws //增加投资类型参数 sInvestType,  modify by wangzuochun 2009.08.15 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
         YssException {
      if (dInterest == 0) {
         return;
      }
      SecPecPayBean secpecpay = new SecPecPayBean();
      SecurityBean sec = new SecurityBean();
      secpecpay.setTransDate(dTransDate);
      secpecpay.setStrPortCode(sPortCode);
      secpecpay.setInvMgrCode(sAnalysisCode1);
      secpecpay.setBrokerCode(sAnalysisCode2);
      secpecpay.setStrSecurityCode(sSecurityCode);
	  secpecpay.setInvestType(sInvestType);  //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;

      secpecpay.setStrCuryCode(operFun.getSecCuryCode(sSecurityCode));
      secpecpay.setMoney(dInterest);
      secpecpay.setMMoney(dInterest);
      secpecpay.setVMoney(dInterest);

      secpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
            dInterest, dBaseRate));
      secpecpay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
            dInterest, dBaseRate));
      secpecpay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
            dInterest, dBaseRate));
      secpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
            dInterest, dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            secpecpay.getStrCuryCode(), dTransDate, sPortCode));
      secpecpay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
            dInterest, dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            secpecpay.getStrCuryCode(), dTransDate, sPortCode));
      secpecpay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
            dInterest, dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            secpecpay.getStrCuryCode(), dTransDate, sPortCode));
      if (sTradeType.equalsIgnoreCase("24")) { //sj add  正回购
         sec.setYssPub(pub);
         sec.setSecurityCode(sSecurityCode);
         secpecpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee); //费用
         secpecpay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_RE_Fee);
      }
      else if (sTradeType.equalsIgnoreCase("25")) { //sj add  逆回购
         sec.setYssPub(pub);
         sec.setSecurityCode(sSecurityCode);

         //secpecpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
         secpecpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);//产生收入 sj 20080226
         secpecpay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_RE_Income);
      }
      secpecpay.setBaseCuryRate(dBaseRate);
      secpecpay.setPortCuryRate(dPortRate);
      secpecpay.checkStateId = 1;

      secPayAdmin.addList(secpecpay);
   }

	/** 合并太平版本代码
	 * 新方法，用于获取综合业务表中关联的应收应付、方向为流入的数据的编号及买入的业务编号
	 * by leeyu 20100417 QDV4中保2010年4月14日02_B MS01092
	 * @param dDate 业务日期
	 * @return 应收应付的编号
	 * @throws YssException
	 */
	private String getStgSecPaytNums(java.util.Date dDate) throws YssException{
		StringBuffer buf =new StringBuffer();
		String sqlStr="";
		ResultSet rs =null;
		try{
			//综合业务关联的编号
			sqlStr="select FNum from "+pub.yssGetTableName("Tb_Data_SecRecPay")+
			" where FNum in( select distinct FRelaNum from "+pub.yssGetTableName("Tb_Data_Integrated")+
			" where FNum in( select distinct FNum from "+pub.yssGetTableName("Tb_Data_Integrated")+
			" where FPortCode in("+operSql.sqlCodes(portCodes)+")"+
			/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
			* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//			(statCodes.length()>0?(" ) and FSecurityCode in("+operSql.sqlCodes(statCodes)):" ")+
			(statCodes.length()>0?(" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500)) + ")":" ")+
			/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
			"  and FTradeTypeCode in('80','81') and FOperDate = "+dbl.sqlDate(dDate)+
			" ) and FNumType='SecRecPay' ) and FInOut=1 and FCheckState=1 "+
			" union "+
			//买入编号
			" select FNum from "+pub.yssGetTableName("Tb_Data_SecRecPay")+
			" where FPortCode in("+operSql.sqlCodes(portCodes)+") and FTransDate="+dbl.sqlDate(dDate)+
			" and FTsfTypeCode='06' and FSubTsfTypeCode='06FI_B' and FCheckState=1 "+
			/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
			* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//			(statCodes.length()>0?(" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")"):" ");
			(statCodes.length()>0?(" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500)) + ")":" ");
			/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while(rs.next()){
				buf.append(rs.getString("FNum")).append(",");
			}
			if(buf.length()>1){
				buf.setLength(buf.length()-1);
			}
			if(buf.length()==0)
				buf.append(" ");
		}catch(Exception ex){
			throw new YssException("获取排除编号出错",ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return buf.toString();
	}
   /* public ArrayList getStorageStatData(java.util.Date dDate) throws
          YssException {
       String strSql = "", strTmpSql = "";
       ResultSet rs = null;
       SecRecPayBalBean secrecpaybal = null;
       ArrayList all = new ArrayList();
//      java.util.Date dDate = null;
       String strError = "统计证券应收应付库存出错";

       boolean analy1;//判断是否需要用分析代码；杨
       boolean analy2;
       boolean analy3;

       try {
//         for (int j = 0; j <= YssFun.dateDiff(dStartDate, dEndDate); j++) {
//            dDate = YssFun.addDay(dStartDate, j);

          analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
          analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
          analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
          portCodes =  operSql.sqlCodes(portCodes);//sj 20071116 若不加则portCodes不是以字符的形式显示在sql语句中
          if (YssFun.getMonth(dDate) == 1 &&
              YssFun.getDay(dDate) == 1) {
             strTmpSql = " and fyearmonth ='" +
                   YssFun.formatDate(dDate, "yyyy") + "00'";
          }
          else {
             strTmpSql = " and fyearmonth <>'" +
                   YssFun.formatDate(dDate, "yyyy") + "00'" +
                   " and FStorageDate=" +
                   dbl.sqlDate(YssFun.addDay(dDate, -1));
          }
          strSql = "select a.*, b.*, e.*, " + dbl.sqlDate(dDate) +
                " as FOperDate from(" +
    " select FTransDate, FPortCode, FSecurityCode, FTsfTypeCode, " +
                " FSubTsfTypeCode1 as FSubTsfTypeCode , FCuryCode, FCatType,FAttrClsCode," + //sj add 20071202
                (analy1?(dbl.sqlIsNull("FAnalysisCode1", "' '") +
                " as FAnalysisCode1,"):" ") +
                (analy2?(dbl.sqlIsNull("FAnalysisCode2", "' '") +
                " as FAnalysisCode2, "):" ") +
                (analy3?(dbl.sqlIsNull("FAnalysisCode3", "' '") +
                " as FAnalysisCode3,"):" ") +
                " sum(FMoney) as FMoney, " +
                " sum(FMMoney) as FMMoney, " +
                " sum(FVMoney) as FVMoney," +
                " sum(FBaseCuryMoney) as FBaseCuryMoney," +
                " sum(FMBaseCuryMoney) as FMBaseCuryMoney," +
                " sum(FVBaseCuryMoney) as FVBaseCuryMoney," +
                " sum(FPortCuryMoney) as FPortCuryMoney," +
                " sum(FMPortCuryMoney) as FMPortCuryMoney," +
                " sum(FVPortCuryMoney) as FVPortCuryMoney from " +
                " (select x.*," +
                //----------------------------------买入应收(应付)利息统计到应收(应付)利息库存
                " (case when FSubTsfTypeCode = '06FI_B' then '06FI' when FSubTsfTypeCode = '07FI_B' then '07FI'" +
                " else FSubTsfTypeCode end) as FSubTsfTypeCode1 from " +
                //------------------------------------------------
                pub.yssGetTableName("Tb_Data_SecRecPay") +
                " x) t1" +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
                " and FCheckState = 1 and (FTsfTypeCode in (" +
                //dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + "," +  收入不参与统计，只需在统计应收应付时进行扣除fazmm20071014
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + ", " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) +
                ", " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) + ", " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) + ")) " +
                " group by FTransDate, FPortCode, FSecurityCode" +
                (analy1?", FAnalysisCode1":" ") +
                (analy2?", FAnalysisCode2":" ") +
                (analy3?", FAnalysisCode3":" ") +
    ", FTsfTypeCode, FSubTsfTypeCode1, FCuryCode ,FCatType,FAttrClsCode" +
                " ) a full join " +
                //---------------------------------------------------------------------------------
                " (select FStorageDate, FPortCode as FPortCode2, FSecurityCode as FSecurityCode2, FTsfTypeCode as FTsfTypeCode2, " +
                dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
                " as FSubTsfTypeCode2, FCuryCode as FCuryCode2, " +
                (analy1?(dbl.sqlIsNull("FAnalysisCode1", "' '") +
                " as FAnalysisCode12, "):" ") +
                (analy2?(dbl.sqlIsNull("FAnalysisCode2", "' '") +
                " as FAnalysisCode22, "):" ") +
                (analy3?(dbl.sqlIsNull("FAnalysisCode3", "' '") +
                " as FAnalysisCode32, "):" ") +
                dbl.sqlIsNull("FBal", "0") + " as FBal, " +
                dbl.sqlIsNull("FMBal", "0") + " as FMBal, " +
                dbl.sqlIsNull("FVBal", "0") + " as FVBal, " +
                dbl.sqlIsNull("FBaseCuryBal", "0") + " as FBaseCuryBal, " +
                dbl.sqlIsNull("FMBaseCuryBal", "0") + " as FMBaseCuryBal, " +
                dbl.sqlIsNull("FVBaseCuryBal", "0") + " as FVBaseCuryBal, " +
                dbl.sqlIsNull("FPortCuryBal", "0") + " as FPortCuryBal, " +
                dbl.sqlIsNull("FMPortCuryBal", "0") + " as FMPortCuryBal, " +
                dbl.sqlIsNull("FVPortCuryBal", "0") +
                " as FVPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " where FCheckstate=1 " + strTmpSql +
                " and FPortCode in (" + portCodes + ")" +
                " )b on a.FPortCode=b.FPortCode2 and a.FSecurityCode=b.FSecurityCode2 and a.FTsfTypeCode=b.FTsfTypeCode2 and a.FSubTsfTypeCode=b.FSubTsfTypeCode2 " +
                (analy1?" and a.FAnalysisCode1=b.FAnalysisCode12 ":" ") +
                (analy2?" and a.Fanalysiscode2=b.FAnalysisCode22 ":" ") +
                (analy3?" and a.FanalysisCode3=b.FAnalysisCode32 ":" ");
          strSql = strSql + " left join ( select FTransDate as FTransDate3, FPortCode as FPortCode3, FSecurityCode as FSecurityCode3, FTsfTypeCode as FTsfTypeCode3, FCatType as FCatType3,FAttrClsCode as FAttrClsCode3," + //sj add 20071202
                dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
                " as FSubTsfTypeCode3, FCuryCode as FCuryCode3, " +
                (analy1?(dbl.sqlIsNull("FAnalysisCode1", "' '") +
                " as FAnalysisCode13,"):" ") +
                (analy2?(dbl.sqlIsNull("FAnalysisCode2", "' '") +
                " as FAnalysisCode23, "):" ") +
                (analy3?(dbl.sqlIsNull("FAnalysisCode3", "' '") +
                " as FAnalysisCode33,"):" ") +
                " sum(-FMoney) as FMoney3, " +
                " sum(-FMMoney) as FMMoney3, " +
                " sum(-FVMoney) as FVMoney3," +
                " sum(-FBaseCuryMoney) as FBaseCuryMoney3," +
                " sum(-FMBaseCuryMoney) as FMBaseCuryMoney3," +
                " sum(-FVBaseCuryMoney) as FVBaseCuryMoney3," +
                " sum(-FPortCuryMoney) as FPortCuryMoney3," +
                " sum(-FMPortCuryMoney) as FMPortCuryMoney3," +
                " sum(-FVPortCuryMoney) as FVPortCuryMoney3 from " +
                "(select FTransDate,FPortCode,FSecurityCode, FCuryCode,FCatType,FAttrClsCode," + //sj add 20071202
                (analy1?"FAnalysisCode1,":" ") +
                (analy2?"FAnalysisCode2,":" ") +
                (analy3?"FAnalysisCode3,":" ") +
                "(case when FTsfTypeCode = '02' " +
                " or (FTsfTypeCode = '07' and (FSubTsfTypeCode = '07FI' or FSubTsfTypeCode = '07FI_B')) " +//有应付利息时，就要把应收利息-应付利息
                " then '06' end) as FTsfTypeCode," +
                //处理结转的利息收入，把应收利息冲掉,应收应付库存表中应收利息'06'与应收应付数据表中利息收入'02'对应上。
                "(case when FSubTsfTypeCode = '1003' " +
                " or FSubTsfTypeCode = '07FI' or FSubTsfTypeCode = '07FI_B'" +
                " then '06FI' " +
    " when FSubTsfTypeCode = '1002' then '06DV'" +//加入股息收入冲减应收股息 胡昆  20071009
                " when FTsftypecode ='02' then '06'" + dbl.sqlJN() + dbl.sqlSubStr("FSubTsfTypeCode","3") +
                " when FTsftypecode ='03' then '07'" + dbl.sqlJN() + dbl.sqlSubStr("FSubTsfTypeCode","3") +
                " else FSubTsfTypeCode " +
                " end) as FSubTsfTypeCode," +
                //应收债券利息'06FI'和债券分红收入'1003'对上，便于取出记录，结转利息时将相应的金额减掉。
    "FMoney,FMMoney,FVMoney,FBaseCuryMoney,FMBaseCuryMoney,FVBaseCuryMoney," +
    "FPortCuryMoney,FMPortCuryMoney,FVPortCuryMoney,FCheckState from " +
                pub.yssGetTableName("Tb_Data_SecRecPay") +
                "  where FTsfTypeCode in ('02', '03','07')) t2" +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
                " and FCheckState = 1 " +
                " group by FTransDate, FPortCode, FSecurityCode" +
                (analy1?", FAnalysisCode1":" ") +
                (analy2?", FAnalysisCode2":" ") +
                (analy3?", FAnalysisCode3":" ") +
    ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode ,FCatType,FAttrClsCode" +
    " ) e on e.FPortCode3=b.FPortCode2 and e.FSecurityCode3=b.FSecurityCode2 " +
                (analy1?" and e.FAnalysisCode13=b.FAnalysisCode12 ":" ") +
                (analy2?" and e.Fanalysiscode23=b.FAnalysisCode22 ":" ") +
                (analy3?" and e.FanalysisCode33=b.FAnalysisCode32 ":" ") +
                " and e.FTsfTypeCode3=b.FTsfTypeCode2 and e.FSubTsfTypeCode3=b.FSubTsfTypeCode2 ";

          rs = dbl.openResultSet(strSql);
          while (rs.next()) {
             if ((rs.getString("FSecurityCode")+"").equalsIgnoreCase("1898") && (rs.getString("FTsfTypeCode")+"").equalsIgnoreCase("06")){
                int t = 1;
             }
             secrecpaybal = new SecRecPayBalBean();
             secrecpaybal.setDtStorageDate(rs.getDate("FOperDate"));
             if (rs.getDate("FTransDate") != null &&
                 rs.getDate("FStorageDate") == null) {
                secrecpaybal.setSPortCode(rs.getString("FPortCode"));
                secrecpaybal.setSSecurityCode(rs.getString("FSecurityCode"));
    secrecpaybal.setSAnalysisCode1((analy1?rs.getString("FAnalysisCode1"):" "));
    secrecpaybal.setSAnalysisCode2((analy2?rs.getString("FAnalysisCode2"):" "));
    secrecpaybal.setSAnalysisCode3((analy3?rs.getString("FAnalysisCode3"):" "));
                secrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                secrecpaybal.setSSubTsfTypeCode(rs.getString(
                      "FSubTsfTypeCode"));
                secrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
             }
             else {
                secrecpaybal.setSPortCode(rs.getString("FPortCode2"));
                secrecpaybal.setSSecurityCode(rs.getString("FSecurityCode2"));
    secrecpaybal.setSAnalysisCode1((analy1?rs.getString("FAnalysisCode12"):" "));
    secrecpaybal.setSAnalysisCode2((analy2?rs.getString("FAnalysisCode22"):" "));
    secrecpaybal.setSAnalysisCode3((analy3?rs.getString("FAnalysisCode32"):" "));
                secrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode2"));
                secrecpaybal.setSSubTsfTypeCode(rs.getString(
                      "FSubTsfTypeCode2"));
                secrecpaybal.setSCuryCode(rs.getString("FCuryCode2"));
             }
             secrecpaybal.setDBal(YssD.add(YssD.add(rs.getDouble("FMoney"),
                   rs.getDouble("FMoney3")), rs.getDouble("FBal")));
             secrecpaybal.setDMBal(YssD.add(YssD.add(rs.getDouble("FMMoney"),
                   rs.getDouble("FMMoney3")), rs.getDouble("FMBal")));
             secrecpaybal.setDVBal(YssD.add(YssD.add(rs.getDouble("FVMoney"),
                   rs.getDouble("FVMoney3")), rs.getDouble("FVBal")));

             secrecpaybal.setDBaseBal(YssD.add(YssD.add(rs.getDouble(
                   "FBaseCuryMoney"), rs.getDouble("FBaseCuryMoney3")),
                                               rs.getDouble("FBaseCuryBal")));
             secrecpaybal.setDMBaseBal(YssD.add(YssD.add(rs.getDouble(
                   "FMBaseCuryMoney"), rs.getDouble("FMBaseCuryMoney3")),
    rs.getDouble("FMBaseCuryBal")));
             secrecpaybal.setDVBaseBal(YssD.add(YssD.add(rs.getDouble(
                   "FVBaseCuryMoney"), rs.getDouble("FVBaseCuryMoney3")),
    rs.getDouble("FVBaseCuryBal")));

             secrecpaybal.setDPortBal(YssD.add(YssD.add(rs.getDouble(
                   "FPortCuryMoney"), rs.getDouble("FPortCuryMoney3")),
                                               rs.getDouble("FPortCuryBal")));
             secrecpaybal.setDMPortBal(YssD.add(YssD.add(rs.getDouble(
                   "FMPortCuryMoney"), rs.getDouble("FMPortCuryMoney3")),
    rs.getDouble("FMPortCuryBal")));
             secrecpaybal.setDVPortBal(YssD.add(YssD.add(rs.getDouble(
                   "FVPortCuryMoney"), rs.getDouble("FVPortCuryMoney3")),
    rs.getDouble("FVPortCuryBal")));

             all.add(secrecpaybal);
                //stmp = sYearMonth + "-" + rs.getDate("FOperDate") + "-" +
                      //sPorCode + "-" + sSecurityCode + "-" + sAnalysisCode1 +
                      //"-" +
              //sAnalysisCode2 + "-" + sAnalysisCode3 + "-" + sTsfTypeCode +
                      //"-" + sSubTsfTypeCode + "-" + sCuryCode;

          }
          dbl.closeResultSetFinal(rs);
//         }
          return all;
       }
       catch (Exception ye) {
          throw new YssException(strError + "\n" + ye.getMessage(), ye);
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
    }*/
	
	  /**
	    * add by wangzuochun 2010.12.15 BUG #639 系统处理债券计息时并未冲减买入利息，导致最终统计债券应收利息金额计算多 
	    * 如果当日有买入债券，且当日的买入日期小于这笔债券的计息起始日，则将此应收应付数据的买入利息置零；
	    * @param secRecPay
	    */
	   public void setBuyInterestMoney(java.util.Date dDate, SecRecPayBalBean secRecstorage) throws YssException{
		   String strSql = "";
		   ResultSet rs = null;
		   
		   try{
			   if (secRecstorage == null){
				   return;
			   }
			   
			   if("06".equals(secRecstorage.getSTsfTypeCode()) && "06FI".equals(secRecstorage.getSSubTsfTypeCode())) {

				   strSql = 
						" select fix.* from (select FSecurityCode from " + pub.yssGetTableName("tb_Para_Security") +
						" where FCatCode = 'FI' and FCheckState = 1 and FSecurityCode = " +
						dbl.sqlString(secRecstorage.getSSecurityCode()) + ") sec " +
						" join (select FSecurityCode, finsstartdate from " + pub.yssGetTableName("Tb_Para_FixInterest") + 
						" where FCheckState = 1 and FSecurityCode = " + dbl.sqlString(secRecstorage.getSSecurityCode()) +
						" and FInsStartDate > " + dbl.sqlDate(dDate)+ ") fix on sec.fsecuritycode = fix.fsecuritycode";
				   
				   rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
				   if (rs.next()){
					   secRecstorage.setDBal(0);
					   secRecstorage.setDMBal(0);
					   secRecstorage.setDVBal(0);
					   secRecstorage.setDBaseBal(0);
					   secRecstorage.setDMBaseBal(0);
					   secRecstorage.setDVBaseBal(0);
					   secRecstorage.setDPortBal(0);
					   secRecstorage.setDMPortBal(0);
					   secRecstorage.setDVPortBal(0);
				   }
				   dbl.closeResultSetFinal(rs);
			   }
		   }
		   catch(Exception e){
			   throw new YssException ("将当日买入债券利息置零出错！",e);
		   }
		   finally{
			   dbl.closeResultSetFinal(rs);
		   }
	   }

	/*删除当天所有证券借贷应收应付的数据
	 * *dDate 根据日期删除 add by zhouxiang 2011.1.26
	 */
	   private void DeleteSecLPayRecDate(Date dDate) throws YssException {
		ResultSet rs = null;
		Connection conn = dbl.loadConnection();
		try {
			String strDelete = "";
			String strSql = "";
			strDelete = "delete  from "
					+ pub.yssGetTableName("tb_stock_secrecpay")
					+ " a where a.fstoragedate=" + dbl.sqlDate(dDate)
					+ " and a.ftsftypecode in ('10','60')";
			dbl.executeSql(strDelete);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除证券借贷应收应付报错");
		}
	      //---------------------------------------------------
	}


}
