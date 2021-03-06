package com.yss.main.operdeal.opermanage;

import java.util.*;
import java.sql.*;

import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.*;
import com.yss.main.operdata.*;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.commeach.EachRateOper;
import com.yss.dsub.YssPreparedStatement;
import com.yss.util.YssFun;
import com.yss.manager.*;
import com.yss.main.cashmanage.*;
import com.yss.main.dao.ICostCalculate;
import com.yss.pojo.cache.YssCost;

/**
 *
 * <p>Title: 开放式基金业务处理</p>
 *
 * <p>Description: 包含开放式基金业务数据、货币基金收益支付、ETF 申购赎回时证券成本应收应付的处理</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OpenFundManage
    extends BaseOperManage {

    private ArrayList alCloseType = null;
    //储存计息相关参数的值，Key = 组合代码 + "\t" + 参数的词汇代码
    private HashMap hmParams = null;
    //产生资金调拨的交易编号
    private String sCashTradeNum = "";
    private String openFundNum="";//story 1574 add by zhouwei 20111109 记录关联编号
    boolean analy1;
    boolean analy2;
    boolean analy3;

    public OpenFundManage() {
    }

    /**
     *
     * @param dDate Date
     * @param portCode String
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     *   method
     */
    public void initOperManageInfo(java.util.Date dDate, String portCode) throws YssException {
        this.dDate = dDate;
        this.sPortCode = portCode;
    }

    /**
     *
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage
     *   method
     */
    public void doOpertion() throws YssException {
        ArrayList alCashTrans = new ArrayList();
        ArrayList alCashRecPay = new ArrayList();
        ArrayList alCashRecPay2 = new ArrayList();//存放场内的分红转投产生的现金应收应付 story 1574 add by zhouwei 20111101
        ArrayList alSubTrade=new ArrayList();//存放场外的分红转投产生的交易数据 story 1574 add by zhouwei 20111101
        ArrayList alSecRecPay = new ArrayList();
        CashTransAdmin cashAdmin = new CashTransAdmin();
        SecRecPayAdmin payAdmin = new SecRecPayAdmin();
        CashPayRecAdmin cashPayAdmin = new CashPayRecAdmin();
        ArrayList alIntegrated = new ArrayList();
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {

            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");


            //处理开放式基金业务数据
            OpenFundDeal(alCashTrans, alIntegrated, alCashRecPay, alSecRecPay,alSubTrade);//story 1574 20111109 update by zhouwei 增加交易数据
            //delete by songjie 2012.03.02 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A
            //场内分红转投业务处理转移到  场内分红转投业务处理 项 处理
            //story 1574 add by zhouwei 20111101 处理场内的分红转投交易数据
            //dealExchangeDividendSubTrade(alCashRecPay2);
            //插入证券变动
            insertData(alIntegrated);

            //------------插入资金调拨
            cashAdmin.setYssPub(pub);
            cashAdmin.addList(alCashTrans);
            conn.setAutoCommit(false);
            bTrans = true;
            cashAdmin.insert("", null,//edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A 只根据申请日期删数据
                             dDate,
                             //edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A 添加 02、02TR
                             "05,03,06,02", "05TR,03TR,06DV_TR,02TR,02DV",//STORY 1574 BY ZHOUWEI 20111110
                             "", "", "", "", "",
                             YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND,
                             "",
                             0, "", sPortCode,
                             0, "", "", "", true, "");
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //------------------证券应收应付----------------------//
            payAdmin.setYssPub(pub);
            payAdmin.addList(alSecRecPay);
            conn.setAutoCommit(false);
            bTrans = true;
            payAdmin.insert("",
                            dDate,
                            dDate,
                            "02,06,09,98",//BUG 3334 BY ZHOUWEI 20111223 QDV4嘉实基金2011年12月06日01_B
                            //edit by songjie 2011.06.20 BUG 2088 QDV4赢时胜（深圳）2011年6月13日01_B
                            //添加 02DV_TR 
                            "02TR,06TR,09TR,02DV_TR,9802DV",//BUG 3334 BY ZHOUWEI 20111223 QDV4嘉实基金2011年12月06日01_B
                            sPortCode,
                            "","", "", "",
                            0,
                            true,
                            0,
                            false,
                            "", "",
                            YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------//
            //--------------------现金应收应付--------------------//
            cashPayAdmin.setYssPub(pub);
            cashPayAdmin.getList().addAll(alCashRecPay);
            conn.setAutoCommit(false);
            bTrans = true;
            cashPayAdmin.insert("",
                                dDate,
                                dDate,
                                "","","","",
                                sPortCode,
                                "","","",
                                0,
                                true,false,false,
                                0,
                                "",
                                YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------//
            //story 1574 add by zhouwei 20111101
            //--------------------场内现金应收应付--------------------//
            cashPayAdmin.setYssPub(pub);
            cashPayAdmin.getList().clear();
            cashPayAdmin.getList().addAll(alCashRecPay2);
            conn.setAutoCommit(false);
            bTrans = true;
            cashPayAdmin.insert("",
                                dDate,
                                dDate,
                                "","","","",
                                sPortCode,
                                "","","",
                                0,
                                true,false,false,
                                1,//流入
                                "",
                                "openfundExchange");
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------//
            //story 1574 add by zhouwei 20111101
            //--------------------场外产生的交易数据--------------------//
            TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
            tradeData.setYssPub(pub);
            tradeData.addAll(alSubTrade);//添加数据
            conn.setAutoCommit(false);
            bTrans = true;
            String subTradeNum=getSubTradeCodes();
            if(subTradeNum!=null && !subTradeNum.equals("")){
            	 tradeData.insert(subTradeNum, dDate, dDate, null, null, "", sPortCode, "",
         				"", "39", "", "", true, false, "");
            }          
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------//
            
            
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(alCashTrans.size()==0 && alIntegrated.size()==0 && alCashRecPay.size()==0&& alSecRecPay.size()==0&&alSubTrade.size()==0){
    			this.sMsg="        当日无业务";
    		}
        } catch (Exception ex) {
            throw new YssException(ex);
        } finally{
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 处理 ETF 申购赎回冲减的证券成本和估值增值
     * @param alIntegrated ArrayList：装载证券变动数据
     * @param alRecPay ArrayListL：装载应收应付数据
     * @throws YssException
     */
    public String ETFFundDeal(ArrayList alIntegrated,
                            ArrayList alRecPay) throws YssException{
        StringBuffer bufSql = new StringBuffer();
        SecIntegratedBean inteCost = null;
        SecPecPayBean recPay = null;
        ResultSet rs = null;
        ResultSet rsSub = null;
        String sTradeNums = "";
        try {
            //查询得到证券成本的变动
            bufSql.append(" SELECT a.FPortCode, a.FInvMgrCode, a.FBrokerCode, a.Fsecuritycode AS FCSSecuritycode, a.Ftradetypecode, a.fbasecuryrate, a.fportcuryrate, a.FAttrClsCode, b.*, c.fsubcatcode, c.ftradecury ");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_Subtrade") + " a ");
            bufSql.append(" JOIN " + pub.yssGetTableName("Tb_Data_TradeRela") + " b ON a.fnum = b.fnum ");
            bufSql.append(" JOIN " + pub.yssGetTableName("Tb_Para_Security") + " c ON a.fsecuritycode = c.fsecuritycode ");
            bufSql.append(" WHERE c.fcatcode = 'TR' ");
            bufSql.append(" AND c.fsubcatcode = 'TR04' ");
            bufSql.append(" AND a.FCheckState = 1 ");
            bufSql.append(" AND b.Fcheckstate = 1 ");
            bufSql.append(" AND a.Fbargaindate = " + dbl.sqlDate(dDate));
            bufSql.append(" AND a.FPortCode = " + dbl.sqlString(sPortCode));
            rs = dbl.queryByPreparedStatement(bufSql.toString());
            while(rs.next()){
                sTradeNums += (rs.getString("FNum") + ",");
                inteCost = new SecIntegratedBean();
                //申购时证券成本流出，赎回时证券成本流入
                if(rs.getString("Ftradetypecode").equalsIgnoreCase("15")){
                    inteCost.setIInOutType(-1);
                } else {
                    inteCost.setIInOutType(1);
                }
                inteCost.setSSecurityCode(rs.getString("FSecurityCode"));
                inteCost.setSExchangeDate(YssFun.formatDate(dDate, "yyyy-MM-dd"));
                inteCost.setSOperDate(YssFun.formatDate(dDate, "yyyy-MM-dd"));
                inteCost.setSRelaNum(" ");
                inteCost.setSNumType(" ");

                inteCost.setSTradeTypeCode(rs.getString("FTradeTypeCode"));

                inteCost.setSPortCode(rs.getString("FPortCode"));
                if (analy1) {
                    inteCost.setSAnalysisCode1(rs.getString("FInvMgrCode"));
                } else {
                    inteCost.setSAnalysisCode1(" ");
                }
                if (analy2) {
                    inteCost.setSAnalysisCode2(rs.getString("FBrokerCode"));
                } else {
                    inteCost.setSAnalysisCode2(" ");
                }
                inteCost.setSAnalysisCode3(" ");

                inteCost.setAttrClsCode(rs.getString("FAttrClsCode"));

                inteCost.setDAmount(rs.getDouble("FAmount"));

                inteCost.setDCost(rs.getDouble("FCost"));
                inteCost.setDMCost(inteCost.getDCost());
                inteCost.setDVCost(inteCost.getDCost());

                inteCost.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                inteCost.setDBaseCost(rs.getDouble("FBaseCuryCost"));
                inteCost.setDMBaseCost(inteCost.getDBaseCost());
                inteCost.setDVBaseCost(inteCost.getDBaseCost());

                inteCost.setDPortCuryRate(rs.getDouble("FPortCuryRate"));
                inteCost.setDPortCost(rs.getDouble("FPortCuryCost"));
                inteCost.setDMPortCost(inteCost.getDPortCost());
                inteCost.setDVPortCost(inteCost.getDPortCost());

                inteCost.checkStateId = 1;
                inteCost.setSTsfTypeCode("05");
                inteCost.setSSubTsfTypeCode("05" + rs.getString("fsubcatcode"));

                alIntegrated.add(inteCost);

                bufSql = new StringBuffer();
                //查询应收应付的变动
                bufSql.append(" SELECT * ");
                bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_Traderelasub"));
                bufSql.append(" WHERE FCheckState = 1 ");
                bufSql.append(" AND FNum = " + dbl.sqlString(rs.getString("FNum")));
                bufSql.append(" AND FSecurityCode = " + dbl.sqlString(rs.getString("FSecurityCode")));
                rsSub = dbl.queryByPreparedStatement(bufSql.toString());

                while(rsSub.next()){
                    recPay = new SecPecPayBean();
                    recPay.setTransDate(dDate);
                    recPay.setCheckState(1);
                    recPay.setAttrClsCode(rs.getString("FAttrClsCode"));
                    recPay.setRelaNum(rs.getString("FNum"));
                    recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_SUBTRADE);
                    recPay.setStrPortCode(rs.getString("FPortCode"));
                    recPay.setInvMgrCode(rsSub.getString("FAnalysisCode1"));
                    recPay.setBrokerCode(rsSub.getString("FAnalysisCode2"));
                    recPay.setStrSecurityCode(rsSub.getString("FSecurityCode"));
                    recPay.setStrCuryCode(rsSub.getString("FCuryCode"));
                    recPay.setStrTsfTypeCode(rsSub.getString("FTsfTypeCode"));
                    recPay.setStrSubTsfTypeCode(rsSub.getString("FSubTsfTypeCode"));
                    //应收应付金额的正负和成本的流入流出一致
                    recPay.setMoney(YssD.mul(rsSub.getDouble("FBal"), inteCost.getIInOutType()));

                    recPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                    recPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));

                    recPay.setMMoney(recPay.getMoney());
                    recPay.setVMoney(recPay.getMoney());

                    recPay.setBaseCuryMoney(YssD.mul(rsSub.getDouble("FBaseCuryBal"), inteCost.getIInOutType()));
                    recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
                    recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());

                    recPay.setPortCuryMoney(YssD.mul(rsSub.getDouble("FPortCuryBal"), inteCost.getIInOutType()));
                    recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
                    recPay.setVPortCuryMoney(recPay.getPortCuryMoney());
                    alRecPay.add(recPay);
                }
                //外围游标每次循环结束，要关闭循环内打开的游标 sunkey@Modify 20090922
                dbl.closeResultSetFinal(rsSub);
            }
            if(sTradeNums.length() > 0){
                sTradeNums = sTradeNums.substring(0, sTradeNums.length() - 1);
            }
        } catch (Exception ex) {
            throw new YssException("处理 ETF 基金申购赎回业务出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rsSub,rs);
        }
        return sTradeNums;
    }

    /**
     * 基金认购申购业务
     * 申请时除了产生流出的资金调拨，还要产生一笔应收数据，保证基金资产不变，单日头寸发生变化
     * 确认和退回发生时需要产生对应的收入，冲减掉申请时的应收数据
     * @param alRecPay ArrayList：装载应收应付数据
     * @param rs ResultSet
     * @throws YssException
     */
    public void fundBuyDeal(ArrayList alCashRecPay, ResultSet rs) throws YssException {
        CashPecPayBean recPay = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        String sTradeTypeCode = "";
        try {
            sTradeTypeCode = rs.getString("FTradeTypeCode");

            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);

            recPay = new CashPecPayBean();
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
            recPay.setTradeDate(dDate);
            recPay.setPortCode(rs.getString("FPortCode"));
            recPay.setInvestManagerCode(analy1 ? (rs.getString("FInvMgrCode") + "") :
                                 " ");
            recPay.setBrokerCode(" ");
            recPay.setCuryCode(rs.getString("FTradeCury"));
            recPay.setDataSource(0);
            recPay.setStockInd(1);
            recPay.checkStateId = 1;
            //申请时，调拨类型是应收
            if (rs.getString("FDataType").equalsIgnoreCase("apply")) {
                recPay.setTsfTypeCode("06");
                //如果是分红产生06DV_TR 应收基金红利，否则产生 06TR
                if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)) {
                    //recPay.setSubTsfTypeCode("06DV_TR");
                	recPay.setSubTsfTypeCode("06DV");//story 1574 update by zhouwei 20111109 调拨子类型改为06DV应收股利
                } else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SGou)){//add by yanghaiming 20100712 MS01348 QDV4国内(测试)2010年06月24日02_AB 
                	//------ modify by wangzuochun BUG2067做场外开放式基金申购和网下新股申购理业务错误
                	//edit by songjie 2011.08.25 BUG 2541 QDV4建行2011年08月24日03_B 申购日生成 06AP_TR的现金应收应付数据
                	recPay.setSubTsfTypeCode("06AP_TR");
                } else {
                    recPay.setSubTsfTypeCode("06TR");
                }

                recPay.setMoney(rs.getDouble("FApplyMoney"));
                recPay.setCashAccCode(rs.getString("FApplyCashAccCode"));
                recPay.setInOutType(1);
            } else if (rs.getString("FDataType").equalsIgnoreCase("return")) {//退回时调拨类型是收入
                //如果是分红产生02DV_TR 基金红利收入，否则产生 02TR
                if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)) {
                    recPay.setTsfTypeCode("02"); //收入
                    //recPay.setSubTsfTypeCode("02DV_TR");
                    recPay.setSubTsfTypeCode("02DV");//story 1574 update by zhouwei 20111109 调拨子类型改为02DV股票分红
                    recPay.setInOutType(1);
                    recPay.setCashAccCode(rs.getString("FLkCashAcccode")); //退回时产生的应收应付是反冲申请时的帐户
                }else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SH)) {//edti by yanghaiming 20100705 MS01234 QDV4赢时胜上海2010年5月31日01_A 
                	recPay.setTsfTypeCode("06"); //流出的应收
                    recPay.setSubTsfTypeCode("06CF_TR");
                    recPay.setInOutType(-1);
                    recPay.setCashAccCode(rs.getString("FRTNCASHACCCODE"));
                }else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SGou)){//add by yanghaiming 20100712 MS01348 QDV4国内(测试)2010年06月24日02_AB
                	recPay.setTsfTypeCode("06"); //流出的应收
                    recPay.setSubTsfTypeCode("06AP_TR");
                    recPay.setInOutType(-1);
                    recPay.setCashAccCode(rs.getString("FLkCashAcccode")); //退回时产生的应收应付是反冲申请时的帐户
                }
                else {
                    recPay.setTsfTypeCode("06"); //流出的应收
                    recPay.setSubTsfTypeCode("06TR");
                    recPay.setInOutType(-1);
                    recPay.setCashAccCode(rs.getString("FLkCashAcccode")); //退回时产生的应收应付是反冲申请时的帐户
                }
                recPay.setMoney(YssD.add(rs.getDouble("FReturnMoney"), rs.getDouble("FReturnFee")));  
            } else {
                //如果是金除货币基以外的分红产生负的 06DV_TR 应收基金红利，货币基金分红产生收入，否则产生 06TR
                if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) &&
                    !rs.getString("FSubCatCode").equalsIgnoreCase("TR03")) {
                    recPay.setInOutType( -1);
                    recPay.setTsfTypeCode("06"); //应收
                    recPay.setSubTsfTypeCode("06DV_TR");
                    recPay.setMoney(YssD.add(rs.getDouble("FComfMoney"), rs.getDouble("FComfFee")));
                    recPay.setCashAccCode(rs.getString("FLkCashAcccode"));
                } else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) &&
                           rs.getString("FSubCatCode").equalsIgnoreCase("TR03")) { //货币基金产生收入
                    recPay.setInOutType(1);
                    recPay.setTsfTypeCode("02"); //流出的应收
                    recPay.setSubTsfTypeCode("02TR");
                    recPay.setMoney(YssD.add(rs.getDouble("FComfMoney"), rs.getDouble("FComfFee")));
                    recPay.setCashAccCode(rs.getString("FLkCashAcccode"));
                } else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SH)) {//edti by yanghaiming 20100705 MS01234 QDV4赢时胜上海2010年5月31日01_A 
                	recPay.setInOutType(1);
                    recPay.setTsfTypeCode("06"); //应收
                    recPay.setSubTsfTypeCode("06CF_TR");
                    recPay.setMoney(YssD.sub(rs.getDouble("FComfMoney"), rs.getDouble("FComfFee")));//modify by zhouwei 20120419 bug 4299
                    recPay.setCashAccCode(rs.getString("FAPPLYCASHACCCODE"));
                } else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SGou)){//add by yanghaiming 20100712 MS01348 QDV4国内(测试)2010年06月24日02_AB
                	recPay.setInOutType( -1);
                    recPay.setTsfTypeCode("06"); //应收
                    recPay.setSubTsfTypeCode("06AP_TR");
                    recPay.setMoney(YssD.add(rs.getDouble("FComfMoney"), rs.getDouble("FComfFee")));
                    recPay.setCashAccCode(rs.getString("FLkCashAcccode"));
                } else { //其他交易的确认产生流出的应收款
                    recPay.setInOutType( -1);
                    recPay.setTsfTypeCode("06"); //应收
                    recPay.setSubTsfTypeCode("06TR");
                    recPay.setMoney(YssD.add(rs.getDouble("FComfMoney"), rs.getDouble("FComfFee")));
                    recPay.setCashAccCode(rs.getString("FLkCashAcccode"));
                }
                
            }

            baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();


            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                baseCuryRate, 2));

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                baseCuryRate, portCuryRate,
                rs.getString("FTradeCury"), dDate, recPay.getPortCode(), 2));
            alCashRecPay.add(recPay);

        } catch (Exception ex) {
            throw new YssException("处理申购业务出错！", ex);
        }
    }
    //story 1574 add by zhouwei 20111109 根据关联编号获取交易编号
    public String getSubTradeCodes() throws YssException{
    	String relaNums=this.openFundNum;
    	ResultSet rs=null;
    	String subTradeNum="";
    	try{
    		String sql="";
	    	if(!relaNums.equals("")){
	    		sql="select * from "+pub.yssGetTableName("tb_data_subtrade")
	    		+" where fdealnum in ("+operSql.sqlCodes(relaNums)+")";
	    		rs=dbl.openResultSet(sql);
	    		while(rs.next()){
	    			subTradeNum+=rs.getString("fnum")+",";
	    		}
	    	}
	    	return subTradeNum;
    	}catch(Exception e){
    		throw new YssException("获取交易编号出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    //story 1574 add by zhouwei 20111109 获取交易数据
    public void getSubTradeData(ResultSet rs,ArrayList alSubTrade) throws SQLException, YssException{     
        String strCashAccCode = " "; //现金帐户
        String strYearMonth = "";//保存截取日期的年和天
        CashAccountBean caBean = null;//声明现金账户的bean
        double dBaseRate = 1;//基础汇率
        double dPortRate = 1;//组合汇率
        boolean analy1;//分析代码1
        boolean analy2;//分析代码2
        boolean analy3;//分析代码3
        TradeSubBean subTrade = null;//交易子表的javaBean
        YssCost cost = null;//声明成本
        long sNum=0;//为了产生的编号不重复
        try{
	        operFun.setYssPub(pub);
	        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
	        rateOper.setYssPub(pub);
	        String strNumDate = YssFun.formatDatetime(dDate).
	            substring(0, 8);
	        strNumDate = strNumDate +
	            dbFun.getNextInnerCode(pub.yssGetTableName(
	                "Tb_Data_Trade"),
	                                   dbl.sqlRight("FNUM", 6),
	                                   "100000",//将000000改为100000 分红编号调整为100000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
	                                   " where FNum like 'T"
	                                   + strNumDate + "1%'", 1);//改为1% 分红编号调整为100000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
	        strNumDate = "T" + strNumDate;
	        strNumDate = strNumDate +
	            dbFun.getNextInnerCode(pub.yssGetTableName(
	                "Tb_Data_SubTrade"),
	                                   dbl.sqlRight("FNUM", 5), "00000",
	                                   " where FNum like '"
	                                   +
	                                   strNumDate.replaceAll("'", "''") +
	                                   "%'");
	        String s = strNumDate.substring(9, strNumDate.length());
	        sNum = Long.parseLong(s);
	        //--------------------------------end--------------------------//
	        
            subTrade = new TradeSubBean();//实例化                       
            if (rs.getString("FDataType").equalsIgnoreCase("confirm") && rs.getString("FLkCashAcccode") != null && 
            		!rs.getString("FSubCatCode").equals("TR03") && "06".equalsIgnoreCase(rs.getString("ftradetypecode"))) {
            	if(rs.getDouble("fcomfamount")>0){//判断证券数量是否大于0  
            		String tmp = "";
                    for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                        tmp += "0";
                    }
                    strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                    //------------------------end--------------------------//
                    dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            rs.getString("FTradeCury"), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);//获取基础汇率的值               
                    rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                            rs.getString("FPortCode"));
                    dPortRate = rateOper.getDPortRate();//获取组合汇率的值
                    subTrade.setNum(strNumDate);//为交易编号赋值

                    subTrade.setSecurityCode(rs.getString("FSecurityCode"));//证券代码赋值

                    subTrade.setPortCode(rs.getString("FPortCode"));//组合代码              
    				subTrade.setAttrClsCode(" ");
                    subTrade.setInvMgrCode(rs.getString("finvmgrcode").equals("")?" ":rs.getString("finvmgrcode"));
                    subTrade.setBrokerCode(" ");
                    subTrade.setTradeCode("39");//交易类型 分红转投

                    subTrade.setTailPortCode(rs.getString("FLkCashAcccode"));//尾差组合代码

                    subTrade.setAllotProportion(0);//分配比例

                    subTrade.setOldAllotAmount(0);//原始分配数量

                    subTrade.setAllotFactor(0);//分配因子
                    subTrade.setBargainDate(YssFun.formatDate(rs.getDate(
                        "fcomfdate")));//成交日期               
                    //----------------end 20101223---------------------------------------------
                    subTrade.setBargainTime("00:00:00");//成交时间

                    subTrade.setSettleDate(YssFun.formatDate(rs.getDate(
                        "fcomfdate")));//结算日期

                    subTrade.setSettleTime("00:00:00");//结算时间

                    subTrade.setAutoSettle(new Integer(1).toString()); //自动结算

                    subTrade.setPortCuryRate(dPortRate);//组合汇率

                    subTrade.setBaseCuryRate(dBaseRate);//基础汇率

                    subTrade.setTradeAmount(rs.getDouble("fcomfamount"));//交易数量

                    subTrade.setTradePrice(0);//交易价格

                    subTrade.setTradeMoney(0);//交易金额

                    subTrade.setAccruedInterest(0);//应计利息
                    cost = new YssCost();
                	cost.setCost(rs.getDouble("fcomfmoney"));
    				cost.setMCost(rs.getDouble("fcomfmoney"));
    				cost.setVCost(rs.getDouble("fcomfmoney"));
    				cost.setBaseCost(YssD.round(YssD.mul(rs.getDouble("fcomfmoney"),
    						dBaseRate), 2));
    				cost.setBaseMCost(YssD.round(YssD.mul(rs.getDouble("fcomfmoney"),
    						dBaseRate), 2));
    				cost.setBaseVCost(YssD.round(YssD.mul(rs.getDouble("fcomfmoney"),
    						dBaseRate), 2));
    				if (dPortRate != 0) {
    					cost.setPortCost(YssD.round(YssD.div(cost
    							.getBaseCost(),dPortRate), 2));
    					cost.setPortMCost(YssD.round(YssD.div(cost
    							.getBaseMCost(), dPortRate), 2));
    					cost.setPortVCost(YssD.round(YssD.div(cost
    							.getBaseVCost(), dPortRate), 2));
    				} else {
    					cost.setPortCost(0);
    					cost.setPortMCost(0);
    					cost.setPortVCost(0);
    				}
                    subTrade.setCost(cost);//成本
                    //---------------------end-----------------//
                    subTrade.setDataSource(0);//数据源

                    subTrade.setDsType("HD_QY");//操作类型，表示系统操作数据，主要是和接口导入数据进行区分

                    subTrade.checkStateId = 1;//审核状态

                    subTrade.creatorCode = pub.getUserCode();//创建人

                    subTrade.checkTime = YssFun.formatDatetime(new java.util.Date());//审核时间

                    subTrade.checkUserCode = pub.getUserCode();//审核人

                    subTrade.creatorTime = YssFun.formatDatetime(new java.util.
                        Date());//创建时间

                    subTrade.setTotalCost(0);//投资总成本

                    subTrade.setSettleState(new Integer(0).toString());//结算状态，未结算“0”

                    subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate(
                        "fcomfdate")));//实际结算日期

                    subTrade.setMatureDate("9998-12-31");//到期日期

                    subTrade.setMatureSettleDate("9998-12-31");//到期结算日期
                    strCashAccCode=rs.getString("FLkCashAcccode");
                    subTrade.setFactCashAccCode(strCashAccCode);//实际结算帐户

                    subTrade.setCashAcctCode(strCashAccCode);//设置现金账户

                    subTrade.setFactSettleMoney(0);//实际结算金额

                    subTrade.setExRate(1);//兑换汇率

                    subTrade.setFactPortRate(dPortRate); //实际结算组合汇率

                    subTrade.setFactBaseRate(dBaseRate);//实际结算基础汇率
                    subTrade.setFdealNum(rs.getString("fnum"));
                    this.openFundNum+=rs.getString("fnum")+",";
                    subTrade.setStrDivdendType("");				//分红类型
                    subTrade.setStrRecordDate(YssFun.formatDate(rs.getDate("fbargaindate")));
                    alSubTrade.add(subTrade);//把数据保存到集合中
                    sNum++;
            	}
                
            }                 
	        while (rs.next()) {  
	            subTrade = new TradeSubBean();//实例化                       
	            if (rs.getString("FDataType").equalsIgnoreCase("confirm") && rs.getString("FLkCashAcccode") != null && 
	            		!rs.getString("FSubCatCode").equals("TR03") && "06".equalsIgnoreCase(rs.getString("ftradetypecode"))) {
	            	if(rs.getDouble("fcomfamount")<=0){//判断证券数量是否大于0  
	            		continue;
	            	}
	                String tmp = "";
	                for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
	                    tmp += "0";
	                }
	                strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
	                //------------------------end--------------------------//
	                dBaseRate = this.getSettingOper().getCuryRate(dDate,
	                        rs.getString("FTradeCury"), rs.getString("FPortCode"),
	                YssOperCons.YSS_RATE_BASE);//获取基础汇率的值               
	                rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
	                        rs.getString("FPortCode"));
	                dPortRate = rateOper.getDPortRate();//获取组合汇率的值
	                subTrade.setNum(strNumDate);//为交易编号赋值
	
	                subTrade.setSecurityCode(rs.getString("FSecurityCode"));//证券代码赋值
	
	                subTrade.setPortCode(rs.getString("FPortCode"));//组合代码              
					subTrade.setAttrClsCode(" ");
	                subTrade.setInvMgrCode(rs.getString("finvmgrcode").equals("")?" ":rs.getString("finvmgrcode"));
	                subTrade.setBrokerCode(" ");
	                subTrade.setTradeCode("39");//交易类型 分红转投
	
	                subTrade.setTailPortCode(rs.getString("FLkCashAcccode"));//尾差组合代码
	
	                subTrade.setAllotProportion(0);//分配比例
	
	                subTrade.setOldAllotAmount(0);//原始分配数量
	
	                subTrade.setAllotFactor(0);//分配因子
	                subTrade.setBargainDate(YssFun.formatDate(rs.getDate(
	                    "fcomfdate")));//成交日期               
	                //----------------end 20101223---------------------------------------------
	                subTrade.setBargainTime("00:00:00");//成交时间
	
	                subTrade.setSettleDate(YssFun.formatDate(rs.getDate(
	                    "fcomfdate")));//结算日期
	
	                subTrade.setSettleTime("00:00:00");//结算时间
	
	                subTrade.setAutoSettle(new Integer(1).toString()); //自动结算
	
	                subTrade.setPortCuryRate(dPortRate);//组合汇率
	
	                subTrade.setBaseCuryRate(dBaseRate);//基础汇率
	
	                subTrade.setTradeAmount(rs.getDouble("fcomfamount"));//交易数量
	
	                subTrade.setTradePrice(0);//交易价格
	
	                subTrade.setTradeMoney(0);//交易金额
	
	                subTrade.setAccruedInterest(0);//应计利息
	                cost = new YssCost();
	            	cost.setCost(rs.getDouble("fcomfmoney"));
					cost.setMCost(rs.getDouble("fcomfmoney"));
					cost.setVCost(rs.getDouble("fcomfmoney"));
					cost.setBaseCost(YssD.round(YssD.mul(rs.getDouble("fcomfmoney"),
							dBaseRate), 2));
					cost.setBaseMCost(YssD.round(YssD.mul(rs.getDouble("fcomfmoney"),
							dBaseRate), 2));
					cost.setBaseVCost(YssD.round(YssD.mul(rs.getDouble("fcomfmoney"),
							dBaseRate), 2));
					if (dPortRate != 0) {
						cost.setPortCost(YssD.round(YssD.div(cost
								.getBaseCost(),dPortRate), 2));
						cost.setPortMCost(YssD.round(YssD.div(cost
								.getBaseMCost(), dPortRate), 2));
						cost.setPortVCost(YssD.round(YssD.div(cost
								.getBaseVCost(), dPortRate), 2));
					} else {
						cost.setPortCost(0);
						cost.setPortMCost(0);
						cost.setPortVCost(0);
					}
