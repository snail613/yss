package com.yss.main.operdata;

import java.sql.*;
import java.util.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * add by wangzuochun 2009.07.22 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
 */
public class TradeAssetSplitBean extends BaseDataSettingBean implements IDataSetting {

    private String num = ""; //编号
    private String tradeType = ""; //交易类
    private int iTradeAmount; //交易类股数
    private String sellType = ""; //可供出售类
    private int iSellAmount; //可供出售类股数
    private String holdType = ""; //持有到期类
    private int iHoldAmount; //持有到期类股数
    private String balanceType = ""; //尾差类型
    private int iTotalAmount; //成交总数量
    private String selectedPage; //前台选定页面的名称
    private String strMultDel = "";
    
    //add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A-------------start
    private String categoryCode;//添加品种类型        
    public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	//add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A-------------end
	
    private TradeSubBean subBean = new TradeSubBean();

    public TradeAssetSplitBean() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void checkSetting() throws YssException {
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

    public void deleteRecycleData() throws YssException {
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
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        TradeSubBean tradeSubBean = new TradeSubBean();
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx().
                                    getBean("tradedeal");

        strSql =
            "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
            " d.FPortName, e.FSecurityName, e.FHandAmount, f.FInvMgrName, g.FTradeTypeName, " +
            " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName, e.FFactor," +
            //edit by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName
            " oo.FCashAccName as FFactCashAccName,ee.FFactRate, ts.fseatname, sh.fstockholdername, ooo.Fcashaccname as FETFBalaAcctName " + //合中保的版本
            " from " + pub.yssGetTableName("Tb_Data_SplitSubTrade") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +

            " left join (select FPortCode, FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            ") d on a.FPortCode = d.FPortCode " +
            " left join (select FSecurityCode, FSecurityName, FHandAmount, FFactor from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") e on a.FSecurityCode = e.FSecurityCode " +
            " left join (select FInvMgrCode, FInvMgrName from " +
            pub.yssGetTableName("Tb_Para_InvestManager") +
            ") f on a.FInvMgrCode = f.FInvMgrCode " +
            " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType) g on a.FTradeTypeCode = g.FTradeTypeCode" +
            " left join (select FBrokerCode, FBrokerName from " +
            pub.yssGetTableName("Tb_Para_Broker") +
            ") h on a.FBrokerCode = h.FBrokerCode " +
            " left join (select FCashAccCode, FCashAccName from " +
            pub.yssGetTableName("Tb_Para_CashAccount") +
            ") o on a.FCashAccCode = o.FCashAccCode " +
            " left join (select FAttrClsCode,FAttrClsName from " +
            pub.yssGetTableName("Tb_Para_AttributeClass") +
            " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode " +
            " left join (select FCashAccCode, FCashAccName from " +
            pub.yssGetTableName("Tb_Para_CashAccount") +
            ") oo on a.FFactCashAccCode = oo.FCashAccCode " +
            //---add by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName start---//
            " left join (select FCashAccCode, FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") + 
            " where FCheckState = 1) ooo on a.Fetfbalaacctcode = ooo.FCashAccCode " +
            //---add by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName end---//
            " left join (select FSecurityCode,FFactRate from " +
            pub.yssGetTableName("tb_para_fixinterest") +
            ") ee on a.FSecurityCode = ee.FSecurityCode " +
            " LEFT JOIN (SELECT FSeatCode, FSeatName" +
            " FROM " + pub.yssGetTableName("Tb_Para_Tradeseat") + 
            " WHERE FCheckState = 1) ts ON a.fseatCode = ts.fseatcode" +
            " LEFT JOIN (SELECT FStockholderCode, FStockholderName" +
            " FROM " + pub.yssGetTableName("Tb_Para_Stockholder") + 
            " WHERE FCheckState = 1) sh ON a.fstockholdercode = sh.fstockholdercode " + buildSearchSql();

        try {
            sHeader = this.getListView1Headers();
            tradeSubBean.setYssPub(pub);
            tradeOper.setYssPub(pub);
            tradeOper.initTradeOperation(tradeSubBean.getSecurityCode(), tradeSubBean.getBrokerCode(),
                                         tradeSubBean.getInvMgrCode(),
                                         tradeSubBean.getTradeCode(), tradeSubBean.getOrderNum(),
                                         YssFun.toDate(tradeSubBean.getBargainDate()),
                                         "");

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                tradeSubBean.setResultSetAttr(rs);
                tradeSubBean.setHandAmount(tradeOper.getHave(rs.getString("FPortCode")));
                bufAll.append(tradeSubBean.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() -
                    2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取已拆分交易数据出错", e);
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
     * 解析前台发送过来的字符串
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String tempStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            //解析前台批量删除已拆分交易数据发送到后台的字符串
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                strMultDel = sRowStr.split("\f\n\f\n\f\n")[1];
            }
            //解析前台修改已拆分交易数据发送到后台的字符串
            else if (sRowStr.indexOf("\r\t") > 0){
                subBean.setYssPub(pub);
                reqAry = sRowStr.split("\r\t");
                subBean.parseRowStr(sRowStr);
            }
            //当按前台查询条件从已拆分交易数据子表中查出已拆分数据时，需要用此解析
            else if (sRowStr.indexOf("\r\f\r\f") >= 0){
                tempStr = sRowStr.split("\r\f\r\f")[1];
                reqAry = tempStr.split("\t");
                subBean.setPortCode(reqAry[0]);
                subBean.setTradeCode(reqAry[1]);
                subBean.setSecurityCode(reqAry[2]);
                subBean.setRateDate(reqAry[3]);
                subBean.setBargainDate(reqAry[4]);
                subBean.setMatureDate(reqAry[5]);
                //add by songjie 2011.03.18 BUG:1351 QDV4赢时胜(测试)2011年3月10日01_B
                if(reqAry != null && reqAry.length >= 7){
                	subBean.setBargainDateEnd(reqAry[6]);
                }
                //add by songjie 2011.03.18 BUG:1351 QDV4赢时胜(测试)2011年3月10日01_B
                //品种类型 add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A-----start
                if(reqAry != null && reqAry.length >= 8){
                	subBean.setCategoryCode(reqAry[7]);
                }
                //品种类型 add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A------end
            }
            //解析资产类型拆分发送到后台的字符串
            else{
                reqAry = sRowStr.split("\t");
                this.num = reqAry[0];
                this.tradeType = reqAry[1];
                if (reqAry[2].length() != 0) {
                    this.iTradeAmount = Integer.parseInt(
                        reqAry[2]);
                }
                this.sellType = reqAry[3];
                if (reqAry[4].length() != 0) {
                    this.iSellAmount = Integer.parseInt(
                        reqAry[4]);
                }
                this.holdType = reqAry[5];
                if (reqAry[6].length() != 0) {
                    this.iHoldAmount = Integer.parseInt(
                        reqAry[6]);
                }
                this.balanceType = reqAry[7];
                if (reqAry[8].length() != 0) {
                    this.iTotalAmount = Integer.parseInt(
                        reqAry[8]);
                }
                this.selectedPage = reqAry[9];
            }
        } catch (Exception e) {
            throw new YssException("解析交易子数据拆分请求出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        return "";
    }
    /**
     * 对交易子数据进行拆分
     * @return String
     * @throws YssException
     */
    public String assetSplit() throws YssException{
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        ResultSet rs = null;
        ResultSet rs2 = null;
        PreparedStatement pst = null;
        String strSqlDelSub = "";
        String strSqlDelSub2 = "";
        String strSqlDelSplit = "";
        String strSqlSelect = "";
        String strSqlSelect2 = "";
        String strSqlInsertSplit = "";
        String strSqlInsertSub = "";
        int iCount = 0;
        double[] dFeeMoney = new double[12]; //此数组用于存储原记录中的8个费用和原记录中的成交金额、实收金额、实际结算金额、应计利息，原记录的1个费用拆分成3个类型的费用
        double[] dTradeFee = new double[12]; //交易类费用数组：用于存储对原记录8个费用进行拆分后的产生8个交易类费用和原记录中的成交金额、实收金额、实际结算金额、应计利息
        double[] dSellFee = new double[12];  //可供出售类费用数组：用于存储对原记录8个费用进行拆分后的产生8个可供出售类费用和原记录中的成交金额、实收金额、实际结算金额、应计利息
        double[] dHoldFee = new double[12];  //持有到期类费用数组：用于存储对原记录8个费用进行拆分后的产生8个持有到期类费用和原记录中的成交金额、实收金额、实际结算金额、应计利息
        ArrayList list = new ArrayList();
        ArrayList numList = new ArrayList();
        TradeSubBean tradeSubTrade = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //SQL删除语句：根据编号删除交易数据子表的记录，当所要拆分的对象为前台未审核分页下的数据时，将用到此语句
            strSqlDelSub = "delete from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FNum = " +
                dbl.sqlString(this.num);
            //SQL删除语句：删除已拆分交易数据子表的记录，当所要拆分的对象为前台已拆分分页下的数据时，将用到此语句
            strSqlDelSplit = "delete from " +
                pub.yssGetTableName("Tb_Data_SplitSubTrade") +
                " where FNum = " +
                dbl.sqlString(this.num);
            //SQL删除语句：根据关联编号删除交易数据子表的记录，当所要拆分的对象为前台已拆分分页下的数据时，将用到此语句
            strSqlDelSub2 = "delete from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FSplitNum = " +
                dbl.sqlString(this.num);
            //SQL查询语句：根据编号查交易数据子表，当所要拆分的对象为前台未审核分页下的数据时，将用到此语句
            strSqlSelect = "select * from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FNum = " +
                dbl.sqlString(this.num);
            //SQL查询语句：根据编号查已拆分交易数据子表，当所要拆分的对象为前台已拆分分页下的数据时，将用到此语句
            strSqlSelect2 = "select * from " +
                pub.yssGetTableName("Tb_Data_SplitSubTrade") +
                " where FNum = " +
                dbl.sqlString(this.num);

            // 如果当所要拆分的对象为前台已拆分分页下的数据时
            if(this.selectedPage.equalsIgnoreCase("pagesplit")){
                rs = dbl.openResultSet(strSqlSelect2);//打开已拆分交易数据子表记录集
            }
            // 如果当所要拆分的对象为前台未审核分页下且是未被拆分的数据时
            else{
                rs = dbl.openResultSet(strSqlSelect);//打开交易数据子表记录集
            }

            if (rs.next()) {
                // 循环获取原记录中的8个费用和成交金额、实收金额、实际结算金额、应计利息，并将这12个值存储于数组dFeeMoney[]中
                for (int i = 1; i <= 12; i++) {
                    if (i <= 8) { // 8个费用
                        if (rs.getString("FTradeFee" + i) != null) {
                            dFeeMoney[i - 1] = rs.getDouble("FTradeFee" + i);
                        }
                    } else if (i == 9) { // 成交金额
                        if (rs.getString("FTRADEMONEY") != null) {
                            dFeeMoney[i - 1] = rs.getDouble("FTRADEMONEY");
                        }
                    } else if (i == 10) { // 实收金额
                        if (rs.getString("FTotalCost") != null) {
                            dFeeMoney[i - 1] = rs.getDouble("FTotalCost");
                        }
                    } else if (i == 11) { // 实际结算金额
                        if (rs.getString("FFACTSETTLEMONEY") != null) {
                            dFeeMoney[i - 1] = rs.getDouble("FFACTSETTLEMONEY");
                        }
                    } else if (i == 12) { // 应计利息
                        if (rs.getString("FAccruedInterest") != null) {
                            dFeeMoney[i - 1] = rs.getDouble("FAccruedInterest");
                        }
                    }
                }
                // 循环拆分数组dFeeMoney[]中的每个值，并将每个值拆分成3种类型的值，分别存储于数组dTradeFee[],dSellFee[],dHoldFee[]中
                for (int i = 0; i < 12; i++) {
                    if (this.balanceType.equalsIgnoreCase("max")) { //尾差归入成交量最大类型
                        if (this.iTradeAmount >= this.iSellAmount && this.iTradeAmount >= this.iHoldAmount) {
                            dSellFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iSellAmount / (double)this.iTotalAmount), 2);
                            dHoldFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iHoldAmount / (double)this.iTotalAmount), 2);
                            dTradeFee[i] = dFeeMoney[i] - (dSellFee[i] + dHoldFee[i]);
                        } else if (this.iSellAmount >= this.iTradeAmount && this.iSellAmount >= this.iHoldAmount) {
                            dTradeFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iTradeAmount / (double)this.iTotalAmount), 2);
                            dHoldFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iHoldAmount / (double)this.iTotalAmount), 2);
                            dSellFee[i] = dFeeMoney[i] - (dTradeFee[i] + dHoldFee[i]);
                        } else if (this.iHoldAmount >= this.iTradeAmount && this.iHoldAmount >= this.iSellAmount) {
                            dTradeFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iTradeAmount / (double)this.iTotalAmount), 2);
                            dSellFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iSellAmount / (double)this.iTotalAmount), 2);
                            dHoldFee[i] = dFeeMoney[i] - (dTradeFee[i] + dSellFee[i]);
                        }
                    } else if (this.balanceType.equalsIgnoreCase("min")) { //尾差归入成交量最小类型
                        if (this.iTradeAmount <= this.iSellAmount && this.iTradeAmount <= this.iHoldAmount) {
                            if (this.iTradeAmount == 0) {
                                if (this.iSellAmount <= this.iHoldAmount) {
                                    dHoldFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iHoldAmount / (double)this.iTotalAmount), 2);
                                    dSellFee[i] = dFeeMoney[i] - dHoldFee[i];
                                    dTradeFee[i] = 0;
                                } else {
                                    dSellFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iSellAmount / (double)this.iTotalAmount), 2);
                                    dHoldFee[i] = dFeeMoney[i] - dSellFee[i];
                                    dTradeFee[i] = 0;
                                }
                            } else {
                                dSellFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iSellAmount / (double)this.iTotalAmount), 2);
                                dHoldFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iHoldAmount / (double)this.iTotalAmount), 2);
                                dTradeFee[i] = dFeeMoney[i] - (dSellFee[i] + dHoldFee[i]);
                            }
                        } else if (this.iSellAmount <= this.iTradeAmount && this.iSellAmount <= this.iHoldAmount) {
                            if (this.iSellAmount == 0) {
                                if (this.iTradeAmount <= this.iHoldAmount) {
                                    dHoldFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iHoldAmount / (double)this.iTotalAmount), 2);
                                    dTradeFee[i] = dFeeMoney[i] - dHoldFee[i];
                                    dSellFee[i] = 0;
                                } else {
                                    dTradeFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iTradeAmount / (double)this.iTotalAmount), 2);
                                    dHoldFee[i] = dFeeMoney[i] - dTradeFee[i];
                                    dSellFee[i] = 0;
                                }
                            } else {
                                dTradeFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iTradeAmount / (double)this.iTotalAmount), 2);
                                dHoldFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iHoldAmount / (double)this.iTotalAmount), 2);
                                dSellFee[i] = dFeeMoney[i] - (dTradeFee[i] + dHoldFee[i]);
                            }
                        } else if (this.iHoldAmount <= this.iTradeAmount && this.iHoldAmount <= this.iSellAmount) {
                            if (this.iHoldAmount == 0) {
                                if (this.iTradeAmount <= this.iSellAmount) {
                                    dSellFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iSellAmount / (double)this.iTotalAmount), 2);
                                    dTradeFee[i] = dFeeMoney[i] - dSellFee[i];
                                    dHoldFee[i] = 0;
                                } else {
                                    dTradeFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iTradeAmount / (double)this.iTotalAmount), 2);
                                    dSellFee[i] = dFeeMoney[i] - dTradeFee[i];
                                    dHoldFee[i] = 0;
                                }
                            } else {
                                dTradeFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iTradeAmount / (double)this.iTotalAmount), 2);
                                dSellFee[i] = YssD.round(dFeeMoney[i] * ( (double)this.iSellAmount / (double)this.iTotalAmount), 2);
                                dHoldFee[i] = dFeeMoney[i] - (dTradeFee[i] + dSellFee[i]);
                            }
                        }
                    }
                }

                numList = getNumList();
                for (int i = 0; i < 4; i++) {

                    // 设置交易类交易子数据对象的部分属性
                    if (i == 0 && this.iTradeAmount > 0) {
                        tradeSubTrade = new TradeSubBean();

                        tradeSubTrade.setNum(numList.get(i).toString()); //设置第一个编号
                        tradeSubTrade.setFTradeFee1(dTradeFee[0]); //设置费用1
                        tradeSubTrade.setFTradeFee2(dTradeFee[1]);
                        tradeSubTrade.setFTradeFee3(dTradeFee[2]);
                        tradeSubTrade.setFTradeFee4(dTradeFee[3]);
                        tradeSubTrade.setFTradeFee5(dTradeFee[4]);
                        tradeSubTrade.setFTradeFee6(dTradeFee[5]);
                        tradeSubTrade.setFTradeFee7(dTradeFee[6]);
                        tradeSubTrade.setFTradeFee8(dTradeFee[7]);
                        tradeSubTrade.setTradeMoney(dTradeFee[8]); //设置成交金额
                        tradeSubTrade.setTotalCost(dTradeFee[9]); //设置实收金额
                        tradeSubTrade.setFactSettleMoney(dTradeFee[10]); //设置实际结算金额
                        tradeSubTrade.setAccruedInterest(dTradeFee[11]); //设置应计利息
                        tradeSubTrade.setTradeAmount(this.iTradeAmount); //设置交易数量
                        tradeSubTrade.setSplitNum(this.num); //设置关联编号
                        tradeSubTrade.setInvestType("C"); // 设置投资类型

                        tradeSubTrade.setSecurityCode(rs.getString("FSECURITYCODE"));
                        tradeSubTrade.setPortCode(rs.getString("FPORTCODE"));
                        tradeSubTrade.setBrokerCode(rs.getString("FBROKERCODE"));
                        tradeSubTrade.setInvMgrCode(rs.getString("FINVMGRCODE"));
                        tradeSubTrade.setTradeCode(rs.getString("FTRADETYPECODE"));
                        tradeSubTrade.setCashAcctCode(rs.getString("FCASHACCCODE"));
                        tradeSubTrade.setTradeSeatCode(rs.getString("FSeatCode"));
                        tradeSubTrade.setStockholderCode(rs.getString("FStockholdercode"));
                        tradeSubTrade.setAttrClsCode(rs.getString("FATTRCLSCODE"));
                        tradeSubTrade.setBargainDate( (rs.getDate("FBARGAINDATE")).toString());
                        tradeSubTrade.setBargainTime(rs.getString("FBARGAINTIME"));
                        tradeSubTrade.setRateDate(rs.getString("FRateDate") == null ? (rs.getDate("FBARGAINDATE")).toString() : (rs.getDate("FRateDate")).toString());
                        tradeSubTrade.setSettleDate( (rs.getDate("FSETTLEDATE")).toString());
                        tradeSubTrade.setFactSettleDate(rs.getString("FFactSettleDate") == null ? null : (rs.getDate("FFactSettleDate")).toString());
                        tradeSubTrade.setMatureDate(rs.getDate("FMATUREDATE") == null ? null : (rs.getDate("FMATUREDATE")).toString());
                        tradeSubTrade.setMatureSettleDate(rs.getDate("FMATURESETTLEDATE") == null ? null : (rs.getDate("FMATURESETTLEDATE")).toString());
                        tradeSubTrade.setSettleTime(rs.getString("FSETTLETIME"));
                        tradeSubTrade.setAutoSettle(rs.getString("FAUTOSETTLE"));
                        tradeSubTrade.setPortCuryRate(rs.getDouble("FPORTCURYRATE"));
                        tradeSubTrade.setBaseCuryRate(rs.getDouble("FBASECURYRATE"));
                        tradeSubTrade.setAllotProportion(rs.getDouble("FALLOTPROPORTION"));
                        tradeSubTrade.setOldAllotAmount(rs.getDouble("FOLDALLOTAMOUNT"));
                        tradeSubTrade.setAllotFactor(rs.getDouble("FALLOTFACTOR"));
                        tradeSubTrade.setTradePrice(rs.getDouble("FTRADEPRICE"));
                        //tradeSubTrade.setAccruedInterest(rs.getDouble("FACCRUEDINTEREST"));
                        tradeSubTrade.setBailMoney(rs.getDouble("FBailMoney"));
                        tradeSubTrade.setFFeeCode1(rs.getString("FFeeCode1"));
                        tradeSubTrade.setFFeeCode2(rs.getString("FFeeCode2"));
                        tradeSubTrade.setFFeeCode3(rs.getString("FFeeCode3"));
                        tradeSubTrade.setFFeeCode4(rs.getString("FFeeCode4"));
                        tradeSubTrade.setFFeeCode5(rs.getString("FFeeCode5"));
                        tradeSubTrade.setFFeeCode6(rs.getString("FFeeCode6"));
                        tradeSubTrade.setFFeeCode7(rs.getString("FFeeCode7"));
                        tradeSubTrade.setFFeeCode8(rs.getString("FFeeCode8"));
                        tradeSubTrade.setOrderNum(rs.getString("FOrderNum"));
                        tradeSubTrade.setDesc(rs.getString("FDesc"));
                        tradeSubTrade.setDataSource(rs.getInt("FDataSource"));
                        tradeSubTrade.checkStateId = 0;
                        tradeSubTrade.creatorCode = rs.getString("FCreator");
                        tradeSubTrade.creatorTime = rs.getString("FCreateTime");
                        tradeSubTrade.checkUserCode = rs.getString("FCheckUser");
                        tradeSubTrade.setFactCashAccCode(rs.getString("FFactCashAccCode"));
                        //tradeSubTrade.setFactSettleMoney(rs.getDouble("FFactSettleMoney"));
                        tradeSubTrade.setExRate(rs.getDouble("FExRate"));
                        tradeSubTrade.setSettleDesc(rs.getString("FSettleDesc"));
                        tradeSubTrade.setFactBaseRate(rs.getDouble("FFactBaseRate"));
                        tradeSubTrade.setFactPortRate(rs.getDouble("FFactPortRate"));
                        tradeSubTrade.setETFBalaAcctCode(rs.getString("FETFBalaAcctCode"));
                        tradeSubTrade.setETFBalaSettleDate(rs.getString("FETFBalaSettleDate") == null ? null : (rs.getDate("FETFBalaSettleDate")).toString());
                        tradeSubTrade.setETFBalanceMoney(rs.getDouble("FETFBalaMoney"));
                        tradeSubTrade.setETFCashAlternat(rs.getDouble("FETFCashAlternat"));
                        list.add(tradeSubTrade); //将交易子数据对象添加到list中
                    }
                    // 设置可供出售类交易子数据对象的部分属性
                    if (i == 1 && this.iSellAmount > 0) {
                        tradeSubTrade = new TradeSubBean();

                        tradeSubTrade.setNum(numList.get(i).toString()); //设置第二个编号
                        tradeSubTrade.setFTradeFee1(dSellFee[0]);
                        tradeSubTrade.setFTradeFee2(dSellFee[1]);
                        tradeSubTrade.setFTradeFee3(dSellFee[2]);
                        tradeSubTrade.setFTradeFee4(dSellFee[3]);
                        tradeSubTrade.setFTradeFee5(dSellFee[4]);
                        tradeSubTrade.setFTradeFee6(dSellFee[5]);
                        tradeSubTrade.setFTradeFee7(dSellFee[6]);
                        tradeSubTrade.setFTradeFee8(dSellFee[7]);
                        tradeSubTrade.setTradeMoney(dSellFee[8]);
                        tradeSubTrade.setTotalCost(dSellFee[9]);
                        tradeSubTrade.setFactSettleMoney(dSellFee[10]); //设置实际结算金额
                        tradeSubTrade.setAccruedInterest(dSellFee[11]); //设置应计利息
                        tradeSubTrade.setTradeAmount(this.iSellAmount);
                        tradeSubTrade.setSplitNum(this.num);
                        tradeSubTrade.setInvestType("S");

                        tradeSubTrade.setSecurityCode(rs.getString("FSECURITYCODE"));
                        tradeSubTrade.setPortCode(rs.getString("FPORTCODE"));
                        tradeSubTrade.setBrokerCode(rs.getString("FBROKERCODE"));
                        tradeSubTrade.setInvMgrCode(rs.getString("FINVMGRCODE"));
                        tradeSubTrade.setTradeCode(rs.getString("FTRADETYPECODE"));
                        tradeSubTrade.setCashAcctCode(rs.getString("FCASHACCCODE"));
                        tradeSubTrade.setTradeSeatCode(rs.getString("FSeatCode"));
                        tradeSubTrade.setStockholderCode(rs.getString("FStockholdercode"));
                        tradeSubTrade.setAttrClsCode(rs.getString("FATTRCLSCODE"));
                        tradeSubTrade.setBargainDate( (rs.getDate("FBARGAINDATE")).toString());
                        tradeSubTrade.setBargainTime(rs.getString("FBARGAINTIME"));
                        tradeSubTrade.setRateDate(rs.getString("FRateDate") == null ? (rs.getDate("FBARGAINDATE")).toString() : (rs.getDate("FRateDate")).toString());
                        tradeSubTrade.setSettleDate( (rs.getDate("FSETTLEDATE")).toString());
                        tradeSubTrade.setFactSettleDate(rs.getString("FFactSettleDate") == null ? null : (rs.getDate("FFactSettleDate")).toString());
                        tradeSubTrade.setMatureDate(rs.getDate("FMATUREDATE") == null ? null : (rs.getDate("FMATUREDATE")).toString());
                        tradeSubTrade.setMatureSettleDate(rs.getDate("FMATURESETTLEDATE") == null ? null : (rs.getDate("FMATURESETTLEDATE")).toString());
                        tradeSubTrade.setSettleTime(rs.getString("FSETTLETIME"));
                        tradeSubTrade.setAutoSettle(rs.getString("FAUTOSETTLE"));
                        tradeSubTrade.setPortCuryRate(rs.getDouble("FPORTCURYRATE"));
                        tradeSubTrade.setBaseCuryRate(rs.getDouble("FBASECURYRATE"));
                        tradeSubTrade.setAllotProportion(rs.getDouble("FALLOTPROPORTION"));
                        tradeSubTrade.setOldAllotAmount(rs.getDouble("FOLDALLOTAMOUNT"));
                        tradeSubTrade.setAllotFactor(rs.getDouble("FALLOTFACTOR"));
                        tradeSubTrade.setTradePrice(rs.getDouble("FTRADEPRICE"));
                        //tradeSubTrade.setAccruedInterest(rs.getDouble("FACCRUEDINTEREST"));
                        tradeSubTrade.setBailMoney(rs.getDouble("FBailMoney"));
                        tradeSubTrade.setFFeeCode1(rs.getString("FFeeCode1"));
                        tradeSubTrade.setFFeeCode2(rs.getString("FFeeCode2"));
                        tradeSubTrade.setFFeeCode3(rs.getString("FFeeCode3"));
                        tradeSubTrade.setFFeeCode4(rs.getString("FFeeCode4"));
                        tradeSubTrade.setFFeeCode5(rs.getString("FFeeCode5"));
                        tradeSubTrade.setFFeeCode6(rs.getString("FFeeCode6"));
                        tradeSubTrade.setFFeeCode7(rs.getString("FFeeCode7"));
                        tradeSubTrade.setFFeeCode8(rs.getString("FFeeCode8"));
                        tradeSubTrade.setOrderNum(rs.getString("FOrderNum"));
                        tradeSubTrade.setDesc(rs.getString("FDesc"));
                        tradeSubTrade.setDataSource(rs.getInt("FDataSource"));
                        tradeSubTrade.checkStateId = 0;
                        tradeSubTrade.creatorCode = rs.getString("FCreator");
                        tradeSubTrade.creatorTime = rs.getString("FCreateTime");
                        tradeSubTrade.checkUserCode = rs.getString("FCheckUser");
                        tradeSubTrade.setFactCashAccCode(rs.getString("FFactCashAccCode"));
                        //tradeSubTrade.setFactSettleMoney(rs.getDouble("FFactSettleMoney"));
                        tradeSubTrade.setExRate(rs.getDouble("FExRate"));
                        tradeSubTrade.setSettleDesc(rs.getString("FSettleDesc"));
                        tradeSubTrade.setFactBaseRate(rs.getDouble("FFactBaseRate"));
                        tradeSubTrade.setFactPortRate(rs.getDouble("FFactPortRate"));
                        tradeSubTrade.setETFBalaAcctCode(rs.getString("FETFBalaAcctCode"));
                        tradeSubTrade.setETFBalaSettleDate(rs.getString("FETFBalaSettleDate") == null ? null : (rs.getDate("FETFBalaSettleDate")).toString());
                        tradeSubTrade.setETFBalanceMoney(rs.getDouble("FETFBalaMoney"));
                        tradeSubTrade.setETFCashAlternat(rs.getDouble("FETFCashAlternat"));
                        list.add(tradeSubTrade); //将交易子数据对象添加到list中
                    }
                    // 设置持有到期类交易子数据对象的部分属性
                    if (i == 2 && this.iHoldAmount > 0) {
                        tradeSubTrade = new TradeSubBean();

                        tradeSubTrade.setNum(numList.get(i).toString()); //设置第三个编号
                        tradeSubTrade.setFTradeFee1(dHoldFee[0]);
                        tradeSubTrade.setFTradeFee2(dHoldFee[1]);
                        tradeSubTrade.setFTradeFee3(dHoldFee[2]);
                        tradeSubTrade.setFTradeFee4(dHoldFee[3]);
                        tradeSubTrade.setFTradeFee5(dHoldFee[4]);
                        tradeSubTrade.setFTradeFee6(dHoldFee[5]);
                        tradeSubTrade.setFTradeFee7(dHoldFee[6]);
                        tradeSubTrade.setFTradeFee8(dHoldFee[7]);
                        tradeSubTrade.setTradeMoney(dHoldFee[8]);
                        tradeSubTrade.setTotalCost(dHoldFee[9]);
                        tradeSubTrade.setFactSettleMoney(dHoldFee[10]); //设置实际结算金额
                        tradeSubTrade.setAccruedInterest(dHoldFee[11]); //设置应计利息
                        tradeSubTrade.setTradeAmount(this.iHoldAmount);
                        tradeSubTrade.setSplitNum(this.num);
                        tradeSubTrade.setInvestType("F");

                        tradeSubTrade.setSecurityCode(rs.getString("FSECURITYCODE"));
                        tradeSubTrade.setPortCode(rs.getString("FPORTCODE"));
                        tradeSubTrade.setBrokerCode(rs.getString("FBROKERCODE"));
                        tradeSubTrade.setInvMgrCode(rs.getString("FINVMGRCODE"));
                        tradeSubTrade.setTradeCode(rs.getString("FTRADETYPECODE"));
                        tradeSubTrade.setCashAcctCode(rs.getString("FCASHACCCODE"));
                        tradeSubTrade.setTradeSeatCode(rs.getString("FSeatCode"));
                        tradeSubTrade.setStockholderCode(rs.getString("FStockholdercode"));
                        tradeSubTrade.setAttrClsCode(rs.getString("FATTRCLSCODE"));
                        tradeSubTrade.setBargainDate( (rs.getDate("FBARGAINDATE")).toString());
                        tradeSubTrade.setBargainTime(rs.getString("FBARGAINTIME"));
                        tradeSubTrade.setRateDate(rs.getString("FRateDate") == null ? (rs.getDate("FBARGAINDATE")).toString() : (rs.getDate("FRateDate")).toString());
                        tradeSubTrade.setSettleDate( (rs.getDate("FSETTLEDATE")).toString());
                        tradeSubTrade.setFactSettleDate(rs.getString("FFactSettleDate") == null ? null : (rs.getDate("FFactSettleDate")).toString());
                        tradeSubTrade.setMatureDate(rs.getDate("FMATUREDATE") == null ? null : (rs.getDate("FMATUREDATE")).toString());
                        tradeSubTrade.setMatureSettleDate(rs.getDate("FMATURESETTLEDATE") == null ? null : (rs.getDate("FMATURESETTLEDATE")).toString());
                        tradeSubTrade.setSettleTime(rs.getString("FSETTLETIME"));
                        tradeSubTrade.setAutoSettle(rs.getString("FAUTOSETTLE"));
                        tradeSubTrade.setPortCuryRate(rs.getDouble("FPORTCURYRATE"));
                        tradeSubTrade.setBaseCuryRate(rs.getDouble("FBASECURYRATE"));
                        tradeSubTrade.setAllotProportion(rs.getDouble("FALLOTPROPORTION"));
                        tradeSubTrade.setOldAllotAmount(rs.getDouble("FOLDALLOTAMOUNT"));
                        tradeSubTrade.setAllotFactor(rs.getDouble("FALLOTFACTOR"));
                        tradeSubTrade.setTradePrice(rs.getDouble("FTRADEPRICE"));
                        //tradeSubTrade.setAccruedInterest(rs.getDouble("FACCRUEDINTEREST"));
                        tradeSubTrade.setBailMoney(rs.getDouble("FBailMoney"));
                        tradeSubTrade.setFFeeCode1(rs.getString("FFeeCode1"));
                        tradeSubTrade.setFFeeCode2(rs.getString("FFeeCode2"));
                        tradeSubTrade.setFFeeCode3(rs.getString("FFeeCode3"));
                        tradeSubTrade.setFFeeCode4(rs.getString("FFeeCode4"));
                        tradeSubTrade.setFFeeCode5(rs.getString("FFeeCode5"));
                        tradeSubTrade.setFFeeCode6(rs.getString("FFeeCode6"));
                        tradeSubTrade.setFFeeCode7(rs.getString("FFeeCode7"));
                        tradeSubTrade.setFFeeCode8(rs.getString("FFeeCode8"));
                        tradeSubTrade.setOrderNum(rs.getString("FOrderNum"));
                        tradeSubTrade.setDesc(rs.getString("FDesc"));
                        tradeSubTrade.setDataSource(rs.getInt("FDataSource"));
                        tradeSubTrade.checkStateId = 0;
                        tradeSubTrade.creatorCode = rs.getString("FCreator");
                        tradeSubTrade.creatorTime = rs.getString("FCreateTime");
                        tradeSubTrade.checkUserCode = rs.getString("FCheckUser");
                        tradeSubTrade.setFactCashAccCode(rs.getString("FFactCashAccCode"));
                        //tradeSubTrade.setFactSettleMoney(rs.getDouble("FFactSettleMoney"));
                        tradeSubTrade.setExRate(rs.getDouble("FExRate"));
                        tradeSubTrade.setSettleDesc(rs.getString("FSettleDesc"));
                        tradeSubTrade.setFactBaseRate(rs.getDouble("FFactBaseRate"));
                        tradeSubTrade.setFactPortRate(rs.getDouble("FFactPortRate"));
                        tradeSubTrade.setETFBalaAcctCode(rs.getString("FETFBalaAcctCode"));
                        tradeSubTrade.setETFBalaSettleDate(rs.getString("FETFBalaSettleDate") == null ? null : (rs.getDate("FETFBalaSettleDate")).toString());
                        tradeSubTrade.setETFBalanceMoney(rs.getDouble("FETFBalaMoney"));
                        tradeSubTrade.setETFCashAlternat(rs.getDouble("FETFCashAlternat"));
                        list.add(tradeSubTrade); //将交易子数据对象添加到list中
                    }
                    // 如果当所要拆分的对象为前台未审核分页下的数据时，设置已拆分交易子数据对象的部分属性
                    if (i == 3 && this.selectedPage.equalsIgnoreCase("pageunaudit")) {
                        tradeSubTrade = new TradeSubBean();

                        tradeSubTrade.setNum(rs.getString("FNum"));
                        tradeSubTrade.setFTradeFee1(rs.getDouble("FTradeFee1"));
                        tradeSubTrade.setFTradeFee2(rs.getDouble("FTradeFee2"));
                        tradeSubTrade.setFTradeFee3(rs.getDouble("FTradeFee3"));
                        tradeSubTrade.setFTradeFee4(rs.getDouble("FTradeFee4"));
                        tradeSubTrade.setFTradeFee5(rs.getDouble("FTradeFee5"));
                        tradeSubTrade.setFTradeFee6(rs.getDouble("FTradeFee6"));
                        tradeSubTrade.setFTradeFee7(rs.getDouble("FTradeFee7"));
                        tradeSubTrade.setFTradeFee8(rs.getDouble("FTradeFee8"));
                        tradeSubTrade.setTradeMoney(rs.getDouble("FTRADEMONEY"));
                        tradeSubTrade.setTotalCost(rs.getDouble("FTotalCost"));
                        tradeSubTrade.setFactSettleMoney(rs.getDouble("FFACTSETTLEMONEY"));
                        tradeSubTrade.setAccruedInterest(rs.getDouble("FAccruedInterest"));
                        tradeSubTrade.setTradeAmount(rs.getDouble("FTRADEAMOUNT"));
                        //tradeSubTrade.setSplitNum(rs.getString("FSplitNum"));
                        tradeSubTrade.setInvestType(rs.getString("FInvestType"));

                        tradeSubTrade.setSecurityCode(rs.getString("FSECURITYCODE"));
                        tradeSubTrade.setPortCode(rs.getString("FPORTCODE"));
                        tradeSubTrade.setBrokerCode(rs.getString("FBROKERCODE"));
                        tradeSubTrade.setInvMgrCode(rs.getString("FINVMGRCODE"));
                        tradeSubTrade.setTradeCode(rs.getString("FTRADETYPECODE"));
                        tradeSubTrade.setCashAcctCode(rs.getString("FCASHACCCODE"));
                        tradeSubTrade.setTradeSeatCode(rs.getString("FSeatCode"));
                        tradeSubTrade.setStockholderCode(rs.getString("FStockholdercode"));
                        tradeSubTrade.setAttrClsCode(rs.getString("FATTRCLSCODE"));
                        tradeSubTrade.setBargainDate( (rs.getDate("FBARGAINDATE")).toString());
                        tradeSubTrade.setBargainTime(rs.getString("FBARGAINTIME"));
                        tradeSubTrade.setRateDate(rs.getString("FRateDate") == null ? (rs.getDate("FBARGAINDATE")).toString() : (rs.getDate("FRateDate")).toString());
                        tradeSubTrade.setSettleDate( (rs.getDate("FSETTLEDATE")).toString());
                        tradeSubTrade.setFactSettleDate(rs.getString("FFactSettleDate") == null ? null : (rs.getDate("FFactSettleDate")).toString());
                        tradeSubTrade.setMatureDate(rs.getDate("FMATUREDATE") == null ? null : (rs.getDate("FMATUREDATE")).toString());
                        tradeSubTrade.setMatureSettleDate(rs.getDate("FMATURESETTLEDATE") == null ? null : (rs.getDate("FMATURESETTLEDATE")).toString());
                        tradeSubTrade.setSettleTime(rs.getString("FSETTLETIME"));
                        tradeSubTrade.setAutoSettle(rs.getString("FAUTOSETTLE"));
                        tradeSubTrade.setPortCuryRate(rs.getDouble("FPORTCURYRATE"));
                        tradeSubTrade.setBaseCuryRate(rs.getDouble("FBASECURYRATE"));
                        tradeSubTrade.setAllotProportion(rs.getDouble("FALLOTPROPORTION"));
                        tradeSubTrade.setOldAllotAmount(rs.getDouble("FOLDALLOTAMOUNT"));
                        tradeSubTrade.setAllotFactor(rs.getDouble("FALLOTFACTOR"));
                        tradeSubTrade.setTradePrice(rs.getDouble("FTRADEPRICE"));
                        //tradeSubTrade.setAccruedInterest(rs.getDouble("FACCRUEDINTEREST"));
                        tradeSubTrade.setBailMoney(rs.getDouble("FBailMoney"));
                        tradeSubTrade.setFFeeCode1(rs.getString("FFeeCode1"));
                        tradeSubTrade.setFFeeCode2(rs.getString("FFeeCode2"));
                        tradeSubTrade.setFFeeCode3(rs.getString("FFeeCode3"));
                        tradeSubTrade.setFFeeCode4(rs.getString("FFeeCode4"));
                        tradeSubTrade.setFFeeCode5(rs.getString("FFeeCode5"));
                        tradeSubTrade.setFFeeCode6(rs.getString("FFeeCode6"));
                        tradeSubTrade.setFFeeCode7(rs.getString("FFeeCode7"));
                        tradeSubTrade.setFFeeCode8(rs.getString("FFeeCode8"));
                        tradeSubTrade.setOrderNum(rs.getString("FOrderNum"));
                        tradeSubTrade.setDesc(rs.getString("FDesc"));
                        tradeSubTrade.setDataSource(rs.getInt("FDataSource"));
                        tradeSubTrade.checkStateId = 0;
                        tradeSubTrade.creatorCode = rs.getString("FCreator");
                        tradeSubTrade.creatorTime = rs.getString("FCreateTime");
                        tradeSubTrade.checkUserCode = rs.getString("FCheckUser");
                        tradeSubTrade.setFactCashAccCode(rs.getString("FFactCashAccCode"));
                        //tradeSubTrade.setFactSettleMoney(rs.getDouble("FFactSettleMoney"));
                        tradeSubTrade.setExRate(rs.getDouble("FExRate"));
                        tradeSubTrade.setSettleDesc(rs.getString("FSettleDesc"));
                        tradeSubTrade.setFactBaseRate(rs.getDouble("FFactBaseRate"));
                        tradeSubTrade.setFactPortRate(rs.getDouble("FFactPortRate"));
                        tradeSubTrade.setETFBalaAcctCode(rs.getString("FETFBalaAcctCode"));
                        tradeSubTrade.setETFBalaSettleDate(rs.getString("FETFBalaSettleDate") == null ? null : (rs.getDate("FETFBalaSettleDate")).toString());
                        tradeSubTrade.setETFBalanceMoney(rs.getDouble("FETFBalaMoney"));
                        tradeSubTrade.setETFCashAlternat(rs.getDouble("FETFCashAlternat"));
                        list.add(tradeSubTrade); //将交易子数据对象添加到list中
                    }
                }
                if (list.size() > 0) {
                    // 如果当所要拆分的对象为前台未审核分页下的数据时
                    if(this.selectedPage.equalsIgnoreCase("pageunaudit"))
                    {
                        //从list中取出已拆分交易子数据对象
                        tradeSubTrade = (TradeSubBean) list.get(list.size()-1);
                        //SQL插入语句：向已拆分交易数据子表插入一条记录
                        strSqlInsertSplit = "insert into " + pub.yssGetTableName("Tb_Data_SplitSubTrade") +
                            "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                            " FCASHACCCODE,FSeatCode,FStockholdercode,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME,FRateDate," +
                            " FSETTLEDATE,FFactSettleDate,FMATUREDATE,FMATURESETTLEDATE," +
                            " FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
                            " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBailMoney," +
                            " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                            " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
                            " FTotalCost, FOrderNum, FDesc, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser," +
                            " FFactCashAccCode,FFactSettleMoney,FExRate,FSettleDesc,FFactBaseRate,FFactPortRate," +
                            " FETFBalaAcctCode, FETFBalaSettleDate, FETFBalaMoney, FETFCashAlternat,FInvestType)" +
                            " values(" + dbl.sqlString(tradeSubTrade.getNum()) + "," +
                            dbl.sqlString(tradeSubTrade.getSecurityCode()) + "," +
                            dbl.sqlString(tradeSubTrade.getPortCode()) + "," +
                            dbl.sqlString(tradeSubTrade.getBrokerCode()) + "," +
                            dbl.sqlString(tradeSubTrade.getInvMgrCode()) + "," +
                            dbl.sqlString(tradeSubTrade.getTradeCode()) + "," +
                            dbl.sqlString(tradeSubTrade.getCashAcctCode()) + "," +
                            dbl.sqlString(tradeSubTrade.getTradeSeatCode() == null ? "" : tradeSubTrade.getTradeSeatCode()) + "," +

                            dbl.sqlString(tradeSubTrade.getStockholderCode() == null ? "" : tradeSubTrade.getStockholderCode()) + "," +
                            dbl.sqlString(tradeSubTrade.getAttrClsCode() == null ? "" : tradeSubTrade.getAttrClsCode()) + "," +
                            dbl.sqlDate(tradeSubTrade.getBargainDate()) + "," +
                            dbl.sqlString(tradeSubTrade.getBargainTime()) + "," +
                            (tradeSubTrade.getRateDate() == null ? dbl.sqlDate(tradeSubTrade.getBargainDate()) : dbl.sqlDate(tradeSubTrade.getRateDate())) + "," +
                            dbl.sqlDate(tradeSubTrade.getSettleDate()) + "," +
                            (tradeSubTrade.getFactSettleDate() == null ? null : dbl.sqlDate(tradeSubTrade.getFactSettleDate())) + "," +
                            (tradeSubTrade.getMatureDate() == null ? null : dbl.sqlDate(tradeSubTrade.getMatureDate())) + "," +
                            (tradeSubTrade.getMatureSettleDate() == null ? null : dbl.sqlDate(tradeSubTrade.getMatureSettleDate())) + "," +
                            dbl.sqlString(tradeSubTrade.getSettleTime()) + "," +
                            tradeSubTrade.getAutoSettle() + "," +
                            tradeSubTrade.getPortCuryRate() + "," +
                            tradeSubTrade.getBaseCuryRate() + "," +
                            tradeSubTrade.getAllotProportion() + "," +
                            tradeSubTrade.getOldAllotAmount() + "," +
                            tradeSubTrade.getAllotFactor() + "," +
                            tradeSubTrade.getTradeAmount() + "," +
                            tradeSubTrade.getTradePrice() + "," +
                            tradeSubTrade.getTradeMoney() + "," +
                            tradeSubTrade.getAccruedInterest() + "," +
                            tradeSubTrade.getBailMoney() + "," +
                            dbl.sqlString(tradeSubTrade.getFFeeCode1() == null ? "" : tradeSubTrade.getFFeeCode1()) + "," +
                            tradeSubTrade.getFTradeFee1() + "," +
                            dbl.sqlString(tradeSubTrade.getFFeeCode2() == null ? "" : tradeSubTrade.getFFeeCode2()) + "," +
                            tradeSubTrade.getFTradeFee2() + "," +
                            dbl.sqlString(tradeSubTrade.getFFeeCode3() == null ? "" : tradeSubTrade.getFFeeCode3()) + "," +
                            tradeSubTrade.getFTradeFee3() + "," +
                            dbl.sqlString(tradeSubTrade.getFFeeCode4() == null ? "" : tradeSubTrade.getFFeeCode4()) + "," +
                            tradeSubTrade.getFTradeFee4() + "," +
                            dbl.sqlString(tradeSubTrade.getFFeeCode5() == null ? "" : tradeSubTrade.getFFeeCode5()) + "," +
                            tradeSubTrade.getFTradeFee5() + "," +
                            dbl.sqlString(tradeSubTrade.getFFeeCode6() == null ? "" : tradeSubTrade.getFFeeCode6()) + "," +
                            tradeSubTrade.getFTradeFee6() + "," +
                            dbl.sqlString(tradeSubTrade.getFFeeCode7() == null ? "" : tradeSubTrade.getFFeeCode7()) + "," +
                            tradeSubTrade.getFTradeFee7() + "," +
                            dbl.sqlString(tradeSubTrade.getFFeeCode8() == null ? "" : tradeSubTrade.getFFeeCode8()) + "," +
                            tradeSubTrade.getFTradeFee8() + "," +
                            tradeSubTrade.getTotalCost() + "," +
                            dbl.sqlString(tradeSubTrade.getOrderNum() == null ? "" : tradeSubTrade.getOrderNum()) + "," +
                            dbl.sqlString(tradeSubTrade.getDesc() == null ? "" : tradeSubTrade.getDesc()) + "," +
                            tradeSubTrade.getDataSource() + "," +
                            tradeSubTrade.checkStateId + "," +
                            dbl.sqlString(tradeSubTrade.creatorCode) + "," +
                            dbl.sqlString(tradeSubTrade.creatorTime) + "," +
                            dbl.sqlString(tradeSubTrade.checkUserCode == null ? "" : tradeSubTrade.checkUserCode) + "," +
                            dbl.sqlString(tradeSubTrade.getFactCashAccCode()) + "," +
                            tradeSubTrade.getFactSettleMoney() + "," +
                            tradeSubTrade.getExRate() + "," +
                            dbl.sqlString(tradeSubTrade.getSettleDesc() == null ? "" : tradeSubTrade.getSettleDesc()) + "," +
                            tradeSubTrade.getFactBaseRate() + "," +
                            tradeSubTrade.getFactPortRate() + "," +
                            dbl.sqlString(tradeSubTrade.getETFBalaAcctCode() == null ? "" : tradeSubTrade.getETFBalaAcctCode()) + "," +
                            (tradeSubTrade.getETFBalaSettleDate() == null ? null : dbl.sqlDate(tradeSubTrade.getETFBalaSettleDate())) + "," +
                            tradeSubTrade.getETFBalanceMoney() + "," +
                            tradeSubTrade.getETFCashAlternat() + "," +
                            dbl.sqlString(tradeSubTrade.getInvestType() == null ? "" : tradeSubTrade.getInvestType()) +
                            ")";

                        dbl.executeSql(strSqlDelSub); //执行删除操作： 从交易数据子表中删除被拆分的那条记录
                        dbl.executeSql(strSqlDelSplit); //执行删除操作： 在向已拆分交易数据子表插入数据之前，先删除相应的数据
                        dbl.executeSql(strSqlInsertSplit); //执行插入操作： 向已拆分交易数据子表插入数据

                    }
                    // 如果当所要拆分的对象为前台已拆分分页下的数据时
                    else{
                        delCashData(); //删除资金调拨
                        dbl.executeSql(strSqlDelSub2); //执行删除操作：在向交易数据子表插入数据之前，先删除原来的数据
                    }
                    //SQL插入语句：向交易数据子表插入数据
                strSqlInsertSub = "insert into " + pub.yssGetTableName("Tb_Data_SubTrade") +
                    "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                    " FCASHACCCODE,FSeatCode,FStockholdercode,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME,FRateDate," +
                    " FSETTLEDATE,FFactSettleDate,FMATUREDATE,FMATURESETTLEDATE," +
                    " FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
                    " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBailMoney," +
                    " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                    " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
                    " FTotalCost, FOrderNum, FDesc, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser," +
                    " FFactCashAccCode,FFactSettleMoney,FExRate,FSettleDesc,FFactBaseRate,FFactPortRate," +
                    " FETFBalaAcctCode, FETFBalaSettleDate, FETFBalaMoney, FETFCashAlternat,FInvestType,FSplitNum)" +
                    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                pst = conn.prepareStatement(strSqlInsertSub);

                if(this.selectedPage.equalsIgnoreCase("pageUnAudit")){
                    iCount = list.size() - 1;
                }
                if(this.selectedPage.equalsIgnoreCase("pageSplit")){
                    iCount = list.size();
                }

                //从list中循环取出交易子数据对象，向交易数据子表插入3条记录
                for (int i = 0; i < iCount; i++){
                    tradeSubTrade = (TradeSubBean)list.get(i);
                    pst.setString(1, tradeSubTrade.getNum());
                    pst.setString(2, tradeSubTrade.getSecurityCode());
                    pst.setString(3, tradeSubTrade.getPortCode());
                    pst.setString(4, tradeSubTrade.getBrokerCode());
                    pst.setString(5, tradeSubTrade.getInvMgrCode());
                    pst.setString(6, tradeSubTrade.getTradeCode());
                    pst.setString(7, tradeSubTrade.getCashAcctCode());
                    pst.setString(8, tradeSubTrade.getTradeSeatCode() == null ? "" : tradeSubTrade.getTradeSeatCode());
                    pst.setString(9, tradeSubTrade.getStockholderCode() == null ? "" : tradeSubTrade.getStockholderCode());
                    pst.setString(10, tradeSubTrade.getAttrClsCode() == null ? "" : tradeSubTrade.getAttrClsCode());
                    pst.setDate(11, YssFun.toSqlDate(tradeSubTrade.getBargainDate()));
                    pst.setString(12, tradeSubTrade.getBargainTime());
                    pst.setDate(13, tradeSubTrade.getRateDate() == null ? YssFun.toSqlDate(tradeSubTrade.getBargainDate()) : YssFun.toSqlDate(tradeSubTrade.getRateDate()));
                    pst.setDate(14, YssFun.toSqlDate(tradeSubTrade.getSettleDate()));
                    pst.setDate(15, tradeSubTrade.getFactSettleDate() == null ? null : YssFun.toSqlDate(tradeSubTrade.getFactSettleDate()));
                    pst.setDate(16, tradeSubTrade.getMatureDate() == null ? null : YssFun.toSqlDate(tradeSubTrade.getMatureDate()));
                    pst.setDate(17, tradeSubTrade.getMatureSettleDate() == null ? null : YssFun.toSqlDate(tradeSubTrade.getMatureSettleDate()));
                    pst.setString(18, tradeSubTrade.getSettleTime());
                    pst.setString(19, tradeSubTrade.getAutoSettle());
                    pst.setDouble(20, tradeSubTrade.getPortCuryRate());
                    pst.setDouble(21, tradeSubTrade.getBaseCuryRate());
                    pst.setDouble(22, tradeSubTrade.getAllotProportion());
                    pst.setDouble(23, tradeSubTrade.getOldAllotAmount());
                    pst.setDouble(24, tradeSubTrade.getAllotFactor());
                    pst.setDouble(25, tradeSubTrade.getTradeAmount());
                    pst.setDouble(26, tradeSubTrade.getTradePrice());
                    pst.setDouble(27, tradeSubTrade.getTradeMoney());
                    pst.setDouble(28, tradeSubTrade.getAccruedInterest());
                    pst.setDouble(29, tradeSubTrade.getBailMoney());
                    pst.setString(30, tradeSubTrade.getFFeeCode1() == null ? "" : tradeSubTrade.getFFeeCode1());
                    pst.setDouble(31, tradeSubTrade.getFTradeFee1());
                    pst.setString(32, tradeSubTrade.getFFeeCode2() == null ? "" : tradeSubTrade.getFFeeCode2());
                    pst.setDouble(33, tradeSubTrade.getFTradeFee2());
                    pst.setString(34, tradeSubTrade.getFFeeCode3() == null ? "" : tradeSubTrade.getFFeeCode3());
                    pst.setDouble(35, tradeSubTrade.getFTradeFee3());
                    pst.setString(36, tradeSubTrade.getFFeeCode4() == null ? "" : tradeSubTrade.getFFeeCode4());
                    pst.setDouble(37, tradeSubTrade.getFTradeFee4());
                    pst.setString(38, tradeSubTrade.getFFeeCode5() == null ? "" : tradeSubTrade.getFFeeCode5());
                    pst.setDouble(39, tradeSubTrade.getFTradeFee5());
                    pst.setString(40, tradeSubTrade.getFFeeCode6() == null ? "" : tradeSubTrade.getFFeeCode6());
                    pst.setDouble(41, tradeSubTrade.getFTradeFee6());
                    pst.setString(42, tradeSubTrade.getFFeeCode7() == null ? "" : tradeSubTrade.getFFeeCode7());
                    pst.setDouble(43, tradeSubTrade.getFTradeFee7());
                    pst.setString(44, tradeSubTrade.getFFeeCode8() == null ? "" : tradeSubTrade.getFFeeCode8());
                    pst.setDouble(45, tradeSubTrade.getFTradeFee8());
                    pst.setDouble(46, tradeSubTrade.getTotalCost());
                    pst.setString(47, tradeSubTrade.getOrderNum() == null ? "" : tradeSubTrade.getOrderNum());
                    pst.setString(48, tradeSubTrade.getDesc() == null ? "" : tradeSubTrade.getDesc());
                    pst.setInt(49,tradeSubTrade.getDataSource());
                    pst.setInt(50,tradeSubTrade.checkStateId);
                    pst.setString(51,tradeSubTrade.creatorCode);
                    pst.setString(52,tradeSubTrade.creatorTime);
                    pst.setString(53,tradeSubTrade.checkUserCode == null ? "" : tradeSubTrade.checkUserCode);
                    pst.setString(54,tradeSubTrade.getFactCashAccCode());
                    pst.setDouble(55,tradeSubTrade.getFactSettleMoney());
                    pst.setDouble(56,tradeSubTrade.getExRate());
                    pst.setString(57,tradeSubTrade.getSettleDesc() == null ? "" : tradeSubTrade.getSettleDesc());
                    pst.setDouble(58,tradeSubTrade.getFactBaseRate());
                    pst.setDouble(59,tradeSubTrade.getFactPortRate());
                    pst.setString(60,tradeSubTrade.getETFBalaAcctCode() == null ? "" : tradeSubTrade.getETFBalaAcctCode());
                    pst.setDate(61,tradeSubTrade.getETFBalaSettleDate() == null ? null : YssFun.toSqlDate(tradeSubTrade.getETFBalaSettleDate()));
                    pst.setDouble(62,tradeSubTrade.getETFBalanceMoney());
                    pst.setDouble(63,tradeSubTrade.getETFCashAlternat());
                    pst.setString(64,tradeSubTrade.getInvestType() == null ? "" : tradeSubTrade.getInvestType());
                    pst.setString(65,tradeSubTrade.getSplitNum() == null ? "" : tradeSubTrade.getSplitNum());
                    pst.executeUpdate();
                }
                conn.commit(); // 提交事务
                bTrans = false;
                conn.setAutoCommit(true);
                }
            }
        }
        catch (Exception e) {
            throw new YssException("拆分交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs2);
            dbl.closeStatementFinal(pst);
        }
        return "";
    }
    /**
     * 批量删除前台已拆分分页下的数据
     * @return String
     * @throws YssException
     */
    public String delSplitData() throws YssException{
        Connection conn = null;
        String strSql = "";
        PreparedStatement psmt = null;
        boolean bTrans = false;
        TradeSubBean data = null;
        String[] multDel = null;
        try{
            conn = dbl.loadConnection();
            //SQL删除语句： 根据编号从已拆分交易子数据表中删除数据
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_SplitSubTrade") +
                " where FNum = ? ";
            psmt = conn.prepareStatement(strSql);
            if (strMultDel.length() > 0) {
                multDel = strMultDel.split("\f\f\f\f");
                if (multDel.length > 0) {
                    for (int i = 0; i < multDel.length; i++) {
                        data = new TradeSubBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multDel[i]);
                        psmt.setString(1, data.getNum());
                        psmt.addBatch();
                    }
                }
                conn.setAutoCommit(false);
                bTrans = true;
                psmt.executeBatch();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        }
        catch (Exception e) {
            throw new YssException("批量删除出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * 当前台已拆分分页下的数据执行重新拆分或还原至未拆分操作时，需要检查先前由此数据拆分产生的数据的结算状态和审核状态
     * 此方法返回数据的结算状态和审核状态
     * @return String
     * @throws YssException
     */
    public String checkSettleData() throws YssException{
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        try{
            //SQL查询语句：根据关联编号查交易数据子表，当所要拆分的对象为前台已拆分分页下的数据时，将用到此语句
            strSql = "select FNum,FSettleState,FCheckState from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FSettleState = 1 and FSplitNum = " +
                dbl.sqlString(this.num);

            rs = dbl.openResultSet(strSql);

            if (rs.next()) {
                buf.append(rs.getInt("FCheckState"));
                while (rs.next()) {
                    buf.append(rs.getInt("FCheckState"));
                }
                if (buf.toString().indexOf("1") >= 0) {
                    return "1"; //已结算，已审核
                } else {
                    return "2"; //已结算，未审核
                }
            }
            else{
                return "0"; //未结算
            }
        }
        catch (Exception e) {
            throw new YssException("检查拆分后数据的结算状态和审核状态出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 执行前台的还原至未拆分操作
     * @return String
     * @throws YssException
     */
    public String toReturnData() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        ResultSet rs = null;
        String strSqlInsert = "";
        String strSqlDelSplit = "";
        String strSqlDelSub = "";
        String strSqlSelect = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            //SQL删除语句：还原至未拆分时，删除已拆分交易数据子表的记录
            strSqlDelSplit = "delete from " +
                pub.yssGetTableName("Tb_Data_SplitSubTrade") +
                " where FNum = " +
                dbl.sqlString(this.num);

            //SQL删除语句：还原至未拆分时，根据关联编号删除交易数据子表的记录
            strSqlDelSub = "delete from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FSplitNum = " +
                dbl.sqlString(this.num);

            //SQL查询语句：还原至未拆分时，根据编号查已拆分交易数据子表
            strSqlSelect = "select * from " +
                pub.yssGetTableName("Tb_Data_SplitSubTrade") +
                " where FNum = " +
                dbl.sqlString(this.num);

            rs = dbl.openResultSet(strSqlSelect);
            if(rs.next()){
                strSqlInsert = "insert into " + pub.yssGetTableName("Tb_Data_SubTrade") +
                "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                " FCASHACCCODE,FSeatCode,FStockholdercode,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME,FRateDate," +
                " FSETTLEDATE,FFactSettleDate,FMATUREDATE,FMATURESETTLEDATE," +
                " FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
                " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBailMoney," +
                " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
                " FTotalCost, FOrderNum, FDesc, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser," +
                " FFactCashAccCode,FFactSettleMoney,FExRate,FSettleDesc,FFactBaseRate,FFactPortRate," +
                " FETFBalaAcctCode, FETFBalaSettleDate, FETFBalaMoney, FETFCashAlternat,FInvestType)" +
                " values(" + dbl.sqlString(rs.getString("FNum")) + "," +
                dbl.sqlString(rs.getString("FSECURITYCODE")) + "," +
                dbl.sqlString(rs.getString("FPORTCODE")) + "," +
                dbl.sqlString(rs.getString("FBROKERCODE")) + "," +
                dbl.sqlString(rs.getString("FINVMGRCODE")) + "," +
                dbl.sqlString(rs.getString("FTRADETYPECODE")) + "," +
                dbl.sqlString(rs.getString("FCASHACCCODE")) + "," +
                dbl.sqlString(rs.getString("FSeatCode") == null ? "" : rs.getString("FSeatCode")) + "," +

                dbl.sqlString(rs.getString("FStockholdercode") == null ? "" : rs.getString("FStockholdercode")) + "," +
                dbl.sqlString(rs.getString("FATTRCLSCODE") == null ? "" : rs.getString("FATTRCLSCODE")) + "," +
                dbl.sqlDate((rs.getDate("FBARGAINDATE")).toString()) + "," +
                dbl.sqlString(rs.getString("FBARGAINTIME")) + "," +
                (rs.getString("FRateDate") == null ? dbl.sqlDate((rs.getDate("FBARGAINDATE")).toString()) : dbl.sqlDate((rs.getDate("FRateDate")).toString())) + "," +
                dbl.sqlDate((rs.getDate("FSETTLEDATE")).toString()) + "," +
                (rs.getString("FFactSettleDate") == null ? null : dbl.sqlDate((rs.getDate("FFactSettleDate")).toString())) + "," +
                (rs.getString("FMATUREDATE") == null ? null : dbl.sqlDate((rs.getDate("FMATUREDATE")).toString())) + "," +
                (rs.getString("FMATURESETTLEDATE") == null ? null : dbl.sqlDate((rs.getDate("FMATURESETTLEDATE")).toString())) + "," +
                dbl.sqlString(rs.getString("FSETTLETIME")) + "," +
                dbl.sqlString(rs.getString("FAUTOSETTLE")) + "," +

                rs.getDouble("FPORTCURYRATE") + "," +
                rs.getDouble("FBASECURYRATE") + "," +
                rs.getDouble("FALLOTPROPORTION") + "," +
                rs.getDouble("FOLDALLOTAMOUNT") + "," +
                rs.getDouble("FALLOTFACTOR") + "," +
                rs.getDouble("FTRADEAMOUNT") + "," +
                rs.getDouble("FTRADEPRICE") + "," +
                rs.getDouble("FTRADEMONEY") + "," +
                rs.getDouble("FACCRUEDINTEREST") + "," +
                rs.getDouble("FBailMoney") + "," +
                dbl.sqlString(rs.getString("FFeeCode1") == null ? "" : rs.getString("FFeeCode1")) + "," +
                rs.getDouble("FTradeFee1") + "," +
                dbl.sqlString(rs.getString("FFeeCode2") == null ? "" : rs.getString("FFeeCode2")) + "," +
                rs.getDouble("FTradeFee2") + "," +
                dbl.sqlString(rs.getString("FFeeCode3") == null ? "" : rs.getString("FFeeCode3")) + "," +
                rs.getDouble("FTradeFee3") + "," +
                dbl.sqlString(rs.getString("FFeeCode4") == null ? "" : rs.getString("FFeeCode4")) + "," +
                rs.getDouble("FTradeFee4") + "," +
                dbl.sqlString(rs.getString("FFeeCode5") == null ? "" : rs.getString("FFeeCode5")) + "," +
                rs.getDouble("FTradeFee5") + "," +
                dbl.sqlString(rs.getString("FFeeCode6") == null ? "" : rs.getString("FFeeCode6")) + "," +
                rs.getDouble("FTradeFee6") + "," +
                dbl.sqlString(rs.getString("FFeeCode7") == null ? "" : rs.getString("FFeeCode7")) + "," +
                rs.getDouble("FTradeFee7") + "," +
                dbl.sqlString(rs.getString("FFeeCode8") == null ? "" : rs.getString("FFeeCode8")) + "," +
                rs.getDouble("FTradeFee8") + "," +
                rs.getDouble("FTotalCost") + "," +
                dbl.sqlString(rs.getString("FOrderNum") == null ? "" : rs.getString("FOrderNum")) + "," +
                dbl.sqlString(rs.getString("FDesc") == null ? "" : rs.getString("FDesc")) + "," +
                rs.getInt("FDataSource") + "," +
                0 + "," +
                dbl.sqlString(rs.getString("FCreator")) + "," +
                dbl.sqlString(rs.getString("FCreateTime")) + "," +
                dbl.sqlString(rs.getString("FCheckUser") == null ? "" : rs.getString("FCheckUser")) + "," +
                dbl.sqlString(rs.getString("FFactCashAccCode")) + "," +

                rs.getDouble("FFactSettleMoney") + "," +
                rs.getDouble("FExRate") + "," +
                dbl.sqlString(rs.getString("FSettleDesc") == null ? "" : rs.getString("FSettleDesc")) + "," +

                rs.getDouble("FFactBaseRate") + "," +
                rs.getDouble("FFactPortRate") + "," +
                dbl.sqlString(rs.getString("FETFBalaAcctCode") == null ? "" : rs.getString("FETFBalaAcctCode")) + "," +
                (rs.getString("FETFBalaSettleDate") == null ? null : dbl.sqlDate((rs.getDate("FETFBalaSettleDate")).toString())) + "," +
                rs.getDouble("FETFBalaMoney") + "," +
                rs.getDouble("FETFCashAlternat") + "," +
                dbl.sqlString(rs.getString("FInvestType") == null ? "" : rs.getString("FInvestType")) + ")";

            delCashData(); // 如果有已结算数据，则删除产生的资金调拨
            dbl.executeSql(strSqlDelSub); //执行删除操作： 删除交易子表中数据
            dbl.executeSql(strSqlDelSplit); //执行删除操作： 删除已拆分交易子表中数据
            dbl.executeSql(strSqlInsert); //执行插入操作： 向交易子表中插入数据

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("检查拆分后数据的结算状态出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * 重新拆分和还原至未拆分操作时，如果有已结算数据，则删除产生的资金调拨
     * @throws YssException
     */
    public void delCashData() throws YssException{
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        String strSql = "";
        String strSqlDelCash = "";
        String strSqlCash = "";
        String strSqlCashSub = "";
        try{
            conn.setAutoCommit(false);
            bTrans = true;
            //SQL查询语句：查询已结算数据的编号
            strSql = "select FNum from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FSettleState = 1 and FSplitNum = " +
                dbl.sqlString(this.num);
            rs3 = dbl.openResultSet(strSql);
            //如果已结算数据存在，则进行逐条删除
            while (rs3.next()) {
                //SQL删除语句：根据业务资料数据的编号删除资金调拨主表中的数据
                strSqlDelCash = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FTradeNum = " + dbl.sqlString(rs3.getString("FNum"));
                //SQL查询语句：根据业务资料数据的编号查询资金调拨主表的编号
                strSqlCash = "select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FTradeNum = " + dbl.sqlString(rs3.getString("FNum"));
                rs4 = dbl.openResultSet(strSqlCash);
                if (rs4.next()) {
                    //SQL删除语句：根据资金调拨主表的编号删除资金调拨子表中的数据
                    strSqlCashSub = "delete from " +
                        pub.yssGetTableName("Tb_Cash_SubTransfer") +
                        " where FNum =" + dbl.sqlString(rs4.getString("FNum"));
                    dbl.executeSql(strSqlCashSub); //删除资金调拨子表中的数据
                }
                dbl.closeResultSetFinal(rs4);
                dbl.executeSql(strSqlDelCash); //删除资金调拨主表中的数据
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("删除资金调拨主表和子表中的数据出错", e);
       } finally {
           dbl.closeResultSetFinal(rs3);
           dbl.closeResultSetFinal(rs4);
           dbl.endTransFinal(conn, bTrans);
       }
    }

    public String getOperValue(String sType) throws YssException {
        if (sType.equalsIgnoreCase("split")) {
            return this.assetSplit();
        }
        else if (sType.equalsIgnoreCase("delsplitdata")) {
            return this.delSplitData();
        }
        else if (sType.equalsIgnoreCase("checkSettleData")) {
            return this.checkSettleData();
        }
        else if (sType.equalsIgnoreCase("toReturnData")) {
            return this.toReturnData();
        }
        else if (sType.equalsIgnoreCase("getListHeads"))
        {
        	return this.getListHeads();
        }

        return "";
    }

    public ArrayList getNumList() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String strSqlSplit = "";
        String strNumDate = "";
        String strNum = "";
        String subNum = "";
        String subNum1 = "";
        String subNum2 = "";
        String sCurCode = "";
        String sFormat = "";

        ArrayList numList = new ArrayList();

        //SQL查询语句：根据编号查交易数据子表，当所要拆分的对象为前台未审核分页下的数据时，将用到此语句
        strSql = "select * from " +
            pub.yssGetTableName("Tb_Data_SubTrade") +
            " where FNum = " +
            dbl.sqlString(this.num);
        //SQL查询语句：根据编号查已拆分交易数据子表，当所要拆分的对象为前台已拆分分页下的数据时，将用到此语句
        strSqlSplit = "select * from " +
            pub.yssGetTableName("Tb_Data_SplitSubTrade") +
            " where FNum = " +
            dbl.sqlString(this.num);
        try{
            if (this.selectedPage.equalsIgnoreCase("pageunaudit")) {
                rs = dbl.openResultSet(strSql); //打开交易数据子表记录集
            } else {
                rs = dbl.openResultSet(strSqlSplit);
            }
            if (rs.next()) {
                //根据原有产生编号的方法产生第一个编号
                strNumDate = YssFun.formatDatetime(YssFun.toDate(rs.getString("FBARGAINDATE").substring(0, 10))).substring(0, 8); //编号前8位
                strNum = strNumDate + dbFun.getNextInnerCode(
                    pub.yssGetTableName("Tb_Data_Trade"), dbl.sqlRight("FNUM", 6),
                    "000000", " where FNum like 'T" + strNumDate + "%'", 1);
                strNum = "T" + strNum; //编号前15位
                subNum = strNum + dbFun.getNextInnerCode(
                    pub.yssGetTableName("Tb_Data_SubTrade"),
                    dbl.sqlRight("FNUM", 5), "00000",
                    " where FNum like '" + strNum.replaceAll("'", "''") + "%'");
                sCurCode = subNum.substring(15, 20); // 获取第一个编号最后5位字符串
                //在产生第一个编号的基础，产生第二和第三个编号
                if (YssFun.isNumeric(sCurCode)) {
                    int iNum = Integer.parseInt(sCurCode);
                    subNum1 = String.valueOf(iNum + 1);
                    subNum2 = String.valueOf(iNum + 2);
                    if (sCurCode.length() > 0) {
                        for (int j = 0; j < sCurCode.length(); j++) {
                            sFormat += "0";
                        }
                        subNum1 = strNum + YssFun.formatNumber(iNum + 1, sFormat); //第二个编号
                        subNum2 = strNum + YssFun.formatNumber(iNum + 2, sFormat); //第三个编号
                    }
                }
                numList.add(subNum);
                numList.add(subNum1);
                numList.add(subNum2);
            }
            return numList;
        }
        catch (Exception e) {
            throw new YssException("产生编号出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private String buildSearchSql() throws YssException {
        String sResult = "";
        sResult = " where 1=1";

        if (this.subBean.getPortCode().length() != 0) {
            //可选择多组合进行查询
            if (this.subBean.getPortCode().indexOf(",") != -1) {
                sResult = sResult + " and a.FPortCode IN (" +
                    operSql.sqlCodes(this.subBean.getPortCode()) + ")";
            } else {
                sResult = sResult + " and a.FPortCode like '" +
                    subBean.getPortCode().replaceAll("'", "''") + "%'";
            }
        }
        if (this.subBean.getTradeCode().length() != 0) {
            sResult = sResult + " and a.FTradeTypeCode = '" +
                subBean.getTradeCode().replaceAll("'", "''") + "'";
        }

        if (this.subBean.getBargainDate().length() != 0 &&
            !this.subBean.getBargainDate().equals("9998-12-31")) {
        	//---delete by songjie 2011.03.18 BUG:1351 QDV4赢时胜(测试)2011年3月10日01_B---//
//            sResult = sResult + " and a.FBargainDate = " +
//                dbl.sqlDate(subBean.getBargainDate());
        	//---delete by songjie 2011.03.18 BUG:1351 QDV4赢时胜(测试)2011年3月10日01_B---//
        	//add by songjie 2011.03.18 BUG:1351 QDV4赢时胜(测试)2011年3月10日01_B 支持按成交日期区间段查询
			sResult = sResult + " and a.FBargainDate between "
			+ dbl.sqlDate(subBean.getBargainDate()) + " and "
			+ dbl.sqlDate(subBean.getBargainDateEnd());
			//add by songjie 2011.03.18 BUG:1351 QDV4赢时胜(测试)2011年3月10日01_B 支持按成交日期区间段查询
        }

        if (this.subBean.getSecurityCode().length() != 0) {
            sResult = sResult + " and a.FSecurityCode like '" +
                subBean.getSecurityCode().replaceAll("'", "''") +
                "'";
        }

        if (this.subBean.getMatureDate().length() != 0 &&
            !this.subBean.getMatureDate().equals("9998-12-31")) {
            sResult = sResult + " and a.FMatureDate = " +
                dbl.sqlDate(subBean.getMatureDate());
        }

        if (this.subBean.getRateDate().length() != 0 &&
            !this.subBean.getRateDate().equals("9998-12-31")) {
            sResult = sResult + " and a.FRateDate = " +
                dbl.sqlDate(subBean.getRateDate());
        }
        //品种类型 add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A------start
		if (this.subBean.getCategoryCode()!=null&&this.subBean.getCategoryCode().length() != 0 && this.subBean.getSecurityCode().length() == 0) 
		{
//			SecurityBean SecurityBean=new SecurityBean();
//			SecurityBean.setYssPub(this.pub);
//			SecurityBean.setStrCategoryCode(this.subBean.getCategoryCode());	
//			String SecurityCodes=SecurityBean.getOperValue("getSecurityCodes");
//			if(SecurityCodes!=null&&SecurityCodes.trim().length()>0)
//			      sResult = sResult + " and a.FSecurityCode in ("+SecurityCodes+")";
			
			sResult = sResult + " and a.FSecurityCode in ( select distinct FSecurityCode from  " +pub.yssGetTableName("Tb_Para_Security");
			sResult = sResult + " where FCatCode=" + dbl.sqlString(this.subBean.getCategoryCode())+" )";
		}
		//品种类型 add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A-------end	
		
        return sResult;
    }

    private String getListHeads(){
    	String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
    	sHeader = this.getListView1Headers();
    	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
        this.getListView1ShowCols();
    }
    
    public String getBalanceType() {

        return balanceType;
    }

    public String getHoldType() {

        return holdType;
    }

    public String getSellType() {

        return sellType;
    }

    public String getTradeType() {

        return tradeType;
    }

    public int getIHoldAmount() {
        return iHoldAmount;
    }

    public int getISellAmount() {
        return iSellAmount;
    }

    public String getNum() {
        return num;
    }

    public int getITradeAmount() {
        return iTradeAmount;
    }

    public int getITotalAmount() {
        return iTotalAmount;
    }

    public String getSelectedPage() {
        return selectedPage;
    }

    public void setBalanceType(String balanceType) {

        this.balanceType = balanceType;
    }

    public void setHoldType(String holdType) {

        this.holdType = holdType;
    }

    public void setSellType(String sellType) {

        this.sellType = sellType;
    }

    public void setTradeType(String tradeType) {

        this.tradeType = tradeType;
    }

    public void setIHoldAmount(int iHoldAmount) {
        this.iHoldAmount = iHoldAmount;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setITradeAmount(int iTradeAmount) {
        this.iTradeAmount = iTradeAmount;
    }

    public void setITotalAmount(int iTotalAmount) {
        this.iTotalAmount = iTotalAmount;
    }

    public void setSelectedPage(String selectedPage) {
        this.selectedPage = selectedPage;
    }
}
