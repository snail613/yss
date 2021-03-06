package com.yss.main.taoperation;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class TaSelNetBean
    extends BaseDataSettingBean implements IDataSetting {

    private String sellNetCode = ""; //销售网点代码
    private String sellNetName = ""; //销售网点名称
    private String sellNetType = ""; //销售网点类型
    private String sellNetTypeName = "";
    private String desc = ""; //描述

    private TaSelNetBean filterType;
    private String sRecycled = null; //保存未解析前的字符串

    private String oldSellNetCode = "";
    public String getDesc() {
        return desc;
    }

    public String getSellNetCode() {
        return sellNetCode;
    }

    public TaSelNetBean getFilterType() {
        return filterType;
    }

    public String getOldSellNetCode() {
        return oldSellNetCode;
    }

    public String getSellNetName() {
        return sellNetName;
    }

    public void setSellNetType(String sellNetType) {
        this.sellNetType = sellNetType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setSellNetCode(String sellNetCode) {
        this.sellNetCode = sellNetCode;
    }

    public void setFilterType(TaSelNetBean filterType) {
        this.filterType = filterType;
    }

    public void setOldSellNetCode(String oldSellNetCode) {
        this.oldSellNetCode = oldSellNetCode;
    }

    public void setSellNetName(String sellNetName) {
        this.sellNetName = sellNetName;
    }

    public void setSellNetTypeName(String sellNetTypeName) {
        this.sellNetTypeName = sellNetTypeName;
    }

    public String getSellNetType() {
        return sellNetType;
    }

    public String getSellNetTypeName() {
        return sellNetTypeName;
    }

    public TaSelNetBean() {
    }

    public String getListViewData1() throws YssException {
        String strSql =
            "select  y.* from " +
            "(select * from " +
            pub.yssGetTableName("Tb_TA_SellNet") + " " +
            //使前台回收站中能够显示被删除数据
            //" where FCheckState <> 2) x join" +
            ") x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
            "  f.FVocName as FVocName from " +
            pub.yssGetTableName("Tb_TA_SellNet") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FSellNetType"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary f on " +
            dbl.sqlToChar("a.FSellNetType") +
            " = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_TA_SellNetType) +
            buildFilterSql() +
            ")y on y.FSellNetCode=x.FSellNetCode " +
            " order by y.FSellNetCode";
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
                this.setSelNetAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_TA_SellNetType);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setSelNetAttr(ResultSet rs) throws YssException {
        try {
            //   this.cusCfgCode = rs.getString("FcusCfgCode");
            this.sellNetCode = rs.getString("FsellNetCode");
            this.sellNetName = rs.getString("FsellNetName");
            this.sellNetType = (rs.getInt("FsellNetType")) + "";
            this.sellNetTypeName = rs.getString("FVocName");
            this.desc = rs.getString("Fdesc");
            super.setRecLog(rs);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.sellNetCode.length() != 0) {
                sResult = sResult + " and a.FsellNetCode like '" +
                    filterType.sellNetCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sellNetName.length() != 0) {
                sResult = sResult + " and a.FsellNetName like '" +
                    filterType.sellNetName.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.sellNetType.equalsIgnoreCase("99") && this.filterType.sellNetType.length() != 0) { //2008-5-27 单亮 添加!this.filterType.sellNetType.equalsIgnoreCase("99")&&
                sResult = sResult + " and a.FsellNetType =" +
                    filterType.sellNetType;
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.Fdesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }

        }
        return sResult;
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = "销售网点代码\t销售网点名称\t销售网点描述";
            conn = dbl.loadConnection();
            sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,'' as FsellNetType,'' as FVocName from " +
                pub.yssGetTableName("Tb_TA_SellNet") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +

                " where a.FCheckState =1 order by a.FSellNetCode";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FSellNetCode")).append("\t");
                bufShow.append(rs.getString("FSellNetName")).append("\t"); //彭鹏 2008.2.3 修改
                bufShow.append(rs.getString("FDesc")).append(YssCons.
                    YSS_LINESPLITMARK);

                this.setSelNetAttr(rs);
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
            throw new YssException("获取TA销售网点设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strSql = "";

        return strSql;
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
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_SellNet"),
                               "FSellNetCode", this.sellNetCode,
                               this.oldSellNetCode);
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务

        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_TA_SellNet") +
                "(FSellNetCode,FSellNetName," +
                " FSellNetType,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" +
                dbl.sqlString(this.sellNetCode) + "," +
                dbl.sqlString(this.sellNetName) + "," +
                this.sellNetType + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? 0 : 1) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString( (pub.getSysCheckState() ? " " : this.creatorCode)) +
                ")";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增TA销售网点设置信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
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
            strSql = "update " + pub.yssGetTableName("Tb_TA_SellNet") +
                " set FSellNetCode = " +
                dbl.sqlString(this.sellNetCode) + ", FSellNetName = " +
                dbl.sqlString(this.sellNetName) + " , FSellNetType = " +
                this.sellNetType + ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FSellNetCode = " +
                dbl.sqlString(this.oldSellNetCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新TA销售网点设置信息出错", e);
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

            strSql = "update " + pub.yssGetTableName("Tb_TA_SellNet") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FSellNetCode = " + dbl.sqlString(this.sellNetCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA销售网点设置信息出错", e);
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
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null || ! ("").equalsIgnoreCase(sRecycled)) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_TA_SellNet") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSellNetCode = " + dbl.sqlString(this.sellNetCode);
                    dbl.executeSql(strSql);

                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (NullPointerException ex) {
            throw new YssException(ex.getMessage()); 
        } catch (Exception e) {
            throw new YssException("审核TA销售网点设置信息出错", e);
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            //this.cusCfgCode=reqAry[0];
            this.sellNetCode = reqAry[0];
            this.sellNetName = reqAry[1];
            this.sellNetType = reqAry[2];
            this.desc = reqAry[3];
            this.checkStateId = YssFun.toInt(reqAry[4]);
            this.oldSellNetCode = reqAry[5];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TaSelNetBean();
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
        buf.append(this.sellNetCode).append("\t");
        buf.append(this.sellNetName).append("\t");
        buf.append(this.sellNetType).append("\t");
        buf.append(this.sellNetTypeName).append("\t");
        buf.append(this.desc).append("\t");
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
                        pub.yssGetTableName("Tb_TA_SellNet") +
                        " where FSellNetCode = " + dbl.sqlString(this.sellNetCode);
                    dbl.executeSql(strSql);
                }
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
