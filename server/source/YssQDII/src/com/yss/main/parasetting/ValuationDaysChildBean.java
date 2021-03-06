package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title:估值日设置 </p>
 *
 * <p>Description: 用于处理估值日的增删改查等</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: yss</p>
 * MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A
 * @author panjunfang 20090710
 * @version 1.0
 */
public class ValuationDaysChildBean
    extends BaseDataSettingBean implements
    IDataSetting {

    private String beginDate;               //开始日期（解析）
    private String endDate;                 //结束日期（解析）
    private String ValuationDayDate = "";   //估值日日期（返回）
    private String ValuationDayRule = "";   //估值日规则（解析）
    private String desc = "";               //描述（解析，返回）
    private String ValuationDaysCode = "";  //估值日群代码（解析）
    private String PortCode = "";           //组合代码（解析）
    private int isDel = 0;                  //判断是否为删除估值日，默认0为生成、增加估值日，1代表删除

    public ValuationDaysChildBean() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    /**
     * 删除估值日
     * 因为估值日是没有审核反审核的，因此删除是实际从数据库删除，而不是更新审核状态
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = true;
        try {
            conn.setAutoCommit(false);

            strSql = "delete from " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                " where FDate between " +
                dbl.sqlDate(YssFun.toDate(this.beginDate)) + " and " +
                dbl.sqlDate(YssFun.toDate(this.endDate)) +
                " and FValuationdaysCode = " + dbl.sqlString(this.ValuationDaysCode) +
                " and FPortCode = " + dbl.sqlString(this.PortCode);
            dbl.executeSql(strSql);

            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            dbl.endTransFinal(conn, bTrans);
            throw new YssException("删除估值日信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {
    }

    /**
     * 新增、修改估值日
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = true;
        java.util.Date addDate;
        java.util.Date endDate;
        String sWeek;
        try {
            this.parseRowStr(sMutilRowStr);
            conn.setAutoCommit(false);
            //先根据估值日群代码、组合代码、日期将估值日删除
            strSql = "delete from " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                " where FDate between " +
                dbl.sqlDate(YssFun.toDate(this.beginDate)) + " and " +
                dbl.sqlDate(YssFun.toDate(this.endDate)) +
                " and FValuationdaysCode = " + dbl.sqlString(this.ValuationDaysCode) +
                " and FPortCode = " + dbl.sqlString(this.PortCode);
            dbl.executeSql(strSql);

            addDate = YssFun.toDate(this.beginDate);
            endDate = YssFun.toDate(this.endDate);

            endDate = YssFun.addDay(endDate, 1);
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                "(FValuationdaysCode,FPortCode,FDate,FDesc,FCheckState,FCreator,FCreateTime) values (?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            //如果开始日期不等于结束日期，将数据插入到估值日表
            while (YssFun.dateDiff(addDate,endDate)!=0 && isDel != 1) {
                //获取addDate是星期几
                sWeek = String.valueOf(YssFun.getWeekDay(addDate));
                //按照估值日规则来处理估值日数据
                if (this.ValuationDayRule != null && this.ValuationDayRule.length() != 0) {
                    if (this.ValuationDayRule.indexOf(sWeek) >= 0) {
                        pstmt.setString(1, this.ValuationDaysCode);
                        pstmt.setString(2, this.PortCode);
                        pstmt.setDate(3, YssFun.toSqlDate(addDate));
                        pstmt.setString(4, this.desc);
                        pstmt.setInt(5, (pub.getSysCheckState() ? 0 : 1));
                        pstmt.setString(6, this.creatorCode);
                        pstmt.setString(7, this.creatorTime);
                        pstmt.addBatch();
                    }
                } else {
                    pstmt.setString(1, this.ValuationDaysCode);
                    pstmt.setString(2, this.PortCode);
                    pstmt.setDate(3, YssFun.toSqlDate(addDate));
                    pstmt.setString(4, this.desc);
                    pstmt.setInt(5, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(6, this.creatorCode);
                    pstmt.setString(7, this.creatorTime);
                    pstmt.addBatch();
                }
                //日期递增
                addDate = YssFun.addDay(addDate, 1);
            }
            //执行批处理插入
            pstmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            dbl.endTransFinal(conn, bTrans);
            throw new YssException("设置估值日出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        String sAllDataStr = "";
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        String sYearMonth = null;
        try {
            strSql = "select a.*," +
                "b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_ValuationDay") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FValuationdaysCode = " + dbl.sqlString(this.ValuationDaysCode) +
                " and a.FPortCode = " + dbl.sqlString(this.PortCode) +
                " order by a.FDate";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                setValuationdaysChildAttr(rs);
                //如果上条数据的年月和本次不同，使用\r\f作为分隔符，否则使用\f\f
                if (!YssFun.formatDate(rs.getDate("FDate"), "yyyy-MM").equalsIgnoreCase(sYearMonth)) {
                    bufAll.append(YssCons.YSS_PASSAGESPLITMARK).append(this.buildRowStr());
                } else {
                    bufAll.append(YssCons.YSS_LINESPLITMARK).append(this.buildRowStr());
                }
                sYearMonth = YssFun.formatDate(rs.getDate("FDate"), "yyyy-MM");
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(2, bufAll.toString().length());
            }
            return sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取估值日设置数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * setValuationdaysChildAttr
     *给变量赋值
     * @param rs ResultSet
     */
    private void setValuationdaysChildAttr(ResultSet rs) throws SQLException {
        this.ValuationDayDate = YssFun.formatDate(rs.getDate("FDate"), "yyyy-MM-dd");
        this.desc = rs.getString("FDesc");
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        if (sRowStr.equals("")) {
            return;
        }
        String[] sRowAry = sRowStr.split("\t");
        this.beginDate = sRowAry[0];
        this.endDate = sRowAry[1];
        this.ValuationDayRule = sRowAry[2];
        this.desc = sRowAry[3];
        this.ValuationDaysCode = sRowAry[4];
        this.PortCode = sRowAry[5];
        this.isDel = Integer.parseInt(sRowAry[6]);
        super.parseRecLog();
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.ValuationDayDate).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
}
