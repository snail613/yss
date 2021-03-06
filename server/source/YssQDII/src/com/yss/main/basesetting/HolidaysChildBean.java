package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class HolidaysChildBean
    extends BaseDataSettingBean implements
    IDataSetting {

    private String beginDate; //开始日期（解析）
    private String endDate; //结束日期（解析）
    private String holidayDate = ""; //节假日日期（返回）
    private int type; //类型（解析，返回）
    private String holidayRule = ""; //假日规则（解析）
    private String desc = ""; //描述（解析，返回）
    private String holidaysCode = ""; //节假日群代码（解析）
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.14 方浩
    public HolidaysChildBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.holidayDate).append("\t");
        buf.append(this.type).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getListViewData1
     * 获取节假日设置信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sAllDataStr = "";
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        String sYearMonth = null;
        try {
            strSql = "select a.*," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_ChildHoliday a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FHolidaysCode = " +
                dbl.sqlString(this.holidaysCode) +
                " order by a.FDate";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                setHolidaysChildAttr(rs);
                if ( (sYearMonth != null) &&
                    (!sYearMonth.equalsIgnoreCase(YssFun.formatDate(rs.getDate(
                        "FDate"), "yyyy-MM")))) {
                    bufAll.append("\r\f").append(this.buildRowStr());
                } else {
                    bufAll.append(YssCons.YSS_LINESPLITMARK).append(this.
                        buildRowStr());
                }
                sYearMonth = YssFun.formatDate(rs.getDate("FDate"), "yyyy-MM");
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(2,
                    bufAll.toString().length());
            }

            return sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取节假日设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * setHolidaysChildAttr
     * 检查输入是否合法
     * @param rs ResultSet
     */
    private void setHolidaysChildAttr(ResultSet rs) throws SQLException {
        this.holidayDate = YssFun.formatDate(rs.getDate("FDate"), "yyyy-MM-dd");
        this.type = rs.getInt("FType");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * parseRowStr
     * 解析所有节假日设置信息
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
        if (sRowStr.equals("")) {
            return;
        }
        String[] sRowAry = sRowStr.split("\t");
        sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.13 方浩
        this.beginDate = sRowAry[0];
        this.endDate = sRowAry[1];
        this.type = Integer.parseInt(sRowAry[2]);
        this.holidayRule = sRowAry[3];
        this.desc = sRowAry[4];
        this.holidaysCode = sRowAry[5];
        //add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A
        this.checkStateId = Integer.parseInt(sRowAry[6]);
        super.parseRecLog();
    }

    /**
     * saveMutliSetting
     * 新增、修改、删除（对节假日子表的设定）
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        java.util.Date addDate;
        java.util.Date endDate;
        String sWeek;
        //---add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A start---//
        ResultSet rs = null;
        String checkState = "0";
        //---add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A end---//
        try {
            this.parseRowStr(sMutilRowStr);
            conn.setAutoCommit(false);
            bTrans = true;
            
            //---add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A start---//
            strSql = " select distinct FCheckState from Tb_Base_ChildHoliday where FHolidaysCode = " + 
            dbl.sqlString(this.holidaysCode) + " and FDate between " + dbl.sqlDate(this.beginDate.substring(0,4) + "-01-01") + 
            " and " + dbl.sqlDate(this.endDate.substring(0,4) + "-12-31");
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	if(rs.getInt("FCheckState") == 1){
            		checkState = "1";
            		break;
            	}
            }
            
            if("1".equals(checkState)){
            	throw new YssException("包含已审核数据，不可修改，请检查起、止日期设定！");
            }
            //---add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A end---//
            
            strSql = "delete from Tb_Base_ChildHoliday where";
//         if (this.holidayRule == null || this.holidayRule.length() == 0)
//         {
//            strSql += " FType = 0 and ";
//         }
            strSql += " FDate between " +
                dbl.sqlDate(YssFun.toDate(this.beginDate)) + " and " +
                dbl.sqlDate(YssFun.toDate(this.endDate)) +
                " and FHolidaysCode = " + dbl.sqlString(this.holidaysCode);
            dbl.executeSql(strSql);
            addDate = YssFun.toDate(this.beginDate);
            endDate = YssFun.toDate(this.endDate);
            int iDaysNum = YssFun.dateDiff(YssFun.toDate(this.beginDate),
                                           YssFun.toDate(this.endDate));
            endDate = YssFun.addDay(endDate, 1);
            strSql =
                "insert into Tb_Base_ChildHoliday (FHolidaysCode,FDate,FType," +
                "FDesc,FCheckState,FCreator,FCreateTime) values (?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            while (!addDate.equals(endDate)) {
                sWeek = String.valueOf(YssFun.getWeekDay(addDate));
                if (this.holidayRule != null && this.holidayRule.length() != 0) {
                    if (this.holidayRule.indexOf(sWeek) >= 0) {
                        pstmt.setString(1, this.holidaysCode);
                        pstmt.setDate(2, YssFun.toSqlDate(addDate));
                        pstmt.setInt(3, this.type);
                        pstmt.setString(4, this.desc);
                        pstmt.setInt(5, (pub.getSysCheckState() ? 0 : 1));
                        pstmt.setString(6, this.creatorCode);
                        pstmt.setString(7, this.creatorTime);
                        pstmt.executeUpdate();
                    }
                } else if (this.type >= 0) {
                    pstmt.setString(1, this.holidaysCode);
                    pstmt.setDate(2, YssFun.toSqlDate(addDate));
                    pstmt.setInt(3, this.type);
                    pstmt.setString(4, this.desc);
                    pstmt.setInt(5, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(6, this.creatorCode);
                    pstmt.setString(7, this.creatorTime);
                    pstmt.executeUpdate();
                }
                addDate = YssFun.addDay(addDate, 1);
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            dbl.endTransFinal(conn, bTrans);
            throw new YssException("保存节假日信息出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            //add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A
            dbl.closeResultSetFinal(rs);
        }
        return "";
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    public void saveSetting(byte btOper) {
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
    }

    /**
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * checkSetting

        public void checkSetting() {
        }
     */
    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
        //定义一个字符数组来循环删除
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update Tb_Base_ChildHoliday set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FHolidaysCode = " +
                        dbl.sqlString(this.holidaysCode) + 
                        //edit by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A 根据年份更新数据审核状态
                        " and TO_CHAR(FDate,'yyyy') like '" + this.beginDate + "%'" ; //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而holidaysCode不为空，则按照holidaysCode来执行sql语句
            else if ( holidaysCode != null && (!holidaysCode.equalsIgnoreCase(""))) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_ChildHoliday set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FHolidaysCode = " +
                    dbl.sqlString(this.holidaysCode) +
                    //edit by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A 根据年份更新数据审核状态
                    " and TO_CHAR(FDate,'yyyy') like '" + this.beginDate + "%'" ; //更新数据的SQL语句

                dbl.executeSql(strSql); //执行更新操作
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存节假日信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
//----------------end

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException{
    	//---add by songjie 2012.02.08 STORY #2089 QDV4中银基金2011年12月29日01_A start---//
    	if(sType.equals("getHolidayCheckState")){
    		return getHolidayCheckState();
    	}
    	//---add by songjie 2012.02.08 STORY #2089 QDV4中银基金2011年12月29日01_A end---//
        return "";
    }

    /**
     * add by songjie 2012.02.08
     * STORY #2089 QDV4中银基金2011年12月29日01_A
     * 获取相关节假日代码、相关年份的审核状态数据
     * @return
     * @throws YssException
     */
    private String getHolidayCheckState() throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	String checkState = null;
    	try{
    		strSql = " select distinct FCheckState from " + 
    		pub.yssGetTableName("Tb_Base_Childholiday") + 
    		" where TO_CHAR(FDate,'yyyy') like '" + 
    		this.beginDate + "%' and FHOLIDAYSCODE = " + 
    		dbl.sqlString(this.holidaysCode);
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			checkState = rs.getInt("FCheckState") + "";
    		}
    		
			if (checkState == null) {
				if (this.type == 3) {
					checkState = "3";//表示没有相关年份的节假日数据
				} else {
					checkState = "0";
				}
			}
    		
    		return checkState;
    	}catch(Exception e){
    		throw new YssException("获取节假日审核状态出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {

        return "";
    }

    /**
     * deleteRecycleData

        public void deleteRecycleData() {
        }
     */
    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据,可以多个一删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Base_ChildHoliday") +
                        " where FHolidaysCode = " +
                        dbl.sqlString(this.holidaysCode);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而holidaysCode不为空，则按照holidaysCode来执行sql语句
            else if (holidaysCode != "" && holidaysCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_ChildHoliday") +
                    " where FHolidaysCode = " +
                    dbl.sqlString(this.holidaysCode);

                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
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

}
