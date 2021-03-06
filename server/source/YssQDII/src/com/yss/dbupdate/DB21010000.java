package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;
import com.yss.util.YssException;

public class DB21010000
    extends BaseDbUpdate {
    public DB21010000() {
    }

    //增加表字段
    public void addTableField() throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------TB_FUN_VERSION 添加字段 FFINISH-------------------------------------//
            strPKName = this.getIsNullPKByTableName_DB2("TB_FUN_VERSION"); //判断是否存在主键
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_FUN_VERSION DROP CONSTRAINT " + strPKName); //如果存在就删除主键
            }
            if (dbl.yssTableExist("TB_12182007093558")) {
                this.dropTableByTableName("TB_12182007093558");
            }
            dbl.executeSql("RENAME TABLE TB_FUN_VERSION TO TB_12182007093558"); //将原表改名为临时表

            bufSql.append("CREATE TABLE TB_FUN_VERSION "); //创建新表
            bufSql.append("(");
            bufSql.append("    FASSETGROUPCODE VARCHAR(20)   NOT NULL,");
            bufSql.append("    FVERNUM         VARCHAR(50)   NOT NULL,");
            bufSql.append("    FISSUEDATE      DATE          NOT NULL,");
            bufSql.append("    FFINISH         VARCHAR(20)   NOT NULL DEFAULT 'Fail',");
            bufSql.append("    FDESC           VARCHAR(1000),");
            bufSql.append("    FCREATEDATE     DATE          NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR(20)   NOT NULL ");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length()); //将 buffer 清空

            bufSql.append("INSERT INTO TB_FUN_VERSION(");
            bufSql.append("                                    FASSETGROUPCODE,");
            bufSql.append("                                    FVERNUM,");
            bufSql.append("                                    FISSUEDATE,");
            bufSql.append("                                    FFINISH,");
            bufSql.append("                                    FDESC,");
            bufSql.append("                                    FCREATEDATE,");
            bufSql.append("                                    FCREATETIME");
            bufSql.append("                                   )");
            bufSql.append("                             SELECT");
            bufSql.append("                                    FASSETGROUPCODE,");
            bufSql.append("                                    FVERNUM,");
            bufSql.append("                                    FISSUEDATE,");
            bufSql.append("                                    'Fail',");
            bufSql.append("                                    FDESC,");
            bufSql.append("                                    FCREATEDATE,");
            bufSql.append("                                    FCREATETIME");
            bufSql.append("                               FROM TB_12182007093558");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            dbl.executeSql("ALTER TABLE TB_FUN_VERSION ADD CONSTRAINT PK_TB_FUN_VERSION " +
                           "PRIMARY KEY (FASSETGROUPCODE,FVERNUM) ");
            //------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 通用数据表增加字段出错！", e);
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
            strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre + "_PARA_FIXINTEREST");
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_12182007093951")) {
                this.dropTableByTableName("TB_12182007093951");
            }
            dbl.executeSql("RENAME TABLE " + pub.yssGetTableName("TB_PARA_FIXINTEREST") + " TO TB_12182007093951");

            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_PARA_FIXINTEREST"));
            bufSql.append(" (");
            bufSql.append("    FSECURITYCODE     VARCHAR(20)    NOT NULL,");
            bufSql.append("    FSTARTDATE        DATE           NOT NULL,");
            bufSql.append("    FISSUEDATE        DATE           NOT NULL,");
            bufSql.append("    FISSUEPRICE       DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FINSSTARTDATE     DATE           NOT NULL,");
            bufSql.append("    FINSENDDATE       DATE           NOT NULL,");
            bufSql.append("    FINSCASHDATE      DATE           NOT NULL,");
            bufSql.append("    FFACEVALUE        DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FFACERATE         DECIMAL(18,12),");
            bufSql.append("    FINSFREQUENCY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FQUOTEWAY         DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREDITLEVEL      VARCHAR(20),");
            bufSql.append("    FCALCINSMETICDAY  VARCHAR(20),");
            bufSql.append("    FCALCINSMETICBUY  VARCHAR(20),");
            bufSql.append("    FCALCINSMETICSELL VARCHAR(20),");
            bufSql.append("    FCalcPriceMetic   VARCHAR(20),");
            bufSql.append("    FCALCINSCFGDAY    VARCHAR(500),");
            bufSql.append("    FCALCINSCFGBUY    VARCHAR(500),");
            bufSql.append("    FCALCINSCFGSELL   VARCHAR(500),");
            bufSql.append("    FCALCINSWAY       DECIMAL(1)     NOT NULL,");
            bufSql.append("    FINTERESTORIGIN   DECIMAL(1)     NOT NULL,");
            bufSql.append("    FPEREXPCODE       VARCHAR(20),");
            bufSql.append("    FPERIODCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("    FROUNDCODE        VARCHAR(20)    NOT NULL,");
            bufSql.append("    FDESC             VARCHAR(100),");
            bufSql.append("    FCHECKSTATE       DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR          VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCREATETIME       VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCHECKUSER        VARCHAR(20),");
            bufSql.append("    FCHECKTIME        VARCHAR(20)");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO ");
            bufSql.append(pub.yssGetTableName("Tb_Para_FixInterest")); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
            bufSql.append("(                                            FSECURITYCODE,");
            bufSql.append("                                             FSTARTDATE,");
            bufSql.append("                                             FISSUEDATE,");
            bufSql.append("                                             FISSUEPRICE,");
            bufSql.append("                                             FINSSTARTDATE,");
            bufSql.append("                                             FINSENDDATE,");
            bufSql.append("                                             FINSCASHDATE,");
            bufSql.append("                                             FFACEVALUE,");
            bufSql.append("                                             FFACERATE,");
            bufSql.append("                                             FINSFREQUENCY,");
            bufSql.append("                                             FQUOTEWAY,");
            bufSql.append("                                             FCREDITLEVEL,");
            bufSql.append("                                             FCALCINSMETICDAY,");
            bufSql.append("                                             FCALCINSMETICBUY,");
            bufSql.append("                                             FCALCINSMETICSELL,");
            bufSql.append("                                             FCALCINSCFGDAY,");
            bufSql.append("                                             FCALCINSCFGBUY,");
            bufSql.append("                                             FCALCINSCFGSELL,");
            bufSql.append("                                             FCALCINSWAY,");
            bufSql.append("                                             FINTERESTORIGIN,");
            bufSql.append("                                             FPEREXPCODE,");
            bufSql.append("                                             FPERIODCODE,");
            bufSql.append("                                             FROUNDCODE,");
            bufSql.append("                                             FDESC,");
            bufSql.append("                                             FCHECKSTATE,");
            bufSql.append("                                             FCREATOR,");
            bufSql.append("                                             FCREATETIME,");
            bufSql.append("                                             FCHECKUSER,");
            bufSql.append("                                             FCHECKTIME");
            bufSql.append("                                            )");
            bufSql.append("                                      SELECT");
            bufSql.append("                                             FSECURITYCODE,");
            bufSql.append("                                             FSTARTDATE,");
            bufSql.append("                                             FISSUEDATE,");
            bufSql.append("                                             FISSUEPRICE,");
            bufSql.append("                                             FINSSTARTDATE,");
            bufSql.append("                                             FINSENDDATE,");
            bufSql.append("                                             FINSCASHDATE,");
            bufSql.append("                                             FFACEVALUE,");
            bufSql.append("                                             FFACERATE,");
            bufSql.append("                                             FINSFREQUENCY,");
            bufSql.append("                                             FQUOTEWAY,");
            bufSql.append("                                             FCREDITLEVEL,");
            bufSql.append("                                             FCALCINSMETICDAY,");
            bufSql.append("                                             FCALCINSMETICBUY,");
            bufSql.append("                                             FCALCINSMETICSELL,");
            bufSql.append("                                             FCALCINSCFGDAY,");
            bufSql.append("                                             FCALCINSCFGBUY,");
            bufSql.append("                                             FCALCINSCFGSELL,");
            bufSql.append("                                             FCALCINSWAY,");
            bufSql.append("                                             FINTERESTORIGIN,");
            bufSql.append("                                             FPEREXPCODE,");
            bufSql.append("                                             FPERIODCODE,");
            bufSql.append("                                             FROUNDCODE,");
            bufSql.append("                                             FDESC,");
            bufSql.append("                                             FCHECKSTATE,");
            bufSql.append("                                             FCREATOR,");
            bufSql.append("                                             FCREATETIME,");
            bufSql.append("                                             FCHECKUSER,");
            bufSql.append("                                             FCHECKTIME");
            bufSql.append("                                        FROM TB_12182007093951");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                           " ADD CONSTRAINT PK_Tb_" + sPre + "_NTEREST" +
                           " PRIMARY KEY (FSECURITYCODE,FSTARTDATE)");
            //----------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 组合群表增加字段出错！", e);
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
            strPKName = this.getIsNullPKByTableName_DB2("TB_PARA_FIXINTEREST");
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_FUN_SYSDATA DROP CONSTRAINT " +
                               strPKName);
            }
            if (dbl.yssTableExist("TB_12182007094137")) {
                this.dropTableByTableName("TB_12182007094137");
            }
            dbl.executeSql("RENAME TABLE TB_FUN_SYSDATA TO TB_12182007094137");

            bufSql.append("CREATE TABLE TB_FUN_SYSDATA ");
            bufSql.append("( ");
            bufSql.append("    FNUM            VARCHAR(20)   NOT NULL,");
            bufSql.append("    FASSETGROUPCODE VARCHAR(20)   NOT NULL,");
            bufSql.append("    FFUNNAME        VARCHAR(50)   NOT NULL,");
            bufSql.append("    FCODE           VARCHAR(20),");
            bufSql.append("    FName           VARCHAR(50),");
            bufSql.append("    FUPDATESQL      VARCHAR(4000),");
            bufSql.append("    FDESC           VARCHAR(100),");
            bufSql.append("    FCREATOR        VARCHAR(20)   NOT NULL,");
            bufSql.append("    FCREATEDATE     DATE          NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR(20)   NOT NULL");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO TB_FUN_SYSDATA(");
            bufSql.append("                                    FNUM,");
            bufSql.append("                                    FASSETGROUPCODE,");
            bufSql.append("                                    FFUNNAME,");
            bufSql.append("                                    FCODE,");
            bufSql.append("                                    FName,");
            bufSql.append("                                    FUPDATESQL,");
            bufSql.append("                                    FDESC,");
            bufSql.append("                                    FCREATOR,");
            bufSql.append("                                    FCREATEDATE,");
            bufSql.append("                                    FCREATETIME");
            bufSql.append("                                   )");
            bufSql.append("                             SELECT");
            bufSql.append("                                    FNUM,");
            bufSql.append("                                    FASSETGROUPCODE,");
            bufSql.append("                                    FFUNNAME,");
            bufSql.append("                                    FCODE,");
            bufSql.append("                                    FNAME,");
            bufSql.append("                                    FUPDATESQL,");
            bufSql.append("                                    FDESC,");
            bufSql.append("                                    FCREATOR,");
            bufSql.append("                                    FCREATEDATE,");
            bufSql.append("                                    FCREATETIME");
            bufSql.append("                               FROM TB_12182007094137");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            dbl.executeSql("ALTER TABLE TB_FUN_SYSDATA ADD CONSTRAINT PK_TB_FUN_SYSDATA " +
                           "PRIMARY KEY (FNUM)");

            //-----------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 通用表修改字段精度出错！", e);
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
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("TB_DATA_MARKETVALUE"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_DATA_MARKETVALUE") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_12182007094414")) {
                this.dropTableByTableName("TB_12182007094414");
            }
            dbl.executeSql("RENAME TABLE " + pub.yssGetTableName("TB_DATA_MARKETVALUE") + " TO TB_12182007094414");

            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_DATA_MARKETVALUE"));
            bufSql.append(" (");
            bufSql.append("    FMKTSRCCODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("    FSECURITYCODE  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FMKTVALUEDATE  DATE           NOT NULL,");
            bufSql.append("    FMKTVALUETIME  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FPORTCODE      VARCHAR(20)    NOT NULL,");
            bufSql.append("    FBARGAINAMOUNT DECIMAL(18,4),");
            bufSql.append("    FBARGAINMONEY  DECIMAL(18,4),");
            bufSql.append("    FYClosePrice   DECIMAL(20,12) NOT NULL,");
            bufSql.append("    FOpenPrice     DECIMAL(20,12) NOT NULL,");
            bufSql.append("    FTopPrice      DECIMAL(20,12) NOT NULL,");
            bufSql.append("    FLowPrice      DECIMAL(20,12) NOT NULL,");
            bufSql.append("    FClosingPrice  DECIMAL(20,12) NOT NULL,");
            bufSql.append("    FAveragePrice  DECIMAL(20,12) NOT NULL,");
            bufSql.append("    FNewPrice      DECIMAL(20,12),");
            bufSql.append("    FMktPrice1     DECIMAL(20,12),");
            bufSql.append("    FMktPrice2     DECIMAL(20,12),");
            bufSql.append("    FDESC          VARCHAR(100),");
            bufSql.append("    FDATASOURCE    DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCHECKSTATE    DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR       VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCREATETIME    VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCHECKUSER     VARCHAR(20),");
            bufSql.append("    FCHECKTIME     VARCHAR(20)");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO " + pub.yssGetTableName("TB_DATA_MARKETVALUE") + "(");
            bufSql.append("                                             FMKTSRCCODE,");
            bufSql.append("                                             FSECURITYCODE,");
            bufSql.append("                                             FMKTVALUEDATE,");
            bufSql.append("                                             FMKTVALUETIME,");
            bufSql.append("                                             FPORTCODE,");
            bufSql.append("                                             FBARGAINAMOUNT,");
            bufSql.append("                                             FBARGAINMONEY,");
            bufSql.append("                                             FYClosePrice,");
            bufSql.append("                                             FOpenPrice,");
            bufSql.append("                                             FTopPrice,");
            bufSql.append("                                             FLowPrice,");
            bufSql.append("                                             FClosingPrice,");
            bufSql.append("                                             FAveragePrice,");
            bufSql.append("                                             FNewPrice,");
            bufSql.append("                                             FMktPrice1,");
            bufSql.append("                                             FMktPrice2,");
            bufSql.append("                                             FDESC,");
            bufSql.append("                                             FDATASOURCE,");
            bufSql.append("                                             FCHECKSTATE,");
            bufSql.append("                                             FCREATOR,");
            bufSql.append("                                             FCREATETIME,");
            bufSql.append("                                             FCHECKUSER,");
            bufSql.append("                                             FCHECKTIME");
            bufSql.append("                                            )");
            bufSql.append("                                      SELECT");
            bufSql.append("                                             FMKTSRCCODE,");
            bufSql.append("                                             FSECURITYCODE,");
            bufSql.append("                                             FMKTVALUEDATE,");
            bufSql.append("                                             FMKTVALUETIME,");
            bufSql.append("                                             FPORTCODE,");
            bufSql.append("                                             FBARGAINAMOUNT,");
            bufSql.append("                                             FBARGAINMONEY,");
            bufSql.append("                                             FYCLOSEPRICE,");
            bufSql.append("                                             FOPENPRICE,");
            bufSql.append("                                             FTOPPRICE,");
            bufSql.append("                                             FLOWPRICE,");
            bufSql.append("                                             FCLOSINGPRICE,");
            bufSql.append("                                             FAVERAGEPRICE,");
            bufSql.append("                                             FNEWPRICE,");
            bufSql.append("                                             FMKTPRICE1,");
            bufSql.append("                                             FMKTPRICE2,");
            bufSql.append("                                             FDESC,");
            bufSql.append("                                             FDATASOURCE,");
            bufSql.append("                                             FCHECKSTATE,");
            bufSql.append("                                             FCREATOR,");
            bufSql.append("                                             FCREATETIME,");
            bufSql.append("                                             FCHECKUSER,");
            bufSql.append("                                             FCHECKTIME");
            bufSql.append("                                        FROM TB_12182007094414");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_DATA_MARKETVALUE") +
                           " ADD CONSTRAINT PK_Tb_" + sPre + "_ETVALUE" +
                           " PRIMARY KEY (FMKTSRCCODE,FSECURITYCODE,FMKTVALUEDATE,FMKTVALUETIME,FPORTCODE)");
            //----------------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0000 组合群表修改字段精度出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
