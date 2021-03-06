package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * 
 * add by wangzuochun 2011.02.09 BUG #1059 证券应收应付数据中存在应收股息和应收股息汇兑损益的历史数据
 *
 */
public class Ora1010037sp1 extends BaseDbUpdate {
	public Ora1010037sp1(){
		
	}
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateRecPayData(); 
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0037sp1 更新出错！", ex);
		}
	}
	
	//调整数据
    public void updateRecPayData() throws YssException {
        boolean bTrans = false;
        StringBuffer bufSql = new StringBuffer(5000);
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //------------将证券应收应付中分红的汇兑损益数据删除
            bufSql.append("update ");
            bufSql.append(pub.yssGetTableName("TB_Data_SecRecPay"));
            bufSql.append(" set FCheckState = 2 ");
            bufSql.append(" where FSubTsfTypeCode in ('9906DV','06DV')");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            
            //------------将证券应收应付库存中分红的汇兑损益数据删除
            bufSql.append("update ");
            bufSql.append(pub.yssGetTableName("TB_Stock_SecRecPay"));
            bufSql.append(" set FCheckState = 2 ");
            bufSql.append(" where FSubTsfTypeCode in ('9906DV','06DV')");
            dbl.executeSql(bufSql.toString());
            bufSql.delete(0, bufSql.length());
            
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //----------------------------------------------------------
        } catch (Exception e) {
            throw new YssException("股票分红数据更新出错！");
        } finally {
            bufSql = null;
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
