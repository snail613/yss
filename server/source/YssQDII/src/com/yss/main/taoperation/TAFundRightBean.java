package com.yss.main.taoperation;

import java.sql.*;
import java.util.Date;
import java.math.BigDecimal;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title:基金权益信息设置 </p>
 *
 * <p>Description: MS00023 QDV4.1赢时胜（上海）2009年4月20日23_A  国内TA业务</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author panjunfang add 20090703
 * @version 1.0
 */
public class TAFundRightBean
    extends BaseDataSettingBean implements IDataSetting {

    private Date dtRightDate;       //除权日期
    private Date dtSettleDate;      //结算日期
    private String strRightTypeCode = "";   //权益类型代码
    private String strRightTypeName = "";   //权益类型名称
    private BigDecimal dblRightMoney = new BigDecimal(0);   //权益金额
    private BigDecimal dblRightAmount= new BigDecimal(0);  //权益份额
    private BigDecimal dblRightUnitMoney= new BigDecimal(0);  //单位权益金额
    private String strCashAccountCode = ""; //现金账户代码
    private String strCashAccountName = ""; //现金账户名称
    private String strPortCode = "";    //组合代码
    private String strPortName = "";    //组合名称
    private String strPortClsCode = ""; //组合分级代码
    private String StrPortClsName = ""; //组合分级名称

    private TAFundRightBean filterType;
    private Date dtOldRightDate;
    private String strOldRightTypeCode;
    private String strOldPortCode;
    private String strOldPortClsCode;
    private String sRecycled = "";

    public TAFundRightBean() {
    }

    /**
     * 检查数据合法性
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_TA_FUNDRight"),
                               "FPortCode,FPortClsCode,FRightDate,FRightType",
                               this.strPortCode + "," +
                               this.strPortClsCode + "," +
                               YssFun.formatDate(this.dtRightDate) + "," +
                               this.strRightTypeCode,
                               this.strOldPortCode + "," +
                               this.strOldPortClsCode + "," +
                               YssFun.formatDate(this.dtOldRightDate) + "," +
                               this.strOldRightTypeCode);
    }

    /**
     * 新增基金权益信息
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = true; //代表是否回滚事务
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_TA_FUNDRight") +
                "(FRightDate,FSettleDate,FRightType,FRightMoney,FRightAmount,FRightUnitMoney,FCashAccount,FPortCode,FPortClsCode," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" +
                dbl.sqlDate(this.dtRightDate) + "," +           //FRightDate
                dbl.sqlDate(this.dtSettleDate) + "," +          //FSettleDate
                dbl.sqlString(this.strRightTypeCode.trim().length() == 0 ? " " : this.strRightTypeCode) + "," +     //FRightType
                this.dblRightMoney + "," +                      //FRightMoney
                this.dblRightAmount + "," +                     //FRightAmount
                this.dblRightUnitMoney + "," +                  //FRightUnitMoney
                dbl.sqlString(this.strCashAccountCode.trim().length() == 0 ? " " : this.strCashAccountCode) + "," + //FCashAccount
                dbl.sqlString(this.strPortCode.trim().length() == 0 ? " " : this.strPortCode) + "," +               //FPortCode
                dbl.sqlString(this.strPortClsCode.trim().length() == 0 ? " " : this.strPortClsCode) + "," +         //FPortClsCode
                (pub.getSysCheckState() ? 0 : 1) + "," +        //FCheckState
                dbl.sqlString(this.creatorCode) + "," +         //FCreator
                dbl.sqlString(this.creatorTime) + "," +         //FCreateTime
                dbl.sqlString( (pub.getSysCheckState() ? " " : this.creatorCode)) + //FCheckUser
                ")";

            con.setAutoCommit(false);
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增基金权益信息出错！",e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

    /**
     * 更新基金权益信息数据
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //代表是否回滚事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_TA_FUNDRight") +
                " set FRightDate = " + dbl.sqlDate(this.dtRightDate) +
                ", FSettleDate = " + dbl.sqlDate(this.dtSettleDate) +
                ", FRightType = " + dbl.sqlString(this.strRightTypeCode) +
                ", FRightMoney = " + this.dblRightMoney +
                ", FRightAmount = " + this.dblRightAmount +
                ", FRightUnitMoney = " + this.dblRightUnitMoney +
                ", FCashAccount = " + dbl.sqlString(this.strCashAccountCode) +
                ", FPortCode = " + dbl.sqlString(this.strPortCode) +
                ", FPortClsCode = " + dbl.sqlString(this.strPortClsCode) +
                ", FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FRightDate = " + dbl.sqlDate(this.dtOldRightDate) +
                " and FRightType = " + dbl.sqlString(this.strOldRightTypeCode) +
                " and FPortCode = " + dbl.sqlString(this.strOldPortCode) +
                " and FPortClsCode = " + dbl.sqlString(this.strOldPortClsCode);

            conn.setAutoCommit(false);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新基金权益信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }
    /**
     * 删除基金权益信息，将数据放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //代表是否回滚事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("Tb_TA_FUNDRight") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FRightDate = " + dbl.sqlDate(this.dtOldRightDate) +
                " and FRightType = " + dbl.sqlString(this.strOldRightTypeCode) +
                " and FPortCode = " + dbl.sqlString(this.strOldPortCode) +
                " and FPortClsCode = " + dbl.sqlString(this.strOldPortClsCode);
            conn.setAutoCommit(false);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除基金权益信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    /**
     * 审核、反审核基金权益信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";                     //sql语句
        String[] arrData = null;                //按条存放所有要审核的数据
        boolean bTrans = true;                  //代表是否回滚事物，默认回滚
        Connection conn = dbl.loadConnection(); //数据库连接
        Statement st = null;                    //游标资源，用于批量处理
        try {
            conn.setAutoCommit(false);          //设置事物不自动处理
            st = conn.createStatement();        //获取游标资源

            //回收站变量中存储了请求的数据，如果回收站数据变量有数据，代表有数据需审核
            /**shashijie 2012-7-2 STORY 2475 */
			if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");  //拆解数据
                //循环处理数据的更新
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);   //解析数据
                    strSql = "update " + pub.yssGetTableName("Tb_TA_FUNDRight")
                        + " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FRightDate = " + dbl.sqlDate(this.dtRightDate) +
                        " and FRightType = " + dbl.sqlString(this.strRightTypeCode) +
                        " and FPortCode = " + dbl.sqlString(this.strPortCode) +
                        " and FPortClsCode = " + dbl.sqlString(this.strPortClsCode);
                    st.addBatch(strSql);    //添加到批处理
                }
                st.executeBatch();          //执行批处理
            }
            conn.commit();                  //提交事物
            bTrans = false;                 //不回滚事物
            conn.setAutoCommit(true);       //事物自动处理
        } catch (Exception e) {
            throw new YssException("审核基金权益信息出错！", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * 清空回收站，将数据从数据库中删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = true; //自动回滚事物
        Connection conn = dbl.loadConnection();
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            st = conn.createStatement();
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_TA_FUNDRight") +
                        " where FRightDate = " + dbl.sqlDate(this.dtRightDate) +
                        " and FRightType = " + dbl.sqlString(this.strRightTypeCode) +
                        " and FPortCode = " + dbl.sqlString(this.strPortCode) +
                        " and FPortClsCode = " + dbl.sqlString(this.strPortClsCode);
                    st.addBatch(strSql);
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除基金权益信息出错！", e);
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
        String strSql = "";
        //------ modify by wangzuochun 2010.07.12   MS01429    当组合设置中含有多条代码相同、启用日期不同的信息时，新建TA基金权益信息出现多条数据    QDV4国内(测试)2010年07月09日01_B   
        strSql = "select fr.*,ca.FCashAccName,d.FPortName,pc.FPortClsName,usa.FUserName as FCreatorName,usb.FUserName as FCheckUserName, " +
            " v.FVocName as FRightTypeName from " + pub.yssGetTableName("Tb_TA_FUNDRight") +
            " fr left join (select FCashAccCode,FCashAccName,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_CashAccount") + " " +
            " where FCheckState = 1 and FState =0 group by FCashAccCode,FCashAccName)" +
            " ca on ca.FCashAccCode = fr.FCashAccount " + 
            //----------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            
     
            " left join (select FPortCode, FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " where  FCheckState = 1 ) d on fr.FPortCode = d.FPortCode " +
          
            //end by lidaolong
            //----------------------------------------------------------------------------------------------------
            
            " left join Tb_Sys_UserList usa on fr.FCreator = usa.FUserCode " +
            " left join " + pub.yssGetTableName("Tb_TA_PortCls") + " pc on pc.FPortClsCode = fr.FPortClsCode" +
            " left join Tb_Sys_UserList usb on fr.FCheckUser = usb.FUserCode " +
            " left join Tb_Fun_Vocabulary v on " + dbl.sqlToChar("fr.FRightType") + " = v.FVocCode and v.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_TA_FundRightType) +
            buildFilterSql() + " order by fr.FCheckState, fr.FCreateTime desc";
        //------------------------------MS01429-----------------------------------//
        return this.builderListViewData(strSql);
    }

    /**
     * buildFilterSql
     *用于筛选的条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.filterType.dtRightDate != null &&
                    !this.filterType.dtRightDate.equals(YssFun.toDate("9998-12-31"))) {
                    sResult = sResult + " and fr.FRightDate =" +
                        dbl.sqlDate(filterType.dtRightDate);
                }
                if (this.filterType.dtSettleDate != null &&
                    !this.filterType.dtSettleDate.equals(YssFun.toDate("9998-12-31"))) {
                    sResult = sResult + " and fr.FSettleDate =" +
                        dbl.sqlDate(filterType.dtSettleDate);
                }
                if (this.filterType.strRightTypeCode != null && this.filterType.strRightTypeCode.length() != 0 &&
                    !this.filterType.strRightTypeCode.equalsIgnoreCase("99")) {
                    sResult = sResult + " and fr.FRightType like '" +
                        filterType.strRightTypeCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.dblRightMoney.compareTo(new BigDecimal(0)) > 0) {
                    sResult = sResult + " and fr.FRightMoney = " +
                        filterType.dblRightMoney;
                }
                if (this.filterType.dblRightAmount.compareTo(new BigDecimal(0)) > 0) {
                    sResult = sResult + " and fr.FRightAmount = " +
                        filterType.dblRightAmount;
                }
                if (this.filterType.dblRightUnitMoney.compareTo(new BigDecimal(0)) > 0) {
                    sResult = sResult + " and fr.FRightUnitMoney = " +
                        filterType.dblRightUnitMoney;
                }
                if (this.filterType.strCashAccountCode != null && this.filterType.strCashAccountCode.length() != 0) {
                    sResult = sResult + " and fr.FCashAccount like '" +
                        filterType.strCashAccountCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strPortCode != null && this.filterType.strPortCode.length() != 0) {
                    sResult = sResult + " and fr.FPortCode like '" +
                        filterType.strPortCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strPortClsCode != null && this.filterType.strPortClsCode.length() != 0) {
                    sResult = sResult + " and fr.FPortClsCode like '" +
                        filterType.strPortClsCode.replaceAll("'", "''") + "%'";
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选基金权益信息数据出错！", e);
        }
        return sResult;
    }

    /**
     * 通过SQL语句获取数据，并将数据组装成客户端可解析的字符串
     * @param strSql String
     * @return String
     * @throws YssException
     */
    private String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setFundRightAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_TA_FundRightType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取基金权益信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * setFundRightAttr
     * 通过结果集给每个成员属性赋值
     * @param rs ResultSet
     */
    private void setFundRightAttr(ResultSet rs) throws YssException {
        try {
            this.dtRightDate = rs.getDate("FRightDate");
            this.dtSettleDate = rs.getDate("FSettleDate");
            this.strRightTypeCode = rs.getString("FRightType");
            this.strRightTypeName = rs.getString("FRightTypeName");
            this.dblRightMoney = rs.getBigDecimal("FRightMoney");
            this.dblRightAmount = rs.getBigDecimal("FRightAmount");
            this.dblRightUnitMoney = rs.getBigDecimal("FRightUnitMoney");
            this.strCashAccountCode = rs.getString("FCashAccount");
            this.strCashAccountName = rs.getString("FCashAccName");
            this.strPortCode = rs.getString("FPortCode");
            this.strPortName = rs.getString("FPortName");
            this.strPortClsCode = rs.getString("FPortClsCode");
            this.StrPortClsName = rs.getString("FPortClsName");
            super.setRecLog(rs);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public String getListViewData2() throws YssException {
        return "";
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
     * 解析前台数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.dtRightDate = YssFun.toDate(reqAry[0]);
            this.dtSettleDate = YssFun.toDate(reqAry[1]);
            this.strRightTypeCode = reqAry[2];
            if(YssFun.isNumeric(reqAry[3])){
                this.dblRightMoney = new BigDecimal(reqAry[3]);
            }
            if(YssFun.isNumeric(reqAry[4])){
                this.dblRightAmount = new BigDecimal(reqAry[4]);
            }
            if(YssFun.isNumeric(reqAry[5])){
                this.dblRightUnitMoney = new BigDecimal(reqAry[5]);
            }
            this.strCashAccountCode = reqAry[6];
            this.strPortCode = reqAry[7];
            this.strPortClsCode = reqAry[8];
            this.checkStateId = YssFun.toInt(reqAry[9]);
            this.dtOldRightDate = YssFun.toDate(reqAry[10]);
            this.strOldRightTypeCode = reqAry[11];
            this.strOldPortCode = reqAry[12];
            this.strOldPortClsCode = reqAry[13];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TAFundRightBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析基金权益信息设置出错", e);
        }
    }
    /**
     * 组装发回前台的数据
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(YssFun.formatDate(this.dtRightDate, YssCons.YSS_DATEFORMAT)).append("\t");
        buf.append(YssFun.formatDate(this.dtSettleDate, YssCons.YSS_DATEFORMAT)).append("\t");
        buf.append(this.strRightTypeCode).append("\t");
        buf.append(this.strRightTypeName).append("\t");
        buf.append(this.dblRightMoney).append("\t");
        buf.append(this.dblRightAmount).append("\t");
        buf.append(this.dblRightUnitMoney).append("\t");
        buf.append(this.strCashAccountCode).append("\t");
        buf.append(this.strCashAccountName).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strPortClsCode).append("\t");
        buf.append(this.StrPortClsName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrCashAccountCode() {
        return strCashAccountCode;
    }

    public String getSRecycled() {
        return sRecycled;
    }

    public TAFundRightBean getFilterType() {
        return filterType;
    }

    public Date getDtSettleDate() {
        return dtSettleDate;
    }

    public Date getDtRightDate() {
        return dtRightDate;
    }

    public BigDecimal getDblRightUnitMoney() {
        return dblRightUnitMoney;
    }

    public BigDecimal getDblRightMoney() {
        return dblRightMoney;
    }

    public void setDblRightAmount(BigDecimal dblRightAmount) {
        this.dblRightAmount = dblRightAmount;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrCashAccountCode(String strCashAccountCode) {
        this.strCashAccountCode = strCashAccountCode;
    }

    public void setSRecycled(String sRecycled) {
        this.sRecycled = sRecycled;
    }

    public void setFilterType(TAFundRightBean filterType) {
        this.filterType = filterType;
    }

    public void setDtSettleDate(Date dtSettleDate) {
        this.dtSettleDate = dtSettleDate;
    }

    public void setDtRightDate(Date dtRightDate) {
        this.dtRightDate = dtRightDate;
    }

    public void setDblRightUnitMoney(BigDecimal dblRightUnitMoney) {
        this.dblRightUnitMoney = dblRightUnitMoney;
    }

    public void setDblRightMoney(BigDecimal dblRightMoney) {
        this.dblRightMoney = dblRightMoney;
    }

    public void setStrRightTypeName(String strRightTypeName) {
        this.strRightTypeName = strRightTypeName;
    }

    public void setStrRightTypeCode(String strRightTypeCode) {
        this.strRightTypeCode = strRightTypeCode;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public void setStrCashAccountName(String strCashAccountName) {
        this.strCashAccountName = strCashAccountName;
    }

    public BigDecimal getDblRightAmount() {
        return dblRightAmount;
    }

    public String getStrRightTypeName() {
        return strRightTypeName;
    }

    public String getStrRightTypeCode() {
        return strRightTypeCode;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public String getStrCashAccountName() {
        return strCashAccountName;
    }
}
