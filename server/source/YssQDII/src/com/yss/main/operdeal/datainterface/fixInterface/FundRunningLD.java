/**   
* @Title: FundLD.java 
* @Package com.yss.main.operdeal.datainterface 
* @Description: TODO( ) 
* @author KR
* @date 2013-5-17 下午03:59:00 
* @version V4.0
*/
package com.yss.main.operdeal.datainterface.fixInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.client.AccountClinkageService;
import com.yss.webServices.AccountClinkage.client.AccountClinkageService_Service;

/** 
 * @ClassName: FundLD 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-17 下午03:59:00 
 *  add by huangqirong 2013-05-17 story #3871	 关于QDII清核联动需求
 *  需求北京-[建设银行]QDII系统[高]20130419001
 */
public class FundRunningLD extends DataBase {
	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public FundRunningLD() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.operdeal.datainterface.pretfun.DataBase#inertData()
	 */
	@Override
	public void inertData() throws YssException {
		
		String sql = "";
		Element record = null;
		if(!this.dbl.yssTableExist("Temp_CashData")){
			/**
			 * 次临时表用于存储   资金流水数据 且
			 * */
			sql = "create table Temp_CashData(" +
			       "FproductCode VARCHAR2(20) not null ," +
			       "FPortCode VARCHAR2(20) not null ," +
			       "FAmt NUMBER(35,4) default 0 ," +
			       "FBussDate DATE not null ," +
			       "FCurCode varchar2(20) ," +
			       "FMark varchar2(200)," +
			       "FBussTypeId varchar2(20) ," +
			       "FpayAccountNo varchar2(20)," +
			       "FRecAccountNo varchar2(20)" +
			     //",FCreator Varchar2(20)" +
			       ")";
			try {
				this.dbl.executeSql(sql);
			} catch (Exception e) {
				System.out.println("创建临时表Temp_CashData出错：" + e.getMessage());
			}
		}
		
		try {
			dbl.executeSql("delete from Temp_CashData");
		} catch (Exception e) {
			System.out.println("删除临时表Temp_CashData数据时出错：" + e.getMessage());
		}
		
//		Document doc = Console.createXml(null, "100", "100", "4.0", "", "AL044IT10", true);
//		if(doc != null){
//			doc.getRootElement().addElement("body");
//			/**
//			 * 触发本地WebService客户端
//			 * */
			AccountClinkageService_Service service = new AccountClinkageService_Service();			
//			String result = service.getAccountClinkageServicePort().doDeal(doc.asXML());	// 调用划款类型查询
//			
//			Document document = Console.parseXml(result); //转换成Xml文件
//			Element replycode = document.getRootElement().element("head").element("replycode");
//			if(replycode != null && "0000".equalsIgnoreCase(replycode.getText())){ //判断是否有replycode 标签
//				Element body = document.getRootElement().element("body"); 	//获取body标签
//				if(body != null){
//	    			List<Element> records = body.elements("busstype_group");
//	    			if(records.size() > 0){
//	    				
//	    				for (int i = 0; i < records.size(); i++) {
//	    					record = records.get(i);
//	    					String bussTypeName = record.elementText("busstypename"); //划款类型名称
//	    					String bussTypeCode = record.elementText("busstypecode"); //划款类型代码
//	    					String flag = record.elementText("flag"); 				//出入款标志
//	    					String paraBussTypeID = record.elementText("parabusstypecd");//划款类型代码
	    					
	    					PortfolioBean portfolio = new PortfolioBean();
	    					portfolio.setYssPub(this.pub);
	    					portfolio.setPortCode(this.sPort);
	    					String assetCode = portfolio.getOperValue("getFundCode");
	    					if(assetCode == null || assetCode.trim().length() == 0)
	    						return;
	    					
	    					
	    					
	    					Document doc = DocumentHelper.createDocument();
	    					doc = Console.createXml(null, "100", "100", "4.0", "", "AL044IT12", true);
	    					if(doc != null){
	    						Element body2 = doc.getRootElement().addElement("body");
	    						body2.addElement("productcode").setText(assetCode); //资产代码
	    						body2.addElement("busstypeid").setText(""); //划款类型编号
	    						body2.addElement("startdate").setText(YssFun.formatDate(this.sDate, "yyyy-MM-dd"));	//开始日期
	    						body2.addElement("enddate").setText(YssFun.formatDate(this.endDate, "yyyy-MM-dd"));	//结束日期
	    						
	    						/**
	    						 * 触发本地WebService客户端
	    						 * 调用 资金流水查询
	    						 * */	    												
	    						String datas = service.getAccountClinkageServicePort().doDeal(doc.asXML());
	    						this.insertTempData(datas);
	    					}
	    					//if(bussTypeCode == null || bussTypeCode.trim().length() == 0)
	    					//	continue;
	    				//}
	    				this.generateTransfer(); //生成资金调拨 
	    			//}
				//}
			//}
		//}
	}
	
