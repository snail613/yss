package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.compliance.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title:PortfolioSubBean </p>
 * <p>Description: 组合关联信息</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PortfolioSubBean
    extends BaseDataSettingBean implements IDataSetting {
    private String portCode; //关联表组合代码
    private String oldPortCode; //修改用原关联表组合代码
    private String relaType; //关联类型
    private String subCode; //关联表关联代码
    private String relaGrade; //关联等级
    private java.util.Date startDate; //启用日期
    private java.util.Date oldStartDate; //修改用原启用日期

    public String getPortCode() {
        return portCode;
    }

    public java.util.Date getOldStartDate() {
        return oldStartDate;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setOldStartDate(java.util.Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }
    
    /*
     * add by huangqirong 2012-02-09 story #1284
     */
    public String getRelaType() {
		return relaType;
	}

    /*
     * add by huangqirong 2012-02-09 story #1284
     */
	public void setRelaType(String relaType) {
		this.relaType = relaType;
	}
	
    public PortfolioSubBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
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
     * 获取可用组合关联信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strName = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            if (this.relaType.equalsIgnoreCase("Manager")) {
                strName = "管理人";
                sHeader = "管理人代码\t管理人名称";//edit by songjie 2011.03.14 不显示启用日期数据
                strSql = "select y.* from " +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    "(select FManagerCode,FCheckState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Manager") + " " +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState <> 2 group by FManagerCode,FCheckState) x join" +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                    //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " (select * from " + pub.yssGetTableName("Tb_Para_Manager") + " where FCheckState <> 2 ) a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Manager' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    ")d on a.FManagerCode = d.FSubCode where a.fcheckstate = 1 " +
                    ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FManagerCode") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FManagerName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);//edit by songjie 不显示启用日期数据
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
//                        YssCons.YSS_DATEFORMAT)).append(
//                            YssCons.YSS_LINESPLITMARK);
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    ManagerBean manager = new ManagerBean();
                    manager.setYssPub(pub);
                    manager.setManagerAttr(rs);
                    bufAll.append(manager.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            } else if (this.relaType.equalsIgnoreCase("Trustee")) {
                strName = "托管人";
                sHeader = "托管人代码\t托管人名称\t关联等级";//edit by songjie 2011.03.14 不显示启用日期数据
                strSql = "select y.* from " +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    "(select FTrusteeCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Trustee") + " " +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState <> 2 group by FTrusteeCode,FCheckState) x join" +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                    "(case when d.FRelaGrade = 'primary' then '主托管行' else '次托管行' end) as FRelaGrade from " + //BugNo:0000439 edit by jc
                    //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " (select * from " + pub.yssGetTableName("Tb_Para_Trustee") + " where FCheckState <> 2) a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " join (select FSubCode, FRelaGrade from " + //BugNo:0000439 edit by jc  查询出关联级别
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Trustee' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    ")d on a.FTrusteeCode = d.FSubCode where a.fcheckstate = 1 " +
                    ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FTrusteeCode") + "").trim()).
                        append(
                            "\t");
                    bufShow.append( (rs.getString("FTrusteeName") + "").trim()).
                        append(
                            "\t");
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
//                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
//                        YssCons.YSS_DATEFORMAT)).append(
//                            "\t");
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
                    bufShow.append( (rs.getString("FRelaGrade") + "").trim()).append(
                        YssCons.YSS_LINESPLITMARK); //BugNo:0000439 edit by jc
                    TrusteeBean trustee = new TrusteeBean();
                    trustee.setYssPub(pub);
                    trustee.setTrusteeAttr(rs);
                    bufAll.append(trustee.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            } else if (this.relaType.equalsIgnoreCase("Keeper")) {
                strName = "保管人";
                sHeader = "保管人代码\t保管人名称";//edit by songjie 2011.03.14 不显示启用日期数据
                strSql = "select y.* from " +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    "(select FKeeperCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Keeper") + " " +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState <> 2 group by FKeeperCode,FCheckState) x join" +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                    //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " (select * from " + pub.yssGetTableName("Tb_Para_Keeper") + " where FCheckState <> 2) a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Keeper' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    ")d on a.FKeeperCode = d.FSubCode where a.fcheckstate = 1 " +
                    ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FKeeperCode") + "").trim()).
                        append(
                            "\t");
                    bufShow.append( (rs.getString("FKeeperName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.14 不显示启用日期数据
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
//                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
//                        YssCons.YSS_DATEFORMAT)).append(
//                            YssCons.YSS_LINESPLITMARK);
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
                    KeeperBean keeper = new KeeperBean();
                    keeper.setYssPub(pub);
                    keeper.setKeeperAttr(rs);
                    bufAll.append(keeper.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            } else if (this.relaType.equalsIgnoreCase("Warrantor")) {
                strName = "担保人";
                sHeader = "担保人代码\t担保人名称";//edit by songjie 2011.03.14 不显示启用日期数据
                strSql = "select y.* from " +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    "(select FWarrantorCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Warrantor") + " " +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState <> 2 group by FWarrantorCode,FCheckState) x join" +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                    //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " (select * from " + pub.yssGetTableName("Tb_Para_Warrantor") + " where FCheckState <> 2) a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Warrantor' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    ")d on a.FWarrantorCode = d.FSubCode where a.fcheckstate = 1 " +
                    ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FWarrantorCode") + "").trim()).
                        append(
                            "\t");
                    bufShow.append( (rs.getString("FWarrantorName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.14 不显示启用日期数据
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
//                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
//                        YssCons.YSS_DATEFORMAT)).append(
//                            YssCons.YSS_LINESPLITMARK);
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
                    WarrantorBean warrantor = new WarrantorBean();
                    warrantor.setYssPub(pub);
                    warrantor.setWarrantorAttr(rs);
                    bufAll.append(warrantor.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            } else if (this.relaType.equalsIgnoreCase("Assignee")) {
                strName = "受托人";
                sHeader = "受托人代码\t受托人名称";//edit by songjie 2011.03.14 不显示启用日期数据
                strSql = "select y.* from " +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    "(select FAssigneeCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Assignee") + " " +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState <> 2 group by FAssigneeCode,FCheckState) x join" +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                    //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " (select * from " + pub.yssGetTableName("Tb_Para_Assignee") + " where FCheckState <> 2) a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Assignee' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    ")d on a.FAssigneeCode = d.FSubCode where a.fcheckstate = 1 " +
                    ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FAssigneeCode") + "").trim()).
                        append(
                            "\t");
                    bufShow.append( (rs.getString("FAssigneeName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.14 不显示启用日期数据
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
//                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
//                        YssCons.YSS_DATEFORMAT)).append(
//                            YssCons.YSS_LINESPLITMARK);
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
                    AssigneeBean assignee = new AssigneeBean();
                    assignee.setYssPub(pub);
                    assignee.setAssigneeAttr(rs);
                    bufAll.append(assignee.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            } else if (this.relaType.equalsIgnoreCase("InvestManager")) {
                strName = "投资经理";
                sHeader = "投资经理代码\t投资经理姓名";//edit by songjie 2011.03.14 不显示启用日期数据
                strSql = "select y.* from " + 
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    "(select FInvMgrCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_InvestManager") + " " +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState <> 2 group by FInvMgrCode,FCheckState) x join" +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                    //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " (select * from " + pub.yssGetTableName("Tb_Para_InvestManager") + " where FCheckState <> 2) a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'InvestManager' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    ")d on a.FInvMgrCode = d.FSubCode where a.fcheckstate = 1 " +
                    ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FInvMgrCode") + "").trim()).
                        append(
                            "\t");
                    bufShow.append( (rs.getString("FInvMgrName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.14 不显示启用日期数据
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
//                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
//                        YssCons.YSS_DATEFORMAT)).append(
//                            YssCons.YSS_LINESPLITMARK);
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
                    InvestManagerBean InvestManager = new InvestManagerBean();
                    InvestManager.setYssPub(pub);
                    InvestManager.setInvestManagerAttr(rs);
                    bufAll.append(InvestManager.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }
            //===============================================
            else if (this.relaType.equalsIgnoreCase("Stockholder")) {
                strName = "股东";
                sHeader = "股东代码\t股东名称";
                strSql = "select y.* from " +
                    "(select FStockholderCode,FCheckState as FChkState from " +
                    pub.yssGetTableName("Tb_Para_Stockholder") + " " +
                    " where  FCheckState <> 2 group by FStockholderCode,FCheckState) x join" +
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName ,e.FExchangeName as FExchangeName,f.FINVMGRNAME as FINVMGRNAME from " +//add by yanghaiming 20100421 B股业务  增加投资经理
                    pub.yssGetTableName("Tb_Para_Stockholder") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) e on a.FExchangeCode = e.FExchangeCode" +
                    " left join (select FINVMGRCODE,FINVMGRNAME from " + pub.yssGetTableName("Tb_Para_InvestManager") + " where FCHECKSTATE = 1)f on a.finvmgrcode = f.FINVMGRCODE" +//add by yanghaiming 20100421 B股业务  增加投资经理
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Stockholder' and FPortCode = " +
                    dbl.sqlString(this.portCode) +
                    ")d on a.FStockholderCode = d.FSubCode where a.fcheckstate = 1 " +
                    ") y on x.FStockholderCode = y.FStockholderCode " +
                    " order by  y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FStockholderCode") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FStockholderName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);

                    StockholderBean Stockholder = new StockholderBean();
                    Stockholder.setYssPub(pub);
                    Stockholder.setStockholderAttr(rs);
                    bufAll.append(Stockholder.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }
//======================================================
            else if (this.relaType.equalsIgnoreCase("TradeSeat")) {
                strName = "席位";
                sHeader = "席位代码\t席位名称";
                strSql = "select y.* from " +
                    "(select FSeatCode,FCheckState as FChkState  from " +
                    pub.yssGetTableName("Tb_Para_TradeSeat") + " " +
                    " where FCheckState <> 2 group by FSeatCode,FCheckState) x join" +
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName as FExchangeName,e.FBrokerName as FBrokerName,f.FVocName as FSeatTypeValue  from " +
                    pub.yssGetTableName("Tb_Para_TradeSeat") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode" +
                    " left join (select FBrokerCode,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState = 1) e on a.FBrokerCode = e.FBrokerCode" +
                    " left join Tb_Fun_Vocabulary f on a.FSeatType = f.FVocCode and f.FVocTypeCode = " +
                    dbl.sqlString(YssCons.YSS_TDS_SEATTYPE) +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'TradeSeat' and FPortCode = " +
                    dbl.sqlString(this.portCode) +
                    ")g on a.FSeatCode = g.FSubCode where a.fcheckstate = 1 " +
                    ") y on x.FSeatCode = y.FSeatCode " +
                    " order by  y.FCheckState, y.FCreateTime desc";

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FSeatCode") + "").trim()).
                        append(
                            "\t");
                    bufShow.append( (rs.getString("FSeatName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);

                    TradeSeatBean TradeSeat = new TradeSeatBean();
                    TradeSeat.setYssPub(pub);
                    TradeSeat.setTradeSeatAttr(rs);
                    bufAll.append(TradeSeat.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }

            //-==============================================
            else if (this.relaType.equalsIgnoreCase("Broker")) {
                strName = "券商";
                sHeader = "券商代码\t券商名称";//edit by songjie 2011.03.14 不显示启用日期数据
                strSql = "select y.* from " +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    "(select FBrokerCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Broker") + " " +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState <> 2 group by FBrokerCode,FCheckState) x join" +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                    //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " (select * from " + pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState <> 2 ) a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Broker' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    ") d on a.FBrokerCode = d.FSubCode where a.fcheckstate =1 " +
                    ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FBrokerCode") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FBrokerName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.14 不显示启用日期数据
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
//                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
//                        YssCons.YSS_DATEFORMAT)).append(
//                            YssCons.YSS_LINESPLITMARK);
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
                    BrokerBean broker = new BrokerBean();
                    broker.setYssPub(pub);
                    broker.setBrokerAttr(rs);
                    bufAll.append(broker.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }
            //--------------------------------------------------------------------
            else if (this.relaType.equalsIgnoreCase("PortLink")) {
                strName = "明细组合";
                sHeader = "明细组合代码\t明细组合名称";//edit by songjie 2011.03.14
                strSql = "select y.* from " +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                    "(select FPortCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState <> 2 group by FPortCode,FCheckState) x join" +
                    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                    " p.FCuryName as FCuryName,"+//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
                    
                    " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
                    
                    " f.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName ,ff.FExRateSrcName as FBaseRateSrcName,gg.FExRateSrcName as FPortRateSrcName from " + //modify by wangzuochun 2009.09.21 MS00702 浏览组合设置或者修改组合设置的基本信息都会报错 QDV4赢时胜（上海）2009年9月15日02_B
                    //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " (select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState <> 2) a" +
                  //---//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
                    "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = a.FCuryCode  " +
                    //---end QDV4上海2010年12月10日02_A-------------
                    
                    //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
                    " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
                    //-----add by zhangjun 2012-04-26 ETF联接基金                    
                    
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) f on a.FAssetGroupCode = f.FAssetGroupCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName from " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    //modify by wangzuochun 2009.09.21 MS00702 浏览组合设置或者修改组合设置的基本信息都会报错 QDV4赢时胜（上海）2009年9月15日02_B
                    " where FCheckState = 1) ff on a.FBaseRateSrcCode = ff.FExRateSrcCode " +
                    " left join (select FExRateSrcCode, FExRateSrcName from " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FCheckState = 1) gg on a.FPortRateSrcCode = gg.FExRateSrcCode " +
                    //-----------------------------------------------------------------------------------------------------------------//
                    " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    ") e on a.FPortCury = e.FCuryCode" +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'PortLink' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    ")d on a.FPortCode = d.FSubCode where a.fcheckstate = 1 " +
                    ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                    " where y.FASSETGROUPCODE = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FPortCode") + "").trim()).append(
                        "\t");
                    bufShow.append( (rs.getString("FPortName") + "").trim()).append(
                    		YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.14 不显示启用日期数据
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
//                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
//                        YssCons.YSS_DATEFORMAT)).append(
//                            YssCons.YSS_LINESPLITMARK);
                    //----delete by songjie 2011.03.14 不显示启用日期数据----//
                    PortfolioBean portfolio = new PortfolioBean();
                    portfolio.setYssPub(pub);
                    portfolio.setPortfolioAttr(rs);
                    bufAll.append(portfolio.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }

            else if (this.relaType.equalsIgnoreCase("MTV")) {
                strName = "估值方法";
                sHeader = "估值方法代码\t估值方法名称\t启用日期";
                strSql = "select y.* from " +
                    "(select FMTVCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_MTVMethod") + " " +
                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    " and FCheckState <> 2 group by FMTVCode,FCheckState) x join" +
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FMktSrcName as FMktSrcName,xx.FExRateSrcName as FBaseRateSrcName,xxx.FExRateSrcName as FPortRateSrcName," +
                    "e.FCatName,f.FSubCatName,g.FCusCatName,h.FSectorName,i.FSecurityName,d.FRelagRade as FRelagRade,ff.FExrateSrcName  from " +
                    pub.yssGetTableName("Tb_Para_MTVMethod") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName from " + pub.yssGetTableName("Tb_Para_ExRateSource") + " where FCheckState = 1) ff on a.FBaseRateSrcCode = ff.FExRateSrcCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName from " + pub.yssGetTableName("Tb_Para_ExRateSource") + " where FCheckState =1) xx on a.FBaseRateSrcCode = xx.FExRateSrcCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName from " + pub.yssGetTableName("Tb_Para_ExRateSource") + " where FCheckState =1) xxx on a.FPortRateSrcCode = xxx.FExRateSrcCode" +
                    //------------------------------------------------------------
                    " left join (select FMktSrcCode,FMktSrcName from " +
                    pub.yssGetTableName("Tb_Para_MarketSource") +
                    " where FCheckState = 1) d on a.FMktSrcCode = d.FMktSrcCode" +
                    //------------------------------------------------------------
                    " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) e on a.FCatCode = e.FCatCode" +
                    " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) f on a.FSubCatCode = f.FSubCatCode" +
                    //------------------------------------------------------------
                    " left join (select FCusCatCode,FCusCatName from " +
                    pub.yssGetTableName("Tb_Para_CustomCategory") +
                    " where FCheckState = 1) g on  a.FCusCatCode = g.FCusCatCode " +
                    //------------------------------------------------------------
                    " left join (" +//edit by songjie 2011.03.22 不以最大的启用日期查询数据
                    //----delete by songjie 2011.03.22 不以最大的启用日期查询数据----//
//                    pub.yssGetTableName("Tb_Para_Sector") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    //----delete by songjie 2011.03.22 不以最大的启用日期查询数据----//
                    " select FSectorCode, FSectorName, FStartDate from " +//edit by songjie 2011.03.22 不以最大的启用日期查询数据
                    pub.yssGetTableName("Tb_Para_Sector") +
                    " where FCheckState = 1) h on a.FSecClsCode = h.FSectorCode " +//edit by songjie 2011.03.22 不以最大的启用日期查询数据
                    //------------------------------------------------------------
                    " left join (select ib.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    " and FCheckState = 1 group by FSecurityCode) ia join (select FSecurityCode, FSecurityName, FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    ") ib on ia.FSecurityCode = ib.FSecurityCode and ia.FStartDate = ib.FStartDate) i on a.FSecurityCode = i.FSecurityCode " +
                    //------------------------------------------------------------
                    " join (select FSubCode,FRelagRade from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'MTV' and FPortCode = " +
                    dbl.sqlString(this.portCode) + //edit by songjie 2011.03.22 不以启用日期查询数据
//                    dbl.sqlDate(this.startDate) +//delete by songjie 2011.03.22 不以启用日期查询数据
                    " order by FRelagRade " +
                    ") d on a.FMTVCode = d.FSubCode where a.fcheckstate =1 " +
                    ") y on x.FMTVCode = y.FMTVCode and x.FStartDate = y.FStartDate" +
                    " order by y.FRelagRade , y.fstartdate desc, y.FCheckState, y.FCreateTime desc";

                rs = dbl.openResultSet(strSql);
                MTVMethodBean MTV = new MTVMethodBean();
                while (rs.next()) {
                    bufShow.append( (rs.getString("FMTVCode") + "").trim()).append(
                        "\t");
                    bufShow.append( (rs.getString("FMTVName") + "").trim()).append(
                        "\t");
                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
                        YssCons.YSS_DATEFORMAT)).append(
                            YssCons.YSS_LINESPLITMARK);

                    MTV.setYssPub(pub);
                    MTV.setMTVMethodAttr(rs);
                    bufAll.append(MTV.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }

            else if (this.relaType.equalsIgnoreCase("Template")) {
                strName = "监控模板";
                sHeader = "监控模板代码\t监控模板名称\t启用日期";
                strSql = "select y.* from " +
                    "(select FIndexTempCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Comp_IndexTemplate") + " " +
                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                    " and FCheckState <> 2 group by FIndexTempCode,FCheckState) x join" +
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                    pub.yssGetTableName("Tb_Comp_IndexTemplate") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " join (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Template' and FPortCode = " +
                    dbl.sqlString(this.portCode) + " and FStartDate = " +
                    dbl.sqlDate(this.startDate) +
                    ") d on a.FIndexTempCode = d.FSubCode where a.fcheckstate =1 " +
                    ") y on x.FIndexTempCode = y.FIndexTempCode and x.FStartDate = y.FStartDate" +
                    " order by y.fstartdate desc, y.FCheckState, y.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FIndexTempCode") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FIndexTempName") + "").trim()).
                        append("\t");
                    bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
                        YssCons.YSS_DATEFORMAT)).append(
                            YssCons.YSS_LINESPLITMARK);

                    IndexTemplateBean template = new IndexTemplateBean();
                    template.setYssPub(pub);
                    template.setIndexTemplateAttr(rs);

                    bufAll.append(template.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
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
            throw new YssException("获取可用" + strName + "信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
     * parseRowSt
     * 为各变量赋值
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRowAry = sRowStr.split("\t");
        this.portCode = sRowAry[0];
        this.relaType = sRowAry[1];
        this.subCode = sRowAry[2];
        this.relaGrade = sRowAry[3];
        this.startDate = YssFun.toDate(sRowAry[4]);
        this.oldPortCode = sRowAry[5];
        this.oldStartDate = YssFun.toDate(sRowAry[6]);
        super.parseRecLog();
    }

    /**
     * saveMutliSetting
     * 保存组合关联信息
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        String sPrevRelaType = "";
        try {
            strSql =
                "insert into " +
                pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                " (FPortCode,FRelaType,FSubCode," +
                "FStartDate,FRelaGrade," +
                "FCheckState,FCreator,FCreateTime) values (?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                if (!sPrevRelaType.equalsIgnoreCase(this.relaType)) {
                    strSql =
                        "delete from " +
                        pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                        " where FPortCode = " +
                        dbl.sqlString(this.oldPortCode) + " and FRelaType = " +
                        dbl.sqlString(this.relaType);//edit by songjie 2011.03.22 不以启用日期删除数据
//                        dbl.sqlDate(this.oldStartDate);//delete by songjie 2011.03.22 不以启用日期删除数据
                    dbl.executeSql(strSql);
                    sPrevRelaType = this.relaType;
                }
                if (this.subCode.equals("")) {
                    continue;
                }
                pstmt.setString(1, this.portCode);
                pstmt.setString(2, this.relaType);
                pstmt.setString(3, this.subCode);
                pstmt.setDate(4, YssFun.toSqlDate(this.startDate));
                pstmt.setString(5, this.relaGrade);
                //modified by yeshenghong BUG7617  20130426
                pstmt.setInt(6,  1);
                //end modified by yeshenghong BUG7617  20130426
                pstmt.setString(7, this.creatorCode);
                pstmt.setString(8, this.creatorTime);
                pstmt.executeUpdate();
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存组合关联信息出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    public void saveSetting(byte btOper) {
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
     * by leeyu add 2008-11-25 MS00020
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String sResult = "";
        String sqlStr = "";
        StringBuffer buf = null;
        ResultSet rs = null;
        try {
            if (sType != null && sType.equalsIgnoreCase("InvestManagers")) { //根据组合代码来查投资经理
                buf = new StringBuffer();
                sqlStr = "select distinct a.FInvmgrCode,a.FInvmgrName from " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " a  join (select FSubCode from " +
                    pub.yssGetTableName("tb_para_portfolio_relaship") +
                    " where FPortCode=" + dbl.sqlString(portCode) + " and FRelaType='InvestManager') b on " +
                    " a.Finvmgrcode =b.FSubCode where a.FCheckState=1 ";
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    buf.append(rs.getString("FInvmgrCode")).append("\t");
                    buf.append(rs.getString("FInvmgrName")).append("\r\n");
                }
                sResult = buf.toString();
                buf = null;
                if (sResult.length() > 2) {
                    sResult = sResult.substring(0, sResult.length() - 2);
                }
            } else if (sType != null && sType.equalsIgnoreCase("portcodes")) { //根据投资经理来查组合代码
                buf = new StringBuffer();
                sqlStr = "select  distinct a.FPortCode,b.FPortName from " + pub.yssGetTableName("tb_para_portfolio_relaship") + " a join ( " +
                    " select FPortCode,FPortName from " + pub.yssGetTableName("tb_para_portfolio") + "  where FCheckState=1 ) b on a.FPortCode=b.FPortCode " +
                    " where a.FSubCode = " + dbl.sqlString(subCode) + " and a.FRelatype='InvestManager'";
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    buf.append(rs.getString("FPortCode")).append("\t");
                    buf.append(rs.getString("FPortName")).append("\r\n");
                }
                sResult = buf.toString();
                buf = null;
                if (sResult.length() > 2) {
                    sResult = sResult.substring(0, sResult.length() - 2);
                }
            }
            //add by huangqirong 2011-09-06 story #1277
            else if(sType!=null&&sType.equalsIgnoreCase("getportname")){
            	buf = new StringBuffer();
                sqlStr = "select FPortCode,FPortName from " + pub.yssGetTableName("tb_para_portfolio") +" where FPortCode="+dbl.sqlString(this.getPortCode())+" and FCheckState=1 ";
                rs = dbl.openResultSet(sqlStr);
                if(rs.next()) {
                    buf.append(rs.getString("FPortCode")).append("\t");
                    buf.append(rs.getString("FPortName"));
                }
                sResult = buf.toString();                
            }
            //---end--
            
        } catch (Exception ex) {
            throw new YssException(ex.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
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

    //   public
}
