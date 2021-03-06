package com.yss.main.parasetting;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * <p>Title: 期权信息操作</p>
 *
 * <p>
 * Description:
 * 该类主要负责期权的信息操作，主要功能如下:
 * <br>
 * 1.数据的增、删、改、查操作
 * 2.实体Bean的封装、拆箱
 * 3.数据合法性的检测
 * </br>
 * </p>
 *
 * BugNO:MS00484 QDV4招商证券2009年06月04日01_A
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech</p>
 *
 * @author libo 20090624
 * @version 1.0
 */
public class OptionContractBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sRecycled = ""; //回收站数据

    private String sOptionCode = ""; //期权代码
    private String sOptionName = ""; //期权名称
    private String sCatCode = ""; //品种类型
    private String sSubCatCode = ""; //品种子类型
    private String sCuryCode = ""; //期权币种
    private String sTSecurityCode = ""; //标的证券代码
    private String sDepDurCode = ""; //期限代码
    private java.util.Date dtExpiryDate; //行权日/到期日
    private String sTradeTypeCode = ""; //期权交易类别
    private String sExecuteTypeCode = ""; //	行权方式
    private BigDecimal dExercisePrice; //标的价格
    private BigDecimal dMultiple; //放大倍数
    private BigDecimal dBeginBail; //初始保证金
    private String sCountryCode = ""; //交易国家
    private String sExchangeCode = ""; //交易所代码
    private String sDesc = ""; //描述

    private String sBailType = ""; //保证金类型 //add
    private String sFUType = ""; //期货类型
    private BigDecimal dBailScale; //保证金比例
    private BigDecimal dBailFix; //每手固定保证金

    private String sCatName = ""; //品种类型 名称
    private String sSubCatName = ""; //品种子类型 名称
    private String sCuryName = ""; //期权币种 名称
    private String sTSecurityName = ""; //标的证券代码 名称
    private String sDepDurName = ""; //期限代码 名称

    private String sExchangeName = ""; //交易所代码 名称

    private String sBailTypeName = ""; //保证金类型名
    private String sExecuteTypeName = ""; //行权方式名
    private String sFUTypeName = ""; //期货类型名
    private String sTradeTypeName = ""; //期权交易类型名

    private String sOldOptionCode = ""; //期权代码 老数据 关键字

    private OptionContractBean filterType; //parseRowStr方法中 //用于筛选
    
    //add by songjie 2011.05.11 需求 859 QDV4赢时胜（深圳）2011年03月30日01_A 
    private String isOnlyColumn = "";//是否只显示列表，不显示数据
    
    public OptionContractBean() {
    }

    /**
     * 此方法用来检查数据的可用性，比如是否存在，是否已被操作等
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {

        // 调用dbFun.checkInputCommon(操作类型，表名，主键字段名，新主键值，旧主键值)
        // 此处仅比较的是期权代码
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_OptionContract"), //edited by  libo 20090709 改表名为公用
                               "FOptionCode", this.sOptionCode, this.sOldOptionCode); //老的数据

    }

    /**
     * 此方法用来将新增加的期权信息保存到数据库
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            //使用insert语句将数据保存到数据库，这里使用+拼装
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_OptionContract") + //edited by  libo 20090709 改表名为公用
                "(" +
                "FOptionCode,FOptionName,FTSecurityCode,FDepDurCode,FExpiryDate" +
                ",FTradeTypeCode,FExecuteTypeCode,FExercisePrice,FMultiple,FBeginBail" +
                ",FDesc,FBailType,FFUType,FBailScale,FBailFix,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
                dbl.sqlString(this.sOptionCode) + "," +
                dbl.sqlString(this.sOptionName) + "," +
                dbl.sqlString(this.sTSecurityCode) + "," +
                dbl.sqlString(this.sDepDurCode) + "," +
                dbl.sqlDate(this.dtExpiryDate) + "," +
                dbl.sqlString(this.sTradeTypeCode) + "," +
                dbl.sqlString(this.sExecuteTypeCode) + "," +
                this.dExercisePrice + "," +
                this.dMultiple + "," +
                this.dBeginBail + "," +
                (this.sDesc.length() == 0 ? dbl.sqlString(" ") : dbl.sqlString(this.sDesc)) + "," + //描述
                dbl.sqlString(this.sBailType) + "," +
                "' '," +                //edited by libo 20090709  期货类型前台已经不显示，不用处理，直接插入空格
                this.dBailScale + "," + //保证金比例
                this.dBailFix + "," +   //每手固定保证金
                //插入审核状态要根据“数据是否需要审核”参数来判断，如果是，则插入0，代表未审核
                (pub.getSysCheckState() ? "0" : "1") + "," + //FCheckState
                dbl.sqlString(this.creatorCode) + "," + //FCreator
                dbl.sqlString(this.creatorTime) + "," + //FCreateTime
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," + //FCheckUser
                dbl.sqlString(this.checkTime) + //FCheckTime
                ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增期权信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改期权信息的方法
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        boolean bTrans = true;
        String strSql = "";
        Connection conn = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            //更新期权信息，通过期权代码进行匹配
            strSql = "update " + pub.yssGetTableName("Tb_Para_OptionContract") + //edited by  libo 20090709 改表名为公用
                " set FOptionCode = " + dbl.sqlString(this.sOptionCode) +
                " ,FOptionName = " + dbl.sqlString(this.sOptionName) +
                " ,FTSecurityCode = " + dbl.sqlString(this.sTSecurityCode) +
                " ,FDepDurCode = " + dbl.sqlString(this.sDepDurCode) +
                " ,FExpiryDate = " + dbl.sqlDate(this.dtExpiryDate) +
                " ,FTradeTypeCode = " + dbl.sqlString(this.sTradeTypeCode) +
                " ,FExecuteTypeCode = " + dbl.sqlString(this.sExecuteTypeCode) +
                " ,FExercisePrice = " + this.dExercisePrice +
                " ,FMultiple = " + this.dMultiple +
                " ,FBeginBail = " + this.dBeginBail +
                " ,FDesc = " + (this.sDesc.length() == 0 ? dbl.sqlString(" ") : dbl.sqlString(this.sDesc)) +
                " ,FBailType = " + dbl.sqlString(this.sBailType) +
                " ,FBailScale = " + this.dBailScale +
                " ,FBailFix = " + this.dBailFix +
                " ,FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                " ,FCreator = "
                + dbl.sqlString(this.creatorCode) +
                " ,FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                " ,FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FOptionCode = " + dbl.sqlString(this.sOldOptionCode);
            dbl.executeSql(strSql);
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("修改期权信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * 删除期权信息的方法
     * 本方法实际上是假删，只是将审核状态更新为2
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_OptionContract") + //edited by  libo 20090709 改表名为公用
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FOptionCode = " + dbl.sqlString(this.sOptionCode);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除期权信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 审核、反审核、还原数据
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String strSql = "";
        String[] arrData = null;
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            st = conn.createStatement();
            //审核、反审核、还原可能存在多条数据同时操作，采用批量处理
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Para_OptionContract") + //edited by  libo 20090709 改表名为公用
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FOptionCode = " + dbl.sqlString(this.sOptionCode);
                    st.addBatch(strSql);
                }
                st.executeBatch();
            } else {
                if (this.sOptionCode != null &&
                    this.sOptionCode.trim().length() > 0) {
                    strSql = "update " + pub.yssGetTableName("Tb_Para_OptionContract") + //edited by  libo 20090709 改表名为公用
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FOptionCode = " + dbl.sqlString(this.sOptionCode); //报文类型
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核期权信息出错!", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * 获取一条期权的信息
     * @return IDataSetting
     * @throws YssException
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,se.fcatcode,se.fsubcatcode,se.ftradecury,se.fexchangecode,b.FUserName as FCreatorName,c.FUserName as FCheckUserName" + //创建人，核查人
                ",d.FCatName as FCatName" + //品种类型
                ",d1.FSubCatName as FSubCatName" + //品种子类型

                ",d2.FCurrencyName as FCuryName" + //期权币种
                ",d3.FIndexName as FTSecurityName" + //指数代码
                ",d4.FDepDurName as FDepDurName" + //期限代码
                ",d5.FExchangeName as FExchangeName" + //交易所代码
                ",d5.fcountrycode" +
                ",e.FVocName as FTradeTypeName" + //期权交易类别
                ",f.FVocName as FExecuteTypeName " + //行权方式
                ",g.FVocName as FBailTypeName " + //保证金类型
                ",h.FVocName as FFUTypeName " + //期货类型

                " from " + pub.yssGetTableName("Tb_Para_OptionContract") + " a" + //edited by  libo 20090709 改表名为公用
                " join(select * from "+pub.yssGetTableName("tb_para_security")
                +" where FCheckState =1" + ") se on a.FOptionCode = se.FSecurityCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" + ///这个保留
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " + ///这个保留

                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on se.FCatCode = d.FCatCode" + //品种类型
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) d1 on se.FSubCatCode = d1.FSubCatCode" + //品种子类型

                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) d2 on se.FTradeCury = d2.FCuryCode" + //期权币种

                " left join (select FIndexCode,FIndexName from " +
                pub.yssGetTableName("Tb_Para_Index") +
                " where FCheckState = 1) d3 on d3.FIndexCode = a.FTSecurityCode " + //指数代码

                " left join (select FDepDurCode,FDepDurName from " +
                pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FCheckState = 1) d4 on d4.FDepDurCode = a.FDepDurCode " + //期限代码

                " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) d5 on se.FExchangeCode = d5.FExchangeCode" + //交易所代码

                " left join Tb_Fun_Vocabulary e on " +
                "a.FTradeTypeCode" + //期权交易类别
                " =e.FVocCode and " + " e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PARASET_TRADETYPECODETYPE) +

                " left join Tb_Fun_Vocabulary f on " +
                dbl.sqlToChar("a.FExecuteTypeCode") + //行权方式
                " =f.FVocCode and " +
                " f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PARASET_EXECUTETYPECODE) +

                " left join Tb_Fun_Vocabulary g on " +
                dbl.sqlToChar("a.FBailType") + //保证金类型
                " =g.FVocCode and " +
                " g.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FUN_BailType) +

                " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FFUType") + //期货类型
                " =h.FVocCode and " +
                " h.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PARASET_FFUType) +

                " where FOptionCode= " + dbl.sqlString(this.sOptionCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                setResultSetAttr(rs);
            }
        } catch (Exception e) {
            throw new YssException("获取股指期权信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * 清空回收站的功能，将数据从数据库中删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            st = conn.createStatement();
            //清空回收站是多选的，因此可能是删除多条数据，采用批量处理
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Para_OptionContract") + //edited by  libo 20090709 改表名为公用
                        " where FOptionCode = " + dbl.sqlString(this.sOptionCode);
                    st.addBatch(strSql);
                }
                st.executeBatch();
            } else {
                if (this.sOptionCode != null &&
                    this.sOptionCode.trim().length() > 0) {
                    strSql = "delete from " + pub.yssGetTableName("Tb_Para_OptionContract") + //edited by  libo 20090709 改表名为公用
                        " where FOptionCode = " + dbl.sqlString(this.sOptionCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }

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
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = getListView1Headers();

            sqlStr =
                "select a.*,se1.fcatcode,se1.fsubcatcode,se1.ftradecury,se1.fexchangecode,b.FUserName as FCreatorName,c.FUserName as FCheckUserName" + //创建人，核查人
                ",d.FCatName as FCatName" + //品种类型 ok
                ",d1.FSubCatName as FSubCatName" + //品种子类型 ok

                ",d2.FCurrencyName as FCuryName" + //期权币种  ok
                //20100423 蒋锦 添加 南方东英H+A项目期权需求 
                //edit by songjie 2011.06.17 指数期权信息设置界面，双击该菜单报 未明确定义列的  错误
                ",CASE WHEN se1.FSubCatCode = 'FP01' THEN d3.FIndexName WHEN se1.FSubCatCode = 'FP02' THEN se.fsecurityname END AS FTSecurityName" +
                ",d4.FDepDurName as FDepDurName" + //期限代码         ok
                
                ",d5.FExchangeName as FExchangeName" + //交易所代码         ok
                ",d5.fcountrycode" +
                ",e.FVocName as FTradeTypeName" + //期权交易类别
                ",f.FVocName as FExecuteTypeName " + //行权方式
                ",g.FVocName as FBailTypeName " + //保证金类型

                " from " + pub.yssGetTableName("Tb_Para_OptionContract") + " a" + //edited by  libo 20090709 改表名为公用
                " join(select * from "+pub.yssGetTableName("tb_para_security")
                //modify by zhangfa 20100928 MS01779    证券信息维护界面，“其他属性”中证券代码应该灰掉    QDV4赢时胜(测试)2010年09月20日02_B 
                //去掉tb_para_security 审核条件为1:如果不去掉,则在证券信息设置支中,期权数据反审核后,点击其他属性后,加载不了
                //已存在的期权信息
                //--------------------------------------------------------------------------------------------------------------------
                + ") se1 on a.FOptionCode = se1.FSecurityCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" + ///这个保留
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " + ///这个保留

                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on se1.FCatCode = d.FCatCode" + //品种类型
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) d1 on se1.FSubCatCode = d1.FSubCatCode" + //品种子类型

                " left join (select FCuryCode,FCuryName as FCurrencyName from " + pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) d2 on se1.FTradeCury = d2.FCuryCode" + //期权币种

                " left join (select FIndexCode,FIndexName from " + pub.yssGetTableName("Tb_Para_Index") + " where FCheckState = 1) d3 on d3.FIndexCode = a.FTSecurityCode " + //指数代码
                //20100423 蒋锦 添加 南方东英H+A项目期权需求 
                " left join (SELECT FSecurityCode, FSecurityName FROM " + pub.yssGetTableName("Tb_Para_Security") + " WHERE FCheckState = 1) se ON se.fsecuritycode = a.ftsecuritycode" + //证券代码
                " left join (select FDepDurCode,FDepDurName from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FCheckState = 1) d4 on d4.FDepDurCode = a.FDepDurCode " + //期限代码

                " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) d5 on se1.FExchangeCode = d5.FExchangeCode" + //交易所代码

                " left join Tb_Fun_Vocabulary e on " +
                "a.FTradeTypeCode" + //期权交易类别
                " =e.FVocCode and " + " e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PARASET_TRADETYPECODETYPE) +

                " left join Tb_Fun_Vocabulary f on " +
                dbl.sqlToChar("a.FExecuteTypeCode") + //行权方式
                " =f.FVocCode and " +
                " f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PARASET_EXECUTETYPECODE) +

                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FBailType") + //保证金类型
                " =g.FVocCode and " +
                " g.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FUN_BailType) +

                buildFilterSql() +
                " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs); //建一个方法
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK); //"/f/f"
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARASET_TRADETYPECODETYPE +
                                        "," +
                                        YssCons.YSS_PARASET_EXECUTETYPECODE + "," +
                                        YssCons.YSS_FUN_BailType + "," +
                                        YssCons.YSS_PARASET_FFUType
                );

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取期权信息出错", e); ///改成你的名字
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = getListView1Headers();

            sqlStr =
                "select a.*,se.fcatcode,se.fsubcatcode,se.ftradecury,se.fexchangecode,b.FUserName as FCreatorName,c.FUserName as FCheckUserName" + //创建人，核查人
                ",d.FCatName as FCatName" + //品种类型
                ",d1.FSubCatName as FSubCatName" + //品种子类型

                ",d2.FCurrencyName as FCuryName" + //期权币种
                ",d3.FIndexName as FTSecurityName" + //指数代码
                ",d4.FDepDurName as FDepDurName" + //期限代码
                ",d5.FExchangeName as FExchangeName" + //交易所代码
                ",d5.fcountrycode" +
                ",e.FVocName as FTradeTypeName" + //期权交易类别
                ",f.FVocName as FExecuteTypeName " + //行权方式
                ",g.FVocName as FBailTypeName " + //保证金类型
                ",h.FVocName as FFUTypeName " + //期货类型

                " from " + pub.yssGetTableName("Tb_Para_OptionContract") + " a" + //edited by  libo 20090709 改表名为公用
                " join(select * from "+pub.yssGetTableName("tb_para_security")
                +" where FCheckState =1" + ") se on a.FOptionCode = se.FSecurityCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" + ///这个保留
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " + ///这个保留

                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on se.FCatCode = d.FCatCode" + //品种类型
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) d1 on se.FSubCatCode = d1.FSubCatCode" + //品种子类型

                " left join (select FCuryCode,FCuryName as FCurrencyName from " + pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) d2 on se.FTradeCury = d2.FCuryCode" + //期权币种

                " left join (select FIndexCode,FIndexName from " + pub.yssGetTableName("Tb_Para_Index") + " where FCheckState = 1) d3 on d3.FIndexCode = a.FTSecurityCode " + //指数代码

                " left join (select FDepDurCode,FDepDurName from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FCheckState = 1) d4 on d4.FDepDurCode = a.FDepDurCode " + //期限代码

                " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) d5 on se.FExchangeCode = d5.FExchangeCode" + //交易所代码

                " left join Tb_Fun_Vocabulary e on " +
                "a.FTradeTypeCode" + //期权交易类别
                " =e.FVocCode and " + " e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PARASET_TRADETYPECODETYPE) +

                " left join Tb_Fun_Vocabulary f on " +
                dbl.sqlToChar("a.FExecuteTypeCode") + //行权方式
                " =f.FVocCode and " +
                " f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PARASET_EXECUTETYPECODE) +

                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FBailType") + //保证金类型
                " =g.FVocCode and " +
                " g.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FUN_BailType) +

                " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FFUType") + //期货类型
                " =h.FVocCode and " +
                " h.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PARASET_FFUType) +
                " where a.FCheckState=1 " +
                buildFilterSql() +
                " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs); //建一个方法
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARASET_TRADETYPECODETYPE +
                                        "," +
                                        YssCons.YSS_PARASET_EXECUTETYPECODE + "," +
                                        YssCons.YSS_FUN_BailType + "," +
                                        YssCons.YSS_PARASET_FFUType
                );

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取期权信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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

    /**
     *
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
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");

            this.sOptionCode = reqAry[0];
            this.sOptionName = reqAry[1];
            this.sCatCode = reqAry[2];
            this.sSubCatCode = reqAry[3];
            this.sCuryCode = reqAry[4];
            this.sTSecurityCode = reqAry[5];
            this.sDepDurCode = reqAry[6];
            this.dtExpiryDate = YssFun.parseDate(reqAry[7]);
            this.sTradeTypeCode = reqAry[8];
            this.sExecuteTypeCode = reqAry[9];
            
            if(reqAry[10].trim().length()>0){
            	this.dExercisePrice = new BigDecimal(reqAry[10]);
            }
            if(reqAry[11].trim().length()>0){
            	this.dMultiple = new BigDecimal(reqAry[11]);
            }
            if(reqAry[12].trim().length()>0){
            	this.dBeginBail = new BigDecimal(reqAry[12]);
            }

            this.sCountryCode = reqAry[13];
            this.sExchangeCode = reqAry[14];
            //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B start---// 
            if(reqAry[15].indexOf("【Enter】") != -1){
            	this.sDesc = reqAry[15].replaceAll("【Enter】", "\r\n");
            }else{
            	this.sDesc = reqAry[15];
            }
            //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B end---// 

            this.sBailType = reqAry[16];
            if(reqAry[17].trim().length()>0){
            	//edit by songjie 2011.09.28  BUG 2880 QDV4赢时胜(测试)2011年9月28日01_B 还原回收站数据时，解析报错
            	this.dBailScale = reqAry[17].equalsIgnoreCase("null") ? new BigDecimal("0") : new BigDecimal(reqAry[17]);
            }
            if(reqAry[18].trim().length()>0){
            	//edit by songjie 2011.09.28 BUG 2880 QDV4赢时胜(测试)2011年9月28日01_B 还原回收站数据时，解析报错
            	this.dBailFix = reqAry[18].equalsIgnoreCase("null") ? new BigDecimal("0") : new BigDecimal(reqAry[18]);
            }

            this.sOldOptionCode = reqAry[19]; //期权代码 老数据

            this.checkStateId = Integer.parseInt(reqAry[20]); //状态 2 为删除,从页面来,在基类中已经定义
            
            //add by songjie 2011.05.11 需求 859 QDV4赢时胜（深圳）2011年03月30日01_A 
            this.isOnlyColumn = reqAry[21]; 
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new OptionContractBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析期权信息出错！", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sOptionCode.trim()).append("\t");
        buf.append(this.sOptionName.trim()).append("\t");
        buf.append(this.sCatCode.trim()).append("\t");
        buf.append(this.sSubCatCode.trim()).append("\t");
        buf.append(this.sCuryCode.trim()).append("\t");
        buf.append(this.sTSecurityCode.trim()).append("\t");
        buf.append(this.sDepDurCode.trim()).append("\t");
        buf.append(this.dtExpiryDate).append("\t");
        buf.append(this.sTradeTypeCode.trim()).append("\t");
        buf.append(this.sExecuteTypeCode.trim()).append("\t");

        buf.append(this.dExercisePrice).append("\t");
        buf.append(this.dMultiple).append("\t");
        buf.append(this.dBeginBail).append("\t");

        buf.append(this.sCountryCode == null ? "" : this.sCountryCode).append("\t");
        buf.append(this.sExchangeCode.trim()).append("\t");

        buf.append( (this.sDesc).equals(" ") ? "" : this.sDesc).append("\t");

        buf.append(this.sBailType).append("\t");
        buf.append(this.dBailScale).append("\t");
        buf.append(this.dBailFix).append("\t");

        buf.append(this.sCatName.trim()).append("\t");
        buf.append(this.sSubCatName.trim()).append("\t");
        buf.append(this.sCuryName.trim()).append("\t");
        buf.append(this.sTSecurityName.trim()).append("\t");
        buf.append(this.sDepDurName.trim()).append("\t");

        buf.append(this.sExchangeName).append("\t"); //交易所代码名

        buf.append(this.sBailTypeName).append("\t"); //保证金类型名
        buf.append(this.sExecuteTypeName).append("\t"); //行权方式名
        buf.append(this.sTradeTypeName).append("\t"); //期权交易类型

        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public String getOperValue(String sType) throws YssException { //页面传来的getsetting方法
        if (sType.equalsIgnoreCase("setting")) {
            getSetting();
        }
      //add by zhangfa 20110110 BUG #790 指数、股票期权信息设置后，证券信息维护期权代码应不能修改 
        if (sType.equalsIgnoreCase("checkOptionContract")) {
        	return checkOptionContract();
        }
      //--------------------end 20110110---------------------------------------------------------  
        return buildRowStr();
    }
  //add by zhangfa 20110110 BUG #790 指数、股票期权信息设置后，证券信息维护期权代码应不能修改 
    private String checkOptionContract() throws YssException{
		String flag = "false";
		String strSql = "";
		ResultSet rs = null;
		try {
			if (sOptionCode == null || sOptionCode.length() == 0)
				return "false";
			strSql = " select FOptionCode from "
					+ pub.yssGetTableName("Tb_Para_OptionContract")
					+ " where FOptionCode in ("
					+ operSql.sqlCodes(this.sOptionCode) + ")"
					+ " and Fcheckstate!=2";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				flag = "true";
			}
		} catch (Exception e) {
			throw new YssException("获取股指期权信息设置出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return flag;
    }
  //-------------------------end 20110110---------------------------------------------------
    /**
     * 获取筛选条件与数据
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
        //===================================
		if(pub.isBrown()==true)
			return " where 1=1";
        //==============end=====================
        if (this.filterType != null) {
            sResult = sResult + "where 1=1";

            //---add by songjie 2011.05.11 需求 859 QDV4赢时胜（深圳）2011年03月30日01_A---//
            if (this.isOnlyColumn.equals("1") && pub.isBrown()==false) {	//20111027 modified by liubo.STORY #1285.  如果要浏览数据，则直接返回
				sResult = sResult + " and 1 = 2 ";
				return sResult;
			}
			//---add by songjie 2011.05.11 需求 859 QDV4赢时胜（深圳）2011年03月30日01_A---//
            
            if (this.filterType.sOptionCode.length() != 0) {
                sResult = sResult + " and a.FOptionCode like '" +
                    filterType.sOptionCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sOptionName.length() != 0) {
                sResult = sResult + " and a.FOptionName like '" +
                    filterType.sOptionName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sCatCode.length() != 0) { //品种类型
                sResult = sResult + " and se1.FCatCode like '" +
                    filterType.sCatCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sSubCatCode.length() != 0) { //品种子类型
                sResult = sResult + " and se1.FSubCatCode like '" +
                    filterType.sSubCatCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sCuryCode.length() != 0) { //期权币种
            	//edit by songjie 2011.09.22 BUG 2656 QDV4兴业银行2011年9月06日01_B
                sResult = sResult + " and se1.FTradeCury like '" +
                    filterType.sCuryCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sTSecurityCode.length() != 0) { //指数代码
                sResult = sResult + " and a.FTSecurityCode like '" +
                    filterType.sTSecurityCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sDepDurCode.length() != 0) { //期限代码
                sResult = sResult + " and a.FDepDurCode like '" +
                    filterType.sDepDurCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.sExchangeCode.length() != 0) { //交易所代码
                sResult = sResult + " and se1.FExchangeCode like '" +
                    filterType.sExchangeCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.dtExpiryDate != null && //行权日/到期日
                ! ( (YssFun.formatDate(this.filterType.dtExpiryDate, "yyyy-MM-dd")).equals("9998-12-31"))) {
                sResult = sResult + " and a.FExpiryDate = " +
                    dbl.sqlDate(this.filterType.dtExpiryDate);
            }
            //edit by licai 20101117 BUG #175 筛选状态时，界面中所有下拉框应显示为所有 
            if (this.filterType.sExecuteTypeCode.length() != 0 && !this.filterType.sExecuteTypeCode.equals("99")) { //行权方式
                sResult = sResult + " and a.FExecuteTypeCode like '" +
                    filterType.sExecuteTypeCode.replaceAll("'", "''") + "%'";
            }
            
            if (this.filterType.sTradeTypeCode.length() != 0 && !this.filterType.sTradeTypeCode.equals("99")) { //
                sResult = sResult + " and a.FTradeTypeCode like '" +
                    filterType.sTradeTypeCode.replaceAll("'", "''") + "%'";
            }
          //edit by licai 20101117 BUG #175 =====================================end
            if (this.filterType.sBailType.length() != 0 &&
                !this.filterType.sBailType.equals("99")) { //保证金类型
                sResult = sResult + " and a.FBailType like '" +
                    filterType.sBailType.replaceAll("'", "''") + "%'";
            }
            //---add by songjie 2011.09.22 BUG 2656 QDV4兴业银行2011年9月06日01_B start---//
            if(this.filterType.dExercisePrice != null){//标的价格
            	sResult = sResult + " and a.FEXERCISEPRICE = " + this.filterType.dExercisePrice;
            }
            //update by guolongchao 20120315 bug 3764 添加放大倍数this.filterType.dMultiple.doubleValue()!=0的条件限制
            if(this.filterType.dMultiple != null && this.filterType.dMultiple.doubleValue()!=0){//放大倍数
            	sResult = sResult + " and a.FMULTIPLE = " + this.filterType.dMultiple;
            }
            if(this.filterType.dBailFix != null){//每手固定保证金
            	sResult = sResult + " and a.FBAILFIX = " + this.filterType.dBailFix;
            }
            if(this.filterType.dBailScale != null){//保证金比例
            	sResult = sResult + " and a.FBAILSCALE = " + this.filterType.dBailScale;
            }
            if(this.filterType.dBeginBail != null){//初始保证金
            	sResult = sResult + " and a.FBEGINBAIL = " + this.filterType.dBeginBail;
            }
            //---add by songjie 2011.09.22 BUG 2656 QDV4兴业银行2011年9月06日01_B end---//
        }
        return sResult;
    }

    /**
     * 读后台的记录
     * @return ResultSet
     * @throws YssException
     */

    public void setResultSetAttr(ResultSet rs) throws YssException, SQLException {

        this.sOptionCode = rs.getString("FOptionCode");
        this.sOptionName = rs.getString("FOptionName");
        this.sCatCode = rs.getString("FCatCode");
        this.sSubCatCode = rs.getString("FSubCatCode");
        this.sCuryCode = rs.getString("FTradeCury");
        this.sTSecurityCode = rs.getString("FTSecurityCode");
        this.sDepDurCode = rs.getString("FDepDurCode");
        this.dtExpiryDate = rs.getDate("FExpiryDate");
        this.sTradeTypeCode = rs.getString("FTradeTypeCode");
        this.sExecuteTypeCode = rs.getString("FExecuteTypeCode");

        this.dExercisePrice = rs.getBigDecimal("FExercisePrice");
        this.dMultiple = rs.getBigDecimal("FMultiple");
        this.dBeginBail = rs.getBigDecimal("FBeginBail");

        this.sCountryCode = rs.getString("FCountryCode");
        this.sExchangeCode = rs.getString("FExchangeCode");
        this.sDesc = rs.getString("FDesc");

        this.sBailType = rs.getString("FBailType");
        this.dBailScale = rs.getBigDecimal("FBailScale");
        this.dBailFix = rs.getBigDecimal("FBailFix");

        this.sCatName = rs.getString("FCatName");
        //add by songjie 2011.12.20 BUG 2923 QDV4赢时胜(测试)2011年10月11日05_B 添加FSubCatName为空的数据获取方式
        this.sSubCatName = (rs.getString("FSubCatName") == null)? "" : rs.getString("FSubCatName");
        this.sCuryName = rs.getString("FCuryName");
        this.sTSecurityName = (rs.getString("FTSecurityName") == null ? "" : rs.getString("FTSecurityName"));
        this.sDepDurName = (rs.getString("FDepDurName") == null ? "" : rs.getString("FDepDurName"));
        this.sExchangeName = (rs.getString("FExchangeName") == null ? "" : rs.getString("FExchangeName")); //交易所名
        this.sBailTypeName = (rs.getString("FBailTypeName") == null ? "" : rs.getString("FBailTypeName")); //保证金类型名
        this.sExecuteTypeName = (rs.getString("FExecuteTypeName") == null ? "" : rs.getString("FExecuteTypeName")); //行权方式名
        this.sTradeTypeName = (rs.getString("FTradeTypeName") == null ? "" : rs.getString("FTradeTypeName")); //期权交易类型名
        super.setRecLog(rs);
    }

    public String getSTradeTypeCode() {
        return sTradeTypeCode;
    }

    public String getSTSecurityCode() {
        return sTSecurityCode;
    }

    public String getSSubCatCode() {
        return sSubCatCode;
    }

    public String getSRecycled() {
        return sRecycled;
    }

    public String getSOptionName() {
        return sOptionName;
    }

    public String getSOptionCode() {
        return sOptionCode;
    }

    public String getSOldOptionCode() {
        return sOldOptionCode;
    }

    public String getSExecuteTypeCode() {
        return sExecuteTypeCode;
    }

    public String getSExchangeCode() {
        return sExchangeCode;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSDepDurCode() {
        return sDepDurCode;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public String getSCountryCode() {
        return sCountryCode;
    }

    public String getSCatCode() {
        return sCatCode;
    }

    public OptionContractBean getFilterType() {
        return filterType;
    }

    public Date getDtExpiryDate() {
        return dtExpiryDate;
    }

    public BigDecimal getDMultiple() {
        return dMultiple;
    }

    public BigDecimal getDExercisePrice() {
        return dExercisePrice;
    }

    public void setDBeginBail(BigDecimal dBeginBail) {
        this.dBeginBail = dBeginBail;
    }

    public void setSTradeTypeCode(String sTradeTypeCode) {
        this.sTradeTypeCode = sTradeTypeCode;
    }

    public void setSTSecurityCode(String sTSecurityCode) {
        this.sTSecurityCode = sTSecurityCode;
    }

    public void setSSubCatCode(String sSubCatCode) {
        this.sSubCatCode = sSubCatCode;
    }

    public void setSRecycled(String sRecycled) {
        this.sRecycled = sRecycled;
    }

    public void setSOptionName(String sOptionName) {
        this.sOptionName = sOptionName;
    }

    public void setSOptionCode(String sOptionCode) {
        this.sOptionCode = sOptionCode;
    }

    public void setSOldOptionCode(String sOldOptionCode) {
        this.sOldOptionCode = sOldOptionCode;
    }

    public void setSExecuteTypeCode(String sExecuteTypeCode) {
        this.sExecuteTypeCode = sExecuteTypeCode;
    }

    public void setSExchangeCode(String sExchangeCode) {
        this.sExchangeCode = sExchangeCode;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSDepDurCode(String sDepDurCode) {
        this.sDepDurCode = sDepDurCode;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setSCountryCode(String sCountryCode) {
        this.sCountryCode = sCountryCode;
    }

    public void setSCatCode(String sCatCode) {
        this.sCatCode = sCatCode;
    }

    public void setFilterType(OptionContractBean filterType) {
        this.filterType = filterType;
    }

    public void setDtExpiryDate(Date dtExpiryDate) {
        this.dtExpiryDate = dtExpiryDate;
    }

    public void setDMultiple(BigDecimal dMultiple) {
        this.dMultiple = dMultiple;
    }

    public void setDExercisePrice(BigDecimal dExercisePrice) {
        this.dExercisePrice = dExercisePrice;
    }

    public void setSFUType(String sFUType) {
        this.sFUType = sFUType;
    }

    public void setSBailType(String sBailType) {
        this.sBailType = sBailType;
    }

    public void setDBailScale(BigDecimal dBailScale) {
        this.dBailScale = dBailScale;
    }

    public void setDBailFix(BigDecimal dBailFix) {
        this.dBailFix = dBailFix;
    }

    public void setSTSecurityName(String sTSecurityName) {
        this.sTSecurityName = sTSecurityName;
    }

    public void setSSubCatName(String sSubCatName) {
        this.sSubCatName = sSubCatName;
    }

    public void setSExchangeName(String sExchangeName) {
        this.sExchangeName = sExchangeName;
    }

    public void setSDepDurName(String sDepDurName) {
        this.sDepDurName = sDepDurName;
    }

    public void setSCuryName(String sCuryName) {
        this.sCuryName = sCuryName;
    }

    public void setSCatName(String sCatName) {
        this.sCatName = sCatName;
    }

    public void setSFUTypeName(String sFUTypeName) {
        this.sFUTypeName = sFUTypeName;
    }

    public void setSExecuteTypeName(String sExecuteTypeName) {
        this.sExecuteTypeName = sExecuteTypeName;
    }

    public void setSBailTypeName(String sBailTypeName) {
        this.sBailTypeName = sBailTypeName;
    }

    public void setSTradeTypeName(String sTradeTypeName) {
        this.sTradeTypeName = sTradeTypeName;
    }

    public BigDecimal getDBeginBail() {
        return dBeginBail;
    }

    public String getSFUType() {
        return sFUType;
    }

    public String getSBailType() {
        return sBailType;
    }

    public BigDecimal getDBailScale() {
        return dBailScale;
    }

    public BigDecimal getDBailFix() {
        return dBailFix;
    }

    public String getSTSecurityName() {
        return sTSecurityName;
    }

    public String getSSubCatName() {
        return sSubCatName;
    }

    public String getSExchangeName() {
        return sExchangeName;
    }

    public String getSDepDurName() {
        return sDepDurName;
    }

    public String getSCuryName() {
        return sCuryName;
    }

    public String getSCatName() {
        return sCatName;
    }

    public String getSFUTypeName() {
        return sFUTypeName;
    }

    public String getSExecuteTypeName() {
        return sExecuteTypeName;
    }

    public String getSBailTypeName() {
        return sBailTypeName;
    }

    public String getSTradeTypeName() {
        return sTradeTypeName;
    }

}
