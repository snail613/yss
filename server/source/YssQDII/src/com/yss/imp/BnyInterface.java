package com.yss.imp;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import com.yss.util.YssFun;
import java.sql.*;
import java.util.HashMap;

import com.yss.main.parasetting.BrokerSubBnyBean;

public class BnyInterface
    extends BaseDataSettingBean {
    public BnyInterface() {
    }

    public void inBnyData(String strValue) throws YssException {

    }

    public String outBnyData(String strValue) throws YssException {
        String strList[] = strValue.split("\t", -1);
        java.util.Date dateYw = YssFun.toDate(strList[0].split(" ")[0]);
        String strPort = strList[1];
        String strType = strList[2];
        String strExchanges = strList[3]; // 增加交易所代码 by leeyu 080723
        String strRetu = "";
        if (strType.equalsIgnoreCase("INFORM")) // 前台传过来是 INFORM 而不是 Security Instruction
        { 
        	//add by guolongchao 20111213  STORY1499 QDV4中银基金2011年11月16日01_A代码开发 ------start
        	BNYChange.transactionTypeHashMap=getConvertInformation("YSS_NY_TransactionType");
        	BNYChange.marketHashMap=getConvertInformation("YSS_NY_Market");
        	BNYChange.SettlementCurrencyHashMap=getConvertInformation("YSS_NY_SettlementCurrency");
        	BNYChange.SettlementMarketHashMap=getConvertInformation("YSS_NY_SettlementMarket");  
        	//add by guolongchao 20111213  STORY1499 QDV4中银基金2011年11月16日01_A代码开发 -------end
            strRetu = getSecurityInstruction(dateYw, strPort, strExchanges); //增加交易所条件 by leeyu 080723 0000335
        } 
        else if (strType.equalsIgnoreCase("Trade Blotter Sample")) 
        {
            strRetu = getTradeBlotterSample(dateYw, strPort);
        }
        return strRetu;
    }

    private String getSecurityInstruction(java.util.Date dateYw, String strPort, String sExchangeCodes) throws
        YssException {
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        BrokerSubBnyBean brokerBny = null;
        try {
            strSql = "Select * from (select * from " +
                pub.yssGetTableName("tb_data_subtrade") + " where fcheckstate = 1 and fportcode = '" +
                //edit by songjie 2011.06.23 BUG 2112 QDV4中银基金2011年6月20日01_B 只获取交易类型代码为 01、02、11的数据
                strPort + "' and FTradeTypeCode in ('01','02','11') and FBargainDate = " + dbl.sqlDate(dateYw) + ") a , (select sb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("tb_para_security") +
                " where FCheckState=1 and FStartDate<= " + dbl.sqlDate(dateYw) +
                " group by FSecurityCode) sa join (select * from " +
                pub.yssGetTableName("tb_para_security") +
                " where FCheckState=1 )sb on sa.FSecurityCode = sb.FSecurityCode and sa.FStartDate = sb.FStartDate) b, "
                +
                " (select * from " + pub.yssGetTableName("Tb_Para_Cashaccount") + " where FCheckstate = 1 ) c "
                +
                "  where a.FSecurityCode = b.FSecurityCode and a.FCashAcccode = c.FCashAcccode and FTradeCury <> 'CNY'";
            if (sExchangeCodes.trim().length() > 0) { // by leeyu 080723 BUG:0000335 海富通增加 市场条件 现改为多个市场
                strSql = strSql + " and FExchangeCode in(" + operSql.sqlCodes(sExchangeCodes) + ")";
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //---------------------------------------------------------------------------
                brokerBny = new BrokerSubBnyBean();
                brokerBny.setYssPub(pub);
                brokerBny.setStrBrokerCode(rs.getString("FBrokerCode"));
                brokerBny.setStrExchangeCode(rs.getString("FExchangeCode"));
                brokerBny.setStrTradeCatCode(rs.getString("FCatCode"));

                brokerBny.setStrTradeSubCatCode(rs.getString("FSubCatCode"));// add by wangzuochun 2011.03.31 BUG #1574 纽约银行网银数据接口导出处理 
                brokerBny.setLinkedBroker(); // modify by wangzuochun 2011.03.31 BUG #1574 纽约银行网银数据接口导出处理 


                //---------------------------------------------------------------------------
                //格式，字符从左开始填充，长度不足补空格，
                //数值靠右侧填充，长度不足补0，
                //日期型号，为空全部为0；
                buf.append("S"); //RECORD TYPE (CONSTANT "S")
                //南方约定只取前15位得主号
                buf.append(BNYChange.formatString(YssFun.left(rs.getString("FNUM"),
                    15), 16)); //CUSTOMER REFERENCE NUMBER , 于trade blotter中的Trade SWIFT Number字段对应
                buf.append(BNYChange.formatString(YssFun.left(rs.getString(
                    "FBankAccount"), 6), 13)); //BNY ACCOUNT NUMBER
                buf.append(BNYChange.formatString(brokerBny.getStrClearAccount(), 16)); //CLEARER ACCOUNT---对手方帐户
                buf.append(BNYChange.formatString(18)); //FILLER
                buf.append(BNYChange.formatString(BNYChange.changeTransactionTypes(
                    rs.getString("FTradeTypeCode")), 20)); //TRANSACTION TYPE
                buf.append(BNYChange.formatString(BNYChange.getMarket(rs.getString(
                    "FExchangeCode")), 20)); //MARKET
                buf.append(BNYChange.formatString("OTHER", 50)); //ASSET TYPE
                buf.append(BNYChange.formatString(26)); //FILLER
                buf.append(BNYChange.formatString(8)); //BNY BROKER NUMBER
                buf.append(BNYChange.formatString(48)); //FILLER
                buf.append(BNYChange.formatDate(rs.getDate("FSettleDate"))); //SETTLEMENT DATE
                buf.append(BNYChange.formatString(BNYChange.getCurrency(rs.
                    getString("FTradeCury")), 20)); //SETTLEMENT CURRENCY
                buf.append(BNYChange.formatString(3)); //FILLER
                buf.append(BNYChange.formatString(16)); //FX CUSTOMER REFERENCE NUMBER
                buf.append(BNYChange.formatNumber(5)); //RELATED BLOCK DETAIL COUNTER
                buf.append(BNYChange.formatNumber(5)); //RELATED COLLATERAL DETAIL COUNTER
                buf.append(BNYChange.formatString(20)); //FX TRANSACTION TYPE
                buf.append(BNYChange.formatNumber(0, 16, 3)); //CASH COLLATERAL AMOUNT (OPTIONS)
                buf.append(BNYChange.formatNumber(4)); //FILLER, VALUE=0000
                buf.append(BNYChange.formatNumber(rs.getDouble("FTradeFee1"), 11, 2)); //COMMISSION
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(16)); //CURRENT FACE (MBS ONLY)
                buf.append(BNYChange.formatNumber(0, 15, 2)); //EXCHANGE FEE
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(16)); //FACTOR (MBS ONLY)
                buf.append(BNYChange.formatNumber(rs.getDouble("FTotalCost"), 16, 3)); //FINAL MONEY
                buf.append(BNYChange.formatNumber(rs.getDouble("FAccruedinterest"),
                                                  15, 2)); //INTEREST
                buf.append(BNYChange.formatNumber(1)); //INTEREST SIGN
                buf.append(BNYChange.formatNumber(15)); //INVENTORY COST IN LOCAL CURRENCY
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(15)); //INVENTORY COST IN BASE CURRENCY
                buf.append(BNYChange.formatNumber(7)); //FILLER, VALUE=0000000
                buf.append(BNYChange.formatNumber(rs.getDouble("FTradeFee2"), 9, 2)); //OTHER CHARGES
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(rs.getDouble("FTradePrice"), 16,
                                                  8)); //PRICE
                buf.append(BNYChange.formatNumber(rs.getDouble("FTradeMoney"), 15,
                                                  2)); //PRINCIPAL CASH
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(rs.getDouble("FTradeAmount"), 16,
                                                  3)); //QUANTITY
                buf.append(BNYChange.formatNumber(16)); //RATE
                buf.append(BNYChange.formatNumber(10)); //FILLER, VALUE=0000000000
                buf.append(BNYChange.formatNumber(5)); //SEC FEE
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(0, 16, 0)); //SETTLEMENT OVERRIDE AMOUNT
                buf.append(BNYChange.formatNumber(16)); //STRIKE PRICE
                buf.append(BNYChange.formatNumber(6)); //STRIKE PRICEFILLER, VALUE=000000
                buf.append(BNYChange.formatNumber(9)); //TAXES
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(5)); //UNITS PER CONTRACT (OPTIONS)
                buf.append(BNYChange.formatString(16)); //TURNAROUND RELATED CUSTOMER REFERENCE NUMBER
                buf.append(BNYChange.formatString("", 16)); //CUSTOMER ACCOUNT AT BROKER (OPTIONS)
                buf.append(BNYChange.formatString(4)); //AGENCY CODE
                buf.append(BNYChange.formatString(20)); //CASH COLLATERAL CURRENCY
                buf.append(BNYChange.formatString(1)); //CASH COLLATERAL DIRECTION
                buf.append(BNYChange.formatString(1)); //CASHLOT FLAG
                buf.append(BNYChange.formatString(8)); //BNY CLEARING BROKER
                buf.append(BNYChange.formatString(48)); //FILLER
                buf.append(BNYChange.formatString("N", 1)); //DTC ELIGIBLE FLAG
                buf.append(BNYChange.formatString(15)); //EXCHANGE CODE
                buf.append(BNYChange.formatString(8)); //EXPIRATION DATE (OPTIONS)
                buf.append(BNYChange.formatString(1)); //FUNDS TYPE CODE
                buf.append(BNYChange.formatString(1)); //HEDGE TYPE
                buf.append(BNYChange.formatString(13)); //SWING RECEIVE ACCOUNT NUMBER
                buf.append(BNYChange.formatString(1)); //FILLER
                buf.append(BNYChange.formatString(20)); //CURRENCY OF INVENTORY COST IN BASE
                buf.append(BNYChange.formatString(20)); //CURRENCY OF INVENTORY COST IN LOCAL
                buf.append(BNYChange.formatString(8));
                buf.append(BNYChange.formatString(3)); //ISSUER CODE

                buf.append(BNYChange.formatString(30)); //BNY ISSUER NAME
                buf.append(BNYChange.formatString(5)); //FILLER
                buf.append(BNYChange.formatString(8)); //MATURITY DATE
                buf.append(BNYChange.formatString("NORMAL", 35)); //SETTLEMENT METHOD
                buf.append(BNYChange.formatString(31)); //FILLER
                buf.append(BNYChange.formatString(10)); //OSDP COUNTRY
                buf.append(BNYChange.formatString(5)); //EUROCLEAR/CEDEL PARTICIPANT NUMBER
                buf.append(BNYChange.formatString(6)); //MBS POOL NUMBER
                buf.append(BNYChange.formatString(4)); //PROJECT NOTE TYPE
                buf.append(BNYChange.formatString(1)); //PUT/CALL TOGGLE

                buf.append(BNYChange.formatString(35)); //REGISTRATION INSTRUCTION LINE 1
                buf.append(BNYChange.formatString(35)); //REGISTRATION INSTRUCTION LINE 2
                buf.append(BNYChange.formatString(35)); //REGISTRATION INSTRUCTION LINE 3
                buf.append(BNYChange.formatString(35)); //REGISTRATION INSTRUCTION LINE 4
                buf.append(BNYChange.formatString(35)); //REGISTRATION INSTRUCTION LINE 5
                buf.append(BNYChange.formatString(32)); //REGISTRATION INSTRUCTION LINE 6

                buf.append(BNYChange.formatString(1)); //ROLL COLLATERAL FLAG
                buf.append(BNYChange.formatString(40)); //SECURITY DESCRIPTION
                buf.append(BNYChange.formatString(rs.getString("FISINCode"), 12)); //SECURITY NUMBER
                buf.append(BNYChange.formatString("ISIN", 12)); //SECURITY NUMBER TYPE
                buf.append(BNYChange.formatString(20)); //CURRENCY OF SETTLEMENT OVERRIDE AMT

                buf.append(BNYChange.formatString(1)); //SETTLEMENT OVERRIDE FLAG
                buf.append(BNYChange.formatString(120)); //SPECIAL INSTRUCTIONS
                buf.append(BNYChange.formatString(2)); //STATE CODE (MUNIS)
                buf.append(BNYChange.formatString(1)); //TAXLOT FLAG
                buf.append(BNYChange.formatString(11)); //TAXPAYER ID (TRANSFER & SHIP)

                buf.append(BNYChange.formatDate(rs.getDate("FBargainDate"))); //TRADE DATE
                buf.append(BNYChange.formatString(45)); //FILLER
                buf.append(BNYChange.formatString(1)); //TURNAROUND
                buf.append(BNYChange.formatString(1)); //SETTLE FREE FLAG
                buf.append(BNYChange.formatString(1)); //FILLER
                buf.append(BNYChange.formatString(40)); //WIRE INSTITUTION NAME
                buf.append(BNYChange.formatString(16)); //WIRE INSTITUTION ID
                buf.append(BNYChange.formatString(40)); //WIRE ACCOUNT NAME
                buf.append(BNYChange.formatString(16)); //WIRE ACCOUNT ID

                buf.append(BNYChange.formatString(brokerBny.getStrBrokerIDType(), 1)); //Non-FINS Broker ID Type
                buf.append(BNYChange.formatString(brokerBny.getStrBrokerID(), 20));
                buf.append(BNYChange.formatString(brokerBny.getStrClearerID(), 20));
                buf.append(BNYChange.formatString(88)); //FILLER
                buf.append(BNYChange.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 1
                buf.append(BNYChange.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 1
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 1
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT BASE 1
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(8)); //TAXLOT/CASHLOT DATE 1

                buf.append(BNYChange.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 2
                buf.append(BNYChange.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 2
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 2
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT BASE 2
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(8)); //TAXLOT/CASHLOT DATE 2

                buf.append(BNYChange.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 3
                buf.append(BNYChange.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 3
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 3
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT BASE 3
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(8)); //TAXLOT/CASHLOT DATE 3

                buf.append(BNYChange.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 4
                buf.append(BNYChange.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 4
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 4
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT BASE 4
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=02
                buf.append(BNYChange.formatNumber(8)); //TAXLOT/CASHLOT DATE 4

                buf.append(BNYChange.formatNumber(5)); //TAXLOT/CASHLOT NUMBER 5
                buf.append(BNYChange.formatNumber(16)); //TAXLOT/CASHLOT QUANTITY 5
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT LOCAL 5
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(15)); //TAXLOT/CASHLOT BASE 5
                buf.append(BNYChange.formatNumber(1)); //FILLER, VALUE=0
                buf.append(BNYChange.formatNumber(8)); //TAXLOT/CASHLOT DATE 5

                buf.append(BNYChange.formatString(1)); //PRICE/DISCOUNT FLAG
                buf.append(BNYChange.formatString(120)); //SUB-CUSTODIAN SPECIAL INSTRUCTIONS
                buf.append(BNYChange.formatString(4)); //STAMP DUTY
                buf.append(BNYChange.formatString(brokerBny.getStrBrokerAccount(), 34)); //CLIENT ACCOUNT AT DEAG/REAG
                buf.append(BNYChange.formatString(brokerBny.getStrPlaceSettlement(), 11));
                buf.append(BNYChange.formatString(11));
                buf.append(BNYChange.formatNumber(18)); //EXPANDED QUANTITY
                buf.append(BNYChange.formatNumber(5)); //FILLER, VALUE = 00000

                buf.append(BNYChange.formatString(BNYChange.getPlaceOfSettlementMarketCode(rs.getString("FExchangeCode")), 2)); //PLACE OF SETTLEMENT MARKET CODE
                buf.append(BNYChange.formatString(2));
                buf.append(BNYChange.formatString(4)); //CHANGE OF BENEFICIAL OWNERSHIP / REGISTRATION INDICATOR
                buf.append(BNYChange.formatString(4)); //SWIFT SETTLEMENT METHOD
                buf.append(BNYChange.formatString(8)); //48 HOUR MBS UPDATE

                buf.append(BNYChange.formatString(brokerBny.getStrClearerIDType(), 1)); //Non-FINS CLEARING BROKER ID Type
                buf.append("\r\n");
            }
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 2);
            }
        } catch (Exception e) {
            throw new YssException("获取Security Instruction失败！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

        return buf.toString();
    }

    /***
     * 下面的代码根据工行接口版本升级修改,by ly 080220
     */

    private String getTradeBlotterSample(java.util.Date dateYw, String strPort) throws
        YssException {
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            //交易业务------------------------
            //表头
            buf.append("Account/Fund/Portfolio Number").append("\t");
            buf.append("SAFE Account").append("\t"); //new
            buf.append("Trade SWIFT Number").append("\t");
            //buf.append("Security Number/Identifier (e.g., Cusip, Sedol, ISIN)").
            //      append("\t"); //删除,现在没有
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
            buf.append("Factor").append("\t"); //new
            buf.append("Commission").append("\t");
            buf.append("Fee/Trade Expense/Tax").append("\t");
            buf.append("Local tax").append("\t"); //new
            buf.append("Stamp tax").append("\t"); //new
            buf.append("Principal Amount").append("\t");
            buf.append("Accrued Income (e.g., Interest)").append("\t");
            buf.append("Net Settlement Amount (must specify: Local or Base)").
                append("\t");
            buf.append("Cost").append("\t");
            buf.append("Cancel (i.e., is this a cancelled trade?: Y/N)").append(
                "\t");
            buf.append("Amortisation Amount").append("\t");
            buf.append("Initial Margin Amount").append("\t");
            buf.append("DEAG/REAG code type").append("\t"); //new
            buf.append("DEAG/REAG data source scheme").append("\t"); //new
            buf.append("DEAG/REAG").append("\t"); //new
            buf.append("BUYR/SELL code type").append("\t"); //new
            buf.append("BUYR/SELL data source scheme").append("\t"); //new
            buf.append("BUYR/SELL").append("\t"); //new
            buf.append("PSET code type").append("\t"); //new
            buf.append("PSET data source scheme").append("\t"); //new
            buf.append("PSET ").append("\r\n"); //new
            //buf.append("Broker").append("\t"); //现在没有,删除
            //-------------工行调整接口
            //buf.append("Place of Settlement").append("\r\n");//删除,现在没有
            //内容
            String strSql =
                "(select aa.*,bb.fassetcode From " + pub.yssGetTableName("Tb_Data_Subtrade") +
                " aa Inner Join " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " bb On aa.fportcode=bb.fportcode where aa.FCheckState = 1 and aa.FPortCode = '" +
                strPort + "' and FBargainDate = " + dbl.sqlDate(dateYw) + ") a";
            //strSql = "select a.*,a.ftrademoney/case when a.ftradeamount=0 then 1 else a.ftradeamount end ftradeprice2 , b.FSecurityName,FTradeCury,b.FDesc as bDesc,b.FISINCode,FExchangeCode,b.FFactor as FFactor from " +
            strSql = "select a.*,a.ftrademoney/case when a.ftradeamount=0 then 1 else a.ftradeamount end ftradeprice2 , b.FSecurityName,FTradeCury,b.FDesc as bDesc,b.FISINCode,FExchangeCode,b.FFactor as FFactor,aff.FOrgCode as FBrokerBICCode,affType.FOrgCode as FSettleBICCode, " +
                " case when a.ffactsettledate=null or a.ffactSettleDate=" + dbl.sqlDate("1900-01-01") + " or a.FfactSettleDate=" + dbl.sqlDate("9998-12-31") + " then a.fsettledate " +
                " when a.Ffactsettledate = a.Fsettledate then a.Fsettledate else a.Ffactsettledate end as FFactSettleDates,case when b.FCatCode='EQ' or b.FCatCode='TR' then 0 else b.FFactor end as FOrgFactor from " + //by leeyu
                //strSql + " join " + "(select c.* from "+pub.yssGetTableName("Tb_Para_Security")+" c join "+
                //"(select fsecuritycode, max(fstartdate) fstartdate from "+pub.yssGetTableName("Tb_Para_Security")+" group by fsecuritycode) d "+
                //"on c.fsecuritycode =  d.fsecuritycode and c.fstartdate = d.fstartdate where c.fcheckstate=1) b "+
                //"on a.FSecuritycode = b.FSecurityCode order by FTradeCury,FBrokerCode,FNUM";
                strSql + " join " + "(select * from " + pub.yssGetTableName("Tb_Para_Security") + ") b " + // 这里去掉启用日期这个字段的判断,by leeyu 080804
                " on a.FSecuritycode = b.FSecurityCode " +
                " left join " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " aff on a.FBrokerCode = aff.FAffcorpCode " + // add by leeyu 080715 增加券商的BIC码
                " left join " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " affType on a.FSettleOrgCode = affType.FAffCorpCode " +
                " order by FTradeCury,FBrokerCode,FNUM";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                double dLocalTax = 0.0, dStampTax = 0.0;
                buf.append(rs.getString("fassetcode")).append("\t");
                //buf.append(" ").append("\t");//new
                buf.append(rs.getString("FAssetCode")).append("\t"); //by leeyu 080714
                buf.append(rs.getString("FNUM")).append("\t"); //SWIFT
                //buf.append(rs.getString("FSecurityCode")).append("\t"); del 现在没有
                buf.append(rs.getString("FISINCode")).append("\t");
                buf.append( (rs.getString("bDesc") != null ? rs.getString("bDesc") :
                             "")).append("\t");
                buf.append(rs.getString("FTradeCury")).append("\t");
                String strTradeType = "";
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase("01")) {
                    strTradeType = "BUY";
                } else if (rs.getString("FTradeTypeCode").equalsIgnoreCase("02")) {
                    strTradeType = "SELL";
                }
                buf.append(strTradeType).append("\t"); //Accrued Income (e.g., Interest)
                buf.append(YssFun.formatDate(rs.getString("FBargainDate"),
                                             "yyyy-MM-dd")).append("\t");
                buf.append(YssFun.formatDate(rs.getString("FBargainDate"),
                                             "yyyy-MM-dd")).append("\t");
                buf.append(YssFun.formatDate(rs.getString("FSettleDate"),
                                             "yyyy-MM-dd")).append("\t");
                buf.append(YssFun.formatDate(rs.getString("FFactSettleDates"),
                                             "yyyy-MM-dd")).append("\t");
                //buf.append(" ").append("\t");//原始面值。对股票、基金为空。对债券、资产支持证券为数量*100,这里取面值,
                buf.append(YssFun.formatNumber(rs.getDouble("FOrgFactor"),
                                               "#,##0.000000000000")).append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FTradeAmount"),
                                               "#,##0.00")).append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FTradePrice2"),
                                               "#,##0.00000000")).append("\t"); //CTQ MODIFY 2007-11-20
                buf.append(YssFun.formatNumber(rs.getDouble("FFactor"),
                                               "#,##0.000000000000")).append("\t"); //new Factor

                buf.append(YssFun.formatNumber(rs.getDouble("FTradeFee1"),
                                               "#,##0.00")).append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FTradeFee2"),
                                               "#,##0.00")).append("\t");
                for (int i = 1; i < 9; i++) {
                    if (rs.getString("FFeeCode" + i) != null) {
                        if (rs.getString("FFeeCode" + i).equalsIgnoreCase("Local tax")) {
                            dLocalTax = rs.getDouble("FTradeFee" + i);
                        }
                        if (rs.getString("FFeeCode" + i).equalsIgnoreCase("Stamp tax")) {
                            dStampTax = rs.getDouble("FTradeFee" + i);
                        }
                    }
                }
                buf.append(YssFun.formatNumber(dLocalTax,
                                               "#,##0.00")).append("\t"); //Local tax
                buf.append(YssFun.formatNumber(dStampTax,
                                               "#,##0.00")).append("\t"); //Stamp tax
                buf.append(YssFun.formatNumber(rs.getDouble("FTradeMoney"),
                                               "#,##0.00")).append("\t");
                buf.append("0.00").append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FTotalCost"),
                                               "#,##0.00")).append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FTotalCost"), "#,##0.00")).
                    append("\t");
                buf.append("N").append("\t");
                buf.append("0.00").append("\t"); //new 资产支持证券当前面值
                buf.append("0.00").append("\t");
                buf.append(" ").append("\t"); //new
                buf.append(" ").append("\t"); //new
                buf.append(" ").append("\t"); //new
                buf.append("1").append("\t"); //new
                buf.append(" ").append("\t"); //new
                //buf.append(" ").append("\t");//new 不能为空
                buf.append(rs.getString("FBrokerBICCode") == null ? " " : rs.getString("FBrokerBICCode")).append("\t"); // by leeyu 080714 取券商的BIC码
                buf.append("1").append("\t"); //new
                buf.append(" ").append("\t"); //new
                //buf.append(" ").append("\t");
                buf.append(rs.getString("FSettleBICCode") == null ? " " : rs.getString("FSettleBICCode")).append("\t"); //by leeyu 080731 取结算机构的BIC码
                // buf.append("0.00").append("\t"); //删除 ,现在没有
                //---------------------工行调整接口Broker字段输出改为BrokerID
