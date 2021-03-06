package com.yss.main.cashmanage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.syssetting.RightBean;
import com.yss.util.*;

/**
 * story 1911 by zhouwei 20111128 QDV4招商基金2011年11月22日01_A
 * <p>Title: TransToCommandBean </p>
 * <p>Description: 现金管理--现金调拨（主表） </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */
public class TransToCommandBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strNum = "";             //现金调拨编号
    private String strPortCode = "";
    private String strPortName = "";
    private String strTsfTypeCode = "";     //调拨类型代码
    private String strTsfTypeName = "";     //调拨类型名称
    private String strSubTsfTypeCode = "";  //调拨子类型代码
    private String strSubTsfTypeName = "";  //调拨子类型名称
    private String strAttrClsCode = "";     //属性代码
    private String strAttrClsName = "";     //属性名称
    private String strSecurityCode = "";    //投资品种代码
    private String strSecurityName = "";    //投资品种名称
    private java.util.Date dtTransferDate;  //调拨日期
    private String strTransferTime = "";    //调拨时间
    private java.util.Date dtTransDate;     //业务日期
    private String strTradeNum = "";        //交易记录
    private String strDesc = "";            //现金调拨描述
    private String strOldNum = "";
    private String srcCashAccCode = "";     //来源帐户代码
    private String srcCashAccName = "";     //来源帐户名称
    private String strIsOnlyColumns = "0";  //在初始登陆时是否只显示标题，不查询数据
    private String savingNum = "";          //定存编号
    private String cprNum = "";             //现金应收应付编号
    private String FIPRNum = "";            //运营应收应付编号
    private String rateTradeNum = "";       //汇率交易的编号
    private TransToCommandBean filterType;
    private String strTansferSet = "";
    private String FRelaNum = "";           //关联编号  wdy add
    private String FNumType = "";           //编号类型  wdy add
    private int dataSource = 0;             //这里应默认数据来源类型为手工添加 0 by leeyu BUG:MS00020 2008-11-24
    private int inOut = 0;                  //凋拨反向
    //-----------------为了在收益支付时可单独添加各自的子调拨 sj add 20080123---------//
    private ArrayList subTrans = null;
    private String sRecycled = "";

    //------ MS00141 QDV4交银施罗德2009年01月4日02_B sj modified ---------//
    private String relaOrderNum = ""; //关联数据排序编号。

    public String getRelaOrderNum() {
        return relaOrderNum;
    }

    public void setRelaOrderNum(String sRelaOrderNum) {
        relaOrderNum = sRelaOrderNum;
    }

    //------------------------------------------------------------------//

    public void setSubTrans(ArrayList list) {
        this.subTrans = list;
    }

    public ArrayList getSubTrans() {
        return this.subTrans;
    }

    //---------------------------------------------------------------------------
    public void setFRelaNum(String FRelaNum) { // wdy add
        this.FRelaNum = FRelaNum;
    }

    public void setFNumType(String FNumType) { // wdy add
        this.FNumType = FNumType;
    }

    public void setFIPRNum(String FIPRNum) {
        this.FIPRNum = FIPRNum;
    }

    public void setCheckStateId(int checkStateId) {
        this.checkStateId = checkStateId;
    }

    public void setStrTradeNum(String strTradeNum) {
        this.strTradeNum = strTradeNum;
    }

    public void setFilterType(TransToCommandBean filterType) {
        this.filterType = filterType;
    }

    public void setStrTsfTypeName(String strTsfTypeName) {
        this.strTsfTypeName = strTsfTypeName;
    }

    public void setStrTransferTime(String strTransferTime) {
        this.strTransferTime = strTransferTime;
    }

    public void setStrTsfTypeCode(String strTsfTypeCode) {
        this.strTsfTypeCode = strTsfTypeCode;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrOldNum(String strOldNum) {
        this.strOldNum = strOldNum;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setStrSubTsfTypeName(String strSubTsfTypeName) {
        this.strSubTsfTypeName = strSubTsfTypeName;
    }

    public void setDtTransDate(java.util.Date dtTransDate) {
        this.dtTransDate = dtTransDate;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrAttrClsCode(String strAttrClsCode) {
        this.strAttrClsCode = strAttrClsCode;
    }

    public void setStrAttrClsName(String strAttrClsName) {
        this.strAttrClsName = strAttrClsName;
    }

    public void setStrNum(String strNum) {
        this.strNum = strNum;
    }

    public void setDtTransferDate(java.util.Date dtTransferDate) {
        this.dtTransferDate = dtTransferDate;
    }

    public void setStrIsOnlyColumns(String strIsOnlyColumns) {
        this.strIsOnlyColumns = strIsOnlyColumns;
    }

    public void setStrTansferSet(String strTansferSet) {
        this.strTansferSet = strTansferSet;
    }

    public void setStrSubTsfTypeCode(String strSubTsfTypeCode) {
        this.strSubTsfTypeCode = strSubTsfTypeCode;
    }

    public void setSrcCashAccName(String srcCashAccName) {
        this.srcCashAccName = srcCashAccName;
    }

    public void setSrcCashAccCode(String srcCashAccCode) {
        this.srcCashAccCode = srcCashAccCode;
    }

    public void setCprNum(String cprNum) {
        this.cprNum = cprNum;
    }

    public void setSavingNum(String savingNum) {
        this.savingNum = savingNum;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }

    public void setRateTradeNum(String rateTradeNum) {
        this.rateTradeNum = rateTradeNum;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public String getFIPRNum() {
        return FIPRNum;
    }

    public String getStrTradeNum() {
        return strTradeNum;
    }

    public TransToCommandBean getFilterType() {
        return filterType;
    }

    public String getStrTsfTypeName() {
        return strTsfTypeName;
    }

    public String getStrTransferTime() {
        return strTransferTime;
    }

    public String getStrTsfTypeCode() {
        return strTsfTypeCode;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrOldNum() {
        return strOldNum;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrSubTsfTypeName() {
        return strSubTsfTypeName;
    }

    public java.util.Date getDtTransDate() {
        return dtTransDate;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrAttrClsCode() {
        return strAttrClsCode;
    }

    public String getStrAttrClsName() {
        return strAttrClsName;
    }

    public String getStrNum() {
        return strNum;
    }

    public java.util.Date getDtTransferDate() {
        return dtTransferDate;
    }

    public String getStrIsOnlyColumns() {
        return strIsOnlyColumns;
    }

    public String getStrTansferSet() {
        return strTansferSet;
    }

    public String getStrSubTsfTypeCode() {
        return strSubTsfTypeCode;
    }

    public String getSrcCashAccName() {
        return srcCashAccName;
    }

    public String getSrcCashAccCode() {
        return srcCashAccCode;
    }

    public String getFRelaNum() { // wdy add
        return FRelaNum;
    }

    public String getFNumType() { // wdy add
        return FNumType;
    }

    public String getCprNum() {
        return cprNum;
    }

    public String getSavingNum() {
        return savingNum;
    }

    public int getCheckStateId() {
        return checkStateId;
    }

    public int getDataSource() {
        return dataSource;
    }

    public String getRateTradeNum() {
        return rateTradeNum;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public TransToCommandBean() {
    }
    //解析字符串
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
                    this.strTansferSet = sRowStr.split("\r\t")[2];
                }

            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sTmpStr;
            reqAry = sTmpStr.split("\t");
            this.strNum = reqAry[0];
            this.strTsfTypeCode = reqAry[1];
            this.strSubTsfTypeCode = reqAry[2];
            this.strAttrClsCode = reqAry[3];
            this.strSecurityCode = reqAry[4];
            this.dtTransferDate = YssFun.toDate(reqAry[5]);
			/**add---shashijie 2013-04-27 解决赵贤林开发需求3441引起的划款指令点击生成报错问题*/
            //add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围
            //this.dtTransferEndDate = YssFun.toDate(reqAry[6]);
            this.strTransferTime = reqAry[7];
            this.dtTransDate = YssFun.toDate(reqAry[8]);
            //add by zhaoxianlin 20130115 STORY #3441 交易结算、资金调拨模块业务日期和结算日期支持选择日期范围
            //this.dtTransEndDate = YssFun.toDate(reqAry[9]);
            this.strTradeNum = reqAry[10];
            this.strDesc = reqAry[11];
            this.strIsOnlyColumns = reqAry[12];
            this.checkStateId = Integer.parseInt(reqAry[13]);
            this.strOldNum = reqAry[14];
            this.strDesc = reqAry[15];
            if (reqAry[16].trim().length() == 0) {
                this.srcCashAccCode = " ";
            }
            this.srcCashAccCode = reqAry[16];
            this.srcCashAccName = reqAry[17];
			/**end---shashijie 2013-04-27 解决赵贤林开发需求3441引起的划款指令点击生成报错问题*/
			
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new TransToCommandBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析现金调拨请求信息出错\r\n" + e.getMessage(), e);
        }

    }
    //批量生成划款指令的解析方式
    private void parsePerRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String temp = "";
        String sTmpStr = "";
        try {
            
            sTmpStr = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.strNum = reqAry[0];
            this.strTsfTypeCode = reqAry[1];
            this.strSubTsfTypeCode = reqAry[2];
            this.strAttrClsCode = reqAry[3];
            this.strSecurityCode = reqAry[4];
            this.dtTransferDate = YssFun.toDate(reqAry[5]);
            this.strTransferTime = reqAry[7]; // add dongqingsong bug 83381
            this.dtTransDate = YssFun.toDate(reqAry[6]);// add dongqingsong bug 83381
            this.strTradeNum = reqAry[8];
            this.strDesc = reqAry[9];
            this.strIsOnlyColumns = reqAry[10];
			// add dongqingsong bug 83381
            if(reqAry[11].trim().equals("")||reqAry[11] == null){
            	 temp ="0";
            }else{
            	temp =reqAry[11];
            }
			// add dongqingsong bug 83381
            this.checkStateId = Integer.parseInt(temp);
            this.strOldNum = reqAry[12];
            this.strDesc = reqAry[13];
            if (reqAry[14].trim().length() == 0) {
                this.srcCashAccCode = " ";
            }
            this.srcCashAccCode = reqAry[14];
            this.srcCashAccName = reqAry[15];
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new TransToCommandBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析现金调拨请求信息出错\r\n" + e.getMessage(), e);
        }

    }
    //组合字符串，往前台传值
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strNum).append("\t");
        buf.append(this.strTsfTypeCode).append("\t");
        buf.append(this.strTsfTypeName).append("\t");
        buf.append(this.strSubTsfTypeCode).append("\t");
        buf.append(this.strSubTsfTypeName).append("\t");
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(YssFun.formatDate(this.dtTransferDate)).append("\t");
        buf.append(this.strTransferTime).append("\t");
        buf.append(YssFun.formatDate(this.dtTransDate)).append("\t");
        buf.append(this.strTradeNum).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(this.srcCashAccCode).append("\t");
        buf.append(this.srcCashAccName).append("\t");
        //------ add by wangzuochun 2010.02.23  MS00922  资金调拨和综合业务界面没有判断组合的权限  QDV4赢时胜上海2010年01月12日01_B    
        buf.append(this.strPortCode).append("\t");
        //--------------- MS00922 ----------------//
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting(boolean bAutoCommit) throws YssException {
    	return null;
    }

    public String addSetting() throws YssException {
        return addSetting(false);
    }

    /**
     * 获取资金调拨的其它信息
     * @throws YssException
     * 20090420 MS00395 QDV4海富通2009年04月20日01_AB
     */
    private void getRelaInfo() throws YssException {
        String strSql = "";
        ResultSet relaRs = null; //获取其他信息的ResultSet,如关联编号
        strSql = "select * from " + pub.yssGetTableName("Tb_Cash_Transfer") +
            " where FNum =" + dbl.sqlString(strOldNum);
        try {
            relaRs = dbl.openResultSet(strSql);
            if (relaRs.next()) {
                this.FRelaNum = relaRs.getString("FRelaNum") == null ? "" : relaRs.getString("FRelaNum"); //将关联编号赋值
                this.FNumType = relaRs.getString("FNumType") == null ? "" : relaRs.getString("FNumType"); //编号类型
                // MS00411 QDV4赢时胜（上海）2009年4月24日01_B  增加获取换汇编号的获取 -------------------------------------------
                this.rateTradeNum = relaRs.getString("FRateTradeNum") == null ? "" : relaRs.getString("FRateTradeNum"); //换汇编号
                this.dataSource = relaRs.getInt("FDataSource"); //新要求获取数据来源;
                //----------------------------------------------------------------------------------------------------------
                //xuqiji 20090526:QDV4赢时胜（上海）2009年5月5日02_B  MS00437    进行存款业务处理时产生的资金调拨会重复---
                this.savingNum = relaRs.getString("FSAVINGNUM") == null ? "" : relaRs.getString("FSAVINGNUM");
                //------------------------------------end--------------------------------------------------------//
            }
        } catch (Exception ex) {
            throw new YssException("获取资金调拨关联编号出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(relaRs);
        }
    }

    public String editSetting() throws YssException {
    	return null;
    }

    public void delSetting() throws YssException {}

    public void checkSetting() throws YssException {}

    public String saveMutliOperData(String sMutilRowStr) throws YssException {
        return "";
    }

    public void getOperData() throws YssException {

    }

    public String builderListViewData(String strSql) throws YssException {
        return builderListViewData(strSql, 1);
    }

    public String builderListViewData(String strSql, int ilistViewIndex) throws YssException {
        String sHeader = "", sShowCols = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            if (ilistViewIndex == 1) {
                sHeader = this.getListView1Headers();
                sShowCols = this.getListView1ShowCols();
            } else {
                sHeader = this.getListView3Headers();
                sShowCols = this.getListView3ShowCols();
            }
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
          //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            //add by yangheng 20100820 MS01310
            if (this.filterType!=null&&this.filterType.strIsOnlyColumns.equals("1")&&!(pub.isBrown())) {	//20111027 modified by liubo.STORY #1285.
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji

            }
            //--------------------------------------end MS01310--------------------------------------------------------
			// QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("Transfer");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, sShowCols)).
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
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }

    }
    /*
     * story 1911 by zhouwei 20111128 QDV4招商基金2011年11月22日01_A
     * 隐含条件：资金调拨中，至少有一个现金账户的“开户银行”为主托管行。
     * */
    public String getListViewData1() throws YssException {
    	//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
		RightBean right = new RightBean();
		right.setYssPub(pub);
		//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
        String strSql = "";
        strSql = "select y.* from " +
            "(select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
            " group by FNum,FCheckState) x join" + // by ly 去掉FcheckState<>2 因为前台的回收站要用

            "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName as FtsfTypeName, " +
            "e.FSubTsfTypeName as FsubtsfTypeName,f.FAttrClsName as FattrClsName,g.FSecurityName as FSecurityName, " +
            " gg.FCashAccCode as FCashAccCode,gg.FCashAccName as FCashAccName, kk.FPortCode as FPortCode " + //------ modify by wangzuochun 2010.02.23  MS00922  资金调拨和综合业务界面没有判断组合的权限  QDV4赢时胜上海2010年01月12日01_B
            " from " + pub.yssGetTableName("Tb_Cash_Transfer") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FTsfTypeCode,FTsfTypeName from  Tb_Base_TransferType where FCheckState = 1) d on a.FTsfTypeCode = d.FTsfTypeCode" +

            " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType where FCheckState = 1) e on a.FSubTsfTypeCode = e.FSubTsfTypeCode" +

            " left join (select FAttrClsCode,FAttrClsName from " +
            pub.yssGetTableName("Tb_Para_AttributeClass") +
            " where FCheckState = 1) f on a.FAttrClsCode= f.FAttrClsCode" +

            " left join (select ef.*, eg.fsecurityname from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") + " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ef join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") eg on ef.FSecurityCode = eg.FSecurityCode and ef.FStartDate = eg.FStartDate) g on a.FSecurityCode = g.FSecurityCode " +
            " left join (select FCashAccCode,FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") + ") gg on gg.FCashAccCode=a.FSrcCashAcc " +
            //------ add by wangzuochun 2010.02.23  MS00922  资金调拨和综合业务界面没有判断组合的权限  QDV4赢时胜上海2010年01月12日01_B 
            " left join (select FNum,Fportcode from " + pub.yssGetTableName("Tb_Cash_SubTransfer") + 
            " group by FNum,FPortcode) kk on a.fnum = kk.fnum "+
            //------------------- MS00922 ---------------------//
          //--modify by 黄啟荣 2011-06-01 story #937 --用于系统查询出来的结果必须与用户的浏览权限一致。
			buildFilterSql()+
            ") y on x.FNum = y.FNum " +
            " join (select FPortcode from "+pub.yssGetTableName("tb_para_portfolio")+" where fcheckstate = 1) p on p.fportcode = y.FPortCode " //modify by wangzuochun 2010.11.15 BUG #358 浏览划款指令产生的资金调拨数据时，提示【无权限】。 
