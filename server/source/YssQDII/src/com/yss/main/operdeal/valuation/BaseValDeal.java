package com.yss.main.operdeal.valuation;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;

public class BaseValDeal
    extends BaseBean {
    protected java.util.Date dDate;
    protected String portCode;
    protected String valType;

    protected String invmgrSecField;
    protected String brokerSecField;
    protected String catCashField;
    protected String invmgrCashField;
    protected String invmgrInvestField;

    protected HashMap hmValPrice;
    protected HashMap hmValRate;
    //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
    //2009.07.03 蒋锦 添加 新增债券交易类型、映射表
    protected HashMap hmBondParams;  //储存数据接口中国内债券的相关参数
    protected HashMap hmExchangeIntFace = new HashMap();  //交易所代码映射接口参数代码 Key-交易所代码；Value-接口参数中定义的代码：01、02...
    protected HashMap hmCatCodeIntFace = new HashMap();   //品种子类型代码映射接口参数代码 Key-交易所代码；Value-接口参数中定义的代码：01、02...
    protected HashMap hmIntAccPer100;  //每百元债券利息

    protected ValMktPriceBean mktPrice;

    protected String valSecCodes = "";
    //是否为 ETF 估值  MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A 20091126 蒋锦 添加
    protected boolean isETFVal = false;
    
    protected String strETFStatType = "";//根据补票方式进行现金应收应付的统计
	protected String tmpMarketValueTable=""; //估值前的行情表 太平资产并行优化

//   protected boolean bHasCalSecStorage = false; //是否已经统计过当日证券库存
//   protected boolean bHasCalCashStorage = false; //是否已经统计过当日现金库存

    public boolean getIsETFVal() {
		return isETFVal;
	}

	public void setIsETFVal(boolean isETFVal) {
		this.isETFVal = isETFVal;
	}
	
	//估值前的行情表 setting
   public void setTmpMarketValueTable(String tmpMarketValueTable){
	   this.tmpMarketValueTable = tmpMarketValueTable;
   }

	public BaseValDeal() {
    }

    /**
     * initValuation
     *
     * @param dDate Date
     * @param sPortCode String
     * @param sValTypes String
     */
    public void initValuation(Date dDate, String sPortCode, String sValTypes,
                              HashMap hmValRate, HashMap hmValPrice
        ) throws
        YssException {
        this.dDate = dDate;
        this.portCode = sPortCode;
        this.valType = sValTypes;
        this.hmValRate = hmValRate;
        this.hmValPrice = hmValPrice;
        invmgrSecField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
        brokerSecField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
        invmgrCashField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);
        catCashField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_CatType);
        invmgrInvestField = this.getSettingOper().getStorageAnalysisField(
            YssOperCons.YSS_KCLX_InvestPayRec, YssOperCons.YSS_KCPZ_InvMgr);

        //获取数据接口中债券的相关参数 2009.07.03 蒋锦 添加 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
        CNInterfaceParamAdmin interParams = new CNInterfaceParamAdmin();
        interParams.setYssPub(pub);
        hmBondParams = (HashMap)interParams.getExchangeBondBean();
        //获取每百元债券利息
        BondInterestBean bondIns = new BondInterestBean();
        bondIns.setYssPub(pub);
        //edit by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
        hmIntAccPer100 = bondIns.getAllSetting(dDate,null);

        hmExchangeIntFace.put(YssOperCons.YSS_JYSDM_SHJYS, "01");//上交易对应01
        hmExchangeIntFace.put(YssOperCons.YSS_JYSDM_SZJYS, "02");//深交所对应02

        hmCatCodeIntFace.put("FI07", "04");//可分离债对应04
        hmCatCodeIntFace.put("FI08", "05");//公司债对应05
        hmCatCodeIntFace.put("FI09", "02");//企业债对应02
    }

    /**
     * getValuationMethods
     *
     * @return ArrayList
     */
    public ArrayList getValuationMethods() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String exRateSrcCode = "";
        String exRateCode = "";
        MTVMethodBean vMethod = null;
        ArrayList alResult = new ArrayList();
        //add by songjie 2013.04.16 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
        String investType = "";
        try {
//         strSql = " select o.FBaseRateSrcCode, o.FBaseRateCode,o.FPortRateSrcCode, o.FPortRateCode from " +
//               pub.yssGetTableName("Tb_Para_Portfolio") + " o join " +
//               "(select FPortCode, max(FStartDate) as FStartDate from " +
//               pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//               " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//               " and FCheckState = 1 and FPortCode = " +
//               dbl.sqlString(portCode) + " group by FPortCode) p " +
//               " on o.FPortCode = p.FPortCode and o.FStartDate = p.FStartDate";
//         rs = dbl.openResultSet(strSql);
//
//         if (rs.next()) {
//            exRateSrcCode = rs.getString("FBaseRateSrcCode") + "";
//            exRateCode = rs.getString("FBaseRateCode") + "";
//         }
//         dbl.closeResultSetFinal(rs);

            //获取估值方法信息
        	// modify by fangjiang 2010.08.27 考虑组合的最大启用日期
            strSql = " select a.*, b.* from " +
                "(select m.* from " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                " m join (select FMTVCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_MTVMethod") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FMTVCode) n on m.FMTVCode = n.FMTVCode and m.FStartDate = n.FStartDate " +
                ") a join (select FSubCode, FPortCode, FRelaGrade from " +
                " ( select FSubCode, FPortCode, FRelaGrade, FStartDate from "+
                pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                " where FRelaType = 'MTV' and FPortCode = " +
                dbl.sqlString(portCode) +
                " and FCheckState = 1 ) m join ( select max(FStartDate) as FStartDate from "+
                pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                " where FRelaType = 'MTV' and FPortCode = " +
                dbl.sqlString(portCode) +
                " and FCheckState = 1 ) n on m.FStartDate = n.FStartDate "+
                " ) b on a.FMTVCode = b.FSubCode where a.FCheckState = 1 order by b.FRelaGrade desc";
            //-------------------------
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
                rs.beforeFirst();
                while (rs.next()) {
                	vMethod = new MTVMethodBean();
                	
                	//---add by songjie 2013.04.16 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
                	investType = rs.getString("FInvestmentType");
                	if(investType != null && investType.equals("CanCell")){
                		vMethod.setFInvestmentType("S");
                	}else if(investType != null && investType.equals("holderMaturity")){
                		vMethod.setFInvestmentType("F");
                	}else if(investType != null && investType.equals("transaction")){
                		vMethod.setFInvestmentType("C");
                	}else{
                		vMethod.setFInvestmentType(" ");
                	}
                	//---add by songjie 2013.04.16 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
                	
                    
                    vMethod.setMTVCode(rs.getString("FMTVCode") + "");
                    vMethod.setMktSrcCode(rs.getString("FMktSrcCode") + "");
                    vMethod.setMktPriceCode(rs.getString("FMktPriceCode") + "");
                    vMethod.setMTVMethod(rs.getString("FMTVMethod") + "");
                    vMethod.setBaseRateSrcCode(rs.getString("FBaseRateSrcCode") + "");
                    vMethod.setBaseRateCode(rs.getString("FBaseRateCode") + "");
                    vMethod.setPortRateSrcCode(rs.getString("FPortRateSrcCode") + "");
                    vMethod.setPortRateCode(rs.getString("FPortRateCode") + "");

                    alResult.add(vMethod);
                }
            }

            return alResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getValuationCats
     *
     * @param mtvBeans ArrayList
     * @return HashMap
     */
    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        return null;
    }

    /**
     * saveValuationCats
     *
     * @param BaseBeans HashMap
     * @return String
     */
    public String saveValuationCats(HashMap BaseBeans) throws YssException {
        CashPayRecAdmin cashpay = null;
        SecRecPayAdmin secpay = null;
        InvestPayAdimin investpay = null;
//      boolean bSecRecPay = false;
//      boolean bCashPayRec = false;

        Object bean = null;
        Object filter = null;
        Object filterCash = null;
        Object filterInvest = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {

            Iterator it = BaseBeans.keySet().iterator();
            secpay = new SecRecPayAdmin();
            secpay.setYssPub(pub);
            cashpay = new CashPayRecAdmin();
            cashpay.setYssPub(pub);
            investpay = new InvestPayAdimin();
            investpay.setYssPub(pub);
            filter = filterSecCondition();
            filterCash = filterCashCondition();
            filterInvest = filterInvestCondition();
            while (it.hasNext()) {
                String key = (String) it.next();
                bean = (Object) BaseBeans.get(key);
                if (bean instanceof SecPecPayBean) {

                    secpay.addList( (SecPecPayBean) bean);
//               bSecRecPay = true;
                } else if (bean instanceof CashPecPayBean) {

                    cashpay.addList( (CashPecPayBean) bean);
//               bCashPayRec = true;

                } else if (bean instanceof InvestPayRecBean) {
                    investpay.addList( (InvestPayRecBean) bean);
                }
            }

         if (secpay.getList().size() > 0) {
            //2009.04.27 缩小事务控制的范围 蒋锦 修改
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.setAutoCommit(false);
            bTrans = true;
            //xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持
            secpay.insert(this.dDate,
                          ( (SecPecPayBean) filter).getStrTsfTypeCode(),//调拨类型
                          ( (SecPecPayBean) filter).getStrSubTsfTypeCode(),//调拨子类型
                          this.portCode,
                          ( (SecPecPayBean) filter).getInvMgrCode(),//投资经理代码
                          ( (SecPecPayBean) filter).getBrokerCode(),//券商代码名称
                          ( (SecPecPayBean) filter).getStrSecurityCode(),//证券代码
                          "", ( (SecPecPayBean) filter).getInOutType(),-99); //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
            //-----------------------end------------------------------------//
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         }
         if (cashpay.getList().size() > 0) {
            //2009.04.27 缩小事务控制的范围 蒋锦 修改
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.setAutoCommit(false);
            bTrans = true;
            cashpay.insert(this.dDate,
                           ( (CashPecPayBean) filterCash).getTsfTypeCode(),
                           ( (CashPecPayBean) filterCash).getSubTsfTypeCode(),
                           this.portCode,
                           ( (CashPecPayBean) filterCash).getInvestManagerCode(),
                           ( (CashPecPayBean) filterCash).getCategoryCode(),
                           ( (CashPecPayBean) filterCash).getCashAccCode(),
                           ( (CashPecPayBean) filterCash).getCuryCode(), 0);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         }
         if (investpay.getList().size() > 0) {
            //2009.04.27 缩小事务控制的范围 蒋锦 修改
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.setAutoCommit(false);
            bTrans = true;
            investpay.insert(this.dDate,
                             ( (InvestPayRecBean) filterInvest).getTsftTypeCode(),
                             ( (InvestPayRecBean) filterInvest).
                             getSubTsfTypeCode(), "", this.portCode, "", 0);

                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
             
              //add by lidaolong 20110422  BUG1736在T+1日汇率发生改变时，进行第1遍资产估值的时候，不会产生相应的汇兑损益库存信息。 
		
                BaseStgStatDeal cashstgstat = (BaseStgStatDeal) pub
						.getOperDealCtx().getBean("InvestPayRec");
				cashstgstat.setYssPub(pub);
				cashstgstat.stroageStat(dDate, dDate, operSql
						.sqlCodes(portCode));
				//end by lidaolong
            }

            return "";
        } catch (Exception e) {
            throw new YssException("系统进行资产估值,在保存估值后的应收应付数据时出现异常!" + "\n", e); // by 曹丞 2009.02.01 保存估值后的应收应付数据异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            //2009.04.27 增加事务失败的回滚
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            dbl.endTransFinal(bTrans);
        }
    }

    public Object filterSecCondition() {
        return null;
    }

    public Object filterCashCondition() {
        return null;
    }

    public Object filterInvestCondition() {
        return null;
    }

    /**
     * 以估值类型来删除估值行情
     * @param sValDate String 估值日期
     * @param portCode String
     * @param valType String 估值类型
     * @throws YssException
     * MS00265 QDV4建行2009年2月23日01_B
     */
    public void delValMktType(String sValDate, String portCode, String valTypes) throws YssException {
        String strSql = "";
        try {
            if (valTypes.length() > 1) { //只有当有估值类型时,才以估值类型为筛选条件进行删除,避免当执行其他类型的估值而没有添加类型时,出现不可测的错误
                valTypes = valTypes.substring(0, valTypes.length() - 1);
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_ValMktPrice") +
                    " where FValDate = " + sValDate + " and " +
                    " FPortCode = " + dbl.sqlString(portCode) + " and " +
                    " FValType in (" + operSql.sqlCodes(valTypes) + ")";
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException("以估值类型删除估值行情出现异常！", e);
        }
    }

    public void insertValMktPrice() throws YssException {
        String strSql = "";
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        ValMktPriceBean mktPrice = null;
        Iterator iter = null;
        StringBuffer buf = new StringBuffer();
        String sSecCodes = "";
        HashMap hmDateToSec = new HashMap();
        //----------- MS00265 QDV4建行2009年2月23日01_B -----
        HashMap hmDateToType = new HashMap(); //需删除的估值类型
        String valTypes = null; //估值类型的汇总
        //--------------------------------------------------
        boolean bTrans =false;//这里直接提交数据 合并太平版本调整添加 by leeyu 20100825
        try {
        	conn.setAutoCommit(bTrans);
        	bTrans =true;
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (hmValPrice == null && (hmValPrice != null && hmValPrice.size() == 0)) {
                return;
            }
         	dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_ValMktPrice"));//添加行级锁 by leeyu 20100613 合并太平版本代码
            //----2008.07.15 蒋锦 修改 在删除老的估值行情的的时候需要判断行情代码的日期----//
            iter = hmValPrice.values().iterator();
            while (iter.hasNext()) {
                mktPrice = (ValMktPriceBean) iter.next();
                //----xuqiji 20100327 MS01059  QDV4赢时胜(测试)2010年03月27日07_B 资产估值，在综合损益处报错---//
                if(mktPrice.getValDate() == null){
                	continue;
                }
                //-------------------------------------end-------------------------------------//
                String sKey = dbl.sqlDate(mktPrice.getValDate());
                //-----------MS00265 QDV4建行2009年2月23日01_B ----------------------
                valTypes = (String) hmDateToType.get(sKey); //获取已有的估值类型
                if (valTypes != null) { //当已有估值类型
                    if (valTypes.indexOf(mktPrice.getValType()) == -1) { //当已有的估值类型和当前的估值类型不相同
                        valTypes += mktPrice.getValType() + ","; //添加估值类型
                    }
                } else { //尚无估值类型
                    hmDateToType.put(sKey, mktPrice.getValType() + ","); //以时间为key，添加初始估值类型
                }
                //------------------------------------------------------------------
                StringBuffer bufSec = (StringBuffer) hmDateToSec.get(sKey);
                if (bufSec != null) {
                    bufSec.append(mktPrice.getSecurityCode()).append(",");
                } else {
                    hmDateToSec.put(sKey, new StringBuffer(mktPrice.getSecurityCode() + ","));
                }
            }

            Iterator it = hmDateToSec.keySet().iterator();
            while (it.hasNext()) {
                String sValDate = (String) it.next();
                buf = ( (StringBuffer) hmDateToSec.get(sValDate));
                if (buf.length() > 0) {
                    buf.setLength(buf.length() - 1);
                    sSecCodes = buf.toString();
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_ValMktPrice") +
                        " where FValDate = " + sValDate + " and " +
                        " FPortCode = " + dbl.sqlString(portCode) + " and " +
                        " FSecurityCode in (" + operSql.sqlCodes(sSecCodes) + ")";
                    dbl.executeSql(strSql);
                    //=======by xuxuming,20090910.MS00683,在重做之前的凭证时，查询当天估值行情表只得到一条数据,QDV4建行2009年9月01日01_B=========
                    if (dbl.sqlDate(dDate).equalsIgnoreCase(sValDate)) { //只删除当天的行情数据
                        //-----------MS00265 QDV4建行2009年2月23日01_B ----------------------
                        if (null != valTypes) { //当有多只证券参与此种类型的估值
                            delValMktType(sValDate, portCode, valTypes); //进行再次删除,以估值类型为条件,确保删除已经删除了行情的数据
                        } else if (valTypes == null && hmDateToType.size() > 0) { //当只有一只证券参与此种类型的估值
                            delValMktType(sValDate, portCode, (String) hmDateToType.get(sValDate));
                        }
                        //------------------------------------------------------------------
                    }//end if
                    //======end==============================================================================================
                }
            }
            //-----------------------------------------//
            //2009.7.9 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A-- 估值行情表添加了属性分类，以区分同一股票公开发行和非公开发行的不同行情
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_ValMktPrice") +
                " (FValDate,FPortCode,FSecurityCode,FPrice,FOTPrice1,FOTPrice2,FOTPrice3,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FMarketStatus" +//新增 行情状态，by leeyu 2008-10-17
                //---- MS00272  QDV4赢时胜（上海）2009年2月26日01_B
                ",FMTvCode" + //添加估值方法字段
                //---------------------------------------------
                //----MS00265 QDV4建行2009年2月23日01_B --------
                ",FValType" + //添加估值类型
                //---------------------------------------------
                ",FAttrClsCode) " + //增加属性分类
                //---- MS00272  QDV4赢时胜（上海）2009年2月26日01_B
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; //添加估值方法的预处理//MS00265 QDV4建行2009年2月23日01_B 添加估值类型
            //----------------------------------------------
            pst = conn.prepareStatement(strSql);

            iter = hmValPrice.values().iterator();
            while (iter.hasNext()) {
                mktPrice = (ValMktPriceBean) iter.next();
                //------xuqiji 20100327 MS01059  QDV4赢时胜(测试)2010年03月27日07_B 资产估值，在综合损益处报错--//
                if(mktPrice.getValDate() == null){
                	continue;
                }
                //---------------------end------------------------//
                pst.setDate(1, YssFun.toSqlDate(mktPrice.getValDate()));
                pst.setString(2, portCode);
                pst.setString(3, mktPrice.getSecurityCode());
                pst.setDouble(4, mktPrice.getPrice());
                pst.setDouble(5, mktPrice.getOtPrice1());
                pst.setDouble(6, mktPrice.getOtPrice2());
                pst.setDouble(7, mktPrice.getOtPrice3());
                pst.setInt(8, 1);
                pst.setString(9, pub.getUserCode());
                pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(11, pub.getUserCode());
                pst.setString(12, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(13, mktPrice.getMarketStatus()); //新增 行情状态，by leeyu 2008-10-17
                //---- MS00272  QDV4赢时胜（上海）2009年2月26日01_B -------
                pst.setString(14, mktPrice.getMtvCode()); //设置估值方法的值
                //------------------------------------------------------
                //----MS00265 QDV4建行2009年2月23日01_B -----------------
                pst.setString(15, mktPrice.getValType()); //添加估值类型
                //-----------------------------------------------------
                pst.setString(16, (mktPrice.getAttrClsCode() == null ||
                                   mktPrice.getAttrClsCode().length() == 0 ||
                                   mktPrice.getAttrClsCode().equalsIgnoreCase("null"))? " ":mktPrice.getAttrClsCode());
                pst.executeUpdate();
            }
			//这里直接提交数据 合并太平版本调整添加 by leeyu 20100825
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans =false;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);//这里直接提交数据 合并太平版本调整添加 by leeyu 20100825
        }
    }

    public void insertValRate() throws YssException {
        String strSql = "";
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        Object obj = null;
        Iterator iter = null;
        StringBuffer buf = new StringBuffer();
        String sCuryCodes = "";
        boolean bTrans =false;//这里直接提交数据 合并太平版本调整添加 by leeyu 20100825
        ResultSet rs = null; //add by huangqirong 2013-03-05 bug #7255
        try {
        	conn.setAutoCommit(bTrans);
        	bTrans= true;
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (hmValRate == null && (hmValRate != null && hmValRate.size() == 0)) {
                return;
            }
         	dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_ValRate"));//添加行级锁byleeyu 20100613 合并太平版本代码
            iter = hmValRate.values().iterator();
            while (iter.hasNext()) {
                obj = iter.next();
                if (obj instanceof SecPecPayBean) {
                    buf.append( ( (SecPecPayBean) obj).getStrCuryCode()).append(",");
                } else if (obj instanceof CashPecPayBean) {
                    buf.append( ( (CashPecPayBean) obj).getCuryCode()).append(",");
                } else if (obj instanceof InvestPayRecBean) {
                    buf.append( ( (InvestPayRecBean) obj).getCuryCode()).append(",");
                }
            }
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }
            sCuryCodes = buf.toString();

            strSql = "delete from " + pub.yssGetTableName("Tb_Data_ValRate") +
                " where FValDate = " + dbl.sqlDate(dDate) + " and " +
                " FPortCode = " + dbl.sqlString(portCode); //+ " and " +  //modify by huangqirong 2013-03-05 bug #7255
                //" FCuryCode in (" + operSql.sqlCodes(sCuryCodes) + ")"; //modify by huangqirong 2013-03-05 bug #7255
            dbl.executeSql(strSql);

            strSql = "insert into " + pub.yssGetTableName("Tb_Data_ValRate") +
                " (FValDate,FPortCode,FCuryCode,FBaseRate,FPortRate,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) " +
                " values(?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            iter = hmValRate.values().iterator();
            while (iter.hasNext()) {
                obj = iter.next();
                pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, portCode);
                if (obj instanceof SecPecPayBean) {
                    pst.setString(3, ( (SecPecPayBean) obj).getStrCuryCode());
                } else if (obj instanceof CashPecPayBean) {
                    pst.setString(3, ( (CashPecPayBean) obj).getCuryCode());
                } else if (obj instanceof InvestPayRecBean) {
                    pst.setString(3, ( (InvestPayRecBean) obj).getCuryCode());
                }
                if (obj instanceof SecPecPayBean) {
                    pst.setDouble(4, ( (SecPecPayBean) obj).getBaseCuryRate());
                } else if (obj instanceof CashPecPayBean) {
                    pst.setDouble(4, ( (CashPecPayBean) obj).getBaseCuryRate());
                } else if (obj instanceof InvestPayRecBean) {
                    pst.setDouble(4, ( (InvestPayRecBean) obj).getBaseCuryRate());
                }
                if (obj instanceof SecPecPayBean) {
                    pst.setDouble(5, ( (SecPecPayBean) obj).getPortCuryRate());
                } else if (obj instanceof CashPecPayBean) {
                    pst.setDouble(5, ( (CashPecPayBean) obj).getPortCuryRate());
                } else if (obj instanceof InvestPayRecBean) {
                    pst.setDouble(5, ( (InvestPayRecBean) obj).getPortCuryRate());
                }

                pst.setInt(6, 1);
                pst.setString(7, pub.getUserCode());
                pst.setString(8, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(9, pub.getUserCode());
                pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                pst.executeUpdate();
            }
            //add by huangqirong 2013-03-05 bug #7255 避免 各获取估值汇率时没有估值汇率
            BaseOperDeal operDeal = new BaseOperDeal();
    		operDeal.setYssPub(pub);
            rs = dbl.openResultSet("select * from " + pub.yssGetTableName("Tb_Para_Currency")+
            		" where FCuryCode not in (" +operSql.sqlCodes(sCuryCodes) + ")");
            while(rs.next()){
            	pst.setDate(1, YssFun.toSqlDate(dDate));
                pst.setString(2, this.portCode);
                pst.setString(3, rs.getString("FCuryCode"));
                pst.setDouble(4, operDeal.getCuryRate(this.dDate, rs.getString("FCuryCode"), this.portCode, "base"));
                pst.setDouble(5, operDeal.getCuryRate(this.dDate, rs.getString("FCuryCode"), this.portCode, "port"));
                pst.setInt(6, 1);
                pst.setString(7, pub.getUserCode());
                pst.setString(8, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(9, pub.getUserCode());
                pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                pst.executeUpdate();
            }
            //---end---
            hmValRate = null;
			//这里直接提交数据 合并太平版本调整添加 by leeyu 20100825
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans=false;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);//这里直接提交数据 合并太平版本调整添加 by leeyu 20100825
            dbl.closeResultSetFinal(rs);	//add by huangqirong 2013-03-05 bug #7255 避免 各获取估值汇率时没有估值汇率
        }
    }

    public void clearValPrice() {
        hmValPrice.clear();
    }

    /**
     * 如果存放行情的hashmap中没有股票篮中的证券，则通过估值方法获取该证券的行情，并用于股票篮的估值中
     *
     */
	public void addETFSecPrice() throws YssException {
		Iterator iter = null;
		ValMktPriceBean mktPrice = null;
		StringBuffer sbTmp = new StringBuffer();
		String strHmSecCodes = "";
		String strSecCodes = "";
		String strSql = "";
		ResultSet rs = null;
		ArrayList arrList = null;
		MTVMethodBean vMethod = null;
		try{
			//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
            if (hmValPrice == null && (hmValPrice != null && hmValPrice.size() == 0)) {
                //return;
            }
            //---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
            iter = hmValPrice.values().iterator();
            while(iter.hasNext()){
            	mktPrice = (ValMktPriceBean) iter.next();
            	sbTmp.append(mktPrice.getSecurityCode()).append(",");            	
            }
            strHmSecCodes = sbTmp.toString();
            if(strHmSecCodes.length() > 1){
            	strHmSecCodes = strHmSecCodes.substring(0,strHmSecCodes.length()-1);
            }
            strSql = " select FSecurityCode from " + pub.yssGetTableName("Tb_ETF_StockList") + 
		    		" where FPortCode = " + dbl.sqlString(this.portCode) + 
		    		" and FDate = " + dbl.sqlDate(this.dDate) + 
		    		(strHmSecCodes.length() > 0 ? " and FSecurityCode not in(" + operSql.sqlCodes(strHmSecCodes) + ")" : " ");
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	strSecCodes = strSecCodes + rs.getString("FSecurityCode") + ",";
            }
            dbl.closeResultSetFinal(rs);
            if(strSecCodes.length() > 1){
            	strSecCodes = strSecCodes.substring(0,strSecCodes.length()-1);
            }else{
            	return;
            }            
            arrList = this.getValuationMethods();
            for (int i = 0; i < arrList.size(); i++) {
            	vMethod = (MTVMethodBean) arrList.get(i);            	
		        strSql = "select a.* , mk.FCsMarketPrice,mk.FMktValueDate, mk.fmarketstatus from (" +
		                " select FSecurityCode from " + pub.yssGetTableName("Tb_ETF_StockList") + 
		                //取替代标识为可以现金替代的证券 深交所：1 上交所：5
		                //STORY #1434 QDV4易方达基金2011年7月27日01_A panjunfang modify 20111130
		        		" where FReplaceMark in( '1','5') and FPortCode = " + dbl.sqlString(this.portCode) + 
		        		" and FDate = " + dbl.sqlDate(this.dDate) + 
		        		" and FSecurityCode in(" + operSql.sqlCodes(strSecCodes) + ") ) a " + 
	                    " left join " +
	                    " ( select mk2.FCsMarketPrice, mk2.FSecurityCode,mk1.FMktValueDate, mk2.fmarketstatus from " +
	                    " (select max(FMktValueDate) as FMktValueDate, FSecurityCode from " +
	                    pub.yssGetTableName("Tb_Data_MarketValue") +
	                    " where FCheckState = 1" +
	                    " and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
	                    " and FMktValueDate <= " + dbl.sqlDate(dDate) +
	                    " group by FSecurityCode ) mk1 join (select " +
	                    vMethod.getMktPriceCode() +
	                    " as FCsMarketPrice,FSecurityCode, FMktValueDate, FMARKETSTATUS from " +
	                    pub.yssGetTableName("Tb_Data_MarketValue") +
	                    " where FCheckState = 1 and FMktSrcCode = " +
	                    dbl.sqlString(vMethod.getMktSrcCode()) + " order by FMktValueDate desc) mk2 " +
	                    " on mk1.FSecurityCode=mk2.FSecurityCode and mk1.FMktValueDate=mk2.FMktValueDate" +
	                    " ) mk on mk.FSecurityCode = a.FSecurityCode ";
		        rs = dbl.openResultSet(strSql);
		        while(rs.next()){
		        	//去掉没行情的数据
		        	if(rs.getDate("FMktValueDate") == null){
		        		continue;
		        	}
		        	mktPrice = new ValMktPriceBean();
                    mktPrice.setValType("ETFValuation"); 
                    mktPrice.setValDate(rs.getDate("FMktValueDate"));
                    mktPrice.setSecurityCode(rs.getString("FSecurityCode"));
                    mktPrice.setPortCode(portCode);
                    mktPrice.setPrice(rs.getDouble("FCsMarketPrice"));
                    mktPrice.setMarketStatus(rs.getString("fmarketstatus"));
                    hmValPrice.put(mktPrice.getSecurityCode(), mktPrice);
		        }
		        dbl.closeResultSetFinal(rs);
            }
            
		}catch(Exception e){
			throw new YssException("获取股票篮中证券对应的行情出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
