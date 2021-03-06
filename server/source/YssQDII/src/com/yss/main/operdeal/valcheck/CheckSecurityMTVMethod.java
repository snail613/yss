package com.yss.main.operdeal.valcheck;

import java.util.Date;
import java.sql.ResultSet;
import com.yss.util.YssException;

/**
 *
 * <p>Title: </p>
 * <p>Description: 检查持仓证券是否有对应的估值方法</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CheckSecurityMTVMethod
    extends BaseValCheck {
    public CheckSecurityMTVMethod() {
    }

    public String doCheck(Date dTheDay, String sPortCode) throws Exception {
        String sReturn = "";
        String strSql = "";
        ResultSet rs = null;
        int iIsError = 0; //记录出错数据数量 
        try {
            strSql =
                "SELECT a.FSecurityCode, e.FPortCode, e.FPortName, f.FSecurityName, a.FStoragedate" +
                " FROM " + pub.yssGetTableName("TB_STOCK_SECURITY") + " a" +
                " LEFT JOIN (SELECT c.*, d.FSubCode " +
                " FROM " + pub.yssGetTableName("Tb_Para_MTVMethodLink") + " c " + //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
                " JOIN (SELECT * " +
                " FROM " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
                " WHERE FRelaType = 'MTV' " +
                " AND FPortCode = " + dbl.sqlString(sPortCode) +
                " ) d ON c.FMtvCode = d.FSubCode) b ON a.FSecurityCode = b.FLinkCode " +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Portfolio") + " e ON a.FPortCode = e.FPortCode " + //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Security") + " f ON a.FSecurityCode = f.FSecurityCode " + //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306 BugNO:MS00306
                " WHERE b.FMtvCode IS NULL " +
                " AND a.FPortCode = " + dbl.sqlString(sPortCode) +
                " AND a.FStorageDate = " + dbl.sqlDate(dTheDay);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (iIsError == 0) {
                	//add by guyichuan STORY #1236 2011.07.13
                	runStatus.appendSchRunDesc(
                    "\r\n        ------------------------------------");
                	runStatus.appendSchRunDesc("\r\n        以下证券估值方法的链接无效：");
                	//--end-STORY #1236--
                    runStatus.appendValCheckRunDesc(
                        "\r\n        ------------------------------------");
                    runStatus.appendValCheckRunDesc("\r\n        以下证券估值方法的链接无效：");
                }
                //add by guyichuan STORY #1236 2011.07.13
                runStatus.appendSchRunDesc("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                        "\r\n            日期：" + rs.getDate("FStoragedate") +
                        "\r\n            证券库存：" + rs.getString("FSecurityCode") + " " + rs.getString("FSecurityName"));
                //--end-STORY #1236--
                runStatus.appendValCheckRunDesc("\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                                                "\r\n            日期：" + rs.getDate("FStoragedate") +
                                                "\r\n            证券库存：" + rs.getString("FSecurityCode") + " " + rs.getString("FSecurityName"));
                if (this.sNeedLog.equals("true"))
                {
                	this.writeLog("\r\n        以下证券估值方法的链接无效：" +
                			"\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                            "\r\n            日期：" + rs.getDate("FStoragedate") +
                            "\r\n            证券库存：" + rs.getString("FSecurityCode") + " " + rs.getString("FSecurityName"));
                }
                iIsError++;
                this.sIsError = "false";
                
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //获取日志信息
				this.checkInfos += "\r\n        以下证券估值方法的链接无效：" +
                                   "\r\n            组合：" + rs.getString("FPortCode") + " " + rs.getString("FPortName") +
                				   "\r\n            日期：" + rs.getDate("FStoragedate") +
                				   "\r\n            证券库存：" + rs.getString("FSecurityCode") + " " + rs.getString("FSecurityName");
                //---add by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }
        } catch (Exception e) {
            throw new YssException("检查持仓股票是否与估值方法链接时出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sReturn;
    }

}
