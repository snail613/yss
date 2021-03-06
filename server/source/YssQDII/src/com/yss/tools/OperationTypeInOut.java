package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;

/**
 * <p>Title: </p>
 * <p>Description: 操作类型的导入导出</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @version 1.0
 */

public class OperationTypeInOut
    extends BaseDataSettingBean {
    private String strOperTypeCode = ""; //操作类型代码
    private String strOperTypeName = ""; //操作类型名称
    private String strType = ""; //类型

    public OperationTypeInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strOperTypeCode = sRespAry[0];
        this.strOperTypeName = sRespAry[1];
        this.strType = sRespAry[2];
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strOperTypeCode.trim()).append("\t");
        buf.append(this.strOperTypeName.trim()).append("\t");
        buf.append(this.strType.trim());
        return buf.toString();
    }

    public String outOperationType() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from Tb_Sys_OperationType";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strOperTypeCode = rs.getString("FOperTypeCode") + "";
                this.strOperTypeName = rs.getString("FOperTypeName") + "";
                this.strType = rs.getString("FType") + "";
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出操作类型错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void inOperationType(String squest) throws YssException {
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
            strSql = "update Tb_Sys_OperationType set FOperTypeCode = ?, FOperTypeName = ?, FType = ?" +
                " where FOperTypeCode = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            strSql = "insert into Tb_Sys_OperationType(FOperTypeCode,FOperTypeName,FType)" +
                " values (?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Sys_OperationType where FOperTypeCode = " +
                    dbl.sqlString(this.strOperTypeCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strOperTypeCode);
                    pstmt1.setString(2, this.strOperTypeName);
                    pstmt1.setString(3, this.strType);
                    pstmt1.setString(4, this.strOperTypeCode);
                    pstmt1.executeUpdate();

                } else {
                    pstmt2.setString(1, this.strOperTypeCode);
                    pstmt2.setString(2, this.strOperTypeName);
                    pstmt2.setString(3, this.strType);
                    pstmt2.executeUpdate();
                }
                dbl.closeResultSetFinal(rs);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入操作类型错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
