package com.yss.main.operdeal.report.netvalueviewpl;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.sql.ResultSet;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import java.util.HashMap;
import com.yss.main.cusreport.RepTabCellBean;

public class FixPub
    extends BaseBuildCommonRep {
    public FixPub() {
    }

    /**
     * 获取基金成立日那天的金额
     * @param portCode String
     * @throws YssException
     * @return double
     */
    public double calInceptionMoney(java.util.Date inceptionDate, String setCode) throws YssException {
        double inception = 0.0;
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select * from " + pub.yssGetTableName("tb_rep_guessvalue") +
                " where FDATE=" + dbl.sqlDate(inceptionDate) +
                " and FPortCode=" + dbl.sqlString(setCode) +
                " and FAcctCode=" + dbl.sqlString("9600");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                inception = rs.getDouble("FStandardMoneyMarketValue");
            }
            return inception;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 生成报表的格式
     * @param str String
     * @param dsCode String
     * @throws YssException
     * @return String
     */
    public String buildRowCompResult(String str, String dsCode) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        String[] sArry = null;
        try {
            sArry = str.split(",");
            hmCellStyle = getCellStyles(dsCode);
            for (int i = 0; i < sArry.length; i++) {
                sKey = dsCode + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append(
                    "\t");
            }
            if (buf.toString().trim().length() > 1) {
                strReturn = buf.toString().substring(0,
                    buf.toString().length() - 1);
            }

            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    /**
     * 获取套帐号
     * @param portCode String
     * @throws YssException
     * @return String
     */
    public String getSetCode(String portCode) throws YssException {
        String sResult = "";
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = " select FSETCODE from lsetlist " +
                " where fsetid=(" +
                "select y.fassetcode from " +
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               /* "(select FPortCode,FCheckState,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                "and FCheckState <> 2 and FASSETGROUPCODE = " +
                  dbl.sqlString(pub.getAssetGroupCode()) +
                " and FPORTCODE=" + dbl.sqlString(portCode) +
                " group by FPortCode,FCheckState) x join" +
                "( select fassetcode,FStartDate,FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " )y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate)";
                *
                */
                
                "(select FPortCode,FCheckState,fassetcode from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
                " where  FCheckState <> 2 and FASSETGROUPCODE = " +              
                dbl.sqlString(pub.getAssetGroupCode()) +
                " and FPORTCODE=" + dbl.sqlString(portCode) +
                " ) y ";
            
            //end by lidaolong 
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getString("FSETCode");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取套帐号报错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 计算费用
     * @param startDate Date
     * @param endDate Date
     * @throws YssException
     * @return double
     */
    public double calFee(java.util.Date startDate, java.util.Date endDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double sResult = 0.0;
        try {
            strSql = " select FKMH,sum(FBBAL)as FBAL from a2007001fcwvch a join (" +
                " select * from a2007001laccount where FAcctDetail=1" +
                " )b on b.FACCTCODE=a.FKMH WHERE substr(FKMH,0,4)='2206'" +
                " and 	Fdate between " + dbl.sqlDate(startDate) + " and " +
                dbl.sqlDate(endDate) +
                " and FJD='D' group by FKMH";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getDouble("FBAL");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("计算费用报错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取基金成立日期
     * @param portCode String
     * @throws YssException
     * @return Date
     */
    public java.util.Date getInceptionDate(String portCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        java.util.Date sResult = null;
        try {
            strSql = "select y.FInceptionDate from " +
         // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               /* "(select FPortCode,FCheckState,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                "and FCheckState <> 2 and FASSETGROUPCODE = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                " and FPORTCODE=" + dbl.sqlString(portCode) +
                " group by FPortCode,FCheckState) x join" +
                "( select FInceptionDate,FStartDate,FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " )y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate";
          
                *
                */
                "(select FPortCode,FCheckState, FInceptionDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
                " where FCheckState <> 2 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " and FPORTCODE=" + dbl.sqlString(portCode) +   
                " ) y ";
          
            //end by lidaolong
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getDate("FInceptionDate");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取基金成立日报错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取净值
     * @param date Date
     * @param setCode String
     * @throws YssException
     * @return double
     */
    public double getNetValue(java.util.Date date, String setCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double sResult = 0.0;
        try {
            strSql = " select FStandardMoneyMarketValue from " + pub.yssGetTableName("tb_rep_guessvalue") +
                " where FDATE=" + dbl.sqlDate(date) +
                " and FPortCode=" + dbl.sqlString(setCode) +
                " and FAcctCode=" + dbl.sqlString("9600");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getDouble("FStandardMoneyMarketValue");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取净值报错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取指数行情数据
     * @param date Date
     * @param indexCode String
     * @throws YssException
     * @return double
     */
    public double getIndexDate(java.util.Date date, String indexCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double sResult = 0.0;
        try {
            strSql = " select FTopValue from " +
                pub.yssGetTableName("Tb_Data_Index") +
                " where FDate=" + dbl.sqlDate(date) +
                " and FIndexCode=" + dbl.sqlString(indexCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getDouble("FTopValue");
            }
            return sResult;

        } catch (Exception e) {
            throw new YssException("从指数表获取数据报错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 获取组合名称
     * @param portCode String
     * @throws YssException
     * @return String
     */
    public String getPortName(String portCode) throws YssException {
        String strSql = "";
        String shortName = "";
        ResultSet rs = null;
        try {
            strSql = "select y.FPortName from " +
         // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            
         /*       "(select FPortCode,FCheckState,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                "and FCheckState <> 2 and FASSETGROUPCODE = " +*/
                
            "(select FPortCode,FCheckState ,FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") + " " +
            " where  FCheckState <> 2 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " and FPORTCODE=" + dbl.sqlString(portCode) +
                " ) y ";
            
            //end by lidaolong
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                shortName = rs.getString("FPortName");
                if (shortName == null) {
                    shortName = "";
                }
            }
            return shortName;
        } catch (Exception e) {
            throw new YssException("获取组合名称报错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
