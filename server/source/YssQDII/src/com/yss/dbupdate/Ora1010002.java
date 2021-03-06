package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;
import com.yss.util.YssException;
import java.util.Hashtable;

public class Ora1010002
    extends BaseDbUpdate {
    public Ora1010002() {
    }

    //增加表字段
    public void addTableField() throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------TB_FUN_COMMONPARAMSSUB 添加字段 FValueDesc-------------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            if ( (strPKName = this.getIsNullPKByTableName_Ora("TB_FUN_COMMONPARAMSSUB")).trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_FUN_COMMONPARAMSSUB DROP CONSTRAINT " +
                    strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_FUN_COM_01142008034624000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_FUN_COM_01142008034624000");
            }
            dbl.executeSql(
                "ALTER TABLE TB_FUN_COMMONPARAMSSUB RENAME TO TB_FUN_COM_01142008034624000"); //将原表更名为临时表

            bufSql.append("CREATE TABLE TB_FUN_COMMONPARAMSSUB "); //重新建表
            bufSql.append("( ");

            bufSql.append("FCPTYPECODE VARCHAR2(20)   NOT NULL, ");
            bufSql.append("FPARAMCODE  VARCHAR2(20)   NOT NULL, ");
            bufSql.append("FPARAMVALUE VARCHAR2(1000) NOT NULL, ");
            bufSql.append("FValueDesc  VARCHAR2(1000)     NULL, ");
            bufSql.append("FCHECKSTATE NUMBER(1)      NOT NULL, ");
            bufSql.append("FCREATOR    VARCHAR2(20)   NOT NULL, ");
            bufSql.append("FCREATETIME VARCHAR2(20)   NOT NULL, ");
            bufSql.append("FCHECKUSER  VARCHAR2(20)       NULL, ");
            bufSql.append("FCHECKTIME  VARCHAR2(20)       NULL, ");
            bufSql.append("FDESC       VARCHAR2(100)      NULL ");

            bufSql.append(") ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO TB_FUN_COMMONPARAMSSUB( "); //将临时表中的数据插入新表
            bufSql.append("FCPTYPECODE, ");
            bufSql.append("FPARAMCODE, ");
            bufSql.append("FPARAMVALUE, ");
            bufSql.append("FValueDesc, ");
            bufSql.append("FCHECKSTATE, ");
            bufSql.append("FCREATOR, ");
            bufSql.append("FCREATETIME, ");
            bufSql.append("FCHECKUSER, ");
            bufSql.append("FCHECKTIME, ");
            bufSql.append("FDESC ");
            bufSql.append(") ");
            bufSql.append("SELECT ");
            bufSql.append("FCPTYPECODE, ");
            bufSql.append("FPARAMCODE, ");
            bufSql.append("FPARAMVALUE, ");
            bufSql.append("null, ");
            bufSql.append("FCHECKSTATE, ");
            bufSql.append("FCREATOR, ");
            bufSql.append("FCREATETIME, ");
            bufSql.append("FCHECKUSER, ");
            bufSql.append("FCHECKTIME, ");
            bufSql.append("FDESC ");

            bufSql.append("FROM TB_FUN_COM_01142008034624000 ");
            dbl.executeSql(bufSql.toString());

            conn.commit();
            bufSql.delete(0, bufSql.length());

            dbl.executeSql(
                "ALTER TABLE TB_FUN_COMMONPARAMSSUB ADD CONSTRAINT PK_TB_FUN_COMMONPARAMSSUB " +
                "PRIMARY KEY (FCPTYPECODE,FPARAMCODE) "); //添加主键

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //----------------------------修改字段大小 TB_FUN_SYSDATA FCODE，FNAME -------------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;

            dbl.executeSql(
                "ALTER TABLE TB_FUN_SYSDATA MODIFY(FCODE  VARCHAR2(50))"); //将FCODE大小变大
            dbl.executeSql(
                "ALTER TABLE TB_FUN_SYSDATA MODIFY(FNAME  VARCHAR2(100))"); //将FNAME大小变大

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------------------------------------------//

        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0002 通用数据表新增字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 为组合群表增加字段
     * @param sPre String: 组合群编号
     * @throws YssException
     */
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //------------------------- CurrencyWay 添加字段 FFactor --------------------------//
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_PARA_CURRENCYWAY")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_PARA_CURRENCYWAY") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
            }

            strPKName = "";
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_PARA_CURRENCYWAY") +
                           " RENAME TO TB_" + sPre + "_PAR_12242007055744000");

            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_PARA_CURRENCYWAY"));
            bufSql.append(" (");
            bufSql.append("    FCURYCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FPORTCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FMARKCURY   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FQUOTEWAY   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FFactor     NUMBER(8)     NOT NULL,");
            bufSql.append("    FDESC       VARCHAR2(200)     NULL,");
            bufSql.append("    FCHECKSTATE NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER  VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME  VARCHAR2(20)      NULL");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_PARA_CURRENCYWAY") + "(");
            bufSql.append("                                          FCURYCODE,");
            bufSql.append("                                          FPORTCODE,");
            bufSql.append("                                          FMARKCURY,");
            bufSql.append("                                          FQUOTEWAY,");
            bufSql.append("                                          FFactor,");
            bufSql.append("                                          FDESC,");
            bufSql.append("                                          FCHECKSTATE,");
            bufSql.append("                                          FCREATOR,");
            bufSql.append("                                          FCREATETIME,");
            bufSql.append("                                          FCHECKUSER,");
            bufSql.append("                                          FCHECKTIME");
            bufSql.append("                                         )");
            bufSql.append("                                   SELECT");
            bufSql.append("                                          FCURYCODE,");
            bufSql.append("                                          FPORTCODE,");
            bufSql.append("                                          FMARKCURY,");
            bufSql.append("                                          FQUOTEWAY,");
            bufSql.append("                                          0,");
            bufSql.append("                                          FDESC,");
            bufSql.append("                                          FCHECKSTATE,");
            bufSql.append("                                          FCREATOR,");
            bufSql.append("                                          FCREATETIME,");
            bufSql.append("                                          FCHECKUSER,");
            bufSql.append("                                          FCHECKTIME");
            bufSql.append("                                     FROM TB_" + sPre +
                          "_PAR_12242007055744000");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            bufSql.delete(0, bufSql.length());

            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_PARA_CURRENCYWAY") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_PARA_CURRENCYWAY" +
                           " PRIMARY KEY (FCURYCODE,FPORTCODE,FMARKCURY)");
            //--------------------------------------------------------------------------------//

            //-------------------------EXCHANGERATE 添加字段 基准货币-FMarkCury 暂时屏蔽--------------------------//
            /*strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                  "TB_DATA_EXCHANGERATE"));
                      if (strPKName != "") {
               dbl.executeSql("ALTER TABLE " +
                              pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                              " DROP CONSTRAINT " + strPKName + " CASCADE");
                      }
                      strPKName="";
                      dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                           " RENAME TO TB_" + sPre + "_DAT_12242007015004000");

                      bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_DATA_EXCHANGERATE"));
                      bufSql.append(" (");
                      bufSql.append("    FEXRATESRCCODE VARCHAR2(20)  NOT NULL,");
                      bufSql.append("    FCURYCODE      VARCHAR2(20)  NOT NULL,");
             bufSql.append("    FMarkCury      VARCHAR2(20)  DEFAULT ' ' NOT NULL,");
                      bufSql.append("    FEXRATEDATE    DATE          NOT NULL,");
                      bufSql.append("    FEXRATETIME    VARCHAR2(20)  NOT NULL,");
                      bufSql.append("    FPORTCODE      VARCHAR2(20)  NOT NULL,");
                      bufSql.append("    FEXRATE1       NUMBER(20,15)     NULL,");
                      bufSql.append("    FEXRATE2       NUMBER(20,15)     NULL,");
                      bufSql.append("    FEXRATE3       NUMBER(20,15)     NULL,");
                      bufSql.append("    FEXRATE4       NUMBER(20,15)     NULL,");
                      bufSql.append("    FEXRATE5       NUMBER(20,15)     NULL,");
                      bufSql.append("    FEXRATE6       NUMBER(20,15)     NULL,");
                      bufSql.append("    FEXRATE7       NUMBER(20,15)     NULL,");
                      bufSql.append("    FEXRATE8       NUMBER(20,15)     NULL,");
                      bufSql.append("    FDESC          VARCHAR2(100)     NULL,");
                      bufSql.append("    FDATASOURCE    NUMBER(1)     NOT NULL,");
                      bufSql.append("    FCHECKSTATE    NUMBER(1)     NOT NULL,");
                      bufSql.append("    FCREATOR       VARCHAR2(20)  NOT NULL,");
                      bufSql.append("    FCREATETIME    VARCHAR2(20)  NOT NULL,");
                      bufSql.append("    FCHECKUSER     VARCHAR2(20)      NULL,");
                      bufSql.append("    FCHECKTIME     VARCHAR2(20)      NULL");
                      bufSql.append(" )");

                      dbl.executeSql(bufSql.toString());
                      bufSql.delete(0, bufSql.length());

                      bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_DATA_EXCHANGERATE") + "(");
                      bufSql.append(
                  "                                           FEXRATESRCCODE,");
             bufSql.append("                                           FCURYCODE,");
             bufSql.append("                                           FMarkCury,");
                      bufSql.append(
                  "                                           FEXRATEDATE,");
                      bufSql.append(
                  "                                           FEXRATETIME,");
             bufSql.append("                                           FPORTCODE,");
             bufSql.append("                                           FEXRATE1,");
             bufSql.append("                                           FEXRATE2,");
             bufSql.append("                                           FEXRATE3,");
             bufSql.append("                                           FEXRATE4,");
             bufSql.append("                                           FEXRATE5,");
             bufSql.append("                                           FEXRATE6,");
             bufSql.append("                                           FEXRATE7,");
             bufSql.append("                                           FEXRATE8,");
             bufSql.append("                                           FDESC,");
                      bufSql.append(
                  "                                           FDATASOURCE,");
                      bufSql.append(
                  "                                           FCHECKSTATE,");
             bufSql.append("                                           FCREATOR,");
                      bufSql.append(
                  "                                           FCREATETIME,");
             bufSql.append("                                           FCHECKUSER,");
             bufSql.append("                                           FCHECKTIME");
             bufSql.append("                                          )");
                      bufSql.append("                                    SELECT");
                      bufSql.append(
                  "                                           FEXRATESRCCODE,");
             bufSql.append("                                           FCURYCODE,");
             bufSql.append("                                           ' ',");
                      bufSql.append(
                  "                                           FEXRATEDATE,");
                      bufSql.append(
                  "                                           FEXRATETIME,");
             bufSql.append("                                           FPORTCODE,");
             bufSql.append("                                           FEXRATE1,");
             bufSql.append("                                           FEXRATE2,");
             bufSql.append("                                           FEXRATE3,");
             bufSql.append("                                           FEXRATE4,");
             bufSql.append("                                           FEXRATE5,");
             bufSql.append("                                           FEXRATE6,");
             bufSql.append("                                           FEXRATE7,");
             bufSql.append("                                           FEXRATE8,");
             bufSql.append("                                           FDESC,");
                      bufSql.append(
                  "                                           FDATASOURCE,");
                      bufSql.append(
                  "                                           FCHECKSTATE,");
             bufSql.append("                                           FCREATOR,");
                      bufSql.append(
                  "                                           FCREATETIME,");
             bufSql.append("                                           FCHECKUSER,");
             bufSql.append("                                           FCHECKTIME");
             bufSql.append("                                      FROM TB_" + sPre +
                          "_DAT_12242007015004000");

                      conn.setAutoCommit(false);
                      bTrans = true;
                      dbl.executeSql(bufSql.toString());
                      conn.commit();
                      bTrans = false;
                      conn.setAutoCommit(true);

                      dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
             " ADD CONSTRAINT PK_Tb_" + sPre + "_Data_ExchangeRate" +
                           " PRIMARY KEY (FEXRATESRCCODE,FCURYCODE,FMarkCury,FEXRATEDATE,FEXRATETIME,FPORTCODE)");*/
            //-----------------------------------------------------------------------------------------//
            //---------------------------为表Tb_XXX_Data_SecRecPay 添加字段 FInOut -----------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            if ( (strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_DATA_SECRECPAY")).trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre + "_DATA_SECRECPAY DROP CONSTRAINT " +
                    strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_02132008050955000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_02132008050955000");
            }
            strPKName = "";
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_SECRECPAY RENAME TO TB_"
                           + sPre + "_DAT_02132008050955000"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_SecRecPay "); //建表
            bufSql.append("(");
            bufSql.append("FNUM            VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRANSDATE      DATE          NOT NULL,");
            bufSql.append("FPORTCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSECURITYCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTSFTYPECODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSUBTSFTYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCATTYPE        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FATTRCLSCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCURYCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FInOut          NUMBER(1)     DEFAULT 1 NOT NULL,");
            bufSql.append("FMONEY          NUMBER(18,4)  NOT NULL,");
            bufSql.append("FMMONEY         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FVMONEY         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FBASECURYRATE   NUMBER(20,15) NOT NULL,");
            bufSql.append("FBASECURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FMBASECURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FVBASECURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPORTCURYRATE   NUMBER(20,15) NOT NULL,");
            bufSql.append("FPORTCURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FMPORTCURYMONEY NUMBER(18,4)  NOT NULL,");
            bufSql.append("FVPORTCURYMONEY NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FDATASOURCE     NUMBER(1)     NOT NULL,");
            bufSql.append("FSTOCKIND       NUMBER(1)     NOT NULL,");
            bufSql.append("FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME      VARCHAR2(20)      NULL");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Data_SecRecPay(");
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
            bufSql.append(" FROM TB_" + sPre + "_DAT_02132008050955000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql(" ALTER TABLE Tb_" + sPre +
                           "_Data_SecRecPay ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_SECRECPAY PRIMARY KEY (FNUM)");
            //------------------------向Tb_XXX_Data_CashPayRec 中新增字段 FInOut ------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            if ( (strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_DATA_CASHPAYREC")).trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre +
                    "_DATA_CASHPAYREC DROP CONSTRAINT " + strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_02132008050956000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_02132008050956000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_CASHPAYREC RENAME TO TB_"
                           + sPre + "_DAT_02132008050956000"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_CashPayRec ");
            bufSql.append("(");
            bufSql.append("FNUM            VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRANSDATE      DATE          NOT NULL,");
            bufSql.append("FPORTCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCASHACCCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTSFTYPECODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSUBTSFTYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCURYCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FInOut          NUMBER(1)     DEFAULT 1 NOT NULL,");
            bufSql.append("FMONEY          NUMBER(18,4)  NOT NULL,");
            bufSql.append("FBASECURYRATE   NUMBER(20,15) NOT NULL,");
            bufSql.append("FBASECURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPORTCURYRATE   NUMBER(20,15) NOT NULL,");
            bufSql.append("FPORTCURYMONEY  NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FDATASOURCE     NUMBER(1)     NOT NULL,");
            bufSql.append("FSTOCKIND       NUMBER(1)     NOT NULL,");
            bufSql.append("FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME      VARCHAR2(20)      NULL ");
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
            bufSql.append(" FCHECKTIME");
            bufSql.append("  FROM TB_" + sPre + "_DAT_02132008050956000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Data_CashPayRec ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_CASHPAYREC PRIMARY KEY (FNUM)");
            //----------------------Tb_XXX_Para_Receiver 增加 FPortCode,FCashAccCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 字段------------------
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            if ( (strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Para_Receiver")).trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre + "_Para_Receiver DROP CONSTRAINT " +
                    strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_" + sPre + "_PAR_02192008031145000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_PAR_02192008031145000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Para_Receiver RENAME TO TB_"
                           + sPre + "_PAR_02192008031145000"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Para_Receiver");
            bufSql.append("(");
            bufSql.append("FRECEIVERCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FRECEIVERNAME      VARCHAR2(50)  NOT NULL,");
            bufSql.append("FRECEIVERSHORTNAME VARCHAR2(50)      NULL,");
            bufSql.append("FOFFICEADDR        VARCHAR2(200)     NULL,");
            bufSql.append("FPOSTALCODE        VARCHAR2(20)      NULL,");
            bufSql.append("FOPERBANK          VARCHAR2(100) NOT NULL,");
            bufSql.append("FACCOUNTNUMBER     VARCHAR2(100) NOT NULL,");
            bufSql.append("FCURYCODE          VARCHAR2(20)      NULL,");
            bufSql.append("FPortCode          VARCHAR2(20)      NULL,");
            bufSql.append("FCashAccCode       VARCHAR2(10)      NULL,");
            bufSql.append("FAnalysisCode1     VARCHAR2(20)      NULL,");
            bufSql.append("FAnalysisCode2     VARCHAR2(20)      NULL,");
            bufSql.append("FAnalysisCode3     VARCHAR2(20)      NULL,");
            bufSql.append("FDESC              VARCHAR2(200)     NULL,");
            bufSql.append("FCHECKSTATE        NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR           VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER         VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME         VARCHAR2(20)      NULL");
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
            bufSql.append(")  SELECT ");
            bufSql.append(" FRECEIVERCODE,");
            bufSql.append(" FRECEIVERNAME,");
            bufSql.append(" FRECEIVERSHORTNAME,");
            bufSql.append(" FOFFICEADDR,");
            bufSql.append(" FPOSTALCODE,");
            bufSql.append(" FOPERBANK,");
            bufSql.append(" FACCOUNTNUMBER,");
            bufSql.append(" FCURYCODE,");
            bufSql.append("' ',");
            bufSql.append("' ',");
            bufSql.append("' ',");
            bufSql.append("' ',");
            bufSql.append("' ',");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_PAR_02192008031145000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Para_Receiver ADD CONSTRAINT PK_TB_" + sPre +
                           "_PARA_RECEIVER" +
                           " PRIMARY KEY (FRECEIVERCODE)");
            //-------------------------Tb_XXX_Cash_Command 增加 FRelaNum,FNumType 字段-------------------------
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            if ( (strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Cash_Command")).trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre + "_Cash_Command DROP CONSTRAINT " +
                    strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_" + sPre + "_CAS_02202008080249000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_CAS_02202008080249000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Cash_Command RENAME TO TB_"
                           + sPre + "_CAS_02202008080249000"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Cash_Command");
            bufSql.append("(");
            bufSql.append("FNUM          VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCOMMANDDATE  DATE          NOT NULL,");
            bufSql.append("FCOMMANDTIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FACCOUNTDATE  DATE          NOT NULL,");
            bufSql.append("FACCOUNTTIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FORDER        NUMBER(8)     NOT NULL,");
            bufSql.append("FPayerName    VARCHAR2(200) NOT NULL,");
            bufSql.append("FPAYERBANK    VARCHAR2(100) NOT NULL,");
            bufSql.append("FPAYERACCOUNT VARCHAR2(100) NOT NULL,");
            bufSql.append("FPAYCURY      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FPAYMONEY     NUMBER(18,4)  NOT NULL,");
            bufSql.append("FREFRATE      NUMBER(20,15)     NULL,");
            bufSql.append("FRecerName    VARCHAR2(200) NOT NULL,");
            bufSql.append("FRECERBANK    VARCHAR2(100) NOT NULL,");
            bufSql.append("FRECERACCOUNT VARCHAR2(100) NOT NULL,");
            bufSql.append("FRECCURY      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FRECMONEY     NUMBER(18,4)  NOT NULL,");
            bufSql.append("FCASHUSAGE    VARCHAR2(100)     NULL,");
            bufSql.append("FRelaNum      VARCHAR2(20)      NULL,");
            bufSql.append("FNumType      VARCHAR2(20)      NULL,");
            bufSql.append("FDESC         VARCHAR2(100)     NULL,");
            bufSql.append("FCHECKSTATE   NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER    VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME    VARCHAR2(20)      NULL");
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
            bufSql.append(" FCASHUSAGE,");
            bufSql.append(" FRelaNum,");
            bufSql.append(" FNumType,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME,");
            bufSql.append(" FRecCury");
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
            bufSql.append(" FCASHUSAGE,");
            bufSql.append(" ' ',");
            bufSql.append(" ' ',");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME,");
            bufSql.append(" FRecCury");
            bufSql.append(" FROM TB_" + sPre + "_CAS_02202008080249000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Cash_Command ADD CONSTRAINT PK_TB_" + sPre +
                           "_CASH_COMMAND " +
                           " PRIMARY KEY (FNUM)");
            //--------------TB_XXX_DATA_RATETRADE 增加 FReceiverCode,FPayCode字段 ------------------------------------
            conn.setAutoCommit(false);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            if ( (strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_DATA_RATETRADE")).trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre + "_DATA_RATETRADE DROP CONSTRAINT " +
                    strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_02202008080952000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_02202008080952000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_RATETRADE RENAME TO TB_"
                           + sPre + "_DAT_02202008080952000"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_RateTrade ");
            bufSql.append("(");
            bufSql.append("FNUM            VARCHAR2(15)  NOT NULL,");
            bufSql.append("FPORTCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FBANALYSISCODE1 VARCHAR2(20)  NOT NULL,");
            bufSql.append("FBANALYSISCODE2 VARCHAR2(20)  NOT NULL,");
            bufSql.append("FBANALYSISCODE3 VARCHAR2(20)  NOT NULL,");
            bufSql.append("FBPORTCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRADETYPE      NUMBER(1)     NOT NULL,");
            bufSql.append("FCATTYPE        NUMBER(1)     NOT NULL,");
            bufSql.append("FBCASHACCCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FBCURYCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSCURYCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSCASHACCCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FReceiverCode   VARCHAR2(20)      NULL,");
            bufSql.append("FPayCode        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEDATE      DATE          NOT NULL,");
            bufSql.append("FTRADETIME      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSETTLEDATE     DATE          NOT NULL,");
            bufSql.append("FSETTLETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FEXCURYRATE     NUMBER(20,15) NOT NULL,");
            bufSql.append("FBSETTLEDATE    DATE          NOT NULL,");
            bufSql.append("FBSETTLETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FBMONEY         NUMBER(18,4)  NOT NULL,");
            bufSql.append("FSMONEY         NUMBER(18,4)  NOT NULL,");
            bufSql.append("FBCURYFEE       NUMBER(18,4)      NULL,");
            bufSql.append("FSCURYFEE       NUMBER(18,4)      NULL,");
            bufSql.append("FBASEMONEY      NUMBER(18,4)  NOT NULL,");
            bufSql.append("FPORTMONEY      NUMBER(18,4)  NOT NULL,");
            bufSql.append("FLONGCURYRATE   NUMBER(18,12)     NULL,");
            bufSql.append("FRATEFX         NUMBER(18,4)      NULL,");
            bufSql.append("FUPDOWN         NUMBER(18,4)      NULL,");
            bufSql.append("FDESC           VARCHAR2(100)     NULL,");
            bufSql.append("FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME      VARCHAR2(20)      NULL");
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
            bufSql.append(" FROM TB_" + sPre + "_DAT_02202008080952000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Data_RateTrade ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_RATETRADE " +
                           " PRIMARY KEY (FNUM)");
            //----------------------------------

        } catch (Exception e) {
            throw new YssException("版本1.0.1.0002 新增表字段出错！", e);
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
            bufSql.append("FPortCode        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FKeyCode         VARCHAR2(20)  NOT NULL,");
            bufSql.append("FReTypeCode      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FOrderCode       VARCHAR2(200) NOT NULL,");
            bufSql.append("FDetail          NUMBER(1)     NOT NULL,");
            bufSql.append("FINVMGRCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FKeyName         VARCHAR2(200)  NOT NULL,");
            bufSql.append("FCuryCode        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FPrice           NUMBER(20,12)     NULL,");
            bufSql.append("FOTPrice1        NUMBER(20,12)     NULL,");
            bufSql.append("FOTPrice2        NUMBER(20,12)     NULL,");
            bufSql.append("FOTPrice3        NUMBER(20,12)     NULL,");
            bufSql.append("FSEDOLCode       VARCHAR2(20)      NULL,");
            bufSql.append("FISINCode        VARCHAR2(20)      NULL,");
            bufSql.append("FSParAmt         NUMBER(20,4)     NULL,");
            bufSql.append("FBaseCuryRate    NUMBER(20,15) NOT NULL,");
            bufSql.append("FPortCuryRate    NUMBER(20,15) NOT NULL,");
            bufSql.append("FMarketValue     NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPortMarketValue NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FCost            NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPortCost        NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FMVValue         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPortMVValue     NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FFXValue         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FGradeType1      VARCHAR2(20)      NULL,");
            bufSql.append("FGradeType2      VARCHAR2(20)      NULL,");
            bufSql.append("FGradeType3      VARCHAR2(20)      NULL,");
            bufSql.append("FGradeType4      VARCHAR2(20)      NULL,");
            bufSql.append("FGradeType5      VARCHAR2(20)      NULL,");
            bufSql.append("FGradeType6      VARCHAR2(20)      NULL,");
            bufSql.append("CONSTRAINT PK_Tb_" + sPre + "_Data_NavData PRIMARY KEY (FNAVDATE,FPORTCODE,FORDERCODE,FReTypeCode,FINVMGRCODE,FKEYCODE)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //--------------------------创建表Tb_XXX_Book_Invest ---------------------------//
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Book_Invest ");
            bufSql.append("(");
            bufSql.append("FIBookCode  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FIBookName  VARCHAR2(50)  NOT NULL,");
            bufSql.append("FBookLink   VARCHAR2(100) NOT NULL,");
            bufSql.append("FDesc       VARCHAR2(100)     NULL,");
            bufSql.append("FCheckState NUMBER(1)     NOT NULL,");
            bufSql.append("FCreator    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCreateTime VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCheckUser  VARCHAR2(20)      NULL,");
            bufSql.append("FCheckTime  VARCHAR2(20)      NULL,");
            bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                          "_Book_Invest PRIMARY KEY (FIBookCode)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            //---------------------------创建表Tb_XXX_Data_Integrated by ly -----------------------------//
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Data_Integrated ");
            bufSql.append("(");
            bufSql.append("FNum            VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSubNum         VARCHAR2(20)  NOT NULL,");
            bufSql.append("FInOutType      NUMBER(1)     NOT NULL,");
            bufSql.append("FExchangeDate   DATE          NOT NULL,");
            bufSql.append("FSecurityCode   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FRelaNum        VARCHAR2(20)      NULL,");
            bufSql.append("FNumType        VARCHAR2(20)      NULL,");
            bufSql.append("FTradeTypeCode  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FPortCode       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FAnalysisCode1  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FAnalysisCode2  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FAnalysisCode3  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FAmount         NUMBER(18,4)  NOT NULL,");
            bufSql.append("FExchangeCost   NUMBER(18,4)  NOT NULL,");
            bufSql.append("FMExCost        NUMBER(18,4)  NOT NULL,");
            bufSql.append("FVExCost        NUMBER(18,4)  NOT NULL,");
            bufSql.append("FPortExCost     NUMBER(18,4)  NOT NULL,");
            bufSql.append("FMPortExCost    NUMBER(18,4)  NOT NULL,");
            bufSql.append("FVPortExCost    NUMBER(18,4)  NOT NULL,");
            bufSql.append("FBaseExCost     NUMBER(18,4)  NOT NULL,");
            bufSql.append("FMBaseExCost    NUMBER(18,4)  NOT NULL,");
            bufSql.append("FVBaseExCost    NUMBER(18,4)  NOT NULL,");
            bufSql.append("FBaseCuryRate   NUMBER(18,15) NOT NULL,");
            bufSql.append("FPortCuryRate   NUMBER(18,15) NOT NULL,");
            bufSql.append("FSecExDesc      VARCHAR2(100)     NULL,");
            bufSql.append("FDesc           VARCHAR2(100)     NULL,");
            bufSql.append("FCheckState     NUMBER(1)     NOT NULL,");
            bufSql.append("FCreator        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCreateTime     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCheckUser      VARCHAR2(20)      NULL,");
            bufSql.append("FCheckTime      VARCHAR2(20)      NULL,");
            bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                          "_Data_Integrated PRIMARY KEY (FNum,FSubNum) ");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0002 创建表出错！", e);
        }
        //---------------------------------------------------------------------------------------------//
    }

    /**
     * 执行系统数据表中的 SQL 语句
     * @throws YssException
     */
    public void executeSysDataSql() throws YssException {
        boolean bTrans = false; //代表是否开始事务
        Connection conn = dbl.loadConnection();
        java.util.Hashtable fields = null;
        java.util.Hashtable values = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //-----------------------------------------------------------------------------------------------------------------------
            fields = new Hashtable();
            fields.put("FSICode", "String");
            values = new Hashtable();
            values.put("FSICode", "BondToCash");
            if (!chValueEx("TB_FUN_SPINGINVOKE", fields, values, false)) { //判断是否存在相应的记录，不存在时插入 sj edit 20080221
                /**D20071223000001 Common null 新增-Spring调用 BondToCash 债券派息 2007-12-23 11:52:11**/
                dbl.executeSql("insert into TB_FUN_SPINGINVOKE  (FSICode,FSIName,FBeanId,FParams,FReturnType,FFORMCODE,FCTLNAME,FModuleCode,FDesc ) " +
                               "values('BondToCash','债券派息','BondToCash','','S','OtherOperDeal','','data','')");
                conn.commit();
            }
            //-----------------------------------------------------------------------------------------------------------------------
            fields = new Hashtable();
            fields.put("FSICode", "String");
            values = new Hashtable();
            values.put("FSICode", "Dividend");
            if (!chValueEx("TB_FUN_SPINGINVOKE", fields, values, false)) { //判断是否存在相应的记录，不存在时插入 sj edit 20080221
                /**D20071223000002 Common null 新增-Spring调用 Dividend 股票分红 2007-12-23 12:03:54**/
                dbl.executeSql("insert into TB_FUN_SPINGINVOKE  (FSICode,FSIName,FBeanId,FParams,FReturnType,FFORMCODE,FCTLNAME,FModuleCode,FDesc ) " +
                               "values('Dividend','股票分红','Dividend','','S','OtherOperDeal','','data','')");
                conn.commit();
            }
            //-----------------------------------------------------------------------------------------------------------------------
            fields = new Hashtable();
            fields.put("FSICode", "String");
            values = new Hashtable();
            values.put("FSICode", "RightsIssue");
            if (!chValueEx("TB_FUN_SPINGINVOKE", fields, values, false)) { //判断是否存在相应的记录，不存在时插入 sj edit 20080221
                /**D20071223000003 Common null 新增-Spring调用 RightsIssue 配股权益 2007-12-23 12:04:38**/
                dbl.executeSql("insert into TB_FUN_SPINGINVOKE  (FSICode,FSIName,FBeanId,FParams,FReturnType,FFORMCODE,FCTLNAME,FModuleCode,FDesc ) " +
                               "values('RightsIssue','配股权益','RightsIssue','','S','OtherOperDeal','','data','')");
                conn.commit();
            }

            //-----------------------------------------------------------------------------------------------------------------------
            fields = new Hashtable();
            fields.put("FSICode", "String");
            values = new Hashtable();
            values.put("FSICode", "BonusShare");
            if (!chValueEx("TB_FUN_SPINGINVOKE", fields, values, false)) { //判断是否存在相应的记录，不存在时插入 sj edit 20080221
                /**D20071223000004 Common null 新增-Spring调用 BonusShare 送股权益 2007-12-23 12:04:57**/
                dbl.executeSql("insert into TB_FUN_SPINGINVOKE  (FSICode,FSIName,FBeanId,FParams,FReturnType,FFORMCODE,FCTLNAME,FModuleCode,FDesc ) " +
                               "values('BonusShare','送股权益','BonusShare','','S','OtherOperDeal','','data','')");
                conn.commit();
            }

            //-----------------------------------------------------------------------------------------------------------------------
            fields = new Hashtable();
            fields.put("FSICode", "String");
            values = new Hashtable();
            values.put("FSICode", "calc001");
            if (!chValueEx("TB_FUN_SPINGINVOKE", fields, values, false)) { //判断是否存在相应的记录，不存在时插入 sj edit 20080221
                /**D20071223000001 Common null 新增-Spring调用 calc001 债券计息设置 2007-12-23 17:16:37**/
                dbl.executeSql("insert into TB_FUN_SPINGINVOKE  (FSICode,FSIName,FBeanId,FParams,FReturnType,FFORMCODE,FCTLNAME,FModuleCode,FDesc ) " +
                               "values('calc001','债券计息设置','BondInsCfgFormula','','U','calcinsmetic','txtSPICode','base','')");
                conn.commit();
            }

            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0002 执行系统数据 SQL 语句出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 调整表中数据
     * 创建日期：2008-02-17
     * 创建人：蒋锦
     * @param sPre String
     * @throws YssException
     */
    public void adjustTableData(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        Connection conn = dbl.loadConnection();
        try {
            //---------------2008.02.17--蒋锦--删除应收应付表中多余数据---------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql("DELETE " +
                           pub.yssGetTableName("Tb_Stock_SecRecPay") +
                           " WHERE FTsfTypeCode = '99' AND FSubTsfTypeCode LIKE '9906DV'");
            dbl.executeSql("DELETE " +
                           pub.yssGetTableName("Tb_Data_SecRecPay") +
                           " WHERE FTsfTypeCode = '99' AND FSubTsfTypeCode LIKE '9906DV'");
            dbl.executeSql("DELETE " +
                           pub.yssGetTableName("Tb_Stock_SecRecPay") +
                           " WHERE FTsfTypeCode = '99' AND FSubTsfTypeCode LIKE '9906EQ'");
            dbl.executeSql("DELETE " +
                           pub.yssGetTableName("Tb_Data_SecRecPay") +
                           " WHERE FTsfTypeCode = '99' AND FSubTsfTypeCode LIKE '9906EQ'");
            dbl.executeSql("DELETE " +
                           pub.yssGetTableName("Tb_Stock_SecRecPay") +
                           " WHERE FTsfTypeCode = '06' AND FSubTsfTypeCode LIKE '06DV'");
            dbl.executeSql("DELETE " +
                           pub.yssGetTableName("Tb_Data_SecRecPay") +
                           " WHERE FTsfTypeCode = '06' AND FSubTsfTypeCode LIKE '06DV'");
            dbl.executeSql("DELETE " +
                           pub.yssGetTableName("Tb_Stock_SecRecPay") +
                           " WHERE FTsfTypeCode = '06' AND FSubTsfTypeCode LIKE '06EQ'");
            dbl.executeSql("DELETE " +
                           pub.yssGetTableName("Tb_Data_SecRecPay") +
                           " WHERE FTsfTypeCode = '06' AND FSubTsfTypeCode LIKE '06EQ'");
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0002 执行数据调整 SQL 语句出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
