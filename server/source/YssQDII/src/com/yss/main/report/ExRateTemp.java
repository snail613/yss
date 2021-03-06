package com.yss.main.report;

import java.sql.*;
import com.yss.base.BaseAPOperValue;
import com.yss.util.*;

/**
 * <p>Title:投连交易费率测算表</p>
 * add by zhangjun
 * 2011-12-13  
 * STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用 
 **/
public class ExRateTemp extends BaseAPOperValue  {	
	private Object obj = null;
    private java.util.Date sDate = null; //开始日期
    private java.util.Date eDate = null;  //结束日期
    private String portCode = ""; //组合代码
    private String periodType = ""; // 期间类型
    
    private boolean isCreate;  //用于判断是否生成报表   	
	
	public ExRateTemp() {
    }
	
	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;

		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
		reqAry1 = reqAry[0].split("\r");
		this.sDate = YssFun.toDate(reqAry1[1]);
		reqAry1 = reqAry[1].split("\r");
		this.eDate = YssFun.toDate(reqAry1[1]);
		reqAry1 = reqAry[2].split("\r");
		this.portCode = reqAry1[1];
		
	    reqAry1 = reqAry[3].split("\r");
	    this.periodType = reqAry1[1];
	    reqAry1 = reqAry[4].split("\r");
	    if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0,则确认报表
		   this.isCreate = false; 
	    }else if(reqAry1[1].equalsIgnoreCase("1")) { //若为1,则 生成报表
	       this.isCreate = true;
	    }	

	}
	
	/**
     * 投连交易费率测算业务处理
     */
	public Object invokeOperMothed() throws YssException {
		
		try{
			if(isCreate){
				insExRateDeal(sDate,eDate,portCode,periodType);  //生成报表
			}else{
				doConfirmed(sDate,eDate,portCode,periodType);  //确认报表
			}
		}catch(Exception e){
			throw new YssException("处理投连交易费率测算报表出错......" + e.getMessage());
		}
		return null;
	}
	
	/**
     * 处理股票、权证、基金、债券交易费率
     */
	public void insExRateDeal(java.util.Date sDate, java.util.Date eDate, String sPortCode, String sPeriodType) throws
	YssException, SQLException {
		try{			
			//股权、权证	平均交易费率
			doAvgStockRate(sDate,eDate,sPortCode,sPeriodType);
			//权证平均交易费率处理
			//doAvgWarrantRate(sDate,eDate,sPortCode,sPeriodType); 权证数据的处理已放在股票处理方法中
			//债券平均交易费率
			doAvgBoundRate(sDate,eDate,sPortCode,sPeriodType);			
			//基金平均费率
			doAvgFundRate(sDate,eDate,sPortCode,sPeriodType);
			//合计项处理
			doTotalMethod(sDate,eDate,sPortCode,sPeriodType);
		}catch (Exception e) {
			throw new YssException("处理投连交易费率时出现异常！\n", e);			
		}
	}
	
	/**
     * 处理股票交易费率
     */
	public void doAvgStockRate(java.util.Date sDate, java.util.Date eDate, String sPortCode, String sPeriodType) throws
	YssException, SQLException {
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false; 	    
	    String strSql = "";	    
	    //ResultSet rSet = null;
	    ResultSet rs = null;	    
	    //ResultSet resultSet = null;
	    PreparedStatement pst = null;
		try{
			bTrans = true;
			strSql = " delete from " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
			         " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FEndDate = " + dbl.sqlDate(eDate) + 
			         " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FFlag = '0' and FCatCode in ('EQ') ";
			if (sPeriodType.equals("扩张期")){
	        	 strSql = strSql +" and FTradeType = '01' " ;
	         }else if (sPeriodType.equals("收缩期")){
	        	 strSql = strSql + " and FTradeType = '02' ";//市场和证券品种分组
	         } 	         
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
				     " (FStartDate,FEndDate,FPortCode,FPeriodType,FTradeType,FTradeMarket,FCatCode,FTradePrice," +   
				     "  FTradeFee,FAvgRate,FFlag)" +
				     " values(?,?,?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			//股票:'EQ'、权证：‘OP’
			strSql = " select FExchangeCode ,count(FExchangeCode) as Num ,sum(FTradeMoney) as FTradeMoney," +
				     " sum(FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 +FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8) as FTradeFee , " + 
				     " sum(AvgRate) as AvgRate  from " +
				     " (select FSecurityCode,FExchangeCode ,FTradeMoney,FTRADEFEE1,FTRADEFEE2,FTRADEFEE3,FTRADEFEE4, FTRADEFEE5,FTRADEFEE6,FTRADEFEE7, FTRADEFEE8," +
			         "  (case when  FTradeMoney <> 0  then " +
			         " (FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 + FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8)/FTradeMoney " +
			         "  else 0 end ) as AvgRate  from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a "  +
			         " join ( select FSecurityCode as FSecurityCode_b,FExchangeCode  from " + pub.yssGetTableName("Tb_Para_Security") + 
			         " where FExchangeCode in ('CG','CS') and FCatCode in ('EQ','OP') ) b on a.FSecurityCode = b.FSecurityCode_b " +
			         " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
			         " and FBargainDate >= " + dbl.sqlDate(sDate)+ " and FBargainDate <= " + dbl.sqlDate(eDate) ;
			         if (sPeriodType.equals("扩张期")){
			        	 strSql = strSql +" and FTradeTypeCode = '01' )" + " group by FExchangeCode ";
			         }else if (sPeriodType.equals("收缩期")){
			        	 strSql = strSql + " and FTradeTypeCode = '02' )" + " group by FExchangeCode ";//市场和证券品种分组
			         } 	         
			          
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next()){ 
				pst.setDate(1, YssFun.toSqlDate(this.sDate));
				pst.setDate(2, YssFun.toSqlDate(this.eDate));
				pst.setString(3, this.portCode);//组合代码
				pst.setString(4, this.periodType);
				if (sPeriodType.equals("扩张期")){
					pst.setString(5, "01");//交易类型代码 ：01:买入、02:卖出
				}else if (sPeriodType.equals("收缩期")){
					pst.setString(5, "02");
				}					
				//pst.setString(5, rs.getString("FTradeTypeCode"));//交易类型代码 ：买入、卖出				
				pst.setString(6, rs.getString("FExchangeCode")); //交易市场				
				pst.setString(7, "EQ");//证券品种				
				pst.setDouble(8, rs.getDouble("FTradeMoney"));//交易金额
				pst.setDouble(9, rs.getDouble("FTradeFee"));//交易费用
				pst.setDouble(10, YssD.round(YssD.div(rs.getDouble("AvgRate"), rs.getDouble("Num")),8));//平均费率		
				//pst.setDouble(10, YssD.round(YssD.div(YssD.div(rs.getDouble("FTradeFee"), rs.getDouble("FTradeMoney")), rs.getDouble("FNum")),8));//平均费率				
				pst.setString(11, "0"); //明细记录
				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);				
		}catch(Exception e){
			throw new YssException("向投连交易费率测算表插入股票权证的费率数据时出现异常！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans); 
		}
	}
	/**
     * 处理权证平均交易费率
     */
	
	public void doAvgWarrantRate(java.util.Date sDate, java.util.Date eDate, String sPortCode, String sPeriodType) throws
	YssException, SQLException {
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false; 	    
	    String strSql = "";	    
	    //ResultSet rSet = null;
	    ResultSet rs = null;	    
	    //ResultSet resultSet = null;
	    PreparedStatement pst = null;
		try{
			bTrans = true;
			strSql = " delete from " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
	         		 " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FEndDate = " + dbl.sqlDate(eDate) + 
	         		 " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FFlag = '0' and FCatCode in ('OP') ";
			if (sPeriodType.equals("扩张期")){
				strSql = strSql +" and FTradeType = '01' " ;
			}else if (sPeriodType.equals("收缩期")){
				strSql = strSql + " and FTradeType = '02'";//市场和证券品种分组
			} 	         
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
				     " (FStartDate,FEndDate,FPortCode,FPeriodType,FTradeType,FTradeMarket,FCatCode,FTradePrice," +   
				     "  FTradeFee,FAvgRate,FFlag)" +
				     " values(?,?,?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			//权证:'OP'	
			strSql = " select FExchangeCode ,count(FExchangeCode) as Num ,sum(FTradeMoney) as FTradeMoney," +
			         " sum(FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 +FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8) as FTradeFee , " + 
			         " sum(AvgRate) as AvgRate  from " +
			         " (select FSecurityCode,FExchangeCode ,FTradeMoney,FTRADEFEE1,FTRADEFEE2,FTRADEFEE3,FTRADEFEE4, FTRADEFEE5,FTRADEFEE6,FTRADEFEE7, FTRADEFEE8 , " +			     
			         " (case when FTradeMoney <> 0  then " +
			         " (FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 + FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8)/FTradeMoney " +
			         "  else 0 end ) as AvgRate  from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a "  +			        
			         " join ( select FSecurityCode as FSecurityCode_b,FExchangeCode  from " + pub.yssGetTableName("Tb_Para_Security") + 
			         " where FExchangeCode in ('CG','CS') and FCatCode in ('OP') ) b on a.FSecurityCode = b.FSecurityCode_b " +
			         " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
			         " and FBargainDate >= " + dbl.sqlDate(sDate)+ " and FBargainDate <= " + dbl.sqlDate(eDate) ;
			         if (sPeriodType.equals("扩张期")){
			        	 strSql = strSql +" and FTradeTypeCode = '01' )" + " group by FExchangeCode ";
			         }else if (sPeriodType.equals("收缩期")){
			        	 strSql = strSql + " and FTradeTypeCode = '02' )" + " group by FExchangeCode ";//市场和证券品种分组
			         }       
			        
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next()){ 
				pst.setDate(1, YssFun.toSqlDate(this.sDate));
				pst.setDate(2, YssFun.toSqlDate(this.eDate));
				pst.setString(3, this.portCode);//组合代码
				pst.setString(4, this.periodType);
				if (sPeriodType.equals("扩张期")){
					pst.setString(5, "01");//交易类型代码 ：01:买入、02:卖出
				}else if (sPeriodType.equals("收缩期")){
					pst.setString(5, "02");
				}					
				//pst.setString(5, rs.getString("FTradeTypeCode"));//交易类型代码 ：买入、卖出				
				pst.setString(6, rs.getString("FExchangeCode")); //交易市场				
				pst.setString(7, "OP");//证券品种				
				pst.setDouble(8, rs.getDouble("FTradeMoney"));//交易金额
				pst.setDouble(9, rs.getDouble("FTradeFee"));//交易费用
				pst.setDouble(10, YssD.round(YssD.div(rs.getDouble("AvgRate"), rs.getDouble("Num")),8));//平均费率				
				//pst.setDouble(10, YssD.round(YssD.div(rs.getDouble("FTradeFee"),rs.getDouble("FTradeMoney")),6));//平均费率
				pst.setString(11, "0"); //明细记录
				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);				
		}catch(Exception e){
			throw new YssException("向投连交易费率测算表插入股票权证的费率数据时出现异常！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans); 
		}
	}
	/**
     * 处理债券平均交易费率
     */
	public void doAvgBoundRate(java.util.Date sDate, java.util.Date eDate, String sPortCode, String sPeriodType) throws
	YssException, SQLException {
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false; 	    
	    String strSql = "";	    
	    //ResultSet rSet = null;
	    ResultSet rs = null;	    
	    //ResultSet resultSet = null;
	    PreparedStatement pst = null;
		try{
			bTrans = true;
			strSql = " delete from " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
    		 		 " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FEndDate = " + dbl.sqlDate(eDate) + 
    		 		 " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FFlag = '0' and FCatCode in ('FI') ";
			if (sPeriodType.equals("扩张期")){
				strSql = strSql +" and FTradeType = '01' " ;
			}else if (sPeriodType.equals("收缩期")){
				strSql = strSql + " and FTradeType = '02'";//市场和证券品种分组
			} 	
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
				     " (FStartDate,FEndDate,FPortCode,FPeriodType,FTradeType,FTradeMarket,FCatCode,FTradePrice," +   
				     "  FTradeFee,FAvgRate,FFlag)" +
				     " values(?,?,?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			
			//债券'FI'--净价	全价	
			strSql =" select FExchangeCode ,count(FExchangeCode) as Num ,sum(FTradeMoney) as FTradeMoney,sum(FTradeFee) as FTradeFee , " +
			        " sum(AvgRate) as AvgRate  from " +
			        " ( select FExchangeCode,FTradeMoney,FTradeFee,FTradeFee/FTradeMoney as AvgRate from  " +
				    " (select FExchangeCode ," +
			          //成交金额
			         " (case when FQuoteWay ='1' and FTradeTypeCode = '01' then FTradeMoney + FAccruedinterest " + //净价  FQuoteWay：报价方式 0-全价；1-净价
			         "  when FQuoteWay = '1' and FTradeTypeCode = '02'  then FTradeMoney - FAccruedinterest " +
			         " when FQuoteWay = '0' then  FTradeMoney  end ) as FTradeMoney, " +//全价		           
			         //交易费用
					 "  FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 +FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8  as FTradeFee  from " + 
				     " ( select FSecurityCode,FTradeMoney,FTradeTypeCode,FQuoteWay,FExchangeCode ,FAccruedinterest,FTRADEFEE1,FTRADEFEE2,FTRADEFEE3,FTRADEFEE4, FTRADEFEE5,FTRADEFEE6,FTRADEFEE7, FTRADEFEE8 " +
				     " from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a "  +				     			    
			         " join ( select FSecurityCode as FSecurityCode_b,FExchangeCode  from " + pub.yssGetTableName("Tb_Para_Security") + 
			         " where FExchangeCode in ('CG','CS') and FCatCode in ('FI') ) b on a.FSecurityCode = b.FSecurityCode_b " +
			         " left join ( select FSecurityCode as FSecurityCode_c ,FQuoteWay  from " + pub.yssGetTableName("Tb_Para_FixInterest") +
			         //" where FQuoteWay in ('1')) c on a.FSecurityCode = c.FSecurityCode_c " + //FQuoteWay：报价方式 0-全价；1-净价
			         " ) c on a.FSecurityCode = c.FSecurityCode_c " +
			         " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
			         " and FBargainDate >= " + dbl.sqlDate(sDate)+ " and FBargainDate <= " + dbl.sqlDate(eDate);
			         if (sPeriodType.equals("扩张期")){  //FTradeTypeCode:交易方式 01-买入；02-卖出
			        	 strSql = strSql +" and FTradeTypeCode = '01' )))" + " group by FExchangeCode ";
			         }else if (sPeriodType.equals("收缩期")){
			        	 strSql = strSql +" and FTradeTypeCode = '02' )))" + " group by FExchangeCode ";//市场分组
			         } 		          
			        
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next()){
				pst.setDate(1, YssFun.toSqlDate(this.sDate));
				pst.setDate(2, YssFun.toSqlDate(this.eDate));
				pst.setString(3, this.portCode);//组合代码
				pst.setString(4, this.periodType);
				if (sPeriodType.equals("扩张期")){
					pst.setString(5, "01");//交易类型代码 ：01:买入、02:卖出
				}else if (sPeriodType.equals("收缩期")){
					pst.setString(5, "02");
				}
				//pst.setString(5, rs.getString("FTradeTypeCode"));//交易类型代码 ：买入、卖出				
				pst.setString(6, rs.getString("FExchangeCode")); //交易市场				
				pst.setString(7, "FI");//证券品种				
				pst.setDouble(8, rs.getDouble("FTradeMoney"));//交易金额
				pst.setDouble(9, rs.getDouble("FTradeFee"));//交易费用
				pst.setDouble(10, YssD.round(YssD.div(rs.getDouble("AvgRate"), rs.getDouble("Num")),8));//平均费率				
				//pst.setDouble(10, YssD.round(YssD.div(rs.getDouble("FTradeFee"),rs.getDouble("FTradeMoney")),6));//平均费率
				pst.setString(11, "0"); //明细记录
				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);				
		}catch(Exception e){
			throw new YssException("向投连交易费率测算表插入债券的费率数据时出现异常！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans); 
		}		
	}
	
	/**
     * 处理基金平均费率
     */
	public void doAvgFundRate(java.util.Date sDate, java.util.Date eDate, String sPortCode, String sPeriodType) throws
	            YssException, SQLException {
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false; 	    
	    String strSql = "";	    
	    ResultSet rSet = null;
	    ResultSet rs = null;	    
	    //ResultSet resultSet = null;
	    PreparedStatement pst = null;
		try{
			bTrans = true;
			strSql = " delete from " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
	 		 		 " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FEndDate = " + dbl.sqlDate(eDate) + 
	 		 		 " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FFlag = '0' and FCatCode in ('TR') ";
			if (sPeriodType.equals("扩张期")){
				strSql = strSql +" and FTradeType in('01' ,'15')" ;
			}else if (sPeriodType.equals("收缩期")){
				strSql = strSql + " and FTradeType in('02','16')";//市场和证券品种分组
			} 
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
				     " (FStartDate,FEndDate,FPortCode,FPeriodType,FTradeType,FTradeMarket,FCatCode,FTradePrice," +   
				     "  FTradeFee,FAvgRate,FFlag)" +
				     " values(?,?,?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			
			//基金TR--交易所	 当交易方式为卖出时， 货币式基金（TR03）不作统计
			strSql = " select FExchangeCode ,count(FExchangeCode) as Num ,sum(FTradeMoney) as FTradeMoney," +
			         " sum(FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 +FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8) as FTradeFee , " +
			         " sum(AvgRate) as AvgRate  from " +
			         " (select FSecurityCode,FExchangeCode ,FTradeMoney,FTRADEFEE1,FTRADEFEE2,FTRADEFEE3,FTRADEFEE4, FTRADEFEE5,FTRADEFEE6,FTRADEFEE7, FTRADEFEE8 ," +					 
			         " (case when FTradeMoney <> 0  then " +
			         " (FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 + FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8)/FTradeMoney " +
			         "  else 0 end ) as AvgRate  from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a "  +	        
			         " join ( select FSecurityCode as FSecurityCode_b,FExchangeCode  from " + pub.yssGetTableName("Tb_Para_Security") + 
			         " where FExchangeCode in ('CG','CS') and FCatCode in ('TR') " ;
			         if (sPeriodType.equals("收缩期")){ //收缩期：卖出 ，卖出时品种子类型为“货币式基金”（TR03）的不作统计
			        	 strSql = strSql + " and FSubCatCode <> 'TR03' ";
			         }
			         strSql = strSql + " ) b on a.FSecurityCode = b.FSecurityCode_b " +
			                  " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
			                  " and FBargainDate >= " + dbl.sqlDate(sDate)+ " and FBargainDate <= " + dbl.sqlDate(eDate);
					 if (sPeriodType.equals("扩张期")){  //FTradeTypeCode:交易方式 01-买入；02-卖出
				        strSql = strSql +" and FTradeTypeCode = '01' )" + " group by FExchangeCode ";
				     }else if (sPeriodType.equals("收缩期")){
				        strSql = strSql +" and FTradeTypeCode = '02' )" + " group by FExchangeCode ";//市场分组
				     }			         
			        
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next()){
				pst.setDate(1, YssFun.toSqlDate(this.sDate));
				pst.setDate(2, YssFun.toSqlDate(this.eDate));
				pst.setString(3, this.portCode);//组合代码
				pst.setString(4, this.periodType);
				if (sPeriodType.equals("扩张期")){
					pst.setString(5, "01");//交易类型代码 ：01:买入、02:卖出
				}else if (sPeriodType.equals("收缩期")){
					pst.setString(5, "02");
				}
				//pst.setString(5, rs.getString("FTradeTypeCode"));//交易类型代码 ：买入、卖出				
				pst.setString(6, rs.getString("FExchangeCode")); //交易市场				
				pst.setString(7, "TR");//证券品种				
				pst.setDouble(8, rs.getDouble("FTradeMoney"));//交易金额
				pst.setDouble(9, rs.getDouble("FTradeFee"));//交易费用
				pst.setDouble(10, YssD.round(YssD.div(rs.getDouble("AvgRate"), rs.getDouble("Num")),8));//平均费率				
				//pst.setDouble(10, YssD.round(YssD.div(rs.getDouble("FTradeFee"),rs.getDouble("FTradeMoney")),6));//平均费率
				pst.setString(11, "0"); //明细记录
				pst.addBatch();
			}
			//基金--开放式
			strSql = " select FSubCatCode,count(FSubCatCode) as Num , sum(FComfMoney) as FTradeMoney ,sum(FComfFee) as FTradeFee, " +
					 "  sum(AvgRate) as  AvgRate from " +
				     " ( select  FSecurityCode,FComfMoney,FComfFee ,FSubCatCode , " +
				     " (case when FComfMoney > 0 then FComfFee/FComfMoney else 0 end ) as AvgRate  " +					 
					 " from " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " a "  +
			         " join ( select FSecurityCode as FSecurityCode_b,FSubCatCode from " + pub.yssGetTableName("Tb_Para_Security") + 
	                 " where FCatCode in ('TR') and FSubCatCode in ('TR0201','TR0202') and FExchangeCode in ('OTC') " +   //TR0201:开放式股票型基金; TR0202:开放式债券型基金
	                 " ) b on a.FSecurityCode = b.FSecurityCode_b " +
	                 " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
	                 " and FBARGAINDATE >= " + dbl.sqlDate(sDate)+ " and FBARGAINDATE <= " + dbl.sqlDate(eDate)+
	                 " and  FDataType = 'confirm' ";
	                 if (sPeriodType.equals("扩张期")){  //15-申购；16-赎回			
					     strSql = strSql +" and FTradeTypeCode = '15' )" + " group by FSubCatCode ";
					 }else if (sPeriodType.equals("收缩期")){
					     strSql = strSql +" and FTradeTypeCode = '16' )" + " group by FSubCatCode　";//品种子类型分组
					 }	                
			rSet = dbl.queryByPreparedStatement(strSql);
			while(rSet.next()){
				pst.setDate(1, YssFun.toSqlDate(this.sDate));
				pst.setDate(2, YssFun.toSqlDate(this.eDate));
				pst.setString(3, this.portCode);//组合代码
				pst.setString(4, this.periodType);
				if (sPeriodType.equals("扩张期")){
					pst.setString(5, "15");
				}else if (sPeriodType.equals("收缩期")){
					pst.setString(5, "16");
				}
				//pst.setString(5, rSet.getString("FTradeTypeCode"));//交易类型代码 ：申购、赎回			
				pst.setString(6, rSet.getString("FSubCatCode")); //(市场代码)品种子类型：开放式股票型基金，开放式债券型基金		
				pst.setString(7, "TR");//证券品种				
				pst.setDouble(8, rSet.getDouble("FTradeMoney"));//交易金额
				pst.setDouble(9, rSet.getDouble("FTradeFee"));//交易费用
				pst.setDouble(10, YssD.round(YssD.div(rSet.getDouble("AvgRate"), rSet.getDouble("Num")),8));//平均费率
				//pst.setDouble(10, YssD.round(YssD.div(rSet.getDouble("FTradeFee"),rSet.getDouble("FTradeMoney")),6));//平均费率
				pst.setString(11, "0"); //明细记录
				pst.addBatch();
			}			
			pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);				
		}catch(Exception e){
			throw new YssException("向投连交易费率测算表插入基金的费率数据时出现异常！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rSet);
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans); 
		}
		
	}
	/**
     * 合计项处理      
     */
	public void  doTotalMethod(java.util.Date sDate, java.util.Date eDate, String sPortCode, String sPeriodType) throws YssException, SQLException {
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false; 	    
	    String strSql = "";	    
	    ResultSet rs = null;  
	    ResultSet rSet = null;
	    ResultSet rSet1 = null;
	    ResultSet rSet2 = null;
	    PreparedStatement pst = null;
		try{
			bTrans = true;
			strSql = " delete from " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
			         " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FEndDate = " + dbl.sqlDate(eDate) + 
			         " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FFlag = '1'";
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
				     " (FStartDate,FEndDate,FPortCode,FPeriodType,FTradeType,FTradeMarket,FCatCode,FTradePrice," +   
				     "  FTradeFee,FAvgRate,FFlag)" +
				     " values(?,?,?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			//股票（权证）合计项
			strSql = " select 'Exchange' as FExchangeCode ,count(FExchangeCode) as Num ,sum(FTradeMoney) as FTradeMoney," +
				     " sum(FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 +FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8) as FTradeFee , " + 
				     " sum(AvgRate) as AvgRate  from " +
				     " (select FSecurityCode,FExchangeCode ,FTradeMoney,FTRADEFEE1,FTRADEFEE2,FTRADEFEE3,FTRADEFEE4, FTRADEFEE5,FTRADEFEE6,FTRADEFEE7, FTRADEFEE8," +
				     "  (case when  FTradeMoney <> 0  then " +
				     " (FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 + FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8)/FTradeMoney " +
				     "  else 0 end ) as AvgRate  from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a "  +
				     " join ( select FSecurityCode as FSecurityCode_b,FExchangeCode  from " + pub.yssGetTableName("Tb_Para_Security") + 
				     " where FExchangeCode in ('CG','CS') and FCatCode in ('EQ','OP') ) b on a.FSecurityCode = b.FSecurityCode_b " +
				     " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
				     " and FBargainDate >= " + dbl.sqlDate(sDate)+ " and FBargainDate <= " + dbl.sqlDate(eDate) ;
				     if (sPeriodType.equals("扩张期")){
				    	 strSql = strSql +" and FTradeTypeCode = '01' )" ;
				     }else if (sPeriodType.equals("收缩期")){
				    	 strSql = strSql + " and FTradeTypeCode = '02' )" ;//市场和证券品种分组
				     }       
			 rSet1 = dbl.queryByPreparedStatement(strSql);
	         
	         if(rSet1.next() && rSet1.getDouble("Num")!= 0  ){
					pst.setDate(1, YssFun.toSqlDate(this.sDate));
					pst.setDate(2, YssFun.toSqlDate(this.eDate));
					pst.setString(3, this.portCode);//组合代码
					pst.setString(4, this.periodType);  //期间类型
					if (sPeriodType.equals("扩张期")){
						pst.setString(5, "01");//交易类型代码 ：01:买入、02:卖出
					}else if (sPeriodType.equals("收缩期")){
						pst.setString(5, "02");
					}						
					pst.setString(6, rSet1.getString("FExchangeCode"));//( 交易市场) : Exchange--交易所
					pst.setString(7, "EQ");//证券品种				
					pst.setDouble(8, rSet1.getDouble("FTradeMoney"));//交易金额
					pst.setDouble(9, rSet1.getDouble("FTradeFee"));//交易费用
					pst.setDouble(10, YssD.round(YssD.div(rSet1.getDouble("AvgRate"), rSet1.getDouble("Num")),8));//平均费率
					pst.setString(11, "1"); //合计记录
					pst.addBatch();
				}
	         //债券合计项
	         strSql=" select 'Exchange' as FExchangeCode ,count(FExchangeCode) as Num ,sum(FTradeMoney) as FTradeMoney,sum(FTradeFee) as FTradeFee , " +
			        " sum(AvgRate) as AvgRate  from " +
			        " ( select FExchangeCode,FTradeMoney,FTradeFee,FTradeFee/FTradeMoney as AvgRate from  " +
				    " (select FExchangeCode ," +
			          //成交金额
			         " (case when FQuoteWay ='1' and FTradeTypeCode = '01' then FTradeMoney + FAccruedinterest " + //净价  FQuoteWay：报价方式 0-全价；1-净价
			         "  when FQuoteWay = '1' and FTradeTypeCode = '02'  then FTradeMoney - FAccruedinterest " +
			         " when FQuoteWay = '0' then  FTradeMoney  end ) as FTradeMoney, " +//全价		           
			         //交易费用
					 "  FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 +FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8  as FTradeFee  from " + 
				     " ( select FSecurityCode,FTradeMoney,FTradeTypeCode,FQuoteWay,FExchangeCode ,FAccruedinterest,FTRADEFEE1,FTRADEFEE2,FTRADEFEE3,FTRADEFEE4, FTRADEFEE5,FTRADEFEE6,FTRADEFEE7, FTRADEFEE8 " +
				     " from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a "  +				     			    
			         " join ( select FSecurityCode as FSecurityCode_b,FExchangeCode  from " + pub.yssGetTableName("Tb_Para_Security") + 
			         " where FExchangeCode in ('CG','CS') and FCatCode in ('FI') ) b on a.FSecurityCode = b.FSecurityCode_b " +
			         " left join ( select FSecurityCode as FSecurityCode_c ,FQuoteWay  from " + pub.yssGetTableName("Tb_Para_FixInterest") +
			         //" where FQuoteWay in ('1')) c on a.FSecurityCode = c.FSecurityCode_c " + //FQuoteWay：报价方式 0-全价；1-净价
			         " ) c on a.FSecurityCode = c.FSecurityCode_c " +
			         " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
			         " and FBargainDate >= " + dbl.sqlDate(sDate)+ " and FBargainDate <= " + dbl.sqlDate(eDate);
			         if (sPeriodType.equals("扩张期")){  //FTradeTypeCode:交易方式 01-买入；02-卖出
			        	 strSql = strSql +" and FTradeTypeCode = '01' )))";
			         }else if (sPeriodType.equals("收缩期")){
			        	 strSql = strSql +" and FTradeTypeCode = '02' )))";//市场分组
			         } 	
			 rSet2 = dbl.queryByPreparedStatement(strSql);
	         if(rSet2.next() && rSet2.getDouble("Num")!= 0  ){
					pst.setDate(1, YssFun.toSqlDate(this.sDate));
					pst.setDate(2, YssFun.toSqlDate(this.eDate));
					pst.setString(3, this.portCode);//组合代码
					pst.setString(4, this.periodType);  //期间类型
					if (sPeriodType.equals("扩张期")){
						pst.setString(5, "01");//交易类型代码 ：01:买入、02:卖出
					}else if (sPeriodType.equals("收缩期")){
						pst.setString(5, "02");
					}						
					pst.setString(6, rSet2.getString("FExchangeCode"));//( 交易市场) : Exchange--交易所
					pst.setString(7, "FI");//证券品种				
					pst.setDouble(8, rSet2.getDouble("FTradeMoney"));//交易金额
					pst.setDouble(9, rSet2.getDouble("FTradeFee"));//交易费用
					pst.setDouble(10, YssD.round(YssD.div(rSet2.getDouble("AvgRate"), rSet2.getDouble("Num")),8));//平均费率
					pst.setString(11, "1"); //合计记录
					pst.addBatch();
				}       
	         
			//处理交易所基金
	         strSql =" select 'Exchange' as FExchangeCode ,count(FExchangeCode) as Num ,sum(FTradeMoney) as FTradeMoney," +
			         " sum(FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 +FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8) as FTradeFee , " +
			         " sum(AvgRate) as AvgRate  from " +
			         " (select FSecurityCode,FExchangeCode ,FTradeMoney,FTRADEFEE1,FTRADEFEE2,FTRADEFEE3,FTRADEFEE4, FTRADEFEE5,FTRADEFEE6,FTRADEFEE7, FTRADEFEE8 ," +					 
			         " (case when FTradeMoney <> 0  then " +
			         " (FTRADEFEE1 + FTRADEFEE2 + FTRADEFEE3 + FTRADEFEE4 + FTRADEFEE5 +FTRADEFEE6 + FTRADEFEE7 + FTRADEFEE8)/FTradeMoney " +
			         "  else 0 end ) as AvgRate  from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a "  +	        
			         " join ( select FSecurityCode as FSecurityCode_b,FExchangeCode  from " + pub.yssGetTableName("Tb_Para_Security") + 
			         " where FExchangeCode in ('CG','CS') and FCatCode in ('TR') " ;
			         if (sPeriodType.equals("收缩期")){ //收缩期：卖出 ，卖出时品种子类型为“货币式基金”（TR03）的不作统计
			        	 strSql = strSql + " and FSubCatCode <> 'TR03' ";
			         }
			         strSql = strSql + " ) b on a.FSecurityCode = b.FSecurityCode_b " +
			                  " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
			                  " and FBargainDate >= " + dbl.sqlDate(sDate)+ " and FBargainDate <= " + dbl.sqlDate(eDate);
					 if (sPeriodType.equals("扩张期")){  //FTradeTypeCode:交易方式 01-买入；02-卖出
				        strSql = strSql +" and FTradeTypeCode = '01' )" ;
				     }else if (sPeriodType.equals("收缩期")){
				        strSql = strSql +" and FTradeTypeCode = '02' )" ;//市场分组
				     }	        
			rs = dbl.queryByPreparedStatement(strSql);
			if(rs.next() && rs.getDouble("Num")!= 0  ){
					pst.setDate(1, YssFun.toSqlDate(this.sDate));
					pst.setDate(2, YssFun.toSqlDate(this.eDate));
					pst.setString(3, this.portCode);//组合代码
					pst.setString(4, this.periodType);  //期间类型
					if (sPeriodType.equals("扩张期")){
						pst.setString(5, "01");//交易类型代码 ：01:买入、02:卖出
					}else if (sPeriodType.equals("收缩期")){
						pst.setString(5, "02");
					}						
					pst.setString(6, rs.getString("FExchangeCode"));//( 交易市场) : Exchange--交易所
					pst.setString(7, "TR");//证券品种				
					pst.setDouble(8, rs.getDouble("FTradeMoney"));//交易金额
					pst.setDouble(9, rs.getDouble("FTradeFee"));//交易费用
					pst.setDouble(10, YssD.round(YssD.div(rs.getDouble("AvgRate"), rs.getDouble("Num")),8));//平均费率
					pst.setString(11, "1"); //合计记录
					pst.addBatch();
			}       
			
			//基金--开放式
			strSql = " select count(FSubCatCode) as Num , sum(FComfMoney) as FTradeMoney ,sum(FComfFee) as FTradeFee, " +
					 "  sum(AvgRate) as  AvgRate from " +
				     " ( select  FSecurityCode,FComfMoney,FComfFee ,FSubCatCode , " +
				     " (case when FComfMoney > 0 then FComfFee/FComfMoney else 0 end ) as AvgRate  " +					 
					 " from " + pub.yssGetTableName("Tb_Data_OpenFundTrade") + " a "  +
			         " join ( select FSecurityCode as FSecurityCode_b,FSubCatCode from " + pub.yssGetTableName("Tb_Para_Security") + 
		             " where FCatCode in ('TR') and FSubCatCode in ('TR0201','TR0202')  " +   //TR0201:开放式股票型基金; TR0202:开放式债券型基金  and FExchangeCode in ('OTC')
		             " ) b on a.FSecurityCode = b.FSecurityCode_b " +
		             " where a.FCHECKSTATE = 1 and a.FPortCode = " + dbl.sqlString(sPortCode) + 
		             " and FBARGAINDATE >= " + dbl.sqlDate(sDate)+ " and FBARGAINDATE <= " + dbl.sqlDate(eDate)+
		             " and  FDataType = 'confirm' ";
		             if (sPeriodType.equals("扩张期")){  //15-申购；16-赎回			
					      strSql = strSql +" and FTradeTypeCode = '15' )";
					  }else if (sPeriodType.equals("收缩期")){
					      strSql = strSql +" and FTradeTypeCode = '16' )" ;//品种子类型分组
					  }	                
			rSet = dbl.queryByPreparedStatement(strSql);
			if(rSet.next() && rSet.getDouble("Num")!= 0 ){
				pst.setDate(1, YssFun.toSqlDate(this.sDate));
				pst.setDate(2, YssFun.toSqlDate(this.eDate));
				pst.setString(3, this.portCode);//组合代码
				pst.setString(4, this.periodType);  //期间类型
				if (sPeriodType.equals("扩张期")){
					pst.setString(5, "15");//交易类型代码 ：15:申购、16:赎回
				}else if (sPeriodType.equals("收缩期")){
					pst.setString(5, "16");
				}						
				pst.setString(6, "Open");//( 交易市场) : Exchange--交易所
				pst.setString(7, "TR");//证券品种				
				pst.setDouble(8, rSet.getDouble("FTradeMoney"));//交易金额
				pst.setDouble(9, rSet.getDouble("FTradeFee"));//交易费用
				pst.setDouble(10, YssD.round(YssD.div(rSet.getDouble("AvgRate"), rSet.getDouble("Num")),8));//平均费率
				pst.setString(11, "1"); //合计记录
				pst.addBatch();
		    } 		
			pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("合计项处理时出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rSet);
			dbl.closeResultSetFinal(rSet1);
			dbl.closeResultSetFinal(rSet2);
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans); 
		}
	}
	
	
	/**
     * 执行确认  处理    
     */
    private String doConfirmed(java.util.Date sDate, java.util.Date eDate, String sPortCode, String sPeriodType) 
             throws YssException {
    	Connection conn = dbl.loadConnection();
    	boolean bTrans = false; 	    
	    String strSql = "";	    
	    ResultSet rs = null;  
	    ResultSet rSet = null;
	    ResultSet rSet1 = null;
	    ResultSet rSet2 = null;
	    PreparedStatement pst = null;
	    
	    try{
	    	bTrans = true;
			strSql = " delete from " + pub.yssGetTableName("Tb_Data_InsAvgExRate") +
			         " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FEndDate = " + dbl.sqlDate(eDate) + 
			         " and FPeriodType = " + dbl.sqlString(sPeriodType) ;
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_InsAvgExRate") +
		             " (FEndDate,FPortCode,FPeriodType,FTradeType,FTradeMarket,FCatCode,FAvgRate)" + 				     
		             " values(?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			//处理股票权证合计
			strSql = " select FTradeMarket ,FAvgRate/num as FAvgRate from " +
				     " (select FTradeMarket,sum(FAvgRate) as FAvgRate ,count(FCatCode) as num from " 
				     + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
			         " where FEndDate = " + dbl.sqlDate(eDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
			         " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FCatCode in ('EQ','OP') and FFlag = '1' " +
			         " group by FTradeMarket )";
			rs = dbl.openResultSet(strSql);  
							
			pst.setDate(1, YssFun.toSqlDate(this.eDate));
			pst.setString(2,this.portCode);//组合代码
			pst.setString(3, this.periodType);  //期间类型
			if (sPeriodType.equals("扩张期")){
				pst.setString(4, "01");//交易类型代码 ：01:买入、02:卖出
			}else if (sPeriodType.equals("收缩期")){
				pst.setString(4, "02");
			}			
			//pst.setString(4, rs.getString("FTradeType"));//交易类型代码
			if(rs.next()){
				pst.setString(5, rs.getString("FTradeMarket")); 
				pst.setString(6, "EQ");//证券品种					
				pst.setDouble(7, rs.getDouble("FAvgRate"));//平均费率		
			}else{
				pst.setString(5, "Exchange");
				pst.setString(6, "EQ");//证券品种					
				pst.setDouble(7, 0);//平均费率		
			}	
			pst.addBatch();
			
			//处理债券
			strSql = " select FTradeType,FTradeMarket,FCatCode, FAvgRate from " 
			     + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
		         " where FEndDate = " + dbl.sqlDate(eDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
		         " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FCatCode in ('FI') and FFlag = '1' " ;
			rSet = dbl.openResultSet(strSql);
		    				
	    	pst.setDate(1, YssFun.toSqlDate(this.eDate));
	    	pst.setString(2, this.portCode);//组合代码
	    	pst.setString(3, this.periodType);  //期间类型
	    	if (sPeriodType.equals("扩张期")){
				pst.setString(4, "01");//交易类型代码 ：01:买入、02:卖出
			}else if (sPeriodType.equals("收缩期")){
				pst.setString(4, "02");
			}			
	    	if(rSet.next()){
				pst.setString(5, rSet.getString("FTradeMarket")); 
				pst.setString(6, "FI");//证券品种					
				pst.setDouble(7, rSet.getDouble("FAvgRate"));//平均费率		
			}else{
				pst.setString(5, "Exchange");
				pst.setString(6, "FI");//证券品种					
				pst.setDouble(7, 0);//平均费率		
			}		
	    	pst.addBatch();
		    //开放式基金
	    	strSql = " select FTradeType,FTradeMarket,FCatCode, FAvgRate from " 
			     + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
		         " where FEndDate = " + dbl.sqlDate(eDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
		         " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FCatCode in ('TR') and FFlag = '1'  and FTradeMarket in ('Open') " ;
			rSet1 = dbl.openResultSet(strSql);
			pst.setDate(1, YssFun.toSqlDate(this.eDate));
	    	pst.setString(2, this.portCode);//组合代码
	    	pst.setString(3, this.periodType);  //期间类型
	    	if (sPeriodType.equals("扩张期")){
				pst.setString(4, "15");
			}else if (sPeriodType.equals("收缩期")){
				pst.setString(4, "16");
			}			
	    	if(rSet1.next()){
				pst.setString(5, rSet1.getString("FTradeMarket")); 
				pst.setString(6, "TR");//证券品种					
				pst.setDouble(7, rSet1.getDouble("FAvgRate"));//平均费率		
			}else{
				pst.setString(5, "Open");
				pst.setString(6, "TR");//证券品种					
				pst.setDouble(7, 0);//平均费率		
			}		
	    	pst.addBatch();
	    	//交易所基金
			strSql = " select FTradeType,FTradeMarket,FCatCode, FAvgRate from " 
			     + pub.yssGetTableName("Tb_Data_ExRateEstimate") +
		         " where FEndDate = " + dbl.sqlDate(eDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
		         " and FPeriodType = " + dbl.sqlString(sPeriodType) + "and FCatCode in ('TR') and FFlag = '1' and FTradeMarket in ('Exchange')  " ;
			rSet2 = dbl.openResultSet(strSql);
			pst.setDate(1, YssFun.toSqlDate(this.eDate));
	    	pst.setString(2, this.portCode);//组合代码
	    	pst.setString(3, this.periodType);  //期间类型
	    	if (sPeriodType.equals("扩张期")){
				pst.setString(4, "01");//交易类型代码 ：01:买入、02:卖出
			}else if (sPeriodType.equals("收缩期")){
				pst.setString(4, "02");
			}			
	    	if(rSet2.next()){
				pst.setString(5, rSet2.getString("FTradeMarket")); 
				pst.setString(6, "TR");//证券品种					
				pst.setDouble(7, rSet2.getDouble("FAvgRate"));//平均费率		
			}else{
				pst.setString(5, "Exchange");
				pst.setString(6, "TR");//证券品种					
				pst.setDouble(7, 0);//平均费率		
			}		
	    	pst.addBatch();
		    pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);			
	    }catch(Exception e){
	    	throw new YssException("确认投连交易费率测算表时出错！\n", e);
	    }finally{
	    	dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rSet);
			dbl.closeResultSetFinal(rSet1);
			dbl.closeResultSetFinal(rSet2);
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans); 	       
	    } 
	    return "";
    }
	
	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}	

	public java.util.Date getsDate() {
		return sDate;
	}

	public void setsDate(java.util.Date sDate) {
		this.sDate = sDate;
	}

	public java.util.Date geteDate() {
		return eDate;
	}

	public void seteDate(java.util.Date eDate) {
		this.eDate = eDate;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}


	

}
