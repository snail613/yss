/**
 * 
 */
package com.yss.main.operdeal.rightequity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.yss.main.operdata.TradeSecurityLendBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.CashAccLinkBean;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.storagemanage.SecRecPayBalBean;
import com.yss.main.storagemanage.SecurityStorageBean;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @包名：com.yss.main.operdeal.rightequity
 * @文件名：RESecLendDivdend.java
 * @创建人：zhangfa
 * @创建时间：2010-12-8
 * @版本号：0.1
 * @说明：计算证券借贷股票分红金额
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-12-8 | zhangfa | 0.1 |  
 */
public class RESecLendDivdend extends BaseRightEquity{
	private String sSecurityCode="";//保存证券代码
	private String sBrokerCode="";//保存的券商代码
    private String msg="";

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
    
	 /**
     * 做分红权益业务处理，产生业务资料数据保存到证券借贷交易数据javaBean中
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return ArrayList 返回值
     * @throws YssException 异常
     */
	public ArrayList getDayRightEquitys(java.util.Date dDate, String sPortCode)
			throws YssException {
		StringBuffer buff = null;// sql语句的拼接
		ResultSet rs = null;
		double dSecurityAmount = 0; // 证券数量
		double dSecurityCost = 0; // 证券成本
		double dRight = 0; // 权益（主表）
		double dRightSub = 0; // 权益（子表）
		String strRightType = ""; // 权益类型

		String strCashAccCode = " "; // 现金帐户
		String strYearMonth = "";// 保存截取日期的年和天
		CashAccountBean caBean = null;// 声明现金账户的bean
		double dBaseRate = 1;// 基础汇率
		double dPortRate = 1;// 组合汇率
		boolean analy1;// 分析代码1
		boolean analy2;// 分析代码2
		boolean analy3;// 分析代码3
		TradeSecurityLendBean subTrade = null;// 证券借贷交易数据的javaBean
		YssCost cost = null;// 声明成本
		SecRecPayBalBean secRSto = null;//证券应收应付库存的javaBean
		ArrayList reArr = new ArrayList();
		CashAccLinkBean cashAccLink = null;// 声明现金账户链接
		ArrayList linkList = null;
		long sNum = 0;// 为了产生的编号不重复
		String strSecAttrCls = "";// 保存所属分类代码
		Date StorageDate = null;
		try{
			 buff = new StringBuffer();
	            BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
	                getOperDealCtx().getBean("cashacclinkdeal");
	            operFun.setYssPub(pub);
	            strYearMonth = YssFun.left(YssFun.formatDate(dDate), 4) + "00";//赋值
	            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");//判断是否有分析代码
	            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
	            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
	            strRightType =""; //权益类型 借入数据为YSS_SECLEND_JYLX__07BDID 应付借入股利;借出数据为YSS_SECLEND_JYLX__06LDID 应收借出股利
	            strDealInfo = "no";
	            //获取数据
	            buff.append(" select a.*, b.*, c.FTradeCury, c.FEXCHANGECODE, d.FPortCury  from ");
	            buff.append(" (select FSecurityCode as FSecurityCode1,FRecordDate,FDividendDate, FDistributeDate,FPreTaxRatio,FAfterTaxRatio, FRoundCode, FCuryCode as FDividendCuryCode from ");
	            buff.append(pub.yssGetTableName("tb_data_Predividend"));//从分红权益预处理表中获取权益数据
	            buff.append(" where FDividendDate = ").append(dbl.sqlDate(dDate));//权益处理时取除权日数据，做权益确认日处理
	            buff.append(" and FCheckState = 1) a "); 
	            
	            buff.append(" left join (select FSecurityCode,FTradeCury, FEXCHANGECODE from ");
	            buff.append(pub.yssGetTableName("Tb_Para_Security"));//关联证券信息表
	            buff.append(" where FCheckState = 1 ) c on a.FSecurityCode1 = c.FSecurityCode");
	            
	            buff.append(" join (select * from ");
	            buff.append(pub.yssGetTableName("Tb_Stock_SecRecPay"));//关联证券应收应付库存表
	            buff.append(" where FPortCode in (" + operSql.sqlCodes(sPortCode));//组合代码
	            buff.append(")");
	            buff.append(" and FYearMonth<>").append(dbl.sqlString(strYearMonth));//不是期初数库存
	            buff.append(" and FTsfTypeCode=").append(dbl.sqlString(YssOperCons.YSS_SECLEND_DBLX_SecBCost)); //modify by wangzuochun 
	            buff.append("  and FSubTsfTypeCode in (").append(operSql.sqlCodes(YssOperCons.YSS_SECLEND_SUBDBLX_BSC+","+YssOperCons.YSS_SECLEND_SUBDBLX_BLC)).append(" )");
	            
	            buff.append(" and FCheckState=1 )b  on a.fsecuritycode1 = b.fsecuritycode ");
	            buff.append(" and (case when c.fexchangecode in ('CY', 'CS', 'CG') then a.FRecordDate else a.FDividendDate - 1 end) = b.FStorageDate ");//取权益确认日库存
	            
	            buff.append(" left join (select FPortCode,FPortCury from ");
	            buff.append(pub.yssGetTableName("Tb_Para_Portfolio"));//关联组合信息表
	            buff.append(" where FCheckState = 1 ) d on b.FPortCode = d.FPortCode"); //关取证券信息表和组合表，取出交易货币和组合货币。
	            
	            rs = dbl.queryByPreparedStatement(buff.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	            buff.delete(0,buff.length());
	            if (rs.next()) {
	            	rs.beforeFirst();//返回第一个rs
	                //--------------------拼接交易编号---------------------
	            	String strNumDate = YssFun.formatDatetime(dDate).
                    substring(0, 8);

	            	strNumDate = strNumDate + dbFun.getNextInnerCode(
		                      pub.yssGetTableName("tb_DATA_SecLendTRADE"), dbl.sqlRight("FNUM", 6),
		                      "000000", " where FNum like 'T" + strNumDate + "000000".substring(0, 1) + "%'", 1);
	            	strNumDate = "T" + strNumDate;
	            	strNumDate = strNumDate + dbFun.getNextInnerCode(
		                      pub.yssGetTableName("TB_DATA_SecLendTRADE"),
		                      dbl.sqlRight("FNUM", 5), "00000",
		                      " where FNum like '" + strNumDate.replaceAll("'", "''") + "%'");
	            	 String s = strNumDate.substring(9, strNumDate.length());
	            	 sNum = Long.parseLong(s);
	                 //--------------------------------end--------------------------//
	            	 
	            	  while (rs.next()) {
	            		  
	            		  String subTsfTypeCode=rs.getString("FSubTsfTypeCode");
                          if(subTsfTypeCode!=null&&subTsfTypeCode.length()>0){
                        	  if(subTsfTypeCode.equals(YssOperCons.YSS_SECLEND_SUBDBLX_BSC)){//权益类型 借入数据为YSS_SECLEND_JYLX_BInPayDid 借入应付股利;借出数据为YSS_SECLEND_JYLX_LOutRecDid 借出应收股利
                        		  strRightType=YssOperCons.YSS_SECLEND_JYLX_BInPayDid;
                        	  }else if(subTsfTypeCode.equals(YssOperCons.YSS_SECLEND_SUBDBLX_BLC)){
                        		  strRightType=YssOperCons.YSS_SECLEND_JYLX_LOutRecDid;
                        	  }
                          }
	            		//-------------------------设置现金账户链接属性值----------------------
	                      cashacc.setYssPub(pub);
	                      cashacc.setLinkParaAttr( (analy1 ?
	                                                rs.getString("FAnalysisCode1") :
	                                                " "), //投资经理
	                                              rs.getString("FPortCode"), //组合代码
	                                              rs.getString("FSecurityCode1"), //证券代码
	                                              (analy2 ?
	                                               rs.getString("FAnalysisCode2") :
	                                               " "), //券商
	                                              strRightType,
	                                              rs.getDate("FRecordDate"), //权益确认日
	                                              rs.getString("FDividendCuryCode"), //分红币种
	                                              strRightType); //交易类型
	                      //--------------------------------------------------------------------
	                      subTrade=new TradeSecurityLendBean();//实例化
	                      /**
	                      secRSto=new SecRecPayBalBean();//实例化
	                      secRSto.setYssPub(pub);*/
	                      /********************************************************
	                       *  MS01233  QDV4赢时胜(上海)2010年06月03日01_A  add by jiangshichao 2010.06.07
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
	                      dSecurityCost=rs.getDouble("FBal");
	                      dSecurityAmount=rs.getDouble("FAmount");
	                      
	                      CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
	                      pubPara.setYssPub(pub);//设置Pub
	                      String rightsRatioMethods=(String)pubPara.getRightsRatioMethods(rs.getString("FPortCode"));//获取通用参数值
	                      String ratioMethodsDetail = pubPara.getBRightsRatioMethods(rs.getString("FPortCode"),YssOperCons.YSS_JYLX_PX);//按权益类型获取权益比例方式 
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
	                              if (caBean != null) {
	                                  strCashAccCode = caBean.getStrCashAcctCode();
	                              } else { 
	                                  throw new YssException("系统执行分红权益时出现异常！" + "\n" + "【" +
	                                      rs.getString("FSecurityCode") +
	                                      "】证券分红权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
	                              }

	                              if(caBean.getCount()>1 && msg.indexOf(rs.getString("FTradeCury"))==-1){
	                              	//一个币种对应多个账户，提示用户。这里拼接的币种不可以重复
	                              	if(msg.length()>0){
	                              		msg +=","+rs.getString("FTradeCury");
	                              	}else{
	                              		msg =rs.getString("FTradeCury");
	                              	}
	                              		
	                              }
	                              //--------------------拼接交易编号---------------------
	                              sNum++;
	                              String tmp = "";
	                              for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
	                                  tmp += "0";
	                              }
	                              strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
	                              //------------------------end--------------------------//
	                              sSecurityCode+=rs.getString("FSecurityCode")+",";//把获取的证券代码拼接 起来
	                             
	                              subTrade.setNum(strNumDate);
	                              subTrade.setSecurityCode(rs.getString("FSecurityCode"));//证券代码赋值
	                              subTrade.setPortCode(rs.getString("FPortCode"));//组合代码
	                              
	                              strSecAttrCls=rs.getString("FAttrClsCode");
	                              if(strSecAttrCls!=null&&strSecAttrCls.length()>0){
	                            	  subTrade.setAttrClsCode(strSecAttrCls);
	                              }else{
	                            	  subTrade.setAttrClsCode(" ");
	                              }
	                              
	                              if (analy1) {
	                                  subTrade.setInvMgrCode(rs.getString("FAnalysisCode1"));//投资经理
	                              } else {
	                                  subTrade.setInvMgrCode(" ");
	                              }
	                              if (analy2) {
	                                  subTrade.setBrokerCode(rs.getString("FAnalysisCode2"));//券商
	                                  sBrokerCode+=rs.getString("FAnalysisCode2")+",";//把获取的证券代码拼接 起来
	                              } else {
	                                  subTrade.setBrokerCode(" ");
	                              }
	                              
	                              subTrade.setAgreementType("协商式");
	                              
	                             /* String subTsfTypeCode=rs.getString("FSubTsfTypeCode");
	                              if(subTsfTypeCode!=null&&subTsfTypeCode.length()>0){
	                            	  if(subTsfTypeCode.equals(YssOperCons.YSS_SECLEND_SUBDBLX_BSC)){//权益类型 借入数据为YSS_SECLEND_JYLX__07BDID 应付借入股利;借出数据为YSS_SECLEND_JYLX__06LDID 应收借出股利
	                            		  strRightType=YssOperCons.YSS_SECLEND_JYLX__07BDID;
	                            	  }else if(subTsfTypeCode.equals(YssOperCons.YSS_SECLEND_SUBDBLX_BLC)){
	                            		  strRightType=YssOperCons.YSS_SECLEND_JYLX__06LDID;
	                            	  }
	                              }*/
	                              subTrade.setTradeCode(strRightType);

	                              
						          subTrade.setBargainDate(YssFun.formatDate(rs
						        		  .getDate("FDividendDate")));// 成交日期

						          subTrade.setBargainTime("00:00:00");// 成交时间

						          subTrade.setSettleDate(YssFun.formatDate(rs
						        		  .getDate("FDistributeDate")));// 结算日期

						          subTrade.setSettleTime("00:00:00");// 结算时间
						          


			                      subTrade.setTradeAmount(0);//交易数量

			                      subTrade.setTradePrice(dRightSub);//交易价格
			                    //---------------------以下为成本赋值--------------
			                      cost = new YssCost();
			                      cost.setCost(0);//原币核算成本

			                      cost.setMCost(0);//原币管理成本

			                      cost.setVCost(0);//原币估值成本

			                      cost.setBaseCost(0);//基础货币核算成本

			                      cost.setBaseMCost(0);//基础货币管理成本

			                      cost.setBaseVCost(0);//基础货币估值成本

			                      cost.setPortCost(0);//组合货币核算成本

			                      cost.setPortMCost(0);//组合货币管理成本

			                      cost.setPortVCost(0);//组合货币估值成本
			                      subTrade.setCost(cost);//成本
			                      //---------------------end-----------------//
			                      
			                      subTrade.checkStateId = 1;//审核状态

			                      subTrade.creatorCode = pub.getUserCode();//创建人

			                      subTrade.checkTime = YssFun.formatDatetime(new java.util.Date());//审核时间

			                      subTrade.checkUserCode = pub.getUserCode();//审核人

			                      subTrade.creatorTime = YssFun.formatDatetime(new java.util.
			                            Date());//创建时间
			                      
			                      subTrade.setTotalCost(0);//投资总成本
			                      subTrade.setCashAcctCode(strCashAccCode);//设置现金账户
			                      subTrade.setAuto(1);
			                      reArr.add(subTrade);//把数据保存到集合中
	                              
	                      }
	            	  }
	            	  strDealInfo = "true";//表示有权益数据
	            }else {
	                strDealInfo = "no";//表示无权益数据
	            }
		}catch (Exception e) {
            strDealInfo = "false";
            throw new YssException("计算证券借贷分红权益处理出错！",e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return reArr;
	}
	 public void  saveRightEquitys(ArrayList alRightEquitys,java.util.Date dDate,String sPortCode) throws YssException{
		 String strSql="";
		 String strDSql="";
		 String[] sFee = null;
		 PreparedStatement pst = null;
		 Connection conn = dbl.loadConnection();
	        boolean bTrans = false; //代表是否开始了事务
	     TradeSecurityLendBean   tb=null;
	     try{
	    	 conn.setAutoCommit(false);
	    	 bTrans=true;
	    	 
	    	 strDSql=" delete from "+pub.yssGetTableName("Tb_Data_Seclendtrade")+
	    	         " where FBARGAINDATE="+dbl.sqlDate(dDate)+
	    	         " and FPORTCODE="+dbl.sqlString(sPortCode)+
	    	         " and FTRADETYPECODE in("+operSql.sqlCodes(YssOperCons.YSS_SECLEND_JYLX_BInPayDid+","+YssOperCons.YSS_SECLEND_JYLX_LOutRecDid)+" )"+
	    	         " and FDataSource =1";
	    	 dbl.executeSql(strDSql);
	    	 
	    	 strSql = "insert into " + pub.yssGetTableName("TB_DATA_SecLendTRADE") +
              		  " (FNUM ,FSecurityCode,FBARGAINDATE ,FBARGAINTIME,FTRADETYPECODE,FSETTLEDATE,FSETTLETIME,FINVMGRCODE,FPORTCODE," +
                      "FBrokerCode,FATTRCLSCODE,FCASHACCCODE,FagreementType,FTRADEAMOUNT,FTRADEPRICE,FTOTALCOST,FcollateralRatio," +
                      //** FCollateralCode 字段去除 modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
                      " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                      " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," + 
                      " FperiodDate,FlendRatio,FPeriodCode,FFormulaCode," +
                      " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost,FMPortCuryCost, FVPortCuryCost, FDataSource,"+
                      " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                      " values(?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,?,?,?)";
	    	  pst = conn.prepareStatement(strSql);
	    	  for(int i=0;i<alRightEquitys.size();i++){
	    		  tb=(TradeSecurityLendBean) alRightEquitys.get(i);
	    		  
	    		  pst.setString(1, tb.getNum());
	    		  pst.setString(2, tb.getSecurityCode());
	    		  pst.setDate(3, YssFun.toSqlDate(tb.getBargainDate()));
	    		  pst.setString(4, tb.getSettleTime());
	    		  pst.setString(5, tb.getTradeCode());
	    		  
	    		  pst.setDate(6, YssFun.toSqlDate(tb.getSettleDate()));
	    		  pst.setString(7, tb.getSettleTime());
	    		  pst.setString(8, tb.getInvMgrCode());
	    		  pst.setString(9, tb.getPortCode());
	    		  pst.setString(10, tb.getBrokerCode());
	    		  
	    		  pst.setString(11,tb.getAttrClsCode());
	    		  pst.setString(12, tb.getCashAcctCode());
	    		  pst.setString(13, tb.getAgreementType());
	    		  pst.setDouble(14, tb.getTradeAmount());
	    		  pst.setDouble(15, tb.getTradePrice());
	    		  
	    		  pst.setDouble(16, tb.getTotalCost());
	    		 // pst.setString(17, tb.getCollateralCode());//modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务
	    		  pst.setDouble(18, tb.getCollateralRatio().length()==0 ? 0 : YssFun.toDouble(tb.getCollateralRatio()));
	    		  sFee = this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
	                      tb.getFees()).
	                      split(",");
	    		  for (int j = 0; j < sFee.length; j++) {
	    			  pst.setString(19 + j, sFee[j].trim().replaceAll("'", ""));
	                }
	    		  pst.setDate(35, YssFun.toSqlDate(tb.getPeriodDate().length()==0? "1900-00-01":tb.getPeriodDate()));
	    		  
	    		  pst.setDouble(36, tb.getLendRatio().length()==0? 0:YssFun.toDouble(tb.getLendRatio()));
	    		  pst.setString(37, tb.getStrPeriodCode());
	    		  pst.setString(38, tb.getStrFormulaCode());
	    		  
	    		  pst.setDouble(39, tb.getCost().getCost());
	              pst.setDouble(40, tb.getCost().getMCost());
	              pst.setDouble(41, tb.getCost().getVCost());
	              pst.setDouble(42, tb.getCost().getBaseCost());
	              pst.setDouble(43, tb.getCost().getBaseMCost());
	              pst.setDouble(44, tb.getCost().getBaseVCost());
	              pst.setDouble(45, tb.getCost().getPortCost());
	              pst.setDouble(46, tb.getCost().getPortMCost());
	              pst.setDouble(47, tb.getCost().getPortVCost());
	              
	              pst.setDouble(48, tb.getAuto());
	              
	              
	              pst.setInt(49, tb.checkStateId); 
	              pst.setString(50, pub.getUserCode());
	              pst.setString(51, YssFun.formatDatetime(new java.util.Date()));
	              pst.setString(52, pub.getUserCode());
	              
	              pst.executeUpdate();
	    		  
	    	  }
	    	  conn.commit();
	          bTrans = false;
	          conn.setAutoCommit(true);
	     }catch (Exception e) {
	            throw new YssException(e.getMessage(), e);
	        } finally {
	            dbl.closeStatementFinal(pst);
	        }
		 
	 }
}
