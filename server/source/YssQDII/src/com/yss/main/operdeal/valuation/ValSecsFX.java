package com.yss.main.operdeal.valuation;

import java.util.*;

import com.yss.main.parasetting.MTVMethodBean;
import com.yss.main.operdata.SecPecPayBean;
import java.sql.*;

import com.yss.util.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.commeach.EachRateOper;

/*
    获取估值证券信息的汇兑损益
    取数原则：
       1.获取组合的估值方法链接中存在所有证券
       2.先统计当日库存，取当日的库存(Tb_Stock_Security)
       3.根据估值方法获取当汇率，计算汇兑损益
 */

public class ValSecsFX
    extends BaseValDeal {
    public ValSecsFX() {
    }

    /**
     * getValuationCats
     *
     * @param mtvBeans ArrayList
     * @return HashMap
     */
    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        HashMap hmResult = new HashMap();
        String strSql = "";
        MTVMethodBean vMethod = null;
        SecPecPayBean payRate = null;
        SecPecPayBean payPrice = null;
        ResultSet rs = null;
        String sKey = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        double dPrice = 0;
        double dTmpMoney1 = 0;
        double dTmpMoney2 = 0;
        double dTmpMoney3 = 0;
        double dTmpBaseMoney = 0;
        double dTmpPortMoney = 0;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        
        //---MS01021  QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差  add by jiangshichao 2010.03.26-----
        CtlPubPara pubpara = null;
        pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        String para = pubpara.getSecRecRound();
        int digit = para.equalsIgnoreCase("0")?2:4;//默认小数点后保留2位有效数字
      //---MS01021  QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差 end----------------------------------
        try {
            //先统计当日证券库存
//         BaseStgStatDeal secstgstat = (BaseStgStatDeal) pub.
//               getOperDealCtx().getBean("SecurityStorage");
//         secstgstat.setYssPub(pub);
//         secstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));

//            OperFunDealBean OperFun = (OperFunDealBean) pub.getOperDealCtx().
//                  getBean("operfun");
//            OperFun.setYssPub(pub);
//            operFun.calculateStorage(dDate, dDate, "'" + this.portCode + "'",
//                                     YssOperCons.YSS_KCLX_Security, true);

            //    hmValRate = new HashMap();
            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);

                strSql = " select cs.*, mtvlink.flinkcode as FLinkCode, " +//edit by songjie 2011.01.26 BUG:947 QDV4汇添富2011年1月18日01_B
                    //--------------------------------------------------edit by jc
                    "(FBal - (case when FAppreciation is null then 0 else FAppreciation end)) as FBal," +
                    "(FMBal - (case when FMAppreciation is null then 0 else FMAppreciation end)) as FMbal," +
                    "(FVBal - (case when FVAppreciation is null then 0 else FVAppreciation end)) as FVBal," +
                    "(FBaseCuryBal - (case when FBaseAppreciation is null then 0 else FBaseAppreciation end)) as FBaseCuryBal," +
                    "(FMBaseCuryBal - (case when FMBaseAppreciation is null then 0 else FMBaseAppreciation end)) as FMBaseCuryBal," +
                    "(FVBaseCuryBal - (case when FVBaseAppreciation is null then 0 else FVBaseAppreciation end)) as FVBaseCuryBal," +
                    "(FPortCuryBal - (case when FPortAppreciation is null then 0 else FPortAppreciation end)) as FPortCuryBal," +
                    "(FMPortCuryBal - (case when FMPortAppreciation is null then 0 else FMPortAppreciation end)) as FMPortCuryBal," +
                    "(FVPortCuryBal - (case when FVPortAppreciation is null then 0 else FVPortAppreciation end)) as FVPortCuryBal" +
                    //----------------------------------------------------------jc
//                  " FBal,FMBal,FVBal,FBaseCuryBal,FMBaseCuryBal,FVBaseCuryBal,FPortCuryBal,FMPortCuryBal,FVPortCuryBal" +
                    " from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode, FStorageCost, FMStorageCost, FVStorageCost," +
                    " FBaseCuryCost as FCsBaseCuryCost, FMBaseCuryCost as FCsMBaseCuryCost, FVBaseCuryCost as FCsVBaseCuryCost," +
                    " FPortCuryCost as FCsPortCuryCost, FMPortCuryCost as FCsMPortCuryCost, FVPortCuryCost as FCsVPortCuryCost," +
                    //判断是否配置分析代码，杨
                    (this.invmgrSecField.length() != 0 ?
                     (this.invmgrSecField + ", ") : " ") +
                    (this.brokerSecField.length() != 0 ?
                     (this.brokerSecField + ",") : " ") +
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                    "a.FPortCode as FCsPortCode,a.FAttrClsCode as FAttrClsCode, sec.FTradeCury as FCsCuryCode, sec.FCatCode as FCsCatCode, m.FCsPortCury, a.FInvestType from " + //这里加上属性代码,也是主键之一,by leeyu BUG:0000437
                    pub.yssGetTableName("tb_stock_security") + " a" +
                    //-------------------------------------------------------------
                    " left join (select sb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("tb_para_security") +
                    " where FCheckState=1 and FStartDate<= " +
                    //edit by songjie 2012.10.31 BUG 6121 QDV4赢时胜(上海开发部)2012年10月29日01_B 由系统日期改为业务日期
                    dbl.sqlDate(dDate) +
                    " group by FSecurityCode) sa join (select FSecurityCode, FSecurityName, FStartDate, FCatCode, FTradeCury from " +
                    pub.yssGetTableName("tb_para_security") +
                    " where FCheckState=1 )sb on sa.FSecurityCode = sb.FSecurityCode and sa.FStartDate = sb.FStartDate " +
                    ") sec on a.FSecurityCode = sec.FSecurityCode " +
                    //-------------------------------------------------------------
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
                    " left join (select FPortCode, FPortName,FPortCury as FCsPortCury from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where  FCheckState = 1 ) m on a.fportcode= m.FPortCode " +
                    
                    //end by lidaolong 
                    //-------------------------------------------------------------
                    " where a.FCheckState = 1 and a.FStorageDate=" +
                    dbl.sqlDate(dDate) +
                    " and " + dbl.sqlRight("a.FYearMonth", "2") +
                    "<>'00' and a.FPortCode = " + dbl.sqlString(this.portCode) +
                    ") cs " +
                    //-------------------------------------------------------------
                    " left join (select * from " +
                    pub.yssGetTableName("Tb_Stock_SecRecPay") +
                    " where " + operSql.sqlStoragEve(dDate) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FTsfTypeCode = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                    " and FSubTsfTypeCode like '" + YssOperCons.YSS_ZJDBZLX_FX_Storage + "%'" + //取库存成本汇兑损益下面的所有汇兑损益，可能包含股票或债券的 20070918 胡昆,%号之前多了一个空格fazmm20070920
                    " and FCheckState = 1 ) rec on cs.FCsSecurityCode = rec.FSecurityCode and cs.FAttrClsCode=rec.FAttrClsCode " + //添加相关的属性分类代码到这里 BUG：000437
                    " and cs.FInvestType = rec.FInvestType " +//edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                    (this.invmgrSecField.length() != 0 ?
                     " and cs.FAnalysisCode1 = rec.FAnalysisCode1 " : " ") +
                    (this.brokerSecField.length() != 0 ?
                     " and cs.FAnalysisCode2 = rec.FAnalysisCode2" : " ") +
                    //--------------------------------------------------edit by jc
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType 
                    " left join (select fb.fsecuritycode,fb.FattrClsCode, fb.FInvestType, " + //添加相关的属性分类代码到这里 BUG：000437
                    " sum(FAppreciation) as FAppreciation, " +
                    " sum(FMAppreciation) as FMAppreciation, " +
                    " sum(FVAppreciation) as FVAppreciation, " +
                    " sum(FBaseAppreciation) as FBaseAppreciation, " +
                    " sum(FMBaseAppreciation) as FMBaseAppreciation, " +
                    " sum(FVBaseAppreciation) as FVBaseAppreciation, " +
                    " sum(FPortAppreciation) as FPortAppreciation, " +
                    " sum(FMPortAppreciation) as FMPortAppreciation, " +
                    " sum(FVPortAppreciation) as FVPortAppreciation " +
                    " from " + pub.yssGetTableName("Tb_Data_TradeSellRela") +
                    " fa, " + pub.yssGetTableName("Tb_Data_SubTrade") + " fb " +
                    " where fa.fnum = fb.fnum and fa.fsubtsftypecode = '9905EQ' " +
                    " and fb.fbargaindate = " + dbl.sqlDate(dDate) +
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType  
                    " group by fb.fsecuritycode,fb.FattrClsCode, fb.FInvestType) ff on ff.fsecuritycode = cs.FCsSecurityCode and ff.FAttrClsCode=cs.FAttrClsCode and ff.FInvestType = cs.FInvestType " + //添加相关的属性分类代码到这里 BUG：000437
                //----------------------------------------------------------jc
                    //add by songjie 2011.01.26 BUG:947 QDV4汇添富2011年1月18日01_B
                    " left join ( select FMtvCode, FLinkCode from " + pub.yssGetTableName("Tb_Para_Mtvmethodlink") + 
                    " where FMtvCode = " + dbl.sqlString(vMethod.getMTVCode()) + 
                    " ) mtvlink on mtvlink.FLinkCode = cs.FCsSecurityCode where FLinkCode is not null ";
                    //add by songjie 2011.01.26 BUG:947 QDV4汇添富2011年1月18日01_B
                rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
                //===add by xuxuming,2010.01.13.MS00902 指数信息调整后不能正确获取昨日成本汇兑损益余额=========
                HashMap hmRsSecOldStor=new HashMap();//保存昨日成本汇兑损益余额
                while(rs.next()){
                	String strKey = "";
                	String strValue="";
                	strKey=rs.getString("FStorageDate")+"\t"+rs.getString("FCSSecuritycode")+"\t"+rs.getString("FCSPortCode")+"\t"+rs.getString("FAttrClsCode");
                	strValue=rs.getDouble("FBaseCuryBal")+"\t"+rs.getDouble("FPortCuryBal")+"\t"+rs.getDouble("FMBaseCuryBal")+"\t"+
                	rs.getDouble("FMPortCuryBal")+"\t"+rs.getDouble("FVBaseCuryBal")+"\t"+rs.getDouble("FVPortCuryBal");
                	hmRsSecOldStor.put(strKey, strValue);
                }
                rs.beforeFirst();
                //=========end======================
                while (rs.next()) {
                    //设置汇兑损益
                    payRate = new SecPecPayBean();
                    payRate.setTransDate(dDate);

                    payRate.setStrSecurityCode(rs.getString("FCsSecurityCode"));
                    payRate.setStrPortCode(rs.getString("FCsPortCode"));
                    
                    payRate.setInvMgrCode(this.invmgrSecField.length() != 0 ?
                                          rs.getString(this.invmgrSecField) : " ");
                    payRate.setBrokerCode(this.brokerSecField.length() != 0 ?
                                          rs.getString(this.brokerSecField) : " ");
                    payRate.setStrCuryCode(rs.getString("FCsCuryCode"));
                    if (rs.getString("FCsCuryCode") == null) {
                        throw new YssException("请检查证券品种【" +
                                               rs.getString("FCsSecurityCode") +
                                               "】的交易币种设置！");
                    }
                    dBaseRate = 1;
                    if (!rs.getString("FCsCuryCode").equalsIgnoreCase(pub.
                    		getPortBaseCury(this.portCode))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                        dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                            rs.getString("FCsCuryCode"), this.portCode,
                            YssOperCons.YSS_RATE_BASE);
                    }

                    if (rs.getString("FCsPortCury") == null) {
                        throw new YssException("请检查投资组合【" +
                                               rs.getString("FCsPortCode") +
                                               "】的币种设置！");
                    }

