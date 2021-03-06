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
public class DB21010011
    extends BaseDbUpdate {
    public DB21010011() {
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            //表中不存在FINBEGINTYPE字段，才执行更新
            //以下为在回购信息中添加计息起始日类型字段
            if (existsTabColumn_DB2("TB_" + sPre + "_PARA_PERIOD",
                                    "FPeriodType")) {
                //设置事物参数状态 -- 事物回滚
                bTrans = true;
                //设置事物不自动提交
                conn.setAutoCommit(false);
                //获取主键约束
                strPKName = this.getIsNullPKByTableName_DB2("TB_" + sPre +
                    "_PARA_PERIOD");
                if (strPKName != null && !strPKName.trim().equals("")) {
                    //删除约束
                    dbl.executeSql("ALTER TABLE TB_" + sPre +
                                   "_PARA_PERIOD DROP CONSTRAINT " +
                                   strPKName);
                }
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_01242009030353")) {
                    this.dropTableByTableName("TB_01242009030353");
                }
                //将原表更改为备份表
                dbl.executeSql("RENAME TABLE TB_" + sPre +
                               "_PARA_PERIOD TO TB_01242009030353");
                //清空bufSql
                bufSql.delete(0, bufSql.length());
                bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_PERIOD ");
                bufSql.append(" ( ");
                bufSql.append(" FPERIODCODE VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FPERIODNAME VARCHAR(50)  NOT NULL, ");
                bufSql.append(" FPeriodType DECIMAL(1)   NOT NULL DEFAULT 0, ");
                bufSql.append(" FDAYOFMONTH INTEGER, ");
                bufSql.append(" FDAYOFYEAR  INTEGER, ");
                bufSql.append(" FDAYIND     DECIMAL(1)   NOT NULL, ");
                bufSql.append(" FDESC       VARCHAR(100), ");
                bufSql.append(" FCHECKSTATE DECIMAL(1)   NOT NULL, ");
                bufSql.append(" FCREATOR    VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER  VARCHAR(20), ");
                bufSql.append(" FCHECKTIME  VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString());
                //清空bufSql
                bufSql.delete(0, bufSql.length());

                bufSql.append(" INSERT INTO TB_" + sPre + "_PARA_PERIOD( ");
                bufSql.append(" FPERIODCODE, ");
                bufSql.append(" FPERIODNAME, ");
                bufSql.append(" FPeriodType, ");
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
                bufSql.append(" FROM TB_01242009030353 ");
                dbl.executeSql(bufSql.toString());
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_PERIOD ADD CONSTRAINT PK_Tb_" + sPre +
                               "_Para_Per PRIMARY KEY (FPERIODCODE)");

                bTrans = false;
                conn.setAutoCommit(true);
                conn.commit();
            }
        } catch (Exception ex) {
            throw new YssException("版本DB210100011更新表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }

    }

}
