package com.yss.main.orderadmin;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.sys.*;
import com.yss.util.*;

public class ConfirmBean
    extends BaseDataSettingBean implements IDataSetting {
    private String invMgrCode = "";
    private String invMgrName = "";
    private String invMgrShortName = "";
    private String orderNum = "";
    private java.util.Date orderDate;
    private String orderTime = "00:00:00";
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String securityCode = ""; //证券代码
    private String securityName = ""; //证券名称
    private String tradeCode = ""; //交易方式代码
    private String tradeName = ""; //交易方式名称
    private String brokerCode = ""; //券商代码
    private String brokerName = ""; //券商名称
    private double tradeAmount; //交易数量
    private double tradePrice; //交易价格
    private double tradeTotal; //交易总额
    private double bargainAmount; //成交数量
    private double bargainPrice; //成交价格
    private double bargainTotal; //成交总额
    private java.util.Date bargainDate; //成交日期
    private String bargainTime = "00:00:00"; //成交时间
    private double handAmount; //每手股数
    private String tradeState = ""; //拆分状态
    private double portCuryRate; //组合汇率
    private double baseCuryRate; //基础汇率
    private String tradeCuryCode = ""; //交易货币代码
    private String portCuryCode = ""; //组合货币代码
    private String tradeNum = ""; //确认编号
    private String bargainOrderNum = ""; //成交笔号
    private double interest; //应收利息
    private double factor = 0.0; //报价因子
    private double yield = 0; //收益率
    private double bargainInterest; //确认应收利息
    private String quoteMode = ""; //报价方式
    private String desc = ""; //确认描述
    private String exchangeCode = ""; //交易所代码
    private String oldTradeNum = "";
    private String listView1Headers = "";

    private String isOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private ConfirmBean filterType;
  //add by zhangfa 20100916 MS01736    交易数据主表进行向导时，所属分类没有带出    QDV4赢时胜(测试)2010年09月13日05_B  
    private String attrClsCode="";
    private String attrClsName="";
	//add by songjie 2011.05.23 BUG 1827 QDV4赢时胜（测试）2011年4月27日03_B
    private String sRecycled = "";
  
    public String getAttrClsCode() {
		return attrClsCode;
	}

	public void setAttrClsCode(String attrClsCode) {
		this.attrClsCode = attrClsCode;
	}

	public String getAttrClsName() {
		return attrClsName;
	}

	public void setAttrClsName(String attrClsName) {
		this.attrClsName = attrClsName;
	}
	//-----------------------------------------------------------------------------------------------------------
	public void setListView1Headers(String listView1Headers) {
        this.listView1Headers = listView1Headers;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }

    public String getListView1Headers() {
        return listView1Headers;
    }

    public double getFactor() {
        return factor;
    }

    public double getYield() {
        return yield;
    }

    public ConfirmBean() {
    }

    /**
     * appendSelectedOrderNums
     * 获取被选择进行确认操作的订单号串
     * @return String
     */
    public String appendSelectedOrderNums() throws YssException {
        String orderNumAry[] = null;
        String reStr = "";
        if (this.orderNum.length() == 0) {
            reStr = "''";
            return reStr;
        }
        orderNumAry = this.orderNum.split(";");
        for (int i = 0; i < orderNumAry.length; i++) {
            reStr += dbl.sqlString(orderNumAry[i]);
            if (i < orderNumAry.length - 1) {
                reStr += ",";
            }
        }
        return reStr;
    }

    /**
     * addSetting
     * 新增订单确认数据
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
      String sStartNum="0";//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B 合并太平版本代码
        Connection conn = dbl.loadConnection();
        try {
	         //将些段代码移到这里 by leeyu 20090814 QDV4中保2009年08月13日01_B MS00626
	         //--------------------MS00201 QDV4赢时胜上海2009年01月06日06_B sj modified -----------------------------------//
	         String dataTradeNum = "";//业务数据中的编号。
	         String strNumDate = YssFun.formatDatetime(bargainDate).
	                 substring(0, 8);
	       //编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B 合并太平版本代码
	     	if(this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)){//买入
	     		sStartNum = "2";
	     	}else if(this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)){//卖出
	     		sStartNum = "9";
	     	}else if(this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)||//派息
	     			this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SG)||//送股
	     			this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PG)){//配股
	     		sStartNum = "1";
	     	}else if(this.tradeCode.equalsIgnoreCase(YssOperCons.Yss_JYLX_ZQ)){//债券兑付
	     		sStartNum = "8";
	     	}else{
	     		sStartNum = "0";
	     	}
	     	//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B 合并太平版本代码
	         dataTradeNum= strNumDate +
	                 dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Trade"),
	                                        //dbl.sqlRight("FNUM", 6), "000000", //合并太平版本代码
	                		 				dbl.sqlRight("FNUM", 6), sStartNum + "00000",//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B
	                                        " where FNum like 'T"
	                                        //+ strNumDate + "%'", 1);// 获取业务数据中编号最大的+1编号 //合并太平版本代码
	                		 				+ strNumDate + sStartNum + "%'", 1);// 获取业务数据中编号最大的+1编号//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B
	           dataTradeNum = "T" + dataTradeNum;//得到希望的最大编号

	         if (this.tradeNum.compareTo(dataTradeNum) < 0){//若获取的业务数据中的编号比订单中的成交编号大,则使用业务数据中的编号
	            this.tradeNum = dataTradeNum;
	         }
	         //-----------------------------------------------------------------------------------------------------------//
            strSql = "insert into " + pub.yssGetTableName("tb_order_confirm") +
                " " +
                "(FTradeNum,FInvMgrCode,FSecurityCode,FBrokerCode,FPortCode,FTradeTypeCode," +
                " FAmount,FInterest,FPrice,FTransDate,FTransTime,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FYield)" +
                " values(" +
                dbl.sqlString(this.tradeNum) + "," +
                dbl.sqlString(this.invMgrCode) + "," +
                dbl.sqlString(this.securityCode) + "," +
                dbl.sqlString(this.brokerCode) + "," +
                dbl.sqlString( (this.portCode.length() == 0 ? " " :
                                this.portCode)) + " ," +
                dbl.sqlString(this.tradeCode) + "," +
                this.bargainAmount + "," +
                this.bargainInterest + "," +
                this.bargainPrice + "," +
                dbl.sqlDate(this.bargainDate) + "," +
                dbl.sqlString(this.bargainTime) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                this.yield +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("tb_order_maintenance") +
                " set FTradeNum = " + dbl.sqlString(this.tradeNum) +
                " where FInvMgrCode = " +
                dbl.sqlString(this.invMgrCode) + " and FOrderNum In (" +
                appendSelectedOrderNums() + ") and FSecurityCode = " +
                dbl.sqlString(this.securityCode) + " and FBrokerCode = " +
                dbl.sqlString(this.brokerCode);

            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增订单确认信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.invMgrCode).append("\t");
        buf.append(this.invMgrName).append("\t");
        buf.append(this.orderNum).append("\t");
        //buf.append(YssFun.formatDate(this.orderDate)).append("\t");//判断，老数据可能这个字段为空的情况 QDV4赢时胜（上海）2009年5月13日01_B MS00448 by leeyu 2009-05-22
        buf.append(YssFun.formatDate(this.orderDate == null ? YssFun.toDate("1900-01-01") : orderDate)).append("\t");
        buf.append(this.orderTime).append("\t");
        buf.append(this.tradeCode).append("\t");
        buf.append(this.tradeName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.tradeAmount).append("\t");
        buf.append(this.tradePrice).append("\t");
        buf.append(this.tradeTotal).append("\t");
        buf.append(this.bargainAmount).append("\t");
        buf.append(this.bargainPrice).append("\t");
        buf.append(this.bargainTotal).append("\t");
        buf.append(YssFun.formatDate(this.bargainDate)).append("\t");
        buf.append(this.bargainTime).append("\t");
        buf.append(this.handAmount).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.bargainOrderNum).append("\t");
        buf.append(this.tradeNum).append("\t");
        buf.append(this.interest).append("\t");
        //报价因子
        buf.append(this.factor).append("\t");
        buf.append(this.bargainInterest).append("\t");
        buf.append(this.quoteMode).append("\t");
        buf.append(this.tradeState).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.exchangeCode).append("\t");
        buf.append(this.invMgrShortName).append("\t");
        buf.append(this.tradeCuryCode).append("\t");
        buf.append(this.yield).append("\t");
      //add by zhangfa 20100916 MS01736    交易数据主表进行向导时，所属分类没有带出    QDV4赢时胜(测试)2010年09月13日05_B 
        buf.append(this.attrClsCode).append("\t");
        buf.append(this.attrClsName).append("\t");
      // ----------------------------------------------------------------------------------------------------------
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("tb_order_confirm"),
                               "FTradeNum", this.tradeNum, this.oldTradeNum);
        checkState(btOper);
    }

    //检查数据状态是否合法
    public void checkState(byte btOper) throws YssException {

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("tb_order_confirm") +
                " set FCheckState = " + this.checkStateId +
                ",FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ",FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FTradeNum = " + dbl.sqlString(this.tradeNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核订单确认信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	//add by songjie 2011.04.28 BUG 1827 QDV4赢时胜（测试）2011年4月27日03_B
        	checkSetting();
        	//---delete by songjie 2011.04.28 BUG 1827 QDV4赢时胜（测试）2011年4月27日03_B---//
//            strSql = "delete from " + pub.yssGetTableName("tb_order_confirm") +
//                " where FTradeNum = " + dbl.sqlString(this.tradeNum);
//            conn.setAutoCommit(false);
//            bTrans = true;
//            dbl.executeSql(strSql);
//
//            strSql = "update " + pub.yssGetTableName("tb_order_maintenance") +
//                " set FTradeNum = ''" +
//                " where FTradeNum = " + dbl.sqlString(this.tradeNum);
//            dbl.executeSql(strSql);
//
//            conn.commit();
//            bTrans = false;
//            conn.setAutoCommit(true);
        	//---delete by songjie 2011.04.28 BUG 1827 QDV4赢时胜（测试）2011年4月27日03_B---//
        } catch (Exception e) {
            throw new YssException("删除订单确认信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
        try {
	         //将些段代码移到这里 by leeyu 20090814 QDV4中保2009年08月13日01_B MS00626
	         //--------------------MS00201 QDV4赢时胜上海2009年01月06日06_B sj modified -----------------------------------//
	         String dataTradeNum = "";//业务数据中的编号。
	         String strNumDate = YssFun.formatDatetime(bargainDate).
	                 substring(0, 8);
	         dataTradeNum= strNumDate +
	                 dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Trade"),
	                                        dbl.sqlRight("FNUM", 6), "000000",
	                                        " where FNum like 'T"
	                                        + strNumDate + "%'", 1);// 获取业务数据中编号最大的+1编号
	           dataTradeNum = "T" + dataTradeNum;//得到希望的最大编号

	         if (this.tradeNum.compareTo(dataTradeNum) < 0){//若获取的业务数据中的编号比订单中的成交编号大,则使用业务数据中的编号
	            this.tradeNum = dataTradeNum;
	         }
	         //-----------------------------------------------------------------------------------------------------------//
            strSql = "update " + pub.yssGetTableName("tb_order_confirm") +
                " set FAmount = " + this.bargainAmount +
                ",FPrice = " + this.bargainPrice +
                ",FInterest = " + this.bargainInterest +
                ",FTransDate = " + dbl.sqlDate(this.bargainDate) +
                ",FTransTime = " + dbl.sqlString(this.bargainTime) +
                ",FTradeNum = " + dbl.sqlString(this.tradeNum) +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FYield=" + this.yield +
                " where FTradeNum = " + dbl.sqlString(this.oldTradeNum);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("tb_order_maintenance") +
                " set FTradeNum = " + dbl.sqlString(this.tradeNum) +
                " where FTradeNum = " + dbl.sqlString(this.oldTradeNum);
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改订单确认信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = " where 1=1 ";
        //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return sResult;
    	//=============end=================
        if (this.isOnlyColumns.equals("1")&&pub.isBrown()==false) {		//20111027 modified by liubo.STORY #1285. 
            sResult += " and 1 = 2 ";
            return sResult;
        }
        if (this.filterType != null) {
            if (this.filterType.tradeNum.length() != 0) {
                sResult = sResult + " and a.FTradeNum = " +
                    dbl.sqlString(this.tradeNum);
            }
            if (this.filterType.invMgrCode.length() != 0) {
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.invMgrCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.orderNum.length() != 0) {
                sResult = sResult + " and a.FOrderNum like '" +
                    filterType.orderNum.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.tradeCode.length() != 0) {
                sResult = sResult + " and a.FTradeTypeCode = '" +
                    filterType.tradeCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.bargainAmount != -1) {
                sResult = sResult + " and a.FAmount = " +
                    filterType.bargainAmount;
            }
            if (this.filterType.bargainPrice != -1) {
                sResult = sResult + " and a.FPrice = " +
                    filterType.bargainPrice;
            }
            if (this.filterType.securityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.securityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.brokerCode.length() != 0) {
                sResult = sResult + " and a.FBrokerCode like '" +
                    filterType.brokerCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.orderDate != null &&
                !this.filterType.orderDate.equals(YssFun.toDate("9998-12-31"))) {
                sResult = sResult + " and a.FOrderDate = " +
                    dbl.sqlDate(filterType.orderDate);
            }
            if (this.filterType.yield != 0) {
                sResult += " and a.FYield=" + filterType.yield;
            }
            if (this.filterType.bargainDate != null &&
                !this.filterType.bargainDate.equals(YssFun.toDate("9998-12-31"))) {
                sResult = sResult + " and a.FTransDate = " +
                    dbl.sqlDate(filterType.bargainDate);
            }
            if (this.filterType.tradeState.equalsIgnoreCase("0")) {
                sResult = sResult + " and FLinkNum IS NULL ";
            } else if (this.filterType.tradeState.equalsIgnoreCase("1")) {
                sResult = sResult + " and FLinkNum IS NOT NULL ";
            }
        }
        return sResult;

    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    //配置ListView显示列时对特殊列的操作
    public void beforeBuildRowShowStr(YssCancel bCancel, String sColName,
                                      ResultSet rs, StringBuffer buf) throws
        YssException,
        SQLException {
        String sFieldName = "";
        String sFieldFormat = "";
        String strSql = "";
        ResultSet tmpRs = null;
        if (sColName.indexOf("FTradeTotal") >= 0) {
            if (sColName.indexOf(";") > 0) {
                sFieldName = sColName.split(";")[0];
                sFieldFormat = sColName.split(";")[1];
            } else {
                sFieldName = sColName;
            }

//         buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble("FTradeAmount"),
//               rs.getDouble("FTradePrice")),rs.getDouble("FPreInterest")), 2),sFieldFormat) + "").append("\t");
//         if (rs.getString("FTradeNum")==null){
            if (rs.getDouble("FFactor") == 0) {
                buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.mul(
                    rs.
                    getDouble("FTradeAmount"), rs.getDouble("FTradePrice")),
                    rs.getDouble("FPreInterest")), 2), sFieldFormat) +
                           "").append("\t");
            } else {
                buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.div(
                    YssD.mul(rs.
                             getDouble("FTradeAmount"),
                             rs.getDouble("FTradePrice")),
                    rs.getDouble("FFactor")),
                    rs.getDouble("FPreInterest")), 2),
                                               sFieldFormat) +
                           "").append("\t");
            }
            bCancel.setCancel(true);
//         }else{
//            buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble("FTradeAmount"),
//            rs.getDouble("FTradePrice")),rs.getDouble("FPreInterest")), 2),sFieldFormat) + "").append("\t");
//            bCancel.setCancel(true);
//         }

        } else if (sColName.indexOf("FBargainTotal") >= 0) {
            if (sColName.indexOf(";") > 0) {
                sFieldName = sColName.split(";")[0];
                sFieldFormat = sColName.split(";")[1];
            } else {
                sFieldName = sColName;
            }
//         buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble("FAmount"),
//               rs.getDouble("FPrice")),rs.getDouble("FInterest")), 2),sFieldFormat) + "").append("\t");
//         bCancel.setCancel(true);

//         if (rs.getString("FTradeNum")==null){
            if (rs.getDouble("FFactor") == 0) {
                buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.mul(
                    rs.
                    getDouble("FAmount"), rs.getDouble("FPrice")),
                    rs.getDouble("FInterest")), 2), sFieldFormat) +
                           "").append("\t");
            } else {
                buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.div(
                    YssD.mul(rs.
                             getDouble("FAmount"),
                             rs.getDouble("FPrice")),
                    rs.getDouble("FFactor")),
                    rs.getDouble("FInterest")), 2),
                                               sFieldFormat) +
                           "").append("\t");
            }
            bCancel.setCancel(true);
//         }else{
//            buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble("FTradeAmount"),
//            rs.getDouble("FTradePrice")),rs.getDouble("FPreInterest")), 2),sFieldFormat) + "").append("\t");
//            bCancel.setCancel(true);
//         }

        } else if (sColName.indexOf("FOrderTime") >= 0) {
            this.tradeNum = rs.getString("FTradeNum") + "";
            this.orderDate = rs.getDate("FOrderDate");
            if (!this.tradeNum.equalsIgnoreCase("null")) {
                strSql =
                    "select FTradeNum, FOrderDate, min(FOrderTime) as FOrderTime from " +
                    pub.yssGetTableName("tb_order_maintenance") +
                    " where FCheckState = 1 and FTradeNum = " +
                    dbl.sqlString(this.tradeNum) +
                    //" and FOrderDate = " + dbl.sqlDate(this.orderDate) +//判断日期 QDV4赢时胜（上海）2009年5月13日01_B MS00448 by leeyu 2009-05-22
                    (this.orderDate == null ? " and FOrderDate is null " : (" and FOrderDate = " + dbl.sqlDate(this.orderDate))) +
                    " group by FTradeNum, FOrderDate";
                tmpRs = dbl.openResultSet(strSql);
                if (tmpRs.next()) {
                    this.orderTime = tmpRs.getString("FOrderTime");
                    if (this.orderTime == null || this.orderTime.length() != 8) {
                        this.orderTime = "00:00:00";
                    }
                }
                dbl.closeResultSetFinal(tmpRs);
            } else {
                this.orderTime = rs.getString("FOrderTime") + "";
            }
            buf.append(this.orderTime).append("\t");
            bCancel.setCancel(true);
        } 
//        else if (sColName.indexOf("FTransTime") >= 0) {
//
//        }
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
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        int i = 0;
        try {
            sHeader = this.getListView1Headers();
//         sHeader = this.getListView1Headers().replaceAll("\\\\t", "\t");       
            strSql = "select a.*,CASE WHEN j.FLinkNum IS NULL THEN 'No' ELSE 'Yes' END as FTradeState," +
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName,f.FInvMgrName,f.FInvMgrShortName,m.FVocName as FQuoteModeValue," +
                " g.FTradeTypeName,h.FBrokerName as FBrokerName,e.FSecurityName,e.Ffactor,e.FExchangeCode,d.FPortName as FPortName" +
                " from (select FTRADENUM,FORDERNUM,FINVMGRCODE,FBROKERCODE,FPORTCODE,FSECURITYCODE,FTRADETYPECODE," +
                " FTRADEAMOUNT,FINTEREST as FPREINTEREST,FINTEREST,FQUOTEMODE,FTRADEPRICE,FORDERDATE,FORDERTIME,FTRADEAMOUNT as FAmount,FTRADEPRICE as FPrice," +
                " FORDERDATE as FTransDate,FORDERTIME as FTransTime,FDESC," +
                (pub.getSysCheckState() ? "0" : "1") +
                " as FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FYield from " +
                pub.yssGetTableName("tb_order_maintenance") +
                //2007.11.30 修改 蒋锦 考虑DB2的情况，在DB2中使用 '' 进行插入的值将不判断为NULL，Oracle正好相反，所以为需要判断NULL值的字段添加长度判断
                " where FCheckState = 1 and (FTradeNum Is Null or " + dbl.sqlLen("FTradeNum") + " = 0) UNION " +
                " (select ab.FTRADENUM,ac.FORDERNUM,ab.FINVMGRCODE,ab.FBROKERCODE,ab.FPORTCODE,ab.FSECURITYCODE,ab.FTRADETYPECODE," +
                " ac.FTRADEAMOUNT,ac.FINTEREST as FPREINTEREST,ab.FINTEREST,ac.FQUOTEMODE,ac.FTRADEPRICE,ac.FORDERDATE,ac.FORDERTIME,ab.FAmount,ab.FPrice," +
                " ab.FTransDate,ab.FTransTime,ab.FDESC,ab.FCHECKSTATE," +
                " ab.FCREATOR,ab.FCREATETIME,ab.FCHECKUSER,ab.FCHECKTIME,ab.FYield from " +
                pub.yssGetTableName("tb_order_confirm") +
                " ab left join (select ad.*, ad.FTradeTotal/ad.FTradeAmount as FTradePrice from " +
                " (select FTradeNum, min(FOrderNum) as FOrderNum, min(FOrderDate) as FOrderDate," +
                " sum(FTRADEAMOUNT) as FTradeAmount, sum(FTRADEAMOUNT*FTRADEPRICE) as FTradeTotal," +
                " min(FQUOTEMODE) as FQuoteMode, min(FOrderTime) as FOrderTime, sum(FINTEREST) as FINTEREST from " +
                pub.yssGetTableName("tb_order_maintenance") +
                " where FCheckState = 1 and FTradeNum Is Not Null group by FTradeNum) ad " +
                " ) ac on ab.FTRADENUM = ac.FTRADENUM)) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g " +
                " on a.FTradeTypeCode = g.FTradeTypeCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName," +
                " FExchangeCode, FStartDate, FHandAmount,Ffactor from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Broker") + " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FBrokerCode, FBrokerName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Broker") +
                " where FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FPortCode, FPortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_InvestManager") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FInvMgrCode, FInvMgrName, FInvMgrShortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (select distinct " + dbl.sqlLeft("FNum", 15) +
                " as FLinkNum from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState <> 2) j on a.FTradeNum = j.FLinkNum " +
                //----------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary m on a.FQuoteMode = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MTN_QUOTEMODE) +
                //----------------------------------------------------------------------------------------------------
                buildFilterSql() +
                " order by a.FCheckState, a.FTradeNum desc, a.FCreateTime desc"; // wdy modify 20070830
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.tradeNum = rs.getString("FTradeNum") + "";
                this.orderNum = rs.getString("FOrderNum") + "";
                this.orderDate = rs.getDate("FOrderDate");
                this.bargainDate = rs.getDate("FTransDate");
                this.bargainTime = rs.getString("FTransTime") + "";
                this.invMgrCode = rs.getString("FInvMgrCode") + "";
                this.invMgrName = rs.getString("FInvMgrName") + "";
                this.invMgrShortName = rs.getString("FInvMgrShortName") + "";
                this.tradeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeName = rs.getString("FTradeTypeName") + "";
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.securityName = rs.getString("FSecurityName") + "";
                this.brokerCode = rs.getString("FBrokerCode") + "";
                this.brokerName = rs.getString("FBrokerName") + "";
                this.portCode = rs.getString("FPortCode") + "";
                this.portName = rs.getString("FPortName") + "";
                this.yield = rs.getDouble("Fyield");
                this.factor = rs.getDouble("Ffactor");

                if (this.tradeNum.equalsIgnoreCase("null")) {
                    this.tradeNum = "";
                    this.bargainOrderNum = "";
                } else if (this.tradeNum.length() > 10) {
                    this.bargainOrderNum = this.tradeNum.substring(9);
                }
                this.tradeAmount = rs.getDouble("FTradeAmount");
//            this.tradePrice = YssFun.roundIt(rs.getDouble("FTradePrice"), 4);
                this.tradePrice = rs.getDouble("FTradePrice"); //MS00126
//            this.tradeTotal = YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble(
//                  "FTradeAmount"), rs.getDouble("FTradePrice")),rs.getDouble("FPreInterest")), 2);
                this.bargainAmount = rs.getDouble("FAmount");
//            this.bargainPrice = YssFun.roundIt(rs.getDouble("FPrice"), 4);
                this.bargainPrice = rs.getDouble("FPrice"); //MS00126
//            this.bargainTotal = YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble(
//                  "FAmount"), rs.getDouble("FPrice")),rs.getDouble("FInterest")), 2);
                this.interest = rs.getDouble("FPreInterest");
                this.bargainInterest = rs.getDouble("FInterest");
                this.quoteMode = rs.getString("FQuoteModeValue") + "";
                this.tradeState = rs.getString("FTradeState") + "";
                this.desc = rs.getString("FDesc") + "";
                this.exchangeCode = rs.getString("FExchangeCode") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取订单确认信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 提取可拆分的订单信息
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader =
                "成交编号\t成交日期\t成交时间\t拆分状态\t证券代码\t交易证券\t交易方式\t投资经理简称\t投资组合简称\t交易券商简称";
//         sHeader = this.getListView1Headers().replaceAll("\\\\t", "\t");
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,f.FInvMgrName,f.FInvMgrShortName, " +
            " g.FTradeTypeName,h.FBrokerName as FBrokerName,h.FBrokerShortName,e.FSecurityName,e.FHandAmount,e.FTradeCury,e.FFactor," +
            " d.FPortName as FPortName, d.FPortShortName, d.FPortCury, d.FStartDate as FStartDate," +
          //add by zhangfa 20100916 MS01736    交易数据主表进行向导时，所属分类没有带出    QDV4赢时胜(测试)2010年09月13日05_B
            " k .FAttrClsCode,k .FAttrClsName,"+
          //------------------------------------------------------------------------------------------------------------
            " CASE WHEN j.FLinkNum IS NULL THEN 'No' ELSE 'Yes' END as FTradeState" +
            //" from " + "(select * from " +
            " from " + "(select a1.*,a2.FOrderNum from " + //这里添加上订单表的订单编号 QDV4赢时胜（上海）2009年4月28日04_B MS00422 by leeyu 20090505
            pub.yssGetTableName("tb_order_confirm") +
            " a1 left join " + pub.yssGetTableName("Tb_Order_Maintenance") + " a2 on a1.FTradeNum =a2.FTradeNum " + //这里添加上订单表，用于关联订单编号 QDV4赢时胜（上海）2009年4月28日04_B MS00422 by leeyu 20090505
            " where a1.FCheckState = 1) a " + //取第一张表的审核状态 QDV4赢时胜（上海）2009年4月28日04_B MS00422 by leeyu 20090505
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            //----------------------------------------------------------------------------------------------------
            " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
            //----------------------------------------------------------------------------------------------------
            " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount,FTradeCury,FFactor from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
            //----------------------------------------------------------------------------------------------------
            " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_Broker") + " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " select FBrokerCode, FBrokerName, FBrokerShortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            pub.yssGetTableName("Tb_Para_Broker") +
            " where FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----------------------------------------------------------------------------------------------------
            " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_Portfolio") +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " select FPortCode, FPortName, FPortShortName, FPortCury, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----------------------------------------------------------------------------------------------------
            " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_InvestManager") +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " select FInvMgrCode, FInvMgrName, FInvMgrShortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            pub.yssGetTableName("Tb_Para_InvestManager") +
            " where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----------------------------------------------------------------------------------------------------
            " left join (select distinct " + dbl.sqlLeft("FNum", 15) +
            " as FLinkNum,FCheckState as FIsChecked  from " +
            pub.yssGetTableName("Tb_Data_SubTrade") +
           //---MS00813 QDV4中保2009年11月16日01_B 蒋世超  修改 2009.11.26 -----------------------------------------
           " where FCheckState <> 2 and fordernum<>' ') j on a.FTradeNum = j.FLinkNum " +//添加fordernum用来区分手工录入的业务资料(解决因区分不开手工录入的业务资料而在拆分数据时，显示错误的拆分状态)
          //add by zhangfa 20100916 MS01736    交易数据主表进行向导时，所属分类没有带出    QDV4赢时胜(测试)2010年09月13日05_B
           " left join (select distinct " + dbl.sqlLeft("FNum", 15) +
           " as FLinkN,FCheckState as FIsChecked ,pa.FAttrClsCode,pa.FAttrClsName from " +
             pub.yssGetTableName("Tb_Data_Trade") +" ds"+
           " left join (select FAttrClsCode,FAttrClsName from " +pub.yssGetTableName("Tb_Para_AttributeClass")+") pa"+
           " on ds.fattrclscode=pa.fattrclscode"+
           " where FCheckState <> 2 and fordernum<>' ') k on a.ftradenum=k.FLinkN "+
          //----------------------------------------------------------------------------------------------------------- 
           //---MS00813 QDV4中保2009年11月16日01_B end ----------------------------------------------------------
            buildFilterSql() +
           //" and (FIsChecked = 0 or FIsChecked is null) " +// 去掉本句条件，原因是拆分后已审核的数据不能够显示 by leeyu 20090828 QDV4中保2009年08月13日03_B MS00628
           "order by a.FTradeNum desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FTradeNum") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.formatDate( (rs.getDate("FTransDate")))).
                    append(
                        "\t");
                bufShow.append(rs.getString("FTransTime")).append(
                    "\t");
                bufShow.append( (rs.getString("FTradeState") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FSecurityCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FTradeTypeName") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FInvMgrShortName") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FPortShortName") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FBrokerShortName") + "").trim()).
                    append(YssCons.YSS_LINESPLITMARK);

                this.tradeNum = rs.getString("FTradeNum") + "";
                this.invMgrCode = rs.getString("FInvMgrCode") + "";
                this.invMgrName = rs.getString("FInvMgrName") + "";
                this.tradeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeName = rs.getString("FTradeTypeName") + "";
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.securityName = rs.getString("FSecurityName") + "";
                this.brokerCode = rs.getString("FBrokerCode") + "";
                this.brokerName = rs.getString("FBrokerName") + "";
                this.portCode = rs.getString("FPortCode") + "";
                this.portName = rs.getString("FPortName") + "";
                this.handAmount = rs.getDouble("FHandAmount");
                this.bargainAmount = rs.getDouble("FAmount");
                this.bargainPrice = rs.getDouble("FPrice");
//            this.bargainTotal = YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble(
//                  "FAmount"), rs.getDouble("FPrice")),rs.getDouble("FInterest")), 2);

                if (rs.getDate("FTransDate") != null) {
                    this.bargainDate = rs.getDate("FTransDate");
                }
                this.bargainTime = rs.getString("FTransTime");

                if (this.tradeNum.equalsIgnoreCase("null")) {
                    this.tradeNum = "";
                    this.bargainOrderNum = "";
                } else if (this.tradeNum.length() > 10) {
                    this.bargainOrderNum = this.tradeNum.substring(9);
                }

                this.tradeCuryCode = rs.getString("FTradeCury") + "";
                this.portCuryCode = rs.getString("FPortCury") + "";
                this.interest = rs.getDouble("FInterest");
                this.factor = rs.getDouble("FFactor");
                this.tradeState = rs.getString("FTradeState") + "";
              //add by zhangfa 20100916 MS01736    交易数据主表进行向导时，所属分类没有带出    QDV4赢时胜(测试)2010年09月13日05_B
                this.attrClsCode=rs.getString("FAttrClsCode")+"";
                this.attrClsName=rs.getString("FAttrClsName")+"";
              //-----------------------------------------------------------------------------------------------------------
                this.desc = rs.getString("FDesc") + "";
                super.setRecLog(rs);

                if (this.securityCode.trim().length() > 0) {
                    this.baseCuryRate = this.getSettingOper().getCuryRate(this.
                        bargainDate,
                        (this.bargainTime == null) ? "" : this.bargainTime.trim(),
                        this.tradeCuryCode, this.portCode,
                        YssOperCons.YSS_RATE_BASE);
                    this.baseCuryRate = YssD.round(this.baseCuryRate, 4);
                }

                if (this.portCode.trim().length() > 0) {
                    this.portCuryRate = this.getSettingOper().getCuryRate(this.
                        bargainDate,
                        (this.bargainTime == null) ? "" : this.bargainTime.trim(),
                        this.portCuryCode, this.portCode,
                        YssOperCons.YSS_RATE_PORT);

                    this.portCuryRate = YssD.round(this.portCuryRate, 4);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取订单确认信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData3
     * 提取订单合并信息
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView3Headers();
            strSql = "select a.*,a.FInterest as FPreInterest,m.FVocName as FQuoteModeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,f.FInvMgrName, " +
                " g.FTradeTypeName,h.FBrokerName as FBrokerName,e.FSecurityName,e.FFactor,d.FPortName as FPortName,d.FStartDate as FStartDate" +
                " from " + pub.yssGetTableName("tb_order_maintenance") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount, FFactor from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Broker") + " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FBrokerCode, FBrokerName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Broker") +
                " where FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FPortCode, FPortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_InvestManager") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FInvMgrCode, FInvMgrName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary m on a.FQuoteMode = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MTN_QUOTEMODE) +
                //----------------------------------------------------------------------------------------------------
                buildFilterSql() +
                " and a.FCheckState = 1 and a.FTradeNum Is Null order by a.FOrderNum desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView3ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.invMgrCode = rs.getString("FInvMgrCode") + "";
                this.invMgrName = rs.getString("FInvMgrName") + "";
                this.orderNum = rs.getString("FOrderNum") + "";
                this.orderDate = rs.getDate("FOrderDate");
                this.orderTime = rs.getString("FOrderTime");
                this.tradeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeName = rs.getString("FTradeTypeName") + "";
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.securityName = rs.getString("FSecurityName") + "";
                this.brokerCode = rs.getString("FBrokerCode") + "";
                this.brokerName = rs.getString("FBrokerName") + "";
                this.portCode = rs.getString("FPortCode") + "";
                this.portName = rs.getString("FPortName") + "";
                this.tradeAmount = rs.getDouble("FTradeAmount");
                this.tradePrice = rs.getDouble("FTradePrice");
                this.interest = rs.getDouble("FPreInterest");
                this.desc = rs.getString("FDesc") + "";
                this.quoteMode = rs.getString("FQuoteModeValue") + "";
                this.tradeTotal = YssFun.roundIt(YssD.add(YssD.mul(rs.getDouble(
                    "FTradeAmount"), rs.getDouble("FTradePrice")),
                    rs.getDouble("FPreInterest")), 2);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView3ShowCols();
        } catch (Exception e) {
            throw new YssException("获取订单合并信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, CASE WHEN j.FLinkNum IS NULL THEN 'No' ELSE 'Yes' END as FTradeState, " +
                "b.FUserName as FCreatorName, c.FUserName as FCheckUserName " +
                " from (select * from " + pub.yssGetTableName("tb_order_confirm") +
                " where FTradeNum = " + dbl.sqlString(this.tradeNum) + ") a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select distinct " + dbl.sqlLeft("FNum", 15) +
                " as FLinkNum from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
               //------MS00813  QDV4中保2009年11月16日01_B  蒋世超 2009.11.24 -----------------------------
               //" where FCheckState <> 2 ) j on a.FTradeNum = j.FLinkNum ";
               " where FCheckState <> 2 and fordernum<>' ') j on a.FTradeNum = j.FLinkNum ";//用fordernum<>' '来区分手工创建的业务资料(解决因为区分不开手工创建的业务资料而不能进行订单的审核报"订单已拆分")
              //------MS00813  QDV4中保2009年11月16日01_B  end --------------------------------------------
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.tradeNum = rs.getString("FTradeNum") + "";
                this.bargainDate = rs.getDate("FTransDate");
                this.bargainTime = rs.getString("FTransTime") + "";
                this.invMgrCode = rs.getString("FInvMgrCode") + "";
                this.tradeCode = rs.getString("FTradeTypeCode") + "";
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.brokerCode = rs.getString("FBrokerCode") + "";
                this.portCode = rs.getString("FPortCode") + "";

                if (this.tradeNum.equalsIgnoreCase("null")) {
                    this.tradeNum = "";
                    this.bargainOrderNum = "";
                } else if (this.tradeNum.length() > 10) {
                    this.bargainOrderNum = this.tradeNum.substring(9);
                }
                this.bargainAmount = rs.getDouble("FAmount");
//            this.bargainPrice = YssFun.roundIt(rs.getDouble("FPrice"), 4);
                this.bargainPrice = rs.getDouble("FPrice"); //MS00126
                this.bargainInterest = rs.getDouble("FInterest");
                this.tradeState = rs.getString("FTradeState") + "";
                this.desc = rs.getString("FDesc") + "";
                super.setRecLog(rs);
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取订单确认信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private IDataSetting getMainSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("tb_order_maintenance") +
                " where FCheckState = 1 and FOrderNum = " +
                dbl.sqlString(this.orderNum);

            rs = dbl.openResultSet(strSql);
            if (!rs.next()) {
                this.orderNum = "NONE";
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取订单信息出错：" + e.getMessage(), e);
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
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
			//add by songjie 2011.05.23 BUG 1827 QDV4赢时胜（测试）2011年4月27日03_B
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.invMgrCode = reqAry[0];
            this.orderNum = reqAry[1];
            this.tradeCode = reqAry[2];
            this.portCode = reqAry[3];
            this.brokerCode = reqAry[4];
            this.securityCode = reqAry[5];
            if (YssFun.isNumeric(reqAry[6])) {
                this.tradeTotal = Double.parseDouble(reqAry[6]);
            }
            if (YssFun.isNumeric(reqAry[7])) {
                this.bargainAmount = Double.parseDouble(reqAry[7]);
            }
            if (YssFun.isNumeric(reqAry[8])) {
                this.bargainPrice = Double.parseDouble(reqAry[8]);
            }
            if (YssFun.isNumeric(reqAry[9])) {
                this.bargainTotal = Double.parseDouble(reqAry[9]);
            }
            if (YssFun.isDate(reqAry[10])) {
                this.bargainDate = YssFun.toDate(reqAry[10]);
            }
            this.bargainTime = reqAry[11];
            if (YssFun.isDate(reqAry[12])) {
                this.orderDate = YssFun.toDate(reqAry[12]);
            }
            this.tradeState = reqAry[13];
            this.bargainOrderNum = reqAry[14];
            if (YssFun.isNumeric(reqAry[15])) {
                this.interest = Double.parseDouble(reqAry[15]);
            }
            //报价因子
            if (YssFun.isNumeric(reqAry[16])) {
                this.factor = Double.parseDouble(reqAry[16]);
            }
            if (YssFun.isNumeric(reqAry[17])) {
                this.bargainInterest = Double.parseDouble(reqAry[17]);
            }
            this.checkStateId = Integer.parseInt(reqAry[18]);
            this.isOnlyColumns = reqAry[19];
            this.oldTradeNum = reqAry[20];
            if (YssFun.isNumeric(reqAry[21])) {
                this.yield = Double.parseDouble(reqAry[21]);
            }
          //add by zhangfa 20100916 MS01736    交易数据主表进行向导时，所属分类没有带出    QDV4赢时胜(测试)2010年09月13日05_B 
            this.attrClsCode=reqAry[22];
          //-----------------------------------------------------------------------------------------------------------
            buildTradeNum();
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ConfirmBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析订单确认设置请求出错", e);
        }

    }

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
     * getSettingListView
     *
     * @return String
     */
    public String getSettingListView() {
        return "";
    }

   /**
    * getOperValue
    *
    * @param sType String
    * @return String
    */
   public String getOperValue(String sType) throws YssException {  
      if (sType.equalsIgnoreCase("BargainOrderNum")) {
    	  //--------QDV4太平2010年11月03日02_B ----panjunfang 20101214 add --//
    	  String sStartNum="0";
			if (this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) {// 买入
				sStartNum = "2";
			} else if (this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) {// 卖出
				sStartNum = "9";
			} else if (this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)|| // 派息
					this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SG) || // 送股
					this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PG)) {// 配股
				sStartNum = "1";
			} else if (this.tradeCode.equalsIgnoreCase(YssOperCons.Yss_JYLX_ZQ)) {// 债券兑付
				sStartNum = "8";
			} else {
				sStartNum = "0";
			}
			//-end-------QDV4太平2010年11月03日02_B ----panjunfang 20101214 add --//
         this.bargainOrderNum = dbFun.getNextInnerCode(pub.yssGetTableName(
               "Tb_Order_Maintenance"),
               dbl.sqlRight("FTradeNum", 6), "000001",
               " where FTradeNum like 'T"
               + YssFun.formatDate(this.bargainDate, "yyyyMMdd") + sStartNum + "%'", 1);
      }
      if (sType.equalsIgnoreCase("Interest")) {
//         this.interest=this.getSettingOper().getInterest(this.securityCode,this.tradeAmount,this.bargainDate);
            this.bargainInterest = this.getSettingOper().getInterest(this.
                securityCode, this.bargainAmount, this.bargainDate);
        }
        if (sType.equalsIgnoreCase("RefreshData")) {
            if (this.tradeNum.length() > 0) {
                this.getSetting();
            } else {
                this.getMainSetting();
            }
        }
        return buildRowStr();
    }

    public void buildTradeNum() throws YssException {
        if (this.bargainOrderNum.length() > 0 && this.bargainDate != null
            && !this.bargainDate.equals(YssFun.toDate("9998-12-31"))) {
            this.bargainOrderNum = "000000" + this.bargainOrderNum;
            this.bargainOrderNum = this.bargainOrderNum.substring(this.
                bargainOrderNum.length() - 6);
            this.tradeNum = "T" + YssFun.formatDate(this.bargainDate, "yyyyMMdd") +
                this.bargainOrderNum;
         //下面的代码已放在了新增与修改的代码块中，这里就不再需要了。QDV4中保2009年08月13日01_B MS00626 by leeyu 20090814
         //--------------------MS00201 QDV4赢时胜上海2009年01月06日06_B sj modified -----------------------------------//
         /*String dataTradeNum = "";//业务数据中的编号。
         String strNumDate = YssFun.formatDatetime(bargainDate).
                 substring(0, 8);
         dataTradeNum= strNumDate +
                 dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Trade"),
                                        dbl.sqlRight("FNUM", 6), "000000",
                                        " where FNum like 'T"
                                        + strNumDate + "%'", 1);// 获取业务数据中编号最大的+1编号
           dataTradeNum = "T" + dataTradeNum;//得到希望的最大编号

         if (this.tradeNum.compareTo(dataTradeNum) < 0){//若获取的业务数据中的编号比订单中的成交编号大,则使用业务数据中的编号
            this.tradeNum = dataTradeNum;
         }*/
         //-----------------------------------------------------------------------------------------------------------//
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        ConfirmBean befEditBean = new ConfirmBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,CASE WHEN j.FLinkNum IS NULL THEN 'No' ELSE 'Yes' END as FTradeState," +
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName,f.FInvMgrName,f.FInvMgrShortName,m.FVocName as FQuoteModeValue," +
                " g.FTradeTypeName,h.FBrokerName as FBrokerName,e.FSecurityName,e.Ffactor,e.FExchangeCode,d.FPortName as FPortName" +
                " from (select FTRADENUM,FORDERNUM,FINVMGRCODE,FBROKERCODE,FPORTCODE,FSECURITYCODE,FTRADETYPECODE," +
                " FTRADEAMOUNT,FINTEREST as FPREINTEREST,FINTEREST,FQUOTEMODE,FTRADEPRICE,FORDERDATE,FORDERTIME,FTRADEAMOUNT as FAmount,FTRADEPRICE as FPrice," +
                " FORDERDATE as FTransDate,FORDERTIME as FTransTime,FDESC," +
                (pub.getSysCheckState() ? "0" : "1") +
                " as FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FYield from " +
                pub.yssGetTableName("tb_order_maintenance") +
                " where FCheckState = 1 and FTradeNum Is Null UNION " +
                " (select ab.FTRADENUM,ac.FORDERNUM,ab.FINVMGRCODE,ab.FBROKERCODE,ab.FPORTCODE,ab.FSECURITYCODE,ab.FTRADETYPECODE," +
                " ac.FTRADEAMOUNT,ac.FINTEREST as FPREINTEREST,ab.FINTEREST,ac.FQUOTEMODE,ac.FTRADEPRICE,ac.FORDERDATE,ac.FORDERTIME,ab.FAmount,ab.FPrice," +
                " ab.FTransDate,ab.FTransTime,ab.FDESC,ab.FCHECKSTATE," +
                " ab.FCREATOR,ab.FCREATETIME,ab.FCHECKUSER,ab.FCHECKTIME,ab.FYield from " +
                pub.yssGetTableName("tb_order_confirm") +
                " ab left join (select ad.*, ad.FTradeTotal/ad.FTradeAmount as FTradePrice from " +
                " (select FTradeNum, min(FOrderNum) as FOrderNum, min(FOrderDate) as FOrderDate," +
                " sum(FTRADEAMOUNT) as FTradeAmount, sum(FTRADEAMOUNT*FTRADEPRICE) as FTradeTotal," +
                " min(FQUOTEMODE) as FQuoteMode, min(FOrderTime) as FOrderTime, sum(FINTEREST) as FINTEREST from " +
                pub.yssGetTableName("tb_order_maintenance") +
                " where FCheckState = 1 and FTradeNum Is Not Null group by FTradeNum) ad " +
                " ) ac on ab.FTRADENUM = ac.FTRADENUM)) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g " +
                " on a.FTradeTypeCode = g.FTradeTypeCode" +
                //----------------------------------------------------------------------------------------------------
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName," +
                " FExchangeCode, FStartDate, FHandAmount,Ffactor from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Broker") + " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FBrokerCode, FBrokerName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Broker") +
                " where FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FPortCode, FPortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_InvestManager") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FInvMgrCode, FInvMgrName, FInvMgrShortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (select distinct " + dbl.sqlLeft("FNum", 15) +
                " as FLinkNum from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState <> 2) j on a.FTradeNum = j.FLinkNum " +
                //----------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary m on a.FQuoteMode = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MTN_QUOTEMODE) +
                //----------------------------------------------------------------------------------------------------
                " where  FTradeNum =" + dbl.sqlString(this.oldTradeNum) +
                " order by a.FCheckState, a.FTradeNum desc, a.FCreateTime desc"; // wdy modify 20070831

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.tradeNum = rs.getString("FTradeNum") + "";
                befEditBean.orderNum = rs.getString("FOrderNum") + "";
                befEditBean.orderDate = rs.getDate("FOrderDate");
                befEditBean.bargainDate = rs.getDate("FTransDate");
                befEditBean.bargainTime = rs.getString("FTransTime") + "";
                befEditBean.invMgrCode = rs.getString("FInvMgrCode") + "";
                befEditBean.invMgrName = rs.getString("FInvMgrName") + "";
                befEditBean.invMgrShortName = rs.getString("FInvMgrShortName") + "";
                befEditBean.tradeCode = rs.getString("FTradeTypeCode") + "";
                befEditBean.tradeName = rs.getString("FTradeTypeName") + "";
                befEditBean.securityCode = rs.getString("FSecurityCode") + "";
                befEditBean.securityName = rs.getString("FSecurityName") + "";
                befEditBean.brokerCode = rs.getString("FBrokerCode") + "";
                befEditBean.brokerName = rs.getString("FBrokerName") + "";
                befEditBean.portCode = rs.getString("FPortCode") + "";
                befEditBean.portName = rs.getString("FPortName") + "";
                befEditBean.yield = rs.getDouble("FYield");
                befEditBean.factor = rs.getDouble("Ffactor");

                if (befEditBean.tradeNum.equalsIgnoreCase("null")) {
                    befEditBean.tradeNum = "";
                    befEditBean.bargainOrderNum = "";
                } else if (befEditBean.tradeNum.length() > 10) {
                    befEditBean.bargainOrderNum = befEditBean.tradeNum.substring(9);
                }
                befEditBean.tradeAmount = rs.getDouble("FTradeAmount");
//            befEditBean.tradePrice = YssFun.roundIt(rs.getDouble("FTradePrice"),
//                  4);
                befEditBean.tradePrice = rs.getDouble("FTradePrice"); //MS00126
                befEditBean.bargainAmount = rs.getDouble("FAmount");
//            befEditBean.bargainPrice = YssFun.roundIt(rs.getDouble("FPrice"), 4);
                befEditBean.bargainPrice = rs.getDouble("FPrice"); //MS00126
                befEditBean.interest = rs.getDouble("FPreInterest");
                befEditBean.bargainInterest = rs.getDouble("FInterest");
                befEditBean.quoteMode = rs.getString("FQuoteModeValue") + "";
                befEditBean.tradeState = rs.getString("FTradeState") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.exchangeCode = rs.getString("FExchangeCode") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * edit by songjie
     * 2011.04.28
     * BUG 1827
     * QDV4赢时胜（测试）2011年4月27日03_B
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
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
                
                strSql = "delete from " + pub.yssGetTableName("tb_order_confirm") +
                " where FTradeNum = " + dbl.sqlString(this.tradeNum);
    			conn.setAutoCommit(false);
    			bTrans = true;
    			dbl.executeSql(strSql);

                strSql = "update " + pub.yssGetTableName("tb_order_maintenance") +
                " set FTradeNum = ''" +
                " where FTradeNum = " + dbl.sqlString(this.tradeNum);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch (Exception e) {
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
