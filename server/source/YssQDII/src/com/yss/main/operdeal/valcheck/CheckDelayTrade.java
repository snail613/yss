package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;

/**
 * <p>Title: </p>
 *
 * <p>Description: 检查当日延迟结算的交易记录</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CheckDelayTrade
    extends BaseValCheck {
    public CheckDelayTrade() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws
        Exception {
        String sReturn = "";
        ResultSet rs = null;
        String strSql = "";
        int iIsError = 0; //记录出错数据数量
        try {
            strSql = "SELECT COUNT(a.FNUM) AS CountNum," +
                " a.FPortCode," +
                " b.FPortName" +
                " FROM " + pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Portfolio") + " b ON a.FPortCode = b.FPortCode" +
                " WHERE a.FBargainDate = " + dbl.sqlDate(dTheDay) +
                " AND a.FSettleState = 3" +
                " AND a.FPortCode = " + dbl.sqlString(sPortCode) +
                " AND a.FCheckState = 1" +
                " GROUP BY a.FPortCode, b.FPortName";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (iIsError == 0) {
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendValCheckRunDesc("\r\n        延迟结算的交易记录：");
                    
                    //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    //获取业务日志信息
					this.checkInfos += "\r\n        延迟结算的交易记录：";
                }
                runStatus.appendValCheckRunDesc("\r\n            组合：" +
                                                rs.getString("FPortCode") + " " +
                                                rs.getString("FPortName") +
                                                "\r\n            延迟结算的交易数目：" +
                                                rs.getInt("CountNum"));
                
    			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    			//获取业务日志信息
				this.checkInfos += "\r\n            组合：" +
                					rs.getString("FPortCode") + " " +
                					rs.getString("FPortName") +
                					"\r\n            延迟结算的交易数目：" +
                					rs.getInt("CountNum");
    			//---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n            组合：" +
                            rs.getString("FPortCode") + " " +
                            rs.getString("FPortName") +
                            "\r\n            延迟结算的交易数目：" +
                            rs.getInt("CountNum"));
                }
                iIsError++;
                this.sIsError = "false";
            }
        } catch (Exception e) {
            throw new YssException("检查当日延迟结算的交易记录出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }
}
