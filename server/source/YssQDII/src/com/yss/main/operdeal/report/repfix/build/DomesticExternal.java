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
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2012-01-31 STORY 3343 */
public class DomesticExternal extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String FStartDate = "";//起始日期
    private String FPortCode = "";//组合代码(多选)
    private String FHolidaysCode = "";//币种代码
    
    public DomesticExternal() {
    }

    /**程序入口 shashijie 2013-4-10 STORY 3343*/
    public String buildReport(String sType) throws YssException {
        String sResult = "";
    	
		//处理运算数据,List中存放每个账户N天的sList,sList中存放每天的数据Map
        ArrayList list = getDoProcess();
        //获取显示数据内容
        sResult = getInfo(list);
        
        return sResult;
    }

	/**shashijie 2013-4-18 STORY 3343 获取组合下所有非人命币的现金账户*/
	private ArrayList getFCashAccCodeList() throws YssException {
		ArrayList list = new ArrayList();
		ResultSet rs = null;//定义游标
		try {
			String query = getFCashAccCodeQuery();
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				list.add(rs.getString("FCashAccCode"));
			}
		} catch (Exception e) {
			throw new YssException("获取组合下所有非人命币的现金账户出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return list;
	}

	/**shashijie 2013-4-18 STORY 3343 获取SQL*/
	private String getFCashAccCodeQuery() {
		String sql = " Select Distinct a.Fcashacccode, a.Fcurycode, Nvl(b.Faccbalance, 0) As Faccbalance" +
			" From "+pub.yssGetTableName("Tb_Para_Cashaccount")+" a" +
			" Left Join (Select B1.Fcashacccode, B1.Faccbalance, B1.Fcurycode" +
			" From "+pub.yssGetTableName("Tb_Stock_Cash")+" B1" +
			" Where B1.Fstoragedate = "+dbl.sqlDate(this.FStartDate)+
            " And B1.Fcurycode <> 'CNY' And B1.Fcheckstate = 1) b On a.Fcashacccode = b.Fcashacccode" +
            " Where a.Fportcode In ("+operSql.sqlCodes(this.FPortCode)+")"+
            " And a.Fcheckstate = 1 And a.Fcurycode <> 'CNY'" +
            //" And rownum < 5 "+//测试用
            " Order By a.Fcurycode, a.Fcashacccode ";
 		return sql;
	}

	/**shashijie 2013-4-10 STORY 3343 获取报表内容*/
	private String getInfo(ArrayList list) throws YssException {
		String str = "";
		if (list == null || list.isEmpty()) {
			return str;
		}
		try {
			//获取组合下所有非人命币的现金账户
	        ArrayList fCashAccCodeList = getFCashAccCodeList();
			//循环天数
			for (int i = 0; i < list.size(); i++) {
				//当前处理日期
				Date pDate = YssFun.addDay(YssFun.toDate(this.FStartDate), i);
				//若非工作日不处理
				if (!isWorkDate(pDate)) {
					continue;
				}
				//每天列头
				String col = "日期:"+YssFun.formatDate(pDate)+"\t \t0.00\t0.00\t0.00\t0.00\t0.00\t0.00\t0.00\t";
				str += buildRowCompResult(col, "GuoWaiTouCun")+"\r\n";
				//循环每个现金账户
				for (int j = 0; j < fCashAccCodeList.size(); j++) {
					String fCashAccCode = (String)fCashAccCodeList.get(j);
					//获取每个现金账户的数据
					col = getColValue(pDate,fCashAccCode,(ArrayList)list.get(i));
					//带格式拼接
					str += buildRowCompResult(col,"GuoWaiTouCun")+"\r\n";
				}
			}
		} catch (Exception e) {
			throw new YssException("获取报表内容出错!",e);
		} finally {
			
		}
		return str;
	}

	/**shashijie 2013-4-18 STORY 3343 获取每个现金账户的数据*/
	private String getColValue(Date pDate, String fCashAccCode,ArrayList list) throws YssException {
		String str = "";
		if (list == null || list.isEmpty()) {
			return str;
		}
		//获取现金账户对象
		CashAccountBean cash = getCashAccountBean(fCashAccCode);
		
		//循环每天的数据(每行数据)
		for (int i = 0; i < list.size(); i++) {
			HashMap map = (HashMap)list.get(i);
			//检测该现金账户在第几次循环中
			String key = getKeyForMap(pDate, "ZMYE", fCashAccCode);
			if (!map.containsKey(key)) {
				continue;
			}
			//获取每个单元格的值
			str = getMapValue(map,fCashAccCode,pDate,cash.getStrCurrencyCode(),cash.getStrCashAcctName());
		}
		return str;
	}

	/**shashijie 2013-4-19 STORY 3343 获取现金账户对象 */
	private CashAccountBean getCashAccountBean(String fCashAccCode) throws YssException {
		CashAccountBean cash = new CashAccountBean();
		cash.setYssPub(pub);
		cash.setStrCashAcctCode(fCashAccCode);
		cash.getSetting();
		return cash;
	}

	/**shashijie 2013-4-18 STORY 3343 获取每行每个单元格数据*/
	private String getMapValue(HashMap map, String fCashAccCode, Date pDate,
			String fCuryCode, String fCashAccName) throws YssException {
		String str = "";
		//币种,账户名称
		str += fCuryCode + "\t" + fCashAccName + "\t";
		
		//账面余额key
		String key = getKeyForMap(pDate,"ZMYE",fCashAccCode);
		str += getListColData(map, key);
		
		//交易_待进款
		key = getKeyForMap(pDate, "JYDJK",fCashAccCode);
		str += getListColData(map, key);
		
		//交易_待出款
		key = getKeyForMap(pDate, "JYDCK",fCashAccCode);
		str += getListColData(map, key);
		
		//换汇_待进款
		key = getKeyForMap(pDate, "HHDJK",fCashAccCode);
		str += getListColData(map, key);
		
		//换汇_待出款
		key = getKeyForMap(pDate, "HHDCK",fCashAccCode);
		str += getListColData(map, key);
		
		//现金分红
		key = getKeyForMap(pDate, "XJFH",fCashAccCode);
		str += getListColData(map, key);
		
		//预测数 = 账面余额+交易待进款-交易待出款+换汇待进款-换汇待出款+现金分红
		key = getKeyForMap(pDate, "YCS",fCashAccCode);
		str += getListColData(map, key);
		
		return str;
	}

	/**shashijie 2013-4-16 STORY 3343 根据key添加到行集合中*/
	private String getListColData(HashMap dmap, String key) throws YssException {
		String str = "";
		
		if (dmap == null || dmap.isEmpty()) {
			return str;
		}
		//若存在则保留2为小数显示,否则存入0.00
		if (dmap.containsKey(key)) {
			//拼接每行数据
			if (dmap.get(key) instanceof String) {
				str = (String)dmap.get(key) + "\t";
			} else if (dmap.get(key) instanceof BigDecimal) {
				str = ((BigDecimal)dmap.get(key)).setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
			}
		} else {
			str = "0.00\t";
		}
		return str;
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
		//获取组合下所有非人命币的现金账户
        ArrayList fCashAccCodeList = getFCashAccCodeList();
        //循环天数
		for (int i = 0; i < dateD; i++) {
			//当前处理日期
			Date pDate = YssFun.addDay(YssFun.toDate(this.FStartDate), i);
			//临时集合存储每天N个账户的数据
			ArrayList templist = new ArrayList();
			
			//循环所有现金账户
			for (int j = 0; j < fCashAccCodeList.size(); j++) {
				String fCashAccCode = (String)fCashAccCodeList.get(j);
				HashMap map = getHandleMap(pDate,fCashAccCode);
				templist.add(map);
			}
			list.add(templist);
		}
		//处理第二天开始的账面余额的值
		doSecondDeposit(list,fCashAccCodeList);
		
		return list;
	}
	
	/**shashijie 2013-4-16 STORY 3343 从第二天开始,账面余额 = 前一日预测数 */
	private void doSecondDeposit(ArrayList list,ArrayList fCashAccCodeList) throws YssException {
		if (list == null || list.isEmpty()) {
			return;
		}
		
		HashMap yesterday = new HashMap();//记录昨日的值
		//循环日期;账面余额 = 前一日预测数
		for (int i = 0; i < list.size(); i++) {
			ArrayList dList = (ArrayList)list.get(i);
			//当前处理日期
			Date pDate = YssFun.addDay(YssFun.toDate(this.FStartDate), i);
			//循环现金账户
			for (int j = 0; j < fCashAccCodeList.size(); j++) {
				String fCashAccCode = (String)fCashAccCodeList.get(j);//现金账户
				//当天预测数key
				String moneykey = getKeyForMap(pDate, "YCS",fCashAccCode);
				//昨日"预测数"
				String yestKey = getKeyForMap(YssFun.addDay(pDate,-1), "YCS",fCashAccCode);
				//当天"账面余额"
				String depositKey = getKeyForMap(pDate,"ZMYE",fCashAccCode);
				
				//每天每个现金账户的值
				for (int k = 0; k < dList.size(); k++) {
					HashMap map = (HashMap)dList.get(k);
					//保存第一天的"预测数"
					if (i==0 && map.containsKey(moneykey)) {
						yesterday.put(moneykey, (BigDecimal)map.get(moneykey));
						break;
					}
					//若是第一天则不修改账面余额的值,否则用前一日"资金余缺"的值来替代当日"账面余额"
					if (i!=0 && yesterday.containsKey(yestKey) && map.containsKey(depositKey)
							&& map.containsKey(moneykey)) {
						//获取昨日"预测数"
						BigDecimal value = (BigDecimal)yesterday.get(yestKey);
						BigDecimal depositValue = new BigDecimal(value.toString());
						//替换当天"账面余额"
						map.put(depositKey, depositValue);
						//重新计算当天"预测数",并保存
						String flowMoneyKey = getKeyForMap(pDate, "YCS",fCashAccCode);
						BigDecimal flowMoneyValue = getSumMapValue(map,pDate,fCashAccCode);
						map.put(flowMoneyKey, flowMoneyValue);
						//保存当天的"预测数",带入下个循环赋值
						BigDecimal temp = (BigDecimal)map.get(moneykey);
						yesterday.put(moneykey, temp);
						break;
					}
				}
			}
		}
	}

	/**shashijie 2013-4-12 STORY 3343 获取每行处理数据并存入集合中*/
	private HashMap getHandleMap(Date pDate,String fCashAccCode) throws YssException {
		HashMap map = new HashMap();
		
		//账面余额key
		String surplusKey = getKeyForMap(pDate,"ZMYE",fCashAccCode);
		//暂时存储T日现金库存中，现金帐户的库存金额
		BigDecimal surplusValue = getSurplusValue(pDate,fCashAccCode);
		map.put(surplusKey, surplusValue);
		
		//交易_待进款
		String canMoneyKey = getKeyForMap(pDate, "JYDJK",fCashAccCode);
		BigDecimal canMoneyValue = getCanMoneyValue(pDate,fCashAccCode,"02");
		map.put(canMoneyKey, canMoneyValue);
		
		//交易_待出款
		String applyKey = getKeyForMap(pDate, "JYDCK",fCashAccCode);
		BigDecimal applyValue = getCanMoneyValue(pDate,fCashAccCode,"01");
		map.put(applyKey, applyValue);
		
		//换汇_待进款
		String faceAboutKey = getKeyForMap(pDate, "HHDJK",fCashAccCode);
		BigDecimal faceAboutValue = getApplyValue(pDate,fCashAccCode,"0");
		map.put(faceAboutKey, faceAboutValue);
		
		//换汇_待出款
		String annulationKey = getKeyForMap(pDate, "HHDCK",fCashAccCode);
		BigDecimal annulationValue = getApplyValue(pDate,fCashAccCode,"1");
		map.put(annulationKey, annulationValue);
		
		//现金分红
		String meltMoneyKey = getKeyForMap(pDate, "XJFH",fCashAccCode);
		BigDecimal meltMoneyValue = getCanMoneyValue(pDate,fCashAccCode,"06");
		map.put(meltMoneyKey, meltMoneyValue);
		
		//预测数 = 账面余额+交易待进款-交易待出款+换汇待进款-换汇待出款+现金分红
		String flowMoneyKey = getKeyForMap(pDate, "YCS",fCashAccCode);
		BigDecimal flowMoneyValue = getSumMapValue(map,pDate,fCashAccCode);
		map.put(flowMoneyKey, flowMoneyValue);
		
		return map;
	}

	/**shashijie 2013-4-18 STORY 3343 获取实际结算金额*/
	private BigDecimal getCanMoneyValue(Date pDate, String fCashAccCode,
			String FTradeTypeCode) throws YssException {
		BigDecimal value = new BigDecimal(0);
		ResultSet rs = null;
		try {
			String query = getCanMoneyQuery(pDate,fCashAccCode,FTradeTypeCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getBigDecimal("FFactSettleMoney");
			}
		} catch (Exception e) {
			throw new YssException("获取实际结算金额出错!" , e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2013-4-18 STORY 获取SQL*/
	private String getCanMoneyQuery(Date pDate, String fCashAccCode,
			String fTradeTypeCode) {
		String sql = "";
		//若是分红数据则取实收实付金额,其余的取实际结算金额
		if (fTradeTypeCode.equals("06")) {
			sql = " Select Nvl(Sum(a.FTotalCost),0) FFactSettleMoney ";
		} else {
			sql = " Select Nvl(Sum(a.FFactSettleMoney),0) FFactSettleMoney ";
		}
		sql +=
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" a" +
			" Where a.Fcheckstate = 1" +
			" And a.Ftradetypecode In ("+operSql.sqlCodes(fTradeTypeCode)+")"+
			" And a.Fcashacccode = "+dbl.sqlString(fCashAccCode)+
			" And a.Fsettledate = "+dbl.sqlDate(pDate)+
			" Order By a.Fsettledate Desc";
		return sql;
	}

	/**shashijie 2013-4-13 STORY 3343 获取map中的几项数据的合计值*/
	private BigDecimal getSumMapValue(HashMap map, Date pDate,String fCashAccCode) throws YssException {
		BigDecimal value = new BigDecimal(0);
		if (map==null || map.isEmpty()) {
			return value;
		}
		//预测数 = 账面余额
		String key = getKeyForMap(pDate, "ZMYE",fCashAccCode);
		value = countBigDecimalValue(map,value,key,"add");
		
		//+交易待进款
		key = getKeyForMap(pDate, "JYDJK", fCashAccCode);
		value = countBigDecimalValue(map,value,key,"add");
		
		//-交易待出款
		key = getKeyForMap(pDate, "JYDCK", fCashAccCode);
		value = countBigDecimalValue(map,value,key,"sub");
		
		//+换汇待进款
		key = getKeyForMap(pDate, "HHDJK", fCashAccCode);
		value = countBigDecimalValue(map,value,key,"add");
		
		//-换汇待出款
		key = getKeyForMap(pDate, "HHDCK", fCashAccCode);
		value = countBigDecimalValue(map,value,key,"sub");
		
		//+现金分红
		key = getKeyForMap(pDate, "XJFH", fCashAccCode);
		value = countBigDecimalValue(map,value,key,"add");
		
		return value;
	}

	/**shashijie 2013-4-18 STORY 3343 根据标示计算bigdecimal的值*/
	private BigDecimal countBigDecimalValue(HashMap map,BigDecimal value, String key,
			String code) {
		BigDecimal tmp = new BigDecimal(0);
		if (map.containsKey(key)) {
			tmp = (BigDecimal)map.get(key);
		
			if (code.equals("add")) {//加法
				value = value.add(tmp);
			} else if (code.equals("sub")) {//减法
				value = value.subtract(tmp);
			} else if (code.equals("mul")) {//乘法
				value = value.multiply(tmp);
			} else if (code.equals("div")) {//除法
				value = value.divide(tmp);
			}
		}
		return value;
	}

	/**shashijie 2013-4-12 STORY 3344 外汇交易数据中的金额”)*/
	private BigDecimal getApplyValue(Date pDate,String fCashAccCode,String code) throws YssException {
		BigDecimal value = new BigDecimal(0);
		ResultSet rs = null;
		try {
			String query = getTaTradeQuery(pDate,fCashAccCode,code);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				//若是买入取买入金额,卖出则取卖出金额
				if (code.equals("0")) {
					value = rs.getBigDecimal("FBMoney");
				} else {
					value = rs.getBigDecimal("FSMoney");
				}
			}
		} catch (Exception e) {
			throw new YssException("获取外汇交易数据中的金额出错!" , e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2013-4-12 STORY 获取TA交易数据中"结算金额"的值SQL*/
	private String getTaTradeQuery(Date pDate, String fCashAccCode,String code) {
		String sql = "";
		//若是买入取买入金额,卖出则取卖出金额
		if (code.equals("0")) {
			sql = " Select Nvl(Sum(a.FBMoney),0) As FBMoney";
		} else {
			sql = " Select Nvl(Sum(a.FSMoney),0) As FSMoney";
		}
		sql += " From "+pub.yssGetTableName("Tb_Data_RateTrade")+" a" +
			" Where a.Fcheckstate = 1" +
			" And a.Fsettledate = "+dbl.sqlDate(pDate);
		if (code.equals("0")) {
			sql += " And a.FBCashAccCode = "+dbl.sqlString(fCashAccCode);
		} else {
			sql += " And a.FSCashAccCode = "+dbl.sqlString(fCashAccCode);
		}
		sql += " Order By a.FSettleDate Desc ";
		return sql;
	}

	/**shashijie 2013-4-12 STORY 3343 获取T日现金库存中，现金帐户的库存金额*/
	private BigDecimal getSurplusValue(Date pDate,String Fcashacccode) throws YssException {
		ResultSet rs = null;
		BigDecimal Faccbalance = new BigDecimal(0);
		try {
			String query = getStockCashQuery(pDate,Fcashacccode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				Faccbalance = rs.getBigDecimal("Faccbalance");
			}
		} catch (Exception e) {
			throw new YssException("获取T日现金库存中，现金帐户的库存金额出错!" , e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return Faccbalance;
	}

	/**shashijie 2013-4-12 STORY 3343 获取SQL*/
	private String getStockCashQuery(Date pDate,String Fcashacccode) {
		String SQL = " Select  B1.Faccbalance, B1.Fcurycode" +
			" From "+pub.yssGetTableName("Tb_Stock_Cash")+" B1" +
			" Where B1.Fstoragedate = "+dbl.sqlDate(pDate)+
            " And B1.Fcurycode <> 'CNY'" +
            " And B1.Fcashacccode = "+dbl.sqlString(Fcashacccode)+ 
            " And B1.Fcheckstate = 1 ";
		return SQL;
	}

	/**shashijie 2013-4-12 STORY 3343 获取key值*/
	private String getKeyForMap(Date pDate, String value,String fCashAccCode) {
		String key = "";
		key = YssFun.formatDate(pDate) + value + fCashAccCode;
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
            hmCellStyle = getCellStyles(FRelaCode);
            for (int i = 0; i < sArry.length; i++) {
                sKey = FRelaCode + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
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
