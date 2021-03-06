package com.yss.main.operdata.moneycontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.NewSharePriceBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * <p>Title: TABean </p>
 * <p>Description: TA数据 </p>
 * @author yangheng
 * @date 20100730
 */
public class TABean extends BaseDataSettingBean implements IDataSetting {
	
	private TABean FilterType=null;
	private String Num="";
	private String sRecycled = ""; //保存未解析前的字符串
	private String PortCode="";//组合代码
	private String PortName="";//组合名称
	private String SellType="";//销售类型
	private String SellTypeName="";//销售类型名称
	private String CashAccCode="";//现金账户代码
	private String CashAccName="";//现金账户名称
	private double SellMoney;//销售金额
	private java.util.Date TradeDate=null;//申请日期
	private java.util.Date SetDate=null;//确认日期
	private java.util.Date SettleDate=null;//结算日期
	private String DataType="";//数据类型
	private String Desc="";//描述说明
    //add by zhaoxianlin  20130129 STORY #2913 start--//
	private String PortClsCode = "";//分级组合代码
	private String PortClsName = "" ;//分级组合名称
	//add by zhaoxianlin  20130129 STORY #2913 end--//
	
	private String OldNum = "";
	private String OldPortCode="";//修改前的组合代码
	private String OldPortName="";//修改前的组合名称
	private String OldSellType="";//修改前的销售类型
	private String OldSellTypeName="";//修改前的销售类型名称
	private String OldCashAccCode="";//修改前的现金账户代码
	private String OldCashAccName="";//修改前的现金账户名称
	private double OldSellMoney;//修改前的销售金额
	private java.util.Date OldTradeDate=null;//修改前的申请日期
	private java.util.Date OldSetDate=null;//修改前的确认日期
	private java.util.Date OldSettleDate=null;//修改前的结算日期
	private String OldDataType="";//修改前的数据类型
	private String OldDesc="";//修改前的描述说明
	
	private String multAuditString = ""; //批量处理数据
	private boolean Show=false;
	
	
	
    public String getPortClsCode() {
		return PortClsCode;
	}

	public void setPortClsCode(String portClsCode) {
		PortClsCode = portClsCode;
	}

	public String getPortClsName() {
		return PortClsName;
	}

	public void setPortClsName(String portClsName) {
		PortClsName = portClsName;
	}
	public String getMultAuditString() {
		return multAuditString;
	}

	public void setMultAuditString(String multAuditString) {
		this.multAuditString = multAuditString;
	}

	public String getNum() {
		return Num;
	}

	public void setNum(String num) {
		Num = num;
	}
	
	public TABean getFilterType() {
		return FilterType;
	}

	public void setFilterType(TABean filterType) {
		FilterType = filterType;
	}

	public String getSRecycled() {
		return sRecycled;
	}

	public void setSRecycled(String recycled) {
		sRecycled = recycled;
	}

	public String getPortCode() {
		return PortCode;
	}

	public void setPortCode(String portCode) {
		PortCode = portCode;
	}

	public String getPortName() {
		return PortName;
	}

	public void setPortName(String portName) {
		PortName = portName;
	}

	public String getSellType() {
		return SellType;
	}

	public void setSellType(String sellType) {
		SellType = sellType;
	}

	public String getSellTypeName() {
		return SellTypeName;
	}

	public void setSellTypeName(String sellTypeName) {
		SellTypeName = sellTypeName;
	}

	public String getCashAccCode() {
		return CashAccCode;
	}

	public void setCashAccCode(String cashAccCode) {
		CashAccCode = cashAccCode;
	}

	public String getCashAccName() {
		return CashAccName;
	}

	public void setCashAccName(String cashAccName) {
		CashAccName = cashAccName;
	}

	public double getSellMoney() {
		return SellMoney;
	}

	public void setSellMoney(double sellMoney) {
		SellMoney = sellMoney;
	}

	public java.util.Date getTradeDate() {
		return TradeDate;
	}

	public void setTradeDate(java.util.Date tradeDate) {
		TradeDate = tradeDate;
	}

	public java.util.Date getSetDate() {
		return SetDate;
	}

	public void setSetDate(java.util.Date setDate) {
		SetDate = setDate;
	}

	public java.util.Date getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(java.util.Date settleDate) {
		SettleDate = settleDate;
	}

	public String getDataType() {
		return DataType;
	}

