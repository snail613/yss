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
public class Ora1010003sp7
    extends BaseDbUpdate {
    public Ora1010003sp7() {
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
            if (existsTabColumn_Ora("TB_" + sPre + "_Data_SubTrade", "FSETTLEORGCODE")) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_SubTrade ADD FSETTLEORGCODE VARCHAR2(20) NULL ");
            }
            //BugNo:0000447 edit by jc
            //对表 Tb_XXX_Data_SubTrade 增加字段 FDataBirth
            if (existsTabColumn_Ora("TB_" + sPre + "_Data_SubTrade",
                                    "FSecurityShortName,FSecurityCorpName")) {
                sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_Data_SubTrade");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_Data_SubTrade DROP CONSTRAINT " + sPKName +
                                   " CASCADE");
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_DAT_09012008112237000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_DAT_09012008112237000");
                }
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Data_SubTrade RENAME TO TB_" + sPre +
                               "_DAT_09012008112237000");
                bufSql.delete(0, bufSql.length());
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
                bufSql.append(" FDATABIRTH        VARCHAR2(20)      NULL, ");
                bufSql.append(" FSETTLEORGCODE    VARCHAR2(20)      NULL, ");
                bufSql.append(" FDESC             VARCHAR2(100)     NULL, ");
                bufSql.append(" FCHECKSTATE       NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR          VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER        VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME        VARCHAR2(20)      NULL ");
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
                bufSql.append(" NULL,");
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
                               "_DATA_SUBTRADE PRIMARY KEY (FNUM) ");
                //-------------------jc
            }
        } catch (Exception ex) {
            throw new YssException("版本Oracle1010003sp7添加表字段出错", ex);
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
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Dao_FileName");
            //如果有主键规则就删除主键规则
            if (sPKName.trim().length() != 0) {
                dbl.executeSql(" ALTER TABLE TB_" + sPre + "_Dao_FileName" +
                               " DROP CONSTRAINT " + sPKName + " CASCADE");
                deleteIndex(sPKName); //若是没有级联删除索引，则手动删除
            }
            //判断临时表是否存在，如果存在，删除临时表
            if (dbl.yssTableExist("TB_" + sPre + "_DAO_08282008082820080")) {
                this.dropTableByTableName("TB_" + sPre + "_DAO_08282008082820080");
            }
            //把源表重命名为临时表
            dbl.executeSql(" ALTER TABLE TB_" + sPre + "_Dao_FileName" +
                           " RENAME TO TB_" + sPre + "_DAO_08282008082820080");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表TB_xxx_Dao_FileName
            bufSql.append(" CREATE TABLE TB_" + sPre + "_Dao_FileName");
            bufSql.append(" (");
            bufSql.append(" FCUSCFGCODE      VARCHAR2(20)    NOT NULL,");
            bufSql.append(" FORDERNUM        NUMBER(3)       NOT NULL,");
            bufSql.append(" FFILENAMECONENT  VARCHAR2(300)       NULL,");
            bufSql.append(" FVALUETYPE       NUMBER(1)       NOT NULL,");
            bufSql.append(" FFILENAMECLS     VARCHAR2(20)        NULL,");
            bufSql.append(" FFILENAMEDICT    VARCHAR2(20)        NULL,");
            bufSql.append(" FTABFEILD        VARCHAR2(30)        NULL,");
            bufSql.append(" FCREATOR         VARCHAR2(20)    NOT NULL,");
            bufSql.append(" FCREATETIME      VARCHAR2(20)    NOT NULL,");
            bufSql.append(" FCHECKUSER       VARCHAR2(20)        NULL,");
            bufSql.append(" FCHECKTIME       VARCHAR2(20)        NULL,");
            bufSql.append(" FDESC            VARCHAR2(100)       NULL,");
            bufSql.append(" FCHECKSTATE      NUMBER(1)       NOT NULL,");
            bufSql.append(" FFORMAT          VARCHAR2(30)        NULL");
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
                           "_Dao_FileName  PRIMARY KEY (FCUSCFGCODE, FORDERNUM)");
            //----------------------jc

            //BugNo:0000449 edit by jc
            //对表Tb_XXX_VCH_DATAENTITY中调整FAssistant字段可以为空
            //通过表名查询主键名
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_VCH_DATAENTITY");
            //如果有主键规则就删除之
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_VCH_DATAENTITY") +
                               " DROP CONSTRAINT " + sPKName + " CASCADE");
                deleteIndex(sPKName); //若是没有级联删除索引，则手工删除
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_05162008081525087")) {
                this.dropTableByTableName("TB_" + sPre + "_VCH_05162008081525087");
            }
            //把源表重命名为临时表
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_VCH_DATAENTITY") +
                           " RENAME TO TB_" + sPre + "_VCH_05162008081525087");
            //首先清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表Tb_XXX_TB_VCH_DATAENTITY
            bufSql.append(" CREATE TABLE TB_" + sPre + "_VCH_DATAENTITY");
            bufSql.append(" (");
            bufSql.append(" FVchNum       VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FEntityNum    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FSubjectCode  VARCHAR2(50)  NOT NULL,");
            bufSql.append(" FCuryRate     NUMBER(35,12) NOT NULL,");
            bufSql.append(" FResume       VARCHAR2(50)      NULL,");
            bufSql.append(" FDCWay        VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FBookSetCode  VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FBal          NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FSetBal       NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FAmount       NUMBER(18,4)      NULL,");
            bufSql.append(" FPrice        NUMBER(20,8)      NULL,");
            bufSql.append(" FAssistant    VARCHAR2(20)      NULL,");
            bufSql.append(" FDesc         VARCHAR2(100)     NULL,");
            bufSql.append(" FCheckState   NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER    VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME    VARCHAR2(20)      NULL");
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
                           "_VCH_DATAENTITY PRIMARY KEY (FVchNum,FEntityNum)");
            //-----------------------jc

        } catch (Exception ex) {
            throw new YssException("版本Oracle1010003sp7调整表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
