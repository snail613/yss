package com.yss.main.operdeal.report.navrep;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.YssPrint;
import com.yss.dsub.YssPub;
import com.yss.main.etfoperation.ETFParamSetAdmin;//xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
import com.yss.main.etfoperation.pojo.ETFParamSetBean;//xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.report.navrep.pojo.*;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.util.*;
import com.yss.main.operdeal.BaseOperDeal;

public class NavTotal
    extends BaseNavRep {
    double Amount = 0.0;
    double TotalValue = 0.0;
    NavRepBean dayNavRep = null; //------ add by wangzuochun 2010.11.25  BUG #483 资产估值后，净值统计表没有产生日净值增长率
    public NavTotal() {
    }

    protected void initReport(java.util.Date dDate, String sPortCode,
                              String sInvMgrCode) throws YssException {
        this.dDate = dDate;
        this.portCode = sPortCode;
        this.invMgrCode = sInvMgrCode;
    }
    /**
     * xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     */
    protected String buildRepView() throws
        YssException {
    	if(this.bETFVal){
    		return pub.yssGetTableName("Tb_ETF_NavData");
    	}else{
    		return pub.yssGetTableName("Tb_Data_NavData");
    	}        
    }

    protected ArrayList getRepData(String tempViewName) throws YssException {
        ArrayList reArr = new ArrayList();
        NavRepBean navRep = null;
        ArrayList plArr = null;
        String strSql = "";       
    	ResultSet rs = null;
    	double supTradeFee = 0 ;  //假设交易费用
    	//int  digit = 0 ;  //保留位数
    	String sPeriodType = "";
        try {
        	//add by yanghaiming 20100510 B股业务
        	NavBSecurity bSecurity = new NavBSecurity();
    		bSecurity.setYssPub(pub);
    		bSecurity.deleteMergeData(portCode, dDate,this.invMgrCode);//为不影响原有净值数据，先删除按币种生成的所有相关数据
    		//===========edit by zhangjun 2011-12-23 STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
    		//资产净值
    		if (getSubAssetType(portCode).equals("0205")){  //0205 -投连保险资产
    			
        		supTradeFee = operInsFee(dDate, portCode); //取假设交易费        		
        		sPeriodType = getPeriodType(dDate,portCode);//取期间类型
        		
    			//加减假设费后的资产净值（保留4位）TotalValue : true       		
    			navRep = getTotalValue1(sPeriodType,supTradeFee,true);
                if (navRep != null) {
                    reArr.add(navRep);
                }    			
    			
    			//没有加减假设费是的资产净值TotalValue_TLX : false
                navRep = getTotalValue1(sPeriodType,supTradeFee,false);
                if (navRep != null) {
                    reArr.add(navRep);
                }
    		}else{
    			navRep = getTotalValue();
                if (navRep != null) {
                    reArr.add(navRep);
                }
    		}
            //========edit end ===================================================================
    		
            //=======add by zhangjun 2011-12-21  STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用
            //投连险假设交易费用
            if (getSubAssetType(portCode).equals("0205")){  //0205 -投连保险资产
        	
        		//假设交易费
        		supTradeFee = operInsFee(dDate, portCode);  
        		//向净值数据表tb_XXX_data_navdata插入假设交易费
        		navRep = getSupTradeFee(supTradeFee);
        		if (navRep != null) {
                    reArr.add(navRep);
                }  
            }
            //===========================add end ================================================
            
            navRep = getTotalAmount();
            if (navRep != null) {
                reArr.add(navRep);
            }
            navRep = getUnit();
            if (navRep != null) {
            	reArr.add(navRep);
            	dayNavRep = navRep; //------ add by wangzuochun 2010.11.25  BUG #483 资产估值后，净值统计表没有产生日净值增长率
            	
            }
            //------2008-12-01 linjunyun 净值报表中增加累计净值项 bug:
            navRep = getAccumulateUnit();
            if (navRep != null) {
                reArr.add(navRep);
            }
            //----------------------------
            navRep = getTotalMV();
            if (navRep != null) {
                reArr.add(navRep);
            }
            //---------------add by yanghaiming 20100303 MS00860 QDV4华夏2009年12月11日01_A 
            //净值统计表增加估值期货浮动盈亏的合计数据
            navRep = getTotalFU();
            if (navRep != null) {
                reArr.add(navRep);
            }
            //--------------------------------------------
            navRep = getTotalFX();
            if (navRep != null) {
                reArr.add(navRep);
            } 
            //xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
            if(this.bETFVal){//如果为ETF资产估值，计算单位现金差额
                navRep = calUnitCashBal();
                if (navRep != null) {
                    reArr.add(navRep);
                }
                navRep = calStockListVal();
                if(navRep != null) {
                	reArr.add(navRep);
                }
            }
            //-------------------------end----------------------------//
            //-------add by yanghaiming 20100505 B股业务，增加各币种组合的净值，估值增值，汇兑损益
            CtlPubPara ctlPubPara = new CtlPubPara();
        	ctlPubPara.setYssPub(pub);
        	if(ctlPubPara.getPerInterface().equalsIgnoreCase("0")){
        		bSecurity.insertMergeData("Security", 4, portCode, dDate,this.invMgrCode);//edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A
            	bSecurity.insertMergeData("Cash", 3, portCode, dDate,this.invMgrCode);
            	
            	//edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A
            	strSql = "select fgradetype3,FInvMgrCode from " + pub.yssGetTableName("Tb_Data_Navdata") +
            			" where fgradetype3 <> ' ' and FNAVDATE = " + dbl.sqlDate(dDate) + " and FInvMgrCode = " +
            			dbl.sqlString(this.invMgrCode.length() > 0 ? this.invMgrCode : "total") + " group by fgradetype3,FInvMgrCode";
            	rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            	while (rs.next()){
            		navRep = getTotalValue(rs.getString("fgradetype3"));
            		if (navRep != null) {
                        reArr.add(navRep);
                    }
            		navRep = getTotalMV(rs.getString("fgradetype3"));
            		if (navRep != null) {
                        reArr.add(navRep);
                    }
            		navRep = getTotalFX(rs.getString("fgradetype3"));
            		if (navRep != null) {
                        reArr.add(navRep);
                    }
            	}
//        		//-------单币种净值---------
//        		navRep = getTotalValue("CNY");
//        		if (navRep != null) {
//                    reArr.add(navRep);
//                }
//        		navRep = getTotalValue("USD");
//        		if (navRep != null) {
//                    reArr.add(navRep);
//                }
//        		navRep = getTotalValue("HKD");
//        		if (navRep != null) {
//                    reArr.add(navRep);
//                }
//        		//-----------单币种估值增值-----------------
//        		navRep = getTotalMV("CNY");
//        		if (navRep != null) {
//                    reArr.add(navRep);
//                }
//        		navRep = getTotalMV("USD");
//        		if (navRep != null) {
//                    reArr.add(navRep);
//                }
//        		navRep = getTotalMV("HKD");
//        		if (navRep != null) {
//                    reArr.add(navRep);
//                }
//        		//---------------单币种汇兑损益--------------
//        		navRep = getTotalFX("USD");
//        		if (navRep != null) {
//                    reArr.add(navRep);
//                }
//        		navRep = getTotalFX("HKD");
//        		if (navRep != null) {
//                    reArr.add(navRep);
//                }

        	}
        	
            //-------------------------------------------------------//
            plArr = getTotalPL();
            if (plArr != null) {
                reArr.addAll(plArr);
            }
            //------现金头寸，放入库中 sj add 20080530 ----
            plArr = getCash();
            if (plArr != null) {//edited by libo MS00615 新成立还未买入证券时，资产估值无法生成可用头寸数据 QDV4赢时胜（上海）2009年8月03日01_B
                reArr.addAll(plArr);
            }
            //------------------------------------------
            reArr.addAll(getTotalSec()); //添加对现金类应收应付汇总合计 QDV4华夏2009年8月24日03_A MS00652 by leeyu 20090831
            reArr.addAll(getUnitScale()); //添加对净值统计增长率的计算 QDV4华夏2009年8月24日03_A MS00652 by leeyu 20090905
			//添加对总净值按资产品种分类汇总的方法 中保管理费计提的需求 QDV4中保2010年06月18日03_A MS01332 中保管理费计提的需求 by leeyu 20100716
	        plArr = statNavDataByCatCode();
	        if(plArr.size()>0){
	        	reArr.addAll(plArr);
	        }
	        //中保管理费计提的需求 by leeyu 20100713
            return reArr;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
        	dbl.closeResultSetFinal(rs);
//            return reArr;
        }
    }
    
    //add by zhangjun 2011-12-22 STORY #1273 获得资产子类型
    private String getSubAssetType(String sPortCode) throws YssException {//0205 -投连保险资产
    	String sqlStr = "";
        ResultSet rs = null;
        String strSubAssetType = "";        
        try{
        	sqlStr = " select * from "  + pub.yssGetTableName("Tb_Para_Portfolio") +
        	         " where FPortCode = " + dbl.sqlString(sPortCode);
        	rs = dbl.openResultSet(sqlStr);
        	if (rs.next()){
        		strSubAssetType = rs.getString("FSubAssetType");       		
        	}
        	if(strSubAssetType.equals("")){//如果没取到资产子类型
				throw new YssException("请先在【业务参数】-【组合设置】界面设置组合【" + sPortCode + "】的资产子类型类型！");
			} 
        	
        }catch (Exception e){
        	throw new YssException(e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
    	return strSubAssetType;
    }    

    //=========end by zhangjun =========================
    
    //add by zhangjun Story #1273 获得期间类型
    private String getPeriodType(java.util.Date dDate, String sPortCode)
           throws YssException {
    	String strSql = "";	    
	    ResultSet rs = null;
	    String sPeriodType = "";
	    
	    /*FLendDate = (select max(FLendDate) from " + 
					pub.yssGetTableName("Tb_Stock_SecOverSell") + " where FLendDate < " + dbl.sqlDate(dDate) + 
					" and FPORTCODE = " + dbl.sqlString(sPortCode) + ") */
	    try{
	    	strSql = " select FPeriodType from " + pub.yssGetTableName("TB_DATA_InsuranceFee")+
	                 " where FFeeDate = " + "( select max(FFeeDate) from " + pub.yssGetTableName("TB_DATA_InsuranceFee") +
	                 " where FFeeDate <= " + dbl.sqlDate(dDate)+ " and FPORTCODE = " + dbl.sqlString(sPortCode) + " and FCheckState = 1 )";
			rs = dbl.openResultSet(strSql);  
			if (rs.next()){
				if(rs.getString("FPeriodType").equalsIgnoreCase("expansion")){
					sPeriodType = "扩张期";
				}else if(rs.getString("FPeriodType").equalsIgnoreCase("contraction")){
					sPeriodType = "收缩期";
				}    			
			}
			if(sPeriodType.equals("")){//如果没取到期间类型
				throw new YssException("请先在【业务功能】-【投连保险费设置】界面设置组合【" + sPortCode + "】的期间类型！");
			}  
	    }catch(Exception e){
	    	throw new YssException("取期间类型数据出错！\n", e);
	    }finally{
	    	dbl.closeResultSetFinal(rs);
	    }
	    return sPeriodType;
    }
    
    /*************************************************************************************************
     * add by zhangjun 2011-12-22
     * 
	 * Story #1273保险假设费业务处理
	 * 
	 */
    public double operInsFee(java.util.Date dDate, String sPortCode) throws
           YssException, SQLException {
    	Connection conn = dbl.loadConnection();
    	boolean bTrans = false; 	    
	    String strSql = "";	    
	    ResultSet rs = null;  
	    ResultSet rSet= null;
	    ResultSet rSet1 = null;
	    ResultSet rSet2 = null;
	    ResultSet rSet3 = null;
	    ResultSet rSet4 = null;
	    ResultSet rSet5 = null;
	    PreparedStatement pst = null;
	    String sPeriodType = "";
	    double supTradeFee = 0; //假设交易费用
	    HashMap hmZQRate = null;
	    DataBase dataBase = null;
	    double mktValue = 0;//债券市值特殊处理
	    double sumFImktValue = 0;
	    boolean sType = false;
	    
    	try{
    		bTrans = true;
    		//得到期间类型    		
    		sPeriodType = getPeriodType(dDate,sPortCode);
    		//判断是否能取到已确认的交易费率数据
    		strSql = " select max(FEndDate) as FEndDate from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") + 
		             " where FEndDate <= " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
		             " and FPeriodType = " + dbl.sqlString(sPeriodType) ;
    		rSet5 = dbl.openResultSet(strSql); 
    		if(rSet5.next()){
    			if(rSet5.getDate("FEndDate") == null)
    				throw new YssException("请先在【报表管理】-【投连交易费率测算表】界面对组合【" + sPortCode + "】期间类型为【" + sPeriodType + "】的平均费率进行确认！");
    		}
    		
			strSql = " delete from " + pub.yssGetTableName("Tb_Data_InsSupFee") +
			         " where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FDate = " + dbl.sqlDate(dDate) + 
			         " and FPeriodType = " + dbl.sqlString(sPeriodType) ;
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_InsSupFee") +
		             " (FDate,FPortCode,FPeriodType,FCatCode,FTradeMarket,FMarketvalue,FAvgRate)" + 				     
		             " values(?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			
			/********************************************************************************************	
			  * 处理上市基金市值  FKeyCode:证券代码
			  * 		  
			  * 上市基金市值数据：证券品种类型为基金、 且品种子类型包含有封闭式基金（TR01）和LOF基金（TR06）和ETF基金（TR04），
			  * 
			  * 且除了交易所为OTC但是包含交易所为NO的这几种证券的市值之和
			  *    
			  */
			strSql = " select  FPortMarketValue,FAvgRate from " +
					 " (select d.FCatCode, FPortMarketValue, FAvgRate from " + 
				     " (select FCatCode,sum(FPortMarketValue) as FPortMarketValue from " +
				     " ( select FKeyCode,FCatCode,FPortMarketValue from " + pub.yssGetTableName("Tb_Data_NavData") + " a " +
					 " join (select FSecurityCode , FCatCode from " + pub.yssGetTableName("Tb_Para_Security") + 
					 " where FCatCode in ('TR') and FSubCatCode in ('TR01','TR06','TR04') and FExchangeCode <> 'OTC' )" + 
					 " b on a.FKeyCode = b.FSecurityCode " + 
			         " where a.FReTypeCode = 'Security' and a.FNAVDate =  " + dbl.sqlDate(dDate) +
			         " and a.FPortCode = " + dbl.sqlString(sPortCode) + " ) c  group by FCatCode ) e " + 
			         " left join ( select FCatCode,FAvgRate from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") +
			         " where FEndDate = (select max(FEndDate) from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") + 
			   		 " where FEndDate <= " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
			   		 " and FPeriodType = " + dbl.sqlString(sPeriodType) +
			         " ) and FPeriodType = " + dbl.sqlString(sPeriodType) + " and FTradeMarket in ('Exchange')) d "+
			         " on e.FCatCode = d.FCatCode ) ";			
			rSet = dbl.openResultSet(strSql);  
							
			pst.setDate(1, YssFun.toSqlDate(this.dDate));
			pst.setString(2,this.portCode);//组合代码
			pst.setString(3,sPeriodType );  //期间类型				
			pst.setString(4, "TR");//证券品种代码		
			pst.setString(5, "Exchange"); //  交易市场
			//pst.setDouble(6, rSet.getDouble("FPortMarketValue"));//市值		
			//pst.setDouble(7, rSet.getDouble("FAvgRate"));//平均费率	
			if (rSet.next()){
				pst.setDouble(6, rSet.getDouble("FPortMarketValue"));//市值	
				pst.setDouble(7, rSet.getDouble("FAvgRate"));//平均费率	
				supTradeFee = supTradeFee +rSet.getDouble("FPortMarketValue")* rSet.getDouble("FAvgRate");
			}else{
				pst.setDouble(6, 0);//市值	
				pst.setDouble(7, 0);//平均费率	
				supTradeFee = supTradeFee + sumFImktValue * 0 ;
			}	
			pst.addBatch();
			//supTradeFee = supTradeFee +rSet.getDouble("FPortMarketValue")* rSet.getDouble("FAvgRate");
			
			
			
			/********************************************************************************************	
			  * 处理开放式基金市值（扣除货币市场基金） 
			  * 		  
			  * 开放式基金市值数据： 证券品种类型为基金、且交易所为OTC、 且除了品种子类型为货币式基金(TR03)的证券的市值之和。
			  *    
			  */
			
			strSql = " select  FPortMarketValue,FAvgRate from " +
					 " ( select d.FCatCode, FPortMarketValue, FAvgRate from " +
		             " ( select FCatCode,sum(FPortMarketValue) as FPortMarketValue from " +
		             " ( select FKeyCode,FCatCode,FPortMarketValue from " + pub.yssGetTableName("Tb_Data_NavData") + " a " +
		             " join (select FSecurityCode , FCatCode from " + pub.yssGetTableName("Tb_Para_Security") + 
		             " where FCatCode in ('TR') and FSubCatCode <> 'TR03' and FExchangeCode in ('OTC') )" + 
		             " b on a.FKeyCode = b.FSecurityCode " + 
		             " where a.FReTypeCode = 'Security' and a.FNAVDate =  " + dbl.sqlDate(dDate) +
		             " and a.FPortCode = " + dbl.sqlString(sPortCode) + " ) c group by FCatCode ) e " + 		            
		             " left join ( select FCatCode,FAvgRate from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") +
		             " where FEndDate = (select max(FEndDate) from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") + 
			   		 " where FEndDate <= " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
			   		 " and FPeriodType = " + dbl.sqlString(sPeriodType) +
		             " ) and FPeriodType = " + dbl.sqlString(sPeriodType) + " and FTradeMarket in ('Open')) d "+
		             " on e.FCatCode = d.FCatCode ) ";
			
			rSet1 = dbl.openResultSet(strSql);  
							
			pst.setDate(1, YssFun.toSqlDate(this.dDate));
			pst.setString(2,this.portCode);//组合代码
			pst.setString(3,sPeriodType );  //期间类型				
			pst.setString(4, "TR");//证券品种代码		
			pst.setString(5, "Open"); //  交易市场
			if (rSet1.next()){
				pst.setDouble(6, rSet1.getDouble("FPortMarketValue"));//市值	
				pst.setDouble(7, rSet1.getDouble("FAvgRate"));//平均费率	
				supTradeFee = supTradeFee +rSet1.getDouble("FPortMarketValue")* rSet1.getDouble("FAvgRate");
			}else{
				pst.setDouble(6, 0);//市值	
				pst.setDouble(7, 0);//平均费率	
				supTradeFee = supTradeFee + sumFImktValue * 0 ;
			}	
			pst.addBatch();
				
			
			
			/********************************************************************************************
			  * 股票市值
			  * 			  
			  * 股票市值数据：证券品种为股票和权证的市值之和。
			  *    
			  */
			strSql = " select  FPortMarketValue,FAvgRate from " +
				     " ( select d.FCatCode, FPortMarketValue, FAvgRate from " +
                     " (select  FCatCode,sum(FPortMarketValue) as FPortMarketValue from " +
                     " ( select FKeyCode,  'EQ' as FCatCode ,FPortMarketValue from " + pub.yssGetTableName("Tb_Data_NavData") + " a " +
                     " join (select FSecurityCode , FCatCode from " + pub.yssGetTableName("Tb_Para_Security") + 
                     " where FCatCode in ('EQ','OP') )" + 
		             " b on a.FKeyCode = b.FSecurityCode " + 
		             " where a.FReTypeCode = 'Security' and a.FNAVDate =  " + dbl.sqlDate(dDate) +
		             " and a.FPortCode = " + dbl.sqlString(sPortCode) + " ) c  group by FCatCode ) e " + 
		             " left join ( select FCatCode,FAvgRate from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") +
		             " where FEndDate = (select max(FEndDate) from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") + 
			   		 " where FEndDate <= " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
			   		 " and FPeriodType = " + dbl.sqlString(sPeriodType) +
		             "  ) and FPeriodType = " + dbl.sqlString(sPeriodType) + " and FTradeMarket in ('Exchange')) d "+
		             " on e.FCatCode = d.FCatCode ) ";
			rSet2 = dbl.openResultSet(strSql);  
						
			pst.setDate(1, YssFun.toSqlDate(this.dDate));
			pst.setString(2,this.portCode);//组合代码
			pst.setString(3,sPeriodType );  //期间类型				
			pst.setString(4, "EQ");//证券品种代码		
			pst.setString(5, "Exchange"); //  交易市场
			if (rSet2.next()){	
				pst.setDouble(6, rSet2.getDouble("FPortMarketValue"));//市值	
				pst.setDouble(7, rSet2.getDouble("FAvgRate"));//平均费率	
				supTradeFee = supTradeFee +rSet2.getDouble("FPortMarketValue")* rSet2.getDouble("FAvgRate");
			}else{
				pst.setDouble(6, 0);//市值	
				pst.setDouble(7, 0);//平均费率	
				supTradeFee = supTradeFee + sumFImktValue * 0 ;
			}		
			pst.addBatch();
				
			
			/********************************************************************************************
			  * 上市债券市值（全价）
			  * 			  
			  * 上市债券市值数据：证券品种类型为债券、且除了交易所为“未上市（NO）”、且除了交易所为“银行间（CY）”的市值之和。
			  * 
			  *  若报价方式为净价(1-净价)，估值按全价来估，则原币市值=持有数量*(行情 + 利息)
			  *    
			  */
			
			//TEST 2011-12-28 FKeyCode:证券代码
			strSql = "select FKeyCode,FPortMarketValue,FQuoteway,FStorageAmount,fclosingprice from "  + pub.yssGetTableName("Tb_Data_NavData")+ " a " +
			         " join ( select FSecurityCode from " + pub.yssGetTableName("Tb_Para_Security") +
			         " where FCatCode in ('FI') and FExchangeCode not in ('NO', 'CY')) b on a.FKeyCode = b.FSecurityCode" + 
			         " left join ( select FSecurityCode as FSecurityCode_c ,FQuoteway from " + pub.yssGetTableName("Tb_Para_FixInterest") + //得到报价方式：0-全价；1-净价
			         " ) c on a.  FKeyCode = c.FSecurityCode_c  " +
			         " left join ( select FSecurityCode as FSecurityCode_d,FStorageAmount  from " 
			         + pub.yssGetTableName("Tb_Stock_Security") +  //得到证券库存数量
			         " where Fstoragedate = " + dbl.sqlDate(dDate)+ ") d on a.FKeyCode = d.FSecurityCode_d " + 
			         " left join ( select aa.fsecuritycode as fsecuritycode_aa , fclosingprice from " + 
					 pub.yssGetTableName("Tb_Data_MarketValue") + " aa  join (select fsecuritycode, max(FMktValueDate) as FMktValueDate  from "  +					 
					 pub.yssGetTableName("Tb_Data_MarketValue") + " where FCheckState = 1 and FMktValueDate <= " + dbl.sqlDate(dDate) + 
					 " group by fsecuritycode ) bb on aa.fmktvaluedate = bb.fmktvaluedate  and aa.fsecuritycode = bb.fsecuritycode " + 					 
					 " ) cc on a.FKeyCode = cc.FSecurityCode_aa " + //取行情收盘价
			         " where a.FReTypeCode = 'Security' and a.FNAVDate =  " + dbl.sqlDate(dDate) + "and a.FPortCode = " + dbl.sqlString(sPortCode);
			rSet3 = dbl.openResultSet(strSql); 
			/*if(rSet3.next()){  //若有记录集则 取费率
				sType = true;
			}*/
			while (rSet3.next()) {
				if(rSet3.getString("FQuoteWay").equalsIgnoreCase("1") ){//净价
					BigDecimal bigInt100 = new BigDecimal(0);
                    BigDecimal bigBefInt100 = new BigDecimal(0);
					dataBase = new DataBase();
                	dataBase.setYssPub(pub);
                	//计算税前 税后 百元债券利息 因为无法判断买卖标志 所以 默认为  用买入计息设置的利息算法公式来计算百元债券利息
                    hmZQRate = dataBase.calculateZQRate(rSet3.getString("FKeyCode"), dDate, "B", sPortCode);
                    //若不能在债券信息设置中找到当前债券的信息 则 提示用户维护当前债券的信息
                    if(((String)hmZQRate.get("haveInfo")).equals("false")){
                    	throw new YssException("请设置 " + rSet3.getString("FKeyCode") + " 的相关债券信息！");
                    }
                    //获取税后百元债券利息
                    bigInt100 = new BigDecimal((String)hmZQRate.get("GZLX"));
                    //获取税前百元债券利息
                    bigBefInt100 = new BigDecimal((String)hmZQRate.get("SQGZLX"));
                    
                    mktValue = rSet3.getDouble("FStorageAmount") * (rSet3.getDouble("fclosingprice") + bigInt100.doubleValue());
                    sumFImktValue = sumFImktValue + mktValue;
				}else if(rSet3.getString("FQuoteWay").equalsIgnoreCase("0") ){//全价
					sumFImktValue = sumFImktValue + rSet3.getDouble("FPortMarketValue");
				}
			}
			//取交易费率
			
			strSql = " select FAvgRate from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") +
		     		 " where FEndDate = (select max(FEndDate) from " + pub.yssGetTableName("Tb_DATA_INSAVGEXRATE") + 
		     		 " where FEndDate <= " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + 
		     		 " and FPeriodType = " + dbl.sqlString(sPeriodType) +
		     		 " ) and FPeriodType = " + dbl.sqlString(sPeriodType) + " and FTradeMarket in ('Exchange') and FCatCode in ('FI') " ;
			rSet4 = dbl.openResultSet(strSql); 
			pst.setDate(1, YssFun.toSqlDate(this.dDate));
			pst.setString(2,this.portCode);//组合代码
			pst.setString(3,sPeriodType );  //期间类型				
			pst.setString(4, "FI");//证券品种代码		
			pst.setString(5, "Exchange"); //  交易市场
			pst.setDouble(6, sumFImktValue);//市值	
			if(rSet4.next()){
				pst.setDouble(7, rSet4.getDouble("FAvgRate"));//平均费率	
				supTradeFee = supTradeFee + sumFImktValue * rSet4.getDouble("FAvgRate");
			}else{
				pst.setDouble(7, 0);//平均费率	
				supTradeFee = supTradeFee + sumFImktValue * 0 ;
			}
			pst.addBatch();	
			
			pst.executeBatch();
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);			
    	}catch(Exception e){
    		throw new YssException("处理保险假设费市值数据出错！\n", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rSet);
			dbl.closeResultSetFinal(rSet1);
			dbl.closeResultSetFinal(rSet2);
			dbl.closeResultSetFinal(rSet3);
			dbl.closeResultSetFinal(rSet4);
			dbl.closeResultSetFinal(rSet5);
			dbl.closeStatementFinal(pst);
	        dbl.endTransFinal(conn, bTrans); 	
    	}
    	return supTradeFee;	 //返回假设交易费用
   }
    
    /*************************************************************************************************
     * add by zhangjun 2011-12-22
	 * Story #1273  保险假设费业务处理
	 * 向净值数据表tb_xxx_data_navdata 插入"假设交易费用"
	 * 
	 */
    private NavRepBean getSupTradeFee(double supTradeFee) 
    		throws YssException {
    	NavRepBean navRep = null;
        String sqlStr = "";
        ResultSet rs = null;
    	//String sPeriodType = "";//期间类型
    	
    	//得到期间类型
    	//sPeriodType = getPeriodType(dDate,sPortCode);      
        
        try {
            
        	//if (supTradeFee == 0) { //当假设交易费数值为0时，跳过。
                //return null;
            //}
            /*sqlStr = "select "+ "1 as FDetail,'' as FDetailOrSum,'假设交易费用：' as FKeyName " +
                ",'SupTradeFee' as FKeyCode ,'Total8' as FOrderCode from " +
                tempViewName + " where FNAVDate = " +
                dbl.sqlDate(this.dDate) + " and FPortCode = " +
                dbl.sqlString(this.portCode) +                
                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                " and FDetail = 0 and FReTypeCode <> 'Total'";
            
            rs = dbl.queryByPreparedStatement(sqlStr); */
            //if (rs.next()) {                
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode("Total8");
                navRep.setKeyCode("SupTradeFee");
                navRep.setKeyName("假设交易费用");
                navRep.setDetail(1); //汇总
                navRep.setReTypeCode("Total");
                navRep.setCuryCode(" ");
                navRep.setPortMarketValue(YssD.round(supTradeFee,2));//新增的字段“假设费用”，保留小数点后两位。
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
                //this.TotalValue = navRep.getPortMarketValue();
            //}
            return navRep;
        } catch (Exception e) {
            throw new YssException("获取假设费用信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }    	
    	
    }
    /*************************************************************************************************
     * add by zhangjun 2011-12-23
	 * Story #1273  保险假设费业务处理
	 * 向净值数据表tb_xxx_data_navdata 插入"资产净值"
	 * 当为扩张期时，原先净值表中的“资产净值”为资产-负债+假设费用；当为收缩期时，资产净值为资产-负债-假设费用。
	 */
  
    private NavRepBean getTotalValue1(String sPeriodType ,double supTradeFee,boolean bool) throws YssException {
    	NavRepBean navRep = null;
        String sqlStr = "";
        ResultSet rs = null; 
        String strMarketValue = "";//double转换为String
        boolean pType;
        
        CtlPubPara pubpara = null;
        pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        String para = pubpara.getSecRecRound();
        String sql1 = " sum(case when FInOut = 1 then FPortMarketValue else FPortMarketValue*-1 end) as FPortMarketValue";
        String sql2 = " sum(case when FInOut = 1 and SUBSTR(fordercode,5,4)<>'TR03'  then round(FPortMarketValue,2)"+
                      " when finout = 1 and SUBSTR(fordercode,5,4)='TR03' then FPortMarketValue"+
                      " when finout = -1 and  SUBSTR(fordercode,5,4)<>'TR03' then round(FPortMarketValue * -1,2)"+
                      " when finout = -1 and SUBSTR(fordercode,5,4)='TR03' then FPortMarketValue * -1 end) as FPortMarketValue";
        
        try{
        	 sqlStr = "select "+(para.equalsIgnoreCase("0")?sql1:sql2)+", 1 as FDetail,'' as FDetailOrSum," ;
        	 if (bool){ 
        		 sqlStr = sqlStr + "'资产净值：' as FKeyName ,'TotalValue' as  FKeyCode ,'Total1' as FOrderCode from " ;
        	 }else{ 
        		 sqlStr = sqlStr + "'净资产：' as FKeyName ,'TotalValue_TLX' as  FKeyCode ,'Total9' as FOrderCode from " ;
        	 }             
             sqlStr = sqlStr + tempViewName + " where FNAVDate = " +
             dbl.sqlDate(this.dDate) + " and FPortCode = " +
             dbl.sqlString(this.portCode) +
             
             " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
             " and FDetail = 0 and FReTypeCode <> 'Total'";
        	 rs = dbl.queryByPreparedStatement(sqlStr); 
             if (rs.next()) {
                 if (rs.getDouble("FPortMarketValue") == 0) { //当数值为0时，跳过。
                     return null;
                 }
                 navRep = new NavRepBean();
                 navRep.setNavDate(this.dDate); //净值日期
                 navRep.setPortCode(this.portCode);
                 navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                 navRep.setKeyCode(rs.getString("FKeyCode"));
                 navRep.setKeyName(rs.getString("FKeyName"));
                 navRep.setDetail(rs.getDouble("FDetail")); //汇总
                 navRep.setReTypeCode("Total");
                 navRep.setCuryCode(" ");
                 
            	 if (bool){ //TotalValue:加减假设费后的资产净值
            		 if(sPeriodType.equals("扩张期")){
                		 pType = true;
                    	 navRep.setPortMarketValue(mSplit(rs.getDouble("FPortMarketValue") + supTradeFee,pType));
                     }else if(sPeriodType.equals("收缩期")){
                    	 pType = false;                	 
                    	 navRep.setPortMarketValue(mSplit(rs.getDouble("FPortMarketValue") - supTradeFee,pType));
                     }   
            	 }else{// TotalValue_TLX:没加减假设费的资产净值
            		 navRep.setPortMarketValue(rs.getDouble("FPortMarketValue"));
            	 }  
                 if (!this.invMgrCode.equalsIgnoreCase("total")) {
                     navRep.setInvMgrCode(this.invMgrCode);
                 } else {
                     navRep.setInvMgrCode("total");
                 }
                 if(bool){ //加减假设费后的资产净值算单位净值
                	 this.TotalValue = navRep.getPortMarketValue();
                 }
                 
             }
             return navRep;
        }catch(Exception e){
        	throw new YssException("获取投连险净值信息出错！");
        }finally{
        	dbl.closeResultSetFinal(rs);
        }    	
    	
    }
    
    /*************************************************************************************************
     * add by zhangjun 2011-12-23
	 * Story #1273  保险假设费业务处理
	 * mSplit():功能
	 * 扩张期: 舍进：当小数点后第5位不管是0或9，都要往前进一位然后保留4位
	 * 收缩期: 舍去：当小数点后第5位不管是0或9，不需要往前进一位直接去掉保留4位；
	 */
    private double mSplit(double mValue,boolean type) throws YssException {
    	//String strNum = "";
    	double marketValue;
    	BaseOperDeal operDeal = null;
    	operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
    	try {
    		if (type){//扩张期
        		marketValue = YssD.round(mValue + 0.00005 , 4);    		
        	}else{//收缩期
        		marketValue = operDeal.cutDigit(mValue , 4);
        		/*strNum = "" + mValue;//市值        	
            	String[] marketValueArray=strNum.split("\\.");
            	if (marketValueArray.length >= 2 && marketValueArray[1].length() >= 4){
            		strNum = ""+ marketValueArray[0]+ "." + marketValueArray[1].substring(0, 3);
            		marketValue = Double.parseDouble(strNum);    	
            	}else{
            		marketValue = mValue;
            	}*/
        	}
    	}catch(Exception e){
    		throw new YssException(e); 
    	}    		
    	return marketValue;
    }
    
    private NavRepBean getTotalValue() throws YssException {
        NavRepBean navRep = null;
        String sqlStr = "";
        ResultSet rs = null;
        //---MS01021  QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差  add by jiangshichao 2010.03.26-----
        CtlPubPara pubpara = null;
        pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        String para = pubpara.getSecRecRound();
        String sql1 = " sum(case when FInOut = 1 then FPortMarketValue else FPortMarketValue*-1 end) as FPortMarketValue";
        String sql2 = " sum(case when FInOut = 1 and SUBSTR(fordercode,5,4)<>'TR03'  then round(FPortMarketValue,2)"+
                      " when finout = 1 and SUBSTR(fordercode,5,4)='TR03' then FPortMarketValue"+
                      " when finout = -1 and  SUBSTR(fordercode,5,4)<>'TR03' then round(FPortMarketValue * -1,2)"+
                      " when finout = -1 and SUBSTR(fordercode,5,4)='TR03' then FPortMarketValue * -1 end) as FPortMarketValue";
        //---MS01021  QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差  end -------------------------------
        try {
            //------------------------------取所有的明细记录取和 sj --------------------------------------------//
            sqlStr = "select "+(para.equalsIgnoreCase("0")?sql1:sql2)+", 1 as FDetail,'' as FDetailOrSum,'资产净值：' as FKeyName " +
                ",'TotalValue' as FKeyCode ,'Total1' as FOrderCode from " +
                tempViewName + " where FNAVDate = " +
                dbl.sqlDate(this.dDate) + " and FPortCode = " +
                dbl.sqlString(this.portCode) +
                //------------------------------------------------------------------------------------------
                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                " and FDetail = 0 and FReTypeCode <> 'Total'" +
                " and nvl(fgradetype1,' ') <> 'FU'"; //modify by fangjiang 2011.12.17 STORY #1886 期货的本位币市值不要统计进资产净值
            //------------------------------------------------------------------------------------------------
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                if (rs.getDouble("FPortMarketValue") == 0) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
                    return null;
                }
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total");
                navRep.setCuryCode(" ");
                navRep.setPortMarketValue(rs.getDouble("FPortMarketValue"));
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
                this.TotalValue = navRep.getPortMarketValue();
            }
            return navRep;
        } catch (Exception e) {
            throw new YssException("获取净值信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	/**
    * 根据品种分类计算净值
    * QDV4中保2010年06月18日03_A MS01332 中保管理费计提的需求 by leeyu 20100716
    * add by leeyu 20100709
    * @return
    * @throws YssException
    */
    private ArrayList statNavDataByCatCode() throws YssException{
	   ArrayList alNavBean =new ArrayList();
	   NavRepBean navRep = null;
	   ResultSet rs =null;
	   String strSql ="";
	   int iRow=1;
	   try{
		   strSql="select sum(case when FInOut = 1 then FPortMarketValue else FPortMarketValue * -1 end) as FPortMarketValue,"+
		   (this.invMgrCode.equalsIgnoreCase("total")?"":" FInvMgrCode,")+
           " FCatCode,1 as FDetail,'' as FDetailOrSum,'资产净值_'"+dbl.sqlJN()+"e.FCatName"+dbl.sqlJN()+"':' as FKeyName,'TotalValue_'"+dbl.sqlJN()+"FCatCode as FKeyCode,"+
           " 'Total1' as FOrderCode from (select a.*,"+
           " case when a.FReTypeCode='Cash' then c.FAnalySisCode2 "+//现金的品种类型字段现用分析代码2
           " when a.FRetypeCode='Security' then b.FAttrClsCode "+//证券的品种类型字段现用所属分类
           " when a.FRetypeCode='Invest' then d.FAnalySisCode3 "+//运营应收应付的品种类型字段现用分析代码3
           " else ' ' end as FCatCode from (select FInvMgrCode,FReTypeCode,"+
           //" case when FReTypeCode='Cash' then FGradeType4 "+//现金类取第四分级，为现金帐户
           " case when FReTypeCode='Cash' then FGradeType5 "+//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
           " when FRetypeCode='Security' then FGradeType5 "+//证券类取第五分级，为证券代码
           " when FRetypeCode='Invest' then FGradeType2 "+//运营类取第二分级，为运营品种代码
           " else FKeyCode end as FCodes,"+
           " FPortMarketValue,FInOut from "+pub.yssGetTableName("Tb_Data_Navdata")+
           " where FNavDate = "+dbl.sqlDate(dDate)+
           " and FPortCode = "+dbl.sqlString(portCode)+
           " and FDetail = 0 and FReTypeCode <> 'Total' " +
           //--- MS01702 QDV4太平2010年09月07日01_AB  add by jiangshichao 2010.09.26 --------------------------
           " and FInvMgrCode= "+dbl.sqlString(this.invMgrCode)+
           " ) a "+ 
           //----MS01702 QDV4太平2010年09月07日01_AB end ------------------------------------------------------
           " left join (select FSecurityCode,FAttrClsCode,"+
           " case when FAnalySisCode1=' ' then 'total' else FAnalySisCode1 end as FInvMgrCode,"+
           " 'Security' as FRetypeCode from "+pub.yssGetTableName("Tb_Stock_Security")+
           "  where FPortCode="+dbl.sqlString(portCode)+
           " and FStorageDate = "+dbl.sqlDate(dDate)+
           " and FYearMonth<> "+dbl.sqlString(YssFun.formatDate(dDate,"yyyy")+"00")+
           //MS01702 QDV4太平2010年09月07日01_AB  add by jiangshichao 2010.09.26 
           (this.invMgrCode.equalsIgnoreCase("total")?"":" and FAnalySisCode1= " +dbl.sqlString(this.invMgrCode))+ //注意：证券库存的分析代码1必须为投资经理，这里被写死
          //----MS01702 QDV4太平2010年09月07日01_AB end ------------------------------------------------------
           " ) b on a.FCodes=b.FSecurityCode and a.FRetypeCode= b.FRetypeCode "+
           (this.invMgrCode.equalsIgnoreCase("total")?"":" and a.FInvMgrCode=b.FInvMgrCode ")+
           " left join (select FCashAccCode,FAnalySisCode2,"+
           " case when FAnalySisCode1=' ' then 'total' else FAnalySisCode1 end as FInvMgrCode,"+
           " 'Cash' as FRetypeCode from "+pub.yssGetTableName("Tb_Stock_Cash")+
           " where FPortCode="+dbl.sqlString(portCode)+
           " and FStorageDate = "+dbl.sqlDate(dDate)+
           " and FYearMonth<> "+dbl.sqlString(YssFun.formatDate(dDate,"yyyy")+"00")+
          //MS01702 QDV4太平2010年09月07日01_AB  add by jiangshichao 2010.09.26 
           (this.invMgrCode.equalsIgnoreCase("total")?"":" and FAnalySisCode1= " +dbl.sqlString(this.invMgrCode))+ //注意：现金库存的分析代码1必须为投资经理，这里被写死
          //----MS01702 QDV4太平2010年09月07日01_AB end ------------------------------------------------------
           " ) c on a.FCodes=c.FCashAccCode and a.FRetypeCode= c.FRetypeCode "+
           (this.invMgrCode.equalsIgnoreCase("total")?"":" and a.FInvMgrCode=c.FInvMgrCode ")+
           " left join (select FIvPayCatCode,FAnalySisCode3,"+
           " case when FAnalySisCode1=' ' then 'total' else FAnalySisCode1 end as FInvMgrCode,"+
           " 'Invest' as FReTypeCode from "+pub.yssGetTableName("Tb_Stock_Invest")+
           " where FPortCode="+dbl.sqlString(portCode)+
           " and FStorageDate = "+dbl.sqlDate(dDate)+
           " and FYearMonth<> "+dbl.sqlString(YssFun.formatDate(dDate,"yyyy")+"00")+
          //MS01702 QDV4太平2010年09月07日01_AB  add by jiangshichao 2010.09.26 
           (this.invMgrCode.equalsIgnoreCase("total")?"":" and FAnalySisCode1= " +dbl.sqlString(this.invMgrCode))+ //注意：投资运营库存的分析代码1必须为投资经理，这里被写死
          //----MS01702 QDV4太平2010年09月07日01_AB end ------------------------------------------------------
           " ) d on a.FCodes=d.FIvPayCatCode and a.FRetypeCode= d.FRetypeCode "+
           (this.invMgrCode.equalsIgnoreCase("total")?"":" and a.FInvMgrCode=d.FInvMgrCode ")+
           " )y left join (select FCatCode as FBCatCode,FCatName from tb_base_category) e on y.FCatCode=e.FBCatCode "+
           " group by FCatCode,e.FCatName"+
           (this.invMgrCode.equalsIgnoreCase("total")?"":",FInvMgrCode");
		   rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		   while(rs.next()){
			   if (rs.getDouble("FPortMarketValue") == 0) {
	               continue;
	            }
	            navRep = new NavRepBean();
	            navRep.setNavDate(this.dDate); //净值日期
	            navRep.setPortCode(this.portCode);
	            navRep.setOrderKeyCode(rs.getString("FOrderCode")+(iRow++));
	            navRep.setKeyCode(rs.getString("FKeyCode"));
	            navRep.setKeyName(rs.getString("FKeyName"));
	            navRep.setDetail(rs.getDouble("FDetail")); //汇总
	            navRep.setReTypeCode("Total");
	            navRep.setCuryCode(" ");
	            navRep.setPortMarketValue(rs.getDouble("FPortMarketValue"));
	            if (!this.invMgrCode.equalsIgnoreCase("total")) {
	               navRep.setInvMgrCode(rs.getString("FInvMgrCode"));
	            }
	            else {
	               navRep.setInvMgrCode("total");
	            }
	            alNavBean.add(navRep);
		   }
	   }catch(Exception ex){
		   throw new YssException("按资产品种统计净值出错",ex);
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   return alNavBean;
    }
    
    //modify by fangjiang 2011.11.08 story 1589
    private NavRepBean getTotalAmount() throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        NavRepBean navRep = null;
        TaTradeBean ta = new TaTradeBean();
        ta.setYssPub(pub);
        int flag = ta.getAccWayState(this.portCode);
        try {
        	if(ta.isMultiClass(this.portCode)){ //fj
        		sqlStr = " select 'TotalAmount' as FKeyCode, '实收资本：' as FKeyName, 1 as FDetail, 'Total2' as FOrderCode, " +
        				 " (case when " + flag + "=0 then sum(FPortStorageAmount) else sum(FStorageAmount) end) as FStorageAmount, " +
        				 " sum(FPortCuryCost) as FPortCuryCost from " + pub.yssGetTableName("Tb_Stock_TA") +
        				 " where FStorageDate= " + dbl.sqlDate(dDate) + " and FCheckState = 1 " + 
 		                 " and FPortCode=" + dbl.sqlString(portCode)+
        		         " and substr(fyearmonth,5,2)!='00'"; //modify by fangjiang 2013.01.04 年结以后实收资本翻倍
        	}else{
        		sqlStr = " select FPortCuryCost,'TotalAmount' as FKeyCode,'实收资本：' as FKeyName ,1 as FDetail,'Total2' as FOrderCode" +
		                 ", FStorageAmount " +
		                 " from " + pub.yssGetTableName("Tb_Stock_TA") +
		                 " where FStorageDate= " + dbl.sqlDate(dDate) +
		                 " AND FCheckState = 1 " + //2008.07.28 蒋锦 添加 审核状态的判断 BUG:0000350
		                 " and FPortCode=" + dbl.sqlString(portCode) +
		                 //-------------------------------------------------------------------------------------------------------------------
		                 (!this.invMgrCode.equalsIgnoreCase("total") ?
		                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "");
        	}             
            //-------------------------------------------------------------------------------------------------------------------
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getDouble("FPortCuryCost") == 0 && (!ta.isMultiClass(this.portCode))) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
                    return null;
                }                
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                this.Amount = rs.getDouble("FStorageAmount");
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total");
                navRep.setCuryCode(" ");
                //20120607 modified by liubo.Story #2683
                //===============================
                navRep.setPortMarketValue(rs.getDouble("FPortCuryCost"));
                navRep.setSparAmt(rs.getDouble("FStorageAmount"));
//                navRep.setPortMarketValue(rs.getDouble("FStorageAmount")); //改成取数量。sj edit 20080522
                //=============end==================
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
            }
            return navRep;
        } catch (Exception e) {
            throw new YssException("获取实收金额信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private NavRepBean getUnit() throws YssException {
        NavRepBean navRep = null;
        double unit = 0.0;
        CtlPubPara pubpara = null;
        String resultStr = "";
        try {
            if (this.Amount > 0) {
                unit = YssD.div(this.TotalValue, this.Amount);
            } else if (this.Amount == 0) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
                return null;
            }
            //-------------获取通用参数配置中，净值位数的配置 sj edit 20080601 --
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            resultStr = pubpara.getCashUnit(this.portCode);
            //---------------------------------------------------------------
            navRep = new NavRepBean();
            navRep.setNavDate(this.dDate); //净值日期
            navRep.setPortCode(this.portCode);
            navRep.setOrderKeyCode("Total3");
            navRep.setKeyCode("Unit");
            navRep.setKeyName("单位净值：");
            navRep.setDetail(0); //汇总
            navRep.setReTypeCode("Total");
            navRep.setCuryCode(" ");
            //navRep.setPortMarketValue(unit);
            navRep.setIsinCode(resultStr); //获取位数，将其存入Insi 代码中。 sj edit 20080601
            navRep.setPrice(unit); //将单位净值的值放入价格中。 sj edit 20080515
            if (!this.invMgrCode.equalsIgnoreCase("total")) {
                navRep.setInvMgrCode(this.invMgrCode);
            } else {
                navRep.setInvMgrCode("total");
            }

            return navRep;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }
    
    /**
     * 计算ETF单位现金差额
     * 单位现金差额＝（净值 / 实收基金）× 基准比例－股票篮的估值 
     * @return
     * @throws YssException
     */
    private NavRepBean calUnitCashBal() throws YssException {
        NavRepBean navRep = null;
        double unitCashBal = 0.0;
        double stockListVal = 0.0;
        HashMap etfparamHM = null;
        ETFParamSetBean etfParamBean = null;
        try {
            //-------------获取股票篮估值 -----------
            stockListVal = valStockBasket(portCode, dDate);
        	if(this.Amount == 0 || stockListVal == 0) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
        		return null;
        	}
            //------------获取基准比例---------------
            ETFParamSetAdmin etfparam = new ETFParamSetAdmin();
            etfparam.setYssPub(pub);
            etfparamHM = etfparam.getETFParamInfo(portCode);
    		etfParamBean = new ETFParamSetBean();
    		etfParamBean = (ETFParamSetBean)etfparamHM.get(portCode);
    		if(etfParamBean == null){
    			throw new YssException("组合【" + portCode + "】对应的ETF参数设置不存在或未审核！");
    		}
            if (this.Amount > 0) {
            	if(etfParamBean.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)
            			|| etfParamBean.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){//华夏ETF按照国内ETF单位现金差额的计算公式round(资产净值 /实收基金 ×最小申赎单位 - 股票篮估值，2）
            		unitCashBal = YssD.round(YssD.sub(YssD.mul(YssD.div(this.TotalValue, this.Amount), etfParamBean.getNormScale()) , stockListVal),2);
            	}else{
            		unitCashBal = YssFun.roundIt(YssD.div(this.TotalValue, this.Amount) * etfParamBean.getNormScale(),2) - stockListVal;
            	}
            }
            navRep = new NavRepBean();
            navRep.setNavDate(this.dDate); //净值日期
            navRep.setPortCode(this.portCode);
            navRep.setOrderKeyCode("Total3");
            navRep.setKeyCode("UnitCashBal");
            navRep.setKeyName("单位现金差额：");
            navRep.setDetail(0); //汇总
            navRep.setReTypeCode("Total");
            navRep.setCuryCode(" ");
            navRep.setPortMarketValue(unitCashBal); 
            if (!this.invMgrCode.equalsIgnoreCase("total")) {
                navRep.setInvMgrCode(this.invMgrCode);
            } else {
                navRep.setInvMgrCode("total");
            }

            return navRep;
        } catch (Exception e) {
            throw new YssException("计算单位现金差额出错！",e);
        }
    }
    
    private NavRepBean calStockListVal() throws YssException {
        NavRepBean navRep = null;
        double stockListVal = 0.0;
        try {
            //-------------获取股票篮估值 -----------
            stockListVal = valStockBasket(portCode, dDate);
        	if(stockListVal == 0) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
        		return null;
        	}
            navRep = new NavRepBean();
            navRep.setNavDate(this.dDate); //净值日期
            navRep.setPortCode(this.portCode);
            navRep.setOrderKeyCode("Total5");
            navRep.setKeyCode("StockListVal");
            navRep.setKeyName("股票篮估值：");
            navRep.setDetail(0); //汇总
            navRep.setReTypeCode("Total");
            navRep.setCuryCode(" ");
            navRep.setPortMarketValue(stockListVal); 
            if (!this.invMgrCode.equalsIgnoreCase("total")) {
                navRep.setInvMgrCode(this.invMgrCode);
            } else {
                navRep.setInvMgrCode("total");
            }
            return navRep;
        } catch (Exception e) {
            throw new YssException("ETF净值表生成股票篮估值出错！",e);
        }
    }
    
    /**
     * 股票篮估值
     * 如果股票篮中某一只股票的现金替代标志不为2，采用当日收盘价对该股票进行估值，当日无收盘价采用最近收盘价进行估值
     * 如果股票篮中某一只股票的现金替代标志为2，采用现金替代金额作为该股票估值结果
     * @param strPort
     * @param date
     * @return
     */
    private double valStockBasket(String strPort,Date date) throws YssException{
    	double dStockBasketValue = 0;
    	double dValuePerSec = 0;
    	double dBaseRate = 1;
    	double dPortRate = 1;
    	String strSql = "";
    	ResultSet rs = null;
    	HashMap etfParams = null;
    	try{
    		//考虑到深交所的股票篮中会有基金代码作为成分股票，所以 SQL 语句中需要去掉证券代码为一级代码、二级代码、资金代码的证券
    		ETFParamSetAdmin paramAdmin = new ETFParamSetAdmin();
			paramAdmin.setYssPub(pub);
			etfParams = paramAdmin.getETFParamInfo(strPort);
			ETFParamSetBean paramSet = 
				(ETFParamSetBean)etfParams.get(strPort);
			/**add---huhuichao 2013-7-4 BUG  8188 国泰ETF基金算出的篮子估值数据不正确  */
    		strSql = "select a.*,b.FPrice,b.FPriceDate,c.FTradeCury,d.FBaseRate,d.FPortRate from (" + 
    				" select * from " + pub.yssGetTableName("Tb_ETF_StockList") + 
    				" where FDate = " + dbl.sqlDate(date) + 
    				" and FPortCode = " + dbl.sqlString(strPort) + 
    				" AND FSecurityCode NOT IN (" + 
    				dbl.sqlString(paramSet.getOneGradeMktCode()) + "," + 
    				dbl.sqlString(paramSet.getTwoGradeMktCode()) + "," + 
    				dbl.sqlString(paramSet.getCapitalCode()) +
    				")) a left join ( " + 
    				//取最近的估值行情（考虑停牌的情况）
    				" select mk2.FPrice, mk1.FPortCode, mk1.FSecurityCode,mk1.FValDate as FPriceDate from ( " + 
    				" select max(FValDate) as FValDate,FSecurityCode,FPortCode from " + 
    				pub.yssGetTableName("Tb_Data_ValMktPrice") + 
    				" where FCheckState = 1 and FPortCode = " + dbl.sqlString(strPort) + 
    				" and FValDate <= " + dbl.sqlDate(date) + 
    				" group by FSecurityCode,FPortCode) mk1 join (" + 
    				" select distinct FPrice, FSecurityCode,FValDate from " + 
    				pub.yssGetTableName("Tb_Data_ValMktPrice") + 
    				" where FCheckState = 1 and FPortCode = " + dbl.sqlString(strPort) + 
    				" order by FValDate desc) mk2 on mk1.FSecurityCode =  mk2.FSecurityCode and mk1.FValDate =  mk2.FValDate " + 
    				" ) b on b.FPortCode = a.FPortCode and b.FSecurityCode = a.FSecurityCode" + 
    				" left join (select FSecurityCode,FTradeCury from " + pub.yssGetTableName("Tb_Para_Security") + 
    				" where FSecurityCode in (select FSecurityCode from " + pub.yssGetTableName("Tb_ETF_StockList") + 
    				" )) c on c.FSecurityCode = a.FSecurityCode" + 
    				" left join (select FCuryCode,FBaseRate,FPortRate from " + pub.yssGetTableName("Tb_Data_ValRate") + 
    				" where FPortCode = " + dbl.sqlString(strPort) + 
    				" and FValDate = " + dbl.sqlDate(date) + 
    				" ) d on d.FCuryCode = c.FTradeCury";
    		/**end---huhuichao 2013-7-4 BUG  8188*/			
    		rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
    		while(rs.next()){
    			if(rs.getDouble("FBaseRate") != 0){
    				dBaseRate = rs.getDouble("FBaseRate");
    			}
    			if(rs.getDouble("FPortRate") != 0){
    				dPortRate = rs.getDouble("FPortRate");
    			}
    			if(rs.getString("FReplaceMark").equals("2") || 
    						rs.getString("FReplaceMark").equals("6")){
    				//必须现金替代（ 深交所：2   上交所：6）panjunfang modify 20111115 QDV4易方达基金2011年7月27日01_A
    				dStockBasketValue = dStockBasketValue + rs.getDouble("FTotalMoney");
    			}else{
    				if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){
    					//再实时加均摊的补票方式中(华夏)，因为二版系统是将行情算好后再导入系统，我们我们在计算时先计算行情并 Round8后再乘以数量
    					dValuePerSec = 
    						YssD.mul(
    								rs.getDouble("FAmount"),
    								YssD.round(
		    								YssD.div(
		    										YssD.mul(
		    												rs.getDouble("FPrice"), 
		    												dBaseRate), 
		    										dPortRate),
		    								8));
    					dStockBasketValue = dStockBasketValue + YssD.round(dValuePerSec,2);
    				} else {
    					//成份股市值 = 股票数量*收市价*外汇中间价
    					dValuePerSec = YssD.mul(rs.getDouble("FAmount"), YssD.div(YssD.mul(rs.getDouble("FPrice"), dBaseRate), dPortRate));
    					dStockBasketValue = dStockBasketValue + YssFun.roundIt(dValuePerSec,2);
    				}			
    			}
    		}
			if(dStockBasketValue <= 0){//如果股票篮净值不大于0，说明没有读入股票篮数据
				//throw new YssException("组合【" + strPort + "】日期【" + YssFun.formatDate(date,"yyyy-MM-dd") +  "】的股票篮数据未导入！");
			}
    		return dStockBasketValue;
    	}catch(Exception e){
    		throw new YssException("股票篮估值出错！",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }

    /**
     * 2008-12-01 linjunyun 净值报表中增加累计净值 bug:
     * @return NavRepBean
     * @throws YssException
     */
    private NavRepBean getAccumulateUnit() throws YssException {
        NavRepBean navRep = null;
        double unit = 0.0;
        CtlPubPara pubpara = null;
        String resultStr = "";
        try {
            if (this.Amount > 0) {
                unit = YssD.div(this.TotalValue, this.Amount);
            } else if (this.Amount == 0) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
                return null;
            }
            //-------------获取通用参数配置中，净值位数的配置 sj edit 20080601 --
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            resultStr = pubpara.getCashUnit(this.portCode);
            //---------------------------------------------------------------
            navRep = new NavRepBean();
            navRep.setNavDate(this.dDate); //净值日期
            navRep.setPortCode(this.portCode);
            navRep.setOrderKeyCode("Total7");
            navRep.setKeyCode("AccumulateUnit");
            navRep.setKeyName("累计净值：");
            navRep.setDetail(0); //汇总
            navRep.setReTypeCode("Total");
            navRep.setCuryCode(" ");
            //navRep.setPortMarketValue(unit);
            navRep.setIsinCode(resultStr); //获取位数，将其存入Insi 代码中。 sj edit 20080601
            //---MS00351 QDV4海富通2009年4月3日01_AB 当日单位净值+累计分红数据=累计单位净值 ----------
//         navRep.setPrice(unit); //将单位净值的值放入价格中。 sj edit 20080515
//            navRep.setPrice(YssD.add(unit, getAccummulateDivided()));
            //因为增加新的功能，屏掉以前的计算单位净值的方法，调用下面新的方法。MS00760 需要增加基金拆分的功能
            //新方法不影响以前没有基金拆分的情形。没有基金拆分时，新方法中还是用以前的计算方法。
            /**add---shashijie 2013-2-4 BUG 7056 QDII基金分红后，净值表和财务估值表的累计单位净值算法不一致
             * 这里取值与财务保持一直,保留位数更具通参设置*/
            int lDecs = getIDecs(this.portCode);//获取通参设置的保留位数
            unit = YssFun.roundIt(unit, lDecs);
			/**end---shashijie 2013-2-4 BUG 7056 */
            navRep.setPrice(getSplitUnit(unit));//add by xuxuming,20091027.MS00760 需要增加基金拆分的功能
            //---------------------------------------------------------------------------------
            if (!this.invMgrCode.equalsIgnoreCase("total")) {
                navRep.setInvMgrCode(this.invMgrCode);
            } else {
                navRep.setInvMgrCode("total");
            }

            return navRep;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    /**shashijie 2013-2-4 BUG 7056 通过通用参数来获取累计净值应该保留的小数位数*/
	private int getIDecs(String portCode) throws YssException {
		int iDigit = 0;
		CtlPubPara ctlPara = new CtlPubPara();
		ctlPara.setYssPub(pub);
		iDigit = Integer.parseInt(ctlPara.getCashUnit(portCode));//通过通用参数来获取累计净值应该保留的小数位数
		return iDigit;
	}

	private NavRepBean getTotalMV() throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        NavRepBean navRep = null;
        try {
            /*strSql = " select sum(FPortMVValue) as FPortMVValue,'Total3' as FOrderCode,'MV' as FKeyCode ," +
                 " '估值增值：' as FKeyName, 1 as FDetail from (" +
                 " select sum(FPortMVValue) as FPortMVValue" +
                 " from " + pub.yssGetTableName("tb_data_navdata") +
                 " where FNAVDate = " + dbl.sqlDate(this.dDate) +
                 " and FPortCode = " + dbl.sqlString(this.portCode) +
                 //-------------------------------------------------------------------------------------------------------------------

                  " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode)  +
                 //-------------------------------------------------------------------------------------------------------------------
                 " and FGradeType1 is not null and FGradeType2 is null and FGradeType3 is null and FGradeType4 is null " +
                 " and FGradeType5 is null and FGradeType6 is null " +
                 //" union " +
                 //" select sum(FFXValue) as FPortMVValue " +
                 //" from " + pub.yssGetTableName("tb_data_navdata") +
                 //" where FNAVDate = " + dbl.sqlDate(this.dDate) +
                 //" and FPortCode = " + dbl.sqlString(this.portCode) +
                 //-------------------------------------------------------------------------------------------------------------------

                  //" and FInvMgrCode = " + dbl.sqlString(this.invMgrCode)  +
                 //-------------------------------------------------------------------------------------------------------------------
                 //" and FGradeType1 is not null and FGradeType2 is null and FGradeType3 is null and FGradeType4 is null " +
                 //" and FGradeType5 is null and FGradeType6 is null and FReTypeCode <> 'Cash' and FKeyCode <> ' ' " +
                 " ) dat ";*/
            //------------------------------取所有的明细记录取和 sj --------------------------------------------//
			sqlStr = " select sum((case when fGradetype6 = '10BSC' then  FPortMVValue *(-1) else FPortMVValue end)) "//add by zhouxiang  证券借贷借入估增做减法运算
					+ "as FPortMVValue,'Total3' as FOrderCode,'MV' as FKeyCode ,"
					+ " '估值增值：' as FKeyName, 1 as FDetail from "
					+
					// pub.yssGetTableName("tb_data_navdata") +
					this.tempViewName
					+ // xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
					" where FNAVDate = "
					+ dbl.sqlDate(this.dDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.portCode)
					+
					// ------------------------------------------------------------------------------------------
					" and FInvMgrCode = "
					+ dbl.sqlString(this.invMgrCode)
					+ " and FDetail = 0 and FReTypeCode <> 'Total'";
            //------------------------------------------------------------------------------------------------
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getDouble("FPortMVValue") == 0) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
                    return null;
                }
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total");
                navRep.setCuryCode(" ");
                navRep.setPortMarketValue(rs.getDouble("FPortMVValue"));
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
            }
            /*
             * 期货的估增余额改成从财务估值表取数
            double portMV = navRep.getPortMarketValue();
            CtlPubPara pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            if(pubpara.getParaValue("ISCredit", "selPort", "cboISCredit", this.portCode)){
            	portMV = YssD.add(portMV, this.getFportcuryMoney(this.dDate, YssOperCons.YSS_ZJDBZLX_FU01_MV));              	
            }
            if(pubpara.getParaValue("ISCredit_ZQ", "selPort", "cboISCredit", this.portCode)){
            	portMV = YssD.add(portMV, this.getFportcuryMoney(this.dDate, YssOperCons.YSS_ZJDBZLX_FU02_MV));              	
            }
            if(pubpara.getParaValue("ISCredit_WH", "selPort", "cboISCredit", this.portCode)){
            	portMV = YssD.add(portMV, this.getFportcuryMoney(this.dDate, YssOperCons.YSS_ZJDBZLX_FU03_MV));              	
            }   
            navRep.setPortMarketValue(portMV);
            */
            return navRep;
        } catch (Exception e) {
            throw new YssException("获取估值增值信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private NavRepBean getTotalFX() throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        NavRepBean navRep = null;
        try {
            sqlStr = " select sum(" +
                dbl.sqlIsNull("FFXValue", "0") + ") as FFXValue" +
                ", 'Total4' as FOrderCode,'FX' as FKeyCode,'汇兑损益：' as FKeyName,1 as FDetail " +
                " from " +
                " (select sum(" +
                dbl.sqlIsNull("FFXValue", "0") + ") as FFXValue " +
                //" from " + pub.yssGetTableName("tb_data_navdata") +
                " from " + this.tempViewName +//xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                " where FNAVDate =  " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                //-------------------------------------------------------------------------------------------------------------------

                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                //-------------------------------------------------------------------------------------------------------------------
                " and FReTypeCode = 'Security' and  " +
                " FGradeType6 in ('06RE', '06FI') " +
                //" and FKeyCode not like '%TD' " +
                " union all " + //加上all是为了防止union的相同而只显示一条。sj edit 20080505
                " select sum(" +
                dbl.sqlIsNull("FFXValue", "0") + ") as FFXValue " +
                //" from " + pub.yssGetTableName("tb_data_navdata") +
                " from " + this.tempViewName +//xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                " where FNAVDate =  " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                //-------------------------------------------------------------------------------------------------------------------

                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                //-------------------------------------------------------------------------------------------------------------------
                " and FReTypeCode = 'Cash' and  " +
                " FGradeType6 = '06DE' " +
                " union all " + //加上all是为了防止union的相同而只显示一条。sj edit 20080505
                " select sum(FFXValue) as FFXValue " +
                //" from " + pub.yssGetTableName("tb_data_navdata") +
                " from " + this.tempViewName +//xuqiji 20091213 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                " where FNAVDate = " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                //-------------------------------------------------------------------------------------------------------------------

                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                //-------------------------------------------------------------------------------------------------------------------
                " and FGradeType1 is not null and " +
                " (FGradeType2 is null or FGradeType2 = ' ') and " +
                " (FGradeType3 is null or FGradeType3 = ' ') and " +
                " (FGradeType4 is null or FGradeType4 = ' ') and " +
                " (FGradeType5 is null or FGradeType5 = ' ') and " +
                " (FGradeType6 is null or FGradeType6 = ' ') and FKeyCode <> ' ') FX ";
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getDouble("FFXValue") == 0) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
                    return null;
                }
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total");
                navRep.setCuryCode(" ");
                navRep.setPortMarketValue(rs.getDouble("FFXValue"));
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }

            }

            return navRep;
        } catch (Exception e) {
            throw new YssException("获取汇兑损益信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private ArrayList getTotalPL() throws YssException {
        String strSql = "";
        ArrayList list = new ArrayList();
        ResultSet rs = null;
        NavRepBean navRep = null;
        TaTradeBean ta = new TaTradeBean(); //add by fangjiang story 1589 2011.11.08
        ta.setYssPub(pub);
        int flag = ta.getAccWayState(this.portCode);
        try {
        	/**Start---panjunfang 2013-9-12 BUG 9363 */
        	//净值表在统计损益平准金时不需要区分是否为分级组合，不管是否分级，统一汇总所有分级组合的损益平准金
        	/*
        	if(ta.isMultiClass(this.portCode) && flag == 2){ //modify by fangjiang 2012.05.05 story 2565
	             strSql = " select sum(FCuryUnpl) as FPortCuryUnpl,'UnPL' as FKeyCode,'损益平准金(未实现)：' as FKeyName ,1 as FDetail,'Total5' as FOrderCode," +
		                " sum(FCuryPl) as FPortCuryPl,'PL' as FOKeyCode,'损益平准金(已实现)：' as FOKeyName,1 as FODetail,'Total6' as FOOrderCode" +
		                " from " + pub.yssGetTableName("Tb_Stock_TA") +
		                " where FStorageDate = " + dbl.sqlDate(dDate) +
		                " and FPortCode = " + dbl.sqlString(portCode) +
		                " and FYearMonth= " + dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
		                " AND FCheckState = 1 " + //2008.07.28 蒋锦 添加 审核状态的判断 BUG:0000350
		                //-------------------------------------------------------------------------------------------------------------------
		                (!this.invMgrCode.equalsIgnoreCase("total") ?
		                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : " ");
		            //-------------------------------------------------------------------------------------------------------------------
        	}else{
        		 strSql = " select FPortCuryUnpl,'UnPL' as FKeyCode,'损益平准金(未实现)：' as FKeyName ,1 as FDetail,'Total5' as FOrderCode," +
		                " FPortCuryPl,'PL' as FOKeyCode,'损益平准金(已实现)：' as FOKeyName,1 as FODetail,'Total6' as FOOrderCode" +
		                " from " + pub.yssGetTableName("Tb_Stock_TA") +
		                " where FStorageDate = " + dbl.sqlDate(dDate) +
		                " and FPortCode = " + dbl.sqlString(portCode) +
		                " and FYearMonth= " + dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
		                " AND FCheckState = 1 " + //2008.07.28 蒋锦 添加 审核状态的判断 BUG:0000350
		                //-------------------------------------------------------------------------------------------------------------------
		                (!this.invMgrCode.equalsIgnoreCase("total") ?
		                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : " ");
		        		//-------------------------------------------------------------------------------------------------------------------
        	}
        	*/
            strSql = " select sum(FPortCuryUnpl) as FPortCuryUnpl," +
            		"'UnPL' as FKeyCode,'损益平准金(未实现)：' as FKeyName ,1 as FDetail,'Total5' as FOrderCode," +
            		" sum(FPortCuryPl) as FPortCuryPl,'PL' as FOKeyCode,'损益平准金(已实现)：' as FOKeyName," + 
            		" 1 as FODetail,'Total6' as FOOrderCode from " + pub.yssGetTableName("Tb_Stock_TA") +
            		" where FStorageDate = " + dbl.sqlDate(dDate) +
            		" and FPortCode = " + dbl.sqlString(portCode) +
            		" and FYearMonth= " + dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
            		" AND FCheckState = 1 " + //2008.07.28 蒋锦 添加 审核状态的判断 BUG:0000350
            		(!this.invMgrCode.equalsIgnoreCase("total") ? 
            				" and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : " ");
			/**End---panjunfang 2013-9-12 BUG 9363*/
            
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getDouble("FPortCuryUnpl") == 0 && //将或改成与。sj edit 20080804
                    rs.getDouble("FPortCuryPl") == 0) { //当数值为0时，跳过。这在资产估值时按投资经理循环统计净值时有用。sj edit 20080401
                    return null;
                }
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total");
                navRep.setCuryCode(" ");
                navRep.setPortMarketValue(rs.getDouble("FPortCuryUnpl"));
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
                list.add(navRep);
                //-------------------------------------------------------------------
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOOrderCode"));
                navRep.setKeyCode(rs.getString("FOKeyCode"));
                navRep.setKeyName(rs.getString("FOKeyName"));
                navRep.setDetail(rs.getDouble("FODetail")); //汇总
                navRep.setReTypeCode("Total");
                navRep.setCuryCode(" ");
                navRep.setPortMarketValue(rs.getDouble("FPortCuryPl"));
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
                list.add(navRep);
                if(ta.isMultiClass(this.portCode)){
                	break;
                }
            }
            return list;
        } catch (Exception e) {
            throw new YssException("获取汇兑损益信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取现金头寸，通过通用参数判断是否包括证券清算款。 sj add 20080530
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList getCash() throws YssException {
        String sqlStr = "";
        ArrayList list = new ArrayList();
        ResultSet rs = null;
        NavRepBean navRep = null;
        CtlPubPara pubpara = null;
        String resultStr = "";
        try {
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            resultStr = pubpara.getCashLiqu(this.portCode);
            if (pubpara.parseResults(resultStr).equalsIgnoreCase("no")) { //不包括证券清算款
                sqlStr = "select a.FMarketValue as FMarketValue," +
                    " 1 as FDetail," +
                    dbl.sqlString("Total6") + dbl.sqlJoinString().trim() +
                    "b.FCuryCode as FOrderCode," +
                    " b.FCuryCode as FKeyCode," +
                    " b.FCuryCode as FCuryCode," +
                    " b.FCuryName as FKeyName " +
                    " from (select sum(FMarketValue * FInOut) as FMarketValue, FCuryCode " +
                    " from " + pub.yssGetTableName("tb_data_NavData") +
                    " where FNAVDate = " + dbl.sqlDate(this.dDate) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                    " and FReTypeCode = 'Cash' and FReTypeCode <> 'Total' and " +
                    " FDetail = 0 and FGradeType1 = '01' and FGradeType2 in ('0101','0102') and " + //加入定期存款的值。 sj edit 20080605
                   // " (FGradeType5 is null or FGradeType5 = ' ')" +
                    //NO.125 用户需要对组合按资本类别进行子组合的分类   add by jiangshichao 2011.01.21
                    " (FGradeType6 is null or FGradeType6 = ' ')" +
                    " group by FCuryCode) a " + //edited by libo MS00615 新成立还未买入证券时，资产估值无法生成可用头寸数据 QDV4赢时胜（上海）2009年8月03日01_B
                    " left join (select FCuryCode,FCuryName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1) b on a.FCuryCode = b.FCuryCode";
                rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    if (rs.getDouble("FMarketValue") == 0) {
                        continue; //将可用头寸为0的记录去除。 sj edit 20080605
                    }
                    navRep = new NavRepBean();
                    navRep.setNavDate(this.dDate); //净值日期
                    navRep.setPortCode(this.portCode);
                    navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                    navRep.setKeyCode(rs.getString("FKeyCode"));
                    navRep.setKeyName(rs.getString("FKeyName"));
                    navRep.setDetail(rs.getDouble("FDetail")); //汇总
                    navRep.setReTypeCode("UseCash");
                    navRep.setCuryCode(rs.getString("FCuryCode"));
                    navRep.setPortMarketValue(rs.getDouble("FMarketValue"));
                    if (!this.invMgrCode.equalsIgnoreCase("total")) {
                        navRep.setInvMgrCode(this.invMgrCode);
                    } else {
                        navRep.setInvMgrCode("total");
                    }
                    list.add(navRep);
                }
            } else if (pubpara.parseResults(resultStr).equalsIgnoreCase("yes")) { //包括证券清算款
                sqlStr = "select r.FMarketValue as FMarketValue," +
                    " 1 as FDetail," +
                    dbl.sqlString("Total6") + dbl.sqlJoinString().trim() +
                    "b.FCuryCode as FOrderCode," +
                    " b.FCuryCode as FKeyCode," +
                    " b.FCuryCode as FCuryCode," +
                    " b.FCuryName as FKeyName " +
                    " from (select sum(FMarketValue) as FMarketValue,FCuryCode from (select sum(FMarketValue * FInOut) as FMarketValue, FCuryCode " +
                    " from " + pub.yssGetTableName("tb_data_NavData") +
                    " where FNAVDate = " + dbl.sqlDate(this.dDate) +
                    " and ((FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                    " and FReTypeCode = 'Cash' and FReTypeCode <> 'Total' and " +
                    " FDetail = 0 and FGradeType1 = '01' and FGradeType2 in ('0101','0102') " + //加入定期存款的值。 sj edit 20080605
                    " and (FGradeType5 is null or FGradeType5 = ' '))" +
                    " or FGradeType5 like '%TD')" +
                    " group by FCuryCode, FInOut) a group by a.FCuryCode ) r " +
                    " left join (select FCuryCode,FCuryName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1) b on r.FCuryCode = b.FCuryCode";
                rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    if (rs.getDouble("FMarketValue") == 0) {
                        //return null;
                        continue; //将可用头寸为0的记录去除。 sj edit 20080605
                    }
                    navRep = new NavRepBean();
                    navRep.setNavDate(this.dDate); //净值日期
                    navRep.setPortCode(this.portCode);
                    navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                    navRep.setKeyCode(rs.getString("FKeyCode"));
                    navRep.setKeyName(rs.getString("FKeyName"));
                    navRep.setDetail(rs.getDouble("FDetail")); //汇总
                    navRep.setReTypeCode("UseCash");
                    navRep.setCuryCode(rs.getString("FCuryCode"));
                    navRep.setPortMarketValue(rs.getDouble("FMarketValue"));
                    if (!this.invMgrCode.equalsIgnoreCase("total")) {
                        navRep.setInvMgrCode(this.invMgrCode);
                    } else {
                        navRep.setInvMgrCode("total");
                    }
                    list.add(navRep);
                }
            }
//在其他bean中设置了默认情况的判断，这里的代码舍弃。 sj edit 20080605
//         else {//若是没有配置的情况，以不包括的方式计算
//            sqlStr = "select a.FMarketValue as FMarketValue," +
//                  " 1 as FDetail," +
//                  dbl.sqlString("Total6") + dbl.sqlJoinString().trim() +
//                  "b.FCuryCode as FOrderCode," +
//                  " b.FCuryCode as FKeyCode," +
//                  " b.FCuryCode as FCuryCode," +
//                  " b.FCuryName as FKeyName " +
//                  " from (select sum(FMarketValue * FInOut) as FMarketValue, FCuryCode " +
//                  " from " + pub.yssGetTableName("tb_data_NavData") +
//                  " where FNAVDate = " + dbl.sqlDate(this.dDate) +
//                  " and FPortCode = " + dbl.sqlString(this.portCode) +
//                  " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
//                  " and FReTypeCode = 'Cash' and FReTypeCode <> 'Total' and " +
//                  " FDetail = 0 and FGradeType1 = '01' and FGradeType2 = '0101' and " +
//                  " (FGradeType5 is null or FGradeType5 = ' ')" +
//                  " group by FCuryCode, FInOut) a " +
//                  " left join (select FCuryCode,FCuryName from " +
//                  pub.yssGetTableName("Tb_Para_Currency") +
//                  " where FCheckState = 1) b on a.FCuryCode = b.FCuryCode";
//            rs = dbl.openResultSet(sqlStr);
//            while (rs.next()) {
//               if (rs.getDouble("FMarketValue") == 0) {
//                  return null;
//               }
//               navRep = new NavRepBean();
//               navRep.setNavDate(this.dDate); //净值日期
//               navRep.setPortCode(this.portCode);
//               navRep.setOrderKeyCode(rs.getString("FOrderCode"));
//               navRep.setKeyCode(rs.getString("FKeyCode"));
//               navRep.setKeyName(rs.getString("FKeyName"));
//               navRep.setDetail(rs.getDouble("FDetail")); //汇总
//               navRep.setReTypeCode("UseCash");
//               navRep.setCuryCode(rs.getString("FCuryCode"));
//               navRep.setPortMarketValue(rs.getDouble("FMarketValue"));
//               if (!this.invMgrCode.equalsIgnoreCase("total")) {
//                  navRep.setInvMgrCode(this.invMgrCode);
//               }
//               else {
//                  navRep.setInvMgrCode("total");
//               }
//               list.add(navRep);
//            }
//         }
            return list;
        } catch (Exception e) {
            throw new YssException("获取可用头寸出错！");
        } finally {
            dbl.closeResultSetFinal(rs); //close rs 20080716 sj
        }
    }

    /**
     * 获取累计分红数据
     * @return double 返回累计分红数据
     * @throws YssException
     * MS00351 QDV4海富通2009年4月3日01_AB
     */
    private double getAccummulateDivided() throws YssException {
        double accummulate = 0.0;
        String sqlStr = null;
        ResultSet rs = null;
        try {
            sqlStr = "select sum(FSellPrice) as FAccumulateDivided from " + pub.yssGetTableName("TB_TA_TRADE") +
                " where FCheckState = 1 and FConfimDate <=" + dbl.sqlDate(this.dDate) + " and FSellType = '03'"; //获取累计分红价格，数据为小于等于当前日期
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                accummulate = rs.getDouble("FAccumulateDivided"); //获取汇总的值。
            }
        } catch (Exception e) {
            throw new YssException("获取累计分红数据出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return accummulate;
    }

  //=========add by xuxuming,20091027.MS00760 需要增加基金拆分的功能.QDV4交银施罗德2009年10月22日01_A===== 
    /**
     * 获取基金拆分时的各次分红金额
     * @param dStartDate,dEndDate 取两个日期之间的所有分红金额之和
     * @return Double 返回两次日期之间的分红金额总额
     * @throws YssException
     * @author ysstech_xuxuming
     * 计算累计净值时，要用到分红金额
     */
	private double getSplitDivided(java.util.Date dStartDate,
			java.util.Date dEndDate) throws YssException {
		String sqlStr = null;
		ResultSet rs = null;
		double dSplitDivided = 0.0;// 存放基金拆分的分红金额
		try {
			sqlStr = "select sum(FSellPrice) as FSplitDivided from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FPortCode = "
					+ dbl.sqlString(this.portCode) + " and FSellType = '03'"
					+ " and FCuryCode = " + dbl.sqlString(this.getPortCuryCode()); // 获取累计分红价格，数据为小于等于当前日期
            //传进来的日期是 拆分日期。因为拆分日期当天不会出现分红，所以统不统计拆分当天的分红金额是没有影响的
			if (dStartDate != null&&!YssFun.formatDate(dStartDate).equals("9998-12-31")) {
				sqlStr += " and FConfimDate >= " + dbl.sqlDate(dStartDate);//因为拆分日期当天无分红，此时用 >= 和用 > 是一样的。
			}
			if (dEndDate != null&&!YssFun.formatDate(dEndDate).equals("9998-12-31")) {
				sqlStr += " and FConfimDate <= " + dbl.sqlDate(dEndDate);
			}
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				dSplitDivided = rs.getDouble("FSplitDivided");
			}
		} catch (Exception e) {
			throw new YssException("获取基金拆分时的分红数据出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dSplitDivided;
	}
    
    /**
     * 获取基金拆分的每次拆分比例
     * @return HashMap 返回每次拆分比例
     * @throws YssException
     * @author ysstech_xuxuming
     * 计算累计净值时，要用到每次的拆分比例 
     */
	private ArrayList getHmSplitRatio() throws YssException {
		String sqlStr = "";
		ResultSet rs = null;
//		HashMap hmSplitRatio = new HashMap();// 存放每次拆分比例
		ArrayList arraySplitDate = new ArrayList();//存储拆分时间,拆分比例，因为要按顺序取
		try {
			sqlStr = "select FSplitRatio,FConfimDate from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState=1 and FPortCode= "
					+ dbl.sqlString(this.portCode)
					+ " and FSplitRatio >0"//排除为空的记录
					+ " and FConfimDate <="+dbl.sqlDate(this.dDate)//只对之前的日期进行计算，所以只获取之前的拆分比例
					+ " order by FConFimDate desc";
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				arraySplitDate.add(YssFun.formatDate(rs.getDate("FConfimDate"))+YssCons.YSS_ITEMSPLITMARK1+
						new Double(rs.getDouble("FSplitRatio")).toString());
//				hmSplitRatio.put(YssFun.formatDate(rs.getDate("FConfimDate")),
//						new Double(rs.getDouble("FSplitRatio")));// 拆分日期作为KEY，保存拆分比例
			}
		} catch (Exception e) {
			throw new YssException("获取基金拆分的每次拆分比例出现异常");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return arraySplitDate;
	}
	
	/**
	 * 获取该组合下是否存在基金拆分
	 * @return boolean 返回有无基金拆分，此值用来决定累计净值的计算方式
	 * @throws YssException
     * @author ysstech_xuxuming
     * 计算累计净值时，要用到此返回值
	 */
	private boolean hasFundSplit() throws YssException{
		ResultSet rs = null;
		String sqlStr = "";
		try{
			sqlStr = "select FSplitRatio from "
				+ pub.yssGetTableName("TB_TA_TRADE")
				+ " where FCheckState=1 and FPortCode= "
				+ dbl.sqlString(this.portCode)
			    + " and FSplitRatio!=''";//排除为空的记录
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				if(rs.getDouble("FSplitRatio")>0){//拆分比例大于0，表明存在基金拆分
					return true;					
				}
			}
		} catch (Exception e) {
			throw new YssException("获取是否有基金拆分时出现异常");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return false;
	}
	
	/**
	 * 计算累计单位净值
	 * 分两种情形：1.没有基金拆分时；2.有基金拆分时。
	 * 1.没有基金拆分时算法：基金份额净值+累计分红金额(以前就是这么计算的)
	 * 2.有基金拆分时，累计净值的算法如下:
     *累计单位净值=｛【（基金份额净值+基金最后一次拆分后的单位分红金额）*基金最后一次拆细比例 +
     *   基金最后一次拆细前的的单位分红金额】×基金倒数第二次拆分比例＋基金倒数第二次拆分前的分红金额 ｝×……
     *   基金第一次拆分比例＋基金第一次拆分前的分红金额。
     *@return Double 累计净值
     *@param  Double unit 基金份额净值
     *@throws YssException
     *@author ysstech_xuxuming
	 */
	private double getSplitUnit(double dUnit) throws YssException {
//		HashMap hmSplitRatio = null;// 拆分日期+拆分比例
		ArrayList arraySplitRatio = new ArrayList();// 拆分日期
		ArrayList arraySplitAll = new ArrayList();//存储拆分时间，
		double dSplitUnit = 0.0;// 累计单位净值
		arraySplitAll = this.getHmSplitRatio();
		String strSplitDate = YssFun.formatDate(this.dDate);// 字符以界面上选的日期开始
		try {
			for (Iterator iter = arraySplitAll.iterator(); iter
					.hasNext();) {
				String tmpRatioAll = (String) iter.next();
				String[] tmpAry = null;
				tmpAry = tmpRatioAll.split(YssCons.YSS_ITEMSPLITMARK1);
				String tmpDate = (String) tmpAry[0];// 取出里面保存的日期
				arraySplitRatio.add(tmpAry[1]);// 将拆分比例保存到列表
				strSplitDate += YssCons.YSS_ITEMSPLITMARK1 + tmpDate
						+ YssCons.YSS_LINESPLITMARK + tmpDate;// 将每次拆分日期保存
				// 将每次拆分日期 分为区间保存，两两为一组，构成一个区间；如：date1,date2,date3
			}
			strSplitDate += YssCons.YSS_ITEMSPLITMARK1
					+ YssFun.formatDate(this.dDate);// 字符以界面上选的日期结束
			// 拼接后的字符串为：9998-12-31 \tdate1\f\fdate1 \tdate2\f\fdate2
			// \tdate3\f\fdate3 \t9998-12-31
			// 这样做的好处是：按 \f\f 来分割，得到 9998-12-31\tdate1; date1\tdate2;
			// date2\tdate3; date3\t9998-12-31
			// 这样就得到了以日期构成的 区间，后面要根据此区间来算 区间内的分红金额
			String reqAry[] = null;
			String tmpAry[] = null;
			reqAry = strSplitDate.split(YssCons.YSS_LINESPLITMARK);
			dSplitUnit = dUnit;
			for (int i = 0; i < arraySplitRatio.size(); i++) {// 遍历每一次拆分比例
				tmpAry = reqAry[i].split(YssCons.YSS_ITEMSPLITMARK1);// 日期区间与列表中拆分比例是对应的
				dSplitUnit = YssD.mul(Double.parseDouble(arraySplitRatio.get(i)
						.toString()), YssD.add(dSplitUnit, this
						.getSplitDivided(YssFun.toDate(tmpAry[1]), YssFun
								.toDate(tmpAry[0]))));
			}
			// 1.无基金拆分情形，要加上累计分红金额；
			// 2.有基金拆分的情形，前面已经计算了每次拆分比例和每次拆分后分红金额，现在还需要加上第一次拆分前的分红金额
			// 因为日期区间段比拆分比例要多一次，前面对拆分比例的遍历是没有考虑最后这一次的。
			tmpAry = reqAry[reqAry.length - 1]
					.split(YssCons.YSS_ITEMSPLITMARK1);// 取最后一次日期区间，即是第一次分红之前的时间段(有基金拆分时),或界面上传来的日期(无基金拆分时)
			dSplitUnit = YssD.add(dSplitUnit, this.getSplitDivided(YssFun  //只需要根据后一个日期来计算，统计<=tmpAry[0]的分红，故前一日期设为固定值，不参与计算
					.toDate("9998-12-31"), YssFun.toDate(tmpAry[0])));// 基金第一次拆分前的分红金额(有基金拆分时),或 累计分红金额(无基金拆分时)

		} catch (Exception e) {
			throw new YssException("计算累计单位净值时出现异常");
		} finally {
			arraySplitAll.clear();
			arraySplitRatio.clear();
		}
		return dSplitUnit;
	}
	//===========end,MS00760=================================================================
	
    /**
     * 添加对现金类应收应付项的汇总合计
     * QDV4华夏2009年8月24日03_A MS00652 by leeyu 20090831
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList getTotalSec() throws YssException {
        ArrayList alTotal = new ArrayList();
        String sqlStr = "";
        ResultSet rs = null;
        String cashTotal = "", Total = "";
        CtlPubPara pubpara = null;
        NavRepBean navRep = null;
        try {
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            HashMap hmNav = pubpara.getNavDataParams(this.portCode);
            if (hmNav.get(this.portCode) == null)
                return alTotal;
            String[] reResult = String.valueOf(hmNav.get(this.portCode)).split("\t");
            cashTotal = reResult[0];
            Total = reResult[1];
            if (cashTotal.equalsIgnoreCase("true")) {
                sqlStr = "select a.*,b.FTsFTypeName from (select FNavDate,FPortCode,FReTypeCode,FInOut,FCuryCode,sum(FCost) as FCost,sum(FPortCost) as FPortCost,sum(FMarketValue) as FMarketValue,sum(FPortMarketValue) as FPortMarketValue,sum(FMVValue) as FMVValue,sum(FPortMVValue) as FPortMVValue,sum(FFXValue) as FFXValue,FGradeType1,FGradeType2,FGradeType3," +
                		" FGradeType4,FTsfTypeCode as FGradeType5,FTsfTypeCode from " +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
                    //" (select n.*," + dbl.sqlLeft("FGradeType5", 2) + " as FTsfTypeCode from " + pub.yssGetTableName("Tb_Data_NavData") + " n where FNavDate=" +
                " (select n.*," + dbl.sqlLeft("FGradeType6", 2) + " as FTsfTypeCode from " + pub.yssGetTableName("Tb_Data_NavData") + " n where FNavDate=" +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
                    dbl.sqlDate(this.dDate) + " and FPortCode=" + dbl.sqlString(this.portCode) +
                   // " and FGRadeType1='01' and FGradeType2='0101' and ( FGradeType5 like '06__' or FGradeType5 like '07__') " +

                    //20130320 modified by liubo.Bug #7350，计算现金类应付合计项，应加上07TA_Fee应付赎回费
                    //+++++++++++++++++++++++++++++++++++++++++
                    " and FGRadeType1='01' and FGradeType2='0101' and ( FGradeType6 like '06__' or FGradeType6 like '07__' or FGradeType6 = '07TA_Fee') " +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
                    //++++++++++++++++++++end+++++++++++++++++++++
                    (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ") + " and FReTypeCode='Cash' ) na " + //获取现金类的所有应收应付数据，并按币种类汇总
                    //" group by FNavDate,FPortCode,FReTypeCode,FInOut,FCuryCode,FGradeType1,FGradeType2,FGradeType3,FTsfTypeCode union" +
                    " group by FNavDate,FPortCode,FReTypeCode,FInOut,FCuryCode,FGradeType1,FGradeType2,FGradeType3,FGradeType4,FTsfTypeCode " +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
                    "union " +
                    " select FNavDate,FPortCode,FReTypeCode,FInOut,'Total' as FCuryCode,sum(FCost) as FCost,sum(FPortCost) as FPortCost,sum(FMarketValue) as FMarketValue,sum(FPortMarketValue) as FPortMarketValue,sum(FMVValue) as FMVValue,sum(FPortMVValue) as FPortMVValue,sum(FFXValue) as FFXValue,FGradeType1,FGradeType2," +
                   // "FTsfTypeCode as FGradeType3,'' as FGradeType4,FTsfTypeCode " +
                    " FGradeType3,FTsfTypeCode as FGradeType4,'' as FGradeType5,FTsfTypeCode " +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
                    //" from (select n.*, subStr(FGradeType5, 0, 2) as FTsfTypeCode  from " + pub.yssGetTableName("Tb_Data_NavData") + " n where FNavDate = " +
                    " from (select n.*, subStr(FGradeType6, 0, 2) as FTsfTypeCode  from " + pub.yssGetTableName("Tb_Data_NavData") + " n where FNavDate = " +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
                    dbl.sqlDate(this.dDate) + " and FPortCode = " + dbl.sqlString(this.portCode) +
                    //" and FGRadeType1 = '01' and FGradeType2 = '0101' and ( FGradeType5 like '06__' or FGradeType5 like '07__') " +
                    
                    //20130320 modified by liubo.Bug #7350，计算现金类应付合计项，应加上07TA_Fee应付赎回费
                    //+++++++++++++++++++++++++++++++++++++++++
                    " and FGRadeType1 = '01' and FGradeType2 = '0101' and ( FGradeType6 like '06__' or FGradeType6 like '07__' or FGradeType6 = '07TA_Fee') " +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
                    //++++++++++++++++++end+++++++++++++++++++++++
                    (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ") + " and FReTypeCode='Cash' ) na " + //获取现金类的所有应收应付数据，并按应收应付汇总
                    " group by FNavDate,FPortCode,FReTypeCode,FInOut,FGradeType1,FGradeType2,FGradeType3,FTsfTypeCode) a left join Tb_Base_Transfertype b on a.FTsfTypeCode=b.FTsfTypeCode";
                rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    navRep = new NavRepBean();
                    navRep.setDetail(1);
                    navRep.setInvMgrCode(invMgrCode.length() > 0 ? invMgrCode : "total");
                    navRep.setNavDate(rs.getDate("FNavDate"));
                    navRep.setPortCode(rs.getString("FPortCode"));
                    navRep.setGradeType1(rs.getString("FGradeType1"));
                    navRep.setGradeType2(rs.getString("FGradeType2"));
                    navRep.setGradeType3(rs.getString("FGradeType3"));
                    navRep.setGradeType4(rs.getString("FGradeType4"));
                    navRep.setGradeType4(rs.getString("FGradeType5"));//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
                    navRep.setReTypeCode(rs.getString("FReTypeCode"));
                    navRep.setInOut(rs.getInt("FInOut"));
                    navRep.setCuryCode(rs.getString("FCuryCode"));
                    navRep.setBookCost(rs.getDouble("FCost"));
                    navRep.setPortBookCost(rs.getDouble("FPortCost"));
                    navRep.setMarketValue(rs.getDouble("FMarketValue"));
                    navRep.setPortMarketValue(rs.getDouble("FPortMarketValue"));
                    navRep.setPayValue(rs.getDouble("FMVValue"));
                    navRep.setPortPayValue(rs.getDouble("FPortMVValue"));
                    navRep.setPortexchangeValue(rs.getDouble("FFXValue"));
                    //---#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05 ------------------
                    if (rs.getString("FCuryCode").equalsIgnoreCase("Total")) {
                        navRep.setCuryCode("汇总：");
                        navRep.setOrderKeyCode(rs.getString("FGradeType1") + "##" + rs.getString("FGradeType2") + "##" + rs.getString("FGradeType3")+"##" + rs.getString("FGradeType4"));
                        navRep.setKeyCode(rs.getString("FGradeType2") + "-" + rs.getString("FGradeType3")+ "-" + rs.getString("FTsFTypeCode"));
                        navRep.setKeyName(".       " + rs.getString("FTsFTypeName"));
                    } else {
                        navRep.setOrderKeyCode(rs.getString("FGradeType1") + "##" + rs.getString("FGradeType2") + "##" + rs.getString("FGradeType3") +"##" + rs.getString("FGradeType4")+rs.getString("FGradeType5"));
                        navRep.setKeyCode(rs.getString("FGradeType2") + "-" + rs.getString("FTsFTypeCode") + "-" + rs.getString("FGradeType3")+ "-" + rs.getString("FGradeType4"));
                        navRep.setKeyName(".       " + rs.getString("FTsFTypeName") + rs.getString("FCuryCode") + "-合计");
                    }
                    //#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05 ---end ----------------
                    alTotal.add(navRep);
                }
                rs.getStatement().close();
            } //end if cashTotal
            if (Total.equalsIgnoreCase("true")) {
            	//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05 
                sqlStr = "select na.*,b.FSubTsfTypeName from( select sum(FPortMarketValue) as FPortMarketValue,FNavDate,FPortCode,FInvMgrCode," +
                		//"FGradeType5 from " +
                
                //20130320 modified by liubo.Bug #7350
                //07TA_Fee应付赎回费需要统计进07TA的项中
                //==============================
//                          "FGradeType6 " +
                		  " substr(FGradeType6,0,4) as FGradeType6 " +
                          " from " +
               //=================end=============
                    pub.yssGetTableName("Tb_Data_NavData") +
                    " where FNavDate = " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(portCode) +
                    (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ") +
                    //" and (FGradeType5 like '06__' or FGradeType5 like '07__' and FReTypeCode='Cash') " + //取现金类所有应收应付数据
                    " and (FGradeType6 like '06__' or FGradeType6 like '07__'  or FGradeType6 = '07TA_Fee' and FReTypeCode='Cash') " + //取现金类所有应收应付数据
                    //" group by  FNavDate,FPortCode,FInvMgrCode,FGradeType5 " +
                    " group by  FNavDate,FPortCode,FInvMgrCode," +
//                    " FGradeType6 " +
                    //20130320 modified by liubo.Bug #7350
                    //07TA_Fee应付赎回费需要统计进07TA的项中
                    //==============================
                    " substr(FGradeType6,0,4) " +
                    //==============end================
                    "union select sum(FPortMarketValue) as FPortMarketValue,FNavDate,FPortCode,FInvMgrCode,FGradeType6 " +
                    " from " + pub.yssGetTableName("Tb_Data_NavData") + " where FNavDate = " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(portCode) + " " +
                    (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ") +
                    //20130320 modified by liubo.Bug #7350，计算现金类应付合计项，应加上07TA_Fee应付赎回费
                    //+++++++++++++++++++++++++++++++++++++++++
                    " and ( FGradeType6 like '06__' or FGradeType6 like '07__' and FReTypeCode='Security' and FGradeType1='FI' ) " + //取债券类的所有应收应付数据
                    //+++++++++++++++++++++end++++++++++++++++++++
                    " group by  FNavDate,FPortCode,FInvMgrCode,FGradeType6 )na left join Tb_Base_Subtransfertype b on na.FGradeType6=b.fsubtsftypecode ";
              
                rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    navRep = new NavRepBean();
                    navRep.setDetail(1);
                    navRep.setInvMgrCode(invMgrCode.length() > 0 ? invMgrCode : "total");
                    navRep.setNavDate(rs.getDate("FNavDate"));
                    navRep.setPortCode(rs.getString("FPortCode"));
                   // navRep.setKeyCode(rs.getString("FGradeType5"));
                    navRep.setKeyCode(rs.getString("FGradeType6"));
                    //navRep.setKeyName(rs.getString("FSubTsfTypeName") == null ? rs.getString("FGradeType5") : rs.getString("FSubTsfTypeName"));
                    navRep.setKeyName(rs.getString("FSubTsfTypeName") == null ? rs.getString("FGradeType6") : rs.getString("FSubTsfTypeName"));
                    navRep.setReTypeCode("subTotal");
                   // navRep.setOrderKeyCode("Total" + rs.getString("FGradeType5"));
                    navRep.setOrderKeyCode("Total" + rs.getString("FGradeType6"));
                    navRep.setCuryCode(" ");
                    navRep.setInOut(1);
                    navRep.setPortMarketValue(rs.getDouble("FPortMarketValue"));
                    alTotal.add(navRep);
                  //#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05 ---end ----------------
                }
                rs.getStatement().close();
            } //end Total
        } catch (Exception ex) {
            throw new YssException("汇总应收应付项数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alTotal;
    }

    /**
     * QDV4华夏2009年8月24日03_A MS00652 by leeyu 20090905
     * 日净值增长率=（今日单位净值-上一日单位净值+当日单位分红金额）/（上一日单位净值-当日单位分红金额）
     * 本期净值增长率=期末单位净值/期初单位净值-1
     * 累计净值增长率=（基金第一次分红前单位基金资产净值÷成立时单位基金资产净值）×（第二次分红前单位基金资产净值÷（第一次分红前单位基金资产净值-第一次分红单位分红金额）×…… ×（期末单位基金资产净值÷(最后一次分红前单位基金资产净值-最后一次分红单位分红金额）-1
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList getUnitScale() throws YssException {
        ArrayList alUnit = new ArrayList();
        String sqlStr = "";
        ResultSet rs = null;
        NavRepBean navRep = null;
        //本期净值增长率 MS00652   QDV4华夏2009年8月24日03_A
        int year = 0;
        String sql = "";
        java.util.Date startDate = new java.util.Date();
        ResultSet tmpRs = null;
        double QMDWJZ = 0;
        //期末单位净值
        double QCDWJZ = 0;
        //期初单位净值
        double dReturn = 0;
        //本期净值增长率
        try {
            //计算日净值增长率
        	//modify by wangzuochun  2010.07.05 MS01327    净值统计表，日净值增长率、累计净值增长率，计算不正确    QDV4赢时胜（上海）2010年6月21日01_B    
            sqlStr = "select distinct today.FIsInCode,round((round(today.FPrice,3)-round(yesterday.FPrice,3)+(case when dividend.FSellPrice is null then 0 else round(dividend.FSellPrice,3) end)),"+dbl.sqlToNumber("today.FIsInCode")+")/round((round(yesterday.FPrice,3)-(case when dividend.FSellPrice is null then 0 else round(dividend.FSellPrice,3) end)),"+dbl.sqlToNumber("today.FIsInCode")+")*100 as FUnitScale from " +
            //-----------------    MS01327	----------------//
            	" (select FPrice,FPortCode,FNavDate,FIsInCode from " + pub.yssGetTableName("Tb_Data_NavData") +
                " where FKeyCode='Unit' and FORderCode='Total3' and FCuryCode=' ' and FRetypeCode='Total' and FNavDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) + (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ") +
                " ) today left join " +
                " (select FPrice,FPortCode,FNavDate from " + pub.yssGetTableName("Tb_Data_NavData") +
                " where FKeyCode='Unit' and FORderCode='Total3' and FCuryCode=' ' and FRetypeCode='Total' and FNavDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                " and FPortCode = " + dbl.sqlString(portCode) + (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ") +
                " ) yesterday on today.FPortCode = yesterday.FPortCode left join " +
                " (select FSellPrice, FConfimDate,FPortCode from " + pub.yssGetTableName("TB_TA_TRADE") +
                " where FCheckState = 1 and FConfimDate =" + dbl.sqlDate(dDate) + " and FSellType = '03' " +
                " order by FConfimDate) dividend on today.FPortCode=dividend.FPortCode and today.FNavDate =dividend.FConfimDate ";
            rs = dbl.openResultSet(sqlStr); 
            if (rs != null && rs.next()) {
                navRep = new NavRepBean();
                navRep.setDetail(0);
                navRep.setInvMgrCode(invMgrCode.length() > 0 ? invMgrCode : "total");
                navRep.setNavDate(dDate);
                navRep.setPortCode(portCode);
                navRep.setReTypeCode("Total");
                navRep.setOrderKeyCode("Total3");
                navRep.setKeyCode("UnitDayScale");
                navRep.setIsinCode(rs.getString("FIsInCode"));
                navRep.setKeyName("日净值增长率：");
                navRep.setInOut(1);
                navRep.setCuryCode(" ");
                navRep.setPrice(YssFun.roundIt(rs.getDouble("FUnitScale"), 12));
                alUnit.add(navRep);
            }
            //------ add by wangzuochun 2010.11.25  BUG #483 资产估值后，净值统计表没有产生日净值增长率
            else{
            	//add by jsc 20120424
                if(rs!=null){
                    rs.getStatement().close();
                    dbl.closeResultSetFinal(rs);
                }
            	if (dayNavRep != null){
            		sqlStr = "select distinct today.FIsInCode,round((round(today.FPrice,3)-round(yesterday.FPrice,3)+(case when dividend.FSellPrice is null then 0 else round(dividend.FSellPrice,3) end)),"+dbl.sqlToNumber("today.FIsInCode")+")/round((round(yesterday.FPrice,3)-(case when dividend.FSellPrice is null then 0 else round(dividend.FSellPrice,3) end)),"+dbl.sqlToNumber("today.FIsInCode")+")*100 as FUnitScale from " +
	                	" (select " + dayNavRep.getPrice() + " as FPrice, " 
	                	+ dbl.sqlString(dayNavRep.getPortCode()) + " as FPortCode, " 
	                	+ " to_date(" + dbl.sqlString(YssFun.formatDate(dayNavRep.getNavDate()))+ ",'yyyy-MM-dd') as FNavDate," 
	                	+ dbl.sqlString(dayNavRep.getIsinCode()) + " as FIsInCode from " + pub.yssGetTableName("Tb_Data_NavData") +
	                    " ) today left join " +
	                    " (select FPrice,FPortCode,FNavDate from " + pub.yssGetTableName("Tb_Data_NavData") +
	                    " where FKeyCode='Unit' and FORderCode='Total3' and FCuryCode=' ' and FRetypeCode='Total' and FNavDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
	                    " and FPortCode = " + dbl.sqlString(portCode) + (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ") +
	                    " ) yesterday on today.FPortCode = yesterday.FPortCode left join " +
	                    " (select FSellPrice, FConfimDate,FPortCode from " + pub.yssGetTableName("TB_TA_TRADE") +
	                    " where FCheckState = 1 and FConfimDate =" + dbl.sqlDate(dDate) + " and FSellType = '03' " +
	                    " order by FConfimDate) dividend on today.FPortCode=dividend.FPortCode and today.FNavDate =dividend.FConfimDate ";
            		
            		rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            		if (rs.next()){
            			navRep = new NavRepBean();
    	                navRep.setDetail(0);
    	                navRep.setInvMgrCode(invMgrCode.length() > 0 ? invMgrCode : "total");
    	                navRep.setNavDate(dDate);
    	                navRep.setPortCode(portCode);
    	                navRep.setReTypeCode("Total");
    	                navRep.setOrderKeyCode("Total3");
    	                navRep.setKeyCode("UnitDayScale");
    	                navRep.setIsinCode(dayNavRep.getIsinCode());
    	                navRep.setKeyName("日净值增长率：");
    	                navRep.setInOut(1);
    	                navRep.setCuryCode(" ");
    	                navRep.setPrice(YssFun.roundIt(rs.getDouble("FUnitScale"), 12));
    	                alUnit.add(navRep);
            		}
            	}
            }
           //-------------------BUG #483 资产估值后，净值统计表没有产生日净值增长率-------------------//
            rs.getStatement().close();
            dbl.closeResultSetFinal(rs);
            //20110601 modified by liubo  STORY #936 
            //此处为旧的获取本期净值增长率的方法，因未确定是否一定采用新的方法，先保留旧的方法
            
            //本期净值增长率
//            year = YssFun.getYear(dDate);
//            sql = "select FInceptionDate from " +
//                pub.yssGetTableName("Tb_Para_PortFolio") +
//                " where FPortCode = " + dbl.sqlString(portCode);
//            rs = dbl.openResultSet(sql);
//            while (rs.next()) {
//                if (YssFun.getYear(rs.getDate("FInceptionDate")) == year) { //如果基金是今年成立的则取成立日期为期初日期
//                    startDate = rs.getDate("FInceptionDate"); //调用主要财务指标中的计算本期基金净值增长率的方法时传入的期初日期会减去一天，是取期初前一天的数据计算的
//                } else { // 如果基金不是今年成立的则取去年的最后一天的日期为期初日期
//                    sql = "select max(fnavdate) as fnavdate from " +
//                        pub.yssGetTableName("Tb_Data_NavData") +
//                        //" where " + dbl.sqlYear("fnavdate") + " = " + (year - 1);
//                        " where fnavdate between "+dbl.sqlDate((year - 1)+"-01-01")+" and "+dbl.sqlDate((year - 1)+"-12-31")+
//                        " and FPortCode = " + dbl.sqlString(portCode); //太平资产版本性能调整  2010.08.25   add by jiangshichao
//                    dbl.closeResultSetFinal(tmpRs);
//                    tmpRs = dbl.openResultSet(sql);
//                    while (tmpRs.next()) {
//                        startDate = tmpRs.getDate("fnavdate"); //调用主要财务指标中的计算本期基金净值增长率的方法时传入的期初日期会减去一天，是取期初前一天的数据计算的
//                    } //这里传入的期初日期其实就是要取数据的日期，故要加上一天带入计算
//                    dbl.closeResultSetFinal(tmpRs);
//                }
//            }
//            dbl.closeResultSetFinal(rs);
//            if(startDate==null){//对startDate进行null判断，因为测试过程中发现有组合查询出的数据为空，合并太平版本调整 by leeyu 20100825
//            	QCDWJZ=0;
//            }else{
//            	QCDWJZ = getUnitValue(startDate);
//            }
//            if (QCDWJZ == 0) { //期初为了0改为1除数不能为0
//                QCDWJZ = 1;
//            }
//            QMDWJZ = getUnitValue(dDate);
//            //add by zhangfa 20101230 BUG #657 资产估值后，查询净值表，累计净值增长率和本期净值增长率为-100%
//            if(YssD.div(QMDWJZ, QCDWJZ)==0){
//            	dReturn=0;
//            }else{
//            	 dReturn = YssD.sub(YssD.div(QMDWJZ, QCDWJZ), 1)*100; //20090921 fanghaoln 加上%号之前先乘以100
//            }
            //------------------end 20101230------------------------------------------------------------
            double AmountRate = getAmountRate(dDate, portCode);   //20110601 modified by liubo  STORY #936 调用新的方法获取本期净值增长率
            navRep = new NavRepBean();
            navRep.setDetail(0);
            navRep.setInvMgrCode(invMgrCode.length() > 0 ? invMgrCode : "total");
            navRep.setNavDate(dDate);
            navRep.setPortCode(portCode);
            navRep.setReTypeCode("Total");
            navRep.setOrderKeyCode("Total3");
            navRep.setKeyCode("UnitStageScale");
            navRep.setIsinCode("3");
            navRep.setKeyName("本期净值增长率：");
            navRep.setInOut(1);
            navRep.setCuryCode(" ");
            navRep.setPrice(YssFun.roundIt(AmountRate, 12));
            alUnit.add(navRep);
            //累计净值增长率
            //MS00652   QDV4华夏2009年8月24日03_A   显示累计净值增长率 fanghaoln
            double amtRate = getAllAmountRate(dDate, portCode)*100; //得到累计净值增长率//20090921 fanghaoln 加上%号之前先乘以100
            navRep = new NavRepBean();
            navRep.setDetail(0);
            navRep.setInvMgrCode(invMgrCode.length() > 0 ? invMgrCode : "total");
            navRep.setNavDate(dDate);
            navRep.setPortCode(portCode);
            navRep.setReTypeCode("Total");
            navRep.setOrderKeyCode("Total3");
            navRep.setKeyCode("UnitPileScale");
            navRep.setIsinCode("3");
            navRep.setKeyName("累计净值增长率：");
            navRep.setInOut(1);
            navRep.setCuryCode(" ");
            navRep.setPrice(YssFun.roundIt(amtRate, 12));
            alUnit.add(navRep);
            //累计净值增长率
        } catch (Exception ex) {
        	dbl.closeResultSetFinal(rs);//modified by  jsc 解决连接池报错问题
            throw new YssException("计算净值增长率出错", ex);
        } finally {
            
        }
        return alUnit;
    }

    /**
     * 本期净值增长率 MS00652   QDV4华夏2009年8月24日03_A
     * 这个是获取单位净值
     * fanghaoln
     * @param
     * @throws YssException
     * @return String
     */
    protected double getUnitValue(java.util.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = "select round(FPrice,"+dbl.sqlToNumber("FIsInCode")+")as FPrice from " + pub.yssGetTableName("Tb_Data_NavData") +
                " where FKeyCode='Unit' and FORderCode='Total3' and FCuryCode=' ' and FRetypeCode='Total' and FNavDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) ;
//                + (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ");
            rs = dbl.queryByPreparedStatement(strSql); //获取单位净值 //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                dReturn = rs.getDouble("FPrice");
            }
            rs.close();
            return dReturn; //返回单位净值
        } catch (Exception e) {
            throw new YssException("获取期末单位净值： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 本期净值增长率
     * author liubo edittime:20110601
     * @return double 返回本期净值增长率
     * @throws YssException
     * MS00652   STORY #936 
     */
    
    //修改后的本期增长率算法为：本期净值增长率=（本期第一次分红前单位基金资产净值÷期初单位基金资产净值）×（本期第二次分红前单位基金资产净值÷本期第一次分红后单位基金资产净值）×…… ×（期末单位基金资产净值÷本期最后一次分红后单位基金资产净值）-1
    //若期内未进行分红，公式可简化为（期末÷期初单位净值）－１
    private double getAmountRate(java.util.Date dDate, String portCode) throws YssException {
        ArrayList accummulate =new ArrayList();
        ArrayList yesdayRate = new ArrayList();
        int year = 0;
        String sql = "";
        String holiday = "";
        java.util.Date startDate = new java.util.Date(); //存储期初日期
        int i = 0;
        String sqlStr = null;
        ResultSet tmpRs = null;
        ResultSet rsRateDate = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        java.util.Date RateDate;
        double firstRate = 1.0; //保存第一次计算
        double centerRate = 1.0; //保存从第二次到第倒数第二次
        double lastRate = 1.0; //保存最后一次计算
        double lastDateRate = 0.0; //期末单位基金资产净值
        try {
        	
        	//+++++++++++++++++++++++++++++++++++++++
        	//20110808 modified by liubo.Bug 2334
        	//若为期内成立基金，则期初单位基金资产净值为基金成立日的单位净值，其中基金成立日指该组合TA交易数据中销售类型为“00”（基金成立）对应的交易日期。
        	//**********************************
        	year = YssFun.getYear(dDate);
        	sql = "select FTradeDate from "
				+ pub.yssGetTableName("Tb_TA_Trade")
				+ " where FPortCode = " + dbl.sqlString(portCode) 
				+ " and FSellType = '00'" 
				+ " and FCheckState = '1'";
            rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (YssFun.getYear(rs.getDate("FTradeDate")) == year) { //如果基金是今年成立的则取成立日期为期初日期
                    startDate = rs.getDate("FTradeDate");
           //*****************end****************
                } else { // 如果基金不是今年成立的则取去年的最后一天的日期为期初日期
                    sql = "select max(fnavdate) as fnavdate from " +
                        pub.yssGetTableName("Tb_Data_NavData") +
                        //" where " + dbl.sqlYear("fnavdate") + " = " + (year - 1);
                        " where fnavdate between "+dbl.sqlDate((year - 1)+"-01-01")+" and "+dbl.sqlDate((year - 1)+"-12-31")+
                        " and FPortCode = " + dbl.sqlString(portCode); //太平资产版本性能调整  2010.08.25   add by jiangshichao
                    dbl.closeResultSetFinal(tmpRs);
                    tmpRs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
                    while (tmpRs.next()) {
                        startDate = tmpRs.getDate("fnavdate"); //调用主要财务指标中的计算本期基金净值增长率的方法时传入的期初日期会减去一天，是取期初前一天的数据计算的
                    } //这里传入的期初日期其实就是要取数据的日期，故要加上一天带入计算
                    dbl.closeResultSetFinal(tmpRs);
                }
            }
            dbl.closeResultSetFinal(rs);
            
            //add by huangqirong 2013-03-04 bug #7117
            if(startDate == null ){
            	startDate = new java.util.Date(); //存储期初日期
            }
            //---end---
            //++++++++++++++++++++++++++++++++++++++++++++++++
            sqlStr="select fholidayscode,fportcode from " +
			     pub.yssGetTableName("Tb_TA_CashSettle ")+
			     "where fcheckstate=1  and FSellTypeCode = '03' and (fportcode = ' ' or fportcode = " + dbl.sqlString(portCode) + ")";
            rsRateDate = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
    		while(rsRateDate.next())
    		{
    			if(rsRateDate.getString("fportcode").length() > 0){
    				holiday=rsRateDate.getString("fholidayscode");
    				break;
    			}
    			holiday=rsRateDate.getString("fholidayscode");
    		}
    		if (holiday.equals(""))
    		{
    			throw new YssException("获取节假日群错误！请在“TA业务模块“>>”TA现金结算链接设置”中为分红交易类型设置对应节假日！");
    		}
            //期末单位基金资产净值
            sqlStr = "select round(FPrice,"+dbl.sqlToNumber("FIsInCode")+")as FPrice from " + pub.yssGetTableName("Tb_Data_NavData") +
                " where FKeyCode='Unit' and FORderCode='Total3' and FCuryCode=' ' and FRetypeCode='Total' and FNavDate <= " + dbl.sqlDate(dDate) +
                " and FNavDate >=" + dbl.sqlDate(startDate) +
                " and FPortCode = " + dbl.sqlString(portCode) + (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " "); //期末单位基金资产净值
            rs1 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs1.next()) {
                lastDateRate = rs1.getDouble("FPrice"); //期末单位基金资产净值
            }
            
            dbl.closeResultSetFinal(rs1);
            
            BaseOperDeal Bdeal = new BaseOperDeal();
            Bdeal.setYssPub(pub);
            //========================================================================================================================
            //得到期内的分红数据
            sqlStr = "select FSellPrice as FAccumulateDivided,FConfimDate from " + pub.yssGetTableName("TB_TA_TRADE") +
                " where FCheckState = 1 and FConfimDate <=" + dbl.sqlDate(dDate) + "  and FConfimDate >= " + dbl.sqlDate(startDate) +" and FSellType = '03' " +
                " and FPortCode = " + dbl.sqlString(portCode) + " order by FConfimDate"; //获取累计分红价格，数据为小于等于当前日期
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                accummulate.add(new Double(rs.getDouble("FAccumulateDivided"))); //获取第N次分红的金额。
                RateDate = Bdeal.getWorkDay(holiday, rs.getDate("FConfimDate"),-1);
                //得到分红前的的单位净值
                sqlStr = "select round(FPrice,"+dbl.sqlToNumber("FIsInCode")+")as FPrice from " + pub.yssGetTableName("Tb_Data_NavData") +
                    " where FKeyCode='Unit' and FORderCode='Total3' and FCuryCode=' ' and FRetypeCode='Total' and FNavDate = " + dbl.sqlDate(RateDate) +
                    " and FPortCode = " + dbl.sqlString(portCode) + (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " ");
                rs2 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                if (rs2.next()) {
                    yesdayRate.add(new Double(rs2.getDouble("FPrice"))); //获取分红前的的单位净值
                    i++;
                }
                dbl.closeResultSetFinal(rs2);
                //===================================================================================
            }
            dbl.closeResultSetFinal(rs);
            if (i >= 1) {
            	//modify by jiangshichao 2011.07.28 
            	double douStartUnitValue = getUnitValue(startDate);
                firstRate =((Double)yesdayRate.get(0)).doubleValue()/(douStartUnitValue == 0 ? 1:douStartUnitValue); //保存第一次计算
                for (int j = 1; j < i; j++) {
                    centerRate = ((Double)yesdayRate.get(j)).doubleValue()/ (((Double)yesdayRate.get(j - 1)).doubleValue() - ((Double)accummulate.get(j - 1)).doubleValue()) * centerRate; //从第二次到最后一次的前一次
                }
                lastRate = lastDateRate /(((Double)yesdayRate.get(i - 1)).doubleValue() - ((Double)accummulate.get(i - 1)).doubleValue()); //保存最后一次计算
                firstRate = (firstRate * centerRate * lastRate - 1) * 100; //把第一到最后乘起来就得到了本期净值增长率
            } 
            else
            {
            	double douStartUnitValue = getUnitValue(startDate);
            	
                firstRate = (getUnitValue(dDate) / (douStartUnitValue == 0 ? getUnitCostByCury():douStartUnitValue) - 1) * 100 ;
            }//期内无分红数据，公式可简化为（期末÷期初单位净值）－１
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsRateDate);
        }
       //add by zhangfa 20101230 BUG #657 资产估值后，查询净值表，累计净值增长率和本期净值增长率为-100%
        if(lastDateRate==0){
        	firstRate=0;
        }
        //--------------end 20101230---------------------------------------------------------------
        return firstRate;
    }

    /**
     * 累计净值增长率
     * author fanghaoln edittime:20090904
     * @return double 返回累计分红数据
     * @throws YssException
     * MS00652   QDV4华夏2009年8月24日03_A
     */
    private double getAllAmountRate(java.util.Date dDate, String portCode) throws YssException {
        ArrayList accummulate =new ArrayList();
        ArrayList yesdayRate = new ArrayList();
        int i = 0;
        String sqlStr = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        java.util.Date RateDate;
        double firstRate = 1.0; //保存第一次计算
        double centerRate = 1.0; //保存从第二次到第倒数第二次
        double lastRate = 1.0; //保存最后一次计算
        double lastDateRate = 0.0; //期末单位基金资产净值
        try {
            //期末单位基金资产净值
            sqlStr = "select round(FPrice,"+dbl.sqlToNumber("FIsInCode")+")as FPrice from " + pub.yssGetTableName("Tb_Data_NavData") +
                " where FKeyCode='Unit' and FORderCode='Total3' and FCuryCode=' ' and FRetypeCode='Total' and FNavDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) + (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " "); //期末单位基金资产净值
            rs1 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs1.next()) {
                lastDateRate = rs1.getDouble("FPrice"); //期末单位基金资产净值
            }
            dbl.closeResultSetFinal(rs1);
            //========================================================================================================================
            //得到当天的累计分红
            sqlStr = "select FSellPrice as FAccumulateDivided,FConfimDate from " + pub.yssGetTableName("TB_TA_TRADE") +
                " where FCheckState = 1 and FConfimDate <=" + dbl.sqlDate(dDate) + " and FSellType = '03' " +
                " and FPortCode = " + dbl.sqlString(portCode) + " order by FConfimDate"; //获取累计分红价格，数据为小于等于当前日期
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                accummulate.add(new Double(rs.getDouble("FAccumulateDivided"))); //获取第N次分红的金额。
                RateDate = rs.getDate("FConfimDate");
                //得到分红前的的单位净值
                sqlStr = "select round(FPrice,"+dbl.sqlToNumber("FIsInCode")+")as FPrice from " + pub.yssGetTableName("Tb_Data_NavData") +
                    " where FKeyCode='Unit' and FORderCode='Total3' and FCuryCode=' ' and FRetypeCode='Total' and FNavDate = " + dbl.sqlDate(YssFun.addDay(RateDate, -1)) +
                    " and FPortCode = " + dbl.sqlString(portCode) + (this.invMgrCode.length() > 0 ? " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) : " "); //获取单位净值
                rs2 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                if (rs2.next()) {
                    yesdayRate.add(new Double(rs2.getDouble("FPrice"))); //获取分红前的的单位净值
                    i++;
                }
                dbl.closeResultSetFinal(rs2);
                //===================================================================================
            }
            dbl.closeResultSetFinal(rs);
            if (i >= 1) {
                firstRate =((Double)yesdayRate.get(0)).doubleValue(); //保存第一次计算
                for (int j = 1; j < i; j++) {
                    centerRate = ((Double)yesdayRate.get(j)).doubleValue()/ (((Double)yesdayRate.get(j - 1)).doubleValue() - ((Double)accummulate.get(j - 1)).doubleValue()) * centerRate; //从第二次到最后一次的前一次
                }
                lastRate = lastDateRate /(((Double)yesdayRate.get(i - 1)).doubleValue() - ((Double)accummulate.get(i - 1)).doubleValue()); //保存最后一次计算
                firstRate = firstRate * centerRate * lastRate - getUnitCostByCury(); //把第一到最后乘起来就得到了累计净值增长率
            } else
                firstRate = lastDateRate - getUnitCostByCury(); //没有分红计处累计净值增长率
        } catch (Exception e) {
            throw new YssException("获取累计分红数据出现异常！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
       //add by zhangfa 20101230 BUG #657 资产估值后，查询净值表，累计净值增长率和本期净值增长率为-100%
        if(lastDateRate==0){
        	firstRate=0;
        }
        //--------------end 20101230---------------------------------------------------------------
//        return firstRate;
        return YssFun.roundIt(firstRate, 12);
    }
    
    //获取股指期货浮动盈亏合计数据  add by yanghaiming 20100304 MS00860 QDV4华夏2009年12月11日01_A 
    private NavRepBean getTotalFU() throws YssException {
    	String sqlStr = "";
        ResultSet rs = null;
        NavRepBean navRep = null;
        try {
            sqlStr = " select sum(b.FPORTMVVALUE) as FPORTMVVALUE,'Total3' as FOrderCode,'TOTALFU' as FKeyCode ," +
                " '期货浮动盈亏：' as FKeyName, 1 as FDetail from " + //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A 
                this.tempViewName + 
                " a left join (select fnum,sum(c.fmoney) as FMVValue,sum(c.FPortCuryMoney) as FPortMVValue from " +
                pub.yssGetTableName("tb_data_futtraderela") +
                " c where c.ftsftypecode in ('09FU01', '09FU02', '09FU03','09FU04') and c.ftransdate = " + //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A, modify by fangjiang 2011.02.15 STORY #462	//modify huangqirong 2012-08-21  商品期货
                dbl.sqlDate(this.dDate) + " and c.fportcode = " +
                dbl.sqlString(this.portCode) + " group by fnum) b on a.fkeycode = b.FNUM " +
                " where FNAVDate = " +
                dbl.sqlDate(this.dDate) + " and FPortCode = " +
                dbl.sqlString(this.portCode) +
                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode) +
                " and FDetail = 0" +
                " and fgradetype2 in ('09FU01', '09FU02', '09FU03','09FU04') and FRETYPECODE = 'Security'"; //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A, modify by fangjiang 2011.02.15 STORY #462	//modify huangqirong 2012-08-21  商品期货
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getDouble("FPortMVValue") == 0) { //当数值为0时，跳过。
                    return null;
                }
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total");
                navRep.setCuryCode(" ");
                navRep.setPortMarketValue(rs.getDouble("FPortMVValue"));
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
            }
            return navRep;
        } catch (Exception e) {
            throw new YssException("获取估值增值信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
  //add by yanghaiming 20100505 B股业务，增加单币种获取资产净值
    private NavRepBean getTotalValue(String currencyCode) throws YssException {
        NavRepBean navRep = null;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select sum(case when FInOut = 1 then FPortMarketValue else FPortMarketValue*-1 end) as FPortMarketValue, sum(case when FInOut = 1 then FMarketValue else FMarketValue*-1 end) as FMarketValue, 1 as FDetail,'' as FDetailOrSum,'资产净值' as FKeyName " +
                ", 'TotalValue' as FKeyCode , 'Total1' as FOrderCode from " +
                tempViewName + " where FNAVDate = " +
                dbl.sqlDate(this.dDate) + " and FPortCode = " +
                dbl.sqlString(this.portCode) +
                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode.length() > 0 ? this.invMgrCode : "total") +
                " and FDetail = 0 and FReTypeCode <> 'Total' and FCURYCODE = " + dbl.sqlString(currencyCode);
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                if (rs.getDouble("FPortMarketValue") == 0) {
                    return null;
                }
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total-" + currencyCode);
                navRep.setCuryCode(currencyCode);
                navRep.setPortMarketValue(YssFun.roundIt(rs.getDouble("FPortMarketValue"),2));//最终结果保留2位小数  edit by yanghaiming 
                navRep.setMarketValue(YssFun.roundIt(rs.getDouble("FMarketValue"),2));//原币资产净值
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
            }
            return navRep;
        } catch (Exception e) {
            throw new YssException("获取净值信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //add by yanghaiming 20100505 B股业务，增加单币种获取估值增值
    private NavRepBean getTotalMV(String currencyCode) throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        NavRepBean navRep = null;
        try {
            sqlStr = " select sum(FPortMVValue) as FPortMVValue, sum(FMVValue) as FMVValue, 'Total3' as FOrderCode, 'MV' as FKeyCode ," +
                " '估值增值' as FKeyName, 1 as FDetail from " +
                this.tempViewName + 
                " where FNAVDate = " +
                dbl.sqlDate(this.dDate) + " and FPortCode = " +
                dbl.sqlString(this.portCode) +
                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode.length() > 0 ? this.invMgrCode : "total") +
                " and FDetail = 0 and FReTypeCode <> 'Total' and FCURYCODE = " + dbl.sqlString(currencyCode);
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getDouble("FPortMVValue") == 0) {
                    return null;
                }
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total-" + currencyCode);
                navRep.setCuryCode(currencyCode);
                navRep.setPortMarketValue(YssFun.roundIt(rs.getDouble("FPortMVValue"),2));//最终结果保留2位小数  edit by yanghaiming 
                navRep.setMarketValue(YssFun.roundIt(rs.getDouble("FMVValue"),2));//原币的估值增值
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }

            }

            return navRep;
        } catch (Exception e) {
            throw new YssException("获取估值增值信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //add by yanghaiming 20100505 B股业务，增加单币种获取汇兑损益
    private NavRepBean getTotalFX(String currencyCode) throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        NavRepBean navRep = null;
        try {
            sqlStr = " select sum(" +
                dbl.sqlIsNull("FFXValue", "0") + ") as FFXValue" +
                ", 'Total4' as FOrderCode, 'FX' as FKeyCode,'汇兑损益' as FKeyName,1 as FDetail " +
                " from " +
                " (select sum(" +
                dbl.sqlIsNull("FFXValue", "0") + ") as FFXValue " +
                " from " + this.tempViewName +
                " where FNAVDate =  " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode.length() > 0 ? this.invMgrCode : "total") +
                " and FReTypeCode = 'Security' and  " +
                " FGradeType6 in ('06RE', '06FI') " +
                " union all " + 
                " select sum(" +
                dbl.sqlIsNull("FFXValue", "0") + ") as FFXValue " +
                " from " + this.tempViewName +
                " where FNAVDate =  " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode.length() > 0 ? this.invMgrCode : "total") +
                " and FReTypeCode = 'Cash' and  " +
                //" FGradeType5 = '06DE' " +
                " FGradeType6 = '06DE' " +//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05 
                " union all " + 
                " select sum(FFXValue) as FFXValue " +
                " from " + this.tempViewName +
                " where FNAVDate = " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FInvMgrCode = " + dbl.sqlString(this.invMgrCode.length() > 0 ? this.invMgrCode : "total") +
                " and FRETYPECODE like '%-" + currencyCode + "'" +
                " and FGradeType1 is not null and " +
                " (FGradeType2 is null or FGradeType2 = ' ') and " +
                " (FGradeType3 is null or FGradeType3 = ' ') and " +
                " (FGradeType4 is null or FGradeType4 = ' ') and " +
                " (FGradeType5 is null or FGradeType5 = ' ') and " +
                " (FGradeType6 is null or FGradeType6 = ' ') and FKeyCode <> ' ') FX ";
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getDouble("FFXValue") == 0) { 
                    return null;
                }
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                navRep.setKeyCode(rs.getString("FKeyCode"));
                navRep.setKeyName(rs.getString("FKeyName"));
                navRep.setDetail(rs.getDouble("FDetail")); //汇总
                navRep.setReTypeCode("Total-" + currencyCode);
                navRep.setCuryCode(currencyCode);
                navRep.setPortMarketValue(YssFun.roundIt(rs.getDouble("FFXValue"),2));//最终结果保留2位小数  edit by yanghaiming 
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }

            }

            return navRep;
        } catch (Exception e) {
            throw new YssException("获取汇兑损益信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
	//add by fangjiang 2011.11.08 story 1589
	public double getUnitCostByCury() throws YssException {
		ResultSet rs = null;
		String strSql = "";
		double result = 1.0;	
		try {
			strSql = " select FDegreeCost from " + pub.yssGetTableName("Tb_TA_ClassFundDegree")
					 + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portCode)
					 + " and FCuryCode = ' ' and FPORTCLSCODE = ' '" 
					 //20120618 added by liubo.Bug #4822
					 //=================================
					 + " and FStartDate in (select max(FStartDate) from " + pub.yssGetTableName("Tb_TA_ClassFundDegree") 
					 + " where FStartDate <= " + dbl.sqlDate(dDate) + ")";
			 		 //=====================end============
			rs = dbl.openResultSet(strSql);			
			while (rs.next()) {				
				result = rs.getDouble("FDegreeCost");
			}							
		}
		catch(Exception e) {
			throw new YssException("查询份额成本出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
	}
	
	private String getPortCuryCode() throws YssException {
		ResultSet rs = null;
		String strSql = "";
		String result = "";	
		try {
			strSql = " select FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio")
					 + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portCode);
			rs = dbl.openResultSet(strSql);			
			while (rs.next()) {				
				result = rs.getString("FPortCury");
			}							
		}
		catch(Exception e) {
			throw new YssException("查询份额成本出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
	}
	
	private double getFportcuryMoney(Date time, String tsftypeCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		double money = 0;
		double basecurymoney = 0;
		double portcurymoney = 0;
		try {
			strSql = "select sum(FMONEY) as money , sum(FBASECURYMONEY) as basecurymoney, sum(FPORTCURYMONEY) as portcurymoney from "+
				pub.yssGetTableName("tb_data_futtraderela") +
				" where FTSFTYPECODE = '" + tsftypeCode +
				"' and FPortCode = " + dbl.sqlString(this.portCode) + " and FTRANSDATE = " + dbl.sqlDate(time);
			rs = dbl.openResultSet(strSql);
	        while (rs.next()) {
	        	money = rs.getDouble("money");
	        	basecurymoney = rs.getDouble("basecurymoney");
	        	portcurymoney = rs.getDouble("portcurymoney");
	        }
		}catch (SQLException e) {
			throw new YssException("获取未实现损益平准金报错", e);
		}finally {
            dbl.closeResultSetFinal(rs);
        }
		return portcurymoney;
	}
}
