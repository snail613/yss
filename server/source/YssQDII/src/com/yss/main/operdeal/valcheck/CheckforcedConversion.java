package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**liubo 2013-5-21 STORY 3759 【杠杆分级达到上拆点、下拆点后第N个工作日内，必须做份额折算】预警指标*/
public class CheckforcedConversion extends BaseValCheck
{
	public CheckforcedConversion()
	{
		
	}
	
	public String doCheck(Date dTheDay, String sPortCodes) throws YssException
	{
		ResultSet rs = null;
		ResultSet rsThresholdValue = null;
        String strSql = "";
		
        double dUpperLimit = 0.0000;		//上拆点
        double dLowerLimit = 0.0000;		//下拆点
        double dUnitValue = 0.0000;			//净值检查日单位净值
        int iDaysInterval = 0;				//间隔天数
        
        String sHolidaysCode = "";			//节假日群代码
        
        java.util.Date dTargetDate = null;
		
        try
        {
        	//获取阀值
        	String sThresholdValue  = this.sPluginValue;
        	
        	//在除“综合业务处理”>>“预警检查”界面调用预警指标时，预警系统会自动获取阀值
        	//在诸如调度方案执行等其他界面中，预警系统不会去获取阀值。
        	//因此，需要手动在预警系统的数据表中查找并获取到该指标的阀值
        	//===================================
        	if(sThresholdValue == null || sThresholdValue.trim().equals(""))
        	{
        		strSql = "select * from t_port_plugin " +
        				 " where C_Plugin_Code = 'CheckforcedConversion' and C_Plugin_Condition = 'ForcedConversion' " +
        				 " and C_Port_Code = " + dbl.sqlString(pub.getAssetGroupCode() + "-" + sPortCodes);
        		rs = dbl.queryByPreparedStatement(strSql);

        		while(rs.next())
        		{
        			if(rs.getString("C_Plugin_Value") != null)
        			{
        				sThresholdValue = rs.getString("C_Plugin_Value");
        			}
        		}
        	}
        	//================end===================
        	
        	//正常情况下，该指标需要设置4个阀值。每个阀值以分号分割
        	if (sThresholdValue != null && sThresholdValue.split(";").length >= 4)
        	{
        		sHolidaysCode = sThresholdValue.split(";")[0];			//阀值：节假日群代码
        		                                           
        		if (YssFun.isNumeric(sThresholdValue.split(";")[1]))
        		{
        			dUpperLimit = YssFun.toDouble(sThresholdValue.split(";")[1]);	//阀值：上拆点
        		}
        		if (YssFun.isNumeric(sThresholdValue.split(";")[2]))
        		{
        			dLowerLimit = YssFun.toDouble(sThresholdValue.split(";")[2]);//阀值：下拆点
        		}
        		if (YssFun.isNumeric(sThresholdValue.split(";")[3]))
        		{
        			iDaysInterval = YssFun.toInt(sThresholdValue.split(";")[3]);//阀值：工作日天数
        		}
        	}
        	
        	if (sHolidaysCode == null || sHolidaysCode.trim().equals(""))
        	{
        		this.sIsError = "true";
        		
            	this.checkInfos = "【杠杆分级达到上拆点、下拆点后第N个工作日内，必须做份额折算】预警指标阀值设置不正确！";
            	
            	printInfo(this.checkInfos);
            	
        		return "";
        	}
        	
        	//获取“估值日期”起第 （- N）个工作日，作为“净值检查日期”\
        	//检查C日的单位净值，如 下拆点阀值L < C日单位净值 < 上拆点阀值H，则认定为正常，不需要在做下面的检查
        	//==============================
        	dTargetDate = super.getSettingOper().getWorkDay(sHolidaysCode, dTheDay, -iDaysInterval);
        	
        	strSql = "Select FPrice from " + pub.yssGetTableName("tb_data_navdata") +
        			 " where FNavDate = " + dbl.sqlDate(dTargetDate) +
        			 " and FPortCode = " + dbl.sqlString(sPortCodes) + 
        			 " and FKeyCode = 'Unit'";
        	
        	rs = dbl.queryByPreparedStatement(strSql);
        	
        	if(rs.next())
        	{
        		dUnitValue = rs.getDouble("FPrice");
        		if (dUnitValue > dLowerLimit && dUnitValue < dUpperLimit)
        		{
            		this.sIsError = "false";
                	this.checkInfos = "正常";
                	
        			return "";
        		}
        	}
        	dbl.closeResultSetFinal(rs);
        	

    		//检查｛TA交易数据｝中，是否有（销售类型为“09 基金拆分” && 成交日=“估值日”）的数据
    		//=================================
        	strSql = "select count(*) as Cnt from " + pub.yssGetTableName("tb_ta_trade") +
        			 " where FSellType = '09' and FPortCode = " + dbl.sqlString(sPortCodes) +
        			 " and FTradeDate = " + dbl.sqlDate(dTargetDate);
        	rs = dbl.queryByPreparedStatement(strSql);
        	if(rs.next())
        	{
        		if (rs.getInt("Cnt") > 0)
        		{
            		this.sIsError = "false";
                	this.checkInfos = "正常";
        			return "";
        		}
        	}
        	
        	dbl.closeResultSetFinal(rs);
    		//=================end================
        	

        	//检查｛杠杆分级份额折算｝中，是否有（“折算日”=“估值日”）的数据。
    		//=================================
        	strSql = "select a.*,Nvl(b.Cnt,0) as Cnt from " + pub.yssGetTableName("tb_para_portfolio") + " a " +
        			 " left join (select FPortCode,FConversionDate,count(*) as Cnt  " +
        			 " from " + pub.yssGetTableName("TB_ta_LeverShare") + " group by FPortCode,FConversionDate) b " +
        			 " on a.Fportcode = b.FportCode and FConversionDate = " + dbl.sqlDate(dTargetDate) +
        			 " where a.FPortCode = " + dbl.sqlString(sPortCodes);

        	rs = dbl.queryByPreparedStatement(strSql);
        	if(rs.next())
        	{
        		if (rs.getInt("Cnt") > 0)
        		{
        			return "";
        		}
        		else
        		{
        			this.sIsError = "true";
	            	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
	            					  rs.getString("FportCode") + "  " + 
	            					  rs.getString("FportName") + "," + 
	            					  "在" + YssFun.formatDate(dTargetDate) + "单位净值为" + 
	            					  YssFun.formatNumber(dUnitValue, "#,##0.000") +
	            					  "，超出预设区间，本日必须进行折算处理。但是未获取到相关折算数据。请核实后再进行估值！";
	            	printInfo(this.checkInfos);
        		}
        	}
        	else
        	{
        		this.sIsError = "false";
            	this.checkInfos = "正常";
            	
            	dbl.closeResultSetFinal(rs);
        	}
    		//================end=================
        	
        }
        catch(Exception ye)
        {
			this.sIsError = "true";
        	throw new YssException();
        }
        finally
        {
        	dbl.closeResultSetFinal(rs);
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
