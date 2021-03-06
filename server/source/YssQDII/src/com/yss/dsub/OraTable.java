package com.yss.dsub;

import java.sql.*;
import com.yss.util.*;
import com.yss.dsub.*;
import java.util.*;

/**
 *
 * <p>Title: OraTable </p>
 * <p>Description: Oracle 数据库处理</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class OraTable
    extends BaseBean {
    private DbBase dbl = null; //DbBase类实例
    public OraTable() {
    }

    public OraTable(DbBase db) {
        dbl = db;
    }

    public void setYssPub(YssPub ysspub) {
        pub = ysspub;
        dbl = ysspub.getDbLink();
    }

    public String getVersion(String sPre) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sVersion = "1.0.0.0000";
        try {
            strSql = "select " +
                dbl.sqlIsNull("max(FVERNUM)", dbl.sqlString(sVersion)) +
                " as FVersion from TB_FUN_VERSION " +
                "where FAssetGroupCode =" + dbl.sqlString(sPre);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sVersion = rs.getString("FVersion");
            }
            return sVersion;
        } catch (Exception e) {
            throw new YssException("获取数据库版本信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void updateVersion1A0A0A0001(String sPre) throws YssException {
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String strSql = "";
        String sKey = "";
        try {
            conn.setAutoCommit(false);
            strSql = "select * from TB_" + sPre + "_PARA_CASHACCOUNT where 1 = 1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FAccAttr")) {

                sKey = dbl.getConstaintKey("TB_" + sPre + "_PARA_CASHACCOUNT");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_PARA_CASHACCOUNT DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_PARA_CASHACCOUNT RENAME TO TB_" +
                    sPre + "_PAR_1109200704072600A";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Para_CashAccount(" +
                    "FCASHACCCODE    VARCHAR2(20)  NOT NULL," +
                    "FSTARTDATE      DATE          NOT NULL," +
                    "FCASHACCNAME    VARCHAR2(100) NOT NULL," +
                    "FACCTYPE        VARCHAR2(20)  NOT NULL," +
                    "FSUBACCTYPE     VARCHAR2(20)      NULL," +
                    "FBANKCODE       VARCHAR2(20)      NULL," +
                    "FCURYCODE       VARCHAR2(20)  NOT NULL," +
                    "FBANKACCOUNT    VARCHAR2(50)      NULL," +
                    "FMATUREDATE     DATE              NULL," +
                    "FSTATE          NUMBER(1)     NOT NULL," +
                    "FPORTCODE       VARCHAR2(20)      NULL," +
                    "FFORMULACODE    VARCHAR2(20)      NULL," +
                    "FINTERESTWAY    NUMBER(1)     NOT NULL," +
                    "FFIXRATE        NUMBER(18,12)     NULL," +
                    "FROUNDCODE      VARCHAR2(20)      NULL," +
                    "FPERIODCODE     VARCHAR2(20)      NULL," +
                    "FINTERESTCYCLE  NUMBER(1)         NULL," +
                    "FINTERESTORIGIN NUMBER(1)         NULL," +
                    "FDEPDURCODE     VARCHAR2(20)      NULL," +
                    "FAccAttr        NUMBER(1)     DEFAULT 1 NOT NULL," +
                    "FDESC           VARCHAR2(200)     NULL," +
                    "FCHECKSTATE     NUMBER(1)     NOT NULL," +
                    "FCREATOR        VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME     VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER      VARCHAR2(20)      NULL," +
                    "FCHECKTIME      VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);
                //-- Insert Data SQL

                strSql = "INSERT INTO Tb_" + sPre + "_Para_CashAccount(" +
                    "FCASHACCCODE," +
                    "FSTARTDATE," +
                    "FCASHACCNAME," +
                    "FACCTYPE," +
                    "FSUBACCTYPE," +
                    "FBANKCODE," +
                    "FCURYCODE," +
                    "FBANKACCOUNT," +
                    "FMATUREDATE," +
                    "FSTATE," +
                    "FPORTCODE," +
                    "FFORMULACODE," +
                    "FINTERESTWAY," +
                    "FFIXRATE," +
                    "FROUNDCODE," +
                    "FPERIODCODE," +
                    "FINTERESTCYCLE," +
                    "FINTERESTORIGIN," +
                    "FDEPDURCODE," +
                    "FAccAttr," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FCASHACCCODE," +
                    "FSTARTDATE," +
                    "FCASHACCNAME," +
                    "FACCTYPE," +
                    "FSUBACCTYPE," +
                    "FBANKCODE," +
                    "FCURYCODE," +
                    "FBANKACCOUNT," +
                    "FMATUREDATE," +
                    "FSTATE," +
                    "FPORTCODE," +
                    "FFORMULACODE," +
                    "FINTERESTWAY," +
                    "FFIXRATE," +
                    "FROUNDCODE," +
                    "FPERIODCODE," +
                    "FINTERESTCYCLE," +
                    "FINTERESTORIGIN," +
                    "FDEPDURCODE," +
                    "1," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_PAR_1109200704072600A";
                dbl.executeSql(strSql);

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Para_CashAccount ADD CONSTRAINT PK_TB_" + sPre +
                    "_PARA_CASHACCOUNT " +
                    "PRIMARY KEY (FCASHACCCODE,FSTARTDATE)";
                dbl.executeSql(strSql);
            }

            if (!dbl.yssTableExist("Tb_" + sPre + "_TA_Assign")) {
                strSql = "CREATE TABLE Tb_" + sPre + "_TA_Assign(" +
                    "FAssignDate    DATE          NOT NULL," +
                    "FPortCode      VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode1 VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode2 VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode3 VARCHAR2(20)  NOT NULL," +
                    "FPortClsCode   VARCHAR2(20)  NOT NULL," +
                    "FAssginScale   NUMBER(18,12) DEFAULT 1     NULL," +
                    "FCalcWay       VARCHAR2(20)  DEFAULT 'Common' NOT NULL," +
                    "FDesc          VARCHAR2(100)     NULL," +
                    "FCreator       VARCHAR2(20)  NOT NULL," +
                    "FCreateTime    VARCHAR2(20)  NOT NULL," +
                    "FCheckState    NUMBER(1)     NOT NULL," +
                    "FCheckUser     VARCHAR2(20)      NULL," +
                    "FCheckTime     VARCHAR2(20)      NULL," +
                    "CONSTRAINT PK_Tb_" + sPre + "_TA_Assign PRIMARY KEY (FAssignDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FPortClsCode))";
                dbl.executeSql(strSql);
            }
            dbl.closeResultSetFinal(rs); //close the cursor modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
            strSql = "SELECT * FROM TB_FUN_SPINGINVOKE WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FFormCode")) {
                strSql =
                    "ALTER TABLE TB_FUN_SPINGINVOKE RENAME TO TB_FUN_SPI_1111200704072800A";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE TB_FUN_SPINGINVOKE(" +
                    "FSICode     VARCHAR2(20)  NOT NULL," +
                    "FSINAME     VARCHAR2(50)  NOT NULL," +
                    "FBEANID     VARCHAR2(50)  NOT NULL," +
                    "FRETURNTYPE VARCHAR2(20)  NOT NULL," +
                    "FFormCode   VARCHAR2(20)      NULL," +
                    "FCtlName    VARCHAR2(30)      NULL," +
                    "FMODULECODE VARCHAR2(20)  NOT NULL," +
                    "FPARAMS     VARCHAR2(300)     NULL," +
                    "FDESC       VARCHAR2(500)     NULL)";
                dbl.executeSql(strSql);

                //-- Insert Data SQL

                strSql = "INSERT INTO TB_FUN_SPINGINVOKE(" +
                    "FSICode," +
                    "FSINAME," +
                    "FBEANID," +
                    "FRETURNTYPE," +
                    "FFormCode," +
                    "FCtlName," +
                    "FMODULECODE," +
                    "FPARAMS," +
                    "FDESC)" +
                    " SELECT FSICODE," +
                    "FSINAME," +
                    "FBEANID," +
                    "FRETURNTYPE," +
                    "' '," +
                    "' '," +
                    "FMODULECODE," +
                    "FPARAMS," +
                    "FDESC" +
                    " FROM TB_FUN_SPI_1111200704072800A";
                dbl.executeSql(strSql);
            }
            dbl.closeResultSetFinal(rs); //close the cursor modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B

            strSql = "SELECT * FROM TB_" + sPre + "_DATA_RIGHTSISSUE WHERE 1= 1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FEndTradeDate")) {

                sKey = dbl.getConstaintKey("TB_" + sPre + "_DATA_RIGHTSISSUE");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_DATA_RIGHTSISSUE DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_DATA_RIGHTSISSUE RENAME TO TB_" +
                    sPre + "_DAT_1111200705082200A";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_RightsIssue( " +
                    "FSECURITYCODE   VARCHAR2(20)  NOT NULL," +
                    "FRECORDDATE     DATE          NOT NULL," +
                    "FTSecurityCode  VARCHAR2(20)      NULL," +
                    "FEXRIGHTDATE    DATE          NOT NULL," +
                    "FEXPIRATIONDATE DATE          NOT NULL," +
                    "FAfficheDate    DATE          NOT NULL," +
                    "FPayDate        DATE          NOT NULL," +
                    "FBeginScriDate  DATE          NOT NULL," +
                    "FEndScriDate    DATE          NOT NULL," +
                    "FBeginTradeDate DATE          NOT NULL," +
                    "FEndTradeDate   DATE          NOT NULL," +
                    "FRATIO          NUMBER(18,8)  NOT NULL," +
                    "FRIPRICE        NUMBER(18,4)  NOT NULL," +
                    "FROUNDCODE      VARCHAR2(20)  NOT NULL," +
                    "FDESC           VARCHAR2(100)     NULL," +
                    "FCHECKSTATE     NUMBER(1)     NOT NULL," +
                    "FCREATOR        VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME     VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER      VARCHAR2(20)      NULL," +
                    "FCHECKTIME      VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Data_RightsIssue(" +
                    "FSECURITYCODE," +
                    "FRECORDDATE," +
                    "FTSecurityCode," +
                    "FEXRIGHTDATE," +
                    "FEXPIRATIONDATE," +
                    "FAfficheDate," +
                    "FPayDate," +
                    "FBeginScriDate," +
                    "FEndScriDate," +
                    "FBeginTradeDate," +
                    "FEndTradeDate," +
                    "FRATIO," +
                    "FRIPRICE," +
                    "FROUNDCODE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    "SELECT FSECURITYCODE," +
                    "FRECORDDATE," +
                    "null," +
                    "FEXRIGHTDATE," +
                    "FEXPIRATIONDATE," +
                    "NVL(FAFFICHEDATE,SYSDATE)," +
                    "NVL(FPAYDATE,SYSDATE)," +
                    "SYSDATE," +
                    "SYSDATE," +
                    "SYSDATE," +
                    "SYSDATE," +
                    "FRATIO," +
                    "FRIPRICE," +
                    "FROUNDCODE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_DAT_1111200705082200A";
                dbl.executeSql(strSql);
            }

            if (!dbl.yssTableExist("Tb_" + sPre + "_Data_ForwardTrade")) {
                strSql = "CREATE TABLE Tb_" + sPre + "_Data_ForwardTrade (" +
                    "FNum            VARCHAR2(15)  NOT NULL," +
                    "FSecurityCode   VARCHAR2(20)  NOT NULL," +
                    "FPortCode       VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode1  VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode2  VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode3  VARCHAR2(20)  NOT NULL," +
                    "FTradeDate      DATE          NOT NULL," +
                    "FTradeTime      VARCHAR2(20)  NOT NULL," +
                    "FMatureDate     DATE          NOT NULL," +
                    "FSettleDate     DATE          NOT NULL," +
                    "FSettleTime     VARCHAR2(20)  NOT NULL," +
                    "FFeeMoney1      NUMBER(18,4)      NULL," +
                    "FFeeMoney2      NUMBER(18,4)      NULL," +
                    "FFeeMoney3      NUMBER(18,4)      NULL," +
                    "FTradeAmount    NUMBER(18,4)  NOT NULL," +
                    "FTradePrice     NUMBER(18,4)  NOT NULL," +
                    "FTrustPrice     NUMBER(18,4)  NOT NULL," +
                    "FMatureMoney    NUMBER(18,4)  NOT NULL," +
                    "FSettleMoney    NUMBER(18,4)  NOT NULL," +
                    "FBailMoney      NUMBER(18,4)      NULL," +
                    "FBAccDesc       VARCHAR2(300) NOT NULL," +
                    "FSAccDesc       VARCHAR2(300) NOT NULL," +
                    "FBailAccDesc    VARCHAR2(300)     NULL," +
                    "FBailOutAccDesc VARCHAR2(300)     NULL," +
                    "FBailInAccDesc  VARCHAR2(300)     NULL," +
                    "FFeeAccDesc1    VARCHAR2(300)     NULL," +
                    "FFeeAccDesc2    VARCHAR2(300)     NULL," +
                    "FFeeAccDesc3    VARCHAR2(300)     NULL," +
                    "FDesc           VARCHAR2(100)     NULL," +
                    "FCheckState     NUMBER(1)     NOT NULL," +
                    "FCreator        VARCHAR2(20)  NOT NULL," +
                    "FCreateTime     VARCHAR2(20)  NOT NULL," +
                    "FCheckUser      VARCHAR2(20)      NULL," +
                    "FCheckTime      VARCHAR2(20)      NULL," +
                    "CONSTRAINT PK_Tb_" + sPre +
                    "_Data_ForwardTrade PRIMARY KEY (FNum))";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_ForwardTradeAcc(" +
                    "FNum           VARCHAR2(20) NOT NULL," +
                    "FAccType       VARCHAR2(20) NOT NULL," +
                    "FPortCode      VARCHAR2(20) NOT NULL," +
                    "FAnalysisCode1 VARCHAR2(20) NOT NULL," +
                    "FAnalysisCode2 VARCHAR2(20) NOT NULL," +
                    "FAnalysisCode3 VARCHAR2(20) NOT NULL," +
                    "FCashAccCode   VARCHAR2(20) NOT NULL," +
                    "CONSTRAINT PK_Tb_" + sPre +
                    "_Data_ForwardTradeAcc PRIMARY KEY (FNum,FAccType))";
                dbl.executeSql(strSql);
            }

            if (!dbl.yssTableExist("Tb_" + sPre + "_Data_SecExchangeOut")) {
                strSql = "CREATE TABLE Tb_" + sPre + "_Data_SecExchangeOut(" +
                    "FNum               VARCHAR2(20)  NOT NULL," +
                    "FInSecNum          VARCHAR2(20)  NOT NULL," +
                    "FSecurityCode      VARCHAR2(20)  NOT NULL," +
                    "FPortCode          VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode1     VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode2     VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode3     VARCHAR2(20)  NOT NULL," +
                    "FOutAmount         NUMBER(18,4)  NOT NULL," +
                    "FExchangeCost      NUMBER(18,4)  NOT NULL," +
                    "FMExchangeCost     NUMBER(18,4)  NOT NULL," +
                    "FVExchangeCost     NUMBER(18,4)  NOT NULL," +
                    "FPortExchangeCost  NUMBER(18,4)  NOT NULL," +
                    "FMPortExchangeCost NUMBER(18,4)  NOT NULL," +
                    "FVPortExchangeCost NUMBER(18,4)  NOT NULL," +
                    "FBaseExchangeCost  NUMBER(18,4)  NOT NULL," +
                    "FMBaseExchangeCost NUMBER(18,4)  NOT NULL," +
                    "FVBaseExchangeCost NUMBER(18,4)  NOT NULL," +
                    "FDesc              VARCHAR2(100)     NULL," +
                    "FCheckState        NUMBER(1)     NOT NULL," +
                    "FCreator           VARCHAR2(20)  NOT NULL," +
                    "FCreateTime        VARCHAR2(20)  NOT NULL," +
                    "FCheckUser         VARCHAR2(20)      NULL," +
                    "FCheckTime         VARCHAR2(20)      NULL," +
                    "CONSTRAINT PK_Tb_" + sPre +
                    "_Data_SecExchangeOut PRIMARY KEY (FNum,FInSecNum))";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_SecExchangeIn(" +
                    "FNum               VARCHAR2(20)  NOT NULL," +
                    "FExchangeDate      DATE          NOT NULL," +
                    "FSecurityCode      VARCHAR2(20)  NOT NULL," +
                    "FTradeTypeCode     VARCHAR2(20)  NOT NULL," +
                    "FPortCode          VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode1     VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode2     VARCHAR2(20)  NOT NULL," +
                    "FAnalysisCode3     VARCHAR2(20)  NOT NULL," +
                    "FInAmount          NUMBER(18,4)  NOT NULL," +
                    "FExchangeCost      NUMBER(18,4)  NOT NULL," +
                    "FMExchangeCost     NUMBER(18,4)  NOT NULL," +
                    "FVExchangeCost     NUMBER(18,4)  NOT NULL," +
                    "FPortExchangeCost  NUMBER(18,4)  NOT NULL," +
                    "FMPortExchangeCost NUMBER(18,4)  NOT NULL," +
                    "FVPortExchangeCost NUMBER(18,4)  NOT NULL," +
                    "FBaseExchangeCost  NUMBER(18,4)  NOT NULL," +
                    "FMBaseExchangeCost NUMBER(18,4)  NOT NULL," +
                    "FVBaseExchangeCost NUMBER(18,4)  NOT NULL," +
                    "FBaseCuryRate      NUMBER(18,15) NOT NULL," +
                    "FPortCuryRate      NUMBER(18,15) NOT NULL," +
                    "FDataSource        NUMBER(1)     NOT NULL," +
                    "FDesc              VARCHAR2(100)     NULL," +
                    "FCheckState        NUMBER(1)     NOT NULL," +
                    "FCreator           VARCHAR2(20)  NOT NULL," +
                    "FCreateTime        VARCHAR2(20)  NOT NULL," +
                    "FCheckUser         VARCHAR2(20)      NULL," +
                    "FCheckTime         VARCHAR2(20)      NULL," +
                    "CONSTRAINT PK_Tb_" + sPre +
                    "_Data_SecExchangeIn PRIMARY KEY (FNum))";
                dbl.executeSql(strSql);
            }

            if (!dbl.yssTableExist("Tb_" + sPre + "_Para_Forward")) {
                strSql = "CREATE TABLE Tb_" + sPre + "_Para_Forward (" +
                    "FSecurityCode VARCHAR2(20)  NOT NULL," +
                    "FTrustRate    NUMBER(18,12) NOT NULL," +
                    "FSaleCury     VARCHAR2(20)  NOT NULL," +
                    "FBuyCury      VARCHAR2(20)  NOT NULL," +
                    "FDepDurCode   VARCHAR2(20)  NOT NULL," +
                    "FDesc         VARCHAR2(100)     NULL," +
                    "FCheckState   NUMBER(1)     NOT NULL," +
                    "FCreator      VARCHAR2(20)  NOT NULL," +
                    "FCreateTime   VARCHAR2(20)  NOT NULL," +
                    "FCheckUser    VARCHAR2(20)      NULL," +
                    "FCheckTime    VARCHAR2(20)      NULL," +
                    "CONSTRAINT PK_Tb_" + sPre +
                    "_Para_Forward PRIMARY KEY (FSecurityCode))";
                dbl.executeSql(strSql);
            }
            if (!dbl.yssTableExist("Tb_" + sPre + "_Para_IndexFutures")) {
                strSql = "CREATE TABLE Tb_" + sPre + "_Para_IndexFutures(" +
                    "FSecurityCode VARCHAR2(20)  NOT NULL," +
                    "FIndexCode    VARCHAR2(20)  NOT NULL," +
                    "FDepDurCode   VARCHAR2(20)  NOT NULL," +
                    "FMultiple     NUMBER(18,4)  NOT NULL," +
                    "FBailType     VARCHAR2(20)  NOT NULL," +
                    "FBailScale    NUMBER(18,12)     NULL," +
                    "FBailFix      NUMBER(18,4)      NULL," +
                    "FBeginBail    NUMBER(18,4)      NULL," +
                    "FDesc         VARCHAR2(100)     NULL," +
                    "FCheckState   NUMBER(1)     NOT NULL," +
                    "FCreator      VARCHAR2(20)  NOT NULL," +
                    "FCreateTime   VARCHAR2(20)  NOT NULL," +
                    "FCheckUser    VARCHAR2(20)      NULL," +
                    "FCheckTime    VARCHAR2(20)      NULL," +
                    "CONSTRAINT PK_Tb_" + sPre +
                    "_Para_IndexFutures PRIMARY KEY (FSecurityCode))";
                dbl.executeSql(strSql);
            }
            dbl.closeResultSetFinal(rs); //close the cursor modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B

            strSql = "SELECT * FROM TB_" + sPre + "_DATA_VALMKTPRICE WHERE 1= 1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FOTPrice1")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_DATA_VALMKTPRICE");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_DATA_VALMKTPRICE DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_DATA_VALMKTPRICE RENAME TO TB_" +
                    sPre + "_DAT_1111200714562700A";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_ValMktPrice(" +
                    "FVALDATE      DATE          NOT NULL," +
                    "FPORTCODE     VARCHAR2(20)  NOT NULL," +
                    "FSECURITYCODE VARCHAR2(20)  NOT NULL," +
                    "FPrice        NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice1     NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice2     NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice3     NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FCHECKSTATE   NUMBER(1)     NOT NULL," +
                    "FCREATOR      VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME   VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER    VARCHAR2(20)      NULL," +
                    "FCHECKTIME    VARCHAR2(20)      NULL," +
                    "CONSTRAINT PK_Tb_" + sPre +
                    "_Data_ValMktPrice PRIMARY KEY (FVALDATE,FPORTCODE,FSECURITYCODE))";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Data_ValMktPrice(" +
                    "FVALDATE," +
                    "FPORTCODE," +
                    "FSECURITYCODE," +
                    "FPrice," +
                    "FOTPrice1," +
                    "FOTPrice2," +
                    "FOTPrice3," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FVALDATE," +
                    "FPORTCODE," +
                    "FSECURITYCODE," +
                    "FPRICE," +
                    "FPrice," +
                    "FPrice," +
                    "FPrice," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_DAT_1111200714562700A";
                dbl.executeSql(strSql);

            }

            //插入版本信息
            strSql = "insert into tb_fun_version(fassetgroupcode,fvernum,fissuedate,fdesc,fcreatedate,fcreatetime)" +
                "values(" + dbl.sqlString(sPre) + ",'1.0.0.0001'," +
                dbl.sqlDate("2007-11-14") +
                ",'1、修改了配股权证信息的设置;2、增加了远期交易数据表和证券兑换数据表;3、现金账户增加了账户属性；4、远期参数、股指期货参数'," +
                dbl.sqlDate(new java.util.Date()) + "," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                ")";
            dbl.executeSql(strSql);

            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("调整表结构失败，请重新登陆！", e);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void updateVersion1A0A0A0002(String sPre) throws YssException {
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String strSql = "";
        String sKey = "";
        String sTable = "";
        try {
            conn.setAutoCommit(false);

            if (!dbl.yssTableExist("TB_" + sPre + "_DAT_1112200711395500B")) {
                strSql = "ALTER TABLE TB_" + sPre +
                    "_DATA_VALMKTPRICE RENAME TO TB_" +
                    sPre + "_DAT_1112200711395500B";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_ValMktPrice (" +
                    "FValDate      DATE         NOT NULL," +
                    "FPortCode     VARCHAR2(20) NOT NULL," +
                    "FSecurityCode VARCHAR2(20) NOT NULL," +
                    "FPrice        NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice1     NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice2     NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice3     NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FCHECKSTATE   NUMBER(1)    NOT NULL," +
                    "FCREATOR      VARCHAR2(20) NOT NULL," +
                    "FCREATETIME   VARCHAR2(20) NOT NULL," +
                    "FCHECKUSER    VARCHAR2(20)     NULL," +
                    "FCHECKTIME    VARCHAR2(20)     NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Data_ValMktPrice(" +
                    "FValDate," +
                    "FPortCode," +
                    "FSecurityCode," +
                    "FPrice," +
                    "FOTPrice1," +
                    "FOTPrice2," +
                    "FOTPrice3," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    "SELECT " +
                    "FVALDATE," +
                    "FPORTCODE," +
                    "FSECURITYCODE," +
                    "FPRICE," +
                    "FOTPRICE1," +
                    "FOTPRICE2," +
                    "FOTPRICE3," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_DAT_1112200711395500B";
                dbl.executeSql(strSql);

                // Add Constraint SQL
                sKey = dbl.getConstaintKey("Tb_" + sPre + "_Data_ValMktPrice");
                if (sKey.trim().length() != 0) {
                    strSql =
                        "ALTER TABLE Tb_" + sPre +
                        "_Data_ValMktPrice DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                sTable = dbl.getTableByConstaintKey("PK_Tb_" + sPre +
                    "_Data_ValMktPrice");

                if (sTable.trim().length() != 0) {
                    strSql =
                        "ALTER TABLE " + sTable + " DROP CONSTRAINT PK_Tb_" + sPre +
                        "_Data_ValMktPrice CASCADE";
                    dbl.executeSql(strSql);
                }
                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Data_ValMktPrice ADD CONSTRAINT PK_Tb_" + sPre +
                    "_Data_ValMktPrice" +
                    " PRIMARY KEY (FValDate,FPortCode,FSecurityCode)";
                dbl.executeSql(strSql);
            }

            strSql = "SELECT * FROM TB_" + sPre + "_DATA_VALRATE WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FOTPortRate1")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_DATA_VALRATE");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_DATA_VALRATE DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_DATA_VALRATE RENAME TO TB_" +
                    sPre +
                    "_DAT_1112200711395600B";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_ValRate(" +
                    "FVALDATE     DATE          NOT NULL," +
                    "FPORTCODE    VARCHAR2(20)  NOT NULL," +
                    "FCURYCODE    VARCHAR2(20)  NOT NULL," +
                    "FBASERATE    NUMBER(20,15) DEFAULT 1 NOT NULL," +
                    "FOTBaseRate1 NUMBER(18,6)  DEFAULT 0 NOT NULL," +
                    "FOTBaseRate2 NUMBER(18,6)  DEFAULT 0 NOT NULL," +
                    "FOTBaseRate3 NUMBER(18,6)  DEFAULT 0 NOT NULL," +
                    "FPORTRATE    NUMBER(20,15) DEFAULT 1 NOT NULL," +
                    "FOTPortRate1 NUMBER(18,6)  DEFAULT 0 NOT NULL," +
                    "FOTPortRate2 NUMBER(18,6)  DEFAULT 0 NOT NULL," +
                    "FOTPortRate3 NUMBER(18,6)  DEFAULT 0 NOT NULL," +
                    "FCHECKSTATE  NUMBER(1)     NOT NULL," +
                    "FCREATOR     VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME  VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER   VARCHAR2(20)      NULL," +
                    "FCHECKTIME   VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Data_ValRate(" +
                    "FVALDATE," +
                    "FPORTCODE," +
                    "FCURYCODE," +
                    "FBASERATE," +
                    "FOTBaseRate1," +
                    "FOTBaseRate2," +
                    "FOTBaseRate3," +
                    "FPORTRATE," +
                    "FOTPortRate1," +
                    "FOTPortRate2," +
                    "FOTPortRate3," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    "SELECT " +
                    "FVALDATE," +
                    "FPORTCODE," +
                    "FCURYCODE," +
                    "FBASERATE," +
                    "0," +
                    "0," +
                    "0," +
                    "FPORTRATE," +
                    "0," +
                    "0," +
                    "0," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_DAT_1112200711395600B";
                dbl.executeSql(strSql);

                sKey = dbl.getConstaintKey("Tb_" + sPre + "_Data_ValRate");
                if (sKey.trim().length() != 0) {
                    strSql =
                        "ALTER TABLE Tb_" + sPre +
                        "_Data_ValRate DROP CONSTRAINT " +
                        sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Data_ValRate ADD CONSTRAINT PK_Tb_" +
                    sPre + "_Data_ValRate" +
                    " PRIMARY KEY (FVALDATE,FPORTCODE,FCURYCODE)";
                dbl.executeSql(strSql);
            }
            rs.close();

            strSql = "SELECT * FROM TB_" + sPre + "_DATA_TRADE WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FBailMoney")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_DATA_TRADE");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_DATA_TRADE DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_DATA_TRADE RENAME TO TB_" + sPre +
                    "_DAT_1112200711452500B";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_Trade(" +
                    "FNUM             VARCHAR2(15)  NOT NULL," +
                    "FSECURITYCODE    VARCHAR2(20)  NOT NULL," +
                    "FPORTCODE        VARCHAR2(20)      NULL," +
                    "FBROKERCODE      VARCHAR2(20)  NOT NULL," +
                    "FINVMGRCODE      VARCHAR2(20)  NOT NULL," +
                    "FTRADETYPECODE   VARCHAR2(20)  NOT NULL," +
                    "FCASHACCCODE     VARCHAR2(20)  NOT NULL," +
                    "FATTRCLSCODE     VARCHAR2(20)      NULL," +
                    "FBARGAINDATE     DATE          NOT NULL," +
                    "FBARGAINTIME     VARCHAR2(20)  NOT NULL," +
                    "FSETTLEDATE      DATE          NOT NULL," +
                    "FSETTLETIME      VARCHAR2(20)  NOT NULL," +
                    "FAUTOSETTLE      NUMBER(1)     NOT NULL," +
                    "FPORTCURYRATE    NUMBER(20,15) NOT NULL," +
                    "FBASECURYRATE    NUMBER(20,15) NOT NULL," +
                    "FALLOTFACTOR     NUMBER(18,4)  NOT NULL," +
                    "FTRADEAMOUNT     NUMBER(18,4)  NOT NULL," +
                    "FTRADEPRICE      NUMBER(18,4)  NOT NULL," +
                    "FTRADEMONEY      NUMBER(18,4)  NOT NULL," +
                    "FUNITCOST        NUMBER(18,12)     NULL," +
                    "FACCRUEDINTEREST NUMBER(18,4)      NULL," +
                    "FFEECODE1        VARCHAR2(20)      NULL," +
                    "FTRADEFEE1       NUMBER(18,4)      NULL," +
                    "FFEECODE2        VARCHAR2(20)      NULL," +
                    "FTRADEFEE2       NUMBER(18,4)      NULL," +
                    "FFEECODE3        VARCHAR2(20)      NULL," +
                    "FTRADEFEE3       NUMBER(18,4)      NULL," +
                    "FFEECODE4        VARCHAR2(20)      NULL," +
                    "FTRADEFEE4       NUMBER(18,4)      NULL," +
                    "FFEECODE5        VARCHAR2(20)      NULL," +
                    "FTRADEFEE5       NUMBER(18,4)      NULL," +
                    "FFEECODE6        VARCHAR2(20)      NULL," +
                    "FTRADEFEE6       NUMBER(18,4)      NULL," +
                    "FFEECODE7        VARCHAR2(20)      NULL," +
                    "FTRADEFEE7       NUMBER(18,4)      NULL," +
                    "FFEECODE8        VARCHAR2(20)      NULL," +
                    "FTRADEFEE8       NUMBER(18,4)      NULL," +
                    "FTOTALCOST       NUMBER(18,4)      NULL," +
                    "FBailMoney       NUMBER(18,4)      DEFAULT 0 NOT NULL," +
                    "FORDERNUM        VARCHAR2(20)      NULL," +
                    "FDESC            VARCHAR2(100)     NULL," +
                    "FCHECKSTATE      NUMBER(1)     NOT NULL," +
                    "FCREATOR         VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME      VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER       VARCHAR2(20)      NULL," +
                    "FCHECKTIME       VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Data_Trade(" +
                    "FNUM," +
                    "FSECURITYCODE," +
                    "FPORTCODE," +
                    "FBROKERCODE," +
                    "FINVMGRCODE," +
                    "FTRADETYPECODE," +
                    "FCASHACCCODE," +
                    "FATTRCLSCODE," +
                    "FBARGAINDATE," +
                    "FBARGAINTIME," +
                    "FSETTLEDATE," +
                    "FSETTLETIME," +
                    "FAUTOSETTLE," +
                    "FPORTCURYRATE," +
                    "FBASECURYRATE," +
                    "FALLOTFACTOR," +
                    "FTRADEAMOUNT," +
                    "FTRADEPRICE," +
                    "FTRADEMONEY," +
                    "FUNITCOST," +
                    "FACCRUEDINTEREST," +
                    "FFEECODE1," +
                    "FTRADEFEE1," +
                    "FFEECODE2," +
                    "FTRADEFEE2," +
                    "FFEECODE3," +
                    "FTRADEFEE3," +
                    "FFEECODE4," +
                    "FTRADEFEE4," +
                    "FFEECODE5," +
                    "FTRADEFEE5," +
                    "FFEECODE6," +
                    "FTRADEFEE6," +
                    "FFEECODE7," +
                    "FTRADEFEE7," +
                    "FFEECODE8," +
                    "FTRADEFEE8," +
                    "FTOTALCOST," +
                    "FBailMoney," +
                    "FORDERNUM," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    "SELECT " +
                    "FNUM," +
                    "FSECURITYCODE," +
                    "FPORTCODE," +
                    "FBROKERCODE," +
                    "FINVMGRCODE," +
                    "FTRADETYPECODE," +
                    "FCASHACCCODE," +
                    "FATTRCLSCODE," +
                    "FBARGAINDATE," +
                    "FBARGAINTIME," +
                    "FSETTLEDATE," +
                    "FSETTLETIME," +
                    "FAUTOSETTLE," +
                    "FPORTCURYRATE," +
                    "FBASECURYRATE," +
                    "FALLOTFACTOR," +
                    "FTRADEAMOUNT," +
                    "FTRADEPRICE," +
                    "FTRADEMONEY," +
                    "FUNITCOST," +
                    "FACCRUEDINTEREST," +
                    "FFEECODE1," +
                    "FTRADEFEE1," +
                    "FFEECODE2," +
                    "FTRADEFEE2," +
                    "FFEECODE3," +
                    "FTRADEFEE3," +
                    "FFEECODE4," +
                    "FTRADEFEE4," +
                    "FFEECODE5," +
                    "FTRADEFEE5," +
                    "FFEECODE6," +
                    "FTRADEFEE6," +
                    "FFEECODE7," +
                    "FTRADEFEE7," +
                    "FFEECODE8," +
                    "FTRADEFEE8," +
                    "FTOTALCOST," +
                    "0," +
                    "FORDERNUM," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_DAT_1112200711452500B";
                dbl.executeSql(strSql);

                sKey = dbl.getConstaintKey("Tb_" + sPre + "_Data_Trade");
                if (sKey.trim().length() != 0) {
                    strSql =
                        "ALTER TABLE Tb_" + sPre + "_Data_Trade DROP CONSTRAINT " +
                        sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Data_Trade ADD CONSTRAINT PK_Tb_" +
                    sPre + "_Data_Trade" +
                    " PRIMARY KEY (FNUM)";
                dbl.executeSql(strSql);
            }
            rs.close();

            strSql = "SELECT * FROM TB_" + sPre + "_DATA_SUBTRADE WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FBailMoney") ||
                !dbl.isFieldExist(rs, "FFactSettleDate")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_DATA_SUBTRADE");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_DATA_SUBTRADE DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_DATA_SUBTRADE RENAME TO TB_" +
                    sPre +
                    "_DAT_1112200711452600B";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_SubTrade (" +
                    "FNUM             VARCHAR2(20)  NOT NULL," +
                    "FSECURITYCODE    VARCHAR2(20)  NOT NULL," +
                    "FPORTCODE        VARCHAR2(20)      NULL," +
                    "FBROKERCODE      VARCHAR2(20)  NOT NULL," +
                    "FINVMGRCODE      VARCHAR2(20)  NOT NULL," +
                    "FTRADETYPECODE   VARCHAR2(20)  NOT NULL," +
                    "FCASHACCCODE     VARCHAR2(20)  NOT NULL," +
                    "FATTRCLSCODE     VARCHAR2(20)      NULL," +
                    "FBARGAINDATE     DATE          NOT NULL," +
                    "FBARGAINTIME     VARCHAR2(20)  NOT NULL," +
                    "FSETTLEDATE      DATE          NOT NULL," +
                    "FSETTLETIME      VARCHAR2(20)  NOT NULL," +
                    "FAUTOSETTLE      NUMBER(1)     NOT NULL," +
                    "FPORTCURYRATE    NUMBER(20,15) NOT NULL," +
                    "FBASECURYRATE    NUMBER(20,15) NOT NULL," +
                    "FALLOTPROPORTION NUMBER(18,8)  NOT NULL," +
                    "FOLDALLOTAMOUNT  NUMBER(18,4)  NOT NULL," +
                    "FALLOTFACTOR     NUMBER(18,4)  NOT NULL," +
                    "FTRADEAMOUNT     NUMBER(18,4)  NOT NULL," +
                    "FTRADEPRICE      NUMBER(18,4)  NOT NULL," +
                    "FTRADEMONEY      NUMBER(18,4)  NOT NULL," +
                    "FACCRUEDINTEREST NUMBER(18,4)      NULL," +
                    "FBailMoney       NUMBER(18,4)   DEFAULT 0 NOT NULL," +
                    "FFEECODE1        VARCHAR2(20)      NULL," +
                    "FTRADEFEE1       NUMBER(18,4)      NULL," +
                    "FFEECODE2        VARCHAR2(20)      NULL," +
                    "FTRADEFEE2       NUMBER(18,4)      NULL," +
                    "FFEECODE3        VARCHAR2(20)      NULL," +
                    "FTRADEFEE3       NUMBER(18,4)      NULL," +
                    "FFEECODE4        VARCHAR2(20)      NULL," +
                    "FTRADEFEE4       NUMBER(18,4)      NULL," +
                    "FFEECODE5        VARCHAR2(20)      NULL," +
                    "FTRADEFEE5       NUMBER(18,4)      NULL," +
                    "FFEECODE6        VARCHAR2(20)      NULL," +
                    "FTRADEFEE6       NUMBER(18,4)      NULL," +
                    "FFEECODE7        VARCHAR2(20)      NULL," +
                    "FTRADEFEE7       NUMBER(18,4)      NULL," +
                    "FFEECODE8        VARCHAR2(20)      NULL," +
                    "FTRADEFEE8       NUMBER(18,4)      NULL," +
                    "FTOTALCOST       NUMBER(18,4)      NULL," +
                    "FCOST            NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMCOST           NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FVCOST           NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FBASECURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMBASECURYCOST   NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FVBASECURYCOST   NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FPORTCURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMPORTCURYCOST   NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FVPORTCURYCOST   NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FSETTLESTATE     NUMBER(1)     DEFAULT 0 NOT NULL," +
                    "FFactSettleDate  DATE              NULL," +
                    "FSettleDesc      VARCHAR2(100)     NULL," +
                    "FORDERNUM        VARCHAR2(20)      NULL," +
                    "FDATASOURCE      NUMBER(1)     NOT NULL," +
                    "FDESC            VARCHAR2(100)     NULL," +
                    "FCHECKSTATE      NUMBER(1)     NOT NULL," +
                    "FCREATOR         VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME      VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER       VARCHAR2(20)      NULL," +
                    "FCHECKTIME       VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Data_SubTrade(" +
                    "FNUM," +
                    "FSECURITYCODE," +
                    "FPORTCODE," +
                    "FBROKERCODE," +
                    "FINVMGRCODE," +
                    "FTRADETYPECODE," +
                    "FCASHACCCODE," +
                    "FATTRCLSCODE," +
                    "FBARGAINDATE," +
                    "FBARGAINTIME," +
                    "FSETTLEDATE," +
                    "FSETTLETIME," +
                    "FAUTOSETTLE," +
                    "FPORTCURYRATE," +
                    "FBASECURYRATE," +
                    "FALLOTPROPORTION," +
                    "FOLDALLOTAMOUNT," +
                    "FALLOTFACTOR," +
                    "FTRADEAMOUNT," +
                    "FTRADEPRICE," +
                    "FTRADEMONEY," +
                    "FACCRUEDINTEREST," +
                    "FBailMoney," +
                    "FFEECODE1," +
                    "FTRADEFEE1," +
                    "FFEECODE2," +
                    "FTRADEFEE2," +
                    "FFEECODE3," +
                    "FTRADEFEE3," +
                    "FFEECODE4," +
                    "FTRADEFEE4," +
                    "FFEECODE5," +
                    "FTRADEFEE5," +
                    "FFEECODE6," +
                    "FTRADEFEE6," +
                    "FFEECODE7," +
                    "FTRADEFEE7," +
                    "FFEECODE8," +
                    "FTRADEFEE8," +
                    "FTOTALCOST," +
                    "FCOST," +
                    "FMCOST," +
                    "FVCOST," +
                    "FBASECURYCOST," +
                    "FMBASECURYCOST," +
                    "FVBASECURYCOST," +
                    "FPORTCURYCOST," +
                    "FMPORTCURYCOST," +
                    "FVPORTCURYCOST," +
                    "FSETTLESTATE," +
                    "FFactSettleDate," +
                    "FSettleDesc," +
                    "FORDERNUM," +
                    "FDATASOURCE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    "SELECT " +
                    "FNUM," +
                    "FSECURITYCODE," +
                    "FPORTCODE," +
                    "FBROKERCODE," +
                    "FINVMGRCODE," +
                    "FTRADETYPECODE," +
                    "FCASHACCCODE," +
                    "FATTRCLSCODE," +
                    "FBARGAINDATE," +
                    "FBARGAINTIME," +
                    "FSETTLEDATE," +
                    "FSETTLETIME," +
                    "FAUTOSETTLE," +
                    "FPORTCURYRATE," +
                    "FBASECURYRATE," +
                    "FALLOTPROPORTION," +
                    "FOLDALLOTAMOUNT," +
                    "FALLOTFACTOR," +
                    "FTRADEAMOUNT," +
                    "FTRADEPRICE," +
                    "FTRADEMONEY," +
                    "FACCRUEDINTEREST," +
                    "0," +
                    "FFEECODE1," +
                    "FTRADEFEE1," +
                    "FFEECODE2," +
                    "FTRADEFEE2," +
                    "FFEECODE3," +
                    "FTRADEFEE3," +
                    "FFEECODE4," +
                    "FTRADEFEE4," +
                    "FFEECODE5," +
                    "FTRADEFEE5," +
                    "FFEECODE6," +
                    "FTRADEFEE6," +
                    "FFEECODE7," +
                    "FTRADEFEE7," +
                    "FFEECODE8," +
                    "FTRADEFEE8," +
                    "FTOTALCOST," +
                    "FCOST," +
                    "FMCOST," +
                    "FVCOST," +
                    "FBASECURYCOST," +
                    "FMBASECURYCOST," +
                    "FVBASECURYCOST," +
                    "FPORTCURYCOST," +
                    "FMPORTCURYCOST," +
                    "FVPORTCURYCOST," +
                    "FSETTLESTATE," +
                    "FSETTLEDATE," +
                    "FSETTLEDATE," +
                    "FORDERNUM," +
                    "FDATASOURCE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_DAT_1112200711452600B";
                dbl.executeSql(strSql);

                sKey = dbl.getConstaintKey("Tb_" + sPre + "_Data_SubTrade");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE Tb_" + sPre +
                        "_Data_SubTrade DROP CONSTRAINT " +
                        sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Data_SubTrade ADD CONSTRAINT PK_Tb_" + sPre +
                    "_Data_SubTrade" +
                    " PRIMARY KEY (FNUM)";
                dbl.executeSql(strSql);
            }
            rs.close();

            //-- Insert Data SQL
            strSql = "SELECT * FROM TB_" + sPre + "_STOCK_SECURITY WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FBailMoney")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_STOCK_SECURITY");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_STOCK_SECURITY DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_STOCK_SECURITY RENAME TO TB_" +
                    sPre + "_STO_1113200702163400B";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Stock_Security (" +
                    "FSECURITYCODE  VARCHAR2(20)  NOT NULL," +
                    "FYEARMONTH     VARCHAR2(6)   NOT NULL," +
                    "FSTORAGEDATE   DATE          NOT NULL," +
                    "FPORTCODE      VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE1 VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE2 VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE3 VARCHAR2(20)  NOT NULL," +
                    "FCURYCODE      VARCHAR2(20)  NOT NULL," +
                    "FSTORAGEAMOUNT NUMBER(18,4)  NOT NULL," +
                    "FSTORAGECOST   NUMBER(18,4)  NOT NULL," +
                    "FMSTORAGECOST  NUMBER(18,4)  NOT NULL," +
                    "FVSTORAGECOST  NUMBER(18,4)  NOT NULL," +
                    "FFREEZEAMOUNT  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FBailMoney     NUMBER(18,4)      NULL," +
                    "FBASECURYRATE  NUMBER(20,15) NOT NULL," +
                    "FBASECURYCOST  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMBASECURYCOST NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FVBASECURYCOST NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FPORTCURYRATE  NUMBER(20,15) NOT NULL," +
                    "FPORTCURYCOST  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMPORTCURYCOST NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FVPORTCURYCOST NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMARKETPRICE   NUMBER(30,12) DEFAULT 0 NOT NULL," +
                    "FCHECKSTATE    NUMBER(1)     NOT NULL," +
                    "FSTORAGEIND    NUMBER(1)     NOT NULL," +
                    "FCREATOR       VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME    VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER     VARCHAR2(20)      NULL," +
                    "FCHECKTIME     VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                // Insert Data SQL

                strSql = "INSERT INTO Tb_" + sPre + "_Stock_Security(" +
                    "FSECURITYCODE," +
                    "FYEARMONTH," +
                    "FSTORAGEDATE," +
                    "FPORTCODE," +
                    "FANALYSISCODE1," +
                    "FANALYSISCODE2," +
                    "FANALYSISCODE3," +
                    "FCURYCODE," +
                    "FSTORAGEAMOUNT," +
                    "FSTORAGECOST," +
                    "FMSTORAGECOST," +
                    "FVSTORAGECOST," +
                    "FFREEZEAMOUNT," +
                    "FBailMoney," +
                    "FBASECURYRATE," +
                    "FBASECURYCOST," +
                    "FMBASECURYCOST," +
                    "FVBASECURYCOST," +
                    "FPORTCURYRATE," +
                    "FPORTCURYCOST," +
                    "FMPORTCURYCOST," +
                    "FVPORTCURYCOST," +
                    "FMARKETPRICE," +
                    "FCHECKSTATE," +
                    "FSTORAGEIND," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    "SELECT " +
                    "FSECURITYCODE," +
                    "FYEARMONTH," +
                    "FSTORAGEDATE," +
                    "FPORTCODE," +
                    "FANALYSISCODE1," +
                    "FANALYSISCODE2," +
                    "FANALYSISCODE3," +
                    "FCURYCODE," +
                    "FSTORAGEAMOUNT," +
                    "FSTORAGECOST," +
                    "FMSTORAGECOST," +
                    "FVSTORAGECOST," +
                    "FFREEZEAMOUNT," +
                    "null," +
                    "FBASECURYRATE," +
                    "FBASECURYCOST," +
                    "FMBASECURYCOST," +
                    "FVBASECURYCOST," +
                    "FPORTCURYRATE," +
                    "FPORTCURYCOST," +
                    "FMPORTCURYCOST," +
                    "FVPORTCURYCOST," +
                    "FMARKETPRICE," +
                    "FCHECKSTATE," +
                    "FSTORAGEIND," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_STO_1113200702163400B";
                dbl.executeSql(strSql);

                // Add Constraint SQL

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Stock_Security ADD CONSTRAINT PK_TB_" + sPre +
                    "_STOCK_SECURITY" +
                    " PRIMARY KEY (FSECURITYCODE,FYEARMONTH,FSTORAGEDATE,FPORTCODE,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3)";
                dbl.executeSql(strSql);
            }
            rs.close();

            // Insert Data SQL
            strSql = "SELECT * FROM TB_" + sPre + "_REP_GUESSVALUE WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FOTPrice1")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_REP_GUESSVALUE");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_REP_GUESSVALUE DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_REP_GUESSVALUE RENAME TO TB_" +
                    sPre + "_REP_1113200704541500B";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Rep_GuessValue (" +
                    "FPORTCODE                      VARCHAR2(20)  NOT NULL," +
                    "FDATE                          DATE          NOT NULL," +
                    "FACCTCODE                      VARCHAR2(100) NOT NULL," +
                    "FCURCODE                       VARCHAR2(50)  NOT NULL," +
                    "FACCTNAME                      VARCHAR2(100) NOT NULL," +
                    "FACCTATTR                      VARCHAR2(100) NOT NULL," +
                    "FACCTCLASS                     VARCHAR2(20)  NOT NULL," +
                    "FEXCHANGERATE                  NUMBER(20,15) NOT NULL," +
                    "FAMOUNT                        NUMBER(18,4)  NOT NULL," +
                    "FCOST                          NUMBER(18,4)  NOT NULL," +
                    "FSTANDARDMONEYCOST             NUMBER(18,4)  NOT NULL," +
                    "FCOSTTONETRATIO                NUMBER(18,8)  NOT NULL," +
                    "FSTANDARDMONEYCOSTTONETRATIO   NUMBER(18,8)  NOT NULL," +
                    "FMarketPrice                   NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice1                      NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice2                      NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FOTPrice3                      NUMBER(20,12) DEFAULT 0 NOT NULL," +
                    "FMARKETVALUE                   NUMBER(18,4)  NOT NULL," +
                    "FSTANDARDMONEYMARKETVALUE      NUMBER(18,4)  NOT NULL," +
                    "FMARKETVALUETORATIO            NUMBER(18,8)  NOT NULL," +
                    "FSTANDARDMONEYMARKETVALUETORAT NUMBER(18,8)  NOT NULL," +
                    "FAPPRECIATION                  NUMBER(18,4)  NOT NULL," +
                    "FSTANDARDMONEYAPPRECIATION     NUMBER(18,4)  NOT NULL," +
                    "FMARKETDESCRIBE                VARCHAR2(50)      NULL," +
                    "FACCTLEVEL                     NUMBER(38)    NOT NULL," +
                    "FACCTDETAIL                    NUMBER(1)     NOT NULL," +
                    "FDesc                          VARCHAR2(100) DEFAULT ' ' NOT NULL)";
                dbl.executeSql(strSql);

                if (dbl.isFieldExist(rs, "FDESC")) {
                    strSql = "INSERT INTO Tb_" + sPre + "_Rep_GuessValue(" +
                        "FPORTCODE," +
                        "FDATE," +
                        "FACCTCODE," +
                        "FCURCODE," +
                        "FACCTNAME," +
                        "FACCTATTR," +
                        "FACCTCLASS," +
                        "FEXCHANGERATE," +
                        "FAMOUNT," +
                        "FCOST," +
                        "FSTANDARDMONEYCOST," +
                        "FCOSTTONETRATIO," +
                        "FSTANDARDMONEYCOSTTONETRATIO," +
                        "FMarketPrice," +
                        "FOTPrice1," +
                        "FOTPrice2," +
                        "FOTPrice3," +
                        "FMARKETVALUE," +
                        "FSTANDARDMONEYMARKETVALUE," +
                        "FMARKETVALUETORATIO," +
                        "FSTANDARDMONEYMARKETVALUETORAT," +
                        "FAPPRECIATION," +
                        "FSTANDARDMONEYAPPRECIATION," +
                        "FMARKETDESCRIBE," +
                        "FACCTLEVEL," +
                        "FACCTDETAIL," +
                        "FDesc)" +
                        "SELECT " +
                        "FPORTCODE," +
                        "FDATE," +
                        "FACCTCODE," +
                        "FCURCODE," +
                        "FACCTNAME," +
                        "FACCTATTR," +
                        "FACCTCLASS," +
                        "FEXCHANGERATE," +
                        "FAMOUNT," +
                        "FCOST," +
                        "FSTANDARDMONEYCOST," +
                        "FCOSTTONETRATIO," +
                        "FSTANDARDMONEYCOSTTONETRATIO," +
                        "FMARKETPRICE," +
                        "0," +
                        "0," +
                        "0," +
                        "FMARKETVALUE," +
                        "FSTANDARDMONEYMARKETVALUE," +
                        "FMARKETVALUETORATIO," +
                        "FSTANDARDMONEYMARKETVALUETORAT," +
                        "FAPPRECIATION," +
                        "FSTANDARDMONEYAPPRECIATION," +
                        "FMARKETDESCRIBE," +
                        "FACCTLEVEL," +
                        "FACCTDETAIL," +
                        "FDESC " +
                        " FROM TB_" + sPre + "_REP_1113200704541500B";
                } else {
                    strSql = "INSERT INTO Tb_" + sPre + "_Rep_GuessValue(" +
                        "FPORTCODE," +
                        "FDATE," +
                        "FACCTCODE," +
                        "FCURCODE," +
                        "FACCTNAME," +
                        "FACCTATTR," +
                        "FACCTCLASS," +
                        "FEXCHANGERATE," +
                        "FAMOUNT," +
                        "FCOST," +
                        "FSTANDARDMONEYCOST," +
                        "FCOSTTONETRATIO," +
                        "FSTANDARDMONEYCOSTTONETRATIO," +
                        "FMarketPrice," +
                        "FOTPrice1," +
                        "FOTPrice2," +
                        "FOTPrice3," +
                        "FMARKETVALUE," +
                        "FSTANDARDMONEYMARKETVALUE," +
                        "FMARKETVALUETORATIO," +
                        "FSTANDARDMONEYMARKETVALUETORAT," +
                        "FAPPRECIATION," +
                        "FSTANDARDMONEYAPPRECIATION," +
                        "FMARKETDESCRIBE," +
                        "FACCTLEVEL," +
                        "FACCTDETAIL," +
                        "FDesc)" +
                        "SELECT " +
                        "FPORTCODE," +
                        "FDATE," +
                        "FACCTCODE," +
                        "FCURCODE," +
                        "FACCTNAME," +
                        "FACCTATTR," +
                        "FACCTCLASS," +
                        "FEXCHANGERATE," +
                        "FAMOUNT," +
                        "FCOST," +
                        "FSTANDARDMONEYCOST," +
                        "FCOSTTONETRATIO," +
                        "FSTANDARDMONEYCOSTTONETRATIO," +
                        "FMARKETPRICE," +
                        "0," +
                        "0," +
                        "0," +
                        "FMARKETVALUE," +
                        "FSTANDARDMONEYMARKETVALUE," +
                        "FMARKETVALUETORATIO," +
                        "FSTANDARDMONEYMARKETVALUETORAT," +
                        "FAPPRECIATION," +
                        "FSTANDARDMONEYAPPRECIATION," +
                        "FMARKETDESCRIBE," +
                        "FACCTLEVEL," +
                        "FACCTDETAIL," +
                        "' ' as FDESC " +
                        " FROM TB_" + sPre + "_REP_1113200704541500B";
                }
                dbl.executeSql(strSql);

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Rep_GuessValue ADD CONSTRAINT PK_TB_" + sPre +
                    "_REP_GUESSVALUE " +
                    " PRIMARY KEY (FPORTCODE,FDATE,FACCTCODE,FCURCODE)";
                dbl.executeSql(strSql);
            }
            rs.close();

            //插入版本信息
            strSql = "insert into tb_fun_version(fassetgroupcode,fvernum,fissuedate,fdesc,fcreatedate,fcreatetime)" +
                "values(" + dbl.sqlString(sPre) + ",'1.0.0.0002'," +
                dbl.sqlDate("2007-11-19") +
                ",'1、估值行情/估值汇率增加了三个行情和汇率;2、新增股指期货的处理'," +
                dbl.sqlDate(new java.util.Date()) + "," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + ")";
            dbl.executeSql(strSql);

            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("调整表结构失败，请重新登陆！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void updateVersion1A0A0A0003(String sPre) throws YssException {
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String strSql = "";
        String sKey = "";
        String sTable = "";
        try {
            conn.setAutoCommit(false);

            strSql = "SELECT * FROM Tb_" + sPre + "_TA_Trade WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FPortClsCode")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_TA_TRADE");
                if (sKey.trim().length() != 0) {
                    strSql =
                        "ALTER TABLE TB_" + sPre + "_TA_TRADE DROP CONSTRAINT " +
                        sKey +
                        " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_TA_TRADE RENAME TO TB_" + sPre +
                    "_TA__1118200709371700C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_TA_Trade(" +
                    "FNUM           VARCHAR2(20)  NOT NULL," +
                    "FSELLNETCODE   VARCHAR2(20)  NOT NULL," +
                    "FPortClsCode   VARCHAR2(20)  NOT NULL," +
                    "FPORTCODE      VARCHAR2(20)      NULL," +
                    "FSELLTYPE      VARCHAR2(20)  NOT NULL," +
                    "FCURYCODE      VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE1 VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE2 VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE3 VARCHAR2(20)  NOT NULL," +
                    "FCASHACCCODE   VARCHAR2(20)  NOT NULL," +
                    "FSELLMONEY     NUMBER(18,4)  NOT NULL," +
                    "FSELLAMOUNT    NUMBER(18,4)  NOT NULL," +
                    "FSELLPRICE     NUMBER(18,4)  NOT NULL," +
                    "FINCOMENOTBAL  NUMBER(18,4)      NULL," +
                    "FINCOMEBAL     NUMBER(18,4)      NULL," +
                    "FCONFIMDATE    DATE          NOT NULL," +
                    "FTRADEDATE     DATE          NOT NULL," +
                    "FSETTLEDATE    DATE          NOT NULL," +
                    "FPORTCURYRATE  NUMBER(20,15) NOT NULL," +
                    "FBASECURYRATE  NUMBER(20,15) NOT NULL," +
                    "FFEECODE1      VARCHAR2(20)      NULL," +
                    "FTRADEFEE1     NUMBER(18,4)      NULL," +
                    "FFEECODE2      VARCHAR2(20)      NULL," +
                    "FTRADEFEE2     NUMBER(18,4)      NULL," +
                    "FFEECODE3      VARCHAR2(20)      NULL," +
                    "FTRADEFEE3     NUMBER(18,4)      NULL," +
                    "FFEECODE4      VARCHAR2(20)      NULL," +
                    "FTRADEFEE4     NUMBER(18,4)      NULL," +
                    "FFEECODE5      VARCHAR2(20)      NULL," +
                    "FTRADEFEE5     NUMBER(18,4)      NULL," +
                    "FFEECODE6      VARCHAR2(20)      NULL," +
                    "FTRADEFEE6     NUMBER(18,4)      NULL," +
                    "FFEECODE7      VARCHAR2(20)      NULL," +
                    "FTRADEFEE7     NUMBER(18,4)      NULL," +
                    "FFEECODE8      VARCHAR2(20)      NULL," +
                    "FTRADEFEE8     NUMBER(18,4)      NULL," +
                    "FSETTLESTATE   NUMBER(1)     DEFAULT 0 NOT NULL," +
                    "FDESC          VARCHAR2(100)     NULL," +
                    "FCHECKSTATE    NUMBER(1)     NOT NULL," +
                    "FCREATOR       VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME    VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER     VARCHAR2(20)      NULL," +
                    "FCHECKTIME     VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_TA_Trade(" +
                    "FNUM," +
                    "FSELLNETCODE," +
                    "FPortClsCode," +
                    "FPORTCODE," +
                    "FSELLTYPE," +
                    "FCURYCODE," +
                    "FANALYSISCODE1," +
                    "FANALYSISCODE2," +
                    "FANALYSISCODE3," +
                    "FCASHACCCODE," +
                    "FSELLMONEY," +
                    "FSELLAMOUNT," +
                    "FSELLPRICE," +
                    "FINCOMENOTBAL," +
                    "FINCOMEBAL," +
                    "FCONFIMDATE," +
                    "FTRADEDATE," +
                    "FSETTLEDATE," +
                    "FPORTCURYRATE," +
                    "FBASECURYRATE," +
                    "FFEECODE1," +
                    "FTRADEFEE1," +
                    "FFEECODE2," +
                    "FTRADEFEE2," +
                    "FFEECODE3," +
                    "FTRADEFEE3," +
                    "FFEECODE4," +
                    "FTRADEFEE4," +
                    "FFEECODE5," +
                    "FTRADEFEE5," +
                    "FFEECODE6," +
                    "FTRADEFEE6," +
                    "FFEECODE7," +
                    "FTRADEFEE7," +
                    "FFEECODE8," +
                    "FTRADEFEE8," +
                    "FSETTLESTATE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FNUM," +
                    "FSELLNETCODE," +
                    "' '," +
                    "FPORTCODE," +
                    "FSELLTYPE," +
                    "FCURYCODE," +
                    "FANALYSISCODE1," +
                    "FANALYSISCODE2," +
                    "FANALYSISCODE3," +
                    "FCASHACCCODE," +
                    "FSELLMONEY," +
                    "FSELLAMOUNT," +
                    "FSELLPRICE," +
                    "FINCOMENOTBAL," +
                    "FINCOMEBAL," +
                    "FCONFIMDATE," +
                    "FTRADEDATE," +
                    "FSETTLEDATE," +
                    "FPORTCURYRATE," +
                    "FBASECURYRATE," +
                    "FFEECODE1," +
                    "FTRADEFEE1," +
                    "FFEECODE2," +
                    "FTRADEFEE2," +
                    "FFEECODE3," +
                    "FTRADEFEE3," +
                    "FFEECODE4," +
                    "FTRADEFEE4," +
                    "FFEECODE5," +
                    "FTRADEFEE5," +
                    "FFEECODE6," +
                    "FTRADEFEE6," +
                    "FFEECODE7," +
                    "FTRADEFEE7," +
                    "FFEECODE8," +
                    "FTRADEFEE8," +
                    "FSETTLESTATE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_TA__1118200709371700C";
                dbl.executeSql(strSql);

                sTable = dbl.getTableByConstaintKey("PK_TB_" + sPre + "_TA_TRADE");
                if (sTable.trim().length() != 0) {
                    strSql =
                        "ALTER TABLE " + sTable + " DROP CONSTRAINT PK_TB_" + sPre +
                        "_TA_TRADE CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql = "ALTER TABLE Tb_" + sPre +
                    "_TA_Trade ADD CONSTRAINT PK_TB_" + sPre +
                    "_TA_TRADE PRIMARY KEY (FNUM)";
                dbl.executeSql(strSql);
            }

            strSql = "SELECT * FROM TB_" + sPre + "_TA_PORTCLS WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FPortCode")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_TA_PORTCLS");
                if (sKey.trim().length() != 0) {
                    strSql =
                        "ALTER TABLE TB_" + sPre +
                        "_TA_PORTCLS DROP CONSTRAINT PK_TB_" + sPre +
                        "_TA_PORTCLS CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_TA_PORTCLS RENAME TO TB_" + sPre +
                    "_TA__1118200709371800C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_TA_PortCls(" +
                    "FPORTCLSCODE VARCHAR2(20)  NOT NULL," +
                    "FPORTCLSNAME VARCHAR2(50)  NOT NULL," +
                    "FPortCode    VARCHAR2(20)  NOT NULL," +
                    "FDESC        VARCHAR2(100)     NULL," +
                    "FCREATOR     VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME  VARCHAR2(20)  NOT NULL," +
                    "FCHECKSTATE  NUMBER(1)     NOT NULL," +
                    "FCHECKUSER   VARCHAR2(20)      NULL," +
                    "FCHECKTIME   VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_TA_PortCls(" +
                    "FPORTCLSCODE," +
                    "FPORTCLSNAME," +
                    "FPortCode," +
                    "FDESC," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKSTATE," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FPORTCLSCODE," +
                    "FPORTCLSNAME," +
                    "' '," +
                    "FDESC," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKSTATE," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_TA__1118200709371800C";
                dbl.executeSql(strSql);

                strSql = "ALTER TABLE Tb_" + sPre +
                    "_TA_PortCls ADD CONSTRAINT PK_TB_" + sPre +
                    "_TA_PORTCLS PRIMARY KEY (FPORTCLSCODE)";
                dbl.executeSql(strSql);

            }

            strSql = "SELECT * FROM TB_" + sPre + "_TA_CASHACCLINK";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FPortCode")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_TA_CASHACCLINK");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_TA_CASHACCLINK DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_TA_CASHACCLINK RENAME TO TB_" +
                    sPre + "_TA__1118200714263300C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_TA_CashAccLink(" +
                    "FSELLNETCODE  VARCHAR2(20)  NOT NULL," +
                    "FSTARTDATE    DATE          NOT NULL," +
                    "FPORTCLSCODE  VARCHAR2(20)  NOT NULL," +
                    "FPortCode     VARCHAR2(20)  NOT NULL," +
                    "FSELLTYPECODE VARCHAR2(20)  NOT NULL," +
                    "FCURYCODE     VARCHAR2(20)  NOT NULL," +
                    "FCASHACCCODE  VARCHAR2(20)  NOT NULL," +
                    "FDESC         VARCHAR2(100)     NULL," +
                    "FCREATOR      VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME   VARCHAR2(20)  NOT NULL," +
                    "FCHECKSTATE   NUMBER(1)     NOT NULL," +
                    "FCHECKUSER    VARCHAR2(20)      NULL," +
                    "FCHECKTIME    VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_TA_CashAccLink(" +
                    "FSELLNETCODE," +
                    "FSTARTDATE," +
                    "FPORTCLSCODE," +
                    "FPortCode," +
                    "FSELLTYPECODE," +
                    "FCURYCODE," +
                    "FCASHACCCODE," +
                    "FDESC," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKSTATE," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FSELLNETCODE," +
                    "FSTARTDATE," +
                    "FPORTCLSCODE," +
                    "FPORTTYPECODE," +
                    "FSELLTYPECODE," +
                    "FCURYCODE," +
                    "FCASHACCCODE," +
                    "FDESC," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKSTATE," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_TA__1118200714263300C";
                dbl.executeSql(strSql);

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_TA_CashAccLink ADD CONSTRAINT PK_TB_" + sPre +
                    "_TA_CASHACCLINK " +
                    "PRIMARY KEY (FSELLNETCODE,FSTARTDATE,FPORTCLSCODE,FPortCode,FSELLTYPECODE,FCURYCODE)";
                dbl.executeSql(strSql);
            }
            rs.close();

            strSql = "SELECT * FROM Tb_" + sPre + "_TA_CashSettle WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FPortCode")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_TA_CASHSETTLE");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_TA_CASHSETTLE DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql = "ALTER TABLE TB_" + sPre + "_TA_CASHSETTLE RENAME TO TB_" +
                    sPre + "_TA__1118200714262900C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_TA_CashSettle (" +
                    "FSellNetCode   VARCHAR2(20)  NOT NULL," +
                    "FStartDate     DATE          NOT NULL," +
                    "FPortClsCode   VARCHAR2(20)  NOT NULL," +
                    "FPortCode      VARCHAR2(20)  NOT NULL," +
                    "FSellTypeCode  VARCHAR2(20)  NOT NULL," +
                    "FCuryCode      VARCHAR2(20)  NOT NULL," +
                    "FHOLIDAYSCODE  VARCHAR2(20)  NOT NULL," +
                    "FSETTLEDAYTYPE NUMBER(1)     NOT NULL," +
                    "FCONFIRMDAYS   NUMBER(38)    NOT NULL," +
                    "FSETTLEDAYS    NUMBER(38)    NOT NULL," +
                    "FDESC          VARCHAR2(100)     NULL," +
                    "FCREATOR       VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME    VARCHAR2(20)  NOT NULL," +
                    "FCHECKSTATE    NUMBER(1)     NOT NULL," +
                    "FCHECKUSER     VARCHAR2(20)      NULL," +
                    "FCHECKTIME     VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_TA_CashSettle(" +
                    "FSellNetCode," +
                    "FStartDate," +
                    "FPortClsCode," +
                    "FPortCode," +
                    "FSellTypeCode," +
                    "FCuryCode," +
                    "FHOLIDAYSCODE," +
                    "FSETTLEDAYTYPE," +
                    "FCONFIRMDAYS," +
                    "FSETTLEDAYS," +
                    "FDESC," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKSTATE," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FSELLNETCODE," +
                    "FSTARTDATE," +
                    "FPORTCLSCODE," +
                    "' '," +
                    "FSELLTYPECODE," +
                    "FCURYCODE,";
                if (dbl.isFieldExist(rs, "FCONFIRMDAYS")) {
                    strSql = strSql + "FHOLIDAYSCODE,";
                } else {
                    strSql = strSql + "' ' FHOLIDAYSCODE,";
                }
                strSql = strSql + "FSETTLEDAYTYPE,";
                if (dbl.isFieldExist(rs, "FCONFIRMDAYS")) {
                    strSql = strSql + "FCONFIRMDAYS,";
                } else {
                    strSql = strSql + " 0 FCONFIRMDAYS,";
                }
                strSql = strSql + "FSETTLEDAYS," +
                    "FDESC," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKSTATE," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_TA__1118200714262900C";
                dbl.executeSql(strSql);

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_TA_CashSettle ADD CONSTRAINT PK_Tb_" + sPre +
                    "_TA_CashSettle " +
                    "PRIMARY KEY (FSellNetCode,FStartDate,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode)";
                dbl.executeSql(strSql);
            }
            rs.close();

            strSql = "SELECT * FROM TB_" + sPre + "_STOCK_SECURITY WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FBailMoney")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_STOCK_SECURITY");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_STOCK_SECURITY DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_STOCK_SECURITY RENAME TO TB_" +
                    sPre + "_STO_1118200712483100C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Stock_Security(" +
                    "FSECURITYCODE  VARCHAR2(20)  NOT NULL," +
                    "FYEARMONTH     VARCHAR2(6)   NOT NULL," +
                    "FSTORAGEDATE   DATE          NOT NULL," +
                    "FPORTCODE      VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE1 VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE2 VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE3 VARCHAR2(20)  NOT NULL," +
                    "FCURYCODE      VARCHAR2(20)  NOT NULL," +
                    "FSTORAGEAMOUNT NUMBER(18,4)  NOT NULL," +
                    "FSTORAGECOST   NUMBER(18,4)  NOT NULL," +
                    "FMSTORAGECOST  NUMBER(18,4)  NOT NULL," +
                    "FVSTORAGECOST  NUMBER(18,4)  NOT NULL," +
                    "FFREEZEAMOUNT  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FBailMoney     NUMBER(18,4)      NULL," +
                    "FBASECURYRATE  NUMBER(20,15) NOT NULL," +
                    "FBASECURYCOST  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMBASECURYCOST NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FVBASECURYCOST NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FPORTCURYRATE  NUMBER(20,15) NOT NULL," +
                    "FPORTCURYCOST  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMPORTCURYCOST NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FVPORTCURYCOST NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FMARKETPRICE   NUMBER(30,12) DEFAULT 0 NOT NULL," +
                    "FCHECKSTATE    NUMBER(1)     NOT NULL," +
                    "FSTORAGEIND    NUMBER(1)     NOT NULL," +
                    "FCREATOR       VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME    VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER     VARCHAR2(20)      NULL," +
                    "FCHECKTIME     VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Stock_Security(" +
                    "FSECURITYCODE," +
                    "FYEARMONTH," +
                    "FSTORAGEDATE," +
                    "FPORTCODE," +
                    "FANALYSISCODE1," +
                    "FANALYSISCODE2," +
                    "FANALYSISCODE3," +
                    "FCURYCODE," +
                    "FSTORAGEAMOUNT," +
                    "FSTORAGECOST," +
                    "FMSTORAGECOST," +
                    "FVSTORAGECOST," +
                    "FFREEZEAMOUNT," +
                    "FBailMoney," +
                    "FBASECURYRATE," +
                    "FBASECURYCOST," +
                    "FMBASECURYCOST," +
                    "FVBASECURYCOST," +
                    "FPORTCURYRATE," +
                    "FPORTCURYCOST," +
                    "FMPORTCURYCOST," +
                    "FVPORTCURYCOST," +
                    "FMARKETPRICE," +
                    "FCHECKSTATE," +
                    "FSTORAGEIND," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FSECURITYCODE," +
                    "FYEARMONTH," +
                    "FSTORAGEDATE," +
                    "FPORTCODE," +
                    "FANALYSISCODE1," +
                    "FANALYSISCODE2," +
                    "FANALYSISCODE3," +
                    "FCURYCODE," +
                    "FSTORAGEAMOUNT," +
                    "FSTORAGECOST," +
                    "FMSTORAGECOST," +
                    "FVSTORAGECOST," +
                    "FFREEZEAMOUNT," +
                    "null," +
                    "FBASECURYRATE," +
                    "FBASECURYCOST," +
                    "FMBASECURYCOST," +
                    "FVBASECURYCOST," +
                    "FPORTCURYRATE," +
                    "FPORTCURYCOST," +
                    "FMPORTCURYCOST," +
                    "FVPORTCURYCOST," +
                    "FMARKETPRICE," +
                    "FCHECKSTATE," +
                    "FSTORAGEIND," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_STO_1118200712483100C";
                dbl.executeSql(strSql);

                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Stock_Security ADD CONSTRAINT PK_TB_" + sPre +
                    "_STOCK_SECURITY " +
                    "PRIMARY KEY (FSECURITYCODE,FYEARMONTH,FSTORAGEDATE,FPORTCODE,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3)";
                dbl.executeSql(strSql);
            }

            strSql = "SELECT * FROM TB_" + sPre + "_STOCK_TA WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FPortClsCode")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_STOCK_TA");
                if (sKey.trim().length() != 0) {
                    strSql =
                        "ALTER TABLE TB_" + sPre +
                        "_STOCK_TA DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_STOCK_TA RENAME TO TB_" + sPre +
                    "_STO_1118200712483300C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Stock_TA (" +
                    "FYEARMONTH     VARCHAR2(6)   NOT NULL," +
                    "FSTORAGEDATE   DATE          NOT NULL," +
                    "FPORTCODE      VARCHAR2(20)  NOT NULL," +
                    "FPortClsCode   VARCHAR2(20)  NOT NULL," +
                    "FCURYCODE      VARCHAR2(20)  NOT NULL," +
                    "FCOST          NUMBER(18,4)  NOT NULL," +
                    "FSTORAGEAMOUNT NUMBER(18,4)  NOT NULL," +
                    "FPORTCURYCOST  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FANALYSISCODE1 VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE2 VARCHAR2(20)  NOT NULL," +
                    "FANALYSISCODE3 VARCHAR2(20)  NOT NULL," +
                    "FBASECURYCOST  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FPORTCURYRATE  NUMBER(20,15) NOT NULL," +
                    "FBASECURYRATE  NUMBER(20,15) NOT NULL," +
                    "FCURYUNPL      NUMBER(18,4)  NOT NULL," +
                    "FCURYPL        NUMBER(18,4)  NOT NULL," +
                    "FPORTCURYUNPL  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FPORTCURYPL    NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FBASECURYUNPL  NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FBASECURYPL    NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "FSTORAGEIND    NUMBER(1)     NOT NULL," +
                    "FCHECKSTATE    NUMBER(1)     NOT NULL," +
                    "FCREATOR       VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME    VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER     VARCHAR2(20)      NULL," +
                    "FCHECKTIME     VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Stock_TA(" +
                    "FYEARMONTH," +
                    "FSTORAGEDATE," +
                    "FPORTCODE," +
                    "FPortClsCode," +
                    "FCURYCODE," +
                    "FCOST," +
                    "FSTORAGEAMOUNT," +
                    "FPORTCURYCOST," +
                    "FANALYSISCODE1," +
                    "FANALYSISCODE2," +
                    "FANALYSISCODE3," +
                    "FBASECURYCOST," +
                    "FPORTCURYRATE," +
                    "FBASECURYRATE," +
                    "FCURYUNPL," +
                    "FCURYPL," +
                    "FPORTCURYUNPL," +
                    "FPORTCURYPL," +
                    "FBASECURYUNPL," +
                    "FBASECURYPL," +
                    "FSTORAGEIND," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FYEARMONTH," +
                    "FSTORAGEDATE," +
                    "FPORTCODE," +
                    "FSELLNETCODE," +
                    "FCURYCODE," +
                    "FCOST," +
                    "FSTORAGEAMOUNT," +
                    "FPORTCURYCOST," +
                    "FANALYSISCODE1," +
                    "FANALYSISCODE2," +
                    "FANALYSISCODE3," +
                    "FBASECURYCOST," +
                    "FPORTCURYRATE," +
                    "FBASECURYRATE," +
                    "FCURYUNPL," +
                    "FCURYPL," +
                    "FPORTCURYUNPL," +
                    "FPORTCURYPL," +
                    "FBASECURYUNPL," +
                    "FBASECURYPL," +
                    "FSTORAGEIND," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_STO_1118200712483300C";
                dbl.executeSql(strSql);

                strSql =
                    "ALTER TABLE Tb_" + sPre + "_Stock_TA ADD CONSTRAINT PK_TB_" +
                    sPre + "_STOCK_TA" +
                    " PRIMARY KEY (FYEARMONTH,FSTORAGEDATE,FPORTCODE,FPortClsCode)";
                dbl.executeSql(strSql);
            }
            rs.close();

            strSql = "SELECT * FROM TB_" + sPre + "_TA_FEELINK WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FPortCode")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_TA_FEELINK");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_TA_FEELINK DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql = "ALTER TABLE TB_" + sPre + "_TA_FEELINK RENAME TO TB_" +
                    sPre + "_TA__1118200714263000C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_TA_FeeLink(" +
                    "FSELLNETCODE  VARCHAR2(20)  NOT NULL," +
                    "FSTARTDATE    DATE          NOT NULL," +
                    "FPORTCLSCODE  VARCHAR2(20)  NOT NULL," +
                    "FPortCode     VARCHAR2(20)  NOT NULL," +
                    "FSELLTYPECODE VARCHAR2(20)  NOT NULL," +
                    "FCURYCODE     VARCHAR2(20)  NOT NULL," +
                    "FFEECODE1     VARCHAR2(20)      NULL," +
                    "FFEECODE2     VARCHAR2(20)      NULL," +
                    "FFEECODE3     VARCHAR2(20)      NULL," +
                    "FFEECODE4     VARCHAR2(20)      NULL," +
                    "FFEECODE5     VARCHAR2(20)      NULL," +
                    "FFEECODE6     VARCHAR2(20)      NULL," +
                    "FDESC         VARCHAR2(100)     NULL," +
                    "FCHECKSTATE   NUMBER(1)     NOT NULL," +
                    "FCREATOR      VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME   VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER    VARCHAR2(20)      NULL," +
                    "FCHECKTIME    VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_TA_FeeLink(" +
                    "FSELLNETCODE," +
                    "FSTARTDATE," +
                    "FPORTCLSCODE," +
                    "FPortCode," +
                    "FSELLTYPECODE," +
                    "FCURYCODE," +
                    "FFEECODE1," +
                    "FFEECODE2," +
                    "FFEECODE3," +
                    "FFEECODE4," +
                    "FFEECODE5," +
                    "FFEECODE6," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FSELLNETCODE," +
                    "FSTARTDATE," +
                    "FPORTCLSCODE," +
                    "FPORTTYPECODE," +
                    "FSELLTYPECODE," +
                    "FCURYCODE," +
                    "FFEECODE1," +
                    "FFEECODE2," +
                    "FFEECODE3," +
                    "FFEECODE4," +
                    "FFEECODE5," +
                    "FFEECODE6," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_TA__1118200714263000C";
                dbl.executeSql(strSql);

                strSql = "ALTER TABLE Tb_" + sPre +
                    "_TA_FeeLink ADD CONSTRAINT PK_TB_" + sPre + "_TA_FEELINK " +
                    "PRIMARY KEY (FSELLNETCODE,FSTARTDATE,FPORTCLSCODE,FPortCode,FSELLTYPECODE,FCURYCODE)";
                dbl.executeSql(strSql);
            }
            rs.close();

            sKey = dbl.getConstaintKey("TB_" + sPre + "_DATA_VALRATE");
            if (sKey.trim().length() != 0) {
                strSql = "ALTER TABLE TB_" + sPre +
                    "_DATA_VALRATE DROP CONSTRAINT " + sKey + " CASCADE";
                dbl.executeSql(strSql);

                strSql = "ALTER TABLE Tb_" + sPre +
                    "_Data_ValRate ADD CONSTRAINT " + pub.yssGetTableName("PK_Tb_DATA_VALRATE") +
                    " PRIMARY KEY (FVALDATE,FPORTCODE,FCURYCODE)";
                dbl.executeSql(strSql);
            }

            strSql = "SELECT * FROM TB_" + sPre + "_DATA_DIVIDEND WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FCuryCode")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_DATA_DIVIDEND");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_DATA_DIVIDEND DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_DATA_DIVIDEND RENAME TO TB_" +
                    sPre + "_DAT_1118200713233400C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_Dividend(" +
                    "FSECURITYCODE   VARCHAR2(20)  NOT NULL," +
                    "FRECORDDATE     DATE          NOT NULL," +
                    "FDIVDENDTYPE    NUMBER(1)     NOT NULL," +
                    "FCuryCode       VARCHAR2(20)  DEFAULT ' ' NOT NULL," +
                    "FDIVIDENDDATE   DATE          NOT NULL," +
                    "FDISTRIBUTEDATE DATE          NOT NULL," +
                    "FAFFICHEDATE    DATE              NULL," +
                    "FRATIO          NUMBER(18,8)  NOT NULL," +
                    "FROUNDCODE      VARCHAR2(20)  NOT NULL," +
                    "FDESC           VARCHAR2(100)     NULL," +
                    "FCHECKSTATE     NUMBER(1)     NOT NULL," +
                    "FCREATOR        VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME     VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER      VARCHAR2(20)      NULL," +
                    "FCHECKTIME      VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Data_Dividend(" +
                    "FSECURITYCODE," +
                    "FRECORDDATE," +
                    "FDIVDENDTYPE," +
                    "FCuryCode," +
                    "FDIVIDENDDATE," +
                    "FDISTRIBUTEDATE," +
                    "FAFFICHEDATE," +
                    "FRATIO," +
                    "FROUNDCODE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT a.FSECURITYCODE," +
                    "a.FRECORDDATE," +
                    "a.FDIVDENDTYPE," +
                    "nvl(b.FTradeCury,' ') as FCURYCODE," +
                    "a.FDIVIDENDDATE," +
                    "a.FDISTRIBUTEDATE," +
                    "a.FAFFICHEDATE," +
                    "a.FRATIO," +
                    "a.FROUNDCODE," +
                    "a.FDESC," +
                    "a.FCHECKSTATE," +
                    "a.FCREATOR," +
                    "a.FCREATETIME," +
                    "a.FCHECKUSER," +
                    "a.FCHECKTIME" +
                    " FROM TB_" + sPre + "_DAT_1118200713233400C a left join tb_" +
                    sPre + "_para_security b " +
                    " on a.fsecuritycode = b.fsecuritycode";
                dbl.executeSql(strSql);
            }
            rs.close();

            strSql = "SELECT * FROM TB_" + sPre + "_PARA_FIXINTEREST WHERE 1=1";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FCalcInsCfgBuy")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_PARA_FIXINTEREST");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_PARA_FIXINTEREST DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql =
                    "ALTER TABLE TB_" + sPre + "_PARA_FIXINTEREST RENAME TO TB_" +
                    sPre + "_PAR_1119200711040000C";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Para_FixInterest(" +
                    "FSECURITYCODE     VARCHAR2(20)  NOT NULL," +
                    "FSTARTDATE        DATE          NOT NULL," +
                    "FISSUEDATE        DATE          NOT NULL," +
                    "FISSUEPRICE       NUMBER(18,4)  NOT NULL," +
                    "FINSSTARTDATE     DATE          NOT NULL," +
                    "FINSENDDATE       DATE          NOT NULL," +
                    "FINSCASHDATE      DATE          NOT NULL," +
                    "FFACEVALUE        NUMBER(18,4)  NOT NULL," +
                    "FFACERATE         NUMBER(18,12)     NULL," +
                    "FINSFREQUENCY     NUMBER(18,4)  NOT NULL," +
                    "FQUOTEWAY         NUMBER(1)     NOT NULL," +
                    "FCREDITLEVEL      VARCHAR2(20)      NULL," +
                    "FCalcInsMeticDay  VARCHAR2(20)      NULL," +
                    "FCalcInsMeticBuy  VARCHAR2(20)      NULL," +
                    "FCalcInsMeticSell VARCHAR2(20)      NULL," +
                    "FCalcInsCfgDay    VARCHAR2(500)     NULL," +
                    "FCalcInsCfgBuy    VARCHAR2(500)     NULL," +
                    "FCalcInsCfgSell   VARCHAR2(500)     NULL," +
                    "FCALCINSWAY       NUMBER(1)     NOT NULL," +
                    "FINTERESTORIGIN   NUMBER(1)     NOT NULL," +
                    "FPEREXPCODE       VARCHAR2(20)      NULL," +
                    "FPERIODCODE       VARCHAR2(20)  NOT NULL," +
                    "FROUNDCODE        VARCHAR2(20)  NOT NULL," +
                    "FDESC             VARCHAR2(100)     NULL," +
                    "FCHECKSTATE       NUMBER(1)     NOT NULL," +
                    "FCREATOR          VARCHAR2(20)  NOT NULL," +
                    "FCREATETIME       VARCHAR2(20)  NOT NULL," +
                    "FCHECKUSER        VARCHAR2(20)      NULL," +
                    "FCHECKTIME        VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Para_FixInterest(" +
                    "FSECURITYCODE," +
                    "FSTARTDATE," +
                    "FISSUEDATE," +
                    "FISSUEPRICE," +
                    "FINSSTARTDATE," +
                    "FINSENDDATE," +
                    "FINSCASHDATE," +
                    "FFACEVALUE," +
                    "FFACERATE," +
                    "FINSFREQUENCY," +
                    "FQUOTEWAY," +
                    "FCREDITLEVEL," +
                    "FCalcInsMeticDay," +
                    "FCalcInsMeticBuy," +
                    "FCalcInsMeticSell," +
                    "FCalcInsCfgDay," +
                    "FCalcInsCfgBuy," +
                    "FCalcInsCfgSell," +
                    "FCALCINSWAY," +
                    "FINTERESTORIGIN," +
                    "FPEREXPCODE," +
                    "FPERIODCODE," +
                    "FROUNDCODE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME)" +
                    " SELECT FSECURITYCODE," +
                    "FSTARTDATE," +
                    "FISSUEDATE," +
                    "FISSUEPRICE," +
                    "FINSSTARTDATE," +
                    "FINSENDDATE," +
                    "FINSCASHDATE," +
                    "FFACEVALUE," +
                    "FFACERATE," +
                    "FINSFREQUENCY," +
                    "FQUOTEWAY," +
                    "FCREDITLEVEL," +
                    "null," +
                    "null," +
                    "null," +
                    "null," +
                    "null," +
                    "null," +
                    "FCALCINSWAY," +
                    "FINTERESTORIGIN," +
                    "FPEREXPCODE," +
                    "FPERIODCODE," +
                    "FROUNDCODE," +
                    "FDESC," +
                    "FCHECKSTATE," +
                    "FCREATOR," +
                    "FCREATETIME," +
                    "FCHECKUSER," +
                    "FCHECKTIME" +
                    " FROM TB_" + sPre + "_PAR_1119200711040000C";
                dbl.executeSql(strSql);
                strSql =
                    "ALTER TABLE Tb_" + sPre +
                    "_Para_FixInterest ADD CONSTRAINT PK_TB_" + sPre +
                    "_PARA_FIXINTEREST" +
                    "  PRIMARY KEY (FSECURITYCODE,FSTARTDATE)";
                dbl.executeSql(strSql);
            }
            rs.close();

            strSql = "UPDATE TB_" + sPre + "_DATA_CASHPAYREC SET  FSUBTSFTYPECODE = '06TD' WHERE FSUBTSFTYPECODE LIKE '06TD%'";
            dbl.executeSql(strSql);

            strSql = "UPDATE TB_" + sPre + "_DATA_CASHPAYREC SET  FSUBTSFTYPECODE = '07TD' WHERE FSUBTSFTYPECODE LIKE '07TD%'";
            dbl.executeSql(strSql);

            strSql = "UPDATE TB_" + sPre + "_DATA_CASHPAYREC SET  FSUBTSFTYPECODE = '02TD' WHERE FSUBTSFTYPECODE LIKE '02TD%'";
            dbl.executeSql(strSql);

            strSql = "UPDATE TB_" + sPre + "_DATA_CASHPAYREC SET  FSUBTSFTYPECODE = '03TD' WHERE FSUBTSFTYPECODE LIKE '03TD%'";
            dbl.executeSql(strSql);

            strSql = "UPDATE TB_" + sPre + "_DATA_CASHPAYREC SET  FSUBTSFTYPECODE = '9906TD' WHERE FSUBTSFTYPECODE LIKE '9906TD%'";
            dbl.executeSql(strSql);

            strSql = "UPDATE TB_" + sPre + "_DATA_CASHPAYREC SET  FSUBTSFTYPECODE = '9907TD' WHERE FSUBTSFTYPECODE LIKE '9907TD%'";
            dbl.executeSql(strSql);

            //strSql = "UPDATE TB_" + sPre + "_DATA_INVESTPAYREC SET FPORTCURYMONEY = FMONEY";
            //dbl.executeSql(strSql);

            //strSql = "DELETE FROM  TB_" + sPre + "_DATA_INVESTPAYREC WHERE FTSFTYPECODE like '99%'";
            //dbl.executeSql(strSql);

            //strSql = "DELETE FROM TB_" + sPre + "_STOCK_INVESTPAYREC WHERE FTSFTYPECODE LIKE '99%'";
            //dbl.executeSql(strSql);

            //插入版本信息
            strSql = "insert into tb_fun_version(fassetgroupcode,fvernum,fissuedate,fdesc,fcreatedate,fcreatetime)" +
                "values(" + dbl.sqlString(sPre) + ",'1.0.0.0003'," +
                dbl.sqlDate("2007-11-19") +
                ",'1、TA交易数据表中增加了分组代码;2、TA库存中增加了分组代码'," +
                dbl.sqlDate(new java.util.Date()) + "," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + ")";
            dbl.executeSql(strSql);

            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("调整表结构失败，请重新登陆！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void updateVersion1A0A0A0004(String sPre) throws YssException {
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String strSql = "";
        String sKey = "";
        String sTable = "";
        try {
            conn.setAutoCommit(false);
            strSql = "SELECT * FROM TB_" + sPre + "_DATA_SUBTRADE WHERE 1=2";
            rs = dbl.openResultSet(strSql);
            if (!dbl.isFieldExist(rs, "FMatureDate")) {
                sKey = dbl.getConstaintKey("TB_" + sPre + "_DATA_SUBTRADE");
                if (sKey.trim().length() != 0) {
                    strSql = "ALTER TABLE TB_" + sPre +
                        "_DATA_SUBTRADE DROP CONSTRAINT " + sKey + " CASCADE";
                    dbl.executeSql(strSql);
                }

                strSql = "ALTER TABLE TB_" + sPre + "_DATA_SUBTRADE RENAME TO TB_" +
                    sPre + "_DAT_1120200717254300D";
                dbl.executeSql(strSql);

                strSql = "CREATE TABLE Tb_" + sPre + "_Data_SubTrade(" +
                    "    FNUM              VARCHAR2(20)  NOT NULL," +
                    "    FSECURITYCODE     VARCHAR2(20)  NOT NULL," +
                    "    FPORTCODE         VARCHAR2(20)      NULL," +
                    "    FBROKERCODE       VARCHAR2(20)  NOT NULL," +
                    "    FINVMGRCODE       VARCHAR2(20)  NOT NULL," +
                    "    FTRADETYPECODE    VARCHAR2(20)  NOT NULL," +
                    "    FCASHACCCODE      VARCHAR2(20)  NOT NULL," +
                    "    FATTRCLSCODE      VARCHAR2(20)      NULL," +
                    "    FBARGAINDATE      DATE          NOT NULL," +
                    "    FBARGAINTIME      VARCHAR2(20)  NOT NULL," +
                    "    FSETTLEDATE       DATE          NOT NULL," +
                    "    FSETTLETIME       VARCHAR2(20)  NOT NULL," +
                    "    FMatureDate       DATE              NULL," +
                    "    FMatureSettleDate DATE              NULL," +
                    "    FAUTOSETTLE       NUMBER(1)     NOT NULL," +
                    "    FPORTCURYRATE     NUMBER(20,15) NOT NULL," +
                    "    FBASECURYRATE     NUMBER(20,15) NOT NULL," +
                    "    FALLOTPROPORTION  NUMBER(18,8)  NOT NULL," +
                    "    FOLDALLOTAMOUNT   NUMBER(18,4)  NOT NULL," +
                    "    FALLOTFACTOR      NUMBER(18,4)  NOT NULL," +
                    "    FTRADEAMOUNT      NUMBER(18,4)  NOT NULL," +
                    "    FTRADEPRICE       NUMBER(18,4)  NOT NULL," +
                    "    FTRADEMONEY       NUMBER(18,4)  NOT NULL," +
                    "    FACCRUEDINTEREST  NUMBER(18,4)      NULL," +
                    "    FBailMoney        NUMBER(18,4)      NULL," +
                    "    FFEECODE1         VARCHAR2(20)      NULL," +
                    "    FTRADEFEE1        NUMBER(18,4)      NULL," +
                    "    FFEECODE2         VARCHAR2(20)      NULL," +
                    "    FTRADEFEE2        NUMBER(18,4)      NULL," +
                    "    FFEECODE3         VARCHAR2(20)      NULL," +
                    "    FTRADEFEE3        NUMBER(18,4)      NULL," +
                    "    FFEECODE4         VARCHAR2(20)      NULL," +
                    "    FTRADEFEE4        NUMBER(18,4)      NULL," +
                    "    FFEECODE5         VARCHAR2(20)      NULL," +
                    "    FTRADEFEE5        NUMBER(18,4)      NULL," +
                    "    FFEECODE6         VARCHAR2(20)      NULL," +
                    "    FTRADEFEE6        NUMBER(18,4)      NULL," +
                    "    FFEECODE7         VARCHAR2(20)      NULL," +
                    "    FTRADEFEE7        NUMBER(18,4)      NULL," +
                    "    FFEECODE8         VARCHAR2(20)      NULL," +
                    "    FTRADEFEE8        NUMBER(18,4)      NULL," +
                    "    FTOTALCOST        NUMBER(18,4)      NULL," +
                    "    FCOST             NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FMCOST            NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FVCOST            NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FBASECURYCOST     NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FMBASECURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FVBASECURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FPORTCURYCOST     NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FMPORTCURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FVPORTCURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL," +
                    "    FSETTLESTATE      NUMBER(1)     DEFAULT 0 NOT NULL," +
                    "    FFACTSETTLEDATE   DATE              NULL," +
                    "    FSETTLEDESC       VARCHAR2(100)     NULL," +
                    "    FORDERNUM         VARCHAR2(20)      NULL," +
                    "    FDATASOURCE       NUMBER(1)     NOT NULL," +
                    "    FDESC             VARCHAR2(100)     NULL," +
                    "    FCHECKSTATE       NUMBER(1)     NOT NULL," +
                    "    FCREATOR          VARCHAR2(20)  NOT NULL," +
                    "    FCREATETIME       VARCHAR2(20)  NOT NULL," +
                    "    FCHECKUSER        VARCHAR2(20)      NULL," +
                    "    FCHECKTIME        VARCHAR2(20)      NULL)";
                dbl.executeSql(strSql);

                strSql = "INSERT INTO Tb_" + sPre + "_Data_SubTrade(" +
                    "    FNUM," +
                    "    FSECURITYCODE," +
                    "    FPORTCODE," +
                    "    FBROKERCODE," +
                    "    FINVMGRCODE," +
                    "    FTRADETYPECODE," +
                    "    FCASHACCCODE," +
                    "    FATTRCLSCODE," +
                    "    FBARGAINDATE," +
                    "    FBARGAINTIME," +
                    "    FSETTLEDATE," +
                    "    FSETTLETIME," +
                    "    FMatureDate," +
                    "    FMatureSettleDate," +
                    "    FAUTOSETTLE," +
                    "    FPORTCURYRATE," +
                    "    FBASECURYRATE," +
                    "    FALLOTPROPORTION," +
                    "    FOLDALLOTAMOUNT," +
                    "    FALLOTFACTOR," +
                    "    FTRADEAMOUNT," +
                    "    FTRADEPRICE," +
                    "    FTRADEMONEY," +
                    "    FACCRUEDINTEREST," +
                    "    FBailMoney," +
                    "    FFEECODE1," +
                    "    FTRADEFEE1," +
                    "    FFEECODE2," +
                    "    FTRADEFEE2," +
                    "    FFEECODE3," +
                    "    FTRADEFEE3," +
                    "    FFEECODE4," +
                    "    FTRADEFEE4," +
                    "    FFEECODE5," +
                    "    FTRADEFEE5," +
                    "    FFEECODE6," +
                    "    FTRADEFEE6," +
                    "    FFEECODE7," +
                    "    FTRADEFEE7," +
                    "    FFEECODE8," +
                    "    FTRADEFEE8," +
                    "    FTOTALCOST," +
                    "    FCOST," +
                    "    FMCOST," +
                    "    FVCOST," +
                    "    FBASECURYCOST," +
                    "    FMBASECURYCOST," +
                    "    FVBASECURYCOST," +
                    "    FPORTCURYCOST," +
                    "    FMPORTCURYCOST," +
                    "    FVPORTCURYCOST," +
                    "    FSETTLESTATE," +
                    "    FFACTSETTLEDATE," +
                    "    FSETTLEDESC," +
                    "    FORDERNUM," +
                    "    FDATASOURCE," +
                    "    FDESC," +
                    "    FCHECKSTATE," +
                    "    FCREATOR," +
                    "    FCREATETIME," +
                    "    FCHECKUSER," +
                    "    FCHECKTIME" +
                    "   )" +
                    " SELECT" +
                    "    FNUM," +
                    "    FSECURITYCODE," +
                    "    FPORTCODE," +
                    "    FBROKERCODE," +
                    "    FINVMGRCODE," +
                    "    FTRADETYPECODE," +
                    "    FCASHACCCODE," +
                    "    FATTRCLSCODE," +
                    "    FBARGAINDATE," +
                    "    FBARGAINTIME," +
                    "    FSETTLEDATE," +
                    "    FSETTLETIME," +
                    "    null," +
                    "    null," +
                    "    FAUTOSETTLE," +
                    "    FPORTCURYRATE," +
                    "    FBASECURYRATE," +
                    "    FALLOTPROPORTION," +
                    "    FOLDALLOTAMOUNT," +
                    "    FALLOTFACTOR," +
                    "    FTRADEAMOUNT," +
                    "    FTRADEPRICE," +
                    "    FTRADEMONEY," +
                    "    FACCRUEDINTEREST," +
                    "    FBAILMONEY," +
                    "    FFEECODE1," +
                    "    FTRADEFEE1," +
                    "    FFEECODE2," +
                    "    FTRADEFEE2," +
                    "    FFEECODE3," +
                    "    FTRADEFEE3," +
                    "    FFEECODE4," +
                    "    FTRADEFEE4," +
                    "    FFEECODE5," +
                    "    FTRADEFEE5," +
                    "    FFEECODE6," +
                    "    FTRADEFEE6," +
                    "    FFEECODE7," +
                    "    FTRADEFEE7," +
                    "    FFEECODE8," +
                    "    FTRADEFEE8," +
                    "    FTOTALCOST," +
                    "    FCOST," +
                    "    FMCOST," +
                    "    FVCOST," +
                    "    FBASECURYCOST," +
                    "    FMBASECURYCOST," +
                    "    FVBASECURYCOST," +
                    "    FPORTCURYCOST," +
                    "    FMPORTCURYCOST," +
                    "    FVPORTCURYCOST," +
                    "    FSETTLESTATE," +
                    "    FFACTSETTLEDATE," +
                    "    FSETTLEDESC," +
                    "    FORDERNUM," +
                    "    FDATASOURCE," +
                    "    FDESC," +
                    "    FCHECKSTATE," +
                    "    FCREATOR," +
                    "    FCREATETIME," +
                    "    FCHECKUSER," +
                    "    FCHECKTIME" +
                    "   FROM TB_" + sPre + "_DAT_1120200717254300D";
                dbl.executeSql(strSql);

                strSql = "ALTER TABLE Tb_" + sPre +
                    "_Data_SubTrade ADD CONSTRAINT PK_TB_" + sPre +
                    "_DATA_SUBTRADE" +
                    " PRIMARY KEY (FNUM)";
                dbl.executeSql(strSql);

            }
            rs.close();

            sKey = dbl.getConstaintKey("Tb_" + sPre + "_Data_ValMktPrice");
            if (sKey.trim().length() == 0) {
                strSql = "ALTER TABLE Tb_" + sPre +
                    "_Data_ValMktPrice ADD CONSTRAINT PK_Tb_" + sPre +
                    "_Data_ValMktPrice" +
                    " PRIMARY KEY (FValDate,FPortCode,FSecurityCode)";
                dbl.executeSql(strSql);
            }

            sKey = dbl.getConstaintKey("Tb_" + sPre + "_Data_Dividend");
            if (sKey.trim().length() == 0) {
                strSql = "ALTER TABLE Tb_" + sPre +
                    "_Data_Dividend ADD CONSTRAINT PK_Tb_" + sPre +
                    "_Data_Dividend" +
                    " PRIMARY KEY (FSecurityCode,FRecordDate,FDivdendType,FCuryCode)";
                dbl.executeSql(strSql);
            }

            if (!dbl.yssTableExist("Tb_" + sPre + "_Para_Receiver")) {
                strSql = "CREATE TABLE Tb_" + sPre + "_Para_Receiver(" +
                    "    FReceiverCode      VARCHAR2(20)  NOT NULL," +
                    "    FReceiverName      VARCHAR2(50)  NOT NULL," +
                    "    FReceiverShortName VARCHAR2(50)      NULL," +
                    "    FOfficeAddr        VARCHAR2(200)     NULL," +
                    "    FPostalCode        VARCHAR2(20)      NULL," +
                    "    FOperBank          VARCHAR2(100) NOT NULL," +
                    "    FAccountNumber     VARCHAR2(100) NOT NULL," +
                    "    FDesc              VARCHAR2(200)     NULL," +
                    "    FCheckState        NUMBER(1)     NOT NULL," +
                    "    FCreator           VARCHAR2(20)  NOT NULL," +
                    "    FCreateTime        VARCHAR2(20)  NOT NULL," +
                    "    FCheckUser         VARCHAR2(20)      NULL," +
                    "    FCheckTime         VARCHAR2(20)      NULL," +
                    "    CONSTRAINT PK_Tb_" + sPre + "_Para_Receiver" +
                    "    PRIMARY KEY (FReceiverCode))";
                dbl.executeSql(strSql);
            }

            if (!dbl.yssTableExist("Tb_" + sPre + "_Para_BrokerSubBny")) {
                strSql = "CREATE TABLE Tb_" + sPre + "_Para_BrokerSubBny(" +
                    "    FBrokerCode    VARCHAR2(20)  NOT NULL," +
                    "    FExchangeCode  VARCHAR2(20)  NOT NULL," +
                    "    FBrokerIDType  VARCHAR2(20)      NULL," +
                    "    FBrokerID      VARCHAR2(20)      NULL," +
                    "    FClearerIDType VARCHAR2(20)      NULL," +
                    "    FClearerID     VARCHAR2(20)      NULL," +
                    "    FBrokerAccount VARCHAR2(50)      NULL," +
                    "    FDesc          VARCHAR2(100)     NULL," +
                    "    FCheckState    NUMBER(1)     NOT NULL," +
                    "    FCreator       VARCHAR2(20)  NOT NULL," +
                    "    FCreateTime    VARCHAR2(20)  NOT NULL," +
                    "    FCheckUser     VARCHAR2(20)      NULL," +
                    "    FCheckTime     VARCHAR2(20)      NULL," +
                    "    CONSTRAINT Pk_Tb_" + sPre + "_Para_BrokerSubBny" +
                    "    PRIMARY KEY (FBrokerCode,FExchangeCode))";
                dbl.executeSql(strSql);
            }

            if (!dbl.yssTableExist("Tb_" + sPre + "_Para_Purchase")) {
                strSql = "CREATE TABLE Tb_" + sPre + "_Para_Purchase(" +
                    "    FSecurityCode VARCHAR2(20)  NOT NULL," +
                    "    FDepDurCode   VARCHAR2(20)  NOT NULL," +
                    "    FPeriodCode   VARCHAR2(20)  NOT NULL," +
                    "    FPurchaseType VARCHAR2(20)  NOT NULL," +
                    "    FPurchaseRate NUMBER(18,12)     NULL," +
                    "    FDesc         VARCHAR2(100)     NULL," +
                    "    FCheckState   NUMBER(1)     NOT NULL," +
                    "    FCreator      VARCHAR2(20)  NOT NULL," +
                    "    FCreateTime   VARCHAR2(20)  NOT NULL," +
                    "    FCheckUser    VARCHAR2(20)      NULL," +
                    "    FCheckTime    VARCHAR2(20)      NULL," +
                    "    CONSTRAINT PK_Tb_" + sPre + "_Para_Purchase" +
                    "    PRIMARY KEY (FSecurityCode))";
                dbl.executeSql(strSql);
            }

            strSql = "ALTER TABLE TB_" + sPre + "_PARA_SECURITY MODIFY(FSECURITYNAME  VARCHAR2(100))";
            dbl.executeSql(strSql);

            sKey = dbl.getConstaintKey("TB_" + sPre + "_PARA_FEE");
            if (sKey.trim().length() != 0) {
                strSql = "ALTER TABLE TB_" + sPre + "_PARA_FEE DROP CONSTRAINT " +
                    sKey + " CASCADE";
                dbl.executeSql(strSql);
            }

            sKey = dbl.getConstaintKey("Tb_" + sPre + "_Para_Fee");
            if (sKey.trim().length() == 0) {
                strSql =
                    "ALTER TABLE Tb_" + sPre + "_Para_Fee ADD CONSTRAINT PK_Tb_" + sPre + "_Para_Fee" +
                    " PRIMARY KEY (FFEECODE)";
                dbl.executeSql(strSql);
            }

            sKey = dbl.getConstaintKey("TB_" + sPre + "_PARA_INVESTPAY");
            if (sKey.trim().length() != 0) {
                strSql = "ALTER TABLE TB_" + sPre + "_PARA_INVESTPAY DROP CONSTRAINT " + sKey + " CASCADE";
                dbl.executeSql(strSql);
            }

            strSql = "ALTER TABLE Tb_" + sPre +
                "_Para_InvestPay ADD CONSTRAINT PK_Tb_" + sPre +
                "_Para_InvestPay" +
                " PRIMARY KEY (FIVPAYCATCODE,FSTARTDATE,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3)";
            dbl.executeSql(strSql);

            //插入版本信息
            strSql = "insert into tb_fun_version(fassetgroupcode,fvernum,fissuedate,fdesc,fcreatedate,fcreatetime)" +
                "values(" + dbl.sqlString(sPre) + ",'1.0.0.0004'," +
                dbl.sqlDate("2007-11-21") +
                ",'1、交易数据表增加回购处理，增加了回购到期日期字段'," +
                dbl.sqlDate(new java.util.Date()) + "," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + ")";
            dbl.executeSql(strSql);
            /*
                     java.util.Date beginDate = YssFun.toDate("2007-10-08");
                     java.util.Date endDate = YssFun.addDay(new java.util.Date(),-1);
                     java.util.Date date = null;
                     ValuationBean valuation = new ValuationBean();
                     valuation.setYssPub(pub);
                     valuation.setStartDate(beginDate);
                     valuation.setEndDate(endDate);
                     //valuation.setValuationTypes("SecsFX,CashFX,SecsMV,IncomeFX,InvestFX");
                     //valuation.setValuationTypesName("SecsFX,CashFX,SecsMV,IncomeFX,InvestFX");
                     valuation.setValuationTypes("SecsFX,CashFX,SecsMV,IncomeFX");
                     valuation.setValuationTypesName("SecsFX,CashFX,SecsMV,IncomeFX");

                     valuation.setPortCodes("001001");
                     runStatus = new YssStatus();
                     valuation.setRunStatus(runStatus);
                     valuation.doOperation("");*/
//         BaseStgStatDeal recStgStat = null;
//         BaseValDeal valuation = null;
//         int iDays = YssFun.dateDiff(beginDate,endDate);
//         date = beginDate;
//         HashMap hm = null;
//         for (int i=0; i<iDays; i++){
//            date = YssFun.addDay(date,i);
//            recStgStat = (BaseStgStatDeal) pub.
//                  getOperDealCtx().getBean("CashPayRec");
//            recStgStat.setYssPub(pub);
//            recStgStat.stroageStat(date, date, "001001");
//
//            valuation = (BaseValDeal) pub.getOperDealCtx().
//                  getBean("IncomeFX");
//            valuation.setYssPub(pub);
//
//            valuation.initValuation(date, "001001", "IncomeFX",null,null);
//            hm = valuation.getValuationCats((ArrayList)this.hmValMethods.get(portCode));
//            sReInfo = valuation.saveValuationCats(hm).trim();
//         }

            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("调整表结构失败，请重新登陆！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

}
