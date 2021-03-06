/**   
* @Title: SubjectPL.java 
* @Package com.yss.webServices.AccountClinkage.services 
* @Description: TODO( ) 
* @author KR
* @date 2013-5-10 下午05:22:00 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;

import com.yss.util.YssFun;
import com.yss.util.YssUtil;
import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.client.AccountClinkageService_Service;

/** 
 * @ClassName: SubjectPL 
 * @Description: TODO(  ) 
 * @date 2013-5-15 下午05:22:00 
 *  add by dongqingsong 2013-05-18 story #3871-2 需求北京-[建设银行]QDII系统[高]20130419001
 *  科目性质发生额数据接口(批量)
 */
public class SubjectPL extends AbsService implements Runnable {

	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public SubjectPL() {
	}
	
	@Override
	public void doResLinkage() {
	}
	
	@Override	
	public void doResOnLine() {
	}
	
	@Override
	public void doResBatch() {
		SubjectPL subjectPl = new SubjectPL();
		subjectPl.setPub(this.getPub());
		subjectPl.setConnection(this.getConnection());
		subjectPl.setRequestMsgXml(this.getRequestMsgXml());
		new Thread(subjectPl).start();//调用线程 进行异步
		Document doc = Console.createXml(null, "100", "100", "1.0", "", this.getTxcode(), false);
		Element body = doc.getRootElement().addElement("body");
		body.addElement("status").setText("0");//状态为成功
		//body.addElement("remark").setText("接收成功");
		this.setResponesMsgXml(doc);
	}
	
	@Override
	public void doReqLinkage() {
	}
	
	@Override
	public void doReqOnLine() {
	}
	
	@Override
	public void doReqBatch() {
		
	}
	
	@Override
	public void setDataType() {
		this.dataType=2; //批量
	}
	
	@Override
	public void setOperType() {
		this.operType=1; //响应
	}
	
	/**
	 * 获取资产代码 套帐（000）
	 *  套帐  == 资产代码
	 * */
	private Hashtable <String, String> getAllAssetSets(String year){
		Hashtable <String, String> htAssetSets = new Hashtable<String, String>();
		ResultSet rs = null;
		String sql = "select FSetID ,FSetCode from lsetlist where Fyear = " + this.getPub().getDbLink().sqlString(year);
		try {
			rs = this.getResult(sql, ""); ;
			while(rs.next()){
				htAssetSets.put(YssFun.formatNumber(rs.getInt("FSetCode"), "000"), rs.getString("FSetID"));	
			}
		} catch (Exception e) {
			this.setReplyCode("1");
			this.setReplyRemark(this.getReplyRemark() + "获取资产代码 套帐出错：" + e.getMessage() + "\n");
			System.out.println("获取资产代码 套帐出错：" + e.getMessage() + "\n");
		}finally{
			this.getPub().getDbLink().closeResultSetFinal(rs);
		}
		return htAssetSets;
	}
	
