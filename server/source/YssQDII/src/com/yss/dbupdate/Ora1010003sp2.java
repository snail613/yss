package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.YssException;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Ora1010003sp2
    extends BaseDbUpdate {
    public Ora1010003sp2() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
//         Ora1010003sp1中已经增加过此字段
//         //对表 Tb_XXX_Vch_Project 增加字段 FHandCheck 默认为1，代表手工选项
//         dbl.executeSql("ALTER TABLE TB_" + sPre +
//                        "_VCH_PROJECT ADD FHANDCHECK NUMBER(1) DEFAULT 1 NOT NULL ");

            //BugNo:0000275 edit by jc
            //对表 Tb_XXX_Vch_Dict 增加字段 FSubDesc 子描述
            if (existsTabColumn_Ora("TB_" + sPre + "_VCH_DICT", "FSubDesc")) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_VCH_DICT ADD FSUBDESC VARCHAR2(100) ");
            }
            //------------------------
            //-------------更新表TB_XXX_Para_brokersubbny表 中增加字段 BUG号:0000307
            if (existsTabColumn_Ora("TB_" + sPre + "_PARA_BROKERSUBBNY",
                                    "FPLACEOFSETTLEMENT,FTRADECATCODE,FCLEARACCount")) {
                sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_PARA_BROKERSUBBNY");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_BROKERSUBBNY DROP CONSTRAINT " + sPKName + " CASCADE");
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_PAR_07152008054602000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_PAR_07152008054602000");
                }
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_BROKERSUBBNY RENAME TO TB_" + sPre +
                               "_PAR_07152008054602000");
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_BROKERSUBBNY ");
                bufSql.append(" ( ");
                bufSql.append(" FBROKERCODE        VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FEXCHANGECODE      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FBROKERIDTYPE      VARCHAR2(20)      NULL, ");
                bufSql.append(" FBROKERID          VARCHAR2(20)      NULL, ");
                bufSql.append(" FCLEARERIDTYPE     VARCHAR2(20)      NULL, ");
                bufSql.append(" FCLEARERID         VARCHAR2(20)      NULL, ");
                bufSql.append(" FBROKERACCOUNT     VARCHAR2(50)      NULL, ");
                bufSql.append(" FPLACEOFSETTLEMENT VARCHAR2(50)      NULL, ");
                bufSql.append(" FTRADECATCODE      VARCHAR2(50)      NULL, ");
                bufSql.append(" FCLEARACCOUNT      VARCHAR2(50)      NULL, ");
                bufSql.append(" FDESC              VARCHAR2(100)     NULL, ");
                bufSql.append(" FCHECKSTATE        NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR           VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME        VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER         VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME         VARCHAR2(20)      NULL ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.append(" INSERT INTO TB_" + sPre + "_PARA_BROKERSUBBNY( ");
                bufSql.append(" FBROKERCODE, ");
                bufSql.append(" FEXCHANGECODE, ");
                bufSql.append(" FBROKERIDTYPE, ");
                bufSql.append(" FBROKERID, ");
                bufSql.append(" FCLEARERIDTYPE, ");
                bufSql.append(" FCLEARERID, ");
                bufSql.append(" FBROKERACCOUNT, ");
                bufSql.append(" FPLACEOFSETTLEMENT, ");
                bufSql.append(" FTRADECATCODE, ");
                bufSql.append(" FCLEARACCOUNT, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FBROKERCODE, ");
                bufSql.append(" FEXCHANGECODE, ");
                bufSql.append(" FBROKERIDTYPE, ");
                bufSql.append(" FBROKERID, ");
                bufSql.append(" FCLEARERIDTYPE, ");
                bufSql.append(" FCLEARERID, ");
                bufSql.append(" FBROKERACCOUNT, ");
                bufSql.append(" NULL, ");
                bufSql.append(" NULL, ");
                bufSql.append(" NULL, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_" + sPre + "_PAR_07152008054602000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_BROKERSUBBNY ADD CONSTRAINT PK_TB_" + sPre +
                               "_PARA_BROKERSUBBNY PRIMARY KEY (FBROKERCODE,FEXCHANGECODE)");
            }
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp2 增加表字段出错！", ex);
        } finally {
            bufSql = null; //回收,以清除内存
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_VCH_BOOKSET");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Vch_BOOKSet DROP CONSTRAINT " + sPKName + " CASCADE");
                deleteIndex(sPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_07132008035137000")) {
                this.dropTableByTableName("TB_" + sPre + "_VCH_07132008035137000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Vch_BOOKSET RENAME TO TB_" + sPre +
                           "_VCH_07132008035137000");
            bufSql.append(" CREATE TABLE TB_" + sPre + "_VCH_BOOKSET");
            bufSql.append(" (");
            bufSql.append(" FBOOKSETCODE VARCHAR(20)  NOT NULL,");
            bufSql.append(" FBOOKSETNAME VARCHAR(50)  NOT NULL,");
            bufSql.append(" FCURYCODE    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDESC        VARCHAR(100)     NULL,");
            bufSql.append(" FCHECKSTATE  NUMBER(1)    NOT NULL,");
            bufSql.append(" FCREATOR     VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME  VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER   VARCHAR(20)      NULL,");
            bufSql.append(" FCHECKTIME   VARCHAR(20)      NULL ");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            bufSql.delete(0, bufSql.length());
            bufSql.append(" INSERT INTO TB_" + sPre + "_VCH_BOOKSET(");
            bufSql.append(" FBOOKSETCODE,");
            bufSql.append(" FBOOKSETNAME,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FBOOKSETCODE,");
            bufSql.append(" FBOOKSETNAME,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" TO_CHAR(FDESC),");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_VCH_07132008035137000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_VCH_BOOKSET ADD CONSTRAINT PK_Tb_" + sPre +
                           "_Vch_BookSet PRIMARY KEY (FBOOKSETCODE)");
        } catch (Exception ex) {
            throw new YssException("调整ORacle1010003sp2出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
