package com.yss.main.compliance;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.basesetting.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class CompAttrBean
    extends BaseDataSettingBean implements IDataSetting {

    private String compAttrCode = ""; //监控属性代码
    private String compAttrName = ""; //监控属性名称
    private int attrType; //属性类型
    private String strAttrType = ""; //属性类型描述
    private int dataType; //数据类型
    private String strDataType = ""; //数据类型描述
    private String catCodes = ""; //类型代码
    private String param = ""; //参数
    private String desc = ""; //描述
    private String oldcompAttrCode = "";
    private CompAttrBean filterType;
    private String category = "";
    private String sRecycled = ""; //保存未解析前的字符串 add by zhouxiang
    public String getDesc() {
        return desc;
    }

    public String getOldcompAttrCode() {
        return oldcompAttrCode;
    }

    public String getCatCodes() {
        return catCodes;
    }

    public CompAttrBean getFiltertype() {
        return filterType;
    }

    public String getParam() {
        return param;
    }

    public String getCompAttrCode() {
        return compAttrCode;
    }

    public int getDataType() {
        return dataType;
    }

    public String getCompAttrName() {
        return compAttrName;
    }

    public void setAttrType(int attrType) {
        this.attrType = attrType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldcompAttrCode(String oldcompAttrCode) {
        this.oldcompAttrCode = oldcompAttrCode;
    }

    public void setCatCodes(String catCodes) {
        this.catCodes = catCodes;
    }

    public void setFiltertype(CompAttrBean filtertype) {
        this.filterType = filtertype;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setCompAttrCode(String compAttrCode) {
        this.compAttrCode = compAttrCode;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setCompAttrName(String compAttrName) {
        this.compAttrName = compAttrName;
    }

    public void setStrAttrType(String strAttrType) {
        this.strAttrType = strAttrType;
    }

    public void setStrDataType(String strDataType) {
        this.strDataType = strDataType;
    }

    public int getAttrType() {
        return attrType;
    }

    public String getStrAttrType() {
        return strAttrType;
    }

    public String getStrDataType() {
        return strDataType;
    }

    public CompAttrBean() {
    }

    /**
     * parseRowStre
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String catCode[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.category = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            //对品种类型进行处理 转换成以逗号隔开的字符串
            if (this.category.length() != 0 && this.category.length() != 1) {
                catCode = this.category.split("\f\f");
                for (int j = 0; j < catCode.length; j++) {
                    catCodes = catCodes + catCode[j].split("\t")[0] + ",";
                }
                if (catCodes.length() > 0) {
                    catCodes = YssFun.left(catCodes, catCodes.length() - 1);
                }
            }
            sRecycled=sRowStr;
            reqAry = sTmpStr.split("\t");
            this.compAttrCode = reqAry[0];
            this.compAttrName = reqAry[1];
            if (reqAry[2].length() != 0) {
                this.attrType = Integer.parseInt(reqAry[2]);
            }
            if (reqAry[3].length() != 0) {
                this.dataType = Integer.parseInt(reqAry[3]);
            }
            // this.catCodes=reqAry[4];
            //------ add by wangzuochun 2010.07.14 MS01436 监控管理——监控属性，点击进入回收站，当该条数据的参数或描述中包含回车时，会报错。 QDV4赢时胜(测试)2010年7月13日01_B.xls 
            if (reqAry[5] != null ){
            	if (reqAry[5].indexOf("【Enter】") >= 0){
            		this.param = reqAry[5].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.param = reqAry[5];
            	}
            }
            
            if (reqAry[6] != null ){
            	if (reqAry[6].indexOf("【Enter】") >= 0){
            		this.desc = reqAry[6].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.desc = reqAry[6];
            	}
            }
            //------------------MS01436----------------//
            this.checkStateId = Integer.parseInt(reqAry[7]);
            this.oldcompAttrCode = reqAry[8];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CompAttrBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析监控属性出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.compAttrCode).append("\t");
        buf.append(this.compAttrName).append("\t");

        buf.append(this.attrType).append("\t");
        buf.append(this.dataType).append("\t");
        buf.append(this.catCodes).append("\t");
        buf.append(this.param).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.strAttrType).append("\t");
        buf.append(this.strDataType).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Comp_Attr"),
                               "fcompattrcode",
                               this.compAttrCode,
                               this.oldcompAttrCode);

    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         Connection conn = dbl.loadConnection();
         boolean bTrans = false;
         String strSql = "";
         try {
            if (btOper == YssCons.OP_ADD) {
               strSql =
                     "insert into " + pub.yssGetTableName("Tb_Comp_Attr") +
                     "(FCompAttrCode,FCompAttrName," +
                     "FAttrType,FDataType,FCatCodes,FParam,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                     " values(" + dbl.sqlString(this.compAttrCode) + "," +
                     dbl.sqlString(this.compAttrName) + "," +
                     this.attrType + "," +
                     this.dataType + "," +
                     dbl.sqlString(this.catCodes) + "," +
                     dbl.sqlString(this.param) + "," +
                     dbl.sqlString(this.desc) + "," +
                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState() ? "' '" :
                      dbl.sqlString(this.creatorCode)) + ")";
            }
            else if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Comp_Attr") +
                     " set FCompAttrCode = " +
                     dbl.sqlString(this.compAttrCode) + ", FCompAttrName = "
                     + dbl.sqlString(this.compAttrName) + ", FAttrType = "
                     + this.attrType + ", FDataType = " +
                     +this.dataType + ", FCatCodes = " +
                     dbl.sqlString(this.catCodes) + ", FParam= " +
                     dbl.sqlString(this.param) + ", FDesc= " +
                     dbl.sqlString(this.desc) + ", FCreator = " +
                     dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                     dbl.sqlString(this.creatorTime) +
                     " where FCompAttrCode = " +
                     dbl.sqlString(this.oldcompAttrCode);
            }
            else if (btOper == YssCons.OP_DEL) {
               strSql = "update " + pub.yssGetTableName("Tb_Comp_Attr") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FCompAttrCode = " +
                     dbl.sqlString(this.compAttrCode);
            }
            else if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Comp_Attr") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FCompAttrCode = " +
                     dbl.sqlString(this.compAttrCode);
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
//----------------------------------------------------------------------------------------------------------
            if (btOper == YssCons.OP_ADD || btOper == YssCons.OP_EDIT) {
               if (this.category != null && ! (this.category.length() == 0)) {
                  CategoryBean category = new CategoryBean();
                  category.setYssPub(pub);
                  category.saveMutliSetting(this.category);
               }
            }
            /*
               else if (btOper == YssCons.OP_DEL) {
                  strSql =
                        "update   Tb_Base_Category set FCheckState = 2 " +
                        "where FPortCode = " +
                        dbl.sqlString(this.portCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);

                  dbl.executeSql(strSql);
               }
               else if (btOper == YssCons.OP_AUDIT) {
                  strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio_Relaship") + " set FCheckState = " +
                        this.checkStateId;
                  if (this.checkStateId == 1) {
                     strSql += ", FCheckUser = '" +
                           pub.getUserCode() + "' , FCheckTime = '" +
                           YssFun.formatDatetime(new java.util.Date()) + "'";
                  }
                  strSql += " where FPortCode = " +
                        dbl.sqlString(this.portCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);

                  dbl.executeSql(strSql);
               }
              }

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);

          }
          catch (Exception e) {
             throw new YssException("设置监控属性信息出错！", e);
          }
          finally {
             dbl.endTransFinal(conn, bTrans);
          }

       }*/

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
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            "(select FCompAttrCode,FCheckState from " +
            pub.yssGetTableName("Tb_Comp_Attr") + " " +
            "  group by FCompAttrCode,FCheckState) x join" + // modify by wangzuochun 2010.06.22 MS01264    在监控属性未审核界面删除数据后在回收站中无法显示被删除的数据。    QDV4赢时胜(测试)2010年6月2日5_B    
            " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,n.FVocName as FAttrTypeDesc,m.FVocName as FDataTypeDesc from " +
            pub.yssGetTableName("Tb_Comp_Attr") + " a" +
            " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FAttrType") + " = n.FVocCode and n.FVocTypeCode = " + // lzp  modify
            dbl.sqlString(YssCons.YSS_CA_ATTRTYPE) +
            " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar(" a.FDataType") + " = m.FVocCode and m.FVocTypeCode = " + // lzp  modify
            dbl.sqlString(YssCons.YSS_CA_DATATYPE) +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +

            buildFilterSql() +
            ") y on x. FCompAttrCode = y. FCompAttrCode " +
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);

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

                setCompAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_CA_ATTRTYPE + "," +
                                        YssCons.YSS_CA_DATATYPE
                );
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取监控属性信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.compAttrCode.length() != 0) {
                sResult = sResult + " and a.FCompAttrCode like '" +
                    filterType.compAttrCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.compAttrName.length() != 0) {
                sResult = sResult + " and a.FCompAttrName like '" +
                    filterType.compAttrName.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void setCompAttr(ResultSet rs) throws SQLException {
        this.compAttrCode = rs.getString("FCompAttrCode") + "";
        this.compAttrName = rs.getString("FCompAttrName") + "";
        this.attrType = rs.getInt("FAttrType");
        this.dataType = rs.getInt("FDataType");
        this.catCodes = rs.getString("FCatCodes") + "";
        this.param = rs.getString("FParam") + "";
        this.desc = rs.getString("FDesc") + "";
        this.strAttrType = rs.getString("FAttrTypeDesc") + "";
        this.strDataType = rs.getString("FDataTypeDesc");
        super.setRecLog(rs);
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
            sHeader = "监控属性代码\t监控属性名称";
            strSql = "select y.* from " +
                "(select FCompAttrCode from " +
                pub.yssGetTableName("Tb_Comp_Attr") + " " +
                " where  FCheckState = 1 and FAttrType = " + this.attrType +
                " group by FCompAttrCode) x join" +
                " (select a.*, n.FVocName as FAttrTypeDesc,m.FVocName as FDataTypeDesc,b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Comp_Attr") + " a" +
                " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FAttrType") + " = n.FVocCode and n.FVocTypeCode = " + // lzp modify
                dbl.sqlString(YssCons.YSS_CA_ATTRTYPE) +
                " left join Tb_Fun_Vocabulary m on  " + dbl.sqlToChar("a.FDataType") + " = m.FVocCode and m.FVocTypeCode = " + // lzp modify
                dbl.sqlString(YssCons.YSS_CA_DATATYPE) +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ") y on x.FCompAttrCode = y.FCompAttrCode " +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCompAttrCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCompAttrName") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);
                this.setCompAttr(rs);
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
            throw new YssException("获取监控属性信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "监控属性代码\t监控属性名称";
            strSql = "select y.* from " +
                "(select FCompAttrCode from " +
                pub.yssGetTableName("Tb_Comp_Attr") + " " +
                " where  FCheckState = 1 and FAttrType=3 group by FCompAttrCode) x join" +
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Comp_Attr") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ") y on x.FCompAttrCode = y.FCompAttrCode " +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCompAttrCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCompAttrName") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);

                this.compAttrCode = rs.getString("FCompAttrCode");
                this.compAttrName = rs.getString("FCompAttrName");

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
            throw new YssException("获取监控属性信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        ResultSet rs2 = null; //保存获得的品种代码
        String fcatcodes[] = null;
        String fcatcode = "'all',";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        sHeader = "品种代码\t品种名称";
        try {
            String str = "select Fcatcodes from " +
                pub.yssGetTableName("Tb_Comp_Attr") + " where FCompAttrCode=" +
                dbl.sqlString(this.compAttrCode);
            rs2 = dbl.openResultSet(str);
            if (rs2.next()) {
                if (rs2.getString("FCatCodes") != null) {
                    fcatcodes = rs2.getString("FCatCodes").split(",");
                    for (int i = 0; i < fcatcodes.length; i++) {
                        fcatcode = fcatcode + dbl.sqlString(fcatcodes[i]) + ",";
                    }
                }
            }
            if (fcatcode.length() > 0) {
                fcatcode = YssFun.left(fcatcode, fcatcode.length() - 1);
            }

            strSql = "select y.* from " +
                "(select FCatCode,FCheckState from  Tb_Base_Category " + " " +
                " where FCheckState <> 2 and FCatCode in ("
                + fcatcode +
                " ) group by FCatCode,FCheckState " +
                ") x join" +
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from Tb_Base_Category " +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " ) y on x.FCatCode = y.FCatCode" +
                " order by  y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCatCode") + "").trim()).
                    append("\t");
                bufShow.append( (rs.getString("FCatName") + "").trim()).
                    append(YssCons.YSS_LINESPLITMARK);
                CategoryBean category = new CategoryBean();
                category.setYssPub(pub);
                category.setCategoryAttr(rs);

                bufAll.append(category.buildRowStr()).append(YssCons.
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        }

        catch (Exception e) {
            throw new YssException("获取品种出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);

        }

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
                "insert into " + pub.yssGetTableName("Tb_Comp_Attr") +
                "(FCompAttrCode,FCompAttrName," +
                "FAttrType,FDataType,FCatCodes,FParam,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.compAttrCode) + "," +
                dbl.sqlString(this.compAttrName) + "," +
                this.attrType + "," +
                this.dataType + "," +
                dbl.sqlString(this.catCodes) + "," +
                dbl.sqlString(this.param) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            dbl.executeSql(strSql);
            if (this.category != null && ! (this.category.length() == 0)) {
                CategoryBean category = new CategoryBean();
                category.setYssPub(pub);
                category.saveMutliSetting(this.category);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加监控属性信息出错", e);
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
        String[] arrDate=null;
        Connection conn = dbl.loadConnection();
        try {
            //edited by zhouxiang MS01367    回收站的清除按钮功能失效，点全选后再还原只能还原第一条数据   
        	if(sRecycled!=null&&sRecycled.length()>0)
            	arrDate=sRecycled.split("\r\n");
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
			if (arrDate != null) {
				for (int i = 0; i < arrDate.length; i++) {
					this.parseRowStr(arrDate[i]);
					// ------------end-----------------
					strSql = "update " + pub.yssGetTableName("Tb_Comp_Attr") + " set FCheckState = "
							+ this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FCompAttrCode = " + dbl.sqlString(this.compAttrCode);
					conn.setAutoCommit(false);
					bTrans = true;
					dbl.executeSql(strSql);
				}
			}
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核监控属性信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Comp_Attr") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FCompAttrCode = " +
                dbl.sqlString(this.compAttrCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除监控属性信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Comp_Attr") +
                " set FCompAttrCode = " +
                dbl.sqlString(this.compAttrCode) + ", FCompAttrName = "
                + dbl.sqlString(this.compAttrName) + ", FAttrType = "
                + this.attrType + ", FDataType = " +
                +this.dataType + ", FCatCodes = " +
                dbl.sqlString(this.catCodes) + ", FParam= " +
                dbl.sqlString(this.param) + ", FDesc= " +
                dbl.sqlString(this.desc) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                dbl.sqlString(this.creatorTime) +
                " where FCompAttrCode = " +
                dbl.sqlString(this.oldcompAttrCode);
            dbl.executeSql(strSql);
            if (this.category != null && ! (this.category.length() == 0)) {
                CategoryBean category = new CategoryBean();
                category.setYssPub(pub);
                category.saveMutliSetting(this.category);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改监控属性信息出错", e);
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
        CompAttrBean befEditBean = new CompAttrBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FCompAttrCode,FCheckState from " +
                pub.yssGetTableName("Tb_Comp_Attr") + " " +
                " where FCheckState <> 2 group by FCompAttrCode,FCheckState) x join" +
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,n.FVocName as FAttrTypeDesc,m.FVocName as FDataTypeDesc from " +
                pub.yssGetTableName("Tb_Comp_Attr") + " a" +
                " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FAttrType") + " = n.FVocCode and n.FVocTypeCode = " + //lzp modify
                dbl.sqlString(YssCons.YSS_CA_ATTRTYPE) +
                " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FDataType") + " = m.FVocCode and m.FVocTypeCode = " + //lzp modify
                dbl.sqlString(YssCons.YSS_CA_DATATYPE) +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  FCompAttrCode =" + dbl.sqlString(this.oldcompAttrCode) +
                ") y on x. FCompAttrCode = y. FCompAttrCode " +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.compAttrCode = rs.getString("FCompAttrCode") + "";
                befEditBean.compAttrName = rs.getString("FCompAttrName") + "";
                befEditBean.attrType = rs.getInt("FAttrType");
                befEditBean.dataType = rs.getInt("FDataType");
                befEditBean.catCodes = rs.getString("FCatCodes") + "";
                befEditBean.param = rs.getString("FParam") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.strAttrType = rs.getString("FAttrTypeDesc") + "";
                befEditBean.strDataType = rs.getString("FDataTypeDesc");

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    public void saveSetting(byte btOper) {
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException{
    	//edited by zhouxiang 回收站清除信息功能
    	String strSql = "";
    	String [] arrData=null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        
        try {
        	 if(sRecycled != null && sRecycled.length()>0 )
        	 {
        		 arrData=sRecycled.split("\r\n");
        	 }
        	 //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
			if (arrData != null) {
				for (int i = 0; i < arrData.length; i++) {
					this.parseRowStr(arrData[i]);

					strSql = "delete  from " + pub.yssGetTableName("Tb_Comp_Attr") + " where FCompAttrCode = "
							+ dbl.sqlString(this.compAttrCode);
					conn.setAutoCommit(false);
					bTrans = true;
					dbl.executeSql(strSql);
				}
			}
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除监控属性信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //---------------end------------------
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
