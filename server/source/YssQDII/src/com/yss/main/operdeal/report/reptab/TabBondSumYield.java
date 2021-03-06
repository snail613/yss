package com.yss.main.operdeal.report.reptab;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import java.sql.*;
import java.util.*;
import com.yss.main.operdeal.bond.*;
import com.yss.main.operdeal.bond.BaseBondOper;
import com.yss.pojo.cache.*;
import com.yss.base.BaseAPOperValue;

//计算持有到期债券收益率
public class TabBondSumYield
      extends BaseAPOperValue {
   private String tabInd;
   private String grpbyFields;
   private String whereSql = "";
   private java.util.Date startDate; //起始日期
   private java.util.Date endDate; //截止日期
   private String costType = "";//成本类型

   public String getTabInd() {
      return tabInd;
   }

   public void setGrpbyFields(String grpbyFields) {
      this.grpbyFields = grpbyFields;
   }

   public void setTabInd(String tabInd) {
      this.tabInd = tabInd;
   }

   public void setWhereSql(String whereSql) {
      this.whereSql = whereSql;
   }

   public void setCostType(String costType) {
      this.costType = costType;
   }

   public String getGrpbyFields() {
      return grpbyFields;
   }

   public String getWhereSql() {
      return whereSql;
   }

   public String getCostType() {
      return costType;
   }

   public TabBondSumYield() {
   }

   /**
    * getOperDoubleValue
    *
    * @return double
    */
   public double getOperDoubleValue() {
      return 0.0;
   }

   /**
    * init
    *
    * @param bean Object
    */
   public void init(Object bean) throws YssException {
      String reqAry[] = null;
      String reqAry1[] = null;
      String sRowStr = (String) bean;
      if (sRowStr.trim().length() == 0) {
         return;
      }
      reqAry = sRowStr.split("\n");
      reqAry1 = reqAry[0].split("\r");
      this.startDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.endDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[2].split("\r");
      this.costType = reqAry1[1];
   }

   /**
    * invokeOperMothed
    *
    * @return Object
    */
   public Object invokeOperMothed() throws YssException {
      createTmpTable();
      loadBondSumYield();
      return "";
   }

   protected void createTmpTable() throws YssException {
      String strSql = "";
      try {
         if (dbl.yssTableExist("tb_tmp_ZqSumSyl_" + tabInd + "_" +
                               pub.getUserCode())) {
        	 /**shashijie ,2011-10-12 , STORY 1698*/
            dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                           "tb_tmp_ZqSumSyl_" + tabInd + "_" +
                           pub.getUserCode()));
            /**end*/
         }

         strSql = "create table tb_tmp_ZqSumSyl_" + tabInd + "_" +
               pub.getUserCode() +
               "( FPortCode varchar2(20)," +
               " FSecurityCode varchar2(20)," +
               " FInvMgrCode varchar2(20)," +
               " FTradeCury varchar2(20)," +
               " FZqSumSyl decimal(18, 4)," +
               " FDate Date)";
         dbl.executeSql(strSql);
      }
      catch (Exception e) {
         throw new YssException(e);
      }
   }

   protected void loadBondSumYield() throws YssException {
      ResultSet rs = null;
      String strSql = "";
      String sKey = "";
      String[] fieldAry = null;
      double dTotalCost = 0;
      double dYield = 0;
      double dSumYield = 0;
      HashMap hmTotalCost = new HashMap();
      HashMap hmSumYield = new HashMap();
      HashMap hmFieldType = new HashMap();
      YssKeyDouble keyDouble = null;
      try {
         BaseBondOper bondOper = (BaseBondOper) pub.getOperDealCtx().getBean(
               "bondoper");
         fieldAry = grpbyFields.split(",");
         strSql = "select " + grpbyFields +
               ",sum(" + this.costType + ") as FTotalCost from " +
               pub.yssGetTableName("Tb_Data_SubTrade") +
               " a join (select * from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " where FCheckState = 1 and FCatCode = 'FI') b on a.FSecurityCode = b.FSecurityCode " +
               " where a.FCheckState = 1 and a.FBargainDate between " +
               dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate) +
               " and a.FTradeTypeCode = " +
               dbl.sqlString(YssOperCons.YSS_JYLX_Buy) +
               " group by " + grpbyFields;        //?
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            sKey = getKey(rs, fieldAry);
            hmTotalCost.put(sKey, new Double(rs.getDouble("FTotalCost")));
         }
         dbl.closeResultSetFinal(rs);

         strSql = "select a.FTradePrice,a.FBargainDate,a." + this.costType + "," +
               grpbyFields + ",c.* from " +
               pub.yssGetTableName("Tb_Data_SubTrade") +
               " a left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode = b.FTradeTypeCode" +
               "  join (select * from " +
               pub.yssGetTableName("Tb_Para_FixInterest") +
               " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode" +
               " where a.FCheckState = 1 and FBargainDate between " +
               dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate) +
               " and a.FTradeTypeCode = " +
               dbl.sqlString(YssOperCons.YSS_JYLX_Buy);
         rs = dbl.openResultSet(strSql);
         hmFieldType = dbFun.getFieldsType(rs);
         while (rs.next()) {
            dSumYield = 0;
            sKey = getKey(rs, fieldAry);
            dTotalCost = ( (Double) hmTotalCost.get(sKey)).doubleValue();
//            dYield = bondOper.getBondYield(rs.getDouble("FInsFrequency"),
//                                           rs.getDouble("FFaceValue"),
//                                           rs.getDouble("FFaceRate"),
//                                           rs.getDouble("FTradePrice"),
//                                           rs.getDate("FBargainDate"),
//                                           rs.getDate("FInsStartDate"),
//                                           rs.getDate("FInsEndDate"));
            /*组合加权
              A 债券成本 ６０００，YTM为3%
              B债券成本4000, ＹＴＭ为4%

              整个组合回报 (6000/(4000+6000))*3%+(4000/(4000+6000))*4%
             */
            dSumYield = YssD.mul(YssD.div(rs.getDouble(this.costType),
                                          dTotalCost), dYield);
            if (!hmSumYield.containsKey(sKey)) {
               keyDouble = new YssKeyDouble();
               if (hmFieldType.containsKey("FPortCode".toUpperCase())) {
                  keyDouble.setStrPortCode(rs.getString("FPortCode"));
               }
               if (hmFieldType.containsKey("FInvMgrCode".toUpperCase())) {
                  keyDouble.setStrInvMgrCode(rs.getString("FInvMgrCode"));
               }
               if (hmFieldType.containsKey("FTradeCury".toUpperCase())) {
                  keyDouble.setStrCuryCode(rs.getString("FTradeCury"));
               }
               if (hmFieldType.containsKey("FSecurityCode".toUpperCase())) {
                  keyDouble.setSecurityCode(rs.getString("FSecurityCode"));
               }
               keyDouble.setBalance(dSumYield);
               hmSumYield.put(sKey,keyDouble);
            }else{
               keyDouble = (YssKeyDouble) hmSumYield.get(sKey);
               dSumYield += keyDouble.getBalance();
               keyDouble.setBalance(dSumYield);
            }
         }
         saveloadBondSumYield(hmSumYield);
      }
      catch (Exception e) {
         throw new YssException(e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   protected void saveloadBondSumYield(HashMap hmResult) throws YssException {
      PreparedStatement pst = null;
      YssKeyDouble keyDouble = null;
      String strSql = "";
      boolean bTrans = false;
      Connection conn = dbl.loadConnection();
      try {
         strSql = "insert into tb_tmp_ZqSumSyl_" + tabInd + "_" +
               pub.getUserCode() +
               " (FSecurityCode,FPortCode,FInvMgrCode,FTradeCury,FZqSumSyl,FDate) " +
               " values (?,?,?,?,?,?)";
         pst = conn.prepareStatement(strSql);

         Iterator iter = hmResult.values().iterator();
         conn.setAutoCommit(false);
         bTrans = true;
         while (iter.hasNext()) {
            keyDouble = (YssKeyDouble) iter.next();
            if (keyDouble.getDAvgCostValue() != 0 ||
                keyDouble.getBalance() != 0) {
               pst.setString(1, keyDouble.getSecurityCode());
               pst.setString(2, keyDouble.getStrPortCode());
               pst.setString(3, keyDouble.getStrInvMgrCode());
               pst.setString(4, keyDouble.getStrCuryCode());
               pst.setDouble(5, YssFun.roundIt(keyDouble.getBalance(), 4));
               pst.setDate(6, YssFun.toSqlDate(this.endDate));

               pst.executeUpdate();
            }

         }
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (Exception e) {
          throw new YssException("向临时表插入数据出错！");
      }
	  //add by rujiangpeng 20100603打开多张报表系统需重新登录
		finally {
			dbl.closeStatementFinal(pst);
		}

   }

   private String getKey(ResultSet rs, String[] fieldAry) throws SQLException {
      String sKey = "";
      for (int i = 0; i < fieldAry.length; i++) {
         sKey += rs.getString(fieldAry[i]);
         if (i < fieldAry.length - 1) {
            sKey += "\f";
         }
      }
      return sKey;
   }
}
