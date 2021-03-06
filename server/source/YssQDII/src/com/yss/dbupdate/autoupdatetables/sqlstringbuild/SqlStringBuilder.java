package com.yss.dbupdate.autoupdatetables.sqlstringbuild;

import java.util.*;

import com.yss.dbupdate.autoupdatetables.entitycreator.pojo.*;
import com.yss.dbupdate.autoupdatetables.sqlstringbuild.sqlhelper.*;
import com.yss.dsub.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 生成动态 SQL</p>
 *
 * <p>Description: 以 Oracle 为标准创建，不同 DBMS 通过 ISqlStringConvert 转换语句实现</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SqlStringBuilder
    extends BaseBean {
    /**
     *转换生成的 SQL
     */
    private ISqlStringConvert sqlConvert;
    /**
     * 标志 DBMS 类型
     */
    private int dbType;

    public SqlStringBuilder(int dbType) {
        this.dbType = dbType;
        if (dbType == YssCons.DB_DB2) {
            //SQL 是以 Oracle 标准生成的 DB2 才需要转换
            sqlConvert = new CvtOraToDb2();
        } else {
            sqlConvert = null;
        }
    }

    public SqlStringBuilder(ISqlStringConvert sqlConvert) {
        this.sqlConvert = sqlConvert;
    }

    /**
     * 转换 SQL
     * @param sql String
     * @return String
     * @throws YssException
     */
    private String sqlConvert(String sql) throws YssException {
        try {
            //通过本类构造方法的约束，sqlConvert == null 只存在于一种情况：连接的 DBMS 为 Oracle
            if (sqlConvert == null) {
                return sql;
            } else {
                return sqlConvert.sqlConvert(sql);
            }
        } catch (Exception e) {
            throw new YssException("转换 SQL 语句出错！\r\n", e);
        }
    }

    /**
     * 获取标准自定义字典表的删除语句
     * @return ArrayList
     * @throws YssException
     */
    public ArrayList getDropMyFrameDataTableStr() throws YssException {
        ArrayList alDropSql = new ArrayList();
        String sql1 = "";
        String sql2 = "";
        String sql3 = "";
        String sql4 = "";
        try {
        	/**shashijie 2011-10-21 STORY 1698 */
            if (dbl.yssTableExist("TB_FUN_AllTableName")) {
                sql1 = dbl.doOperSqlDrop("DROP TABLE TB_FUN_AllTableName");
                alDropSql.add(sql1);
            }
            if (dbl.yssTableExist("TB_Fun_Columns")) {
                sql2 = dbl.doOperSqlDrop("DROP TABLE TB_Fun_Columns");
                alDropSql.add(sql2);
            }
            if (dbl.yssTableExist("TB_FUN_ConsCols")) {
                sql3 = dbl.doOperSqlDrop("DROP TABLE TB_FUN_ConsCols");
                alDropSql.add(sql3);
            }
            if (dbl.yssTableExist("TB_Fun_CONSTRAINTS")) {
                sql4 = dbl.doOperSqlDrop("DROP TABLE TB_Fun_CONSTRAINTS");
                alDropSql.add(sql4);
            }
            /**end*/
        } catch (Exception e) {
            throw new YssException("获取删除自定义数据字典表的 SQL 语句出错！\r\n", e);
        }
        return alDropSql;
    }

    /**
     * 标准自定义字典表的创建语句
     * @return ArrayList
     * @throws YssException
     */
    public ArrayList getCreateMyFrameDataTableStr() throws YssException {
        ArrayList alCreateSql = new ArrayList();
        String sql1 = "";
        String sql2 = "";
        String sql3 = "";
        String sql4 = "";
        try {
            sql1 = "CREATE TABLE TB_FUN_AllTableName(" +
                "    FTableName         VARCHAR2(100)    NOT NULL," +
                "    FTableSpaceName    VARCHAR2(100)," +
                "    CONSTRAINT PK_Fun_AllTableName PRIMARY KEY (FTableName)" +
                ")";
            sql1 = sqlConvert(sql1);
            sql2 = "CREATE TABLE TB_Fun_Columns(" +
                "    FTableName        VARCHAR2(50)     NOT NULL," +
                "    FColumnName       VARCHAR2(50)     NOT NULL," +
                "    FColumnId         NUMBER(10, 0)    NOT NULL," +
                "    FDataType         VARCHAR2(50)     NOT NULL," +
                "    FDataLength       NUMBER(10, 0)    NOT NULL," +
                "    FDataPrecision    NUMBER(10, 0)," +
                "    FDataScale        NUMBER(10, 0)," +
                "    FNullAble         VARCHAR2(1)     NOT NULL," +
                "    FDataDefault      VARCHAR2(2000)," +
                "    FINSERTSCRIPT     VARCHAR2(4000)," +
                "    CONSTRAINT PK_TB_Fun_Columns PRIMARY KEY (FTableName, FColumnName, FColumnId)" +
                ")";
            sql2 = sqlConvert(sql2);
            sql3 = "CREATE TABLE TB_FUN_ConsCols(" +
                "    FConstraintName    VARCHAR2(50)     NOT NULL," +
                "    FTableName         VARCHAR2(50)     NOT NULL," +
                "    FColumnName        VARCHAR2(50)     NOT NULL," +
                "    FPosition          NUMBER(10, 0)    NOT NULL," +
                "    CONSTRAINT PK_TB_FUN_ConsCols PRIMARY KEY (FConstraintName, FTableName, FColumnName)" +
                ")";
            sql3 = sqlConvert(sql3);
            sql4 = "CREATE TABLE TB_Fun_CONSTRAINTS(" +
                "    FConstraintName    VARCHAR2(50)    NOT NULL," +
                "    FTableName         VARCHAR2(50)    NOT NULL," +
                "    FConType           VARCHAR2(1)     NOT NULL," +
                "    CONSTRAINT PK_TB_Fun_CONSTRAINTS PRIMARY KEY (FConstraintName, FTableName)" +
                ")";
            sql4 = sqlConvert(sql4);
            alCreateSql.add(sql1);
            alCreateSql.add(sql2);
            alCreateSql.add(sql3);
            alCreateSql.add(sql4);
        } catch (Exception e) {
            throw new YssException("获取创建自定义数据字典表的 SQL 语句出错！\r\n", e);
        }
        return alCreateSql;
    }

    /**
     * 获取更改表名的 SQL 语句
     * @param sSouName String：原来的表名
     * @param sTagName String：更改后的表名
     * @return String：SQL
     * @throws YssException
     */
    public String getRenameStr(String sSouName, String sTagName) throws YssException {
        String sql;
        if (this.dbType == YssCons.DB_ORA) {
            sql = "ALTER TABLE " + sSouName + " RENAME TO " + sTagName;
        } else {
            sql = "RENAME TABLE " + sSouName + " TO " + sTagName;
        }
        return sql;
    }

    /**
     * 获取删除主键的 SQL 语句
     * @param table TableBean：主要删除主键的表的实体
     * @return String：SQL
     * @throws YssException
     */
    public String getDropConsStr(TableBean table) throws YssException {
        String sql = "";
        if (table == null || table.getPkCons() == null ||
            table.getPkCons().getFCONSTRAINTNAME().trim().length() == 0) {
            return "";
        }
        sql = "ALTER TABLE " + table.getFTableName() + " DROP CONSTRAINT " + table.getPkCons().getFCONSTRAINTNAME();
        //add by xuqiji 20090519 MS00352    新建组合群时能够自动创建对应的一套表
        if (dbl.getDBType() == YssCons.DB_ORA) {
            sql += " CASCADE ";
        }
        //-------------------------end--------------------------------------//
        return sql;
    }

    public String getCreateTableStr(TableBean table) throws YssException {
        return getCreateTableStr(table, "");
    }

    /**
     * 获取创建表的 SQL 语句
     * @param table TableBean
     * @return String
     * @throws YssException
     */
    public String getCreateTableStr(TableBean table, String sTabName) throws YssException {
        String sql = "";
        if (sTabName.trim().length() == 0) {
            sTabName = table.getFTableName();
        }
        ArrayList alCols = table.getColumns();
        sql = "CREATE TABLE " + sTabName.toUpperCase() + "(";
        for (int i = 0; i < alCols.size(); i++) {
            sql += getTableColStr( (ColumnsBean) alCols.get(i));
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        return this.sqlConvert(sql);
    }

    /**
     * 获取创建表的每一行的 SQL 语句
     * @param col ColumnsBean
     * @return String
     * @throws YssException
     */
    private String getTableColStr(ColumnsBean col) throws YssException {
        String sql = col.getFCLOUMNNAME();
        //判断数据类型
        if (col.getFDATATYPE().equalsIgnoreCase("varchar2") ||
            col.getFDATATYPE().equalsIgnoreCase("varchar") ||
            col.getFDATATYPE().equalsIgnoreCase("char")) {
            sql += " " + col.getFDATATYPE() + "(" + col.getFDATALENGTH() + ")";
        } else if (col.getFDATATYPE().equalsIgnoreCase("number") ||
                   col.getFDATATYPE().equalsIgnoreCase("DECIMAL")) {
            if (col.getFDATAPRECISION().trim().length() == 0) {
                sql += " INTEGER";
            } else {
                sql += " " + col.getFDATATYPE() + "(" + col.getFDATAPRECISION() +
                    "," + col.getFDATASCALE() + ")";
            }
        }
        //数据库更新，对于“RAW”类型的判断 by xuqiji 2009 0413 MS00352    新建组合群时能够自动创建对应的一套表    --------------------------
        else if (col.getFDATATYPE().equalsIgnoreCase("RAW")) {
            sql += " " + col.getFDATATYPE() + "(" + col.getFDATALENGTH() + ")";
        }
        //----------------MS00352    新建组合群时能够自动创建对应的一套表 by xuqiji 20090413----------------------------------------------
        else {
            sql += " " + col.getFDATATYPE();
        }
        //判断默认值
        if (col.getFDEFULTVALUE() != null && col.getFDEFULTVALUE().trim().length() != 0) {
            sql += " DEFAULT " + col.getFDEFULTVALUE();
        }
        //判断是否可空
        if (col.getFNULLABLE().equalsIgnoreCase("y")) {
            sql += ",";
        } else {
            sql += " NOT NULL,";
        }
        return sql;
    }

    /**
     * 创建数据复制的 Insert 语句
     * @param nTable TableBean：新表的实体类
     * @param oTable TableBean：旧表的实体类
     * @param sTmpTabName String：临时表的表名
     * @return String
     * @throws YssException
     */
    public String getInsertStr(TableBean nTable, TableBean oTable, String sTmpTabName, String mark) throws YssException { //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
        if (oTable == null) {
            return "";
        }

        String sql = "";
        String sInsert = "INSERT INTO " + (mark.equals("check") ? sTmpTabName : nTable.getFTableName()) + "("; //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表
        String sSelect = "SELECT ";
        //储存新表的所有列
        ArrayList alNCols = nTable.getColumns();
        //储存旧表的所有列
        ArrayList alOCols = oTable.getColumns();
        HashMap hmOCols = new HashMap();

        for (int i = 0; i < alOCols.size(); i++) {
            ColumnsBean col = (ColumnsBean) alOCols.get(i);
            hmOCols.put(col.getFCLOUMNNAME(), col);
        }

        for (int i = 0; i < alNCols.size(); i++) {
            ColumnsBean newCol = (ColumnsBean) alNCols.get(i);

            ColumnsBean oldCol = (ColumnsBean) hmOCols.get(newCol.getFCLOUMNNAME());
            if (oldCol != null) {
                sInsert += newCol.getFCLOUMNNAME() + ",";
                if (newCol.getFINSERTSCRIPT() != null &&
                    newCol.getFINSERTSCRIPT().trim().length() != 0) {
                    //xuqiji 20090511:QDV4赢时胜（上海）2009年4月7日01_A    MS00352  新建组合群时能够自动创建对应的一套表------------------------------------//
                    //对修改后的不为null的列赋值
                    sSelect += " CASE WHEN " + newCol.getFCLOUMNNAME() +
                        " IS NULL THEN " + newCol.getFINSERTSCRIPT() +
                        " ELSE " + newCol.getFCLOUMNNAME() + " END ,";
                    //-----------------------------end------------------------------------------------------------//
                } else {
                    sSelect += newCol.getFCLOUMNNAME() + ",";
                }
            } else {
                //判断自定义值 xuqiji 20090511:QDV4赢时胜（上海）2009年4月7日01_A    MS00352  新建组合群时能够自动创建对应的一套表---------------------
                if (newCol.getFINSERTSCRIPT() != null &&
                    newCol.getFINSERTSCRIPT().trim().length() != 0) {
                    sInsert += newCol.getFCLOUMNNAME() + ",";
                    if (newCol.getFINSERTSCRIPT() != null &&
                        newCol.getFINSERTSCRIPT().trim().length() != 0) {
                        sSelect += newCol.getFINSERTSCRIPT() + ",";
                    } else {
                        sSelect += newCol.getFDEFULTVALUE() + ",";
                    }
                }
                //-----------------------------end-------------------------------------------------------//
                //判断是否有默认值
                if (newCol.getFDEFULTVALUE() != null &&
                    newCol.getFDEFULTVALUE().trim().length() != 0) {
                    sInsert += newCol.getFCLOUMNNAME() + ",";
                    if (newCol.getFINSERTSCRIPT() != null &&
                        newCol.getFINSERTSCRIPT().trim().length() != 0) {
                        sSelect += newCol.getFINSERTSCRIPT() + ",";
                    } else {
                        sSelect += newCol.getFDEFULTVALUE() + ",";
                    }
                } else {
                    continue;
                }
            }
        }
        sInsert = sInsert.substring(0, sInsert.length() - 1);
        sSelect = sSelect.substring(0, sSelect.length() - 1);

        sInsert += ")";
        sSelect += " FROM " + (mark.equals("check") ? nTable.getFTableName() : sTmpTabName); //xuqiji 20090416 MS00352    新建组合群时能够自动创建对应的一套表

        sql = sInsert + sSelect;
        return sql;
    }

    /**
     * 生成创建主键的 SQL 语句
     * @param table TableBean：表结构实体类
     * @return String：SQL 语句
     * @throws YssException
     */
    public String getCreateTablePKStr(TableBean table) throws YssException {
        return getCreateTablePKStr(table, "", "");
    }

    /**
     * 生成创建主键的 SQL 语句
     * @param table TableBean：表结构实体类
     * @param sTabName String：SQL 语句中需要使用的表名
     * @param sPKName String：SQL 语句中的逐渐名
     * @return String： SQL 语句
     * @throws YssException
     */
    public String getCreateTablePKStr(TableBean table, String sTabName, String sPKName) throws YssException {
        if (sPKName.trim().length() == 0) {
            sPKName = table.getPkCons().getFCONSTRAINTNAME();
        }
        if (sTabName.trim().length() == 0) {
            sTabName = table.getFTableName();
        }
        if (null == table.getPkCons()) {
            return "";
        } else {
            ArrayList alConsCols = table.getPkCons().getConsCols();
            if (alConsCols.size() == 0) {
                return "";
            }
            ConsColsBean consCols = null;
            String sql = "ALTER TABLE " + sTabName + " ADD CONSTRAINT " + sPKName +
                " PRIMARY KEY (";
            for (int i = 0; i < alConsCols.size(); i++) {
                consCols = (ConsColsBean) alConsCols.get(i);
                sql += consCols.getFCOLUMNNAME() + ",";
            }
            sql = sql.substring(0, sql.length() - 1);
            sql += ")";

            sql = this.sqlConvert(sql);
            return sql;
        }
    }

    /**
     * 生成删除表的 SQl 语句
     * @param sTmpTabName String：需要删除的表名
     * @return String：SQL 语句
     * @throws YssException
     */
    public String getDropTableStr(String sTmpTabName) throws YssException {
        String sql = "DROP TABLE " + sTmpTabName;
        return sql;
    }

    /**
     * 生成删除索引的 SQL 语句
     * @param sIndexName String：需要删除的索引名
     * @return String：SQL 语句
     * @throws YssException
     */
    public String getDropTableIndex(String sIndexName) throws YssException {
        String sql = "DROP INDEX " + sIndexName;
        return sql;
    }
}
