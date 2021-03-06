/**
 * 
 */
package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.storagemanage.CashStorageBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @包名：com.yss.main.operdata
 * @文件名：CollateralSecBean.java
 * @创建人：zhangfa
 * @创建时间：2010-11-1
 * @版本号：0.1
 * @说明：TODO
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-11-1 | Administrator | 0.1 |  
 */
public class CollateralSecBean extends BaseDataSettingBean implements IDataSetting{

	private String collateralCode="";//抵押物代码
	private String securityCode = "";//证券代码
    private String securityName = "";//券名称
    private String amount = "";//证券数量
    private String transferDate = "1900-01-01"; // 结算日期
    private String inOut = "1";//1存入;-1取出
    
    private String getHisAmount() throws YssException{
    	String strSql = "";
 	    String hisAmount="0";
 	    ResultSet rs = null;
 	    try{
 	    	/**
 	    	strSql=" select FAmount from "+pub.yssGetTableName("tb_Data_CollateralSec")+
 	    	       " where FCollateralCode="+dbl.sqlString(this.collateralCode)+" and FSECURITYCODE="+dbl.sqlString(this.securityCode);
 	    	       
 	    	strSql= " select FSECURITYCODE,sum(FAmount) as FAmount from "+pub.yssGetTableName("tb_Data_CollateralSec")+
 	    	        " where FCollateralCode="+dbl.sqlString(this.collateralCode)+" and FSECURITYCODE="+dbl.sqlString(this.securityCode)+
 	    	        " group by FSECURITYCODE";
 	    	        
 	    	        */
 	    	strSql=" select sum(ab.FAmount) as FAmount, ab.FSECURITYCODE as FSECURITYCODE from "+pub.yssGetTableName("tb_Data_CollateralAdd")+" aa "+
 	    		   " join (select FSECURITYCODE, fcollateralcode,FtransferDate,sum(FAmount) as FAmount from "+pub.yssGetTableName("tb_Data_CollateralSec")+
 	    		   "  where FSECURITYCODE="+dbl.sqlString(this.securityCode)+" and (FinOut is null or FinOut!=-1)"+
 	    		   " group by FSECURITYCODE, FtransferDate, fcollateralcode) ab on aa.fcollateralcode =ab.fcollateralcode and aa.ftransferdate =to_date(ab.FtransferDate,'yyyy-mm-dd')"+
 	    		   " where aa.fcheckstate = 1 and aa.fcollateralcode ="+dbl.sqlString(this.collateralCode)+" and aa.ftransferdate <"+dbl.sqlDate(this.transferDate)+
 	    		   " and (aa.FInOut is null or aa.FInOut!=-1) and aa.ftransfertype='证券'"+
 	    		   " group by FSECURITYCODE";
 	    	       
 	    	rs=dbl.openResultSet(strSql);
 	    	while(rs.next()){
 	    		hisAmount=rs.getDouble("FAmount")+"";
 	    	}
 	    }catch (Exception e) {
            throw new YssException("获取证券历史质押数量出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hisAmount;
    }
    private String getSecAmount() throws YssException{
    	   String strSql = "";
    	   String Amount="0";
    	   String portCode = "";
    	   String transferDate = "1900-01-01"; 
    	   ResultSet rs = null;
    	   
    	   
    	try{
    		if(amount!=null){
    			String [] temp=amount.split(",");
    			portCode=temp[0];
    			transferDate=temp[1];
    			
    		}
    		
    		strSql=" select a.FSECURITYCODE,((case when a.FStorageAmount  is null then 0 else a.FStorageAmount end)" +
    				"-(case when c.FAmount is null then 0 else c.FAmount end)" +
    				"-( case when b.FTradeAmount is null then 0 else b.FTradeAmount end)) as FAmount from "+
    				" (select FSECURITYCODE,FStorageAmount from "+pub.yssGetTableName("Tb_Stock_Security")+
     		       "   where  FCheckState=1 and FPortCode="+dbl.sqlString(portCode) +
     		       "   and FStorageDate="+dbl.sqlDate(YssFun.addDay(YssFun.toDate(transferDate), -1))+") a   "+
    				
    		         
    		       " left join (select FSECURITYCODE,sum(FTradeAmount) as  FTradeAmount from "  +pub.yssGetTableName("tb_data_subtrade")+
    		       "  where FSettleState=0 and FCheckState=1  and FBargainDate="+dbl.sqlDate(transferDate)+" and FPortCode="+dbl.sqlString(portCode) +" group by FSECURITYCODE) b on b.FSECURITYCODE=a.FSECURITYCODE"+
    		       " left join (select sum(FAmount) as FAmount , FSECURITYCODE from (select aa.fcollateralcode, aa.FTransferDate," +
   				   " ab.FAmount, ab.FSECURITYCODE  from "+pub.yssGetTableName("tb_Data_CollateralAdd")+" aa "+
   				   " left join (select FSECURITYCODE,fcollateralcode,FTransferDATE,sum(FAmount) as FAmount  from "+pub.yssGetTableName("tb_Data_CollateralSec")+
   				   " where (FinOut is null or Finout!=-1) and FTransferDate<="+dbl.sqlString(transferDate)+
   				   " group by FSECURITYCODE, fcollateralcode, FTransferDATE) ab on aa.fcollateralcode =ab.fcollateralcode and aa.ftransferdate = to_date(ab.ftransferdate,'yyyy-mm-dd')"+
   				   " where aa.FTransferDate <="+dbl.sqlDate(transferDate)+" and FSecurityCode="+dbl.sqlString(this.securityCode)+"  and aa.Fcheckstate = 1   and (aa.FInOut is null or aa.FInOut!=-1) and aa.ftransfertype='证券')"+
   				   " group by FSECURITYCODE  ) c on c.FSECURITYCODE =a.FSECURITYCODE"+
   				  
    		       " where a.FSECURITYCODE="+dbl.sqlString(this.securityCode);
    		      
    		rs=dbl.openResultSet(strSql);
    		while(rs.next()){
    			Amount=rs.getDouble("FAmount")+"";
    		}
    		return Amount;
    	}catch (Exception e) {
            throw new YssException("获取证券数量出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    	
    }
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
	        	sHeader="证券代码;C\t证券名称;C\t证券数量";
	        	listView1ShowCols="FSecurityCode\tFSecurityName\tFAmount";
	        	strSql=" select * from "+ pub.yssGetTableName("tb_Data_CollateralSec") +
	        	       " where FCollateralCode="+dbl.sqlString(this.collateralCode)+
	        	       " and FTransferDATE="+dbl.sqlString(this.transferDate)+
	        	       " and FInOut="+this.inOut;
	        	rs=dbl.openResultSet(strSql);
	        	while(rs.next()){
	        		bufShow.append(super.buildRowShowStr(rs, listView1ShowCols)).
                    append(YssCons.YSS_LINESPLITMARK);
	        		this.collateralCode=rs.getString("FCollateralCode");
	        		this.securityCode=rs.getString("FSecurityCode");
	        		this.securityName=rs.getString("FSecurityName");
	        		this.amount=rs.getDouble("FAmount")+"";
	        		
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
	            throw new YssException("获取抵押物补交证券信息出错", e);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
	}

  
    
	public String buildRowStr() throws YssException {
		   StringBuffer buf = new StringBuffer();
	        buf.append(this.collateralCode).append("\t");
	        buf.append(this.securityCode).append("\t");
	        buf.append(this.securityName).append("\t");
	        buf.append(this.amount).append("\t");
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
           this.securityCode=reqAry[1];
           this.securityName=reqAry[2];
           this.amount=reqAry[3];
           this.transferDate=reqAry[4];
           this.inOut=reqAry[5];
           //--------------------------------------------------------
           this.checkStateId =1;
           
            super.parseRecLog();
            
          
        } catch (Exception e) {
            throw new YssException("解析抵押物补交证券信息请求出错", e);
        }
		
	}
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

	           

	           

	            strSql = "delete from " + pub.yssGetTableName("tb_Data_CollateralSec") +
	                " where FCollateralCode  = " + dbl.sqlString(this.collateralCode) +
	                " and FTransferDATE="+dbl.sqlString(this.transferDate)+
	                " and FInOut="+this.inOut;
	            
	            dbl.executeSql(strSql);
	            if (sMutilRowStr != null && sMutilRowStr.length() > 0) {
	            	 sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
	            strSql =
	                "insert into " + pub.yssGetTableName("tb_Data_CollateralSec") +
	                "(FCollateralCode, FSecurityCode, FSecurityName,FAmount, FTransferDATE,FCheckState, FCreator, FCreateTime, FCheckUser,FCheckTime,FInOut) " +
	                " values (?,?,?,?,?,?,?,?,?,?,?)";
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
	                pstmt.setString(2, this.securityCode);
	                pstmt.setString(3, this.securityName);
	                pstmt.setString(4, (this.amount.length()==0?"0":this.amount));
	                pstmt.setString(5, this.transferDate);
	                pstmt.setInt(6, 1);
	                pstmt.setString(7, this.creatorCode);
	                pstmt.setString(8, this.creatorTime);
	                pstmt.setString(9,
	                                (pub.getSysCheckState() ? " " : this.creatorCode));
	                pstmt.setString(10,
	                                (pub.getSysCheckState() ? " " : this.creatorTime));
	                pstmt.setDouble(11, YssFun.toDouble(this.inOut));
	                pstmt.executeUpdate();

	            }
	            }
	            if (!bIsTrans) {
	                conn.commit();
	                bTrans = false;
	                conn.setAutoCommit(true);
	            }

	        } catch (Exception e) {
	            throw new YssException("抵押物补交证券信息\r\n" + e.getMessage(), e);
	        } finally {
	        	if (sMutilRowStr != null && sMutilRowStr.length() > 0) {
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
		if(sType.equalsIgnoreCase("getSecAmount")){
			return getSecAmount();
		}
		if(sType.equalsIgnoreCase("getHisAmount")){
			return getHisAmount();
		}
		return null;
	}

	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData2()
	 * 获取抵押物代码对应的证券的历史质押数量
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
        	sHeader="证券代码\t证券名称\t数量";
        	listView1ShowCols="FSecurityCode\tFSecurityName\tFAmount";
        	/**
        	strSql=" select Fsecuritycode ,FSecurityName,max(FAmount) as FAmount from "+ pub.yssGetTableName("tb_Data_CollateralSec") +
        	       " where FCollateralCode="+dbl.sqlString(this.collateralCode)+" and FTransferDATE<="+dbl.sqlDate(this.transferDate)+
        		   " group by FSecurityCOde ,FSecurityName";
        	   */   
            strSql=" select a.*, b.fsecurityname from ( select sum(ab.FAmount) as FAmount , ab.FSECURITYCODE as FSECURITYCODE from "+pub.yssGetTableName("tb_Data_CollateralAdd")+" aa "+
            	  " join (select FSECURITYCODE,fcollateralcode, FtransferDate,  sum(FAmount) as FAmount from "+pub.yssGetTableName("tb_Data_CollateralSec")+
            	  "  where (FInOut is null or FInOut!=-1)"+
            	  " group by FSECURITYCODE,FtransferDate,fcollateralcode) ab on aa.fcollateralcode =ab.fcollateralcode  and aa.ftransferdate =  to_date(ab.FtransferDate,'yyyy-mm-dd')"+
            	  " where aa.fcheckstate = 1 and aa.fcollateralcode ="+dbl.sqlString(this.collateralCode)+"  and aa.ftransferdate <"+dbl.sqlDate(this.transferDate)+ " and (aa.FInOut is null or aa.FInOut!=-1)  and aa.ftransfertype='证券'  group by FSECURITYCODE )a"+
            	  " left join (select FSECURITYCODE, FSecurityName from "+pub.yssGetTableName("tb_para_security")+
            	  " where FcheckState = 1) b on b.fsecuritycode = a.FSECURITYCODE";
        	rs=dbl.openResultSet(strSql);
        	while(rs.next()){
        		bufShow.append(super.buildRowShowStr(rs, listView1ShowCols)).
                append(YssCons.YSS_LINESPLITMARK);
        		this.securityCode=rs.getString("Fsecuritycode");
        		this.securityName=rs.getString("FSecurityName");
        		this.amount=rs.getDouble("FAmount")+"";
        		
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

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public String getAmount() {
		return amount;
	}

	public String getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(String transferDate) {
		this.transferDate = transferDate;
	}
	public void setAmount(String amount) {
		this.amount = amount;
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
