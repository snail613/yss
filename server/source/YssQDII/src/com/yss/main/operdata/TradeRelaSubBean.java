package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class TradeRelaSubBean
    extends BaseDataSettingBean implements IDataSetting {
    public TradeRelaSubBean() {
    }

    private String sNum = "";
    // MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》2009.06.16 蒋锦 添加
    //添加业务日期和数量字段 用于ETF申购冲减估值增值的计算
    private java.util.Date bargainDate;
    private double amount = 0;
    private String sTradeNum = ""; //取外部交易编号
    private String sSecurityCode = "";
    private String sAnalysisCode1 = "";
    private String sAnalysisCode2 = "";
    private String sAnalysisCode3 = "";
    private String sRelaType = "";
    private String sTsfTypeCode = "";
    private String sTsfTypeName = "";
    private String sSubTsfTypeCode = "";
    private String sSubTsfTypeName = "";
    private String sCuryCode = "";
    private String sCuryName = "";
    private String sPortCode = "";
    //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
    //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
    private String attrClsCode = "";
    private int iInOut = 1; //流入流出方向,默认为正方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
    private String sDesc = "";
    private String sFilterSql = "";
    private String sOldNum = "";
    private String sOldRelaType = "";
    private String sOldTsfTypeCode = "";
    private String sOldSubTsfTypeCode = "";

    private double dBal;
    private double dMBal;
    private double dVBal;
    private double dPortCuryBal;
    private double dMPortCuryBal;
    private double dVPortCuryBal;
    private double dBaseCuryBal;
    private double dMBaseCuryBal;
    private double dVBaseCuryBal;
    private TradeRelaBean tradeRela = null;
    private TradeRelaSubBean filterType = null;
    private String delAll = "";
    public double getDBal() {
        return dBal;
    }

    public double getDBaseCuryBal() {
        return dBaseCuryBal;
    }

    public double getDMBal() {
        return dMBal;
    }

    public double getDMBaseCuryBal() {
        return dMBaseCuryBal;
    }

    public double getDMPortCuryBal() {
        return dMPortCuryBal;
    }

    public double getDPortCuryBal() {
        return dPortCuryBal;
    }

    public double getDVBal() {
        return dVBal;
    }

    public double getDVBaseCuryBal() {
        return dVBaseCuryBal;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public double getDVPortCuryBal() {
        return dVPortCuryBal;
    }

    public String getSCuryName() {
        return sCuryName;
    }

    public String getSNum() {
        return sNum;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSRelaType() {
        return sRelaType;
    }

    public String getDelAll() {
        return delAll;
    }

    public String getSSubTsfTypeCode() {
        return sSubTsfTypeCode;
    }

    public String getSSubTsfTypeName() {
        return sSubTsfTypeName;
    }

    public String getSTsfTypeCode() {
        return sTsfTypeCode;
    }

    public String getSTsfTypeName() {
        return sTsfTypeName;
    }

    public String getSOldRelaType() {
        return sOldRelaType;
    }

    public String getSOldNum() {
        return sOldNum;
    }

    public String getSOldTsfTypeCode() {
        return sOldTsfTypeCode;
    }

    public String getSOldSubTsfTypeCode() {
        return sOldSubTsfTypeCode;
    }

    public TradeRelaSubBean getFilterType() {
        return filterType;
    }

    public String getSTradeNum() {
        return sTradeNum;
    }

    public String getSSecurityCode() {
        return sSecurityCode;
    }

    public String getSAnalysisCode3() {
        return sAnalysisCode3;
    }

    public String getSAnalysisCode2() {
        return sAnalysisCode2;
    }

    public String getSAnalysisCode1() {
        return sAnalysisCode1;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public TradeRelaBean getTradeRela() {
        return tradeRela;
    }

    public String getSFilterSql() {
        return sFilterSql;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public int getIInOut() {
        return iInOut;
    }

    public void setSTsfTypeName(String sTsfTypeName) {
        this.sTsfTypeName = sTsfTypeName;
    }

    public void setDelAll(String sdelAll) {
        this.delAll = sdelAll;

    }

    public void setSTsfTypeCode(String sTsfTypeCode) {
        this.sTsfTypeCode = sTsfTypeCode;
    }

    public void setSSubTsfTypeName(String sSubTsfTypeName) {
        this.sSubTsfTypeName = sSubTsfTypeName;
    }

    public void setSSubTsfTypeCode(String sSubTsfTypeCode) {
        this.sSubTsfTypeCode = sSubTsfTypeCode;
    }

    public void setSRelaType(String sRelaType) {
        this.sRelaType = sRelaType;
    }

    public void setSNum(String sNum) {
        this.sNum = sNum;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSCuryName(String sCuryName) {
        this.sCuryName = sCuryName;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setDVPortCuryBal(double dVPortCuryBal) {
        this.dVPortCuryBal = dVPortCuryBal;
    }

    public void setDVBaseCuryBal(double dVBaseCuryBal) {
        this.dVBaseCuryBal = dVBaseCuryBal;
    }

    public void setDVBal(double dVBal) {
        this.dVBal = dVBal;
    }

    public void setDPortCuryBal(double dPortCuryBal) {
        this.dPortCuryBal = dPortCuryBal;
    }

    public void setDMPortCuryBal(double dMPortCuryBal) {
        this.dMPortCuryBal = dMPortCuryBal;
    }

    public void setDMBaseCuryBal(double dMBaseCuryBal) {
        this.dMBaseCuryBal = dMBaseCuryBal;
    }

    public void setDMBal(double dMBal) {
        this.dMBal = dMBal;
    }

    public void setDBaseCuryBal(double dBaseCuryBal) {
        this.dBaseCuryBal = dBaseCuryBal;
    }

    public void setDBal(double dBal) {
        this.dBal = dBal;
    }

    public void setSOldRelaType(String sOldRelaType) {
        this.sOldRelaType = sOldRelaType;
    }

    public void setSOldNum(String sOldNum) {
        this.sOldNum = sOldNum;
    }

    public void setSOldTsfTypeCode(String sOldTsfTypeCode) {
        this.sOldTsfTypeCode = sOldTsfTypeCode;
    }

    public void setSOldSubTsfTypeCode(String sOldSubTsfTypeCode) {
        this.sOldSubTsfTypeCode = sOldSubTsfTypeCode;
    }

    public void setFilterType(TradeRelaSubBean filterType) {
        this.filterType = filterType;
    }

    public void setSTradeNum(String sTradeNum) {
        this.sTradeNum = sTradeNum;
    }

    public void setSSecurityCode(String sSecurityCode) {
        this.sSecurityCode = sSecurityCode;
    }

    public void setSAnalysisCode3(String sAnalysisCode3) {
        this.sAnalysisCode3 = sAnalysisCode3;
    }

    public void setSAnalysisCode2(String sAnalysisCode2) {
        this.sAnalysisCode2 = sAnalysisCode2;
    }

    public void setSAnalysisCode1(String sAnalysisCode1) {
        this.sAnalysisCode1 = sAnalysisCode1;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setTradeRela(TradeRelaBean tradeRela) {
        this.tradeRela = tradeRela;
    }

    public void setSFilterSql(String sFilterSql) throws YssException {
        if (this.filterType != null) {
            this.sFilterSql = this.buildFilterSql();
        } else {
            this.sFilterSql = sFilterSql;
        }
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setIInOut(int iInOut) {
        this.iInOut = iInOut;
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    /**
     * 单亮
     * 2008-4-16
     * 删除交易数据的子关联数据
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "delete " + pub.yssGetTableName("tb_data_traderelasub") +
                " where FNUM = " +
                dbl.sqlString(this.sTradeNum) + " AND FTsfTypeCode = " +
                dbl.sqlString(this.sSubTsfTypeCode) + " AND FTsfTypeCode = " +
                dbl.sqlString(this.sTsfTypeCode);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除板块信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void checkSetting() throws YssException {
    }

    /**
     * 此方法已被修改
     * 修改时间： 2008-4-18
     * 修改人：单亮
     * 修改原因： 原方法不能彻底删除数据
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null; ;
        String strSql = "";
        boolean bTrans = false;
        PreparedStatement pst = null;
        ResultSet rs = null;
        sMutilRowStr = sMutilRowStr.replaceAll("\r\n", "\t\b");
        String[] sOneData = sMutilRowStr.split("\t\b"); //此处分隔符为\r\n不是
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_TradeRelaSub") +
                " where FNum =" + dbl.sqlString(this.sTradeNum) +
                " and FRelaType=" + dbl.sqlString(tradeRela.getSRelaType()) +
                " and FPortCode=" + dbl.sqlString(tradeRela.getSPortCode()) +
                " and FSecurityCode=" + dbl.sqlString(tradeRela.getSSecurityCode()) +
                " and FAnalysisCode1=" + dbl.sqlString(tradeRela.getSAnalysisCode1()) +
                " and FAnalysisCode2=" + dbl.sqlString(tradeRela.getSAnalysisCode2()) +
                " and FAnalysisCode3=" + dbl.sqlString(tradeRela.getSAnalysisCode3()) +
                //------------2009-07-07 蒋锦 添加 属性分类--------------//
                //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
                " AND FAttrClsCode=" + dbl.sqlString(tradeRela.getAttrClsCode().length() == 0 ? " " : tradeRela.getAttrClsCode());

            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_TradeRelaSub") +
                "(FNUM,FRELATYPE,FTSFTYPECODE,FSUBTSFTYPECODE,FCURYCODE,FAttrClsCode,FBAL,FMBAL,FVBAL,FPORTCURYBAL," +
                "FMPORTCURYBAL,FVPORTCURYBAL,FBASECURYBAL,FMBASECURYBAL,FVBASECURYBAL,FDESC,FCHECKSTATE,FCREATOR," +
                "FCREATETIME,FCHECKUSER,FCHECKTIME,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FSecurityCode,FPortCode,FInOut)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = dbl.openPreparedStatement(strSql);
            for (int i = 0; i < sOneData.length; i++) {
                this.parseRowStr(sOneData[i]);
                pst.setString(1, sTradeNum);
                pst.setString(2, tradeRela.getSRelaType());
                pst.setString(3, this.sTsfTypeCode);
                pst.setString(4, this.sSubTsfTypeCode);
                strSql = " select FTradeCury as FCuryCode from " + pub.yssGetTableName("tb_para_security") +
                    " where FSecurityCode=" + dbl.sqlString(tradeRela.getSSecurityCode());
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    pst.setString(5, rs.getString("FCuryCode"));
                }
                //------------2009-07-07 蒋锦 添加 属性分类--------------//
                //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
                pst.setString(6, this.attrClsCode.length() == 0 ? " " : attrClsCode);
                //-----------------------------------------------------//
                pst.setDouble(7, this.dBal);
                pst.setDouble(8, this.dMBal);
                pst.setDouble(9, this.dVBal);
                pst.setDouble(10, this.dPortCuryBal);
                pst.setDouble(11, this.dMPortCuryBal);
                pst.setDouble(12, this.dVPortCuryBal);
                pst.setDouble(13, this.dBaseCuryBal);
                pst.setDouble(14, this.dMBaseCuryBal);
                pst.setDouble(15, this.dVBaseCuryBal);
                pst.setString(16, this.sDesc);
                pst.setInt(17, 0); //默认值是未审核
                pst.setString(18, this.creatorCode);
                pst.setString(19, this.creatorTime);
                pst.setString(20,
                              (pub.getSysCheckState() ? " " : this.checkUserCode));
                pst.setString(21, (pub.getSysCheckState() ? " " : this.checkTime));
                pst.setString(22, tradeRela.getSAnalysisCode1());
                pst.setString(23, tradeRela.getSAnalysisCode2());
                pst.setString(24, tradeRela.getSAnalysisCode3());
                pst.setString(25, tradeRela.getSSecurityCode());
                pst.setString(26, tradeRela.getSPortCode());
                pst.setInt(27, this.iInOut); //流入流出方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
                if (!this.delAll.equals("True")) { //--2008-4-18--单亮--如果全部删除的标识为true就删除所有的而不插入
                    pst.executeUpdate();
                }
            }
            rs.close();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("设置交易关联子表出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String strSql = "";
        if (this.filterType != null) {
            strSql = " where 1=1 ";
            if (this.filterType.sNum != null && this.filterType.sNum.length() > 0) {
                strSql += " and a.FNum like '" + filterType.sNum.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sRelaType != null && filterType.sRelaType.trim().length() > 0) {
                strSql += " and a.FRelaType like '" + filterType.sRelaType.replaceAll("'", "''") + "%'";
            }
            if (filterType.sTsfTypeCode != null && filterType.sTsfTypeCode.trim().length() > 0) {
                strSql += " and a.FTsfTypeCode like '" + filterType.sTsfTypeCode.replaceAll("'", "''") + "%'";
            }
            if (filterType.sSubTsfTypeCode != null && filterType.sSubTsfTypeCode.trim().length() > 0) {
                strSql += " and a.FSubTsfTypeCode like '" + filterType.sSubTsfTypeCode.replaceAll("'", "''") + "%'";
            }
            if (filterType.sSecurityCode != null && filterType.sSecurityCode.trim().length() > 0) {
                strSql += " and a.FSecurityCode like '" + filterType.sSecurityCode.replaceAll("'", "''") + "%'";
            }
            if (filterType.sAnalysisCode1 != null && filterType.sAnalysisCode1.trim().length() > 0) {
                strSql += " and a.FAnalysisCode1 like '" + filterType.sAnalysisCode1.replaceAll("'", "''") + "%'";
            }
            if (filterType.sAnalysisCode2 != null && filterType.sAnalysisCode2.trim().length() > 0) {
                strSql += " and a.FAnalysisCode2 like '" + filterType.sAnalysisCode2.replaceAll("'", "''") + "%'";
            }
            if (filterType.sAnalysisCode3 != null && filterType.sAnalysisCode3.trim().length() > 0) {
                strSql += " and a.FAnalysisCode3 like '" + filterType.sAnalysisCode3.replaceAll("'", "''") + "%'";
            }
            if (filterType.sPortCode != null && filterType.sPortCode.trim().length() > 0) {
                strSql += " and a.FPortCode like '" + filterType.sPortCode.replaceAll("'", "''") + "%'";
            }
        } else {
            strSql = this.sFilterSql;
        }
        return strSql;
    }

    private void setRelaSubSetAttr(ResultSet rs) throws SQLException, YssException {
        this.sNum = rs.getString("FNum");
        this.sRelaType = rs.getString("FRelaType");
        this.sTsfTypeCode = rs.getString("FTsfTypeCode");
        this.sTsfTypeName = rs.getString("FTsfTypeName");
        this.sSubTsfTypeCode = rs.getString("FSubTsfTypeCode");
        this.sSubTsfTypeName = rs.getString("FSubTsfTypeName");
        this.sCuryCode = rs.getString("FCuryCode");
        this.sCuryName = rs.getString("FCuryName");
        this.attrClsCode = rs.getString("FAttrClsCode");
        this.iInOut = rs.getInt("FInOut"); //流入流出方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
        this.dBal = rs.getDouble("FBal");
        this.dMBal = rs.getDouble("FMBal");
        this.dVBal = rs.getDouble("FVBal");
        this.dBaseCuryBal = rs.getDouble("FBaseCuryBal");
        this.dMBaseCuryBal = rs.getDouble("FMBaseCuryBal");
        this.dVBaseCuryBal = rs.getDouble("FVBaseCuryBal");
        this.dPortCuryBal = rs.getDouble("FPortCuryBal");
        this.dMPortCuryBal = rs.getDouble("FMPortCuryBal");
        this.dVPortCuryBal = rs.getDouble("FVPortCuryBal");
        this.sDesc = rs.getString("FDesc") == null ? "" : rs.getString("FDesc");
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                " d.FTsfTypeName as FTsfTypeName,e.FSubTsfTypeName as FSubTsfTypeName," +
                " f.FCuryName as FCuryName " +
                " from " + pub.yssGetTableName("Tb_Data_Traderelasub") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode " +
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) d on a.FTsFTypeCode =d.FTsfTypeCode " +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType) e on a.FSubTsfTypeCode=e.FSubTsfTypeCode " +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") f on a.FCuryCode=f.FCuryCode " +
                this.buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append("\r\n");
                setRelaSubSetAttr(rs);
                bufAll.append(this.buildRowStr()).append("\r\n");
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\f\n" + sShowDataStr + "\f\n" + sAllDataStr + "\f\n" +
                this.getListView1ShowCols() + "\f\fnull";
        } catch (Exception e) {
            throw new YssException("获取交易关联子表信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.indexOf("\r\t") > 2) {
                    buildSql(sRowStr.split("\r\t")[2]);
                }
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            // this.sNum = reqAry[0];
            //this.sRelaType  = reqAry[1];
            //if(this.sRelaType.length()==0) this.sRelaType =" ";
            this.sTsfTypeCode = reqAry[0];
            if (this.sTsfTypeCode.length() == 0) {
                this.sTsfTypeCode = " ";
            }
            this.sSubTsfTypeCode = reqAry[2];
            if (this.sSubTsfTypeCode.length() == 0) {
                this.sSubTsfTypeCode = " ";
            }
            this.dBal = YssFun.toNumber(reqAry[4]);
            this.dMBal = YssFun.toNumber(reqAry[5]);
            this.dVBal = YssFun.toNumber(reqAry[6]);
            this.dPortCuryBal = YssFun.toNumber(reqAry[7]);
            this.dMPortCuryBal = YssFun.toNumber(reqAry[8]);
            this.dVPortCuryBal = YssFun.toNumber(reqAry[9]);
            this.dBaseCuryBal = YssFun.toNumber(reqAry[10]);
            this.dMBaseCuryBal = YssFun.toNumber(reqAry[11]);
            this.dVBaseCuryBal = YssFun.toNumber(reqAry[12]);
            this.sCuryCode = reqAry[13];
            if (sCuryCode.length() == 0) {
                sCuryCode = " ";
            }
            this.sCuryName = reqAry[14];
            this.sDesc = reqAry[15]; //-2008-4-18-单亮-添加彻底删除数据的标识
            //------------2009-07-07 蒋锦 添加 属性分类--------------//
            //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
            this.attrClsCode = reqAry[16];
            //----------------------//
            //---edit by songjie 2012.05.04 BUG 4397 QDV4赢时胜(上海)2012年04月26日06_B 将Inout数据提到delAll数据前，与前台buildStr方法字段拼接顺序一致 start---//
            this.iInOut = YssFun.toInt(reqAry[17]); //流入流出方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
            this.delAll = reqAry[18];
            //MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务 2009.06.16 蒋锦 添加
            //添加 ETF 冲减估值增值所需字段的解析
            this.bargainDate = YssFun.toDate(reqAry[19]);
            this.amount = YssFun.toDouble(reqAry[20]);
            this.sSecurityCode = reqAry[21];
            this.sPortCode = reqAry[22];
            this.sAnalysisCode1 = reqAry[23];
            this.sAnalysisCode2 = reqAry[24];
            this.sAnalysisCode3 = reqAry[25];
            //---edit by songjie 2012.05.04 BUG 4397 QDV4赢时胜(上海)2012年04月26日06_B end---//
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TradeRelaSubBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析交易关联子表出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        //buf.append(sNum).append("\t");
        //buf.append(sRelaType).append("\t");
        buf.append(sTsfTypeCode).append("\t");
        buf.append(sTsfTypeName).append("\t");
        buf.append(sSubTsfTypeCode).append("\t");
        buf.append(sSubTsfTypeName).append("\t");
        buf.append(dBal).append("\t");
        buf.append(dMBal).append("\t");
        buf.append(dVBal).append("\t");
        buf.append(dPortCuryBal).append("\t");
        buf.append(dMPortCuryBal).append("\t");
        buf.append(dVPortCuryBal).append("\t");
        buf.append(dBaseCuryBal).append("\t");
        buf.append(dMBaseCuryBal).append("\t");
        buf.append(dVBaseCuryBal).append("\t");
        buf.append(sCuryCode).append("\t");
        buf.append(sCuryName).append("\t");
        buf.append(sDesc).append("\t");
        buf.append(attrClsCode).append("\t");
        buf.append(iInOut).append("\t"); //流入流出方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务 2009.06.16 蒋锦 添加
     * 获取加权平均应收应付
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String getOperValue(String sType) throws YssException {
        SecPecPayBean pay = null;
        try {
            if (sType.equalsIgnoreCase("getrecpay")) {
                ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().
                    getBean(
                        "avgcostcalculate");
                costCal.setYssPub(pub);
                //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
                //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
                costCal.initCostCalcutate(this.bargainDate,
                                          this.sPortCode,
                                          this.sAnalysisCode1,
                                          "",
                                          attrClsCode);
                pay = costCal.getCarryRecPay(this.sSecurityCode,
                                             amount,
                                             this.sTradeNum,
                                             "subtrade",
                                             "",
                                             this.sTsfTypeCode,
                                             this.sSubTsfTypeCode);
                if (pay == null) {
                    return "";
                }
                dBal = pay.getMoney();
                dMBal = pay.getMMoney();
                dVBal = pay.getVMoney();
                dPortCuryBal = pay.getPortCuryMoney();
                dMPortCuryBal = pay.getMPortCuryMoney();
                dVPortCuryBal = pay.getMPortCuryMoney();
                dBaseCuryBal = pay.getBaseCuryMoney();
                dMBaseCuryBal = pay.getMBaseCuryMoney();
                dVBaseCuryBal = pay.getVBaseCuryMoney();
                return this.buildRowStr();
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        }
        return "";
    }

    private void buildSql(String sfilter) {
        this.filterType = new TradeRelaSubBean();
        String[] sTmp = sfilter.split("\t");
        filterType.sNum = sTmp[0];
        filterType.sRelaType = sTmp[1];
        filterType.sSecurityCode = sTmp[2];
        filterType.sPortCode = sTmp[3];
        filterType.sAnalysisCode1 = sTmp[4];
        filterType.sAnalysisCode2 = sTmp[5];
        filterType.sAnalysisCode3 = sTmp[6];
        filterType.attrClsCode = sTmp[7];
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
