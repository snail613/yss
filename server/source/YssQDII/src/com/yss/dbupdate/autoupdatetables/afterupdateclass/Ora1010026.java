package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * xuqiji 2010-02-05 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
 * 
 * @author Jason_K
 * 
 */
public class Ora1010026 extends BaseDbUpdate {
	public Ora1010026() {
		// TODO Auto-generated constructor stub
	}

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			// 转换表数据
			convertPublicTablesData(hmInfo);
			// 删除表
			dropTable();
			// add by songjie 2009.09.16 用于更新交易关联表中交易类型为认购行权的流入流出方向 改为流出
			updateRGXQOfSubTrade(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0026 更新出错！", ex);
		}
	}

	/**
	 * QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理 转换表数据
	 * 
	 * @param hmInfo
	 *            HashMap
	 * @throws YssException
	 */
	public void convertPublicTablesData(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
		try {
			doDividendData(sqlInfo);//转移分红权益数据
			doBonusShareData(sqlInfo);//转移送股权益数据
			doRightsIssueData(sqlInfo);//转移配股权益数据
		} catch (Exception ex) {
			throw new YssException(ex);
		} 
	}

	/**
	 * 转移分红权益数据 xuqiji QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
	 * 
	 * @throws YssException
	 */
	private void doDividendData(StringBuffer sqlInfo) throws YssException {
		String strSql ="";
		Connection conn = dbl.loadConnection();
		boolean bTrans = true;
		String strPKName = "";
		StringBuffer bufSql = new StringBuffer(5000);
		try {
			conn.setAutoCommit(false);
			strPKName = getIsNullPKByTableName_Ora(pub.yssGetTableNameForUpdTables("TB_Data_Dividend"));
            if (strPKName != null && strPKName.trim().length() > 0) {
                //删除约束
                dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_Data_Dividend")+" DROP CONSTRAINT " + strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在 删除备份表
            if (dbl.yssTableExist("tmp_dividend_new")) {
                this.dropTableByTableName("tmp_dividend_new");
            }
            if(!dbl.yssTableExist(pub.yssGetTableNameForUpdTables("TB_Data_Dividend") + "_bak")){
            	// 对于旧表做好备份
                bufSql.append(" create table ").append(pub.yssGetTableNameForUpdTables("TB_Data_Dividend")).append("_bak ");
                bufSql.append(" as select * from ").append(pub.yssGetTableNameForUpdTables("TB_Data_Dividend"));
                
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0,bufSql.length());
            }
            //将原表更改为备份表
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_Data_Dividend")+" RENAME TO tmp_dividend_new");
            //重新创建表
            bufSql.append(" CREATE TABLE ").append(pub.yssGetTableNameForUpdTables("TB_Data_Dividend"));
            bufSql.append(" ( ");
            bufSql.append(" FSECURITYCODE   VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FRECORDDATE     DATE          NOT NULL, ");
            bufSql.append(" FDIVDENDTYPE    NUMBER(2)     NOT NULL, ");
            bufSql.append(" FCURYCODE       VARCHAR2(20)  DEFAULT ' ' NOT NULL, ");
            bufSql.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, "); 
            bufSql.append(" FDIVIDENDDATE   DATE          NOT NULL, ");
            bufSql.append(" FDISTRIBUTEDATE DATE          NOT NULL, ");
            bufSql.append(" FAFFICHEDATE    DATE              NULL, ");
            bufSql.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL, ");
            bufSql.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL, ");
            bufSql.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDESC           VARCHAR2(100)     NULL,");
            bufSql.append(" FStartDate      DATE          DEFAULT sysdate  NOT NULL,");
            bufSql.append(" FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL");
            bufSql.append(" ) ");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());

			strSql = "INSERT INTO " + pub.yssGetTableNameForUpdTables("TB_Data_Dividend")
					+ "(FSECURITYCODE, FRECORDDATE, FDIVDENDTYPE, FCURYCODE,FPORTCODE, FASSETGROUPCODE,FDIVIDENDDATE, FDISTRIBUTEDATE, FAFFICHEDATE,FPreTaxRatio,FAFTERTAXRATIO, FROUNDCODE, FDESC,FStartDate, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME) "
					+ "SELECT FSECURITYCODE,"
					+ dbl.sqlDateAdd("FDIVIDENDDATE", "-1")
					+ ","
					+ " FDIVDENDTYPE, FCURYCODE, ' ',' ',FDIVIDENDDATE, FDISTRIBUTEDATE, FAFFICHEDATE,0,FRATIO,FROUNDCODE, FDESC,FRECORDDATE, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME "
					+ "FROM "
					+ pub.yssGetTableNameForUpdTables("TB_Data_Dividend") + "_bak "
					+ " a "
					+ "WHERE FCheckState <> 2 "
					+ "AND NOT exists (SELECT * FROM "+pub.yssGetTableNameForUpdTables("TB_Data_Dividend")+" b WHERE a.FSecurityCode = b.FSecurityCode AND a.FCuryCode = b.FCuryCode AND "
					+ dbl.sqlDateAdd("a.FDIVIDENDDATE", "-1")
					+ " = b.FRecordDate AND a.FDivdendType = b.FDivdendType)";
			sqlInfo.append(strSql).append("\n");
			dbl.executeSql(strSql);
			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //添加主键
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_Data_Dividend")+" ADD CONSTRAINT PK_" + pub.yssGetTableNameForUpdTables("TB_Data_Dividend") +
                           " PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FDIVDENDTYPE,FCURYCODE,FPORTCODE,FASSETGROUPCODE,FStartDate)");
            dropTableByTableName("tmp_dividend_new");
		} catch (Exception e) {
			throw new YssException("转移分红权益数据出错！", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}

	/**
	 * 转移送股权益数据 xuqiji QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
	 * 
	 * @throws YssException
	 */
	private void doBonusShareData(StringBuffer sqlInfo) throws YssException {
		String strSql ="";
		Connection conn = dbl.loadConnection();
		boolean bTrans = true;
		String strPKName = "";
		StringBuffer bufSql = new StringBuffer(5000);
		try {
			conn.setAutoCommit(false);
			strPKName = getIsNullPKByTableName_Ora(pub.yssGetTableNameForUpdTables("TB_DATA_BONUSSHARE"));
            if (strPKName != null && strPKName.trim().length() > 0) {
                //删除约束
                dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_DATA_BONUSSHARE")+" DROP CONSTRAINT " + strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在 删除备份表
            if (dbl.yssTableExist("tmp_bonusshare_new")) {
                this.dropTableByTableName("tmp_bonusshare_new");
            }
            if(!dbl.yssTableExist(pub.yssGetTableNameForUpdTables("TB_DATA_BONUSSHARE") + "_bak")){
            	// 对于旧表做好备份
                bufSql.append(" create table ").append(pub.yssGetTableNameForUpdTables("TB_DATA_BONUSSHARE")).append("_bak ");
                bufSql.append(" as select * from ").append(pub.yssGetTableNameForUpdTables("TB_DATA_BONUSSHARE"));
                
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0,bufSql.length());
            }
            //将原表更改为备份表
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_DATA_BONUSSHARE")+" RENAME TO tmp_bonusshare_new");
            //重新创建表
            bufSql.append(" CREATE TABLE ").append(pub.yssGetTableNameForUpdTables("TB_DATA_BONUSSHARE"));
            bufSql.append(" ( ");
            bufSql.append(" FTSECURITYCODE  VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FRECORDDATE     DATE          NOT NULL, ");
            bufSql.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FSSECURITYCODE  VARCHAR2(20)      NULL,");
            bufSql.append(" FEXRIGHTDATE    DATE          NOT NULL, ");
            bufSql.append(" FPAYDATE        DATE              NULL, ");
            bufSql.append(" FAFFICHEDATE    DATE              NULL, ");
            bufSql.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL, ");
            bufSql.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL, ");
            bufSql.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDESC           VARCHAR2(100)     NULL,");
            bufSql.append(" FStartDate      DATE          DEFAULT sysdate  NOT NULL,");
            bufSql.append(" FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL");
            bufSql.append(" ) ");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
			strSql = "INSERT INTO  " + pub.yssGetTableNameForUpdTables("TB_DATA_BONUSSHARE")
					+ "(FTSECURITYCODE, FRECORDDATE, FSSECURITYCODE, FPORTCODE, FASSETGROUPCODE,FEXRIGHTDATE, FAFFICHEDATE, FPAYDATE,FPreTaxRatio,FAFTERTAXRATIO, FROUNDCODE, FDESC, FStartDate,FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME) "
					+ "SELECT FTSECURITYCODE,"
					+ dbl.sqlDateAdd("FEXRIGHTDATE", "-1")
					+ ","
					+ "FSSECURITYCODE,' ',' ', FEXRIGHTDATE, FAFFICHEDATE, FPAYDATE,0,FRATIO,FROUNDCODE, FDESC, FRECORDDATE,FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME "
					+ "FROM "
					+ pub.yssGetTableNameForUpdTables("TB_Data_BonusShare") + "_bak "
					+ " a "
					+ "WHERE FCheckState <> 2 "
					+ "AND NOT exists (SELECT * FROM "+pub.yssGetTableNameForUpdTables("TB_Data_BonusShare")+" b WHERE a.FTSecurityCode = b.FTSecurityCode AND "
					+ dbl.sqlDateAdd("a.FEXRIGHTDATE", "-1")
					+ "= b.FRecordDate)";
			
			sqlInfo.append(strSql).append("\n");
			dbl.executeSql(strSql);
			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //添加主键
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_Data_BonusShare")+" ADD CONSTRAINT PK_" + pub.yssGetTableNameForUpdTables("TB_Data_BonusShare") +
                           " PRIMARY KEY (FTSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FStartDate)");
            dropTableByTableName("tmp_bonusshare_new");
		} catch (Exception e) {
			throw new YssException("转移送股权益数据出错！", e);
		}
	}

	/**
	 * 转移配股权益数据 xuqiji QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
	 * 
	 * @throws YssException
	 */
	private void doRightsIssueData(StringBuffer sqlInfo) throws YssException {
		String strSql ="";
		Connection conn = dbl.loadConnection();
		boolean bTrans = true;
		String strPKName = "";
		StringBuffer bufSql = new StringBuffer(5000);
		try {
			conn.setAutoCommit(false);
			strPKName = getIsNullPKByTableName_Ora(pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE"));
            if (strPKName != null && strPKName.trim().length() > 0) {
                //删除约束
                dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE")+" DROP CONSTRAINT " + strPKName);
                //删除索引
                deleteIndex(strPKName);
            }
            //如果备份表存在 删除备份表
            if (dbl.yssTableExist("tmp_rightsissue_new")) {
                this.dropTableByTableName("tmp_rightsissue_new");
            }
            if(!dbl.yssTableExist(pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE") + "_bak")){
            	// 对于旧表做好备份
                bufSql.append(" create table ").append(pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE")).append("_bak ");
                bufSql.append(" as select * from ").append(pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE"));
                
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0,bufSql.length());
            }
            //将原表更改为备份表
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE")+" RENAME TO tmp_rightsissue_new");
            //重新创建表
            bufSql.append(" CREATE TABLE ").append(pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE"));
            bufSql.append(" ( ");
            bufSql.append(" FSECURITYCODE   VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FRECORDDATE     DATE          NOT NULL, ");
            bufSql.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            bufSql.append(" FRICURYCODE     VARCHAR2(20)      NULL,");
            bufSql.append(" FTSECURITYCODE  VARCHAR2(20)      NULL, ");
            bufSql.append(" FEXRIGHTDATE    DATE          NOT NULL, ");
            bufSql.append(" FEXPIRATIONDATE DATE          NOT NULL,");
            bufSql.append(" FAFFICHEDATE    DATE              NULL, ");
            bufSql.append(" FPAYDATE        DATE          NOT NULL,");
            bufSql.append(" FBEGINSCRIDATE  DATE          NOT NULL,");
            bufSql.append(" FENDSCRIDATE    DATE          NOT NULL,");
            bufSql.append(" FBEGINTRADEDATE DATE          NOT NULL,");
            bufSql.append(" FENDTRADEDATE   DATE          NOT NULL,");
            bufSql.append(" FStartDate      DATE          DEFAULT sysdate  NOT NULL,");
            bufSql.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL, ");
            bufSql.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL, ");
            bufSql.append(" FRIPRICE        NUMBER(18,4)  NOT NULL,");
            bufSql.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FDESC           VARCHAR2(100)     NULL,");
            bufSql.append(" FCHECKSTATE     NUMBER(1)     NOT NULL,");
            bufSql.append(" FCREATOR        VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCREATETIME     VARCHAR2(20)  NOT NULL,");
            bufSql.append(" FCHECKUSER      VARCHAR2(20)      NULL,");
            bufSql.append(" FCHECKTIME      VARCHAR2(20)      NULL");
            bufSql.append(" ) ");

            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
			strSql = "INSERT INTO  " + pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE")
					+ "(FSECURITYCODE, FRECORDDATE, FRICURYCODE, FTSECURITYCODE,FPORTCODE, FASSETGROUPCODE, FEXRIGHTDATE, FEXPIRATIONDATE, FAFFICHEDATE, FPAYDATE, FBEGINSCRIDATE, FENDSCRIDATE, FBEGINTRADEDATE, FENDTRADEDATE,FStartDate,FPreTaxRatio,FAFTERTAXRATIO, FRIPRICE, FROUNDCODE, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME) "
					+ "SELECT FSECURITYCODE,"
					+ dbl.sqlDateAdd("FEXRIGHTDATE", "-1")
					+ ","
					+ " FRICURYCODE, FTSECURITYCODE,' ',' ', FEXRIGHTDATE, FEXPIRATIONDATE, FAFFICHEDATE, FPAYDATE, FBEGINSCRIDATE, FENDSCRIDATE, FBEGINTRADEDATE, FENDTRADEDATE,FRECORDDATE,0,FRATIO, FRIPRICE, FROUNDCODE, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME "
					+ "FROM "
					+ pub.yssGetTableNameForUpdTables("TB_Data_RightsIssue") + "_bak "
					+ " a "
					+ "WHERE FCheckState <> 2 "
					+ "AND NOT exists (SELECT * FROM "+pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE")+" b WHERE a.FSecurityCode = b.FSecurityCode AND "
					+ dbl.sqlDateAdd("a.FEXRIGHTDATE", "-1")
					+ " = b.FRecordDate)";
			sqlInfo.append(strSql).append("\n");
			dbl.executeSql(strSql);
			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

            //添加主键
            dbl.executeSql("ALTER TABLE "+pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE")+" ADD CONSTRAINT PK_" + pub.yssGetTableNameForUpdTables("TB_DATA_RIGHTSISSUE") +
                           " PRIMARY KEY (FSECURITYCODE, FRECORDDATE, FPORTCODE, FASSETGROUPCODE, FSTARTDATE)");
            dropTableByTableName("tmp_rightsissue_new");
		} catch (Exception e) {
			throw new YssException("转移配股权益数据出错！", e);
		}
	}

	/**
	 * 这些表都是在数据字典配置的，但是由于之前有写过国内业务相关的接口处理，所以可能表会存在
	 * 之前的国内接口是无用的，因此要更新数据字典表，所以如果原来配置的临时表要删除掉 add by songjie 2009-08-10
	 * 国内：MS00004 QDV4.1赢时胜（上海）2009年4月20日04_A
	 * 
	 * @param sTableName
	 *            String：表名
	 * @throws YssException
	 */
	protected void dropTable() throws YssException {
		if (dbl.yssTableExist("tmpSH_gh")) {
			dropTableByTableName("tmpSH_gh");
		}
		if (dbl.yssTableExist("sh_show2003")) {
			dropTableByTableName("sh_show2003");
		}
		if (dbl.yssTableExist("tmp_SHQTSL")) {
			dropTableByTableName("tmp_SHQTSL");
		}
		if (dbl.yssTableExist("TMP_ZQBD")) {
			dropTableByTableName("TMP_ZQBD");
		}
		if (dbl.yssTableExist("tmp_SHZQYE")) {
			dropTableByTableName("tmp_SHZQYE");
		}
		if (dbl.yssTableExist("tmp_SZDZ")) {
			dropTableByTableName("tmp_SZDZ");
		}
		if (dbl.yssTableExist("tmp_sjsfx")) {
			dropTableByTableName("tmp_sjsfx");
		}
		if (dbl.yssTableExist("tmp_sjsgf")) {
			dropTableByTableName("tmp_sjsgf");
		}
		if (dbl.yssTableExist("tmp_sjshb")) {
			dropTableByTableName("tmp_sjshb");
		}
		if (dbl.yssTableExist("tmp_sjshq")) {
			dropTableByTableName("tmp_sjshq");
		}
		if (dbl.yssTableExist("tmpSZ_gzlx")) {
			dropTableByTableName("tmpSZ_gzlx");
		}
		if (dbl.yssTableExist("tmpSH_gzlx")) {
			dropTableByTableName("tmpSH_gzlx");
		}
	}

	/**
	 * add by songjie 2009.09.16 用于更新交易关联表中关联类型为认购行权的流入流出标志改为-1
	 * 
	 * @param hmInfo
	 *            HashMap
	 * @throws YssException
	 */
	protected void updateRGXQOfSubTrade(HashMap hmInfo) throws YssException {
		String strSql = ""; // 用于储存sql语句
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
		try {
			conn.setAutoCommit(false);// 设置非自动提交
			bTrans = true;

			// 更新业务关联表中业务类型为认购行权的流入流出方向数据，改为流出
			strSql = " update " + pub.yssGetTableName("Tb_Data_TradeRela")
					+ " set FInOut = -1 where FRelaType = '30' ";
			sqlInfo.append(strSql).append("\n");
			dbl.executeSql(strSql);

			conn.commit();// 提交事务
			bTrans = false;
			conn.setAutoCommit(true);// 设置为自动提交
		} catch (Exception e) {
			throw new YssException("更新交易关联子表的认购行权数据出错!", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
