package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.YssException;

public class DB21010004
    extends BaseDbUpdate {
    public DB21010004() {
    }

    //���ӱ��ֶ�
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer();
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            if (existsTabColumn_DB2("TB_" + sPre + "_data_marketvalue", "FMARKETSTATUS")) {
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_data_marketvalue");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_data_marketvalue DROP CONSTRAINT " + sPKName);
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_02142008093439")) {
                    this.dropTableByTableName("TB_02142008093439");
                }
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_data_marketvalue TO TB_02142008093439");

                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_MARKETVALUE ");
                bufSql.append(" ( ");
                bufSql.append(" FMKTSRCCODE    VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FSECURITYCODE  VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FMKTVALUEDATE  DATE           NOT NULL, ");
                bufSql.append(" FMKTVALUETIME  VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FPORTCODE      VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FBARGAINAMOUNT DECIMAL(18,4), ");
                bufSql.append(" FBARGAINMONEY  DECIMAL(18,4), ");
                bufSql.append(" FYCLOSEPRICE   DECIMAL(20,12) NOT NULL, ");
                bufSql.append(" FOPENPRICE     DECIMAL(20,12) NOT NULL, ");
                bufSql.append(" FTOPPRICE      DECIMAL(20,12) NOT NULL, ");
                bufSql.append(" FLOWPRICE      DECIMAL(20,12) NOT NULL, ");
                bufSql.append(" FCLOSINGPRICE  DECIMAL(20,12) NOT NULL, ");
                bufSql.append(" FAVERAGEPRICE  DECIMAL(20,12) NOT NULL, ");
                bufSql.append(" FNEWPRICE      DECIMAL(20,12), ");
                bufSql.append(" FMKTPRICE1     DECIMAL(20,12), ");
                bufSql.append(" FMKTPRICE2     DECIMAL(20,12), ");
                bufSql.append(" FMARKETSTATUS  VARCHAR(4), ");
                bufSql.append(" FDESC          VARCHAR(100), ");
                bufSql.append(" FDATASOURCE    DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FCHECKSTATE    DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FCREATOR       VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME    VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER     VARCHAR(20), ");
                bufSql.append(" FCHECKTIME     VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_MARKETVALUE( ");
                bufSql.append(" FMKTSRCCODE, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FMKTVALUEDATE, ");
                bufSql.append(" FMKTVALUETIME, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FBARGAINAMOUNT, ");
                bufSql.append(" FBARGAINMONEY, ");
                bufSql.append(" FYCLOSEPRICE, ");
                bufSql.append(" FOPENPRICE, ");
                bufSql.append(" FTOPPRICE, ");
                bufSql.append(" FLOWPRICE, ");
                bufSql.append(" FCLOSINGPRICE, ");
                bufSql.append(" FAVERAGEPRICE, ");
                bufSql.append(" FNEWPRICE, ");
                bufSql.append(" FMKTPRICE1, ");
                bufSql.append(" FMKTPRICE2, ");
                bufSql.append(" FMARKETSTATUS, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FDATASOURCE, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FMKTSRCCODE, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FMKTVALUEDATE, ");
                bufSql.append(" FMKTVALUETIME, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FBARGAINAMOUNT, ");
                bufSql.append(" FBARGAINMONEY, ");
                bufSql.append(" FYCLOSEPRICE, ");
                bufSql.append(" FOPENPRICE, ");
                bufSql.append(" FTOPPRICE, ");
                bufSql.append(" FLOWPRICE, ");
                bufSql.append(" FCLOSINGPRICE, ");
                bufSql.append(" FAVERAGEPRICE, ");
                bufSql.append(" FNEWPRICE, ");
                bufSql.append(" FMKTPRICE1, ");
                bufSql.append(" FMKTPRICE2, ");
                bufSql.append(" ' ', ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FDATASOURCE, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_02142008093439 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_MARKETVALUE ADD CONSTRAINT PK_TB_" + sPre +
                               "_ETVALUE " +
                               "PRIMARY KEY (FMKTSRCCODE,FSECURITYCODE,FMKTVALUEDATE,FMKTVALUETIME,FPORTCODE)");
                bTrans = false;
            }
            if (existsTabColumn_DB2("TB_" + sPre + "_data_valmktprice", "FMARKETSTATUS")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_data_valmktprice");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_data_valmktprice DROP CONSTRAINT " + sPKName);
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_02142008093440")) {
                    this.dropTableByTableName("TB_02142008093440");
                }
                dbl.executeSql("ReName TABLE TB_" + sPre +
                               "_data_valmktprice TO TB_02142008093440");
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_VALMKTPRICE ");
                bufSql.append(" ( ");
                bufSql.append(" FVALDATE      DATE            NOT NULL, ");
                bufSql.append(" FPORTCODE     VARCHAR(20)     NOT NULL, ");
                bufSql.append(" FSECURITYCODE VARCHAR(20)     NOT NULL, ");
                bufSql.append(" FCHECKSTATE   DECIMAL(1)      NOT NULL, ");
                bufSql.append(" FPRICE        DECIMAL(20,12)  NOT NULL DEFAULT 0, ");
                bufSql.append(" FOTPRICE1     DECIMAL(20,12)  NOT NULL DEFAULT 0, ");
                bufSql.append(" FOTPRICE2     DECIMAL(20,12)  NOT NULL DEFAULT 0, ");
                bufSql.append(" FOTPRICE3     DECIMAL(20,12)  NOT NULL DEFAULT 0, ");
                bufSql.append(" FMARKETSTATUS VARCHAR(4), ");
                bufSql.append(" FCREATOR      VARCHAR(20)     NOT NULL, ");
                bufSql.append(" FCREATETIME   VARCHAR(20)     NOT NULL, ");
                bufSql.append(" FCHECKUSER    VARCHAR(20), ");
                bufSql.append(" FCHECKTIME    VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.delete(0, bufSql.length());
                bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_VALMKTPRICE( ");
                bufSql.append(" FVALDATE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FPRICE, ");
                bufSql.append(" FOTPRICE1, ");
                bufSql.append(" FOTPRICE2, ");
                bufSql.append(" FOTPRICE3, ");
                bufSql.append(" FMARKETSTATUS, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FVALDATE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FPRICE, ");
                bufSql.append(" FOTPRICE1, ");
                bufSql.append(" FOTPRICE2, ");
                bufSql.append(" FOTPRICE3, ");
                bufSql.append(" ' ', ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_02142008093440");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_DATA_VALMKTPRICE ADD CONSTRAINT PK_TB_" + sPre + "MKTPRICE " +
                               " PRIMARY KEY (FVALDATE,FPORTCODE,FSECURITYCODE)");
            }
        } catch (Exception ex) {
            throw new YssException("�汾Oracle1010003sp10���ӱ��ֶγ���", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }

    }
}
