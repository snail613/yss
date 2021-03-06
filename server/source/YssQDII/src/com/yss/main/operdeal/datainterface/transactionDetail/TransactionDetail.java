package com.yss.main.operdeal.datainterface.transactionDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.main.datainterface.DaoPretreatBean;
import com.yss.main.operdata.RateTradeBean;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.storagemanage.CashStorageBean;
import com.yss.main.syssetting.DataDictBean;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/************************************************************
 * 
 * @author benson
 * @date 2010.07.01
 */
public class TransactionDetail extends DataBase {

	//~Properties 
	private DaoPretreatBean daoPretreat = null;
	private DaoCusConfigureBean cusCfg  = null;
	private HashMap PortCodeList = null;
	private HashMap AccountList = null;
	public TransactionDetail(){
		
	}
	
	private void initDate() throws YssException{
    	cusCfg = new DaoCusConfigureBean();
    	cusCfg.setYssPub(pub);
    	cusCfg.setCusCfgCode(cusCfgCode);
    	cusCfg.getSetting();
    	daoPretreat = new DaoPretreatBean();
    	daoPretreat.setYssPub(pub);
    	daoPretreat.setDPDsCode(cusCfg.getDPCodes());
    	daoPretreat.getSetting();
    }
	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 分 割 线 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	
	
	/**
     * 入口方法
     * @throws YssException
     */
    public void inertData() throws YssException {
    	//1. 初始化属性
    	   initDate();
    	//2. 删除目标表数据
    	   delTgtTabData();
    	//3. 插入数据
    	   insertTgtTabData();
    }
    
    /**
     * 删除目标表数据
     * 注意：如果不设置删除条件，就不进行数据的删除操作
     * @param pret
     * @throws YssException
     */
    private void delTgtTabData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String delSql = ""; //删除目标表的sql语句
       // List dsTabSql = null; 
        String TemTab = "";//临时表
        String targetTab = ""; //目标表
        StringBuffer whereSqlBuf = new StringBuffer();
        String whereSql = "";
        Connection conn = null;
        boolean bTrans = true;
        int iTabType = 0;
        try {
        	TemTab = cusCfg.getTabName();
        	targetTab = daoPretreat.getTargetTabCode();
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            //1. 获取目标表删除条件
            strSql = " select * from " +
                pub.yssGetTableName("Tb_Dao_TgtTabCond") +
                " where FDPDsCode=" + dbl.sqlString(daoPretreat.getDPDsCode()) +
                " and FCheckState=1" +
                " order by FOrderIndex ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            		whereSqlBuf.append("a.").append(rs.getString("FTargetField")).append("=")
                               .append("b.").append(rs.getString("FDsField")).append(" and ");

            }
            if (whereSqlBuf.length() > 5) {
                whereSql = whereSqlBuf.toString().substring(0,whereSqlBuf.toString().length() - 5);

            }
                       
