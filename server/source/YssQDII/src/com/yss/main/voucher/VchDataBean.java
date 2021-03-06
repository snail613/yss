package com.yss.main.voucher;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
//import com.yss.vsub.*;
import com.yss.main.funsetting.*;
import com.yss.manager.VoucherAdmin;
import com.yss.util.*;

public class VchDataBean
    extends BaseDataSettingBean implements IDataSetting {

//凭证数据表

    private String vRecycled = ""; //回收站
    private String vchNum = ""; // 凭证编号
    private java.util.Date vchDate; //凭证日期
    private String portCode = ""; //组合代码
    private String portName = "";
    private String bookSetCode = ""; //套帐代码
    private String bookSetName = "";
    private String curyCode = ""; //货币代码
    private String curyName = "";
    private String srcCuryCode = "";
    private String srcCuryName = "";
    private double curyRate = 0.0; //汇率
    private String desc = ""; //描述
    private String oldVchNum = "";
    private String sSubDataEntity = ""; //保存 凭证分录内容
    private VchDataBean filterType = null;

    private String tplCode = ""; //模板代码
    private String tplName = ""; //模板名称

    private String multAuditString = "";
    ArrayList dataEntity = new ArrayList();

    private String startVchDate = "";
    private String endVchDate = "";
    private String strIsOnlyColumn = "0";
    public String getDesc() {
        return desc;
    }

    public String getCuryName() {
        return curyName;
    }

    public String getVchNum() {
        return vchNum;
    }

    public String getPortCode() {
        return portCode;
    }

    public VchDataBean getFilterType() {
        return filterType;
    }

    public String getStartVchDate() {
        return startVchDate;
    }

    public String getOldVchNum() {
        return oldVchNum;
    }

    public String getBookSetCode() {
        return bookSetCode;
    }

    public String getEndVchDate() {
        return endVchDate;
    }

    public String getStrIsOnlyColumn() {
        return strIsOnlyColumn;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getCuryRate() {
        return curyRate;
    }

    public String getBookSetName() {
        return bookSetName;
    }

    public String getSSubDataEntity() {
        return sSubDataEntity;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setVchNum(String vchNum) {
        this.vchNum = vchNum;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setFilterType(VchDataBean filterType) {
        this.filterType = filterType;
    }

    public void setStartVchDate(String startVchDate) {
        this.startVchDate = startVchDate;
    }

    public void setOldVchNum(String oldVchNum) {
        this.oldVchNum = oldVchNum;
    }

    public void setVchDate(Date vchDate) {
        this.vchDate = vchDate;
    }

    public void setBookSetCode(String bookSetCode) {
        this.bookSetCode = bookSetCode;
    }

    public void setEndVchDate(String endVchDate) {
        this.endVchDate = endVchDate;
    }

    public void setStrIsOnlyColumn(String strIsOnlyColumn) {
        this.strIsOnlyColumn = strIsOnlyColumn;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setCuryRate(double curyRate) {
        this.curyRate = curyRate;
    }

    public void setBookSetName(String bookSetName) {
        this.bookSetName = bookSetName;
    }

    public void setSSubDataEntity(String sSubDataEntity) {
        this.sSubDataEntity = sSubDataEntity;
    }

    public void setTplCode(String tplCode) {
        this.tplCode = tplCode;
    }

    public void setVchDate(java.util.Date vchDate) {
        this.vchDate = vchDate;
    }

    public void setDataEntity(ArrayList dataEntity) {
        this.dataEntity = dataEntity;
    }

    public void setSrcCuryCode(String srcCuryCode) {
        this.srcCuryCode = srcCuryCode;
    }

    public void setSrcCuryName(String srcCuryName) {
        this.srcCuryName = srcCuryName;
    }

    public void setMultAuditString(String multAuditString) {
        this.multAuditString = multAuditString;
    }

    public String getPortName() {
        return portName;
    }

    public String getTplCode() {
        return tplCode;
    }

    public java.util.Date getVchDate() {
        return vchDate;
    }

    public ArrayList getDataEntity() {
        return dataEntity;
    }

    public String getSrcCuryCode() {
        return srcCuryCode;
    }

    public String getSrcCuryName() {
        return srcCuryName;
    }

    public String getMultAuditString() {
        return multAuditString;
    }

    public VchDataBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    private void setVchData(ResultSet rs) throws SQLException, YssException {
        this.vchDate = rs.getDate("FVchDate");
        this.vchNum = rs.getString("FVchNum");
        this.portCode = rs.getString("FPortCode");
        this.portName = rs.getString("FPortName");
        this.bookSetCode = rs.getString("FBookSetCode");
        this.bookSetName = rs.getString("FBookSetName");
        this.curyCode = rs.getString("FCuryCode");
        this.curyName = rs.getString("FCuryName");
        this.curyRate = rs.getDouble("FCuryRate");
        this.desc = rs.getString("FDesc");
        this.tplCode = rs.getString("FVchTplCode");
        this.tplName = rs.getString("FVchTplName");
        super.setRecLog(rs);
    }

    public String getListViewData1() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        StringBuffer leftBuf = new StringBuffer();
        try {
            conn = dbl.loadConnection();
            sHeader = getListView1Headers();
            sqlStr = "select y.* from ( " +
                " select a.*, " +
                " b.FUserName as FCreatorName," +
                " c.FUserName as FCheckUserName, " +
                " d.FPortName as FPortName, " +
                " e.FBookSetName as FBookSetName, " +
                " f.FCuryName as FCuryName , " +
                " g.FVchTplName as FVchTplName " +
                //  " g.FDCWay as FDCWay " +	
                " from " + pub.yssGetTableName("tb_vch_data") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                //edited by zhouxiang MS01345---------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
         
                " left join (select FPortCode,FPortName from " +pub.yssGetTableName("Tb_Para_Portfolio") +
                " where fcheckstate=1 "+
                //end by lidaolong
                //---------------end------------------
                " ) d on a.fportcode =d.FPortCode " ;
                if(YssCons.YSS_VCH_BUILDER_MODE.equalsIgnoreCase("batch")){
                	leftBuf.append("  left join ");//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
        			leftBuf.append(" (select fsetid,fsetname as FBookSetName,trim(to_char(FSetCode,'000')) as FBookSetCode from lsetlist where fyear=").append(this.filterType!=null?YssFun.formatDate(this.filterType.endVchDate, "yyyy"):"((select max(fyear) from lsetlist)");
        			leftBuf.append(" ) e on a.Fbooksetcode = e.FBookSetCode ");
                	
                }else{
                	leftBuf.append("  left join ");//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
                	leftBuf.append(" (select trim(to_char(FSetCode,'000')) as FBookSetCode,FSetName FBookSetName from  lsetlist ");
                	leftBuf.append(" ) e on e.FBookSetCode =a.Fbooksetcode ");
                	
                }
                
                sqlStr +=leftBuf.toString()+
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " ) f on f.FCuryCode =a.FCuryCode " +
                " left join (select FVchTplCode,FVchTplName from " +
                pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FCheckState=1)g on g.FVchTplCode=a.FVchTplCode" +
                " order by FVchNum ) y " +
                this.buildFilterSql() +
                " order by y.FCheckTime desc,y.FCreateTime desc,y.FCheckState";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setVchData(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_DCWay);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {

        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String sqlStr = "", vchNumNo = "";
        boolean bTrans = false;
        //---add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        VoucherAdmin vchAdmin = new VoucherAdmin();
        vchAdmin.setYssPub(pub);
        //---add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
        	//---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//            if (this.vchNum.trim().length() == 0) {
//                vchNumNo = YssFun.formatDate(new java.util.Date(), "yyyyMMdd");
//                this.vchNum = vchNumNo +
//                    dbFun.getNextInnerCode(pub.yssGetTableName(
//                        "Tb_Vch_Data"),
//                                           dbl.sqlRight("FVchNum", 6), "000000",
//                                           " where FVchNum like 'T"
//                                           + vchNumNo + "%'", 1);
//                this.vchNum = "T" + this.vchNum;
//            }
        	//---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            
            //add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
            this.vchNum = vchAdmin.getNum();
            
            sqlStr = " insert into  " + pub.yssGetTableName("Tb_Vch_Data") +
                " (FVchNum,FVchDate,FPortCode,FBookSetCode,FCuryCode,FCuryRate,FCheckState,FCreator,FCreateTime )" +
                " values( " +
                dbl.sqlString(this.vchNum) + "," +
                dbl.sqlDate(this.vchDate) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.bookSetCode) + "," +
                dbl.sqlString(this.curyCode) + "," +
                this.curyRate + "," +
                this.checkStateId + "," +
                dbl.sqlString(pub.getUserCode()) + ",'" +
                YssFun.formatDatetime(new java.util.Date()) +
                "') ";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            String[] Tmp = this.sSubDataEntity.split("\t");
            if (this.sSubDataEntity.trim().length() != 0 &&
                Tmp[2].trim().length() != 0) {
                VchDataEntityBean entity = new VchDataEntityBean();
                entity.setVchNum(this.vchNum);
                entity.setOldVchNum(this.oldVchNum);
                entity.setYssPub(pub);
                entity.saveMutliSetting(sSubDataEntity);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增凭证数据表出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        /*   dbFun.checkInputCommon(btOper,
                                  pub.yssGetTableName("Tb_Vch_Data"),
                                  "FVchNum",
                                  this.vchNum,
                                  this.oldVchNum);
         */
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String sqlStr = "";
        boolean bTrans = false;
        try {
            if (multAuditString.length() > 0) {
                this.auditMutli(this.multAuditString); //执行批量审核/反审核
            } else {
                //=====修改回收站批量还原功能，BUG：0000491
                conn.setAutoCommit(false);
                String[] arrData = vRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData.length == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    sqlStr = " update " + pub.yssGetTableName("Tb_Vch_Data") +
                        " set FCheckState=" + this.checkStateId +
                        " ,FCheckUser=" + dbl.sqlString(pub.getUserCode()) +
                        " ,FCheckTime='" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FVchNum =" + dbl.sqlString(this.vchNum);
                    dbl.executeSql(sqlStr);
                    //添加对凭证数据处理的功能　QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                    sqlStr = " update " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                        " set FCheckState=" + this.checkStateId +
                        " ,FCheckUser=" + dbl.sqlString(pub.getUserCode()) +
                        " ,FCheckTime='" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FVchNum =" + dbl.sqlString(this.vchNum);
                    dbl.executeSql(sqlStr);
                }
                //======2008-10-21
                bTrans = true;
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("审核出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting方法
     * 功能：执行删除操作，将数据放到回收站中，并未从数据库中删除。
     * @throws YssException
     * 时间：2008年8月8日
     * 修改人：Mao Qiwen
     */
    public void delSetting() throws YssException {
        Connection conn = null;
        String sqlStr = "";
        //ResultSet rs = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            if (multAuditString.length() > 0) {
                this.Mutlidel(this.multAuditString); //执行批量删除
            } else {
                sqlStr = " update " + pub.yssGetTableName("Tb_Vch_Data") +
                    " set FCheckState= " + this.checkStateId + "," +
                    " FCreator=" + dbl.sqlString(this.checkUserCode + " ") + "," +
                    " FCreateTime=" + dbl.sqlString(this.checkTime + " ") +
                    " where FVchNum =" + dbl.sqlString(this.oldVchNum);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(sqlStr);
                //===== 将 delete 改为update BUG:000491
                //sqlStr = " delete from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                sqlStr = " update  " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                    " set FCheckState= " + this.checkStateId + "," +
                    " FCreator=" + dbl.sqlString(this.checkUserCode == null ? " " : this.checkUserCode) + "," +
                    " FCreateTime=" + dbl.sqlString(this.checkTime == null ? " " : this.checkTime) +
                    " where FVchNum=" + dbl.sqlString(this.oldVchNum);
                //======= 2008-10-27 by leeyu
                dbl.executeSql(sqlStr);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("删除错误", e);
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
        Connection conn = null;
        String sqlStr = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            sqlStr = " update " + pub.yssGetTableName("Tb_Vch_Data") +
                " set FVchDate=" + dbl.sqlDate(this.vchDate) +
                " ,FPortCode=" + dbl.sqlString(this.portCode) +
                " ,FBookSetCode=" + dbl.sqlString(this.bookSetCode) +
                " ,FCuryCode=" + dbl.sqlString(this.curyCode) +
                " ,FCuryRate=" + this.curyRate +
                " where FVchNum =" + dbl.sqlString(this.vchNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            String[] Tmp = this.sSubDataEntity.split("\t");
            if (this.sSubDataEntity.trim().length() != 0 &&
                Tmp[2].trim().length() != 0) {
                VchDataEntityBean entity = new VchDataEntityBean();
                entity.setVchNum(this.vchNum);
                entity.setOldVchNum(this.oldVchNum);
                entity.setYssPub(pub);
                entity.saveMutliSetting(sSubDataEntity);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改凭证数据表出错");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
        buf.append(this.vchNum).append("\t");
        //edit by songjie 2011.10.17 BUG 2783 QDV4招商基金2011年09月15日01_B
        buf.append(YssFun.formatDate(this.vchDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.bookSetCode).append("\t");
        buf.append(this.bookSetName).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.curyRate).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.checkStateId).append("\t");
        buf.append(this.tplCode).append("\t");
        buf.append(this.tplName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        String sMutiAudit = "";
        String sRowstr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) { //判断是否有批量数据，以用来批量处理审核/反审核 2007-09-28
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
                multAuditString = sMutiAudit;
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];

                if (sRowStr.indexOf("\r\t") >= 0) {
                    sTmpStr = sRowStr.split("\r\t")[0];
                    if (sRowStr.split("\r\t").length == 3) {
                        this.sSubDataEntity = sRowStr.split("\r\t")[2];
                    }
                } else {
                    sTmpStr = sRowStr;
                }
                reqAry = sTmpStr.split("\t");
                this.vchNum = reqAry[0];
                if (reqAry[0].length() == 0) {
                    this.vchNum = " ";
                }
                this.vRecycled = sTmpStr; //用这个，这里将数据赋值给这个变量，待清除还原时处理 by leeyu 2008-10-21 BUG:0000491
                this.vchDate = YssFun.toDate(reqAry[1]);
                this.portCode = reqAry[2];
                this.portName = reqAry[3];
                this.bookSetCode = reqAry[4];
                this.bookSetName = reqAry[5];
                this.curyCode = reqAry[6];
                this.curyName = reqAry[7];
                this.curyRate = YssFun.toDouble(reqAry[8]);
                this.desc = reqAry[9];
                this.checkStateId = Integer.parseInt(reqAry[10]);
                this.oldVchNum = reqAry[11];
                this.startVchDate = reqAry[12];
                this.endVchDate = reqAry[13];
                this.strIsOnlyColumn = reqAry[14];
                super.parseRecLog();
                if (sRowStr.indexOf("\r\t") >= 0) {
                    if (this.filterType == null) {
                        this.filterType = new VchDataBean();
                        this.filterType.setYssPub(pub);
                    }
                    if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                        this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                    }

                }
            } else {
                if (sRowStr.indexOf("\r\t") >= 0) {
                    sTmpStr = sRowStr.split("\r\t")[0];
                    if (sRowStr.split("\r\t").length == 3) {
                        this.sSubDataEntity = sRowStr.split("\r\t")[2];
                    }
                } else {
                    sTmpStr = sRowStr;
                }
                vRecycled = sTmpStr; //用这个 判断是否有批量数据，以用来批量处理审核/反审核 2007-09-28 BUG：0000491
                reqAry = sTmpStr.split("\t");
                this.vchNum = reqAry[0];
                if (reqAry[0].length() == 0) {
                    this.vchNum = " ";
                }
                this.vchDate = YssFun.toDate(reqAry[1]);
                this.portCode = reqAry[2];
                this.portName = reqAry[3];
                this.bookSetCode = reqAry[4];
                this.bookSetName = reqAry[5];
                this.curyCode = reqAry[6];
                this.curyName = reqAry[7];
                this.curyRate = YssFun.toDouble(reqAry[8]);
                this.desc = reqAry[9];
                this.checkStateId = Integer.parseInt(reqAry[10]);
                this.oldVchNum = reqAry[11];
                this.startVchDate = reqAry[12];
                this.endVchDate = reqAry[13];
                this.strIsOnlyColumn = reqAry[14];
                super.parseRecLog();
                if (sRowStr.indexOf("\r\t") >= 0) {
                    if (this.filterType == null) {
                        this.filterType = new VchDataBean();
                        this.filterType.setYssPub(pub);
                    }
                    if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                        this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                    }
                    // this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析凭证数据表出错!");
        }

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult += " where 1=1";
            if (this.filterType.strIsOnlyColumn.equals("1")) { // wdy add 20070903 添加表别名y
                sResult = sResult + " and 1 = 2";
            }
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and y.FPortCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bookSetCode.length() != 0) {
                sResult = sResult + " and y.FBookSetCode like '" +
                    filterType.bookSetCode.replaceAll("'", "''") + "%'";
            }

            if (filterType.startVchDate.length() != 0) {
                sResult = sResult + " and y.FVchDate between  " +
                    dbl.sqlDate(filterType.startVchDate) +
                    " and " + dbl.sqlDate(filterType.endVchDate);
            }
        }
        //BugNo:0000452 edit by jc  删除时刷新页面不显示数据，故注释掉此代码
        //else {
        //   sResult = " where 1=2 ";
        //}
        //----------------------jc
        return sResult;
    }

    /**
     * auditMutli
     * 执行批量审核
     * @return String
     */

    public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null;
        String sqlStr = "";
        java.sql.PreparedStatement psmt = null;
        java.sql.PreparedStatement psmtEntity = null; //定义一个凭证数据处理的预处理 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
        boolean bTrans = false;
        VchDataBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = " update " + pub.yssGetTableName("Tb_Vch_Data") +
                " set FCheckState=" + this.checkStateId +
                " ,FCheckUser=" + dbl.sqlString(pub.getUserCode()) +
                " ,FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FVchNum = ?";
            psmt = conn.prepareStatement(sqlStr);
            //添加凭证数据批量处理功能 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
            sqlStr = " update " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " set FCheckState=" + this.checkStateId +
                " ,FCheckUser=" + dbl.sqlString(pub.getUserCode()) +
                " ,FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FVchNum = ?";
            psmtEntity = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new VchDataBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.vchNum);
                        psmt.addBatch();
                        psmtEntity.setString(1, data.vchNum); //添加凭证数据处理的SQL QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                        psmtEntity.addBatch();
                    }
                }
                conn.setAutoCommit(false);
                bTrans = true;
                psmt.executeBatch();
                psmtEntity.executeBatch(); //执行命令语句 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("批量审核凭证数据表出错!");
        } finally {
            dbl.closeStatementFinal(psmt, psmtEntity); //关闭命令预处理 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * Mutlidel
     * 执行批量删除
     * @return String
     */

    public String Mutlidel(String sMutilRowStr) throws YssException { //sj add 批量删除数据
        Connection conn = null;
        String sqlStr = "";
        String sqlStrDel = "";
        java.sql.PreparedStatement psmt = null;
        java.sql.PreparedStatement psmtDel = null;
        boolean bTrans = false;
        VchDataBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = " update " + pub.yssGetTableName("Tb_Vch_Data") +
                " set FCheckState=" + this.checkStateId +
                " ,FCheckUser=" + dbl.sqlString(pub.getUserCode()) +
                " ,FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FVchNum = ?";
            //===========将delete 改为update，Bug：000491 by leeyu
            //sqlStrDel = " delete from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
            sqlStrDel = " update " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " set FCheckState=" + this.checkStateId +
                " ,FCheckUser=" + dbl.sqlString(pub.getUserCode()) +
                " ,FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                //========用update ,现回收站可以处理这种数据了
                " where FVchNum= ?";
            psmt = conn.prepareStatement(sqlStr);
            psmtDel = conn.prepareStatement(sqlStrDel);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new VchDataBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.vchNum);
                        psmt.addBatch();
                        //------------------------------------------
                        psmtDel.setString(1, data.vchNum);
                        psmtDel.addBatch();
                    }
                }
                conn.setAutoCommit(false);
                bTrans = true;
                psmt.executeBatch();
                psmtDel.executeBatch();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("批量删除凭证数据表出错!");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * deleteRecycleData方法
     * 功能：实现回收站中的“清除”功能；此操作将数据直接从数据库中删除。
     * @throws YssException
     * 时间：2008年8月8号
     * 修改人：Mao Qiwen
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        //ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //如果vRecycled不为空，就按解析vRecycled中的字符串，然后一个一个来执行sql语句
            if (vRecycled != null && vRecycled.length() != 0) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = vRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Vch_Data") +
                        " where FVchNum = " + dbl.sqlString(this.vchNum);
                    dbl.executeSql(strSql);
                    //conn.commit();
                    //添加对凭证数据表的删除条件 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Vch_DataEntity") +
                        " where FVchNum = " + dbl.sqlString(this.vchNum);
                    dbl.executeSql(strSql);
                }
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void exMultiChe(String beginDate, String endDate, String portCodes,
                           String vchAttrs) throws YssException {
        java.sql.Connection conn = dbl.loadConnection();
        String sqlStr = "";
        boolean bTrans = false;
        try {
            sqlStr = "update " + pub.yssGetTableName("Tb_Vch_Data") +
                " set FCheckState = 1 " +
                " ,FCheckUser=" + dbl.sqlString(pub.getUserCode()) +
                " ,FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FVchDate between " + dbl.sqlDate(beginDate) + " and " +
                dbl.sqlDate(endDate) + " and FPortCode in (" +
                operSql.sqlCodes(portCodes) +
                ") and FVchTplCode in (" +
                operSql.sqlCodes(getVchTplCodes(beginDate, endDate, vchAttrs)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            //添加对凭证数据表的审核处理 QDV4赢时胜（上海）2009年4月10日01_B MS00373 by leeyu 20090414
            sqlStr = "update " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " set FCheckState = 1 " +
                " ,FCheckUser=" + dbl.sqlString(pub.getUserCode()) +
                " ,FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FVchDate between " + dbl.sqlDate(beginDate) + " and " +
                dbl.sqlDate(endDate) + " and FPortCode in (" +
                operSql.sqlCodes(portCodes) +
                ") and FVchTplCode in (" +
                operSql.sqlCodes(getVchTplCodes(beginDate, endDate, vchAttrs)) + ")";
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {

        }
    }

    public String getVchTplCodes(String beginDate, String endDate,
                                 String vchAttrs) throws YssException {
        String reVchTpl = "";
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = " select FVchTplCode,FAttrCode from " +
                pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FAttrCode in (" + this.operSql.sqlCodes(vchAttrs) +
                ") and FCheckState = 1";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                reVchTpl += rs.getString("FVchTplCode") + ",";
            }
            if (reVchTpl.length() > 0) {
                reVchTpl = reVchTpl.substring(0, reVchTpl.length() - 1);
            }
            return reVchTpl;
        } catch (Exception e) {
            throw new YssException("获取凭证模板号出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
