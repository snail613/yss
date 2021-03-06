package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;

/**
 *
 * <p>Title: </p>
 * <p>Description: 检查结算日期已到未结算的交易记录</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CheckUnliquidated
    extends BaseValCheck {
    public CheckUnliquidated() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws Exception {
        String sReturn = "";
        String strSql = "";
        ResultSet rs = null;
        int iIsError = 0; //记录未结算交易数据数量
        int iIsTAError = 0; //记录未结算TA交易数据数量
        try {
            //2008.01.22 修改 蒋锦
            //添加 已到结算日期未结算的TA交易记录的检查
            //使用 UNION 连接原来检查交易结算的查询语句
            //在取值时使用 FSettleType 字段将二者分开
            strSql =
                "SELECT COUNT(a.FNUM) AS CountNum, a.FPortCode, b.FPortName, a.FFactSettleDate, '0' AS FSettleType" +
                " FROM " + pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                "  LEFT JOIN " + pub.yssGetTableName("Tb_Para_Portfolio") + " b ON a.FPortCode = b.FPortCode" +
                " WHERE a.FFactSettleDate = " + dbl.sqlDate(dTheDay) +
                "   AND a.FSettleState = 0" +
                "   AND a.FPortCode = " + dbl.sqlString(sPortCode) +
                "   AND a.FCheckState = 1" +
                " GROUP BY a.FPortCode, b.FPortName, a.FFactSettleDate" +
                //--------------2008.01.22 添加 添加 TA 结算的检查---------------//
                " UNION " +
                "SELECT COUNT(a.FNUM) AS CountNum, a.FPortCode, b.FPortName, a.FSettleDate, '1' AS FSettleType" +
                " FROM " + pub.yssGetTableName("TB_TA_Trade") + " a" +
                "  LEFT JOIN " + pub.yssGetTableName("Tb_Para_Portfolio") + " b ON a.FPortCode = b.FPortCode" +
                " WHERE a.FSettleDate = " + dbl.sqlDate(dTheDay) +
                "   AND a.FSettleState = 0" +
                "   AND a.FPortCode = " + dbl.sqlString(sPortCode) +
                "   AND a.FCheckState = 1" +
                " GROUP BY a.FPortCode, b.FPortName, a.FSettleDate";
            //------------------------------------------------------------//

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getString("FSettleType").equalsIgnoreCase("0")) { //等于0的是交易记录
                    if (iIsError == 0) {
                        runStatus.appendValCheckRunDesc(
                            "\r\n        ------------------------------------");
                        runStatus.appendValCheckRunDesc("\r\n        未结算的交易记录：");
                    }
                    runStatus.appendValCheckRunDesc("\r\n            组合：" +
                        rs.getString("FPortCode") + " " +
                        rs.getString("FPortName") +
                        "\r\n            实际结算日期：" +
                        rs.getDate("FFactSettleDate") +
                        "\r\n            未结算交易数目：" +
                        rs.getInt("CountNum"));
                    if (this.sNeedLog.equals("true"))
                    {
                    	this.writeLog("\r\n            组合：" +
                                rs.getString("FPortCode") + " " +
                                rs.getString("FPortName") +
                                "\r\n            实际结算日期：" +
                                rs.getDate("FFactSettleDate") +
                                "\r\n            未结算交易数目：" +
                                rs.getInt("CountNum"));
                    }
                    iIsError++;
                    this.sIsError = "false";
                    
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    //获取日志信息
					this.checkInfos += "\r\n        未结算的交易记录：" +
                    				   "\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                                       "\r\n            实际结算日期：" + rs.getDate("FFactSettleDate") +
                                       "\r\n            未结算交易数目：" + rs.getInt("CountNum");
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }
                if (rs.getString("FSettleType").equalsIgnoreCase("1")) { ////等于1的是 TA 交易记录
                    if (iIsTAError == 0) {
                        runStatus.appendValCheckRunDesc(
                            "\r\n        ------------------------------------");
                        runStatus.appendValCheckRunDesc("\r\n        未结算的TA交易记录：");
                    }
                    runStatus.appendValCheckRunDesc("\r\n            组合：" +
                        rs.getString("FPortCode") + " " +
                        rs.getString("FPortName") +
                        "\r\n            实际结算日期：" +
                        rs.getDate("FFactSettleDate") +
                        "\r\n            未结算交易数目：" +
                        rs.getInt("CountNum"));
                    if (this.sNeedLog.equals("true"))
                    {
                    	this.writeLog("\r\n            组合：" +
                                rs.getString("FPortCode") + " " +
                                rs.getString("FPortName") +
                                "\r\n            实际结算日期：" +
                                rs.getDate("FFactSettleDate") +
                                "\r\n            未结算交易数目：" +
                                rs.getInt("CountNum"));
                    }
                    iIsTAError++;
                    this.sIsError = "false";
                    
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    //获取日志信息
					this.checkInfos += "\r\n        未结算的TA交易记录：" +
                    				   "\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                    				   "\r\n            实际结算日期：" + rs.getDate("FFactSettleDate") +
                    				   "\r\n            未结算交易数目：" + rs.getInt("CountNum");
                    //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }
            }
        } catch (Exception e) {
            throw new YssException("检查已到结算日期尚未结算的交易记录出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }

}