            if (whereSql.length() > 0) {
            	
            //add by lidaolong 20110415 #758 希望开发重读TA数据、交易数据、外汇数据后，将原资金调拨覆盖功能           
            	//删除资金调拨子数            	
            	delSql = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +	
            	   " where fnum in (select fnum from "+ pub.yssGetTableName("Tb_Cash_Transfer") +	
    			   " where FRATETRADENUM in( select fnum from " + pub.yssGetTableName(targetTab) + " a " +
    						" where exists (select * from " + TemTab + " b " + " where " + whereSql + ")))"; 
            	dbl.executeSql(delSql);
            	
               	//删除资金调拨数据
            	delSql = "delete from " +pub.yssGetTableName("Tb_Cash_Transfer")+	
            			" where FRATETRADENUM in( select fnum from " + pub.yssGetTableName(targetTab) + " a " +
            						" where exists (select * from " + TemTab + " b " + " where " + whereSql + "))"; 
            	dbl.executeSql(delSql);
            //end by lidaolong
            	            	
				delSql = " delete from " + pub.yssGetTableName(targetTab) + " a " + " where exists (select * from " + TemTab + " b " + " where " + whereSql + ")"; //modify by fangjiang 2010.11.15 bug 410
				dbl.executeSql(delSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("【导入彭博外汇交易数据：删除目标表数据出错！】", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn,bTrans);
        }
    }
    
    
    /**
     * 根据预处理的数据源插入数据到目标表
     * @param pret
     * @throws YssException
     */
    private void insertTgtTabData() throws YssException {
    	String strSql = "";
    	ArrayList addList = null;
    	RateTradeBean rateTrade = null;
    	Connection conn = null;
    	PreparedStatement pst = null;
        boolean bTrans = true;
        
        try{
        	strSql = "insert into " + pub.yssGetTableName("Tb_Data_RateTrade") + 
        	"(FNum, FTradeDate,FTradeTime,FPortCode,FBPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FBAnalysisCode1,FBAnalysisCode2,FBAnalysisCode3, " +
        	" FBCashAccCode,FSCashAccCode,FReceiverCode,FPayCode,FSettleTime, FSettleDate,FBSettleTime, FBSettleDate,FTradeType,FCatType," +
        	" FExCuryRate,FLongCuryRate,FBMoney,FSMoney,FBaseMoney,FPortMoney,FRateFx,FUpDown,FDesc,FBCuryCode,FSCuryCode,FBCuryFee,FSCuryFee,FDataSource," +
        	" FCheckState, FCreator, FCreateTime,FCheckUser) " + 
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        	//MS01409 去掉字段FRateTradeType by fangjiang 2010.07.23
        	conn = dbl.loadConnection();
        	pst = conn.prepareStatement(strSql);
        	conn.setAutoCommit(false);//设置为手动提交事物
        	addList = setResultSetAttr();

        	for (int i = 0; i < addList.size(); i++) {
        		rateTrade = (RateTradeBean )addList.get(i);
        		
        		pst.setString(1, rateTrade.getNum());
        		pst.setDate(2, (java.sql.Date)rateTrade.getTradeDate());
        		pst.setString(3, "00:00:00");
        		pst.setString(4, rateTrade.getPortCode());
        		pst.setString(5, rateTrade.getbPortCode());
        		pst.setString(6, " ");
        		pst.setString(7, " ");
        		pst.setString(8, " ");
        		pst.setString(9, " ");
        		pst.setString(10," ");
        		pst.setString(11," ");
        		pst.setString(12, rateTrade.getBCashAccCode());
        		pst.setString(13, rateTrade.getSCashAccCode());
        		pst.setString(14, "");
        		pst.setString(15, "");
        		pst.setString(16, "00:00:00");
        		pst.setDate(17, (java.sql.Date)rateTrade.getSettleDate());
        		pst.setString(18, "00:00:00");
        		pst.setDate(19, (java.sql.Date)rateTrade.getSettleDate());
        		pst.setString(20, rateTrade.getTradeType());
        		pst.setString(21, rateTrade.getCatType());
        		//pst.setString(22, rateTrade.getRateTradeType());
        		//MS01409 QDV4赢时胜(30上线测试)2010年7月6日07_AB modify by fangjiang 2010.07.23
				//pst的调整序号
        		pst.setDouble(22, rateTrade.getExCuryRate());
        		pst.setDouble(23, rateTrade.getLingCuryRate());
        		pst.setDouble(24, rateTrade.getBMoney());
        		pst.setDouble(25, rateTrade.getSMoney());
        		pst.setDouble(26, rateTrade.getBaseMoney());
        		pst.setDouble(27, rateTrade.getPortMoney());
        		pst.setDouble(28, rateTrade.getRateFx());
        		pst.setDouble(29, rateTrade.getUpDown());
        		pst.setString(30, "");
        		pst.setString(31, rateTrade.getBCuryCode());
        		pst.setString(32, rateTrade.getSCuryCode());
        		pst.setDouble(33, rateTrade.getBCuryFee());
        		pst.setDouble(34, rateTrade.getSCuryFee());
        		pst.setString(35, "ZD");
        		pst.setString(36, "1");
        		pst.setString(37, pub.getUserCode());
        		pst.setString(38, YssFun.formatDatetime(new java.util.Date()));
        		pst.setString(39, pub.getUserCode());
				//MS01409 QDV4赢时胜(30上线测试)2010年7月6日07_AB modify by fangjiang 2010.07.23
        		
        		pst.addBatch();//增加批处理
        		
       		    createSavCashTrans(rateTrade);
        	}
        	
        	pst.executeBatch();//执行批处理
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
        }catch(Exception e){
        	throw new YssException("【导入彭博外汇交易数据：数据插入目标表出错！】");
        }finally{
        	dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
        }
    }
    
    
    private ArrayList setResultSetAttr()throws YssException{
		String strSql = "";
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		String strNumberDate = "";
		String Num = "";
		String portcode = "";
		String bPortcode = "";
		String sAccount = "";
		String bAccount = "";
		java.util.Date dTradeDate = null;
		java.util.Date dSettleDate = null;
		double dBMoney = 0;
		double dSMoney = 0;
		try {
			
        	PortCodeList = getPortCodes();//获取组合代码列表
        	AccountList = getCashAccounts();
			strSql = " select BuyAccountNum,BuyAmount,BuyCurrencyCode,Rate,SellAccountNum,SellAmount,SellCurrencyCode,"
					+ " TradeDate,ValueDate from tmp_bloomberg_transaction";

			rs = dbl.openResultSet(strSql);
			int i =0;
			String sNum = "";
			while (rs.next()) {
                
				RateTradeBean rateTrade = new RateTradeBean();
				/*******************************************************
				 * 因为数据插入采用批处理插入，通过查询库是不能达到递增的效果
				 * 所以递增需要在这里控制。
				 * 
				 * 1. 第一次先查询库获取库中Fnum最大值。
				 * 2. 生成第二个Fnum时则通过在原先的Fnum基础上递增了
				 */
				strNumberDate = YssFun.formatDate(rs.getDate("ValueDate"),YssCons.YSS_DATETIMEFORMAT).substring(0, 8);
				if(i==0){
					sNum = dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_RateTrade"),dbl.sqlRight("FNum", 6), "000001"," where FNum like 'T" + strNumberDate+ "%'", 1);
				}else{
					String s = Integer.parseInt(sNum)+i+"";
					String s1 = s;
					for(int j=1;j<=6-s.length();j++){
						s1 =0+s1;
					}
					sNum = s1;
				}
				i++;
				Num = "T"+ strNumberDate+ sNum;


				dTradeDate = rs.getDate("TradeDate");
				dSettleDate = rs.getDate("ValueDate");

				portcode = (String) PortCodeList.get(rs.getString("SellAccountNum"));
				bPortcode = (String) PortCodeList.get(rs.getString("BuyAccountNum"));
				sAccount = (String) AccountList.get(rs.getString("SellAccountNum"));
				bAccount = (String) AccountList.get(rs.getString("BuyAccountNum"));
				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
				rateTrade.setNum(Num);// 交易编号，系统自动生成
				rateTrade.setPortCode(portcode);// 卖出组合代码根据卖出银行账户关联
				rateTrade.setBPortCode(bPortcode);// 买入组合代码根据买入银行账户关联
				rateTrade.setTradeType("0");// 交易类型，默认为0
				rateTrade.setCatType("0");// 品种类型，默认为0
				rateTrade.setBCashAccCode(bAccount);// 买入现金账户对应的是【现金账户设置界面的"银行账号"】
				rateTrade.setSCashAccCode(sAccount);// 卖出现金账户对应的是【现金账户设置界面的"银行账号"】
				rateTrade.setBCuryCode(rs.getString("BuyCurrencyCode"));
				rateTrade.setSCuryCode(rs.getString("SellCurrencyCode"));
				rateTrade.setTradeDate(dTradeDate);
				rateTrade.setSettleDate(dSettleDate);
				rateTrade.setBSettleDate(dSettleDate);
				rateTrade.setBMoney(rs.getDouble("BuyAmount"));
				rateTrade.setSMoney(rs.getDouble("SellAmount"));
				rateTrade.setExCuryRate(rs.getDouble("Rate"));
				rateTrade.setBCuryFee(0);// 买入货币交易费用
				rateTrade.setSCuryFee(0);// 卖出货币交易费用
				rateTrade.setLingCuryRate(0);// 远期交易汇率，默认为0
				rateTrade.setUpDown(0);// 升水点，默认为0
				//rateTrade.setRateTradeType("1");// 外汇类型，默认为购汇
				//MS01409 QDV4赢时胜(30上线测试)2010年7月6日07_AB modify by fangjiang 2010.07.23
				rateTrade = costfx(rateTrade);// 计算组合货币金额，基础货币金额，汇兑损益
				list.add(rateTrade);
			}
			return list;
		} catch (Exception e) {
			throw new YssException("【导入彭博外汇交易数据：设置外汇交易属性出错！】");
		}
    }
    
    
    private RateTradeBean costfx (RateTradeBean rateTrade) throws YssException{
		String sResult = "";
		double dScale = 0;
		CashStorageBean cashStg = null;
		double accBaseMoney = 0;
		double accPortMoney = 0;
		double dhdsy = 0;
		double dBBaseRate = 1; // 买入货币基础汇率
		double dSBaseRate = 1; // 卖出汇率基础汇率
		double dPortRate = 1; // 组合汇率
		String strReturnMoney = "0";
		double dBSetMoney = 0; // 买入本位币金额
		double dSSetMoney = 0; // 卖出本位币金额
		try {
			cashStg = operFun.getCashAccStgForRateTrade(rateTrade.getTradeDate(), rateTrade.getSCashAccCode(), 
					rateTrade.getPortCode(), "", "", ""); // 使用外汇交易专用的获取余额的方式。
			if (cashStg == null) {
				rateTrade.setBaseMoney(0);
				rateTrade.setPortMoney(0);
				rateTrade.setRateFx(0);
				return rateTrade;
			}
			if (YssFun.toDouble(cashStg.getStrAccBalance()) != 0) {
				dScale = YssD.div(rateTrade.getSMoney(), YssFun.toDouble(cashStg.getStrAccBalance()));
			}
			accBaseMoney = YssD.round(YssD.mul(YssFun.toDouble(cashStg.getStrBaseCuryBal()), dScale), 2);
			accPortMoney = YssD.round(YssD.mul(YssFun.toDouble(cashStg.getStrPortCuryBal()), dScale), 2);

			PortfolioBean port = new PortfolioBean();
			port.setYssPub(pub);
			port.setPortCode(rateTrade.getPortCode());
			port.getSetting();

			if (rateTrade.getBCuryCode().equalsIgnoreCase(port.getCurrencyCode())&& !rateTrade.getSCuryCode().equalsIgnoreCase(port.getCurrencyCode())) 
			{
				// 1. 当买入货币是组合货币时计算汇兑损益
				dhdsy = YssD.round(YssD.sub(rateTrade.getBMoney(), accPortMoney), 2); // 汇兑损益的计算方法是 流入货币－计算出的组合货币成本
			} else if (rateTrade.getSCuryCode().equalsIgnoreCase(port.getCurrencyCode())&& !rateTrade.getBCuryCode().equalsIgnoreCase(port.getCurrencyCode())) {
				// 当卖出货币是组合货币时计算汇兑损益
				dBBaseRate = this.getSettingOper().getCuryRate(rateTrade.getTradeDate(),rateTrade.getBCuryCode(), rateTrade.getPortCode(), YssOperCons.YSS_RATE_BASE);
				dhdsy = YssD.sub(YssD.round(YssD.mul(rateTrade.getBMoney(), dBBaseRate), 2),YssD.round(YssD.mul(rateTrade.getBMoney(), rateTrade.getExCuryRate()), 2)); // 汇兑损益的计算方法是
																			// 买入货币金额*中间价汇率(汇率数据表中的汇率)-买入货币金额*交易汇率.
			} else if (!rateTrade.getSCuryCode().equalsIgnoreCase(port.getCurrencyCode())&& !rateTrade.getBCuryCode().equalsIgnoreCase(port.getCurrencyCode())) { 
				// 当卖出货币和买入货币都不是组合货币时计算汇兑损益
				dBBaseRate = this.getSettingOper().getCuryRate(rateTrade.getTradeDate(),rateTrade.getBCuryCode(), rateTrade.getPortCode(), YssOperCons.YSS_RATE_BASE);
				dSBaseRate = this.getSettingOper().getCuryRate(rateTrade.getTradeDate(),rateTrade.getSCuryCode(), rateTrade.getPortCode(), YssOperCons.YSS_RATE_BASE);
				dPortRate = this.getSettingOper().getCuryRate(rateTrade.getTradeDate(),port.getCurrencyCode(), rateTrade.getPortCode(),YssOperCons.YSS_RATE_PORT);
				dBSetMoney = this.getSettingOper().calPortMoney(rateTrade.getBMoney(),dBBaseRate, dPortRate, // 计算买入货币本位币金额
						     rateTrade.getBCuryCode(), rateTrade.getTradeDate(), rateTrade.getPortCode());
				dSSetMoney = this.getSettingOper().calPortMoney(rateTrade.getSMoney(),
						dSBaseRate, dPortRate, // 计算卖出货币本位币金额
						rateTrade.getSCuryCode(), rateTrade.getTradeDate(), rateTrade.getPortCode());
				dhdsy = YssD.sub(dSSetMoney, dBSetMoney);
			}
			rateTrade.setBaseMoney(accBaseMoney);
			rateTrade.setPortMoney(accPortMoney);
			rateTrade.setRateFx(dhdsy);

			return rateTrade;
		} catch (Exception e) {
			throw new YssException("【导入彭博外汇交易数据：计算汇兑损益出错！】");
		}
    }
    
