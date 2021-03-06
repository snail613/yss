package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;

/**
 * <p>Title: </p>
 * <p>Description: 数据对应关系的导入导出</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @version 1.0
 */

public class ImpRelaInOut
    extends BaseDataSettingBean {
    private String strTabName = ""; //系统表名称
    private String strFieldName = ""; //系统字段名称
    private String strOutDataType = ""; //外部数据类型
    private String strCorField = ""; //外部字段名称

    public ImpRelaInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strTabName = sRespAry[0];
        this.strFieldName = sRespAry[1];
        this.strOutDataType = sRespAry[2];
        this.strCorField = sRespAry[3];
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strTabName.trim()).append("\t");
        buf.append(this.strFieldName.trim()).append("\t");
        buf.append(this.strOutDataType.trim()).append("\t");
        buf.append(this.strCorField.trim());
        return buf.toString();
    }

    public String outImpRela() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from Tb_Dao_ImpRela";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strTabName = rs.getString("FTabName") + "";
                this.strFieldName = rs.getString("FFieldName") + "";
                this.strOutDataType = rs.getString("FOutDataType") + "";
                this.strCorField = rs.getString("FCorField") + "";
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出数据对应关系错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void inImpRela(String squest) throws YssException {
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
            strSql = "update Tb_Dao_ImpRela set FTabName = ?, FFieldName = ?, FOutDataType = ?, FCorField = ?" +
                " where FTabName = ? and FFieldName = ? and FOutDataType = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            strSql = "insert into Tb_Dao_ImpRela(FTabName,FFieldName,FOutDataType,FCorField)" +
                " values (?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Dao_ImpRela where FTabName = " +
                    dbl.sqlString(this.strTabName) + " and FFieldName = " +
                    dbl.sqlString(this.strFieldName) + " and FOutDataType = " +
                    dbl.sqlString(this.strOutDataType);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strTabName);
                    pstmt1.setString(2, this.strFieldName);
                    pstmt1.setString(3, this.strOutDataType);
                    pstmt1.setString(4, this.strCorField);
                    pstmt1.setString(5, this.strTabName);
                    pstmt1.setString(6, this.strFieldName);
                    pstmt1.setString(7, this.strOutDataType);
                    pstmt1.executeUpdate();
                } else {
                    pstmt2.setString(1, this.strTabName);
                    pstmt2.setString(2, this.strFieldName);
                    pstmt2.setString(3, this.strOutDataType);
                    pstmt2.setString(4, this.strCorField);
                    pstmt2.executeUpdate();
                }
                dbl.closeResultSetFinal(rs);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入数据对应关系错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
