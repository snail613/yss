package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**liubo 2013-5-17 STORY 3759 【杠杆分级A类份额下期收益率提前录入提醒】预警指标*/
public class CheckNexYieldtDataEntry extends BaseValCheck
{

	public CheckNexYieldtDataEntry()
	{
	}
	

	public String doCheck(Date dTheDay, String sPortCodes) throws YssException
	{
		ResultSet rs = null;
        String strSql = "";
        java.util.Date dPresetDate = null;	//阀值：预设日期
        int iMonthsInterval = 0;		//阀值：间隔月数
        int iDaysInterval = 0;			//阀值：提前天数
        
        int iInterval = 0;
		
        try
        {
        	String sThresholdValue  = this.sPluginValue;	//获取阀值

        	//在除“综合业务处理”>>“预警检查”界面调用预警指标时，预警系统会自动获取阀值
        	//在诸如调度方案执行等其他界面中，预警系统不会去获取阀值。
        	//因此，需要手动在预警系统的数据表中查找并获取到该指标的阀值
        	//===================================
        	if(sThresholdValue == null || sThresholdValue.trim().equals(""))
        	{
        		strSql = "select * from t_port_plugin " +
        				 " where C_Plugin_Code = 'CheckNexYieldtDataEntry' and C_Plugin_Condition = 'NexYield' " +
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

        	//正常情况下，该指标需要设置3个阀值。每个阀值以分号分割
        	if (sThresholdValue.split(";").length >= 3)
        	{
        		if (YssFun.isDate(sThresholdValue.split(";")[0]))
        		{
        			dPresetDate = YssFun.toDate(sThresholdValue.split(";")[0]);		//阀值：预设日期
        		}
        		if (YssFun.isNumeric(sThresholdValue.split(";")[1]))
        		{
        			iMonthsInterval = YssFun.toInt(sThresholdValue.split(";")[1]);	//阀值：间隔月数
        		}
        		if (YssFun.isNumeric(sThresholdValue.split(";")[2]))
        		{
        			iDaysInterval = YssFun.toInt(sThresholdValue.split(";")[2]);	//阀值：提前天数
        		}
        		
        		if (dPresetDate == null)
        		{
            		this.sIsError = "true";
            		
                	this.checkInfos = "阀值设置不正确！";
                	
        			return "";
        		}
        	}
        	else
        	{
        		this.sIsError = "true";
        		
            	this.checkInfos = "阀值设置不正确！";
            	
        		return "";
        	}
        	
        	//新收益率启用日期 = 阀值：预设日期 + 阀值：间隔月数。此日期做为此预警检查的日期段的上限日期
        	dPresetDate = YssFun.addMonth(dPresetDate, iMonthsInterval);
        	//以新收益率启用日期 - 阀值：提前天数，得到此预警检查的日期段的下限日期
        	java.util.Date dTargetDate = YssFun.addDay(dPresetDate, -iDaysInterval);
        	
        	//如“估值日期”介于｛“新收益率启用日期”-M天 至 “新收益率启用日期”｝
        	//查询{组合分级设置}中的份额类型为“A类份额”的[约定收益率]，获得比率公式代码。
        	//再由{比率公式设置}，查询对应比率公式的所有比率关联数据中，最大的“启用日期”。
        	if (YssFun.dateDiff(dTheDay, dTargetDate) <= 0 && YssFun.dateDiff(dTheDay, dPresetDate) >= 0)
        	{

            	strSql = " select * from " + pub.yssGetTableName("tb_ta_portcls") + " a " +
            			 " left join (select FFormulaCode,max(FRangeDate) as FRangeDate " +
            			 " from " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
            			 " where FCheckState = 1 group by FFormulaCode) b " +
            			 " on a.FConvention = b.FFormulaCode " +
            			 " left join (select FPortCode as portCode,FPortName as portName " +
            			 " from " + pub.yssGetTableName("tb_para_portfolio") +
            			 " where FcheckState = 1) c " +
            			 " on a.Fportcode = c.portCode" +
            			 " where a.Fcheckstate = 1 and a.FShareCategory = 1";
            	
            	rs = dbl.queryByPreparedStatement(strSql);

            	while(rs.next())
            	{
            		//如“最大启用日期”大于等于“新收益率启用日期”，则为正常；否则为异常，给出提示
            		if (YssFun.dateDiff(dPresetDate,rs.getDate("FRangeDate")) < 0)
            		{
            			this.sIsError = "true";
                    	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
                    					  rs.getString("FportCode") + "  " + 
                    					  rs.getString("FPortClsName") + "," + 
                    					  "A类份额将于" + YssFun.formatDate(dPresetDate) + "启用新收益率，请及时录入！";
                    	
                    	iInterval++;
            		}
            	}
        		
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
