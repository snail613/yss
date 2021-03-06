package com.yss.main.operdeal.report.reptab;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import com.yss.main.operdeal.BaseOperDeal;

import com.yss.base.BaseAPOperValue;
/**
 * add by zhouxiang at 2010.11.11
 */
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
public class TabJyCash extends BaseAPOperValue {
	   private java.util.Date dStartDate;//查询的第一天
	   private String portCode="";//组合代码
	   private String holidayCode="";//节假日群代码
	   private java.util.Date date0;//前一日
	   private ArrayList dateList=new ArrayList();
	   public TabJyCash() {
	   }

	   public void init(Object bean) throws YssException {
	      String reqAry[] = null;
	      String reqAry1[] = null;
	      String sRowStr = (String) bean;
	      if (sRowStr.trim().length() == 0)return;
	      reqAry = sRowStr.split("\n");
	      reqAry1 = reqAry[0].split("\r");
	      this.dStartDate = YssFun.toDate(reqAry1[1]);
	      reqAry1 = reqAry[1].split("\r");
	      this.portCode = reqAry1[1];
	      reqAry1 = reqAry[2].split("\r");
	      this.holidayCode = reqAry1[1];
	     
	   }

	   public Object invokeOperMothed() throws YssException {
		   checkWorkDay();
	       createTmpTable();//创建交银所需要的表头临时表和数据临时表
	       setHeaderCash();//填写表头数据
	       setCashForeTable();//填写交银应该查询出的数据DATA
	       return "";
	   }

	   protected void createTmpTable() throws YssException {
		String strSql = "";
		try {
			if (dbl.yssTableExist(pub.yssGetTableName("tb_Temp_repHeaderdate_Rep")))  // 创建交银现金头寸的日期临时存储表
					 {
				dbl.executeSql("delete from "
						+ pub.yssGetTableName("tb_Temp_repHeaderdate_Rep"));
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("tb_Temp_repHeaderdate_Rep")
						+ "(cashcode  varchar2(50),"
						+ "cashname  varchar2(50)," + "yesterday number(20,4),"
						+ "todayin1  date," + "todayout1 number(20,4),"
						+ "todayval1 number(20,4)," + "todayin2  date,"
						+ "todayout2 number(20,4)," + "todayval2 number(20,4),"
						+ "todayin3  date," + "todayout3 number(20,4),"
						+ "todayval3 number(20,4)," + "todayin4  date,"
						+ "todayout4 number(20,4)," + "todayval4 number(20,4),"
						+ "todayin5  date," + "todayout5 number(20,4),"
						+ "todayval5 number(20,4)," + "todayin6  date,"
						+ "todayout6 number(20,4)," + "todayval6 number(20,4),"
						+ "todayin7  date," + "todayout7 number(20,4),"
						+ "todayval7 number(20,4))";
				dbl.executeSql(strSql);
			}
			if (dbl.yssTableExist(pub.yssGetTableName("tb_Temp_repCenterdata_Rep")))  // 创建交银现金头寸的数据存储表
				 {
				/**shashijie ,2011-10-12 , STORY 1698*/
				dbl.executeSql(dbl.doOperSqlDrop("drop table "
 						+ pub.yssGetTableName("tb_Temp_repCenterdata_Rep")));
				/**end*/
				 }
				strSql = "create table "
						+ pub.yssGetTableName("tb_Temp_repCenterdata_Rep")
						+ "(cashcode  varchar2(50),"
						+ "cashname  varchar2(50)," + "yesterday number(20,4),"
						+ "todayin1  number(30,4)," + "todayout1 number(20,4),"
						+ "todayval1 number(30,4)," + "todayin2  number(20,4),"
						+ "todayout2 number(30,4)," + "todayval2 number(20,4),"
						+ "todayin3  number(30,4)," + "todayout3 number(20,4),"
						+ "todayval3 number(30,4)," + "todayin4  number(20,4),"
						+ "todayout4 number(30,4)," + "todayval4 number(20,4),"
						+ "todayin5  number(30,4)," + "todayout5 number(20,4),"
						+ "todayval5 number(30,4)," + "todayin6  number(20,4),"
						+ "todayout6 number(30,4)," + "todayval6 number(20,4),"
						+ "todayin7  number(30,4)," + "todayout7 number(20,4),"
						+ "todayval7 number(30,4)," + "LineNuber number(1))";
				dbl.executeSql(strSql);
			
		} catch (Exception e) {
			throw new YssException("生成交银现金头寸预测表（本币）临时表出错");
		}
	   }

