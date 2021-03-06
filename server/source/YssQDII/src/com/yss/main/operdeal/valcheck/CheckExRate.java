package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;

/**
 *
 * <p>Title: </p>
 * <p>Description: 检查汇率信息是否有估值日的汇率</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CheckExRate
    extends BaseValCheck {
    public CheckExRate() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws
        Exception {
        String sReturn = "";
        String strSql = "";
        ResultSet rs = null;
        int iIsError = 0; //记录出错数据数量
        try {
            strSql =
                "SELECT a.FCuryCode, d.FCuryName, c.FExRateDate, e.FPortCode, e.FPortName " +
                " FROM (SELECT FCuryCode, FPortCode, FYearMonth " +
                " FROM " + pub.yssGetTableName("TB_STOCK_SECURITY") +
                " WHERE FStorageDate = " + dbl.sqlDate(dTheDay) +
                " AND SUBSTR(FYearMonth, 5, 2) <> '00' " +
                //2008.1 修改 蒋锦 当交易货币为基础货币时将肯定没有汇率信息，所以不作判断
                " AND FCuryCode <> " + dbl.sqlString(pub.getPortBaseCury(sPortCode)) +// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                " AND FPortCode = " + dbl.sqlString(sPortCode) +
                " UNION " +
                " SELECT FCuryCode, FPortCode, FYearMonth " +
                " FROM " + pub.yssGetTableName("TB_STOCK_CASH") +
                " WHERE FStorageDate = " + dbl.sqlDate(dTheDay) +
                " AND SUBSTR(FYearMonth, 5, 2) <> '00' " +
                " AND FCuryCode <> " + dbl.sqlString(pub.getPortBaseCury(sPortCode)) +// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                " AND FPortCode = " + dbl.sqlString(sPortCode) + ") a " +
                " LEFT JOIN (SELECT FCuryCode, FExRateDate " +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                //edit by songjie 2011.05.25 查询已审核的有效地汇率数据
                " WHERE FExRateDate = " + dbl.sqlDate(dTheDay) + " and FCheckState = 1 " +
                " ) b ON a.FCuryCode = b.FCuryCode " +
                " LEFT JOIN (SELECT MAX(FExRateDate) AS FExRateDate, FCuryCode " +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                //edit by songjie 2011.05.25 查询已审核的有效地汇率数据
                " WHERE FExRateDate <= " + dbl.sqlDate(dTheDay) + " and FCheckState = 1 " +
                " GROUP BY FCuryCode) c ON a.FCuryCode = c.FCuryCode " +
                " LEFT JOIN (SELECT FCuryCode, FCuryName FROM " + pub.yssGetTableName("Tb_Para_Currency") +
                " ) d ON a.FCuryCode = d.FCuryCode " +
                " LEFT JOIN (SELECT FPortCode, FPortName FROM " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " ) e ON a.FPortCode = e.FPortCode " +
                " WHERE b.FExRateDate IS NULL ";

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (iIsError == 0) {
                	runStatus
					.appendSchRunDesc(
	                        "\r\n        ------------------------------------");//add by guyichuan STORY #1236 2011.07.13
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendSchRunDesc("\r\n        以下交易货币没有估值日的汇率：");//add by guyichuan STORY #1236 2011.07.13
                    runStatus.appendValCheckRunDesc("\r\n        以下交易货币没有估值日的汇率：");
                    
                    //add by songjie 2012.10.12 STORY #2344 QDV4建行2012年3月2日05_A
                    //获取业务日志信息
					this.checkInfos += "\r\n        以下交易货币没有估值日的汇率：";
                }
              //add by guyichuan STORY #1236 2011.07.13
                runStatus.appendSchRunDesc("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                        "\r\n            交易货币：" + rs.getString("FCuryCode") +
                        "\r\n            汇率日期：" + rs.getDate("FExRateDate"));
             //--end--STORY #1236---
                runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                                                "\r\n            交易货币：" + rs.getString("FCuryCode") +
                                                "\r\n            汇率日期：" + rs.getDate("FExRateDate"));
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4建行2012年3月2日05_A start---//
                //获取业务日志信息
				this.checkInfos += "\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                				   "\r\n            交易货币：" + rs.getString("FCuryCode") +
                				   "\r\n            汇率日期：" + rs.getDate("FExRateDate");
                //---add by songjie 2012.10.12 STORY #2344 QDV4建行2012年3月2日05_A end---//
                
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n        以下交易货币没有估值日的汇率：" +
                			"\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                            "\r\n            交易货币：" + rs.getString("FCuryCode") +
                            "\r\n            汇率日期：" + rs.getDate("FExRateDate"));
                }
                iIsError++;
                this.sIsError = "false";
            }
        } catch (Exception e) {
            throw new YssException("检查汇率信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }
}
