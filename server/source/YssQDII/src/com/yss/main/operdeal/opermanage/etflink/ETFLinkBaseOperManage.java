package com.yss.main.operdeal.opermanage.etflink;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.SecRecPayAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * add by huangqirong 2012-05-13 story #2565 ETF联接基金
 * */
public class ETFLinkBaseOperManage extends BaseOperManage {
	@Override
	public void doOpertion() throws YssException {
		try{
			this.createSecRecPay();			
			/**
			 * add huangqirong 2012-07-07 华夏联接基金 story #2727
			 * */	
			//---delete by songjie 2012.11.12 STORY #3214 需求深圳-[易方达基金]QDV4.0[紧急]20121030001 start---//
//			ETFLinkDVPBaseOperManage dvpEtf = new ETFLinkDVPBaseOperManage();
//			dvpEtf.setYssPub(this.pub);
//			dvpEtf.initOperManageInfo(this.dDate, this.sPortCode);
//			dvpEtf.doOpertion();
			//---delete by songjie 2012.11.12 STORY #3214 需求深圳-[易方达基金]QDV4.0[紧急]20121030001 end---//
			//---end---
			
		}catch(Exception e){
			throw new YssException("ETF联接基金业务处理失败！",e);
		}
	}
	
	@Override
	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
	}

	private void createSecRecPay() throws YssException {
 	   ResultSet rs = null;	
 	   CashPecPayBean cashpecpay = null;
 	   CashPayRecAdmin cashpecpayAdmin = new CashPayRecAdmin(); 
 	   double baseRate = 0;
 	   double portRate = 0;
 	   double money = 0;
 	   double baseMoney = 0.0;
 	   double portMoney = 0.0;
 	   try{
 		  String sql = "select dsbd.* ,tpca.FCuryCODE as FCuryCODE from (select * from " 
		  + pub.yssGetTableName("Tb_Data_Subtrade") +
			 " where Ftradetypecode in ('106', '107', '202', '203', '204', '205') "+
			 " and FCheckState = '1' and Fbargaindate =  " 
			 + dbl.sqlDate(this.dDate)
			 + " and FPortCode in (" 
			 + operSql.sqlCodes(sPortCode) 
			 + ") )dsbd "
			 + " left join (select tpca2.* " 
			 + " from (select FCASHACCCODE, max(FSTARTDATE) as FSTARTDATE "
			 + " from "
			 + pub.yssGetTableName("Tb_Para_CashAccount")
			 + " where Fcheckstate = 1 "
			 +" group by FCASHACCCODE) tpca1 " 
			 + " left join " 
			 + pub.yssGetTableName("Tb_Para_CashAccount") 
			 + " tpca2 on tpca1.FCASHACCCODE = "+
			 " tpca2.fcashacccode  and tpca1.FSTARTDATE = tpca2.FSTARTDATE) tpca " +
			 " on dsbd.FCASHACCCODE = tpca.FCASHACCCODE";
 		  
 		   rs = dbl.openResultSet(sql);
 		   while (rs.next()){
 			   cashpecpay = new CashPecPayBean();
 			   String tradeTypeCode = rs.getString("Ftradetypecode");
 			   money = rs.getDouble("FCANRETURNMONEY");
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
		   		
		   		baseMoney = this.getSettingOper().calBaseMoney(money,baseRate);
	 			portMoney = this.getSettingOper().calPortMoney(
						 					  money,
						 					  baseRate,
						 					  portRate,
						                      rs.getString("FCuryCode"),
						                      dDate,
						                      sPortCode); 		   		
 			   cashpecpay.setTradeDate(this.dDate);//业务日期
 			   cashpecpay.setNum(rs.getString("FNum"));
 			   cashpecpay.setRelaNum(rs.getString("FNum"));//关联编号
 			   cashpecpay.setRelaNumType("ETFLJ");//关联编号类型
 			   cashpecpay.setMoney(money);
 			   cashpecpay.setDataSource(0);//来源标志 modify by fangjiang 2012.12.24
 			   cashpecpay.checkStateId = 1;
 			   cashpecpay.setPortCode(this.sPortCode);//组合代码  			 
 			   cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));//现金账户
 			   cashpecpay.setCuryCode(rs.getString("FCuryCode"));//币种代码
 			   cashpecpay.setInOutType(1);//方向
 			   cashpecpay.setBaseCuryRate(baseRate);//基础汇率
 			   cashpecpay.setPortCuryRate(portRate);//组合汇率
 			   
 			   cashpecpay.setBaseCuryMoney(baseMoney);
 			   cashpecpay.setPortCuryMoney(portMoney);
			   
			   if("106".equalsIgnoreCase(tradeTypeCode)){ //申购
				   cashpecpay.setTsfTypeCode("06");
				   cashpecpay.setSubTsfTypeCode("06CBCB");
			   }else if("107".equalsIgnoreCase(tradeTypeCode)){//赎回
				   cashpecpay.setTsfTypeCode("06");
				   cashpecpay.setSubTsfTypeCode("06CR");
			   }else if("202".equalsIgnoreCase(tradeTypeCode)){//申购退补款
				   cashpecpay.setTsfTypeCode("02");
	 			   cashpecpay.setSubTsfTypeCode("02CBCB");
			   }else if("203".equalsIgnoreCase(tradeTypeCode)){//赎回退补款
				   cashpecpay.setTsfTypeCode("02");
	 			   cashpecpay.setSubTsfTypeCode("02CR");  
			   }else if("204".equalsIgnoreCase(tradeTypeCode)){//申购失败冲销
				  cashpecpay.setTsfTypeCode("06");
	 			   cashpecpay.setSubTsfTypeCode("06CBCB");
			   }else if("205".equalsIgnoreCase(tradeTypeCode)){//赎回失败冲销
				   cashpecpay.setTsfTypeCode("06");
	 			   cashpecpay.setSubTsfTypeCode("06CR");
			   }
 			   cashpecpayAdmin.addList(cashpecpay);
 		   }
 		   cashpecpayAdmin.setYssPub(pub);
 		   //modify by fangjiang 2012.12.24
 		   cashpecpayAdmin.insert(this.dDate, "06,02", "06CBCB,06CR,02CBCB,02CR", this.sPortCode, -1, false, "", "ETFLJ", false);
			/** shashijie 2012-6-8 BUG 4737 无业务提示 */
			if (cashpecpayAdmin.getAddList().isEmpty()) {
				this.sMsg = "    当日无业务";
			}
			/** end */
 		   
 	    } catch (Exception e) {
            throw new YssException("获取ETF联接基金数据出错！", e);
        } finally {
     	    dbl.closeResultSetFinal(rs);
        }
    }
}
