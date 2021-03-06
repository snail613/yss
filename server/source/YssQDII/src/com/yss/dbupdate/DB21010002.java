package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;
import com.yss.util.YssException;

public class DB21010002
    extends BaseDbUpdate {
    public DB21010002() {
    }

    public void addTableField() throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------TB_FUN_COMMONPARAMSSUB 添加字段 FValueDesc-------------------------------------//
            //通过表名查询主键名
            strPKName = this.getIsNullPKByTableName_DB2("TB_FUN_COMMONPARAMSSUB");
            if (strPKName.length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_FUN_COMMONPARAMSSUB DROP CONSTRAINT " +
                    strPKName);
            }
            if (dbl.yssTableExist("TB_02212008094409")) {
                this.dropTableByTableName("TB_02212008094409");
            }
            dbl.executeSql(
                "RENAME TABLE TB_FUN_COMMONPARAMSSUB TO TB_02212008094409");
            bufSql.append("CREATE TABLE Tb_Fun_CommonParamsSub");
            bufSql.append(" (");
            bufSql.append(" FCPTypeCode VARCHAR(20)   NOT NULL,");
            bufSql.append(" FParamCode  VARCHAR(20)   NOT NULL,");
            bufSql.append(" FPARAMVALUE VARCHAR(1000) NOT NULL,");
            bufSql.append(" FValueDesc  VARCHAR(1000),");
            bufSql.append(" FCHECKSTATE DECIMAL(1)    NOT NULL,");
            bufSql.append(" FCREATOR    VARCHAR(20)   NOT NULL,");
            bufSql.append(" FCREATETIME VARCHAR(20)   NOT NULL,");
            bufSql.append(" FCHECKUSER  VARCHAR(20),");
            bufSql.append(" FCHECKTIME  VARCHAR(20),");
            bufSql.append(" FDESC       VARCHAR(100)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_Fun_CommonParamsSub(");
            bufSql.append(" FCPTypeCode,");
            bufSql.append(" FParamCode,");
            bufSql.append(" FPARAMVALUE,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME,");
            bufSql.append(" FDESC");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FCPTYPECODE,");
            bufSql.append(" FPARAMCODE,");
            bufSql.append(" FPARAMVALUE,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME,");
            bufSql.append(" FDESC");
            bufSql.append(" FROM TB_02212008094409");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            bufSql.delete(0, bufSql.length());

            dbl.executeSql(
                "ALTER TABLE Tb_Fun_CommonParamsSub ADD CONSTRAINT PK_Tb_Fun_ommonPa " +
                "PRIMARY KEY (FCPTypeCode,FParamCode)");
            //-----------------------------------------------------------------------------------------------------------//
            //----------------------------修改字段大小 TB_FUN_SYSDATA FCODE，FNAME ------------------------------//
            //通过表名查询主键名
            strPKName = "";
            strPKName = this.getIsNullPKByTableName_DB2("TB_FUN_SYSDATA ");
            if (strPKName.length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_FUN_SYSDATA DROP CONSTRAINT " +
                    strPKName);
            }
            if (dbl.yssTableExist("TB_02212008100315")) {
                this.dropTableByTableName("TB_02212008100315");
            }
            dbl.executeSql("RENAME TABLE TB_FUN_SYSDATA TO TB_02212008100315");

            bufSql.append("CREATE TABLE TB_FUN_SYSDATA");
            bufSql.append(" (");
            bufSql.append(" FNUM            VARCHAR(20)   NOT NULL,");
            bufSql.append(" FASSETGROUPCODE VARCHAR(20)   NOT NULL,");
            bufSql.append(" FFUNNAME        VARCHAR(50)   NOT NULL,");
            bufSql.append(" FCode           VARCHAR(50),");
            bufSql.append(" FName           VARCHAR(100),");
            bufSql.append(" FUPDATESQL      VARCHAR(4000),");
            bufSql.append(" FDESC           VARCHAR(100),");
            bufSql.append(" FCREATOR        VARCHAR(20)   NOT NULL,");
            bufSql.append(" FCREATEDATE     DATE          NOT NULL,");
            bufSql.append(" FCREATETIME     VARCHAR(20)   NOT NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO TB_FUN_SYSDATA(");
            bufSql.append(" FNUM,");
            bufSql.append(" FASSETGROUPCODE,");
            bufSql.append(" FFUNNAME,");
            bufSql.append(" FCode,");
            bufSql.append(" FName,");
            bufSql.append(" FUPDATESQL,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATEDATE,");
            bufSql.append(" FCREATETIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FNUM,");
            bufSql.append(" FASSETGROUPCODE,");
            bufSql.append(" FFUNNAME,");
            bufSql.append(" FCODE,");
            bufSql.append(" FNAME,");
            bufSql.append(" FUPDATESQL,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATEDATE,");
            bufSql.append(" FCREATETIME");
            bufSql.append(" FROM TB_02212008100315");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            bufSql.delete(0, bufSql.length());

            dbl.executeSql("ALTER TABLE TB_FUN_SYSDATA ADD CONSTRAINT PK_TB_FUN_SYSDATA " +
                           "PRIMARY KEY (FNUM)");
            //-------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0002 通用数据表新增字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 根据组合代码创建表
     * @param sPre String: 组合代码
     * @throws YssException
     */
    public void createTable(String sPre) throws YssException { //彭鹏 2008.1.30 创建
        StringBuffer bufSql = new StringBuffer();
        //--------------------------创建表Tb_XXX_Data_NavData sj edit ---------------------------//
        try {
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_NavData ");
            bufSql.append("(");
            bufSql.append("FNAVDate         DATE          NOT NULL,");
            bufSql.append("FPortCode        VARCHAR(20)  NOT NULL,");
            bufSql.append("FKeyCode         VARCHAR(20)  NOT NULL,");
            bufSql.append("FReTypeCode      VARCHAR(20)  NOT NULL,");
            bufSql.append("FOrderCode       VARCHAR(200) NOT NULL,");
            bufSql.append("FDetail          DECIMAL(1,0)     NOT NULL,");
            bufSql.append("FINVMGRCODE      VARCHAR(20)  NOT NULL,");
            bufSql.append("FKeyName         VARCHAR(200)  NOT NULL,");
            bufSql.append("FCuryCode        VARCHAR(20)  NOT NULL,");
            bufSql.append("FPrice           DECIMAL(20,12),");
            bufSql.append("FOTPrice1        DECIMAL(20,12),");
            bufSql.append("FOTPrice2        DECIMAL(20,12),");
            bufSql.append("FOTPrice3        DECIMAL(20,12),");
            bufSql.append("FSEDOLCode       VARCHAR(20),");
            bufSql.append("FISINCode        VARCHAR(20),");
            bufSql.append("FSParAmt         DECIMAL(20,4),");
            bufSql.append("FBaseCuryRate    DECIMAL(20,15) NOT NULL,");
            bufSql.append("FPortCuryRate    DECIMAL(20,15) NOT NULL,");
            bufSql.append("FMarketValue     DECIMAL(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPortMarketValue DECIMAL(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FCost            DECIMAL(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPortCost        DECIMAL(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FMVValue         DECIMAL(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPortMVValue     DECIMAL(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FFXValue         DECIMAL(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FGradeType1      VARCHAR(20),");
            bufSql.append("FGradeType2      VARCHAR(20),");
            bufSql.append("FGradeType3      VARCHAR(20),");
            bufSql.append("FGradeType4      VARCHAR(20),");
            bufSql.append("FGradeType5      VARCHAR(20),");
            bufSql.append("FGradeType6      VARCHAR(20),");
            bufSql.append("CONSTRAINT PK_Tb_DataNavData PRIMARY KEY (FNAVDATE,FPORTCODE,FORDERCODE,FReTypeCode,FINVMGRCODE,FKEYCODE)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //--------------------------创建表Tb_XXX_Book_Invest ---------------------------//
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Book_Invest ");
            bufSql.append("(");
            bufSql.append("FIBookCode  VARCHAR(20)  NOT NULL,");
            bufSql.append("FIBookName  VARCHAR(50)  NOT NULL,");
            bufSql.append("FBookLink   VARCHAR(100) NOT NULL,");
            bufSql.append("FDesc       VARCHAR(100),");
            bufSql.append("FCheckState DECIMAL(1)   NOT NULL,");
            bufSql.append("FCreator    VARCHAR(20)  NOT NULL,");
            bufSql.append("FCreateTime VARCHAR(20)  NOT NULL,");
            bufSql.append("FCheckUser  VARCHAR(20),");
            bufSql.append("FCheckTime  VARCHAR(20),");
            bufSql.append("CONSTRAINT PK_Tb_" + sPre + "_Invest PRIMARY KEY (FIBookCode)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString()); //ly 添加
            //-------------------------创建表Tb_XXX_Data_Integrated----------------------------//
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_Integrated( ");
            bufSql.append("FNum               VARCHAR(20)        NOT NULL,");
            bufSql.append("FSubNum            VARCHAR(20)        NOT NULL,");
            bufSql.append("FInOutType         DECIMAL(1, 0)      NOT NULL,");
            bufSql.append("FExchangeDate      DATE               NOT NULL,");
            bufSql.append("FSecurityCode      VARCHAR(20)        NOT NULL,");
            bufSql.append("FRelaNum           VARCHAR(20),");
            bufSql.append("FNumType           VARCHAR(20),");
            bufSql.append("FTsfTypeCode       VARCHAR(20)        NOT NULL,");
            bufSql.append("FSubTsfTypeCode    VARCHAR(20)        NOT NULL,");
            bufSql.append("FTradeTypeCode     VARCHAR(20)        NOT NULL,");
            bufSql.append("FPortCode          VARCHAR(20)        NOT NULL,");
            bufSql.append("FAnalysisCode1     VARCHAR(10)        NOT NULL,");
            bufSql.append("FAnalysisCode2     VARCHAR(20)        NOT NULL,");
            bufSql.append("FAnalysisCode3     VARCHAR(20)        NOT NULL,");
            bufSql.append("FAmount            DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FExchangeCost      DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FMExCost           DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FVExCost           DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FPortExCost        DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FMPortExCost       DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FVPortExCost       DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FBaseExCost        DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FMBaseExCost       DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FVBaseExCost       DECIMAL(18, 4)     NOT NULL,");
            bufSql.append("FBaseCuryRate      DECIMAL(18, 15)    NOT NULL,");
            bufSql.append("FPortCuryRate      DECIMAL(18, 15)    NOT NULL,");
            bufSql.append("FSecExDesc         VARCHAR(100),");
            bufSql.append("FDesc              VARCHAR(100),");
            bufSql.append("FCheckState        DECIMAL(1, 0)      NOT NULL,");
            bufSql.append("FCreator           VARCHAR(20)        NOT NULL,");
            bufSql.append("FCreateTime        VARCHAR(20)        NOT NULL,");
            bufSql.append("FCheckUser         VARCHAR(20),");
            bufSql.append("FCheckTime         VARCHAR(20),");
            bufSql.append("CONSTRAINT PK_Tb_Data_egrated PRIMARY KEY (FNum, FSubNum)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());

        } catch (Exception e) {
            throw new YssException("版本1.0.1.0002 创建表出错！", e);
        }
        //---------------------------------------------------------------------------------------------//
    }

    /***
     * 增加表的字段
     */
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //------------------------- CurrencyWay 添加字段 FFactor --------------------------//
            //通过表名查询主键名
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_PARA_CURRENCYWAY"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_PARA_CURRENCYWAY DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_02212008085224")) {
                this.dropTableByTableName("TB_02212008085224");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_PARA_CURRENCYWAY TO TB_02212008085224");
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Para_CurrencyWay");
            bufSql.append(" (");
            bufSql.append(" FCURYCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FPORTCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FMARKCURY   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FQUOTEWAY   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FFactor     DECIMAL(8)   NOT NULL,");
            bufSql.append(" FDESC       VARCHAR(200),");
            bufSql.append(" FCHECKSTATE DECIMAL(1)   NOT NULL,");
            bufSql.append(" FCREATOR    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER  VARCHAR(20),");
            bufSql.append(" FCHECKTIME  VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_" + sPre + "_Para_CurrencyWay(");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FMARKCURY,");
            bufSql.append(" FQUOTEWAY,");
            bufSql.append(" FFactor,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FMARKCURY,");
            bufSql.append(" FQUOTEWAY,");
            bufSql.append(" 0,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_02212008085224");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Para_CurrencyWay ADD CONSTRAINT PK_Tb_ParaencyWay " +
                           "PRIMARY KEY (FCURYCODE,FPORTCODE,FMARKCURY)");
            bufSql.delete(0, bufSql.length());
            //--------------------------------------------------------------------------------//



            //------------------表Tb_XXX_Data_SecRecPay 增加字段 FInOut----------------------------//
            strPKName = "";
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_DATA_SECRECPAY")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("Tb_DATA_SECRECPAY") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_02142008093439")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_02142008093439");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_DATA_SECRECPAY TO TB_02142008093439");
            strPKName = "";
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_SecRecPay ");
            bufSql.append("(");
            bufSql.append("FNUM            VARCHAR(20)    NOT NULL,");
            bufSql.append("FTRANSDATE      DATE           NOT NULL,");
            bufSql.append("FPORTCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE1  VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE2  VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE3  VARCHAR(20)    NOT NULL,");
            bufSql.append("FSECURITYCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append("FTSFTYPECODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("FSUBTSFTYPECODE VARCHAR(20)    NOT NULL,");
            bufSql.append("FCATTYPE        VARCHAR(20)    NOT NULL,");
            bufSql.append("FATTRCLSCODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("FCURYCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("FInOut          DECIMAL(1)     NOT NULL DEFAULT 1,");
            bufSql.append("FMONEY          DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FMMONEY         DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FVMONEY         DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FBASECURYRATE   DECIMAL(20,15) NOT NULL,");
            bufSql.append("FBASECURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FMBASECURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FVBASECURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FPORTCURYRATE   DECIMAL(20,15) NOT NULL,");
            bufSql.append("FPORTCURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FMPORTCURYMONEY DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FVPORTCURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FDATASOURCE     DECIMAL(1)     NOT NULL,");
            bufSql.append("FSTOCKIND       DECIMAL(1)     NOT NULL,");
            bufSql.append("FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append("FCREATOR        VARCHAR(20)    NOT NULL,");
            bufSql.append("FCREATETIME     VARCHAR(20)    NOT NULL,");
            bufSql.append("FCHECKUSER      VARCHAR(20),");
            bufSql.append("FCHECKTIME      VARCHAR(20)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_" + sPre + "_Data_SecRecPay( ");
            bufSql.append(" FNUM,");
            bufSql.append(" FTRANSDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append(" FTSFTYPECODE,");
            bufSql.append(" FSUBTSFTYPECODE,");
            bufSql.append(" FCATTYPE,");
            bufSql.append(" FATTRCLSCODE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FInOut,");
            bufSql.append(" FMONEY,");
            bufSql.append(" FMMONEY,");
            bufSql.append(" FVMONEY,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FBASECURYMONEY,");
            bufSql.append(" FMBASECURYMONEY,");
            bufSql.append(" FVBASECURYMONEY,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FPORTCURYMONEY,");
            bufSql.append(" FMPORTCURYMONEY,");
            bufSql.append(" FVPORTCURYMONEY,");
            bufSql.append(" FDATASOURCE,");
            bufSql.append(" FSTOCKIND,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" ) SELECT ");
            bufSql.append(" FNUM,");
            bufSql.append(" FTRANSDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append(" FTSFTYPECODE,");
            bufSql.append(" FSUBTSFTYPECODE,");
            bufSql.append(" FCATTYPE,");
            bufSql.append(" FATTRCLSCODE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" 1,");
            bufSql.append(" FMONEY,");
            bufSql.append(" FMMONEY,");
            bufSql.append(" FVMONEY,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FBASECURYMONEY,");
            bufSql.append(" FMBASECURYMONEY,");
            bufSql.append(" FVBASECURYMONEY,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FPORTCURYMONEY,");
            bufSql.append(" FMPORTCURYMONEY,");
            bufSql.append(" FVPORTCURYMONEY,");
            bufSql.append(" FDATASOURCE,");
            bufSql.append(" FSTOCKIND,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_02142008093439");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Data_SecRecPay ADD CONSTRAINT PK_Tb_DatacRecPay PRIMARY KEY (FNUM)");

            //------------------表Tb_XXX_Data_CashPayRec 增加字段 FInOut----------------------------//
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_Data_CashPayRec")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("Tb_Data_CashPayRec") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_02142008093438")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_02142008093438");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_DATA_CashPayRec TO TB_02142008093438");
            strPKName = "";
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_CashPayRec ");
            bufSql.append("(");
            bufSql.append("FNUM            VARCHAR(20)    NOT NULL,");
            bufSql.append("FTRANSDATE      DATE           NOT NULL,");
            bufSql.append("FPORTCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE1  VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE2  VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE3  VARCHAR(20)    NOT NULL,");
            bufSql.append("FCASHACCCODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("FTSFTYPECODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("FSUBTSFTYPECODE VARCHAR(20)    NOT NULL,");
            bufSql.append("FCURYCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("FInOut          DECIMAL(1)     NOT NULL DEFAULT 1,");
            bufSql.append("FMONEY          DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FBASECURYRATE   DECIMAL(20,15) NOT NULL,");
            bufSql.append("FBASECURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FPORTCURYRATE   DECIMAL(20,15) NOT NULL,");
            bufSql.append("FPORTCURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FDATASOURCE     DECIMAL(1)     NOT NULL,");
            bufSql.append("FSTOCKIND       DECIMAL(1)     NOT NULL,");
            bufSql.append("FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append("FCREATOR        VARCHAR(20)    NOT NULL,");
            bufSql.append("FCREATETIME     VARCHAR(20)    NOT NULL,");
            bufSql.append("FCHECKUSER      VARCHAR(20),");
            bufSql.append("FCHECKTIME      VARCHAR(20)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_" + sPre + "_Data_CashPayRec( ");
            bufSql.append(" FNUM,");
            bufSql.append(" FTRANSDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FCASHACCCODE,");
            bufSql.append(" FTSFTYPECODE,");
            bufSql.append(" FSUBTSFTYPECODE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FInOut,");
            bufSql.append(" FMONEY,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FBASECURYMONEY,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FPORTCURYMONEY,");
            bufSql.append(" FDATASOURCE,");
            bufSql.append(" FSTOCKIND,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(") SELECT ");
            bufSql.append(" FNUM,");
            bufSql.append(" FTRANSDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FCASHACCCODE,");
            bufSql.append(" FTSFTYPECODE,");
            bufSql.append(" FSUBTSFTYPECODE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" 1,");
            bufSql.append(" FMONEY,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FBASECURYMONEY,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FPORTCURYMONEY,");
            bufSql.append(" FDATASOURCE,");
            bufSql.append(" FSTOCKIND,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_02142008093438 ");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Data_CashPayRec ADD CONSTRAINT PK_Tb_DatahPayRec PRIMARY KEY (FNUM)");
            //----Tb_XXX_Para_Receiver 增加 FPortCode,FCashAccCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 字段----
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_" + sPre + "_Para_Receiver")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("Tb_Para_Receiver") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_02212008032237")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_02212008032237");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_Para_Receiver TO TB_02212008032237");
            strPKName = "";
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Para_Receiver");
            bufSql.append("(");
            bufSql.append("FRECEIVERCODE      VARCHAR(20)  NOT NULL,");
            bufSql.append("FRECEIVERNAME      VARCHAR(50)  NOT NULL,");
            bufSql.append("FRECEIVERSHORTNAME VARCHAR(50),");
            bufSql.append("FOFFICEADDR        VARCHAR(200),");
            bufSql.append("FPOSTALCODE        VARCHAR(20),");
            bufSql.append("FOPERBANK          VARCHAR(100) NOT NULL,");
            bufSql.append("FACCOUNTNUMBER     VARCHAR(100) NOT NULL,");
            bufSql.append("FCURYCODE          VARCHAR(20),");
            bufSql.append("FPortCode          VARCHAR(20),");
            bufSql.append("FCashAccCode       VARCHAR(10),");
            bufSql.append("FAnalysisCode1     VARCHAR(20),");
            bufSql.append("FAnalysisCode2     VARCHAR(20),");
            bufSql.append("FAnalysisCode3     VARCHAR(20),");
            bufSql.append("FDESC              VARCHAR(200),");
            bufSql.append("FCHECKSTATE        DECIMAL(1)   NOT NULL,");
            bufSql.append("FCREATOR           VARCHAR(20)  NOT NULL,");
            bufSql.append("FCREATETIME        VARCHAR(20)  NOT NULL,");
            bufSql.append("FCHECKUSER         VARCHAR(20),");
            bufSql.append("FCHECKTIME         VARCHAR(20)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Para_Receiver(");
            bufSql.append(" FRECEIVERCODE,");
            bufSql.append(" FRECEIVERNAME,");
            bufSql.append(" FRECEIVERSHORTNAME,");
            bufSql.append(" FOFFICEADDR,");
            bufSql.append(" FPOSTALCODE,");
            bufSql.append(" FOPERBANK,");
            bufSql.append(" FACCOUNTNUMBER,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FCashAccCode,");
            bufSql.append(" FAnalysisCode1,");
            bufSql.append(" FAnalysisCode2,");
            bufSql.append(" FAnalysisCode3,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(") SELECT ");
            bufSql.append(" FRECEIVERCODE,");
            bufSql.append(" FRECEIVERNAME,");
            bufSql.append(" FRECEIVERSHORTNAME,");
            bufSql.append(" FOFFICEADDR,");
            bufSql.append(" FPOSTALCODE,");
            bufSql.append(" FOPERBANK,");
            bufSql.append(" FACCOUNTNUMBER,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" ' ',");
            bufSql.append(" ' ',");
            bufSql.append(" ' ',");
            bufSql.append(" ' ',");
            bufSql.append(" ' ',");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_02212008032237");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Para_Receiver ADD CONSTRAINT PK_Tb_Paraeceiver " +
                           " PRIMARY KEY (FRECEIVERCODE)");
            //-----------Tb_XXX_Cash_Command 增加 FRelaNum,FNumType 字段--------------------
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_CASH_COMMAND")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("Tb_CASH_COMMAND") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_02212008030849")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_02212008030849");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_CASH_COMMAND TO TB_02212008030849");
            strPKName = "";
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Cash_Command");
            bufSql.append("(");
            bufSql.append("FNUM          VARCHAR(20)    NOT NULL,");
            bufSql.append("FCOMMANDDATE  DATE           NOT NULL,");
            bufSql.append("FCOMMANDTIME  VARCHAR(20)    NOT NULL,");
            bufSql.append("FACCOUNTDATE  DATE           NOT NULL,");
            bufSql.append("FACCOUNTTIME  VARCHAR(20)    NOT NULL,");
            bufSql.append("FORDER        DECIMAL(8)     NOT NULL,");
            bufSql.append("FPayerName    VARCHAR(200)   NOT NULL,");
            bufSql.append("FPAYERBANK    VARCHAR(100)   NOT NULL,");
            bufSql.append("FPAYERACCOUNT VARCHAR(100)   NOT NULL,");
            bufSql.append("FPAYCURY      VARCHAR(20)    NOT NULL,");
            bufSql.append("FPAYMONEY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FREFRATE      DECIMAL(20,15),");
            bufSql.append("FRecerName    VARCHAR(200)   NOT NULL,");
            bufSql.append("FRECERBANK    VARCHAR(100)   NOT NULL,");
            bufSql.append("FRECERACCOUNT VARCHAR(100)   NOT NULL,");
            bufSql.append("FRECMONEY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FRECCURY      VARCHAR(20)    NOT NULL,");
            bufSql.append("FCASHUSAGE    VARCHAR(100),");
            bufSql.append("FRelaNum      VARCHAR(20),");
            bufSql.append("FNumType      VARCHAR(20),");
            bufSql.append("FDESC         VARCHAR(100),");
            bufSql.append("FCHECKSTATE   DECIMAL(1)     NOT NULL,");
            bufSql.append("FCREATOR      VARCHAR(20)    NOT NULL,");
            bufSql.append("FCREATETIME   VARCHAR(20)    NOT NULL,");
            bufSql.append("FCHECKUSER    VARCHAR(20),");
            bufSql.append("FCHECKTIME    VARCHAR(20)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Cash_Command(");
            bufSql.append(" FNUM,");
            bufSql.append(" FCOMMANDDATE,");
            bufSql.append(" FCOMMANDTIME,");
            bufSql.append(" FACCOUNTDATE,");
            bufSql.append(" FACCOUNTTIME,");
            bufSql.append(" FORDER,");
            bufSql.append(" FPayerName,");
            bufSql.append(" FPAYERBANK,");
            bufSql.append(" FPAYERACCOUNT,");
            bufSql.append(" FPAYCURY,");
            bufSql.append(" FPAYMONEY,");
            bufSql.append(" FREFRATE,");
            bufSql.append(" FRecerName,");
            bufSql.append(" FRECERBANK,");
            bufSql.append(" FRECERACCOUNT,");
            bufSql.append(" FRECMONEY,");
            bufSql.append(" FRECCURY,");
            bufSql.append(" FCASHUSAGE,");
            bufSql.append(" FRelaNum,");
            bufSql.append(" FNumType,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(") SELECT ");
            bufSql.append(" FNUM,");
            bufSql.append(" FCOMMANDDATE,");
            bufSql.append(" FCOMMANDTIME,");
            bufSql.append(" FACCOUNTDATE,");
            bufSql.append(" FACCOUNTTIME,");
            bufSql.append(" FORDER,");
            bufSql.append(" FPAYERNAME,");
            bufSql.append(" FPAYERBANK,");
            bufSql.append(" FPAYERACCOUNT,");
            bufSql.append(" FPAYCURY,");
            bufSql.append(" FPAYMONEY,");
            bufSql.append(" FREFRATE,");
            bufSql.append(" FRECERNAME,");
            bufSql.append(" FRECERBANK,");
            bufSql.append(" FRECERACCOUNT,");
            bufSql.append(" FRECMONEY,");
            bufSql.append(" FRECCURY,");
            bufSql.append(" FCASHUSAGE,");
            bufSql.append(" ' ',");
            bufSql.append(" ' ',");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_02212008030849");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Cash_Command ADD CONSTRAINT PK_Tb_CashCommand " +
                           " PRIMARY KEY (FNUM)");
            //-----TB_XXX_DATA_RATETRADE 增加 FReceiverCode,FPayCode字段 ---------------
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_Data_RateTrade")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("Tb_Data_RateTrade") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_02212008031949")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_02212008031949");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_Data_RateTrade TO TB_02212008031949");
            strPKName = "";
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_RateTrade ");
            bufSql.append("(");
            bufSql.append("FNUM            VARCHAR(15)    NOT NULL,");
            bufSql.append("FPORTCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE1  VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE2  VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE3  VARCHAR(20)    NOT NULL,");
            bufSql.append("FBANALYSISCODE1 VARCHAR(20)    NOT NULL,");
            bufSql.append("FBANALYSISCODE2 VARCHAR(20)    NOT NULL,");
            bufSql.append("FBANALYSISCODE3 VARCHAR(20)    NOT NULL,");
            bufSql.append("FBPORTCODE      VARCHAR(20)    NOT NULL,");
            bufSql.append("FTRADETYPE      DECIMAL(1)     NOT NULL,");
            bufSql.append("FCATTYPE        DECIMAL(1)     NOT NULL,");
            bufSql.append("FBCASHACCCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append("FBCURYCODE      VARCHAR(20)    NOT NULL,");
            bufSql.append("FSCURYCODE      VARCHAR(20)    NOT NULL,");
            bufSql.append("FSCASHACCCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append("FReceiverCode   VARCHAR(20),");
            bufSql.append("FPayCode        VARCHAR(20),");
            bufSql.append("FTRADEDATE      DATE           NOT NULL,");
            bufSql.append("FTRADETIME      VARCHAR(20)    NOT NULL,");
            bufSql.append("FSETTLEDATE     DATE           NOT NULL,");
            bufSql.append("FSETTLETIME     VARCHAR(20)    NOT NULL,");
            bufSql.append("FEXCURYRATE     DECIMAL(20,15) NOT NULL,");
            bufSql.append("FBSETTLEDATE    DATE           NOT NULL,");
            bufSql.append("FBSETTLETIME    VARCHAR(20)    NOT NULL,");
            bufSql.append("FBMONEY         DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FSMONEY         DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FBCURYFEE       DECIMAL(18,4),");
            bufSql.append("FSCURYFEE       DECIMAL(18,4),");
            bufSql.append("FBASEMONEY      DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FPORTMONEY      DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FLONGCURYRATE   DECIMAL(18,12),");
            bufSql.append("FRATEFX         DECIMAL(18,4),");
            bufSql.append("FUPDOWN         DECIMAL(18,4),");
            bufSql.append("FDESC           VARCHAR(100),");
            bufSql.append("FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append("FCREATOR        VARCHAR(20)    NOT NULL,");
            bufSql.append("FCREATETIME     VARCHAR(20)    NOT NULL,");
            bufSql.append("FCHECKUSER      VARCHAR(20),");
            bufSql.append("FCHECKTIME      VARCHAR(20)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Data_RateTrade(");
            bufSql.append(" FNUM,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FBANALYSISCODE1,");
            bufSql.append(" FBANALYSISCODE2,");
            bufSql.append(" FBANALYSISCODE3,");
            bufSql.append(" FBPORTCODE,");
            bufSql.append(" FTRADETYPE,");
            bufSql.append(" FCATTYPE,");
            bufSql.append(" FBCASHACCCODE,");
            bufSql.append(" FBCURYCODE,");
            bufSql.append(" FSCURYCODE,");
            bufSql.append(" FSCASHACCCODE,");
            bufSql.append(" FReceiverCode,");
            bufSql.append(" FPayCode,");
            bufSql.append(" FTRADEDATE,");
            bufSql.append(" FTRADETIME,");
            bufSql.append(" FSETTLEDATE,");
            bufSql.append(" FSETTLETIME,");
            bufSql.append(" FEXCURYRATE,");
            bufSql.append(" FBSETTLEDATE,");
            bufSql.append(" FBSETTLETIME,");
            bufSql.append(" FBMONEY,");
            bufSql.append(" FSMONEY,");
            bufSql.append(" FBCURYFEE,");
            bufSql.append(" FSCURYFEE,");
            bufSql.append(" FBASEMONEY,");
            bufSql.append(" FPORTMONEY,");
            bufSql.append(" FLONGCURYRATE,");
            bufSql.append(" FRATEFX,");
            bufSql.append(" FUPDOWN,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(") SELECT ");
            bufSql.append(" FNUM,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FBANALYSISCODE1,");
            bufSql.append(" FBANALYSISCODE2,");
            bufSql.append(" FBANALYSISCODE3,");
            bufSql.append(" FBPORTCODE,");
            bufSql.append(" FTRADETYPE,");
            bufSql.append(" FCATTYPE,");
            bufSql.append(" FBCASHACCCODE,");
            bufSql.append(" FBCURYCODE,");
            bufSql.append(" FSCURYCODE,");
            bufSql.append(" FSCASHACCCODE,");
            bufSql.append(" ' ',");
            bufSql.append(" ' ',");
            bufSql.append(" FTRADEDATE,");
            bufSql.append(" FTRADETIME,");
            bufSql.append(" FSETTLEDATE,");
            bufSql.append(" FSETTLETIME,");
            bufSql.append(" FEXCURYRATE,");
            bufSql.append(" FBSETTLEDATE,");
            bufSql.append(" FBSETTLETIME,");
            bufSql.append(" FBMONEY,");
            bufSql.append(" FSMONEY,");
            bufSql.append(" FBCURYFEE,");
            bufSql.append(" FSCURYFEE,");
            bufSql.append(" FBASEMONEY,");
            bufSql.append(" FPORTMONEY,");
            bufSql.append(" FLONGCURYRATE,");
            bufSql.append(" FRATEFX,");
            bufSql.append(" FUPDOWN,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append("  FROM TB_02212008031949");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Data_RateTrade ADD CONSTRAINT PK_Tb_DatateTrade " +
                           " PRIMARY KEY (FNUM)");
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0002 增加表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
