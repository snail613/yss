package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.YssException;

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
public class Ora1010003sp5
    extends BaseDbUpdate {
    public Ora1010003sp5() {
    }

    //调整表字段
    public void adjustFieldPrecision(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //BugNo:0000415 edit by jc
            //对表Tb_XXX_VCH_DATAENTITY中调整Fprice字段的精度
            //通过表名查询主键名
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_VCH_DATAENTITY");
            //如果有主键规则就删除之
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE " +
                               pub.yssGetTableName("TB_VCH_DATAENTITY") +
                               " DROP CONSTRAINT " + sPKName + " CASCADE");
                deleteIndex(sPKName); //若是没有级联删除索引，则手工删除
            }
            //判断临时表是否存在 如果临时表已经存在则删除此表
            if (dbl.yssTableExist("TB_" + sPre + "_VCH_05162008081525087")) {
                this.dropTableByTableName("TB_" + sPre + "_VCH_05162008081525087");
            }
            //把源表重命名为临时表
            dbl.executeSql("ALTER TABLE " +
                           pub.yssGetTableName("TB_VCH_DATAENTITY") +
                           " RENAME TO TB_" + sPre + "_VCH_05162008081525087");
            //首先清空字符流
            bufSql.delete(0, bufSql.length());
            //新建表Tb_XXX_VCH_DATAENTITY
            bufSql.append(" CREATE TABLE TB_" + sPre + "_VCH_DATAENTITY");
            bufSql.append(" (");
            bufSql.append(" FVchNum       VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FEntityNum    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FSubjectCode  VARCHAR2(50)  NOT NULL,");
            bufSql.append(" FCuryRate     NUMBER(35,12) NOT NULL,");
            bufSql.append(" FResume       VARCHAR2(50)      NULL,");
            bufSql.append(" FDCWay        VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FBookSetCode  VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FBal          NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FSetBal       NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FAmount       NUMBER(18,4)      NULL,");
            bufSql.append(" FPrice        NUMBER(20,8)      NULL,");
            bufSql.append(" FAssistant    VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDesc         VARCHAR2(100)     NULL,");
            bufSql.append(" FCheckState   NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME   VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER    VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME    VARCHAR2(20)      NULL");
            bufSql.append(" )");
            dbl.executeSql(bufSql.toString());
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //清空字符流
            bufSql.delete(0, bufSql.length());
            //插入数据
            bufSql.append(" INSERT INTO TB_" + sPre + "_VCH_DATAENTITY(");
            bufSql.append(" FVchNum,");
            bufSql.append(" FEntityNum,");
            bufSql.append(" FSubjectCode,");
            bufSql.append(" FCuryRate,");
            bufSql.append(" FResume,");
            bufSql.append(" FDCWay,");
            bufSql.append(" FBookSetCode,");
            bufSql.append(" FBal,");
            bufSql.append(" FSetBal,");
            bufSql.append(" FAmount,");
            bufSql.append(" FPrice,");
            bufSql.append(" FAssistant,");
            bufSql.append(" FDesc,");
            bufSql.append(" FCheckState,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" )");
            bufSql.append(" SELECT");
            bufSql.append(" FVchNum,");
            bufSql.append(" FEntityNum,");
            bufSql.append(" FSubjectCode,");
            bufSql.append(" FCuryRate,");
            bufSql.append(" FResume,");
            bufSql.append(" FDCWay,");
            bufSql.append(" FBookSetCode,");
            bufSql.append(" FBal,");
            bufSql.append(" FSetBal,");
            bufSql.append(" FAmount,");
            bufSql.append(" FPrice,");
            bufSql.append(
                " (case when FAssistant is null then ' ' else FAssistant end),");
            bufSql.append(" FDesc,");
            bufSql.append(" FCheckState,");
            bufSql.append(" FCREATOR,");
            bufSql.append(" FCREATETIME,");
            bufSql.append(" FCHECKUSER,");
            bufSql.append(" FCHECKTIME");
            bufSql.append(" FROM TB_" + sPre + "_VCH_05162008081525087");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键约束
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_VCH_DATAENTITY ADD CONSTRAINT PK_TB_" + sPre +
                           "_VCH_DATAENTITY PRIMARY KEY (FVchNum,FEntityNum)");
            //-----------------------jc
        } catch (Exception ex) {
            throw new YssException("版本 ORacle1010003sp5 调整表字段出错", ex);
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //调整表的主键
    public void adjustTableKey(String sPre) throws YssException {
        String sPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //BugNo:0000425 edit by jc
            //调整表 Tb_XXX_Para_BrokerSubBny 的主键 FBROKERCODE,FEXCHANGECODE,FTRADECATCODE
            sPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre +
                "_PARA_BROKERSUBBNY");
            if (sPKName.trim().length() != 0) {
                dbl.executeSql("ALTER TABLE TB_" + sPre +
                               "_PARA_BROKERSUBBNY DROP CONSTRAINT " + sPKName +
                               " CASCADE");
                deleteIndex(sPKName);
            }
            if (dbl.yssTableExist("TB_" + sPre + "_PAR_08052008061540000")) {
                this.dropTableByTableName("TB_" + sPre +
                                          "_PAR_08052008061540000");
            }
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_PARA_BROKERSUBBNY RENAME TO TB_" + sPre +
                           "_PAR_08052008061540000");
            bufSql.delete(0, bufSql.length());
            bufSql.append(" CREATE TABLE TB_" + sPre + "_PARA_BROKERSUBBNY ");
            bufSql.append(" (");
            bufSql.append(" FBROKERCODE        VARCHAR2(20) NOT NULL, ");
            bufSql.append(" FEXCHANGECODE      VARCHAR2(20) NOT NULL, ");
            bufSql.append(" FBROKERIDTYPE      VARCHAR2(20)     NULL, ");
            bufSql.append(" FBROKERID          VARCHAR2(20)     NULL, ");
            bufSql.append(" FIFBROKERNAME      VARCHAR2(200)    NULL, ");
            bufSql.append(" FCLEARERIDTYPE     VARCHAR2(20)     NULL, ");
            bufSql.append(" FCLEARERID         VARCHAR2(20)     NULL, ");
            bufSql.append(" FCLEARERNAME       VARCHAR2(200)    NULL, ");
            bufSql.append(" FCLEARERDESC       VARCHAR2(100)    NULL, ");
            bufSql.append(" FBROKERACCOUNT     VARCHAR2(50)     NULL, ");
            bufSql.append(" FPLACEOFSETTLEMENT VARCHAR2(50)     NULL, ");
            bufSql.append(" FTRADECATCODE      VARCHAR2(50) NOT NULL, ");
            bufSql.append(" FCLEARACCOUNT      VARCHAR2(50)     NULL, ");
            bufSql.append(" FDESC              VARCHAR2(100)    NULL, ");
            bufSql.append(" FCHECKSTATE        NUMBER(1)    NOT NULL, ");
            bufSql.append(" FCREATOR           VARCHAR2(20) NOT NULL, ");
            bufSql.append(" FCREATETIME        VARCHAR2(20) NOT NULL, ");
            bufSql.append(" FCHECKUSER         VARCHAR2(20)     NULL, ");
            bufSql.append(" FCHECKTIME         VARCHAR2(20)     NULL ");
            bufSql.append(" ) ");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            bTrans = false;
            conn.setAutoCommit(bTrans);
            bTrans = true;
            bufSql.append(" INSERT INTO TB_" + sPre + "_PARA_BROKERSUBBNY( ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FEXCHANGECODE, ");
            bufSql.append(" FBROKERIDTYPE, ");
            bufSql.append(" FBROKERID, ");
            bufSql.append(" FIFBROKERNAME, ");
            bufSql.append(" FCLEARERIDTYPE, ");
            bufSql.append(" FCLEARERID, ");
            bufSql.append(" FCLEARERNAME, ");
            bufSql.append(" FCLEARERDESC, ");
            bufSql.append(" FBROKERACCOUNT, ");
            bufSql.append(" FPLACEOFSETTLEMENT, ");
            bufSql.append(" FTRADECATCODE, ");
            bufSql.append(" FCLEARACCOUNT, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" ) ");
            bufSql.append(" SELECT  ");
            bufSql.append(" FBROKERCODE, ");
            bufSql.append(" FEXCHANGECODE, ");
            bufSql.append(" FBROKERIDTYPE, ");
            bufSql.append(" FBROKERID, ");
            bufSql.append(" FIFBROKERNAME, ");
            bufSql.append(" FCLEARERIDTYPE, ");
            bufSql.append(" FCLEARERID, ");
            bufSql.append(" FCLEARERNAME, ");
            bufSql.append(" FCLEARERDESC, ");
            bufSql.append(" FBROKERACCOUNT, ");
            bufSql.append(" FPLACEOFSETTLEMENT, ");
            bufSql.append(" (CASE WHEN FTRADECATCODE IS NULL THEN ' ' ELSE FTRADECATCODE END), ");
            bufSql.append(" FCLEARACCOUNT, ");
            bufSql.append(" FDESC, ");
            bufSql.append(" FCHECKSTATE, ");
            bufSql.append(" FCREATOR, ");
            bufSql.append(" FCREATETIME, ");
            bufSql.append(" FCHECKUSER, ");
            bufSql.append(" FCHECKTIME ");
            bufSql.append(" FROM TB_" + sPre + "_PAR_08052008061540000 ");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            dbl.executeSql("ALTER TABLE TB_" + sPre +
                           "_PARA_BROKERSUBBNY ADD CONSTRAINT PK_TB_" + sPre + //加个字段做主键
                           "_PARA_BROKERSUBBNY PRIMARY KEY (FBROKERCODE,FEXCHANGECODE,FTRADECATCODE)");
            //----------------------jc
        } catch (Exception ex) {
            throw new YssException("版本1.0.1.0003sp5 调整表主键出错！", ex);
        } finally {
            bufSql = null; //回收,以清除内存
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
