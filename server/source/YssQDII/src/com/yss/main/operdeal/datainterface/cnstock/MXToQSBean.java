package com.yss.main.operdeal.datainterface.cnstock;

import java.sql.*;
import java.util.*;

import com.yss.util.*;

import java.math.BigDecimal;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdata.BondInterestBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.*;
import com.yss.main.operdeal.datainterface.cnstock.shstock.*;
import com.yss.main.operdeal.datainterface.cnstock.szstock.*;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.datainterface.cnstock.BrokerRateBean;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.datainterface.cnstock.RateSpeciesTypeBean;


/**
 * QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 created by songjie 2009-06-21
 */
public class MXToQSBean extends DataBase {
	// 用于储存要存到交易接口清算表的实例
	HashMap hmHzJkQs = new HashMap();

	//---delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
	// 用于储存数据接口参数设置界面的交易费用计算方式分页的各种参数设置 key--费用代码 + 计算方式代码(如cjmx),
	// value--组合代码(如001，002，003) + "\t" + 计算方式代码(如cjmx)
	//HashMap hmTradeFee = null;
	// 用于储存数据接口参数设置界面的读数处理方式分页的各种参数
	// key--组合群代码,组合代码
	//HashMap hmReadType = null;
	//HashMap hmBrokerCode = null;// 用于储存券商代码
	//HashMap hmBrokerRate = null;
	// 用于储存数据接口参数设置界面的交易所债券参数设置分页的各种参数
	// key--组合群代码, 组合代码, 市场, 品种
	//HashMap hmExchangeBond = null;
	// 用于储存数据接口参数设置界面的费用承担方向的参数数据
	//HashMap hmFeeWay = null;
	// 用于储存已选组合代码对应的席位代码和股东代码
	//HashMap hmPortHolderSeat = null;
	// 用于储存交易费率品种设置的数据
	//HashMap hmRateSpeciesType = null;
	//---delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//

	String cjmxShJsfInfo = null; // 上海经手费按成交明细计算的组合代码串
	String sqbhShJsfInfo = null; // 上海经手费按申请编号计算的组合代码串
	String cjhzShJsfInfo = null; // 上海经手费按成交汇总计算的组合代码串
	String cjmxShZgfInfo = null; // 上海证管费按成交明细计算的组合代码串
	String sqbhShZgfInfo = null; // 上海证管费按申请编号计算的组合代码串
	String cjhzShZgfInfo = null; // 上海证管费按成交汇总计算的组合代码串
	String cjmxShGhfInfo = null; // 上海过户费按成交明细计算的组合代码串
	String sqbhShGhfInfo = null; // 上海过户费按申请编号计算的组合代码串
	String cjhzShGhfInfo = null; // 上海过户费按成交汇总计算的组合代码串
	String cjmxShYhsInfo = null; // 上海印花税按成交明细计算的组合代码串
	String sqbhShYhsInfo = null; // 上海印花税按申请编号计算的组合代码串
	String cjhzShYhsInfo = null; // 上海印花税按成交汇总计算的组合代码串
	String cjmxShYjInfo = null; // 上海佣金按成交明细计算的组合代码串
	String sqbhShYjInfo = null; // 上海佣金按申请编号计算的组合代码串
	String cjhzShYjInfo = null; // 上海佣金按成交汇总计算的组合代码串
	String cjmxShFxjInfo = null; // 上海风险金按成交明细计算的组合代码串
	String sqbhShFxjInfo = null; // 上海风险金按申请编号计算的组合代码串
	String cjhzShFxjInfo = null; // 上海风险金按成交汇总计算的组合代码串
	String cjmxShJieSuanfInfo = null; // 上海结算费按成交明细计算的组合代码串
	String sqbhShJieSuanfInfo = null; // 上海结算费按申请编号计算的组合代码串
	String cjhzShJieSuanfInfo = null; // 上海结算费按成交汇总计算的组合代码串
	String cjmxSzJsfInfo = null; // 深圳经手费按成交明细计算的组合代码串
	String sqbhSzJsfInfo = null; // 深圳经手费按申请编号计算的组合代码串
	String cjhzSzJsfInfo = null; // 深圳经手费按成交汇总计算的组合代码串
	String cjmxSzZgfInfo = null; // 深圳证管费按成交明细计算的组合代码串
	String sqbhSzZgfInfo = null; // 深圳证管费按申请编号计算的组合代码串
	String cjhzSzZgfInfo = null; // 深圳证管费按成交汇总计算的组合代码串
	String cjmxSzGhfInfo = null; // 深圳过户费按成交明细计算的组合代码串
	String sqbhSzGhfInfo = null; // 深圳过户费按申请编号计算的组合代码串
	String cjhzSzGhfInfo = null; // 深圳过户费按成交汇总计算的组合代码串
	String cjmxSzYhsInfo = null; // 深圳印花税按成交明细计算的组合代码串
	String sqbhSzYhsInfo = null; // 深圳印花税按申请编号计算的组合代码串
	String cjhzSzYhsInfo = null; // 深圳印花税按成交汇总计算的组合代码串
	String cjmxSzYjInfo = null; // 深圳佣金按成交明细计算的组合代码串
	String sqbhSzYjInfo = null; // 深圳佣金按申请编号计算的组合代码串
	String cjhzSzYjInfo = null; // 深圳佣金按成交汇总计算的组合代码串
	String cjmxSzFxjInfo = null; // 深圳风险金按成交明细计算的组合代码串
	String sqbhSzFxjInfo = null; // 深圳风险金按申请编号计算的组合代码串
	String cjhzSzFxjInfo = null; // 深圳风险金按成交汇总计算的组合代码串
	String cjmxSzJieSuanfInfo = null; // 深圳结算费按成交明细计算的组合代码串
	String sqbhSzJieSuanfInfo = null; // 深圳结算费按申请编号计算的组合代码串
	String cjhzSzJieSuanfInfo = null; // 深圳结算费按成交汇总计算的组合代码串

	java.util.Date date = null; // 系统读数日期
	String portCodes = null; // 接口处理界面已选的组合代码
	//delete by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
	//String assetGroupCode = null; // 组合群代码
	
	String dealDataType ="";//获取要处理的Tb_XXX_HzJkMx 中 FJKDM类型
	/**
	 * 构造函数
	 */
	public MXToQSBean() {
	}

	public void makeData(java.util.Date sDate, String portCode, HashMap hmParam)
			throws YssException {
		try {
			portCodes = portCode;
			date = sDate;
			assetGroupCode = pub.getAssetGroupCode();

			CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin(); // 新建CNInterfaceParamAdmin
			interfaceParam.setYssPub(pub);

			// 获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap
			hmReadType = (HashMap) hmParam.get("hmReadType");

			// 获取数据接口参数设置的交易所债券参数设置界面设置的参数对应的HashMap
			hmExchangeBond = (HashMap) hmParam.get("hmExchangeBond");

			// 获取数据接口参数设置的交易费用计算方式界面设置的参数对应的HashMap
			hmTradeFee = (HashMap) hmParam.get("hmTradeFee");

			// 获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
			hmFeeWay = (HashMap) hmParam.get("hmFeeWay");

			// 获取交易费率品种设置界面设置的费率对应的HashMap
			hmRateSpeciesType = (HashMap) hmParam.get("hmRateSpeciesType");

			// 获取券商佣金利率设置界面设置的券商佣金利率对应的HashMap
			hmBrokerRate = (HashMap) hmParam.get("hmBrokerRate");

			// 获取所有已选组合代码对应的股东和席位代码对应的HashMap
			hmPortHolderSeat = (HashMap) hmParam.get("hmPortHolderSeat");

			// 获取上海经手费按成交明细计算的组合代码串
			cjmxShJsfInfo = (String) hmTradeFee.get("01cjmx");

			// 获取上海经手费按申请编号计算的组合代码串
			sqbhShJsfInfo = (String) hmTradeFee.get("01sqbh");

			// 获取上海经手费按成交汇总计算的组合代码串
			cjhzShJsfInfo = (String) hmTradeFee.get("01cjhz");

			// 获取上海证管费按成交明细计算的组合代码串
			cjmxShZgfInfo = (String) hmTradeFee.get("02cjmx");

			// 获取上海证管费按申请编号计算的组合代码串
			sqbhShZgfInfo = (String) hmTradeFee.get("02sqbh");

			// 获取上海证管费按成交汇总计算的组合代码串
			cjhzShZgfInfo = (String) hmTradeFee.get("02cjhz");

			// 获取上海过户费按成交明细计算的组合代码串
			cjmxShGhfInfo = (String) hmTradeFee.get("03cjmx");

			// 获取上海过户费按申请编号计算的组合代码串
			sqbhShGhfInfo = (String) hmTradeFee.get("03sqbh");

			// 获取上海过户费按成交汇总计算的组合代码串
			cjhzShGhfInfo = (String) hmTradeFee.get("03cjhz");

			// 获取上海印花税按成交明细计算的组合代码串
			cjmxShYhsInfo = (String) hmTradeFee.get("04cjmx");

			// 获取上海印花税按申请编号计算的组合代码串
			sqbhShYhsInfo = (String) hmTradeFee.get("04sqbh");

			// 获取上海印花税按成交汇总计算的组合代码串
			cjhzShYhsInfo = (String) hmTradeFee.get("04cjhz");

			// 获取上海佣金按成交明细计算的组合代码串
			cjmxShYjInfo = (String) hmTradeFee.get("05cjmx");

			// 获取上海佣金按申请编号计算的组合代码串
			sqbhShYjInfo = (String) hmTradeFee.get("05sqbh");

			// 获取上海佣金按成交汇总计算的组合代码串
			cjhzShYjInfo = (String) hmTradeFee.get("05cjhz");

			// 获取上海风险金按成交明细计算的组合代码串
			cjmxShFxjInfo = (String) hmTradeFee.get("13cjmx");

			// 获取上海风险金按申请编号计算的组合代码串
			sqbhShFxjInfo = (String) hmTradeFee.get("13sqbh");

			// 获取上海风险金按成交汇总计算的组合代码串
			cjhzShFxjInfo = (String) hmTradeFee.get("13cjhz");

			// 获取上海结算费按成交明细计算的组合代码串
			cjmxShJieSuanfInfo = (String) hmTradeFee.get("11cjmx");

			// 获取上海结算费按申请编号计算的组合代码串
			sqbhShJieSuanfInfo = (String) hmTradeFee.get("11sqbh");

			// 获取上海结算费按成交汇总计算的组合代码串
			cjhzShJieSuanfInfo = (String) hmTradeFee.get("11cjhz");

			// 获取深圳经手费按成交明细计算的组合代码串
			cjmxSzJsfInfo = (String) hmTradeFee.get("06cjmx");

			// 获取深圳经手费按申请编号计算的组合代码串
			sqbhSzJsfInfo = (String) hmTradeFee.get("06sqbh");

			// 获取深圳经手费按成交汇总计算的组合代码串
			cjhzSzJsfInfo = (String) hmTradeFee.get("06cjhz");

			// 获取深圳证管费按成交明细计算的组合代码串
			cjmxSzZgfInfo = (String) hmTradeFee.get("07cjmx");

			// 获取深圳证管费按申请编号计算的组合代码串
			sqbhSzZgfInfo = (String) hmTradeFee.get("07sqbh");

			// 获取深圳证管费按成交汇总计算的组合代码串
			cjhzSzZgfInfo = (String) hmTradeFee.get("07cjhz");

			// 获取深圳过户费按成交明细计算的组合代码串
			cjmxSzGhfInfo = (String) hmTradeFee.get("08cjmx");

			// 获取深圳过户费按申请编号计算的组合代码串
			sqbhSzGhfInfo = (String) hmTradeFee.get("08sqbh");

			// 获取深圳过户费按成交汇总计算的组合代码串
			cjhzSzGhfInfo = (String) hmTradeFee.get("08cjhz");

			// 获取深圳印花税按成交明细计算的组合代码串
			cjmxSzYhsInfo = (String) hmTradeFee.get("09cjmx");

			// 获取深圳印花税按申请编号计算的组合代码串
			sqbhSzYhsInfo = (String) hmTradeFee.get("09sqbh");

			// 获取深圳印花税按成交汇总计算的组合代码串
			cjhzSzYhsInfo = (String) hmTradeFee.get("09cjhz");

			// 获取深圳佣金按成交明细计算的组合代码串
			cjmxSzYjInfo = (String) hmTradeFee.get("10cjmx");

			// 获取深圳佣金按申请编号计算的组合代码串
			sqbhSzYjInfo = (String) hmTradeFee.get("10sqbh");

			// 获取深圳佣金按成交汇总计算的组合代码串
			cjhzSzYjInfo = (String) hmTradeFee.get("10cjhz");

			// 获取深圳风险金按成交明细计算的组合代码串
			cjmxSzFxjInfo = (String) hmTradeFee.get("14cjmx");

			// 获取深圳风险金按申请编号计算的组合代码串
			sqbhSzFxjInfo = (String) hmTradeFee.get("14sqbh");

			// 获取深圳风险金按成交汇总计算的组合代码串
			cjhzSzFxjInfo = (String) hmTradeFee.get("14cjhz");

			// 获取深圳结算费按成交明细计算的组合代码串
			cjmxSzJieSuanfInfo = (String) hmTradeFee.get("12cjmx");

			// 获取深圳结算费按申请编号计算的组合代码串
			sqbhSzJieSuanfInfo = (String) hmTradeFee.get("12sqbh");

			// 获取深圳结算费按成交汇总计算的组合代码串
			cjhzSzJieSuanfInfo = (String) hmTradeFee.get("12cjhz");
			
			//获取要处理的Tb_XXX_HzJkMx 中 FJKDM类型
			dealDataType = (String) hmParam.get("dealDataType");//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	

			MXToHZ(); // 数据从交易接口明细库到交易接口汇总库的处理
			HZToQS(); // 数据从交易接口汇总库到交易接口清算库的处理
		} catch (Exception e) {
			throw new YssException("从交易接口明细库到交易接口清算库的数据处理出错！", e);
		}
	}

