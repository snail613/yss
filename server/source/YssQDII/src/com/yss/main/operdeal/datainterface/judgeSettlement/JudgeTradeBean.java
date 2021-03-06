package com.yss.main.operdeal.datainterface.judgeSettlement;

import java.sql.ResultSet;

import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 
 * #758 希望开发重读TA数据、交易数据、外汇数据后，将原资金调拨覆盖功能.txt
 * @author ldaolong 20100418
 *
 */
public class JudgeTradeBean extends DataBase{

	public void inertData() throws YssException {
		ResultSet rs = null;
		String strSql="";
		String securityCodes="";
		String date="";
		try {
			DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
			cusCfg.setYssPub(pub);
			cusCfg.setCusCfgCode(cusCfgCode);
			cusCfg.getSetting();

			// 交易数据提示  tmp_subTrade
			strSql="select distinct d.FSecurityCode,d.fbargaindate from "+ pub.yssGetTableName("Tb_Data_SubTrade") +" d "
					+" join (select fbargaindate,fordernum,fportcode  from "+cusCfg.getTabName()+"  )tmp "
					+" on(d.fordernum = tmp.fordernum and d.fbargaindate=tmp.fbargaindate and d.fportcode=tmp.fportcode)"			
					+ "  where d.FSettleState=1 ";
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				securityCodes +="【"+rs.getString("FSecurityCode")+"】,";
				if(date.indexOf(YssFun.formatDate(rs.getDate("fbargaindate"),"yyyy-MM-dd"))==-1){
					date +="【"+YssFun.formatDate(rs.getDate("fbargaindate"),"yyyy-MM-dd")+"】,";
				}			
			}
			
			if (securityCodes.length()>0){
				if(date.length()==0){
					date="【"+YssFun.formatDate(this.sDate,"yyyy-MM-dd")+"】,";
				}
				throw new YssException("~~n~~请反结算成交日期为" + date + "证券代码为"+securityCodes.substring(0,securityCodes.length()-1)+"的数据！");
			}			
			
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
