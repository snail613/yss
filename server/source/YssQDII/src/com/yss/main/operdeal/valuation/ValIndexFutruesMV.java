package com.yss.main.operdeal.valuation;

import java.sql.*;
import java.util.*;

//---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
import com.yss.commeach.*;
import com.yss.main.cashmanage.*;
import com.yss.main.operdata.*;
import com.yss.main.operdata.futures.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.report.reptab.indexfuturesbook.TabIndexFuturesBook;
import com.yss.main.operdeal.stgstat.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 * <p>Description: 产生股指期货的估值增值的应收应付 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @author hukun
 * @version 1.0
 */
public class ValIndexFutruesMV
    extends BaseValDeal {
	String cashAccCode = " ";  //add by zhaoxianlin STORY #3271 期货合约保证金比例经常性调整的系统处理
    String curyCode = " "; //add by zhaoxianlin STORY #3271 期货合约保证金比例经常性调整的系统处理
    public ValIndexFutruesMV() {
    }

    /**
     * 通过组合代码获取期货核算方式
     * @param sPortCode String：组合代码
     * @return String
     * modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A
     * modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
     */
	//edit by songjie 2013.08.19 资产估值优化  由private 改为 public 
    public String getAccountTypeBy(String sPortCode, 
    		Hashtable htPortAccountType, int i) throws YssException {
    	// i = 0 时处理股指期货，i = 1 时处理债券期货，i = 2 时处理外汇期货 , i = 3 时处理商品期货 //modify huangqirong 2012-08-21  商品期货
		String sAccountType = "";
		if(i == 0){ //股指期货
			// 默认使用先入先出
			sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
			String sTheDayFirstFIFO = "";
			String sModAvg = "";
			try {
				sTheDayFirstFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
				sModAvg = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG);
				if (sTheDayFirstFIFO != null
						&& sTheDayFirstFIFO.indexOf(sPortCode) != -1) {//edit by xuxuming,20091228.以前的代码在此处写反了
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
				} else if (sModAvg != null && sModAvg.indexOf(sPortCode) != -1) {//edit by xuxuming,20091228.以前的代码在此处写反了
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
				}
			} catch (Exception ex) {
				throw new YssException(ex.getMessage());
			}
		} else if (i == 1 || i == 2 || i == 3){ //债券期货 ，外汇期货 , 商品期货 //modify huangqirong 2012-08-21  商品期货
			// 默认移动加权
			sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
			String sTheDayFirstFIFO = "";
			String sFIFO = "";
			try {
				sTheDayFirstFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
				sFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO);
				if (sTheDayFirstFIFO != null
						&& sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
				} else if (sFIFO != null && sFIFO.indexOf(sPortCode) != -1) {
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
				}
			} catch (Exception ex) {
				throw new YssException(ex.getMessage());
			}
		}
		return sAccountType;
    }

    /**
     * getValuationCats
     *
     * @param mtvBeans ArrayList
     * @return HashMap
     */
    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        HashMap hmResult = new HashMap();
        String sAccountType = "";
        ValFuturesHedgingMV futuresHedgingMV = null;//xuqiji 20100512
        MTVMethodBean vMethod = null;//xuqiji 20100513
        try {
            //统计期货库存，产生资金调拨 sunkey 20081124 BugID:MS00013
            BaseStgStatDeal futruesstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("IndexFuturesStorage");
            futruesstgstat.setYssPub(pub);
            futruesstgstat.initStorageStat(dDate, dDate, operSql.sqlCodes(portCode), false, false);
            this.deleteData(dDate, operSql.sqlCodes(portCode));//add by huhuichao 20131031 Bug #80693 
            futruesstgstat.getStorageStatData(dDate);

            //获取期货结算类型
//            sAccountType = getAccountTypeBy(portCode); modify by fangjiang 2010.08.28
            //计算当天估值增值
            getTodayStockFutruesRelaData(dDate, portCode, mtvBeans); //modify by fangjiang 2010.08.28

            //计算完估值增值后，统计估值增值收益 sunkey 20081126 BugID:MS00013
            valutionGainsTransfer(dDate, portCode);  //modify by fangjiang 2010.08.28

            //产生股指期货应收应付,根据参数状态判断是否处理 sunkey 20081124 BugID:MS00013
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            boolean bIsFuturesRecPay = pubPara.getIsFuturesRecPay(portCode);
            if (bIsFuturesRecPay) {
                    hmResult = getFuturesRecPay();
            }
            //--xuqiji 20100512 期货套期保值估值增值及应收应付库存统计  MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A-----//
            //循环估值方法
            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);
                futuresHedgingMV = new ValFuturesHedgingMV();
                futuresHedgingMV.setYssPub(pub);
                futuresHedgingMV.doManage(dDate,portCode,vMethod,sAccountType);
            }
            //----------------------------------end---------------------------------------//
            //处理完股指期货的应收应付后，统计现金库存 sunkey 200081126 BugID:MS00013
            BaseStgStatDeal cashstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("CashStorage");
            cashstgstat.setYssPub(pub);
            cashstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));
            
            //add by fangjiang 2011.12.17 STORY #1886
            updateStorageDate();
            //--------------end----------------------
            
            // start dongqingsong  2013-09-25  算法有误，注销   bug 8704 
            //doFuBailChange(dDate,portCode);  //add by zhaoxianlin  STORY #3271 期货合约保证金比例经常性调整的系统处理
            // end  dongqingsong  2013-09-25  算法有误，注销   bug 8704 
            TabIndexFuturesBook  fuBook = new TabIndexFuturesBook();
            String sterm = "1\r" + YssFun.formatDate(dDate) + "\n2\r" + YssFun.formatDate(dDate)
                           + "\n3\r" + this.portCode + "\n4\r1";
            fuBook.setYssPub(this.pub);
            fuBook.init(sterm);            
            //fuBook.invokeOperMothed();

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return hmResult;
    }	
    
    /**
     * add by huhuichao 20131031 Bug #80693 股指期货的核算方式设置为先入先出法或不设置时，期货资产估值报错
     * 期货交易关联数据表中09FU01,09FU02,09FU03,09FU04的数据不能正常被删除，为了不影响库存统计，在此作一个数据的删除
     * @param dDate
     * @param portCode
     * @throws YssException
     */
    public void deleteData(java.util.Date dDate,String sPortCodes) throws YssException {
        String strSql = "";
        try {
            strSql = "DELETE FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(dDate)+
                " and FPortCode in ("+ sPortCodes+")"+
                " and ftsftypecode  in (" + operSql.sqlCodes(com.yss.util.YssOperCons.YSS_FU_09) + ")"; 
                dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("删除期货交易关联数据出错\r\n" + e.getMessage());
        }
    }
    
