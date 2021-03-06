package com.yss.main.datainterface.swift;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.sql.*;
import com.yss.util.YssCons;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssFun;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ysstech</p>
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * @author by leeyu 2009-05-18
 * @version 1.0
 */
public class DaoSwiftEntitySet
    extends BaseDataSettingBean implements
    IDataSetting {

    public String getsWiftCode() {
		return sWiftCode;
	}

	public void setsWiftCode(String sWiftCode) {
		this.sWiftCode = sWiftCode;
	}

	public String getsOldWiftCode() {
		return sOldWiftCode;
	}

	public void setsOldWiftCode(String sOldWiftCode) {
		this.sOldWiftCode = sOldWiftCode;
	}

	private DaoSwiftEntitySet filterType;
    public String sWiftType = ""; //报文类型
    //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
    private String sWiftCode="";//报文代码
    private String sOldWiftCode = "";//主键 报文代码,更新时候用
    //-----------------------end -MS00796-----------------
    private String sStatus = ""; //状态
    private String sIndex = ""; //内容序号
    private String sTag = ""; //报文标识
    private String sQualifier = ""; //限定符
    private String sFieldName = ""; //字段简称
    private String sFieldFullName = ""; //字段全称
    private String sContent = ""; //内容
    private String sOption = ""; //选项
    private String sTableField = ""; //表字段代码

    private String sRecycled; //回收站数据

    //private String sOldWiftType = "";//by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
    private String sTableFieldName;
    private String sStatusName;
   private String sSwiftStatus="";//报文原状态 NEW CANCEL..
    //private String sOldSwiftStatus="";//原 报文原状态 //by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874

    public DaoSwiftEntitySet() {
    }

    /**
     * 提交前的检查
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
    }

    /**
     * single data add Method
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        return "";
    }

    /**
     * single data edit Method
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        return "";
    }

    /**
     * single Data delete Method
     * @throws YssException
     */
    public void delSetting() throws YssException {
    }

    /**
     * 审核、反审核、还原数据
     * @throws YssException
     */
    public void checkSetting() throws YssException {
    }

    /**
     * 多条数据的保存
     * 此处不添加事务处理，统一在外面执行事务处理
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null;
        PreparedStatement stm = null;
        String sqlStr = "";
        String[] arrData = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            sqlStr = "delete from " + pub.yssGetTableName("TB_DAO_SWIFTENTITY") +
                " where FSwiftCode=" + dbl.sqlString(this.sOldWiftCode);//MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
            dbl.executeSql(sqlStr);

            sqlStr = "insert into " + pub.yssGetTableName("TB_DAO_SWIFTENTITY") +
                "(FSwiftType,FStatus,FIndex,FTag,FQualifier,FFieldName,FFIeldFullName,FContent," +
                //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                "FOption,FTableField,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FSwiftStatus,FSwiftCode) Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            	//-----------------------end -MS00796-----------------
            stm = dbl.openPreparedStatement(sqlStr);
            arrData = sMutilRowStr.split("\r\n"); //这里应该是\r\n了,原来是\r\f因为你压数据是这样的
            for (int i = 0; i < arrData.length; i++) {
                this.parseRowStr(arrData[i]);
                if (this.sWiftCode.trim().length() == 0 || this.sWiftCode.trim().length() == 0) {
                    continue;
                }
                stm.setString(1, this.sWiftType);
                stm.setString(2, this.sStatus);
                stm.setString(3, this.sIndex);
                stm.setString(4, this.sTag);
                stm.setString(5, (this.sQualifier.trim().length() == 0 ? " " : this.sQualifier)); //edit by libo 20090703 数据为空时的处理
                stm.setString(6, this.sFieldName);
                stm.setString(7, this.sFieldFullName);
                stm.setString(8, (this.sContent.trim().length() == 0 ? " " : this.sContent)); // stm.setString(8,(this.sContent.trim().length()==0?" ":this.sContent));
                stm.setString(9, (this.sOption.trim().length() == 0 ? " " : this.sOption));
                stm.setString(10, (this.sTableField.trim().length() == 0 ? " " : this.sTableField));
                stm.setInt(11, this.checkStateId);
                stm.setString(12, this.creatorCode);
                stm.setString(13, this.creatorTime);
                stm.setString(14, (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode))); //照之前的做
                stm.setString(15, this.checkTime);
                stm.setString(17, this.sWiftCode);//MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                stm.setString(16,this.sSwiftStatus);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                stm.addBatch();
            }
            bTrans = true;
            stm.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception ex) {
            throw new YssException("保存实体字段信息出错！", ex);
        } finally {
            dbl.closeStatementFinal(stm);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    /**
     * 获取一条数据信息
     * @return IDataSetting
     * @throws YssException
     */
    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * 删除回收站数据的方法
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
    }

    /**
     * treeView1 loading
     * @return String
     * @throws YssException
     */
    public String getTreeViewData1() throws YssException {
        return "";
    }

    /**
     * treeView2 loading
     * @return String
     * @throws YssException
     */
    public String getTreeViewData2() throws YssException {
        return "";
    }

    /**
     * treeView3 loading
     * @return String
     * @throws YssException
     */
    public String getTreeViewData3() throws YssException {
        return "";
    }

    /**
     * listView1 loading
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {

        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = getListView1Headers();
            sqlStr =

                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName" + //创建人，核查人
                ",d.FFieldDesc as FTableFieldName" + //临时表字段
                ",e.FVocName as FStatusName" + //状态 FStatus  add by libo 20090608
                " from " + pub.yssGetTableName("TB_DAO_SWIFTENTITY") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" + ///这个保留
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " + ///这个保留
                " left join (select FFieldName,FFieldDesc from TB_FUN_DATADICT d1 join " +
                pub.yssGetTableName("tb_Dao_Swift") +
                " d2 on d1.FTabname =d2.Ftablecode ) d on d.FFieldName =a.FTableField " + //这个就是点的那个报文临时表字段关联的//"数据字典" by leeyu 修改，取数据字典的字段
                " left join Tb_Fun_Vocabulary e on " + dbl.sqlToChar("a.FStatus") + //"词汇类型"//状态
                " =e.FVocCode and " +
                " e.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_SWIFT_ENTITY_STATE) +
                buildFilterSql() +
                //" order by a.Findex,a.fTag,a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
                " order by a.Findex,a.Fstatus";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs); //建一个方法
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK); //"/f/f"
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取预处理接口信息出错", e); ///改成你的名字
        } finally {
            dbl.closeResultSetFinal(rs); //关闭ＲＳ QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
            ////dbl.endTransFinal(conn, bTrans); //
        }

    }

    /**
     * buildFilterSql
     *
     * @return String
     */
    private String buildFilterSql() {
        String filterSql = "";
        if (this.filterType != null) {
            filterSql = " where 1=1 ";
            if (filterType.sWiftCode != null &&
                filterType.sWiftCode.trim().length() > 0) {
                filterSql += " and a.FSwiftCode ='" +
                    filterType.sWiftCode.replaceAll("'", "''") + "'";
            }
            if (filterType.sSwiftStatus != null &&
                filterType.sSwiftStatus.trim().length() > 0) {
                filterSql += " and a.FSwiftStatus ='" +
                    filterType.sSwiftStatus.replaceAll("'", "''") + "'";
            }
        }
        return filterSql;

    }

    /**
     * listView2 loading
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        return "";
    }

    /**
     * listView3 loading
     * @return String
     * @throws YssException
     */
    public String getListViewData3() throws YssException {
        return "";
    }

    /**
     * listView4 loading
     * listView 数据获取的模式4
     * @return String
     * @throws YssException
     */
    public String getListViewData4() throws YssException {
        return "";
    }

    /**
     * 获取修改前的数据
     * @return String
     * @throws YssException
     */
    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 解析
     * @param sRowStr String
     * @throws YssException
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
            this.sRecycled = sRowStr;

            reqAry = sTmpStr.split("\t");
            this.sWiftType = reqAry[0]; //报文类型
            this.sStatus = reqAry[1]; //状态
            this.sIndex = reqAry[2]; //内容序号
            this.sTag = reqAry[3]; //报文标识
            this.sQualifier = reqAry[4]; //限定符
            this.sFieldName = reqAry[5]; //字段简称
            this.sFieldFullName = reqAry[6]; ////字段全称
            this.sContent = reqAry[7]; //内容
            this.sOption = reqAry[8]; //选项
            this.sTableField = reqAry[9]; //表字段代码

            // this.sOldWiftType = reqAry[10];//主键 报文类型
            this.checkStateId = YssFun.toInt(reqAry[10]); //状态 2 为删除,从页面来,在基类中已经定义
            //this.sSwiftStatus=reqAry[11];//add by libo 加入主键 解析报文原状态 
            this.sSwiftStatus=reqAry[12];//modify jiangshichao //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            //this.sOldWiftType=reqAry[12];//旧报文类型 //by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
            //this.sOldSwiftStatus=reqAry[13];//旧的报文状态 //by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
            //this.sTableFieldName=reqAry[11];//表字段名
            //this.sWiftCode = reqAry[14]; //报文代码MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
            this.sWiftCode = reqAry[11];//modify jiangshichao 
            //super.parseRecLog(); //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoSwiftEntitySet();
                    this.filterType.setYssPub(pub); //全局的东西
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析报文请求信息出错", e);
        }

    }

    /**
     * 编码
     * @return String
     * @throws YssException
     */
    public String buildRowStr() { //from--add  libo
        StringBuffer buf = new StringBuffer();
        buf.append(this.sWiftType).append("\t");
        buf.append(this.sStatus).append("\t");
        buf.append(this.sIndex).append("\t");
        buf.append(this.sTag).append("\t");
        buf.append( (this.sQualifier).equals(" ") ? "" : this.sQualifier).append("\t"); //edited by libo 20090703数据为空时的处理
        buf.append(this.sFieldName).append("\t");
        buf.append(this.sFieldFullName).append("\t");
        buf.append(this.sContent.trim()).append("\t");
        buf.append(this.sOption.trim()).append("\t");
        buf.append(this.sTableField.trim()).append("\t");

        buf.append(this.sTableFieldName).append("\t");
        buf.append(this.sStatusName).append("\t");
        buf.append(this.sWiftCode).append("\t");//MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
        buf.append(super.buildRecLog());
        return buf.toString();
    } //end--add  libo

    /**
     * 获取特定的数据
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {

        this.sWiftType = rs.getString("FSwiftType"); //报文类型
        this.sWiftCode = rs.getString("FSwiftCode"); //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
        this.sStatus = rs.getString("FStatus"); //报文状态
        this.sIndex = rs.getString("FIndex"); //内容序号
        this.sTag = rs.getString("FTag"); //报文标识
        this.sQualifier = (rs.getString("FQualifier") == null ? "" : rs.getString("FQualifier")); //限定符
        this.sFieldName = (rs.getString("FFieldName") == null ? "" : rs.getString("FFieldName")); //字段简称
        this.sFieldFullName = (rs.getString("FFieldFullName") == null ? "" : rs.getString("FFieldFullName")); //字段全称
        this.sContent = (rs.getString("FContent") == null ? "" : rs.getString("FContent")); //内容
        this.sOption = (rs.getString("FOption") == null ? "" : rs.getString("FOption")); //选项
        this.sTableField = (rs.getString("FTableField") == null ? "" : rs.getString("FTableField")); //表字段
        this.sTableFieldName = (rs.getString("FTableFieldName") == null ? "" : rs.getString("FTableFieldName")); //表字段名 修改时显示
        this.sStatusName = (rs.getString("FStatusName") == null ? "" : rs.getString("FStatusName")); //add by libo 20090608
        super.setRecLog(rs);
    }

    public void setSWiftType(String sWiftType) {
        this.sWiftType = sWiftType;
    }

    public void setSStatus(String sStatus) {
        this.sStatus = sStatus;
    }

    public void setSIndex(String sIndex) {
        this.sIndex = sIndex;
    }

    public void setSTag(String sTag) {
        this.sTag = sTag;
    }

    public void setSQualifier(String sQualifier) {
        this.sQualifier = sQualifier;
    }

    public void setSFieldName(String sFieldName) {
        this.sFieldName = sFieldName;
    }

    public void setSFieldFullName(String sFieldFullName) {
        this.sFieldFullName = sFieldFullName;
    }

    public void setSContent(String sContent) {
        this.sContent = sContent;
    }

    public void setSOption(String sOption) {
        this.sOption = sOption;
    }

    public void setSTableField(String sTableField) {
        this.sTableField = sTableField;
    }
//by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
//    public void setSOldWiftType(String sOldWiftType) {
//        this.sOldWiftType = sOldWiftType;
//    }

    public void setSStatusName(String sStatusName) {
        this.sStatusName = sStatusName;
    }

    public void setSSwiftStatus(String sSwiftStatus) {
        this.sSwiftStatus = sSwiftStatus;
    }
//by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
//    public void setSOldSwiftStatus(String sOldSwiftStatus) {
//        this.sOldSwiftStatus = sOldSwiftStatus;
//    }
	
    public String getSWiftType() {
        return sWiftType;
    }

    public String getSStatus() {
        return sStatus;
    }

    public String getSIndex() {
        return sIndex;
    }

    public String getSTag() {
        return sTag;
    }

    public String getSQualifier() {
        return sQualifier;
    }

    public String getSFieldName() {
        return sFieldName;
    }

    public String getSFieldFullName() {
        return sFieldFullName;
    }

    public String getSContent() {
        return sContent;
    }

    public String getSOption() {
        return sOption;
    }

    public String getSTableField() {
        return sTableField;
    }
//by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
//    public String getSOldWiftType() {
//        return sOldWiftType;
//    }

    public String getSStatusName() {
        return sStatusName;
    }

    public String getSSwiftStatus() {
        return sSwiftStatus;
    }
//by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
//    public String getSOldSwiftStatus() {
//        return sOldSwiftStatus;
//    }
	
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
