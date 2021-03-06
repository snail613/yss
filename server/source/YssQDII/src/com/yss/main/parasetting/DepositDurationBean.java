package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class DepositDurationBean
    extends BaseDataSettingBean implements IDataSetting {
    private String depDurCode = ""; //存款期限代码
    private String depDurName = ""; //存款期限名称
    private int duration; //期限
    private String durUnit = ""; //期限单位
    private DepositDurationBean filterType;
    private String desc = "";
    private String oldDepDurCode = "";
    private String sRecycled = "";
    public int getDuration() {
        return duration;
    }

    public String getDesc() {
        return desc;
    }

    public String getDurUnit() {
        return durUnit;
    }

    public DepositDurationBean getFilterType() {
        return filterType;
    }

    public String getOldDepDurCode() {
        return oldDepDurCode;
    }

    public String getDepDurCode() {
        return depDurCode;
    }

    public void setDepDurName(String depDurName) {
        this.depDurName = depDurName;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDurUnit(String durUnit) {
        this.durUnit = durUnit;
    }

    public void setFilterType(DepositDurationBean filterType) {
        this.filterType = filterType;
    }

    public void setOldDepDurCode(String oldDepDurCode) {
        this.oldDepDurCode = oldDepDurCode;
    }

    public void setDepDurCode(String depDurCode) {
        this.depDurCode = depDurCode;
    }

    public String getDepDurName() {
        return depDurName;
    }

    public DepositDurationBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql =
            " select y.* from " +
            "( select FDepDurCode  from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
            " )  x join ";
        strSql = strSql + "(select a.*,b.FUserName as creator,c.FUserName as checkuser,f.FVocName as FDurUnitName from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
            " a " +
            " left join(select FUserCode,FUserName from  Tb_Sys_UserList)b on b.FUserCode=a.FCreator " +
            " left join(select FUserCode,FUserName from  Tb_Sys_UserList)c on c.FUserCode=a.FCheckUser " +
            " left join Tb_Fun_Vocabulary f on a.FDurUnit = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_DEP_DURUNIT) +
            buildFilterSql() +
            ") y on y.FDepDurCode=x.FDepDurCode" +
            " order by y.FDepDurCode";
        return builderListViewData(strSql);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String sVocStr = "";
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.depDurCode = rs.getString("FDepDurCode") + "";
                this.depDurName = rs.getString("FDepDurName") + "";
                this.duration = rs.getInt("FDuration");
                this.durUnit = rs.getString("FDurUnit") + "";
                this.desc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt("FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";

                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_DEP_DURUNIT);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    public String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = "where 1=1";
            if (this.filterType.depDurCode.length() > 0) {
                sResult = sResult + " and a.FDepDurCode like '" +
                    filterType.depDurCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.depDurName.length() > 0) {
                sResult = sResult + " and a.FDepDurName like '" +
                    filterType.depDurName.replaceAll("'", "''") + "%'";
            }
          //edit by lidaolong 20110427 BUG1807存款期限设置界面，筛选按钮的功能不准确。
            if(this.filterType.duration>0){
            	  sResult = sResult + " and a.FDuration = " +
                  filterType.duration + "";
            }//end  by lidaolong
            
            if (this.filterType.durUnit.length() > 0 && !this.filterType.durUnit.equals("99")) {//edit by lidaolong 20110427 BUG1807存款期限设置界面，筛选按钮的功能不准确。 
                sResult = sResult + " and a.FDurUnit like '" +
                    filterType.durUnit.replaceAll("'", "''") + "%'";
            }

        }
        return sResult;

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {

        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "存款期限代码\t存款期限名称";
            strSql =
                " select y.* from " +
                "( select FDepDurCode  from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FCheckState=1)  x join ";
            strSql = strSql + "(select a.*,b.FUserName as creator,c.FUserName as checkuser,f.FVocName as FDurUnitName from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " a " +
                " left join(select FUserCode,FUserName from  Tb_Sys_UserList)b on b.FUserCode=a.FCreator " +
                " left join(select FUserCode,FUserName from  Tb_Sys_UserList)c on c.FUserCode=a.FCheckUser " +
                " left join Tb_Fun_Vocabulary f on a.FDurUnit = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_DEP_DURUNIT) +
                ") y on y.FDepDurCode=x.FDepDurCode" +
                "  order by y.FDepDurCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FDepDurCode") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FDepDurName") + "").trim());
                bufShow.append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.depDurCode = rs.getString("FDepDurCode") + "";
                this.depDurName = rs.getString("FDepDurName") + "";
                this.duration = rs.getInt("FDuration");
                this.durUnit = rs.getString("FDurUnit") + "";
                this.desc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt("FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";

                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用存款利率出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                "(FDepDurCode,FDepDurName,FDuration,FDurUnit,FDesc,FCheckState,FCreator,FCreateTime)" +
                " values(" + dbl.sqlString(this.depDurCode) + "," +
                dbl.sqlString(this.depDurName) + "," +
                this.duration + "," +
                dbl.sqlString(this.durUnit) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + ")";
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存存款期限出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_DepositDuration"), "FDepDurCode",
                               this.depDurCode, this.oldDepDurCode);
    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理存款期限的审核和未审核的单条信息。
     *  新方法功能：可以处理存款期限审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      String sqlStr="";
//      boolean bTrans=false;
//      Connection con=dbl.loadConnection();
//      try
//      {
//         con.setAutoCommit(false);
//         bTrans=true;
//         sqlStr=" update "+pub.yssGetTableName("Tb_Para_DepositDuration")+
//              " set FCheckState="+this.checkStateId +
//              ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//              ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//              "' where FDepDurCode = " + dbl.sqlString(this.depDurCode);
//         dbl.executeSql(sqlStr);
//         con.commit();
//         bTrans=true;
//         con.setAutoCommit(true);
//
//      }catch(Exception e)
//      {
//         throw new YssException("审核存款期限出错",e);
//      }finally
//      {
//            dbl.endTransFinal(con, bTrans);
//      }
        //修改后的代码
        //---------------------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = " update " +
                        pub.yssGetTableName("Tb_Para_DepositDuration") +
                        " set FCheckState=" + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FDepDurCode = " + dbl.sqlString(this.depDurCode);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而depDurCode不为空，则按照depDurCode来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (depDurCode != null && !depDurCode.equalsIgnoreCase("")) {
                strSql = " update " +
                    pub.yssGetTableName("Tb_Para_DepositDuration") +
                    " set FCheckState=" + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FDepDurCode = " + dbl.sqlString(this.depDurCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("审核存款期限出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //------------------------------end
    }

    /**
     * 删除数据，即是放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            strSql = " update " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FDepDurCode = " + dbl.sqlString(this.depDurCode);
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除存款期限出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " set FDepDurCode=" + dbl.sqlString(this.depDurCode) + "," +
                "FDepDurName=" + dbl.sqlString(this.depDurName) + "," +
                "FDuration=" + this.duration + "," +
                "FDurUnit=" + dbl.sqlString(this.durUnit) + "," +
                "FDesc=" + dbl.sqlString(this.desc) + "," +
                "FCheckState = " + (pub.getSysCheckState() ? "0" : "1") + "," +
                //---edit by songjie 2012.04.10 BUG 4061 QDV4赢时胜（测试）2012年3月16日01_B start---//
//                "FCreator = " + dbl.sqlString(this.creatorCode) + "," +
                "FCheckTime = " + dbl.sqlString(this.creatorTime) + "," +
                "FCheckUser = " + dbl.sqlString(this.creatorCode) +
                //---edit by songjie 2012.04.10 BUG 4061 QDV4赢时胜（测试）2012年3月16日01_B end---//
                " where FDepDurCode = " + dbl.sqlString(this.oldDepDurCode);
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改存款期限出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";

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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.depDurCode).append("\t");
        buf.append(this.depDurName).append("\t");
        buf.append(this.duration).append("\t");
        buf.append(this.durUnit).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        double dblDuration = 0.0;
        String strDurUnit = "";
        String str = ""; //查询基础汇率的查询语句
        ResultSet rs = null;
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        try {
            str = "select * from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FDepDurCode =" + dbl.sqlString(sType);
            rs = dbl.openResultSet(str);
            while (rs.next()) {
                dblDuration = rs.getDouble("FDuration");
                strDurUnit = rs.getString("FDurUnit");
            }
        } catch (Exception e) {
            throw new YssException("获取现金帐户信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dblDuration + "\t" + strDurUnit;
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
        String sTemp = "";
        String[] req = null;
        if (sRowStr.equals("")) {
            return;
        }
        //20130110 added by liubo.Story #2839
        //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
        //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
        //=====================================
        if (sRowStr.split("<Logging>").length >= 2)
        {
        	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
        }
        sRowStr = sRowStr.split("<Logging>")[0];
        //==================end===================
        if (sRowStr.indexOf("\r\t") > 0) {
            sTemp = sRowStr.split("\r\t")[0];
        } else {
            sTemp = sRowStr;
        }
        sRecycled = sRowStr;
        req = sTemp.split("\t");
        this.depDurCode = req[0];
        this.depDurName = req[1];
        if (req[2].length() != 0) {
            this.duration = Integer.parseInt(req[2]);
        }
        this.durUnit = req[3];
        //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B start---//
        if(req[4].indexOf("【Enter】") != -1){
        	this.desc = req[4].replaceAll("【Enter】", "\r\n");
        }else{
        	this.desc = req[4];
        }
        //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B end---//
        
        this.checkStateId = Integer.parseInt(req[5]);
        this.oldDepDurCode = req[6];
        super.parseRecLog();
        if (sRowStr.indexOf("\r\t") > 0) {
            this.filterType = new DepositDurationBean();
            this.filterType.setYssPub(pub);
            this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        DepositDurationBean befDepositDuration = new DepositDurationBean();
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FDepDurCode=" + dbl.sqlString(this.oldDepDurCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befDepositDuration.depDurCode = rs.getString("FDepDurCode") + "";
                befDepositDuration.depDurName = rs.getString("FDepDurName") + "";
                befDepositDuration.duration = rs.getInt("FDuration");
                befDepositDuration.durUnit = rs.getString("FDurUnit") + "";
                befDepositDuration.desc = rs.getString("FDesc") + "";
            }
            return befDepositDuration.buildRowStr();
        } catch (Exception e) {
            throw new YssException("保存修改前数据出错", e);
        }

    }

    /**
     * 从存款期限的回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
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
                //循环执行这些语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_DepositDuration") +
                        " where FDepDurCode = " + dbl.sqlString(this.depDurCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (depDurCode != "" && depDurCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_DepositDuration") +
                    " where FDepDurCode = " + dbl.sqlString(this.depDurCode);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
