package com.yss.main.compliance;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.pojo.param.comp.*;
import com.yss.util.*;

public class CompIndexCondBean
    extends BaseDataSettingBean implements IDataSetting {

    private String indexTempCode = ""; //指标模版代码
    private String indexCode = ""; //指标代码
    private String conNum = ""; //条件编号
    private int compType; //监控类型
    private int attrType; //属性类型
    private String attrCode = ""; //属性代码
    private String attrName = ""; //属性名称
    private String sign = ""; //符号
    private String value = ""; //监控值
    private String denominaAttrCode = ""; //分母属性代码
    private String denominaAttrName = ""; //分母属性名称
    private String conRela; //关系
    private String desc = "";
    private String oldConNum = ""; //旧条件编号
    private String oldIndexCode = ""; //旧指标代码

    private YssCompAttrParam attrParam;
    private YssCompAttrParam attrDenParam; //分母属性

    private CompIndexCondBean filterType;
    public String getSign() {
        return sign;
    }

    public int getCompType() {
        return compType;
    }

    public CompIndexCondBean getFilterType() {
        return filterType;
    }

    public String getIndexTempCode() {
        return indexTempCode;
    }

    public String getConNum() {
        return conNum;
    }

    public String getConRela() {
        return conRela;
    }

    public String getValue() {
        return value;
    }

    public int getAttrType() {
        return attrType;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setCompType(int compType) {
        this.compType = compType;
    }

    public void setFilterType(CompIndexCondBean filterType) {
        this.filterType = filterType;
    }

    public void setIndexTempCode(String indexTempCode) {
        this.indexTempCode = indexTempCode;
    }

    public void setConNum(String conNum) {
        this.conNum = conNum;
    }

    public void setConRela(String conRela) {
        this.conRela = conRela;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAttrType(int attrType) {
        this.attrType = attrType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldConNum(String oldConNum) {
        this.oldConNum = oldConNum;
    }

    public void setAttrDenParam(YssCompAttrParam attrDenParam) {
        this.attrDenParam = attrDenParam;
    }

    public void setAttrParam(YssCompAttrParam attrParam) {
        this.attrParam = attrParam;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public void setDenominaAttrName(String denominaAttrName) {
        this.denominaAttrName = denominaAttrName;
    }

    public void setDenominaAttrCode(String denominaAttrCode) {
        this.denominaAttrCode = denominaAttrCode;
    }

    public void setOldIndexCode(String oldIndexCode) {
        this.oldIndexCode = oldIndexCode;
    }

    public String getIndexCode() {
        return indexCode;
    }

    public String getDesc() {
        return desc;
    }

    public String getOldConNum() {
        return oldConNum;
    }

    public YssCompAttrParam getAttrDenParam() throws YssException {
        if (attrDenParam == null) {
            try {
                attrDenParam = this.getAttrParam(this.denominaAttrCode);
            } catch (YssException ex) {
                throw new YssException(ex.getMessage());
            }
        }
        return attrDenParam;
    }

    public YssCompAttrParam getAttrParam() throws YssException {
        if (attrParam == null) {
            try {
                attrParam = this.getAttrParam(this.attrCode);
            } catch (YssException ex) {
                throw new YssException(ex.getMessage());
            }
        }
        return attrParam;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getAttrCode() {
        return attrCode;
    }

    public String getDenominaAttrName() {
        return denominaAttrName;
    }

    public String getDenominaAttrCode() {
        return denominaAttrCode;
    }

    public String getOldIndexCode() {
        return oldIndexCode;
    }

    public CompIndexCondBean() {
    }

    private YssCompAttrParam getAttrParam(String sAttrCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        YssCompAttrParam reAttrParam = null; ;
        try {
            strSql = "select FDataType, FParam from " + pub.yssGetTableName("Tb_Comp_Attr") +
                " where FCheckState=1 and FCompAttrCode=" + dbl.sqlString(sAttrCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                reAttrParam = new YssCompAttrParam();
                reAttrParam.parseRowStr(rs.getString("FParam") + "");
                reAttrParam.setDataType(rs.getInt("FDataType"));
            }
            return reAttrParam;
        } catch (Exception e) {
            throw new YssException("获取监控指标【" + indexCode +
                                   "】中的条件【" + conNum + "】的属性参数出错： \n" +
                                   e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        buf.append(this.conNum).append("\t");
        buf.append(this.compType).append("\t");
        buf.append(this.attrType).append("\t");
        buf.append(this.sign).append("\t");
        buf.append(this.value).append("\t");
        buf.append(this.conRela).append("\t");
        //----------------------------------------------
        buf.append(this.attrCode).append("\t");
        buf.append(this.attrName).append("\t");
        buf.append(this.denominaAttrCode).append("\t");
        buf.append(this.denominaAttrName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.indexTempCode = reqAry[0];
            this.indexCode = reqAry[1];
            this.conNum = reqAry[2];
            if (reqAry[3].length() != 0) {
                this.compType = Integer.parseInt(reqAry[3]);
            }
            if (reqAry[4].length() != 0) {
                this.attrType = Integer.parseInt(reqAry[4]);
            }
            this.sign = reqAry[5];
            this.value = reqAry[6];
            this.conRela = reqAry[7];
            this.attrCode = reqAry[8];
            this.denominaAttrCode = reqAry[9];
            this.checkStateId = Integer.parseInt(reqAry[10]);
            this.oldConNum = reqAry[11];
            this.oldIndexCode = reqAry[12];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CompIndexCondBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析监控条件请求出错", e);
        }
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            "(select FConNum,FCheckState from " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " " +
            " where FCheckState <> 2 group by FConNum,FCheckState) x join" +
            " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y on x.FConNum = y.FConNum " +
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

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
            if (this.filterType.conNum.length() != 0) {
                sResult = sResult + " and a.FConNum like '" +
                    filterType.conNum.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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

                setCompIndexConditionAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_CIC_COMPTYPE + "," +
                                        YssCons.YSS_CIC_ATTRTYPE + "," +
                                        YssCons.YSS_CIC_CONRELA + "," +
                                        YssCons.YSS_CIC_SIGN);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取监控指标条件信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public void setCompIndexConditionAttr(ResultSet rs) throws SQLException {
        this.indexTempCode = rs.getString("FIndexTempCode") + "";
        this.indexCode = rs.getString("FIndexCode") + "";
        this.conNum = rs.getString("FConNum") + "";
        this.compType = rs.getInt("FCompType");
        this.attrType = rs.getInt("FAttrType");
        this.attrCode = rs.getString("FAttr") + "";
        this.attrName = rs.getString("FAttrName") + "";
        this.sign = rs.getString("FSign") + "";
        this.value = rs.getString("FValue") + "";
        this.denominaAttrCode = rs.getString("FDenominaAttr") + "";
        this.denominaAttrName = rs.getString("FDenominaAttrName") + "";
        this.conRela = rs.getString("FConRela");
        this.desc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return null;
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        String strName = "";
        String sVocStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            strName = "监控条件";
            sHeader = "条件编号\t监控类型\t属性类型\t符号\t监控值\t关系\t属性代码\t属性名称\t分母属性代码\t分母属性名称";
            strSql = "select y.* from " +
                "(select FConNum,FIndexCode,FIndexTempCode,FCheckState from " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                " where FIndexTempCode = " + dbl.sqlString(this.indexTempCode) + "and FIndexCode=" + dbl.sqlString(this.oldIndexCode) +
                " and  FCheckState <> 2  " +
                " group by FConNum,FCheckState " +
                ") x join" +
                " (select a.*, mm.FCompAttrName as FAttrName,nn.FCompAttrName as FDenominaAttrName,f.FVocName as FCompTypeValue ,g.FVocName as FAttrTypeValue," + //h.FVocName as FConRelaValue , i.FVocName as FSignValue,"+
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
//--------------------------------------------------------------------------------------------------
                " left join (select FCompAttrCode,FCompAttrName from " + pub.yssGetTableName("Tb_Comp_Attr") + ") mm on a.FAttr = mm.FCompAttrCode" +
                " left join (select FCompAttrCode,FCompAttrName from " + pub.yssGetTableName("Tb_Comp_Attr") + ") nn on a.FDenominaAttr = nn.FCompAttrCode" +
//---------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FCompType") + " = f.FVocCode and f.FVocTypeCode = " + // lzp  modify  2007 12.13
                dbl.sqlString(YssCons.YSS_CIC_COMPTYPE) +
//---------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FAttrType") + " = g.FVocCode and g.FVocTypeCode = " + // lzp  modify  2007 12.13
                dbl.sqlString(YssCons.YSS_CIC_ATTRTYPE) +
//----------------------------------------------------------------------------------------------
                //       " left join Tb_Fun_Vocabulary h on a.FConRela =  h.FVocCode and h.FVocTypeCode = " +
                //            dbl.sqlString(YssCons.YSS_CIC_CONRELA) +
//------------------------------------------------------------------------------------------------
                //       " left join Tb_Fun_Vocabulary i on a.FSign = i.FVocCode and i.FVocTypeCode = " +
                //           dbl.sqlString(YssCons.YSS_CIC_SIGN) +
//------------------------------------------------------------------------------------------------
                " ) y on x.FConNum = y.FConNum and x.FIndexTempCode=y.FIndexTempCode and x.FIndexCode=y.FIndexCode" +
                " order by  y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setCompIndexConditionAttr(rs);
                bufAll.append(buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_CIC_COMPTYPE + "," +
                                        YssCons.YSS_CIC_ATTRTYPE + "," +
                                        YssCons.YSS_CIC_CONRELA + "," +
                                        YssCons.YSS_CIC_SIGN);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception ye) {
            ye.printStackTrace();
        }
        return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
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
     * parseRowStr
     *
     * @param sRowStr String
     */


    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String[] tempiCondition = sMutilRowStr.split("\f\f");
        super.parseRecLog();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Comp_IndexCondition") +
                " where FIndexCode=" + dbl.sqlString(this.indexCode) +
                " and FIndexTempCode =" + dbl.sqlString(this.indexTempCode);
            dbl.executeSql(strSql);
            if (! (tempiCondition[0].split("\t")[2].length() == 0)) {
                for (int i = 0; i < tempiCondition.length; i++) {
                    strSql = "insert into " +
                        pub.yssGetTableName("Tb_Comp_IndexCondition") +
                        "(FIndexTempCode,FIndexCode,FConNum,FCompType,FAttrType,FSign," +
                        " FValue,FConRela,FAttr,FDenominaAttr,FCheckState,FCreator,FCreateTime,FCheckUser )" +
                        " values(" + dbl.sqlString(tempiCondition[i].split("\t")[0]) +
                        "," +
                        dbl.sqlString(tempiCondition[i].split("\t")[1]) + "," +
                        dbl.sqlString(tempiCondition[i].split("\t")[2]) + "," +
                        tempiCondition[i].split("\t")[3] + "," +
                        tempiCondition[i].split("\t")[4] + "," +
                        dbl.sqlString(tempiCondition[i].split("\t")[5]) + "," +
                        dbl.sqlString(tempiCondition[i].split("\t")[6]) + "," +
                        dbl.sqlString(tempiCondition[i].split("\t")[7]) + "," +
                        dbl.sqlString(tempiCondition[i].split("\t")[8].length() == 0 ? " " : tempiCondition[i].split("\t")[8]) + "," +
                        dbl.sqlString(tempiCondition[i].split("\t")[9].length() == 0 ? " " : tempiCondition[i].split("\t")[9]) + "," +
                        (pub.getSysCheckState() ? "0" : "1") + "," +
                        dbl.sqlString(this.creatorCode) + "," +
                        dbl.sqlString(this.creatorTime) + "," +
                        (pub.getSysCheckState() ? "' '" :
                         dbl.sqlString(this.creatorCode)) + ")";
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("获取监控条件信息出错！", e);
        }
        return null;
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
       if (btOper == YssCons.OP_ADD) {
         strSql =
               "insert into " + pub.yssGetTableName("Tb_Comp_IndexCondition") + "(FIndexTempCode,FIndexCode," +
               "FConNum,FCompType,FAttrType,FAttr,FSign ,FValue,FDenominaAttr,FConRela,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
               " values(" + dbl.sqlString(this.indexTempCode) + "," +
               dbl.sqlString(this.indexCode) + "," +
               dbl.sqlString(this.conNum) + "," +
               this.compType + "," +
               this.attrType + "," +
               dbl.sqlString(this.attrCode) + "," +
               dbl.sqlString(this.sign) + "," +
               dbl.sqlString(this.value) + "," +
               dbl.sqlString(this.denominaAttrCode) + "," +
               dbl.sqlString(this.conRela) + "," +
               dbl.sqlString(this.desc) + "," +
               (pub.getSysCheckState()?"0":"1") + "," +
               dbl.sqlString(this.creatorCode) + "," +
               dbl.sqlString(this.creatorTime) + "," +
               (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
      }
      else if (btOper == YssCons.OP_EDIT) {
         strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FIndexTempCode = " +
               dbl.sqlString(this.indexTempCode) + ", FIndexCode= " +

               dbl.sqlString(this.indexCode) + ", FConNum = " +


               dbl.sqlString(this.conNum) + ", FCompType= " +

               this.compType + ", FAttrType= " +
               this.attrType + ", FAttr= " +
               dbl.sqlString(this.attrCode) + ", FSign= " +
               dbl.sqlString(this.sign) + ", FValue= " +
               dbl.sqlString(this.value) + ",FDenominaAttr=" +
               dbl.sqlString(this.denominaAttrCode) + ",FConRela="+
               dbl.sqlString(this.conRela) +  ", FDesc = " +
               dbl.sqlString(this.desc) + ", FCreator = " +
               dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
               dbl.sqlString(this.creatorTime) +
               " where FIndexTempCode = " + dbl.sqlString(this.oldConNum);
      }
      else if (btOper == YssCons.OP_DEL) {
         strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FCheckState = " +
               this.checkStateId + ", FCheckUser = " +
               dbl.sqlString(pub.getUserCode()) +
               ", FCheckTime = '" +
               YssFun.formatDatetime(new java.util.Date()) + "'" +
               " where FIndexTempCode = " +
               dbl.sqlString(this.oldConNum);
      }
      else if (btOper == YssCons.OP_AUDIT) {
         strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FCheckState = " +
               this.checkStateId + ", FCheckUser = " +
               dbl.sqlString(pub.getUserCode()) +
               ", FCheckTime = '" +
               YssFun.formatDatetime(new java.util.Date()) + "'" +
               " where FIndexTempCode = " +
               dbl.sqlString(this.oldConNum) ;
      }
      conn.setAutoCommit(false);
      bTrans = true;
      dbl.executeSql(strSql);
      conn.commit();
      bTrans = false;
      conn.setAutoCommit(true);
       }
       catch (Exception e) {
      throw new YssException("设置监控指标条件信息出错！", e);
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
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Comp_IndexCondition") + "(FIndexTempCode,FIndexCode," +
                "FConNum,FCompType,FAttrType,FAttr,FSign ,FValue,FDenominaAttr,FConRela,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.indexTempCode) + "," +
                dbl.sqlString(this.indexCode) + "," +
                dbl.sqlString(this.conNum) + "," +
                this.compType + "," +
                this.attrType + "," +
                dbl.sqlString(this.attrCode) + "," +
                dbl.sqlString(this.sign) + "," +
                dbl.sqlString(this.value) + "," +
                dbl.sqlString(this.denominaAttrCode) + "," +
                dbl.sqlString(this.conRela) + "," +
                dbl.sqlString(this.desc) + "," +
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
        } catch (Exception e) {
            throw new YssException("增加监控指标条件信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIndexTempCode = " +
                dbl.sqlString(this.oldConNum);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核监控指标条件信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIndexTempCode = " +
                dbl.sqlString(this.oldConNum);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除监控指标条件信息出错", e);
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

            strSql = "update " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " set FIndexTempCode = " +
                dbl.sqlString(this.indexTempCode) + ", FIndexCode= " +

                dbl.sqlString(this.indexCode) + ", FConNum = " +

                dbl.sqlString(this.conNum) + ", FCompType= " +

                this.compType + ", FAttrType= " +
                this.attrType + ", FAttr= " +
                dbl.sqlString(this.attrCode) + ", FSign= " +
                dbl.sqlString(this.sign) + ", FValue= " +
                dbl.sqlString(this.value) + ",FDenominaAttr=" +
                dbl.sqlString(this.denominaAttrCode) + ",FConRela=" +
                dbl.sqlString(this.conRela) + ", FDesc = " +
                dbl.sqlString(this.desc) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                dbl.sqlString(this.creatorTime) +
                " where FIndexTempCode = " + dbl.sqlString(this.oldConNum);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改监控指标条件信息出错", e);
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
        CompIndexCondBean befEditBean = new CompIndexCondBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FConNum,FCheckState from " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " " +
                " where FCheckState <> 2 group by FConNum,FCheckState) x join" +
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Comp_IndexCondition") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  FConNum =" + dbl.sqlString(this.oldConNum) +
                ") y on x.FConNum = y.FConNum " +
                " order by y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.indexTempCode = rs.getString("FIndexTempCode") + "";
                befEditBean.indexCode = rs.getString("FIndexCode") + "";
                befEditBean.conNum = rs.getString("FConNum") + "";
                befEditBean.compType = rs.getInt("FCompType");
                befEditBean.attrType = rs.getInt("FAttrType");
                befEditBean.attrCode = rs.getString("FAttr") + "";
                befEditBean.attrName = rs.getString("FAttrName") + "";
                befEditBean.sign = rs.getString("FSign") + "";
                befEditBean.value = rs.getString("FValue") + "";
                befEditBean.denominaAttrCode = rs.getString("FDenominaAttr") + "";
                befEditBean.denominaAttrName = rs.getString("FDenominaAttrName") + "";
                befEditBean.conRela = rs.getString("FConRela");
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