//	                cost.setCost(0);//原币核算成本
//	
//	                cost.setMCost(0);//原币管理成本
//	
//	                cost.setVCost(0);//原币估值成本
//	
//	                cost.setBaseCost(0);//基础货币核算成本
//	
//	                cost.setBaseMCost(0);//基础货币管理成本
//	
//	                cost.setBaseVCost(0);//基础货币估值成本
//	
//	                cost.setPortCost(0);//组合货币核算成本
//	
//	                cost.setPortMCost(0);//组合货币管理成本
//	
//	                cost.setPortVCost(0);//组合货币估值成本
	                subTrade.setCost(cost);//成本
	                //---------------------end-----------------//
	                subTrade.setDataSource(0);//数据源
	
	                subTrade.setDsType("HD_QY");//操作类型，表示系统操作数据，主要是和接口导入数据进行区分
	
	                subTrade.checkStateId = 1;//审核状态
	
	                subTrade.creatorCode = pub.getUserCode();//创建人
	
	                subTrade.checkTime = YssFun.formatDatetime(new java.util.Date());//审核时间
	
	                subTrade.checkUserCode = pub.getUserCode();//审核人
	
	                subTrade.creatorTime = YssFun.formatDatetime(new java.util.
	                    Date());//创建时间
	
	                subTrade.setTotalCost(0);//投资总成本
	
	                subTrade.setSettleState(new Integer(0).toString());//结算状态，未结算“0”
	
	                subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate(
	                    "fcomfdate")));//实际结算日期
	
	                subTrade.setMatureDate("9998-12-31");//到期日期
	
	                subTrade.setMatureSettleDate("9998-12-31");//到期结算日期
	                strCashAccCode=rs.getString("FLkCashAcccode");
	                subTrade.setFactCashAccCode(strCashAccCode);//实际结算帐户
	
	                subTrade.setCashAcctCode(strCashAccCode);//设置现金账户
	
	                subTrade.setFactSettleMoney(0);//实际结算金额
	
	                subTrade.setExRate(1);//兑换汇率
	
	                subTrade.setFactPortRate(dPortRate); //实际结算组合汇率
	
	                subTrade.setFactBaseRate(dBaseRate);//实际结算基础汇率
	                subTrade.setFdealNum(rs.getString("fnum"));
	                this.openFundNum+=rs.getString("fnum")+",";
	                subTrade.setStrDivdendType("");				//分红类型
                    subTrade.setStrRecordDate(YssFun.formatDate(rs.getDate("fbargaindate")));
	                alSubTrade.add(subTrade);//把数据保存到集合中
	                sNum++;
	            }          
	        }
        }catch (Exception e) {
        	 throw new YssException("根据开放式基金数据获取交易数据出错！", e);
		}
    }
    /*story 1574 add by zhouwei 20111109 开放式基金业务处理时
     * 对06业务类型非货币基金产生的现金应收应付数据
     * */
    public void addCashPayByOpenTrade(ArrayList alCashRecPay, ResultSet rs,
    		String tsTypeCode,String subTsTypeCode,int inOutType,double fmoney,String cashAccCode) throws YssException {
        CashPecPayBean recPay = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        String sTradeTypeCode = "";
        try {
        	 EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
             rateOper.setYssPub(pub);

             recPay = new CashPecPayBean();
             recPay.setRelaNum(rs.getString("FNum"));
             recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
             recPay.setTradeDate(dDate);
             recPay.setPortCode(rs.getString("FPortCode"));
             recPay.setInvestManagerCode(analy1 ? (rs.getString("FInvMgrCode") + "") :
                                  " ");
             recPay.setBrokerCode(" ");
             recPay.setCuryCode(rs.getString("FTradeCury"));
             recPay.setDataSource(0);
             recPay.setStockInd(1);
             recPay.checkStateId = 1;
             recPay.setTsfTypeCode(tsTypeCode);
             recPay.setSubTsfTypeCode(subTsTypeCode);
             recPay.setInOutType(inOutType);
             recPay.setMoney(fmoney);
             //edit by songjie 2011.12.13 若现金账户为null 则替换为空格
             recPay.setCashAccCode(cashAccCode == null ? " " : cashAccCode);
             baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();


            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);
            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                    baseCuryRate, 2));

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                    baseCuryRate, portCuryRate,
                    rs.getString("FTradeCury"), dDate, recPay.getPortCode(), 2));
            alCashRecPay.add(recPay);

        } catch (Exception ex) {
            throw new YssException("处理申购业务出错！", ex);
        }
    }
    /*story 1574 add by zhouwei 20111101 做业务处理时
     * 1.场内的股票分红所产生的交易数据（06分发派息）产生应收款项的现金应收应付数据
     * 2.录入交易类型为（39分红转投）的交易数据，产生收入的现金应收应付数据
     * 3.还会产生98调整金额的现金应收应付
     * */
    public void addCashPayBySubtrade(ArrayList alCashRecPay, ResultSet rs,int i) throws YssException {
        CashPecPayBean recPay = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        String sTradeTypeCode = "";
        try {
            sTradeTypeCode = rs.getString("FTradeTypeCode");
            //对于分红转投，1代表业务日期等于交割日期或结算日期等于业务日期，产生收入的应收应付，2代表结算日期等于业务日期，产生调整的应收应付
            String barginSettletState=rs.getString("barginSettletState");
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            recPay = new CashPecPayBean();
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType("openfundExchange");
            recPay.setTradeDate(dDate);
            recPay.setPortCode(rs.getString("FPortCode"));
            recPay.setInvestManagerCode((rs.getString("FInvMgrCode")!=null) ? (rs.getString("FInvMgrCode") + "") :
                                 " ");
            recPay.setBrokerCode((rs.getString("FBrokerCode")!=null) ? (rs.getString("FBrokerCode") + "") :
                " ");
            recPay.setCuryCode(rs.getString("Fcurycode"));
            recPay.setCashAccCode(rs.getString("FCashAccCode"));
            recPay.setDataSource(0);
            recPay.setStockInd(1);
            recPay.checkStateId = 1;
            //交易类型为06分发派息的交易数据
            //产生“业务类型”为“06 应收款项“，
            //“业务子类型”为“06DV  应收股利”；方向“流入”，“原币金额”为按照分红权益比例，应该得到的总金额。
            if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)){
            	recPay.setTsfTypeCode("06");
                recPay.setSubTsfTypeCode("06DV");
                recPay.setInOutType(1);
                recPay.setMoney(rs.getDouble("FTotalCost"));
            }else if(sTradeTypeCode.equalsIgnoreCase("39")){//交易类型为39分红转投
            	if(barginSettletState.equalsIgnoreCase("1")){//业务日期等于交割日期或结算日期等于业务日期
            		recPay.setTsfTypeCode("02");//收入
                    recPay.setSubTsfTypeCode("02DV");//股票分红
                    recPay.setInOutType(1);
                    recPay.setMoney(rs.getDouble("FMoney"));
            	}else{//产生调整的应收应付与负值的应收款项用于冲减，来统计正确的净值
            		if(i==1){
                		recPay.setTsfTypeCode("98");//调整金额
                        recPay.setSubTsfTypeCode("9802DV");//调整分红金额
                        recPay.setInOutType(1);
                        recPay.setMoney(rs.getDouble("FMoney"));
                	}else{
                		recPay.setTsfTypeCode("06");//调整金额
                        recPay.setSubTsfTypeCode("06DV");//调整分红金额
                        recPay.setInOutType(1);
                        recPay.setMoney(-rs.getDouble("FMoney"));
                	}
        		}
            }
            baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("Fcurycode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("Fcurycode"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();


            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);
            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                    baseCuryRate, 2));

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                    baseCuryRate, portCuryRate,
                    rs.getString("Fcurycode"), dDate, recPay.getPortCode(), 2));
            alCashRecPay.add(recPay);

        } catch (Exception ex) {
            throw new YssException("处理申购业务出错！", ex);
        }
    }
    /**
     * add by songjie 2011.08.30
     * 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A
     * 若货币式基金有分红转投数据，则生成业务类型为收入 ，
     * 业务子类型为基金红利收入的证券应收应付数据
     * @param alSecRecPay
     * @param rs
     * @throws YssException
     */
    private void dividentReInvestDeal(ArrayList alSecRecPay, ResultSet rs)throws YssException{
        double baseCuryRate = 0;
        double portCuryRate = 0;
    	try{
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
    		
            baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                          rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();
            
    		SecPecPayBean recPay = new SecPecPayBean();
    		
            recPay.setInOutType(1);
            recPay.setCheckState(1);
            recPay.setInvestType(rs.getString("FInvestType"));
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
            recPay.setTransDate(rs.getDate("FComfDate"));
            recPay.setStrPortCode(rs.getString("FPortCode"));
            
            recPay.setInvMgrCode(analy1 ? (rs.getString("FInvMgrCode") + "") : " ");
            recPay.setBrokerCode(" ");
            recPay.setExchangeCode(" ");
            recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
            recPay.setStrCuryCode(rs.getString("FTradeCury"));
            recPay.setStrTsfTypeCode("02");//收入
            recPay.setStrSubTsfTypeCode("02TR");//基金红利收入

            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);

            recPay.setMoney(rs.getDouble("FComfMoney"));//金额为  红利转投的确认金额
            recPay.setMMoney(recPay.getMoney());
            recPay.setVMoney(recPay.getMoney());

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                    baseCuryRate, 2));
            
            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                    baseCuryRate, portCuryRate,
                    rs.getString("FTradeCury"), dDate, recPay.getStrPortCode(), 2));
            
            recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
            recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
            
            recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());
            recPay.setVPortCuryMoney(recPay.getPortCuryMoney());
            
            alSecRecPay.add(recPay);
    	}
    	catch (Exception ex) {
            throw new YssException("处理货币式基金分红转投业务出错！", ex);
        }
    }
    
    /**
     * add by zhouwei 20111222
     * bug 3334 	QDV4嘉实基金2011年12月06日01_B
     * 若货币式基金有派息有调整金额，则生成业务类型为应收 ，
     * 业务子类型为应收基金红利收入的证券应收应付数据
     * @param alSecRecPay
     * @param rs
     * @throws YssException
     */
    private void dividentSecRecPayDeal(ArrayList alSecRecPay, ResultSet rs,double money,String tsType,String tsSubType,int state)throws YssException{
        double baseCuryRate = 0;
        double portCuryRate = 0;
    	try{
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
    		
            baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                          rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();
            
    		SecPecPayBean recPay = new SecPecPayBean();
    		
            recPay.setInOutType(1);
            recPay.setCheckState(1);
            recPay.setInvestType(rs.getString("FInvestType"));
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
            recPay.setTransDate(dDate);//bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 业务日期
            recPay.setStrPortCode(rs.getString("FPortCode"));
            
            recPay.setInvMgrCode(analy1 ? (rs.getString("FInvMgrCode") + "") : " ");
            recPay.setBrokerCode(" ");
            recPay.setExchangeCode(" ");
            recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
            recPay.setStrCuryCode(rs.getString("FTradeCury"));
            recPay.setStrTsfTypeCode(tsType);//应收
            recPay.setStrSubTsfTypeCode(tsSubType);//应收基金红利收入

            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);

            recPay.setMoney(YssD.mul(money,state));//金额为  红利转投的调整金额
            recPay.setMMoney(recPay.getMoney());
            recPay.setVMoney(recPay.getMoney());

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                    baseCuryRate, 2));
            
            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                    baseCuryRate, portCuryRate,
                    rs.getString("FTradeCury"), dDate, recPay.getStrPortCode(), 2));
            
            recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
            recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
            
            recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());
            recPay.setVPortCuryMoney(recPay.getPortCuryMoney());
            
            alSecRecPay.add(recPay);
    	}
    	catch (Exception ex) {
            throw new YssException("处理货币式基金分红转投业务出错！", ex);
        }
    }
    
    /**
     * add by songjie 2011.08.30
     * 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A
     * 若货币式基金有红利到账数据，则生成业务类型为收入 ，
     * 业务子类型为基金红利收入的证券应收应付数据,
     * 若到账日期 不等于 业务日期，则生成现金应收应付数据
     * 若到账日期 等于 业务日期 等于 业务处理日期，则不生成现金应收应付数据
     * @param alRecPay
     * @param alSecRecPay
     * @param rs
     * @throws YssException
     */
    private void dividentReserveDeal(ArrayList alRecPay, ArrayList alSecRecPay, ResultSet rs)throws YssException{
        double baseCuryRate = 0;
        double portCuryRate = 0;
    	try{
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
    		
            baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                          rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();
            //若业务处理日期 等于 确认日期，则生成证券应收应付数据
           // if(YssFun.dateDiff(rs.getDate("FBargainDate"), dDate) == 0){ //bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 只产生证券应收应付
            	SecPecPayBean recPay = new SecPecPayBean();
    		
            	recPay.setInOutType(1);
            	recPay.setCheckState(1);
            	recPay.setInvestType(rs.getString("FInvestType"));
            	recPay.setRelaNum(rs.getString("FNum"));
            	recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
            	recPay.setTransDate(dDate);
            	recPay.setStrPortCode(rs.getString("FPortCode"));
            
            	recPay.setInvMgrCode(analy1 ? (rs.getString("FInvMgrCode") + "") : " ");
            	recPay.setBrokerCode(" ");
            	recPay.setExchangeCode(" ");
            	recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
            	recPay.setStrCuryCode(rs.getString("FTradeCury"));
            	recPay.setStrTsfTypeCode("02");//收入
            	recPay.setStrSubTsfTypeCode("02TR");//基金红利收入

            	recPay.setBaseCuryRate(baseCuryRate);
            	recPay.setPortCuryRate(portCuryRate);

            	recPay.setMoney(rs.getDouble("FReturnMoney"));
            	recPay.setMMoney(recPay.getMoney());
            	recPay.setVMoney(recPay.getMoney());

            	recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
            			baseCuryRate, 2));
            
            	recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
            			baseCuryRate, portCuryRate,
                    	rs.getString("FTradeCury"), dDate, recPay.getStrPortCode(), 2));
            
            	recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
            	recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
            
            	recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());
            	recPay.setVPortCuryMoney(recPay.getPortCuryMoney());
            
            	alSecRecPay.add(recPay);
           // }
            //bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 分红到账的货币式基金，不产生现金应收应付 start---------------
            //若 业务日期  等于 红利到账日期 等于 业务处理的业务日期，则 不用生成现金应收应付数据
