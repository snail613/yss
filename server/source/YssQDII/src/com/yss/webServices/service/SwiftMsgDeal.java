package com.yss.webServices.service;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.main.cashmanage.CommandBean;
import com.yss.main.dao.IYssConvert;
import com.yss.main.datainterface.DaoInterfaceManageBean;
import com.yss.main.dayfinish.IncomePaidBean;
import com.yss.main.operdata.InvestPayRecBean;
import com.yss.main.operdeal.income.paid.BaseIncomePaid;
import com.yss.main.operdeal.income.paid.PaidAccIncome;
import com.yss.main.operdeal.income.paid.PaidInvestIncome;
import com.yss.pojo.dayfinish.AccPaid;
import com.yss.pojo.dayfinish.InvestPaid;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssDbOperSql;

/**
 * 报文数据处理,处理webService接受到的报文数据
 * modify by huangqirong 2012-12-19 story #2327 调整部分日期格式处理和功能类处理方式
 * @author yh 2011.05.16 QDV411建行2011年04月19日01_A
 * 
 */
public class SwiftMsgDeal {
	private YssPub pub = null;	
	private Connection connection = null; // 数据库连接
	private String requestMsg = null;	// 请求报文，字符串格式
	private Document requestMsgXml = null;	//请求报文，xml文档格式 
	private Document replyMsgXml = null;	// 应答报文
	private String replyCode = "0000";	//应答报文的应答码
	private String replyRemark = null;	//应答备注	add by huangqirong 2012-04-06 story #2325
	// 定义静态的hash表存放服务机收到的swift报文，用于发送到客户端进行提示
	// key值为报文类型，现在包含三种类型 的数据 1 报文收发统计数据 2 报文收发数据 3 证券交易未匹配数据
	// value值为数组，存放一类数据的不同记录
	public static HashMap<String, List<String>> msgCue = new HashMap<String, List<String>>();
	//保存收到的请求报文的通讯流水号，判断是否有重复的报文数据
	private static HashMap<String,String> htMsgNo = new HashMap<String,String>();
	
	/*add by huangqirong 2012-04-06 story #2326  接受时间 和 反馈信息*/
	public static HashMap<String, String> msgReturnClient = new HashMap<String, String>();
	
	//add by huangqirong 2012-10-24 story #2328
	public void setYssPub(YssPub pub){
		this.pub = pub;
	}
	
	public YssPub getYssPub(){
		return this.pub;
	}
	
	public void setRequestMsg(String requestMsg){
		this.requestMsg = requestMsg;
	}
	
	public String getRequestMsg(){
		return this.requestMsg;
	}
	
	public Document getRequestMsgXml() {
		return requestMsgXml;
	}

	public void setRequestMsgXml(Document requestMsgXml) {
		this.requestMsgXml = requestMsgXml;
	}

	public String getReplyCode() {
		return replyCode;
	}

	public void setReplyCode(String replyCode) {
		this.replyCode = replyCode;
	}

	public String getReplyRemark() {
		return replyRemark;
	}

	public void setReplyRemark(String replyRemark) {
		this.replyRemark = replyRemark;
	}

	public static HashMap<String, String> getHtMsgNo() {
		return htMsgNo;
	}

	public static void setHtMsgNo(HashMap<String, String> htMsgNo) {
		SwiftMsgDeal.htMsgNo = htMsgNo;
	}
	
	//--- end ---	
	

	/**
	 * 报文数据类处理构造方法
	 * @param swiftMsg	报文字符串           
	 */
	public SwiftMsgDeal(String request) {
		requestMsg = request;
		pub = new YssPub();
		pub.setDbLink(new DbBase());

	}
	
	/**
	 * add by huangqirong 2012-04-12 story #2326
	 * 无数据字符串构造方法
	 * */
	public SwiftMsgDeal() {
		pub = new YssPub();
		pub.setDbLink(new DbBase());
	}
	
	public Document getReplyMsgXml() {
		return replyMsgXml;
	}

	public void setReplyMsgXml(Document replyMsg) {
		this.replyMsgXml = replyMsg;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection conn) {
		this.connection = conn;
	}
	
	
	
	/*add by huangqirong 2012-04-12 story #2326  运营交易用途代码*/
	Hashtable<String, String> htOperationUsageCodes = null;
	/*add by huangqirong 2012-04-12 story #2326 Ta交易用途代码*/
	Hashtable<String, String> htTAUsageCodes = null;
	/*add by huangqirong 2012-04-12 story #2326 结息类用途代码*/
	Hashtable<String, String> htSelleOpers = null;
	/*add by huangqirong 2012-04-12 story #2326 单次业务用途代码*/
	Hashtable<String, String> htSingleOpers = null;
	/*add by huangqirong 2012-04-12 story #2326 其它交易用途代码*/
	Hashtable<String, String> htOthers = null;
	
	/**
	 * add by huangqirong 2012-04-12 story #2326
	 * 设置费用交易用途代码
	 * */
	private void setusageCodes(){
		String [] operationUsageCodes = new String [] {"903","902","910","911","912","904","801","905","907"}; //运营类
		String [] taUsageCodes = new String [] {"201","202","205","206","207","208"};
		String [] selleOpers = new String []{"901","913","5","6"}; //结息类
		String [] singleOpers = new String []{"906","908"};
		String [] others =new String []{"909"};
		
		if(this.htOperationUsageCodes == null)
			this.htOperationUsageCodes = new Hashtable<String, String>();
		if(this.htTAUsageCodes == null)
			this.htTAUsageCodes = new Hashtable<String, String>();
		if(this.htSelleOpers == null)
			this.htSelleOpers = new Hashtable<String, String>();
		if(this.htSingleOpers == null)
			this.htSingleOpers = new Hashtable<String, String>();
		if(this.htOthers == null)
			this.htOthers = new Hashtable<String, String>();
		
		for (int i = 0; i < operationUsageCodes.length; i++) {
			if(!this.htOperationUsageCodes.containsKey(operationUsageCodes[i]))
				this.htOperationUsageCodes.put(operationUsageCodes[i], operationUsageCodes[i]);					
		}
		
		for (int i = 0; i < taUsageCodes.length; i++) {
			if(!this.htTAUsageCodes.containsKey(taUsageCodes[i]))
				this.htTAUsageCodes.put(taUsageCodes[i], taUsageCodes[i]);
		}
		
		for (int i = 0; i < selleOpers.length; i++) {
			if(!this.htSelleOpers.containsKey(selleOpers[i]))
				this.htSelleOpers.put(selleOpers[i], selleOpers[i]);
		}
		
		for (int i = 0; i < singleOpers.length; i++) {
			if(!this.htSingleOpers.containsKey(singleOpers[i]))
				this.htSingleOpers.put(singleOpers[i], singleOpers[i]);
		}
		
		for (int i = 0; i < others.length; i++) {
			if(!this.htOthers.containsKey(others[i]))
				this.htOthers.put(others[i], others[i]);
		}
	}
	
	/**
	 * add by huangqirong 2012-04-12 story #2326
	 * 获取对应的费用数据应答
	 */
	public String [][] swiftFeeMsg(String [][] dParameter){
		
		this.setusageCodes();		//交易用途
		
		try {
			connection = pub.getDbLink().loadConnection();
			
		} catch (YssException e) {
			this.replyCode = "1";
			e.printStackTrace();
		}
		
		//add by huangqirong 2012-11-07 story #3227
		if(dParameter.length > 0)
			this.savaStringArray(dParameter , "GCSData" ,"request");
		//---end---
		
		for (int i = 0; i < dParameter.length; i++) {
			if(dParameter[i].length < 2)
				continue;
			String [] condition = dParameter[i][0].split("\\|");//参数条件
			
			String [] groupPorts = getPortCodeBySetCode(condition[1]);//根据资产代码和获取组合和组合群
			
			if(groupPorts[1].trim().length() == 0)
				continue ;
			
			if(this.htOperationUsageCodes.containsKey(condition[2])){	//运营数据
				int count = this.countFee(groupPorts,condition[2],condition[0]); //查找费用条数
				
				
				if(count > 1){
					dParameter[i][1] = "0.0000";
				}else if(count == 1 )
				{
					dParameter[i][1] = getFeeData(groupPorts,condition[2] , condition[0]);
				}else if(count == 0){
					dParameter[i][1] = "0.0000";	//无该费用设置
				}
			}else if(this.htTAUsageCodes.containsKey(condition[2])){	//TA数据
				if(condition[2].equals("201")){			//TA申购款
					//String tahzNets = this.getSH_SZ_Nets(groupPorts[0],"TA_HZ");
					String fee = getTaSettleMoney(groupPorts , condition[0] ,"01");
					dParameter[i][1] = fee.trim().length() == 0 ? "0.0000" : fee;
					
				}else if(condition[2].equals("202")){ 	//TA赎回款
					//String tahzNets = this.getSH_SZ_Nets(groupPorts[0],"TA_HZ");
					String fee = getTaSettleMoney(groupPorts , condition[0] ,"02");
					dParameter[i][1] = fee.trim().length() == 0 ? "0.0000" : fee;					
					
				}else if(condition[2].equals("205")){	//TA申购款上海中登	
					//String tahzNets = this.getSH_SZ_Nets(groupPorts[0],"TA_SHZD");
					dParameter[i][1] = "0.0000";	//无该费用设置
				}else if(condition[2].equals("206")){	//	TA赎回款上海中登
					//String tahzNets = this.getSH_SZ_Nets(groupPorts[0],"TA_SHZD");
					dParameter[i][1] = "0.0000";	//无该费用设置
				}else if(condition[2].equals("207")){	//TA申购款深圳中登
					//String tahzNets = this.getSH_SZ_Nets(groupPorts[0],"TA_SZZD");
					dParameter[i][1] = "0.0000";	//无该费用设置
				}else if(condition[2].equals("208")){	//TA赎回款深圳中登
					//String tahzNets = this.getSH_SZ_Nets(groupPorts[0],"TA_SZZD");
					dParameter[i][1] = "0.0000";	//无该费用设置
				}
			}else{
				dParameter[i][1] = "0.0000";
			}
		}
		
		//add by huangqirong 2012-11-07 story #3227
		if(dParameter.length > 0)
			this.savaStringArray(dParameter , "GCSDataLog" , "response");
		return dParameter;
	}
	
	/**
	 * add by huangqirong 2012-11-06 story #3227
	 * 数组保存为二维数组
	 * */
	private void savaStringArray(String [][] dParameter , String partPath , String requestType){
		Document document = DocumentHelper.createDocument();
		Element resElement = null;
		Element dataInfo = null;
		resElement = document.addElement((requestType == null || requestType.trim().length() == 0) ? "request" : requestType);		
		for (int i = 0; i < dParameter.length; i++) {
			if(dParameter[i].length < 2)
				continue;
			dataInfo = resElement.addElement("datainfo");
			dataInfo.addElement("FeedCondition").setText(dParameter[i][0]); //费用查询条件
			dataInfo.addElement("FeedValue").setText(dParameter[i][1]); //费用
		}
		if(resElement.elements().size() > 0){
			String gcsDataPath = this.getPropertiesPath();
			gcsDataPath = !gcsDataPath.endsWith(File.separator) ? gcsDataPath + File.separator : gcsDataPath ;
			this.saveAsXml(document , gcsDataPath , partPath + File.separator);
		}
	}
	
