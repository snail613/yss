package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;

/**
 * <p>Title: </p>
 * <p>Description: 菜单的导入导出</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @version 1.0
 */

public class MenuInOut
    extends BaseDataSettingBean {
    private String strMenuCode = ""; //菜单代码
    private String strMenuName = ""; //菜单名称
    private String strParentCode = ""; //父菜单代码
    private String strOrderCOde = ""; //排序编号
    private String strCheck = ""; //是否为Check型菜单
    private String strIconPath = ""; //图标路径
    private String strShortCutKey = ""; //快捷建
    private String strEnabled = ""; //是否可用
    private String strRefInvokeCode = ""; //调用代码
    private String strDesc = ""; //菜单描述

    public MenuInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strMenuCode = sRespAry[0];
        this.strMenuName = sRespAry[1];
        this.strParentCode = sRespAry[2];
        this.strOrderCOde = sRespAry[3];
        this.strCheck = sRespAry[4];
        this.strIconPath = sRespAry[5];
        this.strShortCutKey = sRespAry[6];
        this.strEnabled = sRespAry[7];
        this.strRefInvokeCode = sRespAry[8];
        this.strDesc = sRespAry[9];
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strMenuCode.trim()).append("\t");
        buf.append(this.strMenuName.trim()).append("\t");
        buf.append(this.strParentCode.trim()).append("\t");
        buf.append(this.strOrderCOde.trim()).append("\t");
        buf.append(this.strCheck.trim()).append("\t");
        buf.append(this.strIconPath.trim()).append("\t");
        buf.append(this.strShortCutKey.trim()).append("\t");
        buf.append(this.strEnabled.trim()).append("\t");
        buf.append(this.strRefInvokeCode.trim()).append("\t");
        buf.append(this.strDesc.trim());
        return buf.toString();
    }

    public String outMenu() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from Tb_Fun_Menu";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strMenuCode = rs.getString("FMenuCode") + "";
                this.strMenuName = rs.getString("FMenuName") + "";
                this.strParentCode = rs.getString("FParentCode") + "";
                this.strOrderCOde = rs.getString("FOrderCOde") + "";
                this.strCheck = rs.getString("FCheck") + "";
                this.strIconPath = rs.getString("FIconPath") + "";
                this.strShortCutKey = rs.getString("FShortCutKey") + "";
                this.strEnabled = rs.getString("FEnabled") + "";
                this.strRefInvokeCode = rs.getString("FRefInvokeCode") + "";
                this.strDesc = rs.getString("FDesc") + "";
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出菜单信息错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void inMenu(String squest) throws YssException {
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
            strSql = "update Tb_Fun_Menu set FMenuCode = ?, FMenuName = ?, FParentCode = ?," +
                " FOrderCOde = ?, FCheck = ?, FIconPath = ?, FShortCutKey = ?," +
                " FEnabled = ?, FRefInvokeCode = ?, FDesc = ?" +
                " where FMenuCode = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            strSql = "insert into Tb_Fun_Menu(FMenuCode,FMenuName,FParentCode,FOrderCOde," +
                "FCheck,FIconPath,FShortCutKey,FEnabled,FRefInvokeCode,FDesc)" +
                " values (?,?,?,?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Fun_Menu where FMenuCode = " +
                    dbl.sqlString(this.strMenuCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strMenuCode);
                    pstmt1.setString(2, this.strMenuName);
                    pstmt1.setString(3, this.strParentCode);
                    pstmt1.setString(4, this.strOrderCOde);
                    pstmt1.setInt(5, Integer.parseInt(this.strCheck));
                    pstmt1.setString(6, this.strIconPath);
                    pstmt1.setString(7, this.strShortCutKey);
                    pstmt1.setInt(8, Integer.parseInt(this.strEnabled));
                    pstmt1.setString(9, this.strRefInvokeCode);
                    pstmt1.setString(10, this.strDesc);
                    pstmt1.setString(11, this.strMenuCode);
                    pstmt1.executeUpdate();
                } else {
                    pstmt2.setString(1, this.strMenuCode);
                    pstmt2.setString(2, this.strMenuName);
                    pstmt2.setString(3, this.strParentCode);
                    pstmt2.setString(4, this.strOrderCOde);
                    pstmt2.setInt(5, Integer.parseInt(this.strCheck));
                    pstmt2.setString(6, this.strIconPath);
                    pstmt2.setString(7, this.strShortCutKey);
                    pstmt2.setInt(8, Integer.parseInt(this.strEnabled));
                    pstmt2.setString(9, this.strRefInvokeCode);
                    pstmt2.setString(10, this.strDesc);
                    pstmt2.executeUpdate();
                }
                dbl.closeResultSetFinal(rs);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入菜单信息错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
