package com.yss.main.parasetting;

import java.sql.*;
import java.util.ArrayList;

import org.dom4j.Document;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.syssetting.RightBean;
import com.yss.util.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPort;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.client.AccountClinkageService_Service;
import com.yss.webServices.AccountClinkage.services.CashAccountInfo;

/**
 * <p>
 * Title: QSAccountBean
 * </p>
 * <p>
 * Description: 清算现金帐户设置
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.ysstech.com
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class QSAccountBean extends BaseDataSettingBean implements
		IDataSetting {
	private String FundCode; //资产代码
	private String FullName; //账户全称
	private String AccName;  //账户简称
	private String Bank;     //开户行
	private String BankBic;  //开户行BIC QDII专用
	private String Account;  //银行帐号
	private String hbfh;     //货币符号
	private String zhlx;     //账户类型
	private int zhdj;     //账户等级
	private String AccNum;   //序号
	private int audit;    //审核状态
	private String state;    //运行状态
	private String sRecycled = "";
	private String portCode = "";

	public QSAccountBean() {
	}

	/**
	 * parseRowStr 解析现金帐户信息维护请求
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
		String sTmpStr = "";
		try {
			if (sRowStr.equals("")) {
				return;
			}
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
			} else {
				sTmpStr = sRowStr;
			}
			sRecycled = sRowStr;
			reqAry = sTmpStr.split("\t");
			this.portCode = reqAry[0];
		} catch (Exception e) {
//			throw new YssException("解析现金帐户信息维护出错", e);
		}
	}
//			this.strCashAcctCode = reqAry[0];
//			this.strCashAcctName = reqAry[1];
//			this.dtStartDate = YssFun.toDate(reqAry[2]);
//			this.strAcctTypeCode = reqAry[3];
//			this.strSubAcctTypeCode = reqAry[4];
//			this.strBankCode = reqAry[5];
//			this.strBankAccount = reqAry[6];
//			this.strCurrencyCode = reqAry[7];
//			this.strPortCode = reqAry[8];
//			this.strAcctState = reqAry[9];
//			if (YssFun.isNumeric(reqAry[10])) {
//				this.interestOrigin = Integer.parseInt(reqAry[10]);
//			}
//			if (YssFun.isNumeric(reqAry[11])) {
//				this.fixRate = Double.parseDouble(reqAry[11]);
//			}
//			if (YssFun.isNumeric(reqAry[12])) {
//				this.interestCycle = Integer.parseInt(reqAry[12]);
//			}
//			this.strFormulaCode = reqAry[13]; 
//			this.strPeriodCode = reqAry[14];
//			this.strRoundCode = reqAry[15];
//			
//			//------ modify by wangzuochun 2011.06.03 BUG 2003 证券信息维护界面，维护一条证券信息，输入描述信息若含有回车符，清除/还原时报错 
//            if (reqAry[16] != null ){
//            	if (reqAry[16].indexOf("【Enter】") >= 0){
//            		this.strDesc = reqAry[16].replaceAll("【Enter】", "\r\n");
//            	}
//            	else{
//            		this.strDesc = reqAry[16];
//            	}
//            }
//            //----------------- BUG 2003 ----------------//
//			this.checkStateId = Integer.parseInt(reqAry[17]);
//			this.strOldCashAcctCode = reqAry[18];
//			this.dtOldStartDate = YssFun.toDate(reqAry[19]);
//			this.strIsOnlyColumns = reqAry[20];
//			if (reqAry.length > 22) {
//				this.strInvMgrCodeLink = reqAry[21];
//				this.strPortCodeLink = reqAry[22];
//			}
//
//			if (YssFun.isDate(reqAry[23])) {
//				this.dtMatureDate = YssFun.toDate(reqAry[23]);
//			}
//			this.strDepDurCode = reqAry[24];
//			if (YssFun.isNumeric(reqAry[25])) {
//				this.interestWay = YssFun.toInt(reqAry[25]);
//			}
//			if (YssFun.isNumeric(reqAry[26])) {
//				this.accAttr = YssFun.toInt(reqAry[26]);
//			}
//			if (YssFun.isDate(reqAry[27])) {
//				if (!reqAry[27].equalsIgnoreCase("9998-12-31")) {
//					this.dBeginDate = YssFun.parseDate(reqAry[27]);
//				}
//			}
//			if (YssFun.isDate(reqAry[28])) {
//				if (!reqAry[28].equalsIgnoreCase("9998-12-31")) {
//					this.dEndDate = YssFun.parseDate(reqAry[28]); // replace 26
//					// to 27 sj
//					// edit
//					// 20080728
//				}
//			}
//			// -----------------------------------------------------------------------------------------
//			// MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩
//			this.checkAccLinks = reqAry[29]; // 如果是审核和反审核，则把所有的信息都放入了数组的22位
//			this.checkAccLinks = this.checkAccLinks.replaceAll("\f", "\t"); // 为了便于使用通用的解析过程
//			// --------------------------------------------------------------------------------------------
//			// BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln
//			// 20090512
//			this.assetGroupCode = reqAry[30];
//			this.assetGroupName = reqAry[31];
//			// ---------------------------------------------------------------------------------------------
//			this.strBankAccName=reqAry[32];//add by zhouxiang 2010.10.13 招商基金资金账号信息查询功能
//			this.strLoanCode=reqAry[33];    //add by guyichuan 20110520 STORY #561
//			this.recPayCode=reqAry[34];//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
//		    
//			this.strInterTax = reqAry[35];
//			this.strInterTaxName = reqAry[36];
//			if (YssFun.isNumeric(reqAry[37])) {
//				this.interestAlg = YssFun.toInt(reqAry[37]);
//			}
//			super.parseRecLog();
//			if (sRowStr.indexOf("\r\t") >= 0) {
//				if (this.filterType == null) {
//					this.filterType = new QSAccountBean();
//					this.filterType.setYssPub(pub);
//				}
//				if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
//					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
//				}
//			}
//		} catch (Exception e) {
//			throw new YssException("解析现金帐户信息维护出错", e);
//		}
//	}

	/**
	 * buildRowStr 获取数据字符串
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.FundCode.trim());
		buf.append("\t");
		buf.append(this.FullName.trim());
		buf.append("\t");
		buf.append(this.AccName);
		buf.append("\t");
		buf.append(this.Bank);
		buf.append("\t");
		buf.append(this.BankBic);
		buf.append("\t");
		buf.append(this.Account);
		buf.append("\t");
		buf.append(this.hbfh);
		buf.append("\t");
		buf.append(this.zhlx);
		buf.append("\t");
		buf.append(this.zhdj);
		buf.append("\t");
		buf.append(this.AccNum);
		buf.append("\t");
		buf.append(this.audit);
		buf.append("\t");
		buf.append(this.state);
		return buf.toString();
	}

	/**
	 * checkInput 检查证券信息维护数据是否合法
	 * 
	 * @param btOper
	 *            byte
	 */
	public void checkInput(byte btOper) throws YssException {
	}

	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		return sResult;
	}

	/**
	 * getAllSetting
	 * 
	 * @return String
	 */
	public String getAllSetting() {
		return "";
	}

	public String builderListViewData(String strSql) throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
