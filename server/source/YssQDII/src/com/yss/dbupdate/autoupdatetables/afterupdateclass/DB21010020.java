package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.*;

import com.yss.dbupdate.*;
import com.yss.main.syssetting.*;
import com.yss.util.*;

/**
 * <p>Title: 角色权限的转换</p>
 *
 * <p>Description:对角色权限的历史数据进行转换，并将角色权限明细到组合 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author fanghaoln 2009-08-10
 * @bug    MS00590:QDV4赢时胜（上海）2009年7月24日09_B
 */
public class DB21010020
    extends BaseDbUpdate {
    public DB21010020() {
    }

    public void doUpdate(HashMap hmInfo) throws YssException {
        try {
            //如果已经进行了历史数据的转换，则不必再次进行数据转换
            if (!this.isExistsSuccessVerNum(YssCons.YSS_VERSION_1010020)) {
                convertRoleRightData(hmInfo); //用于做历史数据转换
            }
        } catch (Exception ex) {
            throw new YssException("版本 1.0.1.00020 更新出错！", ex);
        }

    }

    /**
     * 根据权限类型表和角色权限表对角色权限进行转换
     * @param hmInfo HashMap
     * @throws YssException
     */
    public void convertRoleRightData(HashMap hmInfo) throws YssException {
        StringBuffer sqlInfo = null;
        StringBuffer updTables = null;
        sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
        StringBuffer bufSql = new StringBuffer(); //用于储存sql语句
        String sPKName = ""; //用于储存主键名
        ResultSet rs = null; //声明结果集
        try {

            UpdateUserRight(hmInfo); //更新用户权限表里的数据和表字段

            //历史表，用来存储转换后的权限
            if (dbl.yssTableExist("TB_TMP_Roleright")) {
                sqlInfo.append("DROP TABLE TB_TMP_Roleright");
                dbl.executeSql("DROP TABLE TB_TMP_Roleright");
            }

            bufSql.delete(0, bufSql.length());

            //创建临时表，从角色权限里取数左连接权限类型表，通过权限类型匹配
            bufSql.append(" create table TB_TMP_Roleright ");
            bufSql.append(" as select a.frolecode,a.frightcode,a.fopertypes ");
            bufSql.append(" from (select b.frolecode,c.fmenubarcode as frightcode, b.fopertypes  from Tb_Sys_Roleright b ");
            bufSql.append(" left join (select * from TB_SYS_RIGHTTYPE)c ");
            bufSql.append(" on b.frightcode=c.frighttypecode )a ");

            sqlInfo.append(bufSql.toString());
            dbl.executeSql(bufSql.toString()); //做历史数据转换
            bufSql.delete(0, bufSql.length());

            //获取主键
            sPKName = this.getIsNullPKByTableName_Ora("Tb_Sys_Roleright");

            //删除Tb_Sys_Roleright的主键约束
            if (sPKName.trim().length() != 0) {
                sqlInfo.append("ALTER TABLE Tb_Sys_Roleright DROP CONSTRAINT " + sPKName + " CASCADE");
                dbl.executeSql("ALTER TABLE Tb_Sys_Roleright DROP CONSTRAINT " + sPKName + " CASCADE");
                deleteIndex(sPKName);
            }
            //若有表Tb_Sys_Roleright_Bak则删除
            if (dbl.yssTableExist("Tb_Sys_Roleright_Bak")) {
                sqlInfo.append("DROP table Tb_Sys_Roleright_Bak");
                dbl.executeSql("DROP table Tb_Sys_Roleright_Bak");
            }
            //通过重命名来备份表
            sqlInfo.append("ALTER TABLE Tb_Sys_Roleright RENAME TO Tb_Sys_Roleright_Bak");
            dbl.executeSql("ALTER TABLE Tb_Sys_Roleright RENAME TO Tb_Sys_Roleright_Bak");

            //若有表Tb_Sys_Roleright则删除
            if (dbl.yssTableExist("Tb_Sys_Roleright")) {
                sqlInfo.append("DROP table Tb_Sys_Roleright");
                dbl.executeSql("DROP table Tb_Sys_Roleright");
            }
            //把前面建立的表改成角色权限表
            sqlInfo.append("ALTER TABLE TB_TMP_Roleright RENAME TO Tb_Sys_Roleright");
            dbl.executeSql("ALTER TABLE TB_TMP_Roleright RENAME TO Tb_Sys_Roleright");

            //从转换后的角色权限表中取主键
            sPKName = this.getIsNullPKByTableName_Ora("Tb_Sys_Roleright");

            //如果有主键，删除主键
            if (sPKName.trim().length() != 0) {
                sqlInfo.append("ALTER TABLE Tb_Sys_Roleright DROP CONSTRAINT " + sPKName + " CASCADE");
                dbl.executeSql("ALTER TABLE Tb_Sys_Roleright DROP CONSTRAINT " + sPKName + " CASCADE"); //删除TB_Sys_UserRight的主键约束
                deleteIndex(sPKName);
            }

            updTables.append("Tb_Sys_Roleright");

            convertRightTypeInfo(hmInfo); //用于修改用户权限表中操作类型字段的数据
            convertUserRightRole(hmInfo); //更新用户权限表里的数据
        } catch (Exception ex) {
            throw new YssException(ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 通过菜单条和角色权限来取操作类型的交集
     * @param hmInfo HashMap
     * @throws YssException
     */
    public void convertRightTypeInfo(HashMap hmInfo) throws YssException {
        String strSql = ""; //用于储存sql语句
        boolean bTrans = false;
        Connection conn = dbl.loadConnection(); //获取数据库连接
        ResultSet rs = null; //用于声明结果集
        RightBean right = null; //声明权限类型实例
        RightBean subRight = null; //声明权限类型实例
        HashMap alRight = new HashMap(); //声明ArrayList
        String fOperTypes = ""; //声明储存操作类型的字符串
        StringBuffer sqlInfo = null; //声明StringBuffer
        Statement st = null;
        ResultSet rsRole = null; //用于声明结果集
        try {
            sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
            st = conn.createStatement();
            conn.setAutoCommit(false);
            bTrans = true;
            //角色权限已经通过convertRoleRightData()方法转换成对应的菜单条
            //这里是通过角色权限表连接菜单条，取出用户角色和菜单条对应的操作类型代码
            strSql = " select a.frolecode, a.frightcode, a.fopertypes, b.fopertypecode " +
                " from Tb_Sys_Roleright a " +
                " left join (select * from TB_FUN_MENUBAR) b on a.frightcode = b.fbarcode ";
            sqlInfo.append(strSql);
            rs = dbl.openResultSet(strSql); //查询相关的菜单代码权限
            while (rs.next()) {
                //将权限的数据储存到权限的实例中，并根据菜单条对应的操作类型来筛选用户角色里的操作类型
                right = new RightBean();
                right.setUserCode(rs.getString("frolecode"));
                right.setRightCode(rs.getString("frightcode") == null ? " " : rs.getString("frightcode"));
                String fopertypes = rs.getString("fopertypes") == null ? " " : rs.getString("fopertypes"); //角色权限
                String fopertypecode = rs.getString("fopertypecode") == null ? " " : rs.getString("fopertypecode"); //菜单条权限
                String unionOpertypes = ""; //菜单条为准
                String[] fopertypesAll = fopertypes.split(","); //把角色权限分解出来
                for (int i = 0; i < fopertypesAll.length; i++) { //循环角色权限
                    if (fopertypecode.indexOf(fopertypesAll[i]) != -1) {
                        unionOpertypes = unionOpertypes + fopertypesAll[i] + ",";
                    }
                }
                if (unionOpertypes.length() > 0) {
                    unionOpertypes = unionOpertypes.substring(0, unionOpertypes.length() - 1);
                }
                right.setOperTypes(unionOpertypes); //把角色权限和菜单条权限取它们的交集
                if (alRight.containsKey(right.getUserCode() + right.getRightCode())) {
                    RightBean oldRight = (RightBean) alRight.get(right.getUserCode() + right.getRightCode()); //声明权限类型实例
                    if (oldRight.getOperTypes() != null && oldRight.getOperTypes().length() > 1) { //>1是因为如果里面存的是一个空格不用去合并直接用新记录就行了
                        String[] Opertypes = right.getOperTypes().split(","); //把新记录权限分解出来
                        for (int i = 0; i < Opertypes.length; i++) { //循环相同记录里的权限
                            if (oldRight.getOperTypes().indexOf(Opertypes[i]) == -1) { //判断老的记录里面是否含有新记录的权限
                                oldRight.setOperTypes(oldRight.getOperTypes() + "," + fopertypesAll[i]); //到老新记录权限的并集
                            }
                        }
                        right.setOperTypes(oldRight.getOperTypes()); //把权限的并集存在新记录里面去的权限里面去
                    }
                    alRight.remove(right.getUserCode() + right.getRightCode()); //有相同的记录把旧的记录删除
                }
                alRight.put(right.getUserCode() + right.getRightCode(), right); //add(right); //将实例添加到hashmap当中
            }
            //循环把角色表里的报表数据取出来重新加到角色表里面去 fanghaoln 20090815MS00590:QDV4赢时胜（上海）2009年7月24日09_B
            strSql = " select a.frolecode, a.frightcode, a.fopertypes " +
                " from tb_sys_roleright_bak a " +
                " where a.fopertypes='Role' ";
            sqlInfo.append(strSql);
            rsRole = dbl.openResultSet(strSql); //查询相关的菜单代码权限
            while (rsRole.next()) {
                //将权限的数据储存到权限的实例中，并根据菜单条对应的操作类型来筛选用户角色里的操作类型
                if (rsRole.getString("frightcode") == null || rsRole.getString("frightcode").length() == 0) {
                    continue;
                } else {
                    right = new RightBean();
                    right.setUserCode(rsRole.getString("frolecode"));
                    right.setRightCode(rsRole.getString("frightcode"));
                    right.setOperTypes(rsRole.getString("fopertypes"));
                    alRight.put(right.getUserCode() + right.getRightCode(), right); //add(right); //将实例添加到hashmap当中
                }
            }
            dbl.closeResultSetFinal(rsRole);
            //==========================================================================================================
            //循环所有权限，更新操作类型
            strSql = " delete tb_sys_roleright "; //先删除权限里的所有数据
            sqlInfo.append(strSql);
            dbl.executeSql(strSql); //删除数据
            Iterator iterator = alRight.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                subRight = (RightBean) alRight.get(key);
                fOperTypes = subRight.getOperTypes(); //获取实例的权限
                if (fOperTypes.length() == 0) {
                    fOperTypes = " ";
                }
                //========================================插入转换后角色的权限================================================
                strSql = " insert into tb_sys_roleright(frolecode,FRightCode,FOperTypes) values ( " +
                    dbl.sqlString(subRight.getUserCode()) + "," +
                    dbl.sqlString(subRight.getRightCode() == null ? " " : subRight.getRightCode()) + "," +
                    dbl.sqlString(fOperTypes) + ")";
                sqlInfo.append(strSql);
                dbl.executeSql(strSql); //更新操作类型数据
            }
            //添加TB_Sys_UserRight的主键约束
            sqlInfo.append("ALTER TABLE Tb_Sys_Roleright ADD CONSTRAINT Tb_Sys_Roleright PRIMARY KEY (frolecode,frightcode)");
            dbl.executeSql("ALTER TABLE Tb_Sys_Roleright ADD CONSTRAINT Tb_Sys_Roleright PRIMARY KEY (frolecode,frightcode)");
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改用户权限表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 更新用户权限表的角色权限
     * 如果一个用户拥有某一角色，那么跟新为用户有权限的公共级别的权限由此角色权限
     * 用户组合群级别的权限也有此角色权限
     * 用户组合级别的权限也应有此角色权限
     * @param hmInfo HashMap
     * @throws YssException
     */
    public void convertUserRightRole(HashMap hmInfo) throws YssException {
        String strSql = ""; //用于储存sql语句
        boolean bTrans = false;
        Connection conn = dbl.loadConnection(); //获取数据库连接
        ResultSet rs = null; //用于声明结果集
        ResultSet rs1 = null; //用于声明结果集
        RightBean right = null; //声明权限类型实例
        RightBean subRight = null; //声明权限类型实例
        ArrayList alRight = new ArrayList(); //声明ArrayList
        StringBuffer sqlInfo = null; //声明StringBuffer
        try {
            sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句

            conn.setAutoCommit(false);
            bTrans = true;

            //从用户权限表中查询角色权限
            strSql = " select * from tb_sys_userright a where a.frightind='Role' ";
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
            dbl.closeResultSetFinal(rs); //fanghaoln 下面要用到rs

            for (int i = 0; i < alRight.size(); i++) { //循环查询到的权限实例
                //===========================================fanghaoln 删除以前的角色权限============
                subRight = (RightBean) alRight.get(i);
                strSql = " delete from tb_sys_userright a where a.frightind= " + dbl.sqlString(subRight.getRightInd()) +
                    " and a.frightcode = " + dbl.sqlString(subRight.getRightCode()) +
                    " and a.FUSERCODE = " + dbl.sqlString(subRight.getUserCode()); //增加用户代码这个条件，防止删除正确的数据 20090819
                sqlInfo.append(strSql);
                dbl.executeSql(strSql); //更新操作类型数据
                //========================================插入角色的公共权限================================================
                strSql = " insert into tb_sys_userright(fusercode,frighttype,fassetgroupcode,frightcode,fportcode,frightind,fopertypes) values ( " +
                    dbl.sqlString(subRight.getUserCode()) + "," +
                    dbl.sqlString("public") + "," +
                    dbl.sqlString(" ") + "," +
                    dbl.sqlString(subRight.getRightCode()) + "," +
                    dbl.sqlString(" ") + "," +
                    dbl.sqlString(subRight.getRightInd()) + "," +
                    dbl.sqlString("") + ")";
                sqlInfo.append(strSql);
                dbl.executeSql(strSql); //更新操作类型数据

                //========================查出角色的组合权限==============================================================
                strSql = " select distinct FAssetGroupCode from TB_SYS_ASSETGROUP ";
                sqlInfo.append(strSql);
                rs = dbl.openResultSet(strSql); //查询相关的菜单代码权限
                while (rs.next()) { //将权限的数据储存到权限的实例中
                    //插入角色对象的组合群权限
                    if (rs.getString("FAssetGroupCode") == null || rs.getString("FAssetGroupCode").equals(" ") ||
                        !dbl.yssTableExist("tb_" + rs.getString("FAssetGroupCode") + "_para_portfoio")) {
                        continue; //对没有组合群或组合群为“ ”进行判断
                    }
                    strSql = " insert into tb_sys_userright values ( " +
                        dbl.sqlString(subRight.getUserCode()) + "," +
                        dbl.sqlString("group") + "," +
                        dbl.sqlString(rs.getString("FAssetGroupCode")) + "," +
                        dbl.sqlString(subRight.getRightCode()) + "," +
                        dbl.sqlString(" ") + "," +
                        dbl.sqlString(subRight.getRightInd()) + "," +
                        dbl.sqlString("") + ")";
                    sqlInfo.append(strSql);
                    dbl.executeSql(strSql); //更新操作类型数据

                    //查出组合群里的有多少个组合=================================================================
                    strSql = " select  distinct fportcode from " + "tb_" + rs.getString("FAssetGroupCode") + "_para_portfoio ";
                    sqlInfo.append(strSql);
                    rs1 = dbl.openResultSet(strSql); //查出组合群里的有多少个组合
                    while (rs1.next()) { //将权限的数据储存到权限的实例中
                        //插入角色对象的组合群权限
                        if (rs1.getString("fportcode") == null || rs1.getString("fportcode").equals(" ")) {
                            continue; //对没有组合或组合为“ ”进行判断不进行角色更新
                        }
                        strSql = " insert into tb_sys_userright values ( " +
                            dbl.sqlString(subRight.getUserCode()) + "," +
                            dbl.sqlString("port") + "," +
                            dbl.sqlString(rs.getString("FAssetGroupCode")) + "," +
                            dbl.sqlString(subRight.getRightCode()) + "," +
                            dbl.sqlString(rs1.getString("fportcode")) + "," +
                            dbl.sqlString(subRight.getRightInd()) + "," +
                            dbl.sqlString("") + ")";
                        sqlInfo.append(strSql);
                        dbl.executeSql(strSql); //更新操作类型数据
                    }
                    dbl.closeResultSetFinal(rs1);
                }
                dbl.closeResultSetFinal(rs);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改用户权限表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs1);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //=====================================================fanghao==============================================================================
    /**
     * 更新用户权限表的角色权限
     * 如果一个用户拥有某一角色，那么跟新为用户有权限的公共级别的权限由此角色权限
     * 用户组合群级别的权限也有此角色权限
     * 用户组合级别的权限也应有此角色权限
     * @param hmInfo HashMap
     * @throws YssException
     */
    public void UpdateUserRight(HashMap hmInfo) throws YssException {
        Ora1010020 ora20 = new Ora1010020();
        ora20.UpdateUserRight(hmInfo);
    }
}
