package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import com.yss.dsub.*;
import com.yss.util.*;
import java.util.*;
import java.sql.*;
import com.yss.dbupdate.*;
import com.yss.main.syssetting.RightBean;

public class Ora1010017
    extends BaseDbUpdate {
    public Ora1010017() {
    }

    public void doUpdate(HashMap hmInfo) throws YssException {
        try {
            convertUserRightData(hmInfo); //用于做历史数据转换
            convertRightOperTypeFromMenubar(hmInfo);
        } catch (Exception ex) {
            throw new YssException("版本 1.0.1.00017 更新出错！", ex);
        }
    }

    /**
     * 通过菜单条代码的权限类型，修改用户权限，把多余的权限去掉
     * 2009.06.10 蒋锦 添加 QDV4赢时胜（上海）2009年02月01日10_A
     * @param hmInfo HashMap
     * @throws YssException
     */
    public void convertRightOperTypeFromMenubar(HashMap hmInfo) throws YssException {
        ResultSet rs = null;
        String strSql = ""; //用于储存sql语句
        boolean bTrans = false;
        Connection conn = dbl.loadConnection(); //获取数据库连接
        StringBuffer sqlInfo = null;
        String sMenuOper = "";
        PreparedStatement pst = null;
        try {
            strSql = "UPDATE TB_SYS_USERRIGHT SET FOperTypes = ?" +
                " WHERE FUserCode = ?" +
                " AND FAssetGroupCode = ?" +
                " AND FRightCode = ?" +
                " AND FRightInd = ?" +
                " AND FPortCode = ?" +
                " AND FRightType = ?";
            pst = conn.prepareStatement(strSql);
            sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "SELECT a.*, b.FoperTypeCode FROM Tb_Sys_Userright a JOIN Tb_Fun_Menubar b ON a.frightcode = b.fbarcode " +
                "AND a.FOperTypes IS NOT null";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                String sRightOper = "";
                ArrayList alRight = new ArrayList(Arrays.asList(rs.getString("FOperTypes").split(",")));
                sMenuOper = rs.getString("FoperTypeCode");
                for (int i = alRight.size() - 1; i >= 0; i--) {
                    if (sMenuOper.indexOf( (String) alRight.get(i)) == -1) {
                        alRight.remove(i);
                    } else {
                        sRightOper = (String) alRight.get(i) + "," + sRightOper;
                    }
                }
                if (sRightOper.length() > 0) {
                    sRightOper = sRightOper.substring(0, sRightOper.length() - 1);
                }
                pst.setString(1, sRightOper);
                pst.setString(2, rs.getString("FUserCode"));
                pst.setString(3, rs.getString("FAssetGroupCode"));
                pst.setString(4, rs.getString("FRightCode"));
                pst.setString(5, rs.getString("FRightInd"));
                pst.setString(6, rs.getString("FPortCode"));
                pst.setString(7, rs.getString("FRightType"));
                pst.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException(ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeStatementFinal(pst);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

    /**
     * MS00010 add by songjie 2009-06-08
     * 用于修改菜单条按钮对应的权限类型，如把收益计提的统计按钮的权限(add或edit)改为execute
     * QDV4赢时胜（上海）2009年02月01日10_A
     * @throws YssException
     */
    public void convertRightTypeInfo(HashMap hmInfo) throws YssException {
        String strSql = ""; //用于储存sql语句
        boolean bTrans = false;
        Connection conn = dbl.loadConnection(); //获取数据库连接
        ResultSet rs = null; //用于声明结果集
        RightBean right = null; //声明权限类型实例
        RightBean subRight = null; //声明权限类型实例
        ArrayList alRight = new ArrayList(); //声明ArrayList
        String operType = ""; //声明储存操作类型的字符串
        String[] operTypes = null; //声明操作类型数组
        String fOperTypes = ""; //声明储存操作类型的字符串
        StringBuffer sqlInfo = null; //声明StringBuffer
        try {
            sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句

            conn.setAutoCommit(false);
            bTrans = true;

            strSql = " select * from tb_sys_userRight where FRightCode in ('incomecalculate','OperDeal'," +
                "'valuation','storagestat','interfacedeal','voucherbuild','valuationreq'," +
                "'navdata','compresult','GuessValue') ";
            sqlInfo.append(strSql);
            rs = dbl.openResultSet(strSql); //查询相关的菜单代码权限
            while (rs.next()) { //将权限的数据储存到权限的实例中
                right = new RightBean();
                right.setUserCode(rs.getString("FUserCode"));
                right.setRightType(rs.getString("FRightType"));
                right.setAssetGroupCode(rs.getString("FAssetGroupCode"));
                right.setRightCode(rs.getString("FRightCode"));
                right.setPortCode(rs.getString("FPortCode"));
                right.setRightInd(rs.getString("FRightInd"));
                right.setOperTypes(rs.getString("FOperTypes"));
                alRight.add(right); //将实例添加到ArrayList当中
            }

            for (int i = 0; i < alRight.size(); i++) { //循环查询到的权限实例
                subRight = (RightBean) alRight.get(i);
                operType = subRight.getOperTypes(); //获取实例的操作类型数据
                if (subRight.getRightCode().equals("GuessValue")) { //若操作类型为GuessValue
                    operTypes = dealOperInfo(operType, "edit"); //拼接操作类型数据
                } else {
                    operTypes = dealOperInfo(operType, "add"); //拼接操作类型数据
                }
                fOperTypes = dealArrayToString(operTypes); //将数组数据以逗号分隔拼接成字符串

                if (operType.equals(fOperTypes)) { //若处理后的字符串和处理前的相同，则执行下一个循环
                    continue;
                }

                strSql = " update tb_sys_userRight set FOperTypes = " + dbl.sqlString(fOperTypes) +
                    " where FUserCode = " + dbl.sqlString(subRight.getUserCode()) +
                    " and FRightType = " + dbl.sqlString(subRight.getRightType()) +
                    " and FAssetGroupCode = " + dbl.sqlString(subRight.getAssetGroupCode()) +
                    " and FRightCode = " + dbl.sqlString(subRight.getRightCode()) +
                    " and FPortCode = " + dbl.sqlString(subRight.getPortCode()) +
                    " and FRightInd = " + dbl.sqlString(subRight.getRightInd());
                sqlInfo.append(strSql);
                dbl.executeSql(strSql); //更新操作类型数据
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改用户权限表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 用于处理操作类型字段的数据
     * MS00010
     * add by songjie
     * 2009-06-08
     * QDV4赢时胜（上海）2009年02月01日10_A
     * @param operType String
     */
    public String[] dealOperInfo(String operType, String operInfo) {
        ArrayList alOperType = new ArrayList();
        String[] operTypes = null;
        operTypes = operType.split(",");

        for (int j = 0; j < operTypes.length; j++) {
            alOperType.add(operTypes[j]);
        }

        if (operInfo.equals("add")) {
            if (operType.indexOf("add") != -1) {
                if (operType.indexOf("execute") != -1) {
                    alOperType.remove("add");
                } else {
                    alOperType.remove("add");
                    alOperType.add("execute");
                }
            } else {
                if (operType.indexOf("execute") != -1) {
                    alOperType.remove("execute");
                }
            }
        }

        if (operInfo.equals("edit")) {
            if (operType.indexOf("edit") != -1) {
                if (operType.indexOf("execute") != -1) {
                    alOperType.remove("edit");
                } else {
                    alOperType.remove("edit");
                    alOperType.add("execute");
                }
            } else {
                if (operType.indexOf("execute") != -1) {
                    alOperType.remove("execute");
                }
            }
        }

        return (String[]) alOperType.toArray(new String[0]);
    }

    /**
     * MS00010
     * add by songjie
     * 2009-06-08
     * QDV4赢时胜（上海）2009年02月01日10_A
     * 用于处理数组型的操作类型数据转化为字符串类型的操作类型数据
     * @param operTypes String[]
     */
    public String dealArrayToString(String[] operTypes) {
        String operInfo = "";
        for (int i = 0; i < operTypes.length; i++) {
            if (i != operTypes.length - 1) {
                operInfo += operTypes[i] + ",";
            } else {
                operInfo += operTypes[i];
            }
        }
        return operInfo;
    }

    /**
     * 调整 TB_Sys_UserRight 中的数据
     * 用于做历史数据转换
     * 2009-05-08 蒋锦 添加
     * MS00010 《QDV4赢时胜（上海）2009年02月01日10_A》
     * @throws YssException
     */
    public void convertUserRightData(HashMap hmInfo) throws YssException {
        StringBuffer sqlInfo = null;
        StringBuffer updTables = null;
        sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
        StringBuffer bufSql = new StringBuffer(); //用于储存sql语句
        ArrayList alPrefix = new ArrayList();
        String sPKName = ""; //用于储存主键名
        ResultSet rs = null; //声明结果集
        String sView = ""; //用于储存生成试图的sql语句
        try {

            //2009-06-01 蒋锦 添加
            //系统权限明细到单个组合   MS00010   《QDV4赢时胜（上海）2009年02月01日10_A》
            //如果版本已经更新过了就直接返回不需要再运行以下的数据转换代码
            if (this.isExistsSuccessVerNum(YssCons.YSS_VERSION_1010017)) {
                return;
            }

            sqlInfo.append("SELECT * FROM Tb_Sys_Assetgroup");
            bufSql.append("SELECT * FROM Tb_Sys_Assetgroup"); //查找所有组合群信息

            rs = dbl.openResultSet(bufSql.toString());
            bufSql.delete(0, bufSql.length());

            while (rs.next()) { //循环组合群信息
                String sAssetGroupCode = rs.getString("fassetgroupcode").trim(); //获取当前指针指向的组合群代码
                if (dbl.yssTableExist("TB_" + sAssetGroupCode + "_Para_Portfolio")) { //判断是否有当前组合群的组合信息
                    alPrefix.add(sAssetGroupCode);
                }
            }

            for (int i = 0; i < alPrefix.size(); i++) { //循环有组合信息的组合群
                sView += " SELECT FAssetGroupCode, FPortCode FROM TB_" +
                    (String) alPrefix.get(i) + "_Para_Portfolio WHERE FCheckState = 1 UNION "; //生成所有已存在组合群的组合群代码和组合代码的视图
            }

            sView = sView.substring(0, sView.lastIndexOf("UNION"));

            if (dbl.yssViewExist("V_TMP_Para_Portfolio")) { //若有表V_TMP_Para_Portfolio则删除
                sqlInfo.append("DROP VIEW V_TMP_Para_Portfolio");
                dbl.executeSql("DROP VIEW V_TMP_Para_Portfolio");
            }

            bufSql.append("CREATE VIEW V_TMP_Para_Portfolio AS (").append(sView).
                append(")");
            sqlInfo.append(bufSql.toString());
            dbl.executeSql(bufSql.toString());

            if (dbl.yssTableExist("TB_TMP_UserRight")) {
                sqlInfo.append("DROP TABLE TB_TMP_UserRight");
                dbl.executeSql("DROP TABLE TB_TMP_UserRight");
            }

            bufSql.delete(0, bufSql.length());

            bufSql.append(" CREATE TABLE TB_TMP_UserRight AS ");
            bufSql.append(" SELECT distinct a.Fusercode, a.frighttype, d.fassetgroupcode, a.fmenubarcode AS FRightCode, ");
            bufSql.append(" e.Fportcode, max(a.frightind) as FRightInd, max(a.fopertypes) as FOperTypes");
            bufSql.append(" FROM (SELECT a.Fusercode, ");
            bufSql.append(" CASE ");
            bufSql.append(" WHEN a.frightind = 'Role' THEN ");
            bufSql.append(" 'group' ");
            bufSql.append(" WHEN a.frightind = 'Report' THEN ");
            bufSql.append(" 'public' ");
            bufSql.append(" ELSE ");
            bufSql.append(" c.frighttype ");
            bufSql.append(" END AS frighttype, ");
            bufSql.append(" b.fmenubarcode, a.frightind, ");
            bufSql.append(" a.fopertypes ");
            bufSql.append(" FROM Tb_Sys_Userright a ");
            bufSql.append(" LEFT JOIN tb_sys_righttype b ON a.frightcode = b.frighttypecode ");
            bufSql.append(" LEFT JOIN (SELECT 'public' AS FRightType, FBarCode ");
            bufSql.append(" FROM Tb_Fun_Menubar ");
            bufSql.append(" WHERE FRightType LIKE '%public%' ");
            bufSql.append(" UNION ");
            bufSql.append(" SELECT 'port' AS FRightType, FBarCode ");
            bufSql.append(" FROM Tb_Fun_Menubar ");
            bufSql.append(" WHERE FRightType LIKE '%port%' ");
            bufSql.append(" UNION ");
            bufSql.append(" SELECT 'group' AS FRightType, FBarCode ");
            bufSql.append(" FROM Tb_Fun_Menubar ");
            bufSql.append(" WHERE FRightType LIKE '%group%' ");
            bufSql.append(" UNION ");
            bufSql.append(" SELECT 'system' AS FRightType, FBarCode ");
            bufSql.append(" FROM Tb_Fun_Menubar ");
            bufSql.append(" WHERE FRightType LIKE '%system%') c ON c.fbarcode = ");
            bufSql.append(" b.fmenubarcode where b.frighttypecode is not null) a ");
            bufSql.append(" LEFT JOIN Tb_Sys_Assetgroup d ON a.frighttype = 'port' ");
            bufSql.append(" OR a.frighttype = 'group' ");
            bufSql.append(" LEFT JOIN V_TMP_Para_Portfolio e ON a.frighttype = 'port' ");
            bufSql.append(" AND e.fassetgroupcode = ");
            bufSql.append(" d.fassetgroupcode where a.Frighttype is not null");
            bufSql.append(" group by a.Fusercode,a.frighttype,d.fassetgroupcode,a.fmenubarcode,e.Fportcode");
            bufSql.append(" UNION select bak.Fusercode,CASE WHEN bak.frightind = 'Role' THEN 'group' ");
            bufSql.append(" WHEN bak.frightind = 'Report' THEN 'public' end as FRightType, ");
            bufSql.append(" bak.FAssetGroupCode,bak.FRightCode,' ' as FPortCode,bak.FRightInd,bak.FOperTypes ");
            bufSql.append(" from tb_sys_userright bak where FRightInd in ('Role', 'Report') ");

            sqlInfo.append(bufSql.toString());
            dbl.executeSql(bufSql.toString()); //做历史数据转换
            bufSql.delete(0, bufSql.length());

            sPKName = this.getIsNullPKByTableName_Ora("TB_Sys_UserRight");
            if (sPKName.trim().length() != 0) {
                sqlInfo.append("ALTER TABLE TB_Sys_UserRight DROP CONSTRAINT " + sPKName + " CASCADE");
                dbl.executeSql("ALTER TABLE TB_Sys_UserRight DROP CONSTRAINT " + sPKName + " CASCADE"); //删除TB_Sys_UserRight的主键约束
                deleteIndex(sPKName);
            }

            if (dbl.yssTableExist("TB_SYS_UserRight_Bak")) { //若有表TB_SYS_UserRight_Bak则删除
                sqlInfo.append("DROP table TB_SYS_UserRight_Bak");
                dbl.executeSql("DROP table TB_SYS_UserRight_Bak");
            }
            sqlInfo.append("ALTER TABLE TB_Sys_UserRight RENAME TO TB_SYS_UserRight_Bak");
            dbl.executeSql("ALTER TABLE TB_Sys_UserRight RENAME TO TB_SYS_UserRight_Bak");

            if (dbl.yssTableExist("TB_Sys_UserRight")) { //若有表TB_Sys_UserRight则删除
                sqlInfo.append("DROP table TB_Sys_UserRight");
                dbl.executeSql("DROP table TB_Sys_UserRight");
            }

            sqlInfo.append("ALTER TABLE TB_TMP_UserRight RENAME TO TB_Sys_UserRight");
            dbl.executeSql("ALTER TABLE TB_TMP_UserRight RENAME TO TB_Sys_UserRight");

            bufSql.append(" INSERT INTO TB_sys_UserRight ");
            bufSql.append(" SELECT Distinct b.fusercode, ");
            bufSql.append(" a.Frighttype, ");
            bufSql.append(" CASE ");
            bufSql.append(" WHEN a.Frighttype LIKE '%port%' OR a.Frighttype LIKE '%group%' THEN ");
            bufSql.append(" c.fassetgroupcode ");
            bufSql.append(" ELSE ");
            bufSql.append(" ' ' ");
            bufSql.append(" END AS FAssetGroupCode, ");
            bufSql.append(" a.fbarcode, ");
            bufSql.append(" CASE ");
            bufSql.append(" WHEN a.Frighttype LIKE '%port%' THEN ");
            bufSql.append(" d.fportcode ");
            bufSql.append(" ELSE ");
            bufSql.append(" ' ' ");
            bufSql.append(" END AS FPortCode, ");
            bufSql.append(" 'Right' AS FRightInd, ");
            bufSql.append(" 'add,del,edit,brow,execute,audit,clear,revert' AS FOperTypes ");
            bufSql.append(" FROM (SELECT 'public' AS FRightType, FBarCode ");
            bufSql.append(" FROM Tb_Fun_Menubar ");
            bufSql.append(" WHERE FRightType LIKE '%public%' ");
            bufSql.append(" UNION ");
            bufSql.append(" SELECT 'port' AS FRightType, FBarCode ");
            bufSql.append(" FROM Tb_Fun_Menubar ");
            bufSql.append(" WHERE FRightType LIKE '%port%' ");
            bufSql.append(" UNION ");
            bufSql.append(" SELECT 'group' AS FRightType, FBarCode ");
            bufSql.append(" FROM Tb_Fun_Menubar ");
            bufSql.append(" WHERE FRightType LIKE '%group%' ");
            bufSql.append(" UNION ");
            bufSql.append(" SELECT 'system' AS FRightType, FBarCode ");
            bufSql.append(" FROM Tb_Fun_Menubar ");
            bufSql.append(" WHERE FRightType LIKE '%system%') a ");
            bufSql.append(" JOIN (SELECT * ");
            bufSql.append(" FROM Tb_Fun_Menubar m ");
            bufSql.append(" JOIN TB_Sys_UserRight u ON m.fbarcode = u.frightcode) b ON a.fbarcode = ");
            bufSql.append(" b.FBarGroupCode ");
            bufSql.append(" LEFT JOIN TB_Sys_AssetGroup c ON 1 = 1 ");
            bufSql.append(" LEFT JOIN V_TMP_Para_Portfolio d ON  c.fassetgroupcode = d.fassetgroupcode where a.frighttype is not null");

            sqlInfo.append(bufSql.toString());
            dbl.executeSql(bufSql.toString());

            sqlInfo.append(" update tb_sys_UserRight set FAssetGroupCode = ' ' where FAssetGroupCode is NULL ");
            dbl.executeSql(" update tb_sys_UserRight set FAssetGroupCode = ' ' where FAssetGroupCode is NULL ");

            sqlInfo.append(" update tb_sys_UserRight set FPortCode = ' ' where FPortCode is NULL ");
            dbl.executeSql(" update tb_sys_UserRight set FPortCode = ' ' where FPortCode is NULL ");

            sqlInfo.append(" update tb_sys_UserRight set FRightCode = ' ' where FRightCode is NULL ");
            dbl.executeSql(" update tb_sys_UserRight set FRightCode = ' ' where FRightCode is NULL ");

            sqlInfo.append(" update tb_sys_UserRight set FRightType = ' ' where FRightType is NULL ");
            dbl.executeSql(" update tb_sys_UserRight set FRightType = ' ' where FRightType is NULL ");

            sPKName = this.getIsNullPKByTableName_Ora("TB_Sys_UserRight");

            if (sPKName.trim().length() != 0) {
                sqlInfo.append("ALTER TABLE TB_Sys_UserRight DROP CONSTRAINT " + sPKName + " CASCADE");
                dbl.executeSql("ALTER TABLE TB_Sys_UserRight DROP CONSTRAINT " + sPKName + " CASCADE"); //删除TB_Sys_UserRight的主键约束
                deleteIndex(sPKName);
            }

            //添加TB_Sys_UserRight的主键约束
            sqlInfo.append("ALTER TABLE TB_sys_UserRight ADD CONSTRAINT PK_TB_sys_UserRight PRIMARY KEY (FUserCode,FAssetGroupCode,FRightCode,FPortCode,FRightType,FRightInd)");
            dbl.executeSql("ALTER TABLE TB_sys_UserRight ADD CONSTRAINT PK_TB_sys_UserRight PRIMARY KEY (FUserCode,FAssetGroupCode,FRightCode,FPortCode,FRightType,FRightInd)");

            updTables.append("tb_sys_userRight");

            convertRightTypeInfo(hmInfo); //用于修改用户权限表中操作类型字段的数据
        } catch (Exception ex) {
            throw new YssException(ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
