package com.yss.main.operdata.futures;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.dayfinish.StorageStatBean;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.util.*;

import java.sql.ResultSet;
import com.yss.main.funsetting.VocabularyBean;
import java.sql.SQLException;
import java.sql.Connection;
import com.yss.util.YssException;
import java.sql.ResultSet;
import com.yss.util.YssCons;
import com.yss.main.funsetting.VocabularyBean;
import java.util.Date;
import java.sql.SQLException;
import com.yss.util.YssFun;
import java.sql.Connection;
import com.yss.main.parasetting.FeeBean;
import com.yss.main.storagemanage.SecurityStorageBean;

/**
 * <p>
 * Title: 操作表TB_XXX_DATA_OptionsTrade的实体类
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * * <br>
 * 1.数据的增、删、改、查操作 2.实体Bean的封装、拆箱 3.数据合法性的检测 </br>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Ysstech
 * </p>
 * 
 * @author libo 20090624
 * @version 1.0
 */
public class OptionsTradeAdmin extends BaseDataSettingBean implements
		IDataSetting {
	private String sRecycled = ""; // 回收站数据
	private OptionsTradeBean optionsTrade = new OptionsTradeBean();// beam数据

	private OptionsTradeAdmin filterType; // parseRowStr方法中 //用于筛选
	public static ArrayList tradeNumPool = null; // 交易编号池，存放一组待使用的交易编号

	public OptionsTradeAdmin() {
	}

	/**
	 * 新增，修改，数据时，检查
	 * 
	 * @param btOper
	 *            byte
	 * @throws YssException
	 */
	public void checkInput(byte btOper) throws YssException {// 重复数据不可改

		dbFun.checkInputCommon(btOper, pub
				.yssGetTableName("TB_Data_OptionsTrade"), "FNum",
				this.optionsTrade.getNum(), this.optionsTrade.getOldNum());
	}

	/**
	 * 新增数据
	 * 
	 * @return String
	 * @throws YssException
	 */
	public String addSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		try {
			strSql = "insert into "
					+ pub.yssGetTableName("TB_Data_OptionsTrade")
					+
					// 2010.04.21 蒋锦 添加 开平标志 南方期权业务
					// MS01134 增加股票期权和股指期权业务
					"(FNum, FSecurityCode,FTradeTypeCode,FPortCode,FBrokerCode,FInvMgrCode,FOffSetFlag,"
					+ "FBegBailAcctCode,FChageBailAcctCode,FInvestTactics,FBargainDate,FSettleDate,FTradeAmount,"
					+ "FTradePrice,FBaseCuryRate,FPortCuryRate,FTradeMoney,FBegBailMoney,FSettleMoney,"
					+ "FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2,FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,"
					+ "FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,FFeeCode7,FTradeFee7,FFeeCode8,FTradeFee8,FDesc,"
					+ "FSettleType,FSettleState,"
					+ "FCheckState,FCreator,FCreateTime,FCheckUser) "
					+ "values("
					+ dbl.sqlString(this.optionsTrade.getNum())
					+ ","
					+ dbl.sqlString(this.optionsTrade.getSecurityCode())
					+ ","
					+ dbl.sqlString(this.optionsTrade.getTradeTypeCode())
					+ ","
					+ dbl.sqlString(this.optionsTrade.getPortCode())
					+ ","
					+ dbl.sqlString((this.optionsTrade.getBrokerCode().trim()
							.length() == 0) ? " " : this.optionsTrade
							.getBrokerCode())
					+ ","
					+ // 交易券商
					dbl.sqlString((this.optionsTrade.getInvMgrCode().trim()
							.length() == 0) ? " " : this.optionsTrade
							.getInvMgrCode())
					+ ","
					+ // 投资经理edited by libo 20090710插入空值--->
					dbl.sqlString(this.optionsTrade.getOffSetFlag())
					+ ","
					+ dbl.sqlString(this.optionsTrade.getBegBailAcctCode())
					+ ","
					+ dbl.sqlString(this.optionsTrade.getChageBailAcctCode())
					+ ","
					+ dbl.sqlString(this.optionsTrade.getInvestTastic())
					+ ","
					+ // 投资策略
					dbl.sqlDate(YssFun.toDate(this.optionsTrade
							.getBargainDate()))
					+ ","
					+ dbl.sqlDate(YssFun.toDate(this.optionsTrade
							.getSettleDate()))
					+ ","
					+ this.optionsTrade.getTradeAmount()
					+ ","
					+ this.optionsTrade.getTradePrice()
					+ ","
					+ this.optionsTrade.getBaseCuryRate()
					+ ","
					+ this.optionsTrade.getPortCuryRate()
					+ ","
					+ this.optionsTrade.getTradeMoney()
					+ ","
					+ this.optionsTrade.getBegBailMoney()
					+ ","
					+ this.optionsTrade.getSettleMoney()
					+ ","
					+ this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
							this.optionsTrade.getFees())
					+ dbl.sqlString(this.optionsTrade.getDesc())
					+ ","
					+ // 描述
					this.optionsTrade.getSettleType()
					+ ","
					+ this.optionsTrade.getSettleState()
					+ ","
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ","
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode)) + ")";

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("新增期权交易数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return this.buildRowStr();
	}

	/**
	 * 修改数据
	 * 
	 * @return String
	 * @throws YssException
	 */
	public String editSetting() throws YssException {
		String strSql = "";
		String num = "";
		String strNumDate = "";
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			// 判断如果修改了交易日期，则交易编号也要相应的重新生成
			if (!this.optionsTrade.getBargainDate().equalsIgnoreCase(
					this.optionsTrade.getOldBargainDate()))
				// 资金调拨表中如果有以该交易编号做关联编号的记录，则一同修改成新编号
				strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer")
						+ " set FRelaNum = "
						+ dbl.sqlString(this.optionsTrade.getNum())
						+ " where FRelaNum = "
						+ dbl.sqlString(this.optionsTrade.getOldNum());
			dbl.executeSql(strSql);

			strSql = "update "
					+ pub.yssGetTableName("TB_Data_OptionsTrade")
					+ " set FNum = "
					+ dbl.sqlString(this.optionsTrade.getNum())
					+ ",FSecurityCode = "
					+ dbl.sqlString(this.optionsTrade.getSecurityCode())
					+ ",FTradeTypeCode = "
					+ dbl.sqlString(this.optionsTrade.getTradeTypeCode())
					+ ",FPortCode = "
					+ dbl.sqlString(this.optionsTrade.getPortCode())
					+ ",FBrokerCode = "
					+ dbl.sqlString(this.optionsTrade.getBrokerCode().trim()
							.length() == 0 ? " " : this.optionsTrade
							.getBrokerCode())
					+ ",FInvMgrCode = "
					+ dbl.sqlString(this.optionsTrade.getInvMgrCode().trim()
							.length() == 0 ? " " : this.optionsTrade
							.getInvMgrCode())
					+ // edited by libo 20090710插入空值--->
					",FBegBailAcctCode = "
					+ dbl.sqlString(this.optionsTrade.getBegBailAcctCode())
					+ ",FChageBailAcctCode = "
					+ dbl.sqlString(this.optionsTrade.getChageBailAcctCode())
					+ ",FInvestTactics = "
					+ dbl.sqlString(this.optionsTrade.getInvestTastic())
					+ ",FBargainDate = "
					+ dbl.sqlDate(this.optionsTrade.getBargainDate())
					+ ",FSettleDate = "
					+ dbl.sqlDate(this.optionsTrade.getSettleDate())
					+ ",FTradeAmount = "
					+ this.optionsTrade.getTradeAmount()
					+ ",FTradePrice = "
					+ this.optionsTrade.getTradePrice()
					+ ",FBaseCuryRate = "
					+ this.optionsTrade.getBaseCuryRate()
					+ ",FPortCuryRate = "
					+ this.optionsTrade.getPortCuryRate()
					+ ",FTradeMoney = "
					+ this.optionsTrade.getTradeMoney()
					+ ",FBegBailMoney = "
					+ this.optionsTrade.getBegBailMoney()
					+ ",FSettleMoney = "
					+ this.optionsTrade.getSettleMoney()
					+
					// 2010.04.21 蒋锦 添加 开平标志 南方期权业务
					// MS01134 增加股票期权和股指期权业务
					",FOffSetFlag = "
					+ dbl.sqlString(this.optionsTrade.getOffSetFlag())
					+ ","
					+ this.operSql.buildSaveFeesSql(YssCons.OP_EDIT,
							this.optionsTrade.getFees())
					+ " FDESC = "
					+ dbl.sqlString(this.optionsTrade.getDesc())
					+ ",FSettleType = "
					+ this.optionsTrade.getSettleType()
					+ ",FSettleState = "
					+ this.optionsTrade.getSettleState()
					+ " ,FCheckstate = "
					+ (pub.getSysCheckState() ? "0" : "1")
					+ " ,FCreator = "
					+ dbl.sqlString(this.creatorCode)
					+ " ,FCreateTime = "
					+ dbl.sqlString(this.creatorTime)
					+ " ,FCheckUser = "
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode)) + " where FNUM = "
					+ dbl.sqlString(this.optionsTrade.getOldNum());
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("修改期权数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return "";
	}

	/**
	 * 删除数据，即放到回收站
	 * 
	 * @throws YssException
	 */
	public void delSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		try {
			strSql = "update " + pub.yssGetTableName("TB_Data_OptionsTrade")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = "
					+ dbl.sqlString(this.optionsTrade.getNum());
			bTrans = true;
			dbl.executeSql(strSql);

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除股指期货交易数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/**
	 * 回收站还原功能
	 * 
	 * @throws YssException
	 */
	public void checkSetting() throws YssException {
		Connection conn = dbl.loadConnection(); // 获取一个连接
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		String[] arrData = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			arrData = this.optionsTrade.getRecycled().split("\r\n");
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0) {
					continue;
				}
				this.parseRowStr(arrData[i]);
				strSql = "update "
						+ pub.yssGetTableName("TB_Data_OptionsTrade")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date()) + "'"
						+ " where FNum = "
						+ dbl.sqlString(this.optionsTrade.getNum());
				dbl.executeSql(strSql);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核期权交易数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return "";
	}

	public IDataSetting getSetting() throws YssException {
		return null;
	}

	public String getAllSetting() throws YssException {
		return "";
	}

	/**
	 * 回收站清除功能
	 * 
	 * @throws YssException
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		Connection conn = null;
		boolean bTrans = false;
		String[] arrData = null;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			arrData = this.optionsTrade.getRecycled().split("\r\n");
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0)
					continue;
				this.parseRowStr(arrData[i]);
				strSql = "delete from "
						+ pub.yssGetTableName("TB_Data_OptionsTrade")
						+ " where FNum = "
						+ dbl.sqlString(this.optionsTrade.getNum());
				dbl.executeSql(strSql);
			}
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		} catch (Exception e) {
			throw new YssException("清除数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	public String getTreeViewData1() throws YssException {
		return "";
	}

	public String getTreeViewData2() throws YssException {
		return "";
	}

	public String getTreeViewData3() throws YssException {
		return "";
	}

	/**
	 * 查询数据
	 * 
	 * @return String
	 * @throws YssException
	 */
	public String getListViewData1() throws YssException {
		String sqlStr = "";
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		ResultSet rs = null;
		String sVocStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();

		try {
			sHeader = getListView1Headers();
			if (this.filterType != null&&this.filterType.optionsTrade.getIsOnlyColumn().equals("1")&&!(pub.isBrown())) {
				//modified by ysh 20111025 story 1285
			// ----edit by songjie 2010.08.04 添加了词汇，否则点击新建的时候，由于取不到词汇，就会报错----//
			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);
			sVocStr = vocabulary.getVoc(YssCons.YSS_CONTRACT_INVESTTYPE);
			// ----edit by songjie 2010.08.04 添加了词汇，否则点击新建的时候，由于取不到词汇，就会报错----//
			// modify by wangzuochun 2010.08.07 MS01549 点击期权交易菜单后点击新建，保存新建的数据时报错
			// QDV4赢时胜(上海开发部)2010年08月04日02_B
				// fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A 20100708
				// 优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
				
				return sHeader + "\r\f" + sShowDataStr + "\r\f"
						+ sAllDataStr
						+ "\r\f"
						+
						// edit by songjie 2010.08.04
						// 添加了词汇，否则点击新建的时候，由于取不到词汇，就会报错
						this.getListView1ShowCols() + "\r\f"
						+ yssPageInationBean.buildRowStr() + "\r\fvoc"
						+ sVocStr;// QDV4赢时胜上海2010年03月16日06_B MS00884 by
									// xuqiji
				// --------------------------------------end
				// MS01310--------------------------------------------------------
			}
			// ---------------------MS01549-------------------//
			sqlStr = "select " +
					/**shashijie 2011-09-01 STORY 1488 */
					" (case when a.foffsetflag = 'set' then '开仓' when a.foffsetflag = 'off' then "+
					" '平仓' else '不指定' end) as foffsetflag ," +
					" a.FNum" +
					" ,a.FSecurityCode" +
					" ,a.FPortCode "+
					" ,a.FBrokerCode "+
					" ,a.FInvMgrCode "+
					" ,a.FTradeTypeCode "+
					" ,a.FBegBailAcctCode "+
					" ,a.FChageBailAcctCode "+
					" ,a.FBargainDate "+
					" ,a.FSettleDate "+
					" ,a.FTradeAmount "+
					" ,a.FTradePrice "+
					" ,a.FBaseCuryRate "+
					" ,a.FPortCuryRate "+
					" ,a.FTradeMoney "+
					" ,a.FBegBailMoney "+
					" ,a.FSettleMoney "+
					" ,a.FInvestTactics "+
					" ,a.FFeeCode1 "+
					" ,a.FTradeFee1 "+
					" ,a.FFeeCode2 "+
					" ,a.FTradeFee2 "+
					" ,a.FFeeCode3 "+
					" ,a.FTradeFee3 "+
					" ,a.FFeeCode4 "+
					" ,a.FTradeFee4 "+
					" ,a.FFeeCode5 "+
					" ,a.FTradeFee5 "+
					" ,a.FFeeCode6 "+
					" ,a.FTradeFee6 "+
					" ,a.FFeeCode7 "+
					" ,a.FTradeFee7 "+
					" ,a.FFeeCode8 "+
					" ,a.FTradeFee8 "+
					" ,a.FDesc "+
					" ,a.FSettleType "+
					" ,a.FSettleState "+
					" ,a.FCheckState "+
					" ,a.FCreator "+
					" ,a.FCreateTime "+
					" ,a.FCheckUser "+
					" ,a.FCheckTime "+
					/**end*/
					" ,b.FUserName as FCreatorName,c.FUserName as FCheckUserName"
					+ // 创建人，核查人
					",d.FSecurityName as FSecurityName" + // 交易证券
					",d1.FTradeTypeName as FTradeTypeName" + // 交易方式
					",d2.FPortName as FPortName" + // 投资组合
					",d3.FBrokerName as FBrokerName" + // 交易券商
					",d4.FInvMgrName as FInvMgrName" + // 投资经理
					",d5.FCashAccName as FBegBailAcctName" + // 初始保证金帐户
					",d6.FCashAccName as FChageBailAcctName" + // 变动保证金帐户
					",e.FVocName as FInvestTacticsName" + // 投资策略 拉
					" from "
					+ pub.yssGetTableName("TB_DATA_OPTIONSTRADE")
					+ " a"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ // /这个保留
					" left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
					+ // /这个保留
					" left join (select FSecurityCode,FSecurityName,FStartDate,FHandAmount,FFactor "
					+ // 交易证券
					" from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1) d on a.FSecurityCode = d.FSecurityCode "
					+

					" left join (select FTradeTypeCode, FTradeTypeName "
					+ // 交易方式
					" from Tb_Base_TradeType "
					+ " where FCheckState = 1) d1 on a.FTradeTypeCode = d1.FTradeTypeCode "
					+

					// ------ modify by wangzuochun 2010.07.16 MS01449
					// 组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍
					// QDV4赢时胜(测试)2010年7月15日01_B
					// ----------------------------------------------------------------------------------------------------
					" left join ("
					+ // edit by songjie 2011.03.15 不以最大的启用日期查询数据
					// ----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
					// pub.yssGetTableName("Tb_Para_Portfolio") +
					// " where FStartDate <= " + dbl.sqlDate(new
					// java.util.Date()) +
					// ----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
					" select FPortCode, FPortName, FStartDate, FPortCury from "
					+ // edit by songjie 2011.03.15 不以最大的启用日期查询数据
					pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1) d2 on a.FPortCode = d2.FPortCode "
					+ // edit by songjie 2011.03.15 不以最大的启用日期查询数据
					// -------------------------------------------- MS01449
					// -------------------------------------------//

					" left join (select FBrokerCode, FBrokerName, FStartDate "
					+ // 交易券商
					" from "
					+ pub.yssGetTableName("Tb_Para_Broker")
					+
					// ----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
					" where "
					+ " FCheckState = 1) d3 on a.FBrokerCode = d3.FBrokerCode "
					+
					// ----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
					// edited by zhouxiang 2010.7.19 MS01450
					" left join (select m.FInvMgrCode, m.FInvMgrName, m.FStartDate "
					+ // 投资经理
					" from "
					+ pub.yssGetTableName("Tb_Para_InvestManager m")
					+
					// ----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
					// " join (select finvmgrcode, max(fstartdate) as fstartdate "+
					// " from " + pub.yssGetTableName("Tb_Para_InvestManager ")+
					// " group by finvmgrcode) n on m.finvmgrcode = n.finvmgrcode"+
					// " and m.fstartdate = n.fstartdate"+
					// ----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
					// ----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
					" where "
					+ " m.FCheckState = 1) d4 on a.FInvMgrCode = d4.FInvMgrCode "
					+
					// ----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
					// ----------------end-----------

					" left join (select FCashAccCode, FCashAccName, FStartDate "
					+ // 初始保证金帐户
					" from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+
					// ----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
					" where "
					+ " FCheckState = 1) d5 on a.FBegBailAcctCode = d5.FCashAccCode "
					+
					// ----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
					" left join (select FCashAccCode, FCashAccName, FStartDate "
					+ // 变动保证金帐户
					" from  "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+
					// ----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
					" where "
					+ " FCheckState = 1) d6 on a.FChageBailAcctCode = d6.FCashAccCode "
					+
					// ----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
					" left join Tb_Fun_Vocabulary e on "
					+ "a.FInvestTactics"
					+ // 投资策略
					" =e.FVocCode and "
					+ " e.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_CONTRACT_INVESTTYPE)
					+

					buildFilterSql()
					+ " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(sqlStr);
			yssPageInationBean.setsTableName("OPTIONSTRADE");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);
				setResultSetAttr(rs); // 建一个方法
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK); // "/f/f"
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f"
					+ yssPageInationBean.buildRowStr() + "\r\fvoc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B
																				// MS00884
																				// by
																				// xuqiji
		} catch (Exception e) {
			throw new YssException("获取期权信息出错", e); // /改成你的名字
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(dbl.getProcStmt());
		}
	}

	public String getListViewData2() throws YssException {
		return "";
	}

	public String getListViewData3() throws YssException {
		return "";
	}

	public String getListViewData4() throws YssException {
		return "";
	}

	public String getBeforeEditData() throws YssException {
		return "";
	}

	/**
	 * 解析前台传来的字符串
	 * 
	 * @param sRowStr
	 *            String
	 * @throws YssException
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		String sTmpStr = "";
		try {
			if (sRowStr.trim().length() == 0)
				return;
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
			} else {
				sTmpStr = sRowStr;
			}
			this.optionsTrade.setRecycled(sRowStr); // 把未解析的字符串先赋给sRecycled
			reqAry = sTmpStr.split("\t");
			this.optionsTrade.setNum(reqAry[0]);
			this.optionsTrade.setSecurityCode(reqAry[1]);
			this.optionsTrade.setTradeTypeCode(reqAry[2]);
			// 2010.04.21 蒋锦 添加 开平标志 南方期权业务
			// MS01134 增加股票期权和股指期权业务
			this.optionsTrade.setOffSetFlag(reqAry[3]);
			this.optionsTrade.setPortCode(reqAry[4]);
			this.optionsTrade.setBrokerCode(reqAry[5]);
			this.optionsTrade.setInvMgrCode(reqAry[6]);

			this.optionsTrade.setBegBailAcctCode(reqAry[7]);
			this.optionsTrade.setChageBailAcctCode(reqAry[8]);
			this.optionsTrade.setInvestTastic(reqAry[9]);

			this.optionsTrade.setBargainDate(reqAry[10]);
			this.optionsTrade.setSettleDate(reqAry[11]);

			if (YssFun.isNumeric(reqAry[12])) {
				this.optionsTrade
						.setTradeAmount(Double.parseDouble(reqAry[12]));
			}
			if (YssFun.isNumeric(reqAry[13])) {
				this.optionsTrade.setTradePrice(Double.parseDouble(reqAry[13]));
			}

			if (YssFun.isNumeric(reqAry[14])) {
				this.optionsTrade.setBaseCuryRate(Double
						.parseDouble(reqAry[14]));
			}

			if (YssFun.isNumeric(reqAry[15])) {
				this.optionsTrade.setPortCuryRate(Double
						.parseDouble(reqAry[15]));
			}

			if (YssFun.isNumeric(reqAry[16])) {
				this.optionsTrade.setTradeMoney(Double.parseDouble(reqAry[16]));
			}

			if (YssFun.isNumeric(reqAry[17])) {
				this.optionsTrade.setBegBailMoney(Double
						.parseDouble(reqAry[17]));
			}
			if (YssFun.isNumeric(reqAry[18])) {
				this.optionsTrade
						.setSettleMoney(Double.parseDouble(reqAry[18]));
			}

			this.optionsTrade.setDesc(reqAry[19]);

			this.checkStateId = YssFun.toInt(reqAry[20]);

			this.optionsTrade.setOldNum(reqAry[21]);

			this.optionsTrade.setFees(reqAry[22].replaceAll("~", "\t"));

			this.optionsTrade.setOldBargainDate(reqAry[23]);

			if (YssFun.isNumeric(reqAry[24])) {
				this.optionsTrade.setSettleType(Integer.parseInt(reqAry[24]));
			}
			if (YssFun.isNumeric(reqAry[25])) {
				this.optionsTrade.setSettleState(Integer.parseInt(reqAry[25]));
			}
			this.optionsTrade.setIsOnlyColumn(reqAry[26]); // add by wangzuochun
															// 2010.08.07
															// MS01549
															// 点击期权交易菜单后点击新建，保存新建的数据时报错
															// QDV4赢时胜(上海开发部)2010年08月04日02_B
			super.parseRecLog();
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filterType == null) {
					this.filterType = new OptionsTradeAdmin();
					this.filterType.setYssPub(pub);
				}
				this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
			}
		} catch (Exception e) {
			throw new YssException("解析期权交易数据信息出错", e);
		}

	}

	/**
	 * 拼接字符串
	 * 
	 * @return String
	 * @throws YssException
	 */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.optionsTrade.getNum()).append("\t");
		buf.append(this.optionsTrade.getSecurityCode()).append("\t");
		buf.append(this.optionsTrade.getTradeTypeCode()).append("\t");
		// 2010.04.21 蒋锦 添加 开平标志 南方期权业务
		// MS01134 增加股票期权和股指期权业务
		buf.append(this.optionsTrade.getOffSetFlag()).append("\t");
		buf.append(this.optionsTrade.getPortCode()).append("\t");
		buf.append(this.optionsTrade.getBrokerCode()).append("\t");// 交易券商
		buf.append(this.optionsTrade.getInvMgrCode()).append("\t");// 投资经理
		buf.append(this.optionsTrade.getBegBailAcctCode()).append("\t");
		buf.append(this.optionsTrade.getChageBailAcctCode()).append("\t");
		buf.append(this.optionsTrade.getInvestTastic()).append("\t");// 投资策略
		buf.append(this.optionsTrade.getBargainDate()).append("\t");// 日期
		buf.append(this.optionsTrade.getSettleDate()).append("\t");
		buf.append(this.optionsTrade.getTradeAmount()).append("\t");
		buf.append(this.optionsTrade.getTradePrice()).append("\t");
		buf.append(this.optionsTrade.getBaseCuryRate()).append("\t");
		buf.append(this.optionsTrade.getPortCuryRate()).append("\t");
		buf.append(this.optionsTrade.getTradeMoney()).append("\t");

		buf.append(this.optionsTrade.getBegBailMoney()).append("\t");
		buf.append(this.optionsTrade.getSettleMoney()).append("\t");

		buf.append(this.optionsTrade.getDesc()).append("\t");

		buf.append(this.optionsTrade.getSecurityName()).append("\t");
		buf.append(this.optionsTrade.getTradeTypeName()).append("\t");
		buf.append(this.optionsTrade.getPortName()).append("\t");
		buf.append(this.optionsTrade.getBrokerName()).append("\t");// 交易券商
		buf.append(this.optionsTrade.getInvMgrName()).append("\t");// 投资经理名

		buf.append(this.optionsTrade.getBegBailAcctName()).append("\t");
		buf.append(this.optionsTrade.getChageBailAcctName()).append("\t");

		buf.append(this.optionsTrade.getFees()).append("\t");

		buf.append(super.buildRecLog());
		return buf.toString();

	}

	public String getOperValue(String sType) throws YssException {
		if (sType != null && sType.indexOf("getPC_BZJ") != -1) { // 取期权卖出保证金
			return getPCBZJ(optionsTrade, sType) + "";
		} else if (sType != null && sType.equalsIgnoreCase("calcPCSY")) { // 计算期权卖出收益
			return getPCSY(optionsTrade) + "";
		} else if (sType != null && sType.equalsIgnoreCase("getEXERCISE_BZJ")) {// 取期权行权保证金
			return getExerciseBZJ(optionsTrade) + "";
		} else if (sType != null && sType.equalsIgnoreCase("exerciseSY")) {// 计算期权行权收益
			return getExerciseSY(optionsTrade) + "";
		} else if (sType != null && sType.equalsIgnoreCase("useTradeNum")) { // 获取使用交易编号
			return getTradeNum(false);
		}
		return "";

	}

	/**
	 * getExerciseSY 计算期权行权收益
	 * 
	 * @param optionsTrade
	 *            OptionsTradeBean
	 * @return double
	 */
	private double getExerciseSY(OptionsTradeBean optionsTrade) {
		return 0.0;
	}

	/**
	 * getExercise 计算期权行权保证金
	 * 
	 * @param optionsTrade
	 *            OptionsTradeBean
	 * @return double
	 */
	private double getExerciseBZJ(OptionsTradeBean optionsTrade) {
		return 0.0;
	}

	/**
	 * getPCSY 计算期权卖出收益
	 * 
	 * @param optionsTrade
	 *            OptionsTradeBean
	 * @return String
	 */
	private double getPCSY(OptionsTradeBean optionsTrade) {
		return 0;
	}

	/**
	 * getPCBZJ 计算卖出保证金，统计出剩余库存包括：今天买入和卖出 1.比例 卖出数量大于昨日库存数量： 保证金=（卖出数量-库存数量） *
	 * 成交价格 * 放大倍数 * 保证金比例 每手固定 卖出数量大于昨日库存数量：保证金=（卖出数量-库存数量）*每首固定保证金金额 比例
	 * 卖出数量小于后等于昨日库存数量： 保证金=0 每手固定 卖出数量小于后等于昨日库存数量：保证金=0 xuqiji 20100429 MS01134
	 * 在现有的程序版本中增加指数期权及股票期权业务
	 * 
	 * @param optionsTrade
	 *            OptionsTradeBean
	 * @return String
	 */
	private double getPCBZJ(OptionsTradeBean optionsTrade, String sType)
			throws YssException {
		double dBZJ = 0;// 保证金额
		String[] sOprions = null;
		String strSql = "";
		ResultSet rs = null;
		try {
			sOprions = sType.split("\f\f");
			strSql = createSQLForGetYesterdayStock(YssFun.toDate(optionsTrade
					.getBargainDate()), optionsTrade.getPortCode(),
					optionsTrade.getSecurityCode(), optionsTrade
							.getInvMgrCode(), optionsTrade.getBrokerCode());

			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				if (rs.getDouble("FStorageAmount") == 0
						&& (optionsTrade.getTradeTypeCode().equalsIgnoreCase(
								"34FP") || optionsTrade.getTradeTypeCode()
								.equalsIgnoreCase("32FP"))) {
					throw new YssException("期权【"
							+ optionsTrade.getSecurityCode() + "】库存不足，请检查期权库存！");
				}
				double dbCloseAmount = optionsTrade.getTradeAmount();
				if (rs.getDouble("FStorageAmount") == 0) {
					if (rs.getDouble("FChangeMoney") != 0
							&& sOprions[3].equalsIgnoreCase("Fix")) {
						dBZJ = YssD.mul(dbCloseAmount, rs
								.getDouble("FChangeMoney")); // 保证金=数量*调整保证金每首固定
					} else {
						if (sOprions[3].equalsIgnoreCase("Fix")) {
							// 2.每手固定 卖出数量大于昨日库存数量：保证金=卖出数量*每首固定保证金金额
							dBZJ = YssD.mul(dbCloseAmount, YssFun
									.toDouble(sOprions[1]));
						} else {
							// 1.比例 卖出数量大于昨日库存数量： 保证金=卖出数量 * 成交价格 * 放大倍数 * 保证金比例
							dBZJ = YssD.mul(YssD.mul(YssD.mul(dbCloseAmount,
									optionsTrade.getTradePrice()), YssFun
									.toDouble(sOprions[4])), YssFun
									.toDouble(sOprions[2]));
						}
					}
					return dBZJ;
				}
				if (optionsTrade.getTradeTypeCode().equalsIgnoreCase("34FP")
						|| optionsTrade.getTradeTypeCode().equalsIgnoreCase(
								"32FP")) {
					if (rs.getDouble("FStorageAmount") < 0) {// 库存数量 < 0
						if (rs.getDouble("FChangeMoney") != 0
								&& sOprions[3].equalsIgnoreCase("Fix")) {
							dBZJ = YssD.mul(dbCloseAmount, rs
									.getDouble("FChangeMoney")); // 保证金=数量*调整保证金每首固定
						} else {
							if (sOprions[3].equalsIgnoreCase("Fix")) {
								// 2.每手固定 卖出数量大于昨日库存数量：保证金=卖出数量*每首固定保证金金额
								dBZJ = YssD.mul(dbCloseAmount, YssFun
										.toDouble(sOprions[1]));
							} else {
								// 1.比例 卖出数量大于昨日库存数量： 保证金=卖出数量 * 成交价格 * 放大倍数 *
								// 保证金比例
								dBZJ = YssD.mul(YssD.mul(YssD.mul(
										dbCloseAmount, optionsTrade
												.getTradePrice()), YssFun
										.toDouble(sOprions[4])), YssFun
										.toDouble(sOprions[2]));
							}
						}
					} else {
						dBZJ = 0;
						dbCloseAmount = 0;
					}
				} else {
					if (rs.getDouble("FStorageAmount") < dbCloseAmount) {// 卖出数量大于库存数量
						if (rs.getDouble("FChangeMoney") != 0
								&& sOprions[3].equalsIgnoreCase("Fix")) {
							dBZJ = YssD.mul(dbCloseAmount, rs
									.getDouble("FChangeMoney")); // 保证金=数量*调整保证金每首固定
						} else {
							if (sOprions[3].equalsIgnoreCase("Fix")) {
								// 2.每手固定 卖出数量大于昨日库存数量：保证金=卖出数量*每首固定保证金金额
								dBZJ = YssD.mul(dbCloseAmount, YssFun
										.toDouble(sOprions[1]));
							} else {
								// 1.比例 卖出数量大于昨日库存数量： 保证金=卖出数量 * 成交价格 * 放大倍数 *
								// 保证金比例
								dBZJ = YssD.mul(YssD.mul(YssD.mul(
										dbCloseAmount, optionsTrade
												.getTradePrice()), YssFun
										.toDouble(sOprions[4])), YssFun
										.toDouble(sOprions[2]));
							}
						}
					} else {
						dBZJ = 0;
						dbCloseAmount = 0;
					}
				}

			}
		} catch (Exception e) {
			throw new YssException("计算期权保证金出错！\r\t", e);
		}
		return dBZJ;
	}

	/**
	 * createSQLForGetTheDayTrade 获取今天买入和卖出期权的数量
	 * 
	 * @param dWorkDate
	 *            Date 交易日期
	 * @param sPortCode
	 *            String 组合代码
	 * @param sSecurityCode
	 *            String 期权代码
	 * @return String
	 */
	private String createSQLForGetTheDayTrade(Date dWorkDate, String sPortCode,
			String sSecurityCode) throws YssException {
		StringBuffer sqlBuf = new StringBuffer();
		try {
			sqlBuf
					.append("SELECT FNum, FSecurityCode, FPortCode, FTradeAmount, FBegBailMoney, FTradeTypeCode");
			sqlBuf.append(" FROM "
					+ pub.yssGetTableName("TB_Data_OptionsTrade"));
			sqlBuf.append(" WHERE FCheckState = 1");
			sqlBuf.append(" AND FBargainDate = " + dbl.sqlDate(dWorkDate));
			sqlBuf.append(" AND FPortCode = " + dbl.sqlString(sPortCode));
			sqlBuf.append(" AND FSecurityCode = "
					+ dbl.sqlString(sSecurityCode));
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		return sqlBuf.toString();
	}

	/**
	 * createSQLForGetYesterdayStock 获取昨日期权库存 xuqiji 20100429 MS01134
	 * 在现有的程序版本中增加指数期权及股票期权业务
	 * 
	 * @param dWorkDate
	 *            Date 计算日期
	 * @param sPortCode
	 *            String 组合代码
	 * @param sSecurityCode
	 *            String 期权代码
	 * @param sInvMgrCode
	 *            String 投资经理
	 * @param sBrokerCode
	 *            String 券商
	 * @return String
	 */
	private String createSQLForGetYesterdayStock(Date dWorkDate,
			String sPortCode, String sSecurityCode, String sInvMgrCode,
			String sBrokerCode) throws YssException {
		StringBuffer sqlBuf = new StringBuffer();
		String strYearMonth = "";
		boolean analy1 = false; // 分析代码1
		boolean analy2 = false; // 分析代码2
		try {
			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Stock"); // 判断证券库存表是否有分析代码
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Stock");

			strYearMonth = YssFun.left(
					YssFun.formatDate(dWorkDate, "yyyymmdd"), 4)
					+ "00";
			sqlBuf.append(" SELECT  * ");
			sqlBuf.append(" FROM ").append(
					pub.yssGetTableName("Tb_stock_security"));
			sqlBuf.append(" a left join (select e2.* from  ");
			sqlBuf
					.append(" (select max(FChangeDate) as FChangeDate,fsecuritycode, FportCode from ");
			sqlBuf.append(pub.yssGetTableName("tb_data_futurebailchange"));// 保证金调整表
			sqlBuf.append(" where FCheckState = 1 and FChangeDate <=").append(
					dbl.sqlDate(dWorkDate));
			sqlBuf.append(" and FportCode =").append(dbl.sqlString(sPortCode));
			sqlBuf.append(" and FSecurityCode =").append(
					dbl.sqlString(sSecurityCode));
			sqlBuf.append(" group by fsecuritycode, FportCode) e1");
			sqlBuf.append(" join (select * from ");
			sqlBuf.append(pub.yssGetTableName("tb_data_futurebailchange"));// 保证金调整表
			sqlBuf.append(" where FCheckState = 1 ");
			sqlBuf.append(" and FportCode =").append(dbl.sqlString(sPortCode));
			sqlBuf.append(" and FSecurityCode =").append(
					dbl.sqlString(sSecurityCode));
			sqlBuf.append(" ) e2 on e1.FChangeDate =e2.FChangeDate");
			sqlBuf
					.append(" and e1.fsecuritycode =e2.fsecuritycode and e1.FportCode = e2.fportcode");
			sqlBuf.append(" ) b on a.fsecuritycode =b.fsecuritycode");

			sqlBuf.append(" WHERE a.FCheckState =1 and a.FstorageDate = ")
					.append(dbl.sqlDate(YssFun.addDay(dWorkDate, -1)));
			sqlBuf.append(" AND a.FYearMonth <> ").append(
					dbl.sqlString(strYearMonth));
			sqlBuf.append(" AND a.FSecurityCode = ").append(
					dbl.sqlString(sSecurityCode));
			sqlBuf.append(" AND a.FPortCode = ").append(
					dbl.sqlString(sPortCode));
			sqlBuf.append(analy1 == true ? " and a.FAnalysisCode1 = "
					+ dbl.sqlString(sInvMgrCode) : "");
			sqlBuf.append(analy2 == true ? " and a.FAnalysisCode2 = "
					+ dbl.sqlString(sBrokerCode) : "");

		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		return sqlBuf.toString();
	}

	/**
	 * 获取成交编号，自动生成,格式 T+yyyyMMdd+000000 为了避免用户同时操作时造成编号的同时使用，采用编号池原理，
	 * 每个成交编号从编号池中取，如果用户取消了操作，将成交编号退回到编号池 编号池原理：从数据库总找到下一个编号，然后再生成4个编号，将编号保存到编号池
	 * 每次从编号池中取最小的编号，如果编号池中为空，则重新创建编号。
	 * 
	 * @param bReadOnly
	 *            boolean 标识该编号是只读的，还是可以作为成交编号实际使用的。
	 * @return String 成交编号
	 * @throws YssException
	 */
	public String getTradeNum(boolean bReadOnly) throws YssException {
		if (tradeNumPool == null) {
			tradeNumPool = new ArrayList();
		}
		String strNumDate = YssFun.formatDatetime(new java.util.Date())
				.substring(0, 8); // 格式化过的日期 yyyyMMdd
		// 如果编号池中存在非当日的编号，将非当日的编号删除
		for (int i = 0; i < tradeNumPool.size(); i++) {
			if (!tradeNumPool.get(i).toString().substring(1, 9).equals(
					strNumDate)) {
				tradeNumPool.remove(i);
			}
		}
		// 判断交易编号池中是否还有编号存在，如果没有则获取新的编号5个
		if (tradeNumPool.size() == 0) {
			int tmpNum = 0;
			String num = dbFun.getNextInnerCode(pub
					.yssGetTableName("TB_Data_OptionsTrade"), dbl.sqlRight(
					"FNUM", 6), "000000", " where FNum like 'P" + strNumDate
					+ "%'", 10); // 编号
			tmpNum = YssFun.toInt(num);
			for (int i = 0; i < 5; i++) {
				tradeNumPool.add("P" + strNumDate
						+ YssFun.formatNumber(tmpNum, "000000"));
				tmpNum += 10;
			}
		}
		// 将交易编号池中最小的数据返回
		String tmpTradeNum = tradeNumPool.get(0).toString();
		int minFlag = 0;
		int minNum = YssFun.toInt(tmpTradeNum.substring(
				tmpTradeNum.length() - 6, tmpTradeNum.length()));
		for (int i = 0; i < tradeNumPool.size(); i++) {
			tmpTradeNum = tradeNumPool.get(i).toString();
			if (minNum > YssFun.toInt(tmpTradeNum.substring(tmpTradeNum
					.length() - 6, tmpTradeNum.length()))) {
				minNum = YssFun.toInt(tmpTradeNum.substring(tmpTradeNum
						.length() - 6, tmpTradeNum.length()));
				minFlag = i;
			}
		}
		tmpTradeNum = tradeNumPool.get(minFlag).toString(); // 获取编号池中最小的编号
		if (!bReadOnly) { // 如果获取的不是只读，要进行实际使用时，从编号池中删除
			tradeNumPool.remove(minFlag); // 将编号从编号池中删除
		}
		return tmpTradeNum;
	}

	/**
	 * 拼接SQL语句
	 * 
	 * @return String
	 * @throws YssException
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		 if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
				return " where 1=1";
		if (this.filterType != null) {
			sResult = sResult + "where 1=1";

			if ((this.filterType.optionsTrade.getNum()).length() != 0) {// 交易编号
				sResult = sResult
						+ " and a.FNum like '"
						+ filterType.optionsTrade.getNum()
								.replaceAll("'", "''") + "%'";
			}
			// 2010.04.21 蒋锦 添加 开平标志 南方期权业务
			// MS01134 增加股票期权和股指期权业务
			if (this.filterType.optionsTrade.getOffSetFlag().length() != 0
					/**shashijie 2011-08-24 STORY 1488 若选择的是"全部"则不添加查询条件*/
					&& !this.filterType.optionsTrade.getOffSetFlag().equalsIgnoreCase("ALL")) {
					/**end*/
				sResult = sResult
						+ " and a.FOffSetFlag Like '"
						+ filterType.optionsTrade.getOffSetFlag().replaceAll(
								"'", "''") + "%'";
			}
			if (this.filterType.optionsTrade.getSecurityCode().length() != 0) {// 交易证券
				sResult = sResult
						+ " and a.FSecurityCode like '"
						+ filterType.optionsTrade.getSecurityCode().replaceAll(
								"'", "''") + "'";
			}
			if (this.filterType.optionsTrade.getTradeTypeCode().length() != 0) {// 交易方式
				sResult = sResult
						+ " and a.FTradeTypeCode = '"
						+ filterType.optionsTrade.getTradeTypeCode()
								.replaceAll("'", "''") + "'";
			}
			if (this.filterType.optionsTrade.getInvMgrCode().length() != 0) {// 投资经理
				sResult = sResult
						+ " and a.FInvMgrCode like '"
						+ filterType.optionsTrade.getInvMgrCode().replaceAll(
								"'", "''") + "%'";
			}
			if (this.filterType.optionsTrade.getBargainDate().length() != 0 && // 成交日期
					!this.filterType.optionsTrade.getBargainDate().equals(
							"9998-12-31")) {
				sResult = sResult
						+ " and a.FBargainDate = "
						+ dbl.sqlDate(YssFun
								.toDate(this.filterType.optionsTrade
										.getBargainDate()));
			}
			if (this.filterType.optionsTrade.getSettleDate().length() != 0 && // 结算日期
					!this.filterType.optionsTrade.getSettleDate().equals(
							"9998-12-31")) {
				sResult = sResult
						+ " and a.FSettleDate = "
						+ dbl.sqlDate(YssFun
								.toDate(this.filterType.optionsTrade
										.getSettleDate()));
			}
			if (this.filterType.optionsTrade.getPortCode().length() != 0) {// 投资组合
				sResult = sResult
						+ " and a.FPortCode = '"
						+ filterType.optionsTrade.getPortCode().replaceAll("'",
								"''") + "'";
			}
			if (this.filterType.optionsTrade.getBrokerCode().length() != 0) { // 交易券商
				sResult = sResult
						+ " and a.FBrokerCode = '"
						+ filterType.optionsTrade.getBrokerCode().replaceAll(
								"'", "''") + "'";
			}
			if (this.filterType.optionsTrade.getBegBailAcctCode().length() != 0) { // 初始保证金帐户
				sResult = sResult
						+ " and a.FBegBailAcctCode = '"
						+ filterType.optionsTrade.getBegBailAcctCode()
								.replaceAll("'", "''") + "'";
			}
			if (this.filterType.optionsTrade.getChageBailAcctCode().length() != 0) { // 变动保证金帐户
				sResult = sResult
						+ " and a.FChageBailAcctCode = '"
						+ filterType.optionsTrade.getChageBailAcctCode()
								.replaceAll("'", "''") + "'";
			}
			if (this.filterType.optionsTrade.getInvestTastic().length() != 0
					&& !this.filterType.optionsTrade.getInvestTastic().equals(
							"99")) { // 投资策略
				sResult = sResult
						+ " and a.FInvestTactics = '"
						+ filterType.optionsTrade.getInvestTastic().replaceAll(
								"'", "''") + "'";
			}
		}
		return sResult;
	}

	public String getTreeViewGroupData1() throws YssException {
		return "";
	}

	public String getTreeViewGroupData2() throws YssException {
		return "";
	}

	public String getTreeViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData1() throws YssException {
		return "";
	}

	public String getListViewGroupData2() throws YssException {
		return "";
	}

	public String getListViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData4() throws YssException {
		return "";
	}

	public String getListViewGroupData5() throws YssException {
		return "";
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException, SQLException {
		this.optionsTrade.setNum(rs.getString("FNum"));
		// 2010.04.21 蒋锦 添加 开平标志 南方期权业务
		// MS01134 增加股票期权和股指期权业务
		this.optionsTrade.setOffSetFlag(rs.getString("FOffSetFlag"));
		this.optionsTrade.setSecurityCode(rs.getString("FSecurityCode"));
		this.optionsTrade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
		this.optionsTrade.setPortCode(rs.getString("FPortCode"));
		this.optionsTrade.setBrokerCode(rs.getString("FBrokerCode"));
		this.optionsTrade.setInvMgrCode(rs.getString("FInvMgrCode"));
		this.optionsTrade.setBegBailAcctCode(rs.getString("FBegBailAcctCode"));
		this.optionsTrade.setChageBailAcctCode(rs
				.getString("FChageBailAcctCode"));
		this.optionsTrade.setInvestTastic(rs.getString("FInvestTactics"));// 投资策略
		this.optionsTrade.setBargainDate(rs.getString("FBargainDate"));// 成交日期
		this.optionsTrade.setSettleDate(rs.getString("FSettleDate"));
		this.optionsTrade.setTradeAmount(rs.getDouble("FTradeAmount"));
		this.optionsTrade.setTradePrice(rs.getDouble("FTradePrice"));
		this.optionsTrade.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
		this.optionsTrade.setPortCuryRate(rs.getDouble("FPortCuryRate"));
		this.optionsTrade.setTradeMoney(rs.getDouble("FTradeMoney"));
		this.optionsTrade.setBegBailMoney(rs.getDouble("FBegBailMoney"));
		this.optionsTrade.setSettleMoney(rs.getDouble("FSettleMoney"));
		this.loadFees(rs);
		this.optionsTrade.setDesc(rs.getString("FDesc"));
		this.optionsTrade.setSecurityName(rs.getString("FSecurityName"));
		this.optionsTrade.setTradeTypeName(rs.getString("FTradeTypeName"));
		this.optionsTrade.setPortName(rs.getString("FPortName"));
		this.optionsTrade
				.setBrokerName(rs.getString("FBrokerName") == null ? "" : rs
						.getString("FBrokerName"));// 交易券商
		this.optionsTrade
				.setInvMgrName(rs.getString("FInvMgrName") == null ? "" : rs
						.getString("FInvMgrName"));// 投资经理
		this.optionsTrade.setBegBailAcctName(rs.getString("FBegBailAcctName"));
		this.optionsTrade.setChageBailAcctName(rs
				.getString("FChageBailAcctName"));

		super.setRecLog(rs);
	}

	public void loadFees(ResultSet rs) throws SQLException, YssException {
		String sName = "";
		double dFeeMoney = 0;
		double dTotalFee = 0;
		StringBuffer buf = new StringBuffer();
		FeeBean fee = new FeeBean();
		fee.setYssPub(pub);

		for (int i = 1; i <= 8; i++) {
			if (rs.getString("FFeeCode" + i) != null
					&& rs.getString("FFeeCode" + i).trim().length() > 0) {
				fee.setFeeCode(rs.getString("FFeeCode" + i));
				fee.getSetting();
				// ------ add by wangzuochun 2010.09.11 MS01708 交易结算未结算中进行结算时会报错
				// QDV4建行2010年09月08日01_B
				// ------ 根据交易子表中的费用代码去查费用设置中的费用，若费用不存在，则跳过此次循环；
				if (fee.getFeeCode() == null) {
					continue;
				}
				// ----------MS01708-----------//
				sName = fee.getFeeName();
				if (rs.getString("FTradeFee" + i) != null) {
					dFeeMoney = rs.getDouble("FTradeFee" + i);
				}
				dTotalFee = YssD.add(dTotalFee, dFeeMoney);
				buf.append(rs.getString("FFeeCode" + i)).append("\n");
				buf.append(sName).append("\n");
				buf.append(dFeeMoney).append("\n");
				buf.append(fee.buildRowStr().replaceAll("\t", "~")).append(
						"\f\n");
			}
		}
		if (buf.toString().length() > 2) {
			buf.append("total").append("\n");
			buf.append("Total: ").append("\n");
			buf.append(dTotalFee).append("\n");
			fee.setAccountingWay("0"); // 不计入成本
			buf.append(fee.buildRowStr().replaceAll("\t", "~"));
			this.optionsTrade.setFees(buf.toString());
		} else {
			this.optionsTrade.setFees("");
		}
	}

}
