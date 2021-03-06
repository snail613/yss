package com.yss.main.operdeal.report.reptab.valrep;

import com.yss.util.YssException;
import java.util.ArrayList;
import java.sql.*;
import com.yss.main.operdeal.report.reptab.valrep.pojo.ValRepBean;

public class SecurityValRep extends BaseValRep{
   public SecurityValRep() {
   }
   private ArrayList alSec=null;

public ArrayList getValRepData() throws YssException {
      alSec =new ArrayList();
      getStockValData();
      return alSec;
   }
   private void getBondBillData() throws YssException{

   }
   //合并邵宏伟修改报表代码 xuqiji 20100608
   private void getStockValData() throws YssException{
      String sqlStr="";
      ResultSet rs =null;
      try{
    	  sqlStr = "select b.FCatCode || '##' || b.FSubCatCode || '##' || a.FAssetType || '##' || a.FCuryCode || '##' || a.FSecurityCode as FOrderCode, a.FPortCode, a.FValDate, a.FSecurityCode, b.FSecurityName, a.FCuryCode, a.FBaseCuryRate, a.FAvgBaseCuryRate as FPortCuryRate, "
    		  + " FAmount, FAvgCost, round(a.FCost, 2) as FCost, 0 as FFee, round(a.FCost + 0, 2) as FTotalCost, nvl(c.FMarketPrice, FAvgCost) as FMarketPrice, nvl(a.FAmount*c.FMarketPrice, a.FCost) as FMarketValue, nvl(a.FAmount*c.FMarketPrice, a.FCost) - (a.FCost + 0) as FYKVBal, round(a.FCost*a.FBaseCuryRate - FPortCuryCost, 2) as FSyvbaseCuryBal, nvl(round(round(a.FAmount * c.FMarketPrice * a.FBaseCuryRate, 2)/FPortMarketValue, 4), 0) as FFundAllotProportion from ( "
    		  + " select FPortCode, FStorageDate as FValDate, FAttrClsCode as FAssetType, FCuryCode, FSecurityCode, FBaseCuryRate, round(case when sum(FVStorageCost) = 0 then FBaseCuryRate else sum(FVPortCuryCost)/sum(FVStorageCost) end, 5) as FAvgBaseCuryRate, sum(FStorageAmount) as FAmount, sum(FVStorageCost) as FCost, "
    		  + " round(sum(FVStorageCost)/sum(FStorageAmount), 4) as FAvgCost, sum(FVPortCuryCost) as FPortCuryCost from " + pub.yssGetTableName("tb_stock_security") + " where FPortCode = " + dbl.sqlString(sPortCode)
    		  + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and FStorageAmount <> 0 group by FPortCode, FStorageDate, FAttrClsCode, FCuryCode, FSecurityCode, FBaseCuryRate) a "
    		  + " join (select FCatCode, FSubCatCode, FSecurityCode, FSecurityName from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1 and FCatCode in ('EQ', 'TR')) b on a.FSecurityCode = b.FSecurityCode "
    		  + " left join (select price1.FSecurityCode, price1.FPrice as FMarketPrice from " + pub.yssGetTableName("tb_data_valmktprice") + " price1 join (select FSecurityCode, max(FValDate) FValDate from " + pub.yssGetTableName("tb_data_valmktprice")
    		  + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FSecurityCode) price2 on price1.FValDate = price2.FValDate and price1.FSecurityCode = price2.FSecurityCode where price1.FPortCode = " + dbl.sqlString(sPortCode) + ") c on a.FSecurityCode = c.FSecurityCode "
    		  + " left join (select FPortCode, FDate as FNavDate, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on a.FPortCode = p.FPortCode and a.FValDate = p.FNavDate "
    		  + " union "
    		  + " select FCatCode || '##' || FSubCatCode || '##' || FAssetType || '##' || FCuryCode || '##total' as FOrderCode, FPortCode, FValDate, '' as FSecurityCode, '港幣等值金額：' as FSecurityName, FCuryCode, FBaseCuryRate, 0 as FPortCuryRate, 0 as FAmount, 0 as FAvgCost, sum(FPortCuryCost) as FCost, "
    		  + " sum(FFee)*FBaseCuryRate as FFee, sum(FPortCuryCost)+sum(FPortFee) as FTotalCost, 0 FMarketPrice, sum(FMarketValue)*FBaseCuryRate as FMarketValue,sum(FYKVBal)*FBaseCuryRate as FYKVBal, sum(FSyvbaseCuryBal) as FSyvbaseCuryBal, sum(FFundAllotProportion) as FFundAllotProportion from ( "
    		  + " select a.FPortCode, a.FValDate, b.FCatCode, b.FSubCatCode, a.FAssetType, a.FSecurityCode, b.FSecurityName, a.FCuryCode, a.FBaseCuryRate, a.FAvgBaseCuryRate as FPortCuryRate, round(a.FCost, 2) as FCost, 0 as FFee, round(a.FCost + 0, 2) as FTotalCost, 0 FPortFee, round(FPortCuryCost, 2) as FPortCuryCost, "
    		  + " c.FMarketPrice, nvl(a.FAmount*c.FMarketPrice, a.FCost) as FMarketValue, nvl(a.FAmount*c.FMarketPrice, a.FCost) - (a.FCost + 0) as FYKVBal, round(a.FCost*a.FBaseCuryRate - FPortCuryCost, 2) as FSyvbaseCuryBal, nvl(round(round(a.FAmount * c.FMarketPrice * a.FBaseCuryRate, 2)/FPortMarketValue, 4), 0) as FFundAllotProportion from ( "
    		  + " select FPortCode, FStorageDate as FValDate, FAttrClsCode as FAssetType, FCuryCode, FSecurityCode, FBaseCuryRate, round(case when sum(FVStorageCost) = 0 then FBaseCuryRate else sum(FVPortCuryCost)/sum(FVStorageCost) end, 5) as FAvgBaseCuryRate, sum(FStorageAmount) as FAmount, sum(FVStorageCost) as FCost, "
    		  + " round(sum(FVStorageCost)/sum(FStorageAmount), 4) as FAvgCost, sum(FVPortCuryCost) as FPortCuryCost from " + pub.yssGetTableName("tb_stock_security") + " where FPortCode = " + dbl.sqlString(sPortCode)
    		  + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and FStorageAmount <> 0 group by FPortCode, FStorageDate, FAttrClsCode, FCuryCode, FSecurityCode, FBaseCuryRate) a "
    		  + " join (select FCatCode, FSubCatCode, FSecurityCode, FSecurityName from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1 and FCatCode in ('EQ', 'TR')) b on a.FSecurityCode = b.FSecurityCode "
    		  + " left join (select price1.FSecurityCode, price1.FPrice as FMarketPrice from " + pub.yssGetTableName("tb_data_valmktprice") + " price1 join (select FSecurityCode, max(FValDate) FValDate from " + pub.yssGetTableName("tb_data_valmktprice")
    		  + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FSecurityCode) price2 on price1.FValDate = price2.FValDate and price1.FSecurityCode = price2.FSecurityCode where price1.FPortCode = " + dbl.sqlString(sPortCode) + ") c on a.FSecurityCode = c.FSecurityCode "
    		  + " left join (select FPortCode, FDate as FNavDate, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on a.FPortCode = p.FPortCode and a.FValDate = p.FNavDate "
    		  + " ) group by FPortCode, FValDate, FCatCode, FSubCatCode, FAssetType, FCuryCode, FBaseCuryRate";
         rs = dbl.openResultSet(sqlStr);
         while(rs.next()){
            valBean =new ValRepBean();
            valBean.setOrder(rs.getString("FOrderCode"));
            valBean.setSecurityCode(rs.getString("FSecurityCode"));
            valBean.setSecurityName(rs.getString("FSecurityName"));
            valBean.setStorageAmount(rs.getDouble("FAmount"));
            valBean.setBaseCuryRate(rs.getDouble("FBasecuryRate"));
            valBean.setPortCuryRate(rs.getDouble("FPortcuryRate"));
            valBean.setAvgCost(rs.getDouble("FAvgCost"));
            valBean.setVstorageCost(rs.getDouble("FCost"));
            valBean.setTotalCost(rs.getDouble("FTotalCost"));
            valBean.setOtherCost(rs.getDouble("FFee"));
            valBean.setMarketPrice(rs.getDouble("FMarketPrice"));
            valBean.setMvalue(rs.getDouble("FMarketValue"));
            valBean.setYKVBal(rs.getDouble("FYKVBal"));
            valBean.setSyvBaseCuryBal(rs.getDouble("FSyvbaseCuryBal"));
            valBean.setFundAllotProportion(rs.getDouble("FFunDallotProportion"));
            valBean.setCuryCode(rs.getString("FCuryCode"));
            alSec.add(valBean);
         }
      }catch(Exception ex){
         throw new YssException("取股票和基金估值数据出错",ex);
      }finally{
         dbl.closeResultSetFinal(rs);
      }
   }
}