/**
 * add by zhaoxianlin  期货合约保证金比例经常性调整的系统处理
 * @param dDate
 * @param portCode
 * @throws YssException
 */
 private void doFuBailChange(java.util.Date dDate,String portCode) throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String securityCode = null;
		String brokerCode = null;
		double stockNum =0;  //库存数量
		double FuBailMoney = 0;  //保证金调整金额
		double FuBegainMoney = 0;  //初始保证金金额
		double FuChgMoney = 0;  //余额
		try{
			 strSql = " select FNum,FStorageAmount,FBrokerCode from "+pub.yssGetTableName("TB_Data_FutTradeRela")+ 
			          " where FTransDate ="+dbl.sqlDate(dDate)+ " and FPortCode = "
			          + dbl.sqlString(portCode)+ " and FTsfTypeCode like '05%' and FCloseNum =' ' "   ;
		     rs = dbl.openResultSet(strSql);
		     while(rs.next()){
		    	 if(FuChgMoney!=0){
		    		 FuChgMoney=0;
		    	 }
		    	 securityCode = rs.getString("FNum");
		    	 brokerCode = rs.getString("FBrokerCode");
		    	 stockNum = rs.getDouble("FStorageAmount");
		    	 FuBailMoney=getFuBailMoney(securityCode,brokerCode,dDate,stockNum);//获取保证金调整金额
		    	 FuBegainMoney = getFuBegainMoney(securityCode,brokerCode,dDate);//获取初始保证金金额
		    	// if(FuBailMoney>FuBegainMoney){
		         FuChgMoney = YssD.sub(FuBailMoney, FuBegainMoney);
		    	 //}
		    	 bulidCashTrans(rs,FuChgMoney);
		      }
		}catch(Exception e){
			throw new YssException("期货合约保证金比例经常性调整的系统处理出错！\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}
 //add by zhaoxianlin   期货合约保证金比例经常性调整的系统处理  生成资金调拨数据
    public void bulidCashTrans(ResultSet rs,double ChgMoney) throws YssException{
		String strSql = "";
		Boolean bTrans = false ;
		Connection conn= null;
		String strNum="";
		String sNum = "";
		double baseRate = 0;
	 	double portRate = 0;
	 	int   i = 0;
		ResultSet rsCash = null;
		try{
			 strNum = "C" + YssFun.formatDate(this.dDate, "yyyyMMdd") +
	            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
	                                   dbl.sqlRight("FNUM", 6), "000001");
			
			 baseRate = this.getSettingOper().getCuryRate(
			  		  this.dDate, 
			  		  curyCode, 
				      this.portCode, 
				      YssOperCons.YSS_RATE_BASE
	  			  );  
			 portRate = this.getSettingOper().getCuryRate(
				      this.dDate, 
				      curyCode, 
				      this.portCode, 
				      YssOperCons.YSS_RATE_PORT
      			  );
			  conn = dbl.loadConnection();
			  conn.setAutoCommit(false);
			  bTrans = true;
			  
			  strSql = "select * from "+ pub.yssGetTableName("Tb_Cash_Transfer") +" where FTsfTypeCode='05' and FSubTsfTypeCode='05FU02' " +
			  		   " and FTransferDate = "+dbl.sqlDate(dDate)+ " and FsecurityCode = "+dbl.sqlString(rs.getString("FNum"));
			  rsCash = dbl.openResultSet(strSql);
			  while(rsCash.next()){
				  sNum = rsCash.getString("FNum");
				  strSql = "delete from " +pub.yssGetTableName("Tb_Cash_SubTransfer") + " where FNum =" + dbl.sqlString(sNum) ;
	              dbl.executeSql(strSql); //删除资金调拨子表
	              strSql = "delete from " +pub.yssGetTableName("tb_cash_transfer") + " where FNum = " +dbl.sqlString(sNum);  
		          dbl.executeSql(strSql); //删除资金调拨表
			  }
			  strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
              "(FNum,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransferTime,FTransDate," +
              "FSecurityCode,FSrcCashAcc,FDATASOURCE,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
              " values(" + dbl.sqlString(strNum) + "," +
              dbl.sqlString("05") + ","+  //调拨类型  ,FSrcCashAcc
              dbl.sqlString("05FU02") + ","+  //调拨子类型
              dbl.sqlDate(this.dDate) + "," +   //调拨日期
              dbl.sqlString("00:00:00") + "," +  //调拨时间
              dbl.sqlDate(this.dDate) + "," +    //业务日期
              dbl.sqlString(rs.getString("FNum")) + "," +
              dbl.sqlString(cashAccCode) + "," +
               "1," + //数据来源  0-手工；1－自动
              dbl.sqlString(" ") + "," +
               "1," +  //审核状态
               dbl.sqlString(pub.getUserCode()) + "," +
               dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
               dbl.sqlString(pub.getUserCode()) + "," +  
               dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
              ")";
		      dbl.executeSql(strSql);
		      
		      strSql = "insert into " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
              "(FNum,FSubNum,FInOut,FPortCode,FCashAccCode,FMoney,FBaseCuryRate,FPortCuryRate," +
              "FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
              dbl.sqlString(strNum) + "," +
              dbl.sqlString(YssFun.formatNumber(i + 1, "00000")) + "," +
              "-1"+","+  //1代表流入;-1代表流出
              dbl.sqlString(this.portCode) + "," +
              dbl.sqlString(cashAccCode) + "," +  //现金账户流出
              ChgMoney + "," +   //调整金额
              baseRate+ "," +
              portRate + "," +
              "1," +  //审核状态
              dbl.sqlString(pub.getUserCode()) + "," +
              dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
              dbl.sqlString(pub.getUserCode()) + "," +  
              dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
              ")";
          	  dbl.executeSql(strSql);//插入资金调拨子表
          	  
          	  conn.commit();
          	  conn.setAutoCommit(true);
          	  bTrans=false;
		}catch(Exception e){
			throw new YssException("股指期货保证金生成资金调拨数据出错！\n", e);
		}finally{
			 dbl.endTransFinal(conn, bTrans);
		}		
}
//add by zhaoxianlin  STORY #3271 期货合约保证金比例经常性调整的系统处理  获取初始保证金金额
    private double getFuBegainMoney(String securityCode,String brokerCode,java.util.Date dDate) throws YssException{
		String strSql = "";
		ResultSet rs = null;
		double startAccMoney = 0;
		try{
			//edit by songjie 2013.08.20 资产估值优化 FCashAccCode= 改为 FCashAccCode in 否则报：单行子查询返回多个行 错误
			  strSql = "select * from (select FAccBalance,FCashAccCode from "+pub.yssGetTableName("Tb_Stock_Cash")+ "  where FCashAccCode in ("
		              +" select FstartCashAccCode from "+pub.yssGetTableName("Tb_Data_OptionsValCal")+ " a join (select FexchangeCode from "
	                  +pub.yssGetTableName("tb_para_security")+" where FsecurityCode = " +dbl.sqlString(securityCode)
	                  +" and FcheckState = 1) b on 1=1 where a.fexchagecode = b.fexchangecode and FcheckState = 1 and FbrokerCode ="
	                  +dbl.sqlString(brokerCode)+") and FcheckState =1 and FStorageDate = "+dbl.sqlDate(dDate)+" ) c left join (select * from "
	                  +pub.yssGetTableName("Tb_Para_CashAccount")+" where FcheckState=1) d on c.FCashAccCode = d.fcashacccode";
		     rs = dbl.openResultSet(strSql);
		     if(rs.next()){
		    	 startAccMoney= rs.getDouble("FAccBalance");
		    	 cashAccCode = rs.getString("FCashAccCode");//现金账户
		    	 curyCode = rs.getString("FcuryCode");//币种
		     }
		    return startAccMoney;
	  }catch(Exception e){
			throw new YssException("获取股指初始保证金库存余额出错！\n", e);
      }finally{
			dbl.closeResultSetFinal(rs);
	  }		
	}
//add by zhaoxianlin  STORY #3271 期货合约保证金比例经常性调整的系统处理  获取保证金调整金额
    private double getFuBailMoney(String securityCode,String brokerCode,java.util.Date dDate,double stockNum) throws YssException{
		String strSql = "";
		ResultSet rsf = null;
		ResultSet rsFix = null;
		double FuFixV = 0;  //每手固定保证金
		double FuMultiple = 0 ;//放大倍数
		double FuBailMoney = 0;
		try{
		     strSql = " select * from "+pub.yssGetTableName("Tb_Para_IndexFutures")+ " where FcheckState = 1 and "
	                  +" FSecurityCode  = " +dbl.sqlString(securityCode);
		     rsFix = dbl.openResultSet(strSql);
		     if(rsFix.next()){
		    	 FuFixV = rsFix.getDouble("FBailFix");
		    	 FuMultiple = rsFix.getDouble("FMultiple");
		     }
		     strSql = " select * from (select * from "+pub.yssGetTableName("TB_DATA_FutureBailChange")+ " where FcheckState = 1 "
	          + " and FPortCode = "+dbl.sqlString(portCode)+ " and FBailFix <> 0 and FSecurityCode  = " +dbl.sqlString(securityCode)
	          + " and FBrokerCode = "+dbl.sqlString(brokerCode)+" order by FchangeDate  desc)  where  rownum = 1 ";
             rsf = dbl.openResultSet(strSql);
		    if(rsf.next()){
		   	     FuFixV = rsf.getDouble("FbailFix");
		    }
		    FuBailMoney =YssD.mul(stockNum, FuFixV, FuMultiple);
		    return FuBailMoney;
	  }catch(Exception e){
			throw new YssException("获取股指期货保证金调整金额出错！\n", e);
      }finally{
			dbl.closeResultSetFinal(rsf,rsFix);
	  }		
	}
    /**
     * add by songjie 2012.12.08 
     * STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
     * 获取使用加权平均法计算的 SQL 语句
     * 使用移动加权平均法不需要关联交易表
     * @param dWorkDay Date
     * @param vMethod MTVMethodBean
     * @return String
     * @throws YssException
     * @param tsfTypeCode1为05FU+期货标识
     * @param tsfTypeCode2为09FU+期货标识
     */
    private String getSqlForModAvgOne(java.util.Date dWorkDay, MTVMethodBean vMethod,
    		String tsfTypeCode1, String tsfTypeCode2) throws YssException { 
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(" SELECT cs.*, rela.FMoney as FYesValMoney, rela.FBaseCuryMoney as FYesValBaseMoney, ");
        sqlBuf.append(" rela.FPortCuryMoney as FYesValPortMoney, mk.FCsMarketPrice,mk.FMktValueDate,m.FCsPortCury ");
        sqlBuf.append(" FROM (SELECT a.FNum,a.FTransDate,a.FNum as FCsSecurityCode, ");//add by jiangshichao  BUG3609股指期货持仓在无行情的情况下，财务估值表依然显示正常交易  添加估值行情日期
        sqlBuf.append(" (a.FMoney / decode(nvl(a.FStorageAmount,0),0,1,a.FStorageAmount)) AS FTradePrice, ");//add by jiangshichao  BUG3609股指期货持仓在无行情的情况下，财务估值表依然显示正常交易  添加估值行情日期
        sqlBuf.append(" a.FStorageAmount,a.FMoney,a.FBaseCuryMoney,a.FPortCuryMoney,a.FPortCode as FCsPortCode,a.FBrokerCode,sec.FTradeCury as FCsCuryCode, ");//add by jiangshichao  BUG3609股指期货持仓在无行情的情况下，财务估值表依然显示正常交易  添加估值行情日期
        sqlBuf.append(" sec.FCatCode as FCsCatCode,sec.FFactor as FCsFactor,sec.FSubCatCode,fi.* ");
        sqlBuf.append(" FROM ");
        sqlBuf.append(" (SELECT FNUM, FTsfTypeCode, FStorageAmount, FPortCode, FMoney, FBaseCuryMoney, FPortCuryMoney, FTransDate,FBrokerCode ");
        sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_FutTradeRela"));
        sqlBuf.append(" WHERE FTransDate = " + dbl.sqlDate(dWorkDay));
        sqlBuf.append(" AND FTsfTypeCode = ");
        sqlBuf.append(dbl.sqlString(tsfTypeCode1));//05FU--当日成本余额
        sqlBuf.append(" AND FPORTCODE="+dbl.sqlString(portCode));//add by xxm,20100126.MS00953 考虑到多组合情况，只取当前组合下 的数据，否则会在多组合估值时取多次重复数据
        sqlBuf.append(" AND FCLOSENUM = ' ') a"); //modify by fangjiang 2011.01.14 BUG #991 开仓后此证券的估值增值计算错误
        sqlBuf.append(" JOIN (SELECT FSecurityCode,FSecurityName,FCatCode,FSubCatCode,FTradeCury,FFactor");
        sqlBuf.append(" FROM " + pub.yssGetTableName("tb_para_security"));
        sqlBuf.append(" WHERE FCheckState = 1");
        sqlBuf.append(" AND FCatCode = 'FU') sec ON a.FNum = sec.FSecurityCode");
        sqlBuf.append(" JOIN (SELECT ma.FLinkCode, mb.FPortCode");
        sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_MTVMethodLink"));
        sqlBuf.append(" ma");
        sqlBuf.append(" JOIN " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"));
        sqlBuf.append(" mb");
        sqlBuf.append(" ON ma.Fmtvcode = mb.fsubcode");
        sqlBuf.append(" WHERE ma.FCheckState = 1");
        sqlBuf.append(" AND mb.FCheckState = 1");
        sqlBuf.append(" AND ma.FMtvCode = " + dbl.sqlString(vMethod.getMTVCode()));
        sqlBuf.append(" AND mb.FRelaType = 'MTV'");
        sqlBuf.append(" ) b ON a.FNum = b.FLinkCode AND a.FPortCode = b.FPortCode");
        sqlBuf.append(" LEFT JOIN (SELECT FSecurityCode,FMultiple,FBailType,FFUType,FBailScale,FBailFix,FBeginBail");
        sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_IndexFutures"));
        sqlBuf.append(" WHERE FCheckState = 1) fi on a.FNum = fi.FSecurityCode");
        sqlBuf.append(" ) cs");
        sqlBuf.append(" LEFT JOIN (SELECT *");
        sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_FutTradeRela"));
        sqlBuf.append(" WHERE FTransDate = ");
        sqlBuf.append(dbl.sqlDate(YssFun.addDay(dWorkDay, -1)));
        sqlBuf.append(" AND FTsfTypeCode = ");
        sqlBuf.append(dbl.sqlString(tsfTypeCode2));//09FU--昨日估值增值余额		
        sqlBuf.append(" ) rela ");
        sqlBuf.append(" on cs.FNum = rela.FNum and cs.FBrokerCode = rela.FBrokerCode ");
        sqlBuf.append(" LEFT JOIN (SELECT ").append(vMethod.getMktPriceCode());
        sqlBuf.append(" as FCsMarketPrice, FSecurityCode, FMktValueDate from ");
        sqlBuf.append(tmpMarketValueTable);
        sqlBuf.append( " where FMktSrcCode=");
        sqlBuf.append(dbl.sqlString(vMethod.getMktSrcCode()));
        sqlBuf.append(" ) mk ON cs.FCsSecurityCode = mk.FSecurityCode");
        sqlBuf.append(" LEFT JOIN (SELECT FPortCode,FPortName,FPortCury as FCsPortCury ");
        sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_Portfolio"));
        sqlBuf.append(" WHERE  FCheckState = 1) m ON cs.FCsPortCode = m.FPortCode");
        return sqlBuf.toString();

    }
    
    /**
     * 获取使用加权平均法计算的 SQL 语句
     * 使用移动加权平均法不需要关联交易表
     * @param dWorkDay Date
     * @param vMethod MTVMethodBean
     * @return String
     * @throws YssException
     * @param tsfTypeCode1为05FU+期货标识
     * @param tsfTypeCode2为09FU+期货标识
     */
    private String getSqlForModAvg(java.util.Date dWorkDay, MTVMethodBean vMethod,
    		String tsfTypeCode1, String tsfTypeCode2) throws YssException { 
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append("SELECT cs.*, rela.FMoney, rela.FBaseCuryMoney,rela.FPortCuryMoney,mk.FCsMarketPrice,mk.FMktValueDate,m.FCsPortCury");//add by jiangshichao  BUG3609股指期货持仓在无行情的情况下，财务估值表依然显示正常交易  添加估值行情日期
        sqlBuf.append(" FROM (SELECT a.FNum,a.FTransDate,a.FNum as FCsSecurityCode, (a.FMoney / decode(nvl(a.FStorageAmount,0),0,1,a.FStorageAmount)) AS FTradePrice, a.FStorageAmount,a.FPortCode as FCsPortCode,a.FBaseCuryMoney,a.FPortCuryMoney,a.FBROKERCODE,sec.FTradeCury as FCsCuryCode,sec.FCatCode as FCsCatCode,sec.FFactor as FCsFactor,sec.FSubCatCode,fi.*");
        sqlBuf.append(" FROM ");
        sqlBuf.append(" (SELECT FNUM, FTsfTypeCode, FStorageAmount, FPortCode, FMoney, FTransDate, FBaseCuryMoney, FPortCuryMoney, FBROKERCODE");
        sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_FutTradeRela"));
        sqlBuf.append(" WHERE FTransDate = " + dbl.sqlDate(dWorkDay));
        // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
        sqlBuf.append(" AND FTsfTypeCode = ");
        sqlBuf.append(dbl.sqlString(tsfTypeCode1));		
        sqlBuf.append(" AND FPORTCODE="+dbl.sqlString(portCode));//add by xxm,20100126.MS00953 考虑到多组合情况，只取当前组合下 的数据，否则会在多组合估值时取多次重复数据
        //------------------
        sqlBuf.append(" AND FCLOSENUM = ' ') a"); //modify by fangjiang 2011.01.14 BUG #991 开仓后此证券的估值增值计算错误
        sqlBuf.append(" JOIN (SELECT FSecurityCode,FSecurityName,FCatCode,FSubCatCode,FTradeCury,FFactor");
        sqlBuf.append(" FROM " + pub.yssGetTableName("tb_para_security"));
        sqlBuf.append(" WHERE FCheckState = 1");
        sqlBuf.append(
            " AND FCatCode = 'FU') sec ON a.FNum = sec.FSecurityCode");
        sqlBuf.append(" JOIN (SELECT ma.FLinkCode, mb.FPortCode");
        sqlBuf.append(" FROM " +
                      pub.yssGetTableName("Tb_Para_MTVMethodLink"));
        sqlBuf.append(" ma");
        sqlBuf.append(" JOIN " +
                      pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"));
        sqlBuf.append(" mb");
        sqlBuf.append(" ON ma.Fmtvcode = mb.fsubcode");
        sqlBuf.append(" WHERE ma.FCheckState = 1");
        sqlBuf.append(" AND mb.FCheckState = 1");
        sqlBuf.append(" AND ma.FMtvCode = " +
                      dbl.sqlString(vMethod.getMTVCode()));
        sqlBuf.append(" AND mb.FRelaType = 'MTV'");
        sqlBuf.append(
            " ) b ON a.FNum = b.FLinkCode AND a.FPortCode = b.FPortCode");
        sqlBuf.append(" LEFT JOIN (SELECT FSecurityCode,FMultiple,FBailType,FFUType,FBailScale,FBailFix,FBeginBail");
        sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_IndexFutures"));
        sqlBuf.append(
            " WHERE FCheckState = 1) fi on a.FNum = fi.FSecurityCode");
        sqlBuf.append(" ) cs");
        sqlBuf.append(" LEFT JOIN (SELECT *");
        sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_FutTradeRela"));
        sqlBuf.append(" WHERE FTransDate = ");
        sqlBuf.append(dbl.sqlDate(YssFun.addDay(dWorkDay, -1)));
        // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
        sqlBuf.append(" AND FTsfTypeCode = ");
        sqlBuf.append(dbl.sqlString(tsfTypeCode2));		
        sqlBuf.append(" ) rela ");
        //-------------------------
        sqlBuf.append(" on cs.FNum = rela.FNum and cs.FBROKERCODE = rela.FBROKERCODE");
        //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
        //sqlBuf.append(
        //    " LEFT JOIN (SELECT mk2.FCsMarketPrice, mk2.FSecurityCode, mk1.FMktValueDate");
        //sqlBuf.append(
        //    " FROM (SELECT max(FMktValueDate) as FMktValueDate,FSecurityCode");
        //sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Data_MarketValue"));
        //sqlBuf.append(" WHERE FCheckState = 1");
        //sqlBuf.append(" AND FMktSrcCode = " +
        //              dbl.sqlString(vMethod.getMktSrcCode()));
        //sqlBuf.append(" AND FMktValueDate <= " + dbl.sqlDate(dWorkDay));
        //sqlBuf.append(" GROUP BY FSecurityCode) mk1");
        //sqlBuf.append(" JOIN (SELECT " + vMethod.getMktPriceCode());
        //sqlBuf.append(" as FCsMarketPrice,");
        //sqlBuf.append(" FSecurityCode,FMktValueDate");
        //sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Data_MarketValue"));
        //sqlBuf.append(" WHERE FCheckState = 1");
        //sqlBuf.append(" AND FMktSrcCode = " +
        //              dbl.sqlString(vMethod.getMktSrcCode()));
        //sqlBuf.append(" ) mk2 ON mk1.FSecurityCode = mk2.FSecurityCode AND mk1.FMktValueDate = mk2.FMktValueDate");
        sqlBuf.append(" LEFT JOIN (SELECT ").append(vMethod.getMktPriceCode());
        sqlBuf.append(" as FCsMarketPrice, FSecurityCode, FMktValueDate from ");
        sqlBuf.append(tmpMarketValueTable);
        sqlBuf.append( " where FMktSrcCode=");
        sqlBuf.append(dbl.sqlString(vMethod.getMktSrcCode()));
        //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
        sqlBuf.append(" ) mk ON cs.FCsSecurityCode = mk.FSecurityCode");
      
     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
        

        sqlBuf.append(" LEFT JOIN (SELECT FPortCode,FPortName,FPortCury as FCsPortCury ");
        sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_Portfolio"));
        sqlBuf.append(" WHERE  FCheckState = 1) m ON cs.FCsPortCode = m.FPortCode");
        //end by lidaolong 
        return sqlBuf.toString();

    }

    /**
     * 获取使用先入先出法计算的 SQL 语句
     * @param dWorkDay Date
     * @param vMethod MTVMethodBean
     * @throws YssException
     * @param tsfTypeCode1为09FU+期货标识
     */
    private String getSqlForFIFO(java.util.Date dWorkDay, MTVMethodBean vMethod,
    		String tsfTypeCode1) throws YssException {
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append("SELECT cs.*, rela.FMoney, rela.FBaseCuryMoney,rela.FPortCuryMoney,mk.FCsMarketPrice,mk.FMktValueDate,m.FCsPortCury");//add by jiangshichao  BUG3609股指期货持仓在无行情的情况下，财务估值表依然显示正常交易  添加估值行情日期
        /**add---huhuichao 2013-10-25 BUG  80693 股指期货的核算方式设置为先入先出法或不设置时，期货资产估值报错*/
        sqlBuf.append(" FROM (SELECT a.FNum,a.FBargainDate,a.FSecurityCode as FCsSecurityCode, a.FTradePrice," +
        		"a.fbrokercode as FBROKERCODE, tr.FStorageAmount,a.FPortCode as FCsPortCode," +
        		"sec.FTradeCury as FCsCuryCode,sec.FCatCode as FCsCatCode,sec.FFactor as FCsFactor,sec.FSubCatCode,fi.*");
        /**end---huhuichao 2013-10-25 BUG  80693*/
        sqlBuf.append(" FROM (SELECT a.*");
        //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
        sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_FuturesTrade_Tmp") +
                      " a");
        sqlBuf.append(" WHERE FCheckState = 1");
        sqlBuf.append(" AND FBargainDate <= " + dbl.sqlDate(dWorkDay));
        sqlBuf.append(" AND FPortCode = " + dbl.sqlString(portCode) + ") a");
        sqlBuf.append(
            " JOIN (SELECT FNUM, FTsfTypeCode, FStorageAmount, FPortCode");
        sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_FutTradeRela"));
        sqlBuf.append(" WHERE FTransDate = " + dbl.sqlDate(dWorkDay));
        // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
        sqlBuf.append(" AND FTsfTypeCode = ");
        sqlBuf.append(dbl.sqlString(tsfTypeCode1));		
        //------------------------
        sqlBuf.append(" AND FStorageAmount <> 0) tr");
        sqlBuf.append("  ON a.FNum = tr.FNum");
        sqlBuf.append(" JOIN (SELECT FSecurityCode,FSecurityName,FCatCode,FSubCatCode,FTradeCury,FFactor");
        sqlBuf.append(" FROM " + pub.yssGetTableName("tb_para_security"));
        sqlBuf.append(" WHERE FCheckState = 1");
        sqlBuf.append(
            " AND FCatCode = 'FU') sec ON a.FSecurityCode = sec.FSecurityCode");
        sqlBuf.append(" JOIN (SELECT ma.FLinkCode, mb.FPortCode");
        sqlBuf.append(" FROM " +
                      pub.yssGetTableName("Tb_Para_MTVMethodLink"));
        sqlBuf.append(" ma");
        sqlBuf.append(" JOIN " +
                      pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"));
        sqlBuf.append(" mb");
        sqlBuf.append(" ON ma.Fmtvcode = mb.fsubcode");
        sqlBuf.append(" WHERE ma.FCheckState = 1");
        sqlBuf.append(" AND mb.FCheckState = 1");
        sqlBuf.append(" AND ma.FMtvCode = " +
                      dbl.sqlString(vMethod.getMTVCode()));
        sqlBuf.append(" AND mb.FRelaType = 'MTV'");
        sqlBuf.append(
            " ) b ON a.Fsecuritycode = b.FLinkCode AND a.FPortCode = b.FPortCode");
        sqlBuf.append(" LEFT JOIN (SELECT FSecurityCode,FMultiple,FBailType,FFUType,FBailScale,FBailFix,FBeginBail");
        sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_IndexFutures"));
        sqlBuf.append(
            " WHERE FCheckState = 1) fi on a.Fsecuritycode = fi.FSecurityCode");
        sqlBuf.append(" WHERE tr.FStorageAmount > 0) cs");
        sqlBuf.append(" LEFT JOIN (SELECT *");
        sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_FutTradeRela"));
        sqlBuf.append(" WHERE FTransDate = ");
        sqlBuf.append(dbl.sqlDate(YssFun.addDay(dWorkDay, -1)));
        // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
        sqlBuf.append(" AND FTsfTypeCode = ");
        sqlBuf.append(dbl.sqlString(tsfTypeCode1));		
        sqlBuf.append(" ) rela ");
        //-------------------------
        sqlBuf.append(" on cs.FNum = rela.FNum");
        //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
        //sqlBuf.append(
        //    " LEFT JOIN (SELECT mk2.FCsMarketPrice, mk2.FSecurityCode, mk1.FMktValueDate");
        //sqlBuf.append(
        //    " FROM (SELECT max(FMktValueDate) as FMktValueDate,FSecurityCode");
        //sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Data_MarketValue"));
        //sqlBuf.append(" WHERE FCheckState = 1");
        //sqlBuf.append(" AND FMktSrcCode = " +
        //              dbl.sqlString(vMethod.getMktSrcCode()));
        //sqlBuf.append(" AND FMktValueDate <= " + dbl.sqlDate(dWorkDay));
        //sqlBuf.append(" GROUP BY FSecurityCode) mk1");
        //sqlBuf.append(" JOIN (SELECT " + vMethod.getMktPriceCode());
        //sqlBuf.append(" as FCsMarketPrice,");
        //sqlBuf.append(" FSecurityCode,FMktValueDate");
        //sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Data_MarketValue"));
        //sqlBuf.append(" WHERE FCheckState = 1");
        //sqlBuf.append(" AND FMktSrcCode = " +
        //              dbl.sqlString(vMethod.getMktSrcCode()));
        //sqlBuf.append(" ) mk2 ON mk1.FSecurityCode = mk2.FSecurityCode AND mk1.FMktValueDate = mk2.FMktValueDate");
        sqlBuf.append(" LEFT JOIN (SELECT ").append(vMethod.getMktPriceCode());
        sqlBuf.append(" as FCsMarketPrice, FSecurityCode, FMktValueDate from ");
        sqlBuf.append(tmpMarketValueTable);
        sqlBuf.append( " where FMktSrcCode=");
        sqlBuf.append(dbl.sqlString(vMethod.getMktSrcCode()));
        //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
        sqlBuf.append(" ) mk ON cs.FCsSecurityCode = mk.FSecurityCode");
        
     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
        
  
        sqlBuf.append(" LEFT JOIN (SELECT FPortCode,FPortName,FPortCury as FCsPortCury ");

        sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_Portfolio"));
        sqlBuf.append(" WHERE  FCheckState = 1) m ON cs.FCsPortCode = m.FPortCode");
       
        //end by lidaolong
        return sqlBuf.toString();
    }

    /**
     * 获取有库存的交易的关联数据,计算估值增值，估值增值余额
     * 使用先入先出计算股指增值使用估值增值的金额 Update 在结算关联表中的09FU01，在做库存统计是已经计算好了数量
     * 使用移动加权平均法计算是将股指增值的09FU01数据插入交易关联表中
     * @param dWorkDay Date
     * @param sPortCodes String
     * @return ArrayList
     * @throws YssException
     * modify by fangjiang 2010.08.27
     * modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
     */
    public void getTodayStockFutruesRelaData(java.util.Date dWorkDay,
                                             String portCode,
                                             ArrayList alMtvMethod) throws
        YssException {
		//---add by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
    	boolean contractType = false;
    	ParaWithPort para = new ParaWithPort();
    	para.setYssPub(pub);
    	contractType = para.getFutursPositionType(portCode.replace("'", ""));
    	//---add by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
    	
    	for(int j=0; j<4; j++) { // j=0时处理股指期货，j=1时处理债券期货，j=2时处理外汇期货 j=3 商品期货//modify huangqirong 2012-08-21  商品期货
    		String strSql = "";
            ResultSet rs = null;
            HashMap hmTradeRela = new HashMap();
            MTVMethodBean vMethod = null;
            ArrayList alValMktPrice = new ArrayList();
            EachRateOper eachOper = new EachRateOper();
            eachOper.setYssPub(pub); //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
            
            CtlPubPara pubPara = new CtlPubPara();
			pubPara.setYssPub(pub);
            Hashtable htPortAccountType = pubPara.getFurAccountType(YssOperCons.YSS_FU_ACCOUT_TYPE[j]); // 存放组合、核算代码对
			String sAccountType = getAccountTypeBy(portCode, htPortAccountType, j); //期货核算类型
            try {
                //循环估值方法
                for (int i = 0; i < alMtvMethod.size(); i++) {
                    vMethod = (MTVMethodBean) alMtvMethod.get(i);
					//---add by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
                    if(contractType){
                		strSql = getSqlForModAvgOne(dWorkDay, vMethod, 
                        		YssOperCons.YSS_ZJDBZLX_FU[j][1], YssOperCons.YSS_ZJDBZLX_FU[j][3]);
                    }else{
					//---add by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
                    	//当日库存数量不为0的开仓交易关联记录，就是要计算估值增值的记录
                    	if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
                    		// modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
                    		strSql = getSqlForModAvg(dWorkDay, vMethod, 
                        		YssOperCons.YSS_ZJDBZLX_FU[j][1], YssOperCons.YSS_ZJDBZLX_FU[j][3]);
                    	} else {
                    		// modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
                    		strSql = getSqlForFIFO(dWorkDay, vMethod, YssOperCons.YSS_ZJDBZLX_FU[j][3]);
                    	}
                    }
                    rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                    while (rs.next()) {
                        if (rs.getString("FSecurityCode") == null) {
                            throw new YssException("对不起，期货【" + rs.getString("FCsSecurityCode") + "】信息不存在或已被反审核，请核对！");
                        }
                        double dMarketPrice = rs.getDouble("FCsMarketPrice"); //行情价格
                        double dTradePrice = rs.getDouble("FTradePrice"); //交易价格
                        double dTmpAmount = rs.getDouble("FStorageAmount"); //库存数量
                        double dTmpBal = 0; //估值增值
						//---add by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
                        double dTmpBaseBal = 0; //基础货币估值增值
                        double dTmpPortBal = 0; //本位币估值增值
						//---add by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
                        double dBaseRate = 1; //基础汇率
                        double dPortRate = 1; //组合汇率
                        if (dMarketPrice == 0) {
                            continue;
                        }
                        if (rs.getString("FCsPortCury") == null) {
                            throw new YssException("请检查投资组合【" +
                                                   rs.getString("FCsPortCode") +
                                                   "】的币种设置！");
                        }
                        if (!rs.getString("FCsCuryCode").equalsIgnoreCase(pub.
                        		getPortBaseCury(rs.getString("FCsPortCode")))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                            dBaseRate = this.getSettingOper().getCuryRate(dWorkDay,
                                vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                                rs.getString("FCsCuryCode"), rs.getString("FCsPortCode"),
                                YssOperCons.YSS_RATE_BASE);
                        }

                        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
                        eachOper.getInnerPortRate(dWorkDay,
                                                  rs.getString("FCsCuryCode"), rs.getString("FCsPortCode"),
                                                  vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                                  vMethod.getPortRateSrcCode(), vMethod.getPortRateCode());
                        dPortRate = eachOper.getDPortRate();
                        
    					// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
    					// 则默认组合汇率为1
    					if (dPortRate == 0) {
    						dPortRate = 1;
    					}
    					// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
    					// 则默认组合汇率为1
                        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --

                        FuturesTradeRelaBean futRale = new FuturesTradeRelaBean();
                        futRale.setNum(rs.getString("FNum"));
                        futRale.setTransDate(dWorkDay);
                        futRale.setBaseCuryRate(dBaseRate);
                        futRale.setPortCuryRate(dPortRate);
                        futRale.setTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU[j][3]); // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
                        //2009.03.24 蒋锦 添加 MS00273 QDV4中金2009年02月27日01_A
                        futRale.setPortCode(portCode);
                        futRale.setSettleState(1);
                        
                        //---edit by songjie 2012.12.08 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
                        if(contractType){
                        	futRale.setBrokerCode(rs.getString("FBrokerCode"));
                        	//估值增值（原币） = 市值 - 成本 = 行情 * 库存数量 * 放大倍数 - 库存成本
                            dTmpBal = YssD.sub(YssD.mul(dMarketPrice, 
                                    		        	rs.getDouble("FMulTiple"),
                                    		        	dTmpAmount
                                    		        	), 
                                    		   rs.getDouble("FMoney")
                                               );
                            

                            //估值增值（基础货币） = 市值 * 汇率 - 成本 = 行情 * 库存数量 * 放大倍数 * 基础汇率 - 库存成本（基础货币）
                            
                            dTmpBaseBal = YssD.sub(this.getSettingOper().calBaseMoney(
                            													  YssD.mul(dMarketPrice, 
    		        						                                               rs.getDouble("FMulTiple"),
    		        						                                               dTmpAmount
    		        						                                               ),
                                                                                  futRale.getBaseCuryRate()
                                                                                  ), 
                		        			   rs.getDouble("FBaseCuryMoney")
                            		           );
                            
                        	//估值增值（本位币） = 市值 - 成本 = 行情 * 库存数量 * 放大倍数 * 基础汇率 / 组合汇率 - 库存成本（本位币）
                            dTmpPortBal = YssD.sub(this.getSettingOper().calPortMoney(YssD.mul(dMarketPrice, 
                         		        	                                               rs.getDouble("FMulTiple"),
                         		        	                                               dTmpAmount
                         		        	                                               ),
                                                                                  futRale.getBaseCuryRate(), 
                                                                                  futRale.getPortCuryRate(),
                                                                                  rs.getString("FCsCuryCode"), 
                                                                                  dWorkDay, 
                                                                                  this.portCode), 
                                    		   rs.getDouble("FPortCuryMoney")
                                               );
                            
                            futRale.setMoney(YssD.round(dTmpBal, 2));//原币估值增值
                            futRale.setBaseCuryMoney(dTmpBaseBal);//基础货币估值增值
                            futRale.setPortCuryMoney(dTmpPortBal);//组合货币估值增值
                        }else{
                        	//---edit by songjie 2012.12.08 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
                        	
                            //多头
                            if (rs.getString("FFUType").equalsIgnoreCase("BuyAM")) {
                                if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
                                    dTmpBal = YssD.mul(YssD.div(YssD.sub(
                                        YssD.mul(dMarketPrice, rs.getDouble("FMulTiple")), dTradePrice), rs.getInt("FCsFactor")),
                                                       dTmpAmount);
                                } else {
                                    //今日估值增值余额 = (今日行情 - 交易价格) * 交易数量 * 放大倍数
                                    dTmpBal = YssD.mul(YssD.div(YssD.sub(
                                        dMarketPrice, dTradePrice), rs.getInt("FCsFactor")),
                                                       dTmpAmount, rs.getDouble("FMulTiple"));
                                }
                            }
                            //空头
                            else {
                                if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
                                    dTmpBal = YssD.mul(YssD.div(YssD.sub(
                                        dTradePrice, YssD.mul(dMarketPrice, rs.getDouble("FMulTiple"))), rs.getInt("FCsFactor")),
                                                       dTmpAmount);

                                } else {
                                    //今日估值增值余额 = (交易价格 - 今日行情) * 交易数量 * 放大倍数
                                    dTmpBal = YssD.mul(YssD.div(YssD.sub(
                                        dTradePrice, dMarketPrice), rs.getInt("FCsFactor")),
                                                       dTmpAmount, rs.getDouble("FMulTiple"));
                                }
                            }
                            
                            futRale.setMoney(YssD.round(dTmpBal, 2));
                            //基础货币估值增值
                            futRale.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                                futRale.getMoney(),
                                futRale.getBaseCuryRate()));
                            //组合货币估值增值
                            futRale.setPortCuryMoney(this.getSettingOper().calPortMoney(
                                futRale.getMoney(),
                                futRale.getBaseCuryRate(), futRale.getPortCuryRate(),
                                //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                rs.getString("FCsCuryCode"), dWorkDay, this.portCode));
                            //add by fangjiang 2012.10.17 story 3147
                            String result = pubPara.getQYRQ("FuBbGzSf");
                            if("1,1".equals(result)){
                            	//原币市值
                            	double marketValue = YssD.mul(dTmpAmount, dMarketPrice, rs.getDouble("FMulTiple"));
                            	marketValue = YssD.round(marketValue, 2);
                            	//基础货币市值
                            	double baseMarketValue = this.getSettingOper().calBaseMoney(
                            							marketValue,
					                                    futRale.getBaseCuryRate());
                            	//基础货币成本
                        		double baseCost = rs.getDouble("FBaseCuryMoney");
                        		//组合货币市值
                        		double portMarketValue = this.getSettingOper().calPortMoney(
					                        			marketValue,
					                                    futRale.getBaseCuryRate(), futRale.getPortCuryRate(),
					                                    rs.getString("FCsCuryCode"), dWorkDay, this.portCode);
								//组合货币成本
								double portCost = rs.getDouble("FPortCuryMoney");
								if (rs.getString("FFUType").equalsIgnoreCase("BuyAM")) {
									//基础货币估值增值(市值-成本)
									futRale.setBaseCuryMoney(YssD.sub(baseMarketValue, baseCost));
									//组合货币估值增值(市值-成本)
									futRale.setPortCuryMoney(YssD.sub(portMarketValue, portCost));
								}else{
									//基础货币估值增值(成本-市值)
									futRale.setBaseCuryMoney(YssD.sub(baseCost, baseMarketValue));
									//组合货币估值增值(成本-市值)
									futRale.setPortCuryMoney(YssD.sub(portCost, portMarketValue));
								}
                            }
                            //end by fangjiang 2012.10.17 story 3147  
                        }                        
                        //edit by songjie 2012.12.08 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
                        if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG) || contractType) {
                        	futRale.setStorageAmount(rs.getDouble("FStorageAmount"));
                        }
                        futRale.setBrokerCode(rs.getString("FBROKERCODE")); //add by fangjiang 2012.10.19
						hmTradeRela.put(futRale.getNum()+"\t"+futRale.getBrokerCode(), futRale);

                        ValMktPriceBean mktPrice = new ValMktPriceBean();
                        //------------- MS00265 QDV4建行2009年2月23日01_B  -----
                        mktPrice.setValType("IndexFutruesMV"); //设置估值类型为股指期货的类型，与估值界面上的代码相一致。
                        //-----------------------------------------------------
                        //mktPrice.setValDate(dWorkDay); delete by jiangshichao  BUG3609股指期货持仓在无行情的情况下，财务估值表依然显示正常交易  
                        mktPrice.setValDate(rs.getDate("FMktValueDate"));//add by jiangshichao  BUG3609股指期货持仓在无行情的情况下，财务估值表依然显示正常交易  
						mktPrice.setSecurityCode(rs.getString("FCSSecurityCode"));
						mktPrice.setPortCode(rs.getString("FCSPortCode"));
						mktPrice.setPrice(dMarketPrice);
						hmValPrice.put(mktPrice.getSecurityCode(), mktPrice);
                    }
                    //add by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
                    if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG) || contractType) {
                    	ArrayList alrela = null;
                    	Iterator it = hmTradeRela.values().iterator();
                    	Connection conn = dbl.loadConnection();
                    	FuturesTradeRelaAdmin futTrdAdmin = new FuturesTradeRelaAdmin();
                    	futTrdAdmin.setYssPub(pub);
                    	while (it.hasNext()) {
                    		alrela = new ArrayList();
                        	//modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
                    		FuturesTradeRelaBean trdRela = (FuturesTradeRelaBean) it.next();
                    		alrela.add(trdRela); 
                        	//----------------------------
                    		strSql = "DELETE FROM " +
                            pub.yssGetTableName("TB_Data_FutTradeRela") +
                            " WHERE FTransDate = " + dbl.sqlDate(dWorkDay) +
                            " AND FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][3]) + //modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
                            " AND FPortCode = " + dbl.sqlString(portCode) +
                            " AND FNum = " + dbl.sqlString(trdRela.getNum());   //modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
                            //" and FBROKERCODE = " + dbl.sqlString(trdRela.getBrokerCode()); //modify by fangjiang 2012.10.19
                    		//---add by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
                    		//if(contractType){
                    			strSql += " and FBrokerCode = " + dbl.sqlString(trdRela.getBrokerCode());
                    		//}
                    		//---add by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
                    		dbl.executeSql(strSql);
                        	futTrdAdmin.saveMutliSetting(alrela, conn);
                    	}
                    	//====以下增加计算汇兑损益部分，移动加权时，将汇兑损益数据保存到关联表，类型为99FU01===
                    
                    } else {
                    	//使用计算好的金额更新交易关联记录
                    	Iterator it = hmTradeRela.values().iterator();
                    	while (it.hasNext()) {
                    		FuturesTradeRelaBean trdRela = (FuturesTradeRelaBean) it.next();
                    		strSql = "UPDATE " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                            " SET FMoney = " + trdRela.getMoney() +
                            " ,FBaseCuryRate = " + trdRela.getBaseCuryRate() +
                            " ,FBaseCuryMoney = " + trdRela.getBaseCuryMoney() +
                            " ,FPortCuryRate = " + trdRela.getPortCuryRate() +
                            " ,FPortCuryMoney = " + trdRela.getPortCuryMoney() +
                            " ,FSettleState = 1" +
                            " WHERE FNum = " + dbl.sqlString(trdRela.getNum()) +
                            " AND FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][3]) + // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
                            " AND FTransDate = " + dbl.sqlDate(dWorkDay);
                    		dbl.executeSql(strSql);
                    	}
                    }
                    dbl.closeResultSetFinal(rs);
                }
            } catch (Exception e) {
                throw new YssException("统计期货估值增值出错！\r\n" + e.getMessage());
            } finally {
                dbl.closeResultSetFinal(rs);
            }
    	}
    }

    /**
     * 生成股指期货应收应付数据
     * @return HashMap
     * @throws YssException
     */
    public HashMap getFuturesRecPay() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        CashPecPayBean cashPay = null;
        HashMap hmResult = new HashMap();
        String sKey = "";
        try {
            strSql = "SELECT a.*, b.*, c.FCuryCode" +
                " FROM (SELECT a.FNum, a.FTsfTypeCode, a.FTransDate, a.FStorageAmount, a.FBaseCuryRate," +
                " a.FPortCuryRate, a.FSettleState," +
                " a.FMoney - CASE" +
                " WHEN b.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " b.FMoney" +
                " END + CASE" +
                " WHEN c.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " c.FMoney" +
                " END AS FMoney," +
                " (a.FMoney - CASE" +
                " WHEN b.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " b.FMoney" +
                " END + CASE" +
                " WHEN c.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " c.FMoney" +
                " END) * a.FBaseCuryRate AS FBaseCuryMoney," +
                " (a.FMoney - CASE" +
                " WHEN b.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " b.FMoney" +
                " END + CASE" +
                " WHEN c.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " c.FMoney" +
                " END) * a.FBaseCuryRate / a.FPortCuryRate AS FPortCuryMoney" +
                " FROM (SELECT *" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(this.dDate) +
                " AND FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU01_MV) +
                " AND FMoney <> 0) a" +
                " Left JOIN (SELECT FNum, FMoney" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(YssFun.addDay(this.dDate, -1)) +
                " AND FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU01_MV) + ") b ON a.FNum = b.FNum" +
                " LEFT JOIN (SELECT FNum, FMoney" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(this.dDate) +
                " AND FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU01_SMV) + ") c ON a.FNum = c.FNum" +
                " UNION" +
                " SELECT FNum, FTsfTypeCode, FTransDate, FStorageAmount, FBaseCuryRate," +
                " FPortCuryRate, FSettleState, FMoney, FBaseCuryMoney, FPortCuryMoney" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(this.dDate) +
                " AND FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU01_SMV) +
                " AND FMoney <> 0) a" +
                //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
                " JOIN " + pub.yssGetTableName("TB_Data_FuturesTrade_Tmp") +
                " b ON a.FNum = b.Fsecuritycode " +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " c ON b.FChageBailAcctCode = c.FCashAccCode" +
                " WHERE a.FSettleState = 1" +
                " AND b.FPortCode = " + dbl.sqlString(this.portCode);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //如果是卖出估值增值 跳过 sunkey 20081124 BugID:MS00013
                if (rs.getString("FTsfTypeCode").equalsIgnoreCase(YssOperCons.
                    YSS_ZJDBZLX_FU01_SMV)) {
                    continue;
                }
                cashPay = new CashPecPayBean();
                cashPay.setPortCode(rs.getString("FPortCode"));
                cashPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                cashPay.setBrokerCode(rs.getString("FBrokerCode"));
                cashPay.setCashAccCode(rs.getString("FChageBailAcctCode"));
                cashPay.setCategoryCode(" ");
                cashPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
                cashPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                cashPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_REC);
                cashPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                cashPay.setMoney(rs.getDouble("FMoney"));
                cashPay.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
                cashPay.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
                if (rs.getString("FTsfTypeCode").equalsIgnoreCase(YssOperCons.YSS_ZJDBZLX_FU01_MV)) {
                    cashPay.setInOutType(1);
                } else {
                    cashPay.setInOutType( -1);
                }
                cashPay.setTradeDate(rs.getDate("FTransDate"));
                cashPay.setCuryCode(rs.getString("FCuryCode"));
                sKey = cashPay.getPortCode() + "\f" +
                    cashPay.getCashAccCode() + "\f" +
                    (this.invmgrCashField.length() != 0 ?
                     (cashPay.getInvestManagerCode() + "\f") : "") +
                    (this.catCashField.length() != 0 ?
                     (cashPay.getCategoryCode() + "\f") : "") +
                    cashPay.getSubTsfTypeCode() + "\f" +
                    rs.getString("FNUM"); //添加交易编号作为hashmap键值的一部分，避免上面的键存在重复造成数据丢失 sunkey 20081124 BugID:MS00013
                // start dongqingsong 2013-09-24 BUG #79801 
				cashPay.checkStateId=1;
                // end dongqingsong 2013-09-24 BUG #79801 
                hmResult.put(sKey, cashPay);

                //产生一条对冲数据 sunkey 20081127 BugID:MS00013
                cashPay = new CashPecPayBean();
                cashPay.setPortCode(rs.getString("FPortCode"));
                cashPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                cashPay.setBrokerCode(rs.getString("FBrokerCode"));
                cashPay.setCashAccCode(rs.getString("FChageBailAcctCode"));
                cashPay.setCategoryCode(" ");
                cashPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
                cashPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                cashPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU_Income);
                cashPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                cashPay.setMoney(rs.getDouble("FMoney"));
                cashPay.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
                cashPay.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
                if (rs.getString("FTsfTypeCode").equalsIgnoreCase(YssOperCons.YSS_ZJDBZLX_FU01_MV)) {
                    cashPay.setInOutType(1);
                } else {
                    cashPay.setInOutType( -1);
                }
                cashPay.setTradeDate(rs.getDate("FTransDate"));
                cashPay.setCuryCode(rs.getString("FCuryCode"));
                sKey = cashPay.getPortCode() + "\f" +
                    cashPay.getCashAccCode() + "\f" +
                    (this.invmgrCashField.length() != 0 ?
                     (cashPay.getInvestManagerCode() + "\f") : "") +
                    (this.catCashField.length() != 0 ?
                     (cashPay.getCategoryCode() + "\f") : "") +
                    cashPay.getSubTsfTypeCode() + "\f" +
                    rs.getString("FNUM"); //添加交易编号作为hashmap键值的一部分，避免上面的键存在重复造成数据丢失 sunkey 20081124 BugID:MS00013
				// start dongqingsong 2013-09-24 BUG #79801 
			   cashPay.checkStateId=1;
			   // end dongqingsong 2013-09-24 BUG #79801 
                hmResult.put(sKey, cashPay);

            }
        } catch (Exception e) {
            throw new YssException("获取期货应收应付数据出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    public Object filterCashCondition() {
        CashPecPayBean cashpay = new CashPecPayBean();
        /**add---huhuichao 2013-12-11 BUG  85058  60sp4版本重复调度产生重复的02应收应付数据*/
        cashpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec+","+YssOperCons.YSS_ZJDBLX_Income);
        cashpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_REC+","+YssOperCons.YSS_ZJDBZLX_FU_Income);
        /**end---huhuichao 2013-12-11 BUG  85058 */
        return cashpay;
    }

    /**
     * 估值增值收益资金调拨，当天的估值增值发生额
     * 估值增值收益=今日估值增值-昨日估值增值+今日卖出估值增值
     * 使用移动加权平均法计算时要关联证券代码和组合，证券代码存在TB_DATA_FUTTRADERELA表的FNum字段中
     * @param dwork Date 业务日期(估值日期)
     * @param portCode String 组合代码
     * @return double
     * @version sunkey 20081124 BugID:MS00013
     * modify by fangjiang 2010.08.27
     * modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
     * 
     */
    public void valutionGainsTransfer(java.util.Date dwork, String portCode) throws YssException {
    	//add by songjie 2011.01.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
        double dBaseRate = 1;
        double dPortRate = 1;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //add by songjie 2011.01.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
    	for(int j=0; j<4; j++) { // j=0时处理股指期货，j=1时处理债券期货, j=2时处理外汇期货 j = 3 商品期货 //modify huangqirong 2012-08-21  商品期货
	        ResultSet rs = null;
	        double money = 0;
	        CashTransAdmin tranAdmin = new CashTransAdmin();
	        tranAdmin.setYssPub(pub);
	        StringBuffer buf = new StringBuffer();
	        //2009-3-24 蒋锦 修改  MS00273 QDV4中金2009年02月27日01_A
	        CtlPubPara pubPara = new CtlPubPara();
			pubPara.setYssPub(pub);
            Hashtable htPortAccountType = pubPara.getFurAccountType(YssOperCons.YSS_FU_ACCOUT_TYPE[j]); // 存放组合、核算代码对
			String sAccountType = getAccountTypeBy(portCode, htPortAccountType, j); //期货核算类型
	    	//---add by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
	    	boolean contractType = false;
	    	ParaWithPort para = new ParaWithPort();
	    	para.setYssPub(pub);
	    	contractType = para.getFutursPositionType(portCode);
	    	ArrayList result = null;
	    	
	    	if(contractType){
	            buf.append("SELECT T.FMONEY AS TMONEY, Y.FMONEY AS YMONEY, TS.FMONEY TSMONEY,");
	            buf.append("TRADE.FBASECURYRATE,TRADE.FPORTCURYRATE,TRADE.FCHAGEBAILACCTCODE,");
	            buf.append("TRADE.FSECURITYCODE,TRADE.FBARGAINDATE,TRADE.FSETTLEDATE,");
	            buf.append("TRADE.FInvMgrCode,T.FNUM,T.FBrokerCode as FBrokerCode,TRADE.FCashAccCode");
	            buf.append(",TRADE.FNum as TradeNum,TRADE.FTradeCury as FCuryCode, T.FPortCode as FPortCode, m.FCsPortCury as FCsPortCury");
	            buf.append(" FROM (SELECT FMONEY, FNUM, FPortCode,FBrokerCode");
	            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
	            buf.append(" WHERE FTRANSDATE =" + dbl.sqlDate(dwork));
	            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
	            buf.append(" AND FTSFTYPECODE = ");
	            buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][3]));//09FU 当日估值增值余额
            	buf.append(" AND FSTORAGEAMOUNT<>0) T");
            	buf.append(" LEFT JOIN (SELECT FMONEY, FNUM, FPortCode,FBrokerCode");
            	buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
            	buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(YssFun.addDay(dwork, -1)));
            	buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            	buf.append(" AND FTSFTYPECODE = ");
            	buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][3]));//09FU 昨日估值增值余额
            	buf.append(" ) Y ON T.FNUM = Y.FNUM AND T.FPortCode = Y.FPortCode and T.FBrokerCode = Y.FBrokerCode ");
            	buf.append(" LEFT JOIN (SELECT SUM(FMONEY) AS FMONEY, FNUM, FPortCode,FBrokerCode ");
            	buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
            	buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(dwork));
            	buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            	buf.append(" AND FTSFTYPECODE = "); 
            	buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][4]));	//当日卖出估值增值
            	buf.append(" GROUP BY FNum, FPortCode,FBrokerCode ) TS ON T.FNUM = TS.FNUM AND T.FPortCode = TS.FPortCode and T.FBrokerCode = TS.FBrokerCode");
				buf.append(" LEFT JOIN (select ss.*,op.fcashacccode as FCashAccCode from(SELECT tr2.*, s.fexchangecode, s.ftradecury ");
				buf.append(" FROM (SELECT MAX(FNum) AS FNum, FSECURITYCODE, FPortCode,FBrokerCode ");
				buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FutTradeSplit "));
            	buf.append(" WHERE FCheckState = 1 ");
            	buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
            	buf.append(" AND FBargainDate <= ").append(dbl.sqlDate(dwork));
            	buf.append(" GROUP BY FSecurityCode, FPortCode,FBrokerCode) tr1 ");
            	buf.append(" JOIN (SELECT FBASECURYRATE, FPORTCURYRATE, FCHAGEBAILACCTCODE, ");
            	buf.append(" FNUM, FPortCode, FSECURITYCODE, FBARGAINDATE, ");
            	buf.append(" FSETTLEDATE, FInvMgrCode, FBrokerCode");
            	buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FutTradeSplit "));
            	buf.append(" WHERE FPORTCODE = " + dbl.sqlString(portCode) + ") tr2 ON tr1.FNum = tr2.FNum and tr1.FBrokerCode = tr2.FBrokerCode");
            	buf.append(" join (select * from ");
            	buf.append(pub.yssGetTableName("tb_para_security"));//证券信息表
            	buf.append(" where FCheckState = 1 and FSubCatCode = " + dbl.sqlString(YssOperCons.YSS_FU[j]));
            	buf.append(") s on s.FSecurityCode = tr1.FSecurityCode) ss");
            	buf.append(" join (select * from ");
            	buf.append(pub.yssGetTableName("tb_data_optionsvalcal"));//期权和期货保证金账户设置表
            	buf.append(" where FCheckState = 1 and FMarkType=1) op on ss.FPortCode = op.FPortCode and ss.fexchangecode = op.fexchagecode");
				buf.append(" and ss.FCHAGEBAILACCTCODE=op.fcashacccode");
            	buf.append(") TRADE ON T.FNUM = ");
            	buf.append(" TRADE.FSecurityCode  AND T.FPortCode = TRADE.FPortCode and T.FBrokerCode = TRADE.FBrokerCode");
            	buf.append(" LEFT JOIN ( SELECT FPortCode, FPortName, FPortCury as FCsPortCury FROM ");
            	buf.append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" WHERE  FCheckState = 1 ) m ON T.FPortCode = m.FPortCode ");
	    	}else{
			//---add by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
	        if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
	            buf.append(
	                "SELECT T.FMONEY AS TMONEY, T.FNUM, T.FPortCode, Y.FMONEY AS YMONEY, TS.FMONEY TSMONEY,");
	            buf.append(
	                "TRADE.FSECURITYCODE, TRADE.FCHAGEBAILACCTCODE as FCashAccCode, m.FCsPortCury, n.fcurycode");	           
				//fanghaoln 20100318 MS01006 QDV4华夏2010年3月02日01_B
				//xuqiji 20090810:QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定
				//-------------------------end--------MS01006-------------------------
	            //====add by xuxuming,20091203.将交易编号也查询出来,因为要将此编号插入资金调拨表中,以便删除时有依据=====
				//edit by songjie 2011.02.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
	            //========================end=======================
	            buf.append(" FROM (SELECT FMONEY, FNUM, FPortCode, FBROKERCODE");
	            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
	            buf.append(" WHERE FTRANSDATE =" + dbl.sqlDate(dwork));
	            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));//add by xxm,2010.01.26.多组合估值 时，要根据组合代码来查询数据 MS00953
	            // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
	            buf.append(" AND FTSFTYPECODE = ");
	            buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][3]));//09FU 当日估值增值余额
	            buf.append(" AND FSTORAGEAMOUNT<>0) T");
	            //---------------------
	            buf.append(" LEFT JOIN (SELECT FMONEY, FNUM, FPortCode, FBROKERCODE");
	            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
	            buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(YssFun.addDay(dwork, -1)));
	            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));//add by xxm,2010.01.26.多组合估值 时，要根据组合代码来查询数据 MS00953
	            // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
	            buf.append(" AND FTSFTYPECODE = ");
	            buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][3]));//09FU 昨日估值增值余额
	            buf.append(" ) Y ON T.FNUM = Y.FNUM AND T.FPortCode = Y.FPortCode and T.FBROKERCODE = Y.FBROKERCODE");
	            //---------------------
	            buf.append(" LEFT JOIN (SELECT SUM(FMONEY) AS FMONEY, FNUM, FPortCode, FBROKERCODE");
	            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
	            buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(dwork));
	            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));//add by xxm,2010.01.26.多组合估值 时，要根据组合代码来查询数据 MS00953
	            // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
	            buf.append(" AND FTSFTYPECODE = "); 
	            buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][4]));	//当日卖出估值增值
	            buf.append(" GROUP BY FNum, FPortCode, FBROKERCODE) TS ON T.FNUM = TS.FNUM AND T.FPortCode = TS.FPortCode and T.FBROKERCODE = TS.FBROKERCODE");
	            //--------------------------------
				//fanghaoln 20100318 MS01006 QDV4华夏2010年3月02日01_B
	            //edit by songjie 2011.02.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
				buf.append(" LEFT JOIN  ");//xuqiji 20090810:QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定
				//fanghaoln 20100318 MS01006 QDV4华夏2010年3月02日01_B
	            buf.append(" (SELECT MAX(FNum) AS FNum, FSECURITYCODE, FPortCode,FBROKERCODE,FCHAGEBAILACCTCODE ");
	            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
	            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTURESTRADE_Tmp"));
	            buf.append(" WHERE FCheckState = 1 ");
	            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));
	            buf.append(" AND FBargainDate <= ").append(dbl.sqlDate(dwork));
	            buf.append(" GROUP BY FSecurityCode, FPortCode, FBROKERCODE, FCHAGEBAILACCTCODE");
				//--------------------------end ------MS01006----------------------
	            buf.append(") TRADE ON T.FNUM = ");
	            //------------------------------------------end 20090810------------------------------------//
	            buf.append(" TRADE.FSecurityCode  AND T.FPortCode = TRADE.FPortCode and T.FBROKERCODE = TRADE.FBROKERCODE");	            
	            buf.append(" LEFT JOIN ( SELECT FPortCode, FPortName, FPortCury as FCsPortCury FROM ");
	            buf.append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" WHERE  FCheckState = 1 ) m ON T.FPortCode = m.FPortCode ");
	            buf.append(" LEFT JOIN ( select fcashacccode, fcurycode FROM ");
	            buf.append(pub.yssGetTableName("Tb_para_cashaccount")).append(" WHERE  FCheckState = 1 ) n ON Trade.fchagebailacctcode = n.fcashacccode ");
	            
	        } else {
	            buf.append(
	                "SELECT T.FMONEY AS TMONEY, Y.FMONEY AS YMONEY, TS.FMONEY TSMONEY,");
	            buf.append(
	                "TRADE.FBASECURYRATE,TRADE.FPORTCURYRATE,TRADE.FCHAGEBAILACCTCODE,");
	            buf.append("TRADE.FSECURITYCODE,TRADE.FBARGAINDATE,TRADE.FSETTLEDATE,");
				//fanghaoln 20100318 MS01006 QDV4华夏2010年3月02日01_B
				buf.append("TRADE.FInvMgrCode,TRADE.FNUM,TRADE.FCashAccCode");//xuqiji 20090810:QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定
				//--------------------------end MS01006--------------------------------------
				//====add by xuxuming,20091203.将交易编号也查询出来,因为要将此编号插入资金调拨表中,以便删除时有依据=====
				//edit by songjie 2011.01.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
	            buf.append(",TRADE.FNum as TradeNum, TRADE.FPortCode as FPortCode, TRADE.FTradeCury as FCuryCode, m.FCsPortCury as FCsPortCury ");
	            //========================end=======================
	            buf.append(" FROM (SELECT FMONEY, FNUM");
	            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
	            buf.append(" WHERE FTRANSDATE =" + dbl.sqlDate(dwork));
	            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));//add by xxm,2010.01.26.多组合估值 时，要根据组合代码来查询数据 MS00953
	            // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
	            buf.append(" AND FTSFTYPECODE = ");
	            buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][3]));	
	            buf.append(" AND FSTORAGEAMOUNT<>0) T");
	            // ----------------------------------------
	            buf.append(" LEFT JOIN (SELECT FMONEY, FNUM");
	            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
	            buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(YssFun.addDay(dwork, -1)));
	            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));//add by xxm,2010.01.26.多组合估值 时，要根据组合代码来查询数据 MS00953
	            // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
	            buf.append(" AND FTSFTYPECODE = ");
	            buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][3]));
	            buf.append(" ) Y ON T.FNUM = Y.FNUM");
	            //---------------------------
	            //----2010-5-12 蒋锦 修改 查询卖出估值增值时添加Group By 查出FMoney的合计值，当一笔开仓被多笔平仓时不能分开计算-------//
	            //MS01133 《QDV4深圳2010年04月28日01_A》
	            buf.append(" LEFT JOIN (SELECT SUM(FMONEY) AS FMoney, FNUM");
	            buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTTRADERELA"));
	            buf.append(" WHERE FTRANSDATE = " + dbl.sqlDate(dwork));
	            buf.append(" AND FPortCode = ").append(dbl.sqlString(portCode));//add by xxm,2010.01.26.多组合估值 时，要根据组合代码来查询数据 MS00953
	            // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
	            buf.append(" AND FTSFTYPECODE = ");
	            buf.append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][4]));
	            buf.append(" GROUP BY FNUM) TS ON T.FNUM = TS.FNUM");
	            // ------------------------
	            //--------------------------------//
				//xuqiji 20090810:QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定
				buf.append(" JOIN (select aa.*,op.fcashacccode as FCashAccCode from(");//fanghaoln 20100318 MS01006 QDV4华夏2010年3月02日01_B
				buf.append("SELECT f.FBASECURYRATE,f.FPORTCURYRATE,f.FCHAGEBAILACCTCODE,FNUM,");
				//edit by songjie 2011.01.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
				buf.append("f.FSECURITYCODE,f.FBARGAINDATE,f.FSETTLEDATE,f.FInvMgrCode,f.fportcode,s.fexchangecode, s.Ftradecury");
				//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
				buf.append(" FROM " + pub.yssGetTableName("TB_DATA_FUTURESTRADE_Tmp")).append(" f ");
				buf.append(" join (select * from ");
				buf.append(pub.yssGetTableName("tb_para_security"));//证券信息表
				buf.append(" where FCheckState = 1) s on f.fsecuritycode = s.fsecuritycode) aa");
				buf.append(" join (select * from ");
				buf.append(pub.yssGetTableName("tb_data_optionsvalcal"));//期权和期货保证金账户设置表
				buf.append(" where FCheckState = 1 and FMarkType=1) op on aa.FPORTCODE = op.FPORTCODE and aa.fexchangecode = op.fexchagecode");
				//fanghaoln 20100318 MS01006 QDV4华夏2010年3月02日01_B
				buf.append(" and aa.FCHAGEBAILACCTCODE=op.fcashacccode");
				//--------------------------end ------MS01006----------------------
				buf.append(" WHERE aa.FPORTCODE = '" + portCode +
	                   "') TRADE ON T.FNUM =TRADE.FNUM");
	        //----------------------------------------end 20090810------------------------------------------//
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				
				
				//add by songjie 2011.01.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
	         /*   buf.append(" LEFT JOIN (SELECT mb.* FROM (SELECT FPortCode, MAX(FStartDate) as FStartDate FROM ");
	            buf.append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" WHERE FStartDate <= ").append(dbl.sqlDate(dwork));
	            buf.append(" AND FCheckState = 1 GROUP BY FPortCode) ma JOIN (");
	            buf.append(" SELECT FPortCode, FPortName, FStartDate, FPortCury as FCsPortCury FROM ");
	            buf.append(pub.yssGetTableName("Tb_Para_Portfolio"));
	            buf.append(" ) mb ON ma.FPortCode = mb.FPortCode AND ma.FStartDate = mb.FStartDate) m ON TRADE.FPortCode = m.FPortCode ");
			*/	//add by songjie 2011.01.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
	            
				
				   buf.append(" LEFT JOIN ( SELECT FPortCode, FPortName, FPortCury as FCsPortCury FROM ");
		            buf.append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" WHERE  FCheckState = 1 ) m ON TRADE.FPortCode = m.FPortCode ");
				
	            //end by lidaolong
	        }
	    	}
	        try {
	            //是否有分析代码
	            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
	            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
	
	            rs = dbl.queryByPreparedStatement(buf.toString()); //modify by fangjiang 2011.08.14 STORY #788
	            while (rs.next()) {
	            	if(rs.getString("FCashAccCode") == null){
	            		continue;
	            	}
	                money = YssD.sub(rs.getDouble("TMONEY"),
	                                 YssD.sub(rs.getDouble("YMONEY"),
	                                          rs.getDouble("TSMONEY"))); //今日估值增值-(昨日估值增值-今日卖出估值增值)
	                //====================================资金调拨================================================
	                TransferBean tran = new TransferBean();
	                TransferSetBean transfersetIn = new TransferSetBean();
	                ArrayList tranSetList = new ArrayList();
	
	                //增加资金调拨记录
	                tran.setYssPub(pub);
	                tran.setDtTransDate(dwork); //业务日期
	                tran.setDtTransferDate(dwork); //调拨日期
	                tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //估值增值
	                // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
	                tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU[j][3]); //股指期货估值增值
	                // ----------------------------
	                tran.setStrTransferTime("00:00:00"); //调拨时间
	                tran.setStrSecurityCode(rs.getString("FSECURITYCODE")); //证券代码
	                
	                //---edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
	                if(contractType){
	                	tran.setStrTradeNum(rs.getString("TradeNum")); //交易编号
	                }else{
	                	tran.setStrTradeNum(rs.getString("FNUM")); //交易编号
	                }
	                //---edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
	                
	                //add by xuxuming,当上面IF判断中,sAccountType为MODAVG时,FNUM中保存的是证券代码,所以需要加上TRADENUM字段
					if (dbl.isFieldExist(rs, "TradeNum")) {
						tran.setFRelaNum(rs.getString("TradeNum"));// 这里保存的是交易编号,add
																	// by
																	// xuxuming,20091203.因为删除交易数据时,以此来删除资金调拨
					}
	                tran.setDataSource(0);
	                tran.checkStateId = 1;
	                tran.creatorTime = YssFun.formatDate(new java.util.Date(),
	                    "yyyyMMdd HH:mm:ss");
	
	                //资金流入帐户
	                transfersetIn.setDMoney(money);
	                transfersetIn.setSPortCode(portCode);
	                //判断是否需要分析
	                if (analy1) {
	                    transfersetIn.setSAnalysisCode1(rs.getString("FInvMgrCode")); //投资经理
	                }
	                if (analy2) {
	                    transfersetIn.setSAnalysisCode2("FU"); //证券类型 -- 期货
	                }
	                
	                //add by songjie 2011.01.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
                    dBaseRate = 1;
                    if (!rs.getString("FCuryCode").equalsIgnoreCase(pub.getPortBaseCury("FPortCode"))) {
		                    dBaseRate = this.getSettingOper().getCuryRate(dwork, rs.getString("FCuryCode"),
                            rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_BASE);
                    }

                    if (rs.getString("FCsPortCury") == null) {
                        throw new YssException("请检查投资组合【" + rs.getString("FPortCode") + "】的币种设置！");
                    }

                    rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode")); //用通用方法，获取组合汇率
                    dPortRate = rateOper.getDPortRate(); //获取组合汇率
                    
					if (dPortRate == 0) {
						dPortRate = 1;
					}
					
					transfersetIn.setDBaseRate(dBaseRate); //基础汇率
					transfersetIn.setDPortRate(dPortRate); //组合汇率
					//add by songjie 2011.01.28 BUG:1022 QDV4赢时胜（深圳）2011年1月27日01_B
					
	                //xuqiji 20090810:QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定
	                //结转账户从期权和期货保证金账户设置表中取得
					transfersetIn.setSCashAccCode(rs.getString("FCashAccCode")); //变动保证金账户 fanghaoln 20100318 MS01006 QDV4华夏2010年3月02日01_B
	                transfersetIn.setIInOut(1); //流入
	                transfersetIn.checkStateId = 1; //已审核
	
	                tranSetList.add(transfersetIn);
	                tran.setSubTrans(tranSetList);
	                tranAdmin.addList(tran);
	
	            }
	            dbl.closeStatementFinal(rs.getStatement());
	
	            //保存资金调拨,保存的时候直接删除当天所有的估值增值收益资金调拨
	//            tranAdmin.insert(dwork, dwork, YssOperCons.YSS_ZJDBLX_MV,
	//                             YssOperCons.YSS_ZJDBZLX_FU01_MV,
	//                             "", -1);//del by xuxuming,20091228.此处直接删除了当天所有估值增值收益的资金调 拨，应该只删除当前组合的
	            //==MS00897,股指期货资产估值时会直接删除资金调拨当天所有的股指期货估值增值  
	            // modify by fangjiang 2010.08.28 MS01439 QDV4博时2010年7月14日02_A 
	            tranAdmin.insert("", dwork, dwork, YssOperCons.YSS_ZJDBLX_MV, YssOperCons.YSS_ZJDBZLX_FU[j][3], "", "", "", "",
	                    "", "", "",
	                    -1, "", portCode, 0, "", "", "", true, "");//add by xuxuming,加上组合代码
	            //-----------------------------
	        } catch (Exception ex) {
	            throw new YssException("对不起,计算股指期货估值增值收益出错！", ex);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
    	}
    }
    
    //add by fangjiang 2011.12.17 STORY #1886
    private void updateStorageDate() throws YssException {
    	String strSql = "";
    	String sql = "";
    	ResultSet rs = null;
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		CtlPubPara pubPara = new CtlPubPara();
		pubPara.setYssPub(pub);
		Hashtable htPortAccountType = null;
		String sAccountType = "";
		CtlPubPara pubpara = new CtlPubPara();
		pubpara.setYssPub(this.pub);
    	try{
    		if(!pubpara.getParaValue("ISShowQHCost", "selPort", "cboISCredit", this.portCode)){
    			return;
    		}
    		strSql = " update " + pub.yssGetTableName("tb_stock_security") + " set FStorageCost = ? , " +
			         " FMStorageCost = ? , FVStorageCost = ? , FBaseCuryCost = ? , FMBaseCuryCost = ? " + 
			         " , FVBaseCuryCost = ? , FPortCuryCost = ? , FMPortCuryCost = ? , FVPortCuryCost = ? " +
			         " where fstoragedate = ? and FPortCode = ? and FSecurityCode = ? ";
	        pst = conn.prepareStatement(strSql);
    	
    		for(int j=0; j<3; j++) { // j=0时处理股指期货，j=1时处理债券期货，j=2时处理外汇期货                 
                htPortAccountType = pubPara.getFurAccountType(YssOperCons.YSS_FU_ACCOUT_TYPE[j]); // 存放组合、核算代码对
    			sAccountType = getAccountTypeBy(portCode, htPortAccountType, j); //期货核算类型
    			if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
    				sql = " select * from " + pub.yssGetTableName("tb_data_futtraderela") +
    				      " where ftsftypecode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU[j][1]) +//05FU 汇总的库存成本
    				      " and ftransdate = " + dbl.sqlDate(this.dDate) + " and fclosenum = ' ' and FPortCode = " + dbl.sqlString(this.portCode);
    				rs = dbl.openResultSet(sql);
    				while(rs.next()){
    					pst.setDouble(1, rs.getDouble("fmoney"));
    					pst.setDouble(2, rs.getDouble("fmoney"));
    					pst.setDouble(3, rs.getDouble("fmoney"));
    					pst.setDouble(4, rs.getDouble("fbasecurymoney"));
    					pst.setDouble(5, rs.getDouble("fbasecurymoney"));
    					pst.setDouble(6, rs.getDouble("fbasecurymoney"));
    					pst.setDouble(7, rs.getDouble("fportcurymoney"));
    					pst.setDouble(8, rs.getDouble("fportcurymoney"));
    					pst.setDouble(9, rs.getDouble("fportcurymoney"));
    					pst.setDate(10, rs.getDate("ftransdate"));
    					pst.setString(11, rs.getString("fportcode"));
    					pst.setString(12, rs.getString("fnum"));
    					pst.addBatch();	
    				}
    			}
    		}
    		conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
            pst.executeBatch();
    		conn.commit();
            bTrans = false;
	    } catch (Exception ex) {
	        throw new YssException("更新期货数据库成本出错！", ex);
	    } finally {
	        dbl.closeResultSetFinal(rs);
	        dbl.closeStatementFinal(pst);
   			dbl.endTransFinal(conn, bTrans);
	    }
    }
    
   /*private void dealBailChange() throws YssException {
       String strSql = "";
 	   ResultSet rs = null;	
 	   double baseRate = 0;
  	   double portRate = 0;
 	   CashTransAdmin tranAdmin = new CashTransAdmin();
 	   ArrayList tranSetList = null;
 	   TransferBean tran = null;
 	   TransferSetBean transferset = null;	   
   	   try{
   		   strSql = " select * from " + pub.yssGetTableName("Tb_Data_DBCGT") + 
   		            " where FDate = " + dbl.sqlDate(dDate) + " and FPORTCODE = " + 
   		            dbl.sqlString(sPortCode) + " and FTradeTypeCode = '02' and FCGT > 0 ";
   		   rs = dbl.queryByPreparedStatement(strSql);
   		   while (rs.next()){
   			   tran = new TransferBean();
 	  		   tran.setDtTransDate(this.dDate); //业务日期 
   			   tran.setDtTransferDate(rs.getDate("FSettleDate")); //调拨日期
   			   tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); //调拨类型
 	  		   tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_CGT_Pay); //调拨子类型
 	  		   tran.setFRelaNum(rs.getString("FNum")); //关联编号 
 	  		   tran.setFNumType("CGT"); //编号类型
 	  		   tran.setDataSource(1);   //数据来源,0表示手动，1表示自动，默认为0
 	  		   tran.checkStateId = 1;
 	  		   tran.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
   			   			   
   			   baseRate = this.getSettingOper().getCuryRate(
 					  		  this.dDate, 
 						      rs.getString("FCuryCode"), 
 						      this.sPortCode, 
 						      YssOperCons.YSS_RATE_BASE
 			  			  );  
 	           portRate = this.getSettingOper().getCuryRate(
 						      this.dDate, 
 						      rs.getString("FCuryCode"), 
 						      this.sPortCode, 
 						      YssOperCons.YSS_RATE_PORT
 	           			  );
 	           
 	           transferset = new TransferSetBean();
 	           transferset.setDBaseRate(baseRate);
 	           transferset.setDPortRate(portRate);
   			   transferset.setDMoney(rs.getDouble("FCGT")); //调拨金额
   			   transferset.setSPortCode(this.sPortCode);    // 组合代码
   			   transferset.setSCashAccCode(rs.getString("FCashAccCode")); //现金帐户代码
   			   transferset.setIInOut(-1); //资金流向 ，1代表流入，-1代表流出
   			   transferset.checkStateId = 1;			   
   			   
   			   tranSetList = new ArrayList();
  			   tranSetList.add(transferset);
  			   
  			   tran.setSubTrans(tranSetList);			   
  			   tranAdmin.addList(tran); 
   		   }
  		   tranAdmin.setYssPub(pub);		   
  		   tranAdmin.insert(this.dDate, "CGT", this.sPortCode, "");
   	   } catch (Exception e) {
   		   throw new YssException("获取数据出错！", e);
       } finally {
       	   dbl.closeResultSetFinal(rs);
       }
   }*/
        
}
