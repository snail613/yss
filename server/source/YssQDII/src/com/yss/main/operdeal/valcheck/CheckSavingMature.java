package com.yss.main.operdeal.valcheck;

import java.util.Date;
import com.yss.util.YssException;
import java.sql.ResultSet;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * <p>Title: </p>
 *
 * <p>Description: 对定存到期的提示功能</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 *  MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
 */
public class CheckSavingMature
    extends BaseValCheck {
    public CheckSavingMature() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws Exception {
        String sReturn = "";
        String strSql = "";
        ResultSet rs = null;
        int iIsError = 0; //记录出错数据数量
        int duringMatureDate = 0; //距到期日期的天数
        try {
            strSql =
                "select * from " +
                pub.yssGetTableName("Tb_cash_savinginacc") +
                " where FCheckState = 1 and FTradeType not in (" +
                dbl.sqlString(YssOperCons.YSS_SAVING_BUY) + "," + dbl.sqlString(YssOperCons.YSS_SAVING_CIRCUCATCH) + ")" + //去除转出和通知取款类型的数据
                " and FPortCode = " + dbl.sqlString(sPortCode) +
                " and (" + dbl.sqlDate(dTheDay) + "  between FSavingDate and FMatureDate)"; //获取存入日期和到期日期之间的数据
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (YssFun.dateDiff(YssFun.parseDate("9998-12-31"), rs.getDate("FMaturedate")) == 0) { //若到期日期为9998-12-31，则不进行检查
                    continue;
                }
                duringMatureDate = YssFun.dateDiff(dTheDay, rs.getDate("FMatureDate")); //计算当前日期距到期日期的天数
                if (duringMatureDate <= 3) { //距到期日3日开始提示
                    if (iIsError == 0) {
                        runStatus.appendValCheckRunDesc(
                            "\r\n        ------------------------------------");
                        runStatus.appendValCheckRunDesc("\r\n        以下定存即将到期：");
                        
                        //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                        //获取业务日志信息
						this.checkInfos += "\r\n        以下定存即将到期：";
                    }
                    runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("FPortCode") +
                        "\r\n            定存编号：" + rs.getString("FNum") +
                        "\r\n            距到期日还有" + duringMatureDate + "日。");
                    
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    //获取业务日志信息
					this.checkInfos += "\r\n            组合：" + rs.getString("FPortCode") +
                    					"\r\n            定存编号：" + rs.getString("FNum") +
                    					"\r\n            距到期日还有" + duringMatureDate + "日。";
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n        以下定存即将到期：" +
                			"\r\n            组合：" + rs.getString("FPortCode") +
                            "\r\n            定存编号：" + rs.getString("FNum") +
                            "\r\n            距到期日还有" + duringMatureDate + "日。");
                }
                iIsError++;
                this.sIsError = "false";
            }
        }
        catch (Exception e) {
            throw new YssException("检查定存到期出现异常！", e);
        }
        finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }

}
