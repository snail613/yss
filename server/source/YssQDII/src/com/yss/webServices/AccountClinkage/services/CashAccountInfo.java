package com.yss.webServices.AccountClinkage.services;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.yss.util.YssException;
import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.ncbs.client.NCBSWebService;

/**
 * dongqingsong 2013-05-12 Story 3871 YSS（上海） 建行清算联动需求  （8）
 */

public class CashAccountInfo extends AbsService {
	
	
	//数据类型 0 = 联动 ；1 = 联机 ； 2 = 批量
	public void setDataType() {
		this.dataType = 0; //联动
	}	
	
	//作为是请求或应答 标识 0 = request ； 1 = respones
	public void setOperType() {
		this.operType = 0; //请求
	}
	
	/**
	 * add dongqongsong 2013-5-14 Story 3871 建行清算联动
	 * 获取返回的xml文件，转化为String,解析xml文件
	 * @return
	 * @throws YssException 
	 * @throws DocumentException 
	 * @throws YssException 
	 * @throws SQLException 
	 * @throws com.yss.core.util.YssException 
	 * @throws DocumentException 
	 */
	public void getRespRecord(String assertCode) throws SQLException{
		List<Element> bizData = null;
		Document document=null;
		try {
			if(this.getResponesMsgXml() == null)
				return ;
			document = this.getResponesMsgXml();
			Element root=document.getRootElement();
			if("1111".equalsIgnoreCase(root.element("head").elementText("replycode")))
				return;
			bizData = root.element("body").elements("account_group");
			this.savaRecord(bizData,assertCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add dongqongsong 2013-5-14 Story 3871 建行清算联动
	 *  保存返回的数据到数据库
	 * @param bizData
	 * @param assertCode
	 * @throws YssException
	 * @throws SQLException 
	 * @throws com.yss.core.util.YssException 
	 */
	public void savaRecord(List<Element> bizData,String assertCode) throws YssException, SQLException{
		String sql=this.InsertQs();
		PreparedStatement pst = null;
		Element data = null;
		Connection conn=null;
		try {
			if (bizData.size() != 0) {
				conn=this.getPub().getDbLink().getConnection();
				pst=this.getPub().getDbLink().getPreparedStatement(sql);
				for (int i = 0; i < bizData.size(); i++) {
					data = bizData.get(i);
					String accountNo = data.elementText("accountno");// 账户号
					String accountName = data.elementText("accountname");// 账户名称
					String accountTypeCode = data.elementText("accounttypecode");//账户类型编号
					String accountTypeName = data.elementText("accounttypename");//账户类型名称
					
					pst.setString(1,assertCode);
					pst.setString(2,accountNo);
					pst.setString(3,accountName);
					pst.setString(4,accountTypeCode);
					pst.setString(5,accountTypeName);
					pst.addBatch();
				}
				pst.executeBatch();
				conn.commit();
				pst.close();
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * add dongqongsong 2013-5-14 Story 3871 建行清算联动
	 * @return
	 */
	public String InsertQs(){
		String sql="insert into ACQSACCOUNT (FAsetCode,FaccountNo,accountname, accounttypecode, accounttypename)" +
				" values(?,?,?,?,?)";
		return sql;
	}
	
	/**
	 * 请求
	 * */
	@Override
	public void doReqLinkage() {
		NCBSWebService service = new NCBSWebService();
		String asXml = service.getNCBSPort().excuteNCBSWebService(this.getTxcode(), this.getRequestMsgXml().asXML());		
		this.setResponesMsgXml(Console.parseXml(asXml));
	}

	public void doResLinkage() {
	}
	
	@Override
	public void doReqBatch() {
		// TODO Auto-generated method stub
		
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
	public void doResOnLine() {
		// TODO Auto-generated method stub
		
	}
}
