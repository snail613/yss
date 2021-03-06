package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import com.yss.dsub.*;
import com.yss.util.*;
import java.util.*;
import java.sql.*;
import com.yss.dbupdate.*;
import java.lang.String;
import com.yss.main.syssetting.RightBean;

/**
 * <p>
 * Title: 角色权限的转换
 * </p>
 * 
 * <p>
 * Description: 对角色权限的历史数据进行转换，并将角色权限明细到组合
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: Ysstech
 * </p>
 * 
 * @author fanghaoln 2009-11-11
 * @bug MS00590:QDV4赢时胜（上海）2009年7月24日09_B
 * @version 1.0
 */
public class Ora1010022 extends BaseDbUpdate {
	public Ora1010022() {
	}

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			// 如果已经进行了历史数据的转换，则不必再次进行数据转换
			if (!this.isExistsSuccessVerNum(YssCons.YSS_VERSION_1010022)) {
				convertRoleRightData(hmInfo); // 用于做历史数据转换
			}

		} catch (Exception ex) {
			throw new YssException("1.0.1.0022 更新表结构出错！", ex);
		}
	}

	/**
	 * 根据权限类型表和角色权限表对角色权限进行转换
	 * 
	 * @param hmInfo
	 *            HashMap
	 * @throws YssException
	 */
	public void convertRoleRightData(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
		updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名
		StringBuffer bufSql = new StringBuffer(); // 用于储存sql语句
		String sPKName = ""; // 用于储存主键名
		ResultSet rs = null; // 声明结果集
		try {
			sPKName = getIsNullPKByTableName_Ora(pub.yssGetTableNameForUpdTables("TB_DAO_SWIFT"));
			if (sPKName != null && sPKName.trim().length() > 0) {
				// 删除约束
				dbl.executeSql("ALTER TABLE "
						+ pub.yssGetTableNameForUpdTables("TB_DAO_SWIFT")
						+ " DROP CONSTRAINT " + sPKName);
				// 删除索引
				deleteIndex(sPKName);
			}
			// 若有表TB_DAO_SWIFT_fh则删除
			if (dbl.yssTableExist(pub.yssGetTableName("TB_DAO_SWIFT_fh"))) {
				sqlInfo.append("DROP TABLE "
						+ pub.yssGetTableName("TB_DAO_SWIFT_fh"));
				dbl.executeSql("DROP TABLE "
						+ pub.yssGetTableName("TB_DAO_SWIFT_fh"));
			}
			// 通过重命名来备份表
			sqlInfo.append("ALTER TABLE "
					+ pub.yssGetTableName("TB_DAO_SWIFT ") + " RENAME TO "
					+ pub.yssGetTableName("TB_DAO_SWIFT_fh"));
			dbl.executeSql("ALTER TABLE "
					+ pub.yssGetTableName("TB_DAO_SWIFT ") + " RENAME TO "
					+ pub.yssGetTableName("TB_DAO_SWIFT_fh"));

			// 建立表的结构
			sqlInfo.append("create table "
					+ pub.yssGetTableName("TB_DAO_SWIFT ")
					+ " as select * from  "
					+ pub.yssGetTableName("TB_DAO_SWIFT_fh") + " where 1=2");
			dbl.executeSql("create table "
					+ pub.yssGetTableName("TB_DAO_SWIFT ")
					+ " as select * from  "
					+ pub.yssGetTableName("TB_DAO_SWIFT_fh") + " where 1=2");

			// 增加新的字段
			if (this.existsTabColumn_Ora(pub.yssGetTableName("TB_DAO_SWIFT"),"FSWIFTCODE")) {
				sqlInfo.append("ALTER TABLE "
						+ pub.yssGetTableName("TB_DAO_SWIFT ")
						+ " add FSWIFTCODE varchar2(30) not null ");
				dbl.executeSql("ALTER TABLE "
						+ pub.yssGetTableName("TB_DAO_SWIFT ")
						+ " add FSWIFTCODE varchar2(30) not null ");
			}
			bufSql.delete(0, bufSql.length());

			// 导入表中原来数据
			bufSql.append(" Insert into "+ pub.yssGetTableName("TB_DAO_SWIFT ") + "( ");
			bufSql.append(" FSWIFTTYPE,");
			bufSql.append(" FSWIFTDESC,");
			bufSql.append(" FTABLECODE,");
			bufSql.append(" FPATH,");
			bufSql.append(" FCRITERION,");
			bufSql.append(" FOPERTYPE,");
			bufSql.append(" FREFLOW,");
			bufSql.append(" FDSCODE,");
			bufSql.append(" FCHECKSTATE,");
			bufSql.append(" FCREATOR,");
			bufSql.append(" FCREATETIME,");
			bufSql.append(" FCHECKUSER,");
			bufSql.append(" FCHECKTIME,");
			bufSql.append(" FSWIFTCODE)");
			bufSql.append(" select FSWIFTTYPE,");
			bufSql.append(" FSWIFTDESC,");
			bufSql.append(" FTABLECODE,");
			bufSql.append(" FPATH,");
			bufSql.append(" FCRITERION,");
			bufSql.append(" FOPERTYPE,");
			bufSql.append(" FREFLOW,");
			bufSql.append(" FDSCODE,");
			bufSql.append(" FCHECKSTATE,");
			bufSql.append(" FCREATOR,");
			bufSql.append(" FCREATETIME,");
			bufSql.append(" FCHECKUSER,");
			bufSql.append(" FCHECKTIME,");
			bufSql.append(" rownum as FSWIFTCODE  from "+ pub.yssGetTableName("TB_DAO_SWIFT_fh"));
			sqlInfo.append(bufSql.toString());
			dbl.executeSql(bufSql.toString()); // 做历史数据转换
			bufSql.delete(0, bufSql.length());
			// 添加TB_Sys_UserRight的主键约束
			sqlInfo.append("ALTER TABLE "
					+ pub.yssGetTableName("TB_DAO_SWIFT ")
					+ " ADD CONSTRAINT PK_"
					+ pub.yssGetTableName("TB_DAO_SWIFT ")
					+ " PRIMARY KEY (FSWIFTCODE)");
			dbl.executeSql("ALTER TABLE "
					+ pub.yssGetTableName("TB_DAO_SWIFT ")
					+ " ADD CONSTRAINT PK_"
					+ pub.yssGetTableName("TB_DAO_SWIFT ")
					+ " PRIMARY KEY (FSWIFTCODE)");
			// ==================================================更新表TB_DAO_SWIFTENTITY=============
			sPKName = getIsNullPKByTableName_Ora(pub.yssGetTableNameForUpdTables("TB_DAO_SWIFTENTITY"));
			if (sPKName != null && sPKName.trim().length() > 0) {
				// 删除约束
				dbl.executeSql("ALTER TABLE "
						+ pub.yssGetTableNameForUpdTables("TB_DAO_SWIFTENTITY")
						+ " DROP CONSTRAINT " + sPKName);
				// 删除索引
				deleteIndex(sPKName);
			}
			// 若有表TB_DAO_SWIFTENTITY_fh则删除
			if (dbl.yssTableExist(pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"))) {
				sqlInfo.append("DROP TABLE "
						+ pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));
				dbl.executeSql("DROP TABLE "
						+ pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));
			}
			// 通过重命名来备份表
			sqlInfo.append("ALTER TABLE "
					+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
					+ " RENAME TO "
					+ pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));
			dbl.executeSql("ALTER TABLE "
					+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
					+ " RENAME TO "
					+ pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh"));

			// 建立表的结构
			sqlInfo.append("create table "
					+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
					+ " as select * from  "
					+ pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh")
					+ " where 1=2");
			dbl.executeSql("create table "
					+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
					+ " as select * from  "
					+ pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh")
					+ " where 1=2");

			// 增加新的字段
			if (this.existsTabColumn_Ora(pub.yssGetTableName("TB_DAO_SWIFTENTITY"), "FSWIFTCODE")) {
				sqlInfo.append("ALTER TABLE "
						+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
						+ " add FSWIFTCODE varchar2(30) not null ");
				dbl.executeSql("ALTER TABLE "
						+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
						+ " add FSWIFTCODE varchar2(30) not null ");
			}
			bufSql.delete(0, bufSql.length());

			// 导入表中原来数据
			bufSql.append(" Insert into "+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ") + "( ");
			bufSql.append(" FSWIFTTYPE,");
			bufSql.append(" FSTATUS,");
			bufSql.append(" FINDEX,");
			bufSql.append(" FCONTENT,");
			bufSql.append(" FOPTION,");
			bufSql.append(" FTAG,");
			bufSql.append(" FQUALIFIER,");
			bufSql.append(" FFIELDNAME,");
			bufSql.append(" FFIELDFULLNAME,");
			bufSql.append(" FTABLEFIELD,");
			bufSql.append(" FCHECKSTATE,");
			bufSql.append(" FCREATOR,");
			bufSql.append(" FCREATETIME,");
			bufSql.append(" FCHECKUSER,");
			bufSql.append(" FCHECKTIME,");
			bufSql.append(" FSWIFTCODE)");
			bufSql.append(" select a.FSWIFTTYPE,");
			bufSql.append(" a.FSTATUS,");
			bufSql.append(" a.FINDEX,");
			bufSql.append(" a.FCONTENT,");
			bufSql.append(" a.FOPTION,");
			bufSql.append(" a.FTAG,");
			bufSql.append(" a.FQUALIFIER,");
			bufSql.append(" a.FFIELDNAME,");
			bufSql.append(" a.FFIELDFULLNAME,");
			bufSql.append(" a.FTABLEFIELD,");
			bufSql.append(" a.FCHECKSTATE,");
			bufSql.append(" a.FCREATOR,");
			bufSql.append(" a.FCREATETIME,");
			bufSql.append(" a.FCHECKUSER,");
			bufSql.append(" a.FCHECKTIME,");
			bufSql.append(" b.fswiftcode as FSFTCODE ");
			bufSql.append(" from "+ pub.yssGetTableName("TB_DAO_SWIFTENTITY_fh") + " a ");
			bufSql.append(" left join " + pub.yssGetTableName("TB_DAO_SWIFT")+ " b on a.fswifttype=b.fswifttype ");
			sqlInfo.append(bufSql.toString());
			dbl.executeSql(bufSql.toString()); // 做历史数据转换
			bufSql.delete(0, bufSql.length());
			// 添加TB_Sys_UserRight的主键约束
			sqlInfo.append("ALTER TABLE "
							+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
							+ " ADD CONSTRAINT PK_"
							+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
							+ " PRIMARY KEY (FSTATUS,FINDEX,FCONTENT,FOPTION,FTAG,FQUALIFIER,FSWIFTCODE)");
			dbl.executeSql("ALTER TABLE "
							+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
							+ " ADD CONSTRAINT PK_"
							+ pub.yssGetTableName("TB_DAO_SWIFTENTITY ")
							+ " PRIMARY KEY (FSTATUS,FINDEX,FCONTENT,FOPTION,FTAG,FQUALIFIER,FSWIFTCODE)");

		} catch (Exception ex) {
			throw new YssException(ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}
