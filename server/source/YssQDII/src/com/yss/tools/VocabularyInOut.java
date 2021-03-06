package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;

public class VocabularyInOut
    extends BaseDataSettingBean {

    private String strVocCode = ""; //词汇代码
    private String strVocName = ""; //词汇名称
    private String strVocTypeCode = ""; //词汇类型
    private String strDesc = ""; //描述
    private String strCheckState = "";
    private String strCreator = "";
    private String strCreateTime = "";
    private String strCheckUser = "";
    private String strCheckTime = "";
    private String strOrderNum = ""; //排序编号

    public VocabularyInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strVocCode = sRespAry[0];
        this.strVocName = sRespAry[1];
        this.strVocTypeCode = sRespAry[2];
        this.strDesc = sRespAry[3];
        this.strOrderNum = sRespAry[4]; //排序编号，前台要对应调整

        this.strCheckState = sRespAry[5];
        this.strCreator = sRespAry[6];
        this.strCreateTime = sRespAry[7];
        this.strCheckUser = sRespAry[8];
        this.strCheckTime = sRespAry[9];
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strVocCode.trim()).append("\t");
        buf.append(this.strVocName.trim()).append("\t");
        buf.append(this.strVocTypeCode.trim()).append("\t");
        buf.append(this.strDesc.trim()).append("\t");
        buf.append(this.strOrderNum.trim()).append("\t"); //排序编号，前台对应调整解析方法

        buf.append(this.strCheckState.trim()).append("\t");
        buf.append(this.strCreator.trim()).append("\t");
        buf.append(this.strCreateTime.trim()).append("\t");
        buf.append(this.strCheckUser.trim()).append("\t");
        buf.append(this.strCheckTime.trim()).append("\t");

        return buf.toString();
    }

    public String outVocabulary() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = " select * from Tb_Fun_Vocabulary";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strVocCode = rs.getString("FVocCode") + "";
                this.strVocName = rs.getString("FVocName") + "";
                this.strVocTypeCode = rs.getString("FVocTypeCode") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.strOrderNum = rs.getString("FOrderNum") + "";

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

            strSql = "update Tb_Fun_Vocabulary set FVocCode = ?, FVocName = ?, FVocTypeCode = ?, FDesc = ?,FCheckState= ?, FCreator= ?, FCreateTime= ?, FCheckUser= ?, FCheckTime= ?,FOrderNum = ?" +
                " where FVocCode = ? and FVocTypeCode = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            strSql = "insert into Tb_Fun_Vocabulary(FVocCode,FVocName,FVocTypeCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FOrderNum)" +
                " values (?,?,?,?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Fun_Vocabulary where FVocCode = " +
                    dbl.sqlString(this.strVocCode) + "and FVocTypeCode = " +
                    dbl.sqlString(this.strVocTypeCode);
                if(this.strVocTypeCode.equalsIgnoreCase("filter_relation")){
                	int tt= 0;
                }
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strVocCode);
                    pstmt1.setString(2, this.strVocName);
                    pstmt1.setString(3, this.strVocTypeCode);
                    pstmt1.setString(4, this.strDesc);

                    pstmt1.setString(5, this.strCheckState);
                    pstmt1.setString(6, this.strCreator);
                    pstmt1.setString(7, this.strCreateTime);
                    pstmt1.setString(8, this.strCheckUser);
                    pstmt1.setString(9, this.strCheckTime);
                    //新增 排序编号
                    pstmt1.setInt(10, "null".equals(this.strOrderNum) ? 0 : Integer.parseInt(strOrderNum));
                    
                    pstmt1.setString(11, this.strVocCode);
                    pstmt1.setString(12, this.strVocTypeCode);


                    pstmt1.executeUpdate();
                } else {
                    pstmt2.setString(1, this.strVocCode);
                    pstmt2.setString(2, this.strVocName);
                    pstmt2.setString(3, this.strVocTypeCode);
                    pstmt2.setString(4, this.strDesc);
                    pstmt2.setString(5, this.strCheckState);
                    pstmt2.setString(6, this.strCreator);
                    pstmt2.setString(7, this.strCreateTime);
                    pstmt2.setString(8, this.strCheckUser);
                    pstmt2.setString(9, this.strCheckTime);

                    //新增 排序编号
                    pstmt2.setInt(10, "null".equals(this.strOrderNum) ? 0 : Integer.parseInt(strOrderNum));

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
