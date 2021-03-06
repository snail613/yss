/**@author shashijie
*  @version 创建时间：2012-5-5 下午03:52:59 STORY 2565
*  类说明:篮子估值
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

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class ImportBraket extends DataBase {
	
	
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
		ps.setDate(2, YssFun.toSqlDate(dDate));//日期
		ps.setDouble(3, Double.valueOf(map.get("FBraket").toString()));//篮子估值
	}

	/**shashijie 2012-5-5 STORY 2565
	* @param dDate
	* @param fPort
	* @return*/
	private String getDelete(Date dDate, String fPort) {
		String query = 
			" delete From "+pub.yssGetTableName("Tb_ETF_BraketMarket")+
	        " where FDate = "+dbl.sqlDate(dDate);
		return query;
	}
	
	/**shashijie 2012-5-5 STORY 2565
	* @return*/
	private String getInsert() {
		String query = " insert into "+
			pub.yssGetTableName("Tb_ETF_BraketMarket")+
				"(" +
				" FSecurityCode ,"+//证券代码
				" FDate ,"+//日期
				" FBraket "+//篮子估值
				
				")"+
				" Values ( " +
				" ?,?,?"+
				" ) ";
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

	/**shashijie 2012-5-5 STORY 2565
	* @param rs
	* @param dDate
	* @param fPort
	* @return*/
	private Map<String, Object> getHashMap(ResultSet rs, Date dDate,
			String fPort) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("FSecurityCode", rs.getString("FSetid"));//证券代码
		map.put("FBraket", YssD.round(rs.getDouble("FBraket"),2));//篮子估值
		return map;
	}

	/**shashijie 2012-5-5 STORY 2565
	* @param dDate
	* @param fPort
	* @return*/
	private String getSql(Date dDate, String fPort) {
		String query = 
			" Select b.Fstandardmoneymarketvalue as FBraket, B4.Fportcode, f.Fsecuritycode1 Fsetid" +
			" From "+pub.yssGetTableName("Tb_Rep_Guessvalue")+" b" +
			" Join (Select B1.Fsetid, B1.Fsetcode, B3.FPortcode ,Max(B1.FYear) FYear " +
			" From Lsetlist B1 Join (Select B2.Fassetcode, B2.Fportcode" +
			" From "+pub.yssGetTableName("Tb_Para_Portfolio")+
			" B2) B3 On B1.Fsetid = B3.Fassetcode " +
			" Group By B1.Fsetid, B1.Fsetcode ,B3.FPortcode ) B4 On B4.Fsetcode = b.Fportcode" +
			//证券
			" Join (Select F1.Fsecuritycode Fsecuritycode1," +
			" Substr(F1.Fsecuritycode, 1, Length(F1.Fsecuritycode) - 3) Fsecuritycode "+
			" From "+pub.yssGetTableName("Tb_Para_Security")+
			" F1) f On f.Fsecuritycode = B4.Fsetid "+
			
			" Where b.Fdate = "+dbl.sqlDate(dDate)+
			//" And B4.Fportcode = "+dbl.sqlString(fPort)+ //modify by fangjiang 2013.05.18 STORY #3856 不能加这个条件
			" And b.Facctcode = '9801'";
		return query;
	}
	
	
}
