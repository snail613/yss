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
public class Ora1010009
    extends BaseDbUpdate {
    public Ora1010009() {
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            //表中不存在FINBEGINTYPE字段，才执行更新
            //以下为在回购信息中添加计息起始日类型字段
            if (existsTabColumn_Ora("TB_" + sPre + "_Para_Purchase", "FInBeginType")) {
                //设置事物参数状态 -- 事物回滚
                bTrans = true;
                //设置事物不自动提交
                conn.setAutoCommit(false);
                //获取主键约束
                strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre + "_Para_Purchase");
                if (strPKName != null && !strPKName.trim().equals("")) {
                    //删除约束
                    dbl.executeSql("ALTER TABLE TB_" + sPre + "_Para_Purchase DROP CONSTRAINT " +
                                   strPKName);
                    //删除索引
                    deleteIndex(strPKName);
                }
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_" + sPre + "_PAR_12082008101628000")) {
                    this.dropTableByTableName("TB_" + sPre + "_PAR_12082008101628000");
                }
                //将原表更改为备份表
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre + "_Para_Purchase RENAME TO TB_" + sPre + "_PAR_12082008101628000");
                //清空bufSql
                bufSql.delete(0, bufSql.length());
                bufSql.append("CREATE TABLE " +
                              pub.yssGetTableName("TB_Para_Purchase"));
                bufSql.append(" ( ");
                bufSql.append("FSECURITYCODE VARCHAR2(20)  NOT NULL,");
                bufSql.append("FDEPDURCODE   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FPERIODCODE   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FPURCHASETYPE VARCHAR2(20)  NOT NULL,");
                bufSql.append("FPURCHASERATE NUMBER(18,12)     NULL,");
                bufSql.append("FINBEGINTYPE  VARCHAR2(20)  DEFAULT 'trade' NOT NULL,"); //计息起始日类型,默认为交易日
                bufSql.append("FDESC         VARCHAR2(100)     NULL,");
                bufSql.append("FCHECKSTATE   NUMBER(1)     NOT NULL,");
                bufSql.append("FCREATOR      VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCREATETIME   VARCHAR2(20)  NOT NULL,");
                bufSql.append("FCHECKUSER    VARCHAR2(20)      NULL,");
                bufSql.append("FCHECKTIME    VARCHAR2(20)      NULL");
                bufSql.append(" )");
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
                //从临时表中导数据到新创建的表中
                bufSql.append("INSERT INTO " +
                              pub.yssGetTableName("TB_Para_Purchase") + "( ");
                bufSql.append("FSECURITYCODE,");
                bufSql.append("FDEPDURCODE,");
                bufSql.append("FPERIODCODE,");
                bufSql.append("FPURCHASETYPE,");
                bufSql.append("FPURCHASERATE,");
                bufSql.append("FINBEGINTYPE,");
                bufSql.append("FDESC,");
                bufSql.append("FCHECKSTATE,");
                bufSql.append("FCREATOR,");
                bufSql.append("FCREATETIME,");
                bufSql.append("FCHECKUSER,");
                bufSql.append("FCHECKTIME )");
                bufSql.append(" SELECT ");
                bufSql.append(" FSECURITYCODE,");
                bufSql.append("FDEPDURCODE,");
                bufSql.append("FPERIODCODE,");
                bufSql.append("FPURCHASETYPE,");
                bufSql.append("FPURCHASERATE,");
                bufSql.append("'trade',"); //起始设置为交易日
                bufSql.append("FDESC,");
                bufSql.append("FCHECKSTATE,");
                bufSql.append("FCREATOR,");
                bufSql.append("FCREATETIME,");
                bufSql.append("FCHECKUSER,");
                bufSql.append("FCHECKTIME");
                bufSql.append(" FROM TB_" + sPre + "_PAR_12082008101628000");
                conn.setAutoCommit(bTrans);
                bTrans = true;
                dbl.executeSql(bufSql.toString());
                conn.commit();
                conn.setAutoCommit(bTrans);
                bTrans = false;
                //设定表的主键
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_Para_Purchase") +
                               " ADD CONSTRAINT PK_TB_" + sPre +
                               "_Para_Purchase PRIMARY KEY (FSECURITYCODE,FINBEGINTYPE)");

            }
        } catch (Exception ex) {
            throw new YssException("版本Oracle1010009更新表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }

    }

}
