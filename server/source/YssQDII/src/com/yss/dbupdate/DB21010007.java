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
public class DB21010007
    extends BaseDbUpdate {
    public DB21010007() {
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            //=====================================================================
            /**
             * date   : 2008-11-25
             * author : sunkey
             * bugid  : MS00035
             * desc   : 更新国家表 tb_base_country ，添加字段“协议”
             *          FAgreement varchar(2000) null
             */
            //表中不存在FAgreement字段，才执行更新
            if (existsTabColumn_DB2("TB_BASE_COUNTRY", "FAgreement")) {
                //设置事物参数状态 -- 事物回滚
                bTrans = true;
                //设置事物不自动提交
                conn.setAutoCommit(false);
                //获取主键约束
                strPKName = this.getIsNullPKByTableName_DB2("TB_BASE_COUNTRY");
                if (strPKName != null && !strPKName.trim().equals("")) {
                    //删除约束
                    dbl.executeSql("ALTER TABLE TB_BASE_COUNTRY DROP CONSTRAINT " +
                                   strPKName);
                }
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_11252008073227")) {
                    this.dropTableByTableName("TB_11252008073227");
                }
                //将原表更改为备份表
                dbl.executeSql("RENAME TABLE TB_BASE_COUNTRY TO TB_11252008073227");
                //销毁StringBuffer对象 并重新构造
                bufSql = null;
                bufSql = new StringBuffer();
                //创建新结构的表
                bufSql.append("CREATE TABLE TB_BASE_COUNTRY(");
                bufSql.append("FCOUNTRYCODE      VARCHAR(20)   NOT NULL,");
                bufSql.append("FCOUNTRYNAME      VARCHAR(50)   NOT NULL,");
                bufSql.append("FREGIONCODE       VARCHAR(20),");
                bufSql.append("FCOUNTRYSHORTNAME VARCHAR(20)   NOT NULL,");
                bufSql.append("FINTERDOMAIN      VARCHAR(20),");
                bufSql.append("FPHONECODE        VARCHAR(20),");
                bufSql.append("FDIFFTIME         VARCHAR(20),");
                bufSql.append("FAgreement        VARCHAR(2000),");
                bufSql.append("FDESC             VARCHAR(200),");
                bufSql.append("FCHECKSTATE       DECIMAL(1)    NOT NULL,");
                bufSql.append("FCREATOR          VARCHAR(20)   NOT NULL,");
                bufSql.append("FCREATETIME       VARCHAR(20)   NOT NULL,");
                bufSql.append("FCHECKUSER        VARCHAR(20),");
                bufSql.append("FCHECKTIME        VARCHAR(20))");
                dbl.executeSql(bufSql.toString());
                //销毁StringBuffer对象 并重新构造
                bufSql = null;
                bufSql = new StringBuffer();
                //将原始表中的数据导入新表
                bufSql.append("INSERT INTO TB_BASE_COUNTRY(");
                bufSql.append("FCOUNTRYCODE,FCOUNTRYNAME,FREGIONCODE,");
                bufSql.append("FCOUNTRYSHORTNAME,FINTERDOMAIN,FPHONECODE,");
                bufSql.append("FDIFFTIME,FDESC,FCHECKSTATE,FCREATOR,");
                bufSql.append("FCREATETIME,FCHECKUSER,FCHECKTIME)");
                bufSql.append(" SELECT ");
                bufSql.append("FCOUNTRYCODE,FCOUNTRYNAME,FREGIONCODE,");
                bufSql.append("FCOUNTRYSHORTNAME,FINTERDOMAIN,FPHONECODE,");
                bufSql.append("FDIFFTIME,FDESC,FCHECKSTATE,FCREATOR,");
                bufSql.append("FCREATETIME,FCHECKUSER,FCHECKTIME ");
                bufSql.append("FROM TB_11252008073227");
                dbl.executeSql(bufSql.toString());
                //添加约束
                dbl.executeSql("ALTER TABLE TB_BASE_COUNTRY ADD CONSTRAINT PK_Tb_Base_Country PRIMARY KEY (FCOUNTRYCODE)");

                //更新表完成 自动提交事物
                bTrans = false;
                conn.setAutoCommit(true);
                conn.commit();
            }
            //=====================================================================
            /**
             * date   : 20081125
             * author : linjunyun
             * desc   : 付款人收款人表 TB_001_PARA_RECEIVER 新增字段FTITLE
             * BugID  : MS00018
             */
            if (existsTabColumn_DB2("TB_" + sPre + "_PARA_RECEIVER", "FTITLE")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                //获得主键
                strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_PARA_RECEIVER");
                //判断主键是否存在，存在则删除主键
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_RECEIVER DROP CONSTRAINT " + strPKName);
                }
                //判断临时表是否存在 如果临时表已经存在则删除此表
                if (dbl.yssTableExist("TB_11202008055521")) {
                    this.dropTableByTableName("TB_11202008055521");
                }
                //重命名原有表为临时表
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_PARA_RECEIVER TO TB_11202008055521");
                //创建包含新字段的表
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_RECEIVER ");
                bufSql.append(" ( ");
                bufSql.append(" FRECEIVERCODE      VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FRECEIVERNAME      VARCHAR(200)  NOT NULL, ");
                bufSql.append(" FRECEIVERSHORTNAME VARCHAR(50), ");
                bufSql.append(" FTITLE             VARCHAR(100) NOT NULL, ");
                bufSql.append(" FOFFICEADDR        VARCHAR(200), ");
                bufSql.append(" FPOSTALCODE        VARCHAR(20), ");
                bufSql.append(" FOPERBANK          VARCHAR(100) NOT NULL, ");
                bufSql.append(" FACCOUNTNUMBER     VARCHAR(100) NOT NULL, ");
                bufSql.append(" FCURYCODE          VARCHAR(20), ");
                bufSql.append(" FPortCode          VARCHAR(20), ");
                bufSql.append(" FCashAccCode       VARCHAR(20), ");
                bufSql.append(" FAnalysisCode1     VARCHAR(20), ");
                bufSql.append(" FAnalysisCode2     VARCHAR(20), ");
                bufSql.append(" FAnalysisCode3     VARCHAR(20), ");
                bufSql.append(" FDESC              VARCHAR(200), ");
                bufSql.append(" FCHECKSTATE        DECIMAL(1)   NOT NULL, ");
                bufSql.append(" FCREATOR           VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME        VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER         VARCHAR(20), ");
                bufSql.append(" FCHECKTIME         VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                //从临时表中导数据到新创建的表中
                bufSql.append(" INSERT INTO TB_" + sPre + "_PARA_RECEIVER( ");
                bufSql.append(" FRECEIVERCODE, ");
                bufSql.append(" FRECEIVERNAME, ");
                bufSql.append(" FRECEIVERSHORTNAME, ");
                bufSql.append(" FTITLE, ");
                bufSql.append(" FOFFICEADDR, ");
                bufSql.append(" FPOSTALCODE, ");
                bufSql.append(" FOPERBANK, ");
                bufSql.append(" FACCOUNTNUMBER, ");
                bufSql.append(" FCURYCODE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FCASHACCCODE, ");
                bufSql.append(" FANALYSISCODE1, ");
                bufSql.append(" FANALYSISCODE2, ");
                bufSql.append(" FANALYSISCODE3, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT  ");
                bufSql.append(" FRECEIVERCODE, ");
                bufSql.append(" FRECEIVERNAME, ");
                bufSql.append(" FRECEIVERSHORTNAME, ");
                bufSql.append(" ' ', ");
                bufSql.append(" FOFFICEADDR, ");
                bufSql.append(" FPOSTALCODE, ");
                bufSql.append(" FOPERBANK, ");
                bufSql.append(" FACCOUNTNUMBER, ");
                bufSql.append(" FCURYCODE, ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FCASHACCCODE, ");
                bufSql.append(" FANALYSISCODE1, ");
                bufSql.append(" FANALYSISCODE2, ");
                bufSql.append(" FANALYSISCODE3, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_11202008055521 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                //设定表的主键
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_RECEIVER ADD CONSTRAINT PK_TB_" + sPre +
                               "_Para_Rec " +
                               "PRIMARY KEY (FRECEIVERCODE)");
                bTrans = false;
            }
            //----------------------------------------------------------------//
            /**
             * date   : 2008-11-27
             * author : 王晓光
             * desc   : 给表TB_001_Comp_ResultData添加字段FNumerator,FDenominator,FFactRatio
             * BugID  : MS00040
             */
            if (this.existsTabColumn_DB2("TB_" + sPre + "_Comp_ResultData",
                                         "FNumerator,FDenominator,FFactRatio")) {
                //根据表名查询主键名
                strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_Comp_ResultData");
                //删除原有的主键
                if (strPKName != null && !strPKName.trim().equals("")) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_COMP_RESULTDATA DROP CONSTRAINT " + strPKName);
                }
                //删除存在的临时表
                if (dbl.yssTableExist("TB_11272008032452")) {
                    this.dropTableByTableName("TB_11272008032452");
                }
                //将原表名改为临时表
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_COMP_RESULTDATA TO TB_11272008032452");
                bufSql.delete(0, bufSql.length());
                //创建新的表结构
                bufSql.append("CREATE TABLE TB_" + sPre + "_COMP_RESULTDATA (");
                bufSql.append(" FCOMPDATE     DATE NOT NULL,");
                bufSql.append(" FPORTCODE     VARCHAR(20)    NOT NULL,");
                bufSql.append(" FINDEXCFGCODE VARCHAR(20)    NOT NULL,");
                bufSql.append(" FCOMPRESULT   VARCHAR(20)    NOT NULL,");
                bufSql.append(" FDESC         VARCHAR(100),");
                bufSql.append(" FCHECKSTATE   DECIMAL(1)     NOT NULL,");
                bufSql.append(" FCREATOR      VARCHAR(20)    NOT NULL,");
                bufSql.append(" FCREATETIME   VARCHAR(20)    NOT NULL,");
                bufSql.append(" FCHECKUSER    VARCHAR(20),");
                bufSql.append(" FCHECKTIME    VARCHAR(20),");
                bufSql.append(" FNumerator    DECIMAL(18,4),");
                bufSql.append(" FDenominator  DECIMAL(18,4),");
                bufSql.append(" FFactRatio    DECIMAL(30,12)");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                //将原有数据插入新表中
                bufSql.append("INSERT INTO TB_" + sPre + "_COMP_RESULTDATA( ");
                bufSql.append("FCOMPDATE, ");
                bufSql.append("FPORTCODE, ");
                bufSql.append("FINDEXCFGCODE, ");
                bufSql.append("FCOMPRESULT, ");
                bufSql.append("FDESC, ");
                bufSql.append("FCHECKSTATE, ");
                bufSql.append("FCREATOR, ");
                bufSql.append("FCREATETIME, ");
                bufSql.append("FCHECKUSER, ");
                bufSql.append("FCHECKTIME )");

                bufSql.append("SELECT ");

                bufSql.append("FCOMPDATE, ");
                bufSql.append("FPORTCODE, ");
                bufSql.append("FINDEXCFGCODE, ");
                bufSql.append("FCOMPRESULT, ");
                bufSql.append("FDESC, ");
                bufSql.append("FCHECKSTATE, ");
                bufSql.append("FCREATOR, ");
                bufSql.append("FCREATETIME, ");
                bufSql.append("FCHECKUSER, ");
                bufSql.append("FCHECKTIME ");
                bufSql.append(" FROM TB_11272008032452 ");

                dbl.executeSql(bufSql.toString());
                //清空内存
                bufSql.delete(0, bufSql.length());
                //添加主键
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_COMP_RESULTDATA ADD CONSTRAINT PK_Tb_" + sPre +
                               "_Comp_Res " +
                               " PRIMARY KEY (FCOMPDATE,FPORTCODE,FINDEXCFGCODE)");
                bTrans = true;
                conn.setAutoCommit(bTrans);
                bTrans = false;
            }

            /**
             * date   : 20081201
             * author : linjunyun
             * desc   : 汇率数据表TB_001_DATA_EXCHANGERATE
             *          新增字段 FMARKCURY
             * BugID  : Ms00011
             */
            if (existsTabColumn_DB2("TB_" + sPre + "_DATA_EXCHANGERATE", "FMARKCURY")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                //获得主键
                strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_DATA_EXCHANGERATE");
                //判断主键是否存在，存在则删除主键
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_DATA_EXCHANGERATE DROP CONSTRAINT " + strPKName);
                }
                //判断临时表是否存在 如果临时表已经存在则删除此表
                if (dbl.yssTableExist("TB_12012008025340")) {
                    this.dropTableByTableName("TB_12012008025340");
                }
                //重命名原有表为临时表
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_DATA_EXCHANGERATE TO TB_12012008025340");
                //创建包含新字段的表
                bufSql.append(" CREATE TABLE TB_" + sPre + "_DATA_EXCHANGERATE ");
                bufSql.append(" ( ");
                bufSql.append("    FEXRATESRCCODE VARCHAR(20)    NOT NULL,");
                bufSql.append("    FCURYCODE      VARCHAR(20)    NOT NULL,");
                bufSql.append("    FMARKCURY      VARCHAR(20)    NOT NULL DEFAULT ' ',");
                bufSql.append("    FEXRATEDATE    DATE           NOT NULL,");
                bufSql.append("    FEXRATETIME    VARCHAR(20)    NOT NULL,");
                bufSql.append("    FPORTCODE      VARCHAR(20)    NOT NULL,");
                bufSql.append("    FEXRATE1       DECIMAL(20,15),");
                bufSql.append("    FEXRATE2       DECIMAL(20,15),");
                bufSql.append("    FEXRATE3       DECIMAL(20,15),");
                bufSql.append("    FEXRATE4       DECIMAL(20,15),");
                bufSql.append("    FEXRATE5       DECIMAL(20,15),");
                bufSql.append("    FEXRATE6       DECIMAL(20,15),");
                bufSql.append("    FEXRATE7       DECIMAL(20,15),");
                bufSql.append("    FEXRATE8       DECIMAL(20,15),");
                bufSql.append("    FDESC          VARCHAR(100),");
                bufSql.append("    FDATASOURCE    DECIMAL(1)     NOT NULL,");
                bufSql.append("    FCHECKSTATE    DECIMAL(1)     NOT NULL,");
                bufSql.append("    FCREATOR       VARCHAR(20)    NOT NULL,");
                bufSql.append("    FCREATETIME    VARCHAR(20)    NOT NULL,");
                bufSql.append("    FCHECKUSER     VARCHAR(20),");
                bufSql.append("    FCHECKTIME     VARCHAR(20)");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                //从临时表中导数据到新创建的表中
                bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_EXCHANGERATE( ");
                bufSql.append(" FEXRATESRCCODE,");
                bufSql.append(" FCURYCODE,");
                bufSql.append(" FMARKCURY,");
                bufSql.append(" FEXRATEDATE,");
                bufSql.append(" FEXRATETIME,");
                bufSql.append(" FPORTCODE,");
                bufSql.append(" FEXRATE1,");
                bufSql.append(" FEXRATE2,");
                bufSql.append(" FEXRATE3,");
                bufSql.append(" FEXRATE4,");
                bufSql.append(" FEXRATE5,");
                bufSql.append(" FEXRATE6,");
                bufSql.append(" FEXRATE7,");
                bufSql.append(" FEXRATE8,");
                bufSql.append(" FDESC,");
                bufSql.append(" FDATASOURCE,");
                bufSql.append(" FCHECKSTATE,");
                bufSql.append(" FCREATOR,");
                bufSql.append(" FCREATETIME,");
                bufSql.append(" FCHECKUSER,");
                bufSql.append(" FCHECKTIME");
                bufSql.append(" ) ");
                bufSql.append(" SELECT  ");
                bufSql.append(" FEXRATESRCCODE,");
                bufSql.append(" FCURYCODE,");
                bufSql.append(" ' ',");
                bufSql.append(" FEXRATEDATE,");
                bufSql.append(" FEXRATETIME,");
                bufSql.append(" FPORTCODE,");
                bufSql.append(" FEXRATE1,");
                bufSql.append(" FEXRATE2,");
                bufSql.append(" FEXRATE3,");
                bufSql.append(" FEXRATE4,");
                bufSql.append(" FEXRATE5,");
                bufSql.append(" FEXRATE6,");
                bufSql.append(" FEXRATE7,");
                bufSql.append(" FEXRATE8,");
                bufSql.append(" FDESC,");
                bufSql.append(" FDATASOURCE,");
                bufSql.append(" FCHECKSTATE,");
                bufSql.append(" FCREATOR,");
                bufSql.append(" FCREATETIME,");
                bufSql.append(" FCHECKUSER,");
                bufSql.append(" FCHECKTIME");
                bufSql.append(" FROM TB_12012008025340 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                //设定表的主键
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_EXCHANGERATE ADD CONSTRAINT PK_TB_" + sPre +
                               "_Data_Exc " +
                               "PRIMARY KEY (FEXRATESRCCODE,FCURYCODE,FMARKCURY,FEXRATEDATE,FEXRATETIME,FPORTCODE)");
                bTrans = false;
            }

            /**
             * date   : 20081201
             * author : linjunyun
             * desc   : 净值数据表 TB_001_Data_NavData
             *          按日期，增加一条记录用来存储净值表中的累计净值，AccumulateUnit
             * BugID  :
             */
            bTrans = false;
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO " +
                          pub.yssGetTableName("TB_Data_NavData") + "(");
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
            bufSql.append(" FGRADETYPE6");
            bufSql.append(" ) ");
            bufSql.append(" SELECT  ");
            bufSql.append(" FNAVDATE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" 'Total7', ");
            bufSql.append(" FRETYPECODE, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" 'AccumulateUnit', ");
            bufSql.append(" FINOUT, ");
            bufSql.append(" '累计净值：', ");
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
            bufSql.append(" FGRADETYPE6");
            bufSql.append(" FROM " + pub.yssGetTableName("TB_Data_NavData"));
            bufSql.append(
                " where FORDERCODE = 'Total3' and FKEYCODE = 'Unit' and FNAVDATE not in");
            bufSql.append(" (select FNAVDATE from tb_" + sPre + "_data_NavData where FORDERCODE = 'Total7' and FKEYCODE = 'AccumulateUnit')");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            /**
             * date   : 20081203
             * author : linjunyun
             * desc   : TA业务--交易数据表，更改表 TB_001_TA_Trade 新增字段 FBEMARKMONEY
             * BugID  :
             */
            if (existsTabColumn_DB2("TB_" + sPre + "_TA_TRADE", "FBeMarkMoney")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                //获得主键
                strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_TA_TRADE");
                //判断主键是否存在，存在则删除主键
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_TA_TRADE DROP CONSTRAINT " + strPKName);
                }
                //判断临时表是否存在 如果临时表已经存在则删除此表
                if (dbl.yssTableExist("TB_12032008062030")) {
                    this.dropTableByTableName("TB_12032008062030");
                }
                //重命名原有表为临时表
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_TA_TRADE TO TB_12032008062030");
                //创建包含新字段的表
                bufSql.append(" CREATE TABLE TB_" + sPre + "_TA_TRADE ");
                bufSql.append(" ( ");
                bufSql.append("FNUM           VARCHAR(20)    NOT NULL,");
                bufSql.append("FTRADEDATE     DATE           NOT NULL,");
                bufSql.append("FMARKDATE      DATE,");
                bufSql.append("FPORTCODE      VARCHAR(20)    NOT NULL,");
                bufSql.append("FPORTCLSCODE   VARCHAR(20)    NOT NULL,");
                bufSql.append("FSELLNETCODE   VARCHAR(20)    NOT NULL,");
                bufSql.append("FSELLTYPE      VARCHAR(20)    NOT NULL,");
                bufSql.append("FCURYCODE      VARCHAR(20)    NOT NULL,");
                bufSql.append("FANALYSISCODE1 VARCHAR(20)    NOT NULL,");
                bufSql.append("FANALYSISCODE2 VARCHAR(20)    NOT NULL,");
                bufSql.append("FANALYSISCODE3 VARCHAR(20)    NOT NULL,");
                bufSql.append("FCASHACCCODE   VARCHAR(20)    NOT NULL,");
                bufSql.append("FSELLMONEY     DECIMAL(18,4)  NOT NULL,");
                bufSql.append("FBeMarkMoney   DECIMAL(18,4)  NOT NULL,");
                bufSql.append("FSELLAMOUNT    DECIMAL(18,4)  NOT NULL,");
                bufSql.append("FSELLPRICE     DECIMAL(18,4)  NOT NULL,");
                bufSql.append("FINCOMENOTBAL  DECIMAL(18,4),");
                bufSql.append("FINCOMEBAL     DECIMAL(18,4),");
                bufSql.append("FCONFIMDATE    DATE           NOT NULL,");
                bufSql.append("FSETTLEDATE    DATE           NOT NULL,");
                bufSql.append("FSETTLEMONEY   DECIMAL(18,4),");
                bufSql.append("FPORTCURYRATE  DECIMAL(20,15) NOT NULL,");
                bufSql.append("FBASECURYRATE  DECIMAL(20,15) NOT NULL,");
                bufSql.append("FFEECODE1      VARCHAR(20),");
                bufSql.append("FTRADEFEE1     DECIMAL(18,4),");
                bufSql.append("FFEECODE2      VARCHAR(20),");
                bufSql.append("FTRADEFEE2     DECIMAL(18,4),");
                bufSql.append("FFEECODE3      VARCHAR(20),");
                bufSql.append("FTRADEFEE3     DECIMAL(18,4),");
                bufSql.append("FFEECODE4      VARCHAR(20),");
                bufSql.append("FTRADEFEE4     DECIMAL(18,4),");
                bufSql.append("FFEECODE5      VARCHAR(20),");
                bufSql.append("FTRADEFEE5     DECIMAL(18,4),");
                bufSql.append("FFEECODE6      VARCHAR(20),");
                bufSql.append("FTRADEFEE6     DECIMAL(18,4),");
                bufSql.append("FFEECODE7      VARCHAR(20),");
                bufSql.append("FTRADEFEE7     DECIMAL(18,4),");
                bufSql.append("FFEECODE8      VARCHAR(20),");
                bufSql.append("FTRADEFEE8     DECIMAL(18,4),");
                bufSql.append("FSETTLESTATE   DECIMAL(1)     NOT NULL DEFAULT 0,");
                bufSql.append("FDESC          VARCHAR(100),");
                bufSql.append("FCHECKSTATE    DECIMAL(1)     NOT NULL,");
                bufSql.append("FCREATOR       VARCHAR(20)    NOT NULL,");
                bufSql.append("FCREATETIME    VARCHAR(20)    NOT NULL,");
                bufSql.append("FCHECKUSER     VARCHAR(20),");
                bufSql.append("FCHECKTIME     VARCHAR(20)");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                conn.setAutoCommit(bTrans);
                bTrans = true;
                //从临时表中导数据到新创建的表中
                bufSql.append(" INSERT INTO TB_" + sPre + "_TA_TRADE( ");
                bufSql.append("FNum,");
                bufSql.append("FTRADEDATE,");
                bufSql.append("FMarkDate,");
                bufSql.append("FPORTCODE,");
                bufSql.append("FPORTCLSCODE,");
                bufSql.append("FSELLNETCODE,");
                bufSql.append("FSELLTYPE,");
                bufSql.append("FCURYCODE,");
                bufSql.append("FANALYSISCODE1,");
                bufSql.append("FANALYSISCODE2,");
                bufSql.append("FANALYSISCODE3,");
                bufSql.append("FCASHACCCODE,");
                bufSql.append("FSELLMONEY,");
                bufSql.append("FBEMARKMONEY,");
                bufSql.append("FSELLAMOUNT,");
                bufSql.append("FSELLPRICE,");
                bufSql.append("FINCOMENOTBAL,");
                bufSql.append("FINCOMEBAL,");
                bufSql.append("FCONFIMDATE,");
                bufSql.append("FSETTLEDATE,");
                bufSql.append("FSETTLEMONEY,");
                bufSql.append("FPORTCURYRATE,");
                bufSql.append("FBASECURYRATE,");
                bufSql.append("FFEECODE1,");
                bufSql.append("FTRADEFEE1,");
                bufSql.append("FFEECODE2,");
                bufSql.append("FTRADEFEE2,");
                bufSql.append("FFEECODE3,");
                bufSql.append("FTRADEFEE3,");
                bufSql.append("FFEECODE4,");
                bufSql.append("FTRADEFEE4,");
                bufSql.append("FFEECODE5,");
                bufSql.append("FTRADEFEE5,");
                bufSql.append("FFEECODE6,");
                bufSql.append("FTRADEFEE6,");
                bufSql.append("FFEECODE7,");
                bufSql.append("FTRADEFEE7,");
                bufSql.append("FFEECODE8,");
                bufSql.append("FTRADEFEE8,");
                bufSql.append("FSETTLESTATE,");
                bufSql.append("FDESC,");
                bufSql.append("FCHECKSTATE,");
                bufSql.append("FCREATOR,");
                bufSql.append("FCREATETIME,");
                bufSql.append("FCHECKUSER,");
                bufSql.append("FCHECKTIME");
                bufSql.append(" ) ");
                bufSql.append(" SELECT  ");
                bufSql.append("FNum,");
                bufSql.append("FTRADEDATE,");
                bufSql.append("FMARKDATE,");
                bufSql.append("CASE WHEN FPORTCODE IS NULL THEN '' ELSE FPORTCODE END,");
                bufSql.append("FPORTCLSCODE,");
                bufSql.append("FSELLNETCODE,");
                bufSql.append("FSELLTYPE,");
                bufSql.append("FCURYCODE,");
                bufSql.append("FANALYSISCODE1,");
                bufSql.append("FANALYSISCODE2,");
                bufSql.append("FANALYSISCODE3,");
                bufSql.append("FCASHACCCODE,");
                bufSql.append("FSELLMONEY,");
                bufSql.append("0,");
                bufSql.append("FSELLAMOUNT,");
                bufSql.append("FSELLPRICE,");
                bufSql.append("FINCOMENOTBAL,");
                bufSql.append("FINCOMEBAL,");
                bufSql.append("FCONFIMDATE,");
                bufSql.append("FSETTLEDATE,");
                bufSql.append("FSETTLEMONEY,");
                bufSql.append("FPORTCURYRATE,");
                bufSql.append("FBASECURYRATE,");
                bufSql.append("FFEECODE1,");
                bufSql.append("FTRADEFEE1,");
                bufSql.append("FFEECODE2,");
                bufSql.append("FTRADEFEE2,");
                bufSql.append("FFEECODE3,");
                bufSql.append("FTRADEFEE3,");
                bufSql.append("FFEECODE4,");
                bufSql.append("FTRADEFEE4,");
                bufSql.append("FFEECODE5,");
                bufSql.append("FTRADEFEE5,");
                bufSql.append("FFEECODE6,");
                bufSql.append("FTRADEFEE6,");
                bufSql.append("FFEECODE7,");
                bufSql.append("FTRADEFEE7,");
                bufSql.append("FFEECODE8,");
                bufSql.append("FTRADEFEE8,");
                bufSql.append("FSETTLESTATE,");
                bufSql.append("FDESC,");
                bufSql.append("FCHECKSTATE,");
                bufSql.append("FCREATOR,");
                bufSql.append("FCREATETIME,");
                bufSql.append("FCHECKUSER,");
                bufSql.append("FCHECKTIME");
                bufSql.append(" FROM TB_12032008062030 ");
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                //设定表的主键
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_TA_TRADE ADD CONSTRAINT PK_TB_" + sPre +
                               "_TA_Trade " +
                               "PRIMARY KEY (FNUM)");
                bTrans = false;
            }
            //===调整表 划款指令表的 FCASHUSAGE 的长度为400
            dbl.executeSql(" alter table tb_" + sPre + "_cash_command alter FCASHUSAGE set data type varchar(400)");
        } catch (Exception ex) {
            throw new YssException("版本DB21010007更新表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
