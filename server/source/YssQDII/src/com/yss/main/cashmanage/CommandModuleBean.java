package com.yss.main.cashmanage;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>CommandModuleBean 划款指令模板设置 </p>
 * <p>Description:1 </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author zhouwei 20111223
 * story 2004 QDV4赢时胜(上海开发部)2011年12月8日01_A 
 */
public class CommandModuleBean
    extends BaseDataSettingBean implements IDataSetting {

    private String RepCode = ""; //模板代码
    private String RepName = ""; //模板名称
    private String RepType = ""; //模板类型
    private int RepRows; //行数
    private int RepCols; //列数
    private int FixRows; //固定行数
    private int FixCols; //固定列数
    private String RCSize = ""; //单元格大小
    private String Merge = ""; //合并单元格
    private String BalFmt = ""; //金额模板
    private String Print = ""; //打印
    private String CtlGrpCode = ""; //控件组代码
    private String BeanId = "";
    private String Desc = ""; //描述
    private String TabCellInfo = ""; //报表单元格信息

    private String oldRepCode;
    private CommandModuleBean filterType;
    private String sRecycled = ""; //回收站  
    
    private String titleFormat="";
    
    public String getTitleFormat() {
        return titleFormat;
    }

    public void setTitleFormat(String titleFormat) {
          this.titleFormat=titleFormat;
    }
    //---end---
    
    public String getPrint() {
        return Print;
    }

    public CommandModuleBean getFilterType() {
        return filterType;
    }

    public String getRepType() {
        return RepType;
    }

    public String getRepName() {
        return RepName;
    }

    public int getFixRows() {
        return FixRows;
    }

    public int getRepCols() {
        return RepCols;
    }

    public String getDesc() {
        return Desc;
    }

    public String getBeanId() {
        return BeanId;
    }

    public int getRepRows() {
        return RepRows;
    }

    public void setOldRepCode(String oldRepCode) {
        this.oldRepCode = oldRepCode;
    }

    public void setPrint(String Print) {
        this.Print = Print;
    }

    public void setFilterType(CommandModuleBean filterType) {
        this.filterType = filterType;
    }

    public void setRepType(String RepType) {
        this.RepType = RepType;
    }

    public void setRepName(String RepName) {
        this.RepName = RepName;
    }

    public void setFixRows(int FixRows) {
        this.FixRows = FixRows;
    }

    public void setRepCols(int RepCols) {
        this.RepCols = RepCols;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public void setBeanId(String BeanId) {
        this.BeanId = BeanId;
    }

    public void setRepRows(int RepRows) {
        this.RepRows = RepRows;
    }

    public String getOldRepCode() {
        return oldRepCode;
    }

    public void setRCSize(String RCSize) {
        this.RCSize = RCSize;
    }

    public String getRCSize() {
        return RCSize;
    }

    public void setRepCode(String RepCode) {
        this.RepCode = RepCode;
    }

    public String getRepCode() {
        return RepCode;
    }

    public void setBalFmt(String BalFmt) {
        this.BalFmt = BalFmt;
    }

    public String getBalFmt() {
        return BalFmt;
    }

    public void setCtlGrpCode(String CtlGrpCode) {
        this.CtlGrpCode = CtlGrpCode;
    }

    public String getCtlGrpCode() {
        return CtlGrpCode;
    }

    public void setTabCellInfo(String TabCellInfo) {
        this.TabCellInfo = TabCellInfo;
    }

    public String getTabCellInfo() {
        return TabCellInfo;
    }

    public void setFixCols(int FixCols) {
        this.FixCols = FixCols;
    }

    public int getFixCols() {
        return FixCols;
    }

    public void setMerge(String Merge) {
        this.Merge = Merge;
    }

    public String getMerge() {
        return Merge;
    }

    public CommandModuleBean() {
    }

    public CommandModuleBean(String sRepCode) {
        this.RepCode = sRepCode;
    }

    /**
     * parseRowStr
     * 解析数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        sRecycled = sRowStr;
        try {
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
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];

            } else {
                sTmpStr = sRowStr;

            }
            if (sTmpStr.indexOf("\n\n") >= 0) {
                this.TabCellInfo = sTmpStr.split("\n\n")[1];
                sTmpStr = sTmpStr.split("\n\n")[0];
            }

            reqAry = sTmpStr.split("\t", -1);
            this.RepCode = reqAry[0];
            this.RepName = reqAry[1];
            this.RepType = reqAry[2];

            this.RepRows = Integer.parseInt(reqAry[3]);
            this.RepCols = Integer.parseInt(reqAry[4]);
            this.FixRows = Integer.parseInt(reqAry[5]);
            this.FixCols = Integer.parseInt(reqAry[6]);
            this.RCSize = reqAry[7];
            this.Merge = reqAry[8];
            this.BalFmt = reqAry[9];
            this.Print = reqAry[10];
            this.CtlGrpCode = reqAry[11];
            this.BeanId = reqAry[12];
            this.Desc = reqAry[13];
            super.checkStateId = Integer.parseInt(reqAry[14].length() > 0 ? reqAry[14] : "0");
            this.titleFormat=reqAry[15];//add by huangqirong 2011-09-06 story #1277
            this.oldRepCode = reqAry[16];//modify by huangqirong 2011-09-06 story #1277
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new CommandModuleBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析划款指令模板请求信息出错\r\n" + e.getMessage(), e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.RepCode).append("\f\t");
        buf.append(this.RepName).append("\f\t");
        buf.append(this.RepRows).append("\f\t");
        buf.append(this.RepCols).append("\f\t");
        buf.append(this.FixRows).append("\f\t");
        buf.append(this.FixCols).append("\f\t");
        buf.append(this.RCSize).append("\f\t");
        buf.append("").append("\f\t"); //FAuthor
        buf.append(this.Desc).append("\f\t");
        buf.append(this.Merge).append("\f\t");
        buf.append(this.Print).append("\f\t");
        buf.append(this.RepType).append("\f\t");
        buf.append(this.BalFmt).append("\f\t");
        buf.append(this.CtlGrpCode).append("\f\t");
        buf.append("!!" + this.TabCellInfo.replaceAll("\f\t", "")).append("\f\t"); //修改此处原因  MS00325    划款指令模板中回收站“清除”功能 解析时出现错误 xuqiji 2009 03 25
        buf.append(this.BeanId).append("\f\t");
        buf.append(this.titleFormat).append("\f\t");;//add by huangqirong 2011-09-06 story #1277
        
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * checkInput
     * 检查输入数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_data_commondModel"), "FCommmondCODE",
                               this.RepCode, this.oldRepCode);
    }

    /**
     * saveSetting
     *
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql =
                "insert into " + pub.yssGetTableName("TB_data_commondModel") +
                "(FCommmondCODE , FCommmondNAME , FRepType, FRows, FCols, FFixRows, FFixCols, FRCSize, " +
                " FMerge, FBalFmt, FPrint, FCtlGrpCode, FBeanId, FDesc," +
                " FCheckState, FCreator, FCreateTime, FCheckUser,FTITLTFORMAT)" +
                " values(" + dbl.sqlString(this.RepCode) + "," +
                dbl.sqlString(this.RepName) + "," +
                dbl.sqlString(this.RepType) + "," +
                this.RepRows + "," + this.RepCols + "," +
                this.FixRows + "," + this.FixCols + ",'" +
                this.RCSize + "','" +
                this.Merge + "','" +
                this.BalFmt + "','" +
                this.Print + "'," +
                dbl.sqlString(this.CtlGrpCode) + "," +
                dbl.sqlString(this.BeanId) + "," +
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ","+dbl.sqlString(this.titleFormat)
                + ")";
                
            	

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (this.TabCellInfo == null || this.TabCellInfo.trim().length() > 0) {
                RepTabCellBean tabcell = new RepTabCellBean(this.RepCode, this.RepCode);
                tabcell.setYssPub(pub);
                tabcell.setRelaType("FMT");
                tabcell.saveMutliSetting(this.TabCellInfo);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("保存划款指令模板信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * editSetting：
     *modify by zhangjun 2012-05-22 BUG#4451
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("TB_data_commondModel") +
                " set FCommmondCODE  = " +
                dbl.sqlString(this.RepCode) + ", FCommmondNAME  = " +
                dbl.sqlString(this.RepName) + ", FRepType = " +
                dbl.sqlString(this.RepType) + ", FRows = " +
                this.RepRows + ", FCols=" +
                this.RepCols + ", FFixRows=" +
                this.FixRows + ", FFixCols=" +
                this.FixCols + ", FRCSize='" +
                this.RCSize + "', FMerge='" +
                this.Merge + "', FBalFmt='" +
                this.BalFmt + "', FPrint='" +
                this.Print + "', FCtlGrpCode=" +
                dbl.sqlString(this.CtlGrpCode) + ", FBeanId=" +
                dbl.sqlString(this.BeanId) + ", FDesc = " +
                dbl.sqlString(this.Desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ",FTITLTFORMAT="+dbl.sqlString(this.titleFormat)+			//add by huangqirong 2011-09-06 story #1277
                " where FCommmondCODE = " +
                dbl.sqlString(this.oldRepCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (this.TabCellInfo == null || this.TabCellInfo.trim().length() > 0) {
                RepTabCellBean tabcell = new RepTabCellBean(this.RepCode, this.oldRepCode);
                tabcell.setYssPub(pub);
                tabcell.setRelaType("FMT");
                tabcell.saveMutliSetting(this.TabCellInfo);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改划款指令模板信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting : 删除一条设置信息
	 *modify by zhangjun 2012-05-22 BUG#4451
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("TB_data_commondModel") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCommmondCODE  = " +
                dbl.sqlString(this.RepCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除划款指令模板信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * auditSetting : 审核设置信息  
	 *modify by zhangjun 2012-05-22 BUG#4451 
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        Statement st = null;
        String array[] = null; 
        try {
            
            if (null != sRecycled && !sRecycled.trim().equalsIgnoreCase("")) { ////如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                st = conn.createStatement();
                conn.setAutoCommit(false);
                for (int i = 0; i < array.length; i++) { //循环执行这些update语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    //-------------------------------------------------------------------------------------
                    strSql = "update " + pub.yssGetTableName("TB_data_commondModel") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FCommmondCODE  = " +
                        dbl.sqlString(this.RepCode);
                    st.addBatch(strSql);
                    
                }
                st.executeBatch();
            }
            //------------------------------------------------------------------------------
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核划款指令模板信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
        }
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
     * getSetting
     *modify by zhangjun 2012-05-22 BUG#4451
     * @return IParaSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, ' ' as FCreatorName, ' ' as FCheckUserName from " +
                pub.yssGetTableName("TB_data_commondModel") + " a " +
                " where FCommmondCODE  = " + dbl.sqlString(RepCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.RepCode = rs.getString("FCommmondCODE") + "";
                this.RepName = rs.getString("FCommmondNAME") + "";
                this.RepType = rs.getString("FRepType") + "";
                this.RepRows = rs.getInt("FRows");
                this.RepCols = rs.getInt("FCols");
                this.FixRows = rs.getInt("FFixRows");
                this.FixCols = rs.getInt("FFixCols");
                this.RCSize = rs.getString("FRCSize") + "";
                this.Merge = rs.getString("FMerge") + "";
                this.BalFmt = rs.getString("FBalFmt") + "";
                this.Print = rs.getString("FPrint") + "";
                this.CtlGrpCode = rs.getString("FCtlGrpCode") + "";
                this.BeanId = rs.getString("FBeanId") + "";
                this.Desc = rs.getString("FDesc") + "";
                this.titleFormat=rs.getString("FTITLTFORMAT") != null?rs.getString("FTITLTFORMAT"):"";//add by huangqirong 2011-09-06 story #1277
                super.setRecLog(rs);
            }

            return null;
        } catch (Exception e) {
            throw new YssException("获取划款指令模板信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * 筛选条件modify by zhangjun 2012-05-22 BUG#4451
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.RepCode.length() != 0) {
                sResult = sResult + " and a.FCommmondCODE  like '" +
                    filterType.RepCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.RepName.length() != 0) {
                sResult = sResult + " and a.FCommmondNAME like '" +
                    filterType.RepName.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.RepType.length() != 0 && !this.filterType.RepType.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FRepType like '" +
                    filterType.RepType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.Desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.Desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }
    //modify by zhangjun 2012-05-22 BUG#4451
    public void setRepAttr(ResultSet rs) throws SQLException {
        this.RepCode = rs.getString("FCommmondCODE") + "";
        this.RepName = rs.getString("FCommmondNAME") + "";
        this.RepType = rs.getString("FRepType") + "";
        this.RepRows = rs.getInt("FRows");
        this.RepCols = rs.getInt("FCols");
        this.FixRows = rs.getInt("FFixRows");
        this.FixCols = rs.getInt("FFixCols");
        this.RCSize = rs.getString("FRCSize") + "";
        this.Merge = rs.getString("FMerge") + "";
        this.BalFmt = rs.getString("FBalFmt") + "";
        this.Print = rs.getString("FPrint") + "";
        this.CtlGrpCode = rs.getString("FCtlGrpCode") + "";
        this.BeanId = rs.getString("FBeanId") + "";
        this.Desc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sTabData = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.setRepAttr(rs);

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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_REP_FORMATTYPE + "," +
                                        YssCons.YSS_REP_SAVETYPE
                                        //story 1645 by zhouwei 20111212 QDII工银2011年9月13日10_A
                                        +","+YssCons.YSS_REP_FORMATVARIABLE);//划款指令模板变量
            							//------------end------------
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取划款指令模板信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData1
     * 获取划款指令模板数据
     * @return String
	 *modify by zhangjun 2012-05-22 BUG#4451
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from (select * from " +
        pub.yssGetTableName("TB_data_commondModel") + " where freptype='CashCommand' ) a " +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +      
            buildFilterSql() +
            " order by a.FCommmondCODE , a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *modify by zhangjun 2012-05-22 BUG#4451
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("TB_data_commondModel") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    public String getListViewData2() throws YssException {
        String strSql = "";
        strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("TB_data_commondModel") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " where a.FCheckState = 1 order by a.FCommmondCODE , a.FCheckState, a.FCreateTime desc";

        return this.builderListViewData(strSql);

    }

    /**
     * getListViewData3
     * 由于划款指令信息字段内容可能包含\t，\r\c等内容，所以分隔符一律在常规基础上前面加\f
     * 返回指定划款指令的信息，套账信息、表级内容、模板、数据间用\f\f分开，字段用\f\t间隔，记录用\f\r\n间隔
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strSql = "", strReturn = "";
        StringBuffer buf = new StringBuffer();
        RepTabCellBean tabCell = new RepTabCellBean();
        tabCell.setYssPub(pub);
        ResultSet rs = null;
        boolean bTmp = false;
        int lRows = 0, lCols = 0;
        try {
            //获取划款指令信息
            //把客户端参数分割成表名和期间
            //先加载当前套账相关信息，以及表名、期间信息...打印的权限
            //因为划款指令页面是htm，第一次进入参数是提前存到session，htm加载后loadrpt要自动读取这些设置并回传给htm

            buf.append(pub.getAssetGroupCode()).append("\f\t");
            buf.append(pub.getAssetGroupName()).append("\f\t");
            buf.append(pub.getUserName()).append("\f\t");
            buf.append(this.RepCode).append("\f\t");
            //因为打印直接在客户端进行，打印时就不提交了。所以这里也传上打印权限
            //可写权限、客户端可实施写保护
            // if(!new UserRights(pubVar).checkRights(YwCons.uoCw_RptPrt))
            buf.append("1").append("\f\t");
            //if(!new UserRights(pubVar).checkRights(YwCons.uoCw_RptMod))
            buf.append("0").append("\f\f");
            if (this.RepCode.length() == 0) {
                return buf.toString(); //空表
            }
            //modify by zhangjun 2012-05-22 BUG#4451
            strSql = "select * from " + pub.yssGetTableName("TB_data_commondModel") +
                     " where FCommmondCODE=" + dbl.sqlString(this.RepCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                lRows = rs.getInt("FRows");
                lCols = rs.getInt("FCols");

                this.RepCode = rs.getString("FCommmondCODE");
                this.RepName = rs.getString("FCommmondNAME");
                this.RepRows = rs.getInt("FRows");
                this.RepCols = rs.getInt("FCols");
                this.FixRows = rs.getInt("FFixRows");
                this.FixCols = rs.getInt("FFixCols");
                this.RCSize = rs.getString("FRCSize");
                this.Desc = rs.getString("FDesc") != null ? rs.getString("FDesc") : "";
                this.Merge = rs.getString("FMerge") != null ? rs.getString("FMerge") : "";
                this.Print = rs.getString("FPrint") != null ? rs.getString("FPrint") : "";
                this.titleFormat=rs.getString("FTITLTFORMAT") != null?rs.getString("FTITLTFORMAT"):"";//add by huangqirong 2011-09-06 story #1277
                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);

            }            
            else {
                buf.delete(0, buf.length());
            }         
            rs.getStatement().close();

            //获取表结构
            strSql = " select * from " + pub.yssGetTableName("Tb_Rep_Cell") +
                	 " where FRelaCode=" + dbl.sqlString(this.RepCode) +
                	 " and FRelaType = 'FMT' " +
                	 " and FRow<" + lRows + " and FCol<" + lCols;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (bTmp) {
                    buf.append("\f\r\n");
                }

                tabCell.setRow(rs.getString("FRow"));
                tabCell.setCol(rs.getString("FCol"));
                if (rs.getString("FContent") == null) {
                    tabCell.setContent(YssFun.rTrim(""));
                } else {
                    tabCell.setContent(YssFun.rTrim(rs.getString("FContent")));
                }
                tabCell.setLColor(rs.getString("FLColor"));
                tabCell.setLLine(rs.getString("FLLine"));
                tabCell.setTColor(rs.getString("FTColor"));
                tabCell.setTLine(rs.getString("FTLine"));
                tabCell.setBColor(rs.getString("FBColor"));
                tabCell.setBLine(rs.getString("FBLine"));
                tabCell.setRColor(rs.getString("FRColor"));
                tabCell.setRLine(rs.getString("FRLine"));
                tabCell.setBackColor(rs.getString("FBackColor"));
                tabCell.setForeColor(rs.getString("FForeColor"));
                tabCell.setFontName(rs.getString("FFontName"));
                tabCell.setFontSize(String.valueOf(rs.getFloat("FFontSize")));
                tabCell.setFontStyle(rs.getString("FFontStyle"));
                tabCell.setDataType(rs.getString("FDataType"));
                tabCell.setFormat(rs.getString("FFormat") == null ? "" :
                                  rs.getString("FFormat")); //这里其实包含\t间隔的五段
                tabCell.setIMerge(rs.getInt("FIsMergeCol"));
                buf.append(tabCell.buildRowStr());

                if (!bTmp) {
                    bTmp = true;
                }
            }
            rs.getStatement().close();
            //buf.append("\f\f");
            return buf.toString();

        } catch (Exception e) {
            throw new YssException("获取划款指令模板详细信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    /**STORY #441 需优化现在的划款指令自定义模板
     * 动态表头划款指令取数据时需要从预处理数据源中的临时表中取数据
     * getListViewData3
     * 由于划款指令信息字段内容可能包含\t，\r\c等内容，所以分隔符一律在常规基础上前面加\f
     * 返回指定划款指令的信息，套账信息、表级内容、模板、数据间用\f\f分开，字段用\f\t间隔，记录用\f\r\n间隔
     * @return String
	 *modify by zhangjun 2012-05-22 BUG#4451
     */
    public String getDynRepListViewData3() throws YssException {
    	String strSql = "", strReturn = "";
    	StringBuffer buf = new StringBuffer();
    	RepTabCellBean tabCell = new RepTabCellBean();
    	tabCell.setYssPub(pub);
    	ResultSet rs = null;
    	boolean bTmp = false;
    	int lRows = 0, lCols = 0;
    	try {
    		//获取划款指令信息
    		//把客户端参数分割成表名和期间
    		//先加载当前套账相关信息，以及表名、期间信息...打印的权限
    		//因为划款指令页面是htm，第一次进入参数是提前存到session，htm加载后loadrpt要自动读取这些设置并回传给htm
    		
    		buf.append(pub.getAssetGroupCode()).append("\f\t");
    		buf.append(pub.getAssetGroupName()).append("\f\t");
    		buf.append(pub.getUserName()).append("\f\t");
    		buf.append(this.RepCode).append("\f\t");
    		//因为打印直接在客户端进行，打印时就不提交了。所以这里也传上打印权限
    		//可写权限、客户端可实施写保护
    		// if(!new UserRights(pubVar).checkRights(YwCons.uoCw_RptPrt))
    		buf.append("1").append("\f\t");
    		//if(!new UserRights(pubVar).checkRights(YwCons.uoCw_RptMod))
    		buf.append("0").append("\f\f");
    		if (this.RepCode.length() == 0) {
    			return buf.toString(); //空表
    		}
    		
    		strSql = "select * from " + pub.yssGetTableName("TB_data_commondModel") +
    		" where FCommmondCODE =" + dbl.sqlString(this.RepCode);
    		//add by licai 20110209 STORY #441 需优化现在的划款指令自定义模板
    		if(this.RepCode.toLowerCase().indexOf("dyn")>0){
    			strSql = "select * from TMP_REP_FORMAT" +
    			" where FCommmondCODE=" + dbl.sqlString(this.RepCode);
    		}
    		//add by licai 20110209 STORY #441 =====================end
    		rs = dbl.openResultSet(strSql);
    		if (rs.next()) {
    			lRows = rs.getInt("FRows");
    			lCols = rs.getInt("FCols");
    			
    			this.RepCode = rs.getString("FCommmondCODE");
    			this.RepName = rs.getString("FCommmondNAME");
    			this.RepRows = rs.getInt("FRows");
    			this.RepCols = rs.getInt("FCols");
    			this.FixRows = rs.getInt("FFixRows");
    			this.FixCols = rs.getInt("FFixCols");
    			this.RCSize = rs.getString("FRCSize");
    			this.Desc = rs.getString("FDesc") != null ? rs.getString("FDesc") : "";
    			this.Merge = rs.getString("FMerge") != null ? rs.getString("FMerge") : "";
    			this.Print = rs.getString("FPrint") != null ? rs.getString("FPrint") : "";
    			
    			buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
    			
    		}
    		//by xuqiji 20090423  MS00389  筛选划款指令模板中的数据系统会报错 -------------------------//
    		else {
    			buf.delete(0, buf.length());
    		}
    		//by xuqiji 20090423  MS00389  筛选划款指令模板中的数据系统会报错 -------------------------//
    		rs.getStatement().close();
    		
    		//获取表结构
    		strSql = " select * from " + pub.yssGetTableName("Tb_Rep_Cell") +
    		" where FRelaCode=" + dbl.sqlString(this.RepCode) +
    		" and FRelaType = 'FMT' " +
    		" and FRow<" + lRows + " and FCol<" + lCols;
    		//add by licai 20110209 STORY #441 需优化现在的划款指令自定义模板
    		if(this.RepCode.toLowerCase().indexOf("dyn")>0){
    			strSql = " select * from TMP_REP_CELL" +
    			" where FRelaCode=" + dbl.sqlString(this.RepCode) +
    			" and FRelaType = 'FMT' " +
    			" and FRow<" + lRows + " and FCol<" + lCols;
    		}
    		//add by licai 20110209 STORY #441 =====================end
    		rs = dbl.openResultSet(strSql);
    		while (rs.next()) {
    			if (bTmp) {
    				buf.append("\f\r\n");
    			}
    			
    			tabCell.setRow(rs.getString("FRow"));
    			tabCell.setCol(rs.getString("FCol"));
    			if (rs.getString("FContent") == null) {
    				tabCell.setContent(YssFun.rTrim(""));
    			} else {
    				tabCell.setContent(YssFun.rTrim(rs.getString("FContent")));
    			}
    			tabCell.setLColor(rs.getString("FLColor"));
    			tabCell.setLLine(rs.getString("FLLine"));
    			tabCell.setTColor(rs.getString("FTColor"));
    			tabCell.setTLine(rs.getString("FTLine"));
    			tabCell.setBColor(rs.getString("FBColor"));
    			tabCell.setBLine(rs.getString("FBLine"));
    			tabCell.setRColor(rs.getString("FRColor"));
    			tabCell.setRLine(rs.getString("FRLine"));
    			tabCell.setBackColor(rs.getString("FBackColor"));
    			tabCell.setForeColor(rs.getString("FForeColor"));
    			tabCell.setFontName(rs.getString("FFontName"));
    			tabCell.setFontSize(String.valueOf(rs.getFloat("FFontSize")));
    			tabCell.setFontStyle(rs.getString("FFontStyle"));
    			tabCell.setDataType(rs.getString("FDataType"));
    			tabCell.setFormat(rs.getString("FFormat") == null ? "" :
    				rs.getString("FFormat")); //这里其实包含\t间隔的五段
    			tabCell.setIMerge(rs.getInt("FIsMergeCol"));
    			buf.append(tabCell.buildRowStr());
    			
    			if (!bTmp) {
    				bTmp = true;
    			}
    		}
    		rs.getStatement().close();
    		//buf.append("\f\f");
    		return buf.toString();
    		
    	} catch (Exception e) {
    		throw new YssException("获取划款指令模板详细信息出错\r\n" + e.getMessage(), e);
    	} finally {
    		dbl.closeResultSetFinal(rs);
    	}
    	
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
     * getOperValue
     *
     * @param sType String
     * @return String
     * @throws YssException 
     */
    public String getOperValue(String sType) throws YssException {
    	 //story 1645 by zhouwei 20111213 获取划款指令模板  QDII工银2011年9月13日10_A
    	if(sType.equals("getCashCommandModule")){
    		return getCashCommandModule();
    	}
    	//------------end----------   	
        return "";
    }
    //story 1645 by zhouwei 20111213 获取划款指令模板  QDII工银2011年9月13日10_A
	//modify by zhangjun 2012-05-22 BUG#4451
    private String getCashCommandModule() throws YssException{
    	ResultSet rs=null;
    	String showStr="";
    	String showAll="";
    	StringBuffer bufAll=new StringBuffer();
    	try{
    		String header="模板编号\t模板名称";
    		String sql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
              pub.yssGetTableName("TB_data_commondModel") + " a " +
              " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
              " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
              " where a.FCHECKSTATE=1  and a.FREPTYPE='CashCommand' " +
              " order by a.FCommmondCODE, a.FCheckState, a.FCreateTime desc";
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			showStr+=rs.getString("FCommmondCODE")+"\t"+rs.getString("FCommmondNAME")
    			         +YssCons.YSS_LINESPLITMARK;
    			  this.setRepAttr(rs);

                  bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
              }
           if (bufAll.toString().length() > 2) {
            	  showAll = bufAll.toString().substring(0,
                      bufAll.toString().length() - 2);
              }
    		if(showStr.length()>2){
    			showStr=showStr.substring(0, showStr.length()-2);
    		}
    		return header+"\r\f"+showStr+"\r\f"+showAll;
    	}catch (Exception e) {
			throw new YssException("获取划款指令模板链接错误"+e.getMessage(),e );
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
	*modify by zhangjun 2012-05-22 BUG#4451
     * deleteRecycleData 回收站清除数据功能  xuqiji  MS00325    划款指令模板中回收站“清除”功能 2009 03 24
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        String[] arrData = null;
        Connection conn = dbl.loadConnection(); //获取一个连接
        try {
            if (sRecycled != "" && sRecycled != null) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                arrData = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < arrData.length; i++) { //循环执行这些删除语句
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("TB_data_commondModel") +
                        " where FCommmondCODE = " + dbl.sqlString(this.RepCode);
                    dbl.executeSql(strSql); //执行sql语句
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
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
