package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.Connection;

public class DB21010002sp1
    extends BaseDbUpdate {
    public DB21010002sp1() {
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------Tb_001_Data_NavData  添加字段 FInOut-------------------------------------//
            strPKName = this.getIsNullPKByTableName_DB2(pub.yssGetTableName("TB_DATA_NavData")); //通过表名查询主键名
            if (strPKName.length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE " + pub.yssGetTableName("TB_DATA_NavData") +
                    " DROP CONSTRAINT " + strPKName); //删除表主键
            }
            if (dbl.yssTableExist("TB_02292008063150")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_02292008063150");
            }
            dbl.executeSql("RENAME TABLE TB_" + sPre + "_DATA_NavData TO TB_02292008063150"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_DATA_NavData ");
            bufSql.append("(");
            bufSql.append("FNAVDATE         DATE           NOT NULL,");
            bufSql.append("FPORTCODE        VARCHAR(20)    NOT NULL,");
            bufSql.append("FORDERCODE       VARCHAR(200)   NOT NULL,");
            bufSql.append("FRETYPECODE      VARCHAR(20)    NOT NULL,");
            bufSql.append("FINVMGRCODE      VARCHAR(20)    NOT NULL,");
            bufSql.append("FKEYCODE         VARCHAR(20)    NOT NULL,");
            bufSql.append("FInout           DECIMAL(1)     NOT NULL DEFAULT 1,");
            bufSql.append("FKEYNAME         VARCHAR(200)   NOT NULL,");
            bufSql.append("FDETAIL          DECIMAL(1)     NOT NULL,");
            bufSql.append("FCURYCODE        VARCHAR(20)    NOT NULL,");
            bufSql.append("FPRICE           DECIMAL(20,12),");
            bufSql.append("FOTPRICE1        DECIMAL(20,12),");
            bufSql.append("FOTPRICE2        DECIMAL(20,12),");
            bufSql.append("FOTPRICE3        DECIMAL(20,12),");
            bufSql.append("FSEDOLCODE       VARCHAR(20),");
            bufSql.append("FISINCODE        VARCHAR(20),");
            bufSql.append("FSPARAMT         DECIMAL(20,4),");
            bufSql.append("FBASECURYRATE    DECIMAL(20,15) NOT NULL,");
            bufSql.append("FPORTCURYRATE    DECIMAL(20,15) NOT NULL,");
            bufSql.append("FCOST            DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FPORTCOST        DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FMARKETVALUE     DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FPORTMARKETVALUE DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FMVVALUE         DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FPORTMVVALUE     DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FFXVALUE         DECIMAL(18,4)  NOT NULL DEFAULT 0,");
            bufSql.append("FGRADETYPE1      VARCHAR(20),");
            bufSql.append("FGRADETYPE2      VARCHAR(20),");
            bufSql.append("FGRADETYPE3      VARCHAR(20),");
            bufSql.append("FGRADETYPE4      VARCHAR(20),");
            bufSql.append("FGRADETYPE5      VARCHAR(20),");
            bufSql.append("FGRADETYPE6      VARCHAR(20) ");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bufSql.append("INSERT INTO Tb_" + sPre + "_Data_NavData( ");
            bufSql.append(" FNAVDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FORDERCODE,");
            bufSql.append(" FRETYPECODE,");
            bufSql.append(" FINVMGRCODE,");
            bufSql.append(" FKEYCODE,");
            bufSql.append(" FInOut,");
            bufSql.append(" FKEYNAME,");
            bufSql.append(" FDETAIL,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FPRICE,");
            bufSql.append(" FOTPRICE1,");
            bufSql.append(" FOTPRICE2,");
            bufSql.append(" FOTPRICE3,");
            bufSql.append(" FSEDOLCODE,");
            bufSql.append(" FISINCODE,");
            bufSql.append(" FSPARAMT,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FCOST,");
            bufSql.append(" FPORTCOST,");
            bufSql.append(" FMARKETVALUE,");
            bufSql.append(" FPORTMARKETVALUE,");
            bufSql.append(" FMVVALUE,");
            bufSql.append(" FPORTMVVALUE,");
            bufSql.append(" FFXVALUE,");
            bufSql.append(" FGRADETYPE1,");
            bufSql.append(" FGRADETYPE2,");
            bufSql.append(" FGRADETYPE3,");
            bufSql.append(" FGRADETYPE4,");
            bufSql.append(" FGRADETYPE5,");
            bufSql.append(" FGRADETYPE6 ");
            bufSql.append(") SELECT ");
            bufSql.append(" FNAVDATE,");
            bufSql.append(" FPORTCODE,");
            bufSql.append(" FORDERCODE,");
            bufSql.append(" FRETYPECODE,");
            bufSql.append(" FINVMGRCODE,");
            bufSql.append(" FKEYCODE,");
            bufSql.append(" 0,");
            bufSql.append(" FKEYNAME,");
            bufSql.append(" FDETAIL,");
            bufSql.append(" FCURYCODE,");
            bufSql.append(" FPRICE,");
            bufSql.append(" FOTPRICE1,");
            bufSql.append(" FOTPRICE2,");
            bufSql.append(" FOTPRICE3,");
            bufSql.append(" FSEDOLCODE,");
            bufSql.append(" FISINCODE,");
            bufSql.append(" FSPARAMT,");
            bufSql.append(" FBASECURYRATE,");
            bufSql.append(" FPORTCURYRATE,");
            bufSql.append(" FCOST,");
            bufSql.append(" FPORTCOST,");
            bufSql.append(" FMARKETVALUE,");
            bufSql.append(" FPORTMARKETVALUE,");
            bufSql.append(" FMVVALUE,");
            bufSql.append(" FPORTMVVALUE,");
            bufSql.append(" FFXVALUE,");
            bufSql.append(" FGRADETYPE1,");
            bufSql.append(" FGRADETYPE2,");
            bufSql.append(" FGRADETYPE3,");
            bufSql.append(" FGRADETYPE4,");
            bufSql.append(" FGRADETYPE5,");
            bufSql.append(" FGRADETYPE6 ");
            bufSql.append(" FROM TB_02292008063150 ");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Data_NavData ADD CONSTRAINT PK_Tb_Data_NavData PRIMARY KEY (FNAVDATE,FPORTCODE,FORDERCODE,FRETYPECODE,FINVMGRCODE,FKEYCODE)");
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0002sp 新增表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
