package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * add by songjie 2014.05.10
 * STORY #15961 需求上海-[交银施罗德]QDIIV4.0[紧急]20140401001
 * 预警指标：“本币现金占总净值比例” 对应的逻辑代码
 * @author 宋洁
 *
 */
public class CheckCashStockScaleOfNav extends BaseValCheck {
	public CheckCashStockScaleOfNav(){
	}
	
	public String doCheck(Date curDate, String portCode) throws Exception {
		try{
			checkCashStockScaleOfNav(curDate, portCode);
		}catch(Exception e){
			throw new YssException("本币现金占总净值比例小于等于阀值", e);
		}
		
		return "";
	}
	
	private void checkCashStockScaleOfNav(Date curDate, String portCode)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		java.util.Date suspendedDate = null;//停牌起始日
		String securityCode = "";//证券代码
		String securityName = "";//证券名称
		double navValue = 0;//今日资产净值
		double scale = 0;//今日市值占昨日资产净值比
		double cashStockM = 0;//估值日本位币现金库存合计值
		String showInfo = "";//明细预警提示信息
		try{
        	if (this.sPluginValue != null && 
        		!this.sPluginValue.equalsIgnoreCase("null") && 
        		!this.sPluginValue.trim().equals("")){
        		scale = Double.parseDouble(this.sPluginValue);
        	}
			
			//获取估值日、相关组合的 资产净值
			sb.append(" select FPortMarketValue from ").append(pub.yssGetTableName("Tb_Data_NavData"))
			.append(" where FNavDate = ").append(dbl.sqlDate(curDate))
			.append(" and FPortCode = ").append(dbl.sqlString(portCode))
			.append(" and FReTypeCode = 'Total' and FKeyCode = 'TotalValue' ");
			
			rs = dbl.openResultSet(sb.toString());
			if(rs.next()){
				navValue = rs.getDouble("FPortMarketValue");
			}
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);
			
			//获取估值日、相关组合现金库存的本位币成本金额合计
			sb.append(" select sum(FPortCuryBal) as FPortCuryBal from ")
			.append(pub.yssGetTableName("Tb_Stock_Cash"))
			.append(" where FStorageDate = ").append(dbl.sqlDate(curDate))
			.append(" and FPortCode = ").append(dbl.sqlString(portCode));
			rs = dbl.openResultSet(sb.toString());
			if(rs.next()){
				cashStockM = rs.getDouble("FPortCuryBal");
			}
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);
			
			if(YssD.div(cashStockM, navValue) <= YssD.div(scale,100)){
				showInfo = YssFun.formatDate(curDate,"yyyy-MM-dd") + "本位币账户金额不足净值" + scale + "%";
			}
			
			if(showInfo.trim().length() > 0){
				this.sIsError = "true";	
				this.checkInfos = showInfo;
			}
			else 
			{
				this.sIsError = "false";	
				this.checkInfos = "正常";
			}
			
			dbl.closeResultSetFinal(rs);
			sb.setLength(0);

			printInfo(this.checkInfos);
		}catch(Exception e){
			throw new YssException("执行预警指标：“本币现金占总净值比例” 出错", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
    //界面输出提示信息
    private void printInfo(String sInfo) throws Exception 
    {
        runStatus.appendValCheckRunDesc(sInfo);
        
        if (this.sNeedLog.equals("true"))
        {
        	this.writeLog(sInfo);
        }
    }
}
