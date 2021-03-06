package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**liubo 2013-5-21 STORY 3759 【定期折算日无折算相关数据，则禁止资产估值】预警指标
 * 此预警指标无阀值*/
public class CheckHaveDiscountData extends BaseValCheck
{

	public CheckHaveDiscountData()
	{
		
	}
	
	public String doCheck(Date dTheDay, String sPortCodes) throws YssException
	{

		ResultSet rs = null;
		ResultSet rsTsg = null;
        String strSql = "";
        boolean bReturn = true;		//检查结果，true为正常;false为不正常，需要终止
        int iInterval = 0;
        
        try
        {
        	//查询估值日当天，进行估值的组合在杠杆分级运作期设置中是否有记录
        	//若有，表示估值日是该组合的定期折算日
        	strSql = "select * from " + pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD") +
        			 " where FDISCOUNTDAY = " + dbl.sqlDate(dTheDay) + 
        			 " and FPortCode = " + dbl.sqlString(sPortCodes);
        	rsTsg = dbl.queryByPreparedStatement(strSql);
        	
        	while(rsTsg.next())
        	{
        		//检查｛TA交易数据｝中，是否有（销售类型为“09 基金拆分” && 成交日=“估值日”）的数据
        		//=================================
	        	strSql = "select count(*) as Cnt from " + pub.yssGetTableName("tb_ta_trade") +
	        			 " where FSellType = '09' and FPortCode = " + dbl.sqlString(sPortCodes) +
	        			 " and FTradeDate = " + dbl.sqlDate(dTheDay);
	        	rs = dbl.queryByPreparedStatement(strSql);
	        	if(rs.next())
	        	{
	        		if (rs.getInt("Cnt") > 0)
	        		{
	        			bReturn = true;
	        		}
	        		else
	        		{
	        			bReturn = false;
	        		}
	        	}
        		//==============end===================
	        	
	        	dbl.closeResultSetFinal(rs);
	        	
	        	//检查｛杠杆分级份额折算｝中，是否有（“折算日”=“估值日”）的数据。
        		//=================================
	        	strSql = "select a.*,Nvl(b.Cnt,0) as Cnt from " + pub.yssGetTableName("tb_para_portfolio") + " a " +
	        			 " left join (select FPortCode,FConversionDate,count(*) as Cnt  " +
	        			 " from " + pub.yssGetTableName("TB_ta_LeverShare") + " group by FPortCode,FConversionDate) b " +
	        			 " on a.Fportcode = b.FportCode " +
	        			 " and FConversionDate = " + dbl.sqlDate(dTheDay) +
	        			 " where a.FPortCode = " + dbl.sqlString(sPortCodes);
	
	        	rs = dbl.queryByPreparedStatement(strSql);
	        	if(rs.next())
	        	{
	        		if (rs.getInt("Cnt") > 0)
	        		{
	        			bReturn = true;
	        		}
	        		else
	        		{
	        			bReturn = false;
	        		}
	        	}
        		//================end=================
	        	
	        	//在上面两个检查中，若其中任意一项检查有数据，bReturn(检查结果)都会为true
	        	//若均无数据，则为不正常，需要终止估值并提示
	        	//=============================
	        	if (!bReturn)
	        	{
	        		this.sIsError = "true";
	            	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
	            					  rs.getString("FportCode") + "  " + 
	            					  rs.getString("FportName") + "," + 
	            					  "本日为定期折算日，但无相关数据，请核实后在进行估值！";
	            	
	            	printInfo(this.checkInfos);
	            	
	            	dbl.closeResultSetFinal(rs);
	        	}
	        	else
	        	{
	        		this.sIsError = "false";
	            	this.checkInfos = "正常";
	            	
	            	dbl.closeResultSetFinal(rs);
	        		
	        	}
	        	//============end=================
	        	
	        	iInterval++;
        	}
        	
        	if(iInterval == 0)
        	{
        		this.sIsError = "false";
            	this.checkInfos = "正常";
            	
            	dbl.closeResultSetFinal(rs);
        		
        	}
        }
        catch(Exception ye)
        {
        	this.sIsError = "true";
        	throw new YssException();
        }
        finally
        {
        	dbl.closeResultSetFinal(rs,rsTsg);
        }
		
		return "";
	}
	

    //界面输出提示信息
    private void printInfo(String sInfo) throws Exception 
    {
        runStatus.appendValCheckRunDesc(sInfo);
        
        if (this.sNeedLog.equals("true"))
        {
        	this.writeLog(sInfo);
        }
    }

}
