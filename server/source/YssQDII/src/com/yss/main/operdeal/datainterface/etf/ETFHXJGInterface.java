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
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.linkInfo.BaseLinkInfoDeal;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.taoperation.TaCashAccLinkBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 华夏接口导入深交所 ETF 申赎回报数据，先存放到临时表（TMP_ETF_SJSJGINTERFACE）,然后导入系统表（Tb_XXX_ETF_JGInterface）
 * @author ZHOUWEI 20111118
 *
 */
public class ETFHXJGInterface extends DataBase{
	
	/**shashijie 2012-01-04 STORY 1789 获取结果数据的成交日期*/
	private Date FBargainDate = null;//交易日
	/**end*/
	
	/**shashijie 2013-1-6 STORY 3328 ETF基础参数*/
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;
	private HashMap etfParam = null;//保存参数设置
	/**end shashijie 2013-1-6 STORY */
	
	public ETFHXJGInterface() {
	}
	
	/**
	 * 接口导入入口方法
	 */
	public void inertData() throws YssException {
		try{
			
			/**shashijie 2012-01-04 结果文件在T+1日导入,结算延迟天数得根据交易日而非当前日期  */
			this.FBargainDate = getBargainDate();
			
			Date oldWork = getWorkDay(this.sDate, -1);//T-1日工作日
			
			//判断文件中的交易日是否是操作日的昨日(工作日)
			if (YssFun.dateDiff(FBargainDate, oldWork)!=0) {
				throw new YssException("导入文件的交易日期不是当前一天工作日！");
			}
			/**end*/
			
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

	/** shashijie 修改 2012-01-04 需求变更,结果数据在T+1日导入
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
			// 1.删除表Tb_XXX_ETF_JGInterface相关交易日期和组合代码的数据
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_JGInterface"));
			buff.append(" where FBargainDate = ").append(dbl.sqlDate(this.FBargainDate));
			buff.append(" and FPortCode in(").append(operSql.sqlCodes(this.sPort)).append(")");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());

			sportCode = this.sPort.split(",");
			for(int i =0; i< sportCode.length; i++){
				conn.setAutoCommit(false);//设置为手动提交事物
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
				
				/*buff.append(" select JGBFZH,JGBYBZ,JGCJJG,JGCJSL,JGFSRQ,JGGFXZ,JGGHF,JGHBDH,JGJGGF,JGJSBZ,JGJSF,JGJSFS,")
					.append("JGJSRQ,JGJSZH,JGJYF,JGJYFS,JGPCBS,JGQSLS,JGQSRQ,JGQSSL,JGQSYJ,JGQTFY,JGSCDM,JGSFJE,JGSJLX,JGSXF,JGXYJY,JGYHS,JGZJJE,JGZQDM2,JGZQLB,JGZYDH")
					 .append(" ,case when JGYWLB = 'LA' and JGZQDM <>'159900' then 'KS' when JGYWLB = 'LA' and JGZQDM ='159900' then 'ZS' when JGYWLB = 'LC' and JGZQDM ='159900' then 'ZB' else 'KB' end as JGYWLB,JGZQDM,JGJYXW,JGXWDM,")
				    .append("JGGDDM,JGWTXH,JGJSSL,JGQSJG,JGQSBJ,JGCJRQ,")//arealw 20111126 增加转换条件，对于深证sjsjg现金替代都是在159900代码对应的数据。
				    .append("JGCJHM,FOtherSeat as JGDFXW,FOtherStockholder as JGDFGD")
				    *//**wujunhua 2011-12-02 STORY 1789 *//*
				    .append(" from TMP_ETF_SJSJGINTERFACE  a Left Join (select FOtherSeat,FOtherStockholder,ftradenum,FBargainDate From "+
				    		pub.yssGetTableName("Tb_ETF_HBInterface")+" Where FPortCode in ("+operSql.sqlCodes(sportCode[i]) +
				    		") ) b On a.jgcjhm=b.ftradenum And a.jgcjrq=b.fbargaindate where JGJSBZ = 'Y' and (JGYWLB = 'LA' or JGYWLB = 'LC')")
				    *//**end*//*
		    		.append(" and  a.jgzqdm in(")
				    .append(dbl.sqlString(sParam[1])).append(",").append(dbl.sqlString(sParam[2])).append(") and a.JGCJRQ =")
				    .append(dbl.sqlDate(this.FBargainDate)).append(" order by a.JGCJHM");*/
				
				/**shashijie 2011-12-29 STORY 1789 修改导入数据的规则 */
				String strSql = " select FPortcode, FTradenum, FSecurityCode, FContractNum, FStockholderCode, FSeatNum," +
						" FBrokercode, FTradeamount, FTradeprice, FOtherseat, FOtherstockholder, FBargaintime, " +
						" FBargaindate, FRelanum, FTradetypecode, FTrademoney, FOpertype, FCheckstate, FCreator, " +
						" FCreatetime, FCheckuser, FChecktime From "+pub.yssGetTableName("Tb_ETF_HBInterface")+" b "+
						" left join(select jgwtxh,JGCJRQ,jggddm,jgjyxw," +
						" case when jgywlb = 'LA' then 'KS' else 'KB' end as jgywlb" +
						" from TMP_ETF_SJSJGINTERFACE  where jgjsbz = 'Y'" + 
						" and JGJSSL <> 0 and jgzqdm in ("+dbl.sqlString(sParam[1]) + 
						") and jgywlb in ('LA','LC')" +
						
						//由于中行的SJSJG库中除了有恒生ETF的申赎数据，还包括了联接基金的申赎数据；
						//这样同一股东（jggddm）、席位（jgjyxw）、合同序号（jgwtxh）会对应出多笔交易记录数，会导致导入文件时报数据重复的错误
						//因此这里将结算账户作为筛选条件，恒指ETF结算账户为999448，由于系统界面没有可维护的地方，时间关系此处先固定写死结算账户代码
						" and jgjszh = '999448'" + 
						" union all " +
						//份额记录 UNION ALL 资金记录
						" select jgwtxh,JGCJRQ,jggddm,jgjyxw," +
						" case when jgywlb = 'LA' then 'ZS' else 'ZB' end as jgywlb" +
						" from TMP_ETF_SJSJGINTERFACE  where jgjsbz = 'Y'" + 
						" and JGJSSL <> 0 and jgzqdm in ("+dbl.sqlString(sParam[1]) + 
						") and jgywlb in ('LA','LC')" +
						" and jgjszh = '999448'" + //写死原因同上
						") a " +
						" on a.jgwtxh = b.fcontractnum and a.JGCJRQ = b.FBargaindate and a.jgjyxw = b.fotherseat" + 
						" and a.jggddm = b.fotherstockholder and a.jgywlb = b.ftradetypecode" + 
						" where b.FPortCode in ("+operSql.sqlCodes(sportCode[i]) + 
						") and b.FBargaindate = " +
						dbl.sqlDate(this.FBargainDate) + 
						" and (a.jgwtxh is not null and a.JGCJRQ is not null" + 
						" and a.jggddm is not null and a.jgjyxw is not null" + 
						" and a.jgywlb is not null)";
				/**end*/
				
				rs = dbl.openResultSet(strSql);
				buff.delete(0,buff.length());
				
				buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_JGInterface"));
				buff.append("(FPortCode,FTradeNum,FSecurityCode,FContractNum,FStockholderCode,FSeatNum,FBrokerCode,FTradeAmount,FTradePrice,");
				buff.append(" FOtherSeat,FOtherStockholder,FBargainTime,FBargainDate,FRelaNum,FTradeTypeCode,FTradeMoney,FOperType,FCheckState,");
				buff.append(" FCreator,FCreateTime,FCheckUser,FCheckTime)").append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				pst = dbl.openPreparedStatement(buff.toString());
				buff.delete(0,buff.length());
				
				while(rs.next()){
					
					/**shashijie 2011-12-29 STORY 1789 修改导入数据的规则 */
					pst.setString(1,sportCode[i]);//组合代码
					pst.setString(2,rs.getString("FTradeNum"));//成交编号
					pst.setString(3,rs.getString("FSecurityCode"));// 证券代码
					pst.setString(4, rs.getString("FContractNum"));//合同序号
					pst.setString(5,rs.getString("FStockholderCode"));//股东代码			
					pst.setString(6,rs.getString("FSeatNum"));//交易席位
					pst.setString(7,rs.getString("FBrokerCode"));//券商代码
					pst.setDouble(8,rs.getDouble("FTradeAmount"));//成交数量
					pst.setDouble(9,rs.getDouble("FTradePrice"));//成交价格
					pst.setString(10,rs.getString("FOtherSeat"));//对方席位
					pst.setString(11,rs.getString("FOtherStockholder"));//对方股东
					pst.setString(12," ");//成交时间
					pst.setDate(13,rs.getDate("FBargainDate"));//成交日期
					if (rs.getString("FRelaNum").equalsIgnoreCase(sParam[1])) {
						iLinkNum++;
					}
					pst.setInt(14,iLinkNum); // 关联编号
					pst.setString(15,rs.getString("FTradeTypeCode"));//成交类型
					pst.setDouble(16,rs.getDouble("FTradeMoney"));//成交金额
					
					/*if (rs.getString("JGZQDM").equalsIgnoreCase(sParam[1])) {
						pst.setString(17, "2ndcode"); // 业务标志
					} else if (rs.getString("JGZQDM").equalsIgnoreCase(sParam[2])) {
						pst.setString(17, "cashcode"); // 业务标志
					} else {
						pst.setString(17, "seccode"); // 业务标志
					}*/
					
					pst.setString(17,rs.getString("FOperType")); // 业务标志
					
					pst.setInt(18,1);
					pst.setString(19,pub.getUserCode());
					pst.setString(20,YssFun.formatDatetime(new java.util.Date()));
					pst.setString(21,pub.getUserCode());
					pst.setString(22,YssFun.formatDatetime(new java.util.Date()));
					/**end*/
					
					pst.addBatch();
				}
				pst.executeBatch();
				dbl.closeStatementFinal(pst);
				dbl.closeResultSetFinal(rs);
				
				// 向TA 交易数据表Tb_XXX_TA_Trade中插入数据
				// 插入数据前，先删除数据，条件： 日期和组合
				sSql = " delete from " + pub.yssGetTableName("Tb_TA_Trade")
						+ " where FTradeDate =" + dbl.sqlDate(this.FBargainDate)
						+ " and FPortCode =" + dbl.sqlString(sportCode[i])
						+ " and FSellType in ('01','02')";

				dbl.executeSql(sSql);

				String insertTASql = " insert into "
						+ pub.yssGetTableName("Tb_TA_Trade")
						+ "(FNum,FTradeDate,FMarkDate,FPortCode,FPortClsCode,FSellNetCode,FSellType,FCuryCode,FAnalysisCode1,FAnalysisCode2,"
						+ "FAnalysisCode3,FSellMoney,FBeMarkMoney,FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FCashAccCode,"
						+ "FConfimDate,FSettleDate,FSettleMoney,FPortCuryRate,FBaseCuryRate,FSettleState,FDesc,FCheckState,FCreator,"
						+ "FCreateTime,FCheckUser,FCheckTime,FConvertNum,FCashRepAmount,FCashBalanceDate,FCashReplaceDate) " +
						" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
				pst = dbl.openPreparedStatement(insertTASql);

				sSql = " select * from (select sum(FTradeAmount) as FTradeAmountAll,  fportcode, FCashAccCode, FTradeTypeCode"
						+ " from (select gh.*, pa.fcashacccode from "
						+ pub.yssGetTableName("Tb_ETF_JGInterface")
						+ " gh join (select FOneGradeMktCode, fportcode, FCashAccCode from "
						+ pub.yssGetTableName("Tb_ETF_Param")//参数设置表
						+ " where FCheckState = 1) pa on gh.fportcode = pa.fportcode "
						+ " where gh.FPortCode = "
						+ dbl.sqlString(sportCode[i])
						+ " and gh.fopertype = '2ndcode' and gh.fbargaindate ="
						+ dbl.sqlDate(this.FBargainDate)
						+ " ) tt"
						+ " group by fportcode ,FCashAccCode, FTradeTypeCode ) aa "
						+ " left join (select FPortCode as Fport, FPortCury,FAssetCode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")//组合设置表
						+ " where FCheckState=1) pf on aa.Fportcode = pf.Fport";

				rs = dbl.openResultSet(sSql);

				// --------------------拼接交易编号---------------------
				strNumDate = YssFun.formatDatetime(this.FBargainDate).substring(0, 8);
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
							this.FBargainDate, rs.getString("FPortCury"),
							sportCode[i], YssOperCons.YSS_RATE_BASE);

					EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
					rateOper.setYssPub(pub);
					rateOper.getInnerPortRate(this.FBargainDate, rs
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
							+ pub.yssGetTableName("Tb_ETF_JGInterface")//过户库
							+ " where FPortCode = "
							+ dbl.sqlString(sportCode[i])
							+ " and FOperType = 'cashcode' and FBargaindate = "
							+ dbl.sqlDate(this.FBargainDate) + " and FTradeTypeCode ="
							+ (rs.getString("FTradeTypeCode").equalsIgnoreCase("KS") ? "'ZS'" : "'ZB'")
							+ " group by FPortCode,FTradeTypeCode";
					rr = dbl.openResultSet(sSql);
					if (rr.next()) {
						dCashRepAmount = rr.getDouble("FTotalMoney");// 现金替代金额
					}
					dbl.closeResultSetFinal(rr);
					// ----------------------------end-----------------------------//
					pst.setString(1, strNumDate); // 编号
					pst.setDate(2, YssFun.toSqlDate(this.FBargainDate)); // 交易日期
					pst.setDate(3, YssFun.toSqlDate(this.FBargainDate)); // 基准日期
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
	                taCashAccLink.setStartDate(YssFun.formatDate(this.FBargainDate));
	                taCashAccOper.setLinkAttr(taCashAccLink);
	                ArrayList reList = taCashAccOper.getLinkInfoBeans();
	                if (reList != null) {
	                	strCashAcctCode = ( (CashAccountBean) reList.get(0)).getStrCashAcctCode();
	                }
					//------------------end--------------------------------------------//
					pst.setString(18,strCashAcctCode.trim().length()>0?strCashAcctCode:" "); // 现金帐户
					//STORY #1789 QDV4中行2011年10月25日01_A panjunfang modify 20111124
					TaTradeBean ta = new TaTradeBean();
					ta.setYssPub(pub);
					confirmDate = ta.getConfirmDay(" ", rs.getString("FAssetCode"), sportCode[i], 
												rs.getString("FTradeTypeCode").equalsIgnoreCase("KS") ? "01" : "02", 
														rs.getString("FPortCury"), this.FBargainDate);
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
							sRowStr = (rr.getString("FSGCashBalHoliday") == null ? rr.getString("FHolidaysCode") : 
								rr.getString("FSGCashBalHoliday")) + "\t" + rr.getInt("FSGBalanceOver") + "\t" + YssFun.formatDate(this.FBargainDate);
						}else{//赎回
							sRowStr = (rr.getString("FSHCashBalHoliday") == null ? rr.getString("FHolidaysCode") : 
								rr.getString("FSHCashBalHoliday")) + "\t" + rr.getInt("FSHBalanceOver") + "\t" + YssFun.formatDate(this.FBargainDate);
						}
						holiday.parseRowStr(sRowStr);
						CashBalanceDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//现金差额结转日期
						if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){//申购
							sRowStr = (rr.getString("FSGRepCashHoliday") == null ? rr.getString("FHolidaysCode") : 
								rr.getString("FSGRepCashHoliday")) + "\t" + rr.getInt("FSGReplaceOver") + "\t" + YssFun.formatDate(this.FBargainDate);
						}else{//赎回
							sRowStr = (rr.getString("FSHRepCashHoliday") == null ? rr.getString("FHolidaysCode") : 
								rr.getString("FSHRepCashHoliday")) + "\t" + rr.getInt("FSHReplaceOver") + "\t" + YssFun.formatDate(this.FBargainDate);
						}
						holiday.parseRowStr(sRowStr);
						CashReplaceDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));*///现金替代结转日期
						/**end shashijie 2013-1-6 STORY 3328 */
						
						/**shashijie 2013-1-6 STORY 3328 使用新获取工作日方法,考虑境内境*/
						if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){//申购
							// 现金差额结转日期
							CashBalanceDate = YssFun.toSqlDate(getWorkDayMakeBack(this.FBargainDate,
									paramSet.getSSGBalanceOver(),(String)paramSet.getHoildaysRela().get("sgbalanceover"),
									paramSet.getsSGBalanceOver2(),(String)paramSet.getHoildaysRela().get("sgbalanceover2"))
									);
							// 现金替代结转日期
							CashReplaceDate = YssFun.toSqlDate(getWorkDayMakeBack(this.FBargainDate,
									paramSet.getSSGReplaceOver(),(String)paramSet.getHoildaysRela().get("sgreplaceover"),
									paramSet.getsSGReplaceOver2(),(String)paramSet.getHoildaysRela().get("sgreplaceover2"))
									);
						}else {
							CashBalanceDate = YssFun.toSqlDate(getWorkDayMakeBack(this.FBargainDate,
									paramSet.getSSHBalanceOver(),(String)paramSet.getHoildaysRela().get("shbalanceover"),
									paramSet.getsSHBalanceOver2(),(String)paramSet.getHoildaysRela().get("shbalanceover2"))
									);
							
							CashReplaceDate = YssFun.toSqlDate(getWorkDayMakeBack(this.FBargainDate,
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
	
	/**获取导入文件的交易日期 
	 * @author shashijie ,2012-1-4 , STORY 1789 
	 */
	private Date getBargainDate() throws YssException {
		Date dDate = null;//交易日
		ResultSet rs = null;
		try {
			String sqlString = " Select distinct(a.jgcjrq) From Tmp_Etf_Sjsjginterface a " + 
							" WHERE a.JGYWLB IN ('LA','LC')";
			rs = dbl.openResultSet(sqlString);
			if (rs.next()) {
				dDate = rs.getDate("jgcjrq");//成交日期
			}
		} catch (Exception e) {
			throw new YssException("获取导入文件的交易日期 出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dDate;
	}
	
	/**shashijie 2012-1-4 
	 * @return 获取fBargainDate的值
	 */
	public Date getFBargainDate() {
		return FBargainDate;
	}
	
	/**shashijie 2012-1-4 
	 * @param 设置fBargainDate为fBargainDate的值
	 */
	public void setFBargainDate(Date fBargainDate) {
		FBargainDate = fBargainDate;
	}
	
	/**获取工作日
	 * @param sHolidayCode 节假日代码
	 * @param dDate 日期
	 * @param dayInt 当天的偏离天数
	 * @author shashijie ,2012-01-04 , STORY 1789
	 */
	private Date getWorkDayByWhere(String sHolidayCode, Date dDate, int dayInt) throws YssException {
		Date mDate = null;//到推出的补票工作日
		//公共获取工作日类
		BaseOperDeal operDeal = new BaseOperDeal();
        operDeal.setYssPub(pub);
        mDate = operDeal.getWorkDay(sHolidayCode, dDate, dayInt);
        return mDate;
	}
	
	/**获取工作日通过境外节假日
	 * @author shashijie ,2012-1-4 , STORY 1789 
	 */
	private Date getWorkDay(Date sDate, int day) throws YssException {
		ETFParamSetAdmin paramSetAdmin = new ETFParamSetAdmin();//实例化参数设置的操作类
		paramSetAdmin.setYssPub(pub);//设置pub
		HashMap etfParam = paramSetAdmin.getETFParamInfo(this.sPort); // 根据已选组合代码用于获取相关ETF参数数据
		ETFParamSetBean paramSet = (ETFParamSetBean) etfParam.get(this.sPort);//根据组合代码获取参数设置的实体bean
		//T-1日工作日
		//取现金替代结转对应的节假日群
		Date oldWork = getWorkDayByWhere((String)paramSet.getHoildaysRela().get("sgreplaceover"), sDate, day);
		return oldWork;
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




















