package com.yss.dbupdate;

import com.yss.util.YssException;
import java.sql.Connection;

public class Ora1010002sp1
    extends BaseDbUpdate {
    public Ora1010002sp1() {
    }

    //创建带组合群号的表
    public void createTable(String sPre) throws YssException {
        StringBuffer bufSql = new StringBuffer(10000);
        try {
            //--------------------------创建表Tb_001_PFOPER_PUBPARA生成通用参数表---------------------------//
            bufSql.append("CREATE TABLE Tb_" + sPre + "_PFOPER_PUBPARA");
            bufSql.append("(");
            bufSql.append("FPubParaCode VARCHAR2(20)  NOT NULL,");
            bufSql.append("FParaGroupCode    VARCHAR2(50)  NOT NULL,");
            bufSql.append("FParaId    NUMBER(2, 0)  DEFAULT 0 NOT NULL,");
            bufSql.append("FCtlCode        VARCHAR2(20)    DEFAULT '' NOT NULL,");
            bufSql.append("FPubParaName  VARCHAR2(50)  NOT NULL,");
            bufSql.append("FCtlGrpCode     VARCHAR2(20),");
            bufSql.append("FCtlValue  VARCHAR2(1000),");
            bufSql.append("FOrderCode   VARCHAR2(50)    NOT  NULL,");
            bufSql.append("FDesc   VARCHAR2(100),");
            bufSql.append("CONSTRAINT PK_Tb_" + sPre +
                          "_PFOPER_PUBPARA PRIMARY KEY (FPubParaCode,FParaGroupCode, FParaId, FCtlCode)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

        } catch (Exception e) {
            throw new YssException("版本1.0.1.0002sp1 创建表出错！", e);
        }
    }

    //增加表字段
    public void addTableField(String sPre) throws YssException {
        boolean bTrans = false; //代表是否开始事务
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String strPKName = "";
        try {
            //-----------------------------Tb_001_Data_NavData  添加字段 FInOut-------------------------------------//
            strPKName = this.getIsNullPKByTableName_Ora(pub.yssGetTableName(
                "TB_DATA_NavData")); //通过表名查询主键名
            if (strPKName.trim().length() != 0) {
                dbl.executeSql(
                    "ALTER TABLE TB_" + sPre +
                    "_DATA_NavData DROP CONSTRAINT " + strPKName + " CASCADE"); //删除表主键
            }
            if (dbl.yssTableExist("TB_" + sPre + "_DAT_02282008123619000")) { //判断临时表是否存在 如果临时表已经存在则删除此表
                this.dropTableByTableName("TB_" + sPre + "_DAT_02282008123619000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_DATA_NavData RENAME TO TB_"
                           + sPre + "_DAT_02282008123619000"); //将原表更名为临时表
            bufSql.append("CREATE TABLE Tb_" + sPre + "_DATA_NavData ");
            bufSql.append("(");
            bufSql.append("FNAVDATE         DATE          NOT NULL,");
            bufSql.append("FPORTCODE        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FORDERCODE       VARCHAR2(200) NOT NULL,");
            bufSql.append("FRETYPECODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FINVMGRCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append("FKEYCODE         VARCHAR2(20)  NOT NULL,");
            bufSql.append("FInOut           NUMBER(1)     DEFAULT 1 NOT NULL,");
            bufSql.append("FKEYNAME         VARCHAR2(200) NOT NULL,");
            bufSql.append("FDETAIL          NUMBER(1)     NOT NULL,");
            bufSql.append("FCURYCODE        VARCHAR2(20)  NOT NULL,");
            bufSql.append("FPRICE           NUMBER(20,12)     NULL,");
            bufSql.append("FOTPRICE1        NUMBER(20,12)     NULL,");
            bufSql.append("FOTPRICE2        NUMBER(20,12)     NULL,");
            bufSql.append("FOTPRICE3        NUMBER(20,12)     NULL,");
            bufSql.append("FSEDOLCODE       VARCHAR2(20)      NULL,");
            bufSql.append("FISINCODE        VARCHAR2(20)      NULL,");
            bufSql.append("FSPARAMT         NUMBER(20,4)      NULL,");
            bufSql.append("FBASECURYRATE    NUMBER(20,15) NOT NULL,");
            bufSql.append("FPORTCURYRATE    NUMBER(20,15) NOT NULL,");
            bufSql.append("FCOST            NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPORTCOST        NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FMARKETVALUE     NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPORTMARKETVALUE NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FMVVALUE         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FPORTMVVALUE     NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FFXVALUE         NUMBER(18,4)  DEFAULT 0 NOT NULL,");
            bufSql.append("FGRADETYPE1      VARCHAR2(20)      NULL,");
            bufSql.append("FGRADETYPE2      VARCHAR2(20)      NULL,");
            bufSql.append("FGRADETYPE3      VARCHAR2(20)      NULL,");
            bufSql.append("FGRADETYPE4      VARCHAR2(20)      NULL,");
            bufSql.append("FGRADETYPE5      VARCHAR2(20)      NULL,");
            bufSql.append("FGRADETYPE6      VARCHAR2(20)      NULL ");
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
            bufSql.append(" FROM TB_" + sPre + "_DAT_02282008123619000");
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(bufSql.toString());
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            dbl.executeSql("ALTER TABLE Tb_" + sPre +
                           "_Data_NavData ADD CONSTRAINT PK_TB_" + sPre +
                           "_DATA_NAVDATA PRIMARY KEY (FNAVDATE,FPORTCODE,FORDERCODE,FRETYPECODE,FINVMGRCODE,FKEYCODE)");
        } catch (Exception e) {
            throw new YssException("版本 1.0.1.0002sp1 新增表字段出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
