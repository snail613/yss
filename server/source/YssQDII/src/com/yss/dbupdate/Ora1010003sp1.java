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
public class Ora1010003sp1
    extends BaseDbUpdate {
    public Ora1010003sp1() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //对表 Tb_XXX_Rep_Cell 增加字段 FOtherParams
//         dbl.executeSql("alter table Tb_" + sPre +
//                        "_Rep_cell add FOtherParams varchar2(500) null "); 在Ora1010003中已经添加过。sj edit 20080703.
            //对表 Tb_XXX_Vch_Project 增加字段 FHandCheck 默认为1，代表手工选项
            if (existsTabColumn_Ora("TB_" + sPre + "Vch_Project", "FHandCheck")) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_VCH_PROJECT ADD FHANDCHECK NUMBER(1) DEFAULT 1 NOT NULL ");
            }

            //---------------------------添加综合业务表中的业务日期字段 sj edit 20080701----------------------------------------------
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_Data_Integrated")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080701
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_Data_Integrated") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_06272008081808000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_06272008081808000");
            }
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_INTEGRATED") +
                           " RENAME TO TB_" + sPre + "_DAT_06272008081808000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_DATA_INTEGRATED"));
            bufSql.append(" (");
            bufSql.append("FNUM           VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSUBNUM        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FINOUTTYPE     NUMBER(1)     NOT NULL,");
            bufSql.append("FEXCHANGEDATE  DATE          NOT NULL,");
            bufSql.append("FOPERDATE      DATE          NOT NULL,");
            bufSql.append("FSECURITYCODE  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FRELANUM       VARCHAR2(20)      NULL,");
            bufSql.append("FNUMTYPE       VARCHAR2(20)      NULL,");
            bufSql.append("FTRADETYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("FPORTCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE1 VARCHAR2(10)  NOT NULL,");
            bufSql.append("FANALYSISCODE2 VARCHAR2(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE3 VARCHAR2(20)  NOT NULL,");
            bufSql.append("FAMOUNT        NUMBER(18,4)  NOT NULL,");
            bufSql.append("FEXCHANGECOST  NUMBER(18,4)  NOT NULL,");
            bufSql.append("FMEXCOST       NUMBER(18,4)  NOT NULL,");
            bufSql.append("FVEXCOST       NUMBER(18,4)  NOT NULL,");
            bufSql.append("FPORTEXCOST    NUMBER(18,4)  NOT NULL,");
            bufSql.append("FMPORTEXCOST   NUMBER(18,4)  NOT NULL,");
            bufSql.append("FVPORTEXCOST   NUMBER(18,4)  NOT NULL,");
            bufSql.append("FBASEEXCOST    NUMBER(18,4)  NOT NULL,");
            bufSql.append("FMBASEEXCOST   NUMBER(18,4)  NOT NULL,");
            bufSql.append("FVBASEEXCOST   NUMBER(18,4)  NOT NULL,");
            bufSql.append("FBASECURYRATE  NUMBER(18,15) NOT NULL,");
            bufSql.append("FPORTCURYRATE  NUMBER(18,15) NOT NULL,");
            bufSql.append("FSECEXDESC     VARCHAR2(100)     NULL,");
            bufSql.append("FDESC          VARCHAR2(100)     NULL,");
            bufSql.append("FCHECKSTATE    NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER     VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME     VARCHAR2(20)      NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_DATA_INTEGRATED") + "(");
            bufSql.append("FNum,");
            bufSql.append("FSUBNUM,");
            bufSql.append("FINOUTTYPE,");
            bufSql.append("FEXCHANGEDATE,");
            bufSql.append("FOPERDATE,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FRELANUM,");
            bufSql.append("FNUMTYPE,");
            bufSql.append("FTRADETYPECODE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FANALYSISCODE1,");
            bufSql.append("FANALYSISCODE2,");
            bufSql.append("FANALYSISCODE3,");
            bufSql.append("FAMOUNT,");
            bufSql.append("FEXCHANGECOST,");
            bufSql.append("FMEXCOST,");
            bufSql.append("FVEXCOST,");
            bufSql.append("FPORTEXCOST,");
            bufSql.append("FMPORTEXCOST,");
            bufSql.append("FVPORTEXCOST,");
            bufSql.append("FBASEEXCOST,");
            bufSql.append("FMBASEEXCOST,");
            bufSql.append("FVBASEEXCOST,");
            bufSql.append("FBASECURYRATE,");
            bufSql.append("FPORTCURYRATE,");
            bufSql.append("FSECEXDESC,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(")");
            bufSql.append("SELECT ");
            bufSql.append("FNum,");
            bufSql.append("FSUBNUM,");
            bufSql.append("FINOUTTYPE,");
            bufSql.append("FEXCHANGEDATE,");
            bufSql.append("SYSDATE,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FRELANUM,");
            bufSql.append("FNUMTYPE,");
            bufSql.append("FTRADETYPECODE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FANALYSISCODE1,");
            bufSql.append("FANALYSISCODE2,");
            bufSql.append("FANALYSISCODE3,");
            bufSql.append("FAMOUNT,");
            bufSql.append("FEXCHANGECOST,");
            bufSql.append("FMEXCOST,");
            bufSql.append("FVEXCOST,");
            bufSql.append("FPORTEXCOST,");
            bufSql.append("FMPORTEXCOST,");
            bufSql.append("FVPORTEXCOST,");
            bufSql.append("FBASEEXCOST,");
            bufSql.append("FMBASEEXCOST,");
            bufSql.append("FVBASEEXCOST,");
            bufSql.append("FBASECURYRATE,");
            bufSql.append("FPORTCURYRATE,");
            bufSql.append("FSECEXDESC,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_DAT_06272008081808000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("Tb_DATA_INTEGRATED") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_DATA_INTEGRATED " +
                           " PRIMARY KEY (FNum,FSUBNUM)");
            //------------------------------------------------------------------------

        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003 增加表字段出错！", ex);
        }
    }

    //调整字段名
    public void adjustFieldName(String sPre) throws YssException {
        try {
            //---------------------2008.06.13 蒋锦 添加 Tb_Comp_IndexCfg FRepCode----------------------//
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre +
                "_COMP_INDEXCFG RENAME COLUMN FREPCOD TO FREPCODE");
            //----------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003 修改组合群表列名出错！", e);
        }
    }

    /**
     * 调整字段的长度
     */
    public void adjustFieldPrecision(String sPre) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            StringBuffer bufSql = new StringBuffer(5000);
            String strPKName = "";
            //---------------------2008.06.16 单亮 修改 Tb_COMP_INDEXCFG FINDEXCFGNAME的长度（原来varchar2（50）修改为100）----------------------//
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre +
                "_COMP_INDEXCFG MODIFY(FINDEXCFGNAME  VARCHAR2(100))");
            //----------------------------------------------------------------------------------------//
            //修改表 tb_XXX_Cash_Command 的字段长度为 500
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_CASH_COMMAND")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_CASH_COMMAND") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
            }
            if (dbl.yssTableExist("TB_" + sPre + "_CAS_06172008020801000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_CAS_06172008020801000");
            }
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_CASH_COMMAND") +
                           " RENAME TO TB_" + sPre + "_CAS_06172008020801000");
            bufSql.append(" CREATE TABLE TB_" + sPre + "_CASH_COMMAND");
            bufSql.append(" (");
            bufSql.append(" FNUM          VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCOMMANDDATE  DATE          NOT NULL,");
            bufSql.append(" FCOMMANDTIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FACCOUNTDATE  DATE          NOT NULL,");
            bufSql.append(" FACCOUNTTIME  VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FORDER        NUMBER(8)     NOT NULL,");
            bufSql.append(" FPAYERNAME    VARCHAR2(200) NOT NULL,");
            bufSql.append(" FPAYERBANK    VARCHAR2(100) NOT NULL,");
            bufSql.append(" FPAYERACCOUNT VARCHAR2(100) NOT NULL,");
            bufSql.append(" FPAYCURY      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FPAYMONEY     NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FREFRATE      NUMBER(20,15)     NULL,");
            bufSql.append(" FRECERNAME    VARCHAR2(200) NOT NULL,");
            bufSql.append(" FRECERBANK    VARCHAR2(100) NOT NULL,");
            bufSql.append(" FRECERACCOUNT VARCHAR2(100) NOT NULL,");
            bufSql.append(" FRECMONEY     NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FRECCURY      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCASHUSAGE    VARCHAR2(100)     NULL,");
            bufSql.append(" FRELANUM      VARCHAR2(20)      NULL,");
            bufSql.append(" FNUMTYPE      VARCHAR2(20)      NULL,");
            bufSql.append(" FPORTCODE     VARCHAR2(20)      NULL,");
            bufSql.append(" FDESC         VARCHAR2(500)     NULL,");
            bufSql.append(" FCHECKSTATE   NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER    VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME    VARCHAR2(20)      NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            bufSql.append(" INSERT INTO TB_" + sPre + "_CASH_COMMAND (");
            bufSql.append(" FNUM,");
            bufSql.append(" FCOMMANDDATE,");
            bufSql.append(" FCOMMANDTIME,");
            bufSql.append(" FACCOUNTDATE,");
            bufSql.append(" FACCOUNTTIME,");
            bufSql.append(" FORDER,");
            bufSql.append(" FPAYERNAME,");
            bufSql.append(" FPAYERBANK,");
            bufSql.append(" FPAYERACCOUNT,");
            bufSql.append(" FPAYCURY,");
            bufSql.append(" FPAYMONEY,");
            bufSql.append(" FREFRATE,");
            bufSql.append(" FRECERNAME,");
            bufSql.append(" FRECERBANK,");
            bufSql.append(" FRECERACCOUNT,");
            bufSql.append(" FRECMONEY,");
            bufSql.append(" FRECCURY,");
            bufSql.append(" FCASHUSAGE,");
            bufSql.append(" FRELANUM,");
            bufSql.append(" FNUMTYPE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append(" FNUM,");
            bufSql.append(" FCOMMANDDATE,");
            bufSql.append(" FCOMMANDTIME,");
            bufSql.append(" FACCOUNTDATE,");
            bufSql.append(" FACCOUNTTIME,");
            bufSql.append(" FORDER,");
            bufSql.append(" FPAYERNAME,");
            bufSql.append(" FPAYERBANK,");
            bufSql.append(" FPAYERACCOUNT,");
            bufSql.append(" FPAYCURY,");
            bufSql.append(" FPAYMONEY,");
            bufSql.append(" FREFRATE,");
            bufSql.append(" FRECERNAME,");
            bufSql.append(" FRECERBANK,");
            bufSql.append(" FRECERACCOUNT,");
            bufSql.append(" FRECMONEY,");
            bufSql.append(" FRECCURY,");
            bufSql.append(" FCASHUSAGE,");
            bufSql.append(" FRELANUM,");
            bufSql.append(" FNUMTYPE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_CAS_06172008020801000 ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_CASH_COMMAND ADD CONSTRAINT PK_TB_" + sPre +
                           "_CASH_COMMAND " +
                           " PRIMARY KEY (FNUM)");

            //调整字段类型  BugNo:0000248 edit by jc
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_COMP_PORTINDEXLINK")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_COMP_PORTINDEXLINK") +
                               " DROP CONSTRAINT " + strPKName + " CASCADE");
                deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
            }
            if (dbl.yssTableExist("TB_" + sPre + "_COMP_06172008020801000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_COMP_06172008020801000");
            }
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_COMP_PORTINDEXLINK") +
                           " RENAME TO TB_" + sPre + "_COMP_06172008020801000");

            bufSql.append(" CREATE TABLE TB_" + sPre + "_COMP_PORTINDEXLINK");
            bufSql.append(" (");
            bufSql.append(" FPortCode     VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FIndexCfgCode VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCtlGrpCode   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCtlCode      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCtlValue     CLOB              NULL,");
            bufSql.append(" FDesc         VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE   NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER    VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME    VARCHAR2(20)      NULL");
            bufSql.append(" )");

            dbl.executeSql(bufSql.toString());

            bufSql.delete(0, bufSql.length());
            bufSql.append(" INSERT INTO TB_" + sPre + "_COMP_PORTINDEXLINK");
            bufSql.append(" (");
            bufSql.append(" FPortCode,");
            bufSql.append(" FIndexCfgCode,");
            bufSql.append(" FCtlGrpCode,");
            bufSql.append(" FCtlCode,");
            bufSql.append(" FCtlValue,");
            bufSql.append(" FDesc,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FPortCode,");
            bufSql.append(" FIndexCfgCode,");
            bufSql.append(" FCtlGrpCode,");
            bufSql.append(" FCtlCode,");
            bufSql.append(" FCtlValue,");
            bufSql.append(" FDesc,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_COMP_06172008020801000");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_COMP_PORTINDEXLINK ADD CONSTRAINT PK_TB_" + sPre +
                           "_COMP_PORTINDEXLINK" +
                           " PRIMARY KEY (FPortCode,FIndexCfgCode,FCtlGrpCode,FCtlCode)");
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003sp1 修改表的字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