//            if(YssFun.dateDiff(rs.getDate("FReturnDate"), rs.getDate("FBargainDate")) == 0 &&
//               YssFun.dateDiff(rs.getDate("FReturnDate"), dDate) == 0){
//            	return;
//            }
//            
//            CashPecPayBean cashRecPay = new CashPecPayBean();
//            
//            cashRecPay.setRelaNum(rs.getString("FNum"));
//            cashRecPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
//            cashRecPay.setTradeDate(dDate);
//            cashRecPay.setPortCode(rs.getString("FPortCode"));
//            cashRecPay.setInvestManagerCode(analy1 ? (rs.getString("FInvMgrCode") + "") : " ");
//            cashRecPay.setBrokerCode(" ");
//            cashRecPay.setCuryCode(rs.getString("FTradeCury"));
//            cashRecPay.setDataSource(0);
//            cashRecPay.setStockInd(1);
//            cashRecPay.checkStateId = 1;   
//            cashRecPay.setInOutType(1);
//            cashRecPay.setCashAccCode(rs.getString("FRtnCashAccCode"));
//            
//            cashRecPay.setBaseCuryRate(baseCuryRate);
//            cashRecPay.setPortCuryRate(portCuryRate);
//            
//            cashRecPay.setMoney(rs.getDouble("FReturnMoney"));  
//            cashRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(cashRecPay.getMoney(),
//                baseCuryRate, 2));
//            cashRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(cashRecPay.getMoney(),
//                baseCuryRate, portCuryRate,
//                rs.getString("FTradeCury"), dDate, cashRecPay.getPortCode(), 2));
//            
//            
//            //若红利到账日期 大于 业务日期 ，且 业务日期  等于 业务处理的日期，则生成现金应收应付数据（ 应收基金红利）
//            if(YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FReturnDate")) > 0 &&
//               YssFun.dateDiff(rs.getDate("FBargainDate"), dDate) == 0){
//                cashRecPay.setTsfTypeCode("06"); //应收款项
//                cashRecPay.setSubTsfTypeCode("06TR");//应收基金红利
//            }
//            
//            //若红利到账日期 大于 业务日期 ，且  红利到账日期  等于 业务处理的业务日期，则生成现金应收应付数据（ 基金红利收入）
//            if(YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FReturnDate")) > 0 &&
//               YssFun.dateDiff(rs.getDate("FReturnDate"), dDate) == 0){
//                cashRecPay.setTsfTypeCode("02"); //收入
//                cashRecPay.setSubTsfTypeCode("02TR");//基金红利收入
//            }
//            
//            alRecPay.add(cashRecPay);
       //bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 分红到账的货币式基金，不产生现金应收应付 end---------------
    	}catch(Exception ex){
    		throw new YssException("处理货币式基金红利到账业务出错！", ex);
    	}
    }
    
    /**
     * 基金转换业务中计算转出的成本和估值增值
     * @param alIntergrated ArrayList：装载证券变动数据
     * @param rs ResultSet
     * @throws YssException
     */
    private void calSwitchToData(ArrayList alIntergrated, ArrayList alSecRecPay, ResultSet rs) throws YssException {
        ResultSet rsSub = null;
        StringBuffer bufSql = new StringBuffer();
        SecIntegratedBean inteCost = null;
        SecPecPayBean recPay = null;
        try {
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean("avgcostcalculate");
            costCal.setYssPub(pub);

            //查询库存，得到转出基金的应收应付和成本余额
            bufSql.append(" SELECT a.*, FTSFTYPECODE, FSUBTSFTYPECODE, FBAL, FMBAL, FVBAL, FBASECURYBAL, ");
            bufSql.append(" FMBASECURYBAL, FVBASECURYBAL, FPORTCURYBAL, FMPORTCURYBAL, ");
            bufSql.append(" FVPORTCURYBAL ");
            bufSql.append(" FROM (SELECT * ");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_Security"));
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FSecurityCode = " + dbl.sqlString(rs.getString("FSecurityCode")));
            bufSql.append(" AND FPortCode = " + dbl.sqlString(rs.getString("FPortCode")));
            bufSql.append(" AND " + operSql.sqlStoragEve(dDate) + ") a ");
            bufSql.append(" LEFT JOIN (SELECT * ");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Stock_Secrecpay"));
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FSubTsfTypeCode = '09TR') b ON a.fsecuritycode = ");
            bufSql.append(" b.FSecurityCode ");
            bufSql.append(" AND a.fstoragedate = ");
            bufSql.append(" b.fstoragedate ");
            bufSql.append(" AND a.FPortCode = ");
            bufSql.append(" b.FPortCode ");
            if (analy1) {
                bufSql.append(" AND a.FAnalysisCode1 = ");
                bufSql.append(" b.FAnalysisCode1 ");
            }
            if (analy2) {
                bufSql.append(" AND a.FAnalysisCode2 = ");
                bufSql.append(" b.FAnalysisCode2 ");
            }
            if (analy3) {
                bufSql.append(" AND a.FAnalysisCode3 = ");
                bufSql.append(" b.FAnalysisCode3 ");
            }
            rsSub = dbl.queryByPreparedStatement(bufSql.toString());
            while (rsSub.next()) {
                //-------------------转出成本
                inteCost = new SecIntegratedBean();
                inteCost.setInvestType(rs.getString("FInvestType"));
                inteCost.setIInOutType(-1);
                inteCost.setSSecurityCode(rs.getString("FSecurityCode"));
                inteCost.setSExchangeDate(YssFun.formatDate(rs.getDate("FComfDate"),
                    "yyyy-MM-dd"));
                inteCost.setSOperDate(YssFun.formatDate(rs.getDate("FComfDate"),
                    "yyyy-MM-dd"));
                inteCost.setSRelaNum(" ");
                inteCost.setSNumType(" ");

                inteCost.setSTradeTypeCode(rs.getString("FTradeTypeCode"));

                inteCost.setSPortCode(rs.getString("FPortCode"));
                if (analy1) {
                    inteCost.setSAnalysisCode1(rsSub.getString("FAnalysisCode1"));
                } else {
                    inteCost.setSAnalysisCode1(" ");
                }
                if (analy2) {
                    inteCost.setSAnalysisCode2(rsSub.getString("FAnalysisCode2"));
                } else {
                    inteCost.setSAnalysisCode2(" ");
                }
                inteCost.setSAnalysisCode3(" ");

                inteCost.setDAmount(YssD.mul(rs.getDouble("FComfAmount"), -1));

                inteCost.setDBaseCuryRate(rsSub.getDouble("FBaseCuryRate"));
                inteCost.setDPortCuryRate(rsSub.getDouble("FPortCuryRate"));

                costCal.initCostCalcutate(rs.getDate("FComfDate"),
                                          rs.getString("FPortCode"),
                                          rsSub.getString("FAnalysisCode1"),
                                          "",
                                          "");
                YssCost cost = costCal.getCarryCost(
                    rs.getString("FSecurityCode"),
                    rs.getDouble("FComfAmount"),
                    rs.getString("FNum"), null,"openfund", "35,16"); //null参数：add by guolongchao 20110815  STORY #1207  添加结算日期参数

                costCal.roundCost(cost, 2);
                inteCost.setDCost(YssD.mul(cost.getCost(), -1));
                inteCost.setDBaseCost(YssD.mul(cost.getBaseCost(), -1));
                inteCost.setDPortCost(YssD.mul(cost.getPortCost(), -1));

                inteCost.setDMCost(inteCost.getDCost());
                inteCost.setDVCost(inteCost.getDCost());

                inteCost.setDMBaseCost(inteCost.getDBaseCost());
                inteCost.setDVBaseCost(inteCost.getDBaseCost());

                inteCost.setDMPortCost(inteCost.getDPortCost());
                inteCost.setDVPortCost(inteCost.getDPortCost());

                inteCost.checkStateId = 1;
                inteCost.setSTsfTypeCode("05");
                inteCost.setSSubTsfTypeCode("05TR");

                alIntergrated.add(inteCost);
                //-------------------------转出股指增值
                //判断是否有股指增值余额
                if (rsSub.getString("FTSFTYPECODE") == null) {
                    continue;
                }
                recPay = new SecPecPayBean();
                recPay.setCheckState(1);
                recPay.setInOutType(-1);
                recPay.setInvestType(rs.getString("FInvestType"));
                recPay.setRelaNum(rs.getString("FNum"));
                recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
                recPay.setTransDate(dDate);
                recPay.setStrPortCode(rs.getString("FPortCode"));
                recPay.setInvMgrCode(analy1 ? (rsSub.getString("FAnalysisCode1") + "") :
                                     " ");
                recPay.setBrokerCode(analy2 ? (rsSub.getString("FAnalysisCode2") + "") :
                                     " ");
                recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                recPay.setStrCuryCode(rsSub.getString("FCuryCode"));
                recPay.setStrTsfTypeCode("09");
                recPay.setStrSubTsfTypeCode("09TR");

                recPay.setBaseCuryRate(rsSub.getDouble("FBaseCuryRate"));
                recPay.setPortCuryRate(rsSub.getDouble("FPortCuryRate"));

                SecPecPayBean Pay = costCal.getCarryRecPay(
                    rs.getString("FSecurityCode"),
                    rs.getDouble("FComfAmount"),
                    rs.getString("FNum"), "openfund", "35,16", "09", "09TR");

                recPay.setMoney(Pay.getMoney());
                recPay.setBaseCuryMoney(Pay.getBaseCuryMoney());
                recPay.setPortCuryMoney(Pay.getPortCuryMoney());

                recPay.setMMoney(recPay.getMoney());
                recPay.setVMoney(recPay.getMoney());

                recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
                recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());

                recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
                recPay.setVPortCuryMoney(recPay.getPortCuryMoney());
                alSecRecPay.add(recPay);
            }
        } catch (Exception ex) {
            throw new YssException("基金转换计算转出基金的成本和应收应付出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rsSub);
        }
    }

    /**
     * 处理退回业务
     * 退回时直接产生退回金额和费用资金调拨，
     * 如果是申购认购业务还要产生应收应负
     * @param alCashTrans ArrayList：装载资金调拨
     * @param alRecPay ArrayList：装载应收应付
     * @throws YssException
     */
	 //edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A 添加参数 ArrayList alSecRecPay 
    private void returnDeal(ArrayList alCashTrans,
                            ArrayList alRecPay,
                            ArrayList alSecRecPay,
                            ResultSet rs) throws YssException {
        double baseCuryRate = 0;
        double portCuryRate = 0;
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList alSubTrans = new ArrayList();
        String sTradeTypeCode = "";
		//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
		//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
        try {
            sTradeTypeCode = rs.getString("FTradeTypeCode");

            //---add by songjie 2011.07.22 BUG 2105 QDV4赢时胜（深圳）2011年6月15日01_B start---//
            //业务处理时，不处理货币式基金的分红到账业务，货币式基金的分红到账业务应在收益支付界面处理
//---delete by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
//            if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) &&
//               rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
//            	return;
//            }
//---delete by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            //---add by songjie 2011.07.22 BUG 2105 QDV4赢时胜（深圳）2011年6月15日01_B end---//
            
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FReturnDate"),
                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();
//bug 3612 edit by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 分红到账产生资金调拨 start----------------------
            //-----回退的资金调拨-------//
			//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
//            if(!(sTradeTypeCode.equals("06") && rs.getString("FSubCatCode").equals("TR03") && 
//               (YssFun.dateDiff(dDate, rs.getDate("FReturnDate")) == 0) &&
//               (YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FReturnDate")) > 0)))
//            {
//			//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
//            	transfer = new TransferBean();
//            	transfer.setCheckStateId(1);
//            
//            	//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
//            	if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) &&
//            		rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
//            		transfer.setDtTransDate(rs.getDate("FBargainDate"));
//            	}else{
//            		transfer.setDtTransDate(rs.getDate("FReturnDate"));
//            	}
//            	//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
//            	
//            	transfer.setDtTransferDate(rs.getDate("FReturnDate"));
//            	transfer.setStrPortCode(rs.getString("FPortCode"));
//            	transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
//                
//            	if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)){
//				    //---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
//            		if(rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
//            			transfer.setStrSubTsfTypeCode("02TR");
//            			transfer.setStrTsfTypeCode("02");
//            		}else{
////            			transfer.setStrSubTsfTypeCode("06DV_TR");
////            			transfer.setStrTsfTypeCode("06");
//            			transfer.setStrSubTsfTypeCode("02DV");//story 1574 add by zhouwei 20111109 修改调拨类型为02.02DV
//            			transfer.setStrTsfTypeCode("02");
//            		}
//					//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
//				} else {
//					transfer.setStrSubTsfTypeCode("05TR");
//					transfer.setStrTsfTypeCode("05");
//				}
//            	transfer.setStrTradeNum(rs.getString("FNum"));
//            	transfer.setFRelaNum(rs.getString("FNum"));
//            	transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
//            	//记录下关联编号
//            	this.sCashTradeNum += (transfer.getFRelaNum() + ",");
//            	
//            	transferset = new TransferSetBean();
//            	transferset.setDBaseRate(baseCuryRate);
//            	transferset.setDPortRate(portCuryRate);
//            	transferset.setDMoney(rs.getDouble("FReturnMoney"));
//            	transferset.setIInOut(1);
//                //edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A
//            	transferset.setSAnalysisCode1(this.analy1 ? rs.getString("FInvMgrCode") : " ");
//            	transferset.setSAnalysisCode2(" ");
//            	transferset.setSAnalysisCode3(" ");
//            	transferset.setSCashAccCode(rs.getString("FRtnCashAccCode"));
//            	transferset.setSPortCode(rs.getString("FPortCode"));
//            	alSubTrans.add(transferset);
//            	transfer.setSubTrans(alSubTrans);
//            	alCashTrans.add(transfer);
//                //---add by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
////            	if((YssFun.dateDiff(transfer.getDtTransDate(), dDate) != 0) ||
////            			(YssFun.dateDiff(transfer.getDtTransferDate(), dDate) != 0)){
////            		conn.setAutoCommit(false);
////            		bTrans = true;
////            		
////            		CashTransAdmin cashAdmin = new CashTransAdmin();
////            		cashAdmin.setYssPub(pub);
////            		cashAdmin.delete("", transfer.getDtTransferDate(), transfer.getDtTransDate(), 
////            				transfer.getStrTsfTypeCode(), transfer.getStrSubTsfTypeCode(),
////            				"", "", "",  "", "", YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND, 
////            				transfer.getStrSecurityCode(), 0, transferset.getSCashAccCode(), 
////            				transfer.getStrPortCode(), 0, "", "", "", transfer.getFRelaNum());
////            	
////            		conn.commit();
////            		bTrans = false;
////            		conn.setAutoCommit(true);
////            	}  
//            }
//bug 3612 edit by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 分红到账产生资金调拨 end---------------------------------
			//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            	transfer = new TransferBean();
            	transfer.setCheckStateId(1);           	
            	if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) &&
            		rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
            		transfer.setDtTransDate(rs.getDate("FReturnDate"));//bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B  到账日期作为调拨日期
            	}else{
            		transfer.setDtTransDate(rs.getDate("FReturnDate"));
            	}           	
            	transfer.setDtTransferDate(rs.getDate("FReturnDate"));
            	transfer.setStrPortCode(rs.getString("FPortCode"));
            	transfer.setStrSecurityCode(rs.getString("FSecurityCode"));               
            	if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)){				    
            		if(rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
            			transfer.setStrSubTsfTypeCode("02TR");
            			transfer.setStrTsfTypeCode("02");
            		}else{
//            			transfer.setStrSubTsfTypeCode("06DV_TR");
//            			transfer.setStrTsfTypeCode("06");
            			transfer.setStrSubTsfTypeCode("02DV");//story 1574 add by zhouwei 20111109 修改调拨类型为02.02DV
            			transfer.setStrTsfTypeCode("02");
            		}
					//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
				} else {
					transfer.setStrSubTsfTypeCode("05TR");
					transfer.setStrTsfTypeCode("05");
				}
            	transfer.setStrTradeNum(rs.getString("FNum"));
            	transfer.setFRelaNum(rs.getString("FNum"));
            	transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
            	//记录下关联编号
            	this.sCashTradeNum += (transfer.getFRelaNum() + ",");
            	
            	transferset = new TransferSetBean();
            	transferset.setDBaseRate(baseCuryRate);
            	transferset.setDPortRate(portCuryRate);
            	transferset.setDMoney(rs.getDouble("FReturnMoney"));
            	transferset.setIInOut(1);
                //edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A
            	transferset.setSAnalysisCode1(this.analy1 ? rs.getString("FInvMgrCode") : " ");
            	transferset.setSAnalysisCode2(" ");
            	transferset.setSAnalysisCode3(" ");
            	transferset.setSCashAccCode(rs.getString("FRtnCashAccCode"));
            	transferset.setSPortCode(rs.getString("FPortCode"));
            	alSubTrans.add(transferset);
            	transfer.setSubTrans(alSubTrans);
            	alCashTrans.add(transfer);
            
			//---add by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_RG) ||
                sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SGou) ||
                sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) ||
                sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SH)) {//edit by yanghaiming 20100705 MS01234 QDV4赢时胜上海2010年5月31日01_A
            	//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
            	if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)){
                	if(rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
                		dividentReserveDeal(alRecPay, alSecRecPay, rs);
                		//bug 3334 zhouwei 20111222 分红转投调整金额大于0，生成证券应收应付  QDV4嘉实基金2011年12月06日01_B
                		//bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 货币基金不为0的数据，产生证券应收应付
            			if(rs.getDouble("FRETURNDIFMONEY")!=0){  
//            				addCashPayByOpenTrade(alRecPay, rs, "06", "06TR", 1, -rs.getDouble("FRETURNDIFMONEY"),
//                					rs.getString("FRtnCashAccCode"));
//            				addCashPayByOpenTrade(alRecPay, rs, "98", "9802DV", 1, YssFun.toDouble(rs.getString("FRETURNDIFMONEY")),
//                					rs.getString("FRtnCashAccCode"));            				
            				dividentSecRecPayDeal(alSecRecPay,rs,rs.getDouble("FRETURNDIFMONEY"),YssOperCons.YSS_ZJDBLX_Rec,YssOperCons.YSS_ZJDBZLX_TR_RecFundIns,-1);
            				dividentSecRecPayDeal(alSecRecPay,rs,rs.getDouble("FRETURNDIFMONEY"),"98", "9802DV",1);
            				
            			}
            			////bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 货币基金不为0的数据，产生证券应收应付
            			//---------------------end------
                	}else{
                		//fundBuyDeal(alRecPay, rs);
                		//story 1574 add by zhouwei 20111109 场外的非货币基金类型的基金分红，
                        //在分红转投（现金），业务日期等于到账日期,产生02的现金应收应付，如果有调整金额，则产生98和06（负值）的两笔现金应收应付
                		if(rs.getString("freturnmoney")!=null && rs.getString("freturnmoney").length()>0){//产生02DV的现金应收应付
                			if(YssFun.toDouble(rs.getString("freturnmoney"))!=0){//bug 3612 edit by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 不为0
                    			addCashPayByOpenTrade(alRecPay, rs, "02", "02DV", 1, YssFun.toDouble(rs.getString("freturnmoney")),
                    					rs.getString("FLkCashAcccode"));
                			}
                		}
                		if(rs.getString("freturndifmoney")!=null && rs.getString("freturndifmoney").length()>0){//产生9802DV和06DV的现金应收应付
                			if(YssFun.toDouble(rs.getString("freturndifmoney"))!=0){//bug 3612 edit by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 不为0
                    			addCashPayByOpenTrade(alRecPay, rs, "98", "9802DV", 1, YssFun.toDouble(rs.getString("freturndifmoney")),
                    					rs.getString("FLkCashAcccode"));
                    			addCashPayByOpenTrade(alRecPay, rs, "06", "06DV", 1, -YssFun.toDouble(rs.getString("freturndifmoney")),
                    					rs.getString("FLkCashAcccode"));
                			}
                		}          	
                        //story 1574 end 
                	}
                }else{
                	fundBuyDeal(alRecPay, rs);
                }
            	//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            }
        } catch (Exception ex) {
            throw new YssException("处理退回业务出错！", ex);
        } 
		//---add by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
		finally{
        	dbl.endTransFinal(conn, bTrans);
        }
		//---add by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
    }

    /**
     * 处理确认数据
     * 确认是产生证券变动，
     * 如果是申购认购业务和基金转换业务还需要产生应收应付，有单独的处理
     * @param alIntegrated ArrayList：装载证券变动数据
     * @param alCashRecPay ArrayList：装载应收应付数据
     * @param alSecRecPay ArrayList:证券应收应付
     * @param rs ResultSet
     * @throws YssException
     */
    private void confirmDeal(ArrayList alIntegrated,
                             ArrayList alCashRecPay,
                             ArrayList alSecRecPay,
                             ResultSet rs
                            ) throws YssException {
        SecIntegratedBean secIntegrate = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        String sTradeTypeCode = "";
        try {
            sTradeTypeCode = rs.getString("FTradeTypeCode");
            //story 1574 add by zhouwei 20111109 场外的非货币基金类型的基金分红，
            //在分红转投（转投），业务日期等于交割日期,产生39的交易数据和02的现金应收应付，如果有调整金额，则产生98和06（负值）的两笔现金应收应付
            if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) &&
                    !rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
        		if(rs.getString("fcomfmoney")!=null && rs.getString("fcomfmoney").length()>0){//产生02DV的现金应收应付
        			if(YssFun.toDouble(rs.getString("fcomfmoney"))!=0){//bug 3612 edit by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 不为0
            			addCashPayByOpenTrade(alCashRecPay, rs, "02", "02DV", 1, YssFun.toDouble(rs.getString("fcomfmoney")),
            					rs.getString("FLkCashAcccode"));
        			}
        		}
        		if(rs.getString("fcomdifmoney")!=null && rs.getString("fcomdifmoney").length()>0){//产生9802DV和06DV的现金应收应付
        			if(YssFun.toDouble(rs.getString("fcomdifmoney"))!=0){//bug 3612 edit by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 不为0
            			addCashPayByOpenTrade(alCashRecPay, rs, "98", "9802DV", 1, YssFun.toDouble(rs.getString("fcomdifmoney")),
            					rs.getString("FLkCashAcccode"));
            			addCashPayByOpenTrade(alCashRecPay, rs, "06", "06DV", 1, -YssFun.toDouble(rs.getString("fcomdifmoney")),
            					rs.getString("FLkCashAcccode"));
        			}
        		}
        		return;
        	}
            //story 1574 end 
