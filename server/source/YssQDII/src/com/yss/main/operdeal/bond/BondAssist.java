package com.yss.main.operdeal.bond;

import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.bond.pojo.BondAssistBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssD;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.operdata.BondInterestBean;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.FixInterestBean;
import com.yss.main.parasetting.PeriodBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.main.parasetting.FixInterest.InterestTime;
import com.yss.main.storagemanage.SecurityStorageBean;
import com.yss.pojo.param.bond.YssBondIns;


public class BondAssist extends BaseBean{
	
	private Date ywDate = null;
	private String portCodes = "";
	private String secCodes = "";
	private HashMap<String, BondAssistBean> bondAssistMap = null;

	public static HashMap hmSec = null;
	public static HashMap hmParam = null;
	public static HashMap hmPort = null;
	public static HashMap hmStock = null;
	public static HashMap hmBonds = null;
	
	public void init(Date ywDate, String secCodes) throws YssException {
		if(null != bondAssistMap){
			return;
		}
		ResultSet rs = null;
		String sql = " select from " ;
		try{
			if(null == bondAssistMap){
				bondAssistMap = new HashMap<String, BondAssistBean>();
			}
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				BondAssistBean bondAssistBean = new BondAssistBean();
				String key = rs.getString("fsecuritycode");
				bondAssistMap.put(key, bondAssistBean);
			}
		}catch(Exception e){
			throw new YssException("初始化出现异常！", e);
		}finally{
			
		}
	}
	
	public void getAssistInfo(String portCode, String investType, String securityCode) {
		
	}
	
	/**
	 * 获得实际数量余额，=库存数量 + 未交割流出数量 - 未交割流入数量
	 * @param d
	 * @param securitycodes
	 * @return
	 */
	public HashMap<String, Double> getSlBalance(){
		//String key = 组合 + "\t" + 证券代码 + "\t" + 投资类型
		return null;
	}
	
	/**
	 * 获得实际应收利息余额，=库存应收利息 + 未交割流出应收利息 - 未交割流入应收利息
	 * @param d
	 * @param securitycodes
	 * @return
	 */
	public HashMap<String, Double> getYslxBalance(){
		//String key = 组合 + "\t" + 证券代码 + "\t" + 投资类型
		return null;
	}
	
	/**
	 * 获得实际溢折价余额，=库存溢折价 + 未交割流出溢折价 - 未交割流入溢折价
	 * @param d
	 * @param securitycodes
	 * @return
	 */
	public HashMap<String, Double> getYzjBalance(){
		//String key = 组合 + "\t" + 证券代码 + "\t" + 投资类型
		return null;
	}
	
	public HashMap<String, BondAssistBean> getBondAssistInfo(){
		//String key = 组合 + "\t" + 证券代码 + "\t" + 投资类型
		return null;
	} 
	
	/**
	 * 根据债券计息公式计算剩余天数
	 * @param accrualFormula	计息公式
	 * @param accrualDate	计息日期
	 * @param endDate	截止日期
	 * @return
	 */
	public double calcRemainDays(String accrualFormula,Date accrualDate,Date endDate){
		double days = 0;
		if(null != accrualFormula && null != accrualDate && null != endDate){
			//30/360 N = 360*(债券截息日的年份-计息日期的年份) + 30 *(债券截息日的月份-计息日期的月份) + (债券截息日期 - 计息日期 +1)
			if(accrualFormula.equalsIgnoreCase("")){
				days = 360 * (YssFun.getYear(endDate)-YssFun.getYear(accrualDate)) + 30 *(YssFun.getMonth(endDate)-YssFun.getMonth(accrualDate)) 
				+ (YssFun.getDay(endDate)-YssFun.getDay(accrualDate) + 1);
			}
			//A/365F 债券截息日与计息日之间包含N个2月29日，则为（债券截息日-计息日+1-N）
			else if(accrualFormula.equalsIgnoreCase("")){
				days = YssFun.dateDiff(accrualDate, endDate)+1.0-
				BondAssist.calcTimes2And29(accrualDate,endDate);
			}
			else{
				//N = 债券计息日 -计息日 +1
				days = YssFun.dateDiff(accrualDate, endDate) + 1;
			}
		}
		return days;
	}

	public static int calcTimes2And29(Date beginDate,Date endDate){
		int times = 0;
		if(null != beginDate && null != endDate){
			Calendar begin = Calendar.getInstance();
			begin.setTime(beginDate);
			while(!begin.getTime().after(endDate)){
				begin.add(Calendar.DATE, 1);
				if(begin.get(Calendar.MONTH)==1&&begin.get(Calendar.DAY_OF_MONTH)==29){
					times++;
				}
			}
		}
		return times;
	}
	
	/**
	 * 根据计息公式计算一年的天数
	 * @param accrualFormula
	 * @return
	 */
	public int calcYearDays(String accrualFormula){
		int yearDays = 0;
		if(null != accrualFormula){
			//A/A
			if(accrualFormula.equalsIgnoreCase("")){
				yearDays = 365;
			}
			//A/A-Bond
			else if(accrualFormula.equalsIgnoreCase("")){
				yearDays = 365;
			}
			//A/365
			else if(accrualFormula.equalsIgnoreCase("")){
				yearDays = 365;
			}
			//A/365F
			else if(accrualFormula.equalsIgnoreCase("")){
				yearDays = 365;
			}
			//A/360
			else if(accrualFormula.equalsIgnoreCase("")){
				yearDays = 360;
			}
			//30/360
			else if(accrualFormula.equalsIgnoreCase("")){
				yearDays = 360;
			}
		}
		return yearDays;
	}
	
	/**
	 * 根据债券计息公式计算债券到期本息和
	 * @param factor 债券相关信息
	 * @return
	 */
	public double calcFiDueSumMoney(){
		double sumMoney = 0.0;
		
		return sumMoney;
	}
	

	public Date getYwDate() {
		return ywDate;
	}

	public void setYwDate(Date ywDate) {
		this.ywDate = ywDate;
	}

	public String getPortCode() {
		return portCodes;
	}

	public void setPortCode(String portCodes) {
		this.portCodes = portCodes;
	}

	public String getSecCodes() {
		return secCodes;
	}

	public void setSecCodes(String secCodes) {
		this.secCodes = secCodes;
	}
	
	/**
	 * add by songjie 2013.04.17 
	 * STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001  
	 * 计算百元债券利息 插入债券利息表  和 付息期间设置表
	 * @param selCodes
	 * @param dDate
	 * @param portCode
	 * @throws YssException
	 */
	public void insertFixRelaInfo(String selCodes,java.util.Date dDate,String portCode)throws YssException{
        InterestTime itTime = new InterestTime();
        InterestTime itTimes = null;
        itTime.setYssPub(pub);
        ArrayList alExist = null;
        ResultSet rs = null;
        String strSql = "";
        HashMap hmReturn = null;
        ArrayList alInterest = new ArrayList();
        ArrayList alZQCodes = new ArrayList();
        ArrayList alZQInfo = new ArrayList();
		BondInterestBean bondInterest = null;
    	DataBase dataBase = null;
    	HashMap hmZQRate = null;
		try{
	        BondInterestBean bondIns = new BondInterestBean();
	        bondIns.setYssPub(pub);
			HashMap hmIntAccPer100 = bondIns.getAllSetting(dDate,selCodes);
			
			alExist = itTime.existFixInfo(selCodes,dDate);
			
			strSql = " select a.FSecurityCode, a.FInsStartDate, a.FInsEndDate, a.FInsFrequency, " +
			"a.FFaceRate, a.FBeforeFaceRate, a.FFACEVALUE, b.FHolidaysCode from " + 
			pub.yssGetTableName("Tb_Para_FixInterest") + " a " +
			" join " + pub.yssGetTableName("Tb_Para_Security") + " b on a.FSecurityCode = b.FSecurityCode " +
			" where a.FCheckState = 1 and a.FSecurityCode in(" + operSql.sqlCodes(selCodes) + ") ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
	            if((alExist == null || (alExist != null && !alExist.contains(rs.getString("FSecurityCode"))))
	            	&& hmSec.containsKey(rs.getString("FSecurityCode"))){
	            	hmReturn = new HashMap();
					BondInsCfgFormula bond = new BondInsCfgFormula();
					bond.setYssPub(pub);

					if(hmBonds != null && hmBonds.get(rs.getString("FSecurityCode")) != null){
						hmReturn = (HashMap)hmBonds.get(rs.getString("FSecurityCode"));
					}else{
						bond.getNextStartDateAndEndDate(dDate, 
								rs.getDate("FInsStartDate"), //总的起息日
								rs.getDate("FInsEndDate"), //总的截止日
								rs.getDouble("FInsFrequency"), //年付息频率
								hmReturn, //用于返回本期计息起始日  截止日 的HashMap
								rs.getString("FHolidaysCode"));//节假日群代码 
					}
					
					itTimes = new InterestTime();
					itTimes.setFSecurityCode(rs.getString("FSecurityCode"));//证券代码
					itTimes.setFExRightDate((Date)hmReturn.get("InsEndDate"));//除权日
					itTimes.setFInsStartDate((Date)hmReturn.get("InsStartDate"));//起始日
					itTimes.setFInsEndDate((Date)hmReturn.get("InsEndDate"));//截止日
					itTimes.setFIssueDate(YssFun.addDay((Date)hmReturn.get("InsEndDate"), 1));//派息日
					itTimes.setFRecordDate((Date)hmReturn.get("InsEndDate"));//登记日  需要删掉
					itTimes.setFFaceRate(rs.getDouble("FFaceRate"));//税后票面利率
					itTimes.setBeforeFaceRate(rs.getDouble("FBeforeFaceRate"));//税前票面利率
					itTimes.setSettleDate((Date)hmReturn.get("InsFXDate"));//到帐日
					itTimes.setID(((Integer)hmReturn.get("PaidInterest")).intValue());//付息次数
					itTimes.setPayMoney(0);
					itTimes.setRemainMoney(rs.getDouble("FFaceValue"));//票面金额
					
					alInterest.add(itTimes);
	            }
	            
	            if (hmIntAccPer100.get(rs.getString("FSecurityCode")) == null && 
	            	hmSec.containsKey(rs.getString("FSecurityCode"))) {
	            	dataBase = new DataBase();
	            	dataBase.setYssPub(pub);
	            	//计算税前 税后 百元债券利息 因为无法判断买卖标志 所以 默认为  用买入计息设置的利息算法公式来计算百元债券利息
	            	String[] portCodes = portCode.split(",");
	            	for(int i = 0; i < portCodes.length; i++){
	            		if (!alZQCodes.contains(rs.getString("FSecurityCode"))) {
	            			hmZQRate = dataBase.calculateZQRate(rs.getString("FSecurityCode"), dDate, "B", portCode);

	            			//若不能在债券信息设置中找到当前债券的信息 则 提示用户维护当前债券的信息
	            			if(((String)hmZQRate.get("haveInfo")).equals("false")){
	            				throw new YssException("请设置 " + rs.getString("FSecurityCode") + " 的相关债券信息！");
	            			}
						
		                    bondInterest = new BondInterestBean();//新建债券利息实例
		                    bondInterest.setSecurityCode(rs.getString("FSecurityCode"));//设置证券代码
		                    bondInterest.setIntAccPer100(new BigDecimal((String)hmZQRate.get("SQGZLX")));//设置税前百元利息
		                    bondInterest.setSHIntAccPer100(new BigDecimal((String)hmZQRate.get("GZLX")));//设置税后百元利息
							
							alZQCodes.add(rs.getString("FSecurityCode"));
							alZQInfo.add(bondInterest);// 将债券利息实例添加到列表中
						}
	            	}
	            }
			}
			
			if(alInterest.size() > 0){
				itTime.saveMutliInfo(alInterest);
			}

			if(alZQCodes.size() > 0){
            	//将债券利息表中没有的债券利息数据插入到表中
            	bondInterest = new BondInterestBean();
            	bondInterest.setYssPub(pub);
            	bondInterest.insertIntoBondInterest(alZQCodes,alZQInfo,dDate);
            }
		}catch(Exception e){
			throw new YssException("插入付息期间设置表出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2013.04.02 
	 * STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 
	 * 获取 债券基本信息
	 * @param selCodes
	 * @param dDate
	 * @param portCode
	 * @throws YssException
	 */
	public void getBasicBondInfo(String selCodes,java.util.Date dDate,String portCode) throws YssException{
		ResultSet rs = null;
		StringBuffer sbSql = new StringBuffer(); 
		FixInterestBean fixInterest = null;
		BondInsCfgFormula bond = new BondInsCfgFormula();
		SecurityBean security = null;
		HashMap hmBondDate = null;
		PeriodBean period = null;
		SecurityStorageBean secstorage = null;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        YssBondIns bondIns = null;
        BondInsCfgFormula.hmKey = new HashMap();
        BondInsCfgFormula.hmSec = new HashMap();
        HashMap hmReturn = null;
		try{
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
			
			bond.setYssPub(pub);
			
			hmSec = new HashMap();
			hmBonds = new HashMap();
			
            sbSql.append(" select fix.*,sec.*,c.FBeanId,b.FFormula,it.FInsStartDate as FInsStartDateI,")
            .append("it.FInsEndDate as FInsEndDateI,it.FPayMoney,it.FRemainMoney,per.FPeriodType,per.FDayOfYear from ")
            .append(" (select * from ").append(pub.yssGetTableName("Tb_Para_FixInterest"))
            .append(" where FSecurityCode in (").append(operSql.sqlCodes(selCodes)).append(")")
            .append(" and FInsStartDate <= ").append(dbl.sqlDate(dDate))
            .append(" and FInsEndDate >= ").append(dbl.sqlDate(dDate)).append(" and FCheckState = 1) fix ")
            .append(" left join ").append(pub.yssGetTableName("Tb_Para_Security")).append(" sec ")
            .append(" on fix.FSecurityCode = sec.FSecurityCode ")
            .append(" left join (select FCIMCode, FCIMName, FCIMType, FFormula, FSPICode ")
            .append(" from Tb_Base_CalcInsMetic where FCheckState = 1) b ")
            .append(" on fix.FCalcInsMeticDay = b.FCIMCode ")
            .append(" left join (select * from TB_FUN_SPINGINVOKE) c ")
            .append(" on c.FSICode = b.FSPICode ")
            .append(" left join (select * from ").append(pub.yssGetTableName("Tb_Para_InterestTime"))
            .append(" where ").append(dbl.sqlDate(dDate)).append(" between FInsStartDate and FInsEndDate) it ")
            .append(" on fix.FSecurityCode = it.FSecurityCode ")
            .append(" left join (select FPeriodType,FDayOfYear,FPeriodCode from ")
            .append(pub.yssGetTableName("Tb_Para_Period")).append(" where FCheckState = 1) per ")
            .append(" on per.FPeriodCode = fix.FPeriodCode")
            .append(" where not exists (select psc.FSecurityCodeBefore as FSecurityCode from ")
            .append(pub.yssGetTableName("Tb_Para_SecCodeChange"))
            .append(" psc where psc.FBusinessDate = ").append(dbl.sqlDate(dDate))
            .append(" and psc.FCheckState = 1 and psc.FSecurityCodeBefore = fix.FSecurityCode) ");
        rs = dbl.openResultSet(sbSql.toString());
        while (rs.next()) {
        	
        	
        	hmReturn = new HashMap();
        	hmBondDate = new HashMap();
        	fixInterest = new FixInterestBean();
        	
        	if(rs.getDate("FInsStartDateI") == null){
        		bond.getNextStartDateAndEndDate(dDate, 
        				rs.getDate("FInsStartDate"), //总的起息日
        				rs.getDate("FInsEndDate"), //总的截止日
        				rs.getDouble("FInsFrequency"), //年付息频率
        				hmReturn, //用于返回本期计息起始日  截止日 的HashMap
				    	rs.getString("FHolidaysCode"));//节假日群代码 

            	/**Start 20130711 added by liubo.Bug #8589.QDV4建行2013年07月09日01_B
            	 * 若无法获取到正确的计息期间设置，且getNextStartDateAndEndDate方法返回的是包含ERROR的hashmap，
            	 * 就不能往下执行，因为在取计息起始日、截止日时，会从hashmap中获取数据
            	 * 但是这种时候一定是取到的NULL值，就会出现空指针错误*/
        		if ((rs.getDate("FInsStartDateI") == null || rs.getDate("FInsEndDateI") == null)
        				&& hmReturn.get("error") != null)
        		{
        			continue ;
        		}
            	/**End 20130711 added by liubo.Bug #8589.QDV4建行2013年07月09日01_B*/
        		
        		hmBonds.put(rs.getString("FSecurityCode"), hmReturn);
        	}
        	
        	fixInterest.setStrSecurityCode(rs.getString("FSecurityCode") + "");
        	fixInterest.setStrPeriodCode(rs.getString("FPeriodCode") + "");
        	fixInterest.setStrCalcInsMeticBuy(rs.getString("FCalcInsMeticBuy") + "");
        	fixInterest.setStrCalcInsMeticSell(rs.getString("FCalcInsMeticSell") + "");
        	fixInterest.setStrCalcInsMeticDay(rs.getString("FCalcInsMeticDay") + "");
        	fixInterest.setExchangeCode(rs.getString("FExchangeCode") + "");
        	fixInterest.setBeanId(rs.getString("FBeanId") + "");
        	
        	if(rs.getDate("FInsStartDateI") == null){
        		fixInterest.setStrFaceValue(new BigDecimal(rs.getDouble("FFaceValue")));
        	}else{
        		fixInterest.setStrFaceValue(new BigDecimal(YssD.add(rs.getDouble("FPayMoney"), rs.getDouble("FRemainMoney"))));
        	}

        	fixInterest.setDFactRate(rs.getDouble("FFactRate"));
        	fixInterest.setStrFaceRate(rs.getBigDecimal("FFaceRate"));
        	fixInterest.setDbPretaxFaceRate(rs.getBigDecimal("FBeforeFaceRate"));
        	fixInterest.setStrInsFrequency(rs.getBigDecimal("FInsFrequency"));
        	fixInterest.setDtInsStartDate(rs.getDate("FInsStartDate"));
        	fixInterest.setDtInsEndDate(rs.getDate("FInsEndDate"));
        	fixInterest.setDtStartDate(rs.getDate("FInsStartDate"));
        	fixInterest.setDtInsCashDate(rs.getDate("FInsCashDate"));
        	fixInterest.setStrIssuePrice(rs.getBigDecimal("FIssuePrice"));//发行价格
        	fixInterest.setBaseCPI(rs.getDouble("FBASECPI"));
        	fixInterest.setStrPeriodCode(rs.getString("FPeriodCode") + "");
        	fixInterest.setFormula(rs.getString("FFormula") + "");
        	if(rs.getDate("FInsStartDateI") == null){
            	fixInterest.setDtThisInsStartDate((Date)hmReturn.get("InsStartDate"));
            	fixInterest.setDtThisInsEndDate((Date)hmReturn.get("InsEndDate"));
        		hmBondDate.put("ThisInsStartDate",YssFun.formatDate((Date)hmReturn.get("InsStartDate"),"yyyy-MM-dd"));//本计息期间计息起始日
        		hmBondDate.put("ThisInsEndDate",YssFun.formatDate((Date)hmReturn.get("InsEndDate"),"yyyy-MM-dd")); //本计息期间计息截止日
        	}else{
            	fixInterest.setDtThisInsStartDate(rs.getDate("FInsStartDateI"));
            	fixInterest.setDtThisInsEndDate(rs.getDate("FInsEndDateI"));
        		hmBondDate.put("ThisInsStartDate",YssFun.formatDate(rs.getDate("FInsStartDateI"),"yyyy-MM-dd"));//本计息期间计息起始日
        		hmBondDate.put("ThisInsEndDate",YssFun.formatDate(rs.getDate("FInsEndDateI"),"yyyy-MM-dd")); //本计息期间计息截止日
        	}
        	fixInterest.setHmBondDate(hmBondDate);
        	
        	period = new PeriodBean();
        	period.setPeriodCode(rs.getString("FPeriodCode") + "");
        	period.setDayOfYear(rs.getInt("FDayOfYear"));
        	period.setPeriodType(rs.getInt("FPeriodType"));
        	fixInterest.setPeriod(period);
        	
        	security = new SecurityBean();
        	security.setStrSecurityCode(rs.getString("FSecurityCode") + "");
        	security.setStrSecurityName(rs.getString("FSecurityName") + "");
            security.setStrSecurityShortName(rs.getString("FSecurityShortName") + "");
            security.setStrSecurityCropName(rs.getString("FSecurityCorpName") + "");
            security.setDtStartDate(rs.getDate("FStartDate"));
            security.setStrCategoryCode(rs.getString("FCatCode") + "");
            security.setStrSubCategoryCode(rs.getString("FSubCatCode") + "");
            security.setStrExchangeCode(rs.getString("FExchangeCode") + "");
            security.setStrMarketCode(rs.getString("FMarketCode") + "");
            security.setStrTradeCuryCode(rs.getString("FTradeCury") + "");
            security.setSectorCode(rs.getString("FSectorCode") + "");
            security.setStrSyntheticCode(rs.getString("FSYNTHETICCODE") + "");
            security.setStrSettleDayType(rs.getString("FSettleDayType") + "");
            security.setIntSettleDays(rs.getInt("FSettleDays"));
            security.setDblFactor(rs.getDouble("FFactor"));
            security.setDblTotalShare(rs.getBigDecimal("FTotalShare"));
            security.setDblCurrentShare(rs.getBigDecimal("FCurrentShare"));
            security.setDblHandAmount(rs.getBigDecimal("FHandAmount"));
            security.setStrIssueCorpCode(rs.getString("FIssueCorpCode") + "");
            security.setStrCusCatCode(rs.getString("FCusCatCode") + "");
            security.setStrHolidaysCode(rs.getString("FHolidaysCode") + "");
            security.setStrExternalCode(rs.getString("FExternalCode") + "");
            security.setStrDesc(rs.getString("FDesc") + "");
            security.checkStateId = rs.getInt("FCheckState");
            security.setStrTradeCuryCode(rs.getString("FTradeCury"));
        	
            fixInterest.setSecurity(security);
            
        	hmSec.put(rs.getString("FSecurityCode"), fixInterest);
        }
        
        dbl.closeResultSetFinal(rs);
        sbSql.setLength(0);
        
     	CtlPubPara clPub =new CtlPubPara();
     	clPub.setYssPub(pub);
     	String sFIstg = clPub.getIncomeFIDateCalcType();
     	String sPara = clPub.getNavType();
        hmParam = new HashMap();
        hmParam.put("sFIstg", sFIstg);
        hmParam.put("analy1", analy1);
        hmParam.put("analy2", analy2);
        hmParam.put("analy3", analy3);
        hmParam.put("NavType", sPara);
        
        String securityCodes = "";
        String calcFomula = "";
        HashMap hmCalc = new HashMap();
        String strSql = " select distinct FSecurityCode,FCalcInsMeticDay from " + pub.yssGetTableName("Tb_Para_Fixinterest") + 
        " where FSecurityCode in(" + operSql.sqlCodes(selCodes) + ") and FCheckState = 1";
        rs = dbl.openResultSet(strSql);
        while(rs.next()){
        	if(hmCalc.get(rs.getString("FCalcInsMeticDay")) == null){
        		calcFomula += rs.getString("FCalcInsMeticDay") + ",";
        		if(hmSec.containsKey(rs.getString("FSecurityCode"))){
        			securityCodes = rs.getString("FSecurityCode") + ",";
        		}
        	}else{
        		securityCodes = (String)hmCalc.get(rs.getString("FCalcInsMeticDay"));
        		if(hmSec.containsKey(rs.getString("FSecurityCode"))){
        			securityCodes += rs.getString("FSecurityCode") + ",";
        		}
        	}
        	
        	hmCalc.put(rs.getString("FCalcInsMeticDay"), securityCodes);
        }
        
        dbl.closeResultSetFinal(rs);
        
        if(calcFomula.length() > 1){
        	calcFomula = calcFomula.substring(0,calcFomula.length() - 1);
        }
        
        hmPort = new HashMap();
        
        strSql = " select h.FPortCode, (case when zb.FCPIPRICE > 0 then zb.FCPIPRICE else za.FCPIPRICE end) as FCPIPRICE " +
        " from (select FPortCode, 'FLINK' as FLINK from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
        " where FCheckState = 1 and FportCode in (" + operSql.sqlCodes(portCode) + ")) h " + 
        " left join (select 'FLINK' as FLINK, FCPIPRICE from  " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
        " where FCHECKSTATE = 1 and FCPIVALUEDATE = (select max(FCPIVALUEDATE) from " + 
        pub.yssGetTableName("TB_DATA_CPIVALUE") +
        " where FCHECKSTATE = 1 and FPORTCODE = ' ' and FCPIVALUEDATE <= " + 
        dbl.sqlDate(dDate) + " and FPORTCODE = ' ')) za on h.FLINK = za.Flink " +
        " left join (select FCPIPRICE, FPORTCODE from " + 
        pub.yssGetTableName("TB_DATA_CPIVALUE") + 
        " where FCHECKSTATE = 1 and FCPIVALUEDATE = (select max(FCPIVALUEDATE) from " + 
        pub.yssGetTableName("TB_DATA_CPIVALUE") + 
        " where FCHECKSTATE = 1 and FPORTCODE <> ' ' and " + 
        " FCPIVALUEDATE <= " + dbl.sqlDate(dDate) + ")) zb on h.FPortCode = zb.FPORTCODE ";
        rs = dbl.openResultSet(strSql);
        while(rs.next()){
        	hmPort.put(rs.getString("FPortCode"), rs.getDouble("FCPIPRICE"));
        }
        
        dbl.closeResultSetFinal(rs);
        
        hmStock = new HashMap();
        String key = "";
        strSql = " select distinct FSecurityCode, FPortCode, FAttrClsCode, FInvestType " + 
        (analy1 ? ",FAnalysisCode1" : " ") + (analy2 ? ",FAnalysisCode2" : " ") + 
        " from " + pub.yssGetTableName("Tb_Stock_Security") +
        " where FCheckState = 1 and FPortCode in (" + operSql.sqlCodes(portCode) + ")" +
        " and FStorageDate between " + dbl.sqlDate(YssFun.addDay(dDate, -1)) + " and " + 
        dbl.sqlDate(YssFun.addDay(dDate, 1)) +
        " and FSecurityCode in (" + operSql.sqlCodes(selCodes) + ")";
        rs = dbl.openResultSet(strSql);
        while(rs.next()){
        	if(!hmSec.containsKey(rs.getString("FSecurityCode"))){
        		continue;
        	}
        	key = rs.getString("FSecurityCode") + "\t" +
        	rs.getString("FPortCode") + "\t" +
        	rs.getString("FAttrClsCode") + "\t" +
        	rs.getString("FInvestType") + "\t" + 
        	(analy1 ? rs.getString("FAnalysisCode1") : " ") + "\t" +
        	(analy2 ? rs.getString("FAnalysisCode2") : " ");
        	
        	secstorage =  new SecurityStorageBean();
            secstorage.setStrStorageDate(YssFun.formatDate(dDate,"yyyy-MM-dd"));
            secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
            secstorage.setStrPortCode(rs.getString("FPortCode"));
            if (analy1) {
                secstorage.setStrFAnalysisCode1(rs.getString("FAnalysisCode1"));
            } else {
                secstorage.setStrFAnalysisCode1(" ");
            }
            if (analy2) {
                secstorage.setStrFAnalysisCode2(rs.getString("FAnalysisCode2"));
            } else {
                secstorage.setStrFAnalysisCode2(" ");
            }
            if (analy3) {
                secstorage.setStrFAnalysisCode3(rs.getString("FAnalysisCode3"));
            } else {
                secstorage.setStrFAnalysisCode3(" ");
            }
            secstorage.setAttrCode(rs.getString("FAttrClsCode"));
            secstorage.setInvestType(rs.getString("FInvestType"));
            hmStock.put(key, secstorage);
        }
        
        //add by songjie 2013.05.07 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
        dbl.closeResultSetFinal(rs);
        
        BondInsCfgFormulaN bondFormula = new BondInsCfgFormulaN();
        bondFormula.setYssPub(pub);
        strSql = " select calc.FCimCode, calc.FFormula, calc.FSpiCode, spring.FBeanid " + 
        " from Tb_Base_CalcInsMetic calc " + 
        " join (select FSiCode,FBeanid from TB_FUN_SPINGINVOKE) spring " + 
        " on calc.fSpiCode = spring.FSiCode " +
        " where calc.FCimType = 'Bond' and calc.FCheckState = 1 " + 
        " and calc.FCimCode in(" + operSql.sqlCodes(calcFomula) + ") ";
        rs = dbl.openResultSet(strSql);
        while(rs.next()){
        	securityCodes = (String)hmCalc.get(rs.getString("FCimCode"));
        	if(securityCodes.length() > 1){
        		securityCodes = securityCodes.substring(0, securityCodes.length() - 1);
        	}
        	if(securityCodes.trim().length() > 0){
					bondIns = new YssBondIns();
					bondIns.setInsDate(dDate);
					bondIns.setSecurityCode(securityCodes);
					bondIns.setPortCode(portCode);
					bondIns.setInsType("Day");
					bondIns.setInsAmount(1);// 设置成交数量为1

					/**Start 20130821 deleted by liubo.Bug #9110,#9111.
					 * 需求3964中的部分削减无用代码的内容，在此处有需要，合到60sp4中来*/
					//bondFormula.hmSec = new HashMap();//无用注释
					//bondFormula.hmKey = new HashMap();//无用注释
					/**End 20130821 deleted by liubo.Bug #9110,#9111*/

					bondFormula.init(bondIns);
					bondFormula.setFormula(rs.getString("FFormula"));
					bondFormula.setPortCode(portCode);
					bondFormula.setSecurityCode(securityCodes);
					bondFormula.hmFormulaValue = new HashMap();	//20131114 added by liubo.招行使用调度方案执行计提债券利息的问题
					bondFormula.calBondInterest();
        	}
        }
        
        dbl.closeResultSetFinal(rs);
        
		}catch(Exception e){
			throw new YssException("获取债券基本信息出错",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
