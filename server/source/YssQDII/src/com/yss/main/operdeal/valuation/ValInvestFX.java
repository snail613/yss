package com.yss.main.operdeal.valuation;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.yss.commeach.EachRateOper;
import com.yss.main.operdata.InvestPayRecBean;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.main.parasetting.MTVMethodBean;
import com.yss.manager.InvestPayAdimin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class ValInvestFX
    extends BaseValDeal {
    public ValInvestFX() {
    }

    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        HashMap hmResult = new HashMap();
        String strSql = "";
        MTVMethodBean vMethod = null;
        InvestPayRecBean payInvestRate = null;
        ResultSet rs = null;
        String sKey = "", sOperType = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        double dTmpMoney1 = 0;
        double dTmpMoney2 = 0;
        double dTmpMoney3 = 0;
        double dTmpBaseMoney = 0;
        double dTmpPortMoney = 0;
        BaseStgStatDeal cashstgstat = null;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090417 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------

        try {
        	/**shashijie 2012-9-10 BUG 5455 先删除之前产生的汇兑损益数据*/
        	deleteDate();
			/**end shashijie 2012-9-10 BUG */
        	
            //先统计当日运营应收应付库存
            cashstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("InvestPayRec");
            cashstgstat.setYssPub(pub);
            cashstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));

            //统计当日的运营收支库存为了估值完成后就不用统计库存了  胡昆  20070921
            cashstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("InvestStorage");
            cashstgstat.setYssPub(pub);
            cashstgstat.stroageStat1(dDate, dDate, operSql.sqlCodes(portCode));//修改 by wuweiqi 20110114 判断是否需要统计两费   QDV4工银2010年12月22日01_A 
          
            //    hmValRate = new HashMap();
            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);

                strSql =
                	/**shashijie 2012-8-15 BUG 5226 汇兑损益取数直接取"基本"的分页数据,而不是直接区06,07应收应付数据的金额*/
                	" Select a.Fivpaycatcode," +
                	" a.Fstoragedate," +
                	" a.Fportcode," +
                	" b.Ftsftypecode," +
                	" b.Fsubtsftypecode," +
                	" a.Fcurycode," +
                	" Nvl(a.Fbal, 0) As Fbal," +
                	" Nvl(a.Fbasecurybal, 0) As Fbasecurybal," +
                	" Nvl(a.Fportcurybal, 0) As Fportcurybal," +
                	/**shashijie 2012-12-28 STORY 3417 建行Oracle升级问题排查*/
                	(this.invmgrInvestField.length() != 0 ? 
                			" a.FAnalysisCode1, " :  " ") +
					/**end shashijie 2012-12-28 STORY 3417 */
                	" m.Fportcury," +
                	" Pay.Fbal As Fhdsybal," +
                	" Pay.Fportcurybal As Fhdsyportbal," +
                	" Pay.Fbasecurybal As Fhdsybasebal" +
                	" From (Select Fivpaycatcode," +
                	" Fstoragedate," +
                	" Fportcode," +
                	" Fcurycode," +
                	" Nvl(Fbal, 0) As Fbal," +
                	" Nvl(Fbasecurybal, 0) As Fbasecurybal," +
                	" Nvl(Fportcurybal, 0) As Fportcurybal," +
                	" Fanalysiscode1" +
                	" From "+pub.yssGetTableName("Tb_Stock_Invest")+
                	" Where Fcheckstate = 1" +
                	" And Fstoragedate = "+dbl.sqlDate(this.dDate)+
                	" And Fyearmonth = "+dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                	" And Fportcode = "+dbl.sqlString(this.portCode)+" ) a"+
                	" Join (Select Fivpaycatcode," +
                	" Fstoragedate," +
                	" Fportcode," +
                	" Ftsftypecode," +
                	" Fsubtsftypecode," +
                	" Fcurycode," +
                	" Nvl(Fbal, 0) As Fbal," +
                	" Nvl(Fbasecurybal, 0) As Fbasecurybal," +
                	" Nvl(Fportcurybal, 0) As Fportcurybal " +
                	(this.invmgrInvestField.length() != 0 ? 
                			", FAnalysisCode1" : " ")+
                	" From "+pub.yssGetTableName("Tb_Stock_Investpayrec")+
                	" Where Fcheckstate = 1" +
                	" And Fstoragedate = "+dbl.sqlDate(this.dDate)+
                	" And Fyearmonth = "+dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                	" And Fportcode = "+dbl.sqlString(this.portCode)+
                	" And Ftsftypecode In ("+operSql.sqlCodes(YssOperCons.YSS_ZJDBLX_Rec+
                			","+YssOperCons.YSS_ZJDBLX_Pay)+" )) b " +
                	" On a.Fivpaycatcode = b.Fivpaycatcode And a.Fcurycode = b.Fcurycode" +
                	" Left Join (Select Fportcode, Fportname, Fportcury" +
                	" From "+pub.yssGetTableName("Tb_Para_Portfolio")+
                	" Where Fcheckstate = 1) m On a.Fportcode = m.Fportcode" +
                	" Left Join (Select x.*," +
                	" Decode(2, 0,  '', Substr(Substr(x.Fsubtsftypecode, 1, 4), - (2))) As Fjoinsubtsfcode" +
                	" From "+pub.yssGetTableName("Tb_Stock_Investpayrec")+" x" +
        			" Where Fcheckstate = 1" +
        			" And Fstoragedate = "+dbl.sqlDate(this.dDate)+
        			" And Fportcode = "+dbl.sqlString(this.portCode)+
        			" And Ftsftypecode = "+dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX)+
        			" And Fsubtsftypecode In ('9906IV', '9907IV')) " +
        			" Pay On a.Fivpaycatcode = Pay.Fivpaycatcode " +
        			(this.invmgrInvestField.length() != 0 ?
                            " and a.FAnalysisCode1 = pay.FAnalysisCode1 " : " ") +
        			/**shashijie 2012-9-7 STORY 5455 需要产生一笔冲减的汇兑损益数据,所以这里原币余额的判断要去掉*/
                    //" Where a.Fbal > 0 "+
                    "";
                	/**end shashijie 2012-9-7 STORY 5455 */
                
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    payInvestRate = new InvestPayRecBean();
                    payInvestRate.setTradeDate(this.dDate);

                    payInvestRate.setFIVPayCatCode(rs.getString("FIVPayCatCode"));
                    payInvestRate.setPortCode(rs.getString("FPortCode"));
                    payInvestRate.setAnalysisCode1(this.invmgrInvestField.length() !=
                        0 ? rs.getString(this.
                                         invmgrInvestField) : " ");

                    payInvestRate.setCuryCode(rs.getString("FCuryCode"));

                    dBaseRate = 1;
                    if (!rs.getString("FCuryCode").equalsIgnoreCase(pub.
                    		getPortBaseCury(this.portCode))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                        dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                            rs.getString("FCuryCode"), this.portCode,
                            YssOperCons.YSS_RATE_BASE);
                    }

                    if (rs.getString("FPortCury") == null) {
                        throw new YssException("请检查投资组合【" +
                                               rs.getString("FPortCode") +
                                               "】的币种设置！");
                    }

