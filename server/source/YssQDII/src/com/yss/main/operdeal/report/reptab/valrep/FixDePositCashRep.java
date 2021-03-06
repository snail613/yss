package com.yss.main.operdeal.report.reptab.valrep;

import com.yss.util.YssException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.yss.main.operdeal.report.reptab.valrep.pojo.ValRepBean;
import com.yss.util.YssFun;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public class FixDePositCashRep extends BaseValRep{
   public FixDePositCashRep() {
   }
   private ArrayList alFixCash=null;
   public ArrayList getValRepData() throws YssException {
      alFixCash =new ArrayList();
      getFixDePositCash();
      return alFixCash;
   }
   //合并邵宏伟修改报表代码 xuqiji 20100608
   private void getFixDePositCash() throws YssException{
      ResultSet rs =null;
      String sqlStr="";

      HashMap hmCostDetail = getCostDetail(sPortCode, dBeginDate, dEndDate);
      HashMap hmCostAssetType = getCostAssetType(hmCostDetail);
      double BaseCuryRate = 0;

      try{
    	  sqlStr = "select * from ("
    		  + " select  FAccType || '##' || FSubAccType || '##' || FAssetType || '##' || FCuryCode || '##' || FCashAccCode as FOrderCode, FPortCode, FNavDate, FCashAccCode, FCashAccName, FCuryCode, FSavingDate, FMatureDate, FPerValue, FBaseCuryRate, FAccBalance, FLXBal, FBFLXBal, FPortMarketValueRatio, FAssetType from ( "
    		  + " select nvl(a.FPortCode, d.FPortCode) as FPortCode, " + dbl.sqlDate(dEndDate) + " as FNavDate, nvl(a.FCashAccCode, d.FCashAccCode) as FCashAccCode, nvl(e.FCashAccName, ' ') as FCashAccName,nvl(e.FAccType, d.FAccType) as FAccType, nvl(e.FSubAccType, d.FSubAccType) as FSubAccType, nvl(a.FAssetType, d.FAssetType) as FAssetType, nvl(a.FCuryCode, d. FCuryCode) as FCuryCode, "
    		  + " nvl(FAccBalance, 0) as FAccBalance, FBaseCuryRate, nvl(round(round(FAccBalance*FBaseCuryRate, 2)/FPortMarketValue, 4), 0) as FPortMarketValueRatio, nvl(FSavingDate, to_date('9998-12-31','yyyy-MM-dd')) as FSavingDate, nvl(FMatureDate, to_date('9998-12-31','yyyy-MM-dd')) as FMatureDate, nvl(FPerValue, 0) as FPerValue, nvl(FLXBal, 0) as FLXBal, nvl(FBFLXBal, 0) as FBFLXBal from ( "
    		  + " select FPortCode, FStorageDate as FNavDate, FCashAccCode, FAnalysisCode2 as FAssetType, FCuryCode, sum(FAccBalance) as FAccBalance from " + pub.yssGetTableName("tb_stock_cash") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " /*and FAccBalance <> 0*/ group by FPortCode, FStorageDate, FCashAccCode, FAnalysisCode2, FCuryCode) a "
    		  + " join (select FPortCode, FCashAccCode, FCashAccName, FCuryCode, FAccType, FSubAccType from " + pub.yssGetTableName("tb_para_cashaccount") + "  where FCheckState = 1 and FSubAccType = '0102') e on a.FPortCode = e.FPortCode and a.FCashAccCode = e.FCashAccCode "
    		  + " left join (select FPortCode, FDate as FNavDate, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on a.FPortCode = p.FPortCode and a.FNavDate = p.FNavDate "
    		  + " left join (select FPortCode, FCashAccCode, FAnalysisCode2 as FAssetType, sum(round(FBal, 2)) as FLXBal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and FSubTsfTypeCode = '06DE' group by FPortCode, FCashAccCode, FAnalysisCode2) c on a.FPortCode = c.FPortCode and a.FCashAccCode = c.FCashAccCode and a.FAssetType = c.FAssetType "
    		  + " left join (select d1.FPortCode, d1.FCashAccCode, FAccType, FSubAccType, FCuryCode, FAnalysisCode2 as FAssetType, sum(round(FBal, 2)) as FBFLXBal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " d1 join (select FPortCode, FCashAccCode, FAccType, FSubAccType from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCheckState = 1 and FSubAccType = '0102') d2 on d1.FPortCode = d2.FPortCode and d1.FCashAccCode = d2.FCashAccCode where d1.FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '06DE' and FCheckState = 1 and FBal <> 0 and "
    		  + " (FStorageDate = " + dbl.sqlDate(dBeginDate) + " - 1 and substr(fyearmonth, 0, 4) = to_char(" + dbl.sqlDate(dBeginDate) + ",'yyyy') or " + dbl.sqlDate(dBeginDate) + " = to_date(to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '-01' || '-01', 'yyyy-MM-dd') and fyearmonth = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '00') group by d1.FPortCode, d1.FCashAccCode, FAccType, FSubAccType, FCuryCode, FAnalysisCode2) d on a.FPortCode = d.FPortCode and a.FCashAccCode = d.FCashAccCode and a.FAssetType = d.FAssetType "
    		  + " left join (select distinct FPortCode, FCashAccCode, FAnalysisCode2 as FAssetType, FSavingDate, FMatureDate, FPerValue * 100 as FPerValue from " + pub.yssGetTableName("tb_cash_savinginacc") + " b1 left join (select b21.FFormulaCode, b22.FPerValue from " + pub.yssGetTableName("Tb_Para_Performula") + " b21 join " + pub.yssGetTableName("tb_para_performula_rela") + " b22 on b21.FFormulaCode = b22.FFormulaCode) b2 on b1.FFormulaCode = b2.FFormulaCode where FPortCode = " + dbl.sqlString(sPortCode) + " and FSavingDate <= " + dbl.sqlDate(dEndDate) + " and FMatureDate > " + dbl.sqlDate(dEndDate)
           + " and b1.fcheckstate = 1) b on a.FPortCode = b.FPortCode and a.FCashAccCode = b.FCashAccCode "
    		  + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") f on a.FCuryCode = f.FCuryCode "
    		  + " union "
    		  + " select nvl(a.FPortCode, d.FPortCode) as FPortCode, " + dbl.sqlDate(dEndDate) + " as FNavDate, nvl(a.FCashAccCode, d.FCashAccCode) as FCashAccCode, nvl(e.FCashAccName, ' ') as FCashAccName, nvl(e.FAccType, d.FAccType) as FAccType, nvl(e.FSubAccType, d.FSubAccType) as FSubAccType, nvl(a.FAssetType, d.FAssetType) as FAssetType, nvl(a.FCuryCode, d. FCuryCode) as FCuryCode, "
    		  + " nvl(FAccBalance, 0) as FAccBalance, FBaseCuryRate, nvl(round(round(FAccBalance*FBaseCuryRate, 2)/FPortMarketValue, 4), 0) as FPortMarketValueRatio, nvl(FSavingDate, to_date('9998-12-31','yyyy-MM-dd')) as FSavingDate, nvl(FMatureDate, to_date('9998-12-31','yyyy-MM-dd')) as FMatureDate, nvl(FPerValue, 0) as FPerValue, nvl(FLXBal, 0) as FLXBal, nvl(FBFLXBal, 0) as FBFLXBal from ( "
    		  + " select FPortCode, FStorageDate as FNavDate, FCashAccCode, FAnalysisCode2 as FAssetType, FCuryCode, sum(FAccBalance) as FAccBalance from " + pub.yssGetTableName("tb_stock_cash") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " /*and FAccBalance <> 0*/ group by FPortCode, FStorageDate, FCashAccCode, FAnalysisCode2, FCuryCode ) a "
    		  + " join (select FPortCode, FCashAccCode, FCashAccName, FCuryCode, FAccType, FSubAccType from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCheckState = 1 and FSubAccType = '0102') e on a.FPortCode = e.FPortCode and a.FCashAccCode = e.FCashAccCode "
    		  + " left join (select FPortCode, FDate as FNavDate, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on a.FPortCode = p.FPortCode and a.FNavDate = p.FNavDate "
    		  + " left join (select FPortCode, FCashAccCode, FAnalysisCode2 as FAssetType, sum(round(FBal, 2)) as FLXBal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and FSubTsfTypeCode = '06DE' group by FPortCode, FCashAccCode, FAnalysisCode2) c on a.FPortCode = c.FPortCode and a.FCashAccCode = c.FCashAccCode and a.FAssetType = c.FAssetType "
    		  + " right join (select d1.FPortCode, d1.FCashAccCode, FAccType, FSubAccType, FCuryCode, FAnalysisCode2 as FAssetType, sum(round(FBal, 2)) as FBFLXBal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " d1 join (select FPortCode, FCashAccCode, FAccType, FSubAccType from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCheckState = 1 and FSubAccType = '0102') d2 on d1.FPortCode = d2.FPortCode and d1.FCashAccCode = d2.FCashAccCode where d1.FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '06DE' and FCheckState = 1 and FBal <> 0 and "
    		  + " (FStorageDate = " + dbl.sqlDate(dBeginDate) + " - 1 and substr(fyearmonth, 0, 4) = to_char(" + dbl.sqlDate(dBeginDate) + ",'yyyy') or " + dbl.sqlDate(dBeginDate) + " = to_date(to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '-01' || '-01', 'yyyy-MM-dd') and fyearmonth = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '00') group by d1.FPortCode, d1.FCashAccCode, FAccType, FSubAccType, FCuryCode, FAnalysisCode2) d on a.FPortCode = d.FPortCode and a.FCashAccCode = d.FCashAccCode and a.FAssetType = d.FAssetType "
    		  + " left join (select distinct FPortCode, FCashAccCode, FAnalysisCode2 as FAssetType, FSavingDate, FMatureDate, FPerValue * 100 as FPerValue from " + pub.yssGetTableName("tb_cash_savinginacc") + " b1 left join (select b21.FFormulaCode, b22.FPerValue from " + pub.yssGetTableName("tb_para_performula") + " b21 join " + pub.yssGetTableName("tb_para_performula_rela") + " b22 on b21.FFormulaCode = b22.FFormulaCode) b2 on b1.FFormulaCode = b2.FFormulaCode where FPortCode = " + dbl.sqlString(sPortCode) + " and FSavingDate <= " + dbl.sqlDate(dEndDate) + " and FMatureDate > " + dbl.sqlDate(dEndDate)
           + " and b1.fcheckstate = 1) b on d.FPortCode = b.FPortCode and d.FCashAccCode = b.FCashAccCode "
    		  + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") f on d.FCuryCode = f.FCuryCode) "
    		  + " union "
    		  + " select  FAccType || '##' || FSubAccType || '##' || FAssetType || '##' || FCuryCode || '##total' as FOrderCode, FPortCode, FNavDate, '' as FCashAccCode, '港幣等值金額：' as FCashAccName, FCuryCode, to_date('9998-12-31','yyyy-MM-dd') as FSavingDate, to_date('9998-12-31','yyyy-MM-dd') as FMatureDate, 0 FPerValue, FBaseCuryRate, sum(FAccBalance)*FBaseCuryRate as FAccBalance, sum(FLXBal)*FBaseCuryRate as FLXBal, sum(FBFLXBal)*FBaseCuryRate as FBFLXBal, sum(FPortMarketValueRatio) as FPortMarketValueRatio, FAssetType from ("
    		  + " select nvl(a.FPortCode, d.FPortCode) as FPortCode, " + dbl.sqlDate(dEndDate) + " as FNavDate, nvl(a.FCashAccCode, d.FCashAccCode) as FCashAccCode, nvl(e.FCashAccName, ' ') as FCashAccName, nvl(e.FAccType, d.FAccType) as FAccType, nvl(e.FSubAccType, d.FSubAccType) as FSubAccType, nvl(a.FAssetType, d.FAssetType) as FAssetType, nvl(a.FCuryCode, d.FCuryCode) as FCuryCode, "
    		  + " nvl(FAccBalance, 0) as FAccBalance, FBaseCuryRate, nvl(round(round(FAccBalance*FBaseCuryRate, 2)/FPortMarketValue, 4), 0) as FPortMarketValueRatio, nvl(FSavingDate, to_date('9998-12-31','yyyy-MM-dd')) as FSavingDate, nvl(FMatureDate, to_date('9998-12-31','yyyy-MM-dd')) as FMatureDate, nvl(FPerValue, 0) as FPerValue, nvl(FLXBal, 0) as FLXBal, nvl(FBFLXBal, 0) as FBFLXBal from (select FPortCode, FStorageDate as FNavDate, FCashAccCode, FAnalysisCode2 as FAssetType, FCuryCode, sum(FAccBalance) as FAccBalance "
    		  + " from " + pub.yssGetTableName("tb_stock_cash") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " /*and FAccBalance <> 0*/ group by FPortCode, FStorageDate, FCashAccCode, FAnalysisCode2, FCuryCode) a join (select FPortCode, FCashAccCode, FCashAccName, FCuryCode, FAccType, FSubAccType from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCheckState = 1 and FSubAccType = '0102') e on a.FPortCode = e.FPortCode and a.FCashAccCode = e.FCashAccCode "
    		  + " left join (select FPortCode, FDate as FNavDate, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on a.FPortCode = p.FPortCode and a.FNavDate = p.FNavDate "
    		  + " left join (select FPortCode, FCashAccCode, FAnalysisCode2 as FAssetType, sum(round(FBal, 2)) as FLXBal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and FSubTsfTypeCode = '06DE' group by FPortCode, FCashAccCode, FAnalysisCode2) c "
    		  + " on a.FPortCode = c.FPortCode and a.FCashAccCode = c.FCashAccCode and a.FAssetType = c.FAssetType "
    		  + " left join (select d1.FPortCode, d1.FCashAccCode, FAccType, FSubAccType, FCuryCode, FAnalysisCode2 as FAssetType, sum(round(FBal, 2)) as FBFLXBal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " d1 join (select FPortCode, FCashAccCode, FAccType, FSubAccType from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCheckState = 1 and FSubAccType = '0102') d2 on d1.FPortCode = d2.FPortCode and d1.FCashAccCode = d2.FCashAccCode where d1.FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '06DE' and FCheckState = 1 and FBal <> 0 and "
    		  + " (FStorageDate = " + dbl.sqlDate(dBeginDate) + " - 1 and substr(fyearmonth, 0, 4) = to_char(" + dbl.sqlDate(dBeginDate) + ",'yyyy') or " + dbl.sqlDate(dBeginDate) + " = to_date(to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '-01' || '-01', 'yyyy-MM-dd') and fyearmonth = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '00') group by d1.FPortCode, d1.FCashAccCode, FAccType, FSubAccType, FCuryCode, FAnalysisCode2) d on a.FPortCode = d.FPortCode and a.FCashAccCode = d.FCashAccCode and a.FAssetType = d.FAssetType "
    		  + " left join (select distinct FPortCode, FCashAccCode, FAnalysisCode2 as FAssetType, FSavingDate, FMatureDate, FPerValue * 100 as FPerValue from " + pub.yssGetTableName("tb_cash_savinginacc") + " b1 left join (select b21.FFormulaCode, b22.FPerValue from " + pub.yssGetTableName("tb_para_performula") + " b21 join " + pub.yssGetTableName("tb_para_performula_rela") + " b22 on b21.FFormulaCode = b22.FFormulaCode) b2 on b1.FFormulaCode = b2.FFormulaCode where FPortCode = " + dbl.sqlString(sPortCode)
    		  + " and FSavingDate <= " + dbl.sqlDate(dEndDate) + " and FMatureDate > " + dbl.sqlDate(dEndDate) + " and b1.fcheckstate = 1) b on a.FPortCode = b.FPortCode and a.FCashAccCode = b.FCashAccCode "
    		  + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") f on a.FCuryCode = f.FCuryCode "
    		  + " union "
    		  + " select nvl(a.FPortCode, d.FPortCode) as FPortCode, " + dbl.sqlDate(dEndDate) + " as FNavDate, nvl(a.FCashAccCode, d.FCashAccCode) as FCashAccCode, nvl(e.FCashAccName, ' ') as FCashAccName, nvl(e.FAccType, d.FAccType) as FAccType, nvl(e.FSubAccType, d.FSubAccType) as FSubAccType, nvl(a.FAssetType, d.FAssetType) as FAssetType, nvl(a.FCuryCode, d. FCuryCode) as FCuryCode, "
    		  + " nvl(FAccBalance, 0) as FAccBalance, FBaseCuryRate, nvl(round(round(FAccBalance*FBaseCuryRate, 2)/FPortMarketValue, 4), 0) as FPortMarketValueRatio, nvl(FSavingDate, to_date('9998-12-31','yyyy-MM-dd')) as FSavingDate, nvl(FMatureDate, to_date('9998-12-31','yyyy-MM-dd')) as FMatureDate, nvl(FPerValue, 0) as FPerValue, nvl(FLXBal, 0) as FLXBal, nvl(FBFLXBal, 0) as FBFLXBal from (select FPortCode, FStorageDate as FNavDate, FCashAccCode, FAnalysisCode2 as FAssetType,FCuryCode, sum(FAccBalance) as FAccBalance, 0 as FFXValue "
    		  + " from " + pub.yssGetTableName("tb_stock_cash") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " /*and FAccBalance <> 0*/ group by FPortCode, FStorageDate, FCashAccCode, FAnalysisCode2, FCuryCode) a join (select FPortCode, FCashAccCode, FCashAccName, FCuryCode, FAccType, FSubAccType from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCheckState = 1 and FSubAccType = '0102') e on a.FPortCode = e.FPortCode and a.FCashAccCode = e.FCashAccCode "
    		  + " left join (select FPortCode, FDate as FNavDate, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on a.FPortCode = p.FPortCode and a.FNavDate = p.FNavDate "
    		  + " left join (select FPortCode, FCashAccCode, FAnalysisCode2 as FAssetType, sum(round(FBal, 2)) as FLXBal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(dEndDate) + " and FSubTsfTypeCode = '06DE' group by FPortCode, FCashAccCode, FAnalysisCode2) c "
    		  + " on a.FPortCode = c.FPortCode and a.FCashAccCode = c.FCashAccCode and a.FAssetType = c.FAssetType "
    		  + " right join (select d1.FPortCode, d1.FCashAccCode, FAccType, FSubAccType, FCuryCode, FAnalysisCode2 as FAssetType, sum(round(FBal, 2)) as FBFLXBal from " + pub.yssGetTableName("tb_stock_cashpayrec") + " d1 join (select FPortCode, FCashAccCode, FAccType, FSubAccType from " + pub.yssGetTableName("tb_para_cashaccount") + " where FCheckState = 1 and FSubAccType = '0102') d2 on d1.FPortCode = d2.FPortCode and d1.FCashAccCode = d2.FCashAccCode where d1.FPortCode = " + dbl.sqlString(sPortCode) + " and FSubTsfTypeCode = '06DE' and FCheckState = 1 and FBal <> 0 and "
    		  + " (FStorageDate = " + dbl.sqlDate(dBeginDate) + " - 1 and substr(fyearmonth, 0, 4) = to_char(" + dbl.sqlDate(dBeginDate) + ",'yyyy') or " + dbl.sqlDate(dBeginDate) + " = to_date(to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '-01' || '-01', 'yyyy-MM-dd') and fyearmonth = to_char(" + dbl.sqlDate(dBeginDate) + ", 'yyyy') || '00') group by d1.FPortCode, d1.FCashAccCode, FAccType, FSubAccType, FCuryCode, FAnalysisCode2) d on a.FPortCode = d.FPortCode and a.FCashAccCode = d.FCashAccCode and a.FAssetType = d.FAssetType "
    		  + " left join (select distinct FPortCode, FCashAccCode, FAnalysisCode2 as FAssetType, FSavingDate, FMatureDate, FPerValue * 100 as FPerValue from " + pub.yssGetTableName("tb_cash_savinginacc") + " b1 left join (select b21.FFormulaCode, b22.FPerValue from " + pub.yssGetTableName("tb_para_performula") + " b21 join " + pub.yssGetTableName("tb_para_performula_rela") + " b22 on b21.FFormulaCode = b22.FFormulaCode) b2 on b1.FFormulaCode = b2.FFormulaCode where FPortCode = " + dbl.sqlString(sPortCode)
    		  + " and FSavingDate <= " + dbl.sqlDate(dEndDate) + " and FMatureDate > " + dbl.sqlDate(dEndDate) + " and b1.fcheckstate = 1) b on d.FPortCode = b.FPortCode and d.FCashAccCode = b.FCashAccCode "
    		  + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") f on d.FCuryCode = f.FCuryCode "
    		  + " ) group by FPortCode, FNavDate, FCuryCode, FAccType, FSubAccType, FAssetType, FBaseCuryRate "
    		  + " ) order by FPortCode, FNavDate, FCuryCode, FAssetType, FCashAccName, FOrderCode";

              rs =dbl.openResultSet(sqlStr);

              double TotalCost = 0;//当前币种合计买入成本价
              double PortTotalCost = 0;//当前币种合计买入成本价港币等值
              double TotalBal = 0;//当前币种合计成本价
              double SyBaseCuryTotalBal = 0;//当前币种的汇兑损益合计数
              String LastCuryCode = "";//上条记录的币种
              String LastAssetType = "";//上条记录的资产类别

              while(rs.next()){
                 valBean =new ValRepBean();
                 String OrderCode = rs.getString("FOrderCode");
                 String CashAccCode = rs.getString("FCashAccCode");//待处理的帐户
                 String CuryCode = rs.getString("FCuryCode");//待处理的币种（按币种及资本类别分类汇总）
                 String AssetType = rs.getString("FAssetType");//待处理的资本类别
                 double AccBalance = rs.getDouble("FAccBalance");
                 BaseCuryRate = rs.getDouble("FBaseCuryRate");
                 if(CashAccCode == null && LastCuryCode.equals(CuryCode) && LastAssetType.equals(AssetType)){
                    valBean.setBoughtInt((TotalBal-TotalCost) * rs.getDouble("FBaseCuryRate"));
                    valBean.setTotalCost(PortTotalCost);
                    valBean.setOtherCost(PortTotalCost);
                    valBean.setPortCuryRate(0);
                    valBean.setSyvBaseCuryBal(SyBaseCuryTotalBal);
                    TotalCost = 0;
                    TotalBal = 0;
                    PortTotalCost = 0;
                    SyBaseCuryTotalBal = 0;
                 }else{

                		 String sKey = CashAccCode + "#" + OrderCode.split("##")[2] + "#END";
                		 double AccDC = 0;
                    	 double PortAccDC = 0;
                    	 double SyBaseCuryBal = 0;
                    	 double AvgBaseCuryRate = 0;
                		 if(hmCostAssetType!=null && !hmCostAssetType.isEmpty()){
                			 if(AccBalance == 0){
                				 hmCostAssetType.remove(sKey);
                			 }
                			 String sValue = (String) hmCostAssetType.get(sKey);
                			 if(sValue != null && !"".equals(sValue)){
                        		 AccDC = Double.parseDouble(sValue.split("#")[0]);
                            	 PortAccDC = Double.parseDouble(sValue.split("#")[1]);
                            	 //汇兑损益取【买入成本原币*估值日汇率-买入成本港币等值】
                            	 SyBaseCuryBal = Math.round((AccDC * rs.getDouble("FBaseCuryRate") - PortAccDC) * Math.pow(10, 2)) / Math.pow(10, 2);
                            	 if(AccDC != 0){
                                     AvgBaseCuryRate = PortAccDC / AccDC;//平均汇率=买入成本价组合货币金额/买入成本价原币金额
                                  }
                        	 }
                		 }
                    	 if("HKD".equalsIgnoreCase(CuryCode)){
                             AvgBaseCuryRate = 1;//汇率修正
                         }
            			 valBean.setTotalCost(AccDC); //买入成本价
            			 valBean.setOtherCost(PortAccDC);//买入成本价港币等值
                         valBean.setBoughtInt(AccBalance - AccDC); //利息收入

                         valBean.setPortCuryRate(AvgBaseCuryRate);
                         valBean.setSyvBaseCuryBal(SyBaseCuryBal);

                         TotalCost += AccDC;//将当前帐户的买入成本价合计数添加到币种及资本类别（汇总项）合计数中
                         TotalBal += AccBalance;
                         PortTotalCost += PortAccDC;
                         SyBaseCuryTotalBal += SyBaseCuryBal;

                 }
                 //保留币种和资产类别信息，方便按币种及资产类别分组统计利息收入合计数
                 LastCuryCode = CuryCode;
                 LastAssetType = AssetType;

                 valBean.setSecurityCode(rs.getString("FCashAccCode"));
                 valBean.setSecurityName(rs.getString("FCashAccName"));
                 valBean.setCuryCode(rs.getString("FCuryCode"));
                 valBean.setFactRate(rs.getDouble("FPerValue"));     //定存利率
                 valBean.setInsStartDate(rs.getDate("FSavingDate")); //定存日期
                 valBean.setInsEndDate(rs.getDate("FMatureDate"));  //到期日期
                 valBean.setMvalue(rs.getDouble("FAccBalance"));   //成本价
//                 valBean.setTotalCost(rs.getDouble("FAccDC")); //买入成本价
//                 valBean.setBoughtInt(rs.getDouble("FALXBal"));    //利息收入
                 valBean.setLXVBal(rs.getDouble("FLXBal"));       //应收利息
                 valBean.setBFlxBal(rs.getDouble("FBFLXBal"));     //应收利息-上期结余
//                 valBean.setSyvBaseCuryBal(rs.getDouble("FSYBaseCuryBal"));       //汇兑损益
                 valBean.setFundAllotProportion(rs.getDouble("FPortMarketValueRatio")); //比例
                 valBean.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
//                 valBean.setPortCuryRate(rs.getDouble("FAvgBaseCuryRate")); //平均汇率
                 valBean.setOrder(rs.getString("FOrderCode"));
                 alFixCash.add(valBean);
              }
      }catch(Exception ex){
         throw new YssException("获取定期存款数据出错",ex);
      }finally{
         dbl.closeResultSetFinal(rs);
      }

      //保存HashMap数据到定期买入成本库存中
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
    	  if(hmCostDetail == null || hmCostDetail.isEmpty()){
    		  return;
    	  }

    	  sqlStr = "delete from " + pub.yssGetTableName("Tb_Stock_Cost") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FYearMonth = " + dbl.sqlString(sYearMonth);
    	  dbl.executeSql(sqlStr);

    	  sqlStr = "insert into " + pub.yssGetTableName("Tb_Stock_Cost") + " (FCostAccCode,FYearMonth,FStorageDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,"
    	  + "FAccBalance,FBaseCuryRate,FBaseCuryBal,FPortCuryRate,FPortCuryBal,FStorageInd,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) "
    	  + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


          stm = dbl.openPreparedStatement(sqlStr);
          conn = dbl.loadConnection();
          conn.setAutoCommit(bTrans);
          bTrans = true;

          Iterator iterator = hmCostDetail.keySet().iterator();
          while(iterator.hasNext()){
        	  String sKey = (String) iterator.next();
        	  String sValue = (String) hmCostDetail.get(sKey);
        	  double dBal = Double.parseDouble(sValue.split("#")[0]);
        	  double dPortBal = Double.parseDouble(sValue.split("#")[1]);
        	  if(dBal == 0){
        		  continue;
        	  }
        	  stm.setString(1, sKey.split("#")[0]);
        	  stm.setString(2, sYearMonth);
        	  stm.setDate(3, YssFun.toSqlDate(dStorageDate));
        	  stm.setString(4, sPortCode);
        	  stm.setString(5, sKey.split("#")[1]);
        	  stm.setString(6, sKey.split("#")[2]);
        	  stm.setString(7, sKey.split("#")[3]);
        	  stm.setDouble(8, dBal);
        	  stm.setDouble(9, BaseCuryRate);
        	  stm.setDouble(10, dPortBal);
        	  stm.setDouble(11, 1);
        	  stm.setDouble(12, dPortBal);
        	  stm.setDouble(13, 2);
        	  stm.setDouble(14, 1);
        	  stm.setString(15, pub.getUserCode());
        	  stm.setString(16, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        	  stm.setString(17, pub.getUserCode());
        	  stm.setString(18, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        	  stm.addBatch();
          }
          stm.executeBatch();
          conn.commit();
          conn.setAutoCommit(bTrans);
          bTrans = false;
      }catch(Exception ex){
         throw new YssException("保存定期存款买入成本价库存出错",ex);
      }finally{
    	  dbl.endTransFinal(conn, bTrans);
      }
   }

   //以下为定期存款买入成本价算法的实现  - 李高辉  - 2009-11-28
   //获取买入成本价 - 只区分帐户
   //当前 组合 期间所有帐户的买入成本价  - 只区分帐户
   private HashMap getCost(HashMap hmCostDetail) throws YssException{
	   String CashAccCode = null;
	   HashMap hmCost = new HashMap();//不区分资产类别的HashMap
	   if(hmCostDetail == null || hmCostDetail.isEmpty()){
		   return null;
	   }
	   Iterator iterator = hmCostDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 CashAccCode = sKey.split("#")[0];
  		 String sTmp1 = (String) hmCostDetail.get(sKey);
  		 String sTmp2 = (String) hmCost.get(CashAccCode);
  		 double dBal = 0;
  		 double dPortBal = 0;
  		 if(sTmp2 != null && !"".equals(sTmp2)){
  			 dBal = Double.parseDouble(sTmp2.split("#")[0]);
  			 dPortBal = Double.parseDouble(sTmp2.split("#")[1]);
  		 }
  		 dBal += Double.parseDouble(sTmp1.split("#")[0]);
  		 dPortBal = Double.parseDouble(sTmp1.split("#")[1]);
  		 hmCost.put(CashAccCode, String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
	   }
	   return hmCost;
   }
   //当前 组合 期间所有帐户的买入成本价 - 只区分帐户
   public HashMap getCost(String sPortCode,Date dBeginDate,Date dEndDate) throws YssException{
	   return getCost(sPortCode,dBeginDate,dEndDate,null);
   }
   //当前组合 期间指定 帐户的买入成本价  - 只区分帐户
   public HashMap getCost(String sPortCode,Date dBeginDate,Date dEndDate,String sCashAccCode) throws YssException{
	   String CashAccCode = sCashAccCode;
	   HashMap hmCostDetail = getCostDetail(sPortCode,dBeginDate,dEndDate,sCashAccCode);//区分资产类别的HashMap
	   HashMap hmCost = new HashMap();//不区分资产类别的HashMap
	   Iterator iterator = hmCostDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 if(sCashAccCode == null){
  			CashAccCode = sKey.split("#")[0];
  		 }
  		 if(sKey.split("#")[0].equals(CashAccCode)){
  			 String sTmp1 = (String) hmCostDetail.get(sKey);
  			 String sTmp2 = (String) hmCost.get(CashAccCode);
  			 double dBal = 0;
  			 double dPortBal = 0;
  			 if(sTmp2 != null && !"".equals(sTmp2)){
  				dBal = Double.parseDouble(sTmp2.split("#")[0]);
  				dPortBal = Double.parseDouble(sTmp2.split("#")[1]);
  			 }
  			 dBal += Double.parseDouble(sTmp1.split("#")[0]);
  			 dPortBal = Double.parseDouble(sTmp1.split("#")[1]);
  			 hmCost.put(CashAccCode, String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
  		 }
	   }
	   return hmCost;
   }

   //获取买入成本价 - 区分资本类型
   //当前 组合 期间所有帐户的买入成本价  - 区分资本类型
   private HashMap getCostAssetType(HashMap hmCostDetail) throws YssException{
	   HashMap hmCostAssetType = new HashMap();//不区分资产类别的HashMap
	   if(hmCostDetail == null || hmCostDetail.isEmpty()){
		   return null;
	   }
	   Iterator iterator = hmCostDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 String sKeyAssetType = sKey.split("#")[0] + "#" + sKey.split("#")[2] + "#END";

  		 String sTmp1 = (String) hmCostDetail.get(sKey);
  		 String sTmp2 = (String) hmCostAssetType.get(sKeyAssetType);
  		 double dBal = 0;
  		 double dPortBal = 0;
  		 if(sTmp2 != null && !"".equals(sTmp2)){
  			 dBal = Double.parseDouble(sTmp2.split("#")[0]);
  			 dPortBal = Double.parseDouble(sTmp2.split("#")[1]);
  		 }
  		 dBal += Double.parseDouble(sTmp1.split("#")[0]);
  		 dPortBal += Double.parseDouble(sTmp1.split("#")[1]);
  		 hmCostAssetType.put(sKeyAssetType, String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
	   }
	   return hmCostAssetType;
   }
   //当前 组合 期间所有帐户的买入成本价 - 区分资本类型
   public HashMap getCostAssetType(String sPortCode,Date dBeginDate,Date dEndDate) throws YssException{
	   return getCostAssetType(sPortCode,dBeginDate,dEndDate,null);
   }
   //当前组合 期间指定 帐户的买入成本价 - 区分资本类型
   public HashMap getCostAssetType(String sPortCode,Date dBeginDate,Date dEndDate,String sCashAccCode) throws YssException{
	   String sKeyTmp = "";
	   String CashAccCode = sCashAccCode;
	   HashMap hmCostDetail = getCostDetail(sPortCode,dBeginDate,dEndDate,sCashAccCode);//区分资产类别的HashMap
	   HashMap hmCostAssetType = new HashMap();//不区分资产类别的HashMap
	   Iterator iterator = hmCostDetail.keySet().iterator();
	   while(iterator.hasNext()){
  		 String sKey = (String) iterator.next();
  		 String sKeyAssetType = sKey.split("#")[0] + "#" + sKey.split("#")[1] + "#END";

  		 if(sCashAccCode == null || "".equals(sCashAccCode)){
  			CashAccCode = sKey.split("#")[0];
  		 }
  		 if(sKey.split("#")[0].equals(CashAccCode)){
  			 String sTmp1 = (String) hmCostDetail.get(sKey);
  			 String sTmp2 = (String) hmCostAssetType.get(sKeyAssetType);
  			 double dBal = 0;
  			 double dPortBal = 0;
  			 if(sTmp2 != null && !"".equals(sTmp2)){
  				dBal = Double.parseDouble(sTmp2.split("#")[0]);
  				dPortBal = Double.parseDouble(sTmp2.split("#")[1]);
  			 }
  			 dBal += Double.parseDouble(sTmp1.split("#")[0]);
  			 dPortBal += Double.parseDouble(sTmp1.split("#")[1]);
  			 hmCostAssetType.put(sKeyAssetType, String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
  		 }

	   }
	   return hmCostAssetType;
   }

   //获取买入成本价  - 区分所有分析代码
   //当前 组合 期间所有帐户的买入成本价 - 区分所有分析代码
   public HashMap getCostDetail(String sPortCode,Date dBeginDate,Date dEndDate) throws YssException{
	   return getCostDetail(sPortCode,dBeginDate,dEndDate,null);
   }
   //当前组合 期间指定 帐户的买入成本价 - 区分所有分析代码
   //合并邵宏伟修改报表代码 xuqiji 20100608
   public HashMap getCostDetail(String sPortCode,Date dBeginDate,Date dEndDate,String sCashAccCode) throws YssException{
	   String sqlStr = "";
	   ResultSet rs = null;
	   HashMap hmCostDetail = new HashMap();
	   //取买入成本价库存
	   sqlStr = "select fcostacccode, fanalysiscode1, fanalysiscode2, fanalysiscode3, sum(round(faccbalance, 2)) as fbal, sum(round(fportcurybal, 2)) as fportbal from " + pub.yssGetTableName("tb_stock_cost") + " where fportcode = " + dbl.sqlString(sPortCode)
			+ " and (to_char("+dbl.sqlDate(dBeginDate)+",'MM')='01' and fyearmonth = to_char("+dbl.sqlDate(dBeginDate)+",'yyyy')||'00' or to_char("+dbl.sqlDate(dBeginDate)+",'MM') <> '01' and fstoragedate = " + dbl.sqlDate(dBeginDate) + ")";
	   if(sCashAccCode != null){
		   sqlStr += " and fcostacccode = " + dbl.sqlString(sCashAccCode) ;
	   }
	   sqlStr += " group by fcostacccode, fanalysiscode1, fanalysiscode2, fanalysiscode3";
	   try {
		   rs = dbl.openResultSet(sqlStr);
		   while (rs.next()) {
			   String sKey = rs.getString("fcostacccode") + "#" + rs.getString("fanalysiscode1") + "#" + rs.getString("fanalysiscode2") + "#" + rs.getString("fanalysiscode3") + "#END";
			   hmCostDetail.put(sKey, rs.getDouble("fbal") + "#" + rs.getDouble("fportbal"));
		   }
	   } catch (Exception ex) {
		   throw new YssException("获取定期存款买入成本价库存出错", ex);
	   } finally {
		   dbl.closeResultSetFinal(rs);
	   }

	   //取买入成本价成本的资金流入流出(对于含息流出成本的应剔除含息部分)
	   sqlStr = "select x.FNum, x.FCashAccCode, x.FAnalysisCode1, x.FAnalysisCode2, x.FAnalysisCode3, x.FTransferDate, x.FCuryCode, x.FAccDC, FInOut, case when FMoney is not null then 1 else 0 end as FAllOut, x.FBaseCuryRate from (select a1.fnum, a1.FCashAccCode, a1.fanalysiscode1, a1.fanalysiscode2, a1.fanalysiscode3, a2.ftransferdate, a1.FMoney * a1.FInOut as FAccDC, a1.Fbasecuryrate, a3.FCuryCode, FInOut from " + pub.yssGetTableName("tb_cash_subtransfer") + " a1 join " + pub.yssGetTableName("tb_cash_transfer")
	   	   + " a2 on a1.FNum = a2.FNum join " + pub.yssGetTableName("tb_para_cashaccount") + " a3 on a1.fcashacccode = a3.fcashacccode where (a2.FTsfTypeCode = '01' or a2.FTsfTypeCode = '04') and a3.fsubacctype = '0102' and a2.FSavingNum is null and a2.FNumType is null /*and a2.fsubtsftypecode is null*/ and a1.FCheckState = 1 and a2.FCheckState = 1 and a3.FCheckState = 1 and a2.FTransferDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate)
	   	   + " and a1.FPortCode = " + dbl.sqlString(sPortCode) + ") x left join (select a.fnum, a.fcashacccode, a.fanalysiscode1, a.fanalysiscode2, a.fanalysiscode3, a.ftransferdate, c.fmoney from (select a1.*, a2.ftransferdate from (select fnum, fcashacccode, sum(fmoney * finout) as ftotal from " + pub.yssGetTableName("tb_cash_subtransfer") + " where fcheckstate = 1 group by fnum, fcashacccode having sum(fmoney * finout) <> 0) a0 "
		   + " left join (select * from " + pub.yssGetTableName("tb_cash_subtransfer") + " where fcheckstate = 1) a1 on a0.fnum = a1.fnum and a0.fcashacccode = a1.fcashacccode left join (select * from " + pub.yssGetTableName("tb_cash_transfer") + " where fcheckstate = 1) a2 on a0.fnum = a2.fnum where (a2.FTsfTypeCode = '01' or a2.FTsfTypeCode = '04') /*and a2.fsubtsftypecode is null*/ and a2.FSavingNum is null and a2.FNumType is null) a "
		   + " left join (select * from " + pub.yssGetTableName("tb_cash_savinginacc") + " where fcheckstate = 1) b on a.fportcode = b.fportcode and a.fcashacccode = b.fcashacccode and a.ftransferdate = b.fmaturedate and a.fanalysiscode1 = b.fanalysiscode1 and a.fanalysiscode2 = b.fanalysiscode2 and a.fanalysiscode3 = b.fanalysiscode3 "
		   + " left join (select c1.fportcode,c1.fcashacccode,c1.fanalysiscode1,c1.fanalysiscode2,c1.fanalysiscode3,c2.ftransdate,sum(c1.fmoney) as fmoney from " + pub.yssGetTableName("tb_cash_subtransfer")+ " c1 left join (select * from " + pub.yssGetTableName("tb_cash_transfer") + " where fcheckstate = 1) c2 on c1.fnum = c2.fnum where c2.ftsftypecode = '02' and c1.fcheckstate = 1 group by c1.fportcode, c1.fcashacccode, c1.fanalysiscode1, c1.fanalysiscode2, c1.fanalysiscode3, c2.ftransdate) c "
		   + " on a.fportcode = c.fportcode and a.fcashacccode = c.fcashacccode and a.fanalysiscode1 = c.fanalysiscode1 and a.fanalysiscode2 = c.fanalysiscode2 and a.fanalysiscode3 = c.fanalysiscode3 and a.ftransferdate = c.ftransdate where a.fportcode = " + dbl.sqlString(sPortCode)
		   + " and a.ftransferdate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + " and a.finout = -1 and a.fmoney - (b.finmoney + c.fmoney) >= 0 ) y on x.fnum = y.fnum and x.fcashacccode = y.fcashacccode and x.ftransferdate = y.ftransferdate and x.fanalysiscode1 = y.fanalysiscode1 and x.fanalysiscode2 = y.fanalysiscode2 and x.fanalysiscode3 = y.fanalysiscode3 ";
	   if(sCashAccCode != null){
		   sqlStr += " and x.FCashAccCode = " + dbl.sqlString(sCashAccCode);
	   }
	   sqlStr += " order by x.FCashAccCode, x.FAnalysisCode1, x.FAnalysisCode2, x.FAnalysisCode3, x.FTransferDate, x.FInOut desc";
	   try {
		   rs = dbl.openResultSet(sqlStr);
		   //清除本期买入成本推演的历史数据
		   sqlStr = "delete from " + pub.yssGetTableName("Tb_Data_Cost") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FTransDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate);
	       dbl.executeSql(sqlStr);

		   while (rs.next()) {
			   String strFNum = rs.getString("FNum");
			   String strCashAccCode = rs.getString("FCashAccCode");
			   String strAnalysisCode1 = rs.getString("FAnalysisCode1");
			   String strAnalysisCode2 = rs.getString("FAnalysisCode2");
			   String strAnalysisCode3 = rs.getString("FAnalysisCode3");
			   Date dtTransDate = rs.getDate("FTransferDate");
			   String strCuryCode = rs.getString("FCuryCode");
			   double dStorageCost = rs.getDouble("FAccDC");
			   double dInOut = rs.getDouble("FInOut");
			   double dAllout = rs.getDouble("FAllOut");
			   double dTransRate = rs.getDouble("FBaseCuryRate");
			   double dHKDCost = 0.0;

			   String sKey = strCashAccCode + "#" + strAnalysisCode1 + "#" + strAnalysisCode2 + "#" + strAnalysisCode3 + "#END";
			   String sTmp = (String)hmCostDetail.get(sKey);
			   double dBal = 0;
			   double dPortBal = 0;
			   double dAllOutCost = 0;
			   double dAllOutPortCost = 0;
			   if(sTmp != null){
				   dBal = Double.parseDouble(sTmp.split("#")[0]);
				   dPortBal = Double.parseDouble(sTmp.split("#")[1]);
			   }
			   if(dAllout == 1){
				   dAllOutCost = dBal*dInOut;
				   dAllOutPortCost = dPortBal*dInOut;
			   }
			   if(dStorageCost>0){//成本流入
				   dBal += dStorageCost;
				   dPortBal += dStorageCost*dTransRate;//使用流入汇率

				   dHKDCost = dStorageCost*dTransRate;
			   }else{//成本流出
				   if(dBal != 0 && dPortBal != 0){
					   double dAvgRate = dPortBal/dBal;
					   dBal += dStorageCost;
					   dPortBal += dStorageCost*dAvgRate;//使用加权平均汇率（取出来的流出原币已经剔除了含息流出的利息部分）

					   dHKDCost = dStorageCost*dAvgRate;
				   }else{
					   continue;//无库存只考虑流入，不考虑流出，此类为资产重分配
				   }
			   }

			   if(dAllout == 1){
				 //存入买入成本数据表
				   sqlStr = "insert into " + pub.yssGetTableName("Tb_Data_Cost")
				   	+ " (FTransNum, FTransDate, FInOut, FTransRate, FPortCode, FCashAccCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, "
				   	+ " FCuryCode, FCuryRate, FStorageCost, FCost, FHKDCost) "
				   	+ " values (" + dbl.sqlString(strFNum) + "," + dbl.sqlDate(dtTransDate) + "," + dInOut + "," + dTransRate + ","
				   	+ dbl.sqlString(sPortCode) + "," + dbl.sqlString(strCashAccCode) + "," + dbl.sqlString(strAnalysisCode1) + ","
				   	+ dbl.sqlString(strAnalysisCode2) + "," + dbl.sqlString(strAnalysisCode3) + "," + dbl.sqlString(strCuryCode) + ",0,"
				   	+ dStorageCost + "," + dAllOutCost + "," + dAllOutPortCost + ")";

				   hmCostDetail.remove(sKey);
			   }else{
				 //存入买入成本数据表
				   sqlStr = "insert into " + pub.yssGetTableName("Tb_Data_Cost")
				   	+ " (FTransNum, FTransDate, FInOut, FTransRate, FPortCode, FCashAccCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, "
				   	+ " FCuryCode, FCuryRate, FStorageCost, FCost, FHKDCost) "
				   	+ " values (" + dbl.sqlString(strFNum) + "," + dbl.sqlDate(dtTransDate) + "," + dInOut + "," + dTransRate + ","
				   	+ dbl.sqlString(sPortCode) + "," + dbl.sqlString(strCashAccCode) + "," + dbl.sqlString(strAnalysisCode1) + ","
				   	+ dbl.sqlString(strAnalysisCode2) + "," + dbl.sqlString(strAnalysisCode3) + "," + dbl.sqlString(strCuryCode) + ",0,"
				   	+ dStorageCost + "," + dStorageCost + "," + dHKDCost + ")";

				   hmCostDetail.put(sKey, String.valueOf(dBal) + "#" + String.valueOf(dPortBal));
			   }
			   dbl.executeSql(sqlStr);
		   }
		   sqlStr = "update " + pub.yssGetTableName("Tb_Data_Cost") + " t set t.FCuryRate = ("
		   	+ " select distinct FBaseRate/FPortRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select FPortCode,FCuryCode,max(FValDate) as FValDate from "
		   	+ pub.yssGetTableName("tb_data_valrate") + " where FValDate < " + dbl.sqlDate(dEndDate) + " group by FPortCode, FCuryCode) rate2 "
		   	+ " on rate1.FPortCode = rate2.FPortCode and rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = t.FPortCode and rate1.FCuryCode = t.FCuryCode "
		   	+ " ) where FPortCode = " + dbl.sqlString(sPortCode) + " and FTransDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate);
		   dbl.executeSql(sqlStr);
		   sqlStr = "update " + pub.yssGetTableName("Tb_Data_Cost") + " t set t.FCuryRate = ("
		   	+ " select distinct FBaseRate/FPortRate from " + pub.yssGetTableName("tb_data_valrate") + " rate where rate.FPortCode = t.FPortCode and rate.FCuryCode = t.FCuryCode and rate.FValDate = " + dbl.sqlDate(dEndDate)
		   	+ " ) where FPortCode = " + dbl.sqlString(sPortCode) + " and FTransDate = " + dbl.sqlDate(dEndDate);
		   dbl.executeSql(sqlStr);
	   } catch (Exception ex) {
		   throw new YssException("获取定期存款成本资金调拨信息出错", ex);
	   } finally {
		   dbl.closeResultSetFinal(rs);
	   }
	   return hmCostDetail;
   }
}
