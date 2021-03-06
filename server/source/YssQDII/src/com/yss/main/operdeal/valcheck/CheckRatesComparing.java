package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

import com.yss.util.YssException;

/**20130712 added by liubo.Story #4106.【基金日净值增长率与市场指数差异比较】预警指标*/
public class CheckRatesComparing  extends BaseValCheck
{
	public CheckRatesComparing()
	{
		
	}
	
	public String doCheck(Date dTheDay, String sPortCodes) throws YssException
	{

		String strSql = "";
		ResultSet rs = null;
		String sSetID = "1";
		
		double dDifference = 0;		//阀值：差异阀值
		
		try
		{
			String sThresholdValue  = this.sPluginValue;	//获取阀值

        	//在除“综合业务处理”>>“预警检查”界面调用预警指标时，预警系统会自动获取阀值
        	//在诸如调度方案执行等其他界面中，预警系统不会去获取阀值。
        	//因此，需要手动在预警系统的数据表中查找并获取到该指标的阀值
        	//===================================
        	if(sThresholdValue == null || sThresholdValue.trim().equals(""))
        	{
        		strSql = " select * from t_port_plugin " +
        			 	 " where C_Plugin_Code = 'CheckRatesComparing' and C_Plugin_Condition = 'presetDate' " +
        			 	 " and C_Port_Code = " + dbl.sqlString(pub.getAssetGroupCode() + "-" + sPortCodes);
        		
        		rs = dbl.queryByPreparedStatement(strSql);

        		while(rs.next())
        		{
        			if(rs.getString("C_Plugin_Value") != null)
        			{
        				sThresholdValue = rs.getString("C_Plugin_Value");
        			}
        		}
        		
        		dbl.closeResultSetFinal(rs);
        	}
        	//================end===================

        	//正常情况下，该指标需要设置1个阀值。
            if (sThresholdValue.split(";").length >= 1)
            {
            	if (YssFun.isNumeric(sThresholdValue.split(";")[0]))
            	{
            		dDifference = YssFun.toDouble(sThresholdValue.split(";")[0]);	//阀值：差异阀值
            	}
            	
            }
            else
            {
        		this.sIsError = "true";
        		
            	this.checkInfos = "【基金日净值增长率与市场指数差异比较】预警指标阀值设置不正确！";

            	printInfo(this.checkInfos);
            	
            	return "";
            }
            
            //根据传入的组合代码，获取套账号
            strSql = "select * from lsetlist " +
            		 " where FSetID in (select FAssetCode from " + pub.yssGetTableName("tb_para_portfolio") +
            		 " where FPortCode = " + dbl.sqlString(sPortCodes) + ") " +
            		 " order by FYear desc";
            
            rs = dbl.queryByPreparedStatement(strSql);
            
            if (rs.next())
            {
            	sSetID = rs.getString("FSetCode");
            }
            
            dbl.closeResultSetFinal(rs);
            
            //获取操作日期当天的财务估值表，科目代码为9400的“市场指数日净值增长率”，科目代码为9991的“日净值增长率”
            //两者相减，并获取相减结果的绝对值，与阀值进行比较
            //若得到的绝对值小于或等于阀值，则正常。大于阀值，则为违规
            strSql = "select Nvl(a.FStandardMoneyMarketValue,0) as Rate9400, " +
            		 " Nvl(b.FStandardMoneyMarketValue,0) as Rate9991 from  " +
            		 " (select * from  " + pub.yssGetTableName("tb_rep_guessvalue") +
            		 " where facctcode = '9400' and FPortCode = " + dbl.sqlString(sSetID) +  
            		 " and FDate = " + dbl.sqlDate(dTheDay) + ") a " +
            		 " left join  " +
            		 " (select * from tb_001_rep_guessvalue  " +
            		 " where facctcode = '9991' and FPortCode = " + dbl.sqlString(sSetID) +  
            		 " and FDate = " + dbl.sqlDate(dTheDay) + ") b " +
            		 " on 1 = 1";
            
            rs = dbl.openResultSet(strSql);
            
            if(rs.next())
            {
            	double dCurDiffValue = Math.abs(YssD.sub(rs.getDouble("Rate9400"), rs.getDouble("Rate9991")));
            	
            	dCurDiffValue = Math.abs(dCurDiffValue);
            	
            	if (dCurDiffValue > dDifference)
            	{
            		this.sIsError = "true";
            		
                	this.checkInfos = YssFun.formatDate(dTheDay) + "，组合" + sPortCodes + "的日净值增长率为" + 
                	YssD.mul(rs.getDouble("Rate9991"), 100) + "%，市场指数日净值增长率为" + 
                	YssD.mul(rs.getDouble("Rate9400"), 100) + "%，两者的差异值为" + dCurDiffValue + 
                	"，超过了预设阀值" + YssFun.formatNumber(dDifference, "#,##0.############");

                	printInfo(this.checkInfos);
            	}
            }
			
		}
		catch(Exception ye)
		{
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
	
	
}
