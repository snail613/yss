package com.yss.main.operdeal.report.reptab.valrep;

import com.yss.util.*;
import java.util.*;
import java.sql.*;
import com.yss.main.operdeal.report.reptab.valrep.pojo.ValRepBean;

/**
 *
 * <p>Title: 处理中保报表之：组合管理费报表显示问题</p>
 *
 * <p>Description: MS00549:QDV4中保2009年06月25日01_A </p>
 *
 * <p>Copyright: 20090708</p>
 *
 * <p>Company: ysstech</p>
 *
 * @author by leeyu create
 * @version 1.0
 */
public class ManagerFeeValRep
    extends BaseValRep {
    public ManagerFeeValRep() {
    }
	
    private ArrayList alFee = null;
    public ArrayList getValRepData() throws YssException {
        alFee = new ArrayList();
        getFeesVal();
        return alFee;
    }

    private void getFeesVal() throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select 'IV001'" + dbl.sqlJN() + "'##'" + dbl.sqlJN() + "a.FportCode" + dbl.sqlJN() + "'##total##'" + dbl.sqlJN() + "a.FAnalySisCode3 as FOrderCode," +
                " a.FPortCuryMoney,b.FPortCuryBal ,a.FAnalysisCode3,b.FPortCode,b.FStorageDate,c.FCatCode,c.FCatName from ( " +
                " select sum(FPortCuryMoney) as FPortCuryMoney,FAnalysisCode3,FPortCode from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " where FIvPayCatCode='IV001' and (FTransDate between " + dbl.sqlDate(dBeginDate) + " and  " + dbl.sqlDate(dEndDate) + ") and FPortCode=" + dbl.sqlString(sPortCode) + " and FAnalySisCode3 <>' ' " +
                " group by FAnalySisCode3,FPortCode ) a left join " +
                " (select sum(FPortCuryBal) as FPortCuryBal,FAnalySisCode3,FPortCode,FStorageDate from " + pub.yssGetTableName("Tb_Stock_Invest") +
                " where FIvPayCatCode='IV001' and " + operSql.sqlStoragEve(dBeginDate) + " and FPortCode=" + dbl.sqlString(sPortCode) + " and FAnalySisCode3 <>' ' " +
                " group by FAnalySisCode3,FPortCode,FStorageDate) b on a.FAnalysisCode3= b.FAnalysisCode3 and a.FPortCode=b.FPortCode " +
                " left join tb_base_category c on a.FAnalysisCode3=c.FCatCode ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                valBean = new ValRepBean();
                valBean.setSecurityCode(rs.getString("FCatCode")); //品种代码
                valBean.setSecurityName(rs.getString("FCatName")); //品种名称
                valBean.setStorageAmount(0);
                valBean.setCuryCode("HKD");
                valBean.setBaseCuryRate(0);
                valBean.setPortCuryRate(0);
                valBean.setFactRate(0);
                valBean.setInsStartDate(dBeginDate); //起始日期
                valBean.setInsEndDate(dEndDate); //结束日期
                valBean.setAvgCost(0);
                valBean.setMarketPrice(0);
                valBean.setTotalCost(0);
                valBean.setBoughtInt(0);
                valBean.setMvalue(0);
                valBean.setLXVBal(rs.getDouble("FPortCuryMoney")); //应收应付
                valBean.setBFlxBal(rs.getDouble("FPortCuryBal")); //应收应付，上期结余
                valBean.setYKVBal(0); //盈亏差价
                valBean.setSyvBaseCuryBal(0); //基金百分比
                valBean.setVstorageCost(0); //汇兑损益
                valBean.setFundAllotProportion(0); //投资比例百分比
                valBean.setOrder(rs.getString("FOrderCode"));
                alFee.add(valBean);
            }
        } catch (Exception ex) {

        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
