package com.yss.webServices.AccountClinkage.services;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;

import com.yss.util.YssUtil;
import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.client.AccountClinkageService_Service;
/**
 * 	@author dongqongsong 20013-05-15 story 3871 建行清算联动  （4）
 *	作为是请求或应答 标识 0 = request ； 1 = respones
 *	数据类型 0 = 联动 ；1 = 联机 ； 2 = 批量

 */

public class TaDataInterfaceBatch extends AbsService implements Runnable{
	
	@Override
	public void setDataType() {
		this.dataType = 2; //批量
	}

	@Override
	public void setOperType() {
		this.operType=1; //响应
	}
	
	@Override
	public void doReqBatch() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doReqLinkage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doResBatch() {
        TaDataInterfaceBatch taDataInterfaceBatch = new TaDataInterfaceBatch();
        taDataInterfaceBatch.setPub(this.getPub());
        taDataInterfaceBatch.setConnection(this.getConnection());
        taDataInterfaceBatch.setRequestMsgXml(this.getRequestMsgXml());
        new Thread(taDataInterfaceBatch).start(); //实现异步调用
        Document doc = Console.createXml(null, "100", "100", "1.0", "", this.getTxcode(), false);
		Element body = doc.getRootElement().addElement("body");
		body.addElement("status").setText("0");//状态为成功
		//body.addElement("remark").setText("接收成功");
		this.setResponesMsgXml(doc);
	}

	@Override
	public void doResLinkage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doReqOnLine() {
		
	}
	
	@Override
	public void doResOnLine() {}
	
   /**
    * 对一条记录的拼接
    * @param assertCode
    * @param sellType
    * @param sellnetcode
    * @param sellMoney
    * @param otherAssertCode
    * @param curycode
    * @return
    */
	private String CreateTxt(String assertCode,String sellType,String sellnetcode,String sellMoney,
			String otherAssertCode,String curycode , String accountsDate){
		String record =null;
		record =  assertCode + "\t" + sellType + "\t" + sellnetcode + "\t" + sellMoney + "\t" + otherAssertCode + 
		          "\t" + curycode+ "\t" + accountsDate + "\r\n";
		return record;
	}

	/**
	 * 实现线程异步
	 * */
	public void run() {
		// TODO Auto-generated method stub
		this.writeTxt();
	}
	
