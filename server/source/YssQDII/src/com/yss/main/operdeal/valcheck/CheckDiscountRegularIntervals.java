package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**liubo 2013-5-20 STORY 3759 【杠杆分级定期折算提醒】预警指标*/
public class CheckDiscountRegularIntervals extends BaseValCheck
{
	
	public CheckDiscountRegularIntervals()
	{
		
	}
	
	public String doCheck(Date dTheDay, String sPortCodes) throws YssException
	{
		ResultSet rs = null;
        String strSql = "";
        java.util.Date dDiscountDate = null;	//折算日
        java.util.Date dPresetDate = null;		//提醒日期首日
        String sHolidayCode = "";			//节假日代码
        
        int iDiscountInterval = 0;			//折算日间隔
        int iPresetInterval = 0;			//提醒日期首日间隔
        
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
        				 " where C_Plugin_Code = 'CheckDiscountRegularIntervals' " +
        				 " and C_Plugin_Condition = 'RegularIntervals' " +
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
        		sHolidayCode = sThresholdValue.split(";")[0];			//阀值：节假日代码
        		
        		if (YssFun.isNumeric(sThresholdValue.split(";")[1]))
        		{
        			//阀值：提醒日期首日间隔。这个阀值从“本运作期末日”往前推算出提醒日期首日
        			iPresetInterval = YssFun.toInt(sThresholdValue.split(";")[1]);	
        		}
        		if (YssFun.isNumeric(sThresholdValue.split(";")[2]))
        		{
        			//阀值：折算日间隔。这个阀值从“本运作期末日”往后推算出折算日。该阀值允许为负数
        			iDiscountInterval = YssFun.toInt(sThresholdValue.split(";")[2]);
        		}
        		
        		//若获取不到节假日代码，则表示阀值配置不正确，推出此预警指标的执行
        		if (sHolidayCode == null || sHolidayCode.trim().equals(""))
        		{
            		this.sIsError = "false";
            		
                	this.checkInfos = "阀值设置不正确！";
                	
        			return "";
        		}
        		
        	}
        	else
        	{
        		this.sIsError = "false";
        		
            	this.checkInfos = "阀值设置不正确！";
            	
        		return "";
        	}
        	
        	//将“估值日期”与｛杠杆分级运作期设置｝中各期间的起、止日期进行对比，
        	//确定“本运作期末日”；并进而确定“提醒日期首日”、“折算日”。
        	//===================================
        	strSql = "select * from " + pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD") + " a " +
        			 "  left join " + pub.yssGetTableName("tb_para_portfolio") + " b " +
        			 " on a.FPortCode = b.FPortCode " +
        			 " where a.FportCode = " + dbl.sqlString(sPortCodes);
        	rs = dbl.queryByPreparedStatement(strSql);
        	
        	if (rs.next())
        	{
        		//若“估值日期”介于 ｛“提醒日期首日””～“本运作期末日”｝期间，需要给出提示
        		//===============================
        		dDiscountDate = super.getSettingOper().getWorkDay(sHolidayCode, rs.getDate("FENDDAY"), iDiscountInterval);
        		dPresetDate = YssFun.addDay(rs.getDate("FENDDAY"), -iPresetInterval);
        		
        		if (YssFun.dateDiff(dTheDay,rs.getDate("FENDDAY")) >=0 && YssFun.dateDiff(dTheDay,dPresetDate) <= 0)
        		{

        			this.sIsError = "true";
                	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
                					  rs.getString("FportCode") + "  " + 
                					  rs.getString("FportName") + "," + 
                					  "将于" + YssFun.formatDate(dDiscountDate) + "进行份额折算，请进行相关准备工作！";
        		}
        		//===============end================
        	}
        	else
        	{
        		this.sIsError = "false";
            	this.checkInfos = "正常";
            	
        		return "";
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
