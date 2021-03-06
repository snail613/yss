/**   
* @Title: FixCashAcountBalace.java 
* @Package com.yss.main.operdeal.report.repfix 
* @Description: TODO( ) 
* @author KR
* @date 2013-5-22 下午03:04:56 
* @version V4.0   
*/
package com.yss.main.operdeal.report.repfix;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.yss.base.BaseAPOperValue;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.client.AccountClinkageService_Service;

/** 
 * @ClassName: FixCashAcountBalace 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-22 下午03:04:56 
 *  add by huangqirong 2013-05-22 story #3871
 *  需求北京-[建设银行]QDII系统[高]20130419001
 *  建行现有清核资金调节表核对报表
 */
public class FixCashAcountBalaceLD extends BaseAPOperValue{
	private String beginDate ="";
	
	private String endDate = "";
	
	private String portCode = "";	
	
	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public FixCashAcountBalaceLD() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see com.yss.base.BaseAPOperValue#init(java.lang.Object)
	 */
	@Override
	public void init(Object bean) throws YssException {
		String[] reqAry = ( (String) bean).split("\n");
		if(reqAry == null)
			return ;
         this.beginDate = reqAry[0].split("\r")[1];
         this.endDate = reqAry[1].split("\r")[1];
         this.portCode = reqAry[2].split("\r")[1];
	}
	
	/* (non-Javadoc)
	 * @see com.yss.base.BaseAPOperValue#invokeOperMothed()
	 */
	@Override
	public Object invokeOperMothed() throws YssException {
		
		try {
			this.createTab(); //创建临时表结构
			
			if(dbl.yssTableExist("Temp_CashAccountBalance"))
				dbl.executeSql("delete Temp_CashAccountBalance");
		} catch (Exception e) {
			System.out.println("删除临时表：Temp_CashAccountBalance数据出错：" + e.getMessage());
		}
		
		AccountClinkageService_Service service = new AccountClinkageService_Service();
		String assetCode = Console.getDatabySql(this.pub, 
				"select * from tb_" + this.pub.getAssetGroupCode() + "_para_portfolio " +
				" where FPortCode = " + this.dbl.sqlString(portCode) + 
				" and fCheckState = 1 " +
				" and FEnabled = 1",
				"FAssetCode");
		
		Document doc0 = Console.createXml(null, "100", "100", "1.", "", "AL044IT11", true);
		doc0.getRootElement().addElement("body").addElement("productcode").setText(assetCode);
		
		String cashAcountDatas = service.getAccountClinkageServicePort().doDeal(doc0.asXML()); //调用资金账户信息查询
		if(cashAcountDatas != null && cashAcountDatas.trim().length() > 0){
			Document doc1 = Console.parseXml(cashAcountDatas);
			
			if("1111".equalsIgnoreCase(doc1.getRootElement().element("head").elementText("replycode")))
				return "";
			
			List<Element> list = doc1.getRootElement().element("body").elements("account_group");
			Hashtable<String, String> htCuryToNums = Console.getMarket(this.pub,
					                                                 "AC_DicCuryToNum","获取币种转编码出错：");
			
			Date startDate = YssFun.toDate(this.beginDate);
			Date endDate = YssFun.toDate(this.endDate);
			while(YssFun.dateDiff(startDate, endDate) >= 0){
				for (int i = 0; i < list.size(); i++) {
					Document doc = Console.createXml(null, "100", "100", "1.0", "", "AL044IT13", true);
					Element body = doc.getRootElement().addElement("body");
					//Element recode = body.addElement("recode");
					Element recode0 = list.get(i);
					
					//FQSAccNum , FBankAccount
					String sqlTemp = "select FcuryCode from tb_"
							+ this.pub.getAssetGroupCode()
							+ "_para_cashaccount"
							+ " where FQSAccNum = "
							+ this.pub.getDbLink().sqlString(recode0.elementText("accountno"))
							+ " and fcheckstate = 1";
			    	
			    	sqlTemp = Console.getDatabySql(this.pub, sqlTemp, "FcuryCode"); //流入账户
			    	
			    	String tmp = htCuryToNums.get(sqlTemp);
					if(tmp != null && tmp.trim().length() > 0){
						sqlTemp = tmp ;
					}else{
						throw new YssException("账号：" + recode0.elementText("accountno") +" 无对应币种");
						//sqlTemp = "840";
					}
			    	
					body.addElement("productcode").setText(assetCode);
					body.addElement("accountno").setText(recode0.elementText("accountno")); //账号
					body.addElement("curcode").setText(sqlTemp);  //币种
					body.addElement("bussdate").setText(YssFun.formatDate(startDate ,"yyyy-MM-dd"));
					String asXml = service.getAccountClinkageServicePort().doDeal(doc.asXML()); //调用 资金账户余额查询
					
					this.inserTempDate(asXml); //插入临时表
				}
				startDate = YssFun.addDay(startDate, 1);
			}
		}
		return "";
	}
	
