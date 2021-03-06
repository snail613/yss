package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;
import com.yss.util.YssException;

public class Ora1010000
    extends BaseDbUpdate {
    public Ora1010000() {
    }

    //增加表字段
    public void addTableField() throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------TB_FUN_VERSION 添加字段 FFINISH-------------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            strPKName = this.getIsNullPKByTableName_Ora("TB_FUN_VERSION");
            if (strPKName.trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_FUN_VERSION DROP CONSTRAINT " + strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_FUN_VER_12122007074450000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_FUN_VER_12122007074450000");
            }
            dbl.executeSql("ALTER TABLE TB_FUN_VERSION RENAME TO TB_FUN_VER_12122007074450000"); //将原表更名为临时表

            bufSql.append("CREATE TABLE TB_FUN_VERSION "); //重新建表
            bufSql.append("( ");
            bufSql.append("FASSETGROUPCODE VARCHAR2(20)   NOT NULL, ");
            bufSql.append("FVERNUM         VARCHAR2(50)   NOT NULL, ");
            bufSql.append("FISSUEDATE      DATE           NOT NULL, ");
            bufSql.append("FFINISH         VARCHAR2(20)   DEFAULT 'Fail' NOT NULL, ");
            bufSql.append("FDesc           VARCHAR2(1000)     NULL, ");
            bufSql.append("FCREATEDATE     DATE           NOT NULL, ");
            bufSql.append("FCREATETIME     VARCHAR2(20)   NOT NULL ");
            bufSql.append(") ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO TB_FUN_VERSION( "); //将临时表中的数据插入新表
            bufSql.append("FASSETGROUPCODE, ");
            bufSql.append("FVERNUM, ");
            bufSql.append("FISSUEDATE, ");
            bufSql.append("FFINISH, ");
            bufSql.append("FDESC, ");
            bufSql.append("FCREATEDATE, ");
            bufSql.append("FCREATETIME ");
            bufSql.append(") ");
            bufSql.append("SELECT ");
            bufSql.append("FASSETGROUPCODE, ");
            bufSql.append("FVERNUM, ");
            bufSql.append("FISSUEDATE, ");
            bufSql.append("' ', ");
            bufSql.append("FDESC, ");
            bufSql.append("FCREATEDATE, ");
            bufSql.append("FCREATETIME ");
            bufSql.append("FROM TB_FUN_VER_12122007074450000 ");
            dbl.executeSql(bufSql.toString());

            conn.commit();
            bufSql.delete(0, bufSql.length());

            dbl.executeSql("ALTER TABLE TB_FUN_VERSION ADD CONSTRAINT PK_TB_FUN_VERSION " +
                           "PRIMARY KEY (FASSETGROUPCODE,FVERNUM) "); //添加主键

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 通用数据表新增字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 为组合群表添加字段
     * @param sPre String：组合群编号
     * @throws YssException
     */
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //----------------------TB_XXX_PARA_FIXINTEREST  添加字段 FCALCPRICEMETIC------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql("UPDATE " + pub.yssGetTableName("TB_PARA_FIXINTEREST") + " SET FCALCINSWAY = 0 " +
                           "WHERE nvl2(translate(FCALCINSWAY,'\\1234567890','\\'),'characters','number') = 'characters'");
            conn.commit();
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName("TB_PARA_FIXINTEREST"));
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_" + sPre + "_PAR_12132007054615000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_PAR_12132007054615000");
            }
            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                           " RENAME TO TB_" + sPre + "_PAR_12132007054615000"); //将原表更名为临时表

            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_PARA_FIXINTEREST")); //重新建表
            bufSql.append("( ");
            bufSql.append("    FSECURITYCODE     VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FSTARTDATE        DATE          NOT NULL,");
            bufSql.append("    FISSUEDATE        DATE          NOT NULL,");
            bufSql.append("    FISSUEPRICE       NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FINSSTARTDATE     DATE          NOT NULL,");
            bufSql.append("    FINSENDDATE       DATE          NOT NULL,");
            bufSql.append("    FINSCASHDATE      DATE          NOT NULL,");
            bufSql.append("    FFACEVALUE        NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FFACERATE         NUMBER(18,12)     NULL,");
            bufSql.append("    FINSFREQUENCY     NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FQUOTEWAY         NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREDITLEVEL      VARCHAR2(20)      NULL,");
            bufSql.append("    FCALCINSMETICDAY  VARCHAR2(20)      NULL,");
            bufSql.append("    FCALCINSMETICBUY  VARCHAR2(20)      NULL,");
            bufSql.append("    FCALCINSMETICSELL VARCHAR2(20)      NULL,");
            bufSql.append("    FCALCPRICEMETIC   VARCHAR2(20)      NULL,");
            bufSql.append("    FCALCINSCFGDAY    VARCHAR2(500)     NULL,");
            bufSql.append("    FCALCINSCFGBUY    VARCHAR2(500)     NULL,");
            bufSql.append("    FCALCINSCFGSELL   VARCHAR2(500)     NULL,");
            bufSql.append("    FCALCINSWAY       NUMBER(1)     NOT NULL,");
            bufSql.append("    FINTERESTORIGIN   NUMBER(1)     NOT NULL,");
            bufSql.append("    FPEREXPCODE       VARCHAR2(20)      NULL,");
            bufSql.append("    FPERIODCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FROUNDCODE        VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FDESC             VARCHAR2(100)     NULL,");
            bufSql.append("    FCHECKSTATE       NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR          VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER        VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME        VARCHAR2(20)      NULL");
            bufSql.append(") ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO " + pub.yssGetTableName("TB_PARA_FIXINTEREST") + "( "); //将临时表中的数据插入新表
            bufSql.append("                                          FSECURITYCODE,");
            bufSql.append("                                          FSTARTDATE,");
            bufSql.append("                                          FISSUEDATE,");
            bufSql.append("                                          FISSUEPRICE,");
            bufSql.append("                                          FINSSTARTDATE,");
            bufSql.append("                                          FINSENDDATE,");
            bufSql.append("                                          FINSCASHDATE,");
            bufSql.append("                                          FFACEVALUE,");
            bufSql.append("                                          FFACERATE,");
            bufSql.append("                                          FINSFREQUENCY,");
            bufSql.append("                                          FQUOTEWAY,");
            bufSql.append("                                          FCREDITLEVEL,");
            bufSql.append("                                          FCALCINSMETICDAY,");
            bufSql.append("                                          FCALCINSMETICBUY,");
            bufSql.append("                                          FCALCINSMETICSELL,");
            bufSql.append("                                          FCalcPriceMetic,");
            bufSql.append("                                          FCALCINSCFGDAY,");
            bufSql.append("                                          FCALCINSCFGBUY,");
            bufSql.append("                                          FCALCINSCFGSELL,");
            bufSql.append("                                          FCALCINSWAY,");
            bufSql.append("                                          FINTERESTORIGIN,");
            bufSql.append("                                          FPEREXPCODE,");
            bufSql.append("                                          FPERIODCODE,");
            bufSql.append("                                          FROUNDCODE,");
            bufSql.append("                                          FDESC,");
            bufSql.append("                                          FCHECKSTATE,");
            bufSql.append("                                          FCREATOR,");
            bufSql.append("                                          FCREATETIME,");
            bufSql.append("                                          FCHECKUSER,");
            bufSql.append("                                          FCHECKTIME");
            bufSql.append("                                         )");
            bufSql.append("                                   SELECT");
            bufSql.append("                                          FSECURITYCODE,");
            bufSql.append("                                          FSTARTDATE,");
            bufSql.append("                                          FISSUEDATE,");
            bufSql.append("                                          FISSUEPRICE,");
            bufSql.append("                                          FINSSTARTDATE,");
            bufSql.append("                                          FINSENDDATE,");
            bufSql.append("                                          FINSCASHDATE,");
            bufSql.append("                                          FFACEVALUE,");
            bufSql.append("                                          FFACERATE,");
            bufSql.append("                                          FINSFREQUENCY,");
            bufSql.append("                                          FQUOTEWAY,");
            bufSql.append("                                          FCREDITLEVEL,");
            bufSql.append("                                          FCALCINSMETICDAY,");
            bufSql.append("                                          FCALCINSMETICBUY,");
            bufSql.append("                                          FCALCINSMETICSELL,");
            bufSql.append("                                          null,");
            bufSql.append("                                          FCALCINSCFGDAY,");
            bufSql.append("                                          FCALCINSCFGBUY,");
            bufSql.append("                                          FCALCINSCFGSELL,");
            bufSql.append("                                          FCALCINSWAY,");
            bufSql.append("                                          FINTERESTORIGIN,");
            bufSql.append("                                          FPEREXPCODE,");
            bufSql.append("                                          FPERIODCODE,");
            bufSql.append("                                          FROUNDCODE,");
            bufSql.append("                                          FDESC,");
            bufSql.append("                                          FCHECKSTATE,");
            bufSql.append("                                          FCREATOR,");
            bufSql.append("                                          FCREATETIME,");
            bufSql.append("                                          FCHECKUSER,");
            bufSql.append("                                          FCHECKTIME");
            bufSql.append("                                     FROM TB_" + sPre + "_PAR_12132007054615000");
            dbl.executeSql(bufSql.toString());

            conn.commit();
            bufSql.delete(0, bufSql.length());

            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_PARA_FIXINTEREST " +
                           " PRIMARY KEY (FSECURITYCODE,FSTARTDATE)"); //添加主键

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 组合群表新增字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //调整字段精度
    public void adjustFieldPrecision() throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------TB_FUN_SYSDATA  字段 FName 增长为50长-------------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            strPKName = this.getIsNullPKByTableName_Ora("TB_FUN_SYSDATA");
            if (strPKName.trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_FUN_SYSDATA DROP CONSTRAINT " + strPKName + " CASCADE");
            }
            if (dbl.yssTableExist("TB_FUN_SYS_12132007023513000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_FUN_SYS_12132007023513000");
            }
            dbl.executeSql("ALTER TABLE TB_FUN_SYSDATA RENAME TO TB_FUN_SYS_12132007023513000");

            bufSql.append("CREATE TABLE TB_FUN_SYSDATA ");
            bufSql.append("( ");
            bufSql.append("    FNum            VARCHAR2(20)   NOT NULL,");
            bufSql.append("    FASSETGROUPCODE VARCHAR2(20)   NOT NULL,");
            bufSql.append("    FFUNNAME        VARCHAR2(50)   NOT NULL,");
            bufSql.append("    FCODE           VARCHAR2(20)       NULL,");
            bufSql.append("    FName           VARCHAR2(50)       NULL,");
            bufSql.append("    FUPDATESQL      VARCHAR2(4000)     NULL,");
            bufSql.append("    FDESC           VARCHAR2(100)      NULL,");
            bufSql.append("    FCREATOR        VARCHAR2(20)   NOT NULL,");
            bufSql.append("    FCREATEDATE     DATE           NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR2(20)   NOT NULL");
            bufSql.append(") ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO TB_FUN_SYSDATA(");
            bufSql.append("                                 FNum,");
            bufSql.append("                                 FASSETGROUPCODE,");
            bufSql.append("                                 FFUNNAME,");
            bufSql.append("                                 FCODE,");
            bufSql.append("                                 FName,");
            bufSql.append("                                 FUPDATESQL,");
            bufSql.append("                                 FDESC,");
            bufSql.append("                                 FCREATOR,");
            bufSql.append("                                 FCREATEDATE,");
            bufSql.append("                                 FCREATETIME");
            bufSql.append("                                )");
            bufSql.append("                          SELECT ");
            bufSql.append("                                 FNUM,");
            bufSql.append("                                 FASSETGROUPCODE,");
            bufSql.append("                                 FFUNNAME,");
            bufSql.append("                                 FCODE,");
            bufSql.append("                                 FNAME,");
            bufSql.append("                                 FUPDATESQL,");
            bufSql.append("                                 FDESC,");
            bufSql.append("                                 FCREATOR,");
            bufSql.append("                                 FCREATEDATE,");
            bufSql.append("                                 FCREATETIME");
            bufSql.append("                            FROM TB_FUN_SYS_12132007023513000");
            dbl.executeSql(bufSql.toString());

            conn.commit();
            bufSql.delete(0, bufSql.length());

            dbl.executeSql("ALTER TABLE TB_FUN_SYSDATA ADD CONSTRAINT PK_TB_FUN_SYSDATA " +
                           "PRIMARY KEY (FNum)");

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 通用数据表新增字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    //调整字段精度
    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------TB_001_DATA_MARKETVALUE  字段 字段精度变化-------------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName("TB_DATA_MARKETVALUE"));
            if (strPKName.trim().length() != 0) { //使用表名取主键名，如果没有就不 Drop
                dbl.executeSql(
                    "ALTER TABLE " + pub.yssGetTableName("TB_DATA_MARKETVALUE") +
                    " DROP CONSTRAINT " + strPKName + " CASCADE");
            }

            if (dbl.yssTableExist("TB_" + sPre + "_DAT_12132007093345000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_12132007093345000");
            }
            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_DATA_MARKETVALUE") +
                           " RENAME TO TB_" + sPre + "_DAT_12132007093345000");

            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_DATA_MARKETVALUE"));
            bufSql.append("( ");
            bufSql.append("    FMKTSRCCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FSECURITYCODE  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FMKTVALUEDATE  DATE          NOT NULL,");
            bufSql.append("    FMKTVALUETIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FPORTCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FBARGAINAMOUNT NUMBER(18,4)      NULL,");
            bufSql.append("    FBARGAINMONEY  NUMBER(18,4)      NULL,");
            bufSql.append("    FYClosePrice   NUMBER(20,12) NOT NULL,");
            bufSql.append("    FOpenPrice     NUMBER(20,12) NOT NULL,");
            bufSql.append("    FTopPrice      NUMBER(20,12) NOT NULL,");
            bufSql.append("    FLowPrice      NUMBER(20,12) NOT NULL,");
            bufSql.append("    FClosingPrice  NUMBER(20,12) NOT NULL,");
            bufSql.append("    FAveragePrice  NUMBER(20,12) NOT NULL,");
            bufSql.append("    FNewPrice      NUMBER(20,12)     NULL,");
            bufSql.append("    FMktPrice1     NUMBER(20,12)     NULL,");
            bufSql.append("    FMktPrice2     NUMBER(20,12)     NULL,");
            bufSql.append("    FDESC          VARCHAR2(100)     NULL,");
            bufSql.append("    FDATASOURCE    NUMBER(1)     NOT NULL,");
            bufSql.append("    FCHECKSTATE    NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER     VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME     VARCHAR2(20)      NULL");
            bufSql.append(") ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO " + pub.yssGetTableName("TB_DATA_MARKETVALUE("));
            bufSql.append("                                          FMKTSRCCODE,");
            bufSql.append("                                          FSECURITYCODE,");
            bufSql.append("                                          FMKTVALUEDATE,");
            bufSql.append("                                          FMKTVALUETIME,");
            bufSql.append("                                          FPORTCODE,");
            bufSql.append("                                          FBARGAINAMOUNT,");
            bufSql.append("                                          FBARGAINMONEY,");
            bufSql.append("                                          FYClosePrice,");
            bufSql.append("                                          FOpenPrice,");
            bufSql.append("                                          FTopPrice,");
            bufSql.append("                                          FLowPrice,");
            bufSql.append("                                          FClosingPrice,");
            bufSql.append("                                          FAveragePrice,");
            bufSql.append("                                          FNewPrice,");
            bufSql.append("                                          FMktPrice1,");
            bufSql.append("                                          FMktPrice2,");
            bufSql.append("                                          FDESC,");
            bufSql.append("                                          FDATASOURCE,");
            bufSql.append("                                          FCHECKSTATE,");
            bufSql.append("                                          FCREATOR,");
            bufSql.append("                                          FCREATETIME,");
            bufSql.append("                                          FCHECKUSER,");
            bufSql.append("                                          FCHECKTIME");
            bufSql.append("                                         )");
            bufSql.append("                                   SELECT");
            bufSql.append("                                          FMKTSRCCODE,");
            bufSql.append("                                          FSECURITYCODE,");
            bufSql.append("                                          FMKTVALUEDATE,");
            bufSql.append("                                          FMKTVALUETIME,");
            bufSql.append("                                          FPORTCODE,");
            bufSql.append("                                          FBARGAINAMOUNT,");
            bufSql.append("                                          FBARGAINMONEY,");
            bufSql.append("                                          FYCLOSEPRICE,");
            bufSql.append("                                          FOPENPRICE,");
            bufSql.append("                                          FTOPPRICE,");
            bufSql.append("                                          FLOWPRICE,");
            bufSql.append("                                          FCLOSINGPRICE,");
            bufSql.append("                                          FAVERAGEPRICE,");
            bufSql.append("                                          FNEWPRICE,");
            bufSql.append("                                          FMKTPRICE1,");
            bufSql.append("                                          FMKTPRICE2,");
            bufSql.append("                                          FDESC,");
            bufSql.append("                                          FDATASOURCE,");
            bufSql.append("                                          FCHECKSTATE,");
            bufSql.append("                                          FCREATOR,");
            bufSql.append("                                          FCREATETIME,");
            bufSql.append("                                          FCHECKUSER,");
            bufSql.append("                                          FCHECKTIME");
            bufSql.append("                                     FROM TB_" + sPre + "_DAT_12132007093345000");

            dbl.executeSql(bufSql.toString());

            conn.commit();
            bufSql.delete(0, bufSql.length());

            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_DATA_MARKETVALUE") +
                           " ADD CONSTRAINT " + "PK_TB_" + sPre + "_Data_MarketValue" +
                           " PRIMARY KEY (FMKTSRCCODE,FSECURITYCODE,FMKTVALUEDATE,FMKTVALUETIME,FPORTCODE)");

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 组合群表新增字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
