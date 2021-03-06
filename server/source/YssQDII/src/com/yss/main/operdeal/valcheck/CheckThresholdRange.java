package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**liubo 2013-5-21 STORY 3759 【单位净值超出阀值区间时提醒】预警指标*/
public class CheckThresholdRange extends BaseValCheck
{
	
	public CheckThresholdRange()
	{
		
	}
	
	public String doCheck(Date dTheDay, String sPortCodes) throws YssException
	{
		ResultSet rs = null;
        String strSql = "";
		
        double dUpperLimit_1 = 0.0000;		//上限1
        double dUpperLimit_2 = 0.0000;		//上限2

        double dLowerLimit_1 = 0.0000;		//下限1
        double dLowerLimit_2 = 0.0000;		//下限2
        
        int iInterval = 0;
        
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
        				 " where C_Plugin_Code = 'CheckThresholdRange' and C_Plugin_Condition = 'ThresholdRange' " +
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
        	if (sThresholdValue.split(";").length >= 4)
        	{
        		
        		if (YssFun.isNumeric(sThresholdValue.split(";")[0]))
        		{
        			dLowerLimit_1 = YssFun.toDouble(sThresholdValue.split(";")[0]);	//阀值：下限1
        		}
        		if (YssFun.isNumeric(sThresholdValue.split(";")[1]))
        		{
        			dLowerLimit_2 = YssFun.toDouble(sThresholdValue.split(";")[1]);	//阀值：下限2
        		}
        		if (YssFun.isNumeric(sThresholdValue.split(";")[2]))
        		{
        			dUpperLimit_1 = YssFun.toDouble(sThresholdValue.split(";")[2]);//阀值：上限1
        		}
        		if (YssFun.isNumeric(sThresholdValue.split(";")[3]))
        		{
        			dUpperLimit_2 = YssFun.toDouble(sThresholdValue.split(";")[3]);//阀值：上限2
        		}
        	}
        	else
        	{
        		this.sIsError = "true";
            	this.checkInfos = "阀值设置不正确！";
            	
        		return "";
        	}
        	
        	//根据传入的估值日期、估值组合，获取该组合在估值日期中净值表的单位净值
        	strSql = "select a.Fportcode,b.Fportname,a.FPrice from " + pub.yssGetTableName("tb_data_navdata") + " a " + 
        			 " left join " + pub.yssGetTableName("tb_para_portfolio") + " b " +
        			 " on a.Fportcode= b.fportcode " +
        			 " where a.FPortCode = " + dbl.sqlString(sPortCodes) +
        			 " and a.FKeyCode = 'Unit' " +
        			 " and a.FNavdate = " + dbl.sqlDate(dTheDay);
        	rs = dbl.queryByPreparedStatement(strSql);
        	while(rs.next())
        	{
        		//若单位净值小于下限1阀值，给出提示
        		if (rs.getDouble("FPrice") < dLowerLimit_1)
        		{
	        		this.sIsError = "true";
	            	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
	            					  rs.getString("FportCode") + "  " + 
	            					  rs.getString("FportName") + "," + 
	            					  "基金净值为" + YssFun.formatNumber(rs.getDouble("FPrice"),"#,##0.000") + 
	            					  "，低于预设阀值" + YssFun.formatNumber(dLowerLimit_1, "#,##0.000") + "！";
        		}
        		//若单位净值小于下限2阀值，大于等于下限1阀值，给出提示：
        		else if(rs.getDouble("FPrice") >= dLowerLimit_1 && rs.getDouble("FPrice") < dLowerLimit_2)
        		{

	        		this.sIsError = "true";
	            	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
	            					  rs.getString("FportCode") + "  " + 
	            					  rs.getString("FportName") + "," + 
	            					  "基金净值为" + YssFun.formatNumber(rs.getDouble("FPrice"),"#,##0.000") + 
	            					  "，低于预设阀值" + YssFun.formatNumber(dLowerLimit_2, "#,##0.000") + "！";
        		}
        		//若单位净值大于下限2阀值，小于上限1阀值，则属于正常情况，不需要提醒
        		else if(rs.getDouble("FPrice") > dLowerLimit_2 && rs.getDouble("FPrice") < dUpperLimit_1)
        		{
        			return "";
        		}
        		//若单位净值大于上限1阀值，小于等于下限2阀值，给出提示
        		else if(rs.getDouble("FPrice") > dUpperLimit_1 && rs.getDouble("FPrice") <= dUpperLimit_2)
        		{

	        		this.sIsError = "true";
	            	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
	            					  rs.getString("FportCode") + "  " + 
	            					  rs.getString("FportName") + "," + 
	            					  "基金净值为" + YssFun.formatNumber(rs.getDouble("FPrice"),"#,##0.000") + 
	            					  "，高于预设阀值" + YssFun.formatNumber(dUpperLimit_1, "#,##0.000") + "！";
        		}
        		//若单位净值大于上限2阀值，给出提示
        		else if (rs.getDouble("FPrice") > dUpperLimit_2)
        		{
	        		this.sIsError = "true";
	            	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
	            					  rs.getString("FportCode") + "  " + 
	            					  rs.getString("FportName") + "," + 
	            					  "基金净值为" + YssFun.formatNumber(rs.getDouble("FPrice"),"#,##0.000") + 
	            					  "，高于预设阀值" + YssFun.formatNumber(dUpperLimit_2, "#,##0.000") + "！";
        		}
        		
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
        	throw new YssException();
        }
        finally
        {
        	dbl.closeResultSetFinal(rs);
        }
		
		return "";
	}

}
