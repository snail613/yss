package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;
import com.yss.util.YssFun;

public class IConfigInOut
    extends BaseDataSettingBean {
    private String strCfgCode = ""; //配置代码
    private String strCfgName = ""; //配置名称
    private int inOut; //导入导出
    private String strFileType = ""; //文件类型
    private String strFileName = ""; //文件名称
    private String strFilePath = ""; //文件路径
    private String strSqlText = ""; //脚本代码
    private String strDesc = ""; //描述

    private String strCreator = "";
    private String strCreateTime = "";
    public IConfigInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strCfgCode = sRespAry[0];
        this.strCfgName = sRespAry[1];
        this.inOut = Integer.parseInt(sRespAry[2]);
        this.strFileType = sRespAry[3];
        this.strFileName = sRespAry[4];
        this.strFilePath = sRespAry[5];
        this.strSqlText = sRespAry[6];
        this.strDesc = sRespAry[7];
        this.strCreator = pub.getUserCode();
        this.strCreateTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strCfgCode.trim()).append("\t");
        buf.append(this.strCfgName.trim()).append("\t");
        buf.append(this.inOut + "").append("\t");
        buf.append(this.strFileType.trim()).append("\t");
        buf.append(this.strFileName.trim()).append("\t");
        buf.append(this.strFilePath.trim()).append("\t");
        buf.append(this.strSqlText.trim()).append("\t");
        buf.append(this.strDesc.trim());
        return buf.toString();
    }

    public String outIConfig() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_Dao_Configure");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strCfgCode = rs.getString("FCfgCode") + "";
                this.strCfgName = rs.getString("FCfgName") + "";
                this.inOut = rs.getInt("FInOut");
                this.strFileType = rs.getString("FFileType") + "";
                this.strFileName = rs.getString("FFileName") + "";
                this.strFilePath = rs.getString("FFilePath") + "";
                this.strSqlText = rs.getString("FSqlText") + "";
                this.strDesc = rs.getString("FDesc") + "";
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出数据接口错误", e);
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

            str = "delete from " + pub.yssGetTableName("tb_Dao_Configure");

            pstmt1 = conn.prepareStatement(str);
            pstmt1.executeUpdate();

            strSql = "insert into " + pub.yssGetTableName("Tb_Dao_Configure") + "(FCfgCode,FCfgName,FInOut,FFileType,FFileName,FFilePath,FSqlText,FDesc,FCreator,FCreateTime)" +
                " values (?,?,?,?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from " + pub.yssGetTableName("Tb_Dao_Configure") + " where FCfgCode = " +
                    dbl.sqlString(this.strCfgCode) + "and FFileName = " +
                    dbl.sqlString(this.strFileName);

                pstmt2.setString(1, this.strCfgCode);
                pstmt2.setString(2, this.strCfgName);
                pstmt2.setInt(3, this.inOut);
                pstmt2.setString(4, this.strFileType);
                pstmt2.setString(5, this.strFileName);
                pstmt2.setString(6, this.strFilePath);
                pstmt2.setString(7, this.strSqlText);
                pstmt2.setString(8, this.strDesc);
                pstmt2.setString(9, "admin");
                pstmt2.setString(10, this.strCreateTime);
                pstmt2.executeUpdate();

            }

            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入数据接口错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
