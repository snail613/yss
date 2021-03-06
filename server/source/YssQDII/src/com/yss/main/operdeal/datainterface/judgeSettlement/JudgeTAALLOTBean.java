package com.yss.main.operdeal.datainterface.judgeSettlement;

import java.sql.ResultSet;

import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * #758 希望开发重读TA数据、交易数据、外汇数据后，将原资金调拨覆盖功能.txt
 * @author ldaolong 20100418
 *
 */
public class JudgeTAALLOTBean extends DataBase{

	public void inertData() throws YssException {
		ResultSet rs = null;
		String strSql="";
		String date="";
		try {
			DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
			cusCfg.setYssPub(pub);
			cusCfg.setCusCfgCode(cusCfgCode);
			cusCfg.getSetting();

			//表 Tb_001_TA_Trade，结算字段 FSettleState  申购 FSellType='01'
			
			// TA交易数据 TMP_TA_JY_ALLOT

			strSql="select ta.* from "+ pub.yssGetTableName("Tb_TA_Trade") +
					" ta  where ta.FSettleState=1 and  ta.FSellType='01' " +
					" and  ta.ftradedate in(select distinct ftradedate from  "+cusCfg.getTabName()+") "+
					" and ta.fportcode in("+operSql.sqlCodes(this.sPort)+")";
			
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {			
				if(date.indexOf(YssFun.formatDate(rs.getDate("ftradedate"),"yyyy-MM-dd"))== -1){
					date +="【"+YssFun.formatDate(rs.getDate("ftradedate"),"yyyy-MM-dd")+"】,";
				}								
			}
			
			if (date.length()>0){
				throw new YssException("~~n~~请反结算交易日期为"+date.substring(0,date.length()-1)+"的TA交易数据");
			}

		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
	}
}
