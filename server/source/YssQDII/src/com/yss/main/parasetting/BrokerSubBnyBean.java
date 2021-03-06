package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class BrokerSubBnyBean
    extends BaseDataSettingBean implements IDataSetting {
    public BrokerSubBnyBean() {
    }

    private String strBrokerCode = ""; //券商代码
    private String strBrokerName = ""; //券商代码
    private String strExchangeCode = ""; //交易所代码
    private String strExchangeName = ""; //交易所代码
    private String strBrokerIDType = "";
    private String strBrokerID = "";
    private String strIFBrokerName = ""; //BugNo:0000363 edit by jc
    private String strClearerIDType = "";
    private String strClearerID = "";
    private String strClearerName = ""; //BugNo:0000363 edit by jc
    private String strClearerDesc = ""; //BugNo:0000363 edit by jc
    private String strBrokerAccount = "";
    private String strDesc = "";

    private String strTradeCatCode = ""; //品种代码
    private String strTradeCatName = ""; //品种名称
    //----#2415 add by wuweiqi 20110128 添加品种子类型----------------//
    private String strTradeSubCatCode="";//品种子类型代码
	private String strTradeSubCatName="";//品种子类型名称]
	private String strOldTradeSubCatCode="";
    //-------------------end by wuweiqi 20110128 -------------------//


	private String strPlaceSettlement = ""; //结算地点

    private String strClearAccount = ""; //清算帐户

    private String strOldTradeCatCode = "";

//    public int checkStateId;	//findbugs风险调整，与父类定义了相同的变量  hukun 20120625
    private BrokerSubBnyBean filterType;

    private String strOldBrokerCode;
    private String strOldExchangeCode;
    public String getStrBrokerCode() {
        return strBrokerCode;
    }

    public String getStrBrokerID() {
        return strBrokerID;
    }

    public String getStrBrokerIDType() {
        return strBrokerIDType;
    }

    public String getStrClearerID() {
        return strClearerID;
    }

    public String getStrClearerIDType() {
        return strClearerIDType;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrExchangeCode() {
        return strExchangeCode;
    }

    public String getStrOldBrokerCode() {
        return strOldBrokerCode;
    }

    public String getStrOldExchangeCode() {
        return strOldExchangeCode;
    }

    public void setStrBrokerCode(String strBrokerCode) {
        this.strBrokerCode = strBrokerCode;
    }

    public void setStrBrokerID(String strBrokerID) {
        this.strBrokerID = strBrokerID;
    }

    public void setStrBrokerIDType(String strBrokerIDType) {
        this.strBrokerIDType = strBrokerIDType;
    }

    public void setStrClearerID(String strClearerID) {
        this.strClearerID = strClearerID;
    }

    public void setStrClearerIDType(String strClearerIDType) {
        this.strClearerIDType = strClearerIDType;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrExchangeCode(String strExchangeCode) {
        this.strExchangeCode = strExchangeCode;
    }

    public void setStrOldBrokerCode(String strOldBrokerCode) {
        this.strOldBrokerCode = strOldBrokerCode;
    }

    public void setStrOldExchangeCode(String strOldExchangeCode) {
        this.strOldExchangeCode = strOldExchangeCode;
    }
    //----#2415 add by wuweiqi 20110128 添加品种子类型----------------//
    public String getStrTradeSubCatCode() {
		return strTradeSubCatCode;
	}

	public void setStrTradeSubCatCode(String strTradeSubCatCode) {
		this.strTradeSubCatCode = strTradeSubCatCode;
	}

	public String getStrTradeSubCatName() {
		return strTradeSubCatName;
	}

	public void setStrTradeSubCatName(String strTradeSubCatName) {
		this.strTradeSubCatName = strTradeSubCatName;
	}

    public String getStrOldTradeSubCatCode() {
		return strOldTradeSubCatCode;
	}

	public void setStrOldTradeSubCatCode(String strOldTradeSubCatCode) {
		this.strOldTradeSubCatCode = strOldTradeSubCatCode;
	}
	//-------------------end by wuweiqi 20110128 ---------=---------//
    //BugNo:0000363 edit by jc
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
            reqAry = sTmpStr.split("\t");

            this.strBrokerCode = reqAry[0];
            this.strBrokerName = reqAry[1];
            this.strExchangeCode = reqAry[2];
            this.strExchangeName = reqAry[3];
            this.strBrokerIDType = reqAry[4];
            this.strBrokerID = reqAry[5];
            this.strIFBrokerName = reqAry[6];
            this.strClearerIDType = reqAry[7];
            this.strClearerID = reqAry[8];
            this.strClearerName = reqAry[9];
            this.strClearerDesc = reqAry[10];
            this.strBrokerAccount = reqAry[11];
            this.strDesc = reqAry[12];
            this.checkStateId = Integer.parseInt(reqAry[13]);
            this.strOldBrokerCode = reqAry[14];
            this.strOldExchangeCode = reqAry[15];
            this.strTradeCatCode = reqAry[16];
            this.strTradeCatName = reqAry[17];

            this.strPlaceSettlement = reqAry[18];

            this.strClearAccount = reqAry[19];
            //-----#2415 add by wuweiqi 20110128 添加品种子类型----------------//
            this.strTradeSubCatCode = reqAry[20];
            this.strTradeSubCatName = reqAry[21];
            this.strOldTradeSubCatCode=reqAry[22];
            //-------------------end by wuweiqi 20110128 ---------------------//
            this.strOldTradeCatCode = reqAry[23];
    
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new BrokerSubBnyBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析券商纽约银行信息出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.strBrokerCode).append("\t");
        buffer.append(this.strBrokerName).append("\t");
        buffer.append(this.strExchangeCode).append("\t");
        buffer.append(this.strExchangeName).append("\t");
        buffer.append(this.strBrokerIDType).append("\t");
        buffer.append(this.strBrokerID).append("\t");
        buffer.append(this.strIFBrokerName).append("\t"); //BugNo:0000363 edit by jc
        buffer.append(this.strClearerIDType).append("\t");
        buffer.append(this.strClearerID).append("\t");
        buffer.append(this.strClearerName).append("\t"); //BugNo:0000363 edit by jc
        buffer.append(this.strClearerDesc).append("\t"); //BugNo:0000363 edit by jc
        buffer.append(this.strBrokerAccount).append("\t");
        buffer.append(this.strDesc).append("\t");

        buffer.append(this.strTradeCatCode).append("\t");
        buffer.append(this.strTradeCatName).append("\t");
        buffer.append(this.strPlaceSettlement).append("\t");
        buffer.append(this.strClearAccount).append("\t");
        //----#2415 add by wuweiqi 20110128 添加品种子类型--------------//
        buffer.append(this.strTradeSubCatCode).append("\t");
        buffer.append(this.strTradeSubCatName).append("\t");
        //-------------------end by wuweiqi 20110128 ------------------//

        buffer.append(super.buildRecLog());
        return buffer.toString();

    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.filterType.strBrokerCode.length() == 0 &&
                    this.filterType.strExchangeCode.length() == 0 && 
                    this.filterType.strTradeCatCode.length()==0 && 
                    this.filterType.strTradeSubCatCode.length()==0) {
                    sResult = sResult + " and 1=2 ";
                }

                if (this.filterType.strBrokerCode.length() != 0) {
                	//edit by songjie 2011.03.24 BUG:1480 QDV4华泰证券2011年3月16日01_B 
                    /**shashijie 2013-1-24 BUG 6985 两个券商的代码的前面是匹配的，然后在查看券商CS的关联信息时，会将券商CCBIS的关联信息也显示出来
                     * 这里取消模糊查询的'%'号*/
                	sResult = sResult + " and a.FBrokerCode like '" +
                    filterType.strBrokerCode.replaceAll("'", "''") + "'";
					/**end shashijie 2013-1-24 BUG 6985 */
                	
                }
                if (this.filterType.strExchangeCode.length() != 0) {
                	//edit by songjie 2011.03.24 BUG:1480 QDV4华泰证券2011年3月16日01_B 
                    sResult = sResult + " and a.FExchangeCode like '" +
                        filterType.strExchangeCode.replaceAll("'", "''") + "%'";
                }
                if(this.filterType.strTradeCatCode.length()!=0){
                	//edit by songjie 2011.03.24 BUG:1480 QDV4华泰证券2011年3月16日01_B 
                	sResult = sResult + " and a.FTRADECATCODE like '" +
                    filterType.strTradeCatCode.replaceAll("'", "''") + "%'";
                }
                if(this.filterType.strTradeSubCatCode.length()!=0){
                	//edit by songjie 2011.03.24 BUG:1480 QDV4华泰证券2011年3月16日01_B 
                	sResult = sResult + " and a.FTRADESUBCATCODE like '" +
                    filterType.strTradeSubCatCode.replaceAll("'", "''") + "%'";
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选费用链接设置数据出错", e);
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
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();
            // strSql="select * from  "+pub.yssGetTableName("TB_PARA_BROKERSUBBNY") +buildFilterSql();
            strSql =
                " select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FBROKERNAME as FBROKERNAME,e.FEXCHANGENAME as FExchangeName" +
                ", f.FCatName as FCatName ,h.FSubCatName as FSubCatName from " +
                pub.yssGetTableName("TB_PARA_BROKERSUBBNY") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (" +
                "select y.* from " +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                "(select FBrokerCode,FCheckState, max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Broker") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState <> 2 group by FBrokerCode,FCheckState) x join" +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                " (select FBrokerCode,FBrokerName, FCheckState,FCreateTime ,FStartDate from " +
                pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState <> 2 " +
                " ) y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                " order by y.FCheckState, y.FCreateTime desc" +
                " ) d on a.FBrokerCode = d.FBROKERCODE" +
                " left join (select FEXCHANGECODE, FEXCHANGENAME from TB_BASE_EXCHANGE) e on a.FExchangeCode=e.FEXCHANGECODE" +
                " left join (select FCatCode,FCatName from Tb_Base_Category)f on a.FTradeCatCode=f.FCatCode " +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory)h on a.FTradeSubCatCode=h.FSubCatCode "+//#2415 add by wuweiqi 20110128 
                buildFilterSql();

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setBrokerSubBnyAttr(rs);
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
            throw new YssException("获取纽约银行信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public void setBrokerSubBnyAttr(ResultSet rs) throws YssException {

        try {
            this.strBrokerCode = rs.getString("FBrokerCode");
            this.strExchangeCode = rs.getString("FExchangeCode");
            this.strBrokerName = rs.getString("FBrokerName");
            this.strExchangeName = rs.getString("FExchangeName");
            this.strBrokerIDType = rs.getString("FBrokerIDType");
            this.strBrokerID = rs.getString("FBrokerID");
            this.strIFBrokerName = rs.getString("FIFBROKERNAME"); //BugNo:0000363 edit by jc
            this.strClearerIDType = rs.getString("FClearerIDType");
            this.strClearerID = rs.getString("FClearerID");
            this.strClearerName = rs.getString("FClearerName"); //BugNo:0000363 edit by jc
            this.strClearerDesc = rs.getString("FClearerDesc"); //BugNo:0000363 edit by jc
            this.strBrokerAccount = rs.getString("FBrokerAccount");
            this.strDesc = rs.getString("FDesc");
            this.strTradeCatCode = rs.getString("FTradeCatCode");
            this.strTradeCatName = rs.getString("FCatName");
            this.strTradeSubCatCode=rs.getString("FTradeSubCatCode");//#2415 add by wuweiqi 20110128 添加品种子类型代码
            this.strTradeSubCatName=rs.getString("FSubCatName");//#2415 add by wuweiqi 20110128 添加品种子类型代码
            this.strPlaceSettlement = rs.getString("FPlaceOfSettlement");
            this.strClearAccount = rs.getString("FClearAccount");

            super.setRecLog(rs);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
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

    public String getListViewData3() {
        return "";
    }

    public String getListViewData4() {
        return "";
    }

//新增
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务

        try {
            strSql = "insert into " + pub.yssGetTableName("TB_PARA_BROKERSUBBNY") +
                "(FBrokerCode,FExchangeCode,FBrokerIDType,FBrokerID,FIFBrokerName,FClearerIDType,FClearerID,FClearerName,FClearerDesc,FBrokerAccount,FDesc,FTradeCatCode,FPlaceOfSettlement,FClearAccount," +
                "FCheckState,FCreator,FCreateTime,FCheckUser,FTradeSubCatCode)" +
                " values(" +

                dbl.sqlString(this.strBrokerCode) + "," +
                dbl.sqlString(this.strExchangeCode) + "," +
                dbl.sqlString(this.strBrokerIDType) + "," +
                dbl.sqlString(this.strBrokerID) + "," +
                dbl.sqlString(this.strIFBrokerName) + "," + //BugNo:0000363 edit by jc
                dbl.sqlString(this.strClearerIDType) + "," +
                dbl.sqlString(this.strClearerID) + "," +
                dbl.sqlString(this.strClearerName) + "," + //BugNo:0000363 edit by jc
                dbl.sqlString(this.strClearerDesc) + "," + //BugNo:0000363 edit by jc
                dbl.sqlString(this.strBrokerAccount) + "," +
                dbl.sqlString(this.strTradeCatCode) + "," +
                dbl.sqlString(this.strPlaceSettlement) + "," +
                dbl.sqlString(this.strClearAccount) + "," +

                (pub.getSysCheckState() ? 0 : 1) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString( (pub.getSysCheckState() ? " " : this.creatorCode)) + "," +
                dbl.sqlString(this.strTradeSubCatCode)+//#2415 add by wuweiqi 20110128 添加品种子类型代码
                ")";
            //System.out.print(strSql);
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增券商纽约银行信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";

    }

    public void checkInput(byte btOper) throws YssException {

        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_BrokerSubBny"), 
                               "FBROKERCODE,FEXCHANGECODE,FTRADECATCODE,FTRADESUBCATCODE",// #2415::add by wuweiqi 20110221  券商添加品种子类型
                               this.strBrokerCode + "," + this.strExchangeCode +"," +this.strTradeCatCode+","+ this.strTradeSubCatCode
                               ,
                               this.strOldBrokerCode + "," +
                               this.strOldExchangeCode+"," +
                               this.strOldTradeCatCode+"," +
                               this.strOldTradeSubCatCode
            );

    }

    public void checkSetting() {
    }

//删除
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("TB_PARA_BROKERSUBBNY") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) :
                 "' '") +
                " where FBrokerCode=" + dbl.sqlString(this.strOldBrokerCode) +
                " and FExchangeCode =" + dbl.sqlString(this.strExchangeCode) +
                " and FTradeCatCode= " +
                dbl.sqlString(this.strTradeCatCode.length() == 0 ? " " :
                              this.strTradeCatCode)+
                " and FTradeSubCatCode= " +
                dbl.sqlString(this.strTradeSubCatCode.length() == 0 ? " " ://#2415 add by wuweiqi 20110128 添加品种子类型代码
                              this.strTradeSubCatCode);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除券商纽约银行信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

