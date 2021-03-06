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
public class DB21010006
    extends BaseDbUpdate {
    public DB21010006() {
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            //---------------------------------更新TB_XXX_DATA_BONUSSHARE表的FRATIO字段精度------------------------------//
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "TB_DATA_BONUSSHARE"));
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_DATA_BONUSSHARE") +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_11212008124709")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_11212008124709");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_DATA_BONUSSHARE TO TB_11212008124709");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_DATA_BONUSSHARE"));
            bufSql.append("(");
            bufSql.append(" FTSECURITYCODE VARCHAR(20)  NOT NULL,");
            bufSql.append(" FRECORDDATE    DATE          NOT NULL,");
            bufSql.append(" FSSECURITYCODE VARCHAR(20),");
            bufSql.append(" FEXRIGHTDATE   DATE          NOT NULL,");
            bufSql.append(" FAFFICHEDATE   DATE,");
            bufSql.append(" FPAYDATE       DATE,");
            bufSql.append(" FRATIO         DECIMAL(25,15) NOT NULL,");
            bufSql.append(" FROUNDCODE     VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDESC          VARCHAR(100),");
            bufSql.append(" FCHECKSTATE    DECIMAL(1)     NOT NULL,");
            bufSql.append(" FCREATOR       VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER     VARCHAR(20),");
            bufSql.append(" FCHECKTIME     VARCHAR(20)");
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
            bufSql.append(" FROM TB_11212008124709");

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_BONUSSHARE ADD CONSTRAINT PK_Tb_" + sPre +
                           "_Data_Bon PRIMARY KEY (FTSECURITYCODE,FRECORDDATE)");

            //---------------------------------更新TB_XXX_DATA_DIVIDEND表的FRATIO 字段精度------------------------------//
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "TB_DATA_DIVIDEND"));
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_DIVIDEND DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_11212008124710")) {
                this.dropTableByTableName("TB_11212008124710");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_DATA_DIVIDEND TO TB_11212008124710");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_DIVIDEND ");
            bufSql.append(" ( ");
            bufSql.append(" FSECURITYCODE   VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FRECORDDATE     DATE          NOT NULL,");
            bufSql.append(" FDIVDENDTYPE    DECIMAL(2)     NOT NULL,");
            bufSql.append(" FCURYCODE       VARCHAR(20)  NOT NULL DEFAULT ' ',");
            bufSql.append(" FDIVIDENDDATE   DATE          NOT NULL,");
            bufSql.append(" FDISTRIBUTEDATE DATE          NOT NULL,");
            bufSql.append(" FAFFICHEDATE    DATE,");
            bufSql.append(" FRATIO          DECIMAL(25,15) NOT NULL,");
            bufSql.append(" FROUNDCODE      VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDESC           VARCHAR(100),");
            bufSql.append(" FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append(" FCREATOR        VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME     VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER      VARCHAR(20),");
            bufSql.append(" FCHECKTIME      VARCHAR(20)");
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
            bufSql.append(" FROM TB_11212008124710");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_DIVIDEND ADD CONSTRAINT PK_Tb_" + sPre +
                           "_Data_Div PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FDIVDENDTYPE,FCURYCODE)");

            //---------------------------------更新TB_XXX_DATA_RIGHTSISSUE表的FRATIO 字段精度------------------------------//
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "TB_DATA_RIGHTSISSUE"));
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_RIGHTSISSUE DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_11212008124711")) {
                this.dropTableByTableName("TB_11212008124711");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_DATA_RIGHTSISSUE TO TB_11212008124711");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_RIGHTSISSUE ");
            bufSql.append(" ( ");
            bufSql.append(" FSECURITYCODE   VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FRECORDDATE     DATE          NOT NULL, ");
            bufSql.append(" FRICURYCODE     VARCHAR(20), ");
            bufSql.append(" FTSECURITYCODE  VARCHAR(20), ");
            bufSql.append(" FEXRIGHTDATE    DATE          NOT NULL, ");
            bufSql.append(" FEXPIRATIONDATE DATE          NOT NULL, ");
            bufSql.append(" FAFFICHEDATE    DATE          NOT NULL, ");
            bufSql.append(" FPAYDATE        DATE          NOT NULL, ");
            bufSql.append(" FBEGINSCRIDATE  DATE          NOT NULL, ");
            bufSql.append(" FENDSCRIDATE    DATE          NOT NULL, ");
            bufSql.append(" FBEGINTRADEDATE DATE          NOT NULL, ");
            bufSql.append(" FENDTRADEDATE   DATE          NOT NULL, ");
            bufSql.append(" FRATIO          DECIMAL(25,15) NOT NULL, ");
            bufSql.append(" FRIPRICE        DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FROUNDCODE      VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FDESC           VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE     DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR        VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME     VARCHAR(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER      VARCHAR(20), ");
            bufSql.append(" FCHECKTIME      VARCHAR(20)");
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
            bufSql.append(" FROM TB_11212008124711 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_RIGHTSISSUE ADD CONSTRAINT PK_Tb_" + sPre +
                           "_Data_Rig PRIMARY KEY(FSECURITYCODE, FRECORDDATE)");

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
            strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_DATA_CASHPAYREC");
            //判断主键是否存在，存在则删除主键
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_CASHPAYREC DROP CONSTRAINT " + strPKName);
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_11262008091456")) {
                this.dropTableByTableName("TB_11262008091456");
            }
            //重命名原有表为临时表
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_DATA_CASHPAYREC TO TB_11262008091456");
            //创建包含新字段的表
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_CASHPAYREC ");
            bufSql.append(" ( ");
            bufSql.append("    FNUM            VARCHAR(20)    NOT NULL,");
            bufSql.append("    FTRANSDATE      DATE           NOT NULL,");
            bufSql.append("    FPORTCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("    FANALYSISCODE1  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FANALYSISCODE2  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FANALYSISCODE3  VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCASHACCCODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("    FTSFTYPECODE    VARCHAR(20)    NOT NULL,");
            bufSql.append("    FSUBTSFTYPECODE VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCURYCODE       VARCHAR(20)    NOT NULL,");
            bufSql.append("    FINOUT          DECIMAL(1)     NOT NULL,");
            bufSql.append("    FMONEY          DECIMAL(30,4)  NOT NULL,");
            bufSql.append("    FBASECURYRATE   DECIMAL(25,15) NOT NULL,");
            bufSql.append("    FBASECURYMONEY  DECIMAL(30,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FPORTCURYRATE   DECIMAL(25,15) NOT NULL,");
            bufSql.append("    FPORTCURYMONEY  DECIMAL(30,4)  NOT NULL DEFAULT 0 ,");
            bufSql.append("    FDATASOURCE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FSTOCKIND       DECIMAL(1)     NOT NULL,");
            bufSql.append("    FDESC           VARCHAR(400),");
            bufSql.append("    FCHECKSTATE     DECIMAL(1)     NOT NULL,");
            bufSql.append("    FCREATOR        VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCREATETIME     VARCHAR(20)    NOT NULL,");
            bufSql.append("    FCHECKUSER      VARCHAR(20),");
            bufSql.append("    FCHECKTIME      VARCHAR(20)");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //从临时表中导数据到新创建的表中
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_CASHPAYREC( ");
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
            bufSql.append(" FROM TB_11262008091456 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            //设定表的主键
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_CASHPAYREC ADD CONSTRAINT PK_TB_" + sPre +
                           "_Data_Cas " +
                           "PRIMARY KEY (FNUM)");
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("版本Oracle1010006更新表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
