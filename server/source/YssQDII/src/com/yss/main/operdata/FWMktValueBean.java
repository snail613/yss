package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: MarketValueBean </p>
 * <p>Description: 远期行情数据 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

public class FWMktValueBean
    extends BaseDataSettingBean implements IDataSetting {

    private String strMktSrcCode = ""; //行情来源代码
    private String strMktSrcName = ""; //行情来源名称
    private String strSecurityCode = ""; //证券代码
    private String strSecurityName = ""; //证券名称
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String strDepDurCode = ""; //期限代码
    private String strDepDurName = ""; //期限名称
    private String strMktValueDate = "1900-01-01"; //行情日期
    private String strMktValueTime = "00:00:00"; //行情时间

    private String strSpotDate = "1900-01-01"; //即期日期
    private String strFWDate = "1900-01-01"; //远期日期

    private double dblBuyPrice; //买入价格
    private double dblSellPrice; //买出价格
    private double dblBuyPoint; //买入点数
    private double dblSellPoint; //买出点数

    private double dblMktPrice1; //行情备用1
    private double dblMktPrice2; //行情备用2
    private String strDesc = ""; //行情描述
//    private String isOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private String sRecycled = ""; //保存未解析前的字符串

    private String strOldDepDurCode = "";
    private String strOldMktSrcCode = "";
    private String strOldPortCode = "";
    private String strOldSecurityCode = "";
    private String strOldMktValueDate = "1900-01-01";
    private String strOldMktValueTime = "00:00:00";

    private FWMktValueBean filterType;

    public FWMktValueBean() {
    }

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
            this.strMktSrcCode = reqAry[0];
            this.strSecurityCode = reqAry[1];
            this.strMktValueDate = reqAry[2];
            this.strMktValueTime = reqAry[3];
            this.strPortCode = reqAry[4];
            this.strDepDurCode = reqAry[5];
            this.strSpotDate = reqAry[6];
            this.strFWDate = reqAry[7];
            if (YssFun.isNumeric(reqAry[8])) {
                this.dblBuyPrice = Double.parseDouble(reqAry[8]);
            }
            if (YssFun.isNumeric(reqAry[9])) {
                this.dblSellPrice = Double.parseDouble(reqAry[9]);
            }
            if (YssFun.isNumeric(reqAry[10])) {
                this.dblBuyPoint = Double.parseDouble(reqAry[10]);
            }
            if (YssFun.isNumeric(reqAry[11])) {
                this.dblSellPoint = Double.parseDouble(reqAry[11]);
            }

            if (YssFun.isNumeric(reqAry[12])) {
                this.dblMktPrice1 = Double.parseDouble(reqAry[12]);
            }
            if (YssFun.isNumeric(reqAry[13])) {
                this.dblMktPrice2 = Double.parseDouble(reqAry[13]);
            }
          //------ modify by nimengjing 2010.12.02 BUG #535 指数行情设置界面描述字段中存在回车符时，清除/还原报错
	         if (reqAry[14] != null ){
	        	 if (reqAry[14].indexOf("【Enter】") >= 0){
	        		 this.strDesc= reqAry[14].replaceAll("【Enter】", "\r\n");
	             }
	             else{
	            	 this.strDesc = reqAry[14];
	             }
	         }
	         //----------------- BUG #533 ----------------//
            this.checkStateId = Integer.parseInt(reqAry[15]);
            this.strOldMktSrcCode = reqAry[16];
            this.strOldSecurityCode = reqAry[17];
            this.strOldMktValueDate = reqAry[18];
            this.strOldMktValueTime = reqAry[19];
            this.strOldPortCode = reqAry[20];
            this.strOldDepDurCode = reqAry[21];
            this.isOnlyColumns = reqAry[22];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new FWMktValueBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析远期行情数据请求信息出错", e);
        }
    }

    /**
     * buildRowStr
     * 返回行情数据信息
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strMktSrcCode).append("\t");
        buf.append(this.strMktSrcName).append("\t");
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(this.strMktValueDate).append("\t");
        buf.append(this.strMktValueTime).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strDepDurCode).append("\t");
        buf.append(this.strDepDurName).append("\t");

        buf.append(this.strSpotDate).append("\t");
        buf.append(this.strFWDate).append("\t");

        buf.append(this.dblBuyPrice).append("\t");
        buf.append(this.dblSellPrice).append("\t");
        buf.append(this.dblBuyPoint).append("\t");
        buf.append(this.dblSellPoint).append("\t");

        buf.append(this.dblMktPrice1).append("\t");
        buf.append(this.dblMktPrice2).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 新增一条行情数据信息
     * @throws YssException
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Data_FWMktValue") +
                "(FMktSrcCode, FSecurityCode, FMktValueDate, FMktValueTime, FPortCode, FDepDurCode," +
                " FSpotDate, FFWDate, FBuyPrice, FSellPrice, FBuyPoint, FSellPoint," +
                "  FMktPrice1, FMktPrice2, FDesc,FDataSource," +
                " FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.strMktSrcCode) + "," +
                dbl.sqlString(this.strSecurityCode) + "," +
                dbl.sqlDate(this.strMktValueDate) + "," +
                dbl.sqlString(this.strMktValueTime) + "," +
                dbl.sqlString( (this.strPortCode.length() == 0) ? " " :
                              this.strPortCode) + "," +
                dbl.sqlString( (this.strDepDurCode.length() == 0) ? " " :
                              this.strDepDurCode) + "," +
                dbl.sqlDate(this.strSpotDate) + "," +
                dbl.sqlDate(this.strFWDate) + "," +
                this.dblBuyPrice + "," +
                this.dblSellPrice + "," +
                this.dblBuyPoint + "," +
                this.dblSellPoint + "," +
                this.dblMktPrice1 + "," +
                this.dblMktPrice2 + "," +
                dbl.sqlString(this.strDesc) + ",1," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增远期行情数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     * 数据验证
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_FWMktValue"),
                               "FMktSrcCode,FSecurityCode,FMktValueDate," +
                               "FMktValueTime,FPortCode,FDepDurCode",
                               this.strMktSrcCode + "," + this.strSecurityCode +
                               "," +
                               this.strMktValueDate + "," + this.strMktValueTime +
                               "," +
                               ( (this.strPortCode.length() == 0) ? " " : this.strPortCode) + "," +
                               ( (this.strDepDurCode.length() == 0) ? " " : this.strDepDurCode),
                               this.strOldMktSrcCode + "," +
                               this.strOldSecurityCode + "," +
                               this.strOldMktValueDate + "," +
                               this.strOldMktValueTime + "," +
                               ( (this.strOldPortCode.length() == 0) ? " " : this.strOldPortCode) + "," +
                               ( (this.strOldDepDurCode.length() == 0) ? " " : this.strOldDepDurCode));
    }

    /**
     * 修改时间：2008年3月27号
     * 修改人：单亮
     * 原方法功能：只能处理远期行情的审核和未审核的单条信息。
     * 新方法功能：可以处理远期行情审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理远期行情审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      Connection conn = dbl.loadConnection();
//      boolean bTrans = false;
//      String strSql = "";
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Data_FWMktValue") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where FMktSrcCode = " + dbl.sqlString(this.strMktSrcCode) +
//               " and FPortCode=" +
//               dbl.sqlString( (this.strPortCode.length() == 0) ? " " :
//                             this.strPortCode) +
//			  " and FDepDurCode=" +
//               dbl.sqlString( (this.strDepDurCode.length() == 0) ? " " :
//                             this.strDepDurCode) +
//               " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode) +
//               " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate) +
//               " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime);
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//      catch (Exception e) {
//         throw new YssException("审核远期行情数据信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //-----------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Data_FWMktValue") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FMktSrcCode = " + dbl.sqlString(this.strMktSrcCode) +
                        " and FPortCode=" +
                        dbl.sqlString( (this.strPortCode.length() == 0) ? " " :
                                      this.strPortCode) +
                        " and FDepDurCode=" +
                        dbl.sqlString( (this.strDepDurCode.length() == 0) ? " " :
                                      this.strDepDurCode) +
                        " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode) +
                        " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate) +
                        " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而strMktSrcCode不为空，则按照strMktSrcCode来执行sql语句
            else if ( strMktSrcCode != null&&(!strMktSrcCode.equalsIgnoreCase(""))) {
                strSql = "update " + pub.yssGetTableName("Tb_Data_FWMktValue") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FMktSrcCode = " + dbl.sqlString(this.strMktSrcCode) +
                    " and FPortCode=" +
                    dbl.sqlString( (this.strPortCode.length() == 0) ? " " :
                                  this.strPortCode) +
                    " and FDepDurCode=" +
                    dbl.sqlString( (this.strDepDurCode.length() == 0) ? " " :
                                  this.strDepDurCode) +
                    " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode) +
                    " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate) +
                    " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核远期行情数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //-------------------end
    }

    /**
     * 删除一条行情数据信息,即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_FWMktValue") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FMktSrcCode = " + dbl.sqlString(this.strMktSrcCode) +
                " and FPortCode=" +
                dbl.sqlString( (this.strPortCode.length() == 0) ? " " :
                              this.strPortCode) +
                " and FDepDurCode=" +
                dbl.sqlString( (this.strDepDurCode.length() == 0) ? " " :
                              this.strDepDurCode) +
                " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode) +
                " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate) +
                " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除远期行情数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     * 编辑一条行情数据信息
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_FWMktValue") +
                " set FMktSrcCode = " +
                dbl.sqlString(this.strMktSrcCode) + ", FSecurityCode = "
                + dbl.sqlString(this.strSecurityCode) + ",FPortCode = "
                + dbl.sqlString( (this.strPortCode.length() == 0) ? " " :
                                this.strPortCode) + "  ,FDepDurCode= " +
                dbl.sqlString( (this.strDepDurCode.length() == 0) ? " " :
                              this.strDepDurCode) + ",FMktValueDate = "
                + dbl.sqlDate(this.strMktValueDate) + ",FMktValueTime = "
                + dbl.sqlString(this.strMktValueTime) + ", FSpotDate = "
                + dbl.sqlDate(this.strSpotDate) + ",FFWDate = "
                + dbl.sqlDate(this.strFWDate) + ", FBuyPrice = "
                + this.dblBuyPrice + ", FSellPrice ="
                + this.dblSellPrice + ", FBuyPoint="
                + this.dblBuyPoint + ", FSellPoint = "
                + this.dblSellPoint + ",FMktPrice1 = "
                + this.dblMktPrice1 + ", FMktPrice2 = "
                + this.dblMktPrice2 + ", FDesc = "
                + dbl.sqlString(this.strDesc) + ", FCreator = "
                + dbl.sqlString(this.creatorCode) + " , FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                " where FMktSrcCode = " + dbl.sqlString(this.strOldMktSrcCode) +
                " and FPortCode = " +
                dbl.sqlString( (this.strOldPortCode.length() == 0) ? " " :
                              this.strOldPortCode) +
                " and FDepDurCode=" +
                dbl.sqlString( (this.strOldDepDurCode.length() == 0) ? " " :
                              this.strOldDepDurCode) +
                " and FSecurityCode = " + dbl.sqlString(this.strOldSecurityCode) +
                " and FMktValueDate = " + dbl.sqlDate(this.strOldMktValueDate) +
                " and FMktValueTime = " + dbl.sqlString(this.strOldMktValueTime);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改远期行情数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.isOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2";
                return sResult;
            }
            if (this.filterType.strMktSrcCode.length() != 0) {
                sResult = sResult + " and a.FMktSrcCode like '" +
                    filterType.strMktSrcCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.strPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strDepDurCode.length() != 0) {
                sResult = sResult + " and a.FDepDurCode like '" +
                    filterType.strDepDurCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSecurityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.strSecurityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strMktValueDate.length() != 0 &&
                !this.filterType.strMktValueDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FMktValueDate = " +
                    dbl.sqlDate(filterType.strMktValueDate);
            }
            if (!this.filterType.strMktValueTime.equalsIgnoreCase("00:00:00")) {
                sResult = sResult + " and a.FMktValueTime = " +
                    dbl.sqlString(filterType.strMktValueTime);
            }
            if (this.filterType.strSpotDate.length() != 0 &&
                !this.filterType.strSpotDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FSpotDate = " +
                    dbl.sqlDate(filterType.strSpotDate);
            }
            if (this.filterType.strFWDate.length() != 0 &&
                !this.filterType.strFWDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FFWDate = " +
                    dbl.sqlDate(filterType.strFWDate);
            }
            if (this.filterType.dblBuyPrice > 0) {
                sResult = sResult + " and a.FBuyPrice = " +
                    filterType.dblBuyPrice;
            }
            if (this.filterType.dblSellPrice > 0) {
                sResult = sResult + " and a.FSellPrice = " +
                    filterType.dblSellPrice;
            }
            if (this.filterType.dblBuyPoint > 0) {
                sResult = sResult + " and a.FBuyPoint = " +
                    filterType.dblBuyPoint;
            }
            if (this.filterType.dblSellPoint > 0) {
                sResult = sResult + " and a.FSellPoint = " +
                    filterType.dblSellPoint;
            }

            if (this.filterType.dblMktPrice1 > 0) {
                sResult = sResult + " and a.FMktPrice1 = " +
                    filterType.dblMktPrice1;
            }
            if (this.filterType.dblMktPrice2 > 0) {
                sResult = sResult + " and a.FMktPrice2 = " +
                    filterType.dblMktPrice2;
            }
            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
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
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f"+ yssPageInationBean.buildRowStr()+"\r\f";//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FPortName, e.FSecurityName, f.FMktSrcName, g.FDepDurName " +
                " from " + pub.yssGetTableName("Tb_Data_FWMktValue") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select FPortCode, FPortName, FStartDate from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                " left join (select FMktSrcCode, FMktSrcName from " +
                pub.yssGetTableName("Tb_Para_MarketSource") +
                ") f on a.FMktSrcCode = f.FMktSrcCode " +
                " left join (select FDepDurCode, FDepDurName from " +
                pub.yssGetTableName("Tb_Para_DepositDuration") +
                ") g on a.FDepDurCode = g.FDepDurCode " +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("FWMktValue");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
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
                this.getListView1ShowCols()+ "\r\f"+ yssPageInationBean.buildRowStr()+"\r\f";//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取远期行情数据信息出错！", e);
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
     * getOperData
     */
    public void getOperData() {
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

    public void setStorageAttr(ResultSet rs) throws SQLException {

        super.setRecLog(rs);
    }

    /**
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliOperData(String sMutilRowStr) throws YssException {
        return "";
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strMktSrcCode = rs.getString("FMktSrcCode") + "";
        this.strMktSrcName = rs.getString("FMktSrcName") + "";
        this.strSecurityCode = rs.getString("FSecurityCode") + "";
        this.strSecurityName = rs.getString("FSecurityName") + "";
        this.strMktValueDate = YssFun.formatDate(rs.getDate("FMktValueDate"));
        this.strMktValueTime = rs.getString("FMktValueTime") + "";
        this.strPortCode = rs.getString("FPortCode").trim() + "";
        this.strPortName = rs.getString("FPortName") + "";
        this.strDepDurCode = rs.getString("FDepDurCode").trim() + "";
        this.strDepDurName = rs.getString("FDepDurName") + "";

        this.strSpotDate = YssFun.formatDate(rs.getDate("FSpotDate"));
        this.strFWDate = YssFun.formatDate(rs.getDate("FFWDate"));

        this.dblBuyPrice = rs.getDouble("FBuyPrice");
        this.dblSellPrice = rs.getDouble("FSellPrice");
        this.dblBuyPoint = rs.getDouble("FBuyPoint");
        this.dblSellPoint = rs.getDouble("FSellPoint");
        this.dblMktPrice1 = rs.getDouble("FMktPrice1");
        this.dblMktPrice2 = rs.getDouble("FMktPrice2");
        this.strDesc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
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
    public String getBeforeEditData() {

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
                        pub.yssGetTableName("Tb_Data_FWMktValue") +
                        " where FMktSrcCode = " + dbl.sqlString(this.strMktSrcCode) +
                        " and FPortCode=" +
                        dbl.sqlString( (this.strPortCode.length() == 0) ? " " :
                                      this.strPortCode) +
                        " and FDepDurCode=" +
                        dbl.sqlString( (this.strDepDurCode.length() == 0) ? " " :
                                      this.strDepDurCode) +
                        " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode) +
                        " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate) +
                        " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而strMktSrcCode不为空，则按照strMktSrcCode来执行sql语句
            else if (strMktSrcCode != "" && strMktSrcCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_FWMktValue") +
                    " where FMktSrcCode = " + dbl.sqlString(this.strMktSrcCode) +
                    " and FPortCode=" +
                    dbl.sqlString( (this.strPortCode.length() == 0) ? " " :
                                  this.strPortCode) +
                    " and FDepDurCode=" +
                    dbl.sqlString( (this.strDepDurCode.length() == 0) ? " " :
                                  this.strDepDurCode) +
                    " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode) +
                    " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate) +
                    " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime);
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
