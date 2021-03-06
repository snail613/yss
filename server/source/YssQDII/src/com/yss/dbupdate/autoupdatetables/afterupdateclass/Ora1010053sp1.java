package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;


/**
 * @author add by huangqirong 2012-06-01 bug #4679、bug#4667
 *
 */
public class Ora1010053sp1 extends BaseDbUpdate {

		
	@Override
	public void adjustTableData(String sPre) throws YssException {
		// TODO Auto-generated method stub
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            conn.setAutoCommit(bTrans);
            bTrans = true;
            
            //更新原先定期存款的词汇代码0为4
            dbl.executeSql("update Tb_" + sPre + "_cash_savinginacc set FSavingType = 4 where FSavingType = 0 "); 
            
            dbl.executeSql("update Tb_" + sPre + "_cash_savinginacc set Ftradetype = 'first' where Ftradetype is null ");
            
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            throw new YssException("版本1.0.1.0053sp1 执行数据调整SQL 语句出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
}
