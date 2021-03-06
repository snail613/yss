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
public class Ora1010007
    extends BaseDbUpdate {
    public Ora1010007() {
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
             *          FAgreement varchar2(2000) null
             */
            //表中不存在FAgreement字段，才执行更新
            if (existsTabColumn_Ora("TB_BASE_COUNTRY", "FAGREEMOUNT")) {
                //设置事物参数状态 -- 事物回滚
                bTrans = true;
                //设置事物不自动提交
                conn.setAutoCommit(false);
                //获取主键约束
                strPKName = this.getIsNullPKByTableName_Ora("TB_BASE_COUNTRY");
                if (strPKName != null && !strPKName.trim().equals("")) {
                    //删除约束
                    dbl.executeSql("ALTER TABLE TB_BASE_COUNTRY DROP CONSTRAINT " +
                                   strPKName);
                    //删除索引
                    deleteIndex(strPKName);
                }
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_BASE_CO_11252008073416000")) {
                    this.dropTableByTableName("TB_BASE_CO_11252008073416000");
                }
                //将原表更改为备份表
                dbl.executeSql(
                    "ALTER TABLE TB_BASE_COUNTRY RENAME TO TB_BASE_CO_11252008073416000");
                //销毁StringBuffer对象 并重新构造
                bufSql = null;
                bufSql = new StringBuffer();
                //创建新结构的表
                bufSql.append("CREATE TABLE TB_BASE_COUNTRY(");
                bufSql.append("FCOUNTRYCODE      VARCHAR2(20)   NOT NULL,");
                bufSql.append("FCOUNTRYNAME      VARCHAR2(50)   NOT NULL,");
                bufSql.append("FREGIONCODE       VARCHAR2(20)       NULL,");
                bufSql.append("FCOUNTRYSHORTNAME VARCHAR2(20)   NOT NULL,");
                bufSql.append("FINTERDOMAIN      VARCHAR2(20)       NULL,");
                bufSql.append("FPHONECODE        VARCHAR2(20)       NULL,");
                bufSql.append("FDIFFTIME         VARCHAR2(20)       NULL,");
                bufSql.append("FAgreement        VARCHAR2(2000)     NULL,");
                bufSql.append("FDESC             VARCHAR2(200)      NULL,");
                bufSql.append("FCHECKSTATE       DECIMAL(1)     NOT NULL,");
                bufSql.append("FCREATOR          VARCHAR2(20)   NOT NULL,");
                bufSql.append("FCREATETIME       VARCHAR2(20)   NOT NULL,");
                bufSql.append("FCHECKUSER        VARCHAR2(20)       NULL,");
                bufSql.append("FCHECKTIME        VARCHAR2(20)       NULL)");
                dbl.executeSql(bufSql.toString());
                //销毁StringBuffer对象 并重新构造
                bufSql = null;
                bufSql = new StringBuffer();
                //将原始表中的数据导入新表
                bufSql.append("INSERT INTO TB_BASE_COUNTRY(");
                bufSql.append("FCOUNTRYCODE,FCOUNTRYNAME,FREGIONCODE,");
                bufSql.append("FCOUNTRYSHORTNAME,FINTERDOMAIN,FPHONECODE,");
                bufSql.append("FDIFFTIME,FAGREEMENT,FDESC,FCHECKSTATE,");
                bufSql.append("FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)");
                bufSql.append(" SELECT ");
                bufSql.append("FCOUNTRYCODE,FCOUNTRYNAME,FREGIONCODE,");
                bufSql.append("FCOUNTRYSHORTNAME,FINTERDOMAIN,FPHONECODE,");
                bufSql.append("FDIFFTIME,NULL,FDESC,FCHECKSTATE,FCREATOR,");
                bufSql.append("FCREATETIME,FCHECKUSER,FCHECKTIME ");
                bufSql.append("FROM TB_BASE_CO_11252008073416000");
                dbl.executeSql(bufSql.toString());
                //添加约束
                dbl.executeSql("ALTER TABLE TB_BASE_COUNTRY ADD CONSTRAINT PK_TB_BASE_COUNTRY PRIMARY KEY (FCOUNTRYCODE)");

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
            if (existsTabColumn_Ora("TB_" + sPre + "_PARA_RECEIVER",
                                    "FTITLE")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                //获得主键
                strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                    "TB_PARA_RECEIVER"));
                //判断主键是否存在，存在则删除主键
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE " +
                                   pub.yssGetTableName("TB_PARA_RECEIVER") +
                                   " DROP CONSTRAINT " + strPKName);
                    deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
                }
                //判断临时表是否存在 如果临时表已经存在则删除此表
                if (dbl.yssTableExist("TB_" + sPre + "_PAR_11202008055258000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_PAR_11202008055258000");
                }
                //重命名原有表为临时表
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_PARA_RECEIVER") +
                               " RENAME TO TB_" + sPre + "_PAR_11202008055258000");
                bufSql.delete(0, bufSql.length());
                //创建包含新字段的表
                bufSql.append("CREATE TABLE " +
                              pub.yssGetTableName("TB_PARA_RECEIVER"));
                bufSql.append(" ( ");
                bufSql.append(" FRECEIVERCODE      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FRECEIVERNAME      VARCHAR2(200)  NOT NULL, ");
                bufSql.append(" FRECEIVERSHORTNAME VARCHAR2(50)      NULL, ");
                bufSql.append(" FTITLE             VARCHAR2(100) NOT NULL, ");
                bufSql.append(" FOFFICEADDR        VARCHAR2(200)     NULL, ");
                bufSql.append(" FPOSTALCODE        VARCHAR2(20)      NULL, ");
                bufSql.append(" FOPERBANK          VARCHAR2(100) NOT NULL, ");
                bufSql.append(" FACCOUNTNUMBER     VARCHAR2(100) NOT NULL, ");
                bufSql.append(" FCURYCODE          VARCHAR2(20)      NULL, ");
                bufSql.append(" FPORTCODE          VARCHAR2(20)      NULL, ");
                bufSql.append(" FCASHACCCODE       VARCHAR2(20)      NULL, ");
                bufSql.append(" FANALYSISCODE1     VARCHAR2(20)      NULL, ");
                bufSql.append(" FANALYSISCODE2     VARCHAR2(20)      NULL, ");
                bufSql.append(" FANALYSISCODE3     VARCHAR2(20)      NULL, ");
                bufSql.append(" FDESC              VARCHAR2(200)     NULL, ");
                bufSql.append(" FCHECKSTATE        NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR           VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME        VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER         VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME         VARCHAR2(20)      NULL ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                //从临时表中导数据到新创建的表中
                bufSql.append("INSERT INTO " +
                              pub.yssGetTableName("TB_PARA_RECEIVER") + "(");
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
                bufSql.append(" FROM TB_" + sPre + "_PAR_11202008055258000");
                conn.setAutoCommit(bTrans);
                bTrans = true;
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                //设定表的主键
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_PARA_RECEIVER") +
                               " ADD CONSTRAINT PK_TB_" + sPre +
                               "_PARA_RECEIVER " +
                               " PRIMARY KEY (FRECEIVERCODE)");
            }
            //----------------------------------------------------------------//
            /**
             * date   : 2008-11-27
             * author : 王晓光
             * desc   : 给表TB_001_Comp_ResultData添加字段FNumerator,FDenominator,FFactRatio
             * BugID  : MS00040
             */
            if (this.existsTabColumn_Ora("Tb_" + sPre + "_Comp_ResultData",
                                         "FNumerator,FDenominator,FFactRatio")) {
                conn.setAutoCommit(bTrans);
                bTrans = true;
                String sql = "ALTER TABLE TB_" + sPre +
                    "_COMP_RESULTDATA ADD FNUMERATOR NUMBER(18,4)     NULL ";
                dbl.executeSql(sql);
                conn.setAutoCommit(bTrans);
                bTrans = false;
            }
            if (this.existsTabColumn_Ora("Tb_" + sPre + "_Comp_ResultData",
                                         "FDenominator")) {
                conn.setAutoCommit(bTrans);
                bTrans = true;
                String sql = " ALTER TABLE TB_" + sPre +
                    "_COMP_RESULTDATA ADD FDENOMINATOR NUMBER(18,4)     NULL";
                dbl.executeSql(sql);
                conn.setAutoCommit(bTrans);
                bTrans = false;
            }
            if (this.existsTabColumn_Ora("Tb_" + sPre + "_Comp_ResultData",
                                         "FFactRatio")) {
                conn.setAutoCommit(bTrans);
                bTrans = true;
                String sql = "ALTER TABLE TB_" + sPre +
                    "_COMP_RESULTDATA ADD FFACTRATIO NUMBER(30,12)     NULL";
                dbl.executeSql(sql);
                conn.setAutoCommit(bTrans);
                bTrans = false;
            }

            /**
             * date   : 20081201
             * author : linjunyun
             * desc   : 汇率数据表 TB_001_DATA_EXCHANGERATE
             *          新增字段 FMARKCURY
             * BugID  : Ms00011
             */
            if (existsTabColumn_Ora("TB_" + sPre + "_DATA_EXCHANGERATE",
                                    "FMARKCURY")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                //获得主键
                strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                    "TB_DATA_EXCHANGERATE"));
                //判断主键是否存在，存在则删除主键
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE " +
                                   pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                                   " DROP CONSTRAINT " + strPKName);
                    deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
                }
                //判断临时表是否存在 如果临时表已经存在则删除此表
                if (dbl.yssTableExist("TB_" + sPre + "_DAT_11212008090900000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_DAT_11212008090900000");
                }
                //重命名原有表为临时表
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                               " RENAME TO TB_" + sPre + "_DAT_11212008090900000");
                bufSql.delete(0, bufSql.length());
                //创建包含新字段的表
                bufSql.append("CREATE TABLE " +
                              pub.yssGetTableName("TB_DATA_EXCHANGERATE"));
                bufSql.append(" ( ");
                bufSql.append("    FEXRATESRCCODE VARCHAR2(20)  NOT NULL,");
                bufSql.append("    FCURYCODE      VARCHAR2(20)  NOT NULL,");
                bufSql.append(
                    "    FMARKCURY      VARCHAR2(20)  DEFAULT ' ' NOT NULL,");
                bufSql.append("    FEXRATEDATE    DATE          NOT NULL,");
                bufSql.append("    FEXRATETIME    VARCHAR2(20)  NOT NULL,");
                bufSql.append("    FPORTCODE      VARCHAR2(20)  NOT NULL,");
                bufSql.append("    FEXRATE1       NUMBER(20,15)     NULL,");
                bufSql.append("    FEXRATE2       NUMBER(20,15)     NULL,");
                bufSql.append("    FEXRATE3       NUMBER(20,15)     NULL,");
                bufSql.append("    FEXRATE4       NUMBER(20,15)     NULL,");
                bufSql.append("    FEXRATE5       NUMBER(20,15)     NULL,");
                bufSql.append("    FEXRATE6       NUMBER(20,15)     NULL,");
                bufSql.append("    FEXRATE7       NUMBER(20,15)     NULL,");
                bufSql.append("    FEXRATE8       NUMBER(20,15)     NULL,");
                bufSql.append("    FDESC          VARCHAR2(100)     NULL,");
                bufSql.append("    FDATASOURCE    NUMBER(1)     NOT NULL,");
                bufSql.append("    FCHECKSTATE    NUMBER(1)     NOT NULL,");
                bufSql.append("    FCREATOR       VARCHAR2(20)  NOT NULL,");
                bufSql.append("    FCREATETIME    VARCHAR2(20)  NOT NULL,");
                bufSql.append("    FCHECKUSER     VARCHAR2(20)      NULL,");
                bufSql.append("    FCHECKTIME     VARCHAR2(20)      NULL");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                //从临时表中导数据到新创建的表中
                bufSql.append("INSERT INTO " +
                              pub.yssGetTableName("TB_DATA_EXCHANGERATE") + "(");
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
                bufSql.append(" FROM TB_" + sPre + "_DAT_11212008090900000");
                conn.setAutoCommit(bTrans);
                bTrans = true;
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                //设定表的主键
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                               " ADD CONSTRAINT PK_TB_" + sPre +
                               "_DATA_EXCHANGERATE " +
                               " PRIMARY KEY (FEXRATESRCCODE,FCURYCODE,FMARKCURY,FEXRATEDATE,FEXRATETIME,FPORTCODE)");
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
            bufSql.append(" where FORDERCODE = 'Total3' and FKEYCODE = 'Unit' and FNAVDATE not in");
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
            if (existsTabColumn_Ora("TB_" + sPre + "_TA_TRADE",
                                    "FBEMARKMONEY")) {
                bTrans = false;
                bufSql.delete(0, bufSql.length());
                //获得主键
                strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                    "TB_TA_TRADE"));
                //判断主键是否存在，存在则删除主键
                if (strPKName.trim().length() != 0) {
                    dbl.executeSql("ALTER TABLE " +
                                   pub.yssGetTableName("TB_TA_TRADE") +
                                   " DROP CONSTRAINT " + strPKName);
                    deleteIndex(strPKName); //若是没有级联删除索引，则手工删除。
                }
                //判断临时表是否存在 如果临时表已经存在则删除此表
                if (dbl.yssTableExist("TB_" + sPre + "_TA__12032008061909000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_TA__12032008061909000");
                }
                //重命名原有表为临时表
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_TA_TRADE") +
                               " RENAME TO TB_" + sPre + "_TA__12032008061909000");
                bufSql.delete(0, bufSql.length());
                //创建包含新字段的表
                bufSql.append("CREATE TABLE " +
                              pub.yssGetTableName("TB_TA_TRADE"));
                bufSql.append(" ( ");
                bufSql.append("FNum           VARCHAR2(20)  NOT NULL,");
                bufSql.append("FTRADEDATE     DATE          NOT NULL,");
                bufSql.append("FMarkDate      DATE              NULL,");
                bufSql.append("FPORTCODE      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FPORTCLSCODE   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSELLNETCODE   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSELLTYPE      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCURYCODE      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE1 VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE2 VARCHAR2(20)  NOT NULL,");
                bufSql.append("FANALYSISCODE3 VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCASHACCCODE   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FSELLMONEY     NUMBER(18,4)  NOT NULL,");
                bufSql.append("FBEMARKMONEY   NUMBER(18,4)  NOT NULL,");
                bufSql.append("FSELLAMOUNT    NUMBER(18,4)  NOT NULL,");
                bufSql.append("FSELLPRICE     NUMBER(18,4)  NOT NULL,");
                bufSql.append("FINCOMENOTBAL  NUMBER(18,4)      NULL,");
                bufSql.append("FINCOMEBAL     NUMBER(18,4)      NULL,");
                bufSql.append("FCONFIMDATE    DATE          NOT NULL,");
                bufSql.append("FSETTLEDATE    DATE          NOT NULL,");
                bufSql.append("FSETTLEMONEY   NUMBER(18,4)      NULL,");
                bufSql.append("FPORTCURYRATE  NUMBER(20,15) NOT NULL,");
                bufSql.append("FBASECURYRATE  NUMBER(20,15) NOT NULL,");
                bufSql.append("FFEECODE1      VARCHAR2(20)      NULL,");
                bufSql.append("FTRADEFEE1     NUMBER(18,4)      NULL,");
                bufSql.append("FFEECODE2      VARCHAR2(20)      NULL,");
                bufSql.append("FTRADEFEE2     NUMBER(18,4)      NULL,");
                bufSql.append("FFEECODE3      VARCHAR2(20)      NULL,");
                bufSql.append("FTRADEFEE3     NUMBER(18,4)      NULL,");
                bufSql.append("FFEECODE4      VARCHAR2(20)      NULL,");
                bufSql.append("FTRADEFEE4     NUMBER(18,4)      NULL,");
                bufSql.append("FFEECODE5      VARCHAR2(20)      NULL,");
                bufSql.append("FTRADEFEE5     NUMBER(18,4)      NULL,");
                bufSql.append("FFEECODE6      VARCHAR2(20)      NULL,");
                bufSql.append("FTRADEFEE6     NUMBER(18,4)      NULL,");
                bufSql.append("FFEECODE7      VARCHAR2(20)      NULL,");
                bufSql.append("FTRADEFEE7     NUMBER(18,4)      NULL,");
                bufSql.append("FFEECODE8      VARCHAR2(20)      NULL,");
                bufSql.append("FTRADEFEE8     NUMBER(18,4)      NULL,");
                bufSql.append("FSETTLESTATE   NUMBER(1)     DEFAULT 0 NOT NULL,");
                bufSql.append("FDESC          VARCHAR2(100)     NULL,");
                bufSql.append("FCHECKSTATE    NUMBER(1)     NOT NULL,");
                bufSql.append("FCREATOR       VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCREATETIME    VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCHECKUSER     VARCHAR2(20)      NULL,");
                bufSql.append("FCHECKTIME     VARCHAR2(20)      NULL");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                //从临时表中导数据到新创建的表中
                bufSql.append("INSERT INTO " +
                              pub.yssGetTableName("TB_TA_TRADE") + "(");
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
                bufSql.append("NVL(FPORTCODE,' '),");
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
                bufSql.append(" FROM TB_" + sPre + "_TA__12032008061909000");
                conn.setAutoCommit(bTrans);
                bTrans = true;
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                //设定表的主键
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_TA_TRADE") +
                               " ADD CONSTRAINT PK_TB_" + sPre +
                               "_TA_TRADE " +
                               " PRIMARY KEY (FNUM)");
            }
            //===调整表 划款指令表的 FCASHUSAGE 的长度为400
            dbl.executeSql("alter table tb_" + sPre + "_cash_command modify FCASHUSAGE varchar2(400)");
        } catch (Exception ex) {
            throw new YssException("版本Oracle1010007更新表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
