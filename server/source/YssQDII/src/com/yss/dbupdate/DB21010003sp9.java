package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.Connection;

/**
 * <p>Title: 修改期货的三张表 </p>
 *期货交易表,期货交易关联表,期货保证金变动表
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DB21010003sp9
    extends BaseDbUpdate {
    public DB21010003sp9() {
    }

    //添加表字段
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            // edit by 张旭 2008-9-26
            //对表 TB_XXX_Data_FuturesTrade 增加字段 FBaseCuryRate(基础汇率)，FPortCuryRate(组合汇率)，修改字段 FDesc(描述)
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_DATA_FUTURESTRADE");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_FuturesTrade DROP CONSTRAINT " + sPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_09262008071623000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_09262008071623000");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_Data_FuturesTrade  TO TB_" + sPre +
                           "_DAT_09262008071623000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_Data_FuturesTrade ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM            VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FSECURITYCODE   VARCHAR(50)  NOT NULL, ");
            bufSql.append(" FPORTCODE       VARCHAR(50)  NOT NULL, ");
            bufSql.append(" FBROKERCODE     VARCHAR(50)  NOT NULL, ");
            bufSql.append(" FINVMGRCODE     VARCHAR(50)  NOT NULL, ");
            bufSql.append(" FTRADETYPECODE  VARCHAR(50)  NOT NULL, ");
            bufSql.append(" FBEGBAILACCTCODE  VARCHAR(50)  NOT NULL, ");
            bufSql.append(" FCHAGEBAILACCTCODE VARCHAR(50)  NOT NULL, ");
            bufSql.append(" FBARGAINDATE       DATE          NOT NULL, ");
            bufSql.append(" FBARGAINTIME       VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FSETTLEDATE        DATE          NOT NULL, ");
            bufSql.append(" FSETTLETIME        VARCHAR(20)  , ");
            bufSql.append(
                " FSETTLETYPE        DECIMAL(1)     DEFAULT 1 NOT NULL, ");
            bufSql.append(" FTRADEAMOUNT       DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FTRADEPRICE        DECIMAL(20,8)  NOT NULL, ");
            bufSql.append(" FTRADEMONEY        DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FBEGBAILMONEY      DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FSETTLEMONEY       DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FBASECURYRATE      DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FPORTCURYRATE      DECIMAL(20,15) NOT NULL, ");
            bufSql.append(
                " FSETTLESTATE       DECIMAL(1)     DEFAULT 0 NOT NULL, ");
            bufSql.append(" FFEECODE1          VARCHAR(20)  , ");
            bufSql.append(" FTRADEFEE1         DECIMAL(18,4)  , ");
            bufSql.append(" FFEECODE2          VARCHAR(20)   , ");
            bufSql.append(" FTRADEFEE2         DECIMAL(18,4)  , ");
            bufSql.append(" FFEECODE3          VARCHAR(20)  , ");
            bufSql.append(" FTRADEFEE3         DECIMAL(18,4)  , ");
            bufSql.append(" FFEECODE4          VARCHAR(20)  , ");
            bufSql.append(" FTRADEFEE4         DECIMAL(18,4)  , ");
            bufSql.append(" FFEECODE5          VARCHAR(20)  , ");
            bufSql.append(" FTRADEFEE5         DECIMAL(18,4) , ");
            bufSql.append(" FFEECODE6          VARCHAR(20)  , ");
            bufSql.append(" FTRADEFEE6         DECIMAL(18,4) , ");
            bufSql.append(" FFEECODE7          VARCHAR(20)  , ");
            bufSql.append(" FTRADEFEE7         DECIMAL(18,4) , ");
            bufSql.append(" FFEECODE8          VARCHAR(20)  , ");
            bufSql.append(" FTRADEFEE8         DECIMAL(18,4) , ");
            bufSql.append(" FDESC              VARCHAR(200) , ");
            bufSql.append(" FCHECKSTATE        DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR           VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME        VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER         VARCHAR(20) , ");
            bufSql.append(" FCHECKTIME         VARCHAR(20)  ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_FuturesTrade( ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FTRADETYPECODE, ");
            bufSql.append(" FBEGBAILACCTCODE, ");
            bufSql.append(" FCHAGEBAILACCTCODE, ");
            bufSql.append(" FBARGAINDATE, ");
            bufSql.append(" FBARGAINTIME, ");
            bufSql.append(" FSETTLEDATE, ");
            bufSql.append(" FSETTLETIME, ");
            bufSql.append(" FSETTLETYPE, ");
            bufSql.append(" FTRADEAMOUNT, ");
            bufSql.append(" FTRADEPRICE, ");
            bufSql.append(" FTRADEMONEY, ");
            bufSql.append(" FBEGBAILMONEY, ");
            bufSql.append(" FSETTLEMONEY, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FSETTLESTATE, ");
            bufSql.append(" FFEECODE1, ");
            bufSql.append(" FTRADEFEE1, ");
            bufSql.append(" FFEECODE2, ");
            bufSql.append(" FTRADEFEE2, ");
            bufSql.append(" FFEECODE3, ");
            bufSql.append(" FTRADEFEE3, ");
            bufSql.append(" FFEECODE4, ");
            bufSql.append(" FTRADEFEE4, ");
            bufSql.append(" FFEECODE5, ");
            bufSql.append(" FTRADEFEE5, ");
            bufSql.append(" FFEECODE6, ");
            bufSql.append(" FTRADEFEE6, ");
            bufSql.append(" FFEECODE7, ");
            bufSql.append(" FTRADEFEE7, ");
            bufSql.append(" FFEECODE8, ");
            bufSql.append(" FTRADEFEE8, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FTRADETYPECODE, ");
            bufSql.append(" FBEGBAILACCTCODE, ");
            bufSql.append(" FCHAGEBAILACCTCODE, ");
            bufSql.append(" FBARGAINDATE, ");
            bufSql.append(" FBARGAINTIME, ");
            bufSql.append(" FSETTLEDATE, ");
            bufSql.append(" FSETTLETIME, ");
            bufSql.append(" FSETTLETYPE, ");
            bufSql.append(" FTRADEAMOUNT, ");
            bufSql.append(" FTRADEPRICE, ");
            bufSql.append(" FTRADEMONEY, ");
            bufSql.append(" FBEGBAILMONEY, ");
            bufSql.append(" FSETTLEMONEY, ");
            bufSql.append(" 0, ");
            bufSql.append(" 0, ");
            bufSql.append(" FSETTLESTATE, ");
            bufSql.append(" FFEECODE1, ");
            bufSql.append(" FTRADEFEE1, ");
            bufSql.append(" FFEECODE2, ");
            bufSql.append(" FTRADEFEE2, ");
            bufSql.append(" FFEECODE3, ");
            bufSql.append(" FTRADEFEE3, ");
            bufSql.append(" FFEECODE4, ");
            bufSql.append(" FTRADEFEE4, ");
            bufSql.append(" FFEECODE5, ");
            bufSql.append(" FTRADEFEE5, ");
            bufSql.append(" FFEECODE6, ");
            bufSql.append(" FTRADEFEE6, ");
            bufSql.append(" FFEECODE7, ");
            bufSql.append(" FTRADEFEE7, ");
            bufSql.append(" FFEECODE8, ");
            bufSql.append(" FTRADEFEE8, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_DAT_09262008071623000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("Alter Table Tb_" + sPre +
                           "_DATA_FUTURESTRADE ADD CONSTRAINT PK_TB_" + sPre +
                           "_RESTRADE PRIMARY KEY (FNUM) ");

            //对表 Tb_XXX_Data_FutTradeRela 增加字段 FCloseNum(平仓编号),FBailMoney	(保证金)
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_Data_FutTradeRela");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_FutTradeRela DROP CONSTRAINT " + sPKName);
                deleteIndex(sPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_09262008095425000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_09262008095425000");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_Data_FutTradeRela  TO TB_" + sPre +
                           "_DAT_09262008095425000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_FutTradeRela ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM           VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FCLOSENUM      VARCHAR(20)  DEFAULT ' ' NOT NULL, ");
            bufSql.append(" FTSFTYPECODE   VARCHAR(50)  NOT NULL, ");
            bufSql.append(" FTRANSDATE     DATE          NOT NULL, ");
            bufSql.append(" FMONEY         DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FSTORAGEAMOUNT DECIMAL(18,4)  , ");
            bufSql.append(" FBASECURYRATE  DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FBASECURYMONEY DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FPORTCURYRATE  DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FPORTCURYMONEY DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FBAILMONEY     DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FSETTLESTATE   DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR       VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME    VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER     VARCHAR(20)   , ");
            bufSql.append(" FCHECKTIME     VARCHAR(50)   ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_FutTradeRela( ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FCLOSENUM, ");
            bufSql.append(" FTSFTYPECODE, ");
            bufSql.append(" FTRANSDATE, ");
            bufSql.append(" FMONEY, ");
            bufSql.append(" FSTORAGEAMOUNT, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FBASECURYMONEY, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FPORTCURYMONEY, ");
            bufSql.append(" FBAILMONEY, ");
            bufSql.append(" FSETTLESTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM, ");
            bufSql.append(" ' ', ");
            bufSql.append(" FTSFTYPECODE, ");
            bufSql.append(" FTRANSDATE, ");
            bufSql.append(" FMONEY, ");
            bufSql.append(" FSTORAGEAMOUNT, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FBASECURYMONEY, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FPORTCURYMONEY, ");
            bufSql.append(" 0, ");
            bufSql.append(" FSETTLESTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_DAT_09262008095425000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("Alter Table Tb_" + sPre +
                           "_Data_FutTradeRela Add Constraint PK_TB_" + sPre +
                           "_radeRela PRIMARY KEY (FNUM,FCloseNum,FTSFTYPECODE,FTRANSDATE) ");
            //-------------------张旭

            //edit by  081013
            //对表 TB_XXX_Data_FutureFillBail 增加字段 FTransDate(业务日期)
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_Data_FuturesFillBail");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_FuturesFillBail DROP CONSTRAINT " + sPKName);
            }
            if (dbl.yssTableExist("TB_10132008090700")) {
                this.dropTableByTableName("TB_10132008090700");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_Data_FuturesFillBail TO TB_10132008090700");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_FUTURESFILLBAIL ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM               VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCASHIND           DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCASHPORTCODE      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBAILPORTCODE      VARCHAR(50)    NOT NULL, ");
            bufSql.append(" FCASHANALYSISCODE1 VARCHAR(20), ");
            bufSql.append(" FCASHANALYSISCODE2 VARCHAR(20), ");
            bufSql.append(" FCASHANALYSISCODE3 VARCHAR(20), ");
            bufSql.append(" FTransDate         DATE           NOT NULL, ");
            bufSql.append(" FTRANSFERDATE      DATE           NOT NULL, ");
            bufSql.append(" FTRANSFERTIME      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCASHACCTCODE      VARCHAR(50)    NOT NULL, ");
            bufSql.append(" FBAILACCTCODE      VARCHAR(50)    NOT NULL, ");
            bufSql.append(" FBAILANALYSISCODE1 VARCHAR(20), ");
            bufSql.append(" FBAILANALYSISCODE2 VARCHAR(20), ");
            bufSql.append(" FBAILANALYSISCODE3 VARCHAR(20), ");
            bufSql.append(" FMONEY             DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FBASECURYRATE      DECIMAL(18,15) NOT NULL, ");
            bufSql.append(" FBASECURYMONEY     DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FPORTCURYRATE      DECIMAL(20,15), ");
            bufSql.append(" FPORTCURYMONEY     DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FDESC              VARCHAR(200), ");
            bufSql.append(" FCHECKSTATE        DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR           VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCREATETIME        VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCHECKUSER         VARCHAR(20), ");
            bufSql.append(" FCHECKTIME         VARCHAR(20) ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_FUTURESFILLBAIL( ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FCASHIND, ");
            bufSql.append(" FCASHPORTCODE, ");
            bufSql.append(" FBAILPORTCODE, ");
            bufSql.append(" FCASHANALYSISCODE1, ");
            bufSql.append(" FCASHANALYSISCODE2, ");
            bufSql.append(" FCASHANALYSISCODE3, ");
            bufSql.append(" FTransDate, ");
            bufSql.append(" FTRANSFERDATE, ");
            bufSql.append(" FTRANSFERTIME, ");
            bufSql.append(" FCASHACCTCODE, ");
            bufSql.append(" FBAILACCTCODE, ");
            bufSql.append(" FBAILANALYSISCODE1, ");
            bufSql.append(" FBAILANALYSISCODE2, ");
            bufSql.append(" FBAILANALYSISCODE3, ");
            bufSql.append(" FMONEY, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FBASECURYMONEY, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FPORTCURYMONEY, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FCASHIND, ");
            bufSql.append(" FCASHPORTCODE, ");
            bufSql.append(" FBAILPORTCODE, ");
            bufSql.append(" FCASHANALYSISCODE1, ");
            bufSql.append(" FCASHANALYSISCODE2, ");
            bufSql.append(" FCASHANALYSISCODE3, ");
            bufSql.append(" CURRENT DATE, ");
            bufSql.append(" FTRANSFERDATE, ");
            bufSql.append(" FTRANSFERTIME, ");
            bufSql.append(" FCASHACCTCODE, ");
            bufSql.append(" FBAILACCTCODE, ");
            bufSql.append(" FBAILANALYSISCODE1, ");
            bufSql.append(" FBAILANALYSISCODE2, ");
            bufSql.append(" FBAILANALYSISCODE3, ");
            bufSql.append(" FMONEY, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FBASECURYMONEY, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FPORTCURYMONEY, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_10132008090700 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_FUTURESFILLBAIL ADD CONSTRAINT PK_TB_" + sPre +
                           "_Data_Fut PRIMARY KEY (FNUM)");
            //-----------------jc

            //BugNo:0000480 edit by jc
            //对表 TB_XXX_Data_SubTrade 增加字段 FRateDate(汇率日期)
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_Data_SubTrade");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_SUBTRADE DROP CONSTRAINT " + sPKName);
            }
            if (dbl.yssTableExist("TB_09282008051236")) {
                this.dropTableByTableName("TB_09282008051236");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_DATA_SUBTRADE TO TB_09282008051236");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_SUBTRADE ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM              VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSECURITYCODE     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FPORTCODE         VARCHAR(20), ");
            bufSql.append(" FBROKERCODE       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FINVMGRCODE       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FTRADETYPECODE    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCASHACCCODE      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FATTRCLSCODE      VARCHAR(20)    DEFAULT ' ', ");
            bufSql.append(" FRATEDATE         DATE, ");
            bufSql.append(" FBARGAINDATE      DATE           NOT NULL, ");
            bufSql.append(" FBARGAINTIME      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSETTLEDATE       DATE           NOT NULL, ");
            bufSql.append(" FSETTLETIME       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FMATUREDATE       DATE, ");
            bufSql.append(" FMATURESETTLEDATE DATE, ");
            bufSql.append(" FFACTCASHACCCODE  VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FFACTSETTLEMONEY  DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FEXRATE           DECIMAL(20,15), ");
            bufSql.append(" FFACTBASERATE     DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FFACTPORTRATE     DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FAUTOSETTLE       DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FPORTCURYRATE     DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FBASECURYRATE     DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FALLOTPROPORTION  DECIMAL(18,8)  NOT NULL, ");
            bufSql.append(" FOLDALLOTAMOUNT   DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FALLOTFACTOR      DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FTRADEAMOUNT      DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FTRADEPRICE       DECIMAL(20,8)  NOT NULL, ");
            bufSql.append(" FTRADEMONEY       DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FACCRUEDINTEREST  DECIMAL(18,4), ");
            bufSql.append(" FBAILMONEY        DECIMAL(18,4), ");
            bufSql.append(" FFEECODE1         VARCHAR(20), ");
            bufSql.append(" FTRADEFEE1        DECIMAL(18,4), ");
            bufSql.append(" FFEECODE2         VARCHAR(20), ");
            bufSql.append(" FTRADEFEE2        DECIMAL(18,4), ");
            bufSql.append(" FFEECODE3         VARCHAR(20), ");
            bufSql.append(" FTRADEFEE3        DECIMAL(18,4), ");
            bufSql.append(" FFEECODE4         VARCHAR(20), ");
            bufSql.append(" FTRADEFEE4        DECIMAL(18,4), ");
            bufSql.append(" FFEECODE5         VARCHAR(20), ");
            bufSql.append(" FTRADEFEE5        DECIMAL(18,4), ");
            bufSql.append(" FFEECODE6         VARCHAR(20), ");
            bufSql.append(" FTRADEFEE6        DECIMAL(18,4), ");
            bufSql.append(" FFEECODE7         VARCHAR(20), ");
            bufSql.append(" FTRADEFEE7        DECIMAL(18,4), ");
            bufSql.append(" FFEECODE8         VARCHAR(20), ");
            bufSql.append(" FTRADEFEE8        DECIMAL(18,4), ");
            bufSql.append(" FTOTALCOST        DECIMAL(18,4), ");
            bufSql.append(" FCOST             DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FMCOST            DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FVCOST            DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FBASECURYCOST     DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FMBASECURYCOST    DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FVBASECURYCOST    DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FPORTCURYCOST     DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FMPORTCURYCOST    DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FVPORTCURYCOST    DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FSETTLESTATE      DECIMAL(1)     NOT NULL DEFAULT 0, ");
            bufSql.append(" FFACTSETTLEDATE   DATE, ");
            bufSql.append(" FSETTLEDESC       VARCHAR(100), ");
            bufSql.append(" FORDERNUM         VARCHAR(20), ");
            bufSql.append(" FDATASOURCE       DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FDATABIRTH        VARCHAR(20), ");
            bufSql.append(" FSETTLEORGCODE    VARCHAR(20), ");
            bufSql.append(" FDESC             VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE       DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR          VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCREATETIME       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCHECKUSER        VARCHAR(20), ");
            bufSql.append(" FCHECKTIME        VARCHAR(20) ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_SUBTRADE( ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FTRADETYPECODE, ");
            bufSql.append(" FCASHACCCODE, ");
            bufSql.append(" FATTRCLSCODE, ");
            bufSql.append(" FRATEDATE, ");
            bufSql.append(" FBARGAINDATE, ");
            bufSql.append(" FBARGAINTIME, ");
            bufSql.append(" FSETTLEDATE, ");
            bufSql.append(" FSETTLETIME, ");
            bufSql.append(" FMATUREDATE, ");
            bufSql.append(" FMATURESETTLEDATE, ");
            bufSql.append(" FFACTCASHACCCODE, ");
            bufSql.append(" FFACTSETTLEMONEY, ");
            bufSql.append(" FEXRATE, ");
            bufSql.append(" FFACTBASERATE, ");
            bufSql.append(" FFACTPORTRATE, ");
            bufSql.append(" FAUTOSETTLE, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FALLOTPROPORTION, ");
            bufSql.append(" FOLDALLOTAMOUNT, ");
            bufSql.append(" FALLOTFACTOR, ");
            bufSql.append(" FTRADEAMOUNT, ");
            bufSql.append(" FTRADEPRICE, ");
            bufSql.append(" FTRADEMONEY, ");
            bufSql.append(" FACCRUEDINTEREST, ");
            bufSql.append(" FBAILMONEY, ");
            bufSql.append(" FFEECODE1, ");
            bufSql.append(" FTRADEFEE1, ");
            bufSql.append(" FFEECODE2, ");
            bufSql.append(" FTRADEFEE2, ");
            bufSql.append(" FFEECODE3, ");
            bufSql.append(" FTRADEFEE3, ");
            bufSql.append(" FFEECODE4, ");
            bufSql.append(" FTRADEFEE4, ");
            bufSql.append(" FFEECODE5, ");
            bufSql.append(" FTRADEFEE5, ");
            bufSql.append(" FFEECODE6, ");
            bufSql.append(" FTRADEFEE6, ");
            bufSql.append(" FFEECODE7, ");
            bufSql.append(" FTRADEFEE7, ");
            bufSql.append(" FFEECODE8, ");
            bufSql.append(" FTRADEFEE8, ");
            bufSql.append(" FTOTALCOST, ");
            bufSql.append(" FCOST, ");
            bufSql.append(" FMCOST, ");
            bufSql.append(" FVCOST, ");
            bufSql.append(" FBASECURYCOST, ");
            bufSql.append(" FMBASECURYCOST, ");
            bufSql.append(" FVBASECURYCOST, ");
            bufSql.append(" FPORTCURYCOST, ");
            bufSql.append(" FMPORTCURYCOST, ");
            bufSql.append(" FVPORTCURYCOST, ");
            bufSql.append(" FSETTLESTATE, ");
            bufSql.append(" FFACTSETTLEDATE, ");
            bufSql.append(" FSETTLEDESC, ");
            bufSql.append(" FORDERNUM, ");
            bufSql.append(" FDATASOURCE, ");
            bufSql.append(" FDATABIRTH, ");
            bufSql.append(" FSETTLEORGCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FTRADETYPECODE, ");
            bufSql.append(" FCASHACCCODE, ");
            bufSql.append(" (Case when FATTRCLSCODE is null then ' ' else FATTRCLSCODE end), ");
            bufSql.append(" FBARGAINDATE, ");
            bufSql.append(" FBARGAINDATE, ");
            bufSql.append(" FBARGAINTIME, ");
            bufSql.append(" FSETTLEDATE, ");
            bufSql.append(" FSETTLETIME, ");
            bufSql.append(" FMATUREDATE, ");
            bufSql.append(" FMATURESETTLEDATE, ");
            bufSql.append(" FFACTCASHACCCODE, ");
            bufSql.append(" FFACTSETTLEMONEY, ");
            bufSql.append(" FEXRATE, ");
            bufSql.append(" FFACTBASERATE, ");
            bufSql.append(" FFACTPORTRATE, ");
            bufSql.append(" FAUTOSETTLE, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FALLOTPROPORTION, ");
            bufSql.append(" FOLDALLOTAMOUNT, ");
            bufSql.append(" FALLOTFACTOR, ");
            bufSql.append(" FTRADEAMOUNT, ");
            bufSql.append(" FTRADEPRICE, ");
            bufSql.append(" FTRADEMONEY, ");
            bufSql.append(" FACCRUEDINTEREST, ");
            bufSql.append(" FBAILMONEY, ");
            bufSql.append(" FFEECODE1, ");
            bufSql.append(" FTRADEFEE1, ");
            bufSql.append(" FFEECODE2, ");
            bufSql.append(" FTRADEFEE2, ");
            bufSql.append(" FFEECODE3, ");
            bufSql.append(" FTRADEFEE3, ");
            bufSql.append(" FFEECODE4, ");
            bufSql.append(" FTRADEFEE4, ");
            bufSql.append(" FFEECODE5, ");
            bufSql.append(" FTRADEFEE5, ");
            bufSql.append(" FFEECODE6, ");
            bufSql.append(" FTRADEFEE6, ");
            bufSql.append(" FFEECODE7, ");
            bufSql.append(" FTRADEFEE7, ");
            bufSql.append(" FFEECODE8, ");
            bufSql.append(" FTRADEFEE8, ");
            bufSql.append(" FTOTALCOST, ");
            bufSql.append(" FCOST, ");
            bufSql.append(" FMCOST, ");
            bufSql.append(" FVCOST, ");
            bufSql.append(" FBASECURYCOST, ");
            bufSql.append(" FMBASECURYCOST, ");
            bufSql.append(" FVBASECURYCOST, ");
            bufSql.append(" FPORTCURYCOST, ");
            bufSql.append(" FMPORTCURYCOST, ");
            bufSql.append(" FVPORTCURYCOST, ");
            bufSql.append(" FSETTLESTATE, ");
            bufSql.append(" FFACTSETTLEDATE, ");
            bufSql.append(" FSETTLEDESC, ");
            bufSql.append(" FORDERNUM, ");
            bufSql.append(" FDATASOURCE, ");
            bufSql.append(" FDATABIRTH, ");
            bufSql.append(" FSETTLEORGCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_09282008051236 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_SUBTRADE ADD CONSTRAINT PK_TB_" + sPre +
                           "_Data_Sub PRIMARY KEY (FNUM)");
            //----------------------jc
        } catch (Exception ex) {
            throw new YssException("版本DB2010003sp9添加表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
