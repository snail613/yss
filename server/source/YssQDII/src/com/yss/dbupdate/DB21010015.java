package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;

public class DB21010015
    extends BaseDbUpdate {
    public DB21010015() {
    }

    /**
     * MS00007
     * QDV4.1赢时胜上海2009年2月1日06_A
     * add by songjie
     * 2009.03.25
     * @param sPre String
     * @throws YssException
     */
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        boolean judge = false;
        try {
            if (existsTabColumn_DB2(pub.yssGetTableName("tb_data_integrated"),
                                    "FTsfTypeCode")
                &&
                existsTabColumn_DB2(pub.yssGetTableName("tb_data_integrated"), "FSubTsfTypeCode")) { //先检查表中是否有这个字段
                bufSql.delete(0, bufSql.length()); //清空BUF
                //获取主键名
                strPKName = getIsNullPKByTableName_DB2(pub.yssGetTableName(
                    "tb_data_integrated"));
                if (strPKName != null && strPKName.trim().length() > 0) {
                    //删除约束
                    dbl.executeSql("ALTER TABLE " +
                                   pub.yssGetTableName("tb_data_integrated") +
                                   " DROP CONSTRAINT " +
                                   strPKName);
                    //删除索引
                    deleteIndex(strPKName);
                }
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_03252009073409")) {
                    this.dropTableByTableName("TB_03252009073409");
                }

                if (existsTabColumn_Ora(pub.yssGetTableName("tb_data_integrated"), "FOperDate")) {
                    judge = true;
                }

                //----------------------源表更改为备份表-------------------------------------//
                dbl.executeSql("RENAME TABLE " +
                               pub.yssGetTableName("tb_data_integrated") +
                               " TO TB_03252009073409");
                //-----------------------创建新表------------------------------------------//
                bufSql.append(" CREATE TABLE " +
                              pub.yssGetTableName("tb_data_integrated"));
                bufSql.append(" ( ");
                bufSql.append(" FNum VARCHAR(20) NOT NULL, ");
                bufSql.append(" FSubNum VARCHAR(20) NOT NULL, ");
                bufSql.append(" FInOutType DECIMAL(1, 0) NOT NULL, ");
                bufSql.append(" FExchangeDate DATE NOT NULL, ");
                bufSql.append(" FOperDate DATE NOT NULL, ");
                bufSql.append(" FSecurityCode VARCHAR(20) NOT NULL, ");
                bufSql.append(" FRelaNum VARCHAR(20), ");
                bufSql.append(" FNumType VARCHAR(20), ");
                bufSql.append(" FTradeTypeCode VARCHAR(20) NOT NULL, ");
                bufSql.append(" FPortCode VARCHAR(20) NOT NULL, ");
                bufSql.append(" FTsfTypeCode VARCHAR(20) NOT NULL, ");
                bufSql.append(" FSubTsfTypeCode VARCHAR(20) NOT NULL, ");
                bufSql.append(" FAnalysisCode1 VARCHAR(10) NOT NULL, ");
                bufSql.append(" FAnalysisCode2 VARCHAR(20) NOT NULL, ");
                bufSql.append(" FAnalysisCode3 VARCHAR(20) NOT NULL, ");
                bufSql.append(" FAmount DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FExchangeCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FMExCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FVExCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FPortExCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FMPortExCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FVPortExCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FBaseExCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FMBaseExCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FVBaseExCost DECIMAL(18, 4) NOT NULL, ");
                bufSql.append(" FBaseCuryRate DECIMAL(18, 15) NOT NULL, ");
                bufSql.append(" FPortCuryRate DECIMAL(18, 15) NOT NULL, ");
                bufSql.append(" FSecExDesc VARCHAR(100), ");
                bufSql.append(" FDesc VARCHAR(100), ");
                bufSql.append(" FCheckState DECIMAL(1, 0) NOT NULL, ");
                bufSql.append(" FCreator VARCHAR(20) NOT NULL, ");
                bufSql.append(" FCreateTime VARCHAR(20) NOT NULL, ");
                bufSql.append(" FCheckUser VARCHAR(20), ");
                bufSql.append(" FCheckTime VARCHAR(20), ");
                bufSql.append(" CONSTRAINT PK_Tb_" + sPre + "_egrated");
                bufSql.append(" PRIMARY KEY(FNum, FSubNum) ");
                bufSql.append(" ) ");

                dbl.executeSql(bufSql.toString()); //执行语句
                bufSql.delete(0, bufSql.length()); //清空bufSql

                //--------------从备份表导入数据到新表-------------------------//
                bufSql.append(" INSERT INTO " +
                              pub.yssGetTableName("tb_data_integrated") + "(");
                bufSql.append(" FNUM, ");
                bufSql.append(" FSUBNUM, ");
                bufSql.append(" FINOUTTYPE, ");
                bufSql.append(" FEXCHANGEDATE, ");
                bufSql.append(" FOPERDATE, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FRELANUM, ");
                bufSql.append(" FNUMTYPE, ");
                bufSql.append(" FTRADETYPECODE, ");
                if (judge == true) {
                    bufSql.append(" FEXCHANGEDATE, ");
                } else {
                    bufSql.append(" FPORTCODE, ");
                }
                bufSql.append(" FTSFTYPECODE, ");
                bufSql.append(" FSUBTSFTYPECODE, ");
                bufSql.append(" FANALYSISCODE1, ");
                bufSql.append(" FANALYSISCODE2, ");
                bufSql.append(" FANALYSISCODE3, ");
                bufSql.append(" FAMOUNT, ");
                bufSql.append(" FEXCHANGECOST, ");
                bufSql.append(" FMEXCOST, ");
                bufSql.append(" FVEXCOST, ");
                bufSql.append(" FPORTEXCOST, ");
                bufSql.append(" FMPORTEXCOST, ");
                bufSql.append(" FVPORTEXCOST, ");
                bufSql.append(" FBASEEXCOST, ");
                bufSql.append(" FMBASEEXCOST, ");
                bufSql.append(" FVBASEEXCOST, ");
                bufSql.append(" FBASECURYRATE, ");
                bufSql.append(" FPORTCURYRATE, ");
                bufSql.append(" FSECEXDESC, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FNUM, ");
                bufSql.append(" FSUBNUM, ");
                bufSql.append(" FINOUTTYPE, ");
                bufSql.append(" FEXCHANGEDATE, ");
                bufSql.append(" FOPERDATE, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FRELANUM, ");
                bufSql.append(" FNUMTYPE, ");
                bufSql.append(" FTRADETYPECODE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" ' ', ");
                bufSql.append(" ' ', ");
                bufSql.append(" FANALYSISCODE1, ");
                bufSql.append(" FANALYSISCODE2, ");
                bufSql.append(" FANALYSISCODE3, ");
                bufSql.append(" FAMOUNT, ");
                bufSql.append(" FEXCHANGECOST, ");
                bufSql.append(" FMEXCOST, ");
                bufSql.append(" FVEXCOST, ");
                bufSql.append(" FPORTEXCOST, ");
                bufSql.append(" FMPORTEXCOST, ");
                bufSql.append(" FVPORTEXCOST, ");
                bufSql.append(" FBASEEXCOST, ");
                bufSql.append(" FMBASEEXCOST, ");
                bufSql.append(" FVBASEEXCOST, ");
                bufSql.append(" FBASECURYRATE, ");
                bufSql.append(" FPORTCURYRATE, ");
                bufSql.append(" FSECEXDESC, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_03252009073409");

                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                //执行生成的SQL
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0015增加表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 创建表
     * MS00007
     * QDV4.1赢时胜上海2009年2月1日06_A
     * add by songjie
     * 2009.03.25
     * @param sPre String
     * @throws YssException
     */
    public void createTable() throws YssException {
        StringBuffer bufSql = null;
        try {
            if (!dbl.yssTableExist("Tb_Base_BusinessSet")) {
                bufSql = new StringBuffer();
                bufSql.append(" CREATE TABLE Tb_Base_BusinessSet ");
                bufSql.append(" ( ");
                bufSql.append(
                    " FBusinessTypeCode    VARCHAR(20)      NOT NULL, ");
                bufSql.append(
                    " FDataFlow            VARCHAR(20)      NOT NULL, ");
                bufSql.append(
                    " FBusinessTypeName    VARCHAR(20)      NOT NULL, ");
                bufSql.append(
                    " FShow                DECIMAL(1, 0)    NOT NULL, ");
                bufSql.append(" FDesc                VARCHAR(100), ");
                bufSql.append(
                    " FCheckState          DECIMAL(1, 0)    NOT NULL, ");
                bufSql.append(
                    " FCreator             VARCHAR(20)      NOT NULL, ");
                bufSql.append(
                    " FCreateTime          VARCHAR(20)      NOT NULL, ");
                bufSql.append(" FCheckUser           VARCHAR(20), ");
                bufSql.append(" FCheckTime           VARCHAR(20), ");
                bufSql.append(" CONSTRAINT PK_Tb_BasenessSet ");
                bufSql.append(" PRIMARY KEY (FBusinessTypeCode, FDataFlow) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }
            if (!dbl.yssTableExist("Tb_Base_SubBusinessSet")) {
                bufSql = new StringBuffer();
                bufSql.append(" CREATE TABLE Tb_Base_SubBusinessSet ");
                bufSql.append(" ( ");
                bufSql.append(" FBusinessTypeCode VARCHAR(20) NOT NULL, ");
                bufSql.append(" FDataFlow VARCHAR(20) NOT NULL, ");
                bufSql.append(" FTsfTypeCode VARCHAR(20) NOT NULL, ");
                bufSql.append(" FSubTsfTypeCode VARCHAR(20) NOT NULL, ");
                bufSql.append(" FDesc VARCHAR(100), ");
                bufSql.append(" FCheckState DECIMAL(1, 0) NOT NULL, ");
                bufSql.append(" FCreator VARCHAR(20) NOT NULL, ");
                bufSql.append(" FCreateTime VARCHAR(20) NOT NULL, ");
                bufSql.append(" FCheckUser VARCHAR(20), ");
                bufSql.append(" FCheckTime VARCHAR(20), ");
                bufSql.append(" CONSTRAINT PK_Tb_BasenessSet ");
                bufSql.append(
                    " PRIMARY KEY(FBusinessTypeCode,FDataFlow,FTsfTypeCode,FSubTsfTypeCode) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }
            //创建流程相关的表 sunkey
            this.createFlowTable();
            //创建流通受限证券信息设置表 by xuqiji 20090409 MS00337    监控中也添加那个区分出流通受限证券及维护该证券锁定日期的功能 ---
            this.createLimitedSecurityTab();
            //创建流通受限证券信息设置表 by xuqiji 20090409 MS00337    监控中也添加那个区分出流通受限证券及维护该证券锁定日期的功能 ---
        } catch (Exception ex) {
            throw new YssException("更新DB2-1010015脚本失败！", ex);
        }
    }

    /***
     *创建流通受限证券信息设置表
     * add by xuqiji 20090409 MS00337    监控中也添加那个区分出流通受限证券及维护该证券锁定日期的功能
     * @throws YssException
     */
    private void createLimitedSecurityTab() throws YssException {
        StringBuffer bufSql = null;
        try {
            if (!dbl.yssTableExist("TB_Base_LimitedSecurity")) {
                bufSql = new StringBuffer();
                bufSql.append(" CREATE TABLE TB_BASE_LIMITEDSECURITY ");
                bufSql.append(" ( ");
                bufSql.append(" FSecurityCode  VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCatCode       VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCusCatCode    VARCHAR(20), ");
                bufSql.append(" FSubCatCode    VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FLockStartDate DATE         NOT NULL, ");
                bufSql.append(" FLockEndDate   DATE         NOT NULL, ");
                bufSql.append(" FDesc          VARCHAR(100), ");
                bufSql.append(" FCheckState    DECIMAL(1)   NOT NULL, ");
                bufSql.append(" FCreator       VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCreateTime    VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCheckUser     VARCHAR(20), ");
                bufSql.append(" FCheckTime     VARCHAR(20), ");
                bufSql.append(" CONSTRAINT PK_Tb_Base_Limited ");
                bufSql.append(" PRIMARY KEY (FSecurityCode) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }
        } catch (Exception e) {
            throw new YssException("创建流通受限证券信息表失败", e);
        } finally {
            bufSql = null;
        }
    }

    /**
     * 创建流程相关的表
     * add by sunkey 20090318
     * @throws YssException
     */
    private void createFlowTable() throws YssException {
        StringBuffer bufSql = null;
        try {
            //如果表TB_fun_flow不存在，则创建此表
            if (!dbl.yssTableExist("TB_FUN_FLOW")) {
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE TB_FUN_FLOW(")
                    .append("FFLOWCODE      VARCHAR(20)  NOT NULL,")
                    .append("FFLOWPOINTID   DECIMAL(3,0) NOT NULL,")
                    .append("FFLOWNAME      VARCHAR(50)  NOT NULL,")
                    .append("FFLOWPOINTNAME VARCHAR(50)  NOT NULL,")
                    .append("FMENUCODE      VARCHAR(20)  NOT NULL,")
                    .append("FISMUST        DECIMAL(1,0) NOT NULL,")
                    .append("FFLOWTYPE      DECIMAL(1,0) NOT NULL,")
                    .append("FPORTS         VARCHAR(200)     ,")
                    .append("FRELATE        VARCHAR(200)     ,")
                    .append("FDEPENDENCE    VARCHAR(200)     ,")
                    .append("FCHECKSTATE    DECIMAL(1,0) NOT NULL,")
                    .append("FCREATOR       VARCHAR(20)  NOT NULL,")
                    .append("FCREATETIME    VARCHAR(20)  NOT NULL,")
                    .append("FCHECKUSER     VARCHAR(20)      ,")
                    .append("FCHECKTIME     VARCHAR(20)      ,")
                    .append("CONSTRAINT PK_TB_FUN_FLOW ")
                    .append("PRIMARY KEY (FFLOWCODE,FFLOWPOINTID))");
                dbl.executeSql(bufSql.toString());
            }
            //创建流程状态表
            if (!dbl.yssTableExist(pub.yssGetTableName("TB_PARA_FLOW"))) {
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE ")
                    .append(pub.yssGetTableName("TB_PARA_FLOW"))
                    .append("(FFLOWCODE  VARCHAR(20)  NOT NULL,")
                    .append(" FEXECUTEID DECIMAL(3,0) NOT NULL,")
                    .append(" FDATE      DATE         NOT NULL,")
                    .append(" FPortCodes VARCHAR(2000)    ,")
                    .append(" FSTATE     DECIMAL(1,0)     ,")
                    .append(" FREMARK    VARCHAR(400)     ,")
                    .append(" CONSTRAINT PK_").append(pub.yssGetTableName("TB_FLOW"))
                    .append(" PRIMARY KEY (FFLOWCODE,FEXECUTEID,FDATE))");
                dbl.executeSql(bufSql.toString());
            }
            //设置流程操作日志表
            if (!dbl.yssTableExist("TB_SYS_FLOWOPERLOG")) {
                bufSql = new StringBuffer();
                bufSql.append("CREATE TABLE ")
                    .append("TB_SYS_FLOWOPERLOG(")
                    .append("FLOGCODE       VARCHAR(20) NOT NULL,")
                    .append("FLOGDATE       DATE        NOT NULL,")
                    .append("FLOGTIME       VARCHAR(20) NOT NULL,")
                    .append("FOPERUSER      VARCHAR(20) NOT NULL,")
                    .append("FFLOWCODE      VARCHAR(20) NOT NULL,")
                    .append("FFLOWPOINTCODE VARCHAR(20) NOT NULL,")
                    .append("FOPERCONTENT   VARCHAR(50)     ,")
                    .append("FOPERRESULT    VARCHAR(50)     ,")
                    .append("CONSTRAINT PK_TB_FLOWLOG ")
                    .append("PRIMARY KEY (FLOGCODE))");
                dbl.executeSql(bufSql.toString());
            }

        } catch (Exception ex) {
            throw new YssException("抱歉，创建流程相关表出现异常！", ex);
        }
    }

}
