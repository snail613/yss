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
public class Ora1010003sp4
    extends BaseDbUpdate {
    public Ora1010003sp4() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //BugNo:0000363 edit by jc
            //对表 Tb_XXX_Para_BrokerSubBny 增加字段 FIFBrokerName,FClearerName,FClearerDesc
            if (existsTabColumn_Ora("TB_" + sPre + "_PARA_BROKERSUBBNY",
                                    "FIFBrokerName,FClearerName,FClearerDesc")) {
                sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_PARA_BROKERSUBBNY");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_BROKERSUBBNY DROP CONSTRAINT " + sPKName + " CASCADE");
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_PAR_08052008061540000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_PAR_08052008061540000");
                }
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_BROKERSUBBNY RENAME TO TB_" + sPre +
                               "_PAR_08052008061540000");
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_BROKERSUBBNY ");
                bufSql.append(" (");
                bufSql.append(" FBROKERCODE        VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FEXCHANGECODE      VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FBROKERIDTYPE      VARCHAR2(20)     NULL, ");
                bufSql.append(" FBROKERID          VARCHAR2(20)     NULL, ");
                bufSql.append(" FIFBROKERNAME      VARCHAR2(200)    NULL, ");
                bufSql.append(" FCLEARERIDTYPE     VARCHAR2(20)     NULL, ");
                bufSql.append(" FCLEARERID         VARCHAR2(20)     NULL, ");
                bufSql.append(" FCLEARERNAME       VARCHAR2(200)    NULL, ");
                bufSql.append(" FCLEARERDESC       VARCHAR2(100)    NULL, ");
                bufSql.append(" FBROKERACCOUNT     VARCHAR2(50)     NULL, ");
                bufSql.append(" FPLACEOFSETTLEMENT VARCHAR2(50)     NULL, ");
                bufSql.append(" FTRADECATCODE      VARCHAR2(50)     NULL, ");
                bufSql.append(" FCLEARACCOUNT      VARCHAR2(50)     NULL, ");
                bufSql.append(" FDESC              VARCHAR2(100)    NULL, ");
                bufSql.append(" FCHECKSTATE        NUMBER(1)    NOT NULL, ");
                bufSql.append(" FCREATOR           VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FCREATETIME        VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FCHECKUSER         VARCHAR2(20)     NULL, ");
                bufSql.append(" FCHECKTIME         VARCHAR2(20)     NULL ");
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
                bufSql.append(" FIFBROKERNAME, ");
                bufSql.append(" FCLEARERIDTYPE, ");
                bufSql.append(" FCLEARERID, ");
                bufSql.append(" FCLEARERNAME, ");
                bufSql.append(" FCLEARERDESC, ");
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
                bufSql.append(" NULL, ");
                bufSql.append(" FCLEARERIDTYPE, ");
                bufSql.append(" FCLEARERID, ");
                bufSql.append(" NULL, ");
                bufSql.append(" NULL, ");
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
                bufSql.append(" FROM TB_" + sPre + "_PAR_08052008061540000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_BROKERSUBBNY ADD CONSTRAINT PK_TB_" + sPre +
                               "_PARA_BROKERSUBBNY PRIMARY KEY (FBROKERCODE,FEXCHANGECODE)");
            }
            //----------------------jc
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp4 增加表字段出错！", ex);
        } finally {
            bufSql = null; //回收,以清除内存
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
