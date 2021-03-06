/**   
* @Title: AbsService.java 
* @Package com.yss.webServices.AccountClinkage 
* @Description: TODO add by huangqirong 2013-05-09 story #3871 需求北京-[建设银行]QDII系统[高]20130419001
* @author KR
* @date 2013-5-9 下午03:12:33 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.util.YssException;
import com.yss.webServices.service.SwiftMsgDeal;



/** 
 * @ClassName: AbsService 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-9 下午03:12:33 
 *  add by huangqirong 2013-05-09 story #3871 需求北京-[建设银行]QDII系统[高]20130419001
 *  抽象的服务类
 */
public abstract class AbsService {
	
	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public AbsService() {}
	
	public abstract void setOperType(); //设置operType 的值
	
	public abstract void setDataType();	//设置dataType 的值	
	
	public int operType; //作为是请求或应答 标识 0 = request ； 1 = respones
	
	public int dataType; //数据类型 0 = 联动 ；1 = 联机 ； 2 = 批量
	
	/**
	 * 是请求还是响应处理
	 * */	
	public String doDealOper(String datas){
		this.init();
		this.saveAsXmlByPath(datas); //存储接收数据
		this.requestMsgXml = Console.parseXml(datas); //转换Xml字符串转为Xml的document
		String result = "";
		this.setOperType();
		this.setDataType();
		if ( this.operType == 0 ){
			this.doRequest(); //请求
			//this.buildReplyMsg(this.doSign , this.txcode , "" , this.replyCode , this.replyRemark , true); //生成应答文件
		} else if ( this.operType == 1 ){ //响应
			this.doResponse();
			this.buildReplyMsg(this.doSign, this.txcode, "", this.replyCode, this.replyRemark, false, true); //生成应答文件
		}
		result = this.responesMsgXml.asXML();
		result = this.replaceXml(result, "<body/>", "<body></body>");
		return result;
	}
	
