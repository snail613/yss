package com.yss.main.operdeal.report.navseclend;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.yss.main.operdeal.report.navseclend.pojo.NavSecLendBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;

public class NavSecLendIn extends BaseNavSecLend {

	public NavSecLendIn() {
	}
	
	protected void initRep() {
		this.sRepType = "-1";//初始化处理报表为借入证券净值表
		this.valDefine = "FCatCode;FSubCatCode;FAttrClsCode;FCuryCode;FSecurityCode";//初始化分级字段
	}
	
	public void buildNavData(ArrayList valueMap) throws YssException {
		//查询证券借贷净值数据并插入到证券借贷净值表中
		insertToTempTb(getRepData());
	}
	
	protected ArrayList getGradeData(String groupStr, int Grade) throws YssException {
        ArrayList gradeDataAry = null;
        String OrderStr = "";
        String strSql = "";
        String sSql = "";
        String strDeSql = "";
        ResultSet rs = null;
        NavSecLendBean secLend = null;
        try {
        	gradeDataAry = new ArrayList();
			OrderStr = buildOrderStr(groupStr);
			//============
			String sFields = "FSECURITYCODE, FBARGAINDATE, FTRADETYPECODE, FSETTLEDATE, FPORTCODE, FBROKERCODE, FATTRCLSCODE," +
					" FCASHACCCODE, FAGREEMENTTYPE, FTRADEAMOUNT, FTRADEPRICE, FTOTALCOST,  FCOST";
			String strCalSql = "select distinct * from(" +
					" select (" + OrderStr + " || '##' || dat.FSubTsfTypeCode) as FOrder, dat.*," +
					" stft.FSubTsfTypeName, (dat.FTradeAmountAl-dat.FReturnedAmount) as amount, (dat.FTradePriceAl-dat.FReturnedPrice) as price from (" +
					" select a.FPortCuryRate, a.FBaseCuryRate, k.FMarketPrice, a.FCuryCode," +
					" b.*, c.FSecurityName, c.FCatCode, d.FCatName, c.FSubCatCode," +
					" e.FSubCatName, c.FIsinCode, c.FExternalCode, f.FCuryName, NVL(rbsb.FReturnedAmount, 0) as FReturnedAmount, NVL(rbsb.FReturnedPrice, 0) as FReturnedPrice," +
					" (case when FTradeTypeCode='BInPaySec' then '07MBI'" +
		            " when FTradeTypeCode='BInPayDid' then '07BDID'" +
		            " when FTradeTypeCode='BInPayOP' then '07AW' end) as FSubTsfTypeCode from" +
					" (select * from " + pub.yssGetTableName("Tb_Stock_Security") + 
					" where FPortCode = " + dbl.sqlString(this.sPortCode) +
					" and FStorageDate = " + dbl.sqlDate(this.sLendDate) + " and FCheckState = 1) a" +
					" left join " +
					" (select " + sFields + ", NVL(sum(FTradeAmount), 0) as FTradeAmountAl, NVL(sum(FTradePrice), 0) as FTradePriceAl from " + pub.yssGetTableName("TB_DATA_SecLendTrade") +
					" where FPortCode = " + dbl.sqlString(this.sPortCode) +
					" and FBargainDate <= " + dbl.sqlDate(this.sLendDate) + " and FCheckState = 1 and FTradeTypeCode in ('BInPaySec', 'BInPayDid', 'BInPayOP')" +
					" group by " + sFields + ") b" +
					" on a.FSecurityCode = b.FSecurityCode and a.FAttrClsCode = b.FAttrClsCode" +
					" left join " + pub.yssGetTableName("Tb_Para_Security") + " c" +
					" on a.FSecurityCode = c.FSecurityCode" +
					" left join Tb_Base_Category d" +
					" on c.FCatCode = d.FCatCode" +
					" left join Tb_Base_SubCategory e" +
					" on c.FSubCatCode = e.FSubCatCode" +
					" left join " + pub.yssGetTableName("Tb_Para_Currency") + " f" +
					" on a.FCuryCode = f.FCuryCode" +
					" left join (select a.FSecurityCode, a.FAttrClsCode, NVL(sum(FTradeAmount), 0) as FReturnedAmount, NVL(sum(FTradePrice), 0) as FReturnedPrice, b.FBrokerCode," +
					" (case when FTradeTypeCode='Rbsb' then 'BInPaySec'" +
		            " when FTradeTypeCode='Drb' then 'BInPayDid'" +
		            " when FTradeTypeCode='Awrb' then 'BInPayOP' end) as FReturnToTypeCode from" +
					" (select * from " + pub.yssGetTableName("Tb_Stock_Security") + 
					" where FPortCode = " + dbl.sqlString(this.sPortCode) +
					" and FStorageDate = " + dbl.sqlDate(this.sLendDate) + ") a" +
					" left join " +
					" (select * from " + pub.yssGetTableName("TB_DATA_SecLendTrade") + 
					" where FPortCode = " + dbl.sqlString(this.sPortCode) +
					" and FCheckState = 1" +
					" and FBargainDate <= " + dbl.sqlDate(this.sLendDate) + ") b" +
					" on a.FSecurityCode = b.FSecurityCode and a.FAttrClsCode = b.FAttrClsCode" +
					" where FTradeTypeCode  in ('Rbsb', 'Drb', 'Awrb')" +
					" group by a.FSecurityCode, a.FAttrClsCode, b.FBrokerCode, b.FTradeTypeCode) rbsb" +
					" on a.FSecurityCode = rbsb.FSecurityCode and a.FAttrClsCode = rbsb.FAttrClsCode and b.FBrokerCode = rbsb.FBrokerCode" +
					" and b.FTradeTypeCode = rbsb.FReturnToTypeCode" +
					
					" LEFT JOIN (SELECT i.FValDate, i.FPortCode, i.FSecurityCode, i.FPrice as FMarketPrice, i.FAttrClsCode" +
		            " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") + " i" +
		            " JOIN (SELECT MAX(FValDate) AS FValDate, FSecurityCode, FAttrClsCode" +
		            " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") +
		            " WHERE FValDate <= " + dbl.sqlDate(this.sLendDate) +
		           	" and FPortCode = "+dbl.sqlString(this.sPortCode) +
		            " GROUP BY FSecurityCode, FAttrClsCode) j ON i.FValDate = j.FValDate" +
		            " AND i.FSecurityCode = j.FSecurityCode AND i.Fattrclscode = j.FAttrClsCode) k" +
		            " on a.FSecurityCode = k.FSecurityCode and a.FAttrClsCode = k.FAttrClsCode" +
		            