	   protected void setCashForeTable() throws YssException {
		   String primary_CNY_Cash=getAccByCuryAndGrade("CNY","primary");//主人民币账户
		   String primary_CNY_Cashcode=" ";
		   String primary_HKD_Cash=getAccByCuryAndGrade("HKD","primary");//主港币账户
		   String primary_HKD_Cashcode=" ";
		   String secondary_HKD_Cash=getAccByCuryAndGrade("HKD","secondary");//次港币账户
		   ArrayList arraylist=new ArrayList(); //用来存储一周的数据，其中arrylist[0]的余额用来存储昨日的余额
		   
		   ArrayList arraylist1=new ArrayList();//人民币合计
		   TodayData sumData=new TodayData();
		   arraylist1.add(sumData);
		   for(int i=1;i<=7;i++){//合计金额的初始化
			   TodayData tempData=new TodayData();
			   arraylist1.add(tempData);
		   }
		   
		   double yesterday;
		   if(primary_CNY_Cash.trim().length()>0){
			   primary_CNY_Cashcode=primary_CNY_Cash.split("\t")[0];
			   String primary_CNY_Cashname=primary_CNY_Cash.split("\t")[1];
			   yesterday=getYesterdayBalanceByAccount(primary_CNY_Cashcode,this.date0);//第一天的昨日余额
			   
			   ((TodayData)arraylist1.get(0)).todayval=YssD.add(((TodayData)arraylist1.get(0)).todayval,yesterday);//合计昨日余额
			   
			   TodayData todaydata=new TodayData();
			   yesterday=YssD.round(yesterday, 2);
			   todaydata.todayval=yesterday;
			   arraylist.add(todaydata);//首先设置昨日余额
			   for(int i=1;i<=7;i++){
				 //modify huangqirong 2011-07-25 Story #1206
				   //todaydata=getTodayDataByCuryAndCashcode(primary_CNY_Cashcode,"CNY",(Date)this.dateList.get(i));
				   todaydata= get_PHKD_TodayDataInOrOutByCuryAndCashcode(primary_CNY_Cashcode,(Date)this.dateList.get(i));
				   //---end---
				   todaydata.todayval=YssD.sub(YssD.add(((TodayData)(arraylist.get(i-1))).todayval, todaydata.todayin), todaydata.todayout);
				   todaydata.todayval=YssD.round(todaydata.todayval, 2);
				   todaydata.todayin=YssD.round(todaydata.todayin, 2);
				   todaydata.todayout=YssD.round(todaydata.todayout, 2);
				   //今日的余额=前一日的余额+今天的流入-今天的流出
				   
				   ((TodayData)arraylist1.get(i)).todayval=YssD.round(YssD.add(((TodayData)arraylist1.get(i)).todayval,todaydata.todayval),2);//当日余额的合计
				   arraylist.add(todaydata);
			   }
			   insertToTmpDataTable(arraylist,primary_CNY_Cashcode,primary_CNY_Cashname,1);
			   arraylist.clear();
			  
		   }
		   if(primary_HKD_Cash.trim().length()>0){
			   double baseRate=0;
			   double baseCNYRate=0;
			   primary_HKD_Cashcode=primary_HKD_Cash.split("\t")[0];
			   String primary_HKD_Cashname=primary_HKD_Cash.split("\t")[1];
			   yesterday=getYesterdayBalanceByAccount(primary_HKD_Cashcode,this.date0);//第一天的昨日余额
			   TodayData todaydata=new TodayData();
			   yesterday=YssD.round(yesterday, 2);
			   todaydata.todayval=yesterday;
			   arraylist.add(todaydata);//首先设置昨日余额
			   for(int i=1;i<=7;i++){
				 //modify huangqirong 2011-07-25 Story #1206
				   //todaydata=get_PHKD_TodayDataByCuryAndCashcode(primary_HKD_Cashcode,(Date)this.dateList.get(i));
				   todaydata=get_PHKD_TodayDataInOrOutByCuryAndCashcode(primary_HKD_Cashcode,(Date)this.dateList.get(i));
				   //---end---
				   todaydata.todayval=YssD.sub(YssD.add(((TodayData)(arraylist.get(i-1))).todayval, todaydata.todayin), todaydata.todayout);
				   todaydata.todayval=YssD.round(todaydata.todayval, 2);
				   todaydata.todayin=YssD.round(todaydata.todayin, 2);
				   todaydata.todayout=YssD.round(todaydata.todayout, 2);
				   //今日的余额=前一日的余额+今天的流入-今天的流出
				   arraylist.add(todaydata);
				   
				   baseRate=this.getSettingOper().getCuryRate((Date)this.dateList.get(i), "HKD", this.portCode, YssOperCons.YSS_RATE_BASE);//获取当日的账户对基础货币的基础汇率
				   baseCNYRate=this.getSettingOper().getCuryRate((Date)this.dateList.get(i), "CNY", this.portCode, YssOperCons.YSS_RATE_BASE);//获取港币对基础货币的基础汇率
				   double tempmoney=YssD.div(YssD.mul(todaydata.todayval, baseRate),baseCNYRate);
				   ((TodayData)arraylist1.get(i)).todayval=YssD.round(YssD.add(((TodayData)arraylist1.get(i)).todayval,tempmoney),2);//当日余额的合计
				   
			   }
			   insertToTmpDataTable(arraylist,primary_HKD_Cashcode,primary_HKD_Cashname,2);
			   arraylist.clear();
			  
		   }
		   if(secondary_HKD_Cash.trim().length()>0){
			   double statebaseCNYRate=0;
			   String secondary_HKD_Cashcode=secondary_HKD_Cash.split("\t")[0];//次港币账户
			   String secondary_HKD_Cashname=secondary_HKD_Cash.split("\t")[1];//次港币账户
			   ResultSet rs=null;
			   String sqlStr="";
			   String searchAcccode="";//单步操作的现金账户ID
			   String searchCruy="";//单步操纵的账户币种代码
			   try{
				   sqlStr = "select Fcashacccode,Fcashaccname,fcurycode  from "
						+ pub.yssGetTableName("tb_para_cashaccount")
						+ " where facctype = '01'   and fsubacctype = '0101' and  fportcode=" +dbl.sqlString(this.portCode)
						+ "and fcheckstate=1 and fcashacccode not in(  select Fcashacccode"
						+"  from "+pub.yssGetTableName("tb_para_cashaccount")
						+" where facctype = '01' and fsubacctype = '0101' and fcurycode in( 'CNY','HKD')" 
						+" and fbankcode in (select fsubcode  from "+pub.yssGetTableName("tb_para_portfolio_relaship")
						+" where frelatype = 'Trustee' and fportcode = "+dbl.sqlString(this.portCode)
                        +" and frelagrade = 'primary'))"
						+ " and fbankcode in (select fsubcode from "
						+ pub.yssGetTableName("tb_para_portfolio_relaship")
						+ " where frelatype = 'Trustee' and fportcode = "
						+ dbl.sqlString(this.portCode) + " and frelagrade = "
						+ dbl.sqlString("secondary") + ")";
				   rs=dbl.openResultSet(sqlStr);//对次托银行下的账户循环处理
				   while(rs.next()){
					   searchAcccode=rs.getString("Fcashacccode");
					   searchCruy=rs.getString("fcurycode");
					   double baseRate=this.getSettingOper().getCuryRate(this.date0, searchCruy, this.portCode, YssOperCons.YSS_RATE_BASE);
					   double baseHKDRate=this.getSettingOper().getCuryRate(this.date0, "HKD", this.portCode, YssOperCons.YSS_RATE_BASE);
					   //获取该账户对应组合下的的基础汇率 即：账户金额*基础汇率=该组合的基础货币，如果是组合汇率则：账户金额=组合汇率*基础货币
					   yesterday=YssD.div(YssD.mul(getYesterdayBalanceByAccount(searchAcccode,this.date0), baseRate), baseHKDRate);
					   //次托港币第一天的昨日余额*账户币种兑基础货币的基础汇率/港币对基础货币的基础汇率==港币的金额
					   TodayData todaydata=new TodayData(); //首先设置昨日余额
					   yesterday=YssD.round(yesterday, 2);
					   todaydata.todayval=yesterday;
					   arraylist.add(todaydata);
					   for(int i=1;i<=7;i++){
						   //modify huangqirong 2011-07-25 Story #1206
						   //todaydata=get_PHKD_TodayDataByCuryAndCashcode(searchAcccode,(Date)this.dateList.get(i));//获取账户一天的交易数据
						   todaydata=get_PHKD_TodayDataInOrOutByCuryAndCashcode(searchAcccode,(Date)this.dateList.get(i));
						   //---end---
						   baseRate=this.getSettingOper().getCuryRate((Date)this.dateList.get(i), searchCruy, this.portCode, YssOperCons.YSS_RATE_BASE);//获取当日的账户对基础货币的基础汇率
						   baseHKDRate=this.getSettingOper().getCuryRate((Date)this.dateList.get(i), "HKD", this.portCode, YssOperCons.YSS_RATE_BASE);//获取港币对基础货币的基础汇率
						   
						   todaydata.todayin=YssD.div(YssD.mul(todaydata.todayin, baseRate), baseHKDRate);//将流入流出转换为人民币
						   todaydata.todayout=YssD.div(YssD.mul(todaydata.todayout, baseRate), baseHKDRate);
						   
						   todaydata.todayval=YssD.sub(YssD.add(((TodayData)(arraylist.get(i-1))).todayval, todaydata.todayin), todaydata.todayout);//计算当日的余额
						   
						   todaydata.todayval=YssD.round(todaydata.todayval, 2);
						   todaydata.todayin=YssD.round(todaydata.todayin, 2);
						   todaydata.todayout=YssD.round(todaydata.todayout, 2);
						   //今日的余额=前一日的余额+今天的流入-今天的流出
						   arraylist.add(todaydata);
						   
						   //余额实现港币转换为人民币
						   statebaseCNYRate=this.getSettingOper().getCuryRate((Date)this.dateList.get(i), "CNY", this.portCode, YssOperCons.YSS_RATE_BASE);//获取港币对基础货币的基础汇率
						   double tempMoney=YssD.div(YssD.mul(todaydata.todayval, baseHKDRate),statebaseCNYRate);
						   ((TodayData)arraylist1.get(i)).todayval=YssD.round(YssD.add(((TodayData)arraylist1.get(i)).todayval,tempMoney),2);//当日余额的合计
					   }
					   insertToTmpDataTable(arraylist,secondary_HKD_Cashcode,secondary_HKD_Cashname,3);
					   arraylist.clear();
				   }
				   dbl.closeResultSetFinal(rs);
			   }catch(Exception e){
				   throw new YssException("次托港币账户信息生成出错");
			   }
			}
		   insertToTmpDataTable(arraylist1,"合计",". 折算人民币小计",4);
		   
	   }
	   
