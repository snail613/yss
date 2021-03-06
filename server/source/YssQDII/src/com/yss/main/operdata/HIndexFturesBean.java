package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.db2.jcc.a.db;
import com.yss.core.util.YssD;
import com.yss.main.operdata.futures.FuturesTradeAdmin;
import com.yss.main.operdata.futures.pojo.FuturesTradeBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssCons;
import com.yss.util.YssOperCons;
//add by zhaoxianlin 20121105 #story 3159 固定数据源 处理香港股指期货 Bean
public class HIndexFturesBean extends DataBase {
	private FuturesTradeAdmin futuresAdmin=null;
	FuturesTradeBean  futureTbPart=null; //期货交易拆分实体
	FuturesTradeBean  futureTb = null;  //期货交易实体bean
	
	private List futureList =null;  //存放期货交易数据集合
	private Map remindMap = new HashMap();  //key 多头/空头标志  ，value 多头/空头剩余数量 （包含库存剩余数量）
	
	private Date tradeDate;   //成交日期
	private Boolean checkStock = true;   //检查前一日库存标志
//	private String kc = "20";  //开仓
//	private String pc = "21"; //平仓
    private String portCode = "";  //组合代码
    private String brokerCode = "";//券商代码
    private String bargainDate = "";//业务日期
    private String securityCode = ""; //证券代码
    private String curyCode = "";//交易币种
    double dSrcCuryRate = 1;   //基础汇率
    double dTagerCuryRate = 1;// 组合汇率
	
	public HIndexFturesBean() {
	}
	
