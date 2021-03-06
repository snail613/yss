package com.yss.dsub;

import java.util.ArrayList;
import java.util.Date;
import com.yss.util.*;
import java.sql.ResultSet;
import com.yss.lic.LicInfo;

public final class YssLicence
    extends BaseBean {
    private boolean bAvailable = true; //是否有效
    private int iStru = 0; //结构层次
    private boolean bOverdue = false; //过期标志
    private String sEdition = ""; //版本 ,试用版/正试版
    private String sClientName = ""; //客户名称
    private String sProductName = ""; //产品名称
    private java.util.Date dStartDate = null; //起用日期
    private java.util.Date dEndDate = null; //结束日期
    //private ArrayList showFun =null; //显示的功能
    public YssLicence() {
    }

    /***
     * 返回 TRUE 正常读到了,FALSE 未读到或指定路径下没有该文件
     */
    public void loadLicence(String filePath) throws YssException {
        try {
            String sData = "";
            LicInfo info = new LicInfo();
            sData = info.yssGetLicAll(filePath);
            ParseRowStr(sData);
            bAvailable = true;
        } catch (Exception ex) {
            //bAvailable=false;
            //这里先将其赋初始值,将文件改为有效及试用过期的版本,目前大多版本没有许可证文件
            bAvailable = true;
            iStru = 0;
            sClientName = "";
            sEdition = "试用版";
            sProductName = "赢时胜QDII资产管理系统";
            bOverdue = true;
            dStartDate = YssFun.toDate("1900-01-01");
            dEndDate = new Date();
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(sClientName).append("\t");
        buf.append(sEdition).append("\t");
        buf.append(sProductName).append("\t");
        buf.append(dStartDate != null ? YssFun.formatDate(dStartDate) : "1900-01-01").append("\t");
        buf.append(dEndDate != null ? YssFun.formatDate(dEndDate) : "1900-01-01").append("\t");
        buf.append(iStru).append("\t");
        buf.append(bOverdue ? "true" : "false").append("\t");
        buf.append(bAvailable ? "true" : "false").append("\tnull");
        return buf.toString();
    }

    private void ParseRowStr(String sData) throws YssException {
        String[] arrData = null;
        try {
            String[] arrSimple = null;
            if (sData.split("\r\n").length > 2) {
                arrData = sData.split("\r\n");
            } else if (sData.split("\n").length > 2) {
                arrData = sData.split("\n");
            } /**shashijie 2012-7-2 STORY 2475 */
            else {
            	arrData = new String[0];
			}
			/**end*/
            sClientName = arrData[0];
            for (int i = 1; i < arrData.length; i++) {
                arrData[i] = arrData[i] + "\tnull"; //增加一个尾部标记，防止字符串解析长度不够　byleeyu 0924
                arrSimple = arrData[i].split("\t");
                iStru = Integer.parseInt(arrSimple[0]);
                sProductName = arrSimple[1];
                sEdition = (arrSimple[2].length() > 0 ? "试用版" : "正式版");
                if (arrSimple[3].length() > 0) {
                    dStartDate = YssFun.toDate(arrSimple[3]);
                }
                if (arrSimple[4].length() > 0) {
                    dEndDate = YssFun.toDate(arrSimple[4]);
                }
                if (sProductName.equalsIgnoreCase("赢时胜QDII资产管理系统")) {
                    break;
                }
            }
            bOverdue = isOverdue();
        } catch (Exception ex) {
            throw new YssException("取许可证文件数据出错");
        }
    }

    //过期判断 返回 true:过期 false:未过期
    private boolean isOverdue() throws YssException {
        //取数据库的日期
        ResultSet rs = null;
        String strSql = "";
        java.util.Date dDate = null;
        boolean bRes = false;
        try {
            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = " select sysDate from dual";
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    dDate = rs.getDate("sysDate");
                } else {
                    bRes = true;
                }
            } else if (dbl.dbType == YssCons.DB_DB2) {
                strSql = "SELECT distinct(CURRENT_DATE) as sysDate from TB_SYS_USERLIST";
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    dDate = rs.getDate("sysDate");
                } else {
                    bRes = true;
                }
            } else {
                bRes = true;
            }
            if (dDate != null) {
                if (dEndDate != null || dStartDate != null) {
                    if (dEndDate.before(dDate) || dStartDate.after(dDate)) {
                        //若当前日期大于结束日期,或若当前日期小于起始日期
                        bRes = true;
                    } else {
                        bRes = false;
                    }
                } else {
                    bRes = true;
                }
            } else {
                bRes = true;
            }
            return bRes;
        } catch (Exception ex) {
            throw new YssException(ex.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getSProductName() {
        return sProductName;
    }

    public String getSEdition() {
        return sEdition;
    }

    public String getSClientName() {
        return sClientName;
    }

    public int getIStru() {
        return iStru;
    }

    public Date getDStartDate() {
        return dStartDate;
    }

    public Date getDEndDate() {
        return dEndDate;
    }

    public boolean isBOverdue() {
        return bOverdue;
    }

    public boolean isBAvailable() {
        return bAvailable;
    }
}
