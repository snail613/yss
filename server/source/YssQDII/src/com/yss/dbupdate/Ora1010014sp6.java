package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;

public class Ora1010014sp6
    extends BaseDbUpdate {
    public Ora1010014sp6() {
    }

    /**
     * MS00339
     * QDV4建行2009年1月16日01_B
     * add by songjie
     * 2009.03.27
     * @param sPre String
     * @throws YssException
     */
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false;
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            if (existsTabColumn_Ora(pub.yssGetTableName("tb_data_tradedetaila"),
                                    "FSeatNum")) { //先检查表中是否有这个字段
                bufSql.delete(0, bufSql.length()); //清空BUF
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist(pub.yssGetTableName(
                    "TB_DAT_03272009053652000"))) {
                    this.dropTableByTableName(pub.yssGetTableName(
                        "TB_DAT_03272009053652000"));
                }
                //----------------------源表更改为备份表-------------------------------------//
                dbl.executeSql("alter TABLE " +
                               pub.yssGetTableName("tb_data_tradedetaila") +
                               " RENAME TO " +
                               pub.yssGetTableName("TB_DAT_03272009053652000"));
                //-----------------------创建新表------------------------------------------//
                bufSql.append(" CREATE TABLE " +
                              pub.yssGetTableName("tb_data_tradedetaila"));
                bufSql.append(" ( ");
                bufSql.append(" FPORTCODE       VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FSECURITYCODE   VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FSRCSECCODE     VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FTRADEORDER     VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FEXCHANGECODE   VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FSTOCKHOLDERNUM VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FSEATNUM        VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FANALYSISCODE2  VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FTRADETYPECODE  VARCHAR2(20) NOT NULL, ");
                bufSql.append(" FTRADEAMOUMT    NUMBER(18,4) NOT NULL, ");
                bufSql.append(" FTRADEPRICE     NUMBER(18,4) NOT NULL, ");
                bufSql.append(" FTRADEMONEY     NUMBER(18,4) NOT NULL, ");
                bufSql.append(" FTRADEDATE      DATE         NOT NULL, ");
                bufSql.append(" FSETTLEDATE     DATE         NOT NULL, ");
                bufSql.append(" FYHSCODE        VARCHAR2(20) NOT NULL, ");
                bufSql.append(
                    " FYHS            NUMBER(18,4) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FJSFCODE        VARCHAR2(20) NOT NULL, ");
                bufSql.append(
                    " FJSF            NUMBER(18,4) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FGHFCODE        VARCHAR2(20) NOT NULL, ");
                bufSql.append(
                    " FGHF            NUMBER(18,4) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FZGFCODE        VARCHAR2(20) NOT NULL, ");
                bufSql.append(
                    " FZGF            NUMBER(18,4) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FQTFCODE        VARCHAR2(20) NOT NULL, ");
                bufSql.append(
                    " FQTF            NUMBER(18,4) DEFAULT 0 NOT NULL, ");
                bufSql.append(" FYJCODE         VARCHAR2(20) NOT NULL, ");
                bufSql.append(
                    " FYJ             NUMBER(18,4) DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FFXJ            NUMBER(18,4) DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FBONDINS        NUMBER(18,4) DEFAULT 0 NOT NULL, ");
                bufSql.append(
                    " FHGGAIN         NUMBER(18,4) DEFAULT 0 NOT NULL ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString()); //执行语句
                bufSql.delete(0, bufSql.length()); //清空bufSql
                //--------------从备份表导入数据到新表-------------------------//
                bufSql.append(" INSERT INTO " +
                              pub.yssGetTableName("tb_data_tradedetaila") + "(");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FSRCSECCODE, ");
                bufSql.append(" FTRADEORDER, ");
                bufSql.append(" FEXCHANGECODE, ");
                bufSql.append(" FSTOCKHOLDERNUM, ");
                bufSql.append(" FSEATNUM, ");
                bufSql.append(" FANALYSISCODE2, ");
                bufSql.append(" FTRADETYPECODE, ");
                bufSql.append(" FTRADEAMOUMT, ");
                bufSql.append(" FTRADEPRICE, ");
                bufSql.append(" FTRADEMONEY, ");
                bufSql.append(" FTRADEDATE, ");
                bufSql.append(" FSETTLEDATE, ");
                bufSql.append(" FYHSCODE, ");
                bufSql.append(" FYHS, ");
                bufSql.append(" FJSFCODE, ");
                bufSql.append(" FJSF, ");
                bufSql.append(" FGHFCODE, ");
                bufSql.append(" FGHF, ");
                bufSql.append(" FZGFCODE, ");
                bufSql.append(" FZGF, ");
                bufSql.append(" FQTFCODE, ");
                bufSql.append(" FQTF, ");
                bufSql.append(" FYJCODE, ");
                bufSql.append(" FYJ, ");
                bufSql.append(" FFXJ, ");
                bufSql.append(" FBONDINS, ");
                bufSql.append(" FHGGAIN ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FPORTCODE, ");
                bufSql.append(" FSECURITYCODE, ");
                bufSql.append(" FSRCSECCODE, ");
                bufSql.append(" FTRADEORDER, ");
                bufSql.append(" FEXCHANGECODE, ");
                bufSql.append(" FSTOCKHOLDERNUM, ");
                bufSql.append(" ' ', ");
                bufSql.append(" FANALYSISCODE2, ");
                bufSql.append(" FTRADETYPECODE, ");
                bufSql.append(" FTRADEAMOUMT, ");
                bufSql.append(" FTRADEPRICE, ");
                bufSql.append(" FTRADEMONEY, ");
                bufSql.append(" FTRADEDATE, ");
                bufSql.append(" FSETTLEDATE, ");
                bufSql.append(" FYHSCODE, ");
                bufSql.append(" FYHS, ");
                bufSql.append(" FJSFCODE, ");
                bufSql.append(" FJSF, ");
                bufSql.append(" FGHFCODE, ");
                bufSql.append(" FGHF, ");
                bufSql.append(" FZGFCODE, ");
                bufSql.append(" FZGF, ");
                bufSql.append(" FQTFCODE, ");
                bufSql.append(" FQTF, ");
                bufSql.append(" FYJCODE, ");
                bufSql.append(" FYJ, ");
                bufSql.append(" FFXJ, ");
                bufSql.append(" FBONDINS, ");
                bufSql.append(" FHGGAIN ");
                bufSql.append(" FROM " +
                              pub.yssGetTableName("TB_DAT_03272009053652000"));
                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                //执行生成的SQL
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                this.dropTableByTableName(pub.yssGetTableName(
                    "TB_DAT_03272009053652000")); //更新完成后删除临时表
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0014增加表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
