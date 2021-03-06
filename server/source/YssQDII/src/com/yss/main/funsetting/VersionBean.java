package com.yss.main.funsetting;

import java.sql.*;

import com.yss.dbupdate.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.moneycontrol.TABean;
import com.yss.util.*;

public class VersionBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strGroupCode = "";
    private String strVerNum = "";
    private String strIssueDate = "";
    private String strFinish = "";
    private String strDesc = "";
    private String strCreateDate = "";
    private String strCreateTime = "";

    private VersionBean filterType;

    public String getGroupCode() {
        return this.strGroupCode;
    }

    public String getVerNum() {
        return this.strVerNum;
    }

    public String getIssueDate() {
        return this.strIssueDate;
    }

    public String getFinish() {
        return this.strFinish;
    }

    public String getDesc() {
        return this.strDesc;
    }

    public String getCreateDate() {
        return this.strCreateDate;
    }

    public String getCreateTime() {
        return this.strCreateTime;
    }

    public void setGroupCode(String groupCode) {
        this.strGroupCode = groupCode;
    }

    public void setVerNum(String verNum) {
        this.strVerNum = verNum;
    }

    public void setIssueDate(String issueDate) {
        this.strIssueDate = issueDate;
    }

    public void setFinish(String finish) {
        this.strFinish = finish;
    }

    public void setDesc(String desc) {
        this.strDesc = desc;
    }

    public void setCreateDate(String createDate) {
        this.strCreateDate = createDate;
    }

    public void setCreateTime(String createTime) {
        this.strCreateTime = createTime;
    }

    public VersionBean() {
    }

    public String buildRowStr() throws YssException {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.strGroupCode).append("\t");
        buffer.append(this.strVerNum).append("\t");
        buffer.append(this.strIssueDate).append("\t");
        buffer.append(this.strFinish).append("\t");
        buffer.append(this.strDesc).append("\t");
        buffer.append(this.strCreateDate).append("\t");
        buffer.append(this.strCreateTime).append("\t");
        buffer.append(super.buildRecLog());
        return buffer.toString();
    }

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
            this.strGroupCode = reqAry[0];
            this.strVerNum = reqAry[1];
            this.strIssueDate = reqAry[2];
            this.strFinish = reqAry[3];
            this.strDesc = reqAry[4];
            this.strCreateDate = reqAry[5];
            this.strCreateTime = reqAry[6];
          //========== add by yangheng  MS01654   QDV4赢时胜(测试)2010年8月25日02_B 
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VersionBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
          //=================
        } catch (Exception e) {
            throw new YssException("解析版本信息出错！", e);
        }
    }

    public String getOperValue(String sType) throws YssException {
        try {
            CtlDbUpdate dbUpdate = new CtlDbUpdate();
            dbUpdate.setYssPub(pub);
            if (this.strGroupCode == "Common") {
                dbUpdate.setCommVerNum(this.strVerNum);
                dbUpdate.updateCommon(false);
            } else {
                dbUpdate.setVersionNum(this.strVerNum);
                dbUpdate.updateGroupOnly(this.strGroupCode, false);
            }
        } catch (Exception e) {
            throw new YssException("手动更新数据库出错！", e);
        }
        return "";
    }

    public void checkInput(byte btOper) throws YssException {

    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
        String strSql = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "DELETE FROM TB_FUN_VERSION " +
                " WHERE FASSETGROUPCODE IN (SELECT FASSETGROUPCODE " +
                "          FROM TB_FUN_VERSION" +
                "         WHERE CAST(REPLACE(FVERNUM, '.', '') AS INTEGER) >= CAST(REPLACE(" + dbl.sqlString(this.strVerNum) + ", '.', '') AS INTEGER)" +
                "         AND FASSETGROUPCODE = " + dbl.sqlString(this.strGroupCode) + ")" +
                " AND FVERNUM IN (SELECT FVERNUM" +
                "          FROM TB_FUN_VERSION" +
                "         WHERE CAST(REPLACE(FVERNUM, '.', '') AS INTEGER) >= CAST(REPLACE(" + dbl.sqlString(this.strVerNum) + ", '.', '') AS INTEGER)" +
                "         AND FASSETGROUPCODE = " + dbl.sqlString(this.strGroupCode) + ")";
            conn.setAutoCommit(false);
            bTrans = true;

            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除失败版本信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

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
            strSql = "SELECT a.*, b.FVocName as FCIMTypeValue " +
                "FROM TB_FUN_VERSION a " +
                "LEFT JOIN Tb_Fun_Vocabulary b ON a.FFINISH = b.FVOCCODE  and b.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_VER_FINISH) + buildFilterSql();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.strGroupCode = rs.getString("FAssetGroupCode") + "";
                this.strVerNum = rs.getString("FVERNUM") + "";
                this.strIssueDate = rs.getString("FISSUEDATE") + "";
                this.strFinish = rs.getString("FFinish") + "";
                this.strDesc = rs.getString("FDESC") + "";
                this.strCreateDate = rs.getString("FCreateDate") + "";
                this.strCreateTime = rs.getString("FCreateTime") + "";
                //super.setRecLog(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_VER_FINISH);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取版本信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
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

    public String getTreeViewData4() throws YssException {
        return "";
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strGroupCode.length() != 0) {
                sResult = sResult + " and a.FAssetGroupCode like '" +
                    filterType.strGroupCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strVerNum.length() != 0) {
                sResult = sResult + " and a.FVerNum like '" + //modify by fangjiang 2010.10.11 MS01834 QDV4赢时胜(上海开发部)2010年10月09日01_B
                    filterType.strVerNum.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strIssueDate.length() != 0&&!this.filterType.strIssueDate.equalsIgnoreCase("9998-12-31")) {
            	//========== add by yangheng  MS01654   QDV4赢时胜(测试)2010年8月25日02_B 
                /*sResult = sResult + " and a.FAssetGroupCode like '" +
                    filterType.strIssueDate.replaceAll("'", "''") + "%'";*/
            	sResult = sResult + " and a.FIssueDate = " +"to_date('"+
                filterType.strIssueDate.replaceAll("'", "''") + "','yyyy-mm-dd')";
            	//=====================================
            }
            if (this.filterType.strFinish.length() != 0) {
            	//========== add by yangheng  MS01654   QDV4赢时胜(测试)2010年8月25日02_B 
                /*sResult = sResult + " and a.FFinish like '" +
                    filterType.strIssueDate.replaceAll("'", "''") + "%'";*/
            	if(!filterType.strFinish.equalsIgnoreCase("99"))
            	{
            		sResult = sResult + " and a.FFinish like '" +
                    filterType.strFinish.replaceAll("'", "''") + "'";
            	}
            	//=======================================
            }
            if (this.filterType.strDesc.length() != 0) {
            //========== add by yangheng  MS01654   QDV4赢时胜(测试)2010年8月25日02_B 
                /*sResult = sResult + " and a.FDESC like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";*/
                sResult = sResult + " and a.FDESC like '" +
                filterType.strDesc.replaceAll("'", "''") + "'";
            //=============================================
            }
            if (this.filterType.strCreateDate.length() != 0&&!this.filterType.strCreateDate.equalsIgnoreCase("9998-12-31")) {
                sResult = sResult + " and a.FCreateDate = " +"to_date('"+
                    filterType.strCreateDate.replaceAll("'", "''") + "','yyyy-mm-dd')";
            }
          //========== add by yangheng  MS01654   QDV4赢时胜(测试)2010年8月25日02_B 
           /* if (this.filterType.strCreateTime.length() != 0) {
                sResult = sResult + " and a.FCreateTime like '" +
                    filterType.strCreateTime.replaceAll("'", "''") + "%'";
            }*/
          //=====================
        }
        return sResult;
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
