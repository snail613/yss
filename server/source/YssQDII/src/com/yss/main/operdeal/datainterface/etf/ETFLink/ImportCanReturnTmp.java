/**@author zhouwei 20120621
*  @version 
*  类说明 : 退补款导入(华夏)
*/
package com.yss.main.operdeal.datainterface.etf.ETFLink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
/*
 * story 2727 add by zhouwei 20120620 
 * ETF退补款临时表
 * */
public class ImportCanReturnTmp  extends DataBase {

	/** 程序入口 */
	public void inertData() throws YssException {boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		PreparedStatement ps = null;
		ResultSet rs=null;
		String securityCode = "";
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			//先删除
			String strSql = getDelete();
			dbl.executeSql(strSql);			
			strSql = getInsert();//新增SQL
			ps = conn.prepareStatement(strSql);
			//查询语句
			strSql=getSql();
			rs=dbl.openResultSet(strSql);
			//批量增加
			BaseOperDeal operDeal = new BaseOperDeal();
	        operDeal.setYssPub(pub);
			while(rs.next()){
				Date returnDate=operDeal.getWorkDay(rs.getString("fholidayscode"), rs.getDate("FRefundDate"),1);
				ps.setDouble(1, rs.getDouble("FSumReturn"));//应退合计(补款金额)
				ps.setString(2, rs.getString("FSecurityCode"));//证券代码
				ps.setDate(3,  new java.sql.Date(returnDate.getTime()));//退款日期
				ps.setDate(4, rs.getDate("FBuyDate"));//申购日期
				ps.setDate(5, rs.getDate("FDate"));//清算数据日期
				ps.setString(6, rs.getString("FSeatCode"));//席位代码
				ps.setDouble(7, rs.getDouble("FRepCash"));//退款金额
				ps.setString(8, rs.getString("FSeatNum"));//  席位号
				ps.setString(9, rs.getString("FBs"));//交易类型
				ps.setString(10, rs.getString("FStockHolderCode"));//股东代码
				ps.setString(11, rs.getString("FConsignNum"));//委托序号 - 成交号码
				ps.setString(12, rs.getString("fportcode"));//组合
				//---edit by songjie 2012.06.27 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A start---//
				securityCode = rs.getString("FZhSecurityCode");
				ps.setString(13, securityCode);//篮子股票证券代码
				//---edit by songjie 2012.06.27 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A end---//
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("保存数据出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(ps);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * add by songjie 2012.06.27 
	 * STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
	 * @param Fsecuritycode
	 * @return
	 */
	private String getFSecurityCode(String Fsecuritycode) {
		ResultSet rs = null;
		String value = Fsecuritycode.trim();
		try {
			String query = " Select s.FSecurityCode From "+pub.yssGetTableName("tb_para_security")+
				" s Where s.FCheckState = 1 And Lpad(Substr(s.FSecuritycode,1,length(s.FSecuritycode)-3),5,'0') = "+
				dbl.sqlString(Fsecuritycode.trim());

            rs = dbl.openResultSet(query);
            if(rs.next()){
            	value = rs.getString("FSecurityCode"); //证券代码
            }else{
            	value = " ";
            }
		} catch (Exception e) {

		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}
	
	/**数据库插入语句
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
				" FStockHolderCode,"+//股东代码
				" FConsignNum,"+//委托序号
				" FPortCode,"+//组合代码
				" FZHSecurityCode)"+//篮子股票证券代码
				" Values ( " +
				" ?,?,?,?,?,?,?,?,?,?,?,?,?"+//12
				" ) ";
	return query;
	}
	
	/**删除语句
	* @param dDate
	* @param fPort
	* @return*/
	private String getDelete() {
		String query = 
			" delete From "+pub.yssGetTableName("Tb_ETF_Book")+
	        " where FDate>="+dbl.sqlDate(this.sDate)+" and FDate<="+dbl.sqlDate(this.endDate)+
	        " and fportcode in ("+operSql.sqlCodes(this.sPort)+")";
		return query;
	}
			
	/**zhouwei 查询语句
	* @param dDate
	* @param fPort
	* @return*/
	private String getSql() {
		String query = 
			" select 0 as Fsumreturn,a.Fsecuritycode,a.FDate as FRefundDate,a.FBuyDate,a.FDate,a.fseatcode,"
			+ "a.FRepCash,a.fseatcode as FSeatNum,"
			+ "case when d.FTradeTypeCode ='102' then 'B' when d.FTradeTypeCode ='103' then 'S' end as FBs,"// B为申购，S为赎回
			+ "a.fstockholdercode,a.FConsignNum,d.fportcode,c.fholidayscode,a.FZhSecurityCode,a.FQsCjhm "
			+ " from (select Fsecuritycode, FDate, FBuyDate, FRepCash,fseatcode,fstockholdercode,"
			+ " (FConsignNum || '-' || FQsCjhm) as FConsignNum,"
			+ "(LTRIM(FZhSecurityCode,'0') || ' HK') as FZhSecurityCode,FQsCjhm,fywlb "
            + " from GuanLiRenQingSuan) a"
			+ " join (select fseatcode,fseatnum from "
			+ pub.yssGetTableName("tb_para_tradeseat")
			+ " where fcheckstate=1) b on a.fseatcode=b.fseatnum"
			+ " join (select * from "
			+ pub.yssGetTableName("tb_para_security")
			+ "  where fcheckstate=1) c on a.fsecuritycode=c.fsecuritycode"
			/**shashijie 2012-6-27 STORY 2727 解决产生重复数据问题 */
			+ " left join (select Distinct FConsignNum,FTradeTypeCode,fportcode,FBargainDate from "
			+ pub.yssGetTableName("tb_ETF_JSMX")
			+ " where fcheckstate=1 And FPortcode = "+dbl.sqlString(this.sPort) 
			+ " and FDate <= " + dbl.sqlDate(this.sDate) +") d "
			/**end*/
			+ " on a.FConsignNum=d.FConsignNum and a.Fbuydate = d.FBargainDate "
			+ " where a.fywlb in('L8','L9') and (d.FTradeTypeCode ='103' or d.FTradeTypeCode ='102') and d.fportcode in ("
			+ operSql.sqlCodes(this.sPort)
			+ ") and exists (select faimetfcode from "
			+ pub.yssGetTableName("Tb_Para_Portfolio")
			+ " port where port.faimetfcode = a.Fsecuritycode) "
			+ " and a.FDate>="
			+ dbl.sqlDate(this.sDate)
			+ " and a.FDate<="
			+ dbl.sqlDate(this.endDate);
		return query;
	}
	
}