//---delete by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
//            if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) &&
//                rs.getString("FSubCatCode").equalsIgnoreCase("TR03")) {
//                return;
//            }
//---delete by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean("avgcostcalculate");

            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);

            baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FComfDate"),
                rs.getString("FTradeCury"), rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"), rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();

            secIntegrate = new SecIntegratedBean();

            if(sTradeTypeCode.equalsIgnoreCase("16")){
                secIntegrate.setIInOutType(-1);
            }else{
                secIntegrate.setIInOutType(1);
            }
            secIntegrate.setInvestType(rs.getString("FInvestType"));
            secIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate("FComfDate"), "yyyy-MM-dd"));
            secIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FComfDate"), "yyyy-MM-dd"));
            secIntegrate.setSRelaNum(rs.getString("FNum"));
            secIntegrate.setSNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
            //---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
            if(sTradeTypeCode.equals("06")){
            	if(rs.getString("FSubCatCode").equals("TR03")){
            		secIntegrate.setSTradeTypeCode("39");//业务类型为 分红转投
            	}else{
            		secIntegrate.setSTradeTypeCode(sTradeTypeCode);
            	}
            }else{
            	secIntegrate.setSTradeTypeCode(sTradeTypeCode);
            }
			//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            secIntegrate.setSPortCode(rs.getString("FPortCode"));
            if (this.analy1) {
                secIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));
            } else {
                secIntegrate.setSAnalysisCode1(" ");
            }
            secIntegrate.setSAnalysisCode2(" ");
            secIntegrate.setSAnalysisCode3(" ");

            if (sTradeTypeCode.equalsIgnoreCase("35")) {
                //基金转换取转入数量
                secIntegrate.setDAmount(YssD.mul(rs.getDouble("FSwitchAmount"), secIntegrate.getIInOutType()));
                secIntegrate.setSSecurityCode(rs.getString("FTsfFunds"));
            } else {
                secIntegrate.setDAmount(YssD.mul(rs.getDouble("FComfAmount"), secIntegrate.getIInOutType()));
                secIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));
            }
            if(sTradeTypeCode.equalsIgnoreCase("16")){
                costCal.initCostCalcutate(rs.getDate("FComfDate"),
                                          rs.getString("FPortCode"),
                                          rs.getString("FInvMgrCode"),
                                          "",
                                          " ");
                costCal.setYssPub(pub);
                YssCost cost = costCal.getCarryCost(rs.getString("FSecurityCode"),
                    rs.getDouble("FComfAmount"),
                    rs.getString("FNum"),
                    null,
                    "openfund",
                    "16");
                costCal.roundCost(cost, 2);
                secIntegrate.setDCost(YssD.mul(cost.getCost(),  secIntegrate.getIInOutType()));
                secIntegrate.setDMCost(YssD.mul(cost.getCost(),  secIntegrate.getIInOutType()));
                secIntegrate.setDVCost(YssD.mul(cost.getCost(),  secIntegrate.getIInOutType()));

                secIntegrate.setDBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));
                secIntegrate.setDMBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));
                secIntegrate.setDVBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));

                secIntegrate.setDPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));
                secIntegrate.setDMPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));
                secIntegrate.setDVPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));
                //---delete by songjie 2011.10.11 BUG 2843 QDV4赢时胜（深圳_Roy）2011年9月22日02_B start---//
