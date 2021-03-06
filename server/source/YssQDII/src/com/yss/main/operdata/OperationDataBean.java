package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.parasetting.*;
import com.yss.main.syssetting.RightBean;
import com.yss.manager.*;
import com.yss.util.*;

/**
 * <p>Title: OperationDataBean </p>
 * <p>Description: 业务数据 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

public class OperationDataBean
    extends BaseDataSettingBean implements IDataSetting {
    private String num = ""; //交易数据流水号
    private String securityCode = ""; //交易证券代码
    private String securityName = ""; //交易证券名称
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String brokerCode = ""; //券商代码
    private String brokerName = ""; //券商名称
    private String invMgrCode = ""; //投资经理代码
    private String invMgrName = ""; //投资经理名称
    private String tradeCode = ""; //交易方式代码
    private String tradeName = ""; //交易方式名称
    private String cashAcctCode = ""; //现金帐户代码
    private String cashAcctName = ""; //现金帐户名称
    private String attrClsCode = ""; //所属分类代码
    private String attrClsName = ""; //所属分类名称
    private String bargainDate = "1900-01-01"; //成交日期
    private String bargainTime = "00:00:00"; //成交时间
    private String settleDate = "1900-01-01"; //结算日期
    private String settleTime = "00:00:00"; //结算时间
  //----add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围---start---//
    private String bargainEndDate = "1900-01-01"; //成交(结束)日期
    private String settleEndDate = "1900-01-01"; //结算（结束）日期
  //----add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围---start---//
    private String autoSettle = "0"; //自动结算
    private double portCuryRate; //组合汇率
    private double baseCuryRate; //基础汇率
    private double tradeAmount; //交易数量
    private double tradePrice; //交易价格
    private double tradeMoney; //交易总额
    private double unitCost; //单位成本
    private double accruedInterest; //应计利息
    private double allotFactor; //分配因子
    private double totalCost; //投资总成本
    private String orderNum = ""; //订单代码
    private String desc = ""; //交易描述
    private String isOnlyColumn = ""; //是否只读取列名的标志
    private String settleState = ""; //结算标志
    private String rollBackDesc = "";
    private String fees = "";
    //add by songjie 2011.07.25 BUG 2299 QDV4赢时胜(开发部)2011年7月22日01_B
    private String investType = "";//投资类型 
    private OperationDataBean filterType;
    /**shashijie 2011-08-24 STORY 1356*/
    private String FactSettleDate = "9998-12-31";//实际结算日期
    /**end*/

    public OperationDataBean() {
    }
     
    public String getBargainEndDate() {
		return bargainEndDate;
	}

	public void setBargainEndDate(String bargainEndDate) {
		this.bargainEndDate = bargainEndDate;
	}

	public String getSettleEndDate() {
		return settleEndDate;
	}

	public void setSettleEndDate(String settleEndDate) {
		this.settleEndDate = settleEndDate;
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
            reqAry = sTmpStr.split("\t");
            if (reqAry.length < 28) {
                return;
            }
            this.num = reqAry[0];
            this.securityCode = reqAry[1];
            this.portCode = reqAry[2];
            this.brokerCode = reqAry[3];
            this.invMgrCode = reqAry[4];
            this.tradeCode = reqAry[5];
            this.cashAcctCode = reqAry[6];
            this.attrClsCode = reqAry[7];
            this.bargainDate = reqAry[8];
            this.bargainTime = reqAry[9];
            this.settleDate = reqAry[10];
            this.settleTime = reqAry[11];
            this.autoSettle = reqAry[12];
            if (reqAry[13].length() != 0) {
                this.portCuryRate = Double.parseDouble(
                    reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.baseCuryRate = Double.parseDouble(
                    reqAry[14]);
            }
            if (reqAry[15].length() != 0) {
                this.tradeAmount = Double.parseDouble(
                    reqAry[15]);
            }
            if (reqAry[16].length() != 0) {
                this.tradePrice = Double.parseDouble(
                    reqAry[16]);
            }
            if (reqAry[17].length() != 0) {
                this.tradeMoney = Double.parseDouble(
                    reqAry[17]);
            }
            if (reqAry[18].length() != 0) {
                this.unitCost = Double.parseDouble(reqAry[
                    18]);
            }
            if (reqAry[19].length() != 0) {
                this.accruedInterest = Double.parseDouble(
                    reqAry[19]);
            }
            if (reqAry[20].length() != 0) {
                this.allotFactor = Double.parseDouble(
                    reqAry[20]);
            }
            if (reqAry[21].length() != 0) {
                this.totalCost = Double.parseDouble(
                    reqAry[21]);
            }
            this.orderNum = reqAry[22];
            this.desc = reqAry[23];
            this.fees = reqAry[24];
            this.checkStateId = Integer.parseInt(reqAry[25]);
            this.isOnlyColumn = reqAry[26];
            this.bargainEndDate = reqAry[27];//add by zhaoxianlin 20130115 STORY #3441
            this.settleEndDate = reqAry[28];//add by zhaoxianlin 20130115 STORY #3441
            this.settleState = reqAry[29];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new OperationDataBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析业务数据设置请求出错", e);
        }

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.isOnlyColumn.equals("1")) {
                sResult = sResult + " and 1=2 ";
                return sResult;
            }
            if (this.filterType.portCode.length() != 0) {
                //-----------2009.05.20 蒋锦 添加 MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合-----------//
                //可选择多组合进行查询
                if (this.filterType.portCode.indexOf(",") != -1) {
                    sResult = sResult + " and a.FPortCode IN (" +
                        operSql.sqlCodes(this.filterType.portCode) + ")";
                } else {
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.portCode.replaceAll("'", "''") + "%'";
                }
                //-----------------------------------------------------------------------------------------//
            }
            if (this.filterType.tradeCode.length() != 0) {
                sResult = sResult + " and a.FTradeTypeCode = '" +
                    filterType.tradeCode.replaceAll("'", "''") + "'";
            }
            //---delete by zhaoxianlin 20130115 STORY #3441----start--//
