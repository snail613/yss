package com.yss.main.operdeal.valuation;

//
//import java.util.*;   //此类就不用了，估值方法已经在ValSecsMV.java中处理了，因此下面的代码注释掉，避免重复处理数据 by leeyu 20090307
//import com.yss.util.*;//QDV4建行2009年3月5日02_B MS00288
//import com.yss.main.operdata.*;
//import com.yss.main.parasetting.*;
//import java.sql.*;
//import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
//
///**
// * <p>Title: </p>
// * <p>Description:配股权证的估值 </p>
// * <p>Copyright: Copyright (c) 2003</p>
// * <p>Company: Ysstech</p>
// * @author hukun
// * @version 1.0
// */
//
public class ValRightsIssueMV
    extends BaseValDeal {
    public ValRightsIssueMV() {
    }
//
//   /**
//    * getValuationCats
//    *
//    * @param mtvBeans ArrayList
//    * @return HashMap
//    */
//   public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
//
//      String strSql = "";
//      MTVMethodBean vMethod = null;
//      ResultSet rs = null;
//      int iFactor = 1; //报价因子
//      String sKey = "", sCatCode = "";
//
//      double dBaseRate = 1;
//      double dPortRate = 1;
//      double dMarketPrice = 0;
//
//      double dTmpMoney = 0;
//      double dTmpMValue = 0;
//      double dTmpAmount = 0;
//
//      SecPecPayBean secPay = null;
//
//      HashMap hmResult = new HashMap();
//
//      StringBuffer buf = new StringBuffer();
//      boolean bIsRound = false;
//      try {
//         //--------------2008.09.04 蒋锦 添加 从通用参数获取计算市值时是否四舍五入两位小数---------------//
//         CtlPubPara pubPara = new CtlPubPara();
//         pubPara.setYssPub(pub);
//         bIsRound = pubPara.getMVIsRound();
//         //---------------------------------------------------------------------------------------//
//
//         for (int i = 0; i < mtvBeans.size(); i++) {
//            vMethod = (MTVMethodBean) mtvBeans.get(i);
//
//            strSql = " select cs.*,FBal,FMBal,FVBal,FBaseCuryBal,FMBaseCuryBal,FVBaseCuryBal,FPortCuryBal,FMPortCuryBal,FVPortCuryBal," +
//                  " mk.FCsMarketPrice,mkri.FRiMktPrice, mkri.FRiMktDate, mk.FMktValueDate, m.FPortCury from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode," +
//                  " FStorageAmount, FStorageCost, FMStorageCost, FVStorageCost," +
//                  " FBaseCuryCost as FCsBaseCuryCost, FMBaseCuryCost as FCsMBaseCuryCost, FVBaseCuryCost as FCsVBaseCuryCost," +
//                  " FPortCuryCost as FCsPortCuryCost, FMPortCuryCost as FCsMPortCuryCost, FVPortCuryCost as FCsVPortCuryCost," +
//                  " ri.FSecurityCode as FRiSecurityCode,ri.FBeginScriDate,ri.FEndScriDate,ri.FBeginTradeDate,ri.FEndTradeDate,ri.FRIPrice" +
//                  //判断是否配置分析代码，杨
//                  (this.invmgrSecField.length() != 0 ?
//                   ("," + this.invmgrSecField) : " ") +
//                  (this.brokerSecField.length() != 0 ?
//                   ("," + this.brokerSecField) : " ") +
//                  ",a.FPortCode as FCsPortCode,a.FAttrClsCode as FAttrClsCode, sec.FTradeCury as FCsCuryCode, sec.FCatCode as FCsCatCode, " + //sj 20071204 add new field for new key
//                  " sec.FSubCatCode as FCsSubCatCode, sec.FFactor as FCsFactor,FCatCode,FSubCatCode from " + // wdy 添加表别名a
//                  pub.yssGetTableName("tb_stock_security") + " a" +
//                  //------------------------------------------------------------
//                  " join (select FSecurityCode, FSecurityName, FStartDate, FCatCode, FSubCatCode, FTradeCury,FFactor from " +
//                  pub.yssGetTableName("tb_para_security") +
////                  " where FCheckState=1 and FCatCode = 'OP' and FSubCatCode = 'OP02') sec on a.FSecurityCode = sec.FSecurityCode" +
//                  " where FCheckState=1 and FCatCode = 'OP' ) sec on a.FSecurityCode = sec.FSecurityCode" +
//                  //------------------------------------------------------------
//                  " join (select FLinkCode from " +
//                  pub.yssGetTableName("Tb_Para_MTVMethodLink") +
//                  " where FCheckState = 1 and FMtvCode=" +
//                  dbl.sqlString(vMethod.getMTVCode()) +
//                  ") b on a.Fsecuritycode = b.FLinkCode" +
//                  //------------------------------------------------------------
//                  " left join (select * from " +
//                  pub.yssGetTableName("Tb_Data_RightsIssue") +
//                  " where FCheckState = 1 and " + dbl.sqlDate(dDate) +
//                  " between FExRightDate and " + dbl.sqlDateAdd("FExpirationDate", "-1") + ") ri on a.Fsecuritycode = ri.FTSecurityCode" +
//                  //-----------------------------------------------------------
//                  " where a.FCheckState = 1 and a.FStorageDate=" +
//                  dbl.sqlDate(dDate) +
//                  " and " + dbl.sqlRight("a.FYearMonth", "2") +
//                  "<>'00' and a.FPortCode = " + dbl.sqlString(this.portCode) +
//                  ") cs " +
//                  //------------------------------------------------------------
//                  " left join (select * from " +
//                  pub.yssGetTableName("Tb_Stock_SecRecPay") +
//                  " where FCheckState = 1 and " + operSql.sqlStoragEve(dDate) +
//                  " and FPortCode = " + dbl.sqlString(this.portCode) +
//                  " and FTsfTypeCode = " +
//                  dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
//                  ") rec on cs.FCSSecurityCode = rec.FSecurityCode and cs.FAttrClsCode= rec.FAttrClsCode " + //add 添加相关的属性分类代码到这里 BUG：000437
//                  (this.invmgrSecField.length() != 0 ?
//                   " and cs.FAnalysisCode1 = rec.FAnalysisCode1 " : " ") +
//                  (this.brokerSecField.length() != 0 ?
//                   " and cs.FAnalysisCode2 = rec.FAnalysisCode2 " : " ") +
//                  //------------------------------------------------------------这段是取配股的原股行情，当估值日期不在交易日期内时使用
//                  " left join ( select mk2.FCsMarketPrice as FRiMktPrice, mk2.FSecurityCode, mk1.FMktValueDate AS FRiMktDate from " +
//                  " (select max(FMktValueDate) as FMktValueDate, FSecurityCode from " +
//                  pub.yssGetTableName("Tb_Data_MarketValue") +
//                  " where FCheckState = 1" +
//                  " and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
//                  " and FMktValueDate <= " + dbl.sqlDate(dDate) +
//                  " group by FSecurityCode ) mk1 join (select " +
//                  vMethod.getMktPriceCode() +
//                  " as FCsMarketPrice,FSecurityCode, FMktValueDate  from " +
//                  pub.yssGetTableName("Tb_Data_MarketValue") +
//                  " where FCheckState = 1 and FMktSrcCode = " +
//                  dbl.sqlString(vMethod.getMktSrcCode()) + ") mk2 " +
//                  " on mk1.FSecurityCode=mk2.FSecurityCode and mk1.FMktValueDate=mk2.FMktValueDate" +
//                  " ) mkri on cs.FRiSecurityCode = mkri.FSecurityCode " +
//                  //------------------------------------------------------------这段是取配股权证的行情
//                  " left join ( select mk2.FCsMarketPrice, mk2.FSecurityCode, mk1.FMktValueDate from " +
//                  " (select max(FMktValueDate) as FMktValueDate, FSecurityCode from " +
//                  pub.yssGetTableName("Tb_Data_MarketValue") +
//                  " where FCheckState = 1" +
//                  " and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
//                  " and FMktValueDate <= " + dbl.sqlDate(dDate) +
//                  " group by FSecurityCode ) mk1 join (select " +
//                  vMethod.getMktPriceCode() +
//                  " as FCsMarketPrice,FSecurityCode, FMktValueDate  from " +
//                  pub.yssGetTableName("Tb_Data_MarketValue") +
//                  " where FCheckState = 1 and FMktSrcCode = " +
//                  dbl.sqlString(vMethod.getMktSrcCode()) + ") mk2 " +
//                  " on mk1.FSecurityCode=mk2.FSecurityCode and mk1.FMktValueDate=mk2.FMktValueDate" +
//                  " ) mk on cs.FCsSecurityCode = mk.FSecurityCode " +
//                  //------------------------------------------------------------
//                  " left join (select * from " +
//                  pub.yssGetTableName("Tb_Para_Portfolio") +
//                  " where FCheckState = 1" +
//                  ") m on  cs.FCsPortCode = m.FPortCode";
//
//            rs = dbl.openResultSet(strSql);
//            while (rs.next()) {
//               secPay = new SecPecPayBean();
//               mktPrice = new ValMktPriceBean();
//               secPay.setTransDate(dDate);
//
//               secPay.setStrSecurityCode(rs.getString("FCsSecurityCode"));
//               secPay.setStrPortCode(rs.getString("FCsPortCode"));
//               secPay.setInvMgrCode(this.invmgrSecField.length() != 0 ?
//                                    rs.getString(this.invmgrSecField) : " ");
//               secPay.setBrokerCode(this.brokerSecField.length() != 0 ?
//                                    rs.getString(this.brokerSecField) : " ");
//               if (rs.getString("FCsCuryCode") == null) {
//                  throw new YssException("请检查证券品种【" +
//                                         rs.getString("FCsSecurityCode") +
//                                         "】的交易币种设置！");
//               }
//
//               secPay.setStrCuryCode(rs.getString("FCsCuryCode"));
//               secPay.setAttrClsCode(rs.getString("FAttrClsCode")); // sj add 20071204 for new key
//               iFactor = rs.getInt("FCsFactor");
//
//               dBaseRate = 1;
//               if (!rs.getString("FCsCuryCode").equalsIgnoreCase(pub.
//                     getBaseCury())) {
//                  dBaseRate = this.getSettingOper().getCuryRate(dDate,
//                   vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                   vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                        rs.getString("FCsCuryCode"), this.portCode,
//                        YssOperCons.YSS_RATE_BASE);
//               }
//
//               if (rs.getString("FPortCury") == null) {
//                  throw new YssException("请检查投资组合【" +
//                                         rs.getString("FCsPortCode") +
//                                         "】的币种设置！");
//               }
//               dPortRate = this.getSettingOper().getCuryRate(dDate,
//                     vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                     vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                     rs.getString("FPortCury"), this.portCode,
//                     YssOperCons.YSS_RATE_PORT);
//
//               sCatCode = rs.getString("FCsCatCode");
//
//               secPay.setBaseCuryRate(dBaseRate);
//               secPay.setPortCuryRate(dPortRate);
//
//               //--------------------2008.07.15 蒋锦 修改--------------------//
//               //行情数据不能依靠权证的交易期间来进行判断是取正股行情还是取权证行情，
//               //如果估值当天有权证行情就应该取权证的行情，否则要看正股行情日期和权证行情日期谁最接近估值日期，取最接近的行情
//               //优先取配股权证的行情
//               java.util.Date mktDate = null;
//               if (rs.getDate("FMktValueDate") != null &&
//                   rs.getDate("FMktValueDate").compareTo(rs.getDate("FRiMktDate")) >= 0) {
//                  dMarketPrice = rs.getDouble("FCsMarketPrice");
//                  mktDate = rs.getDate("FMktValueDate");
//               }
//               else{
//                  dMarketPrice = YssD.sub(rs.getDouble("FRiMktPrice"),rs.getDouble("FRIPrice"));
//                  mktDate = rs.getDate("FRiMktDate");
//               }
////               if (YssFun.dateDiff(rs.getDate("FBeginTradeDate"), dDate) >= 0 && //包括除权日当日.sj edit 20080714
////                   YssFun.dateDiff(dDate, rs.getDate("FEndTradeDate")) >= 0) { //当估值日期在配股权证的交易日期内，取配股权证的行情来估值
////                  dMarketPrice = rs.getDouble("FCsMarketPrice");
////               }else{
////                  dMarketPrice = YssD.sub(rs.getDouble("FRiMktPrice"),rs.getDouble("FRIPrice"));//原股的市场价格-配股价格 bug Num 0000303，恢复原有编码。sj edit 20080714
////                  //dMarketPrice = rs.getDouble("FRiMktPrice");//在计算市值时只需要在之后减去前日的库存成本，不用再减一次配股价格。sj edit 20080407
////               }
//               //------------------------------------------//
//               //当配股权证的市价比配股价低时，该配股权证的估值增值应为0，不能为负数 胡坤 20080702
//               if (dMarketPrice<0){
//                  dMarketPrice = 0;
//               }
//               secPay.setMktPrice(dMarketPrice);
//               //设置原币核算成本估值增值
//               dTmpMoney = rs.getDouble("FStorageCost"); //原币成本
//               dTmpAmount = rs.getDouble("FStorageAmount");
//               //是否四舍五入
//               if(bIsRound){
//                  //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值  BUG:0000416
//                  dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
//                        YssD.div(dMarketPrice,
//                                 rs.getInt("FCsFactor"))), 2);
//               }
//               else{
//                  dTmpMValue = YssD.mul(dTmpAmount,
//                                        YssD.div(dMarketPrice,
//                                                 rs.getInt("FCsFactor")));
//               }
//               if (dMarketPrice == 0) {
//                  dTmpMValue = dTmpMoney;
//               }
//               secPay.setMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney), //市值-成本
//                                        rs.getDouble("FBal"))); //-前日估值增值余额
//
//               //设置原币估值成本估值增值
//               dTmpMoney = rs.getDouble("FVStorageCost"); //原币成本
//               dTmpAmount = rs.getDouble("FStorageAmount");
//               if(bIsRound){
//                  //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
//                  dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
//                        YssD.div(dMarketPrice,
//                                 rs.getInt("FCsFactor"))), 2);
//               }
//               else{
//                  //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
//                  dTmpMValue = YssD.mul(dTmpAmount,
//                                        YssD.div(dMarketPrice,
//                                                 rs.getInt("FCsFactor")));
//               }
//               if (dMarketPrice == 0) {
//                  dTmpMValue = dTmpMoney;
//               }
//               secPay.setVMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney),
//                                         rs.getDouble("FVBal")));
//
//               //设置原币管理成本的估值增值
//               dTmpMoney = rs.getDouble("FMStorageCost"); //原币成本
//               dTmpAmount = rs.getDouble("FStorageAmount");
//               //是否四舍五入
//               if(bIsRound){
//                  //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
//                  dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
//                        YssD.div(dMarketPrice,
//                                 rs.getInt("FCsFactor"))), 2);
//               }
//               else{
//                  dTmpMValue = YssD.mul(dTmpAmount,
//                                        YssD.div(dMarketPrice,
//                                                 rs.getInt("FCsFactor")));
//               }
//               if (dMarketPrice == 0) {
//                  dTmpMValue = dTmpMoney;
//               }
//               secPay.setMMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney),
//                                         rs.getDouble("FMBal")));
//               //设置基础货币估值增值
//               secPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
//                     secPay.getMoney(),
//                     secPay.getBaseCuryRate()));
//                secPay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
//                     secPay.getVMoney(),
//                     secPay.getBaseCuryRate()));
//                secPay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
//                     secPay.getMMoney(),
//                     secPay.getBaseCuryRate()));
//
//               //设置组合货币估值增值
//               secPay.setPortCuryMoney(this.getSettingOper().calPortMoney(
//                     secPay.getMoney(),
//                     secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
//                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
//                     secPay.getStrCuryCode(), dDate, this.portCode));
//               secPay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
//                     secPay.getVMoney(),
//                     secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
//                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
//                     secPay.getStrCuryCode(), dDate, this.portCode));
//               secPay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
//                     secPay.getMMoney(),
//                     secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
//                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
//                     secPay.getStrCuryCode(), dDate, this.portCode));
//
//               secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
//               secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV +
//                                           sCatCode);
//               secPay.checkStateId = 1;
//
//               sKey = secPay.getStrSecurityCode() + "\f" +
//                     (this.invmgrSecField.length() != 0 ?
//                      (secPay.getInvMgrCode() + "\f") : "") +
//                     (this.brokerSecField.length() != 0 ?
//                      (secPay.getBrokerCode() + "\f") : "") +
//                     secPay.getStrSubTsfTypeCode() + "\f" +
//                     (secPay.getAttrClsCode().length() == 0?" ":secPay.getAttrClsCode());
//               hmResult.put(sKey, secPay);
//
//               //------ MS00265 QDV4建行2009年2月23日01_B ---------
//               mktPrice.setValType("RightsIssueMV");// 设置配股权限的估值类型，与估值界面上的代码相一致。
//               //------------------------------------------------
//
//               //2008.07.15 蒋锦 修改 取行情日期而不是估值日期
//               mktPrice.setValDate(mktDate);
//               mktPrice.setSecurityCode(secPay.getStrSecurityCode());
//               mktPrice.setPortCode(portCode);
////               mktPrice.setPrice(rs.getDouble("FRiMktPrice"));
//               mktPrice.setOtPrice1(rs.getDouble("FRIPrice"));
//               //--------------------2008.07.15 蒋锦 修改--------------------//
//               //行情数据不能依靠权证的交易期间来进行判断是取正股行情还是取权证行情，
//               //如果估值当天有权证行情就应该取权证的行情，否则要看正股行情日期和权证行情日期谁最接近估值日期，取最接近的行情
//               //优先取配股权证的行情
//               mktPrice.setPrice(dMarketPrice);
////               if (YssFun.dateDiff(rs.getDate("FBeginTradeDate"), dDate) >= 0 && //在交易日期内，则直接获取配股凭证的行情。
////                   YssFun.dateDiff(dDate, rs.getDate("FEndTradeDate")) >= 0) {
////                  mktPrice.setPrice(rs.getDouble("FCsMarketPrice"));
////                  //mktPrice.setOtPrice1(0);
////               }
////               else{
////                  mktPrice.setPrice(YssD.sub(rs.getDouble("FRiMktPrice"),rs.getDouble("FRIPrice")));//获取原股行情-配股价。
////                  //mktPrice.setOtPrice1(rs.getDouble("FRIPrice"));
////                  if (mktPrice.getPrice() < 0)
////                  {
////                     mktPrice.setPrice(0);
////                  }
////               }
//               //----------------------------------------------//
//               hmValPrice.put(mktPrice.getSecurityCode(), mktPrice);
//            }
//            dbl.closeResultSetFinal(rs);//close rs 20080716 sj
//         }
//         //-----------------获取估值的所有证券代码,用于保存估值结果的删除条件 胡昆 20071217
//         Iterator iter = hmResult.values().iterator();
//         while (iter.hasNext()) {
//            buf.append(((SecPecPayBean)iter.next()).getStrSecurityCode()).append(",");
//         }
//         if (buf.length()>0){
//            buf.setLength(buf.length()-1);
//         }
//         valSecCodes = buf.toString();
//         //---------------------------------------------------------------------
//
//         return hmResult;
//      }
//      catch (Exception e) {
//         throw new YssException("系统资产估值,在执行配股权证浮动盈亏计算时出现异常!"+"\n",e);//by 曹丞 2009.02.01 配股权证浮动盈亏计算异常信息 MS00004 QDV4.1-2009.2.1_09A
//      }
//      finally {
//         dbl.closeResultSetFinal(rs);
//      }
//   }
//
//   public Object filterSecCondition() {
//      SecPecPayBean secpay = new SecPecPayBean();
//      secpay.setStrSecurityCode(valSecCodes);
//      secpay.setStrTsfTypeCode("09"); //调拨类型：估值增值
//      secpay.setStrSubTsfTypeCode("09OP"); //调拨子类型：配股权证估值增值
//      return secpay;
//   }
//
}
