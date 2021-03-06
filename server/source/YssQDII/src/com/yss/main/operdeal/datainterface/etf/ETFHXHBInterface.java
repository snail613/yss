package com.yss.main.operdeal.datainterface.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.commeach.EachRateOper;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.linkInfo.BaseLinkInfoDeal;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.taoperation.TaCashAccLinkBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 华夏接口导入深交所 ETF 申赎回报数据，先存放到临时表（tmp_etf_hbinterface）,然后导入系统表（Tb_XXX_ETF_HBInterface）
 * @author xuqiji 20091217
 *
 */
public class ETFHXHBInterface extends DataBase{
	
	/**shashijie 2013-1-6 STORY 3328 ETF基础参数*/
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;
	private HashMap etfParam = null;//保存参数设置
	/**end shashijie 2013-1-6 STORY */
	
	public ETFHXHBInterface() {
	}
	/**
	 * 接口导入入口方法
	 */
	public void inertData() throws YssException {
		try{
			/**shashijie 2013-1-6 STORY 3328 初始ETF参数*/
			initDateParam();
			/**end shashijie 2013-1-6 STORY */
			
			this.doInertData();//接口导入数据
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	
	/**shashijie 2013-1-6 STORY 3328 初始ETF参数 */
	private void initDateParam() throws YssException {
		paramSetAdmin = new ETFParamSetAdmin();//实例化参数设置的操作类
		paramSetAdmin.setYssPub(pub);//设置pub
		etfParam = paramSetAdmin.getETFParamInfo(this.sPort); // 根据已选组合代码用于获取相关ETF参数数据
		paramSet = (ETFParamSetBean) etfParam.get(this.sPort);//根据组合代码获取参数设置的实体bean
	}
	
	/**
	 * 接口导入数据
	 * @throws YssException 
	 *
	 */
	private void doInertData() throws YssException {
		Connection conn = dbl.loadConnection(); // 新建连接
		boolean bTrans = true;//事物控制标识
		ResultSet rs = null;//结果集
		ResultSet rr = null;
		PreparedStatement pst = null; // 声明PreparedStatement
		StringBuffer buff = null;
		String sSql = "";
		String [] sportCode = null;
		String[] sParam = new String[3];//保存参数设置中获取的参数值
		int iLinkNum = 0;// 关联编号
		long sNum = 0;//用于拼接交易编号
		String strNumDate = "";//保存交易编号
		double dCashRepAmount = 0;// 现金替代金额
		Date CashBalanceDate = null;//现金差额结转日期
		Date CashReplaceDate = null;// 现金替代结转日期
		EachExchangeHolidays holiday = null;//节假日代码
		String strCashAcctCode = "";//现金账户
		Date confirmDate = null; //申赎确认日期
		try{
			buff = new StringBuffer(500);
			// 1.删除表Tb_XXX_ETF_HBInterface相关交易日期和组合代码的数据
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_HBInterface"));
			buff.append(" where FBargainDate = ").append(dbl.sqlDate(this.sDate));
			buff.append(" and FPortCode in(").append(operSql.sqlCodes(this.sPort)).append(")");
			
			dbl.executeSql(buff.toString());
			//BY ZHOUWEI 20111118
			// 2.删除表Tb_XXX_ETF_JGInterface相关交易日期和组合代码的数据
			buff.delete(0,buff.length());
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_JGInterface"));
			buff.append(" where FBargainDate = ").append(dbl.sqlDate(this.sDate));
			buff.append(" and FPortCode in(").append(operSql.sqlCodes(this.sPort)).append(")");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());

			sportCode = this.sPort.split(",");
			for(int i =0; i< sportCode.length; i++){
				conn.setAutoCommit(false);//设置为手动提交事物
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_ETF_HBInterface"));
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_ETF_JGInterface"));
				//获取etf参数设置中的：一级市场代码。二级市场代码，资金代码
				buff.append(" select * from ").append(pub.yssGetTableName("Tb_ETF_Param"));
				buff.append(" where FPortCode =").append(dbl.sqlString(sportCode[i]));

				ResultSet ret = dbl.openResultSet(buff.toString());
				buff.delete(0,buff.length());
				if (ret.next()) {
					sParam[0] = ret.getString("FOneGradeMktCode");// 一级市场代码
					sParam[1] = ret.getString("FTwoGradeMktCode");// 二级市场代码
					sParam[2] = ret.getString("FCapitalCode");// 资金代码
				}
				dbl.closeResultSetFinal(ret);
				
				buff.append(" select a.*,b.fbrokercode from tmp_etf_hbinterface a left join(select * from ");
				buff.append(pub.yssGetTableName("tb_para_tradeseat")).append(" where FCheckState =1").append(") b ");
				buff.append(" on a.HBDFXW = b.FSeatCode where a.hbzqdm in(")
				.append(dbl.sqlString(sParam[1])).append(",").append(dbl.sqlString(sParam[2])).append(")");
				/**shashijie 2011-12-26 STORY 1789 华夏只支持4种方式导入,申,赎,替代金额的申,赎*/
				buff.append(" And HBYWLB In ('KB','KS','ZB','ZS') ").append(" order by a.HBCJHM");
				/**end*/
				
				rs = dbl.openResultSet(buff.toString());
				buff.delete(0,buff.length());
				
				buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_HBInterface"));
				buff.append("(FPortCode,FTradeNum,FSecurityCode,FContractNum,FStockholderCode,FSeatNum,FBrokerCode,FTradeAmount,FTradePrice,");
				buff.append(" FOtherSeat,FOtherStockholder,FBargainTime,FBargainDate,FRelaNum,FTradeTypeCode,FTradeMoney,FOperType,FCheckState,");
				buff.append(" FCreator,FCreateTime,FCheckUser,FCheckTime)").append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				pst = dbl.openPreparedStatement(buff.toString());
				buff.delete(0,buff.length());
				
				while(rs.next()){
					pst.setString(1,sportCode[i]);//组合代码
					pst.setString(2,rs.getString("HBCJHM"));//成交编号
					pst.setString(3,rs.getString("HBZQDM"));// 证券代码
					
					/**shashijie 2011-12-29 STORY 1789  合同序号需要去掉前面14位   */
					pst.setString(4,rs.getString("HBHTXH").substring(14));//合同序号
					/**end*/
					
					pst.setString(5,rs.getString("HBGDDM"));//股东代码
					
					pst.setString(6,rs.getString("HBHTXH").substring(0,6));//交易席位
					pst.setString(7,(null == rs.getString("fbrokercode") || rs.getString("fbrokercode").length() == 0) ? 
							" " : rs.getString("fbrokercode"));//券商代码
					pst.setDouble(8,rs.getDouble("HBCJSL"));//成交数量
					pst.setDouble(9,rs.getDouble("HBCJJG"));//成交价格
					pst.setString(10,rs.getString("HBDFXW"));//对方席位
					pst.setString(11,rs.getString("HBDFGD"));//对方股东
					pst.setString(12,rs.getString("HBCJSJ"));//成交时间
					pst.setDate(13,rs.getDate("HBCJRQ"));//成交日期
					if (rs.getString("HBZQDM").equalsIgnoreCase(sParam[1])) {
						iLinkNum++;
					}
					pst.setInt(14, iLinkNum); // 关联编号
					pst.setString(15,rs.getString("HBYWLB"));//成交类型
					pst.setDouble(16,YssD.round(YssD.mul(rs.getDouble("HBCJSL"),rs.getDouble("HBCJJG")),2));//成交金额
					if (rs.getString("HBZQDM").equalsIgnoreCase(sParam[1])) {
						pst.setString(17, "2ndcode"); // 业务标志
					} else if (rs.getString("HBZQDM").equalsIgnoreCase(sParam[2])) {
						pst.setString(17, "cashcode"); // 业务标志
					} else {
						pst.setString(17, "seccode"); // 业务标志
					}
					pst.setInt(18,1);
					pst.setString(19,pub.getUserCode());
					pst.setString(20,YssFun.formatDatetime(new java.util.Date()));
					pst.setString(21,pub.getUserCode());
					pst.setString(22,YssFun.formatDatetime(new java.util.Date()));
					
					pst.addBatch();
				}
				pst.executeBatch();
				dbl.closeStatementFinal(pst);
				dbl.closeResultSetFinal(rs);
				
				//BY ZHOUWEI 20111118 向结果表中插入同样的数据
				buff.append(" select a.*,b.fbrokercode from tmp_etf_hbinterface a left join(select * from ");
				buff.append(pub.yssGetTableName("tb_para_tradeseat")).append(" where FCheckState =1").append(") b ");
				buff.append(" on a.HBDFXW = b.fseatcode where a.hbzqdm in(")
				.append(dbl.sqlString(sParam[1])).append(",").append(dbl.sqlString(sParam[2])).append(")")
			    .append(" and a.HBCJRQ=").append(dbl.sqlDate(this.sDate))
			    /**shashijie 2011-12-27 STORY 1789 华夏只支持4种方式导入,申,赎,替代金额的申,赎*/
				.append(" And HBYWLB In ('KB','KS','ZB','ZS') ")
				/**end*/
			    .append(" order by a.HBCJHM");
				
				rs = dbl.openResultSet(buff.toString());
				buff.delete(0,buff.length());
				
				buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_JGInterface"));
				buff.append("(FPortCode,FTradeNum,FSecurityCode,FContractNum,FStockholderCode,FSeatNum,FBrokerCode,FTradeAmount,FTradePrice,");
				buff.append(" FOtherSeat,FOtherStockholder,FBargainTime,FBargainDate,FRelaNum,FTradeTypeCode,FTradeMoney,FOperType,FCheckState,");
				buff.append(" FCreator,FCreateTime,FCheckUser,FCheckTime)").append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				pst = dbl.openPreparedStatement(buff.toString());
				buff.delete(0,buff.length());
				
				while(rs.next()){
					pst.setString(1,sportCode[i]);//组合代码
					pst.setString(2,rs.getString("HBCJHM"));//成交编号
					pst.setString(3,rs.getString("HBZQDM"));// 证券代码
					/**shashijie 2011-12-29 STORY 1789  合同序号需要去掉前面14位   */
					pst.setString(4,rs.getString("HBHTXH").substring(14));//合同序号
					/**end*/
					pst.setString(5,rs.getString("HBGDDM"));//股东代码
					
					pst.setString(6,rs.getString("HBHTXH").substring(0,6));//交易席位
					pst.setString(7,(null == rs.getString("fbrokercode") || rs.getString("fbrokercode").length() == 0) ? 
							" " : rs.getString("fbrokercode"));//券商代码
					pst.setDouble(8,rs.getDouble("HBCJSL"));//成交数量
					pst.setDouble(9,rs.getDouble("HBCJJG"));//成交价格
					pst.setString(10,rs.getString("HBDFXW"));//对方席位
					pst.setString(11,rs.getString("HBDFGD"));//对方股东
					pst.setString(12,rs.getString("HBCJSJ"));//成交时间
					pst.setDate(13,rs.getDate("HBCJRQ"));//成交日期
					if (rs.getString("HBZQDM").equalsIgnoreCase(sParam[1])) {
						iLinkNum++;
					}
					pst.setInt(14, iLinkNum); // 关联编号
					pst.setString(15,rs.getString("HBYWLB"));//成交类型
					pst.setDouble(16,YssD.round(YssD.mul(rs.getDouble("HBCJSL"),rs.getDouble("HBCJJG")),2));//成交金额
					if (rs.getString("HBZQDM").equalsIgnoreCase(sParam[1])) {
						pst.setString(17, "2ndcode"); // 业务标志
					} else if (rs.getString("HBZQDM").equalsIgnoreCase(sParam[2])) {
						pst.setString(17, "cashcode"); // 业务标志
					} else {
						pst.setString(17, "seccode"); // 业务标志
					}
					pst.setInt(18,1);
					pst.setString(19,pub.getUserCode());
					pst.setString(20,YssFun.formatDatetime(new java.util.Date()));
					pst.setString(21,pub.getUserCode());
					pst.setString(22,YssFun.formatDatetime(new java.util.Date()));
					
					pst.addBatch();
				}
				pst.executeBatch();
				dbl.closeStatementFinal(pst);
				dbl.closeResultSetFinal(rs);
				// 向TA 交易数据表Tb_XXX_TA_Trade中插入数据
				// 插入数据前，先删除数据，条件： 日期和组合
				sSql = " delete from " + pub.yssGetTableName("Tb_TA_Trade")
						+ " where FTradeDate =" + dbl.sqlDate(this.sDate)
						+ " and FPortCode =" + dbl.sqlString(sportCode[i]) 
						+ " and FSellType in ('01','02')";

				dbl.executeSql(sSql);

				String insertTASql = " insert into "
						+ pub.yssGetTableName("Tb_TA_Trade")
						+ "(FNum,FTradeDate,FMarkDate,FPortCode,FPortClsCode,FSellNetCode,FSellType,FCuryCode,FAnalysisCode1,FAnalysisCode2,"
						+ "FAnalysisCode3,FSellMoney,FBeMarkMoney,FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FCashAccCode,"
						+ "FConfimDate,FSettleDate,FSettleMoney,FPortCuryRate,FBaseCuryRate,FSettleState,FDesc,FCheckState,FCreator,"
						+ "FCreateTime,FCheckUser,FCheckTime,FConvertNum,FCashRepAmount,FCashBalanceDate,FCashReplaceDate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
				pst = dbl.openPreparedStatement(insertTASql);

				sSql = " select * from (select sum(FTradeAmount) as FTradeAmountAll,  fportcode, FCashAccCode, FTradeTypeCode"
						+ " from (select gh.*, pa.fcashacccode from "
						+ pub.yssGetTableName("Tb_ETF_HBInterface")
						+ " gh join (select FOneGradeMktCode, fportcode, FCashAccCode from "
						+ pub.yssGetTableName("Tb_ETF_Param")//参数设置表
						+ " where FCheckState = 1) pa on gh.fportcode = pa.fportcode "
						+ " where gh.FPortCode = "
						+ dbl.sqlString(sportCode[i])
						+ " and gh.fopertype = '2ndcode' and gh.fbargaindate ="
						+ dbl.sqlDate(this.sDate)
						+ " ) tt"
						+ " group by fportcode ,FCashAccCode, FTradeTypeCode ) aa "
						+ " left join (select FPortCode as Fport, FPortCury,FAssetCode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")//组合设置表
						+ " where FCheckState=1) pf on aa.Fportcode = pf.Fport";

				rs = dbl.openResultSet(sSql);

				// --------------------拼接交易编号---------------------
				strNumDate = YssFun.formatDatetime(this.sDate).substring(0, 8);
				strNumDate = strNumDate
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("tb_ta_trade"), dbl.sqlRight(
								"FNUM", 6), "000000", " where FNum like 'T"
								+ strNumDate + "%'", 1);
				strNumDate = "T" + strNumDate;
				String s = strNumDate.substring(9, strNumDate.length());
				sNum = Long.parseLong(s);
				// --------------------------------end--------------------------//
				while (rs.next()) {
					//基础汇率
					double baseCuryRate = this.getSettingOper().getCuryRate(
							this.sDate, rs.getString("FPortCury"),
							sportCode[i], YssOperCons.YSS_RATE_BASE);

					EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
					rateOper.setYssPub(pub);
					rateOper.getInnerPortRate(this.sDate, rs
							.getString("FPortCury"), sportCode[i]);
					double portCuryRate = rateOper.getDPortRate();//组合汇率
					// --------------------拼接交易编号---------------------
					sNum++;
					String tmp = "";
					for (int t = 0; t < s.length()
							- String.valueOf(sNum).length(); t++) {
						tmp += "0";
					}
					strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
					// ------------------------end--------------------------//
					// ------------------------处理现金替代金额-------------------//
					sSql = " select sum(FTrademoney) as FTotalMoney from "
							+ pub.yssGetTableName("Tb_ETF_HBInterface")//过户库
							+ " where FPortCode = "
							+ dbl.sqlString(sportCode[i])
							+ " and FOperType = 'cashcode' and FBargaindate = "
							+ dbl.sqlDate(this.sDate) + " and FTradeTypeCode ="
							+ (rs.getString("FTradeTypeCode").equalsIgnoreCase("KS") ? "'ZS'" : "'ZB'")
							+ " group by FPortCode,FTradeTypeCode";
					rr = dbl.openResultSet(sSql);
					if (rr.next()) {
						dCashRepAmount = rr.getDouble("FTotalMoney");// 现金替代金额
					}
					dbl.closeResultSetFinal(rr);
					// ----------------------------end-----------------------------//
					pst.setString(1, strNumDate); // 编号
					pst.setDate(2, YssFun.toSqlDate(this.sDate)); // 交易日期
					pst.setDate(3, YssFun.toSqlDate(this.sDate)); // 基准日期
					pst.setString(4, sportCode[i]); // 组合代码
					pst.setString(5, rs.getString("FAssetCode")); // 组合分级代码
																		// 为基金代码
					pst.setString(6, " "); // 销售网点代码
					pst.setString(7, rs.getString("FTradeTypeCode").equalsIgnoreCase(
							"KS") ? "01" : "02"); // 销售类型   KS：申购   KB：赎回
					pst.setString(8, rs.getString("FPortCury")); // 销售货币
					pst.setString(9, " "); // 分析代码1
					pst.setString(10, " "); // 分析代码2
					pst.setString(11, " "); // 分析代码3

					pst.setDouble(12, 0); // 销售金额(净金额)=单位净值*销售数量
					pst.setDouble(13, 0); // 基准金额
					pst.setDouble(14, rs.getDouble("FTradeAmountAll")); // 销售数量
					pst.setDouble(15, 0); // 销售价格
					pst.setDouble(16, 0); // 未实现损益平准金
					pst.setDouble(17, 0); // 损益平准金
					//-----------------获取现金账户，通过现金账户链接------------------------//
					 BaseLinkInfoDeal taCashAccOper = (BaseLinkInfoDeal) pub.
	                    getOperDealCtx().getBean(
	                        "TaCashLinkDeal");
	                taCashAccOper.setYssPub(pub);
	                TaCashAccLinkBean taCashAccLink = new TaCashAccLinkBean();
	                taCashAccLink.setSellNetCode(" ");
	                taCashAccLink.setPortClsCode(rs.getString("FAssetCode"));
	                taCashAccLink.setPortCode(sportCode[i]);
	                taCashAccLink.setSellTypeCode(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS") ? "01" : "02");
	                taCashAccLink.setCuryCode(rs.getString("FPortCury"));
	                taCashAccLink.setStartDate(YssFun.formatDate(this.sDate));
	                taCashAccOper.setLinkAttr(taCashAccLink);
	                ArrayList reList = taCashAccOper.getLinkInfoBeans();
	                if (reList != null) {
	                	strCashAcctCode = ( (CashAccountBean) reList.get(0)).getStrCashAcctCode();
	                }
					//------------------end--------------------------------------------//
					pst.setString(18,strCashAcctCode.trim().length()>0?strCashAcctCode:" "); // 现金帐户
					//STORY #1789 QDV4中行2011年10月25日01_A wujunhua  modify 20111230
					TaTradeBean ta = new TaTradeBean();
					ta.setYssPub(pub);
					confirmDate = ta.getConfirmDay(" ", rs.getString("FAssetCode"), sportCode[i], 
												rs.getString("FTradeTypeCode").equalsIgnoreCase("KS") ? "01" : "02", 
														rs.getString("FPortCury"), this.sDate);

					pst.setDate(19, YssFun.toSqlDate(confirmDate)); // 确认日期
					pst.setDate(20, YssFun.toSqlDate(confirmDate)); // 结算日期
					pst.setDouble(21, 0); // 结算金额
					pst.setDouble(22, portCuryRate); // 组合汇率
					pst.setDouble(23, baseCuryRate); // 基础汇率
					pst.setInt(24, 0); // 结算状态
					pst.setString(25, ""); // 描述
					pst.setInt(26, 1); // 审核状态
					pst.setString(27, pub.getUserCode()); // 创建人、修改人
					pst.setString(28, YssFun.formatDatetime(new java.util.Date())); // 创建、修改时间
					pst.setString(29, pub.getUserCode()); // 复核人
					pst.setString(30, YssFun.formatDatetime(new java.util.Date())); // 复核时间
					pst.setDouble(31, 0);// 份额折算数量
					pst.setDouble(32, dCashRepAmount);// 现金替代金额
					// ----------------------以下处理现金差额结转日期和现金替代结转日期------------------------//
					sSql = "select a.*,b.FSGCashBalHoliday,c.FSHCashBalHoliday,d.FSGRepCashHoliday,e.FSHRepCashHoliday from " +
						" (select * from "+pub.yssGetTableName("Tb_ETF_Param") + " where FCheckState = 1 "+//参数设置表
						" and FPortCode = " + dbl.sqlString(sportCode[i]) + 
						" ) a left join (select FPortCode,FHolidaysCode as FSGCashBalHoliday from " + pub.yssGetTableName("Tb_ETF_ParamHoildays") + //参数设置子表
						" where FOverType = 'sgbalanceover') b on b.FPortCode = a.FPortCode " + 
						" left join (select FPortCode,FHolidaysCode as FSHCashBalHoliday from " + pub.yssGetTableName("Tb_ETF_ParamHoildays") + 
						" where FOverType = 'shbalanceover') c on c.FPortCode = a.FPortCode " + 
						" left join (select FPortCode,FHolidaysCode as FSGRepCashHoliday from " + pub.yssGetTableName("Tb_ETF_ParamHoildays") + 
						" where FOverType = 'sgreplaceover') d on d.FPortCode = a.FPortCode " + 
						" left join (select FPortCode,FHolidaysCode as FSHRepCashHoliday from " + pub.yssGetTableName("Tb_ETF_ParamHoildays") + 
						" where FOverType = 'shreplaceover') e on e.FPortCode = a.FPortCode ";
					
					rr = dbl.openResultSet(sSql);
					//String sRowStr="";
					holiday =new EachExchangeHolidays();
					holiday.setYssPub(pub);
					if(rr.next()){
						
						/**shashijie 2013-1-6 STORY 3328 测试*/
						/*if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){//申购
							sRowStr = (rr.getString("FSGCashBalHoliday") == null ? rr.getString("FHolidaysCode") : rr.getString("FSGCashBalHoliday")) + "\t" + rr.getInt("FSGBalanceOver") + "\t" + YssFun.formatDate(this.sDate);
						}else{//赎回
							sRowStr = (rr.getString("FSHCashBalHoliday") == null ? rr.getString("FHolidaysCode") : rr.getString("FSHCashBalHoliday")) + "\t" + rr.getInt("FSHBalanceOver") + "\t" + YssFun.formatDate(this.sDate);
						}
						holiday.parseRowStr(sRowStr);
						CashBalanceDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//现金差额结转日期
						if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){//申购
							sRowStr = (rr.getString("FSGRepCashHoliday") == null ? rr.getString("FHolidaysCode") : rr.getString("FSGRepCashHoliday")) + "\t" + rr.getInt("FSGReplaceOver") + "\t" + YssFun.formatDate(this.sDate);
						}else{//赎回
							sRowStr = (rr.getString("FSHRepCashHoliday") == null ? rr.getString("FHolidaysCode") : rr.getString("FSHRepCashHoliday")) + "\t" + rr.getInt("FSHReplaceOver") + "\t" + YssFun.formatDate(this.sDate);
						}
						holiday.parseRowStr(sRowStr);
						CashReplaceDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));*///现金替代结转日期
						/**end shashijie 2013-1-6 STORY 3328 */
						
						/**shashijie 2013-1-6 STORY 3328 使用新获取工作日方法,考虑境内境*/
						if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){//申购
							// 现金差额结转日期
							CashBalanceDate = YssFun.toSqlDate(getWorkDayMakeBack(this.sDate,
									paramSet.getSSGBalanceOver(),(String)paramSet.getHoildaysRela().get("sgbalanceover"),
									paramSet.getsSGBalanceOver2(),(String)paramSet.getHoildaysRela().get("sgbalanceover2"))
									);
							// 现金替代结转日期
							CashReplaceDate = YssFun.toSqlDate(getWorkDayMakeBack(this.sDate,
									paramSet.getSSGReplaceOver(),(String)paramSet.getHoildaysRela().get("sgreplaceover"),
									paramSet.getsSGReplaceOver2(),(String)paramSet.getHoildaysRela().get("sgreplaceover2"))
									);
						}else {
							CashBalanceDate = YssFun.toSqlDate(getWorkDayMakeBack(this.sDate,
									paramSet.getSSHBalanceOver(),(String)paramSet.getHoildaysRela().get("shbalanceover"),
									paramSet.getsSHBalanceOver2(),(String)paramSet.getHoildaysRela().get("shbalanceover2"))
									);
							
							CashReplaceDate = YssFun.toSqlDate(getWorkDayMakeBack(this.sDate,
									paramSet.getSSHReplaceOver(),(String)paramSet.getHoildaysRela().get("shreplaceover"),
									paramSet.getsSHReplaceOver2(),(String)paramSet.getHoildaysRela().get("shreplaceover2"))
									);
						}
						
						/**end shashijie 2013-1-6 STORY 3328 */
						
					}
					dbl.closeResultSetFinal(rr);
					pst.setDate(33, (java.sql.Date) CashBalanceDate);// 现金差额结转日期
					pst.setDate(34, (java.sql.Date) CashReplaceDate);// 现金替代结转日期
					// ----------------------end-------------------------------------------------------//
					pst.addBatch();
				}
				pst.executeBatch();
				
				conn.commit();
				conn.setAutoCommit(true);
				bTrans = false;
				dbl.closeResultSetFinal(rs);
				dbl.closeStatementFinal(pst);
			}
		}catch (Exception e) {
			throw new YssException("接口导入数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rr);
			dbl.closeStatementFinal(pst);
		}
	}
	
	/**shashijie 2013-1-6 STORY 3328 获取工作日,向后推出int BeginSupply个工作日(考虑国内国外) */
	private Date getWorkDayMakeBack(Date dDate, int num1, String holidayCode1,
			int num2, String holidayCode2) throws YssException {
		Date makeDate = dDate;
		String sDate = paramSetAdmin.getWorkDay(dDate, holidayCode1, num1, holidayCode2, num2);
		makeDate = YssFun.toDate(sDate);
		return makeDate;
	}
	
}




















