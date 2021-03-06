package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;

/**
 * <p>Title: </p>
 * <p>Description:数据字典的导入导出</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @version 1.0
 */

public class DataDictInOut
    extends BaseDataSettingBean {
    private String strTabName = ""; //系统表名称
    private String strFieldName = ""; //系统字段名称
    private String strTableDesc = ""; //系统表描述
    private String strFieldDesc = ""; //系统字段描述
    private String strKey = ""; //是否为主键
    private int iTabType = 0; //表类型
    private String strFieldPre = ""; //字段精度
    private String strFieldType = ""; //字段类型
    private int iIsNull = 0; //是否可为空
    private int iCheckState = 0; //审核状态
    private String strCreator = ""; //创建人
    private String strCreateTime = ""; //创建实践

    public DataDictInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strTabName = sRespAry[0];
        this.strFieldName = sRespAry[1];
        this.strTableDesc = sRespAry[2];
        this.strFieldDesc = sRespAry[3];
        this.strKey = sRespAry[4];
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strTabName.trim()).append("\t");
        buf.append(this.strFieldName.trim()).append("\t");
        buf.append(this.strTableDesc.trim()).append("\t");
        buf.append(this.strFieldDesc.trim()).append("\t");
        buf.append(this.strKey.trim());
        return buf.toString();
    }

    public String outDataDict() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from Tb_Fun_DataDict";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strTabName = rs.getString("FTabName") + "";
                this.strFieldName = rs.getString("FFieldName") + "";
                this.strTableDesc = rs.getString("FTableDesc") + "";
                this.strFieldDesc = rs.getString("FFieldDesc") + "";
                this.strKey = rs.getString("FKey") + "";
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出数据字典错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void inDataDict(String squest) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        Connection conn = dbl.loadConnection();
        String[] sReqAry = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        boolean bTrans = false;
        try {
            sReqAry = squest.split(YssCons.YSS_LINESPLITMARK);
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update Tb_Fun_DataDict set FTabName = ?, FFieldName = ?, FTableDesc = ?, FFieldDesc = ?, FKey = ?" +
                " where FTabName = ? and FFieldName = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            strSql = "insert into Tb_Fun_DataDict(FTabName,FFieldName,FTableType,FFieldPre,FTableDesc,FFieldDesc,FFieldType,FIsNull," +
                "FKey,FCheckState,FCreator,FCreateTime)" +
                " values (?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Fun_DataDict where FTabName = " +
                    dbl.sqlString(this.strTabName) + " and FFieldName = " +
                    dbl.sqlString(this.strFieldName);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strTabName);
                    pstmt1.setString(2, this.strFieldName);
                    pstmt1.setString(3, this.strTableDesc);
                    pstmt1.setString(4, this.strFieldDesc);
                    pstmt1.setString(5, this.strKey);
                    pstmt1.setString(6, this.strTabName);
                    pstmt1.setString(7, this.strFieldName);
                    pstmt1.executeUpdate();
                } else {
                    pstmt2.setString(1, this.strTabName);
                    pstmt2.setString(2, this.strFieldName);
                    pstmt2.setInt(3, this.iTabType);
                    pstmt2.setString(4, this.strFieldPre == "" ? " " : this.strFieldPre);
                    pstmt2.setString(5, this.strTableDesc);
                    pstmt2.setString(6, this.strFieldDesc);
                    pstmt2.setString(7, this.strFieldType == "" ? " " : this.strFieldType);
                    pstmt2.setInt(8, this.iIsNull);
                    pstmt2.setString(9, this.strKey);
                    pstmt2.setInt(10, this.iCheckState);
                    pstmt2.setString(11, this.strCreator == "" ? " " : this.strCreator);
                    pstmt2.setString(12, this.strCreateTime == "" ? " " : this.strCreateTime);
                    pstmt2.executeUpdate();
                }
                dbl.closeResultSetFinal(rs);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入数据字典错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
