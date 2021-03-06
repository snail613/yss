package com.yss.main.orderadmin;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.pojo.sys.*;
import com.yss.util.*;

public class MaintenanceBean
    extends BaseDataSettingBean implements IDataSetting {
    private String invMgrCode = "";
    private String invMgrName = "";
    private String orderNum = "";
    private java.util.Date orderDate;
    private String orderTime = "00:00:00";
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String securityCode = ""; //证券代码
    private String securityName = ""; //证券名称
    private String tradeCode = ""; //交易方式代码
    private String tradeName = ""; //交易方式名称
    private String brokerCode = ""; //券商代码
    private String brokerName = ""; //券商名称
    private double tradeAmount; //交易数量
    private double tradePrice; //交易价格
    private double tradeTotal; //交易总额
    private String tradeNum = ""; //成交编号
    private String quoteMode = ""; //报价方式
    private double interest; //应收利息
    private String desc = ""; //描述
    private double virtualPrice; //虚拟拆分价格
    private double Yield = 0; //收益率

    private String isOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private String oldSecurityCode = "";
    private String oldOrderNum = "";
    private String oldBrokerCode = "";
    private String oldPortCode = "";
    private String oldInvMgrCode = "";
    private MaintenanceBean filterType;
    private double baseCuryRate = 1; //基础汇率
    private String tradeCuryCode = ""; //交易货币代码
    public String getTradeCode() {
        return tradeCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public java.util.Date getOrderDate() {
        return orderDate;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getQuoteMode() {
        return quoteMode;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getInvMgrName() {
        return invMgrName;
    }

    public String getIsOnlyColumns() {
        return isOnlyColumns;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public String getOldOrderNum() {
        return oldOrderNum;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public String getPortName() {
        return portName;
    }

    public String getDesc() {
        return desc;
    }

    public String getOldSecurityCode() {
        return oldSecurityCode;
    }

    public MaintenanceBean getFilterType() {
        return filterType;
    }

    public String getOldBrokerCode() {
        return oldBrokerCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public double getTradeAmount() {
        return tradeAmount;
    }

    public String getSecurityName() {
        return securityName;
    }

    public double getInterest() {
        return interest;
    }

    public String getTradeName() {
        return tradeName;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getOldInvMgrCode() {
        return oldInvMgrCode;
    }

    public void setTradeTotal(double tradeTotal) {
        this.tradeTotal = tradeTotal;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setOrderDate(java.util.Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setQuoteMode(String quoteMode) {
        this.quoteMode = quoteMode;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setInvMgrName(String invMgrName) {
        this.invMgrName = invMgrName;
    }

    public void setIsOnlyColumns(String isOnlyColumns) {
        this.isOnlyColumns = isOnlyColumns;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setOldOrderNum(String oldOrderNum) {
        this.oldOrderNum = oldOrderNum;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }

    public void setFilterType(MaintenanceBean filterType) {
        this.filterType = filterType;
    }

    public void setOldBrokerCode(String oldBrokerCode) {
        this.oldBrokerCode = oldBrokerCode;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setTradeAmount(double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void setOldInvMgrCode(String oldInvMgrCode) {
        this.oldInvMgrCode = oldInvMgrCode;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setTradeCuryCode(String tradeCuryCode) {
        this.tradeCuryCode = tradeCuryCode;
    }

    public void setVirtualPrice(double virtualPrice) {
        this.virtualPrice = virtualPrice;
    }

    public void setYield(double Yield) {
        this.Yield = Yield;
    }

    public double getTradeTotal() {
        return tradeTotal;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getTradeCuryCode() {
        return tradeCuryCode;
    }

    public double getVirtualPrice() {
        return virtualPrice;
    }

    public double getYield() {
        return Yield;
    }

    public MaintenanceBean() {
    }

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
            strSql = " where " + dbl.sqlLeft("FOrderNum", 8) + " = " +
                dbl.sqlString(YssFun.formatDate(this.orderDate, "yyyyMMdd"));
            this.orderNum = dbFun.getNextInnerCode(pub.yssGetTableName(
                "tb_order_maintenance"), dbl.sqlRight("FOrderNum", 6), "000001",
                strSql);
            this.orderNum = YssFun.formatDate(this.orderDate, "yyyyMMdd") +
                this.orderNum;
            strSql = "insert into " + pub.yssGetTableName("tb_order_maintenance") +
                " " +
                "(FInvMgrCode,FSecurityCode,FOrderNum,FBrokerCode,FPortCode,FTradeTypeCode," +
                " FTradeAmount,FInterest,FTradePrice,FVirtualPrice,FOrderDate,FTradeNum,FQuoteMode," +
                " FOrderTime, FDesc, FCheckState, FCreator, FCreateTime, FCheckUser,FYield)" +
                " values('" + pub.getUserCode() + "'," +
                dbl.sqlString(this.securityCode) + "," +
                dbl.sqlString(this.orderNum) + " ," +
                dbl.sqlString(this.brokerCode) + "," +
                dbl.sqlString( (this.portCode.length() == 0 ? " " :
                                this.portCode)) + " ," +
                dbl.sqlString(this.tradeCode) + "," +
                this.tradeAmount + " ," +
                this.interest + " ," +
                this.tradePrice + "," +
                this.virtualPrice + "," +
                dbl.sqlDate(this.orderDate) + " ,''," +
                dbl.sqlString(this.quoteMode) + "," +
                dbl.sqlString(this.orderTime) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," +
                (this.Yield) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增订单制作信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.orderNum).append("\t");
        buf.append(YssFun.formatDate(this.orderDate)).append("\t");
        buf.append(this.orderTime).append("\t");
        buf.append(this.invMgrCode).append("\t");
        buf.append(this.invMgrName).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.tradeCode).append("\t");
        buf.append(this.tradeName).append("\t");
        buf.append(this.tradeAmount).append("\t");
        buf.append(this.interest).append("\t");
        buf.append(this.quoteMode).append("\t");
        buf.append(this.tradePrice).append("\t");
        buf.append(this.tradeTotal).append("\t");
        buf.append(this.tradeNum).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.tradeCuryCode).append("\t");
        buf.append(this.virtualPrice).append("\t");
        buf.append(this.Yield).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        if (btOper != YssCons.OP_ADD) {
            dbFun.checkInputCommon(btOper,
                                   pub.yssGetTableName("tb_order_maintenance"),
                                   "FOrderNum,FPortCode,FInvMgrCode,FSecurityCode,FBrokerCode",
                                   this.orderNum + "," + this.portCode + "," +
                                   (this.invMgrCode.length() == 0 ?
                                    pub.getUserCode() : this.invMgrCode) + "," +
                                   this.securityCode + "," + this.brokerCode,
                                   this.oldOrderNum + "," + this.oldPortCode + "," +
                                   (this.oldInvMgrCode.length() == 0 ?
                                    pub.getUserCode() : this.oldInvMgrCode) + "," +
                                   this.oldSecurityCode + "," + this.oldBrokerCode);
            //checkState(btOper);
        }
    }

    //检查数据状态是否合法
    public void checkState(byte btOper) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName " +
                " from " + pub.yssGetTableName("tb_order_maintenance") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FInvMgrCode = " +
                dbl.sqlString(this.invMgrCode) + " and a.FOrderNum = " +
                dbl.sqlString(this.orderNum) + " and a.FSecurityCode = " +
                dbl.sqlString(this.securityCode) + " and a.FBrokerCode = " +
                dbl.sqlString(this.brokerCode) + " and a.FPortCode = " +
                dbl.sqlString( (this.portCode.trim().length() == 0 ? " " :
                                this.portCode.trim()));

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                String tmpTradeNum = rs.getString("FTradeNum");
                int iCheckId = rs.getInt("FCheckState");
                if (btOper == YssCons.OP_EDIT) {
                    if (tmpTradeNum != null && tmpTradeNum.length() != 0) {
                        throw new YssException("订单已确认，不能进行修改操作！");
                    }
                } else if (btOper == YssCons.OP_DEL) {
                    if (tmpTradeNum != null && tmpTradeNum.length() != 0) {
                        throw new YssException("订单已确认，不能进行删除操作！");
                    }
                } else if (btOper == YssCons.OP_AUDIT) {
                    if (iCheckId == 1 && tmpTradeNum != null && tmpTradeNum.length() != 0) {
                        throw new YssException("订单已确认，不能进行反审核操作！");
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("获取订单信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("tb_order_maintenance") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FInvMgrCode = " +
                dbl.sqlString(this.invMgrCode) + " and FOrderNum = " +
                dbl.sqlString(this.orderNum) + " and FSecurityCode = " +
                dbl.sqlString(this.securityCode) + " and FBrokerCode = " +
                dbl.sqlString(this.brokerCode) + " and FPortCode = " +
                dbl.sqlString( (this.portCode.trim().length() == 0 ? " " :
                                this.portCode.trim()));
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核订单制作信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("tb_order_maintenance") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FInvMgrCode = " +
                dbl.sqlString(this.invMgrCode) + " and FOrderNum = " +
                dbl.sqlString(this.orderNum) + " and FSecurityCode = " +
                dbl.sqlString(this.securityCode) + " and FBrokerCode = " +
                dbl.sqlString(this.brokerCode) + " and FPortCode = " +
                dbl.sqlString( (this.portCode.trim().length() == 0 ? " " :
                                this.portCode.trim()));

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除订单制作信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
            strSql = "update " + pub.yssGetTableName("tb_order_maintenance") +
                " set FInvMgrCode = " + dbl.sqlString(pub.getUserCode()) +
                ",FSecurityCode = " + dbl.sqlString(this.securityCode) +
                ",FOrderNum = " + dbl.sqlString(this.orderNum) +
                ",FBrokerCode = " + dbl.sqlString(this.brokerCode) +
                ",FPortCode = " +
                dbl.sqlString( (this.portCode.length() == 0 ? " " :
                                this.portCode)) +
                ",FTradeTypeCode = " + dbl.sqlString(this.tradeCode) +
                ",FTradeAmount = " + this.tradeAmount +
                ",FInterest = " + this.interest +
                ",FTradePrice = " + this.tradePrice +
                ",FVirtualPrice = " + this.virtualPrice +
                ",FOrderDate = " + dbl.sqlDate(this.orderDate) +
                ",FOrderTime = " + dbl.sqlString(this.orderTime) +
                ",FYield =" + this.Yield +
                ",FQuoteMode = " + dbl.sqlString(this.quoteMode) +
                ",FDesc = " + dbl.sqlString(this.desc) +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                " where FInvMgrCode = " +
                dbl.sqlString(this.oldInvMgrCode) + " and FOrderNum = " +
                dbl.sqlString(this.oldOrderNum) + " and FSecurityCode = " +
                dbl.sqlString(this.oldSecurityCode) + " and FBrokerCode = " +
                dbl.sqlString(this.oldBrokerCode) + " and FPortCode = " +
                dbl.sqlString( (this.oldPortCode.trim().length() == 0 ? " " :
                                this.oldPortCode.trim()));

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改订单制作信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = " where 1=1 ";
        //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return sResult;
    	//=============end=================
        if (this.filterType != null) {
        	 if (this.filterType.isOnlyColumns.equals("1")&&pub.isBrown()==false) {	//20111027 modified by liubo.STORY #1285. 
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.orderNum.length() != 0) {
                sResult = sResult + " and a.FOrderNum like '" +
                    filterType.orderNum.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.invMgrCode.length() != 0) {
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.invMgrCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.tradeCode.length() != 0) {
                sResult = sResult + " and a.FTradeTypeCode = '" +
                    filterType.tradeCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.tradeAmount != -1) {
                sResult = sResult + " and a.FTradeAmount = " +
                    filterType.tradeAmount;
            }
            if (this.filterType.interest != -1) {
                sResult = sResult + " and a.FInterest = " +
                    filterType.interest;
            }
            if (this.filterType.quoteMode.length() != 0 &&
                !this.filterType.quoteMode.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FQuoteMode = " +
                    dbl.sqlString(filterType.quoteMode);
            }
            if (this.filterType.tradePrice != -1) {
                sResult = sResult + " and a.FTradePrice = " +
                    filterType.tradePrice;
            }
            if (this.filterType.virtualPrice != -1) {
                sResult = sResult + " and a.FVirtualPrice = " +
                    filterType.virtualPrice;
            }
            if (this.filterType.securityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.securityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.brokerCode.length() != 0) {
                sResult = sResult + " and a.FBrokerCode like '" +
                    filterType.brokerCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.orderDate != null &&
                !this.filterType.orderDate.equals(YssFun.toDate("9998-12-31"))) {
                sResult = sResult + " and a.FOrderDate = " +
                    dbl.sqlDate(filterType.orderDate);
            }
            if (!this.filterType.orderTime.equals("00:00:00")) {
                sResult = sResult + " and a.FOrderTime = " +
                    dbl.sqlString(filterType.orderTime);
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%' ";
            }
            if (this.filterType.Yield != 0) {
                sResult += " and FYield =" + filterType.Yield;
            }

        }
        return sResult;

    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    //配置ListView显示列时对特殊列的操作
    public void beforeBuildRowShowStr(YssCancel bCancel, String sColName,
                                      ResultSet rs, StringBuffer buf) throws
        SQLException {
        String sFieldName = "";
        String sFieldFormat = "";
        if (sColName.indexOf("FTradeTotal") >= 0) {
            if (sColName.indexOf(";") > 0) {
                sFieldName = sColName.split(";")[0];
                sFieldFormat = sColName.split(";")[1];
            } else {
                sFieldName = sColName;
            }
            if (rs.getDouble("FFactor") == 0) {
                buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.mul(rs.
                    getDouble("FTradeAmount"), rs.getDouble("FTradePrice")),
                    rs.getDouble("FInterest")), 2), sFieldFormat) +
                           "").append("\t");
            } else {
                buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.div(YssD.mul(rs.
                    getDouble("FTradeAmount"), rs.getDouble("FTradePrice")), rs.getDouble("FFactor")),
                    rs.getDouble("FInterest")), 2), sFieldFormat) +
                           "").append("\t");
            }
            bCancel.setCancel(true);
        }
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*,m.FVocName as FQuoteModeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,f.FInvMgrName, " +
                " g.FTradeTypeName,h.FBrokerName as FBrokerName,e.FSecurityName,e.FFactor,e.FTradeCury,d.FPortName as FPortName" +
                " from " + pub.yssGetTableName("tb_order_maintenance") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount,FFactor,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
                " left join (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState = 1 ) h on a.FBrokerCode = h.FBrokerCode " +
             
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
             
                " left join (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1) d on a.FPortCode = d.FPortCode " +
               
                
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
         
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +
                
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary m on a.FQuoteMode = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MTN_QUOTEMODE) +
                //----------------------------------------------------------------------------------------------------
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.invMgrCode = rs.getString("FInvMgrCode") + "";
                this.invMgrName = rs.getString("FInvMgrName") + "";
                this.orderNum = rs.getString("FOrderNum") + "";
                this.orderDate = rs.getDate("FOrderDate");
                this.orderTime = rs.getString("FOrderTime");
                this.tradeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeName = rs.getString("FTradeTypeName") + "";
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.securityName = rs.getString("FSecurityName") + "";
                this.brokerCode = rs.getString("FBrokerCode") + "";
                this.brokerName = rs.getString("FBrokerName") + "";
                this.portCode = rs.getString("FPortCode") + "";
                this.portName = rs.getString("FPortName") + "";
                this.tradeAmount = rs.getDouble("FTradeAmount");
                this.tradePrice = rs.getDouble("FTradePrice");
                this.virtualPrice = rs.getDouble("FVirtualPrice");
                this.Yield = rs.getDouble("FYield");
                this.interest = rs.getDouble("FInterest");
                this.tradeNum = rs.getString("FTradeNum") + "";
                this.desc = rs.getString("FDesc") + "";
                this.quoteMode = rs.getString("FQuoteMode") + "";
                this.tradeCuryCode = rs.getString("FTradeCury") + "";
//            this.tradeTotal = YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble(
//                  "FTradeAmount"), rs.getDouble("FTradePrice")),rs.getDouble("FInterest")), 2);

                if (rs.getDouble("FFactor") != 0) {
                    this.tradeTotal = YssD.sub(YssD.div(YssD.mul(rs.getDouble(
                        "FTradeAmount"), rs.getDouble("FTradePrice")),
                        rs.getDouble("FFactor")),
                                               rs.getDouble("FInterest"));
                } else {
                    this.tradeTotal = YssD.sub(YssD.mul(rs.getDouble("FTradeAmount"), rs.getDouble("FTradePrice")),
                                               rs.getDouble("FInterest"));
                }
                this.tradeTotal = YssD.round(this.tradeTotal, 2);
                super.setRecLog(rs);
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_MTN_QUOTEMODE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取订单信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "投资经理名称\t订单代码\t交易方式\t订单日期";
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,f.FInvMgrName, " +
                " g.FTradeTypeName,h.FBrokerName as FBrokerName,e.FSecurityName,d.FPortName as FPortName,d.FStartDate as FStartDate" +
                " from " + pub.yssGetTableName("tb_order_maintenance") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                " left join (elect FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") + " where  FCheckState = 1 ) h on a.FBrokerCode = h.FBrokerCode " +
                
                //end by lidaolong 
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
      
                " left join (select FPortCode, FPortName,FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) d on a.FPortCode = d.FPortCode " +
              
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
           
                
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where  FCheckState = 1 ) f on a.FInvMgrCode = f.FInvMgrCode " +
              
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
                ( (buildFilterSql().length() == 0) ? " where " :
                 buildFilterSql() + " and ") +
                " a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FInvMgrCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FOrderNum") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FTradeTypeName") + "").trim()).
                    append(
                        "\t");
//            bufShow.append( (rs.getString("FOrderDate") + "").trim()).append(
//                  YssCons.YSS_LINESPLITMARK);
                bufShow.append(YssFun.formatDate( (rs.getString("FOrderDate")))).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.invMgrCode = rs.getString("FInvMgrCode") + "";
                this.invMgrName = rs.getString("FInvMgrName") + "";
                this.orderNum = rs.getString("FOrderNum") + "";
                this.orderDate = rs.getDate("FOrderDate");
                this.orderTime = rs.getString("FOrderTime");
                this.tradeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeName = rs.getString("FTradeTypeName") + "";
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.securityName = rs.getString("FSecurityName") + "";
                this.brokerCode = rs.getString("FBrokerCode") + "";
                this.brokerName = rs.getString("FBrokerName") + "";
                this.portCode = rs.getString("FPortCode") + "";
                this.portName = rs.getString("FPortName") + "";
                this.tradeAmount = rs.getDouble("FTradeAmount");
                this.tradePrice = rs.getDouble("FTradePrice");
                this.virtualPrice = rs.getDouble("FVirtualPrice");
                this.interest = rs.getDouble("FInterest");
                this.tradeNum = rs.getString("FTradeNum") + "";
                this.desc = rs.getString("FDesc") + "";
                this.quoteMode = rs.getString("FQuoteMode") + "";
                this.tradeTotal = YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble(
                    "FTradeAmount"), rs.getDouble("FTradePrice")),
                    rs.getDouble("FInterest")), 2);
                super.setRecLog(rs);
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
            throw new YssException("获取订单信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
    public String getListViewData4() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName " +
                " from " + pub.yssGetTableName("tb_order_maintenance") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FInvMgrCode = " +
                dbl.sqlString(this.invMgrCode) + " and a.FOrderNum = " +
                dbl.sqlString(this.orderNum) + " and a.FSecurityCode = " +
                dbl.sqlString(this.securityCode) + " and a.FBrokerCode = " +
                dbl.sqlString(this.brokerCode) + " and a.FPortCode = " +
                dbl.sqlString( (this.portCode.trim().length() == 0 ? " " :
                                this.portCode.trim()));

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.invMgrCode = rs.getString("FInvMgrCode") + "";
                this.orderNum = rs.getString("FOrderNum") + "";
                this.orderDate = rs.getDate("FOrderDate");
                this.orderTime = rs.getString("FOrderTime");
                this.tradeCode = rs.getString("FTradeTypeCode") + "";
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.brokerCode = rs.getString("FBrokerCode") + "";
                this.portCode = rs.getString("FPortCode") + "";
                this.tradeAmount = rs.getDouble("FTradeAmount");
                this.tradePrice = rs.getDouble("FTradePrice");
                this.virtualPrice = rs.getDouble("FVirtualPrice");
                this.interest = rs.getDouble("FInterest");
                this.tradeNum = rs.getString("FTradeNum") + "";
                this.desc = rs.getString("FDesc") + "";
                this.quoteMode = rs.getString("FQuoteMode") + "";

                super.setRecLog(rs);
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取订单信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.orderNum = reqAry[0];
            if (YssFun.isDate(reqAry[1])) {
                this.orderDate = YssFun.toDate(reqAry[1]);
            }
            this.orderTime = reqAry[2];
            this.invMgrCode = reqAry[3];
            this.invMgrName = reqAry[4];
            this.securityCode = reqAry[5];
            this.securityName = reqAry[6];
            this.brokerCode = reqAry[7];
            this.brokerName = reqAry[8];
            this.portCode = reqAry[9];
            this.portName = reqAry[10];
            this.tradeCode = reqAry[11];
            this.tradeName = reqAry[12];
            if (YssFun.isNumeric(reqAry[13])) {
                this.tradeAmount = Double.parseDouble(reqAry[13]);
            }
            if (YssFun.isNumeric(reqAry[14])) {
                this.interest = Double.parseDouble(reqAry[14]);
            }
            this.quoteMode = reqAry[15];
            if (YssFun.isNumeric(reqAry[16])) {
                this.tradePrice = Double.parseDouble(reqAry[16]);
            }
            if (YssFun.isNumeric(reqAry[17])) {
                this.tradeTotal = Double.parseDouble(reqAry[17]);
            }
            this.tradeNum = reqAry[18];
            this.desc = reqAry[19];
            this.checkStateId = Integer.parseInt(reqAry[20]);
            this.oldOrderNum = reqAry[21];
            this.oldInvMgrCode = reqAry[22];
            this.oldSecurityCode = reqAry[23];
            this.oldBrokerCode = reqAry[24];
            this.oldPortCode = reqAry[25];
            this.isOnlyColumns = reqAry[26];
            this.tradeCuryCode = reqAry[27];
            if (YssFun.isNumeric(reqAry[28])) {
                this.virtualPrice = Double.parseDouble(reqAry[28]);
            }
            if (YssFun.isNumeric(reqAry[29])) {
                this.Yield = Double.parseDouble(reqAry[29]);
            } else {
                this.Yield = 0;
            }
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new MaintenanceBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析订单设置请求出错", e);
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
     * getSettingListView
     *
     * @return String
     */
//   public String getSettingListView() throws YssException {
//      String sResult = "";
//      sResult = getSecurityListView();
//      return sResult;
//   }

    protected String getSecurityListView() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";

        try {
            sHeader = "属性名\t值";
            strSql =
                "select a.*, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
                " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
                " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
                " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
                " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
                " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
                " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Security") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
                " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) f on a.FExchangeCode = f.FExchangeCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode" +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode" +
                " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) g on a.FTradeCury = g.FCuryCode" +
                " left join (select o.FSectorCode as FSectorCode,o.FSectorName as FSectorName from " +
                pub.yssGetTableName("Tb_Para_Sector") + " o join " +
                "(select FSectorCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Sector") + " " +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSectorCode) p " +
                " on o.FSectorCode = p.FSectorCode and o.FStartDate = p.FStartDate) h on a.FSectorCode = h.FSectorCode" +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " ) j on  a.FCusCatCode = j.FCusCatCode " +
                " left join (select FAffCorpCode,FAffCorpName from " +
                pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                " ) k on a.FIssueCorpCode = k.FAffCorpCode " +
                " where a.FSecurityCode = " + dbl.sqlString(this.securityCode) +
                " and a.FStartDate <= (select Max(FStartDate) from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FSecurityCode = " + dbl.sqlString(this.securityCode) +
                " and FStartDate <=" +
                dbl.sqlDate(new java.util.Date()) + ")";

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                bufShow.append("证券代码：").append("\t").append(rs.getString(
                    "FSecurityCode")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("证券名称：").append("\t").append(rs.getString(
                    "FSecurityName")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("品种类型：").append("\t").append(rs.getString(
                    "FCatName")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("品种子类型：").append("\t").append(rs.getString(
                    "FSubCatName")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("自定义品种类型：").append("\t").append(rs.getString(
                    "FCusCatName")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("交易所：").append("\t").append(rs.getString(
                    "FExchangeName")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("上市代码：").append("\t").append(rs.getString(
                    "FMarketCode")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("报价因子：").append("\t").append(rs.getString(
                    "FFactor")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("行业板块：").append("\t").append(rs.getString(
                    "FSectorName")).append(YssCons.YSS_LINESPLITMARK);
                bufShow.append("证券发行人：").append("\t").append(rs.getString(
                    "FIssueCorpName")).append(YssCons.YSS_LINESPLITMARK);

                bufAll.append("SecurityCode").append(YssCons.YSS_ITEMSPLITMARK2).
                    append("证券代码：").append(
                        YssCons.YSS_ITEMSPLITMARK2).append(rs.getString(
                            "FSecurityCode")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("SecurityName").append(YssCons.YSS_ITEMSPLITMARK2).
                    append("证券名称：").append(
                        YssCons.YSS_ITEMSPLITMARK2).append(rs.getString(
                            "FSecurityName")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("CatName").append(YssCons.YSS_ITEMSPLITMARK2).append(
                    "品种类型：").append(YssCons.YSS_ITEMSPLITMARK2).
                    append(rs.getString(
                        "FCatName")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("SubCatName").append(YssCons.YSS_ITEMSPLITMARK2).
                    append("品种子类型：").append(
                        YssCons.YSS_ITEMSPLITMARK2).append(rs.getString(
                            "FSubCatName")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("CusCatName").append(YssCons.YSS_ITEMSPLITMARK2).
                    append("自定义品种类型：").append(
                        YssCons.YSS_ITEMSPLITMARK2).append(rs.getString(
                            "FCusCatName")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("ExchangeName").append(YssCons.YSS_ITEMSPLITMARK2).
                    append("交易所：").append(
                        YssCons.YSS_ITEMSPLITMARK2).append(rs.getString(
                            "FExchangeName")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("MarketCode").append(YssCons.YSS_ITEMSPLITMARK2).
                    append("上市代码：").append(
                        YssCons.YSS_ITEMSPLITMARK2).
                    append(rs.getString(
                        "FMarketCode")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("Factor").append(YssCons.YSS_ITEMSPLITMARK2).append(
                    "报价因子：").append(YssCons.YSS_ITEMSPLITMARK2).
                    append(rs.getString(
                        "FFactor")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("SectorName").append(YssCons.YSS_ITEMSPLITMARK2).
                    append("行业板块：").append(
                        YssCons.YSS_ITEMSPLITMARK2).
                    append(rs.getString(
                        "FSectorName")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append("IssueCorpName").append(YssCons.YSS_ITEMSPLITMARK2).
                    append("证券发行人：").append(
                        YssCons.YSS_ITEMSPLITMARK2).append(rs.getString(
                            "FIssueCorpName")).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取证券信息出错");
        }
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        if (sType.equalsIgnoreCase("interest")) {
            try {
                this.interest = this.getSettingOper().getInterest(this.securityCode,
                    this.tradeAmount, this.orderDate);
            } catch (Exception e) {
                throw new YssException("获得利息出错");
            }
        }
        if (sType.equalsIgnoreCase("BaseCuryRate")) {
            this.baseCuryRate = this.getSettingOper().getCuryRate(this.orderDate,
                (this.orderTime == null) ? "" : this.orderTime.trim(),
                this.tradeCuryCode, this.portCode, YssOperCons.YSS_RATE_BASE);
            this.baseCuryRate = YssD.round(this.baseCuryRate, 4);
        }
        if (sType.equalsIgnoreCase("SecInfo")) {
            return getSecurityListView();
        }
        if (sType.equalsIgnoreCase("DeleteItem")) {
            Connection conn = dbl.loadConnection();
            boolean bTrans = false;
            String strSql = "";
            try {
                strSql = "delete from " + pub.yssGetTableName("tb_order_maintenance") +
                    " where FInvMgrCode = " +
                    dbl.sqlString(this.invMgrCode) + " and FOrderNum = " +
                    dbl.sqlString(this.orderNum) + " and FSecurityCode = " +
                    dbl.sqlString(this.securityCode) + " and FBrokerCode = " +
                    dbl.sqlString(this.brokerCode) + " and FPortCode = " +
                    dbl.sqlString( (this.portCode.trim().length() == 0 ? " " :
                                    this.portCode.trim()));

                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            } catch (Exception e) {
                throw new YssException("删除订单制作信息出错", e);
            } finally {
                dbl.endTransFinal(conn, bTrans);
            }
        }
        if (sType.equalsIgnoreCase("RefreshData")) {
            this.getSetting();
        }
        return buildRowStr();
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        MaintenanceBean befEditBean = new MaintenanceBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,m.FVocName as FQuoteModeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,f.FInvMgrName, " +
                " g.FTradeTypeName,h.FBrokerName as FBrokerName,e.FSecurityName,e.FFactor,e.FTradeCury,d.FPortName as FPortName,d.FStartDate as FStartDate" +
                " from " + pub.yssGetTableName("tb_order_maintenance") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount,FFactor,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
                " left join (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") + " where  FCheckState = 1 ) h on a.FBrokerCode = h.FBrokerCode " +
               
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
                " left join (select FPortCode, FPortName,FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1 ) d on a.FPortCode = d.FPortCode " +
              
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
                
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where  FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +
              
                
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary m on a.FQuoteMode = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MTN_QUOTEMODE) +
                //----------------------------------------------------------------------------------------------------
                " where  a.FOrderNum =" + dbl.sqlString(this.oldOrderNum) + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.invMgrCode = rs.getString("FInvMgrCode") + "";
                befEditBean.invMgrName = rs.getString("FInvMgrName") + "";
                befEditBean.orderNum = rs.getString("FOrderNum") + "";
                befEditBean.orderDate = rs.getDate("FOrderDate");
                befEditBean.orderTime = rs.getString("FOrderTime");
                befEditBean.tradeCode = rs.getString("FTradeTypeCode") + "";
                befEditBean.tradeName = rs.getString("FTradeTypeName") + "";
                befEditBean.securityCode = rs.getString("FSecurityCode") + "";
                befEditBean.securityName = rs.getString("FSecurityName") + "";
                befEditBean.brokerCode = rs.getString("FBrokerCode") + "";
                befEditBean.brokerName = rs.getString("FBrokerName") + "";
                befEditBean.portCode = rs.getString("FPortCode") + "";
                befEditBean.portName = rs.getString("FPortName") + "";
                befEditBean.tradeAmount = rs.getDouble("FTradeAmount");
                befEditBean.tradePrice = rs.getDouble("FTradePrice");
                befEditBean.virtualPrice = rs.getDouble("FVirtualPrice");
                befEditBean.interest = rs.getDouble("FInterest");
                befEditBean.tradeNum = rs.getString("FTradeNum") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.quoteMode = rs.getString("FQuoteMode") + "";
                befEditBean.tradeCuryCode = rs.getString("FTradeCury") + "";
                if (rs.getDouble("FFactor") != 0) {
                    befEditBean.tradeTotal = YssD.sub(YssD.div(YssD.mul(rs.getDouble(
                        "FTradeAmount"), rs.getDouble("FTradePrice")),
                        rs.getDouble("FFactor")),
                        rs.getDouble("FInterest"));
                } else {
                    befEditBean.tradeTotal = YssD.sub(YssD.mul(rs.getDouble("FTradeAmount"), rs.getDouble("FTradePrice")),
                        rs.getDouble("FInterest"));
                }
                befEditBean.tradeTotal = YssD.round(befEditBean.tradeTotal, 2);

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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
