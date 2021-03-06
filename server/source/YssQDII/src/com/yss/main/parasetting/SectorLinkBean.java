package com.yss.main.parasetting;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class SectorLinkBean
    extends BaseDataSettingBean implements IDataSetting {

    private String sectorCode = "";
    private String sectorClsCode = "";
    private String securityCode = "";
    private java.util.Date startDate;
    private SecurityBean sec;
    String[] allReqAry = null;
    String[] oneReqAry = null;
    public String getSectorClsCode() {
        return sectorClsCode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    public void setSectorClsCode(String sectorClsCode) {
        this.sectorClsCode = sectorClsCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getSectorCode() {
        return sectorCode;
    }

    public SectorLinkBean() {
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        String sSecurityStr = ""; //保存证券信息的筛选字符串
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\f") >= 0) {
                allReqAry = sRowStr.split("\r\f");
                this.sectorCode = allReqAry[0].split("\t")[0];
                this.sectorClsCode = allReqAry[0].split("\t")[1];
            } else {
                sTmpStr = sRowStr;
                reqAry = sTmpStr.split("\t");
                this.sectorCode = reqAry[0];
                this.sectorClsCode = reqAry[1];
                this.securityCode = reqAry[2];
                //    super.checkStateId = Integer.parseInt(reqAry[3]);
                super.parseRecLog();
            }
            if (sRowStr.indexOf("\r\r") >= 0) {
                sSecurityStr = sRowStr.substring(sRowStr.indexOf("\r\r") + 2);
                this.sec = new SecurityBean();
                this.sec.setYssPub(pub);
                this.sec.parseRowStr(sSecurityStr);
            }
        } catch (Exception e) {
            throw new YssException("解析板块链接信息出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.securityCode).append("\t");
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
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        /**add---huhuichao 2013-6-7 BUG  8029 55EM2版本行业板块错误 */
        strSql = "select y.* from " +
            "(select FSecurityCode,FCheckState,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") + " " +
            " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            "and FCheckState <> 2 and FSecurityCode in" +
            "(select FLinkCode  from " + pub.yssGetTableName("Tb_Para_SectorLink") +
            " where FSectorCode=" + dbl.sqlString(this.sectorCode) + "and FSecClsCode=" + dbl.sqlString(this.sectorClsCode) + 
            //--- delete by songjie 2014.02.18 BUG 89037 QDV4赢时胜(上海)2014年02月17日01_B start---//
            //" and FCheckState=1 " + //由于板块分类链接没有审核、反审核功能，所以不根据审核状态查询数据
            //--- delete by songjie 2014.02.18 BUG 89037 QDV4赢时胜(上海)2014年02月17日01_B end---//
            ") " +
            "group by FSecurityCode,FCheckState) x join" +
            " (select a.*, m.FVocName as FSettleDayTypeValue, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
            " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
            " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
            " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
            " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
            " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
            " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName," +
            " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Security") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
            " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) f on a.FExchangeCode = f.FExchangeCode" +
            " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode" +
            " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode" +
            " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode" +
            " left join (select FCuryCode,FCuryName as FCurrencyName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1) g on a.FTradeCury = g.FCuryCode" +
            " left join (select o.FSectorCode as FSectorCode,o.FSectorName as FSectorName from " +
            //delete by songjie 2011.03.14 不以最大的启用日期查询数据
//            pub.yssGetTableName("Tb_Para_Sector") + " o join " +
            pub.yssGetTableName("Tb_Para_Sector") + " o where FCheckState = 1" +
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//            "(select FSectorCode,max(FStartDate) as FStartDate from " +
//            pub.yssGetTableName("Tb_Para_Sector") + " " +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//            " and FCheckState = 1 group by FSectorCode) p " +
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
            //edit by songjie 2011.03.14 不以最大的启用日期查询数据
            " ) h on a.FSectorCode = h.FSectorCode" +
            //delete by songjie 2011.03.14 不以最大的启用日期查询数据
//            " on o.FSectorCode = p.FSectorCode and o.FStartDate = p.FStartDate) h on a.FSectorCode = h.FSectorCode" +
            " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
            " left join (select FCusCatCode,FCusCatName from " +
            pub.yssGetTableName("Tb_Para_CustomCategory") +
            " where FCheckState = 1) j on  a.FCusCatCode = j.FCusCatCode " +
            " left join (select FAffCorpCode,FAffCorpName from " +
            pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
            " where FCheckState = 1) k on a.FIssueCorpCode = k.FAffCorpCode " +
            " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FSettleDayType") + "= m.FVocCode and m.FVocTypeCode = " + // lzp  modify 20080123
            dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +
            ") y on x.FSecurityCode = y.FSecurityCode and x.FStartDate = y.FStartDate" +
            " order by y.FCatCode, y.FCheckState, y.FCreateTime desc";
        /**end---huhuichao 2013-6-7 BUG  8029 55EM2版本行业板块错误*/
        return this.builderListViewData(strSql);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append("\t").
                    append(YssCons.YSS_LINESPLITMARK);

                sec = setSecurityAttr(rs);
                bufAll.append(sec.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_SCY_SDAYTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取板块链接数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public SecurityBean setSecurityAttr(ResultSet rs) {
        sec = new SecurityBean();
        try {
            if (rs.getString("FSecurityCode") == null) {
                sec.setSecurityCode("");
            }
            sec.setSecurityCode(rs.getString("FSecurityCode") + "");
            if (rs.getString("FSecurityName") == null) {
                sec.setStrSectorName("");
            }
            sec.setStrSectorName(rs.getString("FSecurityName") + "");

            sec.setDtStartDate(rs.getDate("FStartDate"));

            if (rs.getString("FCatCode") == null) {
                sec.setStrCategoryCode("");
            }
            sec.setStrCategoryCode(rs.getString("FCatCode") + "");
            if (rs.getString("FCatName") == null) {
                sec.setStrCategoryName("");
            }
            sec.setStrCategoryName(rs.getString("FCatName") + "");

            if (rs.getString("FSubCatCode") == null) {
                sec.setStrSubCategoryCode("");
            }
            sec.setStrSubCategoryCode(rs.getString("FSubCatCode") + "");
            if (rs.getString("FSubCatName") == null) {
                sec.setStrSubCategoryName("");
            }
            sec.setStrSubCategoryName(rs.getString("FSubCatName") + "");

            if (rs.getString("FExchangeCode") == null) {
                sec.setStrExchangeCode("");
            }
            sec.setStrExchangeCode(rs.getString("FExchangeCode") + "");
            if (rs.getString("FExchangeName") == null) {
                sec.setStrExchangeName("");
            }
            sec.setStrExchangeName(rs.getString("FExchangeName") + "");

            if (rs.getString("FRegionCode") == null) {
                sec.setStrRegionCode("");
            }
            sec.setStrRegionCode(rs.getString("FRegionCode") + "");
            if (rs.getString("FRegionName") == null) {
                sec.setStrRegionName("");
            }
            sec.setStrRegionName(rs.getString("FRegionName") + "");

            if (rs.getString("FCountryCode") == null) {
                sec.setStrCountryCode("");
            }
            if (rs.getString("FCountryCode") == null) {
                sec.setStrCountryCode("");
            }
            sec.setStrCountryCode(rs.getString("FCountryCode") + "");
            if (rs.getString("FCountryName") == null) {
                sec.setStrCountryName("");
            }
            sec.setStrCountryName(rs.getString("FCountryName") + "");

            if (rs.getString("FAreaCode") == null) {
                sec.setStrAreaCode("");
            }
            sec.setStrAreaCode(rs.getString("FAreaCode") + "");
            if (rs.getString("FAreaName") == null) {
                sec.setStrAreaName("");
            }
            sec.setStrAreaName(rs.getString("FAreaName") + "");
            if (rs.getString("FMarketCode") == null) {
                sec.setStrMarketCode("");
            }
            sec.setStrMarketCode(rs.getString("FMarketCode") + "");
            if (rs.getString("FTradeCury") == null) {
                sec.setStrTradeCuryCode("");
            }
            sec.setStrTradeCuryCode(rs.getString("FTradeCury") + "");
            if (rs.getString("FCurrencyName") == null) {
                sec.setStrTradeCuryName("");
            }
            sec.setStrTradeCuryName(rs.getString("FCurrencyName") + "");

            if (rs.getString("FSectorCode") == null) {
                sec.setStrSectorCode("");
            }
            sec.setStrSectorCode(rs.getString("FSectorCode") + "");
            if (rs.getString("FSectorName") == null) {
                sec.setStrSectorName("");
            }
            sec.setStrSectorName(rs.getString("FSectorName") + "");

            if (rs.getString("FSettleDayType") == null) {
                sec.setStrSettleDayType("");
            }
            sec.setStrSettleDayType(rs.getString("FSettleDayType") + "");
            sec.setIntSettleDays(rs.getInt("FSettleDays"));

            sec.setDblFactor(rs.getDouble("FFactor"));

            //匹配数据库，改为高精度取数 sunkey 20090703 MS00525 QDV4赢时胜（上海）2009年6月21日01_B
            sec.setDblTotalShare(rs.getBigDecimal("FTotalShare"));

            sec.setDblCurrentShare(rs.getBigDecimal("FCurrentShare"));

            sec.setDblHandAmount(rs.getBigDecimal("FHandAmount"));
            //-------------------------------------------------------


            sec.setStrIssueCorpCode(rs.getString("FIssueCorpCode") + "");
            sec.setStrIssueCorpName(rs.getString("FIssueCorpName") + "");

            sec.setCusCatCode(rs.getString("FCusCatCode") + "");
            sec.setStrCusCateName(rs.getString("FCusCatName") + "");

            sec.setStrHolidaysCode(rs.getString("FHolidaysCode") + "");
            sec.setStrHolidaysName(rs.getString("FHolidaysName") + "");
            sec.setExternalCode(rs.getString("FExternalCode") + "");

            //   sec.setStrDesc(rs.getString("FDesc") + "");

            super.setRecLog(rs);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sec;
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
    public String getListViewData3() throws YssException {
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String sTmpStr = "";
        String strSql = "";
        boolean bTrans = false;
        int sLength = 0;
        ResultSet rs = null;
        String sql = "";
        Connection con = dbl.loadConnection();
        try {
            if (sMutilRowStr.indexOf("\r\f") >= 0) {
                allReqAry = sMutilRowStr.split("\r\f");
            }

            sql = "select * from " + pub.yssGetTableName("Tb_Para_SectorLink") +
                " where FSectorCode=" + dbl.sqlString(allReqAry[0].split("\t")[0]) +
                " and FSecClsCode=" + dbl.sqlString(allReqAry[0].split("\t")[1]);
            rs = dbl.openResultSet(sql);
            if (rs.next()) {
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_SectorLink") +
                    " where FSectorCode=" +
                    dbl.sqlString(allReqAry[0].split("\t")[0]) +
                    " and FSecClsCode=" +
                    dbl.sqlString(allReqAry[0].split("\t")[1]);
                con.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                con.commit();
                bTrans = false;
                con.setAutoCommit(true);
                dbl.endTransFinal(con, bTrans);
            }
            for (int i = 0; i < allReqAry.length; i++) {
                sTmpStr = allReqAry[i];
                oneReqAry = sTmpStr.split("\t");
                if (oneReqAry[2].equalsIgnoreCase("")) {
                    continue;
                }
                strSql = "insert into " + pub.yssGetTableName("Tb_Para_SectorLink") +
                    "(FSectorCode,FSecClsCode,FLinkCode,FStartDate,FCheckState,FCreator,FCreateTime) " +
                    "values(" + dbl.sqlString(oneReqAry[0]) + "," +
                    dbl.sqlString(oneReqAry[1]) + "," +
                    dbl.sqlString(oneReqAry[2]) + "," +
                    dbl.sqlDate("1900-01-01") + "," +//edit by songjie 2011.03.23 启用日期默认为1900-01-01
                    (pub.getSysCheckState() ? "1" : "0") + "," +//edit by huhuichao 2013.06.06 bug 8029 55EM2版本行业板块错误 
                    "'" + "admin" + "'" + "," +
                    dbl.sqlString("1900-01-01") + ")";//edit by songjie 2011.03.23 启用日期默认为1900-01-01
                con.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                con.commit();
                bTrans = false;
                con.setAutoCommit(true);
                dbl.endTransFinal(con, bTrans);

            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) {
         try{
              String strSql="";
              boolean bTrans = false;
              Connection con = dbl.loadConnection();
              if(btOper==YssCons.OP_DEL){
                 for(int i=0;i<allReqAry.length;i++)
                 {
                    oneReqAry=allReqAry[i].split("\t");
                    strSql = " update" + " " +
                          pub.yssGetTableName("Tb_Para_SectorLink") +
                          " set FCheckState= " + 2 +
                          " where FLinkCode= " + dbl.sqlString(oneReqAry[2]);
                    con.setAutoCommit(false);
                    bTrans = true;
                    dbl.executeSql(strSql);
                    con.commit();
                    bTrans = false;
                    con.setAutoCommit(true);
                    dbl.endTransFinal(con, bTrans);
                 }
             }

         }catch(Exception e){
            e.printStackTrace();
         }
      }*/

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
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
        try {
            String strSql = "";
            boolean bTrans = false;
            Connection con = dbl.loadConnection();

            for (int i = 0; i < allReqAry.length; i++) {
                oneReqAry = allReqAry[i].split("\t");
                strSql = " update" + " " +
                    pub.yssGetTableName("Tb_Para_SectorLink") +
                    " set FCheckState= " + 2 +
                    " where FLinkCode= " + dbl.sqlString(oneReqAry[2]);
                con.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                con.commit();
                bTrans = false;
                con.setAutoCommit(true);
                dbl.endTransFinal(con, bTrans);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
    public String getOperValue(String sType) throws YssException {
        String strReturn = "";
        try {
            if (sType.equalsIgnoreCase("getSecuritys")) {
                strReturn = this.getSecuritys();
            }
            return strReturn;
        } catch (Exception e) {
            throw new YssException(e.toString());
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
     */
    public void deleteRecycleData() {
    }

    public String getSecuritys() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "证券代码\t证券名称\t启用日期";
            strSql =
                "select a.*, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
                " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
                " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
                " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
                " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
                " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
                " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName from (SELECT * FROM " +
                pub.yssGetTableName("Tb_Para_Security") + " WHERE FCheckState = 1)" +
                " a " +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
                " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) f on a.FExchangeCode = f.FExchangeCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode" +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode" +
                " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) g on a.FTradeCury = g.FCuryCode" +
                " left join (select o.FSectorCode as FSectorCode,o.FSectorName as FSectorName from " +
                //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Sector") + " o where FCheckState = 1" +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                "(select FSectorCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Sector") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FSectorCode) p " +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                " ) h on a.FSectorCode = h.FSectorCode" +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " ) j on  a.FCusCatCode = j.FCusCatCode " +
                " left join (select FAffCorpCode,FAffCorpName from " +
                pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                " ) k on a.FIssueCorpCode = k.FAffCorpCode " +
                this.sec.buildFilterSql() +
                " AND a.FSecurityCode NOT IN(SELECT FLINKCODE " +
                " FROM " + pub.yssGetTableName("Tb_Para_Sectorlink") +
                " WHERE FSectorCode = " + dbl.sqlString(this.sectorCode) +
                " AND FSecclsCode <> " + dbl.sqlString(this.sectorClsCode) + ")" +
                " order by a.FCatCode, a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityCode") + "").trim()).
                    append("\t");
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).
                    append("\t");
                bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
                    YssCons.YSS_DATEFORMAT) + "").
                               trim()).append(YssCons.YSS_LINESPLITMARK);

                this.sec.setSecurityCode(rs.getString("FSecurityCode") + "");
                this.sec.setSecurityName(rs.getString("FSecurityName") + "");
                this.sec.setStartDate(rs.getDate("FStartDate"));
                this.sec.setCategoryCode(rs.getString("FCatCode") + "");
                this.sec.setStrCategoryName(rs.getString("FCatName") + "");
                this.sec.setSubCategoryCode(rs.getString("FSubCatCode") + "");
                this.sec.setSubCategoryName(rs.getString("FSubCatName") + "");
                this.sec.setExchangeCode(rs.getString("FExchangeCode") + "");
                this.sec.setStrExchangeName(rs.getString("FExchangeName") + "");
                this.sec.setStrRegionCode(rs.getString("FRegionCode") + "");
                this.sec.setStrRegionName(rs.getString("FRegionName") + "");
                this.sec.setStrCountryCode(rs.getString("FCountryCode") + "");
                this.sec.setStrCountryName(rs.getString("FCountryName") + "");
                this.sec.setStrAreaCode(rs.getString("FAreaCode") + "");
                this.sec.setStrAreaName(rs.getString("FAreaName") + "");
                this.sec.setStrMarketCode(rs.getString("FMarketCode") + "");
                this.sec.setStrTradeCuryCode(rs.getString("FTradeCury") + "");
                this.sec.setStrTradeCuryName(rs.getString("FCurrencyName") + "");
                this.sec.setStrSectorCode(rs.getString("FSectorCode") + "");
                this.sec.setStrSectorName(rs.getString("FSectorName") + "");
                this.sec.setStrSettleDayType(rs.getString("FSettleDayType") + "");
                this.sec.setIntSettleDays(rs.getInt("FSettleDays"));
                this.sec.setDblFactor(rs.getDouble("FFactor"));
                //匹配数据库，改为高精度取数 sunkey 20090703 MS00525 QDV4赢时胜（上海）2009年6月21日01_B
                this.sec.setDblTotalShare(rs.getBigDecimal("FTotalShare"));
                this.sec.setDblCurrentShare(rs.getBigDecimal("FCurrentShare"));
                this.sec.setDblHandAmount(rs.getBigDecimal("FHandAmount"));
                //=======================End MS00525=============================================
                this.sec.setStrIssueCorpCode(rs.getString("FIssueCorpCode") + "");
                this.sec.setStrIssueCorpName(rs.getString("FIssueCorpName") + "");
                this.sec.setStrCusCatCode(rs.getString("FCusCatCode") + "");
                this.sec.setStrCusCateName(rs.getString("FCusCatName") + "");
                this.sec.setStrHolidaysCode(rs.getString("FHolidaysCode") + "");
                this.sec.setStrHolidaysName(rs.getString("FHolidaysName") + "");
                this.sec.setStrExternalCode(rs.getString("FExternalCode") + "");
                this.sec.setStrDesc(rs.getString("FDesc") + "");
                this.sec.checkStateId = rs.getInt("FCheckState");
                this.sec.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.sec.creatorCode = rs.getString("FCreator") + "";
                this.sec.creatorTime = rs.getString("FcreateTime") + "";
                this.sec.checkUserCode = rs.getString("FCheckUser") + "";
                this.sec.checkTime = rs.getString("FCheckTime") + "";
                bufAll.append(this.sec.buildRowStr()).append(YssCons.
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
            throw new YssException("查询证券信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
