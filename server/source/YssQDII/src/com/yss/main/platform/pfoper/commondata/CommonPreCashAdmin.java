package com.yss.main.platform.pfoper.commondata;

import java.sql.*;
import java.util.*;

import com.yss.main.platform.pfoper.commondata.pojo.*;
import com.yss.util.*;

/**
 * <p>Title:预估现金表 </p>
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
public class CommonPreCashAdmin
    extends BaseCommonData {
    public CommonPreCashAdmin() {
        preCash = new CommonPreCashBean();
    }

    private CommonPreCashBean preCash = null;
    /**
     * 新增
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String sqlStr = "";
        Connection conn = null;
        ResultSet rs = null;
        boolean bTrans = false;
        try {
            /*sqlStr = "select '1' from from preCash where FPayDate=" +
                  dbl.sqlDate(preCash.getPayDate()) + " and FCashAccount =" +
                  dbl.sqlString(preCash.getCashAccount());
                      rs =dbl.openResultSet(sqlStr);
                      if(rs.next()){
               throw new YssException("预处理现金表中已经存在数据，新增失败");
                      }*/
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "insert into preCash (FBeginDate,FEndDate,FPayDate,FResume,FCashAccount,FCuryCode,FMoney,FInOut) values(" +
                dbl.sqlDate(preCash.getBeginDate()) + "," +
                dbl.sqlDate(preCash.getEndDate()) + "," +
                dbl.sqlDate(preCash.getPayDate()) + "," +
                dbl.sqlString(preCash.getResume()) + "," +
                dbl.sqlString(preCash.getCashAccount()) + "," +
                dbl.sqlString(preCash.getCuryCode()) + "," +
                preCash.getMoney() + "," +
                preCash.getInOut() + ")";
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("新增预估现金表数据出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
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
            //----edit by songjie 2011.03.28 BUG:1558 QDV4赢时胜(测试)2011年03月23日3_B----//
            if("".equals(preCash.getCashAccount())){
            	throw new YssException("请设置现金账户！");
            }
            if("".equals(preCash.getCuryCode())){
            	throw new YssException("请设置货币代码！");
            }
            if("".equals(preCash.getResume())){
            	throw new YssException("请设置摘要！");
            }   
            //----edit by songjie 2011.03.28 BUG:1558 QDV4赢时胜(测试)2011年03月23日3_B----//
            sqlStr = "update preCash set FBeginDate=" +
                dbl.sqlDate(preCash.getBeginDate()) + ",FEndDate=" +
                dbl.sqlDate(preCash.getEndDate()) + ",FPayDate=" +
                dbl.sqlDate(preCash.getPayDate()) + ",FResume=" +
                dbl.sqlString(preCash.getResume()) + ",FCashAccount=" +
                dbl.sqlString(preCash.getCashAccount()) + ",FCuryCode=" +
                dbl.sqlString(preCash.getCuryCode()) + ",FMoney=" +
                preCash.getMoney() + ",FInOut=" +
                preCash.getInOut() + " where FPayDate=" +
                dbl.sqlDate(preCash.getOldPayDate()) +
                " and FCashAccount=" + dbl.sqlString(preCash.getOldCashAccount()) +
                " and FMoney=" + preCash.getDOldMoney();//by xuxuming,20090909.MS00679,当业务人员修改其中一条记录的金额时，会报主键重复错误,QDV4海富通2009年9月05日01_B
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
        	//----edit by songjie 2011.03.28 BUG:1558 QDV4赢时胜(测试)2011年03月23日3_B----//
            throw new YssException("修改预估现金表数据出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
            sqlStr = "delete preCash  where FPayDate=" +
                dbl.sqlDate(preCash.getOldPayDate()) +
                " and FCashAccount=" + dbl.sqlString(preCash.getOldCashAccount());
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("删除预估现金表数据出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
            htTabMes = getTableFieldDesc("preCash"); //取出表的字段信息
            //edit by yanghaiming 2011-05-09 BUG1852
            sqlStr = "select FBEGINDATE,FENDDATE,FPAYDATE,FRESUME,FCASHACCOUNT,FCURYCODE,FMONEY,FINOUT from preCash " + fillterSql();
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
                setPreCashAttr(rs);
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
        preCash.parseRowStr(sRowStr);
    }

    /**
     * 连接
     * @return String
     */
    protected String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(YssFun.formatDate(preCash.getBeginDate(), "yyyy-MM-dd")).append("\t");
        buf.append(YssFun.formatDate(preCash.getEndDate(), "yyyy-MM-dd")).append("\t");
        buf.append(YssFun.formatDate(preCash.getPayDate(), "yyyy-MM-dd")).append("\t");
        buf.append(preCash.getResume()).append("\t");
        buf.append(preCash.getCashAccount()).append("\t");
        buf.append(preCash.getCuryCode()).append("\t");
        buf.append(preCash.getMoney()).append("\t");
        buf.append(preCash.getInOut());
        return buf.toString();
    }

    //,,,,,,,
    private String buildRowStr(String sFields) {
        String[] arrField = sFields.split(",");
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arrField.length; i++) {
            if (arrField[i].equalsIgnoreCase("FBeginDate")) {
                buf.append(YssFun.formatDate(preCash.getBeginDate(), "yyyy-MM-dd")).append("\t");
            }
            if (arrField[i].equalsIgnoreCase("FEndDate")) {
                buf.append(YssFun.formatDate(preCash.getEndDate(), "yyyy-MM-dd")).append("\t");
            }
            if (arrField[i].equalsIgnoreCase("FPayDate")) {
                buf.append(YssFun.formatDate(preCash.getPayDate(), "yyyy-MM-dd")).append("\t");
            }
            if (arrField[i].equalsIgnoreCase("FResume")) {
                buf.append(preCash.getResume()).append("\t");
            }
            if (arrField[i].equalsIgnoreCase("FCashAccount")) {
                buf.append(preCash.getCashAccount()).append("\t");
            }
            if (arrField[i].equalsIgnoreCase("FCuryCode")) {
                buf.append(preCash.getCuryCode()).append("\t");
            }
            if (arrField[i].equalsIgnoreCase("FMoney")) {
                buf.append(preCash.getMoney()).append("\t");
            }
            if (arrField[i].equalsIgnoreCase("FInOut")) {
                buf.append(preCash.getInOut()).append("\t");//edit by yanghaiming 2011-05-09 BUG1852
            }
        }
        return buf.toString().substring(0,buf.toString().length()-1);//edit by yanghaiming 2011-05-09 BUG1852
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
            reStr = "CtlCommonPreCash";
            /* FaceCfgParamBean faceCfg = null;
             faceCfg =new FaceCfgParamBean();
             faceCfg.setICtlType(2); //combox
             faceCfg.setSDataSource(YssCons.YSS_CRY_RATEWAY);
             faceCfg.setITabIndex(3);
             faceCfg.setIParamIndex(3);
             faceCfg.setILocalX(190+190);
             faceCfg.setILocalY(10);
             faceCfg.setIHeight(190);
             faceCfg.setIWidth(21);
             faceCfg.setIIntLen(50);
             faceCfg.setSShowText("下拉框");
             faceCfg.setSClassName("Combox");
             reStr=faceCfg.buildRowStr()+"\r\f";
             faceCfg =new FaceCfgParamBean();
             faceCfg.setICtlType(3);//timepicker
             faceCfg.setSCtlName("DateTimePicker1");
             faceCfg.setITabIndex(2);
             faceCfg.setIParamIndex(2);
             faceCfg.setILocalX(800);
             faceCfg.setILocalY(10);
             faceCfg.setIHeight(160);
             faceCfg.setIWidth(21);
             faceCfg.setIFormat(8);
             faceCfg.setSFormatStr("yyyy-MM-dd");
             faceCfg.setSShowText("日期:");
             faceCfg.setBChecked(true);
             reStr+=faceCfg.buildRowStr()+"\r\f";  */
            return reStr;
        }
        return "";
    }

    /**
     * 条件筛选
     * @return String
     */
    protected String fillterSql() {
        String sFilter = "";
        if (preCash.getFilterType() != null) {
            sFilter = " where 1=1 ";
            if (preCash.getFilterType().getCashAccount() != null && preCash.getFilterType().getCashAccount().length() > 0) {
                sFilter += " and FCashAccount like '" + preCash.getFilterType().getCashAccount().replaceAll("'", "''") + "%'";
            }
            if (preCash.getFilterType().getPayDate() != null && !YssFun.formatDate(preCash.getFilterType().getPayDate(), "yyyy-MM-dd").equals("9998-12-31")) {
                sFilter += " and FPayDate =" + dbl.sqlDate(preCash.getFilterType().getPayDate());
            }
        }
        return sFilter;
    }
  //modify by zhangfa 20100827 MS01656    新建两条相同的数据，点击确定保存时系统会报错    QDV4赢时胜(测试)2010年08月25日04_B 
    public void checkInput(byte btOper) throws  YssException{
    	try {
        checkData(btOper, "preCash", "FPayDate,FCashAccount,FMoney",
                  dbl.sqlDate(preCash.getPayDate()) + ";" +
                  dbl.sqlString(preCash.getCashAccount()) + ";"+
                  preCash.getMoney()+";",
                  dbl.sqlDate(preCash.getOldPayDate()) + ";" +
               //modify by zhangfa 201001009  MS01829    特定数据处理界面有问题    QDV4赢时胜(33上线测试)2010年10月9日01_B     
                  dbl.sqlString(preCash.getOldCashAccount())+ ";" + 
               //---------------------------------------------------   
                		  preCash.getDOldMoney()+	  ";");
    	}
    	catch (Exception ex) {
    		throw new YssException(ex.getMessage());
 		}
    }
//---------------------------------------------------------------------------------------------------------------------
    private void setPreCashAttr(ResultSet rs) throws SQLException {
        preCash.setBeginDate(rs.getDate("FBeginDate"));
        preCash.setEndDate(rs.getDate("FEndDate"));
        preCash.setPayDate(rs.getDate("FPayDate"));
        preCash.setResume(rs.getString("FResume"));
        preCash.setCashAccount(rs.getString("FCashAccount"));
        preCash.setCuryCode(rs.getString("FCuryCode"));
        preCash.setMoney(rs.getDouble("FMoney"));
        preCash.setInOut(rs.getInt("FInOut"));
    }

}
