package com.yss.main.report;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IClientReportView;
import com.yss.util.YssException;

//20100708 added by liubo.Story #1196
//此类用于生成两费明细表。因为无法直接用SQL语句查询出每个月月末的数据，所以没有采用数据源的方法来生成。

public class ICBCFeeDetail extends BaseBean implements IClientReportView{
	
	String strPortCode = "";				//组合代码
	String strAccountCode = "";				//境内账户代码
	String strStartDate = "";				//起始日期
	String strEndDate = "";					//截止日期
	
	public ICBCFeeDetail()
	{
		
	}
	
	public void parseRowStr(String sRowStr) throws YssException
	{
		String reqAry[] = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\t");
            
            this.strStartDate = reqAry[0];
            this.strEndDate = reqAry[1]; 
            this.strPortCode = reqAry[2];
            this.strAccountCode = reqAry[3];
            
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }
	}
	

	public String getReportData(String sReportType) throws YssException {
		
		
		String strSql = "";
		double dbTrustee = 0;   				//托管费
		double dbManagerial = 0;				//管理费
		double dbTotal = 0;						//总计
		double dbForeignTotal = 0;  			//外币总计
		double dbManagerial_ToPay = 0;
		double dbTrustee_ToPay = 0;
		double dbTotal_ToPay = 0;			
		double dbForeignTotal_ToPay = 0; 
		double dbAcctBal = 0;					//现金账户余额
		String sRate_ToPay = "0.00";				//待支付项的汇率
		
		StringBuffer buff = new StringBuffer();
		ResultSet rsFee = null;
		ResultSet rsCollect = null;
		ResultSet rsCury = null;
		ResultSet rsEndBal = null;
		
		DecimalFormat dft = new DecimalFormat("#.000");

		
		try
		{
			//this.setYssPub(pub);
			
			String strDateDiff = getDateDiff();		//首先根据前台传入的起始和截止日期，生成yyyymm形式的中间的每个月的明细数据。
			
			strSql  = "select a.FCuryCode as a1,a.FCuryCode as a2,a.FCuryCode as a3,b.FCuryCode as b1,b.FCuryCode as b2 " +
					" from " + pub.yssGetTableName("Tb_Stock_Invest") + " a left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " b on 1=1 " +
					" where a.fportcode = '" + strPortCode + "' and a.fivpaycatcode = 'IV001' and b.fcashacccode = '" + strAccountCode + "' and a.FYearMonth in (" + strDateDiff + ")";
			//查询出两费的货币代码和现金帐户的货币代码
			rsCury = dbl.openResultSet(strSql);
			if (rsCury.next())
			{
				buff.append(" ").append("\t").append(rsCury.getString("a1")).append("\t").append(rsCury.getString("a2")).append("\t").append(rsCury.getString("a3")).append("\t").append(rsCury.getString("b1")).append("\t").append(rsCury.getString("b2")).append("\r\n");
				
			}
			else
			{
				return "";
			}
			
			String strDateStr[]  = strDateDiff.split(",");
			StringBuffer sqlBuff = new StringBuffer();
			
			//根据strDateDiff，也就是起始和截止日中间的月份明细，以月份为单位，生成一组两费查询语句
			for (int i = 0;i < strDateStr.length;i++)
			{
				sqlBuff.append(" select a.FyearMonth as YearMonth,round(a.fbal,3) as IV001,round(b.fbal,3) as IV002,round(sum(a.fbal + b.fbal),3) as cnt,round(c.FPortRate/c.FBaseRate,9) as Rate,round(sum(a.fbal + b.fbal)*(c.FPortRate/c.FBaseRate),3) as ForeignCury ");
				sqlBuff.append(" from " + pub.yssGetTableName("Tb_Stock_Invest") + " a, " + pub.yssGetTableName("Tb_Stock_Invest") + " b," + pub.yssGetTableName("Tb_Data_ValRate") + " c ");
				sqlBuff.append(" where a.fStorageDate = b.fStorageDate and a.fStorageDate = c.FValDate ");
				sqlBuff.append(" and a.fStorageDate in (select max(FStorageDate) as StorageDate from " + pub.yssGetTableName("Tb_Stock_Invest") + " where fyearmonth = " + strDateStr[i] + ") ");
				sqlBuff.append(" and a.fportcode = b.fportcode and a.fportcode = c.FPortCode and a.fportcode = '" + strPortCode + "' and a.fivpaycatcode = 'IV001' and b.fivpaycatcode = 'IV002' ");
				sqlBuff.append(" and c.FCuryCode = '" + rsCury.getString("b1") + "' ");
				sqlBuff.append(" group by c.FPortRate,c.FBaseRate,a.FYearMonth,a.fbal,b.fbal");
				sqlBuff.append(" union all ");
			}
			
			//取出查询语句末尾的union all
			rsCollect = dbl.openResultSet(sqlBuff.toString().substring(0,sqlBuff.toString().length()-11));
			
			//将每个月的两费明细进行自增加，并用变量进行储存，“总计”和“待支付”需要进行调用
			while(rsCollect.next())
			{
				buff.append(rsCollect.getString("YearMonth")).append("\t").append(rsCollect.getString("IV001")).append("\t").append(rsCollect.getString("IV002")).append("\t").append(rsCollect.getString("cnt")).append("\t");
				buff.append(rsCollect.getString("Rate")).append("\t").append(rsCollect.getString("ForeignCury")).append("\r\n");
				
				dbManagerial = dbManagerial + rsCollect.getDouble("IV001");
				dbTrustee = dbTrustee + rsCollect.getDouble("IV002");
				dbTotal = dbTotal + rsCollect.getDouble("cnt");
				dbForeignTotal = dbForeignTotal + rsCollect.getDouble("ForeignCury");
				
			}
			//生成“总计”数据
			buff.append("总计").append("\t").append(dft.format(dbManagerial)).append("\t").append(dft.format(dbTrustee)).append("\t").append(dft.format(dbTotal)).append("\t");
			buff.append(" ").append("\t").append(dft.format(dbForeignTotal)).append("\r\n");
			
			//将截止日期数据赋给起始日期变量。然后调用getDateDiff方法得出截止日期yyyymm形式的数据。
			this.strStartDate = this.strEndDate;

			String strDateForStermEnd = getDateDiff();
			//根据截止日期，生成“待支付”的查询语句
			sqlBuff = new StringBuffer();
			sqlBuff.append(" select a.FStorageDate,a.FyearMonth as YearMonth,round(a.fbal,3) as IV001,round(b.fbal,3) as IV002,round(sum(a.fbal + b.fbal),3) as cnt,round(c.FPortRate/c.FBaseRate,9) as Rate,round(sum(a.fbal + b.fbal)*(c.FPortRate/c.FBaseRate),3) as ForeignCury ");
			sqlBuff.append(" from " + pub.yssGetTableName("Tb_Stock_Invest") + " a, " + pub.yssGetTableName("Tb_Stock_Invest") + " b," + pub.yssGetTableName("Tb_Data_ValRate") + " c ");
			sqlBuff.append(" where a.fStorageDate = b.fStorageDate and a.fStorageDate = c.FValDate ");
			sqlBuff.append(" and a.fStorageDate in (select max(FStorageDate) as StorageDate from " + pub.yssGetTableName("Tb_Stock_Invest") + " where fyearmonth = " + strDateForStermEnd + ") ");
			sqlBuff.append(" and a.fportcode = b.fportcode and a.fportcode = c.FPortCode and a.fportcode = '" + strPortCode + "' and a.fivpaycatcode = 'IV001' and b.fivpaycatcode = 'IV002' ");
			sqlBuff.append(" and c.FCuryCode = '" + rsCury.getString("b1") + "' ");
			sqlBuff.append(" group by c.FPortRate,c.FBaseRate,a.FStorageDate,a.FYearMonth,a.fbal,b.fbal");
			
			rsFee = dbl.openResultSet(sqlBuff.toString());
			//将生成的待支付数据用变量进行储存，在生成“差额”数据的时候需要进行调用
			while (rsFee.next())
			{
				dbManagerial_ToPay = rsFee.getDouble("IV001");
				dbTrustee_ToPay = rsFee.getDouble("IV002");
				dbTotal_ToPay = rsFee.getDouble("cnt");
				dbForeignTotal_ToPay = rsFee.getDouble("ForeignCury");
				sRate_ToPay = rsFee.getString("Rate");
			}
			
			buff.append("已支付").append("\t").append(dft.format(dbManagerial - dbManagerial_ToPay)).append("\t");
			buff.append(dft.format(dbTrustee - dbTrustee_ToPay)).append("\t").append(dft.format(dbTotal - dbTotal_ToPay)).append("\t");
			buff.append(" ").append("\t").append(dft.format(dbForeignTotal - dbForeignTotal_ToPay)).append("\r\n");
			
			buff.append("待支付").append("\t").append(dft.format(dbManagerial_ToPay)).append("\t").append(dft.format(dbTrustee_ToPay)).append("\t").append(dft.format(dbTotal_ToPay)).append("\t");
			buff.append(sRate_ToPay).append("\t").append(dft.format(dbForeignTotal_ToPay)).append("\r\n");
			
			sqlBuff = new StringBuffer();
			//生成“境内账户余额”的查询语句
			sqlBuff.append(" select round(FAccBalance,3)as endBal from " + pub.yssGetTableName("Tb_Stock_Cash") + " where FStorageDate in (select max(FStorageDate) from " + pub.yssGetTableName("Tb_Stock_Cash") + " where fyearmonth = " + strDateForStermEnd + ") ");
			sqlBuff.append(" and FCashAccCode = '" + strAccountCode + "'  ");
			
			rsEndBal = dbl.openResultSet(sqlBuff.toString());
			
			while (rsEndBal.next())
			{
				dbAcctBal = rsEndBal.getDouble("endBal");
			}
			
			buff.append("境内账户余额").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append(dft.format(dbAcctBal)).append("\r\n");
			
			buff.append("差额").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
			buff.append(dft.format(dbForeignTotal_ToPay - dbAcctBal)).append("\r\n");
			
			return buff.toString();
		}
		
		
		catch(Exception ex)
		{
			throw new YssException(ex.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rsFee);
			dbl.closeResultSetFinal(rsCollect);
			dbl.closeResultSetFinal(rsCury);
			dbl.closeResultSetFinal(rsEndBal);
		}
		
	}
	/**此方法会根据传入的起始日期和截止日期，生成yyyymm形式的月份明细数据
	 * 比如起始日期是2011年1月1日，截止日期是2011年5月1日。那么生成的数据就是“'201101','201102','201103','201104','201105'”
	 * 每个月份的明细数据都带有单引号，方便在拼接SQL语句时直接进行in操作
	 */
	public String getDateDiff() throws YssException
	{
		
		try
		{		
			if (strStartDate == "" || strStartDate == null ||strEndDate == "" || strEndDate == null)
			{
				return "";
			}
			else
			{
				SimpleDateFormat spf = new SimpleDateFormat("yyyyMM");
				DateFormat aa = DateFormat.getDateInstance();  
				Date date1 = aa.parse(strStartDate); // 开始日期
				Date date2 = aa.parse(strEndDate); //结束日期
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				List list = new ArrayList();
				list.add(spf.format(date1));
				c1.setTime(date1);  
				c2.setTime(date2);  
				while (!c1.getTime().toString().equals(c2.getTime().toString()))
				{
					c1.add(Calendar.MONTH,1);// 开始日期加一个月直到等于结束日期为止
					Date ss =c1.getTime();
//					String str =aa.format(ss);
//					str =str.substring(0,str.lastIndexOf("-"));
					String str = spf.format(ss);
					list.add(str);
				}
				StringBuffer buff = new StringBuffer();
				for (int i=0;i<list.size();i++ )
				{
					buff.append("'" + (String)list.get(i) + "',");
				}

				return buff.toString().substring(0,buff.toString().length()-1);
			}
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		
		
	}
	
	
	public String getStrPortCode() {
		return strPortCode;
	}
	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}
	public String getStrAccountCode() {
		return strAccountCode;
	}
	public void setStrAccountCode(String strAccountCode) {
		this.strAccountCode = strAccountCode;
	}
	public String getStrStartDate() {
		return strStartDate;
	}
	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}
	public String getStrEndDate() {
		return strEndDate;
	}
	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String GetBookSetName(String sPortCode) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkReportBeforeSearch(String sReportType)
			throws YssException {
		// TODO Auto-generated method stub
		return null;
	}


	public String getReportHeaders(String sReportType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSaveDefuntDay(String sRepotyType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

//	public void setYssPub(YssPub pub) {
//		// TODO Auto-generated method stub
//		
//	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
}
