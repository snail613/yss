package com.yss.main.operdeal.linkInfo;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.util.ArrayList;
import com.yss.main.parasetting.FeeBean;
import com.yss.main.parasetting.FeeLinkBean;
import java.sql.ResultSet;
import com.yss.util.YssCons;

public class FeeLinkDeal
    extends BaseLinkInfoDeal {
    private FeeLinkBean feeLink = null;
    private FeeBean fee = null;
    private String tradeTypeCode = "";
    private String portCode = "";
    private String brokerCode = "";
    private String catCode = "";
    private String subCatCode = "";
    private String cusCatCode = "";
    private String currencyCode = "";
    private String exchangeCode = "";
    private String tradeSeatCode = "";
    private String stockholderCode = "";
    public FeeLinkDeal() {
    }

    public void setLinkAttr(BaseDataSettingBean LinkInfoBean) throws
        YssException {
        feeLink = (FeeLinkBean) LinkInfoBean;
        if (feeLink != null) {
            setFeeAttr(feeLink.getSecurityCode(),
                       feeLink.getTradeTypeCode(),
                       feeLink.getPortCode(),
                       feeLink.getBrokerCode(),
                       feeLink.getTradeSeatCode(),
                       feeLink.getStockholderCode());
        }
    }

    public String buildLinkCondition() {
        StringBuffer buf = new StringBuffer();
        buf.append("FPortCode = " + dbl.sqlString(this.portCode)).append("\t");
        buf.append("FExchangeCode = " +
                   dbl.sqlString(this.exchangeCode)).append("\t");
        buf.append("FBrokerCode = " + dbl.sqlString(this.brokerCode)).append("\t");
        buf.append("FCuryCode = " + dbl.sqlString(this.currencyCode)).append("\t");
        buf.append("FTradeTypeCode = " +
                   dbl.sqlString(this.tradeTypeCode)).append("\t");
        buf.append("FCusCatCode = " + dbl.sqlString(this.cusCatCode)).append("\t");
        buf.append("FSubCatCode = " + dbl.sqlString(this.subCatCode)).append("\t");
        buf.append("FCatCode = " + dbl.sqlString(this.catCode)).append("\t");
        return buf.toString();
    }

    public String createTempData() throws YssException {
        String strSql = "";
        String sTmpTableName = "";
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
            sTmpTableName = "V_Tmp_TradeFeeLink_" + pub.getUserCode();
            if (dbl.yssViewExist(sTmpTableName)) {
                dbl.executeSql("drop view " + sTmpTableName);
            }
            String strTemp = "create view " + sTmpTableName + " as (" + strSql + ")";
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

            return sTmpTableName;
        } catch (Exception e) {
            throw new YssException("获取数据至临时存储处出错");
        }

    }

    public Object getBeans(String sFeeCond) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ArrayList list = null;
        FeeBean fee = null;
        try {
        	//---add by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B start---//
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
        	//---add by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B end---//
            if (sFeeCond.trim().length() == 0) {
            	//edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
                strSql = "select * from (" + strSql + ")";
            } else {
            	//edit by songjie 2011.12.26 BUG 3413 QDV4赢时胜（测试）2011年12月15日01_B
                strSql = "select * from (" + strSql + ") where " +
                    sFeeCond;
            }
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                list = new ArrayList();
                for (int j = 1; j <= 6; j++) {
                    if (rs.getString("FFeeCode" + j) != null) {
                        fee = new FeeBean();
                        fee.setYssPub(pub);
                        fee.setFeeCode(rs.getString("FFeeCode" + j));
                        fee.getSetting(); //这里是为了设置FeeBean的属性
                        if (fee.checkStateId == 1) {
                            list.add(fee);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("获取链接数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
            return list;
        }

    }

    public void setFeeAttr(String sSecurityCode, String sTradeType,
                           String sPortCode, String sBrokerCode) throws
        YssException {
        setFeeAttr(sSecurityCode, sTradeType, sPortCode, sBrokerCode,
                   "", "");
    }

    public void setFeeAttr(String sSecurityCode, String sTradeType,
                           String sPortCode, String sBrokerCode, String tradeSeatCode,
                           String stockholderCode) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            this.tradeTypeCode = sTradeType;
            this.portCode = sPortCode;
            this.brokerCode = sBrokerCode;
            this.tradeSeatCode = tradeSeatCode;
            this.stockholderCode = stockholderCode;
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

}
