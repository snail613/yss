package com.yss.main.operdata.manualdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import java.math.BigDecimal;

/**
 * <p>Title: 外汇交易业务类</p>
 *
 * <p>Description: </p>
 *
 * add by wangzuochun 2009.09.30 MS00708 新增银行费用、汇出入、结售汇、银行存款余额、净值与份额信息维护和导出功能 QDV4中行2009年09月22日01_A
 */
public class ForeignExchangeBean
    extends BaseDataSettingBean implements IDataSetting {

    private String customerCode = ""; //客户代码
    private String customerName = ""; //客户名称
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String tradeDate = ""; //业务日期
    private String exchangeType = ""; //外汇类型：结汇或购汇 0：结汇 1：购汇
    private String origlCuryCode = ""; //原资金币种代码
    private String origlCuryName = ""; //原资金币种名称
    private String newCuryCode = ""; //原资金币种代码
    private String newCuryName = ""; //原资金币种名称
    private BigDecimal origlTradeMoney = new BigDecimal(-1); //原资金金额
    private BigDecimal newTradeMoney = new BigDecimal(-1); //新资金金额
    private String myDeptCode = ""; //我行外汇兑换部门代码
    private String myDeptName = ""; //我行外汇兑换部门名称
    private String otherBankCode = ""; //兑换银行代码
    private String otherBankName = ""; //兑换银行名称

    private String oldCustomerCode = "";
    private String oldPortCode = ""; //组合代码
    private String oldTradeDate = "";

    private String sRecycled = ""; //保存未解析前的字符串
    private ForeignExchangeBean filterType;

    public ForeignExchangeBean() {
    }



    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }


    public String getMyDeptCode() {
        return myDeptCode;
    }

    public String getMyDeptName() {
        return myDeptName;
    }

    public String getOldCustomerCode() {
        return oldCustomerCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getOldTradeDate() {
        return oldTradeDate;
    }

    public String getOtherBankCode() {
        return otherBankCode;
    }

    public String getOtherBankName() {
        return otherBankName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public String getSRecycled() {
        return sRecycled;
    }

    public String getNewCuryCode() {
        return newCuryCode;
    }

    public String getNewCuryName() {
        return newCuryName;
    }

    public BigDecimal getNewTradeMoney() {
        return newTradeMoney;
    }

    public String getOriglCuryCode() {
        return origlCuryCode;
    }

    public String getOriglCuryName() {
        return origlCuryName;
    }

    public BigDecimal getOriglTradeMoney() {
        return origlTradeMoney;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setOtherBankName(String otherBankName) {
        this.otherBankName = otherBankName;
    }

    public void setOtherBankCode(String otherBankCode) {
        this.otherBankCode = otherBankCode;
    }

    public void setOldTradeDate(String oldTradeDate) {
        this.oldTradeDate = oldTradeDate;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setMyDeptName(String myDeptName) {
        this.myDeptName = myDeptName;
    }

    public void setOldCustomerCode(String oldCustomerCode) {
        this.oldCustomerCode = oldCustomerCode;
    }

    public void setMyDeptCode(String myDeptCode) {
        this.myDeptCode = myDeptCode;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public void setSRecycled(String sRecycled) {
        this.sRecycled = sRecycled;
    }

    public void setNewCuryCode(String newCuryCode) {
        this.newCuryCode = newCuryCode;
    }

    public void setNewCuryName(String newCuryName) {
        this.newCuryName = newCuryName;
    }

    public void setNewTradeMoney(BigDecimal newTradeMoney) {
        this.newTradeMoney = newTradeMoney;
    }

    public void setOriglCuryCode(String origlCuryCode) {
        this.origlCuryCode = origlCuryCode;
    }

    public void setOriglCuryName(String origlCuryName) {
        this.origlCuryName = origlCuryName;
    }

    public void setOriglTradeMoney(BigDecimal origlTradeMoney) {
        this.origlTradeMoney = origlTradeMoney;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    /**
     * checkInput
     * 检查用户输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_ForeignExchange"),
                               "FCustomerCode,FTradeDate,FPortCode",
                               this.customerCode + "," + this.tradeDate + "," +
                               this.portCode,
                               this.oldCustomerCode + "," + this.oldTradeDate + ","
                               + this.oldPortCode);
    }

    /**
     * 增加外汇交易业务操作，对应前台的“新建”
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            //SQL插入语句：向外汇交易业务表插入数据
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Data_ForeignExchange") +
                "(FCustomerCode,FTradeDate,FPortCode,FExchangeType,FOriglCuryCode,FNewCuryCode,FOriglTradeMoney,FNewTradeMoney,"+
                "FMyDeptCode,FOtherBankCode,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.customerCode) + "," +
                dbl.sqlDate(this.tradeDate) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.exchangeType) + "," +
                dbl.sqlString(this.origlCuryCode) + "," +
                dbl.sqlString(this.newCuryCode) + "," +
                this.origlTradeMoney + "," +
                this.newTradeMoney + "," +
                dbl.sqlString(this.myDeptCode) + "," +
                dbl.sqlString(this.otherBankCode) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +")";
            System.out.println("SQL=" + strSql);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql); //执行插入操作
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增外汇交易业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
    * 修改外汇交易业务操作，对应前台的“修改”
    * @return String
    * @throws YssException
    */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            //SQL更新语句：更新外汇交易业务表中修改操作所对应的记录
            strSql =
                "update " + pub.yssGetTableName("Tb_Data_ForeignExchange") +
                " set FCustomerCode = " + dbl.sqlString(this.customerCode) +
                ",FTradeDate = " + dbl.sqlDate(this.tradeDate) +
                ",FPortCode = "+ dbl.sqlString(this.portCode) +
                ",FExchangeType = " + dbl.sqlString(this.exchangeType) +
                ",FOriglCuryCode = " + dbl.sqlString(this.origlCuryCode) +
                ",FNewCuryCode = " + dbl.sqlString(this.newCuryCode) +

                ",FOriglTradeMoney = " + this.origlTradeMoney +
                ",FNewTradeMoney = " + this.newTradeMoney +
                ",FMyDeptCode = " + dbl.sqlString(this.myDeptCode) +
                ",FOtherBankCode = " + dbl.sqlString(this.otherBankCode) +

                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FCustomerCode = " + dbl.sqlString(this.oldCustomerCode) +
                " and FTradeDate = " + dbl.sqlDate(this.oldTradeDate) +
                " and FPortCode=" + dbl.sqlString(this.oldPortCode.length() == 0 ? " " : this.oldPortCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql); //执行更新操作
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改外汇交易业务出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
    * 删除外汇交易业务操作，对应前台的“删除”
    * @return String
    * @throws YssException
    */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //SQL更新语句：更新外汇交易业务表中删除操作对应的记录，重新设置审核状态字段FCheckState
            strSql = "update " + pub.yssGetTableName("Tb_Data_ForeignExchange") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FCustomerCode = " +
                dbl.sqlString(this.customerCode) +
                " and FTradeDate = " + dbl.sqlDate(this.tradeDate) +
                " and FPortCode=" + dbl.sqlString(this.portCode);

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除外汇交易业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 可以处理外汇交易业务审核、反审核、回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null &&  (!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //SQL更新语句：更新外汇交易业务表中审核、反审核、还原操作对应的记录，重新设置审核状态字段FCheckState
                    strSql = "update " + pub.yssGetTableName("Tb_Data_ForeignExchange") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FCustomerCode = " +
                        dbl.sqlString(this.customerCode) +
                        " and FTradeDate = " + dbl.sqlDate(this.tradeDate) +
                        " and FPortCode=" + dbl.sqlString(this.portCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核外汇交易业务数据出错", e);
        } finally {
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
     * 从回收站彻底删除数据,单条和多条信息都可以
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
                    //SQL删除语句：从外汇交易业务表中删除清除操作所对应的记录
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_ForeignExchange") +
                        " where FCustomerCode = " +
                        dbl.sqlString(this.customerCode) +
                        " and FTradeDate = " + dbl.sqlDate(this.tradeDate) +
                        " and FPortCode=" +dbl.sqlString(this.portCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("清除外汇交易业务数据出错", e);
        } finally {
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

    /**
     * 按条件查询出外汇交易表的数据并以一定格式显示，并显示回收站的数据
     * @return String
     * @throws YssException
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
          //add by yangheng MS01310  分页查询
            if (this.filterType!=null&&this.filterType.tradeDate.equals("1900-01-01")&&!(pub.isBrown())) {	//20111027 modified by liubo.STORY #1285.
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji

            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                "d.FManagerName as FCustomerName,e.FBankName as FMyDeptName,f.FBankName as FOtherBankName," +
                "h.FPortName as FPortName,k.FCuryName as FOriglCuryName,m.FCuryName as FNewCuryName" + " from " +
                pub.yssGetTableName("Tb_Data_ForeignExchange") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +

                //------ modify by wangzuochun  2010.07.16  MS01449    组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍 QDV4赢时胜(测试)2010年7月15日01_B 
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " select FPortCode, FPortName, FStartDate, FPortCury from " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") + 
                " where FCheckState = 1) h on a.FPortCode = h.FPortCode " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //-------------------------------------------- MS01449 -------------------------------------------//
                
                
                //edited by zhouxiang MS01501   当存在客户名称代码相同，启用日期不同的多条数据时，新建银行费用信息也会产生多条数据
                " left join (select m.FManagerCode, m.FManagerName   from "+pub.yssGetTableName("Tb_Para_Manager")+
                //delete by songjie 2011.03.15 不以最大的启用日期查询数据
//                " m join (select fmanagercode, max(fstartdate) as fstartdate from "+pub.yssGetTableName("Tb_Para_Manager")+
                " m where fcheckstate = 1 "+//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //delete by songjie 2011.03.15 不以最大的启用日期查询数据
//                " group by fmanagercode) n on m.fmanagercode=n.fmanagercode and m.fstartdate=n.fstartdate"+ 
                //end-- by zhouxiang MS01501    当存在客户名称代码相同，启用日期不同的多条数据时，新建银行费用信息也会产生多条数据 
                
                ") d on a.FCustomerCode = d.FManagerCode " +
                " left join (select FBankCode,FBankName from " +
                pub.yssGetTableName("Tb_Para_Bank") + ") e on a.FMyDeptCode = e.FBankCode " +
                " left join (select FBankCode,FBankName from " +
                pub.yssGetTableName("Tb_Para_Bank") + ") f on a.FOtherBankCode = f.FBankCode " +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") + ") k on a.FOriglCuryCode = k.FCuryCode " +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") + ") m on a.FNewCuryCode = m.FCuryCode " +

                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";


			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("ForeignExchange");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
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
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji

        } catch (Exception e) {
            throw new YssException("获取外汇交易业务信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
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
     * 解析前台发送来的字符串
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
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.customerCode = reqAry[0];
            this.portCode = reqAry[1];
            this.tradeDate = reqAry[2];
            this.exchangeType = reqAry[3];
            this.origlCuryCode = reqAry[4];

            if (reqAry[5].length() != 0) {
                this.origlTradeMoney = new BigDecimal(
                    reqAry[5]);
            }
            this.newCuryCode = reqAry[6];
            if (reqAry[7].length() != 0) {
                this.newTradeMoney = new BigDecimal(
                    reqAry[7]);
            }
            this.myDeptCode = reqAry[8];
            this.otherBankCode = reqAry[9];
            this.checkStateId = Integer.parseInt(reqAry[10]);
            this.oldCustomerCode = reqAry[11];
            this.oldPortCode=reqAry[12];
            this.oldTradeDate = reqAry[13];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ForeignExchangeBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析外汇交易业务数据信息出错", e);
        }
    }

    /**
     * 通过拼接字符串来获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.customerCode).append("\t");
        buf.append(this.customerName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.tradeDate).append("\t");
        buf.append(this.exchangeType).append("\t");
        buf.append(this.origlCuryCode).append("\t");
        buf.append(this.origlCuryName).append("\t");
        buf.append(this.origlTradeMoney).append("\t");
        buf.append(this.newCuryCode).append("\t");
        buf.append(this.newCuryName).append("\t");
        buf.append(this.newTradeMoney).append("\t");
        buf.append(this.myDeptCode).append("\t");
        buf.append(this.myDeptName).append("\t");
        buf.append(this.otherBankCode).append("\t");
        buf.append(this.otherBankName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * 筛选条件
     * @throws YssException
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return " where 1=1";
    	//=============end=================
        if (this.filterType != null) {
            sResult = " where 1=1";

            if (this.filterType.customerCode.length() != 0) {
                sResult = sResult + " and a.FCustomerCode like '" +
                    filterType.customerCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.tradeDate.length() != 0 &&
                !this.filterType.tradeDate.equals("9998-12-31")&&!this.filterType.tradeDate.equals("1900-01-01")) {//fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
                sResult = sResult + " and a.FTradeDate = " +
                    dbl.sqlDate(filterType.tradeDate);
            }

            if (this.filterType.exchangeType.length() != 0) {
                sResult = sResult + " and a.FExchangeType like '" +
                    filterType.exchangeType.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.origlCuryCode.length() != 0) {
                sResult = sResult + " and a.FOriglCuryCode like '" +
                    filterType.origlCuryCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.newCuryCode.length() != 0) {
                sResult = sResult + " and a.FNewCuryCode like '" +
                    filterType.newCuryCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.myDeptCode.length() != 0) {
                sResult = sResult + " and a.FMyDeptCode like '" +
                    filterType.myDeptCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.otherBankCode.length() != 0) {
                sResult = sResult + " and a.FOtherBankCode like '" +
                    filterType.otherBankCode.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * 为各项变量赋值
     * @param rs ResultSet
     * @throws SQLException
     * @throws YssException
     */
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.customerCode = rs.getString("FCustomerCode") + "";
        this.customerName = rs.getString("FCustomerName") + "";
        this.tradeDate = rs.getDate("FTradeDate") + "";
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        this.exchangeType = rs.getString("FExchangeType");
        this.origlCuryCode = rs.getString("FOriglCuryCode");
        this.origlCuryName = rs.getString("FOriglCuryName");
        this.origlTradeMoney = rs.getBigDecimal("FOriglTradeMoney");
        this.newCuryCode = rs.getString("FNewCuryCode");
        this.newCuryName = rs.getString("FNewCuryName");
        this.newTradeMoney = rs.getBigDecimal("FNewTradeMoney");
        this.myDeptCode = rs.getString("FMyDeptCode");
        this.myDeptName = rs.getString("FMyDeptName");
        this.otherBankCode = rs.getString("FOtherBankCode");
        this.otherBankName = rs.getString("FOtherBankName");
        super.setRecLog(rs);
    }
}