	   /**
	    * add by huangqirong 2011-07-25 story #1206
	    * 根据账号和时间 获取资金调拨数据
	    * */
	   private TodayData get_PHKD_TodayDataInOrOutByCuryAndCashcode(
				String primaryCNYCashcode,  Date date) throws YssException {
			TodayData todaydata=new TodayData();
			ResultSet rs=null;
			StringBuffer sqlStr=new StringBuffer("");
			try{
			  sqlStr.append("select FIn,FOut from")
			  		.append("(select a.FCashAccCode, nvl(sum(fmoney),0) as FIn from ")
			  		.append("(select * from "+pub.yssGetTableName("Tb_Cash_SubTransfer"))
					.append(" where FCashAccCode="+dbl.sqlString(primaryCNYCashcode)+" and FInOut=1 and FCHECKSTATE=1 and FPortCode="+dbl.sqlString(this.portCode))
					.append(")a join(select * from ")
					.append(pub.yssGetTableName("Tb_Cash_Transfer"))					
					.append(" where FTransferDate="+dbl.sqlDate(date)+" and FCHECKSTATE=1)c on a.FNum=c.FNum group by a.FCashAccCode) i ");
							
			  sqlStr.append(" full join (select b.FCashAccCode,nvl(sum(fmoney),0) as FOut from ")
					.append(" (select * from "+pub.yssGetTableName("Tb_Cash_SubTransfer"))					
					.append(" where FCashAccCode="+dbl.sqlString(primaryCNYCashcode)+" and FInOut=-1 and FPortCode="+dbl.sqlString(this.portCode)+" and FCHECKSTATE=1) b")		     
			    	.append(" join(select * from "+pub.yssGetTableName("Tb_Cash_Transfer")+" where FTransferDate="+dbl.sqlDate(date)+" and FCHECKSTATE=1) d")
			    	.append(" on b.FNum=d.FNum group by b.FCashAccCode) o")
			    	.append(" on i.FCashAccCode=o.FCashAccCode");
					rs=dbl.openResultSet(sqlStr.toString());
					if(rs.next()){
						todaydata.todayout=rs.getDouble("FOut");
						todaydata.todayin=rs.getDouble("FIn");
					}
					return todaydata;
			}catch(Exception e){
				throw new YssException("获取资金调拨数据出错");
			}finally{
				dbl.closeResultSetFinal(rs);
			}
		}
	  