	/**
	 * 初始化相关变量
	 * */
	private void init(){
		if(this.pub == null){
			this.pub = new YssPub();
			pub.setDbLink(new DbBase());
		}
		try {
			this.connection = this.pub.getDbLink().loadConnection();
		} catch (YssException e) {
			System.out.println("初始化变量出错：" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 处理请求的方法
	 * @param request : 字符串数据 (Xml格式字符串)
	 * @return 返回字符串数据 (Xml格式字符串)
	 * */
	private void doRequest( ){
		if(this.dataType == 0){ 		//联动
			this.doReqLinkage();
		}else if(this.dataType == 1){ 	//联机
			this.doReqOnLine();
		}else if(this.dataType == 2){	//批量
			this.doReqBatch();
		}
	}
	
	/**
	 * 处理响应的方法
	 * @param response : 字符串数据 (Xml格式字符串)
	 * @return 返回字符串数据 (Xml格式字符串)
	 * */
    private void doResponse( ){    	
    	if(this.dataType == 0){ 		//联动
			this.doResLinkage();
		}else if(this.dataType == 1){ 	//联机
			this.doResOnLine();
		}else if(this.dataType == 2){	//批量
			this.doResBatch();
		}
    }
        
    /**
     * 处理请求联动接口数据
     * */
    public abstract void doReqLinkage();
    
    /**
     * 处理请求联机接口数据
     * */
    public abstract void doReqOnLine();
    
    /**
     * 处理请求批量接口数据
     * */
    public abstract void doReqBatch();
    
    /**
     * 处理响应联动接口数据
     * */
    public abstract void doResLinkage();
    
    /**
     * 处理响应联机接口数据
     * */
    public abstract void doResOnLine();
    
    /**
     * 处理响应批量接口数据
     * */
    public abstract void doResBatch();
    
	
	private YssPub pub = null;				//用于session bean，处理变量
	private Connection connection = null; 	// 数据库连接
	private String requestMsg = null;		// 请求报文，字符串格式
	private Document requestMsgXml = null;	//请求报文，xml文档格式 
	private Document responesMsgXml = null;	// 应答报文
	private String replyCode = "";			//应答报文的异常代码
	private String replyRemark = null;		//应答备注
	private String doSign = "0";			//处理标志：0 = 正常 ，1 = 异常
	private String txcode = "";				//应答码
	
	public String getTxcode() {
		return txcode;
	}

	public void setTxcode(String txcode) {
		this.txcode = txcode;
	}

	public String getDoSign() {
		return doSign;
	}

	public void setDoSign(String doSign) {
		this.doSign = doSign;
	}
	
	public YssPub getPub() {
		return pub;
	}

	public void setPub(YssPub pub) {
		this.pub = pub;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getRequestMsg() {
		return requestMsg;
	}

	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}

	public Document getRequestMsgXml() {
		return requestMsgXml;
	}

	public void setRequestMsgXml(Document requestMsgXml) {
		this.requestMsgXml = requestMsgXml;
	}

	public Document getResponesMsgXml() {
		return responesMsgXml;
	}

	public void setResponesMsgXml(Document replyMsgXml) {
		this.responesMsgXml = replyMsgXml;
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
	
	/**
	 * 获取路径配置并调用保存Xml文件
	 * */
	public void saveAsXmlByPath(String asXml){
		String acDataPath = this.getPropertiesPath("ac.datapath");
		acDataPath = !acDataPath.endsWith(File.separator) ? acDataPath + File.separator : acDataPath ;
		this.saveAsXml(asXml , acDataPath , "ACData" + File.separator); // ACData 为定死的一个目录
	}
	
	/**
	 * 保存Xml数据文件
	 * */
	public void saveAsXml(String asXml , String dataPath , String partPath){
		
		try {
			if(asXml != null && asXml.trim().length() > 0 && dataPath != null && dataPath.trim().length() > 0){				
				this.saveAsXml(DocumentHelper.parseText(asXml), dataPath , partPath);
			}
		} catch (DocumentException e) {			
			System.out.println("保存Xml数据文件出错：" + e.getMessage());
		}
	}	
	
	/**
	 * 保存Xml数据文件
	 * */
	public void saveAsXml(Document doc , String dataPath , String partPath){
		
		if(doc == null || dataPath == null || dataPath.trim().length() == 0)
			return ;

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
			System.out.println("保存Xml数据文件出错：" + e.getMessage());
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
	 * 读取.properties文件是否设置路径
	 * */
	public String getPropertiesPath(String key){
		String dataPath = "";
		if(key == null || key.trim().length() == 0)
			return "";
		Properties props = this.getProperties();
		String value = props.getProperty(key);
		try {
			dataPath = new String((value == null || value.trim().length() == 0) ? "".getBytes("8859_1") : value.getBytes("8859_1"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataPath;
	}
	
	/**
	 * 读取.properties文件是否设置路径
	 * */
	public Properties getProperties(){
		File file = new File(File.separator + "acdatapath.properties");
		Properties props = new Properties();
		InputStream in = null;
		try {
			if(file.exists()){
				in = new BufferedInputStream(new FileInputStream(file));
				props.load(in);
			}
		} catch (IOException e) {
			System.out.println("读取.properties文件出错：" + e.getMessage());
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return props;
	}
	
	/**
	 * 生成应答报文
	 * @param replyCode	应答码 若正常则为，否则为对应的异常码
	 * @param replyInfo 应答信息
	 * @param txCode 交易码
	 * @param msgNo 报文通讯流水号
	 * @return xml结构的应答报文
	 * 应答报文结构如下：
	 * <response>	
			<head>
				正常
				<channel>100</channel>    
				<branchid>100</branchid> 
				<version>1.0</version>     
				<msgno></msgno>	
				<txcode></txcode>
				异常
				<channel>100</channel>    			
				<branchid>0000</branchid> 
				<version>1.0</version>  
				<txcode>100001001</txcode> 
				<replycode>00</replycode> 		
				<replyremark>XXXXXXXX</replyremark>		
			</head>
			正常时有body标签
			<body>
   				<record></record>
			</body>
		</response>
	 */
	public void buildReplyMsg(String doSign , String txCode , String msgNo , String replyCode , String replyInfo ,
			boolean isRequest , boolean isSave) {	
		Element responseElement = null; //根节点
		Element headElement = null;	//头部
		if(responesMsgXml == null)
			responesMsgXml = DocumentHelper.createDocument();
		else
			responseElement = responesMsgXml.getRootElement();
		
		if(isRequest)
		{
			if(responseElement == null ){
				responseElement = this.responesMsgXml.addElement("request");
				
			}else if (!this.responesMsgXml.getRootElement().getName().equalsIgnoreCase("request")){
				responseElement = this.responesMsgXml.addElement("request");
			}
		}
		else
		{
			if(responseElement == null ){
				responseElement = this.responesMsgXml.addElement("response");
			
			}else if (!this.responesMsgXml.getRootElement().getName().equalsIgnoreCase("response")){
				responseElement = this.responesMsgXml.addElement("response");
			}
		}
		
		if(this.responesMsgXml.getRootElement().element("head") == null){
			//response节点下增加head节点
			headElement = responseElement.addElement("head");
			
			//head节点下增加channel节点，值为100
			headElement.addElement("channel").setText("100");
			//head节点下增加branchid节点，值为0000
			headElement.addElement("branchid").setText("100");
			//head节点下增加version节点，值为1.0
			headElement.addElement("version").setText("1.0");
			//head节点下增加msgno节点
			headElement.addElement("msgno").setText((msgNo != null ? msgNo:""));
			//head节点下增加txcode节点
			headElement.addElement("txCode").setText(txCode != null ? txCode:"");		
			
			if("1".equalsIgnoreCase(doSign)){  //异常
				//head节点下增加replycode节点
				headElement.addElement("replycode").setText("1111");
				//head节点下增加replyremark节点
				headElement.addElement("replyremark").setText(replyInfo != null ? replyInfo:"");
			}else{
				//head节点下增加replycode节点
				headElement.addElement("replycode").setText("0000");//正常
				//head节点下增加replyremark节点
				headElement.addElement("replyremark").setText(""); //正常
			}
		}
		
		if(isSave && this.responesMsgXml != null){
			String gcsDataPath = this.getPropertiesPath("ac.logdatapath");
			gcsDataPath = !gcsDataPath.endsWith(File.separator) ? gcsDataPath + File.separator : gcsDataPath ;
			this.saveAsXml(this.responesMsgXml , gcsDataPath , "ACDataLog" + File.separator);
		}
	}
	
	/**
	 * 根据资产代码获取组合代码
	 */
	protected String[] getPortCodeBySetCode(String setCode){
		ResultSet rs = null;		
		PreparedStatement pstp = null;
		PreparedStatement portPstp = null;
		String portcode = "";
		String groupCode = "";
		try {
			pstp = connection.prepareStatement("select * from Tb_Sys_Assetgroup where FSysCheck = 1 and FLocked = 0" +
					" order by FASSETGROUPCODE");
			rs = pstp.executeQuery();
			while(rs.next()){
				String preTab = rs.getString("FTABPREFIX");
				
				if(!this.getPub().getDbLink().yssTableExist(("tb_" + preTab + "_para_portfolio").toUpperCase()))
					continue;
				
				portPstp = connection.prepareStatement("select * from tb_" + preTab + "_para_portfolio " +
						" where FCHECKSTATE = 1 and FEnabled = 1 and FAssetCode = ?");
				portPstp.setString(1, setCode);
				ResultSet rs1 = portPstp.executeQuery();
				if(rs1.next()){
					portcode = rs1.getString("FPortCode");
					groupCode = preTab;
					break;
				}
				rs1.close();
				portPstp.close();
			}
			
		}catch (Exception e) {
			this.doSign = "1";
			this.replyRemark += "处理数据时查询资产代码及组合时出错：" + e.getMessage() + "\n";
			System.out.println("处理数据时查询资产代码及组合时出错：" + e.getMessage());
		}finally{
			this.pub.getDbLink().closeResultSetFinal(rs);
			if(pstp != null){
				try {
					pstp.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			
		}
		return new String[]{groupCode,portcode};
	}
	
	
	
	/**
	 * 获取一般的Xml文档中的body标签
	 * 一般用于接收数据时用
	 * */
	public Element getRecodeEle(){
		Element body = null;
		if(this.requestMsgXml != null){
			body = this.requestMsgXml.getRootElement().element("body");
		}
		return body;
	}
	
	/**
	 * 获取一般的Xml文档中的body标签
	 * 一般用于接收数据时用
	 * */
	public Element getRecodeEle(Document document){
		Element body = null;
		if(document != null){
			body = document.getRootElement().element("body");
		}
		return body;
	}
	
	
	/**
	 * 市场代码转换
	 * */
	protected Hashtable<String, String> getMarket(String groupCode, String dictCode , String msg){
		Hashtable<String, String> markets = null;
		String sql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
				" Tb_"+groupCode+"_Dao_Dict "+
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
			this.doSign = "1";
			this.replyRemark += msg + e.getMessage() + "\n";
			System.out.println(msg + e.getMessage());
		}finally{
			this.pub.getDbLink().closeResultSetFinal(rs);
			if(psp != null){
				try {
					psp.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		return markets;
	}
	
	/**
	 *  获取证券代码
	 * */
	public String getSecurity(String groupCode , String markCode , String exchangeCode){
		String security = "";
		ResultSet rs = null;
		PreparedStatement psp = null;
		
		String sql = "select * from tb_" + groupCode + "_para_security " + 
				" where FCheckState = 1 and FMarketCode = " + this.pub.getDbLink().sqlString(markCode)
			    + " and FExchangeCode = " + this.pub.getDbLink().sqlString(exchangeCode);
		
		try {
			psp = connection.prepareStatement(sql);			
			rs = psp.executeQuery();
			if(rs.next()){
				security = rs.getString("FSECURITYCODE");
			}
		}catch (Exception e) {
			this.doSign = "1";
			this.replyRemark += "获取获取证券代码出错：" + e.getMessage() + "\n";
			System.out.print("获取获取证券代码出错：" + e.getMessage());
		}finally{
			this.pub.getDbLink().closeResultSetFinal(rs);
			if(psp != null){
				try {
					psp.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return security;
	}
	
	/**
	 * 获取数据 执行sql获取数据的方法
	 */
	public String getDatabySql(String sql, String field ,String msg){
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
			this.doSign = "1";
			this.replyRemark += msg + e.getMessage() + "\n";
			System.out.print(e.getMessage());
		}finally{
			this.pub.getDbLink().closeResultSetFinal(rs);
			if(psp != null){
				try {
					psp.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
		return result;
	}
	
	/**
	 * 获取数据 执行sql获取数据的方法
	 * 支持多个字段取值
	 * */
	public String getDatasbySql(String sql, String fields , String msg){
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
			System.out.print(msg + e.getMessage());
		}finally{
			this.pub.getDbLink().closeResultSetFinal(rs);
			if(psp != null){
				try {
					psp.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
		return result;
	}
	
	/**
	 * add dongqingsong 2013-05-15 story 3871  建行清算联动
	 * @param sql 
	 * @param msg 错误提示信息
	 * @return 查询结果集
	 */
	public ResultSet getResult(String sql,String msg){
		ResultSet rs = null;
		PreparedStatement psp = null;
		try {
			psp = connection.prepareStatement(sql);			
			rs = psp.executeQuery();
		}catch (Exception e) {
			this.doSign = "1";
			this.replyRemark += msg + e.getMessage() + "\n";
			System.out.print(e.getMessage());
		}
		return rs;
	}
	
	/**
	 * add dongqingsong 2013-05-20 story #3871-6 建行清算联动
	 * 通过资产代码和年份获得套账
	 * @param portCode
	 * @return
	 */
	public String getSet(String portCode,String year){
		ResultSet rs = null;		
		PreparedStatement pstp = null;
		String fsetcode = "";
		try {
			String sql = "select distinct(lpad(Fsetcode,3,'0')) as setCode from lsetlist t " +
					"where t.fsetid= '"+portCode+"' and t.fyear = '"+year+"'";
			pstp = connection.prepareStatement(sql);
			rs = pstp.executeQuery();
			if(rs.next()){
				fsetcode = rs.getString("setCode");
			}
		}catch (Exception e) {
			this.doSign = "1";
			this.replyRemark += "查询套账信息：" + e.getMessage() + "\n";
			System.out.println("查询套账信息：" + e.getMessage());
		}finally{
			this.pub.getDbLink().closeResultSetFinal(rs);
			if(pstp != null){
				try {
					pstp.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		return fsetcode;
	}
	
	/**
	 * 上传ftp文件
	 * */
	public void ftpUpLoad(String tagerPath ,String sourcePath , String fileName){
		Properties pro = this.getProperties();
		
		if(pro.getProperty("ftpType") == null || pro.getProperty("ftpType").trim().length() == 0)
			return ;
		
		if(pro.getProperty("ftpAddr") == null || pro.getProperty("ftpAddr").trim().length() == 0)
			return ;
		
		if(pro.getProperty("ftpPort") == null || pro.getProperty("ftpPort").trim().length() == 0)
			return ;
		
		if(pro.getProperty("ftpUser") == null || pro.getProperty("ftpUser").trim().length() == 0)
			return ;
		
		if(pro.getProperty("ftpPassWd") == null || pro.getProperty("ftpPassWd").trim().length() == 0)
			return ;
		
		if(tagerPath != null && tagerPath.trim().length() > 0)
		    this.ftpUpLoadFile(tagerPath ,sourcePath , fileName);
	}
	
	/**
	 * 上传ftp文件
	 * */
	public void ftpUpLoadFile(String tagerPath , String source ,String fileName){
		FtpTool ftp = new FtpTool();
        ftp.upLoad(tagerPath,source , fileName);
        try {
            ftp.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 尾部的目录分隔符
	 * */
	public String getEndsWithFileSeparator(String source){
		if(source == null)
			return "";
		if(source.indexOf("/") > -1 ){
			if(!source.endsWith("/"))
				source = source + "/";
		} else if(source.indexOf(File.separator) > -1){
			if(!source.endsWith(File.separator))
				source = source + File.separator;
		}
		return source;
	}
	
	/**
	 * 替换<body/> -> <body></body>
	 * */
	private String replaceXml(String asXml , String target , String replacement){
		if(asXml != null){
			if(asXml.indexOf(target) > -1){
				asXml = asXml.replace(target, replacement);
			}
		}
		return asXml;
	}
	
	
}
