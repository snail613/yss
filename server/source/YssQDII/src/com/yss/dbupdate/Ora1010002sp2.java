package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.Connection;

public class Ora1010002sp2
    extends BaseDbUpdate {
    public Ora1010002sp2() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------Tb_001_Cash_Command  添加字段 fportcode-- 单亮 2008-5-7-----------------------------------//
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "Tb_Cash_Command")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre +
                    "_Cash_Command DROP CONSTRAINT " + strPKName + " CASCADE"); //删除表主键
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_02282008123619000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_02282008123619000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_Cash_Command RENAME TO TB_"
                           + sPre + "_DAT_02282008123619000"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_Cash_Command ");
            bufSql.append("(");
            bufSql.append("    FNUM          VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FPORTCODE     VARCHAR2(20)      NULL,");
            bufSql.append("    FCOMMANDDATE  DATE          NOT NULL,");
            bufSql.append("    FCOMMANDTIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FACCOUNTDATE  DATE          NOT NULL,");
            bufSql.append("    FACCOUNTTIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FORDER        NUMBER(8)     NOT NULL,");
            bufSql.append("    FPAYERNAME    VARCHAR2(200) NOT NULL,");
            bufSql.append("    FPAYERBANK    VARCHAR2(100) NOT NULL,");
            bufSql.append("    FPAYERACCOUNT VARCHAR2(100) NOT NULL,");
            bufSql.append("    FPAYCURY      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FPAYMONEY     NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FREFRATE      NUMBER(20,15)     NULL,");
            bufSql.append("    FRECERNAME    VARCHAR2(200) NOT NULL,");
            bufSql.append("    FRECERBANK    VARCHAR2(100) NOT NULL,");
            bufSql.append("    FRECERACCOUNT VARCHAR2(100) NOT NULL,");
            bufSql.append("    FRECMONEY     NUMBER(18,4)  NOT NULL,");
            bufSql.append("    FRECCURY      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCASHUSAGE    VARCHAR2(100)     NULL,");
            bufSql.append("    FRELANUM      VARCHAR2(20)      NULL,");
            bufSql.append("    FNUMTYPE      VARCHAR2(20)      NULL,");
            bufSql.append("    FDESC         VARCHAR2(200)     NULL,"); //修改长度.sj edit 20080627
            bufSql.append("    FCHECKSTATE   NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR      VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME   VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER    VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME    VARCHAR2(20)      NULL");
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
            bufSql.append("NULL,");
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

            bufSql.append(" FROM TB_" + sPre + "_DAT_02282008123619000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Cash_Command ADD CONSTRAINT PK_TB_" + sPre +
                           "_Cash_Command PRIMARY KEY (FNUM)");

        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0002sp2 新增表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
