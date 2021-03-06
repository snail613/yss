package com.yss.main.syssetting;

import java.sql.*;
import java.util.*;
import java.sql.ResultSet;
import com.yss.main.dao.*;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.*;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.vsub.YssDbFun;

/**
 * <p>Title:数据表字典设置 </p>
 * <p>Description:数据表字典字段表bean </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company:ysstech.com </p>
 * @author pengjinggang
 * @version 1.0
 */
public class TableSubDictBean
    extends BaseDataSettingBean {
    //数据表字段
    private String sFTableCode = ""; //数据表代码
    private String sFTableName = ""; //数据表名称
    private String sFTableDesc = ""; //数据表描述
    private String sFFieldCode = ""; //字段代码
    private String sFFieldName = ""; //字段名称
    private String sFFieldType = ""; //字段类型
    private String sFFieldDesc = ""; //字段描述
    public String OldFTabName = "";
    public String OldFieldName = "";
    public TableSubDictBean filterType;

    public TableSubDictBean() {
    }
    //构造实体类
    private String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sFTableCode.trim()).append("\t");
        buf.append(this.sFTableName.trim()).append("\t");
        buf.append(this.sFTableDesc.trim()).append("\t");
        buf.append(this.sFFieldCode.trim()).append("\t");
        buf.append(this.sFFieldName.trim()).append("\t");
        buf.append(this.sFFieldType.trim()).append("\t");
        buf.append(this.sFFieldDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    //构造实体发送串
    private String buildSendStr(String strSql) throws YssException {
        //StringBuffer buf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ResultSet rs = null;
        String sResult = "";
        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.sFTableCode = rs.getString("sFTableCode") + "";
                this.sFTableDesc = rs.getString("FTableDesc") + "";
                this.sFTableName = rs.getString("sFTableName") + "";
                this.sFFieldCode = rs.getString("sFFieldCode") + "";
                this.sFFieldName = rs.getString("sFFieldName ") + "";
                this.sFFieldType = rs.getString("sFFieldType") + "";
                this.sFFieldDesc = rs.getString("sFFieldDesc");

                sResult += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("访问数据字典表出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //解析实体类
    public void protocolParse(String sReq) throws YssException {
        String reqAry[] = null;
        try {
            if (sReq.trim().length() == 0) {
                return;
            }
            reqAry = sReq.split("\t");
            if (reqAry.length >= 7) {
                this.sFTableCode = reqAry[0];
                this.sFTableName = reqAry[1];
                this.sFTableDesc = reqAry[2];
                this.sFFieldCode = reqAry[3];
                this.sFFieldName = reqAry[4];
                this.sFFieldType = reqAry[5];
                this.sFFieldDesc = reqAry[6];
                this.OldFTabName = reqAry[7];
                this.OldFieldName = reqAry[8];
                super.parseRecLog();

                if (sReq.indexOf("\r\t") >= 0) {
                    if (this.filterType == null) {
                        this.filterType = new TableSubDictBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.protocolParse(sReq.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析数据字典出错", e);
        }
    }

    //检查输入是否正确
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_FUN_DATADICT"), "sFTableCode,sFTableName",
                               this.sFTableCode + "," + this.sFTableName,
                               this.OldFTabName + "," + this.OldFieldName);

    }

    //添加数据字典
    public String addDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //YssDbFun fun = new YssDbFun(pub);//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            PreparedStatement pst = conn.prepareStatement(
                "insert into Tb_Fun_DataDict" +
                "(FTableCode,FTableName,FTableDesc,FFieldCode,FFieldName,FFieldType,FFieldDesc)" +
                " values(?,?,?,?,?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pst.setString(1, this.sFTableCode);
            pst.setString(2, this.sFTableName);
            pst.setString(3, this.sFTableDesc);
            pst.setString(4, this.sFFieldCode);
            pst.setString(5, this.sFFieldName);
            pst.setString(6, this.sFFieldType);
            pst.setString(7, this.sFFieldDesc);
            pst.executeUpdate();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("增加数据字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //编辑数据字典
    public String editDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update Tb_Fun_DataDict set sFTableCode = " + dbl.sqlString(this.sFTableCode) +
                ",FTableName = " + dbl.sqlString(this.sFTableName) +
                ",FTableDesc = " + dbl.sqlString(this.sFTableDesc) +
                ",FFieldCode = " + dbl.sqlString(this.sFFieldCode) +
                ",FFieldName = " + dbl.sqlString(this.sFFieldName) +
                ",FFieldType = " + dbl.sqlString(this.sFFieldType) +
                ",FFieldDesc = " + this.sFFieldDesc +
                " where FTableCode = " + dbl.sqlString(this.OldFTabName) +
                " and sFTableName = " + dbl.sqlString(this.OldFieldName);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改数据字典设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //删除数据字典
    public String delDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "delete from Tb_Fun_DataDict  where sFTableCode = " +
                dbl.sqlString(this.sFTableCode) + " and sFTableName = " +
                dbl.sqlString(this.sFTableName);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = true;
            conn.setAutoCommit(true);
            return "true";
        } catch (Exception e) {
            throw new YssException("删除数据字典设置出错", e);
        }
    }

}
