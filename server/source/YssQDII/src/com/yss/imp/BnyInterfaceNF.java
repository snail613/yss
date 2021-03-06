package com.yss.imp;

import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class BnyInterfaceNF
      extends BaseDataSettingBean {
   public BnyInterfaceNF() {
   }

   public void inBnyData(String strValue) throws YssException {

   }

   public String outBnyData(String strValue) throws YssException {
      String strList[] = strValue.split("\t", -1);
      java.util.Date dateYw = YssFun.toDate(strList[0].split(" ")[0]);
      String strPort = strList[1];
      String strType = strList[2];
      String strRetu = "";
      if (strType.equalsIgnoreCase("Security Instruction")) {
         strRetu = getSecurityInstruction(dateYw, strPort);
      }
      else if (strType.equalsIgnoreCase("Trade Blotter Sample")) {
         strRetu = getTradeBlotterSample(dateYw, strPort);
	  //start modify huangqirong 2013-07-31 STORY #4321 南方固定接口修改代码合并
      }else if (strType.equalsIgnoreCase("GuangDa_Security Instruction")){
    	  strRetu = getTradeBlotterSampleNew(dateYw, strPort);
	  //end modify huangqirong 2013-07-31 STORY #4321 南方固定接口修改代码合并
      }
      return strRetu;
   }

   private String getSecurityInstruction(java.util.Date dateYw, String strPort) throws
         YssException {
      StringBuffer buf = new StringBuffer();
      ResultSet rs = null;
      String strSql = "";
      try {
         /*
                    strSql = "Select * from (select * from tb_001_data_subtrade where fportcode = '" + strPort + "' and FBargainDate = " + dbl.sqlDate(dateYw) + ") a , "
          + " (select * from tb_001_Para_Security where fcheckstate = 1) b ,"
          + " (select * from Tb_001_Para_Cashaccount where FCheckstate = 1 ) c "
                  + "  where a.FSecurityCode = b.FSecurityCode and a.FCashAcccode = c.FCashAcccode";*/
         //alter by sunny 20071116  修改原因 应该是要取小于等于当前日期的最大日期
         strSql = "Select case when b.fcnvconent is null or b.fcnvconent='' then b.FEXCHANGECODE else b.fcnvconent end FEXCHANGECODE2,a.*,b.*,c.* from (select * from " +
               pub.yssGetTableName("tb_data_subtrade") + " where fcheckstate = 1 and fportcode = '" +
               strPort + "' and FBargainDate = " + dbl.sqlDate(dateYw) + ") a , (select sb.*,x.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
               pub.yssGetTableName("tb_para_security") +
               " where FCheckState=1 and FStartDate<= " + dbl.sqlDate(dateYw) +
               " group by FSecurityCode) sa join (select * from " +
               pub.yssGetTableName("tb_para_security") +
               " where FCheckState=1 )sb on sa.FSecurityCode = sb.FSecurityCode and sa.FStartDate = sb.FStartDate "
               //edit by songjie 2011.12.07 将写死的表名  改为通过pub获取
               +"left join (select a.fsrcconent,a.fcnvconent from " + pub.yssGetTableName("Tb_Dao_Dict") + " a where a.fdictcode = 'DCT000004') x on sb.fexchangecode=x.fsrcconent"
               +
               ") b, (select * from "  + pub.yssGetTableName("Tb_Para_Cashaccount") + " where FCheckstate = 1 ) c "
               +
               "  where a.FSecurityCode = b.FSecurityCode and a.FCashAcccode = c.FCashAcccode and FTradeCury <> 'CNY' and a.ftradetypecode in ('01','02')";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            //格式，字符从左开始填充，长度不足补空格，
            //数值靠右侧填充，长度不足补0，
            //日期型号，为空全部为0；
        	if(rs.getString("FISINCode").length()!=12){
        		throw new YssException(rs.getString("FsecurityCode")+"的ISIN码《"+rs.getString("FISINCode")+"》位数为："+rs.getString("FISINCode").length()+"，错误位数！请设置正确的12位！");
        	}
            buf.append("S"); //RECORD TYPE (CONSTANT "S")
            //南方约定只取前15位得主号
            buf.append(BNYChangeNF.formatString(YssFun.left(rs.getString("FNUM"),
                  15), 16)); //CUSTOMER REFERENCE NUMBER , 于trade blotter中的Trade SWIFT Number字段对应
            buf.append(BNYChangeNF.formatString(YssFun.left(rs.getString(
                  "FBankAccount"), 6), 13)); //BNY ACCOUNT NUMBER
                     buf.append(BNYChangeNF.formatString(getClearerAccount(rs.getString("FBrokerCode"), rs.getString("FEXCHANGECODE2")), 16)); //CLEARER ACCOUNT---对手方帐户
            buf.append(BNYChangeNF.formatString(18)); //FILLER
            buf.append(BNYChangeNF.formatString(BNYChangeNF.changeTransactionTypes(
                  rs.getString("FTradeTypeCode")), 20)); //TRANSACTION TYPE
            buf.append(BNYChangeNF.formatString(BNYChangeNF.getMarket(rs.getString(
                  "FEXCHANGECODE2")), 20)); //MARKET
            buf.append(BNYChangeNF.formatString("OTHER", 50)); //ASSET TYPE
            buf.append(BNYChangeNF.formatString(26)); //FILLER
            buf.append(BNYChangeNF.formatString(8)); //BNY BROKER NUMBER
            buf.append(BNYChangeNF.formatString(48)); //FILLER
            buf.append(BNYChangeNF.formatDate(rs.getDate("FSettleDate"))); //SETTLEMENT DATE
            buf.append(BNYChangeNF.formatString(BNYChangeNF.getCurrency(rs.
                  getString("FTradeCury")), 20)); //SETTLEMENT CURRENCY
            buf.append(BNYChangeNF.formatString(3)); //FILLER
            buf.append(BNYChangeNF.formatString(16)); //FX CUSTOMER REFERENCE NUMBER
            buf.append(BNYChangeNF.formatNumber(5)); //RELATED BLOCK DETAIL COUNTER
            buf.append(BNYChangeNF.formatNumber(5)); //RELATED COLLATERAL DETAIL COUNTER
            buf.append(BNYChangeNF.formatString(20)); //FX TRANSACTION TYPE
            buf.append(BNYChangeNF.formatNumber(16)); //CASH COLLATERAL AMOUNT (OPTIONS)
            buf.append(BNYChangeNF.formatNumber(4)); //FILLER, VALUE=0000
            buf.append(BNYChangeNF.formatNumber(rs.getDouble("FTradeFee1"), 11, 2)); //COMMISSION
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(16)); //CURRENT FACE (MBS ONLY)
            buf.append(BNYChangeNF.formatNumber(0, 15, 2)); //EXCHANGE FEE
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(16)); //FACTOR (MBS ONLY)
            buf.append(BNYChangeNF.formatNumber(rs.getDouble("FTotalCost"), 16, 3)); //FINAL MONEY
            buf.append(BNYChangeNF.formatNumber(rs.getDouble("FAccruedinterest"),
                                              15, 2)); //INTEREST
            buf.append(BNYChangeNF.formatNumber(1)); //INTEREST SIGN
            buf.append(BNYChangeNF.formatNumber(15)); //INVENTORY COST IN LOCAL CURRENCY
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(15)); //INVENTORY COST IN BASE CURRENCY
            buf.append(BNYChangeNF.formatNumber(7)); //FILLER, VALUE=0000000
            buf.append(BNYChangeNF.formatNumber(rs.getDouble("FTradeFee2"),9,2)); //OTHER CHARGES
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(rs.getDouble("FTradePrice"), 16,
                                              8)); //PRICE
            buf.append(BNYChangeNF.formatNumber(rs.getDouble("FTradeMoney"), 15,
                                              2)); //PRINCIPAL CASH
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(rs.getDouble("FTradeAmount"), 16,
                                              3)); //QUANTITY
            buf.append(BNYChangeNF.formatNumber(16)); //RATE
            buf.append(BNYChangeNF.formatNumber(10)); //FILLER, VALUE=0000000000
            buf.append(BNYChangeNF.formatNumber(5)); //SEC FEE
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(0, 16, 0)); //SETTLEMENT OVERRIDE AMOUNT
            buf.append(BNYChangeNF.formatNumber(16)); //STRIKE PRICE
            buf.append(BNYChangeNF.formatNumber(6)); //STRIKE PRICEFILLER, VALUE=000000
            buf.append(BNYChangeNF.formatNumber(9)); //TAXES
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(5)); //UNITS PER CONTRACT (OPTIONS)
            buf.append(BNYChangeNF.formatString(16)); //TURNAROUND RELATED CUSTOMER REFERENCE NUMBER
            buf.append(BNYChangeNF.formatString("", 16)); //CUSTOMER ACCOUNT AT BROKER (OPTIONS)
            buf.append(BNYChangeNF.formatString(4)); //AGENCY CODE
            buf.append(BNYChangeNF.formatString(20)); //CASH COLLATERAL CURRENCY
            buf.append(BNYChangeNF.formatString(1)); //CASH COLLATERAL DIRECTION
            buf.append(BNYChangeNF.formatString(1)); //CASHLOT FLAG
            buf.append(BNYChangeNF.formatString(8)); //BNY CLEARING BROKER
            buf.append(BNYChangeNF.formatString(48)); //FILLER
            buf.append(BNYChangeNF.formatString("N", 1)); //DTC ELIGIBLE FLAG
            buf.append(BNYChangeNF.formatString(15)); //EXCHANGE CODE
            buf.append(BNYChangeNF.formatNumber(8)); //EXPIRATION DATE (OPTIONS)
            buf.append(BNYChangeNF.formatString(1)); //FUNDS TYPE CODE
            buf.append(BNYChangeNF.formatString(1)); //HEDGE TYPE
            buf.append(BNYChangeNF.formatString(13)); //SWING RECEIVE ACCOUNT NUMBER
            buf.append(BNYChangeNF.formatString(1)); //FILLER
            buf.append(BNYChangeNF.formatString(20)); //CURRENCY OF INVENTORY COST IN BASE
            buf.append(BNYChangeNF.formatString(20)); //CURRENCY OF INVENTORY COST IN LOCAL
            buf.append(BNYChangeNF.formatNumber(8)); //ISSUE DATE
            buf.append(BNYChangeNF.formatString(3)); //ISSUER CODE

            buf.append(BNYChangeNF.formatString(30)); //BNY ISSUER NAME
            buf.append(BNYChangeNF.formatString(5)); //FILLER
            buf.append(BNYChangeNF.formatNumber(8)); //MATURITY DATE
            buf.append(BNYChangeNF.formatString("NORMAL", 35)); //SETTLEMENT METHOD
            buf.append(BNYChangeNF.formatString(31)); //FILLER
            buf.append(BNYChangeNF.formatString(10)); //OSDP COUNTRY
            buf.append(BNYChangeNF.formatString(5)); //EUROCLEAR/CEDEL PARTICIPANT NUMBER
            buf.append(BNYChangeNF.formatString(6)); //MBS POOL NUMBER
            buf.append(BNYChangeNF.formatString(4)); //PROJECT NOTE TYPE
            buf.append(BNYChangeNF.formatString(1)); //PUT/CALL TOGGLE

            buf.append(BNYChangeNF.formatString(35)); //REGISTRATION INSTRUCTION LINE 1
            buf.append(BNYChangeNF.formatString(35)); //REGISTRATION INSTRUCTION LINE 2
            buf.append(BNYChangeNF.formatString(35)); //REGISTRATION INSTRUCTION LINE 3
            buf.append(BNYChangeNF.formatString(35)); //REGISTRATION INSTRUCTION LINE 4
            buf.append(BNYChangeNF.formatString(35)); //REGISTRATION INSTRUCTION LINE 5
            buf.append(BNYChangeNF.formatString(32)); //REGISTRATION INSTRUCTION LINE 6

            buf.append(BNYChangeNF.formatString(1)); //ROLL COLLATERAL FLAG
            buf.append(BNYChangeNF.formatString(40)); //SECURITY DESCRIPTION
            buf.append(BNYChangeNF.formatString(rs.getString("FISINCode"), 12)); //SECURITY NUMBER
            buf.append(BNYChangeNF.formatString("ISIN", 12)); //SECURITY NUMBER TYPE
            buf.append(BNYChangeNF.formatString(20)); //CURRENCY OF SETTLEMENT OVERRIDE AMT

            buf.append(BNYChangeNF.formatString(1)); //SETTLEMENT OVERRIDE FLAG
            buf.append(BNYChangeNF.formatString(120)); //SPECIAL INSTRUCTIONS
            buf.append(BNYChangeNF.formatString(2)); //STATE CODE (MUNIS)
            buf.append(BNYChangeNF.formatString(1)); //TAXLOT FLAG
            buf.append(BNYChangeNF.formatString(11)); //TAXPAYER ID (TRANSFER & SHIP)

            buf.append(BNYChangeNF.formatDate(rs.getDate("FBargainDate"))); //TRADE DATE
            buf.append(BNYChangeNF.formatString(45)); //FILLER
            buf.append(BNYChangeNF.formatString(1)); //TURNAROUND
            buf.append(BNYChangeNF.formatString(1)); //SETTLE FREE FLAG
            buf.append(BNYChangeNF.formatString(1)); //FILLER
            buf.append(BNYChangeNF.formatString(40)); //WIRE INSTITUTION NAME
            buf.append(BNYChangeNF.formatString(16)); //WIRE INSTITUTION ID
            buf.append(BNYChangeNF.formatString(40)); //WIRE ACCOUNT NAME
            buf.append(BNYChangeNF.formatString(16)); //WIRE ACCOUNT ID
            buf.append(BNYChangeNF.formatString(getBrokerIDType(rs.
                  getString("FBrokerCode"), rs.getString("FEXCHANGECODE2")), 1)); //Non-FINS Broker ID Type

            buf.append(BNYChangeNF.formatString(getBrokerID(rs.
                  getString("FBrokerCode"), rs.getString("FEXCHANGECODE2")), 20)); //Non-FINS Trading Broker ID
            buf.append(BNYChangeNF.formatString(getClearerID(rs.
                  getString("FBrokerCode"), rs.getString("FEXCHANGECODE2")), 20)); //Non-FINS Clearing Broker ID
            buf.append(BNYChangeNF.formatString(88)); //FILLER
            buf.append(BNYChangeNF.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 1
            buf.append(BNYChangeNF.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 1
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 1
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT BASE 1
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(8)); //TAXLOT/CASHLOT DATE 1

            buf.append(BNYChangeNF.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 2
            buf.append(BNYChangeNF.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 2
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 2
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT BASE 2
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(8)); //TAXLOT/CASHLOT DATE 2

            buf.append(BNYChangeNF.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 3
            buf.append(BNYChangeNF.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 3
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 3
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT BASE 3
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(8)); //TAXLOT/CASHLOT DATE 3

            buf.append(BNYChangeNF.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 4
            buf.append(BNYChangeNF.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 4
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 4
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT BASE 4
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(8)); //TAXLOT/CASHLOT DATE 4

            buf.append(BNYChangeNF.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 5
            buf.append(BNYChangeNF.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 5
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 5
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(15)); //TAXLOT/CASHLOT BASE 5
            buf.append(BNYChangeNF.formatNumber(1)); //FILLER, VALUE=0
            buf.append(BNYChangeNF.formatNumber(8)); //TAXLOT/CASHLOT DATE 5

            buf.append(BNYChangeNF.formatString(1)); //PRICE/DISCOUNT FLAG
            buf.append(BNYChangeNF.formatString(120)); //SUB-CUSTODIAN SPECIAL INSTRUCTIONS
            buf.append(BNYChangeNF.formatString(4)); //STAMP DUTY
            buf.append(BNYChangeNF.formatString(getBrokerAccount(rs.
                  getString("FBrokerCode"), rs.getString("FEXCHANGECODE2")), 34)); //CLIENT ACCOUNT AT DEAG/REAG
            buf.append(BNYChangeNF.formatString(BNYChangeNF.getPlaceOfSettlement(rs.
                  getString("FEXCHANGECODE2")), 11)); //PLACE OF SETTLEMENT
            buf.append(BNYChangeNF.formatString(BNYChangeNF.getPlaceOfSafekeeping(
                  rs.getString("FEXCHANGECODE2")), 11)); //PLACE OF SAFEKEEPING
            buf.append(BNYChangeNF.formatNumber(18)); //EXPANDED QUANTITY
            buf.append(BNYChangeNF.formatNumber(5)); //FILLER, VALUE = 00000

            buf.append(BNYChangeNF.formatString(rs.getString("FEXCHANGECODE2"), 2)); //PLACE OF SETTLEMENT MARKET CODE
            buf.append(BNYChangeNF.formatString(rs.getString("FEXCHANGECODE2"), 2)); //PLACE OF SAFEKEEPING MARKET CODE
            buf.append(BNYChangeNF.formatString(4)); //CHANGE OF BENEFICIAL OWNERSHIP / REGISTRATION INDICATOR
            buf.append(BNYChangeNF.formatString(4)); //SWIFT SETTLEMENT METHOD
            buf.append(BNYChangeNF.formatString(8)); //48 HOUR MBS UPDATE
            buf.append(BNYChangeNF.formatString(getCleareIDType(rs.
                  getString("FBrokerCode"), rs.getString("FEXCHANGECODE2")), 1)); //Non-FINS CLEARING BROKER ID Type
            buf.append("\r\n");
         }
         if (buf.length() > 0) {
            buf.setLength(buf.length() - 2);
         }
      }
      catch (Exception e) {
         throw new YssException("获取Security Instruction失败！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }

      return buf.toString();
   }

   private String getTradeBlotterSample(java.util.Date dateYw, String strPort) throws
         YssException {
      StringBuffer buf = new StringBuffer();
      ResultSet rs = null;
      try {
         //交易业务------------------------
         //表头
         buf.append("Account/Fund/Portfolio Number").append("\t");
         buf.append("Trade SWIFT Number").append("\t");
         buf.append("Security Number/Identifier (e.g., Cusip, Sedol, ISIN)").
               append("\t");
         buf.append("Security Number/Identifier (e.g., Cusip, Sedol, ISIN)").
               append("\t");
         buf.append("Security Description/Name").append("\t");
         buf.append("Currency").append("\t");
         buf.append("Transaction Code (e.g., Buy, Sell, Cover, Short, etc.)").
               append("\t");
         buf.append("Effective Date").append("\t");
         buf.append("Trade Date").append("\t");
         buf.append("Contractual Settlement Date").append("\t");
         buf.append("Actual Settlement Date").append("\t");
         buf.append("Original Face").append("\t");
         buf.append("Shares/Par/Quantity").append("\t");
         buf.append("Price Per Share").append("\t");
         buf.append("Commission").append("\t");
         buf.append("Fee/Trade Expense/Tax").append("\t");
         buf.append("Principal Amount").append("\t");
         buf.append("Accrued Income (e.g., Interest)").append("\t");
         buf.append("Net Settlement Amount (must specify: Local or Base)").
               append("\t");
         buf.append("Cost").append("\t");
         buf.append("Cancel (i.e., is this a cancelled trade?: Y/N)").append(
               "\t");
         buf.append("Amortisation Amount").append("\t");
         buf.append("Initial Margin Amount").append("\t");
         buf.append("Broker").append("\t");
         //-------------工行调整接口
         buf.append("Place of Settlement").append("\r\n");
         //内容
         String strSql =
               "(select * From " + pub.yssGetTableName("Tb_Data_Subtrade") + " where FCheckState = 1 and FPortCode = '" +
               strPort + "' and FBargainDate = " + dbl.sqlDate(dateYw) + ") a"; 
         //edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
		 //在代码中增加了组合代码的资产代码获取，修改原来的默认 202801资产代码类型！不满足多组合情况 
         strSql = "select a.*,a.ftrademoney/a.ftradeamount ftradeprice2 , b.FSecurityName,FTradeCury,b.FDesc as bDesc,b.FISINCode,case when x.fcnvconent is null or x.fcnvconent='' then b.FEXCHANGECODE else x.fcnvconent end FEXCHANGECODE,p.fassetcode from " +
               strSql + " join " + "(select c.* from "+pub.yssGetTableName("Tb_Para_Security")+" c join "+
               "(select fsecuritycode, max(fstartdate) fstartdate from "+pub.yssGetTableName("Tb_Para_Security")+" where FCheckState = 1 group by fsecuritycode) d "+
               "on c.fsecuritycode =  d.fsecuritycode and c.fstartdate = d.fstartdate where c.fcheckstate=1) b "+
               " left join (select a.fsrcconent,a.fcnvconent from " + pub.yssGetTableName("Tb_Dao_Dict") + " a where a.fdictcode = 'DCT000004') x on b.fexchangecode=x.fsrcconent "+
               //edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
			   " on a.FSecuritycode = b.FSecurityCode left join (select * from "+pub.yssGetTableName("tb_para_portfolio")+") p on p.fportcode=a.fportcode where FTradeTypeCode in ('01','02') order by FTradeCury,FBrokerCode,FNUM";        //CTQ MODIFY 2007-11-20
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
        	//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B 2011-11-24 罗侠 修改，获取组合资产代码为第一列
        	buf.append(rs.getString("fassetcode")).append("\t");
            buf.append(YssFun.left(rs.getString("FNUM"),
                    15)).append("\t"); //SWIFT
            buf.append(rs.getString("FSecurityCode")).append("\t");
            buf.append(rs.getString("FISINCode")).append("\t");
            buf.append( (rs.getString("bDesc") != null ? rs.getString("bDesc") :
                         "")).append("\t");
            buf.append(rs.getString("FTradeCury")).append("\t");
            String strTradeType = "";
            if (rs.getString("FTradeTypeCode").equalsIgnoreCase("01")) {
               strTradeType = "BUY";
            }
            else if (rs.getString("FTradeTypeCode").equalsIgnoreCase("02")) {
               strTradeType = "SELL";
            }
            buf.append(strTradeType).append("\t"); //Accrued Income (e.g., Interest)
            buf.append(YssFun.formatDate(rs.getString("FBargainDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FBargainDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(" ").append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeAmount"),
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
                                           "0.00")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradePrice2"),
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
                                           "0.00000000")).append("\t");     //CTQ MODIFY 2007-11-20
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeFee1"),
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
                                           "0.00")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeFee2"),
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
                                           "0.00")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeMoney"),
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
                                           "0.00")).append("\t");
            buf.append("0.00").append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTotalCost"),
			//---edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B start---//
                                           "0.00")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FCost"), "0.00")).
			//---edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B end---//
                  append("\t");
            buf.append("N").append("\t");
            buf.append("0.00").append("\t");
            buf.append("0.00").append("\t");
            //---------------------工行调整接口Broker字段输出改为BrokerID