//            buf.append(rs.getString("FBrokerCode")).append("\r\n");
                // buf.append(BNYChange.formatString(BNYChange.getBrokerID(rs.
                //       getString("FBrokerCode"), rs.getString("FExchangeCode")), 11,
                //                                   "X")).append("\t"); //删除,现在没有
                // buf.append(BNYChange.formatString(BNYChange.getPlaceOfSettlement(rs.
                //       getString("FExchangeCode")), 11, "X")).append("\r\n"); //删除,现在没有
                buf.append("\r\n"); //new
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
                "(select * From " + pub.yssGetTableName("Tb_Para_CashAccount") + " where FCheckState = 1) b , "
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
                buf.append(YssFun.formatNumber(rs.getDouble("FMoney"), "#,##0.00")).
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
                "select * From " + pub.yssGetTableName("Tb_Data_RateTrade") + " where FCheckState = 1 and FPortCode = '" +
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
                buf.append(YssFun.formatNumber(rs.getDouble("FBMoney"), "#,##0.00")).
                    append("\t");
                buf.append(rs.getString("FSCuryCode")).append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FSMoney"), "#,##0.00")).
                    append("\t");
                buf.append(rs.getDouble("FExCuryRate")).append("\t");
                buf.append("").append("\r\n");
            }
            rs.getStatement().close();
            rs = null;
            //远期
            strSql = "select a.FTradeDate as FTradeDate,a.FSettleDate as FSettleDate,b.FBuyCury as FBuyCury,b.FSaleCury as FSaleCury, "
                + "a.FMatureMoney as FMatureMoney,a.FTradeAmount as FTradeAmount,a.FTradePrice as FTradePrice "
                + "from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + " a," + pub.yssGetTableName("Tb_Para_Forward") + " b where a.FCheckState = 1 and a.FPortCode = '"
                + strPort + "' and FTradeDate = " + dbl.sqlDate(dateYw) +
                " and a.FSecurityCode=b.FSecurityCode";
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
                buf.append(rs.getString("FBuyCury")).append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FMatureMoney"),
                                               "#,##0.00")).append("\t");
                buf.append(rs.getString("FSaleCury")).append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FTradeAmount"),
                                               "#,##0.00")).append("\t");
                buf.append(rs.getDouble("FTradePrice")).append("\t");
                buf.append("").append("\r\n");
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
                " where a.ftransferdate=" + dbl.sqlDate(dateYw) +
                " and ftsftypecode='01' and a.fnum in " +
                " (select fnum from " +
                " (select distinct i.fnum, k.fcurycode from " + pub.yssGetTableName("tb_cash_transfer") + " i" +
                " join " + pub.yssGetTableName("tb_cash_subtransfer") + " j on i.fnum = j.fnum" +
                " join " + pub.yssGetTableName("tb_para_cashaccount") + " k on j.fcashacccode = k.fcashacccode" +
                " where i.ftransferdate = " + dbl.sqlDate(dateYw) + " and i.ftsftypecode = '01')" +
                " group by fnum having count(fnum) = 1)";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append(rs.getString("fassetcode")).append("\t");
                buf.append(rs.getString("FNUM")).append("\t");
                String strInOut = "";
                if (rs.getInt("finout") == 1) {
                    strInOut = "Incoming";
                } else {
                    strInOut = "Outgoing";
                }
                buf.append(strInOut).append("\t");
                buf.append( (rs.getString("fDesc") != null ? rs.getString("fDesc") :
                             "")).append("\t");
                buf.append(rs.getString("fcurycode")).append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FMoney"),
                                               "#,##0.00")).append("\t");
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
            //buf.append("\f");

            // buf.setLength(buf.length() - 2);// by leeyu
        } catch (Exception e) {
            throw new YssException("获取Trade Blotter Sample失败！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return buf.toString();
    }
    
    /***
     * add by guolongchao 20111213  STORY1499 QDV4中银基金2011年11月16日01_A代码开发 
     * 或取需要转换的字段内容,将fsrcconent为key,fcnvconent为value,存入到hashmap中,并返回
     * @param dictcode 接口字典代码
     * @throws YssException 
     */
    private HashMap getConvertInformation(String dictcode) throws YssException {      
    	  Connection conn = null;
          String sql = "";
          ResultSet rs = null;
          HashMap resHashMap=new HashMap();
          String  srcContent="";
          String  convertContent="";
          try {
              //conn = dbl.loadConnection();
              sql = "select * from " + pub.yssGetTableName("Tb_Dao_Dict") +
                  " where fcheckstate=1 and Fdictcode=" + dbl.sqlString(dictcode);
              rs = dbl.openResultSet(sql);
              while (rs.next()) 
              {
            	  srcContent=rs.getString("fsrcconent");
            	  convertContent=rs.getString("fcnvconent");
            	  if(!srcContent.equals("null")&&!convertContent.equals("null"))
            	  {
            		  if(!resHashMap.containsKey(srcContent))            		 
            			  resHashMap.put(srcContent, convertContent);            		 
            	  }
              }  
              rs.close();              
          } catch (Exception ex) {
              throw new YssException("获取接口字典：fsrcconent,fcnvconent出错", ex);
          } finally {
              dbl.closeResultSetFinal(rs); 
          } 
          return resHashMap;
    }
}
