package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;

/**
 *
 * <p>Title: </p>
 * <p>Description: 检查估值方法是否有效</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CheckMTVMethod
    extends BaseValCheck {
    public CheckMTVMethod() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws Exception {
        String sReturn = "";
        String strSql = "";
        ResultSet rs = null;
        int iIsError = 0; //记录出错数据数量
        try {
            strSql =
                "SELECT a.FPortCode, c.FPortName, a.FSubCode, b.FMtvName" +
                "  FROM " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + " a" +
                "  LEFT JOIN " + pub.yssGetTableName("Tb_Para_MTVMethod") + " b ON a.FSubCode = b.FMTVCode" +
                "  LEFT JOIN " + pub.yssGetTableName("Tb_Para_Portfolio") + " c ON a.FPortCode = c.FPortCode" +
                " WHERE a.FRelaType = 'MTV'" +
                "   AND b.FCheckState <> 1" +
                "   AND a.FPortcode = " + dbl.sqlString(sPortCode);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (iIsError == 0) {
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendValCheckRunDesc("\r\n        以下估值方法无效：");
                    
                    //add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    //获取业务日志信息
					this.checkInfos += "\r\n        以下估值方法无效：";
                }
                runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                                                "\r\n            估值方法：" + rs.getString("FSubCode") + " " + rs.getString("FMtvName"));
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //获取业务日志信息
				this.checkInfos += "\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                				   "\r\n            估值方法：" + rs.getString("FSubCode") + " " + rs.getString("FMtvName");
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                            "\r\n            估值方法：" + rs.getString("FSubCode") + " " + rs.getString("FMtvName"));
                }
                iIsError++;
                this.sIsError = "false";
            }
        } catch (Exception e) {
            throw new YssException("估值方法有效性检查出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }

}
