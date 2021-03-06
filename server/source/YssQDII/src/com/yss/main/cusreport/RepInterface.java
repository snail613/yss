package com.yss.main.cusreport;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.platform.pfsystem.facecfg.*;
import com.yss.util.*;
import oracle.jdbc.*;
import oracle.sql.*;

public class RepInterface
    extends BaseBean implements IDataInterface {
    // private String customStr = ""; //存放Tb_001_Rep_Custom表中的记录
    // private String formatStr = ""; //存放Tb_001_Rep_Format表中的记录
    // private String paramCtlGrpStr = ""; //存放Tb_001_Rep_paramctlgrp表中的记录
    //  private String dataSourceStr = ""; //存放Tb_001_Rep_Datasource表中的记录
//   private String paramCtlStr = ""; //存放Tb_001_Rep_ParamCtl表中的记录
    //  private String dsFieldStr = ""; //存放Tb_001_Rep_DsField表中的记录
    //  private String tabCellStr = "";

    public RepInterface() {
    }

    public void importData(String sRequestStr) throws YssException {
        String[] tmpStr = sRequestStr.split("\r\f");
        String[] customRecs = tmpStr[0].split("\r\t");
        String[] formatRecs = tmpStr[1].split("\r\t");
        //  String[] paramctlgrpRecs = tmpStr[2].split("\r\t");
        //  String[] datasourceRecs = tmpStr[3].split("\r\t");
        //   String[] paramctlRecs = tmpStr[4].split("\r\t");
        //   String[] dsFieldRecs = tmpStr[5].split("\r\t");
        //   String[] tabCellRecs = tmpStr[6].split("\r\t");
        String[] paramctl = tmpStr[2].split("\r\t"); //改为 Tb_PFSys_FaceCfgInfo表 by ly 080304
        String[] datasourceRecs = tmpStr[3].split("\r\t");
        String[] dsFieldRecs = tmpStr[4].split("\r\t");
        String[] tabCellRecs = tmpStr[5].split("\r\t");
        String strSql = "";
        RepCustomBean repCustom = null;
        RepFormatBean repFormat = null;
        //   RepParamCtlGrpBean repParamCtlGrp = null;
        RepDataSourceBean repDataSource = null;
        //    RepParamCtlBean repParamCtl = null;
        FaceCfgInfoBean cfgInfo = null;
        RepDsFieldBean repDsField = null;
        RepTabCellBean repTabCell = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //----------------------自定义报表------------------------------
            conn.setAutoCommit(false);

            for (int i = 0; i < customRecs.length; i++) {
                if (!customRecs[i].equals("null") && customRecs[i].length() > 0) {
                    repCustom = new RepCustomBean();
                    repCustom.setYssPub(pub);

                    repCustom.parseRowStr(customRecs[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Rep_Custom") +
                        " where FCusRepCode=" +
                        dbl.sqlString(repCustom.getCusRepCode());
                    dbl.executeSql(strSql);
                    strSql =
                        "insert into " + pub.yssGetTableName("Tb_Rep_Custom") +
                        "(FCusRepCode,FCusRepName,FRepFormatCode,FRepType,FMaxCols,FCtlGrpCode,FParamSource,FTmpTables,FSubDsCodes," +
                        "FSubRepCodes,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" + dbl.sqlString(repCustom.getCusRepCode()) +
                        "," +
                        dbl.sqlString(repCustom.getCusRepName()) + "," +
                        dbl.sqlString(repCustom.getRepFormatCode()) + "," +
                        dbl.sqlString(repCustom.getRepType()) + "," +
                        repCustom.getMaxCols() + "," +
                        dbl.sqlString(repCustom.getCtlGrpCode()) + "," +
                        dbl.sqlString(repCustom.getParamSourceCode()) + "," +
                        dbl.sqlString(repCustom.getTmpTables()) + "," +
                        dbl.sqlString(repCustom.getDataSourceCodes()) + "," +
                        dbl.sqlString(repCustom.getSubRepCodes()) + "," +
                        dbl.sqlString(repCustom.getDesc()) + "," +
                        repCustom.checkStateId + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) +
                        ")";
                    dbl.executeSql(strSql);
                }
            }
            //-----------------------------------报表格式-------------------------------------
            for (int i = 0; i < formatRecs.length; i++) {
                if (!formatRecs[i].equals("null") && formatRecs[i].length() > 0) {
                    repFormat = new RepFormatBean();
                    repFormat.setYssPub(pub);
                    repFormat.parseRowStr(formatRecs[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Rep_Format") +
                        " where FRepFormatCode=" +
                        dbl.sqlString(repFormat.getRepCode());
                    dbl.executeSql(strSql);
                    strSql =
                        "insert into " + pub.yssGetTableName("Tb_Rep_Format") +
                        "(FRepFormatCode, FRepFormatName, FRepType, FRows, FCols, FFixRows, FFixCols, FRCSize, " +
                        " FMerge, FBalFmt, FPrint, FCtlGrpCode, FBeanId, FDesc," +
                        " FCheckState, FCreator, FCreateTime, FCheckUser)" +
                        " values(" + dbl.sqlString(repFormat.getRepCode()) + "," +
                        dbl.sqlString(repFormat.getRepName()) + "," +
                        dbl.sqlString(repFormat.getRepType()) + "," +
                        repFormat.getRepRows() + "," + repFormat.getRepCols() +
                        "," +
                        repFormat.getFixRows() + "," + repFormat.getFixCols() +
                        ",'" +
                        repFormat.getRCSize() + "','" +
                        repFormat.getMerge() + "','" +
                        repFormat.getBalFmt() + "','" +
                        repFormat.getPrint() + "'," +
                        dbl.sqlString(repFormat.getCtlGrpCode()) + "," +
                        dbl.sqlString(repFormat.getBeanId()) + "," +
                        dbl.sqlString(repFormat.getDesc()) + "," +
                        repFormat.checkStateId + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) + ")";
                    dbl.executeSql(strSql);
                }
            }
            //----------------------------------控件组参数--------------------------------------
            /* for (int i = 0; i < paramctlgrpRecs.length; i++) {
                if(!paramctlgrpRecs[i].equals("null")&&paramctlgrpRecs[i].length()>0)
                {
                   /*repParamCtlGrp = new RepParamCtlGrpBean();
                    repParamCtlGrp.setYssPub(pub);
                    repParamCtlGrp.parseRowStr(paramctlgrpRecs[i]);
                    strSql =
                         // "delete from " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                          " where FCtlGrpCode=" +
                          dbl.sqlString(repParamCtlGrp.getCtlGrpCode());
                    dbl.executeSql(strSql);
                    strSql =
                          "insert into " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                          "(FCtlGrpCode, FCtlGrpName, " +
                          " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                          " values(" + dbl.sqlString(repParamCtlGrp.getCtlGrpCode()) +
                          "," +
                          dbl.sqlString(repParamCtlGrp.getCtlGrpName()) + "," +
                          repParamCtlGrp.checkStateId + "," +
                          dbl.sqlString(pub.getUserCode()) + "," +
                          dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                          "," +
                          dbl.sqlString(pub.getUserCode()) + ")";

                    dbl.executeSql(strSql);
                    //  dbl.sqlString(strSql);
                 }
              }*/
             //改为下面的表 by ly 080304
             for (int i = 0; i < paramctl.length; i++) {
                 if (!paramctl[i].equals("null") && paramctl[i].length() > 0) {
                     cfgInfo = new FaceCfgInfoBean();
                     cfgInfo.setYssPub(pub);
                     cfgInfo.parseRowStr(paramctl[i]);
                     strSql = "delete from Tb_PFSys_FaceCfgInfo where FCtlGrpCode =" +
                         dbl.sqlString(cfgInfo.getSCtlGrpCode());
                     dbl.executeSql(strSql);
                     strSql = " insert into Tb_PFSys_FaceCfgInfo (FCtlGrpCode,FCtlGrpName,FCtlCode,FParamIndex,FCtlType,FParam," +
                         "FCtlInd,FFunModules,FCheckState,FCreator,FCreateTime) values(" +
                         dbl.sqlString(cfgInfo.getSCtlGrpCode()) + "," +
                         dbl.sqlString(cfgInfo.getSCtlGrpName()) + "," +
                         dbl.sqlString(cfgInfo.getSCtlCode()) + "," +
                         cfgInfo.getIParamIndex() + "," +
                         cfgInfo.getICtlType() + "," +
                         dbl.sqlString(cfgInfo.getSParams()) + "," +
                         dbl.sqlString(cfgInfo.getSCtlInd()) + "," +
                         dbl.sqlString(cfgInfo.getSFunModules()) + "," +
                         cfgInfo.checkStateId + "," +
                         dbl.sqlString(pub.getUserCode()) + "," +
                         dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                         ")";
                     dbl.executeSql(strSql);
                 }
             }
            //------------------------------------数据源---------------------------------
            for (int i = 0; i < datasourceRecs.length; i++) {
                if (!datasourceRecs[i].equals("null") && datasourceRecs[i].length() > 0) {
                    repDataSource = new RepDataSourceBean();
                    repDataSource.setYssPub(pub);
                    repDataSource.parseRowStr(datasourceRecs[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Rep_DataSource") +
                        " where FRepDsCode=" +
                        dbl.sqlString(repDataSource.getRepDsCode());
                    dbl.executeSql(strSql);
                    strSql = "insert into " +
                        pub.yssGetTableName("Tb_Rep_DataSource") +
                        "(FRepDsCode,FRepDsName,FDsType,FTempTab,FDataSource,FFillRange," +
                        "FBeanId, FDesc,FTRowColor,FBRowColor,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" + dbl.sqlString(repDataSource.getRepDsCode()) +
                        "," +
                        dbl.sqlString(repDataSource.getRepDsName()) + "," +
                        repDataSource.getDsType() + "," +
                        dbl.sqlString(repDataSource.getTempTab()) + ",";
                    if (dbl.getDBType() == YssCons.DB_DB2) {
                        strSql = strSql + dbl.sqlString(repDataSource.getDataSource()) + ",";
                    } else if (dbl.getDBType() == YssCons.DB_ORA) {
                        strSql = strSql + "EMPTY_CLOB()" + ",";
                    } else {
                        throw new YssException("数据库访问错误。数据库类型不明或选择了非系统兼容的数据库！");
                    }
                    strSql = strSql +
                        dbl.sqlString(repDataSource.getFillRange()) + "," +
                        dbl.sqlString(repDataSource.getBeanID()) + "," +
                        dbl.sqlString(repDataSource.getDesc()) + "," +
                        YssFun.toInt(repDataSource.getTRowColor()) + "," +
                        YssFun.toInt(repDataSource.getBRowColor()) + "," +
                        repDataSource.checkStateId + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) +
                        ")";
                    dbl.executeSql(strSql);

                    //-----------------------------------------------------------------------
                    if (dbl.getDBType() == YssCons.DB_ORA) {
                        String str = "select FDataSource from " +
                            pub.yssGetTableName("Tb_Rep_DataSource") +
                            " where FRepDsCode= " +
                            dbl.sqlString(repDataSource.getRepDsCode());

                        ResultSet rs = dbl.openResultSet(str);
                        if (rs.next()) {
                        	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                        	  // modify by jsc 20120809 连接池对大对象的特殊处理
                            //CLOB clob = ( (OracleResultSet) rs).getCLOB("FDataSource");
                            CLOB clob =  dbl.CastToCLOB(rs.getClob("FDataSource"));
                            clob.putString(1, repDataSource.getDataSource());
                            String sql =
                                "update " + pub.yssGetTableName("Tb_Rep_DataSource") +
                                " set FDataSource=? where FRepDsCode=" +
                                dbl.sqlString(repDataSource.getRepDsCode());
                            PreparedStatement pstmt = conn.prepareStatement(sql);
                            pstmt.setClob(1, clob);
                            pstmt.executeUpdate();
                            pstmt.close();
                        }
                        dbl.closeResultSetFinal(rs);
                    }
                }
            }
            //-------------------------------控件参数----------------------------
            /* for (int i = 0; i < paramctlRecs.length; i++) {
                if(!paramctlRecs[i].equals("null")&&paramctlRecs[i].length()>0)
                {
                   repParamCtl = new RepParamCtlBean();
                   repParamCtl.setYssPub(pub);
                   repParamCtl.parseRowStr(paramctlRecs[i]);
                   strSql = "delete from " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                         " where FCtlGrpCode=" +
                         dbl.sqlString(repParamCtl.getCtlGrpCode()) +
                         " and FCtlCode=" + dbl.sqlString(repParamCtl.getCtlCode());
                   dbl.executeSql(strSql);
                   strSql = "insert into " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                         "(FCtlGrpCode,FCtlCode,FParamIndex,FCtlType,FParam," +
                         " FCheckState, FCreator, FCreateTime,FCheckUser)" +
                         " Values (" + dbl.sqlString(repParamCtl.getCtlGrpCode()) +
                         "," +
                         dbl.sqlString(repParamCtl.getCtlCode()) + "," +
                         repParamCtl.getParamIndex() + "," +
                         repParamCtl.getCtlType() + "," +
                         dbl.sqlString(repParamCtl.getParam()) + "," +
                         repParamCtl.checkStateId + "," +
                         dbl.sqlString(pub.getUserCode()) + "," +
                         dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                         "," +
                         dbl.sqlString(pub.getUserCode()) +
                         ")";
                   dbl.executeSql(strSql);
                }
             } */
            //------------------------------------数据源格式------------------------------------
            for (int i = 0; i < dsFieldRecs.length; i++) {
                if (!dsFieldRecs[i].equals("null") && dsFieldRecs[i].length() > 0) {
                    repDsField = new RepDsFieldBean();
                    repDsField.setYssPub(pub);
                    repDsField.parseRowStr(dsFieldRecs[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Rep_DsField") +
                        " where FRepDsCode=" + dbl.sqlString(repDsField.getDsCode()) +
                        " and  FOrderIndex=" + repDsField.getOrderIndex();
                    dbl.executeSql(strSql);
                    strSql = "insert into " + pub.yssGetTableName("Tb_Rep_DsField") +
                        "(FRepDsCode, FOrderIndex, FDsField, FColkey,FTotalInd, FIsTotal, " + //加上FColKey的导入fazmm20071023
                        " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" + dbl.sqlString(repDsField.getDsCode()) + "," +
                        repDsField.getOrderIndex() + "," +
                        dbl.sqlString(repDsField.getDsField()) + "," +
                        dbl.sqlString(repDsField.getStrColKey()) + "," +
                        dbl.sqlString(repDsField.getIsTotalInd()) + "," +
                        repDsField.getIsTotal() + "," +
                        repDsField.checkStateId + "," +
                        dbl.sqlString(pub.getUserCode()) + "," +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        "," +
                        dbl.sqlString(pub.getUserCode()) +
                        ")";
                    dbl.executeSql(strSql);
                }

            }
            //------------------------------------报表单元格------------------------------
            for (int i = 0; i < tabCellRecs.length; i++) {
                if (!tabCellRecs[i].equals("null") && tabCellRecs[i].length() > 0) {
                    repTabCell = new RepTabCellBean();
                    repTabCell.setYssPub(pub);
                    repTabCell.parseRowStr(tabCellRecs[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Rep_Cell") +
                        " where FRelaCode=" + dbl.sqlString(repTabCell.getRepCode()) +
                        " and FRelaType=" + dbl.sqlString(repTabCell.getRelaType()) +
                        " and FRow=" + repTabCell.getRow() +
                        " and FCol=" + repTabCell.getCol();
                    dbl.executeSql(strSql);
                    strSql =
                        "insert into " + pub.yssGetTableName("Tb_Rep_Cell") +
                        "(FRelaCode,FRelaType, FRow, FCol, FContent, " +
                        " FLLine, FTLine, FRLine, FBLine, FLColor, FTColor, FRColor, FBColor, FBackColor, FForeColor, " +
                        " FFontName, FFontSize, FFontStyle, FDataType, FFormat,FIsMergeCol)" +
                        "values (" + dbl.sqlString(repTabCell.getRepCode()) + "," +
                        dbl.sqlString(repTabCell.getRelaType()) + "," +
                        YssFun.toInt(repTabCell.getRow()) + "," +
                        YssFun.toInt(repTabCell.getCol()) + "," +
                        dbl.sqlString(repTabCell.getContent()) + "," +
                        YssFun.toInt(repTabCell.getLLine()) + "," +
                        YssFun.toInt(repTabCell.getTLine()) + "," +
                        YssFun.toInt(repTabCell.getRLine()) + "," +
                        YssFun.toInt(repTabCell.getBLine()) + "," +
                        YssFun.toInt(repTabCell.getLColor()) + "," +
                        YssFun.toInt(repTabCell.getTColor()) + "," +
                        YssFun.toInt(repTabCell.getRColor()) + "," +
                        YssFun.toInt(repTabCell.getBColor()) + "," +
                        YssFun.toInt(repTabCell.getBackColor()) + "," +
                        YssFun.toInt(repTabCell.getForeColor()) + "," +
                        dbl.sqlString(repTabCell.getFontName()) + "," +
                        YssFun.toInt(repTabCell.getFontSize()) + "," +
                        YssFun.toInt(repTabCell.getFontStyle()) + "," +
                        YssFun.toInt(repTabCell.getDataType()) + "," +
                        (repTabCell.getFormat().length() > 0 ?
                         (repTabCell.getFormat().substring(0, 1).equals("%") ?
                          dbl.sqlString("\t" + repTabCell.getFormat()) :
                          dbl.sqlString("\t\t" + repTabCell.getFormat())) :
                         dbl.sqlString(repTabCell.getFormat())) + "," +
                        repTabCell.getIMerge() + ")"; //添加新字段 by ly 080321

                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
//            try {
//                conn.rollback();
//            } catch (Exception re) {
//            	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A 
//            	throw new YssException(re.getMessage());
//            }
            throw new YssException("导入数据出错", e);
        }finally
        {
        	dbl.endTransFinal(conn, bTrans);
        }
    }

    /*
     * exportData
     *
     * @param sRequestStr String
     * @return String
     */
    public String exportData(String sRequestStr) throws YssException {
        String[] cusRepCodes = sRequestStr.split("\t");

        Set dataSourceCodes = new HashSet();
        Set dataSourceFieldCodes = new HashSet();
        Set formatCodes = new HashSet();
        // Set ctlGrpCodes = new HashSet();
        // Set ctlCodes = new HashSet();
        Set ctlGrps = new HashSet(); //改为从这张表 Tb_PFSys_FaceCfgInfo 取控件
        Set subRepCodes = new HashSet();
        Set boolCustomCodes = new HashSet();

        String[] dataSourceCode = null;
        String[] dataDsf = null;
        String[] subRepCode = null;
        String formatCode = "";
        String dataSourceFieldCode = "";
        String ctlGrpCode = "";
        // String ctlCode = "";

        String[] subRep = null;

        //初始化每个类
        RepCustomBean repCustom = null; //
        RepFormatBean repFormat = null;
        // RepParamCtlGrpBean repParamCtlGrp = null;
        RepDataSourceBean repDataSource = null;
        // RepParamCtlBean repParamCtl = null;
        FaceCfgInfoBean cfgInfo = null;
        RepDsFieldBean repDsField = null;
        RepTabCellBean repTabCell = null;

        //------------------------------------------------------------------

        String strResult = "";
        String customStr = "";
        String formatStr = "";
        // String paramCtlGrpStr = "";
        String dataSourceStr = "";
        String paramCtlStr = "";
        String dsFieldStr = "";
        String tabCellStr = "";

        //------------------------------------------------------------------
        ResultSet rsCus = null;
        ResultSet rsDs = null;
        ResultSet rsFormat = null;
        ResultSet rsDsField = null;
        //  ResultSet rsCtlGrp = null;
        ResultSet rsCtl = null;
        ResultSet rsCell = null;
        ResultSet rsSubRep = null;

        String strSql = "";
        try {
            //--------------------汇总报表------------------------------------------
            for (int i = 0; i < cusRepCodes.length; i++) {
                strSql = "select * from " +
                    pub.yssGetTableName("Tb_Rep_Custom") +
                    " where FCusRepCode=" + dbl.sqlString(cusRepCodes[i]);
                rsCus = dbl.openResultSet(strSql);
                while (rsCus.next()) {
                    repCustom = new RepCustomBean();
                    repCustom.setCusRepCode(rsCus.getString("FCusRepCode") + "");
                    repCustom.setCusRepName(rsCus.getString("FCusRepName") + "");
                    repCustom.setRepFormatCode(rsCus.getString("FRepFormatCode") +
                                               "");
                    repCustom.setRepType(rsCus.getString("FRepType") + "");
                    repCustom.setMaxCols(rsCus.getInt("FMaxCols"));
                    repCustom.setCtlGrpCode(rsCus.getString("FCtlGrpCode") + "");

                    repCustom.setParamSourceCode(rsCus.getString("FParamSource") +
                                                 "");
                    repCustom.setTmpTables(rsCus.getString("FTmpTables") + "");
                    repCustom.setDataSourceCodes(rsCus.getString("FSubDsCodes"));
                    repCustom.setSubRepCodes(rsCus.getString("FSubRepCodes") + "");
                    repCustom.setDesc(rsCus.getString("FDesc") + "");
                    customStr += repCustom.buildRowStr() + "\r\t";

                    if (repCustom.getRepFormatCode() != null && repCustom.getRepFormatCode().trim().length() > 0) {
                        formatCodes.add(repCustom.getRepFormatCode());
                    }
                    if (repCustom.getDataSourceCodes() != null && repCustom.getDataSourceCodes().trim().length() > 0) {
                        dataSourceCodes.add(repCustom.getDataSourceCodes());
                    }
                    if (repCustom.getCtlGrpCode() != null && repCustom.getCtlGrpCode().trim().length() > 0) {
                        // ctlGrpCodes.add(repCustom.getCtlGrpCode());
                        ctlGrps.add(repCustom.getCtlGrpCode());
                    }
                    if (repCustom.getSubRepCodes() != null && repCustom.getSubRepCodes().trim().length() > 0) {
                        subRepCodes.add(repCustom.getSubRepCodes()); //放明细报表
                    }
                    if (repCustom.getParamSourceCode() != null &&
                        repCustom.getParamSourceCode().trim().length() > 0 &&
                        !repCustom.getParamSourceCode().equalsIgnoreCase("null")) { //发现系统中有"null" 1031
                        dataSourceCodes.add(repCustom.getParamSourceCode()); //将参数数据源代码放入数据源Set中 add liyu 1031
                    }
                    boolCustomCodes.add(repCustom.getCusRepCode()); //被选择的报表报表
                }
                dbl.closeResultSetFinal(rsCus);
            }

            //---------------------------------------------------------------------------
            Iterator ita = subRepCodes.iterator(); //子报表的RepCode
            Iterator itb = boolCustomCodes.iterator(); //
            int iTemp = 0;
            String[] temp = new String[boolCustomCodes.size()];
            while (itb.hasNext()) {
                temp[iTemp] = (String) itb.next();
                iTemp++;
            }
            int flag = 0;
            while (ita.hasNext()) {
                repCustom = new RepCustomBean();
                subRepCode = ( (String) ita.next()).split(",");
                for (int i = 0; i < subRepCode.length; i++) {
                    for (int j = 0; j < temp.length; j++) {
                        if (temp[j].equals(subRepCode[i])) {
                            flag++;
                        }
                    }
                    if (flag == 0) {
                        strSql = "select * from " +
                            pub.yssGetTableName("Tb_Rep_Custom") +
                            " where FCusRepCode=" + dbl.sqlString(subRepCode[i]);
                        rsSubRep = dbl.openResultSet(strSql);
                        while (rsSubRep.next()) {
                            repCustom = new RepCustomBean();
                            repCustom.setCusRepCode(rsSubRep.getString("FCusRepCode") +
                                "");
                            repCustom.setCusRepName(rsSubRep.getString("FCusRepName") +
                                "");
                            repCustom.setRepFormatCode(rsSubRep.getString(
                                "FRepFormatCode") +
                                "");
                            repCustom.setRepType(rsSubRep.getString("FRepType") + "");
                            repCustom.setMaxCols(rsSubRep.getInt("FMaxCols"));
                            repCustom.setCtlGrpCode(rsSubRep.getString("FCtlGrpCode") +
                                "");

                            repCustom.setParamSourceCode(rsSubRep.getString(
                                "FParamSource") +
                                "");
                            repCustom.setTmpTables(rsSubRep.getString("FTmpTables") +
                                "");
                            repCustom.setDataSourceCodes(rsSubRep.getString(
                                "FSubDsCodes"));
                            repCustom.setSubRepCodes(rsSubRep.getString("FSubRepCodes") +
                                "");
                            repCustom.setDesc(rsSubRep.getString("FDesc") + "");
                            customStr += repCustom.buildRowStr() + "\r\t";

                            if (repCustom.getRepFormatCode() != null &&
                                repCustom.getRepFormatCode().trim().length() > 0) {
                                formatCodes.add(repCustom.getRepFormatCode());
                            }
                            if (repCustom.getDataSourceCodes() != null &&
                                repCustom.getDataSourceCodes().trim().length() > 0) {
                                dataSourceCodes.add(repCustom.getDataSourceCodes());
                            }
                            if (repCustom.getCtlGrpCode() != null &&
                                repCustom.getCtlGrpCode().trim().length() > 0) {
                                // ctlGrpCodes.add(repCustom.getCtlGrpCode());
                                ctlGrps.add(repCustom.getCtlGrpCode());
                            }
                            if (repCustom.getParamSourceCode() != null &&
                                repCustom.getParamSourceCode().trim().length() > 0 &&
                                !repCustom.getParamSourceCode().equalsIgnoreCase("null")) {
                                dataSourceCodes.add(repCustom.getParamSourceCode()); //将明细参数数据源代码放入数据源Set中 add liyu 1031
                            }

                        }

                    }
                    dbl.closeResultSetFinal(rsSubRep);
                    flag = 0;
                }

            }
            //----------------------报表格式----------------------------------------------
            Iterator it2 = formatCodes.iterator();
            while (it2.hasNext()) {
                formatCode = (String) it2.next();
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Format") +
                    " where FRepFormatCode=" + dbl.sqlString(formatCode);
                rsFormat = dbl.openResultSet(strSql);
                while (rsFormat.next()) {
                    repFormat = new RepFormatBean();
                    repFormat.setRepCode(rsFormat.getString("FRepFormatCode") + "");
                    repFormat.setRepName(rsFormat.getString("FRepFormatName") + "");
                    repFormat.setRepType(rsFormat.getString("FRepType") + "");
                    repFormat.setRepRows(rsFormat.getInt("FRows"));
                    repFormat.setRepCols(rsFormat.getInt("FCols"));
                    repFormat.setFixRows(rsFormat.getInt("FFixRows"));
                    repFormat.setFixCols(rsFormat.getInt("FFixCols"));
                    repFormat.setRCSize(rsFormat.getString("FRCSize") + "");
                    repFormat.setMerge(rsFormat.getString("FMerge") + "");
                    repFormat.setBalFmt(rsFormat.getString("FBalFmt") + "");
                    repFormat.setPrint(rsFormat.getString("FPrint") + "");
                    repFormat.setCtlGrpCode(rsFormat.getString("FCtlGrpCode") + "");
                    repFormat.setBeanId(rsFormat.getString("FBeanId") + "");
                    repFormat.setDesc(rsFormat.getString("FDesc") + "");
                    formatStr += repFormat.buildRowStr() + "\r\t";
                }
                dbl.closeResultSetFinal(rsFormat);
            }
            //---------------------------------fmt单元格---------------------------------
            Iterator it3 = formatCodes.iterator();
            while (it3.hasNext()) {
                formatCode = (String) it3.next();
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Cell") +
                    " where FRelaCode=" + dbl.sqlString(formatCode) +
                    " and FRelaType=" + dbl.sqlString("FMT");
                rsCell = dbl.openResultSet(strSql);
                while (rsCell.next()) {
                    repTabCell = new RepTabCellBean();
                    repTabCell.setRepCode(rsCell.getString("FRelaCode"));
                    repTabCell.setRelaType(rsCell.getString("FRelaType"));
                    repTabCell.setRow(rsCell.getString("FRow"));
                    repTabCell.setCol(rsCell.getString("FCol"));
                    repTabCell.setContent(rsCell.getString("FContent"));

                    repTabCell.setLLine(rsCell.getString("FLLine"));
                    repTabCell.setTLine(rsCell.getString("FTLine"));
                    repTabCell.setRLine(rsCell.getString("FRLine"));
                    repTabCell.setBLine(rsCell.getString("FBLine"));

                    repTabCell.setLColor(rsCell.getString("FLColor"));
                    repTabCell.setTColor(rsCell.getString("FTColor"));
                    repTabCell.setRColor(rsCell.getString("FRColor"));
                    repTabCell.setBColor(rsCell.getString("FBColor"));

                    repTabCell.setBackColor(rsCell.getString("FBackColor"));
                    repTabCell.setForeColor(rsCell.getString("FForeColor"));
                    repTabCell.setFontName(rsCell.getString("FFontName"));
                    repTabCell.setFontSize(rsCell.getString("FFontSize"));
                    repTabCell.setFontStyle(rsCell.getString("FFontStyle"));
                    repTabCell.setDataType(rsCell.getString("FDataType"));
                    repTabCell.setFormat(rsCell.getString("FFormat"));
                    repTabCell.setIMerge(rsCell.getInt("FIsMergeCol"));
                    tabCellStr += repTabCell.buildRowStr() + "\r\t";
                }
                dbl.closeResultSetFinal(rsCell);
            }
            //----------------------------------DS单元格---------------------------------
            Iterator it4 = dataSourceCodes.iterator();
            while (it4.hasNext()) {
                dataDsf = ( (String) it4.next()).split(",");
                for (int j = 0; j < dataDsf.length; j++) {
                    strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Cell") +
                        " where FRelaCode=" + dbl.sqlString(dataDsf[j]) +
                        " and FRelaType=" + dbl.sqlString("DSF");
                    rsCell = dbl.openResultSet(strSql);
                    while (rsCell.next()) {
                        repTabCell = new RepTabCellBean();
                        repTabCell.setRepCode(rsCell.getString("FRelaCode"));
                        repTabCell.setRelaType(rsCell.getString("FRelaType"));
                        repTabCell.setRow(rsCell.getString("FRow"));
                        repTabCell.setCol(rsCell.getString("FCol"));
                        repTabCell.setContent(rsCell.getString("FContent"));

                        repTabCell.setLLine(rsCell.getString("FLLine"));
                        repTabCell.setTLine(rsCell.getString("FTLine"));
                        repTabCell.setRLine(rsCell.getString("FRLine"));
                        repTabCell.setBLine(rsCell.getString("FBLine"));

                        repTabCell.setLColor(rsCell.getString("FLColor"));
                        repTabCell.setTColor(rsCell.getString("FTColor"));
                        repTabCell.setRColor(rsCell.getString("FRColor"));
                        repTabCell.setBColor(rsCell.getString("FBColor"));

                        repTabCell.setBackColor(rsCell.getString("FBackColor"));
                        repTabCell.setForeColor(rsCell.getString("FForeColor"));
                        repTabCell.setFontName(rsCell.getString("FFontName"));
                        repTabCell.setFontSize(rsCell.getString("FFontSize"));
                        repTabCell.setFontStyle(rsCell.getString("FFontStyle"));
                        repTabCell.setDataType(rsCell.getString("FDataType"));
                        repTabCell.setIMerge(rsCell.getInt("FIsMergeCol"));
                        if (rsCell.getString("FFormat") == null) {
                            repTabCell.setFormat("");
                        } else {
                            repTabCell.setFormat(rsCell.getString("FFormat"));
                        }
                        tabCellStr += repTabCell.buildRowStr() + "\r\t";
                    }
                    dbl.closeResultSetFinal(rsCell);
                }

            }
            //---------------------------------参数控件组---------------------------------
            Iterator it5 = ctlGrps.iterator();
            while (it5.hasNext()) {
                ctlGrpCode = (String) it5.next();
                strSql = "select * from Tb_PFSys_FaceCfgInfo where FCtlGrpCode=" +
                    dbl.sqlString(ctlGrpCode);
                rsCtl = dbl.openResultSet(strSql);
                while (rsCtl.next()) {
                    cfgInfo = new FaceCfgInfoBean();
                    cfgInfo.setYssPub(pub);
                    cfgInfo.setSCtlGrpCode(rsCtl.getString("FCtlGrpCode"));
                    cfgInfo.setSCtlGrpName(rsCtl.getString("FCtlGrpName"));
                    cfgInfo.setSCtlCode(rsCtl.getString("FCtlCode"));
                    cfgInfo.setIParamIndex(rsCtl.getInt("FParamIndex"));
                    cfgInfo.setICtlType(rsCtl.getInt("FCtlType"));
                    cfgInfo.setSParams(rsCtl.getString("FParam"));
                    cfgInfo.setSCtlInd(rsCtl.getString("FCtlInd"));
                    cfgInfo.setSFunModules(rsCtl.getString("FFunModules"));
                    paramCtlStr += cfgInfo.buildRowStr() + "\r\t";
                }
                dbl.closeResultSetFinal(rsCtl);
            }
            /*  Iterator it5=ctlGrpCodes.iterator();
              while(it5.hasNext())
              {
                 ctlGrpCode = (String)it5.next();
                 strSql = "select * from " +
                       pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                       " where FCtlGrpCode=" + dbl.sqlString(ctlGrpCode);
                 rsCtlGrp = dbl.openResultSet(strSql);
                 while (rsCtlGrp.next()) {
                    repParamCtlGrp = new RepParamCtlGrpBean();
                    repParamCtlGrp.setCtlGrpCode(rsCtlGrp.getString("FCtlGrpCode") +
                                                 "");
                    repParamCtlGrp.setCtlGrpName(rsCtlGrp.getString("FCtlGrpName") +
                                                 "");
                    paramCtlGrpStr += repParamCtlGrp.buildRowStr() + "\r\t";
                    ctlCodes.add(repParamCtlGrp.getCtlGrpCode());
                 }
                 dbl.closeResultSetFinal(rsCtlGrp);
              }
              //--------------------------------参数控件------------------------------------
              Iterator it6=ctlCodes.iterator();
              while(it6.hasNext())
              {
                 ctlCode = (String)it6.next();
                 strSql = "select * from " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                       " where FCtlGrpCode=" + dbl.sqlString(ctlCode);
                 rsCtl = dbl.openResultSet(strSql);
                 while (rsCtl.next()) {
                    repParamCtl = new RepParamCtlBean();
                    repParamCtl.setCtlGrpCode(rsCtl.getString("FCtlGrpCode") + "");
                    repParamCtl.setCtlCode(rsCtl.getString("FCtlCode") + "");
                    repParamCtl.setParamIndex(rsCtl.getInt("FParamIndex"));
                    repParamCtl.setCtlType(rsCtl.getInt("FCtlType"));
                    repParamCtl.setParam(rsCtl.getString("FParam") + "");
                    paramCtlStr += repParamCtl.buildRowStr() + "\r\t";
                 }
                 dbl.closeResultSetFinal(rsCtl);
              }*/
            //--------------------------------------数据源--------------------------
            Iterator it7 = dataSourceCodes.iterator();
            while (it7.hasNext()) {
                dataSourceCode = ( (String) it7.next()).split(",");
                for (int j = 0; j < dataSourceCode.length; j++) {
                    strSql = "select * from " +
                        pub.yssGetTableName("Tb_Rep_DataSource") +
                        " where FRepDsCode=" + dbl.sqlString(dataSourceCode[j]);
                    rsDs = dbl.openResultSet(strSql);
                    while (rsDs.next()) {
                        repDataSource = new RepDataSourceBean();
                        repDataSource.setRepDsCode(rsDs.getString("FRepDsCode") + "");
                        repDataSource.setRepDsName(rsDs.getString("FRepDsName") + "");
                        repDataSource.setDsType(rsDs.getString("FDsType") + "");
                        repDataSource.setTRowColor(rsDs.getString("FTRowColor") + "");
                        repDataSource.setBRowColor(rsDs.getString("FBRowColor") + "");
                        repDataSource.setTempTab(rsDs.getString("FTempTab") + "");
                        repDataSource.setBeanID(rsDs.getString("FBeanId") + "");
                        repDataSource.setDataSource
                            (dbl.clobStrValue(rsDs.getClob("FDataSource")).
                             replaceAll(
                                 "\t", "   "));
                        repDataSource.setFillRange(rsDs.getString("FFillRange") + "");
                        repDataSource.setDesc(rsDs.getString("FDesc") + "");
                        dataSourceStr += repDataSource.buildRowStr() + "\r\t";
                        dataSourceFieldCodes.add(repDataSource.getRepDsCode());
                    }
                    dbl.closeResultSetFinal(rsDs);
                }
            }
            //-----------------------------------数据源格式---------------------------------------
            Iterator it8 = dataSourceFieldCodes.iterator();
            while (it8.hasNext()) {
                dataSourceFieldCode = ( (String) it8.next());
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                    " where FRepDsCode=" + dbl.sqlString(dataSourceFieldCode);
                rsDsField = dbl.openResultSet(strSql);
                while (rsDsField.next()) {
                    repDsField = new RepDsFieldBean();
                    repDsField.setDsCode(rsDsField.getString("FRepDsCode") + "");
                    repDsField.setOrderIndex(rsDsField.getString("FOrderIndex") + "");
                    repDsField.setDsField(rsDsField.getString("FDsField") + "");
                    repDsField.setIsTotalInd(rsDsField.getString("FTotalInd") + "");
                    repDsField.setIsTotal(rsDsField.getString("FIsTotal") + "");
                    repDsField.setStrColKey(rsDsField.getString("FColKey"));
                    dsFieldStr += repDsField.buildRowStr() + "\r\t";
                }
                dbl.closeResultSetFinal(rsDsField);
            }
        } catch (Exception e) {
            throw new YssException("导出数据出错");
        }
        strResult = customStr + "\r\f" +
            formatStr + "\r\f" +
            //   paramCtlGrpStr + "\r\f" +
            paramCtlStr + "\r\f" +
            dataSourceStr + "\r\f" +
            dsFieldStr + "\r\f" +
            tabCellStr;
        return strResult;

    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
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
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() {
        return "";
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
//      dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_001_rep_custom"),
//                             "FCusRepCode",
//                             this.fCusRepCode, this);
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
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
    public IDataSetting getSetting() {
        return null;
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
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    public String getCustomSetting(String squest) throws YssException {
        RepCustomBean repCustomBean = new RepCustomBean();
        repCustomBean.setYssPub(pub);
        String sResult = "";

        try {
            repCustomBean.parseRowStr(squest);
            sResult = repCustomBean.buildRowStr();
        } catch (YssException e) {
            throw new YssException("获取报表自定义设置数据出错", e);
        }

        return sResult;
    }

}
