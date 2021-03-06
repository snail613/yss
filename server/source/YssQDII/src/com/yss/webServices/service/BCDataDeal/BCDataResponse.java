package com.yss.webServices.service.BCDataDeal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.yss.dsub.BaseBean;
import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.util.YssException;
import com.yss.webServices.service.SwiftMsgDeal;
/**
 * 
 * @author dongqongsong 2013-06-8 中行
 *
 */

public class BCDataResponse extends BaseBean {
	
	private String requestMessageString ; //请求信息
	private String responseMessageString;//响应信息
	
	private Document requestProductMsgDoc; //请求资产信息的xml
	private Document requestBusinessMsgDoc; //请求业务数据的xml
	
	private Document responseProductDoc; //响应的资产信息的xml
	private Document responseBusinessDoc;//响应的业务数据的xml
	
	private String getProductMsg; //资产信息获取
	private String getBusinessMsg; //业务数据
	private Connection connection = null; 	// 数据库连接
	private DbBase dbl;
	private YssPub pub;
	private Document doc = DocumentHelper.createDocument();

	public Document getDoc() {
		return doc;
	}
	
	public void setDoc(Document doc) {
		this.doc = doc;
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
	
	public DbBase getDbl() {
		return dbl;
	}
	
	public void setDbl(DbBase dbl) {
		this.dbl = dbl;
	}
	
	public String getGetProductMsg() {
		return getProductMsg;
	}
	
	public void setGetProductMsg(String getProductMsg) {
		this.getProductMsg = getProductMsg; //资产信息获取
	}
	
	public String getRequestMessageString() {
		return requestMessageString;
	}
	
	public void setRequestMessageString(String requestMessageString) {
		this.requestMessageString = requestMessageString;
	}
	
	public String getResponseMessageString() {
		return responseMessageString;
	}
	
	public void setResponseMessageString(String responseMessageString) {
		this.responseMessageString = responseMessageString;
	}
	
	public String getGetBusinessMsg() {
		return getBusinessMsg;
	}
	
	public void setGetBusinessMsg(String getBusinessMsg) {
		this.getBusinessMsg = getBusinessMsg;
	}
	
	public Document getRequestProductMsgDoc() {
		return requestProductMsgDoc;
	}
	
	public void setRequestProductMsgDoc(Document requestProductMsgDoc) {
		this.requestProductMsgDoc = requestProductMsgDoc;
	}
	
	public Document getRequestBusinessMsgDoc() {
		return requestBusinessMsgDoc;
	}
	
	public void setRequestBusinessMsgDoc(Document requestBusinessMsgDoc) {
		this.requestBusinessMsgDoc = requestBusinessMsgDoc;
	}
	
	public Document getResponseProductDoc() {
		return responseProductDoc;
	}
	
	public void setResponseProductDoc(Document responseProductDoc) {
		this.responseProductDoc = responseProductDoc;
	}
	
	public Document getResponseBusinessDoc() {
		return responseBusinessDoc;
	}
	
	public void setResponseBusinessDoc(Document responseBusinessDoc) {
		this.responseBusinessDoc = responseBusinessDoc;
	}
	
	/**
	 * 初始化相关变量。链接数据库
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 * @throws Exception 
	 * */
	private void init(){
		if(this.pub == null){
			this.pub = new YssPub();
			pub.setDbLink(new DbBase());
			this.setDbl(pub.getDbLink());
		}
		try {
			this.connection = this.pub.getDbLink().loadConnection();
		} catch (YssException e) {
			System.out.println("初始化变量出错：" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 资产信息的主入口
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 * @throws Exception
	 */
	private void getProduct() throws Exception{
		ResultSet rs =null;
		this.createCommon();
		String assertGroup = this.getAssetGroup();
		String assertGroupInfo [] = assertGroup.split(",");
		for (int i = 0; i < assertGroupInfo.length; i++) {//循环组合群
			rs =this.getAssertGroupInfo(assertGroupInfo[i]);
			while(rs.next()){
				String zhmc = rs.getString("zhmc");//组合名称
				String zhdm = rs.getString("zhdm");//资产代码
				String qyrq = rs.getString("qyrq");//签约日期 固定为’1900-01-01’
				String yxrq = rs.getString("yxrq");//运行日期
				String zzrq = rs.getString("zzrq");//终止日期
				this.createAssetDataInfo(zhmc,zhdm,qyrq,yxrq,zzrq);
			}
		}
		this.setResponseProductDoc(this.getDoc());
	}
	
	/**
	 * 创建Document文档的<head>和<body>两个节点
	 * add dongqingsong 2013-06-08 story #3753  [功能是实现托管服务系统与QDII4.0系统进行webservice对接]
	 * @param version
	 * @param msgType
	 * @param notes
	 */
	private void createCommon(){
		try {
			Document doc = this.getDoc();
			Element request = this.getDoc().addElement("request");
			Element head = request.addElement("head");
			Element versionInfo = head.addElement("version");
			Element msgTypeInfo = head.addElement("msgType");
			Element notesInfo = head.addElement("notes");
			versionInfo.setText("1.2");
			msgTypeInfo.setText("getProduct");
			notesInfo.setText(" ");
			Element body = request.addElement("body");
			
		} catch (Exception e) {
			System.out.println("创建资产信息出错！"+e.getMessage());
		}
	}
	/**
	 * 创建资产信息xml文档
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 */
	
	private void createAssetDataInfo(String PortName,String AssetCode,String qyrqDate,String createDate,String endDate){

		Element datainfo = this.getDoc().getRootElement().element("body").addElement("datainfo");
		Element tgywxmc = datainfo.addElement("tgywxmc");//托管业务线名称
		Element tgcpdlmc = datainfo.addElement("tgcpdlmc");//托管产品大类名称
		Element tgcpxmc = datainfo.addElement("tgcpxmc");//托管产品线名称
		Element khmc = datainfo.addElement("khmc");//客户名称 
		
		Element zhmc = datainfo.addElement("zhmc");//组合名称  [A]
		Element zhdm =datainfo.addElement("zhdm");//资产代码     [A]
		Element qyrq =datainfo.addElement("qyrq");//签约日期(日期格式为“yyyy-MM-dd”)[A]
		Element qyh = datainfo.addElement("qyh");//签约行
		Element yxh = datainfo.addElement("yxh");//营销行
		Element sstx = datainfo.addElement("sstx");//所属条线
		Element khh = datainfo.addElement("khh");//开户行
		Element yyh = datainfo.addElement("yyh");//运营行
		Element tgfrzh = datainfo.addElement("tgfrzh");//托管费入账行
		Element yxrq = datainfo.addElement("yxrq");//运作日期(日期格式为“yyyy-MM-dd”)[A]
		Element zzrq = datainfo.addElement("zzrq");//终止日期(日期格式为“yyyy-MM-dd”)[A]
		
		zhmc.setText(PortName);
		zhdm.setText(AssetCode);
		qyrq.setText(qyrqDate);
		yxrq.setText(createDate);
		zzrq.setText(endDate);
		
		tgywxmc.setText(" ");
		tgcpdlmc.setText(" ");
		tgcpxmc.setText(" ");
		khmc.setText(" ");
		qyh.setText(" ");
		yxh.setText(" ");
		sstx.setText(" ");
		khh.setText(" ");
		yyh.setText(" ");
		tgfrzh.setText(" ");
	}
	
	/**
	 * 通过公共的方法获取组合群的信息
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 * @throws YssException 
	 * @throws SQLException 
	 */
	private String getAssetGroup() throws Exception{
		StringBuffer assetGroup= new StringBuffer();
		String sql= " select t.fassetgroupcode from tb_sys_assetgroup t";
		ResultSet rs = this.getDbl().openResultSet(sql);
		while(rs.next()){
			assetGroup.append(rs.getString("fassetgroupcode")).append(",");
		}
		return assetGroup.toString();
	}
	
	/**
	 * 得到每个组合群信息
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 * @param assetGroupCode
	 * @return
	 */
	private ResultSet getAssertGroupInfo(String assetGroupCode){
		ResultSet rs =null;
		String sql = "select tt.fportname as zhmc, tt.fassetcode as zhdm, " +
				"to_char(tt.FInceptionDate,'yyyy-MM-dd') as yxrq,to_char(tt.FExpirationDate,'yyyy-MM-dd') as zzrq," +
				"'1900-01-01' as qyrq from" +" tb_"+assetGroupCode+"_para_portfolio tt";
		try {
			 rs = dbl.openResultSet(sql);
		} catch (Exception e) {
			System.out.println("查询组合群信息失败"+e.getMessage());
		}
		return rs;
		
	}
	/**
	 * 保存xml到本地成为xml文件
	 * @param doc
	 * @param dataPath
	 * @param partPath
	 */
	private void saveAsXml(Document doc , String dataPath , String partPath){
		
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
	
	/*****************************************************************************/
	
	
	/**
	 * 得到业务数据的请求信息
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 * @throws Exception 
	 */
	private String getBusReqInfo(){

		// 遍历托管系统的请求信息,将资产代码拼接成字符串
		StringBuffer sb= new StringBuffer();
		Element body = this.getRequestBusinessMsgDoc().getRootElement().element("body");
		String busDate = this.getRequestBusinessMsgDoc().getRootElement().element("head").elementTextTrim("busDate");
		Iterator iter = body.elementIterator("datainfo");
		while (iter.hasNext()) {
			Element recordEle = (Element) iter.next();
			String zhdm = recordEle.elementTextTrim("zhdm");
			sb.append(zhdm).append(",");
		}
		return sb.toString();
	}
	
	/**
	 * 创建业务数据报的头部数据
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 */
	private void createBusCommon(String m_busDate){
		try {
			Document doc = this.getDoc();
			Element request = this.getDoc().addElement("request");
			Element head = request.addElement("head");
			Element versionInfo = head.addElement("version");
			Element msgTypeInfo = head.addElement("msgType");
			Element busDate = head.addElement("busDate");
			Element notesInfo = head.addElement("notes");
			versionInfo.setText("1.2");
			msgTypeInfo.setText("busDataList");
			busDate.setText(m_busDate);
			notesInfo.setText(" ");
			Element body = request.addElement("body");
//			System.out.println(this.getResponseBusinessDoc().asXML());
		} catch (Exception e) {
			System.out.println("创建资产信息出错！"+e.getMessage());
		}
	}
	
	/**
	 * 创建datainfo的循环体
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 * @param m_zhdm
	 * @param m_jjjz
	 * @param m_jtje
	 * @param m_sfje
	 * @param m_ljjtje
	 * @param m_ljsfje
	 * @param m_qmck
	 */
	private void BusinessDate(String m_zhdm,String m_jjjz,String m_jtje,String m_sfje,String m_ljjtje,
			String m_ljsfje,String m_qmck){
		Element datainfo = this.getDoc().getRootElement().element("body").addElement("datainfo");
		Element zhdm = datainfo.addElement("zhdm");//基金代码名称
		Element jjjz = datainfo.addElement("jjjz");//基金净值 
		Element jtje = datainfo.addElement("jtje");//本月计提托管费金额 
		Element sfje = datainfo.addElement("sfje");//本月实付托管费金额 
		Element ljjtje= datainfo.addElement("ljjtje");//年度累计计提托管费 
		Element ljsfje = datainfo.addElement("ljsfje");//年度累计实付托管费 
		Element qmck = datainfo.addElement("qmck");//期末存款 
		
		zhdm.setText(m_zhdm);
		jjjz.setText(m_jjjz);
		jtje.setText(m_jtje);
		sfje.setText(m_sfje);
		ljjtje.setText(m_ljjtje);
		ljsfje.setText(m_ljsfje);
		qmck.setText(m_qmck); 
	}
	
	/**
	 * 获得业务数据的主方法 ,创建业务数据的xml文档
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 * @throws Exception
	 */
	private void getBusiness() throws Exception{
		ResultSet rs = null;
		String inputInfo = this.getBusReqInfo();
		String busDate = this.getRequestBusinessMsgDoc().getRootElement().element("head").elementTextTrim("busDate");
		String assetCode[]= null;
		this.createBusCommon(busDate);
		if(inputInfo!=null){
			 assetCode = inputInfo.split(",");
		}
		for (int i = 0; i < assetCode.length; i++) {
			String setCode = assetCode[i];
			String[]groupCodeAndPortcode = this.getPortCodeBySetCode(setCode);
			
			String groupCode = groupCodeAndPortcode[0];//组合群代码
			String portcode = groupCodeAndPortcode[1];//组合代码
			
			rs = this.BusinDataInfo(setCode,groupCode,portcode,busDate);
			while(rs.next()){
				String portCode = rs.getString("portCode");
				String portnetvalue = rs.getString("fportnetvalue") == null ? "0" :rs.getString("fportnetvalue");
				String jtje = rs.getString("jtjeSql")== null ? "0" :rs.getString("jtjeSql");
				String sfje = rs.getString("sfjeSql")== null ? "0" :rs.getString("sfjeSql");
				String ljjtj = rs.getString("ljjtjeSql")== null ? "0" :rs.getString("ljjtjeSql");
				String ljsfje = rs.getString("ljsfje")== null ? "0" :rs.getString("ljsfje");
				String qmck= rs.getString("qmck")== null ? "0" :rs.getString("qmck");
				this.BusinessDate(portCode, portnetvalue, jtje, sfje, ljjtj, ljsfje, qmck);
			}
		}
		this.setResponseBusinessDoc(this.getDoc());
	}
	
	
	/**
	 * 统计业务数据中的各种费用
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
	 * @param assetCode
	 * @param groupCode
	 * @param portCode
	 * @param busDate
	 * @return
	 */
	private ResultSet BusinDataInfo(String assetCode,String groupCode,String portCode,String busDate){
		ResultSet rs =null;
		//组合名称
		String portCodeSql = "select t.fportname from tb_"+groupCode+"_para_portfolio t " +
				"where  t.fcheckstate='1' and t.fassetcode="+dbl.sqlString(assetCode); 
		//基金净值
		String fportnetvalue = "select sum(tt.fportnetvalue) from tb_"+groupCode+"_data_netvalue  tt " +
				"where tt.fportcode= "+dbl.sqlString(portCode)+" and tt.fnavdate=to_date('"+busDate+"','yyyy/MM/dd') "+
				"and tt.ftype='01'  and tt.fcheckstate='1' group by fportcode" ; 
		//本月计提托管费金额
		String jtjeSql = "select sum(bb.fportcurymoney) from Tb_"+groupCode+"_Data_InvestPayRec bb where " +
				"bb.Fivpaycatcode = 'IV002' and bb.fsubtsftypecode = '07IV' and bb.fportcode ="+dbl.sqlString(portCode)+ 
				"and bb.ftransdate between trunc(to_date('"+busDate+"', 'yyyy/mm/dd'), 'mm') and " +
				"to_date('"+busDate+"', 'yyyy/mm/dd') and bb.fcheckstate='1' group by fportcode";
		//本月实付托管费金额
		String sfjeSql = "select sum(m.fportcurymoney) from Tb_"+groupCode+"_Data_InvestPayRec m where " +
				"m.Fivpaycatcode = 'IV002' and m.fsubtsftypecode = '03IV' and " +
				"m.fportcode ="+dbl.sqlString(portCode)+" and m.ftransdate between " +
				" trunc(to_date('"+busDate+"', 'yyyy/mm/dd'), 'mm') and" +
				" to_date('"+busDate+"', 'yyyy/mm/dd') and m.fcheckstate='1'  group by fportcode";
		//年度累计计提托管费
		String ljjtjeSql = "select sum(n.fportcurymoney) from Tb_"+groupCode+"_Data_InvestPayRec n " +
				"where n.Fivpaycatcode = 'IV002'  and n.fsubtsftypecode = '07IV' and " +
				"n.fportcode ="+dbl.sqlString(portCode)+" and n.ftransdate between " +
				"trunc(to_date('"+busDate+"', 'yyyy/mm/dd'), 'yyyy') and to_date('"+busDate+"', 'yyyy/mm/dd') " +
				" and n.fcheckstate='1' group by fportcode";
		//年度累计实付托管费
		String ljsfje = "select sum(l.fportcurymoney) from Tb_"+groupCode+"_Data_InvestPayRec l  " +
				"where l.Fivpaycatcode = 'IV002' and l.fsubtsftypecode = '03IV' and " +
				"l.fportcode ="+dbl.sqlString(portCode)+" and l.ftransdate " +
				"between trunc(to_date('"+busDate+"', 'yyyy/mm/dd'), 'yyyy') and " +
				"to_date('"+busDate+"', 'yyyy/mm/dd') and l.fcheckstate='1'  group by fportcode";
		//期末存款
		String qmck = "select sum(s.fportcurybal) from Tb_"+groupCode+"_Stock_Cash s, tb_"+groupCode+"_para_cashaccount d" +
				" where s.fcashacccode = d.fcashacccode and d.facctype = '01'  and d.fcheckstate = '1'" +
				" and s.fcheckstate = '1' and s.fstoragedate = to_date('"+busDate+"', 'yyyy/mm/dd')" +
				" group by fstoragedate";
		
		StringBuffer sb =new StringBuffer();
		sb.append("select (").append(portCodeSql).append(" ) as portCode,");
		sb.append("(").append(fportnetvalue).append(" ) as fportnetvalue,");
		sb.append("(").append(jtjeSql).append(" ) as jtjeSql,");
		sb.append("(").append(sfjeSql).append(" ) as sfjeSql,");
		sb.append("(").append(ljjtjeSql).append(" ) as ljjtjeSql,");
		sb.append("(").append(ljsfje).append(" ) as ljsfje,");
		sb.append("(").append(qmck).append(" ) as qmck from dual");
//		System.out.println(sb.toString());
		try {
			 rs = dbl.openResultSet(sb.toString());
		} catch (SQLException e) {
			System.out.println("sql语句出错"+e.getMessage());
		} catch (YssException e1) {
			System.out.println("查询结果出错"+e1.getMessage());
		}
		return rs;
	}
	
	/**
	 * 根据资产代码获取组合代码 ,遍历所有组合群
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换]
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
			System.out.println("处理数据时查询资产代码及组合时出错：" + e.getMessage());
		}finally{
			this.pub.getDbLink().closeResultSetFinal(rs);
			if(pstp != null){
				try {
					pstp.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
		return new String[]{groupCode,portcode};
	}
	
	/**
	 * 方法主入口
	 * add dongqingsong 2013-06-14 story 3753  [中行托管服务于QDII数据交换] 
	 * @param requestMsg
	 * @return
	 */
	public String WebServiceInterface(String requestMsg){
		this.init();
		if(!requestMsg.equals(null)&&!requestMsg.equals("")){
			try {
				Document document  = DocumentHelper.parseText(requestMsg);
				String msgType = document.getRootElement().element("head").elementTextTrim("msgType");
				if(msgType.equals("getProduct")){
					this.setRequestProductMsgDoc(document);
					try {
						this.getProduct();
						this.saveAsXml(this.getResponseProductDoc(),  "D:\\Test\\product\\", "");
					} catch (Exception e) {
						System.out.println("获取资产信息数据出错"+e.getMessage());
					}
//					System.out.println(this.getResponseProductDoc().asXML());
					this.setResponseMessageString(this.getResponseProductDoc().asXML());
				}
				if(msgType.equals("getBusData")){
					this.setRequestBusinessMsgDoc(document);
					try {
						this.getBusiness();
						this.saveAsXml(this.getResponseBusinessDoc(), "D:\\Test\\Businsess\\", "");
					} catch (Exception e) {
						System.out.println("获取业务信息数据出错"+e.getMessage());
					}
//					this.setResponseMessageString(this.getResponseBusinessDoc().asXML());
				}
			} catch (DocumentException e) {
				System.out.println("解析字符串数据报错误"+e.getMessage());
			}
		}
		return this.getResponseMessageString();
	}
	
	
//	public static void main(String[] args) throws Exception {
//		BCDataResponse zh = new BCDataResponse();
//		Document doc=null;
//		SAXReader saxReader = new SAXReader();
//		String str="D:\\Workspace\\YssQDII\\src\\com\\yss\\webServices\\service\\1.xml";
//		try {
//			 doc = saxReader.read(new FileInputStream(new File(str)));
//			 String	 requestMsg=doc.asXML();
//			 zh.WebServiceInterface(requestMsg);
//		} catch (Exception e) {
//			System.out.println("解析信息出错"+e.getMessage());
//		}
//		
//		
//	}
}