//            if (this.filterType.bargainDate.length() != 0 &&
//                !this.filterType.bargainDate.equals("9998-12-31")) {
//                sResult = sResult + " and a.FBargainDate = " +
//                    dbl.sqlDate(filterType.bargainDate);
//            }
            //---delete by zhaoxianlin 20130115 STORY #3441----end--//
            if (this.filterType.securityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.securityCode.replaceAll("'", "''") + "%'";
            }
            /**shashijie,2011.05.25,BUG1977筛选时数据条数显示不完整，不能按照查询字段“交易券商”进行筛选 */
            if (this.filterType.brokerCode.length() != 0) {
                sResult = sResult + " and a.FBrokerCode like " +
                    dbl.sqlString(filterType.brokerCode+"%");
            }
            /**end*/
            if (this.filterType.invMgrCode.length() != 0) {
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.invMgrCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.cashAcctCode.length() != 0) {
                sResult = sResult + " and a.FCashAccCode like '" +
                    filterType.cashAcctCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.attrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.attrClsCode.replaceAll("'", "''") + "%'";
            }
          //---delete by zhaoxianlin 20130115 STORY #3441----start--//
//            if (this.filterType.settleDate.length() != 0 &&
//                !this.filterType.settleDate.equals("9998-12-31")) {
//                sResult = sResult + " and a.FFactSettleDate = " + //彭彪20071210 按实际结算日期查询
//                    dbl.sqlDate(filterType.settleDate);
//            }
          //---delete by zhaoxianlin 20130115 STORY #3441----end--//
          //----add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围---start---//
            if (this.filterType.bargainDate.length() != 0 &&
                  !this.filterType.bargainDate.equals("9998-12-31")) {
            	if(this.filterType.bargainEndDate.length() != 0 &&
                        !this.filterType.bargainEndDate.equals("9998-12-31")){
            		sResult = sResult + " and a.FBargainDate between " +
                    dbl.sqlDate(filterType.bargainDate)+" and "+dbl.sqlDate(filterType.bargainEndDate);
            	}else{
            		 sResult = sResult + " and a.FBargainDate = " +
                     dbl.sqlDate(filterType.bargainDate);
            	}
              } 
            /**Start 20131029 modified by liubo.Bug #82345.QDV4赢时胜(上海开发)2013年10月29日01_B
             * 这个BUG之前是用实际结算日期来套结算日期的筛选条件，这里换回来*/
            if (this.filterType.settleDate.length() != 0 &&
                  !this.filterType.settleDate.equals("9998-12-31")) {
            	if(this.filterType.settleEndDate.length() != 0 &&
                        !this.filterType.settleEndDate.equals("9998-12-31")){
//            		sResult = sResult + " and a.FFactSettleDate between " + 
            		sResult = sResult + " and a.FSettleDate between " + 
                    dbl.sqlDate(filterType.settleDate)+" and "+dbl.sqlDate(filterType.settleEndDate);
            	}else{
//            		 sResult = sResult + " and a.FFactSettleDate = " + 
            		sResult = sResult + " and a.FSettleDate = " + 
                    dbl.sqlDate(filterType.settleDate);
            	}
              }
            /**End 20131029 modified by liubo.Bug #82345.QDV4赢时胜(上海开发)2013年10月29日01_B*/
            
          //----add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围---start---//
            if (this.filterType.autoSettle.length() != 0 &&
                !this.filterType.autoSettle.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FAutoSettle =" +
                    filterType.autoSettle;
            }
            if (this.filterType.settleState.trim().length() != 0) {
                if (!this.filterType.settleState.trim().equals("99")) {
                    sResult = sResult + " and a.FSettleState =" +
                        filterType.settleState;
                }

            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }

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
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
//              "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
               //---add by songjie 2011.05.23 BUG 1830 QDV4赢时胜（测试）2011年4月28日01_B---//
               "select a.fnum, a.fsecuritycode, a.fportcode, a.fbrokercode, a.finvmgrcode,"+
               "a.ftradetypecode, a.fcashacccode, a.fattrclscode, a.fratedate,"+
               "a.fbargaindate, a.fbargaintime, a.fsettledate, a.fsettletime,"+
               "a.fmaturedate, a.fmaturesettledate, a.ffactcashacccode,"+
               "a.FFACTSETTLEMONEY, a.FEXRATE, a.FFACTBASERATE, a.FFACTPORTRATE,"+
               "a.FAUTOSETTLE, a.FPORTCURYRATE, a.FBASECURYRATE, a.FALLOTPROPORTION,"+
               "a.FOLDALLOTAMOUNT, a.FALLOTFACTOR, a.FTRADEAMOUNT, a.FTRADEPRICE,"+
               "a.FTRADEMONEY, a.FACCRUEDINTEREST, a.FBAILMONEY, a.FFEECODE1,"+
               "a.FTRADEFEE1, a.FFEECODE2, a.FTRADEFEE2, a.FFEECODE3, a.FTRADEFEE3,"+
               "a.FFEECODE4, a.FTRADEFEE4, a.FFEECODE5, a.FTRADEFEE5, a.FFEECODE6,"+
               "a.FTRADEFEE6, a.FFEECODE7, a.FTRADEFEE7, a.FFEECODE8, a.FTRADEFEE8,"+
               "a.FTOTALCOST, a.FCOST, a.FMCOST, a.FVCOST, a.FBASECURYCOST,"+
               "a.FMBASECURYCOST, a.FVBASECURYCOST, a.FPORTCURYCOST, a.FMPORTCURYCOST,"+
               "a.FVPORTCURYCOST, a.FSETTLESTATE, a.FSETTLEDESC, a.FORDERNUM, a.FDATASOURCE,"+
               "a.FDATABIRTH, a.FSETTLEORGCODE, a.FDESC," +
               /**shashijie 2011.05.31 ,BUG1977筛选时数据条数显示不完整，不能按照查询字段“交易券商”进行筛选*/
               " a.FSettleState as FCheckState " +
               /**end*/
               ", a.FCREATOR, a.FCREATETIME,"+
               "a.FCHECKUSER, a.FCHECKTIME, a.FETFBALAACCTCODE, a.FETFBALASETTLEDATE,"+
               "a.FETFBALAMONEY, a.FETFCASHALTERNAT, a.FSEATCODE, a.FSTOCKHOLDERCODE,"+
               "a.FDS, a.FSPLITNUM, a.FDEALNUM, a.FAPPDATE, a.FJKDR, a.FINVESTTYPE,a.FRECORDDATE, a.FDIVDENDTYPE,"+
               //---add by songjie 2011.05.23 BUG 1830 QDV4赢时胜（测试）2011年4月28日01_B---//
               " b.FUserName as FCreatorName, c.FUserName as FCheckUserName,case when a.FFactSettleDate is null then FSettleDate else FFactSettleDate end as FFactSettleDate," + //添加实际结算日期字段 若实际结算日期为空就取结算日期 byleeyu 2009-1-15 MS00129 QDV4中保2008年12月26日01_A
               " d.FPortName, e.FSecurityName, f.FInvMgrName, g.FTradeTypeName, " +
               " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName,q.FDesc as FRollBackDesc" + //由于增加了回转业务，在查看业务资料时报错chenjia 20070929
               " from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a " +
               " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
               " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
               //----------------------------------------------------------------------------------------------------
               " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//               pub.yssGetTableName("Tb_Para_Portfolio") +