//               dPortRate = this.getSettingOper().getCuryRate(dDate,
//                     vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                     vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                     rs.getString("FPortCury"), this.portCode,
//                     YssOperCons.YSS_RATE_PORT);
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 ---
                    rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode, vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                              vMethod.getPortRateSrcCode(), vMethod.getPortRateCode()); //用通用方法，获取组合汇率
                    dPortRate = rateOper.getDPortRate(); //获取组合汇率

					// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
					// 则默认组合汇率为1
					if (dPortRate == 0) {
						dPortRate = 1;
					}
					// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
					// 则默认组合汇率为1
                    //------------------------------------------------------------

                    sOperType = rs.getString("FSubTsfTypeCode"); //20071113,杨文奇。生成的调拨子类型应该为例如9907IV，而不是9907

                    payInvestRate.setBaseCuryRate(dBaseRate);
                    payInvestRate.setPortCuryRate(dPortRate);

                    dTmpMoney1 = rs.getDouble("FBal");
                    
                    //modify by lidaolong 20110314 #386 增加一个功能，能够自动支付管理费和托管费
                    //应付运营收支款基础货币汇兑损益=今日库存原币金额 * 今日基础汇率 – 今日库存基础货币金额；
                    //应付运营收支款组合货币汇兑损益=今日库存原币金额 * 今日基础汇率 / 今日组合汇率 – 今日库存组合货币金额。

                    //计算基础货币的汇兑损益
                    dTmpMoney2 = rs.getDouble("FBaseCuryBal");
                    dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(dTmpMoney1,
                        dBaseRate),
                                             dTmpMoney2);
                    payInvestRate.setBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                        rs.getDouble("FHDSYBaseBal")));

                 /*   dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(dTmpMoney1,
                            dBaseRate),
                                                 dTmpMoney2);
                        payInvestRate.setBaseCuryMoney(dTmpBaseMoney);*/
                    
                    //计算组合货币的汇兑损益
                    dTmpMoney3 = rs.getDouble("FPortCuryBal");
                  
                    dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(
                        dTmpMoney1,
                        dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCuryCode"), this.dDate, this.portCode), dTmpMoney3);
                    payInvestRate.setPortCuryMoney(YssD.sub(dTmpPortMoney,
                        rs.getDouble("FHDSYPortBal")));
                    
                   /* dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(
                            dTmpMoney1,
                            dBaseRate, dPortRate,
                            rs.getString("FCuryCode"), this.dDate, this.portCode), dTmpMoney3);
                        payInvestRate.setPortCuryMoney(dTmpPortMoney);*/
                    
                    //end by lidaolong 20110314
                    
                    
                    payInvestRate.setTsftTypeCode(YssOperCons.YSS_ZJDBLX_FX);
                    payInvestRate.setSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX +
                        sOperType);
                    payInvestRate.checkStateId = 1;

                    sKey = payInvestRate.getPortCode() + "\f" +
                        payInvestRate.getFIVPayCatCode() + "\f" +
                        payInvestRate.getCuryCode() + "\f" + //add by fangjiang 2011.02.11 #2279
                        (this.invmgrInvestField.length() != 0 ?
                         (payInvestRate.getAnalysisCode1() + "\f") : "") +
                        payInvestRate.getSubTsfTypeCode();

                    hmResult.put(sKey, payInvestRate);
                    hmValRate.put(payInvestRate.getCuryCode(), payInvestRate);
                }
                dbl.closeResultSetFinal(rs);
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**shashijie 2012-9-10 BUG 5455 */
	private void deleteDate() throws YssException {
		InvestPayAdimin investpay = new InvestPayAdimin();
        investpay.setYssPub(pub);
        investpay.delete(this.dDate, this.dDate, YssOperCons.YSS_ZJDBLX_FX, "9906IV,9907IV", 
        		"", this.portCode, "", "", "", -1, "", "");
	}

	public Object filterInvestCondition() {
        InvestPayRecBean invest = new InvestPayRecBean();
        invest.setTsftTypeCode(YssOperCons.YSS_ZJDBLX_FX);
//      invest.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FX_Rec + "," +
//                               YssOperCons.YSS_ZJDBZLX_FX_Pay);
        invest.setSubTsfTypeCode("'9906IV','9907IV'");
        return invest;
    }

}
