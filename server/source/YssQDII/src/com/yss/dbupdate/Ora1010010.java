package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.*;

public class Ora1010010
    extends BaseDbUpdate {
    public Ora1010010() {
    }

    /**
     * author : 曹丞
     * date   : 2009/01/05
     * bugid  : MS000126
     * fdesc  : 在交易数据表中把交易价格的字段调整为numeric(20,8),
     * 在订单制作表中把交易价格调整到numeric(20,8),订单管理表把确认价格调整到numeric(20,8)
     * @param sPre String
     * @throws YssException
     */

    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(8000);
        Connection conn = dbl.loadConnection();
        try {
            //设置事物参数状态 -- 事物回滚
            bTrans = true;
            //设置事物不自动提交
            conn.setAutoCommit(false);
            //----------------------获取主键约束(对Tb_001_Data_Trade字段FtradePrice修改)-------------------------------------//
            strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_DATA_TRADE");
            if (strPKName != null && !strPKName.trim().equals("")) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_TRADE DROP CONSTRAINT " +
                               strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在,删除备份表
            if (dbl.yssTableExist("TB_" + sPre + "_DATA_01052009063313000 ")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_DATA_01052009063313000 ");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre + "_DATA_TRADE RENAME TO TB_" +
                sPre + "_DATA_01052009063313000");
            //清空bufsql
            bufSql.delete(0, bufSql.length());
            //创建一个新表
            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_DATA_TRADE"));
            bufSql.append("(");
            bufSql.append("FNUM VARCHAR2(15)  NOT NULL,");
            bufSql.append("FSECURITYCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FPORTCODE        VARCHAR2(20)      NULL,");
            bufSql.append("FBROKERCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FINVMGRCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRADETYPECODE   VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCASHACCCODE     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FATTRCLSCODE     VARCHAR2(20)      NULL,");
            bufSql.append("FBARGAINDATE     DATE          NOT NULL,");
            bufSql.append("FBARGAINTIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSETTLEDATE      DATE          NOT NULL,");
            bufSql.append("FSETTLETIME      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FAUTOSETTLE      NUMBER(1)     NOT NULL,");
            bufSql.append("FPORTCURYRATE    NUMBER(20,15) NOT NULL,");
            bufSql.append("FBASECURYRATE    NUMBER(20,15) NOT NULL,");
            bufSql.append("FALLOTFACTOR     NUMBER(18,4)  NOT NULL,");
            bufSql.append("FTRADEAMOUNT     NUMBER(18,4)  NOT NULL,");
            bufSql.append("FTRADEPRICE      NUMBER(20,8)  NOT NULL,");
            bufSql.append("FTRADEMONEY      NUMBER(18,4)  NOT NULL,");
            bufSql.append("FUNITCOST        NUMBER(18,12)     NULL,");
            bufSql.append("FACCRUEDINTEREST NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE1        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE1       NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE2        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE2       NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE3        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE3       NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE4        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE4       NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE5        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE5       NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE6        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE6       NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE7        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE7       NUMBER(18,4)      NULL,");
            bufSql.append("FFEECODE8        VARCHAR2(20)      NULL,");
            bufSql.append("FTRADEFEE8       NUMBER(18,4)      NULL,");
            bufSql.append("FTOTALCOST       NUMBER(18,4)      NULL,");
            bufSql.append("FBAILMONEY       NUMBER(18,4)      NULL,");
            bufSql.append("FORDERNUM        VARCHAR2(20)      NULL,");
            bufSql.append("FDESC            VARCHAR2(100)     NULL,");
            bufSql.append("FCHECKSTATE      NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR         VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER       VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME       VARCHAR2(20)      NULL");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString()); //执行语句
            bufSql.delete(0, bufSql.length()); //清空bufSql缓存

            //将临时表中的数据导入至新表
            bufSql.append("INSERT INTO " + pub.yssGetTableName("TB_DATA_TRADE") + "(");
            bufSql.append("FNUM,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FBROKERCODE,");
            bufSql.append("FINVMGRCODE,");
            bufSql.append("FTRADETYPECODE,");
            bufSql.append("FCASHACCCODE,");
            bufSql.append("FATTRCLSCODE,");
            bufSql.append("FBARGAINDATE,");
            bufSql.append("FBARGAINTIME,");
            bufSql.append("FSETTLEDATE,");
            bufSql.append("FSETTLETIME,");
            bufSql.append("FAUTOSETTLE,");
            bufSql.append("FPORTCURYRATE,");
            bufSql.append("FBASECURYRATE,");
            bufSql.append("FALLOTFACTOR,");
            bufSql.append("FTRADEAMOUNT,");
            bufSql.append("FTRADEPRICE,");
            bufSql.append("FTRADEMONEY,");
            bufSql.append("FUNITCOST,");
            bufSql.append("FACCRUEDINTEREST,");
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
            bufSql.append("FTOTALCOST,");
            bufSql.append("FBAILMONEY,");
            bufSql.append("FORDERNUM,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME )");
            //从备份表中取出数据
            bufSql.append(" SELECT ");
            bufSql.append("FNUM,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FBROKERCODE,");
            bufSql.append("FINVMGRCODE,");
            bufSql.append("FTRADETYPECODE,");
            bufSql.append("FCASHACCCODE,");
            bufSql.append("FATTRCLSCODE,");
            bufSql.append("FBARGAINDATE,");
            bufSql.append("FBARGAINTIME,");
            bufSql.append("FSETTLEDATE,");
            bufSql.append("FSETTLETIME,");
            bufSql.append("FAUTOSETTLE,");
            bufSql.append("FPORTCURYRATE,");
            bufSql.append("FBASECURYRATE,");
            bufSql.append("FALLOTFACTOR,");
            bufSql.append("FTRADEAMOUNT,");
            bufSql.append("FTRADEPRICE,");
            bufSql.append("FTRADEMONEY,");
            bufSql.append("FUNITCOST,");
            bufSql.append("FACCRUEDINTEREST,");
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
            bufSql.append("FTOTALCOST,");
            bufSql.append("FBAILMONEY,");
            bufSql.append("FORDERNUM,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_DATA_01052009063313000");

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //设定表的主键
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_DATA_TRADE") +
                           " ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_TRADE PRIMARY KEY (FNUM)");
            //------------------Tb_001_Data_Trade修改完成-----------------------------------------------//




            //-----------------获取主键约束(TB_001_ORDER_CONFIRM的字段Fprice修改)--------------------------------------//
            strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_ORDER_CONFIRM");
            if (strPKName != null && !strPKName.trim().equals("")) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_ORDER_CONFIRM DROP CONSTRAINT " +
                               strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在,删除备份表
            if (dbl.yssTableExist("TB_" + sPre + "_ORDER_01062009022323000 ")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_ORDER_01062009022323000");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre + "_ORDER_CONFIRM RENAME TO TB_" +
                sPre + "_ORDER_01062009022323000");
            //清空bufsql
            bufSql.delete(0, bufSql.length());
            //创建新表TB_ORDER_CONFIRM
            bufSql.append("CREATE TABLE " + pub.yssGetTableName("TB_ORDER_CONFIRM"));
            bufSql.append("(");
            bufSql.append("FTRADENUM      VARCHAR2(15)  NOT NULL,");
            bufSql.append("FINVMGRCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSECURITYCODE  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FBROKERCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FPORTCODE      VARCHAR2(20)      NULL,");
            bufSql.append("FTRADETYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("FAMOUNT        NUMBER(18,4)  NOT NULL,");
            bufSql.append("FINTEREST      NUMBER(18,4)  NOT NULL,");
            bufSql.append("FPRICE         NUMBER(20,8)  NOT NULL,");
            bufSql.append("FYIELD         NUMBER(18,12)     NULL,");
            bufSql.append("FTRANSDATE     DATE          NOT NULL,");
            bufSql.append("FTRANSTIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FDESC          VARCHAR2(100)     NULL,");
            bufSql.append("FCHECKSTATE    NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER     VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME     VARCHAR2(20)      NULL");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString()); //执行语句
            bufSql.delete(0, bufSql.length()); //清空bufSql缓存

            //将临时表数据导入到新表
            bufSql.append("INSERT INTO " + pub.yssGetTableName("TB_ORDER_CONFIRM"));
            bufSql.append("(");
            bufSql.append("FTRADENUM,");
            bufSql.append("FINVMGRCODE,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FBROKERCODE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FTRADETYPECODE,");
            bufSql.append("FAMOUNT,");
            bufSql.append("FINTEREST,");
            bufSql.append("FPRICE,");
            bufSql.append("FYIELD,");
            bufSql.append("FTRANSDATE,");
            bufSql.append("FTRANSTIME,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(")");
            //从备份表中取出数据
            bufSql.append(" SELECT ");
            bufSql.append("FTRADENUM,");
            bufSql.append("FINVMGRCODE,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FBROKERCODE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FTRADETYPECODE,");
            bufSql.append("FAMOUNT,");
            bufSql.append("FINTEREST,");
            bufSql.append("FPRICE,");
            bufSql.append("FYIELD,");
            bufSql.append("FTRANSDATE,");
            bufSql.append("FTRANSTIME,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_ORDER_01062009022323000");

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //指定主键
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_ORDER_CONFIRM") +
                           " ADD CONSTRAINT PK_TB_" + sPre +
                           "_ORDER_CONFIRM PRIMARY KEY (FTRADENUM)");

            //------------------------------表Tb_001_ORDER_Confirm修改完毕--------------------------//





            //---------------------获取主键约束(修改表TB_001_ORDER_MAINTENANCE的字段FtradePrice、FvirtualPrice)----------------------------//
            strPKName = this.getIsNullPKByTableName("TB_" + sPre +
                "_ORDER_MAINTENANCE");
            if (strPKName != null && !strPKName.trim().equals("")) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_ORDER_MAINTENANCE  DROP CONSTRAINT " + strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在,删除备份表
            if (dbl.yssTableExist("TB_" + sPre + "_ORD_01062009022324000 ")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_ORDER_01062009022324000");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre + "_ORDER_MAINTENANCE RENAME TO TB_" +
                sPre + "_ORDER_01062009022324000");
            //清空bufsql
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append("CREATE TABLE  " + pub.yssGetTableName("TB_ORDER_MAINTENANCE"));
            bufSql.append("(");
            bufSql.append("FORDERNUM      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FINVMGRCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FBROKERCODE    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FPORTCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FSECURITYCODE  VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRADETYPECODE VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRADEAMOUNT   NUMBER(18,4)  NOT NULL,");
            bufSql.append("FINTEREST      NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FQUOTEMODE     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRADEPRICE    NUMBER(20,8)  NOT NULL,");
            bufSql.append("FVIRTUALPRICE  NUMBER(20,8)      NULL,");
            bufSql.append("FYIELD         NUMBER(18,12)     NULL,");
            bufSql.append("FORDERDATE     DATE          NOT NULL,");
            bufSql.append("FORDERTIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append("FTRADENUM      VARCHAR2(15)      NULL,");
            bufSql.append("FDESC          VARCHAR2(100)     NULL,");
            bufSql.append("FCHECKSTATE    NUMBER(1)     NOT NULL,");
            bufSql.append("FCREATOR       VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCREATETIME    VARCHAR2(20)  NOT NULL,");
            bufSql.append("FCHECKUSER     VARCHAR2(20)      NULL,");
            bufSql.append("FCHECKTIME     VARCHAR2(20)      NULL");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //将数据从临时表导入新表
            bufSql.append("INSERT INTO " + pub.yssGetTableName("TB_ORDER_MAINTENANCE"));
            bufSql.append("(");
            bufSql.append("FORDERNUM,");
            bufSql.append("FINVMGRCODE,");
            bufSql.append("FBROKERCODE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FTRADETYPECODE,");
            bufSql.append("FTRADEAMOUNT,");
            bufSql.append("FINTEREST,");
            bufSql.append("FQUOTEMODE,");
            bufSql.append("FTRADEPRICE,");
            bufSql.append("FVIRTUALPRICE,");
            bufSql.append("FYIELD,");
            bufSql.append("FORDERDATE,");
            bufSql.append("FORDERTIME,");
            bufSql.append("FTRADENUM,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(")");
            //从备份表取出数据
            bufSql.append(" SELECT ");
            bufSql.append("FORDERNUM,");
            bufSql.append("FINVMGRCODE,");
            bufSql.append("FBROKERCODE,");
            bufSql.append("FPORTCODE,");
            bufSql.append("FSECURITYCODE,");
            bufSql.append("FTRADETYPECODE,");
            bufSql.append("FTRADEAMOUNT,");
            bufSql.append("FINTEREST,");
            bufSql.append("FQUOTEMODE,");
            bufSql.append("FTRADEPRICE,");
            bufSql.append("FVIRTUALPRICE,");
            bufSql.append("FYIELD,");
            bufSql.append("FORDERDATE,");
            bufSql.append("FORDERTIME,");
            bufSql.append("FTRADENUM,");
            bufSql.append("FDESC,");
            bufSql.append("FCHECKSTATE,");
            bufSql.append("FCREATOR,");
            bufSql.append("FCREATETIME,");
            bufSql.append("FCHECKUSER,");
            bufSql.append("FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_ORDER_01062009022324000");

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //指定主键
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_ORDER_MAINTENANCE") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_ORDER_MAINTENANCE " +
                           " PRIMARY KEY (FORDERNUM,FINVMGRCODE,FBROKERCODE,FPORTCODE,FSECURITYCODE)");
            //------------------------------表TB_001_ORDER_MAINTENANCE修改完毕-------------------------------//







            //------------ BUG MS00160  2009.01.12 方浩---------------------------------------------------//
            //----------把债券信息中的发行价格小数位数改为12(修改表tb_001_para_fixinterest的字段FIssuePrice)----//
            strPKName = this.getIsNullPKByTableName("TB_" + sPre +
                "_PARA_FIXINTEREST");
            if (strPKName != null && !strPKName.trim().equals("")) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_FIXINTEREST  DROP CONSTRAINT " + strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在,删除备份表
            if (dbl.yssTableExist("TB_" + sPre + "_PAR_01062009105114000 ")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_PAR_01062009105114000");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre + "_PARA_FIXINTEREST RENAME TO TB_" +
                sPre + "_PAR_01062009105114000");
            //清空bufsql
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append("CREATE TABLE  " + pub.yssGetTableName("TB_PARA_FIXINTEREST"));
            bufSql.append("(");
            bufSql.append(" FSECURITYCODE     VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSTARTDATE        DATE          NOT NULL, ");
            bufSql.append(" FISSUEDATE        DATE          NOT NULL, ");
            bufSql.append(" FISSUEPRICE       NUMBER(18,12) NOT NULL, ");
            bufSql.append(" FINSSTARTDATE     DATE          NOT NULL, ");
            bufSql.append(" FINSENDDATE       DATE          NOT NULL, ");
            bufSql.append(" FINSCASHDATE      DATE          NOT NULL, ");
            bufSql.append(" FFACEVALUE        NUMBER(18,12) NOT NULL, ");
            bufSql.append(" FFACERATE         NUMBER(18,12)     NULL, ");
            bufSql.append(" FINSFREQUENCY     NUMBER(18,4)  NOT NULL, ");
            bufSql.append(" FQUOTEWAY         NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREDITLEVEL      VARCHAR2(20)      NULL, ");
            bufSql.append(" FCALCINSMETICDAY  VARCHAR2(20)      NULL, ");
            bufSql.append(" FCALCINSMETICBUY  VARCHAR2(20)      NULL, ");
            bufSql.append(" FCALCINSMETICSELL VARCHAR2(20)      NULL, ");
            bufSql.append(" FCALCPRICEMETIC   VARCHAR2(20)      NULL, ");
            bufSql.append(" FAMORTIZATION     VARCHAR2(20)      NULL, ");
            bufSql.append(" FFACTRATE         NUMBER(18,4)      NULL, ");
            bufSql.append(" FCALCINSCFGDAY    VARCHAR2(500)     NULL, ");
            bufSql.append(" FCALCINSCFGBUY    VARCHAR2(500)     NULL, ");
            bufSql.append(" FCALCINSCFGSELL   VARCHAR2(500)     NULL, ");
            bufSql.append(" FCALCINSWAY       NUMBER(1)     NOT NULL, ");
            bufSql.append(" FINTERESTORIGIN   NUMBER(1)     NOT NULL, ");
            bufSql.append(" FPEREXPCODE       VARCHAR2(20)      NULL, ");
            bufSql.append(" FPERIODCODE       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FROUNDCODE        VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FDESC             VARCHAR2(100)     NULL, ");
            bufSql.append(" FCHECKSTATE       NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREATOR          VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER        VARCHAR2(20)      NULL, ");
            bufSql.append(" FCHECKTIME        VARCHAR2(20)      NULL ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //将数据从临时表导入新表
            bufSql.append("INSERT INTO " + pub.yssGetTableName("TB_PARA_FIXINTEREST"));
            bufSql.append("(");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FSTARTDATE, ");
            bufSql.append(" FISSUEDATE, ");
            bufSql.append(" FISSUEPRICE, ");
            bufSql.append(" FINSSTARTDATE, ");
            bufSql.append(" FINSENDDATE, ");
            bufSql.append(" FINSCASHDATE, ");
            bufSql.append(" FFACEVALUE, ");
            bufSql.append(" FFACERATE, ");
            bufSql.append(" FINSFREQUENCY, ");
            bufSql.append(" FQUOTEWAY, ");
            bufSql.append(" FCREDITLEVEL, ");
            bufSql.append(" FCALCINSMETICDAY, ");
            bufSql.append(" FCALCINSMETICBUY, ");
            bufSql.append(" FCALCINSMETICSELL, ");
            bufSql.append(" FCALCPRICEMETIC, ");
            bufSql.append(" FAMORTIZATION, ");
            bufSql.append(" FFACTRATE, ");
            bufSql.append(" FCALCINSCFGDAY, ");
            bufSql.append(" FCALCINSCFGBUY, ");
            bufSql.append(" FCALCINSCFGSELL, ");
            bufSql.append(" FCALCINSWAY, ");
            bufSql.append(" FINTERESTORIGIN, ");
            bufSql.append(" FPEREXPCODE, ");
            bufSql.append(" FPERIODCODE, ");
            bufSql.append(" FROUNDCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            //从备份表取出数据
            bufSql.append(" SELECT ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FSTARTDATE, ");
            bufSql.append(" FISSUEDATE, ");
            bufSql.append(" FISSUEPRICE, ");
            bufSql.append(" FINSSTARTDATE, ");
            bufSql.append(" FINSENDDATE, ");
            bufSql.append(" FINSCASHDATE, ");
            bufSql.append(" FFACEVALUE, ");
            bufSql.append(" FFACERATE, ");
            bufSql.append(" FINSFREQUENCY, ");
            bufSql.append(" FQUOTEWAY, ");
            bufSql.append(" FCREDITLEVEL, ");
            bufSql.append(" FCALCINSMETICDAY, ");
            bufSql.append(" FCALCINSMETICBUY, ");
            bufSql.append(" FCALCINSMETICSELL, ");
            bufSql.append(" FCALCPRICEMETIC, ");
            bufSql.append(" FAMORTIZATION, ");
            bufSql.append(" FFACTRATE, ");
            bufSql.append(" FCALCINSCFGDAY, ");
            bufSql.append(" FCALCINSCFGBUY, ");
            bufSql.append(" FCALCINSCFGSELL, ");
            bufSql.append(" FCALCINSWAY, ");
            bufSql.append(" FINTERESTORIGIN, ");
            bufSql.append(" FPEREXPCODE, ");
            bufSql.append(" FPERIODCODE, ");
            bufSql.append(" FROUNDCODE, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_PAR_01062009105114000 "); //MS00280 QDV4建行2009年3月3日02_B 将前缀调整为当前组合群。

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //指定主键
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_PARA_FIXINTEREST") +
                           " ADD CONSTRAINT PK_TB_" + sPre + "_PARA_FIXINTEREST " +
                           " PRIMARY KEY (FSECURITYCODE,FSTARTDATE)");
            //-------------------------------表tb_001_para_fixinterest修改完毕-------------------------------//






        } catch (Exception ex) {
            throw new YssException("版本Oracle1010010更新表字段出错", ex);
        } finally {
            dbl.endTransFinal(conn,
                              bTrans);
        }
    }

    //调整字段精度 MS00032
    public void adjustFieldPrecision() throws YssException {
        try {
            //将词汇表的备注信息长度从100调整到500
            dbl.executeSql(
                "alter table tb_fun_vocabulary modify FDESC varchar2(500)");
        } catch (Exception ex) {
            throw new YssException("版本Oracle1010010调整词汇表字段精度出错!");
        }
    }

}
