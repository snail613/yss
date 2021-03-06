package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.*;
import com.yss.util.*;

import java.util.HashMap;

import com.yss.util.YssException;
import com.yss.main.operdeal.bond.BondInsCfgFormula;
import com.yss.main.operdeal.BaseOperDeal;

/**
 * <p>Title: 检查债券付息或兑付日期并提前3天起给予提示</p>
 * 如果付息或兑付日期小于等于当前日期往后第3个工作日的日期，则给予提示
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CheckFixInterestInsDate
    extends BaseValCheck {
    public CheckFixInterestInsDate() {
    }

    //20130420 modified by liubo.Story #3528
    //根据配置的监控指标（CheckFixInterestInsDate）的阀值（N天）,推导出一个检查日期（以操作日期+阀值）
    //根据这个检查日期来判断是否需要将提示某些债券的派息日、调息日、兑付
    public String doCheck(Date dTheDay, String sPortCodes) throws Exception {
        ResultSet rs = null;
        String strSql = "";
        BaseOperDeal bod = new BaseOperDeal();
        BondInsCfgFormula bicf = new BondInsCfgFormula();
        int iDateIntervel = 0;
        String sIssueInfo = "无\r\n";
        String sInterestInfo = "无\r\n";
        try {
        	
        	if (this.sPluginValue != null && !this.sPluginValue.equalsIgnoreCase("null") && !this.sPluginValue.trim().equals(""))
        	{
        		iDateIntervel = Integer.parseInt(this.sPluginValue);
        	}
        	
            bod.setYssPub(pub);
            bicf.setYssPub(pub);
            strSql =
                " select distinct a.FSecurityCode,a.FPortCode,d.FPortName,b.FHolidaysCode, " +
                " b.FSecurityName,b.FSubCatCode,c.FInsStartDate,c.FInsEndDate, e.FID," +
                " c.FInsCashDate,c.FInsFrequency," +
                " e.finsstartdate,e.finsenddate,e.fissuedate,e.fexrightdate,b.Fsecurityname " +
                " from " +
                pub.yssGetTableName("Tb_Stock_Security") + " a " +
                " join (select FSecurityCode, FSecurityName, FSubCatCode ,FHolidaysCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) b on a.FSecurityCode = b.FSecurityCode " +
                " join (select FSecurityCode,FInsStartDate,FInsEndDate, " +
                " FInsCashDate,FInsFrequency from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode " +
                " join (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +
                " left join (select * from " + pub.yssGetTableName("tb_para_interesttime") + " where FCheckState = 1) e on a.fsecuritycode = e.fsecuritycode " +
                " where e.finsEndDate >= " + dbl.sqlDate(dTheDay) +
                " and a.FPortCode = " + dbl.sqlString(sPortCodes) +
                " and a.FCheckState = 1 " +
                " order by a.FSecurityCode,a.FPortCode";
            rs = dbl.queryByPreparedStatement(strSql); //查找出证券库存中债券的有关信息  //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	//用操作日期加上阀值，得到一个检查日期
//                java.util.Date threeDaysLater = bod.getWorkDay(rs.getString("FHolidaysCode"),dTheDay, iDateIntervel); 
            	java.util.Date threeDaysLater = YssFun.addDay(dTheDay, iDateIntervel);
                
                //获取某只债券的品种子类型
                String subCatCode = rs.getString("FSubCatCode");
                
                if (rs.getString("FISSUEDATE") == null || rs.getString("FExRightDate") == null)
                {
                	continue;
                }
                
                //如果(T日有某债券库存)且(T日～T+N日期间，该债券将进行派息或兑付)，则进行提醒。
                if (YssFun.dateDiff(threeDaysLater, rs.getDate("FISSUEDATE")) <= 0 && YssFun.dateDiff(dTheDay, rs.getDate("FISSUEDATE")) >= 0)
                {
                	if (sIssueInfo.equals("无\r\n"))
                	{
                		sIssueInfo = "";
                	}
                	sIssueInfo += "债券代码：" + rs.getString("FSecurityCode") + "，债券名称：" + rs.getString("Fsecurityname") +
                				  "，派息/兑付日：" + YssFun.formatDate(rs.getString("FISSUEDATE")) + "\r\n";
                	continue;
                }
                if (YssFun.dateDiff(threeDaysLater, rs.getDate("FExRightDate")) <= 0 && YssFun.dateDiff(dTheDay, rs.getDate("FExRightDate")) >= 0)
                {
                	if (sIssueInfo.equals("无\r\n"))
                	{
                		sIssueInfo = "";
                	}
                	sIssueInfo += "债券代码：" + rs.getString("FSecurityCode") + "，债券名称：" + rs.getString("Fsecurityname") +
                				  "，派息/兑付日：" + YssFun.formatDate(rs.getString("FExRightDate")) + "\r\n";
                	continue;
                	
                }
                
                //如果(T日有某债券库存)且(该债券品种子类型为‘FI03 浮息债券’)且｛[(该债券本期间的“计息起始日”处于T日～T+N日)且(该债券不处于第一个计息期间)] ,
                //或 [(该债券本期间的“计息截止日+1日”处于T日～T+N日)且(该债券不处于最后一个计息期间)]
                //则提示该债券需要在某日进行调息
                if (subCatCode.equalsIgnoreCase("FI03")) 
                { 
                	java.util.Date dTargetDate = checkInterestPeriod(rs.getString("FSecurityCode"),threeDaysLater,dTheDay,rs.getInt("FID"));
                	
                	if (dTargetDate != null)
                	{
                    	if (sInterestInfo.equals("无\r\n"))
                    	{
                    		sInterestInfo = "";
                    	}
                    	
                		sInterestInfo += "债券代码：" + rs.getString("FSecurityCode") + "，债券名称：" + rs.getString("Fsecurityname") + "，调息日：" + YssFun.formatDate(dTargetDate) + "\r\n";
                	}
                } 
            }
            
            //--- edit by songjie 2013.04.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
        	this.sIsError = "true";		//20130620 modified by liubo.Story #3759.新的预警指标算法会对已有的产生影响
        	this.checkInfos = "近期有以下债券派息/兑付：\r\n";
        	this.checkInfos += sIssueInfo + "\r\n";
        	this.checkInfos += "近期有以下浮息债券进入新的计息期间，请注意维护新利率：\r\n";
        	this.checkInfos += sInterestInfo + "\r\n";
            //--- edit by songjie 2013.04.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            printInfo(this.checkInfos);
        } 
        catch (Exception e)
        {
            throw new YssException("检查债券付息或兑付出错", e);
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
    
    //20130420 added by liubo.Story #3528
    //返回某只债券的调息日，若返回NULL表示无需进行提醒
    private java.util.Date checkInterestPeriod(String sSecurityCode, 
    				java.util.Date dTargetDate, java.util.Date dTheDay, int FID) throws YssException
    {
    	boolean bReturn = false;
    	String strSql = "";
    	ResultSet rs = null;
    	java.util.Date dReturn = null;
    	
    	try
    	{
    		strSql = "select a.*,b.Finsstartdate as InsStart,b.finsenddate as InsEnd " +
    				 " from " + pub.yssGetTableName("tb_para_interesttime") + " a " +
    				 " left join " + pub.yssGetTableName("tb_para_fixinterest") + " b on a.fsecuritycode = b.fsecuritycode " +
    				 " where a.FSecurityCode = " + dbl.sqlString(sSecurityCode) +
    				 " and a.Fcheckstate = 1 and a.FID = " + FID;
    	
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			//若“债券计息期间设置"的计息起始日大于“债券信息设置”的计息起始日，表示该计息期间不是该券第一个计息期间
    			//那么就需要判断该计息期间的计息起始日是否属于T日～T+N日，若属于则需要提示该起始日为调息日
    			//=======================================
    			if (rs.getDate("InsStart") != null)
    			{
	    			if (YssFun.dateDiff(rs.getDate("FInsStartDATE"), rs.getDate("InsStart")) <0 && 
	    					YssFun.dateDiff(rs.getDate("FInsStartDATE"),dTargetDate) >= 0)
	    			{
	    				dReturn = rs.getDate("FInsStartDATE");
	    				break;
	    			}
    			}
    			//=================end======================
    			

    			//若“债券计息期间设置"的计息截止日小于“债券信息设置”的计息截止日日，表示该计息期间不是该券最后一个计息期间
    			//那么就需要判断该计息期间的计息截止日+1日是否属于T日～T+N日，若属于则需要提示为调息日
    			//=======================================
    			if(rs.getDate("InsEnd") != null)
    			{
	    			if (YssFun.dateDiff(rs.getDate("FInsEndDATE"), rs.getDate("InsEnd")) >0 && 
	    					YssFun.dateDiff(YssFun.addDay(rs.getDate("FInsEndDATE"),1),dTargetDate) >= 0)
	    			{
	    				dReturn = YssFun.addDay(rs.getDate("FInsEndDATE"),1);
	    				break;
	    			}
    			}
    			//=================end======================
    		}
    		
    	}
    	catch(Exception ye)
    	{
    		throw new YssException(ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    	return dReturn;
    }

}
