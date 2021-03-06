package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;

public class Ora1010012
    extends BaseDbUpdate {
    public Ora1010012() {
    }

    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            //-----------------2009.2.10 蒋锦 MS00195 《QDV4建行2009年1月15日01_B》 修改表 Tb_Comp_ResultData 增加 FCreateDate----------//
            //获取主键名
            strPKName = getIsNullPKByTableName_Ora("TB_" + sPre +
                "_Comp_ResultData");
            if (strPKName != null && strPKName.trim().length() > 0) {
                //删除约束
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_Comp_ResultData DROP CONSTRAINT " +
                               strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在 删除备份表
            if (dbl.yssTableExist("TB_" + sPre + "_COM_02102009051923000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_COM_02102009051923000");
            }
            //将原表更改为备份表
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre + "_Comp_ResultData RENAME TO TB_" +
                sPre + "_COM_02102009051923000");

            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE ").append(pub.yssGetTableName("TB_COMP_RESULTDATA"));
            bufSql.append(" ( ");
            bufSql.append(" FCOMPDATE     DATE          NOT NULL, ");
            bufSql.append(" FPORTCODE     VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FINDEXCFGCODE VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATEDATE   DATE          NOT NULL, ");
            bufSql.append(" FCOMPRESULT   VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FDESC         VARCHAR2(100)     NULL, ");
            bufSql.append(" FCHECKSTATE   NUMBER(1)     NOT NULL, ");
            bufSql.append(" FCREATOR      VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCREATETIME   VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FCHECKUSER    VARCHAR2(20)      NULL, ");
            bufSql.append(" FCHECKTIME    VARCHAR2(20)      NULL, ");
            bufSql.append(" FNUMERATOR    NUMBER(18,4)      NULL, ");
            bufSql.append(" FDENOMINATOR  NUMBER(18,4)      NULL, ");
            bufSql.append(" FFACTRATIO    NUMBER(30,12)     NULL ");
            bufSql.append(" ) ");

            //执行创建表
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            bufSql.append(" INSERT INTO ").append(pub.yssGetTableName("TB_COMP_RESULTDATA")).append("( ");
            bufSql.append(" FCOMPDATE, ");
            bufSql.append(" FPORTCODE, ");
            bufSql.append(" FINDEXCFGCODE, ");
            bufSql.append(" FCREATEDATE, ");
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
            bufSql.append(" FROM TB_" + sPre + "_COM_02102009051923000 ");

            //执行数据复制
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

            bufSql.delete(0, bufSql.length());

            //添加约束
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_COMP_RESULTDATA") +
                           " ADD CONSTRAINT PK_TB_" + sPre +
                           "_COMP_RESULTDATA PRIMARY KEY (FCOMPDATE,FPORTCODE,FINDEXCFGCODE)");
            //----------------------------------------------------------------------------------------------------------------------//

            //--------MS00272 QDV4赢时胜（上海）2009年2月26日01_B 添加估值方法字段 ------------------------------------------
            dbl.executeSql("alter table " + pub.yssGetTableName("TB_DATA_VALMKTPRICE") + " add FMTVCODE VARCHAR2(20)");
            //----------------------------------------------------------------------------------------------------------
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0012增加表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
