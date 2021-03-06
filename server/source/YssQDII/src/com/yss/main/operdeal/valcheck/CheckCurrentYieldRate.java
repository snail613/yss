package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**liubo 2013-5-16 STORY 3759 【杠杆分级A类份额当期收益率检查 】预警指标*/
public class CheckCurrentYieldRate extends BaseValCheck
{
	public CheckCurrentYieldRate()
	{
		
	}
	
	public String doCheck(Date dTheDay, String sPortCodes) throws YssException
	{
        ResultSet rs = null;
        String strSql = "";
        java.util.Date dPresetDate = null;	//阀值：预设日期
        int iMonthInterval = 0;					//阀值：间隔月数
        
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
        				 " where C_Plugin_Code = 'CheckCurrentYieldRate' and C_Plugin_Condition = 'presetDate' " +
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
            
        	//正常情况下，该指标需要设置2个阀值。每个阀值以分号分割
            if (sThresholdValue.split(";").length >= 2)
            {
            	if (YssFun.isDate(sThresholdValue.split(";")[0]))
            	{
            		dPresetDate = YssFun.toDate(sThresholdValue.split(";")[0]);	//阀值：预设日期
            	}
            	
            	if (YssFun.isNumeric(sThresholdValue.split(";")[1]))
            	{
            		iMonthInterval = YssFun.toInt(sThresholdValue.split(";")[1]);	//阀值：间隔月数
            	}
            	
            	if (dPresetDate == null)
            	{
            		return "";
            	}
            }
            else
            {
        		this.sIsError = "true";
        		
            	this.checkInfos = "【杠杆分级A类份额当期收益率检查 】预警指标阀值设置不正确！";

            	printInfo(this.checkInfos);
            	
            	return "";
            }
            
            //若满足(“估值日期”大于等于“预设日期”) 
            //&& (“估值日期”的月份=“预设日期”的月份+N * x, 其中x为{0,1,2,3})
            //&& (“估值日期”年月日中的”日”=“预设日期”年月日中的”日”)
            //则开始做预警检查
            if(getMatchCondition(dPresetDate, dTheDay, iMonthInterval))
            {
	            //查询{组合分级设置}中的份额类型为“A类份额”的[约定收益率]，获得比率公式代码。
	            //由{比率公式设置}，查询对应比率公式的所有比率关联数据中，最大的“启用日期”。
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
	        		//如“最大启用日期”大于等于估值日期，则为正常；否则为异常，中止调度方案，并生成如下提示信息
	        		if (YssFun.dateDiff(dTheDay,rs.getDate("FRangeDate")) < 0)
	        		{
	        			this.sIsError = "true";
	                	this.checkInfos = YssFun.formatDate(dTheDay) + "," + 
	                					  rs.getString("FportCode") + "  " + 
	                					  rs.getString("FportClsName") + "," + 
	                					  "无A类约定年化收益率，无法进行资产估值！";
	                	printInfo(checkInfos);
	        		}
	        		iInterval++;
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
        	this.sIsError = "true";
        	throw new YssException(ye);
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


	//此方法用于判断“估值日期”与“预设日期”是否满足下面的关系：
    //(“估值日期”大于等于“预设日期”) 
    //&& (“估值日期”的月份=“预设日期”的月份+N * x, 其中x为{0,1,2,3})
    //&& (“估值日期”年月日中的”日”=“预设日期”年月日中的”日”)
	private boolean getMatchCondition(java.util.Date dTargetDate, java.util.Date dTheDay, int iInterval)
	{
		boolean bReturn = true;

		//判断是否(“估值日期”大于等于“预设日期”) 
		if(YssFun.dateDiff(dTargetDate, dTheDay) >= 0)
		{
			bReturn = true;
		}
		else
		{
			return false;
		}
		
		//判断是否(“估值日期”的月份=“预设日期”的月份+N * x, 其中x为{0,1,2,3})
		
		int iResult = YssFun.monthDiff(dTargetDate, dTheDay);
		
		if (iResult == 0 || iResult == iInterval || iResult == iInterval * 2 || iResult == iInterval * 3)
		{
			bReturn = true;
		}
		else
		{
			return false;
		}
		
		//判断是否(“估值日期”年月日中的”日”=“预设日期”年月日中的”日”)
		int iTargetsection = YssFun.toInt(YssFun.formatDate(dTargetDate,"dd"));
		int iTheDaysection = YssFun.toInt(YssFun.formatDate(dTheDay,"dd"));

		iResult = (int)YssD.sub(iTargetsection, iTheDaysection);
		
		if (iResult == 0)
		{
			bReturn = true;
		}
		else
		{
			return false;
		}
		
		return bReturn;
		
	}
}
