package com.yss.main.cusreport;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class RepCustomBean
    extends BaseDataSettingBean implements IDataSetting {

    private String CusRepCode = "";
    private String CusRepName = "";
    private String RepFormatCode = "";
    private String RepFormatName = "";
    private String CtlGrpCode = "";
    private String CtlGrpName = "";
    private String ParamSourceCode = "";
    private String ParamSourceName = "";
    private String TmpTables = "";
    private String Desc = "";
    private String repType = "";
    private int maxCols;
    private String SubRepCodes = ""; //明细报表组，以“,”分隔
    private String dataSourceCodes = ""; //数据源代码，以“,”分隔
    private String oldCusRepCode = "";
    private RepCustomBean FilterType;
    private int isShowBuild=0;//add by huangqirong 2011-07-15 story #1101
    private String exportPath="";//add by huangqirong 2011-12-29 story #1284  
	private String recycledInfo; //MS00170 QDV4赢时胜上海2009年1月7日04_B add by 宋洁 2009-01-16 用于声明要被回收站删除的信息.
    private String allSetDatas = "";//add by guolongchao 20120112 STORY 1284 添加文件名集合
    private String templatePath = "";//模板路径设置    add by guolongchao 20120130 STORY 1284 
    
    public void setRepType(String repType) {
        this.repType = repType;
    }
    
    //add by huangqirong 2011-07-15 story #1101
    public void setIsShowBuild(int showBuild){
    	this.isShowBuild=showBuild;
    }
    
    public void setSubRepCodes(String subRepCodes) {
        this.SubRepCodes = subRepCodes;
    }

    public void setCtlGrpCode(String ctlGrpCode) {
        this.CtlGrpCode = ctlGrpCode;
    }

    public void setDataSourceCodes(String dataSourceCodes) {
        this.dataSourceCodes = dataSourceCodes;
    }

    public void setMaxCols(int maxCols) {
        this.maxCols = maxCols;
    }

    public void setRepFormatName(String RepFormatName) {
        this.RepFormatName = RepFormatName;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public void setTmpTables(String TmpTables) {
        this.TmpTables = TmpTables;
    }

    public void setParamSourceName(String ParamSourceName) {
        this.ParamSourceName = ParamSourceName;
    }

    public void setParamSourceCode(String ParamSourceCode) {
        this.ParamSourceCode = ParamSourceCode;
    }

    public void setCusRepName(String CusRepName) {
        this.CusRepName = CusRepName;
    }
    
    //add by huangqirong 2011-12-29 story #1284
    public String getExportPath() {
		return exportPath;
	}

	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}
	//---end---
	
   //add by huangqirong 2011-07-15 story #1101
    public int getIsShowBuild(){
    	return this.isShowBuild;
    }

    public String getRepType() {
        return repType;
    }

    public String getSubRepCodes() {
        return this.SubRepCodes;
    }

    public String getCtlGrpCode() {
        return CtlGrpCode;
    }

    public String getDataSourceCodes() {
        return dataSourceCodes;
    }

    public int getMaxCols() {
        return maxCols;
    }

    public String getRepFormatName() {
        return RepFormatName;
    }

    public String getDesc() {
        return Desc;
    }

    public String getTmpTables() {
        return TmpTables;
    }

    public String getParamSourceName() {
        return ParamSourceName;
    }

    public String getParamSourceCode() {
        return ParamSourceCode;
    }

    public String getCusRepName() {
        return CusRepName;
    }

    public RepCustomBean() {
    }

    public RepCustomBean(String sCusRepCode) {
        this.CusRepCode = sCusRepCode;
    }

    public String getCusRepCode() {
        return this.CusRepCode;
    }

    public void setCusRepCode(String sCusRepCode) {
        this.CusRepCode = sCusRepCode;
    }

    public String getRepFormatCode() {
        return this.RepFormatCode;
    }

    public void setRepFormatCode(String sRepFormatCode) {
        this.RepFormatCode = sRepFormatCode;
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        recycledInfo = sRowStr; //MS00170 QDV4赢时胜上海2009年1月7日04_B add by 宋洁 2009-01-16 用于获取回收站要清除的记录信息.
        String sTmpStr = "";
        String dataSource[] = null;
        String[] reqAry = null;

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
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];                   
                if (sRowStr.split("\r\t").length == 3) {
                    this.allSetDatas = sRowStr.split("\r\t")[2];
                }//add by guolongchao 20111228 STORY 1284-------end
            } else {
                sTmpStr = sRowStr;

            }

            reqAry = sTmpStr.split("\t");
            this.CusRepCode = reqAry[0];
            this.CusRepName = reqAry[1];
            this.RepFormatCode = reqAry[2];
            this.repType = reqAry[3];
            this.maxCols = Integer.parseInt(reqAry[4]);
            this.CtlGrpCode = reqAry[5];
            this.dataSourceCodes = reqAry[6];
            this.SubRepCodes = reqAry[7];
            this.Desc = reqAry[8];
            this.ParamSourceCode = reqAry[9];
            this.ParamSourceName = reqAry[10];
            this.TmpTables = reqAry[11];
            this.checkStateId = Integer.parseInt(reqAry[12]);
            this.oldCusRepCode = reqAry[13];
            this.isShowBuild=Integer.parseInt(reqAry[14]);//add by huangqirong 2011-07-13 story #1101
            this.exportPath=reqAry[15];		//add by huangqirong 2011-12-29 story #1284
            this.templatePath=reqAry[16];//模板路径    add by guolongchao 20120130 STORY 1284  
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new RepCustomBean();
                    this.FilterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析报表自定义设置请求出错", e);
        }
    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.FilterType != null) {
            sResult = " where 1=1";
            if (this.FilterType.CusRepCode.length() != 0) {
                sResult = sResult + " and a.FCusRepCode like '" +
                    this.FilterType.CusRepCode.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.CusRepName.length() != 0) {
                sResult = sResult + " and a.FCusRepName like '" +
                    this.FilterType.CusRepName.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.RepFormatCode.length() != 0) {
                sResult = sResult + " and a.FRepFormatCode like '" +
                    this.FilterType.RepFormatCode.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.CtlGrpCode.length() != 0) {
                sResult = sResult + " and a.FCtlGrpCode like '" +
                    this.FilterType.CtlGrpCode.replaceAll("'", "''") + "%'";
            }
            if (!this.FilterType.repType.equalsIgnoreCase("99") &&
                this.FilterType.repType.length() != 0) {
                sResult = sResult + " and a.FRepType = " + this.FilterType.repType;
            }
            if (this.FilterType.Desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    this.FilterType.Desc.replaceAll("'", "''") + "%'";
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
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FRepFormatName," +
                "e.FVocName as FRepTypeValue,f.FCtlGrpName,g.FRepDsName from " +
                pub.yssGetTableName("Tb_Rep_Custom") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FRepFormatCode,FRepFormatName from " +
                pub.yssGetTableName("Tb_Rep_Format") +
                " where FCheckState = 1) d on a.FRepFormatCode = d.FRepFormatCode" +
                " left join Tb_Fun_Vocabulary e on a.FRepType = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RCT_REPTYPE) +
                " left join (select FCtlGrpCode,FCtlGrpName from " +
                // pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                " Tb_PFSys_FaceCfgInfo " + //上面的表不用了,改为这个表 by ly 080304
                " where FCheckState = 1  group by FCtlGrpCode,FCtlGrpName) f on a.FCtlGrpCode = f.FCtlGrpCode" + //2008-6-26 单亮 添加 group by FCtlGrpCode,FCtlGrpName不加数据有重复记录

                " left join (select FRepDsCode,FRepDsName from " +
                pub.yssGetTableName("Tb_Rep_DataSource") +
                " where FCheckState = 1) g on a.FParamSource = g.FRepDsCode" +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

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
            sVocStr = vocabulary.getVoc(YssCons.YSS_RCT_REPTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取报表自定义设置信息出错", e);
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
        	/**shashijie 2012-2-8 BUG 3800 报表组设置的新增的问题 */
        	sHeader = "报表代码\t报表名称";
			/**end*/
            strSql = strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FRepFormatName,e.FCtlGrpName,f.FRepDsName from " +
                pub.yssGetTableName("Tb_Rep_Custom") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FRepFormatCode,FRepFormatName from " +
                pub.yssGetTableName("Tb_Rep_Format") +
                " where FCheckState = 1) d on a.FRepFormatCode = d.FRepFormatCode" +
                " left join (select FCtlGrpCode,FCtlGrpName from " +
                //pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                " Tb_PFSys_FaceCfgInfo " + //上面的表不用了,改为这个表 by ly 080304
                " where FCheckState = 1) e on a.FCtlGrpCode = e.FCtlGrpCode" +
                //--------------------------------------------------------------
                " left join (select FRepDsCode,FRepDsName from " +
                pub.yssGetTableName("Tb_Rep_DataSource") +
                " where FCheckState =1) f on a.FParamSource=f.FRepDsCode" +
                //--------------------------------------------------------------

                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCusRepCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCusRepName") + "").trim()).append(
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
            throw new YssException("获取明细报表数据出错", e);
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
        VocabularyBean vocabulary = new VocabularyBean();
        vocabulary.setYssPub(pub);
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sVocStr = ""; //词汇类型对照字符串
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sVocStr = vocabulary.getVoc(YssCons.Yss_REPORT_TYPE);
            //   sHeader= "报表代码\t报表名称\t报表格式代码\t控件组代码\t描述";
            sHeader = "报表代码\t报表名称\t报表格式代码\t报表格式名称\t报表类型\t控件组代码\t参数控件组名称\t描述";
            //strSql = "select a.*,b.FRepFormatName as FRepFormatName,c.FCtlGrpName as FCtlGrpName,d.FRepDsName as FRepDsName,e.FUserName as FCreatorName,f.FUserName as FCheckUserName from "+ pub.yssGetTableName("tb_Rep_Custom") + " a " +
            strSql = "select a.*,voc.FVocName as FRepTypeName,b.FRepFormatName as FRepFormatName,c.FCtlGrpName as FCtlGrpName,d.FRepDsName as FRepDsName,e.FUserName as FCreatorName,f.FUserName as FCheckUserName from " + pub.yssGetTableName("tb_Rep_Custom") + " a " +
                "left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCreator= e.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) f on a.FCheckUser = f.FUserCode" +
                " left join (select FVocTypeCode,FVocCode,FVocName from tb_fun_vocabulary) voc on a.Freptype = voc.FVocCode and voc.FVocTypeCode =  " + dbl.sqlString(YssCons.YSS_RCT_REPTYPE) + // add by maoqiwen 080714   BUG:0000297
                " left join (select FRepFormatName,FRepFormatCode from " + pub.yssGetTableName("tb_Rep_Format") + " where FCheckState = 1) b on a.FRepFormatCode = b.FRepFormatCode " +
                // " left join (select FCtlGrpName,FCtlGrpCode from " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") + " where FCheckState = 1) c on a.FCtlGrpCode = c.FCtlGrpCode" +
                " left join (select FCtlGrpName,FCtlGrpCode from Tb_PFSys_FaceCfgInfo where FCheckState = 1) c on a.FCtlGrpCode = c.FCtlGrpCode" + //改为 Tb_PF_FaceCfgInfo by ly 080304
                " left join (select FRepDsCode,FRepDsName from " +
                pub.yssGetTableName("Tb_Rep_DataSource") +
                " where FCheckState = 1) d on a.FParamSource = d.FRepDsCode" +
                " where FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FCusRepCode")).append("\t");
                bufShow.append(rs.getString("FCusRepName")).append("\t");
                bufShow.append(rs.getString("FRepFormatCode")).append("\t");
                bufShow.append(rs.getString("FRepFormatName")).append("\t");
                //bufShow.append(rs.getString("FRepTypeName").equalsIgnoreCase("0")?"明細報表":"匯總報表").append("\t");
                bufShow.append(rs.getString("FRepTypeNAME")).append("\t"); //字段名改为“FRepTypeName”by maoqiwen 20080714    BUG:0000297
                bufShow.append(rs.getString("FCtlGrpCode")).append("\t");
                bufShow.append(rs.getString("FCtlGrpName")).append("\t");
                bufShow.append(rs.getString("FDesc")).append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取报表自定义设置数据出错", e);
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
        String sVocStr = ""; //词汇类型对照字符串
        String strId = ""; //作为标实，source为数据源，subrep为明细报表
        ResultSet rs = null;
        ResultSet rs2 = null; //保存获得的品种代码
        String fdatasources[] = null;
        String fsubreps[] = null;
        String ftmptables[] = null;
        String fdatasource = "'all',";
        String fsubrep = "'all',";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            strId = this.CusRepCode.substring(0, 6);
            this.CusRepCode = this.CusRepCode.substring(6, this.CusRepCode.length());

            if (strId.equalsIgnoreCase("source")) {
            	//--- MS01178 QDV4赢时胜_报表管理2010年5月13日01_A  add by jiangshichao 2010.06.22 ---
                sHeader = "数据源代码\t数据源名称\t预处理代码组";//添加预处理代码组表头
                //----------------- MS01178 QDV4赢时胜_报表管理2010年5月13日01_A  end ----------------
                String str =
                    "select FSubDsCodes from " +
                    pub.yssGetTableName("Tb_Rep_Custom") + " where FCusRepCode=" +
                    dbl.sqlString(this.CusRepCode);
                rs2 = dbl.openResultSet(str);
                if (rs2.next()) {
                    if (rs2.getString("FSubDsCodes") != null) {
                        fdatasources = rs2.getString("FSubDsCodes").split(",");
                        /*    for (int i = 0; i < fdatasources.length; i++) {
                         fdatasource = fdatasource + dbl.sqlString(fdatasources[i]) +
                                     ",";
                            }*/
                        for (int i = 0; i < fdatasources.length; i++) {
                            strSql = "select y.* from " +
                                "(select FRepDsCode,FCheckState from " +
                                pub.yssGetTableName("Tb_Rep_DataSource") +
                                " where FCheckState <> 2 and FRepDsCode="
                                + dbl.sqlString(fdatasources[i]) +
                                " group by FRepDsCode,FCheckState " +
                                ") x join" +
                                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FVocName as FDsTypeValue,e.FVocName as FTempTabValue from " +
                                pub.yssGetTableName("Tb_Rep_DataSource") +
                                " a " +
                                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                                " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FDsType") + " = d.FVocCode and d.FVocTypeCode = " +
                                dbl.sqlString(YssCons.YSS_RDS_DSTYPE) +
                                " left join Tb_Fun_Vocabulary e on a.FTempTab = e.FVocCode and e.FVocTypeCode = " +
                                dbl.sqlString(YssCons.YSS_RDS_TEMPTAB) +

                                " ) y on x.FRepDsCode = y.FRepDsCode" +
                                " order by  y.FCheckState, y.FCreateTime desc";
                            rs = dbl.openResultSet(strSql);
                            while (rs.next()) {
                                bufShow.append( (rs.getString("FRepDsCode") + "").trim()).
                                    append("\t");

                                bufShow.append( (rs.getString("FRepDsName") + "").trim()).
                                    append("\t");
                              //--- MS01178 QDV4赢时胜_报表管理2010年5月13日01_A  add by jiangshichao 2010.06.22 ---
                                bufShow.append( (rs.getString("FDpCodes") + "").trim()).
                                append(YssCons.YSS_LINESPLITMARK);
                              //----------------- MS01178 QDV4赢时胜_报表管理2010年5月13日01_A  end ----------------  
                                RepDataSourceBean datasource = new RepDataSourceBean();
                                datasource.setYssPub(pub);
                                datasource.setResultSetAttr(rs);

                                bufAll.append(datasource.buildRowStr()).append(YssCons.
                                    YSS_LINESPLITMARK);
                            }
                            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B

                        }

                    }
                }
                /*if (fdatasource.length() > 0) {
                 fdatasource = YssFun.left(fdatasource, fdatasource.length() - 1);
                             }*/

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
                                            YssCons.YSS_RDS_TEMPTAB);

                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + "voc" + sVocStr;
            }

            else if (strId.equalsIgnoreCase("subrep")) {
                sHeader = "明细报表代码\t明细报表名称";
                String str =
                    "select FSubRepCodes from " +
                    pub.yssGetTableName("Tb_Rep_Custom") + " where FCusRepCode=" +
                    dbl.sqlString(this.CusRepCode);
                rs2 = dbl.openResultSet(str);
                if (rs2.next()) {
                    if (rs2.getString("FSubRepCodes") != null) {
                        fsubreps = rs2.getString("FSubRepCodes").split(",");
                        /*  for (int i = 0; i < fsubreps.length; i++) {
                             fsubrep = fsubrep + dbl.sqlString(fsubreps[i]) +
                                   ",";
                          }*/
                        for (int i = 0; i < fsubreps.length; i++) {
                            strSql = "select y.* from " +
                                "(select FCusRepCode,FCheckState from " +
                                pub.yssGetTableName("Tb_Rep_Custom") +
                                " where FCheckState <> 2 and FCusRepCode="
                                + dbl.sqlString(fsubreps[i]) +
                                " group by FCusRepCode,FCheckState " +
                                ") x join" +
                                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FRepFormatName," +
                                "e.FVocName as FRepTypeValue,f.FCtlGrpName,g.FRepDsName from " +
                                pub.yssGetTableName("Tb_Rep_Custom") +
                                " a " +
                                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                                " left join (select FRepFormatCode,FRepFormatName from " +
                                pub.yssGetTableName("Tb_Rep_Format") +
                                " where FCheckState = 1) d on a.FRepFormatCode = d.FRepFormatCode" +
                                " left join Tb_Fun_Vocabulary e on a.FRepType = e.FVocCode and e.FVocTypeCode = " +
                                dbl.sqlString(YssCons.YSS_RCT_REPTYPE) +
                                " left join (select FCtlGrpCode,FCtlGrpName from " +
                                // pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                                " Tb_PFSys_FaceCfgInfo " + //改为这个表 by ly 080304
                                " where FCheckState = 1) f on a.FCtlGrpCode = f.FCtlGrpCode" +
                                //--------------------------------------------------------------
                                " left join (select FRepDsCode,FRepDsName from " +
                                pub.yssGetTableName("Tb_Rep_DataSource") +
                                " where FCheckState =1) g on a.FParamSource=g.FRepDsCode" +
                                //--------------------------------------------------------------


                                " ) y on x.FCusRepCode = y.FCusRepCode" +
                                " order by  y.FCheckState, y.FCreateTime desc";

                            rs = dbl.openResultSet(strSql);

                            while (rs.next()) {
                                bufShow.append( (rs.getString("FCusRepCode") + "").trim()).
                                    append("\t");
                                bufShow.append( (rs.getString("FCusRepName") + "").trim()).
                                    append(YssCons.YSS_LINESPLITMARK);

                                setResultSetAttr(rs);

                                bufAll.append(this.buildRowStr()).append(YssCons.
                                    YSS_LINESPLITMARK);
                            }
                            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
                        }
                    }
                }
                /*  if (fsubrep.length() > 0) {
                     fsubrep = YssFun.left(fsubrep, fsubrep.length() - 1);
                  }*/

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

            else if (strId.equalsIgnoreCase("retemp")) {
                sHeader = "临时表代码\t临时表名称";
                String str =
                    "select FTmpTables from " +
                    pub.yssGetTableName("Tb_Rep_Custom") + " where FCusRepCode=" +
                    dbl.sqlString(this.CusRepCode);
                rs2 = dbl.openResultSet(str);
                if (rs2.next()) {
                    if (rs2.getString("FTmpTables") != null) {
                        ftmptables = rs2.getString("FTmpTables").split(",");
                        for (int i = 0; i < ftmptables.length; i++) {
                            strSql =
                                "select FVocCode,FVocName from Tb_Fun_Vocabulary where FVocTypeCode='rep_customtemp' and FVocCode=" +
                                dbl.sqlString(ftmptables[i]);

                            rs = dbl.openResultSet(strSql);

                            while (rs.next()) {
                                bufShow.append( (rs.getString("FVocCode") + "").trim()).
                                    append("\t");
                                bufShow.append( (rs.getString("FVocName") + "").trim()).
                                    append(YssCons.YSS_LINESPLITMARK);

//                       bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                             append(YssCons.YSS_LINESPLITMARK);
                                this.CusRepCode = rs.getString("FVocCode") + "";
                                this.CusRepName = rs.getString("FVocName") + "";

                                bufAll.append(this.buildRowStr()).append(YssCons.
                                    YSS_LINESPLITMARK);
                            }
                            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
                        }

                    }
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

            return "";
        }

        catch (Exception e) {
            throw new YssException("获取数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs, rs2);

        }
    }

    //根据CusRepCode获取自定义报表中的数据
    public String getListViewData5(String repCodes) throws YssException {
        VocabularyBean vocabulary = new VocabularyBean();
        vocabulary.setYssPub(pub);
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sVocStr = ""; //词汇类型对照字符串
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String[] repCode = repCodes.split("\t");
        try {
            sVocStr = vocabulary.getVoc(YssCons.Yss_REPORT_TYPE);
            sHeader = "报表代码\t报表名称\t报表格式代码\t报表格式名称\t报表类型\t控件组代码\t参数控件组名称\t描述";
            for (int i = 0; i < repCode.length; i++) {
                strSql = "select a.*,b.FRepFormatName as FRepFormatName,c.FCtlGrpName as FCtlGrpName,d.FRepDsName as FRepDsName,e.FUserName as FCreatorName,f.FUserName as FCheckUserName from " +
                    pub.yssGetTableName("tb_Rep_Custom") + " a " +
                    "left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCreator= e.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) f on a.FCheckUser = f.FUserCode" +

                    " left join (select FRepFormatName,FRepFormatCode from " +
                    pub.yssGetTableName("tb_Rep_Format") +
                    " where FCheckState = 1) b on a.FRepFormatCode = b.FRepFormatCode " +
                    " left join (select FCtlGrpName,FCtlGrpCode from " +
                    // pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                    " Tb_PFSys_FaceCfgInfo " + //改为这个表 by ly 080304
                    " where FCheckState = 1) c on a.FCtlGrpCode = c.FCtlGrpCode" +
                    " left join (select FRepDsCode,FRepDsName from " +
                    pub.yssGetTableName("Tb_Rep_DataSource") +
                    " where FCheckState = 1) d on a.FParamSource = d.FRepDsCode" +
                    " where FCUSREPCODE=" + dbl.sqlString(repCode[i]) +
                    " order by a.FCheckState, a.FCreateTime desc";

                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    bufShow.append(rs.getString("FCusRepCode")).append("\t");
                    bufShow.append(rs.getString("FCusRepName")).append("\t");
                    bufShow.append(rs.getString("FRepFormatCode")).append("\t");
                    bufShow.append(rs.getString("FRepFormatName")).append("\t");
                    bufShow.append(rs.getString("FRepType").equalsIgnoreCase("0") ?
                                   "明細報表" : "匯總報表").append("\t");
                    bufShow.append(rs.getString("FCtlGrpCode")).append("\t");
                    bufShow.append(rs.getString("FCtlGrpName")).append("\t");
                    bufShow.append(rs.getString("FDesc")).append(YssCons.
                        YSS_LINESPLITMARK);
                    setResultSetAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
                dbl.closeResultSetFinal(rs);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取报表自定义设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Rep_Custom") +
                "(FCusRepCode,FCusRepName,FRepFormatCode,FRepType,FMaxCols,FCtlGrpCode,FParamSource,FTmpTables,FSubDsCodes," +
                "FSubRepCodes,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FIsStartBuild,FEXPORTPATH " +//modify by huangqirong 2011-07-13 story #1101 添加是否启用生成 //modify by huangqirong 2011-12-29 story #1284               
                ",FTEMPLATEPATH )" +//add by guolongchao 20120130 STORY 1284 添加模板路径               
                " values(" + dbl.sqlString(this.CusRepCode) + "," +
                dbl.sqlString(this.CusRepName) + "," +
                dbl.sqlString(this.RepFormatCode) + "," +
                dbl.sqlString(this.repType) + "," + //彭鹏 2008.2.3 修改
                this.maxCols + "," +
                dbl.sqlString(this.CtlGrpCode) + "," +
                dbl.sqlString(this.ParamSourceCode) + "," +
                dbl.sqlString(this.TmpTables) + "," +
                dbl.sqlString(this.dataSourceCodes) + "," +
                dbl.sqlString(this.SubRepCodes) + "," +
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ","+this.isShowBuild+//add by huangqirong 2011-07-13 story #1101 添加是否启用生成
                ","+dbl.sqlString(this.exportPath)+//add by huangqirong 2011-12-29 story #1284
                ","+dbl.sqlString(this.templatePath) +//add by guolongchao 20120130 STORY 1284 模板路径                              
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            if(this.allSetDatas!=null&&this.allSetDatas.length()>0)//add by guolongchao 20120112 STORY 1284 添加文件名集合
            {
            	String[] str = allSetDatas.split("\r\f");        
            	RepFileNameBean filename=new RepFileNameBean();
            	filename.setYssPub(pub);        
            	if (str[0].length() > 0) {           		
            		filename.setOldRepCode(this.oldCusRepCode);
                    filename.saveMutliSetting(str[0]);
                } 
                else 
                {
                	filename.setCusRepCode(this.CusRepCode);   
                    filename.setOldRepCode(this.oldCusRepCode);
                    filename.addSetting();
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增报表自定义数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Rep_Custom"),
                               "FCusRepCode",
                               this.CusRepCode, this.oldCusRepCode);
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        //------MS00204 add by 宋洁 2009-01-19 原来选中多项还原的时候。只能还原一项，
        //因为后台处理数据时，方法不当丢失了数据，现在可以把前台传过来的数据都读取到。
        String[] recycledItems; //声明变量用于得到前台传送过来的数据。
        String recycledItem; //声明变量用于得到前台传过来的每一条数据。
        String[] requestArray; //声明变量用于得到每条记录的详细信息。
        try {
            recycledItems = recycledInfo.split("\r\n"); //得到每一条记录存到string数组里
            for (int i = 0; i < recycledItems.length; i++) { //用for循环得到每条记录的详细信息进行筛选，得到唯一性标示的有效信息
                recycledItem = recycledItems[i]; //得到每条要清除记录
                requestArray = recycledItem.split("\t"); //以\t为准，分割记录，得到具体记录
                this.CusRepCode = requestArray[0]; //得到唯一性标示的有效信息
                //------MS00204 add by 宋洁 2009-01-19 原来选中多项还原的时候。只能还原一项，
                //因为后台处理数据时，方法不当丢失了数据，现在可以把前台传过来的数据都读取到。
                strSql = "update " + pub.yssGetTableName("Tb_Rep_Custom") + " set FCheckState = " + this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FCusRepCode = " + dbl.sqlString(this.CusRepCode);

                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                //add by guolongchao 20120112 STORY 1284 更新文件名集合对应的值---------start
                strSql = "update " + pub.yssGetTableName("TB_Rep_FileName") +
                         " set FCheckState = " + this.checkStateId +
                         ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                         ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                         "' where FRepCode = " + dbl.sqlString(this.CusRepCode); //更新文件名设置表中的内容
                dbl.executeSql(strSql);
                //add by guolongchao 20120112 STORY 1284 更新文件名集合对应的值---------end
            }          
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核报表自定义数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_Custom") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCusRepCode = " + dbl.sqlString(this.CusRepCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //add by guolongchao 20120112 STORY 1284 更新文件名集合对应的值---------start
            strSql = "update " + pub.yssGetTableName("TB_Rep_FileName") +
                     " set FCheckState = " + this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FRepCode = " + dbl.sqlString(this.CusRepCode); //更新文件名设置表中的内容
            dbl.executeSql(strSql);
            //add by guolongchao 20120112 STORY 1284 更新文件名集合对应的值---------end
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除报表自定义数据出错", e);
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
        try {
            strSql =
                "update " + pub.yssGetTableName("Tb_Rep_Custom") +
                " set FCusRepCode = " + dbl.sqlString(this.CusRepCode) +
                ",FCusRepName = " + dbl.sqlString(this.CusRepName) +
                ",FRepFormatCode = " + dbl.sqlString(this.RepFormatCode) +
                ",FRepType = " + dbl.sqlString(this.repType) +
                ",FMaxCols = " + this.maxCols +
                ",FCtlGrpCode = " + dbl.sqlString(this.CtlGrpCode) +
                ",FParamSource = " + dbl.sqlString(this.ParamSourceCode) +
                ",FTmpTables = " + dbl.sqlString(this.TmpTables) +
                ",FSubDsCodes = " + dbl.sqlString(this.dataSourceCodes) +
                ",FSubRepCodes = " + dbl.sqlString(this.SubRepCodes) +
                ",FDesc = " + dbl.sqlString(this.Desc) +
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ",FIsStartBuild="+this.isShowBuild+""+//add by huangqirong 2011-07-13 story #1101 添加是否启用生成         
                ",FTEMPLATEPATH="+dbl.sqlString(this.templatePath) +//add by guolongchao 201120130 STORY 1284 模板路径                              
                ",FEXPORTPATH="+dbl.sqlString(this.exportPath)+//add by huangqirong 2011-12-29 story #1284
                " where FCusRepCode = " + dbl.sqlString(this.oldCusRepCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            if(this.allSetDatas!=null&&this.allSetDatas.length()>0)//add by guolongchao 20120112 STORY 1284 添加文件名集合
            {
            	String[] str = allSetDatas.split("\r\f");        
            	RepFileNameBean filename=new RepFileNameBean();
            	filename.setYssPub(pub);        
            	if (str[0].length() > 0) {            		
            		filename.setOldRepCode(this.oldCusRepCode);
                    filename.saveMutliSetting(str[0]);
                } 
                else 
                {
                    strSql = " update " + pub.yssGetTableName("TB_Rep_FileName") +
                             " set FRepCode = " + dbl.sqlString(this.CusRepCode) +                            
                             " where FRepCode = " + dbl.sqlString(this.oldCusRepCode); 
                    dbl.executeSql(strSql);                    
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改报表自定义数据出错", e);
        } finally {
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
                pub.yssGetTableName("Tb_Rep_Custom") + " a " +
                " where FCheckState = 1 and FCusRepCode = " + dbl.sqlString(CusRepCode) +
                " order by FCheckState, FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.CusRepCode = rs.getString("FCusRepCode") + "";
                this.CusRepName = rs.getString("FCusRepName") + "";
                this.RepFormatCode = rs.getString("FRepFormatCode") + "";
                this.repType = rs.getString("FRepType") + "";
                this.maxCols = rs.getInt("FMaxCols");
                this.CtlGrpCode = rs.getString("FCtlGrpCode") + "";
                this.ParamSourceCode = rs.getString("FParamSource") + "";
                this.TmpTables = rs.getString("FTmpTables") + "";
                this.dataSourceCodes = rs.getString("FSubDsCodes") + "";
                this.SubRepCodes = rs.getString("FSubRepCodes") + "";
                this.Desc = rs.getString("FDesc") + "";
                this.isShowBuild=rs.getInt("FIsStartBuild");  //add by huangqirong 2011-07-13 story #1101 添加是否启用生成

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
        buf.append(this.CusRepCode).append("\t");
        buf.append(this.CusRepName).append("\t");
        buf.append(this.RepFormatCode).append("\t");
        buf.append(this.RepFormatName).append("\t");
        buf.append(this.repType).append("\t");
        buf.append(this.maxCols).append("\t");
        buf.append(this.CtlGrpCode).append("\t");
        buf.append(this.CtlGrpName).append("\t");
        buf.append(this.dataSourceCodes).append("\t");
        buf.append(this.SubRepCodes).append("\t");
        buf.append(this.Desc).append("\t");
        buf.append(this.ParamSourceCode).append("\t");
        buf.append(this.ParamSourceName).append("\t");
        buf.append(this.TmpTables).append("\t");
        buf.append(this.isShowBuild).append("\t");//add by huangqirong 2011-07-15 story #1101
        buf.append(this.exportPath).append("\t");//add by huangqirong 2011-12-29 story #1284
        buf.append(this.templatePath).append("\t");//模板路径    add by guolongchao 20120130 STORY 1284
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String strSql = "", sReturn = "";
        ResultSet rs = null;
        try {
            if (sType.equalsIgnoreCase("getFormatCode")) {
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Custom") +
                    " where FCheckState=1 and FCusRepCode=" +
                    dbl.sqlString(this.CusRepCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    sReturn = rs.getString("FRepFormatCode");
                }
            }
            if (sType.equalsIgnoreCase("getCtlGrpCode")) {
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Custom") +
                    " where FCheckState=1 and FCusRepCode=" +
                    dbl.sqlString(this.CusRepCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    sReturn = rs.getString("FCtlGrpCode");
                }
            }
            //add by huangqirong 2011-07-19 story #1101
            if(sType.equalsIgnoreCase("getSetting")){
            	strSql = "select FIsStartBuild from " + pub.yssGetTableName("Tb_Rep_Custom") +
                " where FCusRepCode=" +
                dbl.sqlString(this.CusRepCode);
            	rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    sReturn = rs.getString("FIsStartBuild");
                }
            }
            //---end---
            if (sReturn == null) {
                sReturn = "";
            }
            return sReturn;
        } catch (Exception e) {
            throw new YssException("");
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
        this.CusRepCode = rs.getString("FCusRepCode") + "";
        this.CusRepName = rs.getString("FCusRepName") + "";
        this.RepFormatCode = rs.getString("FRepFormatCode") + "";
        this.RepFormatName = rs.getString("FRepFormatName") + "";
        this.repType = rs.getString("FRepType") + "";
        this.maxCols = rs.getInt("FMaxCols");
        this.CtlGrpCode = rs.getString("FCtlGrpCode") + "";
        this.CtlGrpName = rs.getString("FCtlGrpName") + "";
        this.dataSourceCodes = rs.getString("FSubDsCodes") + "";
        this.SubRepCodes = rs.getString("FSubRepCodes") + "";
        this.Desc = rs.getString("FDesc") + "";
        this.ParamSourceCode = rs.getString("FParamSource") + "";
        this.ParamSourceName = rs.getString("FRepDsName") + "";
        this.TmpTables = rs.getString("FTmpTables") + "";
        this.isShowBuild=rs.getInt("FIsStartBuild");  //add by huangqirong 2011-07-13 story #1101 添加是否启用生成
        this.exportPath = rs.getString("FEXPORTPATH") + "";//导出路径    add by guolongchao 20111228 STORY 1284 
        this.templatePath = rs.getString("FTEMPLATEPATH") + "";//模板路径   add by guolongchao 201120130 STORY 1284                     
        super.setRecLog(rs);
    }

    /**
     * deleteRecycleData
     */
    //-------------------MS00170 QDV4赢时胜上海2009年1月7日04_B add by 宋洁 2009-01-16 编写的代码用于实现清除回收站中选中的记录.
    public void deleteRecycleData() throws YssException {
        String deleteInfo = ""; //声明要被回收站清除的有效信息
        String[] recycledItems; //声明要被回收站清除的每一条有效信息
        Connection conn = dbl.loadConnection(); //用于加载数据库连接
        String strSql = ""; //声明sql变量
        boolean bTrans = false; //代表是否开始了事务;
        try {
            recycledItems = recycledInfo.split("\r\n"); //得到每一条记录存到string数组里
            for (int i = 0; i < recycledItems.length; i++) { //用for循环得到每条记录的详细信息进行筛选，得到唯一性标示的有效信息
                String recycledItem = recycledItems[i]; //得到每条要清除记录
                String[] requestArray = recycledItem.split("\t"); //以\t为准，分割记录，得到具体记录
                this.CusRepCode = requestArray[0]; //得到唯一性标示的有效信息
                if (i != recycledItems.length - 1) { //对有效信息进行处理，方便delete语句中where in(x,y,z)格式调用
                    deleteInfo = deleteInfo + dbl.sqlString(this.CusRepCode) + ",";
                } else {
                    deleteInfo = deleteInfo + dbl.sqlString(this.CusRepCode);
                }
            }
            conn.setAutoCommit(false); //设置不自动提交事务
            bTrans = true; //开始事务
            //System.out.println(dbl.sqlString(this.CusRepCode));
            strSql = "delete from " + pub.yssGetTableName("Tb_Rep_Custom") + " where FCUSREPCODE in(" + deleteInfo + ")"; //编写删除已经选中的记录的sql语句
            dbl.executeSql(strSql); //执行sql语句
            strSql = "delete from " + pub.yssGetTableName("TB_Rep_FileName") + " where FRepCode in(" + deleteInfo + ")"; //add by guolongchao 20120113 STORY 1284 删除文件名集合
            dbl.executeSql(strSql); //执行sql语句
            conn.commit(); //提交事务
            bTrans = false; //关闭事务
            conn.setAutoCommit(true); //设置自动提交事务
        } catch (Exception e) {
            throw new YssException("清除回收站信息出错", e); //发生异常情况时抛出异常信息
        } finally {
            dbl.endTransFinal(conn, bTrans); //事务结束后的处理
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
    //-------------------MS00170 QDV4赢时胜上海2009年1月7日04_B add by 宋洁 2009-01-16 编写的代码用于实现清除回收站中选中的记录.
    
  //add by huangqirong 2011-09-20 story #1284
    public String getReps(String groupCode,String repCodes) throws YssException{
    	StringBuffer result=new StringBuffer();
    	ResultSet rs = null;
    	String reps="";
    	if(!dbl.yssTableExist("tb_"+groupCode+"_rep_custom")){
			return "null";
		}
    	reps=operSql.sqlCodes(repCodes);
    	try {
    		
    		rs = dbl.openResultSet("select * from tb_"+groupCode+"_rep_custom where FCUSREPCODE in ("+reps+") and FCHECKSTATE = 1");
            while (rs.next()) {            	
            	result.append(rs.getString("FCUSREPCODE")+"\t");
            	result.append(rs.getString("FCUSREPNAME")+"\t");  
            	result.append(rs.getString("FTEMPLATEPATH")+"\t");//add by guolongchao 20120130 STORY 1284 添加模板路径
            	result.append(rs.getString("FEXPORTPATH")+"\t"); 
            	
            	result.append(rs.getString("FCTLGRPCODE")+"\r");
            }
            
            if(result.length() >0 ){
            	result.setLength(result.length()-1);	//去掉分隔符
            }
            
		} catch (Exception e) {
			throw new  YssException("获取报表出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return result.toString();
    }
}
