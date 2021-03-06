package com.yss.main.storagemanage;

import java.sql.*;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.*;

/**
 * 初始净值数据维护
 * QDV4中保2010年06月18日03_A MS01332 中保管理费计提的需求 by leeyu 20100716
 * @author liyu
 *
 */
public class NavBeginPeriodBean extends BaseDataSettingBean implements
		IDataSetting {
	private String portCode = ""; 
	private String portName = "";
    private String catCode = "";
    private String catName = "";
    private String invMgrCode = "";
    private String invMgrName = "";
    private String navDate = "";
    private String desc = "";
    private double navMarketValue = 0.0D;

    private String oldPortCode = "";
    private String oldCatCode = "";
    private String oldInvMgrCode = "";
    private String oldNavDate = "";
    private NavBeginPeriodBean filterType = null;
    
    private String recycledStr="";
    
    public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getCatCode() {
		return catCode;
	}

	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getInvMgrCode() {
		return invMgrCode;
	}

	public void setInvMgrCode(String invMgrCode) {
		this.invMgrCode = invMgrCode;
	}

	public String getInvMgrName() {
		return invMgrName;
	}

	public void setInvMgrName(String invMgrName) {
		this.invMgrName = invMgrName;
	}

	public String getNavDate() {
		return navDate;
	}

	public void setNavDate(String navDate) {
		this.navDate = navDate;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getNavMarketValue() {
		return navMarketValue;
	}

	public void setNavMarketValue(double navMarketValue) {
		this.navMarketValue = navMarketValue;
	}

	public String getOldPortCode() {
		return oldPortCode;
	}

	public void setOldPortCode(String oldPortCode) {
		this.oldPortCode = oldPortCode;
	}

	public String getOldCatCode() {
		return oldCatCode;
	}

	public void setOldCatCode(String oldCatCode) {
		this.oldCatCode = oldCatCode;
	}

	public String getOldInvMgrCode() {
		return oldInvMgrCode;
	}

	public void setOldInvMgrCode(String oldInvMgrCode) {
		this.oldInvMgrCode = oldInvMgrCode;
	}

	public String getOldNavDate() {
		return oldNavDate;
	}

	public void setOldNavDate(String oldNavDate) {
		this.oldNavDate = oldNavDate;
	}

	public NavBeginPeriodBean getFilterType() {
		return filterType;
	}

	public void setFilterType(NavBeginPeriodBean filterType) {
		this.filterType = filterType;
	}
	
	public NavBeginPeriodBean() {
	}

	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn =null;
		boolean bTrans = false;
		String sqlStr="";
		try{
			conn =dbl.loadConnection();			
			conn.setAutoCommit(bTrans);
			bTrans = true;
			sqlStr="insert into "+pub.yssGetTableName("Tb_Data_NavBeginPeriod")+
			"(FPortCode,FCatCode,FNavDate,FInvMgrCode,FPortMarketValue,FDesc,"+
			"FCheckState,FLockState,FCreator,FCreateTime) values("+
			dbl.sqlString(portCode)+","+
			dbl.sqlString(catCode==null||catCode.trim().length()==0?" ":catCode)+","+
			dbl.sqlDate(YssFun.toDate(navDate))+","+
			dbl.sqlString(invMgrCode==null||invMgrCode.trim().length()==0?" ":invMgrCode)+","+
			navMarketValue+","+
			dbl.sqlString(desc)+","+
			(pub.getSysCheckState()?"0":"1")+","+
			0+","+
			dbl.sqlString(pub.getUserCode())+","+
			dbl.sqlString(YssFun.formatDate(new java.util.Date(),"yyyy-MM-dd HH:mm:ss"))+
			")";
			dbl.executeSql(sqlStr);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException("新增初始净值数据出错",ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
		return null;
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_NavBeginPeriod"),
                "FPortCode,FCatCode,FNavDate,FInvMgrCode",
                portCode + "," + catCode + "," + navDate +"," + invMgrCode,
                oldPortCode + "," + oldCatCode + "," + oldNavDate + "," + oldInvMgrCode);
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn =null;
		boolean bTrans = false;
		String sqlStr="";
		String[] arrRowData=null;
		try{
			arrRowData = recycledStr.split("\r\n");
			conn =dbl.loadConnection();			
			conn.setAutoCommit(bTrans);
			bTrans = true;
			//------ modify by wangzuochun 2010.12.02 BUG #538 初始净值数据维护界面问题 
			for(int i = 0; i < 1; i++){
				if(arrRowData[i].length()==0) continue;
				this.parseRowStr(arrRowData[i]);
				sqlStr = "update "
						+ pub.yssGetTableName("Tb_Data_NavBeginPeriod")
						+ " set FCheckState = " + this.checkStateId 
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) 
						+ ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" 
						+ " where FPortCode=" + dbl.sqlString(portCode) 
						+ " and FCatCode=" + dbl.sqlString(catCode==null||catCode.trim().length()==0?" ":catCode) 
						+ " and FNavDate ="	+ dbl.sqlDate(YssFun.toDate(navDate))
						+ " and FInvMgrCode=" + dbl.sqlString(invMgrCode==null||invMgrCode.trim().length()==0?" ":invMgrCode);
				dbl.executeSql(sqlStr);
			}
			//----------------------BUG #538 初始净值数据维护界面问题 -------------------------//
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException("审核初始净值数据出错",ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn =null;
		boolean bTrans = false;
		String sqlStr="";
		try{
			conn =dbl.loadConnection();			
			conn.setAutoCommit(bTrans);
			bTrans = true;
			sqlStr="update "+pub.yssGetTableName("Tb_Data_NavBeginPeriod")+
			" set "+
			" FCheckState=" + checkStateId +
			" where FPortCode="+dbl.sqlString(portCode)+
			" and FCatCode="+dbl.sqlString(catCode==null||catCode.trim().length()==0?" ":catCode)+
			" and FNavDate ="+dbl.sqlDate(YssFun.toDate(navDate))+
			" and FInvMgrCode="+dbl.sqlString(invMgrCode==null||invMgrCode.trim().length()==0?" ":invMgrCode);
			dbl.executeSql(sqlStr);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException("删除初始净值数据出错",ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		Connection conn =null;
		boolean bTrans = false;
		String sqlStr="";
		String[] arrRowData=null;
		try{
			arrRowData = recycledStr.split("\r\n");
			conn =dbl.loadConnection();			
			conn.setAutoCommit(bTrans);
			bTrans = true;
			for(int i=0;i<arrRowData.length;i++){
				if(arrRowData[i].length()==0) continue;
				this.parseRowStr(arrRowData[i]);
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_Data_NavBeginPeriod")
						+ " where FPortCode=" + dbl.sqlString(portCode) 
						+ " and FCatCode=" + dbl.sqlString(catCode==null||catCode.trim().length()==0?" ":catCode) 
						+ " and FNavDate ="	+ dbl.sqlDate(YssFun.toDate(navDate))
						+ " and FInvMgrCode=" + dbl.sqlString(invMgrCode==null||invMgrCode.trim().length()==0?" ":invMgrCode);
				dbl.executeSql(sqlStr);
			}
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException("清除初始净值数据出错",ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn =null;
		boolean bTrans = false;
		String sqlStr="";
		try{
			conn =dbl.loadConnection();			
			conn.setAutoCommit(bTrans);
			bTrans = true;
			sqlStr="update "+pub.yssGetTableName("Tb_Data_NavBeginPeriod")+
			" set "+
			" FPortCode=" + dbl.sqlString(portCode)+","+
			" FCatCode=" + dbl.sqlString(catCode==null||catCode.trim().length()==0?" ":catCode)+","+
			" FNavDate=" + dbl.sqlDate(YssFun.toDate(navDate))+","+
			" FInvMgrCode=" + dbl.sqlString(invMgrCode==null||invMgrCode.trim().length()==0?" ":invMgrCode)+","+
			" FPortMarketValue=" + navMarketValue+","+
			" FDesc=" + dbl.sqlString(desc) +
			" where FPortCode="+dbl.sqlString( oldPortCode)+
			" and FCatCode="+dbl.sqlString(oldCatCode==null||oldCatCode.trim().length()==0?" ":oldCatCode)+
			" and FNavDate ="+dbl.sqlDate(YssFun.toDate(oldNavDate))+
			" and FInvMgrCode="+dbl.sqlString(oldInvMgrCode==null||oldInvMgrCode.trim().length()==0?" ":oldInvMgrCode);
			dbl.executeSql(sqlStr);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException("修改初始净值数据出错",ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
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
		ResultSet rs =null;
		String sqlStr="";
		NavBeginPeriodBean navBeginPeriod=new NavBeginPeriodBean();
		try{
			sqlStr = "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
					+ " d.FPortName,e.FCatName,f.FInvMgrName from "
					+ pub.yssGetTableName("Tb_Data_NavBeginPeriod")
					+ " a "
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode "
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
				
					 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					
		
					+ " left join (select FPortCode,FPortName from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1 ) d on a.FPortCode=d.FPortCode "
				
					
					//end by lidaolong 
					+ " left join (select FCatCode,FCatName from Tb_Base_Category) e on a.FCatCode=e.FCatCode "
					
					 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
			
					
					+ " left join (select FInvMgrCode,FInvMgrName from "
					+ pub.yssGetTableName("Tb_Para_InvestManager")
					+ " where FCheckState = 1 ) f on a.FInvMgrCode = f.FInvMgrCode "
					
					
					//end by lidaolong 
					+ buildFilterSql()
					+ " Order By a.FCheckState, a.FCreateTime desc";
			rs =dbl.openResultSet(sqlStr);
			while(rs.next()){
				navBeginPeriod.portCode = rs.getString("FPortCode");
				navBeginPeriod.portName = rs.getString("FPortName");
				navBeginPeriod.catCode = rs.getString("FCatCode")==null?" ":rs.getString("FCatCode");
				navBeginPeriod.catName = rs.getString("FCatName")==null?"":rs.getString("FCatName");
				navBeginPeriod.invMgrCode = rs.getString("FInvMgrCode")==null?" ":rs.getString("FInvMgrCode");
				navBeginPeriod.invMgrName = rs.getString("FInvMgrName")==null?" ":rs.getString("FInvMgrName");
				navBeginPeriod.navDate = YssFun.formatDate(rs.getDate("FNavDate"),"yyyy-MM-dd");
				navBeginPeriod.navMarketValue = rs.getDouble("FPortMarketValue");
				navBeginPeriod.desc = rs.getString("FDesc");
				navBeginPeriod.setRecLog(rs);
			}	         
			return navBeginPeriod.buildRowStr();
		}catch(Exception ex){
			throw new YssException("查询初始净值数据出错",ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		StringBuffer buf =new StringBuffer();
		buf.append(portCode).append("\t");
		buf.append(portName).append("\t");
		buf.append(catCode).append("\t");
		buf.append(catName).append("\t");
		buf.append(invMgrCode).append("\t");
		buf.append(invMgrName).append("\t");
		buf.append(navDate).append("\t");
		buf.append(navMarketValue).append("\t");
		buf.append(desc).append("\t");
		//edited by zhouxiang 20100919 点击报错，字段格式不正确----------------------
		buf.append(this.buildRecLog());
		//----------------------END------------------------------------------------
		return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub
		String reqAry[] = null;
	      String sTmpStr = "";
	      try {
	         if (sRowStr.trim().length() == 0) {
	            return;
	         }
	         if (sRowStr.indexOf("\r\t") >= 0) {
	            sTmpStr = sRowStr.split("\r\t")[0];
	         }
	         else {
	            sTmpStr = sRowStr;
	         }

	         recycledStr = sTmpStr;
	         
	         reqAry = sTmpStr.split("\t");
	         portCode = reqAry[0];
	         catCode = reqAry[1].length()==0?" ":reqAry[1];
	         invMgrCode = reqAry[2].length()==0?" ":reqAry[2];
	         if(YssFun.isDate(reqAry[3])){
	        	 navDate = reqAry[3];
	         }
	         if(YssFun.isNumeric(reqAry[4])){
	        	 navMarketValue = YssFun.toDouble(reqAry[4]);
	         }
	         //------ modify by wangzuochun 2010.12.02 BUG #538 初始净值数据维护界面问题
	         if (reqAry[5] != null ){
	        	 if (reqAry[5].indexOf("【Enter】") >= 0){
	        		 this.desc = reqAry[5].replaceAll("【Enter】", "\r\n");
	             }
	             else{
	            	 this.desc = reqAry[5];
	             }
	         }
	         //----------------- BUG #538 ----------------//
	         oldPortCode = reqAry[6];
	         oldCatCode = reqAry[7].length()==0?" ":reqAry[7];
	         oldInvMgrCode = reqAry[8].length()==0?" ":reqAry[8];
	         if(YssFun.isDate(reqAry[9])){
	        	 oldNavDate = reqAry[9];
	         }
	         if(YssFun.isNumeric(reqAry[10])){
	        	 this.checkStateId = YssFun.toInt(reqAry[10]);
	         }
	         super.parseRecLog();

	         if (sRowStr.indexOf("\r\t") >= 0) {

	            if (this.filterType == null) {
	               this.filterType = new NavBeginPeriodBean();
	               this.filterType.setYssPub(pub);
	            }
	            this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);

	         }
	      }
	      catch (Exception e) {
	         throw new YssException("解析初始净值请求信息出错", e);
	      }
	}

	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		ResultSet rs =null;
		String sqlStr="";
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sDateStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try{
			sHeader = getListView1Headers();
			sqlStr = "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
					+ " d.FPortName,e.FCatName,f.FInvMgrName from "
					+ pub.yssGetTableName("Tb_Data_NavBeginPeriod")
					+ " a "
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode "
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
					
					   // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				
					+ " left join (select FPortCode,FPortName from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+" where FCheckState =1 ) d on a.FPortCode=d.FPortCode "
					
					   // end by lidaolong
					+ " left join (select FCatCode,FCatName from Tb_Base_Category) e on a.FCatCode=e.FCatCode "
					
					   // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
		
					+ " left join (select FInvMgrCode,FInvMgrName from "
					+ pub.yssGetTableName("Tb_Para_InvestManager")
					+ " where FCheckState =1 ) f on a.FInvMgrCode = f.FInvMgrCode "
				
					
					//end bylidaolong
					+ buildFilterSql()
					+ " Order By a.FCheckState, a.FCreateTime desc";
			rs =dbl.openResultSet(sqlStr);
			while(rs.next()){
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);
				setResultSetAttr(rs);
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
	         
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
            this.getListView1ShowCols() + "\r\f" + sDateStr;
		}catch(Exception ex){
			throw new YssException("查询初始净值数据出错",ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
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
	
	private void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
		this.portCode = rs.getString("FPortCode");
		this.portName = rs.getString("FPortName");
		this.catCode = rs.getString("FCatCode")==null?" ":rs.getString("FCatCode");
		this.catName = rs.getString("FCatName")==null?"":rs.getString("FCatName");
		this.invMgrCode = rs.getString("FInvMgrCode")==null?" ":rs.getString("FInvMgrCode");
		this.invMgrName = rs.getString("FInvMgrName")==null?" ":rs.getString("FInvMgrName");
		this.navDate = YssFun.formatDate(rs.getDate("FNavDate"),"yyyy-MM-dd");
		this.navMarketValue = rs.getDouble("FPortMarketValue");
		this.desc = rs.getString("FDesc");
		super.setRecLog(rs);
	}
	
	private String buildFilterSql() throws YssException{
		String sFilterSql="";
		if(filterType!=null){
			sFilterSql =" where 1=1 ";
			if(filterType.portCode!=null&& filterType.portCode.trim().length()>0){
				sFilterSql += " and a.FPortCode like '"+filterType.portCode.replaceAll("'", "''")+"%'";
			}
			if(filterType.catCode!=null && filterType.catCode.trim().length()>0){
				sFilterSql += " and a.FCatCode like '"+filterType.catCode.replaceAll("'", "''")+"%'";
			}
			if(filterType.invMgrCode!=null && filterType.invMgrCode.trim().length()>0){
				sFilterSql += " and a.FInvMgrCode like '"+filterType.invMgrCode.replaceAll("'", "''")+"%'";
			}
			if(filterType.navDate!=null && !(filterType.navDate.equalsIgnoreCase("9998-12-31")||filterType.navDate.equalsIgnoreCase("1900-01-01"))){
				sFilterSql += " and a.FNavDate = "+dbl.sqlDate(filterType.navDate);
			}
		}
		return sFilterSql;
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
