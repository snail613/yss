package com.yss.main.operdata.moneycontrol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import java.sql.Connection;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.cashmanage.CommandBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class AccountMessage extends BaseDataSettingBean implements IDataSetting{
	private String portCode;
	private String portName;
	private String accountCode;
	private String accountName;
	private String detailType;//每日明细表、	历史明细表、	帐户余额表
	private String beginDate;
	private String endDate;
	public void AccountMessage(){}
	
	public String getPortCode(){
		return portCode;
	}
	public String getPortName(){
		return portName;
	}
	public String getAccountCode(){
		return accountCode;
	}
	public String getAccountName(){
		return accountName;
	}
	public String getDetailType(){
		return detailType;
	}
	public String getBeginDate(){
		return beginDate;
	}
	public String getEndDate(){
		return endDate;
	}
	
	
	public void setPortCode(String portcode){
		this.portCode=portcode;
	}
	public void getPortName(String portname){
		this.portName=portname;
	}
	public void getAccountCode(String accountcode){
		this.accountCode=accountcode;
	}
	public void getAccountName(String accountname){
		this.accountName=accountname;
	}
	public void getDetailType(String detailtype){
		this.detailType=detailtype;
	}
	public void getBeginDate(String begindate){
		this.beginDate=begindate;
	}
	public void getEndDate(String enddate){
		this.endDate=enddate;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String[] arry=sRowStr.split("\t");
		this.portCode=arry[0];
		this.portName=arry[1];
		this.accountCode=arry[2];
		this.accountName=arry[3];
		this.detailType=arry[4];
		this.beginDate=arry[5];
		this.endDate=arry[6];
		super.parseRecLog();
		
	}
	
	public String addSetting() throws YssException {
		
		return "";
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void delSetting() throws YssException{
		Connection conn = null;
		ResultSet rs=null;
		boolean bTrans = false;
		String table="";
		if(this.detailType.equals("0")){	//删除当日明细对应的数据
			table="TDzZhmxb";
		}else if(this.detailType.equals("1")){
			table="TDzZhlsmxb";
		}else if(this.detailType.equals("2")){
			table="TDzZhyeb";
		}
		try{
		conn = dbl.loadConnection();
		conn.setAutoCommit(false);
		bTrans = true;
		String sql = "delete " + table + " where FFundcode="
				+ dbl.sqlString(this.portCode) + " and Faccntcode="
				+ dbl.sqlString(this.accountCode) + " and FBdate between "
				+ dbl.sqlDate(this.beginDate) + " and "
				+ dbl.sqlDate(this.endDate);
		dbl.executeSql(sql);
		conn.commit();
		bTrans = false;
		conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("删除历史明细出错", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
		throw new YssException("删除历史数据成功");
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildRowStr() throws YssException {
		
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	//依据不同的查询类型判断做出不同的数据插入处理0:每日明细表、1:历史明细表、2：帐户余额表
	//edited by zhouxiang MS01629    关于招商基金需求之简化查询资产账户步骤   2010.10.13
	public String getListViewData1()throws YssException  {
		ResultSet rs = null;
		String str = "";
		String subStr = "";
		String sqlStr = "";
		boolean bTrans = false;
		CommandBean command = new CommandBean();
		Date bdate = YssFun.parseDate(this.beginDate);
		Date eDate = YssFun.parseDate(this.endDate);
		int DateLength = YssFun.dateDiff(bdate, eDate);
		bdate=YssFun.addDay(bdate, -1);
		for (int i = 0; i < DateLength+1; i++) {
			bdate = YssFun.addDay(bdate, 1);
			command.setYssPub(pub);
			String Date = YssFun.formatDate(bdate);
			String nowdate=YssFun.formatDate(new java.util.Date());
			String Fsn = command.getFsn(Date);
			Connection conn = null;
			String fileType = "";
			try {

				if (this.detailType.equals("0")) {
					fileType = "1061";
					/*sqlStr = "select * from TdzSelinfo where FbDate="
							+ dbl.sqlDate(this.beginDate) + " and FACCNTCODE="
							+ dbl.sqlString(this.accountCode)
							+ " and Ffiletype=" + dbl.sqlString(fileType)
							+ " and FFundcode=" + dbl.sqlString(this.portCode);
					rs = dbl.openResultSet(sqlStr);
					while (rs.next()) {
						throw new YssException("该日明细指令已经发送，请重新查询!");
					}*/

				} else if (this.detailType.equals("1")) {
					fileType = "1081";
					/*sqlStr = "select * from TdzSelinfo where FbDate="
							+ dbl.sqlDate(bdate) + " and FACCNTCODE="
							+ dbl.sqlString(this.accountCode) + " and FEdate="
							+ dbl.sqlDate(this.endDate) + " and Ffiletype="
							+ dbl.sqlString(fileType) + " and FFundcode="
							+ dbl.sqlString(this.portCode);
					rs = dbl.openResultSet(sqlStr);
					while (rs.next()) {
						throw new YssException("该历史明细指令已经发送，请重新查询!");
					}
*/
				} else if (this.detailType.equals("2")) {
					fileType = "1071";
					/*sqlStr = "select * from TdzSelinfo where FbDate="
							+ dbl.sqlDate(bdate) + " and FACCNTCODE="
							+ dbl.sqlString(this.accountCode) + " and FEdate="
							+ dbl.sqlDate(this.endDate) + " and Ffiletype="
							+ dbl.sqlString(fileType) + " and FFundcode="
							+ dbl.sqlString(this.portCode);
					rs = dbl.openResultSet(sqlStr);
					while (rs.next()) {
						throw new YssException("该余额查询指令已经发送，请重新查询!");
					}*/
				}
				if (fileType.equals("1061") || fileType.equals("1071")
						|| fileType.equals("1081")) {
					conn = dbl.loadConnection();
					conn.setAutoCommit(false);
					bTrans = true;
					str = "insert into Tdzbbinfo (FSN,FDATE,FZZR,FSHR,FSH,FISSEND,FFILETYPE,FRPTTYPE)values("
							+ dbl.sqlString(Fsn)
							+ ","
							+ dbl.sqlDate(bdate)
							+ ","
							+ dbl.sqlString(this.creatorCode)
							+ ","
							+ dbl.sqlString(this.creatorCode)
							+ ",1,0,"
							+ dbl.sqlString(fileType)
							+ ","
							+ dbl.sqlString("01") + ")";
					dbl.executeSql(str);
					subStr = "insert into TdzSelinfo(FFILETYPE,FFUNDCODE,FRPTTYPE,FBDATE,FEDATE,FACCNTCODE,FACCNTNAME,FSN) values("
							+ dbl.sqlString(fileType)
							+ ","
							+ dbl.sqlString(this.portCode)
							+ ","
							+ dbl.sqlString("01")
							+ ","
							+ dbl.sqlDate(bdate)
							+ ","
							+ dbl.sqlDate(eDate)
							+ ","
							+ dbl.sqlString(this.accountCode)
							+ ","
							+ dbl.sqlString(this.accountName)
							+ ","
							+ dbl.sqlString(Fsn) + ")";
					dbl.executeSql(subStr);
					conn.commit();
					bTrans = false;
					conn.setAutoCommit(true);

				}
				
			} catch (Exception e) {
				throw new YssException("查询每日账户明细出错", e);
			} finally {
				dbl.endTransFinal(conn, bTrans);
			}
		}
		
		throw new YssException("查询成功");
		
	
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

}
