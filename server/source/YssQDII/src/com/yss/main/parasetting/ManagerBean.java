package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title:ManagerBean 管理人 </p>
 * <p>Description:1 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ManagerBean
    extends BaseDataSettingBean implements IDataSetting {

    private String managerCode; //管理人代码
    private String managerName; //管理人名称
    private String managerShortName; //管理人简称
    private String officeAddr; //办公地址
    private String postalCode; //邮政编码
    private String desc; //描述

    private String oldManagerCode;

    private java.util.Date startDate; //启用日期
    private java.util.Date oldStartDate;

    private String linkMans;
    private String sRecycled = "";

    private ManagerBean filterType;

    public ManagerBean() {
    }

    /**
     * parseRowStr
     * 解析管理人数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
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
                if (sRowStr.split("\r\t").length == 3) {
                    this.linkMans = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.managerCode = reqAry[0];
            this.managerName = reqAry[1];
            this.startDate = YssFun.toDate(reqAry[2]);
            this.managerShortName = reqAry[3];
            this.officeAddr = reqAry[4];
            this.postalCode = reqAry[5];
            this.desc = reqAry[6].replaceAll("\f<>", "\r\n");//edited by zhouxiang MS01405    机构设置描述信息中包含回车换行符，删除记录后，在回收站中清除时会报错    
            super.checkStateId = Integer.parseInt(reqAry[7]);
            this.oldManagerCode = reqAry[8];
            this.oldStartDate = YssFun.toDate(reqAry[9]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new ManagerBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析管理人设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.managerCode).append("\t");
        buf.append(this.managerName).append("\t");
        buf.append(YssFun.formatDate(this.startDate, YssCons.YSS_DATEFORMAT)).
            append("\t");
        buf.append(this.managerShortName).append("\t");
        buf.append(this.officeAddr).append("\t");
        buf.append(this.postalCode).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查管理人输入数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    	//----edit by songjie 2011.03.11 不以启用日期作为查询主键数据的参数----//
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Manager"),
                               "FManagerCode",
                               this.managerCode,
                               this.oldManagerCode);
        //----edit by songjie 2011.03.11 不以启用日期作为查询主键数据的参数----//
    }

    /**
     * saveSetting
     * 更新管理人数据
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (btOper == YssCons.OP_ADD) {
     strSql = "insert into " + pub.yssGetTableName("tb_para_manager") + "" +
                     " (FManagerCode,FManagerName,FStartDate,FManagerShortName,FOfficeAddr,FPostalCode,FDesc," +
                     "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                     " values(" + dbl.sqlString(this.managerCode) + "," +
                     dbl.sqlString(this.managerName) + "," +
                     dbl.sqlDate(this.startDate) + "," +
                     dbl.sqlString(this.managerShortName) + "," +
                     dbl.sqlString(this.officeAddr) + "," +
                     dbl.sqlString(this.postalCode) + "," +
                     dbl.sqlString(this.desc) + "," +
                     (pub.getSysCheckState()?"0":"1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
            }
            else if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("tb_para_manager") + " set FManagerCode = " +
                     dbl.sqlString(this.managerCode) + ",FManagerName = " +
                     dbl.sqlString(this.managerName) + ",FStartDate = " +
                     dbl.sqlDate(this.startDate) + ",FManagerShortName = " +
                     dbl.sqlString(this.managerShortName) + ",FOfficeAddr = " +
                     dbl.sqlString(this.officeAddr) + ",FPostalCode = " +
                     dbl.sqlString(this.postalCode) + ",FDesc = " +
                     dbl.sqlString(this.desc) + ",FCheckstate = " +
                     (pub.getSysCheckState()?"0":"1") + ",FCreator = " +
                     dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                     dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                     " where FManagerCode = " +
                     dbl.sqlString(this.oldManagerCode) +
                     " and FStartDate = " +
                     dbl.sqlDate(this.oldStartDate);
            }
            else if (btOper == YssCons.OP_DEL) {
               //  strSql = "delete from " + pub.yssGetTableName("tb_para_manager") + " where FManagerCode = " +
               //删除时将审核标志修改为2
               strSql = "update " + pub.yssGetTableName("tb_para_manager") + " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FManagerCode = " +
                     dbl.sqlString(this.managerCode) +
                     " and FStartDate = " +
                     dbl.sqlDate(this.startDate);
            }
            else if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("tb_para_manager") + " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FManagerCode = " +
                     dbl.sqlString(this.managerCode) +
                     " and FStartDate = " +
                     dbl.sqlDate(this.startDate);
            }
            dbl.executeSql(strSql);

// 联系人处理
     if (btOper == YssCons.OP_EDIT && (this.managerCode != this.oldManagerCode ||
     this.startDate != this.oldStartDate)) {
               strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FRelaCode = " +
                     dbl.sqlString(this.managerCode) + ", FStartDate = " +
                     dbl.sqlDate(this.startDate) +
                     " where FRelaCode = " +
                     dbl.sqlString(this.oldManagerCode) +
                     " and FRelaType = 'Manager'" +
                     " and FStartDate = " +
                     dbl.sqlDate(this.oldStartDate);
               dbl.executeSql(strSql);
            }

            if (this.linkMans != null) {
               if (btOper == YssCons.OP_ADD || btOper == YssCons.OP_EDIT) {
                  LinkManBean linkMan = new LinkManBean();
                  linkMan.setYssPub(pub);
                  linkMan.saveMutliSetting(this.linkMans);
               }
            }
            if (btOper == YssCons.OP_DEL) {
               //  strSql = "delete from tb_para_linkman where frelacode = " +
               //删除时将审核标志修改为2
               strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FRelaCode = " +
                     dbl.sqlString(this.managerCode) +
                     " and frelatype = 'Manager'" +
                     " and FStartDate = " +
                     dbl.sqlDate(this.startDate);

               dbl.executeSql(strSql);
            }
            if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where frelacode = " +
                     dbl.sqlString(this.managerCode) +
                     " and frelatype = 'Manager'" +
                     " and FStartDate = " +
                     dbl.sqlDate(this.startDate);

               dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         }
         catch (Exception e) {
            throw new YssException("更新管理人信息出错", e);
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
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into " + pub.yssGetTableName("tb_para_manager") + "" +
                " (FManagerCode,FManagerName,FStartDate,FManagerShortName,FOfficeAddr,FPostalCode,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.managerCode) + "," +
                dbl.sqlString(this.managerName) + "," +
                dbl.sqlDate(this.startDate) + "," +
                dbl.sqlString(this.managerShortName) + "," +
                dbl.sqlString(this.officeAddr) + "," +
                dbl.sqlString(this.postalCode) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";

            dbl.executeSql(strSql);

            if (this.linkMans != null) {

                //   LinkManBean linkMan = new LinkManBean();
                //  linkMan.setYssPub(pub);
                linkMan.saveMutliSetting(this.linkMans);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            LinkManBean lkmBean = new LinkManBean();
            lkmBean.setRelaType("Manager");
            lkmBean.setRelaCode(this.managerCode);
            lkmBean.setStartDate(this.startDate);
            linkMan.setFilterType(lkmBean);
            lkmBean = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("增加管理人信息出错", e);
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
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {
            LinkManBean bFilterType = new LinkManBean();
            bFilterType.setRelaType("Manager");
            //    bFilterType.setRelaCode(this.managerCode);
            bFilterType.setRelaCode(this.oldManagerCode);
            bFilterType.setStartDate(this.startDate);

            linkMan.setFilterType(bFilterType);
            bFilterType = linkMan.getFilterType();
            this.setBSubData(linkMan.getListViewData1());

            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("tb_para_manager") +
                " set FManagerCode = " +
                dbl.sqlString(this.managerCode) + ",FManagerName = " +
                dbl.sqlString(this.managerName) + ",FStartDate = " +
                dbl.sqlDate(this.startDate) + ",FManagerShortName = " +
                dbl.sqlString(this.managerShortName) + ",FOfficeAddr = " +
                dbl.sqlString(this.officeAddr) + ",FPostalCode = " +
                dbl.sqlString(this.postalCode) + ",FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckstate = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FManagerCode = " +
                dbl.sqlString(this.oldManagerCode) +
                " and FStartDate = " +
                dbl.sqlDate(this.oldStartDate);
            dbl.executeSql(strSql);
			/**shashijie 2012-7-2 STORY 2475 */
            if (!this.managerCode.equals(this.oldManagerCode) ||
			/**end*/
                this.startDate != this.oldStartDate) {
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                    " set FRelaCode = " +
                    dbl.sqlString(this.managerCode) + ", FStartDate = " +
                    dbl.sqlDate(this.startDate) +
                    " where FRelaCode = " +
                    dbl.sqlString(this.oldManagerCode) +
                    " and FRelaType = 'Manager'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.oldStartDate);
                dbl.executeSql(strSql);
            }

            if (this.linkMans != null) {
                // LinkManBean linkMan = new LinkManBean();
                // linkMan.setYssPub(pub);
                linkMan.saveMutliSetting(this.linkMans);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            LinkManBean lkmBean = new LinkManBean();
            lkmBean.setRelaType("Manager");
            lkmBean.setRelaCode(this.managerCode);
            lkmBean.setStartDate(this.startDate);
            linkMan.setFilterType(lkmBean);
            lkmBean = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("修改管理人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("tb_para_manager") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FManagerCode = " +
                dbl.sqlString(this.managerCode) +
                " and FStartDate = " +
                dbl.sqlDate(this.startDate);
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FRelaCode = " +
                dbl.sqlString(this.managerCode) +
                " and frelatype = 'Manager'" +
                " and FStartDate = " +
                dbl.sqlDate(this.startDate);

            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            LinkManBean lkmBean = new LinkManBean();
            lkmBean.setRelaType("Manager");
            lkmBean.setRelaCode(this.managerCode);
            lkmBean.setStartDate(this.startDate);
            linkMan.setFilterType(lkmBean);
            lkmBean = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("删除管理人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改时间：2008年3月25号
     * 修改人：单亮
     * 原方法功能：只能处管理人的审核和未审核的单条信息。
     * 新方法功能：可以管理人连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以管理人连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      //---------------------------------------
//      LinkManBean linkMan = new LinkManBean();
//      linkMan.setYssPub(pub);
//      //---------------------------------------
//      try {
//         conn.setAutoCommit(false);
//         bTrans = true;
//         strSql = "update " + pub.yssGetTableName("tb_para_manager") +
//               " set FCheckState = " +
//               this.checkStateId +
//               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "' where FManagerCode = " +
//               dbl.sqlString(this.managerCode) +
//               " and FStartDate = " +
//               dbl.sqlDate(this.startDate);
//         dbl.executeSql(strSql);
//         strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
//               " set FCheckState = " +
//               this.checkStateId +
//               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "' where frelacode = " +
//               dbl.sqlString(this.managerCode) +
//               " and frelatype = 'Manager'" +
//               " and FStartDate = " +
//               dbl.sqlDate(this.startDate);
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//         LinkManBean filterType = new LinkManBean();
//         filterType.setRelaType("Manager");
//         filterType.setRelaCode(this.managerCode);
//         filterType.setStartDate(this.startDate);
//         linkMan.setFilterType(filterType);
//         filterType = linkMan.getFilterType();
//         this.setASubData(linkMan.getListViewData1());
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核管理人信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //--------------begin
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        //---------------------------------------
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        //---------------------------------------
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("tb_para_manager") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FManagerCode = " +
                        dbl.sqlString(this.managerCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);
                    dbl.executeSql(strSql);
                    strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where frelacode = " +
                        dbl.sqlString(this.managerCode) +
                        " and frelatype = 'Manager'" +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);
                    dbl.executeSql(strSql);
                }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            } else if (managerCode != null && !managerCode.equalsIgnoreCase("")) { //如果sRecycled为空，而managerCode不为空，则按照managerCode来执行sql语句
                strSql = "update " + pub.yssGetTableName("tb_para_manager") +
                    " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FManagerCode = " +
                    dbl.sqlString(this.managerCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                    " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where frelacode = " +
                    dbl.sqlString(this.managerCode) +
                    " and frelatype = 'Manager'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Manager");
            filterType.setRelaCode(this.managerCode);
            filterType.setStartDate(this.startDate);
            linkMan.setFilterType(filterType);
            filterType = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());
        } catch (Exception e) {
            throw new YssException("审核管理人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //----------end
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
     * @return IParaSetting
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
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.managerCode != null && this.filterType.managerCode.length() != 0) {
                sResult = sResult + " and a.FManagerCode like '" +
                    filterType.managerCode.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.managerName != null && this.filterType.managerName.length() != 0) {
                sResult = sResult + " and a.FManagerName like '" +
                    filterType.managerName.replaceAll("'", "''") + "%'";
            }
            //----delete by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据----//
//            if (this.filterType.startDate != null &&
//                !this.filterType.startDate.equals(YssFun.toDate("9998-12-31"))) {
//                sResult = sResult + " and a.FStartDate <= " +
//                    dbl.sqlDate(filterType.startDate);
//            }
            //----delete by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据----//
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.managerShortName != null && this.filterType.managerShortName.length() != 0) {
                sResult = sResult + " and a.FManagerShortName like '" +
                    filterType.managerShortName.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.officeAddr != null && this.filterType.officeAddr.length() != 0) {
                sResult = sResult + " and a.FOfficeAddr like '" +
                    filterType.officeAddr.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.postalCode != null && this.filterType.postalCode.length() != 0) {
                sResult = sResult + " and a.FPostalCode like '" +
                    filterType.postalCode.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.desc != null && this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void setManagerAttr(ResultSet rs) throws SQLException {
        this.managerCode = rs.getString("FManagerCode") + "";
        this.managerName = rs.getString("FManagerName") + "";
        this.startDate = rs.getDate("FStartDate");
        this.managerShortName = rs.getString("FManagerShortName") + "";
        this.officeAddr = rs.getString("FOfficeAddr") + "";
        this.postalCode = rs.getString("FPostalCode") + "";
        this.desc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setManagerAttr(rs);
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
            throw new YssException("获取管理人设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月25号
     * 修改人：单亮
     * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            //----delete by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据----//
//            "(select FManagerCode,FCheckState,max(FStartDate) as FStartDate from " +
//            pub.yssGetTableName("Tb_Para_Manager") + " " +
//            " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
//            //修改前的代码
//            //"and FCheckState <> 2 group by FManagerCode,FCheckState) x join" +
//            //修改后的代码
//            //----------------------------begin
//            " group by FManagerCode,FCheckState) x join" +
            //----delete by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据----//
            //----------------------------end
            " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Manager") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql =
            "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Manager") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取已审核的管理人数据
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
            sHeader = "管理人代码\t管理人名称\t启用日期";
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据----//
//                "(select FManagerCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Manager") + " " +
//                " where FCheckState = 1 and FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                " group by FManagerCode) x join" +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据----//
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                //edit by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据
                " (select * from  " + pub.yssGetTableName("Tb_Para_Manager") + " where FCheckState = 1 ) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ") y " + //edit by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FManagerCode") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FManagerName") + "").trim());
                bufShow.append("\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
                                                 YssCons.YSS_DATEFORMAT));
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                setManagerAttr(rs);
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
            throw new YssException("获取可用管理人信息出错！", e);
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
        ManagerBean befEditBean = new ManagerBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据----//
