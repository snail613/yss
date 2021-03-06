package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.Connection;

public class DB21010002sp2
    extends BaseDbUpdate {
    public DB21010002sp2() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "Tb_Cash_Command")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre +
                    "_Cash_Command DROP CONSTRAINT " + strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_02282008123619")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_02282008123619");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_Cash_Command TO TB_02282008123619"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Cash_Command ");
            bufSql.append("(");
            bufSql.append("    FNUM          VARCHAR(20)  NOT NULL,");
            bufSql.append("    FPORTCODE     VARCHAR(20),");
            bufSql.append("    FCOMMANDDATE  DATE          NOT NULL,");
            bufSql.append("    FCOMMANDTIME  VARCHAR(20)  NOT NULL,");
            bufSql.append("    FACCOUNTDATE  DATE          NOT NULL,");
            bufSql.append("    FACCOUNTTIME  VARCHAR(20)  NOT NULL,");
            bufSql.append("    FORDER        DECIMAL(8)     NOT NULL,");
            bufSql.append("    FPAYERNAME    VARCHAR(200) NOT NULL,");
            bufSql.append("    FPAYERBANK    VARCHAR(100) NOT NULL,");
            bufSql.append("    FPAYERACCOUNT VARCHAR(100) NOT NULL,");
            bufSql.append("    FPAYCURY      VARCHAR(20)  NOT NULL,");
            bufSql.append("    FPAYMONEY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FREFRATE      DECIMAL(20,15),");
            bufSql.append("    FRECERNAME    VARCHAR(200) NOT NULL,");
            bufSql.append("    FRECERBANK    VARCHAR(100) NOT NULL,");
            bufSql.append("    FRECERACCOUNT VARCHAR(100) NOT NULL,");
            bufSql.append("    FRECMONEY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append("    FRECCURY      VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCASHUSAGE    VARCHAR(100),");
            bufSql.append("    FRELANUM      VARCHAR(20),");
            bufSql.append("    FNUMTYPE      VARCHAR(20),");
            bufSql.append("    FDESC         VARCHAR(100),");
            bufSql.append("    FCHECKSTATE   DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR      VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCREATETIME   VARCHAR(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER    VARCHAR(20),");
            bufSql.append("    FCHECKTIME    VARCHAR(20)");
            bufSql.append(")");

            dbl.executeSql(bufSql.toString());

            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO Tb_" + sPre + "_Cash_Command( ");
            bufSql.append("FNUM,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FCOMMANDDATE,");
            bufSql.append("FCOMMANDTIME,");
            bufSql.append("FACCOUNTDATE,");
            bufSql.append("FACCOUNTTIME,");
            bufSql.append("FORDER,");
            bufSql.append("FPAYERNAME,");
            bufSql.append("FPAYERBANK,");
            bufSql.append("FPAYERACCOUNT,");
            bufSql.append("FPAYCURY,");
            bufSql.append("FPAYMONEY,");
            bufSql.append("FREFRATE,");
            bufSql.append("FRECERNAME,");
            bufSql.append("FRECERBANK,");
            bufSql.append("FRECERACCOUNT,");
            bufSql.append("FRECMONEY,");
            bufSql.append("FRECCURY,");
            bufSql.append("FCASHUSAGE,");
            bufSql.append("FRELANUM,");
            bufSql.append("FNUMTYPE,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append("FNUM,");
            bufSql.append("' ',");
            bufSql.append("FCOMMANDDATE,");
            bufSql.append("FCOMMANDTIME,");
            bufSql.append("FACCOUNTDATE,");
            bufSql.append("FACCOUNTTIME,");
            bufSql.append("FORDER,");
            bufSql.append("FPAYERNAME,");
            bufSql.append("FPAYERBANK,");
            bufSql.append("FPAYERACCOUNT,");
            bufSql.append("FPAYCURY,");
            bufSql.append("FPAYMONEY,");
            bufSql.append("FREFRATE,");
            bufSql.append("FRECERNAME,");
            bufSql.append("FRECERBANK,");
            bufSql.append("FRECERACCOUNT,");
            bufSql.append("FRECMONEY,");
            bufSql.append("FRECCURY,");
            bufSql.append("FCASHUSAGE,");
            bufSql.append("FRELANUM,");
            bufSql.append("FNUMTYPE,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");

            bufSql.append(" FROM TB_02282008123619");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Cash_Command ADD CONSTRAINT PK_Tb_" + sPre + "_Commond PRIMARY KEY (FNUM)");

        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0002sp2 新增表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
