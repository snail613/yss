package com.yss.main.operdeal.valcheck;

import java.sql.ResultSet;
import java.util.Date;
import java.util.ArrayList;
import com.yss.util.YssException;

/**
 *
 * <p>Title: </p>
 * <p>Description: 检查现金库存中的账户信息是否有效</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CheckCashStock
    extends BaseValCheck {

    public CheckCashStock() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws Exception {
        String sReturn = "";
        ResultSet rs = null;
        String strSql = "";
        int iIsError = 0; //记录出错数据数量
        try {
            strSql =
                "SELECT a.FCASHACCCODE, a.FStorageDate " +
                "FROM (SELECT * FROM " + pub.yssGetTableName("Tb_Stock_Cash") +
                " WHERE FPortCode = " + dbl.sqlString(sPortCode) + " AND FCHECKSTATE = 1 AND FStorageDate = "
                + dbl.sqlDate(dTheDay) + ") a " +
                " LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " WHERE FCHECKSTATE = 1) b ON a.FCASHACCCODE = b.FCASHACCCODE " +
                " WHERE b.FCASHACCCODE IS NULL " +
                " ORDER BY a.FCASHACCCODE";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (iIsError == 0) {
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendValCheckRunDesc("\r\n        以下现金库存的账户信息无效：");
                    //add by songjie 2012.10.12 STORY #2344 QDV4建行2012年3月2日05_A
                    //获取业务日志信息
					this.checkInfos += "\r\n        以下现金库存的账户信息无效：";
                }
                runStatus.appendValCheckRunDesc("\r\n            组合：" + sPortCode +
                                                "\r\n            日期：" + rs.getString("FStorageDate") +
                                                "\r\n            帐户：" + rs.getString("FCASHACCCODE") +
                                                "\r\n            帐户信息无效！");
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4建行2012年3月2日05_A start---//
                //获取业务日志信息
				this.checkInfos += "\r\n            组合：" + sPortCode +
                				   "\r\n            日期：" + rs.getString("FStorageDate") +
                				   "\r\n            帐户：" + rs.getString("FCASHACCCODE") +
                				   "\r\n            帐户信息无效！";
                //---add by songjie 2012.10.12 STORY #2344 QDV4建行2012年3月2日05_A end---//
                
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n        以下现金库存的账户信息无效：" +
                			"\r\n            组合：" + sPortCode +
                            "\r\n            日期：" + rs.getString("FStorageDate") +
                            "\r\n            帐户：" + rs.getString("FCASHACCCODE") +
                            "\r\n            帐户信息无效！");
                }
                
                iIsError++;
                this.sIsError = "false";
            }
        } catch (Exception e) {
            throw new YssException("检查现金库存帐户信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }
}
