package com.yss.tools;

import com.yss.util.YssException;
import com.yss.util.YssCons;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import java.sql.*;
import com.yss.dbupdate.BaseDbUpdate;

/**
 * <p>Title: </p>
 * <p>Description: 权限类型的导入导出</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @version 1.0
 */

public class RightTypeInOut
    extends BaseDataSettingBean {
    private String rightTypeCode = ""; //权限类型代码
    private String rightTypeName = ""; //权限类型名称
    private String funModuleName = ""; //模块名称
    private String type = ""; //类型
    private String FMenuBarCode = ""; //菜单条代码 by caocheng 2009.03.19 MS0001 QDV4.1

    public RightTypeInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");

        this.rightTypeCode = sRespAry[0];
        this.rightTypeName = sRespAry[1];
        this.funModuleName = sRespAry[2];
        this.type = sRespAry[3];
        //by caocheng 2009.03.19 MS0001 QDV4.1
        if (sRespAry.length > 4) {
            this.FMenuBarCode = sRespAry[4];
        } else {
            this.FMenuBarCode = "null";
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.rightTypeCode.trim()).append("\t");
        buf.append(this.rightTypeName.trim()).append("\t");
        buf.append(this.funModuleName.trim()).append("\t");
        buf.append(this.type.trim()).append("\t");
        buf.append(this.FMenuBarCode.trim()); //by caocheng 2009.03.19 MS0001 QDV4.1
        return buf.toString();
    }

    public String outRightType() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from Tb_Sys_RightType";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.rightTypeCode = rs.getString("FRightTypeCode") + "";
                this.rightTypeName = rs.getString("FRightTypeName") + "";
                this.funModuleName = rs.getString("FFunModuleName") + "";
                this.type = rs.getString("FType") + "";
                this.FMenuBarCode = rs.getString("FMenuBarCode") + ""; //by caocheng 2009.03.19 MS0001 QDV4.1
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出权限类型错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /***
     * 根据表来判断表的字段是否存在。 true:不存在 false:存在
     * sTabName :表名
     * cloumsn : 要查询的表字段 集,多个字段中间用"," 分隔
     * add by caocheng MS00001 QDV4.1赢时胜上海2009年2月1日01_A
     */
    protected boolean existsTabColumn_Ora(String sTabName, String columns) throws
        YssException {
        boolean existCol = true;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr =
                "select * from user_col_comments where upper(table_name)=upper(" +
                dbl.sqlString(sTabName) + ")" +
                " and upper(Column_Name) in (" +
                operSql.sqlCodes(columns.toUpperCase()) + ")";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                existCol = false;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询Oracle表" + sTabName + "的字段" + columns +
                                   "时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /***
     * 根据表来判断表的字段是否存在 true:不存在 false:存在
     * sTabName :表名
     * cloumsn : 要查询的表字段 集,多个字段中间用"," 分隔
     * add by caocheng MS00001 QDV4.1赢时胜上海2009年2月1日01_A
     */
    protected boolean existsTabColumn_DB2(String sTabName, String columns) throws YssException {
        boolean existCol = true;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from SYSIBM.COLUMNS_S where upper(table_name)=upper(" + dbl.sqlString(sTabName) + ")" +
                " and upper(Column_Name) in (" + operSql.sqlCodes(columns.toUpperCase()) + ")";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                existCol = false;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询DB2表" + sTabName + "的字段" + columns + "时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 导入系统权限类型
     * @param squest String
     * @throws YssException
     */
    public void inRightType(String squest) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        Connection conn = dbl.loadConnection();
        String[] sReqAry = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        boolean bTrans = false;
        boolean exist = false;
        try {
            sReqAry = squest.split(YssCons.YSS_LINESPLITMARK);
            conn.setAutoCommit(false);
            bTrans = true;
            //----------判断连接的数据库类型-------------//
            if (dbl.getDBType() == YssCons.DB_ORA) {
                exist = this.existsTabColumn_Ora("Tb_Sys_RightType",
                                                 "FMenuBarCode"); // by caocheng 2009.03.19 MS0001 QDV4.1 导入RightType之前先检查
            }
            if (dbl.getDBType() == YssCons.DB_DB2) {
                exist = this.existsTabColumn_DB2("Tb_Sys_RightType",
                                                 "FMenuBarCode");
            }

            //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A edit by songjie 2009-05-13----//
            if (dbl.getDBType() == YssCons.DB_ORA && exist) { //如果列不存在 加上这个列 用于添加FMenuBarCode到Tb_Sys_RightType中
                String sql =
                    "Alter Table Tb_Sys_RightType Add  FMenuBarCode varchar2(20)";
                dbl.executeSql(sql);
            } else if (dbl.getDBType() == YssCons.DB_DB2 && exist) { //若是DB2数据的导入
                String sql = " Alter Table Tb_Sys_RightType Add  FMenuBarCode varchar(20) "; //
                dbl.executeSql(sql);
            }
            //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A edit by songjie 2009-05-13----//

            strSql = "update Tb_Sys_RightType set FRightTypeCode = ?, FRightTypeName = ?, FFunModuleName = ?, FType = ?,FMenuBarCode=?" +
                " where FRightTypeCode = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            strSql = "insert into Tb_Sys_RightType(FRightTypeCode,FRightTypeName,FFunModuleName,FType,FMenuBarCode)" +
                " values (?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Sys_RightType where FRightTypeCode = " +
                    dbl.sqlString(this.rightTypeCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.rightTypeCode);
                    pstmt1.setString(2, this.rightTypeName);
                    pstmt1.setString(3, this.funModuleName);
                    pstmt1.setString(4, this.type);
                    pstmt1.setString(5, this.FMenuBarCode);
                    pstmt1.setString(6, this.rightTypeCode);
                    pstmt1.executeUpdate();
                } else {
                    pstmt2.setString(1, this.rightTypeCode);
                    pstmt2.setString(2, this.rightTypeName);
                    pstmt2.setString(3, this.funModuleName);
                    pstmt2.setString(4, this.type);
                    pstmt2.setString(5, this.FMenuBarCode);
                    pstmt2.executeUpdate();
                }
                dbl.closeResultSetFinal(rs);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入权限类型错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