	private TodayData get_PHKD_TodayDataByCuryAndCashcode(
			String primaryCNYCashcode,  Date date) throws YssException {
		TodayData todaydata=new TodayData();
		ResultSet Subrs=null;
		String sqlStr="";
		try{
			sqlStr = "select nvl(sum(ftodayout1),0) as ftodayout, nvl(sum(ftodayin1),0) as ftodayin from ("+
					" select b.ftodayout1, c.ftodayin1  from dual a,"+
					
					"(select sum(FFactSettleMoney) as ftodayout1 from "+//流出
					pub.yssGetTableName("Tb_Data_SubTrade")+
					" where FTradeTypeCode = '01' and fcheckstate = 1 and fcashacccode ="+
					dbl.sqlString(primaryCNYCashcode)+
					" and FSettleDate ="+dbl.sqlDate(date)+	//--交易数据：股票买入01、基金买入、债券买入、)
					") b,(select sum(FFactSettleMoney) as ftodayin1 from "+//流入
					pub.yssGetTableName("Tb_Data_SubTrade")+
					" where FTradeTypeCode in ('79', '02', '06', '17') and fcashacccode = "+
					dbl.sqlString(primaryCNYCashcode)+
					" and FSettleDate = "+dbl.sqlDate(date)+ //交易数据：买断式卖出回购79）、股票卖出02、基金卖出、债券卖出、股票分红06、债券兑付17
					") c union select   b.Ftodayout1, c.Ftodayin1 from dual a,"+                                             //外汇交易
					" (select sum(FSMoney) as ftodayout1 from "+pub.yssGetTableName("Tb_Data_RateTrade")+
					" where FSCashAccCode = "+dbl.sqlString(primaryCNYCashcode)+ //流出 卖出账户 为对应币种账户
					" and fcheckstate = 1  and FSettleDate = "+dbl.sqlDate(date)+
					") b,  (select sum(FBMoney) as ftodayin1 from "+
					pub.yssGetTableName("Tb_Data_RateTrade")+
					" where FBCashAccCode = "+dbl.sqlString(primaryCNYCashcode)+ //流入 买入账户 为改对应币种账户
					" and fcheckstate = 1  and FSettleDate = "+dbl.sqlDate(date)+") c)";
				Subrs=dbl.openResultSet(sqlStr);
				if(Subrs.next()){
					todaydata.todayout=Subrs.getDouble("ftodayout");
					todaydata.todayin=Subrs.getDouble("ftodayin");
				}
				dbl.closeResultSetFinal(Subrs);
				return todaydata;
				}catch(Exception e){
					throw new YssException("生成现金头寸预测表出错!查询TA交易数据表，交易数据表,外汇交易表出错");
				}
				
		
	}

