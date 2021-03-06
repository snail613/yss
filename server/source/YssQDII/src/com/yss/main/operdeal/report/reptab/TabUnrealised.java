package com.yss.main.operdeal.report.reptab;

import com.yss.base.BaseAPOperValue;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import java.util.HashMap;
import java.sql.*;
import java.util.Iterator;

/**
 * <p>Title: </p>
 * 计算未兑现资产本金增值/贬值分布(浮动盈亏)
 * <p>Description: </p>
 * 此类只产生相应数据，要获取报表的值需要在数据源中配置获取。
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TabUnrealised
      extends BaseAPOperValue {
   private java.util.Date dBeginDate;
   private java.util.Date dEndDate;
   private String portCode;
   // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
   private boolean isCreate; //是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询
   // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出  end -----------
   
   
   private static final int unrealisedType_Accumulate = 0; //上月结余(估值)
   private static final int unrealisedType_YearTodate = 1; //当月累计(估值)
   private static final int unrealisedType_Total = 2; //当月发生(估值)

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

   public TabUnrealised() {
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
      this.dBeginDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.dEndDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[2].split("\r");
      this.portCode = reqAry1[1];
     // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   add by jiangshichao
      reqAry1 = reqAry[3].split("\r");
      this.isCreate = reqAry1[1].equalsIgnoreCase("0")?false:true;
      // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出  end -------------------
   }

   public Object invokeOperMothed() throws YssException {
      HashMap valueMap = null;
      createTempUnrealised();
      valueMap = new HashMap();
      try {
    	 if(isCreate){
    		 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
			 OffAcctBean offAcct = new OffAcctBean();
				offAcct.setYssPub(this.pub);
			 String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
			 String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
			 if(!tmpInfo.trim().equalsIgnoreCase("")) {
				 return "<OFFACCT>" + tmpInfo;
			 }
			 //=================end=================
    		 getUnrealisedValue(valueMap);
    	 }
        
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
         throw new YssException("生成临时未兑现资产本金表(浮动盈亏)出错!");
      }
   }

   private void deleteFromTempUnrealised() throws YssException {
      String sqlStr = "Delete from " +
            pub.yssGetTableName("tb_Data_Unrealised") +
            " where FDate = " + dbl.sqlDate(this.dEndDate) +
            " and FportCode = " + dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (" + unrealisedType_Accumulate + "," +
            unrealisedType_YearTodate +
            "," + unrealisedType_Total + ")";
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
            unrealised = (com.yss.main.operdeal.report.reptab.TabUnrealised.
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
         throw new YssException("insert error", ex);
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
      getEachFund(valueMap);
      getEachMoneyMarket(valueMap);
      insertToTempUnrealised(valueMap);
      getSumEquitis();
      getSumDebtSecurities();
      getSumFund();
      getSumMoneyMarket();
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


   /**
    * 获取资产类别为债券的的汇总信息。
    * @throws YssException
    */
   private void getSumDebtSecurities() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_Total  + dbl.sqlJN() +
            " FCatCode " + dbl.sqlJN() + " FSubCatCode" + " as FCode" +
            ",'Debt Securities-'" + dbl.sqlJN() +
            "case when FSubCatCode='FI' then 'Debt Securities' else " +
            "case when FSubCatCode='EQ' then 'Equities' else case  when  FSubCatCode='TR' then 'Fund Securities' else " +
            " case when FSubCatCode='WT' then 'Warrant' else case when FSubCatCode='MK' then 'MoneyMarket'  end  end" +
            "   end  end   end" + dbl.sqlJN() +
            "FCuryCode as FName, FCatCode, FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_Accumulate +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_Accumulate +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(this.dEndDate) + " as FDate," +
            this.unrealisedType_Total + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = 'FI' and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (0,1) and " +
            " FDate = " + dbl.sqlDate(this.dEndDate) +
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
    * 获取资产类别为货币市场的的汇总信息。
    * @throws YssException
    */
   private void getSumMoneyMarket() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_Total  + dbl.sqlJN() +
            " FCatCode " + dbl.sqlJN() + " FSubCatCode" + " as FCode" +
            ",'Money Market-'" + dbl.sqlJN() +
            "case when FSubCatCode='FI' then 'Debt Securities' else " +
            "case when FSubCatCode='EQ' then 'Equities' else case  when  FSubCatCode='TR' then 'Fund Securities' else " +
            " case when FSubCatCode='WT' then 'Warrant' else case when FSubCatCode='MK' then 'MoneyMarket'  end  end" +
            "   end  end   end" + dbl.sqlJN() +
            "FCuryCode as FName, FCatCode, FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_Accumulate +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_Accumulate +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(this.dEndDate) + " as FDate," +
            this.unrealisedType_Total + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = 'MK' and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (0,1) and " +
            " FDate = " + dbl.sqlDate(this.dEndDate) +
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
    * 获取资产类别为股票的汇总信息。
    * @throws YssException
    */
   private void getSumEquitis() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_Total + dbl.sqlJN() + "FCatCode" + dbl.sqlJN() +
            "FSubCatCode  as FCode " +
            ",'Equities-'" + dbl.sqlJN() +"  case when FSubCatCode='EQ' then 'Equities' else" +
            " case when FSubCatCode='FI' then 'Debt Securities' else case when FSubCatCode='TR' " +
            " then 'Fund Securities' else case when FSubCatCode='WT' then 'Warrant' else case when " +
            "  FSubCatCode='MK' then 'MoneyMarket' end  end  end  end  end"+ dbl.sqlJN() +
            "FCuryCode as FName,FCatCode, FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_Accumulate +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_Accumulate +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(this.dEndDate) + " as FDate," +
            this.unrealisedType_Total + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = 'EQ' " +
            " and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (0,1) and " +
            " FDate = " + dbl.sqlDate(this.dEndDate) +
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
    * 获取资产类别为基金的汇总信息。
    * @throws YssException
    */
   private void getSumFund() throws YssException {
      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_Total + dbl.sqlJN() + "FCatCode" + dbl.sqlJN() +
            "FSubCatCode  as FCode " +
            ",'Fund-'" + dbl.sqlJN() +"  case when FSubCatCode='EQ' then 'Equities' else" +
            " case when FSubCatCode='FI' then 'Debt Securities' else case when FSubCatCode='TR' " +
            " then 'Fund Securities' else case when FSubCatCode='WT' then 'Warrant' else case when " +
            "  FSubCatCode='MK' then 'MoneyMarket' end  end  end  end  end"+ dbl.sqlJN() +
            "FCuryCode as FName,FCatCode, FSubCatCode,FCuryCode" +
            ",sum(case when FUnrealisedType = " + unrealisedType_Accumulate +
            " then -1*FBal else FBal end) as FBal" +
            ",sum(case when FUnrealisedType = " + unrealisedType_Accumulate +
            " then -1*FBaseCuryBal else FBaseCuryBal end) as FBaseCuryBal,FPortCode," +
            dbl.sqlDate(this.dEndDate) + " as FDate," +
            this.unrealisedType_Total + " as FUnrealisedType " +
            " from " + pub.yssGetTableName("tb_Data_Unrealised") +
            " where FCatCode = 'TR' " +
            " and FPortCode = " +
            dbl.sqlString(this.portCode) +
            " and FUnrealisedType in (0,1) and " +
            " FDate = " + dbl.sqlDate(this.dEndDate) +
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
    *
    * 资类别为基金
    * 分资产类别、投资品种
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getEachFund(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }

      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_YearTodate + "" + dbl.sqlJN() +
            "'TR'" + dbl.sqlJN() +
            "pinzhong as FCode,'.  Year-to-date difference' as FName," +
            unrealisedType_YearTodate + " as FUnrealisedType, " +
            dbl.sqlDate(this.dEndDate) +
            " as FDate,FPortCode,FCuryCode,'TR' AS FCatCode,pinzhong as FSubCatCode" +
            ",sum(nbal) as FBal, sum(nbal) as FBaseCuryBal" +
            " from " +
            " ("+
            " select sum(ybal) as nbal,FCuryCode,FPORTCODE,pinzhong from ( "+
            " select FYKVBal*FBaseCuryRate  as ybal,FCuryCode,V.FPORTCODE,substr(v.forder,1,2) as pinzhong  from tb_data_PortfolioVal v where "+
            " v.forder like '__________TR__%' " +
            " and v.forder not like '%total%'" +
            " and FValDate=  "+dbl.sqlDate(this.dEndDate) + " and FYKVBal<>0  and FPORTCODE="+dbl.sqlString(this.portCode)+
            " )group by FCuryCode,FPORTCODE,pinzhong  "+
             ") group by FPortCode,FCuryCode,pinzhong ";
      ResultSet rs = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         setResultValue(valueMap, rs);
      }
      catch (YssException ex) {
         throw new YssException("获取资产类别为基金的数据出错！", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }


      if (YssFun.formatDate(this.dEndDate, "MM").equals("01")){
          sqlStr="select " +
                 dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
                 dbl.sqlJN() + "FPortCode"
                 + dbl.sqlJN() + "FCuryCode" +
                 dbl.sqlJN() + this.unrealisedType_Accumulate + "" + dbl.sqlJN() +
                 "'TR'" + dbl.sqlJN() +
                 "pinzhong as FCode,'.  Accumulated difference b/f' as FName," +
                  unrealisedType_Accumulate + " as FUnrealisedType, " +
                  dbl.sqlDate(this.dEndDate) +
                 " as FDate,FPortCode,FCuryCode,'TR' AS FCatCode,pinzhong as FSubCatCode" +
                 ",sum(fvportcurybal) as FBal,sum(fvportcurybal) as FBaseCuryBal " +
                 " from " +
                 " ( select j.fportcode,j.fsecuritycode,j.fcurycode,j.fbal,j.fvportcurybal,p.fcatcode as pinzhong"+
                 " from " + pub.yssGetTableName("Tb_Stock_Secrecpay") + " j  join  "+
                 pub.yssGetTableName("tb_para_security") + " p on j.fsecuritycode=p.fsecuritycode   where j.FCheckState = 1 "+
                 " and p.fcheckstate=1  and j.FPortCode =  "+dbl.sqlString(this.portCode)+"  and j.fattrclscode = 'TR' and j.ftsftypecode='09' " +
                 " and FYearMonth = to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00' ) group by fportcode,FCuryCode,pinzhong";
          }
          else {
      sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode"
            + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_Accumulate + "" + dbl.sqlJN() +
            "'TR'" + dbl.sqlJN() +
            "pinzhong as FCode,'.  Accumulated difference b/f' as FName," +
            unrealisedType_Accumulate + " as FUnrealisedType, " +
            dbl.sqlDate(this.dEndDate) +
            " as FDate,FPortCode,FCuryCode,'TR' AS FCatCode,pinzhong as FSubCatCode" +
            ",sum(FYKVBal) as FBal,sum(FYKVBal) as FBaseCuryBal " +
            " from " +
            " (select sum(ybal) as FYKVBal,FCuryCode,FPORTCODE,pinzhong from ( "+
            " select FYKVBal*FBaseCuryRate  as ybal,FCuryCode,V.FPORTCODE,substr(v.forder,1,2) as pinzhong  from tb_data_PortfolioVal v where "+
            " v.forder like '__________TR__%' " +
            " and v.forder not like '%total%'" +
            " and FYKVBal<>0 and  FPORTCODE="+dbl.sqlString(this.portCode)+
            " and FValDate="+dbl.sqlDate(this.dBeginDate) + "-1 ) "+
            " group by FCuryCode,FPORTCODE,pinzhong ) group by FPortCode,FCuryCode,pinzhong ";
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
    * 股票资产类别月明细
    * 分资产类别、投资品种
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getEachEquitis(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }

      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_YearTodate + "" + dbl.sqlJN() +
            "'EQ'" + dbl.sqlJN() +
            "pinzhong as FCode,'.  Year-to-date difference' as FName," +
            unrealisedType_YearTodate + " as FUnrealisedType, " +
            dbl.sqlDate(this.dEndDate) +
            " as FDate,FPortCode,FCuryCode,'EQ' AS FCatCode,pinzhong as FSubCatCode" +
            ",sum(nbal) as FBal, sum(nbal) as FBaseCuryBal" +
            " from " +
            " ("+
            " select sum(ybal) as nbal,FCuryCode,FPORTCODE,pinzhong from ( "+
            " select FYKVBal*FBaseCuryRate  as ybal,FCuryCode,V.FPORTCODE,substr(v.forder,1,2) as pinzhong  from tb_data_PortfolioVal v where "+
            " v.forder like '__________EQ__%' " +
            " and v.forder not like '%total%'" +
            " and FValDate=  "+dbl.sqlDate(this.dEndDate) + " and FYKVBal <>0 and FPORTCODE="+dbl.sqlString(this.portCode)+
            " )group by FCuryCode,FPORTCODE,pinzhong  "+
             ") group by FPortCode,FCuryCode,pinzhong ";
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
      sqlStr="select " +
             dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
             dbl.sqlJN() + "FPortCode"
             + dbl.sqlJN() + "FCuryCode" +
             dbl.sqlJN() + this.unrealisedType_Accumulate + "" + dbl.sqlJN() +
             "'EQ'" + dbl.sqlJN() +
             "pinzhong as FCode,'.  Accumulated difference b/f' as FName," +
              unrealisedType_Accumulate + " as FUnrealisedType, " +
              dbl.sqlDate(this.dEndDate) +
             " as FDate,FPortCode,FCuryCode,'EQ' AS FCatCode,pinzhong as FSubCatCode" +  //MS00311 QDV4中保2009年03月11日01_B  获取全部类型的股票,设置为LastAll
             ",sum(fvportcurybal) as FBal,sum(fvportcurybal) as FBaseCuryBal " +
             " from " +
             " ( select j.fportcode,j.fsecuritycode,j.fcurycode,j.fbal,j.fvportcurybal,p.fcatcode as pinzhong"+
             " from " + pub.yssGetTableName("Tb_Stock_Secrecpay") + " j  join  "+
             pub.yssGetTableName("tb_para_security") + " p on j.fsecuritycode=p.fsecuritycode   where j.FCheckState = 1 "+
             " and p.fcheckstate=1  and j.FPortCode =  "+dbl.sqlString(this.portCode)+"  and j.fattrclscode = 'EQ' and j.ftsftypecode='09' " +
             " and FYearMonth = to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00' ) group by fportcode,FCuryCode,pinzhong";
      }
      else {
      sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode"
            + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_Accumulate + "" + dbl.sqlJN() +
            "'EQ'" + dbl.sqlJN() +
            "pinzhong as FCode,'.  Accumulated difference b/f' as FName," +
            unrealisedType_Accumulate + " as FUnrealisedType, " +
            dbl.sqlDate(this.dEndDate) +
            " as FDate,FPortCode,FCuryCode,'EQ' AS FCatCode,pinzhong as FSubCatCode" +  //MS00311 QDV4中保2009年03月11日01_B  获取全部类型的股票,设置为LastAll
            ",sum(FYKVBal) as FBal,sum(FYKVBal) as FBaseCuryBal " +
            " from " +
            " (select sum(ybal) as FYKVBal,FCuryCode,FPORTCODE,pinzhong from ( "+
            " select FYKVBal*FBaseCuryRate  as ybal,FCuryCode,V.FPORTCODE,substr(v.forder,1,2) as pinzhong  from tb_data_PortfolioVal v where "+
            " v.forder like '__________EQ__%' " +
            " and v.forder not like '%total%'  and FYKVBal<>0 " +
            "  and  FPORTCODE="+dbl.sqlString(this.portCode)+
            " and FValDate="+dbl.sqlDate(this.dBeginDate) + "-1 ) "+
            " group by FCuryCode,FPORTCODE,pinzhong ) group by FPortCode,FCuryCode,pinzhong ";
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
    * 资产类别为债券的 月明细
    *
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getEachdebtSecurities(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }

      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_YearTodate + "" + dbl.sqlJN() +
            "'FI'" + dbl.sqlJN() +
            "pinzhong as FCode,'.  Year-to-date difference' as FName," +
            unrealisedType_YearTodate + " as FUnrealisedType, " +
            dbl.sqlDate(this.dEndDate) +
            " as FDate,FPortCode,FCuryCode,'FI' AS FCatCode,pinzhong as FSubCatCode" +
            ",sum(nbal) as FBal" +
            ",sum(nbal) as FBaseCuryBal" +
            " from " +
            " ( "+
            " select sum(ybal) as nbal,FCuryCode,FPORTCODE,pinzhong from ( "+
            "  select FYKVBal*FBaseCuryRate  as ybal,FCuryCode,V.FPORTCODE,substr(v.forder,1,2) as pinzhong " +
            " from tb_data_PortfolioVal v where "+
            " v.forder like '__________FI__%' " +
            " and v.forder not like '%total%'" +
            " and FValDate= "+dbl.sqlDate(this.dEndDate) +"  and FYKVBal<>0  and  FPORTCODE= "+dbl.sqlString(this.portCode)+")"+
            "   group by FCuryCode,FPORTCODE,pinzhong  "+
            " ) group by FPORTCODE,FCuryCode,pinzhong";
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
          sqlStr="select " +
                 dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
                 dbl.sqlJN() + "FPortCode"
                 + dbl.sqlJN() + "FCuryCode" +
                 dbl.sqlJN() + this.unrealisedType_Accumulate + "" + dbl.sqlJN() +
                 "'FI'" + dbl.sqlJN() +
                 "pinzhong as FCode,'.  Accumulated difference b/f' as FName," +
                  unrealisedType_Accumulate + " as FUnrealisedType, " +
                  dbl.sqlDate(this.dEndDate) +
                 " as FDate,FPortCode,FCuryCode,'FI' AS FCatCode,pinzhong as FSubCatCode" +  //MS00311 QDV4中保2009年03月11日01_B  获取全部类型的股票,设置为LastAll
                 ",sum(fvportcurybal) as FBal,sum(fvportcurybal) as FBaseCuryBal " +
                 " from " +
                 " ( select j.fportcode,j.fsecuritycode,j.fcurycode,j.fbal,p.fcatcode as pinzhong,fvportcurybal"+
                 " from  " + pub.yssGetTableName("Tb_Stock_Secrecpay") + " j join "+
                 pub.yssGetTableName("tb_para_security") + " p on j.fsecuritycode=p.fsecuritycode   where j.FCheckState = 1 "+
                 " and p.fcheckstate=1  and j.FPortCode = "+dbl.sqlString(this.portCode)+" and j.fattrclscode = 'FI' and (j.ftsftypecode='09' or  j.fsubtsftypecode='9909FI') " +
                 " and FYearMonth = to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00' ) group by fportcode,FCuryCode,pinzhong";
          }
          else {
      sqlStr =  "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode"
            + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_Accumulate + "" + dbl.sqlJN() +
            "'FI'" + dbl.sqlJN() +
            " pinzhong as FCode,'.  Accumulated difference b/f' as FName," +
            unrealisedType_Accumulate + " as FUnrealisedType, " +
            dbl.sqlDate(this.dEndDate) +
            " as FDate,FPortCode,FCuryCode,'FI' AS FCatCode,pinzhong  as FSubCatCode" +
            ",sum(ybal) as FBal" +
            ",sum(ybal) as FBaseCuryBal" +
            " from " +
            " (select sum(ybal) as ybal,FCuryCode,FPORTCODE,pinzhong from ( "+
            " select FYKVBal*FBaseCuryRate  as ybal,FCuryCode,V.FPORTCODE,substr(v.forder,1,2) as pinzhong  " +
            "  from tb_data_PortfolioVal v where "+
            " v.forder like '__________FI__%' " +
            " and v.forder not like '%total%' and FYKVBal<>0 " +
            " and FPORTCODE="+dbl.sqlString(this.portCode)+
            " and FValDate= "+dbl.sqlDate(this.dBeginDate) +"-1 "+
             ")group by FCuryCode,FPORTCODE,pinzhong ) group by FPortCode,FCuryCode,pinzhong";
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
    * 资产类别为货币市场的 月明细
    *
    * @param valueMap HashMap
    * @throws YssException
    */
   private void getEachMoneyMarket(HashMap valueMap) throws YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }

      String sqlStr = "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode" + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_YearTodate + "" + dbl.sqlJN() +
            "'MK'" + dbl.sqlJN() +
            "pinzhong as FCode,'.  Year-to-date difference' as FName," +
            unrealisedType_YearTodate + " as FUnrealisedType, " +
            dbl.sqlDate(this.dEndDate) +
            " as FDate,FPortCode,FCuryCode,'MK' AS FCatCode,pinzhong as FSubCatCode" +
            ",sum(nbal) as FBal" +
            ",sum(nbal) as FBaseCuryBal" +
            " from " +
            " ( "+
            " select sum(ybal) as nbal,FCuryCode,FPORTCODE,pinzhong from ( "+
            "  select FYKVBal*FBaseCuryRate  as ybal,FCuryCode,V.FPORTCODE,substr(v.forder,1,2) as pinzhong " +
            " from tb_data_PortfolioVal v where "+
            " v.forder like '__________MK__%' " +
            " and v.forder not like '%total%'" +
            " and FValDate= "+dbl.sqlDate(this.dEndDate) +"  and FYKVBal<>0  and  FPORTCODE= "+dbl.sqlString(this.portCode)+")"+
            "   group by FCuryCode,FPORTCODE,pinzhong  "+
            " ) group by FPORTCODE,FCuryCode,pinzhong";
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
          sqlStr="select " +
                 dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
                 dbl.sqlJN() + "FPortCode"
                 + dbl.sqlJN() + "FCuryCode" +
                 dbl.sqlJN() + this.unrealisedType_Accumulate + "" + dbl.sqlJN() +
                 "'MK'" + dbl.sqlJN() +
                 "pinzhong as FCode,'.  Accumulated difference b/f' as FName," +
                  unrealisedType_Accumulate + " as FUnrealisedType, " +
                  dbl.sqlDate(this.dEndDate) +
                 " as FDate,FPortCode,FCuryCode,'MK' AS FCatCode,pinzhong as FSubCatCode" +
                 ",sum(fvportcurybal) as FBal,sum(fvportcurybal) as FBaseCuryBal " +
                 " from " +
                 " ( select j.fportcode,j.fsecuritycode,j.fcurycode,j.fbal,p.fcatcode as pinzhong,fvportcurybal"+
                 " from  " + pub.yssGetTableName("Tb_Stock_Secrecpay") + " j  join "+
                 pub.yssGetTableName("tb_para_security") + " p on j.fsecuritycode=p.fsecuritycode   where j.FCheckState = 1 "+
                 " and p.fcheckstate=1  and j.FPortCode = "+dbl.sqlString(this.portCode)+" and j.fattrclscode = 'MK' and (j.ftsftypecode='09' or  j.fsubtsftypecode='9909FI') " +
                 " and FYearMonth = to_char("+dbl.sqlDate(this.dEndDate)+",'yyyy')||'00' ) group by fportcode,FCuryCode,pinzhong";
          }
          else {
      sqlStr =  "select " +
            dbl.sqlString(YssFun.formatDate(this.dEndDate, "yyyyMMdd")) +
            dbl.sqlJN() + "FPortCode"
            + dbl.sqlJN() + "FCuryCode" +
            dbl.sqlJN() + this.unrealisedType_Accumulate + "" + dbl.sqlJN() +
            "'MK'" + dbl.sqlJN() +
            " pinzhong as FCode,'.  Accumulated difference b/f' as FName," +
            unrealisedType_Accumulate + " as FUnrealisedType, " +
            dbl.sqlDate(this.dEndDate) +
            " as FDate,FPortCode,FCuryCode,'FI' AS FCatCode,pinzhong  as FSubCatCode" +
            ",sum(ybal) as FBal" +
            ",sum(ybal) as FBaseCuryBal" +
            " from " +
            " (select sum(ybal) as ybal,FCuryCode,FPORTCODE,pinzhong from ( "+
            " select FYKVBal*FBaseCuryRate  as ybal,FCuryCode,V.FPORTCODE,substr(v.forder,1,2) as pinzhong  " +
            "  from tb_data_PortfolioVal v where "+
            " v.forder like '__________MK__%' " +
            " and v.forder not like '%total%' and FYKVBal<>0 " +
            " and FPORTCODE="+dbl.sqlString(this.portCode)+
            " and FValDate= "+dbl.sqlDate(this.dBeginDate) +"-1 "+
             ")group by FCuryCode,FPORTCODE,pinzhong ) group by FPortCode,FCuryCode,pinzhong";
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
      UnrealisedBean unrealised = null;
      try {
         while (rs.next()) {
            unrealised = new UnrealisedBean();
            unrealised.Code = rs.getString("FCode");
            unrealised.Name = rs.getString("FName");
            unrealised.CatCode = rs.getString("FCatCode");
            unrealised.SubCatCode = rs.getString("FSubCatCode");
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
