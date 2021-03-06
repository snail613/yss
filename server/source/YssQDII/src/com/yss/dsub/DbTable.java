package com.yss.dsub;

import com.yss.util.*;

import java.util.*;
import java.sql.*;

public class DbTable
    extends BaseBean {
    private String webRoot = "";
    public DbTable() {
    }

    public DbTable(YssPub pub) {
        setYssPub(pub);
    }
    
    /**
     * yeshenghong #1899 20111209
     * 创建套账涉及的建表(覆盖建套账前缀表、套账年份前缀表；不覆盖建年份相关表)
     * @param sPre String：年份套账前缀
     */
    public void createSet(String sPre) throws YssException { //建套账相关表
       if (dbl.dbType == YssCons.DB_ORA)
          new Ora(dbl).createSet(sPre);
//       else if (dbl.dbType == YssCons.DB_SQL)
//          new Sql(dbl).createSet(sPre);
//       else if (dbl.dbType == YssCons.DB_DB2)
//          new DB2(dbl).createSet(sPre);
    }
    //BUG5458 modified by yeshenghong 20120830
    public void dropSet(String sPre) throws YssException { //建套账相关表
        if (dbl.dbType == YssCons.DB_ORA)
           new Ora(dbl).dropSet(sPre);
//        else if (dbl.dbType == YssCons.DB_SQL)
//           new Sql(dbl).createSet(sPre);
//        else if (dbl.dbType == YssCons.DB_DB2)
//           new DB2(dbl).createSet(sPre);
     }

    public ArrayList loadTableName(String sFileName) throws YssException {
        ArrayList alTableName = null;
        String[] pa = null;
        try {
            pa = YssFun.loadTxtFile(webRoot + "/cfg/script/" +
                                    sFileName).split(YssCons.YSS_LINESPLITMARK);
            if (pa.length > 0) {
                alTableName = new ArrayList();
            }
            for (int i = 0; i < pa.length; i++) {
                if (!pa[i].trim().startsWith("*") && pa[i].trim().length() != 0) {
                    alTableName.add(pa[i]);
                }
            }
            return alTableName;
        } catch (YssException e) {
            throw new YssException("加载表名出错");
        }
    }

    public ArrayList loadCreateTableSql(String sFileName) throws YssException {
        ArrayList result = null;
        String[] pa = null;
        try {
            pa = YssFun.loadTxtFile(webRoot + "/cfg/script/" +
                                    sFileName).split(";");
            if (pa.length > 0) {
                result = new ArrayList();
            }
            for (int i = 0; i < pa.length; i++) {
                if (!pa[i].trim().startsWith("*") && pa[i].trim().length() != 0) {
                    result.add(pa[i]);
                }
            }
            return result;
        } catch (YssException e) {
            throw new YssException("加载建表语句出错");
        }
    }

    public void createTable() throws YssException {
        ArrayList alTableName = null;
        ArrayList alCreateTableSql = null;
        String strSql = "";
        String sTableName = "";
        try {
            alTableName = loadTableName(YssCons.Yss_TABLENAME_SCRIPT);
            alCreateTableSql = loadCreateTableSql(YssCons.
                                                  Yss_ORA_CREATETABLE_SCRIPT);
            for (int i = 0; i < alCreateTableSql.size(); i++) {
                strSql = (String) alCreateTableSql.get(i);
                sTableName = (String) alTableName.get(i);
                if (!dbl.yssTableExist(sTableName)) {
                    dbl.executeSql(strSql);
                }
            }

            alTableName = loadTableName(YssCons.Yss_TABLENAME_SCRIPT_PREFIX);
            alCreateTableSql = loadCreateTableSql(YssCons.
                                                  Yss_ORA_CREATETABLE_SCRIPT_PREFIX);
            for (int i = 0; i < alCreateTableSql.size(); i++) {
                strSql = (String) alCreateTableSql.get(i);
                sTableName = (String) alTableName.get(i);
                if (strSql.indexOf(sTableName) > 0) {
                	//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                	strSql = strSql.replaceFirst(sTableName,
                                        pub.getPrefixTB() + sTableName);
                	//---end---
                    if (!dbl.yssTableExist(pub.getPrefixTB() + sTableName)) {
                        dbl.executeSql(strSql);
                    }
                } else {
                    throw new YssException("\r\n没有找到【" + sTableName + "】对应的建表语句");
                }
            }

        } catch (Exception e) {
            throw new YssException("创建表出错" + e.getMessage());
        }
    }

    public void createTempTable() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            if (dbl.yssTableExist("Tb_Temp_SecurityBak_" +
                                  pub.getUserCode())) {
                return;
            }

            strSql = "create table Tb_Temp_SecurityBak_" + pub.getUserCode() + "(" +
                "FSecurityCode     VARCHAR2(20)     NOT NULL," +
                "FStartDate        DATE             NOT NULL," +
                "FSecurityName     VARCHAR2(50)     NOT NULL," +
                "FCatCode          VARCHAR2(20)     NOT NULL," +
                "FSubCatCode       VARCHAR2(20)," +
                "FCusCatCode       VARCHAR2(20)," +
                "FExchangeCode     VARCHAR2(20)     NOT NULL," +
                "FMarketCode       VARCHAR2(20)     NOT NULL," +
                "FExternalCode     VARCHAR2(20)," +
                "FTradeCury        VARCHAR2(20)     NOT NULL," +
                "FHolidaysCode     VARCHAR2(20)     NOT NULL," +
                "FSettleDayType    NUMBER(1, 0)     DEFAULT 0 NOT NULL," +
                "FSettleDays       NUMBER(38, 0)    NOT NULL," +
                "FSectorCode       VARCHAR2(20)," +
                "FTotalShare       NUMBER(18, 4)    NOT NULL," +
                "FCurrentShare     NUMBER(18, 4)    NOT NULL," +
                "FFactor           NUMBER(12, 6)    NOT NULL," +
                "FIssueCorpCode    VARCHAR2(20)," +
                "FDesc             VARCHAR2(100)," +
                "FCheckState       NUMBER(1, 0)     NOT NULL," +
                "FCreator          VARCHAR2(20)     NOT NULL," +
                "FCreateTime       VARCHAR2(20)     NOT NULL," +
                "FCheckUser        VARCHAR2(20)," +
                "FCheckTime        VARCHAR2(20)," +
                "CONSTRAINT PK_Temp_SecurityBak_" + pub.getUserCode() +
                " PRIMARY KEY (FSecurityCode, FStartDate))";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("创建临时表出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    //创建公用表
    public void createCommonTables() throws YssException {
        if (dbl.dbType == YssCons.DB_ORA) {
            new Ora(dbl).createCommonTables();
        } else if (dbl.dbType == YssCons.DB_SQL) {
            new Sql(dbl).createCommonTables();
        } else if (dbl.dbType == YssCons.DB_DB2) {
            new DB2(dbl).createCommonTables();
        }
    }

    /**
     * 创建套账涉及的建表(覆盖建组合群前缀表)
     * @param sPre String：组合群前缀表
     */
    public void createGroup(String sPre) throws YssException {
        if (dbl.dbType == YssCons.DB_ORA) {
            new Ora(dbl).createGroupTables(sPre);
        } else if (dbl.dbType == YssCons.DB_SQL) {
            new Sql(dbl).createGroupTables(sPre);
        } else if (dbl.dbType == YssCons.DB_DB2) {
            new DB2(dbl).createGroupTables(sPre);
        }
    }

    /**
     * 更新数据表结构变更
     * @param sPre String
     * @throws YssException
     */
    public void updateGroupOnly(String sPre) throws YssException {
        if (dbl.dbType == YssCons.DB_ORA) {
            Ora ora = new Ora(dbl);
            ora.setYssPub(pub);
            ora.updateGroupOnly(sPre);
        } else if (dbl.dbType == YssCons.DB_SQL) {
            new Sql(dbl).updateGroupOnly(sPre);
        } else if (dbl.dbType == YssCons.DB_DB2) {
            new DB2(dbl).updateGroupOnly(sPre);
        }
    }

    public void updateCommon() throws YssException {
        if (dbl.dbType == YssCons.DB_ORA) {
            new Ora(dbl).updateCommon();
        } else if (dbl.dbType == YssCons.DB_SQL) {
            new Sql(dbl).updateCommon();
        } else if (dbl.dbType == YssCons.DB_DB2) {
            new DB2(dbl).updateCommon();
        }
    }
}