	public void setDataType(String dataType) {
		DataType = dataType;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}
	
	public String getOldNum() {
		return OldNum;
	}

	public void setOldNum(String oldNum) {
		this.OldNum = oldNum;
	}

	public String getOldPortCode() {
		return OldPortCode;
	}

	public void setOldPortCode(String oldPortCode) {
		OldPortCode = oldPortCode;
	}

	public String getOldPortName() {
		return OldPortName;
	}

	public void setOldPortName(String oldPortName) {
		OldPortName = oldPortName;
	}

	public String getOldSellType() {
		return OldSellType;
	}

	public void setOldSellType(String oldSellType) {
		OldSellType = oldSellType;
	}

	public String getOldSellTypeName() {
		return OldSellTypeName;
	}

	public void setOldSellTypeName(String oldSellTypeName) {
		OldSellTypeName = oldSellTypeName;
	}

	public String getOldCashAccCode() {
		return OldCashAccCode;
	}

	public void setOldCashAccCode(String oldCashAccCode) {
		OldCashAccCode = oldCashAccCode;
	}

	public String getOldCashAccName() {
		return OldCashAccName;
	}

	public void setOldCashAccName(String oldCashAccName) {
		OldCashAccName = oldCashAccName;
	}

	public double getOldSellMoney() {
		return OldSellMoney;
	}

	public void setOldSellMoney(double oldSellMoney) {
		OldSellMoney = oldSellMoney;
	}

	public java.util.Date getOldTradeDate() {
		return OldTradeDate;
	}

	public void setOldTradeDate(java.util.Date oldTradeDate) {
		OldTradeDate = oldTradeDate;
	}

	public java.util.Date getOldSetDate() {
		return OldSetDate;
	}

	public void setOldSetDate(java.util.Date oldSetDate) {
		OldSetDate = oldSetDate;
	}

	public java.util.Date getOldSettleDate() {
		return OldSettleDate;
	}

	public void setOldSettleDate(java.util.Date oldSettleDate) {
		OldSettleDate = oldSettleDate;
	}

	public String getOldDataType() {
		return OldDataType;
	}

	public void setOldDataType(String oldDataType) {
		OldDataType = oldDataType;
	}

	public String getOldDesc() {
		return OldDesc;
	}

	public void setOldDesc(String oldDesc) {
		OldDesc = oldDesc;
	}

	public boolean isShow() {
		return Show;
	}

	public void setShow(boolean show) {
		Show = show;
	}
	
	
	private TABean taBean=null;
	private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	//新增一条数据

