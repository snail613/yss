package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by songjie 2012.07.17
 * STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
 * @author 宋洁
 *
 */
public class Ora1010055 extends BaseDbUpdate{
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateSubTradeInfo(hmInfo);
			addSequence();
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0055更新出错！", ex);
		}
	}
	
	private void updateSubTradeInfo(HashMap hmInfo)throws YssException {
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		
		try{
			//更新ETF申购、ETF赎回交易数据的必须现金替代结算日期为结算日期
			strSql = " update " + pub.yssGetTableName("Tb_Data_SubTrade")
			//edit by songjie 2013.01.30 如果 必须现金替代结算日期 为空  则 更新数据
			+ " set FMtReplaceDate = FSettleDate where FTradeTypeCode in('106','107') and FMtReplaceDate is null";
			
			sqlInfo.append(strSql);
			dbl.executeSql(strSql);
			updTables.append(pub.yssGetTableName("Tb_Data_SubTrade"));	
			
			//---add by yeshenghong 2012.07.23 start---//
			if(!dbl.yssSequenceExist("SEQ_DATA_REDCORDCODE"))//记录号从索引中取
			{
				strSql = " create sequence SEQ_DATA_REDCORDCODE minvalue 1000000 maxvalue 9999999999 " +
						  " start with 1000001 increment by 1 nocache cycle order ";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			//---add by yeshenghong 2012.07.23 end---//
			
			conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0055更新表数据出错！",e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * 添加凭证序列
	 */
	private void addSequence()throws YssException {
	     
		 try{
		   if (!dbl.yssSequenceExist("SEQ_VCH_DATA")){
		      dbl.executeSql("create sequence SEQ_VCH_DATA minvalue 1 maxvalue 999999999 start with 1 increment by 1 cache 10");
		   }
		   
		 }catch(Exception e){
		   throw new YssException("添加序列 SEQ_VCH_DATA 出错");
		 }
	     
	}
	
}
