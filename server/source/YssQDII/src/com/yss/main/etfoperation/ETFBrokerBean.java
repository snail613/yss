package com.yss.main.etfoperation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.etfoperation.pojo.ETFRateBatchCreateBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * add by zhangjun
 * 2011-11-26 
 * ETF券商信息设置 
 * 
 */
public class ETFBrokerBean  extends BaseDataSettingBean implements IDataSetting{
	
	private String seatCode = "";          //席位会员
	private String orgCode = "";           //机构代码
    private String brokerName = "";        //券商名称
    private String operBankCode = "";      //开户行名称
    private String accName = "";           //账户名称
    private String bankCode = "";          //银行账户
    private String cashAccCode = "";       //资金账户
	private String oldSeatCode="";	
    
    public String getOldSeatCode() {
		return oldSeatCode;
	}

	public void setOldSeatCode(String oldSeatCode) {
		this.oldSeatCode = oldSeatCode;
	}

	private ETFBrokerBean filterType;
    
    public ETFBrokerBean getFilterType()
    {
    	return this.filterType;
    }
    
    public void setFilterType(ETFBrokerBean filterType){
    	this.filterType=filterType;
    }
	
	public String getSeatCode() {
		return seatCode;
	}

	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getOperBankCode() {
		return operBankCode;
	}