//修改
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = " update " + pub.yssGetTableName("TB_PARA_BROKERSUBBNY") +
                " set FBrokerCode=" + dbl.sqlString(this.strBrokerCode) + "," +
                " FExchangeCode=" + dbl.sqlString(this.strExchangeCode) + "," +
                " FBrokerIDType=" + dbl.sqlString(this.strBrokerIDType) + "," +
                " FBrokerID =" + dbl.sqlString(this.strBrokerID) + "," +
                " FIFBrokerName =" + dbl.sqlString(this.strIFBrokerName) + "," + //BugNo:0000363 edit by jc
                " FClearerIDType=" + dbl.sqlString(this.strClearerIDType) + "," +
                " FClearerID=" + dbl.sqlString(this.strClearerID) + "," +
                " FClearerName =" + dbl.sqlString(this.strClearerName) + "," + //BugNo:0000363 edit by jc
                " FClearerDesc =" + dbl.sqlString(this.strClearerDesc) + "," + //BugNo:0000363 edit by jc
                " FBrokerAccount=" + dbl.sqlString(this.strBrokerAccount) + "," +
                " FTradeCatCode=" + dbl.sqlString(this.strTradeCatCode) + "," +
                " FTradeSubCatCode" + dbl.sqlString(this.strTradeSubCatCode) + "," + //#2415 add by wuweiqi 20110128 添加品种子类型代码
                " FPlaceOfSettlement=" + dbl.sqlString(this.strPlaceSettlement) +
                "," +
                " FClearAccount=" + dbl.sqlString(this.strClearAccount) + "," +
                " FDesc =" + dbl.sqlString(this.strDesc) + "," +
                " FCheckState=" + this.checkStateId + "," +
                " FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) :
                 "' '") +
                " where FBrokerCode=" + dbl.sqlString(this.strOldBrokerCode) +
                " and FExchangeCode =" + dbl.sqlString(this.strOldExchangeCode) +
                " and FTradeCatCode= " +
                dbl.sqlString(this.strOldTradeCatCode.length() == 0 ? " " :
                              this.strOldTradeCatCode)+
                " and FTradeSubCatCode= " +
                dbl.sqlString(this.strOldTradeSubCatCode.length() == 0 ? " " ://#2415 add by wuweiqi 20110128 添加品种子类型代码
                              this.strOldTradeSubCatCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改券商纽约银行信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public String getAllSetting() {
        return "";
    }
    
    /**
     * add by wangzuochun 2011.03.31 BUG #1574 纽约银行网银数据接口导出处理 
     * 找到最匹配的券商关联表数据
     * @throws YssException
     */
    public void setLinkedBroker() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        
        try {
            strSql = " select * from " +
                pub.yssGetTableName("tb_para_brokersubbny") +
                " where FBROKERCODE = " +
                dbl.sqlString(this.strBrokerCode) + " and fexchangecode = " +
                dbl.sqlString(this.strExchangeCode)+
                " and  FTRADECATCODE = " +
                dbl.sqlString(this.strTradeCatCode)+
                " and  FTRADESUBCATCODE = "+
                dbl.sqlString(this.strTradeSubCatCode) + " and FCheckState = 1";

            rs = dbl.openResultSet(strSql);
            
            if (rs.next()){
            	setBrokerBnyBean(rs);
            }
            else {
            	dbl.closeResultSetFinal(rs);
            	
            	strSql = " select * from " +
	                pub.yssGetTableName("tb_para_brokersubbny") +
	                " where FCheckState = 1 and FBROKERCODE = " +
	                dbl.sqlString(this.strBrokerCode) + " and fexchangecode = " +
	                dbl.sqlString(this.strExchangeCode)+
	                " and  FTRADECATCODE = " +
	                dbl.sqlString(this.strTradeCatCode) + " and (FTRADESUBCATCODE is null or FTRADESUBCATCODE = ' ')";
            	
            	rs = dbl.openResultSet(strSql);
            	
            	if (rs.next()){
            		setBrokerBnyBean(rs);
                }
            	else {
            		dbl.closeResultSetFinal(rs);
                	
                	strSql = " select * from " +
    	                pub.yssGetTableName("tb_para_brokersubbny") +
    	                " where FCheckState = 1 and FBROKERCODE = " +
    	                dbl.sqlString(this.strBrokerCode) + " and fexchangecode = " +
    	                dbl.sqlString(this.strExchangeCode);
                	
                	rs = dbl.openResultSet(strSql);
            		
                	if (rs.next()){
                		setBrokerBnyBean(rs);
                	}
            	}
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * add by wangzuochun 2011.03.31 BUG #1574 纽约银行网银数据接口导出处理 
     * @param rs
     * @throws YssException
     */
    public void setBrokerBnyBean(ResultSet rs) throws YssException {
    	try {
    	
    		this.strBrokerIDType = rs.getString("FBrokerIDType");
	        if (this.strBrokerIDType == null ||
	            this.strBrokerIDType.equalsIgnoreCase("null")) {
	            this.strBrokerIDType = "";
	        }
	        this.strBrokerID = rs.getString("FBrokerID");
	        if (this.strBrokerID == null ||
	            this.strBrokerID.equalsIgnoreCase("null")) {
	            this.strBrokerID = "";
	        }
	        this.strIFBrokerName = rs.getString("FIFBrokerName");
	        if (this.strIFBrokerName == null ||
	            this.strIFBrokerName.equalsIgnoreCase("null")) {
	            this.strIFBrokerName = "";
	        }
	        this.strClearerIDType = rs.getString("FClearerIDType");
	        if (this.strClearerIDType == null ||
	            this.strClearerIDType.equalsIgnoreCase("null")) {
	            this.strClearerIDType = "";
	        }
	        this.strClearerID = rs.getString("FClearerID");
	        if (this.strClearerID == null ||
	            this.strClearerID.equalsIgnoreCase("null")) {
	            this.strClearerID = "";
	        }
	        this.strClearerName = rs.getString("FClearerName");
	        if (this.strClearerName == null ||
	            this.strClearerName.equalsIgnoreCase("null")) {
	            this.strClearerName = "";
	        }
	        this.strClearerDesc = rs.getString("FClearerDesc");
	        if (this.strClearerDesc == null ||
	            this.strClearerDesc.equalsIgnoreCase("null")) {
	            this.strClearerDesc = "";
	        }
	        this.strBrokerAccount = rs.getString("FBrokerAccount");
	        if (this.strBrokerAccount == null ||
	            this.strBrokerAccount.equalsIgnoreCase("null")) {
	            this.strBrokerAccount = "";
	        }
	        this.strPlaceSettlement = rs.getString("FPlaceOfSettlement");
	        if (this.strPlaceSettlement == null ||
	            this.strClearAccount.equalsIgnoreCase("null")) {
	            this.strPlaceSettlement = "";
	        }
	        this.strClearAccount = rs.getString("FClearAccount");
	        if (this.strClearAccount == null ||
	            this.strClearAccount.equalsIgnoreCase("null")) {
	            this.strClearAccount = "";
	        }
    	}
    	catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }
    

    /*********************************************************************
     * #1553 .纽约银行风控数据接口导出处理,无法查到对应的券商设置数据  
     * add by jiangshichao 2011.03.23
     * 
     * 查询出最匹配的券商信息
     * @return
     * @throws YssException
     */
    public IDataSetting getSettingforBnyInterface()throws YssException{
    	String strSql = "";
        ResultSet rs = null;
        int i = 0;
        try {
            strSql = " select * from " +
                pub.yssGetTableName("tb_para_brokersubbny") +
                " where fcheckstate=1 and FBROKERCODE= " +
                dbl.sqlString(this.strBrokerCode) + " and fexchangecode = " +
                dbl.sqlString(this.strExchangeCode)+
                " order by FTRADESUBCATCODE,ftradecatcode ";//排序是为了把最通用的券商信息排在最前面。(即交易品种代码、交易品种子代码都为空)
            
            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
            	if(i==0){
            		this.strBrokerIDType = rs.getString("FBrokerIDType");
            if (this.strBrokerIDType == null ||
                this.strBrokerIDType.equalsIgnoreCase("null")) {
                this.strBrokerIDType = "";
            }
            this.strBrokerID = rs.getString("FBrokerID");
            if (this.strBrokerID == null ||
                this.strBrokerID.equalsIgnoreCase("null")) {
                this.strBrokerID = "";
            }
            //BugNo:0000363 edit by jc
            this.strIFBrokerName = rs.getString("FIFBrokerName");
            if (this.strIFBrokerName == null ||
                this.strIFBrokerName.equalsIgnoreCase("null")) {
                this.strIFBrokerName = "";
            }
            //----------------------jc
            this.strClearerIDType = rs.getString("FClearerIDType");
            if (this.strClearerIDType == null ||
                this.strClearerIDType.equalsIgnoreCase("null")) {
                this.strClearerIDType = "";
            }
            this.strClearerID = rs.getString("FClearerID");
            if (this.strClearerID == null ||
                this.strClearerID.equalsIgnoreCase("null")) {
                this.strClearerID = "";
            }
            //BugNo:0000363 edit by jc
            this.strClearerName = rs.getString("FClearerName");
            if (this.strClearerName == null ||
                this.strClearerName.equalsIgnoreCase("null")) {
                this.strClearerName = "";
            }
            this.strClearerDesc = rs.getString("FClearerDesc");
            if (this.strClearerDesc == null ||
                this.strClearerDesc.equalsIgnoreCase("null")) {
                this.strClearerDesc = "";
            }
            //----------------------jc
            this.strBrokerAccount = rs.getString("FBrokerAccount");
            if (this.strBrokerAccount == null ||
                this.strBrokerAccount.equalsIgnoreCase("null")) {
                this.strBrokerAccount = "";
            }

            this.strPlaceSettlement = rs.getString("FPlaceOfSettlement");
            if (this.strPlaceSettlement == null ||
                this.strClearAccount.equalsIgnoreCase("null")) {
                this.strPlaceSettlement = "";
            }
            this.strClearAccount = rs.getString("FClearAccount");
            if (this.strClearAccount == null ||
                this.strClearAccount.equalsIgnoreCase("null")) {
                this.strClearAccount = "";
            }
            	}
            	if(i!=0 && this.strTradeCatCode.equalsIgnoreCase(rs.getString("ftradecatcode"))){
            		this.strBrokerIDType = rs.getString("FBrokerIDType");
            if (this.strBrokerIDType == null ||
                this.strBrokerIDType.equalsIgnoreCase("null")) {
                this.strBrokerIDType = "";
            }
            this.strBrokerID = rs.getString("FBrokerID");
            if (this.strBrokerID == null ||
                this.strBrokerID.equalsIgnoreCase("null")) {
                this.strBrokerID = "";
            }
            //BugNo:0000363 edit by jc
            this.strIFBrokerName = rs.getString("FIFBrokerName");
            if (this.strIFBrokerName == null ||
                this.strIFBrokerName.equalsIgnoreCase("null")) {
                this.strIFBrokerName = "";
            }
            //----------------------jc
            this.strClearerIDType = rs.getString("FClearerIDType");
            if (this.strClearerIDType == null ||
                this.strClearerIDType.equalsIgnoreCase("null")) {
                this.strClearerIDType = "";
            }
            this.strClearerID = rs.getString("FClearerID");
            if (this.strClearerID == null ||
                this.strClearerID.equalsIgnoreCase("null")) {
                this.strClearerID = "";
            }
            //BugNo:0000363 edit by jc
            this.strClearerName = rs.getString("FClearerName");
            if (this.strClearerName == null ||
                this.strClearerName.equalsIgnoreCase("null")) {
                this.strClearerName = "";
            }
            this.strClearerDesc = rs.getString("FClearerDesc");
            if (this.strClearerDesc == null ||
                this.strClearerDesc.equalsIgnoreCase("null")) {
                this.strClearerDesc = "";
            }
            //----------------------jc
            this.strBrokerAccount = rs.getString("FBrokerAccount");
            if (this.strBrokerAccount == null ||
                this.strBrokerAccount.equalsIgnoreCase("null")) {
                this.strBrokerAccount = "";
            }

            this.strPlaceSettlement = rs.getString("FPlaceOfSettlement");
            if (this.strPlaceSettlement == null ||
                this.strClearAccount.equalsIgnoreCase("null")) {
                this.strPlaceSettlement = "";
            }
            this.strClearAccount = rs.getString("FClearAccount");
            if (this.strClearAccount == null ||
                this.strClearAccount.equalsIgnoreCase("null")) {
                this.strClearAccount = "";
            }
            	}
            	if(i!=0 && this.strTradeCatCode.equalsIgnoreCase(rs.getString("ftradecatcode"))
            			&&this.strTradeSubCatCode.equalsIgnoreCase(rs.getString("FTradeSubCatCode"))){
            		this.strBrokerIDType = rs.getString("FBrokerIDType");
            if (this.strBrokerIDType == null ||
                this.strBrokerIDType.equalsIgnoreCase("null")) {
                this.strBrokerIDType = "";
            }
            this.strBrokerID = rs.getString("FBrokerID");
            if (this.strBrokerID == null ||
                this.strBrokerID.equalsIgnoreCase("null")) {
                this.strBrokerID = "";
            }
            //BugNo:0000363 edit by jc
            this.strIFBrokerName = rs.getString("FIFBrokerName");
            if (this.strIFBrokerName == null ||
                this.strIFBrokerName.equalsIgnoreCase("null")) {
                this.strIFBrokerName = "";
            }
            //----------------------jc
            this.strClearerIDType = rs.getString("FClearerIDType");
            if (this.strClearerIDType == null ||
                this.strClearerIDType.equalsIgnoreCase("null")) {
                this.strClearerIDType = "";
            }
            this.strClearerID = rs.getString("FClearerID");
            if (this.strClearerID == null ||
                this.strClearerID.equalsIgnoreCase("null")) {
                this.strClearerID = "";
            }
            //BugNo:0000363 edit by jc
            this.strClearerName = rs.getString("FClearerName");
            if (this.strClearerName == null ||
                this.strClearerName.equalsIgnoreCase("null")) {
                this.strClearerName = "";
            }
            this.strClearerDesc = rs.getString("FClearerDesc");
            if (this.strClearerDesc == null ||
                this.strClearerDesc.equalsIgnoreCase("null")) {
                this.strClearerDesc = "";
            }
            //----------------------jc
            this.strBrokerAccount = rs.getString("FBrokerAccount");
            if (this.strBrokerAccount == null ||
                this.strBrokerAccount.equalsIgnoreCase("null")) {
                this.strBrokerAccount = "";
            }

            this.strPlaceSettlement = rs.getString("FPlaceOfSettlement");
            if (this.strPlaceSettlement == null ||
                this.strClearAccount.equalsIgnoreCase("null")) {
                this.strPlaceSettlement = "";
            }
            this.strClearAccount = rs.getString("FClearAccount");
            if (this.strClearAccount == null ||
                this.strClearAccount.equalsIgnoreCase("null")) {
                this.strClearAccount = "";
            }
            	}
                i ++;
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }
    
    
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int i = 0;
        try {
            strSql = " select * from " +
                pub.yssGetTableName("tb_para_brokersubbny") +
                " where FBROKERCODE= " +
                dbl.sqlString(this.strBrokerCode) + " and fexchangecode = " +
                dbl.sqlString(this.strExchangeCode)+
                " and  FTRADECATCODE = " +
                dbl.sqlString(this.strTradeCatCode)+// add by wuweiqi 20110221 券商添加品种子类型
                " and  FTRADESUBCATCODE= "+
                dbl.sqlString(this.strTradeSubCatCode);
            //         " and FTradeCatCode= " + dbl.sqlString(this.strTradeCatCode.length() == 0?" ":this.strTradeCatCode);
            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                this.strBrokerIDType = rs.getString("FBrokerIDType");
                if (this.strBrokerIDType == null ||
                    this.strBrokerIDType.equalsIgnoreCase("null")) {
                    this.strBrokerIDType = "";
                }
                this.strBrokerID = rs.getString("FBrokerID");
                if (this.strBrokerID == null ||
                    this.strBrokerID.equalsIgnoreCase("null")) {
                    this.strBrokerID = "";
                }
                //BugNo:0000363 edit by jc
                this.strIFBrokerName = rs.getString("FIFBrokerName");
                if (this.strIFBrokerName == null ||
                    this.strIFBrokerName.equalsIgnoreCase("null")) {
                    this.strIFBrokerName = "";
                }
                //----------------------jc
                this.strClearerIDType = rs.getString("FClearerIDType");
                if (this.strClearerIDType == null ||
                    this.strClearerIDType.equalsIgnoreCase("null")) {
                    this.strClearerIDType = "";
                }
                this.strClearerID = rs.getString("FClearerID");
                if (this.strClearerID == null ||
                    this.strClearerID.equalsIgnoreCase("null")) {
                    this.strClearerID = "";
                }
                //BugNo:0000363 edit by jc
                this.strClearerName = rs.getString("FClearerName");
                if (this.strClearerName == null ||
                    this.strClearerName.equalsIgnoreCase("null")) {
                    this.strClearerName = "";
                }
                this.strClearerDesc = rs.getString("FClearerDesc");
                if (this.strClearerDesc == null ||
                    this.strClearerDesc.equalsIgnoreCase("null")) {
                    this.strClearerDesc = "";
                }
                //----------------------jc
                this.strBrokerAccount = rs.getString("FBrokerAccount");
                if (this.strBrokerAccount == null ||
                    this.strBrokerAccount.equalsIgnoreCase("null")) {
                    this.strBrokerAccount = "";
                }

                this.strPlaceSettlement = rs.getString("FPlaceOfSettlement");
                if (this.strPlaceSettlement == null ||
                    this.strClearAccount.equalsIgnoreCase("null")) {
                    this.strPlaceSettlement = "";
                }
                this.strClearAccount = rs.getString("FClearAccount");
                if (this.strClearAccount == null ||
                    this.strClearAccount.equalsIgnoreCase("null")) {
                    this.strClearAccount = "";
                }

                i = i + 1;
            }
            dbl.closeResultSetFinal(rs);
            if (i > 1) {
                strSql = " select * from " +
                    pub.yssGetTableName("tb_para_brokersubbny") +
                    " where FBROKERCODE= " +
                    dbl.sqlString(this.strBrokerCode) + " and fexchangecode = " +
                    dbl.sqlString(this.strExchangeCode) +
                    " and FTradeCatCode= " + 
                    dbl.sqlString(this.strTradeCatCode)+
                    " and  FTRADESUBCATCODE= "+ // add by wuweiqi 20110221 券商添加品种子类型
                    dbl.sqlString(this.strTradeSubCatCode);
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strBrokerIDType = rs.getString("FBrokerIDType");
                if (this.strBrokerIDType == null ||
                    this.strBrokerIDType.equalsIgnoreCase("null")) {
                    this.strBrokerIDType = "";
                }
                this.strBrokerID = rs.getString("FBrokerID");
                if (this.strBrokerID == null ||
                    this.strBrokerID.equalsIgnoreCase("null")) {
                    this.strBrokerID = "";
                }
                //BugNo:0000363 edit by jc
                this.strIFBrokerName = rs.getString("FIFBrokerName");
                if (this.strIFBrokerName == null ||
                    this.strIFBrokerName.equalsIgnoreCase("null")) {
                    this.strIFBrokerName = "";
                }
                //----------------------jc
                this.strClearerIDType = rs.getString("FClearerIDType");
                if (this.strClearerIDType == null ||
                    this.strClearerIDType.equalsIgnoreCase("null")) {
                    this.strClearerIDType = "";
                }
                this.strClearerID = rs.getString("FClearerID");
                if (this.strClearerID == null ||
                    this.strClearerID.equalsIgnoreCase("null")) {
                    this.strClearerID = "";
                }
                //BugNo:0000363 edit by jc
                this.strClearerName = rs.getString("FClearerName");
                if (this.strClearerName == null ||
                    this.strClearerName.equalsIgnoreCase("null")) {
                    this.strClearerName = "";
                }
                this.strClearerDesc = rs.getString("FClearerDesc");
                if (this.strClearerDesc == null ||
                    this.strClearerDesc.equalsIgnoreCase("null")) {
                    this.strClearerDesc = "";
                }
                //----------------------jc
                this.strBrokerAccount = rs.getString("FBrokerAccount");
                if (this.strBrokerAccount == null ||
                    this.strBrokerAccount.equalsIgnoreCase("null")) {
                    this.strBrokerAccount = "";
                }

                this.strPlaceSettlement = rs.getString("FPlaceOfSettlement");
                if (this.strPlaceSettlement == null ||
                    this.strClearAccount.equalsIgnoreCase("null")) {
                    this.strPlaceSettlement = "";
                }
                this.strClearAccount = rs.getString("FClearAccount");
                if (this.strClearAccount == null ||
                    this.strClearAccount.equalsIgnoreCase("null")) {
                    this.strClearAccount = "";
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr,String brokerCode) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);
            strSql = "delete from " + pub.yssGetTableName("TB_PARA_BROKERSUBBNY") +
//                " where FBrokerCode=" + dbl.sqlString(this.strBrokerCode);
                  " where FBrokerCode=" + dbl.sqlString(brokerCode);//modify by wuweqi 20110309 券商关联表最后一条数据无法删除

            dbl.executeSql(strSql);
            // conn.setAutoCommit(true);
            strSql =
                "insert into " + pub.yssGetTableName("TB_PARA_BROKERSUBBNY") +
                "(FBrokerCode,FExchangeCode,FBrokerIDType,FBrokerID,FIFBrokerName,FClearerIDType,FClearerID,FClearerName,FClearerDesc,FBrokerAccount,FDesc,FTradeCatCode,FPlaceOfSettlement,FClearAccount," +
                "FCheckState,FCreator,FCreateTime,FCheckUser,FTradeSubCatCode)" +//#2415 add by wuweiqi 20110128 添加品种子类型代码
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (i > 0) {
                    this.parseRowStr(sMutilRowAry[i]);
                }
                if (this.strBrokerCode.trim().length() > 0) {
                    pstmt.setString(1, this.strBrokerCode);
                    pstmt.setString(2, this.strExchangeCode);
                    pstmt.setString(3, this.strBrokerIDType);
                    pstmt.setString(4, this.strBrokerID);
                    pstmt.setString(5, this.strIFBrokerName);
                    pstmt.setString(6, this.strClearerIDType);
                    pstmt.setString(7, this.strClearerID);
                    pstmt.setString(8, this.strClearerName);
                    pstmt.setString(9, this.strClearerDesc);
                    pstmt.setString(10, this.strBrokerAccount);
                    pstmt.setString(11, this.strDesc);
                    pstmt.setString(12,
                                    this.strTradeCatCode.length() == 0 ? " " :
                                    this.strTradeCatCode);
                    pstmt.setString(13, strPlaceSettlement);
                    pstmt.setString(14, strClearAccount);

                    pstmt.setInt(15, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(16, this.creatorCode);
                    pstmt.setString(17, this.creatorTime);
                    pstmt.setString(18,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    //----#2415 add by wuweiqi 20110128 添加品种子类型代码------------------//
                    pstmt.setString(19,
                            this.strTradeSubCatCode.length() == 0 ? " " :
                            this.strTradeSubCatCode);
                    //------------------end by wuweiqi 20110128---------------------------//
                    pstmt.executeUpdate();
                }
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存关联表信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            
        }
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData2() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String getOperValue(String sType) {
        return "";
    }

    public String getBeforeEditData() {
        return "";
    }

    public String getStrBrokerName() {
        return strBrokerName;
    }

    public void setStrBrokerName(String strBrokerName) {
        this.strBrokerName = strBrokerName;
    }

    public String getStrExchangeName() {
        return strExchangeName;
    }

    public void setStrExchangeName(String strExchangeName) {
        this.strExchangeName = strExchangeName;
    }

    public String getStrBrokerAccount() {
        return strBrokerAccount;
    }

    public String getStrOldTradeCatCode() {
        return strOldTradeCatCode;
    }

    public BrokerSubBnyBean getFilterType() {
        return filterType;
    }

    public int getCheckStateId() {
        return checkStateId;
    }

    public String getStrPlaceSettlement() {
        return strPlaceSettlement;
    }

    public String getStrClearAccount() {
        return strClearAccount;
    }

    public String getStrTradeCatName() {
        return strTradeCatName;
    }

    public String getStrTradeCatCode() {
        return strTradeCatCode;
    }

    public String getStrClearerDesc() {
        return strClearerDesc;
    }

    public String getStrClearerName() {
        return strClearerName;
    }

    public String getStrIFBrokerName() {
        return strIFBrokerName;
    }

    public void setStrBrokerAccount(String strBrokerAccount) {
        this.strBrokerAccount = strBrokerAccount;
    }

    public void setStrOldTradeCatCode(String strOldTradeCatCode) {
        this.strOldTradeCatCode = strOldTradeCatCode;
    }

    public void setFilterType(BrokerSubBnyBean filterType) {
        this.filterType = filterType;
    }

    public void setCheckStateId(int checkStateId) {
        this.checkStateId = checkStateId;
    }

    public void setStrPlaceSettlement(String strPlaceSettlement) {
        this.strPlaceSettlement = strPlaceSettlement;
    }

    public void setStrClearAccount(String strClearAccount) {
        this.strClearAccount = strClearAccount;
    }

    public void setStrTradeCatName(String strTradeCatName) {
        this.strTradeCatName = strTradeCatName;
    }

    public void setStrTradeCatCode(String strTradeCatCode) {
        this.strTradeCatCode = strTradeCatCode;
    }
    
    public void setStrClearerName(String strClearerName) {
        this.strClearerName = strClearerName;
    }

    public void setStrClearerDesc(String strClearerDesc) {
        this.strClearerDesc = strClearerDesc;
    }

    public void setStrIFBrokerName(String strIFBrokerName) {
        this.strIFBrokerName = strIFBrokerName;
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

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
}
