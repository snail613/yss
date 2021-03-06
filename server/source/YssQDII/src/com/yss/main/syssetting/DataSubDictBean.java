package com.yss.main.syssetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.vsub.*;

public class DataSubDictBean
    extends BaseDataSettingBean {
    String FTabName = ""; //表名
    String FFieldName = ""; //字段名
    String FTabDesc = ""; //表描述
    String FFieldDesc = ""; //字段描述
    String FFieldType = ""; //字段类型
    String FDefaultValue = ""; //默认值
    int FKey; // 主键
    String OldFTabName = "";
    String OldFFieldName = "";
    DataSubDictBean filterType;
    public DataSubDictBean() {
    }

    private String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.FTabName.trim()).append("\t");
        buf.append(this.FFieldName.trim()).append("\t");
        buf.append(this.FTabDesc.trim()).append("\t");
        buf.append(this.FFieldDesc.trim()).append("\t");
        buf.append(this.FFieldType.trim()).append("\t");
        buf.append(this.FDefaultValue.trim()).append("\t");
        buf.append(this.FKey).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    private String buildSendStr(String strSql) throws YssException {
        //StringBuffer buf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ResultSet rs = null;
        String sResult = "";
        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.FTabName = rs.getString("FTabName") + "";
                this.FTabDesc = rs.getString("FTableDesc") + "";
                this.FFieldName = rs.getString("FFieldName") + "";
                this.FFieldDesc = rs.getString("FFieldDesc") + "";
                this.FFieldType = rs.getString("FFIELDTYPE ") + "";
                this.FDefaultValue = rs.getString("FDEFAULTVALUE") + "";
                this.FKey = rs.getInt("FKey");

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

    public void protocolParse(String sReq) throws YssException {
        String reqAry[] = null;
        try {
            if (sReq.trim().length() == 0) {
                return;
            }
            reqAry = sReq.split("\t");
            if (reqAry.length >= 7) {
                this.FTabName = reqAry[0];
                this.FFieldName = reqAry[1];
                this.FTabDesc = reqAry[2];
                this.FFieldDesc = reqAry[3];
                this.FFieldType = reqAry[4];
                this.FDefaultValue = reqAry[5];
                this.FKey = com.yss.util.YssFun.toInt(reqAry[6]);
                this.OldFTabName = reqAry[7];
                this.OldFFieldName = reqAry[8];
                // this.strOldTabName = reqAry[5];
                // this.strOldFieldName = reqAry[6];
                super.parseRecLog();

                if (sReq.indexOf("\r\t") >= 0) {
                    if (this.filterType == null) {
                        this.filterType = new DataSubDictBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.protocolParse(sReq.split("\r\t")[1]);
                }

            }
        } catch (Exception e) {
            throw new YssException("解析数据字典出错", e);
        }
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_FUN_DATADICT"), "FTabName,FFieldName",
                               this.FTabName + "," + this.FFieldName,
                               this.OldFTabName + "," + this.OldFFieldName);

    }

    public String addDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //YssDbFun fun = new YssDbFun(pub);//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            PreparedStatement pst = conn.prepareStatement(
                "insert into Tb_Fun_DataDict" +
                "(FTabName,FFieldName,FTableDesc,FFieldDesc,FFieldType,FDefaultValue,FKey)" +
                " values(?,?,?,?,?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pst.setString(1, this.FTabName);
            pst.setString(2, this.FFieldName);
            pst.setString(3, this.FTabDesc);
            pst.setString(4, this.FFieldDesc);
            pst.setString(5, this.FFieldType);
            pst.setString(6, this.FDefaultValue);
            pst.setInt(7, this.FKey);
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

    public String editDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update Tb_Fun_DataDict set FTabName = " + dbl.sqlString(this.FTabName) +
                ",FFieldName = " + dbl.sqlString(this.FFieldName) +
                ",FTableDesc = " + dbl.sqlString(this.FTabDesc) +
                ",FFieldDesc = " + dbl.sqlString(this.FFieldDesc) +
                ",FFieldType = " + dbl.sqlString(this.FFieldType) +
                ",FDefaultValue = " + dbl.sqlString(this.FDefaultValue) +
                ",FKey = " + this.FKey +
                " where FTabName = " + dbl.sqlString(this.OldFTabName) +
                " and FFieldName = " + dbl.sqlString(this.OldFFieldName);
            System.out.println(strSql);
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

    public String delDataDict() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "delete from Tb_Fun_DataDict  where FTabName = " +
                dbl.sqlString(this.FTabName) + " and FFieldName = " +
                dbl.sqlString(this.FFieldName);
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
