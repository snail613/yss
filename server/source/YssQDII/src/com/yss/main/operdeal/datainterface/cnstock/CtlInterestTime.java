package com.yss.main.operdeal.datainterface.cnstock;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.main.operdeal.bond.BondInsCfgFormula;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.parasetting.FixInterest.InterestTime;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**20130717 added by liubo.Story #4123.需求北京-[赢时胜北京]QDV4.0[中]20130626001
 * 读取国内业务数据接口时，自动生成债券计息期间设置*/
public class CtlInterestTime extends DataBase 
{
	public CtlInterestTime()
	{
		
	}
	
	/**
	 * 主方法。获取操作日期当天的交易明细汇总表中的数据，查询出类型为债券的数据，并关联债券计息期间设置表
	 * 若某只债券不存在计息期间数据，则添加之
	 */
	public void inertData() throws YssException 
	{
		StringBuffer bufSql = new StringBuffer();
		ResultSet rs = null;
		
		try
		{
			bufSql.append("select distinct Nvl(c.cnt,0) as cnt,b.*,d.FHolidaysCode from " 
												+ pub.yssGetTableName("tb_HzJkMx") + " a ")
			.append(" left join " + pub.yssGetTableName("tb_para_fixinterest") + "  b ")
			.append(" on a.fzqdm = b.fsecuritycode ")
			.append(" left join (select FSecurityCode,Count(FSecurityCode) as cnt ")
			.append(" from " + pub.yssGetTableName("tb_para_interesttime") + " where FCheckState = 1 ")
			.append(" group by FSecurityCode) c ")
			.append(" on a.fzqdm = c.FSecurityCode ")
			.append("left join (select FSecurityCode,FHolidaysCode ")
			.append(" from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) d ")
			.append(" on a.fzqdm = d.FSecurityCode ")
			.append(" where FZQBZ = 'ZQ' ")
			.append(" and a.FDate = " + dbl.sqlDate(this.sDate));
			
			rs = dbl.queryByPreparedStatement(bufSql.toString());
			
			while(rs.next())
			{
				//判断证券代码是否为空，是为了确定某条债券的交易明细数据是否在债券信息表中存在
				//判断cnt这个字段是否为0，是确定该债券是否存在计息期间设置数据
				if (rs.getString("FSecurityCode") != null && rs.getDouble("cnt") == 0)
				{
					newInterestTime(rs.getString("FSecurityCode"),rs.getInt("FInsFrequency"),rs.getString("FHolidaysCode"),
									rs.getDate("FInsStartDate"),rs.getDate("FInsEndDate"),
									rs.getDouble("FFaceRate"),rs.getDouble("FBeforeFaceRate"),rs.getDouble("FFaceValue"));
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
	}
	
	/**
	 * 20130807 added by liubo.Bug #8970.QDV4赢时胜(北京)2013年08月05日02_B
	 * 重载该方法，使此类能指定某只债券生成债券计息期间设置数据
	 * @param sSecurityCode
	 * @param dDate
	 * @throws YssException
	 */
	public void inertData(String sSecurityCode, java.util.Date dDate) throws YssException 
	{
		StringBuffer bufSql = new StringBuffer();
		ResultSet rs = null;
		
		this.sDate = dDate;
		
		try
		{
			bufSql.append("select distinct Nvl(c.cnt, 0) as cnt, a.*, d.FHolidaysCode from " 
												+ pub.yssGetTableName("tb_para_fixinterest") + " a ")
			.append(" left join (select FSecurityCode,Count(FSecurityCode) as cnt ")
			.append(" from " + pub.yssGetTableName("tb_para_interesttime") + " where FCheckState = 1 ")
			.append(" group by FSecurityCode) c ")
			.append(" on a.FSecurityCode = c.FSecurityCode ")
			.append("left join (select FSecurityCode,FHolidaysCode ")
			.append(" from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) d ")
			.append(" on a.FSecurityCode = d.FSecurityCode ")
			.append(" where a.FSecurityCode = " + dbl.sqlString(sSecurityCode));
			
			rs = dbl.queryByPreparedStatement(bufSql.toString());
			
			while(rs.next())
			{
				//判断证券代码是否为空，是为了确定某条债券的交易明细数据是否在债券信息表中存在
				//判断cnt这个字段是否为0，是确定该债券是否存在计息期间设置数据
				if (rs.getString("FSecurityCode") != null && rs.getDouble("cnt") == 0)
				{
					newInterestTime(rs.getString("FSecurityCode"),rs.getInt("FInsFrequency"),rs.getString("FHolidaysCode"),
									rs.getDate("FInsStartDate"),rs.getDate("FInsEndDate"),
									rs.getDouble("FFaceRate"),rs.getDouble("FBeforeFaceRate"),rs.getDouble("FFaceValue"));
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
	}
	
	/**
	 * 生成债券计息期间设置数据
	 * @param sSecurityCode		证券代码
	 * @param iInsFrequency		年付息频率
	 * @param sHolidayCode		节假日群代码
	 * @param dInsStartDate		债券基本信息中的计息起始日
	 * @param dInsEndDate		债券基本信息中的计息截止日
	 * @param dFaceRate			税后票面利率
	 * @param dBeforeFaceRate	税前票面利率
	 * @param dFaceValue		票面金额
	 * @throws YssException
	 */
	private void newInterestTime(String sSecurityCode,int iInsFrequency,String sHolidayCode,
									Date dInsStartDate,Date dInsEndDate,
									double dFaceRate,double dBeforeFaceRate,double dFaceValue) throws YssException
	{
		
        InterestTime itTime = new InterestTime();
        InterestTime itTimes = null;
		BondInsCfgFormula bond = new BondInsCfgFormula();
		bond.setYssPub(pub);
		itTime.setYssPub(pub);
		HashMap hmReturn = new HashMap();

        ArrayList alInterest = new ArrayList();
		
		try
		{
			bond.getNextStartDateAndEndDate(this.sDate, 
					dInsStartDate, //总的起息日
					dInsEndDate, //总的截止日
					iInsFrequency, //年付息频率
					hmReturn, //用于返回本期计息起始日  截止日 的HashMap
					sHolidayCode);//节假日群代码 

			itTimes = new InterestTime();
			itTimes.setFSecurityCode(sSecurityCode);//证券代码
			itTimes.setFExRightDate((Date)hmReturn.get("InsEndDate"));//除权日
			itTimes.setFInsStartDate((Date)hmReturn.get("InsStartDate"));//起始日
			itTimes.setFInsEndDate((Date)hmReturn.get("InsEndDate"));//截止日
			itTimes.setFIssueDate(YssFun.addDay((Date)hmReturn.get("InsEndDate"), 1));//派息日
			itTimes.setFRecordDate((Date)hmReturn.get("InsEndDate"));//登记日  需要删掉
			itTimes.setFFaceRate(dFaceRate);//税后票面利率
			itTimes.setBeforeFaceRate(dBeforeFaceRate);//税前票面利率
			itTimes.setSettleDate((Date)hmReturn.get("InsFXDate"));//到帐日
			itTimes.setID(((Integer)hmReturn.get("PaidInterest")).intValue());//付息次数
			itTimes.setPayMoney(0);
			itTimes.setRemainMoney(dFaceValue);//票面金额
			
			alInterest.add(itTimes);
			

			if(alInterest.size() > 0){
				itTime.saveMutliInfo(alInterest);
			}
			
		}
		catch(Exception ye)
		{
			throw new YssException();
		}
		
	}

}
