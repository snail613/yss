package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;

public class DB21010014sp6
    extends BaseDbUpdate {
    public DB21010014sp6() {
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
            if (existsTabColumn_DB2(pub.yssGetTableName("tb_data_tradedetaila"),
                                    "FSeatNum")) { //先检查表中是否有这个字段
                bufSql.delete(0, bufSql.length()); //清空BUF
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_03272009054255")) {
                    this.dropTableByTableName("TB_03272009054255");
                }
                //----------------------源表更改为备份表-------------------------------------//
                dbl.executeSql("RENAME TABLE " +
                               pub.yssGetTableName("tb_data_tradedetaila") +
                               " TO TB_03272009054255");
                //-----------------------创建新表------------------------------------------//
                bufSql.append(" CREATE TABLE " +
                              pub.yssGetTableName("tb_data_tradedetaila"));
                bufSql.append(" ( ");
                bufSql.append(" FPORTCODE       VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FSECURITYCODE   VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FSRCSECCODE     VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FTRADEORDER     VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FEXCHANGECODE   VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FSTOCKHOLDERNUM VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FSEATNUM        VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FANALYSISCODE2  VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FTRADETYPECODE  VARCHAR(20)   NOT NULL, ");
                bufSql.append(" FTRADEAMOUMT    DECIMAL(18,4) NOT NULL, ");
                bufSql.append(" FTRADEPRICE     DECIMAL(18,4) NOT NULL, ");
                bufSql.append(" FTRADEMONEY     DECIMAL(18,4) NOT NULL, ");
                bufSql.append(" FTRADEDATE      DATE          NOT NULL, ");
                bufSql.append(" FSETTLEDATE     DATE          NOT NULL, ");
                bufSql.append(" FYHSCODE        VARCHAR(20)   NOT NULL, ");
                bufSql.append(
                    " FYHS            DECIMAL(18,4) NOT NULL DEFAULT 0, ");
                bufSql.append(" FJSFCODE        VARCHAR(20)   NOT NULL, ");
                bufSql.append(
                    " FJSF            DECIMAL(18,4) NOT NULL DEFAULT 0, ");
                bufSql.append(" FGHFCODE        VARCHAR(20)   NOT NULL, ");
                bufSql.append(
                    " FGHF            DECIMAL(18,4) NOT NULL DEFAULT 0, ");
                bufSql.append(" FZGFCODE        VARCHAR(20)   NOT NULL, ");
                bufSql.append(
                    " FZGF            DECIMAL(18,4) NOT NULL DEFAULT 0, ");
                bufSql.append(" FQTFCODE        VARCHAR(20)   NOT NULL, ");
                bufSql.append(
                    " FQTF            DECIMAL(18,4) NOT NULL DEFAULT 0, ");
                bufSql.append(" FYJCODE         VARCHAR(20)   NOT NULL, ");
                bufSql.append(
                    " FYJ             DECIMAL(18,4) NOT NULL DEFAULT 0, ");
                bufSql.append(
                    " FFXJ            DECIMAL(18,4) NOT NULL DEFAULT 0, ");
                bufSql.append(
                    " FBONDINS        DECIMAL(18,4) NOT NULL DEFAULT 0, ");
                bufSql.append(
                    " FHGGAIN         DECIMAL(18,4) NOT NULL DEFAULT 0 ");
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
                bufSql.append(" FROM TB_03272009054255");
                bTrans = false;
                conn.setAutoCommit(bTrans);
                bTrans = true;
                //执行生成的SQL
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                this.dropTableByTableName("TB_03272009054255"); //更新完成后删除临时表
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0014增加表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