//                //---add by songjie 2011.06.20 BUG 2088 QDV4赢时胜（深圳）2011年6月13日01_B---//
//                SecPecPayBean secRecPay = getSecRecPayCost(rs.getString("FSecurityCode"),rs.getString("FPortCode")," ",rs.getString("FInvMgrCode"));
//                if(secRecPay != null) {
//                	secRecPay.setBaseCuryRate(baseCuryRate);
//                	secRecPay.setPortCuryRate(portCuryRate);
//                	alSecRecPay.add(secRecPay);
//                };
//                //---add by songjie 2011.06.20 BUG 2088 QDV4赢时胜（深圳）2011年6月13日01_B---//
                //---delete by songjie 2011.10.11 BUG 2843 QDV4赢时胜（深圳_Roy）2011年9月22日02_B end---//
            }
            else{
                if (sTradeTypeCode.equalsIgnoreCase("35")) {
                    //转出基金的成本 = 确认金额 - 费用
                    secIntegrate.setDCost(
                        YssD.mul(
                            YssD.sub(
                                rs.getDouble("FComfMoney"),
                                rs.getDouble("FComfFee")),
                            secIntegrate.getIInOutType()));
                    secIntegrate.setDMCost(secIntegrate.getDCost());
                    secIntegrate.setDVCost(secIntegrate.getDCost());
                }
                else{
                    secIntegrate.setDCost(YssD.mul(rs.getDouble("FComfMoney"), secIntegrate.getIInOutType()));
                    secIntegrate.setDMCost(secIntegrate.getDCost());
                    secIntegrate.setDVCost(secIntegrate.getDCost());
                }

                secIntegrate.setDBaseCost(this.getSettingOper().calBaseMoney(secIntegrate.getDCost(),
                    baseCuryRate, 2));
                secIntegrate.setDMBaseCost(secIntegrate.getDBaseCost());
                secIntegrate.setDVBaseCost(secIntegrate.getDBaseCost());

                secIntegrate.setDPortCost(this.getSettingOper().calPortMoney(secIntegrate.getDCost(),
                    baseCuryRate, portCuryRate,
                    rs.getString("FTradeCury"), dDate, secIntegrate.getSPortCode(), 2));
                secIntegrate.setDMPortCost(secIntegrate.getDPortCost());
                secIntegrate.setDVPortCost(secIntegrate.getDPortCost());
            }
            secIntegrate.setDPortCuryRate(portCuryRate);

            secIntegrate.setDBaseCuryRate(baseCuryRate);

            secIntegrate.checkStateId = 1;
            secIntegrate.setSTsfTypeCode("05");
            secIntegrate.setSSubTsfTypeCode("05TR");
            alIntegrated.add(secIntegrate);


            if (sTradeTypeCode.equalsIgnoreCase("35")) {
                calSwitchToData(alIntegrated, alSecRecPay, rs);
            } else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_RG) ||
                       sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SGou) ||
                       sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) ||
                       sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SH)) {//edit by yanghaiming 20100705 MS01234 QDV4赢时胜上海2010年5月31日01_A
                //---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
            	//如果为货币式基金的分红转投数据，则不生成现金应收应付数据
            	if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)){
            		if(rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
            			dividentReInvestDeal(alSecRecPay, rs);
            			//bug 3334 zhouwei 20111222 分红转投调整金额大于0，生成证券应收应付  QDV4嘉实基金2011年12月06日01_B
            			if(rs.getDouble("fcomdifmoney")!=0){//bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 不为0
            				dividentSecRecPayDeal(alSecRecPay,rs,rs.getDouble("fcomdifmoney"),YssOperCons.YSS_ZJDBLX_Rec,YssOperCons.YSS_ZJDBZLX_TR_RecFundIns,-1);
            				dividentSecRecPayDeal(alSecRecPay,rs,rs.getDouble("fcomdifmoney"),"98", "9802DV",1);
            			}
            			//---------------------end------
            		}else{
            			fundBuyDeal(alCashRecPay, rs);
            		}
                }else{
                	fundBuyDeal(alCashRecPay, rs);
                }
            	//---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            }
        } catch (Exception ex) {
            throw new YssException("处理确认数据出错！", ex);
        }
    }

    /**
     * 2011-06-20 
     * add by songjie 获取昨日证券应收应付库存数据
     * BUG 2088 QDV4赢时胜（深圳）2011年6月13日01_B
     * @param sSecurityCode String：证券代码
     * @return YssCost：昨日成本
     * @throws YssException
     */
    private SecPecPayBean getSecRecPayCost(String sSecurityCode,String portCode,String attrClsCode,String invmgrCode) throws YssException {
        SecPecPayBean secRecPay = new SecPecPayBean();
        StringBuffer bufSql = new StringBuffer();
        String sInvmgrField = "";
        ResultSet rs = null;
        boolean haveInfo = false;
        try {
            sInvmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);

            bufSql.append(" select FSecurityCode, FInvestType, FCuryCode, FTsfTypeCode, FSubTsfTypeCode, sum(FBal) as FBal, ");
            bufSql.append(" sum(FMBal) as FMBal, ");
            bufSql.append(" sum(FVBal) as FVBal, ");
            bufSql.append(" sum(FBaseCuryBal) as FBaseCuryBal, ");
            bufSql.append(" sum(FMBaseCuryBal) as FMBaseCuryBal, ");
            bufSql.append(" sum(FVBaseCuryBal) as FVBaseCuryBal, ");
            bufSql.append(" sum(FPortCuryBal) as FPortCuryBal, ");
            bufSql.append(" sum(FMPortCuryBal) as FMPortCuryBal, ");
            bufSql.append(" sum(FVPortCuryBal) as FVPortCuryBal, ");
            bufSql.append(" sum(FBalF) as FBalF, ");
            bufSql.append(" sum(FPortCuryBalF) as FPortCuryBalF,");
            bufSql.append(" sum(FBaseCuryBalF) as FBaseCuryBalF ");
            bufSql.append(" from ").append(pub.yssGetTableName("Tb_Stock_SecRecPay"));
            bufSql.append(" where FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            bufSql.append(" and ").append(operSql.sqlStoragEve(dDate));
            bufSql.append(" and FPortCode = ").append(dbl.sqlString(portCode));
            if (sInvmgrField.trim().length() > 0) {
                bufSql.append(" and FAnalysisCode1 = ").append(dbl.sqlString(invmgrCode));
            }
            if (attrClsCode != null && attrClsCode.length() > 0) {
                bufSql.append(" AND FAttrClsCode = ").append(dbl.sqlString(attrClsCode));
            }
            bufSql.append(" and FCheckState = 1 ");
            bufSql.append(" and FTsfTypeCode = '06' ");
            bufSql.append(" group by FSecurityCode, FInvestType, FCuryCode, FTsfTypeCode, FSubTsfTypeCode ");

            rs = dbl.queryByPreparedStatement(bufSql.toString());
            while (rs.next()) {
            	haveInfo = true;
            	secRecPay.setStrPortCode(portCode);
            	secRecPay.setTransDate(dDate);
            	secRecPay.setCheckState(1);
            	secRecPay.setInOutType(1);
            	secRecPay.setStrSecurityCode(rs.getString("FSecurityCode"));
            	secRecPay.setInvestType(rs.getString("FInvestType"));
            	secRecPay.setInvMgrCode((sInvmgrField.trim().length() > 0)? invmgrCode : " ");
            	secRecPay.setBrokerCode(" ");
            	secRecPay.setStrCuryCode(rs.getString("FCuryCode"));
            	secRecPay.setRelaNumType("openfund");
            	secRecPay.setStrTsfTypeCode("02");
            	secRecPay.setStrSubTsfTypeCode("02" + rs.getString("FSubTsfTypeCode").substring(2));
            	secRecPay.setMoney(rs.getDouble("FBal"));
            	secRecPay.setVMoney(rs.getDouble("FVBal"));
            	secRecPay.setMMoney(rs.getDouble("FMBal"));
            	secRecPay.setBaseCuryMoney(rs.getDouble("FBaseCuryBal"));
            	secRecPay.setVBaseCuryMoney(rs.getDouble("FVBaseCuryBal"));
            	secRecPay.setMBaseCuryMoney(rs.getDouble("FMBaseCuryBal"));
            	secRecPay.setPortCuryMoney(rs.getDouble("FBaseCuryBal"));
            	secRecPay.setVPortCuryMoney(rs.getDouble("FVBaseCuryBal"));
            	secRecPay.setMPortCuryMoney(rs.getDouble("FMBaseCuryBal"));
            	secRecPay.setMoneyF(rs.getDouble("FBalF"));
            	secRecPay.setPortCuryMoneyF(rs.getDouble("FPortCuryBalF"));
            	secRecPay.setBaseCuryMoneyF(rs.getDouble("FBaseCuryBalF"));
            }
        } catch (Exception ex) {
            throw new YssException("获取昨日证券应收应付数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return haveInfo ? secRecPay : null;
    }
    
    /**
     * 申请业务处理
     * 申请时产生资金调拨，
     * 如果是认购申购业务要需要产生应收应付
     * @param alCashTrans ArrayList:装载资金调拨数据
     * @param alRecPay ArrayList：装载应收应付数据
     * @throws YssException
     */
    private void applyDeal(ArrayList alCashTrans,
                           ArrayList alCashRecPay,
                           ResultSet rs) throws YssException {
        double baseCuryRate = 0;
        double portCuryRate = 0;
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList alSubTrans = new ArrayList();
        String sTradeTypeCode = "";
        try {
            sTradeTypeCode = rs.getString("FTradeTypeCode");
            //分红在申请时不产生资金调拨
            if(!sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)){

                EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
                rateOper.setYssPub(pub);
                baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FApplyDate"),
                    rs.getString("FTradeCury"), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);

                rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                          rs.getString("FPortCode"));
                portCuryRate = rateOper.getDPortRate();

                //-----申请的资金调拨-------//
                transfer = new TransferBean();
                transfer.setCheckStateId(1);
                transfer.setDtTransDate(rs.getDate("FApplyDate"));
                transfer.setDtTransferDate(rs.getDate("FApplyDate"));
                transfer.setStrPortCode(rs.getString("FPortCode"));
                transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
                transfer.setStrSubTsfTypeCode("05TR");
                transfer.setStrTsfTypeCode("05");
                transfer.setStrTradeNum(rs.getString("FNum"));
                transfer.setFRelaNum(rs.getString("FNum"));
                transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND);
                this.sCashTradeNum += (transfer.getFRelaNum() + ",");

                transferset = new TransferSetBean();
                transferset.setDBaseRate(baseCuryRate);
                transferset.setDPortRate(portCuryRate);
                transferset.setDMoney(rs.getDouble("FApplyMoney"));
                transferset.setIInOut( -1);
                if (this.analy1) {
                    transferset.setSAnalysisCode1(rs.getString("FInvMgrCode"));
                } else {
                    transferset.setSAnalysisCode1(" ");
                }
                transferset.setSAnalysisCode2(" ");
                transferset.setSAnalysisCode3(" ");

                transferset.setSCashAccCode(rs.getString("FApplyCashAccCode"));
                transferset.setSPortCode(rs.getString("FPortCode"));
                alSubTrans.add(transferset);
                transfer.setSubTrans(alSubTrans);
                alCashTrans.add(transfer);
            }
            
            //---add by songjie 2011.07.22 BUG 2105 QDV4赢时胜（深圳）2011年6月15日01_B start---//
            //业务处理时，不处理货币式基金的分红转投业务，货币式基金的分红转投业务应在收益支付界面处理
            if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX) &&
               rs.getString("FSubCatCode").equalsIgnoreCase("TR03")){
            	return;
            }
            //---add by songjie 2011.07.22 BUG 2105 QDV4赢时胜（深圳）2011年6月15日01_B end---//
            
            if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_RG) ||
                sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SGou) ||
                sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)) {
                fundBuyDeal(alCashRecPay, rs);
            }
        } catch (Exception ex) {
            throw new YssException("处理申请业务出错！", ex);
        }
    }

    /**
     * 查询出业务日期的所有基金业务数据，
     * 并根据叫业务类型不同调用不同的处理方法
     * @param alCashTrans ArrayList
     * @param alIntegrated ArrayList
     * @param alRecPay ArrayList
     * @throws YssException
     */
    public void OpenFundDeal(ArrayList alCashTrans,
                             ArrayList alIntegrated,
                             ArrayList alCashRecPay,
                             ArrayList alSecRecPay,
                             ArrayList alSubTrade) throws YssException {//story 1574 20111109 update by zhouwei 增加交易数据
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        try {

            bufSql.append(" SELECT a.*, c.FLkCashAcccode, b.* ");
            bufSql.append(" FROM (SELECT * ");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_OpenFundTrade"));
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" and (FApplyDate = " + dbl.sqlDate(dDate));
            bufSql.append(" OR FComfDate = " + dbl.sqlDate(dDate));
            bufSql.append(" OR FReturnDate = " + dbl.sqlDate(dDate));
            //---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A start---//
            bufSql.append(" ) and FPortCode in (" + operSql.sqlCodes(this.sPortCode));
            bufSql.append(" ) ")
           //bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 货币基金不考虑业务日期与到账日期的区别  start-------
//            union select * FROM " + pub.yssGetTableName("Tb_Data_OpenFundTrade"));
//            bufSql.append(" openfund WHERE FCheckState = 1 and FBargainDate = " + dbl.sqlDate(dDate));
//            bufSql.append(" and FBargainDate < FReturnDate and FDataType = 'return' and ");
//            bufSql.append(" FTradeTypeCode = '06' and FPortCode in (" + operSql.sqlCodes(this.sPortCode));
//            bufSql.append(" ) and exists (select FSecurityCode from ");
//            bufSql.append(pub.yssGetTableName("Tb_Para_Security") + " sec where FSubCatCode = 'TR03' ");
//            bufSql.append(" and FCheckState = 1 and sec.Fsecuritycode = openfund.Fsecuritycode)")
            //bug 3612 update by zhouwei 20120208 QDV4嘉实2012年01月10日01_B 货币基金不考虑业务日期与到账日期的区别  end-------
            .append(") a ");
            //---edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A end---//
            bufSql.append(" LEFT JOIN (SELECT FNum, FApplyCashAccCode AS FLkCashAcccode");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_OpenFundTrade"));
            bufSql.append(" WHERE FCheckState = 1");
            bufSql.append(" AND FDataType = 'apply') c ON (a.FDataType = 'confirm' OR a.FDataType = 'return') AND a.FNum = c.FNum");
            bufSql.append(" JOIN (SELECT FSecurityCode, FSubCatCode, FTradeCury ");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Para_Security"));
            bufSql.append(" WHERE FCheckState = 1) b ON a.FSecurityCode = b.FSecurityCode ");

            rs=dbl.openResultSet(bufSql.toString(),ResultSet.TYPE_SCROLL_SENSITIVE);//可以双向滚动
            int index=0;
            while (rs.next()) {
            	index++;
                if (rs.getString("FDataType").equalsIgnoreCase("apply")) {               	
                    //申请业务处理
                    applyDeal(alCashTrans, alCashRecPay, rs);
                } else if (rs.getString("FDataType").equalsIgnoreCase("confirm")) {
                    //确认业务处理
                	//add by yangheng MS01711 QDV4赢时胜(测试)2010年09月03日02_B 未关联到申请数据的现金账户报错 2010.09.10
                	String sTradeTypeCode = rs.getString("FTradeTypeCode");
                	if("04".equalsIgnoreCase(sTradeTypeCode)||"06".equalsIgnoreCase(sTradeTypeCode)||"15".equalsIgnoreCase(sTradeTypeCode))
                	{
                		//edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A 
                		//货币式基金不用录入申请数据（分红信息数据），所以不用判断是否在申请数据中录入了现金账户
	                	if(rs.getString("FLkCashAcccode") == null && !rs.getString("FSubCatCode").equals("TR03")){
	                    	throw new YssException("证券代码为【"+ rs.getString("FSecurityCode")+"】" +"\n"
	                    			+ "组合代码为【" + rs.getString("FPortCode")+"】" +"\n"
	                    			+ "交易类型为【"+sTradeTypeCode+"】" +"\n"		
	                    			+ "以上描述的开放式基金业务确认数据的现金账户没有关联上，请检查是否关联上申请数据！");
	                    }
                	}
                	//===========end=============
                    confirmDeal(alIntegrated, alCashRecPay, alSecRecPay, rs);
                } else if (rs.getString("FDataType").equalsIgnoreCase("return")) {
                    //退回业务处理
					//edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A
                    returnDeal(alCashTrans, alCashRecPay, alSecRecPay, rs);
                }
            }
            //story 1574 add by zhouwei 20111109 根据分红转投来获得交易数据
            if(index>0){
                rs.first();
                getSubTradeData(rs, alSubTrade);
            }
        } catch (Exception ex) {
            throw new YssException("开放式基金业务处理出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /*story 1574 add by zhouwei 20111101
     * 处理场内的分红转投数据（交易类型为39）
     * */
    public void dealExchangeDividendSubTrade(ArrayList alCashRecPay) throws YssException{
    	ResultSet rs=null;
    	String sql=null;
    	try{
//    		//交易方式为06分发派息的处理
//    		sql="select '1' as barginSettletState,a.*,b.fcurycode from "+pub.yssGetTableName("tb_data_subtrade")
//    		   +" a,"+pub.yssGetTableName("tb_para_cashaccount")+" b"
//    		   +" where a.fcashacccode=b.fcashacccode and a.ftradetypecode='06' and a.fcheckstate=1 and b.fcheckstate=1"
//    		   +" and a.fportcode in ("+operSql.sqlCodes(this.sPortCode)+")"
//    		   +" and a.FBargainDate="+dbl.sqlDate(dDate)
//    		   +" and NVL(a.FTotalCost,0)>0";
//    		rs=dbl.openResultSet(sql);
//    		while(rs.next()){
//    			addCashPayBySubtrade(alCashRecPay, rs,1);
//    		}
//    		dbl.closeResultSetFinal(rs);
    		//交易方式为39分红转投的处理，业务日期等于交割日期
    		sql="select '1' as barginSettletState,NVL(a.FTradeMoney,0) as fmoney,a.*,b.fcurycode from "
    		   +pub.yssGetTableName("tb_data_subtrade")
    		   +" a,"+pub.yssGetTableName("tb_para_cashaccount")+" b"
    		   +" where a.fcashacccode=b.fcashacccode and a.ftradetypecode='39' and a.fcheckstate=1 and b.fcheckstate=1"
    		   +" and a.fportcode in ("+operSql.sqlCodes(this.sPortCode)+")"
    		   +" and a.FBargainDate="+dbl.sqlDate(dDate)
    		   +" and NVL(a.FTradeMoney,0)>0";
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			addCashPayBySubtrade(alCashRecPay, rs,1);
    		}
    		dbl.closeResultSetFinal(rs);
    		//交易方式为39分红转投的处理，业务日期等于结算日期 1
    		sql="select '1' as barginSettletState,NVL(a.FTotalCost,0) as fmoney,a.*,b.fcurycode from "
    		   +pub.yssGetTableName("tb_data_subtrade")
    		   +" a,"+pub.yssGetTableName("tb_para_cashaccount")+" b"
    		   +" where a.fcashacccode=b.fcashacccode and a.ftradetypecode='39' and a.fcheckstate=1 and b.fcheckstate=1"
    		   +" and a.fportcode in ("+operSql.sqlCodes(this.sPortCode)+")"
    		   +" and a.FSettleDate="+dbl.sqlDate(dDate)
 		       +" and NVL(a.FTotalCost,0)>0";
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			addCashPayBySubtrade(alCashRecPay, rs,1);
    		}
    		dbl.closeResultSetFinal(rs);
    		//交易方式为39分红转投的处理，业务日期等于结算日期   产生 调整金额和负值的应收款项用于净值统计
    		sql="select '2' as barginSettletState,NVL(a.FAccruedinterest,0) as fmoney,a.*,b.fcurycode from "
    		   +pub.yssGetTableName("tb_data_subtrade")
    		   +" a,"+pub.yssGetTableName("tb_para_cashaccount")+" b"
    		   +" where a.fcashacccode=b.fcashacccode and a.ftradetypecode='39' and a.fcheckstate=1 and b.fcheckstate=1"
    		   +" and a.fportcode in ("+operSql.sqlCodes(this.sPortCode)+")"
    		   +" and a.FSettleDate="+dbl.sqlDate(dDate)
    		   +" and NVL(a.FAccruedinterest,0)>0";
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			for(int i=1;i<=2;i++){
        			addCashPayBySubtrade(alCashRecPay, rs,i);
    			}
    		}
    		dbl.closeResultSetFinal(rs);
    	}catch (Exception e) {
			throw new YssException("场内分红转投处理出错！", e);
		} finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 将证券变动数据插入综合业务表
     * @param list ArrayList：装载证券变动数据
     * @throws YssException
     */
    private void insertData(ArrayList list) throws YssException {
        String sqlStr = "";	
        //modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        String sNewNum = "";
        SecIntegratedBean secIntegrade = null;

        dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated"));
        sqlStr = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") +
            " where " +
//			//edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A 添加 34 分红转投
//            " FTradeTypeCode in ('04','15','16','35','36','37', '06', '34')" +
//            " and FPortCode in(" + operSql.sqlCodes(this.sPortCode) + ")" +
//            " and FOperDate = " + dbl.sqlDate(dDate) +
//            //modify by zhangfa 20100909 MS01710    开放式基金业务，反复进行业务处理，会产生重复综合业务数据    QDV4赢时胜(测试)2010年09月03日01_B  
//            " AND FSUBTSFTYPECODE = '05TR' AND (FDATAORIGIN is null or FDATAORIGIN <> 'plan')";//add by yanghaiming 201007011 MS01349 QDV4国内(测试)2010年06月24日03_AB  增加FDATAORIGIN字段避免在产生综合业务时误删
            //-----------------------------------------------------------------------------------------------------------------------------
        	//edit by zhouwei 20120330 根据关联类型来进行删除
      //edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A 添加 34 分红转投
      " FNumType=" +dbl.sqlString(YssOperCons.YSS_SECRECPAY_RELATYPE_OPENFUND)+
      " and FPortCode in(" + operSql.sqlCodes(this.sPortCode) + ")" +
      " and FOperDate = " + dbl.sqlDate(dDate);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
        } catch (Exception e) {
            throw new YssException("删除综合业务出错！", e);
        }
        sqlStr = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
            " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
            " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
            " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator,FCreateTime, FTSFTYPECODE, FSUBTSFTYPECODE) " +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.getPreparedStatement(sqlStr);
        	pst = dbl.getYssPreparedStatement(sqlStr);
			//==============end================

        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        	integrateAdmin.setYssPub(pub);
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            for (int i = 0; i < list.size(); i++) {
                secIntegrade = (SecIntegratedBean) list.get(i);
                sNewNum = "E" +
                    YssFun.formatDate(this.dDate,
                                      "yyyyMMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Integrated"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000001",
                                           " where FExchangeDate=" +
                                           dbl.sqlDate(this.dDate) +
                                           " or FExchangeDate=" +
                                           dbl.sqlDate("9998-12-31"));
                pst.setString(1, sNewNum);
                //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                pst.setString(2, integrateAdmin.getKeyNum());
                pst.setInt(3, secIntegrade.getIInOutType());
                pst.setString(4, secIntegrade.getSSecurityCode());
                pst.setDate(5, YssFun.toSqlDate(secIntegrade.getSExchangeDate()));
                pst.setDate(6, YssFun.toSqlDate(secIntegrade.getSOperDate()));
                pst.setString(7, secIntegrade.getSTradeTypeCode());
                pst.setString(8, secIntegrade.getSRelaNum());
				//edit by songjie 2011.08.30 需求 1222 QDV4赢时胜（招商证券）2011年06月14日01_A
                pst.setString(9, secIntegrade.getSNumType());
                pst.setString(10, secIntegrade.getSPortCode());
                pst.setString(11, secIntegrade.getSAnalysisCode1());
                pst.setString(12, secIntegrade.getSAnalysisCode2());
                pst.setString(13, secIntegrade.getSAnalysisCode3());
                pst.setDouble(14, secIntegrade.getDAmount());
                pst.setDouble(15, secIntegrade.getDCost());
                pst.setDouble(16, secIntegrade.getDMCost());
                pst.setDouble(17, secIntegrade.getDVCost());
                pst.setDouble(18, secIntegrade.getDBaseCost());
                pst.setDouble(19, secIntegrade.getDMBaseCost());
                pst.setDouble(20, secIntegrade.getDVBaseCost());
                pst.setDouble(21, secIntegrade.getDPortCost());
                pst.setDouble(22, secIntegrade.getDMPortCost());
                pst.setDouble(23, secIntegrade.getDVPortCost());
                pst.setDouble(24, secIntegrade.getDBaseCuryRate());
                pst.setDouble(25, secIntegrade.getDPortCuryRate());
                pst.setString(26, "");
                pst.setString(27, "");
                pst.setInt(28, secIntegrade.checkStateId);
                pst.setString(29, pub.getUserCode());
                pst.setString(30, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(31, secIntegrade.getSTsfTypeCode());
                pst.setString(32, secIntegrade.getSSubTsfTypeCode());

                pst.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("插入综合业务出错！", e);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 获取货币型基金收益
     * @param sSelCodes String：选中的代码，如果为空则取所有基金
     * @return ArrayList
     * @throws YssException
     */
    public ArrayList getMonetaryFundIncome(String sSelCodes) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        StringBuffer bufSql = new StringBuffer();
        ArrayList alPay = new ArrayList();
        SecPecPayBean payPrice = null;
        String sSelWhere = "";
        double baseCuryRate = 0;
        double portCuryRate = 0;
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        String sNewNum = "";
        HashMap hmTradeFee = new HashMap();//保存月结型基金的费用
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);

            if (sSelCodes.length() > 0) {
                sSelWhere = " AND FSecurityCode IN (" + operSql.sqlCodes(sSelCodes) + ")";
            }

            sqlStr = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
                " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
                " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
                " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
                " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator,FCreateTime, FTSFTYPECODE, FSUBTSFTYPECODE, FInvestType, FDATAORIGIN) " +//add by yanghaiming 201007011 MS01349  QDV4国内(测试)2010年06月24日03_AB  增加FDATAORIGIN字段避免在产生综合业务时误删
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//            pst = conn.prepareStatement(sqlStr);
            pst = dbl.getYssPreparedStatement(sqlStr);
			//==============end================

            for (int i = 0; i < alCloseType.size(); i++) {

                //日结型基金的结转
                if (i == 0) {

                    bufSql.append(" SELECT a.* ");
                    bufSql.append(" FROM (SELECT * ");
                    bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_Secrecpay"));
                    bufSql.append(" WHERE FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_TR_RecFundIns));
                    bufSql.append(" AND FPortCode IN (" + operSql.sqlCodes( (String) alCloseType.get(i)) + ") ");
                    bufSql.append(" AND FTransDate = " + dbl.sqlDate(dDate) + ") a ");
                    bufSql.append(" JOIN (SELECT * ");
                    bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Monetaryfund"));
                    bufSql.append(" WHERE FCheckState = 1 ");
                    bufSql.append(sSelWhere);
                    bufSql.append(" AND FClosedType = " + dbl.sqlString(YssOperCons.YSS_MONETARYFUN_CLOSETYPE_DAY) + ") b ON a.Fsecuritycode = b.Fsecuritycode ");

                    rs = dbl.queryByPreparedStatement(bufSql.toString());
                    while (rs.next()) {

                        payPrice = new SecPecPayBean();
                        payPrice.setTransDate(dDate);
                        payPrice.setInvestType(rs.getString("FInvestType"));

                        payPrice.setStrSecurityCode(rs.getString("FSecurityCode"));
                        payPrice.setStrPortCode(rs.getString("FPortCode"));
                        payPrice.setInvMgrCode(rs.getString("FAnalysisCode1"));
                        payPrice.setBrokerCode(rs.getString("FAnalysisCode2"));
                        payPrice.setStrCuryCode(rs.getString("FCuryCode"));
                        payPrice.setAttrClsCode(rs.getString("FAttrClsCode"));

                        payPrice.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                        payPrice.setPortCuryRate(rs.getDouble("FPortCuryRate"));

                        payPrice.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                        payPrice.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TR_Income);
                        payPrice.checkStateId = 1;

                        payPrice.setMoney(rs.getDouble("FMoney"));
                        payPrice.setVMoney(rs.getDouble("FVMoney"));
                        payPrice.setMMoney(rs.getDouble("FMMoney"));
                        payPrice.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
                        payPrice.setMBaseCuryMoney(rs.getDouble("FMBaseCuryMoney"));
                        payPrice.setVBaseCuryMoney(rs.getDouble("FVBaseCuryMoney"));
                        payPrice.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
                        payPrice.setMPortCuryMoney(rs.getDouble("FMPortCuryMoney"));
                        payPrice.setVPortCuryMoney(rs.getDouble("FVPortCuryMoney"));
                        payPrice.setRelaNumType("");
                        alPay.add(payPrice);
                    }
                } else { //月结型基金结转
                    bufSql = new StringBuffer();
                    bufSql.append(" SELECT fund.*, c.FTradeCury FROM (");
                    bufSql.append(" SELECT a.*");
                    bufSql.append(" FROM (SELECT * ");
                    bufSql.append(" FROM " + pub.yssGetTableName("TB_Data_OpenFundTrade"));
                    bufSql.append(" WHERE FCheckState = 1 ");
                    bufSql.append(" AND FDataType = 'confirm' ");
                    bufSql.append(" AND FTradeTypeCode = '06' ");
                    bufSql.append(" AND FPortCode IN(" + operSql.sqlCodes( (String) alCloseType.get(i)) + ")");
                    bufSql.append(" AND FComfDate = " + dbl.sqlDate(dDate) + ") a ");
                    bufSql.append(" JOIN (SELECT * ");
                    bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Monetaryfund"));
                    bufSql.append(" WHERE FCheckState = 1 ");
                    bufSql.append(sSelWhere);
                    bufSql.append(" AND FClosedType = 'day') b ON a.Fsecuritycode = b.Fsecuritycode ");
                    bufSql.append(" UNION ");
                    bufSql.append(" SELECT a.*");
                    bufSql.append(" FROM (SELECT * ");
                    bufSql.append(" FROM " + pub.yssGetTableName("TB_Data_OpenFundTrade"));
                    bufSql.append(" WHERE FCheckState = 1 ");
                    bufSql.append(" AND FDataType = 'confirm' ");
                    bufSql.append(" AND FTradeTypeCode = '06' ");
                    bufSql.append(" AND FPortCode IN(" + operSql.sqlCodes(this.sPortCode) + ")");
                    bufSql.append(" AND FComfDate = " + dbl.sqlDate(dDate) + ") a ");
                    bufSql.append(" JOIN (SELECT * ");
                    bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Monetaryfund"));
                    bufSql.append(" WHERE FCheckState = 1 ");
                    bufSql.append(sSelWhere);
                    bufSql.append(" AND FClosedType = 'month') b ON a.Fsecuritycode = b.Fsecuritycode) fund ");
                    bufSql.append(" LEFT JOIN (SELECT FSecurityCode, FTradeCury FROM  ");
                    bufSql.append(pub.yssGetTableName("TB_Para_Security"));
                    bufSql.append(" WHERE FCheckState = 1 ) c ON fund.Fsecuritycode = c.Fsecuritycode");

                    rs = dbl.queryByPreparedStatement(bufSql.toString());
                    while (rs.next()) {
                        payPrice = new SecPecPayBean();
                        payPrice.setTransDate(dDate);
                        payPrice.setInvestType(rs.getString("FInvestType"));

                        payPrice.setStrSecurityCode(rs.getString("FSecurityCode"));
                        payPrice.setStrPortCode(rs.getString("FPortCode"));
                        payPrice.setInvMgrCode(rs.getString("FInvMgrCode"));

                        payPrice.setBrokerCode(" ");
                        payPrice.setStrCuryCode(rs.getString("FTradeCury"));

                        baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                            rs.getString("FTradeCury"), payPrice.getStrPortCode(),
                            YssOperCons.YSS_RATE_BASE);

                        rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                                  payPrice.getStrPortCode());
                        portCuryRate = rateOper.getDPortRate();

                        payPrice.setBaseCuryRate(baseCuryRate);
                        payPrice.setPortCuryRate(portCuryRate);

                        payPrice.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                        payPrice.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TR_Income);
                        payPrice.checkStateId = 1;

                        payPrice.setMoney(YssD.add(rs.getDouble("FComfMoney"), rs.getDouble("FComfFee")));
                        payPrice.setVMoney(payPrice.getMoney());
                        payPrice.setMMoney(payPrice.getMoney());
                        payPrice.setBaseCuryMoney(this.getSettingOper().calBaseMoney(payPrice.getMoney(),
                            baseCuryRate, 2));
                        payPrice.setMBaseCuryMoney(payPrice.getBaseCuryMoney());
                        payPrice.setVBaseCuryMoney(payPrice.getBaseCuryMoney());
                        payPrice.setPortCuryMoney(this.getSettingOper().calPortMoney(payPrice.getMoney(),
                            baseCuryRate, portCuryRate,
                            rs.getString("FTradeCury"), dDate, payPrice.getStrPortCode(), 2)
                            );
                        payPrice.setMPortCuryMoney(payPrice.getPortCuryMoney());
                        payPrice.setVPortCuryMoney(payPrice.getPortCuryMoney());
                        payPrice.setRelaNumType("");
                        alPay.add(payPrice);
                        hmTradeFee.put(payPrice.getStrSecurityCode(), new Double(rs.getDouble("FComfFee")));
                    }
                }
                //将循环里打开的游标关闭 sunkey@Modify 20090922
                dbl.closeResultSetFinal(rs);
            }

            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " where " +
                " FTradeTypeCode = '06'" +
                " and FPortCode in(" + operSql.sqlCodes(this.sPortCode) + ")" +
                " and FOperDate = " + dbl.sqlDate(dDate) +
                " AND FSUBTSFTYPECODE = '05TR'" +
                sSelWhere;
            dbl.executeSql(sqlStr);

            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated"));
            for (int i = 0; i < alPay.size(); i++) {
                payPrice = (SecPecPayBean) alPay.get(i);
                sNewNum = "E" +
                    YssFun.formatDate(this.dDate,
                                      "yyyyMMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Integrated"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000001",
                                           " where FExchangeDate=" +
                                           dbl.sqlDate(dDate) +
                                           " or FExchangeDate=" +
                                           dbl.sqlDate("9998-12-31"));
                double dbFee = hmTradeFee.get(payPrice.getStrSecurityCode()) == null ?
                    0 : ((Double)hmTradeFee.get(payPrice.getStrSecurityCode())).doubleValue();
                double dbCost = YssD.sub(payPrice.getMoney(), dbFee);
                double dbBaseCost = this.getSettingOper().calBaseMoney(dbCost,
                            payPrice.getBaseCuryRate(), 2);
                double dbPortCost = this.getSettingOper().calPortMoney(dbCost,
                            payPrice.getBaseCuryRate(), payPrice.getPortCuryRate(),
                            payPrice.getStrCuryCode(), dDate, payPrice.getStrPortCode(), 2);

                pst.setString(1, sNewNum);
                //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                pst.setString(2, integrateAdmin.getKeyNum());
                pst.setInt(3, 1);
                pst.setString(4, payPrice.getStrSecurityCode());
                pst.setDate(5, YssFun.toSqlDate(dDate));
                pst.setDate(6, YssFun.toSqlDate(dDate));
                pst.setString(7, "06");
                pst.setString(8, " "); //这里的 sRelaNum,sNumType都为' '
                pst.setString(9, " ");
                pst.setString(10, payPrice.getStrPortCode());
                pst.setString(11, payPrice.getInvMgrCode());
                pst.setString(12, " ");
                pst.setString(13, " ");
                pst.setDouble(14, dbCost);
                pst.setDouble(15, dbCost);
                pst.setDouble(16, dbCost);
                pst.setDouble(17, dbCost);
                pst.setDouble(18, dbBaseCost);
                pst.setDouble(19, dbBaseCost);
                pst.setDouble(20, dbBaseCost);
                pst.setDouble(21, dbPortCost);
                pst.setDouble(22, dbPortCost);
                pst.setDouble(23, dbPortCost);
                pst.setDouble(24, payPrice.getBaseCuryRate());
                pst.setDouble(25, payPrice.getPortCuryRate());
                pst.setString(26, "");
                pst.setString(27, "");
                pst.setInt(28, 1);
                pst.setString(29, pub.getUserCode());
                pst.setString(30, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(31, YssOperCons.YSS_ZJDBLX_Cost);
                pst.setString(32, "05TR");
                pst.setString(33, payPrice.getInvestType());
                pst.setString(34, "plan");//add by yanghaiming 201007011 MS01349 QDV4国内(测试)2010年06月24日03_AB  增加FDATAORIGIN字段避免在产生综合业务时误删
                pst.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("结转货币基金收益出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
        return alPay;
    }

    /**
     * 获取计息相关参数，并判断选中组合是否需要计息
     * @param sPortCodes String
     * @return boolean
     * @throws YssException
     */
    public boolean getCalParams(String sPortCodes) throws YssException {
        boolean bIsCalInterest = true;
        String[] arrPortCode = null;
        String sFundPorts = ""; //记入基金投资的组合代码
        String sRecPorts = ""; //记入应收红利的组合代码
        try {
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            hmParams = pubPara.getMonetaryFundIncomeCalaParams();
            arrPortCode = sPortCodes.split(",");
            alCloseType = new ArrayList();
            if (hmParams == null) {
                //默认记入基金投资
                alCloseType.add(sPortCodes);
                return true;
            }
            for (int i = 0; i < arrPortCode.length; i++) {
                //判断是否计息
                if (hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_FUN_CONTINUE) != null &&
                    ( (String) hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_FUN_CONTINUE)).equalsIgnoreCase("0")) {
                    continue;
                }
                //获取日结型基金红利结转方式
                if (hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_RATERESULT) != null &&
                    ( (String) hmParams.get(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_RATERESULT)).
                    equalsIgnoreCase(YssOperCons.YSS_MONETARYFUN_INTEREST_RECRATE)) {
                    sRecPorts += (arrPortCode[i] + ",");
                } else {
                    //默认使用记入基金投资
                    sFundPorts += (arrPortCode[i] + ",");
                }
            }

            if (sRecPorts.length() > 0) {
                sRecPorts = sRecPorts.substring(0, sRecPorts.length() - 1);
            }
            if (sFundPorts.length() > 0) {
                sFundPorts = sFundPorts.substring(0, sFundPorts.length() - 1);
            }

            alCloseType.add(sFundPorts);//记入基金投资
            alCloseType.add(sRecPorts);//记入应收红利
        } catch (Exception ex) {
            throw new YssException(ex);
        }
        return bIsCalInterest;
    }
}
