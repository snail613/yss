package com.yss.main.platform.pfoper.commondata;

import java.sql.*;
import java.util.*;

import com.yss.main.platform.pfoper.commondata.pojo.*;
import com.yss.util.*;

/**
 * <p>Title: 预测中登TA表</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CommonPreTAAdmin
    extends BaseCommonData {
    public CommonPreTAAdmin() {
        preTA = new CommonPreTABean();
    }

    private CommonPreTABean preTA = null;
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
            sqlStr = "insert into preTA (FManagerCode,FFundCode,FApplyDate,FOperType,FApplyStock,FApplyAmount,FSendDate,FStandByFlag) values(" +
                dbl.sqlString(preTA.getManagerCode()) + "," +
                dbl.sqlString(preTA.getFundCode()) + "," +
                dbl.sqlDate(preTA.getApplyDate()) + "," +
                dbl.sqlString(preTA.getOperType()) + "," +
                preTA.getApplyStock() + "," +
                preTA.getApplyAmount() + "," +
                dbl.sqlDate(preTA.getSendDate()) + "," +
                preTA.getStandByFlag() + ")";
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
            sqlStr = "update preTA set FManagerCode=" +
                dbl.sqlString(preTA.getManagerCode()) + ",FFundCode=" +
                dbl.sqlString(preTA.getFundCode()) + ",FApplyDate=" +
                dbl.sqlDate(preTA.getApplyDate()) + ",FOperType=" +
                dbl.sqlString(preTA.getOperType()) + ",FApplyStock=" +
                preTA.getApplyStock() + ",FApplyAmount=" +
                preTA.getApplyAmount() + ",FSendDate=" +
                dbl.sqlDate(preTA.getSendDate()) + ",FStandByFlag=" +
                preTA.getStandByFlag() + " where FFundCode=" +
                dbl.sqlString(preTA.getOldFundCode()) +
                " and FApplyDate=" + dbl.sqlDate(preTA.getOldApplyDate()) +
                " and FOperType=" + dbl.sqlString(preTA.getOldOperType())+
                //modify by zhangfa 20101012 MS01829    特定数据处理界面有问题    QDV4赢时胜(33上线测试)2010年10月9日01_B 
                " and FAPPLYAMOUNT=" + preTA.getOldApplyAmount();
                //--------------------------------------------------------------------------------------------------
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
            sqlStr = "delete preTA where FFundCode=" +
                dbl.sqlString(preTA.getOldFundCode()) +
                " and FApplyDate=" + dbl.sqlDate(preTA.getOldApplyDate()) +
                " and FOperType=" + dbl.sqlString(preTA.getOldOperType());
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
            htTabMes = getTableFieldDesc("preTA"); //取出表的字段信息
            sqlStr = "select * from preTA " + fillterSql();
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
                setPreTAAttr(rs);
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
        preTA.parseRowStr(sRowStr);
    }

    /**
     * 连接
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(preTA.getManagerCode()).append("\t");
        buf.append(preTA.getFundCode()).append("\t");
        buf.append(YssFun.formatDate(preTA.getApplyDate(), "yyyy-MM-dd")).append("\t");
        buf.append(preTA.getOperType()).append("\t");
        buf.append(preTA.getApplyStock()).append("\t");
        buf.append(preTA.getApplyAmount()).append("\t");
        buf.append(YssFun.formatDate(preTA.getSendDate(), "yyyy-MM-dd")).append("\t");
        buf.append(preTA.getStandByFlag());
        return buf.toString();
    }

    public String buildRowStr(String sFields) {
        String[] arrField = sFields.split(",");
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arrField.length; i++) {
            if (arrField[i].equalsIgnoreCase("FManagerCode")) {
                buf.append(preTA.getManagerCode()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FFundCode")) {
                buf.append(preTA.getFundCode()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FApplyDate")) {
                buf.append(YssFun.formatDate(preTA.getApplyDate(), "yyyy-MM-dd")).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FOperType")) {
                buf.append(preTA.getOperType()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FApplyStock")) {
                buf.append(preTA.getApplyStock()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FApplyAmount")) {
                buf.append(preTA.getApplyAmount()).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FSendDate")) {
                buf.append(YssFun.formatDate(preTA.getSendDate(), "yyyy-MM-dd")).append("\t");
            } else if (arrField[i].equalsIgnoreCase("FStandByFlag")) {
                buf.append(preTA.getStandByFlag());
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
            reStr = "CtlCommonPreTA";
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
        if (preTA.getFilterType() != null) {
            sFilter = " where 1=1 ";
            if (preTA.getFilterType().getFundCode() != null && preTA.getFilterType().getFundCode().length() > 0) {
                sFilter += " and FFundCode like '" + preTA.getFilterType().getFundCode().replaceAll("'", "''") + "%'";
            }
            if (preTA.getFilterType().getApplyDate() != null && !YssFun.formatDate(preTA.getFilterType().getApplyDate(), "yyyy-MM-dd").equals("9998-12-31")) {
                sFilter += " and FApplyDate =" + dbl.sqlDate(preTA.getFilterType().getApplyDate());
            }
        }
        return sFilter;
    }

    private void setPreTAAttr(ResultSet rs) throws SQLException {
        preTA.setManagerCode(rs.getString("FManagerCode"));
        preTA.setFundCode(rs.getString("FFundCode"));
        preTA.setApplyDate(rs.getDate("FApplyDate"));
        preTA.setOperType(rs.getString("FOperType"));
        preTA.setApplyStock(rs.getDouble("FApplyStock"));
        preTA.setApplyAmount(rs.getDouble("FApplyAmount"));
        preTA.setSendDate(rs.getDate("FSendDate"));
        preTA.setStandByFlag(rs.getInt("FStandByFlag"));
    }

    public void checkInput(byte btOper) throws YssException {
    	//modify by zhangfa 20101012 MS01829    特定数据处理界面有问题    QDV4赢时胜(33上线测试)2010年10月9日01_B    
    	try {
        checkData(btOper, "preTA", "FFundCode,FApplyDate,FOperType,FAPPLYAMOUNT",
                  dbl.sqlString(preTA.getFundCode()) + ";" + dbl.sqlDate(preTA.getApplyDate()) + ";" +
                  dbl.sqlString(preTA.getOperType()) + ";"+preTA.getApplyAmount()+";",
                  dbl.sqlString(preTA.getOldFundCode()) + ";" + dbl.sqlDate(preTA.getOldApplyDate()) + ";" +
                  dbl.sqlString(preTA.getOldOperType()) + ";"+preTA.getOldApplyAmount()+";");
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage());
    	}
        //------------------------------------------------------------------------------------------------
    }

}
