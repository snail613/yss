package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.EachGetPubPara;
import com.yss.main.operdata.futures.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.linkInfo.*;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

/**
 * <P>xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 * 
 * <p>Title: 拆分交易数据 </p>
 *
 * <p>Description: 对期权业务的处理-拆分交易数据：主要是根据昨日库存数量和今日交易数据，进行比对是否进行拆分</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech</p>
 *
 * Cause : QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持
 *
 * @author xuqiji 20090626
 * @version 1.0
 */
public class OptionsDivideTradeDataManage
    extends OptionsControlManage {
    private OptionsTradeRealBean trade = null; //期权交易数据表的POJO类
    private double dMarkPrice = 0; //保存期权当日市价
    private double dAimPrice = 0; //保存期权标的价格
    private double dIndexPrice = 0; //保存期权当日指数价格
    private double dMultiple = 0; //保存期权放大倍数
    private HashMap mapPrice = new HashMap();//保存关于期权的所有价格
    public OptionsDivideTradeDataManage() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate; //调拨日期
        this.sPortCode = portCode; //组合
    }

    /**
     * 做业务处理：拆分交易数据：主要是根据昨日库存数量和今日交易数据，进行比对是否进行拆分
     * @throws YssException
     */
    public void doOpertion() throws YssException {
        divideTradeData(null); //拆分交易数据
     }

    /**
     * 做业务处理：拆分交易数据：主要是根据昨日库存数量和今日交易数据，进行比对是否进行拆分
     * @throws YssException
     */
    public void doOpertion(OptionsTradeRealBean optionsTrade) throws YssException {
        divideTradeData(optionsTrade); //拆分交易数据
    }

    /**
     * divideTradeData 1.拆分交易数据,主要是根据昨日库存数量和今日交易数据，进行比对是否进行拆分,2.计算保证金
     * 拆分后的数据插入到期权交易子表中（Tb_001_data_OptTradeReal）
     */
    private void divideTradeData(OptionsTradeRealBean optionsTrade) throws YssException {
        ArrayList alTrade = null;
        double dBZJ = 0; //保证金
        Connection conn = null;
        boolean bTrans = true;
        boolean flag = false;
        try {
            OptionsTradeRealBeanAdmin realBeanAdmin = new OptionsTradeRealBeanAdmin(); //期权交易数据表具体数据库操作实体类
            realBeanAdmin.setYssPub(pub);
            if (null != optionsTrade) {
                alTrade = getLastStock(optionsTrade.getSecurityCode());
            } else {
                conn = dbl.loadConnection();
                conn.setAutoCommit(false);
                alTrade = getLastStock(""); //拆分交易数据方法
                realBeanAdmin.deleteData(this.dDate,this.sPortCode); //删除期权交易关联表中的数据
                getFee(alTrade); //计算费用和清算款
                realBeanAdmin.saveMutliSetting(alTrade); //保存数据到期权交易关联表中的数据
                
                if(alTrade.size()>0){
                	flag = true;
        		}

                //-----------------以下处理行权日当天没有录入放弃行权数据时，要产生一条放弃行权的数据---------------------//
                this.doDropRightSQL(alTrade);
                getFee(alTrade); //计算费用和清算款
                realBeanAdmin.deleteData(this.dDate,this.sPortCode,YssOperCons.YSS_JYLX_DropExcerise,"07", "1");
                realBeanAdmin.saveMutliSetting(alTrade);
                //-------------------------------------------end---------------------------------------------//
                if(alTrade.size()>0){
                	flag = true;
        		}
                //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
        		//当日产生数据，则认为有业务。
        		if((alTrade==null || alTrade.size()==0) &&!flag){
        			this.sMsg="        当日无业务";
        		}
                
                conn.commit();
                conn.setAutoCommit(true);
                bTrans = false;
                return;
            }
//以下注释掉内容为 日后操作，请不要删除！！！
//            if (alTrade.size() == 0) {
//                throw new YssException("期权【" + optionsTrade.getSecurityCode() + "】库存不足，请检查期货库存！");
//            }
//            double dbCloseAmount = optionsTrade.getTradeAmount();
//            for (int i = alTrade.size() - 1; i >= 0; i--) {
//                if (dbCloseAmount == 0) {
//                    break;
//                }
//                trade = (OptionsTradeRealBean) alTrade.get(i);
//                if (trade.getTradeAmount() <= dbCloseAmount) { //卖出数量大于库存数量
//                    if (optionsTrade.getBailType().equals("比例")) {
//                        dBZJ += YssD.mul(YssD.mul(YssD.mul(YssD.sub(dbCloseAmount, trade.getTradeAmount()),
//                            optionsTrade.getTradePrice()), YssFun.toDouble(optionsTrade.getMultipy())),
//                                         YssFun.toDouble(optionsTrade.getBailScale()));
//                    } else {
//                        dBZJ += YssD.mul(YssD.sub(dbCloseAmount, trade.getTradeAmount()), YssFun.toDouble(optionsTrade.getBailFix()));
//                    }
//                    dbCloseAmount -= trade.getTradeAmount();
//                } else {
//                    dBZJ += YssD.round(YssD.mul(YssD.div(trade.getBegBailMoney(), trade.getTradeAmount()), dbCloseAmount), 2);
//                    dbCloseAmount = 0;
//                }
//            }
//            if (dbCloseAmount > 0) {
//                throw new YssException("期权【" + optionsTrade.getSecurityCode() + "】库存不足，无法计算保证金，请确认！");
//            }
        } catch (Exception e) {
            throw new YssException("拆分交易数据出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * getLastStock 拆分交易数据
     *
     * @param dWorkDate Date 计算日期
     * @param sPortCode String 组合代码
     * @param sSecurityCode String 期权代码
     * @return ArrayList
     */
    private ArrayList getLastStock(String sSecurityCode) throws YssException {
        String sql = "";
        ResultSet rs = null;
        ArrayList alResult = new ArrayList();
        HashMap map = new HashMap(); //存放证券库存
        SecurityStorageBean storageBean = null; //证券库存
        //storageBean.setYssPub(pub);
        double theDayAmount = 0;
        boolean analy1 = false;//分析代码1
        boolean analy2 = false;//分析代码2
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Stock");//判断证券库存表是否有分析代码
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Stock");
            //获取昨日库存
            sql = getYesStorage() + ("".equals(sSecurityCode.trim()) ? "" : " and AND FSecurityCode =" + dbl.sqlString(sSecurityCode));
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                storageBean = new SecurityStorageBean();
                storageBean.setStrSecurityCode(rs.getString("FSecurityCode"));//证券代码
                storageBean.setStrStorageDate(rs.getDate("FStorageDate").toString());//库存日期
                storageBean.setStrPortCode(rs.getString("FPortCode"));//组合代码
                storageBean.setCatType(rs.getString("FCatType"));//品种类型
                storageBean.setAttrCode(rs.getString("FAttrClsCode"));//所属分类
                storageBean.setStrCuryCode(rs.getString("FCuryCode")); ;//币种
                storageBean.setStrStorageAmount(rs.getString("FStorageAmount"));//库存日期
                storageBean.setStrStorageCost(rs.getString("FStorageCost"));//原币成本
                storageBean.setStrBaseCuryCost(rs.getString("FBaseCuryCost"));//基础货币成本
                storageBean.setStrPortCuryCost(rs.getString("FPortCuryCost"));//组合货币成本
                storageBean.setBailMoney(rs.getDouble("FBailMoney"));//保证金
                storageBean.setStrVStorageCost(rs.getString("FVStorageCost"));
                storageBean.setStrFAnalysisCode1(rs.getString("FAnalysisCode1"));//分析代码1
                storageBean.setStrFAnalysisCode2(rs.getString("FAnalysisCode2"));//分析代码2
                map.put(storageBean.getStrSecurityCode() + "\f" + storageBean.getStrPortCode() + "\f"
                        + (analy1?storageBean.getStrFAnalysisCode1():"")
                        + "\f" + (analy2?storageBean.getStrFAnalysisCode2():""), storageBean);
            }
            dbl.closeResultSetFinal(rs);
            //获取期权基本信息：放大倍数，保证金类型，保证金比例，每首固定保证金，以及今天期权行情，指数价格
            sql = getTheDateTradeAmount() + ("".equals(sSecurityCode.trim()) ? "" : " and AND FSecurityCode =" + dbl.sqlString(sSecurityCode)) +
                  //" ORDER by FSecurityCode, FPortCode, FNum "; //modify by fangjiang story 1342 2011.09.07
            //BUG2988业务处理勾选期权业务  add by jiangshichao 2011.10.26
             " ORDER by trade.FSecurityCode, trade.FPortCode, trade.FNum "; 
            
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                dMarkPrice = rs.getDouble("FClosingPrice"); //期权当日市价
                dAimPrice = rs.getDouble("FExerciseprice"); //期权标的物价格
                dIndexPrice = rs.getDouble("FClosedValue"); //期权当日指数价格
                dMultiple = rs.getDouble("FMultiple"); //期权放大倍数
                String str = rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" +
                            (analy1?rs.getString("FInvMgrCode"):"") + "\f" + (analy2?rs.getString("FBrokerCode"):"");
                //如果设置开平状态并且交易类型为买入或者卖出时，不要进行交易拆分，直接保存到期权交易关联子表中
                if(!rs.getString("FOffsetflag").equalsIgnoreCase("no")&&
                		(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)||rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale))){//开平状态： ‘set’-开仓；’off’-平仓；’no’-不指定
                	trade = new OptionsTradeRealBean();
                    trade.setNum(rs.getString("FNum")); //编号
                    if(rs.getString("FOffsetflag").equalsIgnoreCase("set")&&rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)){
                    	trade.setCloseNum("01"); //买入状态 -开仓
                    }else if(rs.getString("FOffsetflag").equalsIgnoreCase("off")&&rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)){
                    	trade.setCloseNum("02"); //买入状态 -平仓
                    }else if(rs.getString("FOffsetflag").equalsIgnoreCase("set")&&rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)){
                    	trade.setCloseNum("03"); //卖出状态 -开仓
                    }else if(rs.getString("FOffsetflag").equalsIgnoreCase("off")&&rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)){
                    	trade.setCloseNum("04"); //卖出状态 -平仓
                    }
                    if("01".equals(trade.getCloseNum()) || "02".equals(trade.getCloseNum())){
                    	trade.setTradeAmount(rs.getDouble("FTradeAmount")); //交易数量
                    }else{
                    	trade.setTradeAmount(-rs.getDouble("FTradeAmount")); //交易数量
                    } 
                    trade.setBegBailMoney(rs.getDouble("FBegBailMoney"));
                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易类型
                    trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                    insertOptTradeReal(trade, rs); //为变量赋值方法
                    alResult.add(trade);
                    //add by fangjiang story 1342 2011.09.07 
                    double newStorage = 0.0;
                    if (map.containsKey(str)) { 
                    	storageBean =(SecurityStorageBean) map.get(str); 
                    	if("01".equalsIgnoreCase(trade.getCloseNum()) || "03".equalsIgnoreCase(trade.getCloseNum())){
                    		newStorage = YssD.add(
		                            		 Double.parseDouble(storageBean.getStrStorageAmount()), 
		                            		 trade.getTradeAmount()
										 );
                    		storageBean.setStrStorageAmount(Double.toString(newStorage));
                    	}else{
                    		newStorage = YssD.sub(
			                           		 Double.parseDouble(storageBean.getStrStorageAmount()), 
			                           		 trade.getTradeAmount()
								 		 );
                    		storageBean.setStrStorageAmount(Double.toString(newStorage));
                    	}
                    }else{
                    	storageBean = new SecurityStorageBean();
                        storageBean.setYssPub(pub);
                        storageBean.setStrSecurityCode(trade.getSecurityCode());
                        storageBean.setStrPortCode(trade.getPortCode());
                        storageBean.setStrStorageAmount(Double.toString(trade.getTradeAmount()));
                        storageBean.setBailMoney(trade.getBegBailMoney());
                        storageBean.setStrFAnalysisCode1(trade.getInvMgrCode());
                        storageBean.setStrFAnalysisCode2(trade.getBrokerCode());
                    	map.put(storageBean.getStrSecurityCode() + "\f" + storageBean.getStrPortCode() + "\f" + storageBean.getStrFAnalysisCode1()
                                + "\f" + storageBean.getStrFAnalysisCode2(), storageBean);
                    }
                    //-------------
                    trade = null;
                    continue;
                }
                if (rs.getString("FOPTradeType").equalsIgnoreCase("call")) { //期权认购
                    if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) { //买入期权
                        double dbBuyAmount = rs.getDouble("FTradeAmount"); //交易数量
                        if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            if (yesStorageAmount >= 0) { //昨日库存为正或0，此时为 买入状态
                                trade = new OptionsTradeRealBean();
                                trade.setNum(rs.getString("FNum")); //编号
                                trade.setCloseNum("01"); //买入状态 -开仓
                                trade.setTradeAmount(rs.getDouble("FTradeAmount")); //交易数量
                                //trade.setBegBailMoney(YssD.add(rs.getDouble("FBegBailMoney"), storageBean.getBailMoney()));
                                trade.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易类型
                                trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                
                                insertOptTradeReal(trade, rs); //为变量赋值方法
                                alResult.add(trade);
                                trade = null;
                            } else { //昨日库存为负，此时为 卖出状态 -买入
                                theDayAmount = YssD.add(yesStorageAmount, rs.getDouble("FTradeAmount")); //今天买入与昨日库存之和
                                double tradePrice = rs.getDouble("FTradePrice");
                                double dbMoney = rs.getDouble("FBegBailMoney"); //初始保证金金额
                                double tradeMoney = rs.getDouble("FTradeMoney"); //成交金额
                                if (theDayAmount > 0) { //拆分数据
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("04"); //卖出状态-平仓
                                    trade.setTradeAmount(yesStorageAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), tradePrice,
                                            rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));
                                    }*/
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeMoney(YssD.mul(tradePrice, -yesStorageAmount,rs.getDouble("FMultiple"))); //成交金额
                                    insertOptTradeReal(trade, rs);
                                    alResult.add(trade);
                                    trade = null;

                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("01"); //买入状态-开仓
                                    trade.setTradeAmount(theDayAmount); //交易数量
                                    //trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金额
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易方式
                                    trade.setTradeMoney(YssD.mul(tradePrice, theDayAmount,rs.getDouble("FMultiple"))); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;

                                } else if (theDayAmount == 0) {
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("04"); //卖出状态-平仓
                                    trade.setTradeAmount(yesStorageAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), tradePrice,
                                            rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易方式
                                    trade.setTradeMoney(YssD.mul(tradePrice, -trade.getTradeAmount(),rs.getDouble("FMultiple"))); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;
                                } else {
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum")); //编号
                                    trade.setCloseNum("04"); //卖出状态-平仓
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeAmount(-rs.getDouble("FTradeAmount"));
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(YssD.sub(0, trade.getTradeAmount()),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));
                                    }*/
                                    trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;
                                }
                            }
                            double newStorage = YssD.add(yesStorageAmount, dbBuyAmount);
                            storageBean.setStrStorageAmount(Double.toString(newStorage));
                        } else { //昨日无库存，今天先买入期权，此时状态为：买入状态-开仓
                            trade = new OptionsTradeRealBean();
                            trade.setNum(rs.getString("FNum")); //交易编号
                            trade.setCloseNum("01"); //买入状态-开仓
                            trade.setTradeAmount(rs.getDouble("FTradeAmount")); 
                            trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                            trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                            trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                            insertOptTradeReal(trade, rs); //为变量赋值方法
                            alResult.add(0, trade);

                            //为证券库存赋值
                            storageBean = new SecurityStorageBean();
                            storageBean.setYssPub(pub);
                            storageBean.setStrSecurityCode(trade.getSecurityCode());
                            storageBean.setStrPortCode(trade.getPortCode());
                            storageBean.setStrStorageAmount(Double.toString(trade.getTradeAmount()));
                            storageBean.setBailMoney(trade.getBegBailMoney());
                            storageBean.setStrFAnalysisCode1(trade.getInvMgrCode());
                            storageBean.setStrFAnalysisCode2(trade.getBrokerCode());
                            map.put(storageBean.getStrSecurityCode() + "\f" + storageBean.getStrPortCode() + "\f" + storageBean.getStrFAnalysisCode1()
                                    + "\f" + storageBean.getStrFAnalysisCode2(), storageBean);
                            trade = null;
                        }
                    } else if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) { //卖出期权
                        double dbCloseAmount = rs.getDouble("FTradeAmount"); //交易数量
                        double dbMoney = rs.getDouble("FBegBailMoney"); //初始保证金金额
                        double tradeMoney = rs.getDouble("FTradeMoney");
                        double tradePrice = rs.getDouble("FTradePrice");
                        if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            if (yesStorageAmount >= 0) { //库存为正或0，
                                if (yesStorageAmount < dbCloseAmount) { //如果卖出的交易数量>库存数量
                                    double amount = YssD.div(yesStorageAmount, dbCloseAmount); 
                                    //dbMoney -= storageBean.getBailMoney(); //初始保证金 = 卖出初始保证金 - 原有的初始保证金

                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("02"); //买入状态-平仓
                                    trade.setTradeAmount(yesStorageAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeMoney(YssD.mul(yesStorageAmount,tradePrice,rs.getDouble("FMultiple"))); //成交金额=成交数量*成交价格
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;

                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("03"); //卖出状态-开仓，即此时库存数量为负数
                                    trade.setTradeAmount(amount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(YssD.sub(0, trade.getTradeAmount()),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeMoney(YssD.mul(-amount, tradePrice, rs.getDouble("FMultiple"))); //成交金额
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;

                                } else if (yesStorageAmount > dbCloseAmount) { //库存数量>卖出数量
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("02"); //买入状态-平仓
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeAmount(dbCloseAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeMoney(YssD.mul(trade.getTradeAmount(), tradePrice, rs.getDouble("FMultiple"))); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;
                                } else {
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("02"); //买入状态-平仓
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeAmount(dbCloseAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;
                                }
                                double newStorage = YssD.sub(yesStorageAmount, dbCloseAmount);
                                storageBean.setStrStorageAmount(Double.toString(newStorage)); //重新计算库存
                            } else { //库存为负，此时为卖出状态-卖出04
                                trade = new OptionsTradeRealBean();
                                trade.setNum(rs.getString("FNum"));
                                trade.setCloseNum("03"); //卖出状态-开仓,库存为负值
                                trade.setTradeAmount(-rs.getDouble("FTradeAmount"));
                                //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                /*if (rs.getString("FBailType").equals("Scale")) {
                                    trade.setBegBailMoney(YssD.mul(YssD.sub(0, trade.getTradeAmount()),
                                        tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                    trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                }*/
                                trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                insertOptTradeReal(trade, rs); //为变量赋值方法
                                alResult.add(trade);
                                trade = null;
                                double newStorage = YssD.add(yesStorageAmount, -dbCloseAmount);
                                storageBean.setStrStorageAmount(Double.toString(newStorage)); //重新计算库存
                            }
                        } else { //昨日无库存，今天先卖出期权，此时状态为，卖出状态，库存为负数
                            trade = new OptionsTradeRealBean();
                            trade.setNum(rs.getString("FNum")); //交易编号
                            trade.setCloseNum("03"); //卖出状态-开仓
                            trade.setTradeAmount(-dbCloseAmount); //库存
                            //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                            /*if (rs.getString("FBailType").equals("Scale")) {
                                trade.setBegBailMoney(YssD.mul(YssD.sub(0, trade.getTradeAmount()),
                                    tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                            } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                            }*/
                            trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                            trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                            insertOptTradeReal(trade, rs); //为变量赋值方法
                            alResult.add(trade);

                            //把数据保存到证券库存中
                            storageBean = new SecurityStorageBean();
                            storageBean.setStrSecurityCode(trade.getSecurityCode());
                            storageBean.setStrPortCode(trade.getPortCode());
                            storageBean.setStrStorageAmount(Double.toString(YssD.sub(0, trade.getTradeAmount())));
                            storageBean.setBailMoney(trade.getBegBailMoney());
                            storageBean.setStrFAnalysisCode1(trade.getInvMgrCode());
                            storageBean.setStrFAnalysisCode2(trade.getBrokerCode());
                            map.put(storageBean.getStrSecurityCode() + "\f" + storageBean.getStrPortCode() + "\f" + storageBean.getStrFAnalysisCode1()
                                    + "\f" + storageBean.getStrFAnalysisCode2(), storageBean);
                            trade = null;
                        }
                    } else if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Excerise)) { //期权行权
                    	/*if(YssFun.dateDiff(rs.getDate("fexpirydate"), this.dDate) != 0){
                    		break;
                    	}*/
                        if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            double exerciseAmount = rs.getDouble("FTradeAmount");
                            double value = 0.0;
                            //昨日库存大于0，
                            if (rs.getDouble("FTradeAmount") > Math.abs(yesStorageAmount)) {
                                throw new YssException("行权数量大于库存,库存不足！");
                            } else{
                                trade = new OptionsTradeRealBean();
                                trade.setNum(rs.getString("FNum")); //交易编号
                                trade.setCloseNum("05"); //行权，卖出
                                if(yesStorageAmount > 0){
                                	trade.setTradeAmount(rs.getDouble("FTradeAmount")); 
                                }else{
                                	trade.setTradeAmount(-rs.getDouble("FTradeAmount")); 
                                }
                                trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                                /*if("USA".equalsIgnoreCase(rs.getString("FExecuteTypeCode"))){ //美式行权
                    				value = YssD.sub(rs.getDouble("FClosedValue"),rs.getDouble("fexerciseprice"));                            		
                            	}else if("EUR".equalsIgnoreCase(rs.getString("FExecuteTypeCode"))){ //欧式行权
                        			value = rs.getDouble("FClosingPrice");                  		
                            	}
                            	//成交金额
                            	trade.setTradeMoney(
                            					   	   YssD.mul
        			        		            	   (
        			        		            	       value, 
        			        		            	       rs.getDouble("fmultiple"), 
        			        		            	       rs.getDouble("FTradeAmount")
        			            		               )
        		            		               );*/
                                trade.setTradeMoney(rs.getDouble("FTradeMoney"));
                                insertOptTradeReal(trade, rs); //为变量赋值方法
                                alResult.add(trade);                                
                            } 
                            double newStorage = YssD.sub(yesStorageAmount, trade.getTradeAmount());
                            storageBean.setStrStorageAmount(Double.toString(newStorage));
                            trade = null;
                        } else {
                            throw new YssException("对不起，库存不足，无法行权！");
                        }
                    } else if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_DropExcerise)) { //期权放弃行权行权
                    	/*if(YssFun.dateDiff(rs.getDate("fexpirydate"), this.dDate) != 0){
                    		break;
                    	}*/
                    	if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            double exerciseAmount = rs.getDouble("FTradeAmount");
                            if (rs.getDouble("FTradeAmount") > Math.abs(yesStorageAmount)) {
                                throw new YssException("期权放弃行权数量大于库存,库存不足！");
                            } else {                                
                                trade = new OptionsTradeRealBean();
                                trade.setNum(rs.getString("FNum")); //交易编号
                                trade.setCloseNum("07"); //行权，卖出
                                if(yesStorageAmount > 0){
                                	trade.setTradeAmount(rs.getDouble("FTradeAmount")); 
                                }else{
                                	trade.setTradeAmount(-rs.getDouble("FTradeAmount")); 
                                }
                                trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                                trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                trade.setDropRightDataSource("0");  //add by fangjiang 2011.09.08 story 1342
                                insertOptTradeReal(trade, rs); //为变量赋值方法
                                alResult.add(trade);  
                            } 
                            double newStorage = YssD.sub(yesStorageAmount, trade.getTradeAmount());
                            storageBean.setStrStorageAmount(Double.toString(newStorage));
                            trade = null;
                        } else {
                            throw new YssException("对不起，库存不足，无法放弃行权！");
                        }
                    } else { //期权认购结算
                        double dbBuyAmount = rs.getDouble("FTradeAmount"); //交易数量
                        if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            //昨日库存为负数，为卖出认购状态
                            trade = new OptionsTradeRealBean();
                            trade.setNum(rs.getString("FNum")); //交易编号
                            trade.setCloseNum("06"); //结算，卖出
                            trade.setTradeAmount(yesStorageAmount < 0 ?YssD.sub(0, yesStorageAmount):yesStorageAmount); //库存
                            trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                            trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                            trade.setTradeMoney(Double.parseDouble(storageBean.getStrStorageCost())); //成交金额
                            insertOptTradeReal(trade, rs); //为变量赋值方法
                            alResult.add(trade);
                            trade = null;
                        }
                    }
                } else { //期权认沽
                	if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) { //买入期权
                        double dbBuyAmount = rs.getDouble("FTradeAmount"); //交易数量
                        if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            if (yesStorageAmount >= 0) { //昨日库存为正或0，此时为 买入状态
                                trade = new OptionsTradeRealBean();
                                trade.setNum(rs.getString("FNum")); //编号
                                trade.setCloseNum("01"); //买入状态 -开仓
                                trade.setTradeAmount(rs.getDouble("FTradeAmount")); //交易数量
                                //trade.setBegBailMoney(YssD.add(rs.getDouble("FBegBailMoney"), storageBean.getBailMoney()));
                                trade.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易类型
                                trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                
                                insertOptTradeReal(trade, rs); //为变量赋值方法
                                alResult.add(trade);
                                trade = null;
                            } else { //昨日库存为负，此时为 卖出状态 -买入
                                theDayAmount = YssD.add(yesStorageAmount, rs.getDouble("FTradeAmount")); //今天买入与昨日库存之和
                                double tradePrice = rs.getDouble("FTradePrice");
                                double dbMoney = rs.getDouble("FBegBailMoney"); //初始保证金金额
                                double tradeMoney = rs.getDouble("FTradeMoney"); //成交金额
                                if (theDayAmount > 0) { //拆分数据
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("04"); //卖出状态-平仓
                                    trade.setTradeAmount(yesStorageAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), tradePrice,
                                            rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));
                                    }*/
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeMoney(YssD.mul(tradePrice, -yesStorageAmount,rs.getDouble("FMultiple"))); //成交金额
                                    insertOptTradeReal(trade, rs);
                                    alResult.add(trade);
                                    trade = null;

                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("01"); //买入状态-开仓
                                    trade.setTradeAmount(theDayAmount); //交易数量
                                    //trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金额
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易方式
                                    trade.setTradeMoney(YssD.mul(tradePrice, theDayAmount,rs.getDouble("FMultiple"))); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;

                                } else if (theDayAmount == 0) {
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("04"); //卖出状态-平仓
                                    trade.setTradeAmount(yesStorageAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), tradePrice,
                                            rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode")); //交易方式
                                    trade.setTradeMoney(YssD.mul(tradePrice, -trade.getTradeAmount(),rs.getDouble("FMultiple"))); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;
                                } else {
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum")); //编号
                                    trade.setCloseNum("04"); //卖出状态-平仓
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeAmount(-rs.getDouble("FTradeAmount"));
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(YssD.sub(0, trade.getTradeAmount()),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));
                                    }*/
                                    trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;
                                }
                            }
                            double newStorage = YssD.add(yesStorageAmount, dbBuyAmount);
                            storageBean.setStrStorageAmount(Double.toString(newStorage));
                        } else { //昨日无库存，今天先买入期权，此时状态为：买入状态-开仓
                            trade = new OptionsTradeRealBean();
                            trade.setNum(rs.getString("FNum")); //交易编号
                            trade.setCloseNum("01"); //买入状态-开仓
                            trade.setTradeAmount(rs.getDouble("FTradeAmount")); 
                            trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                            trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                            trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                            insertOptTradeReal(trade, rs); //为变量赋值方法
                            alResult.add(0, trade);

                            //为证券库存赋值
                            storageBean = new SecurityStorageBean();
                            storageBean.setYssPub(pub);
                            storageBean.setStrSecurityCode(trade.getSecurityCode());
                            storageBean.setStrPortCode(trade.getPortCode());
                            storageBean.setStrStorageAmount(Double.toString(trade.getTradeAmount()));
                            storageBean.setBailMoney(trade.getBegBailMoney());
                            storageBean.setStrFAnalysisCode1(trade.getInvMgrCode());
                            storageBean.setStrFAnalysisCode2(trade.getBrokerCode());
                            map.put(storageBean.getStrSecurityCode() + "\f" + storageBean.getStrPortCode() + "\f" + storageBean.getStrFAnalysisCode1()
                                    + "\f" + storageBean.getStrFAnalysisCode2(), storageBean);
                            trade = null;
                        }
                    } else if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) { //卖出期权
                        double dbCloseAmount = rs.getDouble("FTradeAmount"); //交易数量
                        double dbMoney = rs.getDouble("FBegBailMoney"); //初始保证金金额
                        double tradeMoney = rs.getDouble("FTradeMoney");
                        double tradePrice = rs.getDouble("FTradePrice");
                        if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            if (yesStorageAmount >= 0) { //库存为正或0，
                                if (yesStorageAmount < dbCloseAmount) { //如果卖出的交易数量>库存数量
                                    double amount = YssD.div(yesStorageAmount, dbCloseAmount); 
                                    //dbMoney -= storageBean.getBailMoney(); //初始保证金 = 卖出初始保证金 - 原有的初始保证金

                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("02"); //买入状态-平仓
                                    trade.setTradeAmount(yesStorageAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeMoney(YssD.mul(yesStorageAmount,tradePrice,rs.getDouble("FMultiple"))); //成交金额=成交数量*成交价格
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;

                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("03"); //卖出状态-开仓，即此时库存数量为负数
                                    trade.setTradeAmount(amount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(YssD.sub(0, trade.getTradeAmount()),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeMoney(YssD.mul(-amount, tradePrice, rs.getDouble("FMultiple"))); //成交金额
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;

                                } else if (yesStorageAmount > dbCloseAmount) { //库存数量>卖出数量
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("02"); //买入状态-平仓
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeAmount(dbCloseAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeMoney(YssD.mul(trade.getTradeAmount(), tradePrice, rs.getDouble("FMultiple"))); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;
                                } else {
                                    trade = new OptionsTradeRealBean();
                                    trade.setNum(rs.getString("FNum"));
                                    trade.setCloseNum("02"); //买入状态-平仓
                                    trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                    trade.setTradeAmount(dbCloseAmount);
                                    //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                    /*if (rs.getString("FBailType").equals("Scale")) {
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(),
                                            tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                    } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                        trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                    }*/
                                    trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                    insertOptTradeReal(trade, rs); //为变量赋值方法
                                    alResult.add(trade);
                                    trade = null;
                                }
                                double newStorage = YssD.sub(yesStorageAmount, dbCloseAmount);
                                storageBean.setStrStorageAmount(Double.toString(newStorage)); //重新计算库存
                            } else { //库存为负，此时为卖出状态-卖出04
                                trade = new OptionsTradeRealBean();
                                trade.setNum(rs.getString("FNum"));
                                trade.setCloseNum("03"); //卖出状态-开仓,库存为负值
                                trade.setTradeAmount(-rs.getDouble("FTradeAmount"));
                                //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                                /*if (rs.getString("FBailType").equals("Scale")) {
                                    trade.setBegBailMoney(YssD.mul(YssD.sub(0, trade.getTradeAmount()),
                                        tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                                } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                    trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                                }*/
                                trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                insertOptTradeReal(trade, rs); //为变量赋值方法
                                alResult.add(trade);
                                trade = null;
                                double newStorage = YssD.add(yesStorageAmount, -dbCloseAmount);
                                storageBean.setStrStorageAmount(Double.toString(newStorage)); //重新计算库存
                            }
                        } else { //昨日无库存，今天先卖出期权，此时状态为，卖出状态，库存为负数
                            trade = new OptionsTradeRealBean();
                            trade.setNum(rs.getString("FNum")); //交易编号
                            trade.setCloseNum("03"); //卖出状态-开仓
                            trade.setTradeAmount(-dbCloseAmount); //库存
                            //保证金=（卖出数量-库存数量） * 成交价格 * 放大倍数 * 保证金比例
                            /*if (rs.getString("FBailType").equals("Scale")) {
                                trade.setBegBailMoney(YssD.mul(YssD.sub(0, trade.getTradeAmount()),
                                    tradePrice, rs.getDouble("FMultiple"), rs.getDouble("FBailScale")));
                            } else { //保证金=（卖出数量-库存数量）*每首固定保证金金额
                                trade.setBegBailMoney(YssD.mul(trade.getTradeAmount(), rs.getDouble("FBailFix")));

                            }*/
                            trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                            trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                            insertOptTradeReal(trade, rs); //为变量赋值方法
                            alResult.add(trade);

                            //把数据保存到证券库存中
                            storageBean = new SecurityStorageBean();
                            storageBean.setStrSecurityCode(trade.getSecurityCode());
                            storageBean.setStrPortCode(trade.getPortCode());
                            storageBean.setStrStorageAmount(Double.toString(YssD.sub(0, trade.getTradeAmount())));
                            storageBean.setBailMoney(trade.getBegBailMoney());
                            storageBean.setStrFAnalysisCode1(trade.getInvMgrCode());
                            storageBean.setStrFAnalysisCode2(trade.getBrokerCode());
                            map.put(storageBean.getStrSecurityCode() + "\f" + storageBean.getStrPortCode() + "\f" + storageBean.getStrFAnalysisCode1()
                                    + "\f" + storageBean.getStrFAnalysisCode2(), storageBean);
                            trade = null;
                        }
                    } else if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Excerise)) { //期权行权
                    	/*if(YssFun.dateDiff(rs.getDate("fexpirydate"), this.dDate) != 0){
                    		break;
                    	}*/
                        if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            double exerciseAmount = rs.getDouble("FTradeAmount");
                            double value = 0.0;
                            //昨日库存大于0，
                            if (rs.getDouble("FTradeAmount") > Math.abs(yesStorageAmount)) {
                                throw new YssException("行权数量大于库存,库存不足！");
                            } else{
                                trade = new OptionsTradeRealBean();
                                trade.setNum(rs.getString("FNum")); //交易编号
                                trade.setCloseNum("05"); //行权，卖出
                                if(yesStorageAmount > 0){
                                	trade.setTradeAmount(rs.getDouble("FTradeAmount")); 
                                }else{
                                	trade.setTradeAmount(-rs.getDouble("FTradeAmount")); 
                                }
                                trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                                /*if("USA".equalsIgnoreCase(rs.getString("FExecuteTypeCode"))){ //美式行权
                    				value = YssD.sub(rs.getDouble("fexerciseprice"), rs.getDouble("FClosedValue"));                            		
                            	}else if("EUR".equalsIgnoreCase(rs.getString("FExecuteTypeCode"))){ //欧式行权
                        			value = rs.getDouble("FClosingPrice");                  		
                            	}
                            	//成交金额
                            	trade.setTradeMoney(
                            					   	   YssD.mul
        			        		            	   (
        			        		            	       value, 
        			        		            	       rs.getDouble("fmultiple"), 
        			        		            	       rs.getDouble("FTradeAmount")
        			            		               )
        		            		               );*/
                                trade.setTradeMoney(rs.getDouble("FTradeMoney"));
                                insertOptTradeReal(trade, rs); //为变量赋值方法
                                alResult.add(trade);     
                            } 
                            double newStorage = YssD.sub(yesStorageAmount, trade.getTradeAmount());
                            storageBean.setStrStorageAmount(Double.toString(newStorage));
                            trade = null;
                        
                        } else {
                            throw new YssException("对不起，库存不足，无法行权！");
                        }
                    } else if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_DropExcerise)) { //期权放弃行权行权
                    	/*if(YssFun.dateDiff(rs.getDate("fexpirydate"), this.dDate) != 0){
                    		break;
                    	}*/
                    	if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            double exerciseAmount = rs.getDouble("FTradeAmount");
                            if (rs.getDouble("FTradeAmount") > Math.abs(yesStorageAmount)) {
                                throw new YssException("期权放弃行权数量大于库存,库存不足！");
                            } else {                                
                                trade = new OptionsTradeRealBean();
                                trade.setNum(rs.getString("FNum")); //交易编号
                                trade.setCloseNum("07"); //行权，卖出
                                if(yesStorageAmount > 0){
                                	trade.setTradeAmount(rs.getDouble("FTradeAmount")); 
                                }else{
                                	trade.setTradeAmount(-rs.getDouble("FTradeAmount")); 
                                }
                                trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                                trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                                trade.setTradeMoney(rs.getDouble("FTradeMoney")); //成交金额
                                trade.setDropRightDataSource("0");  //add by fangjiang 2011.09.08 story 1342
                                insertOptTradeReal(trade, rs); //为变量赋值方法
                                alResult.add(trade); 
                            } 
                            double newStorage = YssD.sub(yesStorageAmount, trade.getTradeAmount());
                            storageBean.setStrStorageAmount(Double.toString(newStorage));
                            trade = null;
                        } else {
                            throw new YssException("对不起，库存不足，无法放弃行权！");
                        }
                    } else { //期权认购结算
                        double dbBuyAmount = rs.getDouble("FTradeAmount"); //交易数量
                        if (map.containsKey(str)) {
                            storageBean = (SecurityStorageBean) map.get(str);
                            double yesStorageAmount = Double.parseDouble(storageBean.getStrStorageAmount()); //昨日库存
                            //昨日库存为负数，为卖出认购状态
                            trade = new OptionsTradeRealBean();
                            trade.setNum(rs.getString("FNum")); //交易编号
                            trade.setCloseNum("06"); //结算，卖出
                            trade.setTradeAmount(yesStorageAmount < 0 ?YssD.sub(0, yesStorageAmount):yesStorageAmount); //库存
                            trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                            trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                            trade.setTradeMoney(Double.parseDouble(storageBean.getStrStorageCost())); //成交金额
                            insertOptTradeReal(trade, rs); //为变量赋值方法
                            alResult.add(trade);
                            trade = null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("拆分交易数据出错！\r\t", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alResult;
    }
    
    private void doDropRightSQL(ArrayList alResult) throws YssException{
    	String sSql = "";
    	String sYearMonth = "";
    	ResultSet rs = null;
    	CashAccountBean caBean = null;//声明现金账户的bean
    	String strCashAccCode = " "; //现金帐户
    	try{
    		if(alResult.size() > 0){
    			alResult.clear();
    		}
    		sYearMonth = YssFun.left(YssFun.formatDate(this.dDate,"yyyyMMdd"),4) + "00";
    		sSql = " select a.*,b.FstorageAmount,b.FStorageCost,b.FPortCode,b.FAnalysiscode1 as FInvMgrCode," +
       	 			" b.FAnalysiscode1 as FBrokerCode,b.FBaseCuryRate,b.FPortCuryRate,b.FCuryCode,e.ftradeamount from " + 
       	 			pub.yssGetTableName("tb_para_optioncontract")+
       	 			" a join (select * from " + pub.yssGetTableName("tb_stock_security") +
       	 			" where FCheckState = 1 and FYearMonth <> " + dbl.sqlString(sYearMonth) +
       	 			" and FPortCode in (" + this.operSql.sqlCodes(this.sPortCode) + ")" +
       	 			" and FStoragedate = " + dbl.sqlDate(YssFun.addDay(this.dDate,-1)) +
       	 			" ) b on a.foptioncode = b.FSecurityCode" +
       	 			" left join (select sum(case when d.fclosenum in ('01', '04') then d.ftradeamount else -d.ftradeamount " + //modify by fangjiang story 1342 2011.09.07
       	 			" end) as ftradeamount,d.fsecuritycode,d.fportcode,d.fbargaindate from " +
       	 			pub.yssGetTableName("tb_data_optionstraderela")+
       	 			" d where d.fcheckstate = 1 and d.fbargaindate = " + dbl.sqlDate(this.dDate) +
       	 			" and d.fportcode in (" + this.operSql.sqlCodes(this.sPortCode) + ")" +
       	 			" group by d.fsecuritycode, d.fportcode, d.fbargaindate) e on e.FSecurityCode = a.foptioncode " +
       	 			" and e.fbargaindate = a.fexpirydate and e.fportcode = b.fportcode " +
       	 			" where a.fcheckstate = 1 and a.fexpirydate = " + dbl.sqlDate(this.dDate) + 
       	 			" and not exists (select * from " + pub.yssGetTableName("tb_data_optionstrade") +
       	 			" c where c.FCheckState = 1 and c.ftradetypecode in( " + dbl.sqlString(YssOperCons.YSS_JYLX_DropExcerise) + "," +
       	 			dbl.sqlString(YssOperCons.YSS_JYLX_Excerise) + "," + dbl.sqlString(YssOperCons.YSS_JYLX_Balance)+ ")" +
       	 			" and c.fsecuritycode = a.foptioncode and c.fbargaindate = a.fexpirydate)";
    		
			 rs = dbl.queryByPreparedStatement(sSql);
	         //--------------------拼接交易编号---------------------
	         long sNum=0;//为了产生的编号不重复
	         String strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
	         strNumDate = strNumDate +
	             dbFun.getNextInnerCode(pub.yssGetTableName(
	                 "Tb_Data_optionstrade"),
	                                    dbl.sqlRight("FNUM", 6),
	                                    "000000",
	                                    " where FNum like 'T"
	                                    + strNumDate + "%'", 1);
	         strNumDate = "T" + strNumDate;
	         String s = strNumDate.substring(9, strNumDate.length());
	         sNum = Long.parseLong(s);
	         //--------------------------------end--------------------------//
	         while(rs.next()){
	        	 //modify by fangjiang story 1342 2011.09.07
	        	 /*if(Math.abs(rs.getDouble("ftradeamount")) == Math.abs(rs.getDouble("FstorageAmount"))){
	        		 continue;
	        	 }*/
	        	 if(YssD.add(rs.getDouble("FstorageAmount"), rs.getDouble("ftradeamount")) == 0.0){
	        		 continue;
	        	 }
	        	 //------- end by fangjiang story 1342 2011.09.07
	         	 //--------------------拼接交易编号---------------------
	             sNum++;
	             String tmp = "";
	             for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
	                 tmp += "0";
	             }
	             strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
	             //------------------------end--------------------------//
	         	trade = new OptionsTradeRealBean();
	            trade.setNum(strNumDate); //交易编号
	            trade.setCloseNum("07"); //期权放弃行权
	            trade.setTradeAmount(Math.abs(
		            							  YssD.add(
		            								  rs.getDouble("FstorageAmount"), 
		            								  rs.getDouble("ftradeamount")
		            							  )
	            							 )
	            					); //modify by fangjiang story 1342 2011.09.07
	            trade.setSecurityCode(rs.getString("foptioncode"));
	            trade.setTradeTypeCode(YssOperCons.YSS_JYLX_DropExcerise);
	            if(rs.getDouble("FstorageAmount") < 0){
	            	if (rs.getString("FBailType").equalsIgnoreCase("scale")) {
	            		trade.setBegBailMoney(Math.abs(YssD.mul(YssD.mul(rs.getDouble("FstorageAmount"),
	            				YssD.div(rs.getDouble("FstorageCost"),rs.getDouble("FstorageAmount"))),rs.getDouble("FMultiple"),rs.getDouble("FBailScale")))); //保证金
	            	} else {
						trade.setBegBailMoney(Math.abs(YssD.mul(rs.getDouble("FstorageAmount"),rs.getDouble("FBailfix"))));
					}
	            }else{
	             	trade.setBegBailMoney(0);
	            }
	            trade.setTradeMoney(0); //成交金额
	            //-------------------------设置现金账户链接属性值----------------------
	            BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal");
	            cashacc.setYssPub(pub);
	            cashacc.setLinkParaAttr( (rs.getString("FInvMgrCode").trim().length() > 0 ?
	             							rs.getString("FInvMgrCode") :" "), //投资经理
	                                     rs.getString("FPortCode"), //组合代码
	                                     rs.getString("foptioncode"), //证券代码
	                                     (rs.getString("FBrokerCode").trim().length() > 0 ?
	                                      rs.getString("FBrokerCode") : " "), //券商
	                                      YssOperCons.YSS_JYLX_DropExcerise,
	                                     this.dDate, //日期
	                                     rs.getString("FCuryCode"), //币种
	                                     YssOperCons.YSS_JYLX_DropExcerise); //交易类型放弃行权
	            caBean = cashacc.getCashAccountBean();
	            if (caBean != null) {
	                strCashAccCode = caBean.getStrCashAcctCode();//获取现金账户代码
	            } else {
	                throw new YssException("系统执行期权业务处理时出现异常！" + "\n" + "【" +
	                    rs.getString("ftsecuritycode") +
	                    "】处理股票期权产生关联数据没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
	            }
	            //--------------------------------------------------------------------
	            trade.setSecurityCode(rs.getString("foptioncode"));//证券代码
	            trade.setPortCode(rs.getString("FPortCode"));//组合代码
	            trade.setBrokerCode(rs.getString("FBrokerCode"));//券商
	            trade.setInvMgrCode(rs.getString("FInvMgrCode"));//投资经理
	            trade.setBegBailAcctCode(strCashAccCode);//初始保证金账户
	            trade.setChageBailAcctCode(strCashAccCode);//变动保证金账户
	            trade.setBargainDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));//成交日期
	            trade.setSettleDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));//结算日期
	            trade.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
	            trade.setPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
	            trade.setDesc("");//描述
	            trade.setSettleType(1);//结算方式
	            trade.setSettleState(0);//结算状态
	            trade.setTradePrice(YssD.div(rs.getDouble("FstorageCost"),rs.getDouble("FstorageAmount")));//成交价格
	            trade.setTradeType(rs.getString("FTradeTypeCode")); //保存期权交易类别：认购：CALL、认沽：PUT
	            trade.setDExercisePrice(rs.getDouble("FExerciseprice"));//行权价
	            trade.setMultipy(Double.toString(rs.getDouble("FMultiple")));//放大倍数
	            trade.setDropRightDataSource("1");  //add by fangjiang 2011.09.08 story 1342
	            alResult.add(trade);
	            trade = null;
	         }
    	}catch (Exception e) {
			throw new YssException("获取行权日的期权信息和库存信息数据！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    }
    /**
     * getFee 计算费用和清算款
     *
     */
    private void getFee(ArrayList alResult) throws YssException {
        ArrayList alFeeBeans = null;
        YssFeeType feeType = new YssFeeType(); //费用类型
        FeeBean feeBean = null;
        double dFeeMoney = 0;
        BaseOperDeal baseOper = new BaseOperDeal(); //处理汇率，行情，费用的类
        FeeLinkBean FeeLink = new FeeLinkBean(); //费用链接
        //feeBean.setYssPub(pub);
        baseOper.setYssPub(pub);
        FeeLink.setYssPub(pub);
        OptionsTradeRealBean trade = null; //期权交易数据表的POJO类
        //String feeA = ""; //把所有费用拼接起来 //modify by wangzuochun 2010.09.14     期权业务处理产生的费用计算不正确    QDV4赢时胜（深圳）2010年9月8日01_B
        double Total = 0; //总费用
        double[] yesDateValueAmount = null; //昨日估值增值余额和库存数量
        //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  start
        EachGetPubPara pubPara = new EachGetPubPara();;
        pubPara.setYssPub(pub);
        String sCostAccount_Para="";//是否核算成本参数
        //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  end
        double value = 0; //期权价值   //add by fangjiang 2011.09.08 story 1342
        try {
            for (int i = 0; i < alResult.size(); i++) {
                trade = (OptionsTradeRealBean) alResult.get(i);
                /** modify by wangzuochun 2010.09.14  MS01738   期权业务处理产生的费用计算不正确    QDV4赢时胜（深圳）2010年9月8日01_B    
                 * 屏蔽此处代码，原因是：若重新计算，比率又更新，则产生的资金调拨金额与清算款金额不符，费用金额也与实际期权业务数据中的费用金额不符；
                BaseLinkInfoDeal feeOper = (BaseLinkInfoDeal) pub.getOperDealCtx().getBean(
                    "FeeLinkDeal");
                feeOper.setYssPub(pub);
                //设置变量，证券代码，交易方式，组合代码，券商代码
                FeeLink.setSecurityCode(trade.getSecurityCode());
                FeeLink.setTradeTypeCode(trade.getTradeTypeCode());
                FeeLink.setPortCode(trade.getPortCode());
                FeeLink.setBrokerCode(trade.getBrokerCode());
                feeOper.setLinkAttr(FeeLink);
                alFeeBeans = feeOper.getLinkInfoBeans();
                if (alFeeBeans != null) {
                    //设置费用类型：数量，成本，利息，金额，等
                    feeType = new YssFeeType();
                    feeType.setAmount(trade.getTradeAmount());
                    feeType.setCost( -1);
                    feeType.setInterest(0);
                    feeType.setMoney(trade.getTradeMoney());
                    feeType.setIncome( -1);
                    feeType.setFee( -1);
                    for (int j = 0; j < alFeeBeans.size(); j++) {
                        feeBean = (FeeBean) alFeeBeans.get(j);
                        dFeeMoney = baseOper.calFeeMoney(feeType, feeBean);
                        feeBean.setFee(dFeeMoney);
                        feeA += feeBean.getFeeCode() + "," + Double.toString(dFeeMoney) + ",";
                    }
                }
                String[] str = feeA.split(",");
                //把计算的费用赋值
                trade.setFeeCode1(str.length == 2 ? str[0] : "");
                trade.setTradeFee1(str.length == 2 ? Double.parseDouble(str[1]) : 0);
                trade.setFeeCode2(str.length == 4 ? str[2] : "");
                trade.setTradeFee2(str.length == 4 ? Double.parseDouble(str[3]) : 0);
                trade.setFeeCode3(str.length == 6 ? str[4] : "");
                trade.setTradeFee3(str.length == 6 ? Double.parseDouble(str[5]) : 0);
                trade.setFeeCode4(str.length == 8 ? str[6] : "");
                trade.setTradeFee4(str.length == 8 ? Double.parseDouble(str[7]) : 0);
                trade.setFeeCode5(str.length == 10 ? str[8] : "");
                trade.setTradeFee5(str.length == 10 ? Double.parseDouble(str[9]) : 0);
                trade.setFeeCode6(str.length == 12 ? str[10] : "");
                trade.setTradeFee6(str.length == 12 ? Double.parseDouble(str[11]) : 0);
                trade.setFeeCode7(str.length == 14 ? str[12] : "");
                trade.setTradeFee7(str.length == 14 ? Double.parseDouble(str[13]) : 0);
                trade.setFeeCode8(str.length == 16 ? str[14] : "");
                trade.setTradeFee8(str.length == 16 ? Double.parseDouble(str[15]) : 0);
                feeA = "";
                **/
                yesDateValueAmount = getYesDateAddValue(trade);
                //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  start
                pubPara.setSPortCode(trade.getPortCode());
                pubPara.setSPubPara(trade.getSecurityCode());
                pubPara.setsDate(YssFun.formatDate(trade.getBargainDate()));
                //20120816 added by liubo.Story #2754
                //与钱有关的项的判断，不在判断通参的“是否核算成本”的值，改为判断“是否有资金流动”的值
                //=================================
                pubPara.setCtlFlag("selTransfering");   
                if("32FP".equalsIgnoreCase(trade.getTradeTypeCode())){
                	if(yesDateValueAmount[1] > 0){
                		pubPara.setTradeType("01");
                	}else{
                		pubPara.setTradeType("02");
                	}
                }else{
            	    pubPara.setTradeType(trade.getTradeTypeCode());
                }             
                //==============end===================
                sCostAccount_Para = pubPara.getOptCostAccountSet();
                //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  end
                Total = trade.getTradeFee1() + trade.getTradeFee2() + trade.getTradeFee3() + trade.getTradeFee4() +
                        trade.getTradeFee5() + trade.getTradeFee6() + trade.getTradeFee7() + trade.getTradeFee8();
                
                if (sCostAccount_Para.equalsIgnoreCase("false")) {
                	trade.setSettleMoney(Total);
                } else {
                    if ("01".equals(trade.getCloseNum())) {
                    	trade.setSettleMoney(YssD.add(trade.getTradeMoney(), Total)); //清算款=权利金+交易费用
                    } else if ("02".equals(trade.getCloseNum())) {
                    	trade.setSettleMoney(YssD.sub(trade.getTradeMoney(), Total)); //清算款=权利金-交易费用
                    } else if ("03".equals(trade.getCloseNum())) {
                    	trade.setSettleMoney(YssD.sub(trade.getTradeMoney(), Total)); //清算款=权利金-交易费用
                    } else if ("04".equals(trade.getCloseNum())) {
                    	trade.setSettleMoney(YssD.add(trade.getTradeMoney(), Total)); //清算款=权利金+交易费用
                    }
                    if (trade.getTradeTypeCode().equals(YssOperCons.YSS_JYLX_Excerise)) { //期权行权
                    	if("USA".equalsIgnoreCase(trade.getExecuteTypeCode())){ //美式行权
                    		if(trade.getTradeType().equalsIgnoreCase("call")) { //认购
                				value = YssD.sub(trade.getDIndexPrice(),trade.getDExercisePrice()); 
                    		}else{ //认沽
                				value = YssD.sub(trade.getDExercisePrice(),trade.getDIndexPrice());
                    		}
                    	}else if("EUR".equalsIgnoreCase(trade.getExecuteTypeCode())){ //欧式行权
                			value = trade.getMarketValue();                  		
                    	}                    	
                    	value = YssD.mul
	            				(
			            			value, 
			            			YssFun.toDouble(trade.getMultipy()), 
			            			trade.getTradeAmount()
	            				);
                    	value = YssD.round(value, 2);
                    	if(trade.getTradeMoney() > 0){
                    		value = trade.getTradeMoney();
                    	}
                    	//清算款
                    	if(yesDateValueAmount[1] > 0){
                    		trade.setSettleMoney(YssD.sub(value, Total));
                    	}else{
                    		trade.setSettleMoney(YssD.add(value, Total));
                    	}
                    } else if (trade.getTradeTypeCode().equals(YssOperCons.YSS_JYLX_DropExcerise)) { //期权放弃行权
                        trade.setSettleMoney(0);
                    } else if (trade.getTradeTypeCode().equals(YssOperCons.YSS_JYLX_Balance)) {     //期权结算
                    	trade.setSettleMoney(0);
                    }                   
                } 
            }
        } catch (Exception e) {
            throw new YssException("计算费用和清算款出错！\r\t", e);
        }
    }

    /**
     * getYesDateAddValue 获取昨日估值增值和库存数量
     *
     * @param trade OptionsTradeRealBean
     * @return double
     */
    private double[] getYesDateAddValue(OptionsTradeRealBean trade) throws YssException {
        OptionsCostAddValueManage costAddValue = new OptionsCostAddValueManage();
        costAddValue.setYssPub(pub);
        StringBuffer buff = null;
        ResultSet rs = null;
        double[] yesDateValueAmount = new double[2];
        try {
            buff = new StringBuffer();
            buff.append(" select stock.*,sec.FVBal,sec.FVBaseCuryBal,sec.FVPortCuryBal from (")
                .append(" select s.*,(case when s.fstorageamount >= 0 then ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FP01_MV))
                .append(" else ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FP01_SR)).append(" end) as FState from ")
                .append(pub.yssGetTableName("Tb_Stock_Security")).append(" s where FCheckState = 1")//证券库存表
                .append(" and FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1))).append(") stock join (");
            buff.append(" select * from ").append(pub.yssGetTableName("tb_para_optioncontract")).append(" where FOptionCode=");//期权信息设置表
            buff.append(dbl.sqlString(trade.getSecurityCode())).append(" and FCheckState = 1) tract on tract.FOptionCode = stock.fsecuritycode");
            buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_stock_secrecpay"));//库存应收应付表
            buff.append("  where FCheckState = 1 and fstoragedate =").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
            buff.append(" and FSecuritycode=").append(dbl.sqlString(trade.getSecurityCode()));
            buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            buff.append(" ) sec on sec.fsecuritycode =stock.fsecuritycode and sec.fstoragedate =stock.FStorageDate");
            buff.append(" and sec.FPortCode =stock.fportcode and sec.FSubTsfTypeCode =FState");
            buff.append(" where stock.FStorageDate=").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
            buff.append(" and stock.FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            buff.append(" and stock.FCheckState = 1");
            rs = dbl.queryByPreparedStatement(buff.toString());
            if (rs.next()) {
                yesDateValueAmount[0] = rs.getDouble("FVBal");
                yesDateValueAmount[1] = rs.getDouble("FStorageAmount");
            }
        } catch (Exception e) {
            throw new YssException("获取昨日估值增值和库存数量！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return yesDateValueAmount;
    }

    /**
     * insertOptTradeReal 给变量赋值
     *
     * @param trade OptionsTradeBean
     */
    private void insertOptTradeReal(OptionsTradeRealBean trade, ResultSet rs) throws YssException {
        try {
            trade.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
            trade.setPortCode(rs.getString("FPortCode"));//组合代码
            trade.setBrokerCode(rs.getString("FBrokerCode"));//券商
            trade.setInvMgrCode(rs.getString("FInvMgrCode"));//投资经理
            trade.setBegBailAcctCode(rs.getString("FBegBailAcctCode"));//初始保证金账户
            trade.setChageBailAcctCode(rs.getString("FChageBailAcctCode"));//变动保证金账户
            trade.setBargainDate(rs.getDate("FBargainDate").toString());//成交日期
            trade.setSettleDate(rs.getDate("FSettleDate").toString());//结算日期
            trade.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
            trade.setPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
            trade.setInvestTastic(rs.getString("FInvestTactics"));//投资策略
            trade.setDesc(rs.getString("FDesc"));//描述
            trade.setSettleType(rs.getInt("FSettleType"));//结算方式
            trade.setSettleState(rs.getInt("FSettleState"));//结算状态
            trade.setTradePrice(rs.getDouble("FTradePrice"));//成交价格
            trade.setTradeType(rs.getString("FOPTradeType")); //保存期权交易类别：认购：CALL、认沽：PUT
            trade.setDExercisePrice(rs.getDouble("FExerciseprice"));//行权价
            trade.setDIndexPrice(rs.getDouble("FClosedValue"));//指数行情价
            trade.setMultipy(Double.toString(rs.getDouble("FMultiple")));//放大倍数
            trade.setExecuteTypeCode(rs.getString("FExecuteTypeCode")); //add by fangjiang 2011.09.08 story 1342
            trade.setMarketValue(rs.getDouble("FClosingPrice"));//add by fangjiang 2011.09.08 story 1342
            
            //------ add by wangzuochun 2010.09.14  MS01738    期权业务处理产生的费用计算不正确    QDV4赢时胜（深圳）2010年9月8日01_B 
            //------ 将交易费用直接设置到trade对象中，以便后面直接从对象中取出，不要再根据费用链接去重新计算费用，
            //------ 若重新计算，比率又更新，则产生的资金调拨金额与清算款金额不符，费用金额也与实际期权业务数据中的费用金额不符；
            trade.setFeeCode1(rs.getString("FFeeCode1"));
            trade.setTradeFee1(rs.getDouble("FTradeFee1"));
            trade.setFeeCode2(rs.getString("FFeeCode2"));
            trade.setTradeFee2(rs.getDouble("FTradeFee2"));
            trade.setFeeCode3(rs.getString("FFeeCode3"));
            trade.setTradeFee3(rs.getDouble("FTradeFee3"));
            trade.setFeeCode4(rs.getString("FFeeCode4"));
            trade.setTradeFee4(rs.getDouble("FTradeFee4"));
            trade.setFeeCode5(rs.getString("FFeeCode5"));
            trade.setTradeFee5(rs.getDouble("FTradeFee5"));
            trade.setFeeCode6(rs.getString("FFeeCode6"));
            trade.setTradeFee6(rs.getDouble("FTradeFee6"));
            trade.setFeeCode7(rs.getString("FFeeCode7"));
            trade.setTradeFee7(rs.getDouble("FTradeFee7"));
            trade.setFeeCode8(rs.getString("FFeeCode8"));
            trade.setTradeFee8(rs.getDouble("FTradeFee8"));
            //------------------MS01738------------------//
        } catch (Exception e) {
            throw new YssException("给变量赋值出错！\r\t", e);
        }
    }

    /**
     * getTheDateTradeAmount 获取期权基本信息：放大倍数，保证金类型，保证金比例，每首固定保证金，以及今天期权行情，指数价格
     *
     * @return String
     */
    private String getTheDateTradeAmount() throws YssException {
        StringBuffer sqlBuf = new StringBuffer();
        try {
            sqlBuf.append(" SELECT trade.*,contract.fmultiple,contract.ftradetypecode as FOPTradeType,contract.fexerciseprice,");
            sqlBuf.append(" contract.fbailtype,contract.fbailscale,contract.fbailfix,contract.FExecuteTypeCode,contract.fexpirydate,market.FClosingPrice,");
            sqlBuf.append(" indexdata.FClosedValue").append(" FROM " + pub.yssGetTableName("TB_Data_OptionsTrade"));//期权交易数据表
            sqlBuf.append(" trade left join (select * from ").append(pub.yssGetTableName("Tb_Para_OptionContract"));//期权信息设置表 
            sqlBuf.append(" where FCheckState = 1) contract on trade.FSecurityCode =contract.FOptionCode");
            sqlBuf.append(" left join (select t2.*,oo.FOptionCode from (select max(FDate) as FDate, FIndexCode from ");
            sqlBuf.append(pub.yssGetTableName("Tb_Data_Index")).append(" where FCheckState = 1 and FDate <=");//指数行情表
            sqlBuf.append(dbl.sqlDate(this.dDate)).append(" group by FIndexCode) t1 left join (select * from ");
            sqlBuf.append(pub.yssGetTableName("Tb_Data_Index")).append(" where FCheckState = 1) t2 on t1.FDate = t2.FDate");
            sqlBuf.append(" and t1.FIndexCode =t2.FIndexCode").append(" left join (select * from ");
            sqlBuf.append(pub.yssGetTableName("tb_para_optioncontract")).append(" where FCheckState = 1) oo on t2.findexcode =oo.FTSecurityCode");
            sqlBuf.append(") indexdata on trade.fsecuritycode =indexdata.FOptionCode");
            sqlBuf.append(" left join (select m2.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode from ");
            sqlBuf.append(pub.yssGetTableName("Tb_Data_MarketValue")).append(" where FCheckState = 1 and FMktValueDate <=");//行情表
            sqlBuf.append(dbl.sqlDate(this.dDate)).append(" group by FSecurityCode) m1 left join (select * from ");
            sqlBuf.append(pub.yssGetTableName("Tb_Data_MarketValue")).append(" where FCheckState = 1) m2 on m1.FSecurityCode =");
            sqlBuf.append(" m2.FSecurityCode and m1.FMktValueDate =m2.FMktValueDate) market on trade.fsecuritycode =market.FSecurityCode");
            sqlBuf.append(" WHERE trade.FCheckState = 1");
            sqlBuf.append(" AND trade.FBargainDate = " + dbl.sqlDate(this.dDate));
            sqlBuf.append(" AND trade.FPortCode in(" + this.operSql.sqlCodes(this.sPortCode)).append(")");
        } catch (Exception e) {
            throw new YssException("获取期权基本信息：放大倍数，保证金类型，保证金比例，每首固定保证金，以及今天期权行情，指数价格！", e);
        }
        return sqlBuf.toString();
    }

    /**
     * getYesStorage 获取昨日库存数量的sql语句
     *
     * @return double
     */
    private String getYesStorage() throws YssException {
        StringBuffer sqlBuf = new StringBuffer();
        try {
            sqlBuf.append("select stock.*").append(" FROM ").append(pub.yssGetTableName("Tb_Stock_Security"));//证券库存表
            sqlBuf.append(" stock  join (select * from ").append(pub.yssGetTableName("Tb_Para_OptionContract"));//期权信息设置表
            sqlBuf.append(" where FCheckState=1) tract on tract.FOptionCode=stock.FSecurityCode");
            sqlBuf.append(" WHERE stock.FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
            sqlBuf.append(" and stock.FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            sqlBuf.append(" and stock.FCheckState=1");
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return sqlBuf.toString();
    }
}
