package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.Connection;

/**
 * <p>Title: 更新表结构</p>
 *
 * <p>Description: xuqiji 20090611 QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DB21010015sp11
    extends BaseDbUpdate {
    public DB21010015sp11() {
    }

    public void adjustFieldPrecision(String sPre) throws YssException {
        boolean bTrans = true;
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            if (!existsTabColumn_DB2(pub.yssGetTableName("TB_PFSys_ValCompare"), "FComScript")) { //先检查表中是否有这个字段
                bufSql.delete(0, bufSql.length()); //清空BUF
                //获取主键约束
                strPKName = this.getIsNullPKByTableName_DB2("TB_PFSys_ValCompare");
                if (strPKName != null && !strPKName.trim().equals("")) {
                    //删除约束
                    dbl.executeSql("ALTER TABLE TB_PFSys_ValCompare DROP CONSTRAINT " + strPKName);
                }
                //如果备份表存在 删除备份表
                if (dbl.yssTableExist("TB_06112009054141")) {
                    this.dropTableByTableName("TB_06112009054141");
                }
                //----------------------源表更改为备份表-------------------------------------//
                dbl.executeSql("RENAME TABLE " + pub.yssGetTableName("TB_PFSys_ValCompare") + " TO TB_06112009054141");
                //-----------------------创建新表------------------------------------------//
                bufSql.append(" CREATE TABLE " + pub.yssGetTableName("TB_PFSys_ValCompare"));
                bufSql.append(" ( ");
                bufSql.append(" FCOMPROJECTCODE VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCOMPROJECTNAME VARCHAR(50)  NOT NULL, ");
                bufSql.append(" FCOMSCRIPT      CLOB(18M)    NOT NULL LOGGED NOT COMPACT, ");
                bufSql.append(" FDESC           VARCHAR(200), ");
                bufSql.append(" FCHECKSTATE     DECIMAL(1)   NOT NULL, ");
                bufSql.append(" FCREATOR        VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCREATETIME     VARCHAR(20)  NOT NULL, ");
                bufSql.append(" FCHECKUSER      VARCHAR(20), ");
                bufSql.append(" FCHECKTIME      VARCHAR(20) ");
                bufSql.append(" ) ");
                dbl.executeSql(bufSql.toString()); //执行语句
                bufSql.delete(0, bufSql.length()); //清空bufSql
                //--------------从备份表导入数据到新表-------------------------//
                bufSql.append(" INSERT INTO " + pub.yssGetTableName("TB_PFSys_ValCompare") + "(");
                bufSql.append(" FCOMPROJECTCODE, ");
                bufSql.append(" FCOMPROJECTNAME, ");
                bufSql.append(" FCOMSCRIPT, ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" ) ");
                bufSql.append(" SELECT ");
                bufSql.append(" FCOMPROJECTCODE, ");
                bufSql.append(" FCOMPROJECTNAME, ");
                bufSql.append(" CLOB(FCOMSCRIPT), ");
                bufSql.append(" FDESC, ");
                bufSql.append(" FCHECKSTATE, ");
                bufSql.append(" FCREATOR, ");
                bufSql.append(" FCREATETIME, ");
                bufSql.append(" FCHECKUSER, ");
                bufSql.append(" FCHECKTIME ");
                bufSql.append(" FROM TB_06112009054141");

                conn.setAutoCommit(false);
                //执行生成的SQL
                dbl.executeSql(bufSql.toString());
                dbl.executeSql("ALTER TABLE TB_PFSYS_VALCOMPARE ADD CONSTRAINT PK_TB_PFSys_ValCom PRIMARY KEY (FCOMPROJECTCODE)");
                conn.commit();
                conn.setAutoCommit(true);
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("版本1010015sp11修改表字段精度出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
