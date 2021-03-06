/**@author shashijie
*  @version 创建时间：2012-5-5 下午07:17:05 STORY 2565
*  类说明 : 退补款导入
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
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class ImportCanReturn  extends DataBase {

	/**shashijie 2012-4-26 STORY 2565 程序入口 */
	public void inertData() throws YssException {
		//获取数据集合
		List subBeanList = getBeanList(this.sDate,this.sPort);
		
		//存入数据
		insertSubBeanList(subBeanList,this.sDate,this.sPort);
    }
	
	/**shashijie 2012-5-5 STORY 2565 
	* @param subBeanList
	* @param sDate
	* @param sPort*/
	private void insertSubBeanList(List subBeanList, Date dDate, String fPort) throws YssException {
				
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
		ps.setDouble(1, Double.valueOf(map.get("FSumReturn").toString()));//应退合计(补款金额)
		ps.setString(2, map.get("FSecurityCode").toString());//证券代码
		ps.setDate(3, YssFun.toSqlDate(map.get("FRefundDate").toString()));//退款日期
		ps.setDate(4, YssFun.toSqlDate(map.get("FBargainDate").toString()));//申购日期
		ps.setDate(5, YssFun.toSqlDate(dDate));//清算数据日期
		ps.setString(6, map.get("FSeatCode").toString());//席位代码
		ps.setDouble(7, Double.valueOf(map.get("FRepCash").toString()));//退款金额
		ps.setString(8, map.get("FSeatNum").toString());//  席位号
		ps.setString(9, map.get("FBs").toString());//交易类型
		ps.setString(10, map.get("FStockHolderCode").toString());//股东代码
		
	}
	
	/**shashijie 2012-5-5 STORY 2565
	* @return*/
	private String getInsert() {
		String query = " insert into "+
			pub.yssGetTableName("Tb_ETF_Book")+
				"(" +
				" Fsumreturn ,"+//应退合计(补款金额)
				" Fsecuritycode ,"+//证券代码
				" FRefundDate ,"+//	退款日期
				" FBuyDate ,"+//	申购日期
				" FDate ,"+//清算数据日期
				" FSeatCode ,"+//席位代码
				" FRepCash ,"+//退款金额
				" FSeatNum ,"+//席位号
				" FBs ,"+//交易类型
				" FStockHolderCode "+//股东代码
				
				")"+
				" Values ( " +
				" ?,?,?,?,?,?,?,?,?,?"+//10
				" ) ";
	return query;
	}
	
	/**shashijie 2012-5-5 STORY 2565
	* @param dDate
	* @param fPort
	* @return*/
	private String getDelete(Date dDate, String fPort) {
		String query = 
			" delete From "+pub.yssGetTableName("Tb_ETF_Book")+
	        " where FDate = "+dbl.sqlDate(dDate);
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
				//退款日期与计算出来的一致才处理
				if (getSettleDate(rs.getString("FHolidaysCode"), dDate, 1).
						equals(YssFun.formatDate(rs.getDate("FRefundDate")))
					/**shashijie 2012-6-26 BUG 增加当天必须是工作日判断 */
					&& getSettleDate(rs.getString("FHolidaysCode"), dDate, 0).
						equals(YssFun.formatDate(dDate))
					/**end*/	
				) {
					//对象赋值
					Map<String, Object> map = getHashMap(rs,dDate,fPort);
					list.add(map);
				}
			}
		} catch (Exception e) {
			throw new YssException("获取数据集合出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return list;
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
			throw new YssException("获取结算日期出错!",e);
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
		map.put("FSumReturn", rs.getDouble("Bkje"));//应退合计(补款金额)
		map.put("FSecurityCode", rs.getString("FSecurityCode1"));//证券代码
		map.put("FRefundDate", rs.getDate("FRefundDate"));//	退款日期
		map.put("FBargainDate", rs.getDate("Fbargaindate"));//	申购日期
		map.put("FDate", dDate);//清算数据日期
		map.put("FSeatCode", rs.getString("FSeatCode"));//席位代码
		map.put("FRepCash", rs.getDouble("Tkje"));//退款金额
		map.put("FSeatNum", " ");//席位号
		map.put("FBs", rs.getString("Fbargainbs"));//交易类型
		map.put("FStockHolderCode", rs.getString("Fstockholdercode"));//股东代码
		return map;
	}
	
	/**shashijie 2012-5-5 STORY 2565
	* @param dDate
	* @param fPort
	* @return*/
	private String getSql(Date dDate, String fPort) {
		String query = 
			"Select " +
			" Abs(Nvl(Case When b.Fsumreturn < 0 And b.Fbs = 'S' Then" +
			" Round(b.Fsumreturn * a.Ftradeamount / f.Fnormscale, 2)" +
			" Else 0 End, 0)) As Bkje," +
			
			" Abs(Nvl(Case When b.Fsumreturn < 0 And b.Fbs = 'B' Then" +
			" 0 Else Round(b.Fsumreturn * a.Ftradeamount / f.Fnormscale, 2) " +
			" End, 0)) As Tkje," +
 
			" f.Fholidayscode," +
			" a.Fsecuritycode1," +
			" a.Fdate," +
			" a.Fbargaindate," +
			" a.Fbargainbs," +
			" a.Fcurrencycode," +
			" a.FSeatNum As FSeatCode," +//席位代码
			" a.Fstockholdercode," +
			" b.FRefundDate,"+
			" ' '" +
			" From (Select h.Fportcode," +
			" h.Ftradetypecode," +
			" h.Fsecuritycode1," +
			" h.Fdate," +
			" h.Fbargaindate," +
			" h.Fbargainbs," +
			" h.Ffundsbar," +
			" h.Fcurrencycode," +
			" h.Fseatnum," +
			" h.Fstockholdercode," +
			" Sum(h.Ftradeamount) As Ftradeamount" +
        
			" From "+pub.yssGetTableName("Tb_Etf_Jsmx")+" h" +
			" Where h.Fsecuritytype = 'JJ'" +
			" And h.Fclearmark In ('276', ' ')" +
			" And h.Fcheckstate = 1" +
			" And h.Frecordtype = '003'" +
			" And h.Fresultcode = '0000'" +
			" Group By h.Fportcode," +
			" h.Ftradetypecode," +
			" h.Fsecuritycode1," +
			" h.Fdate," +
			" h.Fbargainbs," +
			" h.Ffundsbar," +
			" h.Fcurrencycode," +
			" h.Fseatnum," +
			" h.Fstockholdercode," +
			" h.Fbargaindate) a" +
			//证券基本信息
			" Join (Select F1.Fsecuritycode, F1.Fholidayscode, F1.Fnormscale" +
			" From "+pub.yssGetTableName("Tb_Para_Security")+
			" F1) f On f.Fsecuritycode = a.Fsecuritycode1" +

			" Join (Select B1.Frefunddate," +
			" B1.Fbuydate," +
			" Nvl(Case When B1.Fbs = 'B' Then B1.Fsumreturn * -1 Else B1.Fsumreturn + nvl(B4.Money,0) End, 0) As Fsumreturn," +
			" B1.Fbs" +
			" From (Select B3.Frefunddate," +
			" B3.Fbuydate, Sum(-b3.Fsumreturn) As Fsumreturn, B3.Fbs" +
			" From "+pub.yssGetTableName("Tb_Etf_Standingbook")+" B3" +
			
			" Where B3.Fsecuritycode != ' '" +
			" And B3.FBUYDATE < "+dbl.sqlDate(dDate)+
			" And B3.Frefunddate > "+dbl.sqlDate(dDate)+
           
			" Group By B3.Frefunddate, B3.Fbuydate, B3.Fbs) B1" +
			" left Join (Select B2.Fdate, Sum(B2.Ftotalmoney) As Money" +
			" From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" B2" +
            " Where B2.Freplacemark = '6'" +
            " Group By B2.Fdate) B4 On B4.Fdate = B1.Fbuydate) b " +
            " On b.Fbuydate =  a.Fbargaindate And b.Fbs = a.Fbargainbs" +
                        
            " Order By a.Fdate";
		return query;
	}
	
}
