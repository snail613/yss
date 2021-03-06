package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010029 extends BaseDbUpdate {
    public Ora1010029() {
    }
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTableStructure();
			deleteVoc();//删除词汇数据 by leeyu add 20100526
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0029 更新出错！", ex);
		}
	}
	
	/**
	 * 更新shgh表结构，增加JYFS（交易方式），用于区分上海过户大宗交易和普通交易
	 * 通过数据字典配置的临时表 如果增加了字段，系统不能自动增加该字段。
	 * B股业务
	 * panjunfang add 20100511
	 * @throws YssException
	 */
	private void updateTableStructure() throws YssException{
		Connection conn = null;
		boolean bTrans = false;
		try{
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			if(dbl.yssTableExist("SHGH") && this.existsTabColumn_Ora("SHGH","JYFS")){
				bTrans = true;
				StringBuffer buf = new StringBuffer();
				buf.append("Alter Table SHGH Add JYFS VARCHAR2(2) default 'PT'");
				dbl.executeSql(buf.toString());
				conn.commit();
	            conn.setAutoCommit(bTrans);
	            bTrans = false;
			}
		}catch (Exception e) {
			throw new YssException("更新表结构出错！", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	/**
	 * 删除原词汇中的数据，原因是旧库中有错误的词汇，在使用tools更新时没有删除掉，只能通过删除了
	 * by leeyu create 20100526
	 * @throws YssException
	 */
	private void deleteVoc() throws YssException{
		Connection conn=null;
		boolean bTrans =false;
		String sql="";
		try{
			conn =dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans =true;
			sql="delete from tb_fun_vocabulary where FVocTypeCode='csh_savingtype' and FVocCode='0'";
			dbl.executeSql(sql);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans =false;
		}catch(Exception ex){
			throw new YssException("删除词汇类型为【csh_savingtype】代码为【0】出错！",ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
