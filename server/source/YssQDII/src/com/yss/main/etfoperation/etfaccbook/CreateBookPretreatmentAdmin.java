package com.yss.main.etfoperation.etfaccbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class CreateBookPretreatmentAdmin extends CtlETFAccBook{
	public CreateBookPretreatmentAdmin() {
	}
	
	/**
	 * 插入前一日申购赎回的补票数据，权益数据和强制处理数据
	 * @param dDate 日期
	 * @param sPortCodes 组合代码
	 * @param tradeSettleDetailYesDate 保存数据的集合
	 * @param paramSet 参数设置实体bean
	 * @throws YssException
	 */
	public void insertYesDateData(Date dDate,String sPortCodes,ArrayList tradeSettleDetailYesDate,ETFParamSetBean paramSet) throws YssException{
		StringBuffer buff =null;//做拼接SQL语句
		Connection conn = null;//声明数据库连接
		PreparedStatement pst = null;//声明预处理
		boolean bTrans = true;//失误控制标识
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		ArrayList alTradeSettleDelRef = null;//保存交易关联明细
		String sRowStr ="";
		Date yesDate = null;//操作日当天的钱一个工作日
		EachExchangeHolidays holiday = null;//节假日代码的类
		ResultSet rs = null;
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			sRowStr = paramSet.getSHolidayCode() + "\t" + -1 + "\t" + YssFun.formatDate(dDate);//拼接参数：节假日代码+当天的偏离天数+操作日期
			
			holiday.parseRowStr(sRowStr);//传入参数进行解析
			yesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
			
			buff = new StringBuffer(500);//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置事物手动提交

			if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){
				//插入数据前先删除数据，条件：补票日期为当天；关联编号为 2--第二次补票，3-强制处理；并且申请编号与另一张表关联到的数据
				buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref")).append(" t1 ");
				buff.append(" where FMakeUpDate =").append(dbl.sqlDate(dDate)).append(" and FRefnum in('2','3')");
				buff.append(" and exists (select FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
				buff.append(" t2 where FBuyDate < ").append(dbl.sqlDate(dDate));
				buff.append(" and ftradenum <> 'totaldata'");
				buff.append(" and FPortCode in (").append(this.operSql.sqlCodes(sPortCodes)).append(")");
				buff.append(" and t1.fnum = t2.FNum)");
			}else{
				//插入数据前先删除数据，条件：补票日期为当天；关联编号为 2--第二次补票，3-强制处理；并且申请编号与另一张表关联到的数据
				buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref")).append(" t1 ");
				buff.append(" where FMakeUpDate =").append(dbl.sqlDate(dDate)).append(" and FRefnum in('2','3')");
				buff.append(" and exists (select FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
				buff.append(" t2 where FBuyDate between ").append(dbl.sqlDate(yesDate)).append(" and ").append(dbl.sqlDate(dDate));
				buff.append(" and FPortCode in (").append(this.operSql.sqlCodes(sPortCodes)).append(")");
				buff.append(" and t1.fnum = t2.FNum)");
			}
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" select c.*,d.fmakeupdate from (select a.FNum ");
			buff.append(" from ").append(pub.yssGetTableName("Tb_Etf_Tradestldtl"));
			buff.append(" a join (select * from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append(" t1 where t1.fexrightdate <> ").append(dbl.sqlDate(YssFun.toDate("9998-12-31")));
			buff.append(" and t1.frefnum = '2' and t1.fmakeupdate = ").append(dbl.sqlDate(YssFun.toDate("9998-12-31"))).append(" ) b on a.fnum = b.FNum");
			buff.append(" where a.FportCode in(").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			buff.append(" and a.FBuyDate <").append(dbl.sqlDate(dDate));
			buff.append(" and a.ftradenum <> 'totaldata') c left join (select * from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append(" t2 where t2.frefnum = '3') d on c.FNum = d.FNum");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append(" where FRefNum ='2' and FNum = ?");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){
				if(rs.getDate("fmakeupdate")== null){
					pst.setString(1,rs.getString("FNum"));
					
					pst.executeUpdate();
				}
			}
			
			dbl.closeStatementFinal(pst);
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append("(FNum,FRefNum,FMakeUpDate,FUnitCost,FMakeUpAmount,FOMakeUpCost,FHMakeUpCost,FOPReplaceCash,");
			buff.append(" FHPReplaceCash,FOCPReplaceCash,FHCPReplaceCash,FExRightDate,FSumAmount,FRealAmount,FInterest,");
			buff.append(" FBBInterest,FBBWarrantCost,FRightRate,FWarrantCost,FRemaindAmount,FOCRefundSum,FHCRefundSum,");
			buff.append(" FRefundDate,FDataMark,FDataDirection,FSettleMark,FExchangeRate,FCreator,FCreateTime"+
			/**shashijie 2011-11-03 STORY 1789 */
			" ,FDeflationAmount,");
			/**end*/
			/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
		    buff.append(" FOtherRight,FBBOtherRight)");
		    buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		    /**end---huhuichao 2013-8-8 STORY  4276  */
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			for(int i = 0;i < tradeSettleDetailYesDate.size();i++ ){//循环保存当天的上一个工作日的数据的集合
				etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetailYesDate.get(i);//获取交易结算明细bean
				alTradeSettleDelRef = etfTradeSettleDetail.getAlTradeSettleDelRef();//获取保存交易结算明细关联bean的集合
				for(int j=0; j< alTradeSettleDelRef.size();j++){//循环集合
					tradeSettleDelRef = (ETFTradeSettleDetailRefBean) alTradeSettleDelRef.get(j);//获取交易结算明细关联bean
					System.out.println(etfTradeSettleDetail.getSecurityCode()+ "------"+tradeSettleDelRef.getNum()+"----"+tradeSettleDelRef.getRefNum()+"odsdsadsadsad");
					this.setTradeDelRef(tradeSettleDelRef,pst);//为预处理赋值
				}
				pst.executeBatch();
			}
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置事物为自动提交
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("插入前一日申购赎回的补票数据，权益数据和强制处理数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * STORY #1789 恒指ETF QDV4中行2011年10月25日01_A
	 * 插入申赎数据到 ETF申赎数据明细表（TB_ETF_TRADESTLDTL）
	 */
	public void insertTradeData(Date dDate,String sPortCodes,ArrayList tradeSettleDetail) throws YssException {
		StringBuffer buff =null;//做拼接SQL语句
		Connection conn = null;//数据库连接声明
		PreparedStatement pstTrade = null;//交易结算明细数据的预处理声明
		final int batchSize = 499;//预处理批量执行大小，每500条提交一次到数据库执行
		boolean bTrans = true;//事物控制标识
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		try{
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为事物手动提交
			
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_tradestldtl"));//给表加锁

			buff = new StringBuffer(500);//实例化
			buff.append(" insert into ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append("(FNum,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,FBs,FBuyDate,")
				.append(" FReplaceAmount,FBraketNum,FUnitCost,FOReplaceCash,FHReplaceCash,FOCReplaceCash,")
				.append(" FHCReplaceCash,FExchangeRate,FTradeNum,FMarkType,FCreator,FCreateTime)")
				.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pstTrade = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			//插入数据前先执行删除,删除条件：组合代码，申赎日期
			buff.append(" delete from ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" where FPortCode in(").append(this.operSql.sqlCodes(sPortCodes)).append(")")
				.append(" and FBuyDate = ").append(dbl.sqlDate(dDate));
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			//循环保存申赎明细数据的集合，执行插入
			for(int i = 0;i < tradeSettleDetail.size();i++ ){
				etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);
				this.setTradeDetail(etfTradeSettleDetail,pstTrade);//为申赎明细表赋值

				pstTrade.addBatch();//增加明细数据的批处理
				if(i % batchSize == 0){//每500条执行一次数据库操作，提高性能
					pstTrade.executeBatch();
				}
			}
			pstTrade.executeBatch();//执行明细数据的批处理
			
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("插入当天申购赎回数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pstTrade);
		}
	}
	

	/**
	 * STORY #1789 恒指ETF QDV4中行2011年10月25日01_A
	 * 插入补票和权益数据到ETF补票数据明细表（TB_ETF_TRADSTLDTLREF）
	 * @param dDate 补票（权益）日期
	 * @param sPortCodes 组合代码
	 * @param tradeSettleDetail 存放补票（权益）数据
	 * @param sDataType 数据类型（Right：权益数据     MakeUp：补票数据）
	 * @throws YssException
	 */
	public void insertTradeRefData(Date dDate, String sPortCodes,
			ArrayList tradeSettleDetail, String sDataType,String MakeUpNo) throws YssException {
		StringBuffer buff =null;//做拼接SQL语句
		Connection conn = null;//声明数据库连接
		PreparedStatement pst = null;//声明预处理
		boolean bTrans = true;//失误控制标识
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		ArrayList alTradeSettleDelRef = null;//保存交易关联明细
		ResultSet rs = null;
		final int batchSize = 499;//预处理批量执行大小，每500条提交一次到数据库执行
		try{		
			buff = new StringBuffer(500);//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置事物手动提交

			String sqlDeleteFilter = sDataType.equalsIgnoreCase("MakeUp") ? " t1.FMakeUpDate = " : " t1.FExrightDate = ";
			//插入数据前先删除数据，条件：补票、权益日期，组合（通过ETF申赎数据明细表关联出组合）
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" t1 where ").append(sqlDeleteFilter).append(dbl.sqlDate(dDate))
				.append(" and FRefNum = ").append(dbl.sqlString(MakeUpNo))
				.append(" and exists (select FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t2 where FPortCode in (").append(this.operSql.sqlCodes(sPortCodes)).append(")")
				.append(" and ").append(dbl.sqlSubStr("t1.fnum", "1", "9"))
				.append(" = ").append(dbl.sqlSubStr("t2.FNum", "1", "9"))
				.append(")");

			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append("(FNum,FRefNum,FMakeUpDate,FUnitCost,FMakeUpAmount,FOMakeUpCost,FHMakeUpCost,FOPReplaceCash,")
				.append(" FHPReplaceCash,FOCPReplaceCash,FHCPReplaceCash,FExRightDate,FSumAmount,FRealAmount,FInterest,")
				.append(" FBBInterest,FBBWarrantCost,FRightRate,FWarrantCost,FRemaindAmount,FOCRefundSum,FHCRefundSum,")
				.append(" FRefundDate,FDataMark,FDataDirection,FSettleMark,FExchangeRate,FCreator,FCreateTime,FDeflationAmount ,");
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
			    buff.append(" FOtherRight,FBBOtherRight)");
			    buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			    /**end---huhuichao 2013-8-8 STORY  4276  */
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			for(int i = 0;i < tradeSettleDetail.size();i++ ){//循环保存当天的上一个工作日的数据的集合
				etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);//获取交易结算明细bean
				alTradeSettleDelRef = etfTradeSettleDetail.getAlTradeSettleDelRef();//获取保存交易结算明细关联bean的集合
				for(int j=0; j< alTradeSettleDelRef.size();j++){//循环集合
					tradeSettleDelRef = (ETFTradeSettleDetailRefBean) alTradeSettleDelRef.get(j);//获取交易结算明细关联bean					
					this.setTradeDelRef(tradeSettleDelRef,pst);//为预处理赋值
				}
				
				if(i % batchSize == 0){//每500条执行一次数据库操作，提高性能
					pst.executeBatch();
				}
				
			}
			pst.executeBatch();
			
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置事物为自动提交
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("保存补票和权益数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 插入当天申购赎回数据和关联数据
	 * @throws YssException 
	 *
	 */
	public void insertTheDateData(Date dDate,String sPortCodes,ArrayList tradeSettleDetail) throws YssException {
		StringBuffer buff =null;//做拼接SQL语句
		Connection conn = null;//数据库连接声明
		PreparedStatement pstTrade = null;//交易结算明细数据的预处理声明
		PreparedStatement pstTradeRef = null;//交易结算明细关联数据的预处理声明
		final int batchSize = 1000;//预处理批量执行大小，每1000条提交一次给数据库执行
		boolean bTrans = true;//事物控制标识
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		ArrayList alTradeSettleDelRef = null;//保存交易关联明细
		try{
			buff = new StringBuffer(500);//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为事物手动提交
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_tradestldtl"));//给表加锁
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
			buff.append("(FNum,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,FBs,FBuyDate,");
			buff.append(" FReplaceAmount,FBraketNum,FUnitCost,FOReplaceCash,FHReplaceCash,FOCReplaceCash,");
			buff.append(" FHCReplaceCash,FExchangeRate,FTradeNum,FMarkType,FCreator,FCreateTime)");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pstTrade = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			//插入数据前先删除数据,条件:明细关联表的申请编号与明细表的申请编号关联
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref")).append(" t1 ");
			buff.append(" where exists (select FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
			buff.append(" t2 where FBuyDate =").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in (").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			buff.append(" and t1.fnum = t2.FNum)");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			//删除明细表中数据,条件：组合代码，申赎日期
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
			buff.append(" where FPortCode in(").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			buff.append(" and FBuyDate = ").append(dbl.sqlDate(dDate));
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append("(FNum,FRefNum,FMakeUpDate,FUnitCost,FMakeUpAmount,FOMakeUpCost,FHMakeUpCost,FOPReplaceCash,");
			buff.append(" FHPReplaceCash,FOCPReplaceCash,FHCPReplaceCash,FExRightDate,FSumAmount,FRealAmount,FInterest,");
			buff.append(" FBBInterest,FBBWarrantCost,FRightRate,FWarrantCost,FRemaindAmount,FOCRefundSum,FHCRefundSum,");
			buff.append(" FRefundDate,FDataMark,FDataDirection,FSettleMark,FExchangeRate,FCreator,FCreateTime" +
			/**shashijie 2011-08-07 STORY 1434 */
			" ,FDeflationAmount,");
			/**end*/
			/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
		    buff.append(" FOtherRight,FBBOtherRight)");
		    buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		    /**end---huhuichao 2013-8-8 STORY  4276  */
			
			pstTradeRef = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			for(int i = 0;i < tradeSettleDetail.size();i++ ){//循环保存明细数据的集合
				etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);
				//---------------------设置交易明细表------------------------//
				this.setTradeDetail(etfTradeSettleDetail,pstTrade);//为交易关联明细表赋值
				//----------------------end-------------------------------//
				alTradeSettleDelRef = etfTradeSettleDetail.getAlTradeSettleDelRef();//获取保存明细关联数据的集合
				
				pstTrade.addBatch();//增加明细数据的批处理
				for(int j =0; j < alTradeSettleDelRef.size(); j++){//循环保存明细关联数据的集合
					tradeSettleDelRef = (ETFTradeSettleDetailRefBean) alTradeSettleDelRef.get(j);//获取明细关联的bean
					this.setTradeDelRef(tradeSettleDelRef,pstTradeRef);//为明细关联预处理赋值
				}
				pstTradeRef.executeBatch();//执行明细关联数据的批处理
				//-----------------------------设置明细关联表----------------------//
				//--------------------------------end----------------------------//
				if(i % batchSize == 0){//每1000条执行一次插入操作，以提高性能
					pstTrade.executeBatch();
				}
			}
			pstTrade.executeBatch();//执行明细数据的批处理
			
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("插入当天申购赎回数据和关联数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pstTrade);
			dbl.closeStatementFinal(pstTradeRef);
		}
	}
	
	/**
	 * 插入交易结算明细关联数据（补票和权益数据）
	 * STORY #1434 QDV4易方达基金2011年7月27日01_A
	 * panjunfang add 20110913
	 * @param dDate
	 * @param sPortCodes
	 * @param tradeSettleDetail
	 * @throws YssException
	 */
	public void insertRefData(Date dDate,String sPortCodes,ArrayList tradeSettleDetail) throws YssException{
		StringBuffer buff =null;//做拼接SQL语句
		Connection conn = null;//声明数据库连接
		PreparedStatement pst = null;//声明预处理
		boolean bTrans = true;//失误控制标识
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		ArrayList alTradeSettleDelRef = null;//保存交易关联明细
		ResultSet rs = null;
		try{		
			buff = new StringBuffer(500);//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置事物手动提交

			//插入数据前先删除数据，条件：补票、权益日期为当天，并且在交易结算明细表中能关联到对应数据
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref")).append(" t1 ");
			buff.append(" where (t1.FMakeUpDate = ").append(dbl.sqlDate(dDate));
			buff.append(" or t1.FExrightDate = ").append(dbl.sqlDate(dDate));
			buff.append(") and exists (select FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
			buff.append(" t2 where FPortCode in (").append(this.operSql.sqlCodes(sPortCodes)).append(")");
			buff.append(" and t1.fnum = t2.FNum)");

			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append("(FNum,FRefNum,FMakeUpDate,FUnitCost,FMakeUpAmount,FOMakeUpCost,FHMakeUpCost,FOPReplaceCash,");
			buff.append(" FHPReplaceCash,FOCPReplaceCash,FHCPReplaceCash,FExRightDate,FSumAmount,FRealAmount,FInterest,");
			buff.append(" FBBInterest,FBBWarrantCost,FRightRate,FWarrantCost,FRemaindAmount,FOCRefundSum,FHCRefundSum,");
			buff.append(" FRefundDate,FDataMark,FDataDirection,FSettleMark,FExchangeRate,FCreator,FCreateTime ,FDeflationAmount,");
			/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
			buff.append(" FOtherRight,FBBOtherRight)");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			/**end---huhuichao 2013-8-8 STORY  4276  */
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			for(int i = 0;i < tradeSettleDetail.size();i++ ){//循环保存当天的上一个工作日的数据的集合
				etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);//获取交易结算明细bean
				alTradeSettleDelRef = etfTradeSettleDetail.getAlTradeSettleDelRef();//获取保存交易结算明细关联bean的集合
				for(int j=0; j< alTradeSettleDelRef.size();j++){//循环集合
					tradeSettleDelRef = (ETFTradeSettleDetailRefBean) alTradeSettleDelRef.get(j);//获取交易结算明细关联bean
					this.setTradeDelRef(tradeSettleDelRef,pst);//为预处理赋值
				}
				pst.executeBatch();
			}
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置事物为自动提交
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("处理补票和权益数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 为交易关联明细表赋值
	 * @param pst
	 * @throws YssException
	 */
	private void setTradeDelRef(ETFTradeSettleDetailRefBean tradeSettleDelRef,PreparedStatement pst) throws YssException{
		try{
			pst.setString(1,tradeSettleDelRef.getNum());
			pst.setString(2,tradeSettleDelRef.getRefNum());
			pst.setDate(3,YssFun.toSqlDate(tradeSettleDelRef.getMakeUpDate()!=null?tradeSettleDelRef.getMakeUpDate():YssFun.toDate("9998-12-31")));
			pst.setDouble(4,tradeSettleDelRef.getUnitCost());
			pst.setDouble(5,tradeSettleDelRef.getMakeUpAmount());
			pst.setDouble(6,tradeSettleDelRef.getoMakeUpCost());
			pst.setDouble(7,tradeSettleDelRef.gethMakeUpCost());
			pst.setDouble(8,tradeSettleDelRef.getOpReplaceCash());
			pst.setDouble(9,tradeSettleDelRef.getHpReplaceCash());
			pst.setDouble(10,tradeSettleDelRef.getOcReplaceCash());
			pst.setDouble(11,tradeSettleDelRef.getHcReplaceCash());
			pst.setDate(12,YssFun.toSqlDate(tradeSettleDelRef.getExRightDate()!=null?tradeSettleDelRef.getExRightDate():YssFun.toDate("9998-12-31")));
			pst.setDouble(13,tradeSettleDelRef.getSumAmount());
			pst.setDouble(14,tradeSettleDelRef.getRealAmount());
			pst.setDouble(15,tradeSettleDelRef.getInterest());
			pst.setDouble(16,tradeSettleDelRef.getBbinterest());
			pst.setDouble(17,tradeSettleDelRef.getBbwarrantCost());
			pst.setDouble(18,tradeSettleDelRef.getRightRate());
			pst.setDouble(19,tradeSettleDelRef.getWarrantCost());
			pst.setDouble(20,tradeSettleDelRef.getRemaindAmount());
			pst.setDouble(21,tradeSettleDelRef.getOcRefundSum());
			pst.setDouble(22,tradeSettleDelRef.getHcRefundSum());
			pst.setDate(23,YssFun.toSqlDate(tradeSettleDelRef.getRefundDate()!=null?tradeSettleDelRef.getRefundDate():YssFun.toDate("9998-12-31")));
			pst.setString(24,tradeSettleDelRef.getDataMark());
			pst.setString(25,tradeSettleDelRef.getDataDirection());
			pst.setString(26,tradeSettleDelRef.getSettleMark());
			pst.setDouble(27,tradeSettleDelRef.getExchangeRate());
			pst.setString(28,pub.getUserCode());
			pst.setString(29,YssFun.formatDatetime(new Date()));
			/**shashijie 2011.08.07 STORY 1434 */
			pst.setDouble(30,tradeSettleDelRef.getDeflationAmount());//缩股数量
			/**end*/
			/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
			pst.setDouble(31,tradeSettleDelRef.getOtherRight());//其他权益原币
			pst.setDouble(32,tradeSettleDelRef.getbBOtherRight());//其他权益本币
			/**end---huhuichao 2013-8-8 STORY  4276*/
			pst.addBatch();
		}catch (Exception e) {
			throw new YssException("为交易关联明细表赋值出错！",e);
		}
	}
	
	/**
	 *  为交易结算明细表赋值
	 * @param etfTradeSettleDetail
	 * @param pst
	 * @throws YssException
	 */
	private void setTradeDetail(ETFTradeSettleDetailBean etfTradeSettleDetail,PreparedStatement pstTrade) throws YssException{
		try{
			pstTrade.setString(1,etfTradeSettleDetail.getNum());
			pstTrade.setString(2,etfTradeSettleDetail.getPortCode());
			pstTrade.setString(3,etfTradeSettleDetail.getSecurityCode());
			pstTrade.setString(4,etfTradeSettleDetail.getStockHolderCode());
			pstTrade.setString(5,etfTradeSettleDetail.getBrokerCode());
			pstTrade.setString(6,etfTradeSettleDetail.getSeatCode());
			pstTrade.setString(7,etfTradeSettleDetail.getBs());
			pstTrade.setDate(8,YssFun.toSqlDate(etfTradeSettleDetail.getBuyDate()));
			pstTrade.setDouble(9,etfTradeSettleDetail.getReplaceAmount());
			pstTrade.setDouble(10,etfTradeSettleDetail.getBraketNum());
			pstTrade.setDouble(11,etfTradeSettleDetail.getUnitCost());
			pstTrade.setDouble(12,etfTradeSettleDetail.getOReplaceCash());
			pstTrade.setDouble(13,etfTradeSettleDetail.getHReplaceCash());
			pstTrade.setDouble(14,etfTradeSettleDetail.getOcReplaceCash());
			pstTrade.setDouble(15,etfTradeSettleDetail.getHcReplaceCash());
			pstTrade.setDouble(16,etfTradeSettleDetail.getExchangeRate());
			pstTrade.setString(17,etfTradeSettleDetail.getTradeNum());
			pstTrade.setString(18,etfTradeSettleDetail.getMarktype());
			pstTrade.setString(19,pub.getUserCode());
			pstTrade.setString(20,YssFun.formatDatetime(new Date()));
		}catch (Exception e) {
			throw new YssException("为交易结算明细表赋值出错！",e);
		}
	}
	
	//add by fangjiang 2013.01.08 STORY #3402
	public void deleteBSDetail(Date dDate,String sPortCodes) throws YssException {
		StringBuffer buff =new StringBuffer();//做拼接SQL语句
		try{			
			//删除条件：组合代码，申赎日期
			buff.append(" delete from ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" where FPortCode in(").append(this.operSql.sqlCodes(sPortCodes)).append(")")
				.append(" and FBuyDate = ").append(dbl.sqlDate(dDate));
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("插入当天申购赎回数据出错！",e);
		}finally{

		}
	}
	
	public void deleteGcMakeUp(Date dDate, String sPortCodes) throws YssException {
		StringBuffer buff =null;//做拼接SQL语句
		try{		
			buff = new StringBuffer(500);//实例化

			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" t1 where t1.FMakeUpDate = ").append(dbl.sqlDate(dDate))
				.append(" and t1.FRefNum = 'gc' ")
				.append(" and exists (select FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t2 where FPortCode in (").append(this.operSql.sqlCodes(sPortCodes)).append(")")
				.append(" and ").append(dbl.sqlSubStr("t1.fnum", "1", "9"))
				.append(" = ").append(dbl.sqlSubStr("t2.FNum", "1", "9"))
				.append(")");

			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("保存补票和权益数据出错！",e);
		}finally{

		}
	}
	
	public void deleteMakeUpExceptGc(Date dDate, String sPortCodes) throws YssException {
		StringBuffer buff =null;//做拼接SQL语句
		try{		
			buff = new StringBuffer();//实例化

			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" t1 where t1.FRefNum <> 'gc' and t1.FMakeUpDate = ").append(dbl.sqlDate(dDate))
				.append(" and exists (select FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t2 where FPortCode in (").append(this.operSql.sqlCodes(sPortCodes)).append(")")
				.append(" and ").append(dbl.sqlSubStr("t1.fnum", "1", "9"))
				.append(" = ").append(dbl.sqlSubStr("t2.FNum", "1", "9"))
				.append(")");

			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("保存补票和权益数据出错！",e);
		}finally{

		}
	}
	
	public void deleteEquity(Date dDate, String sPortCodes) throws YssException {
		StringBuffer buff =null;//做拼接SQL语句
		try{		
			buff = new StringBuffer();//实例化

			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" t1 where to_date(substr(t1.frefnum, 9),'yyyymmdd') = ").append(dbl.sqlDate(dDate))
				.append(" and exists (select FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t2 where FPortCode in (").append(this.operSql.sqlCodes(sPortCodes)).append(")")
				.append(" and ").append(dbl.sqlSubStr("t1.fnum", "1", "9"))
				.append(" = ").append(dbl.sqlSubStr("t2.FNum", "1", "9"))
				.append(")");

			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
		}catch (Exception e) {
			throw new YssException("保存补票和权益数据出错！",e);
		}finally{

		}
	}
	
	public void insertBSDetail(ArrayList tradeSettleDetail) throws YssException {
		StringBuffer buff =null;//做拼接SQL语句
		Connection conn = null;//数据库连接声明
		PreparedStatement pstTrade = null;//交易结算明细数据的预处理声明
		final int batchSize = 499;//预处理批量执行大小，每500条提交一次到数据库执行
		boolean bTrans = true;//事物控制标识
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		try{
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为事物手动提交
			
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_tradestldtl"));//给表加锁

			buff = new StringBuffer(500);//实例化
			buff.append(" insert into ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append("(FNum,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,FBs,FBuyDate,")
				.append(" FReplaceAmount,FBraketNum,FUnitCost,FOReplaceCash,FHReplaceCash,FOCReplaceCash,")
				.append(" FHCReplaceCash,FExchangeRate,FTradeNum,FMarkType,FCreator,FCreateTime)")
				.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pstTrade = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			//循环保存申赎明细数据的集合，执行插入
			for(int i = 0;i < tradeSettleDetail.size();i++ ){
				etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);
				this.setTradeDetail(etfTradeSettleDetail,pstTrade);//为申赎明细表赋值

				pstTrade.addBatch();//增加明细数据的批处理
				if(i % batchSize == 0){//每500条执行一次数据库操作，提高性能
					pstTrade.executeBatch();
				}
			}
			pstTrade.executeBatch();//执行明细数据的批处理
			
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("插入当天申购赎回数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pstTrade);
		}
	}

	public void insertBSDetailRef(ArrayList tradeSettleDetail) throws YssException {
		StringBuffer buff =null;//做拼接SQL语句
		Connection conn = null;//声明数据库连接
		PreparedStatement pst = null;//声明预处理
		boolean bTrans = true;//失误控制标识
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		ResultSet rs = null;
		final int batchSize = 499;//预处理批量执行大小，每500条提交一次到数据库执行
		try{		
			buff = new StringBuffer(500);//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置事物手动提交
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append("(FNum,FRefNum,FMakeUpDate,FUnitCost,FMakeUpAmount,FOMakeUpCost,FHMakeUpCost,FOPReplaceCash,")
				.append(" FHPReplaceCash,FOCPReplaceCash,FHCPReplaceCash,FExRightDate,FSumAmount,FRealAmount,FInterest,")
				.append(" FBBInterest,FBBWarrantCost,FRightRate,FWarrantCost,FRemaindAmount,FOCRefundSum,FHCRefundSum,")
				.append(" FRefundDate,FDataMark,FDataDirection,FSettleMark,FExchangeRate,FCreator,FCreateTime,FDeflationAmount,");
			    /**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
			    buff.append(" FOtherRight,FBBOtherRight)");
			    buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			    /**end---huhuichao 2013-8-8 STORY  4276  */
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			for(int i = 0;i < tradeSettleDetail.size();i++ ){//循环保存当天的上一个工作日的数据的集合
				etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);//获取交易结算明细bean
				tradeSettleDelRef = etfTradeSettleDetail.getTargetDelRef();
				this.setTradeDelRef(tradeSettleDelRef,pst);//为预处理赋值				
				
				if(i % batchSize == 0){//每500条执行一次数据库操作，提高性能
					pst.executeBatch();
				}				
			}
			pst.executeBatch();
			
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置事物为自动提交
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("保存补票和权益数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rs);
		}
	}
	//end by fangjiang 2013.01.08 STORY #3402
}