	/** 
	    * @param arraylist
	    * @param primaryCNYCashcode
	    * @throws YssException 
	    * @throws SQLException 
	    */
	   private void insertToTmpDataTable(ArrayList arraylist,
			String primaryCNYCashcode,String acccountName,int number) throws YssException {
		   ResultSet rs=null;
		   String sqlStr="";
		   Connection cn=null;
		   try{
			   cn=dbl.loadConnection();
			   cn.setAutoCommit(false);
			sqlStr = "insert into "
					+ pub.yssGetTableName("tb_Temp_repCenterdata_Rep")
					+ " (CASHCODE, CASHNAME, YESTERDAY, TODAYIN1, TODAYOUT1, TODAYVAL1, "
					+ "TODAYIN2, TODAYOUT2, TODAYVAL2, TODAYIN3, TODAYOUT3, TODAYVAL3, TODAYIN4, TODAYOUT4, TODAYVAL4, "
					+ "TODAYIN5, TODAYOUT5, TODAYVAL5, TODAYIN6, TODAYOUT6, TODAYVAL6, TODAYIN7, TODAYOUT7, TODAYVAL7,Linenuber)"
					+ " values (" + dbl.sqlString(primaryCNYCashcode)
					+ ", "+dbl.sqlString(acccountName)+", " + ((TodayData) arraylist.get(0)).todayval + ", "
					+ ((TodayData) arraylist.get(1)).todayin + ", "
					+ ((TodayData) arraylist.get(1)).todayout + ", "
					+ ((TodayData) arraylist.get(1)).todayval + ", "
					+ ((TodayData) arraylist.get(2)).todayin + ", "
					+ ((TodayData) arraylist.get(2)).todayout + ", "
					+ ((TodayData) arraylist.get(2)).todayval + ", "
					+ ((TodayData) arraylist.get(3)).todayin + ", "
					+ ((TodayData) arraylist.get(3)).todayout + ", "
					+ ((TodayData) arraylist.get(3)).todayval + ", "
					+ ((TodayData) arraylist.get(4)).todayin + ", "
					+ ((TodayData) arraylist.get(4)).todayout + ", "
					+ ((TodayData) arraylist.get(4)).todayval + ", "
					+ ((TodayData) arraylist.get(5)).todayin + ", "
					+ ((TodayData) arraylist.get(5)).todayout + ", "
					+ ((TodayData) arraylist.get(5)).todayval + ", "
					+ ((TodayData) arraylist.get(6)).todayin + ", "
					+ ((TodayData) arraylist.get(6)).todayout + ", "
					+ ((TodayData) arraylist.get(6)).todayval + ", "
					+ ((TodayData) arraylist.get(7)).todayin + ", "
					+ ((TodayData) arraylist.get(7)).todayout + ", "
					+ ((TodayData) arraylist.get(7)).todayval + ","
					+number+")";
			dbl.executeSql(sqlStr);
			cn.setAutoCommit(true);
			cn.commit();
			}catch(Exception e){
			   throw new YssException("插入现金头寸预测表明细数据出错");
		   }
		   // TODO Auto-generated method stub
		
	}

