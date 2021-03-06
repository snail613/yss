package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;
import com.yss.util.*;

public class IConfigRelaInOut
    extends BaseDataSettingBean {

    private String strCfgCode = "";
    private String strTabName = "";
    private String strFieldName = "";
    private String strOutField = "";
    private String strDesc = "";
    private String strCreator = "";
    private String strCreateTime = "";
    public IConfigRelaInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strCfgCode = sRespAry[0];
        this.strTabName = sRespAry[1];
        this.strFieldName = sRespAry[2];
        this.strOutField = sRespAry[3];
        this.strDesc = sRespAry[4];
        this.strCreator = pub.getUserCode();
        this.strCreateTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strCfgCode.trim()).append("\t");
        buf.append(this.strTabName.trim()).append("\t");
        buf.append(this.strFieldName.trim()).append("\t");
        buf.append(this.strOutField.trim()).append("\t");
        buf.append(this.strDesc.trim());
        return buf.toString();
    }

    public String outIConfigRela() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Dao_Configure_Rela");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strCfgCode = rs.getString("FCfgCode") + "";
                this.strTabName = rs.getString("FTabName") + "";
                this.strFieldName = rs.getString("FFieldName") + "";
                this.strOutField = rs.getString("FOutField") + "";
                this.strDesc = rs.getString("FDesc") + "";
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出数据接口对应关系错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void inIConfig(String squest) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String str = "";
        Connection conn = dbl.loadConnection();
        String[] sReqAry = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        boolean bTrans = false;
        try {
            sReqAry = squest.split(YssCons.YSS_LINESPLITMARK);
            conn.setAutoCommit(false);
            bTrans = true;
            str = "delete from " + pub.yssGetTableName("Tb_Dao_Configure_Rela");
            pstmt1 = conn.prepareStatement(str);
            pstmt1.executeUpdate();

            strSql = "insert into " + pub.yssGetTableName("Tb_Dao_Configure_Rela") + "(FCfgCode,FTabName,FFieldName,FOutField,FDesc,FCreator,FCreateTime)" +
                " values (?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from " + pub.yssGetTableName("Tb_Dao_Configure_Rela") + " where FCfgCode = " +
                    dbl.sqlString(this.strCfgCode) + " and FTabName= " +
                    dbl.sqlString(this.strTabName);

                pstmt2.setString(1, this.strCfgCode);
                pstmt2.setString(2, this.strTabName);
                pstmt2.setString(3, this.strFieldName);
                pstmt2.setString(4, this.strOutField);
                pstmt2.setString(5, this.strDesc);
                pstmt2.setString(6, "admin");
                pstmt2.setString(7, this.strCreateTime);
                pstmt2.executeUpdate();

            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入数据接口对应关系错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }

}
