package com.yss.main.funsetting;

import java.io.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CommonParamsAttrBean
    extends BaseDataSettingBean implements IDataSetting, Serializable {

    private String sCPAttrCode = ""; //参数属性代码
    private String sCPAttrName = ""; //参数属性名称
    private String sAttrSrc = ""; //属性来源
    //private String sVocCode="";     //词汇代码
    private String sVocName = ""; //词汇名称
    private String sAttrCfg = ""; //属性配置
    private String sDesc = ""; //描述
    private String sOldCPAttrCode = ""; //参数属性代码
    private String status = ""; //是否记入系统信息状态
    private CommonParamsAttrBean filterType;
    
    private String sRecycled = ""; //保存未解析前的字符串  add by wangzuochun 2010.11.12 BUG #386 通用参数属性设置界面回收站的信息不能被清除

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Fun_CommonParamsAttr",
                               "FCPAttrCode", sCPAttrCode, sOldCPAttrCode);
    }

//新增
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务

        try {
            strSql = "insert into  Tb_Fun_CommonParamsAttr" +
                "( FCPAttrCode,FCPAttrName,FAttrSrc,FAttrCfg,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" +

                dbl.sqlString(this.sCPAttrCode) + "," +
                dbl.sqlString(this.sCPAttrName) + "," +
                dbl.sqlString(this.sAttrSrc) + "," +
                dbl.sqlString(this.sAttrCfg) + "," +
                dbl.sqlString(this.sDesc) + "," +
                (pub.getSysCheckState() ? 0 : 1) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString( (pub.getSysCheckState() ? " " : this.creatorCode)) +
                ")";

            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------------记入系统日志
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-通用参数属性设置");
                sysdata.setStrCode(this.sCPAttrCode);
                sysdata.setStrName(this.sCPAttrName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增通用参数属性设置出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

//修改
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = " update Tb_Fun_CommonParamsAttr" +
                " set FCPAttrCode=" + dbl.sqlString(this.sCPAttrCode) + "," +
                " FCPAttrName=" + dbl.sqlString(this.sCPAttrName) + "," +
                " FAttrSrc=" + dbl.sqlString(this.sAttrSrc) + "," +
                " FAttrCfg=" + dbl.sqlString(this.sAttrCfg) + "," +
                " FDesc =" + dbl.sqlString(this.sDesc) + "," +
                " FCheckState=" + this.checkStateId + "," +
                " FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) : "' '") +
                " where FCPAttrCode=" + dbl.sqlString(this.sOldCPAttrCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------------记入系统日志

            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-通用参数属性设置");
                sysdata.setStrCode(this.sCPAttrCode);
                sysdata.setStrName(this.sCPAttrName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改通用参数属性设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

//删除
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update Tb_Fun_CommonParamsAttr" +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) : "' '") +
                " where FCPAttrCode=" + dbl.sqlString(this.sCPAttrCode);

            bTrans = true;
            dbl.executeSql(strSql);
            //---------------记入系统日志

            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-通用参数属性设置");
                sysdata.setStrCode(this.sCPAttrCode);
                sysdata.setStrName(this.sCPAttrName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除通用参数属性设置出错", e);
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
        
        try {
        	//add by zhangjun 2012-03-01 BUG3833通用参数设置修改的回收站问题 
        	conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            //---------end ----------------------------
        	
            strSql = " update Tb_Fun_CommonParamsAttr" +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) : "' '") +
                " where FCPAttrCode= ? " ; //modify by zhangjun 2012-03-01 BUG3833通用参数设置修改的回收站问题 
               // " where FCPAttrCode=" + dbl.sqlString(this.sCPAttrCode);

            //conn.setAutoCommit(false);
            //bTrans = true;
            //dbl.executeSql(strSql);
            stm = dbl.openPreparedStatement(strSql);
            conn.setAutoCommit(false);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.sCPAttrCode);
                stm.executeUpdate();
            }

            //---------------记入系统日志
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                if (this.checkStateId == 1) {
                    sysdata.setStrFunName("审核-通用参数属性设置");
                } else {
                    sysdata.setStrFunName("反审核-通用参数属性设置");
                }

                sysdata.setStrCode(this.sCPAttrCode);
                sysdata.setStrName(this.sCPAttrName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核通用参数属性设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeStatementFinal(stm);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
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

            if (this.filterType.sCPAttrCode.length() != 0) {
                sResult = sResult + " and a.FCPAttrCode like '" +
                    filterType.sCPAttrCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.sCPAttrName.length() != 0) {
                sResult = sResult + " and a.FCPAttrName like '" +
                    filterType.sCPAttrName.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.sAttrSrc.length() != 0 && !this.filterType.sAttrSrc.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FAttrSrc ='" +
                    filterType.sAttrSrc + "'";
            }
            if (this.filterType.sAttrCfg.length() != 0) {
                sResult = sResult + " and a.FAttrCfg ='" +
                    filterType.sAttrCfg + "'";
            }

            if (this.filterType.sDesc.length() != 0) {
                sResult = sResult + " and a.FDesc ='" +
                    filterType.sDesc + "'";
            }

        }
        return sResult;

    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        String strSql = "";
        ResultSet rs = null;
        try {
            sHeader = "参数属性代码\t参数属性名称";
            strSql = "select  a.* from (select * from Tb_Fun_CommonParamsAttr  where FCheckState = 1) x join (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FVocName as FAttrSrcName,d.FVocTypeCode as FVocTypeCode,e.FVocTypeName as FVocName " +
                " from Tb_Fun_CommonParamsAttr a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary d on a.FAttrSrc = d.FVocCode and d.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FUN_AttrSrc) +
                " left join (select FVocTypeCode,FVocTypeName from Tb_Fun_VocabularyType) e on a.FAttrCfg = e.FVocTypeCode" +
                " where a.FCheckState = 1 ) a  on a.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FUN_AttrSrc) + "and x.FCPAttrCode =a.FCPAttrCode order by a.FCPAttrCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {

                bufShow.append( (rs.getString("FCPAttrCode") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FCPAttrName") + "").trim());
                bufShow.append(YssCons.YSS_LINESPLITMARK);

                setCommonParamsAttrAttr(rs);
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
            throw new YssException("获取通用参数属性设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData3() throws YssException {
        return "";
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
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled  add by wangzuochun 2010.11.12 BUG #386 通用参数属性设置界面回收站的信息不能被清除

            reqAry = sTmpStr.split("\t");
            this.sCPAttrCode = reqAry[0];
            this.sCPAttrName = reqAry[1];
            this.sAttrSrc = reqAry[2];
            this.sAttrCfg = reqAry[3];
            this.sDesc = reqAry[4];
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.sOldCPAttrCode = reqAry[6];
            this.status = reqAry[7];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CommonParamsAttrBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析通用参数属性设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        buf.append(this.sCPAttrCode).append("\t");
        buf.append(this.sCPAttrName).append("\t");
        buf.append(this.sAttrSrc).append("\t");
        buf.append(this.sAttrCfg).append("\t");
        buf.append(this.sVocName).append("\t");
        buf.append(this.sDesc).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        StringBuffer bufAll = new StringBuffer();
        String sAllDataStr = "";
        String str = ""; //根据属性查询代码查询所有字段
        ResultSet rs = null;
        try {
            str = "select * from  Tb_Fun_CommonParamsAttr" +
                " where FCPAttrCode =" + dbl.sqlString(sType);
            rs = dbl.openResultSet(str);
            while (rs.next()) {
                this.sCPAttrCode = rs.getString("FCPAttrCode");
                this.sCPAttrName = rs.getString("FCPAttrName");
                this.sAttrSrc = rs.getString("FAttrSrc");
                this.sAttrCfg = rs.getString("FAttrCfg");
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

        } catch (Exception e) {
            throw new YssException("获取参数属性错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sAllDataStr;

    }

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();

            strSql =
                "select  a.* from (select * from Tb_Fun_CommonParamsAttr )" + // modify by wangzuochun 2010.08.30 MS01652    删除数据，在回收站中查询不到    QDV4赢时胜(测试)2010年08月24日01_B  
                " x join (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FVocName as FAttrSrcName,d.FVocTypeCode as FVocTypeCode,e.FVocTypeName as FVocName " +
                " from Tb_Fun_CommonParamsAttr a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary d on a.FAttrSrc = d.FVocCode and d.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FUN_AttrSrc) +
                " left join (select FVocTypeCode,FVocTypeName from Tb_Fun_VocabularyType) e on a.FAttrCfg = e.FVocTypeCode" +
                buildFilterSql() + ") a  on a.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_AttrSrc) +
                "and x.FCPAttrCode =a.FCPAttrCode order by a.FCPAttrCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setCommonParamsAttrAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_FUN_AttrSrc);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取通用参数属性设置出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setCommonParamsAttrAttr(ResultSet rs) throws YssException {
        try {
            this.sCPAttrCode = rs.getString("FCPAttrCode");
            this.sCPAttrName = rs.getString("FCPAttrName");
            this.sAttrSrc = rs.getString("FAttrSrc");
            this.sAttrCfg = rs.getString("FAttrCfg");
            this.sVocName = rs.getString("FVocName");
            this.sDesc = rs.getString("FDesc");

            super.setRecLog(rs);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    /**
     * add by wangzuochun 2010.11.12 BUG #386 通用参数属性设置界面回收站的信息不能被清除
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException  {
    	
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
                    //SQL删除语句：从银行费用业务表中删除清除操作所对应的记录
                    strSql = "delete from Tb_Fun_CommonParamsAttr " +
                        " where fcpattrcode = " +
                        dbl.sqlString(this.sCPAttrCode) +
                        " and fcpattrname=" +dbl.sqlString(this.sCPAttrName);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("清除通用参数属性设置数据出错", e);
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
