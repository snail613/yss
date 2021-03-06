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
public class Ora1010006
    extends BaseDbUpdate {
    public Ora1010006() {
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            //---------------------------------更新TB_XXX_DATA_BONUSSHARE表的FRATIO字段精度------------------------------//
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_DATA_BONUSSHARE"));
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_DATA_BONUSSHARE") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_11212008074313000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_11212008074313000");
            }
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_BONUSSHARE") +
                           " RENAME TO TB_" + sPre + "_DAT_11212008074313000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_DATA_BONUSSHARE"));
            bufSql.append("(");
            bufSql.append(" FTSECURITYCODE VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FRECORDDATE    DATE          NOT NULL,");
            bufSql.append(" FSSECURITYCODE VARCHAR2(20)      NULL,");
            bufSql.append(" FEXRIGHTDATE   DATE          NOT NULL,");
            bufSql.append(" FAFFICHEDATE   DATE              NULL,");
            bufSql.append(" FPAYDATE       DATE              NULL,");
            bufSql.append(" FRATIO         NUMBER(25,15) NOT NULL,");
            bufSql.append(" FROUNDCODE     VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDESC          VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE    NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR       VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER     VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME     VARCHAR2(20)      NULL");
            bufSql.append(")");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_DATA_BONUSSHARE") + "(");
            bufSql.append(" FTSECURITYCODE, ");
            bufSql.append(" FRECORDDATE, ");
            bufSql.append(" FSSECURITYCODE, ");
            bufSql.append(" FEXRIGHTDATE, ");
            bufSql.append(" FAFFICHEDATE, ");
            bufSql.append(" FPAYDATE, ");
            bufSql.append(" FRATIO, ");
            bufSql.append(" FROUNDCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(")");
            bufSql.append("SELECT ");
            bufSql.append(" FTSECURITYCODE, ");
            bufSql.append(" FRECORDDATE, ");
            bufSql.append(" FSSECURITYCODE, ");
            bufSql.append(" FEXRIGHTDATE, ");
            bufSql.append(" FAFFICHEDATE, ");
            bufSql.append(" FPAYDATE, ");
            bufSql.append(" FRATIO, ");
            bufSql.append(" FROUNDCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_DAT_11212008074313000 ");

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_BONUSSHARE") +
                           " ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_BONUSSHARE " +
                           " PRIMARY KEY (FTSECURITYCODE,FRECORDDATE)");

            //---------------------------------更新TB_XXX_DATA_DIVIDEND表的FRATIO 字段精度------------------------------//
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_DATA_DIVIDEND"));
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_DIVIDEND DROP CONSTRAINT " + strPKName +
                               " CASCADE");
                deleteIndex(strPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_11212008074328000")) {
                this.dropTableByTableName("TB_" + sPre + "_DAT_11212008074328000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_DIVIDEND RENAME TO TB_" + sPre +
                           "_DAT_11212008074328000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_DIVIDEND ");
            bufSql.append(" ( ");
            bufSql.append(" FSECURITYCODE   VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FRECORDDATE     DATE          NOT NULL,");
            bufSql.append(" FDIVDENDTYPE    NUMBER(2)     NOT NULL,");
            bufSql.append(" FCURYCODE       VARCHAR2(20)  DEFAULT ' ' NOT NULL,");
            bufSql.append(" FDIVIDENDDATE   DATE          NOT NULL,");
            bufSql.append(" FDISTRIBUTEDATE DATE          NOT NULL,");
            bufSql.append(" FAFFICHEDATE    DATE              NULL,");
            bufSql.append(" FRATIO          NUMBER(25,15) NOT NULL,");
            bufSql.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDESC           VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_DIVIDEND( ");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append(" FRECORDDATE,");
            bufSql.append(" FDIVDENDTYPE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FDIVIDENDDATE,");
            bufSql.append(" FDISTRIBUTEDATE,");
            bufSql.append(" FAFFICHEDATE,");
            bufSql.append(" FRATIO,");
            bufSql.append(" FROUNDCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append("SELECT ");
            bufSql.append(" FSECURITYCODE,");
            bufSql.append(" FRECORDDATE,");
            bufSql.append(" FDIVDENDTYPE,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FDIVIDENDDATE,");
            bufSql.append(" FDISTRIBUTEDATE,");
            bufSql.append(" FAFFICHEDATE,");
            bufSql.append(" FRATIO,");
            bufSql.append(" FROUNDCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_DAT_11212008074328000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("Alter Table Tb_" + sPre +
                           "_DATA_DIVIDEND Add Constraint PK_TB_" + sPre +
                           "_DATA_DIVIDEND PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FDIVDENDTYPE,FCURYCODE) ");

            //---------------------------------更新TB_XXX_DATA_RIGHTSISSUE表的FRATIO 字段精度------------------------------//
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_DATA_RIGHTSISSUE"));
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_RIGHTSISSUE DROP CONSTRAINT " + strPKName +
                               " CASCADE");
                deleteIndex(strPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_11212008074343000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_11212008074343000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_RIGHTSISSUE RENAME TO TB_" + sPre +
                           "_DAT_11212008074343000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_RIGHTSISSUE ");
            bufSql.append(" ( ");
            bufSql.append(" FSECURITYCODE   VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FRECORDDATE     DATE          NOT NULL, ");
            bufSql.append(" FRICURYCODE     VARCHAR2(20)      NULL, ");
            bufSql.append(" FTSECURITYCODE  VARCHAR2(20)      NULL, ");
            bufSql.append(" FEXRIGHTDATE    DATE          NOT NULL, ");
            bufSql.append(" FEXPIRATIONDATE DATE          NOT NULL, ");
            bufSql.append(" FAFFICHEDATE    DATE          NOT NULL, ");
            bufSql.append(" FPAYDATE        DATE          NOT NULL, ");
            bufSql.append(" FBEGINSCRIDATE  DATE          NOT NULL, ");
            bufSql.append(" FENDSCRIDATE    DATE          NOT NULL, ");
            bufSql.append(" FBEGINTRADEDATE DATE          NOT NULL, ");
            bufSql.append(" FENDTRADEDATE   DATE          NOT NULL, ");
            bufSql.append(" FRATIO          NUMBER(25,15) NOT NULL, ");
            bufSql.append(" FRIPRICE        NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FDESC           VARCHAR2(100)     NULL, ");
            bufSql.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL, ");
            bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL ");
            bufSql.append(") ");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_RIGHTSISSUE( ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FRECORDDATE, ");
            bufSql.append(" FRICURYCODE, ");
            bufSql.append(" FTSECURITYCODE, ");
            bufSql.append(" FEXRIGHTDATE, ");
            bufSql.append(" FEXPIRATIONDATE, ");
            bufSql.append(" FAFFICHEDATE, ");
            bufSql.append(" FPAYDATE, ");
            bufSql.append(" FBEGINSCRIDATE, ");
            bufSql.append(" FENDSCRIDATE, ");
            bufSql.append(" FBEGINTRADEDATE, ");
            bufSql.append(" FENDTRADEDATE, ");
            bufSql.append(" FRATIO, ");
            bufSql.append(" FRIPRICE, ");
            bufSql.append(" FROUNDCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT  ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FRECORDDATE, ");
            bufSql.append(" FRICURYCODE, ");
            bufSql.append(" FTSECURITYCODE, ");
            bufSql.append(" FEXRIGHTDATE, ");
            bufSql.append(" FEXPIRATIONDATE, ");
            bufSql.append(" FAFFICHEDATE, ");
            bufSql.append(" FPAYDATE, ");
            bufSql.append(" FBEGINSCRIDATE, ");
            bufSql.append(" FENDSCRIDATE, ");
            bufSql.append(" FBEGINTRADEDATE, ");
            bufSql.append(" FENDTRADEDATE, ");
            bufSql.append(" FRATIO, ");
            bufSql.append(" FRIPRICE, ");
            bufSql.append(" FROUNDCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_DAT_11212008074343000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("Alter Table Tb_" + sPre +
                           "_DATA_RIGHTSISSUE Add Constraint PK_TB_" + sPre +
                           "_DATA_RIGHTSISSUE PRIMARY KEY (FSECURITYCODE,FRECORDDATE) ");

            /**
             * date   : 2008-11-26
             * author : linjunyun
             * desc   : 业务数据--现金应收应付款表 TB_001_DATA_CASHPAYREC 更改字段的精度
             *          FDESC  VARCHAR2(100)) 改为 FDESC VARCHAR2(400)
             * BugID  :
             */
            bTrans = false;
            bufSql.delete(0, bufSql.length());
            //获得主键
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_DATA_CASHPAYREC"));
            //判断主键是否存在，存在则删除主键
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_DATA_CASHPAYREC") +
                               " DROP CONSTRAINT " + strPKName);
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_11262008091343000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DAT_11262008091343000");
            }
            //重命名原有表为临时表
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_CASHPAYREC") +
                           " RENAME TO TB_" + sPre + "_DAT_11262008091343000");
            bufSql.delete(0, bufSql.length());
            //创建包含新字段的表
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_DATA_CASHPAYREC"));
            bufSql.append(" ( ");
            bufSql.append("    FNUM            VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FTRANSDATE      DATE          NOT NULL,");
            bufSql.append("    FPORTCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE1  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE2  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FANALYSISCODE3  VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCASHACCCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FTSFTYPECODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FSUBTSFTYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCURYCODE       VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FINOUT          NUMBER(1)     NOT NULL,");
            bufSql.append("    FMONEY          NUMBER(30,4)  NOT NULL,");
            bufSql.append("    FBASECURYRATE   NUMBER(25,15) NOT NULL,");
            bufSql.append("    FBASECURYMONEY  NUMBER(30,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FPORTCURYRATE   NUMBER(25,15) NOT NULL,");
            bufSql.append("    FPORTCURYMONEY  NUMBER(30,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("    FDATASOURCE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FSTOCKIND       NUMBER(1)     NOT NULL,");
            bufSql.append("    FDESC           VARCHAR2(400)     NULL,");
            bufSql.append("    FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append("    FCHECKTIME      VARCHAR2(20)      NULL");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //从临时表中导数据到新创建的表中
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_DATA_CASHPAYREC") + "(");
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
            bufSql.append(" FINOUT,");
            bufSql.append(" FMONEY,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FBASECURYMONEY,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FPORTCURYMONEY,");
            bufSql.append(" FDATASOURCE,");
            bufSql.append(" FSTOCKIND,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" ) ");
            bufSql.append(" SELECT  ");
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
            bufSql.append(" FINOUT,");
            bufSql.append(" FMONEY,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FBASECURYMONEY,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FPORTCURYMONEY,");
            bufSql.append(" FDATASOURCE,");
            bufSql.append(" FSTOCKIND,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_DAT_11262008091343000");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //设定表的主键
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_CASHPAYREC") +
                           " ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_CASHPAYREC " +
                           " PRIMARY KEY (FNUM)");

        } catch (Exception ex) {
            throw new YssException("版本Oracle1010006更新表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
