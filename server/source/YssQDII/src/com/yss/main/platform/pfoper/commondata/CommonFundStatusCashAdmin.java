package com.yss.main.platform.pfoper.commondata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.main.platform.pfoper.commondata.pojo.CommonFundStatusCash;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CommonFundStatusCashAdmin extends BaseCommonData {

	private CommonFundStatusCash fundStatusCash = null;
	
	public CommonFundStatusCashAdmin() {
		fundStatusCash = new CommonFundStatusCash();
    }

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
            sqlStr = "insert into TMP_fundStatusCash (FDATE,FFUNDCODE,FCashCode,FSUBTSFTYPECODE,FMarketValue,FASSETTYPE) values(" +
                dbl.sqlDate(fundStatusCash.getDate()) + "," +
                dbl.sqlString(fundStatusCash.getFundCode()) + "," +
                dbl.sqlString(fundStatusCash.getCashCode()) + "," +
                dbl.sqlString(fundStatusCash.getSubtsftypeCode()) + "," +
                fundStatusCash.getMarketValue() + "," +
                dbl.sqlString(fundStatusCash.getAssetType()) + ")";
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("新增资产分类表_现金表数据出错！", ex);
        }
        return "";
    }

    /**
     * 修改
     * @return String
     * @throws YssException
     * modify by fangjiang BUG 2632
     */
    public String editSetting() throws YssException {
        String sqlStr = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "update TMP_fundStatusCash set " +
            	"FDate=" + dbl.sqlDate(fundStatusCash.getDate()) + 
            	",FFUNDCODE=" + dbl.sqlString(fundStatusCash.getFundCode()) + 
            	",FCashCode=" + dbl.sqlString(fundStatusCash.getCashCode()) + 
            	",FSUBTSFTYPECODE=" + dbl.sqlString(fundStatusCash.getSubtsftypeCode()) +
            	",FASSETTYPE=" + dbl.sqlString(fundStatusCash.getAssetType()) +
            	",FMarketValue=" + fundStatusCash.getMarketValue() + 
                " where FDate=" + dbl.sqlDate(fundStatusCash.getOldDate()) +
                " and FFUNDCODE=" + dbl.sqlString(fundStatusCash.getOldFundCode()) +
                " and FCashCode=" + dbl.sqlString(fundStatusCash.getOldCashCode()) +
                " and FASSETTYPE=" + dbl.sqlString(fundStatusCash.getOldAssetType());
            if(fundStatusCash.getOldSubtsftypeCode().trim().length() > 0){
            	sqlStr = sqlStr + " and FSUBTSFTYPECODE= " + dbl.sqlString(fundStatusCash.getOldSubtsftypeCode());
            }
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("修改资产分类表_现金表数据出错！", ex);
        }
        return "";

    }

    /**
     * 删除数据
     * @param sMutilRowStr String
     * @return String
     * modify by fangjiang BUG 2632
     */
    public String delSetting() throws YssException {
        String sqlStr = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "delete TMP_fundStatusCash " +
	            " where FDate=" + dbl.sqlDate(fundStatusCash.getOldDate()) +
	            " and FFUNDCODE=" + dbl.sqlString(fundStatusCash.getOldFundCode()) +
	            " and FCashCode=" + dbl.sqlString(fundStatusCash.getOldCashCode()) +
	            " and FASSETTYPE=" + dbl.sqlString(fundStatusCash.getOldAssetType());
            if(fundStatusCash.getOldSubtsftypeCode().trim().length() > 0){
            	sqlStr = sqlStr + " and FSUBTSFTYPECODE= " + dbl.sqlString(fundStatusCash.getOldSubtsftypeCode());
            }
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("删除资产分类表_现金表数据出错！", ex);
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
            htTabMes = getTableFieldDesc("TMP_FundStatusCash"); //取出表的字段信息
            sqlStr = "select * from TMP_fundStatusCash " + fillterSql();
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
                setfundStatusCashAttr(rs);
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
        fundStatusCash.parseRowStr(sRowStr);
    }

    /**
     * 连接
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(YssFun.formatDate(fundStatusCash.getDate(), "yyyy-MM-dd")).append("\t");
        buf.append(fundStatusCash.getFundCode()).append("\t");
        buf.append(fundStatusCash.getCashCode()).append("\t");
        buf.append(fundStatusCash.getSubtsftypeCode()).append("\t");
        buf.append(fundStatusCash.getMarketValue()).append("\t");
        buf.append(fundStatusCash.getAssetType());
        return buf.toString();
    }

    public String buildRowStr(String sFields) {
        String[] arrField = sFields.split(",");
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arrField.length; i++) {
            if (arrField[i].equalsIgnoreCase("FDate")) {
            	buf.append(YssFun.formatDate(fundStatusCash.getDate(), "yyyy-MM-dd")).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FFUNDCODE")) {
                buf.append(fundStatusCash.getFundCode()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FCashCode")) {
            	buf.append(fundStatusCash.getCashCode()).append("\t");
            }  else if (arrField[i].equalsIgnoreCase("FSUBTSFTYPECODE")) {
                buf.append(fundStatusCash.getSubtsftypeCode()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FMarketValue")) {
                buf.append(fundStatusCash.getMarketValue()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FASSETTYPE")) {
                buf.append(fundStatusCash.getAssetType());
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
            reStr = "CtlFundStatusCash";
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
        if (fundStatusCash.getFilterType() != null) {
            sFilter = " where 1=1 ";
            if (fundStatusCash.getFilterType().getFundCode() != null && fundStatusCash.getFilterType().getFundCode().length() > 0) {
                sFilter += " and FFundCode like '" + fundStatusCash.getFilterType().getFundCode().replaceAll("'", "''") + "%'";
            }
            if (fundStatusCash.getFilterType().getDate() != null && !YssFun.formatDate(fundStatusCash.getFilterType().getDate(), "yyyy-MM-dd").equals("9998-12-31")) {
                sFilter += " and FDate =" + dbl.sqlDate(fundStatusCash.getFilterType().getDate());
            }
        }
        return sFilter;
    }

    private void setfundStatusCashAttr(ResultSet rs) throws SQLException {
        fundStatusCash.setFundCode(rs.getString("FFundCode"));
        fundStatusCash.setDate(rs.getDate("FDate"));
        fundStatusCash.setCashCode(rs.getString("FCashCode"));
        fundStatusCash.setSubtsftypeCode(rs.getString("FSUBTSFTYPECODE") == null ? "" : rs.getString("FSUBTSFTYPECODE")); //modify by fangjiang BUG 2632
        fundStatusCash.setMarketValue(rs.getDouble("FMarketValue"));
        fundStatusCash.setAssetType(rs.getString("FASSETTYPE"));
    }

    public void checkInput(byte btOper) throws YssException {
    	try {
    		checkData(btOper, "TMP_FundStatusCash", "FFundCode,FDate,FCashCode,FSUBTSFTYPECODE,FASSETTYPE",
                  dbl.sqlString(fundStatusCash.getFundCode()) + ";" + dbl.sqlDate(fundStatusCash.getDate()) + ";" +
                  dbl.sqlString(fundStatusCash.getCashCode()) + ";" + dbl.sqlString(fundStatusCash.getSubtsftypeCode()) + ";" + dbl.sqlString(fundStatusCash.getAssetType())+";",
                  dbl.sqlString(fundStatusCash.getOldFundCode()) + ";" + dbl.sqlDate(fundStatusCash.getOldDate()) + ";" +
                  dbl.sqlString(fundStatusCash.getOldCashCode()) + ";" + dbl.sqlString(fundStatusCash.getOldSubtsftypeCode()) + ";" + dbl.sqlString(fundStatusCash.getOldAssetType())+";");
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage());
    	}
    }
}
