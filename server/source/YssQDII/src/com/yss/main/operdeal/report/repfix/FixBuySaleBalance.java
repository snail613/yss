package com.yss.main.operdeal.report.repfix;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.accbook.pojo.SecAccBookDetailBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssReflection;
import com.yss.vsub.YssDbOperSql;

/**
 * @author 
 * add by huangqirong 2011-12-02 ETF STORY #1789应补、应卖股票余额报表
 *
 */
/**
 * @author Cyn
 *
 */
public class FixBuySaleBalance extends BaseBuildCommonRep {
	
	private String date="";
	private String portCode="";
	private CommonRepBean repBean;
	
	public FixBuySaleBalance() {
		// TODO Auto-generated constructor stub
	}
	
	public String buildReport(String sType) throws YssException {
		// TODO Auto-generated method stub
		String sResult = "";
        sResult = getRepDatas();
        return sResult;
	}
	
	public void initBuildReport(BaseBean bean) throws YssException {
		String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        this.date = reqAry[0].split("\r")[1];
        this.portCode = reqAry[1].split("\r")[1];
	}	
	
	private String getRepDatas() throws YssException{
		ResultSet rs = null;	
		
		//" \n2004-8-2\t \n2004-8-3 \n2004-8-2\t \n2004-8-3 \n2004-8-2\t \n2004-8-3 \n2004-8-2\t \n2004-8-3 \n2004-8-2\t \n2004-8-3\r\n
		StringBuffer heads=new StringBuffer();	// 列头
		
		java.util.Date date=YssFun.parseDate(this.date, "yyyy-MM-dd");
		int workDayCount=21;			//要循环天数
		BaseOperDeal operDeal=new BaseOperDeal();
		operDeal.setYssPub(pub);
		java.util.Date [] dates=new java.util.Date[21];	//21个工作日		
		String [] strDates=new String[21];				//以字符串形式
		
		//StringBuffer leftContent = new StringBuffer();						//证券数据内容//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		StringBuffer middleContent = new StringBuffer();					//汇总待补卖数据内容		
		//StringBuffer rightContent = new StringBuffer();					//日待补卖数据内容//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		StringBuffer content = new StringBuffer();			//数据内容
		
		String sql="select * from "+pub.yssGetTableName("Tb_ETF_Param")+" where FPortcode="+dbl.sqlString(this.portCode); //查找节假日群代码
		
		try {
			rs = dbl.openResultSet(sql);
			if(rs.next()){
				String holidayCode=rs.getString("FCROSSHOLIDAYSCODE");	//节假日代码
				
				dbl.closeResultSetFinal(rs);
				
				if(holidayCode.trim().length()==0)
					return "";
				
				boolean isWorkDay=operDeal.isWorkDay(holidayCode, date, 0);
				
				workDayCount=isWorkDay ? 20 : 21 ; //设置获取工作日天数
				
				if(isWorkDay)
					dates[dates.length-1]= date;
				
				
				java.util.Date tempDate = date;		//赋值 初始 循环获取工作日
				
				//获取工作日
				for (int i = workDayCount-1; i >=0 ; i--) {					
					tempDate = operDeal.getWorkDay(holidayCode, tempDate, -1);
					dates[i]=tempDate;					
				}
				
				//转为字符串 工作日
				
				for (int i = 0; i < dates.length; i++) {
					String temp=getStringByDate(dates[i],"yyyy-MM-dd");
					strDates[i]=temp;
					heads.append(" \n\t \n"+temp + "\t");			//工作日列头
				}				
				
				ArrayList securityCodeList=new ArrayList();	//所有的证券代码
				ArrayList securityNameList=new ArrayList();	//所有的证券名称
				sql="select etf_sb.FSecurityCode as FSecurityCode, ps.FSecurityName as FSecurityName from"
						+" (select FSecurityCode from "+pub.yssGetTableName("Tb_ETF_StandingBook")
						+" where FPortCode=" + dbl.sqlString(this.portCode)
						+" group by FSecurityCode order by FSecurityCode) etf_sb"
						+" left join "+pub.yssGetTableName("Tb_Para_Security")+" ps"
						+" on etf_sb.FSecurityCode = ps.FSecurityCode";
				
				rs = dbl.openResultSet(sql);
				while(rs.next()){
					securityCodeList.add(rs.getString("FSecurityCode").trim());
					securityNameList.add(rs.getString("FSecurityName").trim());
				}
				dbl.closeResultSetFinal(rs);
				
				boolean color = false;
				
				for (int si = 0; si < securityCodeList.size(); si++) {
					
					double totalTreatPath = 0;	//汇总待补
					double totalTreatSale = 0;	//汇总待卖
										
					color = !color;
					
					String sColor = "";
					
					int row = si;
					int column1 = 0;
					
					
					if(color)
						sColor=row+"~"+column1+"~  ~0~0~0~0~0~0~0~0~16777215~0~宋体~11~0~0~,,2,1,1~~~0\n";
					else
						sColor=row+"~"+column1+1+"~  ~0~0~0~0~0~0~0~0~16777166~0~宋体~11~0~0~,,2,1,1~~~0\n";
						
					content.append(sColor+securityCodeList.get(si).toString() + "\t" + sColor+securityNameList.get(si)+ "\t");
					
					StringBuffer daysContent=new StringBuffer();
					
					for (int di = 0; di < strDates.length; di++) {
						
						double dayTreatPath = 0;	//日待补
						double dayTreatSale = 0;	//日待卖
						
						int column2 = di + 4 ;
						
						sql="select * from "+pub.yssGetTableName("Tb_ETF_StandingBook")
							+ " where FBuyDate =to_date("+dbl.sqlString(strDates[di])+",'yyyy-MM-dd')"
							+ " and (FStockHolderCode=' ' or FBrokerCode is null) "
							+ " and FPortCode=" + dbl.sqlString(this.portCode)
							+ " and FSecurityCode="+dbl.sqlString(securityCodeList.get(si).toString())
							+ " order by Fbuydate,Fportcode,Fsecuritycode,FBS";
						
						rs=dbl.openResultSet(sql);
						
						while(rs.next()){							
							String tempFBS = "" ;
							double tempTotle = 0 ;
							
							tempFBS=rs.getString("FBS").trim();

							double MakeUpAmount = YssD.round(rs.getDouble("FMakeUpAmount"), 0);	//申赎 数量								
							double RealAmount = YssD.round(rs.getDouble("FRealAmount"),0);		//实际数量 权益数据
							double MakeUpAmount1 = YssD.round(rs.getDouble("FMakeUpAmount1"),0);	//1、补票数量
							double MakeUpAmount2 = YssD.round(rs.getDouble("FMakeUpAmount2"),0);	//2、补票数量
							double MakeUpAmount3 = YssD.round(rs.getDouble("FMakeUpAmount3"),0);	//3、补票数量
							double MakeUpAmount4 = YssD.round(rs.getDouble("FMakeUpAmount4"),0);	//4、补票数量
							double MakeUpAmount5 = YssD.round(rs.getDouble("FMakeUpAmount5"),0);	//5、补票数量
							double MustMkUpAmount = YssD.round(rs.getDouble("FMustMkUpAmount"),0);	//强制处理数量
							
							tempTotle = YssD.sub(YssD.add(MakeUpAmount,RealAmount), YssD.add(MakeUpAmount1, MakeUpAmount2,MakeUpAmount3,MakeUpAmount4,MakeUpAmount5,MustMkUpAmount));
							
							if(tempFBS.equalsIgnoreCase("B")){	//待补
								dayTreatPath = tempTotle;								
							}else if(tempFBS.equalsIgnoreCase("S")){	//待卖
								dayTreatSale = tempTotle;																
							}
						}
						dbl.closeResultSetFinal(rs);
						
						if(color)
							sColor = row + "~" +column2 + "~  ~0~0~0~0~0~0~0~0~16777215~0~宋体~11~8~0~,,0,1,1~~~0\n";
						else
							sColor = row + "~" +column2 + "~  ~0~0~0~0~0~0~0~0~16777166~0~宋体~11~8~0~,,0,1,1~~~0\n";
						
						daysContent.append(sColor+(Math.round(dayTreatPath))+"\t"+sColor+(Math.round(Math.abs(dayTreatSale)))+"\t");
						
						totalTreatPath = YssD.add(totalTreatPath, dayTreatPath);
						totalTreatSale=YssD.add(totalTreatSale, dayTreatSale);						
					}
					
					if(color)
						sColor=row+"~"+column1+2+"~  ~0~0~0~0~0~0~0~0~16777215~0~宋体~11~8~0~,,0,1,1~~~0\n";
					else
						sColor=row+"~"+column1+3+"~  ~0~0~0~0~0~0~0~0~16777166~0~宋体~11~8~0~,,0,1,1~~~0\n";
					
					middleContent.append(sColor+(Math.round(totalTreatPath)) + "\t" + sColor + (Math.round(Math.abs(totalTreatSale))) + "\t");
					if(daysContent.length() > 0)
						daysContent.setLength(daysContent.length()-1);					
					middleContent.append(daysContent);
					content.append(middleContent+"\r\n");
					
					middleContent.setLength(0);	//清空中间待补待卖汇总数据
					daysContent.setLength(0);	//清空日数据
				}
			}
			
			if(heads.length() > 0)
				heads.setLength(heads.length()-1);
								
			heads.append("\f\n-1,-1,0,-1\f\f").append(content);
			
		} catch (Exception e) {
			throw new YssException("查询应补、应卖股票余额表： \n" + e.getMessage());
		}		
		return heads.toString();
	}	
	
	/*
	 * 日期转字符串yyyy-MM-dd格式
	 * */
	private String getStringByDate(Date date,String format) throws YssException {
		String sDate = new SimpleDateFormat(format).format(date);
		return sDate;
	}
}
