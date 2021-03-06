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
public class DB21010003sp1
    extends BaseDbUpdate {
    public DB21010003sp1() {
    }

    //调整字段名
    public void adjustFieldName(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer(10000);
        boolean bTrans = false; //代表是否开始事务
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //---------------------2008.06.13 蒋锦 添加 Tb_Comp_IndexCfg FRepCode----------------------//
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "TB_COMP_INDEXCFG"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_COMP_INDEXCFG DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_06132008081125")) {
                this.dropTableByTableName("TB_06132008081125");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_COMP_INDEXCFG TO TB_06132008081125");

            bufSql.append("CREATE TABLE TB_" + sPre + "_COMP_INDEXCFG");
            bufSql.append("( ");
            bufSql.append(" FINDEXCFGCODE    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FINDEXCFGNAME    VARCHAR(50)  NOT NULL,");
            bufSql.append(" FINDEXTYPE       VARCHAR(20)  NOT NULL,");
            bufSql.append(" FBEANID          VARCHAR(30),");
            bufSql.append(" FCOMPPARAM       VARCHAR(20)  NOT NULL,");
            bufSql.append(" FRepCode         VARCHAR(20),");
            bufSql.append(" FINDEXDS         CLOB(18M)    LOGGED NOT COMPACT,");
            bufSql.append(" FMEMOYWAY        VARCHAR(20)  NOT NULL,");
            bufSql.append(" FTGTTABLEVIEW    VARCHAR(30),");
            bufSql.append(" FBEFORECOMP      VARCHAR(20)  NOT NULL,");
            bufSql.append(" FFINALCOMP       VARCHAR(20)  NOT NULL,");
            bufSql.append(" FWARNANALYSIS    VARCHAR(500),");
            bufSql.append(" FVIOLATEANALYSIS VARCHAR(500),");
            bufSql.append(" FFORBIDANALYSIS  VARCHAR(500),");
            bufSql.append(" FDESC            VARCHAR(100),");
            bufSql.append(" FCHECKSTATE      DECIMAL(1)   NOT NULL,");
            bufSql.append(" FCREATOR         VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME      VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER       VARCHAR(20),");
            bufSql.append(" FCHECKTIME       VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append("INSERT INTO TB_" + sPre + "_COMP_INDEXCFG(");
            bufSql.append(" FINDEXCFGCODE,");
            bufSql.append(" FINDEXCFGNAME,");
            bufSql.append(" FINDEXTYPE,");
            bufSql.append(" FBEANID,");
            bufSql.append(" FCOMPPARAM,");
            bufSql.append(" FRepCode,");
            bufSql.append(" FINDEXDS,");
            bufSql.append(" FMEMOYWAY,");
            bufSql.append(" FTGTTABLEVIEW,");
            bufSql.append(" FBEFORECOMP,");
            bufSql.append(" FFINALCOMP,");
            bufSql.append(" FWARNANALYSIS,");
            bufSql.append(" FVIOLATEANALYSIS,");
            bufSql.append(" FFORBIDANALYSIS,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FINDEXCFGCODE,");
            bufSql.append(" FINDEXCFGNAME,");
            bufSql.append(" FINDEXTYPE,");
            bufSql.append(" FBEANID,");
            bufSql.append(" FCOMPPARAM,");
            bufSql.append(" FREPCOD,");
            bufSql.append(" FINDEXDS,");
            bufSql.append(" FMEMOYWAY,");
            bufSql.append(" FTGTTABLEVIEW,");
            bufSql.append(" FBEFORECOMP,");
            bufSql.append(" FFINALCOMP,");
            bufSql.append(" FWARNANALYSIS,");
            bufSql.append(" FVIOLATEANALYSIS,");
            bufSql.append(" FFORBIDANALYSIS,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_06132008081125");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            bufSql.delete(0, bufSql.length());

            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_COMP_INDEXCFG ADD CONSTRAINT PK_TB_001NDEXCFG PRIMARY KEY (FINDEXCFGCODE)");
            //----------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003 修改组合群表列名出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //表 Tb_XXX_Rep_Cell 增加 FOtherParams 字段
            dbl.executeSql("alter table Tb_" + sPre +
                           "_Rep_cell add FOtherParams varchar(500)");
            //对表 Tb_XXX_Vch_Project 增加字段 FHandCheck 默认为1，代表手工选项
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_VCH_PROJECT ADD FHANDCHECK DECIMAL(1) DEFAULT 1 NOT NULL ");
            //---------------------------添加综合业务表中的业务日期字段 sj edit 20080701----------------------------------------------
            strPKName = this.getIsNullPKByTableName(pub.yssGetTableName(
                "TB_Data_Integrated")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) { // sj edit 20080701
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_Data_Integrated") +
                               " DROP CONSTRAINT " + strPKName);
                //deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。 sj edit 20080602
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_06272008081808000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_06272008081808000");
            }
            dbl.executeSql("RENAME " + pub.yssGetTableName("TB_DATA_INTEGRATED") +
                           " TO TB_" + sPre + "_DAT_06272008081808000");
            bufSql.delete(0, bufSql.length());
            bufSql.append("CREATE TABLE " +
                          pub.yssGetTableName("TB_DATA_INTEGRATED"));
            bufSql.append(" (");
            bufSql.append("FNUM           VARCHAR(20)  NOT NULL,");
            bufSql.append("FSUBNUM        VARCHAR(20)  NOT NULL,");
            bufSql.append("FINOUTTYPE     DECIMAL(1)     NOT NULL,");
            bufSql.append("FEXCHANGEDATE  DATE          NOT NULL,");
            bufSql.append("FOPERDATE      DATE          NOT NULL,");
            bufSql.append("FSECURITYCODE  VARCHAR(20)  NOT NULL,");
            bufSql.append("FRELANUM       VARCHAR(20)      ,");
            bufSql.append("FNUMTYPE       VARCHAR(20)      ,");
            bufSql.append("FTRADETYPECODE VARCHAR(20)  NOT NULL,");
            bufSql.append("FPORTCODE      VARCHAR(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE1 VARCHAR(10)  NOT NULL,");
            bufSql.append("FANALYSISCODE2 VARCHAR(20)  NOT NULL,");
            bufSql.append("FANALYSISCODE3 VARCHAR(20)  NOT NULL,");
            bufSql.append("FAMOUNT        DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FEXCHANGECOST  DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FMEXCOST       DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FVEXCOST       DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FPORTEXCOST    DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FMPORTEXCOST   DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FVPORTEXCOST   DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FBASEEXCOST    DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FMBASEEXCOST   DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FVBASEEXCOST   DECIMAL(18,4)  NOT NULL,");
            bufSql.append("FBASECURYRATE  DECIMAL(18,15) NOT NULL,");
            bufSql.append("FPORTCURYRATE  DECIMAL(18,15) NOT NULL,");
            bufSql.append("FSECEXDESC     VARCHAR(100)     ,");
            bufSql.append("FDESC          VARCHAR(100)     ,");
            bufSql.append("FCHECKSTATE    DECIMAL(1)     NOT NULL,");
            bufSql.append("FCREATOR       VARCHAR(20)  NOT NULL,");
            bufSql.append("FCHECKUSER     VARCHAR(20)      ,");
            bufSql.append("FCHECKTIME     VARCHAR(20)      ");
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
            bufSql.append("current timestamp,");
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
                           " ADD CONSTRAINT PK_TB_" + sPre + "_EGRATED " +
                           " PRIMARY KEY (FNum,FSUBNUM)");
            //------------------------------------------------------------------------

        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003 增加表字段出错！", ex);
        }
    }

    /**
     * 调整字段的长度
     */
    public void adjustFieldPrecision(String sPre) throws YssException {
        String strPKName;
        Connection conn = null;
        boolean bTrans = false;
        try {
            StringBuffer bufSql = new StringBuffer(5000);
            conn = dbl.loadConnection();
            //---------------------2008.06.16 单亮 修改 Tb_COMP_INDEXCFG FINDEXCFGNAME的长度（原来varchar2（50）修改为100）----------------------//

            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_COMP_INDEXCFG ALTER COLUMN FINDEXCFGNAME SET DATA TYPE VARCHAR(100)");

            //----------------------------------------------------------------------------------------//
            //修改 Tb_XXX_Cash_Command 表的 FDesc 长度 by leeyu
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "TB_CASH_COMMAND"));
            if (strPKName.length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_CASH_COMMAND DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_06172008022014")) {
                this.dropTableByTableName("TB_06172008022014");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_CASH_COMMAND TO TB_06172008022014");
            bufSql.append(" CREATE TABLE TB_" + sPre + "_CASH_COMMAND");
            bufSql.append(" (");
            bufSql.append(" FNUM          VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCOMMANDDATE  DATE           NOT NULL,");
            bufSql.append(" FCOMMANDTIME  VARCHAR(20)    NOT NULL,");
            bufSql.append(" FACCOUNTDATE  DATE           NOT NULL,");
            bufSql.append(" FACCOUNTTIME  VARCHAR(20)    NOT NULL,");
            bufSql.append(" FORDER        DECIMAL(8)     NOT NULL,");
            bufSql.append(" FPAYERNAME    VARCHAR(200)   NOT NULL,");
            bufSql.append(" FPAYERBANK    VARCHAR(100)   NOT NULL,");
            bufSql.append(" FPAYERACCOUNT VARCHAR(100)   NOT NULL,");
            bufSql.append(" FPAYCURY      VARCHAR(20)    NOT NULL,");
            bufSql.append(" FPAYMONEY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append(" FREFRATE      DECIMAL(20,15),");
            bufSql.append(" FRECERNAME    VARCHAR(200)   NOT NULL,");
            bufSql.append(" FRECERBANK    VARCHAR(100)   NOT NULL,");
            bufSql.append(" FRECERACCOUNT VARCHAR(100)   NOT NULL,");
            bufSql.append(" FRECMONEY     DECIMAL(18,4)  NOT NULL,");
            bufSql.append(" FRECCURY      VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCASHUSAGE    VARCHAR(100),");
            bufSql.append(" FRelaNum      VARCHAR(20),");
            bufSql.append(" FNumType      VARCHAR(20),");
            bufSql.append(" FPortCode     VARCHAR(20),");
            bufSql.append(" FDESC         VARCHAR(500),");
            bufSql.append(" FCHECKSTATE   DECIMAL(1)     NOT NULL,");
            bufSql.append(" FCREATOR      VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCREATETIME   VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCHECKUSER    VARCHAR(20),");
            bufSql.append(" FCHECKTIME    VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            bufSql.append(" INSERT INTO TB_" + sPre + "_CASH_COMMAND(");
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
            bufSql.append(" FRelaNum,");
            bufSql.append(" FNumType,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" )");
            bufSql.append(" select ");
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
            bufSql.append(" FRelaNum,");
            bufSql.append(" FNumType,");
            bufSql.append(" FPortCode,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" from TB_06172008022014");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_CASH_COMMAND ADD CONSTRAINT PK_Tb_" +
                           sPre + "_Cash_Com PRIMARY KEY (FNUM)");

            //调整字段类型  BugNo:0000248 edit by jc
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName(
                "TB_COMP_PORTINDEXLINK")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre + "_COMP_PORTINDEXLINK" +
                               " DROP CONSTRAINT " + strPKName);
            }
            if (dbl.yssTableExist("TB_06172008022014")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_06172008022014");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre +
                           "_COMP_PORTINDEXLINK TO TB_06172008022014");
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE TB_" + sPre + "_COMP_PORTINDEXLINK");
            bufSql.append(" (");
            bufSql.append(" FPortCode     VARCHAR(20)  NOT NULL,");
            bufSql.append(" FIndexCfgCode VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCtlGrpCode   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCtlCode      VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCTLVALUE     CLOB(18M)    LOGGED NOT COMPACT,");
            bufSql.append(" FDesc         VARCHAR(100),");
            bufSql.append(" FCHECKSTATE   DECIMAL(1)   NOT NULL,");
            bufSql.append(" FCREATOR      VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER    VARCHAR(20),");
            bufSql.append(" FCHECKTIME    VARCHAR(20)");
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
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT ");
            bufSql.append(" FPortCode,");
            bufSql.append(" FIndexCfgCode,");
            bufSql.append(" FCtlGrpCode,");
            bufSql.append(" FCtlCode,");
            bufSql.append(" CLOB(FCTLVALUE),");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_06172008022014");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_COMP_PORTINDEXLINK ADD CONSTRAINT PK_TB_" + sPre +
                           "_DEXLINK" +
                           " PRIMARY KEY (FPortCode,FIndexCfgCode,FCtlGrpCode,FCtlCode)");
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0003sp1 调整表的字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
