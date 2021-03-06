package com.yss.tools;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import java.sql.*;
import com.yss.dbupdate.*; //MS00010 add by songjie 2009-05-08

/**
 * <p>Title: </p>
 * <p>Description: 菜单条的导入导出</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @version 1.0
 */

public class MenuBarInOut
    extends BaseDataSettingBean {
    private String strMenuBarCode = ""; //菜单条代码
    private String strMenuBarName = ""; //菜单条名称
    private String strBarGroupCode = ""; //菜单条组代码
    private String strIconPath = ""; //图标路径
    private String strEnabled = ""; //是否可用
    private String strOrderCOde = ""; //排序编号
    private String strRefInvokeCode = ""; //调用代码
    private String strDesc = ""; //菜单条描述
    //----------by caocheng 2009.03.27 MS00001 QDV 4.1 ----------//
    private String FoperTypecode = ""; //操作类型
    private String FrightType = ""; //权限类型
    private String strInnerGroup = "";
//---------------------------------------------------//
    //add by songjie 2013.01.24 添加 公司名称
    private String strCompanyName = "";//公司名称
    public MenuBarInOut() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRespAry = sRowStr.split("\t");
        this.strMenuBarCode = sRespAry[0];
        this.strMenuBarName = sRespAry[1];
        this.strBarGroupCode = sRespAry[2];
        this.strIconPath = sRespAry[3];
        this.strEnabled = sRespAry[4];
        this.strOrderCOde = sRespAry[5];
        this.strRefInvokeCode = sRespAry[6];
        this.strDesc = sRespAry[7];
        //---- by caocheng 2009.03.27 MS00001 QDV4.1--//
        this.FoperTypecode = sRespAry[8];
        this.FrightType = sRespAry[9];
        //---------------------------------//
        this.strInnerGroup = sRespAry[10];//组内分组 add by yeshenghong 20121217
        this.strCompanyName = sRespAry[11];//add by songjie 201301.24 添加公司名称
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strMenuBarCode.trim()).append("\t");
        buf.append(this.strMenuBarName.trim()).append("\t");
        buf.append(this.strBarGroupCode.trim()).append("\t");
        buf.append(this.strIconPath.trim()).append("\t");
        buf.append(this.strEnabled.trim()).append("\t");
        buf.append(this.strOrderCOde.trim()).append("\t");
        buf.append(this.strRefInvokeCode.trim()).append("\t");
        buf.append(this.strDesc.trim()).append("\t");
        //----- by caocheng 2009.03.27 MS00001 QDV4.1------//
        buf.append(this.FoperTypecode.trim()).append("\t");
        buf.append(this.FrightType.trim()).append("\t");
        buf.append(this.strInnerGroup.trim()).append("\t");//add by yeshenghong story2917 20121217
        //---------------------------------------//
        buf.append(this.strCompanyName.trim());//add by songjie 2013.01.24 添加 公司名称
        return buf.toString();
    }

    public String outMenuBar() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from Tb_Fun_MenuBar";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strMenuBarCode = rs.getString("FBarCode") + "";
                this.strMenuBarName = rs.getString("FBarName") + "";
                this.strBarGroupCode = rs.getString("FBarGroupCode") + "";
                this.strIconPath = rs.getString("FIconPath") + "";
                this.strEnabled = rs.getString("FEnabled") + "";
                this.strOrderCOde = rs.getString("FOrderCOde") + "";
                this.strRefInvokeCode = rs.getString("FRefInvokeCode") + "";
                this.strDesc = rs.getString("FDesc") + "";
                //---- by caocheng 2009.03.27 MS00001 QDV4.1-------//
                this.FoperTypecode = rs.getString("FOperTypeCode") + "";
                this.FrightType = rs.getString("FRightType") + "";
                this.strInnerGroup = rs.getString("FInnerGroup") + "";
                //-----------------------------------------------//
                //add by songjie 2013.01.24 添加 公司名称
                this.strCompanyName = rs.getString("FCompanyName") + "";
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出菜单条信息错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //add by yeshenghong to complete story2917 20121115
    public String outNavMenuBar() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select * from Tb_Fun_NavMenuBar";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strMenuBarCode = rs.getString("FBarCode") + "";
                this.strMenuBarName = rs.getString("FBarName") + "";
                this.strBarGroupCode = rs.getString("FBarGroupCode") + "";
                this.strIconPath = rs.getString("FIconPath") + "";
                this.strEnabled = rs.getString("FEnabled") + "";
                this.strOrderCOde = rs.getString("FOrderCOde") + "";
                this.strRefInvokeCode = rs.getString("FRefInvokeCode") + "";
                this.strDesc = rs.getString("FDesc") + "";
                //---- by caocheng 2009.03.27 MS00001 QDV4.1-------//
                this.FoperTypecode = rs.getString("FOperTypeCode") + "";
                this.FrightType = rs.getString("FRightType") + "";
                //-----------------------------------------------//
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            sResult = buf.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出导航菜单条信息错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * MS00010 add by songjie 2009-05-08
     * QDV4.1赢时胜上海2009年2月1日08_A
     * 用于判断是否有指定的字段，若没有则添加
     * @throws YssException
     */
    public void judgeColumns() throws YssException, SQLException {
        String sqlStr = ""; //用于储存sql语句
        boolean exist1 = false; //用于判断字段是否存在
        boolean exist2 = false; //用于判断字段是否存在
        boolean exist3 = false; //add by songjie 2013.01.24 用于判断字段 FCompanyName 是否存在
        boolean exist4 = false; //add by songjie 2013.01.24 用于判断字段 FINNERGROUP 是否存在
        try {
            if (dbl.getDBType() == YssCons.DB_ORA) {
                exist1 = this.existsTabColumn_Ora("Tb_Fun_Menubar", "FRightType"); //判断oracle库中表Tb_Fun_Menubar的FRightType是否存在
                exist2 = this.existsTabColumn_Ora("Tb_Fun_Menubar", "FOperTypeCode"); //判断oracle库中表Tb_Fun_Menubar的FOperTypeCode是否存在
                //add by songjie 2013.01.24 判断oracle库中表Tb_Fun_Menubar的字段 FCompanyName 是否存在
                exist3 = this.existsTabColumn_Ora("Tb_Fun_Menubar", "FCompanyName");
                //add by songjie 2013.01.24 判断oracle库中表Tb_Fun_Menubar的字段 FINNERGROUP 是否存在
                exist4 = this.existsTabColumn_Ora("Tb_Fun_Menubar", "FINNERGROUP");
            }

            if (dbl.getDBType() == YssCons.DB_DB2) {
                exist1 = this.existsTabColumn_DB2("Tb_Fun_Menubar", "FRightType"); //判断DB2库中表Tb_Fun_Menubar的FRightType是否存在
                exist2 = this.existsTabColumn_DB2("Tb_Fun_Menubar", "FOperTypeCode"); //判断DB2库中表Tb_Fun_Menubar的FOperTypeCode是否存在
                //add by songjie 2013.01.24 判断DB2库中表Tb_Fun_Menubar的字段 FCompanyName 是否存在
                exist3 = this.existsTabColumn_DB2("Tb_Fun_Menubar", "FCompanyName");
                //add by songjie 2013.01.24 判断DB2库中表Tb_Fun_Menubar的字段 FINNERGROUP 是否存在
                exist4 = this.existsTabColumn_DB2("Tb_Fun_Menubar", "FINNERGROUP");
            }

            if (dbl.getDBType() == YssCons.DB_ORA && exist1 && exist2) { //若不存在
                sqlStr = "alter table Tb_Fun_Menubar add FRightType VARCHAR2(20) NULL"; //则给表Tb_Fun_Menubar添加字段FRightType
                dbl.executeSql(sqlStr);

                sqlStr = "alter table tb_fun_menubar add FOperTypeCode VARCHAR2(400) NULL"; //则给表Tb_Fun_Menubar添加字段FOperTypeCode
                dbl.executeSql(sqlStr);
            }

            if (dbl.getDBType() == YssCons.DB_DB2 && exist1 && exist2) { //若TB_FUN_MENUBAR中的FRightType，FOperTypeCode字段不存在
                sqlStr = " alter table Tb_Fun_Menubar add FRightType VARCHAR(20) "; //添加FRightType字段
                dbl.executeSql(sqlStr);

                sqlStr = " alter table tb_fun_menubar add FOperTypeCode VARCHAR(400) "; //添加FOperTypeCode字段
                dbl.executeSql(sqlStr);
            }
            //--- add by songjie 2013.01.24 添加 公司名称字段 start---//
            if(dbl.getDBType() == YssCons.DB_ORA && exist3){
                sqlStr = "alter table Tb_Fun_Menubar add FCompanyName VARCHAR2(1000) NULL"; //则给表Tb_Fun_Menubar添加字段 FCompanyName
                dbl.executeSql(sqlStr);
            }
            if(dbl.getDBType() == YssCons.DB_DB2 && exist3){
                sqlStr = "alter table Tb_Fun_Menubar add FCompanyName VARCHAR(1000) NULL"; //则给表Tb_Fun_Menubar添加字段 FCompanyName
                dbl.executeSql(sqlStr);
            }
            if(dbl.getDBType() == YssCons.DB_ORA && exist4){
                sqlStr = "alter table Tb_Fun_Menubar add FINNERGROUP VARCHAR2(20) NULL"; //则给表Tb_Fun_Menubar添加字段 FINNERGROUP
                dbl.executeSql(sqlStr);
            }
            if(dbl.getDBType() == YssCons.DB_DB2 && exist4){
                sqlStr = "alter table Tb_Fun_Menubar add FINNERGROUP VARCHAR(20) NULL"; //则给表Tb_Fun_Menubar添加字段 FINNERGROUP
                dbl.executeSql(sqlStr);
            }
            //--- add by songjie 2013.01.24 添加 公司名称字段 end---//
        } catch (Exception e) {
            throw new YssException("添加表 tb_fun_menubar 字段出错", e);
        }
    }

    /**
     * MS00010 add by songjie 2009-05-08
     * QDV4.1赢时胜上海2009年2月1日08_A
     * 用于判断oracle数据库中指定表的指定字段是否存在
     * @param sTabName String
     * @param columns String
     * @return boolean
     * @throws YssException
     */
    private boolean existsTabColumn_Ora(String sTabName, String columns) throws YssException {
        boolean existCol = true;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from user_col_comments where upper(table_name)=upper(" + dbl.sqlString(sTabName) + ")" +
                " and upper(Column_Name) in (" + operSql.sqlCodes(columns.toUpperCase()) + ")";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                existCol = false;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询Oracle表" + sTabName + "的字段" + columns + "时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /***
     * MS00010 add by songjie 2009-05-08
     * QDV4.1赢时胜上海2009年2月1日08_A
     * 用于判断DB2数据库中指定表的指定字段是否存在
     * 根据表来判断表的字段是否存在 true:不存在 false:存在
     * sTabName :表名
     * cloumsn : 要查询的表字段 集,多个字段中间用"," 分隔
     */
    protected boolean existsTabColumn_DB2(String sTabName, String columns) throws YssException {
        boolean existCol = true;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select * from SYSIBM.COLUMNS_S where upper(table_name)=upper(" + dbl.sqlString(sTabName) + ")" +
                " and upper(Column_Name) in (" + operSql.sqlCodes(columns.toUpperCase()) + ")";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                existCol = false;
            }
            return existCol;
        } catch (Exception e) {
            throw new YssException("查询DB2表" + sTabName + "的字段" + columns + "时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void inMenuBar(String squest) throws YssException {
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
            judgeColumns(); //MS00010 add by songjie 2009-05-08 用于添加表字段
            strSql =
                "update Tb_Fun_MenuBar set FBarCode = ?, FBarName = ?, FBarGroupCode = ?," +
                " FIconPath = ?, FEnabled = ?, FOrderCOde = ?," +
                //edit by songjie 2013.01.24 添加 FCompanyName 
                " FRefInvokeCode = ?, FDesc = ?,FOperTypeCode=?,FRightType=?,FInnerGroup = ?,FCompanyName = ? where FBarCode = ?";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY,
                                           ResultSet.CONCUR_UPDATABLE);

            strSql = "insert into Tb_Fun_MenuBar(FBarCode,FBarName,FBarGroupCode," +
            	//edit by songjie 2013.01.24 添加 FCompanyName
                "FIconPath,FEnabled,FOrderCOde,FRefInvokeCode,FDesc,FOperTypeCode,FRightType,FInnerGroup, FCompanyName)" +
                //edit by songjie 2013.01.24 添加 FCompanyName 对应的 ?
                " values (?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY,
                                           ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = "select * from Tb_Fun_MenuBar where FBarCode = " +
                    dbl.sqlString(this.strMenuBarCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strMenuBarCode);
                    pstmt1.setString(2, this.strMenuBarName);
                    pstmt1.setString(3, this.strBarGroupCode);
                    pstmt1.setString(4, this.strIconPath);
                    pstmt1.setInt(5, Integer.parseInt(this.strEnabled));
                    pstmt1.setString(6, this.strOrderCOde);
                    pstmt1.setString(7, this.strRefInvokeCode);
                    pstmt1.setString(8, this.strDesc);
                    //-- by caocheng 2009.03.27 MS0001 QDV4.1--//
                    pstmt1.setString(9, this.FoperTypecode);
                    pstmt1.setString(10, this.FrightType);
                    //--------------------------------------//
                    pstmt1.setString(11, this.strInnerGroup);
                    //add by songjie 2013.01.24 添加 公司名称
                    pstmt1.setString(12, this.strCompanyName.equals("null") ? "" : this.strCompanyName);
                    pstmt1.setString(13, this.strMenuBarCode);
                    
                    pstmt1.executeUpdate();

                } else {
                    pstmt2.setString(1, this.strMenuBarCode);
                    pstmt2.setString(2, this.strMenuBarName);
                    pstmt2.setString(3, this.strBarGroupCode);
                    pstmt2.setString(4, this.strIconPath);
                    pstmt2.setInt(5, Integer.parseInt(this.strEnabled));
                    pstmt2.setString(6, this.strOrderCOde);
                    pstmt2.setString(7, this.strRefInvokeCode);
                    pstmt2.setString(8, this.strDesc);
                    //-- by caocheng 2009.03.27 MS0001 QDV4.1--//
                    pstmt2.setString(9, this.FoperTypecode);
                    pstmt2.setString(10, this.FrightType);
                    pstmt2.setString(11, this.strInnerGroup);
                    //-----------------------------------//
                    //add by songjie 2013.01.24 添加 公司名称
                    pstmt2.setString(12, this.strCompanyName.equals("null") ? "" : this.strCompanyName);
                    pstmt2.executeUpdate();
                }
                dbl.closeResultSetFinal(rs);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入菜单条信息错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }
    //add by yeshenghong to complete story2917 20121115  this method to update navigator menubar data
    public void inNavMenuBar(String squest) throws YssException {
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
            judgeColumns(); //MS00010 add by songjie 2009-05-08 用于添加表字段
            strSql =
                "update Tb_Fun_NavMenuBar set FBarCode = ?, FBarName = ?, FBarGroupCode = ?," +
                " FIconPath = ?, FEnabled = ?, FOrderCOde = ?," +
                " FRefInvokeCode = ?, FDesc = ?,FOperTypeCode=?,FRightType=? where FBarCode = ? and FBarGroupCode = ? ";
            pstmt1 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY,
                                           ResultSet.CONCUR_UPDATABLE);

            strSql = "insert into Tb_Fun_NavMenuBar(FBarCode,FBarName,FBarGroupCode," +
                "FIconPath,FEnabled,FOrderCOde,FRefInvokeCode,FDesc,FOperTypeCode,FRightType)" +
                " values (?,?,?,?,?,?,?,?,?,?)";
            pstmt2 = conn.prepareStatement(strSql, ResultSet.TYPE_FORWARD_ONLY,
                                           ResultSet.CONCUR_UPDATABLE);
            for (int i = 0; i < sReqAry.length; i++) {
                this.parseRowStr(sReqAry[i]);
                strSql = " select * from Tb_Fun_NavMenuBar where FBarCode = " +
                    dbl.sqlString(this.strMenuBarCode) + " and FBarGroupCode = " + dbl.sqlString(this.strBarGroupCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    pstmt1.setString(1, this.strMenuBarCode);
                    pstmt1.setString(2, this.strMenuBarName);
                    pstmt1.setString(3, this.strBarGroupCode);
                    pstmt1.setString(4, this.strIconPath);
                    pstmt1.setInt(5, Integer.parseInt(this.strEnabled));
                    pstmt1.setString(6, this.strOrderCOde);
                    pstmt1.setString(7, this.strRefInvokeCode);
                    pstmt1.setString(8, this.strDesc);
                    //-- by caocheng 2009.03.27 MS0001 QDV4.1--//
                    pstmt1.setString(9, this.FoperTypecode);
                    pstmt1.setString(10, this.FrightType);
                    //--------------------------------------//
                    pstmt1.setString(11, this.strMenuBarCode);
                    pstmt1.setString(12, this.strBarGroupCode);
                    pstmt1.executeUpdate();

                } else {
                    pstmt2.setString(1, this.strMenuBarCode);
                    pstmt2.setString(2, this.strMenuBarName);
                    pstmt2.setString(3, this.strBarGroupCode);
                    pstmt2.setString(4, this.strIconPath);
                    pstmt2.setInt(5, Integer.parseInt(this.strEnabled));
                    pstmt2.setString(6, this.strOrderCOde);
                    pstmt2.setString(7, this.strRefInvokeCode);
                    pstmt2.setString(8, this.strDesc);
                    //-- by caocheng 2009.03.27 MS0001 QDV4.1--//
                    pstmt2.setString(9, this.FoperTypecode);
                    pstmt2.setString(10, this.FrightType);
                    //-----------------------------------//
                    pstmt2.executeUpdate();
                }
                dbl.closeResultSetFinal(rs);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = true;
        } catch (Exception e) {
            throw new YssException("导入菜单条信息错误", e);
        } finally {
            dbl.closeStatementFinal(pstmt1);
            dbl.closeStatementFinal(pstmt2);
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
}
