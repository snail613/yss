package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.YssException;

/**
 * <p>Title: 修改期货的三张表</p>
 * 期货交易表,期货交易关联表,期货保证金变动表
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Ora1010003sp9
    extends BaseDbUpdate {
    public Ora1010003sp9() {
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
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Data_FuturesTrade");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_FuturesTrade DROP CONSTRAINT " + sPKName +
                               " CASCADE");
                deleteIndex(sPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_09262008071623000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_09262008071623000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Data_FuturesTrade RENAME TO TB_" + sPre +
                           "_DAT_09262008071623000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_Data_FuturesTrade ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM            VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSECURITYCODE   VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FPORTCODE       VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FBROKERCODE     VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FINVMGRCODE     VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FTRADETYPECODE  VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FBEGBAILACCTCODE  VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FCHAGEBAILACCTCODE VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FBARGAINDATE       DATE          NOT NULL, ");
            bufSql.append(" FBARGAINTIME       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSETTLEDATE        DATE          NOT NULL, ");
            bufSql.append(" FSETTLETIME        VARCHAR2(20)      NULL, ");
            bufSql.append(" FSETTLETYPE        NUMBER(1)     DEFAULT 1 NOT NULL, ");
            bufSql.append(" FTRADEAMOUNT       NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FTRADEPRICE        NUMBER(20,8)  NOT NULL, ");
            bufSql.append(" FTRADEMONEY        NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FBEGBAILMONEY      NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FSETTLEMONEY       NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FBASECURYRATE      NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FPORTCURYRATE      NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FSETTLESTATE       NUMBER(1)     DEFAULT 0 NOT NULL, ");
            bufSql.append(" FFEECODE1          VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE1         NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE2          VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE2         NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE3          VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE3         NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE4          VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE4         NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE5          VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE5         NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE6          VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE6         NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE7          VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE7         NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE8          VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE8         NUMBER(18,4)      NULL, ");
            bufSql.append(" FDESC              VARCHAR2(200)     NULL, ");
            bufSql.append(" FCHECKSTATE        NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREATOR           VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME        VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER         VARCHAR2(20)      NULL, ");
            bufSql.append(" FCHECKTIME         VARCHAR2(20)      NULL ");
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
                           "_Data_FUTURESTRADE Add Constraint PK_TB_" + sPre +
                           "_Data_FUTURESTRADE PRIMARY KEY (FNUM) ");

            //对表 Tb_XXX_Data_FutTradeRela 增加字段 FCloseNum(平仓编号),FBailMoney	(保证金)
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Data_FutTradeRela");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_FutTradeRela DROP CONSTRAINT " + sPKName +
                               " CASCADE");
                deleteIndex(sPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_09262008095425000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_09262008095425000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Data_FutTradeRela RENAME TO TB_" + sPre +
                           "_DAT_09262008095425000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_FutTradeRela ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM           VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCLOSENUM      VARCHAR2(20)  DEFAULT ' ' NOT NULL, ");
            bufSql.append(" FTSFTYPECODE   VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FTRANSDATE     DATE          NOT NULL, ");
            bufSql.append(" FMONEY         NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FSTORAGEAMOUNT NUMBER(18,4)      NULL, ");
            bufSql.append(" FBASECURYRATE  NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FBASECURYMONEY NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FPORTCURYRATE  NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FPORTCURYMONEY NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FBAILMONEY     NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FSETTLESTATE   NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREATOR       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME    VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER     VARCHAR2(20)      NULL, ");
            bufSql.append(" FCHECKTIME     VARCHAR2(50)      NULL ");
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
                           "_Data_FutTradeRela PRIMARY KEY (FNUM,FCloseNum,FTsfTypeCode,FTransDate) ");
            //-------------------张旭

            //edit by jc 081013
            //对表 TB_XXX_Data_FutureFillBail 增加字段 FTransDate(业务日期)
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Data_FuturesFillBail");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_FuturesFillBail DROP CONSTRAINT " + sPKName);
                deleteIndex(sPKName); //删除索引
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_10132008090328000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_10132008090328000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Data_FuturesFillBail RENAME TO TB_" + sPre +
                           "_DAT_10132008090328000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_FUTURESFILLBAIL ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM               VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCASHIND           NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCASHPORTCODE      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FBAILPORTCODE      VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FCASHANALYSISCODE1 VARCHAR2(20)      NULL, ");
            bufSql.append(" FCASHANALYSISCODE2 VARCHAR2(20)      NULL, ");
            bufSql.append(" FCASHANALYSISCODE3 VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRANSDATE         DATE          NOT NULL, ");
            bufSql.append(" FTRANSFERDATE      DATE          NOT NULL, ");
            bufSql.append(" FTRANSFERTIME      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCASHACCTCODE      VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FBAILACCTCODE      VARCHAR2(50)  NOT NULL, ");
            bufSql.append(" FBAILANALYSISCODE1 VARCHAR2(20)      NULL, ");
            bufSql.append(" FBAILANALYSISCODE2 VARCHAR2(20)      NULL, ");
            bufSql.append(" FBAILANALYSISCODE3 VARCHAR2(20)      NULL, ");
            bufSql.append(" FMONEY             NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FBASECURYRATE      NUMBER(18,15) NOT NULL, ");
            bufSql.append(" FBASECURYMONEY     NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FPORTCURYRATE      NUMBER(20,15)     NULL, ");
            bufSql.append(" FPORTCURYMONEY     NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FDESC              VARCHAR2(200)     NULL, ");
            bufSql.append(" FCHECKSTATE        NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREATOR           VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME        VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER         VARCHAR2(20)      NULL, ");
            bufSql.append(" FCHECKTIME         VARCHAR2(20)      NULL ");
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
            bufSql.append(" FTRANSDATE, ");
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
            bufSql.append(" SYSDATE, ");
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
            bufSql.append(" FROM TB_" + sPre + "_DAT_10132008090328000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_FUTURESFILLBAIL ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_FUTURESFILLBAIL PRIMARY KEY (FNUM)");
            //------------------jc

            //BugNo:0000480 edit by jc
            //对表 TB_XXX_Data_SubTrade 增加字段 FRateDate(汇率日期)
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Data_SubTrade");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_SUBTRADE DROP CONSTRAINT " + sPKName);
                deleteIndex(sPKName); //删除索引
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_09282008033258000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_09282008033258000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_SUBTRADE RENAME TO TB_" + sPre +
                           "_DAT_09282008033258000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_SUBTRADE ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM              VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSECURITYCODE     VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FPORTCODE         VARCHAR2(20)      NULL, ");
            bufSql.append(" FBROKERCODE       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FINVMGRCODE       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FTRADETYPECODE    VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCASHACCCODE      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FATTRCLSCODE      VARCHAR2(20)  DEFAULT ' ' NULL, ");
            bufSql.append(" FRATEDATE         DATE              NULL, ");
            bufSql.append(" FBARGAINDATE      DATE          NOT NULL, ");
            bufSql.append(" FBARGAINTIME      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSETTLEDATE       DATE          NOT NULL, ");
            bufSql.append(" FSETTLETIME       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FMATUREDATE       DATE              NULL, ");
            bufSql.append(" FMATURESETTLEDATE DATE              NULL, ");
            bufSql.append(" FFACTCASHACCCODE  VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FFACTSETTLEMONEY  NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FEXRATE           NUMBER(20,15)     NULL, ");
            bufSql.append(" FFACTBASERATE     NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FFACTPORTRATE     NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FAUTOSETTLE       NUMBER(1)     NOT NULL, ");
            bufSql.append(" FPORTCURYRATE     NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FBASECURYRATE     NUMBER(20,15) NOT NULL, ");
            bufSql.append(" FALLOTPROPORTION  NUMBER(18,8)  NOT NULL, ");
            bufSql.append(" FOLDALLOTAMOUNT   NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FALLOTFACTOR      NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FTRADEAMOUNT      NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FTRADEPRICE       NUMBER(20,8)  NOT NULL, ");
            bufSql.append(" FTRADEMONEY       NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FACCRUEDINTEREST  NUMBER(18,4)      NULL, ");
            bufSql.append(" FBAILMONEY        NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE1         VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE1        NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE2         VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE2        NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE3         VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE3        NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE4         VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE4        NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE5         VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE5        NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE6         VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE6        NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE7         VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE7        NUMBER(18,4)      NULL, ");
            bufSql.append(" FFEECODE8         VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEFEE8        NUMBER(18,4)      NULL, ");
            bufSql.append(" FTOTALCOST        NUMBER(18,4)      NULL, ");
            bufSql.append(" FCOST             NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMCOST            NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FVCOST            NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FBASECURYCOST     NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMBASECURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FVBASECURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTCURYCOST     NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMPORTCURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FVPORTCURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
            bufSql.append(" FSETTLESTATE      NUMBER(1)     DEFAULT 0 NOT NULL, ");
            bufSql.append(" FFACTSETTLEDATE   DATE              NULL, ");
            bufSql.append(" FSETTLEDESC       VARCHAR2(100)     NULL, ");
            bufSql.append(" FORDERNUM         VARCHAR2(20)      NULL, ");
            bufSql.append(" FDATASOURCE       NUMBER(1)     NOT NULL, ");
            bufSql.append(" FDATABIRTH        VARCHAR2(20)      NULL, ");
            bufSql.append(" FSETTLEORGCODE    VARCHAR2(20)      NULL, ");
            bufSql.append(" FDESC             VARCHAR2(100)     NULL, ");
            bufSql.append(" FCHECKSTATE       NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREATOR          VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER        VARCHAR2(20)      NULL, ");
            bufSql.append(" FCHECKTIME        VARCHAR2(20)      NULL ");
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
            bufSql.append(" NULL, ");
            bufSql.append(" FSETTLEORGCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_DAT_09282008033258000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_SUBTRADE ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_SUBTRADE PRIMARY KEY (FNUM)");
            //----------------------jc
        } catch (Exception ex) {
            throw new YssException("版本Oracle1010003sp9添加表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
