package com.yss.dbupdate.autoupdatetables.standardframebuild;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

class ExportTableFrameData
    extends BaseBean {
    ExportTableFrameData() {
    }

    //接收表名,用于sql语句中的 not in
    public final String TABLENAME_NOEXPORT =
        "TB_BLOOMBERG_DATA,TB_BROKER_DATA,TB_FUN_COLUMNS,TB_FUN_VERSION,TB_HBBROKER_DATA,TB_FUN_CONSCOLS,TB_FUN_ALLTABLENAME,TB_FUN_CONSTRAINTS,TB_DATA_RATETRADE,TB_DATA_OUTSTANDING";

    public String exportData() throws YssException {
        //写死表前缀
        pub.setPrefixTB("001");
        return getAllDatas();
    }

    public String getAllDatas() throws YssException {
        String sqlStr = "";
        StringBuffer bufRes = new StringBuffer(600000);
        StringBuffer bufTable = new StringBuffer(20000);
        StringBuffer bufCons = new StringBuffer(30000);
        StringBuffer bufConCols = new StringBuffer(60000);
        StringBuffer bufCols = new StringBuffer(500000);
        ResultSet rs = null;
        try {
            //------------用户下的所有表
            sqlStr = "SELECT table_name, tablespace_name" +
                " FROM User_All_Tables" +
                " WHERE Table_Name LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND Table_Name NOT LIKE '%/_TEMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_TMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_BAK'ESCAPE'/'" +
                " AND TABLE_Name NOT IN (" + operSql.sqlCodes(TABLENAME_NOEXPORT) +
                ")" + //排除不需要的表
                " ORDER BY table_name";
            rs = dbl.openResultSet_antReadonly(sqlStr);
            bufTable.append("<USER_ALL_TABLES>");
            while (rs.next()) {
                String sTableName = rs.getString("table_name");
                String sTableSpace = rs.getString("tablespace_name");
                if (!Character.isDigit(sTableName.charAt(sTableName.length() - 1)) &&
                    !Character.isDigit(sTableName.charAt(sTableName.length() - 2))) {
                    String strTableName = "";
                    strTableName = pub.yssGetUnPrefixTableName(sTableName); //获取不带组合群代码的表名         20081117 王晓光
                    if (!strTableName.trim().equals("")) {
                        bufTable.append("<ROWS TABLE_NAME=\"").append(
                            strTableName).append(
                                "\" TABLESPACE_NAME=\"").append(sTableSpace).
                            append(
                                "\"/>");
                    }
                }
            }
            bufTable.append("</USER_ALL_TABLES>");
            dbl.closeResultSetFinal(rs);
            //------------表级约束
            sqlStr = "SELECT Constraint_Name, Table_name, Constraint_Type" +
                " FROM USER_CONSTRAINTS" +
                " WHERE Constraint_Type = 'P'" +
                " AND Table_Name LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND Table_Name NOT LIKE '%/_TEMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_TMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_BAK'ESCAPE'/'" +
                " AND TABLE_Name NOT IN (" + operSql.sqlCodes(TABLENAME_NOEXPORT) +
                ")" + //排除不需要的表
                " ORDER BY table_name";
            rs = dbl.openResultSet_antReadonly(sqlStr);
            bufCons.append("<USER_CONSTRAINTS>");
            while (rs.next()) {
                String sConName = rs.getString("Constraint_Name");
                String sTableName = rs.getString("Table_name");
                String sConType = rs.getString("Constraint_Type");
                if (!Character.isDigit(sTableName.charAt(sTableName.length() - 1)) &&
                    !Character.isDigit(sTableName.charAt(sTableName.length() - 2))) {
                    String strTableName = "";
                    strTableName = pub.yssGetUnPrefixTableName(sTableName); //获取不带组合群代码的表名         20081117 王晓光
                    if (!strTableName.trim().equals("")) {
                        bufCons.append("<ROWS CONSTRAINT_NAME=\"").append(pub.
                            yssGetUnPrefixPKName(sConName)).append( //获取不带组合群代码的主键名         20081117 王晓光
                                "\" TABLE_NAME=\"").append(strTableName).append(
                                    "\" CONSTRAINT_TYPE=\"").append(sConType).
                            append("\"/>");
                    }
                }
            }
            dbl.closeResultSetFinal(rs);
            bufCons.append("</USER_CONSTRAINTS>");
            //-------------表的主键约束
            sqlStr =
                "select a.Constraint_Name, a.Table_Name, a.Column_Name, a.Position" +
                " FROM USER_CONS_COLUMNS a" +
                " JOIN (SELECT Constraint_Name" +
                " FROM USER_CONSTRAINTS" +
                " WHERE Constraint_Type = 'P'" +
                " AND Table_Name LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND Table_Name NOT LIKE '%/_TEMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_TMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_BAK'ESCAPE'/'" +
                " AND TABLE_Name NOT IN (" + operSql.sqlCodes(TABLENAME_NOEXPORT) +
                ")" + //排除不需要的表
                ") b ON a.Constraint_Name = b.Constraint_Name" +
                " ORDER BY Table_Name";
            rs = dbl.openResultSet_antReadonly(sqlStr);
            bufConCols.append("<USER_CONS_COLUMNS>");
            while (rs.next()) {
                String sConName = rs.getString("Constraint_Name");
                String sTableName = rs.getString("Table_Name");
                String sColName = rs.getString("Column_Name");
                String sPosition = rs.getString("Position");
                if (!Character.isDigit(sTableName.charAt(sTableName.length() - 1)) &&
                    !Character.isDigit(sTableName.charAt(sTableName.length() - 2))) {
                    String strTableName = "";
                    strTableName = pub.yssGetUnPrefixTableName(sTableName); //获取不带组合群代码的表名         20081117 王晓光
                    if (!strTableName.trim().equals("")) {
                        bufConCols.append("<ROWS CONSTRAINT_NAME=\"").append(pub.
                            yssGetUnPrefixPKName(sConName)). //获取不带组合群代码的主键名         20081117 王晓光
                            append("\" TABLE_NAME=\"").append(strTableName).append(
                                "\" COLUMN_NAME=\"").append(sColName).append(
                                    "\" POSITION=\"").append(sPosition).append(
                                        "\"/>");
                    }
                }
            }
            dbl.closeResultSetFinal(rs);
            bufConCols.append("</USER_CONS_COLUMNS>");
            //-------------表的字段信息
            sqlStr = "SELECT table_name, Column_name, Column_Id, Data_Type, Data_Length, Data_Precision, Data_Scale, Nullable, DATA_DEFAULT" +
                " FROM user_tab_columns" +
                " WHERE Table_Name LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND Table_Name NOT LIKE '%/_TEMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_TMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_BAK'ESCAPE'/'" +
                " AND TABLE_Name NOT IN (" + operSql.sqlCodes(TABLENAME_NOEXPORT) + ")" +
                " ORDER BY TABLE_Name, Column_Id";
            rs = dbl.openResultSet_antReadonly(sqlStr);
            bufCols.append("<USER_TAB_COLUMNS>");
            while (rs.next()) {
                String sTableName = rs.getString("table_name");
                if (!Character.isDigit(sTableName.charAt(sTableName.length() - 1)) &&
                    !Character.isDigit(sTableName.charAt(sTableName.length() - 2))) {
                    String strTableName = "";
                    strTableName = pub.yssGetUnPrefixTableName(sTableName); //获取不带组合群代码的表名         20081117 王晓光
                    if (!strTableName.trim().equals("")) {
                        bufCols.append("<ROWS TABLE_NAME=\"").append(strTableName).
                            append("\" COLUMN_NAME=\"").append(rs.getString(
                                "Column_name")).append("\" COLUMN_ID=\"").append(
                                    rs.
                                    getString("Column_Id")).
                            append("\" DATA_TYPE=\"").append(rs.getString(
                                "Data_Type")).
                            append("\" DATA_LENGTH=\"").append(rs.getString(
                                "Data_Length")).append("\" DATA_PRECISION=\"").
                            append(
                                rs.
                                getString("Data_Precision") == null ? "" : rs.getString("Data_Precision")).
                            append("\" DATA_SCALE=\"").
                            append(rs.getString("Data_Scale") == null ? "" : rs.getString("Data_Scale")).
                            append("\" NULLABLE=\"").append(rs.getString("Nullable")).
                            append("\" DATA_DEFAULT=\"").append(
                                rs.getString("DATA_DEFAULT") == null ? "" : rs.getString("DATA_DEFAULT")).append("\"/>");
                    }
                }
            }
            bufCols.append("</USER_TAB_COLUMNS>");
            dbl.closeResultSetFinal(rs);
            bufRes.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            bufRes.append("<ysstech>");
            bufRes.append(bufTable).append(bufCons).append(bufConCols).append(
                bufCols);
            bufRes.append("</ysstech>");
        } catch (Exception e) {
            throw new YssException("获取所有表的结构数据信息出错！\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        System.out.println(bufRes.toString());
        return bufRes.toString();
    }

}
