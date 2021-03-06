package com.yss.main.datainterface.swift;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.sql.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssFun;
import com.yss.main.datainterface.DaoFileNameBean;
import com.yss.main.datainterface.swift.DaoSwiftOutInfo;

/**
 * SWIFT 参数设置类
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Ysstech</p>
 *QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * @author leeyu 2009-05-18
 * @version 1.0
 */
public class DaoSwiftSet
    extends BaseDataSettingBean implements IDataSetting {
    private String sRecycled = ""; //回收站数据
    //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
    private String sWiftCode="";//报文代码
    private String sOldWiftCode = "";//主键 报文代码,更新时候用
    //-----------------------end -MS00796-----------------
    private String sWiftType = ""; //报文类型
    private String sWiftDesc = ""; //报文描述
    private String sTableCode = ""; //报文表代码
    private String sPath = ""; //路径
    private String sCriterion = ""; //报文标准
    private String sOperType = ""; //业务类型
    private String sReflow = ""; //报文流向

    private String sFTableCodeName = ""; //报文表名称
    private String sCriterionName = ""; //词汇类型: 报文标准 用于显示
    private String sOperTypeName = ""; //业务类型
    private String sReflowName = ""; //报文流向
    private String sDSCode = ""; //预处理代码

    private DaoSwiftSet filterType; //parseRowStr方法中 //用于筛选

    private String sOldWiftType = ""; //checkinput中用//主键 报文类型

    private String sEntitySWIFT = ""; //实体数据
    private String sPathSWIFT = ""; //文件路径
    private DaoSwiftEntitySet daoSwiftEntitySet; //用于实体的
    private String sSwiftOutInfo = ""; //add by libo 保存导出需要的数据信息
    private String sSwiftStatus = ""; //add by libo 主键报文原状态的加入
    private String sOldSwiftStatus = ""; //旧报文状态
    public DaoSwiftSet() {
        daoSwiftEntitySet = new DaoSwiftEntitySet();
    }

    /**
     * 提交前的检查
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        // 调用dbFun.checkInputCommon(操作类型，表名，主键字段名，新主键值，旧主键值)
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_DAO_SWIFT"),
                               "FSwiftCode", this.sWiftCode, this.sOldWiftCode); //老的数据//MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
    }

    /**
     * single data add Method
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        //调用 Connection 执行 insert sql
        //调用 DaoSwiftEntitySet.saveMutliSetting()方法执行批量数据保存
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	checkFieldName();//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            strSql = "insert into " + pub.yssGetTableName("TB_DAO_SWIFT") + "(" +
                "FSwiftCode,FSwiftType,FSwiftStatus,FSwiftDesc,FTableCode,FPath,FCriterion,FOperType,FReflow,FDSCode" +//MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                ",FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
                dbl.sqlString(this.sWiftCode) + "," +//MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                dbl.sqlString(this.sWiftType) + "," +
               (this.sSwiftStatus.length()==0 ? "' '" :dbl.sqlString(this.sSwiftStatus))+ "," +//add by libo 主键报文原状态的加入 加入字段FSwiftStatus
                dbl.sqlString(this.sWiftDesc) + "," +
                dbl.sqlString(this.sTableCode) + "," +
                dbl.sqlString(this.sPath) + "," +
                dbl.sqlString(this.sCriterion) + "," +
                dbl.sqlString(this.sOperType) + "," +
                dbl.sqlString(this.sReflow) + "," +
                (this.sDSCode.length() == 0 ? "' '" : dbl.sqlString(this.sDSCode)) + "," +

                (pub.getSysCheckState() ? "0" : "1") + "," + //FCheckState
                dbl.sqlString(this.creatorCode) + "," + //FCreator
                dbl.sqlString(this.creatorTime) + "," + //FCreateTime
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," + //FCheckUser
                dbl.sqlString(this.checkTime) + //FCheckTime
                ")";

            conn.setAutoCommit(bTrans);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            bTrans = true;
            dbl.executeSql(strSql);

            //实体数据增加

            daoSwiftEntitySet.setYssPub(pub);
            daoSwiftEntitySet.creatorCode = this.creatorCode; //基类定义了
            daoSwiftEntitySet.creatorTime = this.creatorTime; //基类定义了
            daoSwiftEntitySet.saveMutliSetting(sEntitySWIFT);

            DaoFileNameBean fileName = new DaoFileNameBean();
            fileName.setYssPub(pub);
            fileName.creatorCode = this.creatorCode; //基类定义了
            fileName.creatorTime = this.creatorTime; //基类定义了
            fileName.saveMutliSetting(this.sPathSWIFT); //设置 文件字串传入

            //<--add by libo 当为导出时,需要的信息把其保存在表中
            //if(this.sReflow.equalsIgnoreCase("out"))//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            {
                DaoSwiftOutInfo swiftOutInfo = new DaoSwiftOutInfo();
                swiftOutInfo.setYssPub(pub);
                swiftOutInfo.setSFSwiftType(this.sWiftType);//报文原类型传入
                swiftOutInfo.setSSwiftStatus(this.sSwiftStatus);//add by libo 主键报文原状态的加入
                swiftOutInfo.setsSwiftCode(this.sOldWiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                swiftOutInfo.saveMutliSetting(this.sSwiftOutInfo);
            }
            conn.commit(); //add by libo 20090703 整体提交数据
            //bTrans = false;
            conn.setAutoCommit(bTrans);
            bTrans =false;//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            return "";
        } catch (Exception e) {
        	bTrans =true;//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            throw new YssException("新增报文配置信息出错"+e.getMessage(), e);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
  //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    private void checkFieldName() throws YssException{
    	ResultSet rs =null;
    	String sqlStr="";
    	try{
    		sqlStr="select '1' from "+pub.yssGetTableName("Tb_Dao_FileName")+
    		" where FCusCfgCode="+dbl.sqlString(this.sWiftCode);
    		rs =dbl.openResultSet(sqlStr);
    		if(rs.next()){
    			throw new YssException("报文代码【"+sWiftCode+"】已被自定义接口代码占用，请选择其他代码");
    		}
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage(),ex);
    	}
    }
    /**
     * single data edit Method
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        //调用 Connection 执行 update sql
        //调用 DaoSwiftEntitySet.saveMutliSetting()方法执行批量数据保存

        boolean bTrans = false;
        String strSql = "";
        Connection conn = null;
        try {        	
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            strSql = "update " + pub.yssGetTableName("TB_DAO_SWIFT") +
                " set FSwiftType = " + dbl.sqlString(this.sWiftType) +
                //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                " ,FSwiftCode = " + dbl.sqlString(this.sWiftCode) +
                //-----------------------end -MS00796-----------------
				//<---add by libo 主键报文原状态的加入
                " ,FSwiftStatus = " + dbl.sqlString(this.sSwiftStatus) +
                //add by libo 主键报文原状态的加入--->
                " ,FSwiftDesc = " + dbl.sqlString(this.sWiftDesc) +
                " ,FTableCode = " + dbl.sqlString(this.sTableCode) +
                " ,FPath = " + dbl.sqlString(this.sPath) +
                " ,FCriterion = " + dbl.sqlString(this.sCriterion) +
                " ,FOperType = " + dbl.sqlString(this.sOperType) +
                " ,FReflow = " + dbl.sqlString(this.sReflow) + //报文流向
                " ,FDSCode = " + (this.sDSCode.length() == 0 ? "' '" : dbl.sqlString(this.sDSCode)) +
                " ,FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                " ,FCreator = "
                + dbl.sqlString(this.creatorCode) +
                " ,FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                " ,FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                 //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                " where FSwiftCode = " + dbl.sqlString(this.sOldWiftCode);
            	//-----------------------end -MS00796-----------------
            //bTrans = true; //add by libo 20090703 整体提交数据//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            dbl.executeSql(strSql);

            daoSwiftEntitySet.setYssPub(pub);
            daoSwiftEntitySet.creatorCode = this.creatorCode; //基类定义了
            daoSwiftEntitySet.creatorTime = this.creatorTime; //基类定义了
            daoSwiftEntitySet.setsOldWiftCode(this.sOldWiftCode); //把老数据类型传入
            //daoSwiftEntitySet.setSOldSwiftStatus(this.sOldSwiftStatus);//add by libo把报文旧状态加入 //by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
            daoSwiftEntitySet.saveMutliSetting(sEntitySWIFT);

            DaoFileNameBean fileName = new DaoFileNameBean();
            fileName.setYssPub(pub);
            fileName.creatorCode = this.creatorCode; //基类定义了
            fileName.creatorTime = this.creatorTime; //基类定义了
            fileName.setCusCfgCode(this.sOldWiftCode); //老的数据
            fileName.saveMutliSetting(this.sPathSWIFT); //设置 文件字串传入

            //<--add by libo 当为导出时,需要的信息把其保存在表中
            //if (this.sReflow.equalsIgnoreCase("out")) {//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                DaoSwiftOutInfo swiftOutInfo = new DaoSwiftOutInfo();
                swiftOutInfo.setYssPub(pub);
                swiftOutInfo.setSFOldSwiftType(this.sOldWiftType); //报文原类型传入
                swiftOutInfo.setSOldSwiftStatus(this.sOldSwiftStatus);//报文原状态
                swiftOutInfo.setsSwiftCode(this.sOldWiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                swiftOutInfo.saveMutliSetting(this.sSwiftOutInfo);
            //}
            //add by libo 当为导出时,需要的信息把其保存在表中-->
            conn.commit(); //add by libo 20090703 整体提交数据
            //bTrans = false;
            conn.setAutoCommit(bTrans);////by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            bTrans =false;
        } catch (Exception e) {
        	bTrans =true;
            throw new YssException("修改信息出错"+e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    /**
     * single Data delete Method
     * @throws YssException
     */
    public void delSettingMutiDel(String sType, String sTable, String sKey) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName(sTable) +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where " + sKey + "= '" + sType + "'"; //报文类型
            bTrans = true;
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除报文配置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }
	
	 /**
 * single Data delete Method
 * @throws YssException
 * 用于传两个参数的删除
 */
