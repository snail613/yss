package com.yss.main.operdeal.opermanage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.yss.commeach.EachRateOper;
import com.yss.main.operdeal.income.paid.PaidAccIncome;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.pojo.dayfinish.AccPaid;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 现金账户结息.
 * @author ldaolong
 * 
 */
public class OperAutoCashPay extends BaseOperManage {

	/**
	 * 执行业务处理
	 */
	public void doOpertion() throws YssException {
		PaidAccIncome paidAccIncome = new PaidAccIncome();
		paidAccIncome.setYssPub(pub);
		paidAccIncome.initIncomeCalculate(null, dDate, null, sPortCode, "\t06DE");

		// 先获取自动结息账户设置对应的账户代码和节假日群代码，
		Map autoCashMap = getAutoCashAccount();

		// 若设置了节假日群代码，需判断业务日期是否为节假日，若是的话，就不作处理，如不是，
		// 则调用com.yss.main.operdeal.income.paid.PaidAccIncome.getDayIncomes(java.util.Date
		// dDate),查询需要做收益支付的数据
		ArrayList incomeList = new ArrayList();
		if (autoCashMap != null) {
			Set set = autoCashMap.entrySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String cashAccountCode = (String) entry.getKey();// 现金账户代码
				String holidayCode = (String) entry.getValue();// 节假日代码
				if (holidayCode.equals("") || !isHoliday(holidayCode, dDate)) {				

					incomeList=getNoCashData(cashAccountCode);
					 
					if (incomeList.size() !=0){
						paidAccIncome.saveIncome(incomeList, "", false);	
					}					
				}

			}
		}
		
