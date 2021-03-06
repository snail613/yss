package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.Connection;
import java.sql.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DB21010005
    extends BaseDbUpdate {
    public DB21010005() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false; //代表是否开始事务
        Connection conn = dbl.loadConnection();
        String sPKName = "";
        try {
            //********************************** TB_PARA_FIXINTEREST  添加字段 FAMORTIZATION,FFACTRATE **********************************//
            if (existsTabColumn_DB2("TB_" + sPre + "_PARA_FIXINTEREST",
                                    "FMARKETSTATUS")) {
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_PARA_FIXINTEREST");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_FIXINTEREST DROP CONSTRAINT " + sPKName);
                }
                if (dbl.yssTableExist("TB_10242008102051")) {
                    this.dropTableByTableName("TB_10242008102051");
                }
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_PARA_FIXINTEREST TO TB_10242008102051");
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_FIXINTEREST ");
                bufSql.append(" ( ");
                bufSql.append(" FSECURITYCODE     VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FSTARTDATE        DATE           NOT NULL, ");
                bufSql.append(" FISSUEDATE        DATE           NOT NULL, ");
                bufSql.append(" FISSUEPRICE       DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FINSSTARTDATE     DATE           NOT NULL, ");
                bufSql.append(" FINSENDDATE       DATE           NOT NULL, ");
                bufSql.append(" FINSCASHDATE      DATE           NOT NULL, ");
                bufSql.append(" FFACEVALUE        DECIMAL(18,12) NOT NULL, ");
                bufSql.append(" FFACERATE         DECIMAL(18,12), ");
                bufSql.append(" FINSFREQUENCY     DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FQUOTEWAY         DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FCREDITLEVEL      VARCHAR(20), ");
                bufSql.append(" FCALCINSMETICDAY  VARCHAR(20), ");
                bufSql.append(" FCALCINSMETICBUY  VARCHAR(20), ");
                bufSql.append(" FCALCINSMETICSELL VARCHAR(20), ");
                bufSql.append(" FCALCPRICEMETIC   VARCHAR(20), ");
                bufSql.append(" FAmortization     VARCHAR(20), ");
                bufSql.append(" FFactRate         DECIMAL(18,4), ");
                bufSql.append(" FCALCINSCFGDAY    VARCHAR(500), ");
                bufSql.append(" FCALCINSCFGBUY    VARCHAR(500), ");
                bufSql.append(" FCALCINSCFGSELL   VARCHAR(500), ");
                bufSql.append(" FCALCINSWAY       DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FINTERESTORIGIN   DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FPEREXPCODE       VARCHAR(20), ");
                bufSql.append("FPERIODCODE       VARCHAR(20)    NOT NULL, ");
                bufSql.append("FROUNDCODE        VARCHAR(20)    NOT NULL, ");
                bufSql.append("FDESC             VARCHAR(100), ");
                bufSql.append("FCHECKSTATE       DECIMAL(1)     NOT NULL, ");
                bufSql.append("FCREATOR          VARCHAR(20)    NOT NULL, ");
                bufSql.append("FCREATETIME       VARCHAR(20)    NOT NULL, ");
                bufSql.append("FCHECKUSER        VARCHAR(20), ");
                bufSql.append("FCHECKTIME        VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.append(" INSERT INTO TB_" + sPre + "_PARA_FIXINTEREST( ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FSTARTDATE, ");
                bufSql.append(" FISSUEDATE, ");
                bufSql.append(" FISSUEPRICE, ");
                bufSql.append(" FINSSTARTDATE, ");
                bufSql.append(" FINSENDDATE, ");
                bufSql.append(" FINSCASHDATE, ");
                bufSql.append(" FFACEVALUE, ");
                bufSql.append(" FFACERATE, ");
                bufSql.append(" FINSFREQUENCY, ");
                bufSql.append(" FQUOTEWAY, ");
                bufSql.append(" FCREDITLEVEL, ");
                bufSql.append(" FCALCINSMETICDAY, ");
                bufSql.append(" FCALCINSMETICBUY, ");
                bufSql.append(" FCALCINSMETICSELL, ");
                bufSql.append(" FCALCPRICEMETIC, ");
                bufSql.append(" FCALCINSCFGDAY, ");
                bufSql.append(" FCALCINSCFGBUY, ");
                bufSql.append(" FCALCINSCFGSELL, ");
                bufSql.append(" FCALCINSWAY, ");
                bufSql.append(" FINTERESTORIGIN, ");
                bufSql.append(" FPEREXPCODE, ");
                bufSql.append(" FPERIODCODE, ");
                bufSql.append(" FROUNDCODE, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FSTARTDATE, ");
                bufSql.append(" FISSUEDATE, ");
                bufSql.append(" FISSUEPRICE, ");
                bufSql.append(" FINSSTARTDATE, ");
                bufSql.append(" FINSENDDATE, ");
                bufSql.append(" FINSCASHDATE, ");
                bufSql.append(" FFACEVALUE, ");
                bufSql.append(" FFACERATE, ");
                bufSql.append(" FINSFREQUENCY, ");
                bufSql.append(" FQUOTEWAY, ");
                bufSql.append(" FCREDITLEVEL, ");
                bufSql.append(" FCALCINSMETICDAY, ");
                bufSql.append(" FCALCINSMETICBUY, ");
                bufSql.append(" FCALCINSMETICSELL, ");
                bufSql.append(" FCALCPRICEMETIC, ");
                bufSql.append(" FCALCINSCFGDAY, ");
                bufSql.append(" FCALCINSCFGBUY, ");
                bufSql.append(" FCALCINSCFGSELL, ");
                bufSql.append(" FCALCINSWAY, ");
                bufSql.append(" FINTERESTORIGIN, ");
                bufSql.append(" FPEREXPCODE, ");
                bufSql.append(" FPERIODCODE, ");
                bufSql.append(" FROUNDCODE, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_10242008102051 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_FIXINTEREST ADD CONSTRAINT PK_TB_" + sPre +
                               "_Para_Fix " +
                               "PRIMARY KEY (FSECURITYCODE,FSTARTDATE)");
                bTrans = false;
            }
            /**
             * date   : 20081112
             * author : sunkey
             * desc   : 为组合群表tb_sys_assetgroup添加‘用户可以审核自己录入的数据’(FCheckSelf)字段
             * BugID  : MS00010
             */
            if (existsTabColumn_DB2("TB_SYS_ASSETGROUP", "FCHECKSELF")) {
                bTrans = true; //标识事物回滚状态 如果中途出现异常，回滚事物
                //开始事物处理
                conn.setAutoCommit(false);
                //如果有主键 删除主键（相关约束）
                if (! (sPKName = getIsNullPKByTableName_DB2("TB_SYS_ASSETGROUP")).trim().equals("")) {
                    dbl.executeSql("ALTER TABLE TB_SYS_ASSETGROUP DROP CONSTRAINT " + sPKName);
                }
                //将原表备份 如果备份的表存在 删除
                if (dbl.yssTableExist("TB_11122008132943")) {
                    dbl.executeSql("DROP TABLE TB_SYS_ASSETGROUP");
                }
                //更新表名
                dbl.executeSql("RENAME TABLE TB_SYS_ASSETGROUP TO TB_11122008132943");
                //创建表 -- 添加字段FCheckSelf
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE TB_SYS_ASSETGROUP(");
                bufSql.append("FASSETGROUPCODE  VARCHAR(3)   NOT NULL,");
                bufSql.append("FASSETGROUPNAME  VARCHAR(100) NOT NULL,");
                bufSql.append("FMAXNUM          INTEGER      NOT NULL,");
                bufSql.append("FSTARTDATE       DATE         NOT NULL,");
                bufSql.append("FBASECURY        VARCHAR(20)  NOT NULL,");
                bufSql.append("FBASERATESRCCODE VARCHAR(20)  NOT NULL,");
                bufSql.append("FBASERATECODE    VARCHAR(20)  NOT NULL,");
                bufSql.append("FPORTRATESRCCODE VARCHAR(20)  NOT NULL,");
                bufSql.append("FPORTRATECODE    VARCHAR(20)  NOT NULL,");
                bufSql.append("FTABIND          DECIMAL(1)   DEFAULT 0,");
                bufSql.append("FTABPREFIX       VARCHAR(6),");
                bufSql.append("FLOCKED          INTEGER      NOT NULL DEFAULT 0,");
                bufSql.append("FSYSCHECK        DECIMAL(1)   NOT NULL DEFAULT 0,");
                bufSql.append("FCheckSelf       VARCHAR(20)  NOT NULL DEFAULT 'yes',");
                bufSql.append("FDESC            VARCHAR(100))");
                dbl.executeSql(bufSql.toString());
                //插入数据 采用insert into ... select ...
                bufSql = new StringBuffer();
                bufSql.append("INSERT INTO TB_SYS_ASSETGROUP(");
                bufSql.append("FASSETGROUPCODE,FASSETGROUPNAME,FMAXNUM,FSTARTDATE,FBASECURY,FBASERATESRCCODE,FBASERATECODE,");
                bufSql.append("FPORTRATESRCCODE,FPORTRATECODE,FTABIND,FTABPREFIX,FLOCKED,FSYSCHECK,FCheckSelf,FDESC)");
                bufSql.append("SELECT FASSETGROUPCODE,FASSETGROUPNAME,FMAXNUM,FSTARTDATE,FBASECURY,FBASERATESRCCODE,FBASERATECODE,");
                bufSql.append("FPORTRATESRCCODE,FPORTRATECODE,FTABIND,FTABPREFIX,FLOCKED,FSYSCHECK,'yes',FDESC FROM TB_11122008132943");
                dbl.executeSql(bufSql.toString());
                //添加主键
                dbl.executeSql("ALTER TABLE TB_SYS_ASSETGROUP ADD CONSTRAINT PK_TB_SYS_ETGROUP PRIMARY KEY (FASSETGROUPCODE)");
                conn.commit();
            }
            bTrans = false;

            // linjunyun 需求MS00016 2008-11-13
            //******************* 更新DB2数据库中的表，给表 TB_SYS_USERLIST 添加字段 FPASSLEVEL和FVALIDTIME；****************//
            if (existsTabColumn_DB2("TB_SYS_USERLIST", "FPassLevel,FValidTime")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                sPKName = this.getIsNullPKByTableName_DB2("TB_SYS_USERLIST");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_SYS_USERLIST DROP CONSTRAINT " +
                                   sPKName);
                }
                if (dbl.yssTableExist("TB_11132008015106")) {
                    this.dropTableByTableName("TB_11132008015106");
                }
                dbl.executeSql("ReName TABLE TB_SYS_USERLIST TO TB_11132008015106");
                bufSql.append(" CREATE TABLE TB_SYS_USERLIST ");
                bufSql.append(" ( ");
                bufSql.append(" FUSERCODE      VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FUSERNAME      VARCHAR(50)   NOT NULL, ");
                bufSql.append(" FPASS          CHAR(200)      FOR BIT DATA, ");
                bufSql.append(" FPASSLEN       INTEGER, ");
                bufSql.append(" FPASSCOUNT     INTEGER, ");
                bufSql.append(" FPASSDATE      DATE, ");
                bufSql.append(" FLOCKED        INTEGER       NOT NULL DEFAULT 0, ");
                bufSql.append(" FMENUSCODE     VARCHAR(3000), ");
                bufSql.append(" FMENUBARSCODE  VARCHAR(3000), ");
                bufSql.append(" FPORTGROUPCODE VARCHAR(1000), ");
                bufSql.append(" FDEPTCODE      VARCHAR(20), ");
                bufSql.append(" FPOSITIONCODE  VARCHAR(20), ");
                bufSql.append(" FPassLevel     VARCHAR(200), ");
                bufSql.append(" FValidTime     INTEGER       NOT NULL DEFAULT 0, ");
                bufSql.append(" FMEMO          VARCHAR(200) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.delete(0, bufSql.length());
                bufSql.append(" INSERT INTO TB_SYS_USERLIST( ");
                bufSql.append(" FUSERCODE, ");
                bufSql.append(" FUSERNAME, ");
                bufSql.append(" FPASS, ");
                bufSql.append(" FPASSLEN, ");
                bufSql.append(" FPASSCOUNT, ");
                bufSql.append(" FPASSDATE, ");
                bufSql.append(" FLOCKED, ");
                bufSql.append(" FMENUSCODE, ");
                bufSql.append(" FMENUBARSCODE, ");
                bufSql.append(" FPORTGROUPCODE, ");
                bufSql.append(" FDEPTCODE, ");
                bufSql.append(" FPOSITIONCODE, ");
                bufSql.append(" FValidTime, ");
                bufSql.append(" FMEMO ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FUSERCODE, ");
                bufSql.append(" FUSERNAME, ");
                bufSql.append(" CHAR(FPASS), ");
                bufSql.append(" FPASSLEN, ");
                bufSql.append(" FPASSCOUNT, ");
                bufSql.append(" FPASSDATE, ");
                bufSql.append(" FLOCKED, ");
                bufSql.append(" FMENUSCODE, ");
                bufSql.append(" FMENUBARSCODE, ");
                bufSql.append(" FPORTGROUPCODE, ");
                bufSql.append(" FDEPTCODE, ");
                bufSql.append(" FPOSITIONCODE, ");
                bufSql.append(" 0, ");
                bufSql.append(" FMEMO ");
                bufSql.append(" FROM TB_11132008015106");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql(
                    "ALTER TABLE TB_SYS_USERLIST ADD CONSTRAINT PK_Tb_Sys_UserList " +
                    " PRIMARY KEY (FUSERCODE)");
            }
            /**
             * date   : 20081113
             * author : sunkey
             * desc   : 库存管理--证券应收应付款库存表添加3个字段
             *          FBalF          Numeric(30,15)
             *          FPortCuryBalF  Numeric(30,15)
             *          FBaseCuryBalF  Numeric(30,15)
             *          因为以上3个字段在同一版本状态是相同的 所以只需判断一个就可以了
             * BugID  : MS00002 文档：《QDV4华夏2008年11月04日01_B》
             */
            String tabName = "Tb_" + sPre + "_Stock_SecRecPay";
            if (existsTabColumn_DB2(tabName, "FBalF")) {
                bTrans = true; //标识事物回滚状态 如果中途出现异常，回滚事物
                //开始事物处理
                conn.setAutoCommit(false);
                //如果有主键 删除主键（相关约束）
                if (! (sPKName = getIsNullPKByTableName_DB2(tabName)).trim().equals("")) {
                    dbl.executeSql("ALTER TABLE " + tabName + " DROP CONSTRAINT " + sPKName);
                }
                //将原表备份 如果备份的表存在 删除
                if (dbl.yssTableExist("TB_11132008104029")) {
                    dbl.executeSql("DROP TABLE TB_11132008104029");
                }
                //更新表名
                dbl.executeSql("RENAME TABLE " + tabName + " TO TB_11132008104029");
                //创建表
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE ").append(tabName).append("(");
                bufSql.append("FYEARMONTH      VARCHAR(6)   NOT NULL,");
                bufSql.append("FSTORAGEDATE    DATE         NOT NULL,");
                bufSql.append("FPORTCODE       VARCHAR(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE1  VARCHAR(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE2  VARCHAR(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE3  VARCHAR(20)  NOT NULL,");
                bufSql.append("FSECURITYCODE   VARCHAR(20)  NOT NULL,");
                bufSql.append("FTSFTYPECODE    VARCHAR(20)  NOT NULL,");
                bufSql.append("FSUBTSFTYPECODE VARCHAR(20)  NOT NULL,");
                bufSql.append("FCATTYPE        VARCHAR(20)  NOT NULL,");
                bufSql.append("FATTRCLSCODE    VARCHAR(20)  NOT NULL,");
                bufSql.append("FCURYCODE       VARCHAR(20)  NOT NULL,");
                bufSql.append("FBAL            DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FMBAL           DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FVBAL           DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FBASECURYBAL    DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FMBASECURYBAL   DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FVBASECURYBAL   DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FPORTCURYBAL    DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FMPORTCURYBAL   DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FVPORTCURYBAL   DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FBALF           DECIMAL(30,15) NOT NULL DEFAULT 0,");
                bufSql.append("FPORTCURYBALF   DECIMAL(30,15) NOT NULL DEFAULT 0,");
                bufSql.append("FBASECURYBALF   DECIMAL(30,15) NOT NULL DEFAULT 0,");
                bufSql.append("FSTORAGEIND     DECIMAL(1)   NOT NULL,");
                bufSql.append("FCHECKSTATE     DECIMAL(1)   NOT NULL,");
                bufSql.append("FCREATOR        VARCHAR(20)  NOT NULL,");
                bufSql.append("FCREATETIME     VARCHAR(20)  NOT NULL,");
                bufSql.append("FCHECKUSER      VARCHAR(20),");
                bufSql.append("FCHECKTIME      VARCHAR(20))");
                dbl.executeSql(bufSql.toString());
                //插入数据 采用insert into ... select ...
                bufSql = new StringBuffer();
                bufSql.append("INSERT INTO ").append(tabName).append("(");
                bufSql.append("FYEARMONTH,FSTORAGEDATE,FPORTCODE,FANALYSISCODE1,");
                bufSql.append("FANALYSISCODE2,FANALYSISCODE3,FSECURITYCODE,FTSFTYPECODE,");
                bufSql.append("FSUBTSFTYPECODE, FCATTYPE,FATTRCLSCODE,FCURYCODE,");
                bufSql.append("FBAL, FMBAL, FVBAL, FBASECURYBAL, FMBASECURYBAL,");
                bufSql.append("FVBASECURYBAL,FPORTCURYBAL, FMPORTCURYBAL, FVPORTCURYBAL,");
                bufSql.append("FBALF, FPORTCURYBALF, FBASECURYBALF, FSTORAGEIND,");
                bufSql.append("FCHECKSTATE, FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) ");
                bufSql.append("SELECT FYEARMONTH,FSTORAGEDATE, FPORTCODE,");
                bufSql.append("FANALYSISCODE1, FANALYSISCODE2,FANALYSISCODE3,FSECURITYCODE,");
                bufSql.append("FTSFTYPECODE,FSUBTSFTYPECODE, FCATTYPE,FATTRCLSCODE, FCURYCODE,");
                bufSql.append("FBAL, FMBAL, FVBAL,FBASECURYBAL, FMBASECURYBAL,");
                bufSql.append("FVBASECURYBAL, FPORTCURYBAL,FMPORTCURYBAL, FVPORTCURYBAL,");
                bufSql.append("FBAL,FPORTCURYBAL,FBASECURYBAL,FSTORAGEIND,FCHECKSTATE, FCREATOR,");
                bufSql.append("FCREATETIME,FCHECKUSER,FCHECKTIME FROM TB_11132008104029");
                dbl.executeSql(bufSql.toString());
                //添加主键
                bufSql = new StringBuffer();
                bufSql.append("ALTER TABLE ").append(tabName).append(" ADD CONSTRAINT PK_TB_" + sPre + "_CRECPAY ");
                bufSql.append("PRIMARY KEY (FYEARMONTH,FSTORAGEDATE,FPORTCODE,FANALYSISCODE1,FANALYSISCODE2,");
                bufSql.append("FANALYSISCODE3,FSECURITYCODE,FTSFTYPECODE,FSUBTSFTYPECODE,FCATTYPE,FATTRCLSCODE)");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                bTrans = false;
            }

            /**
             * date   : 20081113
             * author : sunkey
             * desc   : 业务数据--证券应收应付款表添加3个字段
             *          FMoneyF          Numeric(30,15)
             *          FBaseCuryMoneyF  Numeric(30,15)
             *          FPortCuryMoneyF  Numeric(30,15)
             *          因为以上3个字段在同一版本状态是相同的 所以只需判断一个就可以了
             * BugID  : MS00002 文档：《QDV4华夏2008年11月04日01_B》
             */
            tabName = "TB_" + sPre + "_DATA_SECRECPAY";
            if (existsTabColumn_DB2(tabName, "FMoneyF")) {
                bTrans = true; //标识事物回滚状态 如果中途出现异常，回滚事物
                //开始事物处理
                conn.setAutoCommit(false);
                //如果有主键 删除主键（相关约束）
                if (! (sPKName = getIsNullPKByTableName_DB2(tabName)).trim().equals("")) {
                    dbl.executeSql("ALTER TABLE " + tabName + " DROP CONSTRAINT " + sPKName);
                }
                //将原表备份 如果备份的表存在 删除
                if (dbl.yssTableExist("TB_11132008102306")) {
                    dbl.executeSql("DROP TABLE TB_11132008102306");
                }
                //更新表名
                dbl.executeSql("RENAME TABLE " + tabName + " TO TB_11132008102306");
                //创建表
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE ").append(tabName).append("(");
                bufSql.append("FNUM            VARCHAR(20)  NOT NULL,");
                bufSql.append("FDESC           VARCHAR(100),");
                bufSql.append("FTRANSDATE      DATE          NOT NULL,");
                bufSql.append("FPORTCODE       VARCHAR(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE1  VARCHAR(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE2  VARCHAR(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE3  VARCHAR(20)  NOT NULL,");
                bufSql.append("FSECURITYCODE   VARCHAR(20)  NOT NULL,");
                bufSql.append("FTSFTYPECODE    VARCHAR(20)  NOT NULL,");
                bufSql.append("FSUBTSFTYPECODE VARCHAR(20)  NOT NULL,");
                bufSql.append("FCATTYPE        VARCHAR(20)  NOT NULL,");
                bufSql.append("FATTRCLSCODE    VARCHAR(20)  NOT NULL,");
                bufSql.append("FCURYCODE       VARCHAR(20)  NOT NULL,");
                bufSql.append("FINOUT          DECIMAL(1)     NOT NULL,");
                bufSql.append("FMONEY          DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FMMONEY         DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FVMONEY         DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FBASECURYRATE   DECIMAL(20,15) NOT NULL,");
                bufSql.append("FBASECURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FMBASECURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FVBASECURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FPORTCURYRATE   DECIMAL(20,15) NOT NULL,");
                bufSql.append("FPORTCURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FMPORTCURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FVPORTCURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0,");
                bufSql.append("FMONEYF         DECIMAL(30,15) NOT NULL DEFAULT 0,");
                bufSql.append("FBASECURYMONEYF DECIMAL(30,15) NOT NULL DEFAULT 0,");
                bufSql.append("FPORTCURYMONEYF DECIMAL(30,15) NOT NULL DEFAULT 0,");
                bufSql.append("FDATASOURCE     DECIMAL(1)     NOT NULL,");
                bufSql.append("FSTOCKIND       DECIMAL(1)     NOT NULL,");
                bufSql.append("FCHECKSTATE     DECIMAL(1)     NOT NULL,");
                bufSql.append("FCREATOR        VARCHAR(20)  NOT NULL,");
                bufSql.append("FCREATETIME     VARCHAR(20)  NOT NULL,");
                bufSql.append("FCHECKUSER      VARCHAR(20),");
                bufSql.append("FCHECKTIME      VARCHAR(20))");
                dbl.executeSql(bufSql.toString());
                //插入数据 采用insert into ... select ...
                bufSql = new StringBuffer();
                bufSql.append("INSERT INTO ").append(tabName).append("(");
                bufSql.append("FNUM,FDESC,FTRANSDATE,FPORTCODE,FANALYSISCODE1,");
                bufSql.append("FANALYSISCODE2,FANALYSISCODE3, FSECURITYCODE, FTSFTYPECODE,");
                bufSql.append("FSUBTSFTYPECODE,FCATTYPE,FATTRCLSCODE,FCURYCODE, FINOUT,");
                bufSql.append("FMONEY,FMMONEY,FVMONEY,FBASECURYRATE,FBASECURYMONEY,");
                bufSql.append("FMBASECURYMONEY,FVBASECURYMONEY,FPORTCURYRATE,");
                bufSql.append("FPORTCURYMONEY,FMPORTCURYMONEY, FVPORTCURYMONEY,");
                bufSql.append("FMONEYF,FBASECURYMONEYF,FPORTCURYMONEYF,FDATASOURCE,");
                bufSql.append("FSTOCKIND,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) ");
                bufSql.append("SELECT FNUM,FDESC,FTRANSDATE,FPORTCODE,FANALYSISCODE1,");
                bufSql.append("FANALYSISCODE2, FANALYSISCODE3,FSECURITYCODE,FTSFTYPECODE,");
                bufSql.append("FSUBTSFTYPECODE,FCATTYPE,FATTRCLSCODE,FCURYCODE,");
                bufSql.append("FINOUT,FMONEY,FMMONEY,FVMONEY,FBASECURYRATE,");
                bufSql.append("FBASECURYMONEY,FMBASECURYMONEY,FVBASECURYMONEY,");
                bufSql.append("FPORTCURYRATE,FPORTCURYMONEY,FMPORTCURYMONEY, FVPORTCURYMONEY,");
                bufSql.append("0,0,0,FDATASOURCE,FSTOCKIND,FCHECKSTATE,FCREATOR,FCREATETIME,");
                bufSql.append("FCHECKUSER,FCHECKTIME FROM TB_11132008102306");
                dbl.executeSql(bufSql.toString());
                //添加主键
                bufSql = new StringBuffer();
                dbl.executeSql("ALTER TABLE " + tabName + " ADD CONSTRAINT PK_TB_" + sPre + "_CRECPAY PRIMARY KEY (FNUM)");
                conn.commit();
                bTrans = false;
            }
            // linjunyun 2008-11-14 报表数据源设置里增加存储表
            //******************* 更新DB2数据库中的表，给表 TB_001_REP_DATASOURCE 添加字段 FSTORAGETAB；****************//
            if (existsTabColumn_DB2("TB_" + sPre + "_REP_DATASOURCE", "FSTORAGETAB")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_REP_DATASOURCE");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_REP_DATASOURCE DROP CONSTRAINT " + sPKName);
                }
                if (dbl.yssTableExist("TB_11132008120143")) {
                    this.dropTableByTableName("TB_11132008120143");
                }
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_REP_DATASOURCE TO TB_11132008120143");

                bufSql.append(" CREATE TABLE TB_" + sPre + "_REP_DATASOURCE ");
                bufSql.append(" ( ");
                bufSql.append(" FREPDSCODE  VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FREPDSNAME  VARCHAR(50)  NOT NULL, ");
                bufSql.append(" FDSTYPE     DECIMAL(1)   NOT NULL, ");
                bufSql.append(" FTROWCOLOR  DECIMAL(10)  NOT NULL DEFAULT 0, ");
                bufSql.append(" FBROWCOLOR  DECIMAL(10)  NOT NULL DEFAULT 0, ");
                bufSql.append(" FTEMPTAB    VARCHAR(20), ");
                bufSql.append(" FBEANID     VARCHAR(30), ");
                bufSql.append(" FSTORAGETAB VARCHAR(20), ");
                bufSql.append(" FDATASOURCE CLOB(18M)    LOGGED NOT COMPACT, ");
                bufSql.append(" FFILLRANGE  VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FDESC       VARCHAR(100), ");
                bufSql.append(" FCHECKSTATE DECIMAL(1)   NOT NULL, ");
                bufSql.append(" FCREATOR    VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER  VARCHAR(20), ");
                bufSql.append(" FCHECKTIME  VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.append(" INSERT INTO TB_" + sPre + "_REP_DATASOURCE( ");
                bufSql.append(" FREPDSCODE, ");
                bufSql.append(" FREPDSNAME, ");
                bufSql.append(" FDSTYPE, ");
                bufSql.append(" FTROWCOLOR, ");
                bufSql.append(" FBROWCOLOR, ");
                bufSql.append(" FTEMPTAB, ");
                bufSql.append(" FBEANID, ");
                bufSql.append(" FDATASOURCE, ");
                bufSql.append(" FFILLRANGE, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FREPDSCODE, ");
                bufSql.append(" FREPDSNAME, ");
                bufSql.append(" FDSTYPE, ");
                bufSql.append(" FTROWCOLOR, ");
                bufSql.append(" FBROWCOLOR, ");
                bufSql.append(" FTEMPTAB, ");
                bufSql.append(" FBEANID, ");
                bufSql.append(" FDATASOURCE, ");
                bufSql.append(" FFILLRANGE, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_11132008120143 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_REP_DATASOURCE ADD CONSTRAINT PK_TB_" + sPre +
                               "_Rep_Data " +
                               "PRIMARY KEY (FREPDSCODE)");
                bTrans = false;
            }

            /**
             * date   : 20081117
             * author : linjunyun
             * desc   : 业务数据--净值数据表更改表 TB_001_DATA_NAVDATA 字段的长度
             *          FSEDOLCODE VARCHAR2(20) 改为 FSEDOLCODE VARCHAR2(50)
             *          FISINCODE VARCHAR2(20) 改为 FISINCODE VARCHAR2(50)
             * BugID  :
             */
            //获得主键
            bTrans = false;
            bufSql.delete(0, bufSql.length());
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_DATA_NAVDATA");
            //判断主键是否存在，存在则删除主键
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_NAVDATA DROP CONSTRAINT " + sPKName);
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_11172008021836")) {
                this.dropTableByTableName("TB_11172008021836");
            }
            //重命名原有表为临时表
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_DATA_NAVDATA TO TB_11172008021836");
            //创建包含新字段的表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_NAVDATA ");
            bufSql.append(" ( ");
            bufSql.append(" FNAVDATE              DATE           NOT NULL, ");
            bufSql.append(" FPORTCODE             VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FORDERCODE            VARCHAR(200)   NOT NULL, ");
            bufSql.append(" FRETYPECODE           VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FINVMGRCODE           VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FKEYCODE              VARCHAR(50)    NOT NULL, ");
            bufSql.append(" FINOUT                DECIMAL(1)     DEFAULT 1 NOT NULL, ");
            bufSql.append(" FKEYNAME              VARCHAR(200)   NOT NULL, ");
            bufSql.append(" FDETAIL               DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCURYCODE             VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FPRICE                DECIMAL(20,12), ");
            bufSql.append(" FOTPRICE1             DECIMAL(20,12), ");
            bufSql.append(" FOTPRICE2             DECIMAL(20,12), ");
            bufSql.append(" FOTPRICE3             DECIMAL(20,12), ");
            bufSql.append(" FSEDOLCODE            VARCHAR(50), ");
            bufSql.append(" FISINCODE             VARCHAR(50), ");
            bufSql.append(" FSPARAMT              DECIMAL(20,4), ");
            bufSql.append(" FBASECURYRATE         DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FPORTCURYRATE         DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FCOST                 DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTCOST             DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMARKETVALUE          DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTMARKETVALUE      DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMVVALUE              DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTMVVALUE          DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FFXVALUE              DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTMARKETVALUERATIO DECIMAL(18,4), ");
            bufSql.append(" FGRADETYPE1           VARCHAR(20), ");
            bufSql.append(" FGRADETYPE2           VARCHAR(20), ");
            bufSql.append(" FGRADETYPE3           VARCHAR(20), ");
            bufSql.append(" FGRADETYPE4           VARCHAR(20), ");
            bufSql.append(" FGRADETYPE5           VARCHAR(20), ");
            bufSql.append(" FGRADETYPE6           VARCHAR(20) ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //从临时表中导数据到新创建的表中
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_NAVDATA( ");
            bufSql.append(" FNAVDATE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FORDERCODE, ");
            bufSql.append(" FRETYPECODE, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FKEYCODE, ");
            bufSql.append(" FINOUT, ");
            bufSql.append(" FKEYNAME, ");
            bufSql.append(" FDETAIL, ");
            bufSql.append(" FCURYCODE, ");
            bufSql.append(" FPRICE, ");
            bufSql.append(" FOTPRICE1, ");
            bufSql.append(" FOTPRICE2, ");
            bufSql.append(" FOTPRICE3, ");
            bufSql.append(" FSEDOLCODE, ");
            bufSql.append(" FISINCODE, ");
            bufSql.append(" FSPARAMT, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FCOST, ");
            bufSql.append(" FPORTCOST, ");
            bufSql.append(" FMARKETVALUE, ");
            bufSql.append(" FPORTMARKETVALUE, ");
            bufSql.append(" FMVVALUE, ");
            bufSql.append(" FPORTMVVALUE, ");
            bufSql.append(" FFXVALUE, ");
            bufSql.append(" FPORTMARKETVALUERATIO, ");
            bufSql.append(" FGRADETYPE1, ");
            bufSql.append(" FGRADETYPE2, ");
            bufSql.append(" FGRADETYPE3, ");
            bufSql.append(" FGRADETYPE4, ");
            bufSql.append(" FGRADETYPE5, ");
            bufSql.append(" FGRADETYPE6 ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT  ");
            bufSql.append(" FNAVDATE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FORDERCODE, ");
            bufSql.append(" FRETYPECODE, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FKEYCODE, ");
            bufSql.append(" FINOUT, ");
            bufSql.append(" FKEYNAME, ");
            bufSql.append(" FDETAIL, ");
            bufSql.append(" FCURYCODE, ");
            bufSql.append(" FPRICE, ");
            bufSql.append(" FOTPRICE1, ");
            bufSql.append(" FOTPRICE2, ");
            bufSql.append(" FOTPRICE3, ");
            bufSql.append(" FSEDOLCODE, ");
            bufSql.append(" FISINCODE, ");
            bufSql.append(" FSPARAMT, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FCOST, ");
            bufSql.append(" FPORTCOST, ");
            bufSql.append(" FMARKETVALUE, ");
            bufSql.append(" FPORTMARKETVALUE, ");
            bufSql.append(" FMVVALUE, ");
            bufSql.append(" FPORTMVVALUE, ");
            bufSql.append(" FFXVALUE, ");
            bufSql.append(" FPORTMARKETVALUERATIO, ");
            bufSql.append(" FGRADETYPE1, ");
            bufSql.append(" FGRADETYPE2, ");
            bufSql.append(" FGRADETYPE3, ");
            bufSql.append(" FGRADETYPE4, ");
            bufSql.append(" FGRADETYPE5, ");
            bufSql.append(" FGRADETYPE6 ");
            bufSql.append(" FROM TB_11172008021836 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            //设定表的主键
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_NAVDATA ADD CONSTRAINT PK_TB_" + sPre +
                           "_Data_Nav " +
                           "PRIMARY KEY (FNAVDATE,FPORTCODE,FORDERCODE,FRETYPECODE,FINVMGRCODE,FKEYCODE)");
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("版本db2 1.0.1.0005 新增表字段出错！", ex);
        } finally {
            bufSql = null; //回收,以清除内存
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //调整字段精度
    public void adjustFieldPrecision(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        String sPKName = "";
        //********************************** 修改 Tb_Base_CalcInsMetic中字段 FFormula 的长度 **********************************//
        try {
            sPKName = this.getIsNullPKByTableName_DB2("TB_BASE_CALCINSMETIC");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_BASE_CALCINSMETIC DROP CONSTRAINT " +
                               sPKName);
            }
            if (dbl.yssTableExist("TB_10242008111153")) {
                this.dropTableByTableName("TB_10242008111153");
            }
            dbl.executeSql(
                "RENAME TABLE TB_BASE_CALCINSMETIC TO TB_10242008111153");
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE TB_BASE_CALCINSMETIC ");
            bufSql.append(" ( ");
            bufSql.append(" FCIMCODE    VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FCIMNAME    VARCHAR(50)   NOT NULL, ");
            bufSql.append(" FCIMTYPE    VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FFORMULA    VARCHAR(2000), ");
            bufSql.append(" FSPICODE    VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FDESC       VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE DECIMAL(1)    NOT NULL, ");
            bufSql.append(" FCREATOR    VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FCREATETIME VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FCHECKUSER  VARCHAR(20), ");
            bufSql.append(" FCHECKTIME  VARCHAR(20) ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            bufSql.append(" INSERT INTO TB_BASE_CALCINSMETIC( ");
            bufSql.append(" FCIMCODE, ");
            bufSql.append(" FCIMNAME, ");
            bufSql.append(" FCIMTYPE, ");
            bufSql.append(" FFORMULA, ");
            bufSql.append(" FSPICODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FCIMCODE, ");
            bufSql.append(" FCIMNAME, ");
            bufSql.append(" FCIMTYPE, ");
            bufSql.append(" FFORMULA, ");
            bufSql.append(" FSPICODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_10242008111153 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bufSql.delete(0, bufSql.length());
            dbl.executeSql(
                "ALTER TABLE TB_BASE_CALCINSMETIC ADD CONSTRAINT PK_Tb_Base_CalcIns " +
                "PRIMARY KEY (FCIMCODE)");

            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("版本db2 1.0.1.0005 执行调整字段精度 SQL 语句出错！", ex);
        } finally {
            bufSql = null; //回收,以清除内存
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
