package com.yss.main.syssetting;

/**
 *
 * <p>Title: DepartmentBean</p>
 * <p>Description: 部门设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ysstech</p>
 * @author not attributable
 * @version 1.0
 */

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

public class DepartmentBean
    extends BaseDataSettingBean {
    private String strDeptCode = ""; //部门代码
    private String strDeptName = ""; //部门名称
    private String strDesc = ""; //职位描述

    private String strOldDeptCode;
    private DepartmentBean filterType;

    public DepartmentBean() {
    }

    public DepartmentBean(YssPub pub) {
        setYssPub(pub);
    }

    /**
     * parseRowStr
     * 解析部门设置数据
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
            this.strDeptCode = reqAry[0];
            this.strDeptName = reqAry[1];
            this.strDesc = reqAry[2];
            this.checkStateId = Integer.parseInt(reqAry[3]);
            this.strOldDeptCode = reqAry[4];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DepartmentBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析部门设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strDeptCode.trim()).append("\t");
        buf.append(this.strDeptName.trim()).append("\t");
        buf.append(this.strDesc.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        String strSql = "", strTmp = "";
        if (btOper == YssCons.OP_ADD) {
            strSql = "select FDeptname from Tb_Sys_Department where FDeptCode=" +
                dbl.sqlString(this.strDeptCode.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
                throw new YssException("部门代码【" + this.strDeptCode.trim() +
                                       "】已经被部门【" + strTmp + "】所占用，请重新输入");
            }
        } else if (btOper == YssCons.OP_EDIT) {
            if (!this.strDeptCode.trim().equalsIgnoreCase(this.strOldDeptCode)) {
                strSql = "select FDeptname from Tb_Sys_Department where FDeptCode=" +
                    dbl.sqlString(this.strDeptCode.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("部门代码【" + this.strDeptCode.trim() +
                                           "】已经被部门【" + strTmp + "】所占用，请重新输入");
                }
            }
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";

            if (this.filterType.strDeptCode.length() != 0) {
                sResult = sResult + " and FDeptCode like '" +
                    filterType.strDeptCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strDeptName.length() != 0) {
                sResult = sResult + " and FDeptName like '" +
                    filterType.strDeptName.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取部门设置数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "部门代码\t部门名称\t部门描述";
            strSql = "select * from Tb_Sys_Department";
            strSql = strSql + buildFilterSql() + " order by FDeptCode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FDeptCode"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FDeptName"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FDesc"));
                bufShow.append(YssCons.YSS_LINESPLITMARK);

                this.strDeptCode = rs.getString("FDeptCode") + "";
                this.strDeptName = rs.getString("FDeptName") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.checkStateId = 1; //审核状态处理
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\fFDeptCode\tFDeptName\tFDesc";

        } catch (Exception se) {
            throw new YssException("获取部门信息出错！", se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "部门代码\t部门名称";
            strSql = "select * from Tb_Sys_Department ";
            strSql = strSql + " order by FDeptCode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FDeptCode"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FDeptName"));
                bufShow.append(YssCons.YSS_LINESPLITMARK);

                this.strDeptCode = rs.getString("FDeptCode") + "";
                this.strDeptName = rs.getString("FDeptName") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.checkStateId = 1; //审核状态处理
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        } catch (SQLException se) {
            throw new YssException("获取可用部门信息出错！", se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * saveSetting
     * 新增、修改、删除
     * @param btOper byte
     */
    public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            if (btOper == YssCons.OP_ADD) {
                strSql = "insert into Tb_Sys_Department(FDeptCode,FDeptName,FDesc)" +
                    " values(" + dbl.sqlString(this.strDeptCode) + "," +
                    dbl.sqlString(this.strDeptName) + "," +
                    dbl.sqlString(this.strDesc) + ")";
            } else if (btOper == YssCons.OP_EDIT) {
                strSql = "update Tb_Sys_Department set FDeptCode = " +
                    dbl.sqlString(this.strDeptCode) + ", FDeptName = " +
                    dbl.sqlString(this.strDeptName) + ", FDesc = " +
                    dbl.sqlString(this.strDesc) +
                    " where FDeptCode = " +
                    dbl.sqlString(this.strOldDeptCode);
            } else if (btOper == YssCons.OP_DEL) {
                strSql = "delete from Tb_Sys_Department " +
                    "where FDeptCode = " + dbl.sqlString(this.strDeptCode);
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存部门设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
