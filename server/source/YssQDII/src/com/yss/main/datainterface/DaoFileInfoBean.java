package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class DaoFileInfoBean
    extends BaseDataSettingBean implements IDataSetting {
    private String cusCfgCode = ""; //接口代码
    private String cusCfgName = "";
    private String orderNum; //排序号
    private String loadRow; //读取行
    private String loadIndex; //读取位置
    private String loadLen; //读取长度
    private String tabField = ""; //对应表的字段
    private String fieldDesc = "";
    private String format = ""; //转换格式
    private String fileInfoDict = ""; //文件头字典
    private String fileInfoDictName = "";
    private String desc = ""; //描述
    private DaoFileInfoBean filterType;
    private String oldcusCfgCode = "";

    private String tabName = "";

    public DaoFileInfoBean() {
    }

    public String setTabName() {
        return tabName;
    }

    public void getTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getFileInfoDictName() {
        return fileInfoDictName;
    }

    public void setFileInfoDictName(String fileInfoDictName) {
        this.fileInfoDictName = fileInfoDictName;
    }

    public DaoFileInfoBean getFilterType() {
        return filterType;
    }

    public void setFilterType(DaoFileInfoBean filterType) {
        this.filterType = filterType;
    }

    public String getOldcusCfgCode() {
        return oldcusCfgCode;
    }

    public void setOldcusCfgCode(String oldcusCfgCode) {
        this.oldcusCfgCode = oldcusCfgCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFileInfoDict() {
        return fileInfoDict;
    }

    public void setFileInfoDict(String fileInfoDict) {
        this.fileInfoDict = fileInfoDict;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getTabField() {
        return tabField;
    }

    public void setTabField(String tabField) {
        this.tabField = tabField;
    }

    public String getLoadLen() {
        return loadLen;
    }

    public void setLoadLen(String loadLen) {
        this.loadLen = loadLen;
    }

    /* public String getloadLen()
      {
      return loadLen;
      }*/

    public String getLoadIndex() {
        return loadIndex;
    }

    public void setLoadIndex(String loadIndex) {
        this.loadIndex = loadIndex;
    }

    public String getLoadRow() {
        return loadRow;
    }

    public void setLoadRow(String loadRow) {
        this.loadRow = loadRow;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getCusCfgName() {
        return cusCfgName;
    }

    public void setCusCfgName(String cusCfgName) {
        this.cusCfgName = cusCfgName;
    }

    public String getCusCfgCode() {
        return cusCfgCode;
    }

    public void setCusCfgCode(String cusCfgCode) {
        this.cusCfgCode = cusCfgCode;
    }

    public String getListViewData1() throws YssException {
        String strSql =
            "select distinct y.* from " +
            "(select * from " +
            pub.yssGetTableName("Tb_Dao_FileInfo") + " " +
            " where FCheckState <> 2) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
            "  d.FFieldDesc as FFieldDesc,g.FCusCfgName as FCusCfgName,h.FDictName as FDictName from " +
            pub.yssGetTableName("Tb_Dao_FileInfo") + " a" +
            " left join (select FDictCode,FDictName from " + pub.yssGetTableName("Tb_Dao_Dict") + " ) h on a.FFileInfoDict = h.FDictCode " +
            " left join (select FCusCfgCode,FCusCfgName from " + pub.yssGetTableName("Tb_Dao_CusConfig") + " ) g on a.FCusCfgCode = g.FCusCfgCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join(select FFieldName,FFieldDesc from TB_FUN_DATADICT where  FTabName = " + dbl.sqlString(this.tabName) +
            " ) d on d.FFieldName=a.FTabFeild " +
            //   " left join Tb_Fun_Vocabulary f on a.FFormat = f.FVocCode and f.FVocTypeCode = " +
            //   dbl.sqlString(YssCons.YSS_FORMAT) +
            buildFilterSql() +
            ")y on y.FCusCfgCode=x.FCusCfgCode and y.FOrderNum=x.FOrderNum" +
            " order by y.FCusCfgCode,y.FOrderNum";
        return builderListViewData(strSql);

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
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setDaoFileInfoAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_FORMAT);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setDaoFileInfoAttr(ResultSet rs) throws YssException {
        try {

            this.cusCfgCode = rs.getString("FcusCfgCode");
            this.cusCfgName = rs.getString("FCusCfgName");
            this.orderNum = String.valueOf(rs.getInt("ForderNum"));
            this.loadRow = Integer.toString(rs.getInt("FloadRow"));
            this.loadIndex = Integer.toString(rs.getInt("FloadIndex"));
            this.loadLen = Integer.toString(rs.getInt("FloadLen"));
            this.tabField = rs.getString("FtabFeild");
            this.fieldDesc = rs.getString("FFieldDesc");
            this.format = rs.getString("FFormat");
            this.fileInfoDict = rs.getString("FfileInfoDict");
            this.fileInfoDictName = rs.getString("FDictName");
            this.desc = rs.getString("FDesc");
            this.orderNum = rs.getString("FOrderNum");
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.cusCfgCode.length() != 0) {
//           sResult = sResult + " and a.FCusCfgCode like '" +
                //filterType.cusCfgCode.replaceAll("'", "''") + "%'";
                sResult = sResult + " and a.FCusCfgCode = '" +
                    filterType.cusCfgCode.replaceAll("'", "''") + "'"; //这里取全名，不能模糊查询 by leeyu 20090226 QDV4赢时胜(上海)2008年12月19日01_B MS00113
            }

        }
        return sResult;
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
    public String getListViewData3() throws YssException {
        String strSql =
            "select * from " + pub.yssGetTableName("Tb_Dao_FileInfo") +
            " where 1=2"; //只是获取表头
        return builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        /* String strSql=
             "select y.* from " +
                "(select FVchTplCode,FEntityCode,FCheckState from " +
                pub.yssGetTableName("Tb_Vch_EntityResume") + " " +
                " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
                " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
                "  e.FDesc as FResumeFieldValue,d.FDictName as FResumeDictValue, f.FVocName as FValueTypeValue from " +
                pub.yssGetTableName("Tb_Vch_EntityResume") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join(select distinct FDictCode,FDictName from "+pub.yssGetTableName("Tb_Vch_Dict")+
                " ) d on d.FDictCode=a.FResumeDict"+
                " left join(select FAliasName,FDesc from "+pub.yssGetTableName("Tb_Vch_DsTabField")+
                " where FVchDsCode="+dbl.sqlString(this.dataSource)+") e on e.FAliasName=a.FResumeField "+
                " left join Tb_Fun_Vocabulary f on a.FValueType = f.FVocCode and f.FVocTypeCode = " +
                 dbl.sqlString(YssCons.YSS_VALUETYPE) +
                buildFilterSql() +
                ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
                " order by y.FVchTplCode,y.FEntityCode";
          return builderListViewData(strSql);*/
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        ResultSet rs = null;
        String strSql = "";
        PreparedStatement pstmt = null;
        int Num = 0;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                " where FCusCfgCode=" + dbl.sqlString(this.oldcusCfgCode);

            rs = dbl.openResultSet(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                "(FCusCfgCode,FOrderNum," +
                " FLoadRow,FLoadIndex,FLoadLen,FTabFeild,FFormat,FFileInfoDict,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(strSql);
            while (rs.next()) {
                Num += 1;
                super.parseRecLog();
                pstmt.setString(1, this.cusCfgCode);
                pstmt.setInt(2, Num);
                pstmt.setInt(3, rs.getInt("FLoadRow"));
                pstmt.setInt(4, rs.getInt("FLoadIndex"));
                pstmt.setInt(5, rs.getInt("FLoadLen"));
                pstmt.setString(6, rs.getString("FTabFeild"));
                pstmt.setString(7, rs.getString("FFormat"));
                pstmt.setString(8, rs.getString("FFileInfoDict"));
                pstmt.setString(9, rs.getString("FDesc"));
                pstmt.setInt(10, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(11, this.creatorCode);
                pstmt.setString(12, this.creatorTime);
                pstmt.setString(13, (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            return "";
        } catch (Exception e) {
            throw new YssException("新增文件头设置信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
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
            strSql = "update " + pub.yssGetTableName("Tb_Dao_FileInfo") + " set FCusCfgCode = " +
                dbl.sqlString(this.cusCfgCode) + ", FLoadRow = " +
                this.loadRow + " , FLoadIndex = " +
                this.loadIndex + ", FLoadLen = " +
                this.loadLen + ", FDesc = " +
                dbl.sqlString(this.desc) + ", FTabFeild =" +
                dbl.sqlString(this.tabField) + ", FFormat =" +
                dbl.sqlString(this.format) + ", FFileInfoDict =" +
                dbl.sqlString(this.fileInfoDict) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FCusCfgCode = " +
                dbl.sqlString(this.oldcusCfgCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新文件头设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("Tb_Dao_FileInfo") + " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode) +
                " and FOrderNum = " + this.orderNum;

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除文件头设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Dao_FileInfo") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode) +
                " and FOrderNum = " + this.orderNum;

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核文件头设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
        Connection con = dbl.loadConnection();
        String sql = "";
        String Num = "";
        try {

            /*Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                    "Tb_Dao_FileInfo"),
                                   dbl.sqlRight("FOrderNum", 1), "1",
                                   " where 1=1", 1);*/

            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            sql = "delete from " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                " where FCusCfgCode=" + dbl.sqlString(this.cusCfgCode);
            dbl.executeSql(sql);

            sql = "insert into " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                "(FCusCfgCode,FOrderNum," +
                " FLoadRow,FLoadIndex,FLoadLen,FTabFeild,FFormat,FFileInfoDict,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(sql);

            for (int i = 0; i < sMutilRowAry.length; i++) {

                this.parseRowStr(sMutilRowAry[i]);
                if (this.cusCfgCode.trim().length() > 0) {
                    pstmt.setString(1, this.cusCfgCode);
                    pstmt.setInt(2, i + 1);
                    pstmt.setInt(3, YssFun.toInt(this.loadRow));
                    pstmt.setInt(4, YssFun.toInt(this.loadIndex));
                    pstmt.setInt(5, YssFun.toInt(this.loadLen));
                    pstmt.setString(6, this.tabField);
                    pstmt.setString(7, this.format);
                    pstmt.setString(8, this.fileInfoDict);
                    pstmt.setString(9, this.desc);

                    pstmt.setInt(10, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(11, this.creatorCode);
                    pstmt.setString(12, this.creatorTime);
                    pstmt.setString(13, (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new YssException("保存文件头设置信息出错\r\n" + ex.getMessage());
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
    }

    /**
     * getSetting
     * modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
     * 将关闭数据库连接、不用的代码删除；将关闭游标方法切换到finally中
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException { // add liyu 1016 获取当前字段的Bean信息
        ResultSet rs = null;
        String reSql = "";
        try {
            reSql = "select * from " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                " where FCusCfgCode=" + dbl.sqlString(this.cusCfgCode) +
                " and FTabFeild=" + dbl.sqlString(this.tabField);
            rs = dbl.openResultSet(reSql);
            while (rs.next()) {
                this.loadRow = rs.getString("FLoadRow");
                this.loadIndex = rs.getString("FLoadIndex");
                this.loadLen = rs.getString("FLoadLen");
                this.cusCfgCode = rs.getString("FCusCfgCode");
                this.tabField = rs.getString("FTabFeild");
                this.format = rs.getString("FFormat");
                this.fileInfoDict = rs.getString("FFileInfoDict");
                this.desc = rs.getString("FDesc");
                this.orderNum = rs.getString("FOrderNum");
            }
        } catch (Exception e) {
            throw new YssException("解析文件头参数出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.cusCfgCode = reqAry[0];
            // this.orderNum=reqAry[1];
            this.loadRow = reqAry[1];
            this.loadIndex = reqAry[2];
            this.loadLen = reqAry[3];
            this.tabField = reqAry[4];
            this.format = reqAry[5];
            this.fileInfoDict = reqAry[6];
            this.desc = reqAry[7];
            this.checkStateId = YssFun.toInt(reqAry[8]);
            this.tabName = reqAry[9];
            this.oldcusCfgCode = reqAry[10];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoFileInfoBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析分录摘要信息出错");
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.cusCfgCode).append("\t");
        buf.append(this.cusCfgName).append("\t");
        // buf.append(this.orderNum).append("\t");
        buf.append(this.loadRow).append("\t");
        buf.append(this.loadLen).append("\t");
        buf.append(this.loadIndex).append("\t");
        buf.append(this.tabField).append("\t");
        buf.append(this.fieldDesc).append("\t");
        buf.append(this.format).append("\t");
        buf.append(this.fileInfoDict).append("\t");
        buf.append(this.fileInfoDictName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.orderNum).append("\t");
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
