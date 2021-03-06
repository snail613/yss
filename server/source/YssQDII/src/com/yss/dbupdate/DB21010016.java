package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.*;

public class DB21010016
    extends BaseDbUpdate {
    public DB21010016() {
    }

    public void addTableField() throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            strPKName = getIsNullPKByTableName_DB2("TB_FUN_VERSION");
            if (strPKName != null && strPKName.trim().length() > 0) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_FUN_VERSION DROP CONSTRAINT " + strPKName);
            }
            //如果备份表存在 删除备份表
            if (dbl.yssTableExist("TB_04072009062346")) {
                this.dropTableByTableName("TB_04072009062346");
            }
            //将原表更改为备份表
            dbl.executeSql("RENAME TABLE TB_FUN_VERSION TO TB_04072009062346");
            //创建表
            bufSql.append(" CREATE TABLE TB_FUN_VERSION ");
            bufSql.append(" ( ");
            bufSql.append(" FASSETGROUPCODE VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FVERNUM         VARCHAR(50)   NOT NULL, ");
            bufSql.append(" FISSUEDATE      DATE          NOT NULL, ");
            bufSql.append(" FFINISH         VARCHAR(20)   NOT NULL DEFAULT 'Fail', ");
            bufSql.append(" FUserCode       VARCHAR(20)   NOT NULL, ");
            bufSql.append(" FUPDATETABLES   CLOB, "); //xuqiji 20090416 原因：更新表太多时，原来字段精度不够  MS00352    新建组合群时能够自动创建对应的一套表    ------------------
            bufSql.append(" FERRORINFO      VARCHAR(4000), ");
            bufSql.append(" FSQLSTR         CLOB, ");
            bufSql.append(" FDESC           VARCHAR(1000), ");
            bufSql.append(" FCREATEDATE     DATE          NOT NULL, ");
            bufSql.append(" FCREATETIME     VARCHAR(20)   NOT NULL ");
            bufSql.append(" ) ");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            //复制数据
            bufSql.append(" INSERT INTO TB_FUN_VERSION( ");
            bufSql.append(" FASSETGROUPCODE, ");
            bufSql.append(" FVERNUM, ");
            bufSql.append(" FISSUEDATE, ");
            bufSql.append(" FFINISH, ");
            bufSql.append(" FUserCode, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCREATEDATE, ");
            bufSql.append(" FCREATETIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FASSETGROUPCODE, ");
            bufSql.append(" FVERNUM, ");
            bufSql.append(" FISSUEDATE, ");
            bufSql.append(" FFINISH, ");
            bufSql.append(" ' ', ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCREATEDATE, ");
            bufSql.append(" FCREATETIME ");
            bufSql.append(" FROM TB_04072009062346 ");

            bTrans = false;
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //执行生成的SQL
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            dbl.executeSql("ALTER TABLE TB_FUN_VERSION ADD CONSTRAINT PK_TB_FUN_VERSION" +
                           " PRIMARY KEY (FASSETGROUPCODE,FVERNUM)");
            dropTableByTableName("TB_04072009062346");
        } catch (Exception ex) {
            throw new YssException("1.0.1.0016 更新表结构出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
