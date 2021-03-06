package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.*;

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
public class DB21010003sp8
    extends BaseDbUpdate {
    public DB21010003sp8() {
    }

    public void createTable(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        try {
            if (!dbl.yssTableExist("TB_" + sPre + "_Data_FuturesTrade")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_Data_FuturesTrade ");
                bufSql.append(" ( ");
                bufSql.append(" FNum               VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FSecurityCode      VARCHAR(50)   NOT NULL, ");
                bufSql.append(" FPortCode          VARCHAR(50)   NOT NULL, ");
                bufSql.append(" FBrokerCode        VARCHAR(50)   NOT NULL, ");
                bufSql.append(" FInvMgrCode        VARCHAR(50)   NOT NULL, ");
                bufSql.append(" FTradeTypeCode     VARCHAR(50)   NOT NULL, ");
                bufSql.append(" FBegBailAcctCode   VARCHAR(50)   NOT NULL, ");
                bufSql.append(" FChageBailAcctCode VARCHAR(50)   NOT NULL, ");
                bufSql.append(" FBargainDate       DATE          NOT NULL, ");
                bufSql.append(" FBargainTime       VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FSettleDate        DATE          NOT NULL, ");
                bufSql.append(" FSettleTime        VARCHAR(20), ");
                bufSql.append(
                    " FSettleType        DECIMAL(1)    NOT NULL DEFAULT 1, ");
                bufSql.append(" FTradeAmount       DECIMAL(18,4) NOT NULL, ");
                bufSql.append(" FTradePrice        DECIMAL(20,8) NOT NULL, ");
                bufSql.append(" FTradeMoney        DECIMAL(18,4) NOT NULL, ");
                bufSql.append(" FBegBailMoney      DECIMAL(18,4) NOT NULL, ");
                bufSql.append(" FSettleMoney       DECIMAL(18,4) NOT NULL, ");
                bufSql.append(
                    " FSettleState       DECIMAL(1)    NOT NULL DEFAULT 0, ");
                bufSql.append(" FFeeCode1          VARCHAR(20), ");
                bufSql.append(" FTradeFee1         DECIMAL(18,4), ");
                bufSql.append(" FFeeCode2          VARCHAR(20), ");
                bufSql.append(" FTradeFee2         DECIMAL(18,4), ");
                bufSql.append(" FFeeCode3          VARCHAR(20), ");
                bufSql.append(" FTradeFee3         DECIMAL(18,4), ");
                bufSql.append(" FFeeCode4          VARCHAR(20), ");
                bufSql.append(" FTradeFee4         DECIMAL(18,4), ");
                bufSql.append(" FFeeCode5          VARCHAR(20), ");
                bufSql.append(" FTradeFee5         DECIMAL(18,4), ");
                bufSql.append(" FFeeCode6          VARCHAR(20), ");
                bufSql.append(" FTradeFee6         DECIMAL(18,4), ");
                bufSql.append(" FFeeCode7          VARCHAR(20), ");
                bufSql.append(" FTradeFee7         DECIMAL(18,4), ");
                bufSql.append(" FFeeCode8          VARCHAR(20), ");
                bufSql.append(" FTradeFee8         DECIMAL(18,4), ");
                bufSql.append(" FDesc              VARCHAR(20), ");
                bufSql.append(" FCheckState        DECIMAL(1)    NOT NULL, ");
                bufSql.append(" FCreator           VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FCreateTime        VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FCheckUser         VARCHAR(20), ");
                bufSql.append(" FCheckTime         VARCHAR(20), ");
                bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_resTrade");
                bufSql.append(" PRIMARY KEY (FNum) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_Data_FutTradeRela")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_Data_FutTradeRela ");
                bufSql.append(" ( ");
                bufSql.append(" FNum           VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FTsfTypeCode   VARCHAR(50)    NOT NULL, ");
                bufSql.append(" FTransDate     DATE           NOT NULL, ");
                bufSql.append(" FMoney         DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FStorageAmount DECIMAL(18,4), ");
                bufSql.append(" FBaseCuryRate  DECIMAL(20,15) NOT NULL, ");
                bufSql.append(" FBaseCuryMoney DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FPortCuryRate  DECIMAL(20,15) NOT NULL, ");
                bufSql.append(" FPortCuryMoney DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FSettleState   DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FCreator       VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCreateTime    VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCheckUser     VARCHAR(20), ");
                bufSql.append(" FCheckTime     VARCHAR(50), ");
                bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_radeRela");
                bufSql.append(" PRIMARY KEY (FNum,FTsfTypeCode,FTransDate) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_Data_FuturesFillBail")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_Data_FuturesFillBail ");
                bufSql.append(" ( ");
                bufSql.append(" FNum               VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCashInd           DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FCashPortCode      VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FBailPortCode      VARCHAR(50)    NOT NULL, ");
                bufSql.append(" FCashAnalysisCode1 VARCHAR(20), ");
                bufSql.append(" FCashAnalysisCode2 VARCHAR(20), ");
                bufSql.append(" FCashAnalysisCode3 VARCHAR(20), ");
                bufSql.append(" FTransferDate      DATE           NOT NULL, ");
                bufSql.append(" FTransferTime      VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCashAcctCode      VARCHAR(50)    NOT NULL, ");
                bufSql.append(" FBailAcctCode      VARCHAR(50)    NOT NULL, ");
                bufSql.append(" FBailAnalysisCode1 VARCHAR(20), ");
                bufSql.append(" FBailAnalysisCode2 VARCHAR(20), ");
                bufSql.append(" FBailAnalysisCode3 VARCHAR(20), ");
                bufSql.append(" FMoney             DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FBaseCuryRate      DECIMAL(18,15) NOT NULL, ");
                bufSql.append(" FBaseCuryMoney     DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FPortCuryRate      DECIMAL(20,15), ");
                bufSql.append(" FPortCuryMoney     DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FDesc              VARCHAR(200), ");
                bufSql.append(" FCheckState        DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FCreator           VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCreateTime        VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCheckUser         VARCHAR(20), ");
                bufSql.append(" FCheckTime         VARCHAR(20), ");
                bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_FillBail");
                bufSql.append(" PRIMARY KEY (FNum) ");
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
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_DATA_TRADESELLRELA");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_TRADESELLRELA DROP CONSTRAINT " + sPKName);
            }
            if (dbl.yssTableExist("TB_09112008025409")) {
                this.dropTableByTableName("TB_09112008025409");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_DATA_TRADESELLRELA TO TB_09112008025409");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE ");
            bufSql.append("TB_" + sPre + "_DATA_TRADESELLRELA"); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
            bufSql.append(" ( ");
            bufSql.append(" FNUM               VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FTsfTypeCode       VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FSubTsfTypeCode    VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FAPPRECIATION      DECIMAL(18,4) NOT NULL DEFAULT 0, ");
            bufSql.append(" FMAPPRECIATION     DECIMAL(18,4) NOT NULL DEFAULT 0, ");
            bufSql.append(" FVAPPRECIATION     DECIMAL(18,4) NOT NULL DEFAULT 0, ");
            bufSql.append(" FBASEAPPRECIATION  DECIMAL(18,4) NOT NULL DEFAULT 0, ");
            bufSql.append(" FMBASEAPPRECIATION DECIMAL(18,4) NOT NULL DEFAULT 0, ");
            bufSql.append(" FVBASEAPPRECIATION DECIMAL(18,4) NOT NULL DEFAULT 0, ");
            bufSql.append(" FPORTAPPRECIATION  DECIMAL(18,4) NOT NULL DEFAULT 0, ");
            bufSql.append(" FMPORTAPPRECIATION DECIMAL(18,4) NOT NULL, ");
            bufSql.append(" FVPORTAPPRECIATION DECIMAL(18,4) NOT NULL, ");
            bufSql.append(" FREVENUE           DECIMAL(18,4) NOT NULL DEFAULT 0 ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_TRADESELLRELA( "); //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306 BugNO:MS00306
            bufSql.append(" FNUM, ");
            bufSql.append(" FTsfTypeCode, ");
            bufSql.append(" FSubTsfTypeCode, ");
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
            bufSql.append(" FROM TB_09112008025409 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_TRADESELLRELA ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_TRA PRIMARY KEY (FNUM,FTSFTYPECODE,FSUBTSFTYPECODE)");
            //----------------------jc

            //BugNo:0000462 edit by jc
            //对表 Tb_XXX_Data_RateTrade 增加字段 FDataSource
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_Data_RateTrade");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_RateTrade DROP CONSTRAINT " + sPKName);
            }
            if (dbl.yssTableExist("TB_09112008022700")) {
                this.dropTableByTableName("TB_09112008022700");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_Data_RateTrade  TO TB_09112008022700");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_RATETRADE ");
            bufSql.append(" ( ");
            bufSql.append(" FNUM            VARCHAR(15)    NOT NULL, ");
            bufSql.append(" FPORTCODE       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FANALYSISCODE1  VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FANALYSISCODE2  VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FANALYSISCODE3  VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBANALYSISCODE1 VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBANALYSISCODE2 VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBANALYSISCODE3 VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBPORTCODE      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FTRADETYPE      DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCATTYPE        DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FBCASHACCCODE   VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBCURYCODE      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSCURYCODE      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSCASHACCCODE   VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FRECEIVERCODE   VARCHAR(20), ");
            bufSql.append(" FPAYCODE        VARCHAR(20), ");
            bufSql.append(" FTRADEDATE      DATE           NOT NULL, ");
            bufSql.append(" FTRADETIME      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSETTLEDATE     DATE           NOT NULL, ");
            bufSql.append(" FSETTLETIME     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FEXCURYRATE     DECIMAL(30,15) NOT NULL, ");
            bufSql.append(" FBSETTLEDATE    DATE           NOT NULL, ");
            bufSql.append(" FBSETTLETIME    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBMONEY         DECIMAL(30,4)  NOT NULL, ");
            bufSql.append(" FSMONEY         DECIMAL(30,4)  NOT NULL, ");
            bufSql.append(" FBCURYFEE       DECIMAL(18,4), ");
            bufSql.append(" FSCURYFEE       DECIMAL(18,4), ");
            bufSql.append(" FBASEMONEY      DECIMAL(30,4)  NOT NULL, ");
            bufSql.append(" FPORTMONEY      DECIMAL(30,4)  NOT NULL, ");
            bufSql.append(" FLONGCURYRATE   DECIMAL(18,12), ");
            bufSql.append(" FRATEFX         DECIMAL(30,4), ");
            bufSql.append(" FUPDOWN         DECIMAL(18,4), ");
            bufSql.append(" FDataSource     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FDESC           VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE     DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR        VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCREATETIME     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCHECKUSER      VARCHAR(20), ");
            bufSql.append(" FCHECKTIME      VARCHAR(20) ");
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
            bufSql.append(" FDataSource, ");
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
            bufSql.append(" FROM TB_09112008022700 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("Alter Table Tb_" + sPre +
                           "_Data_RateTrade Add Constraint PK_TB_" + sPre +
                           "_teTrade PRIMARY KEY (FNUM) ");
            //-------------------jc
        } catch (Exception ex) {
            throw new YssException("版本DB2010003sp8添加表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
