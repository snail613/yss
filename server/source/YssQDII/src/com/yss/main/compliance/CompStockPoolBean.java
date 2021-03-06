package com.yss.main.compliance;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: CompStockPoolBean</p>
 *
 * <p>Description: 监控股票池信息设置</p>
 *
 * <p>BugNO:MS00466-QDV4建行2009年5月15日01_A<监控模块针对监控指标和监控所需数据来源需求开发></p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Ysstech</p>
 *
 * @author xuqiji 20090603
 * @version 1.0
 */
public class CompStockPoolBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strSecurityCode = ""; //证券代码
    private String strSecurityName = ""; //证券名称
    private String strCatCode = ""; //品种代码
    private String strCatName = ""; //品种名称
    private String strSubCatCode = ""; //品种明细代码
    private String strSubCatName = ""; //品种明细名称
    private String strCountryCode = ""; //国家代码
    private String strCountryName = ""; //国家名称
    private String strISINCode = ""; //ISIN代码
    private String strExternalCode = ""; //外部代码
    private String strDesc = ""; //描述

    private String strOldSecurityCode = ""; //保存证券代码
    private String strOldISINCode = ""; //保存ISIN代码
    private String strOldExternalCode = ""; //保存外部代码
    private CompStockPoolBean filterType;
    private String sRecycled = ""; //回收站删除，保存数据的字符串

    public CompStockPoolBean() {
    }

    /**
     * 检查新建，修改，复制数据时，是否该数据已经存在
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Comp_SecurityPool"),
                               "FSecurityCode,FISINCode,FExternalCode",
                               this.strSecurityCode + "," + this.strISINCode + "," + this.strExternalCode,
                               this.strOldSecurityCode + "," + this.strOldISINCode + "," + this.strOldExternalCode);

    }

    /**
     * 新建数据
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        StringBuffer buff = null;
        boolean bTrans = true;
        Connection conn = null;
        try {
            buff = new StringBuffer();
            conn = dbl.loadConnection();
            buff.append("insert into ");
            buff.append(pub.yssGetTableName("Tb_Comp_SecurityPool"));
            buff.append(" (FSecurityCode,FSecurityName,FCatCode,FSubCatCode,FCountryCode,FISINCode,FExternalCode,FDesc,");
            buff.append("FCHECKSTATE,FCREATOR, FCREATETIME,FCheckUser) values(");
            buff.append(dbl.sqlString(this.strSecurityCode)).append(",");
            buff.append(dbl.sqlString(this.strSecurityName)).append(",");
            buff.append(dbl.sqlString(this.strCatCode)).append(",");
            buff.append(dbl.sqlString(this.strSubCatCode)).append(",");
            buff.append(dbl.sqlString(this.strCountryCode)).append(",");
            buff.append(dbl.sqlString(this.strISINCode)).append(",");
            buff.append(dbl.sqlString(this.strExternalCode)).append(",");
            buff.append(dbl.sqlString(this.strDesc)).append(",");
            buff.append( (pub.getSysCheckState() ? "0" : "1")).append(",");
            buff.append(dbl.sqlString(this.creatorCode)).append(",");
            buff.append(dbl.sqlString(this.creatorTime)).append(",");
            buff.append( (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode))).append(")");
            conn.setAutoCommit(false);
            dbl.executeSql(buff.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加监控股票池信息设置数据出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return buff.toString();
    }

    /**
     * 修改数据
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        StringBuffer buff = null;
        Connection conn = null;
        boolean bTrans = true;
        try {
            buff = new StringBuffer();
            conn = dbl.loadConnection();
            buff.append("update ");
            buff.append(pub.yssGetTableName("Tb_Comp_SecurityPool"));
            buff.append(" set FSecurityCode =");
            buff.append(dbl.sqlString(this.strSecurityCode)).append(",");
            buff.append("FSecurityName =").append(dbl.sqlString(this.strSecurityName)).append(",");
            buff.append("FCatCode =").append(dbl.sqlString(this.strCatCode)).append(",");
            buff.append("FSubCatCode =").append(dbl.sqlString(this.strSubCatCode)).append(",");
            buff.append("FCountryCode =").append(dbl.sqlString(this.strCountryCode)).append(",");
            buff.append("FISINCode =").append(dbl.sqlString(this.strISINCode)).append(",");
            buff.append("FExternalCode =").append(dbl.sqlString(this.strExternalCode)).append(",");
            buff.append("FDesc =").append(dbl.sqlString(this.strDesc)).append(",");
            buff.append("FCheckState =").append( (pub.getSysCheckState() ? "0" : "1")).append(",");
            buff.append("FCreator =").append(dbl.sqlString(this.creatorCode)).append(",");
            buff.append("FCreateTime =").append(dbl.sqlString(this.creatorTime)).append(",");
            buff.append("FCheckUser =").append( (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)));
            buff.append(" where FSecurityCode =").append(dbl.sqlString(this.strOldSecurityCode));
            buff.append(" and FISINCode =").append(dbl.sqlString(this.strOldISINCode));
            buff.append(" and FExternalCode =").append(dbl.sqlString(this.strOldExternalCode));
            conn.setAutoCommit(false);
            dbl.executeSql(buff.toString());
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改监控股票池信息设置数据出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return buff.toString();
    }

    /**
     * 删除后的数据先放到回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        StringBuffer buff = null;
        Connection conn = null;
        boolean bTrans = true;
        try {
            buff = new StringBuffer();
            conn = dbl.loadConnection();
            buff.append("update ");
            buff.append(pub.yssGetTableName("Tb_Comp_SecurityPool"));
            buff.append(" set FCheckState =");
            buff.append(this.checkStateId).append(",");
            buff.append("FCheckUser =").append(dbl.sqlString(pub.getUserCode())).append(",");
            buff.append("FCheckTime ='").append(YssFun.formatDate(new java.util.Date())).append("'");
            buff.append(" where FSecurityCode =").append(dbl.sqlString(this.strOldSecurityCode));
            buff.append(" and FISINCode =").append(dbl.sqlString(this.strOldISINCode));
            buff.append(" and FExternalCode =").append(dbl.sqlString(this.strOldExternalCode));

            conn.setAutoCommit(false);
            dbl.executeSql(buff.toString());
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除监控股票池信息设置数据出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 回收站还原功能，以及审核，反审核所调用的方法
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        StringBuffer buff = null;
        Connection conn = null;
        String[] array = null;
        Statement st = null;
        boolean bTrans = true;
        try {
            if (null != sRecycled && !"".equals(sRecycled.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                buff = new StringBuffer();
                conn = dbl.loadConnection();
                conn.setAutoCommit(false);
                st = conn.createStatement();
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                for (int i = 0; i < array.length; i++) { //循环执行这些还原语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    buff.append(" update ").append(pub.yssGetTableName("Tb_Comp_SecurityPool"));
                    buff.append(" set FCheckState =").append(this.checkStateId).append(",");
                    buff.append(" FCheckUser =").append(dbl.sqlString(pub.getUserCode())).append(",");
                    buff.append(" FCheckTime ='").append(YssFun.formatDate(new java.util.Date())).append("'");
                    buff.append(" where FSecurityCode =").append(dbl.sqlString(this.strSecurityCode));
                    buff.append(" and FISINCode =").append(dbl.sqlString(this.strISINCode));
                    buff.append(" and FExternalCode =").append(dbl.sqlString(this.strExternalCode));
                    st.addBatch(buff.toString());
                    buff.delete(0, buff.length());
                }
                st.executeBatch();
                
                conn.commit();
                conn.setAutoCommit(true);
                bTrans = false;
            }

        } catch (Exception e) {
            throw new YssException("还原监控股票池信息设置数据出错！", e);
        } finally {
        	if(conn != null){
        		dbl.endTransFinal(conn, bTrans);
        	}
        	if(st != null){
        		dbl.closeStatementFinal(st);
        	}
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * 回收站清除功能
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        StringBuffer buff = null;
        Connection conn = null;
        boolean bTrans = true; //代表事务是否开始
        String[] array = null;
        Statement st = null;
        try {
            if (null != sRecycled && !"".equals(sRecycled.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                buff = new StringBuffer();
                conn = dbl.loadConnection();
                conn.setAutoCommit(false);
                st = conn.createStatement();
                for (int i = 0; i < array.length; i++) { //循环执行这些删除语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    buff.append("delete from ");
                    buff.append(pub.yssGetTableName("Tb_Comp_SecurityPool"));
                    buff.append(" where FSecurityCode =").append(dbl.sqlString(this.strSecurityCode));
                    buff.append(" and FISINCode =").append(dbl.sqlString(this.strISINCode));
                    buff.append(" and FExternalCode =").append(dbl.sqlString(this.strExternalCode));
                    st.addBatch(buff.toString());
                    buff.delete(0, buff.length());
                }
                st.executeBatch();
                
                //---edit by songjie 2012.07.18  STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
                conn.commit();
                conn.setAutoCommit(true);
                bTrans = false;
                //---edit by songjie 2012.07.18  STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
            }
        } catch (Exception e) {
            throw new YssException("清除监控股票池信息数据出错！", e);
        } finally {
        	//---edit by songjie 2012.07.18  STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
        	if(conn != null){
        		dbl.endTransFinal(conn, bTrans);
        	}
        	if(st != null){
        		dbl.closeStatementFinal(st);
        	}
        	//---edit by songjie 2012.07.18  STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
        }
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

    /**
     * 获取监控股票池信息设置数据
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        StringBuffer buff = null;
        StringBuffer bufAll = null;
        StringBuffer bufShow = null;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        try {
            buff = new StringBuffer();
            bufAll = new StringBuffer();
            bufShow = new StringBuffer();
            sHeader = this.getListView1Headers();
            buff.append(" select y.* from (select FSecurityCode, FISINcode, FExternalCode, FCheckState from ");
            buff.append(pub.yssGetTableName("Tb_Comp_SecurityPool"));
            buff.append(" group by FSecurityCode, FISINcode, FExternalCode, FCheckState) x join (select a.*, ");
            buff.append(" b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FCatName as FCatName,");
            buff.append(" f.FSubCatName as FSubCatName,g.FCountryName as FCountryName from ");
            buff.append(pub.yssGetTableName("Tb_Comp_SecurityPool")).append(" a ");
            buff.append(" left join (select FUserCode, FUserName from ").append(pub.yssGetTableName("Tb_Sys_UserList"));
            buff.append(" ) b on a.FCreator = b.FUserCode left join (select FUserCode, FUserName from ");
            buff.append(pub.yssGetTableName("Tb_Sys_UserList")).append(") c on a.FCheckUser = c.FUserCode ");
            buff.append(" left join (select FSecurityCode, FSecurityName from ");
            buff.append(pub.yssGetTableName("Tb_Para_Security"));
            buff.append(" ) d on a.FSecurityCode = d.FSecurityCode");
            buff.append(" left join (select FCatCode, FCatName from ").append(pub.yssGetTableName("Tb_Base_Category"));
            buff.append(" where FCheckState = 1) e on e.FCatCode = a.FCatCode");
            buff.append(" left join (select FSubCatCode, FSubCatName from ").append(pub.yssGetTableName("Tb_Base_SubCategory"));
            buff.append(" where FCheckState = 1) f on f.FSubCatCode = a.FSubCatCode");
            buff.append(" left join (select FCountryCode, FCountryName from ").append(pub.yssGetTableName("Tb_Base_Country"));
            buff.append(" where FCheckState = 1) g on g.FCountryCode = a.FCountryCode ");
            buff.append(this.buildFilterSql());
            buff.append(" ) y on x.FSecurityCode = y.FSecurityCode");
            buff.append(" and x.FISINCode = y.FISINCode and x.FExternalCode = y.FExternalCode");
            buff.append(" order by y.FCheckState, y.FCreateTime");

            rs = dbl.openResultSet(buff.toString());
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);
                this.strSecurityCode = rs.getString("FSecurityCode");
                this.strSecurityName = rs.getString("FSecurityName");
                this.strCatCode = rs.getString("FCatCode");
                this.strCatName = rs.getString("FCatName");
                this.strSubCatCode = rs.getString("FSubCatCode");
                this.strSubCatName = rs.getString("FSubCatName");
                this.strCountryCode = rs.getString("FCountryCode");
                this.strCountryName = rs.getString("FCountryName");
                this.strISINCode = rs.getString("FISINCode");
                this.strExternalCode = rs.getString("FExternalCode");
                this.strDesc = rs.getString("FDesc");
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("查询监控股票池信息设置数据出错！\r\t", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 筛选条件
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.filterType.strSecurityCode != null && this.filterType.strSecurityCode.length() != 0 ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    sResult = sResult + " and a.FSecurityCode like '" +
                        filterType.strSecurityCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strCatCode.length() != 0 &&
                    this.filterType.strSecurityCode != null) {
                    sResult = sResult + " and a.FCatCode like '" +
                        filterType.strCatCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strSubCatCode.length() != 0 &&
                    this.filterType.strSecurityCode != null) {
                    sResult = sResult + " and a.FSubCatCode like '" +
                        filterType.strSubCatCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strDesc.length() != 0 &&
                    this.filterType.strSecurityCode != null) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.strDesc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strCountryCode.length() != 0 &&
                    this.filterType.strSecurityCode != null) {
                    sResult = sResult + " and a.FCountryCode like '" +
                        filterType.strCountryCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strISINCode.length() != 0 &&
                    this.filterType.strSecurityCode != null) {
                    sResult = sResult + " and a.FISINCode like '" +
                        filterType.strISINCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strExternalCode.length() != 0 &&
                    this.filterType.strSecurityCode != null) {
                    sResult = sResult + " and a.FExternalCode like '" +
                        filterType.strExternalCode.replaceAll("'", "''") + "%'";
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选监控股票池信息设置数据出错！", e);
        }
        return sResult;
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

    /**
     * 解析前台传来的字符串
     * @param sRowStr String 从前台传来的字符串
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String array[] = null;
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
            sRecycled = sRowStr;
            array = sTmpStr.split("\t");
            this.strSecurityCode = array[0];
            this.strSecurityName = array[1];
            this.strCatCode = array[2];
            this.strSubCatCode = array[3];
            this.strCountryCode = array[4];
            this.strISINCode = array[5];
            this.strExternalCode = array[6];
            this.strDesc = array[7];
            this.checkStateId = YssFun.toInt(array[8]);
            this.strOldSecurityCode = array[9];
            this.strOldISINCode = array[10];
            this.strOldExternalCode = array[11];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CompStockPoolBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析监控股票池信息设置出错！\r\t", e);
        }
    }

    /**
     * 做字符串拼接
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(this.strCatCode).append("\t");
        buf.append(this.strCatName).append("\t");
        buf.append(this.strSubCatCode).append("\t");
        buf.append(this.strSubCatName).append("\t");
        buf.append(this.strCountryCode).append("\t");
        buf.append(this.strCountryName).append("\t");
        buf.append(this.strISINCode).append("\t");
        buf.append(this.strExternalCode).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(super.buildRecLog());

        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getStrCatCode() {
        return strCatCode;
    }

    public String getStrCountryCode() {
        return strCountryCode;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrExternalCode() {
        return strExternalCode;
    }

    public String getStrISINCode() {
        return strISINCode;
    }

    public String getStrOldSecurityCode() {
        return strOldSecurityCode;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrSubCatCode() {
        return strSubCatCode;
    }

    public CompStockPoolBean getFilterType() {
        return filterType;
    }

    public String getSRecycled() {
        return sRecycled;
    }

    public String getStrCatName() {
        return strCatName;
    }

    public String getStrCountryName() {
        return strCountryName;
    }

    public String getStrSubCatName() {
        return strSubCatName;
    }

    public String getStrOldExternalCode() {
        return strOldExternalCode;
    }

    public String getStrOldISINCode() {
        return strOldISINCode;
    }

    public void setFilterType(CompStockPoolBean filterType) {
        this.filterType = filterType;
    }

    public void setStrCatCode(String strCatCode) {
        this.strCatCode = strCatCode;
    }

    public void setStrCountryCode(String strCountryCode) {
        this.strCountryCode = strCountryCode;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrExternalCode(String strExternalCode) {
        this.strExternalCode = strExternalCode;
    }

    public void setStrISINCode(String strISINCode) {
        this.strISINCode = strISINCode;
    }

    public void setStrOldSecurityCode(String strOldSecurityCode) {
        this.strOldSecurityCode = strOldSecurityCode;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrSubCatCode(String strSubCatCode) {
        this.strSubCatCode = strSubCatCode;
    }

    public void setSRecycled(String sRecycled) {
        this.sRecycled = sRecycled;
    }

    public void setStrCatName(String strCatName) {
        this.strCatName = strCatName;
    }

    public void setStrCountryName(String strCountryName) {
        this.strCountryName = strCountryName;
    }

    public void setStrSubCatName(String strSubCatName) {
        this.strSubCatName = strSubCatName;
    }

    public void setStrOldExternalCode(String strOldExternalCode) {
        this.strOldExternalCode = strOldExternalCode;
    }

    public void setStrOldISINCode(String strOldISINCode) {
        this.strOldISINCode = strOldISINCode;
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }
}
