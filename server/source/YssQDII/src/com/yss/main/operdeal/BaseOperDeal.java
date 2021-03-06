package com.yss.main.operdeal;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.bond.BaseBondOper;
import com.yss.main.operdeal.invest.BaseInvestOper;
import com.yss.main.parasetting.CurrencyBean;
import com.yss.main.parasetting.FeeBean;
import com.yss.main.parasetting.IndexFuturesBean;
import com.yss.main.parasetting.PerformulaRelaBean;
import com.yss.main.parasetting.PeriodBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.parasetting.RoundingBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.pojo.cache.YssFeeType;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class BaseOperDeal
    extends BaseBean {
    public BaseOperDeal() {
    }

    private HashMap hmCuryRate = new HashMap(); //用于保存当前取出来的汇率
    private HashMap hmValCuryRate = new HashMap(); //用于保存估值汇率
    private HashMap hmDirectCury = new HashMap(); //2008-11-26 linjunyun bug:Ms00011 用于存放直接获取的利率及报价方向和报价因子
   	private HashMap hmcCulatePerExp = new HashMap();//保存舍入计算的值,系统优化 by leeyu 20100617 合并太平版本代码
    /**
     * getPerformulaRela
     *
     * @param dSumMoney double
     * @return PerformulaRelaBean
     */
    //屏蔽旧公式 杨文奇  20070918
    //public PerformulaRelaBean getPerformulaRela(String sFormulaCode,
    public HashMap getPerformulaRela(int perType, String sFormulaCode,
                                     double dSumMoney, java.util.Date dDate) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        int i = 0;
        double maxMoney = 0;
        PerformulaRelaBean perRela = null;
        HashMap hmPerRela = null;
        try {
            hmPerRela = new HashMap();
            if (perType == 0) { //绝对值处理方式
                strSql = "select y.* from " +
                    " (select FFormulaCode,FCheckState as FChkState,max(FRangeDate) as FRangeDate, min(FRangeMoney) as FRangeMoney from " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FRangeDate <= " +
                    dbl.sqlDate(dDate) + " and FRangeMoney >= " +
                    String.valueOf(dSumMoney) +
                    " and FCheckState = 1 group by FFormulaCode,FCheckState) x join" +
                    " (select * from " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FFormulaCode = " + dbl.sqlString(sFormulaCode) +
                    ") y" +
                    " on x.FFormulaCode = y.FFormulaCode and x.FRangeDate = y.FRangeDate" +
                    " and x.FRangeMoney = y.FRangeMoney";
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    perRela = new PerformulaRelaBean();
                    perRela.setFormulaCode(rs.getString("FFormulaCode") + "");
                    perRela.setPerValue(rs.getDouble("FPerValue"));
                    perRela.setFixValue(rs.getDouble("FFixValue"));
                    perRela.setLeastValue(rs.getDouble("FLeastValue"));
                    perRela.setMaxValue(rs.getDouble("FMaxValue"));
                } else {
                    //如果金额超出了最大的金额范围，那么就取最大金额范围的那条记录
                    dbl.closeResultSetFinal(rs);
                    strSql = "select y.* from " +
                        " (select FFormulaCode,FCheckState as FChkState,max(FRangeDate) as FRangeDate, max(FRangeMoney) as FRangeMoney from " +
                        pub.yssGetTableName("Tb_Para_Performula_Rela") +
                        " where FRangeDate <= " +
                        dbl.sqlDate(dDate) + " and FRangeMoney <= " +
                        String.valueOf(dSumMoney) +
                        " and FCheckState = 1 group by FFormulaCode,FCheckState) x join" +
                        " (select * from " +
                        pub.yssGetTableName("Tb_Para_Performula_Rela") +
                        " where FFormulaCode = " + dbl.sqlString(sFormulaCode) +
                        ") y" +
                        " on x.FFormulaCode = y.FFormulaCode and x.FRangeDate = y.FRangeDate" +
                        " and x.FRangeMoney = y.FRangeMoney";
                    rs = dbl.openResultSet(strSql);
                    if (rs.next()) {
                        perRela = new PerformulaRelaBean();
                        perRela.setFormulaCode(rs.getString("FFormulaCode") + "");
                        perRela.setPerValue(rs.getDouble("FPerValue"));
                        perRela.setFixValue(rs.getDouble("FFixValue"));
                        perRela.setLeastValue(rs.getDouble("FLeastValue"));
                        perRela.setMaxValue(rs.getDouble("FMaxValue"));
                    }
                }
                //if(perRela!=null)
                hmPerRela.put(new Integer(1), perRela);
                //else throw new YssException("还未设置费用比率");
            } else { //相对值处理方式
                strSql = "select y.* from " +
                    " (select FFormulaCode,FCheckState as FChkState,max(FRangeDate) as FRangeDate, min(FRangeMoney) as FRangeMoney from " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FRangeDate <= " +
                    dbl.sqlDate(dDate) + " and FRangeMoney >= " +
                    String.valueOf(dSumMoney) +
                    " and FCheckState = 1 group by FFormulaCode,FCheckState) x join" +
                    " (select * from " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FFormulaCode = " + dbl.sqlString(sFormulaCode) +
                    ") y" +
                    " on x.FFormulaCode = y.FFormulaCode and x.FRangeDate = y.FRangeDate" +
                    " and x.FRangeMoney = y.FRangeMoney";
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    maxMoney = rs.getDouble("FRangeMoney");
                } else {
                    //如果金额超出了最大的金额范围，那么就取最大金额范围的那条记录
                    dbl.closeResultSetFinal(rs);
                    strSql = "select y.* from " +
                        " (select FFormulaCode,FCheckState as FChkState,max(FRangeDate) as FRangeDate, max(FRangeMoney) as FRangeMoney from " +
                        pub.yssGetTableName("Tb_Para_Performula_Rela") +
                        " where FRangeDate <= " +
                        dbl.sqlDate(dDate) + " and FRangeMoney <= " +
                        String.valueOf(dSumMoney) +
                        " and FCheckState = 1 group by FFormulaCode,FCheckState) x join" +
                        " (select * from " +
                        pub.yssGetTableName("Tb_Para_Performula_Rela") +
                        " where FFormulaCode = " + dbl.sqlString(sFormulaCode) +
                        ") y" +
                        " on x.FFormulaCode = y.FFormulaCode and x.FRangeDate = y.FRangeDate" +
                        " and x.FRangeMoney = y.FRangeMoney";
                    rs = dbl.openResultSet(strSql);
                    if (rs.next()) {
                        maxMoney = rs.getDouble("FRangeMoney");
                    }
                    rs = dbl.openResultSet(strSql);
                }
                strSql = "select * from " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FCheckState = 1 and FRangeMoney <= " + maxMoney +
                    " and FFormulaCode = " + dbl.sqlString(sFormulaCode) //增加了条件，否则会把所有比例公式都取出来  胡坤  20080415
                    + " order by FRangeMoney";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    i++;
                    perRela = new PerformulaRelaBean();
                    perRela.setFormulaCode(rs.getString("FFormulaCode") + "");
                    perRela.setPerValue(rs.getDouble("FPerValue"));
                    perRela.setFixValue(rs.getDouble("FFixValue"));
                    perRela.setLeastValue(rs.getDouble("FLeastValue"));
                    perRela.setMaxValue(rs.getDouble("FMaxValue"));
                    perRela.setRangeMoney(rs.getBigDecimal("FRangeMoney")); //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
                    hmPerRela.put(new Integer(i), perRela);
                }
            }
            return hmPerRela;
        } catch (Exception e) {
            throw new YssException("获取费用比率出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double calculate(PerformulaRelaBean per, double dSumMoney) {
        double dResult = 0;
        if (per != null) { //---单亮-----2008.4.8----在调用此方法以前传来的PerformulaRelaBean有可能为空
            dResult = YssD.mul(dSumMoney, per.getPerValue()); //乘以比率
            dResult = YssD.add(dResult, per.getFixValue()); //加上固定值
            if (dResult < per.getLeastValue()) {
                dResult = per.getLeastValue(); //如果小于最小值都等于最小值
            }
            if (per.getMaxValue() != 0 && dResult > per.getMaxValue()) {
                dResult = per.getMaxValue();
            }
        }

        return dResult;

    }

    public double calculatePerExp(String sPerExpCode, double dSumMoney,
                                  java.util.Date dDate) throws
        YssException {
        String strSql = "";
        double dResult = 0;
        ResultSet rs = null;
        int perType = 0;
        HashMap hmPerRela = null;
      	String key =sPerExpCode+"\f"+ dSumMoney +"\f"+YssFun.formatDate(dDate, "yyyyMMdd"); //合并太平版本代码
        try {
	    	  //系统优化 by leeyu 2010617 合并太平版本代码
	    	  if(hmcCulatePerExp.get(key)!=null){
	    		  return Double.parseDouble(String.valueOf((hmcCulatePerExp.get(key))));
	    	  }
	    	  //系统优化 by leeyu 2010617
            strSql = "select FPerType from " +
                pub.yssGetTableName("tb_para_performula") +
                " where FCheckState = 1 and FFormulaCode = " +
                dbl.sqlString(sPerExpCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                perType = rs.getInt("FPerType"); //0绝对值，1相对值
            } else {
                //----------------------彭鹏 2008.2.20 BUG0000049----------------//
                if (sPerExpCode.equalsIgnoreCase("null")) {
                    throw new YssException("请检查比率公式是否未设置或已经审核");
                }
                //--------------------------------------------------------------//
                throw new YssException("请检查比率公式" + sPerExpCode + "是否维护比率类型并已经审核");
            }
            dbl.closeResultSetFinal(rs);

            hmPerRela = getPerformulaRela(perType, sPerExpCode, dSumMoney, dDate);

            if (perType == 0) { //绝对值
                dResult = calculate( (PerformulaRelaBean) hmPerRela.get(new Integer(
                    1)), dSumMoney);
            } else { //相对值
                for (int i = 1; i <= hmPerRela.size(); i++) {
                    PerformulaRelaBean perRela = (PerformulaRelaBean) hmPerRela.
                        get(new
                            Integer(i));
                    if (i == 1 && hmPerRela.size() == 1) { //dSumMoney小于最小的一个金额范围，直接用dSumMoney*比率
                        dResult += calculate(perRela, dSumMoney);
                    } else if (i == 1 && hmPerRela.size() != 1) { //dSumMoney大于最小的一个金额范围，则用最小的金额范围*比率
                        dResult += calculate(perRela, perRela.getRangeMoney().doubleValue()); //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
                    } else if (i != hmPerRela.size() && i != 1) { //中间过程就用（大金额范围-小金额范围）* 比率
                        PerformulaRelaBean perRelasub = (PerformulaRelaBean)
                            hmPerRela.
                            get(new
                                Integer(i - 1));
                        dResult +=
                            calculate(perRela,
                                      perRela.getRangeMoney().doubleValue() -
                                      perRelasub.getRangeMoney().doubleValue()); //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
                    } else { //（dSumMoney-小于dSumMoney的最大的金额范围）*比率
                        PerformulaRelaBean perRelasub = (PerformulaRelaBean)
                            hmPerRela.
                            get(new
                                Integer(i - 1));
                        dResult +=
                            calculate(perRela, dSumMoney - perRelasub.getRangeMoney().doubleValue()); //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
                    }
                }
            }
         	hmcCulatePerExp.put(key, new Double(dResult));//系统优化 by leeyu 2010617 合并太平版本代码
            return dResult;
        } catch (Exception e) {
            //2008.2.13 修改 蒋锦 原方法没有将变量 e 抛出，修改后抛出 e。
            throw new YssException("获取费用比率出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * reckonMoneyFee
     *
     * @param dSumMoney double
     * @return double
     */
    public double calMoneyByPerExp(String sPerExpCode, String sRoundCode,
                                   double dSumMoney, java.util.Date dDate) throws
        YssException {
        double dResult = 0;
        try {
            dResult = calculatePerExp(sPerExpCode, dSumMoney, dDate);
            dResult = reckonRoundMoney(sRoundCode, dResult);
            return dResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }
    
    public double calMoneyByPerExp(String sPerExpCode, double dSumMoney, java.util.Date dDate) throws YssException {
		double dResult = 0;
		try {
			dResult = calculatePerExp(sPerExpCode, dSumMoney, dDate);
			return dResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
    
    /**
     * 通过计息公式来计算利息，不在此过程中进行舍入
     * @param dDate Date
     * @param sPerExpCode String
     * @param dSumMoney double
     * @return double
     * @throws YssException
     */
    public double calMoneyByPerExp(java.util.Date dDate, String sPerExpCode,
                                   double dSumMoney) throws
        YssException {
        double dResult = 0;
        try {
            dResult = calculatePerExp(sPerExpCode, dSumMoney, dDate);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return dResult; //将方法总有return值
    }

    /*
        public double calMoneyByPerExp(String sPerExpCode, String sRoundCode,
                                   double dSumMoney) throws
          YssException {
       double dResult = 0;
       try {
          PerformulaRelaBean perRela = getPerformulaRela(sPerExpCode,
                dSumMoney);
          if (perRela != null) {
             if (dSumMoney == 0) {
                return dResult;
             }
             dResult = YssD.mul(dSumMoney, perRela.getPerValue()); //乘以比率
             dResult = YssD.add(dResult, perRela.getFixValue()); //加上固定值
             if (dResult < perRela.getLeastValue()) {
                dResult = perRela.getLeastValue(); //如果小于最小值都等于最小值
             }
     if (perRela.getMaxValue() != 0 && dResult > perRela.getMaxValue()) {
                dResult = perRela.getMaxValue();
             }
             dResult = reckonRoundMoney(sRoundCode, dResult);
          }
          return dResult;
       }
       catch (Exception e) {
          throw new YssException(e.getMessage());
       }
        }
        public double calMoneyByPerExp(String sPerExpCode, String sRoundCode,
                                   double dSumMoney) throws
          YssException {
       double dResult = 0;
       try {
          dResult = calculatePerExp(sPerExpCode,dSumMoney);
          dResult = reckonRoundMoney(sRoundCode, dResult);
          return dResult;
       }
       catch (Exception e) {
          throw new YssException(e.getMessage());
       }
        }*/
    /**
     * reckonMoneyFee
     *
     * @param dSumMoney double
     * @return double
     */
    /*
     public double calMoneyByPerExp(String sPerExpCode, double dSumMoney) throws
          YssException {
       double dResult = 0;
       try {
          dResult = calculatePerExp(sPerExpCode,dSumMoney);
          return dResult;
       }
       catch (Exception e) {
          throw new YssException(e.getMessage());
       }
        }
     */
    //哪儿调用的此方法啊
    public double calFeeMoney(YssFeeType feeType, FeeBean fee) throws
        YssException {
        return calFeeMoney(feeType, fee, new java.util.Date());
    }

    public double calcFuturesBail(String sSecCode, double dAmount, double dPrice) throws
        YssException {
        double dResult = 0;
        IndexFuturesBean fu = new IndexFuturesBean();
        fu.setYssPub(pub);
        fu.setSecurityCode(sSecCode);
        fu.getSetting();
        dResult = calcFuturesBail(fu.getBailType(), dAmount, dPrice,
                                  fu.getMultiple(), fu.getBailScale(),
                                  fu.getBailFix());
        return dResult;
    }

    public double calcFuturesBail(String sBailType, double dAmount,
                                  double dPrice, double dMultiple,
                                  double dBailScale, double dBailFix) throws
        YssException {
        double dResult = 0;
        if (sBailType.equalsIgnoreCase("Scale")) { //保证金类型：按比例
            dResult = YssD.round(YssD.mul(YssD.mul(YssD.mul(dAmount, dPrice),
                dMultiple), dBailScale), 2); //数量*价格*放大倍数*比例
        } else if (sBailType.equalsIgnoreCase("Scale")) { //保证金类型：按每手固定
            dResult = YssD.mul(dAmount, dBailFix);
        }
        return dResult;
    }

    public double calFeeMoney(YssFeeType feeType, FeeBean fee,
                              java.util.Date dDate) throws
        YssException {
        double dValue = 0;
        String sInfo = "";
        if (fee.getFeeType().equalsIgnoreCase("0")) { //金额
            dValue = feeType.getMoney();
            sInfo = "金额";
        } else if (fee.getFeeType().equalsIgnoreCase("1")) { //数量
            dValue = feeType.getAmount();
            sInfo = "数量";
        } else if (fee.getFeeType().equalsIgnoreCase("2")) { //成本
            dValue = feeType.getCost();
            sInfo = "成本";
        } else if (fee.getFeeType().equalsIgnoreCase("3")) { //收入
            dValue = feeType.getIncome();
            sInfo = "收入";
        } else if (fee.getFeeType().equalsIgnoreCase("4")) { //利息
            dValue = feeType.getInterest();
            sInfo = "利息";
        } else if (fee.getFeeType().equalsIgnoreCase("5")) { //费用
            dValue = feeType.getFee();
            sInfo = "费用";
        }
        if (dValue == -1) {
            throw new YssException("没有找到相关的【" + sInfo + "】请检查费用设置");
        }
        return calMoneyByPerExp(fee.getPerExpCode(), fee.getRoundCode(), dValue,
                                dDate);
    }

    /**
     * reckonRoundMoney
     *
     * @param sRoundCode String
     * @param dSumMoney double
     * @return double
     */
    public double reckonRoundMoney(String sRoundCode, double dMoney) throws
        YssException {
        boolean bFlag = false;
        double dResult = 0.0;

		RoundingBean round = new RoundingBean();
		//modify huangqirong 2013-02-27 bug #7146
		/*if (sRoundCode == null && sRoundCode.equalsIgnoreCase("")) {
			return 0;
		}*/

		// MS01284 add by zhangfa 2010.06.11 QDV4赢时胜(测试)2010年06月10日01_AB
		if (sRoundCode == null || sRoundCode.length() == 0
				|| sRoundCode.equalsIgnoreCase("null")) {
			// sRoundCode="R001".trim();//四舍五入保留2位小数位
			round.setRoundName("四舍五入保留2位小数位");
			round.setRoundSymbol("0");
			round.setRoundRange("0");
			round.setRoundDigit(2);
			round.setRoundWay("0");
		} else {
			round.setYssPub(pub);
			round.setRoundCode(sRoundCode);
			round.getSetting();
		}
		// ----------------------------------------------------------------------
		if (round.getRoundName().length() == 0) {
            throw new YssException("舍入代码【" + sRoundCode + "】被反审核或被删除");
        }
        if (Integer.parseInt(round.getRoundSymbol()) == YssOperCons.Yss_SRFH_BOTH) {
            bFlag = true;
        } else if (Integer.parseInt(round.getRoundSymbol()) ==
                   YssOperCons.Yss_SRFH_NEGATIVE) {
            bFlag = (dMoney < 0);
        } else if (Integer.parseInt(round.getRoundSymbol()) ==
                   YssOperCons.Yss_SRFH_POSITIVE) {
            bFlag = (dMoney > 0);
        }
        if (bFlag) {
            if (Integer.parseInt(round.getRoundWay()) ==
                YssOperCons.Yss_SRFF_NORMAL) {
                dResult = YssD.round(dMoney, round.getRoundDigit());
            } else if (Integer.parseInt(round.getRoundWay()) ==
                       YssOperCons.Yss_SRFF_MAX) {
                dResult = YssD.round(dMoney, round.getRoundDigit(),
                                     (dMoney < 0 ? BigDecimal.ROUND_DOWN :
                                      BigDecimal.ROUND_UP));
            } else if (Integer.parseInt(round.getRoundWay()) ==
                       YssOperCons.Yss_SRFF_MIN) {
                dResult = YssD.round(dMoney, round.getRoundDigit(),
                                     (dMoney < 0 ? BigDecimal.ROUND_UP :
                                      BigDecimal.ROUND_DOWN));
            } else if (Integer.parseInt(round.getRoundWay()) ==
                       YssOperCons.Yss_SRFF_TRUNCATION) {
                dResult = YssD.round(dMoney, round.getRoundDigit(),
                                     (dMoney < 0 ? BigDecimal.ROUND_CEILING :
                                      BigDecimal.ROUND_DOWN));
            }
        }
        return dResult;
    }

    /**
     * reckonRoundMoney
     *
     * @param rounding RoundingBean
     * @param dSumMoney double
     * @return double
     */
    public double reckonRoundMoney(RoundingBean rounding, double dSumMoney) {
        return 0.0;
    }

    public double getValCuryRate(java.util.Date dValDate, String sCuryCode,
                                 String sPortCode, String sRateType) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        double dResult = 0;
        String sKey = "";
        try {
            sKey = YssFun.formatDate(dValDate, "yyyy-MM-dd") + "\f\f" + sCuryCode +
                "\f\f" + sPortCode + "\f\f" + sRateType;
            if (hmValCuryRate.get(sKey) != null) {
                dResult = YssFun.toDouble(String.valueOf(hmValCuryRate.get(sKey)));
                return dResult;
            }
            //2008.01.31 修改 蒋锦 添加是否传入组合代码的判断，如果没有传入组合代码就不加组合代码的条件//
            strSql = "select * from " + pub.yssGetTableName("Tb_Data_ValRate") +
                " where FValDate = (select max(FValDate) from " +
                pub.yssGetTableName("Tb_Data_ValRate") +
                " WHERE FValDate <= " + dbl.sqlDate(dValDate);
            if (sPortCode.length() > 0) {
                strSql += " AND FPortCode = " + dbl.sqlString(sPortCode);
            }
            strSql +=
                " and FCuryCode = " + dbl.sqlString(sCuryCode) +
                " ) and FCuryCode = " + dbl.sqlString(sCuryCode);
            if (sPortCode.length() > 0) {
                strSql += " and FPortCode = " + dbl.sqlString(sPortCode);
            }
            //------------------------------------------------------------------------------//
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATEVAL_BASE)) { //取估值基础汇率
                    dResult = rs.getDouble("FBaseRate");
                } else if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATEVAL_PORT)) { //取估值组合汇率
                    dResult = rs.getDouble("FPortRate");
                }
            }
            hmValCuryRate.put(sKey, dResult + "");
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dResult;
    }

    //------------彭鹏 2008.03.10 BUG0000066 手工录业务资料,带出的基础汇率不正确----------//
    public double getCuryExchange(java.util.Date dRateDate, String sRateTime,
                                  String sCuryCode, String sPortCode,
                                  String sRateType) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String strError = "获取汇率行情出错";
        double dReturn = 1;
        double dFactor = 1;
        String sInvertInd = "";
        PortfolioBean port = null;
        try {
        	  // edit by lidaolong 20110401 ,BUG #1635 数据接口：导入券商交易数据的时候，获取汇率有问题 
            if (sCuryCode.equalsIgnoreCase(pub.getPortBaseCury(sPortCode)) &&
            		 !sRateType.equals( YssOperCons.YSS_RATE_PORT)) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
             
                return dReturn;
            }
            //end by lidaolong
            
            if (port != null) {
                if (sCuryCode.equalsIgnoreCase(port.getCurrencyCode())) {
                    sRateType = YssOperCons.YSS_RATE_PORT;
                }
            }
            strSql = "select a.*, b.FInvertInd, b.FFactor " +
                " from (select FCuryCode,FPortCode,FExRate1 from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 ";
            //edit by lidaolong  2011.02.18  #399 QDV4上海2010年12月10日02_A                
            strSql = strSql +  " AND FMarkCury = '"+pub.getPortBaseCury(sPortCode)+"'";              
            // --end lidaolong --
            strSql = strSql + " and FCuryCode=" + dbl.sqlString(sCuryCode) +
                " and FExRateDate = " +
                "(select max(FExRateDate) as FExRateDate from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 and FExRateDate <= " +
                dbl.sqlDate(dRateDate) ;
                //edit by lidaolong  2011.02.18  #399 QDV4上海2010年12月10日02_A                
                strSql = strSql +  " AND FMarkCury = '"+pub.getPortBaseCury(sPortCode)+"'"+" and (FPortCode=" +
                (sPortCode == null ? dbl.sqlString(" ") :
                    dbl.sqlString(sPortCode)) +
                   " or FPortCode=' ')";              
                // --end lidaolong --
                strSql = strSql +    " and FCuryCode=" + dbl.sqlString(sCuryCode) +
                " group by FCuryCode)";
            strSql = strSql +
                "order by FPortCode desc) a left join " +
                " (select FCuryCode, FInvertInd, FFactor from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") b on a.FCuryCode = b.FCuryCode";

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReturn = rs.getDouble("FExRate1");
                sInvertInd = rs.getString("FInvertInd");
                dFactor = rs.getDouble("FFactor") == 0 ? 1 : rs.getDouble("FFactor");
                if (sInvertInd != null) {
                    if (sInvertInd.equals("1") && dReturn != 0) {
                        dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
                    }
                    if (sInvertInd.equals("0")) {
                        dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
                    }
                }
            } else {
                dbl.closeResultSetFinal(rs);
                strSql = "select FInitRate,FInvertInd, FFactor from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1"
                    + " and FCuryCode = " + dbl.sqlString(sCuryCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    dReturn = rs.getDouble("FInitRate");
                    sInvertInd = rs.getString("FInvertInd");
                    dFactor = rs.getDouble("FFactor") == 0 ? 1 :
                        rs.getDouble("FFactor");
                    if (sInvertInd != null) {
                        if (sInvertInd.equals("1") && dReturn != 0) {
                            dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
                        }
                        if (sInvertInd.equals("0")) {
                            dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
                        }
                    }
                }
            }
            return dReturn;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    //--------------------------------------------------------------------------//

    /**
     * getCuryRate
     * 获取指定日期和时间，指定来源和币种的汇率
     * @param dRateDate Date
     * @param sRateTime String
     * @param sRateSource String
     * @param sCuryCode String
     * @param sPortCode String
     * @return double
     */
    public double getCuryRate(java.util.Date dRateDate, String sRateTime,
                              String sCuryCode, String sBaseRateSrc_f,
                              String sBaseRateField_f,
                              String sPortRateSrc_f, String sPortRateField_f,
                              String sPortCode, String sRateType) throws
        YssException {
        String sKey = "";
        String strSql = "";
        String strSrcWhereSql = "";
        ResultSet rs = null;
        String strError = "获取汇率行情出错";
        double dReturn = 1;
        double dFactor = 1;
        String sInvertInd = "";
        String sBaseRateSrc = "";
        String sBaseRateField = "";
        String sPortRateSrc = "01";
        String sPortRateField = "FExRate1";
        String sRateField = "";
        PortfolioBean port = null;
        try {
            sKey = YssFun.formatDate(dRateDate, "yyyy-MM-dd") + "\f\f" + sRateTime +
                "\f\f" +
                sCuryCode + "\f\f" + sBaseRateSrc_f + "\f\f" + sBaseRateField_f +
                "\f\f" +
                sPortRateSrc_f + "\f\f" + sPortRateField_f + "\f\f" + sPortCode +
                "\f\f" + sRateType;
            //将外部传进来的条件值转成一个Key by liyu
            if (hmCuryRate.get(sKey) != null) {
                dReturn = YssFun.toDouble(String.valueOf(hmCuryRate.get(sKey)));
                return dReturn;
            }
            //获取组合汇率时,如果该币种刚好跟组合货币一致，就不能取出汇率数据fazmm20071007
            //字符不能用“！=”，否则总会进到这个里面，20071108杨文奇
            // edit by lidaolong 20110401 ,BUG #1635 数据接口：导入券商交易数据的时候，获取汇率有问题 
            if (sCuryCode.equalsIgnoreCase(pub.getPortBaseCury(sPortCode)) &&
            		!sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_PORT)) {  // edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
             
                return dReturn;
            }
            
            //end by lidaolong
            
            sBaseRateSrc = pub.getBaseRateSrcCode();
            sBaseRateField = pub.getBaseRateCode(); //默认取组合群设置中的基础汇率来源和汇率字段
            sPortRateSrc = pub.getPortRateSrcCode();
            sPortRateField = pub.getPortRateCode(); //默认取组合群设置中的组合汇率来源和汇率字段
            if (sPortCode != null && sPortCode.length() > 0) { //如果传入了组合，就取组合设置中的基础汇率来源和汇率字段
                port = new PortfolioBean();
                port.setYssPub(pub);
                port.setPortCode(sPortCode);
                port.getSetting();
                if (port.getBaseRateSrcCode() != null &&
                    port.getBaseRateSrcCode().trim().length() > 0 &&
                    !port.getBaseRateSrcCode().equalsIgnoreCase("null")) {
                    sBaseRateSrc = port.getBaseRateSrcCode();
                }
                if (port.getBaseRateCode() != null &&
                    port.getBaseRateCode().trim().length() > 0 &&
                    !port.getBaseRateCode().equalsIgnoreCase("null")) {
                    sBaseRateField = port.getBaseRateCode();
                }
                if (port.getPortRateSrcCode() != null &&
                    port.getPortRateSrcCode().trim().length() > 0 &&
                    !port.getPortRateSrcCode().equalsIgnoreCase("null")) {
                    sPortRateSrc = port.getPortRateSrcCode();
                }
                if (port.getPortRateCode() != null &&
                    port.getPortRateCode().trim().length() > 0 &&
                    !port.getPortRateCode().equalsIgnoreCase("null")) {
                    sPortRateField = port.getPortRateCode();
                }
            }
            if (port != null) {
                if (sCuryCode.equalsIgnoreCase(port.getCurrencyCode())) { //如果交易货币和组合货币相同，那么组合汇率来源强制等于组合货币来源，否则会出现原币和组合货币不相等的情况  胡昆  20070919
                    sRateType = YssOperCons.YSS_RATE_PORT;
                }
                //进入组合汇率计算时，都需要重新获取组合货币进行计算fazmm20071016
                //if ((sCuryCode.equalsIgnoreCase(pub.getBaseCury()) || sCuryCode.trim().length() ==0)  && sRateType == YssOperCons.YSS_RATE_PORT){
                //   sCuryCode = port.getCurrencyCode();
                //}
            }
            //华夏这边基础汇率来源和组合汇率来源是不一样的，基础汇率来源是澎博，组合汇率来源是外汇中心
            //基础货币：美元，组合货币：人民币。
            //人民币对兑美元，他们不会维护澎博的来源的数据的，所以如果这样改，就取不到汇率了。
            //杨文奇20070925
            //以上这个问题应该修改为组合货币来源，而不是基础货币来源fazmm20071001
            if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_BASE)) { //取基础汇率
                if (sBaseRateSrc_f.trim().length() == 0 &&
                    sBaseRateField_f.trim().length() == 0) { //如果传入了基础汇率来源和字段，就用传进来的，如果没有传入就用组合设置中的
                    if (sBaseRateSrc.trim().length() > 0) {
                        strSrcWhereSql = " and FExRateSrcCode=" +
                            dbl.sqlString(sBaseRateSrc);
                    }
                    sRateField = sBaseRateField;
                } else {
                    strSrcWhereSql = " and FExRateSrcCode=" +
                        dbl.sqlString(sBaseRateSrc_f);
                    sRateField = sBaseRateField_f;
                }

            } else if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_PORT)) { //取组合汇率
          
            	
                sCuryCode = (port == null ? "" : port.getCurrencyCode()); //20071012  chenyibo   获取组合中的货币代码
              
                // add by lidaolong 20110401 ,BUG #1635 数据接口：导入券商交易数据的时候，获取汇率有问题 	
           	 if (sCuryCode.equalsIgnoreCase(pub.getPortBaseCury(sPortCode))){
           		 return dReturn;
           	 }
           	 //end by lidaolong
                
                if (sPortRateSrc_f.trim().length() == 0 &&
                    sPortRateField_f.trim().length() == 0) { //如果传入了组合汇率来源和字段，就用传进来的，如果没有传入就用组合设置中的
                    if (sPortRateSrc.trim().length() > 0) {
                        strSrcWhereSql = " and FExRateSrcCode=" +
                            dbl.sqlString(sPortRateSrc);
                    }
                    sRateField = sPortRateField;
                } else {
                    strSrcWhereSql = " and FExRateSrcCode=" +
                        dbl.sqlString(sPortRateSrc_f);
                    sRateField = sPortRateField_f;
                }
            }

            strSql = "select a.*, b.FInvertInd, b.FFactor " +
                " from (select FCuryCode,FPortCode," + sRateField + " from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 ";
            //edit by lidaolong  2011.02.18  #399 QDV4上海2010年12月10日02_A                
            strSql = strSql +  " AND FMarkCury = '"+pub.getPortBaseCury(sPortCode)+"'";              
            // --end lidaolong --
            
            strSql = strSql + " and FCuryCode=" + dbl.sqlString(sCuryCode) +
                " and FExRateDate = " +
                "(select max(FExRateDate) as FExRateDate from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 and FExRateDate <= " +
                dbl.sqlDate(dRateDate) +
                " and FCuryCode=" + dbl.sqlString(sCuryCode);
            //edit by lidaolong  2011.02.18  #399 QDV4上海2010年12月10日02_A    
          //  if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_PORT)) {
            	strSql = strSql +  " AND FMarkCury = '"+pub.getPortBaseCury(sPortCode)+"'";   
          //  }
           // --end lidaolong --
               	 strSql = strSql +  strSrcWhereSql +
                " and (FPortCode=" +
                (sPortCode == null ? dbl.sqlString(" ") :
                 dbl.sqlString(sPortCode)) +
                " or FPortCode=' ')" +
                /* 彭彪20070929 先不考虑时间
                       " and FExRateTime=" +
                                (sRateTime.length() == 0 ? "'00:00:00'" :
                 //既有维护组合代码也有没有维护组合代码的就会存在有问题，会取出多条记录，导致报错fazmm20070906
                 //dbl.sqlString(sRateTime)) + " group by FCuryCode,FPortCode" +
                 dbl.sqlString(sRateTime)) +*/
                " group by FCuryCode" +
                ")" + strSrcWhereSql;

            strSql = strSql + " and (FPortCode=" +
                (sPortCode == null ? dbl.sqlString(" ") :
                 dbl.sqlString(sPortCode)) +
                " or FPortCode=' ')";
            strSql = strSql +
                /*彭彪20070929 先不考虑时间
                        " and FExRateTime=" +
                        (sRateTime.length() == 0 ? "'00:00:00'" :
                         dbl.sqlString(sRateTime)) +*/
                "order by FPortCode desc) a left join " +
                " (select FCuryCode, FInvertInd, FFactor from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") b on a.FCuryCode = b.FCuryCode order by FPortCode desc"; //2008-6-1 单亮 添加 order by FPortCode desc

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReturn = rs.getDouble(sRateField);
                sInvertInd = rs.getString("FInvertInd");
                dFactor = rs.getDouble("FFactor") == 0 ? 1 : rs.getDouble("FFactor");
                if (sInvertInd != null) {
                    if (sInvertInd.equals("1") && dReturn != 0) {
                        dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
                    }
                    if (sInvertInd.equals("0")) {
                        dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
                    }
                }
            } else {
                dbl.closeResultSetFinal(rs);
                strSql = "select FInitRate,FInvertInd, FFactor from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1"
                    + " and FCuryCode = " + dbl.sqlString(sCuryCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    dReturn = rs.getDouble("FInitRate");
                    sInvertInd = rs.getString("FInvertInd");
                    dFactor = rs.getDouble("FFactor") == 0 ? 1 :
                        rs.getDouble("FFactor");
                    if (sInvertInd != null) {
                        if (sInvertInd.equals("1") && dReturn != 0) {
                            dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
                        }
                        if (sInvertInd.equals("0")) {
                            dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
                        }
                    }
                }
            }
            hmCuryRate.put(sKey, dReturn + ""); //将值放入到hashMap中
            return dReturn;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    /**
     * getCuryRate
     * 获取指定日期和时间，指定来源和币种的汇率
     * add by huangqirong 2012-10-11 story #3120 主要针对外管局汇率
     * @param dRateDate Date
     * @param sRateTime String
     * @param sRateSource String
     * @param sCuryCode String
     * @param sPortCode String
     * @return double
     */
    public double getCuryRateByRateSrc(java.util.Date dRateDate, String sRateTime,
                              String sCuryCode, String sBaseRateSrc_f,
                              String sBaseRateField_f,
                              String sPortRateSrc_f, String sPortRateField_f,
                              String sPortCode, String sRateType ) throws
        YssException {
        String sKey = "";
        String strSql = "";
        String strSrcWhereSql = "";
        ResultSet rs = null;
        String strError = "获取汇率行情出错";
        double dReturn = 1;
        double dFactor = 1;
        String sInvertInd = "";
        String sBaseRateSrc = "";
        String sBaseRateField = "";
        String sPortRateSrc = "01";
        String sPortRateField = "FExRate1";
        String sRateField = "";
        PortfolioBean port = null;
        try {
            sKey = YssFun.formatDate(dRateDate, "yyyy-MM-dd") + "\f\f" + sRateTime +
                "\f\f" +
                sCuryCode + "\f\f" + sBaseRateSrc_f + "\f\f" + sBaseRateField_f +
                "\f\f" +
                sPortRateSrc_f + "\f\f" + sPortRateField_f + "\f\f" + sPortCode +
                "\f\f" + sRateType;
            //将外部传进来的条件值转成一个
            if (hmCuryRate.get(sKey) != null) {
                dReturn = YssFun.toDouble(String.valueOf(hmCuryRate.get(sKey)));
                return dReturn;
            }
            //获取组合汇率时,如果该币种刚好跟组合货币一致，就不能取出汇率数据
            //字符不能用“！=”，否则总会进到这个里面
            //数据接口：导入券商交易数据的时候，获取汇率有问题 
            
            //20130206 deleted by liubo.Bug #7069
            //取外管局汇率，不需要进行这个判断
            //=================================
//            if (sCuryCode.equalsIgnoreCase(pub.getPortBaseCury(sPortCode)) &&
//            		!sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_PORT)) {
//             
//                return dReturn;
//            }
            //================end=================

            sBaseRateSrc = pub.getBaseRateSrcCode();
            sBaseRateField = pub.getBaseRateCode(); //默认取组合群设置中的基础汇率来源和汇率字段
            sPortRateSrc = pub.getPortRateSrcCode();
            sPortRateField = pub.getPortRateCode(); //默认取组合群设置中的组合汇率来源和汇率字段
            if (sPortCode != null && sPortCode.length() > 0) { //如果传入了组合，就取组合设置中的基础汇率来源和汇率字段
                port = new PortfolioBean();
                port.setYssPub(pub);
                port.setPortCode(sPortCode);
                port.getSetting();
                if (port.getBaseRateSrcCode() != null &&
                    port.getBaseRateSrcCode().trim().length() > 0 &&
                    !port.getBaseRateSrcCode().equalsIgnoreCase("null")) {
                    sBaseRateSrc = port.getBaseRateSrcCode();
                }
                if (port.getBaseRateCode() != null &&
                    port.getBaseRateCode().trim().length() > 0 &&
                    !port.getBaseRateCode().equalsIgnoreCase("null")) {
                    sBaseRateField = port.getBaseRateCode();
                }
                if (port.getPortRateSrcCode() != null &&
                    port.getPortRateSrcCode().trim().length() > 0 &&
                    !port.getPortRateSrcCode().equalsIgnoreCase("null")) {
                    sPortRateSrc = port.getPortRateSrcCode();
                }
                if (port.getPortRateCode() != null &&
                    port.getPortRateCode().trim().length() > 0 &&
                    !port.getPortRateCode().equalsIgnoreCase("null")) {
                    sPortRateField = port.getPortRateCode();
                }
            }
            if (port != null) {
                if (sCuryCode.equalsIgnoreCase(port.getCurrencyCode())) { //如果交易货币和组合货币相同，那么组合汇率来源强制等于组合货币来源，否则会出现原币和组合货币不相等的情况
                    sRateType = YssOperCons.YSS_RATE_PORT;
                }
                //进入组合汇率计算时，都需要重新获取组合货币进行计算
                //if ((sCuryCode.equalsIgnoreCase(pub.getBaseCury()) || sCuryCode.trim().length() ==0)  && sRateType == YssOperCons.YSS_RATE_PORT){
                //   sCuryCode = port.getCurrencyCode();
                //}
            }
            //华夏这边基础汇率来源和组合汇率来源是不一样的，基础汇率来源是澎博，组合汇率来源是外汇中心
            //基础货币：美元，组合货币：人民币。
            //人民币对兑美元，他们不会维护澎博的来源的数据的，所以如果这样改，就取不到汇率了。            
            //以上这个问题应该修改为组合货币来源，而不是基础货币来源
            if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_BASE)) { //取基础汇率
                if (sBaseRateSrc_f.trim().length() == 0 &&
                    sBaseRateField_f.trim().length() == 0) { //如果传入了基础汇率来源和字段，就用传进来的，如果没有传入就用组合设置中的
                    if (sBaseRateSrc.trim().length() > 0) {
                        strSrcWhereSql = " and FExRateSrcCode=" +
                            dbl.sqlString(sBaseRateSrc);
                    }
                    sRateField = sBaseRateField;                
                }else if (sBaseRateSrc_f.trim().length() > 0 && sBaseRateField_f.trim().length() == 0){
                	strSrcWhereSql = " and FExRateSrcCode=" +
                    dbl.sqlString(sBaseRateSrc_f);
                	sRateField = sBaseRateField;
                }
                else {
                    strSrcWhereSql = " and FExRateSrcCode=" +
                        dbl.sqlString(sBaseRateSrc_f);
                    sRateField = sBaseRateField_f;
                }

            } else if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_PORT)) { //取组合汇率
          
            	
                sCuryCode = (port == null ? "" : port.getCurrencyCode()); //获取组合中的货币代码
              
             //数据接口：导入券商交易数据的时候，获取汇率有问题 	
           	 if (sCuryCode.equalsIgnoreCase(pub.getPortBaseCury(sPortCode)) 
           			 && (
           					 (sPortRateSrc_f.trim().length() > 0 && sPortRateField_f.trim().length() > 0 ) 
           					 || 
           					 (sPortRateSrc_f.trim().length() == 0 && sPortRateField_f.trim().length() == 0 )
           				)
           		){
           		 return dReturn;
           	 }
                
                if (sPortRateSrc_f.trim().length() == 0 &&
                    sPortRateField_f.trim().length() == 0) { //如果传入了组合汇率来源和字段，就用传进来的，如果没有传入就用组合设置中的
                    if (sPortRateSrc.trim().length() > 0) {
                        strSrcWhereSql = " and FExRateSrcCode=" +
                            dbl.sqlString(sPortRateSrc);
                    }
                    sRateField = sPortRateField;                
                } else if(sPortRateSrc_f.trim().length() > 0 && sPortRateField_f.trim().length() == 0){
                	strSrcWhereSql = " and FExRateSrcCode=" +
                    dbl.sqlString(sPortRateSrc_f);
                	sRateField = sPortRateField;
                }
                else {
                    strSrcWhereSql = " and FExRateSrcCode=" +
                        dbl.sqlString(sPortRateSrc_f);
                    sRateField = sPortRateField_f;
                }
            }

            strSql = "select a.*, b.FInvertInd, b.FFactor " +
                " from (select FCuryCode,FPortCode," + sRateField + " from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 ";            
            //20121227 modified by liubo.Story #3380
            //外管局月报平均汇率的基准货币一定是美元，在这里写死
            //=================================