	public void inertData() throws YssException {
		doManagerBefore();
		doManager();
	}
	private double getBegBailMoney(FuturesTradeBean futureTb) throws YssException {
		ResultSet rs= null;
		String strSql = null;
		String bailType ="";  //保证金类型
		double bailScale = 0; //保证金比例
		double bailFix =0; //每手固定保证金
		double bailMoney = 0 ;
		try{
			strSql = "select * from " +pub.yssGetTableName("Tb_Para_IndexFutures ")+" where FSecurityCode = "
					+dbl.sqlString(futureTb.getSecurityCode());
					//+" and FbrokerCode = " +dbl.sqlString(futureTb.getBrokerCode()) ;
			rs=dbl.openResultSet(strSql);
			if(rs.next()){
				bailType = rs.getString("FBailType");
				bailScale = rs.getDouble("FBailScale");
				bailFix = rs.getDouble("FBailFix");
			}else{
				throw new YssException("请检查是否设置期货基本信息！");
			}
			if(bailType.equalsIgnoreCase("Fix")){ //每手固定
				bailMoney = YssD.round(YssD.mul(futureTb.getTradeAmount(), bailFix), 2);
			}else if(bailType.equalsIgnoreCase("Scale")){  //按比例
				bailMoney = YssD.round(YssD.mul(futureTb.getTradeMoney(), bailScale), 2);
			}
			return bailMoney;
		}catch(Exception e){
			throw new YssException("获取初始保证金出错",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	public void doManager() throws YssException {
		String strSql = null;
		ResultSet rs= null;
		try{
			strSql = "select * from "+pub.yssGetTableName("TB_Data_FuturesTrade")+" where fbargaindate ="+dbl.sqlDate(this.bargainDate)
			          + " and FbrokerCode = " + dbl.sqlString(this.brokerCode)+ " and FsecurityCode = "+dbl.sqlString(this.securityCode)+ " order by Fnum";
			rs=dbl.openResultSet(strSql);
			while(rs.next()){
				futureTb = new FuturesTradeBean();
				tradeDate = rs.getDate("FbargainDate");
				futureTb.setNum(rs.getString("Fnum"));
				futureTb.setSecurityCode(rs.getString("FSecurityCode"));
				futureTb.setDesc(rs.getString("FDesc"));  //描述
				futureTb.setTradeTypeCode(rs.getString("FtradeTypeCode"));   //买卖标示
				futureTb.setTradeAmount(rs.getDouble("FTradeAmount"));      //成交数量
				futureTb.setTradePrice(rs.getDouble("FTRADEPRICE"));   //成交价格
				futureTb.setTradeFee1(rs.getDouble("FtradeFee1"));  //佣金   
				futureTb.setTradeMoney(rs.getDouble("FTradeMoney"));  //成交金额 
				futureTb.setBrokerCode(rs.getString("FBROKERCode"));     //券商代码
				futureTb.setBargainDate((rs.getDate("FBargainDate").toString()));  //成交日期
				futureTb.setsCuryCode(this.curyCode);  //交易币种
                futureTb.setPortCode(rs.getString("FportCode"));  //组合代码
				portCode = rs.getString("FportCode");
				getBailAccCode(futureTb);  //获取初始保证金账户和变动保证金账户
				if(checkStock){  //第一笔交易数据 检查前一日库存
					checkFuStock(futureTb); 
				}
				doOperData(futureTb);
			}
			saveFuSplitData(futureList);
		}catch(Exception e){
			throw new YssException("",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	public void doManagerBefore() throws YssException {
		String strSql = null;
		ResultSet rs= null;
		try{
			if(futureList!=null){
				futureList.clear();
			}
			futureList = new ArrayList();
			strSql = "select * from TMP_HKIndexFutures";
			rs=dbl.openResultSet(strSql);
			while(rs.next()){
				futureTb = new FuturesTradeBean();
				tradeDate = rs.getDate("FTRADEDATE");
				portCode = rs.getString("FACCOUNT");
				brokerCode = rs.getString("FBROKER");
				curyCode = rs.getString("FCURRENCY");
				bargainDate = rs.getDate("FTRADEDATE").toString();
				securityCode  = rs.getString("FBLOOMBERGCODE");
				if(rs.getString("FBS").equals("B")){
					futureTb.setTradeTypeCode("01");   //买入
				}else if(rs.getString("FBS").equals("S")){
					futureTb.setTradeTypeCode("02"); //卖出
				}
				futureTb.setSecurityCode(rs.getString("FBLOOMBERGCODE"));//证券代码
				futureTb.setDesc(rs.getString("FPRODESCRIPTION"));  //描述
				futureTb.setTradeAmount(rs.getDouble("FQTY"));      //成交数量
				futureTb.setTradePrice(rs.getDouble("FTRADEPRICE"));   //成交价格
				futureTb.setTradeFee1(Math.abs(rs.getDouble("FCommission")));  //佣金 
				futureTb.setTradeFee2(Math.abs(rs.getDouble("FtotalFees"))); //费用
				futureTb.setTradeMoney(rs.getDouble("FNotionalValue"));  //成交金额 
				futureTb.setBrokerCode(rs.getString("FBROKER"));     //券商代码
				futureTb.setBargainDate((rs.getDate("FTRADEDATE").toString()));  //成交日期
				futureTb.setsCuryCode(rs.getString("FCURRENCY"));  //交易币种
                futureTb.setPortCode(rs.getString("FACCOUNT"));  //组合代码
				getBailAccCode(futureTb);  //获取初始保证金账户和变动保证金账户
				futureTb.setBailMoney(getBegBailMoney(futureTb));  //保证金金额
				futureList.add(futureTb);
			    }
			   saveFuTrade(futureList);
		}catch(Exception e){
			throw new YssException("",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	/**
	 * 获取初始保证金账户、变动保证金账户
	 * 	 * @param futureTb
	 * @throws YssException
	 */
	public void getBailAccCode(FuturesTradeBean futureTb ) throws YssException{
		 List cashList=null;
		 CashAccountBean cashAccountBean;  //现金账户实体bean
		try{
			BaseCashAccLinkDeal cashDeal = new BaseCashAccLinkDeal();
			cashDeal.setYssPub(pub);
			cashDeal.setLinkParaAttr("", this.portCode, futureTb.getSecurityCode(),
					                  futureTb.getBrokerCode(), "", this.sDate);
			cashList = cashDeal.getHKCashAccount();
			Iterator iterator = cashList.iterator();
			while(iterator.hasNext()){
			  cashAccountBean = (CashAccountBean)iterator.next();
		     if(cashAccountBean.getStrAcctTypeCode().equals("03")){
					futureTb.setBegBailAcctCode(cashAccountBean.getStrCashAcctCode());
			 }else if(cashAccountBean.getStrAcctTypeCode().equals("02")){
					futureTb.setChageBailAcctCode(cashAccountBean.getStrCashAcctCode());
				}
			}
            YssCons.YSS_BAIL_ACC_SETTINGSTATE="";
			//初始保证金账户未设置，变动保证金已设  常量为OnlyBalAcc
			if(futureTb.getBegBailAcctCode().equals(" ")&&!futureTb.getChageBailAcctCode().equals(" ")){
				YssCons.YSS_BAIL_ACC_SETTINGSTATE="OnlyBalAcc";
		    //初始保证金账户设置，变动保证金未设  常量为OnlyChgAcc
			}else if(!futureTb.getBegBailAcctCode().equals(" ")&&futureTb.getChageBailAcctCode().equals(" ")){
				YssCons.YSS_BAIL_ACC_SETTINGSTATE="OnlyChgAcc";
		    //初始保证金账户未置，变动保证金未设  常量为BothBalChg
			}else if(futureTb.getBegBailAcctCode().equals(" ")&&futureTb.getChageBailAcctCode().equals(" ")){
				YssCons.YSS_BAIL_ACC_SETTINGSTATE="BothBalChg";
			}
		}catch(Exception e){
			throw new YssException("获取初始保证金账户、变动保证金账户出错！\n", e);
		}
     }

	public void doOperData(FuturesTradeBean futureTb) throws YssException {
		double remindNum=0;   //多头/空头数据剩余数量
		double lTradeNum = 0;  //多头数量
		double sTradeNum = 0;  //空头数量
		try{
			sTradeNum=Double.parseDouble(remindMap.get("Short").toString());
			lTradeNum=Double.parseDouble(remindMap.get("Long").toString());
			
			if(futureTb.getTradeTypeCode().equals("01")){  //多头交易数据
				if(sTradeNum!=0){  //空头剩余数量不为0，要做平仓处理
					remindNum = Double.parseDouble(remindMap.get("Short").toString());
					doSSetting(futureTb,remindNum);
				}
				else if(lTradeNum==0){
					//futureTb.setTradeTypeCode(kc);  //多头开仓
					futureTb.setNum(futureTb.getNum().concat("2"));  //开仓交易编号+2
	        		futureTb.setOperType("20");
					futureTb.setBailMoney(getBegBailMoney(futureTb));
					futureList.add(futureTb);
					remindMap.put("Long",futureTb.getTradeAmount());
				}else if(lTradeNum!=0){
					//futureTb.setTradeTypeCode(kc);  //多头开仓
					futureTb.setNum(futureTb.getNum().concat("2"));  //开仓交易编号+1
	        		futureTb.setOperType("20");
					futureTb.setBailMoney(getBegBailMoney(futureTb));
					futureList.add(futureTb);
					remindNum = Double.parseDouble(remindMap.get("Long").toString());
					remindMap.put("Long",YssD.add(futureTb.getTradeAmount(), remindNum));
				}
			}else if(futureTb.getTradeTypeCode().equals("02")){  //空头交易数据
				if(lTradeNum!=0){  //多头剩余数据不为空，空头交易数据做相应平仓
					remindNum = Double.parseDouble(remindMap.get("Long").toString());
					doLSetting(futureTb,remindNum);
				}
				else if(sTradeNum==0){
					//futureTb.setTradeTypeCode(kc);  //空头开仓
					futureTb.setNum(futureTb.getNum().concat("2"));  //开仓交易编号+1
	        		futureTb.setOperType("20");
					futureTb.setBailMoney(getBegBailMoney(futureTb));
					futureList.add(futureTb);
					remindMap.put("Short",futureTb.getTradeAmount());
				}else if(sTradeNum!=0){
					//futureTb.setTradeTypeCode(kc);  //空头开仓
					futureTb.setNum(futureTb.getNum().concat("2"));  //开仓交易编号+1
	        		futureTb.setOperType("20");
					futureTb.setBailMoney(getBegBailMoney(futureTb));
					futureList.add(futureTb);
					remindNum = Double.parseDouble(remindMap.get("Short").toString());
					remindMap.put("Short",YssD.add(futureTb.getTradeAmount(), remindNum));
				}
			}
		}catch(Exception e){
			throw new YssException("",e);
		}
		
	}
	/**
	 * 检查库存剩余多头/空头数量
	 * @param futureTb
	 * @throws YssException
	 */
	public void checkFuStock(FuturesTradeBean futureTb) throws YssException {
		ResultSet rs= null;
		String sql = "";
		double count=0;
		try{
			remindMap.put("Long", 0);
			remindMap.put("Short", 0);
			if(futureList!=null){
				futureList.clear();
			}
			futureList = new ArrayList();
			sql="select * from " + pub.yssGetTableName("Tb_Data_FutTradeRela") + 
			" where  FPortCode = "+dbl.sqlString(this.portCode)+ 
			" and FTransDate="+dbl.sqlDate(YssFun.addDay(YssFun.toDate(futureTb.getBargainDate()), -1))+
			" and FNum = "+dbl.sqlString(futureTb.getSecurityCode())+
			" and FTsfTypeCode = '05FU01' and FCloseNum = ' ' and FbrokerCode = "+dbl.sqlString(this.brokerCode);
			rs=dbl.openResultSet(sql);
			while(rs.next()){
				count= rs.getDouble("FStorageAmount");  //库存多头/空头数量
				if(count>=0){  //多头期货数据有库存 
					remindMap.put("Long",count);
				}else if(count<0){  //空头期货数据有库存
					remindMap.put("Short",Math.abs(count));
				}
			}
			 checkStock = false;
		}catch(Exception e){
			throw new YssException("获取库存期货数据出错",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	
	public void doLSetting(FuturesTradeBean futureTb,double remindNum) throws YssException {
		double FuAmount=0;//读入的空头交易数据成交数量
		try{
			FuAmount=futureTb.getTradeAmount();
			if(FuAmount<=remindNum){  //空头成交数量小于等于剩余多头数量
				futureTb.setTradeAmount(FuAmount);  
        		//futureTb.setTradeTypeCode(pc);//多头平仓
        		futureTb.setNum(futureTb.getNum().concat("1"));  //平仓交易编号+1
        		futureTb.setOperType("21");
        		//获取初始保证金
        		futureTb.setBailMoney(getBegBailMoney(futureTb));
        		futureList.add(futureTb);
        		remindMap.put("Long",remindNum-FuAmount);  //更新多头剩余数量存入map
			}else if(FuAmount>remindNum){ //空头成交数量大于剩余多头数量  拆分读入交易数据
				futureTbPart=(FuturesTradeBean)futureTb.clone();
        		//平仓成交金额 = 成交金额*剩余数量/成交数量 
        		//平仓费用 = 交易费用*剩余数量/成交数量 
				futureTb.setTradeMoney(YssD.round(YssD.div(YssD.mul(futureTb.getTradeMoney(), remindNum), FuAmount), 2));
        		futureTb.setTradeFee1(YssD.round(YssD.div(YssD.mul(futureTb.getTradeFee1(), remindNum),FuAmount),2));
        		futureTb.setTradeAmount(remindNum);
        		//futureTb.setTradeTypeCode(pc);  //多头平仓
        		futureTb.setNum(futureTb.getNum().concat("1"));  //平仓交易编号+1
        		futureTb.setOperType("21");
        		//获取初始保证金
        		futureTb.setBailMoney(getBegBailMoney(futureTb));
        		futureList.add(futureTb);
        		
        		futureTbPart.setTradeAmount(YssD.sub(FuAmount, remindNum));
        		//futureTbPart.setTradeTypeCode(kc);  //空头开仓
        		//拆分后开仓成交金额 =  成交金额  - 平仓成交金额
        		//拆分后开仓费用 =  交易费用 - 平仓交易费用
        		futureTbPart.setTradeMoney(YssD.round(futureTbPart.getTradeMoney()-futureTb.getTradeMoney(), 2));
        		futureTbPart.setTradeFee1(YssD.round(futureTbPart.getTradeFee1()-futureTb.getTradeFee1(), 2));
        		futureTbPart.setBailMoney(getBegBailMoney(futureTbPart));
        		futureTbPart.setNum(futureTbPart.getNum().concat("2"));  //开仓交易编号+1
        		futureTbPart.setOperType("20");
        		futureList.add(futureTbPart);
        		
        		remindMap.put("Short",futureTbPart.getTradeAmount());  //平仓后剩余空头数量存入map
        		remindMap.put("Long",0);  //多头剩余数据清空
			}
		}catch(Exception e){
			throw new YssException("生成交易数据（多头平仓）出错",e);
		}
		
	}
	public void doSSetting(FuturesTradeBean futureTb,double remindNum) throws YssException {
		double FuAmount=0;  //读入的多头交易数据成交数量
		String securityCode="";
		try{
			FuAmount=futureTb.getTradeAmount();
			if(FuAmount<=remindNum){   //多头成交数量小于等于剩余空头数量
				futureTb.setTradeAmount(FuAmount);  
        		//futureTb.setTradeTypeCode(pc);//空头平仓
        		futureTb.setNum(futureTb.getNum().concat("1"));  //平仓交易编号+1
        		futureTb.setOperType("21");
        		//获取初始保证金
        		futureTb.setBailMoney(getBegBailMoney(futureTb));
        		futureList.add(futureTb);
        		//若空头数量未平仓完，保存剩余数量
        		remindMap.put("Short",remindNum-FuAmount); 
			}else if(FuAmount>remindNum){  //多头成交数量大于剩余空头数量  拆分读入交易数据
				futureTbPart=(FuturesTradeBean)futureTb.clone();
				//平仓成交金额 = 成交金额*剩余数量/成交数量 
        		//平仓费用 = 交易费用*剩余数量/成交数量 
				futureTb.setTradeMoney(YssD.round(YssD.div(YssD.mul(futureTb.getTradeMoney(), remindNum), FuAmount), 2));
        		futureTb.setTradeFee1(YssD.round(YssD.div(YssD.mul(futureTb.getTradeFee1(), remindNum),FuAmount),2));
        		futureTb.setTradeAmount(remindNum);
        		//futureTb.setTradeTypeCode(pc);  //空头平仓
        		futureTb.setNum(futureTb.getNum().concat("1"));  //平仓交易编号+1
        		futureTb.setOperType("21");
        		
        		futureTb.setBailMoney(getBegBailMoney(futureTb));
        		futureList.add(futureTb);
        		//钆差处理平仓剩余成交数据
        		futureTbPart.setTradeMoney(YssD.round(futureTbPart.getTradeMoney()-futureTb.getTradeMoney(), 2));
        		futureTbPart.setTradeFee1(YssD.round(futureTbPart.getTradeFee1()-futureTb.getTradeFee1(), 2));
        		futureTbPart.setTradeAmount(YssD.sub(FuAmount, remindNum));
        		//futureTbPart.setTradeTypeCode(kc);  //多头开仓
        		futureTbPart.setNum(futureTbPart.getNum().concat("2"));  //开仓交易编号+1
        		futureTbPart.setOperType("20");
        		futureTbPart.setBailMoney(getBegBailMoney(futureTbPart));
        		futureList.add(futureTbPart);
        		
        		remindMap.put("Long",futureTbPart.getTradeAmount());  //平仓后剩余多头数量存入map
        		remindMap.put("Short",0);  //空头剩余数据清空
			}
		}catch(Exception e){
			throw new YssException("生成交易数据（空头平仓）出错!",e);
		}
	}
	/**
	 * 检查是否设置期货证券基本信息信息
	 * @param securityCode
	 * @param bargainDate
	 * @return
	 * @throws YssException
	 */
	private boolean checkFuTradeSetting(String securityCode,String bargainDate) throws YssException {
		ResultSet rs= null;
		String sql = "";
		try{
			sql="select * from " + pub.yssGetTableName("Tb_Para_Security")  +"where FSecurityCode="+securityCode+
			" and FPortCode = "+this.portCode+ " and FBargainDate="+dbl.sqlDate(bargainDate);
			rs=dbl.openResultSet(sql);
			if(rs.next()){
				return true;
			}else{
				throw new YssException("请检是否维护该期货基本信息！");
			}
		}catch(Exception e){
			throw new YssException("检查期货基本信息出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
 /**
  * 保存期货交易数据
  * @param futureList
  * @throws YssException
  */
	public void saveFuTrade(List futureList) throws YssException {
		String strSql = "";
		ResultSet rs= null;
		PreparedStatement pst= null;
		Connection con = null;
		Boolean bTrans=false;
		String sNum="";
		FuturesTradeBean futuresTradeBean = null;
	    String strNumDate = "";
		try{
			deleteTradeData();
//			BaseOperDeal operDeal = new BaseOperDeal();
//			operDeal.setYssPub(pub); // 设置pub
			Iterator iterator = futureList.iterator();
			con = dbl.loadConnection();
			bTrans=true;
			con.setAutoCommit(false);
			//pst=con.prepareStatement(strSql);
			while(iterator.hasNext()){
				futuresTradeBean = (FuturesTradeBean)iterator.next();
				sNum = "T" + YssFun.formatDate(YssFun.toDate(futuresTradeBean.getBargainDate()), "yyyyMMdd") +
	                dbFun.getNextInnerCode(pub.yssGetTableName("TB_Data_FuturesTrade"),
	                                       dbl.sqlRight("FNUM", 6),
	                                       "000001",
	                                       " where FBargainDate=" + dbl.sqlDate(futuresTradeBean.getBargainDate()) +
	                                       " or FBargainDate=" + dbl.sqlDate("9998-12-31") +
	                                       " or FNum like 'T" + YssFun.formatDate(YssFun.toDate(futuresTradeBean.getBargainDate()), "yyyyMMdd") + "%'");
				dSrcCuryRate = this.getSettingOper().getCuryRate(YssFun.toSqlDate(futuresTradeBean.getBargainDate()),
						futuresTradeBean.getsCuryCode(),//币种
						this.portCode,//组合代码
	                    YssOperCons.YSS_RATE_BASE);//获取基础汇率的值
				dTagerCuryRate = this.getSettingOper().getCuryRate(YssFun.toSqlDate(futuresTradeBean.getBargainDate()),
					futuresTradeBean.getsCuryCode(),
					this.portCode,
	                YssOperCons.YSS_RATE_PORT);//获取组合汇率的值
	            strSql = "insert into "+pub.yssGetTableName("TB_Data_FuturesTrade")+" (FNum,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FBegBailAcctCode,FChageBailAcctCode" +
				",FBargainDate,FBargainTime,FSettleDate,FSettleTime,FSettleType,FTradeAmount,FTradePrice" +
				",FTradeMoney,FBegBailMoney,FBaseCuryRate,FPortCuryRate,FSettleMoney,FSettleState,FFeeCode1,FTradeFee1" +
				",FFeeCode2,FTradeFee2,FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6" +
				",FFeeCode7,FTradeFee7,FFeeCode8,FTradeFee8,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) " +
				 " values("
				 +dbl.sqlString(sNum)+ ","+
				 dbl.sqlString(futuresTradeBean.getSecurityCode())+ ","+
				 dbl.sqlString(futuresTradeBean.getPortCode())+ ","+
				 dbl.sqlString(futuresTradeBean.getBrokerCode())+ ","+
				 dbl.sqlString(" ") + ","+
				 dbl.sqlString(futuresTradeBean.getTradeTypeCode())+ ","+
				 dbl.sqlString(futuresTradeBean.getBegBailAcctCode())+ ","+
				 dbl.sqlString(futuresTradeBean.getChageBailAcctCode())+ ","+
				 dbl.sqlDate(YssFun.toSqlDate(futuresTradeBean.getBargainDate()))+ ","+
				 dbl.sqlString("00:00:00") + ","+
				 dbl.sqlDate(YssFun.toSqlDate(futuresTradeBean.getBargainDate()))+ ","+
				 dbl.sqlString("00:00:00") + ","+
				 1 + ","+
				futuresTradeBean.getTradeAmount()+ ","+
				futuresTradeBean.getTradePrice()+ ","+
				futuresTradeBean.getTradeMoney()+ ","+
				futuresTradeBean.getBailMoney()+ ","+
				dSrcCuryRate+ ","+
				dTagerCuryRate+ ","+
				YssD.add(futuresTradeBean.getTradeFee1(), futuresTradeBean.getTradeFee2())+ ","+
				0+ ","+
				dbl.sqlString("FuCommission") + ","+
				futuresTradeBean.getTradeFee1()+ ","+
				dbl.sqlString("FuTradeFee") + ","+
				futuresTradeBean.getTradeFee2() + ","+
				 dbl.sqlString("交易费用3") + ","+
				 0 + ","+
				 dbl.sqlString("交易费用4") + ","+
				 0 + ","+
				 dbl.sqlString("交易费用5") + ","+
				 0 + ","+
				 dbl.sqlString("交易费用6") + ","+
				 0 + ","+
				 dbl.sqlString("交易费用7") + ","+
				 0 + ","+
				 dbl.sqlString("交易费用8") + ","+
				 0 + ","+
				 dbl.sqlString(futuresTradeBean.getDesc())+ ","+
				 1 + ","+
				 dbl.sqlString(pub.getUserCode())+ ","+
				 dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))+ ","+
				 dbl.sqlString(futuresTradeBean.getPortCode())+ ","+
				 dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))+")";
				
//				pst.setString(1, sNum);
//				pst.setString(2, futuresTradeBean.getSecurityCode());
//				pst.setString(3, futuresTradeBean.getPortCode());
//				pst.setString(4, futuresTradeBean.getBrokerCode());
//				pst.setString(5, " ");
//				pst.setString(6, futuresTradeBean.getTradeTypeCode());
//				pst.setString(7, futuresTradeBean.getBegBailAcctCode());
//				pst.setString(8, futuresTradeBean.getChageBailAcctCode());
//				
//				pst.setDate(9,YssFun.toSqlDate(futuresTradeBean.getBargainDate()));
//				pst.setString(10, "00:00:00");
//				pst.setDate(11, YssFun.toSqlDate(futuresTradeBean.getBargainDate()));
//				pst.setString(12, "00:00:00");
//				pst.setDouble(13, 1);
//				pst.setDouble(14, futuresTradeBean.getTradeAmount());
//				pst.setDouble(15, futuresTradeBean.getTradePrice());
//				pst.setDouble(16, futuresTradeBean.getTradeMoney());
//				pst.setDouble(17, futuresTradeBean.getBailMoney());
//				pst.setDouble(18, dSrcCuryRate);
//				pst.setDouble(19, 1);
//				
//				pst.setDouble(20, futuresTradeBean.getTradeFee1());
//				pst.setDouble(21, 0);
//				pst.setString(22, "券商佣金");
//				pst.setDouble(23, futuresTradeBean.getTradeFee1());
//				pst.setString(24, "费用代码2");
//				pst.setDouble(25, 0);
//				pst.setString(26, "费用代码3");
//				pst.setDouble(27, 0);
//				pst.setString(28, "费用代码4");
//				pst.setDouble(29, 0);
//				pst.setString(30, "费用代码5");
//				pst.setDouble(31, 0);
//				pst.setString(32, "费用代码6");
//				pst.setDouble(33, 0);
//				pst.setString(34, "费用代码7");
//				pst.setDouble(35, 0);
//				pst.setString(36, "费用代码8");
//				pst.setDouble(37, 0);
//				pst.setString(38, futuresTradeBean.getDesc());
//				pst.setDouble(39, 1);
//				pst.setString(40, pub.getUserCode()	);
//				pst.setString(41, YssFun.formatDatetime(new java.util.Date()));
//				pst.setString(42, pub.getUserCode());
//				pst.setString(43, YssFun.formatDatetime(new java.util.Date()));
//				
//				pst.addBatch();
//			}
//			pst.executeBatch();
	        dbl.executeSql(strSql);
			con.commit();
			bTrans=false;
			con.setAutoCommit(true);
			}
		}catch(Exception e){
			throw new YssException("保存交易数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(con, bTrans);
		}
	}
	/**
	  * 保存期货拆分交易数据至新建的拆分交易表
	  * @param futureList
	  * @throws YssException
	  */
		public void saveFuSplitData(List futureList) throws YssException {
			String strSql = "";
			ResultSet rs= null;
			PreparedStatement pst= null;
			Connection con = null;
			Boolean bTrans=false;
			String sNum="";
			FuturesTradeBean futuresTradeBean = null;
			try{
				deleteCashTrans();  //删除资金调拨数据
				deleteSplitData();  
				BaseOperDeal operDeal = new BaseOperDeal();
				operDeal.setYssPub(pub); // 设置pub
				Iterator iterator = futureList.iterator();
				con = dbl.loadConnection();
				bTrans=true;
				con.setAutoCommit(false);
				while(iterator.hasNext()){
					futuresTradeBean = (FuturesTradeBean)iterator.next();
					sNum = futuresTradeBean.getNum();
		            strSql = "insert into "+pub.yssGetTableName("TB_DATA_FUTTRADESPLIT")+" (FNum,FoperType,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FBegBailAcctCode,FChageBailAcctCode" +
					",FBargainDate,FBargainTime,FSettleDate,FSettleTime,FSettleType,FTradeAmount,FTradePrice" +
					",FTradeMoney,FBegBailMoney,FBaseCuryRate,FPortCuryRate,FSettleMoney,FSettleState,FFeeCode1,FTradeFee1" +
					",FFeeCode2,FTradeFee2,FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6" +
					",FFeeCode7,FTradeFee7,FFeeCode8,FTradeFee8,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) " +
					 " values("
					 +dbl.sqlString(sNum)+ ","+
					 dbl.sqlString(futuresTradeBean.getOperType())+ ","+
					 dbl.sqlString(futuresTradeBean.getSecurityCode())+ ","+
					 dbl.sqlString(futuresTradeBean.getPortCode())+ ","+
					 dbl.sqlString(futuresTradeBean.getBrokerCode())+ ","+
					 dbl.sqlString(" ") + ","+
					 dbl.sqlString(futuresTradeBean.getTradeTypeCode())+ ","+
					 dbl.sqlString(futuresTradeBean.getBegBailAcctCode())+ ","+
					 dbl.sqlString(futuresTradeBean.getChageBailAcctCode())+ ","+
					 dbl.sqlDate(YssFun.toSqlDate(futuresTradeBean.getBargainDate()))+ ","+
					 dbl.sqlString("00:00:00") + ","+
					 dbl.sqlDate(YssFun.toSqlDate(futuresTradeBean.getBargainDate()))+ ","+
					 dbl.sqlString("00:00:00") + ","+
					 1 + ","+
					futuresTradeBean.getTradeAmount()+ ","+
					futuresTradeBean.getTradePrice()+ ","+
					futuresTradeBean.getTradeMoney()+ ","+
					futuresTradeBean.getBailMoney()+ ","+
					dSrcCuryRate+ ","+
					dTagerCuryRate+ ","+
					futuresTradeBean.getTradeFee1()+ ","+
					0+ ","+
					dbl.sqlString("FuCommission") + ","+
					futuresTradeBean.getTradeFee1()+ ","+
					 dbl.sqlString("FuTradeFee") + ","+
					 0 + ","+
					 dbl.sqlString("交易费用3") + ","+
					 0 + ","+
					 dbl.sqlString("交易费用4") + ","+
					 0 + ","+
					 dbl.sqlString("交易费用5") + ","+
					 0 + ","+
					 dbl.sqlString("交易费用6") + ","+
					 0 + ","+
					 dbl.sqlString("交易费用7") + ","+
					 0 + ","+
					 dbl.sqlString("交易费用8") + ","+
					 0 + ","+
					 dbl.sqlString(futuresTradeBean.getDesc())+ ","+
					 1 + ","+
					 dbl.sqlString(pub.getUserCode())+ ","+
					 dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))+ ","+
					 dbl.sqlString(futuresTradeBean.getPortCode())+ ","+
					 dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))+")";
		            
			        dbl.executeSql(strSql);
					con.commit();
					bTrans=false;
					con.setAutoCommit(true);
				}
			}catch(Exception e){
				throw new YssException("保存拆分后交易数据出错！",e);
			}finally{
				dbl.closeResultSetFinal(rs);
				dbl.endTransFinal(con, bTrans);
			}
		}
	/**
	 * 删除期货交易数据
	 * @throws YssException
	 */
	public void deleteTradeData() throws YssException {
		String sql = "";
		boolean bTrans = false; // 代表是否开始了事务
	    Connection conn = dbl.loadConnection();
		try{
			conn.setAutoCommit(false);
	        bTrans = true;
			sql="delete  from " + pub.yssGetTableName("TB_Data_FuturesTrade")+" where FBargainDate="+dbl.sqlDate(tradeDate)+ 
			        " and FPortCode = "+dbl.sqlString(this.portCode)+" and FBrokerCode="+dbl.sqlString(this.brokerCode);
			        //" and FsecurityCode = "+dbl.sqlString(this.securityCode);
			dbl.executeSql(sql);
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("删除期货交易数据出错",e);
		}finally{
			  dbl.endTransFinal(conn, bTrans);
		}
	}	
	/**
	 * 删除拆分后的期货交易数据
	 * @throws YssException
	 */
	public void deleteSplitData() throws YssException {
		String sql = "";
		boolean bTrans = false; // 代表是否开始了事务
	    Connection conn = dbl.loadConnection();
		try{
			conn.setAutoCommit(false);
	        bTrans = true;
			sql="delete  from " + pub.yssGetTableName("TB_DATA_FUTTRADESPLIT")+" where FBargainDate="+dbl.sqlDate(tradeDate)+ 
			        " and FPortCode = "+dbl.sqlString(this.portCode)+" and FBrokerCode="+dbl.sqlString(this.brokerCode);
			       // " and FsecurityCode = "+dbl.sqlString(this.securityCode);
			dbl.executeSql(sql);
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("删除拆分后期货交易数据出错",e);
		}finally{
			  dbl.endTransFinal(conn, bTrans);
		}
	}	
	/**
	 * 资金调拨处理
	 * @throws YssException
	 */
	public void deleteCashTrans() throws YssException {
		String sql = "";
		ResultSet rs= null;
		try{
			sql="select Fnum  from " + pub.yssGetTableName("TB_DATA_FUTTRADESPLIT")+" where FBargainDate="+dbl.sqlDate(tradeDate)+ 
			        " and FPortCode = "+dbl.sqlString(this.portCode)+" and FBrokerCode="+dbl.sqlString(this.brokerCode)+
			        " and FsecurityCode = "+dbl.sqlString(this.securityCode);
			rs=dbl.openResultSet(sql);
			while(rs.next()){
				doCashTrans(rs.getString("Fnum"));
			}
		}catch(Exception e){
			throw new YssException("获取待删除资金调拨编号出错",e);
		}finally{
			  dbl.closeResultSetFinal(rs);
		}
	}	
	/**
	 * 删除资金调拨数据
	 * @throws YssException
	 */
	public void doCashTrans(String num) throws YssException {
		String sql = "";
		String strSql= "";
		boolean bTrans = false; // 代表是否开始了事务
	    Connection conn = dbl.loadConnection();
	    ResultSet rs = null;
		try{
			conn.setAutoCommit(false);
	        bTrans = true;
			
			sql="select Fnum  from " + pub.yssGetTableName("Tb_Cash_Transfer")+" where FTradeNum="+dbl.sqlString(num);
			rs=dbl.openResultSet(sql);
			while(rs.next()){
				strSql = "delete from "+ pub.yssGetTableName("Tb_Cash_SubTransfer")+" where FNum="+dbl.sqlString(rs.getString("Fnum"));
				dbl.executeSql(strSql);
				strSql = "delete from "+ pub.yssGetTableName("Tb_Cash_Transfer")+" where FNum="+dbl.sqlString(rs.getString("Fnum"));
				dbl.executeSql(strSql);
			}
			
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("删除资金调拨数据出错",e);
		}finally{
			  dbl.closeResultSetFinal(rs);
			  dbl.endTransFinal(conn, bTrans);
		}
	}	
}