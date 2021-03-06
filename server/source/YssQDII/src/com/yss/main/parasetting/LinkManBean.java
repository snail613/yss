package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class LinkManBean
    extends BaseDataSettingBean implements IDataSetting {

    private String linkManCode = ""; //联系人代码
    private String linkManName = ""; //联系人名称
    private String relaCode = ""; //关联代码
    private String relaType = ""; //关联类型
    private String relaGrade = ""; //关联等级
    private String email = ""; //电子邮箱
    private String addr = ""; //联系地址
    private String postCode = ""; //邮政编码
    private String phone = ""; //电话号码
    private String telephone = ""; //手机号码
    private String faxcode = ""; //传真号码
    private String desc = ""; //描述

    private String oldlinkManCode = "";

    private java.util.Date startDate;
    private LinkManBean filterType;
    public void setFilterType(LinkManBean filterType) {
        this.filterType = filterType;
    }

    public void setRelaType(String relaType) {
        this.relaType = relaType;
    }

    public void setRelaCode(String relaCode) {
        this.relaCode = relaCode;
    }

    public void setOldlinkManCode(String oldlinkManCode) {
        this.oldlinkManCode = oldlinkManCode;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setFaxcode(String faxcode) {
        this.faxcode = faxcode;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LinkManBean getFilterType() {
        return filterType;
    }

    public String getRelaType() {
        return relaType;
    }

    public String getRelaCode() {
        return relaCode;
    }

    public String getOldlinkManCode() {
        return oldlinkManCode;
    }

    public String getAddr() {
        return addr;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getFaxcode() {
        return faxcode;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public String getPhone() {
        return phone;
    }

    public LinkManBean() {
    }

    /**
     * parseRowStr
     * 解析联系人设置信息
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
            this.linkManCode = reqAry[0];
            this.linkManName = reqAry[1];
            this.relaGrade = reqAry[2];
            this.relaType = reqAry[3];
            this.relaCode = reqAry[4];
            this.email = reqAry[5];
            this.addr = reqAry[6];
            this.postCode = reqAry[7];
            this.phone = reqAry[8];
            this.telephone = reqAry[9];
            this.faxcode = reqAry[10];
            this.desc = reqAry[11];
            this.startDate = YssFun.toDate(reqAry[12]);
            super.checkStateId = Integer.parseInt(reqAry[13]);
            this.oldlinkManCode = reqAry[14];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new LinkManBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析联系人设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.linkManCode.trim()).append("\t");
        buf.append(this.linkManName.trim()).append("\t");
        buf.append(this.relaGrade.trim()).append("\t");
        buf.append(this.email.trim()).append("\t");
        buf.append(this.addr.trim()).append("\t");
        buf.append(this.postCode.trim()).append("\t");
        buf.append(this.phone.trim()).append("\t");
        buf.append(this.telephone.trim()).append("\t");
        buf.append(this.faxcode.trim()).append("\t");
        buf.append(this.desc.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查联系人输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_LinkMan"), "FLinkManCode",
                               this.linkManCode, this.oldlinkManCode);
    }

    /**
     * saveSetting
     * 更新联系人设置信息
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
            if (btOper == YssCons.OP_ADD) {
               strSql = "insert into " + pub.yssGetTableName("tb_para_linkman") + "" +
                     " (FLinkManCode,FLinkManName,FRelaCode,FRelaType,FRelaGrade,FEmail,FLinkAddr,FPostalCode," +
                     "FPhoneCode,FTelephoneCode,FFaxCode,FDesc,FStartDate,fcheckstate,fcreator,fcreatetime)" +
                     " values(" + dbl.sqlString(this.linkManCode) + "," +
                     dbl.sqlString(this.linkManName) + "," +
                     dbl.sqlString(this.relaCode) + "," +
                     dbl.sqlString(this.relaType) + "," +
                     dbl.sqlString(this.relaGrade) + "," +
                     dbl.sqlString(this.email) + "," +
                     dbl.sqlString(this.addr) + "," +
                     dbl.sqlString(this.postCode) + "," +
                     dbl.sqlString(this.phone) + "," +
                     dbl.sqlString(this.telephone) + "," +
                     dbl.sqlString(this.faxcode) + "," +
                     dbl.sqlString(this.desc) + "," +
                     dbl.sqlDate(this.startDate) + "," +
                     this.checkStateId + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + ")";
            }
            else if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set flinkmancode = " +
                     dbl.sqlString(this.linkManCode) + ",flinkmanname = " +
                     dbl.sqlString(this.linkManName) + ",frelacode = " +
                     dbl.sqlString(this.relaCode) + ",frelatype = " +
                     dbl.sqlString(this.relaType) + ",frelagrade = " +
                     dbl.sqlString(this.relaGrade) + ",femail = " +
                     dbl.sqlString(this.email) + ",flinkaddr = " +
                     dbl.sqlString(this.addr) + ",fpostalcode = " +
                     dbl.sqlString(this.postCode) + ",fphonecode = " +
                     dbl.sqlString(this.phone) + ", FTelephoneCode = " +
                     dbl.sqlString(this.telephone) + ", FFaxCode = " +
                     dbl.sqlString(this.faxcode) + ",FDesc = " +
                     dbl.sqlString(this.desc) + ",FCreator = " +
                     dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                     dbl.sqlString(this.creatorTime) + ",FStartDate =" +
                     dbl.sqlDate(this.startDate) +
                     " where flinkmancode = " +
                     dbl.sqlString(this.oldlinkManCode);
            }
            else if (btOper == YssCons.OP_DEL) {
               //   strSql = "delect from " + pub.yssGetTableName("tb_para_linkman") + " where flinkmancode = " +
               //删除时将审核标志修改为2
               strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set fcheckstate = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FLinkmanCode = " +
                     dbl.sqlString(this.oldlinkManCode);
            }
            //        else if (btOper == YssCons.OP_AUDIT) {
            //           strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set fcheckstate = " +
            //                 this.checkStateId +
            //                 ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            //                 ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
            //                 "' where flinkmancode = " +
            //                 dbl.sqlString(this.linkManCode);
            //        }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         }
         catch (Exception e) {
            throw new YssException("更新联系人信息出错", e);
         }
         finally {
            dbl.endTransFinal(conn, bTrans);
         }
      }

     */



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
            strSql = "insert into " + pub.yssGetTableName("tb_para_linkman") + "" +
                " (FLinkManCode,FLinkManName,FRelaCode,FRelaType,FRelaGrade,FEmail,FLinkAddr,FPostalCode," +
                "FPhoneCode,FTelephoneCode,FFaxCode,FDesc,FStartDate,fcheckstate,fcreator,fcreatetime)" +
                " values(" + dbl.sqlString(this.linkManCode) + "," +
                dbl.sqlString(this.linkManName) + "," +
                dbl.sqlString(this.relaCode) + "," +
                dbl.sqlString(this.relaType) + "," +
                dbl.sqlString(this.relaGrade) + "," +
                dbl.sqlString(this.email) + "," +
                dbl.sqlString(this.addr) + "," +
                dbl.sqlString(this.postCode) + "," +
                dbl.sqlString(this.phone) + "," +
                dbl.sqlString(this.telephone) + "," +
                dbl.sqlString(this.faxcode) + "," +
                dbl.sqlString(this.desc) + "," +
                dbl.sqlDate(this.startDate) + "," +
                this.checkStateId + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加联系人信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set flinkmancode = " +
                dbl.sqlString(this.linkManCode) + ",flinkmanname = " +
                dbl.sqlString(this.linkManName) + ",frelacode = " +
                dbl.sqlString(this.relaCode) + ",frelatype = " +
                dbl.sqlString(this.relaType) + ",frelagrade = " +
                dbl.sqlString(this.relaGrade) + ",femail = " +
                dbl.sqlString(this.email) + ",flinkaddr = " +
                dbl.sqlString(this.addr) + ",fpostalcode = " +
                dbl.sqlString(this.postCode) + ",fphonecode = " +
                dbl.sqlString(this.phone) + ", FTelephoneCode = " +
                dbl.sqlString(this.telephone) + ", FFaxCode = " +
                dbl.sqlString(this.faxcode) + ",FDesc = " +
                dbl.sqlString(this.desc) + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FStartDate =" +
                dbl.sqlDate(this.startDate) +
                " where flinkmancode = " +
                dbl.sqlString(this.oldlinkManCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改联系人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set fcheckstate = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FLinkmanCode = " +
                dbl.sqlString(this.oldlinkManCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除联系人信息信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);
            strSql = "delete from " + pub.yssGetTableName("tb_para_linkman") + " where FRelaType = " +
                dbl.sqlString(this.relaType) + " and FRelaCode = " +
                dbl.sqlString(this.relaCode) + " and FStartDate = " +
                dbl.sqlDate(this.startDate);
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("tb_para_linkman") + " (FLinkManCode,FRelaType,FRelaCode," +
                "FStartDate,FRelaGrade,FLinkManName,FLinkAddr,FPostalCode,FPhoneCode,FTelephoneCode,FEmail,FFaxCode,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (i > 0) {
                    this.parseRowStr(sMutilRowAry[i]);
                }
                if (this.linkManCode.trim().length() > 0) {
                    pstmt.setString(1, this.linkManCode);
                    pstmt.setString(2, this.relaType);
                    pstmt.setString(3, this.relaCode);
                    pstmt.setDate(4, YssFun.toSqlDate(this.startDate));
                    pstmt.setString(5, this.relaGrade);
                    pstmt.setString(6, this.linkManName);
                    pstmt.setString(7, this.addr);
                    pstmt.setString(8, this.postCode);
                    pstmt.setString(9, this.phone);
                    pstmt.setString(10, this.telephone);
                    pstmt.setString(11, this.email);
                    pstmt.setString(12, this.faxcode);
                    pstmt.setString(13, this.desc);
                    pstmt.setInt(14, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(15, this.creatorCode);
                    pstmt.setString(16, this.creatorTime);
                    pstmt.setString(17, (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.executeUpdate();
                }
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存联系人信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
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
     * getListViewData1
     * 获取联系人数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        try {
            sHeader = "联系人代码\t联系人名称\t电子邮箱\t手机号码";
//               \t状态";
            String sql = "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername from " + pub.yssGetTableName("tb_para_linkman") + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where a.FRelaType = " +
                //20110928 modifyed by liubo.Story #1509
                //为filterType为NULL时做出一定的处理，避免调用时报NULL值的错误
                //======================================
                dbl.sqlString((this.filterType == null ? " " : this.filterType.relaType)) + 
                " and a.FRelaCode = " +
                dbl.sqlString((this.filterType == null ? " " : this.filterType.relaCode)) +
                " and a.FStartDate = " + 
                dbl.sqlDate((this.filterType == null ? new java.util.Date() : this.filterType.startDate)) +
                //===============end=======================

                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                buf.append( (rs.getString("FLinkManCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FLinkManName") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FEmail") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FTelephoneCode") + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.linkManCode = rs.getString("FLinkManCode") + "";
                this.linkManName = rs.getString("FLinkManName") + "";
                this.relaCode = rs.getString("FRelaCode") + "";
                this.relaType = rs.getString("FRelaType") + "";
                this.relaGrade = rs.getString("FRelaGrade") + "";
                this.email = rs.getString("FEmail") + "";
                this.addr = rs.getString("FLinkAddr") + "";
                this.postCode = rs.getString("FPostalCode") + "";
                this.phone = rs.getString("FPhoneCode") + "";
                this.telephone = rs.getString("FTelephoneCode") + "";
                this.faxcode = rs.getString("FFaxCode") + "";
                this.desc = rs.getString("FDesc") + "";
                super.setRecLog(rs);
                buf1.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取联系人数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
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
        LinkManBean befEditBean = new LinkManBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            String sql = "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername from " + pub.yssGetTableName("tb_para_linkman") + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where a.FRelaType = " +
                dbl.sqlString(this.filterType.relaType) + " and a.FRelaCode = " +
                dbl.sqlString(this.oldlinkManCode) +
                " and a.FStartDate = " + dbl.sqlDate(this.filterType.startDate) +
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.linkManCode = rs.getString("FLinkManCode") + "";
                befEditBean.linkManName = rs.getString("FLinkManName") + "";
                befEditBean.relaCode = rs.getString("FRelaCode") + "";
                befEditBean.relaType = rs.getString("FRelaType") + "";
                befEditBean.relaGrade = rs.getString("FRelaGrade") + "";
                befEditBean.email = rs.getString("FEmail") + "";
                befEditBean.addr = rs.getString("FLinkAddr") + "";
                befEditBean.postCode = rs.getString("FPostalCode") + "";
                befEditBean.phone = rs.getString("FPhoneCode") + "";
                befEditBean.telephone = rs.getString("FTelephoneCode") + "";
                befEditBean.faxcode = rs.getString("FFaxCode") + "";
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

}