//            strSql = strSql +  " AND FMarkCury = '"+pub.getPortBaseCury(sPortCode)+"'";
            strSql = strSql +  " AND FMarkCury = 'USD' ";
            //================end=================
            
            strSql = strSql + " and FCuryCode=" + dbl.sqlString(sCuryCode) +
                " and FExRateDate = " +
                "(select max(FExRateDate) as FExRateDate from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 and FExRateDate <= " +
                "last_day(" + dbl.sqlDate(dRateDate) +") " + //每月只有一个汇率 取这个月最大日期
                " and FCuryCode=" + dbl.sqlString(sCuryCode);            
          //  if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_PORT)) {

            //20121227 modified by liubo.Story #3380
            //外管局月报平均汇率的基准货币一定是美元，在这里写死
            //=================================
//            	strSql = strSql +  " AND FMarkCury = '"+pub.getPortBaseCury(sPortCode)+"'";   
            	strSql = strSql +  " AND FMarkCury = 'USD'";  
           //================end=================
            	
          //  }           
               	 strSql = strSql +  strSrcWhereSql +
                " and (FPortCode=" +
                (sPortCode == null ? dbl.sqlString(" ") :
                 dbl.sqlString(sPortCode)) +
                " or FPortCode=' ')" +                
                " group by FCuryCode" +
                ")" + strSrcWhereSql;

            strSql = strSql + " and (FPortCode=" +
                (sPortCode == null ? dbl.sqlString(" ") :
                 dbl.sqlString(sPortCode)) +
                " or FPortCode=' ')";
            strSql = strSql +                
                "order by FPortCode desc) a left join " +
                " (select FCuryCode, FInvertInd, FFactor from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") b on a.FCuryCode = b.FCuryCode order by FPortCode desc";

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReturn = rs.getDouble(sRateField);
                sInvertInd = rs.getString("FInvertInd");
                dFactor = rs.getDouble("FFactor") == 0 ? 1 : rs.getDouble("FFactor");
                if (sInvertInd != null) {
                    if (sInvertInd.equals("1") && dReturn != 0) {
                        dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
                    }
                    if (sInvertInd.equals("0")) {
                        dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
                    }
                }
            } else {
                dbl.closeResultSetFinal(rs);
                strSql = "select FInitRate,FInvertInd, FFactor from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1"
                    + " and FCuryCode = " + dbl.sqlString(sCuryCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    dReturn = rs.getDouble("FInitRate");
                    sInvertInd = rs.getString("FInvertInd");
                    dFactor = rs.getDouble("FFactor") == 0 ? 1 :
                        rs.getDouble("FFactor");
                    if (sInvertInd != null) {
                        if (sInvertInd.equals("1") && dReturn != 0) {
                            dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
                        }
                        if (sInvertInd.equals("0")) {
                            dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
                        }
                    }
                }
            }
            hmCuryRate.put(sKey, dReturn + ""); //将值放入到hashMap中
            return dReturn;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    
    /**
     * add by huangqirong 2012-10-08 story #3120
     * 
     * */
    public double getCuryRate(java.util.Date dRateDate, String sBaseSrcRate, String sPortSrcRate, 
    						String sCuryCode, String sPortCode, String sRateType) throws YssException {
    	return getCuryRateByRateSrc(dRateDate, "", sCuryCode, sBaseSrcRate, "", sPortSrcRate, "", sPortCode, sRateType);
    }

    public double getCuryRate(java.util.Date dRateDate, String sBaseSrcRate,
                              String sBaseFieldRate,
                              String sPortSrcRate, String sPortFieldRate,
                              String sCuryCode, String sPortCode,
                              String sRateType) throws YssException {
        return getCuryRate(dRateDate, "", sCuryCode, sBaseSrcRate, sBaseFieldRate,
                           sPortSrcRate, sPortFieldRate, sPortCode, sRateType);
    }

    public double getCuryRate(java.util.Date dRateDate,
                              String sCuryCode, String sPortCode,
                              String sRateType) throws YssException {
        return getCuryRate(dRateDate, "", sCuryCode, "", "", "", "", sPortCode,
                           sRateType);
    }

    public double getCuryRate(java.util.Date dRateDate, String sRateTime,
                              String sCuryCode, String sPortCode,
                              String sRateType) throws YssException {
        return getCuryRate(dRateDate, sRateTime, sCuryCode, "", "", "", "",
                           sPortCode,
                           sRateType);
    }

