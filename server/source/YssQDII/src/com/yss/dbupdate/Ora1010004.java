package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.YssException;

public class Ora1010004
    extends BaseDbUpdate {
    public Ora1010004() {
    }

    //���ӱ��ֶ�
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer();
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            if (existsTabColumn_Ora("TB_" + sPre + "_data_marketvalue", "FMARKETSTATUS")) {
                sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_data_marketvalue");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_data_marketvalue DROP CONSTRAINT " + sPKName +
                                   " CASCADE");
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_DAT_10172008121507000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_DAT_10172008121507000");
                }
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_data_marketvalue RENAME TO TB_" + sPre +
                               "_DAT_10172008121507000");

                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_MARKETVALUE ");
                bufSql.append(" ( ");
                bufSql.append(" FMKTSRCCODE    VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FSECURITYCODE  VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FMKTVALUEDATE  DATE          NOT NULL, ");
                bufSql.append(" FMKTVALUETIME  VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FPORTCODE      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FBARGAINAMOUNT NUMBER(18,4)      NULL, ");
                bufSql.append(" FBARGAINMONEY  NUMBER(18,4)      NULL, ");
                bufSql.append(" FYCLOSEPRICE   NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FOPENPRICE     NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FTOPPRICE      NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FLOWPRICE      NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FCLOSINGPRICE  NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FAVERAGEPRICE  NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FNEWPRICE      NUMBER(20,12)     NULL, ");
                bufSql.append(" FMKTPRICE1     NUMBER(20,12)     NULL, ");
                bufSql.append(" FMKTPRICE2     NUMBER(20,12)     NULL, ");
                bufSql.append(" FMARKETSTATUS  VARCHAR2(4)       NULL, ");
                bufSql.append(" FDESC          VARCHAR2(100)     NULL, ");
                bufSql.append(" FDATASOURCE    NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCHECKSTATE    NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME    VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER     VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME     VARCHAR2(20)      NULL ");
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
                bufSql.append(" NULL, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FDATASOURCE, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_" + sPre + "_DAT_10172008121507000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_MARKETVALUE ADD CONSTRAINT PK_TB_" + sPre +
                               "_DATA_MARKETVALUE " +
                               "PRIMARY KEY (FMKTSRCCODE,FSECURITYCODE,FMKTVALUEDATE,FMKTVALUETIME,FPORTCODE)");
                bTrans = false;
            }
            if (existsTabColumn_Ora("TB_" + sPre + "_data_valmktprice", "FMARKETSTATUS")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_data_valmktprice");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_data_valmktprice DROP CONSTRAINT " + sPKName +
                                   " CASCADE");
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_DAT_10172008121535000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_DAT_10172008121535000");
                }
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_data_valmktprice RENAME TO TB_" + sPre +
                               "_DAT_10172008121535000");
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_VALMKTPRICE ");
                bufSql.append(" ( ");
                bufSql.append(" FVALDATE      DATE          NOT NULL, ");
                bufSql.append(" FPORTCODE     VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FSECURITYCODE VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKSTATE   NUMBER(1)     NOT NULL, ");
                bufSql.append(" FPRICE        NUMBER(20,12) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FOTPRICE1     NUMBER(20,12) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FOTPRICE2     NUMBER(20,12) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FOTPRICE3     NUMBER(20,12) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMARKETSTATUS VARCHAR2(4)       NULL, ");
                bufSql.append(" FCREATOR      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME   VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER    VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME    VARCHAR2(20)      NULL ");
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
                bufSql.append(" NULL, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_" + sPre + "_DAT_10172008121535000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_DATA_VALMKTPRICE ADD CONSTRAINT PK_TB_" + sPre + "_DATA_VALMKTPRICE " +
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
