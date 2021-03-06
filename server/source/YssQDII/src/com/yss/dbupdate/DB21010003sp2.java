package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.Connection;

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
public class DB21010003sp2
    extends BaseDbUpdate {
    public DB21010003sp2() {
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
//                        "_VCH_PROJECT ADD FHANDCHECK DECIMAL(1) DEFAULT 1 NOT NULL ");

            //BugNo:0000275 edit by jc
            //对表 Tb_XXX_Vch_Dict 增加字段 FSubDesc 子描述
            if (existsTabColumn_DB2("TB_" + sPre + "_VCH_DICT", "FSUBDESC")) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_VCH_DICT ADD FSUBDESC VARCHAR(100) ");
            }
            //------------------------
            //-------------更新表TB_XXX_Para_brokersubbny表 中增加字段 BUG号:0000307
            if (existsTabColumn_DB2("TB_" + sPre + "_PARA_BROKERSUBBNY",
                                    "FPLACEOFSETTLEMENT,FTRADECATCODE,FCLEARACCount")) {
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_PARA_BROKERSUBBNY");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_BROKERSUBBNY DROP CONSTRAINT " + sPKName);
                }
                if (dbl.yssTableExist("TB_07152008055522")) {
                    this.dropTableByTableName("TB_07152008055522");
                }
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_PARA_BROKERSUBBNY TO TB_07152008055522");
                bufSql.delete(0, bufSql.length());
                bTrans = false;
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_BROKERSUBBNY ");
                bufSql.append(" ( ");
                bufSql.append(" FBROKERCODE        VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FEXCHANGECODE      VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FBROKERIDTYPE      VARCHAR(20), ");
                bufSql.append(" FBROKERID          VARCHAR(20), ");
                bufSql.append(" FCLEARERIDTYPE     VARCHAR(20), ");
                bufSql.append(" FCLEARERID         VARCHAR(20), ");
                bufSql.append(" FBROKERACCOUNT     VARCHAR(50), ");
                bufSql.append(" FPLACEOFSETTLEMENT VARCHAR(50), ");
                bufSql.append(" FTRADECATCODE      VARCHAR(50), ");
                bufSql.append(" FCLEARACCOUNT      VARCHAR(50), ");
                bufSql.append(" FDESC              VARCHAR(100), ");
                bufSql.append(" FCHECKSTATE        DECIMAL(1)   NOT NULL, ");
                bufSql.append(" FCREATOR           VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME        VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER         VARCHAR(20), ");
                bufSql.append(" FCHECKTIME         VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
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
                bufSql.append(" SELECT  ");
                bufSql.append(" FBROKERCODE, ");
                bufSql.append(" FEXCHANGECODE, ");
                bufSql.append(" FBROKERIDTYPE, ");
                bufSql.append(" FBROKERID, ");
                bufSql.append(" FCLEARERIDTYPE, ");
                bufSql.append(" FCLEARERID, ");
                bufSql.append(" FBROKERACCOUNT, ");
                bufSql.append(" ' ', ");
                bufSql.append(" ' ', ");
                bufSql.append(" ' ', ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_07152008055522 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_BROKERSUBBNY ADD CONSTRAINT Pk_Tb_" + sPre +
                               "_RSUBBNY PRIMARY KEY (FBROKERCODE,FEXCHANGECODE)");
            }
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp2 增加表字段出错！", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_VCH_BOOKSET");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Vch_BOOKSet DROP CONSTRAINT " + sPKName);
            }
            if (dbl.yssTableExist("TB_07132008035822")) {
                this.dropTableByTableName("TB_07132008035822");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_Vch_BOOKSET TO TB_07132008035822");
            bufSql.append(" CREATE TABLE TB_" + sPre + "_VCH_BOOKSET");
            bufSql.append(" (");
            bufSql.append(" FBOOKSETCODE VARCHAR(20)  NOT NULL,");
            bufSql.append(" FBOOKSETNAME VARCHAR(50)  NOT NULL,");
            bufSql.append(" FCURYCODE    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDESC        VARCHAR(100),");
            bufSql.append(" FCHECKSTATE  DECIMAL(1)   NOT NULL,");
            bufSql.append(" FCREATOR     VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME  VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER   VARCHAR(20),");
            bufSql.append(" FCHECKTIME   VARCHAR(20)");
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
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_07132008035822");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_VCH_BOOKSET ADD CONSTRAINT PK_Tb_" + sPre +
                           "_Vch_Book PRIMARY KEY (FBOOKSETCODE)");
        } catch (Exception ex) {
            throw new YssException("调整DB21010003sp2出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