//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//            +" where FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//	        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//				    +" and frighttype = 'port'"
//				    +" and FOPERTYPES like '%brow%'"
//				    +" and frightcode = 'cashtransfer') tsu"
//				    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//				    +" tpp on tpp.fportcode=tsu.fportcode"
//				    +" where tpp.fenabled=1"
//				    +" and tpp.FCheckState=1)";
//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
			//add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B
			//edit by songjie 2011.07.26 BUG 2308 QDV4博时2011年07月26日01_B
            + " where y.FPortcode in (" + operSql.sqlCodes(right.getUserPortCodes("cashtransfer")) + ")";
			//---end---	 
            strSql+=" order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    public String getListViewData2() throws YssException {

        return "";

    }

    public String getListViewData3() throws YssException {
        String strSql = "";
        String sAry[] = null;
        //edit by rujiangpeng 20100504 MS01116 QDV4工银2010年04月22日01_B (FNum&FDesc列重复，创建视图会报错)
        try {
        	//------ modify by wangzuochun 2011.01.27 BUG #994 资金调拨，查询时，选择显示级别为调拨子类型，查询时出错 
        	//------ 由于资金调拨子表增加了所属分类字段FAttrClsCode，主表子表关联后造成FAttrClsCode列重复，创建视图会报错
            strSql =
                "select a.*,tsf.*,case when a.Finout=1 then '流入' else '流出' end as FInOutName, b.fusername as fcreatorname, c.fusername as fcheckusername, d.FPortName, e.FCashAccName,f1.FAttrClsName ";
            strSql = strSql +
                ( (this.getCashStorageAnalysisSql().trim().length() == 0) ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ");

            strSql = strSql + " from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") + " a " +
                " join (select t1.*,d1.FTsfTypeName,e1.FSubTsfTypeName,g1.fsecurityname from " +
                " (select FNum as FNum1,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransferTime,FTransDate, " +
                " FTradeNum,FSecurityCode,FDesc as FDesc1 from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                "  ) t1 " + // 去掉FCheckState<>2 因为前台的回收站要用到
                " left join (select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType where FCheckState = 1) d1 " +
                " on t1.FTsfTypeCode = d1.FTsfTypeCode left join (select FSubTsfTypeCode,FSubTsfTypeName  from " +
                " Tb_Base_SubTransferType where FCheckState = 1) e1 on t1.FSubTsfTypeCode = e1.FSubTsfTypeCode " +
                
                " left join (select ef.*, eg.fsecurityname from " +
                " (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") + " where " +
                " FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ef " +
                " join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from " +
                pub.yssGetTableName("Tb_Para_Security") + ") eg " +
                " on ef.FSecurityCode = eg.FSecurityCode and ef.FStartDate = eg.FStartDate) g1 on t1.FSecurityCode = g1.FSecurityCode " +

                ") tsf on a.FNum=tsf.FNum1 " +
                //------ modify by wangzuochun 2011.01.27 BUG #994 资金调拨，查询时，选择显示级别为调拨子类型，查询时出错 
                " left join (select FAttrClsCode, FAttrClsName from " + pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) f1 on a.FAttrClsCode = f1.FAttrClsCode " + 
                //----------------------------------BUG #994--------------------------------//
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                //-----------------------------------------------------------------------------------------------
                " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from " +
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") + " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                "(select FPortCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 and FASSETGROUPCODE = " +
//                dbl.sqlString(pub.getAssetGroupCode()) +
//                " group by FPortCode) p " +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " ) d on a.FPortCode = d.FPortCode" +
                //-------------------------------------------------------------------------------------------------
                " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " select FCashAccCode, FCashAccName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                //edit by songjie 2011.03.15 不以最大的启用日期查询数据
                " where FCheckState = 1) e on a.FCashAccCode = e.FCashAccCode " +
                //-----------------------------------------------------------------------------------------------

                this.getCashStorageAnalysisSql() +
                buildSubFilterSql() +
                " order by a.FNum desc, a.FInOut desc ";

            return this.builderListViewData(strSql, 3);
        } catch (Exception e) {
            throw new YssException("获取资金调拨信息出错\r\n" + e.getMessage(), e);
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

    /**
     * 子调拨筛选条件
     * @return String
     */
    private String buildSubFilterSql() throws YssException {
        String sResult = " where 1=1 ";
        TransferSetBean trset = null;
        if (this.filterType != null) {
            if (this.strTansferSet.length() != 0) {
                trset = new TransferSetBean();
                trset.setYssPub(pub);
                trset.parseRowStr(this.strTansferSet);
                //edit by licai 20101216 BUG #661 资金调拨分页浏览时不能显示调拨子编号。
                if (trset.getSPortCode().length() != 0) {
                    sResult = sResult + " and a.FPortCode like '" +
                        trset.getSPortCode().replaceAll("'", "''") + "%'";
                }
                if (trset.getSCashAccCode().trim().length() != 0) {
                    sResult = sResult + " and a.FCashAccCode like '" +
                        trset.getSCashAccCode().replaceAll("'", "''") + "%'";
                }
              //edit by licai 20101216 BUG #661=================================end
            }
            if (this.filterType.dtTransferDate != null &&
                !YssFun.formatDate(this.filterType.dtTransferDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and tsf.FTransferDate = " +
                    dbl.sqlDate(filterType.dtTransferDate);
            }
            if (this.filterType.dtTransDate != null &&
                !YssFun.formatDate(this.filterType.dtTransDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and FTransDate = " +
                    dbl.sqlDate(filterType.dtTransDate);
            }

            if (this.filterType.strTsfTypeCode.length() != 0) {
                sResult = sResult + " and tsf.FTsfTypeCode like '" +
                    filterType.strTsfTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSubTsfTypeCode.length() != 0) {
                sResult = sResult + " and tsf.FSubTsfTypeCode like '" +
                    filterType.strSubTsfTypeCode.replaceAll("'", "''") +
                    "%'";
            }

            if (this.filterType.strAttrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strSecurityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.strSecurityCode.replaceAll("'", "''") + "%'";
            }
            //edit by licai 20101216 BUG #661 资金调拨分页浏览时不能显示调拨子编号。
            if (trset!=null&&trset.getIInOut() != 99) { //彭鹏 2008.03.10 BUG0000025 资金调拨列表查询问题
                sResult = sResult + " and a.FInOut = " +
                    trset.getIInOut();
            }
        }
        return sResult;
    }  
    //对成员变量赋值
    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        this.strNum = rs.getString("FNum");
        this.strTsfTypeCode = rs.getString("FTsfTypeCode");
        this.strTsfTypeName = rs.getString("FTsfTypeName");
        this.strSubTsfTypeCode = rs.getString("FSubTsfTypeCode");
        this.strSubTsfTypeName = rs.getString("FSubTsfTypeName");
        this.strAttrClsCode = rs.getString("FAttrClsCode");
        this.strAttrClsName = rs.getString("FAttrClsName");
        this.strSecurityCode = rs.getString("FSecurityCode");
        this.strSecurityName = rs.getString("FSecurityName");

        this.dtTransferDate = rs.getDate("FTransferDate");
        this.strTransferTime = rs.getString("FTransferTime");
        this.strTradeNum = rs.getString("FTradeNum");
        this.dtTransDate = rs.getDate("FTransDate");
        this.strDesc = rs.getString("FDesc");
        this.srcCashAccCode = rs.getString("FCashAccCode");
        this.srcCashAccName = rs.getString("FCashAccName");
        
        if(dbl.isFieldExist(rs, "FPortCode")){ // add by jiangshichao  Swift查看资金调拨数据不会有FPortCode字段，所以这里进行判断
        //------ add by wangzuochun 2010.02.23  MS00922  资金调拨和综合业务界面没有判断组合的权限  QDV4赢时胜上海2010年01月12日01_B  
        this.strPortCode = rs.getString("FPortCode");
        //---------------- MS00922 -----------------//
        }
        super.setRecLog(rs);
    }
    /**根据指令日期获取同一天当中的最大指令序号,拼接日期得出主见FNum的值*/
    private String getMaxNextAddOneToFNum(String commandDate) throws YssException{
    	String sRes = "";
    	ResultSet rs = null;
    	String date = YssFun.formatDate(commandDate, "yyyyMMdd");
    	try {
    		String sqlStr = "SELECT " + dbl.sqlString(date) + " || MAX(to_number(substr(FNum,9,length(FNum) - 8))) || '' AS FNum FROM " +
	            pub.yssGetTableName("tb_cash_command") +
	            " WHERE FNum LIKE " + dbl.sqlString(date+"%");
	        rs = dbl.openResultSet(sqlStr);
	        if (rs.next()) {
	            if (rs.getString("FNum") != null && rs.getString("FNum").length() > 8) {
	            	//同一天中编号最大值加1
	            	if(rs.getString("FNum").endsWith("9")){
	            		sRes = rs.getString("FNum").substring(0,rs.getString("FNum").length() - 1) + "10";
	            	}else{
	            		sRes = YssFun.toInt(rs.getString("FNum")) + 1 + "";
	            	}	            	
	            } else {
	                sRes = date + "1" ;
	            }
	        } else {
	        	sRes = date+"1";
			}
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("获取最大指令序号出错!" + "\r\n" +e.getMessage());
		} finally{
			dbl.closeResultSetFinal(rs);
		}
        return sRes;
	}
    public String getOperValue(String sType) throws YssException {
    	//story 1911 by zhouwei 20111128资金调拨 批量生成划款指令
        if(sType.equalsIgnoreCase("generateCommand")){
        	batchGenerateCommand();
        }
		return "";

    }  
  //story 1911 by zhouwei 20111128 资金调拨 批量生成划款指令
    private void batchGenerateCommand() throws YssException{
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String arrData[] = null;
        ResultSet rs=null;
        PreparedStatement ps=null;
        String checkRs="";
        boolean yssException=false;
        Map mapCashAccode=new HashMap();//保存开户行是主托管人的现金账户
        try {
        	//检查同一个资金调拨是否存在两个调拨方向相同的现金账户
        	checkRs=checkDataInout();
        	if(!checkRs.equals("")){
        		yssException=true;
        		throw new YssException(checkRs);
        	}
        	//现金账户是否已全部设置“关联收付款人”
        	checkRs=checkCashAcccode();
        	if(!checkRs.equals("")){
        		yssException=true;
        		throw new YssException(checkRs);
        	}
        	//资金调拨已生成审核的划款指令
        	checkRs=checkCommandState();
        	if(!checkRs.equals("")){
        		yssException=true;
        		throw new YssException(checkRs);
        	}
        	//获取开户行是主托管行的现金账户
        	mapCashAccode=getCashAcccodeOfPrimaryTrustBank();
        	CommandBean cb=null;
        	strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Command") +
            " (FNum,FCommandDate,FCommandTime,FAccountDate,FAccountTime,FPayerName,FOrder," +
            " FPayerBank,FPayerAccount,FPayCury,FPayMoney,FRefRate,FRecerName,FRecerBank," +
            " FRecerAccount,FRecCury,FRecMoney,FCashUsage,FDesc,FPortCode,FRelaNum,FNumType,FCheckState," +
            " FCreator,FCreateTime,FCheckUser,fzltype,fhktype, fhkremarkn,fds " +              
            " , FHKcode " +
            ",FOTHERACCOUNTDATE,FOTHERACCOUNTTIME,FOTHERPAYERNAME,FOTHERPAYERBANK,FOTHERPAYERACCOUNT"+
            ",FOTHERRECERNAME,FOTHERRECERBANK,FOTHERRECERACCOUNT,FFEXCHANGESTAT"+
            ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        	ps=conn.prepareStatement(strSql);
        	String commandDate=YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd");//获取当前系统时间      
        	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        	java.util.Date ud=sdf.parse(commandDate);
        	java.sql.Date sd=new java.sql.Date(ud.getTime());
        	//获取划款指令编号
        	String cnum = getMaxNextAddOneToFNum(commandDate);
        	int num=Integer.parseInt(cnum.substring(8));
        	String prefixDate=cnum.substring(0,8);
        	//获取指令序号
        	int order =Integer.parseInt(dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Command"), "FOrder", "1", " where FNum like '" + YssFun.left(cnum, 4) + "%'"));
            arrData = sRecycled.split("\r\n");
            bTrans = true;//开始事务
            conn.setAutoCommit(false);
            for (int i = 0; i <arrData.length; i ++ ) {
            	this.parsePerRowStr(arrData[i]);
            	//获取资金调拨数据
            	strSql="select a.FINOUT,a.FPORTCODE,a.FCASHACCCODE,a.FMONEY,a.fdesc,c.freceivercode,"
            		  +" c.FRECEIVERNAME,c.FCURYCODE,c.FOPERBANK,c.FACCOUNTNUMBER"
            		  +" from "+pub.yssGetTableName("Tb_Cash_SubTransfer")+" a,"+pub.yssGetTableName("Tb_Para_CashAccount")+" b,"
            		  +pub.yssGetTableName("tb_para_receiver")+" c"
            		  +" where a.fcashacccode=b.fcashacccode and b.frecpaycode=c.freceivercode"
            		  +" and a.fnum="+dbl.sqlString(this.strNum)+" order by a.FINOUT";
            	rs=dbl.openResultSet(strSql);
            	cb=new CommandBean();
            	//对划款指令bean赋值
            	setCommandAttr(rs, cb, mapCashAccode);
            	dbl.closeResultSetFinal(rs);
            	cnum=prefixDate+num;     
            	//转化为java.sql.date
            	java.sql.Date accountDate=new java.sql.Date (this.dtTransferDate.getTime());
            	//替换标示符
            	ps.setString(1,cnum );
            	ps.setDate(2,sd );
            	ps.setString(3,"00:00:00");
            	ps.setDate(4,accountDate);
            	ps.setString(5, "00:00:00");
            	ps.setString(6, cb.getPayName());
            	ps.setInt(7, order);
            	ps.setString(8, cb.getPayOperBank());
            	ps.setString(9, cb.getPayAccountNO());
            	ps.setString(10, cb.getPayCuryCode());
            	ps.setDouble(11, cb.getPayMoney());
            	ps.setDouble(12, 1);
            	ps.setString(13, cb.getReceiverName());
            	ps.setString(14, cb.getReOperBank());
            	ps.setString(15, cb.getReAccountNO());
            	ps.setString(16, cb.getReCuryCode());
            	ps.setDouble(17, cb.getReMoney());
            	ps.setString(18, " ");//划款用途
            	ps.setString(19, cb.getDesc()+"");
            	ps.setString(20, cb.getPortCode());
            	ps.setString(21, this.strNum);//关联编号
            	ps.setString(22, "CashTransfer");//关联类型
            	ps.setInt(23, 0);//
            	ps.setString(24, this.creatorCode);
            	ps.setString(25, this.creatorTime);
            	ps.setString(26, this.creatorCode);
            	ps.setInt(27, cb.getOrderType());
            	ps.setString(28, "");//划款类型
            	ps.setString(29, "");
            	ps.setString(30, cb.getfDS()+"");
            	ps.setString(31, " ");
            	ps.setDate(32, accountDate);
            	ps.setString(33, "00:00:00");
            	ps.setString(34, "");
            	ps.setString(35, "");
            	ps.setString(36, "");
            	ps.setString(37, "");
            	ps.setString(38, "");
            	ps.setString(39, "");
            	ps.setInt(40, 0);//外汇交收标示
            	ps.addBatch();
            	num++;
            	order++;
            	//生成之前，删除已生成过的划款指令
            	strSql="delete from "+pub.yssGetTableName("tb_cash_command")
            	+" where frelanum="+dbl.sqlString(this.strNum)
            	+" and fnumtype='CashTransfer'";
            	dbl.executeSql(strSql);
            }
            ps.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
        	if(yssException==true){
        		throw new YssException(""+e.getMessage(),e);
        	}else{
        		throw new YssException("资金调拨数据批量生成划款指令出错" + e.getMessage(), e);
        	}           
        } finally {
        	dbl.closeStatementFinal(ps);
        	dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }   
    }
  //story 1911 by zhouwei 20111229
  //对划款指令的bean赋值
   //以资金调拨中，开户银行为主托管行的现金账户的调拨方向为准。如其调拨方向为‘流出’，则取‘付款’；如为‘流入’，则取‘收款’；如存在两个主托管行现金账户时，取‘收款’。
    private void setCommandAttr(ResultSet rs,CommandBean cb,Map mapCashAccode) throws YssException{
    	try{
    		while(rs.next()){
        		String key=rs.getString("FCASHACCCODE")+"\t"+rs.getString("FPORTCODE");
            	if(mapCashAccode.containsKey(key)){//主托管行
            		if(rs.getInt("FINOUT")==1){//流入---收款0
            			cb.setOrderType(0);
            		}else{//付款
            			cb.setOrderType(1);
            		}
            	}
            	if(rs.getInt("FINOUT")==1){//流入---收款0
        			cb.setReceiverName(rs.getString("FRECEIVERNAME"));
        			cb.setReAccountNO(rs.getString("FACCOUNTNUMBER"));
        			cb.setReCuryCode(rs.getString("FCURYCODE"));
        			cb.setReOperBank(rs.getString("FOPERBANK"));
        			cb.setReMoney(rs.getDouble("FMONEY"));
        		}else{//付款
        			cb.setPayName(rs.getString("FRECEIVERNAME"));
        			cb.setPayAccountNO(rs.getString("FACCOUNTNUMBER"));
        			cb.setPayCuryCode(rs.getString("FCURYCODE"));
        			cb.setPayOperBank(rs.getString("FOPERBANK"));
        			cb.setPayMoney(rs.getDouble("FMONEY"));
        		}
            	cb.setPortCode(rs.getString("FPORTCODE"));
            	if(cb.getReceiverName()==null || cb.getReceiverName().equals("")){
            		cb.setReceiverName(" ");
            	}
            	if(cb.getReAccountNO()==null || cb.getReAccountNO().equals("")){
            		cb.setReAccountNO(" ");
            	}
            	if(cb.getReCuryCode()==null || cb.getReCuryCode().equals("")){
            		cb.setReCuryCode(" ");
            	}
            	if(cb.getReOperBank()==null || cb.getReOperBank().equals("")){
            		cb.setReOperBank(" ");
            	}
            	if(cb.getPayName()==null || cb.getPayName().equals("")){
            		cb.setPayName(" ");
            	}
            	if(cb.getPayAccountNO()==null || cb.getPayAccountNO().equals("")){
            		cb.setPayAccountNO(" ");
            	}
            	if(cb.getPayCuryCode()==null || cb.getPayCuryCode().equals("")){
            		cb.setPayCuryCode(" ");
            	}
            	if(cb.getPayOperBank()==null || cb.getPayOperBank().equals("")){
            		cb.setPayOperBank(" ");
            	}
        	}
    	}catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		}   	
    }
    //story 1911 by zhouwei 20111228
    //检查所选的资金调拨中，是否存在一个资金调拨中同时存在两个（含两个）调拨方向相同的现金账户。如存在，则生成失败，且给予提示信息
    private String checkDataInout() throws YssException{
    	StringBuffer sb=new StringBuffer();
    	String reStr="";
    	String arrData[] = null;
        ResultSet rs=null;
        String strSql = "";
        try{
        	arrData = sRecycled.split("\r\n");
            for (int i = 0; i <arrData.length; i ++ ) {
            	this.parsePerRowStr(arrData[i]);
            	strSql="select a.fnum,a.ftsftypecode,a.fsubtsftypecode,a.ftransdate,a.ftransferdate"
            		  +" from "+pub.yssGetTableName("Tb_Cash_Transfer")+" a,"+pub.yssGetTableName("Tb_Cash_SubTransfer")+" b"
            		  +" where a.fnum=b.fnum and a.fnum="+dbl.sqlString(this.strNum)+"having count(b.finout)>1"
            		  +" group by a.fnum,a.ftsftypecode,a.fsubtsftypecode,a.ftransdate,a.ftransferdate,b.finout"
            		  +" order by a.fnum";       
            	rs=dbl.openResultSet(strSql);
            	if(rs.next()){
            		if(sb.length()==0){
            			sb.append("调拨编号\t调拨日期\t业务日期\t调拨类型   调拨子类型\r\n");
            		}
            		sb.append(this.strNum).append("\t").append(YssFun.formatDate(rs.getString("ftransferdate"),"yyyy-MM-dd")).append("\t")
            		  .append(YssFun.formatDate(rs.getString("ftransdate"),"yyyy-MM-dd")).append("\t").append(rs.getString("ftsftypecode"))
            		  .append("\t\t").append(rs.getString("fsubtsftypecode")).append("\r\n");
            	}
            	dbl.closeResultSetFinal(rs);         	
            }
            if(sb.toString().length()>2){
        		reStr="以下资金调拨中存在多个调拨方向相同的现金账户，不能生成划款指令！~@~"+sb.toString().substring(0, sb.toString().length()-2);
        	}
        }catch (Exception e) {
			throw new YssException("检查资金调拨子表数据的调拨方向出错"+e.getMessage(), e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return reStr;
    }
    
    //story 1911 by zhouwei 20111228
    //检查所选的资金调拨中，所涉及的现金账户是否已全部设置“关联收付款人”
    private String checkCashAcccode() throws YssException{
    	StringBuffer sb=new StringBuffer();
    	String reStr="";
    	String arrData[] = null;
        ResultSet rs=null;
        String strSql = "";
        Map mapCashAcccode=new HashMap();
        try{
        	arrData = sRecycled.split("\r\n");
            for (int i = 0; i <arrData.length; i ++ ) {
            	this.parsePerRowStr(arrData[i]);
            	strSql="select c.fcashacccode,c.fcashaccname from  (select b.fcashacccode,b.fcashaccname,b.frecpaycode"
            		  +" from  "+pub.yssGetTableName("Tb_Cash_SubTransfer")+" a,"+pub.yssGetTableName("Tb_Para_CashAccount")+" b where"
            		  +" a.fcashacccode=b.fcashacccode and b.fcheckstate=1 and a.fnum="+dbl.sqlString(this.strNum)
            		  +" group by b.fcashacccode,b.fcashaccname,b.frecpaycode) c"
            		  +" left join (select * from "+pub.yssGetTableName("tb_para_receiver")+" where fcheckstate=1) d"
            		  +" on c.frecpaycode=d.freceivercode  where d.freceivercode is null";       
            	rs=dbl.openResultSet(strSql);
            	if(rs.next()){
            		if(!mapCashAcccode.containsKey(rs.getString("fcashacccode"))){
            			mapCashAcccode.put(rs.getString("fcashacccode"), rs.getString("fcashacccode"));
            		}else{
            			continue;
            		}
            		sb.append(rs.getString("fcashacccode")).append("\t")
            		  .append(rs.getString("fcashaccname")).append("\r\n");
            	}
            	dbl.closeResultSetFinal(rs);         	
            }
            if(sb.toString().length()>2){
        		reStr="以下现金账户未关联收付款人，请设置后再生成划款指令！~@~"+sb.toString().substring(0, sb.toString().length()-2);
        	}
        }catch (Exception e) {
			throw new YssException("检查资金调拨现金账户的收付款人出错"+e.getMessage(), e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return reStr;
    }
    //story 1911 by zhouwei 20111228
    //检查所选的资金调拨中是否已生成过划款指令，并且该划款指令已审核
    private String checkCommandState() throws YssException{
    	StringBuffer sb=new StringBuffer();
    	String reStr="";
    	String arrData[] = null;
        ResultSet rs=null;
        String strSql = "";
        try{
        	arrData = sRecycled.split("\r\n");
            for (int i = 0; i <arrData.length; i ++ ) {
            	this.parsePerRowStr(arrData[i]);
            	strSql="select a.fnum as tnum,a.ftsftypecode,a.fsubtsftypecode,a.ftransdate,a.ftransferdate,b.FCOMMANDDATE,b.FNUM as cnum,b.FORDER"
            		  +" from "+pub.yssGetTableName("Tb_Cash_Transfer")+" a,"+pub.yssGetTableName("tb_cash_command")+" b"
            		  +" where a.fnum=b.FRELANUM and b.FNUMTYPE='CashTransfer' and b.fcheckstate=1 and a.fnum="+dbl.sqlString(this.strNum);       
            	rs=dbl.openResultSet(strSql);
            	if(rs.next()){
            		if(sb.length()==0){
            			sb.append("调拨编号\t调拨日期\t业务日期\t指令序号   指令日期\r\n");
            		}
            		sb.append(this.strNum).append("\t").append(YssFun.formatDate(rs.getString("ftransferdate"),"yyyy-MM-dd")).append("\t")
            		  .append(YssFun.formatDate(rs.getString("ftransdate"),"yyyy-MM-dd")).append("\t").append(rs.getString("FORDER"))
            		  .append("\t  ").append(YssFun.formatDate(rs.getString("FCOMMANDDATE"),"yyyy-MM-dd")).append("\r\n");
            	}
            	dbl.closeResultSetFinal(rs);         	
            }
            if(sb.toString().length()>2){
        		reStr="以下资金调拨中生成的划款指令为审核状态，不可重新生成！~@~"+sb.toString().substring(0, sb.toString().length()-2);
        	}
        }catch (Exception e) {
			throw new YssException("检查资金调拨中生成的划款指令出错"+e.getMessage(), e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return reStr;
    }
   //story 1911 by zhouwei 20111228
   //获取开户行是主托管行的现金账户
    private Map getCashAcccodeOfPrimaryTrustBank() throws YssException{
    	Map mapCashAccode=new HashMap();
        ResultSet rs=null;
        String strSql = "";
        try{
        	strSql="select g1.fcashacccode,g1.FPORTCODE"
        		  +" from "+pub.yssGetTableName("Tb_Para_CashAccount")+" g1 ,"+pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
        		  +" g2,"+pub.yssGetTableName("Tb_Para_Trustee")+" g3"
        		  +" where g1.FPORTCODE=g2.fportcode and g1.FBANKCODE=g3.FTrusteeCode "
        		  +" and g2.FSubCode=g3.ftrusteecode"
        		  +" and g2.FRelaGrade='primary' and g2.FRelaType='Trustee'"
        		  +" and g1.fcheckstate=1 and g2.fcheckstate=1 and g3.fcheckstate=1";       
        	rs=dbl.openResultSet(strSql);
        	while(rs.next()){
        		String key=rs.getString("fcashacccode")+"\t"+rs.getString("FPORTCODE");
        		if(!mapCashAccode.containsKey(key)){
        			mapCashAccode.put(key, rs.getString("fcashacccode"));
        		}
        	}   	       
        }catch (Exception e) {
			throw new YssException("获取开户行是主托管行的现金账户出错"+e.getMessage(), e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return mapCashAccode;
    }
    /**
     * 筛选条件 story 1911 by zhouwei 20111228 
     * 隐含条件：资金调拨中，至少有一个现金账户的“开户银行”为主托管行。
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
            sResult = " where 1=1 ";
            if (this.filterType.strIsOnlyColumns.equals("1")&&pub.isBrown()==false) {	//20111027 modified by liubo.STORY #1285. 
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.srcCashAccCode.trim().length() != 0) {
                sResult = sResult + " and a.FSrcCashAcc like '" +
                    this.filterType.srcCashAccCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.dtTransferDate != null &&
                !YssFun.formatDate(this.filterType.dtTransferDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and a.FTransferDate = " +
                    dbl.sqlDate(filterType.dtTransferDate);
            }
            if (this.filterType.dtTransDate != null &&
                !YssFun.formatDate(this.filterType.dtTransDate).equals(
                    "9998-12-31")) {
                sResult = sResult + " and a.FTransDate = " +
                    dbl.sqlDate(filterType.dtTransDate);
            }

            if (this.filterType.strTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FTsfTypeCode like '" +
                    filterType.strTsfTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSubTsfTypeCode.length() != 0) {
                sResult = sResult + " and a.FSubTsfTypeCode like '" +
                    filterType.strSubTsfTypeCode.replaceAll("'", "''") +
                    "%'";
            }

            if (this.filterType.strAttrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.strAttrClsCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strSecurityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.strSecurityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strNum.trim().length() != 0) { //若用到资金调拨主表的编号集里用 by ly 080203
                if (filterType.strNum.indexOf(",") > 0) {
                    sResult += " and a.FNum in(" + operSql.sqlCodes(filterType.strNum) + ") ";
                } else {
                    sResult += " and a.FNum like'" + filterType.strNum.replaceAll("'", "''") + "%' ";
                }
            }

            if (this.strTansferSet.length() != 0) {
                //获取资金调拨子表的主编号
                TransferSetBean tfSetBean = new TransferSetBean();
                tfSetBean.setYssPub(pub);
                tfSetBean.parseRowStr(this.strTansferSet);
                sResult = sResult + " and a.FNum in (select distinct FNum from " +      
                    pub.yssGetTableName("Tb_Cash_SubTransfer")+" g join (select g1.fcashacccode,g1.FPORTCODE from "+pub.yssGetTableName("Tb_Para_CashAccount")
                    +" g1 ,"+pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")+" g2,"+pub.yssGetTableName("Tb_Para_Trustee")+" g3" 
                    +" where g1.FPORTCODE=g2.fportcode and g1.FBANKCODE=g3.FTrusteeCode and g2.FSubCode=g3.ftrusteecode"
                    +" and g2.FRelaGrade='primary' and g2.FRelaType='Trustee') g4 on g.FPORTCODE=g4.fportcode and g.FCashAccCode=g4.fcashacccode"
                    +" where 1=1";
                if (tfSetBean.getSPortCode().trim().length() > 0) {
                    sResult = sResult + " and g.FPortCode = " +
                        dbl.sqlString(tfSetBean.getSPortCode().trim());
                }
                if (tfSetBean.getSCashAccCode().trim().length() > 0) {
                    sResult = sResult + " and g.FCashAccCode = " +
                        dbl.sqlString(tfSetBean.getSCashAccCode().trim());
                }
                if (tfSetBean.getIInOut() != 99) {
                    sResult = sResult + " and g.FInOut = " + tfSetBean.getIInOut();
                }
                if (tfSetBean.getSAnalysisCode1().trim().length() > 0) {
                    sResult = sResult + " and g.FAnalysisCode1 = " +
                        dbl.sqlString(tfSetBean.getSAnalysisCode1().trim());
                }
                if (tfSetBean.getSAnalysisCode2().trim().length() > 0) {
                    sResult = sResult + " and g.FAnalysisCode2 = " +
                        dbl.sqlString(tfSetBean.getSAnalysisCode2().trim());
                }
                sResult = sResult + " ) ";
            }

        }
        return sResult;
    }
    private String getCashStorageAnalysisSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
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
                        " left join (select FExchangeCode,FExchangeName as FAnalysisName" +
                        i +
                        " from tb_base_exchange) e on a.FAnalysisCode" + i +
                        " = e.FExchangeCode " +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
                        
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " n where  n.FCheckState = 1 ) exchange on a.FAnalysisCode" +
                        i + " = exchange.FInvMgrCode";
                    
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("004")) {
                    sResult = sResult +
                        //------ modify by wangzuochun 2010.12.02 BUG #539 资金调拨，选择按照调拨子编号查询时，会提示报错 
                        " left join (select FCatCode,FCatName as FAnalysisName" + i + " from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
                        i + " = category.FCatCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
             
                    
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i +
                    "  from  " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where  n.FCheckState = 1 ) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                
                    // end by lidaolong
                }

                else {
                	//------ 改为select空格，否则生成分页table的时候报错  modify by wangzuochun 2010.12.02 BUG #539 资金调拨，选择按照调拨子编号查询时，会提示报错 
                    sResult = sResult +
                        " left join (select ' ' as FAnalysisNull , ' ' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                    //-----------------------------BUG #539---------------------------//
                }
            }
        }
        dbl.closeResultSetFinal(rs);//QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-07-09 关闭结果集
        return sResult;
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
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        TransToCommandBean beforeEditBean = new TransToCommandBean();
        //--------//MS00226 QDV4华宝兴业2009年2月4日01_B ---------
        TransferSetBean logTransferset = new TransferSetBean();
        TransferSetBean filter = new TransferSetBean();
        String transferLogInfo = "";
        //------------------------------------------------------
        try {
            strSql = "select y.* from " +
                "(select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FCheckState <> 2 group by FNum,FCheckState) x join" +

                "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName as FtsfTypeName," +
                "e.FSubTsfTypeName as FsubtsfTypeName,f.FAttrClsName as FattrClsName,g.FSecurityName as FSecurityName " +
                " from " + pub.yssGetTableName("Tb_Cash_Transfer") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FTsfTypeCode,FTsfTypeName from  Tb_Base_TransferType where FCheckState = 1) d on a.FTsfTypeCode = d.FTsfTypeCode" +
                " left join (select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType where FCheckState = 1) e on a.FSubTsfTypeCode = e.FSubTsfTypeCode" +

                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) f on a.FAttrClsCode= f.FAttrClsCode" +

                " left join (select ef.*, eg.fsecurityname from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ef join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eg on ef.FSecurityCode = eg.FSecurityCode and ef.FStartDate = eg.FStartDate) g on a.FSecurityCode = g.FSecurityCode " +
                //       buildFilterSql() +
                " where FNum=" + dbl.sqlString(this.strOldNum) +
                ") y on x.FNum = y.FNum " +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strNum = rs.getString("FNum");
                //-------------------------------------------------------------------------
                beforeEditBean.strNum = rs.getString("FNum"); //MS00226 QDV4华宝兴业2009年2月4日01_B
                //-------------------------------------------------------------------------
                beforeEditBean.strTsfTypeCode = rs.getString("FTsfTypeCode");
                beforeEditBean.strTsfTypeName = rs.getString("FTsfTypeName");
                beforeEditBean.strSubTsfTypeCode = rs.getString("FSubTsfTypeCode");
                beforeEditBean.strSubTsfTypeName = rs.getString("FSubTsfTypeName");
                beforeEditBean.strAttrClsCode = rs.getString("FAttrClsCode");
                beforeEditBean.strAttrClsName = rs.getString("FAttrClsName");
                beforeEditBean.strSecurityCode = rs.getString("FSecurityCode");
                beforeEditBean.strSecurityName = rs.getString("FSecurityName");

                beforeEditBean.dtTransferDate = rs.getDate("FTransferDate");
                beforeEditBean.strTransferTime = rs.getString("FTransferTime");
                beforeEditBean.strTradeNum = rs.getString("FTradeNum");
                beforeEditBean.dtTransDate = rs.getDate("FTransDate");
                beforeEditBean.strDesc = rs.getString("FDesc");

            }
            //MS00226 QDV4华宝兴业2009年2月4日01_B -----------------------------------------------------------------------------
            logTransferset.setYssPub(pub);
            logTransferset.setSNum(beforeEditBean.strNum);
            filter.setSNum(beforeEditBean.strNum); //设置资金调拨的编号
            logTransferset.setFilterType(filter);
            transferLogInfo = logTransferset.getListViewData1(); //获取资金子调拨的数据
            if (transferLogInfo.indexOf("\f\f") > 0) {
                transferLogInfo = transferLogInfo.replaceAll("\f\f", "\bsubset\b"); //将不同的资金子调拨数据的分割符进行转换，避免在查询日志时出现错误的解析。
            }
            return beforeEditBean.buildRowStr() + "\f\b\f\b\f\b" + transferLogInfo;
            //-----------------------------------------------------------------------------------------------------------------
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {}

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
