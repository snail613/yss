package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;

public class Ora1010013
    extends BaseDbUpdate { //调整为13版本
    public Ora1010013() {
    }

    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            //======QDV4深圳2009年01月15日02_B Tb_Vch_Entity 表中添加FAllow字段 by leeyu 20090210
            if (existsTabColumn_Ora("Tb_" + sPre + "_Vch_Entity", "FAllow")) { //先检查表中是否有这个字段
                bufSql.delete(0, bufSql.length()); //清空BUF
                //获取主键名
                strPKName = getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_Vch_Entity");
                if (strPKName != null && strPKName.trim().length() > 0) {
                    //删除约束
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_Vch_Entity DROP CONSTRAINT " +
                                   strPKName);
                    //删除索引
                    deleteIndex(strPKName);
                }
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_" + sPre + "_Vch_02092009065119000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_Vch_02092009065119000");
                }
                //将原表更改为备份表
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre + "_Vch_Entity RENAME TO TB_" +
                    sPre + "_Vch_02092009065119000");
                bufSql.append(" CREATE TABLE TB_" + sPre + "_VCH_ENTITY ");
                bufSql.append(" ( ");
                bufSql.append(" FVCHTPLCODE    VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FENTITYCODE    VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FENTITYNAME    VARCHAR2(50)  NOT NULL, ");
                bufSql.append(" FDCWAY         VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCALCWAY       VARCHAR2(20)  DEFAULT 0 NOT NULL, ");
                bufSql.append(" FPRICEFIELD    VARCHAR2(20)      NULL, ");
                bufSql.append(" FENCURYCODE    VARCHAR2(20)      NULL, ");
                bufSql.append(" FALLOW         VARCHAR2(20)  DEFAULT 0     NULL, ");
                bufSql.append(" FRESUMEDESC    VARCHAR2(300) NOT NULL, ");
                bufSql.append(" FSUBJECTCODE   VARCHAR2(300) NOT NULL, ");
                bufSql.append(" FMONEYDESC     VARCHAR2(300) NOT NULL, ");
                bufSql.append(" FAMOUNTDESC    VARCHAR2(300)     NULL, ");
                bufSql.append(" FSETMONEYDESC  VARCHAR2(300)     NULL, ");
                bufSql.append(" FCONDDESC      VARCHAR2(300)     NULL, ");
                bufSql.append(" FASSISTANTDESC VARCHAR2(300)     NULL, ");
                bufSql.append(" FENTITYIND     VARCHAR2(20)      NULL, ");
                bufSql.append(" FDESC          VARCHAR2(100)     NULL, ");
                bufSql.append(" FCHECKSTATE    NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME    VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER     VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME     VARCHAR2(20)      NULL ");
                bufSql.append(" ) ");
                //执行创建表语句
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());

                bufSql.append(" INSERT INTO TB_" + sPre + "_VCH_ENTITY( ");
                bufSql.append(" FVCHTPLCODE, ");
                bufSql.append(" FENTITYCODE, ");
                bufSql.append(" FENTITYNAME, ");
                bufSql.append(" FDCWAY, ");
                bufSql.append(" FCALCWAY, ");
                bufSql.append(" FPRICEFIELD, ");
                bufSql.append(" FENCURYCODE, ");
                bufSql.append(" FALLOW, ");
                bufSql.append(" FRESUMEDESC, ");
                bufSql.append(" FSUBJECTCODE, ");
                bufSql.append(" FMONEYDESC, ");
                bufSql.append(" FAMOUNTDESC, ");
                bufSql.append(" FSETMONEYDESC, ");
                bufSql.append(" FCONDDESC, ");
                bufSql.append(" FASSISTANTDESC, ");
                bufSql.append(" FENTITYIND, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FVCHTPLCODE, ");
                bufSql.append(" FENTITYCODE, ");
                bufSql.append(" FENTITYNAME, ");
                bufSql.append(" FDCWAY, ");
                bufSql.append(" FCALCWAY, ");
                bufSql.append(" FPRICEFIELD, ");
                bufSql.append(" FENCURYCODE, ");
                bufSql.append(" '0', ");
                bufSql.append(" FRESUMEDESC, ");
                bufSql.append(" FSUBJECTCODE, ");
                bufSql.append(" FMONEYDESC, ");
                bufSql.append(" FAMOUNTDESC, ");
                bufSql.append(" FSETMONEYDESC, ");
                bufSql.append(" FCONDDESC, ");
                bufSql.append(" FASSISTANTDESC, ");
                bufSql.append(" FENTITYIND, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_" + sPre + "_Vch_02092009065119000 ");
                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                //执行生成的SQL
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                //为表添加主键
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_VCH_ENTITY ADD CONSTRAINT PK_TB_" + sPre +
                               "_VCH_ENTITY PRIMARY KEY (FVCHTPLCODE,FENTITYCODE)");
            } //end
            //======QDV4深圳2009年01月15日02_B MS00194 byleeyu 20090210

            //----  MS00265   QDV4建行2009年2月23日01_B ----------------------------------------------------
            if (existsTabColumn_Ora("Tb_" + sPre + "_Data_ValMktPrice", "FValType")) { //先检查表中是否有估值类型字段
                dbl.executeSql("alter table " + pub.yssGetTableName("TB_DATA_VALMKTPRICE") + " add FValType VARCHAR2(20)");
            }
            //-----------------------------------------------------------------------------------------

        } catch (Exception e) {
            throw new YssException("版本1.0.1.0013增加表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 创建表
     * 创建证券信息表TB_Base_Security、行情表Tb_Base_MarketValue、汇率表Tb_Base_ExchangeRate,并往这些表中添加数据
     * byleeyu 20090203 MS00131 QDV4建行2008年12月25日01_A
     * @throws YssException
     */
    public void createTable() throws YssException {
        String sqlStr = "";
        StringBuffer bufSql = null;
        try {
            if (!dbl.yssTableExist("TB_BASE_SECURITY")) {
                bufSql = new StringBuffer();
                bufSql.append(" CREATE TABLE TB_BASE_SECURITY ");
                bufSql.append(" ( ");
                bufSql.append(" FSECURITYCODE      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FASSETGROUPCODE    VARCHAR2(3)   NOT NULL, ");
                bufSql.append(" FSTARTDATE         DATE          NOT NULL, ");
                bufSql.append(" FSECURITYNAME      VARCHAR2(100) NOT NULL, ");
                bufSql.append(" FSECURITYSHORTNAME VARCHAR2(100)      NULL, ");
                bufSql.append(" FSECURITYCORPNAME  VARCHAR2(100)      NULL, ");
                bufSql.append(" FCATCODE           VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FSUBCATCODE        VARCHAR2(20)      NULL, ");
                bufSql.append(" FCUSCATCODE        VARCHAR2(20)      NULL, ");
                bufSql.append(" FEXCHANGECODE      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FMARKETCODE        VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FEXTERNALCODE      VARCHAR2(100)     NULL, ");
                bufSql.append(" FISINCODE          VARCHAR2(20)      NULL, ");
                bufSql.append(" FTRADECURY         VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FHOLIDAYSCODE      VARCHAR2(20)  NOT NULL, ");
                bufSql.append(
                    " FSETTLEDAYTYPE     NUMBER(1)     DEFAULT 0 NOT NULL, ");
                bufSql.append(" FSETTLEDAYS        NUMBER(38)    NOT NULL, ");
                bufSql.append(" FSECTORCODE        VARCHAR2(20)      NULL, ");
                bufSql.append(" FTOTALSHARE        NUMBER(18,4)  NOT NULL, ");
                bufSql.append(" FCURRENTSHARE      NUMBER(18,4)  NOT NULL, ");
                bufSql.append(
                    " FHANDAMOUNT        NUMBER(18,4)  DEFAULT 1 NOT NULL, ");
                bufSql.append(" FFACTOR            NUMBER(12,6)  NOT NULL, ");
                bufSql.append(" FISSUECORPCODE     VARCHAR2(40)      NULL, ");
                bufSql.append(" FDESC              VARCHAR2(100)     NULL, ");
                bufSql.append(" FCHECKSTATE        NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR           VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME        VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER         VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME         VARCHAR2(20)      NULL, ");
                bufSql.append(" CONSTRAINT PK_TB_BASE_SECURITY ");
                bufSql.append(" PRIMARY KEY (FSECURITYCODE,FASSETGROUPCODE) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                insertDataToBaseTable("Tb_Base_Security"); //插入数据到新表
            }
            if (!dbl.yssTableExist("TB_BASE_MARKETVALUE")) {
                bufSql = new StringBuffer();
                bufSql.append(" CREATE TABLE TB_BASE_MARKETVALUE ");
                bufSql.append(" ( ");
                bufSql.append(" FMKTSRCCODE     VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FSECURITYCODE   VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FMKTVALUEDATE   DATE          NOT NULL, ");
                bufSql.append(" FMKTVALUETIME   VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FASSETGROUPCODE VARCHAR2(3)   NOT NULL, ");
                bufSql.append(" FBARGAINAMOUNT  NUMBER(18,4)      NULL, ");
                bufSql.append(" FBARGAINMONEY   NUMBER(18,4)      NULL, ");
                bufSql.append(" FYCLOSEPRICE    NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FOPENPRICE      NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FTOPPRICE       NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FLOWPRICE       NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FCLOSINGPRICE   NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FAVERAGEPRICE   NUMBER(20,12) NOT NULL, ");
                bufSql.append(" FNEWPRICE       NUMBER(20,12)     NULL, ");
                bufSql.append(" FMKTPRICE1      NUMBER(20,12)     NULL, ");
                bufSql.append(" FMKTPRICE2      NUMBER(20,12)     NULL, ");
                bufSql.append(" FMARKETSTATUS   VARCHAR2(4)       NULL, ");
                bufSql.append(" FDESC           VARCHAR2(100)     NULL, ");
                bufSql.append(" FDATASOURCE     NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL, ");
                bufSql.append(" CONSTRAINT PK_TB_BASE_MARKETVALUE ");
                bufSql.append(" PRIMARY KEY (FMKTSRCCODE,FSECURITYCODE,FMKTVALUEDATE,FMKTVALUETIME,FPORTCODE,FASSETGROUPCODE) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                insertDataToBaseTable("Tb_Base_MarketValue"); //插入数据到新表
            }
            if (!dbl.yssTableExist("TB_BASE_EXCHANGERATE")) {
                bufSql = new StringBuffer();
                bufSql.append(" CREATE TABLE TB_BASE_EXCHANGERATE ");
                bufSql.append(" ( ");
                bufSql.append(" FEXRATESRCCODE  VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCURYCODE       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(
                    " FMARKCURY       VARCHAR2(20)  DEFAULT ' ' NOT NULL, ");
                bufSql.append(" FEXRATEDATE     DATE          NOT NULL, ");
                bufSql.append(" FEXRATETIME     VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FASSETGROUPCODE VARCHAR2(3)   NOT NULL, ");
                bufSql.append(" FEXRATE1        NUMBER(20,15)     NULL, ");
                bufSql.append(" FEXRATE2        NUMBER(20,15)     NULL, ");
                bufSql.append(" FEXRATE3        NUMBER(20,15)     NULL, ");
                bufSql.append(" FEXRATE4        NUMBER(20,15)     NULL, ");
                bufSql.append(" FEXRATE5        NUMBER(20,15)     NULL, ");
                bufSql.append(" FEXRATE6        NUMBER(20,15)     NULL, ");
                bufSql.append(" FEXRATE7        NUMBER(20,15)     NULL, ");
                bufSql.append(" FEXRATE8        NUMBER(20,15)     NULL, ");
                bufSql.append(" FDESC           VARCHAR2(100)     NULL, ");
                bufSql.append(" FDATASOURCE     NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL, ");
                bufSql.append(" CONSTRAINT PK_TB_BASE_EXCHANGERATE ");
                bufSql.append(" PRIMARY KEY (FEXRATESRCCODE,FCURYCODE,FMARKCURY,FEXRATEDATE,FEXRATETIME,FPORTCODE,FASSETGROUPCODE) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                insertDataToBaseTable("Tb_Base_ExchangeRate"); //插入数据到新表
            }
        } catch (Exception ex) {
            throw new YssException("更新ORA-1010013脚本失败！\r\n" + ex.getMessage());
        } finally {
            bufSql = null;
        }
    }

    /**
     *  MS00008 add by 宋洁 2009-02-17
     * @param sPre String
     * @throws YssException
     */
    public void createTable(String sPre) throws YssException {
        String sqlStr = "";
        StringBuffer bufSql = null;
        try {
            if (!dbl.yssTableExist(pub.yssGetTableName("TB_PARA_MTVSELCONDSET"))) {
                bufSql = new StringBuffer();
                bufSql.append(" CREATE TABLE ");
                bufSql.append(pub.yssGetTableName("TB_PARA_MTVSELCONDSET") +
                              " ");
                bufSql.append(" ( ");
                bufSql.append(" FMTVCODE        VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FMTVSELCONDCODE VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FCATCODE        VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FSUBCATCODE     VARCHAR2(20)     NULL, ");
                bufSql.append(" FEXCHANGECODE   VARCHAR2(20)     NULL, ");
                bufSql.append(" FCUSCATCODE     VARCHAR2(20)     NULL, ");
                bufSql.append(" FCheckState     NUMBER(1, 0)  NOT NULL, ");
                bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL, ");
                bufSql.append(" CONSTRAINT ");
                bufSql.append(" PK_" + pub.yssGetTableName("TB_PARA_MTVSELCONDSET") +
                              " ");
                bufSql.append(" PRIMARY KEY (FMTVCODE,FMTVSELCONDCODE) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
            }

        } catch (Exception ex) {
            throw new YssException("更新ORA-1010013脚本失败！\r\n" + ex.getMessage());
        }
    }

    /**
     * 根据所提供的表名来选择插入值到BASE表中。
     * @param sTableName String
     */
    private void insertDataToBaseTable(String sTableName) throws YssException {
        //定义行情表的字段及主键
        String marketvalue =
            "FMKTSRCCODE,FSECURITYCODE,FMKTVALUEDATE,FMKTVALUETIME,FPORTCODE,FBARGAINAMOUNT,FBARGAINMONEY,FYCLOSEPRICE,FOPENPRICE,FTOPPRICE,FLOWPRICE,FCLOSINGPRICE,FAVERAGEPRICE,FNEWPRICE,FMKTPRICE1,FMKTPRICE2,FMARKETSTATUS,FDESC,FDATASOURCE,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME";
        String marketvaluePK = "a.FMKTSRCCODE=y.FMKTSRCCODE and a.FSECURITYCODE=y.FSECURITYCODE and a.FMKTVALUEDATE=y.FMKTVALUEDATE and a.FMKTVALUETIME=y.FMKTVALUETIME and a.FPORTCODE=y.FPORTCODE";
        //定义汇率表的字段及主键
        String exchangerate = "FEXRATESRCCODE,FCURYCODE,FMARKCURY,FEXRATEDATE,FEXRATETIME,FPORTCODE,FEXRATE1,FEXRATE2,FEXRATE3,FEXRATE4,FEXRATE5,FEXRATE6,FEXRATE7,FEXRATE8,FDESC,FDATASOURCE,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME";
        String exchangeratePK = "a.FEXRATESRCCODE=y.FEXRATESRCCODE and a.FCURYCODE=y.FCURYCODE and a.FMARKCURY=y.FMARKCURY and a.FEXRATEDATE=y.FEXRATEDATE and a.FEXRATETIME=y.FEXRATETIME and a.FPORTCODE=y.FPORTCODE";
        //定义证券信息表的字段及主键
        String security = "FSECURITYCODE,FSTARTDATE,FSECURITYNAME,FSECURITYSHORTNAME,FSECURITYCORPNAME,FCATCODE,FSUBCATCODE,FCUSCATCODE,FEXCHANGECODE,FMARKETCODE,FEXTERNALCODE,FISINCODE,FTRADECURY,FHOLIDAYSCODE,FSETTLEDAYTYPE,FSETTLEDAYS,FSECTORCODE,FTOTALSHARE,FCURRENTSHARE,FHANDAMOUNT,FFACTOR,FISSUECORPCODE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME";
        String securityPK = "a.FSECURITYCODE=y.FSECURITYCODE";

        String sOldTable = "", sRelaField = "", sRelaPK = "";
        String sqlStr = "", sTableSql = "";
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection(); //建立连接
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //查出系统中所有的可用组合群
            sqlStr = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FTabInd=1 order by FAssetGroupCode ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                if (sTableName.equalsIgnoreCase("Tb_Base_Security")) {
                    sOldTable = "Tb_" + rs.getString("FTabPreFix") + "_Para_Security";
                    sRelaField = security;
                    sRelaPK = securityPK;
                } else if (sTableName.equalsIgnoreCase("Tb_Base_MarketValue")) {
                    sOldTable = "Tb_" + rs.getString("FTabPreFix") + "_Data_MarketValue";
                    sRelaField = marketvalue;
                    sRelaPK = marketvaluePK;
                } else if (sTableName.equalsIgnoreCase("Tb_Base_ExchangeRate")) {
                    sOldTable = "Tb_" + rs.getString("FTabPreFix") + "_Data_ExchangeRate";
                    sRelaField = exchangerate;
                    sRelaPK = exchangeratePK;
                }
                if (dbl.yssTableExist(sOldTable)) { //若这张表存在的话
                    //将旧表的值插入到新表里
                    sTableSql = "insert into " + sTableName + " (" + sRelaField +
                        ",FASSETGROUPCODE) select " + sRelaField + ",' ' from " +
                        sOldTable + " y where not exists (" +
                        "select '1' from " + sTableName + " a where " + sRelaPK +
                        ")";
                    dbl.executeSql(sTableSql);
                    conn.commit();
                    conn.setAutoCommit(bTrans);
                    bTrans = false;
                }
            }
        } catch (Exception ex) {
            throw new YssException("更新表【" + sTableName + "】数据失败!", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //调整数据
    //MS00279 QDV4建行2009年3月3日01_B
    public void adjustTableData(String sPre) throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        conn = dbl.loadConnection(); //建立连接
        String sqlStr = "";
        try {
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "update " + pub.yssGetTableName("tb_data_secrecpay") +
                " set FTsfTypeCode = '02', FSubTsfTypeCode = '02FI_B' " +
                " where FCheckState = 1 and FTsfTypeCode = '07' and FSubTsfTypeCode = '07FI_B'"; // 将原来的07FI_B的数据调整为02FI_B的调拨类型
            dbl.executeSql(sqlStr);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (SQLException ex) {
            throw new YssException("调整债券卖出利息出现异常！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
