package com.yss.dbupdate.autoupdatetables.tableframecompare;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 创建临时字典表</p>
 *
 * <p>Description: 通过被更新的数据库的字典表，创建临时字典表。因为不同 DBMS 的字典表表结构都不一样，所以使用本类产生统一的字典表，
 * 可避免在比较表结构时，需要为不同的 DBMS 写不同的 SQL 语句。现在一共产生四张表：tb_tmp_Tables，表名；tb_tmp_Columns 列数据；tb_tmp_Const 约束数据；
 * tb_tmp_ConstCols 约束列。现在将创建 Oracle 的字典表和 DB2 的字典标都在本类中实现，如果需要支持更多的数据库，可以将此类分开，避免一个类中的代码过于庞大</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TmpTabCreate
    extends BaseBean {
    public TmpTabCreate() {
    }

    public void CreateTmpDictTables() throws YssException {
        try {
            //如果这些表已存在，先删掉
            if (dbl.yssTableExist("tb_tmp_Tables")) {
            	/**shashijie 2011-10-20 STORY 1698 */
                dbl.executeSql(dbl.doOperSqlDrop("drop table tb_tmp_Tables"));
                /**end*/
            }
            if (dbl.yssTableExist("tb_tmp_Columns")) {
            	/**shashijie 2011-10-20 STORY 1698 */
                dbl.executeSql(dbl.doOperSqlDrop("drop table tb_tmp_Columns"));
                /**end*/
            }
            if (dbl.yssTableExist("tb_tmp_Const")) {
            	/**shashijie 2011-10-20 STORY 1698 */
                dbl.executeSql(dbl.doOperSqlDrop("drop table tb_tmp_Const"));
                /**end*/
            }
            if (dbl.yssTableExist("tb_tmp_ConstCols")) {
            	/**shashijie 2011-10-20 STORY 1698 */
                dbl.executeSql(dbl.doOperSqlDrop("drop table tb_tmp_ConstCols"));
                /**end*/
            }

            if (dbl.dbType == YssCons.DB_ORA) {
                CreateOracleTmpTables();
            } else if (dbl.dbType == YssCons.DB_DB2) {
                CreateDB2TmpTables();
            } else {
                throw new YssException("暂不支持此数据库的更新！");
            }
        } catch (Exception e) {
            throw new YssException("创建临时字典表出错！\n", e);
        }
    }

    /**
     * 创建 DB2 的临时字典表
     * @throws YssException
     */
    private void CreateDB2TmpTables() throws YssException {
        String selectStr = "";
        String insertStr = "";
        String sqlStr = "";
        try {
            //表空间先不要，现在也没什么用
            //倒入的时候要剔除掉不需要的表，系统中的表都是以TB_开头的，而且表名中至少出现两个_
            selectStr = "SELECT TabName AS Table_Name, ' ' AS TableSpace_Name" +
                " FROM SYSCAT.TABLES" +
                " WHERE TabName LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND TabName NOT LIKE '%/_TEMP/_%'ESCAPE'/'" +
                " AND TabName NOT LIKE '%/_TMP/_%'ESCAPE'/'" +
                " AND TabName NOT LIKE '%/_BAK'ESCAPE'/'" +
                " AND (TabName <> 'TB_FUN_COLUMNS'" +
                " AND TabName <> 'TB_FUN_CONSCOLS'" +
                " AND TabName <> 'TB_FUN_ALLTABLENAME'" +
                " AND TabName <> 'TB_FUN_CONSTRAINTS')";
            sqlStr = "CREATE TABLE tb_tmp_Tables  AS (" + selectStr + ") Definition only";
            insertStr = "INSERT INTO tb_tmp_Tables (" + selectStr + ")";
            dbl.executeSql(sqlStr);
            dbl.executeSql(insertStr);

            selectStr = "SELECT ConstName AS Constraint_Name, TabName AS Table_Name, Type AS Constraint_Type" +
                " FROM SYSCAT.TABCONST" +
                " WHERE Type = 'P'" +
                " AND TabName LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND TabName NOT LIKE '%/_TEMP/_%' ESCAPE'/'" +
                " AND TabName NOT LIKE '%/_TMP/_%' ESCAPE'/'" +
                " AND TabName NOT LIKE '%/_BAK' ESCAPE'/'" +
                " AND (TabName <> 'TB_FUN_COLUMNS' AND TabName <> 'TB_FUN_CONSCOLS' AND" +
                " TabName <> 'TB_FUN_ALLTABLENAME' AND" +
                " TabName <> 'TB_FUN_CONSTRAINTS')";
            sqlStr = "CREATE TABLE tb_tmp_Const AS (" + selectStr + ") Definition only";
            insertStr = "INSERT INTO tb_tmp_Const (" + selectStr + ")";
            dbl.executeSql(sqlStr);
            dbl.executeSql(insertStr);

            //DB2 导入时将 DATE 型的长度设置为7，DECIMAL 型的长度设置为22，CLOB 的长度设置为4000，方便比对，这些都是 Oracle 字典表中的标准
            selectStr = "select TabName AS Table_Name, ColName AS Column_Name, ColNo AS Column_Id," +
                " TypeName AS Data_Type," +
                " CASE" +
                " WHEN TypeName = 'VARCHAR' THEN Length" +
                " WHEN TypeName = 'DATE' THEN 7" +
                " WHEN TypeName = 'DECIMAL' THEN 22" +
                " WHEN TypeName = 'CLOB' THEN 4000" +
                " ELSE Length" +
                " END AS Data_Length," +
                " CASE" +
                " WHEN TypeName = 'DECIMAL' THEN" +
                " Length" +
                " ELSE NULL" +
                " END AS Data_Precision," +
                " CASE" +
                " WHEN TypeName = 'DECIMAL' THEN Scale" +
                " ELSE NULL" +
                " END AS Data_Scale, Nulls AS Nullable," +
                " DEFAULT AS DATA_DEFAULT" +
                " FROM SYSCAT.COLUMNS" +
                " WHERE TabName LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND TabName NOT LIKE '%/_TEMP/_%' ESCAPE'/'" +
                " AND TabName NOT LIKE '%/_TMP/_%' ESCAPE'/'" +
                " AND TabName NOT LIKE '%_BAK' ESCAPE'/'" +
                " AND TabName <> 'TB_FUN_COLUMNS'" +
                " AND TabName <> 'TB_FUN_CONSCOLS'" +
                " AND TabName <> 'TB_FUN_ALLTABLENAME'" +
                " AND TabName <> 'TB_FUN_CONSTRAINTS'";
            sqlStr = "CREATE TABLE tb_tmp_Columns AS (" + selectStr + ")DEFINITION ONLY";
            insertStr = "INSERT INTO tb_tmp_Columns (" + selectStr + ")";
            dbl.executeSql(sqlStr);
            dbl.executeSql(insertStr);

            //比较约束列的时候要关联以下约束表，取约束类型为“P”的，也就是主键
            selectStr = "SELECT b.CONSTNAME AS constraint_name, b.TABNAME AS Table_Name, b.COLNAME AS Column_Name, b.COLSEQ AS Position" +
                " FROM SYSCAT.TABCONST a JOIN SYSCAT.KEYCOLUSE b" +
                " ON a.CONSTNAME = b.CONSTNAME" +
                " AND a.TABNAME = b.TABNAME" +
                " WHERE a.TabName LIKE 'TB/_%/_%'ESCAPE'/' AND a.TabName" +
                " NOT LIKE '%/_TEMP/_%' ESCAPE'/' AND a.TabName" +
                " NOT LIKE '%/_TMP/_%' ESCAPE'/' AND a.TabName" +
                " NOT LIKE '%/_BAK' ESCAPE'/'" +
                " AND a.TabName <> 'TB_FUN_COLUMNS'" +
                " AND a.TabName <> 'TB_FUN_CONSCOLS'" +
                " AND a.TabName <> 'TB_FUN_ALLTABLENAME'" +
                " AND a.TabName <> 'TB_FUN_CONSTRAINTS'" +
                " AND a.TYPE = 'P'";
            sqlStr = "CREATE TABLE tb_tmp_ConstCols AS (" + selectStr + ")DEFINITION ONLY";
            insertStr = "INSERT INTO tb_tmp_ConstCols (" + selectStr + ")";
            dbl.executeSql(sqlStr);
            dbl.executeSql(insertStr);
        } catch (Exception e) {
            throw new YssException("创建 DB2 的临时字典表出错！\r\n", e);
        }
    }

    /**
     * 创建 Oracle 的临时字典表
     * @throws YssException
     */
    private void CreateOracleTmpTables() throws YssException {
        String sqlStr = "";
        try {
            //没什么好说的，把不需要的表剔除掉直接创建就可以了
            sqlStr = "CREATE TABLE tb_tmp_Tables AS " +
                " (SELECT Table_Name, TableSpace_Name" +
                " FROM USER_ALL_TABLES" +
                " WHERE Table_Name LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND Table_Name NOT LIKE '%/_TEMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_TMP/_%'ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_BAK'ESCAPE'/'" +
                " AND (TABLE_Name <> 'TB_FUN_COLUMNS'" +
                " AND TABLE_Name <> 'TB_FUN_CONSCOLS'" +
                " AND TABLE_Name <> 'TB_FUN_ALLTABLENAME'" +
                " AND TABLE_Name <> 'TB_FUN_CONSTRAINTS'))";
            dbl.executeSql(sqlStr);

            sqlStr = "CREATE TABLE tb_tmp_Const AS" +
                " (SELECT Constraint_Name, Table_Name, Constraint_Type" +
                " FROM USER_CONSTRAINTS" +
                " WHERE Constraint_Type = 'P'" +
                " AND Table_Name LIKE 'TB/_%/_%'ESCAPE'/'" +
                " AND Table_Name NOT LIKE '%/_TEMP/_%' ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_TMP/_%' ESCAPE'/'" +
                " AND TABLE_Name NOT LIKE '%/_BAK' ESCAPE'/'" +
                " AND (TABLE_Name <> 'TB_FUN_COLUMNS' AND TABLE_Name <> 'TB_FUN_CONSCOLS' AND" +
                " TABLE_Name <> 'TB_FUN_ALLTABLENAME' AND" +
                " TABLE_Name <> 'TB_FUN_CONSTRAINTS'))";
            dbl.executeSql(sqlStr);

            sqlStr = "Create table tb_tmp_ConstCols as (select a.constraint_name,a.Table_Name, a.Column_Name, a.Position from USER_CONS_COLUMNS a JOIN " +
                "(SELECT Constraint_Name FROM USER_CONSTRAINTS WHERE Constraint_Type = 'P' AND Table_Name LIKE 'TB/_%/_%'ESCAPE'/'" +
                "AND Table_Name NOT LIKE '%/_TEMP/_%'ESCAPE'/' AND TABLE_Name NOT LIKE '%/_TMP/_%'ESCAPE'/' AND TABLE_Name NOT LIKE '%/_BAK'ESCAPE'/' " +
                " AND TABLE_Name <> 'TB_FUN_COLUMNS' " +
                " AND TABLE_Name <> 'TB_FUN_CONSCOLS' " +
                " AND TABLE_Name <> 'TB_FUN_ALLTABLENAME' " +
                " AND TABLE_Name <> 'TB_FUN_CONSTRAINTS' " +
                ") b ON a.Constraint_Name = b.Constraint_Name)";
            dbl.executeSql(sqlStr);

            sqlStr = "create table tb_tmp_Columns as (select Table_Name, Column_Name, Column_Id, Data_Type, Data_Length, Data_Precision, Data_Scale, Nullable, " +
                "to_lob(DATA_DEFAULT) AS DATA_DEFAULT from User_Tab_Columns WHERE Table_Name LIKE 'TB/_%/_%'ESCAPE'/' AND Table_Name NOT LIKE '%/_TEMP/_%'ESCAPE'/' " +
                "AND TABLE_Name NOT LIKE '%/_TMP/_%'ESCAPE'/' AND TABLE_Name NOT LIKE '%_BAK'ESCAPE'/' " +
                " AND TABLE_Name <> 'TB_FUN_COLUMNS' " +
                " AND TABLE_Name <> 'TB_FUN_CONSCOLS' " +
                " AND TABLE_Name <> 'TB_FUN_ALLTABLENAME' " +
                " AND TABLE_Name <> 'TB_FUN_CONSTRAINTS' " +
                ")";
            dbl.executeSql(sqlStr);

        } catch (Exception e) {
            throw new YssException("创建 Oracle 的临时字典表出错！\r\n", e);
        }
    }
}
