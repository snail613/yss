package com.yss.main.operdeal.datainterface.dataCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**************************************************************
 * 数据中心接口：外汇交易表（QDII_RateTrade） STORY #1098 QDV4赢时胜上海2011年05月5日01_A 数据中心
 * 
 * @author baopingping
 * @date 2011.06.15
 */
public class ImpRateTrade extends BaseDataCenter {
	private String mgs = "";

	// 删除QDII_RateTrade表中在前台页面所填写日期范围内的数据    add by #story 1089 baopingping  20110616
	public void delData() throws YssException {
		Connection con = null;
		PreparedStatement ps = null;
		String delsql = null;
		boolean bTran = true;
		try {
			con = loadConnection();
			delsql = "delete from QDII_RateTrade where FTradeDate between "
					+ dbl.sqlDate(sStartDate) + "and " + dbl.sqlDate(sEndDate);
			ps = openPreparedStatement(delsql);
			ps.execute();
			con.commit();
			bTran = false;
			con.setAutoCommit(true);
		} catch (Exception e) {
			mgs = "☆☆☆☆☆ 导入【外汇数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心——外汇数据接口：删除外汇数据出错！！！】\t" + mgs);
		} finally {
			endTransFinal(con, bTran);
			dbl.closeConnection();
			closeStatementFinal(ps);
		}
	}
	// 从Tb_Data_RateTrade表中查询到要添加到QDII_RateTrade表中的结果集  add by #story 1089 baopingping  20110616
	public ResultSet getQDIIDate(String portCode) throws YssException {
		String sql = null;
		ResultSet rs = null;
		try {
			sql = "select a.FNum, a.FTradeDate,a.FPortCode,a.FBCashAccCode,a.FSCashAccCode,a.FBCuryCode,a.fscurycode,"
					+ "a.FSettleDate,a.FBSettleDate,a.FExCuryRate,a.FBMoney,a.FSMoney from "
					+ pub.yssGetTableName("Tb_Data_RateTrade")
					+ " a where "
					+ "a.FTradeDate between "
					+ dbl.sqlDate(sStartDate)
					+ " and "
					+ dbl.sqlDate(sEndDate)
					+ "and a.FBPORTCODE in ("//modified  yeshenghong to support mutiple groups 20130412
					+ dbl.sqlString(portCode) + ")" + "order by a.FTradeDate";
			rs = dbl.openResultSet(sql);
			return rs;
		} catch (Exception e) {
			mgs = "☆☆☆☆☆ 导入【外汇数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心——外汇数据接口：获取外汇数据出错！！！】\t" + mgs);
		}
	}

	// 将所查到数据添加到QDII_RateTrade表中  add by #story 1089 baopingping  20110616
	public String addDate() throws YssException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sBeginDate = "";
		String sFinishDate = "";
		String msg = "☆☆☆☆☆  所选日期没有【外汇数据】 ，请核对后再重新导入 ☆☆☆☆☆\r\n ";
		boolean flag = false;
		boolean bTrans = true;
		String curGroup = "";
		String curPort = "";
		String preTb = pub.getPrefixTB();
		try {
			String sql="insert into QDII_RateTrade(FNum,FTradeDate,FPortCode,FBCashAccCode,FSCashAccCode,FBCuryCode,FSCuryCode,FSettleDate,FBSettleDate,FExCuryRate,FBMoney,FSMoney) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = openPreparedStatement(sql);
			int count = 0;
			for(int i=0;i<tmpPortCodes.length;i++){
				if(tmpPortCodes[i].indexOf("-")>0)
				{
					curGroup = tmpPortCodes[i].split("-")[0];
				    curPort = tmpPortCodes[i].split("-")[1];//add  yeshenghong to support mutiple groups 20130412
				    pub.setPrefixTB(curGroup);
				}
				else
				{
					curGroup = pub.getAssetGroupCode();
				    curPort = tmpPortCodes[i];//add  yeshenghong to support mutiple groups 20130412
				}
				rs = getQDIIDate(curPort);
				
				while (rs.next()) {
					if (count == 0) {
						flag = true;
						sBeginDate=sStartDate;													
					}
					ps.setString(1, rs.getString("FNum") + i );
					ps.setDate(2, rs.getDate("FTradeDate"));
					ps.setString(3, rs.getString("FPortCode"));
					ps.setString(4, rs.getString("FBCashAccCode"));
					ps.setString(5, rs.getString("FSCashAccCode"));
					ps.setString(6, rs.getString("FBCuryCode"));
					ps.setString(7, rs.getString("FSCuryCode"));
					ps.setDate(8, rs.getDate("fsettleDate"));
					ps.setDate(9, rs.getDate("fbsettleDate"));
					ps.setDouble(10, rs.getDouble("FExCuryRate"));
					ps.setDouble(11, rs.getDouble("FBMoney"));
					ps.setDouble(12, rs.getDouble("FSMoney"));
					count++;
					ps.addBatch();
					sFinishDate=sEndDate;
				}
			}
			if(!curGroup.equals(""))
			{
				pub.setPrefixTB(preTb);
			}
			if (flag) {
				ps.executeBatch();
				con.commit();
				bTrans = false;
				con.setAutoCommit(true);
			}
			if (count > 0) {
				
				if (sBeginDate.equalsIgnoreCase(sFinishDate)) {
					msg = "★★★★★ 导入【" + sBeginDate + "日 外汇数据】成功 ★★★★★ \r\n";
				} else {
					msg = "★★★★★ 导入【" + sBeginDate + " 至 " + sFinishDate
							+ "日外汇数据】成功 ★★★★★ \r\n";
				}
				
			}

			return msg;
		} catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【外汇数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心——外汇数据接口：导入外汇数据出错！！！】\t" + msg);
		} finally {
			dbl.closeConnection();
			closeStatementFinal(ps);
			endTransFinal(con, bTrans);
		}
	}

	public String impData() throws YssException {
		
		delData();
		
		return addDate( );
	}
}
