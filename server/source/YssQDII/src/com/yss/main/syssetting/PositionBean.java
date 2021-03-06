package com.yss.main.syssetting;

/**
 *
 * <p>Title: PositionBean</p>
 * <p>Description: 职位设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ysstech</p>
 * @author not attributable
 * @version 1.0
 */

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

public class PositionBean
    extends BaseDataSettingBean {

    private String strPositionCode = ""; //职位代码
    private String strPositionName = ""; //职位名称
    private String strDeptCode = ""; //部门代码
    private String strDeptName = ""; //部门名称
    private String strDesc = ""; //职位描述

    private String strOldPositionCode;
    private PositionBean filterType;

    public PositionBean() {

    }

    /**
     * parseRowStr
     * 解析职位设置数据
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
            this.strPositionCode = reqAry[0];
            this.strPositionName = reqAry[1];
            this.strDeptCode = reqAry[2];
            this.strDeptName = reqAry[3];
            this.strDesc = reqAry[4];
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.strOldPositionCode = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new PositionBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析职位设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strPositionCode.trim()).append("\t");
        buf.append(this.strPositionName.trim()).append("\t");
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
            strSql = "select FPositionName from Tb_Sys_Position where FPositionCode=" +
                dbl.sqlString(this.strPositionCode.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
                throw new YssException("职位代码【" + this.strPositionCode.trim() +
                                       "】已经被职位【" + strTmp + "】所占用，请重新输入");
            }
        } else if (btOper == YssCons.OP_EDIT) {
            if (!this.strPositionCode.trim().equalsIgnoreCase(this.strOldPositionCode)) {
                strSql = "select FPositionName from Tb_Sys_Position where FPositionCode=" +
                    dbl.sqlString(this.strPositionCode.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("职位代码【" + this.strPositionCode.trim() +
                                           "】已经被职位【" + strTmp + "】所占用，请重新输入");
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
            if (this.filterType.strPositionCode.length() != 0) {
                sResult = sResult + " and a.FPositionCode like '" +
                    filterType.strPositionCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strPositionName.length() != 0) {
                sResult = sResult + " and a.FPositionName like '" +
                    filterType.strPositionName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strDeptCode.length() != 0) {
                sResult = sResult + " and a.FDeptCode like '" +
                    filterType.strDeptCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取职位设置数据
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
            sHeader = "职位代码\t职位名称\t部门名称\t职位描述";
            strSql = "select a.*, b.FDeptName from Tb_Sys_Position a ";
            strSql = strSql +
                " left join Tb_Sys_Department b on a.fdeptcode=b.fdeptcode ";
            strSql = strSql + buildFilterSql() + " order by a.FPositionCode,a.FDeptCode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FPositionCode"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FPositionName"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FDeptName"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FDesc"));
                bufShow.append(YssCons.YSS_LINESPLITMARK);

                this.strPositionCode = rs.getString("FPositionCode");
                this.strPositionName = rs.getString("FPositionName");
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\fFPositionCode\tFPositionName\tFDeptName\tFDesc";

        } catch (Exception se) {
            throw new YssException("获取职位信息出错！", se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 返回职位信息
     * @param strDeptCode String
     * @throws YssException
     * @return String
     */
    public String getListViewData2(String sDeptCode) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "职位代码\t职位名称\t部门名称";
            strSql = " select a.*, b.FDeptName from Tb_Sys_Position a ";
            strSql = strSql +
                " left join Tb_Sys_Department b on a.fdeptcode=b.fdeptcode ";
            if (sDeptCode != null && sDeptCode.trim().length() != 0) {
                strSql = strSql + " where a.FDeptCode='" + sDeptCode.trim() + "'";
            }
            strSql = strSql + " order by a.FPositionCode,a.FDeptCode ";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                bufShow.append(rs.getString("FPositionCode"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FPositionName"));
                bufShow.append("\t");
                bufShow.append(rs.getString("FDeptName"));
                bufShow.append(YssCons.YSS_LINESPLITMARK);

                this.strPositionCode = rs.getString("FPositionCode");
                this.strPositionName = rs.getString("FPositionName");
                this.strDeptCode = rs.getString("FDeptCode") + "";
                this.strDeptName = rs.getString("FDeptName") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.checkStateId = 1;

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
            throw new YssException("获取可用职位信息出错！", se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            if (btOper == YssCons.OP_ADD) {
                strSql = "insert into Tb_Sys_Position(FPositionCode,FPositionName,FDeptCode,FDesc)" +
                    " values(" + dbl.sqlString(this.strPositionCode) + "," +
                    dbl.sqlString(this.strPositionName) + "," +
                    dbl.sqlString(this.strDeptCode) + "," +
                    dbl.sqlString(this.strDesc) + ")";
            } else if (btOper == YssCons.OP_EDIT) {
                strSql = "update Tb_Sys_Position set FPositionCode = " +
                    dbl.sqlString(this.strPositionCode) + ", FPositionName = " +
                    dbl.sqlString(this.strPositionName) + ", FDeptCode=" +
                    dbl.sqlString(this.strDeptCode) + ", FDesc = " +
                    dbl.sqlString(this.strDesc) +
                    " where FPositionCode = " +
                    dbl.sqlString(this.strOldPositionCode);
            } else if (btOper == YssCons.OP_DEL) {
                strSql = "delete from Tb_Sys_Position " +
                    "where FPositionCode = " + dbl.sqlString(this.strPositionCode);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存职位设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