	/**
	 * 从明细库到汇总库的处理方法
	 */
	public void MXToHZ() throws YssException {
		Connection con = dbl.loadConnection(); // 新建连接
		PreparedStatement pstmt = null; // 声明PreparedStatement
		boolean bTrans = false;
		String strSql = ""; // 用于储存sql语句
		ResultSet rs = null; // 声明结果集
		try {
			con.setAutoCommit(false); // 设置不自动提交事务
			bTrans = true;
			//---add by songjie 2012.02.17 复原成交金额字段值 start---//
			strSql = " update " + pub.yssGetTableName("Tb_Hzjkmx") + " set FCjje = (FCjsl * FCjjg) where FInDate = " + 
			dbl.sqlDate(date) + " and FPortCode in(" + operSql.sqlCodes(portCodes) + ") and FZqbz = 'ZQ' ";
			dbl.executeSql(strSql);
			//---add by songjie 2012.02.17 复原成交金额字段值 end---//
			
			strSql = " delete from " + pub.yssGetTableName("Tb_HzJkHz")
					+ " where FInDate = " + dbl.sqlDate(date)
					+ " and FPortCode in(" + operSql.sqlCodes(portCodes) + ")";
			dbl.executeSql(strSql);

			strSql = "insert into "
					+ pub.yssGetTableName("Tb_HzJkHz")
					+ "(Fdate, FZqdm, FSzsh, FGddm, Fjyxwh, FBs, FCjsl,"
					+ "Fcjje, Fyhs, FJsf, FGhf, FZgf, Fyj, FGzlx, Fhggain, FZqbz, "
					+ "Fywbz, FSqbh, Fqtf, Zqdm, FJYFS, Ffxj, Findate, "
					+ "FTZBZ, FPortCode, FSqGzlx, FCreator, FCreateTime)values"
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = dbl.openPreparedStatement(strSql); // 将交易接口明细库的数据经过汇总储存到交易接口汇总库中

			strSql = " select FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, FGddm, FJyxwh, FBs, "
					+ "FZqbz, FYwbz, FSqbh, FTzbz, FPortCode, sum(FCjsl) as FCjsl, "
					+ "sum(Fcjje) as Fcjje, sum(Fyhs) as Fyhs, sum(FJsf) as FJsf, sum(FGhf) as FGhf, "
					+ "sum(FZgf) as FZgf, sum(Fyj) as Fyj, sum(FGzlx) as FGzlx, "
					+ "sum(Fhggain) as Fhggain, sum(Fqtf) as Fqtf, sum(Ffxj) as Ffxj, sum(FSqGzlx) as FSqGzlx from "
					+ pub.yssGetTableName("Tb_HzJkMx")
					+ " where FInDate = "
					+ dbl.sqlDate(date)
					//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
					+ " and FPortCode in("
					+ operSql.sqlCodes(portCodes)
					+ ")group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, "
					+ "FSzsh, FGddm, FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode";

			// 根据FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, FGddm, FJyxwh, FBs,
			// FZqbz, FYwbz, FSqbh, FTzbz, FPortCode 字段汇总交易接口明细库中的数据
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				pstmt.setDate(1, rs.getDate("FDate")); // 交易日期
				pstmt.setString(2, rs.getString("FZqdm")); // 转换前的证券代码
				pstmt.setString(3, rs.getString("FSzsh")); // 交易所代码
				pstmt.setString(4, rs.getString("FGddm")); // 股东代码
				pstmt.setString(5, rs.getString("Fjyxwh")); // 席位代码
				pstmt.setString(6, rs.getString("FBs")); // 买卖标志
				pstmt.setDouble(7, rs.getDouble("FCjsl")); // 成交数量
				pstmt.setDouble(8, rs.getDouble("Fcjje")); // 成交金额
				pstmt.setDouble(9, rs.getDouble("Fyhs")); // 印花税
				pstmt.setDouble(10, rs.getDouble("FJsf")); // 经手费
				pstmt.setDouble(11, rs.getDouble("FGhf")); // 过户费
				pstmt.setDouble(12, rs.getDouble("FZgf")); // 证管费
				pstmt.setDouble(13, rs.getDouble("Fyj")); // 佣金
				pstmt.setDouble(14, rs.getDouble("FGzlx")); // 国债利息
				pstmt.setDouble(15, rs.getDouble("Fhggain")); // 回购收益
				pstmt.setString(16, rs.getString("FZqbz")); // 证券标志
				pstmt.setString(17, rs.getString("Fywbz")); // 业务标志
				pstmt.setString(18, rs.getString("FSqbh")); // 申请编号
				pstmt.setDouble(19, rs.getDouble("Fqtf")); // 结算费
				pstmt.setString(20, rs.getString("Zqdm")); // 转换前的证券代码
				pstmt.setString(21, rs.getString("FJYFS")); // 交易方式
				pstmt.setDouble(22, rs.getDouble("FFxj")); // 风险金
				pstmt.setDate(23, rs.getDate("FInDate")); // 系统读数日期
				pstmt.setString(24, rs.getString("FTZBZ")); // 投资标志
				pstmt.setString(25, rs.getString("FPortCode")); // 组合代码
				pstmt.setDouble(26, rs.getDouble("FSqGzlx")); // 税前国债利息
				pstmt.setString(27, pub.getUserCode()); // 创建人
				pstmt
						.setString(28, YssFun
								.formatDatetime(new java.util.Date())); // 创建时间
				pstmt.addBatch();
			}
			pstmt.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交事务
		} catch (Exception e) {
			throw new YssException("处理交易接口明细库数据到交易接口汇总库出错", e);
		} finally {
			dbl.endTransFinal(con, bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pstmt);
		}
	}

	/**
	 * 从汇总库到清算库的处理方法
	 */
	public void HZToQS() throws YssException {
		Connection con = dbl.loadConnection(); // 新建连接
		PreparedStatement pstmt = null; // 声明PreparedStatement
		boolean bTrans = false;
		String strSql = ""; // 用于储存sql语句
		ResultSet rs = null; // 声明结果集
		try {
			con.setAutoCommit(false);
			bTrans = true;

			dealZZGInfo(); // 处理交易接口汇总表中上海债转股的补差金额

			CtlStock stock = new CtlStock();
			stock.setYssPub(pub);

			// 获取SZHB表中所有席位代码对应的券商代码对应的HashMap
			hmBrokerCode = super.getBrokerCode("FJyxwh", "Tb_" + assetGroupCode+ "_HzJkMx", true, date);

			strSql = " delete from " + pub.yssGetTableName("Tb_HzJkQs")
					+ " where FInDate = " + dbl.sqlDate(date)
					+ " and FPortCode in(" + operSql.sqlCodes(portCodes) + ")";
			dbl.executeSql(strSql); // 根据系统日期和已选组合代码删除数据

			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
			
			dealInsertInfo(); // 处理要插入到交易接口清算表的数据操作

			updateJsfAndZgf();//add by songjie 2012.06.07 BUG 4736 QDV4赢时胜(上海)2012年06月05日01_B
			
			dealXGFKInfo(); // 新股返款的数据进行调整，因两地的返款数据均是全额返还的，需减去当天中签的金额
			dealToNextWorkDay(); // 将特殊数据的业务日期调整为下一工作日
			// dealFxjOfCommonSeat();对共用席位的风险金进行调整

			dealZQInfoByInterest();// 计算债券成本时根据利息税入成本的方式（明细/申请编号/汇总）将利息税计入成本，即成交金额=净价+利息税

			dealETFInfo();// ETF申购或赎回的基金的实收实付金额要包含 ETF申购或赎回的股票的过户费.

			adjustCjje();// 调整全价交易的可转债成交金额

			//若为新债中签 则判断业务日期是否大于计息开始日 若大于计息开始日，
			//就计算债券利息，并将百元债券利息更新到债券利息表中
			//add by songjie 2010.03.19 MS00919 QDV4赢时胜（测试）2010年03月18日05_B
			dealXZZQInfo();
		} catch (Exception e) {
			throw new YssException("处理交易接口汇总库数据到交易接口清算库出错", e);
		} finally {
			dbl.endTransFinal(con, bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pstmt);
		}
	}

	/**
	 * 处理要插入到交易接口清算表的数据操作
	 * 
	 * @throws YssException
	 */
	private void dealInsertInfo() throws YssException {
		String strSql = null;
		PreparedStatement pstmt = null;
		HzJkQsBean hzJkQs = null;
		Connection con = dbl.loadConnection();
		boolean bTrans = false;
		try {
			dealGpZqJjInfo(); // 处理原上海过户库和深圳回报库中的股票/债券/基金的交易数据（从表HzJkHz到表HzJkQs）,注意不包括回购

			dealOtherInfo(); // 处理两地新股新债、权益、回购及债转股等业务
			
			con.setAutoCommit(false); // 开启事务
			bTrans = true;

			strSql = " insert into "
					+ pub.yssGetTableName("Tb_HzJkQs")
					+ "(FDate, FInDate, FZqdm, FSzsh, FJyxwh, FBje, FSje, FBsl, FSsl, "
					+ "Fbyj, FSyj, FBjsf, FSjsf, FByhs, FSyhs, FBzgf, FSzgf, FBghf, "
					+ "FSghf, FBgzlx, FSgzlx, FHggain, Fbsfje, Fsssje, FZqbz, "
					+ "Fywbz, FQsbz, FBQTF, FSQTF, ZQDM, FJYFS, FBFXJ, FSFXJ, "
					+ "FTZBZ, FGddm, FPortCode, FCreator, FCreateTime)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
					+ ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = dbl.openPreparedStatement(strSql);

			Iterator iterator = hmHzJkQs.values().iterator();
			while (iterator.hasNext()) {
				hzJkQs = (HzJkQsBean) iterator.next();
				pstmt.setDate(1, YssFun.toSqlDate(hzJkQs.getDate()));
				pstmt.setDate(2, YssFun.toSqlDate(hzJkQs.getInDate()));
				pstmt.setString(3, hzJkQs.getZqdm());
				pstmt.setString(4, hzJkQs.getSzsh());
				pstmt.setString(5, hzJkQs.getJyxwh());
				pstmt.setDouble(6, hzJkQs.getBJe());
				pstmt.setDouble(7, hzJkQs.getSJe());
				pstmt.setDouble(8, hzJkQs.getBSl());
				pstmt.setDouble(9, hzJkQs.getSSl());
				pstmt.setDouble(10, hzJkQs.getBYj());
				pstmt.setDouble(11, hzJkQs.getSYj());
				pstmt.setDouble(12, hzJkQs.getBJsf());
				pstmt.setDouble(13, hzJkQs.getSJsf());
				pstmt.setDouble(14, hzJkQs.getBYhs());
				pstmt.setDouble(15, hzJkQs.getSYhs());
				pstmt.setDouble(16, hzJkQs.getBZgf());
				pstmt.setDouble(17, hzJkQs.getSZgf());
				pstmt.setDouble(18, hzJkQs.getBGhf());
				pstmt.setDouble(19, hzJkQs.getSGhf());
				pstmt.setDouble(20, hzJkQs.getBGzlx());
				pstmt.setDouble(21, hzJkQs.getSGzlx());
				pstmt.setDouble(22, hzJkQs.getHgGain());
				pstmt.setDouble(23, hzJkQs.getBSfje());
				pstmt.setDouble(24, hzJkQs.getSSsje());
				pstmt.setString(25, hzJkQs.getZqbz());
				pstmt.setString(26, hzJkQs.getYwbz());
				pstmt.setString(27, hzJkQs.getQsbz());
				pstmt.setDouble(28, hzJkQs.getBQtf());
				pstmt.setDouble(29, hzJkQs.getSQtf());
				pstmt.setString(30, hzJkQs.getOldZqdm());
				pstmt.setString(31, hzJkQs.getJyfs());
				pstmt.setDouble(32, 0); // 在数据存入清算表中时将风险金置为0
				pstmt.setDouble(33, 0); // 在数据存入清算表中时将风险金置为0
				pstmt.setString(34, hzJkQs.getTzbz());
				pstmt.setString(35, hzJkQs.getGddm());
				pstmt.setString(36, hzJkQs.getPortCode());
				pstmt.setString(37, pub.getUserCode());
				pstmt.setString(38, YssFun.formatDatetime(new java.util.Date()));
				pstmt.addBatch();
			}

			pstmt.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置可以自动提交
		} catch (Exception e) {
			throw new YssException("将交易接口汇总表中的数据经过处理后插入到交易接口清算表中时出错！", e);
		} finally {
			dbl.closeStatementFinal(pstmt);
			dbl.endTransFinal(con, bTrans); // 关闭连接
		}

	}

	/**
	 * add by songjie 2012.06.07
	 * BUG 4736 QDV4赢时胜(上海)2012年06月05日01_B
	 * 根据读数处理方式界面的参数：
	 * 1.佣金包含征管费、经手费，2.回购佣金不包含经手费，
	 * 更新经手费、征管费金额
	 */
	private void updateJsfAndZgf() throws YssException{
		try{
			String strSql = "";
			String assetGroupCode = pub.getAssetGroupCode();
			String[] ptCodes = portCodes.split(",");
			ReadTypeBean readType = null;
			ArrayList alReadType = null;
			for(int i = 0; i < ptCodes.length; i++){
			
				// 获取交易接口参数设置界面的读书处理方式分页相关组合群组合下的参数设置
				readType = (ReadTypeBean) hmReadType.get(assetGroupCode + " " + ptCodes[i]);
				// 获取选项框的参数
				if(readType != null){
					alReadType = (ArrayList) readType.getParameters();
			
					if(alReadType.contains("05")){//表示 佣金包含经手费、征管费被勾选
						//若非回购数据，且 佣金包含经手费、征管费，则征管费 = 0 、经手费 = 0
						strSql = " update " + pub.yssGetTableName("Tb_HzJkQs") + 
						" set FBJSF = 0, FSJSF = 0, FBZGF = 0, FSZGF = 0 " +
						" where FZqbz <> 'HG' and FPortCode = " + dbl.sqlString(ptCodes[i]) + 
						" and FInDate = " + dbl.sqlDate(date);
						dbl.executeSql(strSql);
				
						//若为回购数据，且 1.佣金包含经手费、征管费 2.回购佣金不包含经手费 都被勾选，则征管费 = 0 、经手费 = 0
						if(!alReadType.contains("06")){//表示 回购佣金不包含经手费被勾选
							strSql = " update " + pub.yssGetTableName("Tb_HzJkQs") + 
							" set FBJSF = 0, FSJSF = 0, FBZGF = 0, FSZGF = 0 " +
							" where FZqbz = 'HG' and FPortCode = " + dbl.sqlString(ptCodes[i]) + 
							" and FInDate = " + dbl.sqlDate(date);
							dbl.executeSql(strSql);
						}
					}
				}
			}
		}catch(Exception e){
			throw new YssException("更新经手费、征管费金额出错！");
		}
	}
	
	/**
	 * 处理交易接口汇总表中上海债转股的补差金额
	 * 
	 * @throws YssException
	 */
	private void dealZZGInfo() throws YssException {
		Connection con = dbl.loadConnection(); // 新建连接
		Statement st = null; // 声明Statement
		boolean bTrans = false;
		String strSql = ""; // 用于储存sql语句
		ResultSet rs = null; // 声明ResultSet
		ResultSet rs1 = null; // 声明ResultSet
		double zgsl = 0; // 转股数量
		double zcsl = 0; // 转出数量
		double zgjg = 0; // 转股价格
		double bcje = 0; // 债转股补差金额
		BaseOperDeal bo = null;
		try {
			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			st = con.createStatement(); // 创建Statement

			bo = new BaseOperDeal();

			strSql = " select fzqdm,fcjsl,fjyxwh,fsqbh,zqdm from "
					+ pub.yssGetTableName("Tb_HzJkHz")
					+ " where FInDate = "
					+ dbl.sqlDate(date)
					+ " and fzqbz='GP' and fywbz='KZZGP' and fszsh='CG' order by fzqdm ";
			rs = dbl.openResultSet(strSql); // 在交易接口汇总表中查询上海债转股股票的数据
			while (rs.next()) {
				// 在交易接口汇总表中查询相同相同席位，相同申请编号，原证券代码为债转股股票的原证券代码的数据
				strSql = " select fcjsl from "
						+ pub.yssGetTableName("Tb_HzJkHz")
						+ " where FInDate = "
						+ dbl.sqlDate(date)
						+ " and fzqbz='ZQ' and fywbz='KZZGP' and fszsh='CG' and Zqdm = "
						+ dbl.sqlString(rs.getString("zqdm"))
						+ " and fjyxwh = "
						+ dbl.sqlString(rs.getString("fjyxwh"))
						+ " and fsqbh = "
						+ dbl.sqlString(rs.getString("fsqbh"));
				rs1 = dbl.openResultSet(strSql);
				zgsl = rs.getDouble("fcjsl"); // 转股数量
				while (rs1.next()) {
					zcsl = rs1.getDouble("fcjsl"); // 转出数量
				}

				// 转股价格 = round(转债面额 * 转出数量/转股数量,2)；—截取2位小数

				zgjg = bo.cutDigit(100 * zcsl / zgsl, 2);

				// 债转股补差金额＝转债面额 * 转出数量－ 转股价格 * 转股数量
				bcje = YssD.mul(100, zcsl) - YssD.mul(zgjg, zgsl);
				if (bcje > 0) {
					strSql = "update "
							+ pub.yssGetTableName("Tb_HzJkHz")
							+ " set fcjje = "
							+ bcje
							+ " where FInDate="
							+ dbl.sqlDate(date)
							+ " and fzqbz='GP' and fywbz='KZZGP' and fszsh='CG' and fzqdm="
							+ dbl.sqlString(rs.getString("fzqdm"))
							+ " and fjyxwh= "
							+ dbl.sqlString(rs.getString("fjyxwh"))
							+ " and fsqbh = "
							+ dbl.sqlString(rs.getString("fsqbh"));
					st.addBatch(strSql); // 更新债转股股票的成交金额
				}
				dbl.closeResultSetFinal(rs1);
			}
			st.executeBatch(); // 批处理sql语句

			con.commit(); // 手动提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交事务
		} catch (Exception e) {
			throw new YssException("处理交易接口汇总表中上海债转股的补差金额出错", e);
		} finally {
			dbl.endTransFinal(con, bTrans);
			dbl.closeResultSetFinal(rs, rs1);
			dbl.closeStatementFinal(st);
		}
	}

	/**
	 * cjmxShJsfInfo,portCodes 从已选的组合代码中选择某种费用按成交明细计算的组合代码
	 * 
	 * @param methodInfo
	 *            String
	 * @param selectedPortCodes
	 *            String
	 * @return String
	 */
	public String splitMethodInfo(String methodInfo, String selectedPortCodes) {
		String[] arrPortCodes = methodInfo.split(","); // 用,拆分某种费用按成交明细计算的组合代码
		String[] arrSelPortCodes = selectedPortCodes.split(","); // 用,拆分已选的组合代码
		ArrayList alPortCodes = new ArrayList();
		ArrayList alSelPortCodes = new ArrayList();
		String strPortCodes = "";

		for (int i = 0; i < arrPortCodes.length; i++) { // 循环某种费用按成交明细计算的组合代码
			alPortCodes.add(arrPortCodes[i]); // 将某种费用按成交明细计算的组合代码添加到alPortCodes中
		}

		for (int i = 0; i < arrSelPortCodes.length; i++) { // 循环已选的组合代码
			alSelPortCodes.add(arrSelPortCodes[i]); // 将已选的组合代码添加到alSelPortCodes中
		}

		for (int i = 0; i < arrPortCodes.length; i++) { // 循环某种费用按成交明细计算的组合代码
			if (!alSelPortCodes.contains(arrPortCodes[i])) { // 若已选的组合代码不包含某种费用按成交明细计算的组合代码
				alPortCodes.remove(arrPortCodes[i]); // 将某种费用按成交明细计算的组合代码从alPortCodes中移除
			}
		}

		for (int i = 0; i < alPortCodes.size(); i++) {
			strPortCodes += alPortCodes.get(i) + ","; // 拼接某种费用按成交明细计算的组合代码
		}

		if (strPortCodes.length() >= 1) {
			strPortCodes = strPortCodes.substring(0, strPortCodes.length() - 1); // 移除最后的逗号
		}

		return strPortCodes; // 返回从已选的组合代码中某种费用按成交明细计算的组合代码
	}

	/**
	 * 给feeAttribute设置属性
	 * 
	 * @param feeAttribute
	 *            FeeAttributeBean
	 * @param rs
	 *            ResultSet
	 * @throws YssException
	 */
	private void setFeeAttribute(FeeAttributeBean feeAttribute, ResultSet rs)
			throws YssException {
		try {
			if (feeAttribute.getSelectedFee() != null
					&& feeAttribute.getSelectedFee().equals("FGzlx")) {
				feeAttribute.setFSqbh(rs.getString("FSqbh")); // 申请编号
			}
			feeAttribute.setShsz(rs.getString("fSZSH"));// 交易所代码
			feeAttribute.setTzbz(rs.getString("FTzbz")); // 投资标志
			feeAttribute.setGddm(rs.getString("FGddm")); // 股东代码
			feeAttribute.setFhggain(rs.getDouble("FhgGain")); // 回购收益
			feeAttribute.setJyfs(rs.getString("FJYFS")); // 交易方式
			feeAttribute.setInDate(rs.getDate("FInDate")); // 系统读数日期
			feeAttribute.setOldZqdm(rs.getString("ZQDM")); // 转换前的证券代码
			feeAttribute.setZqdm(rs.getString("FZqdm")); // 转换后的证券代码
			feeAttribute.setGsdm(rs.getString("FJyxwh")); // 席位代码
			feeAttribute.setBs(rs.getString("FBS")); // 买卖标志
			feeAttribute.setDate(rs.getDate("FDate")); // 交易日期
			feeAttribute.setSecuritySign(rs.getString("FZqbz")); // 证券标志
			feeAttribute.setBusinessSign(rs.getString("Fywbz")); // 业务标志
			feeAttribute.setCjsl(rs.getDouble("FCjsl")); // 成交数量
			feeAttribute.setCjje(rs.getDouble("Fcjje")); // 成交金额
			if (feeAttribute.getSelectedFee() == null) {
				feeAttribute.setCjjg(rs.getDouble("Fcjjg")); // 成交价格
			}
			feeAttribute.setPortCode(rs.getString("FPortCode")); // 组合代码
			feeAttribute.setReadType((ReadTypeBean) hmReadType
					.get(assetGroupCode + " " + rs.getString("FPortCode"))); // 读书处理方式的参数
			feeAttribute.setHmBrokerCode(hmBrokerCode); // 用于储存券商代码
			feeAttribute.setHmBrokerRate(hmBrokerRate); // 用于储存佣金利率
			feeAttribute.setHmExchangeBond(hmExchangeBond); // 用于储存债券信息设置参数
			feeAttribute.setHmFeeWay(hmFeeWay); // 用于储存费用承担方向参数
			feeAttribute.setHmRateSpeciesType(hmRateSpeciesType); // 用于储存费用利率信息参数
			//add by songjie 2012.11.08 BUG 6227 QDV4农业银行2012年11月06日01_B
			feeAttribute.setHmTradeFee(hmTradeFee);//用于储存费用计算方式
		} catch (Exception e) {
			throw new YssException("给费用实体类赋值时出错", e);
		}
	}

	/**
	 * 给hzJkQs设置属性
	 * 
	 * @param feeAttribute
	 *            FeeAttributeBean
	 * @param hzJkQs
	 *            HzJkQsBean
	 */
	private void setHzJkQs(FeeAttributeBean feeAttribute, HzJkQsBean hzJkQs) {
		if (feeAttribute.getBs().equals("B")) {
			if (!((feeAttribute.getShsz().equals("CS") 
				&& (feeAttribute.getBusinessSign().equals("MRHG") 
				|| feeAttribute.getBusinessSign().equals("MRHGDQ"))) 
				|| ((feeAttribute.getSecuritySign().equals("XG") 
				|| feeAttribute.getSecuritySign().equals("XZ")) 
				&& feeAttribute.getBusinessSign().indexOf("SG") != -1))) {
				hzJkQs.setBJe(feeAttribute.getCjje()); // 买金额
				hzJkQs.setBSl(feeAttribute.getCjsl()); // 买数量
			}
		}

		if (feeAttribute.getBs().equals("S")) {
			if (!(feeAttribute.getBusinessSign().equals("XQ_RZQZ")
					|| ((feeAttribute.getSecuritySign().equals("XG")  
							|| feeAttribute.getSecuritySign().equals("XZ")) 
							&& ((feeAttribute.getBusinessSign().equals("ZQ")
							|| feeAttribute.getBusinessSign().equals("ZQ_FLKZZ")
							|| feeAttribute.getBusinessSign().equals("ZQ_QYZQ")
							|| feeAttribute.getBusinessSign().equals("ZQ_PSZF")
							|| feeAttribute.getBusinessSign().equals("ZQ_KZZ")
							|| feeAttribute.getBusinessSign().equals("ZQ_ZS")
							|| feeAttribute.getBusinessSign().equals("ZQ_SZ_ZS")
							|| feeAttribute.getBusinessSign().equals("KZZXZ")
							|| feeAttribute.getBusinessSign().equals("FLKZZXZ") 
							|| feeAttribute.getBusinessSign().equals("QYZQXZ"))
							|| feeAttribute.getBusinessSign().indexOf("SG") != -1 
							|| feeAttribute.getBusinessSign().indexOf("FK") != -1)) 
							|| (feeAttribute.getShsz().equals("CS") 
							&& (feeAttribute.getBusinessSign().equals("MCHG") 
							|| feeAttribute.getBusinessSign().equals("MCHGDQ"))))) {
				hzJkQs.setSJe(feeAttribute.getCjje()); // 卖金额
			}

			if (!(((feeAttribute.getSecuritySign().equals("XG") || 
					feeAttribute.getSecuritySign().equals("XZ"))  
					&& ((feeAttribute.getBusinessSign().equals("ZQ")
					|| feeAttribute.getBusinessSign().equals("ZQ_FLKZZ")
					|| feeAttribute.getBusinessSign().equals("ZQ_QYZQ")
					|| feeAttribute.getBusinessSign().equals("ZQ_KZZ")
					|| feeAttribute.getBusinessSign().equals("ZQ_ZS")
					|| feeAttribute.getBusinessSign().equals("ZQ_SZ_ZS")
					|| feeAttribute.getBusinessSign().equals("KZZXZ")
					|| feeAttribute.getBusinessSign().equals("FLKZZXZ") 
					|| feeAttribute.getBusinessSign().equals("QYZQXZ"))
					|| feeAttribute.getBusinessSign().indexOf("SG") != -1 
					|| feeAttribute.getBusinessSign().indexOf("FK") != -1)) 
					|| (feeAttribute.getShsz().equals("CS") 
					&& (feeAttribute.getBusinessSign().equals("MCHG") // BUG4273 modify by zhouwei 20120412 正回购时数量不乘10
					|| feeAttribute.getBusinessSign().equals("MCHGDQ"))))) {
				hzJkQs.setSSl(feeAttribute.getCjsl()); // 卖数量
			}
		}

		if (feeAttribute.getSecuritySign().equals("GP")
				&& feeAttribute.getBusinessSign().equals("KZZGP")) {
			// 成交数量计入买数量，成交金额计入卖金额
			hzJkQs.setBSl(feeAttribute.getCjsl());
			hzJkQs.setSSl(0);
			hzJkQs.setSJe(feeAttribute.getCjje());
			hzJkQs.setBJe(0);
		}

		if (feeAttribute.getSecuritySign().equals("ZQ")
				&& feeAttribute.getBusinessSign().equals("KZZGP")) {
			// 成交数量为卖数量
			hzJkQs.setSSl(feeAttribute.getCjsl());
			hzJkQs.setBSl(0);
		}
		hzJkQs.setHgGain(feeAttribute.getFhggain()); // 回购收益
		hzJkQs.setQsbz("N"); // 清算标志
		hzJkQs.setJyfs(feeAttribute.getJyfs()); // 交易方式
		hzJkQs.setGddm(feeAttribute.getGddm()); // 股东代码
		hzJkQs.setInDate(feeAttribute.getInDate()); // 系统读数日期
		hzJkQs.setDate(feeAttribute.getDate()); // 交易日期
		hzJkQs.setJyxwh(feeAttribute.getGsdm()); // 席位代码
		hzJkQs.setOldZqdm(feeAttribute.getOldZqdm()); // 原证券代码
		hzJkQs.setPortCode(feeAttribute.getPortCode()); // 组合代码
		hzJkQs.setTzbz(feeAttribute.getTzbz()); // 投资标志
		hzJkQs.setYwbz(feeAttribute.getBusinessSign()); // 业务标志
		hzJkQs.setZqbz(feeAttribute.getSecuritySign()); // 证券标志
		hzJkQs.setZqdm(feeAttribute.getZqdm()); // 转换后的证券代码
		hzJkQs.setSzsh(feeAttribute.getShsz()); // 交易所代码
		hzJkQs.setFeeAttribute(feeAttribute);
	}

	/**
	 * 用于计算费用
	 * 
	 * @param rs
	 *            ResultSet
	 * @param feeAttribute
	 *            FeeAttributeBean
	 * @param strShSz
	 *            String
	 * @param keyStr
	 *            String
	 * @throws YssException
	 */
	private void calOneFee(ResultSet rs, String strShSz, String calcuInfo)
			throws YssException {
		String keyStr = null;
		FeeAttributeBean feeAttribute = null;

		CtlStock stock = new CtlStock();
		stock.setYssPub(pub);

		HzJkQsBean hzJkQs = null; // 声明交易接口清算表的实体类

		SHGHBean shgh = new SHGHBean(); // 新建SHGHBean
		shgh.setYssPub(pub);

		SZHBBean szhb = new SZHBBean(); // 新建SZHBBean
		szhb.setYssPub(pub);

		SZGFBean szgf = new SZGFBean(); // 新建SZGFBean
		szgf.setYssPub(pub);

		SZFXBean szfx = new SZFXBean(); // 新建SZFXBean
		szfx.setYssPub(pub);
		try {
			while (rs.next()) {
				feeAttribute = new FeeAttributeBean();
				//begin zhouxiang MS01480 数据接口再导入上海过户库数据信息时报错 
				feeAttribute.setSeatCode(rs.getString("FJyxwh"));
				//end-- zhouxiang MS01480 数据接口再导入上海过户库数据信息时报错
				if (calcuInfo.equals("cjmxShJsfInfo")
						|| calcuInfo.equals("cjmxSzJsfInfo")
						|| calcuInfo.equals("cjhzShJsfInfo")
						|| calcuInfo.equals("cjhzSzJsfInfo")
						|| calcuInfo.equals("sqbhShJsfInfo")
						|| calcuInfo.equals("sqbhSzJsfInfo")) { // 经手费
					feeAttribute.setFJsf(rs.getDouble("FJsf"));
				}

				if (calcuInfo.equals("cjmxShZgfInfo")
						|| calcuInfo.equals("cjmxSzZgfInfo")
						|| calcuInfo.equals("cjhzShZgfInfo")
						|| calcuInfo.equals("cjhzSzZgfInfo")
						|| calcuInfo.equals("sqbhShZgfInfo")
						|| calcuInfo.equals("sqbhSzZgfInfo")) { // 证管费
					feeAttribute.setFZgf(rs.getDouble("FZgf"));
				}

				if (calcuInfo.equals("cjmxShGhfInfo")
						|| calcuInfo.equals("cjmxSzGhfInfo")
						|| calcuInfo.equals("cjhzShGhfInfo")
						|| calcuInfo.equals("cjhzSzGhfInfo")
						|| calcuInfo.equals("sqbhShGhfInfo")
						|| calcuInfo.equals("sqbhSzGhfInfo")) { // 过户费
					feeAttribute.setFGhf(rs.getDouble("FGhf"));
				}

				if (calcuInfo.equals("cjmxShYhsInfo")
						|| calcuInfo.equals("cjmxSzYhsInfo")
						|| calcuInfo.equals("cjhzShYhsInfo")
						|| calcuInfo.equals("cjhzSzYhsInfo")
						|| calcuInfo.equals("sqbhShYhsInfo")
						|| calcuInfo.equals("sqbhSzYhsInfo")) { // 印花税
					feeAttribute.setFYhs(rs.getDouble("FYhs"));
				}

				if (calcuInfo.equals("cjmxShJieSuanfInfo")
						|| calcuInfo.equals("cjmxSzJieSuanfInfo")
						|| calcuInfo.equals("cjhzShJieSuanfInfo")
						|| calcuInfo.equals("cjhzSzJieSuanfInfo")
						|| calcuInfo.equals("sqbhShJieSuanfInfo")
						|| calcuInfo.equals("sqbhSzJieSuanfInfo")) { // 结算费
					feeAttribute.setFqtf(rs.getDouble("FQtf"));
				}

				if (calcuInfo.equals("cjmxShYjInfo")
						|| calcuInfo.equals("cjmxSzYjInfo")
						|| calcuInfo.equals("cjhzShYjInfo")
						|| calcuInfo.equals("cjhzSzYjInfo")
						|| calcuInfo.equals("sqbhShYjInfo")
						|| calcuInfo.equals("sqbhSzYjInfo")) { // 佣金
					feeAttribute.setFJsf(rs.getDouble("FJsf"));
					feeAttribute.setFZgf(rs.getDouble("FZgf"));
					feeAttribute.setFGhf(rs.getDouble("FGhf"));
					feeAttribute.setFqtf(rs.getDouble("Fqtf"));
					feeAttribute.setFYj(rs.getDouble("FYj"));
				}

				feeAttribute.setFGzlx(rs.getDouble("FGzlx"));
				feeAttribute.setFBeforeGzlx(rs.getDouble("FSqGzlx"));

				if (calcuInfo.equals("cjmxShJsfInfo")
						|| calcuInfo.equals("cjmxSzJsfInfo")) { // 经手费
//					if (rs.getString("FSzSh").equals("CS")) {
//						// 深圳的经手费 = 获取的深圳经手费 — 深圳证管费（汇总计算）
//						feeAttribute.setFJsf(YssD.sub(rs.getDouble("FJsf"), rs
//								.getDouble("FZgf")));
//					} else {
//						feeAttribute.setFJsf(rs.getDouble("FJsf"));
//					}
					feeAttribute.setFJsf(rs.getDouble("FJsf"));
				}

				if (calcuInfo.equals("cjmxShZgfInfo")
						|| calcuInfo.equals("cjmxSzZgfInfo")) { // 证管费
					feeAttribute.setFZgf(rs.getDouble("FZgf"));
				}

				if (calcuInfo.equals("cjmxShGhfInfo")
						|| calcuInfo.equals("cjmxSzGhfInfo")) { // 过户费
					feeAttribute.setFGhf(rs.getDouble("FGhf"));
				}

				if (calcuInfo.equals("cjmxShYhsInfo")
						|| calcuInfo.equals("cjmxSzYhsInfo")) { // 印花税
					feeAttribute.setFYhs(rs.getDouble("FYhs"));
				}

				if (calcuInfo.equals("cjmxShYjInfo")
						|| calcuInfo.equals("cjmxSzYjInfo")) { // 佣金
					feeAttribute.setFYj(rs.getDouble("FYj"));
				}

				if (calcuInfo.equals("cjmxShJieSuanfInfo")
						|| calcuInfo.equals("cjmxSzJieSuanfInfo")) { // 结算费
					feeAttribute.setFqtf(rs.getDouble("FQtf"));
				}

				feeAttribute.setShsz(strShSz);
				setFeeAttribute(feeAttribute, rs);

				if (calcuInfo.equals("sqbhShJsfInfo")
						|| calcuInfo.equals("cjhzShJsfInfo")
						|| calcuInfo.equals("sqbhSzJsfInfo")
						|| calcuInfo.equals("cjhzSzJsfInfo")) { // 经手费
					feeAttribute.setSelectedFee("FJsf");
					if (rs.getString("FSzSh").equals("CG")) {
						feeAttribute.setComeFromQS(true);
						shgh.calculateFee(feeAttribute); // 计算上海数据费用
					}
					if (rs.getString("FSzSh").equals("CS")) {
						feeAttribute.setComeFromQS(true);
						szhb.calculateFee(feeAttribute); // 计算深圳数据费用
						szgf.calculateFee(feeAttribute); // 计算深圳股份数据费用
						szfx.calculateFee(feeAttribute); // 计算深圳发行数据费用
					}
				}

				if (calcuInfo.equals("sqbhShZgfInfo")
						|| calcuInfo.equals("cjhzShZgfInfo")
						|| calcuInfo.equals("sqbhSzZgfInfo")
						|| calcuInfo.equals("cjhzSzZgfInfo")) { // 证管费
					feeAttribute.setSelectedFee("FZgf");
					if (rs.getString("FSzSh").equals("CG")) {
						feeAttribute.setComeFromQS(true);
						shgh.calculateFee(feeAttribute); // 计算上海数据费用
					}
					if (rs.getString("FSzSh").equals("CS")) {
						feeAttribute.setComeFromQS(true);
						if(rs.getString("FZqbz").equals("B_GP")){//B股
							feeAttribute.setFJsf(rs.getDouble("FJsf"));//计算证管费的同时计算经手费，此处获得按明细计算的经手费和证管费和的汇总值 panjunfang add 20100429
						}						
						szhb.calculateFee(feeAttribute); // 计算深圳数据费用
						szgf.calculateFee(feeAttribute); // 计算深圳股份数据费用
						szfx.calculateFee(feeAttribute); // 计算深圳发行数据费用
					}
				}

				if (calcuInfo.equals("sqbhShGhfInfo")
						|| calcuInfo.equals("cjhzShGhfInfo")
						|| calcuInfo.equals("sqbhSzGhfInfo")
						|| calcuInfo.equals("cjhzSzGhfInfo")) { // 过户费
					feeAttribute.setSelectedFee("FGhf");
					if (rs.getString("FSzSh").equals("CG")) {
						feeAttribute.setComeFromQS(true);
						shgh.calculateFee(feeAttribute); // 计算上海数据费用
					}
					if (rs.getString("FSzSh").equals("CS")) {
						feeAttribute.setComeFromQS(true);
						szhb.calculateFee(feeAttribute); // 计算深圳数据费用
						szgf.calculateFee(feeAttribute); // 计算深圳股份数据费用
						szfx.calculateFee(feeAttribute); // 计算深圳发行数据费用
					}
				}

				if (calcuInfo.equals("sqbhShYhsInfo")
						|| calcuInfo.equals("cjhzShYhsInfo")
						|| calcuInfo.equals("sqbhSzYhsInfo")
						|| calcuInfo.equals("cjhzSzYhsInfo")) { // 印花税
					feeAttribute.setSelectedFee("FYhs");
					if (rs.getString("FSzSh").equals("CG")) {
						feeAttribute.setComeFromQS(true);
						shgh.calculateFee(feeAttribute); // 计算上海数据费用
					}
					if (rs.getString("FSzSh").equals("CS")) {
						feeAttribute.setComeFromQS(true);
						szhb.calculateFee(feeAttribute); // 计算深圳数据费用
						szgf.calculateFee(feeAttribute); // 计算深圳股份数据费用
						szfx.calculateFee(feeAttribute); // 计算深圳发行数据费用
					}
				}

				if (calcuInfo.equals("sqbhShYjInfo")
						|| calcuInfo.equals("cjhzShYjInfo")
						|| calcuInfo.equals("sqbhSzYjInfo")
						|| calcuInfo.equals("cjhzSzYjInfo")) { // 佣金
					feeAttribute.setSelectedFee("FYj");
					if (rs.getString("FSzSh").equals("CG")) {
						feeAttribute.setComeFromQS(true);
						shgh.calculateFee(feeAttribute); // 计算上海数据费用
					}
					if (rs.getString("FSzSh").equals("CS")) {
						feeAttribute.setComeFromQS(true);
						szhb.calculateFee(feeAttribute); // 计算深圳数据费用
						szgf.calculateFee(feeAttribute); // 计算深圳股份数据费用
						szfx.calculateFee(feeAttribute); // 计算深圳发行数据费用
					}
				}

				if (calcuInfo.equals("sqbhShJieSuanfInfo")
						|| calcuInfo.equals("cjhzShJieSuanfInfo")
						|| calcuInfo.equals("sqbhSzJieSuanfInfo")
						|| calcuInfo.equals("cjhzSzJieSuanfInfo")) { // 结算费
					feeAttribute.setSelectedFee("Fqtf");
					if (rs.getString("FSzSh").equals("CG")) {
						feeAttribute.setComeFromQS(true);
						shgh.calculateFee(feeAttribute); // 计算上海数据费用
					}
					if (rs.getString("FSzSh").equals("CS")) {
						feeAttribute.setComeFromQS(true);
						szhb.calculateFee(feeAttribute); // 计算深圳数据费用
						szgf.calculateFee(feeAttribute); // 计算深圳股份数据费用
						szfx.calculateFee(feeAttribute); // 计算深圳发行数据费用
					}
				}

				keyStr = feeAttribute.getDate().toString() + " "
						+ feeAttribute.getInDate().toString() + " "
						+ feeAttribute.getZqdm() + " " + feeAttribute.getGsdm() 
						+ (rs.getString("FZqbz").equals("B_GP") ? " " + feeAttribute.getGddm() : "")//如果是B股，将股东也作为key，区分不同的投资经理 panjunfang add 20100514
						+ " " + feeAttribute.getJyfs() + " "
						+ feeAttribute.getBusinessSign() + " "
						+ feeAttribute.getPortCode();

				// 若已经有相关证券类型和业务类型的汇总接口清算的实体类
				if (hmHzJkQs.get(keyStr) != null) {
					hzJkQs = (HzJkQsBean) hmHzJkQs.get(keyStr);// 则取出相关实体类
				} else {
					hzJkQs = new HzJkQsBean();// 若没有，就新建实体类
				}

				if (calcuInfo.equals("cjmxShJsfInfo")
						|| calcuInfo.equals("cjmxSzJsfInfo")
						|| calcuInfo.equals("cjhzShJsfInfo")
						|| calcuInfo.equals("cjhzSzJsfInfo")
						|| calcuInfo.equals("sqbhShJsfInfo")
						|| calcuInfo.equals("sqbhSzJsfInfo")) { // 经手费
					if (feeAttribute.getBs().equals("B")) {
						//modify by zhangfa 20100824 MS01591
						 if(calcuInfo.equals("sqbhShJsfInfo")){
							 hzJkQs.setBJsf(YssD.add(hzJkQs.getBJsf(), feeAttribute.getFJsf())); 
						 }else{
							hzJkQs.setBJsf(feeAttribute.getFJsf());
						 }
						//-----------------------------------
					}
					if (feeAttribute.getBs().equals("S")) {
						//modify by zhangfa 20100824 MS01591
						 if(calcuInfo.equals("sqbhShJsfInfo")){
							 hzJkQs.setSJsf(YssD.add(hzJkQs.getSJsf(), feeAttribute.getFJsf())); 
						 }else{
							 hzJkQs.setSJsf(feeAttribute.getFJsf());
						 }
						//-----------------------------------
					}
				}

				if (calcuInfo.equals("cjmxShZgfInfo")
						|| calcuInfo.equals("cjmxSzZgfInfo")
						|| calcuInfo.equals("cjhzShZgfInfo")
						|| calcuInfo.equals("cjhzSzZgfInfo")
						|| calcuInfo.equals("sqbhShZgfInfo")
						|| calcuInfo.equals("sqbhSzZgfInfo")) { // 证管费
					if (feeAttribute.getBs().equals("B")) {
						if(calcuInfo.equals("sqbhSzZgfInfo") || calcuInfo.equals("cjhzSzZgfInfo")){						
							if(rs.getString("FZqbz").equals("B_GP") && rs.getString("FSzSh").equals("CS")){//B股  panjunfang add 20100429
								hzJkQs.setBZgf(YssD.add(hzJkQs.getBZgf(), feeAttribute.getFZgf()));//按申请编号汇总应累加不同申请编号的费用	
								hzJkQs.setBJsf(YssD.sub(hzJkQs.getBJsf(), feeAttribute.getFZgf()));//按申请编号汇总或成交汇总深圳证管费     赋值证管费的同时应将经手费同时赋值到清算表中
							}else{
								hzJkQs.setBZgf(feeAttribute.getFZgf());
							}
						}else{
							//modify by zhangfa 20100824 MS01591
							if(calcuInfo.equals("sqbhShZgfInfo")){
								hzJkQs.setBZgf(YssD.add(hzJkQs.getBZgf(), feeAttribute.getFZgf()));
							}else{
								hzJkQs.setBZgf(feeAttribute.getFZgf());
							}
							//-----------------------------------	
						}		
					}
					if (feeAttribute.getBs().equals("S")) {
						if(calcuInfo.equals("sqbhSzZgfInfo") || calcuInfo.equals("cjhzSzZgfInfo")){											
							if(rs.getString("FZqbz").equals("B_GP")&& rs.getString("FSzSh").equals("CS")){//B股 panjunfang add 20100429
								hzJkQs.setSZgf(YssD.add(hzJkQs.getSZgf(), feeAttribute.getFZgf()));	//按申请编号汇总应累加不同申请编号的费用		
								hzJkQs.setSJsf(YssD.sub(hzJkQs.getSJsf(), feeAttribute.getFZgf()));//经手费 =经手费与证管费之和（按明细计算）-证管费（按汇总计算）
							}else{
								hzJkQs.setSZgf(feeAttribute.getFZgf());
							}
						}else{
							//modify by zhangfa 20100824 MS01591
							if(calcuInfo.equals("sqbhShZgfInfo")){
								hzJkQs.setSZgf(YssD.add(hzJkQs.getSZgf(), feeAttribute.getFZgf()));
							}else{
								hzJkQs.setSZgf(feeAttribute.getFZgf());
							}
							//-----------------------------------
						}
					}
				}

				if (calcuInfo.equals("cjmxShGhfInfo")
						|| calcuInfo.equals("cjmxSzGhfInfo")
						|| calcuInfo.equals("cjhzShGhfInfo")
						|| calcuInfo.equals("cjhzSzGhfInfo")
						|| calcuInfo.equals("sqbhShGhfInfo")
						|| calcuInfo.equals("sqbhSzGhfInfo")) { // 过户费
					if (feeAttribute.getBs().equals("B")) {
						//modify by zhangfa 20100824 MS01591
						if(calcuInfo.equals("sqbhShGhfInfo")){
							hzJkQs.setBGhf(YssD.add(hzJkQs.getBGhf(), feeAttribute.getFGhf()));
						}else{
							hzJkQs.setBGhf(feeAttribute.getFGhf());
						}
						//----------------------------------------
					}
					if (feeAttribute.getBs().equals("S")) {
						//modify by zhangfa 20100824 MS01591
						if(calcuInfo.equals("sqbhShGhfInfo")){
							hzJkQs.setSGhf(YssD.add(hzJkQs.getSGhf(), feeAttribute.getFGhf()));
						}else{
							hzJkQs.setSGhf(feeAttribute.getFGhf());
						}
						//----------------------------------------
					}
				}

				if (calcuInfo.equals("cjmxShYhsInfo")
						|| calcuInfo.equals("cjmxSzYhsInfo")
						|| calcuInfo.equals("cjhzShYhsInfo")
						|| calcuInfo.equals("cjhzSzYhsInfo")
						|| calcuInfo.equals("sqbhShYhsInfo")
						|| calcuInfo.equals("sqbhSzYhsInfo")) { // 印花税
					if (feeAttribute.getBs().equals("B")) {
						if(calcuInfo.equals("sqbhShYhsInfo")){
						//modify by zhangfa 20100824 MS01591
							hzJkQs.setBYhs(YssD.add(hzJkQs.getBYhs(), feeAttribute.getFYhs()));
						}else{
							hzJkQs.setBYhs(feeAttribute.getFYhs());
						}
						//-------------------------------------
					}
					if (feeAttribute.getBs().equals("S")) {
						if(calcuInfo.equals("sqbhShYhsInfo")){
							//modify by zhangfa 20100824 MS01591
								hzJkQs.setSYhs(YssD.add(hzJkQs.getSYhs(), feeAttribute.getFYhs()));
							}else{
								hzJkQs.setSYhs(feeAttribute.getFYhs());
							}
							//-------------------------------------
					}
				}

				if (calcuInfo.equals("cjmxShYjInfo")
						|| calcuInfo.equals("cjmxSzYjInfo")
						|| calcuInfo.equals("cjhzShYjInfo")
						|| calcuInfo.equals("cjhzSzYjInfo")
						|| calcuInfo.equals("sqbhShYjInfo")
						|| calcuInfo.equals("sqbhSzYjInfo")) { // 佣金
					if (feeAttribute.getBs().equals("B")) {
						if(rs.getString("FZqbz").equals("B_GP")){//B股  panjunfang add 20100429
							hzJkQs.setBYj(YssD.add(hzJkQs.getBYj(), feeAttribute.getFYj()));//按申请编号汇总应累加不同申请编号的费用	
						}else{
							//modify by zhangfa 20100824 MS01591
							if(calcuInfo.equals("sqbhShYjInfo")){
								hzJkQs.setBYj(YssD.add(hzJkQs.getBYj(), feeAttribute.getFYj()));
							}else{
								hzJkQs.setBYj(feeAttribute.getFYj());
							}
							//-----------------------------------
						}						
					}
					if (feeAttribute.getBs().equals("S")) {
						if(rs.getString("FZqbz").equals("B_GP")){//B股 panjunfang add 20100429
							hzJkQs.setSYj(YssD.add(hzJkQs.getSYj(), feeAttribute.getFYj()));//按申请编号汇总应累加不同申请编号的费用	
						}else{
							//modify by zhangfa 20100824 MS01591
							if(calcuInfo.equals("sqbhShYjInfo")){
								hzJkQs.setSYj(YssD.add(hzJkQs.getSYj(), feeAttribute.getFYj()));
							}else{
								hzJkQs.setSYj(feeAttribute.getFYj());
							}
							//-----------------------------------
						}						
					}
				}

				if (calcuInfo.equals("cjmxShJieSuanfInfo")
						|| calcuInfo.equals("cjmxSzJieSuanfInfo")
						|| calcuInfo.equals("cjhzShJieSuanfInfo")
						|| calcuInfo.equals("cjhzSzJieSuanfInfo")
						|| calcuInfo.equals("sqbhShJieSuanfInfo")
						|| calcuInfo.equals("sqbhSzJieSuanfInfo")) { // 结算费
					if (feeAttribute.getBs().equals("B")) {
						//modify by zhangfa 20100824 MS01591
						if(calcuInfo.equals("sqbhShJieSuanfInfo")){
							hzJkQs.setBQtf(YssD.add(hzJkQs.getBQtf(), feeAttribute.getFqtf()));
						}else{
							hzJkQs.setBQtf(feeAttribute.getFqtf());
						}
						//-----------------------------------
					}
					if (feeAttribute.getBs().equals("S")) {
						//modify by zhangfa 20100824 MS01591
						if(calcuInfo.equals("sqbhShJieSuanfInfo")){
							hzJkQs.setSQtf(YssD.add(hzJkQs.getSQtf(), feeAttribute.getFqtf()));
						}else{
							hzJkQs.setSQtf(feeAttribute.getFqtf());
						}
						//-----------------------------------
					}
				}

				hzJkQs.setBGzlx(feeAttribute.getFGzlx());

				if (feeAttribute.getBs().equals("B")) {
					hzJkQs.setBGzlx(feeAttribute.getFGzlx());
				}
				if (feeAttribute.getBs().equals("S")) {
					hzJkQs.setSGzlx(feeAttribute.getFGzlx());
				}

				setHzJkQs(feeAttribute, hzJkQs);

				hmHzJkQs.put(keyStr, hzJkQs);
			}
		} catch (Exception e) {
			throw new YssException("计算费用时出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 回购业务时，若设置了参数‘实收实付金额包含佣金’ 则需在计算时买加佣金、卖减佣金
	 * 
	 * @param feeAttribute
	 *            FeeAttributeBean
	 * @param hzJkQs
	 *            HzJkQsBean
	 */
	private void dealBSfjeAndSSsje(FeeAttributeBean feeAttribute,
			HzJkQsBean hzJkQs) throws SQLException {
		ReadTypeBean readType = null;
		double bSfje = 0; // 买实付金额
		double sSsje = 0; // 卖实收金额
		boolean bSfjeFlag=false;//判断买实付金额是否发生变化
		boolean sSsjeFlag=false;//判断卖实付金额是否发生变化
		String keyStr = null;

		// 获取交易接口参数设置界面的读书处理方式分页相关组合群组合下的参数设置
		readType = (ReadTypeBean) hmReadType.get(assetGroupCode + " "
				+ feeAttribute.getPortCode());
		// 获取选项框的参数
		ArrayList alReadType = (ArrayList) readType.getParameters();
		
		//若设置了参数‘佣金包含经手费，征管费’   update by guolongchao 20120217 STORY 2261-----start
		// 表示数据接口参数设置中设置了实收实付金额包含佣金这个参数
		if (alReadType.contains("04")) // 若设置了参数‘实收实付金额包含佣金’，则需在计算时买加佣金
		{
			if (feeAttribute.getBs().equals("B")) 
			{
				 bSfje = hzJkQs.getBSfje() + hzJkQs.getBYj();
				 bSfjeFlag=true;
				 //若设置了参数‘佣金包含经手费，征管费’
				 if (alReadType.contains("05"))//佣金包含经手费，征管费
				 {
					 if(feeAttribute.getSecuritySign().equals("HG"))
					 {
						 if (!alReadType.contains("06"))//回购佣金包含经手费
						 {
							 bSfje=bSfje-hzJkQs.getBJsf();
							 bSfjeFlag=true;
						 }						
					 }
					 else
					 {
						 bSfje=bSfje-hzJkQs.getBJsf()-hzJkQs.getBZgf();
						 bSfjeFlag=true;
					 }					
				}						 
			}
			// 若设置了参数‘实收实付金额包含佣金’，则需在计算时卖减佣金
			if (feeAttribute.getBs().equals("S")) 
			{
				sSsje = hzJkQs.getSSsje() - hzJkQs.getSYj();
				sSsjeFlag=true;
				//若设置了参数‘佣金包含经手费，征管费’
				 if (alReadType.contains("05"))//佣金包含经手费，征管费
				 {
					 if(feeAttribute.getSecuritySign().equals("HG"))
					 {
						 if (!alReadType.contains("06"))//回购佣金包含经手费
						 {
							 sSsje=sSsje+hzJkQs.getSJsf();
							 sSsjeFlag=true;
						 }						
					 }
					 else
					 {
						 sSsje=sSsje+hzJkQs.getSJsf()+hzJkQs.getSZgf();
						 sSsjeFlag=true;
					 }					
				 }				
			}
		}
		else//佣金不用计入实收实付金额
		{
			if (feeAttribute.getBs().equals("B"))
			{
				if (alReadType.contains("05"))//佣金包含经手费，征管费
				{
					 if(feeAttribute.getSecuritySign().equals("HG"))
					 {
						 if (!alReadType.contains("06"))//回购佣金包含经手费
						 {
							 bSfje=hzJkQs.getBSfje()-hzJkQs.getBJsf();
							 bSfjeFlag=true;
						 }						
					 }
					 else
					 {
						 bSfje=hzJkQs.getBSfje()-hzJkQs.getBJsf()-hzJkQs.getBZgf();
						 bSfjeFlag=true;
					 }					
				}
			}			
			if (feeAttribute.getBs().equals("S")) 
			{
				if (alReadType.contains("05"))//佣金包含经手费，征管费
				{
					 if(feeAttribute.getSecuritySign().equals("HG"))
					 {
						 if (!alReadType.contains("06"))//回购佣金包含经手费
						 {
							 sSsje=hzJkQs.getSSsje()+hzJkQs.getSJsf();
							 sSsjeFlag=true;
						 }						
					 }
					 else
					 {
						 sSsje=hzJkQs.getSSsje()+hzJkQs.getSJsf()+hzJkQs.getSZgf();
						 sSsjeFlag=true;
					 }
				}
			}
		}

		if (feeAttribute.getBs().equals("B")&&bSfjeFlag) {
			hzJkQs.setBSfje(bSfje);
		}
		if (feeAttribute.getBs().equals("S")&&sSsjeFlag) {
			hzJkQs.setSSsje(sSsje);
		}
		//若设置了参数‘佣金包含经手费，征管费’   update by guolongchao 20120217 STORY 2261-----end
		keyStr = feeAttribute.getDate().toString() + " "
				+ feeAttribute.getInDate().toString() + " "
				+ feeAttribute.getZqdm() + " " + feeAttribute.getGsdm() + " "
				+ (feeAttribute.getSecuritySign().equals("B_GP") ? feeAttribute.getGddm() + " " : "") //如果是B股，将股东代码也做为key，区分不同投资经理 panjunfang add 20100514
				+ feeAttribute.getJyfs() + " " + feeAttribute.getBusinessSign()
				+ " " + feeAttribute.getPortCode();

		hmHzJkQs.put(keyStr, hzJkQs);
	}

	/**
	 * 用于计算国债利息
	 * 
	 * @param assetGroupCode
	 *            String
	 * @param fPortCode
	 *            String
	 * @param cjsl
	 *            int
	 * @param fywbz
	 *            String
	 * @param zqdm
	 *            String
	 * @param stock
	 *            CtlStock
	 * @param readType
	 *            ReadTypeBean
	 * @param exchangeBond
	 *            ExchangeBondBean
	 * @param fDate
	 *            Date
	 * @return double
	 * @throws YssException
	 */
	public FeeAttributeBean calculateGzlx(ResultSet rs,
			FeeAttributeBean feeAttribute) throws YssException {
		double gzlx = 0; // 国债利息
		ExchangeBondBean exchangeBond = null; // 声明ExchangeBondBean的实体类
		ReadTypeBean readType = null; // 声明ReadTypeBean的实体类
		String fPortCode = null; // 声明组合代码
		double cjsl = 0; // 初始化成交数量
		String fywbz = null; // 声明业务标志
		String zqdm = null; // 声明证券代码

		java.util.Date fDate = null; // 声明交易日期
		String szsh = null; // 声明交易所代码
		HashMap hmZQRate = null;
		HashMap hmPerZQRate = null;
		String haveInfo = null;
		String bs = "";// 买卖标志 add by songjie 2009.12.21 MS00847
		// QDV4赢时胜（北京）2009年11月30日03_B
		try {
			fPortCode = rs.getString("FPortCode"); // 组合代码
			cjsl = rs.getDouble("FCjsl"); // 成交数量
			fywbz = rs.getString("Fywbz"); // 业务标志
			zqdm = rs.getString("FZqdm"); // 转换后的证券代码
			fDate = rs.getDate("FDate"); // 交易日期
			szsh = rs.getString("FSzsh"); // 交易所代码
			gzlx = rs.getDouble("FGzlx");// 债券利息
			// 买卖标志 add by songjie 2009.12.21 MS00847 QDV4赢时胜（北京）2009年11月30日03_B
			bs = rs.getString("FBS");
			// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
			// edit by songjie 2009.12.21 MS00847 QDV4赢时胜（北京）2009年11月30日03_B
			hmZQRate = super.calculateZQRate(zqdm, fDate, bs, fPortCode);// 计算债券的每百元债券利息，税前每百元债券利息，税后每百元债券利息储存到哈希表中

			// 获取交易接口参数设置的读书处理方式的参数数据
			readType = (ReadTypeBean) hmReadType.get(assetGroupCode + " "
					+ fPortCode);

			if (fywbz.equalsIgnoreCase("MR_GZ") || fywbz.equalsIgnoreCase("MC_GZ") ||
				fywbz.equalsIgnoreCase("MR_DFZFZ") || fywbz.equalsIgnoreCase("MC_DFZFZ")	) { // 国债的买入或卖出
				if (szsh.equals("CG")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关上交所的国债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 01 01");
				}

				if (szsh.equals("CS")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关深交所的国债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 02 01");
				}

				if (exchangeBond.getBondTradeType().equals("01")) { // 全价交易
					hmPerZQRate = super.getPerHundredZQRate(zqdm, fDate);

					haveInfo = (String) hmPerZQRate.get("haveInfo");

					if (haveInfo.equals("true")) {
						// 如果国债利息库中已存在对应品种的债券利息，直接取国债利息库的每百元债券利息
						gzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double
								.parseDouble(((String) hmPerZQRate
										.get("PerGZLX"))), readType
								.getExchangePreci()), cjsl), 2);
					} else {
						// 如果国债利息库中不存在对应品种的债券利息，则需根据维护的债券信息计算出每百元债券利息
						gzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double
								.parseDouble((String) hmZQRate.get("GZLX")),
								readType.getExchangePreci()), cjsl), 2);
					}
				}
			}

			if (fywbz.equalsIgnoreCase("MR_QYZQ")
					|| fywbz.equalsIgnoreCase("MC_QYZQ")) { // 企业债的买入和卖出
				if (szsh.equals("CG")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关上交所的企业债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 01 02");
				}
				if (szsh.equals("CS")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关深交所的企业债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 02 02");
				}
			}

			if (fywbz.equalsIgnoreCase("MR_FLKZZ")
					|| fywbz.equalsIgnoreCase("MC_FLKZZ")) { // 分离可转债的买入和卖出
				if (szsh.equals("CG")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关上交所的分离可转债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 01 04");
				}

				if (szsh.equals("CS")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关深交所的分离可转债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 02 04");
				}
			}

			if (fywbz.equalsIgnoreCase("MR_QYZQ_GS")
					|| fywbz.equalsIgnoreCase("MC_QYZQ_GS")) { // 公司债的买入和卖出
				if (szsh.equals("CG")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关上交所的公司债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 01 05");
				}

				if (szsh.equals("CS")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关深交所的公司债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 02 05");
				}
			}

			if (fywbz.equalsIgnoreCase("KZZGP")
					|| fywbz.equalsIgnoreCase("MR_KZZ")
					|| fywbz.equalsIgnoreCase("MC_KZZ")) {// 可转债债券
				if (szsh.equals("CG")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关上交所的可转债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 01 03");
				}

				if (szsh.equals("CS")) {
					// 获取交易接口参数设置的交易所债券参数设置分页设置的有关深交所的可转债的参数数据
					exchangeBond = (ExchangeBondBean) hmExchangeBond
							.get(assetGroupCode + " " + fPortCode + " 02 03");
				}
			}

			if (fywbz.equalsIgnoreCase("MR_QYZQ")
					|| fywbz.equalsIgnoreCase("MC_QYZQ")
					|| // 企业债的买入和卖出
					fywbz.equalsIgnoreCase("MR_FLKZZ")
					|| fywbz.equalsIgnoreCase("MC_FLKZZ")
					|| // 分离可转债的买入和卖出
					fywbz.equalsIgnoreCase("MR_QYZQ_GS")
					|| fywbz.equalsIgnoreCase("MC_QYZQ_GS")
					|| // 公司债的买入和卖出
					fywbz.equalsIgnoreCase("KZZGP")
					|| fywbz.equalsIgnoreCase("MR_KZZ")
					|| fywbz.equalsIgnoreCase("MC_KZZGP")) { // 可转债债券
				if (exchangeBond.getBondTradeType().equals("01")) { // 全价交易
					// 对于非国债，需根据维护的债券信息计算出税前或税后的债券利息
					gzlx = YssFun.roundIt(YssD.mul(Double
							.parseDouble((String) hmZQRate.get("GZLX")), cjsl),
							2);

				}
			}

			feeAttribute.setCjsl(cjsl);
			feeAttribute.setFGzlx(gzlx);

			return feeAttribute;
		} catch (Exception e) {
			throw new YssException("计算国债利息出错", e);
		}
	}

	/**
	 * 全价交易的债券需在此根据交易接口汇总表中的数据再计算债券利息
	 * 
	 * @throws YssException
	 */
	private void dealZQFee() throws YssException {
		String strSql = null;
		ResultSet rs = null;
		FeeAttributeBean feeAttribute = null; // 声明费用参数设置的实体类
		double cjsl = 0;
		double gzlx = 0;
		ArrayList alHzJkHz = new ArrayList();
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null;
		try {
			st = con.createStatement(); // 新建Statement

			strSql = " select * from " + pub.yssGetTableName("Tb_HzJkHz")
					+ " where FInDate = " + dbl.sqlDate(date)
					+ " and FZqbz = 'ZQ' and FYwbz <> 'KZZGP'";
			rs = dbl.openResultSet(strSql); // 查询交易接口汇总表中证券标志为债券的数据
			while (rs.next()) {
				feeAttribute = new FeeAttributeBean();
				feeAttribute.setSelectedFee("FGzlx");
				setFeeAttribute(feeAttribute, rs);
				cjsl = rs.getDouble("FCjsl"); // 成交数量
				gzlx = rs.getDouble("FGzlx"); // 国债利息
				// 全价交易的债券需在此根据汇总数据再计算债券利息
				feeAttribute = calculateGzlx(rs, feeAttribute);
				// 若计算后的国债利息不等于计算前的国债利息或计算后的成交数量不等于计算前的成交数量
				if (cjsl != feeAttribute.getCjsl()
						|| gzlx != feeAttribute.getFGzlx()) {
					alHzJkHz.add(feeAttribute); // 则添加feeAttribute到alHzJkHz
				}
			}

			con.setAutoCommit(false); // 开启事务
			bTrans = true;

			Iterator iterator = alHzJkHz.iterator(); // 获取alHzJkHz的迭代器
			while (iterator.hasNext()) { // 循环迭代器
				feeAttribute = (FeeAttributeBean) iterator.next(); // 获取feeAttribute
				strSql = " update " + pub.yssGetTableName("Tb_HzJkHz")
						+ " set FCjsl = " + feeAttribute.getCjsl()
						+ ", FGzlx = " + feeAttribute.getFGzlx()
						+ " where FDate = "
						+ dbl.sqlDate(feeAttribute.getDate()) + " and FZqdm = "
						+ dbl.sqlString(feeAttribute.getZqdm())
						+ " and FGddm = "
						+ dbl.sqlString(feeAttribute.getGddm())
						+ " and FJyxwh = "
						+ dbl.sqlString(feeAttribute.getGsdm()) + " and FBs = "
						+ dbl.sqlString(feeAttribute.getBs()) + " and Fywbz = "
						+ dbl.sqlString(feeAttribute.getBusinessSign())
						+ " and FSqbh = "
						+ dbl.sqlString(feeAttribute.getFSqbh())
						+ " and FPortCode = "
						+ dbl.sqlString(feeAttribute.getPortCode());
				st.addBatch(strSql); // 更新交易接口汇总表中相关的债券的成交数量和国债利息字段的数据
			}
			st.executeBatch(); // 批量执行

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置可以自动提交
		} catch (Exception e) {
			throw new YssException("计算债券利息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs); // 关闭结果集
			dbl.endTransFinal(con, bTrans); // 关闭连接
			dbl.closeStatementFinal(st); // 关闭Statement
		}
	}

	/**
	 * 处理原上海过户库和深圳回报库中的股票/债券/基金 的交易数据（从表HzJkHz到表HzJkQs）,注意不包括回购
	 * 
	 * @param date
	 *            Date
	 * @param tradeDate
	 *            Date
	 * @param portCodes
	 *            String
	 * @throws YssException
	 */
	private void dealGpZqJjInfo() throws YssException {
		Connection con = dbl.loadConnection(); // 新建连接
		PreparedStatement pstmt = null; // 声明PreparedStatement
		boolean bTrans = false;
		ResultSet rs = null; // 声明结果集
		String setPortCodes = "";
		String strShSz = "";
		FeeAttributeBean feeAttribute = null;
		ExchangeBondBean exchangeBond = null;
		try {
			con.setAutoCommit(false); // 设置非自动提交事务
			bTrans = true;

			CtlStock stock = new CtlStock();
			stock.setYssPub(pub);

//			dealZQFee(); // 全价交易的债券需在此根据交易接口汇总表中的数据再计算债券利息

			// ----处理原上海过户库中的股票/债券/基金的交易数据----//
			SHGHBean shgh = new SHGHBean(); // 新建SHGHBean
			shgh.setYssPub(pub);

			// ----上海经手费----//
			if (cjmxShJsfInfo != null) {
				// 从已选的组合代码中选择上海经手费按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxShJsfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxShJsfInfo"); // 计算经手费的费用
			}

			if (sqbhShJsfInfo != null) {
				// 从已选的组合代码中选择上海经手费按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhShJsfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhShJsfInfo");
			}

			if (cjhzShJsfInfo != null) {
				// 从已选的组合代码中选择上海经手费按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzShJsfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzShJsfInfo");
			}

			if (cjmxShJsfInfo == null && sqbhShJsfInfo == null
					&& cjhzShJsfInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置上海经手费的计算方式！");
			}
			// ----上海经手费----//

			// ----上海证管费----//
			if (cjmxShZgfInfo != null) {
				// 从已选的组合代码中选择上海证管费按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxShZgfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxShZgfInfo");
			}

			if (sqbhShZgfInfo != null) {
				// 从已选的组合代码中选择上海证管费按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhShZgfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhShZgfInfo");
			}

			if (cjhzShZgfInfo != null) {
				// 从已选的组合代码中选择上海证管费按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzShZgfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzShZgfInfo");
			}

			if (cjmxShZgfInfo == null && sqbhShZgfInfo == null
					&& cjhzShZgfInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置上海证管费的计算方式！");
			}
			// ----上海证管费----//

			// ----上海过户费----//
			if (cjmxShGhfInfo != null) {
				// 从已选的组合代码中选择上海过户费按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxShGhfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxShGhfInfo");
			}

			if (sqbhShGhfInfo != null) {
				// 从已选的组合代码中选择上海过户费按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhShGhfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhShGhfInfo");
			}

			if (cjhzShGhfInfo != null) {
				// 从已选的组合代码中选择上海过户费按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzShGhfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzShGhfInfo");
			}

			if (cjmxShGhfInfo == null && sqbhShGhfInfo == null
					&& cjhzShGhfInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置上海过户费的计算方式！");
			}
			// ----上海过户费----//

			// ----上海印花税----//
			if (cjmxShYhsInfo != null) {
				// 从已选的组合代码中选择上海印花税按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxShYhsInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxShYhsInfo");
			}

			if (sqbhShYhsInfo != null) {
				// 从已选的组合代码中选择上海印花税按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhShYhsInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhShYhsInfo");
			}

			if (cjhzShYhsInfo != null) {
				// 从已选的组合代码中选择上海印花税按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzShYhsInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzShYhsInfo");
			}

			if (cjmxShYhsInfo == null && sqbhShYhsInfo == null
					&& cjhzShYhsInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置上海印花税的计算方式！");
			}
			// ----上海印花税----//

			// ----上海佣金----//
			if (cjmxShYjInfo != null) {
				// 从已选的组合代码中选择上海佣金按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxShYjInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxShYjInfo");
			}

			if (sqbhShYjInfo != null) {
				// 从已选的组合代码中选择上海佣金按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhShYjInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhShYjInfo");
			}

			if (cjhzShYjInfo != null) {
				// 从已选的组合代码中选择上海佣金按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzShYjInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzShYjInfo");
			}

			if (cjmxShYjInfo == null && sqbhShYjInfo == null
					&& cjhzShYjInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置上海佣金的计算方式！");
			}
			// ----上海佣金----//

			// ----上海结算费----//
			if (cjmxShJieSuanfInfo != null) {
				// 从已选的组合代码中选择上海结算费按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxShJieSuanfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxShJieSuanfInfo");
			}

			if (sqbhShJieSuanfInfo != null) {
				// 从已选的组合代码中选择上海结算费按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhShJieSuanfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhShJieSuanfInfo");
			}

			if (cjhzShJieSuanfInfo != null) {
				// 从已选的组合代码中选择上海结算费按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzShJieSuanfInfo, portCodes);
				strShSz = "CG"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzShJieSuanfInfo");
			}

			if (cjmxShJieSuanfInfo == null && sqbhShJieSuanfInfo == null
					&& cjhzShJieSuanfInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置上海结算费的计算方式！");
			}
			// ----上海结算费----//

			// ----深圳经手费----//
			if (cjmxSzJsfInfo != null) {
				// 从已选的组合代码中选择深圳经手费按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxSzJsfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxSzJsfInfo");
			}

			if (sqbhSzJsfInfo != null) {
				// 从已选的组合代码中选择深圳经手费按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhSzJsfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhSzJsfInfo");
			}

			if (cjhzSzJsfInfo != null) {
				// 从已选的组合代码中选择深圳经手费按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzSzJsfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzSzJsfInfo");
			}

			if (cjmxSzJsfInfo == null && sqbhSzJsfInfo == null
					&& cjhzSzJsfInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置深圳经手费的计算方式！");
			}
			// ----深圳经手费----//

			// ----深圳证管费----//
			if (cjmxSzZgfInfo != null) {
				// 从已选的组合代码中选择深圳证管费按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxSzZgfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxSzZgfInfo");
			}

			if (sqbhSzZgfInfo != null) {
				// 从已选的组合代码中选择深圳证管费按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhSzZgfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhSzZgfInfo");
			}

			if (cjhzSzZgfInfo != null) {
				// 从已选的组合代码中选择深圳证管费按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzSzZgfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzSzZgfInfo");
			}

			if (cjmxSzZgfInfo == null && sqbhSzZgfInfo == null
					&& cjhzSzZgfInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置深圳证管费的计算方式！");
			}
			// ----深圳证管费----//

			// ----深圳过户费----//
			if (cjmxSzGhfInfo != null) {
				// 从已选的组合代码中选择深圳过户费按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxSzGhfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxSzGhfInfo");
			}

			if (sqbhSzGhfInfo != null) {
				// 从已选的组合代码中选择深圳过户费按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhSzGhfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhSzGhfInfo");
			}

			if (cjhzSzGhfInfo != null) {
				// 从已选的组合代码中选择深圳过户费按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzSzGhfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzSzGhfInfo");
			}

			if (cjmxSzGhfInfo == null && sqbhSzGhfInfo == null
					&& cjhzSzGhfInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置深圳过户费的计算方式！");
			}
			// ----深圳过户费----//

			// ----深圳印花税----//
			if (cjmxSzYhsInfo != null) {
				// 从已选的组合代码中选择深圳印花税按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxSzYhsInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxSzYhsInfo");
			}

			if (sqbhSzYhsInfo != null) {
				// 从已选的组合代码中选择深圳印花税按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhSzYhsInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhSzYhsInfo");
			}

			if (cjhzSzYhsInfo != null) {
				// 从已选的组合代码中选择深圳印花税按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzSzYhsInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzSzYhsInfo");
			}

			if (cjmxSzYhsInfo == null && sqbhSzYhsInfo == null
					&& cjhzSzYhsInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置深圳印花税的计算方式！");
			}
			// ----深圳印花税----//

			// ----深圳佣金----//
			if (cjmxSzYjInfo != null) {
				// 从已选的组合代码中选择深圳佣金按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxSzYjInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxSzYjInfo");
			}

			if (sqbhSzYjInfo != null) {
				// 从已选的组合代码中选择深圳佣金按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhSzYjInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhSzYjInfo");
			}

			if (cjhzSzYjInfo != null) {
				// 从已选的组合代码中选择深圳佣金按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzSzYjInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzSzYjInfo");
			}

			if (cjmxSzYjInfo == null && sqbhSzYjInfo == null
					&& cjhzSzYjInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置深圳佣金的计算方式！");
			}
			// ----深圳佣金----//

			// ----深圳结算费----//
			if (cjmxSzJieSuanfInfo != null) {
				// 从已选的组合代码中选择深圳结算费按成交明细计算的组合代码
				setPortCodes = splitMethodInfo(cjmxSzJieSuanfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjmxSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjmxSzJieSuanfInfo");
			}

			if (sqbhSzJieSuanfInfo != null) {
				// 从已选的组合代码中选择深圳结算费按申请编号计算的组合代码
				setPortCodes = splitMethodInfo(sqbhSzJieSuanfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getSqbhSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "sqbhSzJieSuanfInfo");
			}

			if (cjhzSzJieSuanfInfo != null) {
				// 从已选的组合代码中选择深圳结算费按成交汇总计算的组合代码
				setPortCodes = splitMethodInfo(cjhzSzJieSuanfInfo, portCodes);
				strShSz = "CS"; // 设置交易所
				rs = dbl.openResultSet(getCjhzSql(strShSz, setPortCodes)); // 打开结果集
				calOneFee(rs, strShSz, "cjhzSzJieSuanfInfo");
			}

			if (cjmxSzJieSuanfInfo == null && sqbhSzJieSuanfInfo == null
					&& cjhzSzJieSuanfInfo == null) {
				throw new YssException("请在数据接口参数设置的费用计算方式分页设置深圳结算费的计算方式！");
			}
			// ----深圳结算费----//

			Iterator iterator = hmHzJkQs.values().iterator();

			while (iterator.hasNext()) {
				HzJkQsBean hzJkQs = (HzJkQsBean) iterator.next();
				feeAttribute = hzJkQs.getFeeAttribute();

				if (hzJkQs.getZqbz().equals("ZQ")) {
					if (feeAttribute.getBusinessSign().equals("MR_GZ") || feeAttribute.getBusinessSign().equals("MC_GZ") ||
						feeAttribute.getBusinessSign().equals("MR_DFZFZ") || feeAttribute.getBusinessSign().equals("MC_DFZFZ")	) {
						if (feeAttribute.getShsz().equals("CG")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 01 01");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置上交所国债的相关参数！");
							}
						}
						if (feeAttribute.getShsz().equals("CS")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 02 01");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置深交所国债的相关参数！");
							}
						}
					}

					// 企业债的买入或卖出
					if (feeAttribute.getBusinessSign().equals("MR_QYZQ")
							|| feeAttribute.getBusinessSign().equals("MC_QYZQ")) {
						if (feeAttribute.getShsz().equals("CG")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 01 02");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置上交所企业债的相关参数！");
							}
						}
						if (feeAttribute.getShsz().equals("CS")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 02 02");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置深交所企业债的相关参数！");
							}
						}
					}

					// 可转债的买入或卖出
					if (feeAttribute.getBusinessSign().equals("MR_KZZ")
							|| feeAttribute.getBusinessSign().equals("MC_KZZ")
							|| feeAttribute.getBusinessSign().equals("KZZGP")
							|| feeAttribute.getBusinessSign().equals("KZZHS")) {
						if (feeAttribute.getShsz().equals("CG")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 01 03");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置上交所可转债的相关参数！");
							}
						}
						if (feeAttribute.getShsz().equals("CS")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 02 03");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置深交所可转债的相关参数！");
							}
						}
					}

					// 分离可转债的买入或卖出
					if (feeAttribute.getBusinessSign().equals("MR_FLKZZ")
							|| feeAttribute.getBusinessSign()
									.equals("MC_FLKZZ")) {
						if (feeAttribute.getShsz().equals("CG")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 01 04");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置上交所分离可转债的相关参数！");
							}
						}
						if (feeAttribute.getShsz().equals("CS")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 02 04");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置深交所分离可转债的相关参数！");
							}
						}
					}

					// 公司债的买入或卖出
					if (feeAttribute.getBusinessSign().equals("MC_QYZQ_GS")
							|| feeAttribute.getBusinessSign().equals(
									"MR_QYZQ_GS")) {
						if (feeAttribute.getShsz().equals("CG")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 01 05");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置上交所公司债的相关参数！");
							}
						}
						if (feeAttribute.getShsz().equals("CS")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 02 05");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置深交所公司债的相关参数！");
							}
						}
					}

					// 资产证券化产品
					if (feeAttribute.getBusinessSign().equals("MR_ZCZQ")
							|| feeAttribute.getBusinessSign().equals("MC_ZCZQ")) {
						if (feeAttribute.getShsz().equals("CG")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 01 06");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置上交所资产证券化产品的相关参数！");
							}
						}
						if (feeAttribute.getShsz().equals("CS")) {
							// 获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
							exchangeBond = (ExchangeBondBean) hmExchangeBond
									.get(assetGroupCode + " "
											+ feeAttribute.getPortCode()
											+ " 02 06");

							if (exchangeBond == null) {
								throw new YssException(
										"请在数据接口参数设置的交易所债券参数设置界面设置深交所资产证券化产品的相关参数！");
							}
						}
					}

					if (exchangeBond.getBondTradeType().equals("00")) { // 净价交易
						HashMap hmZQRate = super.calculateZQRate(hzJkQs.getZqdm(), hzJkQs.getDate(), feeAttribute.getBs(), hzJkQs.getPortCode());
						//全价金额 = round((税前百元债券利息 + 成交价格) * 成交数量,2)
						double jsCjje = YssD.round(YssD.mul(YssD.add(Double.parseDouble((String) hmZQRate.get("SQGZLX")), 
								feeAttribute.getCjjg()), feeAttribute.getCjsl()), 2);
						if (feeAttribute.getBs().equals("B")) {
							hzJkQs.setBJe(feeAttribute.getCjje());
//							// 买实付金额 = roundIt((买金额 + 税前国债利息 + 买印花税 + 买经手费 + 买过户费 +买证管费 + 买其他费), 2)
//							hzJkQs.setBSfje(YssFun.roundIt(hzJkQs.getBJe()
//									+ feeAttribute.getFBeforeGzlx()
							// 买实付金额 = roundIt((全价金额 + 买印花税 + 买经手费 + 买过户费 +买证管费 + 买其他费), 2)
							hzJkQs.setBSfje(YssFun.roundIt(jsCjje
									+ hzJkQs.getBYhs() + hzJkQs.getBJsf()
									+ hzJkQs.getBGhf() + hzJkQs.getBZgf()
									+ hzJkQs.getBQtf(), 2));
						}
						if (feeAttribute.getBs().equals("S")) {
							hzJkQs.setSJe(feeAttribute.getCjje());
//							// 卖实收金额 = roundIt((卖金额 + 税前国债利息 - 卖印花税 - 卖经手费 - 卖过户费 - 卖证管费 - 卖其他费), 2)
//							hzJkQs.setSSsje(YssFun.roundIt(hzJkQs.getSJe()
//									+ feeAttribute.getFBeforeGzlx()
							// 卖实收金额 = roundIt((全价金额 - 买印花税 - 买经手费 - 买过户费 -买证管费 - 买其他费), 2)
							hzJkQs.setSSsje(YssFun.roundIt(jsCjje
									- hzJkQs.getSYhs() - hzJkQs.getSJsf()
									- hzJkQs.getSGhf() - hzJkQs.getSZgf()
									- hzJkQs.getSQtf(), 2));
						}
					}

					if (exchangeBond.getBondTradeType().equals("01")) { // 全价交易
						if (feeAttribute.getBs().equals("B")) {
							hzJkQs.setBJe(feeAttribute.getCjje());
							// 买实付金额 = roundIt((买金额 + 买印花税 + 买经手费 + 买过户费 +买证管费 +
							// 买其他费), 2)
							hzJkQs.setBSfje(YssFun.roundIt(hzJkQs.getBJe()
									+ hzJkQs.getBYhs() + hzJkQs.getBJsf()
									+ hzJkQs.getBGhf() + hzJkQs.getBZgf()
									+ hzJkQs.getBQtf(), 2));
						}
						if (feeAttribute.getBs().equals("S")) {
							hzJkQs.setSJe(feeAttribute.getCjje());
							// 卖实收金额 = roundIt((卖金额 - 卖印花税 - 卖经手费 - 卖过户费 - 卖证管费
							// - 卖其他费), 2)
							hzJkQs.setSSsje(YssFun.roundIt(hzJkQs.getSJe()
									- hzJkQs.getSYhs() - hzJkQs.getSJsf()
									- hzJkQs.getSGhf() - hzJkQs.getSZgf()
									- hzJkQs.getSQtf(), 2));
						}
					}
				} else {
					//B股业务：根据费用承担方向设置重新计算佣金，如果经手费、证管费由券商承担，则佣金还需扣除经手费、证管费  panjunfang add 20100517
					updateSZYj(hzJkQs);
					
					if (feeAttribute.getBs().equals("B")) {
						// 买业务时，即其fbs字段为‘B’时，其金额计入买类型
						hzJkQs.setBJe(feeAttribute.getCjje());

						if (feeAttribute.getSecuritySign().equals("GP")
								&& feeAttribute.getBusinessSign().equals(
										"SH_ETF")
								|| (feeAttribute.getSecuritySign().equals("JJ") && feeAttribute
										.getBusinessSign().equals("SG_ETF"))) {
							hzJkQs.setBSfje(0);
						} else {
							// 买实付金额 = roundIt((买金额 + 买印花税 + 买经手费 + 买过户费 +买证管费 +
							// 买其他费), 2)
							hzJkQs.setBSfje(YssFun.roundIt(hzJkQs.getBJe()
									+ hzJkQs.getBYhs() + hzJkQs.getBJsf()
									+ hzJkQs.getBGhf() + hzJkQs.getBZgf()
									+ hzJkQs.getBQtf(), 2));
						}
					}
					if (feeAttribute.getBs().equals("S")) {
						// 卖业务时，即其fbs字段为‘S’时，其金额计入卖类型
						hzJkQs.setSJe(feeAttribute.getCjje());

						if ((feeAttribute.getSecuritySign().equals("GP") && feeAttribute
								.getBusinessSign().equals("SG_ETF"))
								|| (feeAttribute.getSecuritySign().equals("JJ") && feeAttribute
										.getBusinessSign().equals("SH_ETF"))) {
							hzJkQs.setSSsje(0);
						} else {
							// 卖实收金额 = roundIt((卖金额 - 卖印花税 - 卖经手费 - 卖过户费 - 卖证管费
							// - 卖其他费), 2)
							hzJkQs.setSSsje(YssFun.roundIt(hzJkQs.getSJe()
									- hzJkQs.getSYhs() - hzJkQs.getSJsf()
									- hzJkQs.getSGhf() - hzJkQs.getSZgf()
									- hzJkQs.getSQtf(), 2));
						}
					}
				}

				if (!(feeAttribute.getSecuritySign().equals("JJ") && (feeAttribute
						.getBusinessSign().equals("SH_ETF") || feeAttribute
						.getBusinessSign().equals("SG_ETF")))) {
					// 若设置了参数‘实收实付金额包含佣金’则需在计算时买加佣金、卖减佣金
					dealBSfjeAndSSsje(feeAttribute, hzJkQs);
				}
			}

			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
			// ----处理原上海过户库中的股票/债券/基金的交易数据----//
		} catch (Exception e) {
			throw new YssException("处理交易接口汇总库数据到交易接口清算库出错", e);
		} finally {
			dbl.endTransFinal(con, bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pstmt);
		}
	}

	/**
	 * B股业务：更新深圳回报库佣金
	 * 如果深圳经手费和证管费由券商承担，则佣金需扣除经手费和证管费
	 * @throws YssException
	 */
	private void updateSZYj(HzJkQsBean hzJkQs) throws YssException{
		StringBuffer buf = null;
		ResultSet rs = null;
        double FJsfZgfRate = 0;//经手费与证管费费率和
        double FZgfRate = 0;//证管费率
        //--- add by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
        double FJsfRate = 0;//经手费率
        double dJsf = 0;//经手费
        //--- add by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
        double dJsfZgf = 0;//经手费于证管费和
        double dZgf = 0;//证管费
        double dYj = 0;//佣金
		try{
			FeeAttributeBean feeAttribute = hzJkQs.getFeeAttribute();
			
			if(!feeAttribute.getSecuritySign().equals("B_GP") || feeAttribute.getShsz().equals("CG")){//暂考虑深圳B股
				return;
			}
			CNInterfaceParamAdmin cnInterfaceParam = new CNInterfaceParamAdmin();
			cnInterfaceParam.setYssPub(pub);
			String brokerCode = (String) hmBrokerCode.get(feeAttribute.getGsdm()); //根据席位获取券商代码
            ArrayList alBears = cnInterfaceParam.getBrokerBearCost(pub.getAssetGroupCode(), feeAttribute.getPortCode(), brokerCode, feeAttribute.getGsdm());
            if (!alBears.contains("01") && !alBears.contains("05")) {//如果经手费和证管费都不是券商承担，则不用从佣金中扣除经手费和证管费
            	return;
            }
            
            //获取经手费和证管费费率和、证管费率
            if(feeAttribute.getJyfs().equals("DZ")){//大宗交易费率
            	FZgfRate = YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP ZGF")).getBigExchange(), 0.01);
            	//add by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B
            	FJsfRate = YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JSF")).getBigExchange(), 0.01);
                FJsfZgfRate = YssD.add(YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JSF"))
                        .getBigExchange(), 0.01),FZgfRate);
            }else{
            	FZgfRate = YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP ZGF")).getExchangeRate(), 0.01);
            	//add by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B
            	FJsfRate = YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JSF")).getExchangeRate(), 0.01);
                FJsfZgfRate = YssD.add(YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JSF"))
                        .getExchangeRate(), 0.01),FZgfRate);
            }
            
            //获取计算深圳佣金过程中费用小数点保留位数
            BrokerRateBean brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + feeAttribute.getPortCode() + " " +
                brokerCode + " 1 " + feeAttribute.getGsdm() + " EQ B"); //获取深圳股票对应组合代码和券商代码的佣金利率设置实例
            if (brokerRate == null) {
                throw new YssException("请在券商佣金利率设置中设置深圳B股佣金费率数据！");
            }
            
			buf = new StringBuffer(200);
			buf.append("select * from ").append(pub.yssGetTableName("Tb_HzJkMx")).append(" where FInDate = ").append(dbl.sqlDate(date))
				.append(" and FDate = ").append(dbl.sqlDate(feeAttribute.getDate())).append(" and FZqdm = ").append(dbl.sqlString(feeAttribute.getZqdm()))
				.append(" and fzqbz in ('B_GP') and FSzsh = 'CS' and FPortCode = ").append(dbl.sqlString(feeAttribute.getPortCode()))
				.append(" and FJyxwh = ").append(dbl.sqlString(feeAttribute.getGsdm())).append(" and FGddm = ").append(dbl.sqlString(feeAttribute.getGddm()))
				.append(" and Fywbz = ").append(dbl.sqlString(feeAttribute.getBusinessSign()))
				.append(" and FJYFS = ").append(dbl.sqlString(feeAttribute.getJyfs()));
			rs = dbl.openResultSet(buf.toString());
			buf.delete(0, buf.length());
			while(rs.next()){
				//--- add by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
				dJsfZgf += YssD.add(YssD.round(YssD.mul(rs.getDouble("FCjje"), FJsfRate), brokerRate.getYjCoursePreci()), 
						            YssD.round(YssD.mul(rs.getDouble("FCjje"), FZgfRate), brokerRate.getYjCoursePreci()));
				//--- add by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
				
				//delete by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B
				//dJsfZgf += YssD.round(YssD.mul(rs.getDouble("FCjje"), FJsfZgfRate), brokerRate.getYjCoursePreci());//按成交明细汇总的经手费和证管费和
			}
			dZgf = YssD.round(YssD.mul(feeAttribute.getCjje(), FZgfRate),brokerRate.getYjCoursePreci());//按成交汇总计算的证管费
			//add by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B
			dJsf = YssD.round(YssD.mul(feeAttribute.getCjje(), FJsfRate),brokerRate.getYjCoursePreci());//按成交汇总计算的经手费
			
			if(feeAttribute.getBs().equals("B")){	//买						
				// 经手费，证管费都由券商承担,	佣金=round(汇总佣金金额 – 汇总按逐笔计算的经手费与证管费之和,2)
				if (alBears.contains("01") && alBears.contains("05")) {
					dYj = YssD.round(YssD.sub(hzJkQs.getBYj(), dJsfZgf), 2);
				}
				//证管费由券商承担，经手费不由券商承担， 	佣金 = round (汇总佣金金额 – 计算佣金专用证管费,2)
				if (alBears.contains("05") && !alBears.contains("01")) { 
					dYj = YssD.round(YssD.sub(hzJkQs.getBYj(), dZgf), 2);
				}
				//经手费由券商承担，证管费不由券商承担，	佣金 = round (汇总佣金金额 – 汇总按逐笔计算的经手费与证管费之和 + 计算佣金专用证管费,2)。
				if (alBears.contains("01") && !alBears.contains("05")) { 
					//edit by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B
					dYj = YssD.round(YssD.sub(hzJkQs.getBYj(), dJsf), 2);
				}
				
				if(dYj < brokerRate.getStartMoney()) {//如果佣金低于深圳股票佣金的起点金额，则调整佣金为起点金额
					dYj = brokerRate.getStartMoney();
				}
				hzJkQs.setBYj(dYj);
			}else{	//卖			
				// 经手费，证管费都由券商承担,	佣金=round(汇总佣金金额 – 汇总按逐笔计算的经手费与证管费之和,2)
				if (alBears.contains("01") && alBears.contains("05")) {
					dYj = YssD.round(YssD.sub(hzJkQs.getSYj(), dJsfZgf), 2);
				}
				//证管费由券商承担，经手费不由券商承担， 	佣金 = round (汇总佣金金额 – 计算佣金专用证管费,2)
				if (alBears.contains("05") && !alBears.contains("01")) { 
					dYj = YssD.round(YssD.sub(hzJkQs.getSYj(), dZgf), 2);
				}
				//经手费由券商承担，证管费不由券商承担，	佣金 = round (汇总佣金金额 – 汇总按逐笔计算的经手费与证管费之和 + 计算佣金专用证管费,2)。
				if (alBears.contains("01") && !alBears.contains("05")) { 
					//edit by songjie 2013.05.08 BUG 7639 QDV4嘉实2013年04月26日01_B  
					dYj = YssD.round(YssD.sub(hzJkQs.getSYj(), dJsf), 2);
				}
				
				if(dYj < brokerRate.getStartMoney()) {//如果佣金低于深圳股票佣金的起点金额，则调整佣金为起点金额
					dYj = brokerRate.getStartMoney();
				}
				hzJkQs.setSYj(dYj);
			}

		}catch(Exception e){
			throw new YssException("计算深圳佣金出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 按成交明细计算的sql语句
	 * 
	 * @param str
	 *            String
	 * @param strShSz
	 *            String
	 * @param setPortCodes
	 *            String
	 * @return String
	 */
	private String getCjmxSql(String strShSz, String setPortCodes) {
		String cjmxStrSql = " select sum(FYj) as FYj, sum(FJsf) as FJsf, sum(FZgf) as FZgf,"
				+ " sum(FGhf) as FGhf, sum(FYhs) as FYhs, sum(FQtf) as FQtf, sum(FFxj) as FFxj, "
				//edit by songjie 2012.07.10 BUG 4967 QDV4嘉实2012年07月06日01_B 修改 成交价格算法
				+ " sum(FCjje) as FCjje, sum(FCjsl) as FCjsl, sum(FCjje)/decode(sum(FCjsl),0,1,sum(FCjsl)) as FCjjg, sum(FGzlx) as FGzlx, "
				+ " sum(FSqGzlx) as FSqGzlx, sum(Fhggain) as Fhggain, FDate, FInDate, FZqdm, FBs, FSzsh, FGddm, "
				+ "FJyxwh, FZqbz, Fywbz, Zqdm, FJYFS, FTZBZ, FPortCode from "
				+ pub.yssGetTableName("Tb_HzJkMx")
				+ " where FInDate = "
				+ dbl.sqlDate(date)
				+ " and fzqbz in('GP','ZQ','JJ','B_GP') and FSzsh = " //增加B股 panjunfang modify 20100421
				+ dbl.sqlString(strShSz)
				+ " and Fywbz <> 'KZZGP' and FPortCode in("
				+ operSql.sqlCodes(setPortCodes)
				+ ") group by FDate, FInDate, FZqdm, FSzsh, FGddm, FJyxwh, FZqbz, Fywbz,FBs, "
				+ "Zqdm, FJYFS, FTZBZ, FPortCode order by Fzqdm, Zqdm, Fywbz, FBs";

		return cjmxStrSql;
	}

	/**
	 * 按申请编号计算的sql语句
	 * 
	 * @param str
	 *            String
	 * @param strShSz
	 *            String
	 * @param setPortCodes
	 *            String
	 * @return String
	 */
	private String getSqbhSql(String strShSz, String setPortCodes) {
		String sqbhStrSql = " select FJsf, FZgf, FGhf, FYhs, FQtf, FFxj, FYj, FGzlx, FSqGzlx, "
				+ " FCjje, FCjsl, FCjje/decode(FCjsl,0,1,FCjsl) as FCjjg, FHgGain, FDate, FInDate, FZqdm, FSzsh, FGddm, "
				+ " FJyxwh, FBs, FZqbz, Fywbz, Zqdm, FJYFS, FTZBZ, FPortCode from "
				+ pub.yssGetTableName("Tb_HzJkHz")
				+ " where  FInDate = "
				+ dbl.sqlDate(date)
				+ " and FPortCode in("
				+ operSql.sqlCodes(setPortCodes)
				+ ")  and fzqbz in('GP','ZQ','JJ','B_GP') and FSzSh = "//增加B股 panjunfang modify 20100421
				+ dbl.sqlString(strShSz)
				+ " and Fywbz <> 'KZZGP' order by fzqdm,zqdm,fywbz,fbs";

		return sqbhStrSql;
	}

	/**
	 * 按成交汇总计算的sql语句
	 * 
	 * @param str
	 *            String
	 * @param strShSz
	 *            String
	 * @param setPortCodes
	 *            String
	 * @return String
	 */
	private String getCjhzSql(String strShSz, String setPortCodes) {
		String cjhzStrSql = " select sum(FYj) as FYj, sum(FJsf) as FJsf, sum(FZgf) as FZgf,"
				+ " sum(FGhf) as FGhf, sum(FYhs) as FYhs, sum(FQtf) as FQtf, sum(FFxj) as FFxj, "
				+ "sum(FCjje) as FCjje, sum(FCjsl) as FCjsl, sum(FCjje)/sum(decode(FCjsl,0,1,FCjsl)) as FCjjg, "
				+ // edit by
				// xuxuming,20091110.sum(FCjje)/sum(FCjsl)得平均价，以前是sum(FCjje/FCjsl).
				"sum(FGzlx) as FGzlx,  sum(FSqGzlx) as FSqGzlx, sum(Fhggain) as Fhggain, FDate, FInDate, FZqdm, FSzsh, "
				+ "FGddm, FJyxwh, FBs, FZqbz, Fywbz, Zqdm, FJYFS, FTZBZ, FPortCode from "
				+ pub.yssGetTableName("Tb_HzJkHz")
				+ " where FInDate = "
				+ dbl.sqlDate(date)
				+ " and FPortCode in("
				+ operSql.sqlCodes(setPortCodes)
				+ ") and FSzSh = "
				+ dbl.sqlString(strShSz)
				+ " and fzqbz in('GP','ZQ','JJ','B_GP')  and Fywbz <> 'KZZGP' "//增加B股 panjunfang modify 20100421
				+ " group by Fhggain, FDate, FInDate, FZqdm, FSzsh, FGddm, "
				+ "FJyxwh, FBs, FZqbz, Fywbz, Zqdm, FJYFS, FTZBZ, FPortCode order by fzqdm,zqdm,fywbz,fbs";
		return cjhzStrSql;
	}

	/**
	 * 处理两地新股新债、权益、回购及债转股等业务
	 * 
	 * @param portCodes
	 *            String
	 * @param tradeDate
	 *            Date
	 * @param date
	 *            Date
	 * @throws YssException
	 */
	private void dealOtherInfo() throws YssException {
		String strSql = ""; // 声明sql语句
		ResultSet rs = null; // 声明结果集
		HzJkQsBean hzJkQs = null; // 声明HzJkQsBean的实体类
		String szsh = null; // 声明交易所代码
		String zqbz = null; // 声明证券标志
		String ywbz = null; // 声明业务标志
		double cjje = 0; // 声明成交金额
		double cjsl = 0; // 声明成交数量
		double hgGain = 0; // 声明回购收益
		String portcode = null; // 声明组合代码
		FeeAttributeBean feeAttribute = null; // 声明FeeAttributeBean
		String keyStr = null;
		try {
			CtlStock stock = new CtlStock();
			stock.setYssPub(pub);

			SHGHBean shgh = new SHGHBean();
			shgh.setYssPub(pub);

			SZHBBean szhb = new SZHBBean();
			szhb.setYssPub(pub);

			// ----处理两地新股新债、权益、回购及债转股等业务----//
			strSql = " select sum(FCjje) as FCjje, sum(FCjsl) as FCjsl, sum(FCjje / decode(FCjsl,0,1,FCjsl)) as FCjjg, "
					+ "sum(FYhs) as fyhs, sum(FJsf) as FJsf, sum(FGhf) as FGhf,sum(FZgf) as FZgf, "
					+ " sum(FYj) as FYj , sum(FFxj) as FFxj,sum(FQtf) as FQtf,sum(FGzlx) as FGzlx,"
					+ "sum(FHgGain) as FHgGain, FDate,FInDate,FJyfs,Zqdm, "
					+ " FZqdm, FSzsh, FJyxwh, FBS, FZqbz, FYwbz, FGddm, FTzbz, FPortCode from "
					+ pub.yssGetTableName("Tb_HzJkHz")
					+ " where FPortCode = " + operSql.sqlCodes(portCodes)
					+ " and FZqbz in('QY', 'HG', 'GP', 'ZQ', 'XG', 'XZ', 'QZ') and FInDate = "
					+ dbl.sqlDate(date)
					+ " group by FDate,FInDate,FJyfs,Zqdm,FZqdm, FSzsh, FJyxwh, FBS, FZqbz, FYwbz, FGddm, FTzbz, FPortCode ";
			rs = dbl.openResultSet(strSql); // 在交易接口汇总表中根据已选组合代码查询两地新股新债、权益、回购及债转股的数据
			while (rs.next()) {

				szsh = rs.getString("fSZSH"); // 交易所代码
				zqbz = rs.getString("fZqbz"); // 证券标志
				ywbz = rs.getString("fYwbz"); // 业务标志
				cjje = rs.getDouble("FCjje"); // 成交金额
				cjsl = rs.getDouble("FCjsl"); // 成交数量
				portcode = rs.getString("FPortCode"); // 组合代码
				hgGain = rs.getDouble("FHgGain"); // 回购收益

				if (zqbz.equals("GP") || zqbz.equals("ZQ")) {
					if (!ywbz.equals("KZZGP")) {
						continue;
					}
				}

				feeAttribute = new FeeAttributeBean();

				feeAttribute.setFJsf(rs.getDouble("FJsf"));
				feeAttribute.setFZgf(rs.getDouble("FZgf"));
				feeAttribute.setFGhf(rs.getDouble("FGhf"));
				feeAttribute.setFfxj(rs.getDouble("FFxj"));
				feeAttribute.setFYhs(rs.getDouble("FYhs"));
				feeAttribute.setFqtf(rs.getDouble("FQtf"));
				feeAttribute.setFYj(rs.getDouble("FYj"));
				feeAttribute.setFGzlx(rs.getDouble("FGzlx"));
				feeAttribute.setFhggain(rs.getDouble("FHgGain"));
				//begin zhouxiang MS01480    数据接口再导入上海过户库数据信息时报错   
				feeAttribute.setSeatCode(rs.getString("FJYXWH"));
				//end-- zhouxiang MS01480    数据接口再导入上海过户库数据信息时报错   

				setFeeAttribute(feeAttribute, rs); // 给feeAttribute赋值

				keyStr = feeAttribute.getDate().toString() + " "
						+ feeAttribute.getInDate().toString() + " "
						+ feeAttribute.getZqdm() + " " 
						+ feeAttribute.getGsdm() + " " 
						+ feeAttribute.getJyfs() + " "
						+ feeAttribute.getBusinessSign() + " "
						+ feeAttribute.getPortCode();

				// 若已经有相关的汇总接口清算的实体类
				if (hmHzJkQs.containsKey(keyStr)) {
					hzJkQs = (HzJkQsBean) hmHzJkQs.get(keyStr);// 就取出汇总接口清算实体类
				} else {
					hzJkQs = new HzJkQsBean();// 若没有相关实体类，就新建一个实体类
				}

				if (zqbz.equals("XG") || zqbz.equals("XZ")) {
					// 申购，申购金额计入买金额
					if (ywbz.equals("SG") 
						|| ywbz.equals("SG_FLKZZ")
						|| ywbz.equals("SG_KZZ") 
						|| ywbz.equals("SG_QYZQ")
						|| ywbz.equals("SG_GZ")
						|| ywbz.equals("SG_DFZFZ")) {
						// 新股,新债的申购没有成交金额，成交数量，成交价格，只有实收实付金额
						hzJkQs.setBJe(0); // 新股申购的成交金额为零
						hzJkQs.setBSl(0); // 新股申购的成交数量为零
					}

					// 中签，中签金额与数量均计入买金额与买数量
					if (ywbz.equals("ZQ") 
						|| ywbz.equals("ZQ_FLKZZ")
						|| ywbz.equals("ZQ_QYZQ") 
						|| ywbz.equals("ZQ_KZZ")
						|| ywbz.equals("ZQ_ZS") 
						|| ywbz.equals("ZQ_SZ_ZS")
						|| ywbz.equals("ZQ_PSZF")
						|| ywbz.equals("KZZXZ") 
						|| ywbz.equals("FLKZZXZ")
						|| ywbz.equals("QYZQXZ")) {
						hzJkQs.setBJe(cjje);
						hzJkQs.setBSl(cjsl);
					}

					// 未中返款，返款金额计入卖金额
					if (ywbz.equals("FK") 
						|| ywbz.equals("FK_FLKZZ")
						|| ywbz.equals("FK_KZZ")) {
						// 新股返款的成交数量和成交金额都为零
						hzJkQs.setSJe(0);
						hzJkQs.setSSl(0);
					}

					// 新股或新债转流通，金额与数量均计入买类型
					if (ywbz.equals("XGLT") 
						|| ywbz.equals("XGLT_ZS")
						|| ywbz.equals("XGLT_ZB") 
						|| ywbz.equals("XZLT")) {
						hzJkQs.setBJe(cjje);
						hzJkQs.setBSl(cjsl);
					}
				}

				if (zqbz.equals("QY")) {
					// 分红派息、派息到账、现金对价、对价到账，原成交金额为卖出金额；
					if (ywbz.equals("PX_GP") 
						|| ywbz.equals("DZ_PX")
						|| ywbz.equals("XJDJ") 
						|| ywbz.equals("DZ_XJDJ")) {
						hzJkQs.setSJe(cjje);
					}
					// 而送股、权证行权或股份对价时，原成交数量为买入数量；
					if (ywbz.equals("SG") 
                        || ywbz.equals("GFDJ_ZS")
						|| ywbz.equals("GFDJ")) {
						hzJkQs.setBJe(cjje);
					}
				}

				if (zqbz.equals("QZ")) { // 权证
					// 认沽权证行权，认购权证行权的原成交数量为买入数量
					if (ywbz.equals("XQ_RGQZ") || ywbz.equals("XQ_RZQZ")) {
						hzJkQs.setBJe(cjje);
						hzJkQs.setSJe(0);
					}
				}

				if (zqbz.equals("HG")) { // 回购
					boolean judge1 = judgeIfPortCodeIn(portCodes, portcode,cjmxSzJsfInfo);
					boolean judge2 = judgeIfPortCodeIn(portCodes, portcode,cjmxShJsfInfo);
					boolean judge3 = judgeIfPortCodeIn(portCodes, portcode,cjmxSzYjInfo);
					boolean judge4 = judgeIfPortCodeIn(portCodes, portcode,cjmxShYjInfo);
					boolean judge5 = judgeIfPortCodeIn(portCodes, portcode,cjmxSzFxjInfo);
					boolean judge6 = judgeIfPortCodeIn(portCodes, portcode,cjmxShFxjInfo);
					feeAttribute.setCjsl(feeAttribute.getCjsl() * 10);// add by
					// xuxuming,20091112.回购数量应为（gh.cjsl
					// * 10）
					// 若该佣金、经手费及风险金的计算方式（取参数设置）为按明细计算，佣金可直接取汇总表中的字段
					if ((feeAttribute.getShsz().equals("CG") 
						&& judge2 == true
						&& judge4 == true 
						&& judge6 == true)
						|| (feeAttribute.getShsz().equals("CS")
						&& judge1 == true 
						&& judge3 == true 
						&& judge5 == true)) {
						hzJkQs.setHgGain(hgGain);
					} else { // 否则再次计算
						if (szsh.equals("CG")) { // 表示为上交所的数据
							feeAttribute.setComeFromQS(true);
							shgh.calculateFee(feeAttribute);
						}

						if (szsh.equals("CS")) { // 表示为深交所的数据
							feeAttribute.setComeFromQS(true);
							szhb.calculateFee(feeAttribute);
						}
					}

					// 企业债融券回购 买断式融券回购 融券回购 融券回购到期
					// 若为融券回购，金额、数量及费用均计入卖类型
					if (ywbz.equals("MRHG_QYZQ") 
						|| ywbz.equals("MDMRHG")
						|| ywbz.equals("MRHG") 
						|| ywbz.equals("MRHGDQ")) {
						if (szsh.equals("CG")) {
							// hzJkQs.setSJe(rs.getDouble("FCjje"));
							// hzJkQs.setSSl(rs.getDouble("FCjsl"));
							// hzJkQs.setSFxj(rs.getDouble("FFxj"));
							// hzJkQs.setSJsf(rs.getDouble("FJsf"));
							// hzJkQs.setSYj(rs.getDouble("FYj"));
							// =====edit by
							// xuxuming,20091112.这里要和需求上不符，改正为：=============
							// ======= 若为融资回购，金额、数量及费用均计入卖类型。
							// ==========若为融券回购，金额、数量及费用均计入买类型。
							hzJkQs.setBJe(rs.getDouble("FCjje"));
							hzJkQs.setBSl(rs.getDouble("FCjsl"));
							hzJkQs.setBFxj(rs.getDouble("FFxj"));
							hzJkQs.setBJsf(rs.getDouble("FJsf"));
							hzJkQs.setBYj(rs.getDouble("FYj"));
						}
						if (szsh.equals("CS")) {
							hzJkQs.setBJe(rs.getDouble("FCjje"));
							hzJkQs.setBSl(rs.getDouble("FCjsl"));
							hzJkQs.setBFxj(rs.getDouble("FFxj"));
							hzJkQs.setBJsf(rs.getDouble("FJsf"));
							hzJkQs.setBYj(rs.getDouble("FYj"));
						}
					}

					// 企业债融资回购 买断式融资回购 融资回购 融资回购到期
					// 若为融资回购，金额、数量及费用均计入买类型
					if (ywbz.equals("MCHG_QYZQ") 
						|| ywbz.equals("MDMCHG")
						|| ywbz.equals("MCHG") 
						|| ywbz.equals("MCHGDQ")) {
						if (szsh.equals("CG")) {
							// hzJkQs.setBJe(rs.getDouble("FCjje"));
							// hzJkQs.setBSl(rs.getDouble("FCjsl"));
							// hzJkQs.setBFxj(rs.getDouble("FFxj"));
							// hzJkQs.setBJsf(rs.getDouble("FJsf"));
							// hzJkQs.setBYj(rs.getDouble("FYj"));
							// =====edit by
							// xuxuming,20091112.这里要和需求上不符，改正为：=============
							// ======= 若为融资回购，金额、数量及费用均计入卖类型。
							// ==========若为融券回购，金额、数量及费用均计入买类型。
							hzJkQs.setSJe(rs.getDouble("FCjje"));
							hzJkQs.setSSl(rs.getDouble("FCjsl"));
							hzJkQs.setSFxj(rs.getDouble("FFxj"));
							hzJkQs.setSJsf(rs.getDouble("FJsf"));
							hzJkQs.setSYj(rs.getDouble("FYj"));
						}
						if (szsh.equals("CS")) {
							hzJkQs.setSJe(rs.getDouble("FCjje"));
							hzJkQs.setSSl(rs.getDouble("FCjsl"));
							hzJkQs.setSFxj(rs.getDouble("FFxj"));
							hzJkQs.setSJsf(rs.getDouble("FJsf"));
							hzJkQs.setSYj(rs.getDouble("FYj"));
						}
					}
					setHzJkQs(feeAttribute, hzJkQs); // 给hzJkQs赋值
				}

				if (!zqbz.equals("HG")) {
					// 若买卖标志为买标志，则佣金，经手费，结算费，印花税，证管费都要存入买类型的字段中
					if (feeAttribute.getBs().equals("B")) {
						hzJkQs.setBFxj(0);
						hzJkQs.setBGhf(rs.getDouble("FGhf"));
						hzJkQs.setBGzlx(rs.getDouble("FGzlx"));
						hzJkQs.setBJsf(rs.getDouble("FJsf"));
						hzJkQs.setBQtf(rs.getDouble("FQtf"));
						hzJkQs.setBYhs(rs.getDouble("FYhs"));
						hzJkQs.setBYj(rs.getDouble("FYj"));
						hzJkQs.setBZgf(rs.getDouble("FZgf"));
					}

					// 若买卖标志为卖标志，则佣金，经手费，结算费，印花税，证管费都要存入卖类型的字段中
					if (feeAttribute.getBs().equals("S")) {
						hzJkQs.setSFxj(0);
						hzJkQs.setSGhf(rs.getDouble("FGhf"));
						hzJkQs.setSGzlx(rs.getDouble("FGzlx"));
						hzJkQs.setSJsf(rs.getDouble("FJsf"));
						hzJkQs.setSQtf(rs.getDouble("FQtf"));
						hzJkQs.setSYhs(rs.getDouble("FYhs"));
						hzJkQs.setSYj(rs.getDouble("FYj"));
						hzJkQs.setSZgf(rs.getDouble("FZgf"));
					}

					setHzJkQs(feeAttribute, hzJkQs); // 给hzJkQs赋值
				}

				if (feeAttribute.getBs().equals("B")) {
					// 买实付金额 = roundIt((买金额 + 买印花税 + 买经手费 + 买过户费 +买证管费 + 买其他费), 2);
					hzJkQs.setBSfje(YssFun.roundIt(hzJkQs.getBJe()
							+ hzJkQs.getBYhs() + hzJkQs.getBJsf()
							+ hzJkQs.getBGhf() + hzJkQs.getBZgf()
							+ hzJkQs.getBQtf(), 2));

					if (zqbz.equals("XG") || zqbz.equals("XZ")) {
						// 申购，申购金额计入买金额
						if (ywbz.equals("SG") 
							|| ywbz.equals("SG_FLKZZ")
							|| ywbz.equals("SG_KZZ")
							|| ywbz.equals("SG_QYZQ")
							|| ywbz.equals("SG_GZ")
							|| ywbz.equals("SG_DFZFZ")) {
							// 新股,新债的申购没有成交金额，成交数量，成交价格，只有实收实付金额
							hzJkQs.setBSfje(cjje);
						}
					}
				}

				if (feeAttribute.getBs().equals("S")) {
					// 卖实收金额 = roundIt((卖金额 - 卖印花税 - 卖经手费 - 卖过户费 - 卖证管费 - 卖其他费), 2)
					hzJkQs.setSSsje(YssFun.roundIt(hzJkQs.getSJe()
							- hzJkQs.getSYhs() - hzJkQs.getSJsf()
							- hzJkQs.getSGhf() - hzJkQs.getSZgf()
							- hzJkQs.getSQtf(), 2));

					if (zqbz.equals("XG") || zqbz.equals("XZ")) {
						if (ywbz.equals("FK") 
							|| ywbz.equals("FK_FLKZZ") 
							|| ywbz.equals("FK_KZZ")) {
							// 新股返款的成交数量和成交金额都为零
							hzJkQs.setSSsje(cjje);
						}
					}
				}
				
				//add by songjie 2010.03.04  王晓玲发现的BUG
				//若证券标志为新股或新债，业务标志为中签 ，则不产生实收实付金额
				//(除了业务标志为 ZQ_PSZF，ZQ_SZ_ZS，ZQ_SZ)
				if((zqbz.equals("XG") 
					|| zqbz.equals("XZ")) 
					&& (ywbz.equals("ZQ") 
					|| ywbz.equals("ZQ_FLKZZ")
					//delete by songjie 2012.04.27 BUG 4431 QDV4赢时胜(测试)2012年04月27日02_B
//				    || ywbz.equals("ZQ_QYZQ") 
				    || ywbz.equals("ZQ_KZZ")
				//edit by songjie 2010.03.18 MS00915 QDII4.1赢时胜上海2010年03月18日01_B
//				|| ywbz.equals("ZQ_ZS") || ywbz.equals("ZQ_SZ_ZS")
				    || ywbz.equals("KZZXZ") 
				    || ywbz.equals("FLKZZXZ")
				    || ywbz.equals("QYZQXZ"))){
					hzJkQs.setSSsje(0);
					hzJkQs.setBSfje(0);
				}
				
				//---add by songjie 2012.04.27 BUG 4431 QDV4赢时胜(测试)2012年04月27日02_B start---//
				if(ywbz.equals("ZQ_QYZQ")){
					hzJkQs.setBSfje(cjje);
				}
				//---add by songjie 2012.04.27 BUG 4431 QDV4赢时胜(测试)2012年04月27日02_B end---//
				
				// 回购业务时，若设置了参数‘实收实付金额包含佣金’则需在计算时买加佣金、卖减佣金
				if (zqbz.equals("HG")) {
					dealBSfjeAndSSsje(feeAttribute, hzJkQs);
				}
				//add by zhouwei 20120516 story 2538 上海过户库配股交款成交数量*配股权益比例
				if(feeAttribute.getBs().equals("B")){
					if(feeAttribute.getShsz().equals("CG") && feeAttribute.getBusinessSign().equals("PGJK")){
						hzJkQs.setBSl(getTradeAmountOfPGJK(portcode, rs.getString("FZQDM"), cjsl));
					}
				}
				hmHzJkQs.put(keyStr, hzJkQs);
			}
			// ----处理两地新股新债、权益、回购及债转股等业务----//
		} catch (Exception e) {
			throw new YssException("处理两地新股新债、权益、回购及债转股等业务出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/** story 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A add by zhouwei 20120516 根据权益比例获取配股数量
	*/
	private double getTradeAmountOfPGJK(String sportCode,String securityCode,double cjsl) throws YssException{
		double tradeAmount=0;
		ResultSet rs=null;
		String sql="";
		try{
			CtlPubPara pubPara = new CtlPubPara(); //通用参数实例化
            pubPara.setYssPub(pub); //设置Pub
            String rightsRatioMethods = (String) pubPara.getRightsRatioMethods(sportCode); //获取通用参数值
            sql = " select * from " + pub.yssGetTableName("Tb_Data_RightsIssue") +
            	 " where FTSecurityCode = " + dbl.sqlString(securityCode) + " and FCheckState = 1 "
	            +" and FRecordDate<="+dbl.sqlDate(date)+" and FExpirationDate>="+dbl.sqlDate(date)
	            +" and FExpirationDate<FExRightDate order by FRecordDate";
            rs = dbl.openResultSet(sql); //在配股权益表中查询相关证券代码的数据
            if(rs.next()){
            	tradeAmount=this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"),
            			YssD.mul(cjsl,
	            			rightsRatioMethods.equalsIgnoreCase("PreTaxRatio")?
	                        rs.getDouble("FPreTaxRatio"):rs.getDouble("FAfterTaxRatio"))
                        );
                
            }
		}catch (Exception e) {
			throw new YssException("获取配股缴款的权益信息出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return tradeAmount;
	}
	/**
	 * 新股返款的数据进行调整，因两地的返款数据均是全额返还的， 需减去当天中签的金额
	 * 
	 * @throws YssException
	 */
	private void dealXGFKInfo() throws YssException {
		String strSql = null; // 用于储存sql语句
		ResultSet rs = null; // 声明结果集
		String xgfkDm = ""; // 业务标志位新股返款的证券代码串
		HashMap hmXGInfo = new HashMap();
		HzJkQsBean hzJkQs = null; // 声明HzJkQsBean的实体类
		boolean haveZQInfo = false;// 用于判断在交易接口清算表中是否有业务标志为新股中签的数据
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement
		try {
			st = con.createStatement(); // 新建Statement

			strSql = " select * from " + pub.yssGetTableName("Tb_HzJkQs")
					+ " where FZqbz = 'XG' " + "and FYwbz = 'FK' and FInDate ="
					+ dbl.sqlDate(date);
			rs = dbl.openResultSet(strSql); // 在交易接口清算表中查询新股返款的数据
			while (rs.next()) {
				hzJkQs = new HzJkQsBean(); // 新建HzJkQsBean的实体类
				hzJkQs.setZqdm(rs.getString("FZqdm")); // 转换后的证券代码
				hzJkQs.setBJe(rs.getDouble("FBje")); // 买金额
				hzJkQs.setBSfje(rs.getDouble("FBsfje")); // 买实付金额
				hzJkQs.setSJe(rs.getDouble("FSje")); // 卖金额
				hzJkQs.setSSsje(rs.getDouble("FSssje")); // 卖实收金额
				hzJkQs.setDate(rs.getDate("FDate")); // 交易日期
				hzJkQs.setJyxwh(rs.getString("FJYXWH")); // 席位代码
				hzJkQs.setJyfs(rs.getString("FJYFS")); // 交易方式
				hzJkQs.setPortCode(rs.getString("FPortCode")); // 组合代码
				xgfkDm = xgfkDm+hzJkQs.getZqdm() + ","; // 储存转换后的证券代码
				hmXGInfo.put(YssFun.formatDate(hzJkQs.getDate(), "yyyy-MM-dd")
						+ " " + hzJkQs.getZqdm() + " FK "
						+ hzJkQs.getPortCode(), hzJkQs);
			}

			dbl.closeResultSetFinal(rs);
			rs = null;

			if (xgfkDm.length() > 1) {
				xgfkDm = xgfkDm.substring(0, xgfkDm.length() - 1); // 去掉字符串最后的逗号
			}

			strSql = " select * from " + pub.yssGetTableName("Tb_HzJkQs")
					+ " where FZqbz = 'XG' and FYwbz like '%ZQ%' and FZqdm in("
					+ operSql.sqlCodes(xgfkDm) + ") and FInDate = "
					+ dbl.sqlDate(date);
			rs = dbl.openResultSet(strSql); // 在交易接口清算表中查询新股中签的数据
			while (rs.next()) {
				haveZQInfo = true;// 表示有新股中签的数据
				hzJkQs = (HzJkQsBean) hmXGInfo.get(YssFun.formatDate(rs
						.getDate("FDate"), "yyyy-MM-dd")
						+ " "
						+ rs.getString("FZqdm")
						+ " FK "
						+ rs.getString("FPortCode")); // 找出相关的新股返款的数据

				if (hzJkQs != null) {
					if (hzJkQs.getSSsje() != 0 && rs.getDouble("FBje") != 0) {
//						// 新股返款的卖金额 = 新股返款的卖金额 - 中签的买金额
//						hzJkQs.setSJe(YssD.sub(hzJkQs.getSJe(), rs
//								.getDouble("FBje")));
						// 新股返款的卖实收金额 = 新股返款的卖实收金额 - 中签的买金额
						hzJkQs.setSSsje(YssD.sub(hzJkQs.getSSsje(), rs.getDouble("FBje")));
					}

					// 在HashMap中更新新股返款的数据
					hmXGInfo.put(YssFun.formatDate(hzJkQs.getDate(),"yyyy-MM-dd")
							+ " " + hzJkQs.getZqdm() + " FK " + hzJkQs.getPortCode(), hzJkQs);
				}
			}

			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			if (!haveZQInfo) {// 若没有新股中签的数据，则返回
				return;
			}

			Iterator iterator = hmXGInfo.values().iterator(); // 获取储存新股返款的HashMap的迭代器
			while (iterator.hasNext()) {
				hzJkQs = (HzJkQsBean) iterator.next(); // 获取新股返款数据的实例

				strSql = " update " + pub.yssGetTableName("Tb_HzJkQs")
						+ " set FSje = " + hzJkQs.getSJe() + ", FSSSJe = "
						+ hzJkQs.getSSsje() + " where FZqdm = "
						+ dbl.sqlString(hzJkQs.getZqdm())
						+ " and FZqbz = 'XG' and FYwbz = 'FK' and FInDate = "
						+ dbl.sqlDate(date) + " and FDate = "
						+ dbl.sqlDate(hzJkQs.getDate()) + " and FPortCode = "
						+ dbl.sqlString(hzJkQs.getPortCode())
						+ " and FJYXWH = " + dbl.sqlString(hzJkQs.getJyxwh())
						+ " and FJYFS = " + dbl.sqlString(hzJkQs.getJyfs()); // 更新交易接口清算表中新股返款的数据
				st.addBatch(strSql);
			}
			st.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交事务
		} catch (Exception e) {
			throw new YssException("处理交易接口清算表的新股返款数据时出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans);
		}
	}

	/**
	 * 将特殊数据的业务日期调整为下一工作日
	 * 
	 * @throws YssException
	 */
	private void dealToNextWorkDay() throws YssException {
		String strSql = ""; // 储存sql语句
		ResultSet rs = null; // 声明ResultSet
		HzJkQsBean hzJkQs = null; // 声明HzJkQsBean
		ArrayList alChangeDate = new ArrayList(); // 新建ArrayList
		java.util.Date tradeDate = null; // 声明交易日期
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement
		ReadTypeBean readType = null; // 声明ReadTypeBean
		String fPortCode = null; // 声明组合代码
		ArrayList alParam = null; // 声明交易接口参数设置中读书处理方式分页的选择型参数数据
		try {
			BaseOperDeal baseOperDeal = new BaseOperDeal(); // 新建BaseOperDeal
			baseOperDeal.setYssPub(pub);

			st = con.createStatement(); // 新建Statement

			// 在交易接口清算表查询系统读数日期为当天，业务标志为新股/新债流通，现金对价，派息到账，现金对价到账，权证行权
			strSql = " select * from "
					+ pub.yssGetTableName("Tb_HZJkQs")
					+ " where FYwbz in('XGLT', 'XGLT_ZS', 'XGLT_ZB', "
					+ " 'XZLT', 'XJDJ', 'DZ_PX', 'DZ_XJDJ', 'XQ_RGQZ', 'XQ_RZQZ') and FInDate = "
					+ dbl.sqlDate(date);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				hzJkQs = new HzJkQsBean(); // 新建HzJkQsBean实体类
				// 获取下一个交易日的日期
				//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
				readType = (ReadTypeBean)hmReadType.get
				(assetGroupCode + " " + rs.getString("FPortCode"));
				tradeDate = baseOperDeal.getWorkDay
				(readType.getHolidaysCode(), rs.getDate("FDate"), 1);
				//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
				hzJkQs.setDate(tradeDate); // 设置交易日期
				hzJkQs.setZqdm(rs.getString("FZqdm")); // 设置转换后的证券代码
				hzJkQs.setZqbz(rs.getString("FZqbz")); // 设置证券标志
				hzJkQs.setYwbz(rs.getString("FYwbz")); // 设置业务标志
				hzJkQs.setJyxwh(rs.getString("FJYXWH")); // 设置席位代码
				hzJkQs.setJyfs(rs.getString("FJYFS")); // 设置交易方式
				hzJkQs.setPortCode(rs.getString("FPortCode")); // 设置组合代码
				alChangeDate.add(hzJkQs);
			}

			dbl.closeResultSetFinal(rs);
			rs = null;

			// 查询交易接口清算表中权益的分红派息,送股，指数送股数据
			strSql = " select * from " + pub.yssGetTableName("Tb_HzJkQs")
					+ " where FInDate =" + dbl.sqlDate(date)
					//edit by yanghaiming 2010.02.28 MS00892 QDII4.1赢时胜上海2010年02月25日01_AB
					+ " and FZqbz = 'QY' and FYwbz in ('PX_GP','SG_ZS','SG')";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				fPortCode = rs.getString("FPortCode");
				readType = (ReadTypeBean) hmReadType.get(assetGroupCode + " "
						+ fPortCode);
				alParam = (ArrayList) readType.getParameters();
				//edit by yanghaiming 2010.02.28 MS00892 QDII4.1赢时胜上海2010年02月25日01_AB
				if (rs.getString("FYwbz").equals("PX_GP")) {
					if (!alParam.contains("02")) {
						hzJkQs = new HzJkQsBean();
						// 用于获取下一个交易日的日期
						//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
						tradeDate = baseOperDeal.getWorkDay
						(readType.getHolidaysCode(), rs.getDate("FDate"), 1);
						//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
						hzJkQs.setDate(tradeDate);
						hzJkQs.setZqdm(rs.getString("FZqdm"));
						hzJkQs.setZqbz(rs.getString("FZqbz"));
						hzJkQs.setYwbz(rs.getString("FYwbz"));
						hzJkQs.setJyxwh(rs.getString("FJYXWH"));
						hzJkQs.setJyfs(rs.getString("FJYFS"));
						hzJkQs.setPortCode(rs.getString("FPortCode"));
						alChangeDate.add(hzJkQs);
					}
				}
				//add by yanghaiming 2010.02.28 MS00892 QDII4.1赢时胜上海2010年02月25日01_AB
				else{
					hzJkQs = new HzJkQsBean();
					// 用于获取下一个交易日的日期
					//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
					tradeDate = baseOperDeal.getWorkDay
					(readType.getHolidaysCode(), rs.getDate("FDate"), 1);
					//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
					hzJkQs.setDate(tradeDate);
					hzJkQs.setZqdm(rs.getString("FZqdm"));
					hzJkQs.setZqbz(rs.getString("FZqbz"));
					hzJkQs.setYwbz(rs.getString("FYwbz"));
					hzJkQs.setJyxwh(rs.getString("FJYXWH"));
					hzJkQs.setJyfs(rs.getString("FJYFS"));
					hzJkQs.setPortCode(rs.getString("FPortCode"));
					alChangeDate.add(hzJkQs);
				}
			}

			dbl.closeResultSetFinal(rs);
			rs = null;

			// 查询交易接口清算表中的新股，新债业务的数据
			strSql = " select * from "
					+ pub.yssGetTableName("Tb_HzJkQs")
					+ " where FInDate = "
					+ dbl.sqlDate(date)
					+ " and FZqbz in('XG', 'XZ') and FYwbz in('SG', 'FK', 'SG_KZZ', 'FK_KZZ', "
					+ "'SG_FLKZZ', 'FK_FLKZZ', 'SG_QYZQ', 'SG_GZ', 'SG_DFZFZ') ";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				fPortCode = rs.getString("FPortCode");
				readType = (ReadTypeBean) hmReadType.get(assetGroupCode + " "
						+ fPortCode);
				alParam = (ArrayList) readType.getParameters();
				if (alParam.contains("03")) {
					hzJkQs = new HzJkQsBean();
					// 用于获取下一个交易日的日期
					//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
					tradeDate = baseOperDeal.getWorkDay
					(readType.getHolidaysCode(), rs.getDate("FDate"), 1);
					//edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
					hzJkQs.setDate(tradeDate);
					hzJkQs.setZqdm(rs.getString("FZqdm"));
					hzJkQs.setZqbz(rs.getString("FZqbz"));
					hzJkQs.setYwbz(rs.getString("FYwbz"));
					hzJkQs.setJyxwh(rs.getString("FJYXWH"));
					hzJkQs.setJyfs(rs.getString("FJYFS"));
					hzJkQs.setPortCode(rs.getString("FPortCode"));
					alChangeDate.add(hzJkQs);
				}
			}

			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			Iterator iterator = alChangeDate.iterator();
			while (iterator.hasNext()) {
				hzJkQs = (HzJkQsBean) iterator.next();
				strSql = " update " + pub.yssGetTableName("Tb_HzJkQs")
						+ " set FDate = " + dbl.sqlDate(hzJkQs.getDate())
						+(hzJkQs.getZqbz().equals("QY")?",fds= 'ZD_QY_T+1'":",FDS='ZD_JK_T+1'") // add by lidaolong #536 有关国内接口数据处理顺序的变更
						+ " where FInDate = " + dbl.sqlDate(date)
						+ " and FZqdm = " + dbl.sqlString(hzJkQs.getZqdm())
						+ " and FZqbz = " + dbl.sqlString(hzJkQs.getZqbz())
						+ " and FYwbz = " + dbl.sqlString(hzJkQs.getYwbz())
						+ " and FJYXWH = " + dbl.sqlString(hzJkQs.getJyxwh())
						+ " and FJYFS = " + dbl.sqlString(hzJkQs.getJyfs());
				st.addBatch(strSql); // 更新交易接口清算表中相关数据的交易日期
			}
			st.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交
		} catch (Exception e) {
			throw new YssException("将交易接口清算表中特殊数据的业务日期调整为下一工作日时出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(con, bTrans);
			dbl.closeStatementFinal(st);
		}
	}

	/**
	 * 调整交易接口清算表的风险金数据
	 * 
	 * @throws YssException
	 */
	private void dealFxjOfCommonSeat() throws YssException {
		try {

		} catch (Exception e) {

		} finally {

		}
	}

	/**
	 * 用于判断当前组合代码是否在接口处理中已选的， 费用(如：上海经手费)按某种算法(如：成交明细)计算的组合代码
	 * 
	 * @param portCodes
	 *            String
	 * @param portcode
	 *            String
	 * @param info
	 *            String
	 * @return boolean
	 */
	private boolean judgeIfPortCodeIn(String portCodes, String portcode,
			String info) {
		String setPortCodes = "";
		// 在接口处理界面已选的组合代码中筛选费用(如：上海经手费)按某种算法(如：成交明细)计算的组合代码
		if (info != null) {
			setPortCodes = splitMethodInfo(info, portCodes);
		}

		String[] setPortCodess = setPortCodes.split(","); // 拆分筛选后的组合代码
		ArrayList al = new ArrayList();
		for (int i = 0; i < setPortCodess.length; i++) {
			// 将拆分后的组合代码储存到ArrayList中
			al.add(setPortCodess[i]);
		}
		// 判断当前组合是否在筛选后的组合代码中
		if (al.contains(portcode)) {
			return true; // 包含就返回真
		} else {
			return false; // 不包含就返回假
		}
	}

	// 计算债券成本时根据利息税入成本的方式（明细/申请编号/汇总）将利息税计入成本，即成交金额=净价+利息税
	// 当数据都存入到交易接口清算表后，再根据债券在数据接口参数设置中利息税入成本的方式来修改成交金额或国债利息数据。
	// 若利息税按明细入成本，那么交易接口明细库，交易接口汇总库，交易接口清算库的成交金额 = 成交金额 + 利息税；
	// 若利息税按汇总入成本，那么交易接口清算库的金额 = 成交金额 + 利息税；
	// 若利息税按明细不入成本，那么交易接口明细库，交易接口汇总库，交易接口清算库的国债利息字段储存的数据必须是税前的债券利息数据
	private void dealZQInfoByInterest() throws YssException {
		String ywbz = "";// 业务标志
		String portCode = "";// 组合代码
		ExchangeBondBean exchangeBond = null;
		String szsh = "";// 交易所代码
		String szshYwbz = "";
		String shYwbz = "";
		String szYwbz = "";
		String shGzlxYwbz = "";
		String szGzlxYwbz = "";
		String inteDutyType = "";// 利息税处理方式

		HashMap hmChangeCjjeThree = new HashMap();// 用于储存交易接口明细表，汇总表，清算表中需要修改成交金额的债券的业务标志和交易所代码
		HashMap hmChangeCjjeOne = new HashMap();// 用于储存需要交易接口清算表中需要修改成交金额的债券的业务标志和交易所代码
		HashMap hmChangeGzlx = new HashMap();// 用于储存交易接口明细表，汇总表，清算表中需要修改国债利息的债券的业务标志和交易所代码

		HashMap hmChangeGzlx2 = new HashMap();
		
		String strSql = "";// 用于储存sql语句
		ResultSet rs = null;// 声明结果集
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement
		try {
			// 查询交易接口清算表中相关交易日期的债券的业务标志，组合代码。交易所代码
			strSql = "select distinct FYwbz, FPortCode, FSzsh from "
					+ pub.yssGetTableName("Tb_HzJkQs")
					+ " where FZqbz = 'ZQ' and FInDate = " + dbl.sqlDate(date);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				ywbz = rs.getString("Fywbz");// 业务标志
				portCode = rs.getString("FPortCode");// 组合代码
				szsh = rs.getString("FSzsh");// /交易所代码

				if (ywbz.equals("MR_GZ") || ywbz.equals("MC_GZ") ||
					ywbz.equals("MR_DFZFZ") || ywbz.equals("MC_DFZFZ")	) {// 国债 地方政府债
					if (szsh.equals("CG")) {
						// 获取上海国债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 01");
					}

					if (szsh.equals("CS")) {
						// 获取深圳国债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 01");
					}
				}

				if (ywbz.equals("MR_FLKZZ") || ywbz.equals("MC_FLKZZ")) {// 分离可转债
					if (szsh.equals("CG")) {
						// 获取上海分离可转债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 04");
					}
					if (szsh.equals("CS")) {
						// 获取深圳分离可转债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 04");
					}
				}

				if (ywbz.equals("MR_QYZQ_GS") || ywbz.equals("MC_QYZQ_GS")) {// 公司债
					if (szsh.equals("CG")) {
						// 获取上海公司债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 05");
					}
					if (szsh.equals("CS")) {
						// 获取深圳公司债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 05");
					}
				}

				if (ywbz.equals("MR_QYZQ") || ywbz.equals("MC_QYZQ")) {// 企业债
					if (szsh.equals("CG")) {
						// 获取上海企业债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 02");
					}
					if (szsh.equals("CS")) {
						// 获取深圳企业债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 02");
					}
				}

				if (ywbz.equals("MR_KZZ") || ywbz.equals("MC_KZZ")
						|| ywbz.equals("KZZGP")) {// 可转债
					if (szsh.equals("CG")) {
						// 获取上海可转债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 03");
					}
					if (szsh.equals("CS")) {
						// 获取深圳可转债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 03");
					}
				}

				if (ywbz.equals("MR_ZCZQ") || ywbz.equals("MC_ZCZQ")) {// 资产证券化产品
					if (szsh.equals("CG")) {
						// 获取上海资产证券化产品的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 06");
					}
					if (szsh.equals("CS")) {
						// 获取深圳资产证券化产品的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 06");
					}
				}

				inteDutyType = exchangeBond.getInteDutyType();// 债券的利息税处理方式

				if (exchangeBond.getBondTradeType().equals("00")) { // 若债券为净价交易
					if (inteDutyType.equals("00")) { // 按明细入成本
						if (szsh.equals("CG")) {
							if (hmChangeCjjeThree.get("CG") != null) {
								// 获取交易接口明细表，汇总表，清算表中需要修改成交金额的上交所债券的业务标志
								szshYwbz = (String) hmChangeCjjeThree.get("CG");
								szshYwbz += ywbz + ",";// 添加业务标志
								// 将修改后的业务标志数据储存到HashMap中
								hmChangeCjjeThree.put("CG", szshYwbz);
							} else {
								// 将需要修改成交金额的债券的业务标志储存到HashMap中
								hmChangeCjjeThree.put("CG", ywbz + ",");
							}
						}
						if (szsh.equals("CS")) {
							if (hmChangeCjjeThree.get("CS") != null) {
								// 获取交易接口明细表，汇总表，清算表中需要修改成交金额的深交所债券的业务标志
								szshYwbz = (String) hmChangeCjjeThree.get("CS");
								szshYwbz += ywbz + ",";// 添加业务标志
								// 将修改后的业务标志数据储存到HashMap中
								hmChangeCjjeThree.put("CS", szshYwbz);
							} else {
								// 将需要修改成交金额的债券的业务标志储存到HashMap中
								hmChangeCjjeThree.put("CS", ywbz + ",");
							}
						}
					}

					if (inteDutyType.equals("01")) { // 按明细不入成本（进应收利息）
						if (szsh.equals("CG")) {
							if (hmChangeGzlx.get("CG") != null) {
								// 获取交易接口明细表，汇总表，清算表中需要修改国债利息的上交所债券的业务标志
								szshYwbz = (String) hmChangeGzlx.get("CG");
								szshYwbz += ywbz + ",";// 添加业务标志
								// 将修改后的业务标志数据储存到HashMap中
								hmChangeGzlx.put("CG", szshYwbz);
							} else {
								// 将需要修改国债利息的债券的业务标志储存到HashMap中
								hmChangeGzlx.put("CG", ywbz + ",");
							}
						}
						if (szsh.equals("CS")) {
							if (hmChangeGzlx.get("CS") != null) {
								// 获取交易接口明细表，汇总表，清算表中需要修改国债利息的深交所债券的业务标志
								szshYwbz = (String) hmChangeGzlx.get("CS");
								szshYwbz += ywbz + ",";// 添加业务标志
								// 将修改后的业务标志数据储存到HashMap中
								hmChangeGzlx.put("CS", szshYwbz);
							} else {
								// 将需要修改国债利息的债券的业务标志储存到HashMap中
								hmChangeGzlx.put("CS", ywbz + ",");
							}
						}
					}

					if (inteDutyType.equals("02")) { // 按汇总入成本
						if (szsh.equals("CG")) {
							if (hmChangeCjjeOne.get("CG") != null) {
								// 获取交易接口清算表中需要修改成交金额的上交所债券的业务标志
								szshYwbz = (String) hmChangeCjjeOne.get("CG");
								szshYwbz += ywbz + ",";// 添加业务标志
								// 将修改后的业务标志数据储存到HashMap中
								hmChangeCjjeOne.put("CG", szshYwbz);
							} else {
								// 将需要修改成交金额的债券的业务标志储存到HashMap中
								hmChangeCjjeOne.put("CG", ywbz + ",");
							}
						}
						if (szsh.equals("CS")) {
							if (hmChangeCjjeOne.get("CS") != null) {
								// 获取交易接口清算表中需要修改成交金额的深交所债券的业务标志
								szshYwbz = (String) hmChangeCjjeOne.get("CS");
								szshYwbz += ywbz + ",";// 添加业务标志
								// 将修改后的业务标志数据储存到HashMap中
								hmChangeCjjeOne.put("CS", szshYwbz);
							} else {
								// 将需要修改成交金额的债券的业务标志储存到HashMap中
								hmChangeCjjeOne.put("CS", ywbz + ",");
							}
						}
					}
				}
				
				//全价交易
				if(exchangeBond.getBondTradeType().equals("01")){
					if (szsh.equals("CG")) {
						if(hmChangeGzlx2.get("QJCG") != null){
							szshYwbz = (String) hmChangeGzlx2.get("QJCG");
							szshYwbz += ywbz + ",";// 添加业务标志
							hmChangeGzlx2.put("QJCG", szshYwbz);
						}
						else{
							hmChangeGzlx2.put("QJCG", ywbz + ",");
						}
					}
					else{
						if(hmChangeGzlx2.get("QJCS") != null){
							szshYwbz = (String) hmChangeGzlx2.get("QJCS");
							szshYwbz += ywbz + ",";// 添加业务标志
							hmChangeGzlx2.put("QJCS", szshYwbz);
						}
						else{
							hmChangeGzlx2.put("QJCS", ywbz + ",");
						}
					}
				}
			}

			dbl.closeResultSetFinal(rs);
			rs = null;

			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			// 获取交易接口明细表，汇总表，清算表中需要修改成交金额的上交所债券的业务标志
			shYwbz = (String) hmChangeCjjeThree.get("CG");

			if (shYwbz != null && shYwbz.length() > 1) {
				shYwbz = shYwbz.substring(0, shYwbz.length() - 1);// 去掉字符串最后的逗号
			}

			if (shYwbz != null && shYwbz.length() != 0) {
				// 查询交易接口明细表中需要修改成交金额的上交所债券数据
				strSql = "select * from " + pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CG' and FInDate = "
						+ dbl.sqlDate(date) + " and FYwbz in("
						+ operSql.sqlCodes(shYwbz) + ")";
				// 将交易接口明细表的债券的利息税算出来之后加到成交金额中去
				changeZQInfoOfMx(strSql, "HzJkMxCjje");

				st = con.createStatement(); // 新建Statement
				
				strSql = "select FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, FGddm,"
						+ " FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode, "
						+ "sum(FCjje) as FCjje from "
						+ pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzsh = 'CG' and FInDate = "
						+ dbl.sqlDate(date)
						+ " and FYwbz in("
						+ operSql.sqlCodes(shYwbz)
						+ ") group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, "
						+ "FGddm, FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					strSql = "update " + pub.yssGetTableName("Tb_HzJkHz")
							+ " set FCjje = " + rs.getDouble("FCjje")
							+ " where FDate = "
							+ dbl.sqlDate(rs.getDate("FDate"))
							+ " and FInDate = "
							+ dbl.sqlDate(rs.getDate("FInDate"))
							+ " and FJyfs = "
							+ dbl.sqlString(rs.getString("FJyfs"))
							+ " and Zqdm = "
							+ dbl.sqlString(rs.getString("Zqdm"))
							+ " and FZqdm = "
							+ dbl.sqlString(rs.getString("FZqdm"))
							+ " and FSzsh = "
							+ dbl.sqlString(rs.getString("FSzsh"))
							+ " and FGddm = "
							+ dbl.sqlString(rs.getString("FGddm"))
							+ " and FJyxwh = "
							+ dbl.sqlString(rs.getString("FJyxwh"))
							+ " and FBs = "
							+ dbl.sqlString(rs.getString("FBs"))
							+ " and FZqbz = "
							+ dbl.sqlString(rs.getString("FZqbz"))
							+ " and FYwbz = "
							+ dbl.sqlString(rs.getString("FYwbz"))
							+ " and FSqbh = "
							+ dbl.sqlString(rs.getString("FSqbh"))
							+ " and FTzbz = "
							+ dbl.sqlString(rs.getString("FTzbz"))
							+ " and FPortCode = "
							+ dbl.sqlString(rs.getString("FPortCode"));

					st.addBatch(strSql);
				}

				st.executeBatch();

				dbl.closeResultSetFinal(rs);
				rs = null;
				dbl.closeStatementFinal(st);
				st = null;

				st = con.createStatement(); // 新建Statement

				strSql = "select FDate, FInDate, FJyfs , FZqdm, FJyxwh, FBs, FZqbz, FYwbz, "
						+ "FPortCode, sum(FCjje) as FCjje from "
						+ pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CG' and FInDate = "
						+ dbl.sqlDate(date)
						+ " and FYwbz in("
						+ operSql.sqlCodes(shYwbz)
						+ ") group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, "
						+ "FGddm, FJyxwh, FBs, FZqbz, FYwbz, FTzbz, FPortCode";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					if (rs.getString("FBS").equals("B")) {
						strSql = " update "
								+ pub.yssGetTableName("Tb_HzJkQs")
								+ " set FBJe = " + rs.getDouble("FCjje")
								+ " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FJyfs = "
								+ dbl.sqlString(rs.getString("FJyfs"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and FYwbz = "
								+ dbl.sqlString(rs.getString("FYwbz"))
								+ " and FPortCode = "
								+ dbl.sqlString(rs.getString("FPortCode"));
					}
					if (rs.getString("FBS").equals("S")) {
						strSql = " update "
								+ pub.yssGetTableName("Tb_HzJkQs")
								+ " set FSJe = " + rs.getDouble("FCjje")
								+ " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FJyfs = "
								+ dbl.sqlString(rs.getString("FJyfs"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and FYwbz = "
								+ dbl.sqlString(rs.getString("FYwbz"))
								+ " and FPortCode = "
								+ dbl.sqlString(rs.getString("FPortCode"));
					}

					st.addBatch(strSql);
				}

				st.executeBatch();

				dbl.closeResultSetFinal(rs);
				rs = null;
				dbl.closeStatementFinal(st);
				st = null;
			}

			// 获取交易接口明细表，汇总表，清算表中需要修改成交金额的深交所债券的业务标志
			szYwbz = (String) hmChangeCjjeThree.get("CS");

			if (szYwbz != null && szYwbz.length() > 1) {
				szYwbz = szYwbz.substring(0, szYwbz.length() - 1);// 去掉字符串最后的逗号
			}

			if (szYwbz != null && szYwbz.length() != 0) {
				// 查询交易接口明细表中需要修改成交金额的深交所债券数据
				strSql = "select * from " + pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CS' and FInDate = "
						+ dbl.sqlDate(date) + " and FYwbz in("
						+ operSql.sqlCodes(szYwbz) + ")";
				// 将交易接口明细表的债券的利息税算出来之后加到成交金额中去
				changeZQInfoOfMx(strSql, "HzJkMxCjje");

				st = con.createStatement(); // 新建Statement

				strSql = "select FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, FGddm,"
						+ " FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode, "
						+ "sum(FCjje) as FCjje from "
						+ pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CS' and FInDate = "
						+ dbl.sqlDate(date)
						+ " and FYwbz in("
						+ operSql.sqlCodes(szYwbz)
						+ ") group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, "
						+ "FGddm, FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					strSql = "update " + pub.yssGetTableName("Tb_HzJkHz")
							+ " set FCjje = " + rs.getDouble("FCjje")
							+ " where FDate = "
							+ dbl.sqlDate(rs.getDate("FDate"))
							+ " and FInDate = "
							+ dbl.sqlDate(rs.getDate("FInDate"))
							+ " and FJyfs = "
							+ dbl.sqlString(rs.getString("FJyfs"))
							+ " and Zqdm = "
							+ dbl.sqlString(rs.getString("Zqdm"))
							+ " and FZqdm = "
							+ dbl.sqlString(rs.getString("FZqdm"))
							+ " and FSzsh = "
							+ dbl.sqlString(rs.getString("FSzsh"))
							+ " and FGddm = "
							+ dbl.sqlString(rs.getString("FGddm"))
							+ " and FJyxwh = "
							+ dbl.sqlString(rs.getString("FJyxwh"))
							+ " and FBs = "
							+ dbl.sqlString(rs.getString("FBs"))
							+ " and FZqbz = "
							+ dbl.sqlString(rs.getString("FZqbz"))
							+ " and FYwbz = "
							+ dbl.sqlString(rs.getString("FYwbz"))
							+ " and FSqbh = "
							+ dbl.sqlString(rs.getString("FSqbh"))
							+ " and FTzbz = "
							+ dbl.sqlString(rs.getString("FTzbz"))
							+ " and FPortCode = "
							+ dbl.sqlString(rs.getString("FPortCode"));
					st.addBatch(strSql);
				}

				st.executeBatch();

				strSql = "select FDate, FInDate, FJyfs , FZqdm, FJyxwh, FBs, FZqbz, FYwbz, "
						+ "FPortCode, sum(FCjje) as FCjje from "
						+ pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CS' and FInDate = "
						+ dbl.sqlDate(date)
						+ " and FYwbz in("
						+ operSql.sqlCodes(szYwbz)
						+ ") group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, "
						+ "FGddm, FJyxwh, FBs, FZqbz, FYwbz, FTzbz, FPortCode";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					if (rs.getString("FBS").equals("B")) {
						strSql = " update "
								+ pub.yssGetTableName("Tb_HzJkQs")
								+ " set FBJe = " + rs.getDouble("FCjje")
								+ " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FJyfs = "
								+ dbl.sqlString(rs.getString("FJyfs"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and FYwbz = "
								+ dbl.sqlString(rs.getString("FYwbz"))
								+ " and FPortCode = "
								+ dbl.sqlString(rs.getString("FPortCode"));
					}
					if (rs.getString("FBS").equals("S")) {
						strSql = " update "
								+ pub.yssGetTableName("Tb_HzJkQs")
								+ " set FSJe = " + rs.getDouble("FCjje")
								+ " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FJyfs = "
								+ dbl.sqlString(rs.getString("FJyfs"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and FYwbz = "
								+ dbl.sqlString(rs.getString("FYwbz"))
								+ " and FPortCode = "
								+ dbl.sqlString(rs.getString("FPortCode"));
					}

					st.addBatch(strSql);
				}

				st.executeBatch();

				dbl.closeResultSetFinal(rs);
				rs = null;
				dbl.closeStatementFinal(st);
				st = null;
			}

			// 获取交易接口清算表中需要修改成交金额的上交所债券的业务标志
			shYwbz = (String) hmChangeCjjeOne.get("CG");

			if (shYwbz != null && shYwbz.length() > 1) {
				shYwbz = shYwbz.substring(0, shYwbz.length() - 1);// 去掉字符串最后的逗号
			}

			if (shYwbz != null && shYwbz.length() != 0) {
				// 查询交易接口清算表中需要修改成交金额的上交所债券数据
				strSql = "select * from " + pub.yssGetTableName("Tb_HzJkQs")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CG' and FInDate = "
						+ dbl.sqlDate(date) + " and FYwbz in("
						+ operSql.sqlCodes(shYwbz) + ")";
				// 将交易接口清算表的债券的利息税算出来之后加到成交金额中去
				changeZQInfoOfQs(strSql, "HzJkQsCjje");
			}

			// 获取交易接口清算表中需要修改成交金额的深交所债券的业务标志
			szYwbz = (String) hmChangeCjjeOne.get("CS");

			if (szYwbz != null && szYwbz.length() > 1) {
				szYwbz = szYwbz.substring(0, szYwbz.length() - 1);// 去掉字符串最后的逗号
			}

			if (szYwbz != null && szYwbz.length() != 0) {
				// 查询交易接口清算表中需要修改成交金额的深交所债券数据
				strSql = "select * from " + pub.yssGetTableName("Tb_HzJkQs")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CS' and FInDate = "
						+ dbl.sqlDate(date) + " and FYwbz in("
						+ operSql.sqlCodes(szYwbz) + ")";
				// 将交易接口清算表的债券的利息税算出来之后加到成交金额中去
				changeZQInfoOfQs(strSql, "HzJkQsCjje");
			}

			// 获取交易接口明细表，汇总表，清算表中需要修改国债利息的上交所债券的业务标志
			shYwbz = (String) hmChangeGzlx.get("CG");
			
			if (shYwbz != null && shYwbz.length() > 1) {
				shYwbz = shYwbz.substring(0, shYwbz.length() - 1);// 去掉字符串最后的逗号
			}

			if (shYwbz != null && shYwbz.length() != 0) {
				// 查询交易接口明细表中需要修改国债利息的上交所债券数据
				strSql = "select * from " + pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CG' and FInDate = "
						+ dbl.sqlDate(date) + " and FYwbz in("
						+ operSql.sqlCodes(shYwbz) + ")";
				// 将交易接口明细表的债券的税前国债利息算出来之后更新到国债利息字段中去
				changeZQInfoOfMx(strSql, "HzJkMxGzlx");

				st = con.createStatement(); // 新建Statement

				strSql = "select FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, FGddm,"
						+ " FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode, "
						+ "sum(FGzlx) as FGzlx from "
						+ pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzsh = 'CG' and FInDate = "
						+ dbl.sqlDate(date)
						+ " and FYwbz in("
						+ operSql.sqlCodes(shYwbz)
						+ ") group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, "
						+ "FGddm, FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					strSql = "update " + pub.yssGetTableName("Tb_HzJkHz")
							+ " set FGzlx = " + rs.getDouble("FGzlx")
							+ " where FDate = "
							+ dbl.sqlDate(rs.getDate("FDate"))
							+ " and FInDate = "
							+ dbl.sqlDate(rs.getDate("FInDate"))
							+ " and FJyfs = "
							+ dbl.sqlString(rs.getString("FJyfs"))
							+ " and Zqdm = "
							+ dbl.sqlString(rs.getString("Zqdm"))
							+ " and FZqdm = "
							+ dbl.sqlString(rs.getString("FZqdm"))
							+ " and FSzsh = "
							+ dbl.sqlString(rs.getString("FSzsh"))
							+ " and FGddm = "
							+ dbl.sqlString(rs.getString("FGddm"))
							+ " and FJyxwh = "
							+ dbl.sqlString(rs.getString("FJyxwh"))
							+ " and FBs = "
							+ dbl.sqlString(rs.getString("FBs"))
							+ " and FZqbz = "
							+ dbl.sqlString(rs.getString("FZqbz"))
							+ " and FYwbz = "
							+ dbl.sqlString(rs.getString("FYwbz"))
							+ " and FSqbh = "
							+ dbl.sqlString(rs.getString("FSqbh"))
							+ " and FTzbz = "
							+ dbl.sqlString(rs.getString("FTzbz"))
							+ " and FPortCode = "
							+ dbl.sqlString(rs.getString("FPortCode"));

					st.addBatch(strSql);
				}

				st.executeBatch();

				dbl.closeResultSetFinal(rs);
				rs = null;
				dbl.closeStatementFinal(st);
				st = null;

				st = con.createStatement(); // 新建Statement

				strSql = "select FDate, FInDate, FJyfs , FZqdm, FJyxwh, FBs, FZqbz, FYwbz, "
						+ "FPortCode, sum(FGzlx) as FGzlx from "
						+ pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CG' and FInDate = "
						+ dbl.sqlDate(date)
						+ " and FYwbz in("
						+ operSql.sqlCodes(shYwbz)
						+ ") group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, "
						+ "FGddm, FJyxwh, FBs, FZqbz, FYwbz,  FTzbz, FPortCode";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					if (rs.getString("FBS").equals("B")) {
						strSql = " update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FBGzlx = " + rs.getDouble("FGzlx")
								+ " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FJyfs = "
								+ dbl.sqlString(rs.getString("FJyfs"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and FYwbz = "
								+ dbl.sqlString(rs.getString("FYwbz"))
								+ " and FPortCode = "
								+ dbl.sqlString(rs.getString("FPortCode"));
					}
					if (rs.getString("FBS").equals("S")) {
						strSql = " update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FSGzlx = " + rs.getDouble("FGzlx")
								+ " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FJyfs = "
								+ dbl.sqlString(rs.getString("FJyfs"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and FYwbz = "
								+ dbl.sqlString(rs.getString("FYwbz"))
								+ " and FPortCode = "
								+ dbl.sqlString(rs.getString("FPortCode"));
					}

					st.addBatch(strSql);
				}

				st.executeBatch();

				dbl.closeResultSetFinal(rs);
				rs = null;
				dbl.closeStatementFinal(st);
				st = null;
			}
			
			shGzlxYwbz = (String) hmChangeGzlx2.get("QJCG");
			
			if (shGzlxYwbz != null && shGzlxYwbz.length() > 1) {
				shGzlxYwbz = shGzlxYwbz.substring(0, shGzlxYwbz.length() - 1);// 去掉字符串最后的逗号
			}
			
			// 若为全价交易，则根据清算表中的成交数量乘以百元债券利息得到清算表中的债券利息
			if (shGzlxYwbz != null && shGzlxYwbz.length() != 0) {
				// 查询交易接口清算表中需要修改国债利息的上交所债券数据
				strSql = "select * from " + pub.yssGetTableName("Tb_HzJkQs")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CG' and FInDate = "
						+ dbl.sqlDate(date) + " and FYwbz in("
						+ operSql.sqlCodes(shGzlxYwbz) + ")";
				// 将交易接口清算表的债券的税前国债利息算出来之后更新到国债利息字段中去
				changeZQInfoOfQs(strSql, "HzJkQsGzlx");
			}

			// 获取交易接口明细表，汇总表，清算表中需要修改国债利息的深交所债券的业务标志
			szYwbz = (String) hmChangeGzlx.get("CS");
			
			if (szYwbz != null && szYwbz.length() > 1) {
				szYwbz = szYwbz.substring(0, szYwbz.length() - 1);// 去掉字符串最后的逗号
			}

			if (szYwbz != null && szYwbz.length() != 0) {
				// 查询交易接口明细表中需要修改国债利息的深交所债券数据
				strSql = "select * from " + pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CS' and FInDate = "
						+ dbl.sqlDate(date) + " and FYwbz in("
						+ operSql.sqlCodes(szYwbz) + ")";
				// 将交易接口明细表的债券的税前国债利息算出来之后更新到国债利息字段中去
				changeZQInfoOfMx(strSql, "HzJkMxGzlx");

				st = con.createStatement(); // 新建Statement

				strSql = "select FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, FGddm,"
						+ " FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode, "
						+ "sum(FGzlx) as FGzlx from "
						+ pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CS' and FInDate = "
						+ dbl.sqlDate(date)
						+ " and FYwbz in("
						+ operSql.sqlCodes(szYwbz)
						+ ") group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, "
						+ "FGddm, FJyxwh, FBs, FZqbz, FYwbz, FSqbh, FTzbz, FPortCode";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					strSql = "update " + pub.yssGetTableName("Tb_HzJkHz")
							+ " set FGzlx = " + rs.getDouble("FGzlx")
							+ " where FDate = "
							+ dbl.sqlDate(rs.getDate("FDate"))
							+ " and FInDate = "
							+ dbl.sqlDate(rs.getDate("FInDate"))
							+ " and FJyfs = "
							+ dbl.sqlString(rs.getString("FJyfs"))
							+ " and Zqdm = "
							+ dbl.sqlString(rs.getString("Zqdm"))
							+ " and FZqdm = "
							+ dbl.sqlString(rs.getString("FZqdm"))
							+ " and FSzsh = "
							+ dbl.sqlString(rs.getString("FSzsh"))
							+ " and FGddm = "
							+ dbl.sqlString(rs.getString("FGddm"))
							+ " and FJyxwh = "
							+ dbl.sqlString(rs.getString("FJyxwh"))
							+ " and FBs = "
							+ dbl.sqlString(rs.getString("FBs"))
							+ " and FZqbz = "
							+ dbl.sqlString(rs.getString("FZqbz"))
							+ " and FYwbz = "
							+ dbl.sqlString(rs.getString("FYwbz"))
							+ " and FSqbh = "
							+ dbl.sqlString(rs.getString("FSqbh"))
							+ " and FTzbz = "
							+ dbl.sqlString(rs.getString("FTzbz"))
							+ " and FPortCode = "
							+ dbl.sqlString(rs.getString("FPortCode"));
					st.addBatch(strSql);
				}

				st.executeBatch();

				strSql = "select FDate, FInDate, FJyfs , FZqdm, FJyxwh, FBs, FZqbz, FYwbz, "
						+ "FPortCode, sum(FGzlx) as FGzlx from "
						+ pub.yssGetTableName("Tb_HzJkMx")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CS' and FInDate = "
						+ dbl.sqlDate(date)
						+ " and FYwbz in("
						+ operSql.sqlCodes(szYwbz)
						+ ") group by FDate,FInDate, FJyfs ,Zqdm, FZqdm, FSzsh, "
						+ "FGddm, FJyxwh, FBs, FZqbz, FYwbz, FTzbz, FPortCode";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					if (rs.getString("FBS").equals("B")) {
						strSql = " update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FBGzlx = " + rs.getDouble("FGzlx")
								+ " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FJyfs = "
								+ dbl.sqlString(rs.getString("FJyfs"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and FYwbz = "
								+ dbl.sqlString(rs.getString("FYwbz"))
								+ " and FPortCode = "
								+ dbl.sqlString(rs.getString("FPortCode"));
					}
					if (rs.getString("FBS").equals("S")) {
						strSql = " update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FSGzlx = " + rs.getDouble("FGzlx")
								+ " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FJyfs = "
								+ dbl.sqlString(rs.getString("FJyfs"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and FYwbz = "
								+ dbl.sqlString(rs.getString("FYwbz"))
								+ " and FPortCode = "
								+ dbl.sqlString(rs.getString("FPortCode"));
					}

					st.addBatch(strSql);
				}

				st.executeBatch();

				dbl.closeResultSetFinal(rs);
				rs = null;
				dbl.closeStatementFinal(st);
				st = null;
			}

			szGzlxYwbz = (String) hmChangeGzlx2.get("QJCS");

			if (szGzlxYwbz != null && szGzlxYwbz.length() > 1) {
				szGzlxYwbz = szGzlxYwbz.substring(0, szGzlxYwbz.length() - 1);// 去掉字符串最后的逗号
			}
				
			// 若为全价交易，则根据清算表中的成交数量乘以百元债券利息得到清算表中的债券利息
			if (szGzlxYwbz != null && szGzlxYwbz.length() != 0) {
				// 查询交易接口清算表中需要修改国债利息的深交所债券数据
				strSql = "select * from " + pub.yssGetTableName("Tb_HzJkQs")
						+ " where FZqbz = 'ZQ' and FSzSh = 'CS' and FInDate = "
						+ dbl.sqlDate(date) + " and FYwbz in("
						+ operSql.sqlCodes(szGzlxYwbz) + ")";
				// 将交易接口清算表的债券的税前国债利息算出来之后更新到国债利息字段中去
				changeZQInfoOfQs(strSql, "HzJkQsGzlx");
			}

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交
		} catch (Exception e) {
			throw new YssException(
					"根据债券品种的利息税处理方式来调整交易接口明细库，交易接口汇总库，交易接口清算库债券的成交金额或国债利息", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans);
		}
	}

	/**
	 * 将交易接口明细表的债券的利息税算出来之后加到成交金额中去 或将税前国债利息算出来之后更新到国债利息字段中去
	 * 
	 * @param strSql
	 *            String
	 * @param deal
	 *            String
	 * @throws YssException
	 */
	private void changeZQInfoOfMx(String strSql, String deal)
			throws YssException {
		ResultSet rs = null;// 声明结果集
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement

		double cjje = 0;// 成交金额
		double cjsl = 0;// 成交数量
		double gzlx = 0;// 税后国债利息
		String ywbz = null;// 业务标志
		String fZqdm = null;// 证券代码
		String portCode = null;// 组合代码
		HashMap hmZQRate = null;// 用于储存债券的每百元债券利息，税前每百元债券利息，税后每百元债券利息
		ReadTypeBean readType = null;
		String bs = "";// 买卖标志 add by songjie 2009.12.21 MS00847
		// QDV4赢时胜（北京）2009年11月30日03_B
		try {
			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			st = con.createStatement(); // 新建Statement

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				cjje = rs.getDouble("FCjje");
				cjsl = rs.getDouble("FCjsl");
				gzlx = rs.getDouble("FGzlx");
				ywbz = rs.getString("FYwbz");
				portCode = rs.getString("FPortCode");
				fZqdm = rs.getString("FZqdm");
				// 买卖标志 add by songjie 2009.12.21 MS00847
				// QDV4赢时胜（北京）2009年11月30日03_B
				bs = rs.getString("FBS");
				readType = (ReadTypeBean) hmReadType.get(pub
						.getAssetGroupCode()
						+ " " + portCode);

				// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数 edit by songjie
				// 2009.12.21 MS00847 QDV4赢时胜（北京）2009年11月30日03_B
				hmZQRate = super.calculateZQRate(fZqdm, date, bs, portCode);

				// 表示需要更新相关债券的成交金额
				if (deal.equals("HzJkMxCjje")) {
					if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
						ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")	) {
						// 成交金额 = 成交金额 + 利息税 = 成交金额 + 税前债券利息 - 税后债券利息
						cjje += YssFun.roundIt(YssD.mul(YssFun.roundIt(Double
								.parseDouble((String) hmZQRate.get("SQGZLX")),
								readType.getExchangePreci()), cjsl), 2)
								- YssFun.roundIt(YssD.mul(YssFun.roundIt(Double
										.parseDouble((String) hmZQRate
												.get("GZLX")), readType
										.getExchangePreci()), cjsl), 2);
					} else {
						// 成交金额 = 成交金额 + 利息税 = 成交金额 + 税前债券利息 - 税后债券利息
						cjje += YssFun.roundIt(YssD.mul(Double
								.parseDouble((String) hmZQRate.get("SQGZLX")),
								cjsl), 2)
								- YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("GZLX")), cjsl), 2);
					}
				}

				// 表示需要更新相关债券的国债利息
				if (deal.equals("HzJkMxGzlx")) {
					// 国债利息 = 税前国债利息
					if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
						ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
						gzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double
								.parseDouble((String) hmZQRate.get("SQGZLX")),
								readType.getExchangePreci()), cjsl), 2);
					} else {
						gzlx = YssFun.roundIt(YssD.mul(Double
								.parseDouble((String) hmZQRate.get("SQGZLX")),
								cjsl), 2);
					}
				}

				// 更新交易接口明细库中相关债券的国债利息或成交金额
				strSql = "update " + pub.yssGetTableName("Tb_HzJkMx")
						+ " set FCjje = " + cjje + ", FGzlx = " + gzlx
						+ " where FInDate = " + dbl.sqlDate(date)
						+ " and FDate = " + dbl.sqlDate(rs.getDate("FDate"))
						+ " and FZqdm = " + dbl.sqlString(fZqdm)
						+ " and FSzsh = "
						+ dbl.sqlString(rs.getString("FSzSh"))
						+ " and FGddm = "
						+ dbl.sqlString(rs.getString("FGddm"))
						+ " and FJyxwh = "
						+ dbl.sqlString(rs.getString("FJyxwh")) + " and FBs = "
						+ dbl.sqlString(rs.getString("FBs")) + " and FCjsl = "
						+ cjsl + " and FCjjg = " + rs.getDouble("FCjjg")
						+ " and FJsf = " + rs.getDouble("FJsf")
						+ " and FZgf = " + rs.getDouble("FZgf") + " and FYj = "
						+ rs.getDouble("FYj") + " and FSqGzlx = "
						+ rs.getDouble("FSqGzlx") + " and FQtf = "
						+ rs.getDouble("FQtf") + " and FFxj = "
						+ rs.getDouble("FFxj") + " and FZqbz = "
						+ dbl.sqlString(rs.getString("FZqbz"))
						+ " and FYwbz = " + dbl.sqlString(ywbz)
						+ " and FSqbh = "
						+ dbl.sqlString(rs.getString("FSqbh")) + " and Zqdm = "
						+ dbl.sqlString(rs.getString("Zqdm")) + " and FJyfs = "
						+ dbl.sqlString(rs.getString("FJyfs"))
						+ " and FTZBZ = "
						+ dbl.sqlString(rs.getString("FTZBZ"))
						+ " and FPortCode = " + dbl.sqlString(portCode)
						+ " and FCreator = "
						+ dbl.sqlString(rs.getString("FCreator"))
						+ " and FCreateTime = "
						+ dbl.sqlString(rs.getString("FCreateTime"));

				System.out.println(strSql);

				st.addBatch(strSql);
			}

			st.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交
		} catch (Exception e) {
			throw new YssException("根据债券品种的利息税处理方式来调整交易接口明细库债券的成交金额或国债利息", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans);
		}
	}

	/**
	 * 将交易接口汇总表的债券的利息税算出来之后加到成交金额中去 或将税前国债利息算出来之后更新到国债利息字段中去
	 * 
	 * @param strSql
	 *            String
	 * @param deal
	 *            String
	 * @throws YssException
	 */
	private void changeZQInfoOfHz(String strSql, String deal)
			throws YssException {
		ResultSet rs = null;// 声明结果集
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement

		double cjje = 0;// 成交金额
		double cjsl = 0;// 成交数量
		double gzlx = 0;// 国债利息
		String portCode = null;// 组合代码
		String ywbz = null;// 业务标志

		ReadTypeBean readType = null;
		HashMap hmZQRate = null;
		String bs = "";// 买卖标志 add by songjie 2009.12.21 MS00847
		// QDV4赢时胜（北京）2009年11月30日03_B
		try {
			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			st = con.createStatement(); // 新建Statement

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				cjje = rs.getDouble("FCjje");
				cjsl = rs.getDouble("FCjsl");
				gzlx = rs.getDouble("FGzlx");
				portCode = rs.getString("FPortCode");
				ywbz = rs.getString("FYwbz");
				// 买卖标志 add by songjie 2009.12.21 MS00847
				// QDV4赢时胜（北京）2009年11月30日03_B
				bs = rs.getString("FBS");

				readType = (ReadTypeBean) hmReadType.get(pub
						.getAssetGroupCode()
						+ " " + portCode);

				// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数 edit by songjie
				// 2009.12.21 MS00847 QDV4赢时胜（北京）2009年11月30日03_B
				hmZQRate = super.calculateZQRate(rs.getString("FZqdm"), date,
						bs, portCode);

				// 表示需要更新相关债券的成交金额
				if (deal.equals("HzJkHzCjje")) {
					// 成交金额 = 成交金额 + 利息税 = 成交金额 + 税前债券利息 - 税后债券利息
					if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
						ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")	) {
						cjje += YssFun.roundIt(YssD.mul(YssFun.roundIt(Double
								.parseDouble((String) hmZQRate.get("SQGZLX")),
								readType.getExchangePreci()), cjsl), 2)
								- YssFun.roundIt(YssD.mul(YssFun.roundIt(Double
										.parseDouble((String) hmZQRate
												.get("GZLX")), readType
										.getExchangePreci()), cjsl), 2);
					} else {
						cjje += YssFun.roundIt(YssD.mul(Double
								.parseDouble((String) hmZQRate.get("SQGZLX")),
								cjsl), 2)
								- YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("GZLX")), cjsl), 2);
					}
				}

				// 表示需要更新相关债券的国债利息
				if (deal.equals("HzJkHzGzlx")) {
					// 国债利息 = 税前国债利息
					if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
						ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")	) {
						gzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double
//								.parseDouble((String) hmZQRate.get("SQGZLX")),
								.parseDouble((String) hmZQRate.get("GZLX")),
								readType.getExchangePreci()), cjsl), 2);
					} else {
						gzlx = YssFun.roundIt(YssD.mul(Double
//								.parseDouble((String) hmZQRate.get("SQGZLX")),
								.parseDouble((String) hmZQRate.get("GZLX")),
								cjsl), 2);
					}
				}

				// 更新交易接口汇总库中相关债券的国债利息或成交金额
				strSql = "update " + pub.yssGetTableName("Tb_HzJkHz")
						+ " set FCjje = " + cjje + ", FGzlx = " + gzlx
						+ " where FInDate = " + dbl.sqlDate(date)
						+ " and FDate = " + dbl.sqlDate(rs.getDate("FDate"))
						+ " and FZqdm = "
						+ dbl.sqlString(rs.getString("FZqdm"))
						+ " and FSzsh = "
						+ dbl.sqlString(rs.getString("FSzSh"))
						+ " and FGddm = "
						+ dbl.sqlString(rs.getString("FGddm"))
						+ " and FJyxwh = "
						+ dbl.sqlString(rs.getString("FJyxwh")) + " and FBs = "
						+ dbl.sqlString(rs.getString("FBs"))
						+ " and FSqGzlx = " + rs.getDouble("FSqGzlx")
						+ " and FZqbz = "
						+ dbl.sqlString(rs.getString("FZqbz"))
						+ " and FYwbz = "
						+ dbl.sqlString(rs.getString("FYwbz"))
						+ " and FSqbh = "
						+ dbl.sqlString(rs.getString("FSqbh")) + " and Zqdm = "
						+ dbl.sqlString(rs.getString("Zqdm")) + " and FJyfs = "
						+ dbl.sqlString(rs.getString("FJyfs"))
						+ " and FTZBZ = "
						+ dbl.sqlString(rs.getString("FTZBZ"))
						+ " and FPortCode = "
						+ dbl.sqlString(rs.getString("FPortCode"))
						+ " and FCreator = "
						+ dbl.sqlString(rs.getString("FCreator"))
						+ " and FCreateTime = "
						+ dbl.sqlString(rs.getString("FCreateTime"));

				System.out.println(strSql);

				st.addBatch(strSql);
			}

			st.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交
		} catch (Exception e) {
			throw new YssException("根据债券品种的利息税处理方式来调整交易接口汇总库债券的成交金额或国债利息", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans);
		}
	}

	/**
	 * 将交易接口清算表的债券的利息税算出来之后加到成交金额中去 或将税前国债利息算出来之后更新到国债利息字段中去
	 * 
	 * @param strSql
	 *            String
	 * @param deal
	 *            String
	 * @throws YssException
	 */
	private void changeZQInfoOfQs(String strSql, String deal)
			throws YssException {
		ResultSet rs = null;
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement

		double bJe = 0;// 买金额
		double sJe = 0;// 卖金额
		double bSl = 0;// 买数量
		double sSl = 0;// 卖数量
		double bGzlx = 0;// 买国债利息
		double sGzlx = 0;// 卖国债利息

		String portCode = null;// 组合代码
		String fZqdm = null;// 证券代码
		String ywbz = null;// 业务标志

		ReadTypeBean readType = null;

		HashMap hmZQRate = null;
		try {
			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			st = con.createStatement(); // 新建Statement

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bJe = rs.getDouble("FBJe");
				sJe = rs.getDouble("FSJe");
				bSl = rs.getDouble("FBSl");
				sSl = rs.getDouble("FSSl");
				bGzlx = rs.getDouble("FBGzlx");
				sGzlx = rs.getDouble("FSGzlx");
				fZqdm = rs.getString("FZqdm");
				ywbz = rs.getString("FYwbz");
				portCode = rs.getString("FPortCode");

				readType = (ReadTypeBean) hmReadType.get(pub
						.getAssetGroupCode()
						+ " " + portCode);
				// delete by songjie 2009.12.21 MS00847
				// QDV4赢时胜（北京）2009年11月30日03_B
				// hmZQRate = super.calculateZQRate(fZqdm, date);

				// 表示需要更新相关债券的成交金额
				if (deal.equals("HzJkQsCjje")) {
					if (bJe != 0) {// 表示需要更新买金额
						if (bSl != 0) {// 表示需要根据买数量来计算税前国债利息和税后国债利息
							// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
							// edit by songjie 2009.12.21 MS00847
							// QDV4赢时胜（北京）2009年11月30日03_B
							hmZQRate = super.calculateZQRate(fZqdm, date, "B",
									portCode);

							if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
								ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
								bJe += YssFun.roundIt(YssD.mul(YssFun.roundIt(
										Double.parseDouble((String) hmZQRate
												.get("SQGZLX")), readType
												.getExchangePreci()), bSl), 2)
										- YssFun
												.roundIt(
														YssD
																.mul(
																		YssFun
																				.roundIt(
																						Double
																								.parseDouble((String) hmZQRate
																										.get("GZLX")),
																						readType
																								.getExchangePreci()),
																		bSl), 2);
							} else {
								bJe += YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("SQGZLX")), bSl), 2)
										- YssFun.roundIt(YssD.mul(Double
												.parseDouble((String) hmZQRate
														.get("GZLX")), bSl), 2);
							}
						}

						if (sSl != 0) {// 表示需要根据卖数量来计算税前国债利息和税后国债利息
							// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
							// edit by songjie 2009.12.21 MS00847
							// QDV4赢时胜（北京）2009年11月30日03_B
							hmZQRate = super.calculateZQRate(fZqdm, date, "S",
									portCode);

							if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
								ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
								bJe += YssFun.roundIt(YssD.mul(YssFun.roundIt(
										Double.parseDouble((String) hmZQRate
												.get("SQGZLX")), readType
												.getExchangePreci()), sSl), 2)
										- YssFun
												.roundIt(
														YssD
																.mul(
																		YssFun
																				.roundIt(
																						Double
																								.parseDouble((String) hmZQRate
																										.get("GZLX")),
																						readType
																								.getExchangePreci()),
																		sSl), 2);
							} else {
								bJe += YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("SQGZLX")), sSl), 2)
										- YssFun.roundIt(YssD.mul(Double
												.parseDouble((String) hmZQRate
														.get("GZLX")), sSl), 2);
							}
						}

						// 在交易接口清算表中更新相关债券的买金额
						strSql = "update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FBJe = " + bJe + " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = " + dbl.sqlDate(date)
								+ " and FZqdm = " + dbl.sqlString(fZqdm)
								+ " and FJYXWH = "
								+ dbl.sqlString(rs.getString("FJYXWH"))
								+ " and FYWBZ = " + dbl.sqlString(ywbz)
								+ " and FJYFS = "
								+ dbl.sqlString(rs.getString("FJYFS"))
								+ " and FPortCode = " + dbl.sqlString(portCode);

						System.out.println(strSql);

						st.addBatch(strSql);
					}

					if (sJe != 0) {// 表示需要更新卖金额
						if (bSl != 0) {// 表示需要根据买数量来计算税前国债利息和税后国债利息
							// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
							// edit by songjie 2009.12.21 MS00847
							// QDV4赢时胜（北京）2009年11月30日03_B
							hmZQRate = super.calculateZQRate(fZqdm, date, "B",
									portCode);

							if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
								ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
								sJe += YssFun.roundIt(YssD.mul(YssFun.roundIt(
										Double.parseDouble((String) hmZQRate
												.get("SQGZLX")), readType
												.getExchangePreci()), bSl), 2)
										- YssFun
												.roundIt(
														YssD
																.mul(
																		YssFun
																				.roundIt(
																						Double
																								.parseDouble((String) hmZQRate
																										.get("GZLX")),
																						readType
																								.getExchangePreci()),
																		bSl), 2);
							} else {
								sJe += YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("SQGZLX")), bSl), 2)
										- YssFun.roundIt(YssD.mul(Double
												.parseDouble((String) hmZQRate
														.get("GZLX")), bSl), 2);
							}
						}

						if (sSl != 0) {// 表示需要根据卖数量来计算税前国债利息和税后国债利息
							// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
							// edit by songjie 2009.12.21 MS00847
							// QDV4赢时胜（北京）2009年11月30日03_B
							hmZQRate = super.calculateZQRate(fZqdm, date, "S",
									portCode);

							if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
								ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
								sJe += YssFun.roundIt(YssD.mul(YssFun.roundIt(
										Double.parseDouble((String) hmZQRate
												.get("SQGZLX")), readType
												.getExchangePreci()), sSl), 2)
										- YssFun
												.roundIt(
														YssD
																.mul(
																		YssFun
																				.roundIt(
																						Double
																								.parseDouble((String) hmZQRate
																										.get("GZLX")),
																						readType
																								.getExchangePreci()),
																		sSl), 2);
							} else {
								sJe += YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("SQGZLX")), sSl), 2)
										- YssFun.roundIt(YssD.mul(Double
												.parseDouble((String) hmZQRate
														.get("GZLX")), sSl), 2);
							}
						}

						// 在交易接口清算表中更新相关债券的卖金额
						strSql = "update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FSJe = " + sJe + " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = " + dbl.sqlDate(date)
								+ " and FZqdm = " + dbl.sqlString(fZqdm)
								+ " and FJYXWH = "
								+ dbl.sqlString(rs.getString("FJYXWH"))
								+ " and FYWBZ = " + dbl.sqlString(ywbz)
								+ " and FJYFS = "
								+ dbl.sqlString(rs.getString("FJYFS"))
								+ " and FPortCode = " + dbl.sqlString(portCode);

						System.out.println(strSql);

						st.addBatch(strSql);
					}
				}

				// 表示需要更新相关债券的国债利息
				if (deal.equals("HzJkQsGzlx")) {
					if (bGzlx != 0) {// 表示需要更新买国债利息
						if (bSl != 0) {// 表示需要根据买数量来计算税前国债利息
							// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
							// edit by songjie 2009.12.21 MS00847
							// QDV4赢时胜（北京）2009年11月30日03_B
							hmZQRate = super.calculateZQRate(fZqdm, date, "B",
									portCode);

							if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
								ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
								bGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(
										Double.parseDouble((String) hmZQRate
												.get("GZLX")), readType
												.getExchangePreci()), bSl), 2);

							} else {
								bGzlx = YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("GZLX")), bSl), 2);
							}
						}
						if (sSl != 0) {// 表示需要根据卖数量来计算税前国债利息
							// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
							// edit by songjie 2009.12.21 MS00847
							// QDV4赢时胜（北京）2009年11月30日03_B
							hmZQRate = super.calculateZQRate(fZqdm, date, "S",
									portCode);

							if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
								ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
								bGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(
										Double.parseDouble((String) hmZQRate
												.get("GZLX")), readType
												.getExchangePreci()), sSl), 2);
							} else {
								bGzlx = YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("GZLX")), sSl), 2);
							}
						}

						// 在交易接口清算表中更新相关债券的买国债利息
						strSql = "update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FBGzlx = " + bGzlx + " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = " + dbl.sqlDate(date)
								+ " and FZqdm = " + dbl.sqlString(fZqdm)
								+ " and FJYXWH = "
								+ dbl.sqlString(rs.getString("FJYXWH"))
								+ " and FYWBZ = " + dbl.sqlString(ywbz)
								+ " and FJYFS = "
								+ dbl.sqlString(rs.getString("FJYFS"))
								+ " and FPortCode = " + dbl.sqlString(portCode);

						System.out.println(strSql);

						st.addBatch(strSql);
					}
					if (sGzlx != 0) {// 表示需要更新卖国债利息
						if (bSl != 0) {// 表示需要根据买数量来计算税前国债利息
							// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
							// edit by songjie 2009.12.21 MS00847
							// QDV4赢时胜（北京）2009年11月30日03_B
							hmZQRate = super.calculateZQRate(fZqdm, date, "B",
									portCode);

							if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
								ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
								sGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(
										Double.parseDouble((String) hmZQRate
												.get("GZLX")), readType
												.getExchangePreci()), bSl), 2);
							} else {
								sGzlx = YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("GZLX")), bSl), 2);
							}
						}
						if (sSl != 0) {// 表示需要根据卖数量来计算税前国债利息
							// 修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数
							// edit by songjie 2009.12.21 MS00847
							// QDV4赢时胜（北京）2009年11月30日03_B
							hmZQRate = super.calculateZQRate(fZqdm, date, "S",
									portCode);

							if (ywbz.equalsIgnoreCase("MR_GZ") || ywbz.equalsIgnoreCase("MC_GZ") ||
								ywbz.equalsIgnoreCase("MR_DFZFZ") || ywbz.equalsIgnoreCase("MC_DFZFZ")) {
								sGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(
										Double.parseDouble((String) hmZQRate
												.get("GZLX")), readType
												.getExchangePreci()), sSl), 2);
							} else {
								sGzlx = YssFun.roundIt(YssD.mul(Double
										.parseDouble((String) hmZQRate
												.get("GZLX")), sSl), 2);
							}
						}

						// 在交易接口清算表中更新相关债券的卖国债利息
						strSql = "update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FSGzlx = " + sGzlx + " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = " + dbl.sqlDate(date)
								+ " and FZqdm = " + dbl.sqlString(fZqdm)
								+ " and FJYXWH = "
								+ dbl.sqlString(rs.getString("FJYXWH"))
								+ " and FYWBZ = " + dbl.sqlString(ywbz)
								+ " and FJYFS = "
								+ dbl.sqlString(rs.getString("FJYFS"))
								+ " and FPortCode = " + dbl.sqlString(portCode);

						System.out.println(strSql);

						st.addBatch(strSql);
					}
				}
			}

			st.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交
		} catch (Exception e) {
			throw new YssException("根据债券品种的利息税处理方式来调整交易接口清算库债券的成交金额或国债利息", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans);
		}
	}

	/**
	 * ETF申购或赎回的基金的实收实付金额要包含 ETF申购或赎回的股票的过户费.
	 * 
	 * @throws YssException
	 */
	private void dealETFInfo() throws YssException {
		String strSql = null;// 用于储存sql语句
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement

		ResultSet rs = null;// 声明结果集
		ResultSet rs1 = null;// 声明结果集

		double bsfje = 0;// 买实付金额
		double sssje = 0;// 卖实收金额
		double bGhf = 0;// 买过户费
		double sGhf = 0;// 卖过户费
		try {
			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			st = con.createStatement(); // 新建Statement

			strSql = " select * from "
					+ pub.yssGetTableName("Tb_HzJkQs")
					+ " where FZqbz = 'JJ' and FYwbz in ('SH_ETF','SG_ETF') and FInDate = "
					+ dbl.sqlDate(date);
			rs = dbl.openResultSet(strSql);// 在交易接口清算表中查询交易日的ETF申购或赎回的基金的数据

			while (rs.next()) {
				// 在交易接口清算表中根据转换前的证券代码字段的数据查询同一交易日的ETF申购或赎回的股票的汇总之后的买卖过户费
				strSql = " select sum(FBGhf) as FBGhf, sum(FSGhf) as FSGhf from "
						+ pub.yssGetTableName("Tb_HzJkQs")
						+ " where FZqbz = 'GP' and FYwbz in('SH_ETF','SG_ETF') and FInDate = "
						+ dbl.sqlDate(date)
						+ " and Zqdm = "
						+ dbl.sqlString(rs.getString("Zqdm"));
				rs1 = dbl.openResultSet(strSql);

				while (rs1.next()) {
					bGhf = rs1.getDouble("FBGhf");// 获取买过户费
					sGhf = rs1.getDouble("FSGhf");// 获取卖过户费

					if (bGhf != 0) {// 若买过户费不为零
						// 且基金的业务标志为ETF赎回，股票的业务标志为ETF申购
						if (rs.getString("FYwbz").equals("SH_ETF")
								|| rs.getString("FYwbz").equals("SG_ETF")) {
							sssje = bGhf;// 将汇总之后的ETF股票的买过户费赋给ETF基金的卖实收金额
							strSql = " update "
									+ pub.yssGetTableName("Tb_HzJkQs")
									+ " set FSssJe = " + sssje
									+ " where FInDate = " + dbl.sqlDate(date)
									+ " and FZqdm = "
									+ dbl.sqlString(rs.getString("FZqdm"))
									+ " and FZqbz = "
									+ dbl.sqlString(rs.getString("FZqbz"))
									+ " and FYwbz = "
									+ dbl.sqlString(rs.getString("FYwbz"))
									+ " and FPortCode = "
									+ dbl.sqlString(rs.getString("FPortCode"))
									+ " and FJyfs = "
									+ dbl.sqlString(rs.getString("FJyfs"))
									+ " and FDate = "
									+ dbl.sqlDate(rs.getDate("FDate"))
									+ " and FJyxwh = "
									+ dbl.sqlString(rs.getString("FJyxwh"));

							st.addBatch(strSql);
						}
					}

					if (sGhf != 0) {
						// 且基金的业务标志为ETF申购，股票的业务标志为ETF赎回
						if (rs.getString("FYwbz").equals("SG_ETF")
								|| rs1.getString("FYwbz").equals("SH_ETF")) {
							bsfje = sGhf;// 将汇总之后的ETF股票的卖过户费赋给ETF基金的买实付金额
							strSql = " update "
									+ pub.yssGetTableName("Tb_HzJkQs")
									+ " set FBsfJe = " + bsfje
									+ " where FInDate = " + dbl.sqlDate(date)
									+ " and FZqdm = "
									+ dbl.sqlString(rs.getString("FZqdm"))
									+ " and FZqbz = "
									+ dbl.sqlString(rs.getString("FZqbz"))
									+ " and FYwbz = "
									+ dbl.sqlString(rs.getString("FYwbz"))
									+ " and FPortCode = "
									+ dbl.sqlString(rs.getString("FPortCode"))
									+ " and FJyfs = "
									+ dbl.sqlString(rs.getString("FJyfs"))
									+ " and FDate = "
									+ dbl.sqlDate(rs.getDate("FDate"))
									+ " and FJyxwh = "
									+ dbl.sqlString(rs.getString("FJyxwh"));

							st.addBatch(strSql);
						}
					}
				}
			}

			st.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交
		} catch (Exception e) {
			throw new YssException("更新交易接口清算表中ETF申购或赎回的基金的实收实付金额出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs, rs1);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans);
		}
	}

	/**
	 * add by songjie 
	 * 2010.01.15 
	 * 调整全价交易的成交金额
	 */
	private void adjustCjje() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement
		ExchangeBondBean exchangeBond = null;
		String portCode = "";// 组合代码
		String szsh = "";// 交易所代码
		String ywbz = "";// 业务标志
		double cjje = 0;// 成交金额
		try {
			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			st = con.createStatement(); // 新建Statement

			strSql = "select * from " + pub.yssGetTableName("Tb_HzJkQs")
					+ " where FInDate = " + dbl.sqlDate(date) + " and FZQBZ = 'ZQ'";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				portCode = rs.getString("FPortCode");
				szsh = rs.getString("FSZSH");
				ywbz = rs.getString("FYWBZ");
				
				if (ywbz.equals("MR_GZ") || ywbz.equals("MC_GZ") ||
					ywbz.equals("MR_DFZFZ") || ywbz.equals("MC_DFZFZ")) {// 国债
					if (szsh.equals("CG")) {
						// 获取上海国债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 01");
					}

					if (szsh.equals("CS")) {
						// 获取深圳国债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 01");
					}
				}

				if (ywbz.equals("MR_FLKZZ") || ywbz.equals("MC_FLKZZ")) {// 分离可转债
					if (szsh.equals("CG")) {
						// 获取上海分离可转债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 04");
					}
					if (szsh.equals("CS")) {
						// 获取深圳分离可转债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 04");
					}
				}

				if (ywbz.equals("MR_QYZQ_GS") || ywbz.equals("MC_QYZQ_GS")) {// 公司债
					if (szsh.equals("CG")) {
						// 获取上海公司债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 05");
					}
					if (szsh.equals("CS")) {
						// 获取深圳公司债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 05");
					}
				}

				if (ywbz.equals("MR_QYZQ") || ywbz.equals("MC_QYZQ")) {// 企业债
					if (szsh.equals("CG")) {
						// 获取上海企业债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 02");
					}
					if (szsh.equals("CS")) {
						// 获取深圳企业债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 02");
					}
				}

				if (ywbz.equals("MR_KZZ") || ywbz.equals("MC_KZZ")
						|| ywbz.equals("KZZGP")) {// 可转债
					if (szsh.equals("CG")) {
						// 获取上海可转债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 03");
					}
					if (szsh.equals("CS")) {
						// 获取深圳可转债的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 03");
					}
				}

				if (ywbz.equals("MR_ZCZQ") || ywbz.equals("MC_ZCZQ")) {// 资产证券化产品
					if (szsh.equals("CG")) {
						// 获取上海资产证券化产品的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 01 06");
					}
					if (szsh.equals("CS")) {
						// 获取深圳资产证券化产品的参数设置
						exchangeBond = (ExchangeBondBean) hmExchangeBond
								.get(pub.getAssetGroupCode() + " " + portCode
										+ " 02 06");
					}
				}
				
				// 全价交易
				if (exchangeBond != null && exchangeBond.getBondTradeType().equals("01")) { 
					if (rs.getDouble("FBJE") != 0 && rs.getDouble("FBGZLX") != 0) {
						cjje = rs.getDouble("FBJE") - rs.getDouble("FBGZLX");

						strSql = "update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FBJE = " + cjje + " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FSzsh = " + dbl.sqlString(szsh)
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and Fywbz = "
								+ dbl.sqlString(rs.getString("Fywbz"))
								+ " and FJYFS = "
								+ dbl.sqlString(rs.getString("FJYFS"))
								+ " and FPortCode = " + dbl.sqlString(portCode);
						
						st.addBatch(strSql);
					}
					if (rs.getDouble("FSJE") != 0 && rs.getDouble("FSGZLX") != 0) {
						cjje = rs.getDouble("FSJE") - rs.getDouble("FSGZLX");

						strSql = "update " + pub.yssGetTableName("Tb_HzJkQs")
								+ " set FSJE = " + cjje + " where FDate = "
								+ dbl.sqlDate(rs.getDate("FDate"))
								+ " and FInDate = "
								+ dbl.sqlDate(rs.getDate("FInDate"))
								+ " and FZqdm = "
								+ dbl.sqlString(rs.getString("FZqdm"))
								+ " and FSzsh = " + dbl.sqlString(szsh)
								+ " and FJyxwh = "
								+ dbl.sqlString(rs.getString("FJyxwh"))
								+ " and FZqbz = "
								+ dbl.sqlString(rs.getString("FZqbz"))
								+ " and Fywbz = "
								+ dbl.sqlString(rs.getString("Fywbz"))
								+ " and FJYFS = "
								+ dbl.sqlString(rs.getString("FJYFS"))
								+ " and FPortCode = " + dbl.sqlString(portCode);
						
						st.addBatch(strSql);
					}
				}
			}

			st.executeBatch();

			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交
		} catch (Exception e) {
			throw new YssException("调整全价交易的可转债成交金额出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans);
		}
	}
	
	/**
	 * 若为新债中签 则判断业务日期是否大于计息开始日 若大于计息开始日，
	 * 就计算债券利息，并将百元债券利息更新到债券利息表中
     * add by songjie
     * 2010.03.19
     * MS00919
     * QDV4赢时胜（测试）2010年03月18日05_B
	 */
	private void dealXZZQInfo()throws YssException{
		String strSql = "";
		ResultSet rs = null;
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = false;
		Statement st = null; // 声明Statement
		HashMap hmPerZQRate = null;
		HashMap hmZQRate = null;
		String haveInfo = null;
		String bs = "";
		String strGzlx = "";
		double FBeforeGzlx = 0;
		double FGzlx = 0;
		double gzlx = 0;
		double cjsl = 0;
		boolean isGzlx = false;
		BondInterestBean bondInterest = null;
		ArrayList alZQCodes = new ArrayList();
		ArrayList alZQInfo = new ArrayList();
		FeeAttributeBean feeAttribute = null;
		try{
			con.setAutoCommit(false); // 设置手动提交事务
			bTrans = true;

			st = con.createStatement(); // 新建Statement
			
			strSql = " select * from " + pub.yssGetTableName("Tb_HzJkQs") + 
			" where FZqbz = 'XZ' and FYwbz in ('KZZXZ','ZQ_FLKZZ','ZQ_QYZQ','ZQ_KZZ') " + 
			" and FInDate = " + dbl.sqlDate(date);
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				//BUG3869 ad by zhouwei 20120216 start---------
				FBeforeGzlx=0;
				FGzlx=0;
				//BUG3869 ad by zhouwei 20120216 end---------
				//在债券利息表中查询债券利息数据
				hmPerZQRate = super.getPerHundredZQRate(rs.getString("FZQDM"),date);
                haveInfo = (String)hmPerZQRate.get("haveInfo");//判断债券利息表中是否有当前债券利息数据

                if(haveInfo.equals("false")){ //表示债券利息表中没有当前债券利息数据
                    //计算债券的税前每百元债券利息，税后每百元债券利息并储存到哈希表中
                	
                	bs = (rs.getDouble("FBSL") > 0)? "B" : "S";
                	cjsl = (rs.getDouble("FBSL") > 0)? rs.getDouble("FBSL") : rs.getDouble("FSSL");
                	
                	//根据债券信息设置表中的相关信息计算债券的每百元债券利息
                    hmZQRate = super.calculateZQRate(rs.getString("FZQDM"), date, bs, rs.getString("FPortCode"));

                    //若计算出的税前百元债券利息 或 税后百元债券利息都大于零  
                    if(Double.parseDouble((String) hmZQRate.get("SQGZLX")) > 0 ||
                       Double.parseDouble((String) hmZQRate.get("GZLX")) > 0){
                    	//税前债券利息
    					FBeforeGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble((String) hmZQRate.get("SQGZLX")), cjsl), 2);
    					//税后债券利息
    					FGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble((String) hmZQRate.get("GZLX")), cjsl), 2);

                        bondInterest = new BondInterestBean();//新建债券利息实例
                        bondInterest.setSecurityCode(rs.getString("FZQDM"));//设置证券代码
                        bondInterest.setIntAccPer100(new BigDecimal((String)hmZQRate.get("SQGZLX")));//设置税前百元利息
                        bondInterest.setSHIntAccPer100(new BigDecimal((String)hmZQRate.get("GZLX")));//设置税后百元利息

    					if (!alZQCodes.contains(rs.getString("FZQDM"))) {
    						alZQCodes.add(rs.getString("FZQDM"));
    						alZQInfo.add(bondInterest);// 将债券利息实例添加到列表中
    					}
                    }
                }
                else{
					FGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble((
							(String) hmPerZQRate.get("PerGZLX"))), cjsl), 2);
					FBeforeGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble((
							(String) hmPerZQRate.get("SHPerGZLX"))), cjsl), 2);
                }
                
                if(FGzlx > 0 || FBeforeGzlx > 0){
                	feeAttribute = new FeeAttributeBean();
                	feeAttribute.setZqdm(rs.getString("FZQDM"));
                	
                	//判断更新清算表中的税前债券利息还是税后债券利息
                	isGzlx = super.judgeGzlx(feeAttribute);
                	
                	if(isGzlx && FGzlx > 0){
                		strGzlx = "FBGzlx";
                		gzlx = FGzlx;
                	}
                	if(!isGzlx && FBeforeGzlx > 0){
                		strGzlx = "FSGzlx";
                		gzlx = FBeforeGzlx;
                	}
                	
                	if(!strGzlx.equals("")){
                		strSql = "update " + pub.yssGetTableName("Tb_HzJkQs") + 
                		" set " + strGzlx + " = " + gzlx + 
                		" where FDate = " + dbl.sqlDate(rs.getDate("FDate")) + 
                		" and FInDate = " + dbl.sqlDate(date) + 
                		" and FZqdm = " + dbl.sqlString(rs.getString("FZQDM")) + 
                		" and FJyxwh = " + dbl.sqlString(rs.getString("FJyxwh")) + " and FZqbz = 'XZ' " +
                		" and Fywbz = " + dbl.sqlString(rs.getString("Fywbz")) + 
                		" and FJYFS = " + dbl.sqlString(rs.getString("FJYFS")) + 
                		" and FPortCode = " + dbl.sqlString(rs.getString("FPortCode"));
                		
                		st.addBatch(strSql);
                	}
                }
			}
			
			st.executeBatch();
			
			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置自动提交
			
			//将债券利息实体类储存或更新到债券利息表中
			if(alZQCodes.size() > 0){
				insertIntoBondInterest(alZQCodes, alZQInfo);
			}
		}catch(Exception e){
			throw new YssException("更新新债中签的债券利息出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans);
		}
	}
	
	/**
     * 将债券利息数据中没有的债券利息数据插入到表中
     * add by songjie
     * 2010.03.19
     * MS00919
     * QDV4赢时胜（测试）2010年03月18日05_B
     * @throws YssException
     */
    private void insertIntoBondInterest(ArrayList alZQCodes, ArrayList alZQInfo) throws YssException {
        String strSql = "";//用于储存sql语句
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        PreparedStatement pstmt = null;//声明PreparedStatement
        BondInterestBean bondInterest = null;
        Iterator iterator = null;
        String zqdms = "";
        String zqdm = null;
        try{
            iterator = alZQCodes.iterator();//获取迭代器

            while(iterator.hasNext()){
                zqdm = (String)iterator.next();//获取证券代码
                zqdms += zqdm + ",";//拼接证券代码
            }

            if(zqdms.length() >= 1){
                zqdms = zqdms.substring(0, zqdms.length() - 1);//去掉字符串最后逗号
            }

            con.setAutoCommit(false); //设置手动提交事务
            bTrans = true;

            //先在债券利息表中删除需要插入到债券利息表中的债券数据
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where FSecurityCode in(" + operSql.sqlCodes(zqdms) +
                ") and FRecordDate = " + dbl.sqlDate(date);

            dbl.executeSql(strSql);

            //添加数据到债券利息表中
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_BondInterest") +
                "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,FIntAccPer100," +
                "FIntDay,FSHIntAccPer100,FDataSource,FCheckState,FCreator,FCreateTime)" +
                "values(?,?,?,?,?,?,?,?,?,?,?)";

            pstmt=dbl.openPreparedStatement(strSql);

            iterator = alZQInfo.iterator();
            while(iterator.hasNext()){
                bondInterest = (BondInterestBean)iterator.next();//获取债券利息实例

                pstmt.setString(1, bondInterest.getSecurityCode());//设置证券代码
                pstmt.setDate(2, YssFun.toSqlDate(date));//设置业务日期
                pstmt.setDate(3, YssFun.toSqlDate("9998-12-31"));
                pstmt.setDate(4, YssFun.toSqlDate("9998-12-31"));
                pstmt.setBigDecimal(5, bondInterest.getIntAccPer100());//设置税前百元利息
                pstmt.setInt(6, 0);
                pstmt.setBigDecimal(7, bondInterest.getSHIntAccPer100());//设置税后百元利息
                //edit by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
                //数据来源改为 "ZD－自动计算"
                pstmt.setString(8, "ZD");//表示是系统计算而得到的百元债券利息
                pstmt.setInt(9, 1);
                pstmt.setString(10, pub.getUserCode()); //创建人、修改人
                pstmt.setString(11, YssFun.formatDatetime(new java.util.Date())); //创建、修改时间

                pstmt.addBatch();
            }

            pstmt.executeBatch();

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }
        catch(Exception e){
            throw new YssException("将数据插入到债券利息表时出错！",e);
        }
        finally{
            dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(con, bTrans); //关闭连接
        }
    }
}
