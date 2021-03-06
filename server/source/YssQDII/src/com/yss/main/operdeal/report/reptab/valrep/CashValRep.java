package com.yss.main.operdeal.report.reptab.valrep;

import com.yss.util.*;
import java.util.*;
import java.sql.*;
import com.yss.main.operdeal.report.reptab.valrep.pojo.ValRepBean;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CashValRep extends BaseValRep{
   public CashValRep() {
   }
   private ArrayList alCash=null;

public ArrayList getValRepData() throws YssException {
     try{
        alCash = new ArrayList();
        getCurrentCash();
     }catch(Exception ex){
        throw new YssException(ex.toString());
     }
      return alCash;
   }
   //合并邵宏伟修改报表代码 xuqiji 20100608
   private void getCurrentCash() throws YssException{
      String sqlStr="";
      ResultSet rs =null;
      try{
         //取出活期所有的明细与汇总项
    	  //modify by ctq 2010-1-18
    	  sqlStr = "select * from ("
    		  + " select b.FAccType || '##' || b.FSubAccType || '##' || a.FAnalysisCode2 || '##' || a.FCuryCode || '##' || a.FCashAccCode as FOrderCode,"
    		  + " a.FStorageDate as FNavDate, a.FPortCode, a.FCashAccCode, b.FCashAccName, a.FCuryCode,round(nvl(c.FBaseRate,1),5) as FBaseCuryRate,1 as FPortCuryRate, "
    		  + " sum(a.FAccBalance) as FCost,  sum(a.FAccBalance) as FMarketValue, "
    		  + " round(sum(FAccBalance) * nvl(c.FBaseRate,1) - sum(a.FPortCuryBal), 2) as FFXValue, nvl(round(round(sum(FAccBalance) * nvl(c.FBaseRate, 1), 2)/FPortMarketValue, 4), 0) as FPortMarketValueRatio from " + pub.yssGetTableName("tb_stock_cash") + " a join " + pub.yssGetTableName("tb_para_cashaccount") + " b on a.FCashAccCode = b.FCashAccCode "
    		  //查询11号报表报错，列不可以外部连接子查询，将left join tb_001_data_valrate更改为join tb_001_data_valrate
    		  + "  join " + pub.yssGetTableName("tb_data_valrate") + " c on a.FPortCode = c.FPortCode and b.FCuryCode=c.FCuryCode and c.FValDate=(select max(d.FValDate) from " + pub.yssGetTableName("tb_data_valrate") + " d where d.FCuryCode=c.FCuryCode and d.FValDate<=a.FStorageDate) left join (select FDate as FNavDate, FPortCode, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") e on a.FStorageDate = e.FNavDate and a.FPortCode = e.FPortCode "
    		  + " where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FStorageDate = " + dbl.sqlDate(dEndDate) + " and a.FAccBalance <> 0 and b.FSubAccType in ('0101', '0105') group by a.FPortCode, a.FStorageDate, b.FAccType, b.FSubAccType, a.FAnalysisCode2, a.FCuryCode, a.FCashAccCode, b.FCashAccName, c.FBaseRate, FPortMarketValue "
    		  + " union "
    		  + " select FAccType || '##' || FSubAccType || '##' || FAssetType || '##' || FCuryCode || '##total' as FOrderCode, FNavDate, FPortCode, '' FCashAccCode, '港幣等值金額：' as FCashAccName, FCuryCode, FBaseCuryRate, FPortCuryRate, sum(FCost) as FCost, sum(FMarketValue) as FMarketValue, "
    		  + " sum(FFXValue) as FFXValue, sum(FPortMarketValueRatio) as FPortMarketValueRatio from (select b.FAccType, b.FSubAccType, a.FAnalysisCode2 as FAssetType, a.FStorageDate as FNavDate, a.FPortCode, a.FCashAccCode, a.FCuryCode, round(nvl(c.FBaseRate,1),5) as FBaseCuryRate, 1 as FPortCuryRate,sum(a.FPortCuryBal) as FCost, round(sum(a.FAccBalance)*nvl(c.FBaseRate,1),2) as FMarketValue, "
    		  + " round(sum(FAccBalance) * nvl(c.FBaseRate,1) - sum(a.FPortCuryBal), 2) as FFXValue, nvl(round(round(sum(FAccBalance) * nvl(c.FBaseRate, 1), 2)/FPortMarketValue, 4), 0) as FPortMarketValueRatio from " + pub.yssGetTableName("tb_stock_cash") + " a join " + pub.yssGetTableName("tb_para_cashaccount") + " b on a.FCashAccCode = b.FCashAccCode "
    		//查询11号报表报错，列不可以外部连接子查询，将left join tb_001_data_valrate更改为join tb_001_data_valrate
    		  + "  join " + pub.yssGetTableName("tb_data_valrate") + " c on a.FPortCode = c.FPortCode and b.FCuryCode=c.FCuryCode and c.FValDate=(select max(d.FValDate) from " + pub.yssGetTableName("tb_data_valrate") + " d where d.FCuryCode=c.FCuryCode and d.FValDate<=a.FStorageDate) left join (select FDate as FNavDate, FPortCode, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") e on a.FStorageDate = e.FNavDate and a.FPortCode = e.FPortCode "
    		  + " where a.FPortCode = " + dbl.sqlString(sPortCode) + " and a.FStorageDate = " + dbl.sqlDate(dEndDate) + " and a.FAccBalance <> 0 and b.FSubAccType in ('0101', '0105') group by a.FPortCode, a.FStorageDate, b.FAccType, b.FSubAccType, a.FAnalysisCode2, a.FCuryCode, a.FCashAccCode, b.FCashAccName, c.FBaseRate, FPortMarketValue "
    		  + " ) group by FPortCode, FNavDate, FAccType, FSubAccType, FAssetType, FCuryCode, FBaseCuryRate, FPortCuryRate "
    		  + " ) order by FCuryCode, FCashAccCode ";
         rs =dbl.openResultSet(sqlStr);
         while(rs.next()){
            valBean =new ValRepBean();
            valBean.setInsStartDate(rs.getDate("FNavDate"));
            valBean.setSecurityCode(rs.getString("FCashAccCode"));
            valBean.setSecurityName(rs.getString("FCashAccName"));
            valBean.setCuryCode(rs.getString("FCuryCode"));
            valBean.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
            valBean.setPortCuryRate(rs.getDouble("FPortCuryRate"));
            valBean.setVstorageCost(rs.getDouble("FCost"));              //成本
            valBean.setMvalue(rs.getDouble("FMarketValue"));        //市值
            valBean.setSyvBaseCuryBal(rs.getDouble("FFxValue"));     //汇兑损益 = 市值-成本
            valBean.setFundAllotProportion(rs.getDouble("FPortMarketValueRatio"));
            valBean.setOrder(rs.getString("FOrderCode"));
            alCash.add(valBean);
         }
      }catch(Exception ex){
         throw new YssException("获取活期存款利息出错",ex);
      }finally{
         dbl.closeResultSetFinal(rs);
      }
   }
}
