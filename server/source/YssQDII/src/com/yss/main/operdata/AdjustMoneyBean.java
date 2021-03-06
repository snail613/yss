package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.parasetting.AssetStorageCfgBean;
import com.yss.main.storagemanage.CostStorageBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/***************************************
 * MS01669 QDV4工银2010年8月25日05_A
 * 
 * 调整金额业务Bean
 * 
 * @author benson
 * 
 */
public class AdjustMoneyBean extends BaseDataSettingBean implements IDataSetting {

	//============================================================================
	private Date dAdjustDate = null;
	private Date dAdjustDate_begin = null;
	private Date dAdjustDate_end = null;
	private String sPortCode = "";
	private String sPortName = "";
	private String sCashCode = "";
	private String sCashName = "";
	private String sCuryCode = "";
	private String sCuryName = "";
	private double dAdjustMoney = 0;
	private String sDesc = "";
	private String sIsOnlyColumns = "0";
	private Date dOldAdjustDate = null;
	private String sOldPortCode = "";
	private String sOldCashCode = "";
	private AdjustMoneyBean filterType = null;
	
	private String sRecycled = ""; //回收站字符串
	
	public Date getdAdjustDate() {
		return dAdjustDate;
	}

	public void setdAdjustDate(Date dAdjustDate) {
		this.dAdjustDate = dAdjustDate;
	}

	public Date getdAdjustDate_begin() {
		return dAdjustDate_begin;
	}

	public void setdAdjustDate_begin(Date dAdjustDateBegin) {
		dAdjustDate_begin = dAdjustDateBegin;
	}

	public Date getdAdjustDate_end() {
		return dAdjustDate_end;
	}

	public void setdAdjustDate_end(Date dAdjustDateEnd) {
		dAdjustDate_end = dAdjustDateEnd;
	}

	public String getsPortCode() {
		return sPortCode;
	}

