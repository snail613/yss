package com.yss.main.operdata.moneycontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.cashmanage.CommandBean;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdata.ExchangeRateBean;
import com.yss.main.operdata.RateTradeBean;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/******************************************************************
 * 模块：业务资料-期间头寸管控-外汇数据
 * @author benson
 *
 */
public class DivineRateTrade extends BaseDataSettingBean implements
		IDataSetting {

	//~ Propertiy -----
	private String sFnum = "";
    private Date tradeDate = null;
    private Date BeginDate = null;
    private Date EndDate = null;
    private String bCashAccCode = "";
    private String bCashAccName = "";
    private String sCashAccCode = "";
    private String sCashAccName = "";
    private Date settleDate = null;
    private Date bSettleDate = null;
    private Date reachDate = null;
    private Date bReachDate = null;
    private double bMoney;
    private double sMoney;
    private String bCuryCode = "";
    private String bCuryName = "";
    private String sCuryCode = "";
    private String sCuryName = "";
    private String sDesc = "";
    private String sPortCode = "";
    private String sPortName = "";
    private String strIsOnlyColumns = "0";
    private DivineRateTrade filterType;
    private String sRecycled = "";  //保存未解析前的字符串
    private String multAuditString = ""; //批量处理数据 MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩


	public String getsFnum() {
		return sFnum;
	}

	public void setsFnum(String sFnum) {
		this.sFnum = sFnum;
	}

	public String getsDesc() {
		return sDesc;
	}

	public void setsDesc(String sDesc) {
		this.sDesc = sDesc;
	}

	public String getsPortCode() {
		return sPortCode;
	}

	public void setsPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}

	public String getsPortName() {
		return sPortName;
	}

	public void setsPortName(String sPortName) {
		this.sPortName = sPortName;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Date getbSettleDate() {
		return bSettleDate;
	}

	public void setbSettleDate(Date bSettleDate) {
		this.bSettleDate = bSettleDate;
	}

	public Date getReachDate() {
		return reachDate;
	}

	public void setReachDate(Date reachDate) {
		this.reachDate = reachDate;
	}

	public Date getbReachDate() {
		return bReachDate;
	}

	public void setbReachDate(Date bReachDate) {
		this.bReachDate = bReachDate;
	}

	public Date getBeginDate() {
		return BeginDate;
	}

	public void setBeginDate(Date beginDate) {
		BeginDate = beginDate;
	}

	public Date getEndDate() {
		return EndDate;
	}

	public void setEndDate(Date endDate) {
		EndDate = endDate;
	}

	public String getbCashAccCode() {
		return bCashAccCode;
	}

	public void setbCashAccCode(String bCashAccCode) {
		this.bCashAccCode = bCashAccCode;
	}

	public String getbCashAccName() {
		return bCashAccName;
	}

	public void setbCashAccName(String bCashAccName) {
		this.bCashAccName = bCashAccName;
	}

	public String getsCashAccCode() {
		return sCashAccCode;
	}

	public void setsCashAccCode(String sCashAccCode) {
		this.sCashAccCode = sCashAccCode;
	}

	public String getsCashAccName() {
		return sCashAccName;
	}

	public void setsCashAccName(String sCashAccName) {
		this.sCashAccName = sCashAccName;
	}

	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	public double getbMoney() {
		return bMoney;
	}

	public void setbMoney(double bMoney) {
		this.bMoney = bMoney;
	}

	public double getsMoney() {
		return sMoney;
	}

	public void setsMoney(double sMoney) {
		this.sMoney = sMoney;
	}

	public String getbCuryCode() {
		return bCuryCode;
	}

	public void setbCuryCode(String bCuryCode) {
		this.bCuryCode = bCuryCode;
	}

	public String getbCuryName() {
		return bCuryName;
	}

	public void setbCuryName(String bCuryName) {
		this.bCuryName = bCuryName;
	}

	public String getsCuryCode() {
		return sCuryCode;
	}

	public void setsCuryCode(String sCuryCode) {
		this.sCuryCode = sCuryCode;
	}

	public String getsCuryName() {
		return sCuryName;
	}

	public void setsCuryName(String sCuryName) {
		this.sCuryName = sCuryName;
	}

	public String getStrIsOnlyColumns() {
		return strIsOnlyColumns;
	}

	public void setStrIsOnlyColumns(String strIsOnlyColumns) {
		this.strIsOnlyColumns = strIsOnlyColumns;
	}

	public DivineRateTrade getFilterType() {
		return filterType;
	}

	public void setFilterType(DivineRateTrade filterType) {
		this.filterType = filterType;
	}
    //---------------------------------------------------------------------------------------------------
    
    

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        buf.append(this.sFnum).append("\t");
        buf.append(this.sPortCode).append("\t");
        buf.append(this.sPortName).append("\t");
        buf.append(YssFun.formatDate(this.tradeDate, "yyyy-MM-dd")).append("\t");
        buf.append(YssFun.formatDate(this.settleDate, "yyyy-MM-dd")).append("\t");
        buf.append(YssFun.formatDate(this.bSettleDate, "yyyy-MM-dd")).append("\t");
        buf.append(YssFun.formatDate(this.reachDate, "yyyy-MM-dd")).append("\t");
        buf.append(YssFun.formatDate(this.bReachDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.bCashAccCode).append("\t");
        buf.append(this.bCashAccName).append("\t");
        buf.append(this.sCashAccCode).append("\t");
        buf.append(this.sCashAccName).append("\t");
        buf.append(this.bMoney).append("\t");
        buf.append(this.sMoney).append("\t");
        buf.append(this.bCuryCode).append("\t");
        buf.append(this.sCuryCode).append("\t");
        buf.append(this.bCuryName).append("\t");
        buf.append(this.sCuryName).append("\t");
        buf.append(this.sDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		if (sType.equalsIgnoreCase("multauditTradeSub")) { //判断是否要进行批量审核与反审核
            if (multAuditString.length() > 0) { //判断批量审核与反审核的内容是否为空
                return this.auditMutli(this.multAuditString); //执行批量审核/反审核
            }
        }
		return "";
	}

	 /**
     * parseRowStr
     * 解析汇率行情数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";

        String sMutiAudit = ""; 
        try {
            
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];  
                multAuditString = sMutiAudit;                   
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];     
            }

            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; 
            reqAry = sTmpStr.split("\t");
            this.tradeDate = YssFun.parseDate(reqAry[0], "yyyy-MM-dd");
            this.settleDate = YssFun.parseDate(reqAry[1], "yyyy-MM-dd");
            this.bSettleDate = YssFun.parseDate(reqAry[2], "yyyy-MM-dd");
            this.reachDate = YssFun.parseDate(reqAry[3], "yyyy-MM-dd");
            this.bReachDate = YssFun.parseDate(reqAry[4], "yyyy-MM-dd");
            this.bCuryCode = reqAry[5];
            this.sCuryCode = reqAry[6];
            this.bCashAccCode = reqAry[7];
            this.sCashAccCode = reqAry[8];
            this.bMoney = YssFun.toDouble(reqAry[9]);
            this.sMoney = YssFun.toDouble(reqAry[10]);
            this.BeginDate = YssFun.parseDate(reqAry[11], "yyyy-MM-dd");
            this.EndDate = YssFun.parseDate(reqAry[12], "yyyy-MM-dd");
            this.checkStateId = YssFun.toInt(reqAry[13]);
            this.sFnum = reqAry[14];
            this.sPortCode = reqAry[15];
            this.sDesc = reqAry[16];
            this.strIsOnlyColumns = reqAry[17];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DivineRateTrade();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析外汇交易数据设置请求出错", e);
        }
    }
 
	public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            if (strSql.length() != 0) {
                //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
                //rs = dbl.openResultSet(strSql);
                yssPageInationBean.setsQuerySQL(strSql);
                yssPageInationBean.setsTableName("RateTrade");
                rs =dbl.openResultSet(yssPageInationBean);
                //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
                while (rs.next()) {
                    bufShow.append(super.buildRowShowStr(rs,
                        this.getListView1ShowCols())).
                        append(YssCons.YSS_LINESPLITMARK);
                    setResultSetAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);

            sVocStr = vocabulary.getVoc(YssCons.YSS_TA_TradeType + "," +
                                        YssCons.YSS_TA_CatType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji

        } catch (Exception e) {
            throw new YssException("获取外汇交易数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }
	
	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
       
        this.sFnum = rs.getString("FNum");
        this.sPortCode = rs.getString("FPortCode");
        this.sPortName = rs.getString("FPortName");
        this.tradeDate = rs.getDate("FtradeDate");
        this.settleDate = rs.getDate("FsettleDate");
        this.bSettleDate = rs.getDate("FBSettledate");
        this.reachDate = rs.getDate("FReachDate");
        this.bReachDate = rs.getDate("FBReachDate");
        this.bCashAccCode = rs.getString("FbCashAccCode");
        this.bCashAccName = rs.getString("FbCashAccName");
        this.sCashAccCode = rs.getString("FsCashAccCode");
        this.sCashAccName = rs.getString("FsCashAccName");
        this.bMoney = rs.getDouble("FbMoney");
        this.sMoney = rs.getDouble("FsMoney");
        this.bCuryCode = rs.getString("FBCuryCode");
        this.bCuryName = rs.getString("FBCuryName");
        this.sCuryCode = rs.getString("FSCuryCode");
        this.sCuryName = rs.getString("FSCuryName");
        this.sDesc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

	 /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strIsOnlyColumns.equalsIgnoreCase("1")) {
                sResult = sResult + " and 1=2 ";
                return sResult;
            }
            if (this.filterType.sPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.sPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bCuryCode.length() != 0) {
                sResult = sResult + " and a.FBCuryCode like '" +
                    filterType.bCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sCuryCode.length() != 0) {
                sResult = sResult + " and a.FSCuryCode like '" +
                    filterType.sCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bCashAccCode.length() != 0) {
                sResult = sResult + " and a.FbCashAccCode like '" +
                    filterType.bCashAccCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sCashAccCode.length() != 0) {
                sResult = sResult + " and a.FsCashAccCode like '" +
                    filterType.sCashAccCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.tradeDate != null &&
                !YssFun.formatDate(filterType.tradeDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FtradeDate = " +
                    dbl.sqlDate(filterType.tradeDate);
            }
            if (this.filterType.settleDate != null &&
                !YssFun.formatDate(filterType.settleDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FsettleDate = " +
                    dbl.sqlDate(filterType.settleDate);
            } if (this.filterType.bSettleDate != null &&
                !YssFun.formatDate(filterType.bSettleDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FbsettleDate = " +
                    dbl.sqlDate(filterType.bSettleDate);
            }if (this.filterType.reachDate != null &&
                !YssFun.formatDate(filterType.reachDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FREACHDATE = " +
                    dbl.sqlDate(filterType.reachDate);
            }if (this.filterType.bReachDate != null &&
                !YssFun.formatDate(filterType.bReachDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FBREACHDATE = " +
                    dbl.sqlDate(filterType.bReachDate);
            }
            if (this.filterType.bMoney != 0) {
                sResult = sResult + " and FbMoney = " +
                    filterType.bMoney;
            }
            if (this.filterType.sMoney != 0) {
                sResult = sResult + " and FsMoney = " +
                    filterType.sMoney;
            }
            if (this.filterType.EndDate != null &&
                !YssFun.formatDate(filterType.EndDate).equals("9998-12-31")) {
                if (this.filterType.BeginDate != null &&
                    !YssFun.formatDate(filterType.BeginDate).equals(
                        "9998-12-31")) {
                    sResult += " and a.FTradeDate between " +
                        dbl.sqlDate(this.filterType.BeginDate) +
                        " and " + dbl.sqlDate(this.filterType.EndDate);
                }
            }
            /*******************************************************
             * 日期区间段查询 [起始日期，截止日期]
             * 只勾选起始日期，查询从起始日期开始所有数据(包括起始日期)
             * 只勾选终止日期，查询出到截止日期止的数据（包括截止日期）
             */
            if (this.filterType.EndDate != null &&
                !YssFun.formatDate(filterType.EndDate).equals("9998-12-31")) {
                if (this.filterType.BeginDate != null &&
                    !YssFun.formatDate(filterType.BeginDate).equals(
                        "9998-12-31")) {
                    sResult += " and a.FTradeDate between " +
                        dbl.sqlDate(this.filterType.BeginDate) +
                        " and " + dbl.sqlDate(this.filterType.EndDate);
                }else{
                	sResult += " and a.FTradeDate <= " + dbl.sqlDate(this.filterType.EndDate);
                }
            }
            if (this.filterType.BeginDate != null &&
                    !YssFun.formatDate(filterType.BeginDate).equals(
                        "9998-12-31")) {
            	
            		sResult += " and a.FTradeDate >= " + dbl.sqlDate(this.filterType.BeginDate);
            }
        }
        return sResult;
    }
    //---------------------------------------------------------------------------------------------------	
    
    public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
    
    public String getListViewData1() throws YssException {
		 String strSql = "";
	        try {
	            
	                strSql =" select  a.*,b.fusername as FCreatorName,c.fusername as FCheckUserName,d.FPortName as FPortName,e.fcashaccname as FbCashAccName," +
	                		" f.fcashaccname as FsCashAccName,g.fcuryname as FBCuryName,h.fcuryname as FSCuryName from " + pub.yssGetTableName("tb_data_divineratetrade") +" a"+
	                        " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode "+
	                        " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "+
	                        " left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
//				+pub.yssGetTableName("tb_Para_Portfolio")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
				+" select FSTARTDATE, fportcode, FPORTNAME from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				+pub.yssGetTableName("tb_Para_Portfolio")+
				" where FCheckState = 1"//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				+") d on a.FPortCode = d.FPortCode"+
	                        " left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
//				+pub.yssGetTableName("Tb_Para_CashAccount")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
				+" select FSTARTDATE, FCashAccCode, FCashAccName from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				+pub.yssGetTableName("Tb_Para_CashAccount")+
				" where FCheckState = 1"//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				+") e on a.FBCashAccCode = e.FCashAccCode "+
	                        " left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
//				+pub.yssGetTableName("Tb_Para_CashAccount")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
				+" select FSTARTDATE, FCashAccCode, FCashAccName from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				+pub.yssGetTableName("Tb_Para_CashAccount")+
				" where FCheckState = 1"//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
				+") f on a.FSCashAccCode = f.FCashAccCode "+
	                        " left join (select FCuryCode, FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState = 1) g on a.FBCuryCode = g.FCuryCode" +
	                        " left join (select FCuryCode, FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState = 1) h on a.FSCuryCode = h.FCuryCode "+
	                        buildFilterSql()+" order by FNum ";
	                    
	        } catch (Exception e) {
	            throw new YssException("获取外汇交易数据设置信息" + "\r\n" + e.getMessage(), e);
	        }
	        return builderListViewData(strSql);
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
    //---------------------------------------------------------------------------------------------------
	
	public void checkInput(byte btOper) throws YssException {
		 dbFun.checkInputCommon(btOper, pub.yssGetTableName("tb_data_divineratetrade"),
                 "FNum", this.sFnum, this.sFnum);

	}
	
	public String addSetting() throws YssException {
		  Connection conn = dbl.loadConnection();
	        boolean bTrans = false;
	        String strSql = "";
	        String strNumberDate = "";
	        try {
	            strNumberDate = YssFun.formatDate(this.tradeDate,YssCons.YSS_DATETIMEFORMAT).substring(0, 8);

	            this.sFnum = "T" + strNumberDate +"00000"+
	                dbFun.getNextInnerCode(pub.yssGetTableName("tb_data_divineratetrade"),
	                                       dbl.sqlRight("FNUM", 6), "000001",
	                                       " where FNUM like 'T"
	                                       + strNumberDate + "%'", 1);

	            strSql =
	                " insert into " + pub.yssGetTableName("tb_data_divineratetrade") + "(FNUM,FPORTCODE, FTradeDate, FBCashAccCode,FSCashAccCode," +
	                "  FSettleDate,FBSettleDate,FReachDate,FBReachDate,FBMoney,FSMoney,FBCuryCode,FSCuryCode,FDESC,FCheckState, FCreator, FCreateTime) " + 
	                " values(" +
	                dbl.sqlString(this.sFnum) + "," +
	                dbl.sqlString(this.sPortCode)+","+
	                dbl.sqlDate(this.tradeDate) + "," +
	                dbl.sqlString(this.bCashAccCode) + "," +
	                dbl.sqlString(this.sCashAccCode) + "," +
                    dbl.sqlDate(this.settleDate) + "," +
                    dbl.sqlDate(this.bSettleDate) + "," +
                    dbl.sqlDate(this.reachDate) + "," +
                    dbl.sqlDate(this.bReachDate) + "," +
                    (this.bMoney) + "," +
	                (this.sMoney) + "," +
                    dbl.sqlString(this.bCuryCode) + "," +
	                dbl.sqlString(this.sCuryCode) + "," +
	                dbl.sqlString(this.sDesc) + "," +
                   (pub.getSysCheckState() ? "0" : "1") + "," +
	                dbl.sqlString(this.creatorCode) +
	                ", " + dbl.sqlString(this.creatorTime) +")";
	            
	            conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	            
	        } catch (Exception e) {
	            throw new YssException("新增换汇交易数据设置信息出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	        return "";
	}

	public void checkSetting() throws YssException {
		String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("tb_data_divineratetrade") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum = " + dbl.sqlString(this.sFnum);
                    conn.setAutoCommit(false);
                    bTrans = true;
                    dbl.executeSql(strSql);

                }
            }
            //如果sRecycled为空，而Num不为空，则按照Num来执行sql语句
            else if (sFnum != null && (!sFnum.equalsIgnoreCase(""))) {
                strSql = "update " + pub.yssGetTableName("tb_data_divineratetrade") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FNum = " + dbl.sqlString(this.sFnum);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核外汇交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		
        boolean bTrans = false;
        String strSql = "";
        TransferBean transfer = null; //资金调拨 BugID:MS00167 QDV4赢时胜上海2009年1月7日01_B
        try {
            strSql = "update " + pub.yssGetTableName("tb_data_divineratetrade") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum = " + dbl.sqlString(this.sFnum);
            
            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除外汇交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

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
                        pub.yssGetTableName("tb_data_divineratetrade") +
                        " where FNum = " + dbl.sqlString(this.sFnum);
                    //执行sql语句
                    dbl.executeSql(strSql);
                   
                }
            }
            //sRecycled如果sRecycled为空，而Num不为空，则按照Num来执行sql语句
            else if (sFnum != "" && sFnum != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("tb_data_divineratetrade") +
                    " where FNum = " + dbl.sqlString(this.sFnum);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public String editSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("tb_data_divineratetrade") +
            " set FPORTCODE = " + dbl.sqlString(this.sPortCode) +
            ",FBCASHACCCODE = " + dbl.sqlString(this.bCashAccCode) +
            ",FSCASHACCCODE = " + dbl.sqlString(this.sCashAccCode) + 
            ",FBCURYCODE = " + dbl.sqlString(this.bCuryCode) +
            ",FSCURYCODE = " + dbl.sqlString(this.sCuryCode) +
            ",FBMONEY = " + this.bMoney +
            ",FSMONEY = " + this.sMoney +
            ",FTRADEDATE = " + dbl.sqlDate(this.tradeDate) +
            ",FSETTLEDATE = " + dbl.sqlDate(this.settleDate) +
            ",FBSETTLEDATE = " + dbl.sqlDate(this.bSettleDate) +
            ",FREACHDATE = " + dbl.sqlDate(this.reachDate) +
            ",FBREACHDATE = " + dbl.sqlDate(this.bReachDate) +
            ",FDESC = " + dbl.sqlString(this.sDesc) +
            ",FCheckState = " +this.checkStateId + ", FCheckUser = " +dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +YssFun.formatDatetime(new java.util.Date()) + "'" +
            " where FNum = " +dbl.sqlString(this.sFnum) ;

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
           return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改外汇交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null; //建立一个数据库连接
        String sqlStr = ""; //创建一个字符串
        PreparedStatement psmt1 = null;
        //PreparedStatement psmt2 = null;
        boolean bTrans = true; //建一个boolean变量，默认自动回滚
        DivineRateTrade tmpRate = null; //创建一个外汇交易pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); 
            sqlStr = "update " + pub.yssGetTableName("tb_data_divineratetrade") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = ?"; //更新数据库审核与未审核的SQL语句
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句

            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                        tmpRate = new DivineRateTrade(); //new 一个pojo类
                        tmpRate.setYssPub(pub); //设置一些基础信息
                        tmpRate.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                        //更新外汇交易
                        psmt1.setString(1, tmpRate.sFnum); //设置SQL语句的查寻条件
                        psmt1.addBatch();
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                //psmt2.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核换汇交易表出错!");
        } finally {
            //关闭游标，结束事物
            dbl.closeStatementFinal(psmt1);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }	
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		ExchangeRateBean befEditBean = new ExchangeRateBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
	}

    
}
