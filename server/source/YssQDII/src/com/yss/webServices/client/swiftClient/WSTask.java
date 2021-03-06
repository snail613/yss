package com.yss.webServices.client.swiftClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.yss.dsub.DbBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.webServices.client.swiftClientAuto.YssCISSService;
/**
 * web service的任务类，在每一周的星期一发送系统的汇率数据到服务机
 * @author yh 2011.05.25 QDV411建行2011年04月19日01_A
 *
 */
public class WSTask extends TimerTask {
	
	private Timer timer = null;
	private static boolean isRunning = false;
	public static String urlLocation = null;
	public WSTask(Timer timer)
	{
		this.timer = timer;
	}
	@Override
	public void run() {
		if(!isRunning)
		{
			//获取当前时间
			Calendar calendar = Calendar.getInstance();
			//如果当前星期是星期一，当前时间段是13点，则执行任务
			if(calendar.get(Calendar.DAY_OF_WEEK) == 2&& calendar.get(Calendar.HOUR_OF_DAY) == 13)
			{	
				isRunning = true;
				sendEXRate();
				isRunning = false;
			}
			
		}

	}
	/**
	 * 调用客户机代码向服务机发送汇率数据
	 */
	private void sendEXRate()
	{	
		int resultCode = 0;
		//从配置文件中获取服务名为YssCISSService的webService服务所在的url地址
		urlLocation = getWsdlUrl("YssCISSService");
		//判断如果没有获取到url，则不再启动客户机
		if(null != urlLocation)
		{	
			//测试用户配置url是否正确
			URL testURL = null;
			try {
				testURL = new URL(urlLocation);
			} catch (MalformedURLException e) {
				//edit by songjie 2012.05.25 BUG 4177 QDV4博时2012年03月29日02_B 添加非空判断
				if(timer != null){
					timer.cancel();
				}
				return;
			}
			//设置此类是否应该自动执行 HTTP 重定向。
	        HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection urlConn;
			try {
				urlConn = (HttpURLConnection)testURL.openConnection();
				resultCode = urlConn.getResponseCode();
			} catch (IOException e) {
				//edit by songjie 2012.05.25 BUG 4177 QDV4博时2012年03月29日02_B 添加非空判断
				if(timer != null){
					timer.cancel();
				}
				return;
			}
			//如果地址可访问，则执行客户机代码
			if(resultCode == 200){
			//实例化本地化的客户机访问类
			YssCISSService service = new YssCISSService();
			//构造请求报文，发送到服务机
			service.getYssCISSServer().networkServices(buildRequestMsg().asXML(), "SWIFT");
			System.out.println("客户机发送数据成功");
			}
		}
		else
		{
			//关闭定时器，不再定时执行任务
			//edit by songjie 2012.05.25 BUG 4177 QDV4博时2012年03月29日02_B 添加非空判断
			if(timer != null){
				timer.cancel();
			}
		}

	}
	/**
	 * 根据用户配置获取服务的url
	 * @return url
	 */
	private static String getWsdlUrl(String webServiceName)
	{
		String url = null;
		Document doc = null;
		SAXReader saxRead = null;
		File cfgFile = new File("/wsdlUrl.xml");
		Element root = null;
		try {
			if(null != cfgFile && cfgFile.exists())
			{	
				//读取配置文件还原为xml文档对象
				saxRead = new SAXReader();
				InputStream inStream = new FileInputStream(cfgFile);
				InputStreamReader inReader = new InputStreamReader(inStream,"GB2312");
				doc = saxRead.read(inReader);
				root = doc.getRootElement();
				List<Element> eleList = root != null?root.elements("WebService"):null;
				if(null != eleList){
					for(Element ele : eleList){
						if(null != ele){	
							String name = ele.attributeValue("name");
							if(name != null && name.equals("YssCISSService")){
								url = ele.elementText("url").length()!=0 ? ele.elementText("url"):null;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private Document buildRequestMsg()
	{
		Document doc = null;
		Element root = null;
		Element head = null;
		Element body = null;
		Element bizbody = null;
		List<String[]> rateList = null;
		ArrayList<String> groups = null; // 拥有swiftMsgTrade接口的组合群代码集
		String assetGroupCode = ""; // 组合群代码
		try {
			rateList = new ArrayList<String[]>();
			// 根据返回格式构造返回的文档结构
			doc = DocumentHelper.createDocument();
			// 添加根节点
			root = doc.addElement("request");
			// 添加head节点
			head = root.addElement("head");
			// 在head节点中添加元素
			head.addElement("channel").setText("100");
			head.addElement("branchid").setText("0000");
			head.addElement("version").setText("1.0");
			head.addElement("txcode").setText("100011001");
			head.addElement("msgno").setText(getMsgNo());
			head.addElement("smsgtype").setText("000");
			head.addElement("override").setText("0");
			// 添加body元素
			body = root.addElement("body");
			bizbody = body.addElement("bizdata");
			// 添加bizdata元素数组
			// 因为在后台不知道当前登录的组合群是哪一个，故循环所有的拥有该接口的组合群代码,然后调用接口的处理
			groups = getAssetGroups("swiftMsgTrade");
			if (null != groups && groups.size() != 0) {
				// 处理所有拥有该接口的组合群
				for (int i = 0; i < groups.size(); i++) {
					assetGroupCode = groups.get(i);
					this.getRatefromDb(rateList,assetGroupCode);
				}
			}
			for (int i = 0; i < rateList.size(); i++) {
				String[] temp = rateList.get(i);
				Element biz = bizbody.addElement("curinfo");
				biz.addElement("effect_date").setText(YssFun.formatDate(temp[0], "yyyyMMdd"));
				biz.addElement("cur").setText(temp[1]);
				biz.addElement("rate").setText(temp[2]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 生成由20位数字组成的报文编号，作为报文的唯一标识
	 * @return 报文编号
	 */
	private String getMsgNo()
	{
		String msgNo = null;
		//用当前时间加上一个随机数生成报文编号
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		msgNo = format.format(curDate);
		//由日期组成前14位，由100000到999999之间的数字组成后六位
		msgNo += Math.round(Math.random()*899999+100000);
		return msgNo;
	}
	/**
	 * 从汇率信息表中获取汇率信息
	 * 对于非美元币种，汇率为该币种对美元的汇率。对于美元币种，汇率为美元对人民币的汇率
	 * @return 汇率信息数组，数组的组成单元为字符串数组；字符串数组的结构为：0 汇率日期 1 币种 2 汇率值
	 * @throws SQLException
	 */
	private void getRatefromDb(List<String[]> rateList,String assetGroupCode) throws SQLException
	{
		ResultSet rs = null;
		Connection conn = null;
		Statement stat = null;
		String sTableA  = "TB_" + assetGroupCode + "_DATA_EXCHANGERATE";
		String sTableB  = "TB_" + assetGroupCode + "_PARA_CURRENCY";
        double dReturn = 1;
        double dFactor = 1;
        String sInvertInd = "";
        String sRate = "1";
		try {
			String sql = "SELECT p.FEXRATEDATE effect_date,p.FCURYCODE cur,p.FEXRATE1 rate,c.FInvertInd, c.FFactor FROM " +
			"( SELECT A.FCURYCODE,A.FMARKCURY,A.FEXRATEDATE,A.FEXRATE1 FROM " + sTableA + 
			" a  JOIN ( SELECT MAX(FEXRATEDATE) as FEXRATEDATE FROM  " + sTableA + 
			" WHERE (FMARKCURY = 'CNY' AND FCURYCODE = 'USD') OR (FMARKCURY = 'USD' AND FCURYCODE <> 'USD') AND FCHECKSTATE = 1) b" +
			" ON A.FEXRATEDATE = B.FEXRATEDATE WHERE (A.FMARKCURY = 'CNY' AND A.FCURYCODE = 'USD') OR (A.FMARKCURY = 'USD' " + 
			" AND A.FCURYCODE <> 'USD') AND A.FCHECKSTATE = 1" +
			") p left join (select FInitRate,FInvertInd, FFactor,FCuryCode from " + sTableB +
			" where FCheckState = 1) c on c.fcurycode = p.FCURYCODE";
			
			DbBase db = new DbBase();
			conn = db.loadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			while(rs.next())
			{
                dReturn = rs.getDouble("rate");
                sInvertInd = rs.getString("FInvertInd");
                dFactor = rs.getDouble("FFactor") == 0 ? 1 : rs.getDouble("FFactor");
                if (sInvertInd != null) {
                    if (sInvertInd.equals("1") && dReturn != 0) {
                        sRate = String.valueOf(new BigDecimal(String.valueOf(dFactor)).divide(new BigDecimal(String.valueOf(dReturn)),8,BigDecimal.ROUND_DOWN ));
                    }
                    if (sInvertInd.equals("0")) {
                        sRate = String.valueOf(new BigDecimal(String.valueOf(dReturn)).divide(new BigDecimal(String.valueOf(dFactor)),8,BigDecimal.ROUND_DOWN ));
                    }
                }

				String[] rate = new String[3];
				rate[0] = rs.getDate("effect_date").toString();
				rate[1] = rs.getString("cur");
				rate[2] = sRate;
				rateList.add(rate);
			}
		} catch (YssException e) {
			
			e.printStackTrace();
		}
		finally
		{	
			if(null != conn){conn.close();}
			if(null != stat){stat.close();}
			if(null != rs){rs.close();}
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
		Connection connection = null;

		try {
			if(null != ifCode && ifCode.length()!=0)
			{
				DbBase db = new DbBase();
				connection = db.loadConnection();
				stat = connection.createStatement();
				stat1 = connection.createStatement();
				rs = stat.executeQuery(sqlGroup);
				while(rs.next())
				{	
					groupCode = rs.getString("FASSETGROUPCODE");
					ifTableName = "tb_"+groupCode+"_Dao_CusConfig";
					if(db.yssTableExist(ifTableName))
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
}
