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
public class DB21010003sp4
    extends BaseDbUpdate {
    public DB21010003sp4() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //BugNo:0000363 edit by jc
            //对表 Tb_XXX_Para_BrokerSubBny 增加字段 FBrokerName,FClearerName,FClearerDesc
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_PARA_BROKERSUBBNY");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_BROKERSUBBNY DROP CONSTRAINT " + sPKName);
            }
            if (dbl.yssTableExist("TB_08052008061540000")) {
                this.dropTableByTableName("TB_08052008061540000");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_PARA_BROKERSUBBNY TO TB_08052008061540000");
            bufSql.delete(0, bufSql.length());
            bTrans = false;
            bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_BROKERSUBBNY ");
            bufSql.append(" ( ");
            bufSql.append(" FBROKERCODE        VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FEXCHANGECODE      VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FBROKERIDTYPE      VARCHAR(20), ");
            bufSql.append(" FBROKERID          VARCHAR(20), ");
            bufSql.append(" FIFBROKERNAME      VARCHAR(200),");
            bufSql.append(" FCLEARERIDTYPE     VARCHAR(20), ");
            bufSql.append(" FCLEARERID         VARCHAR(20), ");
            bufSql.append(" FCLEARERNAME       VARCHAR(200),");
            bufSql.append(" FCLEARERDESC       VARCHAR(100),");
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
            bufSql.append(" ' ', ");
            bufSql.append(" FCLEARERIDTYPE, ");
            bufSql.append(" FCLEARERID, ");
            bufSql.append(" ' ', ");
            bufSql.append(" ' ', ");
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
            bufSql.append(" FROM TB_08052008061540000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_PARA_BROKERSUBBNY ADD CONSTRAINT Pk_Tb_" + sPre +
                           "_RSUBBNY PRIMARY KEY (FBROKERCODE,FEXCHANGECODE)");
            //----------------------jc
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp4 增加表字段出错！", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
