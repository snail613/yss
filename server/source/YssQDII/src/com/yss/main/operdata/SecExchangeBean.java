package com.yss.main.operdata;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

public class SecExchangeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String num = "";
    private java.util.Date exchangeDate;
    private String securityCode = "";
    private String securityName = "";
    private String tsfTypeCode = ""; //业务类型
    private String tsfTypeName = "";

    private String tradeTypeCode = "";
    private String tradeTypeName = "";
    private String subTsfTypeCode = "";
    private String subTsfTypeName = "";
    private String portCode = "";
    private String portName = "";
    private String analysisCode1 = "";
    private String analysisCode2 = "";
    private String analysisCode3 = "";

    private String analysisName1 = "";
    private String analysisName2 = "";
    private String analysisName3 = "";

    private double exchangeAmount; //兑换数量
    private double exchangeCost; //核算成本
    private double mExchangeCost; //管理成本
    private double vExchangeCost; //估值成本
    private double portExchangeCost; //组合货币核算成本
    private double mPortExchangeCost; //组合货币管理成本
    private double vPortExchangeCost; //组合货币估值成本
    private double baseExchangeCost;
    private double mBaseExchangeCost;
    private double vBaseExchangeCost;
    private double baseCuryRate;
    private double portCuryRate;
    private String dataSource = "";
    private String desc = "";

    private String attrClsCode = ""; //所属分类 sj add 20071202
    private String attrClsName = "";
    private String catTypeCode = ""; //品种类型
    private String catTypeName = "";

    private String oldNum = "";
    private String strIsOnlyColumns = "0";
    private java.util.Date beginDate;
    private java.util.Date endDate;

    private String strExchangeOut = "";
    private SecExchangeBean filterType;
    private String sRecycled = "";

    public double getMBaseExchangeCost() {
        return mBaseExchangeCost;
    }

    public double getVPortExchangeCost() {
        return vPortExchangeCost;
    }

    public String getPortCode() {
        return portCode;
    }

    public double getExchangeAmount() {
        return exchangeAmount;
    }

    public double getMPortExchangeCost() {
        return mPortExchangeCost;
    }

    public double getVBaseExchangeCost() {
        return vBaseExchangeCost;
    }

    public double getExchangeCost() {
        return exchangeCost;
    }

    public String getAnalysisCode3() {
        return analysisCode3;
    }

    public String getOldNum() {
        return oldNum;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getDataSource() {
        return dataSource;
    }

    public double getMExchangeCost() {
        return mExchangeCost;
    }

    public String getNum() {
        return num;
    }

    public double getBaseExchangeCost() {
        return baseExchangeCost;
    }

    public String getAnalysisCode2() {
        return analysisCode2;
    }

    public String getAnalysisCode1() {
        return analysisCode1;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    public String getAnalysisName2() {
        return analysisName2;
    }

    public String getDesc() {
        return desc;
    }

    public java.util.Date getExchangeDate() {
        return exchangeDate;
    }

    public String getSubTsfTypeCode() {
        return subTsfTypeCode;
    }

    public String getAnalysisName3() {
        return analysisName3;
    }

    public SecExchangeBean getFilterType() {
        return filterType;
    }

    public double getPortExchangeCost() {
        return portExchangeCost;
    }

    public String getAnalysisName1() {
        return analysisName1;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public void setVExchangeCost(double vExchangeCost) {
        this.vExchangeCost = vExchangeCost;
    }

    public void setMBaseExchangeCost(double mBaseExchangeCost) {
        this.mBaseExchangeCost = mBaseExchangeCost;
    }

    public void setVPortExchangeCost(double vPortExchangeCost) {
        this.vPortExchangeCost = vPortExchangeCost;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setExchangeAmount(double exchangeAmount) {
        this.exchangeAmount = exchangeAmount;
    }

    public void setMPortExchangeCost(double mPortExchangeCost) {
        this.mPortExchangeCost = mPortExchangeCost;
    }

    public void setVBaseExchangeCost(double vBaseExchangeCost) {
        this.vBaseExchangeCost = vBaseExchangeCost;
    }

    public void setExchangeCost(double exchangeCost) {
        this.exchangeCost = exchangeCost;
    }

    public void setAnalysisCode3(String analysisCode3) {
        this.analysisCode3 = analysisCode3;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setMExchangeCost(double mExchangeCost) {
        this.mExchangeCost = mExchangeCost;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setBaseExchangeCost(double baseExchangeCost) {
        this.baseExchangeCost = baseExchangeCost;
    }

    public void setAnalysisCode2(String analysisCode2) {
        this.analysisCode2 = analysisCode2;
    }

    public void setAnalysisCode1(String analysisCode1) {
        this.analysisCode1 = analysisCode1;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    public void setAnalysisName2(String analysisName2) {
        this.analysisName2 = analysisName2;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setExchangeDate(java.util.Date exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public void setSubTsfTypeCode(String subTsfTypeCode) {
        this.subTsfTypeCode = subTsfTypeCode;
    }

    public void setAnalysisName3(String analysisName3) {
        this.analysisName3 = analysisName3;
    }

    public void setFilterType(SecExchangeBean filterType) {
        this.filterType = filterType;
    }

    public void setPortExchangeCost(double portExchangeCost) {
        this.portExchangeCost = portExchangeCost;
    }

    public void setAnalysisName1(String analysisName1) {
        this.analysisName1 = analysisName1;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setTsfTypeName(String tsfTypeName) {
        this.tsfTypeName = tsfTypeName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setSubTsfTypeName(String subTsfTypeName) {
        this.subTsfTypeName = subTsfTypeName;
    }

    public void setStrExchangeOut(String strExchangeOut) {
        this.strExchangeOut = strExchangeOut;
    }

    public void setStrIsOnlyColumns(String strIsOnlyColumns) {
        this.strIsOnlyColumns = strIsOnlyColumns;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setTradeTypeName(String tradeTypeName) {
        this.tradeTypeName = tradeTypeName;
    }

    public void setAttrClsName(String attrClsName) {
        this.attrClsName = attrClsName;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setCatTypeCode(String catTypeCode) {
        this.catTypeCode = catTypeCode;
    }

    public void setCatTypeName(String catTypeName) {
        this.catTypeName = catTypeName;
    }

    public double getVExchangeCost() {
        return vExchangeCost;
    }

    public String getTsfTypeName() {
        return tsfTypeName;
    }

    public String getPortName() {
        return portName;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getSubTsfTypeName() {
        return subTsfTypeName;
    }

    public String getStrExchangeOut() {
        return strExchangeOut;
    }

    public String getStrIsOnlyColumns() {
        return strIsOnlyColumns;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public String getTradeTypeName() {
        return tradeTypeName;
    }

    public String getAttrClsName() {
        return attrClsName;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public String getCatTypeCode() {
        return catTypeCode;
    }

    public String getCatTypeName() {
        return catTypeName;
    }

    public SecExchangeBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShowDataStr = new StringBuffer();
        StringBuffer bufAllDataStr = new StringBuffer();
        String sAry[] = null;
        try {
            sHeader = this.getListView1Headers();
            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Security); //获得分析代码
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.strIsOnlyColumns.equals("1")) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql =
                "select a.*, b.fusername as fcreatorname, c.fusername as fcheckusername," +
                " d.FPortName as FPortName,e.FTradeTypeName as FTradeTypeName,g.FSecurityName as FSecurityName, j.FAttrClsName as FAttrClsName " +
                sAry[0];
            strSql = strSql + " from " +
                pub.yssGetTableName("Tb_Data_SecExchangeIn") + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                //-----------------------------------------------------------------------------------------------
                " left join (select FPortCode,FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState <> 2) d  on a.FPortCode = d.FPortCode" +
                //-------------------------------------------------------------------------------------------------
                //" left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) e on a.FTsfTypeCode = e.FTsfTypeCode" +
                //" left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) f on a.FSubTsfTypeCode = f.FSubTsfTypeCode" +
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType) e on a.FTradeTypeCode = e.FTradeTypeCode" +
                " left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState <> 2) g on a.FSecurityCode = g.FSecurityCode  " +
                " left join (select FAttrClsCode,FAttrClsName from " + //sj add 20071204
                pub.yssGetTableName("Tb_Para_AttributeClass") + ") j on a.FAttrClsCode = j.FAttrClsCode " +
                sAry[1] +
                this.buildFilterSql() +
                " order by a.FExchangeDate desc ";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("SecExchangeIn");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShowDataStr.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs);
                bufAllDataStr.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }

            if (bufShowDataStr.toString().length() > 2) {
                sShowDataStr = bufShowDataStr.toString().substring(0,
                    bufShowDataStr.toString().length() - 2);
            }

            if (bufAllDataStr.toString().length() > 2) {
                sAllDataStr = bufAllDataStr.toString().substring(0,
                    bufAllDataStr.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取证券兑换-流入信息出错" + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        return addSetting(false);
    }

    public String addSetting(boolean bAutoCommit) throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {

            if (this.num.equals("")) {
                this.num = "E" +
                    YssFun.formatDate(this.exchangeDate, "yyyyMMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_SecExchangeIn"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000001");
            }
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_SecExchangeIn") +
                " (FNum,FExchangeDate,FSecurityCode,FTradeTypeCode,FPortCode," +
                " FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FINAMOUNT,FExchangeCost,FMExchangeCost,FVExchangeCost,FPortExchangeCost,FMPortExchangeCost," +
                " FVPortExchangeCost,FBaseExchangeCost,FMBaseExchangeCost,FVBaseExchangeCost,FBaseCuryRate,FPortCuryRate,FDataSource,FDesc,FAttrClsCode,FCatType" +
                ",FCheckState,FCreator,FCreateTime) values(" +
                dbl.sqlString(this.num) + ", " +
                dbl.sqlDate(this.exchangeDate) + ", " +
                dbl.sqlString(this.securityCode) + ", " +
                dbl.sqlString(this.tradeTypeCode) + ", " +
                dbl.sqlString(this.portCode) + ", " +
                dbl.sqlString( (this.analysisCode1 == null ||
                                this.analysisCode1.equals("")) ? " " :
                              this.analysisCode1) + ", " +
                dbl.sqlString( (this.analysisCode2 == null || this.analysisCode2.equals("")) ?
                              " " : this.analysisCode2) + ", " +
                dbl.sqlString( (this.analysisCode3 == null ||
                                this.analysisCode3.equals("")) ? " " :
                              this.analysisCode3) + ", " +
                this.exchangeAmount + ", " +
                this.exchangeCost + ", " +
                this.mExchangeCost + ", " +
                this.vExchangeCost + ", " +
                this.portExchangeCost + ", " +
                this.mPortExchangeCost + ", " +
                this.vPortExchangeCost + ", " +
                this.baseExchangeCost + ", " +
                this.mBaseExchangeCost + ", " +
                this.vBaseExchangeCost + ", " +
                this.baseCuryRate + ", " +
                this.portCuryRate + "," +
                this.dataSource + ", " +
                dbl.sqlString(this.desc) + ", " +
                (this.attrClsCode == null || this.attrClsCode.length() == 0 ? dbl.sqlString(" ") : dbl.sqlString(this.attrClsCode)) + "," +
                (this.catTypeCode == null || this.catTypeCode.length() == 0 ? dbl.sqlString(" ") : dbl.sqlString(this.catTypeCode)) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode + " ") + ", " +
                dbl.sqlString(this.creatorTime + " ") + ")";
            if (!bAutoCommit) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            dbl.executeSql(strSql);
            if (this.strExchangeOut != null && this.strExchangeOut.length() != 0) {
                SecExchangeOutBean exchangeout = new SecExchangeOutBean();
                exchangeout.setYssPub(pub);
                exchangeout.saveMutliSetting(this.strExchangeOut, true, this.num);
            }
            if (!bAutoCommit) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return null;
        } catch (Exception e) {
//       dbl.closeConnection();//QDV4南方2009年1月6日02_B MS00165 by leeyu 去掉这句关闭连接的语句
            throw new YssException("新增证券兑换-流入信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String[] arrData = null;
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            if (!sRecycled.equalsIgnoreCase("") || sRecycled != null) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Data_SecExchangeIn") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum=" + dbl.sqlString(this.num);
                    conn.setAutoCommit(false);
                    bTrans = true;
                    dbl.executeSql(strSql);
                    //证券兑换-流出
                    strSql = "update " + pub.yssGetTableName("Tb_Data_SecExchangeOut") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FINSECNUM=" + dbl.sqlString(this.num);
                    dbl.executeSql(strSql);
                    conn.commit();

                }
            }

            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核证券兑换-流入信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_SecExchangeIn") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ",FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum=" + dbl.sqlString(this.num);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //证券兑换-流出
            strSql = "update " + pub.yssGetTableName("Tb_Data_SecExchangeOut") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ",FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FINSECNUM=" + dbl.sqlString(this.num);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除证券兑换-流入信息出错\r\n" + e.getMessage(), e);
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
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //先删后增
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_SecExchangeIn") +
                " where FNum=" +
                dbl.sqlString(this.num);
            dbl.executeSql(strSql);
            this.addSetting();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改证券兑换-流入信息出错" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.num).append("\t");
        if (this.exchangeDate != null) {
            buf.append(YssFun.formatDate(this.exchangeDate)).append("\t");
        }
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.tradeTypeCode).append("\t");
        buf.append(this.tradeTypeName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.analysisCode1).append("\t");
        buf.append(this.analysisName1).append("\t");
        buf.append(this.analysisCode2).append("\t");
        buf.append(this.analysisName2).append("\t");
        buf.append(this.analysisCode3).append("\t");
        buf.append(this.analysisName3).append("\t");
        buf.append(this.exchangeAmount).append("\t");
        buf.append(this.exchangeCost).append("\t");
        buf.append(this.mExchangeCost).append("\t");
        buf.append(this.vExchangeCost).append("\t");
        buf.append(this.baseExchangeCost).append("\t");
        buf.append(this.mBaseExchangeCost).append("\t");
        buf.append(this.vBaseExchangeCost).append("\t");
        buf.append(this.portExchangeCost).append("\t");
        buf.append(this.mPortExchangeCost).append("\t");
        buf.append(this.vPortExchangeCost).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.dataSource).append("\t");
        buf.append(this.desc).append("\t");
        //buf.append(this.attrClsCode).append("\t");
        //buf.append(this.attrClsName).append("\t");
        //buf.append(this.catTypeCode).append("\t");
        //buf.append(this.catTypeName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        if (sType.equalsIgnoreCase("calCost")) { //sj 20071123 add 获取流出证券的成本
            try {
                calCost(this.exchangeDate
                        , this.portCode
                        , this.analysisCode1
                        , this.analysisCode2
                        , this.securityCode
                        , this.baseCuryRate
                        , this.portCuryRate);
            } catch (YssException ex) {
                throw new YssException("保存证券兑换-流出信息出错" + "\r\n" + ex.getMessage(),
                                       ex);
            }
        }
        return this.buildRowStr();
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
                
                if (sRowStr.split("\r\t").length == 3) {
                    this.strExchangeOut = sRowStr.split("\r\t")[2];
                }

            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.num = reqAry[0];
            if (YssFun.isDate(reqAry[1])) {
                this.exchangeDate = YssFun.toDate(reqAry[1]);
            }
            this.securityCode = reqAry[2];
            this.tradeTypeCode = reqAry[3];
            this.portCode = reqAry[4];
            this.analysisCode1 = reqAry[5];
            this.analysisCode2 = reqAry[6];
            this.analysisCode3 = reqAry[7];
            if (YssFun.isNumeric(reqAry[8])) {
                this.exchangeAmount = Double.parseDouble(reqAry[8]);
            }
            if (YssFun.isNumeric(reqAry[9])) {
                this.exchangeCost = Double.parseDouble(reqAry[9]);
            }
            if (YssFun.isNumeric(reqAry[10])) {
                this.mExchangeCost = Double.parseDouble(reqAry[10]);
            }
            if (YssFun.isNumeric(reqAry[11])) {
                this.vExchangeCost = Double.parseDouble(reqAry[11]);
            }
            if (YssFun.isNumeric(reqAry[12])) {
                this.baseExchangeCost = Double.parseDouble(reqAry[12]);
            }
            if (YssFun.isNumeric(reqAry[13])) {
                this.mBaseExchangeCost = Double.parseDouble(reqAry[13]);
            }
            if (YssFun.isNumeric(reqAry[14])) {
                this.vBaseExchangeCost = Double.parseDouble(reqAry[14]);
            }
            if (YssFun.isNumeric(reqAry[15])) {
                this.portExchangeCost = Double.parseDouble(reqAry[15]);
            }
            if (YssFun.isNumeric(reqAry[16])) {
                this.mPortExchangeCost = Double.parseDouble(reqAry[16]);
            }
            if (YssFun.isNumeric(reqAry[17])) {
                this.vPortExchangeCost = Double.parseDouble(reqAry[17]);
            }
            if (YssFun.isNumeric(reqAry[18])) {
                this.baseCuryRate = Double.parseDouble(reqAry[18]);
            }
            if (YssFun.isNumeric(reqAry[19])) {
                this.portCuryRate = Double.parseDouble(reqAry[19]);
            }
            this.dataSource = reqAry[20];
            this.desc = reqAry[21];
            this.oldNum = reqAry[22];
            this.strIsOnlyColumns = reqAry[23];
            this.checkStateId = YssFun.toInt(reqAry[24]);
            if (YssFun.isDate(reqAry[25])) {
                this.beginDate = YssFun.toDate(reqAry[25]);
            }
            if (YssFun.isDate(reqAry[26])) {//// edited by zhouxiang MS01223    证券兑换界面，按照日期查询无效，仍显示所有数据    QDV4赢时胜(测试)2010年05月31日06_B    
                this.endDate = YssFun.toDate(reqAry[26]);
            }
            //this.attrClsCode = reqAry[27];
            //this.catTypeCode = reqAry[28];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new SecExchangeBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析证券兑换-流入信息出错\r\n" + e.getMessage(), e);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); //判断分析代码存不存在
        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
        this.num = rs.getString("FNum") + "";
        this.oldNum = rs.getString("FNum") + "";
        this.exchangeDate = rs.getDate("FExchangeDate");
        this.securityCode = rs.getString("FSecurityCode") + "";
        this.securityName = rs.getString("FSecurityName") + "";
        this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
        this.tradeTypeName = rs.getString("FTradeTypeName") + "";
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        if (analy1) {
            this.analysisCode1 = rs.getString("FInvMgrCode") + "";
            this.analysisName1 = rs.getString("FInvMgrName") + "";
        }
        if (analy2) {
            this.analysisCode2 = rs.getString("FBrokerCode") + "";
            this.analysisName2 = rs.getString("FBrokerName") + "";
        }
        this.analysisCode3 = rs.getString("FAnalysisCode3") + "";
        //this.brokerName = rs.getString("FAnalysisName3") + "";
        this.exchangeAmount = rs.getDouble("FINAMOUNT");
        this.exchangeCost = rs.getDouble("FExchangeCost");
        this.mExchangeCost = rs.getDouble("FMExchangeCost");
        this.vExchangeCost = rs.getDouble("FVExchangeCost");
        this.baseExchangeCost = rs.getDouble("FBaseExchangeCost");
        this.mBaseExchangeCost = rs.getDouble("FMBaseExchangeCost");
        this.vBaseExchangeCost = rs.getDouble("FVBaseExchangeCost");
        this.portExchangeCost = rs.getDouble("FPortExchangeCost");
        this.mPortExchangeCost = rs.getDouble("FMPortExchangeCost");
        this.vPortExchangeCost = rs.getDouble("FVPortExchangeCost");
        this.baseCuryRate = rs.getDouble("FBaseCuryRate");
        this.portCuryRate = rs.getDouble("FPortCuryRate");
        this.desc = rs.getString("FDesc") + "";
        this.dataSource = rs.getString("FDataSource");
        this.attrClsCode = rs.getString("FAttrClsCode");
        this.attrClsName = rs.getString("FAttrClsName");
        super.setRecLog(rs);
    }

    public String buildFilterSql() throws YssException {
        String strSql = "";
        try {
            if (this.filterType != null) {
                if (this.filterType.strIsOnlyColumns.equalsIgnoreCase("1")) {
                    strSql = strSql + " where 1=2 ";
                    return strSql;
                }
                //else里面的东西是lzp 11.7 号查出有误然后添加的
                else {
                    strSql = strSql + " where 1=1 ";
                }

                if (this.filterType.num.trim() != null &&
                    this.filterType.num.trim().length() > 0) {
                    strSql += " and a.FNum like '" +
                        this.filterType.num.trim().replaceAll("'", "''") + "%'";
                }
                if (this.filterType.securityCode.trim() != null
                    && this.filterType.securityCode.trim().length() > 0) {
                    strSql += " and a.FSecurityCode like '" +
                        this.filterType.securityCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.tradeTypeCode.trim() != null
                    && this.filterType.tradeTypeCode.trim().length() > 0) {
                    strSql += " and a.FTradeTypeName like '" +
                        this.filterType.tradeTypeCode.replaceAll("'", "''") + "%'";
                } else if (this.filterType.exchangeDate != null &&
                           !YssFun.formatDate(filterType.exchangeDate).equals(
                               "9998-12-31")) {
                    strSql += " and a.FExchangeDate = " +
                        dbl.sqlDate(this.filterType.exchangeDate);
                }

                if (this.filterType.portCode.trim() != null
                    && this.filterType.portCode.trim().length() > 0) {
                    strSql += " and a.FPortCode like '" +
                        this.filterType.portCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.analysisCode1.trim() != null &&
                    this.filterType.analysisCode1.trim().length() > 0) {
                    strSql += " and a.FAnalysisCode1 like '" +
                        this.filterType.analysisCode2.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.analysisCode2.trim() != null &&
                    this.filterType.analysisCode2.trim().length() > 0) {
                    strSql += " and a.FAnalysisCode2 like '" +
                        this.filterType.analysisCode2.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.analysisCode3.trim() != null &&
                    this.filterType.analysisCode3.trim().length() > 0) {
                    strSql += " and a.FAnalysisCode3 like '" +
                        this.filterType.analysisCode3.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.desc.trim() != null &&
                    this.filterType.desc.trim().length() > 0) {
                    strSql += " and a.FDesc like '" +
                        this.filterType.desc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.endDate != null &&
                    !YssFun.formatDate(this.filterType.endDate).equals("9998-12-31")) {
                    if (this.filterType.beginDate != null &&
                        !YssFun.formatDate(this.filterType.beginDate).equals(
                            "9998-12-31")) {
                        strSql += " and a.FExchangeDate between " +
                            dbl.sqlDate(this.filterType.beginDate) +
                            " and " + dbl.sqlDate(this.filterType.endDate);
                    }
                }
                if (this.filterType.attrClsCode.trim() != null &&
                    this.filterType.attrClsCode.trim().length() > 0) {
                    strSql += " and a.FAttrClsCode like '" +
                        this.filterType.attrClsCode.replaceAll("'", "''") + "%'";
                }
            }
        } catch (Exception e) {
            throw new YssException("过滤证券兑换-流入出错\r\n" + e.getMessage(), e);
        }
        return strSql;
    }

    /**
     * sj 20071203 add 获取流出证券的成本
     * @param rs ResultSet
     * @throws YssException
     */
    public void calCost(java.util.Date StorageDate
                        , String PortCode
                        , String AnalysisCode1
                        , String AnalysisCode2
                        , String SecurityCode
                        , double BaseCuryRate
                        , double PortCuryRate) throws YssException {
        String sqlStr = "";
        YssCost cost = null;
        try {
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
                "avgcostcalculate");
            //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
            //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
            costCal.initCostCalcutate(StorageDate,
                                      PortCode,
                                      (AnalysisCode1 == null || AnalysisCode1.length() == 0 ? " " : AnalysisCode1),
                                      (AnalysisCode2 == null || AnalysisCode2.length() == 0 ? " " : AnalysisCode2),
                                      attrClsCode);
            costCal.setYssPub(pub);
            cost = costCal.getCarryCost(SecurityCode,
                                        this.exchangeAmount,
                                        "00000",
                                        BaseCuryRate,
                                        PortCuryRate);
            costCal.roundCost(cost, 2);
            this.exchangeCost = cost.getCost();
            this.mExchangeCost = cost.getMCost();
            this.vExchangeCost = cost.getVCost();
            this.baseExchangeCost = cost.getBaseCost();
            this.mBaseExchangeCost = cost.getBaseMCost();
            this.vBaseExchangeCost = cost.getBaseVCost();
            this.portExchangeCost = cost.getPortCost();
            this.mPortExchangeCost = cost.getPortMCost();
            this.vPortExchangeCost = cost.getPortVCost();
        } catch (Exception e) {
            throw new YssException("获取证券兑换信息出错" + "\r\n" + e.getMessage(), e);
        }

    }

    /**
     * 证券兑换的清空回收站方法
     * 邱健
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        Connection conn = null;
        boolean bTrans = false;
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null && ! ("").equals(sRecycled)) {
                arrData = sRecycled.split("\r\n");
            }
            if (arrData == null)
            {
            	return ;
            }
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }

                //该语句不可省，否则将不能对数据字符串进行解析，这样将下面的 this.num 将不会改变
                this.parseRowStr(arrData[i]);

                strSql = "delete from " + pub.yssGetTableName("Tb_Data_SecExchangeIn") +
                    " where FNum = " +
                    dbl.sqlString(this.num);
                dbl.executeSql(strSql);
                strSql = "delete from " + pub.yssGetTableName("Tb_Data_SecExchangeOut") +
                    " where FNum = " +
                    dbl.sqlString(this.num);
                dbl.executeSql(strSql);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            try {
            	//--- edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
            	if(conn != null){
            		conn.rollback();
            	}
            	//--- edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
            } catch (SQLException ex) {
                throw new YssException("清除数据出错", ex);
            }
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
