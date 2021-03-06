package com.yss.main.operdata.moneycontrol;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/*
 * Title:EstimateTradeBean
 * Description:预估交易数据
 * @author yangheng
 * @data 20100805
 */
public class EstimateTradeBean extends BaseDataSettingBean implements
		IDataSetting {
	
	private EstimateTradeBean FilterType=null;
	private String sRecycled = ""; //保存未解析前的字符串
	private String Num="" ;//编号
	private String PortCode="";//组合代码
	private String PortName="";//组合名称
	private String SecurityCode="";//证券代码
	private String SecurityName="";//证券名称
	private String TradeType=""; //交易方式
	private String TradeTypeName="";//交易名称
	private String CashAccCode="";//现金账户
	private String CashAccName="";//现金账户名称
	private String ExchangeCode="";//交易所代码
	private String ExchangeName="";//交易所名称
	private Date TradeDate=null;//成交日期
	private Date SettleDate=null;//结算日期
	private double YClosePrice;//前一日成交价
	private String CommisionRateType="";//佣金费率类型
	private BigDecimal FloatValue=null;//浮动值
	private BigDecimal CommisionRate=null;//佣金费率
	private BigDecimal Amount=null;//交易股数
	private BigDecimal CommisionMoney=null;//佣金金额
	private BigDecimal EstimateMoney=null;//预估金额
	private String Desc="";//描述
	
	private String OldNum="" ;//修改前的编号
	private String OldPortCode="";//修改前的组合代码
	private String OldPortName="";//修改前的组合名称
	private String OldSecurityCode="";//修改前的证券代码
	private String OldSecurityName="";//修改前的证券名称
	private String OldTradeType=""; //修改前的交易方式
	private String OldTradeTypeName="";//修改前的交易名称
	private String OldCashAccCode="";//修改前的现金账户
	private String OldCashAccName="";//修改前的现金账户名称
	private String OldExchangeCode="";//修改前的交易所代码
	private String OldExchangeName="";//修改前的交易所名称
	private Date OldTradeDate=null;//修改前的成交日期
	private Date OldSettleDate=null;//修改前的结算日期
	private double OldYClosePrice;//修改前的前一日成交价
	private String OldCommisionRateType="";//修改前的佣金费率类型
	private BigDecimal OldFloatValue=null;//修改前的浮动值
	private BigDecimal OldCommisionRate=null;//修改前的佣金费率
	private BigDecimal OldAmount=null;//修改前的交易股数
	private BigDecimal OldCommisionMoney=null;//修改前的佣金金额
	private BigDecimal OldEstimateMoney=null;//修改前的预估金额
	private String OldDesc="";//修改前的描述
	
	private String multAuditString = ""; //批量处理数据
	private boolean Show=false;
	
	public EstimateTradeBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(EstimateTradeBean filterType) {
		FilterType = filterType;
	}

	public String getSRecycled() {
		return sRecycled;
	}

	public void setSRecycled(String recycled) {
		sRecycled = recycled;
	}

	public String getNum() {
		return Num;
	}

	public void setNum(String num) {
		Num = num;
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

	public String getSecurityCode() {
		return SecurityCode;
	}

	public void setSecurityCode(String securityCode) {
		SecurityCode = securityCode;
	}

	public String getSecurityName() {
		return SecurityName;
	}

	public void setSecurityName(String securityName) {
		SecurityName = securityName;
	}

	public String getTradeType() {
		return TradeType;
	}

	public void setTradeType(String tradeType) {
		TradeType = tradeType;
	}

	public String getTradeTypeName() {
		return TradeTypeName;
	}

	public void setTradeTypeName(String tradeTypeName) {
		TradeTypeName = tradeTypeName;
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

	public String getExchangeCode() {
		return ExchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		ExchangeCode = exchangeCode;
	}

	public String getExchangeName() {
		return ExchangeName;
	}

	public void setExchangeName(String exchangeName) {
		ExchangeName = exchangeName;
	}

	public Date getTradeDate() {
		return TradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		TradeDate = tradeDate;
	}

	public Date getSettleDate() {
		return SettleDate;
	}

	public void setSettleDate(Date settleDate) {
		SettleDate = settleDate;
	}

	public double getYClosePrice() {
		return YClosePrice;
	}

	public void setYClosePrice(double closePrice) {
		YClosePrice = closePrice;
	}

	public String getCommisionRateType() {
		return CommisionRateType;
	}

	public void setCommisionRateType(String commisionRateType) {
		CommisionRateType = commisionRateType;
	}

	public BigDecimal getFloatValue() {
		return FloatValue;
	}

	public void setFloatValue(BigDecimal floatValue) {
		FloatValue = floatValue;
	}

	public BigDecimal getCommisionRate() {
		return CommisionRate;
	}

	public void setCommisionRate(BigDecimal commisionRate) {
		CommisionRate = commisionRate;
	}

	public BigDecimal getAmount() {
		return Amount;
	}

	public void setAmount(BigDecimal amount) {
		Amount = amount;
	}

	public BigDecimal getCommisionMoney() {
		return CommisionMoney;
	}

	public void setCommisionMoney(BigDecimal commisionMoney) {
		CommisionMoney = commisionMoney;
	}

	public BigDecimal getEstimateMoney() {
		return EstimateMoney;
	}

	public void setEstimateMoney(BigDecimal estimateMoney) {
		EstimateMoney = estimateMoney;
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
		OldNum = oldNum;
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

	public String getOldSecurityCode() {
		return OldSecurityCode;
	}

	public void setOldSecurityCode(String oldSecurityCode) {
		OldSecurityCode = oldSecurityCode;
	}

	public String getOldSecurityName() {
		return OldSecurityName;
	}

	public void setOldSecurityName(String oldSecurityName) {
		OldSecurityName = oldSecurityName;
	}

	public String getOldTradeType() {
		return OldTradeType;
	}

	public void setOldTradeType(String oldTradeType) {
		OldTradeType = oldTradeType;
	}

	public String getOldTradeTypeName() {
		return OldTradeTypeName;
	}

	public void setOldTradeTypeName(String oldTradeTypeName) {
		OldTradeTypeName = oldTradeTypeName;
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

	public String getOldExchangeCode() {
		return OldExchangeCode;
	}

	public void setOldExchangeCode(String oldExchangeCode) {
		OldExchangeCode = oldExchangeCode;
	}

	public String getOldExchangeName() {
		return OldExchangeName;
	}

	public void setOldExchangeName(String oldExchangeName) {
		OldExchangeName = oldExchangeName;
	}

	public Date getOldTradeDate() {
		return OldTradeDate;
	}

	public void setOldTradeDate(Date oldTradeDate) {
		OldTradeDate = oldTradeDate;
	}

	public Date getOldSettleDate() {
		return OldSettleDate;
	}

	public void setOldSettleDate(Date oldSettleDate) {
		OldSettleDate = oldSettleDate;
	}

	public double getOldYClosePrice() {
		return OldYClosePrice;
	}

	public void setOldYClosePrice(double oldYClosePrice) {
		OldYClosePrice = oldYClosePrice;
	}

	public String getOldCommisionRateType() {
		return OldCommisionRateType;
	}

	public void setOldCommisionRateType(String oldCommisionRateType) {
		OldCommisionRateType = oldCommisionRateType;
	}

	public BigDecimal getOldFloatValue() {
		return OldFloatValue;
	}

	public void setOldFloatValue(BigDecimal oldFloatValue) {
		OldFloatValue = oldFloatValue;
	}

	public BigDecimal getOldCommisionRate() {
		return OldCommisionRate;
	}

	public void setOldCommisionRate(BigDecimal oldCommisionRate) {
		OldCommisionRate = oldCommisionRate;
	}

	public BigDecimal getOldAmount() {
		return OldAmount;
	}

	public void setOldAmount(BigDecimal oldAmount) {
		OldAmount = oldAmount;
	}

	public BigDecimal getOldCommisionMoney() {
		return OldCommisionMoney;
	}

	public void setOldCommisionMoney(BigDecimal oldCommisionMoney) {
		OldCommisionMoney = oldCommisionMoney;
	}

	public BigDecimal getOldEstimateMoney() {
		return OldEstimateMoney;
	}

	public void setOldEstimateMoney(BigDecimal oldEstimateMoney) {
		OldEstimateMoney = oldEstimateMoney;
	}

	public String getOldDesc() {
		return OldDesc;
	}

	public void setOldDesc(String oldDesc) {
		OldDesc = oldDesc;
	}

	public String getMultAuditString() {
		return multAuditString;
	}

	public void setMultAuditString(String multAuditString) {
		this.multAuditString = multAuditString;
	}

	public boolean isShow() {
		return Show;
	}

	public void setShow(boolean show) {
		Show = show;
	}

	private EstimateTradeBean estimateBean=null;
	private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	//添加一条数据
	public String addSetting() throws YssException {
		String strSql="";
		boolean bTrans=false;//代表事务是否开始
		String nowDate="";
		Connection conn = dbl.loadConnection();
		try
		{	conn.setAutoCommit(false);
        	bTrans = true;
        	nowDate=YssFun.formatDate(new java.util.Date(),
                    YssCons.YSS_DATETIMEFORMAT).
                    substring(0, 8);
        	this.Num = "T" + nowDate +"00000"+
            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_DivineEstimate"),
                                   dbl.sqlRight("FNum", 6), "000001",
                                   " where FNum like 'T"
                                   + nowDate + "%'", 1);
        	strSql="insert into "+pub.yssGetTableName("Tb_Data_DivineEstimate")
        	+ " (FNUM,FPORTCODE,FSECURITYCODE,FTRADETYPECODE,FCASHACCCODE,FAMOUNT,FYCLOSEPRICE,FFLOATVALUE,FCOMMISIONRATETYPE,FCOMMISIONRATE,FCOMMISIONMONEY,FESTIMATEMONEY,FEXCHANGECODE,FTRADEDATE,FSETTLEDATE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME)"
        	+" values("
        	+dbl.sqlString(this.Num)+ ","
        	+dbl.sqlString(this.PortCode)+ ","
        	+dbl.sqlString(this.SecurityCode)+ ","
        	+dbl.sqlString(this.TradeType)+ ","
        	+dbl.sqlString(this.CashAccCode)+ ","
        	+this.Amount+ ","
        	+this.YClosePrice+ ","
        	+this.FloatValue+ ","
        	+dbl.sqlString(this.CommisionRateType)+ ","
        	+this.CommisionRate+ ","
        	+this.CommisionMoney+ ","
        	+this.EstimateMoney+ ","
        	+dbl.sqlString(this.ExchangeCode)+ ","
        	+dbl.sqlDate(this.TradeDate)+ ","
        	+dbl.sqlDate(this.SettleDate)+ ","
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
            throw new YssException("新增预估交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}

	public void checkInput(byte btOper) throws YssException {
		;

	}
	/*
	 * 审核与反审核操作
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
					strSql="update " +pub.yssGetTableName("Tb_Data_DivineEstimate")+
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
			throw new YssException("审核预估交易数据出错", e);
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
             strSql="update " + pub.yssGetTableName("Tb_Data_DivineEstimate")
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
            throw new YssException("删除预估交易数据出错", e);
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
							+ pub.yssGetTableName("Tb_Data_DivineEstimate")
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
            throw new YssException("清除预估交易数据出错", e);
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
        	strSql = "update " + pub.yssGetTableName("Tb_Data_DivineEstimate") 
        	+" set FPORTCODE = " + dbl.sqlString(this.PortCode)+","
        	+" FSECURITYCODE = "+dbl.sqlString(this.SecurityCode)+","
        	+" FTRADETYPECODE = "+dbl.sqlString(this.TradeType)+","
        	+" FCASHACCCODE = "+dbl.sqlString(this.CashAccCode)+","
        	+" FAMOUNT = "+this.Amount+","
        	+" FYCLOSEPRICE = "+this.YClosePrice+","
        	+" FFLOATVALUE = "+this.FloatValue+","
        	+" FCOMMISIONRATETYPE = "+dbl.sqlString(this.CommisionRateType)+","
        	+" FCOMMISIONRATE = "+this.CommisionRate+","
        	+" FCOMMISIONMONEY = "+this.CommisionMoney+","
        	+" FESTIMATEMONEY = "+this.EstimateMoney+","
        	+" FEXCHANGECODE = "+dbl.sqlString(this.ExchangeCode)+","
        	+" FTRADEDATE = "+dbl.sqlDate(this.TradeDate)+","
        	+" FSETTLEDATE = "+dbl.sqlDate(this.SettleDate)+","
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
            throw new YssException("修改预估交易数据出错", e);
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
		buf.append(this.SecurityCode).append("\t");
		buf.append(this.SecurityName).append("\t");
		buf.append(this.TradeType).append("\t");
		buf.append(this.TradeTypeName).append("\t");
		buf.append(this.CashAccCode).append("\t");
		buf.append(this.CashAccName).append("\t");
		buf.append(this.ExchangeCode).append("\t");
		buf.append(this.ExchangeName).append("\t");
		buf.append(format.format(this.TradeDate)).append("\t");
		buf.append(format.format(this.SettleDate)).append("\t");
		buf.append(this.YClosePrice).append("\t");
		buf.append(this.CommisionRateType).append("\t");
		buf.append(this.FloatValue).append("\t");
		buf.append(this.CommisionRate).append("\t");
		buf.append(this.Amount).append("\t");
		buf.append(this.CommisionMoney).append("\t");
		buf.append(this.EstimateMoney).append("\t");
		buf.append(this.Desc).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();

	}

	
	/*
	 * 解析字符串
	 */
	public void parseRowStr(String rowStr) throws YssException {
		
			String reqAry[] = null;
	        String sTmpStr = "";
	        String sMutiAudit = ""; //批量处理的数据
			if (estimateBean == null) 
			{
			estimateBean = new EstimateTradeBean();
			estimateBean.setYssPub(pub);
			}
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
	            } 
				else {
	                sTmpStr = rowStr;
	            }
				sRecycled = rowStr; //把未解析的字符串先赋给sRecycled
				reqAry = sTmpStr.split("\t");
				this.Num=reqAry[0];
	            this.PortCode=reqAry[1];
	            this.PortName=reqAry[2];
	            this.SecurityCode=reqAry[3];
	            this.SecurityName=reqAry[4];
	            this.TradeType=reqAry[5];
	            this.TradeTypeName=reqAry[6];
	            this.CashAccCode=reqAry[7];
	            this.CashAccName=reqAry[8];
	            this.ExchangeCode=reqAry[9];
	            this.ExchangeName=reqAry[10];
	            this.TradeDate=YssFun.parseDate(reqAry[11].trim().length()==0?"9998-12-31":reqAry[11]);
	            this.SettleDate=YssFun.parseDate(reqAry[12].trim().length()==0?"9998-12-31":reqAry[12]);
	            this.YClosePrice=Double.parseDouble(reqAry[13].length()==0?"0":reqAry[13]);
	            this.CommisionRateType=reqAry[14];
	            this.FloatValue=new BigDecimal(reqAry[15].length()==0?"0":reqAry[15]);
	            this.CommisionRate=new BigDecimal(reqAry[16].length()==0?"0":reqAry[16]);
	            this.Amount=new BigDecimal(reqAry[17].length()==0?"0":reqAry[17]);
	            this.CommisionMoney=new BigDecimal(reqAry[18].length()==0?"0":reqAry[18]);
	            this.EstimateMoney=new BigDecimal(reqAry[19].length()==0?"0":reqAry[19]);
	            this.Desc=reqAry[20];
	            
	            this.OldNum=reqAry[21];
	            ;
	            this.checkStateId = YssFun.toInt(reqAry[22]);
	            if (reqAry[23].equalsIgnoreCase("true")) {
	                this.Show = true;
	            } else {
	                this.Show = false;
	            } 
	            super.parseRecLog();
	            if (rowStr.indexOf("\r\t") >= 0) {
	                if (this.FilterType == null) {
	                    this.FilterType = new EstimateTradeBean();
	                    this.FilterType.setYssPub(pub);
	                }
	                this.FilterType.parseRowStr(rowStr.split("\r\t")[1]);    
	            }
				
			}
			catch (Exception e) {
	            throw new YssException("解析预估交易数据出错！", e);
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
        {	;
        	sHeader = this.getListView1Headers();
        	strSql="select a.fnum as Fnum," +
			"a.fportcode as Fportcode," +
			"b.fportname as Fportname," +
			"a.fsecuritycode as Fsecuritycode,"+
			"c.fsecurityname as Fsecurityname,"+
			"a.ftradetypecode as FtradeType, "+
			"d.ftradetypename as FtradeTypeName ,"+
			"a.fcashacccode as Fcashacccode ," +
			"e.fcashaccname as Fcashaccname," +
			"a.fexchangecode as Fexchangecode,"+
			"f.fexchangename as Fexchangename,"+
			"a.ftradedate as Ftradedate,"+
			"a.fsettledate as Fsettledate,"+
			"a.fycloseprice as Fycloseprice,"+
			"a.fcommisionRateType as FcommisionRateType,"+
			"a.ffloatvalue as Ffloatvalue,"+
			"a.fcommisionrate as Fcommisionrate,"+
			"a.famount as Famount,"+
			"a.fcommisionmoney as Fcommisionmoney,"+
			"a.festimatemoney as festimatemoney,"+
			"a.fdesc as Fdesc," +
			"a.FCheckState as FCheckState, " +
			"a.fcreator as FCreator,"+
			"o.FUserName as FCreatorName, " +
			"a.fcreatetime as FCreateTime, " +
			"a.fcheckuser as FCheckUser,"+
			"p.fusername as FCheckUserName,"
			+ "a.fchecktime as FCheckTime from "
		+ pub.yssGetTableName("Tb_Data_DivineEstimate")+
		//
		" a left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
//		+pub.yssGetTableName("tb_Para_Portfolio")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
		+" select FSTARTDATE, fportcode, FPORTNAME from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
		+pub.yssGetTableName("tb_Para_Portfolio")+
		" where fcheckstate=1 "//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
		+") b "
		+"on a.fportcode = b.fportcode "
		//" left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
        +" left join (select eb.*,case when ec.FFactRate is null then 0 else ec.FFactRate end FFactRate from (select FSecurityCode, max(FStartDate) as FStartDate from " + //合并中保版本
        pub.yssGetTableName("Tb_Para_Security") +
        " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
        " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, " +
        " FHandAmount,FFactor,FTradeCury from " +
        pub.yssGetTableName("Tb_Para_Security") +
        //") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
        ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate " +
        " left join(select * from " +
        pub.yssGetTableName("Tb_Para_Fixinterest") + " ) ec on ea.FSecurityCode = ec.FSecurityCode) c on a.FSecurityCode = c.FSecurityCode " 
		//
		+ " left join (select * from tb_base_tradetype where Fcheckstate=1)"+ "d on a.ftradetypecode = d.ftradetypecode "
		//
		+" left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
//        pub.yssGetTableName("Tb_Para_CashAccount")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据 
        " select fstartdate,fcashacccode,fcashaccname from "+//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
        pub.yssGetTableName("Tb_Para_CashAccount") +
        " where fcheckstate=1 "+//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
        ") e on a.FCashAccCode = e.FCashAccCode"  
		//
        +" left join (select fexchangecode,fexchangename from tb_base_exchange where fcheckstate=1)"+"f on a.fexchangecode = f.fexchangecode"
        //
        + " left join (select FUserCode,FUserName from Tb_Sys_UserList) o " 
        + " on a.FCreator = o.FUserCode "
		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) p " 
        + " on a.FCheckUser = p.FUserCode " 
		+ " where " + buildFilterStr("a")
		+ " order by a.FCheckState, a.FCreateTime desc";
		;
		rs = dbl.openResultSet(strSql);
        while(rs.next()){
        	bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
            append(YssCons.YSS_LINESPLITMARK);
        	
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
        	throw new YssException("获取预估交易数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
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
    			EstimateTradeBean filter = this.FilterType;
    			
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
    			//成交日期
    			if(!YssFun.formatDate(filter.getTradeDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FTRADEDATE = "+dbl.sqlDate(filter.getTradeDate()));
    			}
    			//结算日期
    			if(!YssFun.formatDate(filter.getSettleDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FSETTLEDATE = "+dbl.sqlDate(filter.getSettleDate()));
    			}
    			//组合代码
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FPORTCODE = ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 
    			//证券代码
    			if(filter.getSecurityCode()!=null&&filter.getSecurityCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FSECURITYCODE = ("+dbl.sqlString(filter.getSecurityCode().trim())+")");
    			}
    			//交易方式
    			if(filter.getTradeType()!=null&&filter.getTradeType().trim().length()!=0)
    			{
    				alCon.add(prefix+"FTRADETYPECODE = ("+dbl.sqlString(filter.getTradeType().trim())+")");
    			}
    			//现金账户代码
    			if(filter.getCashAccCode() !=null && filter.getCashAccCode().trim().length()>0)
    			{
    				alCon.add(prefix+"FCASHACCCODE = ("+dbl.sqlString(filter.getCashAccCode().trim())+")");
    			}
    			//交易所
    			if(filter.getExchangeCode()!=null&&filter.getExchangeCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FEXCHANGECODE = ("+dbl.sqlString(filter.getExchangeCode().trim())+")");
    			}
    			//佣金费率类型
    			if(filter.getCommisionRateType() != null && filter.getCommisionRateType().trim().length()>0)
    			{	
    				if(!filter.getCommisionRateType().equalsIgnoreCase("ALL"))
    				{
    					alCon.add(prefix+"FCOMMISIONRATETYPE = "+dbl.sqlString(filter.getCommisionRateType().trim()));
    				}
    			}
    			
    			//描述
    			if(filter.getDesc()!= null&& filter.getDesc().trim().length()>0)
    			{
    				alCon.add(prefix+"FDESC = "+dbl.sqlString(filter.getDesc().trim()));
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
        this.SecurityCode=rs.getString("FSecurityCode");
        this.SecurityName=rs.getString("FSecurityName");
        this.TradeType=rs.getString("FTradeType");
        this.TradeTypeName=rs.getString("FTradeTypeName");
        this.CashAccCode=rs.getString("FCashAccCode");
        this.CashAccName=rs.getString("FCashAccName");
        this.ExchangeCode=rs.getString("FExchangeCode");
        this.ExchangeName=rs.getString("FExchangeName");        
        this.TradeDate=rs.getDate("FTradeDate");
        this.SettleDate=rs.getDate("FSettleDate");
        this.YClosePrice=rs.getDouble("FYClosePrice");
        this.CommisionRateType=rs.getString("FCommisionRateType");
        this.CommisionRate=rs.getBigDecimal("FCommisionRate");
        this.CommisionMoney=rs.getBigDecimal("FCommisionMoney");
        this.FloatValue=rs.getBigDecimal("FFloatValue");
        this.Amount=rs.getBigDecimal("FAmount");
        this.EstimateMoney=rs.getBigDecimal("FEstimateMoney");
        this.Desc=rs.getString("Fdesc");
        super.setRecLog(rs);
    }
	public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try {
            //批量审核/反审核/删除
            if (sType.equalsIgnoreCase("multauditEstimatetradedataSub")) { //判断是否要进行批量审核与反审核
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
        EstimateTradeBean estimate = null; //创建一个pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); //和数据库进行连接
            //审核、反审核、删除汇率数据
            sqlStr = "update " + pub.yssGetTableName("Tb_Data_DivineEstimate") +
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
                    	estimate = new EstimateTradeBean(); //new 一个pojo类
                    	estimate.setYssPub(pub); //设置一些基础信息
                    	estimate.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                    	psmt1.setString(1,estimate.Num);
                        psmt1.addBatch(); 
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核预估交易数据出错!");
        } finally
        {
        	dbl.closeStatementFinal(psmt1);
        }
        return "";
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

}
