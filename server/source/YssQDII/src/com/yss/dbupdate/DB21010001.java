package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;
import com.yss.util.YssException;

public class DB21010001
    extends BaseDbUpdate {
    public DB21010001() {
    }

    /**
     * 创建通用数据表
     * @throws YssException
     */
    public void createTable() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strPKName = "";
        try {
            //----------修改--TB_FUN_COMMONPARAMSSUB，TB_FUN_COMMONPARAMS 表字段 创建表Tb_Fun_CommonParamsAttr----------//
            strPKName = this.getIsNullPKByTableName_DB2("TB_FUN_COMMONPARAMS");
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_FUN_COMMONPARAMS DROP CONSTRAINT " + strPKName);
            }
            strPKName = "";

            if (dbl.yssTableExist("TB_12182007095544")) {
                this.dropTableByTableName("TB_12182007095544");
            }
            dbl.executeSql("RENAME TABLE TB_FUN_COMMONPARAMS TO TB_12182007095544");

            bufSql.append("CREATE TABLE Tb_Fun_CommonParams ");
            bufSql.append("(");
            bufSql.append("    FCPTYPECODE VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCONDTYPE   VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCONDCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCPTypeName VARCHAR(50)  NOT NULL,");
            bufSql.append("    FDESC       VARCHAR(100),");
            bufSql.append("    FCheckState DECIMAL(1)   NOT NULL,");
            bufSql.append("    FCreator    VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCreateTime VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCheckUser  VARCHAR(20),");
            bufSql.append("    FCheckTime  VARCHAR(20)");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            strPKName = this.getIsNullPKByTableName_DB2("TB_FUN_COMMONPARAMSSUB");
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_FUN_COMMONPARAMSSUB DROP CONSTRAINT " + strPKName);
            }
            strPKName = "";
            if (dbl.yssTableExist("TB_12182007095547")) {
                this.dropTableByTableName("TB_12182007095547");
            }
            dbl.executeSql("RENAME TABLE TB_FUN_COMMONPARAMSSUB TO TB_12182007095547");

            bufSql.append("CREATE TABLE Tb_Fun_CommonParamsSub ");
            bufSql.append("(");
            bufSql.append("    FCPTypeCode VARCHAR(20)   NOT NULL,");
            bufSql.append("    FPARAMCODE  VARCHAR(20)   NOT NULL,");
            bufSql.append("    FParamValue VARCHAR(1000) NOT NULL,");
            bufSql.append("    FCheckState DECIMAL(1)    NOT NULL,");
            bufSql.append("    FCreator    VARCHAR(20)   NOT NULL,");
            bufSql.append("    FCreateTime VARCHAR(20)   NOT NULL,");
            bufSql.append("    FCheckUser  VARCHAR(20),");
            bufSql.append("    FCheckTime  VARCHAR(20),");
            bufSql.append("    FDESC       VARCHAR(100)");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("CREATE TABLE Tb_Fun_CommonParamsAttr ");
            bufSql.append("(");
            bufSql.append("    FCPAttrCode VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCPAttrName VARCHAR(50)  NOT NULL,");
            bufSql.append("    FAttrSrc    VARCHAR(20)  NOT NULL,");
            bufSql.append("    FAttrCfg    VARCHAR(200),");
            bufSql.append("    FCheckState DECIMAL(1)   NOT NULL,");
            bufSql.append("    FDesc       VARCHAR(100) NOT NULL,");
            bufSql.append("    FCreator    VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCreateTime VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCheckUser  VARCHAR(20),");
            bufSql.append("    FCheckTime  VARCHAR(20),");
            bufSql.append("    CONSTRAINT PK_Tb_Fun_amsAttr");
            bufSql.append("    PRIMARY KEY (FCPAttrCode,FCPAttrName)");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_Fun_CommonParams( ");
            bufSql.append("                                         FCPTYPECODE,");
            bufSql.append("                                         FCONDTYPE,");
            bufSql.append("                                         FCONDCODE,");
            bufSql.append("                                         FCPTypeName,");
            bufSql.append("                                         FDESC,");
            bufSql.append("                                         FCheckState,");
            bufSql.append("                                         FCreator,");
            bufSql.append("                                         FCreateTime");
            bufSql.append("                                        )");
            bufSql.append("                                  SELECT");
            bufSql.append("                                         FCPTYPECODE,");
            bufSql.append("                                         FCONDTYPE,");
            bufSql.append("                                         FCONDCODE,");
            bufSql.append("                                         FCPTYPENAME,");
            bufSql.append("                                         FDESC,");
            bufSql.append("                                         0,");
            bufSql.append("                                         ' ',");
            bufSql.append("                                         ' '");
            bufSql.append("                                    FROM TB_12182007095544");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_Fun_CommonParamsSub(");
            bufSql.append("                                            FCPTypeCode,");
            bufSql.append("                                            FPARAMCODE,");
            bufSql.append("                                            FParamValue,");
            bufSql.append("                                            FCheckState,");
            bufSql.append("                                            FCreator,");
            bufSql.append("                                            FCreateTime,");
            bufSql.append("                                            FDESC");
            bufSql.append("                                           )");
            bufSql.append("                                     SELECT");
            bufSql.append("                                            ' ',");
            bufSql.append("                                            FPARAMCODE,");
            bufSql.append("                                            FPARAMVALUE,");
            bufSql.append("                                            0,");
            bufSql.append("                                            ' ',");
            bufSql.append("                                            ' ',");
            bufSql.append("                                            FDESC");
            bufSql.append("                                       FROM TB_12182007095547");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParams ADD CONSTRAINT PK_Tb_Fun_nParams " +
                           "PRIMARY KEY (FCPTYPECODE,FCONDTYPE,FCONDCODE)");
            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParamsSub ADD CONSTRAINT PK_Tb_Fun_ramsSub " +
                           "PRIMARY KEY (FCPTypeCode,FPARAMCODE)");
            //-------------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0001 创建通用数据表出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 根据组合代码创建表
     * @param sPre String: 组合代码
     * @throws YssException
     */
    public void createTable(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        //--------------------------创建表Tb_XXX_Data_TradeDetailA---------------------------//
        try {
            bufSql.append("CREATE TABLE " + pub.yssGetTableName("Tb_Data_TradeDetailA"));
            bufSql.append(" (");
            bufSql.append("    FPortCode       VARCHAR(20)   NOT NULL,");
            bufSql.append("    FSecurityCode   VARCHAR(20)   NOT NULL,");
            bufSql.append("    FSrcSecCode     VARCHAR(20)   NOT NULL,");
            bufSql.append("    FTradeOrder     VARCHAR(20)   NOT NULL,");
            bufSql.append("    FExchangeCode   VARCHAR(20)   NOT NULL,");
            bufSql.append("    FStockholderNum VARCHAR(20)   NOT NULL,");
            bufSql.append("    FAnalysisCode2  VARCHAR(20)   NOT NULL,");
            bufSql.append("    FTradeTypeCode  VARCHAR(20)   NOT NULL,");
            bufSql.append("    FTradeAmoumt    DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FTradePrice     DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FTradeMoney     DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FTradeDate      DATE          NOT NULL,");
            bufSql.append("    FSettleDate     DATE          NOT NULL,");
            bufSql.append("    FYhsCode        VARCHAR(20)   NOT NULL,");
            bufSql.append("    FYhs            DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FJsfcode        VARCHAR(20)   NOT NULL,");
            bufSql.append("    FJsf            DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FGhfCode        VARCHAR(20)   NOT NULL,");
            bufSql.append("    FGhf            DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FZgfCode        VARCHAR(20)   NOT NULL,");
            bufSql.append("    FZgf            DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FQtfCode        VARCHAR(20)   NOT NULL,");
            bufSql.append("    FQtf            DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FYjCode         VARCHAR(20)   NOT NULL,");
            bufSql.append("    FYj             DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FFxj            DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FBondIns        DECIMAL(18,4) NOT NULL,");
            bufSql.append("    FHggain         DECIMAL(18,4) NOT NULL");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0001 创建组合群数据表出错", e);
        }
        //----------------------------------------------------------------------------------//
    }

    public void addTableField(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strPKName = "";
        try {
            //--------------------------修改表Tb_XXX_para_Receiver---------------------------//
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("Tb_PARA_RECEIVER"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("Tb_PARA_RECEIVER") + " DROP CONSTRAINT " + strPKName);
            }

            if (dbl.yssTableExist("TB_01032008094610")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_01032008094610");
            }

            dbl.executeSql("RENAME TABLE " + pub.yssGetTableName("TB_PARA_RECEIVER") + " TO TB_01032008094610");

            bufSql.append("CREATE TABLE " + pub.yssGetTableName("Tb_Para_Receiver"));
            bufSql.append(" (");
            bufSql.append("    FRECEIVERCODE      VARCHAR(20)  NOT NULL,");
            bufSql.append("    FRECEIVERNAME      VARCHAR(50)  NOT NULL,");
            bufSql.append("    FRECEIVERSHORTNAME VARCHAR(50),");
            bufSql.append("    FOFFICEADDR        VARCHAR(200),");
            bufSql.append("    FPOSTALCODE        VARCHAR(20),");
            bufSql.append("    FOPERBANK          VARCHAR(100) NOT NULL,");
            bufSql.append("    FACCOUNTNUMBER     VARCHAR(100) NOT NULL,");
            bufSql.append("    FCuryCode          VARCHAR(20),");
            bufSql.append("    FDESC              VARCHAR(200),");
            bufSql.append("    FCHECKSTATE        DECIMAL(1)   NOT NULL,");
            bufSql.append("    FCREATOR           VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCREATETIME        VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER         VARCHAR(20),");
            bufSql.append("    FCHECKTIME         VARCHAR(20)");
            bufSql.append(")");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO " + pub.yssGetTableName("Tb_Para_Receiver") + "(");
            bufSql.append("                                      FRECEIVERCODE,");
            bufSql.append("                                      FRECEIVERNAME,");
            bufSql.append("                                      FRECEIVERSHORTNAME,");
            bufSql.append("                                      FOFFICEADDR,");
            bufSql.append("                                      FPOSTALCODE,");
            bufSql.append("                                      FOPERBANK,");
            bufSql.append("                                      FACCOUNTNUMBER,");
            bufSql.append("                                      FDESC,");
            bufSql.append("                                      FCHECKSTATE,");
            bufSql.append("                                      FCREATOR,");
            bufSql.append("                                      FCREATETIME,");
            bufSql.append("                                      FCHECKUSER,");
            bufSql.append("                                      FCHECKTIME");
            bufSql.append("                                     )");
            bufSql.append("                               SELECT");
            bufSql.append("                                      FRECEIVERCODE,");
            bufSql.append("                                      FRECEIVERNAME,");
            bufSql.append("                                      FRECEIVERSHORTNAME,");
            bufSql.append("                                      FOFFICEADDR,");
            bufSql.append("                                      FPOSTALCODE,");
            bufSql.append("                                      FOPERBANK,");
            bufSql.append("                                      FACCOUNTNUMBER,");
            bufSql.append("                                      FDESC,");
            bufSql.append("                                      FCHECKSTATE,");
            bufSql.append("                                      FCREATOR,");
            bufSql.append("                                      FCREATETIME,");
            bufSql.append("                                      FCHECKUSER,");
            bufSql.append("                                      FCHECKTIME");
            bufSql.append("                                 FROM TB_01032008094610");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("Tb_Para_Receiver") + " ADD CONSTRAINT PK_Tb_Paraeceiver " +
                           "PRIMARY KEY (FRECEIVERCODE)");
            //------------------------------------------------------------------------------//

            //----------------------修改表 Tb_XXX_Cash_Command-------------------------------//
            strPKName = "";
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("TB_CASH_COMMAND"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_CASH_COMMAND") +
                               " DROP CONSTRAINT PK_TB_" + sPre + "_COMMAND");
            }

            if (dbl.yssTableExist("TB_01032008094928")) {
                this.dropTableByTableName("TB_01032008094928");
            }
            dbl.executeSql("RENAME TABLE " + pub.yssGetTableName("TB_CASH_COMMAND") + " TO TB_01032008094928");

            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_CASH_COMMAND"));
            bufSql.append(" (");
            bufSql.append("    FNUM          VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCOMMANDDATE  DATE           NOT NULL,");
            bufSql.append("    FCOMMANDTIME  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FACCOUNTDATE  DATE           NOT NULL,");
            bufSql.append("    FACCOUNTTIME  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FORDER        DECIMAL(8)     NOT NULL,");
            bufSql.append("    FPayerName    VARCHAR(50)    NOT NULL,");
            bufSql.append("    FPayerBank    VARCHAR(100)   NOT NULL,");
            bufSql.append("    FPayerAccount VARCHAR(100)   NOT NULL,");
            bufSql.append("    FPayCury      VARCHAR(20)    NOT NULL,");
            bufSql.append("    FPayMoney     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FRefRate      DECIMAL(20,15),");
            bufSql.append("    FRecerName    VARCHAR(50)    NOT NULL,");
            bufSql.append("    FRecerBank    VARCHAR(100)   NOT NULL,");
            bufSql.append("    FRecerAccount VARCHAR(100)   NOT NULL,");
            bufSql.append("    FRecCury VARCHAR(20)   NOT NULL,"); //  LZP  增加收款币种
            bufSql.append("    FRecMoney     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FCASHUSAGE    VARCHAR(100),");
            bufSql.append("    FDESC         VARCHAR(100),");
            bufSql.append("    FCHECKSTATE   DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR      VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCREATETIME   VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCHECKUSER    VARCHAR(20),");
            bufSql.append("    FCHECKTIME    VARCHAR(20)");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO " + pub.yssGetTableName("TB_CASH_COMMAND") + "(");
            bufSql.append("                                     FNUM,");
            bufSql.append("                                     FCOMMANDDATE,");
            bufSql.append("                                     FCOMMANDTIME,");
            bufSql.append("                                     FACCOUNTDATE,");
            bufSql.append("                                     FACCOUNTTIME,");
            bufSql.append("                                     FORDER,");
            bufSql.append("                                     FPayerName,");
            bufSql.append("                                     FPayerBank,");
            bufSql.append("                                     FPayerAccount,");
            bufSql.append("                                     FPayCury,");
            bufSql.append("                                     FPayMoney,");
            bufSql.append("                                     FRecerName,");
            bufSql.append("                                     FRecerBank,");
            bufSql.append("                                     FRecerAccount,");
            bufSql.append("                                     FRecCury,"); //  LZP  增加收款币种
            bufSql.append("                                     FRecMoney,");
            bufSql.append("                                     FCASHUSAGE,");
            bufSql.append("                                     FDESC,");
            bufSql.append("                                     FCHECKSTATE,");
            bufSql.append("                                     FCREATOR,");
            bufSql.append("                                     FCREATETIME,");
            bufSql.append("                                     FCHECKUSER,");
            bufSql.append("                                     FCHECKTIME");
            bufSql.append("                                     )");
            bufSql.append("                              SELECT");
            bufSql.append("                                     FNUM,");
            bufSql.append("                                     FCOMMANDDATE,");
            bufSql.append("                                     FCOMMANDTIME,");
            bufSql.append("                                     FACCOUNTDATE,");
            bufSql.append("                                     FACCOUNTTIME,");
            bufSql.append("                                     FORDER,");
            bufSql.append("                                     ' ',");
            bufSql.append("                                     ' ',");
            bufSql.append("                                     ' ',");
            bufSql.append("                                     ' ',");
            bufSql.append("                                     0,");
            bufSql.append("                                     ' ',");
            bufSql.append("                                     ' ',");
            bufSql.append("                                     ' ',");
            bufSql.append("                                     ' ',");
            bufSql.append("                                     0,");
            bufSql.append("                                     FCASHUSAGE,");
            bufSql.append("                                     FDESC,");
            bufSql.append("                                     FCHECKSTATE,");
            bufSql.append("                                     FCREATOR,");
            bufSql.append("                                     FCREATETIME,");
            bufSql.append("                                     FCHECKUSER,");
            bufSql.append("                                     FCHECKTIME");
            bufSql.append("                                FROM TB_01032008094928");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_CASH_COMMAND") +
                           " ADD CONSTRAINT PK_Tb_" + sPre + "_COMMAND" +
                           " PRIMARY KEY (FNUM)");
            //------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0001 修改组合群表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void adjustFieldName(String sPre) throws YssException {
        String strPKName = "";
        try {
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("TB_PARA_SECURITY"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_PARA_SECURITY") +
                               " DROP CONSTRAINT " + strPKName);
            }
            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_PARA_SECURITY") +
                           " ADD CONSTRAINT PK_Tb_" + sPre + "_ecurity" +
                           " PRIMARY KEY (FSECURITYCODE)");
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0001 修改数据表 " + pub.yssGetTableName("TB_PARA_SECURITY") + " 主键出错！", e);
        }
    }
}
