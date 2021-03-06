package com.yss.main.operdeal.opermanage;

import java.util.*;

import com.yss.util.*;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.manager.*;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.main.parasetting.MTVMethodBean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.management.Query;

/**
 * <p>Title: 资本利得税的业务处理</p>
 *
 * <p>Description: 处理资本利得税业务的类</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: Ysstech </p>
 *
 * @author fangjiang
 * @version 1.0
 * @version  2.0   添加移动加权处理方式   modify by jsc
 * STORY #845
 */

public class CapitalGainsTax extends BaseOperManage {

	
	SecRecPayAdmin secRecPayAdmin = null;
	CashTransAdmin tranAdmin = null;
	CashPayRecAdmin cashRecPayAdmin = null;
	
	public void initOperManageInfo(Date dDate, String portCode) throws YssException {
	    this.dDate = dDate;
	    this.sPortCode = portCode;
	}
		
	public void doOpertion() throws YssException {
		BaseStgStatDeal secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecurityStorage");
        secstgstat.setYssPub(pub);
        secstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(sPortCode));
		
        CGT_BizProcessor();//处理资本利得税
    }
	
	/*********************************************************************************
	 *  add by jiangshichao 20120301  
	 *   资本利得税处理步骤
	 *    1  如果证券有库存
	 *        1.1   生成未实现利得税   (估值增值 * 税率)
	 *    2  如果有证券卖出
	 *        2.1  生成冲减由未实现转换成已实现的利得税数据   (结转)
	 *        2.2  根据结转方式生成已实现利得税   (收益 * 税率)
	 *   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 *    a. 根据成本计算方式来选择由未实现转换成已实现资本利得税生成03冲减数据的方法
	 *           先入先出；
	 *           移动加权；
	 *    b. 根据已实现资本利得税结转方式来选择处理方式
	 *           已实现利得税挂应付(现金应收应付库存)
	 *           已实现利得资金结转，生成资金调拨
	 * @throws YssException
	 */
	private void CGT_BizProcessor()throws YssException{
		
		CtlPubPara para = new CtlPubPara ();
		para.setYssPub(pub);
		try{
			if( para. getCalculatCost(sPortCode)){
				//2. 成本计算方式是先入先出方式
				dealByFiFo();
			}else{
				//1. 成本计算方式是移动加权方式
				 dealByAvg() ;
			}
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}
	}
	
	
	/***************************************************
	 * 以先入先出的处理方式处理资本利得税
	 * @throws YssException
	 */
	private void dealByFiFo()throws YssException{
		HashMap map = new HashMap();
		ArrayList list = new ArrayList();
		map = getTradeData();    //获取交易方式为买入、送股，且未被全部卖完的交易数据，存到HashMap中
		getSellCGT(map, list);   //卖出资本利得税
		getAppCGT(list);         //估值增值资本利得税
		insertCGTData(list);     //插入资本利得税数据
		createSecRecPay();       //生成证券应收应付
		createCashTrans();       //生成资金调拨		
		
		//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
		//当日产生数据，则认为有业务。
		if(list.size()==0){
			this.sMsg="        当日无业务";
		}
		
	}
	
	/*******************************************************
	 * 以 移动加权方式处理资本利得税
	 * @throws YssException
	 */
	private void dealByAvg() throws YssException{
		
		 Connection conn = dbl.loadConnection();
		 boolean bTrans = false;
		secRecPayAdmin = new SecRecPayAdmin();
		tranAdmin  =  new CashTransAdmin();
		cashRecPayAdmin =  new CashPayRecAdmin();
		secRecPayAdmin.setYssPub(pub);
		tranAdmin.setYssPub(pub);
		cashRecPayAdmin.setYssPub(pub);
		CtlPubPara para = new CtlPubPara ();
		para.setYssPub(pub);
		try{
			//1.处理未实现资本利得税
			 dealUnRealizedCGT();
			//2.  处理已实现资本利得税
			dealRealizedCGT();
			 conn.setAutoCommit(false);
	          bTrans = true;
			 secRecPayAdmin.insert(this.dDate, "07", "07CGT_EQ,07CGT_FI,07CGT_TR", this.sPortCode, "", "", "", "", 0);
			 // 根据已实现利得税结转方式来生成相应的数据
			   if(para.getCgtCarryWay(sPortCode)){
				   //结转，产生资金调拨
				   tranAdmin.insert(null,this.dDate, "07", "07CGT_EQ,07CGT_FI,07CGT_TR", "", 1, "");
			   }else{
				   //不结转，产生应付数据挂现金应收应付。
				   cashRecPayAdmin.insert(this.dDate, "07", "07CGT_EQ,07CGT_FI,07CGT_TR", this.sPortCode, "", "", "", "", 0);
			   }
			   
			 //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
	    		//当日产生数据，则认为有业务。
	    		if(secRecPayAdmin.getList().size()==0&&tranAdmin.getAddList().size()==0&&cashRecPayAdmin.getList().size()==0){
	    			this.sMsg="        当日无业务";
	    		}
			   conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException();
		}finally{
			  dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/***********************************************************
	 *  add by jsc 20120301
	 *   处理未实现资本利得税
	 * @throws YssException
	 */
	private void dealUnRealizedCGT() throws YssException{
		
		ResultSet rs  =  null;
		StringBuffer queryBuf  =  new StringBuffer();
		ArrayList alMethods = getValuationMethods();    
	  	 MTVMethodBean vMethod = null;
	  	 String sCateCode="EQ,FI,TR";  //要进行计提资本利得税的品种
		try{
			/*****************************************************************************************
			 *  每日计提的未实现资本利得税 ： （ 当日库存数量  *  行情  - 成本 ）* 税率 - 昨日未实现利得税余额 
			 *    备注：
			 *       1. 要获取当日库存数量，所以在做计提资本利得税时需要先统计证券库存。
			 *       2. 行情获取当天行情，如果当天没有行情，就获取最近日期的行情 
			 */
			  for(int i = 0; i < alMethods.size(); i++) {
	  			   vMethod = (MTVMethodBean) alMethods.get(i);		
			queryBuf.append(" select a.FSECURITYCODE  as FSECURITYCODE,sec.fsubcatcode,sec.fexchangecode,a.fstoragecost  as FCost");
			queryBuf.append(" ,a.fstorageamount as FAmount,c.FCsMarketPrice as FCsMarketPrice,a.fcurycode,sec.fcatcode,nvl(d.FBAL,0) as FBAL,a.FATTRCLSCODE ");
	  		queryBuf.append(" from ");	   
			//~证券库存表
			queryBuf.append(" (select FSECURITYCODE,fcurycode,fstorageamount,fstoragecost,FATTRCLSCODE  ");
			queryBuf.append(" from ").append(pub.yssGetTableName("tb_stock_security"));
			queryBuf.append(" sto  where fstoragedate = ").append( dbl.sqlDate(dDate)).append(" and fcheckstate=1");
			queryBuf.append(" and fportcode=").append( dbl.sqlString(this.sPortCode)).append(" )a");
			//~证券信息表
			queryBuf.append("  join  ");
			queryBuf.append("  (select fsecuritycode,fcatcode,FSUBCATCODE,FEXCHANGECODE from ").append(pub.yssGetTableName("tb_para_security"));
			queryBuf.append("   where fcheckstate=1 and fcatcode in(").append(operSql.sqlCodes(sCateCode)).append("))sec ") ;
			queryBuf.append(" on a.FSECURITYCODE= sec.FSECURITYCODE");
			//~ 估值方式设置表
			queryBuf.append(" join ");
			queryBuf.append(" (select FLinkCode ");
			queryBuf.append(" from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
			queryBuf.append(" where FCheckState = 1 and FMtvCode = ").append(dbl.sqlString(vMethod.getMTVCode())).append(")b");
			queryBuf.append(" on a.Fsecuritycode =  b.FLinkCode");
			//~ 行情表
			queryBuf.append(" left join ");
			queryBuf.append(" (select   ").append( vMethod.getMktPriceCode()).append(" as FCsMarketPrice,m1.FSecurityCode,m1.FMktValueDate");
			queryBuf.append(" from ").append(" (select  *   from ").append(pub.yssGetTableName("Tb_Data_MarketValue") ).append(" )m1");
			queryBuf.append("  join ");
			queryBuf.append(" (select max(FMktValueDate) as FMktValueDate, FSecurityCode,FMktSrcCode  from  ");
			queryBuf.append(pub.yssGetTableName("Tb_Data_MarketValue"));
			queryBuf.append("  where FCheckState = 1 and FMktValueDate <=").append(dbl.sqlDate(dDate)).append(" group by FSecurityCode, FMktSrcCode) m2 ");
			queryBuf.append(" on m1.FMKtvalueDate =m2.FMktValueDate and m1.FSecuritycode = m2.FSecurityCode");
			queryBuf.append(" and m1.FMktSrcCode =m2.FMktSrcCode) c on a.FSecurityCode = c.FSecurityCode ");
			//~证券应收应付库存
			queryBuf.append(" left join ");//edit by zhouwei 20120322 left join 基于的前提是证券卖空时，有库存记录，使资本利得税的证券应收应付库存能够冲减成0
			queryBuf.append(" (select   FBAL,fsecuritycode  from  ");
			queryBuf.append( pub.yssGetTableName("Tb_Stock_Secrecpay"));
			queryBuf.append(" where FCheckState =1 and fsubtsftypecode  like '07CGT%' and ftsftypecode='07'  and fstoragedate = ");
			queryBuf.append( dbl.sqlDate(YssFun.addDay(dDate, -1)));
			queryBuf.append(" and FPORTCODE =  ").append(dbl.sqlString(sPortCode)).append(" ) d on a.FSecurityCode = d.FSecurityCode ");
			
			   rs = dbl.openResultSet(queryBuf.toString());
			   setSecRecPayAttr(rs ,"unrealized");
			   queryBuf.setLength(0);
			   dbl.closeResultSetFinal(rs);
			  }	   
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}
	}
	
	
	/********************************************************
	 * add by jsc  20120301
	 *  处理已实现资本利得税
	 * @throws YssException
	 */
	private void  dealRealizedCGT() throws YssException{
		
		 ResultSet rs  =  null;
		 StringBuffer queryBuf  =  new StringBuffer();
		CtlPubPara para = new CtlPubPara ();
		para.setYssPub(pub);
		
          try{
        	  /*********************************************************
        	   *    add by jsc  20120302  
        	   *   SQL 查询说明：
        	   *    目前需要进行资本利得税计提的有交易所的股票、基金、债券、银行间债券、开放式基金
        	   *    已实现资本利得税 = 差价收入 * 税率 
        	   *    应收清算款 = 成交数量 * 成交价格
        	   *    实收实付 = 清算款 - 费用
        	   *    差价收入 = 实收实付 - 利息  
        	   *    注意： 交易所字段包含应收利息
        	   *    
        	   *    【资本利得税算法调整    已实现资本利得税 = (成交金额- 成本) *税率   20120309】
        	   *    
        	   */
        	  queryBuf.append(" select  trade.*,sec.fcatcode,sec.ftradecury as FCuryCode,sto.fstorageamount,sto.fstoragecost ");
        	  queryBuf.append(" from");
        	  //交易所取的是业务日期   
        	  // modify by jsc  2012
        	  queryBuf.append(" ( select fnum,FATTRCLSCODE,FSettleDate,fsecuritycode,fcashacccode,ftradeamount,ftrademoney,FAccruedinterest  from ");   
        	  // queryBuf.append(" ( select fnum,FATTRCLSCODE,FSettleDate,fsecuritycode,fcashacccode,ftradeamount,FTotalCost-nvl(FAccruedinterest,0)as FTotalCost  from "); 
        	  queryBuf.append(pub.yssGetTableName("tb_data_subtrade"));
        	  queryBuf.append(" where fcheckstate=1  and ftradetypecode='02' and fbargaindate =").append(dbl.sqlDate(dDate)).append("  and fportcode= ");
        	  queryBuf.append(dbl.sqlString(sPortCode));
               
               //开放式基金   日期取的是申请日
        	  queryBuf.append(" union all ");
              queryBuf.append(" select fnum,' ' as FATTRCLSCODE,FComfDate as FSettleDate, fsecuritycode,fapplycashacccode as fcashacccode,fcomfamount ");
              queryBuf.append("as ftradeamount,fcomfmoney as ftrademoney,0 as FAccruedinterest from ");
         	  queryBuf.append(pub.yssGetTableName("tb_Data_OpenFundTrade"));
         	  queryBuf.append(" where fcheckstate=1  and ftradetypecode='16' and FBARGAINDATE=").append(dbl.sqlDate(dDate)).append("  and fportcode= ");
         	  queryBuf.append(dbl.sqlString(sPortCode));
                
                //银行间债券  日期取的是业务日期
         	  queryBuf.append(" union all ");
              queryBuf.append(" select fnum, FATTRCLSCODE,FSETTLEDATE,fsecuritycode,fcashacccode,ftradeamount,ftrademoney,FBondIns as FAccruedinterest  from  ");
          	  queryBuf.append(pub.yssGetTableName("tb_Data_IntBakBond"));
          	  queryBuf.append(" where fcheckstate=1  and ftradetypecode='02' and fbargaindate =").append(dbl.sqlDate(dDate)).append("  and fportcode= ");
          	  queryBuf.append(dbl.sqlString(sPortCode)).append(" )trade ");
             
              //证券信息表，目前资本利得税只处理 债券，股票，基金
          	 queryBuf.append(" join  ");
              queryBuf.append(" (select fsecuritycode,fcatcode,ftradecury from  ").append(pub.yssGetTableName("tb_para_security"));
              queryBuf.append(" where fcheckstate=1 and fcatcode in ('EQ','FI','TR') and FSUBCATCODE<>'TR03') sec "); //货币基金不进行资本里的计提
              queryBuf.append(" on trade.fsecuritycode = sec.fsecuritycode ");
              //证券库存表
              queryBuf.append(" join  ");
              queryBuf.append(" (select fsecuritycode,fstorageamount,fstoragecost  from  ").append(pub.yssGetTableName("tb_stock_security"));
             queryBuf.append(" where fcheckstate=1 and fportcode= ").append(dbl.sqlString(sPortCode));
             queryBuf.append(" and fstoragedate=").append(dbl.sqlDate(YssFun.addDay(dDate, -1))).append(" ) sto ");
             queryBuf.append(" on trade.fsecuritycode = sto.fsecuritycode ");
        	  rs = dbl.openResultSet(queryBuf.toString());
			
			   //2. 根据已实现利得税结转方式来生成相应的数据
			   if(para.getCgtCarryWay(sPortCode)){
				   //结转，产生资金调拨
				   setCashTransAttr(rs);
			   }else{
				   //不结转，产生应付数据挂现金应收应付。
				   setCashRecPayAttr(rs);
			   }

		    }catch(Exception e){
			throw new YssException(e.getMessage());
		  }finally{
			  dbl.closeResultSetFinal(rs);
		  }
	}
	
	
	/**************************************************************
	 *  add by jsc 20120302 
	 *  设置证券应收应付属性值
	 *  @param flag   
	 *       realized        生成冲减未实现资本利得税的证券应收应付数据   03CGT
	 *       unrealized   生成未实现资本利得税的证券应收应付数据           07CGT
	 */
	private void setSecRecPayAttr(ResultSet rs, String flag)
			throws YssException {
		StringBuffer errorBuf = new StringBuffer(); // 用于给出明确的提示
		SecPecPayBean secRecPay = null;
		String sTsfTypeCode = "", sSubTsfTypeCode = "";
		double money = 0, baseRate = 0, portRate = 0, dCGTrate = 0,dMarketPrice=0 ;
		String sRoundCode = "";
		String strSubTsfTypeCode = "";
		HashMap  map_FI_FullPrice = null;
		try {
			sTsfTypeCode = "07";
			sSubTsfTypeCode = "07CGT";
			map_FI_FullPrice =  initExChange_FI_Para();
			while (rs.next()) {

				//货币基金不需要进行资本利得税计提
				if(rs.getString("fsubcatcode").equalsIgnoreCase("TR03")){
					continue;
				}
				
				secRecPay = new SecPecPayBean();
				dCGTrate = getCGTRate(rs.getString("FSECURITYCODE")); // 获取资本利得税税率
																		// (优先级别：组合证券专用>组合公用)
				sRoundCode = this.getCGTRondSet(rs.getString("FSECURITYCODE"));
				// 1. 当资本利得税税率为0时，则不进行计提
				if (dCGTrate == 0) {
					continue;
				}
                /*
                 * 注意全价行情的债券，算出的估增应刨去利息。
                 * 银行间已全部实行净价的报价方式，所以这里只对交易所债券行情进行判断处理。
                 */
				if(map_FI_FullPrice !=null &&"FI".equalsIgnoreCase(rs.getString("fcatcode"))&&"CG".equalsIgnoreCase(rs.getString("fexchangecode"))){
					
					dMarketPrice = (((String)map_FI_FullPrice.get(rs.getString("fexchangecode"))).indexOf(rs.getString("fsubcatcode"))>=0)?getFINetPrice(rs.getString("FSECURITYCODE"),rs.getDouble("FCsMarketPrice")): rs.getDouble("FCsMarketPrice");
				}else if(map_FI_FullPrice !=null &&"FI".equalsIgnoreCase(rs.getString("fcatcode"))&&"CS".equalsIgnoreCase(rs.getString("fexchangecode"))){
					dMarketPrice =  (((String)map_FI_FullPrice.get(rs.getString("fexchangecode"))).indexOf(rs.getString("fsubcatcode"))>=0)?getFINetPrice(rs.getString("FSECURITYCODE"),rs.getDouble("FCsMarketPrice")): rs.getDouble("FCsMarketPrice");
				}else{
					dMarketPrice = rs.getDouble("FCsMarketPrice");
				}
				
				money = YssD.sub(YssD.round(YssD.mul(rs.getDouble("FAmount"),
						dMarketPrice), 2), rs
						.getDouble("FCost"));// 估增= 库存数量 * 行情 - 成本

				if (YssD.sub((this.getSettingOper().reckonRoundMoney(
						sRoundCode, YssD.mul(money, dCGTrate))), rs
						.getDouble("FBAL")) == 0) {
					// 如果当天计提的资本利得税余额没变化，可以认为当天估增发生额为0，则不进行计提。
					// 这里判断为了减少尾差，算出余额就进行舍入操作，然后跟昨日资本利得税库存进行比较
					continue;
				} else if (money <= 0 && rs.getDouble("FBAL") == 0) {
					// 2. 第一次买入，估增余额小于0，则不进行计提
					continue;
				} else if (money <= 0 && rs.getDouble("FBAL") > 0) {
					// 1. 估增余额小于0时，则要将资本利得税余额反冲干净
					money = 0;
				}

				//投资品种为股票
				if (rs.getString("fcatcode").equalsIgnoreCase("EQ")) {

					strSubTsfTypeCode = YssOperCons.YSS_ZJDBZLX_ZBLDS_EQ;

				} else if (rs.getString("fcatcode").equalsIgnoreCase("FI")) {
                   //投资品种为债券
					strSubTsfTypeCode = YssOperCons.YSS_ZJDBZLX_ZBLDS_FI;

				} else if (rs.getString("fcatcode").equalsIgnoreCase("TR")) {
                  //投资品种为基金
					strSubTsfTypeCode = YssOperCons.YSS_ZJDBZLX_ZBLDS_TR;

				}
				money = YssD.sub(YssD.mul(money, dCGTrate), rs
						.getDouble("FBAL"));// 资本利得税余额 - 昨日资本利得税余额

				money = this.getSettingOper().reckonRoundMoney(sRoundCode,
						money);

				baseRate = this.getSettingOper().getCuryRate(this.dDate,
						rs.getString("FCuryCode"), this.sPortCode,
						YssOperCons.YSS_RATE_BASE);
				secRecPay.setBaseCuryRate(baseRate);
				portRate = this.getSettingOper().getCuryRate(this.dDate,
						rs.getString("FCuryCode"), this.sPortCode,
						YssOperCons.YSS_RATE_PORT);
				secRecPay.setPortCuryRate(portRate);
				secRecPay.setMoney(money);
				secRecPay.setMMoney(money);
				secRecPay.setVMoney(money);
				secRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
						secRecPay.getMoney(), secRecPay.getBaseCuryRate()));
				secRecPay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
						secRecPay.getMoney(), secRecPay.getBaseCuryRate()));
				secRecPay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
						secRecPay.getMoney(), secRecPay.getBaseCuryRate()));
				secRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(
						secRecPay.getMoney(), secRecPay.getBaseCuryRate(),
						secRecPay.getPortCuryRate(), rs.getString("FCuryCode"),
						this.dDate, this.sPortCode, 2));
				secRecPay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
						secRecPay.getMoney(), secRecPay.getBaseCuryRate(),
						secRecPay.getPortCuryRate(), rs.getString("FCuryCode"),
						this.dDate, this.sPortCode, 2));
				secRecPay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
						secRecPay.getMoney(), secRecPay.getBaseCuryRate(),
						secRecPay.getPortCuryRate(), rs.getString("FCuryCode"),
						this.dDate, this.sPortCode, 2));
				secRecPay.setStrTsfTypeCode(sTsfTypeCode);
				secRecPay.setStrSubTsfTypeCode(strSubTsfTypeCode);
				secRecPay.setAttrClsCode(rs.getString("FATTRCLSCODE"));
				secRecPay.setTransDate(this.dDate);
				secRecPay.setStrSecurityCode(rs.getString("FSecurityCode"));
				secRecPay.setStrPortCode(this.sPortCode);
				secRecPay.setStrCuryCode(rs.getString("FCuryCode"));
				// secRecPay.setDataSource(0);//
				secRecPay.checkStateId = 1;
				secRecPayAdmin.addList(secRecPay);
			}

		} catch (Exception e) {
			if (flag.equalsIgnoreCase("realized")) {
				errorBuf.append("处理已实现资本利得税时生成证券应收应付数据出错......");
			} else {
				errorBuf.append("处理未实现资本利得税时证券生成应收应付数据出错......");
			}
			throw new YssException(errorBuf.toString() + "\r\n"
					+ e.getMessage());
		}
	}
	
	/***************************************************************
	 * add by jsc  20120301
	 * @return
	 * @throws YssException
	 */
	private void setCashRecPayAttr(ResultSet  rs) throws YssException{
		
		CashPecPayBean cashRecPay  =  null;
		String sTsfTypeCode="",sSubTsfTypeCode="";
		double money=0 ,baseRate =0, portRate=0,dCGTrate=0;
		String strSubTsfTypeCode = "";
		double cost = 0;
		String sRoundCode="";
		try{
			
			  while (rs.next()){
				  
					
				  cashRecPay = new CashPecPayBean();
	 			  dCGTrate =   getCGTRate (rs.getString("FSECURITYCODE")); //获取资本利得税税率 (优先级别：组合证券专用>组合专用)
	 			 sRoundCode = this.getCGTRondSet(rs.getString("FSECURITYCODE"));
	 			  //1. 当资本利得税税率为0时，则不进行计提
	 			  if(dCGTrate==0){
	 				  continue;
	 			  }
	 			  //2. 收益>0  才生成已实现资本利得税
	 			  double scale =YssD.div(rs.getDouble("ftradeamount"),rs.getDouble("fstorageamount"));
	 			  cost =YssD.round( YssD.mul(scale,rs.getDouble("fstoragecost")),2);//计算结转的成本
	  			  //如果差价收入小于0 ，就不进行资本利得税的计提   
	 			//delete by jsc  20120309   资本利得税算法调整   (成交金额- 成本) * 费率
	  			  // if( rs.getDouble("FTotalCost")-cost<=0){   
	 			  if(rs.getDouble("ftrademoney")-cost<=0){
	  				   continue;
	  			   }
				  if(rs.getString("fcatcode").equalsIgnoreCase("EQ")){
	  				   
	  				    strSubTsfTypeCode =YssOperCons. YSS_ZJDBZLX_ZBLDS_EQ;
	  				    
	  			   }else if(rs.getString("fcatcode").equalsIgnoreCase("FI")){
	  				   
	  				  strSubTsfTypeCode =YssOperCons. YSS_ZJDBZLX_ZBLDS_FI;
	  				 
	  			   }else if(rs.getString("fcatcode").equalsIgnoreCase("TR")){
	  				   
	  				  strSubTsfTypeCode =YssOperCons. YSS_ZJDBZLX_ZBLDS_TR;
	  				 
	  			   }
				   //delete by jsc  20120309   资本利得税算法调整   (成交金额- 成本) * 费率
				  //money =YssD.mul((rs.getDouble("FTotalCost")-cost),dCGTrate);  
				  //mpdify by zhouwei 20120414 债券资本利得税计算方式改变
				  if(rs.getString("fcatcode").equalsIgnoreCase("FI")){//如果为债券，需要判断全价还是净价
					  //1.全价计算方式为:（成交金额- 成本-利息）*费率					  
					  //2.净价计算方式为： (成交金额- 成本) * 费率
					  money=caculateBondCGT(rs.getString("FSECURITYCODE"),rs.getDouble("FAccruedinterest"),rs.getDouble("ftrademoney"),
							          cost,dCGTrate);
				  }else{//非债券计算方式不变： (成交金额- 成本) * 费率
					  money =YssD.mul((rs.getDouble("ftrademoney")-cost),dCGTrate);
				  }	
				  if(money<=0){
					  continue;
				  }
				  money =  this.getSettingOper().reckonRoundMoney(sRoundCode, money);
	 			   baseRate = this.getSettingOper().getCuryRate( this.dDate,   rs.getString("FCuryCode"),  this.sPortCode,  YssOperCons.YSS_RATE_BASE );
	 			  cashRecPay.setBaseCuryRate(baseRate);
				   portRate = this.getSettingOper().getCuryRate(
							      this.dDate,  rs.getString("FCuryCode"), this.sPortCode, 
							      YssOperCons.YSS_RATE_PORT);
				   cashRecPay.setPortCuryRate(portRate);
				   
				   cashRecPay.setMoney(money);
				
				   cashRecPay.setBaseCuryMoney(
					   this.getSettingOper().calBaseMoney(
							   cashRecPay.getMoney(),
							   cashRecPay.getBaseCuryRate()
		               )
	               );

				   cashRecPay.setPortCuryMoney(
					   this.getSettingOper().calPortMoney(
							   cashRecPay.getMoney(),
							   cashRecPay.getBaseCuryRate(), 
							   cashRecPay.getPortCuryRate(),
	                       rs.getString("FCuryCode"), 
	                       this.dDate, 
	                       this.sPortCode,
	                       2
	                   )
	 	           );
				
				   cashRecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
				   cashRecPay.setSubTsfTypeCode(strSubTsfTypeCode);
				   cashRecPay.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
				   cashRecPay.setTradeDate(this.dDate);
				   cashRecPay.setCashAccCode(rs.getString("FCASHACCCODE"));
				   cashRecPay.setPortCode(this.sPortCode);
				   cashRecPay.setCuryCode(rs.getString("FCuryCode"));
				   cashRecPay.setDataSource(0);
				   //cashRecPay.setRelaNum(rs.getString("Fnum"));
				   cashRecPay.setInOutType(1);
				   cashRecPay.checkStateId = 1;
	 			   cashRecPayAdmin.addList(cashRecPay);
	 			   
	 		   }
		}catch(Exception e){
			throw new YssException("处理已实现资本利得税时生成现金应付数据出错......");
		}
	}
	
	/****************************************************************
	 *   add by jsc  20120301
	 * @param rs
	 * @throws YssException
	 */
	private void setCashTransAttr(ResultSet rs ) throws YssException{
		
		TransferBean tran = null;
		 TransferSetBean transferset =null;
		 ArrayList  tranSetList = null;
		double baseRate =0,portRate=0;
		String strSubTsfTypeCode = "";
		double money=0;
		double cost = 0,dCGTrate=0;
		String sRoundCode="";
		try{
			
			  while (rs.next()){
	  			   tran = new TransferBean();
	  			   
	  			  dCGTrate =   getCGTRate (rs.getString("FSECURITYCODE")); //获取资本利得税税率 (优先级别：组合证券专用>组合专用)
	  			 sRoundCode = this.getCGTRondSet(rs.getString("FSECURITYCODE"));
	 			  //1. 当资本利得税税率为0时，则不进行计提
	 			  if(dCGTrate==0){
	 				  continue;
	 			  }
	  			   
	  			   if(rs.getString("fcatcode").equalsIgnoreCase("EQ")){
	  				   //交易品种为股票
	  				    strSubTsfTypeCode =YssOperCons. YSS_ZJDBZLX_ZBLDS_EQ;
	  				    
	  			   }else if(rs.getString("fcatcode").equalsIgnoreCase("FI")){
	  				   //交易品种为债券
	  				  strSubTsfTypeCode =YssOperCons. YSS_ZJDBZLX_ZBLDS_FI;
	  				 
	  			   }else if(rs.getString("fcatcode").equalsIgnoreCase("TR")){
	  				   //交易品种为基金
	  				  strSubTsfTypeCode =YssOperCons. YSS_ZJDBZLX_ZBLDS_TR;
	  				 
	  			   }
	  			 double scale =YssD.div(rs.getDouble("ftradeamount"),rs.getDouble("fstorageamount"));
	  			   cost =YssD.round( YssD.mul(scale,rs.getDouble("fstoragecost")),2);//计算结转的成本


	  			   //delete by jsc  20120309   资本利得税算法调整   (成交金额- 成本) * 费率
					  // if( rs.getDouble("FTotalCost")-cost<=0){
	  			   //亏损的情况不支付资本利得
	  			   if( rs.getDouble("ftrademoney")-cost<=0){
	  				   continue;
	  			   }
	  			   
	  			//delete by jsc  20120309   资本利得税算法调整   (成交金额- 成本) * 费率
	  			  // money =YssD.mul((rs.getDouble("FTotalCost")-cost),dCGTrate);
	  			//modify by zhouwei 20120414 债券资本利得税计算方式改变
	  			 if(rs.getString("fcatcode").equalsIgnoreCase("FI")){//如果为债券，需要判断全价还是净价
					  //1.全价计算方式为:（成交金额- 成本-利息）*费率					  
					  //2.净价计算方式为： (成交金额- 成本) * 费率
	  				money=caculateBondCGT(rs.getString("FSECURITYCODE"),rs.getDouble("FAccruedinterest"),rs.getDouble("ftrademoney"),
							          cost,dCGTrate);
				  }else{//非债券计算方式不变： (成交金额- 成本) * 费率
					  money =YssD.mul((rs.getDouble("ftrademoney")-cost),dCGTrate);
				  }	
	  			 //支付已实现资本利得大于0
				  if(money<=0){
					  continue;
				  }
	  			  money =  this.getSettingOper().reckonRoundMoney(sRoundCode, money);
		  		   tran.setDtTransDate(this.dDate); //业务日期 
	  			   tran.setDtTransferDate(rs.getDate("FSettleDate")); //调拨日期
	  			   tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); //调拨类型
		  		   tran.setStrSubTsfTypeCode(strSubTsfTypeCode); //调拨子类型
		  		   tran.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
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
		           transferset.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
		           transferset.setDBaseRate(baseRate);
		           transferset.setDPortRate(portRate);
	  			   transferset.setDMoney(money); //调拨金额
	  			   transferset.setSPortCode(this.sPortCode);    // 组合代码
	  			   transferset.setSCashAccCode(rs.getString("FCashAccCode")); //现金帐户代码
	  			   transferset.setIInOut(-1); //资金流向 ，1代表流入，-1代表流出
	  			   transferset.checkStateId = 1;			   
	  			   
	  			   tranSetList = new ArrayList();
	 			   tranSetList.add(transferset);
	 			   
	 			   tran.setSubTrans(tranSetList);			   
	 			   tranAdmin.addList(tran); 
			  }
		}catch(Exception e ){
			throw new YssException("处理已实现资本利得税时生成资金调拨数据出错......");
		}
	}
	
    private HashMap getTradeData() throws YssException {
	   HashMap tempHash = new HashMap();
	   String key = "";
	   ArrayList tempAry = new ArrayList();//modified by yeshenghong 20120917 for safe check
	   String strSql = "";
	   ResultSet rs = null;
	   SecStockDetail secStockDetail = null;
	   
	   double tAmount = 0;//存放送股数量    
	   double sumAmount = 0;//用于存放同一支证券总的借入数量
	   double pAmount = 0;//存放每次借入应得的送股数量
	   double tmpAmount = 0;
	   
	   try{
		   //modify by fangjiang 2011-12-01 STORY #1773 资本利得税计算逻辑需要修改
		   strSql = " select * from " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + 
  			        " where FBARGAINDATE = (select max(FBARGAINDATE) from " + 
		            pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + " where FBARGAINDATE < " + dbl.sqlDate(dDate) + 
		            " and FPORTCODE = " + dbl.sqlString(sPortCode) + ") and FTRADEAMOUNT > 0 order by FPortCode, FSECURITYCODE, FNUM";
           rs = dbl.queryByPreparedStatement(strSql); 
           while (rs.next()){
        	   if(!key.equalsIgnoreCase(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"))){
				   tempAry = new ArrayList();   
			   }
			   secStockDetail = new SecStockDetail();
			   secStockDetail.setAmount(rs.getDouble("FTradeAmount"));
			   secStockDetail.setCost(rs.getDouble("FCost"));
			   secStockDetail.setfNum(rs.getString("FNum"));
			   if(tempAry!=null)
    		   {
				   tempAry.add(secStockDetail);
    		   }
			   
			   tempHash.put(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"), tempAry);  
			   
			   key = rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE");   
           }
           
           tempAry = null;
           dbl.closeResultSetFinal(rs);

           //送股数量更新HashMap
    	   strSql = " select * from " + pub.yssGetTableName("TB_DATA_SUBTRADE") + 
  				    " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode) +
  			        " and FBARGAINDATE = " + dbl.sqlDate(dDate) + " and FTRADETYPECODE in ('07') order by FPortCode, FSecurityCode, FNum "; 
    	   rs = dbl.queryByPreparedStatement(strSql); 
    	   while(rs.next()){ 	    		
    		    tempAry = (ArrayList)tempHash.get(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"));  		
	    		if(tempAry !=null){	    			
    				tAmount = rs.getDouble("FTRADEAMOUNT");
    				for(int i = 0; i<tempAry.size(); i++){
	    				secStockDetail = (SecStockDetail)tempAry.get(i);
		    			sumAmount = YssD.add(sumAmount, secStockDetail.getAmount());
		    		}
		    		if(sumAmount == 0 ){
		    			break;
		    		}
		    		for(int i = 0; i < tempAry.size(); i++ ){
		    			secStockDetail = (SecStockDetail)tempAry.get(i);
		    			if(i < tempAry.size() - 1){
			    			pAmount =  YssD.round
			    			           (
		    			        		   YssD.mul
		    			        		   (
	    			        				   YssD.div
	    			        				   (
				        						   secStockDetail.getAmount(), 
				        						   sumAmount
			        						   ), 
			        						   tAmount
				        				   ), 
	    			        		       2
				        		       );
			    			tmpAmount = YssD.add(tmpAmount, pAmount);
		    			}else{
		    				pAmount = YssD.sub(tAmount, tmpAmount);
		    			}
		    			secStockDetail.setAmount(YssD.add(secStockDetail.getAmount(), pAmount));
		    		}
		    		tmpAmount = 0;
		    		sumAmount = 0;		
	    		}	    				
	    	}
    	    
    	   tempAry = null;
    	   secStockDetail = null;
    	   dbl.closeResultSetFinal(rs);
    	   key = "";
    	   
    	   strSql = " select * from " + pub.yssGetTableName("TB_DATA_SUBTRADE") + 
	                " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode) +
                    " and FBARGAINDATE = " + dbl.sqlDate(dDate) + " and FTRADETYPECODE in ('01') order by FPortCode, FSecurityCode, FNum "; 
    	   rs = dbl.queryByPreparedStatement(strSql); 
    	   while(rs.next()){	    		 
    		   if(!key.equalsIgnoreCase(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"))){
    			   tempAry = new ArrayList();
    		   }
    		   secStockDetail = new SecStockDetail();
			   secStockDetail.setAmount(rs.getDouble("FTradeAmount"));
			   secStockDetail.setCost(rs.getDouble("FCost"));
			   secStockDetail.setfNum(rs.getString("FNum"));
			   
			   key = rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE");
	    		
	    	   if(tempHash.containsKey(key)){
	    	       ((ArrayList)tempHash.get(key)).add(secStockDetail);
	    	   }else{
	    		   if(tempAry!=null)
	    		   {
	    			   tempAry.add(secStockDetail);
	    		   }
	    		   tempHash.put(key, tempAry);
	    	   }
		   }  
           
		   /**
		    * 如果当天同时有买入、送股，卖出时先处理送股，再处理买入。
		    * 在同一天，'送股'的编号排在'买入'之前，所以可以根据FNum排序。
		    * 买入、送股、卖出在同一天时，认为当天送的股当天可以卖出，如果当天送的股当天不可以卖出，则目前还不支持，有待修改。
		    */
		   /*strSql = " select FSECURITYCODE, FTradeAmount, FCost, FNum from " + pub.yssGetTableName("TB_DATA_SUBTRADE") + 
		            " where FCheckState = 1 and FBARGAINDATE = " + dbl.sqlDate(dDate) + 
		            " and FPORTCODE = " + dbl.sqlString(sPortCode) + " and FTradeTypeCode in ('01','07') " +
  			        " union all (select FSECURITYCODE, FTradeAmount, FCost, FNum from " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + 
  			        " where FBARGAINDATE = (select max(FBARGAINDATE) from " + 
		   			pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + " where FBARGAINDATE < " + dbl.sqlDate(dDate) + 
		   			" and FPORTCODE = " + dbl.sqlString(sPortCode) + ") and FTRADEAMOUNT > 0) order by FSECURITYCODE,FNUM ";
		   //根据证券代码，编号排序后就无需频繁操作hashmap了
		   rs = dbl.queryByPreparedStatement(strSql);
		   while (rs.next()){
			   if(!strSecCode.equalsIgnoreCase(rs.getString("FSECURITYCODE"))){//如果证券代码为初始值或者证券代码与之前一条数据的证券代码不一致
				   tempAry = new ArrayList();
				   tempHash.put(rs.getString("FSECURITYCODE"), tempAry);  
			   }
			   strSecCode = rs.getString("FSECURITYCODE");
			   secStockDetail = new SecStockDetail();
			   secStockDetail.setAmount(rs.getDouble("FTradeAmount"));
			   secStockDetail.setCost(rs.getDouble("FCost"));
			   secStockDetail.setfNum(rs.getString("FNum"));
			   tempAry.add(secStockDetail);
		   }*/
		   return tempHash;
	   } catch (Exception e) {
           throw new YssException("获取数据出错！", e);
       } finally {
    	   dbl.closeResultSetFinal(rs);
       }
	}
    
    private void getSellCGT(HashMap map, ArrayList list) throws YssException {
       String strSql = "";
 	   ResultSet rs = null;
 	   ArrayList secAry = null;
 	   SecStockDetail secStockDetail = null;
 	   double scale = 0;
 	   double cost = 0;
 	   double tradeMoney = 0; //成交金额
 	   double surTradeMoney = 0; //剩余的成交金额
 	   double tradeAmount = 0;
 	   double rate = 0; //税率
 	   double gz = 0;  //估增
 	   double cgt = 0; //资本利得税
 	   double bcgt = 0; //本位币资本利得税
 	   double baseRate = 0;
 	   double portRate = 0;
 	   CGTBean bean = null;
 	   boolean flag;
 	   try{
 		   strSql = " select a.FNum as FNum, a.FPortCode as FPortCode, a.FSecurityCode as FSecurityCode, a.FTradeMoney as FTradeMoney,a.FAccruedinterest as FAccruedinterest, sec.FCatCode," +
 		   		    " a.FTradeAmount as FTradeAmount, a.FSettleDate as FSettleDate, b.FRate as FRate, " +
 		   		    " b.FRoundCode as FRoundCode, c.FCashAccCode as FCashAccCode, c.FCuryCode as FCuryCode,bond.FQuoteWay from " +
 		   		    " ( select * from " + pub.yssGetTableName("TB_DATA_SUBTRADE") + " where FCheckState = 1 and FBARGAINDATE = " +
   			        dbl.sqlDate(dDate) + " and FPORTCODE = " + dbl.sqlString(sPortCode) + " and FTradeTypeCode = '02' ) a " +
   			        " join (select * from " + pub.yssGetTableName("TB_DATA_CGT") + " where FCheckState = 1 ) b " +
   			        " on a.FSecurityCode = b.FSecurityCode " +
   			        " join (select * from " + pub.yssGetTableName("tb_para_cashaccount") +   			       
   			        " where FCheckState = 1 ) c on a.FCashAccCode = c.FCashAccCode " +
   			        //add by zhouwei 20120414 债券信息表
   			        " left join (select * from "+pub.yssGetTableName("tb_para_security")+" where fcheckstate=1) sec on a.FSecurityCode = sec.FSecurityCode"+
   			        " left join (select * from "+pub.yssGetTableName("Tb_Para_FixInterest")+" where fcheckstate=1) bond on a.FSecurityCode = bond.FSecurityCode"+
   			        //----------end--------
   			        " order by a.FSECURITYCODE, a.FNUM ";
 		   rs = dbl.queryByPreparedStatement(strSql);
 		   while (rs.next()){
 			  secAry = (ArrayList)map.get(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"));
 			  tradeMoney = rs.getDouble("FTradeMoney");
 			  surTradeMoney = tradeMoney;
 			  tradeAmount = rs.getDouble("FTradeAmount");
 			  rate = rs.getDouble("FRate");
 			  flag = true;
 			  for(int i=0; i<secAry.size(); i++){
 				  secStockDetail = (SecStockDetail)secAry.get(i);
 				  if(secStockDetail.getAmount() == 0){
 					  break;
 				  }
 				  if(tradeAmount >= secStockDetail.getAmount()){					  
 					  scale = YssD.div(secStockDetail.getAmount(), tradeAmount);
 					  tradeMoney = YssD.mul(surTradeMoney, scale);
 					  cost = secStockDetail.getCost();
 					  tradeAmount = tradeAmount - secStockDetail.getAmount();
 					  surTradeMoney = YssD.sub(surTradeMoney, tradeMoney);
 					  secStockDetail.setCost(0);
 					  secStockDetail.setAmount(0);
 				  }else{
 					  tradeMoney = surTradeMoney;
 					  scale = YssD.div(tradeAmount, secStockDetail.getAmount());
 					  cost = YssD.mul(secStockDetail.getCost(), scale);
 					  cost = YssD.round(cost, 2);
 					  secStockDetail.setCost(YssD.sub(secStockDetail.getCost(), cost));
 					  secStockDetail.setAmount(YssD.sub(secStockDetail.getAmount(), tradeAmount));
 					  flag = false;
 				  }
 				  //if(tradeMoney > cost){
 				  //modify by zhouwei 20120414 债券资本利得税计算方式改变 ----------
 	  			 if(rs.getString("FCatCode").equalsIgnoreCase("FI")){//如果为债券，需要判断全价还是净价
 	  				 if(rs.getInt("FQuoteWay")==0){
 					  //1.全价计算方式为:（成交金额- 成本-利息）
 	  					 gz=YssD.sub(tradeMoney, cost,rs.getDouble("FAccruedinterest"));
 	  				 }else{
 					  //2.净价计算方式为： (成交金额- 成本) 
 	  					 gz = YssD.sub(tradeMoney, cost);
 	  				 }
 				  }else{//非债券计算方式不变： (成交金额- 成本) 
 					 gz = YssD.sub(tradeMoney, cost);
 				  }	
 				  //----------------end---------
				  baseRate = this.getSettingOper().getCuryRate(
						  this.dDate, 
						  rs.getString("FCuryCode"), 
						  this.sPortCode, 
						  YssOperCons.YSS_RATE_BASE);
				  portRate = this.getSettingOper().getCuryRate(
						  this.dDate, 
						  rs.getString("FCuryCode"), 
						  this.sPortCode, 
						  YssOperCons.YSS_RATE_PORT);
				  if(gz > 0) {
					  cgt = this.getSettingOper().reckonRoundMoney(
							  rs.getString("FRoundCode"), 
							  YssD.mul(gz, rate));
					  bcgt = this.getSettingOper().calPortMoney(
							  cgt,
							  baseRate, 
							  portRate,
							  rs.getString("FCuryCode"), 
	                          this.dDate,
	                          this.sPortCode);
				  } else {
					  cgt = 0;
					  bcgt = 0;
				  } 
				  bean = new CGTBean();
				  bean.setPortCode(this.sPortCode);
				  bean.setNum(rs.getString("FNum"));
				  bean.setRelaNum(secStockDetail.getfNum());
				  bean.setSecurityCode(rs.getString("FSecurityCode"));
				  bean.setDate(this.dDate);
				  bean.setSettleDate(rs.getDate("FSettleDate"));
				  bean.setCost(cost);
				  bean.setMarketValue(tradeMoney);
				  bean.setTradeTypeCode("02");
				  bean.setApp(gz);
				  bean.setCgt(cgt);
				  bean.setBcgt(bcgt);
				  bean.setCashAccCode(rs.getString("FCashAccCode")); 
				  bean.setCuryCode(rs.getString("FCuryCode"));				  
				  list.add(bean);
 				  //}
 				  if(!flag) break;
 			  }
 		   }
 		   
 		   dbl.closeResultSetFinal(rs);
 		   
 	   } catch (Exception e) {
           throw new YssException("获取数据出错！", e);
       } finally {
     	   dbl.closeResultSetFinal(rs);
       }
    }
    
    private void getAppCGT(ArrayList list) throws YssException {
       String strSql = "";
  	   ResultSet rs = null;
  	   double cost = 0;
  	   double marketValue = 0;
  	   double tradeAmount = 0;
  	   double rate = 0; //税率
  	   double gz = 0;  //估增
  	   double cgt = 0; //资本利得税
  	   double bcgt = 0; //本位币资本利得税
  	   double baseRate = 0;
  	   double portRate = 0;
  	   CGTBean bean = null;
  	   ArrayList alMethods = getValuationMethods();    
  	   MTVMethodBean vMethod = null;
  	   try{
  		   for(int i = 0; i < alMethods.size(); i++) {
  			   vMethod = (MTVMethodBean) alMethods.get(i);		  
	  		   strSql = " select a.FSECURITYCODE as FSECURITYCODE, a.FCost as FCost, a.FTradeAmount as FTradeAmount, " +
	  		   		    " a.FNum as FNum, c.FCsMarketPrice as FCsMarketPrice, " +
	  		   		    " d.FRate as FRate, d.FRoundCode as FRoundCode, e.FCashAccCode as FCashAccCode, " +
	  		   		    " f.FCuryCode as FCuryCode from (select * from " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + 
	  		            " where FBARGAINDATE = " + dbl.sqlDate(dDate) + " and FPORTCODE = " + dbl.sqlString(sPortCode) + 
	   			        " and FTRADEAMOUNT > 0) a join (select FLinkCode from " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
	   	                " where FCheckState = 1 and FMtvCode= " + dbl.sqlString(vMethod.getMTVCode()) +
	   	                " ) b on a.Fsecuritycode = b.FLinkCode join (select " + vMethod.getMktPriceCode() +
	   	                " as FCsMarketPrice, FSecurityCode, FMktValueDate from (select m1.* from " +
	   	                pub.yssGetTableName("Tb_Data_MarketValue") + " m1 join " +
	   	                " (select max(FMktValueDate) as FMktValueDate, FSecurityCode, FMktSrcCode from " +
	   	                pub.yssGetTableName("Tb_Data_MarketValue") + " m2 where FCheckState = 1 and FMktValueDate <= " +
	   	                dbl.sqlDate(dDate) + " group by FSecurityCode, FMktSrcCode) m2 " + 
	   	                " on m1.FMKtvalueDate=m2.FMktValueDate and m1.FSecuritycode=m2.FSecurityCode " + 
	   	                " and m1.FMktSrcCode= m2.FMktSrcCode)) c on a.FSecurityCode = c.FSecurityCode " +
	   	                " join (select * from " + pub.yssGetTableName("TB_DATA_CGT") + 
	   	                " where FCheckState = 1) d on a.FSecurityCode = d.FSecurityCode " +
	   	                " join (select * from " + pub.yssGetTableName("TB_DATA_SUBTRADE") + 
	   	                " where FCheckState = 1 and FBARGAINDATE <= " + dbl.sqlDate(dDate) + 
	   	                " and FPORTCODE = " + dbl.sqlString(sPortCode) + " and FTradeTypeCode in ('01','07')) e " +
	   	                " on a.FNum = e.FNum join (select * from " + pub.yssGetTableName("tb_para_cashaccount") + 
	   			        " where FCheckState = 1 ) f on e.FCashAccCode = f.FCashAccCode " +
	   	                " order by a.FSECURITYCODE, a.FNUM ";
	  		   rs = dbl.queryByPreparedStatement(strSql);
	  		   while (rs.next()){
	  			  cost = rs.getDouble("FCost");
	  			  tradeAmount = rs.getDouble("FTradeAmount");
	  			  marketValue = YssD.round(
	  					  YssD.mul(tradeAmount, rs.getDouble("FCsMarketPrice"))
	  					  ,2);
	  			  rate = rs.getDouble("FRate");
  				  //if(marketValue > cost){
				  gz = YssD.sub(marketValue, cost);
				  baseRate = this.getSettingOper().getCuryRate(
						  this.dDate, 
						  rs.getString("FCuryCode"), 
						  this.sPortCode, 
						  YssOperCons.YSS_RATE_BASE);
				  portRate = this.getSettingOper().getCuryRate(
						  this.dDate, 
						  rs.getString("FCuryCode"), 
						  this.sPortCode, 
						  YssOperCons.YSS_RATE_PORT);
				  if(gz > 0) {
					  cgt = this.getSettingOper().reckonRoundMoney(
							  rs.getString("FRoundCode"), 
							  YssD.mul(gz, rate));
					  bcgt = this.getSettingOper().calPortMoney(
							  cgt,
							  baseRate, 
							  portRate,
							  rs.getString("FCuryCode"), 
	                          this.dDate,
	                          this.sPortCode); 
				  } else {
					  cgt = 0;
					  bcgt = 0;
				  }
				  bean = new CGTBean();
				  bean.setPortCode(this.sPortCode);
				  bean.setNum(rs.getString("FNum"));
				  bean.setRelaNum(rs.getString("FNum"));
				  bean.setSecurityCode(rs.getString("FSecurityCode"));
				  bean.setDate(this.dDate);
				  //结算日期只对卖出有用，设成跟业务日期不一样，是为了在生成证券应收应付时取数方便
				  bean.setSettleDate(YssFun.addDay(this.dDate, 1)); 
				  bean.setCost(cost);
				  bean.setMarketValue(marketValue);
				  bean.setTradeTypeCode(" ");
				  bean.setApp(gz);
				  bean.setCgt(cgt);
				  bean.setBcgt(bcgt);
				  bean.setCashAccCode(rs.getString("FCashAccCode")); 
				  bean.setCuryCode(rs.getString("FCuryCode"));
				  list.add(bean);
				  //break;
  				  //} 
  			   }   
  		   }
  	   } catch (Exception e) {
           throw new YssException("获取数据出错！", e);
       } finally {
      	   dbl.closeResultSetFinal(rs);
       }
    }   
    
    private void insertCGTData(ArrayList list) throws YssException {
    	String strSql = "";
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
    	YssPreparedStatement pst = null; 
        //=============end====================
		Connection conn = dbl.loadConnection();
		CGTBean bean = null;
		boolean bTrans = false;
		try {			
			conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_DBCGT") +
   			         " where FPORTCODE = " + dbl.sqlString(sPortCode) + 
   			         " and FDate = " + dbl.sqlDate(dDate);
            dbl.executeSql(strSql);
            
			strSql = " insert into " + pub.yssGetTableName("Tb_Data_DBCGT")
				   + " (FNum, FDATE, FSETTLEDATE, FSECURITYCODE, FCOST, FTRADETYPECODE, " 
				   + " FMARKETVALUE, FAPPRECIATION, FCGT, FBCGT, FCashAccCode, FCuryCode, FRelaNum, FPortCode) "
				   + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			for (int i = 0; i < list.size(); i++) {
				bean = (CGTBean)list.get(i);
                pst.setString(1, bean.getNum());
                pst.setDate(2, YssFun.toSqlDate(this.dDate));
                pst.setDate(3, YssFun.toSqlDate(bean.getSettleDate()));
                pst.setString(4, bean.getSecurityCode());
                pst.setDouble(5, bean.getCost());
                pst.setString(6, bean.getTradeTypeCode());
                pst.setDouble(7, bean.getMarketValue());
                pst.setDouble(8, bean.getApp());
                pst.setDouble(9, bean.getCgt());
                pst.setDouble(10, bean.getBcgt());
                pst.setString(11, bean.getCashAccCode());
                pst.setString(12, bean.getCuryCode());
                pst.setString(13, bean.getRelaNum());
                pst.setString(14, bean.getPortCode());
				pst.executeUpdate();
            }			
			/*strSql = " insert into " + pub.yssGetTableName("Tb_Data_DBCGT")
			         + " select * from (select FNUM, FRELANUM, FPORTCODE, " + dbl.sqlDate(this.dDate)
			         + " as fdate, FSETTLEDATE, FSECURITYCODE, FTRADETYPECODE, FCOST, FMARKETVALUE, "
			         + " FAPPRECIATION, FCGT, FBCGT, FCURYCODE, FCASHACCCODE from " 
			         + pub.yssGetTableName("Tb_Data_DBCGT") 
			         + " where FPORTCODE = " + dbl.sqlString(sPortCode) 
			         + " and FTradeTypeCode = '02' and FDate < " + dbl.sqlDate(dDate)
			         + " and FSettleDate >= " + dbl.sqlDate(dDate) + ")";
			dbl.executeSql(strSql);*/
			
            conn.commit();
            bTrans = false;
		} catch(Exception e) {
			throw new YssException("插入資本利得稅数据出错！", e);
		} finally {
			dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
		}
    }  
    
    private void createSecRecPay() throws YssException {
       String strSql = "";
 	   ResultSet rs = null;	
 	   SecPecPayBean secRecPay = null;
 	   SecRecPayAdmin secRecPayAdmin = new SecRecPayAdmin(); 
 	   double baseRate = 0;
 	   double portRate = 0;
 	   double money = 0;
 	   try{
 		   strSql = " select a.FCGT as FCGT, nvl(a.FSecurityCode,b.FSecurityCode) as FSecurityCode, " +
 		   		    " nvl(a.FCuryCode,b.FCuryCode) as FCuryCode, b.FBal as FBal from " +
 			   		" (select sum(FCGT) as FCGT, FSecurityCode, FCuryCode from " +			   		
 			   		" (select FCGT, FSecurityCode, FCuryCode from " + pub.yssGetTableName("Tb_Data_DBCGT") + 
 		            " where FPORTCODE = " + dbl.sqlString(sPortCode) + 
 		            " and FDate = " + dbl.sqlDate(dDate) + 
 		            " and FDate < FSettleDate " +
 		            " and FCGT > 0 " +
 		            " union all " +
 		            " select FCGT, FSecurityCode, FCuryCode from " + pub.yssGetTableName("Tb_Data_DBCGT") + 
 		            " where FPORTCODE = " + dbl.sqlString(sPortCode) + 
 		            " and FDate < " + dbl.sqlDate(dDate) +
 		            " and FSettleDate >= " + dbl.sqlDate(dDate) +
 		            " and FTradeTypeCode = '02' " +
 		            " and FCGT > 0) m group by m.FSecurityCode, m.FCuryCode) a " +
 		            " full join (select * from " + pub.yssGetTableName("Tb_Stock_Secrecpay") + 
 		            " where FCheckState =1 and fsubtsftypecode = '07CGT' and fstoragedate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
 		            " and FPORTCODE = " + dbl.sqlString(sPortCode) + ") b on a.FSecurityCode = b.FSecurityCode ";
 		   rs = dbl.openResultSet(strSql);
 		   while (rs.next()){
 			   secRecPay = new SecPecPayBean();
 			   money = YssD.sub(
 						   rs.getDouble("FCGT"), 
 					       rs.getDouble("FBal")
 					   );
 			   baseRate = this.getSettingOper().getCuryRate(
					  		  this.dDate, 
						      rs.getString("FCuryCode"), 
						      this.sPortCode, 
						      YssOperCons.YSS_RATE_BASE
						  );
 			   secRecPay.setBaseCuryRate(baseRate);
			   portRate = this.getSettingOper().getCuryRate(
						      this.dDate, 
						      rs.getString("FCuryCode"), 
						      this.sPortCode, 
						      YssOperCons.YSS_RATE_PORT
						  );
			   secRecPay.setPortCuryRate(portRate);
 			   secRecPay.setMoney(money);
 			   secRecPay.setMMoney(money);
 			   secRecPay.setVMoney(money);
 			   secRecPay.setBaseCuryMoney(
				   this.getSettingOper().calBaseMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate()
	               )
               );
 			   secRecPay.setMBaseCuryMoney(
				   this.getSettingOper().calBaseMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate()
                   )
               );
 			   secRecPay.setVBaseCuryMoney(
				   this.getSettingOper().calBaseMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate()
                   )
               );
 			   secRecPay.setPortCuryMoney(
				   this.getSettingOper().calPortMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate(), 
					   secRecPay.getPortCuryRate(),
                       rs.getString("FCuryCode"), 
                       this.dDate, 
                       this.sPortCode,
                       2
                   )
 	           );
 			   secRecPay.setMPortCuryMoney(
				   this.getSettingOper().calPortMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate(), 
					   secRecPay.getPortCuryRate(),
                       rs.getString("FCuryCode"), 
                       this.dDate, 
                       this.sPortCode,
                       2
                   )
 	 	       );
 			   secRecPay.setVPortCuryMoney(
				   this.getSettingOper().calPortMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate(), 
					   secRecPay.getPortCuryRate(),
                       rs.getString("FCuryCode"), 
                       this.dDate, 
                       this.sPortCode,
                       2
                   )
 	 	       );
 			   secRecPay.setStrTsfTypeCode("07");
 			   secRecPay.setStrSubTsfTypeCode("07CGT");
 			   secRecPay.setTransDate(this.dDate);
 			   secRecPay.setStrSecurityCode(rs.getString("FSecurityCode"));
 			   secRecPay.setStrPortCode(this.sPortCode);
 			   secRecPay.setStrCuryCode(rs.getString("FCuryCode"));
 			   secRecPay.checkStateId = 1;
 			   secRecPayAdmin.addList(secRecPay);
 		   }
 		   secRecPayAdmin.setYssPub(pub);
 		   secRecPayAdmin.insert(this.dDate, "07", "07CGT", this.sPortCode, "", "", "", "", 0);
 		   secRecPayAdmin = new SecRecPayAdmin(); 
 		   
 		   dbl.closeResultSetFinal(rs);
 		   
 		   strSql = " select sum(FCGT) as FCGT, FSecurityCode, FCuryCode from " + pub.yssGetTableName("Tb_Data_DBCGT") + 
                    " where FSettleDate = " + dbl.sqlDate(dDate) + " and FPORTCODE = " + dbl.sqlString(sPortCode) + 
	                " and FDate < FSettleDate and FTradeTypeCode = '02' and FCGT > 0 group by FSecurityCode, FCuryCode " ;
 		   rs = dbl.queryByPreparedStatement(strSql);
		   while (rs.next()){
			   secRecPay = new SecPecPayBean();
			   money = rs.getDouble("FCGT"); 
			   baseRate = this.getSettingOper().getCuryRate(
					  		  this.dDate, 
						      rs.getString("FCuryCode"), 
						      this.sPortCode, 
						      YssOperCons.YSS_RATE_BASE
						  );
			   secRecPay.setBaseCuryRate(baseRate);
			   portRate = this.getSettingOper().getCuryRate(
						      this.dDate, 
						      rs.getString("FCuryCode"), 
						      this.sPortCode, 
						      YssOperCons.YSS_RATE_PORT
			              );
			   secRecPay.setPortCuryRate(portRate);
			   secRecPay.setMoney(money);
			   secRecPay.setMMoney(money);
			   secRecPay.setVMoney(money);
			   secRecPay.setBaseCuryMoney(
				   this.getSettingOper().calBaseMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate()
				   )
			   );
	           secRecPay.setMBaseCuryMoney(
				   this.getSettingOper().calBaseMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate()
				   )
               );
			   secRecPay.setVBaseCuryMoney(
				   this.getSettingOper().calBaseMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate()
		           )
		       );
			   secRecPay.setPortCuryMoney(
				   this.getSettingOper().calPortMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate(), 
					   secRecPay.getPortCuryRate(),
			           rs.getString("FCuryCode"), 
			           this.dDate, 
			           this.sPortCode,
			           2
		           )
		       );
			   secRecPay.setMPortCuryMoney(
				   this.getSettingOper().calPortMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate(), 
					   secRecPay.getPortCuryRate(),
		               rs.getString("FCuryCode"), 
		               this.dDate, 
		               this.sPortCode,
		               2
		           )
	           );
			   secRecPay.setVPortCuryMoney(
				   this.getSettingOper().calPortMoney(
					   secRecPay.getMoney(),
					   secRecPay.getBaseCuryRate(), 
					   secRecPay.getPortCuryRate(),
		               rs.getString("FCuryCode"), 
		               this.dDate, 
		               this.sPortCode,
		               2
		           )
		       );
			   secRecPay.setStrTsfTypeCode("03");
			   secRecPay.setStrSubTsfTypeCode("03CGT");
			   secRecPay.setTransDate(this.dDate);
			   secRecPay.setStrSecurityCode(rs.getString("FSecurityCode"));
			   secRecPay.setStrPortCode(this.sPortCode);
			   secRecPay.setStrCuryCode(rs.getString("FCuryCode"));
			   secRecPay.checkStateId = 1;
			   secRecPayAdmin.addList(secRecPay);
		   }
		   secRecPayAdmin.setYssPub(pub);
		   secRecPayAdmin.insert(this.dDate, "03", "03CGT", this.sPortCode, "", "", "", "", 1);
 	    } catch (Exception e) {
            throw new YssException("获取数据出错！", e);
        } finally {
     	    dbl.closeResultSetFinal(rs);
        }
    }
    
    private void createCashTrans() throws YssException {
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
    }
    
    public ArrayList getValuationMethods() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        MTVMethodBean vMethod = null;
        ArrayList alResult = new ArrayList();
        try {
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
                dbl.sqlString(this.sPortCode) +
                " and FCheckState = 1 ) m join ( select max(FStartDate) as FStartDate from "+
                pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                " where FRelaType = 'MTV' and FPortCode = " +
                dbl.sqlString(this.sPortCode) +
                " and FCheckState = 1 ) n on m.FStartDate = n.FStartDate "+
                " ) b on a.FMTVCode = b.FSubCode where a.FCheckState = 1 order by b.FRelaGrade desc";
            rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            if (rs.next()) {
                rs.beforeFirst();
                while (rs.next()) {
                    vMethod = new MTVMethodBean();
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

    private double getCGTRate (String securitycode) throws YssException{
    	
    	ResultSet rs = null;
    	StringBuffer queryBuf = new StringBuffer();
    	double rate =0;
    	try{
    		
    		//1. 获取组合证券专用的CGT税率
    		queryBuf.append("  select  frate from ").append(pub.yssGetTableName("TB_DATA_CGT"));
    		queryBuf.append(" where FCheckState = 1 and fsecuritycode=").append(dbl.sqlString(securitycode));
    		queryBuf.append(" and fportcode=").append(dbl.sqlString(this.sPortCode));
    		 rs = dbl.openResultSet(queryBuf.toString());
    		 if(rs.next()){
    			 rate = rs.getDouble("frate");
    			 return rate;
    		 }
    		 dbl.closeResultSetFinal(rs);
    		 queryBuf.setLength(0);
    		 
    		//获取品种信息  add by zhouwei 20120409 ----------start------
    		 String catCode="";
    		 String subCatCode="";
    		queryBuf.append("select a.* from ").append(pub.yssGetTableName("tb_para_security"))
    				.append(" a left join Tb_Base_Category b on a.FCatCode=b.Fcatcode")
    				.append(" left join Tb_Base_SubCategory c on a.FSubCatCode=c.Fsubcatcode")
    				.append(" where a.fsecuritycode=").append(dbl.sqlString(securitycode));
    		rs=dbl.openResultSet(queryBuf.toString());
    		if(rs.next()){
    			catCode=rs.getString("FCatCode");
    			subCatCode=rs.getString("FSubCatCode");
    		}
    		dbl.closeResultSetFinal(rs);
    		queryBuf.setLength(0);
    		//根据品种类型来获取配置
    		queryBuf.append("select * from ").append(pub.yssGetTableName("TB_DATA_CGT"))
    		         .append(" where fcheckstate=1 and ( FCatCode=").append(dbl.sqlString(catCode))
    		         .append(" or FCatCode=' ' ) and (FSubCatCode=").append(dbl.sqlString(subCatCode))
    		         .append(" or FSubCatCode=' ' )")
    		         .append(" and fportcode=").append(dbl.sqlString(this.sPortCode))
    		         .append(" and fsecuritycode = ' ' order by FCatCode desc,FSubCatCode desc");
    		rs=dbl.openResultSet(queryBuf.toString());
    		if(rs.next()){
    			 rate = rs.getDouble("frate");
       			 return rate;
    		}
    		dbl.closeResultSetFinal(rs);
    		queryBuf.setLength(0);
    		//------------------end----------------------
    		//2. 如果没有组合证券专用的税率， 则获取组合公用汇率
     		queryBuf.append("  select  frate from ").append(pub.yssGetTableName("TB_DATA_CGT"));
     		queryBuf.append(" where FCheckState = 1 ");
     		queryBuf.append(" and fportcode=").append(dbl.sqlString(this.sPortCode));
     		queryBuf.append(" and fsecuritycode = ' '");
            rs = dbl.openResultSet(queryBuf.toString());
            if(rs.next()){
   			 rate = rs.getDouble("frate");
   			 return rate;
   		    }
    		 dbl.closeResultSetFinal(rs);
    		 queryBuf.setLength(0);
    		 


    		 
    		return rate;
    	}catch(Exception e ){
    		throw new YssException("获取资本利得税率出错......");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }

    private String getCGTRondSet (String securitycode) throws YssException{
    	
    	ResultSet rs = null;
    	StringBuffer queryBuf = new StringBuffer();
    	String sRoundCode = "  ";
    	try{
    		
    		//1. 获取组合证券专用的CGT税率
    		queryBuf.append("  select  FROUNDCODE from ").append(pub.yssGetTableName("TB_DATA_CGT"));
    		queryBuf.append(" where FCheckState = 1 and fsecuritycode=").append(dbl.sqlString(securitycode));
    		queryBuf.append(" and fportcode=").append(dbl.sqlString(this.sPortCode));
    		 rs = dbl.openResultSet(queryBuf.toString());
    		 if(rs.next()){
    			 sRoundCode = rs.getString("FROUNDCODE");
    			 return sRoundCode;
    		 }
    		 dbl.closeResultSetFinal(rs);
    		 queryBuf.setLength(0);
    		//获取品种信息  add by zhouwei 20120409 ----------start------
    		 String catCode="";
    		 String subCatCode="";
    		queryBuf.append("select a.* from ").append(pub.yssGetTableName("tb_para_security"))
    				.append(" a left join Tb_Base_Category b on a.FCatCode=b.Fcatcode")
    				.append(" left join Tb_Base_SubCategory c on a.FSubCatCode=c.Fsubcatcode")
    				.append(" where a.fsecuritycode=").append(dbl.sqlString(securitycode));
    		rs=dbl.openResultSet(queryBuf.toString());
    		if(rs.next()){
    			catCode=rs.getString("FCatCode");
    			subCatCode=rs.getString("FSubCatCode");
    		}
    		dbl.closeResultSetFinal(rs);
    		queryBuf.setLength(0);
    		//根据品种类型来获取配置
    		queryBuf.append("select * from ").append(pub.yssGetTableName("TB_DATA_CGT"))
    		         .append(" where fcheckstate=1 and ( FCatCode=").append(dbl.sqlString(catCode))
    		         .append(" or FCatCode=' ' ) and (FSubCatCode=").append(dbl.sqlString(subCatCode))
    		         .append(" or FSubCatCode=' ' )")
    		         .append(" and fportcode=").append(dbl.sqlString(this.sPortCode))
    		         .append(" and fsecuritycode = ' ' order by FCatCode desc,FSubCatCode desc");
    		rs=dbl.openResultSet(queryBuf.toString());
    		if(rs.next()){
    			sRoundCode = rs.getString("FROUNDCODE");
       			 return sRoundCode;
    		}
    		dbl.closeResultSetFinal(rs);
    		queryBuf.setLength(0);
    		//------------------end----------------------
    		//2. 如果没有组合证券专用的税率， 则获取组合公用汇率
     		queryBuf.append("  select  FROUNDCODE from ").append(pub.yssGetTableName("TB_DATA_CGT"));
     		queryBuf.append(" where FCheckState = 1 ");
     		queryBuf.append(" and fportcode=").append(dbl.sqlString(this.sPortCode));
     		queryBuf.append(" and fsecuritycode = ' '");
            rs = dbl.openResultSet(queryBuf.toString());
            if(rs.next()){
            	sRoundCode = rs.getString("FROUNDCODE");
   			 return sRoundCode;
   		    }
    		 dbl.closeResultSetFinal(rs);
    		 queryBuf.setLength(0);
    		 


    		 
    		return sRoundCode;
    	}catch(Exception e ){
    		throw new YssException("获取资本利得税率出错......");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    
    /***************************************************
     *  获取交易所进行全价交易的债券品种  
     * @return
     * @throws YssException
     */
    private HashMap initExChange_FI_Para() throws YssException{
    	
    	ResultSet rs = null;
    	HashMap  map =null;
    	StringBuffer buf = new StringBuffer();
    	StringBuffer keyBuf = new StringBuffer();
    	StringBuffer valueBuf = new StringBuffer();
    	try{
    		
    		buf.append(" select fmarket,fcatcode,fbondtradetype  from   ").append(pub.yssGetTableName("tb_DAO_ExchangeBond"));
    		buf.append("  where fcheckstate=1 and fportcode= ").append(dbl.sqlString(this.sPortCode));
    		
    		rs = dbl.openResultSet(buf.toString());
    		
    		
    		while(rs.next()){
    			
    			if( rs.getString("fbondtradetype").equalsIgnoreCase("00")){
    				   continue;  
    			}
    			
    			if(map ==null){
    				  map = new HashMap();
    			}
    			
    			//交易所  (01  上交所;02  深交所)
    			if(rs.getString("fmarket").equalsIgnoreCase("01")){
    				keyBuf.append("CG");
    			}else{
    				keyBuf.append("CS");
    			}
    			
    			//债券品种  (01  国债;02 企业债;03 可转债;04 分离可转债;05 公司债;06 资产证券化产品)
    			if(rs.getString("fcatcode").equalsIgnoreCase("01")){
    				valueBuf.append("FI12");
    			}else if (rs.getString("fcatcode").equalsIgnoreCase("02")){
    				valueBuf.append("FI09");
    			}else if (rs.getString("fcatcode").equalsIgnoreCase("03")){
    				valueBuf.append("FI06");
    			}else if (rs.getString("fcatcode").equalsIgnoreCase("04")){
    				valueBuf.append("FI07");
    			}else if (rs.getString("fcatcode").equalsIgnoreCase("05")){
    				valueBuf.append("FI08");
    			}else if (rs.getString("fcatcode").equalsIgnoreCase("06")){
    				valueBuf.append("FI10");
    			}
    			
    			
    			
    			if(map.containsKey(keyBuf.toString())){
    				valueBuf.append(",").append((String)map.get(keyBuf.toString()));
    			}
    			map.put(keyBuf.toString(),valueBuf.toString());
    			keyBuf.setLength(0);
    			valueBuf.setLength(0);
    		}
    		
    		return map;
    	}catch(YssException e){
    		throw new YssException("初始化数据接口参数设置 - 交易所债券参数设置 出错......");
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
    		throw new YssException("初始化数据接口参数设置 - 交易所债券参数设置 出错......");
		}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    // 获取债券净价行情   add by jsc 20120310 
    private double getFINetPrice (String securityCode,double dMarketPrice) throws YssException {
    	
    	HashMap hmZQRate = null;
    	BigDecimal bigInt100,bigBefInt100 ;
    	try{
    		
    	
    		DataBase dataBase = new DataBase();
        	dataBase.setYssPub(pub);
        	//计算税前 税后 百元债券利息 因为无法判断买卖标志 所以 默认为  用买入计息设置的利息算法公式来计算百元债券利息
            hmZQRate = dataBase.calculateZQRate(securityCode, dDate, "B",this.sPortCode);

            //若不能在债券信息设置中找到当前债券的信息 则 提示用户维护当前债券的信息
            if(((String)hmZQRate.get("haveInfo")).equals("false")){
            	throw new YssException("请设置 " +securityCode+ " 的相关债券信息！");
            }
            
            //获取税后百元债券利息
            bigInt100 = new BigDecimal((String)hmZQRate.get("GZLX"));
            //获取税前百元债券利息
//            bigBefInt100 = new BigDecimal((String)hmZQRate.get("SQGZLX"));

        dMarketPrice = YssD.sub(new BigDecimal(dMarketPrice + ""), bigInt100);
        dMarketPrice = YssD.round(dMarketPrice, 2);
        
        return dMarketPrice;
        
    	}catch(Exception e){
    		throw new YssException("获取债券："+securityCode+" 净价行情出错......");
    	}
    }
    private double caculateBondCGT(String securityCode, double accruedinterest,double tradeMoney,
    		  						double cost,double dCGTrate) throws YssException{    	
    	ResultSet rs = null;
//    	HashMap  map =new HashMap();
    	String sql="";
    	double money=0;
    	try{
    		int quoteWay=1;//0-全价；1-净价
    		sql="select FQuoteWay from "+pub.yssGetTableName("Tb_Para_FixInterest")
    		   +" where fcheckstate=1 and FSecurityCode="+dbl.sqlString(securityCode);
    		rs=dbl.openResultSet(sql);
    		if(rs.next()){
    			quoteWay=rs.getInt("FQuoteWay");
    		}
    		if(quoteWay==0){
    			//1.全价计算方式为:（成交金额- 成本-利息）*费率	
    			money =YssD.mul(YssD.sub(tradeMoney, cost, accruedinterest),dCGTrate);
    		}else{
    			//2.净价计算方式为： (成交金额- 成本) * 费率
    			money =YssD.mul(YssD.sub(tradeMoney, cost),dCGTrate);
    		}		    
    		return money;
    	}catch(Exception e){
    		throw new YssException("计算债券已实现资本利得税出错！",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
}
