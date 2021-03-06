package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.math.BigDecimal;

/**
 * <p>Title:BondInterestBean </p>
 * <p>Description:债券利息维护 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BondInterestBean
    extends BaseDataSettingBean implements IDataSetting {

    private String SecurityCode = ""; //证券代码
    private String SecurityName = ""; //证券名称
    private String RecordDate = ""; //权益登记日
    private String CurCpnDate = ""; //本次起息日
    private String NextCpnDate = ""; //下次起息日
    private BigDecimal IntAccPer100 = new BigDecimal("0"); //税前百元利息  蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
    //税后百元利息 add by songjie 国内: MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A 添加税后每百元债券利息字段,原先的百元利息字段默认为税前的百元利息
    private BigDecimal SHIntAccPer100 = new BigDecimal("0");
    private String dataSource;//数据来源 add by songjie 国内: MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A 添加数据来源字段
    private int IntDay; //已计提天数
    private String FaceValue = ""; //票面金额
    private String FaceRate = ""; //票面利率
    private String InsStart = ""; //计息起始日
    private String InsEnd = ""; //计息截止日
//    private String  = "0"; //在初始登陆时是否只显示列，不查询数据
    private String sRecycled = ""; //保存未解析前的字符串

    private String oldSecurityCode = "";
    private String oldRecordDate = "";

    private BondInterestBean filterType;

    //add by songjie 国内: MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A 添加数据来源字段和税后百元利息字段
    public void setDataSource(String dataSource){
        this.dataSource = dataSource;
    }

    public String getDataSource(){
        return dataSource;
    }

    public void setSHIntAccPer100(BigDecimal SHIntAccPer100){
        this.SHIntAccPer100 = SHIntAccPer100;
    }

    public BigDecimal getSHIntAccPer100(){
        return SHIntAccPer100;
    }
    //add by songjie 国内: MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A 添加数据来源字段和税后百元利息字段

    public void setIntDay(int IntDay) {
        this.IntDay = IntDay;
    }

    public int getIntDay() {
        return IntDay;
    }

    public void setIntAccPer100(BigDecimal IntAccPer100) {
        this.IntAccPer100 = IntAccPer100;
    }

    public BigDecimal getIntAccPer100() {
        return IntAccPer100;
    }

    public BondInterestBean() {
    }

    public void setSecurityCode(String securityCode) {
        this.SecurityCode = securityCode;
    }

    public String getSecurityCode() {
        return SecurityCode;
    }

    /**
     * parseRowStr
     * 解析债券利息数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.SecurityCode = reqAry[0];
            this.SecurityName = reqAry[1];
            this.RecordDate = reqAry[2];
            this.CurCpnDate = reqAry[3];
            this.NextCpnDate = reqAry[4];
            if (YssFun.isNumeric(reqAry[5])) {
                this.IntAccPer100 = new BigDecimal(reqAry[5]);//蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
            }
            if (YssFun.isNumeric(reqAry[6])) {
                this.IntDay = Integer.parseInt(reqAry[6]);
            }
            super.checkStateId = Integer.parseInt(reqAry[7]);
            this.oldSecurityCode = reqAry[8];
            this.oldRecordDate = reqAry[9];
            this.isOnlyColumns = reqAry[10];
            if (YssFun.isNumeric(reqAry[11])) {//税后百元利息，panjunfang add 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                this.SHIntAccPer100 = new BigDecimal(reqAry[11]);
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new BondInterestBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析债券利息数据信息出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.SecurityCode).append("\t");
        buf.append(this.SecurityName).append("\t");
        buf.append(this.RecordDate).append("\t");
        buf.append(this.CurCpnDate).append("\t");
        buf.append(this.NextCpnDate).append("\t");
        buf.append(this.IntAccPer100.toString()).append("\t");//蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
        buf.append(this.IntDay).append("\t");
        buf.append(this.FaceValue).append("\t");
        buf.append(this.FaceRate).append("\t");
        buf.append(this.InsStart).append("\t");
        buf.append(this.InsEnd).append("\t");
        buf.append(this.SHIntAccPer100.toString()).append("\t");//税后百元利息，panjunfang add 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * addOperData
     * 新增债券利息数据
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Data_BondInterest") +
                "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,FIntAccPer100,FSHIntAccPer100," + //增加字段：税后百元利息（FSHIntAccPer100），panjunfang add 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                " FIntDay,FCheckState,FCreator,FCreateTime,FCheckUser,FDataSource)" +
                " values(" + dbl.sqlString(this.SecurityCode) + "," +
                dbl.sqlDate(this.RecordDate) + "," +
                dbl.sqlDate(this.CurCpnDate) + "," +
                dbl.sqlDate(this.NextCpnDate) + "," +
                this.IntAccPer100.toString() + "," +//蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
                this.SHIntAccPer100.toString() + "," + //税后百元利息，panjunfang add 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                this.IntDay + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," +
                //edit by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 数据来源改为"HD-手工录入"
                "'HD'" +  //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 2009.07.03 蒋锦 添加 字段 来源标示，页面录入为手工 标示为 1
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增债券利息数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     * 修改债券利息数据
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "update " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " set FSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                ",FRecordDate = " + dbl.sqlDate(this.RecordDate) +
                ",FCurCpnDate = " + dbl.sqlDate(this.CurCpnDate) +
                ",FNextCpnDate = " + dbl.sqlDate(this.NextCpnDate) +
                ",FIntAccPer100 = " + this.IntAccPer100.toString() +//蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
                ",FSHIntAccPer100 = " + this.SHIntAccPer100.toString() + //税后百元利息，panjunfang add 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                ",FIntDay = " + this.IntDay +
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                //edit by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 数据来源改为 "HD-手工录入"
                ",FDataSource = 'HD'" + //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 2009.07.03 蒋锦 添加 字段 来源标示，页面录入为手工 标示为 1
                " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
                " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改债券利息数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_BondInterest"),
                               "FSecurityCode,FRecordDate",
                               this.SecurityCode + "," + this.RecordDate,
                               this.oldSecurityCode + "," + this.oldRecordDate);

    }

    /**
     * 修改时间：2008年3月27号
     * 修改人：单亮
     * 原方法功能：只能处理债券利息的审核和未审核的单条信息。
     * 新方法功能：可以处理债券利息审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理债券利息审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      Connection conn = dbl.loadConnection();
//      boolean bTrans = false; //代表是否开始了事务
//      String strSql = "";
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Data_BondInterest") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where  FSecurityCode = " + dbl.sqlString(this.SecurityCode) +
//               " and FRecordDate = " + dbl.sqlDate(this.RecordDate);
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//      catch (Exception e) {
//         throw new YssException("审核债券利息数据出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //---------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (  sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Data_BondInterest") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FSecurityCode = " +
                        dbl.sqlString(this.SecurityCode) +
                        " and FRecordDate = " + dbl.sqlDate(this.RecordDate);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而SecurityCode不为空，则按照SecurityCode来执行sql语句
            else if (SecurityCode != null&& (!SecurityCode.equalsIgnoreCase("")) ) {
                strSql = "update " + pub.yssGetTableName("Tb_Data_BondInterest") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where  FSecurityCode = " +
                    dbl.sqlString(this.SecurityCode) +
                    " and FRecordDate = " + dbl.sqlDate(this.RecordDate);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核债券利息数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //--------------end
    }

    /**
     * delOperData
     * 删除债券利息数据,即放入回收站
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_Bondinterest") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                " and FRecordDate = " + dbl.sqlDate(this.RecordDate);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除债券利息数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * buildFilterSql
     * 筛选条件
     * @throws YssException
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.isOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.SecurityCode.length() != 0) { // wdy 添加表别名a
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.SecurityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.RecordDate.length() != 0 &&
                !this.filterType.RecordDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FRecordDate = " +
                    dbl.sqlDate(filterType.RecordDate);
            }
            if (this.filterType.CurCpnDate.length() != 0 &&
                !this.filterType.CurCpnDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FCurCpnDate = " +
                    dbl.sqlDate(filterType.CurCpnDate);
            }
            if (this.filterType.NextCpnDate.length() != 0 &&
                !this.filterType.NextCpnDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FNextCpnDate = " +
                    dbl.sqlDate(filterType.NextCpnDate);
            }
            //蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
            if (this.filterType.IntAccPer100.compareTo(new BigDecimal("0")) > 0) {
                sResult = sResult + " and a.FIntAccPer100 = " +
                    filterType.IntAccPer100.toString();
            }
            //税后百元利息，panjunfang add 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            if(this.filterType.SHIntAccPer100.compareTo(new BigDecimal("0")) > 0) {
                sResult = sResult + " and a.FSHIntAccPer100 = " +
                    filterType.SHIntAccPer100.toString();
            }
            if (this.filterType.IntDay > 0) {
                sResult = sResult + " and a.FIntDay = " +
                    filterType.IntDay;
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取债券利息数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {// modified by ysh 20111025
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FSecurityName as FSecurityName" +
                ",d.FInsStartDate as FInsStartDate,d.FInsEndDate as FInsEndDate,d.FFaceValue as FFaceValue,d.FFaceRate as FFaceRate from " +
                pub.yssGetTableName("Tb_Data_BondInterest") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select o.FSecurityCode as FSecurityCode,o.FInsStartDate as FInsStartDate," +
                "o.FInsEndDate as FInsEndDate,o.FFaceValue as FFaceValue,o.FFaceRate as FFaceRate from " +
                pub.yssGetTableName("Tb_Para_FixInterest") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p " + " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
                " left join (select q.FSecurityCode as FSecurityCode,q.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " q join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) r " + " on q.FSecurityCode = r.FSecurityCode and q.FStartDate = r.FStartDate) e on a.FSecurityCode = e.FSecurityCode" +

                buildFilterSql() +
                " order by FCheckState, FCreateTime desc";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("BondInterest");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取债券利息数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
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
            //将登记日改为计息日 edit by songjie 2009-08-06,国内：MS00011 QDV4.1赢时胜（上海）2009年4月20日11_A
            sHeader = "证券品种\t计息日\t本次起息日\t下次起息日\t税前百元利息\t已计提天数";//更名百元利息为税前百元利息，panjunfang modify 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FSecurityName as FSecurityName" +
                ",d.FInsStartDate as FInsStartDate,d.FInsEndDate as FInsEndDate,d.FFaceValue as FFaceValue,d.FFaceRate as FFaceRate from " +
                pub.yssGetTableName("Tb_Data_BondInterest") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select o.FSecurityCode as FSecurityCode,o.FInsStartDate as FInsStartDate," +
                "o.FInsEndDate as FInsEndDate,o.FFaceValue as FFaceValue,o.FFaceRate as FFaceRate from " +
                pub.yssGetTableName("Tb_Para_FixInterest") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p " + " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
                " left join (select q.FSecurityCode as FSecurityCode,q.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " q join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) r " + " on q.FSecurityCode = r.FSecurityCode and q.FStartDate = r.FStartDate) e on a.FSecurityCode = e.FSecurityCode" +
                buildFilterSql() +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FRecordDate"))).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FCurCpnDate"))).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FNextCpnDate"))).
                    append(
                        "\t");
                bufShow.append(rs.getDouble("FIntAccPer100")).append("\t");
                bufShow.append(rs.getString("FIntDay")).append(YssCons.
                    YSS_LINESPLITMARK);

                setResultSetAttr(rs);

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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取债券利息数据出错!", e);
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
     * getOperData
     */
    public void getOperData() {
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
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliOperData(String sMutilRowStr) {
        return "";
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.SecurityCode = rs.getString("FSecurityCode") + "";
        this.SecurityName = rs.getString("FSecurityName") + "";
        this.RecordDate = rs.getDate("FRecordDate") + "";
        this.CurCpnDate = rs.getDate("FCurCpnDate") + "";
        this.NextCpnDate = rs.getDate("FNextCpnDate") + "";
        this.IntAccPer100 = rs.getBigDecimal("FIntAccPer100");//蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
        this.SHIntAccPer100 = rs.getBigDecimal("FSHIntAccPer100");//税后百元利息，panjunfang add 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
        this.IntDay = rs.getInt("FIntDay");
        this.FaceValue = rs.getDouble("FFaceValue") + "";
        this.FaceRate = rs.getDouble("FFaceRate") + "";
        this.InsStart = rs.getDate("FInsStartDate") + "";
        this.InsEnd = rs.getDate("FInsEndDate") + "";
        super.setRecLog(rs);

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        BondInterestBean befEditBean = new BondInterestBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FSecurityName as FSecurityName" +
                ",d.FInsStartDate as FInsStartDate,d.FInsEndDate as FInsEndDate,d.FFaceValue as FFaceValue,d.FFaceRate as FFaceRate from " +
                pub.yssGetTableName("Tb_Data_BondInterest") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select o.FSecurityCode as FSecurityCode,o.FInsStartDate as FInsStartDate," +
                "o.FInsEndDate as FInsEndDate,o.FFaceValue as FFaceValue,o.FFaceRate as FFaceRate from " +
                pub.yssGetTableName("Tb_Para_FixInterest") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p " + " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
                " left join (select q.FSecurityCode as FSecurityCode,q.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " q join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) r " + " on q.FSecurityCode = r.FSecurityCode and q.FStartDate = r.FStartDate) e on a.FSecurityCode = e.FSecurityCode" +
                " where  a.FSecurityCode =" + dbl.sqlString(this.oldSecurityCode) +
                " and   a.FRecordDate=" + dbl.sqlDate(this.oldRecordDate) +
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.SecurityCode = rs.getString("FSecurityCode") + "";
                befEditBean.SecurityName = rs.getString("FSecurityName") + "";
                befEditBean.RecordDate = rs.getDate("FRecordDate") + "";
                befEditBean.CurCpnDate = rs.getDate("FCurCpnDate") + "";
                befEditBean.NextCpnDate = rs.getDate("FNextCpnDate") + "";
                befEditBean.IntAccPer100 = rs.getBigDecimal("FIntAccPer100");//蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
                befEditBean.SHIntAccPer100 = rs.getBigDecimal("FSHIntAccPer100");//税后百元利息，panjunfang add 20090803,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                befEditBean.IntDay = rs.getInt("FIntDay");
                befEditBean.FaceValue = rs.getDouble("FFaceValue") + "";
                befEditBean.FaceRate = rs.getDouble("FFaceRate") + "";
                befEditBean.InsStart = rs.getDate("FInsStartDate") + "";
                befEditBean.InsEnd = rs.getDate("FInsEndDate") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
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
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009.07.03 蒋锦 添加 新增债券交易类型
     * 获取每百元债券利息
     * @param dDate Date：登记日期
     * @return HashMap
     */
	 //edit by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 添加 参数 secs
    public HashMap getAllSetting(java.util.Date dDate,String secs) throws YssException {
        HashMap hmResult = new HashMap();
        String strSql = "";
        ResultSet rs = null;
        BondInterestBean bondIns = null;
        try {
		    //edit by songjie 2010.01.26 
            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " WHERE FCheckState = 1" +
                " AND FRecordDate = " + dbl.sqlDate(dDate) + 
                //add by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 获取 接口导入 以及手工录入的数据
                " and FDataSource in('HD','IF') ";
            //--- add by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(secs != null && secs.trim().length() > 0){
            	strSql += " and FSecurityCode in (" + operSql.sqlCodes(secs) + ")";
            }
            //--- add by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bondIns = new BondInterestBean();
                bondIns.setIntAccPer100(rs.getBigDecimal("FIntAccPer100"));//税前每百元债券利息
                //add by songjie 2010.03.15 MS00896 QDV4赢时胜（上海）2010年02月05日01_B
                bondIns.setSHIntAccPer100(rs.getBigDecimal("FSHIntAccPer100"));//税后每百元债券利息
                bondIns.setSecurityCode(rs.getString("FSecurityCode"));
                bondIns.setRecordDate(YssFun.formatDate(rs.getDate("FRecordDate")));

                hmResult.put(bondIns.getSecurityCode(), bondIns);
            }
        } catch (Exception ex) {
            throw new YssException("获取每百元债券利息出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("tb_data_bondinterest") +
                " where FsecurityCode=" + dbl.sqlString(this.SecurityCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.IntAccPer100 = rs.getBigDecimal("FIntAccPer100");//蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 每百元债券利息改为高精度字段
                this.IntDay = rs.getInt("FIntDay");
            }
        } catch (Exception e) {
            throw new YssException();
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * 从回收站删除数据，即从数据库彻底删除数据
     * @throws YssException
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
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_Bondinterest") +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.SecurityCode) +
                        " and FRecordDate = " + dbl.sqlDate(this.RecordDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而SecurityCode不为空，则按照SecurityCode来执行sql语句
            else if (SecurityCode != "" && SecurityCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_Bondinterest") +
                    " where FSecurityCode = " +
                    dbl.sqlString(this.SecurityCode) +
                    " and FRecordDate = " + dbl.sqlDate(this.RecordDate);
                //执行sql语句
                dbl.executeSql(strSql);
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

    public String getRecordDate() {
        return RecordDate;
    }

    public void setRecordDate(String RecordDate) {
        this.RecordDate = RecordDate;
    }

    /**
     * add by songjie 2013.03.26
     * STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
     * 插入债券利息表
     * @param alZQCodes
     * @param alZQInfo
     * @param dDate
     * @throws YssException
     */
    public void insertIntoBondInterest(ArrayList alZQCodes,ArrayList alZQInfo, java.util.Date dDate) throws YssException {
        String strSql = "";//用于储存sql语句
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        PreparedStatement pstmt = null;//声明PreparedStatement
        BondInterestBean bondInterest = null;
        Iterator iterator = null;
        String zqdms = "";
        String zqdm = null;
   		int count = 1;
        try{
            iterator = alZQCodes.iterator();//获取迭代器

            while(iterator.hasNext()){
                zqdm = (String)iterator.next();//获取证券代码
                zqdms += zqdm + ",";//拼接证券代码
            }

            if(zqdms.length() >= 1){
                zqdms = zqdms.substring(0, zqdms.length() - 1);//去掉字符串最后逗号
            }

            con.setAutoCommit(false); //设置手动提交事务
            bTrans = true;

            //先在债券利息表中删除需要插入到债券利息表中的债券数据
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where FSecurityCode in(" + operSql.sqlCodes(zqdms) +
                ") and FRecordDate = " + dbl.sqlDate(dDate);

            dbl.executeSql(strSql);

            //添加数据到债券利息表中
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_BondInterest") +
                "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,FIntAccPer100," +
                "FIntDay,FSHIntAccPer100,FDataSource,FCheckState,FCreator,FCreateTime)" +
                "values(?,?,?,?,?,?,?,?,?,?,?)";

            pstmt=dbl.getPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788

            iterator = alZQInfo.iterator();
            while(iterator.hasNext()){
                bondInterest = (BondInterestBean)iterator.next();//获取债券利息实例

                pstmt.setString(1, bondInterest.getSecurityCode());//设置证券代码
                pstmt.setDate(2, YssFun.toSqlDate(dDate));//设置业务日期
                pstmt.setDate(3, YssFun.toSqlDate("9998-12-31"));
                pstmt.setDate(4, YssFun.toSqlDate("9998-12-31"));
                pstmt.setBigDecimal(5, bondInterest.getIntAccPer100());//设置税前百元利息
                pstmt.setInt(6, 0);
                pstmt.setBigDecimal(7, bondInterest.getSHIntAccPer100());//设置税后百元利息
                pstmt.setString(8, "ZD");//表示是系统计算而得到的百元债券利息
                pstmt.setInt(9, 1);
                pstmt.setString(10, pub.getUserCode()); //创建人、修改人
                pstmt.setString(11, YssFun.formatDatetime(new java.util.Date())); //创建、修改时间

                pstmt.addBatch();
              	if(count==500){
              		pstmt.executeBatch();
              		count = 1;
					continue;
              	}
              	
				count++;
            }

            pstmt.executeBatch();
            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }
        catch(Exception e){
            throw new YssException("将数据插入到债券利息表时出错！",e);
        }
        finally{
            dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(con, bTrans); //关闭连接
        }
    }
}
