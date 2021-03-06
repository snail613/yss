package com.yss.main.operdeal.datainterface.etf.ETFLink.HuaXia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class ETFCanCashMoneyHX extends DataBase{

	private String FNum = "";//交易编号(流水号)
	private long Max = 0;//递增流水号
	
	/**shashijie 2012-7-12 STORY 2727 程序入口 */
	public void inertData() throws YssException {
		
		//获取退补款数据
		List subBeanList = operionYestDay(this.sDate,this.sPort);
		//插入交易数据子表
		insertSubBeanList(subBeanList,this.sDate,this.sPort);
    }

	/**shashijie 2012-7-12 STORY 2727 插入交易数据子表 */
	private void insertSubBeanList(List subBeanList,
			Date dDate, String fPort) throws YssException {
		if (subBeanList.isEmpty()) {
			return;
		}
		
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			//删除资金调拨
			deleteCashTransfer(dDate,fPort);
			//先删除
			String strSql = getDelete(dDate,fPort);
			dbl.executeSql(strSql);
			
			strSql = getInsert();//新增SQL
			ps = conn.prepareStatement(strSql);
			//批量增加
			for (int i = 0; i < subBeanList.size(); i++) {
				TradeSubBean trade = (TradeSubBean)subBeanList.get(i);
				//赋值
				setPreparedStatement(ps,trade);
				ps.executeUpdate();
			}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("保存交易数据子表出错!",e);
		} finally {
			dbl.closeStatementFinal(ps);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**shashijie 2012-7-12 STORY 2727 删除资金调拨
	* @param dDate
	* @param conn 
	* @param fPort*/
	private void deleteCashTransfer(Date dDate, String fPort) throws YssException {
		String strSql = "";
		String FNum = getSubFNum(dDate, fPort);//获取交易数据编号
    	String strNum = getStrNum(FNum);//获取资金调拨编号
    	
        try {
        	//调拨子表
            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum In (" +operSql.sqlCodes(strNum)+" ) ";
            dbl.executeSql(strSql);
            //调拨主表
            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTradeNum in ("+operSql.sqlCodes(FNum)+")";
            dbl.executeSql(strSql);

        } catch (Exception e) {
            throw new YssException("清除资金调拨信息出错\r\n", e);
        } finally {
            //dbl.endTransFinal(conn, true);
        }
	}

	/**shashijie 2012-7-12 STORY 2727 获取资金调拨编号 */
	private String getStrNum(String fNum) throws YssException {
		ResultSet rs = null;
		String fnum = "";//编号
		try {
			String query = " select FNum from "+pub.yssGetTableName("Tb_Cash_Transfer")+
				" where FTradeNum in ("+operSql.sqlCodes(fNum)+")";
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				fnum += rs.getString("FNum") + ",";
			}
		} catch (Exception e) {
			throw new YssException("获取交易数据编号出错\r\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		//去逗号
		if (!fnum.equals("")) {
			fnum = YssFun.getSubString(fnum);
		}
		return fnum;
	}

	/**shashijie 2012-7-12 STORY 2727 获取交易数据编号 */
	private String getSubFNum(Date dDate, String fPort) throws YssException {
		ResultSet rs = null;
		String fnum = "";//编号
		try {
			String query = " select a.FNum from " + pub.yssGetTableName("Tb_Data_SubTrade")+
				" a Where a.FBargainDate = " +dbl.sqlDate(dDate)+
				" And a.FPortCode = " +dbl.sqlString(fPort)+
				//申购,赎回类型随后补上
				" And a.FTradeTypeCode In ("+operSql.sqlCodes(
				//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
				YssOperCons.YSS_JYLX_ETFSGTK+
				"," + YssOperCons.YSS_JYLX_ETFSGBK+
				"," + YssOperCons.YSS_JYLX_ETFSHTBK)+
				//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
				")";
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				fnum += rs.getString("FNum") + ",";
			}
		} catch (Exception e) {
			throw new YssException("获取交易数据编号出错\r\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		//去逗号
		if (!fnum.equals("")) {
			fnum = YssFun.getSubString(fnum);
		}
		return fnum;
	}
	
	/**shashijie 2012-7-12 STORY 2727 给对象属性赋值
	* @param ps
	* @param trade*/
	private void setPreparedStatement(PreparedStatement ps, TradeSubBean trade) throws Exception {
		if (trade==null || trade.getNum().trim().equals("") ) {
			throw new YssException("赋值交易数据子表对象出错!");
		}
		ps.setString(1, trade.getNum());//编号
		ps.setString(2, trade.getSecurityCode());//证券代码
		ps.setString(3, trade.getPortCode());//组合代码
		ps.setString(4, trade.getBrokerCode());//券商代码
		ps.setString(5, trade.getInvMgrCode());//投资经理
		ps.setString(6, trade.getTradeCode());//交易方式
		ps.setString(7, trade.getCashAcctCode());//现金帐户
		ps.setString(8, trade.getAttrClsCode());//所属分类
		ps.setDate(9 ,YssFun.toSqlDate(YssFun.toDate(trade.getRateDate())));//汇率日期
		ps.setDate(10,YssFun.toSqlDate(YssFun.toDate(trade.getBargainDate())));//成交日期
		ps.setString(11, trade.getBargainTime());//成交时间
		ps.setDate(12,YssFun.toSqlDate(YssFun.toDate(trade.getSettleDate())));//结算日期
		ps.setString(13,trade.getSettleTime());//结算时间
		ps.setDate(14,YssFun.toSqlDate(YssFun.toDate(trade.getMatureDate())));//到期日期
		ps.setDate(15,YssFun.toSqlDate(YssFun.toDate(trade.getMatureSettleDate())));//到期结算日期
		ps.setString(16,trade.getFactCashAccCode());//实际结算帐户
		ps.setDouble(17,trade.getFactSettleMoney());//实际结算金额
		ps.setDouble(18,trade.getExRate());//兑换汇率
		ps.setDouble(19,trade.getFactBaseRate());//实际结算基础汇率--这个字段说明书中没有
		ps.setDouble(20,trade.getFactPortRate());//实际结算组合汇率
		ps.setString(21,trade.getAutoSettle());//自动结算
		ps.setDouble(22,trade.getPortCuryRate());//组合汇率
		ps.setDouble(23,trade.getBaseCuryRate());//基础汇率
		ps.setDouble(24,trade.getAllotProportion());//分配比例
		ps.setDouble(25,trade.getOldAllotAmount());//原始分配数量
		ps.setDouble(26,trade.getAllotFactor());//分配因子
		ps.setDouble(27,trade.getTradeAmount());//交易数量
		ps.setDouble(28,trade.getTradePrice());//交易价格
		ps.setDouble(29,trade.getTradeMoney());//交易金额
		ps.setDouble(30,trade.getAccruedInterest());//应计利息
		ps.setDouble(31,trade.getBailMoney());//保证金金额
		ps.setString(32,trade.getFFeeCode1());//费用代码1
		ps.setDouble(33,trade.getFTradeFee1());//交易费用1
		ps.setString(34,trade.getFFeeCode2());//费用代码2
		ps.setDouble(35,trade.getFTradeFee2());//交易费用2
		ps.setString(36,trade.getFFeeCode3());//费用代码3
		ps.setDouble(37,trade.getFTradeFee3());//交易费用3
		ps.setString(38,trade.getFFeeCode4());//费用代码4
		ps.setDouble(39,trade.getFTradeFee4());//交易费用4
		ps.setString(40,trade.getFFeeCode5());//费用代码5
		ps.setDouble(41,trade.getFTradeFee5());//交易费用5
		ps.setString(42,trade.getFFeeCode6());//费用代码6
		ps.setDouble(43,trade.getFTradeFee6());//交易费用6
		ps.setString(44,trade.getFFeeCode7());//费用代码7
		ps.setDouble(45,trade.getFTradeFee7());//交易费用7
		ps.setString(46,trade.getFFeeCode8());//费用代码8
		ps.setDouble(47,trade.getFTradeFee8());//交易费用8
		ps.setDouble(48,trade.getTotalCost());//实收实付金额
		ps.setDouble(49,trade.getCost().getCost());//原币核算成本
		ps.setDouble(50,trade.getCost().getMCost());//原币管理成本
		ps.setDouble(51,trade.getCost().getVCost());//原币估值成本
		ps.setDouble(52,trade.getCost().getBaseCost());//基础货币核算成本
		ps.setDouble(53,trade.getCost().getBaseMCost());//基础货币管理成本
		ps.setDouble(54,trade.getCost().getBaseVCost());//基础货币估值成本
		ps.setDouble(55,trade.getCost().getPortCost());//组合货币核算成本
		ps.setDouble(56,trade.getCost().getPortMCost());//组合货币管理成本
		ps.setDouble(57,trade.getCost().getPortVCost());//组合货币估值成本
		ps.setString(58,trade.getSettleState());//结算状态
		ps.setDate(59,YssFun.toSqlDate(YssFun.toDate(trade.getFactSettleDate())));//实际结算日期
		ps.setString(60,trade.getSettleDesc());//结算描述
		ps.setString(61,trade.getOrderNum());//订单编号
		ps.setInt(62,trade.getDataSource());//数据来源
		ps.setString(63,trade.getDataBirth());//交易来源
		ps.setString(64,"");//结算机构代码
		ps.setString(65,trade.getDesc());//描述
		ps.setInt(66, this.checkState.equalsIgnoreCase("true")?1:0);//审核状态
		ps.setString(67, pub.getUserCode());//创建人、修改人
        ps.setString(68, YssFun.formatDatetime(new Date()));//创建、修改时间
		ps.setString(69, pub.getUserCode());//复核人
		ps.setString(70, YssFun.formatDatetime(new Date()));//复核时间
		ps.setString(71,trade.getETFBalaAcctCode());//ETF现金差额账户代码
		ps.setDate(72,YssFun.toSqlDate(YssFun.toDate(trade.getETFBalaSettleDate())));//ETF现金差额结算日期
		ps.setDouble(73,trade.getETFBalanceMoney());//ETF现金差额
		ps.setDouble(74,trade.getETFCashAlternat());//ETF现金替代
		ps.setString(75,trade.getTradeSeatCode());//席位代码
		ps.setString(76,trade.getStockholderCode());//股东代码
		ps.setString(77,"");//操作类型
		ps.setString(78,trade.getSplitNum());//拆分关联编号
		ps.setString(79,trade.getInvestType());//投资类型
		ps.setString(80,trade.getFdealNum());//开放式基金业务,编号	--这个字段说明书中没有
		ps.setDate(81,YssFun.toSqlDate(YssFun.toDate("1900-01-01")));//???    --这个字段说明书中没有
		ps.setString(82,trade.getJkdr());// 接口导入:0手工录入,1 接口导入    --这个字段说明书中没有
		ps.setDate(83,YssFun.toSqlDate(YssFun.toDate(trade.getStrRecordDate())));//登记日
		ps.setDouble(84,Double.valueOf(trade.getStrDivdendType()).doubleValue());//分红类型
		ps.setString(85,trade.getFSecurityDelaySettleState());//延迟交割标识
		ps.setString(86,"");//券商代码类型
		ps.setString(87,trade.getBrokerCode());//券商代码
		ps.setString(88,"");//结算结构代码类型
		ps.setString(89,"");//结算结构代码
		ps.setString(90,"");//结算券商代码类型
		ps.setString(91,"");//结算券商代码
		ps.setString(92,"");//结算账户代码
		ps.setString(93,trade.getCostIsHandEditState());//手动修改成本标示
		ps.setDate(94,YssFun.toSqlDate(YssFun.toDate(trade.getFBSDate())));//ETF申赎日期
		ps.setDouble(95,trade.getFCanReturnMoney());//ETF可退替代款
	}

	/**shashijie 2012-7-12 STORY 2727 新增SQL语句 */
	private String getInsert() {
		String sql = " insert into "+
			pub.yssGetTableName("Tb_Data_SubTrade")+
			"(" +
			" FNUM," +//编号
			" FSECURITYCODE," +//证券代码
			" FPORTCODE," +//组合代码
			" FBROKERCODE," +//券商代码
			" FINVMGRCODE," +//投资经理
			" FTRADETYPECODE," +//交易方式
			" FCASHACCCODE," +//现金帐户
			" FATTRCLSCODE," +//所属分类
			" FRATEDATE," +//汇率日期
			" FBARGAINDATE," +//成交日期
			" FBARGAINTIME," +//成交时间
			" FSETTLEDATE," +//结算日期
			" FSETTLETIME," +//结算时间
			" FMATUREDATE," +//到期日期
			" FMATURESETTLEDATE," +//到期结算日期
			" FFACTCASHACCCODE," +//实际结算帐户
			" FFACTSETTLEMONEY," +//实际结算金额
			" FEXRATE," +//兑换汇率
			" FFACTBASERATE," +//实际结算基础汇率--这个字段说明书中没有
			" FFACTPORTRATE," +//实际结算组合汇率
			" FAUTOSETTLE, " +//自动结算
			" FPORTCURYRATE," +//组合汇率
			" FBASECURYRATE," +//基础汇率
			" FALLOTPROPORTION," +//分配比例
			" FOLDALLOTAMOUNT," +//原始分配数量
			" FALLOTFACTOR," +//分配因子
			" FTRADEAMOUNT," +//交易数量
			" FTRADEPRICE," +//交易价格
			" FTRADEMONEY," +//交易金额
			" FACCRUEDINTEREST," +//应计利息
			" FBAILMONEY," +//保证金金额
			" FFEECODE1," +//费用代码1
			" FTRADEFEE1," +//交易费用1
			" FFEECODE2," +//费用代码2
			" FTRADEFEE2," +//交易费用2
			" FFEECODE3," +//费用代码3
			" FTRADEFEE3," +//交易费用3
			" FFEECODE4," +//费用代码4
			" FTRADEFEE4," +//交易费用4
			" FFEECODE5," +//费用代码5
			" FTRADEFEE5," +//交易费用5
			" FFEECODE6," +//费用代码6
			" FTRADEFEE6," +//交易费用6
			" FFEECODE7," +//费用代码7
			" FTRADEFEE7," +//交易费用7
			" FFEECODE8," +//费用代码8
			" FTRADEFEE8," +//交易费用8
			" FTOTALCOST," +//实收实付金额
			" FCOST," +//原币核算成本
			" FMCOST," +//原币管理成本
			" FVCOST," +//原币估值成本
			" FBASECURYCOST," +//基础货币核算成本
			" FMBASECURYCOST," +//基础货币管理成本
			" FVBASECURYCOST," +//基础货币估值成本
			" FPORTCURYCOST," +//组合货币核算成本
			" FMPORTCURYCOST," +//组合货币管理成本
			" FVPORTCURYCOST," +//组合货币估值成本
			" FSETTLESTATE," +//结算状态
			" FFACTSETTLEDATE," +//实际结算日期
			" FSETTLEDESC," +//结算描述
			" FORDERNUM," +//订单编号
			" FDATASOURCE," +//数据来源
			" FDATABIRTH," +//交易来源
			" FSETTLEORGCODE," +//结算机构代码
			" FDESC," +//描述
			" FCHECKSTATE," +//审核状态--这个字段说明书中没有
			" FCREATOR," +//创建人、修改人
			" FCREATETIME," +//创建、修改时间
			" FCHECKUSER," +//复核人
			" FCHECKTIME," +//复核时间
			" FETFBALAACCTCODE," +//ETF现金差额账户代码
			" FETFBALASETTLEDATE," +//ETF现金差额结算日期
			" FETFBALAMONEY," +//ETF现金差额
			" FETFCASHALTERNAT," +//ETF现金替代
			" FSEATCODE," +//席位代码
			" FSTOCKHOLDERCODE," +//股东代码
			" FDS," +//操作类型
			" FSPLITNUM," +//拆分关联编号
			" FINVESTTYPE," +//投资类型
			" FDEALNUM," +//开放式基金业务,编号--这个字段说明书中没有
			" FAPPDATE," +//???    --这个字段说明书中没有
			" FJKDR," +// 接口导入:0手工录入,1 接口导入    --这个字段说明书中没有
			" FRECORDDATE," +//登记日
			" FDIVDENDTYPE," +//分红类型
			" FSECURITYDELAYSETTLESTATE," +//延迟交割标识
			" FBROKERIDCODETYPE," +//券商代码类型
			" FBROKERIDCODE," +//券商代码
			" FSETTLEORGIDCODETYPE," +//结算结构代码类型
			" FSETTLEORGIDCODE," +//结算结构代码
			" FCLEARINGBROKERCODETYPE," +//结算券商代码类型
			" FCLEARINGBROKERCODE," +//结算券商代码
			" FCLEARINGACCOUNT," +//结算账户代码
			" FHANDCOSTSTATE," +//手动修改成本标示
			" FBSDATE," +//ETF申赎日期
			" FCANRETURNMONEY" +//ETF可退替代款
						
			")"+
			" Values (" +
			" ?,?,?,?,?,?,?,?,?,?" +//10
			",?,?,?,?,?,?,?,?,?,?" +//20
			",?,?,?,?,?,?,?,?,?,?" +//30
			",?,?,?,?,?,?,?,?,?,?" +//40
			",?,?,?,?,?,?,?,?,?,?" +//50
			",?,?,?,?,?,?,?,?,?,?" +//60
			",?,?,?,?,?,?,?,?,?,?" +//70
			",?,?,?,?,?,?,?,?,?,?" +//80
			",?,?,?,?,?,?,?,?,?,?" +//90
			",?,?,?,?,?)";
		return sql;
	}

	/**shashijie 2012-7-12 STORY 2727 删除交易数据子表sql
	* @return*/
	private String getDelete(Date dDate, String fPort) {
		String sqlString = " delete from " + pub.yssGetTableName("Tb_Data_SubTrade")+
			" a Where a.FBargainDate = " +dbl.sqlDate(dDate)+
			" And a.fportcode = " +dbl.sqlString(fPort)+
			" And a.fdatasource = '1' " +
			//申购,赎回类型随后补上
			" And a.FTradeTypeCode In ("+operSql.sqlCodes(
			//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
			YssOperCons.YSS_JYLX_ETFSGTK+
			"," + YssOperCons.YSS_JYLX_ETFSGBK+
			","+YssOperCons.YSS_JYLX_ETFSHTBK)+
			//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
			")";
		return sqlString;
	}

	/**shashijie 2012-7-12 STORY 2727 交易对象赋值
	* @param rs 
	* @param FPort 组合
	* @param mark 标识 	1:T日申请数据,		2:T-1日确认数据
	* @return
	* @throws YssException*/
	private TradeSubBean getTradeSubBean(ResultSet rs,String FPort,Date dDate) throws Exception {
		TradeSubBean trade = new TradeSubBean();
		//获取流水号
		getTradeNum(rs.getDate("FDate"),rs.getString("FBs"));
		String num = FNum;
		trade.setNum(num); // 交易拆分数据流水号
		
		trade.setInvestType("C");//投资类型
		trade.setSecurityCode(rs.getString("FSecurityCode")); // 交易证券代码
		trade.setPortCode(FPort); // 组合代码
		trade.setBrokerCode(rs.getString("Fbrokercode")); // 券商代码
		trade.setInvMgrCode(" "); // 投资经理代码
		
		//获取交易方式代码
		//edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001
		String tradeCode = getTradeCode(rs);
		trade.setTradeCode(tradeCode);//交易类型
		
		//获取现金账户代码
		String cashAcctCode = getCashAcctCode(FPort,trade.getTradeCode(),
				rs.getDate("FDate"),"CNY");//先写死人命币
		trade.setCashAcctCode(cashAcctCode); // 现金帐户代码
		trade.setAttrClsCode(""); // 所属分类代码
		
		//获取成交日期(根据退款日期往前倒推1天)
		String BargainDate = YssFun.formatDate(dDate);
		trade.setBargainDate(YssFun.formatDate(BargainDate)); // 成交日期
		trade.setBargainTime("00:00:00"); // 成交时间
		
		trade.setSettleDate(YssFun.formatDate(rs.getDate("Frefunddate"))); // 结算日期
		trade.setSettleTime("00:00:00"); // 结算时间
		trade.setRateDate(YssFun.formatDate(rs.getDate("FDate"))); // 汇率日期
		trade.setAutoSettle("0"); // 自动结算
		
		//获取汇率
		double baseCuryRate = getCuryRate(rs.getDate("FDate"),FPort,"CNY",//先写死人命币
				YssOperCons.YSS_RATE_BASE);
		trade.setBaseCuryRate(baseCuryRate); // 基础汇率
		double portCuryRate = getCuryRate(rs.getDate("FDate"), FPort, "", YssOperCons.YSS_RATE_PORT);
		trade.setPortCuryRate(portCuryRate); // 组合汇率
		trade.setFactBaseRate(baseCuryRate);//实际结算基础汇率//这个字段说明书中没有
		trade.setFactPortRate(portCuryRate);//实际结算组合汇率
		
		trade.setHandAmount(0); // 每手股数
		trade.setAllotProportion(0); // 分配比例
		trade.setOldAllotAmount(0); // 原始分配数量
		
		//获取交易数量
		double tradeAmount = getTradeAmount(0,"", 0);
		trade.setTradeAmount(tradeAmount); // 交易数量
		trade.setTradePrice(0); // 交易价格
		//获取成交金额
		double tradeMoney = getTradeMoney(trade.getTradeAmount(), trade.getTradePrice(), "",
				0);
		trade.setTradeMoney(tradeMoney); // 交易总额
		trade.setAccruedInterest(0); // 应计利息
		trade.setAllotFactor(1); // 分配因子
		
		//获取实收实付金额
		double TotalCost = getTotalCost(rs.getDouble("Bkje"),rs.getDouble("Tkje"));
		//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
		//若为 申购补款 则实收实付金额 需 乘以 -1  资金方向  = 交易方式'206'(ETF申购补款)的资金方向
		if(rs.getString("FBs").equals("B") && rs.getDouble("Tkje") < 0){
			trade.setTotalCost(YssD.round(YssD.mul(TotalCost, -1),2)); //实收实付金额
		}else{
			trade.setTotalCost(YssD.round(TotalCost,2)); //实收实付金额
		}
		//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
		trade.setDesc(""); // 交易描述
		trade.setSettleState("0"); // 结算状态,临时
		trade.setBailMoney(0); // 保证金金额
		
		trade.setFactor(1); // 报价因子
		trade.setFactCashAccCode(cashAcctCode); // 实际结算帐户
		trade.setFactSettleMoney(trade.getTotalCost()); // 实际结算金额
		trade.setExRate(1); // 兑换汇率
		trade.setFactSettleDate(YssFun.formatDate(rs.getDate("Frefunddate"))); // 实际结算日期
		trade.setSettleDesc(""); // 结算描述
		trade.setDataSource(1);//数据来源
		
		trade.setETFBalaAcctCode(cashAcctCode); // ETF 现金差额结算帐户代码
		trade.setFBSDate(YssFun.formatDate(rs.getDate("Fbuydate")));//ETF申购日期
		//现金差额 = 单位现金差额 * 篮子数(交易数量/最小申赎份额)
		double ETFBalanceMoney = getETFBalanceMoney(0,
				trade.getTradeAmount(), 0,"",
				0);
		trade.setETFBalanceMoney(ETFBalanceMoney); // ETF 现金差额
		//获取现金替代
		double ETFCashAlternat = getETFCashAlternat(0,"",
				0);
		//ETF 现金替代
		trade.setETFCashAlternat(ETFCashAlternat); 
		//可退替代款 = T日申请可退替代款 + T+1日可退替代款
		double FCanReturnMoney = rs.getDouble("Fprereturnmoney");
		trade.setFCanReturnMoney(YssD.round(FCanReturnMoney,2));//ETF可退替代款
		//现金差额结算日期
		/*String ETFBalaSettleDate = getSettleDate(rs.getString("FHolidaysCode"),rs.getDate("FBargainDate"),
				rs.getInt("Fbsdifferenceover")
				);*/
		trade.setETFBalaSettleDate("9998-12-31"); // ETF 现金差额结算日期
		
		//申购退补款成本 = 可退替代款 - 实收实付金额
		//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
		YssCost cost = null;
		if(rs.getString("FBs").equals("B") && rs.getDouble("Tkje") < 0){
			cost = getYssCost(trade.getTradeCode(),//交易方式
							  TotalCost,//实收实付
				              0,//ETF 现金替代
					          trade.getFCanReturnMoney(),//ETF 退补款
					          trade.getBaseCuryRate(),trade.getPortCuryRate()//汇率
					          );
		}else{
			cost = getYssCost(trade.getTradeCode(),//交易方式
				              trade.getTotalCost(),//实收实付
				              0,//ETF 现金替代
				              trade.getFCanReturnMoney(),//ETF 退补款
				              trade.getBaseCuryRate(),trade.getPortCuryRate()//汇率
				              );
		}
		//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
		trade.setCost(cost);
		
		trade.setTradeSeatCode(rs.getString("FSeatCode")); // 席位代码
		trade.setStockholderCode(rs.getString("Fstockholdercode"));// 股东代码
		//证券延迟交割标示，0未延迟 ，1延迟
		trade.setFSecurityDelaySettleState("0");
		trade.setMatureDate("9998-12-31");//到期日期
		trade.setMatureSettleDate("9998-12-31");//到期结算日期
		return trade;
	}

	/**shashijie 2012-7-12 STORY 2727
	* @param string
	* @return*/
	//edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 String FBs 改为 ResultSet rs
	private String getTradeCode(ResultSet rs)throws YssException {
		String value = "";
		//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
		try{
			//若为申购数据，且 退款金额 大于 零 则为 申购退款数据，否则为申购补款数据
			if (rs.getString("FBs").equals("B")) {
				if(rs.getDouble("TKJE") > 0){
					value = YssOperCons.YSS_JYLX_ETFSGTK;
				}else{
					value = YssOperCons.YSS_JYLX_ETFSGBK;
				}
			} else {
				value = YssOperCons.YSS_JYLX_ETFSHTBK;
			}
			return value;
		}catch(Exception e){
			throw new YssException("获取结果集数据出错");
		}
		//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
	}

	/**shashijie 2012-7-12 STORY 2727 获取实收实付金额
	* @param buyMoney 赎回
	* @param selMoney 申购
	* @return*/
	private double getTotalCost(double buyMoney, double selMoney) {
		double value = 0;
		if (buyMoney!=0) {
			value = buyMoney;
		}
		if (selMoney!=0) {
			value = selMoney;
		}
		return value ;
	}

	/**shashijie 2012-7-12 STORY 2727 获取现金替代 
	* @param Ftotalmoney
	* @param mark
	* @param Fetfcashalternat
	* @return*/
	private double getETFCashAlternat(double Ftotalmoney, String mark,
			double Fetfcashalternat) {
		double CashAlternat = Ftotalmoney;
		//如果是生产冲减数据,倒钆计算
		if (mark.equals("2")) {
			CashAlternat = YssD.sub(Fetfcashalternat, CashAlternat);
		}
		return CashAlternat;
	}

	/**shashijie 2012-7-12 STORY 2727 获取成交金额 = 数量 * 成交价 */
	private double getTradeMoney(double tradeAmount, double tradePrice,
			String mark, double tradeMoney) {
		//数量*价格
		double money = YssD.mul(tradeAmount, tradePrice);
		//如果是生产冲减数据,倒钆计算
		if (mark.equals("2")) {
			money = YssD.sub(tradeMoney,money);
		}
		return money;
	}

	/**shashijie 2012-7-12 STORY 2727 获取交易数量 */
	private double getTradeAmount(double Ftradeamount, String mark, double tradeamount) {
		double amount = Ftradeamount;
		//如果是生成冲减数据,倒钆计算值
		if (mark.equals("2")) {
			amount = YssD.sub(tradeamount, amount);
		}
		return amount;
	}

	/**shashijie 2012-7-12 STORY 2727  成本 
	* @param tradeCode 交易方式
	* @param totalCost 实收实付
	* @param ETFCashAlternat 现金替代
	* @param FCanReturnMoney 退补款
	* @param baseRate 基础汇率
	* @param portRate 组合汇率
	* @param FBSDate 申赎日期
	* @param fPort 组合
	* @param mark 申请确认标识
	* @return*/
	private YssCost getYssCost(String tradeCode, double totalCost, 
			double ETFCashAlternat, double FCanReturnMoney, double baseRate, double portRate
			) throws YssException {
		YssCost yssCost = new YssCost();
		//原币成本
		double cost = 0;
		//基础成本
		double baseCost = 0;
		//组合成本
		double portCost = 0;
		try {
			//申购,申购失败冲减
			//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
			if (tradeCode.equals(YssOperCons.YSS_JYLX_ETFSGTK) || 
				tradeCode.equals(YssOperCons.YSS_JYLX_ETFSGBK)) {
			//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
				//原币成本
				cost = YssD.sub(FCanReturnMoney, totalCost);
				//基础成本
				baseCost = YssD.mul(cost, baseRate);
				//组合成本
				portCost = YssD.div(baseCost, portRate);
				
				//对象赋值
				setYssCostValue(yssCost,cost,baseCost,portCost);
			}
		} catch (Exception e2) {
			throw new YssException("获取成本出错!");
		} finally {

		}
		
		return yssCost;
	}

	/**shashijie 2012-7-12 STORY 2727 成本对象赋值 */
	private void setYssCostValue(YssCost yssCost,double cost, double baseCost, double portCost) {
		if (yssCost==null) {
			return;
		}
		//原币成本
		yssCost.setCost(cost);
		yssCost.setMCost(cost);
		yssCost.setVCost(cost);
		//基础成本
		yssCost.setBaseCost(baseCost);
		yssCost.setBaseMCost(baseCost);
		yssCost.setBaseVCost(baseCost);
		//组合成本
		yssCost.setPortCost(portCost);
		yssCost.setPortMCost(portCost);
		yssCost.setPortVCost(portCost);
	}

	/**shashijie 2012-7-12 STORY 2727 现金差额 = 单位现金差额 * 篮子数(交易数量/最小申赎份额)
	* @param Fdvalue 单位现金差额
	* @param tradeAmount 交易数量
	* @param Fnormscale 最小申赎份额
	 * @param Fetfbalamoney 
	 * @param mark 
	* @return*/
	private double getETFBalanceMoney(double Fdvalue, double tradeAmount,
			double Fnormscale, String mark, double Fetfbalamoney) {
		double ETFBalanceMoney = YssD.mul(Fdvalue, 
			YssD.div(tradeAmount, Fnormscale));
		//如果是生产冲减数据,倒钆计算
		if (mark.equals("2")) {
			ETFBalanceMoney = YssD.sub(Fetfbalamoney, ETFBalanceMoney);
		}
		return ETFBalanceMoney;
	}

	/**shashijie 2012-7-12 STORY 2727 获取汇率
	* @param date 日期
	* @param fPort 组合
	* @param string 币种
	* @param yssRateBase 标识
	* @return*/
	private double getCuryRate(Date dDate, String FPort, String FCuryCode,
			String YSSRATEBASE) throws YssException {
		double rate = 0;
		try {
			rate = this.getSettingOper().getCuryRate(
					dDate, 
					FCuryCode.trim().equals("RMB") ? "CNY" : FCuryCode ,//币种
					FPort, 
					YSSRATEBASE);
		} catch (Exception e) {
			throw new YssException("获取汇率出错!");
		} finally {

		}
		return rate;
	}

	/**shashijie 2012-7-12 STORY 2727 获取现金账户代码
	* @return*/
	private String getCashAcctCode(String FPort,String tradeTypeCode,Date FDate,String FCuryCode
			) throws YssException {
		String strCashAccCode = "";//现金账户
		try {
			
			BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal");//账户链接
			cashacc.setYssPub(pub);
			cashacc.setLinkParaAttr(
					"", //投资经理
					FPort, //组合代码
					YssOperCons.YSS_ZQPZ_TR,//品种类型
					YssOperCons.YSS_ZQPZZLX_TR04,//品种子类型 TR04 ETF基金
					"",//交易所
					"",//券商代码
					tradeTypeCode, //交易类型
	        		FDate,//启用日期
	        		FCuryCode.trim().equals("RMB") ? "CNY" : FCuryCode //币种
	        		); 
			
			CashAccountBean caBean = cashacc.getCashAccountBean();//获取现金账户Bean
			strCashAccCode = caBean.getStrCashAcctCode(); //现金账户
		} catch (Exception e) {
			throw new YssException("获取现金账户错误!");
		} finally {
			
		}
		return strCashAccCode;
	}

	/**shashijie 2012-7-12 STORY 2727 获取交易数据流水号 */
	private void getTradeNum(java.sql.Date bargainDate,String FBs) throws YssException {
		try {
			//判断申赎拼接流水号
			String fNumType = "";
			fNumType = "100000";//退补款
			
			//日期
			String strNumDate = YssFun.formatDatetime((YssFun.toSqlDate(bargainDate))).substring(0, 8);
			//同类别下最大编号
			FNum = "T" + strNumDate;
			//交易主表
			FNum += dbFun.getNextInnerCode(
					pub.yssGetTableName("Tb_Data_Trade"), 
					dbl.sqlRight("FNUM", 6), 
					fNumType, 
					" where FNum like 'T" + strNumDate + fNumType.substring(0, 1) + "%'", 
					1);
			//交易子表
			FNum += dbFun.getNextInnerCode(
					pub.yssGetTableName("Tb_Data_SubTrade"), 
					dbl.sqlRight("FNUM", 5), 
					"00000", 
					" where FNum like '" + FNum.replaceAll("'", "''") + "%'" );
		
			//递增
			String totle = FNum.substring(0,10);//编号头
			String tempNum = FNum.substring(10);//递增流水号
			long tmp = Long.valueOf(tempNum) + Max;//始终+1递增
			FNum = totle + YssFun.formatNumber(tmp, "0000000000");
			//每次循环加1
			Max++;
		} catch (Exception e) {
			throw new YssException("获取交易数据流水号错误!");
		} finally {

		}
		//return FNum;
	}

	/**shashijie 2012-7-12 STORY 2727 获取退补款数据 */
	private List operionYestDay(Date dDate, String fPort) throws YssException {
		ResultSet rs = null;
		List SubBean = new ArrayList();
		try {
			String query = getYesterdaySql(dDate,fPort);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//获取交易数据对象并赋值
				TradeSubBean suBean = getTradeSubBean(rs,fPort,dDate);
				SubBean.add(suBean);//一般只有2条数据,B申购,S赎回
			}
		} catch (Exception e) {
			throw new YssException("获取退补款数据出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return SubBean;
	}

	/**shashijie 2012-7-12 STORY 2727 获取冲减SQL */
	private String getYesterdaySql(Date dDate, String fPort) {
		//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
		StringBuffer sb = new StringBuffer();
		sb.append(" Select Sum(a.FSumReturn) * -1 As Bkje, ");
		sb.append(" Sum(a.FRepCash) As Tkje, ");//退款金额
		sb.append(" Sum(round(c.FPreReturnMoney,2)) As FPreReturnMoney, ");//可收预估款
		sb.append(" a.FSecurityCode, ");
		sb.append(" a.FRefundDate, ");
		sb.append(" a.FBuyDate, ");
		sb.append(" a.FDate, ");
		sb.append(" a.FSeatCode, ");
		sb.append(" a.FSeatNum, ");
		sb.append(" Case When a.Fbs = '02' Or a.Fbs = 'B' Then 'B' Else 'S' End As Fbs, ");
		sb.append(" a.Fstockholdercode, ");
		sb.append(" b.Fbrokercode ");
		sb.append(" From (Select FSumReturn, ");
		sb.append(" FSecurityCode, ");
		sb.append(" FRefundDate, ");
		sb.append(" FBuyDate, ");
		sb.append(" FDate, ");
		sb.append(" FSeatCode, ");
		sb.append(" FRepCash, ");
		sb.append(" FSeatNum, ");
		sb.append(" FBs, ");
		sb.append(" FStockHolderCode, ");
		sb.append(" FConsignNum, ");
		sb.append(" FPortCode, ");
		sb.append(" FZhSecurityCode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Etf_Book"));
		sb.append(" Where FPortCode = "+dbl.sqlString(fPort));
		sb.append(" And FDate = "+dbl.sqlDate(dDate)+" ) a ");
		sb.append(" Join (Select A1.FPortCode, A5.FSeatNum, A5.FBrokerCode, A5.FSeatCode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" A1 ");
		sb.append(" Join (Select A2.Fsubcode, A2.Fportcode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" A2 ");
		sb.append(" Where A2.Fportcode = "+dbl.sqlString(fPort));
		sb.append(" And A2.Frelatype = 'TradeSeat' ");
		sb.append(" And A2.Fcheckstate = 1) A3 On A1.Fportcode = A3.Fportcode ");
		sb.append(" Left Join (Select A4.Fseatcode, A4.Fseatnum, A4.Fbrokercode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Para_Tradeseat")+" A4) A5 On A3.Fsubcode = ");
		sb.append(" A5.FSeatCode) b On a.FSeatCode = b.FSeatNum ");
		sb.append(" Join (Select A1.Fsecuritycode, ");
		sb.append(" A1.Fportcode, ");
		sb.append(" A1.Fconsignnum, ");
		sb.append(" A1.Fbargaindate, ");
		sb.append(" A1.Fprereturnmoney, ");
		sb.append(" A1.Ftradetypecode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Etf_Linkedbook")+" A1 ");
		sb.append(" where A1.FPortCode = "+dbl.sqlString(fPort)+" ) c On c.Fsecuritycode = ");
		sb.append(" a.Fzhsecuritycode And c.Fbargaindate = a.Fbuydate And c.Fconsignnum = a.FConsignNum ");
		sb.append(" And c.Fportcode = a.Fportcode And Case When c.Ftradetypecode = '102' Then 'B' Else 'S' End = a.Fbs ");
		sb.append(" where a.Frepcash > 0 ");//金额 > 0 为 退款
		sb.append(" Group By a.Fsecuritycode, ");
		sb.append(" a.Frefunddate, ");
		sb.append(" a.Fbuydate, ");
		sb.append(" a.Fdate, ");
		sb.append(" a.Fseatcode, ");
		sb.append(" a.Fseatnum, ");
		sb.append(" a.Fbs, ");
		sb.append(" a.Fstockholdercode, ");
		sb.append(" b.Fbrokercode ");
		
		sb.append(" union all ");
		
		sb.append(" Select Sum(a.Fsumreturn) * -1 As Bkje, ");
		sb.append(" Sum(a.Frepcash) As Tkje, ");
		sb.append(" Sum(round(c.FPreReturnMoney,2)) As FPreReturnMoney, ");
		sb.append(" a.Fsecuritycode, ");
		sb.append(" a.Frefunddate, ");
		sb.append(" a.Fbuydate, ");
		sb.append(" a.Fdate, ");
		sb.append(" a.Fseatcode, ");
		sb.append(" a.Fseatnum, ");
		sb.append(" Case When a.Fbs = '02' Or a.Fbs = 'B' Then 'B' Else 'S' End As Fbs, ");
		sb.append(" a.Fstockholdercode, ");
		sb.append(" b.Fbrokercode ");
		sb.append(" From (Select FSumReturn, ");
		sb.append(" FSecurityCode, ");
		sb.append(" FRefundDate, ");
		sb.append(" FBuyDate, ");
		sb.append(" FDate, ");
		sb.append(" FSeatCode, ");
		sb.append(" FRepCash, ");
		sb.append(" FSeatNum, ");
		sb.append(" FBs, ");
		sb.append(" FStockHolderCode, ");
		sb.append(" FConsignNum, ");
		sb.append(" FPortCode, ");
		sb.append(" FZhSecurityCode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Etf_Book"));//退补款(台账)过渡表
		sb.append(" Where FPortCode = "+dbl.sqlString(fPort));
		sb.append(" And FDate = "+dbl.sqlDate(dDate)+" ) a ");
		sb.append(" Join (Select A1.FPortCode, A5.FSeatNum, A5.FBrokerCode, A5.FSeatCode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" A1 ");
		sb.append(" Join (Select A2.Fsubcode, A2.Fportcode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" A2 ");
		sb.append(" Where A2.Fportcode = "+dbl.sqlString(fPort));
		sb.append(" And A2.Frelatype = 'TradeSeat' ");
		sb.append(" And A2.Fcheckstate = 1) A3 On A1.Fportcode = A3.Fportcode ");
		sb.append(" Left Join (Select A4.Fseatcode, A4.Fseatnum, A4.Fbrokercode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Para_Tradeseat")+" A4) A5 On A3.Fsubcode = ");//券商
		sb.append(" A5.FSeatCode) b On a.FSeatCode = b.FSeatNum ");
		sb.append(" Join (Select A1.Fsecuritycode, ");
		sb.append(" A1.Fportcode, ");
		sb.append(" A1.Fconsignnum, ");
		sb.append(" A1.Fbargaindate, ");
		sb.append(" A1.Fprereturnmoney, ");
		sb.append(" A1.Ftradetypecode ");
		sb.append(" From "+pub.yssGetTableName("Tb_Etf_Linkedbook")+" A1 ");//ETF联接基金台账过度表
		sb.append(" where A1.FPortCode = "+dbl.sqlString(fPort)+" ) c On c.Fsecuritycode = ");
		sb.append(" a.Fzhsecuritycode And c.Fbargaindate = a.Fbuydate And c.Fconsignnum = a.FConsignNum ");
		sb.append(" And c.Fportcode = a.Fportcode And Case When c.Ftradetypecode = '102' Then 'B' Else 'S' End = a.Fbs ");
		sb.append(" where a.Frepcash < 0 ");//金额 < 0 为 补款
		sb.append(" Group By a.Fsecuritycode, ");
		sb.append(" a.Frefunddate, ");
		sb.append(" a.Fbuydate, ");
		sb.append(" a.Fdate, ");
		sb.append(" a.Fseatcode, ");
		sb.append(" a.Fseatnum, ");
		sb.append(" a.Fbs, ");
		sb.append(" a.Fstockholdercode, ");
		sb.append(" b.Fbrokercode ");
		
		return sb.toString();
		//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
	}

}