	/**
	 * 数据添加进入临时表
	 * */
	private void insertTempData(String datas){
		Document doc = Console.parseXml(datas);
		Element record = null;
		PreparedStatement pstmt = null;
		Connection conn = dbl.getConnection();
    	String sql = "insert into Temp_CashData " +
    			"(FproductCode,FPortCode,FAmt,FBussDate,FCurCode,FMark,FBussTypeId,FpayAccountNo,FRecAccountNo) " +
    			" values (?,?,?,?,?,?,?,?,?)";
    	
    	Hashtable<String, String> subTranTypes = Console.getMarket(this.pub ,
				                                       "AC_DicSubTranType" ,"获取调拨类型转换出错：");//调拨类型
    	    	
    	if(doc != null){
    		Element body = doc.getRootElement().element("body"); 	//获取body标签
    		if(body != null){
    			if("1111".equalsIgnoreCase(body.elementText("replycode")))//有异常则不处理
    				return;
    			List<Element> records = body.elements("record_group");
    			if(records.size() > 0){
    				try {
    					String productCode = body.elementText("productcode");	//资产代码
    					pstmt = conn.prepareStatement(sql);
    					
	    				for (int i = 0; i < records.size(); i++) {
	    					record = records.get(i);
	    					
	    					String amt = record.elementText("amt");	//划款金额
	    					String bussDate = record.elementText("bussdate");	//划款日期
	    					String curCode = record.elementText("curcode");	//币种代码
	    					String rmark = record.elementText("rmark");	//摘要信息
	    					String bussTypeID = record.elementText("busstypeid");	//划款类型编码
	    					String payAccountNo = record.elementText("payaccountno");	//付款方账号
	    					String recAccountNo = record.elementText("recaccountno");	//收款方账号
	    					
	    					String subTsftype = "";
	    					if(subTranTypes != null)
	    						subTsftype = subTranTypes.get(bussTypeID);
	    			        if(subTsftype == null || subTsftype.trim().length() ==0 )
	    			        	subTsftype =bussTypeID;
	    			        
	    			        sql = "select FTsftypecode from tb_base_subtransfertype " +
	    			        		" where Fsubtsftypecode = " + this.pub.getDbLink().sqlString(subTsftype) +
	    			        		" and Fcheckstate = 1";
	    			        
	    			        String tranType = Console.getDatabySql(this.pub , sql, "FTsftypecode");
	    					
							String temp = " select WMSYS.WM_CONCAT(cash1.FNum) as FNum from ( select * from tb_"
									+ this.pub.getAssetGroupCode()
									+ "_cash_transfer "
									+ " where FTsFtypeCode = "
									+ this.pub
											.getDbLink()
											.sqlString(
													(tranType == null || tranType
															.trim().length() == 0) ? " "
															: tranType)
									+ " and FSubTsfTypeCode = "
									+ this.pub.getDbLink()
											.sqlString(subTsftype)
									+ "and FTransferDate = "
									+ this.pub.getDbLink().sqlDate(bussDate)
									+ " ) cash1 "
									+ " join (select * from tb_"
									+ this.pub.getAssetGroupCode()
									+ "_cash_subtransfer "
									+ " where FPortCode = "
									+ this.pub.getDbLink().sqlString(this.sPort)
									+ " and Fdesc = 'QHLD' ) cash2 on cash1.FNum = cash2.FNum ";
							String nums = Console.getDatabySql(this.pub, temp,
									"FNum");
				
							nums = operSql.sqlCodes(nums);

							// 删除资金调拨重复的数据
							dbl.executeSql(" delete from tb_"
									+ this.pub.getAssetGroupCode()
									+ "_cash_subtransfer where FNum in(" + nums
									+ ") ");

							dbl.executeSql(" delete from tb_"
									+ this.pub.getAssetGroupCode()
									+ "_cash_transfer where FNum in(" + nums
									+ ") ");
	    					
	    					pstmt.setString(1, productCode);
	    					pstmt.setString(2, this.sPort);
	    					pstmt.setDouble(3, Double.parseDouble(amt));
	    					pstmt.setDate(4, YssFun.toSqlDate(bussDate));
	    					pstmt.setString(5 , curCode);
	    					pstmt.setString(6 , rmark);
	    					pstmt.setString(7, bussTypeID);
	    					pstmt.setString(8, payAccountNo);
	    					pstmt.setString(9, recAccountNo);
	    					pstmt.addBatch();
	    				}
	    				pstmt.executeBatch();
    				} catch (Exception e) {
    					System.out.println("插入数据表：Temp_CashData是出错:" + e.getMessage());
    				}
    			}
    		}
    	}		
	}
	
	/**
	 * 生成资金调拨    	
	 * */
	private void generateTransfer(){
		
		String sql = "select FproductCode,FPortCode,FAmt,FBussDate,FCurCode,FMark,FBussTypeId,FpayAccountNo," +
				" FRecAccountNo" +
				" from Temp_CashData " ;
		
		TransferBean tran = null;
        TransferSetBean transfersetIn = null;
        TransferSetBean transfersetOut = null;
        ArrayList tranSetList = null;
        CashTransAdmin tranAdmin = null;
        
        BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.pub);
		
		Connection conn = dbl.getConnection();
		ResultSet rs =  null;
		boolean bTrans = false;
		
