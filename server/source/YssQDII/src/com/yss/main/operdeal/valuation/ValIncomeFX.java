package com.yss.main.operdeal.valuation;

import java.sql.*;
import java.util.*;

import com.yss.commeach.*;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.stgstat.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class ValIncomeFX
    extends BaseValDeal {
    //------MS00272  QDV4赢时胜（上海）2009年2月26日01_B 增加通用参数的获取-----------------------------
    SecPecPayBean oldMktRate = null; //用于获取之前行情来源中获取的行情数据。
    CtlPubPara pubpara = null;
    String priMarketPrice = "";
    //-------------------------------------------------------------------------------------------

    public ValIncomeFX() {
    }

    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        HashMap hmResult = new HashMap();
        MTVMethodBean vMethod = null;
        try {
            //先统计当日现金应收应付库存和证券应收应付库存
            BaseStgStatDeal cashstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("CashPayRec");
            cashstgstat.setYssPub(pub);
            cashstgstat.setBETFStat(
            		this.isETFVal && 
            		!this.strETFStatType.equals(YssOperCons.YSS_ETF_MAKEUP_TIMESUB) &&
            		!this.strETFStatType.equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) &&
            		!this.strETFStatType.equals(YssOperCons.YSS_ETF_MAKEUP_ONE) &&
            		!this.strETFStatType.equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ));//如果为ETF资产估值（华宝、华夏、易方达除外），在统计综合损益、汇兑损益时调用ETF现金应收应付统计过程
            cashstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));

            BaseStgStatDeal secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("SecRecPay");
            secstgstat.setYssPub(pub);
            secstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));

            //--------MS00272  QDV4赢时胜（上海）2009年2月26日01_B 获取通用参数----------
            CtlPubPara ctlpubpara = new CtlPubPara();
            ctlpubpara.setYssPub(pub);
            priMarketPrice = ctlpubpara.getPriMarketPrice();
            //----------------------------------------------------------------------

            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);
                this.getCashValCats(vMethod, hmResult);
                this.getSecValCats(vMethod, hmResult);
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException("系统资产估值,在执行综合损益的汇兑损益计算时出现异常!" + "\n", e); //by 曹丞 2009.02.01 综合损益的汇兑损益计算异常信息 MS00004 QDV4.1-2009.2.1_09A
        }
    }

    private void getSecValCats(MTVMethodBean vMethod, HashMap hmResult) throws
        YssException {
        double dTmpMoney1 = 0;
        double dTmpMoney2 = 0;
        double dTmpMoney3 = 0;
        ResultSet rs = null;
        String sKey = "", sOperType = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        String strSql = "";
        SecPecPayBean paySecRate = null;
        double dTmpBaseMoney = 0;
        double dTmpPortMoney = 0;

        double dMktPrice = 0;
        boolean bIsRound = false;
        boolean bIsFXRound2 = false;
        boolean bMVIsRound2 = false;
        //------------------------------------------------------------------
        //证券综合损益
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090417 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------

        try {
            //--------------2008.09.04 蒋锦 添加 从通用参数获取计算市值时是否四舍五入两位小数---------------//
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            bIsRound = pubPara.getMVIsRound();
            bIsFXRound2 = pubPara.getCalIncomeFxUserRound8();
            //---------------------------------------------------------------------------------------//
            bMVIsRound2 = pubPara.getMValueRoundOfCalFX();//20110112  panjunfang add 从通用参数获取计算汇兑损益时，市值是否中间不保留位数最终保留两位小数

            //先执行一次估值行情的插入，否则如果第一次估值会找不到估值行情
            this.insertValMktPrice();
            strSql =
                "select cs. *, cost.*, mk.*, mtvlink.FLinkCode as FLinkCode," + //edit by songjie 2011.01.26 BUG:947 QDV4汇添富2011年1月18日01_B
                //--------------------------------------------------edit by jc
                "(FBal - (case when FAppreciation is null then 0 else FAppreciation end)) as FBal," +
                "(FMBal - (case when FMAppreciation is null then 0 else FMAppreciation end)) as FMbal," +
                "(FVBal - (case when FVAppreciation is null then 0 else FVAppreciation end)) as FVBal," +
                "(FBaseCuryBal - (case when FBaseAppreciation is null then 0 else FBaseAppreciation end)) as FBaseCuryBal," +
                "(FMBaseCuryBal - (case when FMBaseAppreciation is null then 0 else FMBaseAppreciation end)) as FMBaseCuryBal," +
                "(FVBaseCuryBal - (case when FVBaseAppreciation is null then 0 else FVBaseAppreciation end)) as FVBaseCuryBal," +
                "(FPortCuryBal - (case when FPortAppreciation is null then 0 else FPortAppreciation end)) as FPortCuryBal," +
                "(FMPortCuryBal - (case when FMPortAppreciation is null then 0 else FMPortAppreciation end)) as FMPortCuryBal," +
                "(FVPortCuryBal - (case when FVPortAppreciation is null then 0 else FVPortAppreciation end)) as FVPortCuryBal," +
                //----------------------------------------------------------jc
                //2008.11.13 蒋锦 添加 查询保留8位小数的原币、基础货币、本位币
                //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                " FBalF, FBaseCuryBalF, FPortCuryBalF" +
                " from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode, FBal as FCsBal, FMBal as FCsMBal, FVBal as FCsVBal," +
                " FBaseCuryBal as FCsBaseCuryBal, FMBaseCuryBal as FCsMBaseCuryBal, FVBaseCuryBal as FCsVBaseCuryBal," +
                " FPortCuryBal as FCsPortCuryBal, FMPortCuryBal as FCsMPortCuryBal, FVPortCuryBal as FCsVPortCuryBal," +
                //2008.11.13 蒋锦 添加 查询保留8位小数的原币、基础货币、本位币
                //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                " FBalF AS FCsBalF, FBaseCuryBalF AS FCsBaseCuryBalF, FPortCuryBalF AS FCsPortCuryBalF," +
                " FTsfTypeCode as FCsTsfTypeCode, FSubTsfTypeCode as FCsSubTsfTypeCode," +
                //判断是否配置分析代码，杨
                (this.invmgrSecField.length() != 0 ? " FAnalysisCode1," : " ") +
                (this.brokerSecField.length() != 0 ? " FAnalysisCode2," : " ") +
                //------ modify by wangzuochun 2011.01.27 BUG #1018 期权业务当日估值统计期权汇兑损益时出错 添加FMultiple
                //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                " a.FPortCode as FCsPortCode, a.FAttrClsCode as FAttrClsCode, sec.FTradeCury as FCsCuryCode, sec.FCatCode as FCsCatCode,sec.FSubCatCode as FCsSubCatCode,sec.FFactor, m.FCsPortCury,op.FMultiple, a.FInvestType from " + // wdy 添加表别名a
                pub.yssGetTableName("Tb_Stock_SecRecPay") + " a" +
                //----------------------------------------------------------
                " left join (select sb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("tb_para_security") +
                " where FCheckState=1 and FStartDate<= " +
                //edit by songjie 2012.10.31 BUG 6121 QDV4赢时胜(上海开发部)2012年10月29日01_B 由系统日期改为业务日期
                dbl.sqlDate(dDate) +
                " group by FSecurityCode) sa join (select FSecurityCode, FSecurityName, FStartDate, FCatCode,FSubCatCode, FTradeCury,FFactor from " +
                pub.yssGetTableName("tb_para_security") +
                " where FCheckState=1 )sb on sa.FSecurityCode = sb.FSecurityCode and sa.FStartDate = sb.FStartDate " +
                " ) sec on a.FSecurityCode = sec.FSecurityCode" +
                //-------------------------------------------------------------
                
                //------ modify by wangzuochun 2011.01.27 BUG #1018 期权业务当日估值统计期权汇兑损益时出错
                " left join (select FOptionCode,FMultiple from " + pub.yssGetTableName("Tb_Para_Optioncontract") + 
                " where FCheckState = 1 ) op on op.FOptionCode = a.FSecurityCode" + 
                //----------------------------------BUG #1018------------------------------//
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
         
                " left join (select FPortCode, FPortName, FPortCury as FCsPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1 ) m on a.fportcode= m.FPortCode" +
                
                //end by lidaolong
                //------------------------------------------------------------

                " where a.FCheckState = 1 and " +
                operSql.sqlStorageDate(dDate) +
                " and a.FPortCode = " + dbl.sqlString(this.portCode) +
                " and FTsfTypeCode In ( " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                " )) cs left join " +
                //-----------------------------------------------------------
                //取出前一日汇兑损益余额
                " (select a.*," + dbl.sqlSubStr("a.FSubTsfTypeCode", "3") +
                " as FJoinSubTsfCode from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " a where " + operSql.sqlStoragEve(dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                " and (FSubTsfTypeCode like '9906%' or FSubTsfTypeCode like '9907%' or FSubTsfTypeCode like '9909%' " +
                ") and FCheckState = 1 ) rec on cs.FCsSecurityCode = rec.FSecurityCode " +
                (this.invmgrSecField.length() != 0 ?
                 " and cs.FAnalysisCode1 = rec.FAnalysisCode1 " : " ") +
                (this.brokerSecField.length() != 0 ?
                 " and cs.FAnalysisCode2 = rec.FAnalysisCode2 " : " ") +
                " and cs.FCsSubTsfTypeCode = rec.FJoinSubTsfCode" +
                //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加 FInvestType
                " and cs.FAttrClsCode = rec.FAttrClsCode and cs.FInvestType = rec.FInvestType" + //sj add 20071209
                " left join " +
                //-----------------------------------取证券成本，计算估值增值轧差用
                " (select FSecurityCode as FCostSecCode,FPortCode as FCostPortCode,FAttrClsCode as FCostAttrClsCode,FStorageAmount," +
                " FStorageCost,FMStorageCost,FVStorageCost" +
                (this.invmgrSecField.length() != 0 ? ",FAnalysisCode1 " : " ") +
                (this.brokerSecField.length() != 0 ? ",FAnalysisCode2 " : " ") +
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FPortCode = " + dbl.sqlString(portCode) +
                " and FCheckState = 1 and " + operSql.sqlStorageDate(dDate) +
                " ) cost on cs.FCsSecurityCode = cost.FCostSecCode" +
                " and cs.FAttrClsCode = cost.FCostAttrClsCode" + //sj add 20071209
                (this.invmgrSecField.length() != 0 ?
                 " and cs.FAnalysisCode1 = cost.FAnalysisCode1 " : " ") +
                (this.brokerSecField.length() != 0 ?
                 " and cs.FAnalysisCode2 = cost.FAnalysisCode2 " : " ") +
                //----------------------------------取行情，计算估值增值轧差用
                 //----------------------------------取行情，计算估值增值轧差用
                 " left join (select mk1.* from " + pub.yssGetTableName("tb_data_valmktprice") +
                 " mk1 join (select max(FValDate) as FValDate, FSecurityCode from " +
                 pub.yssGetTableName("tb_data_valmktprice") + " where FPortCode = " +
                 dbl.sqlString(portCode) +
                 //--------MS00272 QDV4赢时胜（上海）2009年2月26日01_B ---------------------------------
                 " and FMTvCode = " + dbl.sqlString(vMethod.getMTVCode()) + //添加估值方法筛选
                 //----------------------------------------------------------------------------------
                 " and FValDate <= " + dbl.sqlDate(dDate) +
                 "group by FSecurityCode ) mk2 " +
                 " on mk1.FSecurityCode = mk2.FSecurityCode and mk1.FValDate = mk2.FValDate" +
                 " where mk1.FPortCode = " + dbl.sqlString(portCode) + //modify by wangzuochun 2010.06.23 MS01323    国泰估值报错，未明确定义列    QDV4国泰2010年6月21日01_B    
                 ") mk on " +
                 " mk.fportcode = cs.fcsportcode and mk.fsecuritycode = cs.FCsSecuritycode " +
                 //--------------------------------------------------edit by jc
                 //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType 
                 " left join (select fb.fsecuritycode,fb.FAttrClsCode, fb.FInvestType, " + //这里加上属性代码,也是主键之一,by leeyu BUG:0000437
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
                 " where fa.fnum = fb.fnum and fa.fsubtsftypecode = '9909EQ' " +
                 " and fb.fbargaindate = " + dbl.sqlDate(dDate) +
                 //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType  
                 " group by fb.fsecuritycode,fb.FattrClsCode, fb.FInvestType) ff on ff.fsecuritycode = cs.FCsSecurityCode and ff.FAttrClsCode=cs.FAttrClsCode and ff.FInvestType = cs.FInvestType " + //这里加上属性代码,也是主键之一,by leeyu BUG:0000437
             //----------------------------------------------------------jc
                 //add by songjie 2011.01.26 BUG:947 QDV4汇添富2011年1月18日01_B
                 " left join (select FMtvCode, FLinkCode from " + pub.yssGetTableName("Tb_Para_Mtvmethodlink") + 
                 " where FMtvCode = " + dbl.sqlString(vMethod.getMTVCode()) + 
                 ") mtvlink on mtvlink.FLinkCode = cs.FCsSecurityCode where FLinkCode is not null ";
                 //add by songjie 2011.01.26 BUG:947 QDV4汇添富2011年1月18日01_B
             rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
           //===add by xuxuming,2010.01.13.MS00902 指数信息调整后不能正确获取昨日估增汇兑损益余额=========
             HashMap hmRsSecOldStor=new HashMap();//保存昨日成本汇兑损益余额
             while(rs.next()){
             	String strKey = "";
             	String strValue="";
             	strKey=rs.getString("FStorageDate")+"\t"+rs.getString("FCSSecuritycode")+"\t"+rs.getString("FCSPortCode")+"\t"+rs.getString("FAttrClsCode");
             	strValue=rs.getDouble("FBaseCuryBal")+"\t"+rs.getDouble("FPortCuryBal")+"\t"+rs.getDouble("FMBaseCuryBal")+"\t"+
             	rs.getDouble("FMPortCuryBal")+"\t"+rs.getDouble("FVBaseCuryBal")+"\t"+rs.getDouble("FVPortCuryBal")+"\t"+
             	rs.getDouble("FBaseCuryBalF")+"\t"+rs.getDouble("FPortCuryBalF");
             	hmRsSecOldStor.put(strKey, strValue);
             }
             rs.beforeFirst();
             //=========end======================
             while (rs.next()) {
                 //设置汇兑损益
                 paySecRate = new SecPecPayBean();
                 paySecRate.setTransDate(dDate);
                 //--------MS00272 QDV4赢时胜（上海）2009年2月26日01_B 获取日期用于在日期优先时作判断用 -------------------------------------------------------
                 paySecRate.setEndDate(rs.getDate("FValDate") == null ? rs.getDate("FStorageDate") : rs.getDate("FValDate")); //若无获取行情时间，则获取库存日期
                 //--------

                 paySecRate.setStrSecurityCode(rs.getString("FCsSecurityCode"));           

                paySecRate.setStrPortCode(rs.getString("FCsPortCode"));
                paySecRate.setInvMgrCode(this.invmgrSecField.length() != 0 ?
                                         rs.getString(this.invmgrSecField) : " ");
                paySecRate.setBrokerCode(this.brokerSecField.length() != 0 ?
                                         rs.getString(this.brokerSecField) : " ");

                if (rs.getString("FCsCuryCode") == null) {
                    throw new YssException("请检查证券品种【" +
                                           rs.getString("FCsSecurityCode") +
                                           "】的币种设置！");
                }
                paySecRate.setStrCuryCode(rs.getString("FCsCuryCode"));
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

                sOperType = rs.getString("FCsSubTsfTypeCode");

                paySecRate.setBaseCuryRate(dBaseRate);
                paySecRate.setPortCuryRate(dPortRate);

              //===add by xuxuming.2010.01.12.MS00902 指数信息调整后，当取不到昨日成本汇兑损益余额时，重新获取另一属性代码的成本汇兑损益余额==========
                String strAttrCode = rs.getString("FAttrClsCode");// 所属分类	
                double dFBaseCuryBal = 0;
                double dFPortCuryBal = 0;
                double dMBaseCuryBal = 0;
                double dMPortCuryBal = 0;
                double dVBaseCuryBal = 0;
                double dVPortCuryBal = 0;
                double dFBaseCuryBalF = 0;
                double dFPortCuryBalF = 0;
                dFBaseCuryBal=rs.getDouble("FBaseCuryBal");
                dFPortCuryBal=rs.getDouble("FPortCuryBal");
                dMBaseCuryBal=rs.getDouble("FMBaseCuryBal");
                dMPortCuryBal=rs.getDouble("FMPortCuryBal");
                dVBaseCuryBal=rs.getDouble("FVBaseCuryBal");
                dVPortCuryBal=rs.getDouble("FVPortCuryBal");
                dFBaseCuryBalF=rs.getDouble("FBaseCuryBalF");
                dFPortCuryBalF=rs.getDouble("FPortCuryBalF");
                //start 2013-07-05 dongqingsong BUG 8546 指数型基金维护证券代码变更业务后
//    			if (strAttrCode != null
//    					&& (strAttrCode.equals("CEQ") || strAttrCode
//    							.equals("IDXEQ")) && hmRsSecOldStor !=null) {				
//    				if(dFBaseCuryBal==0&&dFPortCuryBal==0&&dMBaseCuryBal==0&&
//    						dMPortCuryBal==0&&dVBaseCuryBal==0&&dVPortCuryBal==0&&
//    						dFBaseCuryBalF==0&&dFPortCuryBalF==0){//都为零，表明首次调整后无昨日库存余额，取另一属性的昨日余额
//    					String strKey="";
//    					strKey=rs.getString("FStorageDate")+"\t"+rs.getString("FCSSecuritycode")+"\t"+rs.getString("FCSPortCode")+"\t"+(strAttrCode.equals("CEQ")?"IDXEQ":"CEQ");//取另一属性
//    					String tmpRsBal=(String)hmRsSecOldStor.get(strKey);
//    					if(tmpRsBal!=null&&tmpRsBal.trim().length()>0){
//    						String[] bufRsBal = tmpRsBal.split("\t");        						
//    						dFBaseCuryBal=new Double(bufRsBal[0]).doubleValue();
//    	                    dFPortCuryBal=new Double(bufRsBal[1]).doubleValue();
//    	                    dMBaseCuryBal=new Double(bufRsBal[2]).doubleValue();
//    	                    dMPortCuryBal=new Double(bufRsBal[3]).doubleValue();
//    	                    dVBaseCuryBal=new Double(bufRsBal[4]).doubleValue();
//    	                    dVPortCuryBal=new Double(bufRsBal[5]).doubleValue();
//    	                    dFBaseCuryBalF=new Double(bufRsBal[6]).doubleValue();
//    	                    dFPortCuryBalF=new Double(bufRsBal[7]).doubleValue();
//    					}
//    				}
//    			}
                //start 2013-07-05 dongqingsong BUG 8546 指数型基金维护证券代码变更业务后
                //===========end====================================================
                if (!rs.getString("FCsTsfTypeCode").equalsIgnoreCase("09") ||
                    //---------------------------------------------2009.6.18 胡坤 MS00476  QDV4华夏2009年6月03日01_B
                    //这句判断本来应该去掉，但是“rs.getDouble("FStorageCost") == 0”这句代码已经在客户那里使用，为了安全期间还是增加“!rs.getString("FCsCatCode").equalsIgnoreCase("EQ")”代码，解决华夏汇兑损益多4分钱的问题
                    //华夏汇兑损益多4分钱的问题原因是由于在计算每日估值增值的本位币时，原币估值增值金额没有保留2位小数，而存到数据库中的原币估值增值金额是保留2位小数的，使用轧差的算法可避免此问题
                    (rs.getDouble("FStorageCost") == 0 && !rs.getString("FCsCatCode").equalsIgnoreCase("EQ")) ||
                    //---------------------------------------------
                    (rs.getString("FCsCatCode").equalsIgnoreCase("OP") && rs.getString("FCsSubCatCode").equalsIgnoreCase("OP02")) ||
                    rs.getString("FCsCatCode").equalsIgnoreCase("FW") ||
                    rs.getString("FCsCatCode").equalsIgnoreCase("FU") || //股指期货成本都不算入净值，所以不用算尾差  胡坤 20080129
                    (rs.getString("FCsCatCode").equalsIgnoreCase("FP") && rs.getDouble("FStorageAmount") < 0)) { //股指期权数量小于0时，当做期货来处理，没有估值增值，不能使用钆差的算法 胡坤 20090717 QDV4招商证券2009年06月04日01_A  MS00484 需在系统中增加对期权业务的支持

                    //2008.11.14 蒋锦 添加 判断
                    //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                    //判断使用Round 2位小数的原币、本位币、基础货币金额计算汇兑损益，还是使用Round 8位小数的原币、本位币、基础货币金额计算汇兑损益
                    if (!bIsFXRound2) { //Round 2
                        //1.核算成本
                        dTmpMoney1 = rs.getDouble("FCsBal");
                        //---- MS00639 QDV4华夏2009年08月17日01_B sj modified -----------------//
                        if ( (rs.getString("FCsSubCatCode").equalsIgnoreCase("OP03"))||(rs.getString("FCsSubCatCode").equalsIgnoreCase("OP02"))//fanghaoln 20091124 MS00808 QDV4华夏2009年9月30日01_B 重新计算金额
                            && rs.getDouble("FPrice") != 0) { //如果类型为0P03的，并且有行情。
                            if (dTmpMoney1 != YssD.mul(rs.getDouble(
                                "FStorageAmount"),
                                rs.getDouble("FPrice"))) { //判断行情计算的原币市值，和保留的市值直接的差距。若有差距，则使用真实的用行情算出来的市值
                                dTmpMoney1 = YssD.mul(rs.getDouble(
                                    "FStorageAmount"),
                                    YssD.div(rs.getDouble("FPrice"),rs.getInt("FFactor"))); //行情×数量
                            }
                        }
                        //-------------------------------------------------------------------//
                        //计算基础货币的汇兑损益
//                        if(rs.getString("fsecuritycode").equalsIgnoreCase("YELL LN OPTION")){
//                        	int ff=10;
//                        }
                        dTmpMoney2 = rs.getDouble("FCsBaseCuryBal");
                        dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(
                            dTmpMoney1,
                            dBaseRate),
                                                 dTmpMoney2);
                        paySecRate.setBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                            rs.getDouble("FBaseCuryBal")));
                        //计算组合货币的汇兑损益
                        dTmpMoney3 = rs.getDouble("FCsPortCuryBal");
                        dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(
                            dTmpMoney1,
                            dBaseRate, dPortRate,
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FCsCuryCode"), this.dDate, this.portCode), dTmpMoney3);
                        paySecRate.setPortCuryMoney(YssD.sub(dTmpPortMoney,
                            rs.getDouble("FPortCuryBal")));
                    } else { //Round 8
                        //1.核算成本
                        dTmpMoney1 = rs.getDouble("FCsBalF");
                        //计算基础货币的汇兑损益
                        dTmpMoney2 = rs.getDouble("FCsBaseCuryBalF");
                        dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(
                            dTmpMoney1,
                            dBaseRate, 8),
                                                 dTmpMoney2);
                        paySecRate.setBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                            rs.getDouble("FBaseCuryBalF")));
                        //计算组合货币的汇兑损益
                        dTmpMoney3 = rs.getDouble("FCsPortCuryBalF");
                        dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(
                            dTmpMoney1,
                            dBaseRate, dPortRate,
                            //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FCsCuryCode"), dDate, this.portCode, 8), dTmpMoney3);
                        paySecRate.setPortCuryMoney(YssD.sub(dTmpPortMoney,
                            rs.getDouble("FPortCuryBalF")));

                    }
                    //2.管理成本
                    dTmpMoney1 = rs.getDouble("FCsMBal");
                    //计算基础货币的汇兑损益
                    dTmpMoney2 = rs.getDouble("FCsMBaseCuryBal");
                    dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(
                        dTmpMoney1,
                        dBaseRate), dTmpMoney2);
                    paySecRate.setMBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                        rs.getDouble("FMBaseCuryBal")));
                    //计算组合货币的汇兑损益
                    dTmpMoney3 = rs.getDouble("FCsMPortCuryBal");
                    dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(
                        dTmpMoney1,
                        dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCsCuryCode"), this.dDate, this.portCode), dTmpMoney3);
                    paySecRate.setMPortCuryMoney(YssD.sub(dTmpPortMoney,
                        rs.getDouble("FMPortCuryBal")));

                    //3.估值成本
                    dTmpMoney1 = rs.getDouble("FCsVBal");
                    //计算基础货币的汇兑损益
                    dTmpMoney2 = rs.getDouble("FCsVBaseCuryBal");
                    dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(
                        dTmpMoney1,
                        dBaseRate), dTmpMoney2);
                    paySecRate.setVBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                        rs.getDouble("FVBaseCuryBal")));
                    //计算组合货币的汇兑损益
                    dTmpMoney3 = rs.getDouble("FCsVPortCuryBal");
                    dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(
                        dTmpMoney1,
                        dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCsCuryCode"), this.dDate, this.portCode), dTmpMoney3);
                    paySecRate.setVPortCuryMoney(YssD.sub(dTmpPortMoney,
                        rs.getDouble("FVPortCuryBal")));
                } else { //计算估值增值汇兑损益的时候通过轧差处理  胡昆  20070930
                    //-------------------1.核算成本
                    //计算基础货币的估值增值汇兑损益
                    dMktPrice = rs.getDouble("FPrice");
                    if (rs.getDouble("FPrice") != 0) {
                        //是否四舍五入
                        if (bIsRound) {
                        	
                        	//------ modify by wangzuochun 2011.01.27 BUG #1018 期权业务当日估值统计期权汇兑损益时出错
                        	if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        		//2008.09.01 蒋锦 修改 添加 Round 计算市值
                                dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple")), 2);
                        	}
                        	else{
                        		//2008.09.01 蒋锦 修改 添加 Round 计算市值
                                dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice), 2);
                        	}
                        } else {
                        	if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple"));
                        	}
                        	else{
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice);
                        	}
                        	//-----------------------------BUG #1018-------------------------------//
                        }
                    } else {
                        //2008.11.14 蒋锦 添加 判断
                        //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                        //判断使用Round 2位小数的原币、本位币、基础货币金额计算汇兑损益，还是使用Round 8位小数的原币、本位币、基础货币金额计算汇兑损益
                        if (!bIsFXRound2) {
                            dTmpMoney1 = YssD.add(rs.getDouble("FStorageCost"),
                                                  rs.getDouble("FCsBal")); //没有行情时取成本+估值增值余额
                        } else {
                            dTmpMoney1 = YssD.add(rs.getDouble("FStorageCost"),
                                                  rs.getDouble("FCsBalF"));
                        }
                    }
                    //2008.11.14 蒋锦 添加
                    //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                    //判断使用Round 2位小数的原币、本位币、基础货币金额计算汇兑损益，还是使用Round 8位小数的原币、本位币、基础货币金额计算汇兑损益
                    if (!bIsFXRound2) { //Round 2
                        dTmpMoney1 = this.getSettingOper().calBaseMoney(dTmpMoney1,
                            dBaseRate); //数量*行情*汇率得到证券基础货币市值
                        dTmpMoney2 = this.getSettingOper().calBaseMoney(rs.getDouble(
                            "FStorageCost"), dBaseRate); //计算成本的当前基础货币金额
                        dTmpBaseMoney = YssD.sub(dTmpMoney1, dTmpMoney2); //证券市值-成本的当前基础货币金额
                        paySecRate.setBaseCuryMoney(YssD.sub(YssD.sub(dTmpBaseMoney,
                            rs.getDouble("FCsBaseCuryBal")),
                            /*rs.getDouble("FBaseCuryBal")*/dFBaseCuryBal)); //证券市值-成本的当前基础货币金额-估值增值基础货币金额-昨日基础汇兑损益余额=估值增值汇兑损益调整金额
                    } else { //Round 8
                        dTmpMoney1 = this.getSettingOper().calBaseMoney(dTmpMoney1,
                            dBaseRate, 8);
                        dTmpMoney2 = this.getSettingOper().calBaseMoney(rs.getDouble(
                            "FStorageCost"), dBaseRate, 8);
                        dTmpBaseMoney = YssD.sub(dTmpMoney1, dTmpMoney2);
                        paySecRate.setBaseCuryMoney(YssD.sub(YssD.sub(dTmpBaseMoney,
                            rs.getDouble("FCsBaseCuryBalF")),
                            /*rs.getDouble("FBaseCuryBalF")*/dFBaseCuryBalF));
                    }
                    //计算组合货币的估值增值汇兑损益
                    if (rs.getDouble("FPrice") != 0) {
                        if (bIsRound) {
                            //2008.09.01 蒋锦 修改 添加 Round 计算市值
                        	if(bMVIsRound2){//QDV4汇添富2011年01月10日01_A  组合货币市值 = round[数量*价格*基础汇率/组合汇率,2]
                        		//------ modify by wangzuochun 2011.01.27 BUG #1018 期权业务当日估值统计期权汇兑损益时出错
                        		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        			dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple"));
                        		}
                        		else{
                        			dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice);
                        		}
                                
                        	}else{
                        		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        			dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple")), 2);
                        		}
                        		else{
                        			dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice), 2);
                        		}                      		
                        	}
                        } else {
                        	if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple"));
                        	}
                        	else{
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice);
                        	} 
                        }
                        //-----------------------------------------BUG #1018--------------------------------------------//
                    } else {
                        //2008.11.14 蒋锦 添加 判断
                        //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                        if (!bIsFXRound2) {
                            dTmpMoney1 = YssD.add(rs.getDouble("FStorageCost"),
                                                  rs.getDouble("FCsBal"));
                        } else {
                            dTmpMoney1 = YssD.add(rs.getDouble("FStorageCost"),
                                                  rs.getDouble("FCsBalF"));
                        }
                    }
                    //2008.11.14 蒋锦 添加
                    //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                    //判断使用Round 2位小数的金额计算汇兑损益，还是使用Round 8位小数的金额计算汇兑损益
                    if (!bIsFXRound2) { //Round 2
                        dTmpMoney1 = this.getSettingOper().calPortMoney(dTmpMoney1,
                                dBaseRate, dPortRate,
                                //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                rs.getString("FCsCuryCode"), this.dDate, this.portCode); //数量*行情*汇率得到证券组合货币市值
                        dTmpMoney2 = this.getSettingOper().calPortMoney(rs.getDouble(
                            "FStorageCost"), dBaseRate, dPortRate,
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FCsCuryCode"), this.dDate, this.portCode); //计算成本的当前组合货币金额
                        dTmpPortMoney = YssD.sub(dTmpMoney1, dTmpMoney2); //证券市值-成本的当前基础货币金额
                        paySecRate.setPortCuryMoney(YssD.sub(YssD.sub(dTmpPortMoney,
                            rs.getDouble("FCsPortCuryBal")),
                            /*rs.getDouble("FPortCuryBal")*/dFPortCuryBal)); //证券市值-成本的当前基础货币金额-估值增值组合货币金额-昨日组合汇兑损益余额=估值增值汇兑损益
                    } else {
                        dTmpMoney1 = this.getSettingOper().calPortMoney(dTmpMoney1,
                            dBaseRate, dPortRate,
                            //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FCsCuryCode"), dDate, this.portCode, 8);
                        dTmpMoney2 = this.getSettingOper().calPortMoney(rs.getDouble(
                            "FStorageCost"), dBaseRate, dPortRate,
                            //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FCsCuryCode"), dDate, this.portCode, 8);
                        dTmpPortMoney = YssD.sub(dTmpMoney1, dTmpMoney2);
                        paySecRate.setPortCuryMoney(YssD.sub(YssD.sub(dTmpPortMoney,
                            rs.getDouble("FCsPortCuryBalF")),
                           /* rs.getDouble("FPortCuryBalF")*/dFPortCuryBalF));
                    }
                    //-----------------------------------------------------------------------------
                    //-------------------2.管理成本
                    //计算基础货币的估值增值汇兑损益
                    if (rs.getDouble("FPrice") != 0) {
                        if (bIsRound) {
                        	//------ modify by wangzuochun 2011.01.27 BUG #1018 期权业务当日估值统计期权汇兑损益时出错
                    		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                    			//2008.09.01 蒋锦 修改 添加 Round 计算市值
                                dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple")), 2);
                    		}
                    		else{
                    			//2008.09.01 蒋锦 修改 添加 Round 计算市值
                                dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice), 2);
                    		}
                            
                        } else {
                        	if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple"));
                        	}
                        	else{
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice);
                        	}
                        	//-----------------------------------------BUG #1018--------------------------------------------//
                        }
                    } else {
                        dTmpMoney1 = YssD.add(rs.getDouble("FMStorageCost"), rs.getDouble("FCsMBal")); //没有行情时取成本+估值增值余额
                    }
                    dTmpMoney1 = this.getSettingOper().calBaseMoney(dTmpMoney1,
                        dBaseRate); //数量*行情*汇率得到证券基础货币市值
                    dTmpMoney2 = this.getSettingOper().calBaseMoney(rs.getDouble(
                        "FMStorageCost"), dBaseRate); //计算成本的当前基础货币金额
                    dTmpBaseMoney = YssD.sub(dTmpMoney1, dTmpMoney2); //证券市值-成本的当前基础货币金额
                    paySecRate.setMBaseCuryMoney(YssD.sub(YssD.sub(dTmpBaseMoney,
                        rs.getDouble("FCsMBaseCuryBal")), /*rs.getDouble("FMBaseCuryBal")*/dMBaseCuryBal)); //证券市值-成本的当前基础货币金额-估值增值基础货币金额-昨日基础汇兑损益余额=估值增值汇兑损益
                    //计算组合货币的估值增值汇兑损益
                    if (rs.getDouble("FPrice") != 0) {
                        if (bIsRound) {
                            //2008.09.01 蒋锦 修改 添加 Round 计算市值
                        	if(bMVIsRound2){//QDV4汇添富2011年01月10日01_A  组合货币市值 = round[数量*价格*基础汇率/组合汇率,2]
                        		//------ modify by wangzuochun 2011.01.27 BUG #1018 期权业务当日估值统计期权汇兑损益时出错
                        		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        			dTmpMoney1 = YssD.mul(rs.getDouble( "FStorageAmount"), dMktPrice,rs.getDouble("FMultiple"));
                        		}
                        		else{
                        			dTmpMoney1 = YssD.mul(rs.getDouble( "FStorageAmount"), dMktPrice);
                        		}
                                
                        	}else{
                        		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        			dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple")), 2);
                        		}
                        		else{
                        			dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice), 2);
                        		}
                        	}
                        } else {
                        	if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple"));
                        	}
                        	else{
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice);
                        	}
                        	//-----------------------------------------BUG #1018--------------------------------------------//
                        }
                    } else {
                        dTmpMoney1 = YssD.add(rs.getDouble("FMStorageCost"), rs.getDouble("FCsMBal")); //没有行情时取成本+估值增值余额
                    }

                    dTmpMoney1 = this.getSettingOper().calPortMoney(dTmpMoney1,
                            dBaseRate, dPortRate,
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FCsCuryCode"), this.dDate, this.portCode); //数量*行情*汇率得到证券组合货币市值
                    dTmpMoney2 = this.getSettingOper().calPortMoney(rs.getDouble(
                        "FMStorageCost"), dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCsCuryCode"), this.dDate, this.portCode); //计算成本的当前组合货币金额
                    dTmpPortMoney = YssD.sub(dTmpMoney1, dTmpMoney2); //证券市值-成本的当前基础货币金额
                    paySecRate.setMPortCuryMoney(YssD.sub(YssD.sub(dTmpPortMoney,
                        rs.getDouble("FCsMPortCuryBal")), /*rs.getDouble("FMPortCuryBal")*/dMPortCuryBal)); //证券市值-成本的当前基础货币金额-估值增值组合货币金额-昨日组合汇兑损益余额=估值增值汇兑损益
                    //-----------------------------------------------------------------------------
                    //-------------------3.估值成本
                    //计算基础货币的估值增值汇兑损益
                    if (rs.getDouble("FPrice") != 0) {
                        if (bIsRound) {
                        	//------ modify by wangzuochun 2011.01.27 BUG #1018 期权业务当日估值统计期权汇兑损益时出错
                    		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                    			dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple")), 2);
                    		}
                    		else{
                    			//2008.09.01 蒋锦 修改 添加 Round 计算市值
                                dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice), 2);
                    		}
                            
                        } else {
                        	if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple"));
                        	}
                        	else{
                        		dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice);
                        	}
                        	//-----------------------------------------BUG #1018--------------------------------------------//
                        }
                    } else {
                        dTmpMoney1 = YssD.add(rs.getDouble("FVStorageCost"), rs.getDouble("FCsVBal")); //没有行情时取成本+估值增值余额
                    }
                    dTmpMoney1 = this.getSettingOper().calBaseMoney(dTmpMoney1,
                        dBaseRate); //数量*行情*汇率得到证券基础货币市值
                    dTmpMoney2 = this.getSettingOper().calBaseMoney(rs.getDouble(
                        "FVStorageCost"), dBaseRate); //计算成本的当前基础货币金额
                    dTmpBaseMoney = YssD.sub(dTmpMoney1, dTmpMoney2); //证券市值-成本的当前基础货币金额
                    paySecRate.setVBaseCuryMoney(YssD.sub(YssD.sub(dTmpBaseMoney,
                        rs.getDouble("FCsVBaseCuryBal")), /*rs.getDouble("FVBaseCuryBal")*/dVBaseCuryBal)); //证券市值-成本的当前基础货币金额-估值增值基础货币金额-昨日基础汇兑损益余额=估值增值汇兑损益
                    //计算组合货币的估值增值汇兑损益
                    if (rs.getDouble("FPrice") != 0) {
                        if (bIsRound) {
                            //2008.09.01 蒋锦 修改 添加 Round 计算市值
                        	if(bMVIsRound2){//QDV4汇添富2011年01月10日01_A  组合货币市值 = round[数量*价格*基础汇率/组合汇率,2]
                        		//------ modify by wangzuochun 2011.01.27 BUG #1018 期权业务当日估值统计期权汇兑损益时出错
                        		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        			dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple"));    
                        		}
                        		else{
                        			dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice);    
                        		}
                                                    		
                        	}else{
                        		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                        			dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple")), 2); 
                        		}
                        		else{
                        			dTmpMoney1 = YssD.round(YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice), 2); 
                        		}
                                     		
                        	}
                        } else {
                    		if (rs.getDouble("FMultiple") != 0 && "09FP01".equals(rs.getString("FCSSubTsftypecode"))) {
                    			dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice,rs.getDouble("FMultiple"));
                    		}
                    		else{
                    			dTmpMoney1 = YssD.mul(rs.getDouble("FStorageAmount"),dMktPrice);
                    		}
                    		//-----------------------------------------BUG #1018--------------------------------------------//
                        }
                    } else {
                        dTmpMoney1 = YssD.add(rs.getDouble("FVStorageCost"), rs.getDouble("FCsVBal")); //没有行情时取成本+估值增值余额
                    }
                    dTmpMoney1 = this.getSettingOper().calPortMoney(dTmpMoney1,
                            dBaseRate, dPortRate,
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FCsCuryCode"), this.dDate, this.portCode); //数量*行情*汇率得到证券组合货币市值
                    dTmpMoney2 = this.getSettingOper().calPortMoney(rs.getDouble(
                        "FVStorageCost"), dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCsCuryCode"), this.dDate, this.portCode); //计算成本的当前组合货币金额
                    dTmpPortMoney = YssD.sub(dTmpMoney1, dTmpMoney2); //证券市值-成本的当前基础货币金额
                    paySecRate.setVPortCuryMoney(YssD.sub(YssD.sub(dTmpPortMoney,
                        rs.getDouble("FCsVPortCuryBal")), /*rs.getDouble("FVPortCuryBal")*/dVPortCuryBal)); //证券市值-成本的当前基础货币金额-估值增值组合货币金额-昨日组合汇兑损益余额=估值增值汇兑损益
                    //-----------------------------------------------------------------------------
                }

                paySecRate.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX);
                paySecRate.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX +
                                                sOperType);
                paySecRate.setAttrClsCode(rs.getString("FAttrClsCode")); //sj add 20071209
                //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                paySecRate.setInvestType(rs.getString("FInvestType"));
                paySecRate.checkStateId = 1;
