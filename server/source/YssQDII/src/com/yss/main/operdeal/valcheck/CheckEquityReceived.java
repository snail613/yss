package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssException;


/***************************************
 * 【STORY #2406 希望在到帐日执行调度方案时，系统有提示说明当日有权益到账】
 *  检查当日是否有权益到帐
 * @author jsc 20120416
 *
 */
public class CheckEquityReceived extends BaseValCheck {

	
	public CheckEquityReceived() {}

	public String doCheck(Date dTheDay, String sPortCode) throws Exception {

		StringBuffer Msg = new StringBuffer();
		ResultSet rs = null;
		StringBuffer queryBuf = new StringBuffer();
		StringBuffer errMsg = new StringBuffer();
		try{
			errMsg.append("【估值检查-检查当日是否有权益到账】出错...... ");
			
			//1. 交易所数据 
			queryBuf.append(" select a.fsecuritycode, a.fportcode, a.ftradetypecode||'-'||b.ftradetypename as ftradetypecode,a.fbargaindate from ");
			queryBuf.append(pub.yssGetTableName("tb_data_subtrade")).append(" a,").append(pub.yssGetTableName("tb_base_tradetype")).append(" b ");
			queryBuf.append(" where a.fcheckstate = 1 and a.ftradetypecode = b.ftradetypecode ");
			queryBuf.append(" and a.ftradetypecode in ('06', '07', '22')  and a.fportcode = ");
			queryBuf.append(dbl.sqlString(sPortCode)).append(" and a.ffactsettledate= ").append(dbl.sqlDate(dTheDay));
			queryBuf.append(" union all ");
			//2. 开放式基金
			queryBuf.append(" select a.fsecuritycode, a.fportcode, a.ftradetypecode||'-'||b.ftradetypename as ftradetypecode,a.fbargaindate from ");
			queryBuf.append(pub.yssGetTableName("tb_Data_OpenFundTrade")).append(" a,").append(pub.yssGetTableName("tb_base_tradetype")).append(" b ");
			queryBuf.append(" where a.fcheckstate = 1 and a.ftradetypecode = b.ftradetypecode ");
			queryBuf.append(" and a.ftradetypecode in ('06', '22')  and a.fportcode = ");
			queryBuf.append(dbl.sqlString(sPortCode)).append(" and a.FComfDate= ").append(dbl.sqlDate(dTheDay));
			queryBuf.append(" union all ");
			//3. 银行间债券
			queryBuf.append(" select a.fsecuritycode, a.fportcode, a.ftradetypecode||'-'||b.ftradetypename as ftradetypecode,a.fbargaindate from ");
			queryBuf.append(pub.yssGetTableName("tb_Data_IntBakBond")).append(" a,").append(pub.yssGetTableName("tb_base_tradetype")).append(" b ");
			queryBuf.append(" where a.fcheckstate = 1 and a.ftradetypecode = b.ftradetypecode ");
			queryBuf.append(" and a.ftradetypecode in ('06', '22')  and a.fportcode = ");
			queryBuf.append(dbl.sqlString(sPortCode)).append(" and a.FSettleDate= ").append(dbl.sqlDate(dTheDay));
			
			rs = dbl.openResultSet(queryBuf.toString());
			while(rs.next()){

				Msg.append("当日有权益到账数据！");
				if(rs.isFirst()){
					this.sIsError = "false";
					runStatus.appendValCheckRunDesc("\r\n        ------------------------------------");
	                runStatus.appendValCheckRunDesc("\r\n        检测到当天有以下证券有权益到账： ");
	                
                    //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    //获取业务日志信息
					this.checkInfos += "\r\n        检测到当天有以下证券有权益到账： ";
				}
				runStatus.appendValCheckRunDesc("\r\n            组合：" + sPortCode +
                        "\r\n            日期：" + rs.getString("fbargaindate") +
                        "\r\n            证券代码：" + rs.getString("fsecuritycode") +
                        "\r\n            权益类型：" + rs.getString("ftradetypecode"));
				
    			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    			//获取业务日志信息
				this.checkInfos += "\r\n            组合：" + sPortCode +
                				   "\r\n            日期：" + rs.getString("fbargaindate") +
                				   "\r\n            证券代码：" + rs.getString("fsecuritycode") +
                				   "\r\n            权益类型：" + rs.getString("ftradetypecode");
    			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			}
		}catch(Exception e){
			//edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
			 throw new YssException(Msg.length()>0?Msg.toString():errMsg.append(e.getMessage()).toString());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		 return Msg.toString();
	}
}
