package com.yss.main.parasetting;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.sys.YssPageInationBean;
import com.yss.util.*;

public class MTVMethodLinkBean
    extends BaseDataSettingBean implements IDataSetting {
    private String mTVCode; //估值方法代码
    private String linkCode; //链接代码
    private java.util.Date startDate; //启用日期
    private String exchangeCode = ""; //交易所代码
    private String catCode = ""; //品种代码
    private String subCatCode = ""; //品种明细代码
    private String securityCode = ""; //证券代码
    private String cusCatCode = ""; //自定义子品种代码
    private String sectorCode = ""; //行业板块代码
    private String isOnlyColumns = "0";
    private MTVMethodLinkBean filterType = null;
    public HashSet MTVMethodLinkCode = new HashSet(); //MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009.04.03

    public String getLinkCode() {
        return linkCode;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
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

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
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

    public String getSectorCode() {
        return sectorCode;
    }

    public String getIsOnlyColumns() {
        return isOnlyColumns;
    }

    public String getExchangeCode() {

        return exchangeCode;
    }

    public MTVMethodLinkBean() {
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        this.autoParseRowStr(sRowStr);
        super.parseRecLog();
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        return this.autoBuildRowStr(this.getBuilderRowFields1());
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
     * MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009.04.03
     * 用于获取所有估值方法链接代码
     * @return HashSet
     */
    public HashSet getAllMTVMethodLinkCode(String mtvCode) throws YssException {
        String strSql = ""; //声明字符串用于储存sql语句
        ResultSet rs = null; //声明结果集

        strSql = " select FLinkcode from " + pub.yssGetTableName("tb_para_mtvmethodlink");

        try {
            if (mtvCode != null && mtvCode.length() > 0) { //若估值方法代码不为空且有内容
                strSql += " where FMTVCode= " + dbl.sqlString(mtvCode.trim()) +
                    " and FCheckState = '1'";
            }

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                MTVMethodLinkCode.add(rs.getString("flinkcode")); //添加估值方法链接代码及估值方法对应的证券代码到MTVMethodLinkCode
            }
        } catch (Exception e) {
            throw new YssException("获取估值方法对应的所有估值方法链接代码出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return MTVMethodLinkCode;
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return saveMutliSetting(sMutilRowStr, false);
    }

    /**
     * @param sMutilRowStr String
     * @param bIsTrans boolean
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr, boolean bIsTrans) throws YssException {
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

            strSql = "delete from " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                " and FStartDate = " + dbl.sqlDate(this.startDate);
            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
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
     * by caocheng 2009.02.21 MS00008
     * 将估值方法链接表的数据复制到一张临时表中
     * @throws YssException
     */

    public void createMTVtmpTab() throws YssException {
        String strsql = "Create table " + pub.yssGetTableName("tb_tmp_mtvlink_") + pub.getUserCode() + " as (";
        String sql = "select * from " +
            pub.yssGetTableName("Tb_Para_MTVMethodLink") + " where FMTVcode= " +
            dbl.sqlString(this.mTVCode) + ")"; //根据用户登陆代码创建临时表,并将估值方法链接表数据复制到临时表

        try {
            if (dbl.yssTableExist(pub.yssGetTableName("tb_tmp_mtvlink_") + pub.getUserCode())) { //如果临时表存在,删除
            	/**shashijie 2011-10-20 STORY 1698 */
                dbl.executeSql(dbl.doOperSqlDrop("Drop table " + pub.yssGetTableName("tb_tmp_mtvlink_") + 
                		pub.getUserCode()));
                /**end*/
            }
            if (dbl.getDBType() == YssCons.DB_ORA) { //判断数据库类型
                dbl.executeSql(strsql + sql);
            } else if (dbl.getDBType() == YssCons.DB_DB2) { //判断数据库类型
                dbl.executeSql(strsql + "(" + sql + ") definition only");
                dbl.executeSql("insert into " +
                               pub.yssGetTableName("tb_tmp_mtvlink_") +
                               pub.getUserCode() + "(" + sql);
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
        } catch (SQLException ex) {
            throw new YssException("展开估值方法链接出现错误!", ex);
        }
    }

    /**
     * by caocheng 2009.02.23 MS00008
     * 将临时表的数据插入估值方法链接表,并删除临时表
     * @throws YssException
     */
    public void saveAndDelMTVtmp() throws YssException {
        boolean bTrans = false;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();

        String delsql = "delete from  " +
            pub.yssGetTableName("Tb_Para_MTVMethodLink")
            + " where fmtvcode=" + dbl.sqlString(this.mTVCode); //根据前台传来的估值方法链接代码,删除估值方法链接表中的数据

        String insertsql = "insert into  " +
            pub.yssGetTableName("Tb_Para_MTVMethodLink") +
            "(fmtvcode,flinkcode,fstartdate,fcheckstate,fcreator,fcreatetime,fcheckuser,fchecktime)" +
            " select * from " +
            pub.yssGetTableName("tb_tmp_mtvlink_") + pub.getUserCode(); //临时表中的数据插入估值方法链接表
        /**shashijie 2011-10-20 STORY 1698 */
        String dropsql = dbl.doOperSqlDrop("drop table " + pub.yssGetTableName("tb_tmp_mtvlink_") +
            pub.getUserCode()); //删除临时表
        /**end*/
        try {
            if (!bTrans) {
                conn.setAutoCommit(false); //屏蔽自动提交
                bTrans = true;
            }
            dbl.executeSql(delsql);

            pstmt = conn.prepareStatement(insertsql);
            pstmt.executeUpdate();

            dbl.executeSql(dropsql);
            if (bTrans) {
                conn.commit(); //执行sql语句
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new YssException("往估值方法链接表中导入数据时出错", ex);
        } finally {
            dbl.closeStatementFinal(pstmt); //关闭statement
            if (!bTrans) {
                dbl.endTransFinal(conn, bTrans); //关闭connection
            }
        }
    }

    /**
     * by caocheng 2009.02.23 MS00008
     * 查询临时表中的数据表
     */
    public String searchMTVtmp() throws YssException {
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
            bufSql.append(" SELECT sec.*,d.FCatName,e.FSubCatName,f.FExchangeName, f.FRegionCode,fa.FRegionName,fb.Fcountrycode,fb.FCountryName,");
            bufSql.append("fc.FAreaCode,fc.FAreaName,g.FCurrencyName,h.FSectorName,i.FHolidaysName,j.FCusCatName,k.FAffCorpName as FIssueCorpName,m.FVocName as FSettleDayTypeValue,");
            bufSql.append("b.fusername as fcreatorname,c.fusername as fcheckusername,l.fsecclsname as FSYNTHETICNAME ");//板块分类名称add by yanghaiming 20100426
            bufSql.append(" FROM (SELECT * from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_Security"));
            
            //edited by zhouxiang MS01550 估值方法设置展开后，不能按照选择的“交易所”条件筛选证券信息 
			bufSql.append(" a WHERE FCheckState = 1");
			if (this.exchangeCode.length() > 1)
				bufSql.append("and  a.FExchangeCode= ").append(
						dbl.sqlString(this.exchangeCode));
			//end--  by zhouxiang MS01550 估值方法设置展开后，不能按照选择的“交易所”条件筛选证券信息 
            
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
            bufSql.append(" p ) h on sec.FSectorCode = h.FSectorCode ");
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

    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            sResult += (" AND EXISTS (SELECT FLinkCode from "
                        + pub.yssGetTableName("Tb_TMP_MTVLink_") + pub.getUserCode() +
                        " WHERE FMtvCode = " + dbl.sqlString(this.mTVCode) +
                        " AND a.FSecurityCode = FLinkCode)");

            if (this.securityCode != null && this.securityCode.trim().length() > 0) {
                sResult = sResult + "AND FSecurityCode = " +
                    dbl.sqlString(this.securityCode);
            }
            if (this.catCode != null && this.catCode.trim().length() > 0) {
                sResult = sResult + "AND FCatCode = " +
                    dbl.sqlString(this.catCode);
            }
            if (this.subCatCode != null && this.subCatCode.trim().length() > 0) {
                sResult = sResult + "AND FSubCatCode = " +
                    dbl.sqlString(this.subCatCode);
            }
            if (this.cusCatCode != null && this.cusCatCode.trim().length() > 0) {
                sResult = sResult + "AND FCusCatCode = " +
                    dbl.sqlString(this.cusCatCode);
            }
            if (this.exchangeCode != null && this.exchangeCode.trim().length() > 0) {
                sResult = sResult + "AND FExchangeCode = " +
                    dbl.sqlString(this.exchangeCode);
            }
            if (sResult.length() == 0) {
                sResult += " AND 1= 2";
            }

        } catch (Exception e) {
            throw new YssException("前台参数获取出错!", e);
        }
        return sResult;
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

    /**
     * 筛选条件
     * @return String
     */
    private String buildSecFilterSql() throws YssException {
        String sResult = " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 ";
        try {
            if (this.mTVCode.length() != 0) {
                sResult = sResult + " and FSecurityCode ";
                if (this.linkCode.equalsIgnoreCase("not")) {
                    sResult = sResult + " not ";
                }
                sResult = sResult + " in " +
                    "(select FLinkCode from " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                    " and FStartDate = " + dbl.sqlDate(this.startDate) + ") ";
            }
            if (this.catCode.length() != 0) {
                sResult = sResult + " and FCatCode = " +
                    dbl.sqlString(this.catCode);
            }
            if (this.subCatCode.length() != 0) {
                sResult = sResult + " and FSubCatCode = " +
                    dbl.sqlString(this.subCatCode);
            }
            if (this.sectorCode.length() != 0) {
                sResult = sResult + " and FSectorCode = " +
                    dbl.sqlString(this.sectorCode);
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
        boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
        Connection conn = dbl.loadConnection();
        SecurityBean securityOper = (SecurityBean) pub.getParaSettingCtx().
            getBean("security");
        try {
            securityOper.setYssPub(pub);
            sHeader = securityOper.getListView1Headers();
            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get("security")).booleanValue(); //获取PUB中参数的值 MS00131
            if (isOnlyColumns.equalsIgnoreCase("1")) {
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            }

            conn.setAutoCommit(false);
            bTrans = true;

            if (dbl.yssTableExist("Tb_Temp_SecurityBak_" + pub.getUserCode())) {
            	/**shashijie 2011-10-20 STORY 1698 */
                strSql = dbl.doOperSqlDrop("drop table Tb_Temp_SecurityBak_" + pub.getUserCode());
                /**end*/
                dbl.executeSql(strSql);
            }
            //-------------2007.12.06 修改 蒋锦 考虑 DB2 数据库建表与 Oracle 的不同--------------//
            strSql = "CREATE TABLE Tb_Temp_SecurityBak_" + pub.getUserCode() + " AS ";
            String strSelect = " select b.* from (select FSecurityCode,FCheckState as FChkState,max(FStartDate) " +
//                " as FStartDate from " +
                " as FStartDate" + (bIsSecurityPub ? ",FASSETGROUPCODE" : " ") + " from " + //若采用新表，则还要加上FASSETGROUPCODE主键字段 MS00131
                (bIsSecurityPub ? "Tb_Base_Security" : pub.yssGetTableName("Tb_Para_Security")) + //根据参数取具体的表 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
                buildSecFilterSql() +
                (bIsSecurityPub ? (" and (FASSETGROUPCODE=' ' or FASSETGROUPCODE=" + dbl.sqlString(pub.getAssetGroupCode()) + ")") : "") + //添加上查询条件 MS00131
//                " group by FSecurityCode,FCheckState) a " +
                " group by FSecurityCode,FCheckState" + (bIsSecurityPub ? ",FASSETGROUPCODE" : " ") + ") a " + //若采用新表，必须用这个FASSETGROUPCODE这个字段分组 MS00131
                " join (select * from " +
                (bIsSecurityPub ? ("Tb_Base_Security where FASSETGROUPCODE=' ' or FASSETGROUPCODE=" + dbl.sqlString(pub.getAssetGroupCode())) : pub.yssGetTableName("Tb_Para_Security")) + ") b " + //根据参数取具体的表 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
//                " on a.FSecurityCode = b.FSecurityCode and a.FStartDate = b.FStartDate ";
                " on a.FSecurityCode = b.FSecurityCode and a.FStartDate = b.FStartDate " + (bIsSecurityPub ? " and a.FASSETGROUPCODE=b.FASSETGROUPCODE " : " "); //若采用新表，还要加上FASSETGROUPCODE这个字段 MS00131
            if (dbl.getDBType() == YssCons.DB_ORA) {
                dbl.executeSql(strSql + strSelect);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                dbl.executeSql(strSql + "(" + strSelect + ") definition only");
                dbl.executeSql("insert into Tb_Temp_SecurityBak_" + pub.getUserCode() + "(" + strSelect + ")");
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            //------------------------------------------------------------------------------//

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            strSql =
                "select a.*, m.FVocName as FSettleDayTypeValue, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
                " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
                " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
                " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
                " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
                " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
                " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName,l.fsecclsname as FSYNTHETICNAME from " +//板块分类名称add by yanghaiming 20100426 
                "Tb_Temp_SecurityBak_" + pub.getUserCode() + " a " +
                " left join (select FSecClsCode,FSecClsName from " + pub.yssGetTableName("Tb_Para_SectorClass") + " where FCheckState = 1) l on a.fsyntheticcode = l.FSecClsCode" +//板块分类名称add by yanghaiming 20100426
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
                pub.yssGetTableName("Tb_Para_Sector") + " o join " +
                "(select FSectorCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Sector") + " " +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSectorCode) p " +
                " on o.FSectorCode = p.FSectorCode and o.FStartDate = p.FStartDate) h on a.FSectorCode = h.FSectorCode" +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " where FCheckState = 1) j on  a.FCusCatCode = j.FCusCatCode " +
                " left join (select FAffCorpCode,FAffCorpName from " +
                pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                " where FCheckState = 1) k on a.FIssueCorpCode = k.FAffCorpCode " +
                " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FSettleDayType") + " = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +
                " order by a.FCatCode, a.FSubCatCode, a.FCusCatCode, a.FExchangeCode, a.FSectorCode, a.FSecurityCode";

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
            throw new YssException("获取证券信息维护数据出错", e);
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
    public void delSetting() {
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
     *by caocheng 2009.02.21 MS00008 优化估值方法
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        if (sType.trim().toLowerCase().equals("open")) {
            createMTVtmpTab(); //用户点击前台"展开按钮"获取数据
        }
        if (sType.trim().toLowerCase().equals("search")) {
            return searchMTVtmp(); //插叙估值方法关联的证券信息
        }
        if (sType.trim().toLowerCase().equals("close")) {
            saveAndDelMTVtmp(); //临时表的数据导入估值方法链接表,并删除临时表
        }
        //add by fangjiang 2010.11.17 bug 254
        if(sType.split(":")[0].equalsIgnoreCase("searchmtv")){
        	return searchMTV(sType); //临时表的数据导入估值方法链接表,并删除临时表
        }
        //----------------------
        return this.getListViewData1();
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
     * 2009-02-24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
     * 根据传入的估值方法代码查询筛选条件拼接为 WHERE 语句
     * @param sMtvCode String：估值方法代码
     * @return String：WHERE 语句
     * @throws YssException
     */
    private String buildSqlWhereByMTVSelCond(String sMtvCode) throws YssException {
        String strSql = "";
        String sWhereSql = "";
        ResultSet rs = null;
        try {
            //查询所有筛选条件
            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Para_MTVSelCondSet") +
                " WHERE FMTVCode = " + dbl.sqlString(sMtvCode) +
                " AND FCheckState = 1 ORDER BY FMTVSelCondCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                String sSubWhere = "";
                String sCond = "";

                //如果某一项不为空就拼入 WHERE 语句中
                //品种类型
                sCond = rs.getString("FCatCode");
                if (sCond != null && !sCond.equalsIgnoreCase("null") && sCond.length() != 0) {
                    sSubWhere += (" FCatCode = " + dbl.sqlString(sCond) + " AND");
                }
                //品种子类型
                sCond = rs.getString("FSubCatCode");
                //2009.04.03 蒋锦 添加 添加不为 "null" 的判断
                //MS00350 《QDV4赢时胜上海2009年4月3日01_B》
                if (sCond != null && !sCond.equalsIgnoreCase("null") && sCond.length() != 0) {
                    sSubWhere += (" FSubCatCode = " + dbl.sqlString(sCond) + " AND");
                }
                //自定义品种类型
                sCond = rs.getString("FCusCatCode");
                if (sCond != null && !sCond.equalsIgnoreCase("null") && sCond.length() != 0) {
                    sSubWhere += (" FCusCatCode = " + dbl.sqlString(sCond) + " AND");
                }
                
             // edit by lidaolong 20110401  #553 估值方法设置中添加字段区分市场或交易所
                //交易所代码
                sCond = rs.getString("FExchangeCode");
                if (sCond != null && !sCond.equalsIgnoreCase("null") && sCond.length() != 0) {
                   // sSubWhere += (" FExchangeCode = " + dbl.sqlString(sCond) + " AND");
                	   sSubWhere += (" FExchangeCode in (" + operSql.sqlCodes(sCond) + ") AND");
                }
                
                //end by lidaolong

                //去掉最后一个 “AND”
                if (sSubWhere.length() > 0) {
                    sSubWhere = sSubWhere.substring(0, sSubWhere.length() - 3);
                }

                sWhereSql += (" (" + sSubWhere + ") OR");
            }
            if (sWhereSql.length() > 0) {
                //去掉最后一个“OR”
                sWhereSql = sWhereSql.substring(0, sWhereSql.length() - 2);
                //2009.04.03 蒋锦修改 添加一个括号，将所有的 OR 条件作为一个条件
                //MS00350 《QDV4赢时胜上海2009年4月3日01_B》
                sWhereSql = "(" + sWhereSql + ")";
            } else {
                //如果不存在筛选条件
                sWhereSql = " 1 = 2";
            }
        } catch (Exception e) {
            throw new YssException("拼装筛选条件出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sWhereSql;
    }

    /**
     * 2009-02-24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
     * 证券链接估值方法或取消与估值方法的链接的数据库处理
     * @param sOperStr String：证券与估值链接的详情。格式: 证券代码,估值方法代码,估值方法代码,...\t证券代码,估值方法代码,估值方法代码,...
     * @param sOperType String：操作类型：add 添加；del 剔除
     * @throws YssException
     */
    private void linkOrUnLinkMtv(String sOperStr, String sOperType) throws YssException {
        String arrSecAndMtv[] = null; //单只证券与估值方法关系数组，数组第一位为证券代码后面的都是与此只证券关联的估值方法代码
        String arrSubOperStr[] = null;
        PreparedStatement psmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            //将总的字符串分割为子字符串组
            arrSubOperStr = sOperStr.split("\t");
            //添加和剔除操作分开处理
            if (sOperType.equalsIgnoreCase("add")) {
                strSql = "INSERT INTO " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " (FMTVCode, FLinkCode, FStartDate, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime)" +
                    " VALUES(?,?,?,?,?,?,?,?)";
                psmt = conn.prepareStatement(strSql);
                for (int i = 0; i < arrSubOperStr.length; i++) {
                    arrSecAndMtv = arrSubOperStr[i].split(",");
                    //循环所有关联的估值方法
                    for (int j = 1; j < arrSecAndMtv.length; j++) {
                        psmt.setString(1, arrSecAndMtv[j]);
                        psmt.setString(2, arrSecAndMtv[0]); //数组第一位永远是证券代码
                        psmt.setDate(3, YssFun.toSqlDate("1901-01-01"));
                        psmt.setInt(4, 1);
                        psmt.setString(5, pub.getUserCode());
                        psmt.setString(6, YssFun.formatDatetime(new java.util.Date()));
                        psmt.setString(7, pub.getUserCode());
                        psmt.setString(8, YssFun.formatDatetime(new java.util.Date()));
                        psmt.addBatch();
                    }
                }
                psmt.executeBatch();
            } else if (sOperType.equalsIgnoreCase("del")) {
                strSql = "DELETE FROM " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " WHERE FMTVCode = ? AND FLinkCode = ?";
                psmt = conn.prepareStatement(strSql);
                for (int i = 0; i < arrSubOperStr.length; i++) {
                    arrSecAndMtv = arrSubOperStr[i].split(",");
                    for (int j = 1; j < arrSecAndMtv.length; j++) {
                        psmt.setString(1, arrSecAndMtv[j]);
                        psmt.setString(2, arrSecAndMtv[0]); //数组第一位永远是证券代码
                        psmt.addBatch();
                    }
                }
                psmt.executeBatch();
            }
        } catch (Exception e) {
            throw new YssException("将证券插入估值方法链接表出错！", e);
        } finally {
            //2009.04.14 蒋锦 添加 关闭 prepareStatement MS00377
            dbl.closeStatementFinal(psmt);
        }
    }

    /**
     * 2009-02-24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
     * 根据传入的证券代码和操作方式，将证券链接估值方法或取消与估值方法的链接
     * @param alSecurity ArrayList：证券代码
     * @param sOperType String：操作类型：add 添加；del 剔除
     * @return String：返回证券与估值链接的详情。格式: 证券代码,估值方法代码,估值方法代码,...\t证券代码,估值方法代码,估值方法代码,...
     * @throws YssException
     */
    public String operSecurityLinkMtvMethod(ArrayList alSecurity, String sOperType) throws YssException {
        String sResult = ""; //返回值
        String strSql = "";
        ResultSet rs = null;
        ArrayList alMtvCode = new ArrayList();
        try {
            //-------查出所有已被审核的估值方法---------//
            strSql = "SELECT FMTVCode FROM " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                " WHERE FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                alMtvCode.add(rs.getString("FMTVCode"));
            }
            dbl.closeResultSetFinal(rs);
            //-------------------------------------//

            //添加和剔出分开处理
            if (sOperType.equalsIgnoreCase("add")) {
                //循环证券代码
                for (int iSec = 0; iSec < alSecurity.size(); iSec++) {
                    String sSecCode = (String) alSecurity.get(iSec) + ",";
                    String sMtvCode = "";
                    //循环估值方法代码
                    for (int iMtv = 0; iMtv < alMtvCode.size(); iMtv++) {
                        //判断是否满足估值方法的筛选条件
                        strSql = "SELECT FSecurityCode FROM " + pub.yssGetTableName("TB_Para_Security") +
                            " WHERE (FSecurityCode = " + dbl.sqlString( (String) alSecurity.get(iSec)) +
                            ") AND " + buildSqlWhereByMTVSelCond( (String) alMtvCode.get(iMtv));
                        rs = dbl.openResultSet(strSql);
                        if (rs.next()) {
                            //拼装单个证券的所有估值方法字符串，证券代码和估值方法代码和估值方法代码之间用“,”分割
                            sMtvCode += ( (String) alMtvCode.get(iMtv) + ",");
                        }
                        //2009.04.14 蒋锦添加 关闭游标 BUG:MS00377
                        dbl.closeResultSetFinal(rs);
                    }
                    if (sMtvCode.length() == 0) {
                        //如果一个估值方法都不满足，跳出此次循环，继续下一次循环
                        continue;
                    } else {
                        //将证券代码放在估值方法代码前面，多个证券之间用“\t”分割
                        sSecCode += (sMtvCode.substring(0, sMtvCode.length() - 1) + "\t");
                    }
                    //拼装完整的返回字符串
                    sResult += sSecCode;
                }
                linkOrUnLinkMtv(sResult, sOperType);
            } else if (sOperType.equalsIgnoreCase("del")) {
                //剔除操作的循环方式与增加操作一样
                for (int iSec = 0; iSec < alSecurity.size(); iSec++) {
                    String sSecCode = (String) alSecurity.get(iSec) + ",";
                    String sMtvCode = "";
                    for (int iMtv = 0; iMtv < alMtvCode.size(); iMtv++) {
                        //判断证券是否已和估值方法相绑定
                        strSql = "SELECT b.FSecurityCode" +
                            " FROM (SELECT FMTVCode, FLinkCode" +
                            " FROM " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                            " WHERE FCheckState = 1" +
                            " AND FMtvCode = " + dbl.sqlString( (String) alMtvCode.get(iMtv)) +
                            " AND FLinkCode = " + dbl.sqlString( (String) alSecurity.get(iSec)) + ") a" +
                            " JOIN (SELECT FSecurityCode" +
                            " FROM " + pub.yssGetTableName("TB_Para_Security") +
                            " WHERE (1=2) OR " + buildSqlWhereByMTVSelCond( (String) alMtvCode.get(iMtv)) +
                            " ) b ON a.FLinkCode = b.FSecurityCode";
                        rs = dbl.openResultSet(strSql);
                        //字符串拼接方式与增加操作一样
                        if (rs.next()) {
                            sMtvCode += ( (String) alMtvCode.get(iMtv) + ",");
                        }
                        //2009.04.14 蒋锦添加 关闭游标 BUG:MS00377
                        dbl.closeResultSetFinal(rs);
                    }
                    if (sMtvCode.length() == 0) {
                        continue;
                    } else {
                        sSecCode += (sMtvCode.substring(0, sMtvCode.length() - 1) + "\t");
                    }
                    sResult += sSecCode;
                }
                linkOrUnLinkMtv(sResult, sOperType);
            }
        } catch (Exception e) {
            throw new YssException("证券绑定估值方法出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
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
    
    //add by fangjiang 2010.11.17 bug 254
    public String searchMTV(String sType) throws YssException {
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
            bufSql.append(" SELECT sec.*,ass.FAssetGroupName as FAssetGroupName,d.FCatName,e.FSubCatName,f.FExchangeName, f.FRegionCode,fa.FRegionName,fb.Fcountrycode,fb.FCountryName,");
            bufSql.append("fc.FAreaCode,fc.FAreaName,g.FCurrencyName,h.FSectorName,i.FHolidaysName,j.FCusCatName,k.FAffCorpName as FIssueCorpName,m.FVocName as FSettleDayTypeValue,");
            bufSql.append("b.fusername as fcreatorname,c.fusername as fcheckusername,l.fsecclsname as FSYNTHETICNAME ");//板块分类名称add by yanghaiming 20100426
            bufSql.append(" FROM (SELECT * from ");
            bufSql.append(pub.yssGetTableName("Tb_Para_Security"));
            
            //edited by zhouxiang MS01550 估值方法设置展开后，不能按照选择的“交易所”条件筛选证券信息 
			bufSql.append(" a WHERE FCheckState = 1");
			if (this.exchangeCode.length() > 1)
				bufSql.append("and  a.FExchangeCode= ").append(
						dbl.sqlString(this.exchangeCode));
			//end--  by zhouxiang MS01550 估值方法设置展开后，不能按照选择的“交易所”条件筛选证券信息 
            
            bufSql.append(buildFilterSql()).append(") sec ");
            bufSql.append(" left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on sec.FAssetGroupCode = ass.FAssetGroupCode");		//added by liubo.Story #1770
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
            bufSql.append(" p ) h on sec.FSectorCode = h.FSectorCode ");
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
    //------------------------
    
    //added by liubo.Story #1770
    //====================================
    /**
     * 2009-02-24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
     * 根据传入的证券代码和操作方式，将证券链接估值方法或取消与估值方法的链接
     * @param alSecurity ArrayList：证券代码
     * @param sOperType String：操作类型：add 添加；del 剔除
     * @return String：返回证券与估值链接的详情。格式: 证券代码,估值方法代码,估值方法代码,...\t证券代码,估值方法代码,估值方法代码,...
     * @throws YssException
     */
    public String operSecurityLinkMtvMethod(ArrayList alSecurity, String sOperType,String sAssetGroupCode) throws YssException {
        String sResult = ""; //返回值
        String strSql = "";
        ResultSet rs = null;
        ArrayList alMtvCode = new ArrayList();
        try {
            //-------查出所有已被审核的估值方法---------//
            strSql = "SELECT FMTVCode FROM " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                " WHERE FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                alMtvCode.add(rs.getString("FMTVCode"));
            }
            dbl.closeResultSetFinal(rs);
            //-------------------------------------//

            //添加和剔出分开处理
            if (sOperType.equalsIgnoreCase("add")) {
                //循环证券代码
                for (int iSec = 0; iSec < alSecurity.size(); iSec++) {
                    String sSecCode = (String) alSecurity.get(iSec) + ",";
                    String sMtvCode = "";
                    //循环估值方法代码
                    for (int iMtv = 0; iMtv < alMtvCode.size(); iMtv++) {
                        //判断是否满足估值方法的筛选条件
                        strSql = "SELECT FSecurityCode FROM " + "TB_" + sAssetGroupCode + "_Para_Security" +
                            " WHERE (FSecurityCode = " + dbl.sqlString( (String) alSecurity.get(iSec)) +
                            ") AND " + buildSqlWhereByMTVSelCond( (String) alMtvCode.get(iMtv),sAssetGroupCode);
                        rs = dbl.openResultSet(strSql);
                        if (rs.next()) {
                            //拼装单个证券的所有估值方法字符串，证券代码和估值方法代码和估值方法代码之间用“,”分割
                            sMtvCode += ( (String) alMtvCode.get(iMtv) + ",");
                        }
                        //2009.04.14 蒋锦添加 关闭游标 BUG:MS00377
                        dbl.closeResultSetFinal(rs);
                    }
                    if (sMtvCode.length() == 0) {
                        //如果一个估值方法都不满足，跳出此次循环，继续下一次循环
                        continue;
                    } else {
                        //将证券代码放在估值方法代码前面，多个证券之间用“\t”分割
                        sSecCode += (sMtvCode.substring(0, sMtvCode.length() - 1) + "\t");
                    }
                    //拼装完整的返回字符串
                    sResult += sSecCode;
                }
                linkOrUnLinkMtv(sResult, sOperType,sAssetGroupCode);
            } else if (sOperType.equalsIgnoreCase("del")) {
                //剔除操作的循环方式与增加操作一样
                for (int iSec = 0; iSec < alSecurity.size(); iSec++) {
                    String sSecCode = (String) alSecurity.get(iSec) + ",";
                    String sMtvCode = "";
                    for (int iMtv = 0; iMtv < alMtvCode.size(); iMtv++) {
                        //判断证券是否已和估值方法相绑定
                        strSql = "SELECT b.FSecurityCode" +
                            " FROM (SELECT FMTVCode, FLinkCode" +
                            " FROM " + "Tb_" + sAssetGroupCode + "_Para_MTVMethodLink" +
                            " WHERE FCheckState = 1" +
                            " AND FMtvCode = " + dbl.sqlString( (String) alMtvCode.get(iMtv)) +
                            " AND FLinkCode = " + dbl.sqlString( (String) alSecurity.get(iSec)) + ") a" +
                            " JOIN (SELECT FSecurityCode" +
                            " FROM " + pub.yssGetTableName("TB_Para_Security") +
                            " WHERE (1=2) OR " + buildSqlWhereByMTVSelCond( (String) alMtvCode.get(iMtv),sAssetGroupCode) +
                            " ) b ON a.FLinkCode = b.FSecurityCode";
                        rs = dbl.openResultSet(strSql);
                        //字符串拼接方式与增加操作一样
                        if (rs.next()) {
                            sMtvCode += ( (String) alMtvCode.get(iMtv) + ",");
                        }
                        //2009.04.14 蒋锦添加 关闭游标 BUG:MS00377
                        dbl.closeResultSetFinal(rs);
                    }
                    if (sMtvCode.length() == 0) {
                        continue;
                    } else {
                        sSecCode += (sMtvCode.substring(0, sMtvCode.length() - 1) + "\t");
                    }
                    sResult += sSecCode;
                }
                linkOrUnLinkMtv(sResult, sOperType,sAssetGroupCode);
            }
        } catch (Exception e) {
            throw new YssException("证券绑定估值方法出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }
    
    //added by liubo.Story #1770
    //=================================
    /**
     * 2009-02-24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
     * 根据传入的估值方法代码查询筛选条件拼接为 WHERE 语句
     * @param sMtvCode String：估值方法代码
     * @return String：WHERE 语句
     * @throws YssException
     */
    private String buildSqlWhereByMTVSelCond(String sMtvCode,String sAssetGroupCode) throws YssException {
        String strSql = "";
        String sWhereSql = "";
        ResultSet rs = null;
        try {
            //查询所有筛选条件
            strSql = "SELECT * FROM " + "Tb_" + sAssetGroupCode + "_Para_MTVSelCondSet" +
                " WHERE FMTVCode = " + dbl.sqlString(sMtvCode) +
                " AND FCheckState = 1 ORDER BY FMTVSelCondCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                String sSubWhere = "";
                String sCond = "";

                //如果某一项不为空就拼入 WHERE 语句中
                //品种类型
                sCond = rs.getString("FCatCode");
                if (sCond != null && !sCond.equalsIgnoreCase("null") && sCond.length() != 0) {
                    sSubWhere += (" FCatCode = " + dbl.sqlString(sCond) + " AND");
                }
                //品种子类型
                sCond = rs.getString("FSubCatCode");
                //2009.04.03 蒋锦 添加 添加不为 "null" 的判断
                //MS00350 《QDV4赢时胜上海2009年4月3日01_B》
                if (sCond != null && !sCond.equalsIgnoreCase("null") && sCond.length() != 0) {
                    sSubWhere += (" FSubCatCode = " + dbl.sqlString(sCond) + " AND");
                }
                //自定义品种类型
                sCond = rs.getString("FCusCatCode");
                if (sCond != null && !sCond.equalsIgnoreCase("null") && sCond.length() != 0) {
                    sSubWhere += (" FCusCatCode = " + dbl.sqlString(sCond) + " AND");
                }
                
             // edit by lidaolong 20110401  #553 估值方法设置中添加字段区分市场或交易所
                //交易所代码
                sCond = rs.getString("FExchangeCode");
                if (sCond != null && !sCond.equalsIgnoreCase("null") && sCond.length() != 0) {
                   // sSubWhere += (" FExchangeCode = " + dbl.sqlString(sCond) + " AND");
                	   sSubWhere += (" FExchangeCode in (" + operSql.sqlCodes(sCond) + ") AND");
                }
                
                //end by lidaolong

                //去掉最后一个 “AND”
                if (sSubWhere.length() > 0) {
                    sSubWhere = sSubWhere.substring(0, sSubWhere.length() - 3);
                }

                sWhereSql += (" (" + sSubWhere + ") OR");
            }
            if (sWhereSql.length() > 0) {
                //去掉最后一个“OR”
                sWhereSql = sWhereSql.substring(0, sWhereSql.length() - 2);
                //2009.04.03 蒋锦修改 添加一个括号，将所有的 OR 条件作为一个条件
                //MS00350 《QDV4赢时胜上海2009年4月3日01_B》
                sWhereSql = "(" + sWhereSql + ")";
            } else {
                //如果不存在筛选条件
                sWhereSql = " 1 = 2";
            }
        } catch (Exception e) {
            throw new YssException("拼装筛选条件出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sWhereSql;
    }
    
    //added by liubo.Story #1770
    //==================================
    /**
     * 2009-02-24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
     * 证券链接估值方法或取消与估值方法的链接的数据库处理
     * @param sOperStr String：证券与估值链接的详情。格式: 证券代码,估值方法代码,估值方法代码,...\t证券代码,估值方法代码,估值方法代码,...
     * @param sOperType String：操作类型：add 添加；del 剔除
     * @throws YssException
     */
    private void linkOrUnLinkMtv(String sOperStr, String sOperType, String sAssetGroupCode) throws YssException {
        String arrSecAndMtv[] = null; //单只证券与估值方法关系数组，数组第一位为证券代码后面的都是与此只证券关联的估值方法代码
        String arrSubOperStr[] = null;
        PreparedStatement psmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            //将总的字符串分割为子字符串组
            arrSubOperStr = sOperStr.split("\t");
            //添加和剔除操作分开处理
            if (sOperType.equalsIgnoreCase("add")) {
                strSql = "INSERT INTO " + "Tb_" + sAssetGroupCode + "_Para_MTVMethodLink" +
                    " (FMTVCode, FLinkCode, FStartDate, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime)" +
                    " VALUES(?,?,?,?,?,?,?,?)";
                psmt = conn.prepareStatement(strSql);
                for (int i = 0; i < arrSubOperStr.length; i++) {
                    arrSecAndMtv = arrSubOperStr[i].split(",");
                    //循环所有关联的估值方法
                    for (int j = 1; j < arrSecAndMtv.length; j++) {
                    	
                    	strSql = "Delete from Tb_" + sAssetGroupCode + "_Para_MTVMethodLink where FMTVCode = " + dbl.sqlString(arrSecAndMtv[j]) + 
           			 			 " and FLinkCode = " + dbl.sqlString(arrSecAndMtv[0]);
                        dbl.executeSql(strSql);
                    	psmt.setString(1, arrSecAndMtv[j]);
                        psmt.setString(2, arrSecAndMtv[0]); //数组第一位永远是证券代码
                        psmt.setDate(3, YssFun.toSqlDate("1901-01-01"));
                        psmt.setInt(4, 1);
                        psmt.setString(5, pub.getUserCode());
                        psmt.setString(6, YssFun.formatDatetime(new java.util.Date()));
                        psmt.setString(7, pub.getUserCode());
                        psmt.setString(8, YssFun.formatDatetime(new java.util.Date()));
                        psmt.addBatch();
                    }
                }
                psmt.executeBatch();
            } else if (sOperType.equalsIgnoreCase("del")) {
                strSql = "DELETE FROM " + "Tb_" + sAssetGroupCode + "_Para_MTVMethodLink" +
                    " WHERE FMTVCode = ? AND FLinkCode = ?";
                psmt = conn.prepareStatement(strSql);
                for (int i = 0; i < arrSubOperStr.length; i++) {
                    arrSecAndMtv = arrSubOperStr[i].split(",");
                    for (int j = 1; j < arrSecAndMtv.length; j++) {
                        psmt.setString(1, arrSecAndMtv[j]);
                        psmt.setString(2, arrSecAndMtv[0]); //数组第一位永远是证券代码
                        psmt.addBatch();
                    }
                }
                psmt.executeBatch();
            }
        } catch (Exception e) {
            throw new YssException("将证券插入估值方法链接表出错！", e);
        } finally {
            //2009.04.14 蒋锦 添加 关闭 prepareStatement MS00377
            dbl.closeStatementFinal(psmt);
        }
    }

}
