package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010034 extends BaseDbUpdate {
	public Ora1010034(){
		
	}
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			createTable();
			updateTransferOrder();//add by zhouxiang 2010.09.19  MS01628    关于招商基金需求之电子指令功能    20100919
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0034 更新出错！", ex);
		}
	}
	
	//创建表
	 public void createTable() throws YssException {
		 String sql = "";
			try {
				if (!dbl.yssTableExist("TDZACCOUNT")) {
					sql = " create table TDZACCOUNT ("
						  + "FFILETYPE   VARCHAR2(20) not null,"
						  + "FFUNDCODE   VARCHAR2(20) not null,"
						  + "FRPTTYPE    VARCHAR2(20),"
						  + "FBDATE      DATE not null,"
						  + "FEDATE      DATE not null,"
						  + "FACCTCODE   VARCHAR2(50) not null,"
						  + "FACCTNAME   VARCHAR2(50) not null,"
						  + "FACCTLEVEL  NUMBER(3) not null,"
						  + "FACCTPARENT VARCHAR2(50) not null,"
						  + "FACCTDETAIL NUMBER(3) not null,"
						  + "FACCTCLASS  VARCHAR2(50) not null,"
						  + "FBALDC      NUMBER(5) not null,"
						  + "FSN         VARCHAR2(30) not null,"
						  + "constraint PK_TDZACCOUNT primary key (FSN, FFUNDCODE, FACCTCODE))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZJJGZB")) {
					sql = "create table TDZJJGZB (" +
						  " FFILETYPE VARCHAR2(20) not null," +
						  " FFUNDCODE VARCHAR2(20) not null," +
						  " FRPTTYPE  VARCHAR2(20)," +
						  " FBDATE    DATE not null," +
						  " FEDATE    DATE not null," +
						  " FKMBM     VARCHAR2(100) not null," +
						  " FKMMC     VARCHAR2(100) not null," +
						  " FHQJG     NUMBER(20,12) not null," +
						  " FHQBZ     VARCHAR2(1) not null," +
						  " FZQSL     NUMBER(18,4) not null," +
						  " FZQCB     NUMBER(18,4) not null," +
						  " FZQSZ     NUMBER(18,4) not null," +
						  " FGZ_ZZ    NUMBER(18,4) not null," +
						  " FCB_JZ_BL NUMBER(18,8) not null," +
						  " FSZ_JZ_BL NUMBER(18,8) not null," +
						  " FISDETAIL NUMBER(1) not null," +
						  " FSN       VARCHAR2(30) not null," +
						  " constraint PK_TDZJJGZB primary key (FSN, FFUNDCODE, FKMBM))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZBALANCE")) {
					sql = "create table TDZBALANCE " +
						" (FFILETYPE  VARCHAR2(20) not null," +
						" FFUNDCODE  VARCHAR2(20) not null," +
						" FRPTTYPE   VARCHAR2(20)," +
						" FBDATE     DATE not null," +
						" FEDATE     DATE not null," +
						" FACCTCODE  VARCHAR2(50) not null," +
						" FCURCODE   VARCHAR2(3) not null," +
						" FSTARTBAL  NUMBER(19,4) not null," +
						" FDEBIT     NUMBER(19,4) not null," +
						" FCREDIT    NUMBER(19,4) not null," +
						" FENDBAL    NUMBER(19,4) not null," +
						" FBSTARTBAL NUMBER(19,4) not null," +
						" FBDEBIT    NUMBER(19,4) not null," +
						" FBCREDIT   NUMBER(19,4) not null," +
						" FBENDBAL   NUMBER(19,4) not null," +
						" FASTARTBAL NUMBER(19,4) not null," +
						" FADEBIT    NUMBER(19,4) not null," +
						" FACREDIT   NUMBER(19,4) not null," +
						" FAENDBAL   NUMBER(19,4) not null," +
						" FISDETAIL  NUMBER(3) not null," +
						" FSN        VARCHAR2(30) not null," +
						" constraint PK_TDZBALANCE primary key (FSN, FFUNDCODE, FBDATE, FACCTCODE, FCURCODE))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZRESULT")) {
					sql = "create table TDZRESULT" +
						" (FFILETYPE  VARCHAR2(4) not null," +
						" FFUNDCODE  VARCHAR2(12) not null," +
						" FRPTTYPE   VARCHAR2(2)," +
						" FBDATE     DATE," +
						" FEDATE     DATE," +
						" FRESULT    VARCHAR2(2000)," +
						" FREFNO     VARCHAR2(50)," +
						" FNOTE      VARCHAR2(2000)," +
						" FDEALER    VARCHAR2(20) not null," +
						" FTIME      DATE not null," +
						" FSN        VARCHAR2(30) not null," +
						" FXMDMB     VARCHAR2(50)," +
						" FXMDMD     VARCHAR2(50)," +
						" BJE        NUMBER(20,6)," +
						" DJE        NUMBER(20,6)," +
						" BSL        NUMBER(20,6)," +
						" DSL        NUMBER(20,6)," +
						" JSTIME     VARCHAR2(50)," +
						" CHECK_FLAG VARCHAR2(1) not null," +
						" constraint PK_TDZRESULT primary key (FSN, FFUNDCODE))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZTYPECODEPP")) {
					sql = "create table TDZTYPECODEPP" +
						" (FTGR        NVARCHAR2(50) not null," +
						" FHKTYPE     NVARCHAR2(10) not null," +
						" FHKCODE     NVARCHAR2(100) not null," +
						" FBWTYPE     NVARCHAR2(100) not null," +
						" FCHECKSTATE NUMBER(1) not null," +
						" FCREATOR    VARCHAR2(20) not null," +
						" FCHECKUSER  VARCHAR2(20) not null," +
						" FCREATETIME VARCHAR2(20) not null," +
						" FCHECKTIME  VARCHAR2(20) not null," +
						" FDESC       VARCHAR2(500)," +
						" constraint PK_TDZTYPECODEPP primary key (FTGR, FHKTYPE))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZBBINFO")) {
					sql = "create table TDZBBINFO" +
						" (FSN       VARCHAR2(30) not null," +
						" FDATE     DATE not null," +
						" FZZR      VARCHAR2(30)," +
						" FSHR      VARCHAR2(30)," +
						" FSH       NUMBER(1) not null," +
						" FISSEND   NUMBER(1) not null," +
						" FSDR      VARCHAR2(30)," +
						" FFILETYPE VARCHAR2(20) not null," +
						" FRPTTYPE  VARCHAR2(20) not null," +
						" constraint PK_TDZBBINFO primary key (FSN))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZHKRESULT")) {
					sql = "create table TDZHKRESULT" +
						" (FILE_TYPE       NVARCHAR2(4) not null," +
						" FUND_ID         NVARCHAR2(12) not null," +
						" REPORT_TYPE     NVARCHAR2(2) not null," +
						" BEGIN_DATE      DATE not null," +
						" END_DATE        DATE," +
						" TIMESTMP        DATE not null," +
						" SEQ_NO          VARCHAR2(50) not null," +
						" UNDERWRITE_CODE VARCHAR2(20)," +
						" STATUS          VARCHAR2(100) not null," +
						" INCOR_CODE      VARCHAR2(20)," +
						" CHECKER_CODE    VARCHAR2(20)," +
						" FSN             NVARCHAR2(30) not null," +
						" constraint PK_TDZHKRESULT primary key (SEQ_NO, FSN))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZSELINFO")) {
					sql = "create table TDZSELINFO" +
						" (FFILETYPE  VARCHAR2(4)," +
						" FFUNDCODE  VARCHAR2(12) not null," +
						" FRPTTYPE   VARCHAR2(2) not null," +
						" FBDATE     DATE," +
						" FEDATE     DATE," +
						" FACCNTCODE VARCHAR2(100) not null," +
						" FACCNTNAME VARCHAR2(100)," +
						" FSN        NVARCHAR2(30) not null," +
						" constraint PK_TDZSELINFO primary key (FFUNDCODE, FRPTTYPE, FACCNTCODE, FSN))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZZHMXB")) {
					sql = "create table TDZZHMXB" +
						" (FFILETYPE    VARCHAR2(4)," +
						" FFUNDCODE    VARCHAR2(12) not null," +
						" FRPTTYPE     VARCHAR2(2)," +
						" FBDATE       DATE," +
						" FEDATE       DATE," +
						" FREFNO       VARCHAR2(50)," +
						" FSEQCODE     VARCHAR2(8)," +
						" FACCNTCODE   VARCHAR2(100)," +
						" FACCNTNAME   VARCHAR2(100)," +
						" FTIME        DATE," +
						" FDEBIT       NUMBER(20,6)," +
						" FCREDIT      NUMBER(20,6)," +
						" FBALANCECUR  NUMBER(20,6)," +
						" FCOINCODE    VARCHAR2(3)," +
						" FOPPACCNT    VARCHAR2(100)," +
						" FOPPNAME     VARCHAR2(100)," +
						" FDEBITCREDIT VARCHAR2(1)," +
						" FSUMMARY     VARCHAR2(100)," +
						" FNOTE        VARCHAR2(100)," +
						" FUSAGE       VARCHAR2(100)," +
						" FBALANCE     NUMBER(20,6)," +
						" FSN          NVARCHAR2(30) not null," +
						" constraint PK_TDZZHMXB primary key (FSN, FFUNDCODE))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZZHLSMXB")) {
					sql = "create table TDZZHLSMXB" +
						" (FFILETYPE  VARCHAR2(4)," +
						" FFUNDCODE  VARCHAR2(12) not null," +
						" FRPTTYPE   VARCHAR2(2)," +
						" FBDATE     DATE," +
						" FREFNO     VARCHAR2(50)," +
						" FSEQCODE   VARCHAR2(8)," +
						" FACCNTCODE VARCHAR2(100)," +
						" FACCNTNAME VARCHAR2(100)," +
						" FDATE      DATE," +
						" FDEBIT     NUMBER(20,6)," +
						" FCREDIT    NUMBER(20,6)," +
						" FBALANCE   NUMBER(20,6)," +
						" FCOIN_CODE VARCHAR2(3)," +
						" FSUMMARY   VARCHAR2(100)," +
						" FOPP_ACCNT VARCHAR2(100)," +
						" FOPP_NAME  VARCHAR2(100)," +
						" FSN        NVARCHAR2(30) not null," +
						" constraint PK_TDZZHLSMXB primary key (FFUNDCODE, FSN))";
					dbl.executeSql(sql);
				}
				if (!dbl.yssTableExist("TDZZHYEB")) {
					sql = "create table TDZZHYEB" +
						" (FFILETYPE   VARCHAR2(4)," +
						" FFUNDCODE   VARCHAR2(12) not null," +
						" FRPTTYPE    VARCHAR2(2)," +
						" FBDATE      DATE," +
						" FEDATE      DATE," +
						" FREFNO      VARCHAR2(50)," +
						" FACCNTCODE  VARCHAR2(100)," +
						" FACCNTNAME  VARCHAR2(100)," +
						" FCOINCODE   VARCHAR2(3)," +
						" FACCNTSTATE VARCHAR2(10)," +
						" FACCNTATTR  VARCHAR2(10)," +
						" FBALANCE    NUMBER(20,6)," +
						" FTIME       DATE," +
						" FSN         NVARCHAR2(30) not null," +
						" constraint PK_TDZZHYEB primary key (FFUNDCODE, FSN))";
					dbl.executeSql(sql);
				}
			}catch (Exception e) {
				throw new YssException("版本1.0.1.0034 创建表结构出错！", e);
			}
	 }
	 /**
		 * MS01628    关于招商基金需求之电子指令功能    20100928
		 * @方法名：updateTransferOrder 根据套账号新建电子划拨表
		 * @author  by zhouxiang 
		 * @throws YssException 
		 * @throws SQLException 
		 * @返回类型：void
		 * @说明：TODO
		 */
	private void updateTransferOrder() throws SQLException, YssException {
		String sql = "";
		ResultSet rs = null;
		sql = "select * from " + pub.yssGetTableName("tb_vch_bookset")
				+ " where fcheckstate=1";
		try {
			rs = dbl.openResultSet(sql);
			while (rs.next()) {
				String tableName = "A" + rs.getString("FBOOKSETCODE")
						+ "jjhkzl";
				if (!dbl.yssTableExist(tableName)) {
					sql = "Create table "
							+ tableName
							+ "(FZLDATE	DATE	not null,"
							+ " FHKDATE	DATE	not null,"
							+ " FNUM		VARCHAR2(20)	not null,"
							+ " FDZDATE	DATE	not null,"
							+ " FHKREN	NVARCHAR2(100)	not null,"
							+ " FHKBANK	NVARCHAR2(100)	not null,"
							+ " FHKACCT	NVARCHAR2(100)	not null,"
							+ " FHKJE		NUMBER(18,4)	not null,"
							+ " FHKREMARK	NVARCHAR2(200)	not null,"
							+ " FSKREN	NVARCHAR2(100)	not null,"
							+ " FSKBANK	NVARCHAR2(100)	not null,"
							+ " FSKACCT	NVARCHAR2(200)	not null,"
							+ " FSKYT		NVARCHAR2(200)	not null,"
							+ " FDELBZ	CHAR(1)	,				 "
							+ " FZLTYPE	NUMBER(1)		not null,"
							+ " FHKTYPE	VARCHAR2(10)	not null,"
							+ " FHKTYPE2	VARCHAR2(50),			 "
							+ " FSN		NVARCHAR2(30),			 "
							+ " SEQ_NO	NVARCHAR2(50)	not null,"
							+ " RESULT	NVARCHAR2(30),			 "
							+ " REMARK	NVARCHAR2(200),			 "
							+ " CHECKER_CODE	NVARCHAR2(30),		 "
							+ " FPK_BOOKMARK	NVARCHAR2(100),		 "
							+ " TIMESTMP	NVARCHAR2(50)	not null,"
							+ " OPERATION_TYPE	NVARCHAR2(50)	not null,"
							+ " FYHSN	NVARCHAR2(50)	,"
							+ " FSH	NVARCHAR2(30)	not null,"
							+ " FZZR	VARCHAR2(30)	not null,"
							+ " FCHK	VARCHAR2(30)	not null,"
							+ " FHKREMARKN	NVARCHAR2(200)	,"
							+ "constraint PK_"+tableName+ " primary key (FZLDATE, FNUM, FZLTYPE, FSH))";

					dbl.executeSql(sql);
				}

			}
		} catch (Exception e) {
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}
