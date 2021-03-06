package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdata.TradeBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.CashAccLinkBean;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.storagemanage.SecurityStorageBean;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.manager.TradeDataAdmin;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @author guyichuan 20110514 STORY #741  
 * 客户要求增加投资的基金做分红转投业务的菜单
 * QDV4富国基金2011年3月2日01_A 
 * 功能：业务处理中的分红转投处理
 */
public class DividendInvest extends BaseOperManage{
	private double investSumMoney=0.0; //金额变量
	private double investMoney;
	

	public void doOpertion() throws YssException {
		//产生一笔交易数据和一笔收入数据（用于冲减应收股利)
		createTradeData();	
	}

	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.dDate = dDate;
	       this.sPortCode = portCode;
	}
	/**
	 * 产生交易数据和一笔收入数据（用于冲减应收股利)
	 * @throws YssException 
	 */
	public void createTradeData() throws YssException{
		try {
			ArrayList tradeList=saveTradeDataToBean();
			if(tradeList!=null){
				saveTradeData(tradeList,dDate,sPortCode);//产生交易数据
			}
			
			//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(tradeList==null || tradeList.size()==0){
    			this.sMsg="        当日无业务";
    		}
    		
		} catch (YssException e) {
			e.printStackTrace();
			throw new YssException("产生交易数据出错",e);
		}
	}
	
	/**
	 * 保存相应的交易数据到bean中
	 * @return tradeList  ArrayList
	 */
	public ArrayList saveTradeDataToBean()throws YssException{
        StringBuffer bufSql = null;				//sql语句的拼接
        ResultSet rs = null;
        double dSecurityAmount = 0; 			//证券数量
        double dSecurityCost = 0; 				//证券成本
        String strCashAccCode = " "; 			//现金帐户
        String strYearMonth = "";				//保存截取日期的年和天
        CashAccountBean caBean = null;			//声明现金账户的bean
        double dBaseRate = 1;					//基础汇率
        double dPortRate = 1;					//组合汇率
        boolean analy1;							//分析代码1
        boolean analy2;							//分析代码2
        boolean analy3;							//分析代码3
        TradeSubBean subTrade = null;			//交易子表的javaBean
        YssCost cost = null;					//声明成本
        SecurityStorageBean secSto = null;		//证券库存的javaBean
        ArrayList tradeList =null;
        CashAccLinkBean cashAccLink = null;		//声明现金账户链接
        ArrayList linkList = null;
        long sNum=0;							//为了产生的编号不重复
        String strSecAttrCls="";
        double dRightSub = 0; //权益（子表）
        Date StorageDate = null; 
        boolean bDivdendByIn=false;
        boolean bDistribute=false;
        String nowDate=null;
        String fNum="";
        String strNumDate=null;
        
        bufSql=new StringBuffer();
        BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
        getOperDealCtx().getBean("cashacclinkdeal");
        
        strYearMonth = YssFun.left(YssFun.formatDate(dDate), 4) + "00";//赋值
        try{ 
        
        analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");//判断是否有分析代码
        analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
        analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
        //sql
        bufSql.append("select b.FBargainDate as BargainDate,");
        bufSql.append("b.FNum as Num,");
        bufSql.append("b.FInvestDate as InvestDate,");
        bufSql.append("b.FInvestMoney as InvestMoney,");
        bufSql.append("b.FChangeMoney as ChangeMoney,");
        bufSql.append("c.FTradeCury, c.FEXCHANGECODE,");
        
        bufSql.append("b.FInvestNum as InvestNum,");
        bufSql.append("b.FBroker as Broker,");
        bufSql.append("d.FPortCury as PortCury,");
        bufSql.append("bb.FPortCode as PortCode,");
        bufSql.append("a.* ,bb.*  from"); 
        
        bufSql.append(" (select FSecurityCode as FSecurityCode1 ,FRecordDate,");
        bufSql.append(" FDividendDate,FDistributeDate,FPreTaxRatio,FAfterTaxRatio,");
        bufSql.append(" FRoundCode,FPortCode,FDivdendType,FCuryCode as FDividendCuryCode");
        bufSql.append(" from "+pub.yssGetTableName("Tb_Data_Dividend")+" where FIsInvest=1 and FCheckState=1)a");
        bufSql.append(" inner join (select * from "+pub.yssGetTableName("Tb_Data_Dividendinvest"));
        bufSql.append(" where FInvestDate="+dbl.sqlDate(this.dDate));
        bufSql.append(" and FCheckState=1)b on a.FSecurityCode1= b.Fsecuritycode");
        bufSql.append(" and a.FPortCode= b.FPortCode");
        
        bufSql.append(" and a.FDividendDate = b.FDividendDate");
        bufSql.append("  and a.FDividendCuryCode = b.FCuryCode");
        bufSql.append(" and a.FDivdendType = b.FDivdendType and a.FRecordDate = b.FRecordDate");
        bufSql.append(" left join (select FSecurityCode, FTradeCury, FEXCHANGECODE");
        bufSql.append(" from "+pub.yssGetTableName("Tb_Para_Security"));
        
        bufSql.append(" where FCheckState = 1) c on a.FSecurityCode1 = c.FSecurityCode");
        bufSql.append(" join (select * from "+pub.yssGetTableName("Tb_Stock_Security"));
        bufSql.append(" where FPortCode =" + dbl.sqlString(sPortCode));//组合代码
        bufSql.append(" and FYearMonth<>").append(dbl.sqlString(strYearMonth));//不是期初数库存
        bufSql.append("  and FCheckState = 1) bb on a.fsecuritycode1 = bb.fsecuritycode");
        bufSql.append(" and (case when c.fexchangecode in ('CY', 'CS', 'CG') then a.FRecordDate else a.FDividendDate - 1 end) = bb.FStorageDate ");//取权益确认日库存
        
        bufSql.append(" left join");
        bufSql.append(" (select FPortCode, FPortCury from "+ pub.yssGetTableName("Tb_Para_Portfolio"));
        bufSql.append(" where FCheckState = 1) d on bb.FPortCode = d.FPortCode");
        
        	
        rs = dbl.queryByPreparedStatement(bufSql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        if(rs.next()){
        	rs.beforeFirst();//将光标移动到此 ResultSet对象的开头
        	strNumDate = YssFun.formatDatetime(dDate).
            substring(0, 8);
           strNumDate = strNumDate +
            dbFun.getNextInnerCode(pub.yssGetTableName(
                "Tb_Data_Trade"),
                                   dbl.sqlRight("FNUM", 6),
                                   "000000",
                                   " where FNum like 'T"
                                   + strNumDate + "1%'", 1);
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
        
        while(rs.next()){
            //--------------------拼接交易编号---------------------
            sNum++;
            String tmp = "";
            for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                tmp += "0";
            }
            strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
            //------------------------end--------------------------//
          //-------------------------设置现金账户链接属性值----------------------
            cashacc.setYssPub(pub);
            cashacc.setLinkParaAttr( "", //投资经理
                                    rs.getString("PortCode"), //组合代码
                                    rs.getString("FSecurityCode1"), //证券代码
                                    "", //券商
                                     YssOperCons.YSS_JYLX_PX,    
                                    rs.getDate("FRecordDate"), //权益确认日
                                    rs.getString("FDividendCuryCode"), //分红币种
                                    YssOperCons.YSS_JYLX_PX); //交易类型为分红转投
            //--------------------------------------------------------------------
            //------------------------start--------------------------//
            dBaseRate = this.getSettingOper().getCuryRate(rs.
                getDate("InvestDate"),//转投日
                rs.getString("FDividendCuryCode"),//分红币种
                rs.getString("PortCode"),//组合代码
                YssOperCons.YSS_RATE_BASE);//获取基础汇率的值
            dPortRate = this.getSettingOper().getCuryRate(rs.
                getDate("InvestDate"),
                rs.getString("PortCury"),
                rs.getString("PortCode"),
                YssOperCons.YSS_RATE_PORT);//获取组合汇率的值
            //-----------------------end---------------------------------
            /********************************************************       
             *  若交易所为国内交易所，如交易所代码为：CG、CS、CY，即为国内业务，则获取登记日当天相关证券的库存数量作为权益数量，
             *  若交易所为国外交易所，即为QDII普通业务，则获取除权日前一天的库存数量作为权益数量
             */
            if("CS".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))||"CG".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))
                	||"CY".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))){
                	StorageDate = rs.getDate("FRecordDate");
                }else{
                	//除权日前一天
                	java.util.Date date = YssFun.parseDate(YssFun.formatDate(rs.getDate("FDividendDate"), "yyyy-MM-dd"));
                	StorageDate = YssFun.toSqlDate(YssFun.addDay(date, -1));
                }
            secSto = new SecurityStorageBean();//实例化
            secSto.setYssPub(pub);
            secSto = secSto.getStorageCost(StorageDate,
                    rs.getString("FSecurityCode"),//证券代码
                    this.sPortCode,//组合代码
                    (analy1 ?
                     rs.getString("FAnalysisCode1") :
                     " "),//分析代码1
                    (analy2 ?
                     rs.getString("FAnalysisCode2") :
                     " "),//分析代码2
                    "", "C",
                    rs.getString("FAttrClsCode")); //"C"为获取 核算成本
            if (secSto != null) {
                dSecurityCost = YssFun.toDouble(secSto.getStrStorageCost()); //为汇总的核算成本赋值
                dSecurityAmount = YssFun.toDouble(secSto.getStrStorageAmount()); //为汇总的库存数量赋值
            } else {
                dSecurityCost = 0.0;//为汇总的核算成本赋值
                dSecurityAmount = 0.0;//为汇总的库存数量赋值
            }
            CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
            pubPara.setYssPub(pub);//设置Pub
            String rightsRatioMethods=(String)pubPara.getRightsRatioMethods(rs.getString("FPortCode"));//获取通用参数值
            String ratioMethodsDetail = pubPara.getBRightsRatioMethods(rs.getString("FPortCode"),YssOperCons.YSS_JYLX_INVEST);//按权益类型获取权益比例方式 
            if(ratioMethodsDetail.length() > 0){
            	rightsRatioMethods = ratioMethodsDetail;
            }
            if (dSecurityAmount > 0) {//判断证券数量是否大于0
                dRightSub = this.getSettingOper().reckonRoundMoney(//分红权益=确认日库存数量*权益比例
                    rs.getString("FRoundCode") + "",
                    YssD.mul(dSecurityAmount,
                             (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio")?
                              rs.getDouble("FPreTaxRatio"):rs.getDouble("FAfterTaxRatio"))));//通过通用参数获取权益比例方式
                caBean = cashacc.getCashAccountBean();
                //======MS01626 QDV4赢时胜(测试)2010年8月20日02_B add by yangheng 2010.08.25
                if (caBean != null) {
                    strCashAccCode = caBean.getStrCashAcctCode();
                } else { //MS00173 当分红处理时没有现金帐户时提示用户 by leeyu 2009-01-09
                    throw new YssException("系统执行分红权益时出现异常！" + "\n" + "【" +
                        rs.getString("FSecurityCode") +
                        "】证券分红权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
                }
        //--------------------------------------------------
        subTrade = new TradeSubBean();			//实例化
        caBean = cashacc.getCashAccountBean();
        strCashAccCode = caBean.getStrCashAcctCode();//取现金帐户代码
               
        subTrade.setNum(strNumDate);			//为交易编号赋值
        subTrade.setSecurityCode(rs.getString("FSecurityCode1"));	//证券代码赋值
        subTrade.setPortCode(rs.getString("PortCode"));				//组合代码
        
        subTrade.setInvMgrCode("");				//投资经理
        subTrade.setBrokerCode((rs.getString("Broker")==null||rs.getString("Broker").equals("null"))?" ":rs.getString("Broker"));	//券商
        subTrade.setTradeCode(YssOperCons.YSS_JYLX_INVEST);	//交易类型
        subTrade.setTailPortCode(strCashAccCode);//尾差组合代码//现金帐户

        subTrade.setAllotProportion(0);			//分配比例
        subTrade.setOldAllotAmount(0);			//原始分配数量
        subTrade.setAllotFactor(0);				//分配因子
        subTrade.setBargainDate(YssFun.formatDate(rs.getDate("InvestDate")));	 //成交日期
        
        subTrade.setBargainTime("00:00:00");	//成交时间
        subTrade.setSettleDate(YssFun.formatDate(rs.getDate("InvestDate")));			//结算日期
        subTrade.setSettleTime("00:00:00");		//结算时间
        subTrade.setAutoSettle("1"); 			//自动结算
        subTrade.setPortCuryRate(dPortRate);	//组合汇率
        
        subTrade.setBaseCuryRate(dBaseRate);	//基础汇率
        subTrade.setTradeAmount(rs.getDouble("InvestNum"));		 //交易数量
        subTrade.setTradePrice(0);				//交易价格
        subTrade.setTradeMoney(0);				//交易金额
        
        subTrade.setAccruedInterest(0);	        //应计利息 
        subTrade.setDataSource(0);				//数据源
        subTrade.setDsType("HD_ZT");			//操作类型，表示系统操作数据，主要是和接口导入数据进行区分
        subTrade.checkStateId = 1;				//审核状态

        subTrade.creatorCode = pub.getUserCode();//创建人
        subTrade.checkTime = YssFun.formatDatetime(new java.util.Date());//审核时间
        subTrade.checkUserCode = pub.getUserCode();//审核人
        subTrade.creatorTime = YssFun.formatDatetime(new java.util.Date());//创建时间

        subTrade.setTotalCost(0);			//投资总成本
        subTrade.setSettleState("0");				//结算状态，未结算“0”
        subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate("InvestDate")));			//实际结算日期
        subTrade.setMatureDate("9998-12-31");		//到期日期
        
        subTrade.setMatureSettleDate("9998-12-31");		//到期结算日期
        subTrade.setFactCashAccCode(strCashAccCode);	//实际结算帐户
        subTrade.setCashAcctCode(strCashAccCode);		//设置现金账户
        subTrade.setFactSettleMoney(0);					//实际结算金额
        
        subTrade.setExRate(1);						//兑换汇率
        subTrade.setFactPortRate(dPortRate); 		//实际结算组合汇率
        subTrade.setFactBaseRate(dBaseRate);		//实际结算基础汇率
        
        tradeList=new ArrayList();
        tradeList.add(subTrade);					//放于tradeList中保存
        
        /**产生一笔收入数据 （用于冲减应收股利) */
        createSecPecPay(rs,dRightSub,subTrade);				  //产生一笔收入数据
           }
        }
        //add by songjie 2011.06.02 BUG QDV4赢时胜(测试)2011年5月26日04_B
        dbl.closeResultSetFinal(rs);
        rs = null;
       }
        return tradeList;
        }catch(Exception e){
        	e.printStackTrace();
        	throw new YssException("保存相应的交易数据到bean中出错",e);
        }finally{
        	//add by songjie 2011.06.02 BUG QDV4赢时胜(测试)2011年5月26日04_B
        	dbl.closeResultSetFinal(rs);
        }
	}
	/**
	 * 此方法主要保存数据到交易主表，和交易子表中
     * @param tradeDatas ArrayList 保存数据的集合
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
	 */
	public void saveTradeData(ArrayList tradeDatas,java.util.Date dDate,String sPortCode) throws YssException {
        Connection conn = null;
        boolean bTrans = true;
        try {
            if (tradeDatas != null && tradeDatas.size() > 0) {
                TradeDataAdmin tradeData = new TradeDataAdmin();	//交易数据操作类
                tradeData.setYssPub(pub);
                tradeData.addAll(tradeDatas);						//添加数据
                conn=dbl.loadConnection();							//获取连接
                conn.setAutoCommit(false); 							//设置为手动打开连接
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_SubTrade")); //给操作表加锁
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Trade")); 	//给操作表加锁
                TradeBean tradefilter = filterBean(dDate, sPortCode);
                if (tradefilter != null) {
                    tradeData.insert(YssFun.parseDate(tradefilter.getBargainDate())
                                     , YssFun.parseDate(tradefilter.getBargainDate())
                                     , sPortCode
                                     , tradefilter.getTradeCode()
                                     ,tradefilter.getDsType());		//保存数据
                }
                conn.commit();
                conn.setAutoCommit(true);
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
	
	/**
	 * 产生一笔收入数据（用于冲减应收股利)
	 * rs　
     * dRightSub 应收股利金额
	 * @throws YssException 
	 */
	public void createSecPecPay(ResultSet rs,double dRightSub,TradeSubBean subTrade) throws YssException{
		try {
			investMoney = rs.getDouble("InvestMoney");			//转投金额
			double changeMoney=rs.getDouble("ChangeMoney");		//调整金额
			
			TransferBean transfer = null;
			TransferSetBean transferSet = null;
	        ArrayList subTransfer = null;
	        
	        CashTransAdmin cashtransAdmin = null;
	        CashPecPayBean cashPecPayBean = null;
	        CashPayRecAdmin cashPayRecAdmin = null;
			
	        
            
	        this.investSumMoney=dRightSub-investMoney-changeMoney;//应收股利金额 - 转投金额 - 调整金额
			//产生资金调拨   ,金额为应收股利金额 - 转投金额 - 调整金额
			if(this.investSumMoney>0){
				cashtransAdmin = new CashTransAdmin(); //新建资金调拨控制对象
				cashtransAdmin.setYssPub(pub);
				
				transfer = setTransfer(rs,rs.getDate("InvestDate")); //获取资金调拨数据,调拨日期为转投日
            	transfer.setFRelaNum(rs.getString("Num")); 
            	transferSet = setTransferSet(rs,1,subTrade);  //获取资金调拨子数据, -1：方向为流出
            	subTransfer = new ArrayList();         //实例化放置资金调拨子数据的容器
            	subTransfer.add(transferSet);         //将资金调拨子数据放入容器
            	transfer.setSubTrans(subTransfer);   //将子数据放入资金调拨中
            	cashtransAdmin.addList(transfer);           
                    cashtransAdmin.insert(dDate, "DividendInvest", 1, rs.getString("Num")); //插入资金调拨,以调拨日期和关联编号类型,自动生成的来删除已有资金调拨
			}
			//产生一笔现金应收应付数据，金额为转投金额+资金调拨金额
	        cashPayRecAdmin = new CashPayRecAdmin();//现金应收应付
            cashPayRecAdmin.setYssPub(pub);
            this.investSumMoney+=investMoney;		//金额为转投金额
            cashPecPayBean = setInvestDateCash(rs,1,subTrade,YssOperCons.YSS_ZJDBLX_Income,"02DV"); 
            cashPecPayBean.setRelaNum(rs.getString("Num"));              //设置关联编号
            cashPayRecAdmin.addList(cashPecPayBean);
            if (cashPayRecAdmin.getList().size() > 0) {
                cashPayRecAdmin.insert(dDate, dDate, "02", "02DV", "", sPortCode, "", "", "", 0,true);//插入关联编号防止错误删除
            } 
			
			
			//调整金额”不为0或空,产生一笔现金应收数据
			if(changeMoney!=0){
				this.investSumMoney=changeMoney;   //金额为调整金额
				
				cashPayRecAdmin = new CashPayRecAdmin();					//现金应收应付
                cashPayRecAdmin.setYssPub(pub);
                
                cashPecPayBean = setInvestDateCash(rs,-1,subTrade,dRightSub,investMoney,changeMoney);         
                cashPecPayBean.setRelaNum(rs.getString("Num"));              //设置关联编号
                cashPayRecAdmin.addList(cashPecPayBean);
                
                if (cashPayRecAdmin.getList().size() > 0) {
                    cashPayRecAdmin.insert(dDate, dDate, "06", "06DV", "", sPortCode, "", "", "", 0,true);//插入关联编号防止错误删除
                }   
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new YssException("产生一笔收入数据出错",e);
		}			
	}
	
	/**
     * 设置转投日产生的现金应收应付对象，并返回此对象
     * @param rs ResultSet
     * @param inOut int   				-1:流出　1:流入
     * @param subTrade	TradeSubBean
     * @param TsfTypeCode String 		业务类型
     * @param SubTsfTypeCode String 	业务子类型
     * @return CashPecPayBean
     * @throws YssException
     */
    private CashPecPayBean setInvestDateCash(ResultSet rs,int inOut,
    										TradeSubBean subTrade,
    										String tsfTypeCode,
    										String subTsfTypeCode) throws YssException {
        double BaseCuryRate =subTrade.getBaseCuryRate(); //基础汇率
        double PortCuryRate = subTrade.getPortCuryRate();//组合汇率
        double Money = 0;
        double BaseMoney = 0;
        double PortMoney = 0;
        CashPecPayBean cashBean = new CashPecPayBean();  // 新建现金应收应付对象
        try {
            Money = this.investSumMoney;       			 //原币金额
            BaseMoney = this.getSettingOper().calBaseMoney(Money, BaseCuryRate);    				//计算基础货币金额
            PortMoney = this.getSettingOper().calPortMoney(Money, BaseCuryRate,
                PortCuryRate, rs.getString("FDividendCuryCode"), dDate, rs.getString("PortCode")); //计算组合货币金额

            cashBean.setBaseCuryRate(BaseCuryRate);
            cashBean.setPortCuryRate(PortCuryRate);
            cashBean.setMoney(Money); 								// 设置原币金额
            cashBean.setBaseCuryMoney(BaseMoney);
            cashBean.setPortCuryMoney(PortMoney);
            cashBean.setPortCode(rs.getString("PortCode"));
            cashBean.setCashAccCode(subTrade.getCashAcctCode());
            cashBean.setInOutType(inOut); 							// 流入流出
            cashBean.setCuryCode(rs.getString("FDividendCuryCode"));
            cashBean.setTradeDate(rs.getDate("InvestDate"));        // 设置业务日期
            cashBean.setTsfTypeCode(tsfTypeCode);    				// 设置业务类型为
            cashBean.setSubTsfTypeCode(subTsfTypeCode);             // 设置业务子类型为
            cashBean.checkStateId = 1;                              // 设置审核状态为已审核
        } catch (Exception e) {
            throw new YssException("设置转投日现金应收应付数据时出现异常！", e);
        }
        return cashBean;
    }
	/**
     * 设置转投日产生的现金应收应付对象，并返回此对象
     * 转投的金额通过轧差计算得到
     * @param rs ResultSet
     * @param inOut int   				-1:流出　1:流入
     * @param subTrade	TradeSubBean
     * @param dRightSub double 应收股利金额
     * @param investMoney 转投金额
     * @param changeMoney 调整金额
     * @return CashPecPayBean
     * @throws YssException
     */
    private CashPecPayBean setInvestDateCash(ResultSet rs,int inOut,
    										TradeSubBean subTrade,double dRightSub ,
    										double investsMoney,double changeMoney
    										) throws YssException {
        double BaseCuryRate =subTrade.getBaseCuryRate(); //基础汇率
        double PortCuryRate = subTrade.getPortCuryRate();//组合汇率
        double Money = 0;
        double BaseMoney = 0;
        double PortMoney = 0;
        
        double basedRightSub=0.0;
        double portdRightSub=0.0;
        
        double baseinvestMoney=0.0;
        double portinvestMoney=0.0;
        
        double baseTranMoney=0.0;
        double portTranMoney=0.0;
        CashPecPayBean cashBean = new CashPecPayBean();  // 新建现金应收应付对象
        try {
            double tranMoney = dRightSub-investsMoney-changeMoney;      //应收股利金额 - 转投金额- 调整金额
            basedRightSub = this.getSettingOper().calBaseMoney(dRightSub, BaseCuryRate);    	//计算基础货币金额
            portdRightSub = this.getSettingOper().calPortMoney(dRightSub, BaseCuryRate,
                PortCuryRate, rs.getString("FDividendCuryCode"), dDate, rs.getString("PortCode")); //计算组合货币金额
            
            baseinvestMoney = this.getSettingOper().calBaseMoney(investsMoney, BaseCuryRate);    //计算基础货币金额
            portinvestMoney = this.getSettingOper().calPortMoney(investsMoney, BaseCuryRate,
                PortCuryRate, rs.getString("FDividendCuryCode"), dDate, rs.getString("PortCode")); //计算组合货币金额
            
            baseTranMoney = this.getSettingOper().calBaseMoney(tranMoney, BaseCuryRate);    //计算基础货币金额
            portTranMoney = this.getSettingOper().calPortMoney(tranMoney, BaseCuryRate,
                PortCuryRate, rs.getString("FDividendCuryCode"), dDate, rs.getString("PortCode")); //计算组合货币金额
                  
            BaseMoney=basedRightSub-baseinvestMoney-baseTranMoney;
            PortMoney=portdRightSub-portinvestMoney-portTranMoney;                  
            
            cashBean.setBaseCuryRate(BaseCuryRate);
            cashBean.setPortCuryRate(PortCuryRate);
            cashBean.setMoney(changeMoney); 								// 设置原币金额
            cashBean.setBaseCuryMoney(BaseMoney);
            cashBean.setPortCuryMoney(PortMoney);
            cashBean.setPortCode(rs.getString("PortCode"));
            cashBean.setCashAccCode(subTrade.getCashAcctCode());
            cashBean.setInOutType(inOut); 							// 流入流出
            cashBean.setCuryCode(rs.getString("FDividendCuryCode"));
            cashBean.setTradeDate(rs.getDate("InvestDate"));        // 设置业务日期
            cashBean.setTsfTypeCode("06");    				// 设置业务类型为
            cashBean.setSubTsfTypeCode("06DV");             // 设置业务子类型为
            cashBean.checkStateId = 1;                              // 设置审核状态为已审核
        } catch (Exception e) {
            throw new YssException("设置转投日现金应收应付数据时出现异常！", e);
        }
        return cashBean;
    }
    /**
     * 产生的资金调拨数据对象，并返回此对象
     * @param rs ResultSet
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferBean setTransfer(ResultSet rs,Date date) throws YssException {
        TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(dDate);   //业务日期
            transfer.setDtTransferDate(date); //调拨日期为转投日期
            transfer.setStrTsfTypeCode("02");
            transfer.setStrSubTsfTypeCode("02DV");
            transfer.setFNumType("DividendInvest"); //设置关联编号类型为股票分红转投
            transfer.checkStateId = 1;
            transfer.setDataSource(1);
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
    }
    /**
     *  资金调拨数据子对象，并返回此对象  
     * @param rs ResultSet
     * inOut int 资金调拨流方向 -1:流出  1：流入
     * @param BaseCuryRate double 　基础汇率
     * @param PortCuryRate double　　组合汇率
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferSetBean setTransferSet(ResultSet rs,int inOut,TradeSubBean subTrade)
    	throws YssException {
        TransferSetBean transferSet = null;
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = subTrade.getBaseCuryRate();				//基础汇率
            double dPortRate = subTrade.getPortCuryRate();				//组合汇率

            transferSet.setIInOut( inOut);                              //资金调拨流方向 -1:流出  1：流入
            transferSet.setSPortCode(rs.getString("PortCode"));
            transferSet.setSCashAccCode(subTrade.getCashAcctCode());  	//设置现金账户
            transferSet.setDMoney(this.investSumMoney);            		//设置金额
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
        return transferSet; //返回资金调拨子数据
    }

    /**
     * 删除条件，设置删除交易主子表数据的条件
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return TradeBean 返回值
     */
    public TradeBean filterBean(java.util.Date dDate, String sPortCode) {
        TradeBean trade = new TradeBean();
        trade.setTradeCode(YssOperCons.YSS_JYLX_INVEST);	//交易方式为分红转投
        trade.setPortCode(sPortCode);						//组合代码
        trade.setBargainDate(YssFun.formatDate(dDate));		//成交日期
        trade.setDsType("HD_ZT");							//操作类型，表示此数据是界面输入的数据
        return trade;
    }
    
    
	
}
