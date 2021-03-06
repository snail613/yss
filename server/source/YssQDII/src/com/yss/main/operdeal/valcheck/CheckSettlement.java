package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssException;

public class CheckSettlement extends BaseValCheck {

	 public CheckSettlement() {
	    }

	    public String doCheck(Date dTheDay, String sPortCode) throws
	        Exception {
	        String sReturn = "";
	        String strSql = "";
	        ResultSet rs = null;
	        int iIsError = 0; //记录出错数据数量
	        try {
	            
	            strSql =" select a.fnum,a.fsecuritycode,b.fportcode,b.fportname from "+
	                    " ( select fnum,fsecuritycode,fportcode from "+ pub.yssGetTableName("tb_data_subtrade")+
	                    " where fcheckstate=1 and fsettlestate=0 and fbargaindate= "+ dbl.sqlDate(dTheDay) +
	                    " and fportcode= "+ dbl.sqlString(sPortCode) + " )a "+
	                    " left join "+
	                    " ( select fportcode,fportname from "+ pub.yssGetTableName("tb_para_portfolio")+" ) b"+
	                    " on a.fportcode = b.fportcode ";
	                
	            rs = dbl.openResultSet(strSql); 
	            while (rs.next()) {
	                if (iIsError == 0) {
	                    runStatus.appendValCheckRunDesc(
	                        "\r\n        ------------------------------------");
	                    runStatus.appendValCheckRunDesc("\r\n        以下交易证券还没有进行结算：");
	                }
	                runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
	                		                        "\r\n            交易编号：" + rs.getString("fnum")+
	                                                "\r\n            交易证券：" + rs.getString("FSECURITYCODE"));
	                if (this.sNeedLog.equals("true"))
	                {
	                	this.writeLog("\r\n        以下交易证券还没有进行结算：" +
	                			"\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
		                        "\r\n            交易编号：" + rs.getString("fnum")+
                                "\r\n            交易证券：" + rs.getString("FSECURITYCODE"));
	                }
	                iIsError++;
	                this.sIsError = "false";
	                
	                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	                //获取日志信息
					this.checkInfos += "\r\n        以下交易证券还没有进行结算：" +
        							   "\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
        							   "\r\n            交易编号：" + rs.getString("fnum")+
        							   "\r\n            交易证券：" + rs.getString("FSECURITYCODE");
	                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	            }
	            dbl.closeResultSetFinal(rs);
	            
			strSql = " select a.fnum,b.fportcode,b.fportname from "
					+ " ( select fnum,fportcode from "
					+ pub.yssGetTableName("tb_ta_trade")
					+ " where fcheckstate=1 and fsettlestate=0 and FConfimDate= "
					+ dbl.sqlDate(dTheDay)
					+ " and fportcode= "
					+ dbl.sqlString(sPortCode)
					+ " )a "
					+ " left join "
					+ " ( select fportcode,fportname from "
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " ) b"
					+ " on a.fportcode = b.fportcode ";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (iIsError == 0) {
					runStatus.appendValCheckRunDesc("\r\n        ------------------------------------");
				}
				runStatus.appendValCheckRunDesc("\r\n        以下TA交易数据还没有进行结算：");
				runStatus.appendValCheckRunDesc("\r\n            组合："+ rs.getString("FPortCode") + " "+ rs.getString("FPortName") +
						                        "\r\n            交易编号："+ rs.getString("fnum"));
				iIsError++;
				this.sIsError = "false";
				
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                this.checkInfos += "\r\n        以下TA交易数据还没有进行结算：" +
                				   "\r\n            组合："+ rs.getString("FPortCode") + " "+ rs.getString("FPortName") +
                				   "\r\n            交易编号："+ rs.getString("fnum");
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			}
			dbl.closeResultSetFinal(rs);

		} catch (Exception e) {
			throw new YssException("检查当日交易数据是否全部结算出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return sReturn;
	    }
}
