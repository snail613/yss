package com.yss.main.operdeal.report.stockcheck;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.yss.base.*;
import com.yss.dsub.YssPub;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.operdeal.report.navrep.pojo.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

public class CtlStockCheck extends BaseAPOperValue implements
		IClientOperRequest {

	public String checkRequest(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String doOperation(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

//	public void setYssPub(YssPub pub) {
//		// TODO Auto-generated method stub
//
//	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		String[] confirms = null;
        String resultStr = "";
        confirms = sType.split("\b\f\b");
        if (confirms[2].equalsIgnoreCase("check")) {
            resultStr = doStockCheck(confirms[0],confirms[1]);
        } else if (confirms[2].indexOf("confirm")>=0){
        	resultStr = doConfirmed(confirms[0],confirms[1],confirms[2]);
        } else if (confirms[2].equalsIgnoreCase("stockInfo")){
        	resultStr = getConfirmInfo(confirms[0],confirms[1]);
        }
        return resultStr;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub

	}
	
	private String doStockCheck(String checkDate, String portCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		PreparedStatement stm = null;
		Connection conn = dbl.loadConnection();//新建连接
        boolean bTrans = false;
        boolean flag = false;
        try{
        	conn.setAutoCommit(false);//设置手动提交事务
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("TB_DATA_STOCKCHECK") + " where fportcode = " + dbl.sqlString(portCode) +
            		" and FCHECKDATE = " + dbl.sqlDate(checkDate);
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("TB_DATA_STOCKCHECK") + " (FPORTCODE,FSECURITYCODE,FSECURITYCOUNTA,FSECURITYCOUNTB,FCHECKDATE,FCHECKRUSULT,FCHECKUSER)" +
					" values(?,?,?,?,?,?,?)";
            stm = dbl.openPreparedStatement(strSql);
            strSql = "select a.fportcode as FPortCode,a.FKcsl as FSECURITYCOUNTA,a.Fdate as FCHECKDATE,a.fzqdm as fsecuritycode,b.FStorageAmount as FSECURITYCOUNTB from " +
            		pub.yssGetTableName("Tb_JjHZDZ") + " a left join (select fsecuritycode,sum(FStorageAmount) as FStorageAmount from " + pub.yssGetTableName("tb_stock_security") +
            		" where fportcode = " + dbl.sqlString(portCode) + " and fcheckstate = '1' and Fstoragedate = " + dbl.sqlDate(checkDate) +
            		" group by fsecuritycode) b on  a.fzqdm = b.fsecuritycode where a.fportcode = " + dbl.sqlString(portCode) +
            		" and a.Fdate = " + dbl.sqlDate(checkDate);
            rs = dbl.openResultSet(strSql);
        	while (rs.next()){
        		flag = true;
        		stm.setString(1, portCode);
        		stm.setString(2, rs.getString("fsecuritycode") == null ? " " : rs.getString("fsecuritycode"));
        		stm.setDouble(3, rs.getDouble("FSECURITYCOUNTA"));
        		stm.setDouble(4, rs.getDouble("FSECURITYCOUNTB"));
        		stm.setDate(5, YssFun.toSqlDate(checkDate));
        		if(rs.getDouble("FSECURITYCOUNTA") == rs.getDouble("FSECURITYCOUNTB")){
        			stm.setInt(6, 0);
        		}else{
        			stm.setInt(6, 1);
        		}
        		stm.setString(7, pub.getUserCode());
        		stm.addBatch();
        		
        	}
        	if(!flag){
        		throw new YssException(portCode + "组合无" + checkDate + "相关的对账数据！");
        	}
    		stm.executeBatch();
            conn.commit();//提交事务
            bTrans = false;
            conn.setAutoCommit(true);//设置为自动提交事务
            return "1";
        }catch (Exception e) {
            throw new YssException("处理持仓核对出错！", e);
        } finally {
            dbl.closeStatementFinal(stm);
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	private String doConfirmed(String checkDate, String portCode, String param) throws YssException {
		String strResult = "";
		String strSql = "";
		ResultSet rs = null;
		PreparedStatement stm = null;
		Connection conn = dbl.loadConnection();//新建连接
		//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dDate = YssFun.toDate(checkDate);
        boolean bTrans = false;
        String exceptiion = "";
        int days = 1;
        CtlPubPara ctlPubPara = new CtlPubPara();
        ctlPubPara.setYssPub(pub);
        days = ctlPubPara.getPlanDate() == 0 ? 1 : ctlPubPara.getPlanDate();
		try{
			conn.setAutoCommit(false);//设置手动提交事务
            bTrans = true;
			if(param.equalsIgnoreCase("confirm")){
				exceptiion = "确认持仓核对数据出错！";
				strSql = "insert into " + pub.yssGetTableName("TB_DATA_STOCKCHECK") + " (FPORTCODE,FSECURITYCODE,FSECURITYCOUNTA," +
						" FSECURITYCOUNTB,FCHECKDATE,FCHECKINGDATE,FPLANDATE,FCHECKRUSULT,FCHECKUSER)" +
						" values(?,?,?,?,?,?,?,?,?)";
				stm = dbl.openPreparedStatement(strSql);
				strSql = "select * from " + pub.yssGetTableName("TB_DATA_STOCKCHECK") + " where FCHECKDATE = " + dbl.sqlDate(dDate) +
						" and FPORTCODE = " + dbl.sqlString(portCode) + " and FCHECKRUSULT <> 0";
				rs = dbl.openResultSet(strSql);
				stm.setString(1, portCode);
				stm.setString(2, "0");
				stm.setDouble(3, 0);
				stm.setDouble(4, 0);
				stm.setDate(5, YssFun.toSqlDate(dDate));
				stm.setDate(6, YssFun.toSqlDate(pub.getUserDate()));
				stm.setDate(7, YssFun.toSqlDate(YssFun.addDay(dDate, days)));
				if(rs.next()){
					stm.setInt(8, 1);
				}else{
					stm.setInt(8, 0);
				}
				stm.setString(9, pub.getUserCode());
				stm.addBatch();
				stm.executeBatch();
				strResult = "confirm";
			}else if (param.equalsIgnoreCase("unconfirm")){
				exceptiion = "反确认持仓核对数据出错！";
				strSql = "delete from " + pub.yssGetTableName("TB_DATA_STOCKCHECK") + " where fportcode = " + dbl.sqlString(portCode) +
        			" and FCHECKDATE = " + dbl.sqlDate(dDate) + " and FSECURITYCODE = '0'";
				dbl.executeSql(strSql);
				strResult = "unconfirm";
			}
			conn.commit();//提交事务
            bTrans = false;
            conn.setAutoCommit(true);//设置为自动提交事务
            return strResult;
		}catch (Exception e) {
            throw new YssException(exceptiion, e);
        } finally {
            dbl.closeStatementFinal(stm);
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	private String getConfirmInfo(String checkDate, String portCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String strResult = "";
		int i = 0;
		try {
			strResult = "unconfirm";//未核对
			strSql = "select * from " + pub.yssGetTableName("TB_DATA_STOCKCHECK") + " where fportcode = " + dbl.sqlString(portCode) +
			" and FCHECKDATE = " + dbl.sqlDate(checkDate);
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				if(i == 0){
					strResult = "checked";//已核对
					i++;
				}
				if(rs.getString("FSECURITYCODE").equalsIgnoreCase("0")){
					strResult = "confirm";//已确认
				}
			}
			return strResult;
		}catch (Exception e) {
            throw new YssException("获取持仓核对确认信息出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		
	}

}
