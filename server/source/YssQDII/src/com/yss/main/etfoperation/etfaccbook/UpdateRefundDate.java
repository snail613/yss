package com.yss.main.etfoperation.etfaccbook;

import java.sql.Connection;

import com.yss.util.YssException;

/**
 * 此类处理批量更新台账表中的退款日期
 * @author xuqiji 20091112
 *
 */
public class UpdateRefundDate extends CtlETFAccBook{

	public UpdateRefundDate() {
		super();
		
	}
	
	/**批量更新台账表中的退款日期,shashijie 2013-3-5 STORY 3693 这里的方法名与类名相同,变成构造方法了,所以改了 */
	public String updateRefundDate(String type) throws YssException{
		String sUpdateState = "true";
		StringBuffer buff = null;
		Connection conn = null;
		boolean bTrans = true;
		String[] sType = null;
		String[] sData = null;
		//String[] sPortCode = null;//无用注释
		try{
			buff = new StringBuffer(1000);
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			
			sType = type.split("/t");// 解析前台传来的数据
			sData = sType[1].split("\f\f");
			
			buff.append(" update ").append(pub.yssGetTableName("Tb_ETF_StandingBook"));
			buff.append(" set FRefundDate = ").append(dbl.sqlDate(sData[3]));
			buff.append(" where FPortCode in(").append(this.operSql.sqlCodes(sData[2])).append(")");
			buff.append(" and FBs =").append(dbl.sqlString(sData[1]));
			buff.append(" and FBuyDate = ").append(dbl.sqlDate(sData[0]));
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			
		}catch (Exception e) {
			throw new YssException("批量更新台账表中的退款日期出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
		return sUpdateState;
	}
}
