//----------MS00272  QDV4赢时胜（上海）2009年2月26日01_B 在此处也和浮动盈亏一样，用通用参数来进行控制--------
                if (priMarketPrice.toLowerCase().equalsIgnoreCase("valuation")) {
                    // 将放置估值值的位置放入各自的通用参数选择项中，视各自的情况而定--//
                    sKey = paySecRate.getStrPortCode() + "\f" +
                        paySecRate.getStrSecurityCode() + "\f" +
                        (this.invmgrSecField.length() != 0 ?
                         (paySecRate.getInvMgrCode() + "\f") : "") +
                        (this.brokerSecField.length() != 0 ?
                         (paySecRate.getBrokerCode() + "\f") : "") +
                        paySecRate.getStrSubTsfTypeCode() + "\f" +
                        //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType
                        (paySecRate.getAttrClsCode().length() == 0 ? " " : paySecRate.getAttrClsCode()) + "\f" + paySecRate.getInvestType();
                    hmResult.put(sKey, paySecRate);
                    hmValRate.put(paySecRate.getStrCuryCode(), paySecRate);
                } else if (priMarketPrice.toLowerCase().equalsIgnoreCase("day")) {
                    //-----------若其它行情来源的行情比之前行情来源的行情数据更新，则代替此行情数据。//
                    sKey = paySecRate.getStrPortCode() + "\f" +
                        paySecRate.getStrSecurityCode() + "\f" +
                        (this.invmgrSecField.length() != 0 ?
                         (paySecRate.getInvMgrCode() + "\f") : "") +
                        (this.brokerSecField.length() != 0 ?
                         (paySecRate.getBrokerCode() + "\f") : "") +
                        paySecRate.getStrSubTsfTypeCode() + "\f" +
                        //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType
                        (paySecRate.getAttrClsCode().length() == 0 ? " " : paySecRate.getAttrClsCode()) + "\f" + paySecRate.getInvestType();
                    if (hmResult.get(sKey) != null) { //若之前有
                        oldMktRate = (SecPecPayBean) hmResult.get(sKey); //获取之前的数据
                        if (YssFun.dateDiff(oldMktRate.getEndDate(),
                                            paySecRate.getEndDate()) >= 0) { //判断日期是否大于等于之前的数据
                            hmValRate.remove(oldMktRate.getStrCuryCode()); //替换数据
                            hmValRate.put(paySecRate.getStrCuryCode(), paySecRate);
                            hmResult.put(sKey, paySecRate);
                        }
                    } else { //若没有
                        hmResult.put(sKey, paySecRate);//加入新数据
                        hmValRate.put(paySecRate.getStrCuryCode(), paySecRate);
                    }
//---------------------------------------------------------------------------------------------------------------------------//
                }
            }
            dbl.closeResultSetFinal(rs);
        } catch (Exception e) {
            throw new YssException("系统进资产估值,在执行证券类综合损益的汇兑损益计算时出现异常!" + "\n", e); //by 曹丞 2009.02.01 证券类综合损益的汇兑损益计算异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void getCashValCats(MTVMethodBean vMethod, HashMap hmResult) throws
        YssException {
        double dTmpMoney1 = 0;
        double dTmpMoney2 = 0;
        double dTmpMoney3 = 0;
        ResultSet rs = null;
        String sKey = "", sOperType = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        String strSql = "";
        CashPecPayBean payCashRate = null;
        double dTmpBaseMoney = 0;
        double dTmpPortMoney = 0;

        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090417 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------

        //------------------------------------------------------------------
        //存款综合损益
        try {
            strSql =
                " select a.*, b.FCuryCode, m.FPortCury,pay.FBal as FHDSYBal," +
                "pay.FPortCuryBal as FHDSYPortBal,pay.FBaseCuryBal as FHDSYBaseBal " +
                " from (select FCashAccCode, FStorageDate, FPortCode, FTsfTypeCode, FSubTsfTypeCode," +
                dbl.sqlIsNull("FBal", "0") + " as FBal, " +
                dbl.sqlIsNull("FBaseCuryBal") + " as FBaseCuryBal, " +
                dbl.sqlIsNull("FPortCuryBal") +
                " as FPortCuryBal" +
                
                //------ modify by wangzuochun 2010.08.31  MS01624    库存信息配置界面,将现金类配置分析代码三，估值报错    QDV4赢时胜(测试)2010年08月19日1_B   
                (this.invmgrCashField.length() != 0 ? "," + this.invmgrCashField : " ") +
                (this.catCashField.length() != 0 ? "," + this.catCashField : " ") +
                //------------------------------MS01624-----------------------------------//
                
                
                " from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where fcheckstate=1 and FStorageDate = " +
                dbl.sqlDate(dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FTsfTypeCode In ( " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                " )) a " +
                //-应考虑重复记录，但鉴于之后不再使用启用日期，因此直接删除启用日期的判断-
                //---modify by sunkey 20090615 BugNO:MS00413 QDV4赢时胜（上海）2009年4月24日03_B
                " left join (select FCashAccCode, FCuryCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " Where FCheckState = 1 " +
                ") b on a.FCashAccCode=b.FCashAccCode " +
                //----------------------End MS00413--------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
                
                " left join (select FPortCode, FPortName,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1) m on a.FPortCode = m.FPortCode " +
                
                //end by lidaolong
                //---------------------------------------------------------
                //取出前一日汇兑损益余额

//                  " (select a.*," + dbl.sqlRight("a.FSubTsfTypeCode", 2) +
//                 " as FJoinSubTsfCode from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
//                 " a where " + operSql.sqlStoragEve(dDate) +

                " left join (select x.*," +
                //取右边四位有问题，现在资金调拨的位数越来越多的，fazmm20071016
                // dbl.sqlRight("x.FSubTsfTypeCode", 4) +
                dbl.sqlSubStr("x.FSubTsfTypeCode", "3") +
                " as FJoinSubTsfCode from " +
                pub.yssGetTableName("tb_stock_cashpayrec") +
                " x where FCheckState = 1 and" +
                operSql.sqlStoragEve(dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                " and (FSubTsfTypeCode like '9906%' or FSubTsfTypeCode like '9907%')" +
                " ) pay on a.FCashAccCode = pay.FCashAccCode " +
                
                //------ modify by wangzuochun 2010.08.31  MS01624    库存信息配置界面,将现金类配置分析代码三，估值报错    QDV4赢时胜(测试)2010年08月19日1_B   
                (this.invmgrCashField.length() != 0 ?
                 " and a." + this.invmgrCashField + " = pay." + this.invmgrCashField  : " ") +
                (this.catCashField.length() != 0 ?
                 " and a." + this.catCashField + " = pay." + this.catCashField  : " ") +
                 " and a.FSubTsfTypeCode = pay.FJoinSubTsfCode";
                 //------------------------------MS01624----------------------------------//

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                payCashRate = new CashPecPayBean();
                payCashRate.setTradeDate(this.dDate);

                payCashRate.setCashAccCode(rs.getString("FCashAccCode"));
                payCashRate.setPortCode(rs.getString("FPortCode"));
                payCashRate.setInvestManagerCode(this.invmgrCashField.length() !=
                                                 0 ? rs.getString(this.
                    invmgrCashField) : " ");
                payCashRate.setCategoryCode(this.catCashField.length() != 0 ?
                                            rs.getString(this.catCashField) :
                                            " ");

                payCashRate.setCuryCode(rs.getString("FCuryCode"));
                if (rs.getString("FCuryCode") == null) {
                    throw new YssException("请检查现金帐户【" +
                                           rs.getString("FCashAccCode") +
                                           "】的币种设置！");
                }

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

                sOperType = rs.getString("FSubTsfTypeCode");

                payCashRate.setBaseCuryRate(dBaseRate);
                payCashRate.setPortCuryRate(dPortRate);

                dTmpMoney1 = rs.getDouble("FBal");
                //计算基础货币的汇兑损益
                dTmpMoney2 = rs.getDouble("FBaseCuryBal");
                int digit = this.getSettingOper().getRoundDigit(dTmpMoney1); //获取库存中的金额的小数位，使得在之后的计算基础和组合获取的金额时不会出现进位的不同引起的差额。 sj edit 20080829 bug号 0000444
                dTmpBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(
                    dTmpMoney1,
                    dBaseRate, digit), //按照之前获取的小数位来进位。sj edit 20080829 暂无 bug号
                                         dTmpMoney2);
                payCashRate.setBaseCuryMoney(YssD.sub(dTmpBaseMoney,
                    rs.getDouble("FHDSYBaseBal")));

                //计算组合货币的汇兑损益
                dTmpMoney3 = rs.getDouble("FPortCuryBal");
                dTmpPortMoney = YssD.sub(this.getSettingOper().calPortMoney(
                    dTmpMoney1,
                    dBaseRate, dPortRate,
                    //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCuryCode"), dDate, this.portCode, digit), dTmpMoney3); //按照之前获取的小数位来进位。sj edit 20080829 暂无 bug号
                payCashRate.setPortCuryMoney(YssD.sub(dTmpPortMoney,
                    rs.getDouble("FHDSYPortBal")));

                payCashRate.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX);
                payCashRate.setSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX +
                                              sOperType);

                payCashRate.checkStateId = 1;

                sKey = payCashRate.getPortCode() + "\f" +
                    payCashRate.getCashAccCode() +
                    "\f" +
                    (this.invmgrCashField.length() != 0 ?
                     (payCashRate.getInvestManagerCode() + "\f") : "") +
                    (this.catCashField.length() != 0 ?
                     (payCashRate.getCategoryCode() + "\f") : "") +
                    payCashRate.getSubTsfTypeCode();
                //      calculateAdjust(payCashRate);
                hmResult.put(sKey, payCashRate);
                hmValRate.put(payCashRate.getCuryCode(), payCashRate);
            }
            dbl.closeResultSetFinal(rs);
        } catch (Exception e) {
            throw new YssException("系统资产估值,在执行现金类综合损益的汇兑损益计算时出现异常!" + "\n", e); //by 曹丞 2009.02.01 现金类综合损益的汇兑损益计算异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

   public Object filterCashCondition() {
      CashPecPayBean cashpay = new CashPecPayBean();
      cashpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX);
      cashpay.setSubTsfTypeCode(
    		
            //添加了9906FE，9906FE这两个子调拨类型  sj edit 20080324 xuqiji 20091204 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
    		//增加9906FU02 fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A  
    		//增加9906FU03 fangjiang 2011.02.15 STORY #462
    		//添加'9907RE02','9907RE03' by fangjiang 2010.11.05 BUG #214 
    		//增加9907LI guyichuan 2011.05.22 STORY #561
            //增加了9906GZ 应收款项-挂账汇兑损益  ,9907GZ 应付款项-挂账汇兑损益 shashijie 2011-08-26 STORY 1327
    		//增加了9907SE 应收款项-挂账汇兑损益   shashijie 2011-09-07 STORY 1447,1561
    		//增加 ,嘉实资本利得税没有计算汇兑损益  dongqingsong 2013-10-11 BUG #79704
    		"'9907SE',"+   //modify huangqirong 2012-08-24 bug #5324 去掉前后的空格，不应该存在空格 否则值的内容就不一样的
      		"'9906XZ','9907XZ','9906GZ','9907GZ','9906DE','9907DE','9906OH','9906TA','9907OH','9906TD','9907TD'" +
      		",'9906CE','9907CE','9907TA','9907TA_Fee','9906OT','9907OT','9906DV','9907DV','9906FE','9907FE','9906IM','9906FU01'," +//资产估值赎回费分开统计  yeshenghong 20130313
      		"'9906FU02','9906FU03','9906FU04','9906PF','9906AP_EQ','9906AP_FI','9906TR','9906DV_TR','9907FD','9906TA_CB'," + // modify huangqirong 2012-08-21  商品期货
      		"'9907TA_CB','9906TA_CR','9907TA_CR','9907TA_IDS','9907TA_IDB','9907TA_IDB_HD','9907TA_IDS_HD'," +
      		"'9907TA_IDB_HD_1','9907TA_IDS_HD_1','9907TA_CBCB','9907TA_CBCS','9909CR','9907TA_JYSHR_SG'," +
      		"'9907TA_JYSHR_SH','9903TA_JYSHR_SG','9903TA_JYSHR_SH','9907RE01','9907RE02','9907RE03','9907PLI'," +//add by zhaoxianlin 20121126  #story 3208 应付借贷利息
      		"'9907RE','9907SB','9907LI','9907LXS','9907LXS_FI','9907LXS_DE','9907TF_EQ','9907TF_FI','9907TF_DE','9907TF'," +
      		//add by songjie 2012.10.26 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 添加 9906TAZR,9907TAZC TA应收转入款汇兑损益、TA应付转出款汇兑损益
      		"9906TAZR,9907TAZC"+
      		"9907CGT_EQ,9907CGT_FI,9907CGT_TR,9907CGT_DR,9907CGT_RT,9907CGT_OP"//增加 ,嘉实资本利得税没有计算汇兑损益  dongqingsong 2013-10-11 BUG #79704
      		);//edit by zhouwei 20120228 增加了债券利息税,存款利息税  固定交易费用
      //MS00398 QDV4赢时胜（上海）2009年4月21日06_B 添加申购款的汇兑损益 //添加回购应付费用的汇兑与回购利息汇兑 by leeyu 20100327

      //增加股指期货汇兑损益的调拨类型。sj edit 20080921
      //调拨类型依次是1.应收存款利息汇兑损益,2.应付存款利息汇兑损益,3.其他应收款项汇兑损益,4.其他应付款项汇兑损益,5.应收清算款汇兑损益,6.应付清算款汇兑损益   胡昆  20070918
      //增加ETF应收应付汇兑损益'9906TA_CB','9907TA_CB','9906TA_CR','9907TA_CR','9907TA_IDS','9907TA_IDB','9907TA_IDB_HD','9907TA_IDS_HD','9907TA_IDB_HD_1','9907TA_IDS_HD_1','9907TA_CBCB','9907TA_CBCS','9909CR' ,panjunfang add 
      return cashpay;
   }

   public Object filterSecCondition() {
      SecPecPayBean secpay = new SecPecPayBean();
      secpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX);

      //调拨类型依次是1.应收债券利息汇兑损益,2.应付债券利息汇兑损益,3.股票估值增值汇兑损益,4.债券估值增值汇兑损益   胡昆  20070918
      //Jw20071221需增加回购'9906RE'
      //添加了9906FE，9906FE这两个子调拨类型  sj edit 20080324
        //xuqiji 20090629:QDV4招商证券2009年06月04日01_A  MS00484 需在系统中增加对期权业务的支持
      //9909B_ST（B股估值增值汇兑损益）panjunfang add 20100504
      //增加9909FU02 fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A
      //增加9909FU03 fangjiang 2011.02.15 STORY #462
      //添加'9907RE02','9907RE03' by fangjiang 2010.11.05 BUG #214 
      //添加'9907CGT' by fangjiang 2011.05.23 story 845
      //添加9906TR by songjie 2012.03.07 BUG 3983 QDV4银华2012年03月06日01_B
      secpay.setStrSubTsfTypeCode(
            "'9906RE','9906FI','9907FI','9909EQ','9909FI','9909OP','9909TR','9909DR'," +
            "'9909TC','9909FW','9906DVEQ','9906DVTR','9906DVOP','9906DVFI','9906EQ','9907PLI'," +  //add by zhaoxianlin 20121126  #story 3208 应付借贷利息
            "'9909RT','9909FU01','9909FU02','9909FU03','9909FU04','9906FE','9907FE','9906IM'," + //modify huangqirong 2012-08-21  商品期货
            "'9909PN','9906PN','9907PN','9909FP01','9907RE01','9907RE02','9907RE03'," +
            "'9907RE','9909B_ST','9907SB','9907CGT','9906TR','9907CGT_FI',"+//PN新添加的类型 sj edit 20080814 //添加回购应付费用的汇兑与回购利息汇兑 by leeyu 20100327 add by zhouwei 20120228  债券资本利得税
      		"'9907CGT_EQ','9907CGT_TR','9907CGT_DR','9907CGT_RT','9907CGT_OP'"); // 增加 ,嘉实资本利得税没有计算汇兑损益  dongqingsong 2013-10-11 BUG #79704
            
        //-----------------------------end---------------------------------//
      return secpay;
   }
}
