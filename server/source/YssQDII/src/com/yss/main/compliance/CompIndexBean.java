package com.yss.main.compliance;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class CompIndexBean
    extends BaseDataSettingBean implements IDataSetting {

    private String indexTempCode = ""; //指标模版代码
    private String indexCode = ""; //指标代码
    private String indexName = ""; //指标名称
    private int bOrder; //下单前监控
    private int bCompWay; //监控方式
    private int aOrder; //下单后监控
    private int aCompWay; //监控方式
    private int endOfDay; //日终监控
    private String endCompGradeCode = ""; //监控等级代码
    private String endCompGradeName = ""; //监控等级名称
    private int rangeType; //范围类型
    private String UnPassHint = ""; //违规提示
    private String desc = "";
    private String bCompCond = ""; //事前监控条件
    private String compDealWay = ""; //监控处理方式

    private String oldIndexCode = "";
    private String oldIndexTempCode = "";
    private java.util.Date startDate; //启用日期
    private java.util.Date oldStartDate;
    private CompIndexBean filterType;
    private String hRecycled="";//add by nimengjing 2011.1.14 BUG #670 在回收站中不会显示删除后的监控指标设置信息 

    private String CompIndexCondition = "";
    public int getBCompWay() {
        return bCompWay;
    }

    public int getAOrder() {
        return aOrder;
    }

    public String getOldIndexCode() {
        return oldIndexCode;
    }

    public int getBOrder() {
        return bOrder;
    }

    public int getACompWay() {
        return aCompWay;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexTempCode() {
        return indexTempCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public void setBCompWay(int bCompWay) {
        this.bCompWay = bCompWay;
    }

    public void setAOrder(int aOrder) {
        this.aOrder = aOrder;
    }

    public void setOldIndexCode(String oldIndexCode) {
        this.oldIndexCode = oldIndexCode;
    }

    public void setBOrder(int bOrder) {
        this.bOrder = bOrder;
    }

    public void setACompWay(int aCompWay) {
        this.aCompWay = aCompWay;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setIndexTempCode(String indexTempCode) {
        this.indexTempCode = indexTempCode;
    }

    public void setUnPassHint(String UnPassHint) {
        this.UnPassHint = UnPassHint;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setEndOfDay(int endOfDay) {
        this.endOfDay = endOfDay;
    }

    public void setRangeType(int rangeType) {
        this.rangeType = rangeType;
    }

    public void setCompIndexCondition(String CompIndexCondition) {
        this.CompIndexCondition = CompIndexCondition;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public void setOldStartDate(java.util.Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public void setFilterType(CompIndexBean filterType) {
        this.filterType = filterType;
    }

    public void setOldIndexTempCode(String oldIndexTempCode) {
        this.oldIndexTempCode = oldIndexTempCode;
    }

    public void setEndCompGradeCode(String endCompGradeCode) {
        this.endCompGradeCode = endCompGradeCode;
    }

    public void setEndCompGradeName(String endCompGradeName) {
        this.endCompGradeName = endCompGradeName;
    }

    public void setCompDealWay(String compDealWay) {
        this.compDealWay = compDealWay;
    }

    public void setBCompCond(String bCompCond) {
        this.bCompCond = bCompCond;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getIndexCode() {
        return indexCode;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public String getUnPassHint() {
        return UnPassHint;
    }

    public String getDesc() {
        return desc;
    }

    public int getEndOfDay() {
        return endOfDay;
    }

    public int getRangeType() {
        return rangeType;
    }

    public java.util.Date getOldStartDate() {
        return oldStartDate;
    }

    public CompIndexBean getFilterType() {
        return filterType;
    }

    public String getOldIndexTempCode() {
        return oldIndexTempCode;
    }

    public String getEndCompGradeCode() {
        return endCompGradeCode;
    }

    public String getEndCompGradeName() {
        return endCompGradeName;
    }

    public String getCompDealWay() {
        return compDealWay;
    }

    public String getBCompCond() {
        return bCompCond;
    }

    public String getCompIndexCondition() {
        return CompIndexCondition;
    }

    public CompIndexBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.indexTempCode).append("\t");
        buf.append(this.indexCode).append("\t");
        buf.append(this.indexName).append("\t");
        buf.append(this.bOrder).append("\t");
        buf.append(this.bCompWay).append("\t");
        buf.append(this.aOrder).append("\t");
        buf.append(this.aCompWay).append("\t");
        buf.append(this.endOfDay).append("\t");
        buf.append(this.endCompGradeCode).append("\t");
        buf.append(this.rangeType).append("\t");
        buf.append(this.UnPassHint).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.startDate.toString()).append("\t");
        buf.append(this.endCompGradeName).append("\t");
        buf.append(this.compDealWay).append("\t");
        buf.append(this.bCompCond).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
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
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.CompIndexCondition = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            this.hRecycled=sRowStr;//add by nimengjing 2011.1.14 BUG #670 在回收站中不会显示删除后的监控指标设置信息
            reqAry = sTmpStr.split("\t");
            this.indexTempCode = reqAry[0];
            this.indexCode = reqAry[1];
            this.indexName = reqAry[2];
            if (reqAry[3].length() != 0) {
                this.bOrder = Integer.parseInt(reqAry[3]);
            }
            if (reqAry[4].length() != 0) {
                this.bCompWay = Integer.parseInt(reqAry[4]);
            }
            if (reqAry[5].length() != 0) {
                this.aOrder = Integer.parseInt(reqAry[5]);
            }
            if (reqAry[6].length() != 0) {
                this.aCompWay = Integer.parseInt(reqAry[6]);
            }
            if (reqAry[7].length() != 0) {
                this.endOfDay = Integer.parseInt(reqAry[7]);
            }
            this.endCompGradeCode = reqAry[8];
            if (reqAry[9].length() != 0) {
                this.rangeType = Integer.parseInt(reqAry[9]);
            }
            this.UnPassHint = reqAry[10];
            this.desc = reqAry[11];
            this.checkStateId = Integer.parseInt(reqAry[12]);
            this.startDate = YssFun.toDate(reqAry[13]);
            this.oldIndexCode = reqAry[14];
            this.oldIndexTempCode = reqAry[15];
            this.compDealWay = reqAry[16];
            this.bCompCond = reqAry[17];
            if (reqAry[17].length() == 0) {
                this.bCompCond = " ";
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CompIndexBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                    this.indexTempCode = this.filterType.indexTempCode;
                }
            }
        } catch (Exception e) {
            throw new YssException("解析监控指标出错", e);
        }
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Comp_Index"), "FIndexTempCode,FIndexCode",
                               this.indexTempCode + "," + this.indexCode,
                               this.oldIndexTempCode + "," + this.oldIndexCode);

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
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            "(select FIndexTempCode,FIndexCode,FCheckState from " + pub.yssGetTableName("Tb_Comp_Index") + " " +
            " where FIndexTempCode= " + dbl.sqlString(this.indexTempCode) +
            " and FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            "  group by FIndexTempCode,FIndexCode,FCheckState) x join" +//modify by nimengjing 2010.12.16 BUG #670 在回收站中不会显示删除后的监控指标设置信息 
            " (select a.*,mm.FGradeName as EndCompGradeName,f.FVocName as FBOrderValue," +
            //---------------------------------------------------------------------------------------
            " g.FVocName as FBCompWayValue, h.FVocName as FAOrderValue ," +
            " i.FVocName as FACompWayValue , j.FVocName as FEndOfDayValue , k.FVocName as FRangeTypeValue ," +
            " l.FVocName as FCompDealWayValue , " +
            //---------------------------------------------------------------------------------------
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_Index") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +

            " left join (select FGradeCode,FGradeName from " + pub.yssGetTableName("Tb_Comp_Grade") + ") mm on a.FEndCompGrade = mm.FGradeCode" +
            //---------------------------------------------------------------------------------------------------lzp   modify  2007-12-13 与DB2 匹配
            " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FBOrder") + " = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_BORDER) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FBCompWay") + " = g.FVocCode and g.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_BCOMPWAY) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FAOrder") + " = h.FVocCode and h.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_AORDER) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary i on " + dbl.sqlToChar("a.FACompWay") + " = i.FVocCode and i.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_ACOMPWAY) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary j on " + dbl.sqlToChar("a.FEndOfDay") + " = j.FVocCode and j.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_ENDOFDAY) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary k on " + dbl.sqlToChar("a.FRangeType") + " = k.FVocCode and k.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_RANGETYPE) +
            //-------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary l on a.FCompDealWay = l.FVocCode and l.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_RELAWAY) +
            buildFilterSql() +
            ") y on x.FIndexTempCode = y.FIndexTempCode and x.FIndexCode = y.FIndexCode" +
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    /**
     *
     * @param strSql String
     * @throws YssException
     * @return String
     */
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

                setCompIndexAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_CI_BORDER + "," +
                                        YssCons.YSS_CI_BCOMPWAY + "," +
                                        YssCons.YSS_CI_AORDER + "," +
                                        YssCons.YSS_CI_ACOMPWAY + "," +
                                        YssCons.YSS_CI_ENDOFDAY + "," +
                                        YssCons.YSS_CI_RANGETYPE + "," +
                                        YssCons.YSS_CI_RELAWAY);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取监控指标信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     *
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.indexTempCode.length() != 0) {
                sResult = sResult + " and a.FIndexTempCode like '" +
                    filterType.indexTempCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.indexCode.length() != 0) {
                sResult = sResult + " and a.FIndexCode like '" +
                    filterType.indexCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.indexName.length() != 0) {
                sResult = sResult + " and a.FIndexName like '" +
                    filterType.indexName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.bCompCond.trim().length() != 0) {
                sResult += " and a.FBCompCond like '" +
                    filterType.bCompCond.replaceAll("'", "''") + "%'";
            }

        }
        return sResult;
    }

    /**
     *
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setCompIndexAttr(ResultSet rs) throws SQLException {
        this.indexTempCode = rs.getString("FIndexTempCode") + "";
        this.indexCode = rs.getString("FIndexCode") + "";
        this.indexName = rs.getString("FIndexName") + "";
        this.bOrder = rs.getInt("FBOrder");
        this.bCompWay = rs.getInt("FBCompWay");
        this.aOrder = rs.getInt("FAOrder");
        this.aCompWay = rs.getInt("FACompWay");
        this.endOfDay = rs.getInt("FEndOfDay");
        this.endCompGradeCode = rs.getString("FEndCompGrade") + "";
        this.endCompGradeName = rs.getString("EndCompGradeName") + "";
        this.rangeType = rs.getInt("FRangeType");
        this.startDate = rs.getDate("FStartDate");
        this.UnPassHint = rs.getString("FUnPassHint");
        this.desc = rs.getString("FDesc") + "";
        this.compDealWay = rs.getString("FCompDealWay");
        this.bCompCond = rs.getString("FBCompCond") + "";
        super.setRecLog(rs);
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
        String strSql = "";
        strSql = "select y.* from " +
            "(select FIndexTempCode,FIndexCode,FCheckState from " + pub.yssGetTableName("Tb_Comp_Index") + " " +
            " where FIndexTempCode =" + dbl.sqlString(this.indexTempCode) +
            "  group by FIndexTempCode,FIndexCode,FCheckState) x join" +//modify by nimengjing 2011.1.14 BUG #670 在回收站中不会显示删除后的监控指标设置信息 
            " (select a.*,mm.FGradeName as EndCompGradeName,f.FVocName as FBOrderValue," +
            " g.FVocName as FBCompWayValue, h.FVocName as FAOrderValue ," +
            " i.FVocName as FACompWayValue , j.FVocName as FEndOfDayValue , k.FVocName as FRangeTypeValue ," +
            " l.FVocName as FCompDealWayValue ," +
            //----------------------------------------------------------------------------------------------------
            "  b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_Index") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +

            " left join (select FGradeCode,FGradeName from " + pub.yssGetTableName("Tb_Comp_Grade") + ") mm on a.FEndCompGrade = mm.FGradeCode" +
            //----------------------------------------------------------------------------------------------------lzp modify 20071213
            " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FBOrder") + " = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_BORDER) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FBCompWay") + " = g.FVocCode and g.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_BCOMPWAY) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FAOrder") + " = h.FVocCode and h.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_AORDER) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary i on " + dbl.sqlToChar("a.FACompWay") + " = i.FVocCode and i.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_ACOMPWAY) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary j on " + dbl.sqlToChar("a.FEndOfDay") + " = j.FVocCode and j.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_ENDOFDAY) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary k on " + dbl.sqlToChar("a.FRangeType") + " = k.FVocCode and k.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_RANGETYPE) +
            //------------------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary l on a.FCompDealWay = l.FVocCode and l.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CI_RELAWAY) +

            ") y on x.FIndexTempCode = y.FIndexTempCode and x.FIndexCode = y.FIndexCode " +
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
        strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_Index") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    /*
        public void saveSetting(byte btOper) throws YssException{

       Connection conn = dbl.loadConnection();
       boolean bTrans = false;
       String strSql = "";
       try {
//--------------------------------------------------Add------------------------------------------------------------
       if (btOper == YssCons.OP_ADD) {
         strSql =
               "insert into " + pub.yssGetTableName("Tb_Comp_Index") + "(FIndexTempCode,FIndexCode," +
               "FIndexName,FBOrder ,FBCompWay,FAOrder,FACompWay,FEndOfDay,FEndCompGrade,FRangeType,FCompDealWay,FStartDate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
               " values(" + dbl.sqlString(this.indexTempCode) + "," +
               dbl.sqlString(this.indexCode) + "," +
               dbl.sqlString(this.indexName) + "," +
               this.bOrder + "," +
               this.bCompWay + "," +
               this.aOrder + "," +
               this.aCompWay + "," +
               this.endOfDay + "," +
               dbl.sqlString(this.endCompGradeCode) + "," +
               this.rangeType + "," +
               dbl.sqlString(this.compDealWay) + "," +
               dbl.sqlDate(this.startDate) + "," +
               dbl.sqlString(this.desc) + "," +
               (pub.getSysCheckState()?"0":"1") + "," +
               dbl.sqlString(this.creatorCode) + "," +
               dbl.sqlString(this.creatorTime) + "," +
               (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
      }
//--------------------------------------------Edit---------------------------------------------------
      else if (btOper == YssCons.OP_EDIT) {
         strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") + " set FIndexTempCode= " +
               dbl.sqlString(this.indexTempCode) + ", FIndexCode= " +
               dbl.sqlString(this.indexCode) + ", FIndexName= " +
               dbl.sqlString(this.indexName) + ", FBOrder= " +
               this.bOrder + ", FBCompWay= " +
               this.bCompWay + ",FAOrder= " +
               this.aOrder + ", FACompWay= " +
               this.aCompWay +", FEndOfDay= " +
               this.endOfDay +",FEndCompGrade= " +
               dbl.sqlString(this.endCompGradeCode) + ", FRangeType= " +

               this.rangeType + ",FCompDealWay= " +
               dbl.sqlString(this.compDealWay) + ", FStartDate= " +
               dbl.sqlDate(this.startDate) + ", FDesc = " +
               dbl.sqlString(this.desc) + ", FCreator = " +
               dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
               dbl.sqlString(this.creatorTime) +
               " where FIndexCode = " + dbl.sqlString(this.oldIndexCode)+
               " and FIndexTempCode= " + dbl.sqlString(this.indexTempCode);

      }
//----------------------------------------------Del-----------------------------------------------------
      else if (btOper == YssCons.OP_DEL) {
         strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") + " set FCheckState = " +
               this.checkStateId + ", FCheckUser = " +
               dbl.sqlString(pub.getUserCode()) +
               ", FCheckTime = '" +
               YssFun.formatDatetime(new java.util.Date()) + "'" +
               " where FIndexCode = " + dbl.sqlString(this.indexCode) +
               " and FIndexTempCode= " + dbl.sqlString(this.indexTempCode);
        }
//--------------------------------------------------AUDIT---------------------------------------------------------
      else if (btOper == YssCons.OP_AUDIT) {
         strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") + " set FCheckState = " +
               this.checkStateId + ", FCheckUser = " +
               dbl.sqlString(pub.getUserCode()) +
               ", FCheckTime = '" +
               YssFun.formatDatetime(new java.util.Date()) + "'" +
               " where FIndexCode = " +
               dbl.sqlString(this.indexCode) + " and FStartDate=" +
               dbl.sqlDate(this.startDate);
      }
        conn.setAutoCommit(false);
        bTrans = true;
        dbl.executeSql(strSql);
//-------------------------------------子窗体操作------------------------------------
        //-------------------------------------Add/Edit---------------------------------------------
        if (btOper == YssCons.OP_ADD ) {
           if (this.CompIndexCondition != null && ! (this.CompIndexCondition.length() ==0)) {
              CompIndexCondBean indexCond=new CompIndexCondBean();
              indexCond.setYssPub(pub);
              indexCond.setIndexCode(this.indexCode);
              indexCond.saveMutliSetting(CompIndexCondition);
           }
        }
        if(btOper == YssCons.OP_EDIT)
        {
           if (this.CompIndexCondition != null && ! (this.CompIndexCondition.length() ==0)) {
           CompIndexCondBean indexCond=new CompIndexCondBean();
           indexCond.setYssPub(pub);
           indexCond.setIndexCode(this.oldIndexCode);
           indexCond.saveMutliSetting(CompIndexCondition);
        }

        }
        //-------------------------------------Del----------------------------------------------------
        else if (btOper == YssCons.OP_DEL) {
           strSql=
               "delete from " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
               " where FIndexCode = " + dbl.sqlString(this.indexCode);
               dbl.executeSql(strSql);
        }
        //-------------------------------------Audit---------------------------------------
        else if (btOper == YssCons.OP_AUDIT) {
           strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FCheckState = " +
                 this.checkStateId;
                 if (this.checkStateId == 1) {
                 strSql += ", FCheckUser = '" +
                    pub.getUserCode() + "' , FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'";
           }
           strSql += " where FIndexCode = " +
                 dbl.sqlString(this.indexCode);
           dbl.executeSql(strSql);
        }
        conn.commit();
        bTrans = false;
        conn.setAutoCommit(true);
       }
       catch (Exception e) {
      throw new YssException("设置监控指标信息出错！", e);
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
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Comp_Index") + "(FIndexTempCode,FIndexCode," +
                "FIndexName,FBOrder ,FBCompWay,FAOrder,FACompWay,FEndOfDay,FEndCompGrade,FRangeType,FCompDealWay,FStartDate,FUnPassHint,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FBCompCond) " +
                " values(" + dbl.sqlString(this.indexTempCode) + "," +
                dbl.sqlString(this.indexCode) + "," +
                dbl.sqlString(this.indexName) + "," +
                this.bOrder + "," +
                this.bCompWay + "," +
                this.aOrder + "," +
                this.aCompWay + "," +
                this.endOfDay + "," +
                dbl.sqlString(this.endCompGradeCode) + "," +
                this.rangeType + "," +
                dbl.sqlString(this.compDealWay) + "," +
                dbl.sqlDate(this.startDate) + "," +
                dbl.sqlString(this.UnPassHint) + ", " +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + " ," +
                dbl.sqlString(this.bCompCond) +
                ")";
            dbl.executeSql(strSql);
            if (this.CompIndexCondition != null && ! (this.CompIndexCondition.length() == 0)) {
                CompIndexCondBean indexCond = new CompIndexCondBean();
                indexCond.setYssPub(pub);
                indexCond.setIndexCode(this.indexCode);
                indexCond.saveMutliSetting(CompIndexCondition);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加监控指标信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    /**
     * checkSetting
     */

    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIndexCode = " +
                dbl.sqlString(this.indexCode) + " and FStartDate=" +
                dbl.sqlDate(this.startDate);
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                " set FCheckState = " +
                this.checkStateId;
            if (this.checkStateId == 1) {
                strSql += ", FCheckUser = '" +
                    pub.getUserCode() + "' , FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'";
            }
            strSql += " where FIndexCode = " +
                dbl.sqlString(this.indexCode);
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核监控指标信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIndexCode = " + dbl.sqlString(this.indexCode) +
                " and FIndexTempCode= " + dbl.sqlString(this.indexTempCode);
            dbl.executeSql(strSql);
            strSql =
                "delete from " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                " where FIndexCode = " + dbl.sqlString(this.indexCode);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除监控指标信息出错", e);
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
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Comp_Index") + " set FIndexTempCode= " +
                dbl.sqlString(this.indexTempCode) + ", FIndexCode= " +
                dbl.sqlString(this.indexCode) + ", FIndexName= " +
                dbl.sqlString(this.indexName) + ", FBOrder= " +
                this.bOrder + ", FBCompWay= " +
                this.bCompWay + ",FAOrder= " +
                this.aOrder + ", FACompWay= " +
                this.aCompWay + ", FEndOfDay= " +
                this.endOfDay + ",FEndCompGrade= " +
                dbl.sqlString(this.endCompGradeCode) + ", FRangeType= " +

                this.rangeType + ",FCompDealWay= " +
                dbl.sqlString(this.compDealWay) + ", FStartDate= " +
                dbl.sqlDate(this.startDate) + ",FUnPassHint= " +
                dbl.sqlString(this.UnPassHint) + ", FDesc = " +
                dbl.sqlString(this.desc) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                dbl.sqlString(this.creatorTime) + " , FBCompCond = " +
                dbl.sqlString(this.bCompCond) +
                " where FIndexCode = " + dbl.sqlString(this.oldIndexCode) +
                " and FIndexTempCode= " + dbl.sqlString(this.indexTempCode);

            dbl.executeSql(strSql);
            if (this.CompIndexCondition != null && ! (this.CompIndexCondition.length() == 0)) {
                CompIndexCondBean indexCond = new CompIndexCondBean();
                indexCond.setYssPub(pub);
                indexCond.setIndexCode(this.oldIndexCode);
                indexCond.setIndexTempCode(this.oldIndexTempCode);
                indexCond.saveMutliSetting(CompIndexCondition);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改监控指标信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

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
        CompIndexBean befEditBean = new CompIndexBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FIndexTempCode,FIndexCode,FCheckState from " + pub.yssGetTableName("Tb_Comp_Index") + " " +
                " where FIndexTempCode= " + dbl.sqlString(this.indexTempCode) +
                " and FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState <> 2 group by FIndexTempCode,FIndexCode,FCheckState) x join" +
                " (select a.*,mm.FGradeName as EndCompGradeName,f.FVocName as FBOrderValue," +
                //---------------------------------------------------------------------------------------
                " g.FVocName as FBCompWayValue, h.FVocName as FAOrderValue ," +
                " i.FVocName as FACompWayValue , j.FVocName as FEndOfDayValue , k.FVocName as FRangeTypeValue ," +
                " l.FVocName as FCompDealWayValue ," +
                //---------------------------------------------------------------------------------------
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_Index") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +

                " left join (select FGradeCode,FGradeName from " + pub.yssGetTableName("Tb_Comp_Grade") + ") mm on a.FEndCompGrade = mm.FGradeCode" +
                //---------------------------------------------------------------------------------------------------lzp  2007 12-13 modify-
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FBOrder") + " = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CI_BORDER) +
                //------------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FBCompWay") + " = g.FVocCode and g.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CI_BCOMPWAY) +
                //------------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FAOrder") + " = h.FVocCode and h.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CI_AORDER) +
                //------------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary i on " + dbl.sqlToChar("a.FACompWay") + " = i.FVocCode and i.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CI_ACOMPWAY) +
                //------------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary j on " + dbl.sqlToChar("a.FEndOfDay") + " = j.FVocCode and j.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CI_ENDOFDAY) +
                //------------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary k on " + dbl.sqlToChar("a.FRangeType") + " = k.FVocCode and k.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CI_RANGETYPE) +
                //-------------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary l on a.FCompDealWay = l.FVocCode and l.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CI_RELAWAY) +
                " where  FIndexCode =" + dbl.sqlString(this.oldIndexCode) +
                ") y on x.FIndexTempCode = y.FIndexTempCode and x.FIndexCode = y.FIndexCode" +
                " order by y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.indexTempCode = rs.getString("FIndexTempCode") + "";
                befEditBean.indexCode = rs.getString("FIndexCode") + "";
                befEditBean.indexName = rs.getString("FIndexName") + "";
                befEditBean.bOrder = rs.getInt("FBOrder");
                befEditBean.bCompWay = rs.getInt("FBCompWay");
                befEditBean.aOrder = rs.getInt("FAOrder");
                befEditBean.aCompWay = rs.getInt("FACompWay");
                befEditBean.endOfDay = rs.getInt("FEndOfDay");
                befEditBean.endCompGradeCode = rs.getString("FEndCompGrade") + "";
                befEditBean.endCompGradeName = rs.getString("EndCompGradeName") + "";
                befEditBean.rangeType = rs.getInt("FRangeType");
                befEditBean.startDate = rs.getDate("FStartDate");
                befEditBean.UnPassHint = rs.getString("FUnPassHint") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.compDealWay = rs.getString("FCompDealWay");

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
     * add by nimengjing 2011.1.14 BUG #670 在回收站中不会显示删除后的监控指标设置信息 
     */
    public void deleteRecycleData()throws YssException {
    	String strSql="";
    	boolean bTrans=false;//代表事务的开始
    	String[] arrData=null;
    	Connection conn=dbl.loadConnection();
    	try {
    		if ((!"".equals(hRecycled))&&hRecycled!=null) {
    			arrData=hRecycled.split("\r\n");
    			conn.setAutoCommit(false);
    			bTrans=true;
    			//循环删除多条数据
    			for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length()==0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql="delete from "+pub.yssGetTableName("Tb_Comp_Index")+
					" where FindexTempCode="+dbl.sqlString(this.indexTempCode)+
					" and FIndexCode="+dbl.sqlString(this.indexCode);
					dbl.executeSql(strSql);
				}
    			
			} 		
    		conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);	
		} catch (Exception e) {
			throw new YssException("清除监控指标设置信息出错!");
		}finally {
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
