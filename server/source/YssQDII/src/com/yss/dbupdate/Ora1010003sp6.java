/**
 更新凭证字典设置表Tb_XXX_Vch_Dict中字段的长度，使库中字段的长度与设计表中字段的长度保持一致。
 bugNo.:0000401
 add by MaoQiwen
 20080819
 */
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

public class Ora1010003sp6
    extends BaseDbUpdate {
    public Ora1010003sp6() {}

    //创建表 edit by jc 新建卖出交易关联表
    public void createTable(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer(5000);
        if (!dbl.yssTableExist("TB_" + sPre + "_DATA_TRADESELLRELA")) {
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_TRADESELLRELA");
            bufSql.append(" (");
            bufSql.append(" FNUM               VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FAPPRECIATION      NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FMAPPRECIATION     NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FVAPPRECIATION     NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FBASEAPPRECIATION  NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FMBASEAPPRECIATION NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FVBASEAPPRECIATION NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FPORTAPPRECIATION  NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FMPORTAPPRECIATION NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FVPORTAPPRECIATION NUMBER(18, 4) NOT NULL,");
            bufSql.append(" FREVENUE           NUMBER(18, 4) NOT NULL,");
            bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_DATA_TRADESELLRELA");
            bufSql.append(" PRIMARY KEY(FNUM)");
            bufSql.append(" )");
            try {
                dbl.executeSql(bufSql.toString());
            } catch (Exception ex) {
                throw new YssException("版本1.0.1.0003sp6 创建表出错！", ex);
            } finally {
                bufSql = null; //回收,以清除内存
            }
        }
    }

    //添加表字段
    public void addTableField(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //BugNo:0000429 edit by jc
            //对表 Tb_XXX_Para_Security 增加字段 FSecurityShortName,FSecurityCorpName
            if (existsTabColumn_Ora("TB_" + sPre + "_PARA_SECURITY",
                                    "FSecurityShortName,FSecurityCorpName")) {
                sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_PARA_SECURITY");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_SECURITY DROP CONSTRAINT " + sPKName + " CASCADE");
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_PAR_08252008021511000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_PAR_08252008021511000");
                }
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_SECURITY RENAME TO TB_" + sPre +
                               "_PAR_08252008021511000");
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_SECURITY");
                bufSql.append(" (");
                bufSql.append(" FSECURITYCODE      VARCHAR2(20)  NOT NULL,");
                bufSql.append(" FSTARTDATE         DATE          NOT NULL,");
                bufSql.append(" FSECURITYNAME      VARCHAR2(100) NOT NULL,");
                bufSql.append(" FSECURITYSHORTNAME VARCHAR2(50)      NULL,");
                bufSql.append(" FSECURITYCORPNAME  VARCHAR2(50)      NULL,");
                bufSql.append(" FCATCODE           VARCHAR2(20)  NOT NULL,");
                bufSql.append(" FSUBCATCODE        VARCHAR2(20)      NULL,");
                bufSql.append(" FCUSCATCODE        VARCHAR2(20)      NULL,");
                bufSql.append(" FEXCHANGECODE      VARCHAR2(20)  NOT NULL,");
                bufSql.append(" FMARKETCODE        VARCHAR2(20)  NOT NULL,");
                bufSql.append(" FEXTERNALCODE      VARCHAR2(100)     NULL,");
                bufSql.append(" FISINCODE          VARCHAR2(20)      NULL,");
                bufSql.append(" FTRADECURY         VARCHAR2(20)  NOT NULL,");
                bufSql.append(" FHOLIDAYSCODE      VARCHAR2(20)  NOT NULL,");
                bufSql.append(" FSETTLEDAYTYPE     NUMBER(1)     DEFAULT 0 NOT NULL,");
                bufSql.append(" FSETTLEDAYS        NUMBER(38)    NOT NULL,");
                bufSql.append(" FSECTORCODE        VARCHAR2(20)      NULL,");
                bufSql.append(" FTOTALSHARE        NUMBER(18,4)  NOT NULL,");
                bufSql.append(" FCURRENTSHARE      NUMBER(18, 4) NOT NULL,");
                bufSql.append(" FHANDAMOUNT        NUMBER(18, 4) DEFAULT 1 NOT NULL,");
                bufSql.append(" FFACTOR            NUMBER(12, 6) NOT NULL,");
                bufSql.append(" FISSUECORPCODE     VARCHAR2(40)  NULL,");
                bufSql.append(" FDESC              VARCHAR2(100) NULL,");
                bufSql.append(" FCHECKSTATE        NUMBER(1)     NOT NULL,");
                bufSql.append(" FCREATOR           VARCHAR2(20)  NOT NULL,");
                bufSql.append(" FCREATETIME        VARCHAR2(20)  NOT NULL,");
                bufSql.append(" FCHECKUSER         VARCHAR2(20)  NULL,");
                bufSql.append(" FCHECKTIME         VARCHAR2(20)  NULL");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.append(" INSERT INTO TB_" + sPre + "_PARA_SECURITY(");
                bufSql.append(" FSECURITYCODE,");
                bufSql.append(" FSTARTDATE,");
                bufSql.append(" FSECURITYNAME,");
                bufSql.append(" FSECURITYSHORTNAME,");
                bufSql.append(" FSECURITYCORPNAME,");
                bufSql.append(" FCATCODE,");
                bufSql.append(" FSUBCATCODE,");
                bufSql.append(" FCUSCATCODE,");
                bufSql.append(" FEXCHANGECODE,");
                bufSql.append(" FMARKETCODE,");
                bufSql.append(" FEXTERNALCODE,");
                bufSql.append(" FISINCODE,");
                bufSql.append(" FTRADECURY,");
                bufSql.append(" FHOLIDAYSCODE,");
                bufSql.append(" FSETTLEDAYTYPE,");
                bufSql.append(" FSETTLEDAYS,");
                bufSql.append(" FSECTORCODE,");
                bufSql.append(" FTOTALSHARE,");
                bufSql.append(" FCURRENTSHARE,");
                bufSql.append(" FHANDAMOUNT,");
                bufSql.append(" FFACTOR,");
                bufSql.append(" FISSUECORPCODE,");
                bufSql.append(" FDESC,");
                bufSql.append(" FCHECKSTATE,");
                bufSql.append(" FCREATOR,");
                bufSql.append(" FCREATETIME,");
                bufSql.append(" FCHECKUSER,");
                bufSql.append(" FCHECKTIME");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FSECURITYCODE,");
                bufSql.append(" FSTARTDATE,");
                bufSql.append(" FSECURITYNAME,");
                bufSql.append(" null,");
                bufSql.append(" null,");
                bufSql.append(" FCATCODE,");
                bufSql.append(" FSUBCATCODE,");
                bufSql.append(" FCUSCATCODE,");
                bufSql.append(" FEXCHANGECODE,");
                bufSql.append(" FMARKETCODE,");
                bufSql.append(" FEXTERNALCODE,");
                bufSql.append(" FISINCODE,");
                bufSql.append(" FTRADECURY,");
                bufSql.append(" FHOLIDAYSCODE,");
                bufSql.append(" FSETTLEDAYTYPE,");
                bufSql.append(" FSETTLEDAYS,");
                bufSql.append(" FSECTORCODE,");
                bufSql.append(" FTOTALSHARE,");
                bufSql.append(" FCURRENTSHARE,");
                bufSql.append(" FHANDAMOUNT,");
                bufSql.append(" FFACTOR,");
                bufSql.append(" FISSUECORPCODE,");
                bufSql.append(" FDESC,");
                bufSql.append(" FCHECKSTATE,");
                bufSql.append(" FCREATOR,");
                bufSql.append(" FCREATETIME,");
                bufSql.append(" FCHECKUSER,");
                bufSql.append(" FCHECKTIME");
                bufSql.append(" FROM TB_" + sPre + "_PAR_08252008021511000");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_SECURITY ADD CONSTRAINT PK_TB_" + sPre +
                               "_PARA_SECURITY PRIMARY KEY (FSecurityCode)");
            }
            //----------------------jc

            //BugNo:0000413 edit by jc
            //对表 TB_XXX_DATA_NAVDATA 增加字段 FPortMarketValueRatio
            if (existsTabColumn_Ora("TB_" + sPre + "_DATA_NAVDATA",
                                    "FPortMarketValueRatio")) {
                sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_DATA_NAVDATA");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_DATA_NAVDATA DROP CONSTRAINT " + sPKName +
                                   " CASCADE");
                    deleteIndex(sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_DAT_08282008023706000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_DAT_08282008023706000");
                }
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_NAVDATA RENAME TO TB_" + sPre +
                               "_DAT_08282008023706000");
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_NAVDATA ");
                bufSql.append(" ( ");
                bufSql.append(" FNAVDATE              DATE          NOT NULL, ");
                bufSql.append(" FPORTCODE             VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FORDERCODE            VARCHAR2(200) NOT NULL, ");
                bufSql.append(" FRETYPECODE           VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FINVMGRCODE           VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FKEYCODE              VARCHAR2(50)  NOT NULL, ");
                bufSql.append(" FINOUT                NUMBER(1)     DEFAULT 1 NOT NULL, ");
                bufSql.append(" FKEYNAME              VARCHAR2(200) NOT NULL, ");
                bufSql.append(" FDETAIL               NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCURYCODE             VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FPRICE                NUMBER(20,12)     NULL, ");
                bufSql.append(" FOTPRICE1             NUMBER(20,12)     NULL, ");
                bufSql.append(" FOTPRICE2             NUMBER(20,12)     NULL, ");
                bufSql.append(" FOTPRICE3             NUMBER(20,12)     NULL, ");
                bufSql.append(" FSEDOLCODE            VARCHAR2(20)      NULL, ");
                bufSql.append(" FISINCODE             VARCHAR2(20)      NULL, ");
                bufSql.append(" FSPARAMT              NUMBER(20,4)      NULL, ");
                bufSql.append(" FBASECURYRATE         NUMBER(20,15) NOT NULL, ");
                bufSql.append(" FPORTCURYRATE         NUMBER(20,15) NOT NULL, ");
                bufSql.append(" FCOST                 NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPORTCOST             NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMARKETVALUE          NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPORTMARKETVALUE      NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMVVALUE              NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPORTMVVALUE          NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FFXVALUE              NUMBER(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPORTMARKETVALUERATIO NUMBER(18,4)      NULL, ");
                bufSql.append(" FGRADETYPE1           VARCHAR2(20)      NULL, ");
                bufSql.append(" FGRADETYPE2           VARCHAR2(20)      NULL, ");
                bufSql.append(" FGRADETYPE3           VARCHAR2(20)      NULL, ");
                bufSql.append(" FGRADETYPE4           VARCHAR2(20)      NULL, ");
                bufSql.append(" FGRADETYPE5           VARCHAR2(20)      NULL, ");
                bufSql.append(" FGRADETYPE6           VARCHAR2(20)      NULL ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_NAVDATA( ");
                bufSql.append(" FNAVDATE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FORDERCODE, ");
                bufSql.append(" FRETYPECODE, ");
                bufSql.append(" FINVMGRCODE, ");
                bufSql.append(" FKEYCODE, ");
                bufSql.append(" FINOUT, ");
                bufSql.append(" FKEYNAME, ");
                bufSql.append(" FDETAIL, ");
                bufSql.append(" FCURYCODE, ");
                bufSql.append(" FPRICE, ");
                bufSql.append(" FOTPRICE1, ");
                bufSql.append(" FOTPRICE2, ");
                bufSql.append(" FOTPRICE3, ");
                bufSql.append(" FSEDOLCODE, ");
                bufSql.append(" FISINCODE, ");
                bufSql.append(" FSPARAMT, ");
                bufSql.append(" FBASECURYRATE, ");
                bufSql.append(" FPORTCURYRATE, ");
                bufSql.append(" FCOST, ");
                bufSql.append(" FPORTCOST, ");
                bufSql.append(" FMARKETVALUE, ");
                bufSql.append(" FPORTMARKETVALUE, ");
                bufSql.append(" FMVVALUE, ");
                bufSql.append(" FPORTMVVALUE, ");
                bufSql.append(" FFXVALUE, ");
                bufSql.append(" FPORTMARKETVALUERATIO, ");
                bufSql.append(" FGRADETYPE1, ");
                bufSql.append(" FGRADETYPE2, ");
                bufSql.append(" FGRADETYPE3, ");
                bufSql.append(" FGRADETYPE4, ");
                bufSql.append(" FGRADETYPE5, ");
                bufSql.append(" FGRADETYPE6 ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT  ");
                bufSql.append(" FNAVDATE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FORDERCODE, ");
                bufSql.append(" FRETYPECODE, ");
                bufSql.append(" FINVMGRCODE, ");
                bufSql.append(" FKEYCODE, ");
                bufSql.append(" FINOUT, ");
                bufSql.append(" FKEYNAME, ");
                bufSql.append(" FDETAIL, ");
                bufSql.append(" FCURYCODE, ");
                bufSql.append(" FPRICE, ");
                bufSql.append(" FOTPRICE1, ");
                bufSql.append(" FOTPRICE2, ");
                bufSql.append(" FOTPRICE3, ");
                bufSql.append(" FSEDOLCODE, ");
                bufSql.append(" FISINCODE, ");
                bufSql.append(" FSPARAMT, ");
                bufSql.append(" FBASECURYRATE, ");
                bufSql.append(" FPORTCURYRATE, ");
                bufSql.append(" FCOST, ");
                bufSql.append(" FPORTCOST, ");
                bufSql.append(" FMARKETVALUE, ");
                bufSql.append(" FPORTMARKETVALUE, ");
                bufSql.append(" FMVVALUE, ");
                bufSql.append(" FPORTMVVALUE, ");
                bufSql.append(" FFXVALUE, ");
                bufSql.append(" NULL, ");
                bufSql.append(" FGRADETYPE1, ");
                bufSql.append(" FGRADETYPE2, ");
                bufSql.append(" FGRADETYPE3, ");
                bufSql.append(" FGRADETYPE4, ");
                bufSql.append(" FGRADETYPE5, ");
                bufSql.append(" FGRADETYPE6 ");
                bufSql.append(" FROM TB_" + sPre + "_DAT_08282008023706000 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_NAVDATA ADD CONSTRAINT PK_TB_" + sPre +
                               "_DATA_NAVDATA PRIMARY KEY (FNAVDATE,FPORTCODE," +
                               "FORDERCODE,FRETYPECODE,FINVMGRCODE,FKEYCODE)");
            }
            //---------------------jc
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp6 增加表字段出错！", ex);
        } finally {
            bufSql = null; //回收,以清除内存
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
            //更改表TB_XXX_VCH_DICT中字段FDictName的长度
            //通过表名查询主键名
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre + "_VCH_DICT");
            //如果有主键规则就删除主键规则
            if (sPKName.trim().length() != 0) {
                dbl.executeSql(" ALTER TABLE TB_" + sPre + "_VCH_DICT" +
                               " DROP CONSTRAINT " + sPKName + " CASCADE");
                deleteIndex(sPKName); //若是没有级联删除索引，则手动删除
            }
            //判断临时表是否存在，如果存在，删除临时表
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_08202008032745000")) {
                this.dropTableByTableName("TB_" + sPre + "_VCH_08202008032745000");
            }
            //把源表重命名为临时表
            dbl.executeSql(" ALTER TABLE TB_" + sPre + "_VCH_DICT" +
                           " RENAME TO TB_" + sPre + "_VCH_08202008032745000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表TB_xxx_VCH_DICT
            bufSql.append(" CREATE TABLE TB_" + sPre + "_VCH_DICT");
            bufSql.append(" (");
            bufSql.append(" FDICTCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FINDCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FPORTCODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDICTNAME   VARCHAR2(50)  NOT NULL,");
            bufSql.append(" FCNVCONENT  VARCHAR2(50)  NOT NULL,");
            bufSql.append(" FDESC       VARCHAR2(100)     NULL,");
            bufSql.append(" FSUBDESC    VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER  VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME  VARCHAR2(20)      NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_VCH_DICT(");
            bufSql.append(" FDICTCODE,");
            bufSql.append(" FINDCODE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FDICTNAME,");
            bufSql.append(" FCNVCONENT,");
            bufSql.append(" FDESC,");
            bufSql.append(" FSUBDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FDICTCODE,");
            bufSql.append(" FINDCODE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FDICTNAME,");
            bufSql.append(" FCNVCONENT,");
            bufSql.append(" FDESC,");
            bufSql.append(" FSUBDESC,");
            bufSql.append(" FCHECKSTATE,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_VCH_08202008032745000");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql(" ALTER TABLE TB_" + sPre + "_VCH_DICT  ADD CONSTRAINT PK_TB_" +
                           sPre + "_VCH_DICT  PRIMARY KEY (FDICTCODE,FINDCODE,FPORTCODE)");
        } catch (Exception ex) {
            throw new YssException("版本Oracle1010003sp6调整表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