//               dPortRate = this.getSettingOper().getCuryRate(dDate,
//                     vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                     vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                     rs.getString("FCsPortCury"), this.portCode,
//                     YssOperCons.YSS_RATE_PORT);
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 ---
                    rateOper.getInnerPortRate(dDate, rs.getString("FCsCuryCode"), this.portCode, vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
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
                    payRate.setAttrClsCode(rs.getString("FAttrClsCode")); //这里加上属性代码,也是主键之一,by leeyu BUG:0000437
                    payRate.setInvestType(rs.getString("FInvestType"));//edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                    payRate.setBaseCuryRate(dBaseRate);
                    payRate.setPortCuryRate(dPortRate);
                    if (payRate.getStrSecurityCode().equalsIgnoreCase("FW0090")) {
                        int iii = 0;
                    }

//               secPay.setRateBaseIncome();
                  //===add by xuxuming.2010.01.12.MS00902 指数信息调整后，当取不到昨日成本汇兑损益余额时，重新获取另一属性代码的成本汇兑损益余额==========
                    String strAttrCode = rs.getString("FAttrClsCode");// 所属分类	
                    double dFBaseCuryBal = 0;
                    double dFPortCuryBal = 0;
                    double dMBaseCuryBal = 0;
                    double dMPortCuryBal = 0;
                    double dVBaseCuryBal = 0;
                    double dVPortCuryBal = 0;
                    dFBaseCuryBal=rs.getDouble("FBaseCuryBal");
                    dFPortCuryBal=rs.getDouble("FPortCuryBal");
                    dMBaseCuryBal=rs.getDouble("FMBaseCuryBal");
                    dMPortCuryBal=rs.getDouble("FMPortCuryBal");
                    dVBaseCuryBal=rs.getDouble("FVBaseCuryBal");
                    dVPortCuryBal=rs.getDouble("FVPortCuryBal");
        			if (strAttrCode != null
        					&& (strAttrCode.equals("CEQ") || strAttrCode
        							.equals("IDXEQ")) && hmRsSecOldStor !=null) {				
        				if(dFBaseCuryBal==0&&dFPortCuryBal==0&&dMBaseCuryBal==0&&
        						dMPortCuryBal==0&&dVBaseCuryBal==0&&dVPortCuryBal==0){//都为零，表明首次调整后无昨日库存余额，取另一属性的昨日余额
        					String strKey="";

        					/**Start 20130702 modified by liubo.Bug #8308.QDV4建行2013年06月18日01_B
        					 * 当CEQ和IDXEQ在指数信息调整界面做转换时，实际上产生的综合业务已经将证券库存和证券应收应付库存做了流入
        					 * 在这里不需要再重复做取对方库存的动作*/
//        					strKey=rs.getString("FStorageDate")+"\t"+rs.getString("FCSSecuritycode")+"\t"+rs.getString("FCSPortCode")+"\t"+(strAttrCode.equals("CEQ")?"IDXEQ":"CEQ");//取另一属性
        					strKey=rs.getString("FStorageDate")+"\t"+rs.getString("FCSSecuritycode")+"\t"
        										+rs.getString("FCSPortCode")+"\t"+strAttrCode;
        					/**Start 20130702 modified by liubo.Bug #8308.QDV4建行2013年06月18日01_B*/
        					String tmpRsBal=(String)hmRsSecOldStor.get(strKey);
        					if(tmpRsBal!=null&&tmpRsBal.trim().length()>0){
        						String[] bufRsBal = tmpRsBal.split("\t");        						
        						dFBaseCuryBal=new Double(bufRsBal[0]).doubleValue();
        	                    dFPortCuryBal=new Double(bufRsBal[1]).doubleValue();
        	                    dMBaseCuryBal=new Double(bufRsBal[2]).doubleValue();
        	                    dMPortCuryBal=new Double(bufRsBal[3]).doubleValue();
        	                    dVBaseCuryBal=new Double(bufRsBal[4]).doubleValue();
        	                    dVPortCuryBal=new Double(bufRsBal[5]).doubleValue();
        					}
        				}
        			}
                    //===========end====================================================
                    //1.核算成本
                    dTmpMoney1 = rs.getDouble("FStorageCost");
                    //计算基础货币的汇兑损益
                    dTmpMoney2 = rs.getDouble("FCsBaseCuryCost");
                    dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(dTmpMoney1, dBaseRate),
                                             dTmpMoney2);
                    payRate.setBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                        /*rs.getDouble("FBaseCuryBal")*/dFBaseCuryBal));
                    //计算组合货币的汇兑损益
                    dTmpMoney3 = rs.getDouble("FCsPortCuryCost");
                    dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(dTmpMoney1,
                        dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                       //modify by jiangshichao 2010.03.16 QDV4南方2010年3月11日01_B MS01021 组合货币的市值尾差问题，这里保留小数点4位有效数
                        rs.getString("FCsCuryCode"), dDate, this.portCode,digit), dTmpMoney3); 
                       //------------------QDV4南方2010年3月11日01_B MS01021 组合货币的市值尾差问题 ------------------------------------------
                    payRate.setPortCuryMoney(YssD.sub(dTmpPortMoney,
                        /*rs.getDouble("FPortCuryBal")*/dFPortCuryBal));

                    //2.管理成本
                    dTmpMoney1 = rs.getDouble("FMStorageCost");
                    //计算基础货币的汇兑损益
                    dTmpMoney2 = rs.getDouble("FCsMBaseCuryCost");
                    dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(dTmpMoney1, dBaseRate),
                                             dTmpMoney2);
                    payRate.setMBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                        /*rs.getDouble("FMBaseCuryBal")*/dMBaseCuryBal));
                    //计算组合货币的汇兑损益
                    dTmpMoney3 = rs.getDouble("FCsMPortCuryCost");
                    dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(dTmpMoney1,
                        dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCsCuryCode"), dDate, this.portCode,digit), dTmpMoney3);//modify by jiangshichao 2010.03.16 QDV4南方2010年3月11日01_B MS01021 组合货币的市值尾差问题，这里保留小数点4位有效数
                    payRate.setMPortCuryMoney(YssD.sub(dTmpPortMoney,
                        /*rs.getDouble("FMPortCuryBal")*/dMPortCuryBal));

                    //3.估值成本
                    dTmpMoney1 = rs.getDouble("FVStorageCost");
                    //计算基础货币的汇兑损益
                    dTmpMoney2 = rs.getDouble("FCsVBaseCuryCost");
                    dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(dTmpMoney1,
                        dBaseRate), dTmpMoney2);
                    payRate.setVBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                        /*rs.getDouble("FVBaseCuryBal")*/dVBaseCuryBal));
                    //计算组合货币的汇兑损益
                    dTmpMoney3 = rs.getDouble("FCsVPortCuryCost");
                    dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(dTmpMoney1,
                        dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCsCuryCode"), dDate, this.portCode,digit), dTmpMoney3);//modify by jiangshichao 2010.03.16 QDV4南方2010年3月11日01_B MS01021 组合货币的市值尾差问题，这里保留小数点4位有效数
                    payRate.setVPortCuryMoney(YssD.sub(dTmpPortMoney,
                        /*rs.getDouble("FVPortCuryBal")*/dVPortCuryBal));

                    payRate.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX);
                    payRate.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FX_Storage + rs.getString("FCsCatCode")); //调拨子类型中加入证券品种类型  胡昆 20070918
                    payRate.checkStateId = 1;

                    sKey = payRate.getStrPortCode() + "\f" +
                        payRate.getStrSecurityCode() + "\f" +
                        (this.invmgrSecField.length() != 0 ?
                         (payRate.getInvMgrCode() + "\f") : "") +
                        (this.brokerSecField.length() != 0 ?
                         (payRate.getBrokerCode() + "\f") : "") +
                        payRate.getStrSubTsfTypeCode() +
                        //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                        "\f" + (payRate.getAttrClsCode().length() > 0 ? payRate.getAttrClsCode() : "") + "\f" + payRate.getInvestType(); //这里加上属性代码,也是主键之一,by leeyu BUG:0000437
                    //       calculateAdjust(payRate);
                    hmResult.put(sKey, payRate);
                    hmValRate.put(payRate.getStrCuryCode(), payRate);
                }
                dbl.closeResultSetFinal(rs);
            }
            return hmResult;
        } catch (Exception e) {
            //e.printStackTrace();
            throw new YssException("系统资产估值,在执行证券汇兑损益计算时出现异常!" + "\n", e); //by 曹丞 2009.02.01 证券汇兑损益计算异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs); //close rs 20080716 sj
        }
    }

    public Object filterSecCondition() {
        SecPecPayBean secpay = new SecPecPayBean();
        secpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX);
        secpay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FX_Storage + "%"); //删除的时候需要做like操作  胡昆  20070918
        return secpay;
    }

}