	/***
	    * @param primaryCNYCashcode 用户
	    * @param string       
	    * @param date
	    * @return
	 * @throws YssException 
	    */
	   private TodayData getTodayDataByCuryAndCashcode(String primaryCNYCashcode,
			String string, Date date) throws YssException {
		TodayData todaydata=new TodayData();
		ResultSet Subrs=null;
		String sqlStr="";
		try{
			sqlStr = "select "
					+ dbl.sqlString(primaryCNYCashcode)
					+ ", nvl(sum(Ftodayout1),0) as ftodayout, nvl(sum(ftodayin1),0) as ftodayin"
					

					+" from ( select "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " as Fcashacccode, b.Ftodayout1, c.Ftodayin1"
					+ " from dual a,"			// TA交易数据（申购 01 流入 赎回02 流出）
					+ " (select sum(FSettleMoney) as Ftodayout1  from "
					+ pub.yssGetTableName("Tb_TA_Trade")
					+ " where fselltype = '02' and Fcashacccode = "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " and Fcurycode = 'CNY'  and fcheckstate = 1 and FSettleDate = "
					+ dbl.sqlDate(date)
					+ ") b, (select sum(FSettleMoney) as Ftodayin1  from "
					+ pub.yssGetTableName("Tb_TA_Trade")
					+ " where Fcurycode = 'CNY' and Fcashacccode ="
					+ dbl.sqlString(primaryCNYCashcode)
					+ " and fselltype = '01' and fcheckstate = 1 and FSettleDate = "
					+ dbl.sqlDate(date)
					+ ") c union"
					

					+" select "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " as Fcashacccode, b.Ftodayout1, c.Ftodayin1"
					+ " from dual a, (select sum(FFactSettleMoney) as ftodayout1 " // 流出
					+" from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FTradeTypeCode in ('25', '78', '01')  and Fcashacccode = "// 交易数据（回购（逆回购 25、买断式买入回购78）、股票买入01、基金买入01、债券买入01、）流出
					+ dbl.sqlString(primaryCNYCashcode)
					+ " and fcheckstate = 1 and FSettleDate = "
					+ dbl.sqlDate(date)
					+ ") b,"
					
					+" (select sum(FFactSettleMoney) as ftodayin1"
					+ // 流入交易数据：正回购24、逆回购到期10、买断式卖出回购79）、卖出02、股票分红06、债券兑付17
					" from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FTradeTypeCode in ('24', '10', '79', '02', '06', '17')"
					+ " and Fcashacccode = "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " and fcheckstate = 1"
					+ " and FSettleDate =  "
					+ dbl.sqlDate(date)
					+ ") c union"
					
				   
					+" select "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " as Fcashacccode, b.Ftodayout1, c.Ftodayin1"
					+ " from dual a, (select sum(FFactSettleMoney) as ftodayout1 " //正回购24到期结算日期，资金流出 
					+" from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FTradeTypeCode in ('24')  and Fcashacccode = "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " and fcheckstate = 1 and FMatureSettleDate = "
					+ dbl.sqlDate(date)
					+ ") b,"
					
					+" (select sum(FFactSettleMoney) as ftodayin1"//逆回购25回购到期，资金流入
					+" from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FTradeTypeCode in ('25')"
					+ " and Fcashacccode = "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " and fcheckstate = 1"
					+ " and FMatureSettleDate =  "
					+ dbl.sqlDate(date)
					+ ") c union"
					
					
					+ " select "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " as Fcashacccode, b.Ftodayout1, c.Ftodayin1  from dual a,"
					+ " (select sum(FSMoney) as ftodayout1  from " // 外汇交易
					+ pub.yssGetTableName("Tb_Data_RateTrade")
					+ " where FSCashAccCode = "// 流出 卖出账户 为该人民币账户
					+ dbl.sqlString(primaryCNYCashcode)
					+ " and fcheckstate = 1 and FSettleDate = "
					+ dbl.sqlDate(date)
					+ ") b,(select sum(FBMoney) as ftodayin1  from "
					+ pub.yssGetTableName("Tb_Data_RateTrade")
					+ " where FBCashAccCode = "
					+ dbl.sqlString(primaryCNYCashcode)
					+ " and fcheckstate = 1 and FSettleDate = "
					+ dbl.sqlDate(date) + ") c)";
				Subrs=dbl.openResultSet(sqlStr);
				if(Subrs.next()){
					todaydata.todayout=Subrs.getDouble("ftodayout");
					todaydata.todayin=Subrs.getDouble("ftodayin");
				}
				dbl.closeResultSetFinal(Subrs);
				return todaydata;
				}catch(Exception e){
					throw new YssException("生成现金头寸预测表出错!查询TA交易数据表，交易数据表,外汇交易表出错");
				}
	}

