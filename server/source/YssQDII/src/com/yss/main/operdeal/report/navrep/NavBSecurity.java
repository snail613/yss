package com.yss.main.operdeal.report.navrep;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import com.yss.main.operdeal.report.navrep.BaseNavRep;
import com.yss.main.operdeal.report.navrep.pojo.NavRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class NavBSecurity extends BaseNavRep {
	private java.util.Date dDate = null;
	private String portCode = "";
	private int grade = 0;
	private String invMgrCode = "";
	public NavBSecurity(){
		
	}
	//edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A 增加投资经理参数
	public void insertMergeData(String typeCode, int Grade, String sPortCode, Date date, String InvMgrCode) throws YssException{
		this.portCode = sPortCode;
		this.dDate = date;
		this.grade = Grade;
		this.invMgrCode = InvMgrCode;
		for (int i = Grade; i>1; i--){
			doInsertTable(typeCode,i);
		}
	}
	//edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A 增加投资经理参数
	public void deleteMergeData(String sPortCode, Date date,String invMgrCode) throws YssException{
		String deleteSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try{
        	deleteSql = "delete from " + pub.yssGetTableName("tb_data_NavData") + " where fretypecode like '%-%'" + 
        			" and FNAVDate = " + dbl.sqlDate(date) + 
        			" and FPortCode = " + dbl.sqlString(sPortCode) + 
        			" and FInvMgrCode = " + dbl.sqlString(invMgrCode.length() > 0 ? invMgrCode : "total");
        	conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(deleteSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch (Exception e){
        	throw new YssException("系统执行删除净值表数据时出错！", e);
        }finally{
        	dbl.endTransFinal(conn, bTrans);
        }
	}
	
	private void doInsertTable(String typeCode, int Grade) throws YssException{
		String orderSql = "";
		String sGroupSql = "";
		String strSql = "";
		ResultSet rs = null;
		NavRepBean navRep = null;
		ArrayList valCashBeans = null;
		String[] gradeTypes = null;
		try{
			valCashBeans = new ArrayList();
			orderSql = doBuildOrderStr(Grade) + " as FOrderCode, ";
			sGroupSql = doBuildGroupStr(Grade);
			strSql = "select a.*, b.fkeyname as FKeyName, b.fkeycode as FkeyCode from(select " + orderSql + sGroupSql;
			if(typeCode.equalsIgnoreCase("Security")){
				strSql = strSql + ",Fportcode, fnavdate,FInvMgrCode, sum(fmarketvalue*1) as fmarketvalue," + dbl.sqlString(Grade-1 +"") + " as fdetail,'汇总：' as fcurycode," +
						"sum(fcost*1) as Fcost, sum(FMVVAlue*1) as FMVVAlue, sum(FFXValue*1) as FFXValue,sum(FPortCost*1) as FPortCost," +
						"sum(FPortMVValue*1) as FPortMVvalue,sum(Fportmarketvalue*1) as Fportmarketvalue,sum(FSParAmt*1) as FSParAmt,";
			}else{
				strSql = strSql + ",Fportcode, fnavdate,FInvMgrCode, sum(fmarketvalue*finout) as fmarketvalue," + dbl.sqlString(Grade-1 +"") + " as fdetail,'汇总：' as fcurycode," +
				"sum(fcost*finout) as Fcost, sum(FMVVAlue*finout) as FMVVAlue, sum(FFXValue*finout) as FFXValue,sum(FPortCost*finout) as FPortCost," +
				"sum(FPortMVValue*finout) as FPortMVvalue,sum(Fportmarketvalue*finout) as Fportmarketvalue,sum(FSParAmt*finout) as FSParAmt,";
			}
			if(Grade == this.grade){
				strSql = strSql + dbl.sqlString(typeCode) + dbl.sqlJoinString().trim() + dbl.sqlString("-") + dbl.sqlJoinString().trim() + "fgradetype" + (Grade+"") + " as fretypecode from " + pub.yssGetTableName("tb_data_navdata");
			}else if (Grade != this.grade){
				strSql = strSql + " fretypecode from " + pub.yssGetTableName("tb_data_navdata");
			}
			strSql += " where  fnavdate = " + dbl.sqlDate(this.dDate) +
					" and fdetail = " + dbl.sqlString(Grade + "") + " and FportCode = " + dbl.sqlString(this.portCode) +
					" and FInvMgrCode = " + dbl.sqlString(this.invMgrCode.trim().length() > 0 ? this.invMgrCode : "total");
			if(Grade == this.grade){
				strSql += " and FretypeCode = " + dbl.sqlString(typeCode);
			}else if (Grade != this.grade){
				strSql += " and FretypeCode like " + dbl.sqlString(typeCode) + dbl.sqlJoinString().trim() + "'-%'";
			}
			strSql += " group by " + sGroupSql + ",Fportcode,fnavdate,FInvMgrCode";
			if (Grade != this.grade){
				strSql += ",fretypecode";
			}
			strSql += ") a left join (select fkeyname,fkeycode,fnavdate,FportCode,FretypeCode,fgradetype" + (Grade-1+"") + ",FInvMgrCode from " +
					pub.yssGetTableName("tb_data_navdata") + " where fdetail = " + dbl.sqlString(Grade-1+"") +
					" group by fkeyname,fkeycode,fnavdate,FportCode,FInvMgrCode,FretypeCode,fgradetype" + (Grade-1+"") + ")b on" +
					" a.fnavdate = b.fnavdate" +
					" and a.FportCode = b.FportCode" +
					" and a.fgradetype" + (Grade-1+"") + " = b.fgradetype" + (Grade-1+"") + 
					" and a.FInvMgrCode = b.FInvMgrCode";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setSparAmt(rs.getDouble("FSParAmt"));
                navRep.setKeyCode(rs.getString("FkeyCode") == null ?
                        " " : rs.getString("FkeyCode"));
                navRep.setKeyName(rs.getString("FKeyName") == null ?
                        " " : rs.getString("FKeyName"));
                navRep.setDetail(rs.getInt("fdetail"));
                navRep.setReTypeCode(rs.getString("fretypecode"));
                navRep.setCuryCode("汇总：");
                navRep.setBookCost(rs.getDouble("FCost"));
                navRep.setPayValue(rs.getDouble("FMVVAlue"));
                navRep.setMarketValue(rs.getDouble("fmarketvalue"));
                navRep.setPortBookCost(rs.getDouble("FPortCost"));
                navRep.setPortexchangeValue(rs.getDouble("FFXValue"));
                navRep.setPortPayValue(rs.getDouble("FPortMVvalue"));
                navRep.setPortMarketValue(rs.getDouble("Fportmarketvalue"));
                gradeTypes = navRep.getOrderKeyCode().split("##");
                switch (gradeTypes.length) {
                    case 1:
                        navRep.setGradeType1(gradeTypes[0]);
                        break;
                    case 2:
                        navRep.setGradeType1(gradeTypes[0]);
                        navRep.setGradeType2(gradeTypes[1]);
                        break;
                    case 3:
                        navRep.setGradeType1(gradeTypes[0]);
                        navRep.setGradeType2(gradeTypes[1]);
                        navRep.setGradeType3(gradeTypes[2]);
                        break;
                }
                navRep.setInvMgrCode(rs.getString("FInvMgrCode"));
                navRep.setInOut(1);
                valCashBeans.add(navRep);
			}
			insertTable(valCashBeans);
		}catch (Exception e) {
            throw new YssException("生成B股汇总数据出现异常！" + "\n", e);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	private String doBuildOrderStr(int Grade){
		String reStr = "";
		for (int i = 1; i < Grade; i++) {
            reStr += "fgradetype" + (i+"") + dbl.sqlJoinString().trim() + dbl.sqlString("##") + dbl.sqlJoinString().trim();
        }
        if (reStr.length() > 0) {
            reStr = reStr.substring(0, reStr.length() - 8);
        }
        return reStr;
	}
	
	private String doBuildGroupStr(int Grade){
		String reStr = "";
		if(Grade == this.grade){
			Grade += 1;
		}
		for (int i = 1; i < Grade; i++) {
            reStr += "fgradetype" + (i+"") + ",";
        }
		if(reStr.length()>0){
			reStr = reStr.substring(0,reStr.length()-1);
		}
        return reStr;
	}
	
//	private void doTotalNav(String FCuryCode) throws YssException{
//		ResultSet rs = null;
//		NavRepBean navRep = null;
//		ArrayList valCashBeans = null;
//		String strSql = "";
//		try{
//			strSql = ""
//		}
//	}
}
