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
public class Ora1010005
    extends BaseDbUpdate {
    public Ora1010005() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        boolean bTrans = false; //代表是否开始事务
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //********************************** TB_PARA_FIXINTEREST  添加字段 FAMORTIZATION,FFACTRATE **********************************//
            if (existsTabColumn_Ora("TB_" + sPre + "_PARA_FIXINTEREST",
                                    "FAMORTIZATION")) {
                strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                    "TB_PARA_FIXINTEREST"));
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE " +
                                   pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                                   " DROP CONSTRAINT " + strPKName + " CASCADE");
                    deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
                }
                if (dbl.yssTableExist("TB_" + sPre + "_PAR_10242008025210000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                    this.dropTableByTableName("TB_" + sPre +
                                              "_PAR_10242008025210000");
                }
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                               " RENAME TO TB_" + sPre + "_PAR_10242008025210000");
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE " +
                              pub.yssGetTableName("TB_PARA_FIXINTEREST"));
                bufSql.append(" (");
                bufSql.append("FSECURITYCODE     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSTARTDATE        DATE          NOT NULL,");
                bufSql.append("FISSUEDATE        DATE          NOT NULL,");
                bufSql.append("FISSUEPRICE       NUMBER(18,4)  NOT NULL,");
                bufSql.append("FINSSTARTDATE     DATE          NOT NULL,");
                bufSql.append("FINSENDDATE       DATE          NOT NULL,");
                bufSql.append("FINSCASHDATE      DATE          NOT NULL,");
                bufSql.append("FFACEVALUE        NUMBER(18,12) NOT NULL,");
                bufSql.append("FFACERATE         NUMBER(18,12)     NULL,");
                bufSql.append("FINSFREQUENCY     NUMBER(18,4)  NOT NULL,");
                bufSql.append("FQUOTEWAY         NUMBER(1)     NOT NULL,");
                bufSql.append("FCREDITLEVEL      VARCHAR2(20)      NULL,");
                bufSql.append("FCALCINSMETICDAY  VARCHAR2(20)      NULL,");
                bufSql.append("FCALCINSMETICBUY  VARCHAR2(20)      NULL,");
                bufSql.append("FCALCINSMETICSELL VARCHAR2(20)      NULL,");
                bufSql.append("FCALCPRICEMETIC   VARCHAR2(20)      NULL,");
                bufSql.append("FAMORTIZATION     VARCHAR2(20)      NULL,");
                bufSql.append("FFACTRATE         NUMBER(18,4)      NULL,");
                bufSql.append("FCALCINSCFGDAY    VARCHAR2(500)     NULL,");
                bufSql.append("FCALCINSCFGBUY    VARCHAR2(500)     NULL,");
                bufSql.append("FCALCINSCFGSELL   VARCHAR2(500)     NULL,");
                bufSql.append("FCALCINSWAY       NUMBER(1)     NOT NULL,");
                bufSql.append("FINTERESTORIGIN   NUMBER(1)     NOT NULL,");
                bufSql.append("FPEREXPCODE       VARCHAR2(20)      NULL,");
                bufSql.append("FPERIODCODE       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FROUNDCODE        VARCHAR2(20)  NOT NULL,");
                bufSql.append("FDESC             VARCHAR2(100)     NULL,");
                bufSql.append("FCHECKSTATE       NUMBER(1)     NOT NULL,");
                bufSql.append("FCREATOR          VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCREATETIME       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCHECKUSER        VARCHAR2(20)      NULL,");
                bufSql.append("FCHECKTIME        VARCHAR2(20)      NULL");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                bufSql.append("INSERT INTO " +
                              pub.yssGetTableName("TB_PARA_FIXINTEREST") + "(");
                bufSql.append("FSECURITYCODE,");
                bufSql.append("FSTARTDATE,");
                bufSql.append("FISSUEDATE,");
                bufSql.append("FISSUEPRICE,");
                bufSql.append("FINSSTARTDATE,");
                bufSql.append("FINSENDDATE,");
                bufSql.append("FINSCASHDATE,");
                bufSql.append("FFACEVALUE,");
                bufSql.append("FFACERATE,");
                bufSql.append("FINSFREQUENCY,");
                bufSql.append("FQUOTEWAY,");
                bufSql.append("FCREDITLEVEL,");
                bufSql.append("FCALCINSMETICDAY,");
                bufSql.append("FCALCINSMETICBUY,");
                bufSql.append("FCALCINSMETICSELL,");
                bufSql.append("FCALCPRICEMETIC,");
                bufSql.append("FAMORTIZATION,");
                bufSql.append("FFACTRATE,");
                bufSql.append("FCALCINSCFGDAY,");
                bufSql.append("FCALCINSCFGBUY,");
                bufSql.append("FCALCINSCFGSELL,");
                bufSql.append("FCALCINSWAY,");
                bufSql.append("FINTERESTORIGIN,");
                bufSql.append("FPEREXPCODE,");
                bufSql.append("FPERIODCODE,");
                bufSql.append("FROUNDCODE,");
                bufSql.append("FDESC,");
                bufSql.append("FCHECKSTATE,");
                bufSql.append("FCREATOR,");
                bufSql.append("FCREATETIME,");
                bufSql.append("FCHECKUSER,");
                bufSql.append("FCHECKTIME");
                bufSql.append(")");
                bufSql.append("SELECT ");
                bufSql.append("FSECURITYCODE,");
                bufSql.append("FSTARTDATE,");
                bufSql.append("FISSUEDATE,");
                bufSql.append("FISSUEPRICE,");
                bufSql.append("FINSSTARTDATE,");
                bufSql.append("FINSENDDATE,");
                bufSql.append("FINSCASHDATE,");
                bufSql.append("FFACEVALUE,");
                bufSql.append("FFACERATE,");
                bufSql.append("FINSFREQUENCY,");
                bufSql.append("FQUOTEWAY,");
                bufSql.append("FCREDITLEVEL,");
                bufSql.append("FCALCINSMETICDAY,");
                bufSql.append("FCALCINSMETICBUY,");
                bufSql.append("FCALCINSMETICSELL,");
                bufSql.append("FCALCPRICEMETIC,");
                bufSql.append("NULL,");
                bufSql.append("NULL,");
                bufSql.append("FCALCINSCFGDAY,");
                bufSql.append("FCALCINSCFGBUY,");
                bufSql.append("FCALCINSCFGSELL,");
                bufSql.append("FCALCINSWAY,");
                bufSql.append("FINTERESTORIGIN,");
                bufSql.append("FPEREXPCODE,");
                bufSql.append("FPERIODCODE,");
                bufSql.append("FROUNDCODE,");
                bufSql.append("FDESC,");
                bufSql.append("FCHECKSTATE,");
                bufSql.append("FCREATOR,");
                bufSql.append("FCREATETIME,");
                bufSql.append("FCHECKUSER,");
                bufSql.append("FCHECKTIME");
                bufSql.append(" FROM TB_" + sPre + "_PAR_10242008025210000");
                conn.setAutoCommit(bTrans);
                bTrans = true;
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                               " ADD CONSTRAINT PK_TB_" + sPre +
                               "_PARA_FIXINTEREST " +
                               " PRIMARY KEY (FSECURITYCODE,FSTARTDATE)");
            }
            /**
             * date   : 20081112
             * author : sunkey
             * desc   : 为组合群表tb_sys_assetgroup添加‘用户可以审核自己录入的数据’(FCheckSelf)字段
             * BugID  : MS00010
             */
            if (existsTabColumn_Ora("TB_SYS_ASSETGROUP", "FCHECKSELF")) {
                bTrans = true; //标识事物回滚状态 如果中途出现异常，回滚事物
                //开始事物处理
                conn.setAutoCommit(false);
                //如果有主键 删除主键（相关约束）
                if (! (strPKName = getIsNullPKByTableName_Ora("TB_SYS_ASSETGROUP")).trim().equals("")) {
                    dbl.executeSql("ALTER TABLE TB_SYS_ASSETGROUP DROP CONSTRAINT " + strPKName);
                    //2008-11-19 蒋锦 添加 删除索引
                    deleteIndex(strPKName);
                }
                //将原表备份 如果备份的表存在 删除
                if (dbl.yssTableExist("TB_SYS_ASS_11122008113519000")) {
                    dbl.executeSql("DROP TABLE TB_SYS_ASS_11122008113519000");
                }
                //更新表名
                dbl.executeSql("ALTER TABLE TB_SYS_ASSETGROUP RENAME TO TB_SYS_ASS_11122008113519000");
                //创建表 -- 添加字段FCheckSelf
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE TB_SYS_ASSETGROUP(");
                bufSql.append("FASSETGROUPCODE  VARCHAR2(3)   NOT NULL,");
                bufSql.append("FASSETGROUPNAME  VARCHAR2(100) NOT NULL,");
                bufSql.append("FMAXNUM          NUMBER(38)    NOT NULL,");
                bufSql.append("FSTARTDATE       DATE          NOT NULL,");
                bufSql.append("FBASECURY        VARCHAR2(20)  NOT NULL,");
                bufSql.append("FBASERATESRCCODE VARCHAR2(20)  NOT NULL,");
                bufSql.append("FBASERATECODE    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FPORTRATESRCCODE VARCHAR2(20)  NOT NULL,");
                bufSql.append("FPORTRATECODE    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FTABIND          NUMBER(1)     DEFAULT 0     NULL,");
                bufSql.append("FTABPREFIX       VARCHAR2(6)       NULL,");
                bufSql.append("FLOCKED          NUMBER(38)    DEFAULT 0 NOT NULL,");
                bufSql.append("FSYSCHECK        NUMBER(1)     DEFAULT 0 NOT NULL,");
                bufSql.append("FCHECKSELF       VARCHAR2(20)  DEFAULT 'yes' NOT NULL,");
                bufSql.append("FDESC            VARCHAR2(100)     NULL)");
                dbl.executeSql(bufSql.toString());
                //插入数据 采用insert into ... select ...
                bufSql = new StringBuffer();
                bufSql.append("INSERT INTO TB_SYS_ASSETGROUP(");
                bufSql.append("FASSETGROUPCODE,FASSETGROUPNAME,FMAXNUM,FSTARTDATE,FBASECURY,FBASERATESRCCODE,FBASERATECODE,");
                bufSql.append("FPORTRATESRCCODE,FPORTRATECODE,FTABIND,FTABPREFIX,FLOCKED,FSYSCHECK,FCheckSelf,FDESC)");
                bufSql.append("SELECT FASSETGROUPCODE,FASSETGROUPNAME,FMAXNUM,FSTARTDATE,FBASECURY,FBASERATESRCCODE,FBASERATECODE,");
                bufSql.append("FPORTRATESRCCODE,FPORTRATECODE,FTABIND,FTABPREFIX,FLOCKED,FSYSCHECK,'yes',FDESC FROM TB_SYS_ASS_11122008113519000");
                dbl.executeSql(bufSql.toString());
                //添加主键
                dbl.executeSql("ALTER TABLE TB_SYS_ASSETGROUP ADD CONSTRAINT PK_TB_SYS_ASSETGROUP PRIMARY KEY (FASSETGROUPCODE)");
                conn.commit();
                bTrans = false;
            }

            // linjunyun 需求MS00016 2008-11-13
            //******************* 更新数据库中的表，给表 TB_SYS_USERLIST 添加字段 FPASSLEVEL和FVALIDTIME；****************//
            if (existsTabColumn_Ora("TB_SYS_USERLIST", "FPASSLEVEL,FVALIDTIME")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                strPKName = this.getIsNullPKByTableName_Ora("TB_SYS_USERLIST");
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_SYS_USERLIST DROP CONSTRAINT " +
                                   strPKName);
                    deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
                }
                if (dbl.yssTableExist("TB_SYS_USE_11122008105353000")) {
                    this.dropTableByTableName("TB_SYS_USE_11122008105353000");
                }
                dbl.executeSql(
                    "ALTER TABLE TB_SYS_USERLIST RENAME TO TB_SYS_USE_11122008105353000");
                bufSql.append(" CREATE TABLE TB_SYS_USERLIST ");
                bufSql.append(" ( ");
                bufSql.append(" FUSERCODE      VARCHAR2(20)   NOT NULL, ");
                bufSql.append(" FUSERNAME      VARCHAR2(50)   NOT NULL, ");
                bufSql.append(" FPASS          RAW(200)           NULL, ");
                bufSql.append(" FPASSLEN       NUMBER(38)         NULL, ");
                bufSql.append(" FPASSCOUNT     NUMBER(38)         NULL, ");
                bufSql.append(" FPASSDATE      DATE               NULL, ");
                bufSql.append(" FLOCKED        NUMBER(38)     DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMENUSCODE     VARCHAR2(3000)     NULL, ");
                bufSql.append(" FMENUBARSCODE  VARCHAR2(3000)     NULL, ");
                bufSql.append(" FPORTGROUPCODE VARCHAR2(1000)     NULL, ");
                bufSql.append(" FDEPTCODE      VARCHAR2(20)       NULL, ");
                bufSql.append(" FPOSITIONCODE  VARCHAR2(20)       NULL, ");
                bufSql.append(" FPASSLEVEL     VARCHAR2(200)      NULL, ");
                bufSql.append(" FVALIDTIME     NUMBER(38)     DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMEMO          VARCHAR2(200)      NULL ");
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
                bufSql.append(" FPASSLEVEL, ");
                bufSql.append(" FVALIDTIME, ");
                bufSql.append(" FMEMO ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
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
                bufSql.append(" NULL, ");
                bufSql.append(" 0, ");
                bufSql.append(" FMEMO ");
                bufSql.append(" FROM TB_SYS_USE_11122008105353000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql(
                    "ALTER TABLE TB_SYS_USERLIST ADD CONSTRAINT PK_TB_SYS_USERLIST " +
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
            if (existsTabColumn_Ora(tabName, "FBalF")) {
                bTrans = true; //标识事物回滚状态 如果中途出现异常，回滚事物
                //开始事物处理
                conn.setAutoCommit(false);
                //如果有主键 删除主键（相关约束）
                if (! (strPKName = getIsNullPKByTableName_Ora(tabName)).trim().equals("")) {
                    dbl.executeSql("ALTER TABLE " + tabName + " DROP CONSTRAINT " + strPKName);
                    //2008-11-19 蒋锦 添加 删除索引
                    deleteIndex(strPKName);
                }
                //将原表备份 如果备份的表存在 删除
                if (dbl.yssTableExist("TB_" + sPre + "_STO_11132008104128000")) {
                    dbl.executeSql("DROP TABLE " + "TB_" + sPre + "_STO_11132008104128000");
                }
                //更新表名
                dbl.executeSql("ALTER TABLE " + tabName + " RENAME TO TB_" + sPre + "_STO_11132008104128000");
                //创建表
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE ").append(tabName).append("(");
                bufSql.append("FYEARMONTH      VARCHAR2(6)   NOT NULL,");
                bufSql.append("FSTORAGEDATE    DATE          NOT NULL,");
                bufSql.append("FPORTCODE       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSECURITYCODE   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FTSFTYPECODE    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSUBTSFTYPECODE VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCATTYPE        VARCHAR2(20)  NOT NULL,");
                bufSql.append("FATTRCLSCODE    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCURYCODE       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FBAL            NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FMBAL           NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FVBAL           NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FBASECURYBAL    NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FMBASECURYBAL   NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FVBASECURYBAL   NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FPORTCURYBAL    NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FMPORTCURYBAL   NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FVPORTCURYBAL   NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FBALF           NUMBER(30,15) DEFAULT 0 NOT NULL,");
                bufSql.append("FPORTCURYBALF   NUMBER(30,15) DEFAULT 0 NOT NULL,");
                bufSql.append("FBASECURYBALF   NUMBER(30,15) DEFAULT 0 NOT NULL,");
                bufSql.append("FSTORAGEIND     NUMBER(1)     NOT NULL,");
                bufSql.append("FCHECKSTATE     NUMBER(1)     NOT NULL,");
                bufSql.append("FCREATOR        VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCREATETIME     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCHECKUSER      VARCHAR2(20)      NULL,");
                bufSql.append("FCHECKTIME      VARCHAR2(20)      NULL)");
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
                bufSql.append("FANALYSISCODE1, FANALYSISCODE2,FANALYSISCODE3,FSECURITYCODE, FTSFTYPECODE,");
                bufSql.append("FSUBTSFTYPECODE, FCATTYPE,FATTRCLSCODE, FCURYCODE,");
                bufSql.append("FBAL, FMBAL, FVBAL,FBASECURYBAL, FMBASECURYBAL,");
                bufSql.append("FVBASECURYBAL, FPORTCURYBAL,FMPORTCURYBAL, FVPORTCURYBAL,");
                bufSql.append("FBAL,FPORTCURYBAL,FBASECURYBAL,FSTORAGEIND,FCHECKSTATE, FCREATOR,");
                bufSql.append("FCREATETIME,FCHECKUSER,FCHECKTIME FROM TB_" + sPre + "_STO_11132008104128000 ");
                dbl.executeSql(bufSql.toString());
                //添加主键
                bufSql = new StringBuffer();
                bufSql.append("ALTER TABLE ").append(tabName).append(" ADD CONSTRAINT PK_TB_" + sPre + "_STOCK_SECRECPAY ");
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
            if (existsTabColumn_Ora(tabName, "FMoneyF")) {
                bTrans = true; //标识事物回滚状态 如果中途出现异常，回滚事物
                //开始事物处理
                conn.setAutoCommit(false);
                //如果有主键 删除主键（相关约束）
                if (! (strPKName = getIsNullPKByTableName_Ora(tabName)).trim().equals("")) {
                    dbl.executeSql("ALTER TABLE " + tabName + " DROP CONSTRAINT " + strPKName);
                    //2008-11-19 蒋锦 添加 删除索引
                    deleteIndex(strPKName);
                }
                //将原表备份 如果备份的表存在 删除
                if (dbl.yssTableExist("TB_" + sPre + "_DAT_11132008103854000")) {
                    dbl.executeSql("DROP TABLE " + "TB_" + sPre + "_DAT_11132008103854000");
                }
                //更新表名
                dbl.executeSql("ALTER TABLE " + tabName + " RENAME TO TB_" + sPre + "_DAT_11132008103854000");
                //创建表
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE ").append(tabName).append("(");
                bufSql.append("FNUM            VARCHAR2(20)  NOT NULL,");
                bufSql.append("FDESC           VARCHAR2(100)     NULL,");
                bufSql.append("FTRANSDATE      DATE          NOT NULL,");
                bufSql.append("FPORTCODE       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSECURITYCODE   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FTSFTYPECODE    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSUBTSFTYPECODE VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCATTYPE        VARCHAR2(20)  NOT NULL,");
                bufSql.append("FATTRCLSCODE    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCURYCODE       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FINOUT          NUMBER(1)     NOT NULL,");
                bufSql.append("FMONEY          NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FMMONEY         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FVMONEY         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FBASECURYRATE   NUMBER(20,15) NOT NULL,");
                bufSql.append("FBASECURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FMBASECURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FVBASECURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FPORTCURYRATE   NUMBER(20,15) NOT NULL,");
                bufSql.append("FPORTCURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FMPORTCURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FVPORTCURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
                bufSql.append("FMONEYF         NUMBER(30,15) DEFAULT 0 NOT NULL,");
                bufSql.append("FBASECURYMONEYF NUMBER(30,15) DEFAULT 0 NOT NULL,");
                bufSql.append("FPORTCURYMONEYF NUMBER(30,15) DEFAULT 0 NOT NULL,");
                bufSql.append("FDATASOURCE     NUMBER(1)     NOT NULL,");
                bufSql.append("FSTOCKIND       NUMBER(1)     NOT NULL,");
                bufSql.append("FCHECKSTATE     NUMBER(1)     NOT NULL,");
                bufSql.append("FCREATOR        VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCREATETIME     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCHECKUSER      VARCHAR2(20)      NULL,");
                bufSql.append("FCHECKTIME      VARCHAR2(20)      NULL)");
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
                bufSql.append("FCHECKUSER,FCHECKTIME FROM TB_" + sPre + "_DAT_11132008103854000");
                dbl.executeSql(bufSql.toString());
                //添加主键
                bufSql = new StringBuffer();
                dbl.executeSql("ALTER TABLE " + tabName + " ADD CONSTRAINT PK_TB_" + sPre + "_DATA_SECRECPAY PRIMARY KEY (FNUM)");
                conn.commit();
                bTrans = false;
            }

            // linjunyun 2008-11-14 报表数据源设置里增加存储表
            //******************* 更新数据库中的表，给表 TB_001_REP_DATASOURCE 添加字段 FSTORAGETAB；****************//
            if (existsTabColumn_Ora("TB_" + sPre + "_REP_DATASOURCE",
                                    "FSTORAGETAB")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                    "TB_REP_DATASOURCE"));
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE " +
                                   pub.yssGetTableName("TB_REP_DATASOURCE") +
                                   " DROP CONSTRAINT " + strPKName);
                    deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
                }
                if (dbl.yssTableExist("TB_" + sPre + "_REP_11032008074209000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                    this.dropTableByTableName("TB_" + sPre +
                                              "_REP_11032008074209000");
                }
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_REP_DATASOURCE") +
                               " RENAME TO TB_" + sPre + "_REP_11032008074209000");
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE " +
                              pub.yssGetTableName("TB_REP_DATASOURCE"));
                bufSql.append(" (");
                bufSql.append("FREPDSCODE  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FREPDSNAME  VARCHAR2(50)  NOT NULL,");
                bufSql.append("FDSTYPE     NUMBER(1)     NOT NULL,");
                bufSql.append("FTROWCOLOR  NUMBER(10)    DEFAULT 0 NOT NULL,");
                bufSql.append("FBROWCOLOR  NUMBER(10)    DEFAULT 0 NOT NULL,");
                bufSql.append("FTEMPTAB    VARCHAR2(20)      NULL,");
                bufSql.append("FSTORAGETAB VARCHAR2(20)      NULL,");
                bufSql.append("FBEANID     VARCHAR2(30)      NULL,");
                bufSql.append("FDATASOURCE CLOB              NULL,");
                bufSql.append("FFILLRANGE  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FDESC       VARCHAR2(100)     NULL,");
                bufSql.append("FCHECKSTATE NUMBER(1)     NOT NULL,");
                bufSql.append("FCREATOR    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCREATETIME VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCHECKUSER  VARCHAR2(20)      NULL,");
                bufSql.append("FCHECKTIME  VARCHAR2(20)      NULL");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                bufSql.append("INSERT INTO " +
                              pub.yssGetTableName("TB_REP_DATASOURCE") + "(");
                bufSql.append("FREPDSCODE,");
                bufSql.append("FREPDSNAME,");
                bufSql.append("FDSTYPE,");
                bufSql.append("FTROWCOLOR,");
                bufSql.append("FBROWCOLOR,");
                bufSql.append("FTEMPTAB,");
                bufSql.append("FSTORAGETAB,");
                bufSql.append("FBEANID,");
                bufSql.append("FDATASOURCE,");
                bufSql.append("FFILLRANGE,");
                bufSql.append("FDESC,");
                bufSql.append("FCHECKSTATE,");
                bufSql.append("FCREATOR,");
                bufSql.append("FCREATETIME,");
                bufSql.append("FCHECKUSER,");
                bufSql.append("FCHECKTIME");
                bufSql.append(")");
                bufSql.append("SELECT ");
                bufSql.append("FREPDSCODE,");
                bufSql.append("FREPDSNAME,");
                bufSql.append("FDSTYPE,");
                bufSql.append("FTROWCOLOR,");
                bufSql.append("FBROWCOLOR,");
                bufSql.append("FTEMPTAB,");
                bufSql.append("NULL,");
                bufSql.append("FBEANID,");
                bufSql.append("FDATASOURCE,");
                bufSql.append("FFILLRANGE,");
                bufSql.append("FDESC,");
                bufSql.append("FCHECKSTATE,");
                bufSql.append("FCREATOR,");
                bufSql.append("FCREATETIME,");
                bufSql.append("FCHECKUSER,");
                bufSql.append("FCHECKTIME");
                bufSql.append(" FROM TB_" + sPre + "_REP_11032008074209000");
                conn.setAutoCommit(bTrans);
                bTrans = true;
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_REP_DATASOURCE") +
                               " ADD CONSTRAINT PK_TB_" + sPre +
                               "_REP_DATASOURCE " +
                               " PRIMARY KEY (FREPDSCODE)");
            }
            if (existsTabColumn_Ora("TB_SYS_USERLIST", "FPASSLEVEL")
                && existsTabColumn_Ora("TB_SYS_USERLIST", "FVALIDTIME")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                strPKName = this.getIsNullPKByTableName_Ora("TB_SYS_USERLIST");
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_SYS_USERLIST DROP CONSTRAINT " + strPKName);
                    deleteIndex(strPKName);
                }
                if (dbl.yssTableExist("TB_SYS_USE_11122008105353000")) {
                    this.dropTableByTableName("TB_SYS_USE_11122008105353000");
                }
                dbl.executeSql("ALTER TABLE TB_SYS_USERLIST RENAME TO TB_SYS_USE_11122008105353000");
                bufSql.append(" CREATE TABLE TB_SYS_USERLIST ");
                bufSql.append(" ( ");
                bufSql.append(" FUSERCODE      VARCHAR2(20)   NOT NULL, ");
                bufSql.append(" FUSERNAME      VARCHAR2(50)   NOT NULL, ");
                bufSql.append(" FPASS          RAW(200)           NULL, ");
                bufSql.append(" FPASSLEN       NUMBER(38)         NULL, ");
                bufSql.append(" FPASSCOUNT     NUMBER(38)         NULL, ");
                bufSql.append(" FPASSDATE      DATE               NULL, ");
                bufSql.append(" FLOCKED        NUMBER(38)     DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMENUSCODE     VARCHAR2(3000)     NULL, ");
                bufSql.append(" FMENUBARSCODE  VARCHAR2(3000)     NULL, ");
                bufSql.append(" FPORTGROUPCODE VARCHAR2(1000)     NULL, ");
                bufSql.append(" FDEPTCODE      VARCHAR2(20)       NULL, ");
                bufSql.append(" FPOSITIONCODE  VARCHAR2(20)       NULL, ");
                bufSql.append(" FPASSLEVEL     VARCHAR2(200)      NULL, ");
                bufSql.append(" FVALIDTIME     NUMBER(38)     DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMEMO          VARCHAR2(200)      NULL ");
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
                bufSql.append(" FPASSLEVEL, ");
                bufSql.append(" FVALIDTIME, ");
                bufSql.append(" FMEMO ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
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
                bufSql.append(" NULL, ");
                bufSql.append(" 0, ");
                bufSql.append(" FMEMO ");
                bufSql.append(" FROM TB_SYS_USE_11122008105353000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_SYS_USERLIST ADD CONSTRAINT PK_TB_SYS_USERLIST " +
                               " PRIMARY KEY (FUSERCODE)");
            }

            /**
             * date   : 20081117
             * author : linjunyun
             * desc   : 业务数据--净值数据表更改表 TB_001_DATA_NAVDATA 字段的长度
             *          FSEDOLCODE VARCHAR2(20) 改为 FSEDOLCODE VARCHAR2(50)
             *          FISINCODE VARCHAR2(20) 改为 FISINCODE VARCHAR2(50)
             * BugID  :
             */
            bTrans = false;
            bufSql.delete(0, bufSql.length());
            //获得主键
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_DATA_NAVDATA"));
            //判断主键是否存在，存在则删除主键
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_DATA_NAVDATA") +
                               " DROP CONSTRAINT " + strPKName);
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_11172008021703000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_11172008021703000");
            }
            //重命名原有表为临时表
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_NAVDATA") +
                           " RENAME TO TB_" + sPre + "_DAT_11172008021703000");
            bufSql.delete(0, bufSql.length());
            //创建包含新字段的表
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_DATA_NAVDATA"));
            bufSql.append(" ( ");
            bufSql.append(" FNAVDATE              DATE          NOT NULL, ");
            bufSql.append(" FPORTCODE             VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FORDERCODE            VARCHAR2(200) NOT NULL, ");
            bufSql.append(" FRETYPECODE           VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FINVMGRCODE           VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FKEYCODE              VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FINOUT                NUMBER(1)     DEFAULT 1 NOT NULL, ");
            bufSql.append(" FKEYNAME              VARCHAR2(200) NOT NULL, ");
            bufSql.append(" FDETAIL               NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCURYCODE             VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FPRICE                NUMBER(20,12)     NULL, ");
            bufSql.append(" FOTPRICE1             NUMBER(20,12)     NULL, ");
            bufSql.append(" FOTPRICE2             NUMBER(20,12)     NULL, ");
            bufSql.append(" FOTPRICE3             NUMBER(20,12)     NULL, ");
            bufSql.append(" FSEDOLCODE            VARCHAR2(50)      NULL, ");
            bufSql.append(" FISINCODE             VARCHAR2(50)      NULL, ");
            bufSql.append(" FSPARAMT              NUMBER(20,4)      NULL, ");
            bufSql.append(" FBASECURYRATE         NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FPORTCURYRATE         NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FCOST                 NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTCOST             NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMARKETVALUE          NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTMARKETVALUE      NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMVVALUE              NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTMVVALUE          NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FFXVALUE              NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTMARKETVALUERATIO NUMBER(18,4)      NULL, ");
            bufSql.append(" FGRADETYPE1           VARCHAR2(20)      NULL, ");
            bufSql.append(" FGRADETYPE2           VARCHAR2(20)      NULL, ");
            bufSql.append(" FGRADETYPE3           VARCHAR2(20)      NULL, ");
            bufSql.append(" FGRADETYPE4           VARCHAR2(20)      NULL, ");
            bufSql.append(" FGRADETYPE5           VARCHAR2(20)      NULL, ");
            bufSql.append(" FGRADETYPE6           VARCHAR2(20)      NULL ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //从临时表中导数据到新创建的表中
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_DATA_NAVDATA") + "(");
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
            bufSql.append(" FROM TB_" + sPre + "_DAT_11172008021703000");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //设定表的主键
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_NAVDATA") +
                           " ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_NAVDATA " +
                           " PRIMARY KEY (FNAVDATE,FPORTCODE,FORDERCODE,FRETYPECODE,FINVMGRCODE,FKEYCODE)");

        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0005 新增表字段出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //调整字段精度
    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        //********************************** 修改 Tb_Base_CalcInsMetic中字段 FFormula 的长度 **********************************//
        try {
            dbl.executeSql(
                "ALTER TABLE TB_BASE_CALCINSMETIC MODIFY(FFORMULA  VARCHAR2(2000))");
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0005 执行调整字段精度 SQL 语句出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
