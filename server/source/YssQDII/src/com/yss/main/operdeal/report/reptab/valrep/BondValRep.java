package com.yss.main.operdeal.report.reptab.valrep;

import com.yss.util.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.sql.*;

import com.yss.main.operdeal.report.reptab.valrep.pojo.ValRepBean;
import com.yss.main.operdeal.bond.BondInsCfgFormula;


public class BondValRep extends BaseValRep{
   public BondValRep() {
   }
   private ArrayList alBond=null;
   public ArrayList getValRepData() throws YssException {
      alBond =new ArrayList();
      getBondVal();
      return alBond;
   }
   private void getBondVal() throws YssException{
	   HashMap hmBoughtIntBF = getBoughtIntBFAssetType(getBoughtIntBFDetail(sPortCode, dBeginDate, dEndDate));
	   HashMap hmBoughtIntDetail = getBoughtIntDetail(sPortCode, dBeginDate, dEndDate);
       HashMap hmBoughtInt = getBoughtIntAssetType(hmBoughtIntDetail);

       String sqlStr="";
       ResultSet rs =null;
       try{
    	   sqlStr = "select FOrderCode, FPortCode, FNavDate, FSecurityCode, FSecurityName, FAmount, FCuryCode, FBaseCuryRate, FFaceRate, case when FAmount = 0 then to_date('9998-12-31','yyyy-MM-dd') else FInsStartDate end as FInsStartDate, case when FAmount = 0 then to_date('9998-12-31','yyyy-MM-dd') else FInsEndDate end as FInsEndDate, "
    		   + " FAvgBaseCuryRate, FAvgCost, FMarketPrice, FCost, FMarketValue, FLXBal, FBFLXBal, FGainLossValue, FSyvBaseCuryBal, FPortMarketValueRatio, FSubCatCode, FAssetType from ("
    		   + " select FCatCode || '##' || FSubCatCode || '##' || FAssetType || '##' || FCuryCode || '##' || FSecurityCode as FOrderCode, FPortCode, FNavDate, FSecurityCode, FSecurityName, FAmount, FCuryCode, FBaseCuryRate, FFaceRate, "
    		   + dbl.sqlDate(dBeginDate) + " as FInsStartDate, " + dbl.sqlDate(dEndDate) + " as FInsEndDate, FAvgBaseCuryRate, round(FAvgCost*FFactor, 5) as FAvgCost, round(FMarketPrice*FFactor, 5) as FMarketPrice, FCost, "
    		   + " round(FAmount*FMarketPrice, 2) as FMarketValue, round(FLXBal, 2) as FLXBal, round(FBFLXBal, 2) as FBFLXBal, round(FAmount*FMarketPrice, 2) - FCost as FGainLossValue, round(FCost*FBaseCuryRate - FPortCuryCost, 2) as FSyvBaseCuryBal, nvl(round(round(FAmount * FMarketPrice * FBaseCuryRate, 2)/FPortMarketValue, 4), 0) as FPortMarketValueRatio, FSubCatCode, FAssetType from ( "
    		   + " select nvl(a.FPortCode, f.FPortCode) as FPortCode, " + dbl.sqlDate(dEndDate) + " as FNavDate, nvl(a.FCuryCode, f.FCuryCode) as FCuryCode, nvl(a.FSecurityCode, f.FSecurityCode) as FSecurityCode, nvl(b.FSecurityName, ' ') as FSecurityName, "
    		   + " nvl(a.FAssetType, f.FAssetType) as FAssetType, nvl(b.FCatCode, f.FCatCode) as FCatCode, nvl(b.FSubCatCode, f.FSubCatCode) as FSubCatCode, nvl(a.FAmount, 0) as FAmount, nvl(FFaceValue, 0) as FFaceValue, nvl(a.FCost, 0) as FCost, nvl(a.FPortCuryCost, 0) as FPortCuryCost, "
    		   + " case when nvl(a.FCost, 0) = 0 then 0 else nvl(a.FPortCuryCost, 0)/nvl(a.FCost, 0) end as FAvgBaseCuryRate, nvl(FAvgCost, 0) as FAvgCost, nvl(b.FFactor, 1) as FFactor, nvl(c.FFaceRate, 0) as FFaceRate, nvl(d.FPrice, 0) as FMarketPrice, nvl(e.FLXBal, 0) as FLXBal, nvl(f.FBFLXBal, 0) as FBFLXBal, FBaseCuryRate from ( "
    		   + " select FPortCode, FStorageDate as FNavDate, FCuryCode, FSecurityCode, FAttrClsCode as FAssetType, sum(fstorageamount) as FAmount, sum(FVStorageCost) as FCost, sum(FVPortCuryCost) as FPortCuryCost, case when sum(FStorageAmount) = 0 then 0 else sum(FVStorageCost)/sum(FStorageAmount) end as FAvgCost from "
    		   + pub.yssGetTableName("tb_stock_security") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and fstorageamount<>0 group by FPortCode, FStorageDate, FCuryCode, FSecurityCode, FAttrClsCode having sum(FStorageAmount)<>0) a "
    		   + " join (select FCatCode, FSubCatCode, FSecurityCode, FSecurityName, FFactor from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1 and FCatCode = 'FI') b on a.FSecurityCode = b.FSecurityCode "
    		   + " join (select FSecurityCode, FFaceValue, FFaceRate from " + pub.yssGetTableName("tb_para_fixinterest") + " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode "
    		   + " left join (select price1.FSecurityCode, price1.FPrice from " + pub.yssGetTableName("tb_data_valmktprice") + " price1 join (select FSecurityCode, max(FValDate) FValDate from " + pub.yssGetTableName("tb_data_valmktprice")
    		   + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FSecurityCode) price2 on price1.FValDate = price2.FValDate and price1.FSecurityCode = price2.FSecurityCode where price1.FPortCode = " + dbl.sqlString(sPortCode) + ") d on a.FSecurityCode = d.FSecurityCode "
    		   + " left join (select FPortCode, FSecurityCode, FAttrClsCode as FAssetType, sum(FBal) as FLXBal from " + pub.yssGetTableName("tb_stock_secrecpay") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " "
    		   + " and FSubTsfTypeCode = '06FI' group by FPortCode, FSecurityCode, FAttrClsCode) e on a.FPortCode = e.FPortCode and a.FSecurityCode = e.FSecurityCode and a.FAssetType = e.FAssetType "
    		   + " left join (select f1.FPortCode, f1.FSecurityCode, f2.FCatCode, f2.FSubCatCode, f1.FCuryCode, f1.FAttrClsCode as FAssetType, sum(f1.FBal) as FBFLXBal from " + pub.yssGetTableName("tb_stock_secrecpay") + " f1 "
    		   + " join (select FSecurityCode, FCatCode, FSubCatCode from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) f2 on f1.FSecurityCode = f2.FSecurityCode where f1.FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '06FI' and FBal <> 0 and "
    		   + " (FStorageDate = " + dbl.sqlDate(dBeginDate) + " - 1 and substr(fyearmonth, 0, 4) = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') or " + dbl.sqlDate(dBeginDate) + " = to_date(to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '-01' || '-01', 'yyyy-MM-dd') and fyearmonth = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '00') "
    		   + " group by f1.FPortCode, f1.FCuryCode, f1.FSecurityCode, f1.FAttrClsCode, f2.FCatCode, f2.FSubCatCode) f on a.FPortCode = f.FPortCode and a.FSecurityCode = f.FSecurityCode and a.FAssetType = f.FAssetType "
    		   + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate")
    		   + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") g on a.FCuryCode = g.FCuryCode "
    		   + " union "
    		   + " select nvl(a.FPortCode, f.FPortCode) as FPortCode, " + dbl.sqlDate(dEndDate) + " as FNavDate, nvl(a.FCuryCode, f.FCuryCode) as FCuryCode, nvl(a.FSecurityCode, f.FSecurityCode) as FSecurityCode, nvl(b.FSecurityName, ' ') as FSecurityName, "
    		   + " nvl(a.FAssetType, f.FAssetType) as FAssetType, nvl(b.FCatCode, f.FCatCode) as FCatCode, nvl(b.FSubCatCode, f.FSubCatCode) as FSubCatCode, nvl(a.FAmount, 0) as FAmount, nvl(FFaceValue, 0) as FFaceValue, nvl(a.FCost, 0) as FCost, nvl(a.FPortCuryCost, 0) as FPortCuryCost, "
    		   + " case when nvl(a.FCost, 0) = 0 then 0 else nvl(a.FPortCuryCost, 0)/nvl(a.FCost, 0) end as FAvgBaseCuryRate, nvl(FAvgCost, 0) as FAvgCost, nvl(b.FFactor, 1) as FFactor, nvl(c.FFaceRate, 0) as FFaceRate, nvl(d.FPrice, 0) as FMarketPrice, nvl(e.FLXBal, 0) as FLXBal, nvl(f.FBFLXBal, 0) as FBFLXBal, FBaseCuryRate from ( "
    		   + " select FPortCode, FStorageDate as FNavDate, FCuryCode, FSecurityCode, FAttrClsCode as FAssetType, sum(fstorageamount) as FAmount, sum(FVStorageCost) as FCost, sum(FVPortCuryCost) as FPortCuryCost, case when sum(FStorageAmount) = 0 then 0 else sum(FVStorageCost)/sum(FStorageAmount) end as FAvgCost from "
    		   + pub.yssGetTableName("tb_stock_security") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and fstorageamount<>0 group by FPortCode, FStorageDate, FCuryCode, FSecurityCode, FAttrClsCode having sum(FStorageAmount)<>0) a "
    		   + " join (select FCatCode, FSubCatCode, FSecurityCode, FSecurityName, FFactor from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1 and FCatCode = 'FI') b on a.FSecurityCode = b.FSecurityCode "
    		   + " join (select FSecurityCode, FFaceValue, FFaceRate from " + pub.yssGetTableName("tb_para_fixinterest") + " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode "
    		   + " left join (select price1.FSecurityCode, price1.FPrice from " + pub.yssGetTableName("tb_data_valmktprice") + " price1 join (select FSecurityCode, max(FValDate) FValDate from " + pub.yssGetTableName("tb_data_valmktprice")
    		   + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FSecurityCode) price2 on price1.FValDate = price2.FValDate and price1.FSecurityCode = price2.FSecurityCode where price1.FPortCode = " + dbl.sqlString(sPortCode) + ") d on a.FSecurityCode = d.FSecurityCode "
    		   + " left join (select FPortCode, FSecurityCode, FAttrClsCode as FAssetType, sum(FBal) as FLXBal from " + pub.yssGetTableName("tb_stock_secrecpay") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " "
    		   + " and FSubTsfTypeCode = '06FI' group by FPortCode, FSecurityCode, FAttrClsCode) e on a.FPortCode = e.FPortCode and a.FSecurityCode = e.FSecurityCode and a.FAssetType = e.FAssetType "
    		   + " right join (select f1.FPortCode, f1.FSecurityCode, f2.FCatCode, f2.FSubCatCode, f1.FCuryCode, f1.FAttrClsCode as FAssetType, sum(f1.FBal) as FBFLXBal from " + pub.yssGetTableName("tb_stock_secrecpay") + " f1 "
    		   + " join (select FSecurityCode, FCatCode, FSubCatCode from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) f2 on f1.FSecurityCode = f2.FSecurityCode where f1.FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '06FI' and FBal <> 0 and "
    		   + " (FStorageDate = " + dbl.sqlDate(dBeginDate) + " - 1 and substr(fyearmonth, 0, 4) = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') or " + dbl.sqlDate(dBeginDate) + " = to_date(to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '-01' || '-01', 'yyyy-MM-dd') and fyearmonth = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '00') "
    		   + " group by f1.FPortCode, f1.FCuryCode, f1.FSecurityCode, f1.FAttrClsCode, f2.FCatCode, f2.FSubCatCode) f on a.FPortCode = f.FPortCode and a.FSecurityCode = f.FSecurityCode and a.FAssetType = f.FAssetType "
    		   + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate")
    		   + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") g on f.FCuryCode = g.FCuryCode) x "
            + " left join (select FPortCode as FPortCode2, FDate as FNavDate2, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on x.FPortCode = p.FPortCode2 and x.FNavDate = p.FNavDate2 "
    		   + " union "
    		   + " select FCatCode || '##' || FSubCatCode || '##' || FAssetType || '##' || FCuryCode || '##total' as FOrderCode, FPortCode, FNavDate, '' as FSecurityCode, '港幣等值金額：' as FSecurityName, 0 as FAmount, FCuryCode, FBaseCuryRate, 0 FFaceRate, "
    		   + dbl.sqlDate(dBeginDate) + " as FInsStartDate, " + dbl.sqlDate(dEndDate) + " as FInsEndDate, 0 as FAvgBaseCuryRate, 0 as FAvgCost, 0 as FMarketPrice, sum(FPortCuryCost) as FCost, sum(round(FAmount*FMarketPrice, 2))*FBaseCuryRate as FMarketValue, "
    		   + " sum(round(FLXBal, 2)) as FLXBal, sum(round(FBFLXBal, 2)) as FBFLXBal, sum(round(FAmount*FMarketPrice, 2) - FCost)*FBaseCuryRate as FGainLossValue, sum(round(FCost*FBaseCuryRate - FPortCuryCost, 2)) as FSyvBaseCuryBal, nvl(round(round(sum(round(FAmount * FMarketPrice, 2)) * FBaseCuryRate, 2)/FPortMarketValue, 4), 0) as FPortMarketValueRatio, FSubCatCode, FAssetType from ( "
    		   + " select nvl(a.FPortCode, f.FPortCode) as FPortCode, " + dbl.sqlDate(dEndDate) + " as FNavDate, nvl(a.FCuryCode, f.FCuryCode) as FCuryCode, nvl(a.FSecurityCode, f.FSecurityCode) as FSecurityCode, nvl(b.FSecurityName, ' ') as FSecurityName, "
    		   + " nvl(a.FAssetType, f.FAssetType) as FAssetType, nvl(b.FCatCode, f.FCatCode) as FCatCode, nvl(b.FSubCatCode, f.FSubCatCode) as FSubCatCode, nvl(a.FAmount, 0) as FAmount, nvl(a.FCost, 0) as FCost, nvl(a.FPortCuryCost, 0) as FPortCuryCost, "
    		   + " nvl(FAvgCost, 0) as FAvgCost, nvl(b.FFactor, 1) as FFactor, nvl(c.FFaceRate, 0) as FFaceRate, nvl(d.FPrice, 0) as FMarketPrice, nvl(e.FLXBal, 0) as FLXBal, nvl(f.FBFLXBal, 0) as FBFLXBal, FBaseCuryRate from ( "
    		   + " select FPortCode, FStorageDate as FNavDate, FCuryCode, FSecurityCode, FAttrClsCode as FAssetType, sum(fstorageamount) as FAmount, sum(FVStorageCost) as FCost, sum(FVPortCuryCost) as FPortCuryCost, case when sum(FStorageAmount) = 0 then 0 else sum(FVStorageCost)/sum(FStorageAmount) end as FAvgCost from "
    		   + pub.yssGetTableName("tb_stock_security") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and fstorageamount<>0 group by FPortCode, FStorageDate, FCuryCode, FSecurityCode, FAttrClsCode having sum(FStorageAmount)<>0) a "
    		   + " join (select FCatCode, FSubCatCode, FSecurityCode, FSecurityName, FFactor from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1 and FCatCode = 'FI') b on a.FSecurityCode = b.FSecurityCode "
    		   + " join (select FSecurityCode, FFaceRate from " + pub.yssGetTableName("tb_para_fixinterest") + " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode "
    		   + " left join (select price1.FSecurityCode, price1.FPrice from " + pub.yssGetTableName("tb_data_valmktprice") + " price1 join (select FSecurityCode, max(FValDate) FValDate from " + pub.yssGetTableName("tb_data_valmktprice")
    		   + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FSecurityCode) price2 on price1.FValDate = price2.FValDate and price1.FSecurityCode = price2.FSecurityCode where price1.FPortCode = " + dbl.sqlString(sPortCode) + ") d on a.FSecurityCode = d.FSecurityCode "
    		   + " left join (select FPortCode, FSecurityCode, FAttrClsCode as FAssetType, sum(FBal) as FLXBal from " + pub.yssGetTableName("tb_stock_secrecpay") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " "
    		   + " and FSubTsfTypeCode = '06FI' group by FPortCode, FSecurityCode, FAttrClsCode) e on a.FPortCode = e.FPortCode and a.FSecurityCode = e.FSecurityCode and a.FAssetType = e.FAssetType "
    		   + " left join (select f1.FPortCode, f1.FSecurityCode, f2.FCatCode, f2.FSubCatCode, f1.FCuryCode, f1.FAttrClsCode as FAssetType, sum(f1.FBal) as FBFLXBal from " + pub.yssGetTableName("tb_stock_secrecpay") + " f1 "
    		   + " join (select FSecurityCode, FCatCode, FSubCatCode from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) f2 on f1.FSecurityCode = f2.FSecurityCode where f1.FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '06FI' and FBal <> 0 and "
    		   + " (FStorageDate = " + dbl.sqlDate(dBeginDate) + " - 1 and substr(fyearmonth, 0, 4) = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') or " + dbl.sqlDate(dBeginDate) + " = to_date(to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '-01' || '-01', 'yyyy-MM-dd') and fyearmonth = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '00') "
    		   + " group by f1.FPortCode, f1.FCuryCode, f1.FSecurityCode, f1.FAttrClsCode, f2.FCatCode, f2.FSubCatCode) f on a.FPortCode = f.FPortCode and a.FSecurityCode = f.FSecurityCode and a.FAssetType = f.FAssetType "
    		   + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate")
    		   + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") g on a.FCuryCode = g.FCuryCode "
    		   + " union "
    		   + " select nvl(a.FPortCode, f.FPortCode) as FPortCode, " + dbl.sqlDate(dEndDate) + " as FNavDate, nvl(a.FCuryCode, f.FCuryCode) as FCuryCode, nvl(a.FSecurityCode, f.FSecurityCode) as FSecurityCode, nvl(b.FSecurityName, ' ') as FSecurityName, "
    		   + " nvl(a.FAssetType, f.FAssetType) as FAssetType, nvl(b.FCatCode, f.FCatCode) as FCatCode, nvl(b.FSubCatCode, f.FSubCatCode) as FSubCatCode, nvl(a.FAmount, 0) as FAmount, nvl(a.FCost, 0) as FCost, nvl(a.FPortCuryCost, 0) as FPortCuryCost, "
    		   + " nvl(FAvgCost, 0) as FAvgCost, nvl(b.FFactor, 1) as FFactor, nvl(c.FFaceRate, 0) as FFaceRate, nvl(d.FPrice, 0) as FMarketPrice, nvl(e.FLXBal, 0) as FLXBal, nvl(f.FBFLXBal, 0) as FBFLXBal, FBaseCuryRate from ( "
    		   + " select FPortCode, FStorageDate as FNavDate, FCuryCode, FSecurityCode, FAttrClsCode as FAssetType, sum(fstorageamount) as FAmount, sum(FVStorageCost) as FCost, sum(FVPortCuryCost) as FPortCuryCost, case when sum(FStorageAmount) = 0 then 0 else sum(FVStorageCost)/sum(FStorageAmount) end as FAvgCost from "
    		   + pub.yssGetTableName("tb_stock_security") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and fstorageamount<>0 group by FPortCode, FStorageDate, FCuryCode, FSecurityCode, FAttrClsCode having sum(FStorageAmount)<>0) a "
    		   + " join (select FCatCode, FSubCatCode, FSecurityCode, FSecurityName, FFactor from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1 and FCatCode = 'FI') b on a.FSecurityCode = b.FSecurityCode "
    		   + " join (select FSecurityCode, FFaceRate from " + pub.yssGetTableName("tb_para_fixinterest") + " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode "
    		   + " left join (select price1.FSecurityCode, price1.FPrice from " + pub.yssGetTableName("tb_data_valmktprice") + " price1 join (select FSecurityCode, max(FValDate) FValDate from " + pub.yssGetTableName("tb_data_valmktprice")
    		   + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FSecurityCode) price2 on price1.FValDate = price2.FValDate and price1.FSecurityCode = price2.FSecurityCode where price1.FPortCode = " + dbl.sqlString(sPortCode) + ") d on a.FSecurityCode = d.FSecurityCode "
    		   + " left join (select FPortCode, FSecurityCode, FAttrClsCode as FAssetType, sum(FBal) as FLXBal from " + pub.yssGetTableName("tb_stock_secrecpay") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " "
    		   + " and FSubTsfTypeCode = '06FI' group by FPortCode, FSecurityCode, FAttrClsCode) e on a.FPortCode = e.FPortCode and a.FSecurityCode = e.FSecurityCode and a.FAssetType = e.FAssetType "
    		   + " right join (select f1.FPortCode, f1.FSecurityCode, f2.FCatCode, f2.FSubCatCode, f1.FCuryCode, f1.FAttrClsCode as FAssetType, sum(f1.FBal) as FBFLXBal from " + pub.yssGetTableName("tb_stock_secrecpay") + " f1 "
    		   + " join (select FSecurityCode, FCatCode, FSubCatCode from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) f2 on f1.FSecurityCode = f2.FSecurityCode where f1.FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '06FI' and FBal <> 0 and "
    		   + " (FStorageDate = " + dbl.sqlDate(dBeginDate) + " - 1 and substr(fyearmonth, 0, 4) = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') or " + dbl.sqlDate(dBeginDate) + " = to_date(to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '-01' || '-01', 'yyyy-MM-dd') and fyearmonth = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '00') "
    		   + " group by f1.FPortCode, f1.FCuryCode, f1.FSecurityCode, f1.FAttrClsCode, f2.FCatCode, f2.FSubCatCode) f on a.FPortCode = f.FPortCode and a.FSecurityCode = f.FSecurityCode and a.FAssetType = f.FAssetType "
    		   + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate")
    		   + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") g on f.FCuryCode = g.FCuryCode "
    		   + " ) x left join (select FPortCode as FPortCode2, FDate as FNavDate2, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on x.FPortCode = p.FPortCode2 and x.FNavDate = p.FNavDate2 group by FPortCode, FNavDate, FCuryCode, FCatCode, FSubCatCode, FAssetType, FBaseCuryRate, FPortMarketValue "
    		   + " ) order by FPortCode, FNavDate, FCuryCode, FSubCatCode, FAssetType, FSecurityName, FOrderCode ";

         rs =dbl.openResultSet(sqlStr);
         //合计买入利息
         double TotalBoughtInt = 0;
         double PortTotalBoughtInt = 0;
         double TotalBoughtIntBF = 0;
         String LastCuryCode = "";//上条记录的币种
         String LastSubCatCode = "";
         String LastAssetType = "";
         while(rs.next()){
        	 valBean =new ValRepBean();
        	 String OrderCode = rs.getString("FOrderCode");
        	 String strSecCode = rs.getString("FSecurityCode");
        	 String CuryCode = rs.getString("FCuryCode");
        	 String AssetType = rs.getString("FAssetType");
        	 String SubCatCode = rs.getString("FSubCatCode");

        	 if(strSecCode == null && LastCuryCode.equals(CuryCode) && LastAssetType.equals(AssetType) && LastSubCatCode.equals(SubCatCode)){
        		 valBean.setBoughtInt(PortTotalBoughtInt);
 				 valBean.setLXVBal((rs.getDouble("FLXBal") - TotalBoughtInt)*rs.getDouble("FBaseCuryRate"));
 				 valBean.setBFlxBal((rs.getDouble("FBFLXBal") - TotalBoughtIntBF)*rs.getDouble("FBaseCuryRate"));
        		 TotalBoughtInt = 0;
        		 PortTotalBoughtInt = 0;
        		 TotalBoughtIntBF = 0;
        	 }
        	 else{
        		 double BoughtInt = 0;
        		 double PortBoughtInt = 0;
        		 double BoughtIntBF = 0;

        		 String sKey = strSecCode + "#" + OrderCode.split("##")[2] + "#END";
        		 if(hmBoughtInt != null && !hmBoughtInt.isEmpty()){
        			 String sValue = (String)hmBoughtInt.get(sKey);
            		 if(sValue != null && !"".equals(sValue)){
            			 BoughtInt = Double.parseDouble(sValue.split("#")[1]);
            			 PortBoughtInt = Double.parseDouble(sValue.split("#")[2]);
            		 }
        		 }
        		 if(hmBoughtIntBF != null && !hmBoughtIntBF.isEmpty()){
        			 String sValue = (String)hmBoughtIntBF.get(sKey);
            		 if(sValue != null && !"".equals(sValue)){
            			 BoughtIntBF = Double.parseDouble(sValue.split("#")[1]);
            		 }
        		 }
				valBean.setBoughtInt(BoughtInt);
				valBean.setLXVBal(rs.getDouble("FLXBal") - BoughtInt);
				valBean.setBFlxBal(rs.getDouble("FBFLXBal") - BoughtIntBF);
				TotalBoughtIntBF += BoughtIntBF;
				PortTotalBoughtInt += PortBoughtInt;
				TotalBoughtInt += BoughtInt;
        	}
        	//保留币种及资产类别信息，方便按币种及资产类别分组统计利息收入合计数
        	LastCuryCode = CuryCode;
        	LastAssetType = AssetType;
        	LastSubCatCode = SubCatCode;

            valBean.setSecurityCode(rs.getString("FSecurityCode"));
            valBean.setSecurityName(rs.getString("FSecurityName"));
            valBean.setStorageAmount(rs.getDouble("FAmount"));  //库存数量 票面数/股数
            valBean.setCuryCode(rs.getString("FCuryCode"));
            valBean.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
            valBean.setPortCuryRate(rs.getDouble("FAvgBaseCuryRate")); //平均基础汇率
            valBean.setFactRate(rs.getDouble("FFaceRate"));//票面利率
            valBean.setInsStartDate(rs.getDate("FInsStartDate")); //期间的起始日期
            valBean.setInsEndDate(rs.getDate("FInsEndDate")); //期间的结束日期
            valBean.setAvgCost(rs.getDouble("FAvgCost")); //平均成本
            valBean.setMarketPrice(rs.getDouble("FMarketPrice")); //市场价格
            valBean.setTotalCost(rs.getDouble("FCost")); //成本价
            //valBean.setBoughtInt(rs.getDouble("FBoughtInt")); //买入利息
            valBean.setMvalue(rs.getDouble("FMarketValue")); //市值
            //valBean.setLXVBal(rs.getDouble("FAccrInt")); //应收利息
            //valBean.setBFlxBal(rs.getDouble("FAccrIntBF")); //应收利息，上期结余
            valBean.setYKVBal(rs.getDouble("FGainLossValue")); //盈亏差价
            valBean.setSyvBaseCuryBal(rs.getDouble("FSyvBaseCuryBal")); //汇兑损益
            valBean.setVstorageCost(0);//合并邵宏伟修改报表代码 xuqiji 20100608
            valBean.setFundAllotProportion(rs.getDouble("FPortMarketValueRatio"));//合并邵宏伟修改报表代码 xuqiji 20100608
            valBean.setOrder(rs.getString("FOrderCode"));
            alBond.add(valBean);
         }
      }catch(YssException ex){
         throw new YssException("获取债券临时表数据出错！");
      } catch (SQLException e) {
		// TODO Auto-generated catch block
    	  throw new YssException("获取债券临时表数据出错！");
	}finally{
         dbl.closeResultSetFinal(rs);
      }

      //保存HashMap数据到债券买入利息库存中
      Connection conn = null;
      PreparedStatement stm = null;
      boolean bTrans = false;

      //库存日期设置
      String sYearMonth = "";
      Date dStorageDate = null;
      Calendar cal = Calendar.getInstance();
	  cal.setTime(dEndDate);
	  int iYear = cal.get(cal.YEAR);
	  int iMonth = cal.get(cal.MONTH) + 2;
	  if(iMonth == 13){
		  iYear += 1;
		  iMonth = 0;
		  sYearMonth = String.valueOf(iYear) + String.valueOf(iMonth+100).substring(1);
		  iMonth = 1;
	  }else{
		  sYearMonth = String.valueOf(iYear) + String.valueOf(iMonth+100).substring(1);
	  }
	  cal.set(iYear, iMonth - 1, 1);
	  dStorageDate = cal.getTime();

      try{
    	  if(hmBoughtIntDetail == null || hmBoughtIntDetail.isEmpty()){
    		  return;
    	  }

    	  sqlStr = "delete from " + pub.yssGetTableName("Tb_Stock_PurBond") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FYearMonth = " + dbl.sqlString(sYearMonth);
    	  dbl.executeSql(sqlStr);

    	  sqlStr = "insert into " + pub.yssGetTableName("Tb_Stock_PurBond") + " (FSecurityCode,FYearMonth,FStorageDate,FPortCode,FAttrClsCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageAmount,"
    	  + "FStorageCost,FMStorageCost,FVStorageCost,FBaseCuryRate,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryRate,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FStorageInd,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) "
    	  + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    	  conn = dbl.loadConnection();
          stm = dbl.openPreparedStatement(sqlStr);
          conn.setAutoCommit(bTrans);
          bTrans = true;

          Iterator iterator = hmBoughtIntDetail.keySet().iterator();
          while(iterator.hasNext()){
        	  String sKey = (String) iterator.next();
        	  String sValue = (String) hmBoughtIntDetail.get(sKey);
        	  double dAmount = Double.parseDouble(sValue.split("#")[0]);
        	  double dBal = Double.parseDouble(sValue.split("#")[1]);
        	  double dPortBal = Double.parseDouble(sValue.split("#")[2]);

        	  stm.setString(1, sKey.split("#")[0]);
        	  stm.setString(2, sYearMonth);
        	  stm.setDate(3, YssFun.toSqlDate(dStorageDate));
        	  stm.setString(4, sPortCode);
        	  stm.setString(5, sKey.split("#")[1]);
        	  stm.setString(6, sKey.split("#")[2]);
        	  stm.setString(7, sKey.split("#")[3]);
        	  stm.setString(8, sKey.split("#")[4]);
        	  stm.setDouble(9, dAmount);
        	  stm.setDouble(10, dBal);
        	  stm.setDouble(11, dBal);
        	  stm.setDouble(12, dBal);
        	  stm.setDouble(13, 1);
        	  stm.setDouble(14, dPortBal);
        	  stm.setDouble(15, dPortBal);
        	  stm.setDouble(16, dPortBal);
        	  if(dBal == 0){
        		  stm.setDouble(17, 0);
        	  }else{
        		  stm.setDouble(17, dPortBal/dBal);
        	  }
        	  stm.setDouble(18, dPortBal);
        	  stm.setDouble(19, dPortBal);
        	  stm.setDouble(20, dPortBal);
        	  stm.setDouble(21, 2);
        	  stm.setDouble(22, 1);
        	  stm.setString(23, pub.getUserCode());
        	  stm.setString(24, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        	  stm.setString(25, pub.getUserCode());
        	  stm.setString(26, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        	  stm.addBatch();
          }
          stm.executeBatch();
          conn.commit();
          conn.setAutoCommit(bTrans);
          bTrans = false;
      }catch(Exception ex){
         throw new YssException("保存债券买入利息库存出错",ex);
      }finally{
    	  dbl.endTransFinal(conn, bTrans);
      }
   }
   //用于更新临时表中债券的利息，债券计息期间等。
   public void afterValRepData() throws YssException{
      ResultSet rs =null;
      String sql="",upSql="";
      Connection conn=null;
      boolean bTrans =false;
      java.util.Date dInsStartDate=null,dInsendDate=null;
      BondInsCfgFormula formula=null;
      try{
         conn =dbl.loadConnection();
         conn.setAutoCommit(bTrans);
         bTrans = true;
//-----------李高辉 20090918 方便统计上期有应收利息余额，本期没有库存的情况 begin
         //sql="select FSecurityCode from tb_temp_portfoioVal_"+pub.getUserCode()+" where FOrder like 'FI##FI%' and FOrder not like 'FI##FI01##total%'";
         sql="select FSecurityCode from tb_data_PortfolioVal where FOrder like 'FI##FI%' and FOrder not like 'FI##FI%total' and FInsStartDate <> " + dbl.sqlDate("9998-12-31") + " and FValDate = " + dbl.sqlDate(dEndDate);
//-----------李高辉 20090918 end
         rs =dbl.openResultSet(sql);
         while(rs.next()){
            formula =new BondInsCfgFormula();
            formula.setYssPub(pub);
            formula.getFixInterestInfo(rs.getString("FSecurityCode"),dEndDate,"Day");//此方法需要在BondInsCfgFormula将访问权限改为public
            dInsStartDate=(java.util.Date)formula.getKeywordValue("ThisInsStartDate"); //本期间的起始日
//            dInsendDate =(java.util.Date)formula.getKeywordValue("ThisInsEndDate");   //本期间的结束日
            //修改中保报表的BUG问题 这里不再重新计算期间日期 by leeyu 2008-12-16
            dInsendDate =(java.util.Date)formula.getKeywordValue("InsEndDate");//本期计息截止日
            upSql="update tb_data_PortfolioVal set FInsStartDate="+dbl.sqlDate(dInsStartDate)+", FInsendDate="+dbl.sqlDate(dInsendDate)+
                  " where FSecurityCode ="+dbl.sqlString(rs.getString("FSecurityCode"))+" and FOrder like 'FI##FI%' and FValDate = " + dbl.sqlDate(dEndDate);
            dbl.executeSql(upSql);
         }
         conn.commit();
         conn.setAutoCommit(bTrans);
         bTrans =false;
      }catch(Exception ex){
         throw new YssException("更新债券临时表信息出错！");
      }finally{
         dbl.closeResultSetFinal(rs);
         dbl.endTransFinal(conn,bTrans);
      }
   }

   //以下为债券买入利息算法的实现  - 李高辉  - 2009-11-29
   //债券买入利息 - 上期结余
   //获取所有债券买入利息上期结余  - 区分证券
   public HashMap getBoughtIntBF(HashMap hmBoughtIntBFDetail){
	   String SecurityCode = null;
	   HashMap hmBoughtIntBF = new HashMap();//不区分资产类别的HashMap
	   if(hmBoughtIntBFDetail == null || hmBoughtIntBFDetail.isEmpty()){
		   return null;
	   }
	   Iterator iterator = hmBoughtIntBFDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 SecurityCode = sKey.split("#")[0];
  		 String sTmp1 = (String) hmBoughtIntBFDetail.get(sKey);
  		 String sTmp2 = (String) hmBoughtIntBF.get(SecurityCode);
  		double dAmount = 0;
  		double dBal = 0;
  		double dPortBal = 0;
  		if(sTmp2 != null && !"".equals(sTmp2)){
  			dAmount = Double.parseDouble(sTmp2.split("#")[0]);
  			dBal = Double.parseDouble(sTmp2.split("#")[1]);
  			dPortBal = Double.parseDouble(sTmp2.split("#")[2]);
  		}
  		dAmount += Double.parseDouble(sTmp1.split("#")[0]);
  		dBal += Double.parseDouble(sTmp1.split("#")[1]);
  		dPortBal = Double.parseDouble(sTmp1.split("#")[2]);
  		hmBoughtIntBF.put(SecurityCode, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
	   }
	   return hmBoughtIntBF;
   }

   //获取所有债券买入利息上期结余  - 区分资产类别
   public HashMap getBoughtIntBFAssetType(HashMap hmBoughtIntBFDetail) {
		String SecurityCode = null;
		HashMap hmBoughtIntBF = new HashMap();// 不区分资产类别的HashMap
		if (hmBoughtIntBFDetail == null || hmBoughtIntBFDetail.isEmpty()) {
			return null;
		}
		Iterator iterator = hmBoughtIntBFDetail.keySet().iterator();
		while (iterator.hasNext()) {
			String sKey = (String) iterator.next();
			String sKeyAssetType = sKey.split("#")[0] + "#" + sKey.split("#")[1] + "#END";
			String sTmp1 = (String) hmBoughtIntBFDetail.get(sKey);
			String sTmp2 = (String) hmBoughtIntBF.get(sKeyAssetType);
			double dAmount = 0;
			double dBal = 0;
			double dPortBal = 0;
			if (sTmp2 != null && !"".equals(sTmp2)) {
				dAmount = Double.parseDouble(sTmp2.split("#")[0]);
				dBal = Double.parseDouble(sTmp2.split("#")[1]);
				dPortBal = Double.parseDouble(sTmp2.split("#")[2]);
			}
			dAmount += Double.parseDouble(sTmp1.split("#")[0]);
			dBal += Double.parseDouble(sTmp1.split("#")[1]);
			dPortBal = Double.parseDouble(sTmp1.split("#")[2]);
			hmBoughtIntBF.put(sKeyAssetType, String.valueOf(dAmount) + "#"
					+ String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
		}
		return hmBoughtIntBF;
	}

   //获取所有债券买入利息上期结余  - 区分所属分类和分析代码
   public HashMap getBoughtIntBFDetail(String sPortCode,Date dBeginDate,Date dEndDate) throws YssException{
	   return getBoughtIntBFDetail(sPortCode,dBeginDate,dEndDate,null);
   }
   //获取指定债券买入利息上期结余  - 区分所属分类和分析代码
   public HashMap getBoughtIntBFDetail(String sPortCode,Date dBeginDate,Date dEndDate,String sSecurityCode) throws YssException{
	   String sqlStr = "";
	   ResultSet rs = null;
	   HashMap hmBoughtInt = new HashMap();
	   //取买入利息库存
	   sqlStr = "select s.*,t.FStorageAmount from (select FPortCode, FSecurityCode, FAttrClsCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FYearMonth, FStorageDate, sum(FStorageAmount) as FAmount, sum(FVStorageCost) as FBal, sum(FVPortCuryCost) as FPortBal from " + pub.yssGetTableName("Tb_Stock_PurBond") + " where fportcode = " + dbl.sqlString(sPortCode)
			+ " and (to_char("+dbl.sqlDate(dBeginDate)+",'MM')='01' and fyearmonth = to_char("+dbl.sqlDate(dBeginDate)+",'yyyy')||'00' or to_char("+dbl.sqlDate(dBeginDate)+",'MM') <> '01' and fstoragedate = " + dbl.sqlDate(dBeginDate) + ")";
	   if(sSecurityCode != null){
		   sqlStr += " and FSecurityCode = " + dbl.sqlString(sSecurityCode) ;
	   }
	   sqlStr += " group by FPortCode, FSecurityCode, FAttrClsCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FYearMonth, FStorageDate) s, " 
		   + pub.yssGetTableName("Tb_Stock_Security") + " t where t.fsecuritycode = s.fsecuritycode and t.fstoragedate = s.fstoragedate and t.fyearmonth = s.fyearmonth and t.fportcode = s.fportcode and t.fanalysiscode1 = s.fanalysiscode1 and t.fanalysiscode2 = s.fanalysiscode2 and t.fanalysiscode3 = s.fanalysiscode3 and t.fattrclscode = s.fattrclscode";
	   try {
		   rs = dbl.openResultSet(sqlStr);
		   while (rs.next()) {
			   String sKey = rs.getString("FSecurityCode") + "#" + rs.getString("FAttrClsCode") + "#" + rs.getString("FAnalysisCode1") + "#" + rs.getString("FAnalysisCode2") + "#" + rs.getString("FAnalysisCode3") + "#END";
			   hmBoughtInt.put(sKey, rs.getDouble("FStorageAmount") + "#" + rs.getDouble("FBal") + "#" + rs.getDouble("FPortBal"));
		   }
	   } catch (Exception ex) {
		   throw new YssException("获取债券买入利息库存数据出错", ex);
	   } finally {
		   dbl.closeResultSetFinal(rs);
	   }
	   return hmBoughtInt;
   }

   //获取债券买入利息  - 只区分证券
   //当前组合期间所有债券买入利息  - 只区分证券
   public HashMap getBoughtInt(HashMap hmBoughtIntDetail){
	   String SecurityCode = null;
	   HashMap hmBoughtInt = new HashMap();//不区分资产类别的HashMap
	   if(hmBoughtIntDetail == null || hmBoughtIntDetail.isEmpty()){
		   return null;
	   }
	   Iterator iterator = hmBoughtIntDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 SecurityCode = sKey.split("#")[0];
  		 String sTmp1 = (String) hmBoughtIntDetail.get(sKey);
  		 String sTmp2 = (String) hmBoughtInt.get(SecurityCode);
  		double dAmount = 0;
  		double dBal = 0;
  		double dPortBal = 0;
  		if(sTmp2 != null && !"".equals(sTmp2)){
  			dAmount = Double.parseDouble(sTmp2.split("#")[0]);
  			dBal = Double.parseDouble(sTmp2.split("#")[1]);
  			dPortBal = Double.parseDouble(sTmp2.split("#")[2]);
  		}
  		dAmount += Double.parseDouble(sTmp1.split("#")[0]);
  		dBal += Double.parseDouble(sTmp1.split("#")[1]);
  		dPortBal = Double.parseDouble(sTmp1.split("#")[2]);
  		hmBoughtInt.put(SecurityCode, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
	   }
	   return hmBoughtInt;
   }
   //当前组合期间所有债券买入利息 - 只区分证券
   public HashMap getBoughtInt(String sPortCode,Date dBeginDate,Date dEndDate) throws YssException{
	   return getBoughtInt(sPortCode,dBeginDate,dEndDate,null);
   }
   //当前组合期间指定债券买入利息   - 只区分证券
   public HashMap getBoughtInt(String sPortCode,Date dBeginDate,Date dEndDate,String sSecurityCode) throws YssException{
	   String SecurityCode = sSecurityCode;
	   HashMap hmBoughtIntDetail = getBoughtIntDetail(sPortCode,dBeginDate,dEndDate,sSecurityCode);//区分资产类别的HashMap
	   HashMap hmBoughtInt = new HashMap();//不区分资产类别的HashMap
	   Iterator iterator = hmBoughtIntDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 if(sSecurityCode == null){
  			SecurityCode = sKey.split("#")[0];
  		 }
  		 if(sKey.split("#")[0].equals(SecurityCode)){
  			 String sTmp1 = (String) hmBoughtIntDetail.get(sKey);
  			 String sTmp2 = (String) hmBoughtInt.get(SecurityCode);
  			 double dAmount = 0;
  			 double dBal = 0;
  			 double dPortBal = 0;
  			 if(sTmp2 != null && !"".equals(sTmp2)){
  				dAmount = Double.parseDouble(sTmp2.split("#")[0]);
  				dBal = Double.parseDouble(sTmp2.split("#")[1]);
  				dPortBal = Double.parseDouble(sTmp2.split("#")[2]);
  			 }
  			 dAmount += Double.parseDouble(sTmp1.split("#")[0]);
  			 dBal += Double.parseDouble(sTmp1.split("#")[1]);
  			 dPortBal = Double.parseDouble(sTmp1.split("#")[2]);
  			 hmBoughtInt.put(SecurityCode, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
  		 }
	   }
	   return hmBoughtInt;
   }

   //获取债券买入利息  - 区分所属分类（子资产类别）
   //当前组合期间所有债券买入利息  - 区分所属分类（子资产类别）
   public HashMap getBoughtIntAssetType(HashMap hmBoughtIntDetail){
	   HashMap hmBoughtInt = new HashMap();//不区分资产类别的HashMap
	   if(hmBoughtIntDetail == null || hmBoughtIntDetail.isEmpty()){
		   return null;
	   }
	   Iterator iterator = hmBoughtIntDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 String sKeyAssetType = sKey.split("#")[0] + "#" + sKey.split("#")[1] + "#END";
  		 String sTmp1 = (String) hmBoughtIntDetail.get(sKey);
  		 String sTmp2 = (String) hmBoughtInt.get(sKeyAssetType);
  		double dAmount = 0;
  		double dBal = 0;
  		double dPortBal = 0;
  		if(sTmp2 != null && !"".equals(sTmp2)){
  			dAmount = Double.parseDouble(sTmp2.split("#")[0]);
  			dBal = Double.parseDouble(sTmp2.split("#")[1]);
  			dPortBal = Double.parseDouble(sTmp2.split("#")[2]);
  		}
  		dAmount += Double.parseDouble(sTmp1.split("#")[0]);
  		dBal += Double.parseDouble(sTmp1.split("#")[1]);
  		dPortBal += Double.parseDouble(sTmp1.split("#")[2]);
  		hmBoughtInt.put(sKeyAssetType, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
	   }
	   return hmBoughtInt;
   }
   //当前组合期间所有债券买入利息  - 区分所属分类（子资产类别）
   public HashMap getBoughtIntAssetType(String sPortCode,Date dBeginDate,Date dEndDate) throws YssException{
	   return getBoughtIntAssetType(sPortCode,dBeginDate,dEndDate,null);
   }
   //当前组合期间指定债券买入利息  - 区分所属分类（子资产类别）
   public HashMap getBoughtIntAssetType(String sPortCode,Date dBeginDate,Date dEndDate,String sSecurityCode) throws YssException{
	   String SecurityCode = sSecurityCode;
	   String sKeyTmp = "";
	   HashMap hmBoughtIntDetail = getBoughtIntDetail(sPortCode,dBeginDate,dEndDate,sSecurityCode);//区分资产类别的HashMap
	   HashMap hmBoughtInt = new HashMap();//不区分资产类别的HashMap
	   Iterator iterator = hmBoughtIntDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 if(sSecurityCode == null){
  			SecurityCode = sKey.split("#")[0];
  		 }
  		 if(sKey.split("#")[0].equals(SecurityCode)){
  			 String sTmp1 = (String) hmBoughtIntDetail.get(sKey);
  			 String sTmp2 = (String) hmBoughtInt.get(SecurityCode);
  			 double dAmount = 0;
  			 double dBal = 0;
  			 double dPortBal = 0;
  			 if(sTmp2 != null && !"".equals(sTmp2)){
  				dAmount = Double.parseDouble(sTmp2.split("#")[0]);
  				dBal = Double.parseDouble(sTmp2.split("#")[1]);
  				dPortBal = Double.parseDouble(sTmp2.split("#")[2]);
  			 }
  			 dAmount += Double.parseDouble(sTmp1.split("#")[0]);
  			 dBal += Double.parseDouble(sTmp1.split("#")[1]);
  			 dPortBal = Double.parseDouble(sTmp1.split("#")[2]);
  			 hmBoughtInt.put(sSecurityCode, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
  		 }
	   }
	   return hmBoughtInt;
   }

   //获取债券买入利息  - 区分所属分类和所有分析代码
   //当前组合期间所有债券买入利息  - 区分所属分类和所有分析代码
   public HashMap getBoughtIntDetail(String sPortCode,Date dBeginDate,Date dEndDate) throws YssException{
	   return getBoughtIntDetail(sPortCode,dBeginDate,dEndDate,null);
   }
   //当前组合期间指定债券买入利息  - 区分所属分类和所有分析代码
   public HashMap getBoughtIntDetail(String sPortCode,Date dBeginDate,Date dEndDate,String sSecurityCode) throws YssException{
	   String sqlStr = "";
	   ResultSet rs = null;
	   HashMap hmBoughtInt =  getBoughtIntBFDetail(sPortCode,dBeginDate,dEndDate,sSecurityCode);//取期初买入利息余额

	   HashMap hmPaidInt = new HashMap();
	   sqlStr = "select FSecurityCode, max(FTransDate) as FLastINTDate from " + pub.yssGetTableName("tb_data_secrecpay") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '02FI' and FTransDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and FCheckState = 1 group by FSecurityCode";
	   try {
		   rs = dbl.openResultSet(sqlStr);
		   while (rs.next()) {
			   hmPaidInt.put(rs.getString("FSecurityCode"), rs.getDate("FLastINTDate"));
		   }
	   } catch (Exception ex) {
		   throw new YssException("获取债券交易数据出错", ex);
	   } finally {
		   dbl.closeResultSetFinal(rs);
	   }

	   //有派息调整买入利息为零
	   Iterator iterator = hmBoughtInt.keySet().iterator();
	   while(iterator.hasNext()){
		   String sKey = (String) iterator.next();
		   if(hmPaidInt.containsKey(sKey.split("#")[0])){
			   String sTmp = (String) hmBoughtInt.get(sKey);
			   hmBoughtInt.put(sKey, sTmp.split("#")[0] + "#0#0");
		   }
	   }

	   //取债券买入、转货转出、转货转入、债券卖出情况，计算买入利息的冲减金额
	   sqlStr = "select * from ( "
		   + " select 1 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FInvMgrCode as FAnalysisCode1,a.FBrokerCode as FAnalysisCode2,' ' as FAnalysisCode3,a.FBargainDate,a.FTradeAmount * c.FAmountInd as FAmount,a.FAccruedInterest * c.FAmountInd as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,'' as FNum from " + pub.yssGetTableName("tb_data_subtrade")  
		   + " a join " + pub.yssGetTableName("tb_para_security") + " b on a.FSecurityCode = b.FSecurityCode join tb_base_tradetype c on a.FTradeTypeCode = c.FTradeTypeCode where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FCheckState = 1 and b.FCatCode = 'FI' and c.FTradeTypeCode = '01' and a.FBargainDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate)
		   + " union "
		   + " select 2 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FAnalysisCode1,a.FAnalysisCode2,' ' as FAnalysisCode3,a.FExchangeDate,abs(a.FAmount)*a.FInOutType as FAmount,abs(c.FMoney)*c.FInOut as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,a.FNum from " + pub.yssGetTableName("tb_data_integrated") + " a join (select * from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = 0) b on a.fnum = b.fnum "
		   //转货转入时取数出现错误，同一业务编号下有多个组合数据，对tb_001_data_secrecpay表添加组合限定 renzhi 20101103
		   + " join " + pub.yssGetTableName("tb_data_secrecpay") + " c on c.FInOut = -1 and c.fportcode = " + dbl.sqlString(sPortCode) + " and b.frelanum = c.fnum where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = -1 "
		   //+ " join " + pub.yssGetTableName("tb_data_secrecpay") + " c on c.FInOut = -1 and b.frelanum = c.fnum where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = -1 "
		   + " union "
		   + " select 3 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FAnalysisCode1,a.FAnalysisCode2,' ' as FAnalysisCode3,a.FExchangeDate,abs(a.FAmount)*a.FInOutType as FAmount,0 as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,a.FNum from " + pub.yssGetTableName("tb_data_integrated")
		   + " a where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = -1 "
		   + " and a.fnum not in (select fnum from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = 0) "
		   + " union "
		   + " select 4 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FAnalysisCode1,a.FAnalysisCode2,' ' as FAnalysisCode3,a.FExchangeDate,abs(a.FAmount)*a.FInOutType as FAmount,abs(c.FMoney)*c.FInOut as FBoughtInt,a.FBaseCuryRate,case when d.FSecurityCode <> a.FSecurityCode then d.FSecurityCode else '' end as FSecurityCode2,a.FNum from " + pub.yssGetTableName("tb_data_integrated") + " a join (select * from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = 0) b on a.fnum = b.fnum "
		   //转货转入时取数出现错误，同一业务编号下有多个组合数据，对tb_001_data_secrecpay表添加组合限定 renzhi 20101103
		   + " join " + pub.yssGetTableName("tb_data_secrecpay") + " c on c.FInOut = 1 and c.fportcode = " + dbl.sqlString(sPortCode) + " and b.frelanum = c.fnum join (select * from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = -1) d on a.fnum = d.fnum where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = 1 "
		   //+ " join " + pub.yssGetTableName("tb_data_secrecpay") + " c on c.FInOut = 1 and b.frelanum = c.fnum join (select * from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = -1) d on a.fnum = d.fnum where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = 1 "
		   + " union "
		   + " select 5 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FAnalysisCode1,a.FAnalysisCode2,' ' as FAnalysisCode3,a.FExchangeDate,abs(a.FAmount)*a.FInOutType as FAmount,0 as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,a.FNum from " + pub.yssGetTableName("tb_data_integrated")
		   + " a where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = 1 "
		   + " and a.fnum not in (select fnum from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = 0) "
		   + " union "
		   + " select 6 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FInvMgrCode as FAnalysisCode1,a.FBrokerCode as FAnalysisCode2,' ' as FAnalysisCode3,a.FBargainDate,a.FTradeAmount * c.FAmountInd as FAmount,a.FAccruedInterest * c.FAmountInd as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,'' as FNum from " + pub.yssGetTableName("tb_data_subtrade")
		   + " a join " + pub.yssGetTableName("tb_para_security") + " b on a.FSecurityCode = b.FSecurityCode join tb_base_tradetype c on a.FTradeTypeCode = c.FTradeTypeCode where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FCheckState = 1  and b.FCatCode = 'FI' and c.FTradeTypeCode = '02'  and a.FBargainDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " ) ";
	   if(sSecurityCode != null){
		   sqlStr += " where FSecurityCode = " + dbl.sqlString(sSecurityCode);
	   }
	   sqlStr += " order by FSecurityCode, FBargainDate, OrderNum";
	   try {
		   rs = dbl.openResultSet(sqlStr);
		   while (rs.next()) {
			   String sKey = rs.getString("FSecurityCode") + "#" + rs.getString("FAttrClsCode") + "#" + rs.getString("FAnalysisCode1") + "#" + rs.getString("FAnalysisCode2") + "#" + rs.getString("FAnalysisCode3") + "#END";
			   String sTmp = (String)hmBoughtInt.get(sKey);
			   double dAmount = 0;
			   double dBal = 0;
			   double dPortBal = 0;
			   if(sTmp != null){
				   dAmount = Double.parseDouble(sTmp.split("#")[0]);
				   dBal = Double.parseDouble(sTmp.split("#")[1]);
				   dPortBal = Double.parseDouble(sTmp.split("#")[2]);
			   }
			   Date paidDate = (Date)hmPaidInt.get(rs.getString("FSecurityCode"));
			   if(paidDate != null && (rs.getDate("FBargainDate").before(paidDate)
					   || rs.getDate("FBargainDate").equals(paidDate))){
				   dAmount += rs.getDouble("FAmount");
				   hmBoughtInt.put(sKey, String.valueOf(dAmount) + "#0#0");
			   } else {
				   if(rs.getDouble("FBoughtInt") == 0){
					   dAmount += rs.getDouble("FAmount");
					   hmBoughtInt.put(sKey, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
					   continue;
				   } else if(rs.getDouble("FBoughtInt") > 0){//流入时累加买入利息
					   if(rs.getString("FSecurityCode2") != null){
						   HashMap hmBoughtIntDetail = getBoughtIntDetail(sPortCode,dBeginDate,rs.getDate("FBargainDate"),rs.getString("FSecurityCode2"),rs.getString("FNum"));
						   Iterator iValue = hmBoughtIntDetail.values().iterator();
						   while(iValue.hasNext()){
							   String sValue = (String) iValue.next();
							   dBal += Double.parseDouble(sValue.split("#")[1]);
							   dPortBal += Double.parseDouble(sValue.split("#")[2]);
						   }
					   } else {
//						   dBal += (int)Math.round(rs.getDouble("FBoughtInt")*100)/100;
//						   dPortBal += (int)Math.round(rs.getDouble("FBoughtInt")*rs.getDouble("FBaseCuryRate")*100)/100;
						   dBal += YssFun.roundIt(rs.getDouble("FBoughtInt"),2);
						   dPortBal += YssFun.roundIt(rs.getDouble("FBoughtInt")*rs.getDouble("FBaseCuryRate"),2);

					   }
				   } else{//流出时冲减买入利息
					   if(dAmount != 0){
						   dBal += YssFun.roundIt(dBal*rs.getDouble("FAmount")/dAmount,2);
						   dPortBal += YssFun.roundIt(dPortBal*rs.getDouble("FAmount")/dAmount,2);
					   }else{
						   continue;
					   }
				   }
				   dAmount += rs.getDouble("FAmount");
				   hmBoughtInt.put(sKey, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
			   }
		   }
	   } catch (Exception ex) {
		   throw new YssException("获取债券交易数据出错", ex);
	   } finally {
		   dbl.closeResultSetFinal(rs);
	   }
	   return hmBoughtInt;
   }

 //当前组合期间指定债券买入利息  - 区分所属分类和所有分析代码
   public HashMap getBoughtIntDetail(String sPortCode,Date dBeginDate,Date dEndDate,String sSecurityCode,String sNum) throws YssException{
	   String sqlStr = "";
	   ResultSet rs = null;
	   HashMap hmBoughtInt =  getBoughtIntBFDetail(sPortCode,dBeginDate,dEndDate,sSecurityCode);//取期初买入利息余额

	   HashMap hmPaidInt = new HashMap();
	   sqlStr = "select FSecurityCode, max(FTransDate) as FLastINTDate from " + pub.yssGetTableName("tb_data_secrecpay") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '02FI' and FTransDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and FCheckState = 1 group by FSecurityCode";
	   try {
		   rs = dbl.openResultSet(sqlStr);
		   while (rs.next()) {
			   hmPaidInt.put(rs.getString("FSecurityCode"), rs.getDate("FLastINTDate"));
		   }
	   } catch (Exception ex) {
		   throw new YssException("获取债券交易数据出错", ex);
	   } finally {
		   dbl.closeResultSetFinal(rs);
	   }

	   //有派息调整买入利息为零
	   Iterator iterator = hmBoughtInt.keySet().iterator();
	   while(iterator.hasNext()){
		   String sKey = (String) iterator.next();
		   if(hmPaidInt.containsKey(sKey.split("#")[0])){
			   String sTmp = (String) hmBoughtInt.get(sKey);
			   hmBoughtInt.put(sKey, sTmp.split("#")[0] + "#0#0");
		   }
	   }

	   //取债券买入、转货转出、转货转入、债券卖出情况，计算买入利息的冲减金额
	   sqlStr = "select * from ( "
		   + " select 1 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FInvMgrCode as FAnalysisCode1,a.FBrokerCode as FAnalysisCode2,' ' as FAnalysisCode3,a.FBargainDate,a.FTradeAmount * c.FAmountInd as FAmount,a.FAccruedInterest * c.FAmountInd as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,'' as FNum from " + pub.yssGetTableName("tb_data_subtrade")  
		   + " a join " + pub.yssGetTableName("tb_para_security") + " b on a.FSecurityCode = b.FSecurityCode join tb_base_tradetype c on a.FTradeTypeCode = c.FTradeTypeCode where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FCheckState = 1 and b.FCatCode = 'FI' and c.FTradeTypeCode = '01' and a.FBargainDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate)
		   + " union "
		   + " select 2 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FAnalysisCode1,a.FAnalysisCode2,' ' as FAnalysisCode3,a.FExchangeDate,abs(a.FAmount)*a.FInOutType as FAmount,abs(c.FMoney)*c.FInOut as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,a.FNum from " + pub.yssGetTableName("tb_data_integrated") + " a join (select * from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = 0) b on a.fnum = b.fnum "
		   //转货转入时取数出现错误，同一业务编号下有多个组合数据，对tb_001_data_secrecpay表添加组合限定 renzhi 20101103
		   + " join " + pub.yssGetTableName("tb_data_secrecpay") + " c on c.FInOut = -1 and c.fportcode = " + dbl.sqlString(sPortCode) +  " and b.frelanum = c.fnum where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = -1 "
		   //+ " join " + pub.yssGetTableName("tb_data_secrecpay") + " c on c.FInOut = -1 and b.frelanum = c.fnum where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = -1 "
		   + " union "
		   + " select 3 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FAnalysisCode1,a.FAnalysisCode2,' ' as FAnalysisCode3,a.FExchangeDate,abs(a.FAmount)*a.FInOutType as FAmount,0 as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,a.FNum from " + pub.yssGetTableName("tb_data_integrated")
		   + " a where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = -1 "
		   + " and a.fnum not in (select fnum from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = 0) "
		   + " union "		   
		   + " select 4 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FAnalysisCode1,a.FAnalysisCode2,' ' as FAnalysisCode3,a.FExchangeDate,abs(a.FAmount)*a.FInOutType as FAmount,abs(c.FMoney)*c.FInOut as FBoughtInt,a.FBaseCuryRate,case when d.FSecurityCode <> a.FSecurityCode then d.FSecurityCode else '' end as FSecurityCode2,a.FNum from " + pub.yssGetTableName("tb_data_integrated") + " a join (select * from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = 0) b on a.fnum = b.fnum "
		   //转货转入时取数出现错误，同一业务编号下有多个组合数据，对tb_001_data_secrecpay表添加组合限定 renzhi 20101103
		   + " join " + pub.yssGetTableName("tb_data_secrecpay") + " c on c.FInOut = 1 and c.fportcode = " + dbl.sqlString(sPortCode) +  " and b.frelanum = c.fnum join (select * from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = -1) d on a.fnum = d.fnum where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = 1 "
		   //+ " join " + pub.yssGetTableName("tb_data_secrecpay") + " c on c.FInOut = 1 and b.frelanum = c.fnum join (select * from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = -1) d on a.fnum = d.fnum where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = 1 "
		   + " union "
		   + " select 5 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FAnalysisCode1,a.FAnalysisCode2,' ' as FAnalysisCode3,a.FExchangeDate,abs(a.FAmount)*a.FInOutType as FAmount,0 as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,a.FNum from " + pub.yssGetTableName("tb_data_integrated")
		   + " a where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FTradeTypeCode in ('80', '81') and a.FTsfTypeCode = '05' and a.FSubTsfTypeCode = '05FI' and a.FExchangeDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.FCheckState = 1 and a.FInOutType = 1 "
		   + " and a.fnum not in (select fnum from " + pub.yssGetTableName("tb_data_integrated") + " where FInOutType = 0) "
		   + " union "
		   + " select 6 as ordernum,a.FSecurityCode,a.FAttrClsCode,a.FInvMgrCode as FAnalysisCode1,a.FBrokerCode as FAnalysisCode2,' ' as FAnalysisCode3,a.FBargainDate,a.FTradeAmount * c.FAmountInd as FAmount,a.FAccruedInterest * c.FAmountInd as FBoughtInt,a.FBaseCuryRate,'' as FSecurityCode2,'' as FNum from " + pub.yssGetTableName("tb_data_subtrade")
		   + " a join " + pub.yssGetTableName("tb_para_security") + " b on a.FSecurityCode = b.FSecurityCode join tb_base_tradetype c on a.FTradeTypeCode = c.FTradeTypeCode where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FCheckState = 1  and b.FCatCode = 'FI' and c.FTradeTypeCode = '02'  and a.FBargainDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " ) ";
	   if(sSecurityCode != null){
		   sqlStr += " where FSecurityCode = " + dbl.sqlString(sSecurityCode) + " and FNum <> " + dbl.sqlString(sNum);
	   }
	   sqlStr += " order by FSecurityCode, FBargainDate, OrderNum";
	   try {
		   rs = dbl.openResultSet(sqlStr);
		   while (rs.next()) {
			   String sKey = rs.getString("FSecurityCode") + "#" + rs.getString("FAttrClsCode") + "#" + rs.getString("FAnalysisCode1") + "#" + rs.getString("FAnalysisCode2") + "#" + rs.getString("FAnalysisCode3") + "#END";
			   String sTmp = (String)hmBoughtInt.get(sKey);
			   double dAmount = 0;
			   double dBal = 0;
			   double dPortBal = 0;
			   if(sTmp != null){
				   dAmount = Double.parseDouble(sTmp.split("#")[0]);
				   dBal = Double.parseDouble(sTmp.split("#")[1]);
				   dPortBal = Double.parseDouble(sTmp.split("#")[2]);
			   }
			   Date paidDate = (Date)hmPaidInt.get(rs.getString("FSecurityCode"));
			   if(paidDate != null && (rs.getDate("FBargainDate").before(paidDate)
					   || rs.getDate("FBargainDate").equals(paidDate))){
				   dAmount += rs.getDouble("FAmount");
				   hmBoughtInt.put(sKey, String.valueOf(dAmount) + "#0#0");
			   } else {
				   if(rs.getDouble("FBoughtInt") == 0){
					   dAmount += rs.getDouble("FAmount");
					   hmBoughtInt.put(sKey, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
					   continue;
				   } else if(rs.getDouble("FBoughtInt") > 0){//流入时累加买入利息
					   dBal += YssFun.roundIt(rs.getDouble("FBoughtInt")*100,2);
					   dPortBal += YssFun.roundIt(rs.getDouble("FBoughtInt")*rs.getDouble("FBaseCuryRate"),2);
				   } else{//流出时冲减买入利息
					   if(dAmount != 0){
						   dBal += YssFun.roundIt(dBal*rs.getDouble("FAmount")/dAmount,2);
						   dPortBal += YssFun.roundIt(dPortBal*rs.getDouble("FAmount")/dAmount,2);
					   }else{
						   continue;
					   }
				   }
				   dAmount += rs.getDouble("FAmount");
				   hmBoughtInt.put(sKey, String.valueOf(dAmount) + "#" + String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
			   }
		   }
	   } catch (Exception ex) {
		   throw new YssException("获取债券交易数据出错", ex);
	   } finally {
		   dbl.closeResultSetFinal(rs);
	   }
	   return hmBoughtInt;
   }

}
