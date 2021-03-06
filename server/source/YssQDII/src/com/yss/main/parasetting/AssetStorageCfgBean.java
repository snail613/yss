package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: AssetGroupCfgBean </p>
 * <p>Description: 库存信息配置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AssetStorageCfgBean
    extends BaseDataSettingBean implements IDataSetting {

    private String strStorageType = ""; //库存类型
    private String strAnalysisCode1 = ""; //分析代码1
    private String strAnalysisCode2 = ""; //分析代码2
    private String strAnalysisCode3 = ""; //分析代码3
    private String strAnalysisName1 = ""; //分析名称1
    private String strAnalysisName2 = ""; //分析名称2
    private String strAnalysisName3 = ""; //分析名称3
    private String strParams1 = "";
    private String strParams2 = "";
    private String strParams3 = "";
    private String sRecycled = "";

    private String strDesc = ""; //配置描述

    private String strOldStorageType = "";
    private boolean bOverGroup = false;//判断是否跨组合群，在收益支付中如果组合群没有设置投资经理，则去掉修改界面中的投资经理相关控件。MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090814
    private String assetGroupCode = "";//组合群代码 ，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090814
    private AssetStorageCfgBean filterType;

    public AssetStorageCfgBean() {
    }

    /**
     * parseRowStr
     * 解析库存信息配置请求
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
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
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.strStorageType = reqAry[0];
            this.strAnalysisCode1 = reqAry[1];
            this.strAnalysisCode2 = reqAry[2];
            this.strAnalysisCode3 = reqAry[3];
            //add by nimengjing 2011.2.15 BUG #1102 库存信息配置界面问题 
            if (reqAry[4] != null ){
	        	 if (reqAry[4].indexOf("【Enter】") >= 0){
	        		 this.strDesc= reqAry[4].replaceAll("【Enter】", "\r\n");
	             }
	             else{
	            	 this.strDesc = reqAry[4];
	             }
	         }
            //----------------------------end bug #1102--------------------------------
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.strOldStorageType = reqAry[6];
            if(reqAry[7].equals("true")){
                this.bOverGroup = true;
            }
            this.assetGroupCode = reqAry[8];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new AssetStorageCfgBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析库存信息配置请求出错", e);
        }
    }

    /**
     * auditSetting
     */
    public void auditSetting() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strStorageType).append("\t");
        buf.append(this.strAnalysisCode1).append("\t");
        buf.append(this.strAnalysisName1).append("\t");
        buf.append(this.strAnalysisCode2).append("\t");
        buf.append(this.strAnalysisName2).append("\t");
        buf.append(this.strAnalysisCode3).append("\t");
        buf.append(this.strAnalysisName3).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     */
    public void checkInput() {
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
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strStorageType.length() != 0 &&
                !this.filterType.strStorageType.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FStorageType like '" +
                    filterType.strStorageType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strAnalysisCode1.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.strAnalysisCode1.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strAnalysisCode2.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode2 like '" +
                    filterType.strAnalysisCode2.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strAnalysisCode3.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.strAnalysisCode3.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取库存信息配置信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            String sql =
                "select a.*, f.FVocName as FStorageTypeValue, b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                " d.FAnalysisName as FAnalysisName1, e.FAnalysisName as FAnalysisName2," +
                " f.FAnalysisName as FAnalysisName3 from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) d on a.FAnalysisCode1 = d.FAnalysisCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) e on a.FAnalysisCode2 = e.FAnalysisCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) f on a.FAnalysisCode3 = f.FAnalysisCode" +
                " left join Tb_Fun_Vocabulary f on a.FStorageType = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_SCG_TYPE) +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setStorageAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_SCG_TYPE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取库存信息配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的库存信息配置信息
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "库存信息类型";
            String sql =
                "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                " d.FAnalysisName as FAnalysisName1, e.FAnalysisName as FAnalysisName2," +
                " f.FAnalysisName as FAnalysisName3 from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) d on a.FAnalysisCode1 = d.FAnalysisCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) e on a.FAnalysisCode2 = e.FAnalysisCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) f on a.FAnalysisCode3 = f.FAnalysisCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FStorageType") + "").trim()).
                    append("\t").append(YssCons.YSS_LINESPLITMARK);

                setStorageAttr(rs);
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
            throw new YssException("获取库存信息配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData3
     *
     * @return String
     * @throws YssException 
     */
    public String getListViewData3() throws YssException {
    	//bug 3152 by zhouwei 20111121 QDV4农业银行2011年11月14日02_B
    	//查询库存信息配置中，库存类型为证券与现金，是否有001 投资经理的配置代码
    	ResultSet rs=null;
    	String returnStr="0";//默认没记录
    	try{
    		String sql="select * from "+ pub.yssGetTableName("Tb_Para_StorageCfg")+" a where"
    				   +" a.FStorageType in ("+operSql.sqlCodes(YssOperCons.YSS_KCLX_Security+","+YssOperCons.YSS_KCLX_Cash)
    				   +") and a.fcheckstate=1 and ( a.FAnalysisCode1='001' or a.FAnalysisCode2='001' or a.FAnalysisCode3='001' )";
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			returnStr="1";
    		}
    	}catch(Exception e){
    		throw new YssException("获取投资经理的库存信息配置出错"+e.getMessage(),e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	//end QDV4农业银行2011年11月14日02_B
        return returnStr;
    }

    /**
     * getListViewData4
     * panjunfang modify 20090814 判断是否跨组合群，在收益支付中如果组合群没有设置投资经理，则去掉修改界面中的投资经理相关控件。MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * @return String
     */
    public String getListViewData4() throws YssException {
        String reStr = "";//定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码
        try{
            if(this.bOverGroup){
                pub.setPrefixTB(this.assetGroupCode);//修改表前缀为的当前组合群代码
            }
            reStr = getPartSetting(this.strStorageType);//调用先前处理方法
        }catch(Exception e){
            throw new YssException(e);
        }finally{
            pub.setPrefixTB(sPrefixTB);//还原公共变的里的组合群代码
            this.bOverGroup = false;
        }
        return reStr;//把结果返回到前台
    }

    /**
     * getPartSetting
     *获取已审核的库存配置信息
     * @return String
     */
    public String getPartSetting(String strStorageType) throws YssException {
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer();
        //StringBuffer bufShow = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        //StringBuffer bufAll = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        try {
            strSql = "select a.*,b.FAnalysisName as FAnalysisName1,c.FAnalysisName as FAnalysisName2,d.FAnalysisName as FAnalysisName3, " +
                " b.FParams as FParams1,c.FParams as FParams2,d.FParams as FParams3 " +
                " from " + pub.yssGetTableName("Tb_Para_StorageCfg") + " a " +
                " left join (select FAnalysisCode,FAnalysisName,FParams from Tb_Base_AnalysisCode where FCheckState = 1) b on a.FAnalysisCode1 = b.FAnalysisCode" +
                " left join (select FAnalysisCode,FAnalysisName,FParams from Tb_Base_AnalysisCode where FCheckState = 1) c on a.FAnalysisCode2 = c.FAnalysisCode" +
                " left join (select FAnalysisCode,FAnalysisName,FParams from Tb_Base_AnalysisCode where FCheckState = 1) d on a.FAnalysisCode3 = d.FAnalysisCode" +
                " where FCheckState = 1 and FStorageType= '" + strStorageType +
                "'" +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strAnalysisCode1 = (rs.getString("FAnalysisCode1") + "").trim();
                this.strAnalysisCode2 = (rs.getString("FAnalysisCode2") + "").trim();
                this.strAnalysisCode3 = (rs.getString("FAnalysisCode3") + "").trim();
                this.strAnalysisName1 = (rs.getString("FAnalysisName1") + "").trim();
                this.strAnalysisName2 = (rs.getString("FAnalysisName2") + "").trim();
                this.strAnalysisName3 = (rs.getString("FAnalysisName3") + "").trim();
                this.strParams1 = (rs.getString("FParams1") + "").trim();
                this.strParams2 = (rs.getString("FParams2") + "").trim();
                this.strParams3 = (rs.getString("FParams3") + "").trim();

                if (this.strAnalysisCode1.trim().length() > 0 &&
                    !this.strAnalysisCode1.equalsIgnoreCase("null")) {
                    buf.append(this.strAnalysisCode1).append("\t");
                    buf.append(this.strAnalysisName1).append("\t");
                    buf.append(this.strParams1).append(YssCons.YSS_LINESPLITMARK);
                }

                if (this.strAnalysisCode2.trim().length() > 0 &&
                    !this.strAnalysisCode2.equalsIgnoreCase("null")) {
                    buf.append(this.strAnalysisCode2).append("\t");
                    buf.append(this.strAnalysisName2).append("\t");
                    buf.append(this.strParams2).append(YssCons.YSS_LINESPLITMARK);
                }

                if (this.strAnalysisCode3.trim().length() > 0 &&
                    !this.strAnalysisCode3.equalsIgnoreCase("null")) {
                    buf.append(this.strAnalysisCode3).append("\t");
                    buf.append(this.strAnalysisName3).append("\t");
                    buf.append(this.strParams3).append(YssCons.YSS_LINESPLITMARK);
                }
                sResult = buf.toString();
                if (sResult.length() > 2) {
                    sResult = sResult.substring(0, sResult.length() - 2);
                }
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取库存信息配置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getSetting
     *
     * @return IBaseSetting
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
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_StorageCfg"),
                               "FStorageType",
                               this.strStorageType, this.strOldStorageType);
    }

    
    /**
     *
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setStorageAttr(ResultSet rs) throws SQLException {
        this.strStorageType = rs.getString("FStorageType") + "";
        this.strAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
        this.strAnalysisName1 = rs.getString("FAnalysisName1") + "";
        this.strAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
        this.strAnalysisName2 = rs.getString("FAnalysisName2") + "";
        this.strAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
        this.strAnalysisName3 = rs.getString("FAnalysisName3") + "";
        //edit by songjie 2011.06.21 BUG 2104 QDV4赢时胜(测试)2011年6月16日01_B
        this.strDesc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
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
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_StorageCfg") +
                "" +
                "(FStorageType,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.strStorageType) + "," +
                dbl.sqlString(this.strAnalysisCode1) + "," +
                dbl.sqlString(this.strAnalysisCode2) + " ," +
                dbl.sqlString(this.strAnalysisCode3) + "," +
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加库存信息配置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_StorageCfg") +
                " set FStorageType = " +
                dbl.sqlString(this.strStorageType) + ", FAnalysisCode1 = " +
                dbl.sqlString(this.strAnalysisCode1) + " , FAnalysisCode2 = " +
                dbl.sqlString(this.strAnalysisCode2) + ", FAnalysisCode3 = " +
                dbl.sqlString(this.strAnalysisCode3) + ", FDesc = " +
                dbl.sqlString(this.strDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FStorageType = " +
                dbl.sqlString(this.strOldStorageType);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改库存信息配置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即是放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_StorageCfg") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FStorageType = " +
                dbl.sqlString(this.strOldStorageType);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除库存信息配置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理期间连接的审核和未审核的单条信息。
     *  新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */
    public void checkSetting() throws YssException {
        //-------------------------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FStorageType = " +
                        dbl.sqlString(this.strOldStorageType);

                    dbl.executeSql(strSql);
                }
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            } else if (strOldStorageType != null && (!strOldStorageType.equalsIgnoreCase(""))) { //如果sRecycled为空，而strOldStorageType不为空，则按照strOldStorageType来执行sql语句
                strSql = "update " + pub.yssGetTableName("Tb_Para_StorageCfg") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FStorageType = " +
                    dbl.sqlString(this.strOldStorageType);

                //执行sql语句
                dbl.executeSql(strSql);

            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核库存信息配置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //------------------------------------end

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        AssetStorageCfgBean befEditBean = new AssetStorageCfgBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*, f.FVocName as FStorageTypeValue, b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                " d.FAnalysisName as FAnalysisName1, e.FAnalysisName as FAnalysisName2," +
                " f.FAnalysisName as FAnalysisName3 from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) d on a.FAnalysisCode1 = d.FAnalysisCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) e on a.FAnalysisCode2 = e.FAnalysisCode" +
                " left join (select FAnalysisCode,FAnalysisName from Tb_Base_AnalysisCode where FCheckState = 1) f on a.FAnalysisCode3 = f.FAnalysisCode" +
                " left join Tb_Fun_Vocabulary f on a.FStorageType = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_SCG_TYPE) +
                " where  a.FStorageType =" + dbl.sqlString(this.strOldStorageType) + " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strStorageType = rs.getString("FStorageType") + "";
                befEditBean.strAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                befEditBean.strAnalysisName1 = rs.getString("FAnalysisName1") + "";
                befEditBean.strAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                befEditBean.strAnalysisName2 = rs.getString("FAnalysisName2") + "";
                befEditBean.strAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                befEditBean.strAnalysisName3 = rs.getString("FAnalysisName3") + "";
                befEditBean.strDesc = YssFun.left(rs.getString("FDesc") + "", 40);

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

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
     * 从期间连接回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
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
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where FStorageType = " +
                        dbl.sqlString(this.strOldStorageType);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (strOldStorageType != "" && strOldStorageType != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("tb_para_tradefeelink") +
                    " where FStorageType = " +
                    dbl.sqlString(this.strOldStorageType);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
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
