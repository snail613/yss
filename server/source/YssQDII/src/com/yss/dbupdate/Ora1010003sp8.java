package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.YssException;

/**
 *
 * <p>Title: 新增期货的三张表</p>
 * 期货交易表,期货交易关联表,与期货保证金处理表
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Ora1010003sp8
    extends BaseDbUpdate {
    public Ora1010003sp8() {
    }

    //创建表
    public void createTable(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        try {
            if (!dbl.yssTableExist("TB_" + sPre + "_DATA_FUTURESTRADE")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_FUTURESTRADE ");
                bufSql.append(" ( ");
                bufSql.append(" FNUM               VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FSECURITYCODE      VARCHAR2(50) NOT NULL, ");
                bufSql.append(" FPORTCODE          VARCHAR2(50) NOT NULL, ");
                bufSql.append(" FBROKERCODE        VARCHAR2(50) NOT NULL, ");
                bufSql.append(" FINVMGRCODE        VARCHAR2(50) NOT NULL, ");
                bufSql.append(" FTRADETYPECODE     VARCHAR2(50) NOT NULL, ");
                bufSql.append(" FBEGBAILACCTCODE   VARCHAR2(50) NOT NULL, ");
                bufSql.append(" FCHAGEBAILACCTCODE VARCHAR2(50) NOT NULL, ");
                bufSql.append(" FBARGAINDATE       DATE         NOT NULL, ");
                bufSql.append(" FBARGAINTIME       VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FSETTLEDATE        DATE         NOT NULL, ");
                bufSql.append(" FSETTLETIME        VARCHAR2(20)     NULL, ");
                bufSql.append(
                    " FSETTLETYPE        NUMBER(1)    DEFAULT 1 NOT NULL, ");
                bufSql.append(" FTRADEAMOUNT       NUMBER(18,4) NOT NULL, ");
                bufSql.append(" FTRADEPRICE        NUMBER(20,8) NOT NULL, ");
                bufSql.append(" FTRADEMONEY        NUMBER(18,4) NOT NULL, ");
                bufSql.append(" FBEGBAILMONEY      NUMBER(18,4) NOT NULL, ");
                bufSql.append(" FSETTLEMONEY       NUMBER(18,4) NOT NULL, ");
                bufSql.append(
                    " FSETTLESTATE       NUMBER(1)    DEFAULT 0 NOT NULL, ");
                bufSql.append(" FFEECODE1          VARCHAR2(20)     NULL, ");
                bufSql.append(" FTRADEFEE1         NUMBER(18,4)     NULL, ");
                bufSql.append(" FFEECODE2          VARCHAR2(20)     NULL, ");
                bufSql.append(" FTRADEFEE2         NUMBER(18,4)     NULL, ");
                bufSql.append(" FFEECODE3          VARCHAR2(20)     NULL, ");
                bufSql.append(" FTRADEFEE3         NUMBER(18,4)     NULL, ");
                bufSql.append(" FFEECODE4          VARCHAR2(20)     NULL, ");
                bufSql.append(" FTRADEFEE4         NUMBER(18,4)     NULL, ");
                bufSql.append(" FFEECODE5          VARCHAR2(20)     NULL, ");
                bufSql.append(" FTRADEFEE5         NUMBER(18,4)     NULL, ");
                bufSql.append(" FFEECODE6          VARCHAR2(20)     NULL, ");
                bufSql.append(" FTRADEFEE6         NUMBER(18,4)     NULL, ");
                bufSql.append(" FFEECODE7          VARCHAR2(20)     NULL, ");
                bufSql.append(" FTRADEFEE7         NUMBER(18,4)     NULL, ");
                bufSql.append(" FFEECODE8          VARCHAR2(20)     NULL, ");
                bufSql.append(" FTRADEFEE8         NUMBER(18,4)     NULL, ");
                bufSql.append(" FDESC              VARCHAR2(20)     NULL, ");
                bufSql.append(" FCHECKSTATE        NUMBER(1)    NOT NULL, ");
                bufSql.append(" FCREATOR           VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FCREATETIME        VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FCHECKUSER         VARCHAR2(20)     NULL, ");
                bufSql.append(" FCHECKTIME         VARCHAR2(20)     NULL, ");
                bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_DATA_FUTURESTRADE ");
                bufSql.append(" PRIMARY KEY (FNUM) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_DATA_FUTTRADERELA")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE  TB_" + sPre + "_DATA_FUTTRADERELA ");
                bufSql.append(" ( ");
                bufSql.append(" FNUM           VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FTSFTYPECODE   VARCHAR2(50)  NOT NULL, ");
                bufSql.append(" FTRANSDATE     DATE          NOT NULL, ");
                bufSql.append(" FMONEY         NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FSTORAGEAMOUNT NUMBER(18,4)      NULL, ");
                bufSql.append(" FBASECURYRATE  NUMBER(20,15) NOT NULL, ");
                bufSql.append(" FBASECURYMONEY NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FPORTCURYRATE  NUMBER(20,15) NOT NULL, ");
                bufSql.append(" FPORTCURYMONEY NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FSETTLESTATE   NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME    VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER     VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME     VARCHAR2(50)      NULL, ");
                bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_DATA_FUTTRADERELA ");
                bufSql.append(" PRIMARY KEY (FNUM,FTSFTYPECODE,FTRANSDATE) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_DATA_FUTURESFILLBAIL")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_FUTURESFILLBAIL ");
                bufSql.append(" ( ");
                bufSql.append(" FNUM               VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCASHIND           NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCASHPORTCODE      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FBAILPORTCODE      VARCHAR2(50)  NOT NULL, ");
                bufSql.append(" FCASHANALYSISCODE1 VARCHAR2(20)      NULL, ");
                bufSql.append(" FCASHANALYSISCODE2 VARCHAR2(20)      NULL, ");
                bufSql.append(" FCASHANALYSISCODE3 VARCHAR2(20)      NULL, ");
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
                bufSql.append(" FCHECKTIME         VARCHAR2(20)      NULL, ");
                bufSql.append(" CONSTRAINT PK_TB_" + sPre +
                              "_DAT_FURTURESFILLBAIL ");
                bufSql.append(" PRIMARY KEY (FNUM) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp8 创建表出错！", ex);
        } finally {
            bufSql = null;
        }
    }

    //添加表字段
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //------------edit by jc
            //对表 Tb_XXX_Data_TradeSellRela 增加字段 FTsfTypeCode,FSubTsfTypeCode
            //并把FSubTsfTypeCode作为主键字段之一
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_DATA_TRADESELLRELA");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_TRADESELLRELA DROP CONSTRAINT " + sPKName);
                this.deleteIndex(sPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_09112008025618000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_09112008025618000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_TRADESELLRELA RENAME TO TB_" + sPre +
                           "_DAT_09112008025618000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_TRADESELLRELA ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM               VARCHAR2(20) NOT NULL, ");
            bufSql.append(" FTSFTYPECODE       VARCHAR2(20) NOT NULL, ");
            bufSql.append(" FSUBTSFTYPECODE    VARCHAR2(20) NOT NULL, ");
            bufSql.append(" FAPPRECIATION      NUMBER(18,4) DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMAPPRECIATION     NUMBER(18,4) DEFAULT 0 NOT NULL, ");
            bufSql.append(" FVAPPRECIATION     NUMBER(18,4) DEFAULT 0 NOT NULL, ");
            bufSql.append(" FBASEAPPRECIATION  NUMBER(18,4) DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMBASEAPPRECIATION NUMBER(18,4) DEFAULT 0 NOT NULL, ");
            bufSql.append(" FVBASEAPPRECIATION NUMBER(18,4) DEFAULT 0 NOT NULL, ");
            bufSql.append(" FPORTAPPRECIATION  NUMBER(18,4) DEFAULT 0 NOT NULL, ");
            bufSql.append(" FMPORTAPPRECIATION NUMBER(18,4) NOT NULL, ");
            bufSql.append(" FVPORTAPPRECIATION NUMBER(18,4) NOT NULL, ");
            bufSql.append(" FREVENUE           NUMBER(18,4) DEFAULT 0 NOT NULL ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_TRADESELLRELA( ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FTSFTYPECODE, ");
            bufSql.append(" FSUBTSFTYPECODE, ");
            bufSql.append(" FAPPRECIATION, ");
            bufSql.append(" FMAPPRECIATION, ");
            bufSql.append(" FVAPPRECIATION, ");
            bufSql.append(" FBASEAPPRECIATION, ");
            bufSql.append(" FMBASEAPPRECIATION, ");
            bufSql.append(" FVBASEAPPRECIATION, ");
            bufSql.append(" FPORTAPPRECIATION, ");
            bufSql.append(" FMPORTAPPRECIATION, ");
            bufSql.append(" FVPORTAPPRECIATION, ");
            bufSql.append(" FREVENUE ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM, ");
            bufSql.append(" ' ', ");
            bufSql.append(" ' ', ");
            bufSql.append(" FAPPRECIATION, ");
            bufSql.append(" FMAPPRECIATION, ");
            bufSql.append(" FVAPPRECIATION, ");
            bufSql.append(" FBASEAPPRECIATION, ");
            bufSql.append(" FMBASEAPPRECIATION, ");
            bufSql.append(" FVBASEAPPRECIATION, ");
            bufSql.append(" FPORTAPPRECIATION, ");
            bufSql.append(" FMPORTAPPRECIATION, ");
            bufSql.append(" FVPORTAPPRECIATION, ");
            bufSql.append(" FREVENUE ");
            bufSql.append(" FROM TB_" + sPre + "_DAT_09112008025618000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_TRADESELLRELA ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_TRADESELLRELA PRIMARY KEY (FNUM,FTSFTYPECODE,FSUBTSFTYPECODE)");
            //----------------------jc

            //BugNo:0000462 edit by jc
            //对表 Tb_XXX_Data_RateTrade 增加字段 FDataSource
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Data_RateTrade");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_RateTrade DROP CONSTRAINT " + sPKName +
                               " CASCADE");
                deleteIndex(sPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_09112008022407000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_09112008022407000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Data_RateTrade RENAME TO TB_" + sPre +
                           "_DAT_09112008022407000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_RATETRADE ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM            VARCHAR2(15)  NOT NULL, ");
            bufSql.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FANALYSISCODE1  VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FANALYSISCODE2  VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FANALYSISCODE3  VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FBANALYSISCODE1 VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FBANALYSISCODE2 VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FBANALYSISCODE3 VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FBPORTCODE      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FTRADETYPE      NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCATTYPE        NUMBER(1)     NOT NULL, ");
            bufSql.append(" FBCASHACCCODE   VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FBCURYCODE      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSCURYCODE      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSCASHACCCODE   VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FRECEIVERCODE   VARCHAR2(20)      NULL, ");
            bufSql.append(" FPAYCODE        VARCHAR2(20)      NULL, ");
            bufSql.append(" FTRADEDATE      DATE          NOT NULL, ");
            bufSql.append(" FTRADETIME      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSETTLEDATE     DATE          NOT NULL, ");
            bufSql.append(" FSETTLETIME     VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FEXCURYRATE     NUMBER(30,15) NOT NULL, ");
            bufSql.append(" FBSETTLEDATE    DATE          NOT NULL, ");
            bufSql.append(" FBSETTLETIME    VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FBMONEY         NUMBER(30,4)  NOT NULL, ");
            bufSql.append(" FSMONEY         NUMBER(30,4)  NOT NULL, ");
            bufSql.append(" FBCURYFEE       NUMBER(18,4)      NULL, ");
            bufSql.append(" FSCURYFEE       NUMBER(18,4)      NULL, ");
            bufSql.append(" FBASEMONEY      NUMBER(30,4)  NOT NULL, ");
            bufSql.append(" FPORTMONEY      NUMBER(30,4)  NOT NULL, ");
            bufSql.append(" FLONGCURYRATE   NUMBER(18,12)     NULL, ");
            bufSql.append(" FRATEFX         NUMBER(30,4)      NULL, ");
            bufSql.append(" FUPDOWN         NUMBER(18,4)      NULL, ");
            bufSql.append(" FDATASOURCE     VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FDESC           VARCHAR2(100)     NULL, ");
            bufSql.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL, ");
            bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_RATETRADE( ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FANALYSISCODE1, ");
            bufSql.append(" FANALYSISCODE2, ");
            bufSql.append(" FANALYSISCODE3, ");
            bufSql.append(" FBANALYSISCODE1, ");
            bufSql.append(" FBANALYSISCODE2, ");
            bufSql.append(" FBANALYSISCODE3, ");
            bufSql.append(" FBPORTCODE, ");
            bufSql.append(" FTRADETYPE, ");
            bufSql.append(" FCATTYPE, ");
            bufSql.append(" FBCASHACCCODE, ");
            bufSql.append(" FBCURYCODE, ");
            bufSql.append(" FSCURYCODE, ");
            bufSql.append(" FSCASHACCCODE, ");
            bufSql.append(" FRECEIVERCODE, ");
            bufSql.append(" FPAYCODE, ");
            bufSql.append(" FTRADEDATE, ");
            bufSql.append(" FTRADETIME, ");
            bufSql.append(" FSETTLEDATE, ");
            bufSql.append(" FSETTLETIME, ");
            bufSql.append(" FEXCURYRATE, ");
            bufSql.append(" FBSETTLEDATE, ");
            bufSql.append(" FBSETTLETIME, ");
            bufSql.append(" FBMONEY, ");
            bufSql.append(" FSMONEY, ");
            bufSql.append(" FBCURYFEE, ");
            bufSql.append(" FSCURYFEE, ");
            bufSql.append(" FBASEMONEY, ");
            bufSql.append(" FPORTMONEY, ");
            bufSql.append(" FLONGCURYRATE, ");
            bufSql.append(" FRATEFX, ");
            bufSql.append(" FUPDOWN, ");
            bufSql.append(" FDATASOURCE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FANALYSISCODE1, ");
            bufSql.append(" FANALYSISCODE2, ");
            bufSql.append(" FANALYSISCODE3, ");
            bufSql.append(" FBANALYSISCODE1, ");
            bufSql.append(" FBANALYSISCODE2, ");
            bufSql.append(" FBANALYSISCODE3, ");
            bufSql.append(" FBPORTCODE, ");
            bufSql.append(" FTRADETYPE, ");
            bufSql.append(" FCATTYPE, ");
            bufSql.append(" FBCASHACCCODE, ");
            bufSql.append(" FBCURYCODE, ");
            bufSql.append(" FSCURYCODE, ");
            bufSql.append(" FSCASHACCCODE, ");
            bufSql.append(" FRECEIVERCODE, ");
            bufSql.append(" FPAYCODE, ");
            bufSql.append(" FTRADEDATE, ");
            bufSql.append(" FTRADETIME, ");
            bufSql.append(" FSETTLEDATE, ");
            bufSql.append(" FSETTLETIME, ");
            bufSql.append(" FEXCURYRATE, ");
            bufSql.append(" FBSETTLEDATE, ");
            bufSql.append(" FBSETTLETIME, ");
            bufSql.append(" FBMONEY, ");
            bufSql.append(" FSMONEY, ");
            bufSql.append(" FBCURYFEE, ");
            bufSql.append(" FSCURYFEE, ");
            bufSql.append(" FBASEMONEY, ");
            bufSql.append(" FPORTMONEY, ");
            bufSql.append(" FLONGCURYRATE, ");
            bufSql.append(" FRATEFX, ");
            bufSql.append(" FUPDOWN, ");
            bufSql.append(" ' ', ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_DAT_09112008022407000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("Alter Table Tb_" + sPre +
                           "_Data_RateTrade Add Constraint PK_TB_" + sPre +
                           "_Data_RateTrade PRIMARY KEY (FNUM) ");
            //-------------------jc
        } catch (Exception ex) {
            throw new YssException("版本Oracle1010003sp8添加表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
