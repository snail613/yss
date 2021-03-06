package com.yss.main.operdeal.datainterface.cnstock.szstock;

import com.yss.util.*;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;

import java.sql.*;
import java.util.*;

import com.yss.main.operdeal.datainterface.cnstock.pojo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.SecurityBean;
import com.yss.main.datainterface.cnstock.*;

/**
 * 深圳股份库接口，主要储存权益数据
 * 用于处理深圳股份库文件到系统的交易接口清算库中
 * QDV4.1赢时胜（上海）2009年4月20日08_A  MS00008
 * created by leeyu
 * 2009-06-04
 */
public class SZGFBean extends DataBase{
    HashMap hmSubAssetType = null; //用于储存已选组合代码对应的资产子类型
    //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
    HashMap hmParam = null;
    //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
    HashMap hmShowZqdm = new HashMap();
    
    java.util.Date startDate = null; //add by huangqirong 2012-07-27 story #2760 获取 深交所中小企业板“003000－004999”证券代码区间的启用日期
    
    public HashMap getHmShowZqdm() {
		return hmShowZqdm;
	}
    //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
    public SZGFBean() {
    }

  /**
   * 实现此方法，将数据插入到指定的表中
   * by leeyu 20090629
   * edit by songjie 
   * 2010.02.24
   * MS00879 
   * QDII4.1赢时胜上海2010年02月10日02_AB
   * @throws YssException
   */
  public void inertData(HashMap hmParam) throws YssException {
      Connection conn = null;
      boolean bTrans = false;
      try {
    	  //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
    	  this.hmParam = hmParam;
    	  //add by songjie 2012.10.15 深圳股份库获取数据接口参数设置读书处理方式的交易类型数据逻辑错误
    	  this.hmReadType = (HashMap)this.hmParam.get("hmReadType");
    	  
    	  //--- add by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 start---//
          //获取数据接口参数设置的交易所债券参数设置界面设置的参数对应的HashMap
          hmExchangeBond = (HashMap) hmParam.get("hmExchangeBond");

          //获取数据接口参数设置的交易费用计算方式界面设置的参数对应的HashMap
          hmTradeFee = (HashMap) hmParam.get("hmTradeFee");
          
          //获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
          hmFeeWay = (HashMap) hmParam.get("hmFeeWay");

          //获取交易费率品种设置界面设置的费率对应的HashMap
          hmRateSpeciesType = (HashMap) hmParam.get("hmRateSpeciesType");

          //获取券商佣金利率设置界面设置的券商佣金利率对应的HashMap
          hmBrokerRate = (HashMap) hmParam.get("hmBrokerRate");
    	  //--- add by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 end---//
          
          hmSubAssetType = new HashMap();//用于储存已选组合代码对应的资产子类型
          pubMethod.setYssPub(pub);
          hmSubAssetType = pubMethod.judgeAssetType(sPort,sDate);//获取已选组合代码对应的资产子类型
          conn = dbl.loadConnection();
          conn.setAutoCommit(bTrans);
          bTrans = true;
          //1：将临时表TMP_ZQBD的数据添加到SHZQBD表中。先按日期删除旧的数据，再执行插入操作
          insertGF();
          //2：将SHZQBD表的数据添加到A001HZJKMX表中。先按日期与组合删除旧的数据，再执行插入操作
          insertHzJkMx(conn);

          conn.commit();
          conn.setAutoCommit(bTrans);
          bTrans = false;
      } catch (Exception e) {
          throw new YssException(e.getMessage(), e);
      } finally {
          dbl.endTransFinal(conn, bTrans);
      }
  }

  /**
   * 数据插入到临时表的过程
   * @throws YssException
   */
  private void insertGF() throws YssException {
      String sqlStr = "";
      try {
          //1:删除掉深交所股份库中的数据
          sqlStr = "delete from SZGF where FDate=" + dbl.sqlDate(sDate);
          dbl.executeSql(sqlStr);
          //2:将临时表tmp_sjsgf 数据插入到深交所股份库表SZGF中
          sqlStr = "insert into SZGF(FDATE,GFXWDM,GFZQDM,GFGDDM,GFYWLB,GFSFZH,GFWTXH,GFWTGS,GFQRGS,GFZJJE,GFBYBZ) " +
              " select GFFSRQ,GFXWDM,GFZQDM,GFGDDM,GFYWLB,GFSFZH,GFWTXH,GFWTGS,GFQRGS,GFZJJE,GFBYBZ " +
              " from tmp_sjsgf where IsDel='False' and GFFSRQ = " + dbl.sqlDate(sDate);
          dbl.executeSql(sqlStr);
      } catch (Exception ex) {
          throw new YssException(ex.getMessage(), ex);
      } finally {

      }
  }

