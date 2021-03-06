package com.yss.main.platform.pfoper.commondata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.main.platform.pfoper.commondata.pojo.CommonFundStatusSec;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CommonFundStatusSecAdmin extends BaseCommonData {
	
	private CommonFundStatusSec fundStatusSec = null;
	
	public CommonFundStatusSecAdmin() {
		fundStatusSec = new CommonFundStatusSec();
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
            sqlStr = "insert into TMP_FundStatusSec (FDATE,FFUNDCODE,FSECURITYCODE,FSUBTSFTYPECODE,FAPARAMT,FASSETTYPE) values(" +
                dbl.sqlDate(fundStatusSec.getDate()) + "," +
                dbl.sqlString(fundStatusSec.getFundCode()) + "," +
                dbl.sqlString(fundStatusSec.getSecurityCode()) + "," +
                dbl.sqlString(fundStatusSec.getSubtsftypeCode()) + "," +
                fundStatusSec.getAparamt() + "," +
                dbl.sqlString(fundStatusSec.getAssetType()) + ")";
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("新增资产分类表_证券表数据出错！", ex);
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
            sqlStr = "update TMP_FundStatusSec set " +
            	"FDate=" + dbl.sqlDate(fundStatusSec.getDate()) + 
            	",FFUNDCODE=" + dbl.sqlString(fundStatusSec.getFundCode()) + 
            	",FSECURITYCODE=" + dbl.sqlString(fundStatusSec.getSecurityCode()) + 
            	",FSUBTSFTYPECODE=" + dbl.sqlString(fundStatusSec.getSubtsftypeCode()) +
            	",FASSETTYPE=" + dbl.sqlString(fundStatusSec.getAssetType()) +
            	",FAPARAMT=" + fundStatusSec.getAparamt() + 
                " where FDate=" + dbl.sqlDate(fundStatusSec.getOldDate()) +
                " and FFUNDCODE=" + dbl.sqlString(fundStatusSec.getOldFundCode()) +
                " and FSECURITYCODE=" + dbl.sqlString(fundStatusSec.getOldSecurityCode()) +
                " and FASSETTYPE=" + dbl.sqlString(fundStatusSec.getOldAssetType());
            if(fundStatusSec.getOldSubtsftypeCode().trim().length() > 0){
            	sqlStr = sqlStr + " and FSUBTSFTYPECODE= " + dbl.sqlString(fundStatusSec.getOldSubtsftypeCode());
            }
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("修改资产分类表_证券表数据出错！", ex);
        }
        return "";

    }

    /**
     * 删除数据
     * @param sMutilRowStr String
     * @return String
     * modify by fangjiang BUG 2632c
     */
    public String delSetting() throws YssException {
        String sqlStr = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "delete TMP_FundStatusSec " +
	            " where FDate=" + dbl.sqlDate(fundStatusSec.getOldDate()) +
	            " and FFUNDCODE=" + dbl.sqlString(fundStatusSec.getOldFundCode()) +
	            " and FSECURITYCODE=" + dbl.sqlString(fundStatusSec.getOldSecurityCode()) +
	            " and FASSETTYPE=" + dbl.sqlString(fundStatusSec.getOldAssetType());
            if(fundStatusSec.getOldSubtsftypeCode().trim().length() > 0){
            	sqlStr = sqlStr + " and FSUBTSFTYPECODE= " + dbl.sqlString(fundStatusSec.getOldSubtsftypeCode());
            }
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("删除资产分类表_证券表数据出错！", ex);
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
            htTabMes = getTableFieldDesc("TMP_FundStatusSec"); //取出表的字段信息
            sqlStr = "select * from TMP_FundStatusSec " + fillterSql();
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
            } 
            while (rs.next()) {
                setfundStatusSecAttr(rs);
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
        fundStatusSec.parseRowStr(sRowStr);
    }

    /**
     * 连接
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(YssFun.formatDate(fundStatusSec.getDate(), "yyyy-MM-dd")).append("\t");
        buf.append(fundStatusSec.getFundCode()).append("\t");
        buf.append(fundStatusSec.getSecurityCode()).append("\t");
        buf.append(fundStatusSec.getSubtsftypeCode()).append("\t");
        buf.append(fundStatusSec.getAparamt()).append("\t");
        buf.append(fundStatusSec.getAssetType());
        return buf.toString();
    }

    public String buildRowStr(String sFields) {
        String[] arrField = sFields.split(",");
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arrField.length; i++) {
            if (arrField[i].equalsIgnoreCase("FDate")) {
            	buf.append(YssFun.formatDate(fundStatusSec.getDate(), "yyyy-MM-dd")).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FFUNDCODE")) {
                buf.append(fundStatusSec.getFundCode()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FSECURITYCODE")) {
            	buf.append(fundStatusSec.getSecurityCode()).append("\t");
            }  else if (arrField[i].equalsIgnoreCase("FSUBTSFTYPECODE")) {
                buf.append(fundStatusSec.getSubtsftypeCode()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FAPARAMT")) {
                buf.append(fundStatusSec.getAparamt()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FASSETTYPE")) {
                buf.append(fundStatusSec.getAssetType());
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
            reStr = "CtlFundStatusSec";
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
        if (fundStatusSec.getFilterType() != null) {
            sFilter = " where 1=1 ";
            if (fundStatusSec.getFilterType().getFundCode() != null && fundStatusSec.getFilterType().getFundCode().length() > 0) {
                sFilter += " and FFundCode like '" + fundStatusSec.getFilterType().getFundCode().replaceAll("'", "''") + "%'";
            }
            if (fundStatusSec.getFilterType().getDate() != null && !YssFun.formatDate(fundStatusSec.getFilterType().getDate(), "yyyy-MM-dd").equals("9998-12-31")) {
                sFilter += " and FDate =" + dbl.sqlDate(fundStatusSec.getFilterType().getDate());
            }
        }
        return sFilter;
    }

    private void setfundStatusSecAttr(ResultSet rs) throws SQLException {
        fundStatusSec.setFundCode(rs.getString("FFundCode"));
        fundStatusSec.setDate(rs.getDate("FDate"));
        fundStatusSec.setSecurityCode(rs.getString("FSECURITYCODE"));
        fundStatusSec.setSubtsftypeCode(rs.getString("FSUBTSFTYPECODE") == null ? "" : rs.getString("FSUBTSFTYPECODE")); //modify by fangjiang BUG 2632
        fundStatusSec.setAparamt(rs.getDouble("FAPARAMT"));
        fundStatusSec.setAssetType(rs.getString("FASSETTYPE"));
    }

    public void checkInput(byte btOper) throws YssException {
    	try {
    		checkData(btOper, "TMP_FundStatusSec", "FFundCode,FDate,FSECURITYCODE,FSUBTSFTYPECODE,FASSETTYPE",
                  dbl.sqlString(fundStatusSec.getFundCode()) + ";" + dbl.sqlDate(fundStatusSec.getDate()) + ";" +
                  dbl.sqlString(fundStatusSec.getSecurityCode()) + ";" + dbl.sqlString(fundStatusSec.getSubtsftypeCode()) + ";" + dbl.sqlString(fundStatusSec.getAssetType())+";",
                  dbl.sqlString(fundStatusSec.getOldFundCode()) + ";" + dbl.sqlDate(fundStatusSec.getOldDate()) + ";" +
                  dbl.sqlString(fundStatusSec.getOldSecurityCode()) + ";" + dbl.sqlString(fundStatusSec.getOldSubtsftypeCode()) + ";" + dbl.sqlString(fundStatusSec.getOldAssetType())+";");
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage());
    	}
    }
    
}
