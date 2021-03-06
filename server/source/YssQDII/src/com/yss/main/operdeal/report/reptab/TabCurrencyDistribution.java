package com.yss.main.operdeal.report.reptab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.base.BaseAPOperValue;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class TabCurrencyDistribution extends BaseAPOperValue{

	private java.util.Date dBeginDate;
    private java.util.Date dEndDate;
    private String portCode;
    private boolean isCreate; //是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询
	
    
	public TabCurrencyDistribution(){
		
	}
	
	//==========================================================================================
	 public void init(Object bean) throws YssException {
	        String reqAry[] = null;
	        String reqAry1[] = null;
	        String sRowStr = (String) bean;
	        if (sRowStr.trim().length() == 0) {
	            return;
	        }
	        reqAry = sRowStr.split("\n");
	        reqAry1 = reqAry[0].split("\r");
	        this.dBeginDate = YssFun.toDate(reqAry1[1]);
	        reqAry1 = reqAry[1].split("\r");
	        this.dEndDate = YssFun.toDate(reqAry1[1]);
	        reqAry1 = reqAry[2].split("\r");
	        this.portCode = reqAry1[1];
	        //--------MS00432 QDV4中保2009年05月04日03_B add by sj---------------
	        reqAry1 = reqAry[3].split("\r"); //此参数为新加入，需更新报表配置
	        if (reqAry1[1].equalsIgnoreCase("0")) { //若为0，则只查询已生成的报表数据
	            this.isCreate = false;
	        } else { //生成报表
	            this.isCreate = true;
	        }
	 }
	 
	 
	 
	 public Object invokeOperMothed() throws YssException {
	            HashMap valueMap = null;
	            //createTempSummary();
	            try {
	                valueMap = new HashMap();
	               
	                if (this.isCreate) { 
	                    processGetSummary(valueMap);
	                }
	                //------------------------------------------------------------
	            } catch (YssException ex) {
	                throw new YssException(ex.getMessage());
	            }
	            return "";
	        }
	        
	//==========================================================================================
	 /**
    * 生成数据表
    * @throws YssException
    */
   private void createTable() throws YssException {
       String strSql = "";
       try {
           if (dbl.yssTableExist(pub.yssGetTableName("tb_rep_InterestIncome"))) {
               return;
           } else {
               strSql = "create table " +
                   pub.yssGetTableName("tb_rep_InterestIncome") +
                   " (FCode varchar2(70) not null," +
                   " FName varchar2(50)," +
                   " FCatCode varchar2(100) not null," +
                   " FSubCatCode varchar2(100)," +
                   " FCuryCode varchar2(20) not null," +
                   " FBal number(18,4)," +
                   " FBaseCuryBal number(18,4)," +
                   " FPortCode varchar2(20)," +
                   " FProportion number(10,5)," +
                   " FDate Date not null," +
                   " FSummaryType number(2))";
               dbl.executeSql(strSql);
           }
       } catch (Exception e) {
           throw new YssException("创建利息收入分布表出错！"+e.getMessage());
       }
   }
   
   /**
    * 执行从各个报表获取数据的动作
    * @throws YssException
    */
   private void processGetSummary(HashMap valueMap) throws YssException {
       double PortCost = 0;
       double Accumulated = 0;
       double monthCost = 0;
       double netFirst = 0;
       if (null == valueMap) {
           throw new YssException("未实例化Map！");
       }
       Connection conn = dbl.loadConnection();
       boolean bTrans = false;
       //--- sj modified 20090707 第2个净值数据和管理费
       double netSecond = 0D;
       double manageFee = 0D;
       //------------------------------------
       try {
           conn.setAutoCommit(false);
       } catch (SQLException ex) {
       }
       bTrans = true;

       if (!dbl.yssTableExist(pub.yssGetTableName("tb_Data_Summary"))) {
           throw new YssException("Summary表不存在！");
       }
       deleteSummary();

       insertToTempSummary(valueMap);

    
       //----------------------------------------------------------------------
       try {
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
       } catch (SQLException ex1) {
           throw new YssException(ex1.getMessage());
       } finally {
           dbl.endTransFinal(conn, bTrans);
       }
   }
   
   
   /**
    * 将数据封装放入HashMap中。
    * @param valueMap HashMap
    * @param rs ResultSet
    * @throws YssException
    */
   private void setResultValue(HashMap valueMap, ResultSet rs) throws
       YssException {
       if (null == valueMap) {
           throw new YssException("未实例化Map！");
       }
       if (null == rs) {
           return;
       }
      
       try {
           
       } catch (Exception ex) {
           throw new YssException(ex.getMessage());
       }
//     finally {
//        dbl.closeResultSetFinal(rs);
//     }
   }

   /**
    * 将数据插入数据库
    * @param valueMap HashMap
    * @throws YssException
    */
   private void insertToTempSummary(HashMap valueMap) throws YssException {
       if (null == valueMap || valueMap.isEmpty()) {
           return;
       }
       
       Object object = null;
       PreparedStatement prst = null;
       String sqlStr = "insert into " +
           pub.yssGetTableName("tb_Data_Summary") +
           "(FCode,FName,FCatCode,FSubCatCode,FCuryCode,FBal,FBaseCuryBal,FPortCode,FProportion,FDate,FSummaryType)" +
           " values(?,?,?,?,?,?,?,?,?,?,?)";
       try {
           prst = dbl.openPreparedStatement(sqlStr);
           Iterator it = valueMap.keySet().iterator();
           while (it.hasNext()) {
              
               prst.executeUpdate();
           }
       } catch (YssException ex) {
           throw new YssException("insert error", ex);
       } catch (SQLException ex) {
           throw new YssException(ex.getMessage());
       } finally {
           dbl.closeStatementFinal(prst);
       }
   }

   
   /**
    * 从Summary表中按要求删除相关数据
    * @throws YssException
    */
   private void deleteSummary() throws YssException {
       String sqlStr = "Delete from " +
           pub.yssGetTableName("tb_Data_Summary") +
           " where FDate = " + dbl.sqlDate(this.dEndDate) + " and Fportcode ='" + portCode + "'";
       try {
           dbl.executeSql(sqlStr);
       } catch (Exception ex) {
           throw new YssException(ex.getMessage());
       }

   }
}