	/**
	 * 插入数据
	 * */
	private void inserTempDate(String asXml){
		PreparedStatement pst = null ;
		boolean bTrans = true;
		if(asXml != null && asXml.trim().length() > 0){
			Document doc2 = Console.parseXml(asXml);
			
			if("1111".equalsIgnoreCase(doc2.getRootElement().element("head").elementText("replycode")))
				return;
			
			List<Element> recodes = doc2.getRootElement().element("body").elements("balance_group");
			
			String sql = "insert into Temp_CashAccountBalance(FPortCode,FproductCode,FAccountNo,FCurCode," +
					" FBussDate,FAmt,FCashCode)" +
						 " values(?,?,?,?,?,?,?)";
			
			try {
			    pst = dbl.getPreparedStatement(sql);
				this.dbl.getConnection().setAutoCommit(false);
				
				for (int i = 0; i < recodes.size(); i++) {
					Element recode1 = recodes.get(i);
					
					//FQSAccNum , FBankAccount
					String sqlTemp = "select FCashAcccode from tb_"
							+ this.pub.getAssetGroupCode()
							+ "_para_cashaccount"
							+ " where FQSAccNum = "
							+ this.pub.getDbLink().sqlString(
									recode1.elementText("accountno"))
							+ " and fcheckstate = 1";
			    	
			    	sqlTemp = Console.getDatabySql(this.pub, sqlTemp, "FCashAcccode"); //流入账户
			    	
					pst.setString(1, this.portCode);
					pst.setString(2, recode1.elementText("productcode"));
					pst.setString(3, recode1.elementText("accountno"));
					pst.setString(4, recode1.elementText("curcode"));
					pst.setDate(5, new java.sql.Date(YssFun.toDate(recode1.elementText("bussdate")).getTime()));
					pst.setDouble(6, Double.parseDouble(recode1.elementText("amt")));
					pst.setString(7, (sqlTemp == null || sqlTemp.trim().length() == 0) ? " " : sqlTemp);
					pst.addBatch();
				}
				pst.executeBatch();
				this.dbl.getConnection().commit();
				this.dbl.getConnection().setAutoCommit(true);
				bTrans = false;
			} catch (Exception e) {
				System.out.println("插入数据到临时表：Temp_CashAccountBalance中出错：" + e.getMessage());
			}finally{
				this.dbl.endTransFinal(bTrans);
				if(pst != null){
				    try {
				    	pst.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
			}
		}
	}
	
	/**
	 * 创建表结构
	 * */
	private void createTab(){
		try {
			if(!dbl.yssTableExist("Temp_CashAccountBalance")){
				String sql = " create table Temp_CashAccountBalance(" +
						" FPortCode nvarchar2(20) ," +
						" FproductCode nvarchar2(20)," +
						" FAccountNo nvarchar2(50)," +
						" FCurCode nvarchar2(20)," +
						" FBussDate Date," +
						" FAmt number(19,4), " +
						" FCashCode varchar(20)" +
						")" ;
				try {
					dbl.executeSql(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("创建临时表Temp_CashAccountBalance出错：" + e.getMessage());
				}
			}
		} catch (YssException e) {
			// TODO Auto-generated catch block
			System.out.println("创建临时表Temp_CashAccountBalance出错：" + e.getMessage());
		}
	}	
}
