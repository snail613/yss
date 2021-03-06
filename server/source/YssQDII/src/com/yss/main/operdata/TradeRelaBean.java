package com.yss.main.operdata;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

public class TradeRelaBean
    extends BaseDataSettingBean implements IDataSetting {
    public TradeRelaBean() {
    }

    private String sNum = "";
    private String sTradeNum = ""; //取外部交易编号
    private String sRelaType = "";
    private String sPortCode = "";
    private String sPortName = "";
    private String sAnalysisCode1 = "";
    private String sAnalysisCode2 = "";
    private String sAnalysisCode3 = "";
    private String sAnalysisName1 = "";
    private String sAnalysisName2 = "";
    private String sAnalysisName3 = "";
    private String sSecurityCode = "";
    private String sSecurityName = "";

    //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
    //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
    private int iInOut = 1; //流入流出方向,默认为正方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
    private String attrClsCode = "";
    private String attrClsName = "";
    private String sDesc = "";
    private String sMutilStr = "";
    private String sOldNum = "";
    private String sOldRelaType = "";
    private String sOldPortCode = "";
    private String sOldAnalysisCode1 = "";
    private String sOldAnalysisCode2 = "";
    private String sOldAnalysisCode3 = "";
    private String sOldSecurityCode = "";

    private double dAmount;
    private double dCost;
    private double dMCost;
    private double dVCost;
    private double dBaseCuryCost;
    private double dMBaseCuryCost;
    private double dVBaseCuryCost;
    private double dPortCuryCost;
    private double dMPortCuryCost;
    private double dVPortCuryCost;
    private String delAll = ""; //2008-4-18--单亮
    private TradeSubBean tradeSub = null;
    private TradeRelaBean filterType = null;
    private HashMap hmRelaSub = new HashMap();
    public double getDAmount() {
        return dAmount;
    }

    public String getDelAll() {
        return this.delAll;
    }

    public double getDBaseCuryCost() {
        return dBaseCuryCost;
    }

    public double getDCost() {
        return dCost;
    }

    public double getDMBaseCuryCost() {
        return dMBaseCuryCost;
    }

    public double getDMCost() {
        return dMCost;
    }

    public double getDMPortCuryCost() {
        return dMPortCuryCost;
    }

    public double getDPortCuryCost() {
        return dPortCuryCost;
    }

    public double getDVBaseCuryCost() {
        return dVBaseCuryCost;
    }

    public double getDVCost() {
        return dVCost;
    }

    public double getDVPortCuryCost() {
        return dVPortCuryCost;
    }

    public String getSAnalysisCode1() {
        return sAnalysisCode1;
    }

    public String getSAnalysisCode2() {
        return sAnalysisCode2;
    }

    public String getSAnalysisCode3() {
        return sAnalysisCode3;
    }

    public String getSAnalysisName1() {
        return sAnalysisName1;
    }

    public String getSAnalysisName2() {
        return sAnalysisName2;
    }

    public String getSAnalysisName3() {
        return sAnalysisName3;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSNum() {
        return sNum;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public String getSPortName() {
        return sPortName;
    }

    public String getSRelaType() {
        return sRelaType;
    }

    public String getSSecurityCode() {
        return sSecurityCode;
    }

    public String getSOldPortCode() {
        return sOldPortCode;
    }

    public String getSOldRelaType() {
        return sOldRelaType;
    }

    public String getSOldNum() {
        return sOldNum;
    }

    public String getSOldAnalysisCode2() {
        return sOldAnalysisCode2;
    }

    public String getSOldAnalysisCode1() {
        return sOldAnalysisCode1;
    }

    public String getSOldAnalysisCode3() {
        return sOldAnalysisCode3;
    }

    public TradeRelaBean getFilterType() {
        return filterType;
    }

    public String getSSecurityName() {
        return sSecurityName;
    }

    public String getSOldSecurityCode() {
        return sOldSecurityCode;
    }

    public String getSTradeNum() {
        return sTradeNum;
    }

    public TradeSubBean getTradeSub() {
        return tradeSub;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public String getAttrClsName() {
        return attrClsName;
    }

    public int getIInOut() {
        return iInOut;
    }

    public void setDelAll(String Delall) {
        this.delAll = Delall;
    }

    public void setSSecurityCode(String sSecurityCode) {
        this.sSecurityCode = sSecurityCode;
    }

    public void setSRelaType(String sRelaType) {
        this.sRelaType = sRelaType;
    }

    public void setSPortName(String sPortName) {
        this.sPortName = sPortName;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setSNum(String sNum) {
        this.sNum = sNum;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSAnalysisName3(String sAnalysisName3) {
        this.sAnalysisName3 = sAnalysisName3;
    }

    public void setSAnalysisName2(String sAnalysisName2) {
        this.sAnalysisName2 = sAnalysisName2;
    }

    public void setSAnalysisName1(String sAnalysisName1) {
        this.sAnalysisName1 = sAnalysisName1;
    }

    public void setSAnalysisCode3(String sAnalysisCode3) {
        this.sAnalysisCode3 = sAnalysisCode3;
    }

    public void setSAnalysisCode2(String sAnalysisCode2) {
        this.sAnalysisCode2 = sAnalysisCode2;
    }

    public void setSAnalysisCode1(String sAnalysisCode1) {
        this.sAnalysisCode1 = sAnalysisCode1;
    }

    public void setDVPortCuryCost(double dVPortCuryCost) {
        this.dVPortCuryCost = dVPortCuryCost;
    }

    public void setDVCost(double dVCost) {
        this.dVCost = dVCost;
    }

    public void setDVBaseCuryCost(double dVBaseCuryCost) {
        this.dVBaseCuryCost = dVBaseCuryCost;
    }

    public void setDPortCuryCost(double dPortCuryCost) {
        this.dPortCuryCost = dPortCuryCost;
    }

    public void setDMPortCuryCost(double dMPortCuryCost) {
        this.dMPortCuryCost = dMPortCuryCost;
    }

    public void setDMCost(double dMCost) {
        this.dMCost = dMCost;
    }

    public void setDMBaseCuryCost(double dMBaseCuryCost) {
        this.dMBaseCuryCost = dMBaseCuryCost;
    }

    public void setDCost(double dCost) {
        this.dCost = dCost;
    }

    public void setDBaseCuryCost(double dBaseCuryCost) {
        this.dBaseCuryCost = dBaseCuryCost;
    }

    public void setDAmount(double dAmount) {
        this.dAmount = dAmount;
    }

    public void setSOldPortCode(String sOldPortCode) {
        this.sOldPortCode = sOldPortCode;
    }

    public void setSOldRelaType(String sOldRelaType) {
        this.sOldRelaType = sOldRelaType;
    }

    public void setSOldNum(String sOldNum) {
        this.sOldNum = sOldNum;
    }

    public void setSOldAnalysisCode2(String sOldAnalysisCode2) {
        this.sOldAnalysisCode2 = sOldAnalysisCode2;
    }

    public void setSOldAnalysisCode1(String sOldAnalysisCode1) {
        this.sOldAnalysisCode1 = sOldAnalysisCode1;
    }

    public void setSOldAnalysisCode3(String sOldAnalysisCode3) {
        this.sOldAnalysisCode3 = sOldAnalysisCode3;
    }

    public void setFilterType(TradeRelaBean filterType) {
        this.filterType = filterType;
    }

    public void setSSecurityName(String sSecurityName) {
        this.sSecurityName = sSecurityName;
    }

    public void setSOldSecurityCode(String sOldSecurityCode) {
        this.sOldSecurityCode = sOldSecurityCode;
    }

    public void setSTradeNum(String sTradeNum) {
        this.sTradeNum = sTradeNum;
    }

    public void setTradeSub(TradeSubBean tradeSub) {
        this.tradeSub = tradeSub;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setAttrClsName(String attrClsName) {
        this.attrClsName = attrClsName;
    }

    public void setIInOut(int iInOut) {
        this.iInOut = iInOut;
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        if (this.sMutilStr.trim().length() != 0 && this.sMutilStr.trim() != null) {
            TradeRelaSubBean sub = new TradeRelaSubBean();
            sub.setYssPub(pub);
            if (sTradeNum.length() == 0) {
                sTradeNum = sNum;
            }
            sub.setSTradeNum(sTradeNum);
            sMutilStr = sMutilStr.replaceAll("~", "\t");
            sub.saveMutliSetting(sMutilStr);
        }
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    /**
     * 单亮
     * 2008-4-15
     * 删除交易数据
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete " + pub.yssGetTableName("tb_data_traderela") +
                " where FNUM = " +
                dbl.sqlString(this.sNum) + " and FPORTCODE = " +
                dbl.sqlString(this.sPortCode) + " AND FSECURITYCODE = " +
                dbl.sqlString(this.sSecurityCode);
            dbl.executeSql(strSql);

            strSql = "delete " + pub.yssGetTableName("tb_data_traderelasub") +
                " where FNUM = " +
                dbl.sqlString(this.sNum) + " and FPORTCODE = " +
                dbl.sqlString(this.sPortCode) + " AND FSECURITYCODE = " +
                dbl.sqlString(this.sSecurityCode);

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除板块信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void checkSetting() throws YssException {
    }

    /**
     * 此方法已经被修改
     * 修改时间：2008-4-18
     * 修改人： 单亮
     * 修改原因：原方法不能 彻底删除数据
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null; ;
        String strSql = "";
        boolean bTrans = false;
        String[] sOneData = sMutilRowStr.split("\f\f");
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_TradeRela") +
                " where FNum =" + dbl.sqlString(this.sTradeNum);
            dbl.executeSql(strSql);
            //如果删除此交易关联，那么就删除此交易关联下的所有的应收应付  2008-5-29 单亮
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_TradeRelaSub") +
                " where FNum =" + dbl.sqlString(this.sTradeNum);

            dbl.executeSql(strSql);

            for (int i = 0; i < sOneData.length; i++) {
                this.parseRowStr(sOneData[i]);
                if (this.delAll.equals("True")) { //-2008-4-18-单亮--根据标识判断是否可以全部删除数据
                    dbl.executeSql(strSql);
                } else {
                    strSql = "insert into " +
                        pub.yssGetTableName("Tb_Data_TradeRela") +
                        "(FNum,FRelaType,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAttrClsCode," +
                        " FInOut," + //流入流出方向，panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
                        "FSecurityCode,FAmount,FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost," +
                        "FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FDesc,FCheckstate,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
                        dbl.sqlString(sTradeNum) + "," +
                        dbl.sqlString(tradeSub.getTradeCode()) + "," +
                        dbl.sqlString(sPortCode) + "," +
                        dbl.sqlString(this.sAnalysisCode1) + "," +
                        dbl.sqlString(this.sAnalysisCode2) + "," +
                        dbl.sqlString(this.sAnalysisCode3) + "," +
                        //------------2009-07-07 蒋锦 添加 属性分类--------------//
                        //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
                        dbl.sqlString(this.attrClsCode.length() == 0 ? " " : this.attrClsCode) + "," +
                        //-----------------------------------------------------//
                        this.iInOut + "," +
                        dbl.sqlString(sSecurityCode) + "," +
                        this.dAmount + "," +
                        this.dCost + "," +
                        this.dMCost + "," +
                        this.dVCost + "," +
                        this.dBaseCuryCost + "," +
                        this.dMBaseCuryCost + "," +
                        this.dVBaseCuryCost + "," +
                        this.dPortCuryCost + "," +
                        this.dMPortCuryCost + "," +
                        this.dVPortCuryCost + "," +
                        dbl.sqlString(this.sDesc) + "," +
                        0 + "," + //默认值是未审核
                        dbl.sqlString(this.creatorCode) + "," +
                        dbl.sqlString(this.creatorTime) + "," +
                        (pub.getSysCheckState() ? "' '" : this.checkUserCode) +
                        "," +
                        (pub.getSysCheckState() ? "' '" : this.checkTime) +
                        ")";
                    dbl.executeSql(strSql);
                    //2008-5-29 单亮 在插入时：插入了一条交易关联就要直接插入此交易关联的应收应付，如果放下面则只能插入最后一条的应收应付
                    if (this.sMutilStr.trim().length() != 0 &&
                        this.sMutilStr.trim() != null) {
                        TradeRelaSubBean sub = new TradeRelaSubBean();
                        TradeRelaBean rela = new TradeRelaBean();
                        rela.setYssPub(pub);
                        rela.setSPortCode(this.sPortCode);
                        rela.setSRelaType(this.sRelaType);
                        rela.setSSecurityCode(this.sSecurityCode);
                        rela.setSAnalysisCode1(this.sAnalysisCode1);
                        rela.setSAnalysisCode2(this.sAnalysisCode2);
                        rela.setSAnalysisCode3(this.sAnalysisCode3);
                        rela.setAttrClsCode(this.attrClsCode);
                        rela.setIInOut(this.iInOut); //流入流出方向，panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
                        sub.setYssPub(pub);
                        sub.setSTradeNum(sTradeNum);
                        sub.setTradeRela(rela);
                        if (sub.getSTradeNum().length() == 0) {
                            sub.setSTradeNum(sNum);
                        }
                        sMutilStr = sMutilStr.replaceAll("~", "\t");
                        sub.saveMutliSetting(sMutilStr);

                    }
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("设置交易关联表出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String strSql = "";
        if (this.filterType != null) {
            strSql = " where 1=1 ";
            if (this.filterType.sNum != null && filterType.sNum.length() > 0) {
                strSql += " and a.FNum like '" +
                    filterType.sNum.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sRelaType != null &&
                filterType.sRelaType.trim().length() > 0) {
                strSql += " and a.FRelaType like '" +
                    filterType.sRelaType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sPortCode != null &&
                filterType.sPortCode.trim().length() > 0) {
                strSql += " and a.FPortCode like '" +
                    filterType.sPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sAnalysisCode1 != null &&
                filterType.sAnalysisCode1.trim().length() > 0) {
                strSql += " and a.FAnalysisCode1 like '" +
                    filterType.sAnalysisCode1.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sAnalysisCode2 != null &&
                filterType.sAnalysisCode2.trim().length() > 0) {
                strSql += " and a.FAnalysisCode2 like '" +
                    filterType.sAnalysisCode2.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sAnalysisCode3 != null &&
                filterType.sAnalysisCode3.trim().length() > 0) {
                strSql += " and a.FAnalysisCode3 like '" +
                    filterType.sAnalysisCode3.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sSecurityCode != null &&
                filterType.sSecurityCode.trim().length() > 0) {
                strSql += " and a.FSecurityCode like '" +
                    filterType.sSecurityCode.replaceAll("'", "''") + "%'";
            }
            //------------2009-07-07 蒋锦 添加 属性分类--------------//
            //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
            if (this.filterType.attrClsCode != null &&
                filterType.attrClsCode.trim().length() > 0) {
                strSql += " and a.FAttrClsCode like '" +
                    filterType.attrClsCode.replaceAll("'", "''") + "%'";
            }
            //------------------------------------------------------//
        }
        return strSql;
    }

    private void setRelaSetAttr(ResultSet rs) throws SQLException, YssException {
        this.sNum = rs.getString("FNum");
        this.sRelaType = rs.getString("FRelaType");
        this.sPortCode = rs.getString("FPortCode");
        this.sPortName = rs.getString("FPortName");
        this.sAnalysisCode1 = rs.getString("FAnalysisCode1");
        this.sAnalysisCode2 = rs.getString("FAnalysisCode2"); //1  2008-5-22  单亮
        this.sAnalysisCode3 = rs.getString("FAnalysisCode3"); //2  2008-5-22  单亮
        this.sAnalysisName1 = rs.getString("FAnalysisName1"); //3  2008-5-22  单亮
        this.sAnalysisName2 = rs.getString("FAnalysisName2");
        this.sAnalysisName3 = rs.getString("FAnalysisName3");
        this.sSecurityCode = rs.getString("FSecurityCode");
        this.sSecurityName = rs.getString("FSecurityName");
        //------------2009-07-07 蒋锦 添加 属性分类--------------//
        //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
        this.attrClsCode = rs.getString("FAttrClsCode");
        this.attrClsName = rs.getString("FAttrClsName");
        //----------------------------------------------------//
        this.iInOut = rs.getInt("FInOut"); //流入流出方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
        this.dAmount = rs.getDouble("FAmount");
        this.dCost = rs.getDouble("FCost");
        this.dMCost = rs.getDouble("FMCost");
        this.dVCost = rs.getDouble("FVCost");
        this.dBaseCuryCost = rs.getDouble("FBaseCuryCost");
        this.dMBaseCuryCost = rs.getDouble("FMBaseCuryCost");
        this.dVBaseCuryCost = rs.getDouble("FVBaseCuryCost");
        this.dPortCuryCost = rs.getDouble("FPortCuryCost");
        this.dMPortCuryCost = rs.getDouble("FMPortCuryCost");
        this.dVPortCuryCost = rs.getDouble("FVPortCuryCost");
        this.sDesc = rs.getString("FDesc");
        super.setRecLog(rs);

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

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sRelaSubHeader = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        TradeRelaSubBean relaSub = new TradeRelaSubBean();
        TradeRelaSubBean filterType = new TradeRelaSubBean();
        relaSub.setYssPub(pub);
        filterType.setYssPub(pub);
        BaseDataSettingBean id = (BaseDataSettingBean) pub.getOperDataCtx().
            getBean("traderelasub");
        id.setYssPub(pub);
        sRelaSubHeader = id.getListView1Headers();
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.FNum,a.FRelaType,a.FPortCode,a.FAnalysisCode1,a.Fanalysiscode2,a.fanalysiscode3,a.Fsecuritycode,a.famount,a.Fcost,a.fmcost,a.fvcost," +
                " a.fbasecurycost,a.fmbasecurycost,a.fvbasecurycost,a.fportcurycost,a.fmportcurycost,a.fvportcurycost,a.Fdesc,1 as FCheckState," +
                " FInout, " + //流入流出方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName,c.FPortName as FPortName,d.FSecurityName as FSecurityName, " +
                (SecPecPayAnalysis().length() == 0 ?
                 " ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3, " :
               //modify by zhangfa 20100916  MS01737    浏览交易数据中关联分页时，投资经理、券商代码未加载    QDV4赢时胜(测试)2010年09月13日06_B     	 
                 " h.FInvMgrName AS FAnalysisName1, i.FBrokerName AS FAnalysisName2, j.FAnalysisName AS FAnalysisName3, ") +
               //----------------------------------------------------------------------------------------------------------------------
                " a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime,a.fattrclscode, p.FAttrClsName from " +
                pub.yssGetTableName("Tb_Data_TradeRela") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " ) c on a.FPortCode = c.FPortCode " +
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " ) d on a.FSecurityCode = d.FSecurityCode " +
                //-----------------2008.01.10 修改 蒋锦 显示分析代码名---------------------//
              //modify by zhangfa 20100916  MS01737    浏览交易数据中关联分页时，投资经理、券商代码未加载    QDV4赢时胜(测试)2010年09月13日06_B
                " left join (select FInvMgrCode,FInvMgrName from  " +pub.yssGetTableName("Tb_Para_InvestManager") + 
                " where FCheckState = 1) h on a.FAnalysisCode1 = h.FInvMgrCode" +
                " left join (select FBrokerCode,FBrokerName from " +pub.yssGetTableName("Tb_Para_Broker")+
                " where FCheckState = 1) i on a.FAnalysisCode2 = i.FBrokerCode" +
              //-------------------------------------------------------------------------------------------------------------------------  
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) j on a.FAnalysisCode3 = j.FAnalysisCode" +
                //----------------------------------------------------------------------//
                //------------2009-07-07 蒋锦 添加 属性分类--------------//
                //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode " +
                //-----------------------------------------------------//
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {	
            	//---add by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B start---//
            	bufShow.append(rs.getString("FPortCode") + "").append("\t");
            	bufShow.append(rs.getString("FPortName") + "").append("\t");
            	bufShow.append(rs.getString("FSecurityCode") + "").append("\t");
            	bufShow.append(rs.getString("FSecurityName") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FAmount"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FCost"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FMCost"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FVCost"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FPortCuryCost"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FMPortCuryCost"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FVPortCuryCost"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FBaseCuryCost"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FMBaseCuryCost"),"#,##0.####") + "").append("\t");
				bufShow.append(YssFun.formatNumber(rs.getDouble("FVBaseCuryCost"),"#,##0.####") + "").append("\t");
				bufShow.append(rs.getString("FAnalysisCode1") + "").append("\t");
				bufShow.append(rs.getString("FAnalysisName1") + "").append("\t");
				bufShow.append(rs.getString("FAnalysisCode2") + "").append("\t");
				bufShow.append(rs.getString("FAnalysisName2") + "").append("\t");
				bufShow.append(rs.getString("FAnalysisCode3") + "").append("\t");
				bufShow.append(rs.getString("FAnalysisName3") + "").append("\t");
				bufShow.append(rs.getString("FAttrClsCode") + "").append("\t");
				bufShow.append(rs.getString("FAttrClsName") + "").append("\t");
				bufShow.append((rs.getInt("FInOut") == 1) ? "流入" : "流出").append(YssCons.YSS_LINESPLITMARK);
				//---add by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B end---//
				//---delete by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B start---//
//                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                    append("\t").
//                    append(YssCons.YSS_LINESPLITMARK);
				//---delete by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B end---//
                setRelaSetAttr(rs);
                //------------2009-07-07 蒋锦 修改 属性分类也是主键之一--------------//
                //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
                String key = sNum + sRelaType + sPortCode + sSecurityCode +
                    sAnalysisCode1 + sAnalysisCode2 + sAnalysisCode3 + attrClsCode;
                //----------------------------------------------------------------//
                filterType.setSNum(sNum);
                filterType.setSRelaType(sRelaType);
                filterType.setSPortCode(sPortCode);
                filterType.setSSecurityCode(sSecurityCode);
                filterType.setSAnalysisCode1(sAnalysisCode1);
                filterType.setSAnalysisCode2(sAnalysisCode2);
                filterType.setSAnalysisCode3(sAnalysisCode3);
                filterType.setAttrClsCode(attrClsCode);
                relaSub.setFilterType(filterType);
                String[] sArr = relaSub.getListViewData1().split("\f\n");
                String sRes = sArr[2];
                if (hmRelaSub.get(key) == null) {
                    hmRelaSub.put(key, sRes);
                }
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
            sAllDataStr += "\b\b" + sRelaSubHeader;
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取交易关联表信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.sMutilStr = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.sNum = reqAry[0];
            this.sRelaType = reqAry[1];
            if (this.sRelaType.length() == 0) {
                this.sRelaType = " ";
            }
            this.sPortCode = reqAry[2];
            if (this.sPortCode.length() == 0) {
                this.sPortCode = " ";
            }
            this.sSecurityCode = reqAry[4];
            if (this.sSecurityCode.length() == 0) {
                this.sSecurityCode = " ";
            }
            this.dAmount = YssFun.toNumber(reqAry[6]);
            this.dCost = YssFun.toNumber(reqAry[7]);
            this.dMCost = YssFun.toNumber(reqAry[8]);
            this.dVCost = YssFun.toNumber(reqAry[9]);
            this.dPortCuryCost = YssFun.toNumber(reqAry[10]);
            this.dMPortCuryCost = YssFun.toNumber(reqAry[11]);
            this.dVPortCuryCost = YssFun.toNumber(reqAry[12]);
            this.dBaseCuryCost = YssFun.toNumber(reqAry[13]);
            this.dMBaseCuryCost = YssFun.toNumber(reqAry[14]);
            this.dVBaseCuryCost = YssFun.toNumber(reqAry[15]);
            this.sAnalysisCode1 = reqAry[16];
            if (this.sAnalysisCode1.length() == 0) {
                this.sAnalysisCode1 = " ";
            }
            this.sAnalysisCode2 = reqAry[18];
            if (this.sAnalysisCode2.length() == 0) {
                this.sAnalysisCode2 = " ";
            }
            this.sAnalysisCode3 = reqAry[20];
            if (this.sAnalysisCode3.length() == 0) {
                this.sAnalysisCode3 = " ";
            }
            this.sDesc = reqAry[22];
            this.delAll = reqAry[23]; //-2008-4-18-单亮-是否全部删除的标识
            this.sMutilStr = reqAry[24];
            //------------2009-07-07 蒋锦 添加 属性分类--------------//
            //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
            this.attrClsCode = reqAry[25];
            this.attrClsName = reqAry[26];
            //-----------------------------------------------------//
            //-----------panjunfang add 20090727--------
            if (reqAry[27].trim().length() > 0 && YssFun.isNumeric(reqAry[27])) {
                this.iInOut = YssFun.toInt(reqAry[27]);
            }
            //---流入流出方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TradeRelaBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析交易关联表出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sNum).append("\t");
        buf.append(this.sRelaType).append("\t");
        buf.append(this.sPortCode).append("\t");
        buf.append(this.sPortName).append("\t");
        buf.append(this.sSecurityCode).append("\t");
        buf.append(this.sSecurityName).append("\t");
        buf.append(this.dAmount).append("\t");
        buf.append(this.dCost).append("\t");
        buf.append(this.dMCost).append("\t");
        buf.append(this.dVCost).append("\t");
        buf.append(this.dPortCuryCost).append("\t");
        buf.append(this.dMPortCuryCost).append("\t");
        buf.append(this.dVPortCuryCost).append("\t");
        buf.append(this.dBaseCuryCost).append("\t");
        buf.append(this.dMBaseCuryCost).append("\t");
        buf.append(this.dVBaseCuryCost).append("\t");
        buf.append(this.sAnalysisCode1).append("\t");
        buf.append(this.sAnalysisName1).append("\t");
        buf.append(this.sAnalysisCode2).append("\t");
        buf.append(this.sAnalysisName2).append("\t");
        buf.append(this.sAnalysisCode3).append("\t");
        buf.append(this.sAnalysisName3).append("\t");
        buf.append(this.sDesc).append("\t");
        //------------2009-07-07 蒋锦 添加 属性分类--------------//
        //MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A
        buf.append(this.getRelaSub(sNum + sRelaType + sPortCode + sSecurityCode +
                                   sAnalysisCode1 + sAnalysisCode2 +
                                   sAnalysisCode3 + attrClsCode)).append("\t");
        buf.append(this.attrClsCode).append("\t");
        buf.append(this.attrClsName).append("\t");
        //-----------------------------------------------------//
        //edit by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B
        buf.append((this.iInOut == 1) ? "流入" : "流出").append("\t"); //流入流出方向,panjunfang add 20090727，MS00025  汇总接口清算至业务资料 QDV4.1赢时胜（上海）2009年4月20日25_A
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        Connection conn = null;
        ResultSet rs = null;
        String sStorageDate = "";
        double dBaseCuryRate = 0, dPortCuryRate = 0;
        String strSql = "";
        try {
            if (sType.equalsIgnoreCase("getcost")) {
                conn = dbl.loadConnection();
                strSql = "select a.FBaseCuryRate as FBaseCuryRate,a.FPortCuryRate as FPortCuryRate,a.FBargainDate as FStorageDate from " +
                    pub.yssGetTableName("Tb_Data_SubTrade") + " a" +
                    " where a.FNum=" + dbl.sqlString(sNum);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    sStorageDate = YssFun.formatDate(rs.getString("FStorageDate"));
                    dBaseCuryRate = rs.getDouble("FBaseCuryRate");
                    dPortCuryRate = rs.getDouble("FPortCuryRate");
                    this.calCost(sStorageDate, dBaseCuryRate, dPortCuryRate);
                }
                rs.close();
                return this.buildRowStr();
            }
        } catch (Exception e) {
            throw new YssException("获取成本信息出错", e);
        } finally {
            dbl.endTransFinal(conn, false);
        }
        return "";
    }

    private String SecPecPayAnalysis() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;

        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Security);

        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                        " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                        i +
                        " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_broker") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FBrokerCode )x " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker on a.FAnalysisCode" +
                        i + " = broker.FBrokerCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join (select m.FExchangeCode ,m.FExchangeName  as FAnalysisName" +
                        i +
                        " from  (select FExchangeCode from tb_base_exchange " +

                        " where  FCheckState = 1 group by FExchangeCode )x " +
                        " join (select * from tb_base_exchange " +
                        ") m on x.FExchangeCode = m.FExchangeCode) exchange on a.FAnalysisCode" +
                        i + " = exchange.FExchangeCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        "join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";
                } else {
                    sResult = sResult +
                        " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                }
            }
        }
        dbl.closeResultSetFinal(rs); //QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-07-09 关闭结果集
        return sResult;

    }

    private void calCost(String storageDate, double dBaseCuryRate,
                         double dPortCuryRate) throws YssException {
        YssCost cost = null;
        try {
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
                "avgcostcalculate");
            //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
            //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
            costCal.initCostCalcutate(YssFun.toDate(storageDate),
                                      sPortCode,
                                      (sAnalysisCode1 == null ||
                                       sAnalysisCode1.length() == 0 ? " " :
                                       sAnalysisCode1),
                                      (sAnalysisCode2 == null ||
                                       sAnalysisCode2.length() == 0 ? " " :
                                       sAnalysisCode2),
                                      attrClsCode);
            costCal.setYssPub(pub);
            cost = costCal.getCarryCost(sSecurityCode,
                                        this.dAmount,
                                        YssFun.left(sNum +
                "", (sNum + "").length() - 5),
                                        dBaseCuryRate,
                                        dPortCuryRate);
            costCal.roundCost(cost, 2);
            this.dCost = cost.getCost();
            this.dMCost = cost.getMCost();
            this.dVCost = cost.getVCost();
            this.dBaseCuryCost = cost.getBaseCost();
            this.dMBaseCuryCost = cost.getBaseMCost();
            this.dVBaseCuryCost = cost.getBaseVCost();
            this.dPortCuryCost = cost.getPortCost();
            this.dMPortCuryCost = cost.getPortMCost();
            this.dVPortCuryCost = cost.getPortVCost();
        } catch (Exception e) {
            throw new YssException("获取成本信息出错" + "\r\n" + e.getMessage(), e);
        }

    }

    private String getRelaSub(String key) throws YssException {
        String sRes = "";
        if (this.hmRelaSub.get(key) != null) {
            sRes = (String) hmRelaSub.get(key);
        }
        sRes = sRes.replaceAll("\t", "~");
        return sRes;
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
}
