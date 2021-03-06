package com.yss.webServices.AccountClinkage.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.client.AccountClinkageService_Service;
import com.yss.webServices.AccountClinkage.ncbs.client.NCBSWebService;

public class ExchangeDate extends AbsService {

	@Override
	public void doReqBatch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doReqLinkage() {
		// TODO Auto-generated method stub
		/**
		 * 调用NCBS服务端
		 * */
		NCBSWebService service = new NCBSWebService();
		String asXml = service.getNCBSPort().excuteNCBSWebService(this.getTxcode(), this.getRequestMsgXml().asXML());		
		this.setResponesMsgXml(Console.parseXml(asXml));
	}

	@Override
	public void doReqOnLine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doResBatch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doResLinkage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doResOnLine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataType() {
		this.dataType = 0 ; //联动
	}

	@Override
	public void setOperType() {
		// TODO Auto-generated method stub
		this.operType = 0 ; //请求
	}

	/**
	 * 构建请求的头文件
	 * @return
	 */
	private Document getReqHead(){
		Document doc= DocumentHelper.createDocument();
		Element root = doc.addElement("request");
		
		Element head= root.addElement("head");
		Element  channel = head.addElement("channel");
		Element branchid = head.addElement("branchid");
		Element version = head.addElement("version");
		Element msgno = head.addElement("msgno");
		Element txcode = head.addElement("txcode");
		root.addElement("body");
		  
		channel.setText("100");
		branchid.setText("100");
		version.setText("1.0");
		msgno.setText("");
		txcode.setText("AL044IT14");
		return doc;
	}
	
	public void getReqRecord(String portCode , String tradeDate , String PrefixTb ){
		
 		String sql= "select be.fexchangecode as fexchangecode , sum(decode(dst.ftradetypecode, '01'," +
		            " dst.ftrademoney * (-1),'02', dst.ftrademoney,0)) as trademoney, " +
		            " sum(decode(dst.ftradetypecode, '01', dst.ftotalcost * (-1), '02', dst.ftotalcost,0)) as totalcost" +
		            " from (select * from Tb_" + PrefixTb + "_Data_SubTrade where fportcode = " + 
		            this.getPub().getDbLink().sqlString(portCode) + " and fbargaindate = " + 
		            this.getPub().getDbLink().sqlDate(tradeDate) + " and  fcheckstate =1) dst " +
		            " left join  (select * from Tb_" + PrefixTb + "_Para_Security  where fcheckstate =1 ) ps " +
		            " on dst.fsecuritycode = ps.fsecuritycode left join ( select * from Tb_Base_Exchange " +
		            " where fcheckstate =1 ) be on ps.Fexchangecode = be.fexchangecode " +
		            " group by be.fexchangecode order by be.fexchangecode"; 
		ResultSet rs = this.getResult(sql, "查询场内交易数据统计结果出错");
		try {
			while(rs.next()){
				Document doc =null;
				doc = this.getReqHead();
				String exchangeName = rs.getString("fexchangecode");
				//转换业务类型
				Hashtable<String, String> markets = this.getMarket( PrefixTb, "AC_DicMarket2" ,
						                                                     "获取交易市场转换出错");
				String tmp = "";
				if(markets != null)
					tmp = markets.get(exchangeName);
				
				if(tmp != null && tmp.trim().length() > 0){
					exchangeName = tmp ;
				}else{
					//throw new YssException(rs.getString("fexchangecode") + "无对应交易市场！");
					continue;
				}
				String trademoney = "" + rs.getDouble("trademoney");
				String totalcost = "" + rs.getDouble("totalcost");
				this.getReqRecord(doc, portCode, tradeDate, exchangeName, trademoney, totalcost);
				AccountClinkageService_Service service = new AccountClinkageService_Service();			
			    String result = service.getAccountClinkageServicePort().doDeal(doc.asXML());	//调用场内交易数据接口
			    if(result != null && result.trim().length() > 0)
			        this.setResponesMsgXml(Console.parseXml(result)); //设置输出内容
			}
		} catch (SQLException e) {
			this.setDoSign("1");
			//this.setReplyRemark(this.getReplyRemark() + "查询的sql结果集报错" + e.getMessage() + "\n");
			System.out.println("查询的sql结果集报错" + e.getMessage());
		}finally{
			this.getPub().getDbLink().closeResultSetFinal(rs);			
		}
	}
	
	/**
	 * 创建一条记录的值
	 * @param doc
	 * @param port_Code
	 * @param trade_Date
	 * @param exchange_Name
	 * @param trade_Money
	 * @param total_Money
	 * @return
	 */
	private Document getReqRecord(Document doc,String port_Code,String trade_Date,String exchange_Name,
			String trade_Money,String total_Money){
		Element body = doc.getRootElement().element("body");
		body.addElement("productcode").setText(port_Code);
		body.addElement("accountsdate").setText(trade_Date);
		body.addElement("trademarket").setText(exchange_Name);
		body.addElement("dealamount").setText(trade_Money);
		body.addElement("realdealamount").setText(total_Money);
		return doc;
	}	
}
