package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchDataEntityBean
    extends BaseDataSettingBean implements IDataSetting {
    ///凭证子表
    private String vchNum = ""; //凭证编号
    private String entityNum = ""; //分录编号

    private String subjectCode = ""; //科目代码
    private String subjectName = ""; //
    private String resume = ""; //摘要
    private String dcWay = ""; //借贷方向
    private String bookSetCode = ""; //套帐代码
    private String bookSetName = ""; //
    private double bal = 0.0; //原币余额
    private double setBal = 0.0; //本位币金额
    private double amount = 0.0; //数量
    private double price = 0.0; //单价
    private String desc = ""; //描述
    private String oldVchNum = "";
    private String oldEntityNum = "";
    private String assistant = "";
    private String entityCode = ""; //凭证分录模板代码
    private boolean bSetBal; //是否设置了本位币
    private String calcWay = ""; //计算方式
    private String sAllow = ""; //保存凭证分录表中的字段FAllow信息 QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
    private double curyRate; //汇率
    private VchDataEntityBean filterType = null;
    public String getVchNum() {
        return vchNum;
    }

    public void setOldVchNum(String oldVchNum) {
        this.oldVchNum = oldVchNum;
    }

    public void setVchNum(String vchNum) {
        this.vchNum = vchNum;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setFilterType(VchDataEntityBean filterType) {
        this.filterType = filterType;
    }

    public void setBal(double bal) {
        this.bal = bal;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDcWay(String dcWay) {
        this.dcWay = dcWay;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public void setBookSetCode(String bookSetCode) {
        this.bookSetCode = bookSetCode;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setEntityNum(String entityNum) {
        this.entityNum = entityNum;
    }

    public void setBookSetName(String bookSetName) {
        this.bookSetName = bookSetName;
    }

    public void setSetBal(double setBal) {
        this.setBal = setBal;
    }

    public void setOldEntityNum(String oldEntityNum) {
        this.oldEntityNum = oldEntityNum;
    }

    public void setAssistant(String assistant) {
        this.assistant = assistant;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public void setBSetBal(boolean bSetBal) {
        this.bSetBal = bSetBal;
    }

    public void setCalcWay(String calcWay) {
        this.calcWay = calcWay;
    }

    public void setCuryRate(double curyRate) {
        this.curyRate = curyRate;
    }

    /**
     * 保存凭证分录表中的字段FAllow信息 QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
     * @param sAllow String
     */
    public void setSAllow(String sAllow) {
        this.sAllow = sAllow;
    }

    public String getOldVchNum() {
        return oldVchNum;
    }

    public String getDesc() {
        return desc;
    }

    public String getResume() {
        return resume;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public VchDataEntityBean getFilterType() {
        return filterType;
    }

    public double getBal() {
        return bal;
    }

    public double getPrice() {
        return price;
    }

    public String getDcWay() {
        return dcWay;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getBookSetCode() {
        return bookSetCode;
    }

    public double getAmount() {
        return amount;
    }

    public String getEntityNum() {
        return entityNum;
    }

    public String getBookSetName() {
        return bookSetName;
    }

    public double getSetBal() {
        return setBal;
    }

    public String getOldEntityNum() {
        return oldEntityNum;
    }

    public String getAssistant() {
        return assistant;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public boolean isBSetBal() {
        return bSetBal;
    }

    public String getCalcWay() {
        return calcWay;
    }

    public double getCuryRate() {
        return curyRate;
    }

    /**
     * 保存凭证分录表中的字段FAllow信息 QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
     * @return String
     */
    public String getSAllow() {
        return sAllow;
    }

    public VchDataEntityBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    private String builderfilter() {
        String sqlStr = "";
        if (this.filterType != null) {
            sqlStr = " where 1=1";
            if (this.filterType.vchNum.length() != 0) {
                sqlStr += " and a.FVchNum like '" + filterType.vchNum.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.entityNum.length() != 0) {
                sqlStr += " and a.FEntityNum like '" + filterType.entityNum.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.subjectCode.length() != 0) {
                sqlStr += " and a.FSubjectCode like '" + filterType.subjectCode.replaceAll("'", "''") + "%'";
            }
        }
        return sqlStr;
    }

    private void setVchDataEntity(ResultSet rs) throws SQLException, YssException {
        this.vchNum = rs.getString("FVchNum");
        this.entityNum = rs.getString("FEntityNum");
        this.subjectCode = rs.getString("FSubjectCode");
        //     this.subjectName = rs.getString("FSubjectName");
        this.resume = rs.getString("FResume");
        this.dcWay = rs.getString("FDCWay");
        this.bookSetCode = rs.getString("FBookSetCode");
        this.bookSetName = rs.getString("FBookSetName");
        this.bal = rs.getDouble("FBal");
        this.setBal = rs.getDouble("FSetBal");
        this.amount = rs.getDouble("FAmount");
        this.price = rs.getDouble("FPrice");
        this.desc = rs.getString("FDesc");
        //edit by songjie 2011.02.22 BUG:1548 QDV4易方达基金2011年3月21日01_B
        this.assistant = (rs.getString("FAssistant") == null) ? "" : rs.getString("FAssistant");
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
        try {
            conn = dbl.loadConnection();
            sHeader = getListView1Headers();
            sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FVocName as FDCWayName, " +
                //" d.FSubjectName as FSubjectName, e.FBookSetName as FBookSetName "+
                "e.FBookSetName as FBookSetName " +
                "from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " + //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
                //     " left join (select FSubjectCode,FSubjectName from "+pub.yssGetTableName("Tb_Vch_CodeSubjectDict")+") d on d.FSubjectCode=a.FSubjectCode "+
                " left join (select trim(to_char(FSetCode,'000')) as FBookSetCode,FSetName as FBookSetName from  lsetlist) e on e.FBookSetCode=a.FBookSetCode " +
                " left join Tb_Fun_Vocabulary e on a.FDCWay = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_DCWay) +
                this.builderfilter() +
                " order by a.FCheckState, a.FCreateTime desc, a.FCheckTime desc, a.FVchNum";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setVchDataEntity(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_DCWay);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取凭证分录数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
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
        Connection conn = null;
        boolean bTrans = false;
        String vchNumNo = "";
        String strSql = "";
        try {
            conn = dbl.loadConnection();
            if (this.vchNum.length() == 0) {
                vchNumNo = YssFun.formatDate(new java.util.Date(), "yyyyMMDD");
                this.vchNum = vchNumNo +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Vch_DataEntity"),
                                           dbl.sqlRight("FVchNum", 6), "000001",
                                           " where FVchNum like 'T"
                                           + vchNumNo + "%'", 1);
                this.vchNum = "T" + this.vchNum;
            }
            if (this.entityNum.length() == 0) {
                entityNum = dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Vch_DataEntity"),
                    dbl.sqlRight("FEntityNum", 6),
                    "000001");
            }

            strSql = "insert into " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " (FVchNum,FEntityNum,FSubjectCode,FCuryRate,FResume,FDCWay,FBookSetCode,FBal,FSetBal,FAmount" +
                " ,FPrice,FDesc,FCheckState,FCreator,FCreateTime,FAssistant) values(" +
                dbl.sqlString(this.vchNum) + "," +
                dbl.sqlString(this.entityNum) + "," +
                dbl.sqlString(this.subjectCode) + "," +
                1 + "," +
                dbl.sqlString(this.resume) + "," +
                dbl.sqlString(this.dcWay) + "," +
                dbl.sqlString(this.bookSetCode) + "," +
                this.bal + "," +
                this.setBal + "," +
                this.amount + "," +
                this.price + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                (pub.getSysCheckState() ? "' '" : this.creatorCode) + "," +
                "'" + YssFun.formatDatetime(new java.util.Date()) + "'," + 
                dbl.sqlString(this.assistant) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增凭证分录数据表出错", e);
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
    public void checkInput(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException { //此方法暂不用
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "update " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " set FCheckState=" + this.checkStateId + "," +
                " FCreator=" + dbl.sqlString(this.checkUserCode + " ") + "," +
                " FCreateTime=" + dbl.sqlString(this.checkTime + " ") +
                " where FVchNum=" + dbl.sqlString(this.vchNum) +
                " and FEntityNum= " + dbl.sqlString(this.oldEntityNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除凭证分录数据表出错", e);
        }

    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException { //此方法暂不用
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "update " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " set FVchNum=" + dbl.sqlString(this.vchNum) +
                ",FEntityNum=" + dbl.sqlString(this.entityNum) +
                ",FSubjectCode=" + dbl.sqlString(this.subjectCode) +
                ",FResume=" + dbl.sqlString(this.resume) +
                ",FDCWay=" + dbl.sqlString(this.dcWay) +
                ",FBookSetCode=" + dbl.sqlString(this.bookSetCode) +
                ",FBal=" + this.bal +
                ",FSetBal=" + this.setBal +
                ",FAmount=" + this.amount +
                ",FAssistant=" + this.assistant +
                ",FPrice=" + this.price +
                ",FDesc=" + dbl.sqlString(this.desc) +
                " where FVchNum=" + dbl.sqlString(this.oldVchNum) +
                " and FEntityNum =" + dbl.sqlString(this.oldEntityNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改凭证分录数据表出错", e);
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
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null;
        String sqlStr = "";
        String tmpNum = this.vchNum, tmpOldNum = this.oldVchNum;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            String[] sRowData = sMutilRowStr.split("\f\f");
            sqlStr = " delete from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " where FVchNum=" + dbl.sqlString(this.oldVchNum);
            dbl.executeSql(sqlStr);
            for (int i = 0; i < sRowData.length; i++) {
                this.parseRowStr(sRowData[i]);
                this.vchNum = tmpNum;
                this.oldVchNum = tmpOldNum;
                this.addSetting();
            }
        } catch (Exception e) {
            throw new YssException("保存错误", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
        buf.append(this.entityNum).append("\t");
        buf.append(this.subjectCode).append("\t");
        buf.append(this.subjectName).append("\t");
        buf.append(this.resume).append("\t");
        buf.append(this.dcWay).append("\t");
        buf.append(this.bookSetCode).append("\t");
        buf.append(this.bookSetName).append("\t");
        buf.append(this.bal).append("\t");
        buf.append(this.setBal).append("\t");
        buf.append(this.amount).append("\t");
        buf.append(this.price).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.checkStateId).append("\t");
        buf.append(this.vchNum).append("\t");
        buf.append(this.entityNum).append("\t");
        buf.append(this.assistant).append("\t");//add by songjie 2011.03.29 BUG:1548 QDV4易方达基金2011年3月21日01_B
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\f") >= 0) {
                sTmpStr = sRowStr.split("\r\f")[0];
            } else {
                sTmpStr = sRowStr;
                reqAry = sTmpStr.split("\t");
            }
          //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            if(reqAry == null)
            	return ;
            //---end---
            this.vchNum = reqAry[0];
            this.entityNum = reqAry[1];
            this.subjectCode = reqAry[2];
            this.subjectName = reqAry[3];
            this.resume = reqAry[4];
            this.dcWay = reqAry[5];
            this.bookSetCode = reqAry[6];
            this.bookSetName = reqAry[7];
            this.bal = YssFun.toDouble(reqAry[8]);
            this.setBal = YssFun.toDouble(reqAry[9]);
            this.amount = YssFun.toDouble(reqAry[10]);
            this.price = YssFun.toDouble(reqAry[11]);
            this.desc = reqAry[12];
            this.oldVchNum = reqAry[13];
            this.oldEntityNum = reqAry[14];
            this.assistant = reqAry[15];//add by songjie 2011.03.29 BUG:1548 QDV4易方达基金2011年3月21日01_B
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchDataEntityBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析凭证分录数据表出错!");
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
