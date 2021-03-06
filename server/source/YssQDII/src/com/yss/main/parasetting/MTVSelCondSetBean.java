package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 * MS00008 create by 宋洁 2009-02-12
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MTVSelCondSetBean
    extends BaseDataSettingBean implements IDataSetting {

    public MTVSelCondSetBean() {
    }

    private String mTVCode; //估值方法代码
    private String selCondCode; //筛选条件代码
    private String catCode = ""; //品种代码
    private String catName = ""; //品种名称
    private String subCatCode = ""; //品种明细代码
    private String subCatName = ""; //品种明细名称
    private String exchangeCode = ""; //交易所代码
    private String exchangeName = ""; //交易所名称
    private String cusCatCode = ""; //自定义子品种代码
    private String cusCatName = ""; //自定义子品种名称
    private MTVSelCondSetBean filterType;

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatName() {
        return catName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setCusCatName(String cusCatName) {
        this.cusCatName = cusCatName;
    }

    public String getCusCatName() {
        return cusCatName;
    }

    public void setFilterType(MTVSelCondSetBean filterType) {
        this.filterType = filterType;
    }

    public MTVSelCondSetBean getFilterType() {
        return filterType;
    }

    public String getSelCondCode() {
        return selCondCode;
    }

    public void setMTVCode(String mTVCode) {
        this.mTVCode = mTVCode;
    }

    public void setSelCondCode(String selCondCode) {
        this.selCondCode = selCondCode;
    }

    public void setSubCatCode(String subCatCode) {
        this.subCatCode = subCatCode;
    }

    public void setCusCatCode(String cusCatCode) {
        this.cusCatCode = cusCatCode;
    }

    public void setExcahngeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public String getMTVCode() {
        return mTVCode;
    }

    public String getSubCatCode() {
        return subCatCode;
    }

    public String getCusCatCode() {
        return cusCatCode;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public String getCatCode() {
        return catCode;
    }

    /**
     * 解析前台传过来的字符串信息，解析成具体的筛选条件信息
     *
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
            this.mTVCode = reqAry[0];
            this.selCondCode = reqAry[1];
            this.catCode = reqAry[2];
            this.subCatCode = reqAry[3];
            this.cusCatCode = reqAry[4];
            this.exchangeCode = reqAry[5];
            super.checkStateId = Integer.parseInt(reqAry[6]);
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new MTVSelCondSetBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);

            }

        } catch (Exception e) {
            throw new YssException("解析估值方法筛选条件请求信息出错", e);
        }
    }

    /**
     * 将具体的筛选条件信息组成一个字符串
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.mTVCode + "").append("\t");
        buf.append(this.selCondCode + "").append("\t");
        buf.append(this.catCode + "").append("\t");
        buf.append(this.catName + "").append("\t");
        buf.append(this.subCatCode + "").append("\t");
        buf.append(this.subCatName + "").append("\t");
        buf.append(this.cusCatCode + "").append("\t");
        buf.append(this.cusCatName + "").append("\t");
        buf.append(this.exchangeCode + "").append("\t");
        buf.append(this.exchangeName + "").append("\t");
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
     * saveSetting
     *
     * @param btOper byte
     */
    public void saveSetting(byte btOper) {
    }

    /**
     *
     * 存储筛选条件信息到估值方法筛选条件表中
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSelCondData(String sMutilRowStr, boolean bIsTrans,
                                       MTVMethodBean mtvMethodBean) throws
        YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String strYearMonth = "";
        try {
            if (bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }

            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.mTVCode = sMutilRowAry[0].split("\t")[0];
            strSql = "delete from " + pub.yssGetTableName("TB_PARA_MTVSELCONDSET") +
                " where FMTVCode=" + dbl.sqlString(this.mTVCode);

            dbl.executeSql(strSql); //在估值方法筛选条件表中先删除估值方法代码对应的所有筛选条件信息

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_MTVSelCondSet") +
                "(FMTVCode, FMTVSelCondCode, FCatCode, FSubCatCode, FCusCatCode, FExchangeCode,FCheckState,FCreator,FCreateTime)" +
                " values (?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql); //再在估值方法筛选条件表中添加筛选条件列表中的所有筛选条件信息

            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                pstmt.setString(1, this.mTVCode);
                pstmt.setString(2, i + 1 + "");
                pstmt.setString(3, this.catCode);
                pstmt.setString(4, this.subCatCode);
                pstmt.setString(5, this.cusCatCode);
                pstmt.setString(6, this.exchangeCode);
                pstmt.setInt(7, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(8, this.creatorName);
                pstmt.setString(9, this.creatorTime);
                pstmt.executeUpdate();
            }
            if (bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存估值方法筛选条件信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
           
        }
    }

    /**
     * getSetting
     *
     * @return IDaraSetting
     */
    public IDataSetting getSetting() {
        IDataSetting idata = null;
        return idata;
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
     * 用于返回估值方法设置的筛选条件设置页面要显示在listView中的筛选条件信息
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer buf = new StringBuffer();
        StringBuffer buf1 = new StringBuffer();

        try {
            sHeader = "品种类型代码\t品种类型名称\t品种子类型代码\t品种子类型名称\t自定义子类型代码\t自定义子类型名称\t交易所代码\t交易所名称";

            MTVMethodBean mtvMethod = new MTVMethodBean();
            mtvMethod.setYssPub(pub);

            strSql = "select a.*, d.FCatCode,d.FCatName,e.FSubCatCode,e.FSubCatName,f.FCusCatCode,f.FCusCatName,g.FExchangeCode,g.FExchangeName,b.fusername as fcreatorname, c.fusername as fcheckusername from " +
                pub.yssGetTableName("TB_PARA_MTVSELCONDSET") + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " left join (select FCatCode,FCatName from Tb_Base_Category) d on a.FCatCode = d.FCatCode " +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory) e on a.FSubCatCode = e.FSubCatCode " +
                " left join (select FCusCatCode,FCusCatName from " + pub.yssGetTableName("Tb_Para_CustomCategory)") +
                " f on a.FCusCatCode = f.FCusCatCode " +
                " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange) g on a.FExchangeCode = g.FExchangeCode " +
                buildFilterSql() + " order by FMTVCode desc ";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append( (rs.getString("FCatCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FCatName") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FSubCatCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FSubCatName") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FCusCatCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FCusCatName") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FExchangeCode") + "").trim());
                buf.append("\t");
                
             // edit by lidaolong 20110401  #553 估值方法设置中添加字段区分市场或交易所
                //根据交易市场代码查找名称
                String exchangeName="";
                String exchangeCode=rs.getString("FExchangeCode") + "";
                for(int i =0;i<exchangeCode.split(",").length;i++){
                    strSql ="select FExchangeCode, FExchangeName from Tb_Base_Exchange where FExchangeCode ="+ dbl.sqlString(exchangeCode.split(",")[i]);
                    rs2 = dbl.openResultSet(strSql);
                    while(rs2.next()){
                 	   exchangeName =exchangeName + rs2.getString("FExchangeName")+",";
                    }
                }
            
                //去最后一位
                if (exchangeName.length()>0){
                	exchangeName = exchangeName.substring(0,exchangeName.length()-1);
                }
                
                buf.append(exchangeName);
               // buf.append( (rs.getString("FExchangeName") + "").trim());
                
               //end by lidaolong 
               
                buf.append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);

                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2); ;
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            dbl.closeResultSetFinal(rs2);  // add by lidaolong 20110401  #553 估值方法设置中添加字段区分市场或交易所
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取估值方法筛选条件信息出错", e);
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
     * 在估值方法筛选条件表中添加新建的估值方法筛选条件信息
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * delSetting ,写sql语句将前台过来的筛选条件代码的那一行从估值方法筛选条件表中删除 MS00008
     */
    public void delSetting() {
    }

    /**根据前台传过来的摘选条件信息
     * 更新估值方法筛选条件表的内容
     *
     * @return String
     */
    public String editSetting() {
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
    public String getBeforeEditData() {
        return "";

    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    /**
     * 根据查询出的结果集为各个变量赋值
     */
    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        this.mTVCode = rs.getString("FMTVCode");
        this.selCondCode = rs.getString("FMTVSelCondCode");
        this.catCode = rs.getString("FCatCode");
        this.catName = rs.getString("FCatName");
        this.subCatCode = rs.getString("FSubCatCode");
        this.subCatName = rs.getString("FSubCatName");
        this.cusCatCode = rs.getString("FCusCatCode");
        this.cusCatName = rs.getString("FCusCatName");
        this.exchangeCode = rs.getString("FExchangeCode");
        // edit by lidaolong 20110401  #553 估值方法设置中添加字段区分市场或交易所
        //根据交易市场代码查找名称
        String strSql="";
        String exchangeName="";
      ResultSet rs2=null;
      	if (exchangeCode == null){
    	  exchangeCode="";
      	}
        for(int i =0;i<exchangeCode.split(",").length;i++){
            strSql ="select FExchangeCode, FExchangeName from Tb_Base_Exchange where FExchangeCode ="+ dbl.sqlString(exchangeCode.split(",")[i]);
            rs2 = dbl.openResultSet(strSql);
            while(rs2.next()){
         	   exchangeName =exchangeName + rs2.getString("FExchangeName")+",";
            }
        }
    
        //去最后一位
        if (exchangeName.length()>0){
        	exchangeName = exchangeName.substring(0,exchangeName.length()-1);
        }
        
        this.exchangeName = exchangeName;
        
        dbl.closeResultSetFinal(rs2);
       //end by lidaolong 
      
        super.setRecLog(rs);
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
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * 用于编写sql语句 where 关键字后的条件语句
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            if (this.filterType.mTVCode.length() != 0) {
                sResult = sResult + " where FMTVCode='" +
                    filterType.mTVCode.replaceAll("'", "''") + "'";
            }
        }
        return sResult;
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
