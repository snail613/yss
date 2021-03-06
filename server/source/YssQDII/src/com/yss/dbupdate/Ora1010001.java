package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;
import com.yss.util.YssException;

public class Ora1010001
    extends BaseDbUpdate {
    public Ora1010001() {
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
            strPKName = this.getIsNullPKByTableName_Ora("TB_FUN_COMMONPARAMSSUB");
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_FUN_COMMONPARAMSSUB DROP CONSTRAINT " + strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_FUN_COM_12172007050823000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_FUN_COM_12172007050823000");
            }
            dbl.executeSql("ALTER TABLE TB_FUN_COMMONPARAMSSUB RENAME TO TB_FUN_COM_12172007050823000");

            bufSql.append("CREATE TABLE Tb_Fun_CommonParamsSub ");
            bufSql.append("(");
            bufSql.append("    FCPTypeCode VARCHAR2(20)   NOT NULL,");
            bufSql.append("    FPARAMCODE  VARCHAR2(20)   NOT NULL,");
            bufSql.append("    FParamValue VARCHAR2(1000) NOT NULL,");
            bufSql.append("    FCheckState NUMBER(1)      NOT NULL,");
            bufSql.append("    FCreator    VARCHAR2(20)   NOT NULL,");
            bufSql.append("    FCreateTime VARCHAR2(20)   NOT NULL,");
            bufSql.append("    FCheckUser  VARCHAR2(20)       NULL,");
            bufSql.append("    FCheckTime  VARCHAR2(20)       NULL,");
            bufSql.append("    FDESC       VARCHAR2(100)      NULL");
            bufSql.append(")");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_Fun_CommonParamsSub(");
            bufSql.append("                                         FCPTypeCode,");
            bufSql.append("                                         FPARAMCODE,");
            bufSql.append("                                         FParamValue,");
            bufSql.append("                                         FCheckState,");
            bufSql.append("                                         FCreator,");
            bufSql.append("                                         FCreateTime,");
            bufSql.append("                                         FCheckUser,");
            bufSql.append("                                         FCheckTime,");
            bufSql.append("                                         FDESC");
            bufSql.append("                                        )");
            bufSql.append("                                  SELECT");
            bufSql.append("                                         ' ',");
            bufSql.append("                                         FPARAMCODE,");
            bufSql.append("                                         FPARAMVALUE,");
            bufSql.append("                                         0,");
            bufSql.append("                                         ' ',");
            bufSql.append("                                         ' ',");
            bufSql.append("                                         null,");
            bufSql.append("                                         null,");
            bufSql.append("                                         FDESC");
            bufSql.append("                                    FROM TB_FUN_COM_12172007050823000");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            bufSql.delete(0, bufSql.length());

            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParamsSub ADD CONSTRAINT PK_Tb_Fun_CommonParamsSub " +
                           "PRIMARY KEY (FCPTypeCode,FPARAMCODE) ");
            strPKName = this.getIsNullPKByTableName_Ora("TB_FUN_COMMONPARAMS");
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_FUN_COMMONPARAMS DROP CONSTRAINT " + strPKName + " CASCADE");
            }
            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParams ADD FCheckState NUMBER(1) NOT NULL");
            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParams ADD FCreator VARCHAR2(20) NOT NULL");
            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParams ADD FCreateTime VARCHAR2(20) NOT NULL");
            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParams ADD FCheckUser VARCHAR2(20)     NULL");
            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParams ADD FCheckTime VARCHAR2(20)     NULL");
            dbl.executeSql("ALTER TABLE Tb_Fun_CommonParams ADD CONSTRAINT PK_Tb_Fun_CommonParams " +
                           "PRIMARY KEY (FCPTYPECODE,FCONDTYPE,FCONDCODE)");

            bufSql.append("CREATE TABLE Tb_Fun_CommonParamsAttr ");
            bufSql.append("(");
            bufSql.append("    FCPAttrCode VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCPAttrName VARCHAR2(50)  NOT NULL,");
            bufSql.append("    FAttrSrc    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FAttrCfg    VARCHAR2(200)     NULL,");
            bufSql.append("    FCheckState NUMBER(1)     NOT NULL,");
            bufSql.append("    FDesc       VARCHAR2(100) NOT NULL,");
            bufSql.append("    FCreator    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCreateTime VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCheckUser  VARCHAR2(20)      NULL,");
            bufSql.append("    FCheckTime  VARCHAR2(20)      NULL,");
            bufSql.append("    CONSTRAINT PK_Tb_Fun_CommonParamsAttr");
            bufSql.append("    PRIMARY KEY (FCPAttrCode,FCPAttrName)");
            bufSql.append(")");

            dbl.executeSql(bufSql.toString());
            //-------------------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0001 创建通用表结构出错！", e);
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
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_TradeDetailA ");
            bufSql.append("(");
            bufSql.append("    FPortCode       VARCHAR2(20) NOT NULL,");
            bufSql.append("    FSecurityCode   VARCHAR2(20) NOT NULL,");
            bufSql.append("    FSrcSecCode     VARCHAR2(20) NOT NULL,");
            bufSql.append("    FTradeOrder     VARCHAR2(20) NOT NULL,");
            bufSql.append("    FExchangeCode   VARCHAR2(20) NOT NULL,");
            bufSql.append("    FStockholderNum VARCHAR2(20) NOT NULL,");
            bufSql.append("    FAnalysisCode2  VARCHAR2(20) NOT NULL,");
            bufSql.append("    FTradeTypeCode  VARCHAR2(20) NOT NULL,");
            bufSql.append("    FTradeAmoumt    NUMBER(18,4) NOT NULL,");
            bufSql.append("    FTradePrice     NUMBER(18,4) NOT NULL,");
            bufSql.append("    FTradeMoney     NUMBER(18,4) NOT NULL,");
            bufSql.append("    FTradeDate      DATE         NOT NULL,");
            bufSql.append("    FSettleDate     DATE         NOT NULL,");
            bufSql.append("    FYhsCode        VARCHAR2(20) NOT NULL,");
            bufSql.append("    FYhs            NUMBER(18,4) NOT NULL,");
            bufSql.append("    FJsfcode        VARCHAR2(20) NOT NULL,");
            bufSql.append("    FJsf            NUMBER(18,4) NOT NULL,");
            bufSql.append("    FGhfCode        VARCHAR2(20) NOT NULL,");
            bufSql.append("    FGhf            NUMBER(18,4) NOT NULL,");
            bufSql.append("    FZgfCode        VARCHAR2(20) NOT NULL,");
            bufSql.append("    FZgf            NUMBER(18,4) NOT NULL,");
            bufSql.append("    FQtfCode        VARCHAR2(20) NOT NULL,");
            bufSql.append("    FQtf            NUMBER(18,4) NOT NULL,");
            bufSql.append("    FYjCode         VARCHAR2(20) NOT NULL,");
            bufSql.append("    FYj             NUMBER(18,4) NOT NULL,");
            bufSql.append("    FFxj            NUMBER(18,4) NOT NULL,");
            bufSql.append("    FBondIns        NUMBER(18,4) NOT NULL,");
            bufSql.append("    FHggain         NUMBER(18,4) NOT NULL");
            bufSql.append(")");

            dbl.executeSql(bufSql.toString());
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0001 创建组合群表出错！", e);
        }
        //---------------------------------------------------------------------------------------------//
    }

    public void addTableField(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strPKName = "";
        try {
            //--------------------------修改表Tb_XXX_para_Receiver---------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName("Tb_PARA_RECEIVER"));
            if (strPKName.trim().length() != 0) {
                bufSql.append(" ALTER TABLE " + pub.yssGetTableName("TB_PARA_RECEIVER") + " DROP CONSTRAINT " +
                              strPKName + " CASCADE");
                dbl.executeSql(bufSql.toString());
            }

            bufSql.delete(0, bufSql.length());
            if (dbl.yssTableExist("TB_" + sPre + "_PAR_01032007090201000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_PAR_01032007090201000");
            }
            bufSql.append("  ALTER TABLE " + pub.yssGetTableName("TB_PARA_RECEIVER") + " RENAME TO TB_" + sPre + "_PAR_01032007090201000");
            dbl.executeSql(bufSql.toString());

            bufSql.delete(0, bufSql.length());
            bufSql.append("  CREATE TABLE Tb_" + sPre + "_Para_Receiver (");
            bufSql.append("    FRECEIVERCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FRECEIVERNAME      VARCHAR2(50)  NOT NULL,");
            bufSql.append("    FRECEIVERSHORTNAME VARCHAR2(50)      NULL,");
            bufSql.append("    FOFFICEADDR        VARCHAR2(200)     NULL,");
            bufSql.append("    FPOSTALCODE        VARCHAR2(20)      NULL,");
            bufSql.append("    FOPERBANK          VARCHAR2(100) NOT NULL,");
            bufSql.append("    FACCOUNTNUMBER     VARCHAR2(100) NOT NULL,");
            bufSql.append("    FCuryCode          VARCHAR2(20)      NULL,");
            bufSql.append("    FDESC              VARCHAR2(200)     NULL,");
            bufSql.append("    FCHECKSTATE        NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR           VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME        VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER         VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME         VARCHAR2(20)      NULL)");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Para_Receiver(");
            bufSql.append("                                       FRECEIVERCODE,");
            bufSql.append("                                       FRECEIVERNAME,");
            bufSql.append("                                       FRECEIVERSHORTNAME,");
            bufSql.append("                                       FOFFICEADDR,");
            bufSql.append("                                       FPOSTALCODE,");
            bufSql.append("                                       FOPERBANK,");
            bufSql.append("                                       FACCOUNTNUMBER,");
            bufSql.append("                                       FCuryCode,");
            bufSql.append("                                       FDESC,");
            bufSql.append("                                       FCHECKSTATE,");
            bufSql.append("                                       FCREATOR,");
            bufSql.append("                                       FCREATETIME,");
            bufSql.append("                                       FCHECKUSER,");
            bufSql.append("                                       FCHECKTIME)");
            bufSql.append("                                SELECT ");
            bufSql.append("                                       FRECEIVERCODE,");
            bufSql.append("                                       FRECEIVERNAME,");
            bufSql.append("                                       FRECEIVERSHORTNAME,");
            bufSql.append("                                       FOFFICEADDR,");
            bufSql.append("                                       FPOSTALCODE,");
            bufSql.append("                                       FOPERBANK,");
            bufSql.append("                                       FACCOUNTNUMBER,");
            bufSql.append("                                       null,");
            bufSql.append("                                       FDESC,");
            bufSql.append("                                       FCHECKSTATE,");
            bufSql.append("                                       FCREATOR,");
            bufSql.append("                                       FCREATETIME,");
            bufSql.append("                                       FCHECKUSER,");
            bufSql.append("                                       FCHECKTIME");
            bufSql.append("                                  FROM TB_" + sPre + "_PAR_01032007090201000");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append(" ALTER TABLE Tb_" + sPre + "_Para_Receiver ADD CONSTRAINT PK_TB_" + sPre + "_PARA_RECEIVER ");
            bufSql.append(" PRIMARY KEY (FRECEIVERCODE)");
            dbl.executeSql(bufSql.toString());
            //--------------------------修改表Tb_XXX_Cash_COMMAND---------------------------//
            bufSql.delete(0, bufSql.length());
            strPKName = "";
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName("Tb_Cash_COMMAND"));
            if (strPKName.trim().length() != 0) {
                bufSql.append(" ALTER TABLE " + pub.yssGetTableName("Tb_Cash_COMMAND") +
                              " DROP CONSTRAINT PK_TB_" + sPre + "_CASH_COMMAND CASCADE");
                dbl.executeSql(bufSql.toString());
            }

            bufSql.delete(0, bufSql.length());
            if (dbl.yssTableExist("TB_" + sPre + "_CAS_01032007095547000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_CAS_01032007095547000");
            }
            bufSql.append("ALTER TABLE TB_" + sPre + "_CASH_COMMAND RENAME TO TB_" + sPre + "_CAS_01032007095547000");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Cash_Command (");
            bufSql.append("    FNUM          VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCOMMANDDATE  DATE          NOT NULL,");
            bufSql.append("    FCOMMANDTIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FACCOUNTDATE  DATE          NOT NULL,");
            bufSql.append("    FACCOUNTTIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FORDER        NUMBER(8)     NOT NULL,");
            bufSql.append("    FPayerName    VARCHAR2(50)  NOT NULL,");
            bufSql.append("    FPayerBank    VARCHAR2(100) NOT NULL,");
            bufSql.append("    FPayerAccount VARCHAR2(100) NOT NULL,");
            bufSql.append("    FPayCury      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FRecCury      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FPayMoney     NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FRefRate      NUMBER(20,15)     NULL,");
            bufSql.append("    FRecerName    VARCHAR2(50)  NOT NULL,");
            bufSql.append("    FRecerBank    VARCHAR2(100) NOT NULL,");
            bufSql.append("    FRecerAccount VARCHAR2(100) NOT NULL,");
            bufSql.append("    FRecMoney     NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FCASHUSAGE    VARCHAR2(100)     NULL,");
            bufSql.append("    FDESC         VARCHAR2(100)     NULL,");
            bufSql.append("    FCHECKSTATE   NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER    VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME    VARCHAR2(20)      NULL )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Cash_Command(");
            bufSql.append("                                      FNUM,");
            bufSql.append("                                      FCOMMANDDATE,");
            bufSql.append("                                      FCOMMANDTIME,");
            bufSql.append("                                      FACCOUNTDATE,");
            bufSql.append("                                      FACCOUNTTIME,");
            bufSql.append("                                      FORDER,");
            bufSql.append("                                      FPayerName,");
            bufSql.append("                                      FPayerBank,");
            bufSql.append("                                      FPayerAccount,");
            bufSql.append("                                      FPayCury,");
            bufSql.append("                                      FRecCury,");
            bufSql.append("                                      FPayMoney,");
            bufSql.append("                                      FRefRate,");
            bufSql.append("                                      FRecerName,");
            bufSql.append("                                      FRecerBank,");
            bufSql.append("                                      FRecerAccount,");
            bufSql.append("                                      FRecMoney,");
            bufSql.append("                                      FCASHUSAGE,");
            bufSql.append("                                      FDESC,");
            bufSql.append("                                      FCHECKSTATE,");
            bufSql.append("                                      FCREATOR,");
            bufSql.append("                                      FCREATETIME,");
            bufSql.append("                                      FCHECKUSER,");
            bufSql.append("                                      FCHECKTIME )");
            bufSql.append("                               SELECT ");
            bufSql.append("                                      FNUM,");
            bufSql.append("                                      FCOMMANDDATE,");
            bufSql.append("                                      FCOMMANDTIME,");
            bufSql.append("                                      FACCOUNTDATE,");
            bufSql.append("                                      FACCOUNTTIME,");
            bufSql.append("                                      FORDER,");
            bufSql.append("                                      ' ',");
            bufSql.append("                                      ' ',");
            bufSql.append("                                      ' ',");
            bufSql.append("                                      ' ',");
            bufSql.append("                                      ' ',");
            bufSql.append("                                      0,");
            bufSql.append("                                      null,");
            bufSql.append("                                      ' ',");
            bufSql.append("                                      ' ',");
            bufSql.append("                                      ' ',");
            bufSql.append("                                      0,");
            bufSql.append("                                      FCASHUSAGE,");
            bufSql.append("                                      FDESC,");
            bufSql.append("                                      FCHECKSTATE,");
            bufSql.append("                                      FCREATOR,");
            bufSql.append("                                      FCREATETIME,");
            bufSql.append("                                      FCHECKUSER,");
            bufSql.append("                                      FCHECKTIME");
            bufSql.append("                                 FROM TB_" + sPre + "_CAS_01032007095547000");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append(" ALTER TABLE Tb_" + sPre + "_Cash_Command ADD CONSTRAINT PK_TB_" + sPre + "_CASH_COMMAND PRIMARY KEY (FNUM)");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0001 修改表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void adjustFieldName(String sPre) throws YssException {
        String strPKName = "";
        try {
            //---------------------2008-01-06 TB_XXX_Para_Security 修改主键字段-----------------------//
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName("TB_PARA_SECURITY"));

            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_PARA_SECURITY") + " DROP CONSTRAINT " +
                               strPKName + " CASCADE");
            }
            dbl.executeSql("ALTER TABLE " + pub.yssGetTableName("TB_PARA_SECURITY") + " ADD CONSTRAINT " +
                           "PK_Tb_" + sPre + "_Para_Security PRIMARY KEY (FSECURITYCODE)");
            //--------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0001 修改数据表 TB_XXX_PARA_SECURITY 主键出错！", e);
        }
    }

}
