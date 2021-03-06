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
public class Ora1010008
    extends BaseDbUpdate {
    public Ora1010008() {
    }

    //调整数据
    public void adjustTableData(String sPre) throws YssException {
        boolean bTrans = false;
        String strPKName = "";
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //------------将证券应收应付中分红的汇兑损益数据逻辑删除。 MS00050
            bufSql.append("update ");
            bufSql.append(pub.yssGetTableName("TB_Data_SecRecPay"));
            bufSql.append(" set FCheckState = 2 ");
            bufSql.append(" where FSubTsfTypeCode in ('9906DV','06DV')");
            dbl.executeSql(bufSql.toString());
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            bufSql.delete(0, bufSql.length());
            //----------------------------------------------------------
        } catch (Exception e) {
            throw new YssException("版本Oracle1010008调整数据出错！");
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * author : 王晓光
     * date   : 20081208
     * bugid  : MS00060
     * fdesc  : 在回购品种信息设置（Tb_XXX_Para_Purchase）中新增一字段FCircuMarket——“流通市场”。并将其设置为主键
     * @param sPre String
     * @throws YssException
     */
    public void adjustFieldPrecision(String sPre) throws YssException {
        //需求变更 将以下代码屏蔽 2008-12-10 蒋锦
//      boolean bTrans = false;
//      String strPKName = "";
//      StringBuffer bufSql = new StringBuffer(5000);
//      Connection conn = dbl.loadConnection();
//      try {
//         //表中不存在FCircuMarket字段，才执行更新
//         if (existsTabColumn_Ora("TB_" + sPre + "_Para_Purchase", "FCircuMarket")) {
//            //设置事物参数状态 -- 事物回滚
//            bTrans = true;
//            //设置事物不自动提交
//            conn.setAutoCommit(false);
//            //获取主键约束
//            strPKName = this.getIsNullPKByTableName_Ora("TB_" + sPre + "_Para_Purchase");
//            if (strPKName != null && !strPKName.trim().equals("")) {
//               //删除约束
//               dbl.executeSql("ALTER TABLE TB_" + sPre + "_Para_Purchase DROP CONSTRAINT " +
//                              strPKName);
//               //删除索引
//               deleteIndex(strPKName);
//            }
//            //如果备份表存在 删除备份表
//            if (dbl.yssTableExist("TB_" + sPre + "_PAR_12082008101628000 ")) {
//               this.dropTableByTableName("TB_" + sPre + "_PAR_12082008101628000");
//            }
//            //将原表更改为备份表
//            dbl.executeSql(
//                  "ALTER TABLE TB_" + sPre + "_Para_Purchase RENAME TO TB_" + sPre + "_PAR_12082008101628000");
//            //清空bufSql
//            bufSql.delete(0, bufSql.length());
//            bufSql.append("CREATE TABLE " +
//                          pub.yssGetTableName("TB_Para_Purchase"));
//            bufSql.append(" ( ");
//            bufSql.append("FSECURITYCODE VARCHAR2(20)  NOT NULL,");
//            bufSql.append("FDEPDURCODE   VARCHAR2(20)  NOT NULL,");
//            bufSql.append("FPERIODCODE   VARCHAR2(20)  NOT NULL,");
//            bufSql.append("FPURCHASETYPE VARCHAR2(20)  NOT NULL,");
//            bufSql.append("FPURCHASERATE NUMBER(18,12)     NULL,");
//            bufSql.append("FCIRCUMARKET  VARCHAR2(20)  NOT NULL,");
//            bufSql.append("FDESC         VARCHAR2(100)     NULL,");
//            bufSql.append("FCHECKSTATE   NUMBER(1)     NOT NULL,");
//            bufSql.append("FCREATOR      VARCHAR2(20)  NOT NULL,");
//            bufSql.append("FCREATETIME   VARCHAR2(20)  NOT NULL,");
//            bufSql.append("FCHECKUSER    VARCHAR2(20)      NULL,");
//            bufSql.append("FCHECKTIME    VARCHAR2(20)      NULL");
//            bufSql.append(" )");
//            dbl.executeSql(bufSql.toString());
//            bufSql.delete(0, bufSql.length());
//            //从临时表中导数据到新创建的表中
//            bufSql.append("INSERT INTO " +
//                          pub.yssGetTableName("TB_Para_Purchase") + "( ");
//            bufSql.append("FSECURITYCODE,");
//            bufSql.append("FDEPDURCODE,");
//            bufSql.append("FPERIODCODE,");
//            bufSql.append("FPURCHASETYPE,");
//            bufSql.append("FPURCHASERATE,");
//            bufSql.append("FCIRCUMARKET,");
//            bufSql.append("FDESC,");
//            bufSql.append("FCHECKSTATE,");
//            bufSql.append("FCREATOR,");
//            bufSql.append("FCREATETIME,");
//            bufSql.append("FCHECKUSER,");
//            bufSql.append("FCHECKTIME )");
//            bufSql.append(" SELECT ");
//            bufSql.append(" FSECURITYCODE,");
//            bufSql.append("FDEPDURCODE,");
//            bufSql.append("FPERIODCODE,");
//            bufSql.append("FPURCHASETYPE,");
//            bufSql.append("FPURCHASERATE,");
//            bufSql.append("' ',");
//            bufSql.append("FDESC,");
//            bufSql.append("FCHECKSTATE,");
//            bufSql.append("FCREATOR,");
//            bufSql.append("FCREATETIME,");
//            bufSql.append("FCHECKUSER,");
//            bufSql.append("FCHECKTIME");
//            bufSql.append(" FROM TB_"+sPre+"_PAR_12082008101628000 ");
//            conn.setAutoCommit(bTrans);
//            bTrans = true;
//            dbl.executeSql(bufSql.toString());
//            conn.commit();
//            conn.setAutoCommit(bTrans);
//            bTrans = false;
//            //设定表的主键
//            dbl.executeSql("ALTER TABLE " +
//                           pub.yssGetTableName("TB_Para_Purchase") +
//                           " ADD CONSTRAINT PK_TB_" + sPre +
//                           "_PARA_FORWARD_1 PRIMARY KEY (FSECURITYCODE,FCIRCUMARKET)");
//
//         }
//      }catch (Exception ex) {
//         throw new YssException("版本Oracle1010008更新表字段出错", ex);
//      }
//      finally {
//         bufSql = null;
//         dbl.endTransFinal(conn, bTrans);
//      }

    }
}
