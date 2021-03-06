package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.*;

public class Ora1010003
    extends BaseDbUpdate {
    public Ora1010003() {
    }

    //创建带组合群号的表
    public void createTable(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer(10000);
        try {
            //--------------------------创建表Tb_001_Vch_BuildLink生成凭证链接表---------------------------//
            if (!dbl.yssTableExist("Tb_" + sPre + "_Vch_BuildLink")) {
                bufSql.append("CREATE TABLE Tb_" + sPre + "_Vch_BuildLink ");
                bufSql.append("(");
                bufSql.append("FProjectCode VARCHAR2(20)  NOT NULL,");
                bufSql.append("FAttrCode    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FAttrName    VARCHAR2(50)  NOT NULL,");
                bufSql.append("FDesc        VARCHAR2(100)     NULL,");
                bufSql.append("FCheckState  NUMBER(1)     NOT NULL,");
                bufSql.append("FCreator     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCreateTime  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCheckUser   VARCHAR2(20)      NULL,");
                bufSql.append("FCheckTime   VARCHAR2(20)      NULL,");
                bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                              "_Vch_BuildLink PRIMARY KEY (FProjectCode,FAttrCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("Tb_" + sPre + "_Vch_Project")) {
                bufSql.delete(0, bufSql.length());
                //--------------------------创建表Tb_001_Vch_Project生成凭证方案表---------------------------//
                bufSql.append("CREATE TABLE Tb_" + sPre + "_Vch_Project ");
                bufSql.append("(");
                bufSql.append("FProjectCode  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FProjectName  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FExeOrderCode NUMBER(1)     NOT NULL,");
                bufSql.append("FExBuild  NUMBER(1)     DEFAULT 0 NOT NULL,");
                bufSql.append("FExCheck  NUMBER(1)     DEFAULT 0 NOT NULL,");
                bufSql.append("FExInsert NUMBER(1)     DEFAULT 0 NOT NULL,");
                bufSql.append("FDesc        VARCHAR2(100)     NULL,");
                bufSql.append("FCheckState  NUMBER(1)     NOT NULL,");
                bufSql.append("FCreator     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCreateTime  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCheckUser   VARCHAR2(20)      NULL,");
                bufSql.append("FCheckTime   VARCHAR2(20)      NULL,");
                bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                              "_Vch_Project PRIMARY KEY (FProjectCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("Tb_" + sPre + "_Comp_IndexCfg")) {
                //--------------------------创建表 Tb_Comp_IndexCfg 监控指标配置-----------------------------------//
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE Tb_" + sPre + "_Comp_IndexCfg ");
                bufSql.append("(");
                bufSql.append("FIndexCfgCode    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FIndexCfgName    VARCHAR2(50)  NOT NULL,");
                bufSql.append("FIndexType       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FBeanId          VARCHAR2(30)      NULL,");
                bufSql.append("FCompParam       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FRepCod          VARCHAR2(20)      NULL,");
                bufSql.append("FIndexDS         CLOB              NULL,");
                bufSql.append("FMemoyWay        VARCHAR2(20)  NOT NULL,");
                bufSql.append("FTgtTableView    VARCHAR2(30)      NULL,");
                bufSql.append("FBeforeComp      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FFinalComp       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FWarnAnalysis    VARCHAR2(500)     NULL,");
                bufSql.append("FViolateAnalysis VARCHAR2(500)     NULL,");
                bufSql.append("FForbidAnalysis  VARCHAR2(500)     NULL,");
                bufSql.append("FDesc            VARCHAR2(100)     NULL,");
                bufSql.append("FCheckState      NUMBER(1)     NOT NULL,");
                bufSql.append("FCreator         VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCreateTime      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCheckUser       VARCHAR2(20)      NULL,");
                bufSql.append("FCheckTime       VARCHAR2(20)      NULL,");
                bufSql.append(" CONSTRAINT PK_Tb_" + sPre + "_Comp_IndexCfg");
                bufSql.append(" PRIMARY KEY (FIndexCfgCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            //----------------------------------------------------------------------------------------------//
            if (!dbl.yssTableExist("Tb_" + sPre + "_Comp_ResultData")) {
                //--------------------------创建表 Tb_Comp_ResultData 监控结果------------------------------------//
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE Tb_" + sPre + "_Comp_ResultData ");
                bufSql.append("(");
                bufSql.append("FCompDate     DATE          NOT NULL,");
                bufSql.append("FPortCode     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FIndexCfgCode VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCompResult   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FDesc         VARCHAR2(100)     NULL,");
                bufSql.append("FCheckState   NUMBER(1)     NOT NULL,");
                bufSql.append("FCreator      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCreateTime   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCheckUser    VARCHAR2(20)      NULL,");
                bufSql.append("FCheckTime    VARCHAR2(20)      NULL,");
                bufSql.append(" CONSTRAINT PK_Tb_" + sPre + "_Comp_ResultData");
                bufSql.append(" PRIMARY KEY (FCompDate,FPortCode,FIndexCfgCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            //----------------------------------------------------------------------------------------------//
            if (!dbl.yssTableExist("Tb_" + sPre + "_Comp_PortIndexLink")) {
                //--------------------------创建表 Tb_Comp_PortIndexLink 组合指标关联设置表---------------------------//
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE Tb_" + sPre + "_Comp_PortIndexLink ");
                bufSql.append("(");
                bufSql.append("FPortCode     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FIndexCfgCode VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCtlGrpCode   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCtlCode      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCtlValue     VARCHAR2(100)     NULL,");
                bufSql.append("FDesc         VARCHAR2(100)     NULL,");
                bufSql.append("FCheckState   NUMBER(1)     NOT NULL,");
                bufSql.append("FCreator      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCreateTime   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCheckUser    VARCHAR2(20)      NULL,");
                bufSql.append("FCheckTime    VARCHAR2(20)      NULL,");
                bufSql.append(" CONSTRAINT PK_Tb_" + sPre + "_Comp_PortIndexLink ");
                bufSql.append(
                    " PRIMARY KEY (FPortCode,FIndexCfgCode,FCtlGrpCode,FCtlCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            //-----------------------------------------------------------------------------------------------//
            if (!dbl.yssTableExist("Tb_" + sPre + "_PFOper_SchProject")) {
                //--------------------------创建表 Tb_PFOper_SchProject 调度方案 ---------------------------------//
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE Tb_" + sPre + "_PFOper_SchProject ");
                bufSql.append("(");
                bufSql.append("FProjectCode  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FFunModules   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FAttrCode     CLOB              NULL,");
                bufSql.append("FExeOrderCode NUMBER(3)     NOT NULL,");
                bufSql.append("FProjectName  VARCHAR2(50)  NOT NULL,");
                bufSql.append("FDesc         VARCHAR2(100)     NULL,");
                bufSql.append("FCheckState   NUMBER(1)     NOT NULL,");
                bufSql.append("FCreator      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCreateTime   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCheckUser    VARCHAR2(20)      NULL,");
                bufSql.append("FCheckTime    VARCHAR2(20)      NULL,");
                bufSql.append(" CONSTRAINT PK_Tb_" + sPre + "_PFOper_SchProject");
                bufSql.append(
                    " PRIMARY KEY (FProjectCode,FFunModules,FExeOrderCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            //----------------------------------------------------------------------------------------------//
            if (!dbl.yssTableExist("Tb_" + sPre + "_PFOper_PUBPARA")) {
                //--------------------------创建表TB_001_PFOper_PUBPARA通用参数类型设定表---------------------------//
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE Tb_" + sPre + "_PFOper_PUBPARA ");
                bufSql.append("(");
                bufSql.append("FPubParaCode   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FParaGroupCode VARCHAR2(50)  NOT NULL,");
                bufSql.append("FParaId        NUMBER(2)     DEFAULT 0  NOT NULL,");
                bufSql.append("FPubParaName   VARCHAR2(50)  NOT NULL,");
                bufSql.append("FCtlGrpCode    VARCHAR2(20)      NULL,");
                bufSql.append("FCtlCode       VARCHAR2(20)  DEFAULT '' NOT NULL,");
                bufSql.append("FCtlValue      VARCHAR2(4000)    NULL,"); //2008.05.22 蒋锦 修改 将长度由1000改为4000
                bufSql.append("FOrderCode     VARCHAR2(50)  NOT NULL,");
                bufSql.append("FDesc          VARCHAR2(100)     NULL,");
                bufSql.append(" CONSTRAINT PK_Tb_" + sPre + "_PFOper_PubPara ");
                bufSql.append(
                    " PRIMARY KEY (FPubParaCode,FParaId,FCtlCode)"); //去掉了FParaGroupCode作为主键 sj edit 20080528
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("Tb_" + sPre + "_PFOper_ValCompData")) {
                //新增表   TB_XXX_PFOper_ValCompData 表
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PFOper_ValCompData( ");
                bufSql.append(" FPortCode             VARCHAR2(20)      NOT NULL,");
                bufSql.append(" FComProjectCode       VARCHAR2(20)      NOT NULL,");
                bufSql.append(" FGZKeyCode            VARCHAR2(500)      NOT NULL,");
                bufSql.append(" FGZKeyName            VARCHAR2(200)     NOT NULL,");
                bufSql.append(" FCWKeyCode            VARCHAR2(500)      NOT NULL,");
                bufSql.append(" FCWKeyName            VARCHAR2(200)     NOT NULL,");
                bufSql.append(
                    " FGZCost               NUMBER(18, 4)     DEFAULT 0 NOT NULL,");
                bufSql.append(
                    " FGZPortCost           NUMBER(18, 4)     DEFAULT 0 NOT NULL,");
                bufSql.append(
                    " FGZMarketValue        NUMBER(18, 4)     DEFAULT 0 NOT NULL,");
                bufSql.append(
                    " FGZPortMarketValue    NUMBER(18, 4)     DEFAULT 0 NOT NULL,");
                bufSql.append(
                    " FCWCost               NUMBER(18, 4)     DEFAULT 0 NOT NULL,");
                bufSql.append(
                    " FCWPortCost           NUMBER(18, 4)     DEFAULT 0 NOT NULL,");
                bufSql.append(
                    " FCWMarketValue        NUMBER(18, 4)     DEFAULT 0 NOT NULL,");
                bufSql.append(
                    " FCWPortMarketValue    NUMBER(18, 4)     DEFAULT 0 NOT NULL,");
                bufSql.append(" FGZAmount             NUMBER(20, 4),");
                bufSql.append(" FCWAmount             NUMBER(20, 4),");
                bufSql.append(" FORDER                NUMBER(10)    ,");
                bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_PFOper_ValCompData PRIMARY KEY (FPortCode, FComProjectCode, FGZKeyCode, FCWKeyCode)");
                bufSql.append(" )");
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
                bufSql.append("FSubAttrCode VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSubAttrName VARCHAR2(200)  NOT NULL,");
                bufSql.append("FSubType     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FDesc        VARCHAR2(100)     NULL,");
                bufSql.append("FCheckState  NUMBER(1)     NOT NULL,");
                bufSql.append("FCreator     VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCreateTime  VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCheckUser   VARCHAR2(20)      NULL,");
                bufSql.append("FCheckTime   VARCHAR2(20)      NULL,");
                bufSql.append(
                    "CONSTRAINT PK_TB_Base_SubjectAttr PRIMARY KEY (FSubAttrCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("Tb_PFSys_FaceCfgInfo")) {
                //--------------------------创建表 Tb_PF_FaceCfgInfo 界面配置信息表---------------------------//
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE Tb_PFSys_FaceCfgInfo");
                bufSql.append("( ");
                bufSql.append("FCtlGrpCode VARCHAR2(20)   NOT NULL,");
                bufSql.append("FCtlCode    VARCHAR2(20)   NOT NULL,");
                bufSql.append("FCtlGrpName VARCHAR2(50)   NOT NULL,");
                bufSql.append("FParamIndex NUMBER(3)      NOT NULL,");
                bufSql.append("FCtlType    NUMBER(1)      NOT NULL,");
                bufSql.append("FParam      VARCHAR2(2000)     NULL,");
                bufSql.append("FCtlInd     VARCHAR2(100)      NULL,");
                bufSql.append("FFunModules VARCHAR2(20)   NOT NULL,");
                bufSql.append("FCheckState NUMBER(1)      NOT NULL,");
                bufSql.append("FCreator    VARCHAR2(20)   NOT NULL,");
                bufSql.append("FCreateTime VARCHAR2(20)   NOT NULL,");
                bufSql.append("FCheckUser  VARCHAR2(20)       NULL,");
                bufSql.append("FCheckTime  VARCHAR2(20)       NULL,");
                bufSql.append(
                    " CONSTRAINT PK_Tb_PFSys_FaceCfgInfo PRIMARY KEY (FCtlGrpCode,FCtlCode)");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_PFSys_InOutCfg")) {
                //新增Tb_PF_InOutCfg表
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_PFSys_InOutCfg ");
                bufSql.append(" (");
                bufSql.append(" FINOUTCODE    VARCHAR2(20)   NOT NULL,");
                bufSql.append(" FINOUTNAME    VARCHAR2(50)   NOT NULL,");
                bufSql.append(" FOutCfgScript VARCHAR2(3000) NOT NULL,");
                bufSql.append(" FInCfgScript  VARCHAR2(3000) NOT NULL,");
                bufSql.append(" FDESC         VARCHAR2(100)      NULL,");
                bufSql.append(" FCHECKSTATE   NUMBER(1)      NOT NULL,");
                bufSql.append(" FCREATOR      VARCHAR2(20)   NOT NULL,");
                bufSql.append(" FCREATETIME   VARCHAR2(20)   NOT NULL,");
                bufSql.append(" FCHECKUSER    VARCHAR2(20)       NULL,");
                bufSql.append(" FCHECKTIME    VARCHAR2(20)       NULL,");
                bufSql.append(
                    "  CONSTRAINT PK_Tb_PFSys_InOutCfg PRIMARY KEY (FINOUTCODE)");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_PFSys_ValCompare")) {
                //创建TB_PF_ValCompare表
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_PFSys_ValCompare( ");
                bufSql.append(" FComProjectCode    VARCHAR2(20)      NOT NULL,");
                bufSql.append(" FComProjectName    VARCHAR2(50)      NOT NULL,");
                bufSql.append(" FComScript         VARCHAR2(3000)    NOT NULL,");
                bufSql.append(" FDesc              VARCHAR2(200),");
                bufSql.append(" FCheckState        NUMBER(1, 0)      NOT NULL,");
                bufSql.append(" FCreator           VARCHAR2(20)      NOT NULL,");
                bufSql.append(" FCreateTime        VARCHAR2(20)      NOT NULL,");
                bufSql.append(" FCheckUser         VARCHAR2(20),");
                bufSql.append(" FCheckTime         VARCHAR2(20),");
                bufSql.append(
                    " CONSTRAINT PK_TB_PFSys_ValCompare PRIMARY KEY (FComProjectCode)");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("TB_PFSys_OperFunExtend")) {
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE TB_PFSys_OperFunExtend(");
                bufSql.append(" FExtCode        VARCHAR2(20)      NOT NULL,");
                bufSql.append(" FExtName        VARCHAR2(50)      NOT NULL,");
                bufSql.append(" FPubParaCode    VARCHAR2(20),");
                bufSql.append(" FLinkModule     VARCHAR2(20),");
                bufSql.append(" FExtScript      CLOB              NOT NULL,");
                bufSql.append(" FEnable         NUMBER(1, 0)      NOT NULL,");
                bufSql.append(" FDesc           VARCHAR2(100),");
                bufSql.append(" FCheckState     NUMBER(1, 0)      NOT NULL,");
                bufSql.append(" FCreator        VARCHAR2(20)      NOT NULL,");
                bufSql.append(" FCreateTime     VARCHAR2(20)      NOT NULL,");
                bufSql.append(" FCheckUser      VARCHAR2(20),");
                bufSql.append(" FCheckTime      VARCHAR2(20),");
                bufSql.append(" CONSTRAINT PK_TB_PFSys_OperFunExtend PRIMARY KEY (FExtCode)");
                bufSql.append(")");
                dbl.executeSql(bufSql.toString());
            }
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003 创建表出错！", e);
        }
    }

    //向带组合编号的表添加字段
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {

            //------------------------- TB_001_TA_TRADE  添加字段 FMarkDate --------------------------//
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_TA_TRADE")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_TA_TRADE") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_TA__03052008055429000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_TA__03052008055429000");
            }
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_TA_TRADE") +
                           " RENAME TO TB_" + sPre + "_TA__03052008055429000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_TA_TRADE"));
            bufSql.append(" (");
            bufSql.append("FNum           VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRADEDATE     DATE          NOT NULL,");
            bufSql.append("FMarkDate      DATE              NULL,");
            bufSql.append("FPORTCODE      VARCHAR2(20)      NULL,");
            bufSql.append("FPORTCLSCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSELLNETCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSELLTYPE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCURYCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE1 VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE2 VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE3 VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCASHACCCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSELLMONEY     NUMBER(18,4)  NOT NULL,");
            bufSql.append("FSELLAMOUNT    NUMBER(18,4)  NOT NULL,");
            bufSql.append("FSELLPRICE     NUMBER(18,4)  NOT NULL,");
            bufSql.append("FINCOMENOTBAL  NUMBER(18,4)      NULL,");
            bufSql.append("FINCOMEBAL     NUMBER(18,4)      NULL,");
            bufSql.append("FCONFIMDATE    DATE          NOT NULL,");
            bufSql.append("FSETTLEDATE    DATE          NOT NULL,");
            bufSql.append("FPORTCURYRATE  NUMBER(20,15) NOT NULL,");
            bufSql.append("FBASECURYRATE  NUMBER(20,15) NOT NULL,");
            bufSql.append("FFEECODE1      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE1     NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE2      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE2     NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE3      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE3     NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE4      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE4     NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE5      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE5     NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE6      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE6     NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE7      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE7     NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE8      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE8     NUMBER(18,4)      NULL,");
            bufSql.append("FSETTLESTATE   NUMBER(1)     DEFAULT 0 NOT NULL,");
            bufSql.append("FDESC          VARCHAR2(100)     NULL,");
            bufSql.append("FCHECKSTATE    NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER     VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME     VARCHAR2(20)      NULL");
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
            bufSql.append("null,");
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
            bufSql.append(" FROM TB_" + sPre + "_TA__03052008055429000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("Tb_TA_Trade") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_TA_Trade " +
                           " PRIMARY KEY (FNum)");
            //------------------对表Tb_rep_cell增加 FIsMergeCol字段--------------------------
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "Tb_rep_cell")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("Tb_rep_cell") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_rep_03142008103006000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_rep_03142008103006000");
            }
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("Tb_rep_cell") +
                           " RENAME TO TB_" + sPre + "_rep_03142008103006000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Rep_Cell");
            bufSql.append("(");
            bufSql.append("FRELACODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FRELATYPE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FROW        NUMBER(5)     NOT NULL,");
            bufSql.append("FCOL        NUMBER(5)     NOT NULL,");
            bufSql.append("FCONTENT    VARCHAR2(600)     NULL,");
            bufSql.append("FLLINE      NUMBER(3)     DEFAULT 0 NOT NULL,");
            bufSql.append("FTLINE      NUMBER(3)     DEFAULT 0 NOT NULL,");
            bufSql.append("FRLINE      NUMBER(3)     DEFAULT 0 NOT NULL,");
            bufSql.append("FBLINE      NUMBER(3)     DEFAULT 0 NOT NULL,");
            bufSql.append("FLCOLOR     NUMBER(10)    DEFAULT 0 NOT NULL,");
            bufSql.append("FTCOLOR     NUMBER(10)    DEFAULT 0 NOT NULL,");
            bufSql.append("FRCOLOR     NUMBER(10)    DEFAULT 0 NOT NULL,");
            bufSql.append("FBCOLOR     NUMBER(10)    DEFAULT 0 NOT NULL,");
            bufSql.append("FBACKCOLOR  NUMBER(10)    DEFAULT 0 NOT NULL,");
            bufSql.append("FFORECOLOR  NUMBER(10)    DEFAULT 0 NOT NULL,");
            bufSql.append("FFONTNAME   VARCHAR2(50)  NOT NULL,");
            bufSql.append("FFONTSIZE   NUMBER(3)     NOT NULL,");
            bufSql.append("FFONTSTYLE  NUMBER(3)     NOT NULL,");
            bufSql.append("FDATATYPE   NUMBER(3)     NOT NULL,");
            bufSql.append("FIsMergeCol NUMBER(1)     DEFAULT 0 NOT NULL,");
            bufSql.append("FFORMAT     VARCHAR2(100)     NULL");
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
            bufSql.append("FFORMAT ");
            bufSql.append(" FROM TB_" + sPre + "_rep_03142008103006000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre + "_Rep_Cell ADD CONSTRAINT PK_TB_" + sPre + "_REP_CELL PRIMARY KEY (FRELACODE,FRELATYPE,FROW,FCOL)");
            //-----------------------Tb_XXX_TA_Trade 中添加 FSettleMoney 字段-------------
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "Tb_TA_Trade")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("Tb_TA_Trade") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_TA__03252008012038000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_TA__03252008012038000");
            }
            dbl.executeSql("ALTER TABLE " +

                           pub.yssGetTableName("Tb_TA_Trade") +
                           " RENAME TO TB_" + sPre + "_TA__03252008012038000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_TA_Trade ");
            bufSql.append("(");
            bufSql.append(" FNum           VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FTRADEDATE     DATE          NOT NULL,");
            bufSql.append(" FMARKDATE      DATE              NULL,");
            bufSql.append(" FPORTCODE      VARCHAR2(20)      NULL,");
            bufSql.append(" FPORTCLSCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FSELLNETCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FSELLTYPE      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCURYCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FANALYSISCODE1 VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FANALYSISCODE2 VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FANALYSISCODE3 VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCASHACCCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FSELLMONEY     NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FSELLAMOUNT    NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FSELLPRICE     NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FINCOMENOTBAL  NUMBER(18,4)      NULL,");
            bufSql.append(" FINCOMEBAL     NUMBER(18,4)      NULL,");
            bufSql.append(" FCONFIMDATE    DATE          NOT NULL,");
            bufSql.append(" FSETTLEDATE    DATE          NOT NULL,");
            bufSql.append(" FSettleMoney   NUMBER(18,4)      NULL,");
            bufSql.append(" FPORTCURYRATE  NUMBER(20,15) NOT NULL,");
            bufSql.append(" FBASECURYRATE  NUMBER(20,15) NOT NULL,");
            bufSql.append(" FFEECODE1      VARCHAR2(20)      NULL,");
            bufSql.append(" FTRADEFEE1     NUMBER(18,4)      NULL,");
            bufSql.append(" FFEECODE2      VARCHAR2(20)      NULL,");
            bufSql.append(" FTRADEFEE2     NUMBER(18,4)      NULL,");
            bufSql.append(" FFEECODE3      VARCHAR2(20)      NULL,");
            bufSql.append(" FTRADEFEE3     NUMBER(18,4)      NULL,");
            bufSql.append(" FFEECODE4      VARCHAR2(20)      NULL,");
            bufSql.append(" FTRADEFEE4     NUMBER(18,4)      NULL,");
            bufSql.append(" FFEECODE5      VARCHAR2(20)      NULL,");
            bufSql.append(" FTRADEFEE5     NUMBER(18,4)      NULL,");
            bufSql.append(" FFEECODE6      VARCHAR2(20)      NULL,");
            bufSql.append(" FTRADEFEE6     NUMBER(18,4)      NULL,");
            bufSql.append(" FFEECODE7      VARCHAR2(20)      NULL,");
            bufSql.append(" FTRADEFEE7     NUMBER(18,4)      NULL,");
            bufSql.append(" FFEECODE8      VARCHAR2(20)      NULL,");
            bufSql.append(" FTRADEFEE8     NUMBER(18,4)      NULL,");
            bufSql.append(" FSETTLESTATE   NUMBER(1)     DEFAULT 0 NOT NULL,");
            bufSql.append(" FDESC          VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE    NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR       VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER     VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME     VARCHAR2(20)      NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.append(" INSERT INTO Tb_" + sPre + "_TA_Trade( ");
            bufSql.append(" FNum,");
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
            bufSql.append(" FROM TB_" + sPre + "_TA__03252008012038000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql(" ALTER TABLE Tb_" + sPre +
                           "_TA_Trade ADD CONSTRAINT PK_Tb_" + sPre +
                           "_TA_Trade PRIMARY KEY (FNum)");
            //-----------------------Tb_XXX_Vch_VChTpl 中添加 FPortCode 字段-------------
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_VCH_VCHTPL")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_VCH_VCHTPL") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_03252008012358000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_VCH_03252008012358000");
            }
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_VCH_VCHTPL") +
                           " RENAME TO TB_" + sPre + "_VCH_03252008012358000");
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE Tb_" + sPre + "_Vch_VchTpl ");
            bufSql.append(" (");
            bufSql.append(" FVCHTPLCODE VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FSRCCURY    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FVCHTPLNAME VARCHAR2(50)  NOT NULL,");
            bufSql.append(" FPortCode   VARCHAR2(20)      NULL,");
            bufSql.append(" FATTRCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDATEFIELD  VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCURYCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDESC       VARCHAR2(100)     NULL,");
            bufSql.append(" FMODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDSCODE     VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FFIELDS     VARCHAR2(150)     NULL,");
            bufSql.append(" FVCHTWAY    VARCHAR2(20)      NULL,");
            bufSql.append(" FLINKCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKSTATE NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER  VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME  VARCHAR2(20)      NULL ");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(false);
            bTrans = true;
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
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append(" FVCHTPLCODE,");
            bufSql.append(" FSRCCURY,");
            bufSql.append(" FVCHTPLNAME,");
            bufSql.append(" null,");
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
            bufSql.append(" FROM TB_" + sPre + "_VCH_03252008012358000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql(" ALTER TABLE Tb_" + sPre +
                           "_Vch_VchTpl ADD CONSTRAINT PK_TB_" + sPre +
                           "_VCH_VCHTPL PRIMARY KEY (FVCHTPLCODE)");
            //-----------------------Tb_XXX_Vch_Dict 中添加 FPortCode 字段-------------
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_VCH_Dict")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_VCH_Dict") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_03252008012401000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_VCH_03252008012401000");
            }
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_VCH_Dict") +
                           " RENAME TO TB_" + sPre + "_VCH_03252008012401000");
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE Tb_" + sPre + "_Vch_Dict ");
            bufSql.append(" (");
            bufSql.append(" FDICTCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FINDCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDICTNAME   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FPortCode   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCNVCONENT  VARCHAR2(50)  NOT NULL,");
            bufSql.append(" FDESC       VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER  VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME  VARCHAR2(20)      NULL ");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(false);
            bTrans = true;
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
            bufSql.append(" ) ");
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
            bufSql.append(" FROM TB_" + sPre + "_VCH_03252008012401000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Vch_Dict ADD CONSTRAINT PK_TB_" + sPre +
                           "_VCH_DICT PRIMARY KEY (FDICTCODE,FINDCODE,FPortCode)");
            //------------------------- Tb_XXX_Para_AffiliatedCorp   添加字段 FOrgCodeType和FOrgCode --------------------------//2008-5-27 单亮

            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "Tb_Para_AffiliatedCorp")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre +
                    "_Para_AffiliatedCorp DROP CONSTRAINT " + strPKName + " CASCADE"); //删除表主键
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_02282008123619000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_02282008123619000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Para_AffiliatedCorp RENAME TO TB_"
                           + sPre + "_DAT_02282008123619000"); //将原表更名为临时表
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Para_AffiliatedCorp ");
            bufSql.append("(");
            bufSql.append(" FAFFCORPCODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FSTARTDATE   DATE          NOT NULL,");
            bufSql.append("    FPARENTCODE  VARCHAR2(20)      NULL,");
            bufSql.append("    FAFFCORPNAME VARCHAR2(200) NOT NULL,");
            bufSql.append("    FARTPERSON   VARCHAR2(50)      NULL,");
            bufSql.append("    FCAPITALCURY VARCHAR2(20)      NULL,");
            bufSql.append("    FREGCAPITAL  NUMBER(18,4)      NULL,");
            bufSql.append("    FREGADDR     VARCHAR2(100)     NULL,");
            bufSql.append("    FORGCODETYPE VARCHAR2(20)      NULL,");
            bufSql.append("    FORGCODE     VARCHAR2(50)      NULL,");
            bufSql.append("    FDESC        VARCHAR2(200)     NULL,");
            bufSql.append("    FCHECKSTATE  NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR     VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER   VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME   VARCHAR2(20)      NULL");
            bufSql.append(")");

            dbl.executeSql(bufSql.toString());

            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_" + sPre + "_Para_AffiliatedCorp( ");

            bufSql.append(" FAFFCORPCODE,");
            bufSql.append(" FSTARTDATE,");
            bufSql.append(" FPARENTCODE,");
            bufSql.append(" FAFFCORPNAME,");
            bufSql.append(" FARTPERSON,");
            bufSql.append(" FCAPITALCURY,");
            bufSql.append(" FREGCAPITAL,");
            bufSql.append(" FREGADDR,");
            bufSql.append(" FORGCODETYPE,");
            bufSql.append(" FORGCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(")");
            bufSql.append(" SELECT ");
            bufSql.append(" FAFFCORPCODE,");
            bufSql.append(" FSTARTDATE,");
            bufSql.append(" FPARENTCODE,");
            bufSql.append(" FAFFCORPNAME,");
            bufSql.append(" FARTPERSON,");
            bufSql.append(" FCAPITALCURY,");
            bufSql.append(" FREGCAPITAL,");
            bufSql.append(" FREGADDR,");
            bufSql.append(" NULL,");
            bufSql.append(" NULL,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");

            bufSql.append(" FROM TB_" + sPre + "_DAT_02282008123619000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Para_AffiliatedCorp ADD CONSTRAINT PK_TB_" + sPre +
                           "_Para_AffiliatedCorp PRIMARY KEY (FAffCorpCode, FStartDate)");

            //------------------------------------------------------------------------------------------//2008-5-27 单亮
            //------------------------- TB_001_DATA_SECRECPAY   添加字段 FDESC --------------------------//2008-4-22 单亮1
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_DATA_SECRECPAY")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " + //删除主键
                               pub.yssGetTableName("TB_DATA_SECRECPAY") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_TA__03052008055429000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_TA__03052008055429000");
            }
            dbl.executeSql("ALTER TABLE " + //修改临时表的名字
                           pub.yssGetTableName("TB_DATA_SECRECPAY") +
                           " RENAME TO TB_" + sPre + "_TA__03052008055429000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + //创建表
                          pub.yssGetTableName("TB_DATA_SECRECPAY"));
            bufSql.append(" (");
            bufSql.append("FNUM            VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FDESC           VARCHAR2(100)     NULL,");
            bufSql.append("    FTRANSDATE      DATE          NOT NULL,");
            bufSql.append("    FPORTCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FSECURITYCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FTSFTYPECODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FSUBTSFTYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCATTYPE        VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FATTRCLSCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCURYCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FINOUT          NUMBER(1)     NOT NULL,");
            bufSql.append("    FMONEY          NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FMMONEY         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FVMONEY         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FBASECURYRATE   NUMBER(20,15) NOT NULL,");
            bufSql.append("    FBASECURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FMBASECURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FVBASECURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FPORTCURYRATE   NUMBER(20,15) NOT NULL,");
            bufSql.append("    FPORTCURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FMPORTCURYMONEY NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FVPORTCURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FDATASOURCE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FSTOCKIND       NUMBER(1)     NOT NULL,");
            bufSql.append("    FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME      VARCHAR2(20)      NULL");
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
            bufSql.append("                   )");
            bufSql.append("             SELECT ");
            bufSql.append("FNUM,");
            bufSql.append("NULL,");
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

            bufSql.append(" FROM TB_" + sPre + "_TA__03052008055429000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_SECRECPAY ") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_Data_SECRECPAY " +
                           " PRIMARY KEY (FNum)");

            //------------------------- Tb_001_Data_CashPayRec   添加字段 FDESC --------------------------//2008-4-22 单亮2
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_DATA_CASHPAYREC")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " + //删除主键
                               pub.yssGetTableName("TB_DATA_CASHPAYREC") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_TA__03052008055429000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_TA__03052008055429000");
            }
            dbl.executeSql("ALTER TABLE " + //修改临时表的名字
                           pub.yssGetTableName("TB_DATA_CASHPAYREC") +
                           " RENAME TO TB_" + sPre + "_TA__03052008055429000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + //创建表
                          pub.yssGetTableName("TB_DATA_CASHPAYREC"));
            bufSql.append(" (");
            bufSql.append("    FNUM            VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FDESC           VARCHAR2(100)     NULL,");
            bufSql.append("    FTRANSDATE      DATE          NOT NULL,");
            bufSql.append("    FPORTCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCASHACCCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FTSFTYPECODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FSUBTSFTYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCURYCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FINOUT          NUMBER(1)     DEFAULT 1 NOT NULL,");
            bufSql.append("    FMONEY          NUMBER(35,4)  NOT NULL,");
            bufSql.append("    FBASECURYRATE   NUMBER(25,15) NOT NULL,");
            bufSql.append("    FBASECURYMONEY  NUMBER(35,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FPORTCURYRATE   NUMBER(25,15) NOT NULL,");
            bufSql.append("    FPORTCURYMONEY  NUMBER(35,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FDATASOURCE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FSTOCKIND       NUMBER(1)     NOT NULL,");
            bufSql.append("    FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME      VARCHAR2(20)      NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_DATA_CASHPAYREC") + "(");
            bufSql.append("FNUM,");
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
            bufSql.append("              SELECT ");
            bufSql.append(" FNUM,");
            bufSql.append(" NULL,");
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

            bufSql.append(" FROM TB_" + sPre + "_TA__03052008055429000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_CASHPAYREC ") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_Data_CASHPAYREC " +
                           " PRIMARY KEY (FNum)");
            //------------------------- Tb_001_Data_InvestPayRec   添加字段 FDESC --------------------------//2008-4-22 单亮3
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "Tb_Data_InvestPayRec")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " + //删除主键
                               pub.yssGetTableName("Tb_Data_InvestPayRec") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_TA__03052008055429000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_TA__03052008055429000");
            }
            dbl.executeSql("ALTER TABLE " + //修改临时表的名字
                           pub.yssGetTableName("Tb_Data_InvestPayRec") +
                           " RENAME TO TB_" + sPre + "_TA__03052008055429000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + //创建表
                          pub.yssGetTableName("Tb_Data_InvestPayRec"));
            bufSql.append(" (");
            bufSql.append("FNUM            VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FDESC           VARCHAR2(100)     NULL,");
            bufSql.append("    FTRANSDATE      DATE          NOT NULL,");
            bufSql.append("    FPORTCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FIVPAYCATCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FTSFTYPECODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FSUBTSFTYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCURYCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FMONEY          NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FBASECURYRATE   NUMBER(20,15) NOT NULL,");
            bufSql.append("    FBASECURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FPORTCURYRATE   NUMBER(20,15) NOT NULL,");
            bufSql.append("    FPORTCURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FDATASOURCE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FSTOCKIND       NUMBER(1)     NOT NULL,");
            bufSql.append("    FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME      VARCHAR2(20)      NULL");

            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("Tb_Data_InvestPayRec") + "(");
            bufSql.append("FNUM,");
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
            bufSql.append("                SELECT ");
            bufSql.append("   FNUM,");
            bufSql.append("   NULL,");
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

            bufSql.append(" FROM TB_" + sPre + "_TA__03052008055429000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("Tb_Data_InvestPayRec ") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_Data_InvestPayRec " +
                           " PRIMARY KEY (FNum)");

            //------------------------- Tb_001_Data_RightsIssue   添加字段 FDESC --------------------------//2008-4-22 单亮4
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "Tb_Data_RightsIssue")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080602
                dbl.executeSql("ALTER TABLE " + //删除主键
                               pub.yssGetTableName("Tb_Data_RightsIssue") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_TA__03052008055429000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_TA__03052008055429000");
            }
            dbl.executeSql("ALTER TABLE " + //修改临时表的名字
                           pub.yssGetTableName("Tb_Data_RightsIssue") +
                           " RENAME TO TB_" + sPre + "_TA__03052008055429000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + //创建表
                          pub.yssGetTableName("Tb_Data_RightsIssue"));
            bufSql.append(" (");
            bufSql.append("FSECURITYCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FRECORDDATE     DATE          NOT NULL,");
            bufSql.append("    FRICURYCODE     VARCHAR2(20)      NULL,");
            bufSql.append("    FTSECURITYCODE  VARCHAR2(20)      NULL,");
            bufSql.append("    FEXRIGHTDATE    DATE          NOT NULL,");
            bufSql.append("    FEXPIRATIONDATE DATE          NOT NULL,");
            bufSql.append("    FAFFICHEDATE    DATE          NOT NULL,");
            bufSql.append("    FPAYDATE        DATE          NOT NULL,");
            bufSql.append("    FBEGINSCRIDATE  DATE          NOT NULL,");
            bufSql.append("    FENDSCRIDATE    DATE          NOT NULL,");
            bufSql.append("    FBEGINTRADEDATE DATE          NOT NULL,");
            bufSql.append("    FENDTRADEDATE   DATE          NOT NULL,");
            bufSql.append("    FRATIO          NUMBER(18,8)  NOT NULL,");
            bufSql.append("    FRIPRICE        NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FDESC           VARCHAR2(100)     NULL,");
            bufSql.append("    FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME      VARCHAR2(20)      NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("Tb_Data_RightsIssue") + "(");
            bufSql.append("FSECURITYCODE,");
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
            bufSql.append("               SELECT ");
            bufSql.append("  FSECURITYCODE,");
            bufSql.append("  FRECORDDATE,");
            bufSql.append("  NULL,");
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

            bufSql.append(" FROM TB_" + sPre + "_TA__03052008055429000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("Tb_Data_RightsIssue ") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_Data_RightsIssue " +
                           " PRIMARY KEY (FSecurityCode,FRecordDate)");
            //给Tb_XXX_rep_Cell 添加字段 FOTHERPARAMS by leeyu 080604
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_REP_CELL ADD FOTHERPARAMS VARCHAR2(500)     NULL");
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003 新增表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 调整表中数据
     * @throws YssException
     */
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
                "a.FCtlGrpCode," +
                "(case when b.FCtlGrpName is null then ' ' else b.FCtlGrpName end) as FCtlCrpName," + //FCtlCrpName可能为null sj edit 20080602
                "a.FCtlCode,FParamIndex," +
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

    //调整字段精度
    public void adjustFieldPrecision(String sPre) throws YssException {
        //------------------Tb_XXX_Dao_PRETREAT 的字段----------------------
        StringBuffer bufSql = new StringBuffer(5000);
        String strPKName = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre + "_DAO_PRETREAT MODIFY(FDPDSNAME  VARCHAR2(50))");
            //更改股票分红 分红类型 的长度
            dbl.executeSql("ALTER TABLE TB_" + sPre + "_DATA_DIVIDEND MODIFY(FDIVDENDTYPE  NUMBER(2))");
            //更改 Tb_XXX_Para_FixInterest表的票面面额的精度
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName("TB_PARA_FIXINTEREST"));
            if (strPKName.trim().length() != 0) {
                dbl.executeSql(" ALTER TABLE TB_" + sPre +
                               "_PARA_FIXINTEREST DROP CONSTRAINT PK_TB_" + sPre +
                               "_PARA_FIXINTEREST ");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_PAR_05162008052018000")) {
                this.dropTableByTableName("TB_" + sPre + "_PAR_05162008052018000");
            }
            dbl.executeSql(" ALTER TABLE TB_" + sPre + "_PARA_FIXINTEREST RENAME TO TB_" + sPre + "_PAR_05162008052018000");
            bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_FIXINTEREST ");
            bufSql.append(" ( ");
            bufSql.append(" FSECURITYCODE     VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FSTARTDATE        DATE          NOT NULL,");
            bufSql.append(" FISSUEDATE        DATE          NOT NULL,");
            bufSql.append(" FISSUEPRICE       NUMBER(18,12)  NOT NULL,");
            bufSql.append(" FINSSTARTDATE     DATE          NOT NULL,");
            bufSql.append(" FINSENDDATE       DATE          NOT NULL,");
            bufSql.append(" FINSCASHDATE      DATE          NOT NULL,");
            bufSql.append(" FFACEVALUE        NUMBER(18,4) NOT NULL,");
            bufSql.append(" FFACERATE         NUMBER(18,12)     NULL,");
            bufSql.append(" FINSFREQUENCY     NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FQUOTEWAY         NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREDITLEVEL      VARCHAR2(20)      NULL,");
            bufSql.append(" FCALCINSMETICDAY  VARCHAR2(20)      NULL,");
            bufSql.append(" FCALCINSMETICBUY  VARCHAR2(20)      NULL,");
            bufSql.append(" FCALCINSMETICSELL VARCHAR2(20)      NULL,");
            bufSql.append(" FCALCPRICEMETIC   VARCHAR2(20)      NULL,");
            bufSql.append(" FCALCINSCFGDAY    VARCHAR2(500)     NULL,");
            bufSql.append(" FCALCINSCFGBUY    VARCHAR2(500)     NULL,");
            bufSql.append(" FCALCINSCFGSELL   VARCHAR2(500)     NULL,");
            bufSql.append(" FCALCINSWAY       NUMBER(1)     NOT NULL,");
            bufSql.append(" FINTERESTORIGIN   NUMBER(1)     NOT NULL,");
            bufSql.append(" FPEREXPCODE       VARCHAR2(20)      NULL,");
            bufSql.append(" FPERIODCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FROUNDCODE        VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDESC             VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE       NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR          VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME       VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER        VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME        VARCHAR2(20)      NULL");
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
            bufSql.append(" FROM TB_" + sPre + "_PAR_05162008052018000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_PARA_FIXINTEREST ADD CONSTRAINT PK_TB_" + sPre +
                           "_PARA_FIXINTEREST PRIMARY KEY (FSECURITYCODE,FSTARTDATE)");

        } catch (SQLException SQLe) {
        } catch (YssException ex) {
            throw new YssException("版本1.0.1.0003 执行调整字段精度 SQL 语句出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //调制表名
    public void adjustTableName() throws YssException {
        /* try{
            //修改表 Tb_PF_FaceCfgInfo,TB_PF_InOutCfg,TB_PF_ValCompare 将其更改为 Tb_PFSys_FaceCfgInfo,TB_PFSys_InOutCfg,TB_PFSys_ValCompare
            dbl.executeSql(
                  " ALTER TABLE Tb_PF_FaceCfgInfo DROP CONSTRAINT PK_Tb_PF_FaceCfgInfo ");
            dbl.executeSql(
                  " ALTER TABLE Tb_PF_FaceCfgInfo RENAME TO Tb_PFSys_FaceCfgInfo ");
            dbl.executeSql(" ALTER TABLE Tb_PFSys_FaceCfgInfo ADD CONSTRAINT PK_Tb_PFSys_FaceCfgInfo PRIMARY KEY (FCtlGrpCode,FCtlCode) ");

            dbl.executeSql(
                  " ALTER TABLE TB_PF_InOutCfg DROP CONSTRAINT PK_TB_PF_InOutCfg ");
            dbl.executeSql(
                  " ALTER TABLE TB_PF_InOutCfg RENAME TO TB_PFSys_InOutCfg ");
            dbl.executeSql(" ALTER TABLE TB_PFSys_InOutCfg ADD CONSTRAINT PK_TB_PFSys_InOutCfg PRIMARY KEY (FInOutCode) ");

            dbl.executeSql(
                  " ALTER TABLE TB_PF_ValCompare DROP CONSTRAINT PK_TB_PF_ValCompare ");
            dbl.executeSql(
                  " ALTER TABLE TB_PF_ValCompare RENAME TO TB_PFSys_ValCompare ");
            dbl.executeSql(" ALTER TABLE TB_PFSys_ValCompare ADD  CONSTRAINT PK_TB_PFSys_ValCompare PRIMARY KEY (FComProjectCode) ");
         }catch(Exception ex){
            throw new YssException("版本1.0.1.0003 执行调整表名的 SQL 语句出错！",ex);
         }*/
    }

    /**
     * 调整带组合群号的表的主键 sj add
     * @param sPre String
     * @throws YssException
     */
    public void adjustTableKey(String sPre) throws YssException {
        String sqlStr = "";
        try {
            //---------------------TB_XXX_PFOper_PUBPARA(通用参数类型设定)的主键去掉FParaGroupCode(参数组编号) ---sj add 2008528----//
            if (dbl.yssTableExist("TB_" + sPre + "_PFOPER_PUBPARA")) {
                sqlStr = "alter table TB_" + sPre +
                    "_PFOPER_PUBPARA drop constraint PK_TB_" + sPre +
                    "_PFOPER_PUBPARA";
                dbl.executeSql(sqlStr);
                sqlStr = "alter table TB_" + sPre + "_PFOPER_PUBPARA"
                    + " add constraint PK_TB_" + sPre +
                    "_PFOPER_PUBPARA primary key (FPUBPARACODE,FPARAID,FCTLCODE)";
                dbl.executeSql(sqlStr);
            }
            //----------------------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003 执行调整主键的 SQL 语句出错！", e);
        }
    }

}
