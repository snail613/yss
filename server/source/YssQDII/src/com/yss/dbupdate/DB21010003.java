package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.*;

public class DB21010003
    extends BaseDbUpdate {
    public DB21010003() {
    }

    //创建带组合群号的表
    public void createTable(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer(10000);
        try {
            //--------------------------创建表Tb_001_Vch_BuildLink生成凭证链接表---------------------------//
            if (!dbl.yssTableExist("TB_" + sPre + "_Vch_BuildLink")) {
                bufSql.append("CREATE TABLE Tb_" + sPre + "_Vch_BuildLink ");
                bufSql.append("(");
                bufSql.append("FProjectCode VARCHAR(20)  NOT NULL,");
                bufSql.append("FAttrCode    VARCHAR(20)  NOT NULL,");
                bufSql.append("FAttrName    VARCHAR(50)  NOT NULL,");
                bufSql.append("FDesc        VARCHAR(100),");
                bufSql.append("FCheckState  DECIMAL(1)   NOT NULL,");
                bufSql.append("FCreator     VARCHAR(20)  NOT NULL,");
                bufSql.append("FCreateTime  VARCHAR(20)  NOT NULL,");
                bufSql.append("FCheckUser   VARCHAR(20),");
                bufSql.append("FCheckTime   VARCHAR(20),");
                bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                              "_Vch_Buil PRIMARY KEY (FProjectCode,FAttrCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_Vch_Project")) {
                bufSql.delete(0, bufSql.length());
                //--------------------------创建表Tb_001_Vch_Project生成凭证方案表---------------------------//
                bufSql.append("CREATE TABLE Tb_" + sPre + "_Vch_Project ");
                bufSql.append("(");
                bufSql.append("FProjectCode  VARCHAR(20)  NOT NULL,");
                bufSql.append("FProjectName  VARCHAR(20)  NOT NULL,");
                bufSql.append("FExeOrderCode DECIMAL(1)   NOT NULL,");
                bufSql.append("FExBuild      DECIMAL(1)   NOT NULL DEFAULT 0,");
                bufSql.append("FExCheck      DECIMAL(1)   NOT NULL DEFAULT 0,");
                bufSql.append("FExInsert     DECIMAL(1)   NOT NULL DEFAULT 0,");
                bufSql.append("FDesc         VARCHAR(100),");
                bufSql.append("FCheckState   DECIMAL(1)   NOT NULL,");
                bufSql.append("FCreator      VARCHAR(20)  NOT NULL,");
                bufSql.append("FCreateTime   VARCHAR(20)  NOT NULL,");
                bufSql.append("FCheckUser    VARCHAR(20),");
                bufSql.append("FCheckTime    VARCHAR(20),");
                bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                              "_Vch_Proj PRIMARY KEY (FProjectCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_Comp_IndexCfg")) {
                bufSql.delete(0, bufSql.length());
                //--------------------------创建表 Tb_Comp_IndexCfg 监控指标配置-----------------------------------//
                bufSql.append("CREATE TABLE Tb_" + sPre + "_Comp_IndexCfg( ");
                bufSql.append("FIndexCfgCode       VARCHAR(20)      NOT NULL,");
                bufSql.append("FIndexCfgName    VARCHAR(50)   NOT NULL,");
                bufSql.append("FIndexType          VARCHAR(20)      NOT NULL,");
                bufSql.append("FBeanId             VARCHAR(30),");
                bufSql.append("FCompParam          VARCHAR(20)      NOT NULL,");
                bufSql.append("FRepCod             VARCHAR(20),");
                bufSql.append("FIndexDS            CLOB(18M),");
                bufSql.append("FMemoyWay           VARCHAR(20)      NOT NULL,");
                bufSql.append("FTgtTableView       VARCHAR(30),");
                bufSql.append("FBeforeComp         VARCHAR(20)      NOT NULL,");
                bufSql.append("FFinalComp          VARCHAR(20)      NOT NULL,");
                bufSql.append("FWarnAnalysis       VARCHAR(500),");
                bufSql.append("FViolateAnalysis    VARCHAR(500),");
                bufSql.append("FForbidAnalysis     VARCHAR(500),");
                bufSql.append("FDesc               VARCHAR(100),");
                bufSql.append("FCheckState         DECIMAL(1, 0)    NOT NULL,");
                bufSql.append("FCreator            VARCHAR(20)      NOT NULL,");
                bufSql.append("FCreateTime         VARCHAR(20)      NOT NULL,");
                bufSql.append("FCheckUser          VARCHAR(20),");
                bufSql.append("FCheckTime          VARCHAR(20),");
                bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                              "ndexCfg PRIMARY KEY (FIndexCfgCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_Comp_ResultData")) {
                //--------------------------创建表 Tb_Comp_ResultData 监控结果------------------------------------//
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE Tb_" + sPre + "_Comp_ResultData ");
                bufSql.append("(");
                bufSql.append("FCompDate        DATE             NOT NULL,");
                bufSql.append("FPortCode        VARCHAR(20)      NOT NULL,");
                bufSql.append("FIndexCfgCode    VARCHAR(20)      NOT NULL,");
                bufSql.append("FCompResult      VARCHAR(20)      NOT NULL,");
                bufSql.append("FDesc            VARCHAR(100),");
                bufSql.append("FCheckState      DECIMAL(1, 0)    NOT NULL,");
                bufSql.append("FCreator         VARCHAR(20)      NOT NULL,");
                bufSql.append("FCreateTime      VARCHAR(20)      NOT NULL,");
                bufSql.append("FCheckUser       VARCHAR(20),");
                bufSql.append("FCheckTime       VARCHAR(20),");
                bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                              "ultData PRIMARY KEY (FCompDate, FPortCode, FIndexCfgCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("Tb_" + sPre + "_Comp_PortIndexLink")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE Tb_" + sPre + "_Comp_PortIndexLink ");
                bufSql.append(" ( ");
                bufSql.append(" FPortCode     VARCHAR(20)  NOT NULL,");
                bufSql.append(" FIndexCfgCode VARCHAR(20)  NOT NULL,");
                bufSql.append(" FCtlGrpCode   VARCHAR(20)  NOT NULL,");
                bufSql.append(" FCtlCode      VARCHAR(20)  NOT NULL,");
                bufSql.append(" FCtlValue     VARCHAR(100),");
                bufSql.append(" FDesc         VARCHAR(100),");
                bufSql.append(" FCheckState   DECIMAL(1)   NOT NULL,");
                bufSql.append(" FCreator      VARCHAR(20) NOT NULL,");
                bufSql.append(" FCreateTime   VARCHAR(20) NOT NULL,");
                bufSql.append(" FCheckUser    VARCHAR(20),");
                bufSql.append(" FCheckTime    VARCHAR(20),");
                bufSql.append(" CONSTRAINT PK_Tb_" + sPre + "dexLink ");
                bufSql.append(" PRIMARY KEY (FPortCode,FIndexCfgCode,FCtlGrpCode,FCtlCode)");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_PFOper_SchProject")) {
                bufSql.delete(0, bufSql.length());
                //----------------------------------------------------------------------------------------------//
                //--------------------------创建表 Tb_PFOper_SchProject 调度方案设置------------------------------//
                bufSql.append("CREATE TABLE Tb_" + sPre + "_PFOper_SchProject( ");
                bufSql.append("FProjectCode     VARCHAR(20)      NOT NULL,");
                bufSql.append("FFunModules      VARCHAR(20)      NOT NULL,");
                bufSql.append("FAttrCode        CLOB(1M),");
                bufSql.append("FExeOrderCode    DECIMAL(3, 0)    NOT NULL,");
                bufSql.append("FProjectName     VARCHAR(50)      NOT NULL,");
                bufSql.append("FDesc            VARCHAR(100),");
                bufSql.append("FCheckState      DECIMAL(1, 0)    NOT NULL,");
                bufSql.append("FCreator         VARCHAR(20)      NOT NULL,");
                bufSql.append("FCreateTime      VARCHAR(20)      NOT NULL,");
                bufSql.append("FCheckUser       VARCHAR(20),");
                bufSql.append("FCheckTime       VARCHAR(20),");
                bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                              "Project PRIMARY KEY (FProjectCode, FFunModules, FExeOrderCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_PFOper_PUBPARA")) {
                bufSql.delete(0, bufSql.length());
                //---------------------------------------------------------------------------------------------//
                //--------------------------创建表TB_001_PFOper_PUBPARA通用参数类型设定表---------------------------//
                bufSql.append("CREATE TABLE Tb_" + sPre + "_PFOper_PUBPARA ");
                bufSql.append("(");
                bufSql.append("FPubParaCode   VARCHAR(20)  NOT NULL,");
                bufSql.append("FParaGroupCode VARCHAR(50)  NOT NULL,");
                bufSql.append("FParaId        DECIMAL(2)   NOT NULL DEFAULT 0,");
                bufSql.append("FPubParaName   VARCHAR(50)  NOT NULL,");
                bufSql.append("FCtlGrpCode    VARCHAR(20),");
                bufSql.append("FCtlCode       VARCHAR(20)  NOT NULL DEFAULT,");
                bufSql.append("FCtlValue      VARCHAR(4000),"); //2008.05.22 蒋锦 修改 将长度由1000改为4000
                bufSql.append("FOrderCode     VARCHAR(50)  NOT NULL,");
                bufSql.append("FDesc          VARCHAR(100),");
                bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                              "_PFOper_P PRIMARY KEY (FPubParaCode,FParaId,FCtlCode)"); //去处FParaGroupCode作为主键 sj edit 20080530
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_" + sPre + "_PFOper_ValCompData")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE TB_" + sPre + "_PFOper_ValCompData(");
                bufSql.append(" FPortCode             VARCHAR(20)       NOT NULL,");
                bufSql.append(" FComProjectCode       VARCHAR(20)       NOT NULL,");
                bufSql.append(" FGZKeyCode            VARCHAR(480)      NOT NULL,");
                bufSql.append(" FCWKeyCode            VARCHAR(480)      NOT NULL,");
                bufSql.append(" FGZKeyName            VARCHAR(500)      NOT NULL,");
                bufSql.append(" FCWKeyName            VARCHAR(500)      NOT NULL,");
                bufSql.append(" FGZCost               DECIMAL(18, 4)    NOT NULL WITH DEFAULT 0,");
                bufSql.append(" FGZPortCost           DECIMAL(18, 4)    NOT NULL WITH DEFAULT 0,");
                bufSql.append(" FGZMarketValue        DECIMAL(18, 4)    NOT NULL WITH DEFAULT 0,");
                bufSql.append(" FGZPortMarketValue    DECIMAL(18, 4)    NOT NULL WITH DEFAULT 0,");
                bufSql.append(" FCWCost               DECIMAL(18, 4)    NOT NULL WITH DEFAULT 0,");
                bufSql.append(" FCWPortCost           DECIMAL(18, 4)    NOT NULL WITH DEFAULT 0,");
                bufSql.append(" FCWMarketValue        DECIMAL(18, 4)    NOT NULL WITH DEFAULT 0,");
                bufSql.append(" FCWPortMarketValue    DECIMAL(18, 4)    NOT NULL WITH DEFAULT 0,");
                bufSql.append(" FGZAmount             DECIMAL(20, 4),");
                bufSql.append(" FCWAmount             DECIMAL(20, 4),");
                bufSql.append(" FOrder                DECIMAL(10, 0),");
                bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_ompData PRIMARY KEY (FPortCode, FComProjectCode, FGZKeyCode, FCWKeyCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003 创建表出错！", e);
        }
    }

    //创建 不 带组合群号的表
    public void createTable() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        try {
            if (!dbl.yssTableExist("TB_Base_SubjectAttr")) {
                //--------------------------创建表TB_Base_SubjectAttr凭证科目属性设置表---------------------------//
                bufSql.append("CREATE TABLE TB_Base_SubjectAttr ");
                bufSql.append("(");
                bufSql.append("FSubAttrCode VARCHAR(20)  NOT NULL,");
                bufSql.append("FSubAttrName VARCHAR(200) NOT NULL,");
                bufSql.append("FSubType     VARCHAR(20)  NOT NULL,");
                bufSql.append("FDesc        VARCHAR(100),");
                bufSql.append("FCheckState  DECIMAL(1)   NOT NULL,");
                bufSql.append("FCreator     VARCHAR(20)  NOT NULL,");
                bufSql.append("FCreateTime  VARCHAR(20)  NOT NULL,");
                bufSql.append("FCheckUser   VARCHAR(20),");
                bufSql.append("FCheckTime   VARCHAR(20),");
                bufSql.append(
                    "CONSTRAINT PK_TB_SubjectAttr PRIMARY KEY (FSubAttrCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("Tb_PFSys_FaceCfgInfo")) {
                //--------------------------创建表 Tb_PF_FaceCfgInfo 界面配置信息表---------------------------//
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE Tb_PFSys_FaceCfgInfo");
                bufSql.append("( ");
                bufSql.append("FCtlGrpCode VARCHAR(20)   NOT NULL,");
                bufSql.append("FCtlCode    VARCHAR(20)   NOT NULL,");
                bufSql.append("FCtlGrpName VARCHAR(50)   NOT NULL,");
                bufSql.append("FParamIndex DECIMAL(3)      NOT NULL,");
                bufSql.append("FCtlType    DECIMAL(1)      NOT NULL,");
                bufSql.append("FParam      VARCHAR(2000),");
                bufSql.append("FCtlInd     VARCHAR(100),");
                bufSql.append("FFunModules VARCHAR(20)   NOT NULL,");
                bufSql.append("FCheckState DECIMAL(1)      NOT NULL,");
                bufSql.append("FCreator    VARCHAR(20)   NOT NULL,");
                bufSql.append("FCreateTime VARCHAR(20)   NOT NULL,");
                bufSql.append("FCheckUser  VARCHAR(20),");
                bufSql.append("FCheckTime  VARCHAR(20),");
                bufSql.append(
                    " CONSTRAINT PK_Tb_FaceCf PRIMARY KEY (FCtlGrpCode,FCtlCode)");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_PFSys_InOutCfg")) {
                //新增Tb_PF_InOutCfg表
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_PFSys_InOutCfg ");
                bufSql.append(" (");
                bufSql.append(" FINOUTCODE    VARCHAR(20)   NOT NULL,");
                bufSql.append(" FINOUTNAME    VARCHAR(50)   NOT NULL,");
                bufSql.append(" FOutCfgScript CLOB NOT NULL,"); //------ modify by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
                bufSql.append(" FInCfgScript  CLOB NOT NULL,"); //------ modify by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
                bufSql.append(" FDESC         VARCHAR(100),");
                bufSql.append(" FCHECKSTATE   DECIMAL(1)      NOT NULL,");
                bufSql.append(" FCREATOR      VARCHAR(20)   NOT NULL,");
                bufSql.append(" FCREATETIME   VARCHAR(20)   NOT NULL,");
                bufSql.append(" FCHECKUSER    VARCHAR(20),");
                bufSql.append(" FCHECKTIME    VARCHAR(20),");
                bufSql.append(
                    "  CONSTRAINT PK_Tb_InOutCfg PRIMARY KEY (FINOUTCODE)");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_PFSys_ValCompare")) {
                //创建TB_PF_ValCompare表
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_PFSys_ValCompare( ");
                bufSql.append(" FComProjectCode    VARCHAR(20)      NOT NULL,");
                bufSql.append(" FComProjectName    VARCHAR(50)      NOT NULL,");
                bufSql.append(" FComScript         VARCHAR(3000)    NOT NULL,");
                bufSql.append(" FDesc              VARCHAR(200),");
                bufSql.append(" FCheckState        DECIMAL(1)       NOT NULL,");
                bufSql.append(" FCreator           VARCHAR(20)      NOT NULL,");
                bufSql.append(" FCreateTime        VARCHAR(20)      NOT NULL,");
                bufSql.append(" FCheckUser         VARCHAR(20),");
                bufSql.append(" FCheckTime         VARCHAR(20),");
                bufSql.append(
                    " CONSTRAINT PK_TB_ValCompare PRIMARY KEY (FComProjectCode)");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_PFSys_OperFunExtend")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE TB_PFSys_OperFunExtend(");
                bufSql.append(" FExtCode        VARCHAR(20)      NOT NULL,");
                bufSql.append(" FExtName        VARCHAR(50)      NOT NULL,");
                bufSql.append(" FPubParaCode    VARCHAR(20),");
                bufSql.append(" FLinkModule     VARCHAR(20),");
                bufSql.append(" FExtScript      CLOB(18M)        NOT NULL,");
                bufSql.append(" FEnable         DECIMAL(1, 0)    NOT NULL,");
                bufSql.append(" FDesc           VARCHAR(100),");
                bufSql.append(" FCheckState     DECIMAL(1, 0)    NOT NULL,");
                bufSql.append(" FCreator        VARCHAR(20)      NOT NULL,");
                bufSql.append(" FCreateTime     VARCHAR(20)      NOT NULL,");
                bufSql.append(" FCheckUser      VARCHAR(20),");
                bufSql.append(" FCheckTime      VARCHAR(20),");
                bufSql.append(" CONSTRAINT PK_TBPFSynExtend PRIMARY KEY (FExtCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003 创建表出错！", e);
        }
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
            //------------------------- TB_001_TA_TRADE  添加字段 FMarkDate --------------------------//
            //通过表名查询主键名
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("TB_TA_TRADE"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_TA_TRADE DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_03052008101129")) {
                this.dropTableByTableName("TB_03052008101129");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_TA_TRADE TO TB_03052008101129");
            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_TA_TRADE"));
            bufSql.append(" (");
            bufSql.append("FNUM           VARCHAR(20)    NOT NULL,");
            bufSql.append("FSELLNETCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append("FPORTCLSCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append("FPORTCODE      VARCHAR(20),");
            bufSql.append("FSELLTYPE      VARCHAR(20)    NOT NULL,");
            bufSql.append("FCURYCODE      VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE1 VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE2 VARCHAR(20)    NOT NULL,");
            bufSql.append("FANALYSISCODE3 VARCHAR(20)    NOT NULL,");
            bufSql.append("FCASHACCCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append("FSELLMONEY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FSELLAMOUNT    DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FSELLPRICE     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FINCOMENOTBAL  DECIMAL(18,4),");
            bufSql.append("FINCOMEBAL     DECIMAL(18,4),");
            bufSql.append("FCONFIMDATE    DATE           NOT NULL,");
            bufSql.append("FMarkDate      DATE,");
            bufSql.append("FTRADEDATE     DATE           NOT NULL,");
            bufSql.append("FSETTLEDATE    DATE           NOT NULL,");
            bufSql.append("FPORTCURYRATE  DECIMAL(20,15) NOT NULL,");
            bufSql.append("FBASECURYRATE  DECIMAL(20,15) NOT NULL,");
            bufSql.append("FFEECODE1      VARCHAR(20),");
            bufSql.append("FTRADEFEE1     DECIMAL(18,4),");
            bufSql.append("FFEECODE2      VARCHAR(20),");
            bufSql.append("FTRADEFEE2     DECIMAL(18,4),");
            bufSql.append("FFEECODE3      VARCHAR(20),");
            bufSql.append("FTRADEFEE3     DECIMAL(18,4),");
            bufSql.append("FFEECODE4      VARCHAR(20),");
            bufSql.append("FTRADEFEE4     DECIMAL(18,4),");
            bufSql.append("FFEECODE5      VARCHAR(20),");
            bufSql.append("FTRADEFEE5     DECIMAL(18,4),");
            bufSql.append("FFEECODE6      VARCHAR(20),");
            bufSql.append("FTRADEFEE6     DECIMAL(18,4),");
            bufSql.append("FFEECODE7      VARCHAR(20),");
            bufSql.append("FTRADEFEE7     DECIMAL(18,4),");
            bufSql.append("FFEECODE8      VARCHAR(20),");
            bufSql.append("FTRADEFEE8     DECIMAL(18,4),");
            bufSql.append("FSETTLESTATE   DECIMAL(1)     NOT NULL DEFAULT 0,");
            bufSql.append("FDESC          VARCHAR(100),");
            bufSql.append("FCHECKSTATE    DECIMAL(1)     NOT NULL,");
            bufSql.append("FCREATOR       VARCHAR(20)    NOT NULL,");
            bufSql.append("FCREATETIME    VARCHAR(20)    NOT NULL,");
            bufSql.append("FCHECKUSER     VARCHAR(20),");
            bufSql.append("FCHECKTIME     VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_TA_TRADE") + "(");
            bufSql.append("FNum,");
            bufSql.append("FTRADEDATE,");
            bufSql.append("FMarkDate,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FPORTCLSCODE,");
            bufSql.append("FSELLNETCODE,");
            bufSql.append("FSELLTYPE,");
            bufSql.append("FCURYCODE,");
            bufSql.append("FANALYSISCODE1,");
            bufSql.append("FANALYSISCODE2,");
            bufSql.append("FANALYSISCODE3,");
            bufSql.append("FCASHACCCODE,");
            bufSql.append("FSELLMONEY,");
            bufSql.append("FSELLAMOUNT,");
            bufSql.append("FSELLPRICE,");
            bufSql.append("FINCOMENOTBAL,");
            bufSql.append("FINCOMEBAL,");
            bufSql.append("FCONFIMDATE,");
            bufSql.append("FSETTLEDATE,");
            bufSql.append("FPORTCURYRATE,");
            bufSql.append("FBASECURYRATE,");
            bufSql.append("FFEECODE1,");
            bufSql.append("FTRADEFEE1,");
            bufSql.append("FFEECODE2,");
            bufSql.append("FTRADEFEE2,");
            bufSql.append("FFEECODE3,");
            bufSql.append("FTRADEFEE3,");
            bufSql.append("FFEECODE4,");
            bufSql.append("FTRADEFEE4,");
            bufSql.append("FFEECODE5,");
            bufSql.append("FTRADEFEE5,");
            bufSql.append("FFEECODE6,");
            bufSql.append("FTRADEFEE6,");
            bufSql.append("FFEECODE7,");
            bufSql.append("FTRADEFEE7,");
            bufSql.append("FFEECODE8,");
            bufSql.append("FTRADEFEE8,");
            bufSql.append("FSETTLESTATE,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(")");
            bufSql.append("SELECT ");
            bufSql.append("FNum,");
            bufSql.append("FTRADEDATE,");
            bufSql.append(" FTRADEDATE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FPORTCLSCODE,");
            bufSql.append("FSELLNETCODE,");
            bufSql.append("FSELLTYPE,");
            bufSql.append("FCURYCODE,");
            bufSql.append("FANALYSISCODE1,");
            bufSql.append("FANALYSISCODE2,");
            bufSql.append("FANALYSISCODE3,");
            bufSql.append("FCASHACCCODE,");
            bufSql.append("FSELLMONEY,");
            bufSql.append("FSELLAMOUNT,");
            bufSql.append("FSELLPRICE,");
            bufSql.append("FINCOMENOTBAL,");
            bufSql.append("FINCOMEBAL,");
            bufSql.append("FCONFIMDATE,");
            bufSql.append("FSETTLEDATE,");
            bufSql.append("FPORTCURYRATE,");
            bufSql.append("FBASECURYRATE,");
            bufSql.append("FFEECODE1,");
            bufSql.append("FTRADEFEE1,");
            bufSql.append("FFEECODE2,");
            bufSql.append("FTRADEFEE2,");
            bufSql.append("FFEECODE3,");
            bufSql.append("FTRADEFEE3,");
            bufSql.append("FFEECODE4,");
            bufSql.append("FTRADEFEE4,");
            bufSql.append("FFEECODE5,");
            bufSql.append("FTRADEFEE5,");
            bufSql.append("FFEECODE6,");
            bufSql.append("FTRADEFEE6,");
            bufSql.append("FFEECODE7,");
            bufSql.append("FTRADEFEE7,");
            bufSql.append("FFEECODE8,");
            bufSql.append("FTRADEFEE8,");
            bufSql.append("FSETTLESTATE,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" FROM TB_03052008101129");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_TA_TRADE ADD CONSTRAINT PK_Tb_" + sPre + "_TA_TRADE " +
                           "PRIMARY KEY (FNUM)");
            //------------------------- Tb_XXX_Para_AffiliatedCorp   添加字段 FOrgCodeType和FOrgCode ----------------------begin----//2008-5-27 单亮
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_Para_AffiliatedCorp"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Para_AffiliatedCorp DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_03142008103856")) {
                this.dropTableByTableName("TB_03142008103856");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_Para_AffiliatedCorp TO TB_03142008103856");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Para_AffiliatedCorp");
            bufSql.append("(");
            bufSql.append("    FAFFCORPCODE VARCHAR(20)   NOT NULL,");
            bufSql.append("    FSTARTDATE   DATE          NOT NULL,");
            bufSql.append("    FPARENTCODE  VARCHAR(20),");
            bufSql.append("    FAFFCORPNAME VARCHAR(200)  NOT NULL,");
            bufSql.append("    FARTPERSON   VARCHAR(50),");
            bufSql.append("    FCAPITALCURY VARCHAR(20),");
            bufSql.append("    FREGCAPITAL  DECIMAL(18,4),");
            bufSql.append("    FREGADDR     VARCHAR(100),");
            bufSql.append("    FOrgCodeType VARCHAR(20),");
            bufSql.append("    FOrgCode     VARCHAR(50),");
            bufSql.append("    FDESC        VARCHAR(200),");
            bufSql.append("    FCHECKSTATE  DECIMAL(1)    NOT NULL,");
            bufSql.append("    FCREATOR     VARCHAR(20)   NOT NULL,");
            bufSql.append("    FCREATETIME  VARCHAR(20)   NOT NULL,");
            bufSql.append("    FCHECKUSER   VARCHAR(20),");
            bufSql.append("    FCHECKTIME   VARCHAR(20)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Para_AffiliatedCorp(");
            bufSql.append("FAFFCORPCODE,");
            bufSql.append("FSTARTDATE,");
            bufSql.append("FPARENTCODE,");
            bufSql.append("FAFFCORPNAME,");
            bufSql.append("FARTPERSON,");
            bufSql.append("FCAPITALCURY,");
            bufSql.append("FREGCAPITAL,");
            bufSql.append("FREGADDR,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append("FAFFCORPCODE,");
            bufSql.append("FSTARTDATE,");
            bufSql.append("FPARENTCODE,");
            bufSql.append("FAFFCORPNAME,");
            bufSql.append("FARTPERSON,");
            bufSql.append("FCAPITALCURY,");
            bufSql.append("FREGCAPITAL,");
            bufSql.append("FREGADDR,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" FROM TB_03142008103856");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Para_AffiliatedCorp ADD CONSTRAINT PK_Tb_" + sPre +
                           "_tedCorp PRIMARY KEY (FAffCorpCode,FStartDate)");

            //------------------------------------------------------------------------------------------------------------end---//2008-5-27 单亮
            //---------------为表Tb_Rep_cell中增加 FIsMergeCol---------------
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_Rep_Cell"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_Rep_Cell DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_03142008103856")) {
                this.dropTableByTableName("TB_03142008103856");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_Rep_Cell TO TB_03142008103856");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Rep_Cell");
            bufSql.append("(");
            bufSql.append("FRELACODE   VARCHAR(20)  NOT NULL,");
            bufSql.append("FRELATYPE   VARCHAR(20)  NOT NULL,");
            bufSql.append("FROW        DECIMAL(5)   NOT NULL,");
            bufSql.append("FCOL        DECIMAL(5)   NOT NULL,");
            bufSql.append("FCONTENT    VARCHAR(600),");
            bufSql.append("FLLINE      DECIMAL(3)   NOT NULL DEFAULT 0,");
            bufSql.append("FTLINE      DECIMAL(3)   NOT NULL DEFAULT 0,");
            bufSql.append("FRLINE      DECIMAL(3)   NOT NULL DEFAULT 0,");
            bufSql.append("FBLINE      DECIMAL(3)   NOT NULL DEFAULT 0,");
            bufSql.append("FLCOLOR     DECIMAL(10)  NOT NULL DEFAULT 0,");
            bufSql.append("FTCOLOR     DECIMAL(10)  NOT NULL DEFAULT 0,");
            bufSql.append("FRCOLOR     DECIMAL(10)  NOT NULL DEFAULT 0,");
            bufSql.append("FBCOLOR     DECIMAL(10)  NOT NULL DEFAULT 0,");
            bufSql.append("FBACKCOLOR  DECIMAL(10)  NOT NULL DEFAULT 0,");
            bufSql.append("FFORECOLOR  DECIMAL(10)  NOT NULL DEFAULT 0,");
            bufSql.append("FFONTNAME   VARCHAR(50)  NOT NULL,");
            bufSql.append("FFONTSIZE   DECIMAL(3)   NOT NULL,");
            bufSql.append("FFONTSTYLE  DECIMAL(3)   NOT NULL,");
            bufSql.append("FDATATYPE   DECIMAL(3)   NOT NULL,");
            bufSql.append("FIsMergeCol DECIMAL(1)   NOT NULL DEFAULT 0,");
            bufSql.append("FFORMAT     VARCHAR(100)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Rep_Cell(");
            bufSql.append("FRELACODE,");
            bufSql.append("FRELATYPE,");
            bufSql.append("FROW,");
            bufSql.append("FCOL,");
            bufSql.append("FCONTENT,");
            bufSql.append("FLLINE,");
            bufSql.append("FTLINE,");
            bufSql.append("FRLINE,");
            bufSql.append("FBLINE,");
            bufSql.append("FLCOLOR,");
            bufSql.append("FTCOLOR,");
            bufSql.append("FRCOLOR,");
            bufSql.append("FBCOLOR,");
            bufSql.append("FBACKCOLOR,");
            bufSql.append("FFORECOLOR,");
            bufSql.append("FFONTNAME,");
            bufSql.append("FFONTSIZE,");
            bufSql.append("FFONTSTYLE,");
            bufSql.append("FDATATYPE,");
            bufSql.append("FIsMergeCol,");
            bufSql.append("FFORMAT");
            bufSql.append(")SELECT ");
            bufSql.append("FRELACODE,");
            bufSql.append("FRELATYPE,");
            bufSql.append("FROW,");
            bufSql.append("FCOL,");
            bufSql.append("FCONTENT,");
            bufSql.append("FLLINE,");
            bufSql.append("FTLINE,");
            bufSql.append("FRLINE,");
            bufSql.append("FBLINE,");
            bufSql.append("FLCOLOR,");
            bufSql.append("FTCOLOR,");
            bufSql.append("FRCOLOR,");
            bufSql.append("FBCOLOR,");
            bufSql.append("FBACKCOLOR,");
            bufSql.append("FFORECOLOR,");
            bufSql.append("FFONTNAME,");
            bufSql.append("FFONTSIZE,");
            bufSql.append("FFONTSTYLE,");
            bufSql.append("FDATATYPE,");
            bufSql.append("0,");
            bufSql.append("FFORMAT");
            bufSql.append(" FROM TB_03142008103856");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Rep_Cell ADD CONSTRAINT PK_Tb_" + sPre + "_Rep_Cell PRIMARY KEY (FRELACODE,FRELATYPE,FROW,FCOL)");
            //---------------为表Tb_TA_Trade中增加 FSettleMoney---------------
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_TA_Trade"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_TA_Trade DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_03252008013142")) {
                this.dropTableByTableName("TB_03252008013142");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_TA_Trade TO TB_03252008013142");
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE Tb_" + sPre + "_TA_Trade ");
            bufSql.append(" (");
            bufSql.append(" FNUM           VARCHAR(20)    NOT NULL,");
            bufSql.append(" FTRADEDATE     DATE           NOT NULL,");
            bufSql.append(" FMARKDATE      DATE,");
            bufSql.append(" FPORTCODE      VARCHAR(20),");
            bufSql.append(" FPORTCLSCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append(" FSELLNETCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append(" FSELLTYPE      VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCURYCODE      VARCHAR(20)    NOT NULL,");
            bufSql.append(" FANALYSISCODE1 VARCHAR(20)    NOT NULL,");
            bufSql.append(" FANALYSISCODE2 VARCHAR(20)    NOT NULL,");
            bufSql.append(" FANALYSISCODE3 VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCASHACCCODE   VARCHAR(20)    NOT NULL,");
            bufSql.append(" FSELLMONEY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append(" FSELLAMOUNT    DECIMAL(18,4)  NOT NULL,");
            bufSql.append(" FSELLPRICE     DECIMAL(18,4)  NOT NULL,");
            bufSql.append(" FINCOMENOTBAL  DECIMAL(18,4),");
            bufSql.append(" FINCOMEBAL     DECIMAL(18,4),");
            bufSql.append(" FCONFIMDATE    DATE           NOT NULL,");
            bufSql.append(" FSETTLEDATE    DATE           NOT NULL,");
            bufSql.append(" FSettleMoney   DECIMAL(18,4),");
            bufSql.append(" FPORTCURYRATE  DECIMAL(20,15) NOT NULL,");
            bufSql.append(" FBASECURYRATE  DECIMAL(20,15) NOT NULL,");
            bufSql.append(" FFEECODE1      VARCHAR(20),");
            bufSql.append(" FTRADEFEE1     DECIMAL(18,4),");
            bufSql.append(" FFEECODE2      VARCHAR(20),");
            bufSql.append(" FTRADEFEE2     DECIMAL(18,4),");
            bufSql.append(" FFEECODE3      VARCHAR(20),");
            bufSql.append(" FTRADEFEE3     DECIMAL(18,4),");
            bufSql.append(" FFEECODE4      VARCHAR(20),");
            bufSql.append(" FTRADEFEE4     DECIMAL(18,4),");
            bufSql.append(" FFEECODE5      VARCHAR(20),");
            bufSql.append(" FTRADEFEE5     DECIMAL(18,4),");
            bufSql.append(" FFEECODE6      VARCHAR(20),");
            bufSql.append(" FTRADEFEE6     DECIMAL(18,4),");
            bufSql.append(" FFEECODE7      VARCHAR(20),");
            bufSql.append(" FTRADEFEE7     DECIMAL(18,4),");
            bufSql.append(" FFEECODE8      VARCHAR(20),");
            bufSql.append(" FTRADEFEE8     DECIMAL(18,4),");
            bufSql.append(" FSETTLESTATE   DECIMAL(1)     NOT NULL DEFAULT 0,");
            bufSql.append(" FDESC          VARCHAR(100),");
            bufSql.append(" FCHECKSTATE    DECIMAL(1)     NOT NULL,");
            bufSql.append(" FCREATOR       VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCREATETIME    VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCHECKUSER     VARCHAR(20),");
            bufSql.append(" FCHECKTIME     VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.append(" INSERT INTO Tb_" + sPre + "_TA_Trade( ");
            bufSql.append(" FNUM,");
            bufSql.append(" FTRADEDATE,");
            bufSql.append(" FMARKDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FPORTCLSCODE,");
            bufSql.append(" FSELLNETCODE,");
            bufSql.append(" FSELLTYPE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FCASHACCCODE,");
            bufSql.append(" FSELLMONEY,");
            bufSql.append(" FSELLAMOUNT,");
            bufSql.append(" FSELLPRICE,");
            bufSql.append(" FINCOMENOTBAL,");
            bufSql.append(" FINCOMEBAL,");
            bufSql.append(" FCONFIMDATE,");
            bufSql.append(" FSETTLEDATE,");
            bufSql.append(" FSettleMoney,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FFEECODE1,");
            bufSql.append(" FTRADEFEE1,");
            bufSql.append(" FFEECODE2,");
            bufSql.append(" FTRADEFEE2,");
            bufSql.append(" FFEECODE3,");
            bufSql.append(" FTRADEFEE3,");
            bufSql.append(" FFEECODE4,");
            bufSql.append(" FTRADEFEE4,");
            bufSql.append(" FFEECODE5,");
            bufSql.append(" FTRADEFEE5,");
            bufSql.append(" FFEECODE6,");
            bufSql.append(" FTRADEFEE6,");
            bufSql.append(" FFEECODE7,");
            bufSql.append(" FTRADEFEE7,");
            bufSql.append(" FFEECODE8,");
            bufSql.append(" FTRADEFEE8,");
            bufSql.append(" FSETTLESTATE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM,");
            bufSql.append(" FTRADEDATE,");
            bufSql.append(" FMARKDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FPORTCLSCODE,");
            bufSql.append(" FSELLNETCODE,");
            bufSql.append(" FSELLTYPE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FCASHACCCODE,");
            bufSql.append(" FSELLMONEY,");
            bufSql.append(" FSELLAMOUNT,");
            bufSql.append(" FSELLPRICE,");
            bufSql.append(" FINCOMENOTBAL,");
            bufSql.append(" FINCOMEBAL,");
            bufSql.append(" FCONFIMDATE,");
            bufSql.append(" FSETTLEDATE,");
            bufSql.append(" 0,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FFEECODE1,");
            bufSql.append(" FTRADEFEE1,");
            bufSql.append(" FFEECODE2,");
            bufSql.append(" FTRADEFEE2,");
            bufSql.append(" FFEECODE3,");
            bufSql.append(" FTRADEFEE3,");
            bufSql.append(" FFEECODE4,");
            bufSql.append(" FTRADEFEE4,");
            bufSql.append(" FFEECODE5,");
            bufSql.append(" FTRADEFEE5,");
            bufSql.append(" FFEECODE6,");
            bufSql.append(" FTRADEFEE6,");
            bufSql.append(" FFEECODE7,");
            bufSql.append(" FTRADEFEE7,");
            bufSql.append(" FFEECODE8,");
            bufSql.append(" FTRADEFEE8,");
            bufSql.append(" FSETTLESTATE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_03252008013142 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_TA_Trade ADD CONSTRAINT PK_TB_" + sPre + "_TA_TRADE PRIMARY KEY (FNUM)");
            //---------------为表Tb_Vch_VchTpl中增加 FPortCode---------------
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_Vch_VchTpl"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_Vch_VCHTpl DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_03252008013257")) {
                this.dropTableByTableName("TB_03252008013257");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_Vch_VchTpl TO TB_03252008013257");
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE Tb_" + sPre + "_Vch_VchTpl ");
            bufSql.append(" ( ");
            bufSql.append(" FVCHTPLCODE VARCHAR(20)  NOT NULL,");
            bufSql.append(" FSRCCURY    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FVCHTPLNAME VARCHAR(50)  NOT NULL,");
            bufSql.append(" FPortCode   VARCHAR(20),");
            bufSql.append(" FATTRCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDATEFIELD  VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCURYCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDESC       VARCHAR(100),");
            bufSql.append(" FMODE       VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDSCODE     VARCHAR(20)  NOT NULL,");
            bufSql.append(" FFIELDS     VARCHAR(150),");
            bufSql.append(" FVCHTWAY    VARCHAR(20),");
            bufSql.append(" FLINKCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKSTATE DECIMAL(1)   NOT NULL,");
            bufSql.append(" FCREATOR    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER  VARCHAR(20),");
            bufSql.append(" FCHECKTIME  VARCHAR(20) ");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            bufSql.append(" INSERT INTO Tb_" + sPre + "_Vch_VchTpl( ");
            bufSql.append(" FVCHTPLCODE,");
            bufSql.append(" FSRCCURY,");
            bufSql.append(" FVCHTPLNAME,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FATTRCODE,");
            bufSql.append(" FDATEFIELD,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FMODE,");
            bufSql.append(" FDSCODE,");
            bufSql.append(" FFIELDS,");
            bufSql.append(" FVCHTWAY,");
            bufSql.append(" FLINKCODE,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FVCHTPLCODE,");
            bufSql.append(" FSRCCURY,");
            bufSql.append(" FVCHTPLNAME,");
            bufSql.append(" ' ',");
            bufSql.append(" FATTRCODE,");
            bufSql.append(" FDATEFIELD,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FMODE,");
            bufSql.append(" FDSCODE,");
            bufSql.append(" FFIELDS,");
            bufSql.append(" FVCHTWAY,");
            bufSql.append(" FLINKCODE,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_03252008013257 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Vch_VchTpl ADD CONSTRAINT PK_Tb_" + sPre + "_Vch_VchT PRIMARY KEY (FVCHTPLCODE)");
            //---------------为表Tb_Vch_Dict 中增加 FPortCode---------------
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_Vch_Dict"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_Vch_Dict DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_03252008013258")) {
                this.dropTableByTableName("TB_03252008013258");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_Vch_Dict TO TB_03252008013258");
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE Tb_" + sPre + "_Vch_Dict ");
            bufSql.append(" (");
            bufSql.append(" FDICTCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FINDCODE    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDICTNAME   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FPortCode   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCNVCONENT  VARCHAR(50)  NOT NULL,");
            bufSql.append(" FDESC       VARCHAR(100),");
            bufSql.append(" FCHECKSTATE DECIMAL(1)   NOT NULL,");
            bufSql.append(" FCREATOR    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER  VARCHAR(20),");
            bufSql.append(" FCHECKTIME  VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            bufSql.append(" INSERT INTO Tb_" + sPre + "_Vch_Dict( ");
            bufSql.append(" FDICTCODE,");
            bufSql.append(" FINDCODE,");
            bufSql.append(" FDICTNAME,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FCNVCONENT,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append(" FDICTCODE,");
            bufSql.append(" FINDCODE,");
            bufSql.append(" FDICTNAME,");
            bufSql.append(" ' ',");
            bufSql.append(" FCNVCONENT,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_03252008013258 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Vch_Dict ADD CONSTRAINT PK_Tb_" + sPre + "_Vch_Dict PRIMARY KEY (FDICTCODE,FINDCODE,FPORTCODE)");
            //------------------------- TB_001_DATA_SECRECPAY   添加字段 FDESC
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "TB_DATA_SECRECPAY")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + //删除主键
                               pub.yssGetTableName("TB_DATA_SECRECPAY") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_TA032520080132")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_TA032520080132");
            }
            dbl.executeSql("RENAME TABLE " + //修改临时表的名字
                           pub.yssGetTableName("TB_DATA_SECRECPAY") +
                           " TO TB_TA032520080132");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + //创建表
                          pub.yssGetTableName("TB_DATA_SECRECPAY"));
            bufSql.append(" (");
            bufSql.append("    FNUM            VARCHAR(20)  NOT NULL,");
            bufSql.append("    FDESC           VARCHAR(100),");
            bufSql.append("    FTRANSDATE      DATE          NOT NULL,");
            bufSql.append("    FPORTCODE       VARCHAR(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE1  VARCHAR(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE2  VARCHAR(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE3  VARCHAR(20)  NOT NULL,");
            bufSql.append("    FSECURITYCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append("    FTSFTYPECODE    VARCHAR(20)  NOT NULL,");
            bufSql.append("    FSUBTSFTYPECODE VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCATTYPE        VARCHAR(20)  NOT NULL,");
            bufSql.append("    FATTRCLSCODE    VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCURYCODE       VARCHAR(20)  NOT NULL,");
            bufSql.append("    FINOUT          DECIMAL(1)     NOT NULL,");
            bufSql.append("    FMONEY          DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FMMONEY         DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FVMONEY         DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FBASECURYRATE   DECIMAL(20,15) NOT NULL,");
            bufSql.append("    FBASECURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FMBASECURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FVBASECURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FPORTCURYRATE   DECIMAL(20,15) NOT NULL,");
            bufSql.append("    FPORTCURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FMPORTCURYMONEY DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FVPORTCURYMONEY DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FDATASOURCE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FSTOCKIND       DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR(20),");
            bufSql.append("    FCHECKTIME      VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_DATA_SECRECPAY") + "(");
            bufSql.append("FNUM,");
            bufSql.append("FDESC,");
            bufSql.append("FTRANSDATE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FANALYSISCODE1,");
            bufSql.append("FANALYSISCODE2,");
            bufSql.append("FANALYSISCODE3,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FTSFTYPECODE,");
            bufSql.append("FSUBTSFTYPECODE,");
            bufSql.append("FCATTYPE,");
            bufSql.append("FATTRCLSCODE,");
            bufSql.append("FCURYCODE,");
            bufSql.append("FINOUT,");
            bufSql.append("FMONEY,");
            bufSql.append("FMMONEY,");
            bufSql.append("FVMONEY,");
            bufSql.append("FBASECURYRATE,");
            bufSql.append("FBASECURYMONEY,");
            bufSql.append("FMBASECURYMONEY,");
            bufSql.append("FVBASECURYMONEY,");
            bufSql.append("FPORTCURYRATE,");
            bufSql.append("FPORTCURYMONEY,");
            bufSql.append("FMPORTCURYMONEY,");
            bufSql.append("FVPORTCURYMONEY,");
            bufSql.append("FDATASOURCE,");
            bufSql.append("FSTOCKIND,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append("FNUM,");
            bufSql.append("' ',");
            bufSql.append("FTRANSDATE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FANALYSISCODE1,");
            bufSql.append("FANALYSISCODE2,");
            bufSql.append("FANALYSISCODE3,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FTSFTYPECODE,");
            bufSql.append("FSUBTSFTYPECODE,");
            bufSql.append("FCATTYPE,");
            bufSql.append("FATTRCLSCODE,");
            bufSql.append("FCURYCODE,");
            bufSql.append("FINOUT,");
            bufSql.append("FMONEY,");
            bufSql.append("FMMONEY,");
            bufSql.append("FVMONEY,");
            bufSql.append("FBASECURYRATE,");
            bufSql.append("FBASECURYMONEY,");
            bufSql.append("FMBASECURYMONEY,");
            bufSql.append("FVBASECURYMONEY,");
            bufSql.append("FPORTCURYRATE,");
            bufSql.append("FPORTCURYMONEY,");
            bufSql.append("FMPORTCURYMONEY,");
            bufSql.append("FVPORTCURYMONEY,");
            bufSql.append("FDATASOURCE,");
            bufSql.append("FSTOCKIND,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" FROM TB_TA032520080132");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_SECRECPAY ") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "CRECPAY " +
                           " PRIMARY KEY (FNum)");
            //------------------------- Tb_001_Data_CashPayRec   添加字段 FDESC
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "TB_DATA_CASHPAYREC")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + //删除主键
                               pub.yssGetTableName("TB_DATA_CASHPAYREC") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_030520080554290")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_030520080554290");
            }
            dbl.executeSql("RENAME TABLE " + //修改临时表的名字
                           pub.yssGetTableName("TB_DATA_CASHPAYREC") +
                           " TO TB_030520080554290");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + //创建表
                          pub.yssGetTableName("TB_DATA_CASHPAYREC"));
            bufSql.append(" (");
            bufSql.append("    FNUM            VARCHAR(20)    NOT NULL,");
            bufSql.append("    FDESC           VARCHAR(100),");
            bufSql.append("    FTRANSDATE      DATE           NOT NULL,");
            bufSql.append("    FPORTCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("    FANALYSISCODE1  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FANALYSISCODE2  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FANALYSISCODE3  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCASHACCCODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("    FTSFTYPECODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("    FSUBTSFTYPECODE VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCURYCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("    FINOUT          DECIMAL(1)     NOT NULL DEFAULT 1 ,");
            bufSql.append("    FMONEY          DECIMAL(28,4)  NOT NULL,");
            bufSql.append("    FBASECURYRATE   DECIMAL(25,15) NOT NULL,");
            bufSql.append("    FBASECURYMONEY  DECIMAL(28,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FPORTCURYRATE   DECIMAL(25,15) NOT NULL,");
            bufSql.append("    FPORTCURYMONEY  DECIMAL(28,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FDATASOURCE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FSTOCKIND       DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR(20),");
            bufSql.append("    FCHECKTIME      VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_DATA_CASHPAYREC") + "(");
            bufSql.append(" FNUM,");
            bufSql.append(" FDESC,");
            bufSql.append(" FTRANSDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FCASHACCCODE,");
            bufSql.append(" FTSFTYPECODE,");
            bufSql.append(" FSUBTSFTYPECODE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FINOUT,");
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
            bufSql.append(" FCHECKTIME");
            bufSql.append(")");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM,");
            bufSql.append(" ' ',");
            bufSql.append(" FTRANSDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FANALYSISCODE1,");
            bufSql.append(" FANALYSISCODE2,");
            bufSql.append(" FANALYSISCODE3,");
            bufSql.append(" FCASHACCCODE,");
            bufSql.append(" FTSFTYPECODE,");
            bufSql.append(" FSUBTSFTYPECODE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FINOUT,");
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
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_030520080554290");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_CASHPAYREC ") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "HPAYREC " +
                           " PRIMARY KEY (FNum)");
            //------------------------- Tb_001_Data_InvestPayRec   添加字段 FDESC
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "Tb_Data_InvestPayRec")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + //删除主键
                               pub.yssGetTableName("Tb_Data_InvestPayRec") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_03052008055429")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_03052008055429");
            }
            dbl.executeSql("RENAME TABLE " + //修改临时表的名字
                           pub.yssGetTableName("Tb_Data_InvestPayRec") +
                           " TO TB_03052008055429");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + //创建表
                          pub.yssGetTableName("Tb_Data_InvestPayRec"));
            bufSql.append(" (");
            bufSql.append("    FNUM            VARCHAR(20)  NOT NULL,");
            bufSql.append("    FDESC           VARCHAR(100),");
            bufSql.append("    FTRANSDATE      DATE          NOT NULL,");
            bufSql.append("    FPORTCODE       VARCHAR(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE1  VARCHAR(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE2  VARCHAR(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE3  VARCHAR(20)  NOT NULL,");
            bufSql.append("    FIVPAYCATCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append("    FTSFTYPECODE    VARCHAR(20)  NOT NULL,");
            bufSql.append("    FSUBTSFTYPECODE VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCURYCODE       VARCHAR(20)  NOT NULL,");
            bufSql.append("    FMONEY          DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FBASECURYRATE   DECIMAL(20,15) NOT NULL,");
            bufSql.append("    FBASECURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FPORTCURYRATE   DECIMAL(20,15) NOT NULL,");
            bufSql.append("    FPORTCURYMONEY  DECIMAL(18,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FDATASOURCE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FSTOCKIND       DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR(20),");
            bufSql.append("    FCHECKTIME      VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("Tb_Data_InvestPayRec") + "(");
            bufSql.append("   FNUM,");
            bufSql.append("   FDESC,");
            bufSql.append("   FTRANSDATE,");
            bufSql.append("   FPORTCODE,");
            bufSql.append("   FANALYSISCODE1,");
            bufSql.append("   FANALYSISCODE2,");
            bufSql.append("   FANALYSISCODE3,");
            bufSql.append("   FIVPAYCATCODE,");
            bufSql.append("   FTSFTYPECODE,");
            bufSql.append("   FSUBTSFTYPECODE,");
            bufSql.append("   FCURYCODE,");
            bufSql.append("   FMONEY,");
            bufSql.append("   FBASECURYRATE,");
            bufSql.append("   FBASECURYMONEY,");
            bufSql.append("   FPORTCURYRATE,");
            bufSql.append("   FPORTCURYMONEY,");
            bufSql.append("   FDATASOURCE,");
            bufSql.append("   FSTOCKIND,");
            bufSql.append("   FCHECKSTATE,");
            bufSql.append("   FCREATOR,");
            bufSql.append("   FCREATETIME,");
            bufSql.append("   FCHECKUSER,");
            bufSql.append("   FCHECKTIME");
            bufSql.append("  )");
            bufSql.append(" SELECT ");
            bufSql.append("   FNUM,");
            bufSql.append("   ' ',");
            bufSql.append("   FTRANSDATE,");
            bufSql.append("   FPORTCODE,");
            bufSql.append("   FANALYSISCODE1,");
            bufSql.append("   FANALYSISCODE2,");
            bufSql.append("   FANALYSISCODE3,");
            bufSql.append("   FIVPAYCATCODE,");
            bufSql.append("   FTSFTYPECODE,");
            bufSql.append("   FSUBTSFTYPECODE,");
            bufSql.append("   FCURYCODE,");
            bufSql.append("   FMONEY,");
            bufSql.append("   FBASECURYRATE,");
            bufSql.append("   FBASECURYMONEY,");
            bufSql.append("   FPORTCURYRATE,");
            bufSql.append("   FPORTCURYMONEY,");
            bufSql.append("   FDATASOURCE,");
            bufSql.append("   FSTOCKIND,");
            bufSql.append("   FCHECKSTATE,");
            bufSql.append("   FCREATOR,");
            bufSql.append("   FCREATETIME,");
            bufSql.append("   FCHECKUSER,");
            bufSql.append("   FCHECKTIME");
            bufSql.append(" FROM TB_03052008055429");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("Tb_Data_InvestPayRec ") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "tPayRec " +
                           " PRIMARY KEY (FNum)");

            //------------------------- Tb_001_Data_RightsIssue   添加字段 FDESC
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "Tb_Data_RightsIssue")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + //删除主键
                               pub.yssGetTableName("Tb_Data_RightsIssue") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_03052008055290")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_03052008055290");
            }
            dbl.executeSql("RENAME TABLE " + //修改临时表的名字
                           pub.yssGetTableName("Tb_Data_RightsIssue") +
                           " TO TB_03052008055290");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + //创建表
                          pub.yssGetTableName("Tb_Data_RightsIssue"));
            bufSql.append(" (");
            bufSql.append("    FSECURITYCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append("    FRECORDDATE     DATE          NOT NULL,");
            bufSql.append("    FRICURYCODE     VARCHAR(20),");
            bufSql.append("    FTSECURITYCODE  VARCHAR(20),");
            bufSql.append("    FEXRIGHTDATE    DATE          NOT NULL,");
            bufSql.append("    FEXPIRATIONDATE DATE          NOT NULL,");
            bufSql.append("    FAFFICHEDATE    DATE          NOT NULL,");
            bufSql.append("    FPAYDATE        DATE          NOT NULL,");
            bufSql.append("    FBEGINSCRIDATE  DATE          NOT NULL,");
            bufSql.append("    FENDSCRIDATE    DATE          NOT NULL,");
            bufSql.append("    FBEGINTRADEDATE DATE          NOT NULL,");
            bufSql.append("    FENDTRADEDATE   DATE          NOT NULL,");
            bufSql.append("    FRATIO          DECIMAL(18,8)  NOT NULL,");
            bufSql.append("    FRIPRICE        DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FROUNDCODE      VARCHAR(20)  NOT NULL,");
            bufSql.append("    FDESC           VARCHAR(100),");
            bufSql.append("    FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR(20),");
            bufSql.append("    FCHECKTIME      VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("Tb_Data_RightsIssue") + "(");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append("  FRECORDDATE,");
            bufSql.append("  FRICURYCODE,");
            bufSql.append("  FTSECURITYCODE,");
            bufSql.append("  FEXRIGHTDATE,");
            bufSql.append("  FEXPIRATIONDATE,");
            bufSql.append("  FAFFICHEDATE,");
            bufSql.append("  FPAYDATE,");
            bufSql.append("  FBEGINSCRIDATE,");
            bufSql.append("  FENDSCRIDATE,");
            bufSql.append("  FBEGINTRADEDATE,");
            bufSql.append("  FENDTRADEDATE,");
            bufSql.append("  FRATIO,");
            bufSql.append("  FRIPRICE,");
            bufSql.append("  FROUNDCODE,");
            bufSql.append("  FDESC,");
            bufSql.append("  FCHECKSTATE,");
            bufSql.append("  FCREATOR,");
            bufSql.append("  FCREATETIME,");
            bufSql.append("  FCHECKUSER,");
            bufSql.append("  FCHECKTIME");
            bufSql.append(" )");
            bufSql.append("   SELECT ");
            bufSql.append("  FSECURITYCODE,");
            bufSql.append("  FRECORDDATE,");
            bufSql.append("  ' ',");
            bufSql.append("  FTSECURITYCODE,");
            bufSql.append("  FEXRIGHTDATE,");
            bufSql.append("  FEXPIRATIONDATE,");
            bufSql.append("  FAFFICHEDATE,");
            bufSql.append("  FPAYDATE,");
            bufSql.append("  FBEGINSCRIDATE,");
            bufSql.append("  FENDSCRIDATE,");
            bufSql.append("  FBEGINTRADEDATE,");
            bufSql.append("  FENDTRADEDATE,");
            bufSql.append("  FRATIO,");
            bufSql.append("  FRIPRICE,");
            bufSql.append("  FROUNDCODE,");
            bufSql.append("  FDESC,");
            bufSql.append("  FCHECKSTATE,");
            bufSql.append("  FCREATOR,");
            bufSql.append("  FCREATETIME,");
            bufSql.append("  FCHECKUSER,");
            bufSql.append("  FCHECKTIME");

            bufSql.append(" FROM TB_03052008055290");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("Tb_Data_RightsIssue ") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "tsIssue " +
                           " PRIMARY KEY (FSecurityCode,FRecordDate)");
            //给Tb_XXX_rep_Cell 添加字段 FOTHERPARAMS by leeyu 080604
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_REP_CELL ADD FOtherParams VARCHAR(500)");

        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003 增加表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //调制表名
    public void adjustTableName() throws YssException {
        try {
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003 执行调整表名的 SQL 语句出错！", ex);
        }
    }

    public void adjustTableData(String sPre) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //------------------将报表的控件组设置表中的数据移到系统界面配置表中--------------------
            sqlStr = "insert into Tb_PFSys_FaceCfgInfo(" +
                "FCtlGrpCode,FCtlGrpName,FCtlCode,FParamIndex," +
                "FCtlType,FParam,FCtlInd,FFunModules,FCheckstate," +
                "FCreator,FCreateTime,FcheckUser,FCheckTime) select " +
                "a.FCtlGrpCode,b.FCtlGrpName,a.FCtlCode,FParamIndex," +
                "FCtlType,FParam,'','RepParam',a.FCheckstate," +
                "a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime" +
                " from tb_" + sPre + "_rep_paramctl a left join tb_" + sPre + "_rep_paramctlgrp b " +
                " on a.FCtlGrpCode=b.FCtlGrpCode " +
                " where a.FCtlGrpCode not in (select FCtlGrpCode from Tb_PFSys_Facecfginfo)";
            dbl.executeSql(sqlStr);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(" update tb_pfsys_facecfginfo t set FParam= replace(FParam,'YssModulePojo.YssOperParamSet','YssModulePojo.YssSetting.YssOperParamSet')" +
                           " where exists (select * from tb_pfsys_facecfginfo b where t.Fparam=b.Fparam ) "); //更新业务参数控件
            dbl.executeSql(" update tb_pfsys_facecfginfo t set FParam= replace(FParam,'YssModulePojo.YssSysParamSet','YssModulePojo.YssSetting.YssSysParamSet')" +
                           " where exists (select * from tb_pfsys_facecfginfo b where t.Fparam=b.Fparam ) "); //更新系统参数控件
            dbl.executeSql(" update tb_pfsys_facecfginfo t set FParam= replace(FParam,'YssModulePojo.YssSysFunAdmin','YssModulePojo.YssSetting.YssSysParamSet')" +
                           " where exists (select * from tb_pfsys_facecfginfo b where t.Fparam=b.Fparam ) "); //更新基础参数控件
            dbl.executeSql(" update tb_pfsys_facecfginfo t set FParam= replace(FParam,'YssModulePojo.YssBaseParamSet','YssModulePojo.YssSetting.YssBaseParamSet')" +
                           " where exists (select * from tb_pfsys_facecfginfo b where t.Fparam=b.Fparam ) "); //更新基础设置控件
            dbl.executeSql(" update tb_pfsys_facecfginfo t set FParam= replace(FParam,'YssModulePojo.YssOperData','YssModulePojo.YssData.YssGeneral')" +
                           " where exists (select * from tb_pfsys_facecfginfo b where t.Fparam=b.Fparam ) "); //更新业务数据控件
            dbl.executeSql(" update tb_pfsys_facecfginfo t set FParam= replace(FParam,'YssModulePojo.YssCashManager','YssModulePojo.YssSetting.YssCashManager')" +
                           " where exists (select * from tb_pfsys_facecfginfo b where t.Fparam=b.Fparam ) "); //更新现金管理控件
            dbl.executeSql(" update tb_pfsys_facecfginfo t set FParam= replace(FParam,'YssModulePojo.YssStorageManage','YssModulePojo.YssData.YssStorage')" +
                           " where exists (select * from tb_pfsys_facecfginfo b where t.Fparam=b.Fparam ) "); //更新库存数据控件
            dbl.executeSql(" update tb_pfsys_facecfginfo t set FParam= replace(FParam,'YssModulePojo.YssSettlement','YssModulePojo.YssData.YssSettlement')" +
                           " where exists (select * from tb_pfsys_facecfginfo b where t.Fparam=b.Fparam ) "); //更新结算控件
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            throw new YssException("版本1.0.1.0003 执行数据调整 SQL 语句出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        //更改 Tb_XXX_Para_FixInterest表的票面面额的精度
        Connection conn = null;
        boolean bTrans = false;
        StringBuffer bufSql = new StringBuffer(5000);
        String strPKName = "";
        try {
            conn = dbl.loadConnection();
            //修改预处理的表字段
            dbl.executeSql(" alter table tb_" + sPre + "_dao_PRETREAT alter FDPDSNAME set  data type varchar(50)");

            strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre + "_PARA_FIXINTEREST");
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_PARA_FIXINTEREST DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_05162008052251")) {
                this.dropTableByTableName("TB_05162008052251");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_PARA_FIXINTEREST TO TB_05162008052251");
            bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_FIXINTEREST ");
            bufSql.append(" (");
            bufSql.append(" FSECURITYCODE     VARCHAR(20)    NOT NULL,");
            bufSql.append(" FSTARTDATE        DATE           NOT NULL,");
            bufSql.append(" FISSUEDATE        DATE           NOT NULL,");
            bufSql.append(" FISSUEPRICE       DECIMAL(18,12)  NOT NULL,");
            bufSql.append(" FINSSTARTDATE     DATE           NOT NULL,");
            bufSql.append(" FINSENDDATE       DATE           NOT NULL,");
            bufSql.append(" FINSCASHDATE      DATE           NOT NULL,");
            bufSql.append(" FFACEVALUE        DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FFACERATE         DECIMAL(18,12),");
            bufSql.append(" FINSFREQUENCY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append(" FQUOTEWAY         DECIMAL(1)     NOT NULL,");
            bufSql.append(" FCREDITLEVEL      VARCHAR(20),");
            bufSql.append(" FCALCINSMETICDAY  VARCHAR(20),");
            bufSql.append(" FCALCINSMETICBUY  VARCHAR(20),");
            bufSql.append(" FCALCINSMETICSELL VARCHAR(20),");
            bufSql.append(" FCALCPRICEMETIC   VARCHAR(20),");
            bufSql.append(" FCALCINSCFGDAY    VARCHAR(500),");
            bufSql.append(" FCALCINSCFGBUY    VARCHAR(500),");
            bufSql.append(" FCALCINSCFGSELL   VARCHAR(500),");
            bufSql.append(" FCALCINSWAY       DECIMAL(1)     NOT NULL,");
            bufSql.append(" FINTERESTORIGIN   DECIMAL(1)     NOT NULL,");
            bufSql.append(" FPEREXPCODE       VARCHAR(20),");
            bufSql.append(" FPERIODCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append(" FROUNDCODE        VARCHAR(20)    NOT NULL,");
            bufSql.append(" FDESC             VARCHAR(100),");
            bufSql.append(" FCHECKSTATE       DECIMAL(1)     NOT NULL,");
            bufSql.append(" FCREATOR          VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCREATETIME       VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCHECKUSER        VARCHAR(20),");
            bufSql.append(" FCHECKTIME        VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            bufSql.append(" INSERT INTO TB_" + sPre + "_PARA_FIXINTEREST( ");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append(" FSTARTDATE,");
            bufSql.append(" FISSUEDATE,");
            bufSql.append(" FISSUEPRICE,");
            bufSql.append(" FINSSTARTDATE,");
            bufSql.append(" FINSENDDATE,");
            bufSql.append(" FINSCASHDATE,");
            bufSql.append(" FFACEVALUE,");
            bufSql.append(" FFACERATE,");
            bufSql.append(" FINSFREQUENCY,");
            bufSql.append(" FQUOTEWAY,");
            bufSql.append(" FCREDITLEVEL,");
            bufSql.append(" FCALCINSMETICDAY,");
            bufSql.append(" FCALCINSMETICBUY,");
            bufSql.append(" FCALCINSMETICSELL,");
            bufSql.append(" FCALCPRICEMETIC,");
            bufSql.append(" FCALCINSCFGDAY,");
            bufSql.append(" FCALCINSCFGBUY,");
            bufSql.append(" FCALCINSCFGSELL,");
            bufSql.append(" FCALCINSWAY,");
            bufSql.append(" FINTERESTORIGIN,");
            bufSql.append(" FPEREXPCODE,");
            bufSql.append(" FPERIODCODE,");
            bufSql.append(" FROUNDCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append(" FSTARTDATE,");
            bufSql.append(" FISSUEDATE,");
            bufSql.append(" FISSUEPRICE,");
            bufSql.append(" FINSSTARTDATE,");
            bufSql.append(" FINSENDDATE,");
            bufSql.append(" FINSCASHDATE,");
            bufSql.append(" FFACEVALUE,");
            bufSql.append(" FFACERATE,");
            bufSql.append(" FINSFREQUENCY,");
            bufSql.append(" FQUOTEWAY,");
            bufSql.append(" FCREDITLEVEL,");
            bufSql.append(" FCALCINSMETICDAY,");
            bufSql.append(" FCALCINSMETICBUY,");
            bufSql.append(" FCALCINSMETICSELL,");
            bufSql.append(" FCALCPRICEMETIC,");
            bufSql.append(" FCALCINSCFGDAY,");
            bufSql.append(" FCALCINSCFGBUY,");
            bufSql.append(" FCALCINSCFGSELL,");
            bufSql.append(" FCALCINSWAY,");
            bufSql.append(" FINTERESTORIGIN,");
            bufSql.append(" FPEREXPCODE,");
            bufSql.append(" FPERIODCODE,");
            bufSql.append(" FROUNDCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_05162008052251 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_PARA_FIXINTEREST ADD CONSTRAINT PK_Tb_" + sPre +
                           "_Para_Fix PRIMARY KEY (FSECURITYCODE,FSTARTDATE)");
            //更改股票分红 分红类型的长度
            bufSql.delete(0, bufSql.length());
            strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre + "_DATA_DIVIDEND");
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_DATA_DIVIDEND DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_05282008020038")) {
                this.dropTableByTableName("TB_05282008020038");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_DATA_DIVIDEND TO TB_05282008020038");
            bufSql.append("CREATE TABLE TB_" + sPre + "_DATA_DIVIDEND");
            bufSql.append(" (");
            bufSql.append(" FSECURITYCODE   VARCHAR(20)   NOT NULL,");
            bufSql.append(" FRECORDDATE     DATE          NOT NULL,");
            bufSql.append(" FDIVDENDTYPE    DECIMAL(2)    NOT NULL,");
            bufSql.append(" FCURYCODE       VARCHAR(20)   NOT NULL DEFAULT ' ',");
            bufSql.append(" FDIVIDENDDATE   DATE          NOT NULL,");
            bufSql.append(" FDISTRIBUTEDATE DATE          NOT NULL,");
            bufSql.append(" FAFFICHEDATE    DATE,");
            bufSql.append(" FRATIO          DECIMAL(18,8) NOT NULL,");
            bufSql.append(" FROUNDCODE      VARCHAR(20)   NOT NULL,");
            bufSql.append(" FDESC           VARCHAR(100),");
            bufSql.append(" FCHECKSTATE     DECIMAL(1)    NOT NULL,");
            bufSql.append(" FCREATOR        VARCHAR(20)   NOT NULL,");
            bufSql.append(" FCREATETIME     VARCHAR(20)   NOT NULL,");
            bufSql.append(" FCHECKUSER      VARCHAR(20),");
            bufSql.append(" FCHECKTIME      VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bufSql.delete(0, bufSql.length());
            bTrans = true;
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_DIVIDEND(");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append(" FRECORDDATE,");
            bufSql.append(" FDIVDENDTYPE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FDIVIDENDDATE,");
            bufSql.append(" FDISTRIBUTEDATE,");
            bufSql.append(" FAFFICHEDATE,");
            bufSql.append(" FRATIO,");
            bufSql.append(" FROUNDCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append(" FRECORDDATE,");
            bufSql.append(" FDIVDENDTYPE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FDIVIDENDDATE,");
            bufSql.append(" FDISTRIBUTEDATE,");
            bufSql.append(" FAFFICHEDATE,");
            bufSql.append(" FRATIO,");
            bufSql.append(" FROUNDCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_05282008020038 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_DIVIDEND ADD CONSTRAINT PK_Tb_" + sPre +
                           "_Data_Div PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FDIVDENDTYPE,FCURYCODE)");
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003 执行调整字段精度 SQL 语句出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