	public void setOperBankCode(String operBankCode) {
		this.operBankCode = operBankCode;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCashAccCode() {
		return cashAccCode;
	}

	public void setCashAccCode(String cashAccCode) {
		this.cashAccCode = cashAccCode;
	}

	
	
	
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
        	strSql = "select * from " + pub.yssGetTableName("TB_ETF_BROKER")
        		+ " where FSEATCODE = " + dbl.sqlString(this.seatCode);        	
        	
        	ResultSet rs = dbl.openResultSet(strSql);
            if (rs.next()) {      		
        		throw new YssException( YssFun.getCheckStateName(rs.getInt("FCheckState")) + "中【" + this.seatCode + "】已经存在，请重新输入");      		
        	}
            //---------------------------------
            strSql = "insert into " + pub.yssGetTableName("TB_ETF_BROKER")
              //席位会员，机构代码，券商名称，银行账户，开户行名称，账户名称，资金代码，审核状态，  ， 创建时间，审核人，审核时间
            	+ " (FSEATCODE,FAGENCYCODE,FBROKERNAME,FBANKACCOUNTNO,FBANKACCNAME,FACCOUNTNAME,FACCOUNTNO,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) "
            	+ " values ("
            	+ dbl.sqlString(this.seatCode) + "," //席位会员
            	+ dbl.sqlString(this.orgCode) + ","  //机构代码
            	+ dbl.sqlString(this.brokerName) + "," //券商名称
            	+ dbl.sqlString(this.bankCode) + ","   //银行账户
            	+ dbl.sqlString(this.operBankCode) + "," //开户行名称
            	+ dbl.sqlString(this.accName)+","     //账户名称
            	+ dbl.sqlString(this.cashAccCode)+","  //资金账户
            	+ "0"+","   //审核状态
            	+ dbl.sqlString(this.creatorCode)+","   
            	+ dbl.sqlString(this.creatorTime) + ","//创建时间
            	+ dbl.sqlString(this.checkUserCode) + ","//审核人            	
            	+ dbl.sqlString(this.checkTime) + ")";  //审核审核时间
            
            dbl.executeSql(strSql);            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增ETF券商信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		try {
			strSql = "update "
					+ pub.yssGetTableName("TB_ETF_BROKER")
					+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end"
					+ ", FCheckUser = case fcheckstate when 0 then "
					+ dbl.sqlString(this.checkUserCode) + " else null end"
					+ ", FCheckTime = case fcheckstate when 0 then "
					+ dbl.sqlString(this.checkTime) + " else null end "
					+ " where FSEATCODE=" + dbl.sqlString(this.seatCode);
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核ETF券商信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		try {
			strSql = "update "
				+ pub.yssGetTableName("TB_ETF_BROKER")
				+ " set FCheckState = 2 "
				+ ", FCheckUser = case fcheckstate when 0 then "
				+ dbl.sqlString(this.checkUserCode) + " else null end"
				+ ", FCheckTime = case fcheckstate when 0 then "
				+ dbl.sqlString(this.checkTime) + " else null end "
				+ " where FSEATCODE=" + dbl.sqlString(this.seatCode);
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除ETF券商信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		try {
			strSql = "delete from "
				+ pub.yssGetTableName("TB_ETF_BROKER")				
				+ " where FSEATCODE=" + dbl.sqlString(this.seatCode);
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除ETF券商信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		try {
			strSql = "update "
					+ pub.yssGetTableName("TB_ETF_BROKER")
					+" set FSEATCODE="+dbl.sqlString(this.seatCode)
					+", FAGENCYCODE="+dbl.sqlString(this.orgCode)
					+", FBROKERNAME="+dbl.sqlString(this.brokerName)
					+", FBANKACCOUNTNO="+dbl.sqlString(this.bankCode)
					+", FBANKACCNAME="+dbl.sqlString(this.operBankCode)
					+", FACCOUNTNAME="+dbl.sqlString(this.accName)
					+", FACCOUNTNO="+dbl.sqlString(this.cashAccCode)
					+" where FSEATCODE=" + dbl.sqlString(this.oldSeatCode);
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("修改ETF券商信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return "";
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
		// TODO Auto-generated method stub
		StringBuffer buf = new StringBuffer();
        buf.append(this.seatCode).append("\t");
        buf.append(this.orgCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.operBankCode).append("\t");
        buf.append(this.accName).append("\t");
        buf.append(this.bankCode).append("\t");
        buf.append(this.cashAccCode).append("\t");
        
        buf.append(super.buildRecLog());        
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
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.seatCode = reqAry[0].trim();
            this.orgCode = reqAry[1].trim();
            this.brokerName =reqAry[2].trim().equalsIgnoreCase("null")?"":reqAry[2].trim();
            this.operBankCode = reqAry[3].trim().equalsIgnoreCase("null")?"":reqAry[3].trim();
            this.accName = reqAry[4].equalsIgnoreCase("null")?"":reqAry[4].trim();
            this.bankCode = reqAry[5].equalsIgnoreCase("null")?"":reqAry[5].trim();
            this.cashAccCode = reqAry[6].equalsIgnoreCase("null")?"":reqAry[6].trim();
            this.oldSeatCode = reqAry[7];
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType =new ETFBrokerBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析ETF券商数据出错！", e);
        }
	}

	public String getListViewData1() throws YssException {
		  String strSql = ""; // 定义一个存放sql语句的字符串
	        String sHeader = "";
	        String sShowDataStr = "";
	        String sAllDataStr = "";
	        ResultSet rs = null;
	        StringBuffer bufShow = new StringBuffer();
	        StringBuffer bufAll = new StringBuffer();
	        try {
	        	sHeader = this.getListView1Headers();
	        	strSql = "select a.*,c.FUserName as FCreatorName ,d.FUserName as FCheckUserName from "+pub.yssGetTableName("TB_ETF_BROKER")+" a"	        	  
	                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c " 
	                + " on a.FCreator = c.FUserCode "
	                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) d " 
	                + " on a.FCheckUser = d.FUserCode " 
	                + " where "+buildFilterSql() 
	                + " order by a.FCheckState, a.FCreateTime desc";
	            
	            rs = dbl.openResultSet(strSql);
	            while(rs.next()){
	            	bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
	                append(YssCons.YSS_LINESPLITMARK);	            	
	            	this.setETFAttr(rs);	            	
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
	            
	            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
	            + "\r\f" + this.getListView1ShowCols();
	        }
	        catch(Exception e){
	        	throw new YssException("获取ETF汇率设置数据出错！" + "\r\n" + e.getMessage(), e);
	        }
	        finally{
	        	dbl.closeResultSetFinal(rs);
	        }
	}

	/**
     * 筛选条件
     * @return String
     */
	private String buildFilterSql() {
        String sResult = " 1=1 ";
        if (this.filterType != null) {
	        if (this.filterType.seatCode.length() != 0) {
	            sResult = sResult + " and FSEATCODE=" + dbl.sqlString(this.filterType.seatCode);
	
	        }
	        
	        if (this.filterType.orgCode.length() != 0) {
	            sResult = sResult + " and FAGENCYCODE=" + dbl.sqlString(this.filterType.orgCode);
	
	        }
	        
	        if (this.filterType.brokerName.length() != 0) {
	            sResult = sResult + " and FBROKERNAME=" + dbl.sqlString(this.filterType.brokerName);
	        }
	        
	        if (this.filterType.operBankCode.length() != 0) {
	            sResult = sResult + " and FBANKACCOUNTNO=" + dbl.sqlString(this.filterType.operBankCode);
	
	        }
	        
	        if (this.filterType.accName.length() != 0) {
	            sResult = sResult + " and FBANKACCNAME=" + dbl.sqlString(this.filterType.accName);
	
	        }
	        
	        if (this.filterType.bankCode.length() != 0) {
	            sResult = sResult + " and FACCOUNTNAME=" + dbl.sqlString(this.filterType.bankCode);
	        }
	        
	        if (this.filterType.cashAccCode.length() != 0) {
	            sResult = sResult + " and FACCOUNTNO=" + dbl.sqlString(this.filterType.cashAccCode);
	        }
        }
        return sResult;
    }
    
	public void setETFAttr(ResultSet rs) throws SQLException, YssException
	{
		this.seatCode = rs.getString("FSEATCODE");          //席位会员
		this.orgCode = rs.getString("FAGENCYCODE");           //机构代码
		this.brokerName = rs.getString("FBROKERNAME");        //券商名称
		this.operBankCode = rs.getString("FBANKACCNAME");      //开户行名称
		this.accName = rs.getString("FACCOUNTNAME");           //账户名称
		this.bankCode = rs.getString("FBANKACCOUNTNO");          //银行账户
		this.cashAccCode = rs.getString("FACCOUNTNO");           //资金代码
		super.setRecLog(rs);
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