	public String addSetting() throws YssException {
		String strSql="";
		boolean bTrans=false;//代表事务是否开始
		String nowDate="";
		Connection conn = dbl.loadConnection();
		try {
            conn.setAutoCommit(false);
            bTrans = true;
            //nowDate=YssFun.formatDate(new java.util.Date());
            nowDate=YssFun.formatDate(new java.util.Date(),
                    YssCons.YSS_DATETIMEFORMAT).
                    substring(0, 8);
            this.Num = "T" + nowDate +"00000"+
            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_DivineTa"),
                                   dbl.sqlRight("FNum", 6), "000001",
                                   " where FNum like 'T"
                                   + nowDate + "%'", 1);
            strSql="insert into "+pub.yssGetTableName("Tb_Data_DivineTa")
            	+ " (FNUM,FPORTCODE,FPORTCLSCODE,FSELLTYPE,FCASHACCCODE,FSELLMONEY,FTRADEDATE,FSETDATE,FSETTLEDATE,FDATATYPE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME)"
            	//Modified by zhaoxianlin 20130129 STORY #2913 这里增加分级组合字段
            	+" values("
            	+dbl.sqlString(this.Num)+ ","
            	+dbl.sqlString(this.PortCode)+ ","
            	+dbl.sqlString(this.PortClsCode)+ ","//add by zhaoxianlin 20130129 STORY #2913 这里增加分级组合字段
            	+dbl.sqlString(this.SellType)+ ","
            	+dbl.sqlString(this.CashAccCode)+ ","
            	+this.SellMoney+ ","
            	+dbl.sqlDate(this.TradeDate)+ ","
            	+dbl.sqlDate(this.SetDate)+ ","
            	+dbl.sqlDate(this.SettleDate)+ ","
            	+dbl.sqlString(this.DataType)+ ","
            	+dbl.sqlString(this.Desc)+ ","
            	+dbl.sqlString("0")+ ","
            	+dbl.sqlString(this.creatorCode)+ ","
            	+dbl.sqlString(this.creatorTime)+")"
            	;
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        return "";        
	            
            
        }
		catch (Exception e) {
            throw new YssException("新增TA数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	/*
	 * 检查是否有主键冲突
	 */
	public void checkInput(byte btOper) throws YssException {
		;

	}
	/* 
	 * 审核操作
	 */
	public void checkSetting() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try
		{
			conn.setAutoCommit(false); 
			bTrans = true; 
			if (sRecycled != null && (!sRecycled.equalsIgnoreCase("")))// 判断传来的内容是否为空
			{
				arrData = sRecycled.split("\r\n"); 
				for(int i=0;i<arrData.length;i++)
				{
					if(arrData[i].length()==0)
					{
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql="update " +pub.yssGetTableName("TB_DATA_DIVINETA")+
							" set fcheckstate = case fcheckstate when 0 then 1 else 0 end"+
							",fcheckuser = "+
							dbl.sqlString(pub.getUserCode())+
							", FCheckTime = '"+ 
							YssFun.formatDatetime(new java.util.Date()) + "'" +
							" where FNum = " + dbl.sqlString(this.Num)
							;
					dbl.executeSql(strSql); 
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		}
		catch (Exception e) {
			throw new YssException("审核TA数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}

	}
	//删除操作,将数据放到回收站
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try
        {
        	 conn.setAutoCommit(false);
             bTrans = true;
             strSql="update " + pub.yssGetTableName("TB_DATA_DIVINETA")
             + " set FCheckState = 2 " 
             + ", FCheckUser = null " 
             + ", FCheckTime = null "
             +" where FNum = " + dbl.sqlString(this.Num);
             
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("删除TA数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

	}
	//清空回收站数据
	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        try
        {
        	// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                // 根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                // 循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql = "delete from "
							+ pub.yssGetTableName("TB_DATA_DIVINETA")
							+ " where FNum = " + dbl.sqlString(this.Num);
					// 执行sql语句
					dbl.executeSql(strSql);
				}
            }
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        	
        }
        catch (Exception e) {
            throw new YssException("清除TA数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }	

	}
	//修改数据
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try
        {
        	strSql = "update " + pub.yssGetTableName("Tb_Data_DivineTA") 
        	+" set FPORTCODE = " + dbl.sqlString(this.PortCode.length() > 0 ? this.PortCode : " ")+","
        	+" FPORTCLSCODE = " + dbl.sqlString(this.PortClsCode.length() > 0 ? this.PortClsCode : " ")+","
        	//modified by zhaoxianlin  20130129 STORY #2913 这里增加FPORTCLSCODE字段
        	+" FSELLTYPE = "+dbl.sqlString(this.SellType)+","
        	+" FCASHACCCODE = "+dbl.sqlString(this.CashAccCode)+","
        	+" FSELLMONEY = "+this.SellMoney+","
        	+" FTRADEDATE = "+dbl.sqlDate(this.TradeDate)+","
        	+" FSETDATE = "+dbl.sqlDate(this.SetDate)+","
        	+" FSETTLEDATE = "+dbl.sqlDate(this.SettleDate)+","
        	+" FDATATYPE = "+dbl.sqlString(this.DataType)+","
        	+" FDESC = "+dbl.sqlString(this.Desc)+","
        	+ " fcreator = " + dbl.sqlString(this.creatorCode)
            + " , fcreatetime = " + dbl.sqlString(this.creatorTime)
            +" where FNum = " +
            dbl.sqlString(this.OldNum) 
            ;
        	dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        }
        catch (Exception e) {
            throw new YssException("修改TA数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String mutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}
	/*
	 * 拼接字符串
	 */
	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
		buf.append(this.Num).append("\t");
		buf.append(this.PortCode).append("\t");
		buf.append(this.PortName).append("\t");
	    buf.append(this.PortClsCode).append("\t");//add by zhaoxianlin  STORY #2913 
		buf.append(this.PortClsName).append("\t");//add by zhaoxianlin  STORY #2913 
		buf.append(this.SellType).append("\t");
		buf.append(this.SellTypeName).append("\t");
		buf.append(this.CashAccCode).append("\t");
		buf.append(this.CashAccName).append("\t");
		buf.append(this.SellMoney).append("\t");
		buf.append(format.format(this.TradeDate)).append("\t");
		buf.append(format.format(this.SetDate)).append("\t");
		buf.append(format.format(this.SettleDate)).append("\t");
		buf.append(this.DataType).append("\t");
		buf.append(this.Desc).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();
	}


	/*
	 * 解析前台字符串
	 */
	public void parseRowStr(String rowStr) throws YssException {
		if (taBean == null) {
			taBean = new TABean();
			taBean.setYssPub(pub);
			}
		String reqAry[] = null;
        String sTmpStr = "";
        String sMutiAudit = ""; //批量处理的数据
        try
        {
        	if (rowStr.trim().length() == 0) {
                return;
            }
        	if (rowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = rowStr.split("\f\n\f\n\f\n")[1];  //得到的是从前台传来需要审核与反审核的批量数据
                multAuditString = sMutiAudit;                   //保存在全局变量中
                rowStr = rowStr.split("\f\n\f\n\f\n")[0];     //前台传来的要更新的一些数据
            }
        	if (rowStr.indexOf("\r\t") >= 0) {
                sTmpStr = rowStr.split("\r\t")[0];
            } else {
                sTmpStr = rowStr;
            }
        	sRecycled = rowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.Num=reqAry[0];
            this.PortCode=reqAry[1];
            this.PortName=reqAry[2];
            this.SellType=reqAry[3];
            this.SellTypeName=reqAry[4];
            this.CashAccCode=reqAry[5];
            this.CashAccName=reqAry[6];
            this.SellMoney=Double.parseDouble(reqAry[7]);
            this.TradeDate=YssFun.parseDate(reqAry[8].trim().length()==0?"9998-12-31":reqAry[8]);
            this.SetDate=YssFun.parseDate(reqAry[9].trim().length()==0?"9998-12-31":reqAry[9]);
            this.SettleDate=YssFun.parseDate(reqAry[10].trim().length()==0?"9998-12-31":reqAry[10]);
            this.DataType=reqAry[11];
            this.Desc=reqAry[12];
            this.PortClsCode = reqAry[13];//add by zhaoxianlin  STORY #2913 
            
            this.OldNum=reqAry[14];
            this.OldPortCode=reqAry[15];
            this.OldPortName=reqAry[16];
            this.OldSellType=reqAry[17];
            this.OldSellTypeName=reqAry[18];
            this.OldCashAccCode=reqAry[19];
            this.OldCashAccName=reqAry[20];
            this.OldSellMoney=Double.parseDouble(reqAry[21]);
            this.OldTradeDate=YssFun.parseDate(reqAry[22].trim().length()==0?"9998-12-31":reqAry[22]);
            this.OldSetDate=YssFun.parseDate(reqAry[23].trim().length()==0?"9998-12-31":reqAry[23]);
            this.OldSettleDate=YssFun.parseDate(reqAry[24].trim().length()==0?"9998-12-31":reqAry[24]);
            this.OldDataType=reqAry[25];
            this.OldDesc=reqAry[26];
            
            this.checkStateId = YssFun.toInt(reqAry[27]);
            if (reqAry[28].equalsIgnoreCase("true")) {
                this.Show = true;
            } else {
                this.Show = false;
            } 
            super.parseRecLog();
            if (rowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new TABean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(rowStr.split("\r\t")[1]);    
            }
            
        }
        catch (Exception e) {
            throw new YssException("解析TA数据出错！", e);
        }

	}

	public String getListViewData1() throws YssException {
		String strSql = ""; // 定义一个存放sql语句的字符串
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try
        {
        	sHeader = this.getListView1Headers();
        	strSql="select a.fnum as Fnum," +
        			"a.fportcode as Fportcode," +
	                "a.fportClscode as FPortClsCode," + //add by zhaoxianlin  20130129 STORY #2913
        			" g.FPortClsCode as FPortClsName," +//add by zhaoxianlin  20130129 STORY #2913
        			"b.fportname as Fportname," +
        			"a.fselltype as Fselltype," +
        			"c.fselltypename as Fselltypename, " +
        			"a.fcashacccode as Fcashacccode ," +
        			"f.fcashaccname as Fcashaccname," +
        			"a.fsellmoney as Fsellmoney," +
        			"a.ftradedate as Ftradedate," +
        			"a.fsetdate as Fsetdate," +
        			"a.fsettledate as Fsettledate," +
        			"a.fdatatype as Fdatatype," +
        			"a.fdesc as Fdesc," +
        			"a.FCheckState as FCheckState, " +
        			"a.fcreator as FCreator,"+
        			"d.FUserName as FCreatorName, " +
        			"a.fcreatetime as FCreateTime, " +
        			"a.fcheckuser as FCheckUser,"+
        			"e.fusername as FCheckUserName,"
        		+ "a.fchecktime as FCheckTime from "
        		+ pub.yssGetTableName("TB_DATA_DIVINETA")+
        		//
        		" a left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据
//        		+pub.yssGetTableName("tb_Para_Portfolio")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
        		+" select FSTARTDATE, fportcode, FPORTNAME from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
        		+pub.yssGetTableName("tb_Para_Portfolio")+
        		" where fcheckstate=1 "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
        		+") b "
        		+"on a.fportcode = b.fportcode "
        		+ " left join (select * from " + pub.yssGetTableName("Tb_ta_SellType") + ") c on a.fselltype = c.fselltypecode "
        		+ 
        		//
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
//                pub.yssGetTableName("Tb_Para_CashAccount")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
                " select fstartdate,fcashacccode,fcashaccname from "+//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where fcheckstate=1 "+//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                ") f on a.FCashAccCode = f.FCashAccCode" 
                //
        		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) d " 
                + " on a.FCreator = d.FUserCode "
        		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) e " 
                + " on a.FCheckUser = e.FUserCode " 
                /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/  
                +" left join (select FPortClsCode, FPortClsName from "+ pub.yssGetTableName("Tb_TA_PortCls") 
                +" where fcheckstate = 1) g on a.fportclscode = g.FPortClsCode "
                /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--end-*/  
        		+ " where " + buildFilterStr("a")
        		+ " order by a.FCheckState, a.FCreateTime desc";
        		;
        		rs = dbl.openResultSet(strSql);
                while(rs.next()){
                	//edit by yanghaiming 20100819
                	//bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                	bufShow.append(rs.getString("FNUM")).append("\t");
                	bufShow.append(rs.getString("Fportcode")).append("\t");
                	bufShow.append(rs.getString("Fportname")).append("\t");
                	//add by zhaoxianlin  20130129 STORY #2913--start
                	bufShow.append(rs.getString("FportClscode")).append("\t");
                	bufShow.append(rs.getString("FportClsname")).append("\t");
                	//add by zhaoxianlin  20130129 STORY #2913--end
                	bufShow.append(rs.getString("Fselltype")).append("\t");
                	bufShow.append(rs.getString("Fselltypename")).append("\t");
                	bufShow.append(rs.getString("Fcashacccode")).append("\t");
                	bufShow.append(rs.getString("Fcashaccname")).append("\t");
                	bufShow.append(YssFun.formatNumber(rs.getDouble("Fsellmoney"),"#,##0.##")).append("\t");
                	bufShow.append(rs.getString("Ftradedate")).append("\t");
                	bufShow.append(rs.getString("Fsetdate")).append("\t");
                	bufShow.append(rs.getString("Fsettledate")).append("\t");
                	bufShow.append(rs.getString("Fdatatype").equalsIgnoreCase("0") ? "预估数据" : "确认数据").append("\t");
                	bufShow.append(rs.getString("Fdesc")).append("\t");
                	bufShow.append(rs.getString("FCreator")).append("\t");
                	bufShow.append(rs.getString("FCreateTime")).append("\t");
                	bufShow.append(rs.getString("FCheckUser")).append("\t");
                	bufShow.append(rs.getString("FCheckTime"));
                	
                    bufShow.append(YssCons.YSS_LINESPLITMARK);
                	
                	this.setResultSetAttr(rs);
                	
                	bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
                
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }
                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }
                
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
                + "\r\f" + this.getListView1ShowCols();
        }
        catch(Exception e){
        	throw new YssException("获取TA数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
		
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * 生成筛选条件子句
	 */
	public String buildFilterStr(String prefix) throws YssException 
	{
		String str="";
    	
    	try {
 			ArrayList alCon=new ArrayList();
			
			alCon.add(" 1=1 ");
			
    		if(this.FilterType!=null)
    		{
    			TABean filter = this.FilterType;
    			
    			if (filter.isShow() == false) {
    				alCon.add(" 1=2 ");
                }
    			
    			if(prefix==null)
    			{
    				prefix="";
    			}
    			else if(!prefix.trim().endsWith("."))
    			{
    				prefix+=".";
    			}
    			
    			if(!YssFun.formatDate(filter.getTradeDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FTRADEDATE = "+dbl.sqlDate(filter.getTradeDate()));
    			}
    			if(!YssFun.formatDate(filter.getSetDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FSETDATE = "+dbl.sqlDate(filter.getSetDate()));
    			}
    			if(!YssFun.formatDate(filter.getSettleDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FSETTLEDATE = "+dbl.sqlDate(filter.getSettleDate()));
    			}
    			
    			
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FPORTCODE in ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 		
                /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/
    			if(filter.getPortClsCode()!=null&&filter.getPortClsCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FPORTCLSCODE in ("+dbl.sqlString(filter.getPortClsCode().trim())+")");
    			} 
    			 /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--END-*/
    			if(filter.getCashAccCode() !=null && filter.getCashAccCode().trim().length()>0)
    			{
    				alCon.add(prefix+"FCASHACCCODE = "+dbl.sqlString(filter.getCashAccCode().trim()));
    			}    
    			
    			if(filter.getSellType() != null && filter.getSellType().trim().length()>0)
    			{
    				alCon.add(prefix+"FSELLTYPE = "+dbl.sqlString(filter.getSellType().trim()));
    			}
    			if(filter.getDataType() != null && filter.getDataType() .trim().length()>0)
    			{	
    				if(!filter.getDataType().equalsIgnoreCase("ALL"))
    				{
    					alCon.add(prefix+"FDATATYPE = "+dbl.sqlString(filter.getDataType().trim()));
    				}
    			}
    			if(filter.getDesc()!= null&& filter.getDesc().trim().length()>0)
    			{
    				alCon.add(prefix+"FDESC = "+dbl.sqlString(filter.getDesc().trim()));
    			}
    			
    			if(filter.getSellMoney() > 0 )
    			{
    				alCon.add(prefix+"FSELLMONEY = " + filter.getSellMoney());
    			}
    		}
    		
			str=YssFun.join((String[])alCon.toArray(new String[]{}), " and ");
        }
        catch(Exception e){
        	throw new YssException("生成筛选条件子句出错！", e);
        }
        
        return str;
	}
	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
		this.Num=rs.getString("Fnum");
    	this.PortCode = rs.getString("FPortCode");
        this.PortName = rs.getString("FPortName");
        this.SellType=rs.getString("FSellType");
        this.SellTypeName=rs.getString("FSellTypeName");
        this.CashAccCode=rs.getString("FCashAccCode");
        this.CashAccName=rs.getString("FCashAccName");
        this.SellMoney=rs.getDouble("FSellMoney");
        this.TradeDate=rs.getDate("FTradeDate");
        this.SetDate=rs.getDate("FSetDate");
        this.SettleDate=rs.getDate("FSettleDate");
        this.DataType=rs.getString("FDataType");
        this.Desc=rs.getString("Fdesc");
        this.PortClsCode = rs.getString("FPortClsCode");//add by zhaoxianlin  20130129 STORY #2913
        this.PortClsName = rs.getString("FPortClsName");//add by zhaoxianlin  20130129 STORY #2913
        super.setRecLog(rs);
    }
	public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try {
            //批量审核/反审核/删除
            if (sType.equalsIgnoreCase("multauditTASub")) { //判断是否要进行批量审核与反审核
                if (multAuditString.length() > 0) { //判断批量审核与反审核的内容是否为空
                    return this.auditMutli(this.multAuditString); //执行批量审核/反审核
                }
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }
	
	public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null; //建立一个数据库连接
        String sqlStr = ""; //创建一个字符串
        PreparedStatement psmt1 = null;
        boolean bTrans = true; //建一个boolean变量，默认自动回滚
        TABean tA = null; //创建一个pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); //和数据库进行连接
            //审核、反审核、删除汇率数据
            sqlStr = "update " + pub.yssGetTableName("TB_DATA_DivineTA") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = ? " ;
        	     //更新数据库审核与未审核的SQL语句
            
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句


            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                    	tA = new TABean(); //new 一个pojo类
                    	tA.setYssPub(pub); //设置一些基础信息
                    	tA.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                    	psmt1.setString(1,tA.Num);
                        psmt1.addBatch(); 
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核TA数据出错!");
        } finally
        {
        	dbl.closeStatementFinal(psmt1);
        }
        return "";
    }

}
