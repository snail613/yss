package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.*;
import com.yss.util.YssCons;
import java.sql.*;

public class VocabularyTypeInOut
    extends BaseDataSettingBean {

    private String strVocTypeCode = ""; //词汇类型代码
    private String strVocTypeName = ""; //词汇类型名称
    private String strDesc = ""; //描述
    private String strCheckState = "";
    private String strCreator = "";
    private String strCreateTime = "";
    private String strCheckUser = "";
    private String strCheckTime = "";

    public VocabularyTypeInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strVocTypeCode = sRespAry[0];
        this.strVocTypeName = sRespAry[1];
        this.strDesc = sRespAry[2];
        this.strCheckState = sRespAry[3];
        this.strCreator = sRespAry[4];
        this.strCreateTime = sRespAry[5];
        this.strCheckUser = sRespAry[6];
        this.strCheckTime = sRespAry[7];

    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strVocTypeCode.trim()).append("\t");
        buf.append(this.strVocTypeName.trim()).append("\t");
        buf.append(this.strDesc.trim()).append("\t");
        buf.append(this.strCheckState.trim()).append("\t");
        buf.append(this.strCreator.trim()).append("\t");
        buf.append(this.strCreateTime.trim()).append("\t");
        buf.append(this.strCheckUser.trim()).append("\t");
        buf.append(this.strCheckTime.trim()).append("\t");

        return buf.toString();
    }

    public String outVocabularyType() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = " select * from Tb_Fun_VocabularyType";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strVocTypeCode = rs.getString("FVocTypeCode") + "";
                this.strVocTypeName = rs.getString("FVocTypeName") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.strCheckState = rs.getString("FCheckState") + "";
                this.strCreator = rs.getString("FCreator") + "";
                this.strCreateTime = rs.getString("FCreateTime") + "";
                this.strCheckUser = rs.getString("FCheckUser") + "";
                this.strCheckTime = rs.getString("FCheckTime") + "";

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

    public void inVocabulary(String squest) throws YssException {
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

            strSql = "update Tb_Fun_VocabularyType set FVocTypeCode = ?, FVocTypeName = ?, FDesc = ?,FCheckState= ?, FCreator= ?, FCreateTime= ?, FCheckUser= ?, FCheckTime= ?" +
                " where FVocTypeCode = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            strSql = "insert into Tb_Fun_VocabularyType(FVocTypeCode,FVocTypeName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
                " values (?,?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Fun_VocabularyType where FVocTypeCode = " +
                    dbl.sqlString(this.strVocTypeCode);

                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strVocTypeCode);
                    pstmt1.setString(2, this.strVocTypeName);
                    pstmt1.setString(3, this.strDesc);
                    pstmt1.setString(4, this.strCheckState);
                    pstmt1.setString(5, this.strCreator);
                    pstmt1.setString(6, this.strCreateTime);
                    pstmt1.setString(7, this.strCheckUser);
                    pstmt1.setString(8, this.strCheckTime);
                    pstmt1.setString(9, this.strVocTypeCode);
                    pstmt1.executeUpdate();
                } else {
                    pstmt2.setString(1, this.strVocTypeCode);
                    pstmt2.setString(2, this.strVocTypeName);
                    pstmt2.setString(3, this.strDesc);
                    pstmt2.setString(4, this.strCheckState);
                    pstmt2.setString(5, this.strCreator);
                    pstmt2.setString(6, this.strCreateTime);
                    pstmt2.setString(7, this.strCheckUser);
                    pstmt2.setString(8, this.strCheckTime);

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
