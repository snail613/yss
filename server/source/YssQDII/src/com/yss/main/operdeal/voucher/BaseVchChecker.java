package com.yss.main.operdeal.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

public class BaseVchChecker
    extends BaseBean {
    private String vchTplCodes = "";
    public void init(String vchTplCodes) {
        this.vchTplCodes = vchTplCodes;
    }

    public BaseVchChecker() {
    }

    public String doVchCheck() throws YssException {
        String[] arrNums = null;
        String nums = "";
        ResultSet rs = null;
        ResultSet rsVchData = null;
        String strSql = "";
        double jBal = 0.0;
        double dBal = 0.0;
        YssVoucher voucher = null;
        StringBuffer buf = null;
        StringBuffer bufOne = new StringBuffer();

        boolean flag = false;
        StringBuffer bufAll = new StringBuffer();
        StringBuffer bufTmp = null;
        String sHeader = "";
        String sShowDataStr = "";
        String[] sin = null;
        String subFlag = "是";
        String bookSetFlag = "是";
        String flagBalance = "是";
        sHeader = "凭证编号\t借贷平衡\t科目\t套帐";
        try {
            flag = dbl.yssTableExist("LSETLIST");

            
            if (flag) {
                strSql = "select * from " + pub.yssGetTableName("Tb_Vch_Data") +
                    " where FVchTplCode in (" + operSql.sqlCodes(vchTplCodes) +
                    ")";
                rsVchData = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rsVchData.next()) {
                    bufOne.append(rsVchData.getString("FVchNum")).append(",");
                }
                if (bufOne.toString().length() > 1) {
                    nums = bufOne.toString().substring(0,
                        bufOne.toString().length() -
                        1);
                }
                arrNums = nums.split(",");
                for (int i = 0; i < arrNums.length; i++) {

                    bufTmp = new StringBuffer();
                    buf = new StringBuffer();
                    strSql = " select * from " +
                        pub.yssGetTableName("Tb_Vch_DataEntity") +
                        " where FVchNum =" + dbl.sqlString(arrNums[i]);

                    rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                    while (rs.next()) {
                        if (rs.getString("FDCWay").equalsIgnoreCase("J")) {
                            jBal = jBal + rs.getDouble("FSetBal");
                        } else {
                            dBal = dBal + rs.getDouble("FSetBal");
                        }
                        voucher = new YssVoucher();
                        voucher.setSubject(checkSubject(rs.getString("FSubjectCode")));
                        voucher.setBookSet(checkBookSet(rs.getString("FBookSetCode")));
                        buf.append(voucher.buildRowStr()).append("\r\f");
                    }
//                    if (jBal != dBal) 
                    if (YssD.sub(jBal, dBal) != 0) 
                    {
                        flagBalance = "否";
                    }
                    String[] tmp = buf.toString().split("\r\f");
                    for (int m = 0; m < tmp.length; m++) {
                        sin = tmp[m].split("\t");
                        if (sin[0].equalsIgnoreCase("false")) {
                            subFlag = "否";
                        }
                        if (sin[1].equalsIgnoreCase("false")) {
                            bookSetFlag = "否";
                        }
                    }
                    bufTmp.append(arrNums[i]).append("\t");
                    bufTmp.append(String.valueOf(flagBalance)).append("\t");
                    bufTmp.append(subFlag).append("\t");
                    bufTmp.append(bookSetFlag).append("\t");

                    bufAll.append(bufTmp.toString()).append(YssCons.YSS_LINESPLITMARK);
                    dbl.closeResultSetFinal(rs);
                }
                if (bufAll.length() > 2) {
                    sShowDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sShowDataStr;
            } else {
                return "";
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
        	//edit by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs,rsVchData);
        }

    }

    public boolean checkSubject(String subjectCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        boolean flag = false;
        try {
            strSql = " select * from A2007001LACCOUNT" +
                " where FAcctCode=" + dbl.sqlString(subjectCode);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getByte("FAcctDetail") == 1) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return flag;
    }

    public boolean checkBookSet(String bookSetCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        boolean flag = false;
        try {
            strSql = " select * from lsetlist" +
                " where FSetCode=" + dbl.sqlString(bookSetCode);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                flag = true;
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return flag;

    }

}