    /*****************************************
     * 预先将组合代码存储到HashMap中。
     * 通过银行账户关联组合代码
     * @return 
     * @throws YssException
     */
    private HashMap getPortCodes() throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	HashMap portList = new HashMap();
    	try{
    		strSql = " select fbankaccount,fportcode from "+pub.yssGetTableName("tb_para_cashaccount")+" a  "+
    		         " where exists (select distinct fcashacccode from (select BuyAccountNum as fcashacccode from tmp_bloomberg_transaction "+
    		         " union all select SellAccountNum as fcashacccode from tmp_bloomberg_transaction )b where a.fbankaccount=b.fcashacccode )";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			portList.put(rs.getString("fbankaccount"),rs.getString("fportcode"));
    		}
    		return portList;
    	}catch(Exception e){
    		throw new YssException("【导入彭博外汇交易数据：通过银行账户关联组合代码出错！】");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    
    private HashMap getCashAccounts() throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	HashMap CashAccountsList = new HashMap();
    	try{
    		strSql = " select fbankaccount,fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")+" a  "+
    		         " where exists (select distinct fcashacccode from (select BuyAccountNum as fcashacccode from tmp_bloomberg_transaction "+
    		         " union all select SellAccountNum as fcashacccode from tmp_bloomberg_transaction )b where a.fbankaccount=b.fcashacccode )";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			CashAccountsList.put(rs.getString("fbankaccount"),rs.getString("fcashacccode"));
    		}
    		return CashAccountsList;
    	}catch(Exception e){
    		throw new YssException("【导入彭博外汇交易数据：通过银行账户关联组合代码出错！】");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
      public void createSavCashTrans(RateTradeBean rateTradeBean) throws YssException {
        EachRateOper rateIn = null;     //定义取资金调拨的流入汇率方法　QDV4赢时胜（上海）2009年04月15日01_B MS00382
        EachRateOper rateOut = null;    //定义取资金调拨的流出汇率方法　QDV4赢时胜（上海）2009年04月15日01_B MS00382
        PortfolioBean bPort = new PortfolioBean();
        bPort.setYssPub(pub);
        bPort.setPortCode(rateTradeBean.getbPortCode());
        bPort.getSetting();

        PortfolioBean sPort = new PortfolioBean();
        sPort.setYssPub(pub);
        sPort.setPortCode(rateTradeBean.getPortCode());
        sPort.getSetting();
        
        CtlPubPara pubpara = new CtlPubPara();// add by wangzuochun 2010.06.08 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB
        pubpara.setYssPub(pub); // add by wangzuochun 2010.06.08 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB

        TransferBean tran = new TransferBean();
        TransferSetBean transfersetIn = new TransferSetBean();
        TransferSetBean transfersetOut = new TransferSetBean();
        TransferSetBean transfersetInFee = new TransferSetBean();
        TransferSetBean transfersetOutFee = new TransferSetBean();
        ArrayList tranSetList = new ArrayList();

        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);

        //增加资金调拨记录
        tran.setYssPub(pub);
        tran.setDtTransDate(rateTradeBean.getTradeDate()); //存入时间
        tran.setDtTransferDate(rateTradeBean.getBSettleDate());
        tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
        tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_RateTrade);
        tran.setStrTransferTime("00:00:00");
        tran.setDataSource(1);  //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
        tran.setRateTradeNum(rateTradeBean.getNum());

        tran.checkStateId = 1;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss");
        tran.setDataSource(1); //这里应为自动标记
        //资金流入帐户
        transfersetIn.setDMoney(rateTradeBean.getBMoney());
        transfersetIn.setSPortCode(rateTradeBean.getbPortCode());
        transfersetIn.setSAnalysisCode1(" ");
        transfersetIn.setSAnalysisCode2(" ");

        if (rateTradeBean.getBCuryCode().equalsIgnoreCase(bPort.getCurrencyCode()) &&
            !rateTradeBean.getSCuryCode().equalsIgnoreCase(bPort.getCurrencyCode())) {
            //当买入货币是组合货币时,流入汇率采用买入货币金额进行计算fazmm20071008
            //判断一下，若baseMoney为0时，则直接取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416    	
        	if (rateTradeBean.getBaseMoney()== 0.0D) {
                rateIn = new EachRateOper();
                rateIn.setYssPub(pub);
                rateIn.getInnerPortRate(rateTradeBean.getTradeDate(), rateTradeBean.getBCuryCode(), rateTradeBean.getbPortCode());
                transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(rateTradeBean.getTradeDate(),
                		rateTradeBean.getBCuryCode(), rateTradeBean.getbPortCode(), YssOperCons.YSS_RATE_BASE));
                transfersetIn.setDPortRate(rateIn.getDPortRate());
            } else {
                transfersetIn.setDBaseRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getBMoney()));
                transfersetIn.setDPortRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getBMoney()));
            }
        } else if (rateTradeBean.getSCuryCode().equalsIgnoreCase(bPort.getCurrencyCode()) &&
                   !rateTradeBean.getBCuryCode().equalsIgnoreCase(bPort.getCurrencyCode())) {
            //当卖出货币是组合货币时,流入汇率采用卖出货币金额计算fazmm20071008
            //判断，当baseMoney为０时，则直接取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
            //------ modify by wangzuochun 2010.06.08 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB
        	String strMode = pubpara.getRateTradeMode();
        	
        	if (strMode != null && strMode.length() > 0 && strMode.equals("1")){
        		rateIn = new EachRateOper();
        		rateIn.setYssPub(pub);
        		rateIn.getInnerPortRate(rateTradeBean.getTradeDate(), rateTradeBean.getBCuryCode(), rateTradeBean.getbPortCode());
        		transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(rateTradeBean.getTradeDate(), rateTradeBean.getBCuryCode(), rateTradeBean.getbPortCode(), YssOperCons.YSS_RATE_BASE));
        		transfersetIn.setDPortRate(rateIn.getDPortRate());
        	}
        	else if (strMode != null && strMode.length() > 0 && strMode.equals("0")){
        		transfersetIn.setDBaseRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getBMoney()));
        		transfersetIn.setDPortRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getBMoney()));
        	}
        	else{
        		if (rateTradeBean.getBaseMoney() == 0.0D) {
        			rateIn = new EachRateOper();
        			rateIn.setYssPub(pub);
        			rateIn.getInnerPortRate(rateTradeBean.getTradeDate(), rateTradeBean.getBCuryCode(), rateTradeBean.getbPortCode());
        			transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(rateTradeBean.getTradeDate(), rateTradeBean.getBCuryCode(), rateTradeBean.getbPortCode(), YssOperCons.YSS_RATE_BASE));
        			transfersetIn.setDPortRate(rateIn.getDPortRate());
        		} else {
        			transfersetIn.setDBaseRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getBMoney()));
        			transfersetIn.setDPortRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getBMoney()));
        		}
        	}
        	//-------------------------------MS01135 ----------------------------------//
        } else if (!rateTradeBean.getSCuryCode().equalsIgnoreCase(bPort.getCurrencyCode()) &&
                   !rateTradeBean.getBCuryCode().equalsIgnoreCase(bPort.getCurrencyCode())) {
            //当卖出货币和买入货币都不是组合货币时,采用日终汇率做本位币成本fazmm20071008
            transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(rateTradeBean.getTradeDate(), rateTradeBean.getBCuryCode(), rateTradeBean.getbPortCode(), YssOperCons.YSS_RATE_BASE));

            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
            rateIn = new EachRateOper();
            rateIn.setYssPub(pub);
            rateIn.getInnerPortRate(rateTradeBean.getTradeDate(), bPort.getCurrencyCode(), rateTradeBean.getPortCode());
            transfersetIn.setDPortRate(rateIn.getDPortRate());
            //----------------------------------------------------------------------
        }
        //QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
        if (rateTradeBean.getBCuryCode().equalsIgnoreCase(pub.getPortBaseCury(rateTradeBean.getPortCode()))) { //如果买入货币与基础货币相同基础汇率就赋值为１// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
            transfersetIn.setDPortRate(YssD.div(transfersetIn.getDPortRate(), transfersetIn.getDBaseRate())); //xuqiji 20090615:QDV4交银施罗德2009年6月10日01_B  MS00495 外汇交易业务美元应收换汇拆借款本位币成本不正确
            transfersetIn.setDBaseRate(1.0D);
        }
        //QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
        if (bPort.getCurrencyCode().equalsIgnoreCase(rateTradeBean.getBCuryCode())) { //如果组合货币币种和买入货币币种相同组合汇率就取１ //MS00566 QDV4赢时胜（上海）2009年7月13日01_B sj 将bPortCode转换为bCuryCode，及买入货币
            transfersetIn.setDBaseRate(YssD.div(transfersetIn.getDBaseRate(),transfersetIn.getDPortRate()));//重新计算基础汇率金额
            transfersetIn.setDPortRate(1.0D);
        }
        transfersetIn.setSCashAccCode(rateTradeBean.getBCashAccCode());
        transfersetIn.setIInOut(1);
        transfersetIn.checkStateId = 1;

        //费用帐户
        if (rateTradeBean.getBCuryFee() != 0) {
            transfersetInFee.setDMoney(rateTradeBean.getBCuryFee());
            transfersetInFee.setSPortCode(rateTradeBean.getbPortCode());
            transfersetInFee.setSAnalysisCode1("");
            transfersetInFee.setSAnalysisCode2("");
            transfersetInFee.setDBaseRate(this.getSettingOper().getCuryRate(
            		rateTradeBean.getTradeDate(), rateTradeBean.getBCuryCode(), rateTradeBean.getbPortCode(), YssOperCons.YSS_RATE_BASE));

            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
            rateIn = new EachRateOper();
            rateIn.setYssPub(pub);
            rateIn.getInnerPortRate(rateTradeBean.getTradeDate(), bPort.getCurrencyCode(), rateTradeBean.getbPortCode());
            transfersetInFee.setDPortRate(rateIn.getDPortRate());
            //----------------------------------------------------------------------

            transfersetInFee.setSCashAccCode(rateTradeBean.getBCashAccCode());
            transfersetInFee.setIInOut( -1);
            transfersetInFee.checkStateId = 1;
        }

        //如果现金流入结算日期跟现金流出结算日期不一致时，需要处理成两条资金调拨数据fazmm20071020
        if (YssFun.dateDiff(rateTradeBean.getSettleDate(), rateTradeBean.getBSettleDate()) != 0) {
            tranSetList.add(transfersetIn);
            tranSetList.add(transfersetInFee);
            tranAdmin.addList(tran, tranSetList);
            tranAdmin.insert(rateTradeBean.getNum(), true);

            tranSetList = new ArrayList();
            tran = new TransferBean();
            tranAdmin = new CashTransAdmin();
            tranAdmin.setYssPub(pub);

            //增加资金调拨记录
            tran.setYssPub(pub);
            tran.setDtTransDate(rateTradeBean.getTradeDate()); //存入时间
            tran.setDtTransferDate(rateTradeBean.getSettleDate());
            tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
            tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_RateTrade);
            tran.setStrTransferTime("00:00:00");
            tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
            tran.setRateTradeNum(rateTradeBean.getNum());
            tran.checkStateId = 1;
            tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                                 "yyyyMMdd HH:mm:ss");
            tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
        }

        //资金流出帐户
        transfersetOut.setDMoney(rateTradeBean.getSMoney());
        transfersetOut.setSPortCode(rateTradeBean.getPortCode());
        transfersetOut.setSAnalysisCode1("");
        transfersetOut.setSAnalysisCode2("");
        //不保留位数，根据数据表的汇率字段小数位保留fazmm20071001

        if (rateTradeBean.getBCuryCode().equalsIgnoreCase(sPort.getCurrencyCode()) &&
            !rateTradeBean.getSCuryCode().equalsIgnoreCase(sPort.getCurrencyCode())) {
            //当买入货币是组合货币时,本位币成本采用买入货币金额
            //当baseMoney等于０时，应取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
            if (rateTradeBean.getBaseMoney() == 0.0D) {
                rateOut = new EachRateOper();
                rateOut.setYssPub(pub);
                rateOut.getInnerPortRate(rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode());
                transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
                		rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode(), YssOperCons.YSS_RATE_BASE));
                transfersetOut.setDPortRate(rateOut.getDPortRate());
            } else {
                transfersetOut.setDBaseRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getSMoney()));
                transfersetOut.setDPortRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getSMoney()));
            }
        } else if (rateTradeBean.getSCuryCode().equalsIgnoreCase(sPort.getCurrencyCode()) &&
                   !rateTradeBean.getBCuryCode().equalsIgnoreCase(sPort.getCurrencyCode())) {
            //当卖出货币是组合货币时按照卖出货币金额做成本
            //当baseMoney等于０时，取当日的汇率 QDV4赢时胜（上海）2009年04月15日01_B MS00382 by leeyu 20090416
        	
        	//------ modify by wangzuochun 2010.06.08 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB
        	String strMode = pubpara.getRateTradeMode();
        	
        	if (strMode != null && strMode.length() > 0 && strMode.equals("1")){
        		rateOut = new EachRateOper();
    			rateOut.setYssPub(pub);
    			rateOut.getInnerPortRate(rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode());
    			transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
    					rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode(), YssOperCons.YSS_RATE_BASE));
    			transfersetOut.setDPortRate(rateOut.getDPortRate());
        	}
        	else if (strMode != null && strMode.length() > 0 && strMode.equals("0")){
        		transfersetOut.setDBaseRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getSMoney()));
    			transfersetOut.setDPortRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getSMoney()));
        	}
        	else{
        		
        		if (rateTradeBean.getBaseMoney() == 0.0D) {
        			rateOut = new EachRateOper();
        			rateOut.setYssPub(pub);
        			rateOut.getInnerPortRate(rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode());
        			transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(
        					rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode(), YssOperCons.YSS_RATE_BASE));
        			transfersetOut.setDPortRate(rateOut.getDPortRate());
        		} else {
        			transfersetOut.setDBaseRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getSMoney()));
        			transfersetOut.setDPortRate(YssD.div(rateTradeBean.getBaseMoney(), rateTradeBean.getSMoney()));
        		}
        	}
        	//---------------------------------MS01135-----------------------------------//
        } else if (!rateTradeBean.getSCuryCode().equalsIgnoreCase(sPort.getCurrencyCode()) &&
                   !rateTradeBean.getBCuryCode().equalsIgnoreCase(sPort.getCurrencyCode())) {
            //当卖出货币和买入货币都不是组合货币时计算汇兑损益,转出账户的本位币成本按照日终汇率处理fazmm20071008
            transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode(), YssOperCons.YSS_RATE_BASE));

            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
            rateIn = new EachRateOper();
            rateIn.setYssPub(pub);
            rateIn.getInnerPortRate(rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode());
            transfersetOut.setDPortRate(rateIn.getDPortRate());
            //----------------------------------------------------------------------
        }

        transfersetOut.setSCashAccCode(rateTradeBean.getSCashAccCode());
        transfersetOut.setIInOut( -1);
        transfersetOut.checkStateId = 1;

        if (rateTradeBean.getSCuryFee() != 0) {
            transfersetOutFee.setDMoney(rateTradeBean.getSCuryFee());
            transfersetOutFee.setSPortCode(rateTradeBean.getPortCode());
            transfersetOutFee.setSAnalysisCode1("");
            transfersetOutFee.setSAnalysisCode2("");
            transfersetOutFee.setDBaseRate(this.getSettingOper().getCuryRate(
            		rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode(), YssOperCons.YSS_RATE_BASE));

            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 -------------
            rateIn = new EachRateOper();
            rateIn.setYssPub(pub);
            rateIn.getInnerPortRate(rateTradeBean.getTradeDate(), rateTradeBean.getSCuryCode(), rateTradeBean.getPortCode());
            transfersetOutFee.setDPortRate(rateIn.getDPortRate());
            //----------------------------------------------------------------------

            transfersetOutFee.setSCashAccCode(rateTradeBean.getSCashAccCode());
            transfersetOutFee.setIInOut( -1);
            transfersetOutFee.checkStateId = 1;
        }

        tranSetList.add(transfersetOut);
        tranSetList.add(transfersetOutFee);
        if (YssFun.dateDiff(rateTradeBean.getSettleDate(), rateTradeBean.getBSettleDate()) == 0) {
            tranSetList.add(transfersetInFee);
            tranSetList.add(transfersetIn);
            tranAdmin.addList(tran, tranSetList);
            tranAdmin.insert(rateTradeBean.getNum(), true);
        } else {
            tranAdmin.addList(tran, tranSetList);
            tranAdmin.insert(rateTradeBean.getNum(), false);
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 分 割 线 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    
}
