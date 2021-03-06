package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: 利息算法设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CalcInsMeticBean
    extends BaseDataSettingBean implements
    IDataSetting {
    private String strCIMCode = ""; //利息算法代码
    private String strCIMName = ""; //利息算法名称
    private String strCIMType = ""; //利息算法类型
    private String strFormula = ""; //公式
    private String strSPICode = ""; //计息算法
    private String strSPIName = "";
    private String strDesc = ""; //描述
    private String strIsOnlyCounts = "0"; //初加载时是否显示
    private String strOldCIMCode = "";
    private String status = ""; //是否记入系统信息状态  lzp 11.30 add
    private CalcInsMeticBean filterType;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.14 方浩
    public String getStrOldCIMCode() {
        return this.strOldCIMCode;
    }

    public String getStrCIMCode() {
        return this.strCIMCode;
    }

    public String getStrCIMName() {
        return this.strCIMName;
    }

    public String getStrCIMType() {
        return this.strCIMType;
    }

    public String getStrFormula() {
        return this.strFormula;
    }

    public String getStrSPICode() {
        return this.strSPICode;
    }

    public String getStrDesc() {
        return this.strDesc;
    }

    public void setStrIsOnlyCounts(String strIsOnlyCounts) {
        this.strIsOnlyCounts = strIsOnlyCounts;
    }

    public void setStrOldCIMCode(String strOldCIMCode) {
        this.strOldCIMCode = strOldCIMCode;
    }

    public void setStrCIMCode(String strCIMCode) {
        this.strCIMCode = strCIMCode;
    }

    public void setStrCIMName(String strCIMName) {
        this.strCIMName = strCIMName;
    }

    public void setStrCIMType(String strCIMType) {
        this.strCIMType = strCIMType;
    }

    public void setStrFormual(String strFormual) {
        this.strFormula = strFormual;
    }

    public void setStrSPICode(String strSPICode) {
        this.strSPICode = strSPICode;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrSPIName(String strSPIName) {
        this.strSPIName = strSPIName;
    }

    public CalcInsMeticBean() {
    }

    /**
     * 解析利息算法数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.13 方浩
            reqAry = sTmpStr.split("\t");
            /*
             * 下面这个判断是用来解析字符串如果前台把\r\n改成\r\fhln\n则把它恢复到\r\n；
             * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
             */
            for (int i = 0; i < reqAry.length; i++) {
                if (reqAry[i].indexOf("\r\fhln\n") >= 0) { //如果字符串中有\rfhln\n那么进入方法
                    reqAry[i] = reqAry[i].replaceAll("\r\fhln\n", "\r\n"); //替换这个字符串里的\r\fhln\n
                }
            }
//-------------------------------------------------

            this.strCIMCode = reqAry[0];
            this.strCIMName = reqAry[1];
            this.strCIMType = reqAry[2];
            this.strFormula = reqAry[3];
            this.strSPICode = reqAry[4];
            this.strDesc = reqAry[5];
            this.strOldCIMCode = reqAry[6];
            this.checkStateId = Integer.parseInt(reqAry[7]);
            this.status = reqAry[8]; //lzp add 11.30
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CalcInsMeticBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析交易所设置请求出错", e);
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strIsOnlyCounts.equals("1")) {
                sResult = " and 1=2 ";
            }
            if (this.filterType.strCIMCode.length() != 0) {
                sResult = sResult + " and a.FCIMCode like '" +
                    filterType.strCIMCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strCIMName.length() != 0) {
                sResult = sResult + " and a.FCIMName like '" +
                    filterType.strCIMName.replaceAll("'", "''") + "%'";
            }//by guyichuan 2011.08.01 BUG2217筛选页面下拉框的值不是默认为所有
            if (this.filterType.strCIMType.length() != 0 && !"99".equals(this.filterType.strCIMType)) {
                sResult = sResult + " and a.FCIMType = " +
                    dbl.sqlString(filterType.strCIMType);
            }
            if (this.filterType.strFormula.length() != 0) {
                sResult = sResult + " and a.FFormula = " +
                    dbl.sqlString(filterType.strFormula);
            }
            if (this.filterType.strSPICode.length() != 0) {
                sResult = sResult + " and a.FSPICode = " +
                    dbl.sqlString(filterType.strSPICode);
            }
            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc = " +
                    dbl.sqlString(filterType.strDesc);
            }

        }
        return sResult;
    }

    /**
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.strCIMCode).append("\t");
        buffer.append(this.strCIMName).append("\t");
        buffer.append(this.strCIMType).append("\t");
        buffer.append(this.strFormula).append("\t");
        buffer.append(this.strSPICode).append("\t");
        buffer.append(this.strSPIName).append("\t");
        buffer.append(this.strDesc).append("\t");
        buffer.append(super.buildRecLog());
        return buffer.toString();
    }

    /**
     * 数据验证
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Base_CalcInsMetic", "FCIMCode",
                               this.strCIMCode, this.strOldCIMCode);
    }

    /**
     * 新增利息算法
     * @throws YssException
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            strSql = "insert into Tb_Base_CalcInsMetic" +
                "(FCIMCode, FCIMName, FCIMType, FFormula, FSPICode, FDesc, " +
                "FCheckState, FCreator, FCreateTime, FCheckUser)" +
                "values(" + dbl.sqlString(this.strCIMCode) + ", " +
                dbl.sqlString(this.strCIMName) + ", " +
                dbl.sqlString(this.strCIMType) + ", " +
                dbl.sqlString(this.strFormula) + ", " +
                dbl.sqlString(this.strSPICode) + ", " +
                dbl.sqlString(this.strDesc) + ", " +
                (pub.getSysCheckState() ? "0" : "1") + ", " +
                dbl.sqlString(this.creatorCode) + ", " +
                dbl.sqlString(this.creatorTime) + ", " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-利息算法设置");
                sysdata.setStrCode(this.strCIMCode);
                sysdata.setStrName(this.strCIMName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增利息算法出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        /*     String strSql = null;
             boolean bTrans = false; //代表是否开始了事务
             Connection conn = dbl.loadConnection();
             try{
                strSql = "update Tb_Base_CalcInsMetic set FCheckState = " +
                      this.checkStateId + ", " +
         "FCheckUser = " + dbl.sqlString(pub.getUserCode()) + ", " +
         "FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                      "' where FCIMCode = " + dbl.sqlString(this.strCIMCode);

                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                //---------lzp add 11.30
                if (this.status.equalsIgnoreCase("1")) {
         com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                         funsetting.SysDataBean();
                   sysdata.setYssPub(pub);
                   sysdata.setStrAssetGroupCode("Common");
                   if (this.checkStateId == 1) {
                      sysdata.setStrFunName("审核-利息算法设置");
                   }
                   else {
                      sysdata.setStrFunName("反审核-利息算法设置");
                   }
                   sysdata.setStrCode(this.strCIMCode);
                   sysdata.setStrName(this.strCIMName);
                   sysdata.setStrUpdateSql(strSql);
                   sysdata.setStrCreator(pub.getUserName());
                   sysdata.addSetting();
                }
//-----------------------

                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
             }
             catch(Exception e){
                throw new YssException("审核利息算法出错！", e);
             }
             finally{
                dbl.endTransFinal(bTrans);
             }
         */
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null && (!sRecycled.equalsIgnoreCase("")) ) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update Tb_Base_CalcInsMetic set FCheckState = " +
                        this.checkStateId + ", " +
                        "FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", " +
                        "FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FCIMCode = " +
                        dbl.sqlString(this.strCIMCode); //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而strCIMCode不为空，则按照strCIMCode来执行sql语句
            else if ( strCIMCode != null && (!strCIMCode.equalsIgnoreCase(""))) { //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_CalcInsMetic set FCheckState = " +
                    this.checkStateId + ", " +
                    "FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", " +
                    "FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FCIMCode = " + dbl.sqlString(this.strCIMCode); //更新数据的SQL语句
                dbl.executeSql(strSql); //执行更新操作
            }
            if (this.status.equalsIgnoreCase("1")) { //判断status是否等于1,当传入1的时候就记录系统的信息状态
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub); //设置pub的值
                sysdata.setStrAssetGroupCode("Common"); //设置StrAssetGroupCode的值
                if (this.checkStateId == 1) { //如果checkStateId==1就是它要的状态是审核状态
                    sysdata.setStrFunName("审核-利息算法设置"); //设置StrFunName的值
                } else {
                    sysdata.setStrFunName("反审核-利息算法设置");
                }
                sysdata.setStrCode(this.strCIMCode); //设置StrCode的值
                sysdata.setStrName(this.strCIMName); //设置StrName的值
                sysdata.setStrUpdateSql(strSql); //设置StrUpdateSql的值
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting(); //把这些以上数据添加到系统数据表Tb_Fun_SysData
            }
