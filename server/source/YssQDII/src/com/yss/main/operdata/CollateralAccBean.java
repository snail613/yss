/**
 * 
 */
package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.parasetting.CollateralBean;
import com.yss.main.storagemanage.CashStorageBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @包名：com.yss.main.operdata
 * @文件名：CollateralAccBean.java
 * @创建人：zhangfa
 * @创建时间：2010-11-1
 * @版本号：0.1
 * @说明：TODO
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-11-1 | zhangfa | 0.1 |  
 */
public class CollateralAccBean extends BaseDataSettingBean implements IDataSetting{
	
	private String collateralCode="";//抵押物代码
	private String strCashAcctCode = ""; // 现金帐户代码
	private String strCashAcctName = ""; // 现金帐户名称
	private String transferDate = "1900-01-01"; // 结算日期
	
	private String inOut = "1";//1存入;-1取出
	
	
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData1()
	 */
	public String getListViewData1() throws YssException {
		    String sHeader = "";
		    String listView1ShowCols="";
	        String sShowDataStr = "";
	        String sAllDataStr = "";
	        String strSql = "";
	        StringBuffer bufShow = new StringBuffer();
	        StringBuffer bufAll = new StringBuffer();
	        ResultSet rs = null;
	        try {
	        	sHeader="账户代码\t账户名称";
	        	listView1ShowCols="FCASHACCCODE\tFCASHACCName";
	        	strSql=" select * from "+ pub.yssGetTableName("tb_Data_CollateralAcc") +
	        	       " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
	        	       " and FTransferDATE="+dbl.sqlString(this.transferDate)+
	        	       " and FInOut="+this.inOut;
	        	rs=dbl.openResultSet(strSql);
	        	while(rs.next()){
	        		bufShow.append(super.buildRowShowStr(rs, listView1ShowCols)).
                    append(YssCons.YSS_LINESPLITMARK);
	        		this.collateralCode=rs.getString("FCollateralCode");
	        		this.strCashAcctCode=rs.getString("FCASHACCCODE");
	        		this.strCashAcctName=rs.getString("FCASHACCName");
	        		
	        		 bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
	        		
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
	             listView1ShowCols;
	        }catch (Exception e) {
	            throw new YssException("获取抵押物补交现金账户出错", e);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
	}

   
	public String buildRowStr() throws YssException {
		   StringBuffer buf = new StringBuffer();
	        buf.append(this.collateralCode).append("\t");
	        buf.append(this.strCashAcctCode).append("\t");
	        buf.append(this.strCashAcctName).append("\t");
	        buf.append(super.buildRecLog());
	        return buf.toString();
		
	}
	public void parseRowStr(String sRowStr) throws YssException {
		String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
           this.collateralCode=reqAry[0];
           this.strCashAcctCode=reqAry[1];
           this.strCashAcctName=reqAry[2];
           this.transferDate=reqAry[3];
           this.inOut=reqAry[4];
           //--------------------------------------------------------
           this.checkStateId =1;
           
            super.parseRecLog();
            
          
        } catch (Exception e) {
            throw new YssException("解析抵押物补交现金账户设置请求出错", e);
        }
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#addSetting()
	 */
	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkInput(byte)
	 */
	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkSetting()
	 */
	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#delSetting()
	 */
	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#deleteRecycleData()
	 */
	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#editSetting()
	 */
	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#getAllSetting()
	 */
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#getSetting()
	 */
	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#saveMutliSetting(java.lang.String)
	 */
	public String saveMutliSetting(String sMutilRowStr,boolean bIsTrans) throws YssException {
	
	        String[] sMutilRowAry = null;
	        PreparedStatement pstmt = null;
	        Connection conn = dbl.loadConnection();
	        boolean bTrans = false;
	        String strSql = "";
	        //     String strYearMonth = "";
	        try {
	            if (!bIsTrans) {
	                conn.setAutoCommit(false);
	                bTrans = true;
	            }

	           

	           

	            strSql = "delete from " + pub.yssGetTableName("tb_Data_CollateralAcc") +
	                " where FCollateralCode  = " + dbl.sqlString(this.collateralCode) +
	                " and FTransferDATE="+dbl.sqlString(this.transferDate)+
	                " and FInOut="+this.inOut;
	            
	            
	            dbl.executeSql(strSql);
	            
	            if (sMutilRowStr != null && sMutilRowStr.length() >0){
	            	 sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);

	            strSql =
	                "insert into " + pub.yssGetTableName("tb_Data_CollateralAcc") +
	                "(FCollateralCode, FCASHACCCODE, FCASHACCName, FTransferDATE,FCheckState, FCreator, FCreateTime, FCheckUser,FCheckTime,FInOut) " +
	                " values (?,?,?,?,?,?,?,?,?,?)";
	            pstmt = conn.prepareStatement(strSql);

	            for (int i = 0; i < sMutilRowAry.length; i++) {
	                this.parseRowStr(sMutilRowAry[i]);
	                /*         if (this.bBegin.equalsIgnoreCase("true")) {
	                 this.sYearMonth = YssFun.formatDate(this.dtStorageDate, "yyyy") +
	                                  "00";
	                         }
	                         else {
	                 this.sYearMonth = YssFun.formatDate(this.dtStorageDate, "yyyyMM");
	                         }*/
                    
	                pstmt.setString(1, this.collateralCode);
	                pstmt.setString(2, this.strCashAcctCode);
	                pstmt.setString(3, this.strCashAcctName);
	                pstmt.setString(4, this.transferDate);
	                pstmt.setInt(5, 1);
	                pstmt.setString(6, this.creatorCode);
	                pstmt.setString(7, this.creatorTime);
	                pstmt.setString(8,
	                                (pub.getSysCheckState() ? " " : this.creatorCode));
	                pstmt.setString(9,
	                                (pub.getSysCheckState() ? " " : this.creatorTime));
	                pstmt.setDouble(10, YssFun.toDouble(this.inOut));
	                pstmt.executeUpdate();

	            }
	            }
	            if (!bIsTrans) {
	                conn.commit();
	                bTrans = false;
	                conn.setAutoCommit(true);
	            }

	        } catch (Exception e) {
	            throw new YssException("抵押物补交现金账户设置\r\n" + e.getMessage(), e);
	        } finally {
	        	if (sMutilRowStr != null && sMutilRowStr.length() >0){
	        		dbl.closeStatementFinal(pstmt);
	        	}
	            return "";
	        }
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssLogData#getBeforeEditData()
	 */
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#getOperValue(java.lang.String)
	 */
	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	

	

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData2()
	 * 获得抵押物对应的所有的现金账户
	 */
	public String getListViewData2() throws YssException {
		String sHeader = "";
	    String listView1ShowCols="";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
        	sHeader="账户代码\t账户名称";
        	listView1ShowCols="FCASHACCCODE\tFCASHACCName";
        	/**
        	strSql=" select * from "+ pub.yssGetTableName("tb_Data_CollateralAcc") +
        	       " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
        	       " and FInOut="+this.inOut;
        	       */
        	strSql=" select a.FCollateralCode, b.* from "+pub.yssGetTableName("tb_Data_CollateralAdd")+" a "+
        		   " left join (select FCollateralCode,finout,FCASHACCCODE ,FCASHACCName from "+pub.yssGetTableName("tb_Data_CollateralAcc")+"  where FTransferDATE<"+dbl.sqlString(this.transferDate)+" )b " +
        		   " on a.fcollateralcode=b.FCollateralCode and a.finout=b.FInOut "+
        		   " where a.FCollateralCode ="+dbl.sqlString(this.collateralCode)+
        		   " and a.FInOut =1"+//+this.inOut+
        		   "  and a.ftransfertype='现金' and a.fcheckstate=1"+
        		   " and FTransferDATE < "+dbl.sqlDate(this.transferDate);
        		   		

        	rs=dbl.openResultSet(strSql);
        	while(rs.next()){
        		bufShow.append(super.buildRowShowStr(rs, listView1ShowCols)).
                append(YssCons.YSS_LINESPLITMARK);
        		this.collateralCode=rs.getString("FCollateralCode");
        		this.strCashAcctCode=rs.getString("FCASHACCCODE");
        		this.strCashAcctName=rs.getString("FCASHACCName");
        		
        		 bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
        		
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
             listView1ShowCols;
        }catch (Exception e) {
            throw new YssException("获取抵押物补交现金账户出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData3()
	 */
	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData4()
	 */
	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData1()
	 */
	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData2()
	 */
	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData3()
	 */
	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData4()
	 */
	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewGroupData5()
	 */
	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData1()
	 */
	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData2()
	 */
	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewData3()
	 */
	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData1()
	 */
	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData2()
	 */
	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientTreeView#getTreeViewGroupData3()
	 */
	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCollateralCode() {
		return collateralCode;
	}

	public void setCollateralCode(String collateralCode) {
		this.collateralCode = collateralCode;
	}

	public String getStrCashAcctCode() {
		return strCashAcctCode;
	}

	public void setStrCashAcctCode(String strCashAcctCode) {
		this.strCashAcctCode = strCashAcctCode;
	}

	public String getStrCashAcctName() {
		return strCashAcctName;
	}

	public void setStrCashAcctName(String strCashAcctName) {
		this.strCashAcctName = strCashAcctName;
	}


	public String getTransferDate() {
		return transferDate;
	}


	public void setTransferDate(String transferDate) {
		this.transferDate = transferDate;
	}


	public String getInOut() {
		return inOut;
	}


	public void setInOut(String inOut) {
		this.inOut = inOut;
	}


	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#saveMutliSetting(java.lang.String)
	 */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

}
