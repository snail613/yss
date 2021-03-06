package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.commeach.EachRateOper;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class DividendToInvestBean extends BaseDataSettingBean implements
		IDataSetting {
	private String sRecycled = ""; // 保存未解析前的字符串
	private String securityCode = ""; // 证券代码
	private String securityName = ""; // 证券名称
	private String portCode = ""; // 组合代码
	private String portName = ""; // 组合名称
	private String payDate = ""; // 到账日期
	private String businessDate = ""; // 业务日期
	private String tradeNum = ""; // 交易数据编号
	private String confirmAmount = ""; // 确认数量
	private String confirmMoney = ""; // 确认金额
	private String adjustMoney = ""; // 调整金额
	private String price = ""; // 单价
	private String businessStartDate = ""; // 业务日期(开始日期)
	private String businessEndDate = ""; // 业务日期(结束日期)
	private String num = "";// 编号
	private String dealType = "";
	private String curyCode = "";// 币种代码
	private String curyName = "";// 币种名称
	private String accCode = "";// 现金账户代码
	private String accName = "";// 现金账户名称
	private String receiveMoney = "";// 到账金额
	private String inAccType = "";// 入账方式

	private DividendToInvestBean filterType = null;
	private SingleLogOper logOper;
	private String multAuditString = "";
	private String relaInfo = "";
	private String tradeCury = "";// 交易币种
	private String divMoney = "";// 分红金额

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public String getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}

	public String getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}

	public String getConfirmAmount() {
		return confirmAmount;
	}

	public void setConfirmAmount(String confirmAmount) {
		this.confirmAmount = confirmAmount;
	}

	public String getConfirmMoney() {
		return confirmMoney;
	}

	public void setConfirmMoney(String confirmMoney) {
		this.confirmMoney = confirmMoney;
	}

	public String getAdjustMoney() {
		return adjustMoney;
	}

	public void setAdjustMoney(String adjustMoney) {
		this.adjustMoney = adjustMoney;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getBusinessStartDate() {
		return businessStartDate;
	}

	public void setBusinessStartDate(String businessStartDate) {
		this.businessStartDate = businessStartDate;
	}

	public String getBusinessEndDate() {
		return businessEndDate;
	}

	public void setBusinessEndDate(String businessEndDate) {
		this.businessEndDate = businessEndDate;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getDealType() {
		return dealType;
	}

	public String getCuryCode() {
		return curyCode;
	}

	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}

	public String getCuryName() {
		return curyName;
	}

	public void setCuryName(String curyName) {
		this.curyName = curyName;
	}

	public String getAccCode() {
		return accCode;
	}

	public void setAccCode(String accCode) {
		this.accCode = accCode;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getReceiveMoney() {
		return receiveMoney;
	}

	public void setReceiveMoney(String receiveMoney) {
		this.receiveMoney = receiveMoney;
	}

	public String getInAccType() {
		return inAccType;
	}

	public void setInAccType(String inAccType) {
		this.inAccType = inAccType;
	}

	public String getTradeCury() {
		return tradeCury;
	}

	public void setTradeCury(String tradeCury) {
		this.tradeCury = tradeCury;
	}

	public String getDivMoney() {
		return divMoney;
	}

	public void setDivMoney(String divMoney) {
		this.divMoney = divMoney;
	}

	public String addSetting() throws YssException {
		String strNumDate = "";
		StringBuffer bufSql = new StringBuffer();
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = null;
		try {
			conn = dbl.loadConnection();

			conn.setAutoCommit(false);
			bTrans = true;

			strNumDate = YssFun.formatDate(YssFun.toDate(this.businessDate),
					"yyyyMMdd");
			this.num = strNumDate
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("Tb_Data_DividendToInvest"), dbl
							.sqlRight("FNUM", 5), "00000",
							" where FNum like 'T" + strNumDate + "0%'", 1);
			this.num = "T" + this.num;

			bufSql.append(" insert into "
					+ pub.yssGetTableName("Tb_Data_DividendToInvest"));
			bufSql
					.append("(FNum,FTradeNum,FSecurityCode,FPortCode,FCuryCode,FAccCode,FINACCTYPE,FPayDate,FBusinessDate,FConfirmAmount,");
			bufSql
					.append("FConfirmMoney,FAdjustMoney,FRECEIVEMONEY,FPrice,FCheckState,FCreator,FCreateTime) values (");
			bufSql.append(dbl.sqlString(this.num) + ",");
			bufSql.append(dbl.sqlString(this.tradeNum) + ",");
			bufSql.append(dbl.sqlString(this.securityCode) + ",");
			bufSql.append(dbl.sqlString(this.portCode) + ",");
			bufSql.append(dbl.sqlString(this.curyCode) + ",");
			bufSql.append(dbl.sqlString(this.accCode) + ",");
			bufSql.append(dbl.sqlString(this.inAccType) + ",");
			bufSql.append(dbl.sqlDate(this.payDate) + ",");
			bufSql.append(dbl.sqlDate(this.businessDate) + ",");
			bufSql.append(this.confirmAmount + ",");
			bufSql.append(this.confirmMoney + ",");
			bufSql.append(this.adjustMoney + ",");
			bufSql.append(this.receiveMoney + ",");
			bufSql.append(this.price + ",0,");
			bufSql.append(dbl.sqlString(this.creatorCode) + ",");
			bufSql.append(dbl.sqlString(this.creatorTime));
			bufSql.append(")");

			dbl.executeSql(bufSql.toString());

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return "";
		} catch (Exception e) {
			throw new YssException("新增股票分红转投信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub

	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		StringBuffer strSql = null; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					
					strSql = new StringBuffer();

					strSql.append("update "
							+ pub.yssGetTableName("Tb_Data_DividendToInvest"));
					strSql.append(" set FCheckState = " + this.checkStateId
							+ ",");
					strSql.append(" FCheckUser = "
							+ dbl.sqlString(this.checkUserCode) + ",");
					strSql.append(" FCheckTime = "
							+ dbl.sqlString(this.checkTime));
					strSql.append(" where FNum = " + dbl.sqlString(this.num));

					dbl.executeSql(strSql.toString()); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新分红转投审核状态出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		StringBuffer strSql = new StringBuffer();
		try {
			conn.setAutoCommit(false);
			bTrans = true;

			strSql.append("update "+ pub.yssGetTableName("Tb_Data_DividendToInvest"));
			strSql.append(" set FCheckState = 2, FCheckUser = " + this.checkUserCode);
			strSql.append(", FCheckTime = " + dbl.sqlString(this.checkTime));
			strSql.append(" where FNum = " + dbl.sqlString(this.num));

			dbl.executeSql(strSql.toString());
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除股票分红转投信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		StringBuffer strSql = null; // 定义一个放SQL语句的字符串
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		// 获取一个连接
		Connection conn = dbl.loadConnection();
		try {
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
				// 根据规定的符号，把多个sql语句分别放入数组
				arrData = sRecycled.split("\r\n");
				conn.setAutoCommit(false);
				bTrans = true;
				// 循环执行这些删除语句
				for (int i = 0; i < arrData.length; i++) {
					strSql = new StringBuffer();
					
					if (arrData[i].length() == 0) {
						continue;
					}
					
					this.parseRowStr(arrData[i]);
					
					strSql.append(" delete from " + pub.yssGetTableName("Tb_Data_DividendToInvest"));
					strSql.append(" where FNum = " + dbl.sqlString(this.num));

					dbl.executeSql(strSql.toString());
				}
			}
			conn.commit(); // 提交事物
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除分红转投信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "update " + pub.yssGetTableName("Tb_Data_DividendToInvest")
					+ " set FTradeNum = " + dbl.sqlString(this.tradeNum)
					+ " , FPortCode = " + dbl.sqlString(this.portCode)
					+ " , FSecurityCode = " + dbl.sqlString(this.securityCode)
					+ " , FCuryCode = " + dbl.sqlString(this.curyCode)
					+ " , FAccCode = " + dbl.sqlString(this.accCode)
					+ " , FPayDate = " + dbl.sqlDate(this.payDate)
					+ " , FBusinessDate = " + dbl.sqlDate(this.businessDate)
					+ " , FConfirmAmount = " + this.confirmAmount
					+ " , FConfirmMoney =" + this.confirmMoney
					+ " , FAdjustMoney = " + this.adjustMoney
					+ " , FReceiveMoney = " + this.receiveMoney
					+ " , FPrice = " + this.price 
					+ " , FInAccType = " + dbl.sqlString(this.inAccType) + 
					" , fcreator = " + dbl.sqlString(this.creatorCode) + 
					" , fcreatetime = " + dbl.sqlString(this.creatorTime) + 
					" where FNum = " + dbl.sqlString(this.num);
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return "";
		} catch (Exception e) {
			throw new YssException("修改股票分红转投信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();

		buf.append(this.num).append("\t");
		buf.append(this.tradeNum).append("\t");
		buf.append(this.securityCode).append("\t");
		buf.append(this.securityName).append("\t");
		buf.append(this.portCode).append("\t");
		buf.append(this.portName).append("\t");
		buf.append(this.businessDate).append("\t");
		buf.append(this.payDate).append("\t");
		buf.append(this.confirmAmount).append("\t");
		buf.append(this.confirmMoney).append("\t");
		buf.append(this.price).append("\t");
		buf.append(this.adjustMoney).append("\t");
		buf.append(this.curyCode).append("\t");
		buf.append(this.curyName).append("\t");
		buf.append(this.accCode).append("\t");
		buf.append(this.accName).append("\t");
		buf.append(this.receiveMoney).append("\t");
		buf.append(this.inAccType).append("\t");
		buf.append(this.tradeCury).append("\t");
		buf.append(this.divMoney).append("\t");
		buf.append(super.buildRecLog());

		return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		if (sType != null && sType.equals("getTradeInfo")) {
			return getTradeInfo();
		}
		if (sType != null && sType.equals("multDealDividToInv")) {
			if (multAuditString.length() > 0) {
				this.auditMutli(multAuditString);
			}
			return getTradeInfo();
		}
		if (sType != null && sType.indexOf("calAdjustMoney") != -1) {
			return calAdjustMoney();
		}
		if (sType != null && sType.indexOf("getDivAccCode") != -1) {
			return getAccCodeByLink();
		}
		if (sType != null && sType.indexOf("judgeIfMatch") != -1) {
			return judgeIfMatch();
		}
		if (sType != null && sType.indexOf("judgeCury") != -1) {
			return judgeCury();
		}
		return null;
	}
	
	/**
     * 判断到账现金账户币种 和 分红转投界面币种是否相同
     * 不同则给出提示  要求币种一致
	 * @return
	 * @throws YssException
	 */
	private String judgeCury() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try{
			strSql = " select FCuryCode from " + pub.yssGetTableName("Tb_Para_CashAccount") + 
			" where FCashAccCode = " + dbl.sqlString(this.accCode);
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
				if(!this.curyCode.equals(rs.getString("FCuryCode"))){
					return "no";
				}else{
					return "yes";
				}
			}
			return "no";
		}catch(Exception e) {
			throw new YssException("获取交易数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 判断交易编号对应的交易数据的证券代码 和组合代码  是否 
     * 和 录入的 证券代码、组合代码一直  不一致则提示重新设置
	 * @return
	 * @throws YssException
	 */
	private String judgeIfMatch() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = " select * from  " + pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1 and FNum = " + dbl.sqlString(this.tradeNum);
			rs = dbl.openResultSet(strSql);
		    if(rs.next()){
		    	if(!this.portCode.equals(rs.getString("FPortCode")) ||
		    	!this.securityCode.equals(rs.getString("FSecurityCode"))){
		    		return "no";
		    	}else{
		    		return "yes";
		    	}
		    }
			return "";
		} catch (Exception e) {
			throw new YssException("获取交易数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	private String getAccCodeByLink() throws YssException {
		String cashAccCode = "";
		StringBuffer strSql = new StringBuffer();
		ResultSet rs = null; 
		try {
			strSql.append(" select a.* from (select FInvMgrCode, FPortCode, FCatCode, FSubCatCode, ");
			strSql.append(" FBrokerCode, FTradeTypeCode, FExchangeCode, FLinkLevel, FAuxiCashAccCode, ");
			strSql.append(" FCashAccCode, max(FStartDate) as FStartDate, FCuryCode from " + pub.yssGetTableName("Tb_Para_CashAccLink"));
			strSql.append(" where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(this.businessDate));
			strSql.append(" and (FSubCatCode = '' or FSubCatCode = ' ') and (FInvMgrCode = '' or FInvMgrCode = ' ') ");
			strSql.append(" and FTradeTypeCode = '06' and (FBrokerCode = '' or FBrokerCode = ' ') ");
			strSql.append(" and (FExchangeCode = '' or FExchangeCode = ' ') and FCuryCode = " + dbl.sqlString(this.curyCode));
			strSql.append(" and FPortCode = " + dbl.sqlString(this.portCode));
			strSql.append(" group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode, ");
			strSql.append(" FAuxiCashAccCode, FTradeTypeCode, FExchangeCode, FCashAccCode, ");
			strSql.append(" FLinkLevel, FCuryCode) a join (select FCashAccCode, max(FStartDate) as FStartDate from ");
			strSql.append(pub.yssGetTableName("Tb_Para_CashAccount"));
			strSql.append(" where FCheckState = 1 and FCuryCode = " + dbl.sqlString(this.curyCode));
			strSql.append(" and FStartDate <= " + dbl.sqlDate(this.businessDate));
			strSql.append(" group by FCashAccCode) b on a.FCashAccCode = b.FCashAccCode order by a.FLinkLevel desc ");

			rs = dbl.openResultSet(strSql.toString());
			if(rs.next()){
				cashAccCode = rs.getString("FCashAccCode");
			}
			
			if (cashAccCode.equals("")) {
				return "no";
			} else {
				return "yes";
			}
		} catch (Exception e) {
			throw new YssException("获取现金账户链接数据出错！", e);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	private String calAdjustMoney() throws YssException {
		double cvtConfirmMoney = 0;// 根据汇率转换后的确认金额
		double cvtReceiveMoney = 0;// 根据汇率转换后的到账金额
		double tradeCuryBsRate = 1;// 交易货币基础汇率
		double divCuryBsRate = 1;// 分红货币基础汇率
		double dTradeCashAccCuryRate = 1;
		double finalMoney = 0;// 计算得到的调整金额
		try {
			String[] reStrs = relaInfo.split("\t");

			EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
			rateOper.setYssPub(pub);

			// 交易货币 \t 分红货币 \t 组合代码 \t 业务日期 \t 确认金额 \t 到账金额 \t 分红金额
			if (reStrs.length >= 8) {
				String tradeCury = reStrs[0];
				String divCury = reStrs[1];
				String portCode = reStrs[2];
				String businessDate = reStrs[3];
				String confirmMoney = reStrs[4].trim().equals("") ? "0" : reStrs[4];
				String receiveMoney = reStrs[5].trim().equals("") ? "0" : reStrs[5];
				String divMoney = reStrs[6].trim().equals("") ? "0" : reStrs[6];
				String subTradeNum = reStrs[7];
				String TradeCashAccCury = this.getTradeCashAccCury(subTradeNum);

				// 根据 交易货币、业务日期、组合 获取 交易货币基础汇率
				tradeCuryBsRate = this.getSettingOper().getCuryRate(
						YssFun.parseDate(businessDate), tradeCury, portCode,
						YssOperCons.YSS_RATE_BASE);// 获取交易货币基础汇率的值

				// 根据 分红货币、业务日期、组合 获取分红货币基础汇率
				divCuryBsRate = this.getSettingOper().getCuryRate(
						YssFun.parseDate(businessDate), divCury, portCode,
						YssOperCons.YSS_RATE_BASE);// 获取分红货币基础汇率的值
				
				// 根据 分红交易数据现金账户币种、业务日期、组合 获取分红货币基础汇率
				dTradeCashAccCuryRate = this.getSettingOper().getCuryRate(
						YssFun.parseDate(businessDate), TradeCashAccCury, portCode,
						YssOperCons.YSS_RATE_BASE);// 获取分红货币基础汇率的值

				// 折算后的确认金额 = 确认金额 * 分红货币基础汇率 / 交易货币基础汇率
				if (!confirmMoney.equals("0")) {
					cvtConfirmMoney = YssD.round(YssD.div(YssD.mul(Double
							.parseDouble(confirmMoney), tradeCuryBsRate),
							dTradeCashAccCuryRate), 2);
				}
				// 折算后的到账金额 = 到账金额 * 分红货币基础汇率 / 交易货币基础汇率
				if (!receiveMoney.equals("0")) {
					cvtReceiveMoney = YssD.round(YssD.div(YssD.mul(Double
							.parseDouble(receiveMoney), divCuryBsRate),
							dTradeCashAccCuryRate), 2);
				}

				// 调整金额 = 关联交易的原始分红总金额 – 折算后的交易货币确认金额 – 折算的交易货币到账金额
				finalMoney = YssD.sub(Double.parseDouble(divMoney),
						cvtConfirmMoney, cvtReceiveMoney);
			}
			return finalMoney + "";
		} catch (Exception e) {
			throw new YssException("计算分红转投调整金额出错！", e);
		}
	}

	/**
	 * 获取分红交易数据现金账户对应的币种
	 * @param subTradeCashAccCode
	 * @return
	 * @throws YssException
	 */
	private String getTradeCashAccCury(String subTradeNum) throws YssException {
		String sCuryCode = "";
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		try{
			buf = new StringBuffer(100);
			buf.append("select c.FCuryCode From ").append(pub.yssGetTableName("tb_data_subtrade"))
				.append(" s left join (select FCashAccCode,FCuryCode from ")
				.append(pub.yssGetTableName("tb_para_cashaccount"))
				.append(" where fcheckstate = 1) c on c.fcashacccode = s.fcashacccode")
				.append(" where s.fnum = ").append(dbl.sqlString(subTradeNum));
			rs = dbl.openResultSet(buf.toString());
			if(rs.next()){
				sCuryCode = rs.getString("FCuryCode");
			}
		} catch (Exception e) {
			throw new YssException("获取现金账户币种出错！", e);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
		return sCuryCode;
	}

	public String auditMutli(String sMutilRowStr) throws YssException {
		Connection conn = null;
		String sqlStr = "";
		java.sql.PreparedStatement psmt = null;
		boolean bTrans = false;
		DividendToInvestBean data = null;
		String[] multAudit = null;
		String str = "";
		try {
			conn = dbl.loadConnection();

			conn.setAutoCommit(false);
			bTrans = true;

			sqlStr = "update " + pub.yssGetTableName("Tb_Data_DividendToInvest")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = ? ";

			psmt = conn.prepareStatement(sqlStr);
			if (sMutilRowStr.length() > 0) {
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					for (int i = 0; i < multAudit.length; i++) {
						data = new DividendToInvestBean();
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);

						// 若分红转投数据被反审核，则该数据业务处理生成的交易数据 和 现金应收应付数据需被删除
						if (this.dealType.equals("unaudit")) {
							str = " delete from " + pub.yssGetTableName("Tb_Data_SubTrade") + 
							" where FDataBirth = 'dividinvest' and FDealNum = " + dbl.sqlString(data.getNum());
							dbl.executeSql(str);

							str = " delete from " + pub.yssGetTableName("Tb_Data_CashPayRec") + 
							" where FRelaType = 'dividinvest' and FRelaNum = " + dbl.sqlString(data.getNum());
							dbl.executeSql(str);
		
							str = " delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") + 
							" where FNum in(select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") + 
							" where FNumType = 'dividinvest' and FRelaNum = " + dbl.sqlString(data.getNum()) + ")";
							dbl.executeSql(str);
							
							str = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
							" where FNumType = 'dividinvest' and FRelaNum = " + dbl.sqlString(data.getNum());
							dbl.executeSql(str);
						}

						psmt.setString(1, data.getNum());
						psmt.addBatch();
						logOper = SingleLogOper.getInstance();
						data = this;
						if (this.checkStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (this.checkStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
					}
				}
				psmt.executeBatch();

				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new YssException("批量处理数据出错!", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeStatementFinal(psmt);
		}
		return "";
	}

	/**
	 * 根据组合代码、业务日期、证券代码 获取分红交易数据
	 * 
	 * @return
	 * @throws YssException
	 */
	private String getTradeInfo() throws YssException {
		ResultSet rs = null;
		StringBuffer bufSql = new StringBuffer();
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();

		TradeSubBean tradeSub = new TradeSubBean();
		tradeSub.setYssPub(pub);

		String tradeCury = "";
		String curyName = "";
		try {
			sHeader = "交易编号\t证券代码\t证券名称\t交易方式\t成交日期\t结算日期\t投资组合\t投资经理\t交易券商\t实收金额\t交易币种\t现金帐户";

			bufSql.append(" select a.*, port.FPortName, tt.FTradeTypeName, f.FInvMgrName, h.FBrokerName, ");
			bufSql.append(" b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FSecurityName, ");
			bufSql.append(" e.FHandAmount,e.FFactRate,e.FTradeCury,cury.FCuryName,o.FCashAccName, p.FAttrClsName, ");
			//edit by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName
			bufSql.append(" oo.FCashAccName as FFactCashAccName,ts.fseatname,sh.fstockholdername, ooo.Fcashaccname as FETFBalaAcctName from ");
			bufSql.append(pub.yssGetTableName("Tb_Data_SubTrade") + " a left join (select FPortCode, FPortName from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState = 1) port on a.FPortCode = port.FPortCode ");
			bufSql.append(" left join (select FTradeTypeCode, FTradeTypeName from Tb_Base_Tradetype ");
			bufSql.append(" where FCheckState = 1) tt on a.FTradeTypeCode = tt.FTradeTypeCode ");
			bufSql.append(" left join (select FInvMgrCode, FInvMgrName from " + pub.yssGetTableName("Tb_Para_InvestManager"));
			bufSql.append(" where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode ");
			bufSql.append(" left join (select FBrokerCode, FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker"));
			bufSql.append(" where FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode ");
			bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode ");
			bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode ");
			bufSql.append(" left join (select eb.*, case when ec.FFactRate is null then 0 else ec.FFactRate end as FFactRate ");
			bufSql.append(" from (select FSecurityCode, max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_Security"));
			bufSql.append(" where FStartDate <= " + dbl.sqlDate(this.businessDate) + " and FCheckState = 1 group by FSecurityCode) ea ");
			bufSql.append(" join (select FSecurityCode, FSecurityName, FTradeCury, FStartDate, FHandAmount from "+ pub.yssGetTableName("Tb_Para_Security"));
			bufSql.append(" ) eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate ");
			bufSql.append(" left join (select FSecurityCode, FFactRate from " + pub.yssGetTableName("tb_para_fixinterest"));
			bufSql.append(" ) ec on ea.FSecurityCode = ec.FSecurityCode) e on a.FSecurityCode = e.FSecurityCode ");
			bufSql.append(" left join (select FCashAccCode, FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount"));
			bufSql.append(" where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode left join (");
			bufSql.append(" select FCashAccCode, FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount"));
			bufSql.append(" where FCheckState = 1) oo on a.FFactCashAccCode = oo.FCashAccCode left join (");
			//---add by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName start---//
			bufSql.append(" select FCashAccCode, FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount"));
			bufSql.append(" where FCheckState = 1) ooo on a.Fetfbalaacctcode = ooo.FCashAccCode left join(");
			//---add by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName end---//
			bufSql.append(" select FAttrClsCode, FAttrClsName from " + pub.yssGetTableName("Tb_Para_AttributeClass"));
			bufSql.append(" where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode left join ( ");
			bufSql.append(" SELECT FSeatCode, FSeatName from " + pub.yssGetTableName("Tb_Para_Tradeseat"));
			bufSql.append(" where FCheckState = 1) ts ON a.fseatCode = ts.fseatcode left join ( ");
			bufSql.append(" SELECT FStockholderCode, FStockholderName from " + pub.yssGetTableName("Tb_Para_Stockholder"));
			bufSql.append(" where FCheckState = 1) sh ON a.fstockholdercode = sh.fstockholdercode ");
			bufSql.append(" left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency"));
			bufSql.append(" where FCheckState = 1) cury on cury.fcurycode = e.FTradeCury ");
			bufSql.append(" where a.FPortCode = " + dbl.sqlString(this.portCode));
			bufSql.append(" and a.FSecurityCode = "+ dbl.sqlString(this.securityCode) + " and a.FCheckState = 1 ");
			bufSql.append(" and a.FNum not in (select FTradeNum from " + pub.yssGetTableName("Tb_data_DividendToInvest"));
			bufSql.append(this.tradeNum.trim().equals("") ? "" : " where FTradeNum <> " + dbl.sqlString(this.tradeNum));
			bufSql.append(") and a.FTRADETYPECODE = '06' and a.FSettleState = 0 ");

			rs = dbl.openResultSet(bufSql.toString());
			while (rs.next()) {
				if (tradeCury.equals("")) {
					tradeCury = rs.getString("FTradeCury");
				}
				if (curyName.equals("")) {
					curyName = rs.getString("FCuryName");
				}

				bufShow.append(rs.getString("FNum") + "").append("\t");
				bufShow.append(rs.getString("FSecurityCode") + "").append("\t");
				bufShow.append(rs.getString("FSecurityName") + "").append("\t");
				bufShow.append(rs.getString("FTradeTypeName") + "").append("\t");
				bufShow.append(YssFun.formatDate((rs.getString("FBargainDate")))).append("\t");
				bufShow.append(YssFun.formatDate((rs.getString("FSettleDate")))).append("\t");
				bufShow.append(rs.getString("FPortName") + "").append("\t");
				bufShow.append(rs.getString("FInvMgrName") + "").append("\t");
				bufShow.append(rs.getString("FBrokerName") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FTotalCost"),"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FCuryName") + "").append("\t");
				bufShow.append((rs.getString("FCashAccCode") + "").trim()).append(YssCons.YSS_LINESPLITMARK);

				tradeSub.setResultSetAttr(rs);
				bufAll.append(tradeSub.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
			}

			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,bufAll.toString().length() - 2);
			}

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f" + tradeCury
					+ "\r\f" + curyName;

		} catch (Exception e) {
			throw new YssException("获取交易数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
		String sTmpStr = "";
		String sMutiAudit = "";
		try {
			if (sRowStr.indexOf("calAdjustMoney") != -1
					|| sRowStr.indexOf("getDivAccCode") != -1) {
				relaInfo = sRowStr;
				return;
			}

			if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
				sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
				multAuditString = sMutiAudit;
				sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];
			}

			if (sRowStr.trim().length() == 0) {
				return;
			}
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
			} else {
				sTmpStr = sRowStr;
			}
			sRecycled = sRowStr; // 把未解析的字符串先赋给sRecycled
			reqAry = sTmpStr.split("\t");

			this.securityCode = reqAry[0];
			this.portCode = reqAry[1];
			this.payDate = reqAry[2];
			this.businessDate = reqAry[3];
			this.tradeNum = reqAry[4];
			this.confirmAmount = reqAry[5];
			this.confirmMoney = reqAry[6];
			this.adjustMoney = reqAry[7];
			this.price = reqAry[8];
			this.businessStartDate = reqAry[9];
			this.businessEndDate = reqAry[10];
			this.num = reqAry[11];
			this.checkStateId = Integer.parseInt(reqAry[12]);
			this.isOnlyColumns = reqAry[13];
			this.dealType = reqAry[14];
			this.curyCode = reqAry[15];
			this.accCode = reqAry[16];
			this.receiveMoney = reqAry[17];
			this.inAccType = reqAry[18];

			super.parseRecLog();

			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filterType == null) {
					this.filterType = new DividendToInvestBean();
					this.filterType.setYssPub(pub);
				}
				this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
			}
		} catch (Exception e) {
			throw new YssException("解析分红转投数据出错！", e);
		}
	}

	public String getListViewData1() throws YssException {
		ResultSet rs = null;
		StringBuffer bufSql = new StringBuffer();
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = this.getListView1Headers();

			bufSql.append(" select a.*, sec.FSecurityName, port.FPortName, b.FCreatorName, c.FCheckUserName, cury.FCuryName, cash.FCashAccName, sec.FTradeCury,t.Ftotalcost from (select * from ");
			bufSql.append(pub.yssGetTableName("tb_Data_DividendToInvest")+ buildFilterSql());
			bufSql.append(" ) a left join (select FSecurityCode,FSecurityName, FTradeCury from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Security"));
			bufSql.append(" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode ");
			bufSql.append(" left join (select FPortCode, FPortName from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Portfolio"));
			bufSql.append(" where FCheckState = 1) port on port.FPortCode = a.FPortCode ");
			bufSql.append(" left join (select FCuryCode,FCuryName from "+ pub.yssGetTableName("Tb_Para_Currency"));
			bufSql.append(" where FCheckState = 1) cury on cury.FCuryCode = a.FCuryCode ");
			bufSql.append(" left join (select FCashAccCode,FCashAccName from "+ pub.yssGetTableName("Tb_Para_Cashaccount"));
			bufSql.append(" where FCheckState = 1) cash on cash.FCashAccCode = a.FAccCode ");
			bufSql.append(" left join (select FNum, FTotalCost from "+ pub.yssGetTableName("Tb_Data_Subtrade"));
			bufSql.append(" where FCheckState = 1) t on t.FNum = a.FTradeNum ");
			bufSql.append(" left join (select FUserCode, FUserName as FCreatorName from ");
			bufSql.append(" Tb_Sys_UserList) b on a.FCreator = b.FUserCode ");
			bufSql.append(" left join (select FUserCode, FUserName as FCheckUserName from ");
			bufSql.append(" Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode ");
			bufSql.append(" order by a.FCheckState, a.FCreateTime desc ");

			yssPageInationBean.setsQuerySQL(bufSql.toString());
			yssPageInationBean.setsTableName("DividendToInvest");
			rs = dbl.openResultSet(yssPageInationBean);
			while (rs.next()) {
				//FTradeNum	FSecurityCode	FSecurityName	FPortCode	FPortName	
				//FBusinessDate	FPayDate	FCuryCode	FCuryName	FAccCode	
				//FCashAccName	FConfirmAmount;#,##0.##	FConfirmMoney;#,##0.##	
				//FPrice;#,##0.##	FAdjustMoney;#,##0.##	FReceiveMoney;#,##0.##	
				//FInAccType	FCreator	FCreateTime	FCheckUser	FCheckTime
				bufShow.append(rs.getString("FTradeNum")).append("\t");
				bufShow.append(rs.getString("FSecurityCode")).append("\t");
				bufShow.append(rs.getString("FSecurityName")).append("\t");
				bufShow.append(rs.getString("FPortCode")).append("\t");
				bufShow.append(rs.getString("FPortName")).append("\t");
				bufShow.append(YssFun.formatDate(rs.getDate("FBusinessDate"), "yyyy-MM-dd")).append("\t");
				bufShow.append(YssFun.formatDate(rs.getDate("FPayDate"), "yyyy-MM-dd")).append("\t");
				bufShow.append(rs.getString("FCuryCode")).append("\t");
				bufShow.append(rs.getString("FCuryName")).append("\t");
				bufShow.append(rs.getString("FAccCode")).append("\t");
				bufShow.append(rs.getString("FCashAccName")).append("\t");
				
				bufShow.append(rs.getBigDecimal("FConfirmAmount").toString() + "").append("\t");
				bufShow.append(rs.getBigDecimal("FConfirmMoney").toString() + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FPrice"),"#,##0.####")).append("\t");
				bufShow.append(rs.getBigDecimal("FAdjustMoney").toString() + "").append("\t");
				bufShow.append(rs.getBigDecimal("FReceiveMoney").toString() + "").append("\t");
				
//				bufShow.append(YssFun.formatNumber(rs.getDouble("FConfirmAmount"),"#,##0.####")).append("\t");
//				bufShow.append(YssFun.formatNumber(rs.getDouble("FConfirmMoney"),"#,##0.####")).append("\t");
//				bufShow.append(YssFun.formatNumber(rs.getDouble("FPrice"),"#,##0.##")).append("\t");
//				bufShow.append(YssFun.formatNumber(rs.getDouble("FAdjustMoney"),"#,##0.####")).append("\t");
//				bufShow.append(YssFun.formatNumber(rs.getDouble("FReceiveMoney"),"#,##0.####")).append("\t");
				if(rs.getString("FInAccType") != null && rs.getString("FInAccType").equals("01")){
					bufShow.append("按最终分红币种入账").append("\t");
				}else if(rs.getString("FInAccType") != null && rs.getString("FInAccType").equals("02")){
					bufShow.append("按初始分红币种入账").append("\t");
				}else{
					bufShow.append(rs.getString("")).append("\t");
				}
				bufShow.append(rs.getString("FCreator")).append("\t");
				bufShow.append(rs.getString("FCreateTime")).append("\t");
				bufShow.append(rs.getString("FCheckUser")).append("\t");
				bufShow.append(rs.getString("FCheckTime")).append("\t");
				bufShow.append(YssCons.YSS_LINESPLITMARK);
				
//				bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols()))
//						.append(YssCons.YSS_LINESPLITMARK);
				setResultSetAttr(rs);
				bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
			}

			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,bufShow.toString().length() - 2);
			}
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,bufAll.toString().length() - 2);
			}

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f"
					+ yssPageInationBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException("获取分红转投数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(dbl.getProcStmt());
		}
	}

	private String buildFilterSql() throws YssException {
		String sResult = "";
		sResult = " where 1=1";
		if (this.filterType != null) {
			if (this.filterType.isOnlyColumns.equals("1")) {
				sResult = sResult + " and 1 = 2 ";
			}
			if (this.filterType.securityCode != null && this.filterType.securityCode.trim().length() > 0) {
				sResult = sResult + " and FSecurityCode=" + dbl.sqlString(this.securityCode);
			}
			if (this.filterType.portCode != null && this.filterType.portCode.trim().length() > 0) {
				sResult = sResult + " and FPortCode=" + dbl.sqlString(this.portCode);
			}
			if (this.filterType.curyCode != null && this.filterType.curyCode.trim().length() > 0) {
				sResult = sResult + " and FCuryCode=" + dbl.sqlString(this.curyCode);
			}
			if (this.filterType.accCode != null && this.filterType.accCode.trim().length() > 0) {
				sResult = sResult + " and FAccCode=" + dbl.sqlString(this.accCode);
			}
			if (this.filterType.payDate != null && !YssFun.formatDate(this.filterType.payDate).equals("9998-12-31")) {
				sResult = sResult + " and FPayDate=" + dbl.sqlDate(this.payDate);
			}
			if (this.filterType.businessDate != null
					&& !YssFun.formatDate(this.filterType.businessDate).equals("9998-12-31")) {
				sResult = sResult + " and FBusinessDate=" + dbl.sqlDate(this.businessDate);
			}
			if (this.filterType.confirmAmount != null && !this.filterType.confirmAmount.equals("")) {
				sResult = sResult + " and FConfirmAmount = " + this.confirmAmount;
			}
			if (this.filterType.confirmMoney != null && !this.filterType.confirmMoney.equals("")) {
				sResult = sResult + " and FConfirmMoney = " + this.confirmMoney;
			}
			if (this.filterType.adjustMoney != null && !this.filterType.adjustMoney.equals("")) {
				sResult = sResult + " and FAdjustMoney = " + this.adjustMoney;
			}
			if (this.filterType.receiveMoney != null && !this.filterType.receiveMoney.equals("")) {
				sResult = sResult + " and FReceiveMoney = " + this.receiveMoney;
			}
			if (this.filterType.price != null && !this.filterType.price.equals("")) {
				sResult = sResult + " and FPrice = " + this.price;
			}
			if (this.filterType.inAccType != null && !this.filterType.inAccType.equals("99")
					&& !this.filterType.inAccType.equals("")) {
				sResult = sResult + " and FInAccType = " + dbl.sqlString(this.filterType.inAccType);
			}
			if (this.filterType.businessStartDate != null && this.filterType.businessEndDate != null
					&& !this.filterType.businessStartDate.equals("9998-12-31")
					&& !this.filterType.businessEndDate.equals("9998-12-31")) {
				sResult = sResult + " and FBusinessDate between " + dbl.sqlDate(this.businessStartDate) + " and "
						+ dbl.sqlDate(this.businessEndDate);
			}
			if (this.filterType.businessStartDate != null && this.filterType.businessEndDate != null
					&& !this.filterType.businessStartDate.equals("9998-12-31")
					&& this.filterType.businessEndDate.equals("9998-12-31")) {
				sResult = sResult + " and FBusinessDate >= " + dbl.sqlDate(this.businessStartDate);
			}
			if (this.filterType.businessStartDate != null && this.filterType.businessEndDate != null
					&& this.filterType.businessStartDate.equals("9998-12-31")
					&& !this.filterType.businessEndDate.equals("9998-12-31")) {
				sResult = sResult + " and FBusinessDate <= " + dbl.sqlDate(this.businessEndDate);
			}
		}

		return sResult;
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException {
		this.num = rs.getString("FNum");
		this.securityCode = rs.getString("FSecurityCode");
		this.securityName = rs.getString("FSecurityName");
		this.portCode = rs.getString("FPortCode");
		this.portName = rs.getString("FPortName");
		this.curyCode = rs.getString("FCuryCode");
		this.curyName = rs.getString("FCuryName");
		this.accCode = rs.getString("FAccCode");
		this.accName = rs.getString("FCashAccName");
		this.businessDate = YssFun.formatDate(rs.getDate("FBusinessDate"), "yyyy-MM-dd");
		this.payDate = YssFun.formatDate(rs.getDate("FPayDate"), "yyyy-MM-dd");
		this.tradeNum = rs.getString("FTradeNum");
		this.confirmAmount = rs.getBigDecimal("FConfirmAmount").toString() + "";
		this.confirmMoney = rs.getBigDecimal("FConfirmMoney").toString() + "";
		this.price = YssFun.formatNumber(rs.getDouble("FPrice"),"#,##0.####") + "";
		this.adjustMoney = rs.getBigDecimal("FAdjustMoney").toString() + "";
		this.receiveMoney = rs.getBigDecimal("FReceiveMoney").toString() + "";
		this.inAccType = rs.getString("FInAcctype") + "";
		this.tradeCury = rs.getString("FTradeCury");
		this.divMoney = rs.getDouble("FTotalCost") + "";
		super.setRecLog(rs);
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
}