//-----------------------

            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核利息算法出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
        //----------------end

    }

    /**
     * 删除利息算法
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_CalcInsMetic set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FCIMCode = " + dbl.sqlString(this.strCIMCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-利息算法设置");
                sysdata.setStrCode(this.strCIMCode);
                sysdata.setStrName(this.strCIMName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除利息算法出错！", e);
        } finally {
            dbl.endTransFinal(bTrans);
        }
    }

    /**
     * 修改利息算法
     * @throws YssException
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_CalcInsMetic set " +
                "FCIMCode = " + dbl.sqlString(this.strCIMCode) +
                ", FCIMName = " + dbl.sqlString(this.strCIMName) +
                ", FCIMType = " + dbl.sqlString(this.strCIMType) +
                ", FFormula = " + dbl.sqlString(this.strFormula) +
                ", FSPICode = " + dbl.sqlString(this.strSPICode) +
                ", FDesc = " + dbl.sqlString(this.strDesc) +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FCIMCode = " + dbl.sqlString(this.strOldCIMCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-利息算法设置");
                sysdata.setStrCode(this.strCIMCode);
                sysdata.setStrName(this.strCIMName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("编辑利息算法出错！", e);
        } finally {
            dbl.endTransFinal(bTrans);
        }
        return null;
    }

    public String saveMliSetting(String sMutilRowStr) {
        return "";
    }

    /** shashijie  2012-2-3 STORY 1713 */
    public IDataSetting getSetting() throws YssException {
    	ResultSet rs = null;
    	try {
			String strSql = "select a.* from Tb_Base_CalcInsMetic a where a.FCIMCode = "+dbl.sqlString(this.strCIMCode);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				this.strCIMCode = rs.getString("FCIMCode") + "";//利息算法代码
                this.strCIMName = rs.getString("FCIMName") + "";//利息算法名称
                this.strCIMType = rs.getString("FCIMType") + "";//利息算法类型
                this.strFormula = rs.getString("FFormula") + "";//公式
                this.strSPICode = rs.getString("FSPICode") + "";//计息算法
                this.strDesc = rs.getString("FDesc") + "";//描述
                this.creatorName = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserName = rs.getString("FCheckUser") + "";
			}
		} catch (Exception e) {
			throw new YssException("获取利息公式出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
        return null;
    }

    public String getAllSetting() {
        return "";
    }

    public String getPartSetting() {
        return "";
    }

    /**
     * 获取利息算法数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = null;
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = null;
        String sVocStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, n.FVocName as FCIMTypeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, o.FSIName as FSIName" +
                " from Tb_Base_CalcInsMetic a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary n on a.FCIMType = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CAL_TYPE) +
                " left join (select FSICode,FSIName from TB_FUN_SPINGINVOKE) o on a.FSPICode = o.FSICode" + //sj add 20071207 前台没有显示Spring名
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.strCIMCode = rs.getString("FCIMCode") + "";
                this.strCIMName = rs.getString("FCIMName") + "";
                this.strCIMType = rs.getString("FCIMType") + "";
                this.strFormula = rs.getString("FFormula") + "";
                this.strSPICode = rs.getString("FSPICode") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.creatorName = rs.getString("FCreatorName") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserName = rs.getString("FCheckUserName") + "";
                this.strSPIName = rs.getString("FSIName") + "";
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_CAL_TYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取利息算法出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;

        try {
            sHeader = "利息算法代码\t利息算法名称";
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName " +
                " from Tb_Base_CalcInsMetic a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                buildFilterSql() + //sj edit 20071207 为了在弹出窗口时显示需要的类型的算法
                " and a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCIMCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCIMName") + "").trim()).append(
                    "\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.strCIMCode = rs.getString("FCIMCode") + "";
                this.strCIMName = rs.getString("FCIMName") + "";
                this.strCIMType = rs.getString("FCIMType") + "";
                this.strFormula = rs.getString("FFormula") + "";
                this.strSPICode = rs.getString("FSPICode") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.creatorName = rs.getString("FCreatorName") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserName = rs.getString("FCheckUserName") + "";
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
            throw new YssException("获取利息算法出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    public String getListViewData4() {
        return "";
    }

    public String getOperValue(String sType) {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String getStatus() {
        return status;
    }

    public String getStrSPIName() {
        return strSPIName;
    }

    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] delrn = null; //定义一个字符数组来循环删除
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        if(sRecycled == null)
        	return ;
        //---end---
        delrn = sRecycled.split("\t");
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
                        pub.yssGetTableName("Tb_Base_CalcInsMetic") +
                        " where FCIMCode = " +
                        dbl.sqlString(this.strCIMCode);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而strCIMCode不为空，则按照strCIMCode来执行sql语句
            else if (strCIMCode != "" && strCIMCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_CalcInsMetic") +
                    " where FCIMCode = " + dbl.sqlString(this.strCIMCode);

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