  /**
   * 数据从临时表插入到接口汇总明细表的过程
   * @param conn Connection
   * @throws YssException
   */
  private void insertHzJkMx(Connection conn) throws YssException {
      String sqlStr = "";
      String stockHolder = ""; //股东代码
      String TradeSeat = ""; //交易席位号
      String sInvestSign = ""; //投资标志
      HashMap hmHolderSeat = null;
      SecTradejudge judgeBean = null;
      ResultSet rs = null;
      PreparedStatement stm = null;
      FeeAttributeBean feeAttribute = null;
      DataBase dataBase = new DataBase();
      boolean canInsert = false;//判断权益数据是否能处理到交易接口明细库
      try {
    	  
    	  	//add by huangqirong 2012-07-27 story #2760 获取 深交所中小企业板“003000－004999”证券代码区间的启用日期
			CtlPubPara ctlPub = new CtlPubPara();
			ctlPub.setYssPub(this.pub);
			this.startDate = ctlPub.getSJSZXQYStartSet("SJSZXQYStartSet", "DataInterface", "CTL_SJSZXQY", "DateTimePicker1");
			// --- end ---
			
          sqlStr = "insert into " + pub.yssGetTableName("Tb_HzJkMx") + "(FDate,FZqdm,FSzsh,FGddm,FJyxwh,FBs,FCjsl,FCjjg,FCjje,FYhs,FJsf,FGhf,FZgf,FYj," +
              " FGzlx,Fhggain,FZqbz,Fywbz,FSqbh,Fqtf,Zqdm,FJYFS,Ffxj,Findate,FTZBZ,FPortCode,FCreator,FCreateTime,FJKDM) " +//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
              " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
          stm = conn.prepareStatement(sqlStr);
          //1:取股东代码与席位号
          hmHolderSeat = getStockHolderAndSeat(sPort);
          String[] arrPort = sPort.split(",");
          for (int i = 0; i < arrPort.length; i++) {
              if (hmHolderSeat.get(arrPort[i]) != null) {
                  TradeSeat = String.valueOf(hmHolderSeat.get(arrPort[i])).split("\t")[0]; //查找组合下的所有席位
                  stockHolder = String.valueOf(hmHolderSeat.get(arrPort[i])).split("\t")[1]; //查找组合下的所有股东代码
              }
              //2:插入新数据
              sqlStr = "select * from SZGF where FDate=" + dbl.sqlDate(sDate) +
                  " and GFXWDM in(" + operSql.sqlCodes(TradeSeat) + ")" +
                  " and GFGDDM in(" + operSql.sqlCodes(stockHolder) + ")" +
                  " and Gfywlb in('20','21','22','30','40','33','L7','L8','W0')"; //根据日期与组合下的席位号来判断获取
              rs = dbl.openResultSet(sqlStr);
              while (rs.next()) {
                  judgeBean = judgeSecurityTypeAndTradeType(rs, arrPort[i], rs.getString("GFXWDM"));
                  if (!judgeBean.bInsert)
                      continue;

                  if(judgeBean.securitySign.equals("QY")){
                      feeAttribute = new FeeAttributeBean();

                      feeAttribute.setBusinessSign(judgeBean.tradeSign);
                      feeAttribute.setSecuritySign(judgeBean.securitySign);
                      feeAttribute.setDate(sDate);
                      //add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
                      if(judgeBean.insideCode.startsWith("600")){
                    	  feeAttribute.setZqdm(judgeBean.insideCode + " CG");
                      }else{
                    	  feeAttribute.setZqdm(judgeBean.insideCode + " CS");
                      }
                      //add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB

                      dataBase.setYssPub(pub);
                      canInsert = dataBase.judgeQYInfo(feeAttribute); //判断权益数据是否能够处理到交易接口明细库

                      if (!canInsert)
                            continue;
                  }

                  stm.setDate(1, YssFun.toSqlDate(sDate));
                  
                  //add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
                  if(judgeBean.insideCode.startsWith("600")){
                	  stm.setString(2, judgeBean.insideCode + " CG"); //采用内部代码加交易所的方式
                  }else{
                	  stm.setString(2, judgeBean.insideCode + " CS"); //采用内部代码加交易所的方式
                  }
                  //add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
                  
                  stm.setString(3, "CS");
                  
                  stm.setString(4, rs.getString("GFGDDM"));
                  stm.setString(5, rs.getString("GFXWDM"));
                  if(judgeBean.sBS == null || judgeBean.sBS.equals("")){
                  	judgeBean.sBS = " ";
                  }
                  stm.setString(6, judgeBean.sBS);
                  stm.setDouble(7, judgeBean.tradeAmount);
                  stm.setDouble(8, YssD.div(judgeBean.tradeMoney, judgeBean.tradeAmount));
                  stm.setDouble(9, judgeBean.tradeMoney);
                  stm.setDouble(10, judgeBean.stamptax);
                  stm.setDouble(11, judgeBean.handleFee);
                  stm.setDouble(12, judgeBean.transferFee);
                  stm.setDouble(13, judgeBean.collectManageFee);
                  stm.setDouble(14, judgeBean.commisionFee);
                  stm.setDouble(15, 0); //国债利息
                  stm.setDouble(16, 0); //回购收益
                  stm.setString(17, judgeBean.securitySign); //证券标志
                  stm.setString(18, judgeBean.tradeSign); //业务标志
                  stm.setString(19, " "); //申请编号
                  stm.setDouble(20, 0); //其他费
                  stm.setString(21, judgeBean.oldCode); //证券代码
                  stm.setString(22, "PT"); //交易方式
                  stm.setDouble(23, judgeBean.riskPayment); //风险金
                  stm.setDate(24, YssFun.toSqlDate(sDate)); //插入日期
                  if (hmReadType != null && hmReadType.get(assetGroupCode + " " + arrPort[i]) != null) {
                	  //---edit by songjie 2012.10.15 深圳股份库获取数据接口参数设置读书处理方式的交易类型数据逻辑错误 start---//
                	  ReadTypeBean rt = (ReadTypeBean)hmReadType.get(assetGroupCode + " " + arrPort[i]);
                	  String assetCode = rt.getAssetClass();
                      //sInvestSign = String.valueOf(hmReadType.get(assetGroupCode + " " + arrPort[i]));
                	  
                      if (assetCode.equalsIgnoreCase("01")) { //交易类
                          sInvestSign = "C";
                      } else if (assetCode.equalsIgnoreCase("02")) { //可供出售类
					  //---edit by songjie 2012.10.15 深圳股份库获取数据接口参数设置读书处理方式的交易类型数据逻辑错误 end---//
                          sInvestSign = "S";
                      } else { //持有到期类
                          sInvestSign = "F";
                      }
                  } else {
                      sInvestSign = " ";
                  }
                  stm.setString(25, sInvestSign); //投资标识
                  stm.setString(26, arrPort[i]); //组合代码
                  stm.setString(27,pub.getUserCode());
                  stm.setString(28,YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss"));
                  stm.setString(29, "SZGF");//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
                  stm.addBatch();
              }
              dbl.closeResultSetFinal(rs);//关闭每次循环打开的游标 sunkey@Modify
              stm.executeBatch();
          } //end 循环组合
      } catch (Exception ex) {
          throw new YssException("执行深交所股份库表数据到接口明细表时出错！", ex);
      } finally {
          dbl.closeStatementFinal(stm);
          dbl.closeResultSetFinal(rs);
      }

  }

  /**
   * 根据业务类型调整证券标志与业务标志
   * @param rs ResultSet   单条股份数据
   * @param portCode String 单个组合代码
   * @param tradeSeat String 席位代码
   * @return SecTradejudge
   * @throws YssException
   */
  private SecTradejudge judgeSecurityTypeAndTradeType(ResultSet rs, String portCode, String tradeSeat) throws YssException {
      SecTradejudge judge = new SecTradejudge();
      String sYwlb = ""; //业务类别
      String sNbdm = ""; //内部代码
      String sWbdm = ""; //外部代码,用于匹配XML中的外部代码
      String sOlddm= "";//用于转换旧代码
      String sConvertCode = "";

      String sZqbz = ""; //证券标志
      String sYwbz = ""; //业务标志
      String sBS = ""; //买卖标志
      double dCjsl = 0; //成交数量
      double dCjje = 0; //成交金额
      boolean bInsert = true; //是否执行插入操作,默认为执行插入

      String oldCodeAddBS = null;
      String[] oldCodeBs = null;

      FeeAttributeBean feeAttribute=new FeeAttributeBean();
      try {
          sOlddm= rs.getString("GFZQDM");
          sYwlb = rs.getString("gfywlb") == null ? "" : rs.getString("gfywlb");
          if (sYwlb.equalsIgnoreCase("20")) {
              sZqbz = "QY";//权益
              if (rs.getInt("gfQrgs") == 0 && rs.getInt("gfZjje") > 0) {
                  if (rs.getString("GFZQDM").startsWith("10") || rs.getString("GFZQDM").startsWith("11") || rs.getString("GFZQDM").startsWith("12")) {
                      sYwbz = "PX_ZQ"; //业务标志 债券派息
                      dCjje = rs.getDouble("GFZjje");
                      dCjsl = 0;
                      sBS="S";
                      sWbdm=rs.getString("GFZQDM").substring(0,2)+"****";
                      bInsert = true;
                  } else if (rs.getString("GFZQDM").startsWith("15") || rs.getString("GFZQDM").startsWith("18")) {
                      sYwbz = "PX_JJ"; //业务标志 基金派息
                      dCjje = rs.getDouble("GFZjje");
                      dCjsl = 0;
                      sBS="S";
                      sWbdm=rs.getString("GFZQDM").substring(0,2)+"****";
                      bInsert = true;
                  } else {
                	  //edit by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
                      if (YssFun.left(rs.getString("GFZQDM"),3).equals("003")) { //若转换后的代码为‘003***’，则转换为‘600***’
                          sConvertCode = "600" + rs.getString("GFZQDM").substring(3);
                      }
                      else{
                          sConvertCode = "00" + rs.getString("GFZQDM").substring(2);
                      }
                      if (pubMethod.checkDividSecRight(sConvertCode, sDate, "CS")) {
                          sYwbz = "PX_GP"; //业务标志 股票派息
                          sBS="S";
                          dCjje = rs.getDouble("GFZjje");
                          dCjsl = 0;
                          bInsert = true;
                      } else if (pubMethod.checkCashRight(sConvertCode, sDate, "CS")) {
                          sYwbz = "XJDJ"; //业务标志 现金对价
                          sBS="S";
                          dCjje = rs.getDouble("GFZjje"); //----- add by wangzuochun 2010.05.31 MS01140  日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致  QDV4国内（测试）2010年04月28日01_B 
                          bInsert = true;
                      } else {
                          bInsert = false;
                          //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                          if(sConvertCode.startsWith("600")){
                        	  if(hmShowZqdm.get(sConvertCode + " CG") == null){
                        		  hmShowZqdm.put(sConvertCode + " CG", 
                        				  sConvertCode + 
                        				  " CG 股票派息或现金对价");
                        	  }
                        	  
                          }else{
                        	  if(hmShowZqdm.get(sConvertCode + " CS") == null){
                        		  hmShowZqdm.put(sConvertCode + " CS", 
                        				  sConvertCode + 
                        				  " CS 股票派息或现金对价"); 
                        	  }
                          }
                          //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                      }
                  }
              }
              if(rs.getInt("gfQrgs") > 0 && rs.getInt("gfZjje") == 0){
            	  //edit by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
                  if (YssFun.left(rs.getString("GFZQDM"),3).equals("003")) { //若转换后的代码为‘003***’，则转换为‘600***’
                      sConvertCode = "600" + rs.getString("GFZQDM").substring(3);
                  }
                  else {
                      sConvertCode = "00" + rs.getString("GFZQDM").substring(2);
                  }

                  if (pubMethod.checkStockCounterperFormance(rs.getString("GFZQDM"), sDate, "CS")) {//如果为股份对价
                	  sBS="B";
                      if (((String)hmSubAssetType.get(portCode)).equals("0102") && pubMethod.getTradeSeatType(rs.getString("GFXWDM")).equalsIgnoreCase("INDEX")) {
                          sYwbz = "GFDJ_ZS"; //业务标志 指数股份对价
                          bInsert = true;
                      } else if (((String)hmSubAssetType.get(portCode)).equals("0103") && pubMethod.getTradeSeatType(rs.getString("GFXWDM")).equalsIgnoreCase("Index")) {
                          bInsert = false;
                      } else {
                          sYwbz = "GFDJ"; //业务标志 股份对价
                          bInsert = true;
                      }
                  }else{// 为送股权益
                      sBS="B";
                      if (((String)hmSubAssetType.get(portCode)).equals("0102") && pubMethod.getTradeSeatType(rs.getString("GFXWDM")).equalsIgnoreCase("INDEX")) {
                          sYwbz = "SG_ZS"; //业务标志 指数股票送股
                          bInsert = true;
                      } else if (((String)hmSubAssetType.get(portCode)).equals("0103") && pubMethod.getTradeSeatType(rs.getString("GFXWDM")).equalsIgnoreCase("INDEX")) {
                          bInsert = false;
                      } else {
                          sYwbz = "SG"; //业务标志 送股
                          bInsert = true;
                      }
                      dCjje = 0;
                      dCjsl = rs.getDouble("GfQrgs"); 
                  }
                  
//                  //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
//                  else{
//                	  bInsert = false;
//                      if(sConvertCode.startsWith("600")){
//                    	  if(hmShowZqdm.get(sConvertCode + " CG") == null){
//                    		  hmShowZqdm.put(sConvertCode + " CG", 
//                       				  sConvertCode + " CG 送股或股份对价"); 
//                    	  }
//                    	  
//                      }else{
//                    	  if(hmShowZqdm.get(sConvertCode + " CS") == null){
//                    		  hmShowZqdm.put(sConvertCode + " CS", 
//                    				  sConvertCode + " CS 送股或股份对价"); 
//                    	  }
//                      }
//                  }
//                  //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
              }
          } else if (sYwlb.equalsIgnoreCase("21")) {
              sZqbz = "QY";
              sYwbz = "QZ"; //业务标志 权证送配(QZ)
              dCjje = 0;
              dCjsl = rs.getDouble("GfQrgs");
              sBS="B";
              bInsert = true;
          } else if (sYwlb.equalsIgnoreCase("22")) {
              sZqbz = "QY";
              sYwbz = "PG"; //业务标志 配股(PG)
              dCjje = rs.getDouble("GFZjje");
              dCjsl = rs.getDouble("GfQrgs");
              sBS="B";
              bInsert = true;
          } else if (sYwlb.equalsIgnoreCase("30")) {
              if (rs.getString("GFZQDM").startsWith("12")) {
                  sZqbz = "ZQ"; //证券标志 债券
                  sBS="S";
                  sWbdm=rs.getString("GFZQDM").substring(0,2)+"****";
                  bInsert = true;
              } else {
                  sZqbz = "GP"; //证券标志 股票
                  sOlddm = getKZZZQCode();
                  if(sOlddm.equals("")){
                      bInsert = false;
                  }
                  else{
                      dCjje = rs.getDouble("GFZjje");
                      dCjsl = rs.getDouble("GfQrgs");
                      sBS = "B";
                      bInsert = true;
                  }
              }
              sYwbz = "KZZGP"; //业务标志 可转债债转股
          } else if (sYwlb.equalsIgnoreCase("40")) {
              sZqbz = "GP"; //证券标志 股票
              sYwbz = "YYSG"; //业务标志 要约收购
              sBS="S";
              feeAttribute.setCjjg(10);//收购价格 <???收购价格，需维护>
              dCjsl = rs.getDouble("GfQrgs");
              if (((String)hmSubAssetType.get(portCode)).equals("0102") && pubMethod.getTradeSeatType(rs.getString("GFXWDM")).equalsIgnoreCase("INDEX")) {
                  sYwbz = "YYSG_ZS"; //业务标志 指数要约收购
                  bInsert = true;
              } else if (((String)hmSubAssetType.get(portCode)).equals("0103") && pubMethod.getTradeSeatType(rs.getString("GFXWDM")).equalsIgnoreCase("INDEX")) {
                  sYwbz = "YYSG_ZB"; //业务标志 指标要约收购
                  bInsert = true;
              }else{
                  bInsert =true;
              }
          } else if (sYwlb.equalsIgnoreCase("33")) {
              sZqbz = "ZQ"; //证券标志 债券
              sYwbz = "KZZHS"; //业务标志 可转债回售
              sBS="S";
              feeAttribute.setCjjg(0);//回售价格 <???回售价格，需维护>
              dCjsl = rs.getDouble("GfQrgs");
              bInsert = true;
          } else if (sYwlb.equalsIgnoreCase("L7")) {
              sZqbz = "JJ"; //基金
              sYwbz = "XJCE"; //ETF申赎现金差额（XJCE）

              oldCodeAddBS = getETFFundCode(rs);
              oldCodeBs = oldCodeAddBS.split("\t");

              if(oldCodeBs.length >= 2){
                  sOlddm = oldCodeBs[0];
                  sBS = oldCodeBs[1];
              }

              if(sOlddm == null || sOlddm.equals("")){
                  bInsert = false;
              }
              else{
                  bInsert = true;
              }
          } else if (sYwlb.equalsIgnoreCase("L8")) {
              sZqbz = "JJ"; //基金
              sYwbz = "ETFTK"; //ETF现金退款（ETFTK）
              sBS="S";
              bInsert = true;
          } else if (sYwlb.equalsIgnoreCase("W0")) {
              sZqbz = "QZ"; //权证
              sBS="B";
              if (rs.getInt("gfqrgs") < 0 && rs.getInt("gfZjje") > 0 &&
                  (YssFun.toInt(rs.getString("GFZQDM").substring(0, 3)) >= 30 && YssFun.toInt(rs.getString("GFZQDM").substring(0, 3)) <= 32)) {
                  sYwbz = "XQ_RZQZ"; //认沽权证行权（XQ_RZQZ）
                  bInsert = true;
              } else if (rs.getInt("gfqrgs") < 0 && rs.getInt("gfZjje") < 0 &&
                         (YssFun.toInt(rs.getString("GFZQDM").substring(0, 3)) >= 38 && YssFun.toInt(rs.getString("GFZQDM").substring(0, 3)) <= 39)) {
                  sYwbz = "XQ_RGQZ"; //认购权证行权（XQ_RGQZ）
                  bInsert = true;
              } else {
                  bInsert = false;
              }
          } else {
              bInsert = false;
          }
          //通过读XML文件做一次业务类型与证券类型的转换
          pubXMLRead.setSZGF(sWbdm,sZqbz+" "+sYwbz);
          if(pubXMLRead.getSecSign()!=null && pubXMLRead.getSecSign().trim().length()>0){
              sZqbz=pubXMLRead.getSecSign();
          }
          if(pubXMLRead.getBusinessSign()!=null && pubXMLRead.getBusinessSign().trim().length()>0){
              sYwbz= pubXMLRead.getBusinessSign();
          }
          if(pubXMLRead.getConvertedSecCode()!=null && pubXMLRead.getConvertedSecCode().trim().length()>0){
              sNbdm =pubXMLRead.getConvertedSecCode();
              if(sNbdm.trim().length()>0){
                  sNbdm =sNbdm.replaceAll("[*]","");//将全部的"*"号去掉
              }
          }
          if (sNbdm.trim().length() > 0) { //先将内部代码转换完成
              sNbdm = sNbdm + rs.getString("GFZQDM").substring(sNbdm.trim().length());
          } else {
              sNbdm = rs.getString("GFZQDM");
          }
          
      	  //modify by huangqirong 2012-07-27 story #2760 获取 深交所中小企业板“003000－004999”证券代码区间的启用日期
		  java.util.Date tempDate = (java.util.Date)rs.getDate("FDate");		
		  if(this.startDate != null && YssFun.dateDiff(tempDate, this.startDate) > 0 ) {
	          //edit by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
	          //若转换后的代码为‘003***’，且证券标志为股票 或 证券标志为 权益 且 业务标志为 股票派息，
	          //指数股票送股，股票送股，指数股份对价，股份对价，配股， 则转换为‘600***’
	          if (sNbdm.startsWith("003") && (sZqbz.equalsIgnoreCase("GP") || 
	        	 (sZqbz.equalsIgnoreCase("QY") && (sYwbz.equalsIgnoreCase("PX_GP") || 
	        	  sYwbz.equalsIgnoreCase("SG_ZS") || sYwbz.equalsIgnoreCase("SG") ||
	        	  sYwbz.equalsIgnoreCase("GFDJ_ZS") || sYwbz.equalsIgnoreCase("GFDJ") ||
	        	  sYwbz.equalsIgnoreCase("PG"))))) { 
	              sNbdm = "600"+sNbdm.substring(3);
	          }
		  }
		  //---end---
          feeAttribute.setSecuritySign(sZqbz);
          feeAttribute.setBusinessSign(sYwbz);
          feeAttribute.setBs(sBS);
          feeAttribute.setZqdm(sNbdm);
          feeAttribute.setOldZqdm(sOlddm);
          feeAttribute.setPortCode(portCode);
          feeAttribute.setGsdm(tradeSeat);
          feeAttribute.setDate(sDate);
          feeAttribute.setCjje(dCjje);
          feeAttribute.setCjsl(dCjsl);
          calculateFee(feeAttribute);

          judge.insideCode = feeAttribute.getZqdm();
          judge.bInsert = bInsert;
          judge.oldCode = feeAttribute.getOldZqdm();
          judge.securitySign = feeAttribute.getSecuritySign();
          judge.tradeSign = feeAttribute.getBusinessSign();
          judge.sBS = feeAttribute.getBs();
          judge.tradeAmount = feeAttribute.getCjsl();
          judge.tradeMoney = feeAttribute.getCjje();
          judge.stamptax = feeAttribute.getFYhs(); //印花税
          judge.handleFee = feeAttribute.getFJsf(); //经手费
          judge.commisionFee = feeAttribute.getFYj(); //佣金
          judge.collectManageFee = feeAttribute.getFZgf(); //征管费
          judge.transferFee = feeAttribute.getFGhf(); //过户费
          judge.riskPayment = feeAttribute.getFfxj(); //风险金
      } catch (Exception ex) {
          throw new YssException(ex.getMessage(), ex);
      }
      return judge;
  }

  /**
   * add by songjie
   * 2009.09.15
   * 国内：MS00008
   * QDV4.1赢时胜（上海）2009年4月20日08_A
   * 用于获取深圳股份库中可转债债券的证券代码
   * @return String
   * @throws YssException
   */
  private String getKZZZQCode() throws YssException{
      String strSql = "";//用于储存sql语句
      ResultSet rs = null;//用于声明结果集
      String zqdm = "";//证券代码
      try{
          strSql = " select * from SZGF where FDate = " + dbl.sqlDate(this.sDate) +
              " and GFYWLB = '30' and substr(GFZQDM,1,2) = '12' ";
          rs = dbl.openResultSet(strSql);
          while(rs.next()){
              zqdm = rs.getString("GFZQDM");
              break;
          }
          return zqdm;
      }
      catch(Exception e){
          throw new YssException("获取深圳股份库的可转债债券数据出错！", e);
      }
      finally{
          dbl.closeResultSetFinal(rs);
      }
  }

  /**
   * 获取ETF基金申赎的股票代码
   * @param rs ResultSet
   * @return String
   * @throws YssException
   */
  private String getETFFundCode(ResultSet rs) throws YssException {
      String sqlStr = "";//用于储存sql语句
      String FundCodes = "";//ETF申购或赎回的原始证券代码
      ResultSet tmpRs = null;//声明结果集
      BaseOperDeal operDeal=null;
      String bs = "";//买卖标志
      try {
    	  //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
    	  HashMap hmReadType = (HashMap)hmParam.get("hmReadType");
    	  ReadTypeBean readType = (ReadTypeBean)hmReadType.get(pub.getAssetGroupCode() + " " + this.sPort);
    	  //add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
    	  operDeal =new BaseOperDeal();
          operDeal.setYssPub(pub);

          sqlStr = "select ZQDM,FBS from " + pub.yssGetTableName("Tb_HzJkMx") +
              " where FSQBH = " + dbl.sqlString(rs.getString("GFWtxh")) +
              " and FGDDM = " + dbl.sqlString(rs.getString("GFGDDM")) +
              //adit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
              " and FDATE = " + dbl.sqlDate(operDeal.getWorkDay(readType.getHolidaysCode(),rs.getDate("FDate"),-1)) +
              " and ZQDM like '1599%' and FYWBZ in('SG_ETF','SH_ETF') and FZQBZ = 'JJ'";
          tmpRs = dbl.openResultSet(sqlStr);
          if (tmpRs.next()) {
              FundCodes = tmpRs.getString("ZQDM");
              bs = tmpRs.getString("FBS");
          }
          return FundCodes + "\t" + bs;
      } catch (Exception ex) {
          throw new YssException("获取ETF基金申赎的股票代码出现异常！",ex);
      } finally {
          dbl.closeResultSetFinal(tmpRs);
      }
  }

  /**
   * 获转债转股的债券代码
   * @param rs ResultSet
   * @return String
   * @throws YssException
   */
  private String getKZZCode(ResultSet rs) throws YssException {
      ResultSet tmpRs = null;
      String sqlStr = "";
      String KzzCodes = "";
      try {
          sqlStr = "select GFZQDM from szgf where FDate=" + dbl.sqlDate(rs.getDate("FDate")) +
              " and GFxwdm=" + dbl.sqlString(rs.getString("GFxwdm")) +
              " and GFWtxh=" + dbl.sqlString(rs.getString("GFWtxh")) +
              " and GFGDDM=" + dbl.sqlString(rs.getString("GFGDDM")) +
              " and GFYWlb='30' and GFZQDM like '12%'";
          tmpRs = dbl.openResultSet(sqlStr);
          if (rs.next()) {
              KzzCodes = rs.getString("GFZQDM");
          }
      } catch (Exception ex) {
          throw new YssException("获转债转股的债券代码出现异常！",ex);
      } finally {
          dbl.closeResultSetFinal(tmpRs);
      }
      return KzzCodes;
  }

  /**
   *
   * @param feeAttribute FeeAttributeBean
   * @throws YssException
   */
  public void calculateFee(FeeAttributeBean feeAttribute) throws YssException {
      FeeWayBean feeWayBean=null;
      BrokerRateBean brokerRateBean =null;
      try{
    	  //--- add by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 start---//
    	  pubMethod.setYssPub(pub);
    	  
          if (hmFeeWay == null) {
              //获取储存数据接口参数设置的费用承担方向分页设置的HashMap
              hmFeeWay = feeAttribute.getHmFeeWay();
          }
          if (hmRateSpeciesType == null) {
              //获取储存交易费率品种设置界面设置的参数的HashMap
              hmRateSpeciesType = feeAttribute.getHmRateSpeciesType();
          }
          if (hmBrokerRate == null) {
              //获取储存券商佣金利率设置界面设置的参数的HashMap
              hmBrokerRate = feeAttribute.getHmBrokerRate();
          }
          //--- add by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 end---//
          
          if(feeAttribute.getBusinessSign().indexOf("YYSG")>-1
             && feeAttribute.getSecuritySign().equalsIgnoreCase("GP")){
              double dFees=0;
              feeAttribute.setCjje(YssD.mul(feeAttribute.getCjjg(), Math.abs(feeAttribute.getCjsl()))); //收购价格*Abs(确认股数)
              feeAttribute.setCjsl(Math.abs(feeAttribute.getCjsl()));
              feeWayBean = (FeeWayBean) hmFeeWay.get(pub.getAssetGroupCode() + " " + feeAttribute.getPortCode() + " " + pubMethod.getBrokerCode(feeAttribute.getGsdm()) + " " + feeAttribute.getGsdm()); //获取费用承担方向表信息
              brokerRateBean = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + feeAttribute.getPortCode() + " " + 
              pubMethod.getBrokerCode(feeAttribute.getGsdm()) + " 1 " + feeAttribute.getGsdm() + " EQ"); //获取券商佣金利率设置
              
              //--- add by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 start---//
              if (feeWayBean == null) {
                  throw new YssException("请在交易接口参数设置界面设置已选组合的费用承担参数！");
              }
              if (brokerRateBean == null) {
                  throw new YssException("请在券商佣金利率设置中设置深圳股票的佣金费率数据！");
              }
              //--- add by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 end---//
              
              //edit by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 修改获取费率的关键字 取卖印花税
              feeAttribute.setFYhs(YssFun.roundIt(YssD.mul(feeAttribute.getCjje(), ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP S YHF")).getExchangeRate(),0.01), 2)); //金额*印花税率,保留两位
              //add by songjie 2013.03.25 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001
              feeAttribute.setFGhf(YssD.mul(feeAttribute.getCjje(), ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP GHF")).getExchangeRate(),0.01));
              //--- edit by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 修改获取费率的关键字 start---//
              feeAttribute.setFJsf( YssD.mul(feeAttribute.getCjje(), ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP JSF")).getExchangeRate(),0.01)); //金额*经手费率
              feeAttribute.setFZgf(YssD.mul(feeAttribute.getCjje(), ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF")).getExchangeRate(),0.01)); //金额*征管费率
              //--- edit by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 修改获取费率的关键字 end---//
              feeAttribute.setFYj(YssD.mul(feeAttribute.getCjje(), brokerRateBean.getYjRate(),0.01)); //佣金计算方式：佣金率*金额(初始金额)
              if (feeWayBean.getBrokerBear().indexOf("01") > -1) { //如果是券商承担股票经手费
                  dFees =YssD.add(dFees, YssFun.roundIt(feeAttribute.getFJsf(), brokerRateBean.getYjCoursePreci()));
              }
              if (feeWayBean.getBrokerBear().indexOf("05") > -1) { //如果是券商承担股票征管费
                  dFees =YssD.add(dFees, YssFun.roundIt(feeAttribute.getFZgf(), brokerRateBean.getYjCoursePreci()));
              }
              //--- add by songjie 2013.03.25 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 start---//
              if (feeWayBean.getBrokerBear().indexOf("15") > -1) { //如果是券商承担股票过户费
                  dFees =YssD.add(dFees, YssFun.roundIt(feeAttribute.getFGhf(), brokerRateBean.getYjCoursePreci()));
              }
              //--- add by songjie 2013.03.25 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 end---//
              feeAttribute.setFYj(YssD.sub(YssFun.roundIt(feeAttribute.getFYj(), brokerRateBean.getYjPreci()), dFees)); //最终佣金=初始佣金金额-(经手费+征管费)
              if (feeAttribute.getFYj() < brokerRateBean.getStartMoney()) {//如果佣金的金额比征收的起始金额小，则将起始金额赋给它
                  feeAttribute.setFYj(brokerRateBean.getStartMoney());
              }
              feeAttribute.setFYj(YssFun.roundIt(feeAttribute.getFYj(), 2)); //最终保留两位
              feeAttribute.setFJsf(YssFun.roundIt(YssD.add(feeAttribute.getFJsf(), feeAttribute.getFZgf()), 2)); //经手费=(经手费率+征管费率)*金额
              feeAttribute.setFZgf(0);
          }else if(feeAttribute.getBusinessSign().equalsIgnoreCase("KZZHS")
             && feeAttribute.getSecuritySign().equalsIgnoreCase("ZQ")){
              feeAttribute.setCjje(YssD.mul(feeAttribute.getCjjg(), Math.abs(feeAttribute.getCjsl()))); //回售价格(需界面维护可转债回售价格)*确认数量
              feeAttribute.setCjsl(Math.abs(feeAttribute.getCjsl())); //abs(确认数量)
              brokerRateBean = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + feeAttribute.getPortCode() + " " + 
              pubMethod.getBrokerCode(feeAttribute.getGsdm()) + " 1 " + feeAttribute.getGsdm() + " " + feeAttribute.getSecuritySign()); //获取券商佣金利率设置
              
              //--- add by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 start---//
              if (brokerRateBean == null) {
                  throw new YssException("请在券商佣金利率设置中设置深圳债券的佣金费率数据！");
              }
              //--- add by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 end---//
              
              feeAttribute.setFJsf(YssFun.roundIt(YssD.mul(feeAttribute.getCjje(),
              //--- edit by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 修改获取费率的关键字 start---//
              YssD.add( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF")).getExchangeRate(),
              ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF")).getExchangeRate())), 2)); //金额*(债券经手费+债券征管费)
              //--- edit by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 修改获取费率的关键字 end---//
              feeAttribute.setFYj( YssFun.roundIt(YssD.sub(YssFun.roundIt(YssD.mul(feeAttribute.getCjje(), brokerRateBean.getYjRate()), brokerRateBean.getYjPreci()),
              YssFun.roundIt(YssD.mul(feeAttribute.getCjje(),
              //--- edit by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 修改获取费率的关键字 start---//
              YssD.add( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF")).getExchangeRate(),
              ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF")).getExchangeRate())),
              //--- edit by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001 修改获取费率的关键字 end---//
              brokerRateBean.getYjCoursePreci())), 2)); //roundit(roundit(成交金额×佣金利率,深圳佣金保留位数)- roundit(成交金额×(深圳债券经手费率+深圳债券征管费率),计算深圳佣金过程中费用小数点保留位数), 2)
          }else if(feeAttribute.getBusinessSign().equalsIgnoreCase("KZZGP")
             && feeAttribute.getSecuritySign().equalsIgnoreCase("GP")){
              feeAttribute.setCjje(Math.abs(feeAttribute.getCjje()));
              feeAttribute.setCjsl(Math.abs(feeAttribute.getCjsl()));
          }else if(feeAttribute.getBusinessSign().equalsIgnoreCase("PG")
             && feeAttribute.getSecuritySign().equalsIgnoreCase("QY")){
              feeAttribute.setCjje(Math.abs(feeAttribute.getCjje()));
              feeAttribute.setCjsl(Math.abs(feeAttribute.getCjsl()));
          }

      }catch(Exception ex){
    	  //edit by songjie 2013.03.26 STORY #3750 需求上海-[开发部]QDIIV4[紧急]20130325001
    	  throw new YssException("计算深圳股份库数据出错！",ex);
      }
  }
  /**
  *辅助类
  */
  private class SecTradejudge {
      private SecTradejudge() {
      }

      boolean bInsert = false; //是否插入到明细表，true 插入，false 跳过
      String securitySign = ""; //证券标志
      String tradeSign = ""; //业务标志
      String insideCode = ""; //证券内部代码
      String oldCode="";     //证券旧代码
      String sBS = ""; //买卖类型 B/S
      double tradeAmount = 0; //成交数量
      double tradeMoney = 0; //成交金额

      double stamptax = 0; //印花税
      double handleFee = 0; //经手费
      double commisionFee = 0; //佣金
      double collectManageFee = 0; //征管费
      double transferFee = 0; //过户费
      double riskPayment = 0; //风险金
  }

}
