package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.*;

public class DB21010010
    extends BaseDbUpdate {
    public DB21010010() {
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
            strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_DATA_TRADE");
            if (strPKName != null && !strPKName.trim().equals("")) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_DATA_TRADE  DROP CONSTRAINT " + strPKName);
                //删除索引
                //SdeleteIndex(strPKName);
            }
            //如果备份表存在,删除备份表
            if (dbl.yssTableExist("TB_01132009093806 ")) {
                this.dropTableByTableName("TB_01132009093806");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "RENAME  TABLE TB_" + sPre +
                "_DATA_TRADE TO TB_01132009093806");
            //清空bufsql
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append("CREATE TABLE TB_" + sPre + "_DATA_TRADE");
            bufSql.append(" ( ");
            bufSql.append(" FNUM             VARCHAR(15)    NOT NULL, ");
            bufSql.append(" FSECURITYCODE    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FPORTCODE        VARCHAR(20), ");
            bufSql.append(" FBROKERCODE      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FINVMGRCODE      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FTRADETYPECODE   VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCASHACCCODE     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FATTRCLSCODE     VARCHAR(20), ");
            bufSql.append(" FBARGAINDATE     DATE           NOT NULL, ");
            bufSql.append(" FBARGAINTIME     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSETTLEDATE      DATE           NOT NULL, ");
            bufSql.append(" FSETTLETIME      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FAUTOSETTLE      DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FPORTCURYRATE    DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FBASECURYRATE    DECIMAL(20,15) NOT NULL, ");
            bufSql.append(" FALLOTFACTOR     DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FTRADEAMOUNT     DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FTRADEPRICE      DECIMAL(20,8)  NOT NULL, ");
            bufSql.append(" FTRADEMONEY      DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FUNITCOST        DECIMAL(18,12), ");
            bufSql.append(" FACCRUEDINTEREST DECIMAL(18,4), ");
            bufSql.append(" FFEECODE1        VARCHAR(20), ");
            bufSql.append(" FTRADEFEE1       DECIMAL(18,4), ");
            bufSql.append(" FFEECODE2        VARCHAR(20), ");
            bufSql.append(" FTRADEFEE2       DECIMAL(18,4), ");
            bufSql.append(" FFEECODE3        VARCHAR(20), ");
            bufSql.append(" FTRADEFEE3       DECIMAL(18,4), ");
            bufSql.append(" FFEECODE4        VARCHAR(20), ");
            bufSql.append(" FTRADEFEE4       DECIMAL(18,4), ");
            bufSql.append(" FFEECODE5        VARCHAR(20), ");
            bufSql.append(" FTRADEFEE5       DECIMAL(18,4), ");
            bufSql.append(" FFEECODE6        VARCHAR(20), ");
            bufSql.append(" FTRADEFEE6       DECIMAL(18,4), ");
            bufSql.append(" FFEECODE7        VARCHAR(20), ");
            bufSql.append(" FTRADEFEE7       DECIMAL(18,4), ");
            bufSql.append(" FFEECODE8        VARCHAR(20), ");
            bufSql.append(" FTRADEFEE8       DECIMAL(18,4), ");
            bufSql.append(" FTOTALCOST       DECIMAL(18,4), ");
            bufSql.append(" FBAILMONEY       DECIMAL(18,4), ");
            bufSql.append(" FORDERNUM        VARCHAR(20), ");
            bufSql.append(" FDESC            VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE      DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR         VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCREATETIME      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCHECKUSER       VARCHAR(20), ");
            bufSql.append(" FCHECKTIME       VARCHAR(20)");
            bufSql.append(" ) ");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //数据从备份表导入新表
            bufSql.append(" INSERT INTO TB_" + sPre + "_DATA_TRADE( ");
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
            bufSql.append(" FAUTOSETTLE, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FALLOTFACTOR, ");
            bufSql.append(" FTRADEAMOUNT, ");
            bufSql.append(" FTRADEPRICE, ");
            bufSql.append(" FTRADEMONEY, ");
            bufSql.append(" FUNITCOST, ");
            bufSql.append(" FACCRUEDINTEREST, ");
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
            bufSql.append(" FBAILMONEY, ");
            bufSql.append(" FORDERNUM, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            //备份表取出数据
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
            bufSql.append(" FAUTOSETTLE, ");
            bufSql.append(" FPORTCURYRATE, ");
            bufSql.append(" FBASECURYRATE, ");
            bufSql.append(" FALLOTFACTOR, ");
            bufSql.append(" FTRADEAMOUNT, ");
            bufSql.append(" FTRADEPRICE, ");
            bufSql.append(" FTRADEMONEY, ");
            bufSql.append(" FUNITCOST, ");
            bufSql.append(" FACCRUEDINTEREST, ");
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
            bufSql.append(" FBAILMONEY, ");
            bufSql.append(" FORDERNUM, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_01132009093806 ");

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            bufSql.delete(0, bufSql.length());

            //指定主键
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_TRADE ADD CONSTRAINT PK_Tb_" + sPre +
                           "_A_TRADE PRIMARY KEY (FNUM)");

            //-------------------------------表Tb_001_Data_Trade修改完毕-------------------------------//


            //------------获取主键约束(TB_001_ORDER_CONFIRM的字段Fprice修改)---------------------------------------------------//
            strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_ORDER_CONFIRM");
            if (strPKName != null && !strPKName.trim().equals("")) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_ORDER_CONFIRM  DROP CONSTRAINT " + strPKName);
                //删除索引
                //deleteIndex(strPKName);
            }
            //如果备份表存在,删除备份表
            if (dbl.yssTableExist("TB_01132009094739")) {
                this.dropTableByTableName("TB_01132009094739");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "RENAME  TABLE TB_" + sPre +
                "_ORDER_CONFIRM TO TB_01132009094739");
            //清空bufsql
            bufSql.delete(0, bufSql.length());

            bufSql.append(" CREATE TABLE TB_" + sPre + "_ORDER_CONFIRM ");
            bufSql.append(" ( ");
            bufSql.append(" FTRADENUM      VARCHAR(15)    NOT NULL, ");
            bufSql.append(" FINVMGRCODE    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSECURITYCODE  VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBROKERCODE    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FPORTCODE      VARCHAR(20), ");
            bufSql.append(" FTRADETYPECODE VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FAMOUNT        DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FINTEREST      DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FPRICE         DECIMAL(20,8)  NOT NULL, ");
            bufSql.append(" FYIELD         DECIMAL(18,12), ");
            bufSql.append(" FTRANSDATE     DATE           NOT NULL, ");
            bufSql.append(" FTRANSTIME     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FDESC          VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE    DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCREATETIME    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCHECKUSER     VARCHAR(20), ");
            bufSql.append(" FCHECKTIME     VARCHAR(20) ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append(" INSERT INTO TB_" + sPre + "_ORDER_CONFIRM( ");
            bufSql.append(" FTRADENUM, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FTRADETYPECODE, ");
            bufSql.append(" FAMOUNT, ");
            bufSql.append(" FINTEREST, ");
            bufSql.append(" FPRICE, ");
            bufSql.append(" FYIELD, ");
            bufSql.append(" FTRANSDATE, ");
            bufSql.append(" FTRANSTIME, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FTRADENUM, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FTRADETYPECODE, ");
            bufSql.append(" FAMOUNT, ");
            bufSql.append(" FINTEREST, ");
            bufSql.append(" FPRICE, ");
            bufSql.append(" FYIELD, ");
            bufSql.append(" FTRANSDATE, ");
            bufSql.append(" FTRANSTIME, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_01132009094739 ");

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //指定主键
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_ORDER_CONFIRM ADD CONSTRAINT PK_Tb_" + sPre +
                           "_CONFIRM PRIMARY KEY (FTRADENUM)");

            //------------(TB_001_ORDER_CONFIRM的字段Fprice修改完毕)---------------------------------------------------//



            //---------------------获取主键约束(修改表TB_001_ORDER_MAINTENANCE的字段FtradePrice、FvirtualPrice)-----------//


            strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_ORDER_MAINTENANCE");
            if (strPKName != null && !strPKName.trim().equals("")) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_ORDER_MAINTENANCE  DROP CONSTRAINT " + strPKName);
                //删除索引
                // deleteIndex(strPKName);
            }
            //如果备份表存在,删除备份表
            if (dbl.yssTableExist("TB_01132009094740")) {
                this.dropTableByTableName("TB_01132009094740");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "RENAME TABLE TB_" + sPre +
                "_ORDER_MAINTENANCE TO TB_01132009094740");
            //清空bufsql
            bufSql.delete(0, bufSql.length());

            bufSql.append(" CREATE TABLE TB_" + sPre + "_ORDER_MAINTENANCE ");
            bufSql.append(" ( ");
            bufSql.append(" FORDERNUM      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FINVMGRCODE    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FBROKERCODE    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FPORTCODE      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSECURITYCODE  VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FTRADETYPECODE VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FTRADEAMOUNT   DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FINTEREST      DECIMAL(18,4)  NOT NULL DEFAULT 0, ");
            bufSql.append(" FQUOTEMODE     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FTRADEPRICE    DECIMAL(20,8)  NOT NULL, ");
            bufSql.append(" FVIRTUALPRICE  DECIMAL(20,8), ");
            bufSql.append(" FYIELD         DECIMAL(18,12), ");
            bufSql.append(" FORDERDATE     DATE           NOT NULL, ");
            bufSql.append(" FORDERTIME     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FTRADENUM      VARCHAR(15), ");
            bufSql.append(" FDESC          VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE    DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCREATETIME    VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCHECKUSER     VARCHAR(20), ");
            bufSql.append(" FCHECKTIME     VARCHAR(20) ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append(" INSERT INTO TB_" + sPre + "_ORDER_MAINTENANCE( ");
            bufSql.append(" FORDERNUM, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FTRADETYPECODE, ");
            bufSql.append(" FTRADEAMOUNT, ");
            bufSql.append(" FINTEREST, ");
            bufSql.append(" FQUOTEMODE, ");
            bufSql.append(" FTRADEPRICE, ");
            bufSql.append(" FVIRTUALPRICE, ");
            bufSql.append(" FYIELD, ");
            bufSql.append(" FORDERDATE, ");
            bufSql.append(" FORDERTIME, ");
            bufSql.append(" FTRADENUM, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FORDERNUM, ");
            bufSql.append(" FINVMGRCODE, ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FSECURITYCODE, ");
            bufSql.append(" FTRADETYPECODE, ");
            bufSql.append(" FTRADEAMOUNT, ");
            bufSql.append(" FINTEREST, ");
            bufSql.append(" FQUOTEMODE, ");
            bufSql.append(" FTRADEPRICE, ");
            bufSql.append(" FVIRTUALPRICE, ");
            bufSql.append(" FYIELD, ");
            bufSql.append(" FORDERDATE, ");
            bufSql.append(" FORDERTIME, ");
            bufSql.append(" FTRADENUM, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_01132009094740 ");

            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //指定主键
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_ORDER_MAINTENANCE ADD CONSTRAINT PK_Tb_" + sPre +
                           "_TENANCE PRIMARY KEY (FORDERNUM,FINVMGRCODE,FBROKERCODE,FPORTCODE,FSECURITYCODE)");

            //---------------------修改表TB_001_ORDER_MAINTENANCE的字段FtradePrice、FvirtualPrice)完毕-----------------------//


            //-----QDV4赢时胜上海2009年1月4日02_B------- BUG MS00160  2009.01.12 方浩-------------------------------//
            //----------把债券信息中的发行价格小数位数改为12(修改表tb_001_para_fixinterest的字段FIssuePrice)----//
            strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                "_PARA_FIXINTEREST");
            if (strPKName != null && !strPKName.trim().equals("")) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_FIXINTEREST  DROP CONSTRAINT " + strPKName);
                //删除索引
                //            deleteIndex(strPKName);
            }
            //如果备份表存在,删除备份表
            if (dbl.yssTableExist("TB_01132009090915")) {
                this.dropTableByTableName("TB_01132009090915");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "RENAME TABLE TB_" + sPre +
                "_PARA_FIXINTEREST TO TB_01132009090915");
            //清空bufsql
            bufSql.delete(0, bufSql.length());
            //创建新表
            bufSql.append("CREATE TABLE TB_" + sPre + "_PARA_FIXINTEREST( ");
            bufSql.append(" FSECURITYCODE     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FSTARTDATE        DATE           NOT NULL, ");
            bufSql.append(" FISSUEDATE        DATE           NOT NULL, ");
            bufSql.append(" FISSUEPRICE       DECIMAL(18,12) NOT NULL, ");
            bufSql.append(" FINSSTARTDATE     DATE           NOT NULL, ");
            bufSql.append(" FINSENDDATE       DATE           NOT NULL, ");
            bufSql.append(" FINSCASHDATE      DATE           NOT NULL, ");
            bufSql.append(" FFACEVALUE        DECIMAL(18,12) NOT NULL, ");
            bufSql.append(" FFACERATE         DECIMAL(18,12), ");
            bufSql.append(" FINSFREQUENCY     DECIMAL(18,4)  NOT NULL, ");
            bufSql.append(" FQUOTEWAY         DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREDITLEVEL      VARCHAR(20), ");
            bufSql.append(" FCALCINSMETICDAY  VARCHAR(20), ");
            bufSql.append(" FCALCINSMETICBUY  VARCHAR(20), ");
            bufSql.append(" FCALCINSMETICSELL VARCHAR(20), ");
            bufSql.append(" FCALCPRICEMETIC   VARCHAR(20), ");
            bufSql.append(" FAMORTIZATION     VARCHAR(20), ");
            bufSql.append(" FFACTRATE         DECIMAL(18,4), ");
            bufSql.append(" FCALCINSCFGDAY    VARCHAR(500), ");
            bufSql.append(" FCALCINSCFGBUY    VARCHAR(500), ");
            bufSql.append(" FCALCINSCFGSELL   VARCHAR(500), ");
            bufSql.append(" FCALCINSWAY       DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FINTERESTORIGIN   DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FPEREXPCODE       VARCHAR(20), ");
            bufSql.append(" FPERIODCODE       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FROUNDCODE        VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FDESC             VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE       DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR          VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCREATETIME       VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCHECKUSER        VARCHAR(20), ");
            bufSql.append(" FCHECKTIME        VARCHAR(20) ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //将数据从临时表导入新表
            bufSql.append("INSERT INTO TB_" + sPre + "_PARA_FIXINTEREST( ");
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
            bufSql.append(" FROM TB_01132009090915 ");

            //指定主键

            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_PARA_FIXINTEREST ADD CONSTRAINT PK_Tb_" + sPre +
                           "_Para_Fix PRIMARY KEY (FSECURITYCODE,FSTARTDATE)");
            //-------------------------------表tb_001_para_fixinterest修改完毕-------------------------------//



            conn.setAutoCommit(bTrans);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

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
            dbl.executeSql("alter table tb_fun_vocabulary alter FDesc set data type varchar(500)");
        } catch (Exception ex) {
            throw new YssException("1010010版本调整表字段出错");
        }
    }

}
