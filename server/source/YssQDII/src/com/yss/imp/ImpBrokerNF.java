package com.yss.imp;

import com.yss.util.YssException;
import com.yss.dsub.BaseDataSettingBean;
import java.sql.*;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.parasetting.CashAccountBean;

public class ImpBrokerNF
      extends BaseDataSettingBean {
   public ImpBrokerNF() {
   }

   public String saveBrokerData(String strValues) throws YssException {
      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      PreparedStatement ps = null;
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
      
      if (!dbl.yssTableExist("tb_Broker_data")) {
         try {
            dbl.executeSql("create table tb_Broker_data ("
                           + "Fdate Date not null,"
                           + " FCode varchar2(64),"
                           + " ISIN  varchar2(64) not null,"
                           + " Trade_Curr  varchar2(4) ,"
                           + " Trade_Nr  varchar2(8) ,"
                           + " BuySell  varchar2(2) not null,"
                           + " NumberOfShares decimal(18,2),"
                           + " Trade_Price decimal(18,12),"
                           + " Commission decimal(18,2),"
                           + " Fees decimal(18,2),"
                           + " Trade_Date Date not null,"
                           + " Value_Date Date not null,"
                           + " TitelName  varchar2(64) ,"
                           + " NetAmount decimal(18,2) ,"
                           + " Trade_Time varchar2(20) ,"
                           + " CountryBroker  varchar2(64) not null,"
                           + " combination  varchar2(64) not null,"
                           + " primary key (FDate,ISIN,Trade_Nr,BuySell,Trade_Date,Value_Date,CountryBroker,combination))");
            
         }
         catch (Exception e) {
            throw new YssException("保存券商接口数据失败！", e);
         }
      }

      try {
    	  if(1==1){
    		  throw new YssException("此接口已禁用，请使用其他交易数据接口！");
    	  }
         String[] strRows = strValues.split("\r\n");
         if (strRows.length < 2) {
            throw new YssException("上传的数据格式不正确，或没有有效记录！");
         }
         Date dateTrade = YssFun.toSqlDate(strRows[0].split(" ")[0]);
         String[] strField = strRows[1].split("\t");
         if (strField[0].equalsIgnoreCase("Delete")) {
            conn.setAutoCommit(false);
            bTrans = true;
            //要按照券商和币种进行读取。
            dbl.executeSql("delete from tb_Broker_data where Fdate = " +
                           dbl.sqlDate(dateTrade));
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            return "";
         }
         else if (strField[0].equalsIgnoreCase("ImpYhjHg")) {
            changeToTradeData(dateTrade,"","");
            return "";
         }
         else {
        	 
             String[] strList = strRows[2].split("\t");
             String strCurry = strList[3];
             String strBroker = strList[14];
             if(!strRows[strRows.length-1].equals("yes")){
            	 ResultSet rs = null;
                 String temp ="select y.fsecuritycode from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                 " y where y.FBargainDate = " + dbl.sqlDate(dateTrade)+" and y.fdatabirth='BROKER' and substr(y.fsecuritycode,instr(y.fsecuritycode,' ')+1,1)= '"+strBroker.split("-")[0].substring(0,1)+
                 "' and y.fbrokercode= '"+strBroker.split("-")[1]+"'";
                 rs=dbl.openResultSet(temp);
                 if(rs.next()){
                	 rs.close();
               	  return "yes|【"+strBroker+"】券商的交易数据已经导入，是否要重新导入覆盖原有数据";
                 }
                 
             }else{
            	 String[] temp = strRows;
            	 strRows= new String[strRows.length-1];
            	 for(int i =0;i<temp.length-1;i++){
            		 strRows[i]=temp[i];
            	 }
             }
        	 
        	 

            if (strBroker.indexOf("-") < 0) {
               throw new YssException("上传的券商信息格式不正确！");
            }
            String strSql = "insert into tb_Broker_data (Fdate,FCode,ISIN ,Trade_Curr,Trade_Nr,BuySell,NumberOfShares,Trade_Price,Commission,Fees,Trade_Date,Value_Date,TitelName,NetAmount,Trade_Time,CountryBroker,combination) "
                  + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            ps = dbl.openPreparedStatement(strSql);
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            conn.setAutoCommit(false);
            bTrans = true;
            //要按照券商和币种进行读取。
            /*
            dbl.executeSql("delete from tb_Broker_data where Fdate = " +
                           dbl.sqlDate(dateTrade) + " and Trade_Curr = '" +
                           strCurry + "' and CountryBroker = '" + strBroker +
                           "'");
            */

            
            dbl.executeSql("delete from tb_Broker_data where Fdate = " +
                           dbl.sqlDate(dateTrade) +" and countrybroker='"+strBroker+"'");

            for (int i = 2; i < strRows.length; i++) {
               String[] strCols = strRows[i].split("\t", -1);
               if (strCols.length < 15) {
                  throw new YssException("保存券商接口数据失败！\r\n数据格式不正确，请检查第" + i +
                                         "行。");
               }
               if (strCols[1].length() == 0) {
                  continue;
               }
               String strValue;
               if (YssFun.dateDiff(dateTrade, YssFun.toDate(strCols[10])) != 0) {
                  throw new YssException("保存券商接口数据失败！\r\n交易日期不正确，存在有非【" +
                                         strRows[0].split(" ")[0] + "】的日期。");
               }
               ps.setDate(1, dateTrade);
               ps.setString(2, strCols[0]);
               ps.setString(3, strCols[1]);
               /*
                            ps.setString(4, strCols[2]);
                            ps.setString(5, strCols[3]);
                            ps.setString(6, strCols[4]);
                            ps.setDouble(7, YssFun.toDouble(strCols[5]));
                            ps.setDouble(8, YssFun.toDouble(strCols[6]));
                            ps.setDouble(9, YssFun.toDouble(strCols[7]));
                            ps.setDouble(10, YssFun.toDouble(strCols[8]));
                            ps.setDate(11,YssFun.toSqlDate(strCols[9]));
                            ps.setDate(12,YssFun.toSqlDate(strCols[10]));
                            ps.setString(13, strCols[11]);*/

               ps.setString(4, strCols[3]);
               ps.setString(5, strCols[4]);
               ps.setString(6, strCols[5]);
               ps.setDouble(7, YssFun.toDouble(strCols[6]));
               ps.setDouble(8, YssFun.toDouble(strCols[7]));
               ps.setDouble(9, YssFun.toDouble(strCols[8]));
               ps.setDouble(10, YssFun.toDouble(strCols[9]));
               ps.setDate(11, YssFun.toSqlDate(strCols[10]));
               ps.setDate(12, YssFun.toSqlDate(strCols[11]));
               ps.setString(13, strCols[2]);

               ps.setDouble(14, YssFun.toDouble(strCols[12]));
               double dubValue = YssFun.toDouble(strCols[13]) * 86400;
               long lngValue = (long) dubValue;
               long lngHours = 0;
               long lngMinute = 0;
               long lngSecond = 0;
               lngHours = lngValue / 3600;
               lngValue = lngValue % 3600;
               lngMinute = lngValue / 60;
               lngValue = lngValue % 60;
               lngSecond = lngValue;
               strValue = lngHours + ":" + lngMinute + ":" + lngSecond;
               ps.setString(15, strValue);
               ps.setString(16, strCols[14]);
               ps.setString(17, " ");
               ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            changeToTradeData(dateTrade,strCurry,strBroker);
            return "";
         }
      }
      catch (YssException e){
    	  throw new YssException(e);
      }
      catch (BatchUpdateException bue) {
         throw new YssException("保存券商接口数据失败！", bue);
      }
      catch (Exception e) {
         throw new YssException("保存券商接口数据失败！", e);
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
         //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
         dbl.closeStatementFinal(ps);
         //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
      }
   }

   public void changeToTradeData(Date dateTrade,String strCurry,String strBroker) throws YssException {
      ResultSet rs = null;
      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      PreparedStatement ps = null;
      PreparedStatement psSub = null;
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
      try {
         conn.setAutoCommit(false);
         bTrans = true;
         int intCount = 1;

         String strNowDate = YssFun.formatDate(new java.util.Date(),
                                               "yyyyMMdd HH:mm:ss");
         BaseCashAccLinkDeal cashacc = new BaseCashAccLinkDeal();
         cashacc.setYssPub(pub);
         CashAccountBean caBean = null;

         String strSql = "insert into " + pub.yssGetTableName("Tb_Data_Trade") +
               "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
               " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
               " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTFACTOR," +
               " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FUNITCOST,FACCRUEDINTEREST," +
               " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
               " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
               " FTotalCost , FOrderNum, FDesc, FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime)" + //liyu 1128 新增三个字段
               " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
         //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
         ps = dbl.openPreparedStatement(strSql);
         //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
         strSql = "insert into " + pub.yssGetTableName("Tb_Data_SubTrade") +
               "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
               " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
               " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
               " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST," +
               " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
               " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
               " FTotalCost, FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost, " +
               " FOrderNum, FDataSource, FDesc, FCheckState, FCreator, FCreateTime, FCheckUser,FCheckTime,FFactSETTLEDATE,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate,FDataBirth)" + //liyu 1128 新增三个字段
               " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
         //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
         psSub = dbl.openPreparedStatement(strSql);
         //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//

         strSql = "delete from " + pub.yssGetTableName("Tb_Data_Trade") +
               " where fnum in (select distinct substr(fnum,1,15) fnum from " +
               pub.yssGetTableName("Tb_Data_SubTrade") +
               " t join " + pub.yssGetTableName("Tb_Para_Security") +
               " m on t.fsecuritycode=m.fsecuritycode" +
               " join tb_Broker_data k on k.trade_curr=m.ftradecury" +
               " and t.fbrokercode=substr(countrybroker,instr(countrybroker,'-')+1) " +
               " and t.fbargaindate=k.fdate where t.fbargaindate=" +
               dbl.sqlDate(dateTrade) +
               " and (fdatabirth='OMGEO' or fdatabirth='BROKER')and k.countrybroker='"+strBroker+"')";


         dbl.executeSql(strSql);

         strSql = "delete from " + pub.yssGetTableName("Tb_Data_SubTrade") +
               " where fnum in (select distinct fnum from " +
               pub.yssGetTableName("Tb_Data_SubTrade") +
               " t join " + pub.yssGetTableName("Tb_Para_Security") +
               " m on t.fsecuritycode=m.fsecuritycode" +
               " join tb_Broker_data k on k.trade_curr=m.ftradecury" +
               " and t.fbrokercode=substr(countrybroker,instr(countrybroker,'-')+1) " +
               " and t.fbargaindate=k.fdate where t.fbargaindate=" +
               dbl.sqlDate(dateTrade) +
               " and (fdatabirth='OMGEO' or fdatabirth='BROKER')and k.countrybroker='"+strBroker+"')";


         dbl.executeSql(strSql);


//         strSql = "Select FCode,ISIN ,Trade_Curr,BuySell,Trade_Date,Value_Date,CountryBroker,combination,sum(NumberOfShares) as Amount,sum(Trade_Price*NumberOfShares) as money ,sum(Commission) as Commission,sum(Fees) as Fees,sum(NetAmount) as NetAmount from "
//               + "tb_Broker_data where Trade_Date = " + dbl.sqlDate(dateTrade) + "  group by FCode,ISIN ,Trade_Curr,BuySell,Trade_Date,Value_Date,CountryBroker,combination ";
         strSql = "Select FCode,ISIN ,Trade_Curr,BuySell,Trade_Date,Value_Date,CountryBroker,combination,NumberOfShares Amount,Trade_Price*NumberOfShares money ,Commission Commission,Fees Fees,NetAmount NetAmount from "
               + "tb_Broker_data where Trade_Date = " + dbl.sqlDate(dateTrade)+" and countrybroker='"+strBroker+"'"; // + " and CountryBroker = '" + strBroker + "'";

         //先检查是否有未设定信息的股票
         StringBuffer buf = new StringBuffer();
//         rs = dbl.openResultSet("Select * from (" + strSql + ") a left join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " + pub.yssGetTableName("Tb_Para_Security") + " group by FSecurityCode,FMarketCode,FTradeCury ) b on a.ISIN = b.FMarketCode where b.FMarketCode is null");
         rs = dbl.openResultSet("Select * from (" + strSql + ") a left join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " +
                                pub.yssGetTableName("Tb_Para_Security") + " group by FSecurityCode,FMarketCode,FTradeCury ) b on a.FCode = b.FMarketCode where b.FMarketCode is null");
         while (rs.next()) {
            buf.append("[").append(rs.getString("FCode")).append("]");
         }
         rs.getStatement().close();
         rs = null;
         if (buf.length() > 0) {
            throw new YssException("系统中没有下列代码的证券" + buf.toString() + "，清检查设置！");
         }
         rs = dbl.openResultSet("Select max(fnum) from " +
                                pub.yssGetTableName("Tb_Data_SubTrade") +
                                " where FBargainDate = " +
                                dbl.sqlDate(dateTrade));
         if (rs.next()) {
            if (rs.getString(1) != null) {
               String strTmp = YssFun.right(rs.getString(1), 6);
               intCount = YssFun.toInt(strTmp) + 1;
            }
         }
         rs.getStatement().close();
         rs = null;
         //杨方修改 回购交易数据不用判断汇率数据
         if(!(strCurry.equals("") && strBroker.equals(""))){
//        	判断在导入交易数据的当天有没有汇率数据。
             rs = dbl.openResultSet("select * from "
    					+ pub.yssGetTableName("Tb_data_exchangerate")
    					+ " x where x.fcurycode = '"+strCurry+"' and x.fexratedate = "+ dbl.sqlDate(dateTrade));
    			if (!rs.next()) {
    				throw new YssException("系统中没有今天的汇率数据，请在导入交易数据前维护当天汇率数据！");
    			}else if(rs.getDouble("FEXRATE1")==0){
    				throw new YssException("系统中币种:"+strCurry+"的汇率数据为零，汇率数据不能为零,请维护实际的汇率数据！");
    			}
    		rs.getStatement().close();
    		rs = null;
         }
         
         
         
         rs = dbl.openResultSet(
               "select max(to_number(substr(fnum,10,6))) fnum from " +
               pub.yssGetTableName("Tb_Data_SubTrade") +
               " where substr(fnum,2,8)='" +
               YssFun.formatDate(dateTrade, "yyyyMMdd") + "'");
         if (rs.next()) {
            intCount = 1 + rs.getInt("fnum");
         }
         else {
            intCount = 1;
         }
         rs = null;

         //
         // strSql = "Select * from (" + strSql + ") a join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " + pub.yssGetTableName("Tb_Para_Security") + " group by FSecurityCode,FMarketCode,FTradeCury ) b on a.ISIN = b.FMarketCode ";
         strSql = "Select * from (" + strSql +
               ") a join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " +
               pub.yssGetTableName("Tb_Para_Security") + " where FCheckState = 1 group by FSecurityCode,FMarketCode,FTradeCury ) b on a.FCode = b.FMarketCode order by CountryBroker , FSecurityCode";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            String strNum = "T" +
                  YssFun.formatDate(rs.getDate("Trade_Date"), "yyyyMMdd") +
                  YssFun.formatNumber(intCount, "000000");
            String strCode = "";
            strCode = rs.getString("FSecurityCode");
//            if (rs.getString("FSecurityCode").indexOf(" ")>0){
//               strCode = rs.getString("FSecurityCode").split(" ")[0];
//            }else{
//               strCode = rs.getString("FSecurityCode");
//            }
            String strBrokerCode = rs.getString("CountryBroker").split("-")[1];
            String strBS = (rs.getString("BuySell").equalsIgnoreCase("B")) ?
                  "01" : "02";
            double dubBaseRate = this.getSettingOper().getCuryRate(dateTrade,
                  rs.getString("Trade_Curr"), "GFund",
                  YssOperCons.YSS_RATE_BASE);
            double dubPortRate = this.getSettingOper().getCuryRate(dateTrade,
                  pub.getPortBaseCury("GFund"), "GFund",// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                  YssOperCons.YSS_RATE_PORT); //组合汇率
            String strCashAccCode = " ";
            cashacc.setLinkParaAttr("002", "GFund", strCode, strBrokerCode,
                                    strBS, dateTrade);
            caBean = cashacc.getCashAccountBean();
            if (caBean != null) {
               strCashAccCode = caBean.getStrCashAcctCode();
            }
            ps.setString(1, strNum); //编号
            ps.setString(2, strCode); //证券代码
            ps.setString(3, "GFund"); //组合代码
            ps.setString(4, strBrokerCode); //券商代码
            ps.setString(5, "002"); //投资经理代码
            ps.setString(6, strBS); //交易方式
            ps.setString(7, strCashAccCode); //现金帐户代码
            ps.setString(8, " "); //所属分类
            ps.setDate(9, rs.getDate("Trade_Date")); //成交日期
            ps.setString(10, "00:00:00"); //成交时间
            ps.setDate(11, rs.getDate("Value_Date")); //结算日期
            ps.setString(12, "00:00:00"); //结算时间
            ps.setInt(13, 1); //自动结算
            ps.setDouble(14, dubPortRate); //组合汇率
            ps.setDouble(15, dubBaseRate); //基础汇率
            ps.setDouble(16, 1); //分配因子
            ps.setDouble(17, rs.getDouble("Amount")); //交易数量
            ps.setDouble(18, rs.getDouble("money") / rs.getDouble("Amount")); //交易价格
            ps.setDouble(19, YssFun.roundIt(rs.getDouble("money"), 2)); //交易金额
            ps.setDouble(20, rs.getDouble("money") / rs.getDouble("Amount")); //单位成本
            ps.setDouble(21, 0); //应计利息
            ps.setString(22, "COMMISSIONS"); //费用代码1
            ps.setDouble(23, rs.getDouble("Commission")); //交易费用1
            ps.setString(24, "Fee"); //费用代码2
            ps.setDouble(25, rs.getDouble("Fees")); //交易费用2
            ps.setString(26, " "); //费用代码3
            ps.setDouble(27, 0); //交易费用3
            ps.setString(28, " "); //费用代码4
            ps.setDouble(29, 0); //交易费用4
            ps.setString(30, " "); //费用代码5
            ps.setDouble(31, 0); //交易费用5
            ps.setString(32, " "); //费用代码6
            ps.setDouble(33, 0); //交易费用6
            ps.setString(34, " "); //费用代码7
            ps.setDouble(35, 0); //交易费用7
            ps.setString(36, " "); //费用代码8
            ps.setDouble(37, 0); //交易费用8
            ps.setDouble(38, rs.getDouble("NetAmount")); //投资总成本
            ps.setString(39, " "); //订单编号
            ps.setString(40, " "); //描述
            ps.setInt(41, 1); //审核状态
            ps.setString(42, pub.getUserName()); //创建人、修改人
            ps.setString(43, strNowDate); //创建、修改时间
            ps.setString(44, pub.getUserName()); //复核人
            ps.setString(45, strNowDate); //复核时间

            psSub.setString(1, strNum + "00000"); //编号
            psSub.setString(2, strCode); //证券代码
            psSub.setString(3, "GFund"); //组合代码
            psSub.setString(4, strBrokerCode); //券商代码
            psSub.setString(5, "002"); //投资经理代码
            psSub.setString(6, strBS); //交易方式
            psSub.setString(7, strCashAccCode); //现金帐户代码
            psSub.setString(8, " "); //所属分类
            psSub.setDate(9, rs.getDate("Trade_Date")); //成交日期
            psSub.setString(10, "00:00:00"); //成交时间
            psSub.setDate(11, rs.getDate("Value_Date")); //结算日期
            psSub.setString(12, "00:00:00"); //结算时间
            psSub.setInt(13, 1); //自动结算
            psSub.setDouble(14, dubPortRate); //组合汇率
            psSub.setDouble(15, dubBaseRate); //基础汇率

            psSub.setDouble(16, 1); //分配比例
            psSub.setDouble(17, rs.getDouble("Amount")); //原始分配数量

            psSub.setDouble(18, 1); //分配因子
            psSub.setDouble(19, rs.getDouble("Amount")); //交易数量
            psSub.setDouble(20, rs.getDouble("money") / rs.getDouble("Amount")); //交易价格
            psSub.setDouble(21, YssFun.roundIt(rs.getDouble("money"), 2)); //交易金额
            psSub.setDouble(22, 0); //应计利息
            psSub.setString(23, "COMMISSIONS"); //费用代码1
            psSub.setDouble(24, rs.getDouble("Commission")); //交易费用1
            psSub.setString(25, "Fee"); //费用代码2
            psSub.setDouble(26, rs.getDouble("Fees")); //交易费用2
            psSub.setString(27, " "); //费用代码3
            psSub.setDouble(28, 0); //交易费用3
            psSub.setString(29, " "); //费用代码4
            psSub.setDouble(30, 0); //交易费用4
            psSub.setString(31, " "); //费用代码5
            psSub.setDouble(32, 0); //交易费用5
            psSub.setString(33, " "); //费用代码6
            psSub.setDouble(34, 0); //交易费用6
            psSub.setString(35, " "); //费用代码7
            psSub.setDouble(36, 0); //交易费用7
            psSub.setString(37, " "); //费用代码8
            psSub.setDouble(38, 0); //交易费用8
            psSub.setDouble(39, rs.getDouble("NetAmount")); //投资总成本
            double dubCurr = YssFun.roundIt(dubBaseRate *
                                            rs.getDouble("NetAmount"), 2);
            psSub.setDouble(40, rs.getDouble("NetAmount")); //原币核算成本
            psSub.setDouble(41, rs.getDouble("NetAmount")); //原币管理成本
            psSub.setDouble(42, rs.getDouble("NetAmount")); //原币估值成本
            psSub.setDouble(43, dubCurr); //基础货币核算成本
            psSub.setDouble(44, dubCurr); //基础货币管理成本
            psSub.setDouble(45, dubCurr); //基础货币估值成本
            dubCurr = YssFun.roundIt(dubCurr / dubPortRate, 2);
            psSub.setDouble(46, dubCurr); //组合货币核算成本
            psSub.setDouble(47, dubCurr); //组合货币管理成本
            psSub.setDouble(48, dubCurr); //组合货币估值成本
            psSub.setString(49, " "); //订单编号
            psSub.setInt(50, 1); //数据来源
            psSub.setString(51, " "); //描述
            psSub.setInt(52, 1); //审核状态
            psSub.setString(53, pub.getUserName()); //创建人、修改人
            psSub.setString(54, strNowDate); //创建、修改时间
            psSub.setString(55, pub.getUserName()); //复核人
            psSub.setString(56, strNowDate); //复核时间
            psSub.setDate(57, rs.getDate("Value_Date")); //实际结算日期
            psSub.setString(58, strCashAccCode); //实际结算帐户
            psSub.setDouble(59, rs.getDouble("NetAmount")); //实际结算金额
            psSub.setDouble(60, 1); //兑换汇率
            psSub.setDouble(61, dubBaseRate); //实际结算基础汇率
            psSub.setDouble(62, dubPortRate); //实际结算组合汇率
            psSub.setString(63, "BROKER");
            ps.addBatch();
            psSub.addBatch();
            intCount++;
         }
         rs.getStatement().close();
         rs = null;
         ps.executeBatch();
         psSub.executeBatch();

         //杨方修改 避免在导入交易数据时将回购交易数据删除的情况.
         if(strCurry.equals("") && strBroker.equals("")){
        	 SaveYhjHg(dateTrade, intCount, strNowDate);
         }

         conn.commit();
         conn.setAutoCommit(true);
         bTrans = false;
      }
      catch (BatchUpdateException bue) {
         throw new YssException("保存券商接口数据失败！", bue);
      }
      catch (Exception e) {
         throw new YssException("保存券商接口数据失败！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
         dbl.endTransFinal(conn, bTrans);
         //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
         dbl.closeStatementFinal(ps,psSub);
         //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
      }
   }

   private void SaveYhjHg(Date dateTrade, int intCount, String strNowDate) throws
         Exception {
      ResultSet rs = null;
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
      PreparedStatement psSub = null;
      //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
      try {
         String strSql = "insert into " +
               pub.yssGetTableName("Tb_Data_SubTrade") +
               "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
               " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
               " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
               " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST," +
               " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
               " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
               " FTotalCost, FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost, " +
               " FOrderNum, FDataSource, FDesc, FCheckState, FCreator, FCreateTime, FCheckUser,FCheckTime,FFactSETTLEDATE,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate ," + //liyu 1128 新增三个字段
               " FMatureDate,FMatureSettleDate)" +
               " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
         dbl.executeSql("delete from " + pub.yssGetTableName("tb_data_subtrade") + " t   where FTradeTypeCode in ('25','26') and t.FPortCode = 'GFund' and t.FCashAccCode " +
         		"= 'ICBC-CNY' and t.fsecuritycode in (select distinct t2.fsecuritycode from " + pub.yssGetTableName("tb_data_subtrade") + " t left join " + pub.yssGetTableName("tb_para_security") + " t2 " +
         		"on t.fsecuritycode=t2.fsecuritycode where t2.fexchangecode='OTC')  and t.FBARGAINDATE = " + dbl.sqlDate(dateTrade));
         //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
         psSub = dbl.openPreparedStatement(strSql);
         //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
         strSql = "select a.*,b.fbrokercode, 'GFund' as FPortCode,'ICBC-CNY' as FCashAccCode , 0 as FZero , 1 as FOne , 'GFund' as FPortCode from "
               + "(select FZqdm,FJyDate,FGHDate,FPzDate,FJxTs,case when FHgFx = '融券' then '25' else '24' end as FHgFx ,FHglx,FJyje,FGhje,FSn,FHglv,FDsf ,FGhje - FJyje as FHgSy ,"
               + "'OTC REPO 1' as FFC1 , 0 as FTF1 , 'OTC REPO 2' as FFC2 , 200 as FTF2 , 'HHF' as FFC3, 260.5 as FTF3 , FJyje + 260.5 as  FJsJe "
               +
               "from extyh_hgjy where fjjdm = 'A001' and FJyDate = " + dbl.sqlDate(dateTrade) + " ) a left join " + pub.yssGetTableName("tb_para_broker") + " b "
               + "on a.fdsf = b.fbrokershortname ";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            String strNum = "T" + YssFun.formatDate(dateTrade, "yyyyMMdd") +
                  YssFun.formatNumber(intCount, "000000");
            String strCode = "";
            strCode = rs.getString("FZqdm");
            if (rs.getString("fbrokercode") == null) {
               throw new YssException("获取券商信息失败！请检查券商信息设定。");
            }
            String strBrokerCode = rs.getString("fbrokercode");
            String strBS = rs.getString("FHgFx");
            double dubBaseRate = 1.0;
            double dubPortRate = 1.0; //组合汇率
            String strCashAccCode = rs.getString("FCashAccCode");
            /*              cashacc.setLinkParaAttr("002","GFund",strCode,strBrokerCode,strBS,dateTrade);
                          caBean = cashacc.getCashAccountBean();
                          if (caBean != null) {
                             strCashAccCode = caBean.getStrCashAcctCode();
                          }
             */

            psSub.setString(1, strNum + "00000"); //编号
            psSub.setString(2, strCode); //证券代码
            psSub.setString(3, "GFund"); //组合代码
            psSub.setString(4, strBrokerCode); //券商代码
            psSub.setString(5, " "); //投资经理代码
            psSub.setString(6, strBS); //交易方式
            psSub.setString(7, strCashAccCode); //现金帐户代码
            psSub.setString(8, " "); //所属分类
            psSub.setDate(9, rs.getDate("FJyDate")); //成交日期
            psSub.setString(10, "00:00:00"); //成交时间
            psSub.setDate(11, rs.getDate("FPzDate")); //结算日期
            psSub.setString(12, "00:00:00"); //结算时间
            psSub.setInt(13, 1); //自动结算
            psSub.setDouble(14, dubPortRate); //组合汇率
            psSub.setDouble(15, dubBaseRate); //基础汇率

            psSub.setDouble(16, 1); //分配比例
            psSub.setDouble(17, 0); //原始分配数量

            psSub.setDouble(18, 1); //分配因子
            psSub.setDouble(19, 0); //交易数量
            psSub.setDouble(20, 0); //交易价格
            psSub.setDouble(21, YssFun.roundIt(rs.getDouble("FJyje"), 2)); //交易金额
            psSub.setDouble(22, YssFun.roundIt(rs.getDouble("FHgSy"), 2)); //应计利息
            psSub.setString(23, rs.getString("FFC1")); //费用代码1
            psSub.setDouble(24, rs.getDouble("FTF1")); //交易费用1
            psSub.setString(25, rs.getString("FFC2")); //费用代码2
            psSub.setDouble(26, rs.getDouble("FTF2")); //交易费用2
            psSub.setString(27, rs.getString("FFC3")); //费用代码3
            psSub.setDouble(28, rs.getDouble("FTF3")); //交易费用3
            psSub.setString(29, " "); //费用代码4
            psSub.setDouble(30, 0); //交易费用4
            psSub.setString(31, " "); //费用代码5
            psSub.setDouble(32, 0); //交易费用5
            psSub.setString(33, " "); //费用代码6
            psSub.setDouble(34, 0); //交易费用6
            psSub.setString(35, " "); //费用代码7
            psSub.setDouble(36, 0); //交易费用7
            psSub.setString(37, " "); //费用代码8
            psSub.setDouble(38, 0); //交易费用8
            psSub.setDouble(39, rs.getDouble("FJsJe")); //投资总成本

            psSub.setDouble(40, 0); //原币核算成本
            psSub.setDouble(41, 0); //原币管理成本
            psSub.setDouble(42, 0); //原币估值成本
            psSub.setDouble(43, 0); //基础货币核算成本
            psSub.setDouble(44, 0); //基础货币管理成本
            psSub.setDouble(45, 0); //基础货币估值成本
            psSub.setDouble(46, 0); //组合货币核算成本
            psSub.setDouble(47, 0); //组合货币管理成本
            psSub.setDouble(48, 0); //组合货币估值成本
            psSub.setString(49, " "); //订单编号
            psSub.setInt(50, 1); //数据来源
            psSub.setString(51, " "); //描述
            psSub.setInt(52, 0); //审核状态
            psSub.setString(53, pub.getUserCode()); //创建人、修改人
            psSub.setString(54, strNowDate); //创建、修改时间
            psSub.setString(55, pub.getUserCode()); //复核人
            psSub.setString(56, strNowDate); //复核时间
            psSub.setDate(57, rs.getDate("FGHDate")); //实际结算日期
            psSub.setString(58, strCashAccCode); //实际结算帐户
            psSub.setDouble(59, rs.getDouble("FJsJe")); //实际结算金额
            psSub.setDouble(60, 1); //兑换汇率
            psSub.setDouble(61, 1); //实际结算基础汇率
            psSub.setDouble(62, 1); //实际结算组合汇率
            psSub.setDate(63, rs.getDate("FGHDate")); //到期日期
            psSub.setDate(64, rs.getDate("FGHDate")); //到期结算日期
            psSub.addBatch();
            intCount++;
         }
         rs.getStatement().close();
         rs = null;
         psSub.executeBatch();
      }
      catch(YssException e){
    	  throw new RuntimeException(e);
      }
      catch (BatchUpdateException bue) {
         throw new YssException("保存券商接口数据失败！", bue);
      }
      catch (Exception e) {
         throw new YssException("保存券商接口数据失败！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
         //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
         dbl.closeStatementFinal(psSub);
         //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
      }
   }

}
