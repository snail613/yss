package com.yss.main.cusreport;

import java.sql.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.*;
import oracle.jdbc.*;
import oracle.sql.*;

public class RepDataSourceBean
    extends BaseDataSettingBean implements IDataSetting {

    private String repDsCode = "";
    private String repDsName = "";
    private String dsType = "";
    private String tempTab = "";
    private String storageTab = ""; //2008.11.04 修改 linjunyun 用于描述存储表表名
    private String dataSource = "";
    private String fillRange = "";
    private String beginRow = "";
    private String row = "";
    private String beginCol = "";
    private String col = "";
    private String strIsOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private String beanID = "";
    private String desc = "";
    private String sShow = "";
    private String oldRepDsCode = "";
    private RepDataSourceBean FilterType;
    private String sColorStyle = "";
    private String sShowStyle = "";
    private String strDsField = "";

    //private int ifcopy = 0;
    private String tRowColor = "";
    private String bRowColor = "";
    private String sRecycled = ""; //回收站  MS00326  报表数据源设置中回收站“清除”、“还原”功能不可用 xuqiji 2009 03 25

    //--- add by jiangshichao 2010.05.31
    private String sColorFilter = "";// 亮色筛选
    private String sdPCodes = "";//预处理代码组
    
    private String paramDSSynRow = "0"; //add by huangqirong 2012-05-23 story #2473 同步参数数据源行数
    private String paramDSSynCount = "1"; //add by huangqirong 2012-05-23 story #2473 同步参数数据源行数
    
    /*add by huangqirong 2012-05-23 story #2473*/
    public String getParamDSSynRow() {
		return paramDSSynRow;
	}

    /*add by huangqirong 2012-05-23 story #2473*/
	public void setParamDSSynRow(String paramDSSynRow) {
		this.paramDSSynRow = paramDSSynRow;
	}
	
	/*add by huangqirong 2012-05-23 story #2473*/
    public String getParamDSSynCount() {
		return this.paramDSSynCount;
	}

    /*add by huangqirong 2012-05-23 story #2473*/
	public void setParamDSSynCount(String paramDSSynCount) {
		this.paramDSSynCount = paramDSSynCount;
	}

	//add by huangqirong 2011-10-18 story #1747
    private String sFixRows="0";
    private String sIsExport="1";
    
    public String FixRows(){
    	return sFixRows;
    }
    
    public String IsExport(){
    	return sIsExport;
    }
    //---end---
    
    public String getDPCodes() {
        return sdPCodes;
    }

	public String getsColorStyle() {
		return sColorStyle;
	}

	public void setsColorStyle(String sColorStyle) {
		this.sColorStyle = sColorStyle;
	}

	public String getsShowStyle() {
		return sShowStyle;
	}

	public void setsShowStyle(String sShowStyle) {
		this.sShowStyle = sShowStyle;
	}

	public void setDPCodes(String sDPCodes) {
		/**shashijie 2012-7-2 STORY 2475 */
        this.sdPCodes = sDPCodes;
		/**end*/
    }
    
    public String getsColorFilter() {
		return sColorFilter;
	}

	public void setsColorFilter(String sColorFilter) {
		this.sColorFilter = sColorFilter;
	}
    //---------------------------------------------
    
    public void setRepDsCode(String repDsCode) {
        this.repDsCode = repDsCode;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public void setFillRange(String fillRange) {
        this.fillRange = fillRange;
    }

    public void setRepDsName(String repDsName) {
        this.repDsName = repDsName;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public void setBRowColor(String bRowColor) {
        this.bRowColor = bRowColor;
    }

    public void setTRowColor(String tRowColor) {
        this.tRowColor = tRowColor;
    }

    public void setTempTab(String tempTab) {
        this.tempTab = tempTab;
    }

    public void setStorageTab(String storageTab) {
        this.storageTab = storageTab;
    }

    public void setBeanID(String beanID) {
        this.beanID = beanID;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setSShow(String sShow) {
        this.sShow = sShow;
    }

    public String getRepDsCode() {
        return this.repDsCode;
    }

    public String getRow() {
        return row;
    }

    public String getFillRange() {
        return fillRange;
    }

    public String getRepDsName() {
        return repDsName;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getDsType() {
        return dsType;
    }

    public String getBRowColor() {
        return bRowColor;
    }

    public String getTRowColor() {
        return tRowColor;
    }

    public String getTempTab() {
        return tempTab;
    }

    public String getStorageTab() {
        return storageTab;
    }

    public String getBeanID() {
        return beanID;
    }

    public String getDesc() {
        return desc;
    }

    public String getSShow() {
        return sShow;
    }

    public RepDataSourceBean() {
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;
        sRecycled = sRowStr;
        try {
            if (sRowStr.equals("")) {
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
            
            //if (sRowStr.indexOf("\f\f\f") >= 0){
            // if (sRowStr.split("\f\f\f")[1].equalsIgnoreCase("copy"))
            //{
            //ifcopy = 1;
            //sRowStr = sRowStr.split("\f\f\f")[0];
            //}
            //}
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.strDsField = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            this.repDsCode = reqAry[0];
            this.repDsName = reqAry[1];
            this.dsType = reqAry[2];
            this.tempTab = reqAry[3];
            this.dataSource = reqAry[4];
            this.dataSource = this.dataSource.replaceAll("~¤~", "\r\n"); //采用~¤~换\r\n，与回收站回原、清空时的方法里的转义符冲突 QDV4赢时胜上海2009年4月28日01_B MS00419 20090525 liyu
            this.fillRange = reqAry[5];
            this.beanID = reqAry[6];
            this.desc = reqAry[7];
            this.tRowColor = reqAry[8];
            this.bRowColor = reqAry[9];
            this.checkStateId = Integer.parseInt(reqAry[10]);
            this.oldRepDsCode = reqAry[11];
            this.strIsOnlyColumns = reqAry[12];
            this.storageTab = reqAry[13]; //设定存储表表名
            this.sShow = reqAry[14];
            this.sdPCodes = reqAry[15];// add by jiangshichao 2010.05.31
            this.sShowStyle = reqAry[16];
            this.sColorStyle = reqAry[17];
            this.sFixRows=reqAry[18];	//add by huangqirong 2011-10-18 story #1747
            this.sIsExport=reqAry[19];	//add by huangqirong 2011-10-18 story #1747
            this.paramDSSynRow = reqAry[20]; //add by huangqirong 2012-05-23 story #2473
            this.paramDSSynCount = reqAry[21]; //add by huangqirong 2012-05-23 story #2473
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new RepDataSourceBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析报表数据源设置请求出错", e);
        }

    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.FilterType != null) {
            sResult = " where 1=1";
            if (this.FilterType.strIsOnlyColumns.equals("0")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.FilterType.repDsCode.length() != 0) {
                sResult = sResult + " and a.FRepDsCode like '" +
                    this.FilterType.repDsCode.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.repDsName.length() != 0) {
                sResult = sResult + " and a.FRepDsName like '" +
                    this.FilterType.repDsName.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    this.FilterType.desc.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.dataSource.length() != 0) {
                sResult = sResult + " and a.FDataSource like '" +
                    this.FilterType.dataSource.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.beanID.length() != 0) {
                sResult = sResult + " and a.FBeanId like '" +
                    this.FilterType.beanID.replaceAll("'", "''") + "%'";
            }

            if (!this.FilterType.dsType.equalsIgnoreCase("99") &&
                this.FilterType.dsType.length() != 0) {
                sResult = sResult + " and a.FDsType = " + this.FilterType.dsType;
            }
            if (!this.FilterType.tempTab.equalsIgnoreCase("99") &&
                this.FilterType.tempTab.length() != 0) {
                sResult = sResult + " and a.FTempTab = " + this.FilterType.tempTab;
            }
            //增加存储表在查询数据源信息中的过滤条件
            if (!this.FilterType.storageTab.equalsIgnoreCase("99") &&
                this.FilterType.storageTab.length() != 0) {
                sResult = sResult + " and a.FStorageTab = " +
                    dbl.sqlString(this.FilterType.storageTab);
            }
        }

        return sResult;
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FVocName as FDsTypeValue,e.FVocName as FTempTabValue, f.FVocName as FStorageTabValue from " +
                pub.yssGetTableName("Tb_Rep_DataSource") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //2007.12.01 修改 蒋锦 使用dbl.sqlToChar()处理"a.FDsType"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FDsType") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RDS_DSTYPE) +
                " left join Tb_Fun_Vocabulary e on a.FTempTab = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RDS_TEMPTAB) +
                //2008.11.07 修改 linjunyun 左连接词汇表中描述存储表的词汇信息
                " left join Tb_Fun_Vocabulary f on a.FStorageTab = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RDS_STORAGETAB) +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {

                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                /*{
                     setResultSetAttr(rs);
                    bufShow.append(rs.getString("FRepDsCode") + "").append("\t");
                    bufShow.append(rs.getString("FRepDsName") + "").append("\t");
                    bufShow.append(rs.getString("FDsTypeValue") + "").append("\t");
                 bufShow.append(rs.getString("FTempTabValue") + "").append("\t");
                    bufShow.append("").append("\t");
                  //  bufShow.append(rs.getString("FFillRange") + "").append("\t");
                 //   bufShow.append(rs.getString("FBeanId") + "").append("\t");
                    bufShow.append(rs.getString("FTRowColor") + "").append("\t");
                    bufShow.append(rs.getString("FBRowColor") + "").append("\t");
                     bufShow.append(rs.getString("FDesc") + "").append("\t");
                    super.setRecLog(rs);
                    bufShow.append(YssCons.YSS_LINESPLITMARK);
                 }
                 */
                setResultSetAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_RDS_DSTYPE + "," +
                                        YssCons.YSS_RDS_TEMPTAB + "," +
                                        //2008.11.07 修改 linjunyun 在voc中增加存储表下拉框的值
                                        YssCons.YSS_RDS_STORAGETAB);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取报表数据源设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "数据源代码\t数据源名称";
            strSql = strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Rep_DataSource") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FRepDsCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FRepDsName") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
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
            throw new YssException("获取报表数据源数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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

    public void addFields() throws YssException, SQLException {
        Connection conn = dbl.loadConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                "select FRepDsCode, FOrderIndex, FDsField, FTotalInd, FIsTotal from " +
                pub.yssGetTableName("Tb_Rep_DsField") +
                " where  FRepDsCode = " + dbl.sqlString(this.oldRepDsCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                strSql = "insert into " + pub.yssGetTableName("Tb_Rep_DsField") +
                    "(FRepDsCode, FOrderIndex, FDsField, FTotalInd, FIsTotal, " +
                    " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values("
                    + dbl.sqlString(this.repDsCode) + ","
                    + rs.getInt("FOrderIndex") + ","
                    + rs.getString("FDsField") + ","
                    + rs.getString("FTotalInd") + ","
                    + rs.getInt("FISTOTAL") + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    ")";
                stmt.addBatch(strSql);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            stmt.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增报表数据源数据字段出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        // return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        ResultSet rs = null;
        try {
            this.storageTabOccupied(); //验证存储表是否已被占用 sunkey Bug:0000515
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Rep_DataSource") +
                "(FRepDsCode,FRepDsName,FDsType,FDPCodes,FTempTab,FStorageTab,FDataSource,FFillRange," +//添加字段 FDPCodes add by jiangshichao 2010.05.31
                "FFIXROWS,FISEXPORT,"+	//add by huangqirong 2011-10-18 story #1747
                "FParamDSSynRow," + //add by huangqirong 2012-05-23 story #2473
                "FParamDSSynCount," + //add by huangqirong 2012-05-23 story #2473
                "FBeanId, FDesc,FTRowColor,FBRowColor,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.repDsCode) + "," +
                dbl.sqlString(this.repDsName) + "," +
                this.dsType + "," +
                dbl.sqlString(this.sdPCodes)+"," + //add by jiangshichao 2010.05.31
                this.tempTab + ",";
            if ("0".equalsIgnoreCase(this.storageTab)) { //2008.11.04 修改 linjunyun 存储表类型的下拉框对于默认选项不进行数据库插入操作
                strSql = strSql + "null,";
            } else {
                strSql = strSql + dbl.sqlString(this.storageTab) + ",";
            }
            if (dbl.getDBType() == YssCons.DB_ORA) { //2007.12.04 修改 蒋锦 添加了数据库类型的判断，不同类型数据库的CLOB类型插入方法不同
                strSql = strSql + "EMPTY_CLOB()" + ",";
            } else {
                strSql = strSql + dbl.sqlString(this.dataSource) + ",";
            }
            strSql = strSql + dbl.sqlString(this.fillRange) + "," +
            	dbl.sqlString(this.sFixRows)+","+dbl.sqlString(this.sIsExport)+","+				//add by huangqirong 2011-10-18 story #1747
                dbl.sqlString(this.paramDSSynRow) +"," +	//add by huangqirong 2012-05-23 story #2473
                dbl.sqlString(this.paramDSSynCount) +"," +	//add by huangqirong 2012-05-23 story #2473
            	dbl.sqlString(this.beanID) + "," +
                dbl.sqlString(this.desc) + "," +
                YssFun.toInt(this.tRowColor) + "," +
                YssFun.toInt(this.bRowColor) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //-----------------------------------------------------------------------
            if (dbl.getDBType() == YssCons.DB_ORA) {
                strSql = "select FDataSource from " +
                    pub.yssGetTableName("Tb_Rep_DataSource") +
                    " where FRepDsCode= " +
                    dbl.sqlString(this.repDsCode);
                rs = dbl.openResultSet(strSql);
                long i = 1;
                if (rs.next()) {
                	//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	// modify by jsc 20120809 连接池对大对象的特殊处理
                    //CLOB clob = ( (OracleResultSet) rs).getCLOB("FDataSource");
                	CLOB clob = dbl.CastToCLOB(rs.getClob("FDataSource"));
                    clob.putString(1, this.dataSource);
                    String sql =
                        "update " + pub.yssGetTableName("Tb_Rep_DataSource") +
                        " set FDataSource=? where FRepDsCode=" +
                        dbl.sqlString(this.repDsCode);
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setClob(1, clob);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增报表数据源数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Rep_DataSource"),
                               "FRepDsCode",
                               this.repDsCode, this.oldRepDsCode);
    }

    /**
     * checkSetting  MS00326  报表数据源设置中回收站“清除”、“还原”功能不可用  xuqiji 2009 03 25  可以批量还原数据
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = true; //代表是否开始了事务
        String strSql = "";
        String[] arrData = null;
        Statement st = null;
        try {
            if (null != sRecycled && !"".equals(sRecycled.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                arrData = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                st = conn.createStatement();
                for (int i = 0; i < arrData.length; i++) { //循环执行这些还原语句
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    dataSource = dataSource.replaceAll("~¤~", "\r\n"); //采用~¤~换\r\n，与回收站回原、清空时的方法转义符冲突QDV4赢时胜上海2009年4月28日01_B MS00419 20090525 liyu
                    strSql = "update " + pub.yssGetTableName("Tb_Rep_DataSource") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FRepDsCode = " + dbl.sqlString(this.repDsCode);
                    conn.setAutoCommit(false);
                    st.addBatch(strSql);
                    strSql = "update " + pub.yssGetTableName("Tb_Rep_DsField") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FRepDsCode = " + dbl.sqlString(this.repDsCode);
                    st.addBatch(strSql);
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核报表数据源数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
        }
//-----------------------------MS00326 报表数据源设置中回收站“清除”、“还原”功能不可用  xuqiji 2009 03 25
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_DataSource") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FRepDsCode = " + dbl.sqlString(this.repDsCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("Tb_Rep_DsField") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FRepDsCode = " + dbl.sqlString(this.repDsCode);

            dbl.executeSql(strSql);

            strSql = "delete from " + pub.yssGetTableName("Tb_Rep_Cell") +
                " where FRelaType = 'DSF' and FRelaCode = " +
                dbl.sqlString(this.repDsCode);

            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除报表数据源数据出错", e);
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
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        ResultSet rs = null;
        try {
            this.storageTabOccupied(); //验证存储表是否已被占用 sunkey 20081105 Bug:0000515
            strSql =
                "update " + pub.yssGetTableName("Tb_Rep_DataSource") +
                " set FRepDsCode = " + dbl.sqlString(this.repDsCode) +
                ",FRepDsName = " + dbl.sqlString(this.repDsName) +
                ",FDsType = " + this.dsType +
                ",FDPCodes = "+dbl.sqlString(this.sdPCodes)+
                ",FTempTab = " + dbl.sqlString(this.tempTab); //彭鹏 2008.2.3 修改
            if ("0".equalsIgnoreCase(this.storageTab)) { //2008.11.04 linjunyun 修改 存储表类型的下拉框对于默认选项不进行数据库更新操作
                strSql = strSql + ",FStorageTab = null";
            } else {
                strSql = strSql + ",FStorageTab = " + dbl.sqlString(this.storageTab);
            }
            if (dbl.getDBType() == YssCons.DB_ORA) { //2007.12.04 修改 蒋锦 添加了数据库类型的判断，不同类型数据库的CLOB类型插入方法不同
                strSql = strSql + ",FDataSource = " + "EMPTY_CLOB()";
            } else {
                strSql = strSql + ",FDataSource = " + dbl.sqlString(this.dataSource);
            }
            strSql = strSql + ",FFillRange = " + dbl.sqlString(this.fillRange) +
            	",FFIXROWS = "+dbl.sqlString(this.sFixRows)+", FISEXPORT = "+dbl.sqlString(this.sIsExport)+	//add by huangqirong 2011-10-18 story #1747            
            	",FParamDSSynRow = " + dbl.sqlString(this.paramDSSynRow) + //add by huangqirong 2012-05-23 story #2473
            	",FParamDSSynCount = " + dbl.sqlString(this.paramDSSynCount) + //add by huangqirong 2012-05-23 story #2473
            	",FBeanId = " + dbl.sqlString(this.beanID) +
                ",FDesc = " + dbl.sqlString(this.desc) +
                ",FTRowColor = " + YssFun.toInt(this.tRowColor) +
                ",FBRowColor = " + YssFun.toInt(this.bRowColor) +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                " where FRepDsCode = " + dbl.sqlString(this.oldRepDsCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            if(!this.sColorStyle.equalsIgnoreCase("")&&!this.sShowStyle.equalsIgnoreCase("")){
            	
            	strSql = "update " + pub.yssGetTableName("Tb_Rep_ColorFilter") +
                " set fcolorstyle = " + dbl.sqlString(this.sColorStyle) +
                ",fshowstyle = " + dbl.sqlString(this.sShowStyle) +
                " where FREPDSCode = " +dbl.sqlString(this.oldRepDsCode);
                dbl.executeSql(strSql);
            	
            }
            

            if (dbl.getDBType() == YssCons.DB_ORA) { //2007.12.04 修改 蒋锦 添加了数据库类型的判断，不同类型数据库的CLOB类型插入方法不同
                strSql = "select FDataSource from " +
                    pub.yssGetTableName("Tb_Rep_DataSource") +
                    " where FRepDsCode= " +
                    dbl.sqlString(this.repDsCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                    //CLOB clob = ( (OracleResultSet) rs).getCLOB("FDataSource");
                    CLOB clob = dbl.CastToCLOB(rs.getClob("FDataSource"));
                    clob.putString(1, this.dataSource);
                    String sql = "update " + pub.yssGetTableName("Tb_Rep_DataSource") +
                        " set FDataSource=? where FRepDsCode=" +
                        dbl.sqlString(this.oldRepDsCode);
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setClob(1, clob);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
                rs.close();
            }
            //if (this.oldRepDsCode != this.repDsCode ) {
            if(!this.oldRepDsCode.equalsIgnoreCase(this.repDsCode)){
            	strSql = " select * from " + pub.yssGetTableName("Tb_Rep_DSField") +  " where FREPDSCode = " +   dbl.sqlString(this.repDsCode);
            	rs = dbl.openResultSet(strSql);
            	if(rs.next())//BUG5562 modifie yeshenghong  20120912
            	{
            	     strSql = " delete from " + pub.yssGetTableName("Tb_Rep_DSField") +  " where FREPDSCode = " +   dbl.sqlString(this.repDsCode);
            	     dbl.executeSql(strSql);
            	}
            	dbl.closeResultSetFinal(rs);
            	
                strSql = "update " + pub.yssGetTableName("Tb_Rep_DSField") +
                    " set FREPDSCode = " +
                    dbl.sqlString(this.repDsCode) +
                    " where FREPDSCode = " +
                    dbl.sqlString(this.oldRepDsCode);
                dbl.executeSql(strSql);

                strSql = "update " + pub.yssGetTableName("Tb_Rep_Cell") +
                    " set FRelaCode = " +
                    dbl.sqlString(this.repDsCode) +
                    " where FRelaType = 'DSF' and FRelaCode = " +
                    dbl.sqlString(this.oldRepDsCode);
                dbl.executeSql(strSql);
                
                String ColorFiltersql= "";
                if(!this.sColorStyle.equalsIgnoreCase("")&&!this.sShowStyle.equalsIgnoreCase("")){
                	
                	ColorFiltersql = ",fcolorstyle = " + dbl.sqlString(this.sColorStyle)+",fshowstyle = " + dbl.sqlString(this.sShowStyle);
                }
                
                strSql = "update " + pub.yssGetTableName("Tb_Rep_ColorFilter") +
                " set FREPDSCode = " +dbl.sqlString(this.repDsCode)+ColorFiltersql+
                " where FREPDSCode = " +dbl.sqlString(this.oldRepDsCode);
            dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改报表数据源数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }

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
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.* from " +
                pub.yssGetTableName("Tb_Rep_DataSource") + " a " +
                " where FCheckState = 1 and FRepDsCode = " +
                dbl.sqlString(repDsCode) +
                " order by FCheckState, FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.repDsCode = rs.getString("FRepDsCode") + "";
                this.repDsName = rs.getString("FRepDsName") + "";
                this.dsType = rs.getString("FDsType") + "";
                this.tempTab = rs.getString("FTempTab") + "";
                this.storageTab = rs.getString("FStorageTab") + ""; //2008.11.07 修改 linjunyun 初始化存储表的值
                this.dataSource = dbl.clobStrValue(rs.getClob("FDataSource")) + "";
                this.fillRange = rs.getString("FFillRange") + "";
                this.beanID = rs.getString("FBeanId") + "";
                this.desc = rs.getString("FDesc") + "";
                this.tRowColor = rs.getString("FTRowColor");
                this.bRowColor = rs.getString("FBRowColor");
                if(dbl.isFieldExist(rs, "fdpcodes")){
                	this.sdPCodes = rs.getString("fdpcodes");
                }
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取报表自定义设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
    public String getTreeViewData3() throws YssException {
        String sShowDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FVocName as FDsTypeValue,e.FVocName as FTempTabValue from " +
                pub.yssGetTableName("Tb_Rep_DataSource") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FDsType") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RDS_DSTYPE) +
                " left join Tb_Fun_Vocabulary e on a.FTempTab = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RDS_TEMPTAB) +
                //2008.11.07 修改 linjunyun  左连接词汇表中描述存储表的词汇信息
                " left join Tb_Fun_Vocabulary f on a.FStorageTab = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RDS_STORAGETAB) +
                " where a.FRepDsCode = '" + this.oldRepDsCode +
                "' order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.sShow = "true";
                this.setResultSetAttr(rs);
                bufShow.append(this.buildRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);

            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            return sShowDataStr; //返回单条数据源代码
        } catch (Exception e) {
            throw new YssException("获取报表数据源设置信息出错", e);
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
        buf.append(this.repDsCode).append("\t");
        buf.append(this.repDsName).append("\t");
        buf.append(this.dsType).append("\t");
        buf.append(this.sdPCodes).append("\t");//add by jiangshichao 2010.05.31
        buf.append(this.sColorFilter).append("\t");//add by jiangshichao 2010.07.11 
        buf.append(this.tempTab).append("\t");
        buf.append(this.dataSource).append("\t");
        buf.append(this.fillRange).append("\t");
        buf.append(this.beanID).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.tRowColor).append("\t");
        buf.append(this.bRowColor).append("\t");
        buf.append(this.storageTab).append("\t");
        buf.append(this.sFixRows).append("\t");//add by huangqirong 2011-10-18 story #1747
        buf.append(this.sIsExport).append("\t");//add by huangqirong 2011-10-18 story #1747
        buf.append(this.paramDSSynRow).append("\t"); //add by huangqirong 2012-05-23 story #2473
        buf.append(this.paramDSSynCount).append("\t"); //add by huangqirong 2012-05-23 story #2473
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    protected String buildSql(String sDs) throws YssException {
        String sInd = ""; //参数的标识
        String sDataType = ""; //数据类型的标识 S:字符型,I:数字型,D:日期型
        int iPos = 0;
        String sSqlValue = "";
        for (int i = 0; i < 100; i++) {
            sInd = "<" + (i + 1) + ">";
            iPos = sDs.indexOf(sInd);
            if (iPos <= 0) {
                sInd = " < " + (i + 1) + " >";
                iPos = sDs.indexOf(sInd);
            }
            if (iPos > 0) {
                sDataType = sDs.substring(iPos - 1, iPos);
                if (sDataType.equalsIgnoreCase("S")) {
                    sSqlValue = dbl.sqlString("");
                } else if (sDataType.equalsIgnoreCase("I")) {
                    sSqlValue = "0";
                } else if (sDataType.equalsIgnoreCase("D")) {
                    sSqlValue = dbl.sqlDate("1900-01-01");
                } else if (sDataType.equalsIgnoreCase("N")) {
                    sSqlValue = "''";
                }
                sDs = sDs.replaceAll(sDataType + sInd, sSqlValue);
                sDs = pretSqlIns(sDs);
            }
//         else {
//            break;
//         }
           
        }
        
        //------------------处理财务表中的部分数据的替换 by leeyu 080729-----
        if (sDs.indexOf("<Year>") > 0) {
            sDs = sDs.replaceAll("<Year>",
                                 YssFun.formatDate(new java.util.Date(), "yyyy"));
        } else if (sDs.indexOf("< Year >") > 0) {
            sDs = sDs.replaceAll("< Year >",
                                 YssFun.formatDate(new java.util.Date(), "yyyy"));
        }
        if (sDs.indexOf("<Set>") > 0) {
            sDs = sDs.replaceAll("<Set>", "001");
        } else if (sDs.indexOf("< Set >") > 0) {
            sDs = sDs.replaceAll("< Set >", "001");
        }
        //-------------------------END-----
        BaseReportBean reportBean = new BaseReportBean();
        reportBean.setYssPub(pub);
        sDs = reportBean.pretExpress(sDs);

        sDs = sDs.replace('[', ' ');
        sDs = sDs.replace(']', ' ');
//      sDs = sDs.replaceAll("<U>",pub.getUserCode());
        if (sDs.indexOf("<U>") > 0) {
            sDs = sDs.replaceAll("<U>", pub.getUserCode());
        } else if (sDs.indexOf("< U >") > 0) {
            sDs = sDs.replaceAll("< U >", pub.getUserCode());
        }
        //--------------------------------------------------//<Group>为了替换表中的组合群如"001" sj edit 20080307
        if (sDs.indexOf("<Group>") > 0) {
            sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
        } else if (sDs.indexOf("< Group >") > 0) {
            sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
        }
        //--------------------------------------------------
        //--------------------------------------------------//<User>为了替换表中的用户 sj edit 20081107
        if (sDs.indexOf("<User>") > 0) {
            sDs = sDs.replaceAll("<User>", pub.getAssetGroupCode());
        } else if (sDs.indexOf("< User >") > 0) {
            sDs = sDs.replaceAll("< User >", pub.getAssetGroupCode());
        }
        //--------------------------------------------------
        //add by licai 20110210 STORY #441 需优化现在的报表自定义模板	
        //增加了4个标记符号导致点击数据源字段配置报错,此处将标记写死,暂时未找到替代方案
        if(sDs.indexOf("<DynColumnCode>")>0){//组合代码等动态列条件
        	sDs=sDs.replaceAll("<DynColumnCode>", dbl.sqlString(""));
		}else if(sDs.indexOf("< DynColumnCode >")>0){
			sDs=sDs.replaceAll("< DynColumnCode >", dbl.sqlString(""));
		}
		if(sDs.indexOf("<DynRowCodes>")>0){//多券商代码等动态行条件
			sDs=sDs.replaceAll("<DynRowCodes>", "("+operSql.sqlCodes("")+")");
		}else if(sDs.indexOf("< DynRowCodes >")>0){
			sDs=sDs.replaceAll("< DynRowCodes >", "("+operSql.sqlCodes("")+")");
		}				
		if(sDs.indexOf("<RptStyle>")>0){//('报表格式代码','报表数据源')
			sDs=sDs.replaceAll("<RptStyle>", "("+operSql.sqlCodes("")+")");
		}else if(sDs.indexOf("< RptStyle >")>0){
			sDs=sDs.replaceAll("< RptStyle >", "("+operSql.sqlCodes("")+")");
		}		
		if(sDs.indexOf("<RptCode>")>0){//自定义报表代码
			sDs=sDs.replaceAll("<RptCode>",dbl.sqlString("") );
		}else if(sDs.indexOf("< RptCode >")>0){
			sDs=sDs.replaceAll("< RptCode >",dbl.sqlString("") );
		}
		//edit by licai 20110221 STORY #441=====================end

        sDs = sDs.replaceAll("~Base", "0");
        return sDs;
    }

    /**
	 * add by wangzuochun 2009.10.22 MS00750    现金头寸预测报表中，取数据时需要判断数据的节假日情况    
     * 处理函数 WDay[参数1,参数2,参数3]  取工作日前一天
     * 参数1:传入的日期    参数2: 节假日群   参数3:相差天数
     * @param sSql String
     * @return String
     * (WDay){1}[\\[](.)*[;](.)*[;](.)*[\\]]
     * @throws YssException
     */
    private String pretSqlIns(String sSql) throws YssException {
        String sFunCode = ""; //函数名
        String strReplace = ""; //要替代的字符串
        String strCalc = ""; //通过计算得到的字符串
        String sParams = ""; //相关参数字符串
        BaseOperDeal deal = new BaseOperDeal();
        try {
        	deal.setYssPub(pub);
            if (sSql.indexOf("[") > 0 && sSql.indexOf("]") > 0) {
                if (sSql.indexOf("]") > sSql.indexOf("[")) { //确保"]" 在"[" 的后面
                    sParams = sSql.substring(sSql.indexOf("[") + 1,
                                             sSql.indexOf("]"));
                    sFunCode = sSql.substring(sSql.indexOf("[") - 4,
                                              sSql.indexOf("["));
                    if (sFunCode.equalsIgnoreCase("WDay")) {
						strReplace = "WDay" + "\\[([^]]+)\\]";
                        strCalc = dbl.sqlDate("1900-01-01");
                    }
                    //add by luopc STORY #1434 
					//增加一个数据源函数Wdab，保证当前日期如果是节假日，返回的是上一个工作日，而不是下一个工作日 
                    if (sFunCode.equalsIgnoreCase("WDab")) {
						strReplace = "Wdab" + "\\[([^]]+)\\]";
                        strCalc = dbl.sqlDate("1900-01-01");
                    }                    
                }
            }
            sSql = sSql.replaceAll(strReplace, strCalc);
            return sSql;
        } catch (Exception e) {
            throw new YssException("解析SQL内部函数出错" + e.toString(), e);
        }
    }
    
    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String strSql = "", sReturn = "", sError = "";
        String sHeader = "", sShowDataStr = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            if (sType.equalsIgnoreCase("getField")) {
                sError = "获取数据源字段信息出错";
                sHeader = "字段名称\t字段类型";
                if (this.dsType.equalsIgnoreCase("1")) { //动态数据源
                    strSql = this.dataSource.trim();
                    strSql = buildSql(strSql);
                    rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int FieldsCount = rsmd.getColumnCount(); //原始表字段数
                    int[] fDataType = new int[FieldsCount]; //记录数据字段数据类型
                    String[] fDataTypeName = new String[FieldsCount];
                    String[] fDataName = new String[FieldsCount]; //记录数据字段名称
                    for (int i = 0; i < FieldsCount; i++) {
                        fDataName[i] = rsmd.getColumnName(i + 1);
                        fDataTypeName[i] = rsmd.getColumnTypeName(i + 1);
                        fDataType[i] = rsmd.getColumnType(i + 1);
                        buf.append(fDataName[i]).append("\t");
                        buf.append(fDataTypeName[i]).append(YssCons.YSS_LINESPLITMARK);
                    }
                } else if (dsType.equalsIgnoreCase("2")) { //固定数据源
                    BaseReportBean rep = (BaseReportBean) pub.getOperDealCtx().
                        getBean(this.beanID);
                    //获得字段得数据类型 以及  字段名称
                    String[] strData = rep.getReportFields1().split("\t");
                    for (int i = 0; i < strData.length; i++) {
                        buf.append(strData[i]).append("\t");
                        buf.append("varchar").append(YssCons.YSS_LINESPLITMARK);
                    }
                }

                if (buf.toString().length() > 2) {
                    sShowDataStr = buf.toString().substring(0,
                        buf.toString().length() - 2);
                }
                sReturn = sHeader + "\r\f" + sShowDataStr + "\r\f" + sShowDataStr;
            }
            return sReturn;
        } catch (Exception e) {
            throw new YssException(sError + "\n\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
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

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.repDsCode = rs.getString("FRepDsCode") + "";
        this.repDsName = rs.getString("FRepDsName") + "";
        this.dsType = rs.getString("FDsType") + "";
		if(dbl.isFieldExist(rs, "FDPCodes")){
        	 this.sdPCodes = rs.getString("FDPCodes")+"";
        }
        this.tempTab = rs.getString("FTempTab") + "";
        this.storageTab = rs.getString("FStorageTab") + ""; //2008.11.07 修改 linjunyun 初始化存储表的值
        if (this.sShow != null && this.sShow.toLowerCase().equals("true")) {
            this.dataSource = dbl.clobStrValue(rs.getClob("FDataSource")).
                replaceAll(
                    "\t", "   ");
        }
        //---add by songjie 2011.05.18 BUG 1873 QDV4华安2011年5月6日01_B---//
        else{
        	this.dataSource = "";
        }
        //---add by songjie 2011.05.18 BUG 1873 QDV4华安2011年5月6日01_B---//
        this.fillRange = rs.getString("FFillRange") + "";
        this.beanID = rs.getString("FBeanId") + "";
        this.desc = rs.getString("FDesc") + "";
        this.tRowColor = rs.getString("FTRowColor") + "";
        this.bRowColor = rs.getString("FBRowColor") + "";
        //------判断结果集中是否有字段，如有则赋值-----------------------------//
        if(dbl.isFieldExist(rs, "FCOLORFILTERID")){
        	this.sColorFilter = rs.getString("FCOLORFILTERID");
        }
        //-------------------------------------------------------------------//
        this.sIsExport=rs.getString("FISEXPORT");//add by huangqirong 2011-10-21 story #1747
        this.sFixRows=rs.getString("FFIXROWS")==null?"":rs.getString("FFIXROWS");//add by huangqirong 2011-10-21 story #1747
        this.paramDSSynRow = rs.getString("FParamDSSynRow"); //add by huangqirong 2012-05-23 story #2473
        this.paramDSSynCount = rs.getString("FParamDSSynCount"); //add by huangqirong 2012-05-23 story #2473
        super.setRecLog(rs);
    }

    /**
     * deleteRecycleData  MS00326  报表数据源设置中回收站“清除”、“还原”功能不可用  xuqiji 2009 03 25  可以批量清除数据
     */
    public void deleteRecycleData() throws YssException {
        String sql = "";
        String array[] = null;
        boolean bTrans = true; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //获取一个连接
        Statement st = null;
        try {
            if (null != sRecycled && !"".equals(sRecycled.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                st = conn.createStatement();
                conn.setAutoCommit(false);
                for (int i = 0; i < array.length; i++) { //循环执行这些删除语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    dataSource = dataSource.replaceAll("~¤~", "\r\n"); //采用~¤~换\r\n，与回收站回原、清空时的方法转义符冲突QDV4赢时胜上海2009年4月28日01_B MS00419 20090525 liyu
                    sql = "delete from " + //此SQL清除表Tb_Rep_DataSource中的数据
                        pub.yssGetTableName("Tb_Rep_DataSource") +
                        " where FRepDsCode = " + dbl.sqlString(this.repDsCode);
                    st.addBatch(sql); //执行sql语句
                    sql = "delete from " + //此SQL清除表Tb_Rep_DsField中的数据
                        pub.yssGetTableName("Tb_Rep_DsField") +
                        " where FRepDsCode = " + dbl.sqlString(this.repDsCode);
                    st.addBatch(sql);
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
        }
    }

    /**
     * 判断某一存储表是否被占用 Bug:0000515
     * @date 20081105
     * @author sunkey
     * @throws YssException
     */
    private void storageTabOccupied() throws YssException {
        String strSql = null;
        ResultSet rs = null;
        strSql = "select FRepDSName from " + pub.yssGetTableName("Tb_Rep_DataSource") + " where FStorageTab='" + this.storageTab + "' and frepdscode<>'" + this.repDsCode + "'";
        try {
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                throw new YssException("对不起，该存储表已被数据源【" + rs.getString("FRepDSName") + "】占用！");
            }
        } catch (YssException ex) {
            throw new YssException(ex.getMessage());
        } catch (SQLException ex) {
            throw new YssException("对不起，验证存储表占用情况时出现异常！", ex);
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