public void delSettingMutiDel(String sType,String sTable,String  sKey,String sKey1,String sType1)throws YssException
{
    Connection conn = dbl.loadConnection();
    boolean bTrans = false;
    String strSql = "";
    try {
        strSql = "update " + pub.yssGetTableName(sTable) +
            " set FCheckState = " +
            this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
            dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) + "'" +
            " where " + sKey + "= '" + sType + "'"+//报文类型
            " and "+ sKey1 + "= '" + sType1+ "'"; //报文原状态
        bTrans = true;
        dbl.executeSql(strSql);

        conn.commit();
        bTrans = false;
        conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除报文配置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void delSetting() throws YssException {
        //调用 Connection 执行 update sql
        //需更新 SWIFT表与SWIFT实体表

        delSettingMutiDel(this.sWiftCode, "TB_DAO_SWIFT", "FSwiftCode"); //模板表
        delSettingMutiDel(this.sWiftCode, "TB_DAO_SWIFTEntity", "FSwiftCode"); //模板表
        delSettingMutiDel(this.sWiftCode, "TB_Dao_FileName", "FcuscfgCode"); //模板表
        
    }

    /**
     * 审核、反审核、还原数据
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //调用 Connection 执行 update sql
        //需更新 SWIFT表与SWIFT实体表
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("TB_DAO_SWIFT") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                        " where FSwiftCode = " + dbl.sqlString(this.sWiftCode); //报文类型
                    	//-----------------------end -MS00796-----------------
                    dbl.executeSql(strSql);

                    strSql = "update " + pub.yssGetTableName("TB_DAO_SWIFTENTITY") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                        " where FSwiftCode = " + dbl.sqlString(this.sWiftCode); //报文类型
                    	//-----------------------end -MS00796-----------------
                    dbl.executeSql(strSql);

                    strSql = "update " +
                        pub.yssGetTableName("TB_DAO_FILENAME") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                        " where FCusCfgCode = " + dbl.sqlString(this.sWiftCode); //报文类型
                    	//-----------------------end -MS00796-----------------
                    dbl.executeSql(strSql);

                }
            } else {
                if (this.sWiftCode != null &&
                    this.sWiftCode.trim().length() > 0) { //为主键
                    strSql = "update " + pub.yssGetTableName("TB_DAO_SWIFT") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                        " where FSwiftCode = " + dbl.sqlString(this.sWiftCode); //报文类型
                    	//-----------------------end -MS00796-----------------

                    dbl.executeSql(strSql);
                    strSql = "update " +
                        pub.yssGetTableName("TB_DAO_SWIFTENTITY") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                        " where FSwiftCode = " + dbl.sqlString(this.sWiftCode); //报文类型
                    	//-----------------------end -MS00796-----------------
                    dbl.executeSql(strSql);

                    strSql = "update " +
                        pub.yssGetTableName("TB_DAO_FILENAME") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " + //基类中已定义checkStateId
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCusCfgCode = " +
                        //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
                        dbl.sqlString(this.sWiftCode); //报文类型
                    	//-----------------------end -MS00796-----------------
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核报文及相关配置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 多条数据的保存
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * 获取一条数据信息
     * @return IDataSetting
     * @throws YssException
     */
    public IDataSetting getSetting() throws YssException {
        //获取 SwiftType类型的一条数据信息
        // 查询语句
        ResultSet rs = null;
        String sqlStr = "";
        try {
            sqlStr = "select * from " + pub.yssGetTableName("Tb_Dao_Swift") +
            
                " where FSwiftType =" + dbl.sqlString(sWiftType)+
                " and FSwiftStatus="+dbl.sqlString(sSwiftStatus)+
                " and Freflow="+dbl.sqlString(sReflow)+ // add by jiangshichao 2010.05.10 增加报文流向，否则取数有问题
                " and fcheckstate = 1";
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                this.sWiftDesc = rs.getString("FSwiftDesc"); //报文描述
                this.sTableCode = rs.getString("FTableCode"); //报文表代码
                this.sPath = rs.getString("FPath"); //路径
                this.sCriterion = rs.getString("FCriterion"); //报文标准
                this.sOperType = rs.getString("FOperType"); //业务类型
                this.sReflow = rs.getString("FReflow"); //报文流向
                this.sDSCode = rs.getString("FDSCode"); //预处理代码
                this.sWiftCode = rs.getString("FSwiftCode");//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            }
        } catch (Exception ex) {
            throw new YssException("获取报文模板【" + sWiftCode + "】信息出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        //清空回收站数据功能
        //需删除 SWIFT表与SWIFT实体表
        //批量删除应调用PreparedStatement类执行批量操作
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_SWIFT") +
                        " where FSwiftCode = " + dbl.sqlString(this.sWiftCode);
                    dbl.executeSql(strSql);

                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_SWIFTENTITY") +
                        " where FSwiftCode = " + dbl.sqlString(this.sWiftCode);
                    dbl.executeSql(strSql);

                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_FILENAME") +
                        " where fcuscfgcode = " + dbl.sqlString(this.sWiftCode);
                    dbl.executeSql(strSql);
					
					//添加报文状态 by leeyu swift报文导出功能 20091110
                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_SWIFT_OUTINFO") +
                    " where FSwiftType = " + dbl.sqlString(this.sWiftType) +
                    " and FSwiftStatus= "+dbl.sqlString(this.sSwiftStatus);
                    dbl.executeSql(strSql);

                }
            } else {
                if (this.sWiftCode != null &&
                    this.sWiftCode.trim().length() > 0) {
                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_SWIFT") +
                        " where FSwiftCode = " + dbl.sqlString(this.sWiftCode);
                    dbl.executeSql(strSql);

                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_SWIFTENTITY") +
                        " where FSwiftCode = " + dbl.sqlString(this.sWiftCode);
                    dbl.executeSql(strSql);

                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_FILENAME") +
                        " where fcuscfgcode = " + dbl.sqlString(this.sWiftCode);
                    dbl.executeSql(strSql);

                    //添加报文状态 by leeyu swift报文导出功能 20091110
                    strSql = "delete from " + pub.yssGetTableName("TB_DAO_SWIFT_OUTINFO") +
                    " where FSwiftType = " + dbl.sqlString(this.sWiftType) +
                    " and FSwiftStatus= "+dbl.sqlString(this.sSwiftStatus);
              
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除报文配置数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
     * 获取列表数据模式1
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        ///// Connection conn = null; //
        ///// boolean bTrans = false;
        // ResultSet rs = null;
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
                ",d.FTableDesc as FTableCodeName" + //报文临时表
                ",e.FVocName as FCriterionName" + //报文标准
                ",f.FVocName as FOperTypeName " + //业务类型
                ",g.FVocName as FReflowName " + //报文流向
                " from " + pub.yssGetTableName("TB_DAO_SWIFT") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" + ///这个保留
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " + ///这个保留
                " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTableCode " + //这个就是点的那个报文临时表字段关联的//"数据字典"

                " left join Tb_Fun_Vocabulary e on " + dbl.sqlToChar("a.FCriterion") + //业务标准，业务流向，业务类型这三个要关联的.//"词汇类型"
                " =e.FVocCode and " +
                " e.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_SWIFT_CRITERION) +

                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FOperType") + //业务类型: 0全部业务   1支付业务   2外汇业务   3证券业务   4现金对账
                " =f.FVocCode and " +
                " f.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_SWIFT_OPERTYPE) +

                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FReflow") + //报文流向:0导入1导出
                " =g.FVocCode and " +
                " g.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_SWIFT_REFLOW) +
                buildFilterSql() +
                " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
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
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_SWIFT_CRITERION + "," +
                                        YssCons.YSS_SWIFT_OPERTYPE + "," +
                                        YssCons.YSS_SWIFT_REFLOW + "," +
                                     YssCons.YSS_SWIFT_ENTITY_STATE+","+
          //<--add by libo swift导出功能 导出时的下拉信息加载-----------------------
                                     YssCons.YSS_SWIFT_App_TagD+","+//应用标识
                                     YssCons.YSS_SWIFT_Ser_TagD+","+//服务标识
                                     YssCons.YSS_SWIFT_GetS_TagD+","+//收发标识
                                     YssCons.YSS_SWIFT_Mess_TypeD+","+//报文类型
                                     YssCons.YSS_SWIFT_Prior_ClassD+","+//优先等级
                                     YssCons.YSS_SWIFT_Send_ConD+","+//传送监控
                                     YssCons.YSS_SWIFT_User_ReD+","+//用户参考   
                                     YssCons.YSS_SWIFT_STATUS //报文状态
             );
         //-------------------------------------------add by libo swift导出功能-->
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
            // return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
            //      this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取报文模板信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

        //获取列表数据模式1
        //查询SQL
        //通过StringBuffer 类将数据放入到Buf中
        //返回列标题+"\r\f"+显示列值+"\r\f"+显示全值+"\r\f"+列名+"\r\fvoc"+词汇
        //return "";
    }

    /**
     * listView2 loading
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        //获取列表数据模式2
        //返回列标题+"\r\f"+显示列值+"\r\f"+显示全值+"\r\f"+列名
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
            this.sWiftDesc = reqAry[1]; //报文描述
            this.sTableCode = reqAry[2]; //报文表代码
            this.sPath = reqAry[3]; //路径
            this.sCriterion = reqAry[4]; //报文标准
            this.sOperType = reqAry[5]; //业务类型
            this.sReflow = reqAry[6]; //报文流向

            //this.sOldWiftType = reqAry[7]; //主键 报文类型 //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            if(YssFun.isNumeric(reqAry[7])) //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            	this.checkStateId = YssFun.toInt(reqAry[7]); //状态 2 为删除,从页面来,在基类中已经定义
            this.sDSCode = reqAry[8]; //预处理代码 //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            //<---add by libo 主键报文原状态的加入
            this.sSwiftStatus=reqAry[9];//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            //this.sOldSwiftStatus=reqAry[11]; //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            //add by libo 主键报文原状态的加入--->
            this.sWiftCode = reqAry[10]; //报文代码MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106 //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            this.sOldWiftCode= reqAry[11]; //报文代码MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106 //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoSwiftSet();
                    this.filterType.setYssPub(pub); //全局的东西
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }

                if (sRowStr.split("\r\t").length > 2) {
                    this.sEntitySWIFT = sRowStr.split("\r\t")[2]; //实体传入
                }

                if (sRowStr.split("\r\t").length > 3) {
                    this.sPathSWIFT = sRowStr.split("\r\t")[3]; //设置字串传入
                }
                if (sRowStr.split("\r\t").length > 4) {
                    this.sSwiftOutInfo = sRowStr.split("\r\t")[4]; //add by libo 导出需要的信息
                }
            }
        } catch (Exception e) {
            throw new YssException("解析报文请求信息出错", e);
        }
    }

    /**
     * buildRowStr
     * 返回属性信息
     * @return String
     */
    public String buildRowStr() { //from--add  libo
        StringBuffer buf = new StringBuffer();
        buf.append(this.sWiftType).append("\t");
        buf.append(this.sWiftDesc).append("\t");
        buf.append(this.sTableCode).append("\t");
        buf.append(this.sPath).append("\t");
        buf.append(this.sCriterion).append("\t");
        buf.append(this.sOperType).append("\t");
        buf.append(this.sReflow).append("\t");
        buf.append(this.sDSCode).append("\t"); //预处理代码
        buf.append(this.sFTableCodeName).append("\t"); //报文表名称
		//<---add by libo 主键报文原状态的加入
        buf.append(this.sSwiftStatus).append("\t");//报文原状态
        //add by libo 主键报文原状态的加入--->
        //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
        buf.append(this.sWiftCode).append("\t"); //报文表代码
        //-----------------------end -MS00796-----------------
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

    /**
     * 将结果集中的数据赋给变量
     * @param rs ResultSet
     * @throws YssException
     */
    private void setAttrData(ResultSet rs) throws YssException {

    }

    /**
     * 获取筛选条件与数据
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = sResult + "where 1=1";

            if (this.filterType.sWiftType.length() != 0) {
                sResult = sResult + " and a.FSwiftType like '" +
                    filterType.sWiftType.replaceAll("'", "''") + "%'";
            }
            //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
            if (this.filterType.sWiftCode.length() != 0) {
                sResult = sResult + " and a.FSwiftCode like '" +
                    filterType.sWiftCode.replaceAll("'", "''") + "%'";
            }
            //-----------------------end -MS00796-----------------
            if (this.filterType.sWiftDesc.length() != 0) {
                sResult = sResult + " and a.FSwiftDesc like '" +
                    filterType.sWiftDesc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sTableCode.length() != 0) {
                sResult = sResult + " and a.FTableCode like '" +
                    filterType.sTableCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sCriterion.length() != 0 && !this.filterType.sCriterion.equals("99")) {
                sResult = sResult + " and a.FCriterion like '" +
                    filterType.sCriterion.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sOperType.length() != 0 && !this.filterType.sOperType.equals("99")) {
                sResult = sResult + " and a.FOperType like '" +
                    filterType.sOperType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sReflow.length() != 0 && !this.filterType.sReflow.equals("99")) {
                sResult = sResult + " and a.FReflow like '" +
                    filterType.sReflow.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;

    }

    /**
     * 读后台的记录  add by libo 20090522
     * @return ResultSet
     * @throws YssException
     */

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {

        this.sWiftType = rs.getString("FSwiftType"); //报文类型
        this.sWiftDesc = rs.getString("FSwiftDesc"); //报文描述
        this.sTableCode = rs.getString("FTableCode"); //报文表代码
        this.sPath = rs.getString("FPath"); //路径
        this.sCriterion = rs.getString("FCriterion"); //报文标准
        this.sOperType = rs.getString("FOperType"); //业务类型
        this.sReflow = rs.getString("FReflow"); //报文流向
        this.sDSCode = rs.getString("FDSCode"); //预处理代码

        this.sFTableCodeName = rs.getString("FTableCodeName"); //报文表名称
        //MS00796 QDV4南方2009年11月4日01_B fanghaoln 20091106
        this.sWiftCode = rs.getString("FSwiftCode"); //报文表名称
        //-----------------------end -MS00796-----------------
		//<---add by libo 主键报文原状态的加入 得到数据库中报文原状态值
        this.sSwiftStatus=rs.getString("FSwiftStatus");//报文原状态
        //add by libo 主键报文原状态的加入--->
        super.setRecLog(rs);
    }

    public String getSwiftType() {
        return sWiftType;
    }

    public String getSwiftDesc() {
        return sWiftDesc;
    }

    public String getSTableCode() {
        return sTableCode;
    }

    public String getSReflowName() {
        return sReflowName;
    }

    public String getSReflow() {
        return sReflow;
    }

    public String getSPath() {
        return sPath;
    }

    public String getSOperTypeName() {
        return sOperTypeName;
    }

    public String getSOperType() {
        return sOperType;
    }

    public String getSDSCode() {
        return sDSCode;
    }

    public String getSCriterionName() {
        return sCriterionName;
    }

    public void setSCriterion(String sCriterion) {
        this.sCriterion = sCriterion;
    }

    public void setSwiftType(String sWiftType) {
        this.sWiftType = sWiftType;
    }

    public void setSwiftDesc(String sWiftDesc) {
        this.sWiftDesc = sWiftDesc;
    }

    public void setSTableCode(String sTableCode) {
        this.sTableCode = sTableCode;
    }

    public void setSReflowName(String sReflowName) {
        this.sReflowName = sReflowName;
    }

    public void setSReflow(String sReflow) {
        this.sReflow = sReflow;
    }

    public void setSPath(String sPath) {
        this.sPath = sPath;
    }

    public void setSOperTypeName(String sOperTypeName) {
        this.sOperTypeName = sOperTypeName;
    }

    public void setSOperType(String sOperType) {
        this.sOperType = sOperType;
    }

    public void setSDSCode(String sDSCode) {
        this.sDSCode = sDSCode;
    }

    public void setSCriterionName(String sCriterionName) {
        this.sCriterionName = sCriterionName;
    }

    public void setSFTableCodeName(String sFTableCodeName) {
        this.sFTableCodeName = sFTableCodeName;
    }

    public void setSSwiftOutInfo(String sSwiftOutInfo) {
        this.sSwiftOutInfo = sSwiftOutInfo;
    }

    public void setSSwiftStatus(String sSwiftStatus) {
        this.sSwiftStatus = sSwiftStatus;
    }

    public void setSOldSwiftStatus(String sOldSwiftStatus) {
        this.sOldSwiftStatus = sOldSwiftStatus;
    }

    public String getSCriterion() {
        return sCriterion;
    }

    public String getSFTableCodeName() {
       return sFTableCodeName;
    }

    public String getSSwiftOutInfo() {
        return sSwiftOutInfo;
    }

    public String getSSwiftStatus() {
        return sSwiftStatus;
    }

    public String getSOldSwiftStatus() {
        return sOldSwiftStatus;
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

}
