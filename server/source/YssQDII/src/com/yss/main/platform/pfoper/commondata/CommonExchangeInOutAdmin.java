package com.yss.main.platform.pfoper.commondata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.main.platform.pfoper.commondata.pojo.CommonExchangeInOutBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CommonExchangeInOutAdmin extends BaseCommonData {
	
	public CommonExchangeInOutAdmin() {
		exInOut = new CommonExchangeInOutBean();
    }

    private CommonExchangeInOutBean exInOut = null;
    
    /**
     * 新增
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String sqlStr = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "insert into TMP_EXCHANGE_INOUT (FPORTCODE,FTRADEDATE,FSETTLEDATE,FTYPE,FBCURYCODE,FBMONEY) values(" +
                dbl.sqlString(exInOut.getPortCode()) + "," +
                dbl.sqlDate(exInOut.getTradeDate()) + "," +
                dbl.sqlDate(exInOut.getSettleDate()) + "," +
                dbl.sqlString(exInOut.getType()) + "," +
                dbl.sqlString(exInOut.getCuryCode()) + "," +
                exInOut.getMoney() + ")";
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("新增预测TA表数据出错！", ex);
        }
        return "";
    }

    /**
     * 修改
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String sqlStr = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "update TMP_EXCHANGE_INOUT set " +
            	"FPORTCODE=" + dbl.sqlString(exInOut.getPortCode()) + 
            	",FTRADEDATE=" + dbl.sqlDate(exInOut.getTradeDate()) + 
            	",FSETTLEDATE=" + dbl.sqlDate(exInOut.getSettleDate()) + 
            	",FTYPE=" + dbl.sqlString(exInOut.getType()) +
            	",FBCURYCODE=" + dbl.sqlString(exInOut.getCuryCode()) +
            	",FBMONEY=" + exInOut.getMoney() + 
                " where FPORTCODE=" + dbl.sqlString(exInOut.getOldPortCode()) +
                " and FTRADEDATE=" + dbl.sqlDate(exInOut.getOldTradeDate()) +
                " and FSETTLEDATE=" + dbl.sqlDate(exInOut.getOldSettleDate()) +
                " and FTYPE=" + dbl.sqlString(exInOut.getOldType()) +
                " and FBCURYCODE=" + dbl.sqlString(exInOut.getCuryCode());
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("修改预测TA表数据出错！", ex);
        }
        return "";

    }

    /**
     * 删除数据
     * @param sMutilRowStr String
     * @return String
     */
    public String delSetting() throws YssException {
        String sqlStr = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "delete TMP_EXCHANGE_INOUT " +
            	" where FPORTCODE=" + dbl.sqlString(exInOut.getOldPortCode()) +
            	" and FTRADEDATE=" + dbl.sqlDate(exInOut.getOldTradeDate()) +
            	" and FSETTLEDATE=" + dbl.sqlDate(exInOut.getOldSettleDate()) +
            	" and FTYPE=" + dbl.sqlString(exInOut.getOldType()) +
            	" and FBCURYCODE=" + dbl.sqlString(exInOut.getOldCuryCode());
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("删除预测TA表数据出错！", ex);
        }
        return "";
    }

    /**
     * 多条新增
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * 取数据
     * @return String
     * @throws YssException
     */
    public String getListViewData() throws YssException {
        HashMap htTabMes = null;
        String sHeader = "";
        String sFields = "";
        String sqlStr = "";
        String sAllData = "";
        StringBuffer bufData = new StringBuffer();
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        try {
            htTabMes = getTableFieldDesc("TMP_EXCHANGE_INOUT"); //取出表的字段信息
            sqlStr = "select * from TMP_EXCHANGE_INOUT " + fillterSql();
            rs = dbl.openResultSet(sqlStr);
            rsmd = rs.getMetaData();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                if (htTabMes.get(rsmd.getColumnName(i + 1)) != null) {
                    sHeader += htTabMes.get(rsmd.getColumnName(i + 1)) + "\t";
                    sFields += rsmd.getColumnName(i + 1) + ",";
                }
            }
            if (sHeader.endsWith("\t")) {
                sHeader = sHeader.substring(0, sHeader.length() - 1);
            }
            if (sFields.endsWith(",")) {
                sFields = sFields.substring(0, sFields.length() - 1);
            } while (rs.next()) {
                setexInOutAttr(rs);
                bufData.append(buildRowStr(sFields)).append("\r\n");
            }
            sAllData = bufData.toString();
            if (sAllData.endsWith("\r\n")) {
                sAllData = sAllData.substring(0, sAllData.length() - 2);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sHeader + "\r\f" + sAllData;

    }

    /**
     * 解析
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        exInOut.parseRowStr(sRowStr);
    }

    /**
     * 连接
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(exInOut.getPortCode()).append("\t");
        buf.append(YssFun.formatDate(exInOut.getTradeDate(), "yyyy-MM-dd")).append("\t");
        buf.append(YssFun.formatDate(exInOut.getSettleDate(), "yyyy-MM-dd")).append("\t");
        buf.append(exInOut.getType()).append("\t");
        buf.append(exInOut.getCuryCode()).append("\t");
        buf.append(exInOut.getMoney());
        return buf.toString();
    }

    public String buildRowStr(String sFields) {
        String[] arrField = sFields.split(",");
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arrField.length; i++) {
            if (arrField[i].equalsIgnoreCase("FPortCode")) {
                buf.append(exInOut.getPortCode()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FTradeDate")) {
                buf.append(YssFun.formatDate(exInOut.getTradeDate(), "yyyy-MM-dd")).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FSettleDate")) {
                buf.append(YssFun.formatDate(exInOut.getSettleDate(), "yyyy-MM-dd")).append("\t");
            }  else if (arrField[i].equalsIgnoreCase("FType")) {
                buf.append(exInOut.getType()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FBCuryCode")) {
                buf.append(exInOut.getCuryCode()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FBMoney")) {
                buf.append(exInOut.getMoney());
            } 
        }
        return buf.toString();
    }

    /**
     * 功能扩展
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String operValue(String sType) throws YssException {
        String reStr = "";
        if (sType != null && sType.equalsIgnoreCase("getcontrol")) {
            reStr = "CtlExchangeInOut";
            return reStr;
        }
        return "";
    }

    /**
     * 条件筛选
     * @return String
     */
    public String fillterSql() {
        String sFilter = "";
        if (exInOut.getFilterType() != null) {
            sFilter = " where 1=1 ";
            if (exInOut.getFilterType().getPortCode() != null && exInOut.getFilterType().getPortCode().length() > 0) {
                sFilter += " and FPortCode like '" + exInOut.getFilterType().getPortCode().replaceAll("'", "''") + "%'";
            }
            if (exInOut.getFilterType().getTradeDate() != null && !YssFun.formatDate(exInOut.getFilterType().getTradeDate(), "yyyy-MM-dd").equals("9998-12-31")) {
                sFilter += " and FTradeDate =" + dbl.sqlDate(exInOut.getFilterType().getTradeDate());
            }
        }
        return sFilter;
    }

    private void setexInOutAttr(ResultSet rs) throws SQLException {
        exInOut.setPortCode(rs.getString("FPortCode"));
        exInOut.setTradeDate(rs.getDate("FTradeDate"));
        exInOut.setSettleDate(rs.getDate("FSettleDate"));
        exInOut.setType(rs.getString("FType"));
        exInOut.setCuryCode(rs.getString("FBCuryCode"));
        exInOut.setMoney(rs.getDouble("FBMoney"));
    }

    public void checkInput(byte btOper) throws YssException {
  
    	try {
    		checkData(btOper, "TMP_EXCHANGE_INOUT", "FPortCode,FTradeDate,FSettleDate,FType,FBCuryCode",
                  dbl.sqlString(exInOut.getPortCode()) + ";" + dbl.sqlDate(exInOut.getTradeDate()) + ";" +
                  dbl.sqlDate(exInOut.getSettleDate()) + ";" + dbl.sqlString(exInOut.getType()) + ";" + dbl.sqlString(exInOut.getCuryCode())+";",
                  dbl.sqlString(exInOut.getOldPortCode()) + ";" + dbl.sqlDate(exInOut.getOldTradeDate()) + ";" +
                  dbl.sqlDate(exInOut.getOldSettleDate()) + ";" + dbl.sqlString(exInOut.getOldType()) + ";" + dbl.sqlString(exInOut.getOldCuryCode())+";");
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage());
    	}
    }
}
