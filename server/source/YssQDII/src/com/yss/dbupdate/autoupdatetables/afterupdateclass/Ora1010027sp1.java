package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import com.yss.dsub.*;
import com.yss.util.*;
import java.util.*;
import java.sql.*;
import com.yss.dbupdate.*;
import java.lang.String;
import com.yss.main.syssetting.RightBean;

/**
 * <p>Title: 角色权限的转换</p>
 *
 * <p>Description: 对角色权限的历史数据进行转换，并将角色权限明细到组合</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech</p>
 *
 * @author fanghaoln 2009-11-11
 * @bug    MS00590:QDV4赢时胜（上海）2009年7月24日09_B
 * @version 1.0
 */
public class Ora1010027sp1
    extends BaseDbUpdate {
    public Ora1010027sp1() {
    }

    public void doUpdate(HashMap hmInfo) throws YssException {
        try {
            //如果已经进行了历史数据的转换，则不必再次进行数据转换
            //if (!this.isExistsSuccessVerNum(YssCons.YSS_VERSION_1010027sp1)) {
                convertRoleRightData(hmInfo); //用于做历史数据转换
            //}

        } catch (Exception ex) {
            throw new YssException("1.0.1.0027sp2 更新表结构出错！", ex);
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
        /**shashijie 2012-7-2 STORY 2475 */
		//updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
        //StringBuffer bufSql = new StringBuffer(); //用于储存sql语句
        /**end*/
		String sPKName = ""; //用于储存主键名
        ResultSet rs = null; //声明结果集
        String strPKName = "";
        try {
            //若有表TB_DAO_SWIFT_fh则删除
            if (dbl.yssTableExist(pub.yssGetTableName("TB_DAO_SWIFT_fh"))) {
            	strPKName = getIsNullPKByTableName_Ora(pub.yssGetTableNameForUpdTables("TB_DAO_SWIFT_fh"));
            	if (strPKName != null && strPKName.trim().length() > 0) {
                     //删除约束
                     dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_DAO_SWIFT_fh")+" DROP CONSTRAINT " + strPKName);
                     //删除索引
                     deleteIndex(strPKName);
                 }
                sqlInfo.append("DROP TABLE "+pub.yssGetTableName("TB_DAO_SWIFT_fh"));
                dbl.executeSql("DROP TABLE "+pub.yssGetTableName("TB_DAO_SWIFT_fh"));
            }
           
            //若有表TB_DAO_SWIFTENTITY_fh则删除
            if (dbl.yssTableExist(pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"))) {
            	strPKName = getIsNullPKByTableName_Ora(pub.yssGetTableNameForUpdTables("TB_DAO_SWIFTENTITY_fh"));
            	if (strPKName != null && strPKName.trim().length() > 0) {
                     //删除约束
                     dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_DAO_SWIFTENTITY_fh")+" DROP CONSTRAINT " + strPKName);
                     //删除索引
                     deleteIndex(strPKName);
                 }
                sqlInfo.append("DROP TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));
                dbl.executeSql("DROP TABLE "+pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));
            }
      
        } catch (Exception ex) {
            throw new YssException(ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
