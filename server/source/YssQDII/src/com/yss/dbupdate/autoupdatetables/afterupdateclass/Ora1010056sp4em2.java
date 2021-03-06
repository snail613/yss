package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * add by songjie 2012.12.24 
 * BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
 * @author 宋洁
 *
 */
public class Ora1010056sp4em2 extends BaseDbUpdate{
	public static final String lockTable = "0";
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			synchronized(this.lockTable){
				updateInfo(hmInfo);
			}
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0056sp4em2更新出错！", ex);
		}
	}
	
	private void updateInfo(HashMap hmInfo)throws YssException {
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		StringBuffer buff = null;
		ResultSet rs = null;
		String duration = "";//表类型
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try{
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("tb_data_PreBonusShare"));
            buff.append(" ( ");
            buff.append(" FTSECURITYCODE  VARCHAR2(50)  NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FSSECURITYCODE  VARCHAR2(50)      NULL, ");
            buff.append(" FEXRIGHTDATE    DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NULL,");
            buff.append(" FPAYDATE        DATE              NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FCostOddRate    NUMBER(7,6)   DEFAULT 0     NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_PreBonusShare"));
            buff.append(" PRIMARY KEY (FTSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FPAYDATE) ");
            //-----------------------------------
            buff.append(" ) ON COMMIT PRESERVE ROWS");
			
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_PreBonusShare".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_PreBonusShare"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("tb_data_PreBonusShare")));
        			}

                    updTables.append(pub.yssGetTableName("tb_data_PreBonusShare"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("tb_data_PreBonusShare"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0, buff.length());
        	
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("Tb_Data_PreCashConsider"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FExRightDate    DATE          NOT NULL,");
            buff.append(" FPayDate        DATE          NOT NULL,");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("Tb_Data_PreCashConsider"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("Tb_Data_PreCashConsider".toUpperCase()));
        	
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_PreCashConsider"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("Tb_Data_PreCashConsider")));
        			}

                    updTables.append(pub.yssGetTableName("Tb_Data_PreCashConsider"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("Tb_Data_PreCashConsider"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0, buff.length());
        	
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("tb_data_PreDeflationBonus"));
            buff.append(" ( ");
            buff.append(" FTSECURITYCODE  VARCHAR2(50)  NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FSSECURITYCODE  VARCHAR2(50)      NULL, ");
            buff.append(" FEXRIGHTDATE    DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NULL,");
            buff.append(" FPAYDATE        DATE              NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_PreDeflation"));
            buff.append(" PRIMARY KEY (FTSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FPAYDATE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_PreDeflationBonus".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_PreDeflationBonus"))) { 
        				dbl.executeSql(" drop table " + pub.yssGetTableName("tb_data_PreDeflationBonus"));
        			}

                    updTables.append(pub.yssGetTableName("tb_data_PreDeflationBonus"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("tb_data_PreDeflationBonus"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0, buff.length());
        	
       	 	buff=new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE  ").append(pub.yssGetTableName("tb_data_Predividend"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FDIVDENDTYPE    NUMBER(2)     NOT NULL, ");
            buff.append(" FCURYCODE       VARCHAR2(20)  DEFAULT ' ' NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FDIVIDENDDATE   DATE          NOT NULL,");
            buff.append(" FDISTRIBUTEDATE DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_Predividend"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FDIVDENDTYPE,FCURYCODE,FPORTCODE,FASSETGROUPCODE,FDISTRIBUTEDATE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_Predividend".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_Predividend"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("tb_data_Predividend")));
        			}

                    updTables.append(pub.yssGetTableName("tb_data_Predividend"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("tb_data_Predividend"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
            buff.delete(0,buff.length());
        	
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FTSecurityCode  VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FExRightDate    DATE          NOT NULL,");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FAccountType    NUMBER(2)     NOT NULL,");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
            
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("Tb_Data_PreMayApartBond".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_PreMayApartBond"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("Tb_Data_PreMayApartBond")));
        			}

                    updTables.append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0, buff.length());
        	
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("tb_data_PreRightsissue"));
            buff.append(" ( ");
            buff.append(" FSECURITYCODE   VARCHAR2(50)  NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FRICURYCODE     VARCHAR2(20)      NULL, ");
            buff.append(" FTSECURITYCODE  VARCHAR2(50)      NULL, ");
            buff.append(" FEXRIGHTDATE    DATE          NOT NULL,");
            buff.append(" FEXPIRATIONDATE DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NOT NULL,");
            buff.append(" FPAYDATE        DATE          NOT NULL,");
            buff.append(" FBEGINSCRIDATE  DATE          NOT NULL,");
            buff.append(" FENDSCRIDATE    DATE          NOT NULL,");
            buff.append(" FBEGINTRADEDATE DATE          NOT NULL,");
            buff.append(" FENDTRADEDATE   DATE          NOT NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FRIPRICE        NUMBER(18,4)  NOT NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append("FTradeCode     VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_PreRightsissue"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FPAYDATE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_PreRightsissue".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_PreRightsissue"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("tb_data_PreRightsissue")));
        			}

                    updTables.append(pub.yssGetTableName("tb_data_PreRightsissue"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("tb_data_PreRightsissue"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0, buff.length());
        	
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_Integrated")){
				int maxNum = 0;
				strSql = " select max(FSubNum) as FSubNum from " + pub.yssGetTableName("Tb_Data_Integrated") + 
				" where subStr(FNum,0,1) <> 'E' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FSubNum") != null && YssFun.isNumeric(rs.getString("FSubNum"))){
						maxNum = Integer.parseInt(rs.getString("FSubNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum ++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_Integrated " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Data_Integrated");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_CashPayRec")){
				int maxNum = 0;
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_CashPayRec") + 
				" where SUBSTR(FNum,0,3) <> 'SRP' and SUBSTR(FNum,0,3) <> 'CRP' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_CashPayRec " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Data_CashPayRec");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_INVESTPAYREC")){
				int maxNum = 0;
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_INVESTPAYREC") + 
				" where SUBSTR(FNum,0,3) <> 'IPR' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_INVESTPAYREC " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Data_INVESTPAYREC");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_SecRecPay")){
				int maxNum = 0;
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_SecRecPay") + 
				" where subStr(FNum,0,3) <> 'SRP' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_SecRecPay " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Data_SecRecPay");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Vch_Data")){
				int maxNum = 0;
				strSql = " select max(FVchNum) as FNum from " + pub.yssGetTableName("Tb_Vch_Data") + 
				" where subStr(FVchNum,0,1) <> 'T' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Vch_Data " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Vch_Data");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
		}catch(Exception e){
			throw new YssException("1.0.1.0056sp4em2更新表数据出错！",e);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		finally{
			dbl.closeResultSetFinal(rs);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
	}
}
