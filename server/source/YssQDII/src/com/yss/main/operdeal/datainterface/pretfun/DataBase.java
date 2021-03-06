package com.yss.main.operdeal.datainterface.pretfun;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.operdeal.*;
import com.yss.main.parasetting.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.main.operdeal.bond.BondAssist;
import com.yss.main.operdeal.bond.BondInsCfgFormula;
import java.sql.ResultSet; //QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26
import com.yss.main.operdeal.datainterface.cnstock.pojo.PublicMethodBean; // by leeyu
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadXMLRuleBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.FeeAttributeBean; // by leeyu
import com.yss.main.operdeal.income.stat.StatBondInterest;

@SuppressWarnings("unchecked")
public class DataBase
    extends BaseBean {

    protected CalcBean base = null; //数据缓冲
    protected DataMake calc = null; //数据常量
    protected java.util.Date sDate;
    protected String sPort = ""; //组合
    protected String sExchange = ""; //交易所  --上交所或者是深交所
    protected String cusCfgCode = ""; //自定义接口配置代码 xuqiji 20090709 MS00497 QDV4中保2009年06月09日01_A  
   //MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据 add by jiangshichao 2010.09.10
   protected java.util.Date endDate;//截止日期
   protected java.util.Date dealDate;
  //MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据  end --------------------------
	private HashMap htKeyFee = new HashMap();
    //QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26
    protected HashMap hmReadType = null; //用于储存数据接口参数设置界面的读数处理方式分页的各种参数 key--组合群代码,组合代码
    protected HashMap hmExchangeBond = null; //用于储存数据接口参数设置界面的交易所债券参数设置分页的各种参数 key--组合群代码, 组合代码, 市场, 品种
    protected HashMap hmTradeFee = null; //用于储存数据接口参数设置界面的交易费用计算方式分页的各种参数设置 key--费用代码 + " " + 计算方式字符（如cjmx）
    protected HashMap hmFeeWay = null; //用于储存数据接口参数设置界面的费用承担方向分页的各种参数设置 key="组合群代码 组合代码 券商代码 席位代码"  value=FeeWayBean
    protected HashMap hmRateSpeciesType = null; //用于储存各种交易品种费率 key--费率类型, 费率品种
    protected HashMap hmBrokerRate = null; //用于储存券商佣金利率 key--组合代码, 券商代码, 席位地点（上海或深圳）, 席位号, 品种类型
    protected HashMap hmPortHolderSeat = null; //用于储存组合下对应的券商代码和席位代码
    protected HashMap hmBrokerCode = null; //用于储存席位代码对应的券商代码
    private HashMap hmHolderSeat = null; //存储组合下的券商与席位的 key=组合代码 value=券商代码\t席位代码
    protected String assetGroupCode = ""; //组合群代码
    protected PublicMethodBean pubMethod = new PublicMethodBean(); //添加对公共方法处理的类，以便在继承类中调用公共处理的方法 by leeyu 20090630
    protected ReadXMLRuleBean pubXMLRead = new ReadXMLRuleBean(); //添加对国内接口XML配置读的类，以便在继承类中调用统一的方法 by leeyu 20090714
    protected String checkState = ""; //审核状态
    private java.util.Date tradeDate = null;//交易日期
    private String zqdm = null;//证券代码
    //QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26
    public DataBase() {

    }

    public void setDataMake(DataMake obj) {
        calc = obj;
        base = obj.getBase();
        this.setYssPub(base.getYssPub());
	
	}
   //QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26
    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }
	//QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26

    /**
     * 初始化的参数设置 用于对类中各变量初始化
     * by leeyu add
     * @param dataBase DataBase
     */
    public void setDataBase(DataBase dataBase) {
        this.base = dataBase.base;
        this.calc = dataBase.calc;
        this.sDate = dataBase.sDate;
        this.sPort = dataBase.sPort;
        this.sExchange = dataBase.sExchange;
        //国内：MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A
        //add by songjie 2009-06-15 用于设置自定义接口配置代码
        this.cusCfgCode = dataBase.cusCfgCode;
        pubMethod.setYssPub(pub); // by leeyu
        pubXMLRead.setYssPub(pub); // by leeyu
    }

    public void inertData() throws YssException {}

    //国内：MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A add by songjie 2009-06-15 用于设置自定义接口配置代码
    public void setCusCfgCode(String cusCfg) {
        cusCfgCode = cusCfg;
    }
    //QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26
    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }
	//QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26
    public void setEndDate(java.util.Date endDate){
    	this.endDate = endDate;
    }
    
    public void setDealDate(java.util.Date dealDate){
    	this.dealDate = dealDate;
    }
  //MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据  end -------------------------
    
    //国内：MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A add by songjie 2009-06-15 用于设置自定义接口配置代码
    public void initDate(java.util.Date sDate, String sExchange, String sPort) throws YssException {
        this.sDate = sDate;
        this.sExchange = sExchange;
        this.sPort = sPort;
    }

    public Hashtable getFee(String catCode, String stockHoderCode,
                            String exchangeCode, String brokeCode,
                            String tradeSeat,
                            double tradeMoney, double tradeAmount,
                            String tradeDate,
                            String sPortCode) throws YssException { //增加组合代码 by leeyu 080701
        String sResult = "";
        ArrayList alFeeBeans = new ArrayList();
        YssFeeType feeType = null;
        FeeBean fee = null;
        double dFeeMoney = 0.0;
        Hashtable table = new Hashtable();
        String sKey = "";
        try {
            BaseOperDeal baseOper = this.getSettingOper();
            BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean(
                "feedeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);

            feeOper.setBrokerCode("");
            feeOper.setCatCode(catCode);
            feeOper.setTradeSeatCode(tradeSeat);
            feeOper.setStockholderCode("");
            feeOper.setExchangeCode(exchangeCode);
            feeOper.setPortCode(sPortCode);
            feeOper.setCurrencyCode("CNY"); //增加组合代码与币种 by leeyu 080701
            sKey = catCode + "\t" + tradeSeat + "\t" + exchangeCode + "\t" + sPortCode + "\t" + "CNY";
            if (htKeyFee.get(sKey) == null) { //优化代码 by leeyu 080702
                alFeeBeans = feeOper.getFeeBeans();
                htKeyFee.put(sKey, alFeeBeans);
            } else {
                alFeeBeans = (ArrayList) htKeyFee.get(sKey);
            }
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(tradeMoney);
                feeType.setInterest( -1);
                feeType.setAmount(tradeAmount);
                feeType.setCost( -1);
                feeType.setIncome( -1);
                feeType.setFee( -1);
                if (alFeeBeans.size() > 0) {
                    for (int i = 0; i < alFeeBeans.size(); i++) {
                        fee = (FeeBean) alFeeBeans.get(i);
                        dFeeMoney = baseOper.calFeeMoney(feeType, fee,
                            YssFun.toDate(tradeDate));
                        table.put(fee.getFeeCode(), new Double(dFeeMoney));
                    }
                    for (int j = 0; j < 8 - table.size(); j++) {
                        table.put("", new Double(0.0));
                    }
                }
            }
            return table;
        } catch (Exception e) {
            throw new YssException();
        }
    }

    //-------------QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26-----------------------------------------//
    /**
     * 用于获取组合代码对应的股东代码和席位代码对应的Hashmap
     * key-组合代码， value-席位代码 + "\t" + 股东代码
     * @param portCodes String
     * @return HashMap
     * @throws YssException
     */
    public HashMap getStockHolderAndSeat(String portCodes) throws YssException {
        String[] arrPort = null;
        try {
            if (hmHolderSeat == null)
                hmHolderSeat = new HashMap();
            arrPort = portCodes.split(",");
            for (int i = 0; i < arrPort.length; i++) {
                if (hmHolderSeat.get(arrPort[i]) == null) {
                    getPStockHolderAndSeat(arrPort[i]);
                }
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return hmHolderSeat;
    }

    public void setHMHolderSeat(HashMap hmHolderSeat) {
        this.hmHolderSeat = hmHolderSeat;
    }

    private void getPStockHolderAndSeat(String portCode) throws YssException {
        String strSql = ""; //用于储存sql语句
        String tradeSeats = ""; //席位号
        String stockHolders = ""; //股东代码
        String TSInfo = "";
        ResultSet rs = null; //声明结果集
        ArrayList alTradeSeat = new ArrayList();
        ArrayList alStockHolder = new ArrayList();
        try {
            //在组合设置关联信息表中查询相关组合代码的股东代码和席位代码
            strSql = "select FSubCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                " where FPortCode = " + dbl.sqlString(portCode) +
                " and FRelaType = 'Stockholder' and FCheckState = 1 ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {  
                if(!alStockHolder.contains(rs.getString("FSubCode"))){
                    alStockHolder.add(rs.getString("FSubCode"));
                    stockHolders += rs.getString("FSubCode") + ","; //拼接股东代码数据
                }
            }
            dbl.closeResultSetFinal(rs);
          //edit by yanghaiming 20100610 MS01257 QDV4赢时胜(上海)2010年5月26日05_B   这里改取席位号
            strSql = "select distinct b.fseatnum as fsubcode from " +
	            pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
	            " a left join (select * from " + pub.yssGetTableName("tb_para_tradeseat") + ") b on a.fsubcode = b.fseatcode" +
	            " where a.FPortCode = " + dbl.sqlString(portCode) +
	            " and a.FRelaType = 'TradeSeat' and a.FCheckState = 1 ";
	        rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	if(!alTradeSeat.contains(rs.getString("FSubCode"))){
	                alTradeSeat.add(rs.getString("FSubCode"));
	                tradeSeats += rs.getString("FSubCode") + ",";//拼接席位代码数据
	            }
            }      
            if (tradeSeats.indexOf(",") != -1) {
                tradeSeats = tradeSeats.substring(0, tradeSeats.length() - 1); //将最后的逗号去掉
            } else {
                throw new YssException(" 请在组合设置中设置组合代码" + portCode + "对应的席位代码！");
            }

            if (stockHolders.indexOf(",") != -1) {
                stockHolders = stockHolders.substring(0, stockHolders.length() - 1); //将最后的逗号去掉
            } else {
                throw new YssException(" 请在组合设置中设置组合代码" + portCode + "对应的股东代码！");
            }

            TSInfo = tradeSeats + "\t" + stockHolders;//拼接席位代码数据和股东代码数据
            hmHolderSeat.put(portCode, TSInfo);//将组合代码对应的席位代码和股东代码储存到哈希表中
        } catch (Exception e) {
            throw new YssException("获取组合代码对应的股东代码和席位代码数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据席位代码和表名获取券商代码
     * @return HashMap
     * @throws YssException
     */
    //edit by songjie 2012.01.13 BUG 3640 QDV4赢时胜(上海开发部)2012年1月13日01_B 去掉 ifFromQS 的参数
    public HashMap getBrokerCode(String xwdm, String tableName, boolean comeFromQs, java.util.Date date) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        HashMap hmBroker = new HashMap();
        try {
        	 //edit by songjie 2012.01.13 BUG 3640 QDV4赢时胜(上海开发部)2012年1月13日01_B 将 FSeatCode 替换为 FSeatNum
        	 strSql = " select FSeatNum, FBrokerCode from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " where FSeatNum " +
             " in (select distinct " + xwdm + " from " + tableName + ") and FCheckState = 1 "; //查询指定表中的所有席位号对应的券商代码数据
        	 if (comeFromQs) {
                 strSql = " select FSeatNum, FBrokerCode from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " where FSeatNum " +
                 " in (select distinct " + xwdm + " from " + tableName + " where FInDate = " + dbl.sqlDate(date) +
                 ") and FCheckState = 1 ";
        	 }
        	 rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	//edit by songjie 2012.01.13 BUG 3640 QDV4赢时胜(上海开发部)2012年1月13日01_B 将 FSeatCode 替换为 FSeatNum
                hmBroker.put(rs.getString("FSeatNum"), rs.getString("FBrokerCode")); //将席位代码对应的券商代码储存到HashMap中
            }
            return hmBroker;
        } catch (Exception e) {
            throw new YssException("获取席位代码对应的券商代码出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 新的方法 依据组合代码和上海过户库中公司代码即：席位号去索引交易席位中的席位代码和券商代码 add by zhouxiang MS1299
     * @return HashMap
     * @throws YssException
     */ 
   
    public HashMap getBrokerCode(String xwdm, String tableName, boolean comeFromQs, java.util.Date date,String  port) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        HashMap hmBroker = new HashMap();
        try {
        	//modify by zhangfa MS01679    交易所回购，已设置费用承担方式参数，但是导入过户库仍提示    QDV4赢时胜（上海）2010年8月18日01_B 
        	strSql = " select fseatnum as FSeatCode,FBrokerCode from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " where FSeatNum in" +"(select distinct "+xwdm+" from "+tableName+
        	 //---------------------------------------------------------------------------------------------------------------
        	" where "+xwdm+" in (select a.fseatnum from  "+pub.yssGetTableName("Tb_Para_TradeSeat")+" a join ( select * from " +pub.yssGetTableName("Tb_Para_Portfolio_Relaship")
        	+"  where fportcode ="+"'"+port+"'"+") b on a.fseatcode= b.fsubcode)"+
         ") and FCheckState = 1 "; //查询指定表中的所有席位代码对应的券商代码数据
        	
             	if (comeFromQs) {
                //查询指定表中的所有席位代码对应的券商代码数据
             //modify by zhangfa MS01679    交易所回购，已设置费用承担方式参数，但是导入过户库仍提示    QDV4赢时胜（上海）2010年8月18日01_B  
                strSql = " select fseatnum as FSeatCode,FBrokerCode from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " where fseatnum " +
                    " in (select distinct " + xwdm + " from " + tableName + " where FDate = " + dbl.sqlDate(date) +
                    //--------------------------------------------------------------------------------------------------------------- 
                    ")  and FCheckState = 1 ";
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                hmBroker.put(rs.getString("FSeatCode"), rs.getString("FBrokerCode")); //将席位代码对应的券商代码储存到HashMap中
            }
            return hmBroker;
        } catch (Exception e) {
            throw new YssException("获取席位代码对应的券商代码出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //-----------------------------end------------------------------------------
    /**
     * 根据债券代码计算债券利息
     * 债券利息 = roundit(成交数量×每百元债券利息,2)
     * 每百元债券利息 = （每百元票面利率÷365）×（票面金额÷100）×已记提天数
     * @param securityCode String
     * @return double
     * @throws YssException
     */
    public HashMap calculateZQRate(String securityCode, java.util.Date date, String bs, String portCode) throws YssException {
        String strSql = "";//用于储存sql语句
        ResultSet rs = null;//声明结果集      
        double per100ZQRate = 0; //每百元债券利息
        double sqPer100ZQRate = 0; //税前每百元债券利息

        HashMap hmZQRate = new HashMap();

        //--MS00847 QDV4赢时胜（北京）2009年11月30日03_B add by songjie 2009.12.18 根据债券计息公式计算税前或税后百元债券利息--//
        String periodCode = "";//期间代码
        HashMap hmRate = null;
        StatBondInterest bondInterest = null;
        String sType = "";//计息类型
        String calcInsMetic = "";//利息算法
        //--MS00847 QDV4赢时胜（北京）2009年11月30日03_B add by songjie 2009.12.18 根据债券计息公式计算税前或税后百元债券利息--//
        boolean haveInfo = false;
        //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
        ReadTypeBean readType = null;
        //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
        String exchangeCode = "";//交易所代码
        //--- add by songjie 2013.04.27 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
        String holidayCode = "";//节假日群代码
        SecurityBean sec = null;
        //--- add by songjie 2013.04.27 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
        try {
        	sType = bs.equals("B")?"Buy":"Sell";
        	
        	if(bs.equals("Day")){
        		sType = "Day";
        	}
        	
            BondInsCfgFormula bondFormula = new BondInsCfgFormula();
            bondFormula.setYssPub(pub);

            //delete by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
            //HashMap hmDates = new HashMap();

            //--- add by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            HashMap hmsec = BondAssist.hmSec;
            if(hmsec != null && hmsec.get(securityCode) != null){
            	haveInfo = true;
            	FixInterestBean fixIt= (FixInterestBean)hmsec.get(securityCode);
            	sec = fixIt.getSecurity();
            	//--- add by songjie 2013.04.27 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            	if(!(sec.getExchangeCode().equals("CS") || sec.getExchangeCode().equals("CG") || sec.getExchangeCode().equals("CY"))){
            		holidayCode = sec.getHolidaysCode();
            	}
            	//--- add by songjie 2013.04.27 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            	periodCode = fixIt.getStrPeriodCode();
            	exchangeCode = fixIt.getExchangeCode();
                if(bs.equals("B")){
                	calcInsMetic = fixIt.getStrCalcInsMeticBuy();
                }
                else if(bs.equals("S")){
                	calcInsMetic = fixIt.getStrCalcInsMeticSell();
                }
                if(bs.equals("Day")){
                	calcInsMetic = fixIt.getStrCalcInsMeticDay();
                }
            }else{
			    //--- add by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
                //在债券信息设置表中查询相关证券的数据
                //edit by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
                strSql = " select fix.*,sec.FExchangeCode from (select * from " + 
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FSecurityCode = " + dbl.sqlString(securityCode) +
                " and FInsStartDate <= " + dbl.sqlDate(date) +
                " and FInsEndDate >= " + dbl.sqlDate(date) + " and FCheckState = 1) fix " +
                " left join " + pub.yssGetTableName("Tb_Para_Security") + " sec " +
                " on fix.FSecurityCode = sec.FSecurityCode";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	haveInfo = true;
            	
                	periodCode = rs.getString("FPeriodCode");
                	//add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
                	exchangeCode = rs.getString("FExchangeCode");
                	
                	if(bs.equals("B")){
                		calcInsMetic = rs.getString("FCalcInsMeticBuy");
                	}	
                	else if(bs.equals("S")){
                		calcInsMetic = rs.getString("FCalcInsMeticSell");
                	}
                	if(bs.equals("Day")){
                		calcInsMetic = rs.getString("FCalcInsMeticDay");
                	}
                }
            }
            if(haveInfo){
            	//--MS00847 QDV4赢时胜（北京）2009年11月30日03_B add by songjie 2009.12.18 根据债券计息公式计算税前或税后百元债券利息--//
            	bondInterest = new StatBondInterest();
                bondInterest.setYssPub(pub);

                //获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap
                //--- edit by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
                if(sec == null){
                	//add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                	CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin(); //新建CNInterfaceParamAdmin
                	interfaceParam.setYssPub(pub);
                	hmReadType = (HashMap) interfaceParam.getReadTypeBean();
                	//--- edit by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
                	readType = (ReadTypeBean)hmReadType.get(pub.getAssetGroupCode() + " " + portCode);
                	//---add by songjie 2011.11.09 BUG 3042 QDV4赢时胜(测试)2011年11月01日02_B 若获取不到参数 则提示 start---//
                	if(readType == null){
                		throw new YssException("请在交易接口参数设置中设置 " + portCode + " 组合的读数处理方式！");
                	}
                	
                	holidayCode = readType.getHolidaysCode();
                }
                //---add by songjie 2011.11.09 BUG 3042 QDV4赢时胜(测试)2011年11月01日02_B end---//
                
                //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
				//--- add by songjie 2013.06.18 BUG 8325 QDV4赢时胜(北京)2013年06月19日04_B start---//
				//如果获取不到 利息算法公式对应的代码，则不计算债券利息
                if(!calcInsMetic.equals("null")){
				//--- add by songjie 2013.06.18 BUG 8325 QDV4赢时胜(北京)2013年06月19日04_B end---//
                	hmRate = bondInterest.domesticInnerCal(securityCode, 
                			periodCode, 
                			calcInsMetic,
                			" ", 
                			//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                			holidayCode, //edit by songjie 2013.04.27 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
                			date, 
                			0, 
                			" ",
                			" ",
                			sType,
                			" ",
                			portCode,
                			//add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
                			exchangeCode);
                
                	sqPer100ZQRate = Double.parseDouble((String)hmRate.get("before"));
                	per100ZQRate = Double.parseDouble((String)hmRate.get("after"));
                }//add by songjie 2013.06.18 BUG 8325 QDV4赢时胜(北京)2013年06月19日04_B 如果获取不到 利息算法公式对应的代码，则不计算债券利息
                //--MS00847 QDV4赢时胜（北京）2009年11月30日03_B add by songjie 2009.12.18 根据债券计息公式计算税前或税后百元债券利息--//
            }
            
            hmZQRate.put("GZLX", Double.toString(per100ZQRate));//将税后每百元债券利息储存到哈希表中
            hmZQRate.put("SQGZLX", Double.toString(sqPer100ZQRate));//将税前每百元债券利息储存到哈希表中
            //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
            hmZQRate.put("haveInfo", String.valueOf(haveInfo));//将是否能查到债券的信息设置数据的Boolean值储存到hmZQRate中
            
            return hmZQRate;
        } catch (Exception e) {
            throw new YssException("根据债券代码计算债券利息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据债券代码在国债利息库的临时表中获取每百元应计利息
     * @param securityCode String
     * @return double
     */
    public HashMap getPerHundredZQRate(String securityCode, java.util.Date date) throws YssException {
        String strSql = "";//用于储存sql语句
        ResultSet rs = null;//声明结果集
        double perHundredZQRate = 0;//初始化每百元债券利息
        double perSHHundredZQRate = 0;//初始化税后每百元债券利息
        String dDate = null;//格式化之后的交易日期
        HashMap hmPerZQRate = new HashMap();
        String haveInfo = "false";//用于判断是否有债券利息数据
        try {
            dDate = YssFun.formatDate(date);//将交易日期格式化
            /**shashijie 2012-7-2 STORY 2475 */
            if (dDate.indexOf("-") != -1) {
                dDate = dDate.replaceAll("-", "");
            }
            /**end*/

            //在债券利息表中查询相关证券代码的数据
            strSql = " select * from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where FSecurityCode = " + dbl.sqlString(securityCode) +
                " and FRecordDate = " + dbl.sqlDate(date) +
                " and FCheckState = 1 and FDataSource = 'IF'";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                perHundredZQRate = rs.getDouble("FIntAccPer100");//获取税前每百元债券利息
                perSHHundredZQRate = rs.getDouble("FSHIntAccPer100");//获取税后每百元债券利息
                haveInfo ="true";
            }

            hmPerZQRate.put("haveInfo",haveInfo);
            hmPerZQRate.put("PerGZLX", Double.toString(perHundredZQRate));
            hmPerZQRate.put("SHPerGZLX", Double.toString(perSHHundredZQRate));

            return hmPerZQRate;
        } catch (Exception e) {
            throw new YssException("根据债券代码获取每百元债券利息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 在估值方法设置中查询审核状态为已审核且有估值方法筛选条件的筛选条件数据
     * （包括估值方法代码，筛选条件：品种类型，品种子类型，自定义子类型，交易所代码）
     * @throws YssException
     */
    public HashMap getMTVSelInfo() throws YssException {
        String strSql = null;
        ResultSet rs = null;
        String mtvCode = null; //估值方法代码
        String catCode = null; //品种类型代码
        String subCatCode = null; //品种子类型代码
        String cusCatCode = null;//自定义子类型代码
        String exchangeCode = null; //交易所代码

        String key = null;
        HashMap hmMTVInfo = null;
        ArrayList alMTVCode = null; //用于储存估值方法筛选条件数据对应的估值方法
        ArrayList alKey = new ArrayList();//用于储存所有符合条件的估值方法筛选条件
        ArrayList alMtvCodes = new ArrayList();//用于储存所有符合条件的估值方法代码
        boolean haveMtvInfo = false;
        try {
            strSql = " select mtvMethod.Fmtvcode as FMtvCode, mtvSel.FCatCode as FCatCode, " +
                " mtvSel.FSubCatCode as FSubCatCode, mtvSel.FCusCatCode as FCusCatCode, " +
                " mtvSel.FExchangeCode as FExchangeCode from (select * from " +
                pub.yssGetTableName("Tb_Para_MTVMethod") +
                " where FCheckState = 1) mtvMethod " + " left join (select * from " +
                pub.yssGetTableName("Tb_Para_MTVSelCondSet") + " where Fcheckstate = 1) mtvSel " +
                " on mtvMethod.Fmtvcode = mtvSel.Fmtvcode ";

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                mtvCode = rs.getString("FMtvCode");//估值方法代码
                catCode = rs.getString("FCatCode");//品种类型代码
                subCatCode = rs.getString("FSubCatCode");//品种子类型代码
                cusCatCode = rs.getString("FCusCatCode");//自定义子类型代码
                exchangeCode = rs.getString("FExchangeCode");//交易所代码

                if(catCode != null){
                	if(hmMTVInfo == null){
                		hmMTVInfo = new HashMap();
                	}
                	
                    haveMtvInfo = true;

                    if(subCatCode == null || subCatCode.equals("null")){
                    	subCatCode = "";
                    }
                    
                    if(exchangeCode == null || exchangeCode.equals("null")){
                    	exchangeCode = "";
                    }
                    //edit by lidaolong 20110401  #553 估值方法设置中添加字段区分市场或交易所
                    //表中Tb_Para_MTVSelCondSet现在是多个拼在一起的，所以要分开
                    
					for (int i = 0; i < exchangeCode.split(",").length; i++) {

						key = catCode + "\t" + subCatCode + "\t" + exchangeCode.split(",")[i];

						if (!alMtvCodes.contains(mtvCode)) {
							alMtvCodes.add(mtvCode);
						}

						if (!alKey.contains(key)) {
							alKey.add(key);
						}

						alMTVCode = (ArrayList) hmMTVInfo.get(key);

						if (alMTVCode != null) {
							if (!alMTVCode.contains(mtvCode)) {
								alMTVCode.add(mtvCode);
								hmMTVInfo.put(key, alMTVCode);
							}
						} else {
							alMTVCode = new ArrayList();
							alMTVCode.add(mtvCode);
							hmMTVInfo.put(key, alMTVCode);
						}

					}//end for
                    //end by lidaolong
                }
            }

            if(haveMtvInfo){
                hmMTVInfo.put("key", alKey);
                hmMTVInfo.put("mtvCodes",alMtvCodes);
            }

            return hmMTVInfo;
        } catch (Exception e) {
            throw new YssException("查询估值方法筛选条件设置的信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 2009.09.08
     * 国内:MS00006
     * QDV4.1赢时胜（上海）2009年4月20日06_A
     * 若证券标志为权益，则在证券库存中查找相关的权益确认日的库存数据，
     * 若有相关库存数据，才能将权益数据储存到交易接口明细库中。
     * @param zqbz String
     * @param ywbz String
     * @param tradeDate Date
     * @return boolean
     * @throws YssException
     */
    public boolean judgeQYInfo(FeeAttributeBean feeAttribute)throws YssException{
        String zqbz = null;//证券标志
        String ywbz = null;//业务标志
        java.util.Date affirmDate = null;//权益确认日
        boolean canInsert = false;//判断是否能将数据储存到交易接口明细库
        try{
            if(feeAttribute != null && feeAttribute.getSecuritySign().equals("QY")){
                zqbz = feeAttribute.getSecuritySign();
                ywbz = feeAttribute.getBusinessSign();
                zqdm = feeAttribute.getZqdm();
                tradeDate = feeAttribute.getDate();

                if(zqbz.equals("QY")){
                    //若业务标志位 指数型配股缴款，指标型配股缴款，配股缴款，派息到账，现金对价到账,
                	//配股，送股，股票分红，现金对价，可分离债券送配,债券派息 PX_ZQ 或 基金派息 PX_JJ
                    if(ywbz.equals("PGJK_ZS") || ywbz.equals("PGJK") || ywbz.equals("PGJK_ZB") ||
                    ywbz.equals("PG") || ywbz.equals("SG_ZS") || ywbz.equals("SG") ||
                    ywbz.equals("PX_GP") || ywbz.equals("XJDJ") || ywbz.equals("QZ") ||
                    ywbz.equals("PX_ZQ") || ywbz.equals("PX_JJ")	){
                        canInsert = true;
                    }

//                    //配股 PG
//                    if (ywbz.equals("PG") || ywbz.equals("QZ")) {
//                        //获取配股的权益确认日
//                        affirmDate = getPGAffirmDate(ywbz);
//                    }
//                    //送股 SG_ZS SG
//                    if (ywbz.equals("SG_ZS") || ywbz.equals("SG")) {
//                        //获取送股的权益确认日
//                        affirmDate = getSGAffirmDate();
//                    }
//                    //股票分红 PX_GP
//                    if (ywbz.equals("PX_GP")) {
//                        //获取股票分红的权益确认日
//                        affirmDate = getPXAffirmDate();
//                    }
//                    //现金对价 XJDJ
//                    if (ywbz.equals("XJDJ")) {
//                        //获取现金对价的权益确认日
//                        affirmDate = getXJDJAffirmDate();
//                    }

                    //股份对价 GFDJ_ZS GFDJ
                    if (ywbz.equals("GFDJ_ZS") || ywbz.equals("GFDJ")) {
                        canInsert = false;
                    }
                }
            }

            return canInsert;
        }
        catch(Exception e){
            throw new YssException("判断权益是否有库存数据出错！",e);
        }
    }

    /**
     * 获取配股的权益确认日
     * @return Date
     * @throws YssException
     */
    private java.util.Date getPGAffirmDate(String ywbz) throws YssException{
        java.util.Date affirmDate = null;//权益确认日
        String strSql = null;//用于储存sql语句
        ResultSet rs = null;//声明结果集
        String zqCode = "";//可分离债券代码
        try{
            //查询相关配股权益数据
            if(ywbz.equals("PG")){//若业务标志为配股
                strSql = " select * from " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                    " where FSecurityCode = " + dbl.sqlString(zqdm) + " and FCheckState = 1 " +
                    " and " + dbl.sqlDate(tradeDate) + " between FRecordDate and FExRightDate";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    affirmDate = rs.getDate("FRecordDate");
                }
            }

            if(ywbz.equals("QZ")){//若业务标志为权证送配
                strSql = " select * from " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                    " where FTSecurityCode = " + dbl.sqlString(zqdm) + " and FCheckState = 1 " +
                    " and " + dbl.sqlDate(tradeDate) + " between FRecordDate and FExRightDate";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    affirmDate = rs.getDate("FRecordDate");
                    zqCode = rs.getString("FSecurityCode");
                }

                zqdm = zqCode;
            }

            return affirmDate;
        }
        catch(Exception e){
            throw new YssException("获取配股权益确认日出错!",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取送股的权益确认日
     * @return Date
     * @throws YssException
     */
    private java.util.Date getSGAffirmDate() throws YssException{
        java.util.Date affirmDate = null;//权益确认日
        String strSql = null;//用于储存sql语句
        ResultSet rs = null;//声明结果集
        try{
            //查询相关送股权益数据
            strSql = " select * from " + pub.yssGetTableName("Tb_Data_BonusShare") +
                " where FSSecurityCode = " + dbl.sqlString(zqdm) + " and FCheckState = 1 and " +
                dbl.sqlDate(tradeDate) + " between FRecordDate and FExRightDate ";

            rs = dbl.openResultSet(strSql);
            while(rs.next()){
                affirmDate = rs.getDate("FRecordDate");
            }

            return affirmDate;
        }
        catch(Exception e){
            throw new YssException("获取送股权益确认日出错!",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取股票分红派息的权益确认日
     * @return Date
     * @throws YssException
     */
    private java.util.Date getPXAffirmDate() throws YssException{
        java.util.Date affirmDate = null;//权益确认日
        String strSql = null;//用于储存sql语句
        ResultSet rs = null;//声明结果集
        try{
            //查询相关股票分红派息权益数据
            strSql = " select * from " + pub.yssGetTableName("Tb_Data_Dividend") +
                " where FSecurityCode = " + dbl.sqlString(zqdm) + " and FCheckState = 1 and " +
                dbl.sqlDate(tradeDate) + " between FRecordDate and FDividendDate ";

            rs = dbl.openResultSet(strSql);
            while(rs.next()){
                affirmDate = rs.getDate("FRecordDate");
            }

            return affirmDate;
        }
        catch(Exception e){
            throw new YssException("获取股票分红派息权益确认日出错!",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取现金对价的权益确认日
     * @return Date
     * @throws YssException
     */
    private java.util.Date getXJDJAffirmDate() throws YssException{
        java.util.Date affirmDate = null;//权益确认日
        String strSql = null;//用于储存sql语句
        ResultSet rs = null;//声明结果集
        try{
            //查询相关现金对价权益数据
            strSql = " select * from " + pub.yssGetTableName("Tb_Data_CashConsider") +
                " where FSecurityCode = " + dbl.sqlString(zqdm) + " and FCheckState = 1 and " +
                dbl.sqlDate(tradeDate) + " between FRecordDate and FExRightDate ";

            rs = dbl.openResultSet(strSql);
            while(rs.next()){
                affirmDate = rs.getDate("FRecordDate");
            }

            return affirmDate;
        }
        catch(Exception e){
            throw new YssException("获取现金对价权益确认日出错!",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据证券代码和库存日期查找相关库存数据
     * @return boolean
     * @throws YssException
     */
    private boolean getSecStockInfo(java.util.Date stockDate)throws YssException{
        String strSql = null; //用于储存sql语句
        ResultSet rs = null; //声明结果集
        boolean haveInfo = false;//判断是否有相关数据
        try {
            //查询相关证券的库存数据
            strSql = " select * from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FSecurityCode = " + dbl.sqlString(zqdm) +
                " and FStorageDate = " + dbl.sqlDate(stockDate) +
                " and FCheckState = 1 ";

            rs = dbl.openResultSet(strSql);
            while(rs.next()){
                haveInfo = true;
                break;
            }

            return haveInfo;
        } catch (Exception e) {
            throw new YssException("获取现金对价权益确认日出错!",e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //-------------QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-06-26-----------------------------------------//
    
    /**
     * add by songjie 
     * 2010.2.4
     * 判断取税前债券利息或税后债券利息
     */
    protected boolean judgeGzlx(FeeAttributeBean feeAttribute) throws YssException{
    	boolean isGzlx = true;
    	ResultSet rs = null;
    	String strSql = "";
    	try{
    		strSql = " select FINTERESTTAXCODE, FSECURITYCODE from " + 
    		pub.yssGetTableName("TB_PARA_BONDINTERTAX") + " where FSECURITYCODE = " + 
    		dbl.sqlString(feeAttribute.getZqdm()) + " and FCheckState = 1 ";
    		
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			if(rs.getString("FINTERESTTAXCODE").equals("0")){//表示取税前票面利率
    				isGzlx = false;
    			}else{
    				isGzlx = true;
    			}
    		}
    		
    		return isGzlx;
    	}catch(Exception e){
    		throw new YssException("判断取税前债券利息或税后债券利息出错!",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
}