	/**
	 * 到处文件 TXT
	 * */
	private void writeTxt(){

    	Element record = null;
    	ResultSet taData = null;
    	String sql = "";
    	StringBuffer content = new StringBuffer();
    	ResultSet assetRs = null;
    	String accountsDate = "";
    	
    	if(this.getRequestMsgXml() != null){
    		if("1111".equalsIgnoreCase(this.getRequestMsgXml().getRootElement().element("head").elementText("replycode")))
				return;
    		Element body = this.getRequestMsgXml().getRootElement().element("body"); 	//获取body标签
    		if(body != null){
    			List<Element> records = body.elements("ENTITY");
    			if(records.size() > 0){
    				for (int i = 0; i < records.size(); i++) {
    					record = records.get(i);
    					accountsDate = record.elementText("accountsDate");		//销售日期
    					//accountsDate = "2013-4-19";
    					try {
							assetRs = this.getPub().getDbLink().openResultSet("select * from tb_sys_assetgroup" +
								                                         " where flocked = 0 order by FAssetGroupCode");
						    while(assetRs.next()){
						    	String groupCode = assetRs.getString("FAssetGroupCode");
						    	
						    	if(!this.getPub().getDbLink().yssTableExist(("tb_" + groupCode + "_para_portfolio").toUpperCase()))
						    		continue;
		    					//转换业务类型
		    					Hashtable<String, String> bussTypes = this.getMarket(groupCode , "AC_DicBussType" ,
		    							                                                     "Ta业务类型转化出错");
		    					Hashtable<String, String> htCuryToNums = this.getMarket(groupCode , "AC_DicCuryToNum",
		    							"获取币种转编码出错：");
		    	    			
		    	    			sql = "select pf.fassetcode as fassetcode,ta.fselltype as fselltype," +
		    	    				  " ta.fsellnetcode as fsellnetcode,ta.fsellmoney as fsellmoney," +
		    	    				  " ta.fcurycode as fcurycode , " +
		    	    				  " decode(ta.fselltype ,'01', ta.FSellamount , 0) as FSGsellamount," +
		    	    				  " decode(ta.fselltype ,'02', ta.FSellamount , 0) as FSHsellamount," +
		    	 " (case when ta.fselltype = '02' and FFeeCode1 = '009' then ta.FtradeFee1 else 0 end)  as FtradeFee1 ," +
		    	 " (case when ta.fselltype = '02' and FFeeCode2 = '008' then ta.FtradeFee2 else 0 end)  as FtradeFee2 ," +
		    	 " (case when ta.fselltype = '02' and FFeeCode3 = '010' then ta.FtradeFee3 else 0 end)  as FtradeFee3 ," +
		    	 " '-' as fcpdm from ( select * from " +
		    	    				  " tb_" + groupCode + "_para_portfolio where fcheckstate = 1 and Fenabled = 1 ) pf " +
		    	    				  " join (select * from tb_" + groupCode + "_TA_Trade where fcheckstate = 1 " +
		    	    				  " and ftradedate = " + this.getPub().getDbLink().sqlDate(accountsDate) + 
		    	    				  " and fselltype in ('01','02')) ta " +
		    	    				  " on pf.fportcode = ta.fportcode order by pf.fassetcode ,ta.fselltype ," +
		    	    				  " ta.fsellnetcode,ta.fcurycode";
		    	    			taData = this.getResult(sql , "查询TA交易数据出错："); //获得查询的结果集
		    	    			try {
									while(taData.next()){
										String assertCode = taData.getString("fassetcode");//资产代码
										String selltype = taData.getString("fselltype");//销售类型
										
										String sellnetcode = taData.getString("fsellnetcode"); //网点代码
										String sellmoney = taData.getString("fsellmoney"); //数值
										String otherAssertCode = taData
												.getString("fcpdm") == null ? ""
												: taData.getString("fcpdm");// 对方产品代码
										String curycode = taData.getString("fcurycode");
										
										String tmpCury = "";
										if(htCuryToNums != null)
											tmpCury = htCuryToNums.get(curycode);
										
										if(tmpCury != null && tmpCury.trim().length() > 0)
											curycode = tmpCury ;
										else
											curycode = "156";
										
										String str = "";
										
										if("01".equalsIgnoreCase(selltype)){
											//申购份额
											str = this
													.CreateTxt(
															assertCode,
															"02",
															sellnetcode,
															""+ taData.getDouble("FSGsellamount"),
															otherAssertCode,
															curycode , accountsDate);
											content.append(str);
											//后端申购费
											str = this
													.CreateTxt(
															assertCode,
															"07",
															sellnetcode,
															""+ taData.getDouble("FtradeFee3"),
															otherAssertCode,
															curycode,accountsDate);
											content.append(str);
										}else if("02".equalsIgnoreCase(selltype)){
											//赎回份额
											str = this
													.CreateTxt(
															assertCode,
															"04",
															sellnetcode,
															"" + taData.getDouble("FSHsellamount"),
															otherAssertCode,
															curycode,accountsDate);
											content.append(str);
											
											//赎回手续费
											str = this
													.CreateTxt(
															assertCode,
															"05",
															sellnetcode,
															"" + taData.getDouble("FtradeFee1"),
															otherAssertCode,
															curycode,accountsDate);
											content.append(str);
											
											//赎回费收入
											str = this
													.CreateTxt(
															assertCode,
															"06",
															sellnetcode,
															"" + taData.getDouble("FtradeFee2"),
															otherAssertCode,
															curycode,accountsDate);
											content.append(str);
										}
										String tmp = "";
										if(bussTypes != null)
											tmp = bussTypes.get(selltype);
										if(tmp != null && tmp.trim().length() > 0)
											selltype = tmp;
										str = this.CreateTxt(assertCode,
												selltype, sellnetcode,
												sellmoney, otherAssertCode,
												curycode, accountsDate);
										content.append(str);
									}
								} catch (Exception e) {
									this.setDoSign("1");
									this.setReplyRemark(this.getReplyRemark() + "系统处理TA批量接口失败" + e.getMessage() + "\n");
									System.out.println("系统处理TA批量接口失败" + e.getMessage());
								}finally{
									try {
										this.getPub().getDbLink().closeResultSetFinal(taData);
									} catch (Exception e2) {
										e2.getMessage();
									}
								}
						    }
    					}catch (Exception e) {
    						this.setDoSign("1");
							this.setReplyRemark(this.getReplyRemark() + "系统处理TA批量接口失败" + e.getMessage() + "\n");
							System.out.println("系统处理TA批量接口失败" + e.getMessage());
						}
					}
    				String txtName = "QD_TA_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+ ".txt";
					String path = this.getPropertiesPath("ncbs.tapl.txtpath");//需要在properties文件中配置的路径
					try {
						if(path != null && path.trim().length() > 0){
							path = this.getEndsWithFileSeparator(path);
						    YssUtil.writeTxt(path , txtName, content.toString() ,"UTF-8");
						}
					} catch (Exception e) {
						path = "";
						this.setDoSign("1");
						this.setReplyRemark(this.getReplyRemark() + "系统处理TA批量接口写入TXT文件失败：" + e.getMessage() + "\n");
						System.out.println("系统处理TA批量接口写入TXT文件失败：" + e.getMessage());
					}
					//ftp路径
    				String ftpPath = this.getPropertiesPath("ncbsftp.tapl.txtpath");
        			try {
						if (ftpPath != null && ftpPath.trim().length() > 0) {
							ftpPath = this.getEndsWithFileSeparator(ftpPath);
							ftpPath = this.getEndsWithFileSeparator(ftpPath + new SimpleDateFormat("yyyyMMdd").format(new Date()));
							if (path != null && path.trim().length() > 0)
								this.ftpUpLoad(ftpPath, path, txtName); // 上传至ftp
						}
        			} catch (Exception e) {
        				ftpPath = "";
        				this.setDoSign("1");
						this.setReplyRemark(this.getReplyRemark() + "系统处理TA批量接口上传FTP文件出错：" + e.getMessage() + "\n");
						System.out.println("系统处理TA批量接口上传FTP文件出错：" + e.getMessage());
        			} finally {
        			    content.setLength(0); //清空内容
        			}
        			this.requestBatch(ftpPath,accountsDate, txtName); // 请求批量文件接收
    			}
    		}
    	}	
	}
	
	/**
	 * 请求批量文件接收
	 * */
	private void requestBatch(String txtPath , String workDate, String fileName){
		Document doc = Console.createXml(null, "100", "100", "1.0", "", "AL044IT15", true);
		//if("0".equalsIgnoreCase(this.getDoSign())){ //正常数据则返回body标签
			Element body1 = doc.getRootElement().addElement("body");
			//Element record = body1.addElement("record");
			body1.addElement("accountsdate").setText(workDate);
			body1.addElement("filename").setText(fileName);
			body1.addElement("filepath").setText(txtPath);
			body1.addElement("filetype").setText("27");
		//}
		AccountClinkageService_Service service = new AccountClinkageService_Service();
		service.getAccountClinkageServicePort().doDeal(doc.asXML());
	}
}
