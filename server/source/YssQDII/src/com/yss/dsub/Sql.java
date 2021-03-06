package com.yss.dsub;

import java.sql.*;

import com.yss.util.*;

public class Sql
    extends BaseBean {
    private DbBase dbl = null; //DbBase类实例
    public Sql() {
    }

    public Sql(DbBase db) {
        dbl = db;
    }

    /**
     * 通过表名删除表
     * @param sName String
     * @throws YssException
     * @return boolean
     */
    public boolean OraRemoveTable(String sName) throws YssException {
        return RemoveOrExist(sName, true);
    }

    /**
     * 看表是否存在
     * @param sName String
     * @throws YssException
     * @return boolean
     */
    public boolean OraTableExist(String sName) throws YssException {
        return RemoveOrExist(sName, false);
    }

    /**
     * 移除表或判断表是否存在
     * @param sName String
     * @param bRemove boolean true 为移除表
     * @throws YssException
     * @return boolean
     */

    private boolean RemoveOrExist(String sName, boolean bRemove) throws
        YssException {
        ResultSet Rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        boolean bState = false;
        try {
            if (dbl.yssTableExist(sName.toUpperCase())) {
                if (bRemove) {
                    conn.setAutoCommit(false);
                    bTrans = true;
                    dbl.executeSql("drop table " + sName);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
                bState = true;
            } else {
                if (bRemove) {
                    bState = true;
                }
            }
            return bState;
        } catch (SQLException sqle) {
            try {
                if (Rs != null) {
                    Rs.getStatement().close();
                }
            } catch (SQLException se) {}
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException se) {}
            throw new YssException("删除数据库表" + sName + "出错！", sqle);
        }
    }

    /**
     * 创建独立于组合的表
     * @throws YssException
     * @return boolean
     */
    public boolean CreateCommon() throws YssException {
        String[][] sSql = new String[16][2];
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        int j = 0;

        try {
            for (int i = 0; i < sSql.length; i++) {
                if (OraTableExist(sSql[i][0])) {
                    OraRemoveTable(sSql[i][0]);
                }
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql("Create Table " + sSql[i][0] + "(" + sSql[i][1] +
                               ")");
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);

            }
            return true;

        } catch (SQLException sqle) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException se) {}
            throw new YssException("创建系统表出错！", sqle);
        }
    }

    /**
     * 创建系统数据表格，根据传入的组合代码来建立
     * @param strPrefix String
     * @param bRollback boolean
     * @throws YssException
     * @return boolean
     */
    public boolean SqlCreateSetTables(String strPrefix, boolean bRollback) throws
        YssException {
        String sPrefix = "_" + strPrefix; //只有套帐号的或者年份的
        String[][] sSql = new String[81][2];
        boolean bVchExist = false, bTmp = false, bTmp1 = true;
        int iLoop;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        int j = 0;

        String z = "";
        try {
            conn.setAutoCommit(false);
            for (int i = 0; i < sSql.length; i++) {
                if (OraTableExist(sSql[i][0])) {
                    OraRemoveTable(sSql[i][0]);
                    bTmp1 = true;
                } else {
                    bTmp1 = true;
                }
                if (bTmp1) {
                    z = "Create Table " + sSql[i][0] + "(" + sSql[i][1] + ")";
                    dbl.executeSql("Create Table " + sSql[i][0] + "(" + sSql[i][1] +
                                   ")");
                }

            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (Exception sqle) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException se) {}
            throw new YssException("创建系统表出错！", sqle);
        }
    }

    /**
     * createGroupTables
     *
     * @param sPre String
     */
    public void createGroupTables(String sGroup) throws YssException {
    }

    /**
     * updateGroupOnly
     *
     * @param sPre String
     */
    public void updateGroupOnly(String sGroup) throws YssException {
    }

    /**
     * updateCommon
     */
    public void updateCommon() throws YssException {
    }

    /**
     * createCommonTables
     */
    public void createCommonTables() throws YssException {
    }

}