	public double getYesterdayBalanceByAccount(String account,Date date) throws YssException{//根据现金账户和日期查找对应的昨日余额
		String sqlStr = "";
		ResultSet Subrs = null;
		double reStr = 0;
		try {
			sqlStr = "select Faccbalance, max(FstorageDate) as FstorageDate from "
					+ pub.yssGetTableName("Tb_Stock_Cash")
					+ " where fcashacccode = "
					+ dbl.sqlString(account)
					+ "and FstorageDate <= "
					+ dbl.sqlDate(date)
					+ " group by Faccbalance order by FstorageDate desc";
			Subrs=dbl.openResultSet(sqlStr);
			if(Subrs.next()){
				reStr=Subrs.getDouble("Faccbalance");
			}
			dbl.closeResultSetFinal(Subrs);
		} catch (Exception e) {
			throw new YssException("生成交银现金头寸数据报错!");
		}
		return reStr;
	   }
	   
	   /***
	    * @param cury:币种代码 Grade : 托管行在组合设置中的托管人设置分页的等级（主或者次）
	    * @return 返回要查找的特定住托管行下的账户
	    * @throws YssException 
	    * @说明：使用此方法是为了查找现金账户为：账户类型为存款账户，账户子类型为活期存款账户，且开户银行为组合设置中，托管人界面中关联设置为主托管行的账户
	    */
	   public String getAccByCuryAndGrade(String cury,String Grade) throws YssException {
		ResultSet rs = null;
		String sqlStr = "";
		try {
			sqlStr = "select Fcashacccode,Fcashaccname,fcurycode  from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where facctype = '01'   and fportcode=" +dbl.sqlString(this.portCode)
					+ " and fsubacctype = '0101' and fcheckstate=1  and fcurycode = "
					+ dbl.sqlString(cury)
					+ " and fbankcode in (select fsubcode from "
					+ pub.yssGetTableName("tb_para_portfolio_relaship")
					+ " where frelatype = 'Trustee' and fportcode = "
					+ dbl.sqlString(this.portCode) + " and frelagrade = "
					+ dbl.sqlString(Grade) + ")";
			rs = dbl.openResultSet(sqlStr);
			if (rs.next()) {
				return rs.getString("Fcashacccode")+"\t"+rs.getString("Fcashaccname")+"\t"+rs.getString("fcurycode");
			}
			dbl.closeResultSetFinal(rs);
		} catch (Exception e) {
			throw new YssException(e);
		}
		return "";
		   // TODO Auto-generated method stub
		
	}

