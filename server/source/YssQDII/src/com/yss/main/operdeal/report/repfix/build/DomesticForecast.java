package com.yss.main.operdeal.report.repfix.build;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssUtil;

/**shashijie 2012-01-31 STORY 3343 */
public class DomesticForecast extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String FStartDate = "";//起始日期
    private String FPortCode = "";//组合代码(多选)
    private String FHolidaysCode = "";//币种代码
    
    public DomesticForecast() {
    }

    /**程序入口 shashijie 2013-4-10 STORY 3343*/
    public String buildReport(String sType) throws YssException {
        String sResult = "";
    	
		//处理运算数据()
        ArrayList list = getDoProcess();
        //获取显示数据内容
        sResult += getInfo(list);
        
        return sResult;
    }

	/**shashijie 2013-4-10 STORY 3343 获取报表内容*/
	private String getInfo(ArrayList list) throws YssException {
		String str = "";
		if (list == null || list.isEmpty()) {
			return str;
		}
		try {
			//处理数据,将列转换成行
			HashMap map = doRowToCol(list);
			//循环行
			for (int i = 0; i < map.size(); i++) {
				String strSture = "";//每行拼接数据
				ArrayList showList = (ArrayList)map.get(i+"");
				//循环列
				for (int j = 0; j < showList.size(); j++) {
					//拼接每行数据
					if (showList.get(j) instanceof String) {
						strSture += (String)showList.get(j) + "\t";
					} else if (showList.get(j) instanceof BigDecimal) {
						strSture += ((BigDecimal)showList.get(j)).toString() + "\t";
					}
				}
				//不带格式拼接
				//str += "\r\n";
				//带格式拼接
				str += buildRowCompResult(strSture,"GuoNeiTouCun")+"\r\n";
			}
		} catch (Exception e) {
			throw new YssException("获取报表内容出错!",e);
		} finally {
			
		}
		return str;
	}

	/**shashijie 2013-4-16 STORY 3343 列转行,集合控制处理*/
	private HashMap doRowToCol(ArrayList list) throws YssException {
		HashMap map = new HashMap();
		if (list==null || list.isEmpty()) {
			return map;
		}
		//一共34行数据
		//存入第一行数据
		ArrayList col = getTitle("日期:");
		map.put("0", col);
		
		//获取第2行数据
		ArrayList col1 = getListForData(list,"项    目");
		map.put("1", col1);
		
		//获取第3行数据...
		ArrayList col2 = getListColData(list,"一、可用资金","");
		map.put("2", col2);
		
		//银行存款
		ArrayList col3 = getListColData(list,"银行存款","YHCK");
		map.put("3", col3);
		
		//交易所清算备付金
		ArrayList col4 = getListColData(list,"交易所清算备付金","交易所清算备付金");
		map.put("4", col4);
		
		//未清算的金额累计
		ArrayList col5 = getListColData(list,"未清算的金额累计","未清算的金额累计");
		map.put("5", col5);
		
		//可用资金(小计)
		ArrayList col6 = getListColData(list,"可用资金(小计)","KYZJ");
		map.put("6", col6);
		
		//二、资金流入
		ArrayList col7 = getListColData(list,"二、资金流入","");
		map.put("7", col7);
		
		//申购证券资金返回
		ArrayList col8 = getListColData(list,"申购证券资金返回","申购证券资金返回");
		map.put("8", col8);
		
		//申购流入金额key
		ArrayList col9 = getListColData(list,"申购流入金额","SGLRJE");
		map.put("9", col9);
		
		//转换转入金额
		ArrayList col10 = getListColData(list,"转换转入金额","ZHZRJE");
		map.put("10", col10);
		
		//银行间回购融券到期还款
		ArrayList col11 = getListColData(list,"银行间回购融券到期还款","RQDQHK");
		map.put("11", col11);
		
		//银行间回购T+1日融资
		ArrayList col12 = getListColData(list,"银行间回购T+1日融资","HGRZ");
		map.put("12", col12);
		
		//银行间债券卖出
		ArrayList col13 = getListColData(list,"银行间债券卖出","银行间债券卖出");
		map.put("13", col13);
		
		//交易所股票派息
		ArrayList col14 = getListColData(list,"交易所股票派息","交易所股票派息");
		map.put("14", col14);
		
		//债券派息
		ArrayList col15 = getListColData(list,"债券派息","债券派息");
		map.put("15", col15);
		
		//交易所证券清算款
		ArrayList col16 = getListColData(list,"交易所证券清算款","ZQQSK");
		map.put("16", col16);
		
		//调入资金
		ArrayList col17 = getListColData(list,"调入资金","调入资金");
		map.put("17", col17);
		
		//资金流入(小计)
		ArrayList col18 = getListColData(list,"资金流入(小计)","ZJLR");
		map.put("18", col18);
		
		//三、资金流出
		ArrayList col19 = getListColData(list,"三、资金流出","");
		map.put("19", col19);
		
		//赎回流出金额
		ArrayList col20 = getListColData(list,"赎回流出金额","SSLR");
		map.put("20", col20);
		
		//转换转出金额
		ArrayList col21 = getListColData(list,"转换转出金额","ZHZCJE");
		map.put("21", col21);
		
		//现金分红金额
		ArrayList col22 = getListColData(list,"现金分红金额","FHJE");
		map.put("22", col22);
		
		//银行同业回购融资到期还款
		ArrayList col23 = getListColData(list,"银行同业回购融资到期还款","RZDQHK");
		map.put("23", col23);
		
		//银行间回购T+1日融券
		ArrayList col24 = getListColData(list,"银行间回购T+1日融券","HGRQ");
		map.put("24", col24);
		
		//银行间债券买入
		ArrayList col25 = getListColData(list,"银行间债券买入","银行间债券买入");
		map.put("25", col25);
		
		//支付其他费用
		ArrayList col26 = getListColData(list,"支付其他费用","支付其他费用");
		map.put("26", col26);
		
		//归还借入资金
		ArrayList col27 = getListColData(list,"归还借入资金","归还借入资金");
		map.put("27", col27);
		
		//换汇资金流出
		ArrayList col28 = getListColData(list,"换汇资金流出","换汇资金流出");
		map.put("28", col28);
		
		//支付管理费托管费(支付两费)
		ArrayList col29 = getListColData(list,"支付管理费托管费","ZFLF");
		map.put("29", col29);
		
		//调整最低备付金
		ArrayList col30 = getListColData(list,"调整最低备付金","调整最低备付金");
		map.put("30", col30);
		
		//资金流出(小计)
		ArrayList col31 = getListColData(list,"资金流出(小计)","ZJLC");
		map.put("31", col31);
		
		//资金流量净额 = “资金流入小计”-“资金流出小计”
		ArrayList col32 = getListColData(list,"四、资金流量净额","ZJLL");
		map.put("32", col32);
		
		//资金余缺(T+0现有可用资金) = “在帐可用资金小计”+“资金流量净额”
		ArrayList col33 = getListColData(list,"五、资金余缺(T+0现有可用资金)","ZJYQ");
		map.put("33", col33);
		
		return map;
	}

	/**shashijie 2013-4-16 STORY 3343 根据key添加到行集合中*/
	private ArrayList getListColData(ArrayList dList, String title,
			String keyValue) throws YssException {
		ArrayList list = new ArrayList();
		list.add(title);
		if (dList == null || dList.isEmpty()) {
			return list;
		}
		//若没有KEY则直接插入空字符串
		if (YssUtil.isNullOrEmpty(keyValue)) {
			for (int i = 0; i < 7; i++) {
				list.add(" ");
			}
		} else {
			for (int i = 0; i < dList.size(); i++) {
				HashMap map = (HashMap)dList.get(i);
				//当前处理日期
				Date pDate = YssFun.addDay(YssFun.toDate(this.FStartDate), i);
				//只显示工作日的值
				if (isWorkDate(pDate)) {
					String key = getKeyForMap(pDate, keyValue);
					//若存在则保留2为小数显示,否则存入0.00
					if (map.containsKey(key)) {
						BigDecimal big = (BigDecimal)map.get(key);
						list.add(big.setScale(2,BigDecimal.ROUND_HALF_UP));
					} else {
						list.add("0.00");
					}
				}
			}
		}
		return list;
	}

	/**shashijie 2013-4-16 STORY 3343 获取第一行*/
	private ArrayList getTitle(String title) throws YssException {
		ArrayList list = new ArrayList();
		String date = YssFun.formatDate(this.FStartDate,"yyyy年MM月dd日");
		list.add(title+date);
		for (int i = 0; i < 7; i++) {
			list.add(" ");
		}
		return list;
	}

	/**shashijie 2013-4-16 STORY 3343 获取项目行(第二行)数据*/
	private ArrayList getListForData(ArrayList dList,String title) throws YssException {
		ArrayList list = new ArrayList();
		if (dList==null || dList.isEmpty()) {
			return list;
		}
		//列头
		list.add(title);
		for (int i = 0; i < dList.size(); i++) {
			//当前处理日期
			Date pDate = YssFun.addDay(YssFun.toDate(this.FStartDate), i);
			//是否是工作日
			if (isWorkDate(pDate)) {
				list.add(YssFun.formatDate(pDate));
			}
		}
		return list;
	}

	/**shashijie 2013-4-16 STORY 3343 判断是否是工作日,是返回true*/
	private boolean isWorkDate(Date pDate) throws YssException {
		boolean flag = false;
		if (YssFun.dateDiff(pDate,getWorkDayByWhere(this.FHolidaysCode, pDate, 0)) == 0) {
			flag = true;
		}
		return flag;
	}

	/**shashijie 2013-4-10 STORY 3343 获取报表处理数据*/
	private ArrayList getDoProcess() throws YssException {
		ArrayList list = new ArrayList();
		//获取当前日期后的第七个工作日
		Date endDate = getWorkDayByWhere(this.FHolidaysCode, YssFun.toDate(this.FStartDate), 7);
		//处理当前日期至第七个工作日之间的所有自然日的数据,保存至集合中
		int dateD = YssFun.dateDiff(YssFun.parseDate(FStartDate), endDate);
		for (int i = 0; i < dateD; i++) {
			HashMap map = getHandleMap(i);
			list.add(map);
		}
		//处理第二天开始的银行存款的值
		doSecondDeposit(list);
		return list;
	}
	
	/**shashijie 2013-4-16 STORY 3343 从第二天开始,银行存款 = 前一日'CNY'现金账户余额 + 前一日(总流入金额 - 总流出金额) */
	private void doSecondDeposit(ArrayList list) throws YssException {
		if (list == null || list.isEmpty()) {
			return;
		}
		
		BigDecimal value = new BigDecimal(0);//记录昨日的值
		//从第二天开始,银行存款 = 前一日'CNY'现金账户余额 + 前一日(总流入金额 - 总流出金额) 
		for (int i = 0; i < list.size(); i++) {
			HashMap map = (HashMap)list.get(i);
			//当前处理日期
			Date pDate = YssFun.addDay(YssFun.toDate(this.FStartDate), i);
			//保存第一天的"资金余缺"
			String moneykey = getKeyForMap(pDate, "ZJYQ");
			if (i==0 && map.containsKey(moneykey)) {
				value = (BigDecimal)map.get(moneykey);
			}
			//若是第一天则不修改银行存款的值,否则用前一日"资金余缺"的值来替代当日"银行存款"
			if (i!=0) {
				//替换"银行存款"
				String depositKey = getKeyForMap(pDate,"YHCK");
				BigDecimal depositValue = new BigDecimal(value.toString());
				map.put(depositKey, depositValue);
				//重新计算当天的"资金余缺",并保存
				//可用资金(小计)
				String canMoneyKey = getKeyForMap(pDate, "KYZJ");
				BigDecimal canMoneyValue = getSumMapValue(map,pDate,"YHCK");
				map.put(canMoneyKey, canMoneyValue);
				//资金余缺(T+0现有可用资金) = “在帐可用资金小计”+“资金流量净额”
				String survivorKey = getKeyForMap(pDate, "ZJYQ");
				BigDecimal survivorValue = getSumMapValue(map,pDate,"KYZJ,ZJLL");
				map.put(survivorKey, survivorValue);
				//保存当天的"资金余缺",带入下个循环赋值
				value = (BigDecimal)map.get(moneykey);
			}
		}
	}

	/**shashijie 2013-4-12 STORY 3343 获取每行处理数据并存入集合中*/
	private HashMap getHandleMap(int number) throws YssException {
		HashMap map = new HashMap();
		//当前处理日期
		Date pDate = YssFun.addDay(YssFun.toDate(this.FStartDate), number);
		
		//银行存款key
		String surplusKey = getKeyForMap(pDate,"YHCK");
		//暂时存储所有币种为‘CNY’的现金帐户的库存金额,后续会继续处理
		BigDecimal surplusValue = getSurplusValue(pDate);
		map.put(surplusKey, surplusValue);
		
		//可用资金(小计)
		String canMoneyKey = getKeyForMap(pDate, "KYZJ");
		BigDecimal canMoneyValue = getSumMapValue(map,pDate,"YHCK");
		map.put(canMoneyKey, canMoneyValue);
		
		//申购流入金额key
		String applyKey = getKeyForMap(pDate, "SGLRJE");
		BigDecimal applyValue = getApplyValue(pDate,"01");
		map.put(applyKey, applyValue);
		
		//转换转入金额
		String faceAboutKey = getKeyForMap(pDate, "ZHZRJE");
		BigDecimal faceAboutValue = getApplyValue(pDate,"04");
		map.put(faceAboutKey, faceAboutValue);
		
		//银行间回购融券到期还款
		String annulationKey = getKeyForMap(pDate, "RQDQHK");
		BigDecimal annulationValue = getAnnulationValue(pDate,"25,78","0");
		map.put(annulationKey, annulationValue);
		
		//银行间回购T+1日融资
		String meltMoneyKey = getKeyForMap(pDate, "HGRZ");
		BigDecimal meltMoneyValue = getAnnulationValue(pDate,"24,79","1");
		map.put(meltMoneyKey, meltMoneyValue);
		
		//交易所证券清算款
		String clearKey = getKeyForMap(pDate, "ZQQSK");
		BigDecimal clearValue = getFFactSettleMoneyValue(pDate);
		map.put(clearKey, clearValue);
		
		//资金流入(小计)
		String flowMoneyKey = getKeyForMap(pDate, "ZJLR");
		BigDecimal flowMoneyValue = getSumMapValue(map,pDate,"SGLRJE,ZHZRJE,RQDQHK,HGRZ,ZQQSK");
		map.put(flowMoneyKey, flowMoneyValue);
		
		//赎回流出
		String redemptionKey = getKeyForMap(pDate, "SSLR");
		BigDecimal redemptionValue = getApplyValue(pDate,"02");
		map.put(redemptionKey, redemptionValue);
		
		//转换转出金额
		String transmittingKey = getKeyForMap(pDate, "ZHZCJE");
		BigDecimal transmittingValue = getApplyValue(pDate,"05");
		map.put(transmittingKey, transmittingValue);
		
		//现金分红金额
		String divisionKey = getKeyForMap(pDate, "FHJE");
		BigDecimal divisionValue = getApplyValue(pDate,"03");
		map.put(divisionKey, divisionValue);
		
		//银行同业回购融资到期还款
		String meltKey = getKeyForMap(pDate, "RZDQHK");
		BigDecimal meltValue = getAnnulationValue(pDate,"24,79","0");
		map.put(meltKey, meltValue);
		
		//银行间回购T+1日融券
		String thawKey = getKeyForMap(pDate, "HGRQ");
		BigDecimal thawValue = getAnnulationValue(pDate,"25,78","1");
		map.put(thawKey, thawValue);
		
		//支付管理费托管费(支付两费)
		String feeKey = getKeyForMap(pDate, "ZFLF");
		BigDecimal feeValue = new BigDecimal(0);
		//如T+n日为每月第2个工作日，则取上月计提的管理费、托管费总和；否则为0
		if (isSecond(pDate)) {
			feeValue = getFeeValue(pDate);
		} 
		map.put(feeKey, feeValue);
		
		//资金流出(小计)
		String outMoneyKey = getKeyForMap(pDate, "ZJLC");
		BigDecimal outMoneyValue = getSumMapValue(map,pDate,"SSLR,ZHZCJE,FHJE,RZDQHK,HGRQ,ZFLF");
		map.put(outMoneyKey, outMoneyValue);
		
		//资金流量净额 = “资金流入小计”-“资金流出小计”
		String dischargeKey = getKeyForMap(pDate, "ZJLL");
		BigDecimal dischargeValue = getCountMapValue(map,pDate,"ZJLR","ZJLC");
		map.put(dischargeKey, dischargeValue);
		
		//资金余缺(T+0现有可用资金) = “在帐可用资金小计”+“资金流量净额”
		String survivorKey = getKeyForMap(pDate, "ZJYQ");
		BigDecimal survivorValue = getSumMapValue(map,pDate,"KYZJ,ZJLL");
		map.put(survivorKey, survivorValue);
		
		return map;
	}

	/**shashijie 2013-4-16 STORY 3343 根据MAP中的KEY获取值并做减法运算*/
	private BigDecimal getCountMapValue(HashMap map, Date pDate, String key1,
			String key2) {
		BigDecimal value = new BigDecimal(0);
		if (map==null || map.isEmpty()) {
			return value;
		}
		//减数
		BigDecimal sub1 = new BigDecimal(0);
		String key = getKeyForMap(pDate, key1);
		if (map.containsKey(key)) {
			sub1 = (BigDecimal)map.get(key);
		}
		//被减数
		BigDecimal sub2 = new BigDecimal(0);
		key = getKeyForMap(pDate, key2);
		if (map.containsKey(key)) {
			sub2 = (BigDecimal)map.get(key);
		}
		//返回差值
		value = sub1.subtract(sub2);
		
		return value;
	}

	/**shashijie 2013-4-16 STORY 3343 获取两费值*/
	private BigDecimal getFeeValue(Date pDate) throws YssException {
		BigDecimal value = new BigDecimal(0);
		ResultSet rs = null;
		try {
			String query = getFeeQuery(pDate);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getBigDecimal("FMoney");
			}
		} catch (Exception e) {
			throw new YssException("获取证券清算款的金额出错!" , e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2013-4-16 STORY 3343 获取两费SQL*/
	private String getFeeQuery(Date pDate) throws YssException {
		//获取月的第一天
		Date startDate = YssFun.getLastMonthAndDay(pDate,"01");
		//获取月的最后一天
		Date endDate = YssFun.getLastMonthAndDay(pDate,"");
		String sql = " Select Sum(a.FMoney) As FMoney From "+pub.yssGetTableName("Tb_Data_Investpayrec")+
			" a Where a.Fivpaycatcode In ('IV001', 'IV002')" +
			" And a.FCheckState = 1" +
			" And a.FPortCode In ("+operSql.sqlCodes(this.FPortCode)+")" +
			" And a.ftransdate Between "+dbl.sqlDate(startDate)+" And " + dbl.sqlDate(endDate)+
			" And a.FTsfTypeCode In ('03') ";
		return sql;
	}

	/**shashijie 2013-4-16 STORY 3343 判断是否是本月的第二个工作日,若是返回true*/
	private boolean isSecond(Date pDate) throws YssException {
		boolean flag = false;
        //获取上个月的最后一天,考虑跨年问题
		Date dDate = YssFun.getLastMonthAndDay(pDate,"");

        //本月的第2个工作日
        Date secondDate = getWorkDayByWhere(this.FHolidaysCode, dDate, 2);
        
        if (YssFun.dateDiff(secondDate, pDate) == 0) {
			flag = true;
		}
        
		return flag;
	}

	/**shashijie 2013-4-13 STORY 3343 获取map中的几项数据的合计值*/
	private BigDecimal getSumMapValue(HashMap map, Date pDate, String code) throws YssException {
		BigDecimal value = new BigDecimal(0);
		if (map==null || map.isEmpty()) {
			return value;
		}
		String[] keys = code.split(",");
		for (int i = 0; i < keys.length; i++) {
			String key = getKeyForMap(pDate, keys[i]);
			if (map.containsKey(key)) {
				BigDecimal tmp = (BigDecimal)map.get(key);
				value = value.add(tmp);
			}
		}
		return value;
	}

	/**shashijie 2013-4-13 STORY 3343 证券清算款,交易数据子表*/
	private BigDecimal getFFactSettleMoneyValue(Date pDate) throws YssException {
		BigDecimal value = new BigDecimal(0);
		ResultSet rs = null;
		try {
			String query = getFFactSettleMoneyQuery(pDate);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getBigDecimal("money");
			}
		} catch (Exception e) {
			throw new YssException("获取证券清算款的金额出错!" , e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2013-4-13 STORY 3343 获取证券清算款SQL*/
	private String getFFactSettleMoneyQuery(Date pDate) {
		String sql = " Select Sum(y.Money) As Money From (Select /*卖出实际金额 - 买入实际金额*/" +
			" Nvl(Sum(Case" +
			" When a.Ftradetypecode In ('02', '24') Then" +
			" a.Ffactsettlemoney" +
			" Else" +
			" 0" +
			" End),0) - Nvl(Sum(Case" +
			" When a.Ftradetypecode In ('01', '25') Then" +
			" a.Ffactsettlemoney" +
			" Else" +
			" 0" +
			" End),0) As Money" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" a" +
			" Join (Select B1.Fsecuritycode, B1.Fexchangecode" +
			" From "+pub.yssGetTableName("Tb_Para_Security")+" B1" +
			" Where B1.Fexchangecode In ('SSE', 'CG', 'CS')) b On a.Fsecuritycode = b.Fsecuritycode" +
			" Where a.Fcheckstate = 1" +
			" /*结算日期*/ " + 
			" And a.Fsettledate = "+dbl.sqlDate(pDate)+
        
			" Union" +
        
			" Select /*逆回购 - 正回购*/" +
			" Sum(Case" +
			" When a.Ftradetypecode In ('25') Then" +
			" a.Ffactsettlemoney + a.Faccruedinterest" +
			" Else" +
			" 0" +
			" End) - Sum(Case" +
			" When a.Ftradetypecode In ('24') Then" +
			" a.Ffactsettlemoney + a.Faccruedinterest" +
			" Else" +
			" 0" +
			" End) As Money" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" a" +
			" Join (Select B1.Fsecuritycode, B1.Fexchangecode" +
			" From "+pub.yssGetTableName("Tb_Para_Security")+" B1" +
			" Where B1.Fexchangecode In ('SSE', 'CG', 'CS')) b On a.Fsecuritycode = b.Fsecuritycode" +
			" Where a.Fcheckstate = 1" +
			" /*到期结算日期*/ " + 
			" And a.FMatureSettleDate = "+dbl.sqlDate(pDate)+" ) y";
		return sql;
	}

	/**shashijie 2013-4-13 STORY 3343 逆回购*/
	private BigDecimal getAnnulationValue(Date pDate, String FTradeTypeCode,String id) throws YssException {
		BigDecimal value = new BigDecimal(0);
		ResultSet rs = null;
		try {
			String query = getAnnulationQuery(pDate,FTradeTypeCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				//“交易金额”+“回购收益”
				if (id.equals("0")) {
					value = rs.getBigDecimal("Ftrademoney");
				} else if(id.equals("1")) {//“实收实付金额”
					value = rs.getBigDecimal("Ftotalcost");
				}
			}
		} catch (Exception e) {
			throw new YssException("获取银行间回购数据类型为"+FTradeTypeCode+"的金额出错!" , e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2013-4-13 STORY 3343 回购业务SQL*/
	private String getAnnulationQuery(Date pDate, String fTradeTypeCode) {
		String sql = " Select Nvl(Sum(Ftrademoney + Fpurchasegain), 0) As Ftrademoney," +
			" Nvl(Sum(Ftotalcost), 0) As Ftotalcost From "+pub.yssGetTableName("Tb_Data_Purchase")+" a " +
			" Where a.Fsettledate = "+dbl.sqlDate(pDate)+
			" And a.Ftradetypecode In ("+operSql.sqlCodes(fTradeTypeCode)+") And a.Fcheckstate = 1" +
			" Order By a.Fsettledate Desc";
 		return sql;	
	}

	/**shashijie 2013-4-12 STORY 3344 根据销售类型获取TA交易结算金额*/
	private BigDecimal getApplyValue(Date pDate,String Fselltype) throws YssException {
		BigDecimal value = new BigDecimal(0);
		ResultSet rs = null;
		try {
			String query = getTaTradeQuery(pDate,Fselltype);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getBigDecimal("Fsettlemoney");
			}
		} catch (Exception e) {
			throw new YssException("获取TA交易数据交易类型"+Fselltype+"的结算金额出错!" , e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2013-4-12 STORY 获取TA交易数据中"结算金额"的值SQL*/
	private String getTaTradeQuery(Date pDate, String fselltype) {
		String sql = " Select /*若实际结算金额为空则取销售金额*/ Nvl(Sum(Case When y.Fsettlemoney = 0 Then " +
			" /*结算金额 = 销售金额 +- 费用 */ y.Fsellmoney - ((y.Ftradefee1 + y.Ftradefee2 + y.Ftradefee3 + y.Ftradefee4 +" +
			" y.Ftradefee5 + y.Ftradefee6 + y.Ftradefee7 + y.Ftradefee8) * y.Fcashind) Else y.Fsettlemoney " +
			" End),0) As Fsettlemoney From (Select a.Fsellmoney, Case When C1.Fissettle = 1 Then a.Ftradefee1" +
			" Else 0 End As Ftradefee1, Case When C2.Fissettle = 1 Then a.Ftradefee2 Else 0 End As Ftradefee2," +
			" Case When C3.Fissettle = 1 Then a.Ftradefee3 Else 0 End As Ftradefee3, Case When C4.Fissettle = 1 Then" +
			" a.Ftradefee4 Else 0 End As Ftradefee4, Case When C5.Fissettle = 1 Then a.Ftradefee5 Else 0" +
			" End As Ftradefee5, Case When C6.Fissettle = 1 Then a.Ftradefee6 Else 0 End As Ftradefee6," +
			" Case When C7.Fissettle = 1 Then a.Ftradefee7 Else 0 End As Ftradefee7, Case When C8.Fissettle = 1 Then" +
			" a.Ftradefee8 Else 0 End As Ftradefee8, b.Fcashind, Nvl(a.Fsettlemoney, 0) As Fsettlemoney" +
			" From "+pub.yssGetTableName("Tb_Ta_Trade")+" a /*资金流入流出方向,以次判断加还是减费用*/" +
			" Join (Select B1.Fselltypecode, B1.Fcashind From "+pub.yssGetTableName("Tb_Ta_Selltype")+" B1" +
			" Where B1.Fcheckstate = 1) b On a.Fselltype = b.Fselltypecode /*费用设置中必须是清算金额才可以加减费用*/";
		//拼接所有费用sql
		for (int i = 1; i <= 8; i++) {
			sql += " Left Join (Select Ffeecode, Ffeename, Fissettle" +
				" From "+pub.yssGetTableName("Tb_Para_Fee")+
                " Where Fcheckstate = 1) C"+i+" On a.Ffeecode"+i+" = C"+i+".Ffeecode ";
		}
		sql += " Where a.Fsettledate = "+dbl.sqlDate(pDate)+
			" And a.Fselltype = "+dbl.sqlString(fselltype)+ 
			" And a.Fcheckstate = 1" +
			" Order By a.Fsettledate Desc) y ";
		return sql;
	}

	/**shashijie 2013-4-12 STORY 3343 获取T日现金库存中,所有币种为‘CNY’的现金帐户的库存金额*/
	private BigDecimal getSurplusValue(Date pDate) throws YssException {
		ResultSet rs = null;
		BigDecimal Faccbalance = new BigDecimal(0);
		try {
			String query = getStockCashQuery(pDate);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				Faccbalance = rs.getBigDecimal("Faccbalance");
			}
		} catch (Exception e) {
			throw new YssException("获取T日现金库存中，所有币种为‘CNY’的现金帐户的库存金额出错!" , e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return Faccbalance;
	}

	/**shashijie 2013-4-12 STORY 3343 获取SQL*/
	private String getStockCashQuery(Date pDate) {
		String SQL = " Select Sum(a.Faccbalance) As Faccbalance" +
			" From "+pub.yssGetTableName("Tb_Stock_Cash")+" a" +
			" Where a.Fstoragedate = "+dbl.sqlDate(pDate)+
		    " And a.Fcurycode = 'CNY' And a.FCheckState = 1 Order By a.Fstoragedate Desc ";
		return SQL;
	}

	/**shashijie 2013-4-12 STORY 3343 获取key值*/
	private String getKeyForMap(Date pDate, String value) {
		String key = "";
		key = YssFun.formatDate(pDate) + value;
		return key;
	}

	/**shashijie 2013-4-12 STORY 3343 获取工作日方法 */
	private Date getWorkDayByWhere(String sHolidayCode, Date dDate, int dayInt)
			throws YssException {
		Date mDate = null;// 工作日
		// 公共获取工作日类
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		mDate = operDeal.getWorkDay(sHolidayCode, dDate, dayInt);
		return mDate;
	}

	/**shashijie 2013-4-10 STORY 3343 把内容拼接上格式 */
	private String buildRowCompResult(String str,String FRelaCode) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            //获取格式
            hmCellStyle = getCellStyles(FRelaCode);
            for (int i = 0; i < sArry.length; i++) {
                sKey = FRelaCode + "\tDSF\t-1\t" + i;
                //拼接格式
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
            //若有数据则去除最后一个\t
            if (buf.toString().trim().length() > 1) {
            	strReturn = YssFun.getSubString(buf.toString());
            }
            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            //dbl.closeResultSetFinal(rs);
        }
	}

	/**初始数据方法*/
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        FStartDate = reqAry[0].split("\r")[1];//起始日期
        FPortCode = reqAry[1].split("\r")[1];//组合
        FHolidaysCode = reqAry[2].split("\r")[1];//节假日代码
        
    }

    public String saveReport(String sReport) {
        return "";
    }

	public String getFStartDate() {
		return FStartDate;
	}

	public void setFStartDate(String fStartDate) {
		FStartDate = fStartDate;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	public String getFCuryCode() {
		return FHolidaysCode;
	}

	public void setFCuryCode(String fCuryCode) {
		FHolidaysCode = fCuryCode;
	}



}