//            buf.append(rs.getString("FBrokerCode")).append("\r\n");
            buf.append(BNYChangeNF.formatString(getBrokerID(rs.
                  getString("FBrokerCode"), rs.getString("FExchangeCode")), 11,
                                              "X")).append("\t");
            buf.append(BNYChangeNF.formatString(BNYChangeNF.getPlaceOfSettlement(rs.
                  getString("FExchangeCode")), 11, "X")).append("\r\n");
         }
         rs.getStatement().close();
         rs = null;
         buf.setLength(buf.length() - 2);
         buf.append("\f");

         //回购业务------------------------
         //表头
         buf.append("Reference").append("\t");
         buf.append("Transaction Type").append("\t");
         buf.append("Market").append("\t");
         buf.append("Trade Date").append("\t");
         buf.append("First Settlement Date").append("\t");
         buf.append("Final Settlement Date").append("\t");
         buf.append("Currency").append("\t");
         buf.append("Day Count").append("\t");
         buf.append("Interest rate %").append("\t");
         buf.append("Amount Transacted").append("\t");
         buf.append("Market value of collateral received").append("\t");
         buf.append("Bank Charges").append("\t");
         buf.append("Trade Charges-Front Office").append("\t");
         buf.append("Trade Charges-Back Office").append("\t");
         buf.append("Maturity Amount (P+I)").append("\t");
         buf.append("Counterparty").append("\t");
         buf.append("Identifier of the Underlying Security for the Repo").append("\r\n");
        
         //内容
		 //---edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B start---//
         strSql ="(select ftradetypecode,fnum,fbrokercode, fsecuritycode,FBargainDate,FSettleDate,FMatureDate,fmaturesettledate,FTradeMoney,FTradeFee3,FTradeFee2,FTradeFee1,Faccruedinterest From " + pub.yssGetTableName("Tb_Data_Subtrade") + " where FCheckState = 1 and FPortCode = '" +
               strPort + "' and FBargainDate = " + dbl.sqlDate(dateYw) 
               +"union select t.ftradetypecode,t.fnum,t.faffcorpcode fbrokercode,t.fsecuritycode, FBargainDate,FSettleDate,FMatureDate,fmaturesettledate,FTradeMoney,260.5 FTradeFee3, t.fsetservicefee FTradeFee2,t.ftradehandlefee FTradeFee1,t.fpurchasegain Faccruedinterest From " + pub.yssGetTableName("Tb_data_purchase") + " t where FCheckState = 1 and FPortCode = '" +
               strPort + "' and FBargainDate = " + dbl.sqlDate(dateYw)
               + ") a";
         strSql = "select dd.fdayofyear dayofyear, round(a.faccruedinterest/a.ftrademoney*dd.fdayofyear/(a.fmaturesettledate-a.FSettleDate)*100,10) rate, a.*, b.FSecurityName,b.FTradeCury,b.FDesc as bDesc,b.FISINCode, FEXCHANGECODE,cc.FBrokerName  from " +
         //---edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B end---//
			   strSql + " join " + "(select c.* from "+pub.yssGetTableName("Tb_Para_Security")+" c join "+
               "(select fsecuritycode, max(fstartdate) fstartdate from "+pub.yssGetTableName("Tb_Para_Security")+" where FCheckState = 1 group by fsecuritycode) d "+
               "on c.fsecuritycode =  d.fsecuritycode and c.fstartdate = d.fstartdate where c.fcheckstate=1) b "+
               //edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
			   "on a.FSecuritycode = b.FSecurityCode left join " + pub.yssGetTableName("tb_para_broker") + " cc on a.fbrokercode = cc.fbrokercode "+
               " join (select t.fsecuritycode,tt.fdayofyear from " + pub.yssGetTableName("tb_para_purchase") + " t left join " + pub.yssGetTableName("tb_para_period") + " tt on t.fperiodcode=tt.fperiodcode) dd on dd.fsecuritycode= a.fsecuritycode"+
               " where FTradeTypeCode in ('25') order by b.FTradeCury,cc.FBrokerCode,a.FNUM";        //CTQ MODIFY 2007-11-20
         rs = dbl.openResultSet(strSql);
         int temp = 0;
         while (rs.next()) {
		    //edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
            buf.append(rs.getString("fnum")).append("\t");
            buf.append("Reverse Repo").append("\t");
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
            buf.append((rs.getString("FEXCHANGECODE").equals("SH") ||rs.getString("FEXCHANGECODE").equals("CG"))?"SSCECNS1XXX":"XCFECNS1XXX").append("\t");
            buf.append(YssFun.formatDate(rs.getString("FBargainDate"),
            "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
            "yyyy-MM-dd")).append("\t");
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
            buf.append(YssFun.formatDate(rs.getString("fmaturesettledate"),
            "yyyy-MM-dd")).append("\t");
            buf.append(rs.getString("FTradeCury")).append("\t");
            buf.append("Act/"+rs.getString("dayofyear")).append("\t");	
            //---edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B start---//
            buf.append(YssFun.formatNumber(rs.getDouble("rate"),"0.0000000000")).append("\t"); 
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeMoney"),"0.00")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeMoney"),"0.00")).append("\t");
            
            double tmp=0;
            
            if(rs.getDouble("FTradeMoney")>10000000){
            	tmp=200*1.3+0.5;
            }else if(rs.getDouble("FTradeMoney")>1000000 && rs.getDouble("FTradeMoney")<=10000000){
            	tmp=rs.getDouble("FTradeMoney")*2/100000*1.3+0.5;
            }else if(rs.getDouble("FTradeMoney")>500000 && rs.getDouble("FTradeMoney")<=1000000){
            	tmp=20*1.3+0.5;
            }else if(rs.getDouble("FTradeMoney")>100000 && rs.getDouble("FTradeMoney")<=500000){
            	tmp=15*1.3+0.5;
            }else if(rs.getDouble("FTradeMoney")>10000 && rs.getDouble("FTradeMoney")<=100000){
            	tmp=10*1.3+0.5;
            }else{
            	tmp=5*1.3+0.5;
            }
            
            buf.append(YssFun.formatNumber((rs.getString("FEXCHANGECODE").equals("SH") ||rs.getString("FEXCHANGECODE").equals("CG"))?0:tmp,"0.00")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeFee1"),"0.00")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeFee2"),"0.00")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeMoney")+rs.getDouble("Faccruedinterest"),
                                           "0.00")).append("\t");
            buf.append((rs.getString("FEXCHANGECODE").equals("SH") ||rs.getString("FEXCHANGECODE").equals("CG"))?"N.A.":rs.getString("fbrokername")).append("\t");
            //---edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B end---//
			buf.append("N.A.").append("\r\n");
         }
         rs.getStatement().close();
         rs = null;
         buf.setLength(buf.length() - 2);
         buf.append("\f");

         


         //存款业务
         buf.append("Effective Date").append("\t");
         buf.append("Trade Date").append("\t");
         buf.append("Contractual Settlement Date").append("\t");
         buf.append("Actual Settlement Date").append("\t");
         buf.append("Maturity Date").append("\t");
         buf.append("Deposit Currency").append("\t");
         buf.append("Deposit Amount").append("\t");
         buf.append("Interest Rate").append("\t");
         buf.append("Broker").append("\r\n");
         strSql = "Select a.* , b.fcurycode,c.FPervalue from "
               +
               "(select * From " + pub.yssGetTableName("Tb_Cash_Saving") + " where FCheckState = 1 and FPortCode = '" +
               strPort + "' and FSavingDate = " + dbl.sqlDate(dateYw) +
               ") a , "
               +
               "(select * From " +  pub.yssGetTableName("Tb_Para_CashAccount") + " where FCheckState = 1) b , "
               +
               "(select * From " + pub.yssGetTableName("Tb_Para_Performula_Rela") + " where FCheckState = 1) c "
               +
               "where a.fcashacccode = b.fcashacccode and a.FFormulaCode = c.Fformulacode";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            buf.append(YssFun.formatDate(rs.getString("FSavingDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSavingDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSavingDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSavingDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FMatureDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(rs.getString("fcurycode")).append("\t");
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
            buf.append(YssFun.formatNumber(rs.getDouble("FMoney"), "0.00")).
                  append("\t");
            buf.append(rs.getDouble("FPervalue")).append("\t");
            buf.append("").append("\r\n");
         }
         rs.getStatement().close();
         rs = null;
         buf.append("\f");

         //换汇业务
         buf.append("Effective Date").append("\t");
         buf.append("Trade Date").append("\t");
         buf.append("Contractual Settlement Date").append("\t");
         buf.append("Actual Settlement Date").append("\t");
         buf.append("Buy/Receive Currency (Type)").append("\t");
         buf.append("Buy Currency Amount").append("\t");
         buf.append("Sell/Deliver Currency (Type)").append("\t");
         buf.append("Sell Currency Amount").append("\t");
         buf.append("Valuation Rate").append("\t");
         buf.append("Broker").append("\r\n");
         strSql =
               "select * From " + pub.yssGetTableName("Tb_Data_RateTrade") +" where FCheckState = 1 and FPortCode = '" +
               strPort + "' and FTradeDate = " + dbl.sqlDate(dateYw);
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            buf.append(YssFun.formatDate(rs.getString("FTradeDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FTradeDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(rs.getString("FBCuryCode")).append("\t");
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
            buf.append(YssFun.formatNumber(rs.getDouble("FBMoney"), "0.00")).
                  append("\t");
            buf.append(rs.getString("FSCuryCode")).append("\t");
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
            buf.append(YssFun.formatNumber(rs.getDouble("FSMoney"), "0.00")).
                  append("\t");
            buf.append(rs.getDouble("FExCuryRate")).append("\t");
            String strAcc =  rs.getString("FBCashAccCode").toUpperCase();
            if (strAcc.indexOf("ICBC") >= 0 )  {
               buf.append("ICBC");
            }else if (strAcc.indexOf("BONY") >=0 ) {
               buf.append("BONY");
            }
            buf.append("\r\n");
         }
         rs.getStatement().close();
         rs = null;
         //远期
         strSql = "select d.*,e.FCashAccCode from "
             + "(select a.FNum , a.FTradeDate as FTradeDate,a.FSettleDate as FSettleDate,a.FMatureDate as FMatureDate,b.FBuyCury as FBuyCury,b.FSaleCury as FSaleCury, "
             + "a.FMatureMoney as FMatureMoney,a.FTradeAmount as FTradeAmount,a.FTradePrice as FTradePrice "
             + "from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + " a," + pub.yssGetTableName("Tb_Para_Forward") + " b where a.FCheckState = 1 and a.FPortCode = '"
             + strPort + "' and FTradeDate = " + dbl.sqlDate(dateYw)
             + " and a.FSecurityCode=b.FSecurityCode ) d , "
             + " (select * from " + pub.yssGetTableName("tb_data_forwardtradeacc") + " where FAccType = 'BuyCap' ) e "
             + " where d.FNum = e.Fnum";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            buf.append(YssFun.formatDate(rs.getString("FTradeDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FTradeDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("FMatureDate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(rs.getString("FBuyCury")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FMatureMoney"),
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
                                           "0.00")).append("\t");
            buf.append(rs.getString("FSaleCury")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FTradeAmount"),
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
                                           "0.00")).append("\t");
            buf.append(rs.getDouble("FTradePrice")).append("\t");
            String strAcc =  rs.getString("FCashAccCode").toUpperCase();
            if (strAcc.indexOf("ICBC") >= 0 )  {
               buf.append("ICBC");
            }else if (strAcc.indexOf("BONY") >=0 ) {
               buf.append("BONY");
            }
            buf.append("\r\n");
         }
         rs.getStatement().close();
         buf.append("\f");


         //资金调拨 CTQ ADD IN 2007-11-20
         buf.append("Account/Fund/Portfolio Number").append("\t");
         buf.append("Trade SWIFT Number").append("\t");
         buf.append("Incoming/Outgoing").append("\t");
         buf.append("Movement Description").append("\t");
         buf.append("Currency").append("\t");
         buf.append("Amount").append("\t");
         buf.append("Effective Date").append("\t");
         buf.append("Trade Date").append("\t");
         buf.append("Contractual Settlement Date").append("\t");
         buf.append("Actual Settlement Date").append("\r\n");
         strSql = "select f.fassetcode,a.fnum,b.finout,b.fdesc,g.fcurycode,b.fmoney,a.ftransdate,a.ftransferdate from " + pub.yssGetTableName("tb_cash_transfer") + " a " +
               " join " + pub.yssGetTableName("tb_cash_subtransfer") + " b on a.fnum = b.fnum " +
               " join " + pub.yssGetTableName("tb_para_portfolio") + " f on b.fportcode=f.fportcode " +
               " join " + pub.yssGetTableName("tb_para_cashaccount") + " g on b.fcashacccode=g.fcashacccode " +
               " where a.ftransferdate=" + dbl.sqlDate(dateYw)+
			   //edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
               " and ftsftypecode='01' and f.fportcode= '"+strPort+"' and a.fnum in "+
               " (select fnum from "+
               " (select distinct i.fnum, k.fcurycode from " + pub.yssGetTableName("tb_cash_transfer") + " i"+
               " join " + pub.yssGetTableName("tb_cash_subtransfer") + " j on i.fnum = j.fnum"+
               " join " + pub.yssGetTableName("tb_para_cashaccount") + " k on j.fcashacccode = k.fcashacccode"+
               " where i.ftransferdate = "+dbl.sqlDate(dateYw)+ " and i.ftsftypecode = '01')"+
               " group by fnum having count(fnum) = 1)";

         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            buf.append(rs.getString("fassetcode")).append("\t");
            buf.append(rs.getString("FNUM")).append("\t");
            String strInOut = "";
            if (rs.getInt("finout") == 1) {
               strInOut = "Incoming";
            }
            else {
               strInOut = "Outgoing";
            }
            buf.append(strInOut).append("\t");
            buf.append( (rs.getString("fDesc") != null ? rs.getString("fDesc") :
                         "")).append("\t");
            buf.append(rs.getString("fcurycode")).append("\t");
            buf.append(YssFun.formatNumber(rs.getDouble("FMoney"),
			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
                                           "0.00")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("ftransdate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("ftransdate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("ftransferdate"),
                                         "yyyy-MM-dd")).append("\t");
            buf.append(YssFun.formatDate(rs.getString("ftransferdate"),
                                         "yyyy-MM-dd")).append("\r\n");
         }
         rs.getStatement().close();
         rs = null;
         buf.append("\f");

         buf.setLength(buf.length() - 2);
      }
      catch (Exception e) {
         throw new YssException("获取Trade Blotter Sample失败！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
      return buf.toString();

   }
   
   
   /**
    * 根据券商和市场取券商的ClearerAccount
    * @param strBroker
    * @param strMarketShort
    * @return
    * @throws YssException
    */
   private String getClearerAccount(String strBroker , String strMarketShort) throws YssException {
	   
	   return getSomething(strBroker, strMarketShort,"fclearaccount");
   }
   /**
    * 根据券商和市场取券商的BrokerAccount
    * @param strBroker
    * @param strMarketShort
    * @return
    * @throws YssException
    */
   private String getBrokerAccount(String strBroker , String strMarketShort) throws YssException {
    
	    return getSomething(strBroker, strMarketShort,"fbrokeraccount");
   }
   /**
    * 根据券商和市场取券商的BrokerID
    * @param strBroker
    * @param strMarketShort
    * @return
    * @throws YssException
    */
   private String getBrokerID(String strBroker , String strMarketShort)throws YssException {

	   return getSomething(strBroker, strMarketShort,"fbrokerid");
   }
   /**
    * 根据券商和市场取券商的BrokerIDType
    * @param strBroker
    * @param strMarketShort
    * @return
    * @throws YssException
    */
   private String getBrokerIDType(String strBroker , String strMarketShort)throws YssException {

	   return getSomething(strBroker, strMarketShort,"fbrokeridtype");
   }
   /**
    * 根据券商和市场取券商的ClearerID
    * @param strBroker
    * @param strMarketShort
    * @return
    * @throws YssException
    */
   private String getClearerID(String strBroker , String strMarketShort)throws YssException {

	   return getSomething(strBroker, strMarketShort,"fclearerid");
   }
   
   /**
    * 根据券商和市场取券商的CleareIDType
    * @param strBroker
    * @param strMarketShort
    * @return
    * @throws YssException
    */
   private String getCleareIDType(String strBroker , String strMarketShort) throws YssException{

	   return getSomething(strBroker, strMarketShort,"fcleareridtype");
   }
   /**
    * 根据券商和市场取券商的fplaceofsettlement
	* add huangqirong 2013-07-31 STORY #4321 南方固定接口修改代码合并
    * @param strBroker
    * @param strMarketShort
    * @return
    * @throws YssException
    */
   private String getPlaceofsettlement(String strBroker , String strMarketShort) throws YssException{

	   return getSomething(strBroker, strMarketShort,"fplaceofsettlement");
   }
   
   
   /**
    * 封装数据库操作的SQL语句,简化代码量
    * @author:yangfang
    * @param strBroker
    * @param strMarketShort
    * @param DataFiled
    * @return
    * @throws YssException
    */
   private String getSomething(String strBroker , String strMarketShort,String DataFiled) throws YssException {
	   String strValue="";
	   ResultSet rs =null;
	   try {
		   String strSql="select * from "+pub.yssGetTableName("tb_para_brokersubbny")+" t where t.fbrokercode= '"+strBroker+"' and t.fexchangecode='"+strMarketShort+"'";
		   
		   rs = dbl.openResultSet(strSql);
		   while(rs.next()){
			   strValue=rs.getString(DataFiled);
		   }
	   } catch (Exception e) {
		   throw new YssException("获取券商信息失败！", e);
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   //edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
	   return (strValue == null || strValue.equals("null")) ? "":strValue;
   }
   
   
   /**
    * add by snyy 2013-1-9
    * add huangqirong 2013-07-31 STORY #4321 南方固定接口修改代码合并
	*
   */
   private String getTradeBlotterSampleNew(java.util.Date dateYw, String strPort) throws
   YssException {
	   
	   StringBuffer buf = new StringBuffer();
	      ResultSet rs = null;
	      try {
	    	//交易业务------------------------
	          //表头
	          buf.append("Account").append("\t");
	          buf.append("SWIFT Number").append("\t");
	          buf.append("Security Number").
	                append("\t");
	          buf.append("Security Number").
	                append("\t");
	          buf.append("SecurityName").append("\t");
	          buf.append("Currency").append("\t");
	          buf.append("Transaction Code").
	                append("\t");
	          buf.append("Effective Date").append("\t");
	          buf.append("Trade Date").append("\t");
	          buf.append("Settlement Date1").append("\t");
	          buf.append("Settlement Date2").append("\t");
	          buf.append("Original Face").append("\t");
	          buf.append("Shares").append("\t");
	          buf.append("Price Per Share").append("\t");
	          buf.append("Commission").append("\t");
	          buf.append("Fee").append("\t");
	          buf.append("Principal Amount").append("\t");
	          buf.append("Accrued Income").append("\t");
	          buf.append("Net Amount").
	                append("\t");
	          buf.append("Cost").append("\t");
	          buf.append("Cancel").append(
	                "\t");
	          buf.append("Amortisation Amount").append("\t");
	          buf.append("Initial Margin Amount").append("\t");
	          buf.append("Broker").append("\t");
	          //-------------工行调整接口
	          buf.append("Place of Settlement").append("\r\n");
	          //内容
	          String strSql =
	                "(select * From " + pub.yssGetTableName("Tb_Data_Subtrade") + " where FCheckState = 1 and FPortCode = '" +
	                strPort + "' and FBargainDate = " + dbl.sqlDate(dateYw) + ") a"; 
	          //edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
	 		 //在代码中增加了组合代码的资产代码获取，修改原来的默认 202801资产代码类型！不满足多组合情况 
	          strSql = "select a.*,a.ftrademoney/a.ftradeamount ftradeprice2 , b.FSecurityName,FTradeCury,b.FDesc as bDesc,b.FISINCode,case when x.fcnvconent is null or x.fcnvconent='' then b.FEXCHANGECODE else x.fcnvconent end FEXCHANGECODE,p.fassetcode from " +
	                strSql + " join " + "(select c.* from "+pub.yssGetTableName("Tb_Para_Security")+" c join "+
	                "(select fsecuritycode, max(fstartdate) fstartdate from "+pub.yssGetTableName("Tb_Para_Security")+" where FCheckState = 1 group by fsecuritycode) d "+
	                "on c.fsecuritycode =  d.fsecuritycode and c.fstartdate = d.fstartdate where c.fcheckstate=1) b "+
	                " left join (select a.fsrcconent,a.fcnvconent from " + pub.yssGetTableName("Tb_Dao_Dict") + " a where a.fdictcode = 'DCT000004') x on b.fexchangecode=x.fsrcconent "+
	                //edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
	 			   " on a.FSecuritycode = b.FSecurityCode left join (select * from "+pub.yssGetTableName("tb_para_portfolio")+") p on p.fportcode=a.fportcode where FTradeTypeCode in ('01','02') order by FTradeCury,FBrokerCode,FNUM";        //CTQ MODIFY 2007-11-20
	          rs = dbl.openResultSet(strSql);
	          while (rs.next()) {
	         	//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B 2011-11-24 罗侠 修改，获取组合资产代码为第一列
	         	buf.append(rs.getString("fassetcode")).append("\t");
	             buf.append(YssFun.left(rs.getString("FNUM"),
	                     15)).append("\t"); //SWIFT
	             buf.append(rs.getString("FSecurityCode")).append("\t");
	             buf.append(rs.getString("FISINCode")).append("\t");
	             buf.append( (rs.getString("bDesc") != null ? rs.getString("bDesc") :
	            	 rs.getString("FSecurityName"))).append("\t");
	             buf.append(rs.getString("FTradeCury")).append("\t");
	             String strTradeType = "";
	             if (rs.getString("FTradeTypeCode").equalsIgnoreCase("01")) {
	                strTradeType = "BUY";
	             }
	             else if (rs.getString("FTradeTypeCode").equalsIgnoreCase("02")) {
	                strTradeType = "SELL";
	             }
	             buf.append(strTradeType).append("\t"); //Accrued Income (e.g., Interest)
	             buf.append(YssFun.formatDate(rs.getString("FBargainDate"),
	                                          "yyyy-MM-dd")).append("\t");
	             buf.append(YssFun.formatDate(rs.getString("FBargainDate"),
	                                          "yyyy-MM-dd")).append("\t");
	             buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
	                                          "yyyy-MM-dd")).append("\t");
	             buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
	                                          "yyyy-MM-dd")).append("\t");
	             buf.append(" ").append("\t");
	             buf.append(YssFun.formatNumber(rs.getDouble("FTradeAmount"),
	 			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
	                                            "0.00")).append("\t");
	             buf.append(YssFun.formatNumber(rs.getDouble("FTradePrice2"),
	 			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
	                                            "0.00000000")).append("\t");     //CTQ MODIFY 2007-11-20
	             buf.append(YssFun.formatNumber(rs.getDouble("FTradeFee1"),
	 			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
	                                            "0.00")).append("\t");
	             buf.append(YssFun.formatNumber(rs.getDouble("FTradeFee2"),
	 			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
	                                            "0.00")).append("\t");
	             buf.append(YssFun.formatNumber(rs.getDouble("FTradeMoney"),
	 			//edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B
	                                            "0.00")).append("\t");
	             buf.append(YssFun.formatNumber(rs.getDouble("faccruedinterest"),
	            		 "0.00")).append("\t");
	             buf.append(YssFun.formatNumber(rs.getDouble("FTotalCost"),
	 			//---edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B start---//
	                                            "0.00")).append("\t");
	             buf.append(YssFun.formatNumber(rs.getDouble("FCost"), "0.00")).
	 			//---edit by 罗侠 BUG 3242 QDV4赢时胜（深圳_Roy）2011年11月25日02_B end---//
	                   append("\t");
	             buf.append("N").append("\t");
	             buf.append("0.00").append("\t");
	             buf.append("0.00").append("\t");
	             //---------------------工行调整接口Broker字段输出改为BrokerID
//	             buf.append(rs.getString("FBrokerCode")).append("\r\n");
	             buf.append(BNYChangeNF.formatString(getBrokerID(rs.
	                   getString("FBrokerCode"), rs.getString("FExchangeCode")), 11,
	                                               "X")).append("\t");
	             buf.append(BNYChangeNF.formatString(getPlaceofsettlement(rs.
		                   getString("FBrokerCode"), rs.getString("FExchangeCode")), 11, "X")).append("\r\n");
	          }
	          rs.getStatement().close();
	          rs = null;
	          buf.setLength(buf.length() - 2);
	         
	      } catch (Exception e) {
	          throw new YssException("获取Trade Blotter Sample失败！", e);
	      }
	      finally {
	         dbl.closeResultSetFinal(rs);
	      }
	      return buf.toString();
	   
	   
   }
}
