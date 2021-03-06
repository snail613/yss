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
public class DB21010003sp7
    extends BaseDbUpdate {
    public DB21010003sp7() {
    }

    //添加表字段
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //建行库交易子表中无此字段，插入数据时报错
            //故先判断该字段是否存在，不存在就先添加上
            if (existsTabColumn_DB2("TB_" + sPre + "_Data_SubTrade", "FSETTLEORGCODE")) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_SubTrade ADD FSETTLEORGCODE VARCHAR(20) ");
            }
            //BugNo:0000447 edit by jc
            //对表 Tb_XXX_Data_SubTrade 增加字段 FDataBirth
            if (existsTabColumn_DB2("TB_" + sPre + "_Data_SubTrade",
                                    "FSecurityShortName,FSecurityCorpName")) {
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_Data_SubTrade");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_Data_SubTrade DROP CONSTRAINT " + sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_DAT_09012008112237000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_DAT_09012008112237000");
                }
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_Data_SubTrade  TO TB_" + sPre +
                               "_DAT_09012008112237000");
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_SUBTRADE ");
                bufSql.append(" ( ");
                bufSql.append(" FNUM              VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FSECURITYCODE     VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FPORTCODE         VARCHAR(20), ");
                bufSql.append(" FBROKERCODE       VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FINVMGRCODE       VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FTRADETYPECODE    VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCASHACCCODE      VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FATTRCLSCODE      VARCHAR(20), ");
                bufSql.append(" FBARGAINDATE      DATE           NOT NULL, ");
                bufSql.append(" FBARGAINTIME      VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FSETTLEDATE       DATE           NOT NULL, ");
                bufSql.append(" FSETTLETIME       VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FMATUREDATE       DATE, ");
                bufSql.append(" FMATURESETTLEDATE DATE, ");
                bufSql.append(" FFACTCASHACCCODE  VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FFACTSETTLEMONEY  DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FEXRATE           DECIMAL(20,15), ");
                bufSql.append(" FFACTBASERATE     DECIMAL(20,15) NOT NULL, ");
                bufSql.append(" FFACTPORTRATE     DECIMAL(20,15) NOT NULL, ");
                bufSql.append(" FAUTOSETTLE       DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FPORTCURYRATE     DECIMAL(20,15) NOT NULL, ");
                bufSql.append(" FBASECURYRATE     DECIMAL(20,15) NOT NULL, ");
                bufSql.append(" FALLOTPROPORTION  DECIMAL(18,8)  NOT NULL, ");
                bufSql.append(" FOLDALLOTAMOUNT   DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FALLOTFACTOR      DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FTRADEAMOUNT      DECIMAL(18,4)  NOT NULL, ");
                bufSql.append(" FTRADEPRICE       DECIMAL(20,8)  NOT NULL, ");
                bufSql.append(" FTRADEMONEY       DECIMAL(18,4)  NOT NULL, ");
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
                    " FCOST             DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FMCOST            DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FVCOST            DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FBASECURYCOST     DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FMBASECURYCOST    DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FVBASECURYCOST    DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FPORTCURYCOST     DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FMPORTCURYCOST    DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FVPORTCURYCOST    DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FSETTLESTATE      DECIMAL(1)     DEFAULT 0 NOT NULL, ");
                bufSql.append(" FFACTSETTLEDATE   DATE, ");
                bufSql.append(" FSETTLEDESC       VARCHAR(100), ");
                bufSql.append(" FORDERNUM         VARCHAR(20), ");
                bufSql.append(" FDATASOURCE       DECIMAL  (1)   NOT NULL, ");
                bufSql.append(" FDATABIRTH        VARCHAR(20), ");
                bufSql.append(" FSETTLEORGCODE    VARCHAR(20), ");
                bufSql.append(" FDESC             VARCHAR(100), ");
                bufSql.append(" FCHECKSTATE       DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FCREATOR          VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCREATETIME       VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FCHECKUSER        VARCHAR(20), ");
                bufSql.append(" FCHECKTIME        VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                //清空字符流
                bufSql.delete(0, bufSql.length());
                //插入数据
                bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_SUBTRADE(");
                bufSql.append(" FNUM,");
                bufSql.append(" FSECURITYCODE,");
                bufSql.append(" FPORTCODE,");
                bufSql.append(" FBROKERCODE,");
                bufSql.append(" FINVMGRCODE,");
                bufSql.append(" FTRADETYPECODE,");
                bufSql.append(" FCASHACCCODE,");
                bufSql.append(" FATTRCLSCODE,");
                bufSql.append(" FBARGAINDATE,");
                bufSql.append(" FBARGAINTIME,");
                bufSql.append(" FSETTLEDATE,");
                bufSql.append(" FSETTLETIME,");
                bufSql.append(" FMATUREDATE,");
                bufSql.append(" FMATURESETTLEDATE,");
                bufSql.append(" FFACTCASHACCCODE,");
                bufSql.append(" FFACTSETTLEMONEY,");
                bufSql.append(" FEXRATE,");
                bufSql.append(" FFACTBASERATE,");
                bufSql.append(" FFACTPORTRATE,");
                bufSql.append(" FAUTOSETTLE,");
                bufSql.append(" FPORTCURYRATE,");
                bufSql.append(" FBASECURYRATE,");
                bufSql.append(" FALLOTPROPORTION,");
                bufSql.append(" FOLDALLOTAMOUNT,");
                bufSql.append(" FALLOTFACTOR,");
                bufSql.append(" FTRADEAMOUNT,");
                bufSql.append(" FTRADEPRICE,");
                bufSql.append(" FTRADEMONEY,");
                bufSql.append(" FACCRUEDINTEREST,");
                bufSql.append(" FBAILMONEY,");
                bufSql.append(" FFEECODE1,");
                bufSql.append(" FTRADEFEE1,");
                bufSql.append(" FFEECODE2,");
                bufSql.append(" FTRADEFEE2,");
                bufSql.append(" FFEECODE3,");
                bufSql.append(" FTRADEFEE3,");
                bufSql.append(" FFEECODE4,");
                bufSql.append(" FTRADEFEE4,");
                bufSql.append(" FFEECODE5,");
                bufSql.append(" FTRADEFEE5,");
                bufSql.append(" FFEECODE6,");
                bufSql.append(" FTRADEFEE6,");
                bufSql.append(" FFEECODE7,");
                bufSql.append(" FTRADEFEE7,");
                bufSql.append(" FFEECODE8,");
                bufSql.append(" FTRADEFEE8,");
                bufSql.append(" FTOTALCOST,");
                bufSql.append(" FCOST,");
                bufSql.append(" FMCOST,");
                bufSql.append(" FVCOST,");
                bufSql.append(" FBASECURYCOST,");
                bufSql.append(" FMBASECURYCOST,");
                bufSql.append(" FVBASECURYCOST,");
                bufSql.append(" FPORTCURYCOST,");
                bufSql.append(" FMPORTCURYCOST,");
                bufSql.append(" FVPORTCURYCOST,");
                bufSql.append(" FSETTLESTATE,");
                bufSql.append(" FFACTSETTLEDATE,");
                bufSql.append(" FSETTLEDESC,");
                bufSql.append(" FORDERNUM,");
                bufSql.append(" FDATASOURCE,");
                bufSql.append(" FDATABIRTH,");
                bufSql.append(" FSETTLEORGCODE,");
                bufSql.append(" FDESC,");
                bufSql.append(" FCHECKSTATE,");
                bufSql.append(" FCREATOR,");
                bufSql.append(" FCREATETIME,");
                bufSql.append(" FCHECKUSER,");
                bufSql.append(" FCHECKTIME");
                bufSql.append(" )");
                bufSql.append(" SELECT");
                bufSql.append(" FNUM,");
                bufSql.append(" FSECURITYCODE,");
                bufSql.append(" FPORTCODE,");
                bufSql.append(" FBROKERCODE,");
                bufSql.append(" FINVMGRCODE,");
                bufSql.append(" FTRADETYPECODE,");
                bufSql.append(" FCASHACCCODE,");
                bufSql.append(" FATTRCLSCODE,");
                bufSql.append(" FBARGAINDATE,");
                bufSql.append(" FBARGAINTIME,");
                bufSql.append(" FSETTLEDATE,");
                bufSql.append(" FSETTLETIME,");
                bufSql.append(" FMATUREDATE,");
                bufSql.append(" FMATURESETTLEDATE,");
                bufSql.append(" FFACTCASHACCCODE,");
                bufSql.append(" FFACTSETTLEMONEY,");
                bufSql.append(" FEXRATE,");
                bufSql.append(" FFACTBASERATE,");
                bufSql.append(" FFACTPORTRATE,");
                bufSql.append(" FAUTOSETTLE,");
                bufSql.append(" FPORTCURYRATE,");
                bufSql.append(" FBASECURYRATE,");
                bufSql.append(" FALLOTPROPORTION,");
                bufSql.append(" FOLDALLOTAMOUNT,");
                bufSql.append(" FALLOTFACTOR,");
                bufSql.append(" FTRADEAMOUNT,");
                bufSql.append(" FTRADEPRICE,");
                bufSql.append(" FTRADEMONEY,");
                bufSql.append(" FACCRUEDINTEREST,");
                bufSql.append(" FBAILMONEY,");
                bufSql.append(" FFEECODE1,");
                bufSql.append(" FTRADEFEE1,");
                bufSql.append(" FFEECODE2,");
                bufSql.append(" FTRADEFEE2,");
                bufSql.append(" FFEECODE3,");
                bufSql.append(" FTRADEFEE3,");
                bufSql.append(" FFEECODE4,");
                bufSql.append(" FTRADEFEE4,");
                bufSql.append(" FFEECODE5,");
                bufSql.append(" FTRADEFEE5,");
                bufSql.append(" FFEECODE6,");
                bufSql.append(" FTRADEFEE6,");
                bufSql.append(" FFEECODE7,");
                bufSql.append(" FTRADEFEE7,");
                bufSql.append(" FFEECODE8,");
                bufSql.append(" FTRADEFEE8,");
                bufSql.append(" FTOTALCOST,");
                bufSql.append(" FCOST,");
                bufSql.append(" FMCOST,");
                bufSql.append(" FVCOST,");
                bufSql.append(" FBASECURYCOST,");
                bufSql.append(" FMBASECURYCOST,");
                bufSql.append(" FVBASECURYCOST,");
                bufSql.append(" FPORTCURYCOST,");
                bufSql.append(" FMPORTCURYCOST,");
                bufSql.append(" FVPORTCURYCOST,");
                bufSql.append(" FSETTLESTATE,");
                bufSql.append(" FFACTSETTLEDATE,");
                bufSql.append(" FSETTLEDESC,");
                bufSql.append(" FORDERNUM,");
                bufSql.append(" FDATASOURCE,");
                bufSql.append(" ' ',");
                bufSql.append(" FSETTLEORGCODE,");
                bufSql.append(" FDESC,");
                bufSql.append(" FCHECKSTATE,");
                bufSql.append(" FCREATOR,");
                bufSql.append(" FCREATETIME,");
                bufSql.append(" FCHECKUSER,");
                bufSql.append(" FCHECKTIME");
                bufSql.append(" FROM TB_" + sPre + "_DAT_09012008112237000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                //添加主键约束
                dbl.executeSql(" ALTER TABLE TB_" + sPre +
                               "_DATA_SUBTRADE ADD CONSTRAINT PK_TB_" + sPre +
                               "_UBTRADE PRIMARY KEY (FNUM) ");
                //-------------------jc
            }
        } catch (Exception ex) {
            throw new YssException("版本DB21010003sp7添加表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //调整表字段
    public void adjustFieldPrecision(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //BugNo:0000435 edit by jc
            //更改表TB_XXX_Dao_FileName中字段FFIleNameConent的长度
            //通过表名查询主键名
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre + "_Dao_FileName");
            //如果有主键规则就删除主键规则
            if (sPKName.trim().length() != 0) {
                dbl.executeSql(" ALTER TABLE TB_" + sPre + "_Dao_FileName" +
                               " DROP CONSTRAINT " + sPKName);
            }
            //判断临时表是否存在，如果存在，删除临时表
            if (dbl.yssTableExist("TB_" + sPre + "_DAO_08282008082820080")) {
                this.dropTableByTableName("TB_" + sPre + "_DAO_08282008082820080");
            }
            //把源表重命名为临时表
            dbl.executeSql(" RENAME TABLE TB_" + sPre + "_Dao_FileName" +
                           " TO TB_" + sPre + "_DAO_08282008082820080");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表TB_xxx_Dao_FileName
            bufSql.append(" CREATE TABLE TB_" + sPre + "_Dao_FileName");
            bufSql.append(" (");
            bufSql.append(" FCUSCFGCODE      VARCHAR(20)      NOT NULL,");
            bufSql.append(" FORDERNUM        DECIMAL(3)       NOT NULL,");
            bufSql.append(" FFILENAMECONENT  VARCHAR(300),");
            bufSql.append(" FVALUETYPE       DECIMAL(1)       NOT NULL,");
            bufSql.append(" FFILENAMECLS     VARCHAR(20),");
            bufSql.append(" FFILENAMEDICT    VARCHAR(20),");
            bufSql.append(" FTABFEILD        VARCHAR(30),");
            bufSql.append(" FCREATOR         VARCHAR(20)      NOT NULL,");
            bufSql.append(" FCREATETIME      VARCHAR(20)      NOT NULL,");
            bufSql.append(" FCHECKUSER       VARCHAR(20),");
            bufSql.append(" FCHECKTIME       VARCHAR(20),");
            bufSql.append(" FDESC            VARCHAR(100),");
            bufSql.append(" FCHECKSTATE      DECIMAL(1)       NOT NULL,");
            bufSql.append(" FFORMAT          VARCHAR(30)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_Dao_FileName(");
            bufSql.append(" FCUSCFGCODE,");
            bufSql.append(" FORDERNUM,");
            bufSql.append(" FFILENAMECONENT,");
            bufSql.append(" FVALUETYPE,");
            bufSql.append(" FFILENAMECLS,");
            bufSql.append(" FFILENAMEDICT,");
            bufSql.append(" FTABFEILD,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FFORMAT");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FCUSCFGCODE,");
            bufSql.append(" FORDERNUM,");
            bufSql.append(" FFILENAMECONENT,");
            bufSql.append(" FVALUETYPE,");
            bufSql.append(" FFILENAMECLS,");
            bufSql.append(" FFILENAMEDICT,");
            bufSql.append(" FTABFEILD,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME,");
            bufSql.append(" FDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FFORMAT");
            bufSql.append(" FROM TB_" + sPre + "_DAO_08282008082820080");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql(" ALTER TABLE TB_" + sPre +
                           "_Dao_FileName  ADD CONSTRAINT PK_TB_" + sPre +
                           "_ileName  PRIMARY KEY (FCUSCFGCODE, FORDERNUM)");
            //----------------------jc

            //BugNo:0000449 edit by jc
            //对表Tb_XXX_VCH_DATAENTITY中调整FAssistant字段可以为空
            //通过表名查询主键名
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_VCH_DATAENTITY");
            //如果有主键规则就删除之
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_VCH_DATAENTITY") +
                               " DROP CONSTRAINT " + sPKName);
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_05162008081525087")) {
                this.dropTableByTableName("TB_" + sPre + "_VCH_05162008081525087");
            }
            //把源表重命名为临时表
            dbl.executeSql("RENAME TABLE " +
                           pub.yssGetTableName("TB_VCH_DATAENTITY") +
                           " TO TB_" + sPre + "_VCH_05162008081525087");
            //首先清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表TB_XXX_VCH_DATAENTITY
            bufSql.append(" CREATE TABLE TB_" + sPre + "_VCH_DATAENTITY");
            bufSql.append(" (");
            bufSql.append(" FVchNum       VARCHAR(20)    NOT NULL,");
            bufSql.append(" FEntityNum    VARCHAR(20)    NOT NULL,");
            bufSql.append(" FSubjectCode  VARCHAR(50)    NOT NULL,");
            bufSql.append(" FCuryRate     DECIMAL(18,12) NOT NULL,");
            bufSql.append(" FResume       VARCHAR(50),");
            bufSql.append(" FDCWay        VARCHAR(20)    NOT NULL,");
            bufSql.append(" FBookSetCode  VARCHAR(20)    NOT NULL,");
            bufSql.append(" FBal          DECIMAL(18,4)  NOT NULL,");
            bufSql.append(" FSetBal       DECIMAL(18,4)  NOT NULL,");
            bufSql.append(" FAmount       DECIMAL(18,4),");
            bufSql.append(" FPrice        DECIMAL(20,8),");
            bufSql.append(" FAssistant    VARCHAR(20),");
            bufSql.append(" FDesc         VARCHAR(100),");
            bufSql.append(" FCheckState   DECIMAL(1)    NOT NULL,");
            bufSql.append(" FCREATOR      VARCHAR(20)   NOT NULL,");
            bufSql.append(" FCREATETIME   VARCHAR(20)   NOT NULL,");
            bufSql.append(" FCHECKUSER    VARCHAR(20),");
            bufSql.append(" FCHECKTIME    VARCHAR(20)");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_VCH_DATAENTITY(");
            bufSql.append(" FVchNum,");
            bufSql.append(" FEntityNum,");
            bufSql.append(" FSubjectCode,");
            bufSql.append(" FCuryRate,");
            bufSql.append(" FResume,");
            bufSql.append(" FDCWay,");
            bufSql.append(" FBookSetCode,");
            bufSql.append(" FBal,");
            bufSql.append(" FSetBal,");
            bufSql.append(" FAmount,");
            bufSql.append(" FPrice,");
            bufSql.append(" FAssistant,");
            bufSql.append(" FDesc,");
            bufSql.append(" FCheckState,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FVchNum,");
            bufSql.append(" FEntityNum,");
            bufSql.append(" FSubjectCode,");
            bufSql.append(" FCuryRate,");
            bufSql.append(" FResume,");
            bufSql.append(" FDCWay,");
            bufSql.append(" FBookSetCode,");
            bufSql.append(" FBal,");
            bufSql.append(" FSetBal,");
            bufSql.append(" FAmount,");
            bufSql.append(" FPrice,");
            bufSql.append(" FAssistant,");
            bufSql.append(" FDesc,");
            bufSql.append(" FCheckState,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_VCH_05162008081525087");

            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_VCH_DATAENTITY ADD CONSTRAINT PK_TB_" + sPre +
                           "_AENTITY PRIMARY KEY (FVchNum,FEntityNum)");
            //-----------------------jc

        } catch (Exception ex) {
            throw new YssException("版本DB21010003sp7调整表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