//               " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
               //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
               "select FPortCode, FPortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               pub.yssGetTableName("Tb_Para_Portfolio") +
               " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               //----------------------------------------------------------------------------------------------------
               " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
               " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode,FSecurityName, FStartDate from " +
               pub.yssGetTableName("Tb_Para_Security") +
               ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
               //----------------------------------------------------------------------------------------------------
               " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//               pub.yssGetTableName("Tb_Para_InvestManager") +
//               " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
               //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
               " select FInvMgrCode, FInvMgrName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               pub.yssGetTableName("Tb_Para_InvestManager") +
               " where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               //----------------------------------------------------------------------------------------------------
               " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
               //----------------------------------------------------------------------------------------------------
               " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//               pub.yssGetTableName("Tb_Para_Broker") + " where FStartDate <= " +
//               dbl.sqlDate(new java.util.Date()) +
               //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
               " select FBrokerCode, FBrokerName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               pub.yssGetTableName("Tb_Para_Broker") +
               " where FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               //----------------------------------------------------------------------------------------------------
               " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//               pub.yssGetTableName("Tb_Para_CashAccount") +
//               " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
               //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
               " select FCashAccCode, FCashAccName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               pub.yssGetTableName("Tb_Para_CashAccount") +
               " where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
               //----------------------------------------------------------------------------------------------------
               " left join (select FAttrClsCode,FAttrClsName from " +
               pub.yssGetTableName("Tb_Para_AttributeClass") +
               " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode" +
               " left join (select FNum,FDesc from " + pub.yssGetTableName("tb_Data_TradeRollback") + " ) q on a.FNum=q.FNum " +

               buildFilterSql() +
               
               /**shashijie 2011.05.30,BUG1977筛选时数据条数显示不完整，不能按照查询字段“交易券商”进行筛选*/
               " AND a.FCheckState = 1 ORDER BY a.FSettleState,a.FCheckState, a.FCreateTime DESC";
               /**end*/
            
            //rs = dbl.openResultSet(strSql);
            /**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
            yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("SubTrade");//shashijie 2011.06.21 筛选调用ViewData2方法与查询一样,这里(增删改审)不考虑分页
            rs = dbl.openResultSet(yssPageInationBean);
            /**end*/
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
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_TRD_SETTLETYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr
                /**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
                + "\r\f" + yssPageInationBean.buildRowStr();
            	/**end*/
        } catch (Exception e) {
            throw new YssException("获取业务数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
			RightBean right = new RightBean();
			right.setYssPub(pub);
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
        	
            sHeader = this.getListView1Headers();   
            strSql =
//               "select a.FNUM,a.FSECURITYCODE,a.FPORTCODE,a.FBROKERCODE,a.FINVMGRCODE,a.FTRADETYPECODE," +
                "select a.FNUM,a.FSECURITYCODE,a.FPORTCODE,a.FBROKERCODE,a.FINVMGRCODE,a.FTRADETYPECODE,case when a.FFactSettleDate is null then FSettleDate else FFactSettleDate end as FFactSettleDate," + //添加实际结算日期字段 若实际结算日期为空就取结算日期 byleeyu 2009-1-15 MS00129 QDV4中保2008年12月26日01_A
                " a.FCASHACCCODE,a.FATTRCLSCODE,a.FBARGAINDATE,a.FBARGAINTIME,q.FDesc as FRollBackDesc," +
                " a.FSETTLEDATE,a.FSETTLETIME,a.FAUTOSETTLE,a.FPORTCURYRATE,a.FBASECURYRATE,a.FALLOTPROPORTION,a.FOLDALLOTAMOUNT,a.FALLOTFACTOR," +
                " a.FTRADEAMOUNT,a.FTRADEPRICE,a.FTRADEMONEY,a.FACCRUEDINTEREST," +
                " a.FFeeCode1, a.FTradeFee1, a.FFeeCode2, a.FTradeFee2, a.FFeeCode3, a.FTradeFee3, a.FFeeCode4, a.FTradeFee4," +
                " a.FFeeCode5, a.FTradeFee5, a.FFeeCode6, a.FTradeFee6, a.FFeeCode7, a.FTradeFee7, a.FFeeCode8, a.FTradeFee8," +
                " a.FTotalCost, a.FOrderNum, a.FDesc, a.FDataSource, a.FSettleState, " +
                //edit by songjie 2012.03.01 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A
                " case when (a.Ftradetypecode = '06' and a.FSettleState = 0) then 4 else  a.FSettleState end as FCheckState, " +
                "a.FCreator, a.FCreateTime, a.FCheckUser,a.FCheckTime, " +
                //edit by songjie 2011.07.25 BUG 2299 QDV4赢时胜(开发部)2011年7月22日01_B 添加投资类型字段
                " a.Finvesttype, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FPortName, e.FSecurityName, f.FInvMgrName, g.FTradeTypeName, " +
                " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName" +
                ",(case when a.FSecurityDelaySettleState=0 then 'N' else 'Y' end) as  FSecurityDelaySettleState"+//story 1566 add by zhouwei 20111017 新增字段代表延迟交割标示，0为未延迟，1反之
                " from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
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
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
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
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
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
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FCashAccCode, FCashAccName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode" +
                " left join (select FNum,FDesc from " + pub.yssGetTableName("tb_Data_TradeRollback") + " ) q on a.FNum=q.FNum "
              //--modify by 黄啟荣 2011-06-01 story #937 --用于系统查询出来的结果必须与用户的浏览权限一致。
    			+buildFilterSql()		
    			+" and a.FCheckState=1"
    			//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//    				+" and a.FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//		        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//					    +" and frighttype = 'port'"
//					    +" and FOPERTYPES like '%brow%'"
//					    +" and frightcode = 'tradesettle') tsu"
//					    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//					    +" tpp on tpp.fportcode=tsu.fportcode"
//					    +" where tpp.fenabled=1"
//					    +" and tpp.FCheckState=1)";
    			//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
				//add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B
				+ " and a.FPortcode in (" + operSql.sqlCodes(right.getUserPortCodes("tradesettle")) + ")";
    			strSql+=" order by a.FSettleState,a.FCheckState, a.FCreateTime desc";
    		//---end---
            /**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
            yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("SubTrade_TaTrade");
            rs = dbl.openResultSet(yssPageInationBean);
            /**end*/
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
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
			//edit by songjie 2011.07.26 BUG 2299 QDV4赢时胜(开发部)2011年7月22日01_B
            sVocStr = vocabulary.getVoc(YssCons.YSS_TRD_SETTLETYPE + "," + YssCons.YSS_InvestType);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr 
                /**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
                + "\r\f" + yssPageInationBean.buildRowStr();
            	/**end*/
        } catch (Exception e) {
            throw new YssException("获取业务数据信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }

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
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String strError = "", strReturn = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            return "";
        } catch (Exception e) {
            throw new YssException(strError, e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " set FSECURITYCODE = " + dbl.sqlString(this.securityCode) +
                ",FPORTCODE = " + dbl.sqlString(this.portCode) +
                ",FBROKERCODE = " + dbl.sqlString(this.brokerCode) +
                ",FINVMGRCODE = " +
                dbl.sqlString(this.invMgrCode.length() != 0 ? this.invMgrCode :
                              " ") +
                ",FTRADETYPECODE = " + dbl.sqlString(this.tradeCode) +
                ",FCASHACCCODE = " + dbl.sqlString(this.cashAcctCode) +
                ",FATTRCLSCODE = " + dbl.sqlString(this.attrClsCode) +
                ",FBARGAINDATE = " + dbl.sqlDate(this.bargainDate) +
                ",FBARGAINTIME = " + dbl.sqlString(this.bargainTime) +
                ",FSETTLEDATE = " + dbl.sqlDate(this.settleDate) +
                ",FSETTLETIME = " + dbl.sqlString(this.settleTime) +
                ",FAUTOSETTLE = " + dbl.sqlString(this.autoSettle) +
                ",FPORTCURYRATE = " + this.portCuryRate +
                ",FBASECURYRATE = " + this.baseCuryRate +
                ",FALLOTPROPORTION = " + this.allotFactor / 100 +
                ",FALLOTFACTOR = " + this.allotFactor +
                ",FTRADEAMOUNT = " + this.tradeAmount +
                ",FTRADEPRICE = " + this.tradePrice +
                ",FTRADEMONEY = " + this.tradeMoney +
                ",FACCRUEDINTEREST = " + this.accruedInterest +
                ",FTOTALCOST = " + this.totalCost +
                ",FORDERNUM = " + dbl.sqlString(this.orderNum) +
                ",FDESC = " + dbl.sqlString(this.desc) +
                "," + this.operSql.buildSaveFeesSql(YssCons.OP_EDIT, this.fees) +
                " FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCREATOR = " + dbl.sqlString(this.creatorCode) +
                ",FCREATETIME = " + dbl.sqlString(this.creatorTime) +
                " where FNUM = " +
                dbl.sqlString(this.num);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = " +
                dbl.sqlString(this.num);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //审核 证券应收应付  by sunny
            OperSecRecPay(this.num);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum = " +
                dbl.sqlString(this.num);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            //审核 证券应收应付  by sunny
            OperSecRecPay(this.num);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    //根据交易数据的一个编号 来获得业务数据  by sunny
    public void OperSecRecPay(String strNum) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String tsfTypeCode = "";
        String subTsfTypeCode = "";
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FNUM =" + dbl.sqlString(strNum);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                SecRecPayAdmin secrecpay = new SecRecPayAdmin();
                secrecpay.setYssPub(pub);
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase("01")) {
                    tsfTypeCode = "06";
                    subTsfTypeCode = "06FI_B";
                } else {
                    tsfTypeCode = "07";
                    subTsfTypeCode = "07FI";
                }
                if (this.checkStateId == 2) {
                    secrecpay.delete("", rs.getDate("FBargainDate"),
                                     rs.getDate("FBargainDate"), tsfTypeCode,
                                     subTsfTypeCode,
                                     rs.getString("FSecurityCode"),
                                     operFun.getSecCuryCode(rs.getString("FSecurityCode")),
                                     rs.getString("FPortCode"),
                                     rs.getString("FInvMgrCode"),
                                     rs.getString("FBrokerCode"),
                                     "", 0);
                } else if (this.checkStateId == 0) {
                    secrecpay.checkSecRecPay("", rs.getDate("FBargainDate"),
                                             rs.getDate("FBargainDate"), tsfTypeCode,
                                             subTsfTypeCode,
                                             rs.getString("FSecurityCode"),
                                             operFun.getSecCuryCode(rs.getString(
                                                 "FSecurityCode")),
                                             rs.getString("FPortCode"),
                                             rs.getString("FInvMgrCode"),
                                             rs.getString("FBrokerCode"), "",
                                             0, 0);
                } else {
                    secrecpay.checkSecRecPay("", rs.getDate("FBargainDate"),
                                             rs.getDate("FBargainDate"), tsfTypeCode,
                                             subTsfTypeCode,
                                             rs.getString("FSecurityCode"),
                                             operFun.getSecCuryCode(rs.getString(
                                                 "FSecurityCode")),
                                             rs.getString("FPortCode"),
                                             rs.getString("FInvMgrCode"),
                                             rs.getString("FBrokerCode"), ""
                                             , 0, 1);
                }
            }
        } catch (Exception e) {
            throw new YssException("操作证券应收应付表出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * saveMutliSetting
     *
     * @保存回转信息,李钰,override 070911
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.num).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.invMgrCode).append("\t");
        buf.append(this.invMgrName).append("\t");
        buf.append(this.tradeCode).append("\t");
        buf.append(this.tradeName).append("\t");
        buf.append(this.cashAcctCode).append("\t");
        buf.append(this.cashAcctName).append("\t");
        buf.append(this.attrClsCode).append("\t");
        buf.append(this.attrClsName).append("\t");
        buf.append(this.bargainDate).append("\t");
        buf.append(this.bargainTime).append("\t");
        buf.append(this.settleDate).append("\t");
        buf.append(this.settleTime).append("\t");
        buf.append(this.autoSettle).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.tradeAmount).append("\t");
        buf.append(this.tradePrice).append("\t");
        buf.append(this.tradeMoney).append("\t");
        buf.append(this.unitCost).append("\t");
        buf.append(this.accruedInterest).append("\t");
        buf.append(this.allotFactor).append("\t");
        buf.append(this.totalCost).append("\t");
        buf.append(this.orderNum).append("\t");
        buf.append(this.fees).append("\t");
        buf.append(this.desc).append("\t");
        buf.append("0").append("\t");
        buf.append(this.settleState).append("\t");
        /**shashijie 2011-08-24 STORY 1356*/
        buf.append(this.FactSettleDate).append("\t");
        /**end*/
        buf.append(super.buildRecLog());
        return buf.toString();

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
        ResultSet rs = null;
        String strSql = "";
        OperationDataBean befOperDataBean = new OperationDataBean();
        try {
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FPortName, e.FSecurityName, f.FInvMgrName, g.FTradeTypeName, " +
                " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName" +
                " from " + pub.yssGetTableName("Tb_Data_SubTrade") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
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
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode,FSecurityName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
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
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
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
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                "select FCashAccCode, FCashAccName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode" +
                " where FNum=" + dbl.sqlString(this.num);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befOperDataBean.num = rs.getString("FNum") + "";
                befOperDataBean.securityCode = rs.getString("FSecurityCode") + "";
                befOperDataBean.securityName = rs.getString("FSecurityName") + "";
                befOperDataBean.portCode = rs.getString("FPortCode") + "";
                befOperDataBean.portName = rs.getString("FPortName") + "";
                befOperDataBean.brokerCode = rs.getString("FBrokerCode") + "";
                befOperDataBean.brokerName = rs.getString("FBrokerName") + "";
                befOperDataBean.invMgrCode = rs.getString("FInvMgrCode") + "";
                befOperDataBean.invMgrName = rs.getString("FInvMgrName") + "";
                befOperDataBean.tradeCode = rs.getString("FTradeTypeCode") + "";
                befOperDataBean.tradeName = rs.getString("FTradeTypeName") + "";
                befOperDataBean.cashAcctCode = rs.getString("FCashAccCode") + "";
                befOperDataBean.cashAcctName = rs.getString("FCashAccName") + "";
                befOperDataBean.attrClsCode = rs.getString("FAttrClsCode") + "";
                befOperDataBean.attrClsName = rs.getString("FAttrClsName") + "";
                befOperDataBean.bargainDate = rs.getDate("FBargainDate") + "";
                befOperDataBean.bargainTime = rs.getString("FBargainTime") + "";
                befOperDataBean.settleDate = rs.getDate("FSettleDate") + "";
                befOperDataBean.settleTime = rs.getString("FSettleTime") + "";
                befOperDataBean.autoSettle = rs.getString("FAutoSettle") + "";
                befOperDataBean.portCuryRate = rs.getDouble("FPortCuryRate");
                befOperDataBean.baseCuryRate = rs.getDouble("FBaseCuryRate");
                befOperDataBean.allotFactor = rs.getDouble("FAllotFactor");
                befOperDataBean.tradeAmount = rs.getDouble("FTradeAmount");
                befOperDataBean.tradePrice = rs.getDouble("FTradePrice");
                befOperDataBean.tradeMoney = rs.getDouble("FTradeMoney");
                //     this.unitCost = rs.getDouble("FUnitCost");
                befOperDataBean.accruedInterest = rs.getDouble("FAccruedInterest");
                befOperDataBean.totalCost = rs.getDouble("FTotalCost");
                befOperDataBean.orderNum = rs.getString("FOrderNum");
                befOperDataBean.desc = rs.getString("FDesc");
                befOperDataBean.settleState = rs.getString("FSettleState");
                //add by songjie 2011.07.25 BUG 2299 QDV4赢时胜(开发部)2011年7月22日01_B
                befOperDataBean.investType = rs.getString("FInvestType");
                //     loadFees(rs);              20071003      chenyibo      处理日志，这里不要再加栽一次费用了，因为在修改的时候会有冲突
            }

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return befOperDataBean.buildRowStr();
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.num = rs.getString("FNum") + "";
        this.securityCode = rs.getString("FSecurityCode") + "";
        this.securityName = rs.getString("FSecurityName") + "";
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        this.brokerCode = rs.getString("FBrokerCode") + "";
        this.brokerName = rs.getString("FBrokerName") + "";
        this.invMgrCode = rs.getString("FInvMgrCode") + "";
        this.invMgrName = rs.getString("FInvMgrName") + "";
        this.tradeCode = rs.getString("FTradeTypeCode") + "";
        this.tradeName = rs.getString("FTradeTypeName") + "";
        this.cashAcctCode = rs.getString("FCashAccCode") + "";
        this.cashAcctName = rs.getString("FCashAccName") + "";
        this.attrClsCode = rs.getString("FAttrClsCode") + "";
        this.attrClsName = rs.getString("FAttrClsName") + "";
        this.bargainDate = rs.getDate("FBargainDate") + "";
        this.bargainTime = rs.getString("FBargainTime") + "";
        this.settleDate = rs.getDate("FSettleDate") + "";
        this.settleTime = rs.getString("FSettleTime") + "";
        this.autoSettle = rs.getString("FAutoSettle") + "";
        this.portCuryRate = rs.getDouble("FPortCuryRate");
        this.baseCuryRate = rs.getDouble("FBaseCuryRate");
        this.allotFactor = rs.getDouble("FAllotFactor");
        this.tradeAmount = rs.getDouble("FTradeAmount");
        this.tradePrice = rs.getDouble("FTradePrice");
        this.tradeMoney = rs.getDouble("FTradeMoney");
        //     this.unitCost = rs.getDouble("FUnitCost");
        this.accruedInterest = rs.getDouble("FAccruedInterest");
        this.totalCost = rs.getDouble("FTotalCost");
        this.orderNum = rs.getString("FOrderNum");
        this.desc = rs.getString("FDesc");
        this.rollBackDesc = rs.getString("FRollBackDesc");
        this.settleState = rs.getString("FSettleState");
        //add by songjie 2011.07.25 BUG 2299 QDV4赢时胜(开发部)2011年7月22日01_B
        this.investType = rs.getString("FInvestType");
        /**shashijie 2011-08-24 STORY 1356 */
        this.FactSettleDate = rs.getDate("FFactSettleDate") + "";
        /**end*/
        loadFees(rs);
        super.setRecLog(rs);
    }

    /**
     * modify by wangzuochun 2009.12.11 MS00838 在结算时显示的界面中没有费用的列表栏 QDV4赢时胜上海2009年11月26日03_B 
     * @param rs
     * @throws SQLException
     * @throws YssException
     */
    public void loadFees(ResultSet rs) throws SQLException, YssException {
        String sName = "";
        double dFeeMoney = 0;
        double dTotalFee = 0;
        StringBuffer buf = new StringBuffer();
        FeeBean fee = new FeeBean();
        fee.setYssPub(pub);

        for (int i = 1; i <= 8; i++) {
            if (rs.getString("FFeeCode" + i) != null &&
                rs.getString("FFeeCode" + i).trim().length() > 0) {
                fee.setFeeCode(rs.getString("FFeeCode" + i));
                
                fee.getSetting();
                //------ add by wangzuochun 2010.09.11 MS01708    交易结算未结算中进行结算时会报错    QDV4建行2010年09月08日01_B    
                //------ 根据交易子表中的费用代码去查费用设置中的费用，若费用不存在，则跳过此次循环；
                if (fee.getFeeCode() == null){
                	continue;
                }
                //----------MS01708-----------//
                
                sName = fee.getFeeName();
                if (rs.getString("FTradeFee" + i) != null) {
                    dFeeMoney = rs.getDouble("FTradeFee" + i);
                }
                dTotalFee = YssD.add(dTotalFee, dFeeMoney);
                buf.append(rs.getString("FFeeCode" + i)).append("\n");
                buf.append(sName).append("\n");
                buf.append(dFeeMoney).append("\n");
                buf.append(fee.buildRowStr().replaceAll("\t", "~")).append("\f\n");
            }
        }
        
        /**
        if (buf.toString().length() > 2) {
            buf.append("total").append("\n");
            buf.append("Total: ").append("\n");
            buf.append(dTotalFee).append("\n");
            fee.setAccountingWay("0"); //不计入成本
            buf.append(fee.buildRowStr().replaceAll("\t", "~"));
            this.fees = buf.toString();
        } else {
            this.fees = "";
        }
        **/
        
        buf.append("total").append("\n");
        buf.append("Total: ").append("\n");
        buf.append(dTotalFee).append("\n");
        fee.setAccountingWay("0"); //不计入成本
        buf.append(fee.buildRowStr().replaceAll("\t", "~"));
        this.fees = buf.toString();
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public String getTradeName() {
        return tradeName;
    }

    public double getTradeMoney() {
        return tradeMoney;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public double getTradeAmount() {
        return tradeAmount;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getSettleTime() {
        return settleTime;
    }

    public String getSettleState() {
        return settleState;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getRollBackDesc() {
        return rollBackDesc;
    }

    public String getPortName() {
        return portName;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getNum() {
        return num;
    }

    public String getIsOnlyColumn() {
        return isOnlyColumn;
    }

    public String getInvMgrName() {
        return invMgrName;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public OperationDataBean getFilterType() {
        return filterType;
    }

    public String getFees() {
        return fees;
    }

    public String getDesc() {
        return desc;
    }

    public String getCashAcctName() {
        return cashAcctName;
    }

    public String getCashAcctCode() {
        return cashAcctCode;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getBargainTime() {
        return bargainTime;
    }

    public String getBargainDate() {
        return bargainDate;
    }

    public String getAutoSettle() {
        return autoSettle;
    }

    public String getAttrClsName() {
        return attrClsName;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public double getAllotFactor() {
        return allotFactor;
    }

    public void setAccruedInterest(double accruedInterest) {
        this.accruedInterest = accruedInterest;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public void setTradeMoney(double tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public void setTradeAmount(double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setSettleTime(String settleTime) {
        this.settleTime = settleTime;
    }

    public void setSettleState(String settleState) {
        this.settleState = settleState;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setRollBackDesc(String rollBackDesc) {
        this.rollBackDesc = rollBackDesc;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setIsOnlyColumn(String isOnlyColumn) {
        this.isOnlyColumn = isOnlyColumn;
    }

    public void setInvMgrName(String invMgrName) {
        this.invMgrName = invMgrName;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setFilterType(OperationDataBean filterType) {
        this.filterType = filterType;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCashAcctName(String cashAcctName) {
        this.cashAcctName = cashAcctName;
    }

    public void setCashAcctCode(String cashAcctCode) {
        this.cashAcctCode = cashAcctCode;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setBargainTime(String bargainTime) {
        this.bargainTime = bargainTime;
    }

    public void setBargainDate(String bargainDate) {
        this.bargainDate = bargainDate;
    }

    public void setAutoSettle(String autoSettle) {
        this.autoSettle = autoSettle;
    }

    public void setAttrClsName(String attrClsName) {
        this.attrClsName = attrClsName;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setAllotFactor(double allotFactor) {
        this.allotFactor = allotFactor;
    }

    public double getAccruedInterest() {
        return accruedInterest;
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

	public String getFactSettleDate() {
		return FactSettleDate;
	}

	public void setFactSettleDate(String factSettleDate) {
		FactSettleDate = factSettleDate;
	}

}
