/**
 更新凭证字典设置表Tb_XXX_Vch_Dict中字段的长度，使库中字段的长度与设计表中字段的长度保持一致。
 bugNo.:0000401
 add by MaoQiwen
 20080820
 */
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

public class DB21010003sp6
    extends BaseDbUpdate {
    public DB21010003sp6() {}

    //创建表 edit by jc 新建卖出交易关联表
    public void createTable(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer(5000);
        if (!dbl.yssTableExist("TB_" + sPre + "_DATA_TRADESELLRELA")) {
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_TRADESELLRELA");
            bufSql.append(" (");
            bufSql.append(" FNUM               VARCHAR(20)  NOT NULL,");
            bufSql.append(" FAPPRECIATION      DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FMAPPRECIATION     DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FVAPPRECIATION     DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FBASEAPPRECIATION  DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FMBASEAPPRECIATION DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FVBASEAPPRECIATION DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FPORTAPPRECIATION  DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FMPORTAPPRECIATION DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FVPORTAPPRECIATION DECIMAL(18,4) NOT NULL,");
            bufSql.append(" FREVENUE           DECIMAL(18,4) NOT NULL,");
            bufSql.append(" CONSTRAINT PK_TB_" + sPre + "_ELLRELA");
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
            if (existsTabColumn_DB2("TB_" + sPre + "_PARA_SECURITY",
                                    "FSecurityShortName,FSecurityCorpName")) {
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_PARA_SECURITY");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_SECURITY DROP CONSTRAINT " + sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_PAR_08252008021511000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_PAR_08252008021511000");
                }
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_PARA_SECURITY  TO TB_" + sPre +
                               "_PAR_08252008021511000");
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_SECURITY");
                bufSql.append(" (");
                bufSql.append(" FSECURITYCODE      VARCHAR(20)     NOT NULL,");
                bufSql.append(" FSTARTDATE         DATE            NOT NULL,");
                bufSql.append(" FSECURITYNAME      VARCHAR(100)    NOT NULL,");
                bufSql.append(" FSECURITYSHORTNAME VARCHAR(50),");
                bufSql.append(" FSECURITYCORPNAME  VARCHAR(50),");
                bufSql.append(" FCATCODE           VARCHAR(20)     NOT NULL,");
                bufSql.append(" FSUBCATCODE        VARCHAR(20),");
                bufSql.append(" FCUSCATCODE        VARCHAR(20),");
                bufSql.append(" FEXCHANGECODE      VARCHAR(20)     NOT NULL,");
                bufSql.append(" FMARKETCODE        VARCHAR(20)     NOT NULL,");
                bufSql.append(" FEXTERNALCODE      VARCHAR(100),");
                bufSql.append(" FISINCODE          VARCHAR(20),");
                bufSql.append(" FTRADECURY         VARCHAR(20)     NOT NULL,");
                bufSql.append(" FHOLIDAYSCODE      VARCHAR(20)     NOT NULL,");
                bufSql.append(" FSETTLEDAYTYPE     DECIMAL(1)      DEFAULT 0 NOT NULL,");
                bufSql.append(" FSETTLEDAYS        DECIMAL(28)     NOT NULL,");
                bufSql.append(" FSECTORCODE        VARCHAR(20),");
                bufSql.append(" FTOTALSHARE        DECIMAL(18,4)   NOT NULL,");
                bufSql.append(" FCURRENTSHARE      DECIMAL(18,4)   NOT NULL,");
                bufSql.append(" FHANDAMOUNT        DECIMAL(18,4)   DEFAULT 1 NOT NULL,");
                bufSql.append(" FFACTOR            DECIMAL(12,6)   NOT NULL,");
                bufSql.append(" FISSUECORPCODE     VARCHAR(40),");
                bufSql.append(" FDESC              VARCHAR(100),");
                bufSql.append(" FCHECKSTATE        DECIMAL(1)      NOT NULL,");
                bufSql.append(" FCREATOR           VARCHAR(20)     NOT NULL,");
                bufSql.append(" FCREATETIME        VARCHAR(20)     NOT NULL,");
                bufSql.append(" FCHECKUSER         VARCHAR(20),");
                bufSql.append(" FCHECKTIME         VARCHAR(20)");
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
                bufSql.append(" ' ',");
                bufSql.append(" ' ',");
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
                               "_ECURITY PRIMARY KEY (FSecurityCode)");
            }
            //----------------------jc

            //BugNo:0000413 edit by jc
            //对表 TB_XXX_DATA_NAVDATA 增加字段 FPortMarketValueRatio
            if (existsTabColumn_DB2("TB_" + sPre + "_DATA_NAVDATA",
                                    "FPortMarketValueRatio")) {
                sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_DATA_NAVDATA");
                if (sPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_DATA_NAVDATA DROP CONSTRAINT " + sPKName);
                }
                if (dbl.yssTableExist("TB_" + sPre + "_DAT_08282008023706000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_DAT_08282008023706000");
                }
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_DATA_NAVDATA TO TB_" + sPre +
                               "_DAT_08282008023706000");
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_NAVDATA ");
                bufSql.append(" ( ");
                bufSql.append(" FNAVDATE              DATE           NOT NULL, ");
                bufSql.append(" FPORTCODE             VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FORDERCODE            VARCHAR(200)   NOT NULL, ");
                bufSql.append(" FRETYPECODE           VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FINVMGRCODE           VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FKEYCODE              VARCHAR(50)    NOT NULL, ");
                bufSql.append(" FINOUT                DECIMAL(1)     DEFAULT 1 NOT NULL, ");
                bufSql.append(" FKEYNAME              VARCHAR(200)   NOT NULL, ");
                bufSql.append(" FDETAIL               DECIMAL(1)     NOT NULL, ");
                bufSql.append(" FCURYCODE             VARCHAR(20)    NOT NULL, ");
                bufSql.append(" FPRICE                DECIMAL(20,12), ");
                bufSql.append(" FOTPRICE1             DECIMAL(20,12), ");
                bufSql.append(" FOTPRICE2             DECIMAL(20,12), ");
                bufSql.append(" FOTPRICE3             DECIMAL(20,12), ");
                bufSql.append(" FSEDOLCODE            VARCHAR(20), ");
                bufSql.append(" FISINCODE             VARCHAR(20), ");
                bufSql.append(" FSPARAMT              DECIMAL(20,4), ");
                bufSql.append(" FBASECURYRATE         DECIMAL(20,15) NOT NULL, ");
                bufSql.append(" FPORTCURYRATE         DECIMAL(20,15) NOT NULL, ");
                bufSql.append(" FCOST                 DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPORTCOST             DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMARKETVALUE          DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPORTMARKETVALUE      DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FMVVALUE              DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPORTMVVALUE          DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FFXVALUE              DECIMAL(18,4)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPORTMARKETVALUERATIO DECIMAL(18,4), ");
                bufSql.append(" FGRADETYPE1           VARCHAR(20), ");
                bufSql.append(" FGRADETYPE2           VARCHAR(20), ");
                bufSql.append(" FGRADETYPE3           VARCHAR(20), ");
                bufSql.append(" FGRADETYPE4           VARCHAR(20), ");
                bufSql.append(" FGRADETYPE5           VARCHAR(20), ");
                bufSql.append(" FGRADETYPE6           VARCHAR(20) ");
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
                bufSql.append(" 0, ");
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
                               "_NAVDATA PRIMARY KEY (FNAVDATE,FPORTCODE," +
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
            //对表TB_XXX_VCH_DICT中的字段FDictName长度进行更改
            //根据表名查询主键名
            sPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre + "_VCH_DICT");
            //如果有主键规则，删除主键规则
            if (sPKName.trim().length() != 0) {
                dbl.executeSql(" ALTER TABLE TB_" + sPre + "_VCH_DICT" +
                               " DROP CONSTRAINT " + sPKName);
            }
            //判断是否存在临时表，如果已经存在，则删除临时表
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_08202008032745000")) {
                this.dropTableByTableName("TB_" + sPre + "_VCH_08202008032745000");
            }
            //把源表重命名为临时表
            dbl.executeSql(" RENAME TABLE TB_" + sPre + "_VCH_DICT" +
                           " TO TB_" + sPre + "_VCH_08202008032745000");
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表TB_XXX_VCH_DICT
            bufSql.append(" CREATE TABLE TB_" + sPre + "_VCH_DICT");
            bufSql.append(" (");
            bufSql.append(" FDICTCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FINDCODE    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FPORTCODE   VARCHAR(20)  NOT NULL,");
            bufSql.append(" FDICTNAME   VARCHAR(50)  NOT NULL,");
            bufSql.append(" FCNVCONENT  VARCHAR(50)  NOT NULL,");
            bufSql.append(" FDESC       VARCHAR(100),");
            bufSql.append(" FSUBDESC    VARCHAR(100),");
            bufSql.append(" FCHECKSTATE DECIMAL(1)   NOT NULL,");
            bufSql.append(" FCREATOR    VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCREATETIME VARCHAR(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER  VARCHAR(20),");
            bufSql.append(" FCHECKTIME  VARCHAR(20)");
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
            dbl.executeSql(" ALTER TABLE TB_" + sPre +
                           "_VCH_DICT ADD CONSTRAINT PK_TB_" + sPre +
                           "_h_dict  PRIMARY KEY (FDICTCODE,FINDCODE,FPORTCODE)");
        } catch (Exception ex) {
            throw new YssException("版本DB21010003sp6调整表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
