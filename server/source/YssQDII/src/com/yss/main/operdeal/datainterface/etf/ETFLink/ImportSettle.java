/**@author shashijie STORY 2565
*  类说明 : 中登结算明细过度表
*/
package com.yss.main.operdeal.datainterface.etf.ETFLink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class ImportSettle extends DataBase{
	
	
	
	/**shashijie 2012-4-26 STORY 2565 程序入口 */
	public void inertData() throws YssException {
		//获取数据集合
		List subBeanList = getBeanList(this.sDate,this.sPort);
		
		//存入数据
		insertSubBeanList(subBeanList,this.sDate,this.sPort);
    }

	/**shashijie 2012-5-4 STORY 2565 
	* @param subBeanList
	* @param sDate
	* @param sPort*/
	private void insertSubBeanList(List subBeanList, Date dDate, String fPort) throws YssException {
		if (subBeanList.isEmpty()) {
			return;
		}
		
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			//先删除
			String strSql = getDelete(dDate,fPort);
			dbl.executeSql(strSql);
			
			strSql = getInsert();//新增SQL
			ps = conn.prepareStatement(strSql);
			//批量增加
			for (int i = 0; i < subBeanList.size(); i++) {
				JSMXInterfaceBean in = (JSMXInterfaceBean)subBeanList.get(i);
				//赋值
				setPreparedStatement(ps,in,dDate,fPort);
				ps.executeUpdate();
			}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("保存数据出错!",e);
		} finally {
			dbl.closeStatementFinal(ps);
			dbl.endTransFinal(conn, bTrans);
		}
		
	}

	/**shashijie 2012-5-4 STORY 2565
	* @param ps
	* @param in*/
	private void setPreparedStatement(PreparedStatement ps, JSMXInterfaceBean in,
			Date dDate, String fPort) throws Exception {
		if (in == null) {
			throw new YssException("赋值数据表对象出错!");
		}
		ps.setString(1, fPort);//  组合代码
		ps.setString(2, in.getFTradeTypeCode());//  业务类型
		ps.setString(3, in.getFSecurityCode1());//  证券代码1
		ps.setDate(4, YssFun.toSqlDate(dDate));//  数据日期
		ps.setDate(5, YssFun.toSqlDate(in.getFBargainDate()));//  交易日期
		ps.setString(6, in.getFBargainBs());//  买卖标志
		ps.setString(7, in.getFFundsBar());//  资金账号
		ps.setString(8, in.getFCurrencyCode());//币种


		ps.setDouble(9, in.getFTradeAmount());//  成交数量
		ps.setDouble(10, in.getFSettlePrice());//  结算价额
		ps.setDouble(11, in.getFTradePrice());//  成交价格
		ps.setDouble(12, in.getFClearMoney());//  清算金额
		ps.setDouble(13, in.getFTotalMoney());//  实收实付
		ps.setDouble(14, in.getFSettleAmount());//  交收数量
		ps.setDouble(15, in.getFStampTax());//  印花税
		ps.setDouble(16, in.getFhandleTax());// 经手费
		ps.setDouble(17, in.getFTransferTax());// 过户费
		ps.setDouble(18, in.getFCanalTax());//  证管费
		ps.setDouble(19, in.getFProcedureTax());//  手续费
		ps.setDouble(20, in.getFOtherMoney1());// 其它金额1
		ps.setDouble(21, in.getFOtherMoney2());// 其它金额2
		ps.setDouble(22, in.getFOtherMoney3());//  其它金额3
		ps.setString(23, in.getFTradeNum());//成交编号
		ps.setString(24, in.getFRecordType());//记录类型
		ps.setString(25, in.getFSecurityType());//证券类别
		ps.setString(26, in.getFClearMark());//清算标志
		ps.setString(27, in.getFResultCode());//结果代码"0000"
		ps.setString(28, in.getFSeatNum());//  席位代码
		ps.setString(29, in.getFStockholderCode());// 股东代码
		ps.setString(30, in.getFReceiptsBs());//交收方式
		ps.setString(31, "1");//审核状态
		ps.setString(32, pub.getUserCode());//创建人、修改人
		ps.setString(33, YssFun.formatDate(new Date()));//创建、修改时间
		
		
	}

	/**shashijie 2012-5-4 STORY 2565
	* @return*/
	private String getInsert() {
		String query = " insert into "+
			pub.yssGetTableName("Tb_ETF_JSMX")+
			"(" +
			" Fportcode, "+//  组合代码
			" Ftradetypecode, "+//  业务类型
			" Fsecuritycode1, "+//  证券代码1
			" Fdate, "+//  数据日期
			" Fbargaindate, "+//  交易日期
			" Fbargainbs, "+//  买卖标志
			" Ffundsbar, "+//  资金账号
			" FCurrencyCode ,"+//币种
 
 
			" Ftradeamount, "+//  成交数量
			" Fsettleprice, "+//  结算价额
			" Ftradeprice, "+//  成交价格
			" Fclearmoney, "+//  清算金额
			" Ftotalmoney, "+//  实收实付
			" Fsettleamount, "+//  交收数量
			" Fstamptax, "+//  印花税
			" Fhandletax, "+// 经手费
			" Ftransfertax, "+// 过户费
			" Fcanaltax, "+//  证管费
			" Fproceduretax, "+//  手续费
			" Fothermoney1, "+// 其它金额1
			" Fothermoney2, "+// 其它金额2
			" Fothermoney3, "+//  其它金额3
			
			" FTradeNum, "+//成交编号
			" FRecordType, "+//记录类型
			" FSecurityType, "+//证券类别
			" FClearMark, "+//清算标志
			" FResultCode,"+//结果代码"0000"
			" FSeatNum, "+//  席位代码
			" FStockholderCode ,"+// 股东代码
			" FReceiptsBs ,"+//交收方式
			" FCheckState ,"+//审核状态
			" FCreator, "+//创建人、修改人
			" FCreateTime "+//创建、修改时间
			  
			
			")"+
			" Values ( " +
			" ?,?,?,?,?,?,?,?,?,?" +//10
			",?,?,?,?,?,?,?,?,?,?" +//20
			",?,?,?,?,?,?,?,?,?,?" +//30
			",?,?,?"+
			" ) ";
		return query;
	}

	/**shashijie 2012-5-4 STORY 2565
	* @param dDate
	* @param fPort
	* @return*/
	private String getDelete(Date dDate, String fPort) {
		String query = 
			" delete from "+pub.yssGetTableName("Tb_ETF_JSMX")+
	        " where FDate ="+dbl.sqlDate(dDate)+//接口数据日期为当前接口导入日期
	        " and FPortCode in("+operSql.sqlCodes(fPort)+")";
		return query;
	}

	/**shashijie 2012-5-4 STORY 2565
	* @param sDate 
	* @param sPort 
	* @return*/
	private List getBeanList(Date dDate, String fPort) throws YssException {
		ResultSet rs = null;
		List list = new ArrayList();//数据集合
		try {
			String query = getSql(dDate,fPort);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//对象赋值
				JSMXInterfaceBean in = getJSMXInterfaceBean(rs,dDate,fPort);
				list.add(in);
			}
		} catch (Exception e) {
			throw new YssException("获取数据集合出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return list;
	}

	/**shashijie 2012-5-4 STORY 2565
	* @param rs
	* @return*/
	private JSMXInterfaceBean getJSMXInterfaceBean(ResultSet rs,
			Date dDate,String fPortCode) throws Exception {
		JSMXInterfaceBean in = new JSMXInterfaceBean();
		//in.setYssPub(pub);
		
		in.setFPortCode(fPortCode);//  组合代码
		in.setFTradeTypeCode(rs.getString("Ftradetypecode"));//  业务类型
		in.setFSecurityCode1(rs.getString("Fsecuritycode1"));//  证券代码1
		in.setFDate(dDate);//数据日期
		in.setFBargainDate(rs.getDate("FBargainDate"));//  交易日期
		in.setFBargainBs(rs.getString("FBargainBs"));//  买卖标志
		in.setFFundsBar(rs.getString("FFundsBar"));//  资金账号
		in.setFCurrencyCode(rs.getString("FCurrencyCode"));//  币种

		
		in.setFTradeAmount(rs.getDouble("FTradeAmount"));//  成交数量
		in.setFSettlePrice(rs.getDouble("FSettlePrice"));//  结算价额
		in.setFTradePrice(rs.getDouble("FTradePrice"));//  成交价格
		in.setFClearMoney(rs.getDouble("FClearMoney"));//  清算金额
		in.setFTotalMoney(rs.getDouble("FTotalMoney"));//  实收实付
		in.setFSettleAmount(rs.getDouble("FSettleAmount"));//  交收数量
		in.setFStampTax(rs.getDouble("FStampTax"));//  印花税
		in.setFhandleTax(rs.getDouble("FhandleTax"));// 经手费
		in.setFTransferTax(rs.getDouble("FTransferTax"));// 过户费
		in.setFCanalTax(rs.getDouble("FCanalTax"));//  证管费
		in.setFProcedureTax(rs.getDouble("FProcedureTax"));//  手续费
		in.setFOtherMoney1(rs.getDouble("FOtherMoney1"));// 其它金额1
		in.setFOtherMoney2(rs.getDouble("FOtherMoney2"));// 其它金额2
		in.setFOtherMoney3(rs.getDouble("FOtherMoney3"));//其它金额3
		in.setFTradeNum(rs.getString("FTradeNum"));//成交编号
		in.setFRecordType(rs.getString("FRecordType"));//记录类型
		in.setFSecurityType(rs.getString("FSecurityType"));//证券类别
		in.setFClearMark(rs.getString("FClearMark"));//清算标志
		in.setFCurrencyCode(rs.getString("FCurrencyCode"));//币种
		in.setFResultCode(rs.getString("FResultCode"));//结果代码"0000"
		in.setFSeatNum(rs.getString("FSeatcode"));//  席位代码
		in.setFStockholderCode(rs.getString("FStockholderCode"));// 股东代码
		in.setFReceiptsBs(rs.getString("FReceiptsBs"));//交收方式
		in.setFTransactionBs(rs.getString("FTransactionBs"));//交易方式
		
		
		return in;
	}

	/**shashijie 2012-5-4 STORY 2565 
	* @param dDate
	* @param fPort
	* @return*/
	private String getSql(Date dDate, String fPort) {
		String query = 
			"Select " +
			" b.Fbrokercode,"+//  券商代码
			" b.Fseatcode, "+//  席位代码
			" c.Fstockholdercode, "+// 股东代码
			" a.Fportcode, "+//  组合代码
			" a.Ftradetypecode, "+//  业务类型
			" f.Fsecuritycode1, "+//  证券代码
			" a.Fdate, "+//  数据日期
			" a.Fbargaindate, "+//  交易日期
			" a.Fbargainbs, "+//  买卖标志
			" a.Ffundsbar, "+//  资金账号
			" a.Fcurrencycode, "+//  币种
 
			//如果结果代码不是0000表示交易失败,即没有全部或部分确认,此时数量为0
			" Case When a.FResultCode <> '0000' Then 0 Else a.Ftradeamount End Ftradeamount, "+//  成交数量
			" a.Fsettleprice, "+//  结算价额
			" a.Ftradeprice, "+//  成交价格
			" a.Fclearmoney, "+//  清算金额
			" Case When a.FResultCode <> '0000' Then 0 Else a.Ftotalmoney End Ftotalmoney, "+//  实收实付
			" a.Fsettleamount, "+//  交收数量
			" a.Fstamptax, "+//  印花税
			" a.Fhandletax, "+// 经手费
			" a.Ftransfertax, "+// 过户费
			" a.Fcanaltax, "+//  证管费
			" a.Fproceduretax, "+//  手续费
			" a.Fothermoney1, "+// 其它金额1
			" a.Fothermoney2, "+// 其它金额2
			" a.Fothermoney3, "+//  其它金额3
			" a.FTradeNum, "+//成交编号
			" a.FRecordType, "+//记录类型		002-交收通知 003-交收结果
			" a.FSecurityType, "+//证券类别		JJ：基金申赎
			" a.FClearMark,"+//	清算标志
			" a.FCurrencyCode ,"+//币种
			" '0000' FResultCode ,"+//结果代码存入数据库始终是"0000",表示正常交易
			" a.FReceiptsBs ,"+//交收方式
			" a.FTransactionBs ,"+//交易方式
			
			
			
			" '' "+
			" From  ( Select a1.* From "+pub.yssGetTableName("Tb_Etf_Jsmxinterface")+" a1" +
			" Where a1.Fsecuritytype = 'JJ'" +
			" And a1.Fclearmark In ('276', ' ')" +
			" And a1.Fdate = "+dbl.sqlDate(dDate)+
			" And a1.Fcheckstate = 1" +
			" Order By a1.Fdate ) a"+
  
			//席位信息设置表
			" Join (Select A1.Fportcode, A5.Fseatnum, A5.Fbrokercode, A5.Fseatcode" +
			" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" A1" +
					" Join (Select A2.Fsubcode, A2.Fportcode" +
					" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" A2 "+
					" Where A2.Fportcode = "+dbl.sqlString(fPort)+
					" And A2.Frelatype = 'TradeSeat'" +
					" And A2.Fcheckstate = 1) A3 On A1.Fportcode = A3.Fportcode" +
						" Left Join (Select A4.Fseatcode, A4.Fseatnum, A4.Fbrokercode" +
							" From "+pub.yssGetTableName("Tb_Para_Tradeseat")+
			" A4) A5 On A3.Fsubcode = A5.Fseatcode ) b On a.Fseatnum = b.Fseatnum " +
			
			//股东信息设置表
			" Join (Select A1.Fportcode, A7.Fstockholdernum, A7.Fstockholdercode" +
			" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" A1 " +
					" Join (Select A2.Fsubcode, A2.Fportcode" +
					" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" A2" +
					" Where A2.Fportcode = "+dbl.sqlString(fPort)+
					" And A2.Frelatype = 'Stockholder' "+
					" And A2.Fcheckstate = 1) A3 On A1.Fportcode = A3.Fportcode" +
						" Left Join (Select A6.Fstockholdercode, A6.Fstockholdernum" +
							" From "+pub.yssGetTableName("Tb_Para_Stockholder")+
			" A6) A7 On A3.Fsubcode = A7.Fstockholdercode) c On a.Fstockholdercode = c.Fstockholdernum "+
			//证券
			" Join (Select F1.Fsecuritycode Fsecuritycode1," +
			" Substr(F1.Fsecuritycode, 1, Length(F1.Fsecuritycode) - 3) Fsecuritycode "+
			" From "+pub.yssGetTableName("Tb_Para_Security")+
			" F1) f On f.Fsecuritycode = a.Fsecuritycode1 "+
			"";
		return query;
	}
	
	
	
}