		Hashtable<String, String> subTranTypes = Console.getMarket(this.pub,
		                                          "AC_DicSubTranType" ,"获取调拨类型转换出错：");//转换调拨类型
		
		try {
			rs = this.dbl.openResultSet(sql);
	        conn.setAutoCommit(false);
	       
			while(rs.next()){
				tran = new TransferBean();
				transfersetIn = new TransferSetBean();
				transfersetOut = new TransferSetBean();
				tranSetList = new ArrayList();
				tranAdmin = new CashTransAdmin();
				tranAdmin.setYssPub(this.pub);
				
				String subTsftype = "";
				if(subTranTypes != null)
					subTsftype = subTranTypes.get(rs.getString("FBussTypeId"));
		        if(subTsftype == null || subTsftype.trim().length() ==0 )
		        	subTsftype = rs.getString("FBussTypeId");
		        
		        sql = "select FTsftypecode from tb_base_subtransfertype " +
		        		" where Fsubtsftypecode = " + this.pub.getDbLink().sqlString(subTsftype) +
		        		" and Fcheckstate = 1";
		        
		        String tranType = Console.getDatabySql(this.pub , sql, "FTsftypecode");
		        
		        tran.setYssPub(this.pub);
		        tran.setDtTransDate(new Date(rs.getDate("FBussDate").getTime())); 		//业务日期
		        tran.setDtTransferDate(new Date(rs.getDate("FBussDate").getTime()));//调拨日期		        
		        
		        tran.setStrTsfTypeCode( ( tranType == null || tranType.trim().length() == 0) ? " " : tranType);
		        tran.setStrSubTsfTypeCode(subTsftype);
		        tran.setStrTransferTime("00:00:00");
		        tran.setDataSource(1);  //这里应为自动标记 
		        tran.setRateTradeNum("");	            
		        tran.checkStateId = 1;
		        tran.creatorTime = YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss");
		        
		        tran.setStrDesc("QHLD");
		        
		        //FQSAccNum , FBankAccount
		    	String sqlTemp = "select FCashAcccode from tb_" + this.pub.getAssetGroupCode() + "_para_cashaccount" +
		    					 " where FQSAccNum = " + this.pub.getDbLink().sqlString(rs.getString("FRecAccountNo")) +
		    					 " and fcheckstate = 1";
		    	
		    	sqlTemp = Console.getDatabySql(this.pub, sqlTemp, "FCashAcccode"); //流入账户
		        
		    	if(sqlTemp != null && sqlTemp.trim().length() > 0){
		    	
					// 流入
					transfersetIn.setDMoney(rs.getDouble("FAmt")); // 金额
					transfersetIn.setSPortCode(rs.getString("FPortCode")); // 组合
					transfersetIn.setSAnalysisCode1(" ");
					transfersetIn.setSAnalysisCode2(" ");
					// 用户需要对组合按资本类别进行子组合的分类
					transfersetIn.setStrAttrClsCode(" ");

					transfersetIn.setDBaseRate(1);
					transfersetIn.setDPortRate(1);

					transfersetIn.setSCashAccCode((sqlTemp == null || sqlTemp
							.trim().length() == 0) ? " " : sqlTemp); // 账户
																		// 根据字段判断流入或流出
					transfersetIn.setIInOut(1); // 流入
					transfersetIn.checkStateId = 1;
					transfersetIn.setSDesc("QHLD"); // 清核联动标志

					tranSetList.add(transfersetIn);
		        
		    	}
		    	
		    	//FQSAccNum , FBankAccount
		        sqlTemp = "select FCashAcccode from tb_" + this.pub.getAssetGroupCode() + "_para_cashaccount" +
				 " where FQSAccNum = " + this.pub.getDbLink().sqlString(rs.getString("FpayAccountNo")) +
				 " and fcheckstate = 1";

		        sqlTemp = Console.getDatabySql(this.pub, sqlTemp, "FCashAcccode"); //流入账户
			    
		        if(sqlTemp != null && sqlTemp.trim().length() > 0){
		        
					// 资金流出帐户
					transfersetOut.setDMoney(rs.getDouble("FAmt"));
					transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合
					transfersetOut.setSAnalysisCode1(" ");
					transfersetOut.setSAnalysisCode2(" ");
					transfersetOut.setStrAttrClsCode(" ");

					transfersetOut.setSCashAccCode((sqlTemp == null || sqlTemp
							.trim().length() == 0) ? " " : sqlTemp); // 账户
																		// 根据字段判断流入或流出
					transfersetOut.setIInOut(-1); // 流出
					transfersetOut.checkStateId = 1;

					transfersetOut.setDBaseRate(1);
					transfersetOut.setDPortRate(1);
					transfersetOut.setSDesc("QHLD"); // 清核联动标志

					tranSetList.add(transfersetOut);
		        }
		        
				if (tranSetList != null && tranSetList.size() > 0) {
					tranAdmin.addList(tran, tranSetList);
					tranAdmin.insert();
				}
			}
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		} catch (Exception e) {
			System.out.println("产生资金调拨数据出错：" + e.getMessage());
		}finally{
			this.dbl.endTransFinal(conn, bTrans);
		}
				
		
	}
	
		
}
