package com.yss.main.etfoperation.etfaccbook.GcAndJqpj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.etfaccbook.PretValMktPriceAndExRate;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.StandingBookBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 此类做生成台账数据
 * 保存到（tb_ETF_standingbook）
 * @author fangjiang 2013.01.08 STORY #3402
 *
 */

public class CreateBook extends CtlETFAccBook{

	private HashMap mapBookData = null;//保存台账数据
	private HashMap buyDateMap = null;//保存当日有台帐数据的申赎日期，用于台帐数据删除 

	/**
	 * 此方法做解析前台传来数据
	 */
	public void initData(Date tradeDate, Date bsDate, String portCodes, ETFParamSetBean paramSet,
            PretValMktPriceAndExRate marketValue) {			
		super.initData(tradeDate, bsDate, portCodes, paramSet, marketValue);	
	}
	
	/**
	 * 处理业务的入口方法
	 * @throws YssException
	 */
	public void doManageAll() throws YssException{
		try{
			createStandindBookData(this.tradeDate); //获取台帐数据
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	
	/**
	 * 根据申赎数据和补票数据生成台帐数据，并放到集合中
	 * @param date
	 * @throws YssException 
	 */
	private void createStandindBookData(Date dDate) throws YssException {
		String sQuerySql = "";
		String sDelSql = "";
		String sInsertSql = "";
		
		ResultSet rs =null;//结果集
		int DataDirection =0; //数据方向
		StandingBookBean book = null;//台账实体bean		
		String sNum = "";
		String sPreNum = "";
		DecimalFormat df = new DecimalFormat("00000000");
		Date dTradeDate = null;
		
		Connection conn = null;//数据库连接
		boolean bTrans = true;//事物控制标识
		PreparedStatement pst = null;//预处理
		PreparedStatement pstDel = null;//删除预处理
		int i = 0;
		final int batchSize = 100;//预处理批量执行大小，每300条提交一次给数据库执行,modify by fangjiang 2013.05.20 BUG7955 QDV4建行2013年05月20日01_B  调成100，解决在woblogic环境下报错
		
		try{
			mapBookData = new HashMap();//实例化
			buyDateMap = new HashMap();
			
			dTradeDate = this.bsDate;
			if(null == dTradeDate){
				dTradeDate = YssFun.toDate("9998-12-31");
			}
			
			sQuerySql = buildQuerySql(dDate,dTradeDate);
 			rs = dbl.openResultSet(sQuerySql);
			
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);//设置为事物手动提交
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_standingbook"));//锁定表
			
			//插入数据前先要删除表中已有数据
			sDelSql = buildDelSql(dDate);
			pstDel = dbl.openPreparedStatement(sDelSql);
			
			//插入数据预处理
			sInsertSql = buildInsertSql();	
			pst = dbl.openPreparedStatement(sInsertSql);
			
			while(rs.next()){
				
				//根据申赎日期先删除台帐表中已有数据
				if(!buyDateMap.containsKey(rs.getDate("FBuyDate"))){
					//同一申赎日期执行一次删除
					pstDel.setDate(1, rs.getDate("FBuyDate"));
					pstDel.executeUpdate();
				}
				//将申赎日期放在hashmap中去重，避免同一申赎日期数据执行多次删除，影响效率
				buyDateMap.put(rs.getDate("FBuyDate"), rs.getDate("FBuyDate"));
				
				sNum = rs.getString("FNum");
				
				if(!sNum.equals(sPreNum)){
					if(sPreNum.length() > 0){
						//同一FNum数据初始化完成后（包括申赎、权益、补票等数据均完成初始化）
						//由于查询sql中按FNum进行排序，因此当前FNum与前一FNum不一致的话，说明前一FNum初始化完成
						//为插入预处理赋值
						setPstValue(pst,(StandingBookBean)mapBookData.get(sPreNum),dDate);						
						//预处理赋值后及时将该key=FNum从hashmap中移除，避免台帐数据量大的情况下导致内存溢出
						mapBookData.remove(sPreNum);
					}
					sPreNum = sNum;
				}
				
				if(mapBookData.containsKey(sNum)){
					book = (StandingBookBean) mapBookData.get(sNum);
				}else{
					book = new StandingBookBean();//实例化
					DataDirection = Integer.parseInt(rs.getString("FBS").equals("B") ? "1" : "-1");//方向
					book.setNum(rs.getString("FNum"));//申请编号
					book.setTradeNum(rs.getString("FTradeNum"));//成交编号
					book.setBuyDate(rs.getDate("FBuyDate"));//申赎日期
					book.setBs(rs.getString("FBs"));//台账类型
					book.setPortCode(rs.getString("FPortCode"));//组合代码
					book.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
					book.setStockHolderCode(rs.getString("FStockHolderCode"));//投资者
					book.setBrokerCode(rs.getString("FBrokerCode"));//券商
					book.setSeatCode(rs.getString("FSeatCode"));//交易席位
					book.setMakeUpAmount(YssD.mul(rs.getDouble("FReplaceAmount"),DataDirection));//申赎数量
					book.setUnitCost(rs.getDouble("FUnitCost"));//单位成本
					book.setReplaceCash(YssD.mul(rs.getDouble("FHReplaceCash"),DataDirection));//替代金额
					book.setCanReplaceCash(YssD.mul(rs.getDouble("FHCReplaceCash"),DataDirection));//可退替代款
					book.setExchangeRate(rs.getDouble("fexchangerate"));
				}

				if (null != rs.getDate("fexrightdate")
						&& rs.getString("FRefNum").length() == 16) {// 权益数据
					if(null == book.getExRightDate() || //如果权益日期为null
							YssFun.dateDiff(book.getExRightDate(),//或者权益日期比先前大，则更新权益日期和汇率
												rs.getDate("FExRightDate")) > 0){
						book.setExRightDate(rs.getDate("FExRightDate"));//权益日期
						book.setRightRate(rs.getDouble("FRightRate"));//权益汇率
					}
					book.setSumAmount(YssD.add(book.getSumAmount(),
							YssD.mul(rs.getDouble("FSumAmount"), DataDirection)));// 总数量
					book.setRealAmount(YssD.add(book.getRealAmount(),
							YssD.mul(rs.getDouble("FRealAmount"), DataDirection)));// 实际数量
					book.setTotalInterest(YssD.add(book.getTotalInterest(),
							YssD.mul(rs.getDouble("FInterest"), DataDirection)));// 总派息原币
					book.setWarrantCost(YssD.add(book.getWarrantCost(),
							YssD.mul(rs.getDouble("FWarrantCost"), DataDirection)));// 权证价值原币
					book.setBBInterest(YssD.add(book.getBBInterest(),
							YssD.mul(rs.getDouble("FBBInterest"), DataDirection)));// 总派息本币
					book.setBBWarrantCost(YssD.add(book.getBBWarrantCost(),
							YssD.mul(rs.getDouble("FBBWarrantCost"),DataDirection)));// 权证价值本币
				}else if("gc".equals(rs.getString("FRefNum"))){//第一次补票数据
					book.setMakeUpDate1(rs.getDate("FMakeUpDate"));//第一次补票日期
					book.setMakeUpAmount1(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//第一次补票数量
					book.setMakeUpUnitCost1(rs.getDouble("fmakeunitcost"));//第一次补票单位成本
					book.setoMakeUpCost1(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//第一次补票总成本原币
					book.sethMakeUpCost1(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//第一次补票总成本本币
					book.setMakeUpRepCash1(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//第一次补票的应付替代款本币
					book.setCanMkUpRepCash1(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//第一次补票可退替代款本币
					book.setExRate1(rs.getDouble("fmakeuprate"));//第一次补票汇率
				}else if("1".equals(rs.getString("FRefNum"))){//第二次补票数据
					book.setMakeUpDate2(rs.getDate("FMakeUpDate"));//第二次补票日期
					book.setMakeUpAmount2(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//第二次补票数量
					book.setMakeUpUnitCost2(rs.getDouble("fmakeunitcost"));//第二次补票单位成本
					book.setoMakeUpCost2(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//第二次补票总成本原币
					book.sethMakeUpCost2(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//第二次补票总成本本币
					book.setMakeUpRepCash2(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//第二次补票的应付替代款本币
					book.setCanMkUpRepCash2(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//第二次补票可退替代款本币
					book.setExRate2(rs.getDouble("fmakeuprate"));//第二次补票汇率
				}else if("2".equals(rs.getString("FRefNum"))){//第三次补票数据
					book.setMakeUpDate3(rs.getDate("FMakeUpDate"));//第三次补票日期
					book.setMakeUpAmount3(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//第三次补票数量
					book.setMakeUpUnitCost3(rs.getDouble("fmakeunitcost"));//第三次补票单位成本
					book.setoMakeUpCost3(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//第三次补票总成本原币
					book.sethMakeUpCost3(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//第三次补票总成本本币
					book.setMakeUpRepCash3(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//第三次补票的应付替代款本币
					book.setCanMkUpRepCash3(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//第三次补票可退替代款本币
					book.setExRate3(rs.getDouble("fmakeuprate"));//第三次补票汇率
				}else if("3".equals(rs.getString("FRefNum"))){//第四次补票数据
					book.setMakeUpDate4(rs.getDate("FMakeUpDate"));//第四次补票日期
					book.setMakeUpAmount4(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//第四次补票数量
					book.setMakeUpUnitCost4(rs.getDouble("fmakeunitcost"));//第四次补票单位成本
					book.setoMakeUpCost4(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//第四次补票总成本原币
					book.sethMakeUpCost4(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//第四次补票总成本本币
					book.setMakeUpRepCash4(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//第四次补票的应付替代款本币
					book.setCanMkUpRepCash4(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//第四次补票可退替代款本币
					book.setExRate4(rs.getDouble("fmakeuprate"));//第四次补票汇率
				}else if("4".equals(rs.getString("FRefNum"))){//第五次补票数据
					book.setMakeUpDate5(rs.getDate("FMakeUpDate"));//第五次补票日期
					book.setMakeUpAmount5(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//第五次补票数量
					book.setMakeUpUnitCost5(rs.getDouble("fmakeunitcost"));//第五次补票单位成本
					book.setoMakeUpCost5(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//第五次补票总成本原币
					book.sethMakeUpCost5(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//第五次补票总成本本币
					book.setMakeUpRepCash5(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//第五次补票的应付替代款本币
					book.setCanMkUpRepCash5(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//第五次补票可退替代款本币
					book.setExRate5(rs.getDouble("fmakeuprate"));//第五次补票汇率
				}else if("99".equals(rs.getString("FRefNum"))){//强制处理数据
					book.setMustMkUpDate(rs.getDate("FMakeUpDate"));//强制处理日期
					book.setMustMkUpAmount(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//强制处理数量
					book.setMustMkUpUnitCost(rs.getDouble("fmakeunitcost"));//强制处理单位成本
					book.setoMustMkUpCost(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//强制处理总成本原币
					book.sethMustMkUpCost(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//强制处理总成本本币
					book.setMustMkUpRepCash(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//强制处理可退替代款原币
					book.setMustCMkUpRepCash(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//强制处理可退替代款本币
					book.setMustExRate(rs.getDouble("fmakeuprate"));//强制处理汇率
				}

				//剩余数量 = 替代数量 + 权益实际数量 - sum(各次补票数量)
				book.setRemaindAmount(YssD.sub(YssD.add(book.getMakeUpAmount(), 
															book.getRealAmount()),
												YssD.add(book.getMakeUpAmount1(),
														book.getMakeUpAmount2(),
														book.getMakeUpAmount3(),
														book.getMakeUpAmount4(),
														book.getMakeUpAmount5(),
														book.getMustMkUpAmount())));//剩余数量
				
				
				//if(0 == book.getRemaindAmount()){//如果补票完成，则更新应退合计    //edit by zhaoxianlin 20121024 需求更改 
				
					if("B".equals(book.getBs())){
						//应退合计（申购） = sum(各次补票应付替代款)
						book.setSumReturn(YssD.add(book.getMakeUpRepCash1(),
													book.getMakeUpRepCash2(), 
													book.getMakeUpRepCash3(),
													book.getMakeUpRepCash4(),
													book.getMakeUpRepCash5(),
													book.getMustMkUpRepCash()));
					}else{
						//应退合计（赎回） = 替代金额 +　sum(各次补票应付替代款)
						book.setSumReturn(YssD.add(book.getReplaceCash(), 
								YssD.add(book.getMakeUpRepCash1(),
										book.getMakeUpRepCash2(), 
										book.getMakeUpRepCash3(),
										book.getMakeUpRepCash4(),
										book.getMakeUpRepCash5(),
										book.getMustMkUpRepCash())));
					}

				//}
				if(null != rs.getDate("frefunddate") && 
						!"9998-12-31".equals(YssFun.formatDate(rs.getDate("frefunddate"), "yyyy-MM-dd"))){
					book.setRefundDate(rs.getDate("frefunddate"));
				}
				
				book.setGradeType1(book.getSecurityCode());//排序编号1
				book.setGradeType2(book.getStockHolderCode());//排序编号2
				book.setGradeType3(book.getTradeNum());//排序编号3
				book.setOrderCode(book.getSecurityCode() +"##"+book.getGradeType2()+
						"##"+book.getGradeType3());//排序编号
				book.setTradeNum(rs.getString("FTradeNum"));//成交编号
				
				mapBookData.put(sNum,book);//保存台账数据到hash表中
				
				if(i % batchSize == 0) {//每300条执行一次插入操作，提高性能
					pst.executeBatch();
				}
				i++;
			}
			
			if(null != sNum && sNum.length() > 0){
				//结果集最后一条数据，插入预处理赋值
				setPstValue(pst,(StandingBookBean)mapBookData.get(sNum),dDate);
				mapBookData.remove(sNum);
			}

			pst.executeBatch();//执行批处理

			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;	
		}catch (Exception e) {
			throw new YssException("生成台帐数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
			dbl.closeStatementFinal(pstDel);
		}
	}
	
	/**
	 * 插入台帐数据的sql
	 * @return
	 */
	private String buildInsertSql() throws YssException {
		StringBuffer buff = null;
		try{
			buff = new StringBuffer(2000);
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_standingbook"))
			.append("(FNum,FBuyDate,FBs,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,")
			.append(" FMakeUpAmount,FUnitCost,FReplaceCash,FCanReplaceCash,FExRightDate,FSumAmount, ")
			.append(" FRealAmount,FTotalInterest,")
			.append(" FWarrantCost,FBBInterest,FBBWarrantCost,FRightRate,FMakeUpDate1,FMakeUpAmount1, ")
			.append(" FMakeUpUnitCost1,FOMakeUpCost1,FHMakeUpCost1,")
			.append(" FMakeUpRepCash1,FCanMkUpRepCash1,FMakeUpDate2,FMakeUpAmount2,FMakeUpUnitCost2, ")
			.append(" FOMakeUpCost2,FHMakeUpCost2,FMakeUpRepCash2,")
			.append(" FCanMkUpRepCash2,FMakeUpDate3,FMakeUpAmount3,FMakeUpUnitCost3,FOMakeUpCost3, ")
			.append(" FHMakeUpCost3,FMakeUpRepCash3,FCanMkUpRepCash3,")
			.append(" FMakeUpDate4,FMakeUpAmount4,FMakeUpUnitCost4,FOMakeUpCost4,FHMakeUpCost4,FMakeUpRepCash4, ")
			.append(" FCanMkUpRepCash4,FMakeUpDate5,")
			.append(" FMakeUpAmount5,FMakeUpUnitCost5,FOMakeUpCost5,FHMakeUpCost5,FMakeUpRepCash5, ")
			.append(" FCanMkUpRepCash5,FMustMkUpDate,FMustMkUpAmount,")
			.append(" FMustMkUpUnitCost,FOMustMkUpCost,FHMustMkUpCost,FMustMkUpRepCash,FMustCMkUpRepCash, ")
			.append(" FRemaindAmount,FSumReturn,FRefundDate,")
			.append(" FExchangeRate,FOrderCode,FGradeType1,FGradeType2,FGradeType3,FExRate1,FExRate2,FExRate3,")
			.append(" FExRate4,FExRate5,FMustExRate,FFactExRate,FExRateDate,FMarkType,FRateType,FTradeNum,FCreator,FCreateTime,FDate)")
			.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,")//35
			.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,")//75
			.append(" ?,?,?,?,?,?,?,?,?)");
			
			return buff.toString();
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/**
	 * 删除历史台帐数据的sql
	 * @param dDate
	 * @return
	 * @throws YssException
	 */
	private String buildDelSql(Date dDate) throws YssException {
		StringBuffer buff = null;
		try{
			buff = new StringBuffer(300);
			
			//根据组合代码，申赎日期，台帐日期删除台账数据
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append(" where FportCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FBuyDate = ? ");
			buff.append(" and (FDate = ").append(dbl.sqlDate(dDate));
			//FDate 为新增字段，历史数据该字段值为1900-01-01 ，因此也需要一并删除
			buff.append(" or FDate = ").append(dbl.sqlDate(YssFun.toDate("1900-01-01")));
			buff.append(")");
			
			return buff.toString();
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}

	}

	/**
	 * 获取台帐数据的sql
	 * @return
	 * @throws YssException
	 */
	private String buildQuerySql(Date dDate, Date dTradeDate) throws YssException {
		StringBuffer buff = null;
		try{
			buff = new StringBuffer(2000);
			
			buff.append("select t1.fnum,t1.fportcode,t1.fsecuritycode,t1.fstockholdercode,t1.fbrokercode,")
				.append("t1.fseatcode,t1.fbs,t1.fbuydate,t1.freplaceamount,t1.fbraketnum,t1.funitcost,")
				.append("t1.foreplacecash,t1.fhreplacecash,t1.focreplacecash,t1.fhcreplacecash,")
				.append("t1.fexchangerate,t1.ftradenum,t1.fmarktype,")
				.append("t2.fmakeupdate,t2.funitcost as fmakeunitcost,t2.fmakeupamount,")
				.append("t2.fopreplacecash,t2.fhpreplacecash,t2.focpreplacecash,t2.fhcpreplacecash,")
				.append("t2.fexrightdate,t2.fsumamount,t2.frealamount,t2.finterest,t2.fbbinterest,")
				.append("t2.fwarrantcost,t2.fbbwarrantcost,t2.frefunddate,t2.fdatamark,")
				.append("t2.fdatadirection,t2.fsettlemark,t2.frefnum,t2.frightrate,")
				.append("t2.fexchangerate as fmakeuprate,t2.fhmakeupcost,t2.fomakeupcost,")
				.append("t2.ftradeunitcost,t2.ffeeunitcost,t2.ffactamount,t2.fdeletemark,")
				.append("t2.fdeflationamount from ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t1 left join ")
				
				.append(" (select ref1.* ")
				.append(" from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" ref1 where to_char(fexrightdate, 'yyyymmdd') = '99981231' ")
				.append(" and fmakeupdate <= ").append(dbl.sqlDate(dDate))
				.append(" union all ")
				.append(" select ref2.* ")
				.append(" from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" ref2 ")
				.append(" join (select fnum, ")
				.append(" fexrightdate, ")
				.append(" max(to_date(substr(frefnum, 9), 'yyyymmdd')) as fdate ")
				.append(" from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" where to_char(fmakeupdate, 'yyyymmdd') = '99981231' ")
				.append(" and to_date(substr(frefnum, 9), 'yyyymmdd') <= ").append(dbl.sqlDate(dDate))
				.append(" group by fnum, fexrightdate) b on ref2.fnum = b.fnum ")
				.append(" and ref2.fexrightdate = b.fexrightdate ")
				.append(" and to_date(substr(ref2.frefnum, 9),'yyyymmdd') =  b.fdate ")
				.append(") t2 on t1.fnum = t2.fnum")
				
				.append(" where t1.fportcode in (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") and (t1.fbuydate = ").append(dbl.sqlDate(dTradeDate))
				.append(" or exists (select fmakeupdate,fexrightdate from ")
				.append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" where (fmakeupdate = ").append(dbl.sqlDate(dDate))//业务当天有补票
				.append(" or ").append(dbl.sqlSubStr("frefnum", "9", "8"))  
				.append(" = ").append(dbl.sqlString(YssFun.formatDate(dDate, "yyyyMMdd")))//业务当天有权益（或权证价值更新）
				.append(") and frefnum <> 'gc'")
				//modify by fangjiang 2013.05.30 BUG 8046
				.append(" and ").append(dbl.sqlSubStr("FNum", "2", "8"))
				.append(" = ").append(dbl.sqlFormat("t1.fbuydate", "yyyyMMdd"))
				//.append(" and fnum = t1.fnum")
				//end by fangjiang 2013.05.30 BUG 8046
				.append(" )) order by t1.fnum");//按编号排序，便于后续数据插入处理
			
			return buff.toString();
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/**
	 * 台帐数据插入预处理赋值
	 * @param pst
	 * @param book
	 * @param dDate
	 * @throws YssException
	 */
	private void setPstValue(PreparedStatement pst, StandingBookBean book, Date dDate) throws YssException {
		try{
			//以下为预处理赋值
			pst.setString(1,book.getNum());
			pst.setDate(2,YssFun.toSqlDate(book.getBuyDate()));
			pst.setString(3,book.getBs());
			pst.setString(4,book.getPortCode());
			pst.setString(5,book.getSecurityCode());
			pst.setString(6,book.getStockHolderCode());
			pst.setString(7,book.getBrokerCode());
			pst.setString(8,book.getSeatCode());
			pst.setDouble(9,book.getMakeUpAmount());
			pst.setDouble(10,book.getUnitCost());
			pst.setDouble(11,book.getReplaceCash());
			pst.setDouble(12,book.getCanReplaceCash());
			pst.setDate(13,YssFun.toSqlDate(book.getExRightDate()!=null?book.getExRightDate():YssFun.toDate("9998-12-31")));
			pst.setDouble(14,book.getSumAmount());
			pst.setDouble(15,book.getRealAmount());
			pst.setDouble(16,book.getTotalInterest());
			pst.setDouble(17,book.getWarrantCost());
			pst.setDouble(18,book.getBBInterest());
			pst.setDouble(19,book.getBBWarrantCost());
			pst.setDouble(20,book.getRightRate());
			
			pst.setDate(21,YssFun.toSqlDate(book.getMakeUpDate1()!=null?book.getMakeUpDate1():YssFun.toDate("9998-12-31")));
			pst.setDouble(22,book.getMakeUpAmount1());
			pst.setDouble(23,book.getMakeUpUnitCost1());
			pst.setDouble(24,book.getoMakeUpCost1());
			pst.setDouble(25,book.gethMakeUpCost1());
			pst.setDouble(26,book.getMakeUpRepCash1());
			pst.setDouble(27,book.getCanMkUpRepCash1());
			
			pst.setDate(28,YssFun.toSqlDate(book.getMakeUpDate2()!=null?book.getMakeUpDate2():YssFun.toDate("9998-12-31")));
			pst.setDouble(29,book.getMakeUpAmount2());
			pst.setDouble(30,book.getMakeUpUnitCost2());
			pst.setDouble(31,book.getoMakeUpCost2());
			pst.setDouble(32,book.gethMakeUpCost2());
			pst.setDouble(33,book.getMakeUpRepCash2());
			pst.setDouble(34,book.getCanMkUpRepCash2());
			
			pst.setDate(35,YssFun.toSqlDate(book.getMakeUpDate3()!=null?book.getMakeUpDate3():YssFun.toDate("9998-12-31")));
			pst.setDouble(36,book.getMakeUpAmount3());
			pst.setDouble(37,book.getMakeUpUnitCost3());
			pst.setDouble(38,book.getoMakeUpCost3());
			pst.setDouble(39,book.gethMakeUpCost3());
			pst.setDouble(40,book.getMakeUpRepCash3());
			pst.setDouble(41,book.getCanMkUpRepCash3());
			
			pst.setDate(42,YssFun.toSqlDate(book.getMakeUpDate4()!=null?book.getMakeUpDate4():YssFun.toDate("9998-12-31")));
			pst.setDouble(43,book.getMakeUpAmount4());
			pst.setDouble(44,book.getMakeUpUnitCost4());
			pst.setDouble(45,book.getoMakeUpCost4());
			pst.setDouble(46,book.gethMakeUpCost4());
			pst.setDouble(47,book.getMakeUpRepCash4());
			pst.setDouble(48,book.getCanMkUpRepCash4());
			
			pst.setDate(49,YssFun.toSqlDate(book.getMakeUpDate5()!=null?book.getMakeUpDate5():YssFun.toDate("9998-12-31")));
			pst.setDouble(50,book.getMakeUpAmount5());
			pst.setDouble(51,book.getMakeUpUnitCost5());
			pst.setDouble(52,book.getoMakeUpCost5());
			pst.setDouble(53,book.gethMakeUpCost5());
			pst.setDouble(54,book.getMakeUpRepCash5());
			pst.setDouble(55,book.getCanMkUpRepCash5());
			
			pst.setDate(56,YssFun.toSqlDate(book.getMustMkUpDate()!=null?book.getMustMkUpDate():YssFun.toDate("9998-12-31")));
			pst.setDouble(57,book.getMustMkUpAmount());
			pst.setDouble(58,book.getMustMkUpUnitCost());
			pst.setDouble(59,book.getoMustMkUpCost());
			pst.setDouble(60,book.gethMustMkUpCost());
			pst.setDouble(61,book.getMustMkUpRepCash());
			pst.setDouble(62,book.getMustCMkUpRepCash());
			
			pst.setDouble(63,book.getRemaindAmount());
			pst.setDouble(64,book.getSumReturn());
			pst.setDate(65,YssFun.toSqlDate(book.getRefundDate()!=null?book.getRefundDate():YssFun.toDate("9998-12-31")));
			pst.setDouble(66,book.getExchangeRate());
			pst.setString(67,book.getOrderCode());
			pst.setString(68,book.getGradeType1());
			pst.setString(69,book.getGradeType2());
			pst.setString(70,book.getGradeType3());
			pst.setDouble(71,book.getExRate1());
			pst.setDouble(72,book.getExRate2());
			pst.setDouble(73,book.getExRate3());
			pst.setDouble(74,book.getExRate4());
			pst.setDouble(75,book.getExRate5());
			pst.setDouble(76,book.getMustExRate());
	
			pst.setDouble(77,book.getFactExRate());
	
			pst.setDate(78,YssFun.toSqlDate(book.getExRateDate()!=null?book.getExRateDate():YssFun.toDate("9998-12-31")));
			
			pst.setString(79,book.getMarkType());
			pst.setString(80,book.getRateType());
			pst.setString(81,book.getTradeNum());
			pst.setString(82,pub.getUserCode());
			pst.setString(83,YssFun.formatDatetime(new Date()));
			pst.setDate(84,YssFun.toSqlDate(dDate));
			
			pst.addBatch();//增加批处理
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}
	}
	
}
