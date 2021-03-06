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
public class Ora1010011
    extends BaseDbUpdate {
    public Ora1010011() {
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            if (existsTabColumn_Ora("TB_" + sPre + "_PARA_PERIOD", "FPERIODTYPE")) {
                //设置事物参数状态 -- 事物回滚
                bTrans = true;
                //设置事物不自动提交
                conn.setAutoCommit(false);
                //获取主键约束
                strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                    "_PARA_PERIOD");
                if (strPKName != null && !strPKName.trim().equals("")) {
                    //删除约束
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_PERIOD DROP CONSTRAINT " +
                                   strPKName);
                    //删除索引
                    deleteIndex(strPKName);
                }
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_" + sPre + "_PAR_01242009025047000")) {
                    this.dropTableByTableName("TB_" + sPre +
                                              "_PAR_01242009025047000");
                }
                //将原表更改为备份表
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre + "_PARA_PERIOD RENAME TO TB_" +
                    sPre + "_PAR_01242009025047000");
                //清空bufSql
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_PERIOD ");
                bufSql.append(" ( ");
                bufSql.append(" FPERIODCODE VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FPERIODNAME VARCHAR2(50)  NOT NULL, ");
                bufSql.append(" FPERIODTYPE NUMBER(1)     DEFAULT 0 NOT NULL, ");
                bufSql.append(" FDAYOFMONTH NUMBER(38)        NULL, ");
                bufSql.append(" FDAYOFYEAR  NUMBER(38)        NULL, ");
                bufSql.append(" FDAYIND     NUMBER(1)     NOT NULL, ");
                bufSql.append(" FDESC       VARCHAR2(100)     NULL, ");
                bufSql.append(" FCHECKSTATE NUMBER(1)     NOT NULL, ");
                bufSql.append(" FCREATOR    VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME VARCHAR2(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER  VARCHAR2(20)      NULL, ");
                bufSql.append(" FCHECKTIME  VARCHAR2(20)      NULL ");
                bufSql.append(" ) ");

                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());

                bufSql.append(" INSERT INTO TB_" + sPre + "_PARA_PERIOD( ");
                bufSql.append(" FPERIODCODE, ");
                bufSql.append(" FPERIODNAME, ");
                bufSql.append(" FPERIODTYPE, ");
                bufSql.append(" FDAYOFMONTH, ");
                bufSql.append(" FDAYOFYEAR, ");
                bufSql.append(" FDAYIND, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FPERIODCODE, ");
                bufSql.append(" FPERIODNAME, ");
                bufSql.append(" 0, ");
                bufSql.append(" FDAYOFMONTH, ");
                bufSql.append(" FDAYOFYEAR, ");
                bufSql.append(" FDAYIND, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_" + sPre + "_PAR_01242009025047000 ");
                conn.setAutoCommit(bTrans);
                bTrans = true;
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                //设定表的主键
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_PARA_PERIOD") +
                               " ADD CONSTRAINT PK_TB_" + sPre +
                               "_PARA_PERIOD PRIMARY KEY (FPERIODCODE)");

            }
        } catch (Exception ex) {
            throw new YssException("版本Oracle1010011更新表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
