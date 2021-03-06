/**   
* @Title: Console.java 
* @Package com.yss.webServices.AccountClinkage 
* @Description: TODO( ) 
* @author KR
* @date 2013-5-13 上午11:56:39 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.yss.dsub.YssPub;

/** 
 * @ClassName: Console 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-13 上午11:56:39 
 *  add by huangqirong 2013-05-09 story #3871 需求北京-[建设银行]QDII系统[高]20130419001
 */
public class Console {
	/**
	 * 字符串转生成XML文件
	 * 个标签名字对应各参数名字
	 * @param txcode : 这个参数为接口的指令代码
	 * !param body :body 标签
	 * @param 代表 是否请求还是应答 isRequest ： true = request ，false = 应答
	 * */
	public static Document createXml(Document document ,String channel , String branchid ,String version , String msgno,
			String txcode , boolean isRequest){
		if(document == null)
			document = DocumentHelper.createDocument();
		Element element = document.getRootElement();
		
		if(isRequest)
		{
			if(element == null){
				element = document.addElement("request");			
			}else if (!document.getRootElement().getName().equalsIgnoreCase("request"))
			{
			    element = document.addElement("request");
			}
		}
		else
		{
			if(element == null){
				element = document.addElement("response");
			} else if (!document.getRootElement().getName().equalsIgnoreCase("response"))
			{
			    element = document.addElement("response");
			}
		}
		Element head = document.getRootElement().addElement("head");
		head.addElement("channel").setText(channel);
		head.addElement("branchid").setText(branchid);
		head.addElement("version").setText(version);
		head.addElement("msgno").setText(msgno);
		head.addElement("smsgtype").setText(msgno);
		head.addElement("txcode").setText(txcode);
		return document;
	}
	
	/**
	 * 转换Xml文件
	 * */
	public static Document parseXml(String datas){
		Document document = null;
		try {
			document = DocumentHelper.parseText(datas);	 // parseXml("", "", "", "", "",true);
		} catch (DocumentException e1) {
			System.out.println("创建Xml文件出错:" + e1.getMessage());
		}
		return document;
	}
	
	/**
	 * 市场代码转换
	 * */
	public static Hashtable<String, String> getMarket(YssPub pub , String dictCode , String msg){
		Hashtable<String, String> markets = new Hashtable<String, String>();
		String sql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from "
				+ " Tb_"
				+ pub.getAssetGroupCode()
				+ "_Dao_Dict "
				+ " a left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode left join"
				+ " (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode where 1 = 1 "
				+ " and FDictCode = ? and FSrcConent <> 'null'"
				+ " order by a.FCreateTime desc, a.FCheckTime desc, a.FDictCode";
		ResultSet rs = null;
		PreparedStatement psp = null;
		try {
			psp = pub.getDbLink().getConnection().prepareStatement(sql);
			psp.setString(1, dictCode);
			rs = psp.executeQuery();
			while (rs.next()) {
				markets.put(rs.getString("FSRCCONENT"), rs
						.getString("FCNVCONENT"));
			}
		} catch (Exception e) {
			System.out.println("获取接口字典：" +dictCode + " 出错：" + msg + e.getMessage());
		} finally {
			pub.getDbLink().closeResultSetFinal(rs);
			if (psp != null) {
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
	 * 获取数据 执行sql获取数据的方法
	 */
	public static String getDatabySql(YssPub pub, String sql, String field){
		ResultSet rs = null;
		String result = "";
		PreparedStatement psp = null;
		try {
			psp = pub.getDbLink().getConnection().prepareStatement(sql);			
			rs = psp.executeQuery();
			if(rs.next()){
				if(rs.getString(field) != null)
					result = rs.getString(field);
			}
			rs.close();
			psp.close();
		}catch (Exception e) {			
			System.out.print(e.getMessage());
		}finally{
			pub.getDbLink().closeResultSetFinal(rs);
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
	 * 读取.properties文件是否设置路径
	 * */
	public static String getPropertiesPath(String key){
		String dataPath = "";
		if(key == null || key.trim().length() == 0)
			return "";
		Properties props = getProperties();
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
	public  static Properties getProperties(){
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
	 * 尾部的目录分隔符
	 * */
	public static String getEndsWithFileSeparator(String source){
		if(source == null)
			return "";
		if(source.indexOf("/") > -1 ){
			if(!source.endsWith("/"))
				source = source + "/";
		} else{
			if(!source.endsWith(File.separator))
				source = source + File.separator;
		}
		return source;
	}
}
