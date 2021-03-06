package com.yss.main.funsetting;

import java.io.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CommonParamsBean
    extends BaseDataSettingBean implements IDataSetting, Serializable {

    private String sCPTypeCode = ""; //参数类型代码
    private String sCPTypeName = ""; //参数类型名称
    private String sCondType = ""; //条件类型
    private String sCondTypeName = ""; //条件类型名称
    private String sCondCode = ""; //条件代码
    private String sCondName = ""; //条件名称
    private String sDesc = ""; //描述
    private String sOldCPTypeCode = "";
    private String status = ""; //是否记入系统信息状态
    private boolean statu = true;

    private String CommonParams = "";
    private String CommonParamsSubs = "";
    //public int checkStateId; //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
    private String sRecycled = ""; //add by nimengjing 2010.12.27 BUG #749 通用参数设置界面存在问题 
    private CommonParamsBean filterType;

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Fun_CommonParams",
                               "FCPTypeCode", sCPTypeCode, sOldCPTypeCode);
    }

//新增
    public String addSetting() throws YssException {
        this.setYssPub(pub);
        CommonParamsSubBean CommonParamsSub = new CommonParamsSubBean();
        CommonParamsSub.setYssPub(pub);
        try {
            if (this.CommonParams != null) {
                this.saveMutliSetting(this.CommonParams);
            }
            if (this.CommonParamsSubs != null) {
                CommonParamsSub.saveMutliSetting(this.CommonParamsSubs, this.checkStateId, this.status);
            }

        } catch (Exception e) {
            throw new YssException("新增通用参数属性设置出错!");
        }

        return "";
    }

//修改
    public String editSetting() throws YssException {
        this.setYssPub(pub);
        CommonParamsSubBean CommonParamsSub = new CommonParamsSubBean();
        CommonParamsSub.setYssPub(pub);
        try {
            if (this.CommonParams != null) {
                this.saveMutliSetting(this.CommonParams);
            }
            
            if (this.CommonParamsSubs != null) {
                CommonParamsSub.saveMutliSetting(this.CommonParamsSubs, this.checkStateId, this.status);
            }

        } catch (Exception e) {
            throw new YssException("新增通用参数属性设置出错!");
        }
        return "";
    }