	/**
	 * 资产代码 = 组合群代码
	 */
	private Hashtable<String, String> getAssetGroup(){
		Hashtable <String, String> htAssetGroup = new Hashtable<String, String>();
		ResultSet rs = null;		
		PreparedStatement pstp = null;
		PreparedStatement portPstp = null;
		try {
			pstp = this.getConnection().prepareStatement("select * from Tb_Sys_Assetgroup where FSysCheck = 1 and FLocked = 0" +
					" order by FASSETGROUPCODE");
			rs = pstp.executeQuery();
			while(rs.next()){
				String preTab = rs.getString("FTABPREFIX");
				
				if(!this.getPub().getDbLink().yssTableExist(("tb_" + preTab + "_para_portfolio").toUpperCase()))
					continue;
				
				portPstp = this.getConnection().prepareStatement("select * from tb_" + preTab + "_para_portfolio " +
						" where FCHECKSTATE = 1 and FEnabled = 1");
				ResultSet rs1 = portPstp.executeQuery();
				while(rs1.next()){
					htAssetGroup.put(rs1.getString("FASSETCode"), preTab);
				}
				rs1.close();
				portPstp.close();
			}
			
		}catch (Exception e) {
			this.setDoSign("1") ;
			this.setReplyRemark(this.getReplyRemark() + "处理数据时查询资产代码及组合群出错：" + e.getMessage() + "\n");
			System.out.println("处理数据时查询资产代码及组合群出错：" + e.getMessage());
		}finally{
			this.getPub().getDbLink().closeResultSetFinal(rs);
			if(pstp != null){
				try {
					pstp.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			
		}
		return htAssetGroup;
	}
	
	/**
	 * 设置费用类型 = 费用类型简称
	 * */
	private Hashtable<String, String> setFeeType(){
		Hashtable<String, String> feeType= new Hashtable<String, String>();
		feeType.put("应付管理人报酬_管理费", "GLF");
		feeType.put("应付托管费", "TGF");
		feeType.put("应付销售费", "XSFWF");
		feeType.put("其他应付款", "ZSSYF");
		return feeType;
	}
	
	/**
	 * 获得所有套账的查询信息
	 */
	public void getAllSet(){
		Hashtable<String , String> htAssetSets =null; //套帐  == 资产代码
		StringBuffer content = new StringBuffer();
		String sql = null;
		ResultSet rs = null;
		String year = ""; //财务表中的年份
		
		if(this.getRequestMsgXml() != null){
			
			if("1111".equalsIgnoreCase(this.getRequestMsgXml().getRootElement().element("head").elementText("replycode")))
				return;
			
    		Element body = this.getRequestMsgXml().getRootElement().element("body"); 	//获取body标签
    		if(body != null){    		
    			List<Element> records = body.elements("ENTITY");
    			Hashtable<String , String> htFeeType = this.setFeeType(); //获取费用类型简称设置
    			Hashtable<String , String> htAssetGroup = this.getAssetGroup(); //资产代码 = 组合群代码
    			for (int i = 0; i < records.size(); i++) { //遍历费用类型
    				htAssetSets = null;
    				Element record = records.get(i);    				
    				String startDate = record.elementText("startDate");
    				year = startDate.substring(0, 4);
    				htAssetSets = this.getAllAssetSets(year); //套帐  == 资产代码    				
        			String endDate = record.elementText("endDate");
        			String accountAttrs = record.elementText("accountAttrs");
        			String fileType = "";
					if ("GLF".equalsIgnoreCase(htFeeType
							.get(accountAttrs)))
						fileType = "5";
					else if ("TGF".equalsIgnoreCase(htFeeType
							.get(accountAttrs)))
						fileType = "12";
					else if ("XSFWF".equalsIgnoreCase(htFeeType
							.get(accountAttrs)))
						fileType = "14";
					else if ("ZSSYF".equalsIgnoreCase(htFeeType
							.get(accountAttrs)))
						fileType = "20";
        			
        			
        			
        			if(htAssetSets != null){
        				Enumeration<String> en = htAssetSets.keys();
        				while(en.hasMoreElements()){  //遍历各套帐
        					
        					String setCode = en.nextElement(); //套帐
        					String assetCode = htAssetSets.get(setCode); //资产代码
        					sql = this.getFWVsql(year, setCode, accountAttrs, startDate, endDate); //获取凭证数据sql
        					try {
        						
        						if(!this.getPub().getDbLink().yssTableExist("a" + year + setCode + "fcwvch"))
        							continue;
        						
        						rs = this.getPub().getDbLink().openResultSet(sql);
            					while(rs.next()){ //获取单套帐数据
            						if(htAssetGroup.get(assetCode) == null)
            							continue;
            						
            						String facctattr = rs.getString("facctattr")==null?"" : rs.getString("facctattr");
            						
            						String CuryCode = rs.getString("fcyid") == null ? "" : rs.getString("fcyid");//币种 需要加词汇转换
            						
            						Hashtable<String, String> htCuryToNums = this.getMarket(htAssetGroup.get(assetCode) ,
            								"AC_DicCuryToNum","获取币种转编码出错：");
            						String tmp = "";
            						if(htCuryToNums != null)
            						    tmp = htCuryToNums.get(CuryCode);
            						if(tmp != null && tmp.trim().length() > 0)
            							CuryCode = tmp ;
            						else
            							CuryCode = "156";
            						
            						String acctCode = rs.getString("facctcode") == null ? "" : rs.getString("facctcode");
            						String facctname = rs.getString("facctname") == null ? "" : rs.getString("facctname");
            						String fJMoney = rs.getString("fJMoney") == null ? "" : rs.getString("fJMoney");
            						String fDMoney = rs.getString("fDMoney") == null ? "" : rs.getString("fDMoney");
            						
            						content.append(assetCode).append("\t"); //资产代码
            						content.append(startDate).append("\t"); //开始日期
            						content.append(endDate).append("\t");  //结束日期
            						content.append(CuryCode).append("\t"); //币种
            						content.append(facctattr).append("\t");  //科目性质
            						content.append(acctCode).append("\t"); //科目代码
            						content.append(facctname).append("\t");  //科目名称
            						content.append(fJMoney).append("\t"); //借方发生额
            						content.append(fDMoney).append("\t"); //贷方发生额
            						content.append("\r\n"); //换行
            					}
							} catch (Exception e) {
								this.setDoSign("1") ;
								this.setReplyRemark(this.getReplyRemark() + "科目性质发生额数据接口(批量)出错：" + e.getMessage() + "\n");
								System.out.println("科目性质发生额数据接口(批量)出错：" + e.getMessage());
							}finally{
								this.getPub().getDbLink().closeResultSetFinal(rs);
							}
        				}
        			}
        			/**此处写TXT文件*/
        			String txtName = this.CreateTxtname(htFeeType.get(accountAttrs)); //构建TXT文件名
        			
        			//在acdatapath.properties中配置路径
        			String txtPath = this.getPropertiesPath("ncbs.subjectpl.txtpath");
        			
        			try {
        				if(txtPath != null && txtPath.trim().length() > 0){
        					txtPath = this.getEndsWithFileSeparator(txtPath);
        				    YssUtil.writeTxt(txtPath , txtName, content.toString() ,"UTF-8");
        				}
        			} catch (Exception e1) {
        				txtPath = ""; //生成批量文件的过程中出错而没有生成文件，则该接口的“filePath”字段填""
        				this.setDoSign("1");
        				this.setReplyRemark(this.getReplyRemark() + "写入txt文件异常" + e1.getMessage() + "\n");
        				System.out.println("写入txt文件异常" + e1.getMessage());
        			}
        			//ftp路径
    				String ftpPath = this.getPropertiesPath("ncbsftp.subjectpl.txtpath");
					
        			try {
						if (ftpPath != null && ftpPath.trim().length() > 0) {
							ftpPath = this.getEndsWithFileSeparator(ftpPath);
							ftpPath = this.getEndsWithFileSeparator(ftpPath + new SimpleDateFormat("yyyyMMdd").format(new Date()));

							if (txtPath != null && txtPath.trim().length() > 0)
								this.ftpUpLoad(ftpPath, txtPath, txtName); // 上传至ftp	
						}
        			} catch (Exception e) {
        				ftpPath = "";
        				this.setDoSign("1") ;
						this.setReplyRemark(this.getReplyRemark() + "科目性质发生额数据接口(批量)上传FTP出错：" + e.getMessage() + "\n");
						System.out.println("科目性质发生额数据接口(批量)上传FTP出错：" + e.getMessage());
        			}finally{
        			    content.setLength(0); //清空内容
        			}
        			this.requestBatch(ftpPath, txtName, fileType); // 请求批量文件接收
				}
    		}
		}
	}
	
	/**
	 * 获取凭证数据sql
	 * */
	private String getFWVsql(String year , String assetCode , String accountAttrs , String startDate , String endDate){
		String sql = " select fwv.*, alc.facctname as facctname from (select fw.fkmh as facctcode, " +
		  " al.facctattr as facctattr,fcyid, sum(decode(fjd, 'J', fw.fbbal, 0)) as FJmoney," +
		  " sum(decode(fjd, 'D', fw.fbbal, 0)) as FDmoney " +
		  " from a" + year + assetCode + "fcwvch fw left join  " +
		  " a" +  year + assetCode + "laccount" +
		  " al on fw.fkmh = al.facctcode where al.facctattr = " +
		  this.getPub().getDbLink().sqlString(accountAttrs) +  " and fw.fdate between " +
		  this.getPub().getDbLink().sqlDate(startDate) + " and " + 
		  this.getPub().getDbLink().sqlDate(endDate) + 
          " group by al.facctattr, fcyid, fw.fkmh " +
          " order by fkmh, fcyid) fwv  left join " +
          " a" +  year + assetCode + "laccount alc on fwv.facctcode = alc.facctcode";
		return sql;
	}
	
	/**
	 * 请求批量文件接收
	 * */
	private void requestBatch(String txtPath , String fileName ,String fileType){
		Document doc = Console.createXml(null, "100", "100", "1.0", "", "AL044IT15", true);
		//if("0".equalsIgnoreCase(this.getDoSign())){ //正常数据则返回body标签
			Element body1 = doc.getRootElement().addElement("body");
			//Element record = body1.addElement("record");
			//record.addElement("accountsDate").setText(workDate);
			body1.addElement("filename").setText(fileName);
			body1.addElement("filepath").setText(txtPath);
			body1.addElement("filetype").setText(fileType);
		//}
		AccountClinkageService_Service service = new AccountClinkageService_Service();
		service.getAccountClinkageServicePort().doDeal(doc.asXML());
	}
	
	/**
	 * 构建TXT文件名
	 * */
	private String CreateTxtname(String accountAttrs){
		String date = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String txtName ="QD_" + accountAttrs + "_" + date + ".txt";
		return txtName;
	}
    
	/**
	 * 实现异步线程
	 * */
	public void run() {
		Document doc = null;
		try {
			doc = this.getRequestMsgXml();
			this.setRequestMsgXml(doc);
			this.getAllSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
