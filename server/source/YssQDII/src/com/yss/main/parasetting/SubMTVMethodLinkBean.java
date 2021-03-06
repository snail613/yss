package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.sys.YssPageInationBean;
import com.yss.util.*;

/**
 * <p>Title: </p>
 * MS00008 add by 宋洁 2009-02-13
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SubMTVMethodLinkBean
    extends BaseDataSettingBean implements
    IDataSetting {
    private String mTVCode; //估值方法代码
    private String linkCode; //链接代码
    private java.util.Date startDate; //启用日期
    private String catCode = ""; //品种代码
    private String exchangeCode = ""; //交易所代码
    private String subCatCode = ""; //品种明细代码
    private String securityCode = ""; //证券代码
    private String cusCatCode = ""; //自定义子品种代码
    private String operType = ""; //判断为添加还是剔除操作 添加：add，剔除：delete
    private String isOnlyColumns = "0";
    String sInfoStr = "";
    private SubMTVMethodLinkBean filterType = null;

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setMTVCode(String mTVCode) {
        this.mTVCode = mTVCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public void setSubCatCode(String subCatCode) {
        this.subCatCode = subCatCode;
    }

    public void setCusCatCode(String cusCatCode) {
        this.cusCatCode = cusCatCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public void setIsOnlyColumns(String isOnlyColumns) {
        this.isOnlyColumns = isOnlyColumns;
    }

    public String getMTVCode() {
        return mTVCode;
    }

    public String getSubCatCode() {
        return subCatCode;
    }

    public String getCusCatCode() {
        return cusCatCode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getCatCode() {
        return catCode;
    }

    public String getIsOnlyColumns() {
        return isOnlyColumns;
    }

    public SubMTVMethodLinkBean() {
    }

    /**
     * 用于解析前台传过来的字符串信息
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
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
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            if (sRowStr.indexOf("\f\f") >= 0) {
                sInfoStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            this.mTVCode = reqAry[0];
            this.linkCode = reqAry[1];
            this.startDate = YssFun.toSqlDate(reqAry[2]);
            this.catCode = reqAry[3];
            this.subCatCode = reqAry[4];
            this.cusCatCode = reqAry[5];
            this.securityCode = reqAry[6];
            this.exchangeCode = reqAry[7];
            this.operType = reqAry[8];
            this.isOnlyColumns = reqAry[9];

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new SubMTVMethodLinkBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析字段信息出错！\n" + e.getMessage());
        }
    }

    /**
     * 将具体的估值方法链接信息组成一个字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.mTVCode);
        buf.append(this.linkCode);
        return buf.toString();
        //return this.autoBuildRowStr(this.getBuilderRowFields1());
    }

    /**
     * 用于保存估值方法链接信息到估值方法链接表中
     * @param sMutilRowStr String
     * @param bIsTrans boolean
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return saveMutliSetting(sMutilRowStr, false);
    }

    /**
     * 用于保存估值方法链接信息到估值方法链接表中
     * @param sMutilRowStr String
     * @param bIsTrans boolean
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr, boolean bIsTrans) throws
        YssException {
        String[] sMutilRowAry = null;
        String[] sFee = null;
        boolean bTrans = false;
        String subNum = "";
        String sqlSubNums = "";
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);

            strSql = "delete from " +
                pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                " and FLinkCode in(" + dbl.sqlString(this.linkCode) + ")"; //要解析有多少linkcode
            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            dbl.executeSql(strSql);

            strSql =
                "insert into " +
                pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                "(FMTVCode,FLinkCode,FStartDate,FCheckState, FCreator, FCreateTime,FCheckUser)" +
                " Values (?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (i > 0) {
                    this.parseRowStr(sMutilRowAry[i]);
                }
                if (this.linkCode.trim().length() > 0) {
                    pstmt.setString(1, this.mTVCode);
                    pstmt.setString(2, this.linkCode);
                    pstmt.setDate(3, YssFun.toSqlDate(this.startDate));
                    pstmt.setString(4, (pub.getSysCheckState() ? "0" : "1"));
                    pstmt.setString(5, this.creatorCode);
                    pstmt.setString(6, this.creatorTime);
                    pstmt.setString(7, pub.getSysCheckState() ? " " :
                                    this.creatorCode);
                    pstmt.executeUpdate();
                }
            }

            if (!bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存估值方法链接数据出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            if (!bIsTrans) {
                dbl.endTransFinal(conn, bTrans);
            }
        }
    }

    /**
     * 建筛选条件的sql语句
     * @return String
     */
    private String buildSecFilterSql() throws YssException {
        String sResult = " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 ";
        try {
            if (this.mTVCode.length() != 0) {
                sResult = sResult + " and FSecurityCode ";
                if (this.linkCode.equalsIgnoreCase("not")) {
                    sResult = sResult + " not ";
                }
                sResult = sResult + " in " +
                    "(select FLinkCode from " +
                    pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                    " and FStartDate = " + dbl.sqlDate(this.startDate) +
                    ") ";
            }
            if (this.catCode.length() != 0) {
                sResult = sResult + " and FCatCode = " +
                    dbl.sqlString(this.catCode);
            }
            if (this.subCatCode.length() != 0) {
                sResult = sResult + " and FSubCatCode = " +
                    dbl.sqlString(this.subCatCode);
            }
            if (this.cusCatCode.length() != 0) {
                sResult = sResult + " and FCusCatCode = " +
                    dbl.sqlString(this.cusCatCode);
            }
            if (this.securityCode.length() != 0) {
                sResult = sResult + " and FSecurityCode = " +
                    dbl.sqlString(this.securityCode);
            }
        } catch (Exception e) {
            throw new YssException("生成证券信息查询条件出错", e);
        }
        return sResult;
    }

    /**
     * 用于获取在剔除状态下复核筛选条件的临时表中的证券信息。
     * @return String
     * @throws YssException
     */
    public String getDeleteListViewData1() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                if (this.filterType.operType.equalsIgnoreCase("add")) {
                    sResult += (" AND NOT EXISTS (SELECT FLinkCode FROM "
                                + pub.yssGetTableName("Tb_TMP_MTVLink_") + pub.getUserCode() +
                                " WHERE FMtvCode = " + dbl.sqlString(this.mTVCode) +
                                " AND a.FSecurityCode = FLinkCode)");
                } else if (this.filterType.operType.equalsIgnoreCase("delete")) {
                    sResult += (" AND EXISTS (SELECT FLinkCode FROM "
                                + pub.yssGetTableName("Tb_TMP_MTVLink_") + pub.getUserCode() +
                                " WHERE FMtvCode = " + dbl.sqlString(this.mTVCode) +
                                " AND a.FSecurityCode = FLinkCode)");
                }
                if (this.isOnlyColumns.equalsIgnoreCase("1")) {
                    sResult = sResult + " and 1=2 ";
                    return sResult;
                }

                if (this.filterType.securityCode.length() > 0) {
                    sResult = sResult + "AND FSecurityCode = " +
                        dbl.sqlString(this.securityCode);
                }
                if (this.filterType.catCode.length() > 0) {
                    sResult = sResult + "AND FCatCode = " +
                        dbl.sqlString(this.catCode);
                }
                if (this.filterType.subCatCode.length() > 0) {
                    sResult = sResult + "AND FSubCatCode = " +
                        dbl.sqlString(this.subCatCode);
                }
                if (this.filterType.cusCatCode.length() > 0) {
                    sResult = sResult + "AND FCusCatCode = " +
                        dbl.sqlString(this.cusCatCode);
                }
                if (this.filterType.exchangeCode.length() > 0) {
                    sResult = sResult + "AND FExchangeCode = " +
                        dbl.sqlString(this.exchangeCode);
                }
                /*if(sResult.length() == 0){
                    sResult += " AND 1= 2";
                                 }*/
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return sResult;
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        boolean bTrans = false;
        boolean bIsSecurityPub = false;
        Connection conn = dbl.loadConnection();
        SecurityBean securityOper = (SecurityBean) pub.getParaSettingCtx().
            getBean("security");
        try {
            securityOper.setYssPub(pub);
            sHeader = securityOper.getListView1Headers();
//            bIsSecurityPub = Boolean.valueOf((String) pub.getHtPubParams().get(
//                    "security")).booleanValue(); //获取PUB中参数的值
            StringBuffer bufSql = new StringBuffer();
            bufSql.append(" SELECT sec.*,d.FCatName,e.FSubCatName,f.FExchangeName, f.FRegionCode,fa.FRegionName,fb.Fcountrycode,fb.FCountryName,");
            bufSql.append(
                "fc.FAreaCode,fc.FAreaName,g.FCurrencyName,h.FSectorName,i.FHolidaysName,j.FCusCatName,k.FAffCorpName as FIssueCorpName,m.FVocName as FSettleDayTypeValue,");
            bufSql.append("b.fusername as fcreatorname,c.fusername as fcheckusername,l.fsecclsname as FSYNTHETICNAME ");//板块分类名称add by yanghaiming 20100426 ");
            bufSql.append(" FROM (SELECT * from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_Security"));
            bufSql.append(" a WHERE FCheckState = 1 ");
            bufSql.append(buildFilterSql()).append(") sec ");
            bufSql.append(" left join (select FSecClsCode,FSecClsName from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_SectorClass"));
            bufSql.append(" where FCheckState = 1) l on sec.fsyntheticcode = l.FSecClsCode");//板块分类名称add by yanghaiming 20100426
            bufSql.append(" LEFT JOIN (select FUserCode, FUserName from Tb_Sys_UserList) b on sec.FCreator = ");
            bufSql.append(" b.FUserCode ");
            bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) c on sec.FCheckUser = ");
            bufSql.append(" c.FUserCode ");
            bufSql.append(" left join (select FCatCode, FCatName ");
            bufSql.append(" from Tb_Base_Category ");
            bufSql.append(
                " where FCheckState = 1) d on sec.FCatCode = d.FCatCode ");
            bufSql.append(" left join (select FSubCatCode, FSubCatName ");
            bufSql.append(" from Tb_Base_SubCategory ");
            bufSql.append(
                " where FCheckState = 1) e on sec.FSubCatCode = e.FSubCatCode ");
            bufSql.append(" left join (select FExchangeCode, ");
            bufSql.append(" FExchangeName, ");
            bufSql.append(" FRegionCode, ");
            bufSql.append(" FCountryCode, ");
            bufSql.append(" FAreaCode ");
            bufSql.append(" from Tb_Base_Exchange ");
            bufSql.append(
                " where FCheckState = 1) f on sec.FExchangeCode = f.FExchangeCode ");
            bufSql.append(" left join (select FRegionCode, FRegionName ");
            bufSql.append(" from Tb_Base_Region ");
            bufSql.append(
                " where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode ");
            bufSql.append(" left join (select FCountryCode, FCountryName ");
            bufSql.append(" from Tb_Base_Country ");
            bufSql.append(
                " where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode ");
            bufSql.append(" left join (select FAreaCode, FAreaName ");
            bufSql.append(" from Tb_Base_Area ");
            bufSql.append(
                " where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode ");
            bufSql.append(
                " left join (select FCuryCode, FCuryName as FCurrencyName ");
            bufSql.append(" from ").append(pub.yssGetTableName(
                "Tb_Para_Currency")).append(" ");
            bufSql.append(
                " where FCheckState = 1) g on sec.FTradeCury = g.FCuryCode ");
            bufSql.append(" left join (select FSectorCode as FSectorCode, ");
            bufSql.append(" FSectorName as FSectorName from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_Sector"));
            bufSql.append(" ) h on sec.FSectorCode = ");
            bufSql.append(" h.FSectorCode ");
            bufSql.append(" left join (select FHolidaysCode, FHolidaysName ");
            bufSql.append(" from Tb_Base_Holidays ");
            bufSql.append(
                " where FCheckState = 1) i on sec.FHolidaysCode = i.FHolidaysCode ");
            bufSql.append(" left join (select FCusCatCode, FCusCatName from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_CustomCategory"));
            bufSql.append(
                " where FCheckState = 1) j on sec.FCusCatCode = j.FCusCatCode ");
            bufSql.append(" left join (select FAffCorpCode, FAffCorpName from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_AffiliatedCorp"));
            bufSql.append(
                " where FCheckState = 1) k on sec.FIssueCorpCode = k.FAffCorpCode ");
            bufSql.append(
                " left join Tb_Fun_Vocabulary m on ").append(dbl.sqlToChar("sec.FSettleDayType")).append(" = m.FVocCode ");
            bufSql.append(" and m.FVocTypeCode = 'scy_sdaytype' ");
            strSql = bufSql.toString();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(securityOper.buildRowShowStr(rs,
                    securityOper.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                securityOper.setSecurityAttr(rs);
                bufAll.append(securityOper.buildRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("加载证券信息出错\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String[] allDatas = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String[] infos = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            allDatas = sInfoStr.split("\f\f");
            if (allDatas != null) {
                for (int i = 0; i < allDatas.length; i++) {
                    infos = allDatas[i].split("\t");
                    this.mTVCode = infos[0];
                    this.linkCode = infos[1];
                    this.startDate = YssFun.toDate(infos[2]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Tmp_MTVLink_")
                        + pub.getUserCode() + " where FLINKCODE = " + dbl.sqlString(this.linkCode);
                    dbl.executeSql(strSql);

                    strSql = "insert into " + pub.yssGetTableName("Tb_Tmp_MTVLink_") +
                        pub.getUserCode() +
                        "(FMTVCODE,FLINKCODE,FSTARTDATE,FCHECKSTATE,FCREATOR,FCREATETIME) values(" +
                        dbl.sqlString(this.mTVCode) + "," +
                        dbl.sqlString(this.linkCode) + "," +
                        dbl.sqlDate(this.startDate) + "," +
                        (pub.getSysCheckState() ? "0" : "1") + "," +
                        dbl.sqlString(this.creatorCode) + "," +
                        dbl.sqlString(this.creatorTime) + ")";

                    dbl.executeSql(strSql);
                }
                
                //---add by songjie 2011.10.11 BUG 2854 QDV4嘉实2011年09月26日02_B start---//
                //用户添加证券到估值方法设置链接中后，更新估值方法设置数据的创建人和创建时间
                if(allDatas.length > 0 && this.mTVCode.trim().length() > 0){
                	strSql = " update " + pub.yssGetTableName("Tb_Para_Mtvmethod") +
                			 " set FCreator = " + dbl.sqlString(this.creatorCode) + 
                			 " , FCREATETIME = " + dbl.sqlString(this.creatorTime) +
                			 " where FMTVCode = " + dbl.sqlString(this.mTVCode);
                	dbl.executeSql(strSql);
                }
                //---add by songjie 2011.10.11 BUG 2854 QDV4嘉实2011年09月26日02_B end---//	
                
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);

            }
        } catch (Exception e) {
            throw new YssException("增加估值方法链接信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        String[] allDatas = null;
        String[] infos = null;
        String linkcode = "";
        String mtvcode = "";
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            allDatas = sInfoStr.split("\f\f");
            if (allDatas != null) {
                for (int i = 0; i < allDatas.length; i++) {
                    infos = allDatas[i].split("\t");
                    mtvcode = infos[0];
                    //this.linkCode = bb[1];
                    if (i != allDatas.length - 1) {
                        linkcode += "'" + infos[1] + "'" + ",";
                    } else {
                        linkcode += "'" + infos[1] + "'";
                    }
                }
            }
            strSql = "delete from " +
                pub.yssGetTableName("Tb_Tmp_MTVLink_") + pub.getUserCode() +
                " where FLINKCODE in(" + linkcode +
                ")" + " and FMTVCode = " + dbl.sqlString(mtvcode);
            //执行sql语句
            dbl.executeSql(strSql);
            
            //---add by songjie 2011.10.11 BUG 2854 QDV4嘉实2011年09月26日02_B start---//
            //用户剔除证券到估值方法设置链接中后，更新估值方法设置数据的创建人和创建时间
            if(allDatas.length > 0 && mtvcode.trim().length() > 0){
            	strSql = " update " + pub.yssGetTableName("Tb_Para_Mtvmethod") +
            			 " set FCreator = " + dbl.sqlString(this.creatorCode) + 
            			 " , FCREATETIME = " + dbl.sqlString(this.creatorTime) +
            			 " where FMTVCode = " + dbl.sqlString(mtvcode);
            	dbl.executeSql(strSql);
            }
            //---add by songjie 2011.10.11 BUG 2854 QDV4嘉实2011年09月26日02_B end---//	
            
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

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    //modify by fangjiang 2010.11.18 bug 254
    public String getOperValue(String sType) throws YssException {
    	if(sType.split(":")[0].equalsIgnoreCase("search")){
    		int begin = Integer.parseInt(sType.split(":")[1]);
    		int end = Integer.parseInt(sType.split(":")[2]);
    		String sHeader = "";
            String sShowDataStr = "";
            String sAllDataStr = "";
            StringBuffer bufShow = new StringBuffer();
            StringBuffer bufAll = new StringBuffer();
            ResultSet rs = null;
            String strSql = "";
            boolean bTrans = false;
            boolean bIsSecurityPub = false;
            Connection conn = dbl.loadConnection();
            SecurityBean securityOper = (SecurityBean) pub.getParaSettingCtx().
                getBean("security");
            try {
                securityOper.setYssPub(pub);
                sHeader = securityOper.getListView1Headers();
                StringBuffer bufSql = new StringBuffer();
                bufSql.append(" SELECT sec.*,ass.FAssetGroupName,d.FCatName,e.FSubCatName,f.FExchangeName, f.FRegionCode,fa.FRegionName,fb.Fcountrycode,fb.FCountryName,");
                bufSql.append(
                    "fc.FAreaCode,fc.FAreaName,g.FCurrencyName,h.FSectorName,i.FHolidaysName,j.FCusCatName,k.FAffCorpName as FIssueCorpName,m.FVocName as FSettleDayTypeValue,");
                bufSql.append("b.fusername as fcreatorname,c.fusername as fcheckusername,l.fsecclsname as FSYNTHETICNAME ");//板块分类名称add by yanghaiming 20100426 ");
                bufSql.append(" FROM (SELECT * from ");
                bufSql.append(pub.yssGetTableName("Tb_Para_Security"));
                bufSql.append(" a WHERE FCheckState = 1 ");
                bufSql.append(buildFilterSql()).append(") sec ");
                bufSql.append(" left join (select FSecClsCode,FSecClsName from ");
                bufSql.append(pub.yssGetTableName("Tb_Para_SectorClass"));
                bufSql.append(" where FCheckState = 1) l on sec.fsyntheticcode = l.FSecClsCode");//板块分类名称add by yanghaiming 20100426
                bufSql.append(" left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on sec.FAssetGroupCode = ass.FAssetGroupCode ");	//added by liubo.Story #1770
                bufSql.append(" LEFT JOIN (select FUserCode, FUserName from Tb_Sys_UserList) b on sec.FCreator = ");
                bufSql.append(" b.FUserCode ");
                bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) c on sec.FCheckUser = ");
                bufSql.append(" c.FUserCode ");
                bufSql.append(" left join (select FCatCode, FCatName ");
                bufSql.append(" from Tb_Base_Category ");
                bufSql.append(
                    " where FCheckState = 1) d on sec.FCatCode = d.FCatCode ");
                bufSql.append(" left join (select FSubCatCode, FSubCatName ");
                bufSql.append(" from Tb_Base_SubCategory ");
                bufSql.append(
                    " where FCheckState = 1) e on sec.FSubCatCode = e.FSubCatCode ");
                bufSql.append(" left join (select FExchangeCode, ");
                bufSql.append(" FExchangeName, ");
                bufSql.append(" FRegionCode, ");
                bufSql.append(" FCountryCode, ");
                bufSql.append(" FAreaCode ");
                bufSql.append(" from Tb_Base_Exchange ");
                bufSql.append(
                    " where FCheckState = 1) f on sec.FExchangeCode = f.FExchangeCode ");
                bufSql.append(" left join (select FRegionCode, FRegionName ");
                bufSql.append(" from Tb_Base_Region ");
                bufSql.append(
                    " where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode ");
                bufSql.append(" left join (select FCountryCode, FCountryName ");
                bufSql.append(" from Tb_Base_Country ");
                bufSql.append(
                    " where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode ");
                bufSql.append(" left join (select FAreaCode, FAreaName ");
                bufSql.append(" from Tb_Base_Area ");
                bufSql.append(
                    " where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode ");
                bufSql.append(
                    " left join (select FCuryCode, FCuryName as FCurrencyName ");
                bufSql.append(" from ").append(pub.yssGetTableName(
                    "Tb_Para_Currency")).append(" ");
                bufSql.append(
                    " where FCheckState = 1) g on sec.FTradeCury = g.FCuryCode ");
                bufSql.append(" left join (select FSectorCode as FSectorCode, ");
                bufSql.append(" FSectorName as FSectorName from ");
                bufSql.append(pub.yssGetTableName("Tb_Para_Sector"));
                bufSql.append(" ) h on sec.FSectorCode = ");
                bufSql.append(" h.FSectorCode ");
                bufSql.append(" left join (select FHolidaysCode, FHolidaysName ");
                bufSql.append(" from Tb_Base_Holidays ");
                bufSql.append(
                    " where FCheckState = 1) i on sec.FHolidaysCode = i.FHolidaysCode ");
                bufSql.append(" left join (select FCusCatCode, FCusCatName from ");
                bufSql.append(pub.yssGetTableName("Tb_Para_CustomCategory"));
                bufSql.append(
                    " where FCheckState = 1) j on sec.FCusCatCode = j.FCusCatCode ");
                bufSql.append(" left join (select FAffCorpCode, FAffCorpName from ");
                bufSql.append(pub.yssGetTableName("Tb_Para_AffiliatedCorp"));
                bufSql.append(
                    " where FCheckState = 1) k on sec.FIssueCorpCode = k.FAffCorpCode ");
                bufSql.append(
                    " left join Tb_Fun_Vocabulary m on ").append(dbl.sqlToChar("sec.FSettleDayType")).append(" = m.FVocCode ");
                bufSql.append(" and m.FVocTypeCode = 'scy_sdaytype' ");
                strSql = bufSql.toString();   
                YssPageInationBean yssPageInationBean = new YssPageInationBean();
                yssPageInationBean.setYssPub(pub);
                yssPageInationBean.setsQuerySQL(strSql);
                yssPageInationBean.setbCreateView(true);
        		yssPageInationBean.setsTableName("mtv_security");
        		rs = dbl.openResultSet_PageInation(yssPageInationBean,begin,end);
                while (rs.next()) {
                	//modified by liubo.Story #1770
                	//用tmpAssetGroupName变量保存组合群名称，然后使用基类重载的buildRowShowStr方法将组合群名称插入ListView的数据中
    				//================================
                	String tmpAssetGroupName = this.getGroupNameFromGroupCode(rs.getString("FASSETGROUPCODE"));
                    bufShow.append(securityOper.buildRowShowStr(rs,
                    		securityOper.getListView1ShowCols(),tmpAssetGroupName)).
                        append(YssCons.YSS_LINESPLITMARK);
    				//==============end==================

                    securityOper.setSecurityAttr(rs);
                    bufAll.append(securityOper.buildRowStr()).
                        append(YssCons.YSS_LINESPLITMARK);
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
                throw new YssException("加载证券信息出错\n", e);
            } finally {
                dbl.closeResultSetFinal(rs);
                dbl.endTransFinal(conn, bTrans);
            }
    	}
        return "";
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
     */
    public void deleteRecycleData() {
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    public void saveSetting(byte btOper) {
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
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
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
	/**
	 * added by liubo. #story 1770.20111123
	 * 通过此方法，查询出类似“001,002”以逗号分隔开的组合群代号所代表的组合群代码，同样以逗号分隔开
	 * FAssetGroupCode 组合群代码
	 * return String
	 * @throws YssException 
	 */
	
	private String getGroupNameFromGroupCode(String FAssetGroupCode) throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		String[] groupCode = null;
		String requestGroupCode = "";
		try
		{
//			groupCode = ("".equals(FAssetGroupCode.trim()) ? pub.getAssetGroupCode() : FAssetGroupCode).split(",");
			groupCode = FAssetGroupCode.split(",");
			for (int i = 0;i<groupCode.length;i++)
			{
				requestGroupCode = requestGroupCode +"'" + groupCode[i] + "',";
			}
			
			strSql = "select * from tb_sys_AssetGroup where FAssetGroupCode in (" + requestGroupCode.substring(0,requestGroupCode.length() - 1) + ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				sReturn = sReturn + rs.getString("FAssetGroupName") + ",";
			}
			
			return sReturn;
		
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
	}
}
