package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

import com.yss.util.YssException;


/**liubo 2013-7-2 STORY 4065 【新股转流通录入提醒】预警指标*/
public class CheckTurnToCirculationInfo extends BaseValCheck
{
	public CheckTurnToCirculationInfo()
	{
		
	}

	public String doCheck(Date dTheDay, String sPortCodes) throws YssException
	{
		StringBuffer bufSql = new StringBuffer();
		StringBuffer bufResults = new StringBuffer();
		ResultSet rs = null;
		int iResult = 0;
		
		int iInvterval = 0;		//阀值：截止日前N日
		
		try
		{
			String sThresholdValue  = this.sPluginValue;	//获取阀值

        	//在除“综合业务处理”>>“预警检查”界面调用预警指标时，预警系统会自动获取阀值
        	//在诸如调度方案执行等其他界面中，预警系统不会去获取阀值。
        	//因此，需要手动在预警系统的数据表中查找并获取到该指标的阀值
        	//===================================
        	if(sThresholdValue == null || sThresholdValue.trim().equals(""))
        	{
        		bufSql.append(" select * from t_port_plugin ");
        		bufSql.append(" where C_Plugin_Code = 'CheckTurnToCirculationInfo' and C_Plugin_Condition = 'presetDate' ");
        		bufSql.append(" and C_Port_Code = " + dbl.sqlString(pub.getAssetGroupCode() + "-" + sPortCodes));
        		rs = dbl.queryByPreparedStatement(bufSql.toString());

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
            		iInvterval = YssFun.toInt(sThresholdValue.split(";")[0]);	//阀值：截止日前N日
            	}
            	
            }
            else
            {
        		this.sIsError = "true";
        		
            	this.checkInfos = "【新股转流通录入提醒】预警指标阀值设置不正确！";

            	printInfo(this.checkInfos);
            	
            	return "";
            }
            
            bufResults.append("\r\n");
            
            bufSql.append(" select * from ( ");
            bufSql.append(" select distinct FSecurityCode,FLockBeginDate,FLockEndDate,FAttrClsCode from ");
            bufSql.append(pub.yssGetTableName("tb_data_newissuetrade"));
            bufSql.append(" where FSecurityCode in ");
            bufSql.append(" (select distinct FSecurityCode from ");
            bufSql.append(pub.yssGetTableName("tb_data_newissuetrade"));
            bufSql.append(" where FTradeTypeCode = '45') ");
            bufSql.append(" and FSecurityCode not in ");
            bufSql.append(" (select distinct FSecurityCode from ");
            bufSql.append(pub.yssGetTableName("tb_data_newissuetrade"));
            bufSql.append(" where FTradeTypeCode = '46') ");
            bufSql.append(" and FPortCode = " + dbl.sqlString(sPortCodes));
            bufSql.append(" and FCheckState = 1 and FTradeTypeCode = '45') a ");
            bufSql.append(" left join (select distinct FSecurityCode as FStockSec,FAttrClsCode as FStockAttr from ");
            bufSql.append(pub.yssGetTableName("tb_stock_security") + " where FPortCode = " + dbl.sqlString(sPortCodes));
            bufSql.append(" and FCheckState = 1 and FStorageDate = " + dbl.sqlDate(dTheDay) + ") b ");
            bufSql.append(" on a.FSecurityCode = b.FStockSec and a.FAttrClsCode = b.FStockAttr ");
            
            rs = dbl.queryByPreparedStatement(bufSql.toString());
            
            while(rs.next())
            {
            	//当某条新股新债数据，在当天有对应的库存数据
            	if (rs.getString("FStockSec") != null && rs.getString("FStockAttr") != null)
            	{
            		//获取提醒触发区间的上限日期（截止日-阀值：截止日前N日）
            		Date dStartDate = YssFun.addDay(rs.getDate("FLockEndDate"),-iInvterval);
            		//获取提醒触发区间的下限日期（截止日下一天）
            		Date dEndDate = YssFun.addDay(rs.getDate("FLockEndDate"), 1);
            		
            		//在有新股锁定期库存、且没有录入对应转流通数据的前提下，
            		//在 距离新股锁定截止日前N日～新股销定截止日下一日 期间，进行提醒
            		if (YssFun.dateDiff(dStartDate, dTheDay) >= 0 && YssFun.dateDiff(dEndDate, dTheDay) <= 0)
            		{
            			bufResults.append("证券【" + rs.getString("FSecurityCode") + "】已存在新股新债的锁定数据。请最晚于" +
            					"【" + YssFun.formatDate(dEndDate, "yyyy年MM月dd日") + "】录入该证券的新股转流通数据！\r\n");
            			
            			iResult ++;
            			
            		}
            	}
            }
            
            if (iResult > 0)
            {

        		this.sIsError = "true";
        		
            	this.checkInfos = bufResults.toString();

            	printInfo(this.checkInfos);
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
