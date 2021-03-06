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
public class DB21010003sp3
    extends BaseDbUpdate {
    public DB21010003sp3() {
    }

    //调整数据 edit by jc
    public void adjustTableData(String sPre) throws YssException {
        try {
            //调整表 Tb_XXX_Vch_Dict 中 FDesc,和 FSubDesc 的数据
            dbl.executeSql("UPDATE TB_" + sPre + "_VCH_DICT set FSUBDESC = FDESC");
            dbl.executeSql("update TB_" + sPre + "_VCH_DICT set FDesc = ''");
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp2 调整表数据出错！", ex);
        }
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //--------------edit by jc
            //很多库版本在1.0.1.0003sp1以上，但是FHandCheck字段却没有添加，故更新时报错
            //这里先判断有无该字段，没有的话则添加之
            if (existsTabColumn_DB2("TB_" + sPre + "_Vch_Project", "FHandCheck")) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_VCH_PROJECT ADD FHANDCHECK DECIMAL(1) DEFAULT 1 NOT NULL ");
            }
            //----------------------jc
            //BugNo:0000304 edit by jc
            //对表Tb_XXX_Vch_Project中调整FExeOrderCode字段的精度
            //通过表名查询主键名
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_Vch_Project");
            //如果有主键规则就删除之
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_Vch_Project") +
                               " DROP CONSTRAINT " + sPKName + " CASCADE");
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_07162008081652000")) {
                this.dropTableByTableName("TB_" + sPre + "_VCH_07162008081652000");
            }
            //把源表重命名为临时表
            dbl.executeSql("RENAME TABLE " +
                           pub.yssGetTableName("TB_Vch_Project") +
                           " TO TB_" + sPre + "_VCH_07162008081652000");
            //首先清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表Tb_XXX_Vch_Project
            bufSql.append(" CREATE TABLE TB_" + sPre + "_Vch_Project");
            bufSql.append(" (");
            bufSql.append(" FProjectCode   VARCHAR(20)    NOT NULL,");
            bufSql.append(" FProjectName   VARCHAR(20)    NOT NULL,");
            bufSql.append(" FExeOrderCode  DECIMAL(3)     NOT NULL,");
            bufSql.append(" FHANDCHECK     DECIMAL(1)     NOT NULL,");
            bufSql.append(" FEXBUILD       DECIMAL(1)     DEFAULT 0 NOT NULL,");
            bufSql.append(" FEXCHECK       DECIMAL(1)     DEFAULT 0 NOT NULL,");
            bufSql.append(" FEXINSERT      DECIMAL(1)     DEFAULT 0 NOT NULL,");
            bufSql.append(" FDESC          VARCHAR(100),");
            bufSql.append(" FCHECKSTATE    DECIMAL(1)     NOT NULL,");
            bufSql.append(" FCREATOR       VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCREATETIME    VARCHAR(20)    NOT NULL,");
            bufSql.append(" FCHECKUSER     VARCHAR(20),");
            bufSql.append(" FCHECKTIME     VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_VCH_PROJECT(");
            bufSql.append(" FPROJECTCODE,");
            bufSql.append(" FPROJECTNAME,");
            bufSql.append(" FEXEORDERCODE,");
            bufSql.append(" FHANDCHECK,");
            bufSql.append(" FEXBUILD,");
            bufSql.append(" FEXCHECK,");
            bufSql.append(" FEXINSERT,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FPROJECTCODE,");
            bufSql.append(" FPROJECTNAME,");
            bufSql.append(" FEXEORDERCODE,");
            bufSql.append(" FHANDCHECK,");
            bufSql.append(" FEXBUILD,");
            bufSql.append(" FEXCHECK,");
            bufSql.append(" FEXINSERT,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_VCH_07162008081652000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_VCH_PROJECT ADD CONSTRAINT PK_TB_" + sPre +
                           "_PROJECT PRIMARY KEY (FPROJECTCODE,FEXEORDERCODE)");
            //-----------------------jc
        } catch (Exception ex) {
            throw new YssException("调整DB21010003sp2出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        String sPKName = "";
        try {
            //交易子表增加 FSettleOrgCode 字段
            if (existsTabColumn_DB2("TB_" + sPre + "_Data_SUBTRADE", "FSettleOrgCode")) {
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_DATA_SUBTRADE");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_DATA_SUBTRADE DROP CONSTRAINT " + sPKName);
                }
                if (dbl.yssTableExist("TB_07292008112614")) {
                    this.dropTableByTableName("TB_07292008112614");
                }
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_DATA_SUBTRADE TO TB_07292008112614");
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_SUBTRADE ");
                bufSql.append(" ( ");
                bufSql.append(
                    " FNUM              VARCHAR(20)                 NOT NULL, ");
                bufSql.append(
                    " FSECURITYCODE     VARCHAR(20)                 NOT NULL, ");
                bufSql.append(" FPORTCODE         VARCHAR(20), ");
                bufSql.append(
                    " FBROKERCODE       VARCHAR(20)                 NOT NULL, ");
                bufSql.append(
                    " FINVMGRCODE       VARCHAR(20)                 NOT NULL, ");
                bufSql.append(
                    " FTRADETYPECODE    VARCHAR(20)                 NOT NULL, ");
                bufSql.append(
                    " FCASHACCCODE      VARCHAR(20)                 NOT NULL, ");
                bufSql.append(" FATTRCLSCODE      VARCHAR(20), ");
                bufSql.append(
                    " FBARGAINDATE      DATE                        NOT NULL, ");
                bufSql.append(
                    " FBARGAINTIME      VARCHAR(20)                 NOT NULL, ");
                bufSql.append(
                    " FSETTLEDATE       DATE                        NOT NULL, ");
                bufSql.append(
                    " FSETTLETIME       VARCHAR(20)                 NOT NULL, ");
                bufSql.append(" FMATUREDATE       DATE, ");
                bufSql.append(" FMATURESETTLEDATE DATE, ");
                bufSql.append(
                    " FFACTCASHACCCODE  VARCHAR(20)                 NOT NULL, ");
                bufSql.append(
                    " FFACTSETTLEMONEY  DECIMAL(18,4)               NOT NULL, ");
                bufSql.append(" FEXRATE           DECIMAL(20,15), ");
                bufSql.append(
                    " FFACTBASERATE     DECIMAL(20,15)              NOT NULL, ");
                bufSql.append(
                    " FFACTPORTRATE     DECIMAL(20,15)              NOT NULL, ");
                bufSql.append(
                    " FAUTOSETTLE       DECIMAL(1)                  NOT NULL, ");
                bufSql.append(
                    " FPORTCURYRATE     DECIMAL(20,15)              NOT NULL, ");
                bufSql.append(
                    " FBASECURYRATE     DECIMAL(20,15)              NOT NULL, ");
                bufSql.append(
                    " FALLOTPROPORTION  DECIMAL(18,8)               NOT NULL, ");
                bufSql.append(
                    " FOLDALLOTAMOUNT   DECIMAL(18,4)               NOT NULL, ");
                bufSql.append(
                    " FALLOTFACTOR      DECIMAL(18,4)               NOT NULL, ");
                bufSql.append(
                    " FTRADEAMOUNT      DECIMAL(18,4)               NOT NULL, ");
                bufSql.append(
                    " FTRADEPRICE       DECIMAL(20,8)               NOT NULL, ");
                bufSql.append(
                    " FTRADEMONEY       DECIMAL(18,4)               NOT NULL, ");
                bufSql.append(" FACCRUEDINTEREST  DECIMAL(18,4), ");
                bufSql.append(" FBAILMONEY        DECIMAL(18,4), ");
                bufSql.append(" FFEECODE1         VARCHAR(20), ");
                bufSql.append(" FTRADEFEE1        DECIMAL(18,4), ");
                bufSql.append(" FFEECODE2         VARCHAR(20), ");
                bufSql.append(" FTRADEFEE2        DECIMAL(18,4), ");
                bufSql.append(" FFEECODE3         VARCHAR(20), ");
                bufSql.append(" FTRADEFEE3        DECIMAL(18,4), ");
                bufSql.append(" FFEECODE4         VARCHAR(20), ");
                bufSql.append(" FTRADEFEE4        DECIMAL(18,4), ");
                bufSql.append(" FFEECODE5         VARCHAR(20), ");
                bufSql.append(" FTRADEFEE5        DECIMAL(18,4), ");
                bufSql.append(" FFEECODE6         VARCHAR(20), ");
                bufSql.append(" FTRADEFEE6        DECIMAL(18,4), ");
                bufSql.append(" FFEECODE7         VARCHAR(20), ");
                bufSql.append(" FTRADEFEE7        DECIMAL(18,4), ");
                bufSql.append(" FFEECODE8         VARCHAR(20), ");
                bufSql.append(" FTRADEFEE8        DECIMAL(18,4), ");
                bufSql.append(" FTOTALCOST        DECIMAL(18,4), ");
                bufSql.append(
                    " FCOST             DECIMAL(18,4)  DEFAULT 0	NOT NULL, ");
                bufSql.append(
                    " FMCOST            DECIMAL(18,4)  DEFAULT 0 	NOT NULL, ");
                bufSql.append(
                    " FVCOST            DECIMAL(18,4)  DEFAULT 0 	NOT NULL, ");
                bufSql.append(
                    " FBASECURYCOST     DECIMAL(18,4)  DEFAULT 0 	NOT NULL, ");
                bufSql.append(
                    " FMBASECURYCOST    DECIMAL(18,4)  DEFAULT 0 	NOT NULL, ");
                bufSql.append(
                    " FVBASECURYCOST    DECIMAL(18,4)  DEFAULT 0 	NOT NULL, ");
                bufSql.append(
                    " FPORTCURYCOST     DECIMAL(18,4)  DEFAULT 0 	NOT NULL, ");
                bufSql.append(
                    " FMPORTCURYCOST    DECIMAL(18,4)  DEFAULT 0 	NOT NULL, ");
                bufSql.append(
                    " FVPORTCURYCOST    DECIMAL(18,4)  DEFAULT 0 	NOT NULL, ");
                bufSql.append(
                    " FSETTLESTATE      DECIMAL(1)     DEFAULT 0 	NOT NULL, ");
                bufSql.append(" FFACTSETTLEDATE   DATE, ");
                bufSql.append(" FSETTLEDESC       VARCHAR(100), ");
                bufSql.append(" FORDERNUM         VARCHAR(20), ");
                bufSql.append(
                    " FDATASOURCE       DECIMAL(1)               	NOT NULL, ");
                bufSql.append(" FSETTLEORGCODE    VARCHAR(20), ");
                bufSql.append(" FDESC             VARCHAR(100), ");
                bufSql.append(
                    " FCHECKSTATE       DECIMAL(1)               	NOT NULL, ");
                bufSql.append(
                    " FCREATOR          VARCHAR(20)              	NOT NULL, ");
                bufSql.append(
                    " FCREATETIME       VARCHAR(20)              	NOT NULL, ");
                bufSql.append(" FCHECKUSER        VARCHAR(20), ");
                bufSql.append(" FCHECKTIME        VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_SUBTRADE( ");
                bufSql.append(" FNUM, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FBROKERCODE, ");
                bufSql.append(" FINVMGRCODE, ");
                bufSql.append(" FTRADETYPECODE, ");
                bufSql.append(" FCASHACCCODE, ");
                bufSql.append(" FATTRCLSCODE, ");
                bufSql.append(" FBARGAINDATE, ");
                bufSql.append(" FBARGAINTIME, ");
                bufSql.append(" FSETTLEDATE, ");
                bufSql.append(" FSETTLETIME, ");
                bufSql.append(" FMATUREDATE, ");
                bufSql.append(" FMATURESETTLEDATE, ");
                bufSql.append(" FFACTCASHACCCODE, ");
                bufSql.append(" FFACTSETTLEMONEY, ");
                bufSql.append(" FEXRATE, ");
                bufSql.append(" FFACTBASERATE, ");
                bufSql.append(" FFACTPORTRATE, ");
                bufSql.append(" FAUTOSETTLE, ");
                bufSql.append(" FPORTCURYRATE, ");
                bufSql.append(" FBASECURYRATE, ");
                bufSql.append(" FALLOTPROPORTION, ");
                bufSql.append(" FOLDALLOTAMOUNT, ");
                bufSql.append(" FALLOTFACTOR, ");
                bufSql.append(" FTRADEAMOUNT, ");
                bufSql.append(" FTRADEPRICE, ");
                bufSql.append(" FTRADEMONEY, ");
                bufSql.append(" FACCRUEDINTEREST, ");
                bufSql.append(" FBAILMONEY, ");
                bufSql.append(" FFEECODE1, ");
                bufSql.append(" FTRADEFEE1, ");
                bufSql.append(" FFEECODE2, ");
                bufSql.append(" FTRADEFEE2, ");
                bufSql.append(" FFEECODE3, ");
                bufSql.append(" FTRADEFEE3, ");
                bufSql.append(" FFEECODE4, ");
                bufSql.append(" FTRADEFEE4, ");
                bufSql.append(" FFEECODE5, ");
                bufSql.append(" FTRADEFEE5, ");
                bufSql.append(" FFEECODE6, ");
                bufSql.append(" FTRADEFEE6, ");
                bufSql.append(" FFEECODE7, ");
                bufSql.append(" FTRADEFEE7, ");
                bufSql.append(" FFEECODE8, ");
                bufSql.append(" FTRADEFEE8, ");
                bufSql.append(" FTOTALCOST, ");
                bufSql.append(" FCOST, ");
                bufSql.append(" FMCOST, ");
                bufSql.append(" FVCOST, ");
                bufSql.append(" FBASECURYCOST, ");
                bufSql.append(" FMBASECURYCOST, ");
                bufSql.append(" FVBASECURYCOST, ");
                bufSql.append(" FPORTCURYCOST, ");
                bufSql.append(" FMPORTCURYCOST, ");
                bufSql.append(" FVPORTCURYCOST, ");
                bufSql.append(" FSETTLESTATE, ");
                bufSql.append(" FFACTSETTLEDATE, ");
                bufSql.append(" FSETTLEDESC, ");
                bufSql.append(" FORDERNUM, ");
                bufSql.append(" FDATASOURCE, ");
                bufSql.append(" FSETTLEORGCODE, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FNUM, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FBROKERCODE, ");
                bufSql.append(" FINVMGRCODE, ");
                bufSql.append(" FTRADETYPECODE, ");
                bufSql.append(" FCASHACCCODE, ");
                bufSql.append(" FATTRCLSCODE, ");
                bufSql.append(" FBARGAINDATE, ");
                bufSql.append(" FBARGAINTIME, ");
                bufSql.append(" FSETTLEDATE, ");
                bufSql.append(" FSETTLETIME, ");
                bufSql.append(" FMATUREDATE, ");
                bufSql.append(" FMATURESETTLEDATE, ");
                bufSql.append(" FFACTCASHACCCODE, ");
                bufSql.append(" FFACTSETTLEMONEY, ");
                bufSql.append(" FEXRATE, ");
                bufSql.append(" FFACTBASERATE, ");
                bufSql.append(" FFACTPORTRATE, ");
                bufSql.append(" FAUTOSETTLE, ");
                bufSql.append(" FPORTCURYRATE, ");
                bufSql.append(" FBASECURYRATE, ");
                bufSql.append(" FALLOTPROPORTION, ");
                bufSql.append(" FOLDALLOTAMOUNT, ");
                bufSql.append(" FALLOTFACTOR, ");
                bufSql.append(" FTRADEAMOUNT, ");
                bufSql.append(" FTRADEPRICE, ");
                bufSql.append(" FTRADEMONEY, ");
                bufSql.append(" FACCRUEDINTEREST, ");
                bufSql.append(" FBAILMONEY, ");
                bufSql.append(" FFEECODE1, ");
                bufSql.append(" FTRADEFEE1, ");
                bufSql.append(" FFEECODE2, ");
                bufSql.append(" FTRADEFEE2, ");
                bufSql.append(" FFEECODE3, ");
                bufSql.append(" FTRADEFEE3, ");
                bufSql.append(" FFEECODE4, ");
                bufSql.append(" FTRADEFEE4, ");
                bufSql.append(" FFEECODE5, ");
                bufSql.append(" FTRADEFEE5, ");
                bufSql.append(" FFEECODE6, ");
                bufSql.append(" FTRADEFEE6, ");
                bufSql.append(" FFEECODE7, ");
                bufSql.append(" FTRADEFEE7, ");
                bufSql.append(" FFEECODE8, ");
                bufSql.append(" FTRADEFEE8, ");
                bufSql.append(" FTOTALCOST, ");
                bufSql.append(" FCOST, ");
                bufSql.append(" FMCOST, ");
                bufSql.append(" FVCOST, ");
                bufSql.append(" FBASECURYCOST, ");
                bufSql.append(" FMBASECURYCOST, ");
                bufSql.append(" FVBASECURYCOST, ");
                bufSql.append(" FPORTCURYCOST, ");
                bufSql.append(" FMPORTCURYCOST, ");
                bufSql.append(" FVPORTCURYCOST, ");
                bufSql.append(" FSETTLESTATE, ");
                bufSql.append(" FFACTSETTLEDATE, ");
                bufSql.append(" FSETTLEDESC, ");
                bufSql.append(" FORDERNUM, ");
                bufSql.append(" FDATASOURCE, ");
                bufSql.append(" ' ', ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_07292008112614 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_SUBTRADE ADD CONSTRAINT PK_TB_" + sPre +
                               "_UBTRADE PRIMARY KEY (FNUM)");
            }
        } catch (Exception ex) {
            throw new YssException("DB21010003sp3新增字段出错", ex);
        }
    }
}