	/*
	 * add by huangqirong 2012-04-12 story #2326
	 * 获取Ta申赎汇总数据
	 * */
	private String getTaSettleMoney(String [] GroupPorts , String date, String sellType){
		String result ="";
		ResultSet rs = null;		
		PreparedStatement pstp = null;		
		
		String sql = "select sum(case when FSettleMoney is null then FSellMoney else FSettleMoney end) as FSettleMoney from " +
					" Tb_" + GroupPorts[0] + "_TA_Trade where FCheckState = 1 and FSettleDate = ? and FPortCode = ? and FSELLTYPE = ?" ;
		try {
			pstp = connection.prepareStatement(sql);
			pstp.setDate(1, new java.sql.Date(this.toDate(date).getTime()));
			pstp.setString(2, GroupPorts[1]);
			pstp.setString(3, sellType);
			rs = pstp.executeQuery();
			
			if(rs.next()){
				result= YssFun.formatNumber(rs.getDouble("FSettleMoney"), "#,##0.0000");
			}
		} catch (Exception e) {
			this.replyCode = "1";
		}finally{
			try {
				rs.close();
				pstp.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return result;
	}
	
	/*
	 *  add by huangqirong 2012-04-12 story #2326
	 * 获取网点分类比如上海的网点，深圳等
	 * */
	private String getSH_SZ_Nets(String aassetGroupString ,String tradeNetType){
		/*参数编号：  Fpubparacode	，参数组编号： FParaGroupCode ，控件组代码 ：FCtlGrpCode*/
		
		String nets ="";
		ResultSet rs = null;		
		PreparedStatement pstp = null;
		
		String sql = " select tbp2.* from ( select max(FPARAID) as FPARAID from TB_"+aassetGroupString+"_PFOper_PUBPARA " +
					 " where Fpubparacode='TA_ZDMode' and FCTLGRPCODE='CtlTAZDMode'  and FCTLVALUE like '"+tradeNetType+"%'"+  
					 " ) tbp1" +
					 " left join " +
					 " ( select * from TB_"+aassetGroupString+"_PFOper_PUBPARA where Fpubparacode='TA_ZDMode' and FCTLGRPCODE='CtlTAZDMode' and " +
					 "  FPARAID<>0 )tbp2 " +
					 " on tbp1.FPARAID = tbp2.FPARAID and FCTLCODE='scSellPoints' " ;
		
		try {
			pstp = connection.prepareStatement(sql);
			rs = pstp.executeQuery();
			
			if(rs.next()){
				nets= rs.getString("FCTLVALUE");
			}			
			if(nets.trim().length() > 0){
				nets = nets.split("\\|")[0];
			}
			
		} catch (Exception e) {
			this.replyCode = "1";
		}finally{
			try {
				rs.close();
				pstp.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return nets;
	}
	
	/*
	 * add by huangqirong 2012-04-12 story #2326
	 * 是否待摊
	 * */
	private String isDeferredFee(String [] groupPorts ,String tradeUsageCode, String date , boolean isTypeOrPayCat){		
		ResultSet rs = null;		
		PreparedStatement pstp = null;
		String ivType = "";
		//boolean isdeferred = false;
		String sql = " select tpip.*,tbipcfee.fivtype from ( select * from Tb_Base_InvestPayCat where FCheckState = 1 " +
					 " and FTradeUsageCode = ? " +
					 " and FIVTYPE='managetrusteeFee' ) tbipcfee  " +
					 " join (select tpip2.* from (" +
					 " select FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE,max(FStartDate) as FStartDate" +
					 " from Tb_"+groupPorts[0]+"_Para_InvestPay where FCheckState = 1 and FPortCode = ? " +
					 " group by FIVPAYCATCODE, FSTARTDATE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE " +
					 " ) tpip1" +
					 " left join " +
					 " Tb_"+groupPorts[0]+"_Para_InvestPay tpip2 "+
				     " on tpip1.FIVPayCatCode = tpip2.FIVPayCatCode and tpip1.FStartDate = tpip2.FStartDate and tpip1.FPortCode= tpip2.FPortCode " +
				     " and tpip1.FANALYSISCODE1=tpip2.FANALYSISCODE1 and tpip1.FANALYSISCODE2= tpip2.FANALYSISCODE2 and " +
				     " tpip1.FANALYSISCODE3 = tpip2.FANALYSISCODE3 and tpip1.FPORTCLSCODE=tpip2.FPORTCLSCODE) tpip " +
				     " on tbipcfee.FIVPayCatCode = tpip.FIVPayCatCode " +
				     " union all" +
				     " select tpip.*,tbipcfee.fivtype from ( select * from Tb_Base_InvestPayCat where FCheckState = 1" +
				     " and FTradeUsageCode = ? " +
				     " and FIVTYPE<>'managetrusteeFee' " +
				     " and FIVTYPE <>'deferredFee') tbipcfee  " +
				     " join (select tpip2.* from (" +
				     " select FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE," +
				     " max(FStartDate) as FStartDate from Tb_"+groupPorts[0]+"_Para_InvestPay " +
				     " where FCheckState = 1 and FPortCode = ? " +
					 " and FACBeginDate <= ? " +
					 " and FACEndDate   > ? " +
					 " group by FIVPAYCATCODE, FSTARTDATE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE "+
					 " ) tpip1 left join  Tb_"+groupPorts[0]+"_Para_InvestPay tpip2 "+
					 " on tpip1.FIVPayCatCode = tpip2.FIVPayCatCode and tpip1.FStartDate = tpip2.FStartDate " +
					 " and tpip1.FPortCode= tpip2.FPortCode and tpip1.FANALYSISCODE1=tpip2.FANALYSISCODE1 " +
					 " and tpip1.FANALYSISCODE2= tpip2.FANALYSISCODE2 and tpip1.FANALYSISCODE3 = tpip2.FANALYSISCODE3 "+
					 " and tpip1.FPORTCLSCODE=tpip2.FPORTCLSCODE ) tpip on tbipcfee.FIVPayCatCode = tpip.FIVPayCatCode" ;
		try {
			pstp = connection.prepareStatement(sql);
			pstp.setString(1, tradeUsageCode);
			pstp.setString(2, groupPorts[1]);
			pstp.setString(3, tradeUsageCode);
			pstp.setString(4, groupPorts[1]);
			try {
				pstp.setDate(5, new java.sql.Date(this.toDate(date).getTime()));
				pstp.setDate(6, new java.sql.Date(this.toDate(date).getTime()));
			} catch (Exception e) {
				this.replyCode = "1";
				e.printStackTrace();
			}
			//pstp.addBatch();
			rs = pstp.executeQuery();
			if(rs.next()){
				if(isTypeOrPayCat)
					ivType = rs.getString("FIVTYPE");
				else
					ivType = rs.getString("FIVPAYCATCODE");
			}
			
			/*预提:accruedFee  待摊:deferredFee  两费：managetrusteeFee*/
			//if(ivType.equals("deferredFee"))
			//	isdeferred = true;
		}catch (Exception e) {
			this.replyCode = "1";
		}finally{
			try {
				rs.close();
				pstp.close();
			} catch (Exception e2) {
				this.replyCode = "1";
			}
		}
		return ivType;
	}
	
	/*
	 * add by huangqirong 2012-04-12 story #2326
	 * 获取相关费用
	 * */
	private String getFeeData(String [] groupPorts ,String tradeUsageCode, String date){
		String fee = "";
		ResultSet rs = null;		
		PreparedStatement pstp = null;
		String ivPayCatCode = "";
		String ivType = "";
		
		String sql = " select tpip.*,tbipcfee.fivtype from ( select * from Tb_Base_InvestPayCat where FCheckState = 1 " +
					 " and FTradeUsageCode = ? " +
					 " and FIVTYPE='managetrusteeFee' ) tbipcfee  " +
					 " join (select tpip2.* from (" +
					 " select FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE,max(FStartDate) as FStartDate" +
					 " from Tb_"+groupPorts[0]+"_Para_InvestPay where FCheckState = 1 and FPortCode = ? " +
					 " group by FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE " +
					 " ) tpip1" +
					 " left join " +
					 " Tb_"+groupPorts[0]+"_Para_InvestPay tpip2 "+
				     " on tpip1.FIVPayCatCode = tpip2.FIVPayCatCode and tpip1.FStartDate = tpip2.FStartDate and tpip1.FPortCode= tpip2.FPortCode " +
				     " and tpip1.FANALYSISCODE1=tpip2.FANALYSISCODE1 and tpip1.FANALYSISCODE2= tpip2.FANALYSISCODE2 and " +
				     " tpip1.FANALYSISCODE3 = tpip2.FANALYSISCODE3 and tpip1.FPORTCLSCODE=tpip2.FPORTCLSCODE) tpip " +
				     " on tbipcfee.FIVPayCatCode = tpip.FIVPayCatCode " +
				     " union all" +
				     " select tpip.*,tbipcfee.fivtype  from ( select * from Tb_Base_InvestPayCat where FCheckState = 1" +
				     " and FTradeUsageCode = ? " +
				     " and FIVTYPE<>'managetrusteeFee' " +
				     " and FIVTYPE <>'deferredFee') tbipcfee  " +
				     " join (select tpip2.* from (" +
				     " select FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE," +
				     " max(FStartDate) as FStartDate from Tb_"+groupPorts[0]+"_Para_InvestPay " +
				     " where FCheckState = 1 and FPortCode = ? " +
					 " and FACBeginDate <= ? " +
					 " and FACEndDate   > ? " +
					 " group by FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE "+
					 " ) tpip1 left join  Tb_"+groupPorts[0]+"_Para_InvestPay tpip2 "+
					 " on tpip1.FIVPayCatCode = tpip2.FIVPayCatCode and tpip1.FStartDate = tpip2.FStartDate " +
					 " and tpip1.FPortCode= tpip2.FPortCode and tpip1.FANALYSISCODE1=tpip2.FANALYSISCODE1 " +
					 " and tpip1.FANALYSISCODE2= tpip2.FANALYSISCODE2 and tpip1.FANALYSISCODE3 = tpip2.FANALYSISCODE3 "+
					 " and tpip1.FPORTCLSCODE=tpip2.FPORTCLSCODE ) tpip on tbipcfee.FIVPayCatCode = tpip.FIVPayCatCode" ;
		try {
			pstp = connection.prepareStatement(sql);
			pstp.setString(1, tradeUsageCode);
			pstp.setString(2, groupPorts[1]);
			pstp.setString(3, tradeUsageCode);
			pstp.setString(4, groupPorts[1]);
			try {
				pstp.setDate(5, new java.sql.Date(this.toDate(date).getTime()));
				pstp.setDate(6, new java.sql.Date(this.toDate(date).getTime()));
			} catch (Exception e) {
				this.replyCode = "1";
				e.printStackTrace();
			}
			
			
			//pstp.addBatch();
			rs = pstp.executeQuery();
			if(rs.next()){
				ivPayCatCode = rs.getString("FIVPayCatCode");
				ivType = rs.getString("FIVTYPE");
			}
			rs.close();
			pstp.close();
			
			/*预提:accruedFee  待摊:deferredFee  两费：managetrusteeFee*/
			if(ivType.equals("deferredFee"))
				return "0";
			
			if(ivPayCatCode.trim().length() > 0){
				//boolean isContinue = true ;
				double feeDou = 0.0;
				sql = "select * from Tb_"+groupPorts[0]+"_Stock_Invest where FCheckState = 1 and FPortCode = ?" +
						" and FStorageDate = ? and FIVPayCatCode= ?";						
				pstp = connection.prepareStatement(sql) ;
				pstp.setString(1, groupPorts[1]);				
				try {
					pstp.setDate(2, new java.sql.Date(this.toDate(date).getTime()));
				} catch (Exception e) {
					this.replyCode = "1";
				}
				
				pstp.setString(3, ivPayCatCode);
				
				rs = pstp.executeQuery();
				while(rs.next()){
					if(feeDou <= rs.getDouble("FBal"))
						feeDou = rs.getDouble("FBal");
				}				
				fee = ""+YssFun.formatNumber(feeDou, "#,##0.0000");
			}			
		} catch (SQLException e) {
			this.replyCode = "1";
		}finally{
			try {
				if(rs != null)//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
					rs.close();
				if(pstp != null)//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
					pstp.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return fee;
	}
	
	/**
	 * add by huangqirong 2012-04-12 story #2326
	 * 查找费用条数
	 * */
	private int countFee(String [] groupPorts , String tradeUsageCode , String date){
		ResultSet rs = null;		
		PreparedStatement pstp = null;
		int count = 0;
			
		String sql = " select sum(Fct) as Fct from ( select count(*) as Fct from (select * from Tb_Base_InvestPayCat where FCheckState = 1 " +
		 " and FTradeUsageCode = ? " +
		 " and FIVTYPE='managetrusteeFee' ) tbipcfee  " +
		 " join (select tpip2.* from (" +
		 " select FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE,max(FStartDate) as FStartDate" +
		 " from Tb_"+groupPorts[0]+"_Para_InvestPay where FCheckState = 1 and FPortCode = ? " +
		 " group by FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE " +
		 " ) tpip1" +
		 " left join " +
		 " Tb_"+groupPorts[0]+"_Para_InvestPay tpip2 "+
	     " on tpip1.FIVPayCatCode = tpip2.FIVPayCatCode and tpip1.FStartDate = tpip2.FStartDate and tpip1.FPortCode= tpip2.FPortCode " +
	     " and tpip1.FANALYSISCODE1=tpip2.FANALYSISCODE1 and tpip1.FANALYSISCODE2= tpip2.FANALYSISCODE2 and " +
	     " tpip1.FANALYSISCODE3 = tpip2.FANALYSISCODE3 and tpip1.FPORTCLSCODE=tpip2.FPORTCLSCODE) tpip " +
	     " on tbipcfee.FIVPayCatCode = tpip.FIVPayCatCode " +
	     " union all" +
	     " select count(*) as Fct  from ( select * from Tb_Base_InvestPayCat where FCheckState = 1" +
	     " and FTradeUsageCode = ? " +
	     " and FIVTYPE<>'managetrusteeFee' " +
	     " and FIVTYPE <>'deferredFee') tbipcfee  " +
	     " join (select tpip2.* from (" +
	     " select FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE," +
	     " max(FStartDate) as FStartDate from Tb_"+groupPorts[0]+"_Para_InvestPay " +
	     " where FCheckState = 1 and FPortCode = ? " +
		 " and FACBeginDate <= ?" +
		 " and FACEndDate   >?" +
		 " group by FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE "+
		 " ) tpip1 left join  Tb_"+groupPorts[0]+"_Para_InvestPay tpip2 "+
		 " on tpip1.FIVPayCatCode = tpip2.FIVPayCatCode and tpip1.FStartDate = tpip2.FStartDate " +
		 " and tpip1.FPortCode= tpip2.FPortCode and tpip1.FANALYSISCODE1=tpip2.FANALYSISCODE1 " +
		 " and tpip1.FANALYSISCODE2= tpip2.FANALYSISCODE2 and tpip1.FANALYSISCODE3 = tpip2.FANALYSISCODE3 "+
		 " and tpip1.FPORTCLSCODE=tpip2.FPORTCLSCODE ) tpip on tbipcfee.FIVPayCatCode = tpip.FIVPayCatCode )" ;	
		
		try {
			pstp = connection.prepareStatement(sql);
			pstp.setString(1, tradeUsageCode);
			pstp.setString(2, groupPorts[1]);
			pstp.setString(3, tradeUsageCode);
			pstp.setString(4, groupPorts[1]);
			try {
				pstp.setDate(5, new java.sql.Date(this.toDate(date).getTime()));
				pstp.setDate(6, new java.sql.Date(this.toDate(date).getTime()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//pstp.addBatch();
			rs = pstp.executeQuery();
			if(rs.next()){
				count = rs.getInt("Fct");
			}
		} catch (SQLException e) {
			this.replyCode = "1";
		}finally{			
			try {
				if(rs != null)//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
					rs.close();
				if(pstp != null)//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
					pstp.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}
	
	
	
	
	/**
	 * 报文数据处理入口类
	 */
	public void swiftMsgOper() {
		Element root = null;
		Element head = null;
		String txCode = null;	//交易码
		String msgNo = null;	//报文流水号
		List<Element> bizData = null;
		String otherElement = "" ; //add by huangqirong 2012-04-06 story #2325
		
		try {
			//检查报文格式
			String checkResult = checkRequestMsg();
			//如果报文格式正确，可以正确解析,继续执行操作
			if (checkResult.equals("0000")) {
				// 获取根节点
				root = requestMsgXml.getRootElement();
				// 获取head节点
				head = root.element("head");
				// 获取head节点中的txcode交易码节点
				txCode = head.element("txcode").getText();
				// 获取报文通讯流水号
				msgNo = head.element("msgno").getText();
				// 判断是否收到重复的流水号，若收到则不执行后续的处理
				if(!htMsgNo.containsKey(msgNo))
				{	
					// 获取body元素下的bizdata元素数组
					bizData = root.element("body").elements("bizdata");
					otherElement = "bizdata" ; //add by huangqirong 2012-04-06 story #2325
					connection = pub.getDbLink().loadConnection();
					// 数据为持仓数据
					if ("100003001".equals(txCode)) {
						//处理持仓数据
						dealPosition(bizData);
						System.out.println("服务机处理持仓数据报文成功");
					}
					// 证券交易结算指令数据
					else if ("100004001".equals(txCode)) {
						//处理证券交易指令数据
						dealTradeSettle(bizData);
						System.out.println("服务机处理证券交易结算数据报文成功");
					}
					// 报文收发统计数据
					else if ("100008001".equals(txCode)) { 
						dealMsgSRStat(bizData);
						System.out.println("服务机处理报文收发统计数据报文成功");
					}
					// 报文收发情况数据
					else if ("100009001".equals(txCode)) { 
						dealMsgSR(bizData);
						System.out.println("服务机处理报文收发数据报文成功");
					}
					// 证券交易未匹配数据
					else if ("100010001".equals(txCode)) { 
						dealSecNoMatch(bizData);
						System.out.println("服务机处理证券交易未匹配数据报文成功");
					}
					//add by huangqirong 2012-04-06 story #2325
					else if("ZjYeDz".equals(txCode)){	/*资金余额数据*/
						this.replyCode = "0";
						bizData = root.element("body").elements("datainfo");
						otherElement = "datainfo";
						this.dealBalance(bizData);
						if(this.replyRemark == null || this.replyRemark.trim().length() == 0)
							this.replyRemark = "导入成功。";
						System.out.println("服务机处理资金余额数据报文成功");						
					}else if("ZqYeDz".equals(txCode)){	/*证券余额数据*/
						this.replyCode = "0";
						bizData = root.element("body").elements("datainfo");
						otherElement = "datainfo";
						this.dealSecurity(bizData);
						if(this.replyRemark == null || this.replyRemark.trim().length() == 0)
							this.replyRemark = "导入成功。";
						System.out.println("服务机处理证券数据报文成功");
					}
					//---end---
					//add by huangqirong 2012-04-15 story #2326
					else if("QsTransfer".equals(txCode)){	/*划款指令*/
						this.replyCode = "0";
						bizData = root.element("body").elements("datainfo");
						otherElement = "datainfo";
						this.dealThrashDictate(bizData);
						if(this.replyRemark == null || this.replyRemark.trim().length() == 0)
							this.replyRemark = "导入成功。";
						System.out.println("服务机处理划款指令数据报文成功");
					}
					//add by huangqirong 2012-11-07 story #2328
					else if("JYZL".equals(txCode)){	/*交易指令数据*/
						this.setReplyCode("0");
						bizData = root.element("body").elements("datainfo");
						otherElement = "datainfo";
						this.setReplyRemark(""); //让该变量不为 null 空
						GCSTradeDataDeal tradeDeal = new GCSTradeDataDeal();
						tradeDeal.setConnection(this.getConnection());
						tradeDeal.setYssPub(this.getYssPub());
						tradeDeal.dealTradeData(bizData); //处理GCS交易数据
						if(this.getReplyRemark() == null || this.getReplyRemark().trim().length() == 0)
							this.setReplyRemark("导入成功。");
						System.out.println("服务机处理交易接口数据成功");	
					}
					//---end---
					//add by huangqirong 2012-12-17 story #2327
					else if("JJQYXX".equalsIgnoreCase(txCode)){
						this.setReplyCode("0");
						bizData = root.element("body").elements("datainfo");
						otherElement = "datainfo";
						this.setReplyRemark(""); //让该变量不为 null 空
						GCSRightDataDeal rightData = new GCSRightDataDeal();
						rightData.setConnection(this.getConnection());
						rightData.setYssPub(this.getYssPub());
						rightData.dealRightData(bizData); //处理GCS行动接口权益数据
						if(this.getReplyRemark() == null || this.getReplyRemark().trim().length() == 0)
							this.setReplyRemark("导入成功。");
						System.out.println("服务机处理行动接口权益数据成功");	
					}
					//---end---
					
					//处理成功后，保存该流水号
					htMsgNo.put(msgNo, msgNo);
				}
				else
				{	//重复的报文通讯流水号，错误码1013
					replyCode = "1013";
				}
			}
			else
			{
				replyCode = checkResult;
			}
		} catch (Exception e) {
				//add by huangqirong 2012-04-06 story #2325
				if(otherElement.equals("datainfo"))
					replyCode = "1" ; 
				else
					replyCode = "1999";
				//---end---
		} finally {
			try {
				if(null!= connection){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//生成返回报文
			buildReplyMsg(replyCode, replyRemark, txCode, msgNo ,otherElement); //modify huangqirong 2012-04-06 story #2325			
		}
		
	}
	
	/**
	 * 处理划款指令报文数据
	 * add by huangqirong 2012-04-06 story #2326
	 * */
	private void dealThrashDictate(List<Element> bizData){
		this.setusageCodes();		//交易用途
		Element data = null;
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String sDate = formatter.format(date);	
		
		try {
			if (bizData.size() > 0) {
				for (int i = 0; i < bizData.size(); i++) {
					data = bizData.get(i);					
					
					//资产代码转换组合代码
					String fsetcode = data.elementText("ffundcode"); //资产代码
					String fdate = data.elementText("fdate");	//划款日期
					String fnote = data.elementText("fnote");	//划款用途
					String ftype = data.elementText("ftype");	//交易用途
					
					String ffromacc =data.elementText("ffromacc"); //付款帐号
					String ftoacc = data.elementText("ftoacc");	   //收款帐号
					String famount =data.elementText("famount");	//划款金额
					String fzlbh = data.elementText("fzlbh"); 		//后来添加指令编号 改动					
					
					String [] groupPorts = getPortCodeBySetCode(fsetcode);//根据资产代码和获取组合和组合群
					String portCode = groupPorts[1]; //关联组合
					String groupCode = groupPorts[0];
					if(portCode.trim().length() == 0)
						continue ;
					
					int count1 = this.getCountbySql("select count(*) as Fct from Tb_" + groupCode + "_Para_Receiver where FACCOUNTNUMBER="+ this.pub.getDbLink().sqlString(ffromacc), "Fct");
					
					if(count1 >1){
						this.replyCode = "1";
						this.replyRemark = "导入失败:QDII系统(资产代码 " + fsetcode+")付款人设置表中有多个银行帐号为" + ffromacc + " 的付款人。";
						break;
					}else if(count1 ==0){
						this.replyCode = "1";
						this.replyRemark = "导入失败:QDII系统(资产代码 " + fsetcode + ")付款人设置表中没有银行帐号为 " + ffromacc + " 的付款人。";
						break;
					}
					
					int count2 = this.getCountbySql("select count(*) as Fct from Tb_" + groupCode+"_Para_Receiver where FACCOUNTNUMBER=" + this.pub.getDbLink().sqlString(ftoacc), "Fct");
					
					if(count2 >1){
						this.replyCode = "1";
						this.replyRemark = "导入失败:QDII系统(资产代码 " + fsetcode + ")收款人设置表中有多个银行帐号为" + ffromacc + " 的收款人。";
						break;						
					}else if(count2 ==0){
						this.replyCode = "1";
						this.replyRemark = "导入失败:QDII系统(资产代码 " + fsetcode + ")收款人设置表中没有银行帐号为 " + ffromacc + " 的收款人。";
						break;
					}
					
					String payData1 = this.getDatasbySql("select * from Tb_" + groupCode + "_Para_Receiver where FACCOUNTNUMBER=" + 
										this.pub.getDbLink().sqlString(ffromacc), "FReceiverName,FOperBank,FCuryCode");
					
					if(payData1.trim().length() == 0)
						continue;
					
					String [] paydatas = payData1.split(",");
					
					String recData2 = this.getDatasbySql("select * from Tb_" + groupCode + "_Para_Receiver where FACCOUNTNUMBER=" + 
										this.pub.getDbLink().sqlString(ftoacc), "FReceiverName,FOperBank,FCuryCode");
					
					if(recData2.trim().length() == 0)
						continue;
					
					String [] recdatas =recData2.split(",");
					
					String GCSState = "000" ;					
					
					if(this.htOperationUsageCodes.containsKey(ftype)){	//运营
						int count3 = this.countFee(groupPorts, ftype, fdate);
						
						if(count3 > 1){
							GCSState = "110";
						}else if(count3 == 1){
							/*预提:accruedFee  待摊:deferredFee  两费：managetrusteeFee*/
							String ivType = isDeferredFee(groupPorts , ftype, fdate, true);
							if(ivType.equals("deferredFee")){
								GCSState = "110";
							}else {
								GCSState = "100";
							}
						}else if(count3 == 0){
							GCSState = "110";
						}
					}else if(this.htTAUsageCodes.containsKey(ftype)){	//TA
						GCSState = "110";
					}else if(this.htSelleOpers.containsKey(ftype)){		//结息
						if(this.getAccCount(groupCode,ftoacc,recdatas[2]) == 1)
							GCSState = "100";
						else
							GCSState = "110";
					}else if(this.htSingleOpers.containsKey(ftype)){	//单次业务
						GCSState = "110";						
					}else if(this.htOthers.containsKey(ftype)){	//其它
						GCSState = "110";						
					}
					
					String webRoot = this.getClass().getResource("/").toString();	//应用程序上下文路径
					webRoot = webRoot.substring(0,(webRoot.length()-16));
					/*改表前缀*/
					this.pub.setPrefixTB(groupCode);
					this.pub.setAssetGroupCode(groupCode);	
					
					this.pub.setWebRoot(webRoot);
					this.pub.setUserCode("GCS");
					this.pub.setPortBaseCury();
					
					CommandBean command = new CommandBean();
					command.setYssPub(this.pub);					
					command.setOrder("0");
					command.setPortCode(portCode);			//组合代码
					command.setSCommandDate(fdate);			//指令日期
					command.setSCommandTime(YssFun.formatDatetime(new java.util.Date()));//指令时间
					command.setAccountDate(fdate);			//到帐日期
					command.setAccountTime(YssFun.formatDatetime(new java.util.Date()));//到账时间
					command.setCashUsage(fnote);			//划款用途
					command.setTradeUsageCode(ftype);		//交易用途代码
					command.setPayAccountNO(ffromacc);		//付款人帐号
					command.setPayName(paydatas[0]);		//付款人名称
					command.setPayOperBank(paydatas[1]);		//付款人银行
					command.setPayCuryCode(paydatas[2]);		//付款币种
					command.setReAccountNO(ftoacc);			//收款人帐号
					command.setReceiverName(recdatas[0]); 	//收款人名称
					command.setReOperBank(recdatas[1]);		//收款人银行
					command.setReCuryCode(recdatas[2]);		//付款币种
					command.setPayMoney(Double.parseDouble(famount));	//付款金额
					command.setReMoney(Double.parseDouble(famount));	//收款金额					
					command.checkStateId = 1; 			//审核状态
					command.creatorCode ="GCS";				//创建人、修改人
					command.checkUserCode ="GCS";			//审核人
					command.creatorTime = YssFun.formatDatetime(new java.util.Date());
					command.checkTime = YssFun.formatDatetime(new java.util.Date());
					command.setgCSState(GCSState);			//GCS处理状态
					command.setDRate(0);
					
					
					if(GCSState.equalsIgnoreCase("100")){
						
						command.setNumType("gcsdata");
						command.setSNum(this.getMaxNextAddOneToFNum(groupCode, fdate));
						command.setGcsNum(fzlbh);					//指令编号 后来从xml标签中获取
						this.deleteCommand(groupCode, portCode, fdate, fzlbh); //删除划款指令
						command.addSetting();
						
						String beanid1 = "";
						String beanid2 = "";
						ArrayList bList = new ArrayList();
						Object obj = null;
						IYssConvert bond = null;
						if(this.htOperationUsageCodes.containsKey(ftype)) //运营
						{
							beanid1 = "investincomepaid";
							beanid2 = "investpaidbean";
						}else if(this.htSelleOpers.containsKey(ftype)){
							beanid1 = "accincomepaid";
							beanid2 = "accpaidbean";
						}
						
						BaseIncomePaid income = (BaseIncomePaid) pub.getOperDealCtx().getBean(beanid1);
						
						//investincomepaid	investpaidbean  //[accincomepaid, accpaidbean]						
						
						obj = pub.getDayFinishCtx().getBean(beanid2);
		                bond = (IYssConvert) obj;
						
		                income.setYssPub(this.pub);
		                //investIncome.initIncomeCalculate(null, YssFun.parseDate(fdate, "yyyy-MM-dd"), YssFun.toDate("1900-01-01"), portCode, "False\t");
		                //String requestData = fdate+"\n"+portCode+"\n"+ivestPaid.buildRowStr1()+"\ninvestincomepaid\tinvestpaidbean\nFalse\n\n"+groupCode+"\nTrue";
		                income.initIncomeCalculate(null, this.parseDate(fdate, "yyyy-MM-dd"), this.toDate("1900-01-01"), portCode, "False\t06DE");
						
						String ivPayCatCode = isDeferredFee(groupPorts , ftype, fdate, false);						
						
						String accCount = this.getCashAcc(groupCode ,ftoacc ,recdatas[2]);
						
						//ivestPaid.setPortCode(portCode);
						if(income instanceof PaidAccIncome && bond instanceof AccPaid ){ //结息
							PaidAccIncome accIncome =(PaidAccIncome )income;							
		                	bond = accIncome.getSingleIncomes(portCode ,YssFun.parseDate(fdate, "yyyy-MM-dd") , accCount, recdatas[2]);
							if(bond == null)
								continue;
		                	
							AccPaid accPaidBean = (AccPaid)bond;
							accPaidBean.setDDate(this.parseDate(fdate, "yyyy-MM-dd"));
							accPaidBean.setBalMoney(YssD.add(Double.parseDouble(famount), -accPaidBean.getLx()));
							accPaidBean.setLx(Double.parseDouble(famount));
							accPaidBean.setMoney(accPaidBean.getLx());
							
							accPaidBean.parseRowStr(accPaidBean.buildRowStr1());
							bList.add(bond);
							
		                	/*PaidAccIncome paidAccInCome = (PaidAccIncome)income;
		                	HashMap valueMap = paidAccInCome.getPaidInterTax(bond);
		                	double dInterTaxBal = (valueMap.get("07LXS_DE\tbal")==null?0:(Double)valueMap.get("07LXS_DE\tbal"));
		                	if(dInterTaxBal >0 && accPaidBean.getTsfTypeCode().equalsIgnoreCase("06")){
		                		bList.add(paidAccInCome.PaidInterTax(bond,valueMap));
		                	}*/
		                	this.deleteCashInvestTransfer(groupCode , portCode , "02" , "02DE" , fdate , accCount , "cash");//删除现金相关库存变动数据
		                }else if(income instanceof PaidInvestIncome && bond instanceof InvestPaid){ //运营
		                	PaidInvestIncome investIncome =(PaidInvestIncome )income;		                	
		                	bond = investIncome.getSingleIncomes(portCode ,YssFun.parseDate(fdate, "yyyy-MM-dd") ,ivPayCatCode);
		                	if(bond == null)
								continue;
	                		InvestPaid investPaidBean =(InvestPaid)bond;
	                		investPaidBean.setPDate(this.parseDate(fdate, "yyyy-MM-dd"));
	                		investPaidBean.setBalMoney(YssD.add(Double.parseDouble(famount), -investPaidBean.getMoney()));
	                		investPaidBean.setInvestMoney(Double.parseDouble(famount));
	                		investPaidBean.setMoney(Double.parseDouble(famount));
	                		//investPaidBean.setTsfTypeCode("03");
	                		//investPaidBean.setSubTsfTypeCode("03IV");
	                		investPaidBean.parseRowStr(investPaidBean.buildRowStr1());	                		
	                		bList.add(investPaidBean);
	                		this.deleteCashInvestTransfer(groupCode , portCode , "03" , "03IV" , fdate , ivPayCatCode , "invest");//删除运营相关库存变动数据
	                	}
						
						income.saveIncome(bList);
						GCSState = "101";	//处理完毕
						
						updateGCSState(groupCode , command.getSNum() , fzlbh); //修改状态
						
						/*恢复表前缀*/
						this.pub.setPrefixTB(this.oldPrefix);
						command.setYssPub(this.pub);
						income.setYssPub(this.pub);
					}else {
						command.setNumType("gcsdata");
						command.setSNum(this.getMaxNextAddOneToFNum(groupCode, fdate));
						command.setGcsNum(fzlbh);					//指令编号 后来从xml标签中获取
						this.deleteCommand(groupCode, portCode, fdate, fzlbh);
						command.addSetting();
						/*恢复表前缀*/
						this.pub.setPrefixTB(this.oldPrefix);
						command.setYssPub(this.pub);
					}
				}
			}
		} catch (Exception e) {
			this.replyCode = "1";
		} finally {
			if(SwiftMsgDeal.msgReturnClient.get("QsTransfer") != null && SwiftMsgDeal.msgReturnClient.get("QsTransfer").length() > 0){
				String msg = SwiftMsgDeal.msgReturnClient.get("QsTransfer");
				
				SwiftMsgDeal.msgReturnClient.put("QsTransfer", msg + "\n\n["+sDate+"]\tGCS划款指令接口，数据已接收。" );
			}else {
				SwiftMsgDeal.msgReturnClient.put("QsTransfer", "["+sDate+"]\tGCS划款指令接口，数据已接收。");
			}
		}
	}
	
	/**
	 * add by huangqirong 2012-06-13 story #2326 
	 * 删除运营类资金调拨
	 * */
	private void deleteCashInvestTransfer(String groupCode , String portCode , String TsfTypeCode , String SubTsfTypeCode , String date , String IVPayCatCodeOrAcc , String dataType ){
		DbBase dbl = this.pub.getDbLink();
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务        
        Connection conn = null ;
        String nums = "";
        try {
        	conn = dbl.loadConnection();
        	
        	if("invest".equalsIgnoreCase(dataType)){
        		strSql = "select WMSYS.WM_CONCAT(FNum) as FNum from tb_" + groupCode + "_Data_InvestPayRec where FPortCode = " + 
            		 dbl.sqlString(portCode) + " and FTsfTypeCode = " + dbl.sqlString(TsfTypeCode) + 
            		 " and FSubTsfTypeCode =" + dbl.sqlString(SubTsfTypeCode) + " and FTransDate = " + dbl.sqlDate(date) +
            		 " and FIVPayCatCode = " + dbl.sqlString(IVPayCatCodeOrAcc);
        	}else if("cash".equalsIgnoreCase(dataType)){
        		strSql = "select WMSYS.WM_CONCAT(FNum) as FNum from tb_" + groupCode + "_Data_CashPayRec where FPortCode = " + 
		       		 dbl.sqlString(portCode) + " and FTsfTypeCode = " + dbl.sqlString(TsfTypeCode) + 
		       		 " and FSubTsfTypeCode =" + dbl.sqlString(SubTsfTypeCode) + " and FTransDate = " + dbl.sqlDate(date) +
		       		 " and FCashAcccode = " + dbl.sqlString(IVPayCatCodeOrAcc);
        	}
            
            PreparedStatement pst = conn.prepareStatement(strSql);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
            	nums = rs.getString("FNum");
            }
            rs.close();
            pst.close();
            
            if(nums != null){
	            if(nums.trim().length()> 0 && !"null".equalsIgnoreCase(nums)){
	            	YssDbOperSql operSql = new YssDbOperSql(this.pub);
	            	nums = operSql.sqlCodes(nums);
		            bTrans = true;
		            conn.setAutoCommit(false);
		            
		            String numTrans = "";
		            
		            String sql = " select WMSYS.WM_CONCAT(Fnum) as Fnum from tb_" + groupCode + "_cash_transfer where FIPRNum in (" + nums + ")";
		            
		            pst = conn.prepareStatement(sql);
		            rs = pst.executeQuery();
		            if(rs.next()){
		            	numTrans = rs.getString("FNum");
		            }
		            
		            rs.close();
		            pst.close();
		            
		            if(numTrans != null){
			            if(numTrans.trim().length() > 0 && !"null".equalsIgnoreCase(numTrans)){
			            	numTrans = operSql.sqlCodes(numTrans);
			            	dbl.executeSql("delete from tb_" + groupCode + "_cash_subtransfer where FNum in (" + numTrans +")");
			            }
			            
			            if(numTrans.trim().length() > 0 && !"null".equalsIgnoreCase(numTrans)){ 	
			            	dbl.executeSql("delete from tb_" + groupCode + "_cash_transfer where FNum in (" + numTrans +")");
			            }
		            }
		            //删除应收应付
		            if("invest".equalsIgnoreCase(dataType)){
		            	this.deleteInvestPayRec(groupCode,nums);
		            }else if("cash".equalsIgnoreCase(dataType)){
		            	this.deleteCashPayRec(groupCode,nums);
		            }
		            conn.commit();
		            bTrans = false;
		            conn.setAutoCommit(true);	              
	            }
            }
        } catch (Exception e) {
            System.out.println("删除资金调拨数据出错\r\n" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	/**
	 * add by huangqirong 2012-06-13 story #2326 
	 * 删除运营应收应付数据
	 * */
	private void deleteInvestPayRec(String groupCode , String nums){
		DbBase dbl = this.pub.getDbLink();
		String strSql = "";
        try {        	
            strSql = "delete from tb_" + groupCode + "_Data_InvestPayRec where FNum in ("+ nums +")";
            dbl.executeSql(strSql);
            
        } catch (Exception e) {
            System.out.println("删除运营应收应付数据出错\r\n" + e.getMessage());
        } finally {
        	
        }
	}
	
	/**
	 * add by huangqirong 2012-06-13 story #2326 
	 * 删除现金应收应付数据
	 * */
	private void deleteCashPayRec(String groupCode , String nums){
		DbBase dbl = this.pub.getDbLink();
		String strSql = "";
        try {
            strSql = "delete from tb_" + groupCode + "_Data_CashPayRec where FNum in (" + nums + ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            System.out.println(" 删除现金应收应付数据出错\r\n" + e.getMessage());
        } finally {
        	
        }
	}
	
	/**
	 * add by huangqirong 2012-06-13 story #2326 
	 * 删除划款指令
	 * */
	private void deleteCommand(String groupCode , String portCode ,String date , String gcsNum){
		DbBase dbl = this.pub.getDbLink();
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务        
        Connection conn = null ;
        try {
        	conn = dbl.loadConnection();
        	bTrans = true;
            conn.setAutoCommit(false);
            strSql = "delete from tb_" + groupCode + "_Cash_Command where FPortCode = " +
            		   dbl.sqlString(portCode) + " and FDS = 0 and FCommandDate = " + dbl.sqlDate(date) + 
            		   " and FGCSNum = " + dbl.sqlString(gcsNum);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            System.out.println("删除划款指令数据出错\r\n" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }		
	}
	
	
	/**
	 *  add by huangqirong 2012-04-17 story #2326
	 *  更新GCS自动处理数据状态
	 * */
	private void updateGCSState(String groupCode , String num , String gcsNum){
		DbBase dbl = this.pub.getDbLink();
		String sql = " update Tb_"+groupCode+"_Cash_Command"+
									" set FGCSState='101' " +
									" where FGCSState='100' and FNum ="+dbl.sqlString(num) +" and FGCSNum = " +dbl.sqlString(gcsNum) ;
		try {
			dbl.executeSql(sql);
		} catch (Exception e) {
			this.replyCode = "1";
		}
	} 
	
	
	/**
	 *  add by huangqirong 2012-04-17 story #2326
	 *  生成指令编号
	 * */
	private String getMaxNextAddOneToFNum(String groupCode , String sdate) {
    	String sRes = "";
    	ResultSet rs = null;
    	DbBase dbl = this.pub.getDbLink();
    	String date = YssFun.formatDate(this.toDate(sdate), "yyyyMMdd");
    	try {
    		String sqlStr = "SELECT " + dbl.sqlString(date) + " || MAX(to_number(substr(FNum,9,length(FNum) - 8))) || '' AS FNum FROM " +
	            "tb_"+groupCode+"_cash_command" +
	            " WHERE FNum LIKE " + dbl.sqlString(date+"%");
	        rs = dbl.openResultSet(sqlStr);
	        if (rs.next()) {
	            if (rs.getString("FNum") != null && rs.getString("FNum").length() > 8) {
	            	if(rs.getString("FNum").endsWith("9")){
	            		sRes = rs.getString("FNum").substring(0,rs.getString("FNum").length() - 1) + "10";
	            	}else{
	            		long num = Long.valueOf(rs.getString("FNum").substring(8,rs.getString("FNum").length())) + 1;	            		
		            	sRes = rs.getString("FNum").substring(0,8) + (num + "");
	            	}
	            } else {
	                sRes = date + "1" ;
	            }
	        } else {
	        	sRes = "1";
			}
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
        return sRes;
	}
	
	
	private String oldPrefix = "";//add by huangqirong 2012-04-06 story #2326
	
	/*
	 * add by huangqirong 2012-04-06 story #2325
	 * 修改表前缀
	 * */
	private void setPreFixTb(String tbFix){
		this.oldPrefix = pub.getPrefixTB();			
		if(!tbFix.equalsIgnoreCase(this.oldPrefix)){			
			pub.setPrefixTB(tbFix);
			pub.setAssetGroupCode(tbFix);
		}
	}
	
	/**
	 * add by huangqirong 2012-04-06 story #2325
	 * modify huangqirong 2012-10-25 story #2328 private - > protected
	 * 市场代码
	 * */
	protected Hashtable<String, String> getMarket(String groupCode,String dictCode){
		Hashtable<String, String> markets = null;
		String sql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from Tb_"+groupCode+"_Dao_Dict "+
		" a left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode left join" +
		" (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode where 1 = 1 " +
		" and FDictCode = ? and FSrcConent <> 'null'" +
		" order by a.FCreateTime desc, a.FCheckTime desc, a.FDictCode";
		ResultSet rs = null;
		PreparedStatement psp = null;
		try {
		psp = connection.prepareStatement(sql);
		psp.setString(1, dictCode);
		rs = psp.executeQuery();			
		while(rs.next()){
			if(markets == null)
				markets = new Hashtable<String, String>();
			markets.put(rs.getString("FSRCCONENT"),rs.getString("FCNVCONENT"));
		}
		rs.close();
		psp.close();
		}catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return markets;
	}
	
	/**
	 * 证券余额接口处理
	 * add huangqirong 2012-04-06 story #2325
	 * @throws YssException 
	 */
	private void dealSecurity(List<Element> bizData ) {
		try {
			//创建表
			if(!pub.getDbLink().yssTableExist("Stock_Check_Security_GCS")){
				//DbBase dbl = pub.getDbLink();
				String createTableSql = "create table Stock_Check_Security_GCS(" +
										" Fportcode VARCHAR2(20)," +
									    " Fdate DATE," +
									    " FExchageCode VARCHAR2(20)," +
									    " FSECURITYCODE VARCHAR2(20)," +
									    " FStorageAmount Numeric(19,4)," +
									    " FASSETGROUPCODE VARCHAR2(3)" +
									    " )"; 
				pub.getDbLink().executeSql(createTableSql);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		

		String insertSql = "INSERT INTO Stock_Check_Security_GCS"
				+ "(Fportcode,Fdate,FExchageCode,FsecurityCode,FStorageAmount,FASSETGROUPCODE) values "
				+ "(?,?,?,?,?,?)";
		String deleteSql = "DELETE FROM Stock_Check_Security_GCS WHERE Fportcode = ? and Fdate = ? and FExchageCode = ? and FSECURITYCODE = ? and FASSETGROUPCODE = ? ";		
		PreparedStatement pst = null;
		Element data = null;
		try {
			if (bizData.size() > 0) {
				for (int i = 0; i < bizData.size(); i++) {
					data = bizData.get(i);
					
					//资产代码转换组合代码
					String fsetcode = data.elementText("fsetid");	//fsetcode -> fsetid
					String fmarket = data.elementText("fszsh"); 	//fmarket -> fszsh
					String fdate = data.elementText("fdate");
					String fzqdm = data.elementText("fzqdm");
					double fnum = Double.parseDouble(data.elementText("fsl"));	//fnum -> fsl
					
					String [] groupPorts = getPortCodeBySetCode(fsetcode);//根据资产代码和获取组合和组合群
					String portCode = groupPorts[1]; //关联组合
					String groupCode = groupPorts[0];
					if(portCode.trim().length() == 0)
						continue ;
					
					Hashtable<String , String> markets = this.getMarket(groupCode,"Dict_Market_GCS"); //交易所代码
					String exChangeCode = "" ;
					
					if(markets.get(fmarket) != null){
						exChangeCode = markets.get(fmarket);
					}else {
						this.replyCode = "1" ;
						this.replyRemark = "导入失败:QDII系统(资产代码" +fsetcode+ ")接口字典Dict_Market_GCS中无法识别市场标志"+fmarket+"。" ;
						break;
					}
					
					int cs = this.countSecCodeByMark(groupCode, exChangeCode, fzqdm);
					String secsCode = "";
					
					if(cs == 1 ){
						secsCode = this.getSecCodeByMark(groupCode, exChangeCode, fzqdm);
					}else if(cs == 0){
						this.replyCode = "1";
						this.replyRemark = "导入失败:QDII系统(资产代码" + fsetcode + ")证券信息设置表中没有上市代码为" + fzqdm + "交易所，为" + exChangeCode + "的证券。";
						break;
					}else if(cs > 1){
						this.replyCode = "1";
						this.replyRemark = "导入失败:QDII系统(资产代码" + fsetcode + ")证券信息设置表中有多个上市代码为" + fzqdm+"交易所，为" + exChangeCode + "的证券。";
						break;
					}
					
					//删除重复数据
					pst = connection.prepareStatement(deleteSql);
					// 获取对账日期					
					pst.setString(1, portCode);
					pst.setDate(2, new java.sql.Date(this.toDate(fdate).getTime()));
					pst.setString(3, exChangeCode);
					pst.setString(4, secsCode);
					pst.setString(5, groupCode);
					pst.execute();
					
					pst = connection.prepareStatement(insertSql);
					pst.setString(1, portCode);
					pst.setDate(2, new java.sql.Date(this.toDate(fdate).getTime()));
					pst.setString(3, exChangeCode);
					pst.setString(4, secsCode);
					pst.setDouble(5, fnum);
					pst.setString(6, groupCode);
					
					pst.addBatch();
				}
				pst.executeBatch();
				// 提交事务
				connection.commit();
			}
		} catch (SQLException e) {
			replyCode = "1";
		}finally{
			if(pst != null){//STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
	}
	
	
	/**
	 * add huangqirong 2012-04-06 story #2325
	 * 根据上市代码和交易所代码获取证券代码
	 */
	private int countSecCodeByMark(String preTable , String exchangeCode , String mark){
		ResultSet rs = null;
		PreparedStatement pstp = null;
		int cs=0;
		try {
			pstp = connection.prepareStatement("select count(*) as fcs from tb_" + preTable + "_para_security where FCheckState=1 and FMarketCode=? and FExchangeCode = ?");
			pstp.setString(1, mark);
			pstp.setString(2, exchangeCode);
			//pstp.addBatch();
			rs = pstp.executeQuery();
			if(rs.next()){
				cs = rs.getInt("fcs");
			}
			rs.close();
			pstp.close();
		}catch (Exception e) {
		
		}
		return cs;
	}
	
	/**
	 * add huangqirong 2012-04-06 story #2325
	 * 根据上市代码和交易所代码获取证券代码
	 */
	private String getSecCodeByMark(String preTable , String exchangeCode ,String mark){
		ResultSet rs = null;
		PreparedStatement pstp = null;
		String secCode="";
		try {
			pstp = connection.prepareStatement("select * from tb_" + preTable + "_para_security where FCheckState=1 and FMarketCode=? and FExchangeCode = ?");
			pstp.setString(1, mark);
			pstp.setString(2, exchangeCode);
			rs = pstp.executeQuery();
			if(rs.next()){
				secCode = rs.getString("FSecurityCode");
			}
			rs.close();
			pstp.close();
		}catch (Exception e) {
		
		}
		return secCode;
	}
	
	/**
	 * 资金余额接口处理
	 * add huangqirong 2012-04-06 story #2325
	 * @throws YssException 
	 */
	private void dealBalance(List<Element> bizData ) {
		try {
			//创建表
			if(!pub.getDbLink().yssTableExist("Stcok_Check_Cash_GCS")){			
				String createTableSql = "create table Stock_Check_Cash_GCS( " +
										" Fportcode VARCHAR2(20)," +
									    " Fdate DATE," +
									    " FCashAccCode VARCHAR2(50)," +
									    " FAccBalance Numeric(30,4)," +
									    " FASSETGROUPCODE VARCHAR2(3)" +
									    " )";
				pub.getDbLink().executeSql(createTableSql);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		String insertSql = "INSERT INTO Stcok_Check_Cash_GCS"
				+ "(Fportcode,Fdate,FCashAccCode,FAccBalance,FASSETGROUPCODE) values "
				+ "(?,?,?,?,?)";
		String deleteSql = "DELETE FROM Stcok_Check_Cash_GCS WHERE Fportcode = ? and Fdate = ? and FCashAccCode = ? and FASSETGROUPCODE = ? ";		
		PreparedStatement pst = null;
		Element data = null;
		try {
			if (bizData.size() > 0) {
				for (int i = 0; i < bizData.size(); i++) {
					data = bizData.get(i);
					
					String cashAcccount = "";					
					
					//资产代码转换组合代码
					String fsetcode = data.elementText("fsetid"); // fsetcode -> fsetid 改动
					String facc = data.elementText("faccountcode");		//facc -> faccountcode	改动
					String fdate = data.elementText("fdate");
					String fhbfh = data.elementText("fhbfh");
					double famount = Double.parseDouble(data.elementText("fbalance"));	//famount -> fbalance 改动
					
					String [] groupPorts = getPortCodeBySetCode(fsetcode);
					String portCode = groupPorts[1]; //关联组合
					String groupCode = groupPorts[0];
					if(portCode.trim().length() == 0)
						continue ;
					
					Hashtable<String ,String> curyCodes = this.getMarket(groupCode, "Dict_Curycode_GCS");					
					
					String curyCode = curyCodes.get(fhbfh);
					
					if(curyCode == null){
						this.replyCode = "1" ;
						this.replyRemark = "QDII系统(资产代码"+fsetcode+")接口字典Dict_Curycode_GCS中无法识别货币符号:"+fhbfh+"。";
						break;
					}
					
					int rowcount = this.getAccCount(groupCode,facc,curyCode);
					
					if( rowcount == 1){
						cashAcccount = this.getCashAcc(groupCode, facc,curyCode);
					}else if(rowcount == 0 ){
						this.replyCode = "1" ;
						this.replyRemark = "导入失败:QDII系统(资产代码"+fsetcode+")现金帐户设置表中没有银行帐号为"+facc+"且币种符号为"+fhbfh+"的现金帐户。";	
						break;
					}else if(rowcount > 1){
						this.replyCode = "1" ;
						this.replyRemark = "导入失败:QDII系统(资产代码"+fsetcode+")现金帐户设置表中有多个银行帐号为"+facc+"且币种符号为"+fhbfh+"的现金帐户。";
						break;
					}
					
					//删除重复数据
					pst = connection.prepareStatement(deleteSql);
					// 获取对账日期					
					pst.setString(1, portCode);
					pst.setDate(2, new java.sql.Date(this.toDate(fdate).getTime()));
					pst.setString(3, cashAcccount);
					pst.setString(4, groupCode);
					pst.execute();
					
					pst = connection.prepareStatement(insertSql);
					pst.setString(1, portCode);
					pst.setDate(2, new java.sql.Date(this.toDate(fdate).getTime()));
					pst.setString(3, cashAcccount);
					pst.setDouble(4, famount);
					pst.setString(5, groupCode);
					
					pst.addBatch();
				}
				pst.executeBatch();
				// 提交事务
				connection.commit();
			}
		} catch (SQLException e) {
			replyCode = "1";
		}finally{
			if(pst != null){ //add by huangqirong STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
	}
	
	/**
	 * add huangqirong 2012-04-06 story #2325
	 * modify huangqirong 2012-10-24 story #2328 private -> protected
	 * 根据资产代码获取组合代码
	 */
	protected String[] getPortCodeBySetCode(String setCode){
		ResultSet rs = null;		
		PreparedStatement pstp = null;
		PreparedStatement portPstp = null;
		String portcode = "";
		String groupCode = "";
		try {
			pstp = connection.prepareStatement("select * from Tb_Sys_Assetgroup where FSysCheck = 1 and FLocked = 0 order by FASSETGROUPCODE");
			rs = pstp.executeQuery();
			while(rs.next()){
				String preTab = rs.getString("FTABPREFIX");
				
				portPstp = connection.prepareStatement("select * from tb_" + preTab + "_para_portfolio where FCHECKSTATE = 1 and FEnabled = 1 and FAssetCode = ?");//+this.pub.getDbLink().sqlString(setCode) );
				portPstp.setString(1, setCode);
				//portPstp.addBatch();
				
				ResultSet rs1 = portPstp.executeQuery();
				if(rs1.next()){
					portcode = rs1.getString("FPortCode");
					groupCode = preTab;
					break;
				}
				rs1.close();
				portPstp.close();
			}
			rs.close();
			pstp.close();
		}catch (Exception e) {
			this.replyCode = "1";
			System.out.println("处理GCS数据时查询资产代码及组合时出错：" + e.getMessage());
		}
		return new String[]{groupCode,portcode};
	}
	
	
	/**
	 * add huangqirong 2012-04-06 story #2325
	 * 设置为某银行账号的现金账户个数
	 *modify huangqirong 2012-10-25 story #2328 private -> protected
	 */
	protected int getAccCount(String groupCode , String bankCode ,String curyCode){
		String sql = "select count(*) as fct from (select FCASHACCCODE,max(FSTARTDATE) as FSTARTDATE from " +
						" Tb_"+groupCode+"_Para_CashAccount where FCHECKSTATE = 1 group by FCASHACCCODE) pa " +
						" join ( select * from Tb_"+groupCode+"_Para_CashAccount where FCHECKSTATE = 1 and FBankAccount = ? " +
						" and FCuryCode = ?) pb " +
						" on pa.FCASHACCCODE = pb.FCASHACCCODE and pa.FSTARTDATE = pb.FSTARTDATE ";
		ResultSet rs = null;
		int rowCount = 0 ;
		PreparedStatement psp = null;
		try {
			psp = connection.prepareStatement(sql);
			psp.setString(1, bankCode);
			psp.setString(2, curyCode);
			//psp.addBatch();
			rs = psp.executeQuery();			
			if(rs.next()){
				rowCount = rs.getInt("fct");
			}
			rs.close();
			psp.close();
		}catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return rowCount;
	}
	
	/**
	 * add huangqirong 2012-04-06 story #2326
	 * modify huangqirong 2012-10-24 story #2328 private -> protected
	 * 获取数据
	 *
	 */
	protected int getCountbySql(String sql, String field){
		ResultSet rs = null;
		int count = 0;
		PreparedStatement psp = null;
		try {
			psp = connection.prepareStatement(sql);			
			rs = psp.executeQuery();
			if(rs.next()){
				count = rs.getInt(field);
			}
			
		}catch (Exception e) {
			System.out.print(e.getMessage());
		}finally{
			try {
				rs.close();
				psp.close();
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			}
		}
		return count;
	}
	
	/**
	 * add huangqirong 2012-04-06 story #2326
	 * modify huangqirong 2012-10-24 story #2328 private -> protected
	 * 获取数据
	 *
	 */
	protected String getDatabySql(String sql, String field){
		ResultSet rs = null;
		String result = "";
		PreparedStatement psp = null;
		try {
			psp = connection.prepareStatement(sql);			
			rs = psp.executeQuery();
			if(rs.next()){
				if(rs.getString(field) != null)
					result = rs.getString(field);
			}
			rs.close();
			psp.close();
		}catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return result;
	}
	
	/**
	 * add huangqirong 2012-04-06 story #2326
	 * modify huangqirong 2012-10-24 story #2328 private -> protected
	 * 获取数据
	 *
	 */
	protected String getDatasbySql(String sql, String fields){
		ResultSet rs = null;
		String result = "";
		String [] flds = fields.split(",");
		PreparedStatement psp = null;
		try {
			psp = connection.prepareStatement(sql);			
			rs = psp.executeQuery();
			if(rs.next()){
				for (int i = 0; i < flds.length; i++) {
					result += rs.getString(flds[i])+",";
				}
			}
			if(result.trim().length() > 0)
				result = result.substring(0,result.trim().length() -1);
			
			rs.close();
			psp.close();
		}catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return result;
	}
	
	/**
	 * add huangqirong 2012-04-06 story #2325
	 * modify huangqirong 2012-10-24 story #2328 private -> protected
	 * 设置为某银行账号的现金账户
	 *
	 */
	protected String getCashAcc(String groupCode , String bankCode, String curyCode){
		String sql = "select pb.* from (select FCASHACCCODE,max(FSTARTDATE) as FSTARTDATE from " +
					" Tb_"+groupCode+"_Para_CashAccount where FCHECKSTATE = 1 group by FCASHACCCODE) pa " +
					" join ( select * from Tb_"+groupCode+"_Para_CashAccount where FCHECKSTATE = 1 and FBankAccount = ?" +
					" and FCuryCode = ? ) pb " +
					" on pa.FCASHACCCODE = pb.FCASHACCCODE and pa.FSTARTDATE = pb.FSTARTDATE ";
		ResultSet rs = null;
		String result = "" ;
		PreparedStatement psp = null;
		try {
			psp = connection.prepareStatement(sql);
			psp.setString(1, bankCode);
			psp.setString(2, curyCode);
			//psp.addBatch();
			rs = psp.executeQuery();
			if(rs.next()){
				result = rs.getString("FCASHACCCODE");
			}
			rs.close();
			psp.close();
		}catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return result;
	}
	
	
	/**
	 * 检查请求报文格式，是否符合规范,请求报文格式如下：
	 * <request>
			<head>
				<channel>100</channel>   渠道号
				<branchid>0000</branchid> 机构号
				<version>1.0</version>     版本号
				<msgno>XXXX</msgno>	报文通讯流水号
				<smsgtype>000</smsgtype> swift报文类型
				<txcode>100001001</txcode> 交易码
				<override>0</override> 覆盖标识
			</head>
			<body> 业务数据
   				<bizdata></bizdata>
			</body>
		<request/>
	 */
	protected String checkRequestMsg()  //modify huangqirong 2012-10-24 story #2328 原来 private --> protected 
	{
		String replyCode = "0000";
		try {
			if(null != requestMsg){
				//还原报文为xml文档
				requestMsgXml = DocumentHelper.parseText(requestMsg);
				//add by huangqirong 2012-11-06 story #3227
				if(this.requestMsgXml != null){
					String gcsDataPath = this.getPropertiesPath();
					if(gcsDataPath != null && gcsDataPath.trim().length() > 1){
						gcsDataPath = !gcsDataPath.endsWith(File.separator) ? gcsDataPath + File.separator : gcsDataPath ;
						this.saveAsXml(this.requestMsgXml , gcsDataPath , "GCSData" + File.separator);
					}
				}
				//首先获取文档根节点
				Element requestElement = requestMsgXml.getRootElement();
				//根元素request错误，无法解析报文类型,错误码1002
				if(requestElement!=null && requestElement.getName().equalsIgnoreCase("request")){	
					//head栏位解析失败，错误码1003
					Element head = requestElement.element("head");
					if(head!=null) {
						//msgno栏位解析失败，错误码1005
						if(head.element("msgno")==null) replyCode = "1005";
						//txcode栏位解析失败，错误码1006
						if(head.element("txcode")==null) replyCode = "1006";
						//smsgtype栏位解析失败，错误码1007
						if(head.element("smsgtype")==null) replyCode = "1007";
						//override栏位解析失败，错误码1008
						if(head.element("override")==null) replyCode = "1008";
					}
					else{
						replyCode = "1003";
						//不再检查，返回
						return replyCode;
					}
					Element body = requestElement.element("body");
					//body栏位解析失败，错误码1004
					if(body!=null) {						
						if(body.element("datainfo") != null) replyCode = "0000";//add by huangqirong 2012-04-22 story #2325
						else if(body.element("bizdata")== null) replyCode = "1011";//modify by huangqirong 2012-04-22 story #2325
					}
					else{
						replyCode = "1004";
					}
					
				}
				else{	
					replyCode = "1002";
				}
			}
			else{	//接收到的报文为空，错误码1001
				replyCode = "1001";
			}
		} catch (DocumentException e) {
			replyCode = "1999";
		}
		return replyCode;
	}
	
	private void saveAsXml(Document doc , String dataPath , String partPath){
		Date date = new Date();
		SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateTime = formatterDateTime.format(date);
		String sdate = formatterDate.format(date);
		String msgno = SwiftMsgDeal.generateSerialNumber();	// 获取流水号
		OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("GB2312");    // 指定XML编码 
        XMLWriter writer = null;
		try {
			//路径规则 用户设置的路径 + 部分路径(GCSData/GCSDataLog) + 时间 + 请求流水号
			String filePath = dataPath + partPath + sdate + File.separator;
			File file = new File(filePath);
			if(!file.exists())
				file.mkdirs();
			writer = new XMLWriter(new FileWriter(filePath + dateTime + msgno + ".xml"),format);
			writer.write(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * 流水号  
	 */  
	private static volatile int serialNumber = 0;
	
	/**  
	 * 生成流水号  
	 * 从1 - 999999，不足六位，从右往左补0  
	 * @return  
	 */  
	public static synchronized String generateSerialNumber(){   
	    int n = serialNumber = ++serialNumber;   
	    if(n == (999999 + 1)){   
	        serialNumber = n = 1;   
	    }   
	       
	   StringBuffer strbu = new StringBuffer(6);   
	    strbu.append(n);   
	    for(int i=0, length=6-strbu.length(); i<length; i++){   
	        strbu.insert(0, 0);   
	    }   
	       
	    return strbu.toString();   
	}  
	
	/**
	 * add by huangqirong 2012-11-06 story #3227
	 * 读取gcsdatapath.properties文件是否设置路径
	 * */
	private String getPropertiesPath(){
		File file = new File(File.separator + "gcsdatapath.properties");
		Properties props = new Properties();
		String dataPath = "";
		InputStream in = null;
		try {
			if(file.exists()){
				in = new BufferedInputStream(new FileInputStream(file));
				props.load(in);
				dataPath = new String (props.getProperty("gcs.datapath").getBytes("8859_1"));
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return dataPath == null ? "" : dataPath;
	}
   
	/**
	 * 生成应答报文
	 * modify huangqirong 2012-04-06 story #2325
	 * @param replyCode	应答码 若正常则为0000，否则为对应的异常码
	 * @param replyInfo 应答信息
	 * @param txCode 交易码
	 * @param msgNo 报文通讯流水号
	 * @return xml结构的应答报文
	 *  modify by huangqirong 2012-10-24 story #2328  private -> protected
	 * 应答报文结构如下：
	 * <response>	
			<head>
				<channel>100</channel>    			
				<branchid>0000</branchid> 
				<version>1.0</version>  
				<txcode>100001001</txcode>   
				<msgno>XXXX</msgno>
     			<smsgtype>000</smsgtype> 
     			<override>0</override>
				<replycode>00</replycode> 		
				<replyremark>XXXXXXXX</replyremark>		
			</head>
			<body>
   				<bizdata></bizdata>
			</body>
		</response>
	 */
	protected void buildReplyMsg(String replyCode,String replyInfo,String txCode,String msgNo ,String otherElement) 
	{	
		Element responseElement = null;
		Element headElement = null;
		Element bodyElement = null;
		replyMsgXml = DocumentHelper.createDocument();
		//增加根节点response
		responseElement = replyMsgXml.addElement("response");
		//response节点下增加head节点
		headElement = responseElement.addElement("head");
		//response节点下增加body节点
		bodyElement = responseElement.addElement("body");
		//head节点下增加channel节点，值为100
		headElement.addElement("channel").setText("100");
		//head节点下增加branchid节点，值为0000
		headElement.addElement("branchid").setText("0000");
		//head节点下增加version节点，值为1.0
		headElement.addElement("version").setText("1.0");
		//head节点下增加txcode节点
		headElement.addElement("txCode").setText((txCode != null ? txCode:"00000000"));
		//head节点下增加msgno节点
		headElement.addElement("msgno").setText((msgNo != null ? msgNo:"00000000"));
		//head节点下增加smsgtype节点,值为000
		headElement.addElement("smsgtype").setText("000");
		//head节点下增加override节点，值为0
		headElement.addElement("override").setText("0");
		//head节点下增加replycode节点
		headElement.addElement("replycode").setText((replyCode != null ? replyCode:"0000"));
		//head节点下增加replyremark节点
		headElement.addElement("replyremark").setText(replyInfo != null ? replyInfo:"");
		//body节点下增加bizdata节点
		bodyElement.addElement(otherElement).setText("");
		
		//add by huangqirong 2012-11-06 story #3227
		if(this.replyMsgXml != null){
			String gcsDataPath = this.getPropertiesPath();
			gcsDataPath = !gcsDataPath.endsWith(File.separator) ? gcsDataPath + File.separator : gcsDataPath ;
			this.saveAsXml(this.replyMsgXml , gcsDataPath , "GCSDataLog" + File.separator);
		}
		//---end---
	}

	/**
	 * 持仓数据处理
	 * @throws YssException 
	 */
	private void dealPosition(List<Element> bizData) {
		String insertSql = "INSERT INTO tmp_data_position"
				+ "(income_date,cstmr_no,custody_acc,chk_date,chk_fund,sec_code,rec_price,sec_hold,valid_num,sngl_secest,fin_date) values "
				+ "(?,?,?,?,?,?,?,?,?,?,?)";
		String deleteSql = "DELETE FROM tmp_data_position WHERE chk_date = ? and fin_date = ?";
		String chk_date = null;
		String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()).toString();
		PreparedStatement pst = null;
		Element data = null;
		List<Element> securitys = null;
		try {
			if (bizData.size() != 0) {
				// 删除当日同一对账日期的数据
				pst = connection.prepareStatement(deleteSql);
				// 获取对账日期
				chk_date = bizData.get(0).elementText("chk_date");
				pst.setString(1, chk_date);
				pst.setDate(2, new java.sql.Date(this.toDate(curDate).getTime()));
				pst.execute();
				// 插入持仓数据
				pst = connection.prepareStatement(insertSql);
				for (int i = 0; i < bizData.size(); i++) {
					// 获取一条bizdata的数据，根据报文结构获取持仓数据的公共字段
					data = bizData.get(i);
					// 收报日期income_date
					String income_date = data.elementText("income_date");
					// 客户号 cstmr_no
					String cstmr_no = data.elementText("cstmr_no");
					// 托管账号 custody_acc
					String custody_acc = data.elementText("custody_acc");
					// 对账日期 chk_date
					// 对账基金chk_fund
					String chk_fund = data.elementText("chk_fund");
					// 因为一个bizdata节点中包含了多个security节点
					// 遍历所有的security节点获取持仓数据
					securitys = data.elements("security");
					if (securitys != null) {
						for (int j = 0; j < securitys.size(); j++) {
							Element security = securitys.get(j);
							// 报文日期
							pst.setString(1, income_date);
							// 客户号
							pst.setString(2, cstmr_no);
							// 托管账号
							pst.setString(3, custody_acc);
							// 对账日期
							pst.setString(4, chk_date);
							// 对账基金
							pst.setString(5, chk_fund);
							// 证券代码
							pst.setString(6, security.elementText("sec_code"));
							// 收盘价
							pst.setDouble(7,Double.valueOf((security.elementText("rec_price") != null 
																&& security.elementText("rec_price").trim().length() > 0) ? 
																		security.elementText("rec_price") : "0"));
							// 证券持仓
							pst.setDouble(8,Double.valueOf((security.elementText("sec_hold") != null 
																	&& security.elementText("sec_hold").trim().length() > 0) ? 
																			security.elementText("sec_hold") : "0"));
							// 可用数量
							pst.setDouble(9,Double.valueOf((security.elementText("valid_num") != null 
																		&& security.elementText("valid_num").trim().length() > 0) ? 
																				security.elementText("valid_num") : "0"));
							// 单只证券估值
							pst.setDouble(10,Double.valueOf((security.elementText("sngl_secest") != null 
																		&& security.elementText("sngl_secest").trim().length() > 0) ? 
																				security.elementText("sngl_secest") : "0"));
							// 读入日期
							pst.setDate(11, new java.sql.Date(this.toDate(curDate).getTime()));
							pst.addBatch();
						}
					}
				}
				pst.executeBatch();
				// 提交事务
				connection.commit();
				// -----------end----------
				pst.close();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
			}
			
		} catch (SQLException e) {
			replyCode = "1014";
		}
	}


	/**
	 * 证券交易结算数据处理
	 * @throws YssException 
	 */
	private void dealTradeSettle(List<Element> bizData) throws Exception {
		String tradeDate = null;
		PreparedStatement pst = null;
		Element data = null;
		String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()).toString();
		String deleteSql = "DELETE FROM tmp_data_MT540_3 WHERE  tx_date = ? and fin_date = ?";
		// 插入临时表的sql
		String insertSql = "INSERT INTO tmp_data_MT540_3"
				+ "(TX_DATE,CLR_DATE,DEAL_DIRECTION,BAR_PRICE,SEC_CODE,SEC_MARKET,BAR_NUM,CUSTODY_ACC,BAR_AMT,BAR_CUR,"
				+ "BROKERAGE,CLR_CUR,CLR_AMT,EXC_CODE,CERT_CODE,FEE,FIN_DATE)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			if (bizData.size() != 0) {
				//处理数据到临时表中
				// 删除同一交易日期的数据
				pst = connection.prepareStatement(deleteSql); 
				 tradeDate = bizData.get(0).elementText("tx_date");
				 tradeDate = YssFun.formatDate(YssFun.parseDate(tradeDate, "yyyyMMdd"), "yyyy-MM-dd");
				 
				 pst.setString(1,tradeDate);
				 pst.setDate(2, YssFun.toSqlDate(curDate));
				 pst.execute();
				// 插入临时表数据
				pst = connection.prepareStatement(insertSql);
				for (int i = 0; i < bizData.size(); i++) {
					data = bizData.get(i);
					//交易日期
					//pst.setString(1, data.elementText("tx_date"));
					pst.setString(1, YssFun.formatDate(YssFun.parseDate(data.elementText("tx_date"), "yyyyMMdd"), "yyyy-MM-dd"));
					//清算日期
					//pst.setString(2, data.elementText("clr_date"));
					pst.setString(2, YssFun.formatDate(YssFun.parseDate(data.elementText("clr_date"), "yyyyMMdd"), "yyyy-MM-dd"));
					//买卖方向
					pst.setString(3, data.elementText("deal_direction"));
					//成交价格
					pst.setDouble(4, YssFun.toDouble(data.elementText("bar_price")));
					//证券代码
					pst.setString(5, data.elementText("sec_code"));
					//证券市场
					pst.setString(6, data.elementText("sec_market"));
					//成交数量
					pst.setDouble(7,YssFun.toDouble(data.elementText("bar_num")));
					//托管账号
					pst.setString(8, data.elementText("custody_acc"));
					//成交金额
					pst.setDouble(9, YssFun.toDouble(data.elementText("bar_amt")));
					//成交币种
					pst.setString(10,data.elementText("bar_cur"));
					//佣金
					pst.setDouble(11, YssFun.toDouble(data.elementText("brokerage")));
					//清算币种
					pst.setString(12, data.elementText("clr_cur"));
					//清算金额
					pst.setDouble(13, YssFun.toDouble(data.elementText("clr_amt")));
					//交易所代码
					pst.setString(14,data.elementText("exc_code"));
					//券商代码
					pst.setString(15, data.elementText("cert_code"));
					//费用
					pst.setDouble(16, YssFun.toDouble(data.elementText("fee")));
					//读数日期
					pst.setDate(17, YssFun.toSqlDate(curDate));
					pst.addBatch();
				}
				pst.executeBatch();
				// 提交事务
				connection.commit();
				
				//---------把数据从临时表处理到交易数据表中
				insertSubTrade(tradeDate);
				//----------end--------------
				pst.close();
			}
			
		} catch (SQLException e) {
			replyCode = "1014";
		}
	}
	
	/**
	 * 把临时表中的证券结算数据调用接口处理的逻辑，处理到交易数据表中
	 */
	private void insertSubTrade(String startDate) throws Exception
	{	
		String ifCode = "swiftMsgTrade";
		//String ifCode = "ExchangeRate-GY";
		DaoInterfaceManageBean ifManageBean = null;	//接口处理的类
		String pretCodes = "";	//接口的预处理代码
		String assetGroupCode = "";	//组合群代码
		String webRoot = this.getClass().getClassLoader().getResource("/").toString();	//应用程序上下文路径
		webRoot = webRoot.substring(0,(webRoot.length()-16));
		ArrayList<String> groups= null;	//拥有swiftMsgTrade接口的组合群代码集
		try{
			//初始化接口处理类的各个参数
			ifManageBean = new DaoInterfaceManageBean();
			ifManageBean.setYssPub(pub);
			//因为在后台不知道当前登录的组合群是哪一个，故循环所有的拥有该接口的组合群代码,然后调用接口的处理
			groups = getAssetGroups(ifCode);
			if(null != groups && groups.size() != 0)
			{
				//处理所有拥有该接口的组合群
				for(int i = 0;i<groups.size();i++)
				{	
					assetGroupCode = groups.get(i);
					//设置pub变量中当前组合群代码
					pub.setAssetGroupCode(assetGroupCode);
					pub.setPrefixTB(assetGroupCode);
					pub.setWebRoot(webRoot);
					pub.setUserCode(" ");
					pub.setPortBaseCury();
					//因为接口处理类没有把各个属性公开，故不直接对变量赋值
					//字符串的组成部分为起始日期+结束日期+接口代码+组合代码+beanId+allData+数据是否需要审核+交易席位+预处理代码+组合群代码+处理日期
					String beanStr = startDate+"\n"+startDate+"\n"+ifCode+"\n"+""+"\n"+"imp"+"\n"+""+"\n"+"1"+"\n"+""+"\n"+pretCodes+"\n"+assetGroupCode+"\n"+startDate;
					ifManageBean.parseRowStr(beanStr);
					//首先调用接口预处理的方法
					pretCodes = ifManageBean.doOperation("");
					//判断是否有预处理代码
					if(pretCodes!=null && pretCodes.length()!=0)
					{
						String[] str = pretCodes.split(","); 
						//循环预处理代码进行接口预处理
						for(int j=0;j<str.length;j++)
						{
							beanStr = startDate+"\n"+startDate+"\n"+ifCode+"\n"+""+"\n"+"imp"+"\n"+""+"\n"+"1"+"\n"+""+"\n"+pretCodes+"\n"+assetGroupCode+"\n"+startDate;
							//调用接口处理的方法
							ifManageBean.parseRowStr(beanStr);
							ifManageBean.getOperValue("doPretreat");
						}
					}
				}
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取拥有指定接口的组合群代码数组
	 * @param ifCode 接口代码
	 * @return 组合群代码数组
	 * @throws YssException
	 */
	private ArrayList<String> getAssetGroups(String ifCode) throws Exception
	{
		ArrayList<String> groups = new ArrayList<String>();
		ResultSet rs = null;
		ResultSet rs1 = null;
		String ifTableName = null;//自定义接口表名
		Statement stat = null;
		Statement stat1 = null;
		String sqlGroup = "SELECT FASSETGROUPCODE FROM TB_SYS_ASSETGROUP";
		String sqlCusConfig = null;
		String groupCode = null;
		try {
			if(null != ifCode && ifCode.length()!=0)
			{
				stat = connection.createStatement();
				stat1 = connection.createStatement();
				rs = stat.executeQuery(sqlGroup);
				while(rs.next())
				{	
					groupCode = rs.getString("FASSETGROUPCODE");
					ifTableName = "tb_"+groupCode+"_Dao_CusConfig";
					if(pub.getDbLink().yssTableExist(ifTableName))
					{	
						sqlCusConfig = "SELECT FCUSCFGCODE FROM "+ifTableName+" WHERE FCUSCFGCODE = '"+ifCode+"'";
						rs1 = stat1.executeQuery(sqlCusConfig);
						while(rs1.next())
						{
							groups.add(groupCode);
						}
					}
					
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				if(null != rs&&null != stat&&null != stat1)
				{
				rs.close();
				stat.close();
				stat1.close();
				}
				if(null != rs1)
				{
					rs1.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return groups;
	}
	/**
	 * 报文收发统计数据处理
	 */
	private void dealMsgSRStat(List<Element> bizData) throws Exception {
		StringBuffer buff = new StringBuffer();
		Element data = null;
		List<String> msgSRStat = new ArrayList<String>();
			for (int i = 0; i < bizData.size(); i++) {
				data = bizData.get(i);
				buff.append("起始时间: " + data.elementText("start_date") + ",");
				buff.append("终止时间: " + data.elementText("end_date") + ",");
				//获取报文统计元素
				Element stat = data.element("msgstatistics");
				if(stat != null){
				buff.append("收发报行: " + stat.elementText("send_receve_bank") + ",");// 收发报行
				buff.append("报文种类:  " + stat.elementText("swift_no") + ","); // 报文种类
				buff.append(" 收报笔数:" + stat.elementText("income_msg_num")
						+ ",");
				buff.append(" 发报笔数:" + stat.elementText("send_num")
						+ " ;");
				}
				msgSRStat.add(buff.toString());
			}
			msgCue.put("SRStat", msgSRStat);
		
	}

	/**
	 * 报文收发数据处理
	 */
	private void dealMsgSR(List<Element> bizData) throws Exception {
		StringBuffer buff = new StringBuffer();
		Element data = null;
		List<String> msgSR = new ArrayList<String>();
			for (int i = 0; i < bizData.size(); i++) {
				data = bizData.get(i);
				buff.append(data.elementText("real_awoke") + ";");
				msgSR.add(buff.toString());
			}
			msgCue.put("SR", msgSR);
	
	}

	/**
	 * 证券交易未匹配数据处理
	 */
	private void dealSecNoMatch(List<Element> bizData)throws Exception {
		StringBuffer buff = new StringBuffer();
		Element data = null;
		List<String> msgSecNoMatch = new ArrayList<String>();
			for (int i = 0; i < bizData.size(); i++) {
				data = bizData.get(i);
				buff.append(data.elementText("sec_no_cover") + ";");
				msgSecNoMatch.add(buff.toString());
			}
			msgCue.put("SecNoMatch", msgSecNoMatch);
		
	}
	
	/**
	 * add by huangqirong 2012-12-19 story #2327
	 * 类似vb的CDate函数，自动分析sDate，如格式正常，返回日期，否则报错。
     * 注意这里只能处理单纯日期，不处理时间，年份正常范围在0-99和1000－9999
     *仅解析用/-.间隔的日期
     */
    public Date toDate(String sDate){
        int jj;
        char ss, cc;
        String[] sss = {
            "-", "/", "."};
        String[] result;
        int kk, mm;
        
        GregorianCalendar cl = null;

        if(sDate == null)
        	return null;
        
        //检查分隔符
        for (jj = 0; jj < sss.length; jj++) {
            if (sDate.indexOf(sss[jj]) >= 0) {
                break;
            }
        }
        //增加8个字符的日期支持 
        ss = sss[jj > 0 ? (jj-1): 0].charAt(0);
        //检查数字有效性即除了数字和分隔符，不应该再包括其它字符
        for (int i = 0; i < sDate.length(); i++) {
            cc = sDate.charAt(i);
            if (cc != ss && (cc < '0' || cc > '9')) {
                return null;
            }
        }
        
        if(jj > (sss.length -1) && sDate.trim().length() != 8)
        	return null;
        
        if (jj >= sss.length) {
        	result = new String []{sDate.substring(0, 4) ,sDate.substring(4, 6) , sDate.substring(6, 8)};
        }else{        
	        //劈开，获取3个数字
	        result = sDate.split(sss[jj], -1); //检查全部，包括空的元素，用0会忽略空
        }
        if (result.length != 3) {
            return null;
        }
        jj = Integer.parseInt(result[0]);
        kk = Integer.parseInt(result[1]);
        mm = Integer.parseInt(result[2]);
        //---end---
        
        //判断是否符合一种日期格式
        //1、y/M/d格式
        if (this.isValidDate(jj, kk, mm)) {
            cl = new GregorianCalendar(jj < 30 ? jj + 2000 :
                                       (jj <= 99 ? jj + 1900 : jj), kk - 1, mm);
        } else {
            if (mm < 30) {
                mm += 2000;
            } else if (mm <= 99) {
                mm += 1900;
                //2、M/d/y格式
            }
            if (this.isValidDate(mm, jj, kk)) {
                cl = new GregorianCalendar(mm, jj - 1, kk);
                //3、d/M/y格式
            } else if (this.isValidDate(mm, kk, jj)) {
                cl = new GregorianCalendar(mm, kk - 1, jj);
            } else {
                return null;
            }
        }
        return cl.getTime();
    }
    
    /**
     * add by huangqirong 2012-12-19 story #2327
     * 判断年月日是否在正常范围
     * 年份正常范围在0-99和1000－9999
     */
    private boolean isValidDate(int year, int month, int day) {
        GregorianCalendar cl;

        if (year < 0 || (year > 99 && (year < 1000 || year > 9999))) {
            return false;
        }
        if (year < 30) {
            year += 2000;
        } else if (year <= 99) {
            year += 1900;

        }
        if (month < 1 || month > 12) {
            return false;
        }

        cl = new GregorianCalendar(year, month - 1, 1); //参数月份从0开始所以减一
        if (day < cl.getActualMinimum(Calendar.DAY_OF_MONTH) ||
            day > cl.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            return false;
        }

        return true;
    }
    
    /**
     * add by huangqirong 2012-12-19 story #2327
     * 字符串to日期时间
     * 注意要指定日期格式，不像vb可以自动识别
     */
    public Date parseDate(String sDate, String format){
        GregorianCalendar cl = new GregorianCalendar();        
        int year;

        try {
            cl.setTime( (new SimpleDateFormat(format)).parse(sDate));
            year = cl.get(Calendar.YEAR);
            //年份要控制一下
            if (year < 1000 || year > 9999) {
                return null;
            }

            return cl.getTime();
        } catch (ParseException pe) {        	
        	System.out.println(pe.getStackTrace());
            return null;
        }
    }
    
    /**
     * add by huangqirong 2012-12-19 story #2327
     * @param dDate Date
     * @param format String：日期格式
     * @return String：返回格式化好的日期字符串
     */
    public String formatDate(Date dDate, String format) {
        return (new SimpleDateFormat(format)).format(dDate);
    }
}