	public void setsPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}

	public String getsPortName() {
		return sPortName;
	}

	public void setsPortName(String sPortName) {
		this.sPortName = sPortName;
	}

	public String getsCashCode() {
		return sCashCode;
	}

	public void setsCashCode(String sCashCode) {
		this.sCashCode = sCashCode;
	}

	public String getsCashName() {
		return sCashName;
	}

	public void setsCashName(String sCashName) {
		this.sCashName = sCashName;
	}

	public String getsCuryCode() {
		return sCuryCode;
	}

	public void setsCuryCode(String sCuryCode) {
		this.sCuryCode = sCuryCode;
	}

	public String getsCuryName() {
		return sCuryName;
	}

	public void setsCuryName(String sCuryName) {
		this.sCuryName = sCuryName;
	}

	public double getdAdjustMoney() {
		return dAdjustMoney;
	}

	public void setdAdjustMoney(double dAdjustMoney) {
		this.dAdjustMoney = dAdjustMoney;
	}

	public String getsDesc() {
		return sDesc;
	}

	public void setsDesc(String sDesc) {
		this.sDesc = sDesc;
	}

	public String getsIsOnlyColumns() {
		return sIsOnlyColumns;
	}

	public void setsIsOnlyColumns(String sIsOnlyColumns) {
		this.sIsOnlyColumns = sIsOnlyColumns;
	}

	public Date getsOldAdjustDate() {
		return dOldAdjustDate;
	}

	public void setsOldAdjustDate(Date dOldAdjustDate) {
		this.dOldAdjustDate = dOldAdjustDate;
	}

	public String getsOldPortCode() {
		return sOldPortCode;
	}

	public void setsOldPortCode(String sOldPortCode) {
		this.sOldPortCode = sOldPortCode;
	}

	public String getsOldCashCode() {
		return sOldCashCode;
	}

	public void setsOldCashCode(String sOldCashCode) {
		this.sOldCashCode = sOldCashCode;
	}

	public AdjustMoneyBean getFilter() {
		return filterType;
	}

	public void setFilter(AdjustMoneyBean filter) {
		this.filterType = filter;
	}

	
	//===========================================================================
	
	
	public AdjustMoneyBean(){
		
	}
	
	//============================================================================
	
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		String sTmpStr = "";
		
		//1. 从菜单到ListView页面时,是没有传任何参数的,如果没有传任何参数,则直接返回
		if(sRowStr.length()==0){
			return ;
		}
		//2. 添加了筛选条件
		 if (sRowStr.indexOf("\r\t") >= 0) {
             sTmpStr = sRowStr.split("\r\t")[0];
         } else {
             sTmpStr = sRowStr;
         }
		 //回收站批量删除
         this.sRecycled = sRowStr;

         reqAry = sTmpStr.split("\t");
         
         this.dAdjustDate = YssFun.parseDate(reqAry[0]);
         this.dAdjustDate_begin = YssFun.parseDate(reqAry[1]);
         this.dAdjustDate_end = YssFun.parseDate(reqAry[2]);
         this.sCashCode = reqAry[3];
         this.sCuryCode = reqAry[4];
         this.sPortCode = reqAry[5];
         if(YssFun.isNumeric(reqAry[6])){
        	 this.dAdjustMoney = YssFun.toNumber(reqAry[6]);
         }
         if(reqAry[7].indexOf("【Enter】")==-1){
        	 this.sDesc = reqAry[7];
         }else{
        	 this.sDesc = reqAry[7].replaceAll("【Enter】", "\r\n");
         }
         
         this.checkStateId = YssFun.toInt(reqAry[8]);
         this.sOldCashCode = reqAry[9];
         this.sOldPortCode = reqAry[10];
         this.dOldAdjustDate = YssFun.parseDate(reqAry[11]);
         this.sIsOnlyColumns = reqAry[12];
         
         super.parseRecLog();
         if (sRowStr.indexOf("\r\t") >= 0) {
             if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                 if (this.filterType == null) {
                     this.filterType = new AdjustMoneyBean();
                     this.filterType.setYssPub(pub);
                 }
                 this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
             }
         }
	}
	
	public String buildRowStr() throws YssException {
		    StringBuffer buf = new StringBuffer();
		    
	        buf.append(YssFun.formatDate(this.dAdjustDate)).append("\t");
	        buf.append(this.sCashCode).append("\t");
	        buf.append(this.sCashName).append("\t");
	        buf.append(this.sCuryCode).append("\t");
	        buf.append(this.sCuryName).append("\t");
	        buf.append(this.sPortCode).append("\t");
	        buf.append(this.sPortName).append("\t");
	        buf.append(this.dAdjustMoney).append("\t");
	        buf.append(this.sDesc).append("\t");
	        buf.append(super.buildRecLog());
	        return buf.toString();
	}
	
	 /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
         String sResult = "";
        if (this.filterType != null) {
            if(YssFun.formatDate(this.filterType.dAdjustDate_begin).equalsIgnoreCase("9998-12-31")
            		&&YssFun.formatDate(this.filterType.dAdjustDate_end).equalsIgnoreCase("9998-12-31")){
            	//1. 未勾选日期，则所有日期都查询出来
            	sResult = " where 1=1";
            }else if(YssFun.formatDate(this.filterType.dAdjustDate_begin).equalsIgnoreCase("9998-12-31")){
            	//2. 未勾选起始日期，则查询出到截止日期为止所有数据
            	sResult = " where a.fforecastdate <="+dbl.sqlDate(this.filterType.dAdjustDate_end);
            }else{
            	sResult = " where a.fforecastdate between "+dbl.sqlDate(this.filterType.dAdjustDate_begin)+" and "+dbl.sqlDate(this.filterType.dAdjustDate_end);
            }
            	
        	if(this.filterType.sPortCode.length()!=0){
        		sResult = sResult + " and a.fportcode = "+dbl.sqlString(this.filterType.sPortCode);
        	}
        	if(this.filterType.sCashCode.length()!=0){
        		sResult = sResult + " and a.fcashcode = "+dbl.sqlString(this.filterType.sCashCode);
        	}
        	if(this.filterType.sCuryCode.length()!=0){
        		sResult = sResult + " and a.fcurycode = "+dbl.sqlString(this.filterType.sCuryCode);
        	}
        }
        return sResult;
    }
	
    
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
           
    	this.dAdjustDate = rs.getDate("FFORECASTDATE");
    	this.sPortCode = rs.getString("FPORTCODE");
    	this.sPortName = rs.getString("FPORTName");
    	this.sCashCode = rs.getString("FCASHCODE");
    	this.sCashName = rs.getString("FCASHNAME");
    	this.sCuryCode = rs.getString("FCURYCODE");
    	this.sCuryName = rs.getString("FCURYNAME");
    	this.dAdjustMoney = rs.getDouble("FADJUSTMONEY");
    	this.sDesc = rs.getString("FDESC");
    }
	//============================================================================
	
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getListViewData1() throws YssException {
		
	   String sHeader = "";//表头
	   String sShowCols = "";
	   String sShowDataStr = "";
	   String sAllDataStr = "";
	   String sDataStr = "";
	   String strSql = "";
	   ResultSet rs = null;
	   StringBuffer bufShow = new StringBuffer();
	   StringBuffer bufAll = new StringBuffer();
	   
	   try{
		   sHeader = this.getListView1Headers();
		   sShowCols = this.getListView1ShowCols();
		   
		   if(this.sIsOnlyColumns.equalsIgnoreCase("0")){
			   return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +sShowCols ;
		   }
		   
		   strSql = " select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName,e.FCashAccName as FCASHNAME,f.fcuryname as FCURYNAME"+
			        " from "+pub.yssGetTableName("tb_data_adjustmoney")+" a"+
			        " left join (select FuserCode,fUserName from tb_sys_userlist) b on a.fcreator = b.fusercode "+
			        " left join (select FuserCode,fUserName from tb_sys_userlist) c on a.fcheckuser = c.fusercode "+
			        //--- 组合名称
			        //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
			        " left join (select o.fportcode,o.fportname from "+pub.yssGetTableName("tb_para_portfolio")+
			        " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
			        //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
			        //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//			        " (select FPortCode,max(FStartDate) as FStartDate from "+pub.yssGetTableName("tb_para_portfolio")+" where FStartDate <= " + 
//			        dbl.sqlDate(new java.util.Date()) +" and FCheckState = 1 and FASSETGROUPCODE = " +dbl.sqlString(pub.getAssetGroupCode()) +
			        //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
			        " ) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
			        //--- 账户名称
			        //edit by songjie 2011.03.16 不以最大的启用日期查询数据
			        " left join (select FCashAccCode, FCashAccName from " +pub.yssGetTableName("Tb_Para_CashAccount") +
//			        " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +//delete by songjie 2011.03.16 不以最大的启用日期查询数据
			        " where FCheckState = 1 ) e on a.fcashcode = e.FCashAccCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
			        //--- 币种名称
			        " left join (select fcurycode,fcuryname from " + pub.yssGetTableName("tb_para_currency") + " where fcheckstate=1) f on a.fcurycode = f.fcurycode "+
			        buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
		   
		   rs = dbl.openResultSet(strSql);
           while (rs.next()) {
               bufShow.append(super.buildRowShowStr(rs,
                   this.getListView1ShowCols())).
                   append(YssCons.YSS_LINESPLITMARK);

               setResultSetAttr(rs);
               super.setRecLog(rs);

               bufAll.append(this.buildRowStr()).append(YssCons.
                   YSS_LINESPLITMARK);
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
               this.getListView1ShowCols() ;

	   }catch(Exception e){
		   throw new YssException("获取其他金额信息出错\r\n" + e.getMessage(), e);
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
	
	//============================================================================
	
	public String addSetting() throws YssException {
		
		Connection con = dbl.loadConnection();
		boolean bTrans = false;
		StringBuffer insertBuf = new StringBuffer();
		
		try{
			insertBuf.append(" insert into "+pub.yssGetTableName("tb_data_adjustmoney"));
			insertBuf.append(" (FFORECASTDATE,FPORTCODE,FCASHCODE,FCURYCODE,FADJUSTMONEY,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME)");
			insertBuf.append(" values( "+dbl.sqlDate(this.dAdjustDate)+",");
			insertBuf.append(dbl.sqlString(this.sPortCode)+",");
			insertBuf.append(dbl.sqlString(this.sCashCode)+",");
			insertBuf.append(dbl.sqlString(this.sCuryCode)+",");
			insertBuf.append(this.dAdjustMoney+",");
			insertBuf.append(dbl.sqlString(this.sDesc)+",");
			insertBuf.append((pub.getSysCheckState()?"0":"1")+",");
			insertBuf.append(dbl.sqlString(this.creatorCode)+",");
			insertBuf.append(dbl.sqlString(this.creatorTime)+")");
			
			con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(insertBuf.toString()); //执行插入操作
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
            
            return buildRowStr();
		}catch(Exception e){
			 throw new YssException("新增其他金额信息出错", e);
		} finally {
            dbl.endTransFinal(con, bTrans);
        }
		
	}

	public void checkInput(byte btOper) throws YssException {
		
		 dbFun.checkInputCommon(btOper,pub.yssGetTableName("tb_data_adjustmoney"),"FFORECASTDATE,FPORTCODE,FCASHCODE",
				 YssFun.formatDate(this.dAdjustDate)+","+this.sPortCode+","+this.sCashCode, YssFun.formatDate(this.dOldAdjustDate)+","+(this.sOldPortCode.length()==0?" ":this.sOldPortCode)+","+
				 (this.sOldCashCode.length()==0?" ":this.sOldCashCode));

	}

	public void checkSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		StringBuffer checkBuf = new StringBuffer();
		String[] arrData = null;
		boolean bTrans = false;
		
		try{
			
			
			conn.setAutoCommit(false);
			bTrans = true;
			
			if(this.sRecycled !=null && this.sRecycled.trim().length() >0 ){
				
				arrData = sRecycled.split("\r\n");
				for( int i=0;i<arrData.length;i++){
					if(arrData[i].length()>0){
						parseRowStr(arrData[i]);
						checkBuf.append(" update "+pub.yssGetTableName("tb_data_adjustmoney"));
						checkBuf.append(" set FCheckState = "+this.checkStateId);
						checkBuf.append(" , FCheckUser = "+dbl.sqlString(pub.getUserCode()));
						checkBuf.append(" , FCheckTime = "+dbl.sqlString((YssFun.formatDatetime(new java.util.Date()))));
						checkBuf.append(" where FFORECASTDATE="+dbl.sqlDate(this.dAdjustDate));
						checkBuf.append(" and FPORTCODE="+dbl.sqlString(this.sPortCode));
						checkBuf.append(" and FCASHCODE="+dbl.sqlString(this.sCashCode));
						
						dbl.executeSql(checkBuf.toString());
						checkBuf.setLength(0);//清空StringBuffer
					}
					
				}
				
			}
			
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			
		}catch(Exception e){
			throw new YssException("审核其他金额出错......",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}

	}

	public void delSetting() throws YssException {
		
		Connection conn = dbl.loadConnection();
		StringBuffer delBuf = new StringBuffer();
		boolean bTrans = false;
		
		try{
			delBuf.append(" update "+pub.yssGetTableName("tb_data_adjustmoney"));
			delBuf.append(" set FCheckState = "+this.checkStateId);
			delBuf.append(" , FCheckUser = "+dbl.sqlString(pub.getUserCode()));
			delBuf.append(" , FCheckTime = "+dbl.sqlString((YssFun.formatDatetime(new java.util.Date()))));
			delBuf.append(" where FFORECASTDATE="+dbl.sqlDate(this.dAdjustDate));
			delBuf.append(" and FPORTCODE="+dbl.sqlString(this.sPortCode));
			delBuf.append(" and FCASHCODE="+dbl.sqlString(this.sCashCode));
			
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(delBuf.toString());
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			
		}catch(Exception e){
			throw new YssException("删除其他金额出错......",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}

	}

	public void deleteRecycleData() throws YssException {
		
		Connection conn = dbl.loadConnection();
		StringBuffer clearBuf = new StringBuffer();
		String[] arrData = null;
		boolean bTrans = false;
		
		try{
			
			
			conn.setAutoCommit(false);
			bTrans = true;
			
			if(this.sRecycled !=null && this.sRecycled.trim().length() >0 ){
				
				arrData = sRecycled.split("\r\n");
				for( int i=0;i<arrData.length;i++){
					if(arrData[i].length()>0){
						parseRowStr(arrData[i]);
						clearBuf.append(" delete from "+pub.yssGetTableName("tb_data_adjustmoney"));
						clearBuf.append(" where FFORECASTDATE="+dbl.sqlDate(this.dAdjustDate));
						clearBuf.append(" and FPORTCODE="+dbl.sqlString(this.sPortCode));
						clearBuf.append(" and FCASHCODE="+dbl.sqlString(this.sCashCode));
						
						dbl.executeSql(clearBuf.toString());
						clearBuf.setLength(0);//清空StringBuffer
					}
					
				}
				
			}
			
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			
		}catch(Exception e){
			throw new YssException("清空其他金额出错......",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}

	public String editSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		StringBuffer editBuf = new StringBuffer();
		boolean bTrans = false;
		
		try{
			
			editBuf.append(" update "+pub.yssGetTableName("tb_data_adjustmoney"));
			editBuf.append(" set FFORECASTDATE ="+dbl.sqlDate(this.dAdjustDate));
			editBuf.append(" , FPORTCODE ="+dbl.sqlString(this.sPortCode));
			editBuf.append(" , FCASHCODE ="+dbl.sqlString(this.sCashCode));
			editBuf.append(" , FCURYCODE ="+dbl.sqlString(this.sCuryCode));
			editBuf.append(" , FADJUSTMONEY ="+this.dAdjustMoney);
			editBuf.append(" , FDESC ="+dbl.sqlString(this.sDesc));
			editBuf.append(" where FFORECASTDATE="+dbl.sqlDate(this.dOldAdjustDate));
			editBuf.append(" and FPORTCODE="+dbl.sqlString(this.sOldPortCode));
			editBuf.append(" and FCASHCODE="+dbl.sqlString(this.sOldCashCode));
			
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(editBuf.toString());
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			
			return buildRowStr();
		}catch(Exception e){
			throw new YssException("修改其他金额出错......",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		AdjustMoneyBean adjustMoneyBean = new AdjustMoneyBean();
		String strSql = "";
	    ResultSet rs = null;
	    try{
	    	strSql = " select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName,e.FCashAccName as FCASHNAME,f.fcuryname as FCURYNAME"+
	        " from "+pub.yssGetTableName("tb_data_adjustmoney")+" a"+
	        " left join (select FuserCode,fUserName from tb_sys_userlist) b on a.fcreator = b.fusercode "+
	        " left join (select FuserCode,fUserName from tb_sys_userlist) c on a.fcheckuser = c.fusercode "+
	        //--- 组合名称
	        //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
	        " left join (select o.fportcode,o.fportname from "+pub.yssGetTableName("tb_para_portfolio")+
	        " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
	        //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
	        //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//	        " (select FPortCode,max(FStartDate) as FStartDate from "+pub.yssGetTableName("tb_para_portfolio")+" where FStartDate <= " + 
//	        dbl.sqlDate(new java.util.Date()) +" and FCheckState = 1 and FASSETGROUPCODE = " +dbl.sqlString(pub.getAssetGroupCode()) +
	        //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
	        ") d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
	        //--- 账户名称
	        //edit by songjie 2011.03.16 不以最大的启用日期查询数据
	        " left join (select FCashAccCode, FCashAccName from " +pub.yssGetTableName("Tb_Para_CashAccount") +
//	        " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +//delete by songjie 2011.03.16 不以最大的启用日期查询数据
	        " where FCheckState = 1 ) e on a.fcashcode = e.FCashAccCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
	        //--- 币种名称
	        " left join (select fcurycode,fcuryname from " + pub.yssGetTableName("tb_para_currency") + " where fcheckstate=1) f on a.fcurycode = f.fcurycode "+
	        buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
   
           rs = dbl.openResultSet(strSql);
           while(rs.next()){
        	   adjustMoneyBean.sCashCode = rs.getString("FCASHCODE");
        	   adjustMoneyBean.sCashName = rs.getString("FCASHNAME");
        	   adjustMoneyBean.sCuryCode = rs.getString("FCURYCODE");
        	   adjustMoneyBean.sCuryName = rs.getString("FCURYNAME");
        	   adjustMoneyBean.dAdjustMoney = rs.getDouble("FADJUSTMONEY");
        	   adjustMoneyBean.sPortCode = rs.getString("FPORTCODE");
        	   adjustMoneyBean.sPortName = rs.getString("FPORTName");
        	   adjustMoneyBean.sDesc = rs.getString("FDesc");
        	   adjustMoneyBean.dAdjustDate = rs.getDate("FFORECASTDATE");
           }
           return adjustMoneyBean.buildRowStr();
	    }catch(Exception e){
			   throw new YssException("获取其他金额修改前信息出错\r\n" + e.getMessage(), e);
	    }finally{
	    	dbl.closeResultSetFinal(rs);
	    }
	}

	

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}



	

}
