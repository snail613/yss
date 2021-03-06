package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssGlobal;
import com.yss.util.YssOperCons;

/*
 * 估值增值核对类
 * @author fangjiang
 * @version 1.0, 02/06/11
 */
public class AppCheck extends BaseBean {
	
	private String curyCode = ""; //币种
	private String acctCode = ""; //科目代码
	private String acctName = ""; //科目名称
	private double gApp; //财务估值表估值增值
	private double bApp; //余额表估值增值
	private double diff; //估增不一致金额
	private double gStandardApp; //财务估值表本位币估值增值
	private double bStandardApp; //余额表本位币估值增值
	private double standardDiff; //估增本位币不一致金额
	private double flag;  //add by fangjiang 2011.08.08 STORY #1268 
		
	private ArrayList addList = new ArrayList();	
	protected Date dDate = null; //业务日期
    protected String sPortCode = null; //组合
    
	public void init(Date dDate, String portCode){
		this.dDate = dDate;
	    this.sPortCode = portCode;
	}
	
	public void addList(AppCheck appCheck) {
        this.addList.add(appCheck);
    }
	
	public void insert() throws YssException {
		String strSql = "";
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
		AppCheck appCheck = null;
		boolean bTrans = false;
		try {
			conn.setAutoCommit(false);
            bTrans = true;
            
			strSql = " delete from " + pub.yssGetTableName("tb_data_appcheck") +
					 " where FPORTCODE = " + dbl.sqlString(sPortCode) + 
		             " and FDate = " + dbl.sqlDate(dDate);
            dbl.executeSql(strSql);			
			
   			strSql = " insert into " + pub.yssGetTableName("tb_data_appcheck") +
   			         " (FDate, FPortCode, FCuryCode, FAcctCode, FAcctName, FGApp, FBApp, FDiff, FGStandardApp, FBStandardApp, FStandardDiff, FFlag) " + //modify by fangjiang 2011.08.08 STORY #1268 
   				     " values (?,?,?,?,?,?,?,?,?,?,?,?) ";
   			pst = conn.prepareStatement(strSql);

   			for (int i = 0; i < this.addList.size(); i++) {
   				appCheck = (AppCheck) addList.get(i);
        
   				pst.setDate(1, YssFun.toSqlDate(appCheck.getdDate()));
   				pst.setString(2, appCheck.getsPortCode());
   				pst.setString(3, appCheck.getCuryCode());
   				pst.setString(4, appCheck.getAcctCode()); 
   				pst.setString(5, appCheck.getAcctName()); 
   				pst.setDouble(6, appCheck.getgApp());
   				pst.setDouble(7, appCheck.getbApp());
   				pst.setDouble(8, appCheck.getDiff());
   				pst.setDouble(9, appCheck.getgStandardApp());
   				pst.setDouble(10, appCheck.getbStandardApp());
   				pst.setDouble(11, appCheck.getStandardDiff());
   				pst.setDouble(12, appCheck.getFlag()); //add by fangjiang 2011.08.08 STORY #1268 
   				
   				pst.executeUpdate();
   			}
   			
   			conn.commit();
            bTrans = false;
   		} catch (Exception e) {
   			throw new YssException("插入估值核对数据出错！", e); 
   		} finally {
   			dbl.closeStatementFinal(pst);
   			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	public Date getdDate() {
		return dDate;
	}

	public void setdDate(Date dDate) {
		this.dDate = dDate;
	}

	public String getsPortCode() {
		return sPortCode;
	}

	public void setsPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}

	public String getCuryCode() {
		return curyCode;
	}
	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}
	public String getAcctCode() {
		return acctCode;
	}
	public void setAcctCode(String acctCode) {
		this.acctCode = acctCode;
	}
	public String getAcctName() {
		return acctName;
	}
	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}
	public double getgApp() {
		return gApp;
	}
	public void setgApp(double gApp) {
		this.gApp = gApp;
	}
	public double getbApp() {
		return bApp;
	}
	public void setbApp(double bApp) {
		this.bApp = bApp;
	}
	public double getDiff() {
		return diff;
	}
	public void setDiff(double diff) {
		this.diff = diff;
	}
	public double getgStandardApp() {
		return gStandardApp;
	}
	public void setgStandardApp(double gStandardApp) {
		this.gStandardApp = gStandardApp;
	}
	public double getbStandardApp() {
		return bStandardApp;
	}
	public void setbStandardApp(double bStandardApp) {
		this.bStandardApp = bStandardApp;
	}
	public double getStandardDiff() {
		return standardDiff;
	}
	public void setStandardDiff(double standardDiff) {
		this.standardDiff = standardDiff;
	}
	public double getFlag() {
		return flag;
	}
	public void setFlag(double flag) {
		this.flag = flag;
	}
}