//删除
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        this.setYssPub(pub);
        CommonParamsSubBean CommonParamsSub = new CommonParamsSubBean();
        CommonParamsSub.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = " update Tb_Fun_CommonParams" +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) : "' '") +
                " where FCPTypeCode=" + dbl.sqlString(this.sCPTypeCode);
            dbl.executeSql(strSql);
            //---------------记入系统日志
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-通用参数设置");
                sysdata.setStrCode(this.sCPTypeCode);
                sysdata.setStrName(this.sCPTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            strSql = " update Tb_Fun_CommonParamsSub" +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) : "' '") +
                " where FCPTypeCode=" + dbl.sqlString(this.sCPTypeCode);
            dbl.executeSql(strSql);

            //---------------记入系统日志
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-通用子参数设置");
                sysdata.setStrCode(this.sCPTypeCode);
                sysdata.setStrName(this.sCPTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除通用参数设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

//审核
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        
        PreparedStatement stm = null;    //add by zhangjun 2012-03-01 BUG3833通用参数设置修改的回收站问题 
        String[] arrData = null;

        this.setYssPub(pub);
        CommonParamsSubBean CommonParamsSub = new CommonParamsSubBean();
        CommonParamsSub.setYssPub(pub);
        try {
        	//add by zhangjun 2012-03-01 BUG3833通用参数设置修改的回收站问题 
        	conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\n");
            //---------end ----------------------------
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = " update Tb_Fun_CommonParams" +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) : "' '") +
                " where FCPTypeCode= ? " ;
                //" where FCPTypeCode=" + dbl.sqlString(this.sCPTypeCode);  //modify by zhangjun 2012-03-01 BUG3833通用参数设置修改的回收站问题 
                
            //dbl.executeSql(strSql);
            stm = dbl.openPreparedStatement(strSql);
            conn.setAutoCommit(false);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.sCPTypeCode);
                stm.executeUpdate();
            }
            
            
            //---------------记入系统日志
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                if (this.checkStateId == 1) {
                    sysdata.setStrFunName("审核-通用参数设置");
                } else {
                    sysdata.setStrFunName("反审核-通用参数设置");
                }
                sysdata.setStrCode(this.sCPTypeCode);
                sysdata.setStrName(this.sCPTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            strSql = " update Tb_Fun_CommonParamsSub" +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) : "' '") +
                " where FCPTypeCode= ? ";   //modify by zhangjun 2012-03-01 BUG3833通用参数设置修改的回收站问题 
               // " where FCPTypeCode=" + dbl.sqlString(this.sCPTypeCode);
            //dbl.executeSql(strSql);
          //add by zhangjun 2012-03-01 BUG3833通用参数设置修改的回收站问题 
            stm = dbl.openPreparedStatement(strSql);
            conn.setAutoCommit(false);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.sCPTypeCode);
                stm.executeUpdate();
            }
            //--------------------------end -------------------------------
            
            //---------------记入系统日志
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                if (this.checkStateId == 1) {
                    sysdata.setStrFunName("审核-通用子参数设置");
                } else {
                    sysdata.setStrFunName("反审核-通用子参数设置");
                }
                sysdata.setStrCode(this.sCPTypeCode);
                sysdata.setStrName(this.sCPTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核通用参数设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";

            if (this.filterType.sCPTypeCode.length() != 0) {
//                sResult = sResult + " and a.FCPTypeCode = '" +
//                    filterType.sCPTypeCode.replaceAll("'", "''") + "'";
            	//update by guolongchao 20111021 bug 2227 实现模糊查询
                sResult = sResult + " and a.FCPTypeCode like '" +
                filterType.sCPTypeCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.sCPTypeName.length() != 0) {
//                sResult = sResult + " and a.FCPTypeName = '" +
//                    filterType.sCPTypeName.replaceAll("'", "''") + "'";
            	//update by guolongchao 20111021 bug 2227 实现模糊查询
                sResult = sResult + " and a.FCPTypeName like '" +
                filterType.sCPTypeName.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;

    }

    private String buildCodeFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";

            if (this.filterType.sCPTypeCode.length() != 0) {
                sResult = sResult + " and a.FCPTypeCode = '" +
                    filterType.sCPTypeCode.replaceAll("'", "''") + "'";
            } else {
                sResult = sResult + " and 1=2 ";
            }
        }
        return sResult;

    }

    public String getListViewData3() throws YssException {
//      String sHeader = "";
//        String sShowDataStr = "";
//        String sAllDataStr = "";
//        String strSql = "";
//        ResultSet rs = null;
//        String sVocStr = "";
//        StringBuffer bufShow = new StringBuffer();
//        StringBuffer bufAll = new StringBuffer();
//        try {
//           sHeader = this.getListView1Headers();
//       strSql=
//        "select  a.* from (select * from Tb_Fun_CommonParams  where FCheckState <> 2) x join"+
//        " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ,d.FVocName as FCondTypeName,d.FVocTypeCode as FVocTypeCode  ,e.FPortName as FCondName  "+
//        " from Tb_Fun_CommonParams a" +
//        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
//        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
//        " left join Tb_Fun_Vocabulary d on a.FCondType = d.FVocCode and d.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FUN_CondType)+" "+
//          " left join (select FPortCode, FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") +" where FCheckState =1)  e on a.FCondCode = e.FPortCode" +
//        buildFilterSql() +" ) a  on  x.FCPTypeCode =a.FCPTypeCode and x.FCondType=a.FCondType and  x.FCondCode=a.FCondCode order by a.FCPTypeCode ";
//
//         rs = dbl.openResultSet(strSql);
//           while (rs.next()) {
//              bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                    append(YssCons.YSS_LINESPLITMARK);
//              this.setCommonParamsAttr(rs);
//              bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
//           }
//           if (bufShow.toString().length() > 2) {
//              sShowDataStr = bufShow.toString().substring(0,
//                    bufShow.toString().length() - 2);
//           }
//
//           if (bufAll.toString().length() > 2) {
//              sAllDataStr = bufAll.toString().substring(0,
//                    bufAll.toString().length() - 2);
//           }
//
//           VocabularyBean vocabulary = new VocabularyBean();
//                     vocabulary.setYssPub(pub);
//
//                     sVocStr = vocabulary.getVoc(YssCons.YSS_FUN_CondType);
//
//           return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
//                 this.getListView1ShowCols()+"\r\fvoc"+sVocStr ;
//
//        }
//        catch (Exception e) {
//           throw new YssException("获取通用参数设置出错！", e);
//        }
//        finally {
//           dbl.closeResultSetFinal(rs);
//        }
        return "";
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            //sHeader = "条件类型\t条件类型名称\t条件代码\t条件名称";
            sHeader = this.getListView1Headers();
            strSql =
                "select a.FCptypeCode,a.FCptypeName, a.FCondType,a.FCondCode,a.FDesc,a.FCheckState,a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime," +
                " (case when a.FCondType='Assetgroup' then (select FAssetGroupName from TB_SYS_ASSETGROUP where FAssetGroupCode=FCondCode)" +
                "  when a.FCondType='Port' then (select FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FPortCode=FCondCode)" +
                "  when a.FCondType='SecCat' then (select FCatName from Tb_Base_Category where FCatCode=FCondCode)" +
                "  when a.FCondType='SecCatSub' then (select FSubCatName from Tb_Base_SubCategory where FSubCatCode=FCondCode)" +
                "  when a.FCondType='CashCat' then (select FAccTypeName from Tb_Base_AccountType where FAccTypeCode=FCondCode)" +
                "  when a.FCondType='CashCatSub' then (select FSubAccTypeName from Tb_Base_SubAccountType where FSubAccTypeCode=FCondCode)" +
                "  end) as FCondName, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FVocName as FCondTypeName" +
                "  from Tb_Fun_CommonParams a" +
                "  left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                "  left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                "  left join Tb_Fun_Vocabulary d on a.FCondType = d.FVocCode and d.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FUN_CondType) + " " +
                "  and a.FCheckState<>2 " +
                buildCodeFilterSql();

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCondType") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCondTypeName") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCondCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCondName") + "").trim()).
                    append("\t").append(YssCons.YSS_LINESPLITMARK);

                setCommonParamsAttr(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取条件信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.CommonParams = sRowStr.split("\r\t")[2];
                }
                if (sRowStr.split("\r\t").length == 4) {
                    this.CommonParams = sRowStr.split("\r\t")[2];
                    this.CommonParamsSubs = sRowStr.split("\r\t")[3];
                }

            } else {
                sTmpStr = sRowStr;
            }
            sRecycled=sRowStr; //add by nimengjing 2010.12.27 BUG #749 通用参数设置界面存在问题 
            reqAry = sTmpStr.split("\t");

            this.sCPTypeCode = reqAry[0];
            this.sCPTypeName = reqAry[1];
            this.sCondType = reqAry[2];
            this.sCondTypeName = reqAry[3];
            this.sCondCode = reqAry[4];
            this.sCondName = reqAry[5];
            this.sDesc = reqAry[6];
            this.checkStateId = YssFun.toInt(reqAry[7]);
            this.sOldCPTypeCode = reqAry[8];
            if (statu) {
                this.status = reqAry[9];
                statu = false;
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new CommonParamsBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析通用参数设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        buf.append(this.sCPTypeCode).append("\t");
        buf.append(this.sCPTypeName).append("\t");
        buf.append(this.sCondType).append("\t");
        buf.append(this.sCondTypeName).append("\t");
        buf.append(this.sCondCode).append("\t");
        buf.append(this.sCondName).append("\t");
        buf.append(this.sDesc).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        try {
            String sHeader = "";
            String sShowCols = "";
            String sShowDataStr = "";
            String sAllDataStr = "";
            String strSql = "";
            ResultSet rs = null;
            String sVocStr = "";
            StringBuffer bufShow = new StringBuffer();
            StringBuffer bufAll = new StringBuffer();
            sHeader = "参数代码\t参数名称";
            sShowCols = "FCPTypeCode\tFCPTypeName";
            strSql = "select distinct(a.FCpTypeCode) as FCpTypeCode,a.FCpTypeName,a.FCheckState from Tb_Fun_CommonParams a " +
                buildFilterSql() + "order by a.FCPTypeCode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FCpTypeCode")).append("\t").append(rs.getString("FCpTypeName")).append("\t").
                    append(YssCons.YSS_LINESPLITMARK);
                // this.setCommonParamsAttr(rs);
                this.sCPTypeCode = rs.getString("FCpTypeCode");
                this.sCPTypeName = rs.getString("FCpTypeName");
                super.checkStateId = rs.getInt("FCheckState");
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_FUN_CondType);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                sShowCols + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取条件信息出错", e);
        }
    }

    public void setCommonParamsAttr(ResultSet rs) throws YssException {
        try {
            this.sCPTypeCode = rs.getString("FCPTypeCode");
            this.sCPTypeName = rs.getString("FCPTypeName");
            this.sCondType = rs.getString("FCondType");
            this.sCondTypeName = rs.getString("FCondTypeName");
            this.sCondCode = rs.getString("FCondCode");
            this.sCondName = rs.getString("FCondName");
            this.sDesc = rs.getString("FDesc");

            super.setRecLog(rs);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);
            strSql = "delete from  Tb_Fun_CommonParams" +
                " where FCPTypeCode=" + dbl.sqlString(this.sCPTypeCode);

            dbl.executeSql(strSql);
            //---------------记入系统日志
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-通用参数设置");
                sysdata.setStrCode(this.sCPTypeCode);
                sysdata.setStrName(this.sCPTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            strSql =
                "insert into Tb_Fun_CommonParams " +
                "(FCPTypeCode,FCPTypeName,FCondType,FCondCode,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            for (int i = 0; i < sMutilRowAry.length; i++) {
                //   if (i > 0) {
                this.parseCondRowStr(sMutilRowAry[i]);
                //  }

                pstmt.setString(1, this.sCPTypeCode);
                pstmt.setString(2, this.sCPTypeName);
                pstmt.setString(3, this.sCondType);
                pstmt.setString(4, this.sCondCode);
                pstmt.setString(5, this.sDesc);
                pstmt.setInt(6, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(7, this.creatorCode);
                pstmt.setString(8, this.creatorTime);
                pstmt.setString(9, (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.executeUpdate();

                //---------------记入系统日志
                if (this.status.equalsIgnoreCase("1")) {
                    String sql = "insert into Tb_Fun_CommonParams " +
                        "(FCPTypeCode,FCPTypeName,FCondType,FCondCode,FDesc," +
                        "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                        " values(" + dbl.sqlString(this.sCPTypeCode) + "," + dbl.sqlString(this.sCPTypeName) + "," +
                        dbl.sqlString(this.sCondType) + "," + dbl.sqlString(this.sCondCode) + "," + dbl.sqlString(this.sDesc) +
                        "," + (pub.getSysCheckState() ? 0 : 1) + "," + dbl.sqlString(this.creatorCode) + "," +
                        dbl.sqlString(this.creatorTime) + "," + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";

                    com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                        funsetting.SysDataBean();
                    sysdata.setYssPub(pub);
                    sysdata.setStrAssetGroupCode("Common");
                    sysdata.setStrFunName("新增-通用参数设置");
                    sysdata.setStrCode(this.sCPTypeCode);
                    sysdata.setStrName(this.sCPTypeName);
                    sysdata.setStrUpdateSql(sql);
                    sysdata.setStrCreator(pub.getUserName());
                    sysdata.addSetting();
                }
            }
        } catch (Exception e) {
            throw new YssException("保存通用参数设置信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            return "";
        }
    }

    public void parseCondRowStr(String sRowStr) throws YssException {
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

            this.sCPTypeCode = reqAry[0];
            this.sCPTypeName = reqAry[1];
            this.sCondType = reqAry[2];
            this.sCondTypeName = reqAry[3];
            this.sCondCode = reqAry[4];
            this.sCondName = reqAry[5];
            this.sDesc = reqAry[6];
            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析通用参数设置出错", e);
        }

    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    /**
     * 回收站清除功能   add by nimengjing 2010.12.27BUG #749 通用参数设置界面存在问题 
     * deleteRecycleData
     */
    public void deleteRecycleData()throws YssException {
    	String strSql="";
    	String[] arrData=null;
    	boolean bTrans = false;
    	Connection conn=dbl.loadConnection();
    	try {
    		if(sRecycled!=null&&sRecycled!="" ){
    			arrData=sRecycled.split("\r\n");
    			 conn.setAutoCommit(false);
                 bTrans = true;
                 for (int i = 0; i < arrData.length; i++) {
					if(arrData[i].length()==0){
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql="delete from Tb_Fun_CommonParams "+
					" where FCPTYPECODE="+dbl.sqlString(this.sCPTypeCode)+
					" and FCPTYPENAME="+dbl.sqlString(this.sCPTypeName);
					dbl.executeSql(strSql);
				}
    		}
			conn.commit();
			bTrans=false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除通用参数设置数据出错", e);
		}finally{
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