//   public double getCuryRate(java.util.Date dRateDate,
//                             String sCuryCode, String sRateType) throws YssException {
//      return getCuryRate(dRateDate, "", sCuryCode, "",sRateType);
//   }

    /**
     * getCuryRate
     *
     * @param dRateDate Date
     * @param sRateSource String
     * @param sCuryCode String
     * @return double
     */
//   public double getCuryRate(java.util.Date dRateDate, String sRateSource,
//                             String sCuryCode, String sRateType) throws YssException {
//      return getCuryRate(dRateDate, "",  sCuryCode, "", sRateType);
//   }

    public double converMoney(String sSrcCuryCode, String sTagerCuryCode,
                              double dMoney, double dSrcCuryRate,
                              double dTagerCuryRate) throws
        YssException {
        return converMoney(sSrcCuryCode, sTagerCuryCode, dMoney, dSrcCuryRate,
                           dTagerCuryRate, 2);
    }

    /**
     * converMoney
     *
     * @param sSrcCury String
     * @param sTagerCury String
     * @param sBaseCury String
     * @param dMoney double
     * @param dCuryRate double
     * @return double
     */
    public double converMoney(String sSrcCuryCode, String sTagerCuryCode,
                              double dMoney, double dSrcCuryRate,
                              double dTagerCuryRate, int lDecs) throws
        YssException {
        double dBaseMoney = 0;
        double dReMoney = 0;
        CurrencyBean srcCury = new CurrencyBean();
        CurrencyBean tagerCury = new CurrencyBean();
        try {
            srcCury.setYssPub(pub);
            tagerCury.setYssPub(pub);
            srcCury.getCurySetting(sSrcCuryCode);
            tagerCury.getCurySetting(sTagerCuryCode);

//         dSrcCuryRate = YssD.div(dSrcCuryRate, srcCury.getFactor());
//         dTagerCuryRate = YssD.div(dTagerCuryRate,
//                                   tagerCury.getFactor());
            if (srcCury.getInvertInd() == 0) {
                dBaseMoney = YssD.mul(dMoney, dSrcCuryRate);
            } else if (srcCury.getInvertInd() == 1) {
                dBaseMoney = YssD.div(dMoney, dSrcCuryRate);
            }

            if (tagerCury.getInvertInd() == 0) {
                dReMoney = YssD.div(dBaseMoney, dTagerCuryRate);
            } else if (tagerCury.getInvertInd() == 1) {
                dReMoney = YssD.mul(dBaseMoney, dTagerCuryRate);
            }
            return YssD.round(dReMoney, lDecs);
        } catch (Exception e) {
            throw new YssException("货币金额转换出错");
        }
    }

   /**
    *
    * @param sSrcCuryCode String
    * @param sTagerCuryCode String
    * @param dMoney double
    * @param dDate Date
    * @param sPortCode String
    * @return double
    * @throws YssException
    * QDV4中保2009年05月04日02_B 默认的计算方法
    */
   public double converMoney(String sSrcCuryCode, String sTagerCuryCode,
                             double dMoney, java.util.Date dDate,
                             String sPortCode) throws YssException {
      return converMoney(sSrcCuryCode, sTagerCuryCode, dMoney, dDate, sPortCode, 2);
   }


   /**
    *
    * @param sSrcCuryCode String
    * @param sTagerCuryCode String
    * @param dMoney double
    * @param dDate Date
    * @param sPortCode String
    * @param scale int
    * @return double
    * @throws YssException
    * QDV4中保2009年05月04日02_B  MS00431 调整参数，增加一个精度控制的参数
    */
    public double converMoney(String sSrcCuryCode, String sTagerCuryCode,
                              double dMoney, java.util.Date dDate,
                              String sPortCode, int scale) throws//添加scale参数，合并太平版本代码
        YssException {
        double dSrcCuryRate = 1;
        double dTagerCuryRate = 1;
        if (sSrcCuryCode.equalsIgnoreCase(sTagerCuryCode)) { //如果原币等于转换货币，那么不进行转换  胡昆  20070919
            return dMoney;
        }
        dSrcCuryRate = this.getCuryRate(dDate, sSrcCuryCode, sPortCode,
                                        YssOperCons.YSS_RATE_BASE);
        //    dTagerCuryRate = this.getCuryRate(dDate,sTagerCuryCode,sPortCode,YssOperCons.YSS_RATE_BASE);
        dTagerCuryRate = this.getCuryRate(dDate, sTagerCuryCode, sPortCode,
                                          YssOperCons.YSS_RATE_PORT); //本币，杨文奇0925
      	//此处需要基础类型汇率 QDV4中保2009年05月04日02_B 合并太平版本代码
      	//QDV4中保2009年05月04日02_B  MS00431 增加返回数据的精度控制 scale -------------
        return converMoney(sSrcCuryCode, sTagerCuryCode, dMoney, dSrcCuryRate,
                           dTagerCuryRate, scale);
    }

    /**
     * 重载了计算方法.sj edit 20080807
     * @param dMoney double
     * @param dBaseRate double
     * @param digit int
     * @throws YssException
     * @return double
     */
    public double calBaseMoney(double dMoney, double dBaseRate, int digit) throws
        YssException {
        double dResult = 0;
        if (dBaseRate == 0) {
            dBaseRate = 1;
        }
        dResult = YssD.round(YssD.mul(dMoney, dBaseRate), digit);
        return dResult;
    }

    public double calBaseMoney(double dMoney, double dBaseRate) throws
        YssException {
        double dResult = 0;
        if (dBaseRate == 0) {
            dBaseRate = 1;
        }
        dResult = YssD.round(YssD.mul(dMoney, dBaseRate), 2);
        return dResult;
    }

    /**
     * 计算组合货币金额
     * 2008-10-21 蒋锦 添加
     * BUG：0000487
     * 在计算 原币 * 基础汇率 时得到的结果有可能超出 double 型变量的最大精度，此方法使用 BigDecimal 类型计算中间结果
     * @param dMoney double：原币金额
     * @param dBaseRate double：基础汇率
     * @param dPortRate double：组合汇率
     * @return double：组合货币金额
     * @throws YssException
     */
