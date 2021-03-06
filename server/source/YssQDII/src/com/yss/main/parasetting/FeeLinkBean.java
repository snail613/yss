package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class FeeLinkBean
    extends BaseDataSettingBean implements IDataSetting {
    private String feelinkCode = ""; //费用链接代码
    private String feelinkName = ""; //费用链接名称

    //  private String StartDate; //启用日期
    private String catCode = ""; //品种代码
    private String catName = ""; //品种名称
    private String subcatCode = ""; //品种子代码
    private String subcatName = ""; //品种子名称
    private String cuscatCode = ""; //自定义品种代码
    private String cuscatName = ""; //自定义品种名称
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String exchangeCode = ""; //交易所代码
    private String exchangeName = ""; //交易所名称
    private String curyCode = ""; //货币代码
    private String curyName = ""; //货币名称
    private String tradeTypeCode = ""; //交易类型代码
    private String tradeTypeName = ""; //交易类型名称
    private String sRecycled = ""; //保存未解析前的字符串

    //---------lzp 12.4 add
    private String tradeSeatCode = ""; //席位代码
    private String tradeSeatName = ""; //席位名称
    private String StockholderCode = ""; //股东代码
    private String StockholderName = ""; //股东名称

    //------------
    private String brokerCode = ""; //券商代码
    private String brokerName = ""; //券商名称
    private String feeCode1 = ""; //费用代码1
    private String feeName1 = ""; //费用名称1
    private String feeCode2 = ""; //费用代码2
    private String feeName2 = ""; //费用名称2
    private String feeCode3 = ""; //费用代码3
    private String feeName3 = ""; //费用名称3
    private String feeCode4 = ""; //费用代码4
    private String feeName4 = ""; //费用名称4
    private String feeCode5 = ""; //费用代码5
    private String feeName5 = ""; //费用名称5
    private String feeCode6 = ""; //费用代码6
    private String feeName6 = ""; //费用名称6

    private String desc = ""; //费用链接描述
    private FeeLinkBean filterType;
    private java.util.Date StartDate;
    private java.util.Date OldStartDate;

    private String oldFeelinkCode;

    private String securityCode = ""; //为了获取链接信息 sj add

    public String getTradeSeatName() {
        return tradeSeatName;
    }

    public String getCuryName() {
        return curyName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getFeeCode2() {
        return feeCode2;
    }

    public String getStockholderCode() {
        return StockholderCode;
    }

    public java.util.Date getOldStartDate() {
        return OldStartDate;
    }

    public String getFeeName5() {
        return feeName5;
    }

    public String getFeeName6() {
        return feeName6;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getCuscatName() {
        return cuscatName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getCatCode() {
        return catCode;
    }

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public String getCatName() {
        return catName;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public String getCuscatCode() {
        return cuscatCode;
    }

    public java.util.Date getStartDate() {
        return StartDate;
    }

    public String getFeelinkCode() {
        return feelinkCode;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getFeeName1() {
        return feeName1;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getFeeCode1() {
        return feeCode1;
    }

    public String getPortName() {
        return portName;
    }

    public String getFeeName4() {
        return feeName4;
    }

    public String getTradeSeatCode() {
        return tradeSeatCode;
    }

    public String getDesc() {
        return desc;
    }

    public String getOldFeelinkCode() {
        return oldFeelinkCode;
    }

    public String getSubcatName() {
        return subcatName;
    }

    public String getFeeCode3() {
        return feeCode3;
    }

    public String getFeeCode5() {
        return feeCode5;
    }

    public FeeLinkBean getFilterType() {
        return filterType;
    }

    public String getFeeCode4() {
        return feeCode4;
    }

    public String getTradeTypeName() {
        return tradeTypeName;
    }

    public String getFeeName2() {
        return feeName2;
    }

    public String getStockholderName() {
        return StockholderName;
    }

    public String getFeeName3() {
        return feeName3;
    }

    public String getFeeCode6() {
        return feeCode6;
    }

    public String getFeelinkName() {
        return feelinkName;
    }

    public void setSubcatCode(String subcatCode) {
        this.subcatCode = subcatCode;
    }

    public void setTradeSeatName(String tradeSeatName) {
        this.tradeSeatName = tradeSeatName;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setFeeCode2(String feeCode2) {
        this.feeCode2 = feeCode2;
    }

    public void setStockholderCode(String StockholderCode) {
        this.StockholderCode = StockholderCode;
    }

    public void setOldStartDate(Date OldStartDate) {
        this.OldStartDate = OldStartDate;
    }

    public void setFeeName5(String feeName5) {
        this.feeName5 = feeName5;
    }

    public void setFeeName6(String feeName6) {
        this.feeName6 = feeName6;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setCuscatName(String cuscatName) {
        this.cuscatName = cuscatName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public void setCuscatCode(String cuscatCode) {
        this.cuscatCode = cuscatCode;
    }

    public void setStartDate(Date StartDate) {
        this.StartDate = StartDate;
    }

    public void setFeelinkCode(String feelinkCode) {
        this.feelinkCode = feelinkCode;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setFeeName1(String feeName1) {
        this.feeName1 = feeName1;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setFeeCode1(String feeCode1) {
        this.feeCode1 = feeCode1;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setFeeName4(String feeName4) {
        this.feeName4 = feeName4;
    }

    public void setTradeSeatCode(String tradeSeatCode) {
        this.tradeSeatCode = tradeSeatCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldFeelinkCode(String oldFeelinkCode) {
        this.oldFeelinkCode = oldFeelinkCode;
    }

    public void setSubcatName(String subcatName) {
        this.subcatName = subcatName;
    }

    public void setFeeCode3(String feeCode3) {
        this.feeCode3 = feeCode3;
    }

    public void setFeeCode5(String feeCode5) {
        this.feeCode5 = feeCode5;
    }

    public void setFilterType(FeeLinkBean filterType) {
        this.filterType = filterType;
    }

    public void setFeeCode4(String feeCode4) {
        this.feeCode4 = feeCode4;
    }

    public void setTradeTypeName(String tradeTypeName) {
        this.tradeTypeName = tradeTypeName;
    }

    public void setFeeName2(String feeName2) {
        this.feeName2 = feeName2;
    }

    public void setStockholderName(String StockholderName) {
        this.StockholderName = StockholderName;
    }

    public void setFeeName3(String feeName3) {
        this.feeName3 = feeName3;
    }

    public void setFeeCode6(String feeCode6) {
        this.feeCode6 = feeCode6;
    }

    public void setFeelinkName(String feelinkName) {
        this.feelinkName = feelinkName;
    }

    public String getSubcatCode() {
        return subcatCode;
    }

///   private String oldStartDate;
    public FeeLinkBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.feelinkCode).append("\t");
        buf.append(this.feelinkName).append("\t");
        buf.append(YssFun.formatDate(this.StartDate)).append("\t");
        buf.append(this.catCode).append("\t");
        buf.append(this.catName).append("\t");
        buf.append(this.subcatCode).append("\t");
        buf.append(this.subcatName).append("\t");
        buf.append(this.cuscatCode).append("\t");
        buf.append(this.cuscatName).append("\t");
        buf.append(this.exchangeCode).append("\t");
        buf.append(this.exchangeName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.tradeTypeCode).append("\t");
        buf.append(this.tradeTypeName).append("\t");
        //--------------------
        buf.append(this.tradeSeatCode).append("\t");
        buf.append(this.tradeSeatName).append("\t");
        buf.append(this.StockholderCode).append("\t");
        buf.append(this.StockholderName).append("\t");
        //--------------------
        buf.append(this.feeCode1).append("\t");
        buf.append(this.feeName1).append("\t");
        buf.append(this.feeCode2).append("\t");
        buf.append(this.feeName2).append("\t");
        buf.append(this.feeCode3).append("\t");
        buf.append(this.feeName3).append("\t");
        buf.append(this.feeCode4).append("\t");
        buf.append(this.feeName4).append("\t");
        buf.append(this.feeCode5).append("\t");
        buf.append(this.feeName5).append("\t");
        buf.append(this.feeCode6).append("\t");
        buf.append(this.feeName6).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.filterType.feelinkCode.length() != 0) {
                    sResult = sResult + " and a.FFeeLinkCode like '" +
                        filterType.feelinkCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.feelinkName.length() != 0) {
                    sResult = sResult + " and a.FFeeLinkName like '" +
                        filterType.feelinkName.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.catCode.length() != 0) {
                    sResult = sResult + " and a.FCatCode like '" +
                        filterType.catCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.subcatCode.length() != 0) {
                    sResult = sResult + " and a.FSubCatCode like '" +
                        filterType.subcatCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.cuscatCode.length() != 0) {
                    sResult = sResult + " and a.FCusCatCode like '" +
                        filterType.cuscatCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.StartDate != null &&
                    !this.filterType.StartDate.equals(YssFun.toDate("9998-12-31"))) {
                    sResult = sResult + " and a.FStartDate <= " +
                        dbl.sqlDate(filterType.StartDate);
                }
                if (this.filterType.tradeTypeCode.length() != 0) {
                    sResult = sResult + " and a.FTradeTypeCode like '" +
                        filterType.tradeTypeCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.brokerCode.length() != 0) {
                    sResult = sResult + " and a.FBrokerCode like '" +
                        filterType.brokerCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.curyCode.length() != 0) {
                    sResult = sResult + " and a.FCuryCode like '" +
                        filterType.curyCode.replaceAll("'", "''") + "%'";
                }

                if (this.filterType.exchangeCode.length() != 0) {
                    sResult = sResult + " and a.FExchangeCode like '" +
                        filterType.exchangeCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.portCode.length() != 0) {
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.portCode.replaceAll("'", "''") + "%'";
                }
                //-----------------
                if (this.filterType.tradeSeatCode.length() != 0) {
                    sResult = sResult + " and a.FTradeSeatCode like '" +
                        filterType.tradeSeatCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.StockholderCode.length() != 0) {
                    sResult = sResult + " and a.FStockholderCode like '" +
                        filterType.StockholderCode.replaceAll("'", "''") + "%'";
                }

                //-----------------
                if (this.filterType.feeCode1.length() != 0) {
                    sResult = sResult + " and a.FFeeCode1 like '" +
                        filterType.feeCode1.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.feeCode2.length() != 0) {
                    sResult = sResult + " and a.FFeeCode2 like '" +
                        filterType.feeCode2.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.feeCode3.length() != 0) {
                    sResult = sResult + " and a.FFeeCode3 like '" +
                        filterType.feeCode3.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.feeCode4.length() != 0) {
                    sResult = sResult + " and a.FFeeCode4 like '" +
                        filterType.feeCode4.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.feeCode5.length() != 0) {
                    sResult = sResult + " and a.FFeeCode5 like '" +
                        filterType.feeCode5.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.feeCode6.length() != 0) {
                    sResult = sResult + " and a.FFeeCode6 like '" +
                        filterType.feeCode6.replaceAll("'", "''") + "%'";
                }

            }
        } catch (Exception e) {
            throw new YssException("筛选费用链接设置数据出错", e);
        }

        return sResult;
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("tb_para_tradefeelink"),
                               "FFeeLinkCode,FStartDate",
                               this.feelinkCode + "," +
                               YssFun.formatDate(this.StartDate),
                               this.oldFeelinkCode + "," +
                               YssFun.formatDate(this.OldStartDate));

    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();

        } catch (Exception e) {
            throw new YssException("获取费用链接设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月23号
     * 修改人：单亮
     * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            "(select FFeeLinkCode,FCheckState,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("tb_para_tradefeelink") + " " +
            " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            //修改前的代码
            //"and FCheckState <> 2 group by FFeeLinkCode,FCheckState) x join" +
            //修改后的代码
            //----------------------------
            " group by FFeeLinkCode,FCheckState) x join" + //如果有FCheckState <> 2在回收站不显示全部时今天的也不能显示
            //----------------------------
            "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCatName as FcatName," +
            "e.FSubCatName as FSubCatName,f.FCusCatName as FCusCatName,g.FTradeTypeName as FTradeTypeName," +
            "h.FCuryName as FCuryName,i.FExchangeName as FExchangeName,j.FPortName as FPortName,z.FBrokerName as FBrokerName,m.FSeatName as FTradeSeatName,n.FStockholderName as FStockholderName,k1.FFeeName as FFeeName1," +
            "k2.FFeeName as FFeeName2,k3.FFeeName as FFeeName3,k4.FFeeName as FFeeName4,k5.FFeeName as FFeeName5,k6.FFeeName as FFeeName6" +
            " from " + pub.yssGetTableName("tb_para_tradefeelink") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
            " left join (select FCusCatCode,FCusCatName from " +
            pub.yssGetTableName("Tb_Para_CustomCategory") +
            " where FCheckState = 1) f on a.FCusCatCode = f.FCusCatCode" +
            " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
            " left join (select FCuryCode,FCuryName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1) h on a.FCuryCode = h.FCuryCode" +
            " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
            //----------------
            " left join (select FSeatCode,FSeatName from " +
            pub.yssGetTableName("Tb_Para_TradeSeat") +
            " where FCheckState = 1) m on a.FTradeSeatCode = m.FSeatCode" +
            " left join (select FStockholderCode,FStockholderName from " +
            pub.yssGetTableName("Tb_Para_Stockholder") +
            " where FCheckState = 1) n on a.FStockholderCode = n.FStockholderCode" +
            //----------------
            " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " o where FCheckState = 1 " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//            " and FCheckState = 1 group by FPortCode) p " +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " ) j on a.FPortCode = j.FPortCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            " left join (select x.FBrokerCode as FBrokerCode,x.FBrokerName as FBrokerName from " +
            pub.yssGetTableName("tb_para_broker") +
            " x where FCheckState = 1 " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("tb_para_broker") + " " +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//            " and FCheckState = 1 group by FBrokerCode) y " +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " ) z on z.FBrokerCode = a.FBrokerCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k1 on a.FFeeCode1 = k1.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k2 on a.FFeeCode2 = k2.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k3 on a.FFeeCode3 = k3.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k4 on a.FFeeCode4 = k4.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k5 on a.FFeeCode5 = k5.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k6 on a.FFeeCode6 = k6.FFeeCode" +
            buildFilterSql() +
            ") y on x.FFeeLinkCode = y.FFeeLinkCode and x.FStartDate = y.FStartDate";
        // " order by a.FCheckState, a.FCreateTime desc";
        //  lzp  modify  2007 12.7  由于表太多所以 y.FCheckState, y.FCreateTime 对哪个表指代不明 DB2识别不了  干脆不用

        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
        "(select FFeeLinkCode,max(FStartDate) as FStartDate from " +
        pub.yssGetTableName("tb_para_tradefeelink") + " " +
        " where FStartDate <= " +
        dbl.sqlDate(new java.util.Date()) +
        "and FCheckState = 1 group by FFeeLinkCode) x join" +
        "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCatName as FcatName," +
        "e.FSubCatName as FSubCatName,f.FCusCatName as FCusCatName,g.FTradeTypeName as FTradeTypeName," +
        "h.FCuryName as FCuryName,i.FExchangeName as FExchangeName,j.FPortName as FPortName,z.FBrokerName as FBrokerName,m.FSeatName as FTradeSeatName,n.FStockholderName as FStockholderName,k1.FFeeName as FFeeName1," +
        "k2.FFeeName as FFeeName2,k3.FFeeName as FFeeName3,k4.FFeeName as FFeeName4,k5.FFeeName as FFeeName5,k6.FFeeName as FFeeName6" +
        " from " + pub.yssGetTableName("tb_para_tradefeelink") + " a " +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
        " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
        " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
        " left join (select FCusCatCode,FCusCatName from " +
        pub.yssGetTableName("Tb_Para_CustomCategory") +
        " where FCheckState = 1) f on a.FCusCatCode = f.FCusCatCode" +
        //edit by songjie 2011.03.17 FTradeypeName 改为 FTradetypeName
        " left join (select FTradeTypeCode,FTradetypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
        " left join (select FCuryCode,FCuryName from " +
        pub.yssGetTableName("Tb_Para_Currency") +
        " where FCheckState = 1) h on a.FCuryCode = h.FCuryCode" +
        " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
        //----------------
        " left join (select FSeatCode,FSeatName from " +
        pub.yssGetTableName("Tb_Para_TradeSeat") +
        " where FCheckState = 1) m on a.FTradeSeatCode = m.FSeatCode" +
        " left join (select FStockholderCode,FStockholderName from " +
        pub.yssGetTableName("Tb_Para_Stockholder") +
        " where FCheckState = 1) n on a.FStockholderCode = n.FStockholderCode" +
        //----------------

        " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
        pub.yssGetTableName("Tb_Para_Portfolio") +
        //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//        " o join (select FPortCode,max(FStartDate) as FStartDate from " +
//        pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//        " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//        " and FCheckState = 1 group by FPortCode) p " +
        //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
        " o where FCheckState = 1) j on a.FPortCode = j.FPortCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
        " left join (select x.FBrokerCode as FBrokerCode,x.FBrokerName as FBrokerName from " +
        pub.yssGetTableName("tb_para_broker") +
        //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//        " x join (select FBrokerCode,max(FStartDate) as FStartDate from " +
//        pub.yssGetTableName("tb_para_broker") + " " +
//        " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//        " and FCheckState = 1 group by FBrokerCode) y " +
        //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
        " x where FCheckState = 1 ) z on z.FBrokerCode = a.FBrokerCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
        " left join (select FFeeCode,FFeeName from " +
        pub.yssGetTableName("Tb_Para_Fee") +
        " where FCheckState = 1) k1 on a.FFeeCode1 = k1.FFeeCode" +
        " left join (select FFeeCode,FFeeName from " +
        pub.yssGetTableName("Tb_Para_Fee") +
        " where FCheckState = 1) k2 on a.FFeeCode2 = k2.FFeeCode" +
        " left join (select FFeeCode,FFeeName from " +
        pub.yssGetTableName("Tb_Para_Fee") +
        " where FCheckState = 1) k3 on a.FFeeCode3 = k3.FFeeCode" +
        " left join (select FFeeCode,FFeeName from " +
        pub.yssGetTableName("Tb_Para_Fee") +
        " where FCheckState = 1) k4 on a.FFeeCode4 = k4.FFeeCode" +
        " left join (select FFeeCode,FFeeName from " +
        pub.yssGetTableName("Tb_Para_Fee") +
        " where FCheckState = 1) k5 on a.FFeeCode5 = k5.FFeeCode" +
        " left join (select FFeeCode,FFeeName from " +
        pub.yssGetTableName("Tb_Para_Fee") +
        " where FCheckState = 1) k6 on a.FFeeCode6 = k6.FFeeCode" +
        buildFilterSql() +
        ") y on x.FFeeLinkCode = y.FFeeLinkCode and x.FStartDate = y.FStartDate";
        // " order by a.FCheckState, a.FCreateTime desc";
        //  lzp  modify  2007 12.7  由于表太多所以 y.FCheckState, y.FCreateTime 对哪个表指代不明 DB2识别不了  干脆不用
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCatName as FcatName," +
            "e.FSubCatName as FSubCatName,f.FCusCatName as FCusCatName,g.FTradeTypeName as FTradeTypeName," +
            "h.FCuryName as FCuryName,i.FExchangeName as FExchangeName,j.FPortName as FPortName,z.FBrokerName as FBrokerName,m.FSeatName as FTradeSeatName,n.FStockholderName as FStockholderName,k1.FFeeName as FFeeName1," +
            "k2.FFeeName as FFeeName2,k3.FFeeName as FFeeName3,k4.FFeeName as FFeeName4,k5.FFeeName as FFeeName5,k6.FFeeName as FFeeName6" +
            " from " + pub.yssGetTableName("tb_para_tradefeelink") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
            " left join (select FCusCatCode,FCusCatName from " +
            pub.yssGetTableName("Tb_Para_CustomCategory") +
            " where FCheckState = 1) f on a.FCusCatCode = f.FCusCatCode" +
            " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
            " left join (select FCuryCode,FCuryName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1) h on a.FCuryCode = h.FCuryCode" +
            " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
            //----------------
            " left join (select FSeatCode,FSeatName from " +
            pub.yssGetTableName("Tb_Para_TradeSeat") +
            " where FCheckState = 1) m on a.FTradeSeatCode = m.FSeatCode" +
            " left join (select FStockholderCode,FStockholderName from " +
            pub.yssGetTableName("Tb_Para_Stockholder") +
            " where FCheckState = 1) n on a.FStockholderCode = n.FStockholderCode" +
            //----------------

            " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " o where FCheckState = 1 " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//            " and FCheckState = 1 group by FPortCode) p " +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " ) j on a.FPortCode = j.FPortCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            " left join (select x.FBrokerCode as FBrokerCode,x.FBrokerName as FBrokerName from " +
            pub.yssGetTableName("tb_para_broker") +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            " x join (select FBrokerCode,max(FStartDate) as FStartDate from " +
//            pub.yssGetTableName("tb_para_broker") + " " +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//            " and FCheckState = 1 group by FBrokerCode) y " +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " x where FCheckState = 1) z on z.FBrokerCode = a.FBrokerCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k1 on a.FFeeCode1 = k1.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k2 on a.FFeeCode2 = k2.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k3 on a.FFeeCode3 = k3.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k4 on a.FFeeCode4 = k4.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k5 on a.FFeeCode5 = k5.FFeeCode" +
            " left join (select FFeeCode,FFeeName from " +
            pub.yssGetTableName("Tb_Para_Fee") +
            " where FCheckState = 1) k6 on a.FFeeCode6 = k6.FFeeCode" +
            buildFilterSql();
        // " order by a.FCheckState, a.FCreateTime desc";
        //  lzp  modify  2007 12.7  由于表太多所以 y.FCheckState, y.FCreateTime 对哪个表指代不明 DB2识别不了  干脆不用
        return this.builderListViewData(strSql);
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * parseRowStr
     * 解析费用连接设置
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.feelinkCode = reqAry[0];
            this.feelinkName = reqAry[1];
            this.StartDate = YssFun.toDate(reqAry[2]);
            this.catCode = reqAry[3];
            this.catName = reqAry[4];
            this.subcatCode = reqAry[5];
            this.subcatName = reqAry[6];
            this.cuscatCode = reqAry[7];
            this.cuscatName = reqAry[8];
            this.exchangeCode = reqAry[9];
            this.exchangeName = reqAry[10];
            this.portCode = reqAry[11];
            this.portName = reqAry[12];
            this.curyCode = reqAry[13];
            this.curyName = reqAry[14];
            this.tradeTypeCode = reqAry[15];
            this.tradeTypeName = reqAry[16];
            //--------------------
            this.tradeSeatCode = reqAry[17];
            this.tradeSeatName = reqAry[18];
            this.StockholderCode = reqAry[19];
            this.StockholderName = reqAry[20];
            //--------------------

            this.feeCode1 = reqAry[21];
            this.feeName1 = reqAry[22];
            this.feeCode2 = reqAry[23];
            this.feeName2 = reqAry[24];
            this.feeCode3 = reqAry[25];
            this.feeName3 = reqAry[26];
            this.feeCode4 = reqAry[27];
            this.feeName4 = reqAry[28];
            this.feeCode5 = reqAry[29];
            this.feeName5 = reqAry[30];
            this.feeCode6 = reqAry[31];
            this.feeName6 = reqAry[32];
            this.brokerCode = reqAry[33];
            this.brokerName = reqAry[34];
            //---add by songjie 2011.04.13 BUG 1669 QDV4赢时胜(测试)2011年4月8日02_B---//
           	if (reqAry[35] != null && reqAry[35].indexOf("【Enter】") > -1){
           		 this.desc= reqAry[35].replaceAll("【Enter】", "\r\n");
            }else{
               	 this.desc = reqAry[35];
            }
            //---add by songjie 2011.04.13 BUG 1669 QDV4赢时胜(测试)2011年4月8日02_B---//
            this.checkStateId = Integer.parseInt(reqAry[36]);
            this.OldStartDate = YssFun.toDate(reqAry[37]);
            this.oldFeelinkCode = reqAry[38];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new FeeLinkBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析费用链接设置请求出错", e);
        }

    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
           if (btOper == YssCons.OP_ADD) {
              strSql = "insert into " +
                    pub.yssGetTableName("tb_para_tradefeelink") +
                    " (FFeeLinkCode,FFeeLinkName,FStartDate,FCatCode,FSubCatCode,FCusCatCode,FTradeTypeCode,FCuryCode,FExchangeCode,FPortCode,FBrokerCode," +
                    " FFeeCode1,FFeeCode2,FFeeCode3,FFeeCode4,FFeeCode5,FFeeCode6,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.feelinkCode) + "," +
                    dbl.sqlString(this.feelinkName) + "," +
                    dbl.sqlDate(this.StartDate) + "," +
     dbl.sqlString(this.catCode.length() == 0? " " : this.catCode) + "," +
     dbl.sqlString(this.subcatCode.length() == 0? " " : this.subcatCode) + "," +
     dbl.sqlString(this.cuscatCode.length() == 0? " " : this.cuscatCode) + "," +
                    dbl.sqlString(this.tradeTypeCode.length() == 0? " " : this.tradeTypeCode) + "," +
     dbl.sqlString(this.curyCode.length() == 0? " " : this.curyCode) + "," +
     dbl.sqlString(this.exchangeCode.length() == 0? " " : this.exchangeCode) + "," +
     dbl.sqlString(this.portCode.length() == 0? " " : this.portCode) + "," +
     dbl.sqlString(this.brokerCode.length() == 0? " " : this.brokerCode) + "," +
                    dbl.sqlString(this.feeCode1) + "," +
                    dbl.sqlString(this.feeCode2) + "," +
                    dbl.sqlString(this.feeCode3) + "," +
                    dbl.sqlString(this.feeCode4) + "," +
                    dbl.sqlString(this.feeCode5) + "," +
                    dbl.sqlString(this.feeCode6) + "," +
                    dbl.sqlString(this.desc) + "," +
                    (pub.getSysCheckState()?"0":"1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("tb_para_tradefeelink") +
                    " set FFeeLinkCode = " +
                    dbl.sqlString(this.feelinkCode) + ", FFeeLinkName = " +
                    dbl.sqlString(this.feelinkName) + " , FStartDate = " +
                    dbl.sqlDate(this.StartDate) + " , FCatCode = " +
                    dbl.sqlString(this.catCode.length() == 0 ? " " : this.catCode) + ", FSubCatCode = " +
                    dbl.sqlString(this.subcatCode.length() == 0? " " : this.subcatCode) + ", FCusCatCode = " +
                    dbl.sqlString(this.cuscatCode.length() == 0? " " : this.cuscatCode) + ",FTradeTypeCode = " +
                    dbl.sqlString(this.tradeTypeCode.length() == 0? " " : this.tradeTypeCode) + ",FCuryCode = " +
                    dbl.sqlString(this.curyCode.length() == 0? " " : this.curyCode) + ",FExchangeCode = " +
                    dbl.sqlString(this.exchangeCode.length() == 0? " " : this.exchangeCode) + ",FPortCode = " +
                    dbl.sqlString(this.portCode.length() == 0? " " : this.portCode) + ",FBrokerCode = " +
                    dbl.sqlString(this.brokerCode.length() == 0? " " : this.brokerCode) + ", FFeeCode1 = " +
                    dbl.sqlString(this.feeCode1) + ",FFeeCode2 = " +
                    dbl.sqlString(this.feeCode2) + ",FFeeCode3 = " +
                    dbl.sqlString(this.feeCode3) + ",FFeeCode4 = " +
                    dbl.sqlString(this.feeCode4) + ",FFeeCode5 = " +
                    dbl.sqlString(this.feeCode5) + ",FFeeCode6 = " +
                    dbl.sqlString(this.feeCode6) + ",FDesc = " +
                    dbl.sqlString(this.desc) + ",FCheckState = " +
                    (pub.getSysCheckState()?"0":"1") + ", FCreator = " +
                    dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                    " where FFeeLinkCode = " +
                    dbl.sqlString(this.oldFeelinkCode) +
                    " and FStartDate=" + dbl.sqlDate(this.OldStartDate);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("tb_para_tradefeelink") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FFeeLinkCode = " + dbl.sqlString(this.feelinkCode) +
                    " and FStartDate=" + dbl.sqlDate(this.StartDate);

           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("tb_para_tradefeelink") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FFeeLinkCode = " + dbl.sqlString(this.feelinkCode) +
                    " and FStartDate=" + dbl.sqlDate(this.StartDate);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新费用链接设置信息出错", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }

     }
     */
    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " +
                pub.yssGetTableName("tb_para_tradefeelink") +
                " (FFeeLinkCode,FFeeLinkName,FStartDate,FCatCode,FSubCatCode,FCusCatCode,FTradeTypeCode,FCuryCode,FExchangeCode,FPortCode,FBrokerCode," +
                " FTradeSeatCode,FStockholderCode,FFeeCode1,FFeeCode2,FFeeCode3,FFeeCode4,FFeeCode5,FFeeCode6,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.feelinkCode) + "," +
                dbl.sqlString(this.feelinkName) + "," +
                dbl.sqlDate(this.StartDate) + "," +
                dbl.sqlString(this.catCode.length() == 0 ? " " : this.catCode) +
                "," +
                dbl.sqlString(this.subcatCode.length() == 0 ? " " :
                              this.subcatCode) + "," +
                dbl.sqlString(this.cuscatCode.length() == 0 ? " " :
                              this.cuscatCode) + "," +
                dbl.sqlString(this.tradeTypeCode.length() == 0 ? " " :
                              this.tradeTypeCode) + "," +
                dbl.sqlString(this.curyCode.length() == 0 ? " " : this.curyCode) +
                "," +
                dbl.sqlString(this.exchangeCode.length() == 0 ? " " :
                              this.exchangeCode) + "," +
                dbl.sqlString(this.portCode.length() == 0 ? " " : this.portCode) +
                "," +
                dbl.sqlString(this.brokerCode.length() == 0 ? " " :
                              this.brokerCode) + "," +
                dbl.sqlString(this.tradeSeatCode.length() == 0 ? " " :
                              this.tradeSeatCode) + "," +
                dbl.sqlString(this.StockholderCode.length() == 0 ? " " :
                              this.StockholderCode) + "," +
                dbl.sqlString(this.feeCode1) + "," +
                dbl.sqlString(this.feeCode2) + "," +
                dbl.sqlString(this.feeCode3) + "," +
                dbl.sqlString(this.feeCode4) + "," +
                dbl.sqlString(this.feeCode5) + "," +
                dbl.sqlString(this.feeCode6) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加费用链接设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("tb_para_tradefeelink") +
                " set FFeeLinkCode = " +
                dbl.sqlString(this.feelinkCode) + ", FFeeLinkName = " +
                dbl.sqlString(this.feelinkName) + " , FStartDate = " +
                dbl.sqlDate(this.StartDate) + " , FCatCode = " +
                dbl.sqlString(this.catCode.length() == 0 ? " " : this.catCode) +
                ", FSubCatCode = " +
                dbl.sqlString(this.subcatCode.length() == 0 ? " " :
                              this.subcatCode) + ", FCusCatCode = " +
                dbl.sqlString(this.cuscatCode.length() == 0 ? " " :
                              this.cuscatCode) + ",FTradeTypeCode = " +
                dbl.sqlString(this.tradeTypeCode.length() == 0 ? " " :
                              this.tradeTypeCode) + ",FCuryCode = " +
                dbl.sqlString(this.curyCode.length() == 0 ? " " : this.curyCode) +
                ",FExchangeCode = " +
                dbl.sqlString(this.exchangeCode.length() == 0 ? " " :
                              this.exchangeCode) + ",FPortCode = " +
                dbl.sqlString(this.portCode.length() == 0 ? " " : this.portCode) +
                ",FBrokerCode = " +
                dbl.sqlString(this.brokerCode.length() == 0 ? " " :
                              this.brokerCode) + ", FTradeSeatCode = " +
                dbl.sqlString(this.tradeSeatCode.length() == 0 ? " " :
                              this.tradeSeatCode) + ", FStockholderCode = " +
                dbl.sqlString(this.StockholderCode.length() == 0 ? " " :
                              this.StockholderCode) + ", FFeeCode1 = " +
                dbl.sqlString(this.feeCode1) + ",FFeeCode2 = " +
                dbl.sqlString(this.feeCode2) + ",FFeeCode3 = " +
                dbl.sqlString(this.feeCode3) + ",FFeeCode4 = " +
                dbl.sqlString(this.feeCode4) + ",FFeeCode5 = " +
                dbl.sqlString(this.feeCode5) + ",FFeeCode6 = " +
                dbl.sqlString(this.feeCode6) + ",FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FFeeLinkCode = " +
                dbl.sqlString(this.oldFeelinkCode) +
                " and FStartDate=" + dbl.sqlDate(this.OldStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改费用链接设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     *  删除期间连接的数据，即是放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("tb_para_tradefeelink") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FFeeLinkCode = " + dbl.sqlString(this.feelinkCode) +
                " and FStartDate=" + dbl.sqlDate(this.StartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除费用链接设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改时间：2008年3月23号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      try {
//         strSql = "update " + pub.yssGetTableName("tb_para_tradefeelink") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where FFeeLinkCode = " + dbl.sqlString(this.feelinkCode) +
//               " and FStartDate=" + dbl.sqlDate(this.StartDate);
//
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核费用链接设置信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //---------------------------------------------
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("tb_para_tradefeelink") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FFeeLinkCode = " + dbl.sqlString(this.feelinkCode) +
                        " and FStartDate=" + dbl.sqlDate(this.StartDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (feelinkCode != null && !feelinkCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("tb_para_tradefeelink") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FFeeLinkCode = " + dbl.sqlString(this.feelinkCode) +
                    " and FStartDate=" + dbl.sqlDate(this.StartDate);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核费用链接设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //------------------------------------------
    }

    public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.feelinkCode = rs.getString("FFeeLinkCode") + "";
        this.feelinkName = rs.getString("FFeeLinkName") + "";
        this.StartDate = rs.getDate("FStartDate");
        this.catCode = rs.getString("FCatCode") + "";
        this.catName = rs.getString("FCatName") + "";
        this.subcatCode = rs.getString("FSubCatCode") + "";
        this.subcatName = rs.getString("FSubCatName") + "";
        this.cuscatCode = rs.getString("FCusCatCode") + "";
        this.cuscatName = rs.getString("FCusCatName") + "";
        this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
        this.tradeTypeName = rs.getString("FTradeTypeName") + "";
        this.curyCode = rs.getString("FCuryCode") + "";
        this.curyName = rs.getString("FCuryName") + "";
        this.exchangeCode = rs.getString("FExchangeCode") + "";
        this.exchangeName = rs.getString("FExchangeName") + "";
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        //--------------------
        this.tradeSeatCode = rs.getString("FTradeSeatCode") + "";
        this.tradeSeatName = rs.getString("FTradeSeatName") + "";
        this.StockholderCode = rs.getString("FStockholderCode") + "";
        this.StockholderName = rs.getString("FStockholderName") + "";
        //---------------------
        this.feeCode1 = rs.getString("FFeeCode1") + "";
        this.feeName1 = rs.getString("FFeeName1") + "";
        this.feeCode2 = rs.getString("FFeeCode2") + "";
        this.feeName2 = rs.getString("FFeeName2") + "";
        this.feeCode3 = rs.getString("FFeeCode3") + "";
        this.feeName3 = rs.getString("FFeeName3") + "";
        this.feeCode4 = rs.getString("FFeeCode4") + "";
        this.feeName4 = rs.getString("FFeeName4") + "";
        this.feeCode5 = rs.getString("FFeeCode5") + "";
        this.feeName5 = rs.getString("FFeeName5") + "";
        this.feeCode6 = rs.getString("FFeeCode6") + "";
        this.feeName6 = rs.getString("FFeeName6") + "";
        this.brokerCode = rs.getString("FBrokerCode") + "";
        this.brokerName = rs.getString("FBrokerName") + "";
        this.desc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        FeeLinkBean befEditBean = new FeeLinkBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FFeeLinkCode,FCheckState,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("tb_para_tradefeelink") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                "and FCheckState <> 2 group by FFeeLinkCode,FCheckState) x join" +
                "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCatName as FcatName," +
                "e.FSubCatName as FSubCatName,f.FCusCatName as FCusCatName,g.FTradeTypeName as FTradeTypeName," +
                "h.FCuryName as FCuryName,i.FExchangeName as FExchangeName,j.FPortName as FPortName,z.FBrokerName as FBrokerName,m.FSeatName as FTradeSeatName,n.FStockholderName as FStockholderName,k1.FFeeName as FFeeName1," +
                "k2.FFeeName as FFeeName2,k3.FFeeName as FFeeName3,k4.FFeeName as FFeeName4,k5.FFeeName as FFeeName5,k6.FFeeName as FFeeName6" +
                " from " + pub.yssGetTableName("tb_para_tradefeelink") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " where FCheckState = 1) f on a.FCusCatCode = f.FCusCatCode" +
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) h on a.FCuryCode = h.FCuryCode" +
                " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
                //----------------
                " left join (select FSeatCode,FSeatName from " +
                pub.yssGetTableName("Tb_Para_TradeSeat") +
                " where FCheckState = 1) m on a.FTradeSeatCode = m.FSeatCode" +
                " left join (select FStockholderCode,FStockholderName from " +
                pub.yssGetTableName("Tb_Para_Stockholder") +
                " where FCheckState = 1) n on a.FStockholderCode = n.FStockholderCode" +
                //----------------

                " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                " o join (select FPortCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode) p " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " o where FCheckState = 1) j on a.FPortCode = j.FPortCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                " left join (select x.FBrokerCode as FBrokerCode,x.FBrokerName as FBrokerName from " +
                pub.yssGetTableName("tb_para_broker") +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                " x join (select FBrokerCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("tb_para_broker") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FBrokerCode) y " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " x where FCheckState = 1) z on z.FBrokerCode = a.FBrokerCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " where FCheckState = 1) k1 on a.FFeeCode1 = k1.FFeeCode" +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " where FCheckState = 1) k2 on a.FFeeCode2 = k2.FFeeCode" +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " where FCheckState = 1) k3 on a.FFeeCode3 = k3.FFeeCode" +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " where FCheckState = 1) k4 on a.FFeeCode4 = k4.FFeeCode" +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " where FCheckState = 1) k5 on a.FFeeCode5 = k5.FFeeCode" +
                " left join (select FFeeCode,FFeeName from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                " where FCheckState = 1) k6 on a.FFeeCode6 = k6.FFeeCode" +
                " where  FFeeLinkCode =" + dbl.sqlString(this.oldFeelinkCode) +
                ") y on x.FFeeLinkCode = y.FFeeLinkCode and x.FStartDate = y.FStartDate";
            // " order by a.FCheckState, a.FCreateTime desc";
            //  lzp  modify  2007 12.7  由于表太多所以 y.FCheckState, y.FCreateTime 对哪个表指代不明 DB2识别不了  干脆不用

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.feelinkCode = rs.getString("FFeeLinkCode") + "";
                befEditBean.feelinkName = rs.getString("FFeeLinkName") + "";
                befEditBean.StartDate = rs.getDate("FStartDate");
                befEditBean.catCode = rs.getString("FCatCode") + "";
                befEditBean.catName = rs.getString("FCatName") + "";
                befEditBean.subcatCode = rs.getString("FSubCatCode") + "";
                befEditBean.subcatName = rs.getString("FSubCatName") + "";
                befEditBean.cuscatCode = rs.getString("FCusCatCode") + "";
                befEditBean.cuscatName = rs.getString("FCusCatName") + "";
                befEditBean.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                befEditBean.tradeTypeName = rs.getString("FTradeTypeName") + "";
                befEditBean.curyCode = rs.getString("FCuryCode") + "";
                befEditBean.curyName = rs.getString("FCuryName") + "";
                befEditBean.exchangeCode = rs.getString("FExchangeCode") + "";
                befEditBean.exchangeName = rs.getString("FExchangeName") + "";
                befEditBean.portCode = rs.getString("FPortCode") + "";
                befEditBean.portName = rs.getString("FPortName") + "";
                //--------------------
                befEditBean.tradeSeatCode = rs.getString("FTradeSeatCode") + "";
                befEditBean.tradeSeatName = rs.getString("FTradeSeatName") + "";
                befEditBean.StockholderCode = rs.getString("FStockholderCode") + "";
                befEditBean.StockholderName = rs.getString("FStockholderName") + "";
                //---------------------

                befEditBean.feeCode1 = rs.getString("FFeeCode1") + "";
                befEditBean.feeName1 = rs.getString("FFeeName1") + "";
                befEditBean.feeCode2 = rs.getString("FFeeCode2") + "";
                befEditBean.feeName2 = rs.getString("FFeeName2") + "";
                befEditBean.feeCode3 = rs.getString("FFeeCode3") + "";
                befEditBean.feeName3 = rs.getString("FFeeName3") + "";
                befEditBean.feeCode4 = rs.getString("FFeeCode4") + "";
                befEditBean.feeName4 = rs.getString("FFeeName4") + "";
                befEditBean.feeCode5 = rs.getString("FFeeCode5") + "";
                befEditBean.feeName5 = rs.getString("FFeeName5") + "";
                befEditBean.feeCode6 = rs.getString("FFeeCode6") + "";
                befEditBean.feeName6 = rs.getString("FFeeName6") + "";
                befEditBean.brokerCode = rs.getString("FBrokerCode") + "";
                befEditBean.brokerName = rs.getString("FBrokerName") + "";
                befEditBean.desc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * 从期间连接回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && sRecycled != "") {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("tb_para_tradefeelink") +
                        " where FFeeLinkCode = " + dbl.sqlString(this.feelinkCode) +
                        " and FStartDate=" + dbl.sqlDate(this.StartDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }

            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (feelinkCode != null && feelinkCode != "") {
                strSql = "delete from " +
                    pub.yssGetTableName("tb_para_tradefeelink") +
                    " where FFeeCode = " + dbl.sqlString(this.feelinkCode) +
                    " and FStartDate=" + dbl.sqlDate(this.StartDate);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
}
