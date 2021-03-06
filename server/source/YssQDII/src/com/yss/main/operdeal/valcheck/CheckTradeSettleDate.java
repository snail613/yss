package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;

/**
 *
 * <p>Title: </p>
 * <p>Description: 检查是否存在交易日期大于结算日期的业务资料</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CheckTradeSettleDate
    extends BaseValCheck {
    public CheckTradeSettleDate() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws Exception {
        String sReturn = "";
        String strSql = "";
        ResultSet rs = null;
        int iIsError = 0; //记录出错数据数量
        try {
            strSql =
                "SELECT a.FNum, b.FPortCode, b.FPortName" +
                "  FROM " + pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                "  LEFT JOIN " + pub.yssGetTableName("Tb_Para_Portfolio") + " b ON a.FPortCode = b.FPortCode" +
                " WHERE a.FSettleDate < FBargainDate" +
                "   AND a.FPortCode = " + dbl.sqlString(sPortCode) +
                "   AND a.FBargainDate = " + dbl.sqlDate(dTheDay) +
                "   AND a.FCheckState = 1";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (iIsError == 0) {
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendValCheckRunDesc("\r\n        以下业务资料的交易日期大于结算日期：");
                }
                runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                                                "\r\n            交易：" + rs.getString("FNum") +
                                                "\r\n            结算日期小于交易日期！");
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                            "\r\n            交易：" + rs.getString("FNum") +
                            "\r\n            结算日期小于交易日期！");
                }
                iIsError++;
                this.sIsError = "false";
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //获取日志信息
				this.checkInfos += "\r\n        以下业务资料的交易日期大于结算日期：" +
                				   "\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                				   "\r\n            交易：" + rs.getString("FNum") +
                				   "\r\n            结算日期小于交易日期！";
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
            }
        } catch (Exception e) {
            throw new YssException("检查是否存在交易日期大于结算日期业务资料出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }
}
