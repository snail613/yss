package com.yss.dbupdate;

import java.sql.*;

import com.yss.util.*;

public class Ora1010016
    extends BaseDbUpdate {
    public Ora1010016() {
    }

    public void addTableField() throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            strPKName = getIsNullPKByTableName_Ora("TB_FUN_VERSION");
            if (strPKName != null && strPKName.trim().length() > 0) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_FUN_VERSION DROP CONSTRAINT " + strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在 删除备份表
            if (dbl.yssTableExist("TB_FUN_VER_04072009062149000")) {
                this.dropTableByTableName("TB_FUN_VER_04072009062149000");
            }
            //将原表更改为备份表
            dbl.executeSql("ALTER TABLE TB_FUN_VERSION RENAME TO TB_FUN_VER_04072009062149000");
            //重新创建表
            bufSql.append(" CREATE TABLE TB_FUN_VERSION ");
            bufSql.append(" ( ");
            bufSql.append(" FASSETGROUPCODE VARCHAR2(20)   NOT NULL, ");
            bufSql.append(" FVERNUM         VARCHAR2(50)   NOT NULL, ");
            bufSql.append(" FISSUEDATE      DATE           NOT NULL, ");
            bufSql.append(" FFINISH         VARCHAR2(20)   DEFAULT 'Fail' NOT NULL, ");
            bufSql.append(" FUSERCODE       VARCHAR2(20)   NOT NULL, ");
            bufSql.append(" FUPDATETABLES   CLOB               NULL, "); //xuqiji 20090416 原因：更新表太多时，原来字段精度不够 MS00352    新建组合群时能够自动创建对应的一套表    ------
            bufSql.append(" FERRORINFO      VARCHAR2(4000)     NULL, ");
            bufSql.append(" FSQLSTR         CLOB               NULL, ");
            bufSql.append(" FDESC           VARCHAR2(1000)     NULL, ");
            bufSql.append(" FCREATEDATE     DATE           NOT NULL, ");
            bufSql.append(" FCREATETIME     VARCHAR2(20)   NOT NULL ");
            bufSql.append(" ) ");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            //复制数据
            bufSql.append(" INSERT INTO TB_FUN_VERSION( ");
            bufSql.append(" FASSETGROUPCODE, ");
            bufSql.append(" FVERNUM, ");
            bufSql.append(" FISSUEDATE, ");
            bufSql.append(" FFINISH, ");
            bufSql.append(" FUSERCODE, ");
            bufSql.append(" FUPDATETABLES, ");
            bufSql.append(" FERRORINFO, ");
            bufSql.append(" FSQLSTR, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCREATEDATE, ");
            bufSql.append(" FCREATETIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FASSETGROUPCODE, ");
            bufSql.append(" FVERNUM, ");
            bufSql.append(" FISSUEDATE, ");
            bufSql.append(" NVL(FFINISH,'Fail'), ");
            bufSql.append(" ' ', ");
            bufSql.append(" NULL, ");
            bufSql.append(" NULL, ");
            bufSql.append(" NULL, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCREATEDATE, ");
            bufSql.append(" FCREATETIME ");
            bufSql.append(" FROM TB_FUN_VER_04072009062149000 ");

            bTrans = false;
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //执行生成的SQL
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //添加主键
            dbl.executeSql("ALTER TABLE TB_FUN_VERSION ADD CONSTRAINT PK_TB_FUN_VERSION" +
                           " PRIMARY KEY (FASSETGROUPCODE,FVERNUM)");
            dropTableByTableName("TB_FUN_VER_04072009062149000");
        } catch (Exception ex) {
            throw new YssException("1.0.1.0016 更新表结构出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //---在此处调用rename表名的方法，以便在控制类中不再修改。---------------------//
        adjustTableName();
        //----------------------------------------------------------------------//
    }

    /**
     * 调制表名
     * @param sPre String
     * @throws YssException
     * sj 20090817 为了调整中保的Summary报表
     */
    public void adjustTableName() throws YssException {
        String sqlStr = null;
        try{
            //这里的修改主要是针对中保的，因为中保存在手动调整表的行为，所以要对表进行调整，DB2就不处理了
            if(dbl.yssTableExist(pub.yssGetTableName("TB_DATA_SUMMARY"))){
                sqlStr = "ALTER TABLE " + pub.yssGetTableName("TB_DATA_SUMMARY") +
                    " RENAME TO " + pub.yssGetTableName("TB_DATA_SUMMARY_20090817");
                dbl.executeSql(sqlStr);
            }
        }catch(Exception e){
            throw new YssException("1.0.1.0016 更新表结构出错！",e);
        }
    }

}
