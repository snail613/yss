package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;

/**
 * <p>Title: </p>
 * <p>Description: 功能调用的导入导出</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @version 1.0
 */

public class RefInvokeInOut
    extends BaseDataSettingBean {
    private String strRefInvokeCode = ""; //功能调用代码
    private String strRefInvokeName = ""; //功能调用名称
    private String strDLLName = ""; //调用的DLL名称
    private String strClassName = ""; //调用的类名
    private String strMethodName = ""; //调用的方法名称
    private String strParams = ""; //调用的参数列表
    private String strDesc = ""; //调用的描述

    public RefInvokeInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strRefInvokeCode = sRespAry[0];
        this.strRefInvokeName = sRespAry[1];
        this.strDLLName = sRespAry[2];
        this.strClassName = sRespAry[3];
        this.strMethodName = sRespAry[4];
        this.strParams = sRespAry[5];
        this.strDesc = sRespAry[6];
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strRefInvokeCode.trim()).append("\t");
        buf.append(this.strRefInvokeName.trim()).append("\t");
        buf.append(this.strDLLName.trim()).append("\t");
        buf.append(this.strClassName.trim()).append("\t");
        buf.append(this.strMethodName.trim()).append("\t");
        buf.append(this.strParams.trim()).append("\t");
        buf.append(this.strDesc.trim());
        return buf.toString();
    }

    public String outRefInvoke() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from Tb_Fun_RefInvoke";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strRefInvokeCode = rs.getString("FRefInvokeCode") + "";
                this.strRefInvokeName = rs.getString("FRefInvokeName") + "";
                this.strDLLName = rs.getString("FDLLName") + "";
                this.strClassName = rs.getString("FClassName") + "";
                this.strMethodName = rs.getString("FMethodName") + "";
                this.strParams = rs.getString("FParams") + "";
                this.strDesc = rs.getString("FDesc") + "";
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出功能调用信息错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void inRefInvoke(String squest) throws YssException {
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
            strSql = "update Tb_Fun_RefInvoke set FRefInvokeCode = ?, FRefInvokeName = ?, FDLLName = ?," +
                " FClassName = ?, FMethodName = ?, FParams = ?," +
                " FDesc = ? where FRefInvokeCode = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            strSql = "insert into Tb_Fun_RefInvoke(FRefInvokeCode,FRefInvokeName,FDLLName," +
                "FClassName,FMethodName,FParams,FDesc)" +
                " values (?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Fun_RefInvoke where FRefInvokeCode = " +
                    dbl.sqlString(this.strRefInvokeCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strRefInvokeCode);
                    pstmt1.setString(2, this.strRefInvokeName);
                    pstmt1.setString(3, this.strDLLName);
                    pstmt1.setString(4, this.strClassName);
                    pstmt1.setString(5, this.strMethodName);
                    pstmt1.setString(6, this.strParams);
                    pstmt1.setString(7, this.strDesc);
                    pstmt1.setString(8, this.strRefInvokeCode);
                    pstmt1.executeUpdate();
                } else {
                    pstmt2.setString(1, this.strRefInvokeCode);
                    pstmt2.setString(2, this.strRefInvokeName);
                    pstmt2.setString(3, this.strDLLName);
                    pstmt2.setString(4, this.strClassName);
                    pstmt2.setString(5, this.strMethodName);
                    pstmt2.setString(6, this.strParams);
                    pstmt2.setString(7, this.strDesc);
                    pstmt2.executeUpdate();
                }
                dbl.closeResultSetFinal(rs);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入功能调用信息错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
