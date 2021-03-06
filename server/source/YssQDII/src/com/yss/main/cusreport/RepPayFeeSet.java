/**   
* @Title: RepParaConfugure.java 
* @Package com.yss.main.cusreport 
* @Description: TODO( ) 
* @author KR
* @date 2013-2-17 下午03:01:07 
* @version V4.0   
*/
package com.yss.main.cusreport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @ClassName: RepParaConfugure
 * @Description: TODO()
 * @author add by huangqirong 2013-02-17 story #3489
 * @date 2013-2-17 下午03:01:07
 */
public class RepPayFeeSet extends BaseDataSettingBean implements IDataSetting {


	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public RepPayFeeSet() {
		// TODO Auto-generated constructor stub
	}
	
	private String portCode = "";	//组合
	private String feeType = "";	//费用类型
	private String payType = "";	//支付类型
	private String riskGoldScale = "";	//保证金比例
	private String portName = "";	//组合名称
	private String feeTypeName = "";	//费用类型名称
	private String payTypeName = "";	//支付类型名称
	private String oldPortCode = ""; 
	private String oldFeeType = "";
	
	private RepPayFeeSet filterType = null;
	
	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getFeeTypeName() {
		return feeTypeName;
	}

	public void setFeeTypeName(String feeTypeName) {
		this.feeTypeName = feeTypeName;
	}

	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}
		
	public RepPayFeeSet getFilterType() {
		return filterType;
	}

	public void setFilterType(RepPayFeeSet filterType) {
		this.filterType = filterType;
	}

	public String getPortCode() {
		return portCode;
	}
	
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	
	public String getFeeType() {
		return feeType;
	}
	
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	
	public String getPayType() {
		return payType;
	}
	
	public void setPayType(String payType) {
		this.payType = payType;
	}
	
	public String getRiskGoldScale() {
		return riskGoldScale;
	}
	
	public void setRiskGoldScale(String riskGoldScale) {
		this.riskGoldScale = riskGoldScale;
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#buildRowStr()
	 */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.portCode).append("\t");
		buf.append(this.feeType).append("\t");
		buf.append(this.payType).append("\t");
		buf.append(this.riskGoldScale).append("\t");
		buf.append(this.portName).append("\t");
		buf.append(this.feeTypeName).append("\t");
		buf.append(this.payTypeName).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#parseRowStr(java.lang.String)
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub
		String sTmpStr = "";
		String reqAry[] = null;
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
            this.portCode = reqAry[0];
            this.feeType = reqAry[1];
            this.payType = reqAry[2];
            this.riskGoldScale = reqAry[3];
            this.checkStateId = Integer.parseInt(reqAry[4]);
            this.oldPortCode = reqAry[5] ;
            this.oldFeeType = reqAry[6] ;
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RepPayFeeSet();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        }catch (Exception e) {
        	throw new YssException("解析报表参数配置信息出错", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#addSetting()
	 */
	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
        	strSql = "insert into " + pub.yssGetTableName("TB_Rep_PayFeeParaConfigure") + 
        		"(FPortCode ,FFeeType ,FPayType , FRiskGoldScale , FCHECKSTATE , FCREATOR, FCREATETIME , FCHECKUSER) " +
        		" values(" + dbl.sqlString(this.portCode) + "," +
        					dbl.sqlString(this.feeType) + "," +
        					dbl.sqlString(this.payType) + "," +
        					((this.riskGoldScale == null || this.riskGoldScale.trim().length() == 0) ? "0" : this.riskGoldScale) + "," +
        					(pub.getSysCheckState() ? "0" : "1") + "," + 
        					dbl.sqlString(this.creatorCode) + "," +
        					dbl.sqlString(this.checkTime) + "," + 
        					dbl.sqlString(this.checkUserCode)
        					+ ")";
        	conn.setAutoCommit(false);
            bTrans = true;
        	dbl.executeSql(strSql);
        	conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("新增报表应付费用参数配置信息出错", e);
		} finally {
            dbl.endTransFinal(conn, bTrans);
        }
		return this.buildRowStr();
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkInput(byte)
	 */
	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_Rep_PayFeeParaConfigure"),
                "FPortCode,FFeeType",
                this.portCode + "," + this.feeType, this.oldPortCode + "," + this.oldFeeType);
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkSetting()
	 */
	public void checkSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
		String sql = " update " + pub.yssGetTableName("TB_Rep_PayFeeParaConfigure") +		
			" set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
			", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
			" where FPortCode = " + dbl.sqlString(this.portCode) + " and FFeeType = " + dbl.sqlString(this.feeType);
		try {
			bTrans = true;
			conn.setAutoCommit(false);
			dbl.executeSql(sql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核或反审核应付费用参数配置信息出错", e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#delSetting()
	 */
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; //代表是否开始了事务
		String strSql = "";
		strSql = "update " + pub.yssGetTableName("TB_Rep_PayFeeParaConfigure") +
        " set FCheckState = " + this.checkStateId + 
        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
        "' where FFeeType = " + dbl.sqlString(this.feeType) + " and FPortCode = " + dbl.sqlString(this.portCode);
		try {
			conn.setAutoCommit(false);
		    bTrans = true;
		    dbl.executeSql(strSql);
		    conn.commit();
		    bTrans = false;		    
		} catch (Exception e) {
			throw new YssException("删除应付费用参数配置信息出错", e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#deleteRecycleData()
	 */
	public void deleteRecycleData() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; //代表是否开始了事务
		String sql = "";
		sql = "delete from " + pub.yssGetTableName("TB_Rep_PayFeeParaConfigure") + 
			  " where FPortCode = " + dbl.sqlString(this.portCode) +
			  " and FFeeType = " + dbl.sqlString(this.feeType);
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(sql);			
			conn.commit();
			bTrans = false;
		} catch (Exception e) {
			throw new YssException("清除应付费用参数配置信息出错", e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#editSetting()
	 */
	public String editSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
		String sql = " update " + pub.yssGetTableName("TB_Rep_PayFeeParaConfigure") +		
			" set FPortCode = " + dbl.sqlString(this.portCode) + "," +
				" FFeeType = " + dbl.sqlString(this.feeType) + "," + 
				" FPayType = " + dbl.sqlString(this.payType) + "," +
				" FRiskGoldScale = " + ((this.riskGoldScale == null || this.riskGoldScale.trim().length() == 0) ? "0" : this.riskGoldScale) + "," +
				" FCREATOR = " + dbl.sqlString(this.creatorCode) + "," +
				" FCREATETIME = " + dbl.sqlString(this.creatorTime) +		
			" where FPortCode = " + dbl.sqlString(this.oldPortCode) + " and FFeeType = " + dbl.sqlString(this.oldFeeType);
		try {
			bTrans = true;
			dbl.executeSql(sql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("修改应付费用参数配置信息出错", e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
		return this.buildRowStr();
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
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
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
	
	private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if(this.filterType.portCode != null && this.filterType.portCode.trim().length() > 0){
            	sResult = sResult + " and rpfp.fportcode like '" + this.filterType.portCode.replaceAll("'", "''") + "%'";            	
            }            
            if(this.filterType.feeType != null && this.filterType.feeType.trim().length() > 0 && !"99".equalsIgnoreCase(this.filterType.feeType)){
            	sResult = sResult + " and rpfp.FFeeType like '" + this.filterType.feeType.replaceAll("'", "''") + "%'";
            }
            if(this.filterType.payType != null && !"99".equalsIgnoreCase(this.filterType.payType) && this.filterType.payType.trim().length() > 0 ){
            	sResult = sResult + " and rpfp.FPayType like '" + this.filterType.payType.replaceAll("'", "''") + "%'";
            }
            if(this.filterType.riskGoldScale != null && this.filterType.riskGoldScale.trim().length() > 0){
            	sResult = sResult + " and rpfp.FRiskGoldScale like '" + this.filterType.payType.replaceAll("'", "''") + "%'";            	
            }
        }
        return sResult;
    }
	
	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
		this.portCode = rs.getString("FPortCode");
		this.portName = rs.getString("FPortName");
		this.feeType = rs.getString("FFeeType");
		this.feeTypeName = rs.getString("FFeeName");
		this.payType = rs.getString("FPayType");
		this.payTypeName = rs.getString("FPayName");
		this.riskGoldScale = rs.getString("FRiskGoldScale");		
		super.setRecLog(rs);
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData1()
	 */
	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select rpfp.*,ppf.fportname as Fportname ,fvb1.fvocname as FFeeName ,fvb2.fvocname as FPayName ," +
            		 " b.FUserName as FCreatorName,c.FUserName as FCheckUserName " +
            		 " from " + pub.yssGetTableName("TB_Rep_PayFeeParaConfigure") + " rpfp " + 
            		 " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on rpfp.FCreator = b.FUserCode" +
                     " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on rpfp.FCheckUser = c.FUserCode" +
            		 " left join " + pub.yssGetTableName("tb_para_portfolio") + " ppf on rpfp.FPortCode = ppf.fportcode " +
            		 " left join Tb_Fun_Vocabulary fvb1 on rpfp.ffeetype = fvb1.fvoccode and fvb1.fvoctypecode = 'Voc_PayFeeType' " +
            		 " left join Tb_Fun_Vocabulary fvb2 on rpfp.fpaytype = fvb2.fvoccode and fvb2.fvoctypecode = 'Voc_PayType' " + 
            		 this.buildFilterSql() + " order by rpfp.FCheckState, rpfp.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	bufShow.append(super.buildRowShowStr(rs,this.getListView1ShowCols())).
                        append(YssCons.YSS_LINESPLITMARK);
            	this.setResultSetAttr(rs);
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
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc("Voc_PayFeeType,Voc_PayType");            
        } catch (Exception e) {
            throw new YssException("获取报表应付费用参数配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData2()
	 */
	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
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
}
