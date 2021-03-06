package com.yss.ciss.ws.service.serviceImp;

import com.yss.ciss.ws.service.*;
import com.yss.ciss.ws.service.serviceIf.IWSforYssCISS;
import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.webServices.client.swiftClient.WSTask;
import com.yss.webServices.client.swiftClientAuto.IYssService;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebService(serviceName = "WSforYssCISS",endpointInterface = "com.yss.ciss.ws.service.serviceIf.IWSforYssCISS",
		targetNamespace = "http://www.ysstech.com/QDII/CISSWSServiceImp",portName = "cissWSPort")
		
//20120416 added by liubo.Story #2439
//响应托管系统请求的webservice实现类
public class WSforYssCISS implements IWSforYssCISS
{
	private YssPub pub = null;	

	/**
	 * modify huangqirong 2012-12-27
	 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
	 * */
	//20120416 added by liubo.Story #2439
	//处理对账结果请求
	public ResponseMsg sendComparedResult(ComparedResultVO resultVO){
		
		ResultSet rs = null;
		String strSql = "";
		ResponseMsg respon = new ResponseMsg();
		PreparedStatement pst = null;
		Connection conn = null;
		Statement stat = null;
		DbBase db = new DbBase();
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-mm-dd"); 

		try
		{
			conn = db.loadConnection();
			
			if (resultVO == null)
			{
				respon.setStatus("0");
				respon.setRemark("传入的ComparedResultVO对象为空！");
				return respon;
			}
						
			strSql = "insert into InfoResult(Fsn,Finfo,Fdcuser,ftime,ftype) values(?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			pst.setString(1, setFsnCode(resultVO.getStartDate(),"InfoResult"));
			pst.setString(2, "对账完成");
			pst.setString(3, resultVO.getExecName());
			pst.setDate(4, new java.sql.Date((bartDateFormat.parse(resultVO.getStartDate())).getTime()));
			pst.setString(5, "估值表");
			
			pst.execute();
			conn.commit();
			
			respon.setStatus("1");
			respon.setRemark("");
			
		}
		catch(Exception ye)
		{
			respon.setStatus("0");
			respon.setRemark("QDII估值系统将对账结果插入电子对账结果表出现错误：" + ye.getMessage());
			/**
			 * modify huangqirong 2012-12-27
			 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
			 * */
			System.out.print("插入电子对账结果表出现错误：" + ye.getMessage());
			//throw new YssException("插入电子对账结果表出现错误：" + ye.getMessage());
			//---end---
		}
		finally
		{
			if(null != conn){try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			if(null != stat){try {
				stat.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			if(null != pst){
				try {
					pst.close();
				}catch (SQLException e) {
					e.printStackTrace();
				}
			}
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		}
		
		return respon;
		
		
	}

	/**
	 * modify huangqirong 2012-12-27
	 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
	 * */
	//20120416 added by liubo.Story #2439
	//处理平台锁定\解锁
	public ResponseMsg sendLockStatus(LockStatusVO resultVO){
		String strSql = "";
		ResponseMsg respon = new ResponseMsg();
		PreparedStatement pst = null;
		Connection conn = null;
		Statement stat = null;
		DbBase db = new DbBase();
		pub = new YssPub();
		/**shashijie 2012-7-2 STORY 2475 */
		//SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-mm-dd"); 
		/**end*/
		try
		{
			conn = db.loadConnection();
						
			if (resultVO == null)
			{
				respon.setStatus("0");
				respon.setRemark("传入的LockStatusVO对象为空！");
				return respon;
			}
			
			strSql = "Delete LockStatus where Fsetcode = " + db.sqlString(resultVO.getProCode()) + " and ftime = " + db.sqlDate(resultVO.getStartDate());
			db.executeSql(strSql);	
			
			strSql = "insert into LockStatus(Fsn,fstatus,Fsetcode,ftime,fdcuser) values(?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			pst.setString(1, setFsnCode(resultVO.getStartDate(),"LockStatus"));
			pst.setString(2, resultVO.getStatus());
			pst.setString(3, resultVO.getProCode());
			pst.setDate(4, YssFun.toSqlDate(resultVO.getStartDate()));
			pst.setString(5, resultVO.getExecName());
			
			pst.execute();
			conn.commit();
			
			respon.setStatus("1");
			respon.setRemark("");
			
		}
		catch(Exception ye)
		{
			respon.setStatus("0");
			respon.setRemark("QDII估值系统将对账数据锁定/解锁相关属性插入电子对账结果表出现错误：" + ye.getMessage());
			/**
			 * modify huangqirong 2012-12-27
			 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
			 * */
			System.out.println("插入电子对账结果表出现错误：" + ye.getMessage());
			//throw new YssException("插入电子对账结果表出现错误：" + ye.getMessage());
			//---end---
		}
		finally
		{
			if(null != conn){try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			if(null != stat){try {
				stat.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			if(null != pst){
				try {
					pst.close();
				}catch (SQLException e) {
					e.printStackTrace();
				}
			}
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		}
		
		return respon;
		

	}

	/**
	 * modify huangqirong 2012-12-27
	 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
	 * */
	//20120416 added by liubo.Story #2439
	//根据传入日期生成每个表的fsn序号
	private String setFsnCode(String sDate, String sTemp){
		String sReturn = "";
		ResultSet rs = null;
		Connection conn = null;
		Statement stat = null;
		String strSql = "";
		pub = new YssPub();
		
		try
		{
			DbBase db = new DbBase();
			
			strSql = "select * from " + sTemp + " where Ftime = to_date('" + sDate + "','yyyy-mm-dd')";
			
			conn = db.loadConnection();
			stat = conn.createStatement();
			
			rs = stat.executeQuery(strSql);
			
			while(rs.next())
			{
				sReturn = rs.getString("Fsn");
			}
			if (sReturn == null || sReturn.trim().equals(""))
			{
				return YssFun.formatDate(sDate,"yyyyMMdd") + "01";
			}
			
			int iSerialNo = Integer.valueOf(sReturn.substring(9,10));
			
			iSerialNo = iSerialNo + 1;
			
			String sss = YssFun.formatDate(sDate, "yyyyMMdd");
			
			if (iSerialNo < 10)
			{
				sReturn = sss + "0" + String.valueOf(iSerialNo);
			}
			else
			{
				sReturn = sss + String.valueOf(iSerialNo);
			}
			
			/**
			 * modify huangqirong 2012-12-27
			 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
			 * 注释掉这个返回处理
			 * */
			//return sReturn;			
		}
		catch(Exception ye)
		{
			/**
			 * modify huangqirong 2012-12-27
			 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
			 * */
			System.out.println("生成序列号出现错误！" + ye.getMessage());
			//throw new YssException("生成序列号出现错误！" + ye.getMessage());
			//---end---
		}
		finally
		{
			/**
			 * modify huangqirong 2012-12-27
			 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
			 * */
			if(null != conn){
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
				}
			}
			if(null != stat){
				try {
					stat.close();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
				}
			}
			if(null != rs){
				try {
					rs.close();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
				}
			}
			//---end---
		}
		/**
		 * modify huangqirong 2012-12-27
		 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉外层方法 throws异常 后的调整 
		 * */
		return sReturn;
	}

}
