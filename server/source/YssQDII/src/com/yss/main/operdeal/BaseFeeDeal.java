package com.yss.main.operdeal;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class BaseFeeDeal
    extends BaseBean {
    private String catCode = "";
    private String subCatCode = "";
    private String cusCatCode = "";
    private String portCode = "";
    private String currencyCode = "";
    private String exchangeCode = "";
    private String tradeTypeCode = "";
    private String brokerCode = "";
    private String sellNetCode = "";
    private String sellTypeCode = "";
    private String stockholderCode = "";
    private String tradeSeatCode = "";

    public double sumMoney;

    public String getBrokerCode() {
        return brokerCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public String getCatCode() {
        return catCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCusCatCode() {
        return cusCatCode;
    }

    public void setCusCatCode(String cusCatCode) {
        this.cusCatCode = cusCatCode;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getSubCatCode() {
        return subCatCode;
    }

    public void setSubCatCode(String subCatCode) {
        this.subCatCode = subCatCode;
    }

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public String getStockholderCode() {
        return stockholderCode;
    }

    public String getTradeSeatCode() {
        return tradeSeatCode;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setStockholderCode(String stockholderCode) {
        this.stockholderCode = stockholderCode;
    }

    public void setTradeSeatCode(String tradeSeatCode) {
        this.tradeSeatCode = tradeSeatCode;
    }

    /*
       public HashMap getTradeFeeMoney() throws YssException {
          HashMap hmResult = null;
          ArrayList alFees = null;

          alFees = getTradeFeeBeans();
          for (int i=0; i<alFees.size(); i++){
             FeeBean fee = (FeeBean)alFees.get(i);
     fee.setFeeMoney(feeOper.getFeeRate(fee.getPerExpCode(),this.sumMoney));
          }
          return null;
       }
     */
    /**
     * setFeeAttr
     *
     * @param sSecurityCode String
     * @param sTradeType String
     * @param sPortCode String
     * @param sBrokerCode String
     * @param sumMoney double
     */
    public void setFeeAttr(String sSecurityCode, String sTradeType,
                           String sPortCode, String sBrokerCode, double sumMoney) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            this.tradeTypeCode = sTradeType;
            this.portCode = sPortCode;
            this.brokerCode = sBrokerCode;
            this.sumMoney = sumMoney;
            strSql =
                "select FCatCode,FSubCatCode,FCusCatCode,FTradeCury,FExchangeCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate = (select Max(FStartDate) from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                " and FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                ") and FSecurityCode = " + dbl.sqlString(sSecurityCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.catCode = rs.getString("FCatCode") + "";
                this.subCatCode = rs.getString("FSubCatCode") + "";
                this.cusCatCode = rs.getString("FCusCatCode") + "";
                this.currencyCode = rs.getString("FTradeCury") + "";
                this.exchangeCode = rs.getString("FExchangeCode") + "";
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * buildFeeCondition
     *
     * @return String
     */
    public String buildFeeCondition() {
        StringBuffer buf = new StringBuffer();
        buf.append("FPortCode = " + dbl.sqlString(this.portCode)).append("\t");
        buf.append("FExchangeCode = " + dbl.sqlString(this.exchangeCode)).append("\t");
        buf.append("FBrokerCode = " + dbl.sqlString(this.brokerCode)).append("\t");
        buf.append("FCuryCode = " + dbl.sqlString(this.currencyCode)).append("\t");
        buf.append("FTradeTypeCode = " +
                   dbl.sqlString(this.tradeTypeCode)).append("\t");
        buf.append("FCusCatCode = " + dbl.sqlString(this.cusCatCode)).append("\t");
        buf.append("FSubCatCode = " + dbl.sqlString(this.subCatCode)).append("\t");
        buf.append("FCatCode = " + dbl.sqlString(this.catCode)).append("\t");
        return buf.toString();
    }

    /**
     * buildFeeCondition
     *
     * @return String
     */
    public String buildTAFeeCondition() {
        StringBuffer buf = new StringBuffer();
        buf.append("FSellNetCode = " + dbl.sqlString(this.sellNetCode)).append("\t");
        buf.append("FCuryCode = " + dbl.sqlString(this.currencyCode)).append("\t");
        buf.append("FSellTypeCode = " +
                   dbl.sqlString(this.sellTypeCode)).append("\t");

        return buf.toString();
    }

    /**
     * getFeeBeans
     *
     * @return ArrayList
     */
    public ArrayList getFeeBeans() throws YssException {
        String strSql = "";
        ArrayList alResult = new ArrayList();
        String[] sFeeCondAry = null;
        String sTmpTableName = "";
        ResultSet rs = null;
        FeeBean fee = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_TradeFeeLink") +
                " where (FCatCode = " + dbl.sqlString(this.catCode) +
                " or FCatCode = ' ')" +
                " and (FSubCatCode = " + dbl.sqlString(this.subCatCode) +
                " or FSubCatCode = ' ')" +
                " and (FCusCatCode = " + dbl.sqlString(this.cusCatCode) +
                " or FCusCatCode = ' ')" +
                " and (FTradeTypeCode = " + dbl.sqlString(this.tradeTypeCode) +
                " or FTradeTypeCode = ' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.currencyCode) +
                " or FCuryCode = ' ')" +
                " and (FBrokerCode = " + dbl.sqlString(this.brokerCode) +
                " or FBrokerCode = ' ')" +
                " and (FExchangeCode = " + dbl.sqlString(this.exchangeCode) +
                " or FExchangeCode = ' ')" +
                " and (FtradeSeatCode =" + dbl.sqlString(this.tradeSeatCode) +
                " or FtradeSeatCode =' ')" +
                " and (FStockholderCode=" + dbl.sqlString(this.stockholderCode) +
                " or FStockholderCode=' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode = ' ') and FCheckState = 1";
            sTmpTableName = "Tb_Tmp_TradeFeeLink_" + pub.getUserCode();
            if (dbl.yssTableExist(sTmpTableName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + sTmpTableName));
                /**end*/
            }
            String strTemp = "create table " + sTmpTableName + " as (" + strSql + ")";
            //---2007.11.29 添加 蒋锦 因为 DB2 中有具体查询表和基本表的区分，所以通过 create table as 建表时需要添加关键字 definition only ---//
            if (dbl.getDBType() == YssCons.DB_DB2) {
                strTemp = strTemp + " definition only";
                dbl.executeSql(strTemp);
                dbl.executeSql("insert into " + sTmpTableName + "(" + strSql + ")");
            } else if (dbl.getDBType() == YssCons.DB_ORA) {
                dbl.executeSql(strTemp);
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            //------------------------------------------------------------------------//
            sFeeCondAry = buildFeeCondition().split("\t");
            for (int i = 0; i <= sFeeCondAry.length; i++) {
                if (i == sFeeCondAry.length) {
                    strSql = "select * from " + sTmpTableName;
                } else {
                    strSql = "select * from " + sTmpTableName + " where " +
                        sFeeCondAry[i];
                }
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    for (int j = 1; j <= 6; j++) {
                        if (rs.getString("FFeeCode" + j) != null) {
                            fee = new FeeBean();
                            fee.setYssPub(pub);
                            fee.setFeeCode(rs.getString("FFeeCode" + j));
                            fee.getSetting(); //这里是为了设置FeeBean的属性
                            if (fee.checkStateId == 1) {
                                alResult.add(fee);
                            }
                        }
                    }
                    break;
                }
                dbl.closeResultSetFinal(rs);
            }
            return alResult;
        } catch (Exception e) {
            throw new YssException("获取费用集合出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getFeeBeans
     *
     * @return ArrayList
     */
    public ArrayList getTAFeeBeans() throws YssException {
        String strSql = "";
        ArrayList alResult = new ArrayList();
        String[] sFeeCondAry = null;
        String sTmpTableName = "";
        ResultSet rs = null;
        FeeBean fee = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_TA_FeeLink") +
                " where (FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
                " or FSellNetCode = ' ')" +
                " and (FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
                " or FSellTypeCode = ' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.currencyCode) +
                " or FCuryCode = ' ')" + " and FCheckState <> 2";
            sTmpTableName = "Tb_Tmp_TA_FeeLink_" + pub.getUserCode();
            if (dbl.yssTableExist(sTmpTableName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + sTmpTableName));
                /**end*/
            }
            //-----------------2007.11.30 蒋锦 修改-------考虑使用DB2的情况-----------------//
            if (dbl.getDBType() == YssCons.DB_ORA) {
                dbl.executeSql("create table " + sTmpTableName + " as (" + strSql +
                               ")");
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                dbl.executeSql("create table " + sTmpTableName + " as (" + strSql +
                               ")" + " definition only");
                dbl.executeSql("insert into " + sTmpTableName + "(" + strSql + ")");
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            //---------------------------------------------------------------------------//
            sFeeCondAry = buildTAFeeCondition().split("\t");
            for (int i = 0; i <= sFeeCondAry.length; i++) {
                if (i == sFeeCondAry.length) {
                    strSql = "select * from " + sTmpTableName;
                } else {
                    strSql = "select * from " + sTmpTableName + " where " +
                        sFeeCondAry[i];
                }
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    for (int j = 1; j <= 6; j++) {
                        if (rs.getString("FFeeCode" + j) != null) {
                            fee = new FeeBean();
                            fee.setYssPub(pub);
                            fee.setFeeCode(rs.getString("FFeeCode" + j));
                            fee.getSetting(); //这里是为了设置FeeBean的属性
                            if (fee.checkStateId == 1) {
                                alResult.add(fee);
                            }
                        }
                    }
                    break;
                }
                dbl.closeResultSetFinal(rs);
            }
            return alResult;
        } catch (Exception e) {
            throw new YssException("获取费用集合出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setTAFeeAttr(String sSellNetCode, String sSellTypeCode,
                             String sCuryCode,
                             double sumMoney) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            this.sellNetCode = sSellNetCode;
            this.sellTypeCode = sSellTypeCode;
            this.currencyCode = sCuryCode; //增加币种的传入fazmm20071029
            this.sumMoney = sumMoney;
            strSql =
                "select FSellNetCode,FSellTypeCode,FCuryCode from " +
                pub.yssGetTableName("Tb_TA_FeeLink") +
                " where FStartDate = (select Max(FStartDate) from " +
                pub.yssGetTableName("Tb_TA_FeeLink") +
                " where FSellNetCode = " + dbl.sqlString(sSellNetCode) +
                " and FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                ") and FSellTypeCode = " + dbl.sqlString(sSellTypeCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sellNetCode = rs.getString("FSellNetCode") + ""; //20071021   chenyibo
                this.sellTypeCode = rs.getString("FSellTypeCode") + ""; //20071021   chenyibo
                this.currencyCode = rs.getString("FCuryCode") + "";
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