//                "(select FManagerCode,FCheckState,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Manager") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState <> 2 group by FManagerCode,FCheckState) x join" +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据----//
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                //edit by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据
                " (select * from " + pub.yssGetTableName("Tb_Para_Manager") + " where FCheckState <> 2 ) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  a.FManagerCode =" + dbl.sqlString(this.oldManagerCode) +
                ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询管理人设置数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.managerCode = rs.getString("FManagerCode") + "";
                befEditBean.managerName = rs.getString("FManagerName") + "";
                befEditBean.startDate = rs.getDate("FStartDate");
                befEditBean.managerShortName = rs.getString("FManagerShortName") +
                    "";
                befEditBean.officeAddr = rs.getString("FOfficeAddr") + "";
                befEditBean.postalCode = rs.getString("FPostalCode") + "";
                befEditBean.desc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * 从回收站中删除数据，即从数据库彻底删除
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
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equals("")) {
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
                        pub.yssGetTableName("tb_para_manager") +
                        " where FManagerCode = " +
                        dbl.sqlString(this.managerCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                    strSql = "delete from " +
                        pub.yssGetTableName("tb_para_linkman") +
                        " where FRelaCode = " +
                        dbl.sqlString(this.managerCode) +
                        " and frelatype = 'Manager'" +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而managerCode不为空，则按照managerCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (managerCode != null && !managerCode.equals("")) {
                strSql = "delete from " +
                    pub.yssGetTableName("tb_para_manager") +
                    " where FManagerCode = " +
                    dbl.sqlString(this.managerCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                //执行sql语句
                dbl.executeSql(strSql);
                strSql = "delete from " +
                    pub.yssGetTableName("tb_para_linkman") +
                    " where FRelaCode = " +
                    dbl.sqlString(this.managerCode) +
                    " and frelatype = 'Manager'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
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