//		String sVocStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		Connection qsConn = null;
		Statement st = null;
		try {
			
			sHeader = this.getListView1Headers();
			qsConn = dbl.loadQsConnection();
			st = qsConn.createStatement();
			rs = st.executeQuery(strSql);
			while (rs.next()) {
				bufShow.append(super.buildRowShowStr(rs,
	                    this.getListView1ShowCols())).
	                    append(YssCons.YSS_LINESPLITMARK);
				setSecurityAttr(rs);
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

//			VocabularyBean vocabulary = new VocabularyBean();
//			vocabulary.setYssPub(pub);
//			sVocStr = vocabulary.getVoc(YssCons.YSS_CNT_INTCYL + ","
//					+ YssCons.YSS_CNT_INTORG + "," + YssCons.YSS_CNT_STATE
//					+ "," + YssCons.YSS_PCA_INTERESTWAY + ","
//					+ YssCons.Yss_Acc_ACCATTR + "," + YssCons.YSS_PCA_INTERESTALG);
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols();
//					+ "\r\f" + "voc"
//					+ sVocStr;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			try {
				
				if(rs!=null)
				{
					rs=null;
				}
				if(st!=null)
				{
					st=null;
				}
				if(qsConn!=null&&!qsConn.isClosed())
				{
					qsConn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw new YssException("获取清算帐户信息出错", e);
			}
		}
	}


	/**
	 * 
	 * @return String
	 */
	public String getListViewData4() throws YssException {
		return null;
	}

	/**
	 * getListViewData2 获取已审核的现金帐户信息维护数据
	 * 
	 * @return String
	 */
	public String getListViewData2() throws YssException {
		return null;
	}

	/**
	 * getListViewData3 获取现金帐户连接信息
	 * 
	 * @return String
	 */
	public String getListViewData3() throws YssException {
		return null;
	}

	/**
	 * getSetting
	 * 
	 * @return IParaSetting
	 */
	public IDataSetting getSetting() {
			return null;
	}

	/**
	 * getTreeViewData1
	 * 
	 * @return String
	 */
	public String getTreeViewData1() throws YssException{
		return null;
	}

	/**
	 * getTreeViewData2
	 * 
	 * @return String
	 */
	public String getTreeViewData2() {
		return "";
	}

	/**
	 * getTreeViewData3
	 * 
	 * @return String
	 */
	public String getTreeViewData3() {
		return "";
	}

	/**
	 * saveMutliSetting
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliSetting(String sMutilRowStr) {
		return "";
	}

	/**
	 * addSetting
	 * 
	 * @return String
	 */
	public String addSetting() throws YssException {
		return null;
	}

	/**
	 * editSetting
	 * 
	 * @return String
	 */
	public String editSetting() throws YssException {
		return null;
	}

	/**
	 * delSetting
	 */
	public void delSetting() throws YssException {
	}

	/**
	 * 修改时间：2008年3月20号 修改人：单亮 原方法功能：只能处理期间连接的审核和未审核的单条信息。
	 * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息 修改后不影响原方法的功能
	 */
	public void checkSetting() throws YssException {
	}

	/**
	 * 为各项变量赋值
	 * 
	 */
	public void setSecurityAttr(ResultSet rs) throws SQLException {
		this.FundCode = rs.getString("FFundCode") + "";
		this.FullName = rs.getString("FFullName") + "";
		this.AccName = rs.getString("FAccName");
		this.Bank = rs.getString("FBank") + "";
		this.BankBic = rs.getString("FBankBic") + "";
		this.Account = rs.getString("FAccount") + "";
		this.hbfh = rs.getString("Fhbfh") + "";
		this.zhlx = rs.getString("Fzhlx") + "";
		this.zhdj = rs.getInt("Fzhdj");
		this.AccNum = rs.getString("FAccNum") + "";
		this.audit = rs.getInt("FSh");
		this.state = rs.getString("Fstate") + "";
		
	}

	/**
	 * getOperValue
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */
	public String getOperValue(String sType) throws YssException {
		return "";
	}

	/**
	 * getBeforeEditData
	 * 
	 * @return String
	 */
	public String getBeforeEditData() throws YssException {
		return null;
	}

	/**
	 * 从数据库彻底删除数据
	 */
	public void deleteRecycleData() throws YssException {
	}

	public String getListViewGroupData1() throws YssException {
		return "";
	}

	// / <summary>
	// / 修改人：panjunfang
	// / 修改人时间:20090902
	// / MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
	// /
	// 如果为跨组合群，则在点击现金账户后面的放大镜按钮时加载出对应组合群的现金账户列表，例如：在日终处理-收益支付的债券利息收支中点击证券支付中现金账户的查找按钮应区别组合群加载出现金账户列表
	public String getListViewGroupData2() throws YssException {
		String strRe = "";// 存放返回到前台的字符串
		return strRe;
	}

	public String getListViewGroupData3() throws YssException {
		String strRe = "";// 存放返回到前台的字符串
		return strRe;
	}

	public String getListViewGroupData4() throws YssException {
		return "";
	}

	// / <summary>
	// / 修改人：fanghaoln
	// / 修改人时间:20090512
	// / BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
	// / 从后台加载出我们跨组合群的内容
	public String getListViewGroupData5() throws YssException {
		String sAllGroup = ""; // 定义一个字符用来保存执行后的结果传到前台
		return sAllGroup; // 把结果返回到前台进行显示
	}

	public String getTreeViewGroupData1() throws YssException {
		return "";
	}

	public String getTreeViewGroupData2() throws YssException {
		return "";
	}

	public String getTreeViewGroupData3() throws YssException {
		return "";
	}

	/**
	 *add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
	 * 获取清算账户信息
	 */
	public String getListViewData1() throws YssException {
		String strSql="";
		String portCode=this.portCode;
		//flag 返回true则为webService请求来源  ；返回false则为现金账户来源
		boolean flag=this.qsAccountSource((String)dbl.sqlString(portCode));
		if(flag){
			strSql=this.getQsWebServiceCashSouce(portCode);
		}else{
			strSql=this.getQsCashSouce();
		}
		return this.builderListViewData(strSql);
	}
	
	/**
	 * add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
	 * @param str
	 * @return 返回清算来源:true=webservice;false =现金账户;
	 * @throws YssException 
	 */
	public boolean qsAccountSource(String portCode) throws YssException{
		boolean flag = false;
    	ParaWithPort para = new ParaWithPort();
    	para.setYssPub(pub);
    	String value =(String)para.getSpeParaResultJH(portCode);
    	if(value==null || value.equals("01")||value.trim().equals("")){
    		flag=true;
    	}
    	if(value.equals("02")){
    		flag=false;
    	}
		return flag;
	}

	/**
	 * add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
	 *  对原有的获取现金账户的方法对其进行封装
	 * @return
	 */
	public String getQsCashSouce(){
		String strSql = "";
		ResultSet rs = null;
		ArrayList assetList =  new ArrayList();
		String strCondition = "";
		strSql = " select distinct fassetcode from " + pub.yssGetTableName("tb_para_portfolio" ) +  " where FCheckState = 1 " + 
		 (portCode.equals("")?"" : (" and  FPortCode = " + dbl.sqlString(portCode)));
		try {
			rs  = dbl.openResultSet(strSql);
			while(rs.next())
			{
				assetList.add(rs.getString("fassetcode"));
			}
			for(int i=0;i<assetList.size();i++)
			{
				strCondition += dbl.sqlString((String)assetList.get(i)) + ",";
			}
			if(strCondition!="")
			{
				strCondition = strCondition.substring(0, strCondition.length() -1);
				strSql = " select FFundCode,FFullName, FAccName, FBank,FBankBic, FAccount,Fhbfh, Fzhlx,Fzhdj,to_char(FAccNum) as FAccNum, " +  
				 " Fsh,Fstate from qsaccount where fsh = 1 and FFundCode in (" +  strCondition + ") order by FAccNum asc";
				
			}else
			{
				return "";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			dbl.closeResultSetFinal(rs);
		}
		return strSql;
	}

	/**
	 * add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
	 * 需要实现的方法
	 * 通过webservice的接口请求获取现金账户的来源
	 * @return
	 * @throws YssException 
	 */
	public String getQsWebServiceCashSouce(String portCode) throws YssException{
		String tableName = "ACQSACCOUNT";
		boolean flag = dbl.yssTableExist(tableName);
		if (flag == false) {
			this.createTableQsAccount();
		}
		
		PortfolioBean portfolio = new PortfolioBean();
		portfolio.setYssPub(this.pub);
		portfolio.setPortCode(portCode);
		String assetCode = portfolio.getOperValue("getFundCode");
		this.modifyTab(); //改表结构
		this.deleteWebService(assetCode);
		if (portCode != null && portCode.trim().length() > 0) {
			CashAccountInfo caInfo = new CashAccountInfo();
			caInfo.setPub(pub);
			//caInfo.setPortCode(portCode);
			try {
				
				Document doc = Console.createXml(null, "100", "100", "1.0", "",
						"AL044IT11", true);
				doc.getRootElement().addElement("body").addElement(
						"productcode").setText(assetCode);
				AccountClinkageService_Service service = new AccountClinkageService_Service();
				String result = service.getAccountClinkageServicePort().doDeal(
						doc.asXML());
				if(result != null && result.trim().length() > 0){
				    caInfo.setResponesMsgXml(Console.parseXml(result));
				    caInfo.getRespRecord(assetCode);
				}
			} catch (Exception e) {
				System.out.println("----------" + e.getMessage());
			}
		}
		return this.WebServiceSql();
	}
	
	/**
	 * add by huangqirong 2013-06-14 story #3871 
	 * 是否更改表结构
	 * */
	private void modifyTab() throws YssException{
		if(dbl.yssTableExist(this.pub.yssGetTableName("tb_para_cashaccount".toUpperCase()))){
			String sql = "select * from " + this.pub.yssGetTableName("tb_para_cashaccount");
			ResultSet rs = null ;
			try {
				rs = this.dbl.openResultSet(sql);
				if(dbl.isFieldExist(rs, "FQSACCNUM")){
					rs.close();
					rs = null;
					sql = " select FQSACCNUM from " + this.pub.yssGetTableName("tb_para_cashaccount");
					rs = dbl.openResultSet(sql);					
					ResultSetMetaData rsmd = rs.getMetaData();
					if(rsmd.getColumnCount() > 0){
						if(!"VARCHAR2".equalsIgnoreCase(rsmd.getColumnName(1))){
							if(rsmd.getColumnDisplaySize(1) != 50){
								this.dbl.executeSql("alter table " + this.pub.yssGetTableName("tb_para_cashaccount") +
									" modify FQSACCNUM varchar2(50)");
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println("修改表："+this.pub.yssGetTableName("tb_para_cashaccount") + "的字段FQSACCNUM出错：" +
						e.getMessage());
			}finally{
				this.dbl.closeResultSetFinal(rs);
			}			
		}
	}

	/**
	 * add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
	 * 创建qsacctount表，表结构复制jh2服务器上的表结构
	 * @throws YssException 
	 */
	public void createTableQsAccount() throws YssException{
		String sql="create table ACQSACCOUNT ( FAsetCode varchar2(20), FaccountNo VARCHAR2(50), " +
				"accountname VARCHAR2(100), accounttypecode VARCHAR2(50), accounttypename  VARCHAR2(100) )";
		try{
			dbl.executeSql(sql);
		}catch (Exception e) {
            throw new YssException("创建【清算信息表】失败！");
        }
	}
	
	/**
	 * add dongqingsong 2013-05-14 Story 3871 建行清算联动
	 * 删除webservice中的历史数据
	 */
	public  void deleteWebService(String assetCode){
		String deleteSql=" delete from ACQSACCOUNT where FAsetCode = " + this.dbl.sqlString(assetCode);
		try {
			dbl.executeSql(deleteSql);
		} catch (SQLException e) {
			System.out.println("删除语句错误"+e.getMessage());
		} catch (YssException e) {
			System.out.println("删除执行错误"+e.getMessage());
		}
		
	}
	
	/**
	 * add dongqingsong 2013-05-14 Story 3871 建行清算联动
	 * 读取webservice中的数据返回sql
	 * @return
	 */
	public String WebServiceSql(){
		String strSql = "";
		ResultSet rs = null;
		ArrayList assetList =  new ArrayList();
		this.setListView1Headers("银行帐号	账户名称	开户银行	开户行名称	资产代码");
		//账户序号	账户简称	货币符号	开户银行	银行帐号
		//FAccNum	FAccName	FHbfh	FBank	FAccount
		//this.setListView1ShowCols("FaccountNo	accountname	accounttypecode	accounttypename	FAsetCode");
		String strCondition = "";
		strSql = " select distinct fassetcode from " + pub.yssGetTableName("tb_para_portfolio" ) +  " where FCheckState = 1 " + 
		 (portCode.equals("")?"" : (" and  FPortCode = " + dbl.sqlString(portCode)));
		try {
			rs  = dbl.openResultSet(strSql);
			while(rs.next())
			{
				assetList.add(rs.getString("fassetcode"));
			}
			for(int i=0;i<assetList.size();i++)
			{
				strCondition += dbl.sqlString((String)assetList.get(i)) + ",";
			}
			if(strCondition!="")
			{
				strCondition = strCondition.substring(0, strCondition.length() -1);
				strSql = "select distinct (FaccountNo) as FAccNum, accountname as FAccName, accounttypecode as FHbfh," +
						" accounttypename as FBank, FAsetCode as FAccount, ' ' as FFundCode,' ' as FFullName," +
						" ' ' as FBankBic,' ' as Fzhlx,'0' as Fzhdj,'0' as Fsh,' ' as Fstate" +
						" from ACQSACCOUNT where fasetcode in (" +  strCondition + ") order by fAsetCode,FaccountNo";
			}else
			{
				return "";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			dbl.closeResultSetFinal(rs);
		}
		return strSql;
	}
}
