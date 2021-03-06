package com.yss.main.etfoperation.etfaccbook.ETFReport;

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
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssUtil;

/**shashijie 2013-5-18 STORY 3857 需求北京-(工银瑞信)QDIIV4.0(高)20130416003,ETF境内头寸预测 */
public class ETFDomesticMoneyForecast extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String FStartDate = "";//日期
    private String FPortCode = "";//组合代码
    private String FHolidaysCode = "";//节假日代码
    
    private double eCash = 0;//上一日的日末余额
	private BigDecimal property = new BigDecimal(0);//T日的资产净值 
    
    public ETFDomesticMoneyForecast() {
    }

    /**shashijie 2013-5-18 STORY 3857 程序入口 */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
    	//计算数据集合
        ArrayList list = new ArrayList();
        //开始日
        Date startDate = YssFun.toDate(this.FStartDate);
        //结束日
        Date endDate = getWorkDay(this.FHolidaysCode, startDate, 9);
        int diff = YssFun.dateDiff(startDate, endDate);
        //循环,不考虑选择日那天不是节假日的情况
        for (int i = 0; i < diff; i++) {
        	//当前处理日期
        	Date operonDate = YssFun.addDay(startDate, i);
        	//若非工作日则不处理
        	if (isWorkDate(operonDate, this.FHolidaysCode) || i==0) {
        		HashMap map = getCountData(operonDate,FPortCode,FHolidaysCode,i,startDate);
    			list.add(map);
			}
		}
        //获取报表内容
        sResult += getInfo(list);
        
        return sResult;
    }

	/**shashijie 2013-5-18 STORY 3857 获取计算后每天的数据*/
	private HashMap getCountData(Date operonDate, String fPortCode,
			String fHolidaysCode, int tag,Date startDate) throws YssException {
		HashMap map = new HashMap();
		
		double sCash = 0;//日初余额
		double buyMoney = 0;//申购替代款
		double sellMoney = 0;//赎回替代款
		double FCashBal = 0;//现金差额
		double replace = 0;//现金替代退补款
		double eCash = 0;//日末余额
		double proportion = 0;//占T日资产净值比例（%）
		
		//日期
		map.put("operonDate", YssFun.formatDate(operonDate));
		//第一日获取现金库存
		if (tag==0) {
			sCash = getStockCash(operonDate,fPortCode);
		}
		map.put("sCash", sCash);
		//申购替代款,T+1日结转,但却要显示在T+2日上
		buyMoney = getBuyMoney(getWorkDay(fHolidaysCode, operonDate, -1),fPortCode,"01");
		map.put("buyMoney", buyMoney);
		//赎回退补款,显示负数
		sellMoney = getBookReplace(getWorkDay(fHolidaysCode, operonDate, 1),fPortCode,startDate,"S");
		map.put("sellMoney", sellMoney);
		//现金差额,申购 - 赎回
		FCashBal = getFCashBalByDate(operonDate,fPortCode);
		map.put("FCashBal", FCashBal);
		//现金替代退补款,台账取数
		replace = getBookReplace(getWorkDay(fHolidaysCode, operonDate, 1),fPortCode,startDate,"B");
		map.put("replace", replace);
		//日末余额,暂时存0显示时计算
		map.put("eCash", eCash);
		//占T日资产净值比例（%）
		if (tag==0) {
			proportion = getProportion(operonDate,fPortCode,sCash);
			//获取T日的资产净值
			this.property = getDataNetValue(operonDate,fPortCode);
		}
		map.put("proportion", proportion);
		
		return map;
	}

	/**shashijie 2013-5-18 STORY 3857 获取现金库存*/
	private double getStockCash(Date operonDate, String fPortCode) throws YssException {
		double value = 0;
		ResultSet rs = null;//定义游标
		try {
			String query = getStockCashQuery(operonDate,fPortCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getDouble("Faccbalance");
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return value;
	}

	/**shashijie 2013-5-18 STORY 3857 获取现金库存*/
	private String getStockCashQuery(Date operonDate, String fPortCode) {
		String sql = " Select a.Fcashacccode, b.Faccbalance"+
			" From "+pub.yssGetTableName("Tb_Etf_Param")+" a" +
			" Join (Select B1.Faccbalance, B1.Fcashacccode" +
			" From "+pub.yssGetTableName("Tb_Stock_Cash")+" B1" +
			" Where B1.Fcheckstate = 1" +
			" And B1.Fstoragedate = " +dbl.sqlDate(operonDate)+
			" And B1.Fportcode = "+dbl.sqlString(fPortCode)+"" +
			" ) b On a.Fcashacccode = b.Fcashacccode" +
			" Where a.Fcheckstate = 1" +
			" And a.Fportcode = "+dbl.sqlString(fPortCode);
		return sql;
	}

	/**shashijie 2013-5-18 STORY 3857 获取TA交易数据 替代金额*/
	private double getBuyMoney(Date operonDate, String fPortCode, String FSellType) throws YssException {
		double value = 0;
		ResultSet rs = null;//定义游标
		try {
			//TA交易数据取数
			String query = getTAQuery(operonDate, fPortCode, "Fcashrepamount", FSellType, "Fcashreplacedate");
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getDouble("Fcashrepamount");
				if (FSellType.equals("02")) {
					value = value * -1;
				}
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return value;
	}

	/**shashijie 2013-5-18 STORY 3857 获取TA交易数据 现金差额,申购 - 赎回 */
	private double getFCashBalByDate(Date operonDate, String fPortCode) throws YssException {
		double value = 0;
		ResultSet rs = null;//定义游标
		try {
			//TA交易数据取数
			String query = getTAQuery(operonDate,fPortCode,"Fcashbal","01,02","Fcashbalancedate");
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//申购
				if (rs.getString("FSellType").equals("01")) {
					value = value + rs.getDouble("FCashBal");
				} else {//赎回
					value = value - rs.getDouble("FCashBal");
				}
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return value;
	}

	/**shashijie 2013-5-18 STORY 3857 获取TA交易数据SQL*/
	private String getTAQuery(Date operonDate, String fPortCode,
			String selectSum, String fSellType, String whereDate) {
		String sql = " Select Sum(a."+selectSum+") As "+selectSum+", a.Fselltype"+
			" From "+pub.yssGetTableName("Tb_Ta_Trade")+" a" +
			" Join (Select B1.Fcashacccode From " +
			pub.yssGetTableName("Tb_Etf_Param")+" B1"+
			" Where B1.Fcheckstate = 1) b On b.Fcashacccode = a.Fcashacccode"+
			" Where a.Fcheckstate = 1" +
			" And a.Fportcode = "+dbl.sqlString(fPortCode)+
			" And a.Fselltype In ("+operSql.sqlCodes(fSellType)+")"+
			" And a."+whereDate+" = "+dbl.sqlDate(operonDate)+
			" Group By a.Fselltype ";
		return sql;
	}

	/**shashijie 2013-5-18 STORY 3857  现金替代退补款,台账取数*/
	private double getBookReplace(Date operonDate, String fPortCode,
			Date startDate,String fBs) throws YssException {
		double value = 0;
		ResultSet rs = null;//定义游标
		try {
			String query = getFsumreturnQuery(operonDate,fPortCode,startDate,fBs);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				//若是赎回的退补款得加上必须现金替代的总金额
				if ("B".equals(fBs)) {
					value = rs.getDouble("Fsumreturn");
				} else if ("S".equals(fBs)) {
					//台账中的赎回款金额存的是负数所以这里是减
					value = YssD.sub(rs.getDouble("Fsumreturn"), rs.getDouble("Ftotalmoney"));
				}
				//台账中存放的是单位篮子的退补款金额,所以之类要乘以篮子数
				value = YssD.mul(value,rs.getDouble("Fnormscale"));
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return value;
	}

	/**shashijie 2013-5-18 STORY 3857 获取SQL */
	private String getFsumreturnQuery(Date operonDate, String fPortCode,
			Date startDate,String fBs) {
		String where = "";String where2 = "";
		//赎回加上申赎类型条件
		if (!YssUtil.isNullOrEmpty(fBs)) {
			where = " And a.Fbs = "+dbl.sqlString(fBs)+" ";
			where2 = " And a.Ftradetypecode = "+(fBs.equals("B")? " '102' ":" '103' ");
		}
		String sql = " Select Nvl(Sum(a.Fsumreturn),0) As Fsumreturn,"+
			" Nvl(Sum(b.Ftotalmoney),0) As Ftotalmoney,"+
			" Nvl(Sum(c.Ftradeamount / p.Fnormscale),0) As Fnormscale"+
			" /*,a.Frefunddate, a.Fbuydate, a.Fbs, a.Fportcode, a.Fsecuritycode, a.Fstockholdercode*/ "+
			" From "+pub.yssGetTableName("Tb_Etf_Standingbook")+" a"+
			" /*股票蓝*/ "+
			" Join (Select Sum(B1.Ftotalmoney) As Ftotalmoney, B1.Fdate" +
			" From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" B1"+
			" Where B1.Freplacemark = 6"+
			" Group By B1.Fdate) b On a.Fbuydate = b.Fdate"+
			" /*ETF参数设置*/"+
			" Join (Select P1.Fnormscale, P1.Fportcode"+
			" From "+pub.yssGetTableName("Tb_Etf_Param")+" P1"+
			" Where P1.Fcheckstate = 1" +
			" And P1.Fportcode = "+dbl.sqlString(fPortCode)+") p On a.Fportcode = p.Fportcode"+
			" /*ETF结算明细表*/"+
			" Join (Select a.Fportcode," +
			" a.Fbargaindate," +
			" /*a.Fstockholdercode," +
			" a.Fclearcode,*/ " +
			" Sum(a.Ftradeamount) As Ftradeamount" +
			" From "+pub.yssGetTableName("Tb_Etf_Jsmxinterface")+" a" +
			" Where a.Frecordtype = '003'" +
			" And a.Fresultcode = '0000'" +
			" And a.Fbargaindate = "+dbl.sqlDate(startDate)+
	        where2+
	        " And a.Fportcode = "+dbl.sqlString(fPortCode)+
	        " Group By a.Fportcode, a.Fbargaindate /*,a.Fstockholdercode, a.Fclearcode*/ ) c " +
	        " On a.Fbuydate = c.Fbargaindate" +
	        " Where a.Frefunddate = "+dbl.sqlDate(operonDate)+
	        " And a.Fsecuritycode = ' '" +
	        " And a.Fportcode = "+dbl.sqlString(fPortCode)+
	        where+
	        " And a.Fbuydate >= "+dbl.sqlDate(startDate);
		return sql;
	}

	/**shashijie 2013-5-18 STORY 占T日资产净值比例（%） = 日末余额/T日资产净值*100%*/
	private double getProportion(Date operonDate, String fPortCode,
			double sCash) throws YssException {
		BigDecimal netValue = getDataNetValue(operonDate,fPortCode);
		double value = 0;
		if (sCash != 0) {
			//日末余额/T日资产净值*100
			value = YssD.mul(
				YssD.div(new BigDecimal(sCash), netValue)
				,100);
		}
		return value;
	}

	/**shashijie 2013-5-18 STORY 3857 获取当天资产净值*/
	private BigDecimal getDataNetValue(Date operonDate, String fPortCode) throws YssException {
		BigDecimal value = new BigDecimal(0);
		ResultSet rs = null;//定义游标
		try {
			String query = getNetValueQuery(operonDate,fPortCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getBigDecimal("Fportnetvalue");
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return value;
	}

	/**shashijie 2013-5-18 STORY 3857 获取资产净值SQL*/
	private String getNetValueQuery(Date operonDate, String fPortCode) {
		String sql = " Select Fportnetvalue"+
			" From "+pub.yssGetTableName("Tb_Data_Netvalue")+
			" Where Ftype = '01'" +
			" And Finvmgrcode = ' '" +
			" And Fportcode = "+dbl.sqlString(fPortCode)+
			" And Fnavdate = "+dbl.sqlDate(operonDate)+
			" ";
		return sql;
	}

	/**shashijie 2013-5-18 STORY 3857 获取报表内容*/
	private String getInfo(ArrayList list) throws YssException {
		String str = "";
		try {
	        for (int i = 0; i < list.size(); i++) {
	        	HashMap map = (HashMap)list.get(i);
	        	String operonDate = "";//日期
	        	double sCash = 0;//日初余额
	    		double buyMoney = 0;//申购替代款
	    		double sellMoney = 0;//赎回替代款
	    		double FCashBal = 0;//现金差额
	    		double replace = 0;//现金替代退补款
	    		double eCash = 0;//日末余额
	    		double proportion = 0;//占T日资产净值比例（%）
	    		//日期
	    		operonDate = (String)map.get("operonDate");
	    		//第一天日初余额取库存后面的都取上一日的日末余额
	    		if (i==0) {
	    			sCash = (Double)map.get("sCash");
				} else {
					sCash = this.eCash;//上一日的日末余额
				}
	    		//申购替代款
	    		if (i==2) {
	    			buyMoney = (Double)map.get("buyMoney");
				}
	    		//赎回款
	    		if (i==8) {
	    			sellMoney = (Double)map.get("sellMoney");
				}
	    		//现金差额
	    		if (i==3) {
	    			FCashBal = (Double)map.get("FCashBal");
				}
	    		//现金替代退补款
	    		if (i==4) {
	    			replace = (Double)map.get("replace");
				}
	    		//日末余额,后几天都取T日资产净值
	    		eCash = YssD.add(sCash,buyMoney,sellMoney,FCashBal,replace);
	    		this.eCash = eCash;//保存上一日的日末余额
	    		if (i==0) {
	    			proportion = (Double)map.get("proportion");
				} else {
					if (!property.equals(BigDecimal.ZERO)) {
						//日末余额/T日资产净值*100
						proportion = YssD.mul(
							YssD.div(new BigDecimal(eCash), property)
							,100);
					}
				}
	    		//保留2位小数
	    		proportion = YssFun.roundIt(proportion, 2);
	    		
	    		str += operionStr(operonDate,
	    				YssFun.formatNumber(sCash,"0.00") + "", 
	    				YssFun.formatNumber(buyMoney,"0.00") + "",
						YssFun.formatNumber(sellMoney,"0.00") + "",
						YssFun.formatNumber(FCashBal,"0.00") + "",
						YssFun.formatNumber(replace,"0.00") + "",
						YssFun.formatNumber(eCash,"0.00") + "",
						YssFun.formatNumber(proportion,"0.00") + "%");
	        }
	        //去除最后"\r\n"
			if (str.length()>2) {
				str = YssFun.left(str,str.length()-2);
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			//dbl.closeResultSetFinal(rs);
			this.eCash = 0;
			this.property = BigDecimal.ZERO;
		}
		return str;
	}

	/**shashijie 2013-5-18 STORY 3857 拼接每行数据 */
	private String operionStr(String r1, String r2, String r3,
			String r4,String r5,String r6,String r7,String r8) {
		String str = "";
		
		str += r1 + "\t" + r2 + "\t" + r3 + "\t" + r4 + "\t" + r5 + "\t" + r6
				+ "\t" + r7 + "\t" + r8 + "\t";
		
		try {
			str = buildRowCompResult(str,"ETFTouCunYuCeNei")+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
	
	/**shashijie 2013-5-18 STORY 3857 把内容拼接上格式 */
	private String buildRowCompResult(String str,String code) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles(code);
            for (int i = 0; i < sArry.length; i++) {
                sKey = code + "\tDSF\t-1\t" + i;
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
            throw new YssException("拼接报表格式出错!",e);
        } finally {
            //dbl.closeResultSetFinal(rs);
        }
	}

	/**初始数据方法*/
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        FStartDate = reqAry[0].split("\r")[1];//日期
        FPortCode = reqAry[1].split("\r")[1];//组合
        FHolidaysCode = reqAry[2].split("\r")[1];//节假日
        
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

	public String getFEndDate() {
		return FPortCode;
	}

	public void setFEndDate(String fEndDate) {
		FPortCode = fEndDate;
	}

	public String getFPortCode() {
		return FHolidaysCode;
	}

	public void setFPortCode(String fPortCode) {
		FHolidaysCode = fPortCode;
	}

	/**shashijie 2013-5-18 STORY 3557 获取工作日方法 */
	private Date getWorkDay(String sHolidayCode, Date dDate, int dayInt)
			throws YssException {
		Date mDate = null;// 工作日
		// 公共获取工作日类
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		mDate = operDeal.getWorkDay(sHolidayCode, dDate, dayInt);
		return mDate;
	}
	
	/**shashijie 2013-5-18 STORY 3857 判断是否是工作日,是返回true*/
	private boolean isWorkDate(Date pDate,String sHolidayCode) throws YssException {
		boolean flag = false;
		if (YssFun.dateDiff(pDate,getWorkDay(sHolidayCode, pDate, 0)) == 0) {
			flag = true;
		}
		return flag;
	}

}