					" where FTradeTypeCode in ('BInPaySec', 'BInPayDid', 'BInPayOP')" +
					((!this.sBrokerCode.equalsIgnoreCase("total")) ? (" and b.FBrokerCode = " + dbl.sqlString(this.sBrokerCode)) : "") +
					" ) dat" +
					" left join Tb_Base_SubTransferType stft on dat.FSubTsfTypeCode = stft.FSubTsfTypeCode" +
					" ) where (amount > 0 and FSubTsfTypeCode in ('07AW', '07MBI')) or (price > 0 and FSubTsfTypeCode in ('07BDID'))";
			if(this.sBrokerCode.equalsIgnoreCase("total")) {
				String sField = "FOrder, FBargainDate, FPortCode, FSecurityCode, FSubTsfTypeCode, FSubTsfTypeName, FCuryCode, FExternalCode," +
								" FIsinCode, FTradeAmount, FMarketPrice, FBaseCuryRate, FPortCuryRate, FTradeTypeCode, FCatCode, FAttrClsCode";
				strCalSql = "select sum(amount) as amount, sum(price) as price, '' as FBrokerCode, " + 
						 sField + " from (" + strCalSql + ") group by " + sField;
			}
			//================
			strDeSql = "select a.FPortCuryRate, a.FBaseCuryRate, a.FanalYsisCode2 as FBrokerCode," +
            		" b.FSubTsfTypeCode, b.FStorageDate as FBargainDate, b.FPortCode, b.FSecurityCode, b.FCuryCode, b.FBal, b.FAmount, b.FPortCuryBal, b.FAttrClsCode," +
					" c.FSecurityName, c.FCatCode, d.FCatName, c.FSubCatCode," +
					" e.FSubCatName, c.FIsinCode, c.FExternalCode, f.FCuryName, h.FSubTsfTypeName, k.FMarketPrice, l.FAttrClsName" +
					" from" + 
		            " (select * from " + pub.yssGetTableName("Tb_Stock_Security") + " where FPortCode = " + dbl.sqlString(this.sPortCode) +
		            " and FStorageDate = " + dbl.sqlDate(this.sLendDate) + " and FCheckState = 1) a" +
		            " left join " +
		            " (select * from " + pub.yssGetTableName("Tb_Stock_SecrecPay") + " where FPortCode = " + dbl.sqlString(this.sPortCode) +
		            " and FStorageDate = " + dbl.sqlDate(this.sLendDate) + " and FCheckState = 1) b" +
		            " on a.FSecurityCode = b.FSecurityCode and a.FAttrClsCode = b.FAttrClsCode and a.FCatType = b.FCatType and a.FAnalysisCode2 = b.FAnalysisCode2" +
		            
