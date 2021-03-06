package com.yss.main.operdeal.report.reptab;

import com.yss.base.BaseAPOperValue;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Iterator;

import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.YssFun;
import com.yss.util.YssException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;

/**
 * <p>Title: </p>
 * 计算未兑现资产本金增值/贬值分布(汇兑损益)
 * <p>Description: </p>
 * 此类只产生相应数据，要获取报表的值需要在数据源中配置获取。
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TabUnrealisedFx
      extends BaseAPOperValue {
   private java.util.Date dBeginDate;
   private java.util.Date dEndDate;
   private String portCode;
   private boolean isMake = false;//20100311 - 李高辉 - 是否生成标识


   private static final int unrealisedType_AccumulateFx = 3; //上月结余(汇兑)
   private static final int unrealisedType_YearTodateFx = 4; //当月累计(汇兑)
   private static final int unrealisedType_TotalFx = 5; //当月发生(汇兑)
   private static final int unrealisedType_CurrentTotal = 6; //当月总发生额。(汇兑)

   /**
    *
    * <p>Title: </p>
    * 未实现内部类
    * <p>Description: </p>
    * 用于获取未实现的相应信息
    * <p>Copyright: Copyright (c) 2006</p>
    *
    * <p>Company: </p>
    *
    * @author not attributable
    * @version 1.0
    */
   private class UnrealisedBean {
      String Code; //标示字段，用于排序
      String Name; //项目名
      String CatCode; //品种类型
      String SubCatCode; //品种子类型
      String CuryCode; //货币
      double Bal; //原币金额
      int UnrealisedType; //纪录类型
      double BaseCuryBal; //基础货币金额
      String portCode; //组合
      java.sql.Date Date;
      public void UnrealisedBean() {
      }
   }

   public TabUnrealisedFx() {
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
      dBeginDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      dEndDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[2].split("\r");
      portCode = reqAry1[1];
      reqAry1 = reqAry[3].split("\r");//20100311 - 李高辉 - 获取是否生成数据标识
      if(!"0".equals(reqAry1[1])){
         isMake = true;
      }
   }

   public Object invokeOperMothed() throws YssException {
	  if(!isMake) return "";//20100311 - 李高辉 - 判断是否生成数据，不生成则返回
	  
	  //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
	  OffAcctBean offAcct = new OffAcctBean();
	  offAcct.setYssPub(this.pub);
	  String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
	  String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
	  if(!tmpInfo.trim().equalsIgnoreCase("")) {
		  return "<OFFACCT>" + tmpInfo;
	  }
	  //=================end=================
	  
      HashMap valueMap = null;
      createTempUnrealised();
      valueMap = new HashMap();
      try {
         getUnrealisedValue(valueMap);
      }
      catch (YssException ex) {
         throw new YssException(ex.getMessage());
      }
      return "";
   }

   /**
    * 创建用于存放未实现数据的表。
    * @throws YssException
    */
   private void createTempUnrealised() throws YssException {
      String strSql = "";
      try {
         if (dbl.yssTableExist(pub.yssGetTableName("tb_Data_Unrealised"))) {
            return;
         }
         else {
            strSql = "create table " +
                  pub.yssGetTableName(
                        "tb_Data_Unrealised") +
                  " (FCode varchar2(70) not null," +
                  " FName varchar2(50)," +
                  " FCatCode varchar2(20) not null," +
                  " FSubCatCode varchar2(20)," +
                  " FCuryCode varchar2(20) not null," +
                  " FBal number(18,4)," +
                  " FBaseCuryBal number(18,4)," +
                  " FPortCode varchar2(20)," +
                  " FDate Date not null," +
                  " FUnrealisedType number(1))";
            dbl.executeSql(strSql);
         }
      }
      catch (Exception e) {
         throw new YssException("生成临时未兑现资产本金表(汇兑损益)出错!");
      }
   }

   private void deleteFromTempUnrealised() throws YssException {
      String sqlStr = "Delete from " +
            pub.yssGetTableName("tb_Data_Unrealised") +
            " where FDate = " + dbl.sqlDate(dEndDate) +
            " and FportCode = " + dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (" + unrealisedType_AccumulateFx + "," +
            unrealisedType_YearTodateFx +
            "," + unrealisedType_TotalFx +
            "," + unrealisedType_CurrentTotal +
            ")";
      try {
         dbl.executeSql(sqlStr);
      }
      catch (Exception ex) {
         throw new YssException(ex.getMessage());
      }
   }

   /**
    * 将未实现的数据插入数据库
    * @param valueMap HashMap
    * @throws YssException
    */
   private void insertToTempUnrealised(HashMap valueMap) throws YssException {
      if (null == valueMap || valueMap.isEmpty()) {
         return;
      }
      UnrealisedBean unrealised = null;
      Object object = null;
      PreparedStatement prst = null;
      String sqlStr = "insert into " +
            pub.yssGetTableName("tb_Data_Unrealised") +
            "(FCode,FName,FCatCode,FSubCatCode,FCuryCode,FBal,FBaseCuryBal,FPortCode,FDate,FUnrealisedType)" +
            " values(?,?,?,?,?,?,?,?,?,?)";
      try {
         prst = dbl.openPreparedStatement(sqlStr);
         Iterator it = valueMap.keySet().iterator();
         while (it.hasNext()) {
            unrealised = (com.yss.main.operdeal.report.reptab.TabUnrealisedFx.
                          UnrealisedBean) valueMap.get( (String) it.next());
            prst.setString(1, unrealised.Code);
            prst.setString(2, unrealised.Name);
            prst.setString(3, unrealised.CatCode);
            prst.setString(4, unrealised.SubCatCode);
            prst.setString(5, unrealised.CuryCode);
            prst.setDouble(6, YssFun.roundIt(unrealised.Bal, 4));
            prst.setDouble(7, YssFun.roundIt(unrealised.BaseCuryBal, 4));
            prst.setString(8, unrealised.portCode);
            prst.setDate(9, unrealised.Date);
            prst.setInt(10, unrealised.UnrealisedType);
            prst.executeUpdate();
         }
      }
      catch (YssException ex) {
         throw new YssException("insert error！", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
	  //add by rujiangpeng 20100603打开多张报表系统需重新登录
	  finally{
		 dbl.closeStatementFinal(prst);
	  }
   }

   /**
    * 获取未实现的数据
    * @param valueMap HashMap
    * @throws YssException
    * @throws SQLException
    */
   private void getUnrealisedValue(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }
      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      try {
         conn.setAutoCommit(false);
      }
      catch (SQLException ex) {
      }
      bTrans = true;

      deleteFromTempUnrealised(); //先删除已有的数据。

      getEachEquitis(valueMap);
      getEachdebtSecurities(valueMap);
      getEachFixedDeposit(valueMap);
      getRecpayEntiy(valueMap);
      getFundDetail(valueMap);
      insertToTempUnrealised(valueMap);
      getfdd();
      getSumRecpay();
      getSumEquitis();
      getSumDebtSecurities();
      getSumFixedDeposit();
      getFundTotal();

      try {
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (SQLException ex1) {
         throw new YssException(ex1.getMessage());
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }
   }
   /*
    *
    * 上期结余/当期发生.两个中只有一个有值，
    * 另外一个也必须要有值
    * 2009年11月7日 -苏
    *
    * */
private void getfdd() throws YssException{

    ResultSet rs = null;

String sqlstr=" select nvl(a.fcode, b.fcode) as fcode,"+
            "  nvl(a.fname, b.fname) as fname,"+
            "  nvl(a.fcatcode, b.fcatcode) as fcatcode,"+
            " nvl(a.fsubcatcode, b.fsubcatcode) as fsubcatcode,"+
            "  nvl(a.fcurycode, b.fcurycode) as fcurycode,"+
            "  nvl(a.fbal, b.fbal) as fbal,"+
            "  nvl(a.fbasecurybal, b.fbasecurybal) as fbasecurybal,"+
            "  nvl(a.fportcode, b.fportcode) as fportcode,"+
            "  nvl(a.fdate,b.fdate) as fdate,"+
            "  nvl(a.funrealisedtype,b.funrealisedtype) as funrealisedtype "+
            "  from (select *  from "+pub.yssGetTableName("tb_Data_Unrealised") +
            "      where fportcode = "+  dbl.sqlString(this.portCode) +
            "        and fdate ="+ dbl.sqlDate(dEndDate) +" and FUnrealisedType = 3) a "+
            "  full join (select *  from "+pub.yssGetTableName("tb_Data_Unrealised") +
            "           where fportcode =  "+ dbl.sqlString(this.portCode) +
            "            and fdate = "+ dbl.sqlDate(dEndDate) +"  and FUnrealisedType = 4    "+
            "         ) b on a.fcatcode = b.fcatcode "+
            "           and a.fcurycode = b.fcurycode and a.fsubcatcode = b.fsubcatcode "+
            "  where (a.fcode is null or b.fcode is null) ";

               HashMap  valueMaps = null;
               valueMaps = new HashMap();
               ResultSet rs1=null;

 try{
        rs = dbl.openResultSet(sqlstr);

        while (rs.next()){
        if(rs.getInt("funrealisedtype")==3){
        	sqlstr="select substr('"+rs.getString("fcode")+"',0,length('"+rs.getString("fcode")+"')-1)||'4' as fcode ,'.  Year-to-date difference' as fname,'"+ rs.getString("fcatcode")+"' as fcatcode," +
        			" '"+rs.getString("fsubcatcode")+"' as fsubcatcode,'"+rs.getString("fcurycode")+"' as fcurycode,0 as fbal," +
        			"0 as fbasecurybal,'"+rs.getString("fportcode")+"' as fportcode,to_date('"+rs.getDate("fdate")+"','yyyy-MM-dd')   as fdate,4 as funrealisedtype from dual";

        }
        else {
        	sqlstr="select substr('"+rs.getString("fcode")+"',0,length('"+rs.getString("fcode")+"')-1)||'3' as fcode ,'.  Accumulated difference b/f' as fname,'"+ rs.getString("fcatcode")+"' as fcatcode," +
			" '"+rs.getString("fsubcatcode")+"' as fsubcatcode,'"+rs.getString("fcurycode")+"' as fcurycode,0 as fbal," +
			"0 as fbasecurybal,'"+rs.getString("fportcode")+"' as fportcode,to_date('"+rs.getDate("fdate")+"','yyyy-MM-dd') as fdate,3 as funrealisedtype from dual";

        }

        rs1 = dbl.openResultSet(sqlstr);
        setResultValue(valueMaps, rs1);
        insertToTempUnrealised(valueMaps);
        valueMaps.clear();
        }





 } catch (Exception e) {
     throw new YssException("补0数据出错！", e);
 }
 finally {
    dbl.closeResultSetFinal(rs);
 }



}
   /**
    * 获取债券的汇总信息。
    * @throws YssException
    */
   private void getSumDebtSecurities() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            " FCatCode " + dbl.sqlJN() + " FSubCatCode" + dbl.sqlJN() +
            "FCuryCode" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_TotalFx + "" +
            " as FCode" +
            ",'Debt Securities-'" + dbl.sqlJN() +
            "FCuryCode as FName, FCatCode, FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(dEndDate) + " as FDate," +
            unrealisedType_TotalFx + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = 'FI' and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (3,4) and " +
            " FDate = " + dbl.sqlDate(dEndDate) +
            " group by FPortCode,FCuryCode,FCatCode,FSubCatCode";
      ResultSet rs = null;
      HashMap valueMap = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         valueMap = new HashMap();
         setResultValue(valueMap, rs);
         insertToTempUnrealised(valueMap);
      }
      catch (Exception e) {
         throw new YssException("汇总债券信息数据出错！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   /**
    * 获取基金的汇总信息。
    * @throws YssException
    */
   private void getFundTotal() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            " FCatCode " + dbl.sqlJN() + " FSubCatCode" + dbl.sqlJN() +
            "FCuryCode" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_TotalFx + "" +
            " as FCode" +
            ",'Fund Securities-'" + dbl.sqlJN() +
            "FCuryCode as FName, FCatCode, FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(dEndDate) + " as FDate," +
            unrealisedType_TotalFx + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = 'TR' and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (3,4) and " +
            " FDate = " + dbl.sqlDate(dEndDate) +
            " group by FPortCode,FCuryCode,FCatCode,FSubCatCode";
      ResultSet rs = null;
      HashMap valueMap = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         valueMap = new HashMap();
         setResultValue(valueMap, rs);
         insertToTempUnrealised(valueMap);
      }
      catch (Exception e) {
         throw new YssException("汇总基金信息数据出错！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }


   /**
    * 应收应付汇兑的汇总信息。
    * @throws YssException
    */
   private void getSumRecpay() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            " 'RecPay' " + dbl.sqlJN() + " 'RecPay99'" + dbl.sqlJN() +
            "'HKD'" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_TotalFx + "" +
            " as FCode" +
            ",'Due to convertion difference of assets'" + dbl.sqlJN() +
            "'' as FName, FCatCode, FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(dEndDate) + " as FDate," +
            unrealisedType_TotalFx + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = 'RecPay' and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (3,4) and " +
            " FDate = " + dbl.sqlDate(dEndDate) +
            " group by FPortCode,FCuryCode,FCatCode,FSubCatCode";
      ResultSet rs = null;
      HashMap valueMap = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         valueMap = new HashMap();
         setResultValue(valueMap, rs);
         insertToTempUnrealised(valueMap);
      }
      catch (Exception e) {
         throw new YssException("汇总应收应付汇兑信息数据出错！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }


   /**
    * 获取定存的汇总信息。
    * @throws YssException
    */
   private void getSumFixedDeposit() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) + dbl.sqlJN() +
            "'01'" +
            dbl.sqlJN() + "FCuryCode" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_TotalFx + "" +
            " as FCode" +
            ",'Fixed Deposit-'" + dbl.sqlJN() +
            "FCuryCode as FName,FCatCode,FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(dEndDate) + " as FDate," +
            unrealisedType_TotalFx + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = '01' and FSubCatCode = '0102' and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (3,4) and " +
            " FDate = " + dbl.sqlDate(dEndDate) +
            " group by FPortCode,FCuryCode,FCatCode,FSubCatCode";
      ResultSet rs = null;
      HashMap valueMap = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         valueMap = new HashMap();
         setResultValue(valueMap, rs);
         insertToTempUnrealised(valueMap);
      }
      catch (Exception e) {
         throw new YssException("汇总定存信息数据出错！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   /**
    * 获取股票的汇总信息。
    * @throws YssException
    */
   private void getSumEquitis() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            "'EQ'" +
            dbl.sqlJN() + "FCuryCode" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_TotalFx + "" +
            " as FCode" +
            ",'Equities-'" + dbl.sqlJN() +
            "FCuryCode as FName,FCatCode,FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_AccumulateFx +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(dEndDate) + " as FDate," +
            unrealisedType_TotalFx + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = 'EQ' and FSubCatCode = 'EQ01' and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (3,4) and " +
            " FDate = " + dbl.sqlDate(dEndDate) +
            " group by FPortCode,FCuryCode,FCatCode,FSubCatCode";
      ResultSet rs = null;
      HashMap valueMap = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         valueMap = new HashMap();
         setResultValue(valueMap, rs);
         insertToTempUnrealised(valueMap);
      }
      catch (Exception e) {
         throw new YssException("汇总股票信息数据出错！", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   /**
    * 定存类月明细
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getEachFixedDeposit(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }


      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            "'01'" + dbl.sqlJN() +
            "'0102'" +
            dbl.sqlJN() + "FCuryCode" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_YearTodateFx + "" +
            " as FCode,'.  Year-to-date difference' as FName," +
            unrealisedType_YearTodateFx + " as FUnrealisedType, " +
            dbl.sqlDate(dEndDate) +
            " as FDate,FPortCode,FCuryCode,'01' as FAccType,'0102' as FSubAccType" +
            ",sum(ybal) as FBal,sum(ybal) as FBaseCuryBal" +
            " from " +
            " (select round(sum(FSyvBaseCuryBal),2)  as ybal,FCuryCode,v.FPORTCODE from tb_data_PortfolioVal v " +
            " where v.forder like '01__0102%'  "+
            " and v.forder not like '%HKD%' and "+
            " FPortCode = " +dbl.sqlString(this.portCode) +
            " and   v.forder  like '%total%'  "+
            " and FValDate=" + dbl.sqlDate(dEndDate) +
            " group by FCuryCode,FPORTCODE) group by FPortCode,FCuryCode";

      ResultSet rs = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         setResultValue(valueMap, rs);


      }
      catch (YssException ex) {
         throw new YssException("获取Equity数据出错！", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally{
         dbl.closeResultSetFinal(rs);
      }

      if (YssFun.formatDate(this.dEndDate, "MM").equals("01")){
    	  sqlStr = "select " +
          dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
          dbl.sqlJN() +
          "'01'" + dbl.sqlJN() +
          "'0102' " +
          dbl.sqlJN() + "FCuryCode"
          + dbl.sqlJN() + "FPortCode" +
          dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
          " as FCode,'.  Accumulated difference b/f' as FName," +
          unrealisedType_AccumulateFx + " as FUnrealisedType, " +
          dbl.sqlDate(dEndDate) +
          " as FDate,FPortCode,FCuryCode,'01' as FAccType,'0102' as FSubAccType" +
          ",sum(FBal) as FBal,sum(FPortCuryBal) as FBaseCuryBal" +
          " from " +
          " ( select FPortCode,FCuryCode,sum(fbal) as FBal,sum(FBaseCuryBal) as FBaseCuryBal," +
          " sum(FPortCuryBal) as FPortCuryBal  "+
          " from (select CashStorage.*   from (select *  from " + pub.yssGetTableName("TB_STOCK_Cashpayrec") + " j "+
          "  where FCheckState = 1   and FPortCode = "+dbl.sqlString(this.portCode)+"    and FAnalysisCode2 = 'DE' "+
          "  and FCuryCode <> 'HKD'    and FYearMonth =to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00') CashStorage "+
          " join (select * from  " + pub.yssGetTableName("Tb_Para_cashaccount") + "  where FCheckState = 1 "+
          " and FPortCode ="+dbl.sqlString(this.portCode)+"   and FAccType = '04'  and FSubAccType = '0414') "+
          " cashCount on CashStorage.FCashAccCode = cashCount.FCashAccCode) "+
          " group by FStorageDate, FPortCode, FCuryCode  ) group by FCuryCode,FPortCode";
      }
      else {

   sqlStr = "select " +
          dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
          dbl.sqlJN() +
          "'01'" + dbl.sqlJN() +
          "'0102' " +
          dbl.sqlJN() + "FCuryCode"
          + dbl.sqlJN() + "FPortCode" +
          dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
          " as FCode,'.  Accumulated difference b/f' as FName," +
          unrealisedType_AccumulateFx + " as FUnrealisedType, " +
          dbl.sqlDate(dEndDate) +
          " as FDate,FPortCode,FCuryCode,'01' as FAccType,'0102' as FSubAccType" +
          ",sum(ybal) as FBal,sum(ybal) as FBaseCuryBal" +
          " from " +
          " (select round(sum(FSyvBaseCuryBal),2)  as ybal,FCuryCode,v.FPORTCODE from tb_data_PortfolioVal v " +
          " where " +
          " forder like '01__0102%'"+
          " and v.forder not like '%HKD%' and " +
          " FPortCode = " +dbl.sqlString(this.portCode) +
          " and v.forder  like '%total%'"+
          " and FValDate="+dbl.sqlDate(dBeginDate)+"-1 "+
          " group by FCuryCode,FPORTCODE) group by FPortCode,FCuryCode";

      }
          try {

        	rs = dbl.openResultSet(sqlStr);
            setResultValue(valueMap, rs);
      }
      catch (SQLException ex1) {
         throw new YssException("獲取unrealised的起初基準數據出錯！");
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }

   }

    /**
    * 应收应付汇兑
    * 包括：清算款、应收股息、运营应收应付
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getRecpayEntiy(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }

    ///当月结余
    String  sqlStr =
    	"select " +
        dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
        dbl.sqlJN() +
        "'RecPay'" + dbl.sqlJN() +
        "'RecPay99'" +
        dbl.sqlJN() + "'HKD'" + dbl.sqlJN() + dbl.sqlString(portCode)+
        dbl.sqlJN() + unrealisedType_YearTodateFx + "" +
        " as FCode,'.  Year-to-date difference' as FName," +
        unrealisedType_YearTodateFx + " as FUnrealisedType, " +
        dbl.sqlDate(dEndDate) +
        " as FDate,"+dbl.sqlString(portCode)+" as FPortCode,'HKD' as FCuryCode,'RecPay' AS FAccType,'RecPay99' as FSubAccType" +
        ",sum(huidui) as FBal,sum(huidui) as FBaseCuryBal" +
        " from " +
        " ("+
        " select nvl(cash.fbal,0)+nvl(invest.fbal,0)+nvl(sec.fbal,0)+nvl(de.fbal,0)-nvl(bond.fbal,0) as huidui from ( "+    //modify by ctq 20091222

        " select sum(round(nvl(decode(substr(fsubtsftypecode, 3, 2), '06', 1, -1) *j.fportcurybal,0),2)) as fbal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " j  "+
        " where j.fcheckstate = 1  and j.fportcode = " + dbl.sqlString(this.portCode) +
        " and j.fstoragedate ="+dbl.sqlDate(this.dEndDate)+"   and j.ftsftypecode ='99' "+
        "  and j.fsubtsftypecode in ('9906TD','9906DV','9906DE','9907TD','9907OT') ) cash ,"+

        " (select sum(round(nvl(decode(substr(fsubtsftypecode, 3, 2), '06', 1, -1) *i.fportcurybal,0),2)) as fbal from " + pub.yssGetTableName("tb_stock_investpayrec") + " i " +
        " where i.fcheckstate=1 and i.fportcode= "+dbl.sqlString(this.portCode)+
        " and i.fstoragedate= "+dbl.sqlDate(this.dEndDate)+"   and i.ftsftypecode='99' "+
        " and i.fsubtsftypecode in ('9907IV','9906IV')) invest, "+

        " (select sum(round(nvl(decode(substr(fsubtsftypecode, 3, 2), '06', 1, -1) *K.fportcurybal, 0),2)) as fbal " +
        " from " + pub.yssGetTableName("Tb_Stock_Secrecpay") + " K " +
        " where k.fcheckstate = 1 " +
        " and k.fportcode = "+dbl.sqlString(this.portCode)+
        " and k.fstoragedate = "+dbl.sqlDate(this.dEndDate) +
        " and k.ftsftypecode = '99'  and k.fsubtsftypecode in ('9906FI','9907FI','9906EQ')) sec ," +

        //增加活期存款的汇兑损益，取自VAL表的汇兑损益字段 modify by ctq 20091222
        " (select sum(round(nvl(fsyvbasecurybal,0),2)) as fbal from tb_data_portfolioVal " +
        " where forder like '01##0101%' and  forder not like '%total' and fportcode=" + dbl.sqlString(this.portCode) +
        " and fsecuritycode is not null " +
        " and fvaldate=" + dbl.sqlDate(this.dEndDate) + ") de ," +

        //因为买入利息不计算汇兑损益，所以还需要减去该部分数值
        " (select sum(round(round(t1.fstoragecost,2)*nvl(t3.fbaserate,1),2)-round(t1.fportcurycost,2)) as fbal " +
        " from " + pub.yssGetTableName("tb_stock_purbond") + " t1 " +
        " join " + pub.yssGetTableName("tb_para_security") + " t2 on t1.fsecuritycode=t2.fsecuritycode " +
        //将下面left join 更改为join
        " join " + pub.yssGetTableName("tb_data_valrate") + " t3 on t3.fcurycode=t2.ftradecury " +
        " and t3.fvaldate=(select max(fvaldate) from " + pub.yssGetTableName("tb_data_valrate") +
        " t4 where t4.fcurycode=t3.fcurycode " +
        " and t4.fvaldate<= " + dbl.sqlDate(this.dEndDate) +
        " and t4.Fportcode = " + dbl.sqlString(this.portCode) +
        " ) and t3.FportCode = " + dbl.sqlString(this.portCode) +
        " where t1.fstoragedate= to_date(to_char(add_months(" + dbl.sqlDate(this.dEndDate) + ",1),'yyyyMM')||'01','yyyyMMdd')" +
        " and t1.fportcode=" + dbl.sqlString(this.portCode) + ") bond " +

        " ) ";


    ResultSet rs = null;
    try {
       rs = dbl.openResultSet(sqlStr);
       setResultValue(valueMap, rs);
    }
    catch (YssException ex) {
       throw new YssException("获取Equity数据出错！", ex);
    }
    catch (SQLException ex) {
       throw new YssException(ex.getMessage());
    }
    finally{
       dbl.closeResultSetFinal(rs);
    }

    //上期余额
    if (YssFun.formatDate(this.dEndDate, "MM").equals("01")){
  	  	sqlStr = "select " + dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) + dbl.sqlJN() + "'RecPay'" + dbl.sqlJN() + "'RecPay99' "
  	  		+ dbl.sqlJN() + "'HKD'" + dbl.sqlJN() + dbl.sqlString(portCode)+ dbl.sqlJN() + unrealisedType_AccumulateFx + " as FCode,"
  	  		+ "'.  Accumulated difference b/f' as FName," + unrealisedType_AccumulateFx + " as FUnrealisedType, "
  	  		+ dbl.sqlDate(dEndDate) + " as FDate," + dbl.sqlString(portCode)+" as FPortCode,'HKD' AS FCuryCode,'RecPay' AS FAccType,'RecPay99' AS FSubAccType"
  	  		+ ",sum(fbal) as FBal,sum(FPortCuryBal) as FBaseCuryBal  from "
  	  		+ " ( select FPortCode,FCuryCode,sum(fbal) as FBal,sum(FBaseCuryBal) as FBaseCuryBal, sum(FPortCuryBal) as FPortCuryBal "
  	  		+ " from (select CashStorage.*  from (select *  from " + pub.yssGetTableName("TB_STOCK_Cashpayrec") + " j "
  	  		+ "  where FCheckState = 1   and FPortCode = " + dbl.sqlString(this.portCode) + " and FAnalysisCode2 = 'OT' "
  	  		+ "  and FYearMonth = to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00') CashStorage "
  	  		+ " join (select *    from " + pub.yssGetTableName("Tb_Para_cashaccount") + "  where FCheckState = 1 "
  	  		+ " and FPortCode ="+dbl.sqlString(this.portCode)+"   and FAccType = '04'  and FSubAccType = '0414') "
  	  		+  " cashCount on CashStorage.FCashAccCode = cashCount.FCashAccCode) "
  	  		+ " group by FStorageDate, FPortCode, FCuryCode  ) group by FCuryCode,FPortCode";
    }else {
sqlStr = "select " +
      dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
      dbl.sqlJN() +
      "'RecPay'" + dbl.sqlJN() +
      "'RecPay99' " +
      dbl.sqlJN() + "'HKD'"
      + dbl.sqlJN() + dbl.sqlString(portCode) +
      dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
      " as FCode,'.  Accumulated difference b/f' as FName," +
      unrealisedType_AccumulateFx + " as FUnrealisedType, " +
      dbl.sqlDate(dEndDate) +
      " as FDate," + dbl.sqlString(portCode) + " as FPortCode,'HKD' AS FCuryCode,'RecPay' AS FAccType,'RecPay99' AS FSubAccType" +
      ",sum(FBaseCuryBal) as FBal,sum(FBaseCuryBal) as FBaseCuryBal" +
      " from " + pub.yssGetTableName("tb_Data_Unrealised") + " a "
      + " where a.FDate =  last_day(add_months(" + dbl.sqlDate(dEndDate) + ", -1)) and fportcode = "
      + dbl.sqlString(portCode) +
      " and FUnrealisedType = 4  and FCode like '%RecPayRecPay99%' ";
		 }
      try {

               rs = dbl.openResultSet(sqlStr);
               setResultValue(valueMap, rs);
               dbl.closeResultSetFinal(rs);
      }
      catch (SQLException ex1) {
         throw new YssException("獲取unrealised的起初基準數據出錯！");
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }

   }


   /**
    * 基金类月明细
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getFundDetail(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            "'TR'" + dbl.sqlJN() +
            "'TR01' " +
            dbl.sqlJN() + "FCuryCode" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_YearTodateFx + "" +
            " as FCode,'.  Year-to-date difference' as FName," +
            unrealisedType_YearTodateFx + " as FUnrealisedType, " +
            dbl.sqlDate(dEndDate) +
            " as FDate,FPortCode,FCuryCode,'TR' AS FCatCode,'TR01' AS  FSubCatCode" +
            ",sum(ybal) as FBal,sum(ybal) as FBaseCuryBal" +
            " from " +
            " (select sum(FSyvBaseCuryBal)  as ybal,FCuryCode,v.FPORTCODE from tb_data_PortfolioVal v" +
            " where v.forder like 'TR__TR%'  "+
            " and FPortCode = " +dbl.sqlString(this.portCode) +
            " and v.forder not like '%HKD%' and "+
            " v.forder  like '%total%' "+
            " and FValDate= "+ dbl.sqlDate(dEndDate) + " group by FCuryCode,FPORTCODE)  group by FCuryCode, FPORTCODE ";

      ResultSet rs = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         setResultValue(valueMap, rs);
      }
      catch (YssException ex) {
         throw new YssException("获取Equity数据出错！", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
      if (YssFun.formatDate(this.dEndDate, "MM").equals("01")){
    	  sqlStr = "select " +
          dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
          dbl.sqlJN() +
          "'TR'" + dbl.sqlJN() +
          "'TR01' " +
          dbl.sqlJN() + "FCuryCode"
          + dbl.sqlJN() + "FPortCode" +
          dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
          " as FCode,'.  Accumulated difference b/f' as FName," +
          unrealisedType_AccumulateFx + " as FUnrealisedType, " +
          dbl.sqlDate(dEndDate) +
          " as FDate,FPortCode,FCuryCode,'TR' as FAccType,'TR01' as FSubAccType" +
          ",sum(FBal) as FBal,sum(FPortCuryBal) as FBaseCuryBal" +
          " from " +
          " ( select FPortCode,FCuryCode,sum(fbal) as FBal,sum(FBaseCuryBal) as FBaseCuryBal," +
          " sum(FPortCuryBal) as FPortCuryBal  "+
          " from (select CashStorage.*   from (select *  from " + pub.yssGetTableName("TB_STOCK_Cashpayrec") + " j "+
          "  where FCheckState = 1   and FPortCode = "+dbl.sqlString(this.portCode)+"    and FAnalysisCode2 = 'TR' "+
          "  and FCuryCode <> 'HKD'    and FYearMonth =to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00') CashStorage "+
          " join (select * from " + pub.yssGetTableName("Tb_Para_cashaccount") + " where FCheckState = 1 "+
          " and FPortCode ="+dbl.sqlString(this.portCode)+"   and FAccType = '04'  and FSubAccType = '0414') "+
          " cashCount on CashStorage.FCashAccCode = cashCount.FCashAccCode) "+
          " group by FStorageDate, FPortCode, FCuryCode  ) group by FCuryCode,FPortCode";
      }
      else {


      sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            "'TR'" + dbl.sqlJN() +
            "'TR01' " +
            dbl.sqlJN() + "FCuryCode"
            + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
            " as FCode,'.  Accumulated difference b/f' as FName," +
            unrealisedType_AccumulateFx + " as FUnrealisedType, " +
            dbl.sqlDate(dEndDate) +
            " as FDate,FPortCode,FCuryCode,'TR' AS FCatCode,'TR01' AS FSubCatCode" +
            ",sum(ybal) as FBal,sum(ybal) as FBaseCuryBal" +
            " from " +
            " ("+
            " select sum(FSyvBaseCuryBal)  as ybal,FCuryCode,v.FPORTCODE from tb_data_PortfolioVal v " +
            " where v.forder like 'TR__TR%'" +
            " and v.forder not like '%HKD%' and "+
            " FPortCode = " +dbl.sqlString(this.portCode) +
            " and v.forder  like '%total%'  "+
            " and FValDate="+dbl.sqlDate(dBeginDate) +"-1 "+
            " group by FCuryCode,FPORTCODE) group by FCuryCode,FPortCode";

      }
      try {

               rs = dbl.openResultSet(sqlStr);
               setResultValue(valueMap, rs);
               dbl.closeResultSetFinal(rs);

      }
      catch (SQLException ex1) {
         throw new YssException("獲取unrealised的起初基準數據出錯！");
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }


   /**
    * 股票类月明细
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getEachEquitis(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            "'EQ'" + dbl.sqlJN() +
            "'EQ01' " +
            dbl.sqlJN() + "FCuryCode" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_YearTodateFx + "" +
            " as FCode,'.  Year-to-date difference' as FName," +
            unrealisedType_YearTodateFx + " as FUnrealisedType, " +
            dbl.sqlDate(dEndDate) +
            " as FDate,FPortCode,FCuryCode,'EQ' AS FCatCode,'EQ01' AS  FSubCatCode" +
            ",sum(ybal) as FBal,sum(ybal) as FBaseCuryBal" +
            " from " +
            " (select sum(FSyvBaseCuryBal)  as ybal,FCuryCode,v.FPORTCODE from tb_data_PortfolioVal v" +
            " where v.forder like 'EQ__EQ01%'  "+
            " and FPortCode = " +dbl.sqlString(this.portCode) +
            " and v.forder not like '%HKD%' and "+
            " v.forder  like '%total%' "+
            " and FValDate= "+ dbl.sqlDate(dEndDate) + " group by FCuryCode,FPORTCODE)  group by FCuryCode, FPORTCODE ";

      ResultSet rs = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         setResultValue(valueMap, rs);
      }
      catch (YssException ex) {
         throw new YssException("获取Equity数据出错！", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
      if (YssFun.formatDate(this.dEndDate, "MM").equals("01")){
    	  sqlStr = "select " +
          dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
          dbl.sqlJN() +
          "'EQ'" + dbl.sqlJN() +
          "'EQ01' " +
          dbl.sqlJN() + "FCuryCode"
          + dbl.sqlJN() + "FPortCode" +
          dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
          " as FCode,'.  Accumulated difference b/f' as FName," +
          unrealisedType_AccumulateFx + " as FUnrealisedType, " +
          dbl.sqlDate(dEndDate) +
          " as FDate,FPortCode,FCuryCode,'EQ' as FAccType,'EQ01' as FSubAccType" +
          ",sum(FBal) as FBal,sum(FPortCuryBal) as FBaseCuryBal" +
          " from " +
          " ( select FPortCode,FCuryCode,sum(fbal) as FBal,sum(FBaseCuryBal) as FBaseCuryBal," +
          " sum(FPortCuryBal) as FPortCuryBal  "+
          " from (select CashStorage.*   from (select *  from " + pub.yssGetTableName("TB_STOCK_Cashpayrec") + " j "+
          "  where FCheckState = 1   and FPortCode = "+dbl.sqlString(this.portCode)+"    and FAnalysisCode2 = 'EQ' "+
          "  and FCuryCode <> 'HKD'    and FYearMonth =to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00') CashStorage "+
          " join (select *    from  " + pub.yssGetTableName("Tb_Para_cashaccount") + "  where FCheckState = 1 "+
          " and FPortCode ="+dbl.sqlString(this.portCode)+"   and FAccType = '04'  and FSubAccType = '0414') "+
          " cashCount on CashStorage.FCashAccCode = cashCount.FCashAccCode) "+
          " group by FStorageDate, FPortCode, FCuryCode  ) group by FCuryCode,FPortCode";
      }
      else {


      sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            "'EQ'" + dbl.sqlJN() +
            "'EQ01' " +
            dbl.sqlJN() + "FCuryCode"
            + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
            " as FCode,'.  Accumulated difference b/f' as FName," +
            unrealisedType_AccumulateFx + " as FUnrealisedType, " +
            dbl.sqlDate(dEndDate) +
            " as FDate,FPortCode,FCuryCode,'EQ' AS FCatCode,'EQ01' AS FSubCatCode" +
            ",sum(ybal) as FBal,sum(ybal) as FBaseCuryBal" +
            " from " +
            " ("+
            " select sum(FSyvBaseCuryBal)  as ybal,FCuryCode,v.FPORTCODE from tb_data_PortfolioVal v " +
            " where v.forder like 'EQ__EQ01%'" +
            " and v.forder not like '%HKD%' and "+
            " FPortCode = " +dbl.sqlString(this.portCode) +
            " and v.forder  like '%total%'  "+
            " and FValDate="+dbl.sqlDate(dBeginDate) +"-1 "+
            " group by FCuryCode,FPORTCODE) group by FCuryCode,FPortCode";

      }
      try {

               rs = dbl.openResultSet(sqlStr);
               setResultValue(valueMap, rs);
               dbl.closeResultSetFinal(rs);

      }
      catch (SQLException ex1) {
         throw new YssException("獲取unrealised的起初基準數據出錯！");
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   /**
    * 债券类月明细
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getEachdebtSecurities(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }

      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            "'FI'" + dbl.sqlJN() +
            "'FINOT05' " +
            dbl.sqlJN() + "FCuryCode" + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_YearTodateFx + "" +
            " as FCode,'.  Year-to-date difference' as FName," +
            unrealisedType_YearTodateFx + " as FUnrealisedType, " +
            dbl.sqlDate(dEndDate) +
            " as FDate,FPortCode,FCuryCode,'FI' FCatCode,'FINOT05' as FSubCatCode" +
            ",sum(nbal) as FBal,sum(nbal) as FBaseCuryBal" +
            " from " +
            " (select sum(FSyvBaseCuryBal) as nbal ,FCuryCode,V.FPORTCODE from tb_data_PortfolioVal v where " +
            " v.forder like 'FI%'"+
            " and v.forder not like '%HKD%' and "+
            " FPortCode = " +dbl.sqlString(this.portCode) +
            " and v.forder  like '%total%'  "+
            "　and FValDate= "+ dbl.sqlDate(dEndDate) +"　group by FCuryCode,FPORTCODE　"+
            " ) group by FCuryCode,FPortCode";
      ResultSet rs = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         setResultValue(valueMap, rs);
      }
      catch (YssException ex) {
         throw new YssException("获取debt securities数据出错！", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
      if (YssFun.formatDate(this.dEndDate, "MM").equals("01")){
    	  sqlStr = "select " +
          dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
          dbl.sqlJN() +
          "'FI'" + dbl.sqlJN() +
          "'FINOT05' " +
          dbl.sqlJN() + "FCuryCode"
          + dbl.sqlJN() + "FPortCode" +
          dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
          " as FCode,'.  Accumulated difference b/f' as FName," +
          unrealisedType_AccumulateFx + " as FUnrealisedType, " +
          dbl.sqlDate(dEndDate) +
          " as FDate,FPortCode,FCuryCode,'FI' as FAccType,'FINOT05' as FSubAccType" +
          ",sum(FBal) as FBal,sum(FPortCuryBal) as FBaseCuryBal" +
          " from " +
          " ( select FPortCode,FCuryCode,sum(fbal) as FBal,sum(FBaseCuryBal) as FBaseCuryBal," +
          " sum(FPortCuryBal) as FPortCuryBal  "+
          " from (select CashStorage.*   from (select *  from " + pub.yssGetTableName("TB_STOCK_Cashpayrec") + " j "+
          "  where FCheckState = 1   and FPortCode = "+dbl.sqlString(this.portCode)+ " and FAnalysisCode2 = 'FI' "+
          "  and FCuryCode <> 'HKD'    and FYearMonth =to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00') CashStorage "+
          " join (select *    from " + pub.yssGetTableName("Tb_Para_cashaccount") + " where FCheckState = 1 "+
          " and FPortCode ="+dbl.sqlString(this.portCode)+"   and FAccType = '04'  and FSubAccType = '0414') "+
          " cashCount on CashStorage.FCashAccCode = cashCount.FCashAccCode) "+
          " group by FStorageDate, FPortCode, FCuryCode  ) group by FCuryCode,FPortCode";
      }
      else {

      sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() +
            "'FI'" + dbl.sqlJN() +
            "'FINOT05' " +
            dbl.sqlJN() + "FCuryCode"
            + dbl.sqlJN() + "FPortCode" +
            dbl.sqlJN() + unrealisedType_AccumulateFx + "" +
            " as FCode,'.  Accumulated difference b/f' as FName," +
            unrealisedType_AccumulateFx + " as FUnrealisedType, " +
            dbl.sqlDate(dEndDate) +
            " as FDate,FPortCode,FCuryCode,'FI' AS FCatCode, 'FINOT05' as FSubCatCode" +
            ",sum(ybal) as FBal,sum(ybal) as FBaseCuryBal" +
            " from " +
            " (select sum(FSyvBaseCuryBal) as ybal ,FCuryCode,FPORTCODE from tb_data_PortfolioVal v "+
            "  where v.forder like 'FI%' and v.forder not like '%HKD%' and "+
            " v.forder  like '%total%'  "+
            "  and FPortCode = " +dbl.sqlString(this.portCode) +
            " and FValDate= "+ dbl.sqlDate(dBeginDate) +"-1 group by FCuryCode,FPORTCODE)" +
            " group by FCuryCode,FPortCode";
      }
      try {

            rs = dbl.openResultSet(sqlStr);
            setResultValue(valueMap, rs);
            dbl.closeResultSetFinal(rs);

      }
      catch (SQLException ex1) {
         throw new YssException("獲取unrealised的起初基準數據出錯！");
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }

   }

   /**
    * 将未实现的数据封装放入HashMap中。
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
      int columns = 0;
      boolean existAcc = false;
      try {
         ResultSetMetaData metaData = rs.getMetaData();
         columns = metaData.getColumnCount();
         for (int i = 1; i <= columns; i++) {
            if (metaData.getColumnName(i).equalsIgnoreCase("FAccType")) {
               existAcc = true;
               break;
            }
         }
      }
      catch (SQLException ex1) {
         throw new YssException(ex1.getMessage());
      }
      UnrealisedBean unrealised = null;
      try {
         while (rs.next()) {
            unrealised = new UnrealisedBean();
            unrealised.Code = rs.getString("FCode");
            unrealised.Name = rs.getString("FName");
            unrealised.CatCode = existAcc ? rs.getString("FAccType") :
                  rs.getString("FCatCode");
            unrealised.SubCatCode = existAcc ? rs.getString("FSubAccType") :
                  rs.getString("FSubCatCode");
            unrealised.CuryCode = rs.getString("FCuryCode");
            unrealised.Bal = rs.getDouble("FBal");
            unrealised.BaseCuryBal = rs.getDouble("FBaseCuryBal");
            unrealised.Date = rs.getDate("FDate");
            unrealised.UnrealisedType = rs.getInt("FUnRealisedType");
            unrealised.portCode = rs.getString("FPortCode");
            valueMap.put(unrealised.Code, unrealised);
         }
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }
}