//   public double calPortMoney(double dMoney, double dBaseRate, double dPortRate) throws
//         YssException {
//      double dResult;
//      BigDecimal bigPortRate;
//      if (dBaseRate == 0) {
//         dBaseRate = 1;
//      }
//      if (dPortRate == 0) {
//         dPortRate = 1;
//      }
//      bigPortRate = new BigDecimal(Double.toString(dPortRate));
//      dResult = YssD.round(YssD.divD(YssD.mulD(dMoney, dBaseRate), bigPortRate),
//                           2);
//      return dResult;
//   }

    /**
     * 增加判断是否通过直接获取的方式计算组合货币的成本
     * 2008-11-24 linjunyun bug:Ms00011
     *
     * @param dMoney double:原币金额
     * @param dBaseRate double:基础汇率
     * @param dPortRate double:组合汇率
     * @param curyCode String:原币代码
     * @param exRateDate String:汇率日期
     * @param PortCode String:组合代码
     * @return double:组合货币金额
     * @throws YssException:
     */
    public double calPortMoney(double dMoney, double dBaseRate, double dPortRate,
                               String sCuryCode, java.util.Date exRateDate,
                               String sPortCode) throws YssException {
        return calPortMoney(dMoney, dBaseRate, dPortRate, sCuryCode, exRateDate,
                            sPortCode, 2);
    }

    /**
     * 增加判断是否通过直接获取的方式计算组合货币的成本
     * 本位币金额 = 原币金额 * 汇率 (报价方向为“正常”)XtoOne  1原币=X基准货币
     * 本位币金额 = 原币金额/汇率   (报价方向为“反转”)OnetoX  1基准货币=X原币
     * 2008-11-24 linjunyun bug:Ms00011
     *
     * @param dMoney double 原币金额
     * @param dBaseRate double 基础汇率
     * @param dPortRate double 组合汇率
     * @param sCuryCode String 原币代码
     * @param exRateDate Date 汇率日期
     * @param sPortCode String 组合代码
     * @param digit int 保留精度
     * @return double 组合货币金额
     * @throws YssException
     */
    public double calPortMoney(double dMoney, double dBaseRate, double dPortRate,
                               String sCuryCode, java.util.Date exRateDate,
                               String sPortCode, int digit) throws YssException {
        double dResult = 0;
        String sDirectPort = "", sQuoteWay = "";
        String sKey = "";
        BigDecimal bigPortRate, bigMoney;

        sKey = YssFun.formatDate(exRateDate, "yyyy-MM-dd") + "\f\f" +
            sCuryCode + "\f\f" + sPortCode + "\f\f" + YssOperCons.YSS_RATE_MARK;

        if (hmDirectCury.get(sKey) != null) {
            sDirectPort = String.valueOf(hmDirectCury.get(sKey));
        }

        //2008-11-24 linjunyun bug:Ms00011 判断组合货币的金额是否通过直接获取方式计算
        if (!YssCons.YSS_PASSAGESPLITMARK.equalsIgnoreCase(sDirectPort) &&
            (!"".equalsIgnoreCase(sDirectPort) || isDirectWay(sCuryCode, sKey))) {
            if ("".equalsIgnoreCase(sDirectPort)) {
                sDirectPort = getDirectPort(exRateDate, "", sCuryCode, "", "", "",
                                            "",
                                            sPortCode,
                                            YssOperCons.YSS_RATE_MARK);
            }

            String[] str = sDirectPort.split(YssCons.YSS_PASSAGESPLITMARK);
            if (str != null && str.length > 1) {
                dPortRate = YssFun.toDouble(str[0]);
                sQuoteWay = str[1];
                bigPortRate = new BigDecimal(Double.toString(dPortRate));
                bigMoney = new BigDecimal(Double.toString(dMoney));
                if (sQuoteWay.equalsIgnoreCase("XtoOne") && dPortRate != 0) {
                    dResult = YssD.round(YssD.mulD(bigMoney, bigPortRate), digit);
                }
                if (sQuoteWay.equalsIgnoreCase("OnetoX")) {
                    dResult = YssD.round(YssD.divD(bigMoney, bigPortRate), digit);
                }
                return dResult;
            }
        }

        if (dBaseRate == 0) {
            dBaseRate = 1;
        }
        if (dPortRate == 0) {
            dPortRate = 1;
        }
        bigPortRate = new BigDecimal(Double.toString(dPortRate));
        dResult = YssD.round(YssD.divD(YssD.mulD(dMoney, dBaseRate), bigPortRate),
                             digit);
        return dResult;
    }

    /**
     * 判断是否是通过直接获取利率的方式来计算组合货币成本
     * 2008-11-24 linjunyun bug:Ms00011
     *
     * @param sCuryCode String 原币代码
     * @return boolean
     * @throws YssException
     */
    public boolean isDirectWay(String sCuryCode, String sKey) throws //将其改为公共方法,以便在如凭证生成时调用.
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String errorInfo = "查询汇率获取方式时出错！"; //定义错误提示信息
        String rateWay = "";

        try {
            strSql = "select p.FRateWay from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " p where p.fcheckstate = 1 and p.fcurycode = " +
                dbl.sqlString(sCuryCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                rateWay = rs.getString("FRateWay");
            }

            if ("1".equalsIgnoreCase(rateWay)) {
                return true;
            } else {
                hmDirectCury.put(sKey, YssCons.YSS_PASSAGESPLITMARK + ""); //将值放入到hashMap中
            }
        } catch (SQLException se) {
            throw new YssException(errorInfo, se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return false;
    }

    /**
     * 汇率 = 汇率/报价因子
     * 本位币金额 = 原币金额 * 汇率 (报价方向为“正常”)
     * 本位币金额 = 原币金额/汇率   (报价方向为“反转”)
     * 2008-11-24 linjunyun bug:Ms00011
     *
     * @param dRateDate Date 汇率日期
     * @param sRateTime String 汇率时间
     * @param sCuryCode String 货币代码
     * @param sBaseRateSrc_f String
     * @param sBaseRateField_f String
     * @param sPortRateSrc_f String
     * @param sPortRateField_f String
     * @param sPortCode String 组合代码
     * @param sRateType String 汇率类型
     * @param dMoney double 原币成本
     * @return double
     * @throws YssException
     */
    public String getDirectPort(java.util.Date dRateDate, String sRateTime,
                                String sCuryCode, String sBaseRateSrc_f,
                                String sBaseRateField_f,
                                String sPortRateSrc_f,
                                String sPortRateField_f,
                                String sPortCode, String sRateType) throws
        YssException { //改为公共方法，使其他类可以调用 20081204
        String sKey = "";
        String strSql = "";
        String strSrcWhereSql = "";
        ResultSet rs = null;
        String strError = "获取汇率行情出错";
        double dReturn = 1;
        double dFactor = 1;
        String sQuoteWay = "";
        String sPortRateSrc = "01";
        String sPortRateField = "FExRate1";
        PortfolioBean port = null;
        String sRateField = "";
        StringBuffer buf = new StringBuffer();

        try {
            sKey = YssFun.formatDate(dRateDate, "yyyy-MM-dd") + "\f\f" + sCuryCode
                + "\f\f" + sPortCode + "\f\f" + sRateType;
            //将外部传进来的条件值转成一个Key，存入hashmap中
            if (hmDirectCury.get(sKey) != null) {
                return String.valueOf(hmDirectCury.get(sKey));
            }

            sPortRateSrc = pub.getPortRateSrcCode();
            sPortRateField = pub.getPortRateCode(); //默认取组合群设置中的组合汇率来源和汇率字段
            if (sPortCode != null && sPortCode.length() > 0) { //如果传入了组合，就取组合设置中的基础汇率来源和汇率字段
                port = new PortfolioBean();
                port.setYssPub(pub);
                port.setPortCode(sPortCode);
                port.getSetting();
                if (port.getPortRateSrcCode() != null &&
                    port.getPortRateSrcCode().trim().length() > 0 &&
                    !port.getPortRateSrcCode().equalsIgnoreCase("null")) {
                    sPortRateSrc = port.getPortRateSrcCode();
                }
                if (port.getPortRateCode() != null &&
                    port.getPortRateCode().trim().length() > 0 &&
                    !port.getPortRateCode().equalsIgnoreCase("null")) {
                    sPortRateField = port.getPortRateCode();
                }
            }

//        sCuryCode = (port == null ? "" : port.getCurrencyCode()); //20071012  chenyibo   获取组合中的货币代码
            if (sCuryCode == null || "".equalsIgnoreCase(sCuryCode.trim())) {
                sCuryCode = (port == null ? "" : port.getCurrencyCode());
            }

            if (sPortRateSrc_f.trim().length() == 0 &&
                sPortRateField_f.trim().length() == 0) { //如果传入了组合汇率来源和字段，就用传进来的，如果没有传入就用组合设置中的
                if (sPortRateSrc.trim().length() > 0) {
                    strSrcWhereSql = " and FExRateSrcCode=" +
                        dbl.sqlString(sPortRateSrc);
                }
                sRateField = sPortRateField;
            } else {
                strSrcWhereSql = " and FExRateSrcCode=" +
                    dbl.sqlString(sPortRateSrc_f);
                sRateField = sPortRateField_f;
            }

            strSql = "select a.*, b.FQuoteWay, b.FFactor " +
                " from (select FCuryCode,FPortCode," + sRateField +
                " ,FMarkCury from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                //在直接获取汇率的情况下，基准货币等于组合货币
                " where FCheckState=1 and FMarkCury = " +
                (port == null ? dbl.sqlString(sCuryCode) :
                 dbl.sqlString(port.getCurrencyCode()));
            strSql = strSql + " and FCuryCode=" + dbl.sqlString(sCuryCode) +
                " and FExRateDate = " +
                "(select max(FExRateDate) as FExRateDate from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 and FExRateDate <= " +
                dbl.sqlDate(dRateDate) +
                //MS00905 QD招商证券2010年1月3日01_B fanghaoln 20100310
				" and FMarkCury = " +//增加一个在直接获取汇率的情况下，基准货币等于组合货币的条件。防止查出的汇率和外围的汇率不一致的情况
				(port == null ? dbl.sqlString(sCuryCode) ://导致查不出汇率
				 dbl.sqlString(port.getCurrencyCode())) +//在查对应汇率最近日期时也要加上基准货币这个查询条件
                 //-----------------------end----MS00905-------------------------
                " and FCuryCode=" + dbl.sqlString(sCuryCode) +
                strSrcWhereSql +
                " and (FPortCode=" +
                (sPortCode == null ? dbl.sqlString(" ") :
                 dbl.sqlString(sPortCode)) +
                " or FPortCode=' ')" +
                /* 彭彪20070929 先不考虑时间
                       " and FExRateTime=" +
                                (sRateTime.length() == 0 ? "'00:00:00'" :
                 //既有维护组合代码也有没有维护组合代码的就会存在有问题，会取出多条记录，导致报错fazmm20070906
                 //dbl.sqlString(sRateTime)) + " group by FCuryCode,FPortCode" +
                 dbl.sqlString(sRateTime)) +*/
                " group by FCuryCode" +
                ")" + strSrcWhereSql;

            strSql = strSql + " and (FPortCode=" +
                (sPortCode == null ? dbl.sqlString(" ") :
                 dbl.sqlString(sPortCode)) +
                " or FPortCode=' ')";
            strSql = strSql +
                /*彭彪20070929 先不考虑时间
                        " and FExRateTime=" +
                        (sRateTime.length() == 0 ? "'00:00:00'" :
                         dbl.sqlString(sRateTime)) +*/
                "order by FPortCode desc) a left join " +
                " (select FCuryCode, FQuoteWay, FFactor, FMarkCury from " +
                pub.yssGetTableName("Tb_Para_CurrencyWay") +
                ") b on a.FCuryCode = b.FCuryCode" +
                " and a.FMarkCury = b.FMarkCury" +
                " order by FPortCode desc"; //2008-6-1 单亮 添加 order by FPortCode desc

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReturn = rs.getDouble(sRateField);
                sQuoteWay = rs.getString("FQuoteWay");
                dFactor = rs.getDouble("FFactor") == 0 ? 1 : rs.getDouble("FFactor");
                if (sQuoteWay != null) {
                    dReturn = YssD.div(dReturn, dFactor);
                    buf.append(dReturn);
                    buf.append(YssCons.YSS_PASSAGESPLITMARK);
                    buf.append(sQuoteWay);
//              if (sQuoteWay.equalsIgnoreCase("OnetoX") && dReturn != 0) {
//                 dReturn = YssD.round(YssD.mulD(dMoney, dReturn), 2);
//              }
//              if (sQuoteWay.equalsIgnoreCase("XtoOne")) {
//                 dReturn = YssD.round(YssD.div(dMoney, dReturn), 2);
//              }
                }
            } else {
                buf.append(YssCons.YSS_PASSAGESPLITMARK);
                if (port != null &&
                    !port.getCurrencyCode().equalsIgnoreCase(sCuryCode)) {
                    throw new YssException("获取不到日期为" +
                                           YssFun.formatDate(dRateDate, "yyyy-MM-dd") +
                                           "或最近一日的汇率资料，基准货币：" +
                                           port.getCurrencyCode() + "交易货币：" +
                                           sCuryCode +
                                           "\r\n" + "请检查币种及货币方向设置！");
                }
            }

            hmDirectCury.put(sKey, buf.toString() + ""); //将值放入到hashMap中
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 重载了计算方法.sj 20080807
     * @param dMoney double
     * @param dBaseRate double
     * @param dPortRate double
     * @param digit int
     * @throws YssException
     * @return double
     */
//   public double calPortMoney(double dMoney, double dBaseRate, double dPortRate,
//                              int digit) throws
//         YssException {
//      double dResult = 0;
//      BigDecimal bigPortRate;
//      if (dBaseRate == 0) {
//         dBaseRate = 1;
//      }
//      if (dPortRate == 0) {
//         dPortRate = 1;
//      }
//      bigPortRate = new BigDecimal(Double.toString(dPortRate));
//      dResult = YssD.round(YssD.divD(YssD.mulD(dMoney, dBaseRate), bigPortRate),
//                           digit);
//      return dResult;
//   }


    public String chooseAnalyVar(String sStorageType, String sAnalysisInd,
                                 String sAnalyVar1, String sAnalyVar2,
                                 String sAnalyVar3) throws YssException {
        String sField = "";
        String sInd = "";
        String sResult = "";
        try {
            sField = getStorageAnalysisField(sStorageType, sAnalysisInd);
            sInd = YssFun.right(sField, 1);
            if (sInd.equalsIgnoreCase("1")) {
                sResult = sAnalyVar1;
            } else if (sInd.equalsIgnoreCase("2")) {
                sResult = sAnalyVar2;
            } else if (sInd.equalsIgnoreCase("3")) {
                sResult = sAnalyVar3;
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    /**
     * getStorageAnalysisField
     *
     * @param sStroageType String
     * @param sAnalysisInd String
     * @return String
     */
    public String getStorageAnalysisField(String sStorageType,
                                          String sAnalysisInd) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        try {
            strSql = "select " + dbl.sqlIsNull("FAnalysisCode1", "''") +
                " as FAnalysisCode1, " +
                dbl.sqlIsNull("FAnalysisCode2", "''") +
                " as FAnalysisCode2, " + dbl.sqlIsNull("FAnalysisCode3", "''") +
                " as FAnalysisCode3 from " +
                pub.yssGetTableName("tb_para_storagecfg") +
                " where FStorageType = " + dbl.sqlString(sStorageType) +
                " and FCheckState= 1"; // lzp 20080123 add
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                for (int i = 1; i <= 3; i++) {
                    if ( (rs.getString("FAnalysisCode" + i) +
                          "").equalsIgnoreCase(sAnalysisInd)) {
                        sResult = "FAnalysisCode" + i;
                        break;
                    }
                }
            }
            return sResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("获取库存辅助字段出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 2009-07-09 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A
     * 获取开始日期到结束日期之间工作日天数(不含开始日期当天)
     * @param beginDate Date：开始日期
     * @param endDate Date：结束日期
     * @param sHoildayCode String：节假日代码
     * @param iTag int: 0-头尾均记，1-记头不计尾，2-记尾不记头
     * @return int：开始日期到结束日期之间的工作日天数
     * @throws YssException
     */
    public int workDateDiff(java.util.Date beginDate,
                                   java.util.Date endDate,
                                   String sHoildayCode,
                                   int iTag) throws YssException {
        int iDays = 0;
        int iHoildays = 0;
        ResultSet rs = null;
        String sqlStr = "";
        try {
            //获取开始日期到结束日期之间的自然日天数
            iDays = YssFun.dateDiff(beginDate, endDate);
            if(iTag == 0){
                iDays += 1;
            }
            sqlStr = "SELECT COUNT(FHolidaysCode) AS FDays FROM Tb_Base_ChildHoliday WHERE FCheckState = 1" +
                " AND FHolidaysCode = " + dbl.sqlString(sHoildayCode);
            if(iTag == 0){
                sqlStr += " AND FDate >= " + dbl.sqlDate(beginDate) +
                    " AND FDate <= " + dbl.sqlDate(endDate);
            } else if(iTag == 1){
                sqlStr += " AND FDate >= " + dbl.sqlDate(beginDate) +
                    " AND FDate < " + dbl.sqlDate(endDate);
            } else {
                sqlStr += " AND FDate > " + dbl.sqlDate(beginDate) +
                    " AND FDate <= " + dbl.sqlDate(endDate);
            }
            rs = dbl.openResultSet(sqlStr);
            if(rs.next()){
                //开始日期到结束日期之间的节假日天数
                iHoildays = rs.getInt("FDays");
            }
            iDays -= iHoildays;
        } catch (Exception ex) {
            throw new YssException("获取工作日天数出错！", ex);
        }
        return iDays;
    }

    /**
     * getWorkDay
     *
     * @param getHolidaysCode String   //节假日群代码
     * @dDate Date                     //传入日期
     * @lOffset int                    //传入延迟天数
     * @return Date
     */
    public java.util.Date getWorkDay(String getHolidaysCode,
                                     java.util.Date dDate, int lOffset) throws
        YssException {
    	
    	SecurityBean secBean = new SecurityBean();
        secBean.setYssPub(pub);
        secBean.getSetting();
        String strSql = "";
        ResultSet rs = null;
        int lTmp = 0, lStep;
        try {
        	//------ add by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
        	checkHoliday(dDate,getHolidaysCode);
        	//----------- BUG #723 ------------//
            lStep = (lOffset < 0) ? -1 : 1;
            strSql =
                "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " +
                //加上" and FCheckState=1"使查出的节假日是已审核的,没有审核提示错误
                //MS00178 QDV4赢时胜上海2009年1月8日01_B 2009.01.24 方浩
                dbl.sqlString(getHolidaysCode) + " and FCheckState=1" + " and FDate " +
                //------------------------------------------------------------------
                ( (lOffset < 0) ? "<=" : ">=")
                + dbl.sqlDate(dDate, false) + " order by FDate " +
                ( (lOffset < 0) ? " desc" : "");
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
                do {
                	/*add by zhaoxianlin 20130419 STORY #3445 TA读数时判断下一年节假日是否有设置 start*/
                	if(rs.isAfterLast()){
                		if (Math.abs(lTmp) >= Math.abs(lOffset)) {
                            break;
                        }
                        lTmp += lStep;
                	}else{
                   /*add by zhaoxianlin 20130419 STORY #3445 TA读数时判断下一年节假日是否有设置 end*/
                		if (YssFun.dateDiff(rs.getDate("FDate"), dDate) == 0) {
                            rs.next();
                            if (lTmp == 0) {
                                lTmp += lStep;
                            }
                        } else {
                            if (Math.abs(lTmp) >= Math.abs(lOffset)) {
                                break;
                            }
                            lTmp += lStep;
                        }
                	}
                    dDate = YssFun.addDay(dDate, lStep);
                    if(Math.abs(lTmp) >= Math.abs(lOffset)){
                    	 if (rs.isAfterLast()){
                         	break;
                         }
                    }
                    /*modified by zhaoxianlin 20130419 STORY #3445 TA读数时判断下一年节假日是否有设置start */
                    //------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
//                    if (rs.isAfterLast()){
//                    	break;
//                    }
                    //------------------------- BUG #723 ----------------------------//
                    
//                } while (!rs.isAfterLast());
                  } while (1==1);// add by zhaoxianlin 循环条件设置为true
                /*modified by zhaoxianlin 20130419 STORY #3445 TA读数时判断下一年节假日是否有设置end */
                //------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
            	checkHoliday(dDate,getHolidaysCode);
            	
                /**
                if (rs.isAfterLast()) {
                    throw new YssException( ( (lOffset < 0) ? "上" : "下") +
                                           "一个工作日已经超越节假日的边界，请增加节假日定义！");
                }
                **/
            	//------------------------- BUG #723 ----------------------------//
            }
            //BugNo:0000464  edit by jc
            else {
            	 //------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
            	dDate = YssFun.addDay(dDate, lOffset);
            	checkHoliday(dDate,getHolidaysCode);
                //throw new YssException(" 没有节假日信息，请增加节假日定义！");
            	//------------------------- BUG #723 ----------------------------//
            }
            //-----------------------jc
            return dDate;
        } catch (Exception e) {
            throw new YssException(e.getMessage() /*"访问节假日表出错！"*/);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	//add by luopc STORY #1434 
	public java.util.Date getWorkday(String getHolidaysCode,
			java.util.Date dDate, int lOffset) throws YssException {

		SecurityBean secBean = new SecurityBean();
		secBean.setYssPub(pub);
		secBean.getSetting();
		String strSql = "";
		ResultSet rs = null;
		int lTmp = 0, lStep;
		try {

			// ------ add by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错
			checkHoliday(dDate, getHolidaysCode);
			// ----------- BUG #723 ------------//
			lStep = (lOffset <= 0) ? -1 : 1;//modify by luopc
			strSql = "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = "
					+
					// 加上" and FCheckState=1"使查出的节假日是已审核的,没有审核提示错误
					// MS00178 QDV4赢时胜上海2009年1月8日01_B 2009.01.24 方浩
					dbl.sqlString(getHolidaysCode)
					+ " and FCheckState=1"
					+ " and FDate "
					+
					// ------------------------------------------------------------------
					((lOffset <= 0) ? "<=" : ">=")//modify by luopc
					+ dbl.sqlDate(dDate, false)
					+ " order by FDate " + ((lOffset <= 0) ? " desc" : "");//modify by luopc
			rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (rs.next()) {
				do {
					if (YssFun.dateDiff(rs.getDate("FDate"), dDate) == 0) {
						rs.next();
						if (lTmp == 0) {
							lTmp += lStep;
						}
					} else {
						if (Math.abs(lTmp) >= Math.abs(lOffset)) {
							break;
						}
						lTmp += lStep;
					}
					dDate = YssFun.addDay(dDate, lStep);
					// ------ modify by wangzuochun 2010.01.13 BUG #723
					// 读取TA赎回数据时提示获取结算日出错
					if (rs.isAfterLast()) {
						break;
					}
					// ------------------------- BUG #723
					// ----------------------------//

				} while (!rs.isAfterLast());

				// ------ modify by wangzuochun 2010.01.13 BUG #723
				// 读取TA赎回数据时提示获取结算日出错
				checkHoliday(dDate, getHolidaysCode);

				/**
				 * if (rs.isAfterLast()) { throw new YssException( ( (lOffset <
				 * 0) ? "上" : "下") + "一个工作日已经超越节假日的边界，请增加节假日定义！"); }
				 */
				// ------------------------- BUG #723
				// ----------------------------//
			}
			// BugNo:0000464 edit by jc
			else {
				// ------ modify by wangzuochun 2010.01.13 BUG #723
				// 读取TA赎回数据时提示获取结算日出错
				dDate = YssFun.addDay(dDate, lOffset);
				checkHoliday(dDate, getHolidaysCode);
				// throw new YssException(" 没有节假日信息，请增加节假日定义！");
				// ------------------------- BUG #723
				// ----------------------------//
			}
			// -----------------------jc
			return dDate;
		} catch (Exception e) {
			throw new YssException(e.getMessage() /*"访问节假日表出错！"*/);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

    /**
     * getWorkDay
     *
     * @param sSecurityCode String
     * @return Date
     */
    public java.util.Date getWorkDay(String sSecurityCode, java.util.Date dDate) throws
        YssException {
        SecurityBean secBean = new SecurityBean();
        secBean.setYssPub(pub);
        secBean.setSecurityCode(sSecurityCode);
        secBean.getSetting();
        String strSql = "";
        ResultSet rs = null;
        int lTmp = 0, lStep;
        int lOffset = 0;
        try {
        	
        	//------ add by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
        	checkHoliday(dDate,secBean.getHolidaysCode());
        	//----------- BUG #723 ------------//
        	
            lOffset = secBean.getSettleDays();
            if (secBean.getSettleDayType().equalsIgnoreCase("0")) {
                lStep = (lOffset < 0) ? -1 : 1;
                strSql =
                    "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " +
                    dbl.sqlString(secBean.getHolidaysCode()) + " and FDate " +
                    ( (lOffset < 0) ? "<=" : ">=")
                    + dbl.sqlDate(dDate, false) + " order by FDate " +
                    ( (lOffset < 0) ? " desc" : "");
                rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
                if (rs.next()) {
                    do {
                        if (YssFun.dateDiff(rs.getDate("FDate"), dDate) == 0) {
//            YssFun.formatDate(Rs.getDate("FDate"),
//                                     "yyyy-MM-dd").equalsIgnoreCase(YssFun.formatDate(
//                                          dDate, "yyyy-MM-dd"))){
                            rs.next();
                            if (lTmp == 0) {
                                lTmp += lStep;
                            }
                        } else {
                            if (Math.abs(lTmp) >= Math.abs(lOffset)) {
                                break;
                            }
                            lTmp += lStep;
                        }
                        dDate = YssFun.addDay(dDate, lStep);
                        //------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
                        if (rs.isAfterLast()){
                        	break;
                        }
                        //------------------------- BUG #723 ----------------------------//
                    } while (!rs.isAfterLast());
                    
                    //------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
                	checkHoliday(dDate,secBean.getHolidaysCode());
                	
                    /**
                    if (rs.isAfterLast()) {
                        throw new YssException( ( (lOffset < 0) ? "上" : "下") +
                                               "一个工作日已经超越节假日的边界，请增加节假日定义！");
                    }
                    **/
                	//------------------------- BUG #723 ----------------------------//
                }
                else {
               	 	//------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
                	dDate = YssFun.addDay(dDate, lOffset);
                	checkHoliday(dDate,secBean.getHolidaysCode());
                	//throw new YssException(" 没有节假日信息，请增加节假日定义！");
                	//------------------------- BUG #723 ----------------------------//
               }
            } else if (secBean.getSettleDayType().equalsIgnoreCase("1")) {
                dDate = YssFun.addDay(dDate, secBean.getSettleDays());
            }
            return dDate;
        } catch (Exception e) {
            throw new YssException(e.getMessage()); //------ modify by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     *判断是否为工作日
     *
     */
    public boolean isWorkDay(String holidaycode, java.util.Date date,int i) throws
        YssException {
        int days=YssFun.dateDiff(date,getWorkDay(holidaycode, date,i));
        if(days == 0){
        	return true;
        }else{
        	return false;
        }
    }
    

    /**
     * getInterest
     *
     * @param sSecurityCode String
     * @param dAmount double
     * @param dDate Date
     * @return double
     */
    public double getInterest(String sSecurityCode, double dAmount,
                              java.util.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double interest = 0.0;
        double fIntAccPer100 = 0.0;
        double factor = 0.0;

        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " d," + pub.yssGetTableName("Tb_Para_Security") +
                " p where d.FSecurityCode=p.fsecuritycode and d.FSecurityCode=" +
                dbl.sqlString(sSecurityCode) +
                " and FRecordDate= " + dbl.sqlDate(dDate);
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
                fIntAccPer100 = rs.getDouble("FIntAccPer100");
                factor = rs.getDouble("Ffactor");
            }
            if (factor != 0.0) {
                interest = YssD.round(YssD.div(YssD.mul(fIntAccPer100, dAmount),
                                               factor), 2);
            }
//        System.out.println(factor+ "    ");
//        System.out.println("interest:" + interest);

        } catch (Exception e) {
            System.out.println(e.toString());
            throw new YssException("访问债券利息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return interest;
    }

    /**
     * getPeriodYearDay
     *获取期间的年天数
     * @param sPeriodCode String
     * @return int
     */
    //胡昆  20070730
    public int getPeriodYearDay(String sPeriodCode) throws YssException {
        PeriodBean period = new PeriodBean();
        period.setYssPub(pub);
        period.setPeriodCode(sPeriodCode);
        period.getSetting();
        return period.getDayOfYear();
    }

    /**
     * sj 20071121 获取计算利息的bean
     * @param SecurityCode String
     * @param insType String
     * @throws YssException
     * @return BaseBondOper
     */
    public BaseBondOper getSpringRe(String SecurityCode, String insType) throws
        YssException {
        BaseBondOper bondOper = null;
        String FixInterestCode = SecurityCode;
        String FixInterestInsType = insType;
        String sqlStr = "";
        String sqlTypeStr = "";
        ResultSet rs = null;
        try {
            if (FixInterestInsType != null && FixInterestInsType.length() > 0) {
                sqlTypeStr = " left join (select FCIMCode,FCIMName,FCIMType,FFormula,FSPICode from Tb_Base_CalcInsMetic where FCheckState = 1) b on ";
                if (FixInterestInsType.equalsIgnoreCase("Day")) {
                    sqlTypeStr = sqlTypeStr + " a.FCalcInsMeticDay = b.FCIMCode ";
                } else if (FixInterestInsType.equalsIgnoreCase("Buy")) {
                    sqlTypeStr = sqlTypeStr + " a.FCalcInsMeticBuy = b.FCIMCode ";
                } else if (FixInterestInsType.equalsIgnoreCase("Sell")) {
                    sqlTypeStr = sqlTypeStr + " a.FCalcInsMeticSell = b.FCIMCode ";
                } else if (FixInterestInsType.equalsIgnoreCase("ValPrice")) {
                    sqlTypeStr = sqlTypeStr + " a.FCalcPriceMetic = b.FCIMCode ";
                } else if (FixInterestInsType.equalsIgnoreCase("Premium")) { // add by leeyu
                    sqlTypeStr = sqlTypeStr + " a.FAmortization = b.FCIMCode ";
                }
                /**shashijie 2012-1-19 STORY 1713*/
                else if (FixInterestInsType.equalsIgnoreCase("FTaskMoneyCode")) {//百元派息金额
                	sqlTypeStr = sqlTypeStr + " a.FTaskMoneyCode = b.FCIMCode ";
				}
				/**end*/
            }
            sqlStr = "select c.FBeanId from " +
                "(select * from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FCheckState = 1 and FSecurityCode = " +
                dbl.sqlString(FixInterestCode) + " ) a " +
                sqlTypeStr +
                " left join (select FSICode,FBeanId from TB_FUN_SPINGINVOKE) c on c.FSICode = b.FSPICode"; //alter by sunny
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                if (rs.getString("FBeanId") != null) { //alter by sunny 2007 11 26
                    bondOper = (BaseBondOper) pub.getOperDealCtx().getBean(rs.
                        getString("FBeanId"));
                }
            }
            return bondOper;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new YssException("获取Spring调用出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**shashijie 2012-1-19 STORY 1713 获取spring调用的beanId这里不需要债券是审核状态 */
    public BaseBondOper getSpringBeanId(String SecurityCode, String Where) throws YssException {
    	ResultSet rs = null;
    	BaseBondOper bondOper = null;//债券业务公共类
		try {
			String sqlStr = "Select c.Fbeanid "+
				  " From (Select * From "+pub.yssGetTableName("Tb_Para_Fixinterest")+
				  " Where Fsecuritycode = "+dbl.sqlString(SecurityCode)+" ) a "+
				  " Left Join (Select Fcimcode, Fcimname, Fcimtype, Fformula, Fspicode "+
				  " From "+pub.yssGetTableName("Tb_Base_Calcinsmetic")+
				  " Where Fcheckstate = 1) b On "+Where+
				  " Left Join (Select Fsicode, Fbeanid From "+pub.yssGetTableName("Tb_Fun_Spinginvoke")+
				  " ) c On c.Fsicode = b.Fspicode ";
			rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                if (rs.getString("FBeanId") != null) {
                    bondOper = (BaseBondOper) pub.getOperDealCtx().getBean(rs.getString("FBeanId"));
                }
            }
            return bondOper;
		} catch (Exception e) {
			throw new YssException("获取Spring调用出错！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

    /**
     * 获取舍入代码设置的小数位。
     * @param sRoundCode String
     * @throws YssException
     * @return int
     */
    public int getRoundDigit(String sRoundCode) throws YssException {
        int Digit = -1;
        try {
            if (sRoundCode.equalsIgnoreCase("")) {
                Digit = 2;
            }
            RoundingBean round = new RoundingBean();
            round.setYssPub(pub);
            round.setRoundCode(sRoundCode);
            round.getSetting();
            if (round.getRoundName().length() == 0) {
                throw new YssException("舍入代码【" + sRoundCode + "】被反审核或被删除");
            } else {
                Digit = round.getRoundDigit();
            }
        } catch (Exception e) {
            throw new YssException("获取小数位数出错！", e);
        }
        return Digit;
    }

    /**
     * 获取输入数值的小数位数。
     * @param dMoney double
     * @throws YssException
     * @return int
     */
    public int getRoundDigit(double dMoney) throws YssException {
        int Digit = 0;
        String sMoney = "";
        try {
            sMoney = YssFun.formatNumber(dMoney, "#,##0.####");
            int digits = sMoney.indexOf('.');
            if (digits < 0) { //若为整数，则没有"."。默认为2位小数。
                Digit = 2;
            } else {
                Digit = sMoney.length() - digits - 1;
            }
        } catch (Exception e) {
            throw new YssException("获取小数位数出错！", e);
        }
        return Digit < 2 ? 2 : Digit; //最小保留位数位2位小数。
    }

    /**
     * 截取给定位数的数值。
     * @param d double
     * @param scale int
     * @return double
     */
    public static double cutDigit(double d, int scale) {
        long temp = 1;
        for (int i = scale; i > 0; i--) {
            temp *= 10;
        }
        d *= temp;
        long intValue = (long) d; //强制转换，只取整数部分。 edit by zhangjun 2012-01-05
        return YssD.div(intValue, temp);
    }

    /**
     * sj  获取计算利息的bean
     * @param SecurityCode String
     * @param insType String
     * @throws YssException
     * @return BaseBondOper
     * bugID:MS00052
     */
    public BaseInvestOper getInvestSpringRe(String formula) throws
        YssException {
        BaseInvestOper investOper = null;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select spring.FSICode as FSICode,spring.FBeanId as FBeanID from (select FCIMCode,FCIMName,FCIMType,FFormula,FSPICode from Tb_Base_CalcInsMetic where FCheckState = 1) calcMetic " +
                "  join (select FSICode,FBeanId from TB_FUN_SPINGINVOKE where FSICode = " +
                dbl.sqlString(formula) +
                " ) spring on calcMetic.FSPICode = spring.FSICode";
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                if (rs.getString("FBeanId") != null && rs.getString("FBeanId").trim().length() > 0) {
                    investOper = (BaseInvestOper) pub.getOperDealCtx().getBean(rs.
                        getString("FBeanId"));
                } else {
                    throw new YssException("获取Spring调用-请检查Spring是否设置");
                }
            }
            return investOper;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new YssException("获取Spring调用出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

//----------bug:MS00011 ----------------------------------//
    public HashMap getHmDirectCury() {
        return hmDirectCury;
    }

    public void setHmDirectCury(HashMap hmDirectCury) {
        this.hmDirectCury = hmDirectCury;
    }
//--------------------------------------------------------//
    
    /**
     * add by wangzuochun 2010.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
     * 检查当前的年份是否设置了节假日
     * strYear 表示当前日期的年份
     */
    public void checkHoliday(java.util.Date dDate,String strHoliday) throws YssException{
    	
    	ResultSet rsTemp = null;
    	
    	try{
    		String strYear = String.valueOf(YssFun.getYear(dDate));
        
    		String strSql = "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " 
    			+ dbl.sqlString(strHoliday) + " and FCheckState=1 " 
    			+ "and FDate >= to_date('" + strYear + "-01-01','yyyy-MM-dd') "
    			+ "and FDate <= to_date('" + strYear + "-12-31','yyyy-MM-dd') ";
        
    		rsTemp = dbl.openResultSet(strSql);
    		if (!rsTemp.next()){
    			dbl.closeResultSetFinal(rsTemp);
    			//edit by songjie 2011.03.19 BUG:1158 QDV4赢时胜(测试)2011年2月25日02_B
    			throw new YssException(" 节假日群【" + strHoliday + "】没有【"+ strYear + "】年的节假日信息，请增加【" + strYear + "年】的节假日定义！");
    		}
    	}
    	catch(Exception e){
    		throw new YssException(e.getMessage());
    	}
    	finally{
    		dbl.closeResultSetFinal(rsTemp);
    	}
    }
    
    /**
     * fangjiang 2011.10.14 根据传进来的币种，计算该币种对基础货币的汇率
     * bugID:MS00052
     */
    public double getRate(java.util.Date dRateDate, String sRateTime,
                          String sCuryCode, String sPortCode) throws YssException {
		String strSql = "";
		String strSrcWhereSql = "";
		ResultSet rs = null;
		String strError = "获取汇率行情出错";
		double dReturn = 1;
		double dFactor = 1;
		String sInvertInd = "";
		String sPortRateSrc = "01";
		String sPortRateField = "FExRate1";
		String sRateField = "";
		PortfolioBean port = null;
		try {		
			if (sCuryCode.equalsIgnoreCase(pub.getPortBaseCury(sPortCode))) {  
				return dReturn;
			}		
			sPortRateSrc = pub.getPortRateSrcCode();
			sPortRateField = pub.getPortRateCode(); //默认取组合群设置中的组合汇率来源和汇率字段
			if (sPortCode != null && sPortCode.length() > 0) { //如果传入了组合，就取组合设置中的基础汇率来源和汇率字段
				port = new PortfolioBean();
				port.setYssPub(pub);
				port.setPortCode(sPortCode);
				port.getSetting();			
				if (port.getPortRateSrcCode() != null && port.getPortRateSrcCode().trim().length() > 0 && !port.getPortRateSrcCode().equalsIgnoreCase("null")) {
					sPortRateSrc = port.getPortRateSrcCode();
				}
				if (port.getPortRateCode() != null && port.getPortRateCode().trim().length() > 0 && !port.getPortRateCode().equalsIgnoreCase("null")) {
					sPortRateField = port.getPortRateCode();
				}
			}		
		    if (sPortRateSrc.trim().length() > 0) {
		    	strSrcWhereSql = " and FExRateSrcCode = " + dbl.sqlString(sPortRateSrc);
		    }
		    sRateField = sPortRateField;

			strSql = " select a.*, b.FInvertInd, b.FFactor " + " from (select FCuryCode,FPortCode," + sRateField + " from " +
			         pub.yssGetTableName("Tb_Data_ExchangeRate") + " where FCheckState=1 and FMarkCury = '" + pub.getPortBaseCury(sPortCode) + "'" +              
			         " and FCuryCode=" + dbl.sqlString(sCuryCode) + " and FExRateDate = " + "(select max(FExRateDate) as FExRateDate from " +
			         pub.yssGetTableName("Tb_Data_ExchangeRate") + " where FCheckState=1 and FExRateDate <= " + dbl.sqlDate(dRateDate) +
			         " and FCuryCode= " + dbl.sqlString(sCuryCode) + " and FMarkCury = '"+pub.getPortBaseCury(sPortCode)+"'";   
			
		    strSql = strSql +  strSrcWhereSql + " and (FPortCode= " + (sPortCode == null ? dbl.sqlString(" ") : dbl.sqlString(sPortCode)) + " or FPortCode=' ')" +
					" group by FCuryCode" + ")" + strSrcWhereSql;	
		    
			strSql = strSql + " and (FPortCode=" + (sPortCode == null ? dbl.sqlString(" ") : dbl.sqlString(sPortCode)) + " or FPortCode=' ')";
			
			strSql = strSql + " order by FPortCode desc) a left join " + " (select FCuryCode, FInvertInd, FFactor from " +
			         pub.yssGetTableName("Tb_Para_Currency") + ") b on a.FCuryCode = b.FCuryCode order by FPortCode desc"; 
			
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				dReturn = rs.getDouble(sRateField);
				sInvertInd = rs.getString("FInvertInd");
				dFactor = rs.getDouble("FFactor") == 0 ? 1 : rs.getDouble("FFactor");
				if (sInvertInd != null) {
				    if (sInvertInd.equals("1") && dReturn != 0) {
				        dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
				    }
				    if (sInvertInd.equals("0")) {
				        dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
				    }
			    }
			}	
			return dReturn;
		} catch (Exception e) {
			throw new YssException(strError + "\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

    /*public double getCuryRateWithMarkCury(java.util.Date dRateDate,
                                          String sCuryCode,
                                          String sBaseRateSrc_f,
                                          String sBaseRateField_f,
                                          String sPortRateSrc_f,
                                          String sPortRateField_f,
                                          String sPortCode,
     String sRateType, String MarkCury) throws
          YssException {
       return getCuryRateWithMarkCury(dRateDate, "", sCuryCode, sBaseRateSrc_f,
                                      sBaseRateField_f, sPortRateSrc_f,
                                      sPortRateField_f,
                                      sPortCode,
                                      sRateType, MarkCury);
        }

        public double getCuryRateWithMarkCury(java.util.Date dRateDate,
                                          String sCuryCode, String sPortCode,
     String sRateType, String MarkCury) throws
          YssException {
       return getCuryRateWithMarkCury(dRateDate, "", sCuryCode, "", "", "", "",
                                      sPortCode,
                                      sRateType, MarkCury);
        }

        public double getCuryRateWithMarkCury(java.util.Date dRateDate,
                                          String sRateTime,
                                          String sCuryCode,
                                          String sBaseRateSrc_f,
                                          String sBaseRateField_f,
                                          String sPortRateSrc_f,
                                          String sPortRateField_f,
                                          String sPortCode, String sRateType,
                                          String MarkCury) throws
          YssException {
       String strSql = "";
       String strSrcWhereSql = "";
       ResultSet rs = null;
       String strError = "获取汇率行情出错";
       double dReturn = 1;
       double dFactor = 1;
       String sInvertInd = "";
       String sBaseRateSrc = "";
       String sBaseRateField = "";
       String sPortRateSrc = "01";
       String sPortRateField = "FExRate1";
       String sRateField = "";
       PortfolioBean port = null;
       try {
          if (sPortCode == null || sPortCode.length() == 0) {
             throw new YssException("请设置组合代码！");
          }
          if (sCuryCode.equalsIgnoreCase(pub.getBaseCury()) &&
              !sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_PORT)) {
             return dReturn;
          }
          sBaseRateSrc = pub.getBaseRateSrcCode();
          //if (MarkCury == null || MarkCury.length() == 0)
          //{
          //MarkCury = pub.getBaseCury();
          //}
          sBaseRateField = pub.getBaseRateCode(); //默认取组合群设置中的基础汇率来源和汇率字段
          sPortRateSrc = pub.getPortRateSrcCode();
          sPortRateField = pub.getPortRateCode(); //默认取组合群设置中的组合汇率来源和汇率字段
     if (sPortCode != null && sPortCode.length() > 0) { //如果传入了组合，就取组合设置中的基础汇率来源和汇率字段
             port = new PortfolioBean();
             port.setYssPub(pub);
             port.setPortCode(sPortCode);
             port.getSetting();
             if (port.getBaseRateSrcCode() != null &&
                 port.getBaseRateSrcCode().trim().length() > 0 &&
                 !port.getBaseRateSrcCode().equalsIgnoreCase("null")) {
                sBaseRateSrc = port.getBaseRateSrcCode();
             }
             if (port.getBaseRateCode() != null &&
                 port.getBaseRateCode().trim().length() > 0 &&
                 !port.getBaseRateCode().equalsIgnoreCase("null")) {
                sBaseRateField = port.getBaseRateCode();
             }
             if (port.getPortRateSrcCode() != null &&
                 port.getPortRateSrcCode().trim().length() > 0 &&
                 !port.getPortRateSrcCode().equalsIgnoreCase("null")) {
                sPortRateSrc = port.getPortRateSrcCode();
             }
             if (port.getPortRateCode() != null &&
                 port.getPortRateCode().trim().length() > 0 &&
                 !port.getPortRateCode().equalsIgnoreCase("null")) {
                sPortRateField = port.getPortRateCode();
             }
          }
          if (port != null) {
             if (sCuryCode.equalsIgnoreCase(port.getCurrencyCode())) { //如果交易货币和组合货币相同，那么组合汇率来源强制等于组合货币来源，否则会出现原币和组合货币不相等的情况
                sRateType = YssOperCons.YSS_RATE_PORT;
             }
          }
          if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_BASE)) { //取基础汇率
             strSql = "select FQuoteWay,FPortCode,FCuryCode,FMarkCury from " +
                   pub.yssGetTableName("Tb_Para_CurrencyWay") +
                   " where FPortCode = " + dbl.sqlString(sPortCode) +
                   " and FCuryCode = " + dbl.sqlString(sCuryCode) +
                   " and FMarkCury = " + dbl.sqlString(MarkCury) + " and " +
                   dbl.sqlDate(dRateDate) +
                   ">= (select max(FInitDate) as FInitDate from " +
                   pub.yssGetTableName("Tb_Para_Currency") +
                   " where FCuryCode = " + dbl.sqlString(sCuryCode) +
                   " group by FCuryCode)";
             rs = dbl.openResultSet(strSql);
             if (!rs.next()) {
                throw new YssException("请在货币方向中配置相应信息！");
             }
             else {
                if (sBaseRateSrc_f.trim().length() == 0 &&
     sBaseRateField_f.trim().length() == 0) { //如果传入了基础汇率来源和字段，就用传进来的，如果没有传入就用组合设置中的
                   if (sBaseRateSrc.trim().length() > 0) {
                      strSrcWhereSql = " and FExRateSrcCode=" +
                            dbl.sqlString(sBaseRateSrc) +
     " and (FMarkCury = " + dbl.sqlString(pub.getBaseCury())
                            + " or FMarkCury = ' ') "; //因为数据库中多为空格，暂时加上此行。
                   }
                   sRateField = sBaseRateField;
                }
                else {
                   strSrcWhereSql = " and FExRateSrcCode=" +
                         dbl.sqlString(sBaseRateSrc_f) +
                         " and (FMarkCury = " + dbl.sqlString(pub.getBaseCury())
                         + " or FMarkCury = ' ') ";
                   sRateField = sBaseRateField_f;
                }
             }
             dbl.closeResultSetFinal(rs);
          }
     else if (sRateType.equalsIgnoreCase(YssOperCons.YSS_RATE_PORT)) { //取组合汇率
             strSql = "select FQuoteWay,FPortCode,FCuryCode,FMarkCury from " +
                   pub.yssGetTableName("Tb_Para_CurrencyWay") +
                   " where FPortCode = " + dbl.sqlString(sPortCode) +
                   " and FCuryCode = " + dbl.sqlString(sCuryCode) +
                   " and FMarkCury = " + dbl.sqlString(MarkCury) + " and " +
                   dbl.sqlDate(dRateDate) +
                   ">= (select max(FInitDate) as FInitDate from " +
                   pub.yssGetTableName("Tb_Para_Currency") +
                   " where FCuryCode = " + dbl.sqlString(sCuryCode) +
                   " group by FCuryCode)";
             rs = dbl.openResultSet(strSql);
             if (!rs.next()) { //查找组合货币对基础货币的方向
                sCuryCode = port.getCurrencyCode();
     strSql = "select FQuoteWay,FPortCode,FCuryCode,FMarkCury from " +
                      pub.yssGetTableName("Tb_Para_CurrencyWay") +
                      " where FPortCode = " + dbl.sqlString(sPortCode) +
                      " and FCuryCode = " + dbl.sqlString(sCuryCode) +
                      " and FMarkCury = " + dbl.sqlString(pub.getBaseCury()) +
                      " and " + dbl.sqlDate(dRateDate) +
                      ">= (select max(FInitDate) as FInitDate from " +
                      pub.yssGetTableName("Tb_Para_Currency") +
                      " where FCuryCode = " + dbl.sqlString(sCuryCode) +
                      " group by FCuryCode)";
                dbl.closeResultSetFinal(rs);
                rs = dbl.openResultSet(strSql);
                if (!rs.next()) {
                   throw new YssException("请在货币方向中配置相应信息！");
                }
                else {
                   MarkCury = pub.getBaseCury();
                }
                dbl.closeResultSetFinal(rs);
             }
             else {
                MarkCury = port.getCurrencyCode();
             }
             if (sPortRateSrc_f.trim().length() == 0 &&
     sPortRateField_f.trim().length() == 0) { //如果传入了组合汇率来源和字段，就用传进来的，如果没有传入就用组合设置中的
                if (sPortRateSrc.trim().length() > 0) {
                   strSrcWhereSql = " and FExRateSrcCode=" +
                         dbl.sqlString(sPortRateSrc)
                         + " and (FMarkCury = " + dbl.sqlString(MarkCury)
                         + " or FMarkCury = ' ') ";
                }
                sRateField = sPortRateField;
             }
             else {
                strSrcWhereSql = " and FExRateSrcCode=" +
                      dbl.sqlString(sPortRateSrc_f)
                      + " and (FMarkCury = " + dbl.sqlString(MarkCury)
                      + " or FMarkCury = ' ') ";
                sRateField = sPortRateField_f;
             }
          }

          strSql = "select a.*, b.FInvertInd, b.FFactor " +
                " from (select FCuryCode,FPortCode," + sRateField + " from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 ";
          strSql = strSql + " and FCuryCode=" + dbl.sqlString(sCuryCode) +
                " and FExRateDate = " +
                "(select max(FExRateDate) as FExRateDate from " +
                pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " where FCheckState=1 and FExRateDate <= " +
                dbl.sqlDate(dRateDate) +
                " and FCuryCode=" + dbl.sqlString(sCuryCode) +
                strSrcWhereSql +
                " and (FPortCode=" +
                (sPortCode.length() == 0 ? "' '" : dbl.sqlString(sPortCode)) +
                " or FPortCode=' ')" +

                " group by FCuryCode" +
                ")" + strSrcWhereSql;

          strSql = strSql + " and (FPortCode=" +
                (sPortCode.length() == 0 ? "' '" : dbl.sqlString(sPortCode)) +
                " or FPortCode=' ')";
          strSql = strSql +

                "order by FPortCode desc) a left join " +
                " (select FCuryCode, FInvertInd, FFactor from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ") b on a.FCuryCode = b.FCuryCode";

          rs = dbl.openResultSet(strSql);
          if (rs.next()) {
             dReturn = rs.getDouble(sRateField);
             sInvertInd = rs.getString("FInvertInd");
     dFactor = rs.getDouble("FFactor") == 0 ? 1 : rs.getDouble("FFactor");
             if (sInvertInd != null) {
                if (sInvertInd.equals("1") && dReturn != 0) {
                   dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
                }
                if (sInvertInd.equals("0")) {
                   dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
                }
             }
          }
          else {
             dbl.closeResultSetFinal(rs);
             strSql = "select FInitRate,FInvertInd, FFactor from " +
                   pub.yssGetTableName("Tb_Para_Currency") +
                   " where FCheckState = 1"
                   + " and FCuryCode = " + dbl.sqlString(sCuryCode);
             rs = dbl.openResultSet(strSql);
             if (rs.next()) {
                dReturn = rs.getDouble("FInitRate");
                sInvertInd = rs.getString("FInvertInd");
                dFactor = rs.getDouble("FFactor") == 0 ? 1 :
                      rs.getDouble("FFactor");
                if (sInvertInd != null) {
                   if (sInvertInd.equals("1") && dReturn != 0) {
                      dReturn = YssD.round(YssD.div(dFactor, dReturn), 15);
                   }
                   if (sInvertInd.equals("0")) {
                      dReturn = YssD.round(YssD.div(dReturn, dFactor), 15);
                   }
                }
             }
          }

          return dReturn;
       }
       catch (Exception e) {
          throw new YssException(strError + "\r\n" + e.getMessage(), e);
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
        }*/

    /*public double calBaseMoneyWithMarkCury(double dMoney, String sPortCode,
     String sCuryCode, double dBaseRate) throws
          YssException {
       double dResult = 0;
       String strSql = "";
       String QuoteWay = "";
       ResultSet rs = null;
       try {
          if (dBaseRate == 0) {
             dBaseRate = 1;
          }
          QuoteWay = getQuoteWay(sPortCode, sCuryCode, pub.getBaseCury());
          if (QuoteWay.length() > 0) {
             if (QuoteWay.equalsIgnoreCase("OnetoX")) {
                dResult = YssD.round(YssD.mul(dMoney, dBaseRate), 2);
             }
             else if (QuoteWay.equalsIgnoreCase("XtoOne")) {
                dResult = YssD.round(YssD.div(dMoney, dBaseRate), 2);
             }
          }
          return dResult;
       }
       catch (Exception e) {
          throw new YssException("获取基础金额出错！");
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
        }*/

    /*public double calPortMoneyWithMarkCury(java.util.Date dRateDate,
                                           double dMoney, String sPortCode,
     String sCuryCode, double dPortRate) throws
          YssException {
       double dResult = 0;
       double dBaseRate = 0;
       String strSql = "";
       String QuoteWay = "";
       ResultSet rs = null;
       PortfolioBean port = null;
       try {
          if (dPortRate == 0) {
             dPortRate = 1;
          }
          port = new PortfolioBean();
          port.setPortCode(sPortCode);
          port.setYssPub(pub);
          port.getSetting();

          QuoteWay = getQuoteWay(sPortCode, sCuryCode, port.getCurrencyCode());
          if (QuoteWay.length() > 0) {
             if (QuoteWay.equalsIgnoreCase("OnetoX")) {
                dResult = YssD.round(YssD.mul(dMoney, dPortRate), 2);
             }
             else if (QuoteWay.equalsIgnoreCase("XtoOne")) {
                dResult = YssD.round(YssD.div(dMoney, dPortRate), 2);
             }
          }
          else {
             //查找原币对基础货币的方向,先通过
             //此方向计算一个中间值。
             dBaseRate = getCuryRateWithMarkCury(dRateDate,
                                                 sCuryCode, sPortCode,
                                                 YssOperCons.YSS_RATE_BASE,
                                                 pub.getBaseCury());
             QuoteWay = getQuoteWay(sPortCode, sCuryCode, pub.getBaseCury());
             if (QuoteWay.length() > 0 && dBaseRate > 0) {
                if (QuoteWay.equalsIgnoreCase("OnetoX")) {
                   dResult = YssD.round(YssD.mul(dResult, dBaseRate), 2);
                }
                else if (QuoteWay.equalsIgnoreCase("XtoOne")) {
                   dResult = YssD.round(YssD.div(dResult, dBaseRate), 2);
                }
             }
     QuoteWay = getQuoteWay(sPortCode, sCuryCode, port.getCurrencyCode());
             if (QuoteWay.length() > 0) {
                if (QuoteWay.equalsIgnoreCase("OnetoX")) {
                   dResult = YssD.round(YssD.mul(dResult, dPortRate), 2);
                }
                else if (QuoteWay.equalsIgnoreCase("XtoOne")) {
                   dResult = YssD.round(YssD.div(dResult, dPortRate), 2);
                }
             }
          }
          return dResult;
       }
       catch (Exception e) {
          throw new YssException("获取组合金额出错！");
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
        }

        public String getQuoteWay(String sPortCode, String CuryCode, String MarkCury) throws
          YssException {
       String QuoteWay = "";
       String strSql = "";
       ResultSet rs = null;
       try {
          strSql = "select FQuoteWay,FPortCode,FCuryCode,FMarkCury from " +
                pub.yssGetTableName("Tb_Para_CurrencyWay") +
                " where FPortCode = " + dbl.sqlString(sPortCode) +
                " and FCuryCode = " + dbl.sqlString(CuryCode) +
                " and FMarkCury = " + dbl.sqlString(MarkCury);
          rs = dbl.openResultSet(strSql);
          if (rs.next()) {
             QuoteWay = rs.getString("FQuoteWay");
          }
          return QuoteWay;
       }
       catch (Exception e) {
          throw new YssException("获取报价方向出错！");
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
        }*/

}