		            " left join " + pub.yssGetTableName("Tb_Para_Security") + " c" +
		            " on a.FSecurityCode = c.FSecurityCode" +
		            " left join Tb_Base_Category d on c.FCatCode = d.FCatCode" +
		            " left join Tb_Base_SubCategory e on c.FSubCatCode = e.FSubCatCode" +
		            " left join " + pub.yssGetTableName("Tb_Para_Currency") + " f on b.FCuryCode = f.FCuryCode" +
		            " left join Tb_Base_SubTransferType h on b.FSubTsfTypeCode = h.FSubTsfTypeCode" +
		            
		            " LEFT JOIN (SELECT i.FValDate, i.FPortCode, i.FSecurityCode, i.FPrice as FMarketPrice, i.FAttrClsCode" +
	                " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") + " i" +
	                " JOIN (SELECT MAX(FValDate) AS FValDate, FSecurityCode, FAttrClsCode" +
	                " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") +
	                " WHERE FValDate <= " + dbl.sqlDate(this.sLendDate) +
	               	" and FPortCode = "+dbl.sqlString(this.sPortCode) +
	                " GROUP BY FSecurityCode, FAttrClsCode) j ON i.FValDate = j.FValDate" +
	                " AND i.FSecurityCode = j.FSecurityCode AND i.Fattrclscode = j.FAttrClsCode) k" +
	                " on a.FSecurityCode = k.FSecurityCode and a.FAttrClsCode = k.FAttrClsCode" +
	                " left join " + pub.yssGetTableName("Tb_Para_AttributeClass") + " l on a.FAttrClsCode = l.FAttrClsCode" +
	                
	                " union" +
	                " select a.FPortCuryRate, a.FBaseCuryRate, a.FanalYsisCode2 as FBrokerCode," +
	                " b.FSubTsfTypeCode, b.FBargainDate, b.FPortCode, b.FSecurityCode, b.FCuryCode, 0 as FBal, b.amount as FAmount, 0 as FPortCuryBal, b.FAttrClsCode," +
					" c.FSecurityName, c.FCatCode, d.FCatName, c.FSubCatCode," +
					" e.FSubCatName, c.FIsinCode, c.FExternalCode, f.FCuryName, h.FSubTsfTypeName, k.FMarketPrice, l.FAttrClsName" +
					" from" + 
		            " (select * from " + pub.yssGetTableName("Tb_Stock_Security") + " where FPortCode = " + dbl.sqlString(this.sPortCode) +
		            " and FStorageDate = " + dbl.sqlDate(this.sLendDate) + " and FCheckState = 1) a" +
					" join (select * from (" + strCalSql + ") where FSubTsfTypeCode = '07AW') b on a.FSecurityCode = b.FSecurityCode" +
					((!this.sBrokerCode.equalsIgnoreCase("total")) ? (" and a.FanalYsisCode2 = b.FBrokerCode") : "") +
					" left join " + pub.yssGetTableName("Tb_Para_Security") + " c" +
		            " on a.FSecurityCode = c.FSecurityCode" +
		            " left join Tb_Base_Category d on c.FCatCode = d.FCatCode" +
		            " left join Tb_Base_SubCategory e on c.FSubCatCode = e.FSubCatCode" +
		            " left join " + pub.yssGetTableName("Tb_Para_Currency") + " f on a.FCuryCode = f.FCuryCode" +
		            " left join Tb_Base_SubTransferType h on b.FSubTsfTypeCode = h.FSubTsfTypeCode" +
		            
		            " LEFT JOIN (SELECT i.FValDate, i.FPortCode, i.FSecurityCode, i.FPrice as FMarketPrice, i.FAttrClsCode" +
	                " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") + " i" +
	                " JOIN (SELECT MAX(FValDate) AS FValDate, FSecurityCode, FAttrClsCode" +
	                " FROM " + pub.yssGetTableName("TB_Data_ValMktPrice") +
	                " WHERE FValDate <= " + dbl.sqlDate(this.sLendDate) +
	               	" and FPortCode = "+dbl.sqlString(this.sPortCode) +
	                " GROUP BY FSecurityCode, FAttrClsCode) j ON i.FValDate = j.FValDate" +
	                " AND i.FSecurityCode = j.FSecurityCode AND i.Fattrclscode = j.FAttrClsCode) k" +
	                " on a.FSecurityCode = k.FSecurityCode and a.FAttrClsCode = k.FAttrClsCode" +
	                " left join " + pub.yssGetTableName("Tb_Para_AttributeClass") + " l on a.FAttrClsCode = l.FAttrClsCode";
			
			if(this.sBrokerCode.equalsIgnoreCase("total")) {
				String sField = "FPortCuryRate, FBaseCuryRate, FSubTsfTypeCode, FBargainDate, FPortCode, FSecurityCode, FCuryCode, FBal," +
						" FPortCuryBal, FAttrClsCode, FSecurityName, FCatCode, FCatName, FSubCatCode, FSubCatName, FIsinCode, FExternalCode," +
						" FCuryName, FSubTsfTypeName, FMarketPrice, FAttrClsName";
				strDeSql = "select " + sField + ", sum(FAmount) as FAmount, '' as FBrokerCode" + 
						" from (" + strDeSql + ") group by FBrokerCode," + sField;
			}
			
			sSql = "select (" + OrderStr + ") as FOrder, dat.* from (" +
				 strDeSql +
				 ((!this.sBrokerCode.equalsIgnoreCase("total")) ? (" where a.FanalYsisCode2 = " + dbl.sqlString(this.sBrokerCode)) : "") + 
				 ") dat" +
				 " where FSubTsfTypeCode in ('10BSC', '07AW')" +
				 ((!this.sBrokerCode.equalsIgnoreCase("total")) ? (" and FBrokerCode = " + dbl.sqlString(this.sBrokerCode)) : "");
	
			if(Grade == 5) {
				//应付送股、应付借入股利
				strSql = "select * from (" + strCalSql + ") where FCatCode <> 'OP'";
				rs = dbl.openResultSet(strSql);
				String sType = "";
				while(rs.next()) {
					sType = rs.getString("FSubTsfTypeCode");
					secLend = new NavSecLendBean();
					secLend.setNavDate(this.sLendDate);
					secLend.setPortCode(rs.getString("FPortCode"));
					secLend.setSecLendType(this.sRepType);
					secLend.setOrderKeyCode(rs.getString("FOrder"));
					secLend.setInOut(-1);
					secLend.setReTypeCode("");
					secLend.setInvMgrCode(this.sBrokerCode);
					secLend.setDetail(0);
					secLend.setKeyCode(rs.getString("FSecurityCode") + "-" + rs.getString("FSubTsfTypeCode"));
					secLend.setKeyName(setBlo(Grade + 1) + rs.getString("FSubTsfTypeName"));
					secLend.setCuryCode(rs.getString("FCuryCode"));
					secLend.setSedolCode(rs.getString("FExternalCode"));
					secLend.setIsinCode(rs.getString("FIsinCode"));
					secLend.setSparAmt(rs.getDouble("amount"));
					secLend.setPrice(rs.getDouble("FMarketPrice"));
					secLend.setOtPrice1(0.00);
					secLend.setOtPrice2(0.00);
					secLend.setOtPrice3(0.00);
					secLend.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
					secLend.setPortCuryRate(rs.getDouble("FPortCuryRate"));
					if(sType.equalsIgnoreCase("07BDID")) {
						secLend.setBookCost(rs.getDouble("price"));
						secLend.setMarketValue(rs.getDouble("price"));
						double baseRate = this.getSettingOper().getCuryRate(this.sLendDate, rs.getString("FCuryCode"), this.sPortCode, YssOperCons.YSS_RATE_BASE);
						double portRate = this.getSettingOper().getCuryRate(this.sLendDate, rs.getString("FCuryCode"), this.sPortCode, YssOperCons.YSS_RATE_PORT);
						double portCost = YssD.div(YssD.mul(secLend.getBookCost(), baseRate), portRate);
						secLend.setPortBookCost(portCost);
						secLend.setPortMarketValue(portCost);
						
						NavSecLendBean totalBean = new NavSecLendBean();
						if(hm.containsKey("all")) {
							totalBean = (NavSecLendBean)hm.get("all");
						}
						//统计应付借贷股利的本位币市值到资产净值
						totalBean.setTotalPortMarketValue(YssD.sub(totalBean.getTotalPortMarketValue(), secLend.getPortBookCost()));
						hm.put("all", totalBean);
					}
					gradeDataAry.add(secLend);
				}
				
				//证券借入成本
				String sField = "FPortCode, FSubTsfTypeCode, FSubTsfTypeName, FSecurityCode, FSecurityName, FCuryCode, FExternalCode, FIsinCode," +
					" FMarketPrice, FBaseCuryRate, FPortCuryRate, FMarketPrice, FBaseCuryRate, FPortCuryRate, FCatCode, FSubCatCode, FAttrClsCode";
				strSql = "select FOrder, sum(FBal) as FBal, sum(FPortCuryBal) as FPortCuryBal, sum(FAmount) as FAmount, " + sField + 
					" from (" + sSql + ") group by FOrder, " + sField;
				rs = dbl.openResultSet(strSql);
				while(rs.next()) {
					secLend = new NavSecLendBean();
					secLend.setNavDate(this.sLendDate);
					secLend.setPortCode(rs.getString("FPortCode"));
					secLend.setSecLendType(this.sRepType);
					secLend.setOrderKeyCode(rs.getString("FOrder"));
					secLend.setInOut(1);
					secLend.setReTypeCode("");
					secLend.setInvMgrCode(this.sBrokerCode);
					secLend.setDetail(0);
					if(rs.getString("FSubTsfTypeCode").trim().equalsIgnoreCase("07AW")) {
						secLend.setKeyCode(rs.getString("FSecurityCode") + "-" + rs.getString("FSubTsfTypeCode"));
						secLend.setKeyName(setBlo(Grade) + rs.getString("FSubTsfTypeName"));
					} else {
						secLend.setKeyCode(rs.getString("FSecurityCode"));
						secLend.setKeyName(setBlo(Grade) + rs.getString("FSecurityName"));
					}
					secLend.setCuryCode(rs.getString("FCuryCode"));
					secLend.setSedolCode(rs.getString("FExternalCode"));
					secLend.setIsinCode(rs.getString("FIsinCode"));
					secLend.setSparAmt(rs.getDouble("FAmount"));
					secLend.setPrice(rs.getDouble("FMarketPrice"));
					secLend.setOtPrice1(0.00);
					secLend.setOtPrice2(0.00);
					secLend.setOtPrice3(0.00);
					secLend.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
					secLend.setPortCuryRate(rs.getDouble("FPortCuryRate"));
					secLend.setBookCost(rs.getDouble("FBal"));
					double dMarketValue=YssD.mul(rs.getDouble("FMarketPrice"), rs.getDouble("FAmount"));//原币市值=行情*数量
					secLend.setMarketValue(dMarketValue);
					double dValueAdd=YssD.sub(secLend.getMarketValue(),secLend.getBookCost());//原币估增=市值-成本
					secLend.setPayValue(dValueAdd);
					secLend.setPortBookCost(rs.getDouble("FPortCuryBal"));
					secLend.setPortMarketValue(YssD.div(YssD.mul(dMarketValue, rs.getDouble("FBaseCuryRate")), rs.getDouble("FPortCuryRate")));//本位币市值=原币市值*基础汇率/组合汇率
					secLend.setPortPayValue(YssD.sub(secLend.getPortMarketValue(), secLend.getPortBookCost()));//本位币估增=本位币市值-本位币成本
					//double exBal = YssD.sub(YssD.div(YssD.mul(secLend.getBookCost(), rs.getDouble("FBaseCuryRate")), rs.getDouble("FPortCuryRate")), secLend.getPortBookCost());//成本的汇兑损益=原币成本*基础汇率/组合汇率-本位币成本
					//double exValueAdd = YssD.sub(YssD.div(YssD.mul(dValueAdd, rs.getDouble("FBaseCuryRate")), rs.getDouble("FPortCuryRate")), secLend.getPortPayValue());//估增的汇兑损益=原币估增*基础汇率/组合汇率-本位币估增
					secLend.setPortexchangeValue(0.00);//本位币的汇兑损益=成本汇兑损益+估增的汇兑损益
					gradeDataAry.add(secLend);
					
					//计算数量和原币本位币的成本、市值、估增到上层合计
					NavSecLendBean totalBean = new NavSecLendBean();
					if(hm.containsKey(getKey(rs.getString("FOrder"), Grade - 1))) {
						totalBean = (NavSecLendBean)hm.get(getKey(rs.getString("FOrder"), Grade - 1));
					}
					totalBean.setTotalAmount(YssD.add(totalBean.getTotalAmount(), rs.getDouble("FAmount")));
					totalBean.setTotalBal(YssD.add(totalBean.getTotalBal(), rs.getDouble("FBal")));
					totalBean.setTotalMarketValue(YssD.add(totalBean.getTotalMarketValue(), dMarketValue));
					totalBean.setTotalValueAdd(YssD.add(totalBean.getTotalValueAdd(), dValueAdd));
					totalBean.setTotalPortBal(YssD.add(totalBean.getTotalPortBal(), rs.getDouble("FPortCuryBal")));
					totalBean.setTotalPortMarketValue(YssD.add(totalBean.getTotalPortMarketValue(), YssD.div(YssD.mul(dMarketValue, rs.getDouble("FBaseCuryRate")), rs.getDouble("FPortCuryRate"))));
					totalBean.setTotalPortValueAdd(YssD.add(totalBean.getTotalPortValueAdd(), YssD.sub(secLend.getPortMarketValue(), secLend.getPortBookCost())));
					hm.put(getKey(rs.getString("FOrder"), Grade - 1), totalBean);
				}
			}
			if(Grade == 4) {
				strSql = "select FBargainDate, FPortCode, FOrder, FCuryCode, FCuryName, FBaseCuryRate, FPortCuryRate, FSubCatCode from(" +
						sSql +
						") al group by FCuryCode, FCuryName, FBaseCuryRate, FPortCuryRate, FBargainDate, FPortCode, FOrder, FSubCatCode";
				rs = dbl.openResultSet(strSql);
				NavSecLendBean totalBean = null;
				while(rs.next()) {
					totalBean = new NavSecLendBean();
					if(hm.containsKey(getKey(rs.getString("FOrder"), Grade))) {
						totalBean = (NavSecLendBean)hm.get(getKey(rs.getString("FOrder"), Grade));
					}
					secLend = new NavSecLendBean();
					secLend.setNavDate(this.sLendDate);
					secLend.setPortCode(rs.getString("FPortCode"));
					secLend.setSecLendType(this.sRepType);
					secLend.setOrderKeyCode(rs.getString("FOrder"));
					secLend.setInOut(1);
					secLend.setDetail(Grade);
					secLend.setInvMgrCode(this.sBrokerCode);
					secLend.setKeyCode(rs.getString("FCuryCode"));
					secLend.setKeyName(setBlo(Grade) + rs.getString("FCuryName"));
					secLend.setCuryCode(rs.getString("FCuryCode"));
					secLend.setSparAmt(totalBean.getTotalAmount());
					secLend.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
					secLend.setPortCuryRate(rs.getDouble("FPortCuryRate"));
					secLend.setBookCost(totalBean.getTotalBal());
					secLend.setMarketValue(totalBean.getTotalMarketValue());
					secLend.setPayValue(totalBean.getTotalValueAdd());
					secLend.setPortBookCost(totalBean.getTotalPortBal());
					secLend.setPortMarketValue(totalBean.getTotalPortMarketValue());
					secLend.setPortPayValue(totalBean.getTotalPortValueAdd());
					gradeDataAry.add(secLend);
					
					//计算数量和原币本位币的成本、市值、估增到上层合计
					totalBean = new NavSecLendBean();
					if(hm.containsKey(getKey(rs.getString("FOrder"), Grade - 1))) {
						totalBean = (NavSecLendBean)hm.get(getKey(rs.getString("FOrder"), Grade - 1));
					}
					totalBean.setTotalAmount(YssD.add(totalBean.getTotalAmount(), secLend.getSparAmt()));
					totalBean.setTotalBal(YssD.add(totalBean.getTotalBal(), secLend.getBookCost()));
					totalBean.setTotalMarketValue(YssD.add(totalBean.getTotalMarketValue(), secLend.getMarketValue()));
					totalBean.setTotalValueAdd(YssD.add(totalBean.getTotalValueAdd(), secLend.getPayValue()));
					totalBean.setTotalPortBal(YssD.add(totalBean.getTotalPortBal(), secLend.getPortBookCost()));
					totalBean.setTotalPortMarketValue(YssD.add(totalBean.getTotalPortMarketValue(), secLend.getPortMarketValue()));
					totalBean.setTotalPortValueAdd(YssD.add(totalBean.getTotalPortValueAdd(), secLend.getPortPayValue()));
					hm.put(getKey(rs.getString("FOrder"), Grade - 1), totalBean);
				}
			}
			if(Grade == 3) {
				strSql = "select FBargainDate, FPortCode, FOrder, FAttrClsCode, FAttrClsName from(" +
						sSql +
						") al group by FBargainDate, FPortCode, FOrder, FAttrClsCode, FAttrClsName";
				rs = dbl.openResultSet(strSql);
				NavSecLendBean totalBean = null;
				while(rs.next()) {
					totalBean = new NavSecLendBean();
					if(hm.containsKey(getKey(rs.getString("FOrder"), Grade))) {
						totalBean = (NavSecLendBean)hm.get(getKey(rs.getString("FOrder"), Grade));
					}
					secLend = new NavSecLendBean();
					secLend.setNavDate(this.sLendDate);
					secLend.setPortCode(rs.getString("FPortCode"));
					secLend.setSecLendType(this.sRepType);
					secLend.setOrderKeyCode(rs.getString("FOrder"));
					secLend.setInOut(1);
					secLend.setDetail(Grade);
					secLend.setInvMgrCode(this.sBrokerCode);
					secLend.setKeyCode(rs.getString("FAttrClsCode"));
					secLend.setKeyName(setBlo(Grade) + rs.getString("FAttrClsName"));
					secLend.setCuryCode("汇总：");
					secLend.setSparAmt(totalBean.getTotalAmount());
					secLend.setBookCost(totalBean.getTotalBal());
					secLend.setMarketValue(totalBean.getTotalMarketValue());
					secLend.setPayValue(totalBean.getTotalValueAdd());
					secLend.setPortBookCost(totalBean.getTotalPortBal());
					secLend.setPortMarketValue(totalBean.getTotalPortMarketValue());
					secLend.setPortPayValue(totalBean.getTotalPortValueAdd());
					gradeDataAry.add(secLend);
					
					//计算数量和原币本位币的成本、市值、估增到上层合计
					totalBean = new NavSecLendBean();
					if(hm.containsKey(getKey(rs.getString("FOrder"), Grade - 1))) {
						totalBean = (NavSecLendBean)hm.get(getKey(rs.getString("FOrder"), Grade - 1));
					}
					totalBean.setTotalAmount(YssD.add(totalBean.getTotalAmount(), secLend.getSparAmt()));
					totalBean.setTotalBal(YssD.add(totalBean.getTotalBal(), secLend.getBookCost()));
					totalBean.setTotalMarketValue(YssD.add(totalBean.getTotalMarketValue(), secLend.getMarketValue()));
					totalBean.setTotalValueAdd(YssD.add(totalBean.getTotalValueAdd(), secLend.getPayValue()));
					totalBean.setTotalPortBal(YssD.add(totalBean.getTotalPortBal(), secLend.getPortBookCost()));
					totalBean.setTotalPortMarketValue(YssD.add(totalBean.getTotalPortMarketValue(), secLend.getPortMarketValue()));
					totalBean.setTotalPortValueAdd(YssD.add(totalBean.getTotalPortValueAdd(), secLend.getPortPayValue()));
					hm.put(getKey(rs.getString("FOrder"), Grade - 1), totalBean);
				}
			}
			if(Grade == 2) {
				strSql = "select FBargainDate, FPortCode, FOrder, FSubCatCode, FSubCatName from(" +
						sSql +
						") al group by FBargainDate, FPortCode, FOrder, FSubCatCode, FSubCatName";
				rs = dbl.openResultSet(strSql);
				NavSecLendBean totalBean = null;
				while(rs.next()) {
					totalBean = new NavSecLendBean();
					if(hm.containsKey(getKey(rs.getString("FOrder"), Grade))) {
						totalBean = (NavSecLendBean)hm.get(getKey(rs.getString("FOrder"), Grade));
					}
					secLend = new NavSecLendBean();
					secLend.setNavDate(this.sLendDate);
					secLend.setPortCode(rs.getString("FPortCode"));
					secLend.setSecLendType(this.sRepType);
					secLend.setOrderKeyCode(rs.getString("FOrder"));
					secLend.setInOut(1);
					secLend.setDetail(Grade);
					secLend.setInvMgrCode(this.sBrokerCode);
					secLend.setKeyCode(rs.getString("FSubCatCode"));
					secLend.setKeyName(setBlo(Grade) + rs.getString("FSubCatName"));
					secLend.setCuryCode("汇总：");
					secLend.setSparAmt(totalBean.getTotalAmount());
					secLend.setBookCost(totalBean.getTotalBal());
					secLend.setMarketValue(totalBean.getTotalMarketValue());
					secLend.setPayValue(totalBean.getTotalValueAdd());
					secLend.setPortBookCost(totalBean.getTotalPortBal());
					secLend.setPortMarketValue(totalBean.getTotalPortMarketValue());
					secLend.setPortPayValue(totalBean.getTotalPortValueAdd());
					gradeDataAry.add(secLend);
					
					//计算数量和原币本位币的成本、市值、估增到上层合计
					totalBean = new NavSecLendBean();
					if(hm.containsKey(getKey(rs.getString("FOrder"), Grade - 1))) {
						totalBean = (NavSecLendBean)hm.get(getKey(rs.getString("FOrder"), Grade - 1));
					}
					totalBean.setTotalAmount(YssD.add(totalBean.getTotalAmount(), secLend.getSparAmt()));
					totalBean.setTotalBal(YssD.add(totalBean.getTotalBal(), secLend.getBookCost()));
					totalBean.setTotalMarketValue(YssD.add(totalBean.getTotalMarketValue(), secLend.getMarketValue()));
					totalBean.setTotalValueAdd(YssD.add(totalBean.getTotalValueAdd(), secLend.getPayValue()));
					totalBean.setTotalPortBal(YssD.add(totalBean.getTotalPortBal(), secLend.getPortBookCost()));
					totalBean.setTotalPortMarketValue(YssD.add(totalBean.getTotalPortMarketValue(), secLend.getPortMarketValue()));
					totalBean.setTotalPortValueAdd(YssD.add(totalBean.getTotalPortValueAdd(), secLend.getPortPayValue()));
					hm.put(getKey(rs.getString("FOrder"), Grade - 1), totalBean);
				}
			}
			if(Grade == 1) {
				strSql = "select FBargainDate, FPortCode, FOrder, FCatCode, FCatName from(" +
						sSql +
						") al group by FBargainDate, FPortCode, FOrder, FCatCode, FCatName";
				rs = dbl.openResultSet(strSql);
				NavSecLendBean totalBean = null;
				while(rs.next()) {
					totalBean = new NavSecLendBean();
					if(hm.containsKey(getKey(rs.getString("FOrder"), Grade))) {
						totalBean = (NavSecLendBean)hm.get(getKey(rs.getString("FOrder"), Grade));
					}
					secLend = new NavSecLendBean();
					secLend.setNavDate(this.sLendDate);
					secLend.setPortCode(rs.getString("FPortCode"));
					secLend.setSecLendType(this.sRepType);
					secLend.setOrderKeyCode(rs.getString("FOrder"));
					secLend.setInOut(1);
					secLend.setDetail(Grade);
					secLend.setInvMgrCode(this.sBrokerCode);
					secLend.setKeyCode(rs.getString("FCatCode"));
					secLend.setKeyName(setBlo(Grade) + rs.getString("FCatName"));
					secLend.setCuryCode("汇总：");
					secLend.setSparAmt(totalBean.getTotalAmount());
					secLend.setBookCost(totalBean.getTotalBal());
					secLend.setMarketValue(totalBean.getTotalMarketValue());
					secLend.setPayValue(totalBean.getTotalValueAdd());
					secLend.setPortBookCost(totalBean.getTotalPortBal());
					secLend.setPortMarketValue(totalBean.getTotalPortMarketValue());
					secLend.setPortPayValue(totalBean.getTotalPortValueAdd());
					gradeDataAry.add(secLend);
					
					//计算本位币的累计市值到资产净值合计、累计估增到估值增值合计
					totalBean = new NavSecLendBean();
					if(hm.containsKey("all")) {
						totalBean = (NavSecLendBean)hm.get("all");
					}
					//totalBean.setTotalAmount(YssD.add(totalBean.getTotalAmount(), secLend.getSparAmt()));
					//totalBean.setTotalBal(YssD.add(totalBean.getTotalBal(), secLend.getBookCost()));
					//totalBean.setTotalMarketValue(YssD.add(totalBean.getTotalMarketValue(), secLend.getMarketValue()));
					//totalBean.setTotalValueAdd(YssD.add(totalBean.getTotalValueAdd(), secLend.getPayValue()));
					//totalBean.setTotalPortBal(YssD.add(totalBean.getTotalPortBal(), secLend.getPortBookCost()));
					totalBean.setTotalPortMarketValue(YssD.add(totalBean.getTotalPortMarketValue(), secLend.getPortMarketValue()));
					totalBean.setTotalPortValueAdd(YssD.add(totalBean.getTotalPortValueAdd(), secLend.getPortPayValue()));
					hm.put("all", totalBean);
				}
				totalBean = new NavSecLendBean();
				if(hm.containsKey("all")) {
					totalBean = (NavSecLendBean)hm.get("all");
				}
				secLend = new NavSecLendBean();
				secLend.setNavDate(this.sLendDate);
				secLend.setPortCode(this.sPortCode);
				secLend.setSecLendType(this.sRepType);
				secLend.setOrderKeyCode("ASS");
				secLend.setInvMgrCode(this.sBrokerCode);
				secLend.setKeyCode("资产净值：");
				secLend.setKeyName(String.valueOf(totalBean.getTotalPortMarketValue()));
				gradeDataAry.add(secLend);
				secLend = new NavSecLendBean();
				secLend.setNavDate(this.sLendDate);
				secLend.setPortCode(this.sPortCode);
				secLend.setSecLendType(this.sRepType);
				secLend.setOrderKeyCode("VAL");
				secLend.setInvMgrCode(this.sBrokerCode);
				secLend.setKeyCode("估值增值：");
				secLend.setKeyName(String.valueOf(totalBean.getTotalPortValueAdd()));
				gradeDataAry.add(secLend);
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		return gradeDataAry;
	}
	
	public void dealNavData(ArrayList valueMap) throws YssException {
		
	}
	
}
