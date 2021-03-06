package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: InvestManagerBean 投资经理</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class InvestManagerBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strCode; //投资经理代码
    private String strName; //投资经理名称
    private String strShortName; //投资经理简称
    private java.util.Date strStartDate; //启用日期
    private String strPhone; //办公电话
    private String strTelephone; //移动电话
    private String strEmail; //电子邮件
    private String strAddress; //联系地址
    private String strPostcode; //邮政编码
    private String strDesc; //描述
    private String sRecycled = "";

    private String assetGroupCode = "";//组合群代码

    private String strOldCode;
    private java.util.Date strOldStartDate;
    private InvestManagerBean filterType;
    public String getStrEmail() {
        return strEmail;
    }

    public String getStrPostcode() {
        return strPostcode;
    }

    public String getStrTelephone() {
        return strTelephone;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrPhone() {
        return strPhone;
    }

    public InvestManagerBean getFilterType() {
        return filterType;
    }

    public String getStrAddress() {
        return strAddress;
    }

    public String getStrShortName() {
        return strShortName;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrCode() {
        return strCode;
    }

    public void setStrOldCode(String strOldCode) {
        this.strOldCode = strOldCode;
    }

    public void setStrEmail(String strEmail) {
        this.strEmail = strEmail;
    }

    public void setStrPostcode(String strPostcode) {
        this.strPostcode = strPostcode;
    }

    public void setStrTelephone(String strTelephone) {
        this.strTelephone = strTelephone;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrPhone(String strPhone) {
        this.strPhone = strPhone;
    }

    public void setFilterType(InvestManagerBean filterType) {
        this.filterType = filterType;
    }

    public void setStrAddress(String strAddress) {
        this.strAddress = strAddress;
    }

    public void setStrShortName(String strShortName) {
        this.strShortName = strShortName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public void setStrCode(String strCode) {
        this.strCode = strCode;
    }

    public String getStrOldCode() {
        return strOldCode;
    }

    public InvestManagerBean() {
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

            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.strCode = reqAry[0];
            this.strName = reqAry[1];
            this.strShortName = reqAry[2];
            this.strStartDate = YssFun.toDate(reqAry[3]);
            this.strPhone = reqAry[4];
            this.strTelephone = reqAry[5];
            this.strEmail = reqAry[6];
            this.strAddress = reqAry[7];
            this.strPostcode = reqAry[8];
            this.strDesc = reqAry[9];
            this.strDesc = dealwith(this.strDesc);//edited by zhouxiang MS01256    投资经理设置，新建一条数据时，描述中点击回车键，另起一行描述，删除该数据，到回收站清除时，报错    QDV4赢时胜(上海)2010年06月7日01_B 
            this.checkStateId = Integer.parseInt(reqAry[10]);
            this.strOldCode = reqAry[11];
            //-----------------------------
            //解析中丢失strOldStartDate字段   
            //MS00833 新增投资经理出错  李道龙 2009.11.26
            this.strOldStartDate=YssFun.toDate(reqAry[12]);
            //-----------------------
            this.assetGroupCode = reqAry[13];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new InvestManagerBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析投资经理设置请求信息出错", e);
        }

    }
    
  //edited by zhouxiang MS01256    投资经理设置，新建一条数据时，描述中点击回车键，另起一行描述，删除该数据，到回收站清除时，报错    QDV4赢时胜(上海)2010年06月7日01_B 
  //用来解释描述字段的回车符
    public String dealwith(String strDesc)
    {
    	String[] arrays=strDesc.split("\r\n");
    	String Str="";
    	if(arrays.length>1)
    	{
    		for(int i=0;i<arrays.length;i++)
    	    {
    			Str=Str + arrays[i];
    	        Str=Str+"/-";
    	    }
    		//update by guolongchao 20120316 BUG3964  投资经理设置”界面描述内容不输入任何字符却显示为null------start
    		if(Str.endsWith("/-"))
    		{
    			Str=Str.substring(0,Str.length()-2);
    		}
    		//update by guolongchao 20120316 BUG3964  投资经理设置”界面描述内容不输入任何字符却显示为null------end
    	    return Str;
    	}
    	else
    	    return strDesc;
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.strCode.trim()).append("\t");
        buffer.append(this.strName.trim()).append("\t");
        buffer.append(this.strShortName.trim()).append("\t");
        buffer.append(YssFun.formatDate(this.strStartDate)).append("\t");
        buffer.append(this.strPhone).append("\t");
        buffer.append(this.strTelephone).append("\t");
        buffer.append(this.strEmail).append("\t");
        buffer.append(this.strAddress).append("\t");
        buffer.append(this.strPostcode.trim()).append("\t");
        buffer.append(this.strDesc.trim()).append("\t");
        buffer.append(super.buildRecLog());
        return buffer.toString();

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */

    public void checkInput(byte btOper) throws YssException {
    	//----edit by songjie 2011.03.14 不以启用日期查询主键数据----//
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_InvestManager"), "FInvMgrCode",
                               this.strCode,
                               this.strOldCode);
        //----edit by songjie 2011.03.14 不以启用日期查询主键数据----//
    }

    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*   public void saveSetting(byte btOper) throws YssException {
          Connection conn = dbl.loadConnection();
          boolean bTrans = false;
          String strSql = "";
          try {
             if (btOper == YssCons.OP_ADD) {
                strSql =
                      "insert into " + pub.yssGetTableName("Tb_Para_InvestManager") + "(FInvMgrCode, FInvMgrName, FInvMgrShortName, " +
                      " FStartDate,FPhoneCode, FTelephoneCode, FEmail, FLinkAddr, FPOSTALCODE,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                      " values(" + dbl.sqlString(this.strCode) + "," +
                      dbl.sqlString(this.strName) + "," +
                      dbl.sqlString(this.strShortName) + "," +
                      dbl.sqlDate(this.strStartDate) + "," +
                      dbl.sqlString(this.strPhone) + "," +
                      dbl.sqlString(this.strTelephone) + "," +
                      dbl.sqlString(this.strEmail) + "," +
                      dbl.sqlString(this.strAddress) + "," +
                      dbl.sqlString(this.strPostcode) + "," +
                      dbl.sqlString(this.strDesc) + "," +
                      (pub.getSysCheckState()?"0":"1") + "," +
                      dbl.sqlString(this.creatorCode) + "," +
                      dbl.sqlString(this.creatorTime) + "," +
                      (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
             }
             else if (btOper == YssCons.OP_EDIT) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_InvestManager") + " set FInvMgrCode = " +
                      dbl.sqlString(this.strCode) + ", FInvMgrName = "
                      + dbl.sqlString(this.strName) + ",FInvMgrShortName = " +
                      dbl.sqlString(this.strShortName) + ",FStartDate = "
                      + dbl.sqlDate(this.strStartDate) + ", FPhoneCode = " +
                      dbl.sqlString(this.strPhone) + ", FTelephoneCode = "
                      + dbl.sqlString(this.strTelephone) + ", FEmail = " +
                      dbl.sqlString(this.strEmail) + ", FLinkAddr = "
                      + dbl.sqlString(this.strAddress) + ", FPOSTALCODE = " +
                      dbl.sqlString(this.strPostcode) + ", FDesc= "+
                      dbl.sqlString(this.strDesc) + ", FCreator = " +
                      dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                      dbl.sqlString(this.creatorTime) +
                      " where FInvMgrCode = " + dbl.sqlString(this.strOldCode) +
                      " and FStartDate=" + dbl.sqlDate(this.strOldStartDate);
             }
             else if (btOper == YssCons.OP_DEL) {
//            strSql = "delete from " + pub.yssGetTableName("Tb_Para_InvestManager") + " where FInvMgrCode = " +
//                  dbl.sqlString(this.strCode) +" and FStartDate=" + dbl.sqlDate(this.strStartDate);
                strSql = "update " + pub.yssGetTableName("Tb_Para_InvestManager") + " set FCheckState = " +
                      this.checkStateId + ", FCheckUser = " +
                      dbl.sqlString(pub.getUserCode()) +
                      ", FCheckTime = '" +
                      YssFun.formatDatetime(new java.util.Date()) + "'" +
                      " where FInvMgrCode = " +
                      dbl.sqlString(this.strCode) + " and FStartDate=" +
                      dbl.sqlDate(this.strStartDate);

             }
             else if (btOper == YssCons.OP_AUDIT) {
//            if (this.checkStateId == 2) {//删除审核
//               strSql = "delete from " + pub.yssGetTableName("Tb_Para_InvestManager") + " where FInvMgrCode = " +
//                     dbl.sqlString(this.strCode) + " and FStartDate=" +
//                     dbl.sqlDate(this.strStartDate);
//            }
                strSql = "update " + pub.yssGetTableName("Tb_Para_InvestManager") + " set FCheckState = " +
                      this.checkStateId + ", FCheckUser = " +
                      dbl.sqlString(pub.getUserCode()) +
                      ", FCheckTime = '" +
                      YssFun.formatDatetime(new java.util.Date()) + "'" +
                      " where FInvMgrCode = " +
                      dbl.sqlString(this.strCode) + " and FStartDate=" +
                      dbl.sqlDate(this.strStartDate);
             }
             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
          }
          catch (Exception e) {
             throw new YssException("设置投资经理信息出错！", e);
          }
          finally {
             dbl.endTransFinal(conn, bTrans);
          }

       }*/
    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_InvestManager") + "(FInvMgrCode, FInvMgrName, FInvMgrShortName, " +
                " FStartDate,FPhoneCode, FTelephoneCode, FEmail, FLinkAddr, FPOSTALCODE,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.strCode) + "," +
                dbl.sqlString(this.strName) + "," +
                dbl.sqlString(this.strShortName) + "," +
                dbl.sqlDate(this.strStartDate) + "," +
                dbl.sqlString(this.strPhone) + "," +
                dbl.sqlString(this.strTelephone) + "," +
                dbl.sqlString(this.strEmail) + "," +
                dbl.sqlString(this.strAddress) + "," +
                dbl.sqlString(this.strPostcode) + "," +
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加投资经理信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_InvestManager") + " set FInvMgrCode = " +
                dbl.sqlString(this.strCode) + ", FInvMgrName = "
                + dbl.sqlString(this.strName) + ",FInvMgrShortName = " +
                dbl.sqlString(this.strShortName) + ",FStartDate = "
                + dbl.sqlDate(this.strStartDate) + ", FPhoneCode = " +
                dbl.sqlString(this.strPhone) + ", FTelephoneCode = "
                + dbl.sqlString(this.strTelephone) + ", FEmail = " +
                dbl.sqlString(this.strEmail) + ", FLinkAddr = "
                + dbl.sqlString(this.strAddress) + ", FPOSTALCODE = " +
                dbl.sqlString(this.strPostcode) + ", FDesc= " +
                dbl.sqlString(this.strDesc) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                dbl.sqlString(this.creatorTime) +
                " where FInvMgrCode = " + dbl.sqlString(this.strOldCode) +
                " and FStartDate=" + dbl.sqlDate(this.strOldStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改投资经理信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     *删除数据，放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_InvestManager") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FInvMgrCode = " +
                dbl.sqlString(this.strCode) + " and FStartDate=" +
                dbl.sqlDate(this.strStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除投资经理信息信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理投资经理的审核和未审核的单条信息。
     *  新方法功能：可以处理投资经理审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//    String strSql = "";
//    boolean bTrans = false; //代表是否开始了事务
//    Connection conn = dbl.loadConnection();
//    try {
//       strSql = "update " + pub.yssGetTableName("Tb_Para_InvestManager") + " set FCheckState = " +
//                this.checkStateId + ", FCheckUser = " +
//                dbl.sqlString(pub.getUserCode()) +
//                ", FCheckTime = '" +
//                YssFun.formatDatetime(new java.util.Date()) + "'" +
//                " where FInvMgrCode = " +
//                dbl.sqlString(this.strCode) + " and FStartDate=" +
//                dbl.sqlDate(this.strStartDate);
//       conn.setAutoCommit(false);
//       bTrans = true;
//       dbl.executeSql(strSql);
//       conn.commit();
//       bTrans = false;
//       conn.setAutoCommit(true);
//    }
//
//    catch (Exception e) {
//       throw new YssException("审核投资经理信息出错", e);
//    }
//    finally {
//       dbl.endTransFinal(conn, bTrans);
//    }
        //修改后的代码
        //--------------------------------------------
        Connection conn = null;
        String[] arrData = null;
        boolean bTrans = false;
        PreparedStatement stm = null;
        String strSql = "";

        try {
            conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "update " + pub.yssGetTableName("Tb_Para_InvestManager") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FInvMgrCode = ?" +
                " and FStartDate=?";
            //把sql语句付给PreparedStatement
            stm = dbl.openPreparedStatement(strSql);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.strCode);
                stm.setDate(2, YssFun.toSqlDate(strStartDate));
                stm.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核投资经理信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //--------------------------------------------


    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr Sring
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * getSetting
     *
     * @return IParaSet
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FInvMgrCode = " + dbl.sqlString(this.strCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.strName = rs.getString("FInvMgrName");
            }
            return null;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getAllSetting
     *
     * @retur String
     */
    public String getAllSetting() throws YssException {
        StringBuffer buf = new StringBuffer();
        String strSql = "", strResult = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_InvestManager") + "" +
                " order by FCheckState, FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append( (rs.getString("FInvMgrCode") + "").trim()).append("\t");
                buf.append( (rs.getString("FInvMgrName") + "").trim()).append("\t");
                buf.append( (rs.getString("FInvMgrShortName") + "").trim()).append(
                    "\t");
                buf.append(rs.getString("FStartDate")).append("\t");
                buf.append( (rs.getString("FPhoneCode") + "").trim()).append("\t");
                buf.append( (rs.getString("FTelephoneCode") + "").trim()).append(
                    "\t");
                buf.append( (rs.getString("FEmail") + "").trim()).append("\t");
                buf.append( (rs.getString("FLinkAddr") + "").trim()).append("\t");
                buf.append( (rs.getString("FPostcode") + "").trim()).append("\t");
                buf.append( (rs.getString("FDesc") + "").trim()).append("\t");
                buf.append(super.buildRecLog(rs)).append(YssCons.YSS_LINESPLITMARK);

            }
            strResult = buf.toString();
            if (strResult.length() > 2) {
                strResult = strResult.substring(0, strResult.length() - 2);
            }
            return strResult;
        } catch (Exception e) {
            throw new YssException("获取投资经理信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setInvestManagerAttr(rs);
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
            throw new YssException("获取投资经理信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 此方法已被修改
     *修改时间：2008年
     * 修改人：单亮
     * 原方法的功能：查询出投资经理数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     */

    public String getListViewData1() throws YssException {
        String strSql = "";
        //修改前的代码
//      strSql = "select y.* from " +
//            "(select FINVMGRCode,FCheckState,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_InvestManager") + " " +
//            " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
//            "and FCheckState <> 2 group by FINVMGRCode,FCheckState) x join" +
//            " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_InvestManager") + " a" +
//            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
//            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
//            buildFilterSql() +
//            ") y on x.FINVMGRCode = y.FINVMGRCode and x.FStartDate = y.FStartDate" +
//            " order by y.FCheckState, y.FCreateTime desc";
        //修改后的代码
        //----------------------------------
        strSql = "select y.* from " +
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//            "(select FINVMGRCode,FCheckState,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_InvestManager") + " " +
//            " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
//            " group by FINVMGRCode,FCheckState) x join" +
            //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
            " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_InvestManager") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
            " order by y.FCheckState, y.FCreateTime desc";
        //----------------------------------
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取投资管理人全部数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_InvestManager") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.fstartdate desc, a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
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
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "投资经理代码\t投资经理名称\t启用日期";
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                "(select FINVMGRCode,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_InvestManager") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState = 1 group by FINVMGRCode) x join" +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                //----edit by songjie 2011.03.14 不以最大的启用日期查询数据----//
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + 
                " (select * from " + pub.yssGetTableName("Tb_Para_InvestManager") + " where FCheckState = 1 ) a" +
                //----edit by songjie 2011.03.14 不以最大的启用日期查询数据----//
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                buildFilterSql() +
                ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FInvMgrCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FInvMgrName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"))).append(
                    YssCons.YSS_LINESPLITMARK);
                setInvestManagerAttr(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取交易所信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
     * @eturn String
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
     * buildFilterSql 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strCode.length() != 0) {
                // modify by wangzuochun 2009.06.22
                // MS00014  国内回购业务  QDV4.1赢时胜（上海）2009年4月20日14_A
                // sResult = sResult + " and a.FInvMgrCode like '" +
                // filterType.strCode.replaceAll("'", "''") + "%'";
                if (this.filterType.strCode.lastIndexOf(",") > 0) {
                    sResult = sResult + " and a.FInvMgrCode in( " +
                        operSql.sqlCodes(filterType.strCode) + ")";
                } else {
                    sResult = sResult + " and a.FInvMgrCode like '" +
                        filterType.strCode.replaceAll("'", "''") + "%'";
                }
            }
            if (this.filterType.strName.length() != 0) {
                sResult = sResult + " and a.FInvMgrName like '" +
                    filterType.strName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strShortName.length() != 0) {
                sResult = sResult + " and a.FInvMgrShortName = " +
                    dbl.sqlString(filterType.strShortName);
            }
            if (this.filterType.strStartDate != null &&
                !this.filterType.strStartDate.equals(YssFun.toDate("9998-12-31"))
                //edit by songjie 2011.03.14 启用日期默认为1900-01-01
                && !this.filterType.strStartDate.equals(YssFun.toDate("1900-01-01"))) {
                sResult = sResult + " and a.FStartDate <= " +
                    dbl.sqlDate(filterType.strStartDate);
            }

            if (this.filterType.strPhone.length() != 0) {
                sResult = sResult + " and a.FPhoneCode = " +
                    dbl.sqlString(filterType.strPhone);
            }
            if (this.filterType.strTelephone.length() != 0) {
                sResult = sResult + " and a.FTelephoneCode = " +
                    dbl.sqlString(filterType.strTelephone);
            }
            if (this.filterType.strEmail.length() != 0) {
                sResult = sResult + " and a.FEmail = " +
                    dbl.sqlString(filterType.strEmail);
            }
            if (this.filterType.strAddress.length() != 0) {
                sResult = sResult + " and a.FLinkAddr = " +
                    dbl.sqlString(filterType.strAddress);
            }
            if (this.filterType.strPostcode.length() != 0) {
                sResult = sResult + " and a.FPostalCode = " +
                    dbl.sqlString(filterType.strPostcode);
            }
        }
        return sResult;
    }

    public void setInvestManagerAttr(ResultSet rs) throws SQLException {
        this.strCode = rs.getString("FInvMgrCode") + "";
        this.strName = rs.getString("FInvMgrName") + "";
        this.strShortName = rs.getString("FInvMgrShortName") + "";
        this.strStartDate = rs.getDate("FStartDate");
        this.strPhone = rs.getString("FPhoneCode") + "";
        this.strTelephone = rs.getString("FTelephoneCode") + "";
        this.strEmail = rs.getString("FEmail") + "";
        this.strAddress = rs.getString("FLinkAddr") + "";
        this.strPostcode = rs.getString("FPostalcode") + "";
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
    public String getBeforeEditData() throws YssException {
        InvestManagerBean befEditBean = new InvestManagerBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//                "(select FINVMGRCode,FCheckState,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_InvestManager") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState <> 2 group by FINVMGRCode,FCheckState) x join" +
                //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                //edit by songjie 2011.03.14 不以最大的启用日期查询数据
                " (select * from " + pub.yssGetTableName("Tb_Para_InvestManager") + " where FCheckState <> 2 ) a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  FINVMGRCode =" + dbl.sqlString(this.strOldCode) +
                ") y " +//edit by songjie 2011.03.14 不以最大的启用日期查询数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strCode = rs.getString("FInvMgrCode") + "";
                befEditBean.strName = rs.getString("FInvMgrName") + "";
                befEditBean.strShortName = rs.getString("FInvMgrShortName") + "";
                befEditBean.strStartDate = rs.getDate(
                    "FStartDate");
                befEditBean.strPhone = rs.getString("FPhoneCode") + "";
                befEditBean.strTelephone = rs.getString("FTelephoneCode") + "";
                befEditBean.strEmail = rs.getString("FEmail") + "";
                befEditBean.strAddress = rs.getString("FLinkAddr") + "";
                befEditBean.strPostcode = rs.getString("FPostalcode") + "";
                befEditBean.strDesc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * 从回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        PreparedStatement stm = null;
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //根据规定的符号，把多个sql语句分别放入数组
            arrData = sRecycled.split("\r\n");//edited by zhouxiang MS01256    投资经理设置，新建一条数据时，描述中点击回车键，另起一行描述，删除该数据，到回收站清除时，报错    QDV4赢时胜(上海)2010年06月7日01_B   
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FInvMgrCode = ? and FStartDate=?";
            //把sql语句付给PreparedStatement
            stm = dbl.openPreparedStatement(strSql);
            //循环执行这些删除语句
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.strCode);
                stm.setDate(2, YssFun.toSqlDate(this.strStartDate));
                stm.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
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
        String strRe = "";//存放返回到前台的字符串
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码即表前缀
        try{
            pub.setPrefixTB(this.assetGroupCode);//将前台传过来的组合群代码设置为表前缀
            strRe = this.getListViewData2();//将该组合群所对应的投资经理列表返回至前台
        }catch(Exception e){
            throw new YssException("获取投资经理信息出错", e);
        }finally{
            pub.setPrefixTB(sPrefixTB);//还原公共变的里的组合群代码即表前缀
        }
        return strRe;
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
