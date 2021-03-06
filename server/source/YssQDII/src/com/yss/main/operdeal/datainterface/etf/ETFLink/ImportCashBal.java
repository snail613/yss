/**@author shashijie
*  @version 创建时间：2012-5-5 下午02:32:02 STORY 2565
*  类说明 : 现金差额导入,行情表导入
*/
package com.yss.main.operdeal.datainterface.etf.ETFLink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class ImportCashBal extends DataBase {
	
	
	
	
	/**shashijie 2012-4-26 STORY 2565 程序入口 */
	public void inertData() throws YssException {
		//获取数据集合
		List subBeanList = getBeanList(this.sDate,this.sPort);
		
		//存入数据
		insertSubBeanList(subBeanList,this.sDate,this.sPort);
		
		//存入行情表
		insertMarketValue(subBeanList,this.sDate,this.sPort);
    }

	/**shashijie 2012-5-6 STORY 2565
	* @param subBeanList
	* @param sDate
	* @param sPort*/
	private void insertMarketValue(List subBeanList, Date dDate, String fPort) throws YssException {
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
			String strSql = getDeleteMarket(dDate,fPort);
			dbl.executeSql(strSql);
			
			strSql = getInsertMarket();//新增SQL
			ps = conn.prepareStatement(strSql);
			//批量增加
			for (int i = 0; i < subBeanList.size(); i++) {
				Map<String, Object> map = (Map<String, Object>)subBeanList.get(i);
				//赋值
				setPreparedStatementMarket(ps,map,dDate,fPort);
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

	/**shashijie 2012-5-6 STORY 2565
	* @param ps
	* @param map
	* @param dDate
	* @param fPort*/
	private void setPreparedStatementMarket(PreparedStatement ps,
			Map<String, Object> map, Date dDate, String fPort) throws Exception {
		if (map == null) {
			throw new YssException("赋值数据表对象出错!");
		}
		ps.setString(1, "NB");//行情来源代码(内部)
		ps.setString(2, map.get("FSecurityCode").toString());//证券代码
		ps.setDate(3, YssFun.toSqlDate(dDate));//行情日期
		ps.setString(4, "00:00:00");//行情时间
		ps.setString(5, " ");//组合代码
		ps.setDouble(6, Double.valueOf(map.get("FNetValue").toString()));//昨收盘价
		ps.setDouble(7, Double.valueOf(map.get("FNetValue").toString()));//今开盘价
		ps.setDouble(8, Double.valueOf(map.get("FNetValue").toString()));//最高价
		ps.setDouble(9, Double.valueOf(map.get("FNetValue").toString()));//最低价
		ps.setDouble(10, Double.valueOf(map.get("FNetValue").toString()));//收盘价
		ps.setDouble(11, Double.valueOf(map.get("FNetValue").toString()));//平均价
		ps.setString(12, "1");//数据来源标识
		ps.setString(13, this.checkState.equalsIgnoreCase("true")?"1":"0");//审核状态
		ps.setString(14, pub.getUserCode());//创建人、修改人
		ps.setString(15, YssFun.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));//创建、修改时间

	}

	/**shashijie 2012-5-6 STORY 2565
	* @return*/
	private String getInsertMarket() {
		String query =
			"Insert Into "+pub.yssGetTableName("Tb_Data_MarketValue")+
			" (FMktSrcCode ,"+
			" FSecurityCode ,"+
			" FMktValueDate ,"+
			" FMktValueTime ,"+
			" FPortCode ,"+
			" FYClosePrice ,"+
			" FOpenPrice ,"+
			" FTopPrice ,"+
			" FLowPrice ,"+
			" FClosingPrice ,"+
			" FAveragePrice ,"+
			" FDataSource ,"+
			" FCheckState ,"+
			" FCreator ,"+
			" FCreateTime "+
			" ) "+
				
			" Values("+
			"?,?,?,?,?,?,?,?,?,?,"+//10
			"?,?,?,?,?"+
			")";
		return query;
	}

	/**shashijie 2012-5-6 STORY 2565
	* @param dDate
	* @param fPort
	* @return*/
	private String getDeleteMarket(Date dDate, String fPort) {
		String query = 
			" Delete From "+pub.yssGetTableName("Tb_Data_MarketValue")+
			" Where FMktValueDate = " + dbl.sqlDate(dDate)+
			" And FMktSrcCode = 'NB' ";
		return query;
	}

	/**shashijie 2012-5-5 STORY 2565 
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
				Map<String, Object> map = (Map<String, Object>)subBeanList.get(i);
				//赋值
				setPreparedStatement(ps,map,dDate,fPort);
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

	/**shashijie 2012-5-5 STORY 2565
	* @param ps
	* @param map
	* @param dDate
	* @param fPort*/
	private void setPreparedStatement(PreparedStatement ps,
			Map<String, Object> map, Date dDate, String fPort) throws Exception {
		if (map == null) {
			throw new YssException("赋值数据表对象出错!");
		}
		ps.setString(1, map.get("FSecurityCode").toString());//证券代码
		ps.setDate(2, YssFun.toSqlDate(dDate));//交易日期
		ps.setDouble(3, Double.valueOf(map.get("FNetValue").toString()));//基金单位净值
		ps.setDouble(4, Double.valueOf(map.get("FCashBal").toString()));//现金差额
	}

	/**shashijie 2012-5-5 STORY 2565
	* @return*/
	private String getInsert() {
		String query = " insert into "+
			pub.yssGetTableName("Tb_ETF_Difference")+
				"(" +
				" FSecurityCode ,"+//证券代码
				" FBargainDate ,"+//交易日期
				" FNetValue ,"+//基金单位净值
				" FCashBal "+//现金差额
				
				")"+
				" Values ( " +
				" ?,?,?,?"+
				" ) ";
	return query;
	}

	/**shashijie 2012-5-5 STORY 2565
	* @param dDate
	* @param fPort
	* @return*/
	private String getDelete(Date dDate, String fPort) {
		String query = 
			" delete From "+pub.yssGetTableName("Tb_ETF_Difference")+
	        " where FBargainDate = "+dbl.sqlDate(dDate);
		return query;
	}

	/**shashijie 2012-5-5 STORY 2565 
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
				//如果内外节假日都不是工作日则不处理,2012-08-06改为只要境外节假日就不导入行情
				if (isRestDay(dDate,/*rs.getString("one")*/rs.getString("two"),rs.getString("two"))) {
					return list;
				}
				//对象赋值
				Map<String, Object> map = getHashMap(rs,dDate,fPort);
				list.add(map);
			}
		} catch (Exception e) {
			throw new YssException("获取数据集合出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return list;
	}

	/**shashijie 2012-6-28 STORY 2565 判断内外节假日是否是工作日*/
	private boolean isRestDay(Date dDate,String FHolidaysCode1, String FHolidaysCode2) throws YssException {
		boolean flag = false;
		boolean flag2 = false;
		
		String day = getSettleDate(FHolidaysCode1, dDate, 0);
		if (!day.equals(YssFun.formatDate(dDate))) {
			flag = true;
		}
		day = getSettleDate(FHolidaysCode2, dDate, 0);
		if (!day.equals(YssFun.formatDate(dDate))) {
			flag2 = true;
		}
		
		if (flag && flag2) {
			return true;
		} else {
			return false;
		}
		
	}

	/**shashijie 2012-4-27 STORY 2565 获取结算日期
	* @param FHolidaysCode 节假日代码
	* @param dDate 日期
	* @param dayInt 延迟天数
	* @return*/
	private String getSettleDate(String FHolidaysCode, Date dDate , int dayInt) throws YssException {
		Date mDate = null;//工作日
		try {
			//公共获取工作日类
			BaseOperDeal operDeal = new BaseOperDeal();
	        operDeal.setYssPub(pub);
	        mDate = operDeal.getWorkDay(FHolidaysCode, dDate, dayInt);
		} catch (Exception e) {
			throw new YssException("获取工作日日期出错!",e);
		} finally {

		}
        return YssFun.formatDate(mDate);

	}
	
	/**shashijie 2012-5-5 STORY 2565
	* @param rs
	* @param dDate
	* @param fPort
	* @return*/
	private Map<String, Object> getHashMap(ResultSet rs, Date dDate,
			String fPort) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("FSecurityCode", rs.getString("FSetid"));//证券代码
		map.put("FCashBal", YssD.round(rs.getDouble("FCashBal"),2));//现金差额
		map.put("FNetValue", YssD.round(rs.getDouble("FNetvalue"),4));//单位净值
		//map.put("FBargainDate", dDate);//净值日期
		return map;
	}

	/**shashijie 2012-5-5 STORY 2565
	* @param dDate
	* @param fPort
	* @return*/
	private String getSql(Date dDate, String fPort) {
		String query =
			" Select a.Fnetvalue,"+//单位净值
			" a.Fportcode,"+//
			" f.Fsecuritycode1 Fsetid,"+//
			" NVL(a.Fcashbal,0) Fcashbal,"+//现金差额
			" d.Mbetf,"+//目标ETF组合代码
			" d.One, "+//节假日
			" d.Two "+//节假日
			" From (Select b.Fstandardmoneymarketvalue As Fnetvalue,"+
			" B4.Fportcode,"+
			" B4.Fsetid,"+
			" B6.Fcashbal"+
        
				" From "+pub.yssGetTableName("Tb_Rep_Guessvalue")+" b"+
				//目标ETF资产代码
				" Join (Select B1.Fsetid," +
				" B1.Fsetcode," +
				" B3.Fportcode," +
				" Max(B1.Fyear) Fyear" +
				" From Lsetlist B1" +
				" Join (Select B2.Faimetfcode, B2.Fportcode" +
				" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" B2" +
				" Where B2.Fportcode = "+dbl.sqlString(fPort)+") B3 On B1.Fsetid = B3.Faimetfcode" +
				" Group By B1.Fsetid, B1.Fsetcode, B3.Fportcode) B4 On B4.Fsetcode = b.Fportcode"+
	        
				//现金差额
				" Left Join (Select B5.Fstandardmoneymarketvalue As Fcashbal, B5.Fportcode" +
				" From "+pub.yssGetTableName("Tb_Rep_Guessvalue")+" B5" +
				" Where B5.Facctcode = '9802'" +
				" And B5.Fdate = "+dbl.sqlDate(dDate)+") B6 On B6.Fportcode = b.Fportcode"+
				" Where b.Fdate = "+dbl.sqlDate(dDate)+
				" And b.Facctcode = '9600') a"+

			//目标ETF境内外节假日
			" Join ( Select c.Fportcode, c.Mbetf, C7.One, C8.Two" +
			" From (Select C1.Fportcode, C1.Faimetfcode, C3.Fportcode As Mbetf" +
			" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" C1" +
			" Join (Select C2.Fassetcode, C2.Fportcode" +
			" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" C2) C3 On C1.Faimetfcode = C3.Fassetcode"+
			" Where C1.Fportcode = "+dbl.sqlString(fPort)+
			" And c1.Fcheckstate = 1) c" +
			//节假日排序后为1的
			" Join (Select Rownum, C6.Fportcode, C6.Fholidayscode As One" +
			" From (Select Distinct C4.Fportcode, C5.Fholidayscode" +
			" From "+pub.yssGetTableName("Tb_Etf_Param")+" C4" +
			" Join "+pub.yssGetTableName("Tb_Etf_Paramhoildays")+" C5 On C4.Fportcode = C5.Fportcode" +
			" Order By C5.Fholidayscode) C6 Where Rownum = 1) C7 On c.Mbetf = C7.Fportcode" +
			//节假日倒序后为1的
			" Join (Select Rownum, C6.Fportcode, C6.Fholidayscode As Two" +
			" From (Select Distinct C4.Fportcode, C5.Fholidayscode" +
			" From "+pub.yssGetTableName("Tb_Etf_Param")+" C4" +
			" Join "+pub.yssGetTableName("Tb_Etf_Paramhoildays")+" C5 On C4.Fportcode = C5.Fportcode" +
			" Order By C5.Fholidayscode Desc) " +
			" C6 Where Rownum = 1) C8 On c.Mbetf = C8.Fportcode) " +
			" d On d.Fportcode = a.Fportcode"+
			//证券
			" Join (Select F1.Fsecuritycode Fsecuritycode1," +
			" Substr(F1.Fsecuritycode, 1, Length(F1.Fsecuritycode) - 3) Fsecuritycode "+
			" From "+pub.yssGetTableName("Tb_Para_Security")+
			" F1) f On f.Fsecuritycode = a.Fsetid "+
			"";
		return query;
	}
	
	
}
