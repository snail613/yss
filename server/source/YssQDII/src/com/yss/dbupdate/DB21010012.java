package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;

public class DB21010012
    extends BaseDbUpdate {
    public DB21010012() {
    }

    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            //-----------------2009.2.10 蒋锦 MS00195 《QDV4建行2009年1月15日01_B》 修改表 Tb_Comp_ResultData 增加 FCreateDate----------//
            //获取主键名
            strPKName = getIsNullPKByTableName_DB2("TB_" + sPre +
                "_Comp_ResultData");
            if (strPKName != null && strPKName.trim().length() > 0) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Comp_ResultData DROP CONSTRAINT " +
                               strPKName);
            }
            //如果备份表存在 删除备份表
            if (dbl.yssTableExist("TB_02102009060734")) {
                this.dropTableByTableName("TB_02102009060734");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "RENAME TABLE TB_" + sPre + "_Comp_ResultData TO TB_02102009060734");

            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE ").append(pub.yssGetTableName("TB_Comp_ResultData"));
            bufSql.append(" ( ");
            bufSql.append(" FCOMPDATE     DATE           NOT NULL, ");
            bufSql.append(" FPORTCODE     VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FINDEXCFGCODE VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCreateDate   DATE           NOT NULL, ");
            bufSql.append(" FCOMPRESULT   VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FDESC         VARCHAR(100), ");
            bufSql.append(" FCHECKSTATE   DECIMAL(1)     NOT NULL, ");
            bufSql.append(" FCREATOR      VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCREATETIME   VARCHAR(20)    NOT NULL, ");
            bufSql.append(" FCHECKUSER    VARCHAR(20), ");
            bufSql.append(" FCHECKTIME    VARCHAR(20), ");
            bufSql.append(" FNUMERATOR    DECIMAL(18,4), ");
            bufSql.append(" FDENOMINATOR  DECIMAL(18,4), ");
            bufSql.append(" FFACTRATIO    DECIMAL(30,12) ");
            bufSql.append(" ) ");

            //执行创建表
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append(" INSERT INTO ").append("TB_" + sPre + "_COMP_RESULTDATA").append(" (");
            bufSql.append(" FCOMPDATE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FINDEXCFGCODE, ");
            bufSql.append(" FCreateDate, ");
            bufSql.append(" FCOMPRESULT, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME, ");
            bufSql.append(" FNUMERATOR, ");
            bufSql.append(" FDENOMINATOR, ");
            bufSql.append(" FFACTRATIO ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT ");
            bufSql.append(" FCOMPDATE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FINDEXCFGCODE, ");
            bufSql.append(" FCOMPDATE, "); //生成日期不能为空，插入原有的监控日期
            bufSql.append(" FCOMPRESULT, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME, ");
            bufSql.append(" FNUMERATOR, ");
            bufSql.append(" FDENOMINATOR, ");
            bufSql.append(" FFACTRATIO ");
            bufSql.append(" FROM TB_02102009060734 ");

            //执行数据复制
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

            bufSql.delete(0, bufSql.length());
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_COMP_RESULTDATA ADD CONSTRAINT PK_Tb_" + sPre +
                           "_ULTDATA PRIMARY KEY (FCOMPDATE,FPORTCODE,FINDEXCFGCODE)");
            //----------------------------------------------------------------------------------------------------------------------//
            //--------MS00272 QDV4赢时胜（上海）2009年2月26日01_B 添加估值方法字段 ------------------------------------------
            dbl.executeSql("alter table " + pub.yssGetTableName("TB_DATA_VALMKTPRICE") + " ADD COLUMN FMTVCODE VARCHAR(20)");
            //----------------------------------------------------------------------------------------------------------

        } catch (Exception e) {
            throw new YssException("版本1.0.1.0012增加表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