	protected void setHeaderCash() throws YssException{
		BaseOperDeal dateOperDeal = new BaseOperDeal();
		dateOperDeal.setYssPub(this.pub);
		this.date0 = dateOperDeal.getWorkDay(this.holidayCode, this.dStartDate,
				-1);// 初始化需要显示的日期(一周)昨日
		this.dateList.add(this.date0);
		java.util.Date tempDate=new Date();
		tempDate=dateOperDeal.getWorkDay(this.holidayCode,this.date0, 1);//昨日的下一个工作日就是当天
		for(int i=1;i<=7;i++){
			this.dateList.add(tempDate);
			tempDate=dateOperDeal.getWorkDay(this.holidayCode, tempDate, 1);
		}
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "insert into "//将工作日一周的日期全部插入头寸表表中，方便取用作为表头
					+ pub.yssGetTableName("tb_Temp_repHeaderdate_Rep")
					+ "(CASHCODE, CASHNAME, YESTERDAY, TODAYIN1, TODAYOUT1, TODAYVAL1, "
					+ "TODAYIN2, TODAYOUT2, TODAYVAL2, TODAYIN3, TODAYOUT3, TODAYVAL3, "
					+ "TODAYIN4, TODAYOUT4, TODAYVAL4, TODAYIN5, TODAYOUT5, TODAYVAL5,"
					+ " TODAYIN6, TODAYOUT6, TODAYVAL6, TODAYIN7, TODAYOUT7, TODAYVAL7)"
					+ "values ('', '', null," + dbl.sqlDate((Date)this.dateList.get(1))+", null, null, "
					+ dbl.sqlDate((Date)this.dateList.get(2)) + ", null, null,"
					+ dbl.sqlDate((Date)this.dateList.get(3)) + ", null, null, "
					+ dbl.sqlDate((Date)this.dateList.get(4)) + ", null, null, "
					+ dbl.sqlDate((Date)this.dateList.get(5)) + ", null, null, "
					+ dbl.sqlDate((Date)this.dateList.get(6)) + ", null, null, "
					+ dbl.sqlDate((Date)this.dateList.get(7)) + ", null, null)";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	private class TodayData   
	{   
	       public double todayin;   
	       public double todayout;  
	       public double todayval;
	}  
	
	private void checkWorkDay() throws YssException{
		BaseOperDeal dateOperDeal = new BaseOperDeal();
		dateOperDeal.setYssPub(this.pub);
		if(!this.dStartDate.equals(dateOperDeal.getWorkDay(this.holidayCode, this.dStartDate, 0))){
			throw new YssException("该日期为节假日，请选择工作日生成头寸表！"); 
		}
	}
	 
}
