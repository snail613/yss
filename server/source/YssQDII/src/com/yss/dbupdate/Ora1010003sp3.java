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
public class Ora1010003sp3
    extends BaseDbUpdate {
    public Ora1010003sp3() {
    }

    //调整数据 edit by jc
    public void adjustTableData(String sPre) throws YssException {
        try {
            //调整表 Tb_XXX_Vch_Dict 中 FDesc,和 FSubDesc 的数据
            dbl.executeSql("UPDATE TB_" + sPre + "_VCH_DICT set FSUBDESC = FDESC");
            dbl.executeSql("update TB_" + sPre + "_VCH_DICT set FDesc = ''");
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp3 调整表数据出错！", ex);
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
            if (existsTabColumn_Ora("TB_" + sPre + "_Vch_Project", "FHandCheck")) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_VCH_PROJECT ADD FHANDCHECK NUMBER(1) DEFAULT 1 NOT NULL ");
            }
            //----------------------jc
            //BugNo:0000304 edit by jc
            //对表Tb_XXX_Vch_Project中调整FExeOrderCode字段的精度
            //通过表名查询主键名
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Vch_Project");
            //如果有主键规则就删除之
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_Vch_Project") +
                               " DROP CONSTRAINT " + sPKName + " CASCADE");
                deleteIndex(sPKName); //若是没有级联删除索引，则手工删除
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_07162008081652000")) {
                this.dropTableByTableName("TB_" + sPre + "_VCH_07162008081652000");
            }
            //把源表重命名为临时表
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_Vch_Project") +
                           " RENAME TO TB_" + sPre + "_VCH_07162008081652000");
            //首先清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表Tb_XXX_Vch_Project
            bufSql.append(" CREATE TABLE TB_" + sPre + "_Vch_Project");
            bufSql.append(" (");
            bufSql.append(" FProjectCode   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FProjectName   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FExeOrderCode  NUMBER(3)     NOT NULL,");
            bufSql.append(" FHANDCHECK     NUMBER(1)     DEFAULT 1 NOT NULL,");
            bufSql.append(" FEXBUILD       NUMBER(1)     DEFAULT 0 NOT NULL,");
            bufSql.append(" FEXCHECK       NUMBER(1)     DEFAULT 0 NOT NULL,");
            bufSql.append(" FEXINSERT      NUMBER(1)     DEFAULT 0 NOT NULL,");
            bufSql.append(" FDESC          VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE    NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR       VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER     VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME     VARCHAR2(20)      NULL");
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
                           "_Vch_Project PRIMARY KEY (FPROJECTCODE,FEXEORDERCODE)");
            //-----------------------jc
        } catch (Exception ex) {
            throw new YssException("调整ORacle1010003sp3出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            // 将交易子表中增加 FSettleOrgCode 字段
            if (existsTabColumn_Ora("Tb_" + sPre + "_Data_SubTrade", "FSettleOrgCode")) {
                sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_Data_SubTrade");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_Data_SubTrade DROP CONSTRAINT " + sPKName + " CASCADE");
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "DAT_07292008112614000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "DAT_07292008112614000");
                }
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_SUBTRADE RENAME TO TB_" + sPre +
                               "DAT_07292008112614000");
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_SUBTRADE ");
                bufSql.append(" ( ");
                bufSql.append(" FNUM              VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FSECURITYCODE     VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FPORTCODE         VARCHAR2(20)      NULL, ");
                bufSql.append(" FBROKERCODE       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FINVMGRCODE       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FTRADETYPECODE    VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCASHACCCODE      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FATTRCLSCODE      VARCHAR2(20)      NULL, ");
                bufSql.append(" FBARGAINDATE      DATE          NOT NULL, ");
                bufSql.append(" FBARGAINTIME      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FSETTLEDATE       DATE          NOT NULL, ");
                bufSql.append(" FSETTLETIME       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FMATUREDATE       DATE              NULL, ");
                bufSql.append(" FMATURESETTLEDATE DATE              NULL, ");
                bufSql.append(" FFACTCASHACCCODE  VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FFACTSETTLEMONEY  NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FEXRATE           NUMBER(20,15)     NULL, ");
                bufSql.append(" FFACTBASERATE     NUMBER(20,15) NOT NULL, ");
                bufSql.append(" FFACTPORTRATE     NUMBER(20,15) NOT NULL, ");
                bufSql.append(" FAUTOSETTLE       NUMBER(1)     NOT NULL, ");
                bufSql.append(" FPORTCURYRATE     NUMBER(20,15) NOT NULL, ");
                bufSql.append(" FBASECURYRATE     NUMBER(20,15) NOT NULL, ");
                bufSql.append(" FALLOTPROPORTION  NUMBER(18,8)  NOT NULL, ");
                bufSql.append(" FOLDALLOTAMOUNT   NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FALLOTFACTOR      NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FTRADEAMOUNT      NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FTRADEPRICE       NUMBER(20,8)  NOT NULL, ");
                bufSql.append(" FTRADEMONEY       NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FACCRUEDINTEREST  NUMBER(18,4)      NULL, ");
                bufSql.append(" FBAILMONEY        NUMBER(18,4)      NULL, ");
                bufSql.append(" FFEECODE1         VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADEFEE1        NUMBER(18,4)      NULL, ");
                bufSql.append(" FFEECODE2         VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADEFEE2        NUMBER(18,4)      NULL, ");
                bufSql.append(" FFEECODE3         VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADEFEE3        NUMBER(18,4)      NULL, ");
                bufSql.append(" FFEECODE4         VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADEFEE4        NUMBER(18,4)      NULL, ");
                bufSql.append(" FFEECODE5         VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADEFEE5        NUMBER(18,4)      NULL, ");
                bufSql.append(" FFEECODE6         VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADEFEE6        NUMBER(18,4)      NULL, ");
                bufSql.append(" FFEECODE7         VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADEFEE7        NUMBER(18,4)      NULL, ");
                bufSql.append(" FFEECODE8         VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADEFEE8        NUMBER(18,4)      NULL, ");
                bufSql.append(" FTOTALCOST        NUMBER(18,4)      NULL, ");
                bufSql.append(
                    " FCOST             NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FMCOST            NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FVCOST            NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FBASECURYCOST     NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FMBASECURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FVBASECURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FPORTCURYCOST     NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FMPORTCURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FVPORTCURYCOST    NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FSETTLESTATE      NUMBER(1)     DEFAULT 0 NOT NULL, ");
                bufSql.append(" FFACTSETTLEDATE   DATE              NULL, ");
                bufSql.append(" FSETTLEDESC       VARCHAR2(100)     NULL, ");
                bufSql.append(" FORDERNUM         VARCHAR2(20)      NULL, ");
                bufSql.append(" FDATASOURCE       NUMBER(1)     NOT NULL, ");
                bufSql.append(" FSETTLEORGCODE    VARCHAR2(20)      NULL, ");
                bufSql.append(" FDESC             VARCHAR2(100)     NULL, ");
                bufSql.append(" FCHECKSTATE       NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR          VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER        VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME        VARCHAR2(20)      NULL ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.delete(0, bufSql.length());
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
                bufSql.append(" NULL, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_" + sPre + "DAT_07292008112614000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_SUBTRADE ADD CONSTRAINT PK_TB_" + sPre +
                               "_DATA_SUBTRADE PRIMARY KEY (FNUM)");
            }
        } catch (Exception ex) {
            throw new YssException("Oracle1010003sp3中增加字段数据出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