		//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
		//当日产生数据，则认为有业务。
		if(incomeList ==null || incomeList.size()==0){
			this.sMsg="        当日无业务";
		}

	}

	/**
	 * 初始化信息
	 */
	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.dDate = dDate; // 调拨日期
		this.sPortCode = portCode; // 组合

	}

	/**
	 * 获取自动结息账户设置对应的账户代码和节假日群代码
	 * 
	 * @throws YssException
	 */
	private Map getAutoCashAccount() throws YssException {
		Map autoCashPayMap = new HashMap();
		ResultSet rs = null;
		String strSql = "select m.fctlcode as cashAccount,n.fctlcode as holidays,"
				+ "m.fctlvalue as cashAccountCode,n.fctlvalue as holidaysCode"
				+ " from "
				+ pub.yssGetTableName("TB_PFOper_PUBPARA")
				+ " m"
				+ " left join "
				+ pub.yssGetTableName("TB_PFOper_PUBPARA")
				+ " n  on"
				+ "  (m.fparaid = n.fparaid)"
				+ " where m.FPubParaCode = 'autoCashpaying'"
				+ " and m.FParaGroupCode = 'operationDeal'"
				+ " and m.FCtlGrpCode = 'autoCashpaying'"
				+ " and m.fctlcode ='accountCode'"
				+ " and n.fctlcode = 'holidaysCode'";

		try {
			
			rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			while (rs.next()) {
				if (!autoCashPayMap.containsKey(rs.getString("cashAccountCode").split("\\|")[0])){				
				autoCashPayMap.put(rs.getString("cashAccountCode").split("\\|")[0], rs
						.getString("holidaysCode").equals("|")?"":rs
								.getString("holidaysCode").split("\\|")[0]);
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return autoCashPayMap;

	}

	/**
	 * 判断传入的日期是否是节假日
	 * 
	 * @param dDate
	 * @return
	 * @throws YssException
	 */
	private boolean isHoliday(String holidaysCode, java.util.Date dDate)
			throws YssException {
		boolean isHoliday = false;
		ResultSet rs = null;

	
		String strSql = "select FDate from Tb_Base_ChildHoliday where FHolidaysCode ="
				+ dbl.sqlString(holidaysCode)
				+ " and FCheckState=1 and FDate = " + dbl.sqlDate(dDate, false);
		try {
			rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			if (rs.next()) {
				isHoliday = true;
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage() /* "访问节假日表出错！" */);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return isHoliday;
	}

	/**
	 * 获取没有库存但有应收应付数据。
	 * @return
	 */
	private ArrayList getNoCashData(String cashAccountCode){
		String strSql=builderSql(cashAccountCode);
		ArrayList list = null;
		ResultSet rs = null;
		
		try {
			rs = dbl.queryByPreparedStatement(strSql);
			list = setResultData(rs,cashAccountCode);
		} catch (Exception e) {

		} 
				
		return list;
	}
	
	/**
	 * 374 QDV4国泰2010年12月02日01_A
	 * lidaolong 2011.02.16
	 * @param cashAccountCode
	 * @return
	 */
	private String builderSql(String cashAccountCode){
		String strSql="select distinct a. FBal,a.FAttrclsCode,a.FPortCode,a.FMoney, port.FPortName,port.FPortCury, tsf.FTsfTypeName,subtsf.FSubTsfTypeName," +
                		" cury.fcurycode,cury.FCuryName,acc.FCashAccName,h.fassetgroupcode, h.fassetgroupname "
                	+" from (select distinct nvl(aa.FCuryCode,stock.FCuryCode) as FCuryCode" +
                				", nvl(aa.FPortCode,stock.FPortCode) as FPortCode" +
                				",nvl(aa.FAttrclsCode,stock.FAttrclsCode) as FAttrclsCode" +
                				", nvl(aa.FCashAccCode,stock.FCashAccCode) as FCashAccCode" +
                				",aa.FMoney,stock.fbal "	
						+" from (" +
							"select distinct FPortCode,FCuryCode,FAttrclsCode,FCashAccCode,sum(FMoney) as FMoney  from " +  pub.yssGetTableName("Tb_Data_CashPayRec") 
								+" where fcheckstate = 1 and FTransDate= "+dbl.sqlDate(this.dDate)
								+" and fcashacccode ="+dbl.sqlString(cashAccountCode)
								+" and ftsftypecode='06' and fsubtsftypecode='06DE'"
								+" and FPortCode in (" +operSql.sqlCodes(sPortCode) + ")"
								+" group by FPortCode, FCuryCode, FCashAccCode,FAttrclsCode,FMoney) aa"
							+" full join (select * from " + pub.yssGetTableName("Tb_Stock_CashPayRec") +//关联库存
	                						" where FTsfTypeCode = '06'" +          
	                						" and FSubTsfTypeCode in ('06DE')"  + 
	                						"  and fcheckstate=1 "+
	                						" and " + operSql.sqlStoragEve(dDate) +
	                						" and fcashacccode="+dbl.sqlString(cashAccountCode)+
	                						" and FPortCode in (" +operSql.sqlCodes(sPortCode) + ") ) stock" +
	                						" on (stock.FPortCode = aa.FPortCode and aa.FAttrclsCode=stock.FAttrclsCode) ) a"	 
	                			
					+" left join (select FPortCode, FPortName,FPortCury from " +//关联组合
				     				pub.yssGetTableName("tb_para_portfolio") +
				                " where FCheckState = 1) port on a.FPortCode = port.FPortCode "
				    +  " left join (select FCuryCode, FCuryName from " +
				    				pub.yssGetTableName("tb_para_currency") +
				    				" where FCheckState = 1) cury on a.FCuryCode = cury.FCuryCode"               
	               + " left join (select FCashAccCode,FCashAccName from " +//关联现金账户
	                				pub.yssGetTableName("Tb_Para_CashAccount") +
	                				" where FCheckState = 1"	            
	                				+") acc on a.FCashAccCode = acc.FCashAccCode"  
	                
	                +  "  left join  tb_base_transfertype tsf  on tsf.FTsfTypeCode = '06'" +	               
	                " left join  tb_base_subtransfertype  subtsf on subtsf.FSubTsfTypeCode = '06DE'" 			
				    +" left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
	                pub.getPrefixTB() + "' "  ;
		
		return strSql;
	}
	/**
	 * 封装数据
	 * 374 QDV4国泰2010年12月02日01_A
	 * lidaolong 2011.02.16
	 * @param rs
	 * @param cashAccountCode
	 * @return
	 */
	private ArrayList setResultData(ResultSet rs,String cashAccountCode){
		ArrayList list = new ArrayList();
		 AccPaid paid = null;
		 double baseCuryRate;
	     double portCuryRate;
	     EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
	        rateOper.setYssPub(pub);
	        CtlPubPara pubpara = new CtlPubPara();
	        pubpara.setYssPub(pub);
	        try{
		 while (rs.next()) {

             paid = new AccPaid(pub);
           
             paid.setAssetGroupCode(rs.getString("fassetgroupcode"));
             paid.setAssetGroupName(rs.getString("fassetgroupname"));
          
             paid.setDDate(dDate);
             paid.setPortCode(rs.getString("FPortCode"));
             paid.setPortName(rs.getString("FPortName"));
           //  paid.setInvmgrCode(rs.getString("FAnalysisCode1") + "");
           //  paid.setInvmgrName(rs.getString("FInvMgrName"));
            // paid.setCatCode(rs.getString("FAnalysisCode2") + "");
            // paid.setCatName(rs.getString("FCatName"));
             paid.setTsfTypeCode("06");
             paid.setTsfTypeName(rs.getString("FTsfTypeName"));
             paid.setSubTsfTypeCode("06DE");
             paid.setSubTsfTypeName(rs.getString("FSubTsfTypeName"));
             paid.setCuryCode(rs.getString("FCuryCode"));
             paid.setCuryName(rs.getString("FCuryName"));
             paid.setCashAccCode(cashAccountCode);
             paid.setCashAccName(rs.getString("FCashAccName"));
             paid.setAttrClsCode(rs.getString("FAttrclsCode"));
          
             	//金额为T-1日现金应收应付库存中的应收利息A和T日发生的应收利息B
             	paid.setMoney(YssD.add(rs.getDouble("FBal"),rs.getDouble("FMoney")));
           
           
           //  paid.setMatureDate(rs.getDate("FMatureDate"));
             //=========================================================================
             //付息的基础货币、组合货币应该是按照当日的汇率来计算的。 fazmm20070926
             //当天的基础汇率
             baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                 rs.getString("FCuryCode"), paid.getPortCode(),
                 YssOperCons.YSS_RATE_BASE);
             //当天的组合汇率
          
             rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                       paid.getPortCode());
             portCuryRate = rateOper.getDPortRate();
            
             //--------------------------获取原币的数据位数。再通过它来设置其它金额的数据保留位数。为了冲抵应收的金额。
             int Digit = this.getSettingOper().getRoundDigit(paid.getMoney());
             paid.setBaseMoney(this.getSettingOper().calBaseMoney(paid.getMoney(),
                 baseCuryRate, Digit));
             paid.setPortMoney(this.getSettingOper().calPortMoney(paid.getMoney(),
                 baseCuryRate, portCuryRate,
                 //增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                 rs.getString("FCuryCode"), dDate, paid.getPortCode(), Digit));
           
             paid.setBaseCuryRate(baseCuryRate);
             paid.setPortCuryRate(portCuryRate);
        
             list.add(paid);
      
         }
	    }catch(Exception ex){
	        	
	   }
       
		return list;
	}
}
