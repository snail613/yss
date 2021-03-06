package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: CountryBean </p>
 * <p>Description: 国家设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CountryBean
    extends BaseDataSettingBean implements IDataSetting {
    private String countryCode = ""; //国家代码
    private String countryName = ""; //国家名称
    private String regionCode = ""; //地域代码
    private String regionName = ""; //地域名称
    private String countryShortName = ""; //国家简称
    private String interDomain = ""; //国家域名缩写
    private String phoneCode = ""; //电话代码
    private String diffTime = ""; //时差
    private String desc = ""; //国家描述
    private String oldcountryCode = "";
    private String agreement = ""; //协议 sunkey 20081125 bugid:MS00035
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.14 方浩
    private CountryBean filterType;
    private String sMultiRowStr = ""; //存储多条请求信息的变量 每条信息以"\r\n"分隔 sunkey 20081209 BugNO:MS00069
    private String countryChineseName = "";//国家中文名称 add by yanghaiming 20091120 MS00807 QDV4赢时胜（北京）2009年11月12日01_A

    public CountryBean() {
    }

    /**
     * parseRowStr
     * 解析国家设置数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
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
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            //如果是多条记录，将数据保存到储存多条记录的变量中 sunkey 20081209 BugNO:MS00069
            if (sRowStr.indexOf("\r\n") > 0) {
                sMultiRowStr = sRowStr;
            }
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.13 方浩
            reqAry = sTmpStr.split("\t", -1); //添加-1,为了防止\t后面的字符串为""
            this.countryCode = reqAry[0];
            this.countryName = reqAry[1];
            this.regionCode = reqAry[2];
            this.countryShortName = reqAry[3];
            this.interDomain = reqAry[4];
            this.phoneCode = reqAry[5];
            this.diffTime = reqAry[6];
            this.desc = reqAry[7];
            super.checkStateId = Integer.parseInt(reqAry[8]);
            this.oldcountryCode = reqAry[9];
            //设置协议编号 sunkey 20081127 BugID:MS00035
            this.agreement = reqAry[10];
            //国家中文名称 add by yanghaiming 20091120 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
            this.countryChineseName = reqAry[11];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CountryBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析国家设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.countryCode).append("\t");
        buf.append(this.countryName).append("\t");
        buf.append(this.regionCode).append("\t");
        buf.append(this.regionName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.countryShortName).append("\t");
        buf.append(this.interDomain).append("\t");
        buf.append(this.phoneCode).append("\t");
        buf.append(this.diffTime).append("\t");
        //每次累加数据的时候 多添加一个字段值，用来获取协议
        //sunkey 20081126 bugid:MS00035
        buf.append(this.agreement).append("\t");
      //国家中文名称 add by yanghaiming 20091120 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
        buf.append(this.countryChineseName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.countryCode.length() != 0) {
                sResult = sResult + " and a.fcountrycode like '" +
                    filterType.countryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.countryName.length() != 0) {
                sResult = sResult + " and a.fcountryname like '" +
                    filterType.countryName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.regionCode.length() != 0) {
                sResult = sResult + " and a.fregioncode like '" +
                    filterType.regionCode.replaceAll("'", "''") + "%'";

            }
            if (this.filterType.regionName.length() != 0) {
                sResult = sResult + " and a.FCountryShortName like '" +
                    filterType.regionName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.countryShortName.length() != 0) {
                sResult = sResult + " and a.FCountryShortName like '" +
                    filterType.countryShortName.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.interDomain.length() != 0) {
                sResult = sResult + " and a.FInterDomain like '" +
                    filterType.interDomain.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.phoneCode.length() != 0) {
                sResult = sResult + " and a.FPhoneCode like '" +
                    filterType.phoneCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.diffTime.length() != 0) {
                sResult = sResult + " and a.FDiffTime like '" +
                    filterType.diffTime.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.fdesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取国家设置数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername,d.fregionname from Tb_Base_Country a" +
                " left join (select fusercode,fusername from Tb_Sys_UserList) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from Tb_Sys_UserList) c on a.fcheckuser = c.fusercode" +
                " left join (select fregioncode,fregionname from Tb_Base_Region where FCheckState = 1) d on a.fregioncode = d.fregioncode" +
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.countryCode = rs.getString("fcountrycode") + "";
                this.countryName = rs.getString("fcountryname") + "";
                this.countryShortName = rs.getString("FCountryShortName") + "";
                this.interDomain = rs.getString("FInterDomain") + "";
                this.phoneCode = rs.getString("FPhoneCode") + "";
                this.diffTime = rs.getString("FDiffTime") + "";
                this.regionCode = rs.getString("fregioncode") + "";
                this.regionName = rs.getString("fregionname") + "";
                this.desc = rs.getString("fdesc") + "";
                this.countryChineseName = rs.getString("FCOUNTRYCHINESENAME") + "";//添加国家中文名称 add by yanghaiming 20091120 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取国家信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     * 获取已审核的国家设置数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String strShowData = "", strAllData = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "国家代码\t国家名称\t地域名称";
            strSql = "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername,d.fregionname from Tb_Base_Country a" +
                " left join (select fusercode,fusername from Tb_Sys_UserList) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from Tb_Sys_UserList) c on a.fcheckuser = c.fusercode" +
                " left join (select fregioncode,fregionname from Tb_Base_Region where FCheckState = 1) d on a.fregioncode = d.fregioncode" +
                " where a.fcheckstate = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("fcountrycode") + "".trim()).append(
                    "\t");
                bufShow.append(rs.getString("fcountryname") + "".trim()).append(
                    "\t");
                bufShow.append(rs.getString("fregionname") + "".trim()).append(
                    YssCons.YSS_LINESPLITMARK);

                this.countryCode = rs.getString("fcountrycode") + "";
                this.countryName = rs.getString("fcountryname") + "";
                this.regionCode = rs.getString("fregioncode") + "";
                this.regionName = rs.getString("fregionname") + "";
                this.countryShortName = rs.getString("FCountryShortName") + "";
                this.interDomain = rs.getString("FInterDomain") + "";
                this.phoneCode = rs.getString("FPhoneCode") + "";
                this.diffTime = rs.getString("FDiffTime") + "";
                this.desc = rs.getString("fdesc") + "";
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                strShowData = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                strAllData = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + strShowData + "\r\f" + strAllData;

        } catch (Exception e) {
            throw new YssException("获取可用国家信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     * date   : 2008-11-25
     * author : sunkey
     * bugID  : MS00035
     * desc   : 获取协议
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String strShowData = "", strAllData = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "协议名称";
            //如果是根据国家编号查询--主要针对修改和复制的第一次加载
            if (!this.countryCode.trim().equals("")) {
                strSql = "SELECT A.FVOCCODE, A.FVOCNAME, B.FCOUNTRYCHINESENAME ";//添加国家中文名称 edit by yanghaiming 20091120 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
                strSql += "FROM Tb_Fun_Vocabulary A ";
                strSql += "JOIN TB_BASE_COUNTRY B ON " +
                    dbl.sqlInstr("B.FAGREEMENT", "A.FVOCCODE") + " > 0 ";
                strSql += "WHERE A.FVOCTYPECODE = " +
                    dbl.sqlString(YssCons.YSS_COUNTRY_AGREEMENT);
                strSql += " AND A.FCHECKSTATE=1 ";
                strSql += "AND B.FCOUNTRYCODE=" +
                    dbl.sqlString(this.countryCode);
            }
            //如果是查询全部--针对选择协议的时候
            else {
                strSql = "SELECT * ";
                strSql += "FROM TB_FUN_VOCABULARY ";
                strSql += "WHERE FCHECKSTATE = 1 ";
                strSql += "AND FVOCTYPECODE = " +
                    dbl.sqlString(YssCons.YSS_COUNTRY_AGREEMENT);
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //协议名称
                bufShow.append(rs.getString("FVOCNAME") +
                               "".trim()).append(YssCons.YSS_LINESPLITMARK);
                //协议编号
                bufAll.append(rs.getString("FVOCCODE") + "".trim()).append("\t");
                //协议名称
                bufAll.append(rs.getString("FVOCNAME") + "".trim()).append("\t");
                //关联代码
                bufAll.append("").append("\t");
                //描述
                bufAll.append("").append("\t");
                //审核状态
                bufAll.append(1).append("\t");
                //审核状态名称
                bufAll.append("").append("\t");
                //创建人编号
                bufAll.append("").append("\t");
                //创建人名称
                bufAll.append("").append("\t");
                //创建时间
                bufAll.append("").append("\t");
                //审核人代码
                bufAll.append("").append("\t");
                //审核人名称
                bufAll.append("").append("\t");
                //审核时间
                bufAll.append("").append(YssCons.YSS_LINESPLITMARK);

            }
            if (bufShow.toString().length() > 2) {
                strShowData = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                strAllData = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + strShowData + "\r\f" + strAllData;
        } catch (Exception e) {
            throw new YssException("获取可用国家信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getPartSetting
     *
     * @return String
     */
    public String getPartSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IBaseSetting
     */
    /*
      public IBaseSetting getSetting() {
         return null;
      }
     */
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Base_Country", "FCountryCode",
                               this.countryCode, this.oldcountryCode);
    }

    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*
       public void saveSetting(byte btOper) throws YssException {
          String strSql = "";
          boolean bTrans = false; //代表是否开始了事务
          Connection conn = dbl.loadConnection();
          try {
             if (btOper == YssCons.OP_ADD) {
                strSql = "insert into tb_base_country " +
                      "(fcountrycode,fcountryname,fregioncode,FCountryShortName,FInterDomain,FPhoneCode,FDiffTime" +
                      ",fdesc,fcheckstate,fcreator,fcreatetime,FCheckUser) " +
                      "values(" + dbl.sqlString(this.countryCode) + "," +
                      dbl.sqlString(this.countryName) + "," +
                      dbl.sqlString(this.regionCode) + "," +
                      dbl.sqlString(this.countryShortName) + "," +
                      dbl.sqlString(this.interDomain) + "," +
                      dbl.sqlString(this.phoneCode) + "," +
                      dbl.sqlString(this.diffTime) + "," +
                      dbl.sqlString(this.desc) + "," +
                      (pub.getSysCheckState() ? "0" : "1") + "," +
                      dbl.sqlString(this.creatorCode) + "," +
                      dbl.sqlString(this.creatorTime) + "," +
                      (pub.getSysCheckState() ? "' '" :
                       dbl.sqlString(this.creatorCode)) + ")";
             }
             else if (btOper == YssCons.OP_EDIT) {
                strSql = "update tb_base_country set fcountrycode = " +
                      dbl.sqlString(this.countryCode) + ", fcountryname = " +
                      dbl.sqlString(this.countryName) + ", fregioncode = " +
     dbl.sqlString(this.regionCode) + ",FCountryShortName = " +
     dbl.sqlString(this.countryShortName) + ",FInterDomain = " +
                      dbl.sqlString(this.interDomain) + ",FPhoneCode = " +
                      dbl.sqlString(this.phoneCode) + ",FDiffTime = " +
                      dbl.sqlString(this.diffTime) + ", fdesc = " +
                      dbl.sqlString(this.desc) + ", FCreator = " +
                      dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                      dbl.sqlString(this.creatorTime) +
                      " where fcountrycode = " +
                      dbl.sqlString(this.oldcountryCode);
             }
             else if (btOper == YssCons.OP_DEL) {
                //    strSql = "delete from tb_base_country where fcountrycode = " +
                //删除时将审核标志修改为2
                strSql = "update Tb_Base_Country set FCheckState = " +
                      this.checkStateId +
                      ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                      "' where fcountrycode = " +
                      dbl.sqlString(this.oldcountryCode);
             }
             else if (btOper == YssCons.OP_AUDIT) {
                strSql = "update Tb_Base_Country set FCheckState = " +
                      this.checkStateId + ",FCheckUser = " +
                      dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                      YssFun.formatDatetime(new java.util.Date()) +
                      "' where FcountryCode = " +
                      dbl.sqlString(this.countryCode);
             }
             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
          }
          catch (Exception e) {
             throw new YssException("更新国家设置信息出错", e);
          }
          finally {
             dbl.endTransFinal(conn, bTrans);
          }
       }
     */
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
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into tb_base_country " +
                "(fcountrycode,fcountryname,fregioncode,FCountryShortName,FInterDomain,FPhoneCode,FDiffTime" +
                ",fdesc,fcheckstate,fcreator,fcreatetime,FCheckUser,FAgreement,FCOUNTRYCHINESENAME) " +
                "values(" + dbl.sqlString(this.countryCode) + "," +
                dbl.sqlString(this.countryName) + "," +
                dbl.sqlString(this.regionCode) + "," +
                dbl.sqlString(this.countryShortName) + "," +
                dbl.sqlString(this.interDomain) + "," +
                dbl.sqlString(this.phoneCode) + "," +
                dbl.sqlString(this.diffTime) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                "," + dbl.sqlString(this.agreement) + "," + //添加协议的处理 sunkey 20081128 BugNo:MS00035
                dbl.sqlString(this.countryChineseName)+ ")"; //添加国家中文名称 add by yanghaiming 20091120 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新国家设置信息出错", e);
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
        /*    String strSql = "";
            boolean bTrans = true; //初始事物回滚
            Connection conn = dbl.loadConnection();
            String[] rows = sMultiRowStr.split("\r\n");
            Statement st = null;
            try {
              //考虑多条记录的处理  sunkey 20081208 BugNO:MS00069
              st = conn.createStatement();
              for (int i = 0; i < rows.length; i++) {
                //如果rows[i]为"" ，就不重新对请求数据进行解析了
                if (rows[i].trim().length() > 0) {
                  parseRowStr(rows[i]);
                }
                strSql = "update Tb_Base_Country set FCheckState = " +
                    this.checkStateId + ",FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FcountryCode = " +
                    dbl.sqlString(this.countryCode);
                st.addBatch(strSql);
              }
              conn.setAutoCommit(false); //禁止事物自动提交
              //执行更新
              st.executeBatch();
              bTrans = false; //设置为不回滚事物
            }
            catch (Exception e) {
              throw new YssException("更新国家设置信息出错", e);
            }
            finally {
              dbl.endTransFinal(conn, bTrans);
              dbl.closeStatementFinal(st);
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
            if (  sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update Tb_Base_Country set FCheckState = " +
                        this.checkStateId + ",FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FcountryCode = " +
                        dbl.sqlString(this.countryCode); //更新数据的SQL语句

                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而countryCode不为空，则按照countryCode来执行sql语句
            else if (countryCode != null &&(!countryCode.equalsIgnoreCase(""))) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_Country set FCheckState = " +
                    this.checkStateId + ",FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FcountryCode = " +
                    dbl.sqlString(this.countryCode); //更新数据的SQL语句

                dbl.executeSql(strSql); //执行更新操作
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新国家设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
//----------------end

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_Country set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where fcountrycode = " +
                dbl.sqlString(this.oldcountryCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新国家设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update tb_base_country set fcountrycode = " +
                dbl.sqlString(this.countryCode) + ", fcountryname = " +
                dbl.sqlString(this.countryName) + ", fregioncode = " +
                dbl.sqlString(this.regionCode) + ",FCountryShortName = " +
                dbl.sqlString(this.countryShortName) + ",FInterDomain = " +
                dbl.sqlString(this.interDomain) + ",FPhoneCode = " +
                dbl.sqlString(this.phoneCode) + ",FDiffTime = " +
                dbl.sqlString(this.diffTime) + ", fdesc = " +
                dbl.sqlString(this.desc) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ", FAgreement = " + //添加协议字段 sunkey 20081127 BugID:MS00035
                dbl.sqlString(this.agreement) + ", FCOUNTRYCHINESENAME = " +//添加国家中文名称 add by yanghaiming 20091120 MS00807 QDV4赢时胜（北京）2009年11月12日01_A
                dbl.sqlString(this.countryChineseName) + " where fcountrycode = " +
                dbl.sqlString(this.oldcountryCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新国家设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        CountryBean befEditBean = new CountryBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername,d.fregionname from Tb_Base_Country a" +
                " left join (select fusercode,fusername from Tb_Sys_UserList) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from Tb_Sys_UserList) c on a.fcheckuser = c.fusercode" +
                " left join (select fregioncode,fregionname from Tb_Base_Region where FCheckState = 1) d on a.fregioncode = d.fregioncode" +
                " where  a.fcountrycode =" +
                dbl.sqlString(this.oldcountryCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.countryCode = rs.getString("fcountrycode") + "";
                befEditBean.countryName = rs.getString("fcountryname") + "";
                befEditBean.countryShortName = rs.getString("FCountryShortName") +
                    "";
                befEditBean.interDomain = rs.getString("FInterDomain") + "";
                befEditBean.phoneCode = rs.getString("FPhoneCode") + "";
                befEditBean.diffTime = rs.getString("FDiffTime") + "";
                befEditBean.regionCode = rs.getString("fregioncode") + "";
                befEditBean.regionName = rs.getString("fregionname") + "";
                befEditBean.desc = rs.getString("fdesc") + "";
                befEditBean.countryChineseName = rs.getString("FCOUNTRYCHINESENAME") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /*
       /**
      * 清空回收站 彻底删除国家信息
      * author : sunkey
      * date   : 20081208
      * BugNO  : MS00069

            public void deleteRecycleData() throws YssException {
              //处理事物，以防删除过程中出现异常情况，造成数据的不完整性
              Connection con = dbl.loadConnection();
              Statement st = null;
              boolean btrans = true;
              try {
                con.setAutoCommit(false);
                st = con.createStatement();
                String[] rows = this.sMultiRowStr.split("\r\n");
                //清空回收站可能会选择多条记录，要循环处理,由于split的机制，循环至少要执行一次，所以不必考虑executeBatch为空
                for (int i = 0; i < rows.length; i++) {
                  //如果是仅删除一条记录，不需重复进行数据解析
                  if (rows[i].trim().length() > 0) {
                    parseRowStr(rows[i]); //解析每条数据
                  }
        st.addBatch("DELETE FROM TB_BASE_COUNTRY WHERE FCOUNTRYCODE='" +
                              this.countryCode + "'");
                }
                st.executeBatch();
                btrans = false;
              }
              catch (Exception ex) {
                throw new YssException("对不起，清空回收站时出现错误!", ex);
              }
              finally {
                dbl.endTransFinal(btrans);
                dbl.closeStatementFinal(st);
              }
            }
          }

      */
     /**
      * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
      * 修改人：方浩
      * 回收站的删除功能调用此方法deleteRecycleData()
      * 从数据库删除数据，即彻底删除数据
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
                         pub.yssGetTableName("Tb_Base_Country") +
                         " where FcountryCode = " +
                         dbl.sqlString(this.countryCode);
                     //执行sql语句
                     dbl.executeSql(strSql);
                 }
             }
             //sRecycled如果sRecycled为空，而countryCode不为空，则按照countryCode来执行sql语句
             else if (countryCode != "" && countryCode != null) {
                 strSql = "delete from " +
                     pub.yssGetTableName("Tb_Base_Country") +
                     " where FcountryCode = " +
                     dbl.sqlString(this.countryCode);

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
