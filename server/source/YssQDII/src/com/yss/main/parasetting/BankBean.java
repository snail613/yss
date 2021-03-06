package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title:BankBean 银行 </p>
 * <p>Description:1 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class BankBean
    extends BaseDataSettingBean implements IDataSetting {

    private String bankCode = ""; //银行代码
    private String bankName; //银行名称
    private String bankShortName = ""; //银行简称
    private String bankAccount = ""; //银行帐号
    private String Address; //办公地址
    private String Postcode; //邮政编码
    private String bankDesc; //描述

    private String portCode = "";
    private String sRecycled = "";

    private String assetGroupCode = "";//组合群代码 panjunfang add 20090903

    private String oldBankCode;
    private String linkMans;
    private BankBean filterType;

    public BankBean() {
    }

    /**
     * parseRowStr
     * 解析银行数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
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
                if (sRowStr.split("\r\t").length == 3) {
                    this.linkMans = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.bankCode = reqAry[0];
            this.bankName = reqAry[1];
            this.bankShortName = reqAry[2];
            this.Address = reqAry[3];
            this.Postcode = reqAry[4];
            this.bankDesc = reqAry[5].replaceAll("\f<>", "\r\n");//edited by zhouxiang MS1405
            this.bankAccount = reqAry[6];
            super.checkStateId = Integer.parseInt(reqAry[7]);
            this.oldBankCode = reqAry[8];
            this.portCode = reqAry[9];
            this.assetGroupCode = reqAry[10];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new BankBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析银行设置出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.bankCode).append("\t");
        buf.append(this.bankName).append("\t");
        buf.append(this.bankShortName).append("\t");
        buf.append(this.Address).append("\t");
        buf.append(this.Postcode).append("\t");
        buf.append(this.bankDesc).append("\t");
        buf.append(this.bankAccount).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查银行输入数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Bank"),
                               "FBankCode",
                               this.bankCode, this.oldBankCode);
    }

    /**
     * saveSetting
     * 更新银行数据
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
           conn.setAutoCommit(false);
           bTrans = true;
           if (btOper == YssCons.OP_ADD) {
           strSql =
                    "insert into " + pub.yssGetTableName("Tb_Para_Bank") + "(FBankCode,FBankName,FBankShortName,FOfficeAddr,FPostalCode,FDesc," +
                    "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.bankCode) + "," +
                    dbl.sqlString(this.bankName) + "," +
                    dbl.sqlString(this.bankShortName) + "," +
                    dbl.sqlString(this.Address) + "," +
                    dbl.sqlString(this.Postcode) + "," +
                    dbl.sqlString(this.bankDesc) + "," +
                    (pub.getSysCheckState()?"0":"1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";

           }
           else if (btOper == YssCons.OP_EDIT) {
     strSql = "update " + pub.yssGetTableName("Tb_Para_Bank") + " set FBankCode = " +
                    dbl.sqlString(this.bankCode) + ",FBankName = " +
                    dbl.sqlString(this.bankName) + ",FBankShortName = " +
                    dbl.sqlString(this.bankShortName.length()==0 ? " " : this.bankShortName) + ",FOfficeAddr = " +
                    dbl.sqlString(this.Address) + ",FPostalCode = " +
                    dbl.sqlString(this.Postcode) + ",FDesc = " +
                    dbl.sqlString(this.bankDesc) + ",FCheckState = " +
                    (pub.getSysCheckState()?"0":"1") + ",FCreator = " +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                    " where FBankCode = " +
                    dbl.sqlString(this.oldBankCode);
              System.out.println("修改的语句为：" + strSql);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Bank")  + " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FBankCode = " +
                    dbl.sqlString(this.oldBankCode);

           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Bank")  + " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FBankCode = " +
                    dbl.sqlString(this.bankCode);
           }
             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
       // 联系人处理
     if (btOper == YssCons.OP_EDIT && (this.bankCode != this.oldBankCode )) {
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FRelaCode = " +
                      dbl.sqlString(this.bankCode) +
                      " where FRelaCode = " +
                      dbl.sqlString(this.oldBankCode) +
                      " and FRelaType = 'Bank'";
                dbl.executeSql(strSql);
             }

             if (this.linkMans != null) {
                if (btOper == YssCons.OP_ADD || btOper == YssCons.OP_EDIT) {
                   LinkManBean linkMan = new LinkManBean();
                   linkMan.setYssPub(pub);
                   linkMan.saveMutliSetting(this.linkMans);
                }
             }
             if (btOper == YssCons.OP_DEL) {
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " + this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FRelaCode = " +
                     dbl.sqlString(this.bankCode) +
                     " and frelatype = 'Bank'";

                dbl.executeSql(strSql);
             }
             if (btOper == YssCons.OP_AUDIT) {
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
                      this.checkStateId +   ", FCheckUser = " +
                      dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                      "' where frelacode = " +
                      dbl.sqlString(this.bankCode ) +
                      " and frelatype = 'Bank'";

                dbl.executeSql(strSql);
             }

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新银行信息出错", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }
     }*/

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
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.bankCode.length() != 0) {
                sResult = sResult + " and a.FBankCode like '" + // wdy 添加表别名:a
                    filterType.bankCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bankName.length() != 0) {
                sResult = sResult + " and a.FBankName like '" +
                    filterType.bankName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bankShortName.length() != 0) {
                sResult = sResult + " and a.FBankShortName like '" +
                    filterType.bankShortName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.Address.length() != 0) {
                sResult = sResult + " and a.FOfficeAddr like '" +
                    filterType.Address.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.Postcode.length() != 0) {
                sResult = sResult + " and a.FPostalCode like '" +
                    filterType.Postcode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bankDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.bankDesc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and FPortCode = " +
                    dbl.sqlString(filterType.portCode);
            }

        }
        return sResult;
    }

    public void setManagerAttr(ResultSet rs) throws SQLException {
        this.bankCode = rs.getString("FBankCode") + "";
        this.bankName = rs.getString("FBankName") + "";
        this.bankShortName = rs.getString("FBankShortName") + "";
        this.Address = rs.getString("FOfficeAddr") + "";
        this.Postcode = rs.getString("FPostalCode") + "";
        this.bankDesc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setBankAttr(rs);
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
            throw new YssException("获取银行数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月23号
     * 修改人：单亮
     * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            "(select FBankCode from " + pub.yssGetTableName("Tb_Para_Bank") +
            //修改前的代码
            //" where FCheckState <> 2 group by FBankCode) x join" +
            //修改后的代码
            //----------------------------
            "  group by FBankCode) x join" +
            //----------------------------
            " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Bank") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y on x.FBankCode = y.FBankCode " +
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql =
            "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Bank") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    public void setBankAttr(ResultSet rs) throws SQLException {
        this.bankCode = rs.getString("FBankCode") + "";
        this.bankName = rs.getString("FBankName") + "";
        this.bankShortName = rs.getString("FBankShortName") + "";
        this.Address = rs.getString("FOfficeAddr") + "";
        this.Postcode = rs.getString("FPostalCode") + "";
        this.bankDesc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    public String getListViewData2() throws YssException {
        String strSql = "";
        strSql =
            "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Bank") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

        return this.builderListViewData(strSql);

    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "银行帐号\t银行名称"; //奇怪,为什么要导银行帐号?
            strSql = "select y.* from " +
                "(select FCashAccCode from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                //buildFilterSql() + " and FCheckState = 1 " +
                (buildFilterSql().trim().length() > 0 ? buildFilterSql() : " where 1=1 ") + //增加对它的判断,防止筛选返回值等于0 by leeyu 080601
                " and FCheckState = 1 " +
                " group by FCashAccCode) x join" +
                "(select a.*," +
                "b.FBankName,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " a left join (select FBankCode,FBankName from " +
                pub.yssGetTableName("Tb_Para_Bank") +
                " ) b on a.FBankCode = b.FBankCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                " where FCheckState = 1 " +
                ") y on x.FCashAccCode = y.FCashAccCode " +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FBankAccount")).append("\t");
                bufShow.append(rs.getString("FBankName")).append(YssCons.
                    YSS_LINESPLITMARK);
                this.bankCode = rs.getString("FBankCode") + "";
                this.bankName = rs.getString("FBankName") + "";
                this.bankAccount = rs.getString("FBankAccount") + "";
                //        this.bankAccount = this.bankAccount + "\n5\r" + this.bankCode;
                super.setRecLog(rs);

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
            throw new YssException("获取银行帐号信息出错", e);
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_Bank") +
                "(FBankCode,FBankName,FBankShortName,FOfficeAddr,FPostalCode,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.bankCode) + "," +
                dbl.sqlString(this.bankName) + "," +
                dbl.sqlString(this.bankShortName.length() == 0 ? " " :
                              this.bankShortName) + "," +
                dbl.sqlString(this.Address) + "," +
                dbl.sqlString(this.Postcode) + "," +
                dbl.sqlString(this.bankDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            dbl.executeSql(strSql);

            if (this.linkMans != null) {

                // LinkManBean linkMan = new LinkManBean();
                // linkMan.setYssPub(pub);
                linkMan.saveMutliSetting(this.linkMans);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Bank");
            filterType.setRelaCode(this.bankCode);
            filterType.setStartDate(YssFun.toDate("1900-1-1"));
            linkMan.setFilterType(filterType);
            //filterType = linkMan.getFilterType();//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("增加银行信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
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
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {

            LinkManBean bFilterType = new LinkManBean();
            bFilterType.setRelaType("Bank");
            bFilterType.setRelaCode(this.bankCode);
            bFilterType.setStartDate(YssFun.toDate("1900-1-1"));
            linkMan.setFilterType(bFilterType);
            bFilterType = linkMan.getFilterType();
            this.setBSubData(linkMan.getListViewData1());

            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Bank") +
                " set FBankCode = " +
                dbl.sqlString(this.bankCode) + ",FBankName = " +
                dbl.sqlString(this.bankName) + ",FBankShortName = " +
                dbl.sqlString(this.bankShortName.length() == 0 ? " " :
                              this.bankShortName) + ",FOfficeAddr = " +
                dbl.sqlString(this.Address) + ",FPostalCode = " +
                dbl.sqlString(this.Postcode) + ",FDesc = " +
                dbl.sqlString(this.bankDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FBankCode = " +
                dbl.sqlString(this.oldBankCode);
            dbl.executeSql(strSql);
			/**shashijie 2012-7-2 STORY 2475 */
            if (!this.bankCode.equals(this.oldBankCode)) {
			/**end*/
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                    " set FRelaCode = " +
                    dbl.sqlString(this.bankCode) +
                    " where FRelaCode = " +
                    dbl.sqlString(this.oldBankCode) +
                    " and FRelaType = 'Bank'";
                dbl.executeSql(strSql);
            }

            if (this.linkMans != null) {
                // LinkManBean linkMan = new LinkManBean();
                //linkMan.setYssPub(pub);
                linkMan.saveMutliSetting(this.linkMans);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Bank");
            filterType.setRelaCode(this.bankCode);
            filterType.setStartDate(YssFun.toDate("1900-1-1"));
            linkMan.setFilterType(filterType);
            //filterType = linkMan.getFilterType();//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("修改银行信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Bank") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FBankCode = " +
                dbl.sqlString(this.oldBankCode);
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FRelaCode = " +
                dbl.sqlString(this.bankCode) +
                " and frelatype = 'Bank'";
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Bank");
            filterType.setRelaCode(this.bankCode);
            filterType.setStartDate(YssFun.toDate("1900-1-1"));
            linkMan.setFilterType(filterType);
            //filterType = linkMan.getFilterType();//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("删除银行信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改时间：2008年3月23号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      LinkManBean linkMan = new LinkManBean();
//      linkMan.setYssPub(pub);
//      try {
//         conn.setAutoCommit(false);
//         bTrans = true;
//         strSql = "update " + pub.yssGetTableName("Tb_Para_Bank") +
//               " set FCheckState = " +
//               this.checkStateId +
//               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "' where FBankCode = " +
//               dbl.sqlString(this.bankCode);
//         dbl.executeSql(strSql);
//         strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "' where frelacode = " +
//               dbl.sqlString(this.bankCode) +
//               " and frelatype = 'Bank'";
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//
//         LinkManBean filterType = new LinkManBean();
//         filterType.setRelaType("Bank");
//         filterType.setRelaCode(this.bankCode);
//         filterType.setStartDate(YssFun.toDate("1900-1-1"));
//         linkMan.setFilterType(filterType);
//         filterType = linkMan.getFilterType();
//         this.setASubData(linkMan.getListViewData1());
//
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核银行信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //-------------------begin
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        LinkManBean linkMan = new LinkManBean();
        String[] arrData = null;
        linkMan.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Para_Bank") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FBankCode = " +
                        dbl.sqlString(this.bankCode);
                    dbl.executeSql(strSql);
                    strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where frelacode = " +
                        dbl.sqlString(this.bankCode) +
                        " and frelatype = 'Bank'";
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (bankCode != null && (!bankCode.equalsIgnoreCase(""))) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Bank") +
                    " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FBankCode = " +
                    dbl.sqlString(this.bankCode);
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where frelacode = " +
                    dbl.sqlString(this.bankCode) +
                    " and frelatype = 'Bank'";
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Bank");
            filterType.setRelaCode(this.bankCode);
            filterType.setStartDate(YssFun.toDate("1900-1-1"));
            linkMan.setFilterType(filterType);
            filterType = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());
        } catch (Exception e) {
            throw new YssException("审核银行信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //-------------------------end

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
        BankBean befEditBean = new BankBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FBankCode from " + pub.yssGetTableName("Tb_Para_Bank") +
                " where FCheckState <> 2 group by FBankCode) x join" +
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Bank") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  FBankCode =" + dbl.sqlString(this.oldBankCode) +
                ") y on x.FBankCode = y.FBankCode " +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.bankCode = rs.getString("FBankCode") + "";
                befEditBean.bankName = rs.getString("FBankName") + "";
                befEditBean.bankShortName = rs.getString("FBankShortName") + "";
                befEditBean.Address = rs.getString("FOfficeAddr") + "";
                befEditBean.Postcode = rs.getString("FPostalCode") + "";
                befEditBean.bankDesc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

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
                        pub.yssGetTableName("Tb_Para_Bank") +
                        " where FBankCode = " +
                        dbl.sqlString(this.oldBankCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                    strSql = "delete from " +
                        pub.yssGetTableName("tb_para_linkman") +
                        " where FRelaCode = " +
                        dbl.sqlString(this.bankCode) +
                        " and frelatype = 'Bank'";

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而oldBankCode不为空，则按照oldBankCode来执行sql语句
            else if (oldBankCode != "" && oldBankCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_Bank") +
                    " where FBankCode = " +
                    dbl.sqlString(this.oldBankCode);
                //执行sql语句
                dbl.executeSql(strSql);
                strSql = "delete from " +
                    pub.yssGetTableName("tb_para_linkman") +
                    " where FRelaCode = " +
                    dbl.sqlString(this.bankCode) +
                    " and frelatype = 'Bank'";

                //执行sql语句
                dbl.executeSql(strSql);
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
        String strRe = "";//存放返回到前台的字符串
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码即表前缀
        try{
            pub.setPrefixTB(this.assetGroupCode);//将前台传过来的组合群代码设置为表前缀
            strRe = this.getListViewData2();//将该组合群对应的银行列表返回至前台
        }catch(Exception e){
            throw new YssException("获取银行帐号信息出错", e);
        }finally{
            pub.setPrefixTB(sPrefixTB);//还原公共变的里的组合群代码即表前缀
        }
        return strRe;
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
